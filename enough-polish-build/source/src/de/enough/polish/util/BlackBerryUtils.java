/*
 * Copyright (c) 2011 Robert Virkus / Enough Software
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

package de.enough.polish.util;

import de.enough.polish.BuildException;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

/**
 * Helper class for the BlackBerry build support
 * 
 * @author david
 */
public class BlackBerryUtils {

    public static final String WINDOWS_BB_HOME = "C:\\Program Files\\Research In Motion";


    /**
     * Looks for the defined blackberry home directory. If it wasn't defined
     * it tries some default locations.
     * @param env the environment
     * @return the directory configured as blackberry.home
     */
    public static File getBBHome(Environment env) {
        //Try user locations.
        String blackberryHomeStr = env.getVariable("blackberry.emulator.home");
        if (blackberryHomeStr == null) {
            blackberryHomeStr = env.getVariable("blackberry.home");
        }
        //Try guess it windows installs it in a standard place.
        if (blackberryHomeStr == null) {
            File file = new File(WINDOWS_BB_HOME);
            if (file.exists()) {
                blackberryHomeStr = WINDOWS_BB_HOME;
            } else {
            	throw new BuildException("Unable to start blackberry simulator: Ant property \"blackberry.home\" is not set.");
            }
        }
        File blackberryHome = new File(blackberryHomeStr);
        if (!blackberryHome.exists()) {
            throw new BuildException("Can not find blackberry home. Please define blackberry.home or make sure it points to a valid location.");
        }
        return blackberryHome;
    }

    /**
     * Gets the best bbhome bassed on a specific device.
     * This method searches for best possible match checking to see if the simulator was in the path
     * or via the BlackBerry platform version string.
     * @param dev the device
     * @param env the environment
     * @return the directory for the best blackberry JDE
     */
    public static File getBBHome(Device dev, Environment env) {
        File blackberryHome = getBBHome(env);
        // search for "*JDE*" folders:
        File parent;
        if (blackberryHome.getName().indexOf("JDE") == -1) {
            parent = blackberryHome;
        } else {
            parent = blackberryHome.getParentFile();
        }

        //Look for jde's in main blackberry.home
        File[] jdes = parent.listFiles(new DirectoryFilter("JDE"));
        //Look in most highest versioned blackberry folders first
        //must use java.util.Arrays
        Arrays.sort(jdes);

        //First search for an exact matched device.
        //It was BlackBerry Device Simulators, but searching for simulators will also find this value.
        //My install of 4.2 4.7 the folder is called simulators so try match it all.
        //Also make sure that the found device matches the BlackBerry OS version 
        String bbVersion = getBlackBerryOSVersion(dev);
        for (int i = jdes.length - 1; i >= 0; i--) {
            File jdeFolder = jdes[i];
            if (jdeFolder.getName().indexOf(bbVersion) >= 0) {            
	            File[] simulators = jdeFolder.listFiles(new DirectoryFilter("simulator"));
	            for (int j = simulators.length - 1; j >= 0; j--) {
	                File executable = getExecutable(simulators[j], dev, env);
	                if (executable.exists()) {
	                	
	                    return jdeFolder;
	                }
	            }
            }
        }

        //Second search for BB OS Home:
        //Search for closest BBversion
        for (int i = jdes.length - 1; i >= 0; i--) {
            File jdeFolder = jdes[i];
            if (jdeFolder.getName().indexOf(bbVersion) >= 0) {
                return jdeFolder;
            }
        }
        if (jdes.length == 0) {
        	throw new BuildException("Unable to resolve blackberry.home path of " + blackberryHome.getAbsolutePath() + ". Please correct this setting in your build.xml script.");
        }
        System.out.println("WARNING: could not find " + bbVersion
                + " of blackberry we are now using "
                + jdes[jdes.length - 1].getAbsolutePath() + " instead.");

        //Exact version not found use last JDE
        return jdes[jdes.length - 1];
    }

    /**
     * Retrieves the executable
     *
     * @param dev the device
     * @param env the environment
     * @return the executable file, which might not exist
     */
    public static File getExecutable(File simulatorHome, Device dev, Environment env) {
        File executable = new File(simulatorHome, dev.getName() + ".bat");
        if (!executable.exists()) {
            String alternativeName = dev.getCapability("polish.Emulator.Skin");
            if (alternativeName != null) {
                executable = new File(simulatorHome, alternativeName + ".bat");
            }
        }
        return executable;
    }

    public static String getBlackBerryOSVersion(Device dev) {
        String vers = dev.getCapability("build.BlackBerry.JDE-Version");
        if (vers != null && vers.length() > 0) {
            return vers;
        }
        //Try to get what the JDE version is. This shouldn't be there. ;/
        String input = getBlackBerryOSVersion(dev.getCapability(Device.JAVA_PLATFORM));
        if (input == null) {
            throw new BuildException("BlackBerry Device doesn't contain OS version");
        }
        return input;
    }

    public static String getBlackBerryOSVersion(String input) {
        String[] inputs = StringUtil.splitAndTrim(input, ',');
        for (int i = 0; i < inputs.length; i++) {
            String platform = inputs[i].toLowerCase().trim();
            if (platform.startsWith("blackberry/")) {
                String version = platform.substring("blackberry/".length());
                return version;
            }
        }
        return null;
    }

    public static File getRapc(Device dev, Environment env) {
        return getRapc( getBBHome(dev, env), dev, env );
    }
    
    public static File getRapc(File bbHome, Device dev, Environment env) {
        File rapc = new File(bbHome, "/bin/rapc.jar");
        if (!rapc.exists()) {
            throw new BuildException("Could not find any rapc.jar files, looking in [" + bbHome.getAbsolutePath() + "]");
        }
        return rapc;
    }

    private static class DirectoryFilter implements FileFilter {

        private final String requiredName;

        /**
         * @param requiredName the name of the dir
         */
        public DirectoryFilter(String requiredName) {
            this.requiredName = requiredName;
        }

        public boolean accept(File file) {
            //Compare case insensivity
            return file.isDirectory()
                    && file.getName().toLowerCase().indexOf(this.requiredName.toLowerCase()) >= 0;
        }
    }
}
