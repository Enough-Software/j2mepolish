/*
 * Created on 22-Apr-2005 at 18:54:42.
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.tools.ant.Project;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import de.enough.polish.exceptions.InvalidComponentException;
import de.enough.polish.util.ResourceUtil;
import de.enough.polish.util.StringUtil;


/**
 * <p>Manages the available extensions.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        22-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ExtensionManager {
	
	public static final String TYPE_PLUGIN = "plugin";
	public static final String TYPE_PROPERTY_FUNCTION = "propertyfunction";
	public static final String TYPE_LIBRARYPROCESSOR = "libraryprocessor";
	public static final String TYPE_PREPROCESSOR = "preprocessor";
	public static final String TYPE_PRECOMPILER = "precompiler";
	public static final String TYPE_POSTCOMPILER = "postcompiler";
	public static final String TYPE_OBFUSCATOR = "obfuscator";
	public static final String TYPE_POSTOBFUSCATOR = "postobfuscator";
	public static final String TYPE_PREVERIFIER = "preverifier";
	public static final String TYPE_RESOURCE_COPIER = "resourcecopier";
	public static final String TYPE_PACKAGER = "packager";
	public static final String TYPE_FINALIZER = "finalizer";
	public static final String TYPE_LOG_HANDLER = "loghandler";
	public static final String TYPE_MANIFEST_CREATOR = "manifestcreator";
	public static final String TYPE_DESCRIPTOR_CREATOR =  "descriptorcreator";
	public static final String TYPE_DEBUGGER = "debugger";
	
	
	private final Map definitionsByType;
	private final Map extensionsByType;
	private final Map typesByName;
	private final List instantiatedExtensions;
	private final List instantiatedPlugins;
	private final Project antProject;
	
	private Extension[] activeExtensions;
	private final List autoStartExtensions;
    private Extension[] lifeCycleExtensions;

    /**
     * @param antProject
     * @throws IOException
     * @throws JDOMException
     * 
     */
    public ExtensionManager( Project antProject ) throws JDOMException, IOException {
        super();
        this.antProject = antProject;
        this.definitionsByType = new HashMap();
        this.extensionsByType = new HashMap();
        this.typesByName = new HashMap();
        this.instantiatedExtensions = new ArrayList();
        this.instantiatedPlugins = new ArrayList();
        this.autoStartExtensions = new ArrayList();
    }

    
	/**
	 * @param antProject
	 * @param is
	 * @throws IOException
	 * @throws JDOMException
	 * 
	 */
	public ExtensionManager( Project antProject, InputStream is ) throws JDOMException, IOException {
		super();
		this.antProject = antProject;
		this.definitionsByType = new HashMap();
		this.extensionsByType = new HashMap();
		this.typesByName = new HashMap();
		this.instantiatedExtensions = new ArrayList();
		this.instantiatedPlugins = new ArrayList();
		this.autoStartExtensions = new ArrayList();
		loadDefinitions( is );
		is.close();
	}
	
	/**
	 * Loads custom extensions, when there are any.
	 * 
	 * @param customExtensionsFile the file that contains custom extensions
	 * @throws JDOMException
	 * @throws InvalidComponentException
	 */
	public void loadCustomDefinitions(File customExtensionsFile ) 
	throws JDOMException, InvalidComponentException {
		if (customExtensionsFile != null && customExtensionsFile.exists()) {
			try {
				loadDefinitions( new FileInputStream( customExtensionsFile ) );
			} catch (FileNotFoundException e) {
				// this shouldn't happen
				System.err.println("Unable to load [custom-extensions.xml]: " + e.toString() );
				e.printStackTrace();
			} catch (IOException e) {
				// this also shouldn't happen
				System.err.println("Unable to load [custom-extensions.xml]: " + e.toString() );
				e.printStackTrace();
			}
		}
	}


	/**
	 * Loads the definitions from the given input stream.
	 * 
	 * @param is the input stream, usually from extensions.xml or custom-extensions.xml
	 * @throws JDOMException when the XML is not wellformed
	 * @throws IOException when the input stream could not be read
	 */
	private void loadDefinitions( InputStream is ) 
	throws JDOMException, IOException 
	{
		SAXBuilder builder = new SAXBuilder( false );
		Document document = builder.build( is );
		// load type-definitions:
		List xmlList = document.getRootElement().getChildren("typedefinition");
		for (Iterator iter = xmlList.iterator(); iter.hasNext();) {
			Element element = (Element) iter.next();
			ExtensionTypeDefinition type = new ExtensionTypeDefinition( element );
			this.typesByName.put( type.getName(), type );
		}
		
		// load the actual extension-definitions:
		xmlList = document.getRootElement().getChildren("extension");
		for (Iterator iter = xmlList.iterator(); iter.hasNext();) {
			Element element = (Element) iter.next();
			try {
				ExtensionDefinition definition = new ExtensionDefinition( element, this.antProject, this );
				Map store = (Map) this.definitionsByType.get( definition.getType() );
				if ( store == null ) {
					store = new HashMap();
					this.definitionsByType.put( definition.getType(), store );
				}
				store.put( definition.getName(), definition );
				if ( definition.getAutoStartCondition() != null ) {
					this.autoStartExtensions.add( definition );
				}
			} catch (Exception e) {
				System.out.println("Unable to load extension [" + element.getChildTextTrim("class") + "]: " + e.toString() );
			}
		}
	}
	
	/**
	/**
	 * Retrieves an extension.
	 * 
	 * @param type the type of the extension, e.g. "propertyfunction"
	 * @param setting the configuration of the extension, taken from the build.xml
	 * @return the extension, null when the type or the name is not known
	 * @param environment the environment settings
	 * @throws IllegalAccessException when the extension could not be accesssed
	 * @throws InstantiationException when the extension could not be loaded
	 * @throws ClassNotFoundException when the extension was not found or when the extension class was not found in the classpath
	 */
	public Extension getExtension(String type, ExtensionSetting setting, Environment environment) 
	throws ClassNotFoundException, InstantiationException, IllegalAccessException 
	{
		String name = setting.getName();
		if (name == null && setting.getClassName() == null) {
//			name = setting.getClassName();
//			if (name == null) {
				throw new IllegalArgumentException("Unable to load extension without name or class-setting. Please check your build.xml file.");
//			}
		}
		return getExtension(type, name, setting, environment);
	}
	
	/**
	 * Retrieves an extension.
	 * 
	 * @param type the type of the extension, e.g. "propertyfunction"
	 * @param name the name of the extensio, e.g. "uppercase"
	 * @param environment the environment settings
	 * @return the extension, null when the type or the name is not known
	 * @throws IllegalAccessException when the extension could not be accesssed
	 * @throws InstantiationException when the extension could not be loaded
	 * @throws ClassNotFoundException when the extension was not found or when the extension class was not found in the classpath
	 */
	public Extension getExtension( String type, String name, Environment environment ) 
	throws ClassNotFoundException, InstantiationException, IllegalAccessException 
	{
		return getExtension( type, name, null, environment );
	}
	
	/**
	 * Retrieves an extension without storing it.
	 * 
	 * @param type the type of the extension, e.g. "propertyfunction"
	 * @param name the name of the extensio, e.g. "uppercase"
	 * @param environment the environment settings
	 * @return the extension, null when the type or the name is not known
	 * @throws IllegalAccessException when the extension could not be accesssed
	 * @throws InstantiationException when the extension could not be loaded
	 * @throws ClassNotFoundException when the extension was not found or when the extension class was not found in the classpath
	 */
	public Extension getTemporaryExtension(String type, String name, Environment environment )
	throws ClassNotFoundException, InstantiationException, IllegalAccessException 
	{
		return getTemporaryExtension(type, name, null, environment );
	}

	
	/**
	 * Retrieves and stores an extension.
	 * 
	 * @param type the type of the extension, e.g. "propertyfunction"
	 * @param name the name of the extensio, e.g. "uppercase", can be null when the setting is not null
	 * @param setting the configuration of the extension, taken from the build.xml, can be null when the name is not null
	 * @param environment the environment settings
	 * @return the extension, null when the type or the name is not known
	 * @throws IllegalArgumentException when both name and setting are null
	 * @throws IllegalAccessException when the extension could not be accesssed
	 * @throws InstantiationException when the extension could not be loaded
	 * @throws ClassNotFoundException when the extension was not found or when the extension class was not found in the classpath
	 */
	public Extension getExtension( String type, String name, ExtensionSetting setting, Environment environment ) 
	throws ClassNotFoundException, InstantiationException, IllegalAccessException 
	{
		if (name == null && setting == null) {
			throw new IllegalArgumentException("Unable to load extension of type [" + type + "] without specifying a name nor a setting.");
		}
		return getExtension(type, name, setting, environment, true);
	}
	
	/**
	 * Retrieves an extension without storing it.
	 * 
	 * @param type the type of the extension, e.g. "propertyfunction"
	 * @param setting the configuration of the extension, taken from the build.xml
	 * @param environment the environment settings
	 * @return the extension, null when the type or the name is not known
	 * @throws IllegalAccessException when the extension could not be accesssed
	 * @throws InstantiationException when the extension could not be loaded
	 * @throws ClassNotFoundException when the extension was not found or when the extension class was not found in the classpath
	 */
	public Extension getTemporaryExtension(String type, ExtensionSetting setting, Environment environment )
	throws ClassNotFoundException, InstantiationException, IllegalAccessException 
	{
		if (setting == null) {
			return null;
		} else {
			return getExtension(type, setting.getName(), setting, environment, false);
		}
	}
	
	/**
	 * Retrieves an extension without storing it.
	 * 
	 * @param type the type of the extension, e.g. "propertyfunction"
	 * @param name the name of the extensio, e.g. "uppercase"
	 * @param setting the configuration of the extension, taken from the build.xml
	 * @param environment the environment settings
	 * @return the extension, null when the type or the name is not known
	 * @throws IllegalAccessException when the extension could not be accesssed
	 * @throws InstantiationException when the extension could not be loaded
	 * @throws ClassNotFoundException when the extension was not found or when the extension class was not found in the classpath
	 */
	public Extension getTemporaryExtension(String type, String name, ExtensionSetting setting, Environment environment )
	throws ClassNotFoundException, InstantiationException, IllegalAccessException 
	{
		return getExtension(type, name, setting, environment, false);
	}
	
	/**
	 * Retrieves an extension.
	 * 
	 * @param type the type of the extension, e.g. "propertyfunction"
	 * @param name the name of the extensio, e.g. "uppercase"
	 * @param setting the configuration of the extension, taken from the build.xml
	 * @param environment the environment settings
	 * @param storeExtension true when the extension should be stored for other devices as well
	 * @return the extension, null when the type or the name is not known
	 * @throws IllegalAccessException when the extension could not be accesssed
	 * @throws InstantiationException when the extension could not be loaded
	 * @throws ClassNotFoundException when the extension was not found or when the extension class was not found in the classpath
	 * @throws IllegalArgumentException when the type is null, or when both name and setting are null
	 */
	public Extension getExtension( String type, String name, ExtensionSetting setting, Environment environment, boolean storeExtension ) 
	throws ClassNotFoundException, InstantiationException, IllegalAccessException 
	{
		if (type == null) {
			throw new IllegalArgumentException("Unable to load extension without specifying a type.");
		}
		if (name == null && setting == null) {
			throw new IllegalArgumentException("Unable to load extension of type [" + type + "] without specifying neither name nor setting.");
		}
		Map store = (Map) this.extensionsByType.get( type );
		if (store != null) {
			Extension extension;
			if (setting != null) {
				extension = (Extension) store.get( setting );
			} else {
				extension = (Extension) store.get( name );
			}
			if (extension != null) {
				return extension;
			}
		}
		// this extension has not been instantiated so far,
		// so do it now:
		ExtensionDefinition definition = null;
		if ( name != null) {
			definition = getDefinition( type, name );
		}
		ExtensionTypeDefinition typeDefinition = getTypeDefinition( type );
		Extension extension = Extension.getInstance( typeDefinition, definition, setting, this.antProject, this, environment );
		if (store == null) {
			store = new HashMap();
			this.extensionsByType.put( type, store );
		}
		if (setting != null) {
			store.put( name, extension );
		} else {
			store.put( setting, extension );
		}
		if (storeExtension) {
			//System.out.println("Storing  " + type + " " + name  );
			this.instantiatedExtensions.add( extension );
		}
		return extension;
	}
	
	public void executeTemporaryExtension(String type, String name, Environment environment) {
		executeTemporaryExtension(type, name, null, environment);		
	}	

	public void executeTemporaryExtension(String type, ExtensionSetting setting, Environment environment) {
		if (setting == null) {
			return;
		}
		executeTemporaryExtension(type, setting.getName(), setting, environment);
		
	}	
	
	public void executeTemporaryExtension(String type, String name, ExtensionSetting setting, Environment environment) {
		if (name == null ) {
			if (setting != null) {
				name = setting.getName();
			} else {
				return;
			}
		}
		try {
			Extension extension = getTemporaryExtension(type, name, setting, environment);
			extension.execute( environment.getDevice(), environment.getLocale(), environment );
		} catch (BuildException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException( "Unable to execute extension [" + name + "]: " + e.toString()  );
		}
	}

	/**
   * Retrieves the type definition of the specified extension type.
   * 
	 * @param type the name of the type
	 * @return the type definition
	 */
	public ExtensionTypeDefinition getTypeDefinition(String type) {
		return (ExtensionTypeDefinition) this.typesByName.get( type );
	}

	/**
	 * Retrieves the defnition of the specified extension
	 * 
	 * @param type the type of the extension, e.g. "propertyfunction"
	 * @param name the name of the extensio, e.g. "uppercase"
	 * @return the definition of the extension
	 * @throws IllegalArgumentException when the either the type is not known or there is no extension with that name registered
	 */
	public ExtensionDefinition getDefinition( String type, String name ) {
		Map store = (Map) this.definitionsByType.get( type );
		if ( store == null ) {
			throw new IllegalArgumentException("The extension-type [" + type + "] for the extension [" + name + "] is not known.");
		} else {
			ExtensionDefinition definition = (ExtensionDefinition) store.get( name );
			if (definition == null) {
				throw new IllegalArgumentException("The extension [" + name + "] of the extension-type [" + type + "] is not registered, please check your custom-extensions.xml file.");
			}
			return definition;
		}
	}
	
	public void registerExtension( String type, Extension extension ) {
		Map store = (Map) this.extensionsByType.get( type );
		if (store == null) {
			store = new HashMap();
			this.extensionsByType.put( type, store );
		}
		store.put( extension.toString(), extension );
		this.instantiatedExtensions.add( extension );
	}
	
	
	public void preInitialize( Device device, Locale locale ) {
		// call preInitialize on the registered plugins:
	}
	
	public void initialize( Device device, Locale locale, Environment environment ) {
		// find out active extensions:
		BooleanEvaluator evaluator = environment.getBooleanEvaluator();
		ArrayList activeList = new ArrayList();
		for (Iterator iter = this.instantiatedExtensions.iterator(); iter.hasNext();) {
			Extension extension = (Extension) iter.next();
			ExtensionSetting setting = extension.getExtensionSetting();
			if ( setting == null || setting.isActive(evaluator, this.antProject) ) {
				activeList.add( extension );
				// call initialize on all active extensions:
				extension.initialize(device, locale, environment);
			}
		}
		// initialize auto-start extensions:
		Extension[] autoExtensions = getAutoStartExtensions(null, device, locale, environment);
		for (int i = 0; i < autoExtensions.length; i++) {
			Extension extension = autoExtensions[i];
			extension.initialize(device, locale, environment);
			activeList.add( extension );
		}
		this.activeExtensions = (Extension[]) activeList.toArray( new Extension[ activeList.size() ] );
		// initialize device specific extensions:
		initialize( TYPE_PRECOMPILER, device.getCapability( "polish.build.PreCompiler"), device, locale, environment );
		initialize( TYPE_POSTCOMPILER, device.getCapability( "polish.build.PostCompiler"), device, locale, environment );
		initialize( TYPE_PREVERIFIER, device.getCapability( "polish.build.Preverifier"), device, locale, environment );
		initialize( TYPE_FINALIZER, device.getCapability( "polish.build.Finalizer"), device, locale, environment );
	}
	
	private void initialize( String type, String extensions, Device device, Locale locale, Environment environment ) {
		if (extensions != null) {
			String[] extensionNames = StringUtil.splitAndTrim(extensions, ',');
			for (int i = 0; i < extensionNames.length; i++) {
				String extensionName = extensionNames[i];
				//System.out.println("Initializing device specific extension [" + extensionName + "] of type [" + type + "]" );
				try {
					Extension extension = getTemporaryExtension( type, extensionName, environment );
					extension.initialize(device, locale, environment );
				} catch ( Exception e ) {
					e.printStackTrace();
					BuildException be = new BuildException("Unable to initialize extension " + extensionName);
          be.initCause(e);
          throw be;
				}
			}
		}
	}

	public void postInitialize( Device device, Locale locale, Environment environment ) {
		// call postInitialize on the registered plugins:
	}
	
	public void preprocess( Device device, Locale locale, Environment environment ) {
		// in the preprocessing step, registered preprocessors are called
		// by the standard preprocessor itself...
		//executeExtensions( TYPE_PREPROCESSOR, device, locale, environment );
	}

	public void preCompile( Device device, Locale locale, Environment environment ) {
		executeExtensions( TYPE_PRECOMPILER, device, locale, environment );
	}

	public void postCompile( Device device, Locale locale, Environment environment ) {
		executeExtensions( TYPE_POSTCOMPILER, device, locale, environment );
	}
	
	public void postObfuscate( Device device, Locale locale, Environment environment ) {
		executeExtensions( TYPE_POSTOBFUSCATOR, device, locale, environment );
	}
	
	public void executeExtensions( String type, Device device, Locale locale, Environment environment ) {
		Extension[] extensions = getAutoStartExtensions( type, device, locale, environment );
		for (int i = 0; i < extensions.length; i++) {
			Extension extension = extensions[i];
			extension.execute( device, locale, environment );
		}
    String tmpBuildExtensionNames = environment.getVariable("polish.build." + type);
    if (tmpBuildExtensionNames != null)
    {
      String[] buildExtensionNames = StringUtil.splitAndTrim(tmpBuildExtensionNames, ',');
      if (buildExtensionNames != null ) {
        try
        {
          for (int i = 0; i < buildExtensionNames.length; i++)
          {
            Extension extension = getExtension(type, buildExtensionNames[i], environment);
            extension.execute(device, locale, environment);
          }
        }
        catch (BuildException e)  
        {
          throw e;
        }
        catch (Exception e)
        {
          e.printStackTrace();
          throw new BuildException( e.toString(), e );
        }
      }
    }
	}
	
	/**
	 * @param type
	 * @param device
	 * @param locale
	 * @param environment
	 * @return an array of extensions that should be started automatically
	 */
	public Extension[] getAutoStartExtensions(String type, Device device, Locale locale, Environment environment) {
		ArrayList list = new ArrayList();
		for (Iterator iter = this.autoStartExtensions.iterator(); iter.hasNext();) {
			ExtensionDefinition definition = (ExtensionDefinition) iter.next();
			if ( (type == null || type.equals( definition.getType())) && definition.isConditionFulfilled( environment )) {
				
				try {
					list.add( getTemporaryExtension( type != null ? type : definition.getType(), definition.getName(), environment ) );
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return (Extension[]) list.toArray( new Extension[ list.size() ] );
	}

	public void preFinalize( Device device, Locale locale, Environment environment ) {
		// call preInitialize on the registered plugins:
	}
	
	public void finalize( Device device, Locale locale, Environment environment ) {
		executeExtensions( TYPE_FINALIZER, device, locale, environment );
		// call finalize on all active extensions:
		for (int i = 0; i < this.activeExtensions.length; i++) {
			Extension extension = this.activeExtensions[i];
			extension.finalize(device, locale, environment);
		}
	}
	
	public void postFinalize( Device device, Locale locale, Environment environment ) {
        if (this.lifeCycleExtensions != null) {
            for (int i = 0; i < this.lifeCycleExtensions.length; i++) {
                Extension extension = this.lifeCycleExtensions[i];
                extension.execute(device, locale, environment );
            }
        }        
	}
	
	
	/**
	 * Notifies the extensions about the start of the build process.
	 * 
	 * @param env the environment without device specific settings
	 */
	public void notifyBuildStart( Environment env ) {
        if (this.activeExtensions != null) {
    		for (int i = 0; i < this.activeExtensions.length; i++) {
    			Extension extension = this.activeExtensions[i];
    			extension.notifyBuildStart(env);
    		}
        }
        if (this.lifeCycleExtensions != null) {
            for (int i = 0; i < this.lifeCycleExtensions.length; i++) {
                Extension extension = this.lifeCycleExtensions[i];
                extension.notifyBuildStart(env);
            }
        }
	}
	
	/**
	 * Notifies the extensions about the end of the build process.
	 * 
	 * @param env the environment without device specific settings
	 */
	public void notifyBuildEnd( Environment env ) {
        if (this.activeExtensions != null) {
            for (int i = 0; i < this.activeExtensions.length; i++) {
                Extension extension = this.activeExtensions[i];
                extension.notifyBuildEnd(env);
            }
        }
        if (this.lifeCycleExtensions != null) {
            for (int i = 0; i < this.lifeCycleExtensions.length; i++) {
                Extension extension = this.lifeCycleExtensions[i];
                extension.notifyBuildEnd(env);
            }
        }
	}

	/**
	 * @param type the type of the extension
	 * @param environment the environment
	 * @return the extension
	 */
	public Extension getActiveExtension(String type, Environment environment) {
		Map store = (Map) this.extensionsByType.get( type );
		if (store == null) {
			return null;
		}
		BooleanEvaluator evaluator = environment.getBooleanEvaluator();
		Object[] extensions = store.values().toArray();
		for (int i = 0; i < extensions.length; i++) {
			Extension extension = (Extension) extensions[i];
			ExtensionSetting setting = extension.getExtensionSetting();
			if (setting == null || setting.isActive(evaluator, this.antProject)) {
				return extension;
			}
		}
		return null;
	}

	/**
	 * @param type
	 * @param settings
	 * @param environment
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
	public void registerExtensions(String type, ExtensionSetting[] settings, Environment environment) 
	throws ClassNotFoundException, InstantiationException, IllegalAccessException 
	{
		if (settings == null) {
			return;
		}
		for (int i = 0; i < settings.length; i++) {
			ExtensionSetting setting = settings[i];
			getExtension(type, setting, environment);
		}
	}

	public void removeExtension(Extension extension) {
		this.instantiatedExtensions.remove(extension);
	}


	/**
	 * @param polishHome
	 * @param resourceUtil
	 * @return the extension manager instance
	 * @throws IOException if an I/O error occured
	 * @throws InvalidComponentException 
	 */
	public static ExtensionManager getInstance(File polishHome, ResourceUtil resourceUtil) 
	throws IOException, InvalidComponentException 
	{	
		try {
		InputStream in = resourceUtil.open( polishHome, "extensions.xml" );
		ExtensionManager manager = new ExtensionManager( null, in );
		if (polishHome != null) {
			manager.loadCustomDefinitions( new File( polishHome, "custom-extensions.xml"));
		}
		return manager;
		} catch (JDOMException e) {
			e.printStackTrace();
			throw new InvalidComponentException("Unable to read custom-extenions.xml: " + e );
		}
	}


    /**
     * @param lifeCycleExtensions
     */
    public void setLifeCycleExtensions(Extension[] lifeCycleExtensions) {
        this.lifeCycleExtensions = lifeCycleExtensions;
    }

}
