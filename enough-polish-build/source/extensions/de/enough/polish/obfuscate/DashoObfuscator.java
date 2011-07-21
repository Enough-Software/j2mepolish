/*
 * Created on 05-Nov-2004 at 20:40:16.
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
package de.enough.polish.obfuscate;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;

import de.enough.polish.Device;
import de.enough.polish.ant.build.ObfuscatorSetting;
import de.enough.polish.devices.LibraryManager;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.JarUtil;
import de.enough.polish.util.LoggerThread;

/**
 * <p>Integrates the DashO obfuscator from Preemptive Solutions.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        05-Nov-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DashoObfuscator extends Obfuscator {
	
	private String version = "3.1";
	private boolean enableRenaming = true;
	private boolean enableOptimization = true;
	private String constantPoolTag;
	private boolean enableStringEncryption;
	private boolean enableFlowEncryption;
	private File scriptFile;
	private Method dashoMainMethod;
	private String dashoMainClassName;
	private String classPathParameter;
	private Map packageNamesByClassPath;


	/**
	 * Creates a new obfuscator
	 */
	public DashoObfuscator() {
		super();
		this.packageNamesByClassPath = new HashMap();
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.obfuscate.Obfuscator#init(org.apache.tools.ant.Project, java.io.File, de.enough.polish.LibraryManager)
	 */
	public void init(ObfuscatorSetting obfuscatorSetting, Project proj, File lbDir, LibraryManager lbManager) {
		super.init(obfuscatorSetting, proj, lbDir, lbManager);
		String dashOHomePath = proj.getProperty("dasho.home");
		if (dashOHomePath != null) {
			File home = new File( dashOHomePath );
			if (home.exists()) {
				setDashoHome(home);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.obfuscate.Obfuscator#obfuscate(de.enough.polish.Device, java.io.File, java.io.File, java.lang.String[], org.apache.tools.ant.types.Path)
	 */
	public void obfuscate(Device device, File sourceFile, File targetFile,
			String[] preserve, Path bootClassPath) 
	throws BuildException 
	{
		try {
			if (this.dashoMainMethod == null) {
				initDashOMainClass( getClass().getClassLoader() );
			}
			File script = createScriptFile( device, sourceFile, targetFile, preserve, bootClassPath );
			if (false) {
				// direct invocation does not seem to work
				this.dashoMainMethod.invoke(null, new Object[]{ new String[]{ script.getAbsolutePath() }} );
			}
			// invoke DashO from the commandline:
			String[] parameters = new String[5];
			parameters[0] = "java";
			parameters[1] = "-classpath";
			parameters[2] = this.classPathParameter;
			parameters[3] = this.dashoMainClassName;
			parameters[4] = script.getAbsolutePath();
			Process process = Runtime.getRuntime().exec( parameters );
			LoggerThread.log( process, "DashO: ");
			int result = process.waitFor();
			if (result != 0) {
				throw new BuildException("DashO was unable to obfuscate - return value is [" + result + "].");
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException("Unable to obfuscate with DashO: " + e.toString(), e );
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new BuildException("Unable to obfuscate with DashO: " + e.toString(), e );
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new BuildException("Unable to obfuscate with DashO: " + e.toString(), e );
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new BuildException("Unable to obfuscate with DashO: " + e.toString(), e );
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new BuildException("Unable to obfuscate with DashO: " + e.toString(), e );
		}

	}
	
	/**
	 * Creates and returns the script file.
	 * 
	 * @param device
	 * @param sourceFile
	 * @param targetFile
	 * @param preserve
	 * @param bootClassPath
	 */
	private File createScriptFile(Device device, File sourceFile, File targetFile, String[] preserve, Path bootClassPath)
	throws IOException
	{
		if (this.scriptFile != null ) {
			return this.scriptFile;
		}
		ArrayList lines = new ArrayList();
		lines.add( "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" );
		lines.add( "<!-- generated by J2ME Polish at " + (new Date()).toString() + "-->" );
		lines.add( "<dasho version=\"" + this.version + "\">" );
		// globale settings:
		lines.add("\t<global>");
		lines.add("\t\t<option>donotappendrtjar</option>");
		// exclude any standard-classes:
		lines.add("\t\t<exclude classname=\"javax.*\" />");
		// exclude any device-specific packages:
		String[] classPaths = device.getClassPaths();
		for (int i = 0; i < classPaths.length; i++) {
			String classPath = classPaths[i];
			String[] packageNames = getPackageNames( classPath );
			for (int j = 0; j < packageNames.length; j++) {
				String packageName = packageNames[j];
				lines.add("\t\t<exclude classname=\"" + packageName + ".*\" />");
			}
		}
		lines.add("\t</global>");
		// add classpath:
		lines.add("\t<classpath>");
		lines.add("\t\t<jar path=\"" + bootClassPath.toString() + "\" />");
		lines.add("\t\t<jar path=\"" + sourceFile.getAbsolutePath() + "\" />");
		for (int i = 0; i < classPaths.length; i++) {
			lines.add("\t\t<jar path=\"" + classPaths[i] + "\" />");
		}
		lines.add("\t</classpath>");
		// specify entry points:
		lines.add("\t<entrypoints>");
		for (int i = 0; i < preserve.length; i++) {
			String className = preserve[i];
			lines.add("\t\t<classes name=\"" + className +"\">");
			lines.add("\t\t\t<method name=\"(midlet)\" signature=\"\" />");
			lines.add("\t\t</classes>");
		}
		lines.add("\t</entrypoints>");
		// specify the output:
		lines.add("\t<output>");
		lines.add("\t\t<jar path=\"" + targetFile.getAbsolutePath() + "\" />");
		lines.add("\t</output>");
		// specify the removal options:
		lines.add("\t<removal action=\"all\" />");
		// specify the renaming options:
		if (this.enableRenaming) {
			lines.add("\t<renaming prefix=\"\" option=\"on\">");
		} else {
			lines.add("\t<renaming prefix=\"\" option=\"off\">");
		}
		// specify the exclusions for renaming:
		lines.add("\t\t<excludelist>");
		for (int i = 0; i < preserve.length; i++) {
			String className = preserve[i];
			lines.add("\t\t\t<classes regex=\"false\" name=\"" + className + "\" />");
		}		
		lines.add("\t\t</excludelist>");
		// specify the renaming-report-file:
		lines.add("\t\t<mapping>");
		lines.add("\t\t\t<mapreport>");
		lines.add("\t\t\t\t<file path=\"" + device.getBaseDir() + File.separator + "obfuscation-map.txt\" />");
		lines.add("\t\t\t</mapreport>");
		lines.add("\t\t</mapping>");
		lines.add("\t</renaming>");
		// activate  bytecode-optimization:
		if (this.enableOptimization) {
			lines.add("\t<optimization option=\"on\" />");
		} else {
			lines.add("\t<optimization option=\"off\" />");
		}
		// disable obfuscation of the control flow:
		if (this.enableFlowEncryption) {
			lines.add("\t<controlflow option=\"on\" />");
		} else {
			lines.add("\t<controlflow option=\"off\" />");
		}
		// disable string encryption:
		if (this.enableStringEncryption) {
			lines.add("\t<stringencrypt option=\"on\" />");
		} else {
			lines.add("\t<stringencrypt option=\"off\" />");
		}
		if (this.constantPoolTag == null) {
			lines.add("\t<constantpooltag />");
		} else {
			lines.add("\t<constantpooltag>" + this.constantPoolTag + "</constantpooltag>");
		}
		lines.add("\t<includenonclassfiles />");
		lines.add("\t<preverifier run=\"false\" />");
		lines.add( "</dasho>" );
		File standardScriptFile = new File( device.getBaseDir(), "dasho.dox");
		FileUtil.writeTextFile( standardScriptFile, lines );
		return standardScriptFile;
	}



	/**
	 * @param classPath
	 * @return
	 * @throws IOException
	 */
	private String[] getPackageNames(String classPath) throws IOException {
		String[] packageNames = (String[]) this.packageNamesByClassPath.get( classPath );
		if ( packageNames == null ) {
			packageNames = JarUtil.getPackageNames( new File( classPath ) );
			this.packageNamesByClassPath.put( classPath, packageNames );
		}
		return packageNames;
	}



	public void setDashoHome( File dashoHome ) {
		if (!dashoHome.exists()) {
			throw new BuildException("Unable to initialisise DashO-obfuscator: Either the property \"dasho.home\" or the parameter \"DashoHome\" points to a non-existing directory: "  + dashoHome.getAbsolutePath() );
		}
		String dashoProJarPath = dashoHome.getAbsolutePath() + File.separator + "DashOPro.jar";
		AntClassLoader classLoader = new AntClassLoader( this.project, new Path( this.project, dashoProJarPath));
		// include all jar-files from the dasho.home/lib-folder:
		File libFolder = new File( dashoHome, "lib" );
		File[] libraries = libFolder.listFiles();
		StringBuffer classPathBuffer = new StringBuffer();
		classPathBuffer.append( dashoProJarPath );
		for (int i = 0; i < libraries.length; i++) {
			File library = libraries[i];
			String name = library.getName(); 
			if ( name.endsWith(".jar") || name.endsWith("zip")) {
				classLoader.addPathElement( library.getAbsolutePath() );
				classPathBuffer.append( File.pathSeparatorChar )
							.append( library.getAbsolutePath() );
			}
		}
		this.classPathParameter = classPathBuffer.toString();
		// retrieve main class and method:
		initDashOMainClass(classLoader);
		
	}
	
	/**
	 * Tries to load the main()-method from either DashoPro.class or from DashoProEval.class
	 * @throws BuildException when the main class was not found.
	 */
	private void initDashOMainClass( ClassLoader classLoader)  {
		try {
			Class dashoClass;
			try {
				dashoClass = classLoader.loadClass("DashOPro");
				this.dashoMainClassName = "DashOPro";
			} catch (ClassNotFoundException e) {
				// ok, try to load the evaluation-class: 
				dashoClass = classLoader.loadClass( "DashOProEval" );
				this.dashoMainClassName = "DashOProEval";
			}
			this.dashoMainMethod = dashoClass.getMethod("main", new Class[]{ String[].class } );
			if (!Modifier.isStatic( this.dashoMainMethod.getModifiers() )) {
				throw new BuildException("Unable to find main(String[])-method of the " + dashoClass.getName() + "-class.");
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new BuildException("Unable to find the DashoPro or DashoProEval-class. Please set either the \"dasho.home\"-property or the \"DashoHome\"-parameter in the build.xml.");
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new BuildException("Unable to load the main(String[])-method DashoPro or DashoProEval-class. Please set either the \"dasho.home\"-property or the \"DashoHome\"-parameter in the build.xml.");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw new BuildException("Unable to load the main(String[])-method DashoPro or DashoProEval-class. Please set either the \"dasho.home\"-property or the \"DashoHome\"-parameter in the build.xml.");
		}
		
	}



	public void setVersion( String version ) {
		this.version = version;
	}
	
	public void setScriptFile( File scriptFile ) {
		if (!scriptFile.exists()) {
			throw new BuildException("Unable to find the DashO-script file [" + scriptFile.getAbsolutePath() + "].");
		}
		this.scriptFile = scriptFile;
	}

	public void setConstantPoolTag(String constantPoolTag) {
		this.constantPoolTag = constantPoolTag;
	}
	public void setEnableFlowObfuscation(boolean enableFlowEncryption) {
		this.enableFlowEncryption = enableFlowEncryption;
	}
	public void setEnableOptimization(boolean enableOptimization) {
		this.enableOptimization = enableOptimization;
	}
	public void setEnableRenaming(boolean enableRenaming) {
		this.enableRenaming = enableRenaming;
	}
	public void setEnableStringEncryption(boolean enableStringEncryption) {
		this.enableStringEncryption = enableStringEncryption;
	}
}
