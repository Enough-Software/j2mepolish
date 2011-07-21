/*
 * Created on 11-Jul-2004 at 20:02:55.
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
package de.enough.polish.preprocess.custom;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.tools.ant.AntClassLoader;
import de.enough.polish.BuildException;
import org.apache.tools.ant.Project;

import de.enough.polish.Device;
import de.enough.polish.ant.build.PreprocessorSetting;
import de.enough.polish.preprocess.CustomPreprocessor;
import de.enough.polish.preprocess.Preprocessor;
import de.enough.polish.util.ReflectionUtil;
import de.enough.polish.util.StringList;

/**
 * <p>Loads a custom preprocessor from a different classpath.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        11-Jul-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class WrapperPreprocessor extends CustomPreprocessor {

	private Object lineProcessor;
	private Method processClassMethod;
	private Method notifyDeviceMethod;
	private Method notifyPolishPackageMethod;

	/**
	 * Creates a new wrapper line processor
	 *  
	 * @param setting the definition of the line processor 
	 * @param preprocessor the preprocessor
	 * @param project the Ant project
	 * @throws InvocationTargetException when a method could not be loaded
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public WrapperPreprocessor( PreprocessorSetting setting, 
			Preprocessor preprocessor,
			Project project ) 
	throws InstantiationException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException 
	{
		super();
    	AntClassLoader antClassLoader = new AntClassLoader(
    			getClass().getClassLoader(),
    			project,  
    			setting.getClassPath(),
				true);
    	Class lineProcessorClass = antClassLoader.loadClass( setting.getClassName() ); 
    	this.lineProcessor = lineProcessorClass.newInstance();
    	// now init the line processor:
    	Method initMethod = lineProcessorClass.getMethod("init", new Class[]{ Preprocessor.class } );
    	initMethod.invoke( this.lineProcessor, new Object[]{ preprocessor } );

    	// set parameters if there are any:
		if (setting.hasParameters()) {
			ReflectionUtil.populate( this.lineProcessor, setting.getParameters(), project.getBaseDir() );
		}
    	
    	// retrives processing method:
    	this.processClassMethod = lineProcessorClass.getMethod("processClass", new Class[]{ StringList.class, String.class} );
    	// retrieve notify methods:
    	this.notifyDeviceMethod = lineProcessorClass.getMethod("notifyDevice", new Class[]{ Device.class, Boolean.TYPE } );
    	this.notifyPolishPackageMethod = lineProcessorClass.getMethod("notifyPolishPackageStart", new Class[0] );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.LineProcessor#processClass(de.enough.polish.util.StringList, java.lang.String)
	 */
	public void processClass(StringList lines, String className) {
		try {
			this.processClassMethod.invoke(this.lineProcessor, new Object[]{ lines, className} );
		} catch (BuildException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException("Unable to invoke processClass-method of preprocessor [" + this.lineProcessor.getClass().getName() + "]: " + e.toString() );
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.LineProcessor#notifyDevice(de.enough.polish.Device, boolean)
	 */
	public void notifyDevice(Device device, boolean usesPolishGui) {
		try {
			this.notifyDeviceMethod.invoke(this.lineProcessor, new Object[]{ device, Boolean.valueOf( usesPolishGui) } );
		} catch (BuildException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException("Unable to invoke notifyDevice-method of preprocessor [" + this.lineProcessor.getClass().getName() + "]: " + e.toString() );
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.LineProcessor#notifyPolishPackageStart()
	 */
	public void notifyPolishPackageStart() {
		try {
			this.notifyPolishPackageMethod.invoke(this.lineProcessor, new Object[0] );
		} catch (BuildException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException("Unable to invoke notifyPolishPackageStart-method of preprocessor [" + this.lineProcessor.getClass().getName() + "]: " + e.toString() );
		}
	}
}
