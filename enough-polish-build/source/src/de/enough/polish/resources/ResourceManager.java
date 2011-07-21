/*
 * Created on 10-Sep-2004 at 16:35:12.
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
package de.enough.polish.resources;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.enough.polish.BuildException;
import org.apache.tools.ant.Project;

import de.enough.polish.BooleanEvaluator;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ExtensionManager;
import de.enough.polish.ant.build.LocaleSetting;
import de.enough.polish.ant.build.LocalizationSetting;
import de.enough.polish.ant.build.ResourceCopierSetting;
import de.enough.polish.ant.build.ResourceSetting;
import de.enough.polish.ant.build.RootSetting;
import de.enough.polish.ant.requirements.SizeMatcher;
import de.enough.polish.preprocess.Preprocessor;
import de.enough.polish.preprocess.css.CssReader;
import de.enough.polish.preprocess.css.StyleSheet;
import de.enough.polish.util.DirectoryFileFilter;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.StringUtil;

/**
 * <p>Is responsible for the assembling of resources like images and localization messages.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        10-Sep-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ResourceManager {
	private final static String[] DEFAULT_EXCLUDES = new String[]{ "polish.css", "*~", "*.bak", "Thumbs.db", ".DS_Store", "messages_*",".svn","CVS" };
	private final ResourceSetting resourceSetting;
	private final BooleanEvaluator booleanEvaluator;
	private final Map resourceDirsByDevice;
	private final ResourceFilter resourceFilter;
	private final LocalizationSetting localizationSetting;
	//private final ResourceCopier resourceCopier;
	private TranslationManager translationManager;
	private final Environment environment;
	private final ExtensionManager extensionManager;
	private boolean filterZeroLengthFiles;
	private RootSetting[] resourceDirectories;
	private FileFilter excludeDirFilter = new DirectoryFileFilter(false);
	private FileFilter includeDirFilter = new DirectoryFileFilter(true);
	
	private Map translationManagersByLocaleAndDevice;

	/**
	 * Creates a new resource manager.
	 * 
	 * @param setting the configuration
	 * @param manager the extension manager
	 * @param environment environment settings
	 */
	public ResourceManager(ResourceSetting setting,
				ExtensionManager manager,
				Environment environment ) 
	{
		super();
		this.filterZeroLengthFiles = setting.filterZeroLengthFiles();
		this.resourceSetting = setting;
		this.extensionManager = manager;
		this.environment = environment;
		this.booleanEvaluator = environment.getBooleanEvaluator();
		this.resourceDirsByDevice = new HashMap();


		// get localization setting:
		this.localizationSetting = this.resourceSetting.getLocalizationSetting(this.environment);
		
		// creates resources-filter:
		this.resourceFilter = new ResourceFilter( setting.getExcludes(), DEFAULT_EXCLUDES, setting.useDefaultExcludes() );
		if (this.localizationSetting != null) {
			addLocaleExcludes( this.localizationSetting.getMessagesFileName() );
			addLocaleExcludes( this.localizationSetting.getExternalMessagesFileName() );
		}
	}
	
	/**
	 * @param messagesFileName
	 */
	private void addLocaleExcludes(String messagesFileName)
	{
		this.resourceFilter.addExclude( messagesFileName );
		// add filters for messages_de.txt etc! 
		int splitPos = messagesFileName.lastIndexOf('.');
		String start = null;
		String end = null;
		if (splitPos != -1) {
			start = messagesFileName.substring(0, splitPos) + "_";
			end = messagesFileName.substring(splitPos);
		}
		LocaleSetting[] locales = this.localizationSetting.getSupportedLocales( this.environment  );
		for (int i = 0; i < locales.length; i++) {
			Locale locale = locales[i].getLocale();
			if (splitPos != -1) {
				this.resourceFilter.addExclude( start + locale.toString() + end );
				//System.out.println("excluding [" + start + locale.toString() + end + "]");
			} else {
				this.resourceFilter.addExclude(messagesFileName + locale.toString() );
			}
			if (locale.getCountry().length() > 0) {
				// okay, this locale has also a country defined,
				// so we need to look at the language-resources as well:
				if (splitPos != -1) {
					this.resourceFilter.addExclude( start + locale.getLanguage() + end );
					//System.out.println("excluding [" + start + locale.getLanguage() + end + "]");
				} else {
					this.resourceFilter.addExclude(messagesFileName + locale.getLanguage() );
				}
			}

		}
	}

	/**
	 * Copies all resources to the specified target directory.
	 * 
	 * @param targetDir the target directory
	 * @param device the current device
	 * @param locale the current locale, can be null
	 * @throws IOException when a resource could not be copied
	 */
	public void copyResources( File targetDir, Device device, Locale locale ) 
	throws IOException 
	{
		// create target dir:
		if (!targetDir.exists()) {
			targetDir.mkdirs();
		}
		// add manual excludes from the user:
		this.resourceFilter.setAdditionalFilters( this.resourceSetting.getFilters(this.environment));
		File[] resources = getResources( device, locale );
		RootSetting[] includeSubDirsRoots = getIncludeSubDirsRoots();
		//FileUtil.copy(resources, targetDir);
		// creating resource copier:
		ResourceCopierSetting[] copierSettings = this.resourceSetting.getCopiers( this.environment );
		if (copierSettings == null || copierSettings.length == 0 ) {
			// use default resource copier:
			ResourceCopier resourceCopier = ResourceCopier.getInstance( null, this.extensionManager, this.environment );
			resourceCopier.copyResources(device, locale, resources, targetDir);
			for (int i = 0; i < includeSubDirsRoots.length; i++) {
				RootSetting rootSetting = includeSubDirsRoots[i];
				copyRootWithSubDirs( rootSetting, device, locale, rootSetting.resolveDir( this.environment ), resourceCopier, targetDir );
			}
		} else {
			// copy resources using user- or device-defined resource copiers:
			File[] subDirs = new File[ includeSubDirsRoots.length ];
			for (int i = 0; i < subDirs.length; i++) {
				subDirs[i] = includeSubDirsRoots[i].resolveDir( this.environment );
			}
			for (int i = 0; i < copierSettings.length; i++) {
				ResourceCopierSetting setting = copierSettings[i];
				ResourceCopier resourceCopier = ResourceCopier.getInstance( setting, this.extensionManager, this.environment );
				File tempTargetDir;
				boolean lastRound = i == copierSettings.length - 1; 
				if ( lastRound ) {
					tempTargetDir = targetDir;
				} else {
					tempTargetDir = new File( device.getBaseDir() + File.separatorChar + "res" + i );
					if ( tempTargetDir.exists() ) {
						FileUtil.delete( tempTargetDir );
					}
					tempTargetDir.mkdir();
				}
				resourceCopier.copyResources(device, locale, resources, tempTargetDir);
				for (int j = 0; j < subDirs.length; j++) {
					copyRootWithSubDirs( includeSubDirsRoots[j], device, locale, subDirs[j], resourceCopier, tempTargetDir );
				}
				if (!lastRound) {
					resources = tempTargetDir.listFiles( this.excludeDirFilter  );
					subDirs = tempTargetDir.listFiles( this.includeDirFilter );
					
				}
			}
		}
		if (this.localizationSetting != null && this.localizationSetting.isDynamic()) {
			saveDynamicTranslations( targetDir, device, false );
		}
	}
	
	/**
	 * @param destDir
	 * @param device
	 * @param locale
	 * @param env
	 * @throws IOException 
	 */
	public void copyDynamicTranslations(File destDir, Device device, Locale locale, Environment env) throws IOException
	{
		if (this.localizationSetting != null && this.localizationSetting.isDynamic()) {
			saveDynamicTranslations( destDir, device, true );
		}
	}
	
	private void copyRootWithSubDirs(RootSetting rootSetting, Device device, Locale locale, File root, ResourceCopier resourceCopier, File targetDir) throws IOException {
		File[] files = root.listFiles();
		if (files == null) {
			throw new BuildException("<root dir=\"" + root.getAbsolutePath() + "\"> does not exist - please check the <resources> section in your your build.xml script.");
		}
		if (rootSetting.isIncludeBaseDir()) {
			targetDir = new File( targetDir, root.getName() );
			targetDir.mkdir();
		}
		Map resourcesByName = new HashMap( files.length );
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (rootSetting.include( file.getName())) {
				if (file.isDirectory()) {
					File tmpTargetDir = new File( targetDir, file.getName() );
					tmpTargetDir.mkdir();
					copyRootWithSubDirs(rootSetting, device, locale, file, resourceCopier, tmpTargetDir );
				} else {
					resourcesByName.put( file.getName(), file );
				}
			}
		}
		files = filterResources(resourcesByName);
		resourceCopier.copyResources(device, locale, files, targetDir );
	}

	/**
	 * Retrieves all resource roots that should add their subdirectories to the JAR file
	 * 
	 * @return an (possibly empty) array of resource roots with enabled includeSubDirs option
	 */
	private RootSetting[] getIncludeSubDirsRoots() {
		ArrayList rootsList = new ArrayList( this.resourceDirectories.length );
		for (int i = 0; i < this.resourceDirectories.length; i++) {
			RootSetting rootSetting = this.resourceDirectories[i];
			if (rootSetting.isIncludeSubDirs()) {
				rootsList.add( rootSetting );
			}
		}
		return (RootSetting[]) rootsList.toArray( new RootSetting[ rootsList.size() ] );
	}

	/**
	 * Creates and stores the dynamic translations.
	 * 
	 * @param targetDir the target directory
	 * @param device the current device
	 * @param isForExternalUsage 
	 * @throws IOException when an error occurred
	 */
	private void saveDynamicTranslations(File targetDir, Device device, boolean isForExternalUsage ) 
	throws IOException 
	{
		LocaleSetting[] locales = this.localizationSetting.getSupportedLocales( this.environment );
		for (int i = 0; i < locales.length; i++) {
			LocaleSetting locale = locales[i];
			TranslationManager manager = getTranslationManager( device, locale );
			manager.saveTranslations( targetDir, device, locale, isForExternalUsage );
		}
	}

	/**
	 * Retrieves all files for the give device and locale.
	 * 
	 * @param device the current device
	 * @param locale the current locale - can be null
	 * @return an array with File-references to all resources which should be copied
	 *       into the application-jar
	 */
	public File[] getResources( Device device, Locale locale ) {
		Map resourcesByName = new HashMap();
		
		// first step: read all resource file-names:
		File[] resourceDirs = getResourceDirs( device, locale );
		for (int i = 0; i < resourceDirs.length; i++) {
			File dir = resourceDirs[i];
			File[] files = dir.listFiles();
			if (files == null) {
				throw new BuildException("Unable to add files from resource directory " + dir.getAbsolutePath() + ": exists=" + dir.exists() + ", is a directory=" + dir.isDirectory() );				
			}
			//System.out.println("adding " + files.length + " files from " + dir.getPath() );
			addFiles(files, resourcesByName);
		}
		// also load resources from the filesets:
		ResourcesFileSet[] fileSets = this.resourceSetting.getFileSets(this.environment );
		for (int i = 0; i < fileSets.length; i++) {
			ResourcesFileSet set = fileSets[i];
			File dir = set.getDir(null);
			if (!dir.exists()) {
				throw new BuildException("The referenced directory [" + dir.getAbsolutePath() + "] of <fileset> does point to a non-existing directory. Please correct the \"dir\"-attribute of the corresponding <fileset>-element.");
			}
			if (!dir.isDirectory()) {
				throw new BuildException("The referenced directory [" + dir.getAbsolutePath() + "] of <fileset> does point to a file which is not a directory. Please correct the \"dir\"-attribute of the corresponding <fileset>-element.");				
			}
			String[] fileNames = set.getDirectoryScanner( (Project)this.environment.get("ant.project")).getIncludedFiles();
			//String[] fileNames = FileUtil.filterDirectory(dir, null, true );
			//System.out.println("adding " + fileNames.length + " files from <fileset> " + dir.getPath() );
			addFiles( dir, fileNames, resourcesByName );
		}
	
		// second step:
		// filter the unwanted resources:
		return filterResources( resourcesByName );
	}

	/**
	 * Filters the given resources.
	 * 
	 * @param resourcesByName a map containing a file-reference for each found resource.
	 */
	private File[] filterResources(Map resourcesByName) {
		//System.out.println("filter " + resourcesByName.size() + " resources");
		String[] fileNames = (String[]) resourcesByName.keySet().toArray( new String[ resourcesByName.size()] );
		String[] filteredNames = this.resourceFilter.filter(fileNames);
		File[] filteredFiles = new File[ filteredNames.length ];
		for (int i = 0; i < filteredNames.length; i++) {
			String fileName = filteredNames[i];
			filteredFiles[i] = (File) resourcesByName.get( fileName );
		}
		return filteredFiles;
	}

	/**
	 * Gets all directories containing resources for the specified device.
	 * 
	 * @param device the device
	 * @param locale the current locale, can be null
	 * @return an array of file containing all found directories for the given device
	 */
	private File[] getResourceDirs(Device device, Locale locale) {
		String key = device.getIdentifier();
		if (locale != null) {
			key += locale.toString();
		}
		File[] directories = (File[]) this.resourceDirsByDevice.get( key );
		if (directories != null) {
			return directories;
		}
		if (this.resourceDirectories == null) {
			System.out.println("Error: resourceDirectories == null, resourceSetting=" + this.resourceSetting + ", resourceSetting.getRootDirs=" +  (this.resourceSetting == null ? "null" : this.resourceSetting.getRootDirectories(this.environment).toString()));
			this.resourceDirectories = this.resourceSetting.getRootDirectories(this.environment);
		}
		ArrayList dirs = new ArrayList();
		
		for (int j = 0; j < this.resourceDirectories.length; j++) {
			RootSetting rootSetting = this.resourceDirectories[j];
			if (rootSetting.isIncludeSubDirs()) {
				continue;
			}
			File resourcesDir = rootSetting.resolveDir( this.environment );
			if (!resourcesDir.exists()) {
				throw new BuildException( "resource dir " + resourcesDir.getAbsolutePath() + " does not exist.");
			}
			// first dir is the general resources dir:
			dirs.add( resourcesDir );
			//String resourcePath = resourcesDir.getAbsolutePath() + File.separator;
			// then the vendor specific resources:
			File resourceDir = new File( resourcesDir, device.getVendorName() );
			if (resourceDir.exists()  && resourceDir.isDirectory()) {
				dirs.add( resourceDir );
			}
			// now add all dynamic ScreenSize-directories:
			
			// now add all group resource-directories:
			String[] groups = device.getGroupNames();
			for (int i = 0; i < groups.length; i++) {
				String group = groups[i];
				resourceDir = new File( resourcesDir,  group );
				if (resourceDir.exists() && resourceDir.isDirectory()) {
					dirs.add( resourceDir );
				}
			}
			
			// now add any directories that use specific capabilities or contain preprocessing statements etc:
			File[] files = resourcesDir.listFiles();
			Arrays.sort( files );
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				if (file.isDirectory() && !dirs.contains(file)) {
					// The slash can be a protected character:
					String name = StringUtil.replace(file.getName(), "%2f", "/");
					//System.out.println("Considering dir "+ name);
					try {
						if (name.startsWith("ScreenSize.") 
								&& ( (name.indexOf('+') != -1) || (name.indexOf('-') != -1)) )  
						{
							String deviceSize = device.getCapability("polish.ScreenSize");
							if (deviceSize != null) {						
								String requirement = name.substring( "ScreenSize.".length() );
								SizeMatcher sizeMatcher = new SizeMatcher( requirement );
								if (sizeMatcher.matches(deviceSize)) {
									dirs.add( file );
								}
							}
						} 
						else if (name.startsWith("CanvasSize.")
								&& ( (name.indexOf('+') != -1) || (name.indexOf('-') != -1)) )  
						{
							String deviceSize = device.getCapability("polish.CanvasSize");
							if (deviceSize != null) {						
								String requirement = name.substring( "CanvasSize.".length() );
								SizeMatcher sizeMatcher = new SizeMatcher( requirement );
								if (sizeMatcher.matches(deviceSize)) {
									dirs.add( file );
								}
							}
						}
						else if (name.startsWith("FullCanvasSize.") 
								&& ( (name.indexOf('+') != -1) || (name.indexOf('-') != -1)) )  
						{
							String deviceSize = device.getCapability("polish.FullCanvasSize");
							if (deviceSize != null) {						
								String requirement = name.substring( "FullCanvasSize.".length() );
								SizeMatcher sizeMatcher = new SizeMatcher( requirement );
								if (sizeMatcher.matches(deviceSize)) {
									dirs.add( file );
								}
							}
						} else {
							if (!name.startsWith("polish.")) {
								name = "polish." + name;
							}
							if (device.hasFeature(name) 
								|| this.booleanEvaluator.evaluate(name, "resourceassembling", 0)
								|| (name.endsWith(".0x0")
									&& device.getCapability(name.substring(0, name.length() - ".0x0".length()) ) == null ) )
							{
								//System.out.println("Adding feature (" +  device.hasFeature(name) + ") or preprocessing dir (" + this.booleanEvaluator.evaluate(name, "resourceassembling", 0) + "): " + file.getAbsolutePath() );
								dirs.add( file );
							}
						}
					} catch (BuildException e) {
						System.out.println("WARNING: unable to add resource folder [" + file.getAbsolutePath() + "]: " + e.getMessage() );
					}
				}
			}
			
			// The last possible resource directory is the directory for the specific device:
			resourceDir = new File( resourcesDir, device.getVendorName() 
								+ File.separatorChar + device.getName() );
			if (resourceDir.exists() && resourceDir.isDirectory()) {
				dirs.add( resourceDir );
			}
			
			
		}
		if (locale != null) {
			// now add all locale-specific directories:
			ArrayList localizedDirs = new ArrayList( dirs.size() * 2 );
			String languageDirName = null;
			String localeDirName = locale.toString();
			if (locale.getCountry().length() > 0) {
				languageDirName = locale.getLanguage();
			}
			directories = (File[]) dirs.toArray( new File[ dirs.size() ]);
			for (int i = 0; i < directories.length; i++) {
				File resourceDir = directories[i];
				localizedDirs.add( resourceDir );
				//resourcePath = resourceDir.getAbsolutePath() + File.separator;
				if (languageDirName != null) {
					File localizedResourceDir = new File( resourceDir, languageDirName );
					if (localizedResourceDir.exists() && resourceDir.isDirectory()) {
						localizedDirs.add( localizedResourceDir );
					}
				}
				File localizedResourceDir = new File( resourceDir, localeDirName );
				if (localizedResourceDir.exists() && resourceDir.isDirectory()) {
					localizedDirs.add( localizedResourceDir );
				}				
			}
			dirs = localizedDirs;
		}
		directories = (File[]) dirs.toArray( new File[ dirs.size() ]);
		this.resourceDirsByDevice.put( key, directories );
		return directories;
	}

	/**
	 * Adds the found files to the given map.
	 * @param files the files
	 * @param resourcesByName the map.
	 */
	private void addFiles(File[] files, Map resourcesByName) {
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (!file.isDirectory()) {
				if ( this.filterZeroLengthFiles && file.length() == 0) {
					resourcesByName.remove( file.getName() );
				} else {
					resourcesByName.put( file.getName(), file );
				}
			}
		}
	}
	
	/**
	 * Adds the found files to the given map.
	 * @param fileNames the files-names
	 * @param resourcesByName the map.
	 */
	private void addFiles(File baseDir, String[] fileNames, Map resourcesByName) {
		String basePath = baseDir.getAbsolutePath() + File.separator;
		for (int i = 0; i < fileNames.length; i++) {
			File file = new File( basePath + fileNames[i] );
			if ( this.filterZeroLengthFiles  && file.length() == 0) {
				resourcesByName.remove( file.getName() );
			} else {
				resourcesByName.put( file.getName(), file );
			}
		}
	}
	
	/**
	 * Retrieves all supported locales.
	 * 
	 * @return an array of supported locales, can be null
	 */
	public LocaleSetting[] getLocales() {
		if (this.localizationSetting == null) {
			return null;
		} else {
			return this.localizationSetting.getSupportedLocales( this.environment );
		}
	}
	
	/**
	 * Retrieves the manager of translations.
	 * 
	 * @param device the current device
	 * @param locale the current locale (not null)
	 * @return the translation manager
	 * @throws IOException when a messages-files could not be read
	 */
	public TranslationManager getTranslationManager( Device device, LocaleSetting locale ) 
	throws IOException
	{
		if (this.translationManagersByLocaleAndDevice == null) {
			this.translationManagersByLocaleAndDevice = new HashMap();
		}
		String key = locale.toString() + device.getIdentifier(); 
		TranslationManager manager = (TranslationManager) this.translationManagersByLocaleAndDevice.get( key );
		if ( manager == null ) 
		{
			manager = 
				createTranslationManager( device, 
						locale, 
						this.environment, 
						getResourceDirs(device, locale.getLocale()), 
						this.localizationSetting );
			this.translationManagersByLocaleAndDevice.put( key, manager );
			this.translationManager = manager;
		}
		// resetting preprocessing variables:
		this.environment.addVariables( this.translationManager.getPreprocessingVariables() );
		return manager;
	}

	/**
	 * Creates an instance of a TranslationManager
	 * 
	 * @param device the current device
	 * @param locale the current locale (not null)
	 * @param env the environment settings
	 * @param resourceDirs the directories containing resources for this device and this locale
	 * @param setting the localization settings
	 * @return an instance of TranslationManager
	 * @throws IOException when some translation could not be loaded
	 */
	protected TranslationManager createTranslationManager(Device device, LocaleSetting locale, Environment env, File[] resourceDirs, LocalizationSetting setting) 
	throws IOException 
	{
		String className = this.localizationSetting.getTranslationManagerClassName();
		if (className != null) {
			try {
				Class managerClass = Class.forName( className );
				Constructor constructor = managerClass.getConstructor( new Class[]{  Device.class, LocaleSetting.class, Environment.class, File[].class, LocalizationSetting.class} );
				TranslationManager manager = (TranslationManager) constructor.newInstance( new Object[]{ 
						device, 
						locale, 
						env, 
						getResourceDirs(device, locale.getLocale()),
						setting} );
				return manager;
			} catch (Exception e) {
				//just return a normal translation manager
				e.printStackTrace();
				throw new BuildException("The translation manager [" + className + "] could not be found, please adjust your <localization>-translationManager-setting.");
			}
		} else {
			return new TranslationManager( 
				device, 
				locale, 
				env, 
				getResourceDirs(device, locale.getLocale()), 
				this.localizationSetting );
		}
	}

	/**
	 * Reads the style sheet for the given device.
	 * 
	 * @param device the device
	 * @param locale the current locale, can be null
	 * @param preprocessor the preprocessor
	 * @param env the environment
	 * @return the style sheet for that device
	 * @throws IOException when a sub-style sheet could not be loaded.
	 */
	public StyleSheet loadStyleSheet( Device device, Locale locale, Preprocessor preprocessor, Environment env ) throws IOException {
	
		long lastCssModification = 0;		
		CssReader cssReader = new CssReader();
		boolean replaceWithoutDirective = preprocessor.replacePropertiesWithoutDirective();
		preprocessor.setReplacePropertiesWithoutDirective( true );
		// load default.css file first:
		File defaultCssFile = env.resolveFile("${polish.home}/default.css");
		if (defaultCssFile.exists()) {
			cssReader.add( defaultCssFile, preprocessor, env );			
		} else {
			InputStream in = getClass().getResourceAsStream("/default.css");
			if (in == null) {
				System.out.println("Warning: unable to load default.css from either ${polish.home} or ${polish.home}/lib/enough-j2mepolish-build.jar! You might need to define additional styles when using specific components.");
			} else {
				cssReader.add( in, "default.css", preprocessor, env );
			}
		}
		File[] resourceDirs = getResourceDirs(device, locale);
		for (int i = 0; i < resourceDirs.length; i++) {
			File dir = resourceDirs[i];
			File cssFile = new File( dir.getAbsolutePath() + File.separator + "polish.css");
			if (cssFile.exists()) {
				//System.out.println("loading CSS file [" + cssFile.getAbsolutePath() + "].");
				if (cssFile.lastModified() > lastCssModification) {
					lastCssModification = cssFile.lastModified();
				}
				cssReader.add( cssFile, preprocessor, env );
			}
		}
		StyleSheet sheet = cssReader.getStyleSheet();
		sheet.setLastModified(lastCssModification);
		preprocessor.setReplacePropertiesWithoutDirective( replaceWithoutDirective );
		return sheet;
	}

	/**
	 * @param env
	 */
	public void initialize(Environment env) {
		RootSetting[] resDirs = this.resourceSetting.getRootDirectories(env);
		if (resDirs == null || resDirs.length == 0) {
			throw new IllegalStateException("Found no root directories");
		}
		this.resourceDirectories = resDirs;
		for (int i = 0; i < resDirs.length; i++) {
			File resDir = resDirs[i].resolveDir(env);
			if (!resDir.exists()) {
				System.err.println("Warning: the resources-directory [" + resDir.getAbsolutePath() + "] did not exist, J2ME Polish created it automatically now.");
				resDir.mkdir();
			} else if (!resDir.isDirectory()) {
				throw new BuildException("The resources-directory [" + resDir.getAbsolutePath() + "] is not a directory. Please adjust either your \"resources\"-attribute of the <build>-element or the \"dir\"-attribute of the <resources>-element.");
			}
		}		
	}

	
}
