/*
 * Created on 19-Jun-2004 at 20:23:00.
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
package de.enough.polish.preprocess;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;

import de.enough.polish.BuildException;
import org.apache.tools.ant.Project;

import de.enough.polish.BooleanEvaluator;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.Extension;
import de.enough.polish.ExtensionDefinition;
import de.enough.polish.ExtensionManager;
import de.enough.polish.ExtensionSetting;
import de.enough.polish.ExtensionTypeDefinition;
import de.enough.polish.ant.build.PreprocessorSetting;
import de.enough.polish.preprocess.css.StyleSheet;
import de.enough.polish.util.StringList;

/**
 * <p>Preprocesses the source-code in a user-defined manner.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        19-Jun-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class CustomPreprocessor extends Extension {

	protected Preprocessor preprocessor;
	protected BooleanEvaluator booleanEvaluator;
	public boolean isUsingPolishGui;
	protected boolean isInJ2MEPolishPackage;
	protected Device currentDevice;
	protected StyleSheet currentStyleSheet;
	private ArrayList directives;
	protected Locale currentLocale;
	private PreprocessorSetting setting;

	/**
	 * Creates a new line-preprocessor.
	 * The actual initialisation work is done in the init()-method.
	 * 
	 * @see #init(Preprocessor, PreprocessorSetting )
	 */
	public CustomPreprocessor() {
		// no initialisation work done
	}
	
	protected void init(ExtensionTypeDefinition typeDefinition, ExtensionDefinition definition, ExtensionSetting preprocessorSetting, Project project, ExtensionManager manager, Environment env) {
		super.init(typeDefinition, definition, preprocessorSetting, project, manager, env);
		init( (Preprocessor) env.get( Preprocessor.ENVIRONMENT_KEY ), (PreprocessorSetting) preprocessorSetting );
	}

	/**
	 * Initialises this custom preprocessor.
	 * The default implementation set the boolean evaluator and the
	 * parent-preprocessor.
	 * 
	 * @param processor the parent-preprocessor for this custom preprocessor
	 * @param preprocessorSetting the settings of this preprocessor
	 */
	public void init( Preprocessor processor, PreprocessorSetting preprocessorSetting ) {
		this.preprocessor = processor;
		this.setting = preprocessorSetting;
		this.booleanEvaluator = processor.getEnvironment().getBooleanEvaluator();
	}
	
	/**
	 * Notifies the preprocessor that from now on source code from the J2ME Polish package is processed.
	 * This will last until the notifyDevice(...)-method is called.
	 */
	public void notifyPolishPackageStart() {
		this.isInJ2MEPolishPackage = true;
	}
		
	/**
	 * Notifies this preprocessor about a new device for which code is preprocessed.
	 * The default implementation set the currentDevice, currentStyleSheet
	 * and isUsingPolishGui and resets the isInJ2MEPolishPackage instance variables.
	 *  
	 * @param device the new device
	 * @param usesPolishGui true when the J2ME Polish GUI is used for the new device
	 */
	public void notifyDevice( Device device, boolean usesPolishGui ) {
		this.currentDevice = device;
		this.isUsingPolishGui = usesPolishGui;
		this.currentStyleSheet = this.preprocessor.getStyleSheet();
		this.isInJ2MEPolishPackage = false;
	}
	
	/**
	 * Notifies this preprocessor about a new locale.
	 * 
	 * @param locale the new locale, can be null
	 */
	public void notifyLocale( Locale locale ) {
		this.currentLocale = locale;
	}
	
	/**
	 * Notifies this preprocessor about the end of the preprocessing step for the given device
	 * 
	 * @param device the device which preprocessing step has been finished
	 * @param usesPolishGui true when the J2ME Polish GUI is used for new device
	 */
	public void notifyDeviceEnd( Device device, boolean usesPolishGui  ) {
		// do nothing
	}
	
	/**
	 * Processes the given class.
	 * The default implementation searches for the registered directives
	 * and calls the appropriate methods upon findings.
	 * 
	 * @param lines the source code of the class
	 * @param className the name of the class
	 * @see #registerDirective(String)
	 */
	public void processClass( StringList lines, String className ) {
		if (this.directives == null) {
			return;
		}
		Directive[] myDirectives = (Directive[]) this.directives.toArray( new Directive[ this.directives.size()]);
		while (lines.next()) {
			String line = lines.getCurrent();
			for (int i = 0; i < myDirectives.length; i++) {
				Directive directive = myDirectives[i];
				if (line.indexOf( directive.directive ) != -1) {
					try {
						// a registered preprocessing directive has been found:
						// call the appropriate method:
						directive.method.invoke(this, new Object[]{ line, lines, className });
						// now check if the directive has been removed:
						if (line.equals(lines.getCurrent())) {
							// the line has not been changed
							lines.setCurrent("// removed custom directive " + directive );
						}
						// break the for-loop:
						break;
					} catch (BuildException e) {
						throw e;
					} catch (InvocationTargetException e) {
						if (e.getCause() instanceof BuildException) {
							throw (BuildException) e.getCause();
						} else {
							e.printStackTrace();
							throw new BuildException("Unable to process directive [" 
									+ directive.directive + "] in line [" + line 
									+ "] of class [" + className + "] at line [" 
									+ (lines.getCurrentIndex() + 1) + "]: " + e.toString() );
						}
					} catch (Exception e) {
						e.printStackTrace();
						throw new BuildException("Unable to process directive [" 
								+ directive.directive + "] in line [" + line 
								+ "] of class [" + className + "] at line [" 
								+ (lines.getCurrentIndex() + 1) + "]: " + e.toString() );
					}
				}
			}
		}
	}
	
	/**
	 * Loads a custom preprocessor subclass.
	 * 
	 * @param setting the definition of the line preprocessor 
	 * @param preprocessor the preprocessor
	 * @param manager the extension manager
	 * @param environment the environment settings
	 * @return the initialised custom preprocessor
	 * @throws BuildException when the defined class could not be instantiated
	 */
	public static CustomPreprocessor getInstance( PreprocessorSetting setting, 
			Preprocessor preprocessor,
			ExtensionManager manager,
			Environment environment) 
	throws BuildException
	{
		try {
			CustomPreprocessor customProcessor = (CustomPreprocessor) manager.getExtension(  ExtensionManager.TYPE_PREPROCESSOR, setting, environment );
			customProcessor.init( preprocessor, setting );
			return customProcessor;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException("Unable to load preprocessor [" + setting.getClassName() + "]: " + e.toString() );
		}
	}
	
	/**
	 * Adds a directive which is searched for in the preprocessed source codes.
	 * Whenever the directive is found, the appropriate method process[directive-name] is
	 * called.
	 * When for example the preprocessing directive "//#hello" should be processed,
	 * the subclass needs to implement the method 
	 * processHello( String line, StringList lines, String className ).
	 * <pre>
	 * registerDirective("hello");
	 * // is the same like
	 * registerDirective("//#hello");
	 * </pre> 
	 *  
	 * @param directive the preprocessing directive which should be found.
	 *        The directive needs to contain at least 2 characters (apart from
	 * 		  the beginning "//#"). The "//#" beginning is added when not specified.
	 * @throws BuildException when the corresponding method could not be found.
	 */
	protected void registerDirective( String directive ) throws BuildException {
		String methodName = directive;
		if (directive.startsWith("//#")) {
			methodName = directive.substring(3);
		} else {
			directive = "//#" + directive;
		}
		methodName = "process" + Character.toUpperCase( methodName.charAt(0)) + methodName.substring( 1 );
		try {
			Method method = getClass().getMethod( methodName, new Class[]{ String.class, StringList.class, String.class } );
			if (this.directives == null) {
				this.directives = new ArrayList();
			}
			this.directives.add( new Directive( directive, method ));
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new BuildException("Unable to register directive [" + directive + "]: method [" + methodName + "] could not be accessed: " + e.toString(), e );
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw new BuildException("Unable to register directive [" + directive + "]: method [" + methodName + "] could not be found: " + e.toString(), e );
		}
	}
	
	/**
	 * Creates the start of an error message:
	 * 
	 * @param className the name of the current class
	 * @param lines the source code of that class
	 * @return a typical error-message start like "MyClass.java line [12]: " 
	 */
	protected String getErrorStart(String className, StringList lines) {
		return className + " line [" + (lines.getCurrentIndex() + 1) + "]: ";
	}
	
	public PreprocessorSetting getSetting() {
		return this.setting;
	}
	
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.Extension#execute(de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void execute(Device device, Locale locale, Environment environment)
	throws BuildException 
	{
		// ignore...
	}
	
	static class Directive {
		String directive;
		Method method;
		public Directive( String directive, Method method ) {
			this.directive = directive;
			this.method = method;
		}
	}

}
