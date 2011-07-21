/*
 * Created on 04-Sep-2004 at 18:49:18.
 * 
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.emulator;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Locale;

import de.enough.polish.BuildException;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.Extension;
import de.enough.polish.ExtensionManager;
import de.enough.polish.Variable;
import de.enough.polish.ant.emulator.DebuggerSetting;
import de.enough.polish.ant.emulator.EmulatorSetting;
import de.enough.polish.stacktrace.BinaryStackTrace;
import de.enough.polish.stacktrace.DecompilerNotInstalledException;
import de.enough.polish.stacktrace.StackTraceUtil;
import de.enough.polish.util.OutputFilter;
import de.enough.polish.util.ProcessUtil;

/**
 * <p>Excutes an emulator.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        04-Sep-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class Emulator 
extends Extension 
implements Runnable, OutputFilter
{
	protected EmulatorSetting emulatorSetting;
	protected boolean isFinished = false;
	private File[] sourceDirs;
	private String preprocessedSourcePath;
	private String classPath;
	protected Device device;
	private boolean decompilerInstalled;
	private String header;
	private String ignoreMessagePattern;
	
	/**
	 * Creates a new emulator instance.
	 * The actual initialisation is done in the init-method.
	 * 
	 * @see #init(Device, EmulatorSetting, Environment)
	 */
	public Emulator() {
		// no initialisation done here
	}
	
	/**
	 * Starts the emulator for the given device.
	 * @param dev the current device
	 * @param setting the setting
	 * @param env all Ant- and polish-properties for the parameter-values
	 *  
	 * @return true when an emulator could be detected
	 */
	public abstract boolean init(Device dev, EmulatorSetting setting, Environment env );
	
	/**
	 * Retrieves the arguments which are used to start the emulator.
	 * 
	 * @return an array with the arguments for starting the emulator.
	 */
	public abstract String[] getArguments();
	
	
	/**
	 * Sets the minimum settings.
	 * 
	 * @param device the device which is emulated
	 * @param setting the settings for the emulator
	 * @param sourceDirs the directories containing the original source files.
	 * @param environment the J2ME Polish and Ant-properties
	 */
	private void setBasicSettings( Device device, EmulatorSetting setting, File[] sourceDirs, Environment environment ) {
		this.device = device;
		this.emulatorSetting = setting;
		this.environment = environment;
		this.classPath = device.getClassesDir();
		this.preprocessedSourcePath = device.getSourceDir();
		this.sourceDirs = sourceDirs;
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		String[] arguments = getArguments();
		run( arguments, true );
	}
	
	protected void run( String[] arguments, boolean retryOnError ) {
		try {
			DebuggerSetting debuggerSetting = this.emulatorSetting.getDebuggerSetting( this.environment.getBooleanEvaluator() );
			DebuggerThread debuggerThread = null;
			if ( debuggerSetting != null ) {
				ArrayList argsList = new ArrayList();
				for (int i = 0; i < arguments.length; i++) {
					String arg = arguments[i];
					argsList.add( arg );
				}
				try {
					Debugger debugger = (Debugger) this.extensionManager.getTemporaryExtension( ExtensionManager.TYPE_DEBUGGER, debuggerSetting, this.environment );
					if (supportsDebugger(debugger)) {
						addDebugArguments( argsList, debugger );
						arguments = (String[]) argsList.toArray( new String[ argsList.size() ] );
						debuggerThread = new DebuggerThread( debugger );
						debuggerThread.start();
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Warning: unable to initalize debugger connection: " + e.toString() );
				} 
			}
			boolean wait = this.emulatorSetting.doWait();
			long startTime = System.currentTimeMillis();
			int res = exec( arguments, this.device.getIdentifier() + ": ",  wait, this, getExecutionDir() );
			long usedTime = System.currentTimeMillis() - startTime;
			if ( res != 0 || (wait && usedTime < 4000) ) {
				if (debuggerThread != null) {
					debuggerThread.cancel();
				}
//				if (res == 0) {
//					res = -110011;
//				}
				if (res == 0 && retryOnError && arguments.length > 1 && arguments[1].indexOf("transient=") != -1) {
					// the application should have run in the transient mode (install, run, de-install).
					// Most likely it has not been de-installed correctly before, so just de-install all applications:
					String[] removeArguments = new String[]{ arguments[0], "-Xjam:remove=all" };
					System.out.println("Emulator execution has failed in transient mode. Now trying to de-install all applications (-Xjam:remove=all).");
					res = exec( removeArguments, this.device.getIdentifier() + ": ",  wait, this, getExecutionDir() );
					if (res == 0) {
						System.out.println("All applications have been de-installed successfully - now restarting emulator.");
						run( arguments, false );
						return;
					}
				}
				System.out.println("Emulator returned [" + res + "], arguments were:");
				for (int i = 0; i < arguments.length; i++) {
					String argument = arguments[i];
					System.out.println( argument );
				}				
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Unable to start or run emulator: " + e.toString() );
		} finally {
			this.isFinished = true;
		}
	}

	/**
	 * Adds the debugging settings to the arguments list.
	 * By default the UEI arguments -Xdebug and -Xrunjdwp arguments are added by calling debugger.addDebugArguments( List ).
	 * 
	 * @param argsList the arguments list
	 * @param debugger the debugger
	 */
	protected void addDebugArguments(ArrayList argsList, Debugger debugger) {
		debugger.addDebugArguments( this.environment, argsList );
	}
	
	/**
	 * Defines whether this emulator supports the given debugger.
	 * Subclasses can use this for influencing the debugging process.
	 * 
	 * @return true when the debugger is supported. This defaults to true.
	 */
	protected boolean supportsDebugger(Debugger debugger) {
		return true;
	}


	/**
	 * Executes the actual emulator.
	 *  
	 * @param arguments all command line arguments
	 * @param info the info block for output messages
	 * @param wait true when the current thread should block
	 * @param filter the output filter
	 * @param executionDir the director for executing the emulator
	 * @return the result code from the emulator
	 * @throws IOException when the emulator process could not be invoked
	 */
	protected int exec( String[] arguments, String info, boolean wait, OutputFilter filter, File executionDir ) 
	throws IOException 
	{
		System.out.println( "Starting emulator " + arguments[0] );
		this.decompilerInstalled = true;
		this.header = info;
		return ProcessUtil.exec( arguments, info, wait, filter,  executionDir );
	}

	/**
	 * Retrieves the directory in which the emulator should be executed.
	 * 
	 * @return null when the emulator should be started from the current directory
	 *         or a file pointing to the actual needed execution dir.
	 */
	protected File getExecutionDir() {
		return null;
	}

	/**
	 * Determines whether this emulator is finished.
	 * 
	 * @return true when this emulator is finished or when J2ME Polish should not wait for it.
	 */
	public boolean isFinished() {
		return this.isFinished;
	}
	
	/**
	 * Writes all properties for the given parameters.
	 * 
	 * @param variables the input parameters
	 * @param properties the map containing all properties
	 * @return an array of parameters with the written properties
	 */
	protected Variable[] writeProperties( Variable[] variables, Environment properties ) {
		Variable[] newVars = new Variable[ variables.length ];
		for (int i = 0; i < variables.length; i++) {
			Variable variable = variables[i];
			
			String value = variable.getValue();
			if (value.indexOf('$') == -1) {
				// now property needed:
				newVars[i] = variable;
			} else {
				// the value contains a property:
				value = properties.writeProperties( value );
				Variable var = new Variable( variable.getName(), value );
				var.setIf( variable.getIfCondition() );
				var.setUnless( variable.getUnlessCondition() );
				var.setType( variable.getType() );
				newVars[i] = var;
				if (value.indexOf("${") != -1) {
					int startIndex = value.indexOf("${") + 2;
					int endIndex = value.indexOf('}', startIndex );
					String missingProperty = value.substring( startIndex, endIndex );
					System.out.println("Warning: the Ant-property [" + missingProperty + "] needs to be defined, so that the parameter [" + variable.getName() + "] can be set correctly. Current value is [" + value + "].");					
				}
			}
		}
		return newVars;
	}
	
	/**
	 * Gets the parameters for this emulator.
	 * 
	 * @param setting the emulator setting
	 * @param env all Ant- and J2ME Polish-properties.
	 * @return an array of initialised parameters.
	 */
	protected Variable[] getParameters( EmulatorSetting setting, Environment env ) {
		Variable[] parameters = setting.getParameters();
		if (parameters.length == 0) {
			return parameters;
		}
		ArrayList parametersList = new ArrayList( parameters.length );
		for (int i = 0; i < parameters.length; i++) {
			Variable variable = parameters[i];
			if (variable.isConditionFulfilled(env)) {
				parametersList.add( variable );
			}
		}
		Variable[] params = (Variable[]) parametersList.toArray( new Variable[ parametersList.size() ] );
		return writeProperties( params, env );
	}
	
	/**
	 * Gets a specific parameter
	 * 
	 * @param name the name of the parameter
	 * @param parameters all parameters
	 * @return the found parameter or null, when none was found
	 */
	protected Variable getParameter( String name, Variable[] parameters ) {
		for (int i = 0; i < parameters.length; i++) {
			Variable variable = parameters[i];
			if (name.equals( variable.getName())) {
				return variable;
			}
		}
		return null;
	}

	/**
	 * Gets a specific parameter-value
	 * 
	 * @param name the name of the parameter
	 * @param parameters all parameters
	 * @return the found parameter-value or null, when none was found
	 */
	protected String getParameterValue( String name, Variable[] parameters ) {
		for (int i = 0; i < parameters.length; i++) {
			Variable variable = parameters[i];
			if (name.equals( variable.getName())) {
				return variable.getValue();
			}
		}
		return null;
	}
	
	/**
	 * Creates the emulator for the given device.
	 *  
	 * @param device the current device
	 * @param setting the configuration for the emulator
	 * @param environment the variables for the parameter-values
	 * @param sourceDirs the directories containing the original source files.
	 * @param extensionManager manager for extensions
	 * @return true when an emulator could be detected
	 */
	public static Emulator createEmulator(Device device, EmulatorSetting setting, Environment environment, File[] sourceDirs, ExtensionManager extensionManager ) {
		
		String className = setting.getEmulatorClassName();
		if (className == null) {
			className = device.getCapability("polish.Emulator.Class");
			if (className == null) {
				String executable = environment.getVariable("polish.Emulator.Executable");
				String arguments =  environment.getVariable("polish.Emulator.Arguments");
				if (executable != null && arguments != null) {
					className = GenericEmulator.class.getName();
				} else {
					className = WtkEmulator.class.getName();
				}
			}
		}
		Class emulatorClass = null;
		try {
			emulatorClass = Class.forName( className );
		} catch (ClassNotFoundException e) {
			// okay, try to add this package in front if it:
			// System.out.println("did not find class " + className );
			try {
				emulatorClass = Class.forName( "de.enough.polish.emulator." + className );
			} catch (ClassNotFoundException e2) {
				System.err.println("Unable to load emulator-class [" + className + "]: " + e2.toString() );
				return null;
			}
		}
		Emulator emulator = null;
		try {
			emulator = (Emulator) emulatorClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to instantiate the emulator-class [" + className + "]: " + e.toString() );
			return null;
		}
		emulator.init(null, null, setting, null, extensionManager, environment); // extension call
		// for some reason the environment is not set correctly in the init() method... weird.
		emulator.environment = environment;
		emulator.ignoreMessagePattern = setting.getIgnore();
		//System.out.println( "emulator.environment == null: " + (emulator.environment == null) );
		boolean okToStart = emulator.init(device, setting, environment);
		if (!okToStart) {
			// use MPP / Mobile Power Player as a backup:
			if ( environment.getVariable("mpp.home") != null) {
				emulator = new MppEmulator();
				emulator.init(null, null, setting, null, extensionManager, environment); // extension call
				// for some reason the environment is not set correctly in the init() method... weird.
				emulator.environment = environment;
				//System.out.println( "emulator.environment == null: " + (emulator.environment == null) );
				okToStart = emulator.init(device, setting, environment);
			}
			if (!okToStart) {
				emulator = new MicroEmulator();
				emulator.init(null, null, setting, null, extensionManager, environment); // extension call
				// for some reason the environment is not set correctly in the init() method... weird.
				emulator.environment = environment;
				okToStart = emulator.init(device, setting, environment);				
			}
			if (!okToStart) {			
				return null;
			}
		}
		emulator.setBasicSettings(device, setting, sourceDirs, environment );
		//Thread thread = new Thread( emulator );
		//thread.start();
		return emulator;
	}
	
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.Extension#execute(de.enough.polish.devices.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void execute(Device dev, Locale locale, Environment env)
			throws BuildException 
	{
		this.device = dev;
		this.environment = env;
		Thread t = new Thread( this );
		t.start();
	}

	/**
	 * Filters the given message.
	 * 
	 * @param logMessage the message
	 * @param output the stream to which the message should be written (if not filtered)
	 */
	public void filter( String logMessage, PrintStream output ) {
		if (this.ignoreMessagePattern != null) {
			int startIndex = logMessage.indexOf(':');
			String msg = logMessage;
			if (startIndex != -1) {
				msg = logMessage.substring( startIndex + 2 );
			}
			if (msg.startsWith( this.ignoreMessagePattern)) {				
				return;
			}
		}
		output.println( logMessage );
		if (this.decompilerInstalled
				&& (logMessage.indexOf('+') != -1) 
				&& (logMessage.indexOf("at ") != -1)) {
			try {
				// this seems to be an error message like
				// "   at de.enough.polish.ClassName(+263)"
				// so try to use JAD for finding out the source code address:
				BinaryStackTrace stackTrace = StackTraceUtil.translateStackTrace(logMessage, Emulator.this.classPath, Emulator.this.preprocessedSourcePath, Emulator.this.sourceDirs, Emulator.this.environment);
				if (stackTrace != null) {
					boolean showDecompiledStackTrace = true;
					if (stackTrace.couldBeResolved()) {
						output.println( this.header + stackTrace.getSourceCodeMessage() );
						showDecompiledStackTrace = false;
					} 
					if (showDecompiledStackTrace || Emulator.this.emulatorSetting.showDecompiledStackTrace()){
						output.println( this.header + "Decompiled stack-trace: " + stackTrace.getDecompiledCodeSnippet() );
					}
				}
			} catch (DecompilerNotInstalledException e) {
				output.println("Unable to translate stacktrace: " + e.getMessage() );
				this.decompilerInstalled = false;
			}
		}
	}

	class DebuggerThread extends Thread {
		
		private final Debugger debugger;
		private boolean stopRequested;
		
		DebuggerThread( Debugger debugger  ) {
			this.debugger  = debugger;
		}
		/**
		 * Reqests the stop of this thread.
		 */
		public void cancel() {
			this.stopRequested = true;
		}

		public void run() {
			//System.out.println("executing debugger " + this.debugger.getClass().getName() );
			try {
				Thread.sleep( 5000 );
			} catch (InterruptedException e) {
				// ignore
			}
			if (this.stopRequested) {
				System.out.println("stop has been requested...");
				return;
			}
			this.debugger.execute( Emulator.this.environment.getDevice(), Emulator.this.environment.getLocale(), Emulator.this.environment );
		}
		
	}
}
