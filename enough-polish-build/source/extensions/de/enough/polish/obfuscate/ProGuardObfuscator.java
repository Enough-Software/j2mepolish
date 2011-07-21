/*
 * Created on 2-Sept-2004 at 12:49:27.
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
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Path;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ExtensionDefinition;
import de.enough.polish.Variable;
import de.enough.polish.preverify.Preverifier;
import de.enough.polish.util.AbbreviationsGenerator;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.OrderedMultipleEntriesMap;
import de.enough.polish.util.OutputFilter;
import de.enough.polish.util.ProcessUtil;
import de.enough.polish.util.StringUtil;

/**
 * <p>Is used to obfuscate code with the ProGuard obfuscator.</p>
 * <p>For details of ProGuard, please refer to http://proguard.sourceforge.net/.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        2-Sept-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class ProGuardObfuscator 
extends Obfuscator
implements OutputFilter
{
	
	private boolean doOptimize;
	private File proGuardJarFile;
	private boolean dontObfuscate;
	private boolean overloadAggressively = true;
	private String includeFileName;
	
	private Map furtherParameters;
	private boolean isVerbose;
	private boolean doPreverify = true;

	/**
	 * Creates a new pro guard obfuscator.
	 */
	public ProGuardObfuscator() {
		super();
	}
	
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.obfuscate.Obfuscator#obfuscate(de.enough.polish.Device, java.io.File, java.lang.String[], org.apache.tools.ant.types.Path)
	 */
	public void obfuscate(Device device, File sourceFile, File targetFile, String[] preserve, Path bootClassPath) 
	throws BuildException 
	{
		if (this.proGuardJarFile == null) {
			String proguardPath;
			ExtensionDefinition definition = getExtensionDefinition();
			if (definition != null && definition.getClassPath() != null) {
				proguardPath = getEnvironment().writeProperties( definition.getClassPath().toString() );
			} else {
				proguardPath = getEnvironment().writeProperties( "${polish.home}/lib/proguard.jar" );
			}
			this.proGuardJarFile = new File( proguardPath );
			if ( !this.proGuardJarFile.exists() ) {
				this.proGuardJarFile = new File( getEnvironment().getBaseDir(), proguardPath );
			}
		}
		OrderedMultipleEntriesMap params = new OrderedMultipleEntriesMap();
		if (this.includeFileName!=null) {
			params.put("-include", this.includeFileName);
		}
		// the input jar file:
		params.put( "-injars", quote( sourceFile.getAbsolutePath() ) );
		// the output jar file:
		params.put(  "-outjars", quote( targetFile.getAbsolutePath() ) );
		// the libraries:
		StringBuffer buffer = new StringBuffer();
		String[] apiPaths = device.getBootClassPaths();
		String path = apiPaths[0];
		buffer.append( quote( path ) );
		for (int i = 1; i < apiPaths.length; i++) {
			path = apiPaths[i];
			buffer.append( File.pathSeparatorChar );
			buffer.append( quote( path ) );
		}
		apiPaths = device.getClassPaths();
		for (int i = 0; i < apiPaths.length; i++) {
			path = apiPaths[i];
			buffer.append( File.pathSeparatorChar );
			buffer.append( quote( path ) );
		}
		params.put( "-libraryjars", buffer.toString() );
		// add classes that should be kept from obfuscating:
		Map keepClassesByName = new HashMap();
		addKeepClasses(preserve, params, keepClassesByName);
		Environment env = device.getEnvironment();
		if (env != null && env.getVariable("polish.build.Obfuscator.KeepClasses") != null ) {
			preserve = StringUtil.splitAndTrim(env.getVariable("polish.build.Obfuscator.KeepClasses"), ',');
			addKeepClasses(preserve, params, keepClassesByName);			
		}
		// add settings:
		if (!this.doOptimize) {
			params.put( "-dontoptimize", "" );
		}
		if (this.environment.hasSymbol("polish.build.obfuscator.ignorewarnings")) {
			params.put( "-ignorewarnings", "" );
		}
	    List serializableClassNames = (List) this.environment.get("serializable-classes" );
	    if (serializableClassNames != null) {
	    	Map obfuscationMap = new HashMap();
	    	File mapFile = new File( this.environment.getProjectHome(), ".polishSettings/obfuscation-map.txt" );
	    	int originalObfuscationMapSize = 0;
	    	if (mapFile.exists()) {
	    		try {
					FileUtil.readPropertiesFile(mapFile, '=', obfuscationMap);
					originalObfuscationMapSize = obfuscationMap.size();
					// remove all keep classes from the map:
					String[] classNames = (String[]) obfuscationMap.keySet().toArray( new String[ originalObfuscationMapSize ]);
					for (int i = 0; i < classNames.length; i++)
					{
						String name = classNames[i];
						if (keepClassesByName.get(name) != null) {
							obfuscationMap.remove(name);
						}
					}
					
				} catch (IOException e) {
					System.out.println("warning: unable to read obfuscation map " + mapFile.getAbsolutePath() + ": " + e.toString() + " - this error is ignored.");
				}
	    	}
	    	AbbreviationsGenerator abbreviationsGenerator = new AbbreviationsGenerator( obfuscationMap, AbbreviationsGenerator.ABBREVIATIONS_ALPHABET_LOWERCASE );
	    	// add all class names to the map:
	    	for (Iterator iter = serializableClassNames.iterator(); iter.hasNext();) {
				String className = (String) iter.next();
				if (keepClassesByName.get(className) == null) {
					abbreviationsGenerator.getAbbreviation(className, true);
				}
			}
	    	// store obfuscation map:
	    	if (obfuscationMap.size() != originalObfuscationMapSize) {
		    	try {
					FileUtil.writePropertiesFile(mapFile, obfuscationMap );
				} catch (IOException e) {
					BuildException be = new BuildException("Unable to write obfuscation map " + mapFile.getAbsolutePath() + ": " + e.toString() );
					be.initCause(e);
					throw be;
				}
	    	}
	    	// create ProGuard specific obfuscation map file:
	    	String[] keys = (String[]) obfuscationMap.keySet().toArray( new String[ obfuscationMap.size() ] );
	    	String[] lines = new String[ keys.length ];
	    	for (int i = 0; i < keys.length; i++) {
				String key = keys[i];
				lines[i] = key + " -> "+ obfuscationMap.get( key ) + ":";
			}
	    	File pgMapFile = new File( device.getBaseDir(), "input-obfuscation-map.txt" );
	    	try {
				FileUtil.writeTextFile( pgMapFile, lines);
			} catch (IOException e) {
				BuildException be = new BuildException("Unable to write obfuscation map " + pgMapFile.getAbsolutePath() + ": " + e.toString() );
				be.initCause(e);
				throw be;
			}
			params.put("-applymapping", quote( pgMapFile.getAbsolutePath() ) );
	    }

	    params.put( "-printmapping" , quote( device.getBaseDir() + File.separator + "obfuscation-map.txt" ) );
		if (this.dontObfuscate) {
			params.put( "-dontobfuscate", "" );			
		} else {
			params.put( "-allowaccessmodification", "" );
			if (this.overloadAggressively) {
				params.put( "-overloadaggressively", "" );
			}
			params.put( "-defaultpackage", "\"\"" );
			params.put( "-dontusemixedcaseclassnames", "" );			
		}
		if (this.doPreverify) {
			params.put( "-microedition", "" );
			env.addVariable(Preverifier.BUILDCONTROL_PREVERIFIER_ENABLED, "false");
		}
		
		ArrayList argsList = new ArrayList();
		// the executable:
		argsList.add( "java" );
		argsList.add( "-jar" );
		argsList.add( this.proGuardJarFile.getAbsolutePath() );
		
		for (int i = 0; i < params.size(); i++) {
			String key = (String) params.getKey(i);
			String value = (String) params.getValue(i);
			argsList.add( key );
			if (value.length() > 0) {
				argsList.add( value );
			}
		}
		if (this.furtherParameters != null) {
			Object[] keys = this.furtherParameters.keySet().toArray();
			for (int i = 0; i < keys.length; i++)
			{
				String key = (String) keys[i];
				String value = (String) this.furtherParameters.get(key);
				argsList.add( key );
				if (value.length() > 0) {
					argsList.add( value );
				}
			}
		}
		
		adaptParameters( params, device, env );

		//System.out.println( argsList );
		int result = 0;
		try {
			result = ProcessUtil.exec(argsList, "proguard: ", true, this );
			// invoking the main(String[]) method fails because
			// ProGuard calls System.exit(0) explicitely (for whatever reason)...
			//JarUtil.exec( this.proGuardJarFile, argsList, getClass().getClassLoader() );
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ProGuard arguments: " + argsList.toString() );
			throw new BuildException("ProGuard is unable to obfuscate: " + e.toString(), e );
		}
		if (result != 0) {
			System.out.println("ProGuard arguments: " + argsList.toString() );
			throw new BuildException("ProGuard was unable to obfuscate - got return value [" + result + "].");
		}
		/*
		 * The direct invocation cannot be used anymore, because Eric Lafortune withdraw his permission for this...
		argsList.add( "" );
		argsList.add( "" );
		argsList.add( "" );
		// create the configuration for ProGuard:
		Configuration cfg = new Configuration();
		
		// set libraries:
		cfg.libraryJars  = getPath( bootClassPath.toString(), false );
		String[] apiPaths = device.getClassPaths();
		for (int i = 0; i < apiPaths.length; i++) {
			cfg.libraryJars.addAll( getPath( apiPaths[i], false ) );
		}
		cfg.allowAccessModification = true;
		cfg.programJars = getPath( sourceFile, false );
		cfg.programJars.addAll( getPath( targetFile, true ) );

        // do not obfuscate the <keep> classes and the defined midlets:
        cfg.keep = new ArrayList( preserve.length );
        for (int i = 0; i < preserve.length; i++) {
			String className = StringUtil.replace( preserve[i], '.', '/');
			//System.out.println("\n PROGUARD: preservering: " + className );
	        cfg.keep.add(
	        		new ClassSpecification(
        				ClassConstants.INTERNAL_ACC_PUBLIC,
	                    0,
	                    className,
	                    null,
	                    true,
	                    false));
		}
        // some devices do not support mixed-case class names:
        cfg.useMixedCaseClassNames = false;

        // overload names with different return types:
        cfg.overloadAggressively = true;
        
        // optimize the resulting byte-code:
        cfg.optimize = this.doOptimize;
        
        // shrink the code:
        cfg.shrink = true;
        
        cfg.printMapping = device.getBaseDir() + File.separator + "obfuscation-map.txt";

        // move all classes to the root package:
        cfg.defaultPackage = "";		
        
        ProGuard proGuard = new ProGuard( cfg );
		try {
			proGuard.execute();
			System.out.println("ProGuard has successfully finished obfuscation.");
		} catch (IOException e) {
			// check if all preserve-classes are found:
			for (int i = 0; i < preserve.length; i++) {
				String className = preserve[i];
				String fileName = StringUtil.replace( className, '.', File.separatorChar ) + ".java";
				File file = new File( device.getSourceDir() + File.separator + fileName );
				if (!file.exists()) {
					System.err.println("WARNING: the MIDlet or class [" + className + "] was not found: [" + file.getAbsolutePath() + "] does not exist.");
					System.err.println("Please check your <midlet>-setting in the file [build.xml].");
				}
			}
			throw new BuildException("ProGuard was unable to obfuscate: " + e.getMessage(), e );
		}
		*/
	}



	/**
	 * Allows subclasses to adapt the launch parameters
	 * @param params the launch parameters
	 * @param device the device
	 * @param env the environment
	 */
	protected void adaptParameters(OrderedMultipleEntriesMap params, Device device, Environment env) {
		// let subclasses handle this
		
	}



	/**
	 * @param preserve
	 * @param params
	 * @param keepClassesByName
	 */
	private void addKeepClasses(String[] preserve,
			OrderedMultipleEntriesMap params, Map keepClassesByName) 
	{
		for (int i = 0; i < preserve.length; i++) {
			String className = preserve[i];
			if (className.indexOf('{') == -1) {
				params.put( "-keep", "class " + className + "{ *; }" );
			} else {
				params.put( "-keep", "class " + className );
			}
			keepClassesByName.put( className, Boolean.TRUE );
		}
	}
	
	/**
	 * Converts the path of the given file to a proguard.ClassPath
	 * 
	 * @param file The file for which a class path should be retrieved.
	 * @param isOutput true when this path specifies the output-jar.
	 * @return The classpath of the given file
	private ClassPath getPath(File file, boolean isOutput ) {
		return getPath( file.getAbsolutePath(), isOutput );
	}
	 */

	/**
	 * Converts the given file path to a proguard.ClassPath
     * 
	 * @param path The path as a String
	 * @param isOutput true when this path specifies the output-jar.
	 * @return The path as a proguard-ClassPath
    private ClassPath getPath(String path, boolean isOutput)
    {
        ClassPath classPath = new ClassPath();
        String[] elements = StringUtil.split( path, File.pathSeparatorChar );
        for (int i = 0; i < elements.length; i++) {
        	ClassPathEntry entry =
                new ClassPathEntry( elements[i], isOutput );

            classPath.add(entry);
		}
        return classPath;
    }
     */
    
	/**
	 * Enables or disables the optimization step of ProGuard.
	 * 
	 * @param optimize true when the optimization should be enabled. By default the
	 *        optimization is enabled.
	 */
    public void setOptimize( boolean optimize ) {
    	this.doOptimize = optimize;
    }



	/* (non-Javadoc)
	 * @see de.enough.polish.util.OutputFilter#filter(java.lang.String, java.io.PrintStream)
	 */
	public void filter(String message, PrintStream output) {
		if (this.isVerbose 
			|| (message.indexOf("Note:") == -1
			&& message.indexOf("Reading program") == -1
			&& message.indexOf("Reading library") == -1			
			&& message.indexOf("You might consider") == -1
			&& message.indexOf("their implementations") == -1
			&& message.indexOf("Copying resources") == -1
			) )
		{
			output.println( message );
		}
		
	}
	
	private String quote( String path ) {
		if ( path.indexOf( ' ' ) != -1 ) {
			// for some weird reason proguard expects
			// to be doubled quoted under Windows... sigh.
			if ( File.separatorChar == '\\' ) {
				path = "\"'" + path + "'\"";
			} else {
				path = '"' + path + '"';
			}
		}
		return path;
	}



	/**
	 * @param dontObfuscate The dontObfuscate to set.
	 */
	public void setDontObfuscate(boolean dontObfuscate) {
		this.dontObfuscate = dontObfuscate;
	}
	
	/**
	 * Allows to include a specific ProGuard configuration file which is provided to ProGuard using the -include command line option.
	 * @param filename the name of the ProGuard configuration file.
	 */
	public void setInclude(String filename)
	{
		this.includeFileName=filename;
	}
	
	/**
	 * Sets all parameters
	 * @param parameters the parameters
	 * @param baseDir the base directory
	 */
	public void setParameters( Variable[] parameters, File baseDir ) {
		if (this.furtherParameters == null ) {
			this.furtherParameters = new HashMap();
		} else {
			this.furtherParameters.clear();
		}
		for (int i = 0; i < parameters.length; i++)
		{
			Variable variable = parameters[i];
			String name =  variable.getName().toLowerCase();
			if (name.charAt(0) != '-') {
				name = "-" + name;
			}
			String value = variable.getValue() == null ?  "" : variable.getValue();
			if (name.equals("-optimize")) {
				if ("".equals(value) || "false".equals(value)) {
					value = "";
					name = "-dontoptimize";
					this.doOptimize = false;
				} else {
					this.doOptimize = true;
					continue;
				}
			}
			else if (name.equals("-verbose")) {
				if ("true".equals(value)) {
					value = "";
					this.isVerbose = true;
				} else {
					continue;
				}
			}
			else if (name.equals("-overloadaggressively")) {
				if ("false".equals(value)) {
					this.overloadAggressively = false;
				}
				continue;
			}
			this.furtherParameters.put(name, value);
		}
	}
}
