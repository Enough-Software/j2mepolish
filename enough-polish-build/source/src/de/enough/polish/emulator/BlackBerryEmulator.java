package de.enough.polish.emulator;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.emulator.EmulatorSetting;
import de.enough.polish.util.BlackBerryUtils;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.OsUtil;
import de.enough.polish.util.OutputFilter;
import de.enough.polish.util.ProcessUtil;

/**
 * Invokes a specific BlackBerry simulator.
 * <pre>
 * history
 *        29-Dec-2009 - David refactored JDE detection
 *                    - Better linux support, Wine can be specified  by wine.cmd
 *        30-Dec-2009 - David Blackberry Emultors work completely in wine now.
 *        07-April-2010 - David improved BB coded by using BlackBerryUtils.
 * </pre>
 * @author Robert Virkus
 * @author David Rubin
 * TODO: filter out wine noise fixme: and error: by config
 */
public class BlackBerryEmulator extends Emulator {

    private File blackberryHome;
    private File executionDir;
    private String[] arguments;
    private String shortName = null;
    private boolean isSimulatorRunning = false;
    private boolean supportsFledgeController = false;

    public boolean init(Device dev, EmulatorSetting setting,
            Environment env) {

        File executable = getEmulator(dev, env);
        if (executable != null && !executable.exists()) {
            return false;
        }
        
        // Check if fledgecontroller is supported
        if ( env.isConditionFulfilled("polish.build.BlackBerry.JDE-Version >= 4.2") ) {
        	this.supportsFledgeController = true;
        }
        
        this.device = dev;
        this.executionDir = executable.getParentFile();

        ArrayList argumentsList = new ArrayList();
        if (!OsUtil.isRunningWindows()) {
            // this is a unix environment, try wine:
            //wine can NOT execute .bat files so we need to extract the relavent info.
            System.out.println("Extracting params from [" + executable + "] for use in wine.");
            try {
                String[] data = FileUtil.readTextFile(executable);
                String[] args = new String[0];
                //Only include data from the running application line.
                for (int i = 0; i < data.length; i++) {
                    if (data[i].trim().startsWith("fledge.exe")) {
                        args = data[i].split(" ");
                    }
                }
                if (args.length > 0) {
                    //
                    String wineBinary = env.getVariable("wine.cmd");
                    if (wineBinary != null && wineBinary.length() > 0) {
                        argumentsList.add(wineBinary);
                    } else {
                        argumentsList.add("wine");
                    }
                    //Should include the fledge.exe
                    for (int i = 0; i < args.length; i++) {
                        argumentsList.add(args[i]);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                //Blackberry don't by default support linux.
                System.err.println("Failed to extract Blackberry params");
                return false;
            }
        } else {
            argumentsList.add(executable.getAbsolutePath());
        }
        this.arguments = (String[]) argumentsList.toArray(new String[argumentsList.size()]);

        // now copy the jar, cod, alx and jad files to the simulator's home directory:
        File targetDir = this.executionDir;
        File file = new File(env.getVariable("polish.jadPath"));
        this.shortName = file.getName().substring(0, file.getName().length() - ".jar".length());
        try {
            //FileUtil.copy( file, targetDir );
            //file = new File( env.getVariable("polish.jarPath") );
            //FileUtil.copy( file, targetDir );
            String baseName = file.getAbsolutePath();
            baseName = baseName.substring(0, baseName.length() - ".jar".length());
            file = new File(baseName + ".cod");
            FileUtil.copy(file, targetDir);
            file = new File(baseName + ".alx");
            FileUtil.copy(file, targetDir);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Unable to copy BlackBerry resources to the simulator directory: " + e.toString());
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
    protected File getExecutionDir() {
        return this.executionDir;
    }
    
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.emulator.Emulator#exec(java.lang.String[], java.lang.String, boolean, de.enough.polish.util.OutputFilter, java.io.File)
	 */
	protected int exec( String[] args, String info, boolean wait, OutputFilter filter, File execDir ) 
	throws IOException 
	{	
		// If Fledgecontroller is not supported, use the old loading method.
		if ( ! this.supportsFledgeController ) {
			return super.exec(args, info, wait, filter, execDir);
		}
		
		// By default, we assume that the simulator is not already running
		this.isSimulatorRunning = false;
		
		// Set the proper path to fledgecontroller.exe
		String fledgeControllerPath = this.executionDir + File.separator + "fledgecontroller.exe";
		
		// Check if the proper simulator is already running. To do this, we use fledgecontroller to get a list
		// of all running sessions, and check if the session we need is present in the list.
		// To check if the session is present in the list, we redirect fledgecontroller's output to the filter()
		// method of this class.
		ProcessUtil.exec(new String[] { fledgeControllerPath, "/get-sessions"}, info, true, this, execDir );
		
		// Depending on whether the simulator is running or not, we either do nothing or launch the simulator.
		if ( this.isSimulatorRunning ) {
			// Do nothing if the simulator is already running
			System.out.println("Blackberry " + this.device.getName() + " simulator is already running.");
		} else {
			// Launch the simulator
			super.exec( args, info, false, filter,  execDir );
			
			// Give it some "breathing room" so that it can properly initialize itself before loading the COD file
			// via fledgecontroller
			try {
				Thread.sleep(15000);
			} catch (Exception ex) {
				// ignore
			};
		}
		
		// Load the COD file via fledgecontroller
		System.out.println("Loading " + this.shortName + ".cod via fledgecontroller.");
		String [] controllerArguments = new String[3];
		controllerArguments[0] = fledgeControllerPath;
		controllerArguments[1] = "/session=" + this.device.getName();
		controllerArguments[2] = "/execute=LoadCod(\"" + this.shortName + ".cod\")";
		ProcessUtil.exec(controllerArguments, info, wait, filter, execDir);			
		
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.emulator.Emulator#filter(java.lang.String, java.io.PrintStream)
	 */
	public void filter( String logMessage, PrintStream output ) {
		// If the emulator name is found in the session list, then it is already running
		if ( !this.isSimulatorRunning && logMessage.substring(logMessage.length()-this.device.getName().length()).equals(this.device.getName()) ) {
			this.isSimulatorRunning = true;
		}
		output.println(logMessage);
	}

	/**
	 * Retrieves the emulator executable
	 * @param dev the current device
	 * @param env the environment
	 * @return a file pointing to the executable
	 */
    public File getEmulator(Device dev, Environment env) {
        this.blackberryHome = BlackBerryUtils.getBBHome(dev, env);
        File simHome = new File(this.blackberryHome, "simulator");
        File executable = BlackBerryUtils.getExecutable(simHome, dev, env);
        if (!executable.exists()) {
            System.err.println("Simulator not found for device was looking in [" + simHome.getAbsolutePath() + "]");
        }
        return executable;
    }
}