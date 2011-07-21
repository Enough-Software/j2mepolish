package de.enough.polish.emulator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.emulator.EmulatorSetting;
import de.enough.polish.util.FileUtil;

/**
 * Invokes a specific BlackBerry simulator.
 * @author Robert Virkus
 */
public class PalmEmulator extends Emulator {

	private File palmHome;
	private String[] arguments;

	public boolean init( Device dev, EmulatorSetting setting,
			Environment env ) 
	{
		String propertyName = "palm.simulator.home";
		String palmHomeStr = env.getVariable( propertyName );
		if (palmHomeStr == null) {
			propertyName = "palm.home";
			palmHomeStr = env.getVariable( propertyName );
		}
		if (palmHomeStr == null) {
			System.err.println("Unable to start Palm simulator: Neither the Ant property \"palm.simulator.home\" nor \"palm.home\" is set." );
			return false;
		}
		this.palmHome = new File( palmHomeStr );
		if ( !this.palmHome.exists() ) {
			this.palmHome = new File( this.antProject.getBaseDir(), palmHomeStr );
			if ( !this.palmHome.exists() ) {
				System.err.println("Unable to start Palm simulator: Ant property \"" + propertyName + "\" points to an invalid directory: " + this.palmHome.getAbsolutePath()  );
				return false;
			}
		}
		File executable = new File( this.palmHome, "PalmSim.exe" );
		if ( !executable.exists() ) {
			System.err.println("Unable to start Palm simulator: simulator not found: " + executable.getAbsolutePath()  );
			return false;
		}
		ArrayList argumentsList = new ArrayList();
		if (File.separatorChar == '/') { // this is a unix environment, try wine:
			argumentsList.add("wine");
			argumentsList.add( executable.getAbsolutePath() );
			argumentsList.add("--");
		} else {
			argumentsList.add( executable.getAbsolutePath() );
		}
		this.arguments = (String[]) argumentsList.toArray( new String[ argumentsList.size() ] );
		
		// now copy PRC file to the  simulator's AutoStart directory:
		String path = env.getVariable("polish.jarPath");
		path = path.substring( 0, path.length() - ".jar".length() ) + ".prc";
		String prcPath = env.getVariable( "palm.prcPath" );
		if (prcPath == null) {
			System.err.println("Unable to find the PRC file - has the jar2prc finalizer been enabled?");
		}
		File source = new File( prcPath );
		if ( !source.exists() ) {
			System.err.println("Unable to start Palm simulator: PRC file not found: " + source.getAbsolutePath()  );
			return false;
		}
		
		File target = new File( this.palmHome, "AutoLoad" + File.separatorChar + source.getName() );
		try {
			FileUtil.copy( source, target);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Unable to start Palm simulator - copying the PRC file failed: " + e.toString() );
			return false;
		}
		return true;
	}

	public String[] getArguments() {
		return this.arguments;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.emulator.Emulator#getExecutionDir()
	 */
/*	protected File getExecutionDir() {
		return new File( this.palmHome, "simulator" );
	}
*/	
	

}
