/*
 * Created on 04-Apr-2005 at 14:55:10.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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
package de.enough.polish;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.CallTarget;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.types.Path;

import de.enough.polish.util.ReflectionUtil;

/**
 * <p>Provides the common base for any extensions of J2ME Polish.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        04-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class Extension {

	protected ExtensionSetting extensionSetting;
	protected Project antProject;
	protected ExtensionManager extensionManager;
	protected ExtensionDefinition extensionDefinition;
	protected ExtensionTypeDefinition extensionTypeDefinition;
	protected Environment environment;
	protected String autoStartCondition;
	private String type;
	protected boolean isBuildStarted;

	/**
	 * Creates a new extension.
	 */
	public Extension() {
		super();
	}
	
	/**
	 * Initialises this extension.
	 * 
	 * @param typeDefinition the definition of the base type, can be null
	 * @param definition the extension definition taken from extensions.xml or custom-extensions.xml 
	 * @param setting the extension settings
	 * @param project the ant project
	 * @param env
	 */
	protected void init(ExtensionTypeDefinition typeDefinition, ExtensionDefinition definition, ExtensionSetting setting, Project project, ExtensionManager manager, Environment env) {
		if (definition != null) {
			this.autoStartCondition = definition.getAutoStartCondition();
		}
		if (typeDefinition != null) {
			this.type = typeDefinition.getName();
		} else if (definition != null) {
			this.type = definition.getType();
		}
		this.extensionDefinition = definition;
		this.extensionSetting = setting;
		this.antProject = project;
		this.extensionManager= manager;
		this.extensionTypeDefinition = typeDefinition;
		this.environment = env;
	}
	
	public ExtensionSetting getExtensionSetting() {
		return this.extensionSetting;
	}

	public ExtensionDefinition getExtensionDefinition() {
		return this.extensionDefinition;
	}

	public Project getAntProject() {
		return this.antProject;
	}
	
	public Environment getEnvironment() {
		Environment env = this.environment;
		if (env == null) {
			env = Environment.getInstance();
		}
		return env;
	}
	
	/**
	 * Configures this extension with conditional parameters.
	 * Subclasses can implement the setParameters( Variable[] parameters, File baseDir ) method
	 * and subsequently use this method for only setting parameters that either have no conditions
	 * or which conditions are fulfilled.
	 * For each valid parameter the subclass needs to provide the method set[param-name] with
	 * either the argument String, File or boolean. If the parameter name is "message" you
	 * need to implement either setMessage( String ), setMessage( File ) or setMessage( boolean ),
	 * for example.
	 * 
	 * @param parameters the parameters.
	 * @throws  IllegalArgumentException when a parameter has a syntax error
	 *        or when a needed method has not be found. 
	 */
	public void configure( Variable[] parameters ) {
		if ( parameters == null ) {
			return;
		}
		File baseDir = this.antProject.getBaseDir();
		BooleanEvaluator evaluator = this.environment.getBooleanEvaluator();
		for (int i = 0; i < parameters.length; i++) {
			Variable parameter = parameters[i];
			if ( parameter.isConditionFulfilled(evaluator, this.antProject.getProperties() ) ) {
				ReflectionUtil.populate( this, parameter, baseDir );
			}
		}
	}
	
	/**
	 * Notifies the extension about the start of the build process.
	 * Always call super.notifyBuildStart(env) in subclasses.
	 * 
	 * @param env the environment without device specific settings
	 */
	public void notifyBuildStart( Environment env ) {
		this.isBuildStarted = true;
	}
	
	/**
	 * Notifies the extension about the end of the build process.
	 * Always call super.notifyBuildEnd(env) in subclasses.
	 * 
	 * @param env the environment without device specific settings
	 */
	public void notifyBuildEnd( Environment env ) {
		this.isBuildStarted = false;
	}

	
	/**
	 * Initializes this extension for a new device or a new locale.
	 * The default implementation doesn't do anything.
	 * 
	 * @param device the current device
	 * @param locale the current locale, can be null
	 * @param env the environment/configuration
	 */
	public void initialize( Device device, Locale locale, Environment env ) {
		if (!this.isBuildStarted) {
			notifyBuildStart(env);
		}
	}
	
	/**
	 * Finalizes  this extension for a the device and locale.
	 * The default implementation doesn't do anything.
	 * 
	 * @param device the current device
	 * @param locale the current locale, can be null
	 * @param env the environment/configuration
	 */
	public void finalize( Device device, Locale locale, Environment env ) {
		// default implementation does nothing
	}
	
	/**
	 * Executes this extension. Not all extension types are executed.
	 * 
	 * @param device the current device
	 * @param locale the current locale, can be null
	 * @param env the environment/configuration
	 * @throws BuildException when the execution failed
	 */
	public abstract void execute( Device device, Locale locale, Environment env )
	throws BuildException;
	
	/**
	 * Retrieves a parameter value from either the setting, the definition or the type definition of this extension.
	 * 
	 * @param parameterName the name of the parameter
	 * @param env the environment, can be null
	 * @return either the value or null of the parameter is not defined anywhere
	 */
	public String getParameterValue( String parameterName, Environment env ) {
		if (this.extensionSetting != null) {
			Variable parameter = this.extensionSetting.getParameter(parameterName, env);
			if (parameter != null) {
				return parameter.getValue();
			}
		}
		if (this.extensionDefinition != null) {
			String value = this.extensionDefinition.getParameterValue(parameterName);
			if (value != null) {
				return value;
			}
		}
		if (this.extensionTypeDefinition != null) {
			String value = this.extensionTypeDefinition.getParameterValue(parameterName);
			if (value != null) {
				return value;
			}
		}
		return null;
	}

	
	/*
	public static Extension getInstance( ExtensionSetting setting, Project antProject ) 
	throws ClassNotFoundException, InstantiationException, IllegalAccessException 
	{
		ClassLoader classLoader;
		Path classPath = setting.getClassPath();
		if (classPath == null) {
			classLoader = setting.getClass().getClassLoader();
		} else {
			classLoader = new AntClassLoader(
    			setting.getClass().getClassLoader(),
    			antProject,  
				classPath,
				true);
		}
		Class extensionClass = classLoader.loadClass( setting.getClassName() );
		Extension extension = (Extension) extensionClass.newInstance();
		extension.init( setting, antProject );
		if (setting.hasParameters()) {
			PopulateUtil.populate( extension, setting.getParameters(), antProject.getBaseDir() );
		}
		return extension;
	}
	
	public static Extension getInstance( ExtensionDefinition definition, Project antProject, ExtensionManager manager ) 
	throws ClassNotFoundException, InstantiationException, IllegalAccessException 
	{
		
		ClassLoader classLoader;
		String classPathStr = definition.getClassPath();
		if (classPathStr == null) {
			classLoader = Extension.class.getClassLoader();
		} else {
			Path classPath = new Path( antProject, classPathStr );
			classLoader = new AntClassLoader(
				Extension.class.getClassLoader(),
    			antProject,  
				classPath,
				true);
		}
		String className = definition.getClassName();
		Class extensionClass = classLoader.loadClass( className );
		Extension extension = (Extension) extensionClass.newInstance();
		extension.init( definition, antProject, manager );
		return extension;
	}
	*/

	/**
	 * Instantiates the specified exception.
	 * At least one of the given definition of setting parameters must not be null.
	 * 
	 * @param typeDefinition the definition of the type, can be null
	 * @param definition the definition taken from extensions.xml or custom-extensions.xml
	 * @param setting the configuration taken from build.xml
	 * @param antProject the Ant project
	 * @param manager the extension manager
	 * @param environment the environment settings
	 * @return the configured extension.
	 * @throws ClassNotFoundException when the class was not found 
	 * @throws InstantiationException when the class could not get instantiated
	 * @throws IllegalAccessException when the class could not be accessed
	 * @throws IllegalArgumentException when both definition and setting are null or when no class has been defined anywhere.
	 */
	public static Extension getInstance(ExtensionTypeDefinition typeDefinition, ExtensionDefinition definition, ExtensionSetting setting, Project antProject, ExtensionManager manager, Environment environment ) 
	throws ClassNotFoundException, InstantiationException, IllegalAccessException 
	{
		String extensionType;
		if (typeDefinition != null) {
			extensionType = typeDefinition.getName() + "-extension";
		} else {
			extensionType = "extension";
		}
		if (definition == null && setting == null) {
			throw new IllegalArgumentException("Cannot instantiate " + extensionType + " without definition and without configuration-setting!");
		}
		String className = getClassName( typeDefinition, definition, setting );
		String name = getName( definition, setting );
		if (className == null) {
			throw new IllegalArgumentException("Unable to instantiate " + extensionType + " [" + name + "]: no class has been defined in neither extensions.xml, custom-extensions.xml or the build.xml.");
		}
		if ( name == null ) {
			name = className;
		}
		Path classPath = getClassPath( antProject, typeDefinition, definition, setting, environment );
		//System.out.println("Extension [" + className + "]: using classPath [" + classPath + "]");
		ClassLoader classLoader;
		if (setting != null) {
			classLoader = setting.getClass().getClassLoader();
		} else {
			classLoader = definition.getClass().getClassLoader();
		}
		if (classPath != null) {
//			System.out.println("using classpath=" + classPath );
			classLoader = new AntClassLoader(
	    			classLoader,
	    			antProject,  
					classPath,
					false);
		}
		try {
			Class extensionClass = classLoader.loadClass( className );		
			Extension extension = (Extension) extensionClass.newInstance();
			extension.init( typeDefinition, definition, setting, antProject, manager, environment );
			if (setting != null && setting.hasParameters()) {
				//System.out.println("Extension [" + className + "]: setting [" + setting.getParameters().length + "] parameters");
				ReflectionUtil.populate( extension, setting.getAllParameters( environment ), antProject.getBaseDir() );
			//} else {
			//	System.out.println("Extension [" + className + "]: setting no parameters - setting == null: " + (setting == null) );
			}
			return extension;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new BuildException( "Unable to load " 
					+ ((typeDefinition != null) ? typeDefinition.getName() : ((setting != null) ? setting.getName() : "extension"))
					+ " with class " + className
					+ " and classpath " + classPath
					+ ": " + e.toString() );
					
		}
	}

	/**
	 * @param definition
	 * @param setting
	 * @return
	 */
	private static String getName(ExtensionDefinition definition, ExtensionSetting setting) {
		String name = null;
		if (setting != null) {
			name = setting.getName();
		}
		if ( name == null && definition != null) {
			name = definition.getName();
		}
		return name;
	}

	/**
	 * @param typeDefinition
	 * @param definition
	 * @param setting
	 * @param environment
	 * @return
	 */
	private static Path getClassPath( Project antProject, ExtensionTypeDefinition typeDefinition, ExtensionDefinition definition, ExtensionSetting setting, Environment environment) {
		Path classPath = null;
		if (setting != null) {
			classPath = setting.getClassPath();
		}
		if (classPath == null && definition != null && definition.getClassPath() != null ) {
			//System.out.println("definition-classPath=" + definition.getClassPath());
			//System.out.println("polish.home=" + environment.getVariable("polish.home"));
			classPath = new Path( antProject, environment.writeProperties( definition.getClassPath() ) );
		}
		if (classPath == null && typeDefinition != null && typeDefinition.getDefaultClassPath() != null ) {
			classPath = new Path( antProject, environment.writeProperties( typeDefinition.getDefaultClassPath() ) );
		}
		if (classPath == null) {
			classPath = new Path( antProject );
		}
		// add enough-j2mepolish-extensions.jar to the path:
		String extensionsPath = environment.writeProperties("${polish.home}/lib/enough-j2mepolish-extensions.jar:${polish.home}/bin/extensions:${polish.home}/build/extensionsclasses");
		classPath.add( new Path( antProject, extensionsPath ));
		/*
		if (classPath != null) {
			System.out.println("Using classpath [" + classPath.toString() + "]");
		} else {
			System.out.println("Unable to resolve classpath...");
		}
		*/
		return classPath;
	}

	/**
	 * @param typeDefinition
	 * @param definition
	 * @param setting
	 * @return
	 */
	private static String getClassName(ExtensionTypeDefinition typeDefinition, ExtensionDefinition definition, ExtensionSetting setting) {
		String className = null;
		if (setting != null) {
			className = setting.getClassName();
		}
		if (className == null && definition != null) {
			className = definition.getClassName();
		}
		if (className == null && typeDefinition != null) {
			className = typeDefinition.getDefaultClassName();
		}
		return className;
	}
	/**
	 * Executes an Ant target.
	 * 
	 * @param targetName 
	 * @param antPropertiesList
	 */
	public void executeAntTarget( String targetName, List antPropertiesList ) {
		if (antPropertiesList == null) {
			executeAntTarget( targetName, (Variable[]) null );
		}
		Variable[] antProperties = (Variable[]) antPropertiesList.toArray( new Variable[ antPropertiesList.size() ] );
		executeAntTarget( targetName, antProperties );
	}

	/**
	 * Executes an Ant target.
	 * 
	 * @param targetName 
	 * @param antProperties
	 */
	public void executeAntTarget( String targetName, Variable[] antProperties ) {
		CallTarget target = new CallTarget();
		// setting up a new ant project:
		target.setProject( this.antProject );
		target.setTarget( targetName );
		// setting device properties:
		Map symbols = this.environment.getSymbols();
		for (Iterator iter = symbols.keySet().iterator(); iter.hasNext();) {
			String symbol = (String) iter.next();
			Property antProperty = target.createParam();
			antProperty.setName( symbol );
			antProperty.setValue( "true" );			
		}
		Map variables = this.environment.getVariables(); 
		for (Iterator iter = variables.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String value = (String) variables.get( name );
			if ( value.indexOf("${") != -1 ) {
				value = this.environment.writeProperties( value );
			}			
			Property antProperty = target.createParam();
			antProperty.setName( name );
			antProperty.setValue( value );			
		}
		
		// setting user defined properties:
		if (antProperties != null) {
			BooleanEvaluator evaluator = this.environment.getBooleanEvaluator();
			for (int i = 0; i < antProperties.length; i++) {
				Variable property = antProperties[i];
				if (property.isConditionFulfilled(evaluator, this.antProject.getProperties() )) {
					String value = property.getValue();
					if (value == null) {
						continue;
					}
					if ( value.indexOf("${") != -1 ) {
						value = this.environment.writeProperties( value );
					}
					//System.out.println("adding user defined property [" + property.getName() + "] = " + value );
					Property antProperty = target.createParam();
					antProperty.setName( property.getName() );
					antProperty.setValue( value );
				}
			}
		}
		//target.init(); (is initialized automatically when the first param is created)
		target.execute();
	}
	
	public String getAutoStartCondition() {
		return this.autoStartCondition;
	}

	/**
	 * Checks whether the autostart condition for this extension is fulfilled.
	 * 
	 * @param env the environment
	 * @return true when this condition is fulfilled
	 */
	public boolean isConditionFulfilled(Environment env) {
		if (this.autoStartCondition == null) {
			return true;
		}
		return env.isConditionFulfilled( this.autoStartCondition );
	}

	/**
	 * @return the type of this extension
	 */
	public String getType() {
		return this.type;
	}

}
