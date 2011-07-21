/*
 * Created on 22-Jan-2003 at 14:10:02.
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
package de.enough.polish.ant.build;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.enough.polish.BuildException;
import org.apache.tools.ant.Project;

import de.enough.polish.Attribute;
import de.enough.polish.BooleanEvaluator;
import de.enough.polish.Environment;
import de.enough.polish.ExtensionManager;
import de.enough.polish.finalize.Finalizer;
import de.enough.polish.theme.FileSetting;
import de.enough.polish.theme.SerializeSetting;
import de.enough.polish.util.CastUtil;
import de.enough.polish.util.ResourceUtil;
import de.enough.polish.util.StringUtil;

/**
 * <p>Represents the build settings of a polish J2ME project.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        22-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class BuildSetting {
	
	public static final String IMG_LOAD_BACKGROUND = "images.backgroundLoad";
	public static final String IMG_LOAD_FOREGROUND = "images.directLoad";
	public static final String TARGET_1_1 = "1.1";
	public static final String TARGET_1_2 = "1.2";
	
	private static final String DEFAULT_JAD_FILTER_PATTERN = "MIDlet-Name, MIDlet-Version, MIDlet-Vendor, MIDlet-Jar-URL, MIDlet-Jar-Size, MIDlet-Description?, MIDlet-Icon?, MIDlet-Info-URL?, MIDlet-Data-Size?, MIDlet-*, *";
	private static final String DEFAULT_MANIFEST_FILTER_PATTERN = "Manifest-Version, Main-Class?, MIDlet-Name, MIDlet-Version, MIDlet-Vendor, MIDlet-Description?, MIDlet-Icon?, MIDlet-Info-URL?, MIDlet-Data-Size?, MIDlet-*, *";
	
	private static final String DEFAULT_ENCODING = "UTF8";
	
	private final AttributesFilter defaultJadFilter;
	private final AttributesFilter defaultManifestFilter;

	
	private Environment environment;
	private MidletSetting midletSetting; 
	private ArrayList obfuscatorSettings;
	private boolean doObfuscate;
	private File workDir;
	private File apiDir;
	private String destPath;
	private File resDir;
	private String symbols;
	private String imageLoadStrategy;
	private FullScreenSetting fullScreenSetting;
	private File devices;
	private File vendors;
	private File groups;
	private File apis;
	private File extensions;
	private File customDevices;
	private File customVendors;
	private File customGroups;
	private File customApis;
	private File customConfigurations;
	private File customPlatforms;
	private File customExtensions;
	private Variables variables;
	private boolean usePolishGui;
	private File preverify;
	private final Project antProject;
	private final BooleanEvaluator antPropertiesEvaluator;
	private boolean includeAntProperties;
	private ResourceUtil resourceUtil;
	private final ArrayList sourceSettings;
	private File polishDir;
	private JadAttributes jadAttributes;
	private ArrayList preprocessors;
	private ArrayList jadAttributesFilters;
	private ArrayList manifestAttributesFilters;
	private LibrariesSetting binaryLibraries;
	private String polishHomePath;
	private String projectBasePath;
	private ArrayList javaTasks;
	private String javacTarget;
	private boolean compilerMode;
	private boolean compilerModePreverify;
	private File compilerDestDir;
	private ResourceSetting resourceSetting;
	private boolean alwaysUsePolishGui;
	private ArrayList packageSettings;
	private ArrayList compilers;
	private String encoding = DEFAULT_ENCODING;
	private boolean doPreverify = true;
	private boolean doCompile = true;
	private ArrayList postCompilers;
	private ArrayList postObfuscators;
	private ArrayList finalizers;
	private File projectBaseDir;
	private File polishHomeDir;
	private ArrayList preverifiers;
	private File capabilities;
	private File platforms;
	private File configurations;
	private boolean replacePropertiesWithoutDirective;
	private boolean abortOnError = true;
	private String onError;
	private ArrayList preCompilers;
	private ClassSetting mainClassSetting;
	private ClassSetting dojaClassSetting;
	private ArrayList serializers;
	private FileSetting fileSetting;
	private ArrayList debugSettings;
	private SignSetting signSetting;
	
	/**
	 * Creates a new build setting.
	 * 
	 * @param antProject The corresponding ant-project.
	 * @param environment the environment
	 */
	public BuildSetting( Project antProject, Environment environment ) {
		this.polishHomePath = antProject.getProperty( "polish.home" );
		this.environment = environment;
		BooleanEvaluator antEvaluator = environment.getBooleanEvaluator();
		if (this.polishHomePath != null) {
			this.polishHomeDir = new File( this.polishHomePath );
			if (!this.polishHomeDir.isAbsolute()) {
				this.polishHomeDir = new File( antProject.getBaseDir(), this.polishHomePath );
				this.polishHomePath = this.polishHomeDir.getAbsolutePath();
			}
			this.polishHomePath += File.separatorChar;
		} 
		this.projectBasePath = antProject.getBaseDir().getAbsolutePath() + File.separator;
		//System.out.println("project base path=" + this.projectBasePath);
		this.projectBaseDir = antProject.getBaseDir();
		this.antProject = antProject;
		this.antPropertiesEvaluator = antEvaluator;
		this.workDir = new File( this.projectBasePath + "build");
		this.destPath ="dist";
		this.apiDir = getFile("import");
		this.resDir = getFile ("resources");
		this.sourceSettings = new ArrayList();
		this.apis = getFile("apis.xml");
		this.extensions = getFile("extensions.xml");
		this.capabilities = getFile("capabilities.xml");
		this.vendors = getFile("vendors.xml");
		this.groups = getFile("groups.xml");
		this.devices = getFile("devices.xml");
		this.platforms = getFile("platforms.xml");
		this.configurations = getFile("configurations.xml");
		this.capabilities = getFile("capabilities.xml");
		
		this.imageLoadStrategy = IMG_LOAD_FOREGROUND;
		this.resourceUtil = new ResourceUtil( this.getClass().getClassLoader() );
		
		this.defaultJadFilter = new AttributesFilter( DEFAULT_JAD_FILTER_PATTERN );
		this.defaultManifestFilter = new AttributesFilter( DEFAULT_MANIFEST_FILTER_PATTERN );
		
	}
	
	public void addConfiguredObfuscator( ObfuscatorSetting setting ) {
		if (this.obfuscatorSettings == null) {
			this.obfuscatorSettings = new ArrayList();
		}
		if (setting.isActive(this.antProject)) {
			this.obfuscatorSettings.add( setting );
			if (setting.isEnabled()) {
				this.doObfuscate = true;
			}
		}
	}
	
	public void addConfiguredMidlets( MidletSetting setting ) {
		if ( !setting.isActive( this.antPropertiesEvaluator ) ){
			return;
		}
		if (this.midletSetting != null) {
			throw new BuildException("Please use either <midlets> or <midlet> to define your midlets, or use mutually \"if\" and \"unless\" attributes.");
		}
		this.midletSetting = setting;
	}
	
	public void addConfiguredMidlet( Midlet midlet ) {
		if (this.midletSetting == null ) {
			this.midletSetting = new MidletSetting();
		}
		this.midletSetting.addConfiguredMidlet( midlet );
	}
	
	public void addConfiguredFullscreen( FullScreenSetting setting ) {
		if (this.fullScreenSetting != null) {
			throw new BuildException("Please use either the attribute \"fullscreen\" or the nested element <fullscreen>, but not both!");
		}
		this.fullScreenSetting = setting;
	}
	
	public void addConfiguredDebug( LogSetting setting ) {
		if (this.debugSettings == null) {
			this.debugSettings = new ArrayList();
		}
		this.debugSettings.add( setting );
	}
	
	public void addConfiguredVariables( Variables vars ) {
		this.includeAntProperties = vars.includeAntProperties();
		this.variables = vars; //vars.getVariables();
	}
	
	public void addConfiguredManifestFilter( AttributesFilter filter ) {
		if (this.manifestAttributesFilters == null) {
			this.manifestAttributesFilters = new ArrayList();
		}
		this.manifestAttributesFilters.add( filter );
	}
		
	public void addConfiguredJad( JadAttributes attributes ) {
		if (this.jadAttributesFilters == null) {
			this.jadAttributesFilters = new ArrayList();
		}
		this.jadAttributes = attributes;
		this.jadAttributesFilters =  attributes.getFilters();
	}
	
	public void addConfiguredPackager( PackageSetting setting ) {
		if (this.packageSettings == null) {
			this.packageSettings = new ArrayList();
		}
		this.packageSettings.add( setting );
	}
			
	public void addConfiguredPreprocessor( PreprocessorSetting preprocessor ) {
		if (this.preprocessors == null) {
			this.preprocessors = new ArrayList();
		}
		this.preprocessors.add( preprocessor );
	}
	
	public void addConfiguredSources( SourcesSetting setting ) {
		if (setting.isActive( this.antProject ) ) {
			SourceSetting[] sources = setting.getSources();
			for (int i = 0; i < sources.length; i++) {
				SourceSetting source = sources[i];
				this.sourceSettings.add( source );
				/*
				if (source.isActive(this.project)) {
					this.sourceDirs.add( source.getDir() );
				}
				*/
			}
		}
	}
	
	public void addConfiguredSign( SignSetting setting ) {
		this.signSetting = setting;
		//		if (setting.getKeystore() == null) {
//			throw new BuildException("The \"keystore\" attribute of the <sign> element is mandatory.");
//		}
		if (setting.getKeystore() != null && !setting.getKeystore().exists()) {
			throw new BuildException("The \"keystore\" attribute of the <sign> element points to the non-existing file[" + setting.getKeystore().getAbsolutePath() + "].");
		}
		if (setting.getKey() == null) {
			throw new BuildException("The \"key\" attribute of the <sign> element is mandatory.");
		}
		if (setting.getPassword() == null) {
			throw new BuildException("The \"password\" attribute of the <sign> element is mandatory.");
		}
		addConfiguredFinalizer( setting );
	}
	
	public void addConfiguredPreverifier( PreverifierSetting setting ) {
		if (this.preverifiers == null) {
			this.preverifiers = new ArrayList();
		}
		this.preverifiers.add( setting );
	}
	
	public SignSetting getSignSetting() {
		return this.signSetting;
	}

	/**
	 * @param setting
	 */
	public void addConfiguredFinalizer(FinalizerSetting setting) {
		if (this.finalizers == null) {
			this.finalizers = new ArrayList();
		}
		this.finalizers.add( setting );
	}

	public void addConfiguredCompiler(CompilerTask task) {
		if (this.compilers == null) {
			this.compilers = new ArrayList();
		}
		this.compilers.add( task );
	}
	
	public void addConfiguredPreCompiler(PreCompilerSetting setting) {
		if (this.preCompilers == null) {
			this.preCompilers = new ArrayList();
		}
		this.preCompilers.add( setting );
	}
	
	public void addConfiguredPostCompiler(PostCompilerSetting setting) {
		if (this.postCompilers == null) {
			this.postCompilers = new ArrayList();
		}
		this.postCompilers.add( setting );
	}
	
	public void addConfiguredPostObfuscator(PostObfuscatorSetting setting) {
		if (this.postObfuscators == null) {
			this.postObfuscators = new ArrayList();
		}
		this.postObfuscators.add( setting );
	}
	
	/**
	 * Adds a serializer to a rag task, not used in the j2mepolish task
	 */
	public void addConfiguredSerialize(SerializeSetting setting) {
		if (setting.getRegex() == null) {
			throw new BuildException("The nested element <serialize> requires the attribute [regex] which defines a regular expression to choose the fields to serialize.");
		}
		if (setting.getTarget() == null) {
			throw new BuildException("The nested element <serialize> requires the attribute [target] which defines one of the classes to compile for serialization etc.");
		}
		if (this.serializers == null) {
			this.serializers = new ArrayList();
			
		}
		this.serializers.add( setting );
	}
	
	/**
	 * Adds the filename to a rag task, not used in the j2mepolish task
	 */
	public void addConfiguredFile( FileSetting setting ) {
		if (setting.getFile() == null) {
			throw new BuildException("The nested element <file> requires the attribute [name] which defines the name for the resource assembly to generate.");
		}
		
		this.fileSetting = setting;
	}
	
	public PreprocessorSetting[] getPreprocessors() {
		if (this.preprocessors == null) { 
			return new PreprocessorSetting[0];
		} else {
			return (PreprocessorSetting[]) this.preprocessors.toArray( new PreprocessorSetting[ this.preprocessors.size()]);
		}
	}
	
	public JavaExtension createJava() {
		if (this.javaTasks == null) {
			this.javaTasks = new ArrayList();
		}
		JavaExtension java = new JavaExtension( this.antProject );
		this.javaTasks.add( java );
		return java;
	}
	
	public ResourceSetting createResources() {
		if (this.resourceSetting != null) {
			throw new BuildException("Only one <resources> element is allowed within the <build> element, please check your build.xml script. It is also possible that you have used the resDir attribute in combination with the <resources> element - please use only one of both." );
		}
		ResourceSetting setting = new ResourceSetting( this.antProject.getBaseDir() );
		this.resourceSetting = setting;
		return setting;
	}
	
	public Variables getVariables() {
		if (this.variables == null || "false".equals(this.environment.get("polish.buildcontrol.variables.enabled"))) {
			this.variables = new Variables();
		}
		return this.variables;
	}

	
	public JadAttributes getJadAttributes() {
		return this.jadAttributes;
	}
	
	/**
	 * Retrieves the setting for resource handling.
	 * 
	 * @return the setting for resource handling.
	 */
	public ResourceSetting getResourceSetting() {
		if (this.resourceSetting == null) {
			this.resourceSetting = new ResourceSetting( this.antProject.getBaseDir() );
			this.resourceSetting.setDir( getResDir() );
		}
		return this.resourceSetting;
	}
	
	/**
	 * @return Returns the includeAntProperties.
	 */
	public boolean includeAntProperties() {
		return this.includeAntProperties;
	}
	
	public void setSymbols( String symbols ) {
		this.symbols = symbols;
	}
	
	public void setUsePolishGui( String usePolishGuiStr ) {
		if ("always".equals(usePolishGuiStr)) {
			this.usePolishGui = true;
			this.alwaysUsePolishGui = true;
		} else if ( CastUtil.getBoolean(usePolishGuiStr)) {
			this.usePolishGui = true;
			this.alwaysUsePolishGui = false;
		} else {
			this.usePolishGui = false;
			this.alwaysUsePolishGui = false;
		}
	}
		
	/**
	 * Determines whether this project should use the polish GUI at all.
	 * The GUI is only used when the current device allows the use of the GUI.
	 * The GUI makes no sense for devices with black and white screens,
	 * for example.
	 * 
	 * @return true when this projects wants to use the polish GUI
	 */
	public boolean usePolishGui() {
		return this.usePolishGui;
	}
	
	/**
	 * Determines whether the J2ME Polish GUI should beused even for devices which do not "usually" support it.
	 *  
	 * @return true when the J2ME Polish GUI should be used for all devices
	 */
	public boolean alwaysUsePolishGui() {
		return this.alwaysUsePolishGui;
	}
	
	public void setImageLoadStrategy( String strategy ) {
		if ("background".equalsIgnoreCase(strategy) ) {
			this.imageLoadStrategy = IMG_LOAD_BACKGROUND;
		} else if ("foreground".equalsIgnoreCase(strategy)) {
			this.imageLoadStrategy = IMG_LOAD_FOREGROUND;
		} else {
			throw new BuildException("The build-attribute [imageLoadStrategy] needs to be either [background] or [foreground]. "
					+ "The strategy [" + strategy + "] is not supported.");
		}
	}
	
	/**
	 * Retrieves the strategy by which images should be loaded.
	 * 
	 * @return either IMG_LOAD_BACKGROUND or IMG_LOAD_FOREGROUND
	 * @see #IMG_LOAD_BACKGROUND
	 * @see #IMG_LOAD_FOREGROUND
	 */
	public String getImageLoadStrategy() {
		return this.imageLoadStrategy;
	}
	
	public void setFullscreen( String setting ) {
		if (this.fullScreenSetting != null) {
			throw new BuildException("Please use either the attribute [fullscreen] or the nested element [fullscreen], but not both!");
		}
		this.fullScreenSetting = new FullScreenSetting();
		if ("menu".equalsIgnoreCase(setting)) {
			this.fullScreenSetting.setEnable( true );
			this.fullScreenSetting.setMenu( true );
		} else if ("yes".equalsIgnoreCase(setting) || "true".equalsIgnoreCase(setting)) {
			this.fullScreenSetting.setEnable( true );
		} else if ("no".equalsIgnoreCase(setting) || "false".equalsIgnoreCase(setting)) {
			// keep the default setting
		} else {
			throw new BuildException("The build-attribute [fullscreen] needs to be either [yes], [no] or [menu]. "
					+ "The setting [" + setting + "] is not supported.");
		}
	}


	/**
	 * Retrieves the full screen setting.
	 * 
	 * @return the full screen setting
	 */
	public FullScreenSetting getFullScreenSetting() {
		return this.fullScreenSetting;
	}
	
	public LogSetting getDebugSetting(Environment environment) {
		if (this.debugSettings == null) {
			return null;
		}
		for (int i=0; i<this.debugSettings.size(); i++) {
			LogSetting setting = (LogSetting) this.debugSettings.get(i);
			if (setting.isActive(environment)) {
				return setting;
			}
		}
		return null;
	}
	
	/**
	 * Retrieves all MIDlet definitions.
	 * 
	 * @param environment the environment
	 * @return all found MIDlet definitios
	 */
	public Midlet[] getMidlets(Environment environment) {
		Midlet[] midlets = null;
		if (this.midletSetting != null) {
			midlets = this.midletSetting.getMidlets( this.antProject, environment );
		}
		if (midlets == null || midlets.length == 0) {
			// try to load MIDlet definitions from the environment:
			ArrayList list = new ArrayList();
			int i = 1;
			String definition;
			while ( (definition = environment.getVariable("MIDlet-"+i)) != null) {
				Midlet midlet = new Midlet( definition );
				midlet.setNumber(i);
				list.add( midlet );
				i++;
			}
			midlets = (Midlet[]) list.toArray( new Midlet[ list.size() ] );
		}
		if (midlets == null) {
			return new Midlet[ 0 ];
		}
		return midlets;
	}


	/**
	 * Determines whether debugging is enabled.
	 * 
	 * @param environment the environment
	 * @return true when debugging is enabled for this project.
	 */
	public boolean isDebugEnabled(Environment environment) {
		LogSetting setting = getDebugSetting(environment);
		if (setting == null) {
			return false;
		} else {
			return setting.isEnabled();
		}
	}
	
	/**
	 * Retrieves the working directory.
	 * The default working directory is "./build".
	 * If the working directory does not exist, it will be created now.
	 * 
	 * @return Returns the working directory.
	 */
	public File getWorkDir() {
		if (!this.workDir.exists()) {
			this.workDir.mkdirs();
		}
		return this.workDir;
	}
	
	/**
	 * Sets the working directory. Defaults to "./build".
	 * 
	 * @param workDir The working directory to set.
	 */
	public void setWorkDir(File workDir) {
		//File newWorkDir = new File( this.project.getBaseDir().getAbsolutePath() + File.separator + workPath );
		this.workDir = workDir;
	}
	
	/**
	 * Retrieves the directory to which the ready-to-distribute jars should be copied to.
	 * Defaults to "./dist".
	 * If the distribution directory does not exist, it will be created now.
	 * 
	 * @return The destination directory.
	 */
	public File getDestDir( Environment env ) {
		String path = env.writeProperties( this.destPath, true );
		File dir = new File( path );
		if ( !dir.isAbsolute() ) {
			dir = new File( this.projectBaseDir, path );
		}
		if (! dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}
	
	
	/**
	 * Sets the destination directory. Defaults to "./dist".
	 * 
	 * @param destPath The destination directory, can contain J2ME Polish variables such as ${polish.vendor} or ${polish.locale}.
	 */
	public void setDestDir( String destPath ) {
		//File newDestDir = getFile( destPath );
		this.destPath = destPath;
	}
	
	/**
	 * Retrieves the directory which contains the resources. Defaults to "./resources".
	 * Resources include pictures, texts, etc. as well as the CSS-files 
	 * containing the design information. 
	 * If the resource directory does not exist, it will be created now.
	 * 
	 * @return The directory which contains the resources
	 */
	private File getResDir() {
		if (!this.resDir.exists()) {
			this.resDir.mkdirs();
		}
		return this.resDir;
	}
	
	/**
	 * Sets the directory containing the resources of this project.
	 * Default resource directory is "./resources".
	 * Resources include pictures, texts, etc. as well as the CSS-files 
	 * containing the design information. 
	 * 
	 * @param resDir The directory containing the resources.
	 */
	public void setResDir( File resDir ) {
		if (this.resourceSetting != null) {
			throw new BuildException("Please use either the \"resDir\"-attribute of the <build>-element or the <resources>-element, but not both.");
		}
		if (!resDir.exists()) {
			throw new BuildException("The resource directory [" + resDir.getAbsolutePath() + 
					"] does not exist. Please correct the attribute \"resDir\" " +
					"of the <build> element.");
		}
		this.resDir = resDir;
		this.resourceSetting = new ResourceSetting( this.antProject.getBaseDir() );
		this.resourceSetting.setDir( resDir );
	}
	
	/**
	 * Sets the directory containing the J2ME source code of polish.
	 * 
	 * @param polishPath the directory containing the J2ME source code of polish.
	 */
	public void setPolishDir( String polishPath ) {
		File newPolishDir = getFile( polishPath );
		if (!newPolishDir.exists()) {
			throw new BuildException("The J2ME Polish source directory [" + newPolishDir.getAbsolutePath() + 
					"] does not exist. " +
					"Please correct the [polishDir] attribute of the <build> element.");
		}
		String actualSourcePath = newPolishDir.getAbsolutePath() + File.separator
				+ "src";
		File actualSourceDir = new File( actualSourcePath );
		if ( actualSourceDir.exists()) {
			newPolishDir = actualSourceDir;
		}
		this.polishDir = newPolishDir;
	}
	
	/**
	 * Retrieves the directory containing the J2ME source code of polish.
	 * 
	 * @return the directory containing the J2ME source code of polish
	 */
	public File getPolishDir() {
		return this.polishDir;
	}

	public void setSrcdir( String srcDir ) {
		setSourceDir( srcDir );
	}
	public void setSrcDir( String srcDir ) {
		setSourceDir( srcDir );
	}
	/**
	 * Sets the source directory in which the source files for the application reside.
	 * 
	 * @param srcDir the source directory
	 */
	public void setSourceDir( String srcDir ) {
		String[] paths = StringUtil.split( srcDir, ':');
		if (paths.length == 1 || containsSingleCharPath( paths ) ) {
			paths = StringUtil.split( srcDir, ';' );
		}
		for (int i = 0; i < paths.length; i++) {
			String path = paths[i];
			if ( File.separatorChar == '\\' && path.length() == 1 && i < paths.length -1 ) {
				// this is an absolute path on a windows machine, e.g. C:\project\source
				i++;
				path += ":" + paths[i];
			}
			File dir = getFile( path );
			if (dir.exists()) {
				this.sourceSettings.add( new SourceSetting( dir ) );
			}
			else {
				System.err.println("Warning: The source directory [" + path + "] does not exist. " +
					"Please correct the attribute [sourceDir] of the <build> element.");
			}
		}
	}
	
	/**
	 * Determines whether one of the given paths  is only one char long
	 * 
	 * @param paths the paths that are checked
	 * @return true when at one of the given paths is only one char long
	 */
	private boolean containsSingleCharPath(String[] paths) {
		for (int i = 0; i < paths.length; i++) {
			if (paths[i].length() == 1 ) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Retrieves all external source directories.
	 * 
	 * @return an arrray with at least one source directory.
	 */
	public SourceSetting[] getSourceSettings() {
		if (this.sourceSettings.size() == 0) {
			// add default directory: either source/src, scr or source:
			//System.out.println("Starting searching for source dirs...");
			File src = getProjectOrAbsoluteFile( "source/src", false );
			if (!src.exists()) {
				src = getProjectOrAbsoluteFile("src", false );
				if (!src.exists()) {
					src = getProjectOrAbsoluteFile("source", false);
					if (!src.exists()) {
						throw new BuildException("Did not find any of the default " +
								"source directories [source/src], [src] or [source]. " +
								"Please specify the [sourceDir]-attribute of the " +
								"<build> element. " +
								"Base-directory is [" + this.antProject.getBaseDir().getAbsolutePath() + "].");
					}
				}
			}
			//System.out.println("setting source dir to [" + src.getAbsolutePath() + "]");
			this.sourceSettings.add( new SourceSetting( src ) );
		}
		SourceSetting[] settings = (SourceSetting[]) this.sourceSettings.toArray( new SourceSetting[ this.sourceSettings.size()]);
		return settings;
	}
	
	/**
	 * Retrieves the apis.xml file as input stream.
	 * 
	 * @return Returns the apis.xml file as input stream.
	 */
	public InputStream openApis() {
		try {
			return openResource( this.apis, "apis.xml" );
		} catch (FileNotFoundException e) {
			throw new BuildException("Unable to open apis.xml: " + e.getMessage(), e );
		}
	}
	
	public InputStream openConfigurations() {
		try {
			return openResource( this.configurations, "configurations.xml" );
		} catch (FileNotFoundException e) {
			throw new BuildException("Unable to open configurations.xml: " + e.getMessage(), e );
		}
	}

	public InputStream openPlatforms() {
		try {
			return openResource( this.platforms, "platforms.xml" );
		} catch (FileNotFoundException e) {
			throw new BuildException("Unable to open platforms.xml: " + e.getMessage(), e );
		}
	}

	public InputStream openExtensions() {
		try {
			return openResource( this.extensions, "extensions.xml" );
		} catch (FileNotFoundException e) {
			throw new BuildException("Unable to open extensions.xml: " + e.getMessage(), e );
		}
	}

	public InputStream openCapabilities() {
		try {
			return openResource( this.capabilities, "capabilities.xml" );
		} catch (FileNotFoundException e) {
			throw new BuildException("Unable to open capabilities.xml: " + e.getMessage(), e );
		}
	}
	
	/**
	 * Sets the path to the apis.xml file.
	 * 
	 * @param apisPath the path to the apis.xml file
	 */
	public void setApis( String apisPath ) {
		File newApis = getFile( apisPath );
		if (!newApis.exists()) {
			throw new BuildException("The [apis]-attribute of the <build> element points to a non existing file: [" + newApis.getAbsolutePath() + "].");
		}
		this.apis = newApis;		
	}

	/**
	 * @return Returns the the directory which contains device specific libraries.
	 */
	public File getApiDir() {
		if (!this.apiDir.exists()) {
			throw new BuildException("Did not find the api directory in the default path [" + this.apiDir.getAbsolutePath() + "]. Please specify either the [apiDir]-attribute of the <build> element or copy all device-specific jars to this path.");
		}
		return this.apiDir;
	}
	
	/**
	 * Sets the directory which contains device specific libraries
	 * 
	 * @param apiPath The directory which contains device specific libraries. Defaults to "./import"
	 */
	public void setApiDir(String apiPath) {
		File newApiDir = getFile( apiPath );
		if (!newApiDir.exists()) {
			throw new BuildException("The [apiDir]-attribute of the <build> element points to a non existing directory: [" + newApiDir.getAbsolutePath() + "].");
		}
		this.apiDir = newApiDir;
	}
	
	/**
	 * @return Returns the xml file containing the devices-data.
	 */
	public InputStream openDevices() {
		try {
			return openResource( this.devices, "devices.xml" );
		} catch (FileNotFoundException e) {
			throw new BuildException("Unable to open devices.xml: " + e.getMessage(), e );
		}
	}
	
	/**
	 * Sets the path to the device.xml file.
	 * 
	 * @param devicesPath The path to the devices.xml
	 */
	public void setDevices(String devicesPath) {
		File newDevices = getFile( devicesPath );
		if (!newDevices.exists()) {
			throw new BuildException("The [devices]-attribute of the <build> element points to a non existing file: [" + newDevices.getAbsolutePath() + "].");
		}
		this.devices = newDevices;
	}
	
	/**
	 * @return Returns the groups.
	 */
	public InputStream openGroups() {
		try {
			return openResource( this.groups, "groups.xml" );
		} catch (FileNotFoundException e) {
			throw new BuildException("Unable to open groups.xml: " + e.getMessage(), e );
		}
	}
	
	/**
	 * Sets the path to the groups.xml file
	 * 
	 * @param groupsPath The path to the groups.xml file
	 */
	public void setGroups(String groupsPath) {
		File newGroups = getFile( groupsPath );
		if (!newGroups.exists()) {
			throw new BuildException("The [groups]-attribute of the <build> element points to a non existing file: [" + newGroups.getAbsolutePath() + "].");
		}
		this.groups = newGroups;
	}

	/**
	 * Retrieves the vendors.xml file as input stream.
	 * 
	 * @return Returns the vendors.xml file as input stream.
	 */
	public InputStream openVendors() {
		try {
			return openResource( this.vendors, "vendors.xml" );
		} catch (FileNotFoundException e) {
			throw new BuildException("Unable to open vendors.xml: " + e.getMessage(), e );
		}
	}
	
	/**
	 * Sets the path to the vendors.xml file
	 * 
	 * @param vendorsPath The path to the vendors.xml file
	 */
	public void setVendors(String vendorsPath) {
		File newVendors = getFile( vendorsPath );
		if (!newVendors.exists()) {
			throw new BuildException("The [vendors]-attribute of the <build> element points to a non existing file: [" + newVendors.getAbsolutePath() + "].");
		}
		this.vendors = newVendors;
	}

	
	/**
	 * Sets the path to the api-file of the MIDP/1.0 environment
	 *  
	 * @param midp1PathStr The path to the MIDP/1.0-api-file
	 */
	public void setMidp1Path( String midp1PathStr ) {
		throw new BuildException("Plese specify the bootclass-APIs within platforms.xml and configurations.xml.");
	}

	
	/**
	 * Sets the path to the api-file of the MIDP/2.0 environment.
	 * When the midp1Path is not defined, it will use the same
	 * api-path as the given MIDP/2.0 environment.
	 *  
	 * @param midp2PathStr The path to the MIDP/2.0-api-file
	 */
	public void setMidp2Path( String midp2PathStr ) {
		throw new BuildException("Plese specify the bootclass-APIs within platforms.xml and configurations.xml.");
	}

	/**
	 * Sets the path to the api-file of the MIDP/2.0 / CLDC/1.1 environment.
	 *  
	 * @param midp2Cldc11PathStr The path to the MIDP/2.0-api-file
	 */
	public void setMidp2Cldc11Path( String midp2Cldc11PathStr ) {
		throw new BuildException("Plese specify the bootclass-APIs within platforms.xml and configurations.xml.");
	}
	
	
	/**
	 * @return The user-defined symbols
	 */
	public String getSymbols() {
		return this.symbols;
	}
	
	/**
	 * Sets the path to the preverify executable.
	 * 
	 * @param preverifyPath the path to the preverify executable.
	 */
	public void setPreverify( String preverifyPath ) {
		if ( "true".equalsIgnoreCase(preverifyPath) || "yes".equalsIgnoreCase( preverifyPath)) {
			this.doPreverify = true;
			return;
		} else if ( "false".equalsIgnoreCase(preverifyPath) || "no".equalsIgnoreCase( preverifyPath)) {
			this.doPreverify = false;
			return;
		}
		File newPreverify = getFile( preverifyPath );
		if (!newPreverify.exists()) {
			throw new BuildException("The path to the preverify-tool is invalid: [" + newPreverify.getAbsolutePath() + "] points to a non-existing file. Please correct the [preverify] attribute of the <build> element.");
		}
		this.preverify = newPreverify;
	}
		
	/**
	 * Determines whether the project should get compiled.
	 * 
	 * @param compile string to flag if we should compile or not
	 */
	public void setCompile( String compile ) {
		if ( "true".equalsIgnoreCase(compile) || "yes".equalsIgnoreCase( compile)) {
			this.doCompile = true;
			return;
		} else if ( "false".equalsIgnoreCase(compile) || "no".equalsIgnoreCase( compile)) {
			this.doCompile = false;
			return;
		}
	}
	
	public File getPreverify() {
		return this.preverify;
	}
	
	/**
	 * Determines whether the project should get preverified.
	 * 
	 * @return true when the project should get preverified.
	 */
	public boolean doPreverify() {
		return this.doPreverify;
	}
		
	/**
	 * Determines whether the project should be compiled.
	 * This is used for android building.
	 * 
	 * @return true when the project should get preverified.
	 */
	public boolean doCompile() {
		return this.doCompile;
	}
	
	/**
	 * Retrieves all the defined MIDlet-class-names.
	 * 
	 * @param environment the environment
	 * @return The names of all midlet-classes in a String array. 
	 * 		The first midlet is also the first element in the returned array.
	 */
	public String[] getMidletClassNames( Environment environment ) {
		Midlet[] midlets = getMidlets( environment );
		String[] midletClassNames = new String[ midlets.length ];
		for (int i = 0; i < midlets.length; i++) {
			String className = midlets[i].getClassName();
			midletClassNames[i] = className;
		}
		return midletClassNames;
	}
	
	/**
	 * Retrieves the infos for all midlets.
	 * The infos contain the name, the icon and the class of the midlet
	 * and are used for the JAD and the manifest.
	 * 
	 * @param defaultIcon the url of the default icon.
	 * @param environment environment settings
	 * @return The infos of all midlets in a String array.
	 * 		The first midlet is also the first element in the returned array.
	 */
	public String[] getMidletInfos( String defaultIcon, Environment environment ) {
		Midlet[] midlets = getMidlets( environment );
		String[] midletInfos = new String[ midlets.length ];
		for (int i = 0; i < midlets.length; i++) {
			midletInfos[i] = midlets[i].getMidletInfo( defaultIcon );
		}
		return midletInfos;
	}

	/**
	 * @return The obfuscators which should be used
	 */
	public ObfuscatorSetting[] getObfuscatorSettings() {
		if (this.obfuscatorSettings == null) {
			return new ObfuscatorSetting[0];
		} else { 
			return (ObfuscatorSetting[]) this.obfuscatorSettings.toArray( new ObfuscatorSetting[ this.obfuscatorSettings.size() ] );
		}
	}
	
	/**
	 * Sets the name of the obfuscator.
	 * 
	 * @param obfuscator The name of the obfuscator, e.g. "ProGuard" or "RetroGuard"
	 */
	public void setObfuscator( String obfuscator ) {
		ObfuscatorSetting setting = new ObfuscatorSetting();
		setting.setName( obfuscator );
		addConfiguredObfuscator( setting );
	}
	
	/**
	 * Determines whether the resulting jars should be obfuscated at all.
	 * 
	 * @return True when the jars should be obfuscated.
	 */
	public boolean doObfuscate() {
		return this.doObfuscate;
	}
	
	/**
	 * Determines whether the resulting jars should be obfuscated at all.
	 * 
	 * @param obfuscate True when the jars should be obfuscated.
	 */
	public void setObfuscate( boolean obfuscate ) {
		if (obfuscate) {
			if (this.obfuscatorSettings == null) {
				this.obfuscatorSettings = new ArrayList();
				ObfuscatorSetting setting = new ObfuscatorSetting();
				setting.setEnable( true );
				addConfiguredObfuscator(setting);
			} else {
				ObfuscatorSetting setting = (ObfuscatorSetting) this.obfuscatorSettings.get(0);
				setting.setEnable(true);
				this.doObfuscate = true;
			}
			
		}
	}

	/**
	 * Retrieves the specified resource as an input stream.
	 * The caller is responsible for closing the returned input stream.
	 * 
	 * @param file the file which has been set. When the file is not null,
	 * 		  it needs to exists as well.
	 * @param name the name of the resource
	 * @return the input stream for the specified resource.
	 * @throws FileNotFoundException when the resource could not be found.
	 */
	private InputStream openResource(File file, String name) 
	throws FileNotFoundException 
	{
		if (file != null && file.exists() ) {
			try {
				return new FileInputStream( file );
			} catch (FileNotFoundException e) {
				throw new BuildException("Unable to open [" + file.getAbsolutePath() + "]: " + e.getMessage(), e );
			}
		}
		return this.resourceUtil.open( this.antProject.getBaseDir().getAbsolutePath(), name );
	}
	
	/**
	 * Resolves the given path and returns a file handle for that path.
	 * 
	 * @param path the relative or absolute path, e.g. "resources2"
	 * @return the file handle for the path
	 */
	protected File getFile( String path ) {
		return getFile( path, true );
	}
	
	/**
	 * Resolves the given path and returns a file handle for that path.
	 * 
	 * @param path the relative or absolute path, e.g. "resources2"
	 * @param tryPolishHomePath true when the file should also be searched in the polishHomePath 
	 * @return the file handle for the path
	 */
	protected File getFile( String path, boolean tryPolishHomePath ) {
		File absolute = new File( path );
		if (absolute.isAbsolute()) {
			return absolute;
		}
		File file = new File( this.projectBasePath + path );
		if (!file.exists() && tryPolishHomePath) {
			file = new File( this.polishHomePath + path );
		}
		if (!file.exists()) {
			file = new File( path );
		}
		return file;
	}
	
	/**
	 * Resolves the given path and returns a file handle for that path.
	 * 
	 * @param path the relative or absolute path, e.g. "resources2"
	 * @param tryPolishHomePath true when the file should also be searched in the polishHomePath 
	 * @return the file handle for the path
	 */
	protected File getProjectOrAbsoluteFile( String path, boolean tryPolishHomePath ) {
		File absolute = new File( path );
		if (absolute.isAbsolute()) {
			return absolute;
		}
		File file = new File( this.projectBasePath + path );
		if (!file.exists() && tryPolishHomePath) {
			file = new File( this.polishHomePath + path );
		}
		return file;
	}


	/**
	 * Determines whether there is a filter registered for JAD attributes
	 * 
	 * @return true when there is a filter defined.
	 */
	public boolean hasUserDefinedJadAttributesFilter() {
		return this.jadAttributesFilters != null;
	}

	/**
	 * Filters the given JAD attributes.
	 * 
	 * @param attributesMap a hash map containing the available attributes
	 *        with the attribute-names as keys.
	 * @param evaluator the evaluator for getting the correct filter
	 * @return an array of attributes in the correct order,
	 *         not all given attributes are guaranteed to be included.
	 * @throws NullPointerException when there is no JAD-attribute filter.
	 */
	public Attribute[] filterJadAttributes( HashMap attributesMap, BooleanEvaluator evaluator ) {
		if (this.jadAttributesFilters != null) {
			AttributesFilter[] filters = (AttributesFilter[]) this.jadAttributesFilters.toArray( new AttributesFilter[ this.jadAttributesFilters.size() ]);
			for (int i = 0; i < filters.length; i++) {
				AttributesFilter filter = filters[i];
				if (filter.isActive(evaluator, this.antProject)) {
					return filter.filterAttributes(attributesMap);
				}
			}
		} 
		return this.defaultJadFilter.filterAttributes(attributesMap);
	}
	
	/**
	 * Determines whether there is a filter registered for manifest attributes
	 * 
	 * @return true when there is a filter defined.
	 */
	public boolean hasUserDefinedManifestAttributesFilter() {
		return this.manifestAttributesFilters != null;
	}

	/**
	 * Filters the given manifest attributes.
	 * 
	 * @param attributesMap a hash map containing the available attributes
	 *        with the attribute-names as keys.
	 * @param evaluator the boolean evaluator for getting the correct filter
	 * @return an array of attributes in the correct order,
	 *         not all given attributes are guaranteed to be included.
	 * @throws NullPointerException when there is no MANIFEST-attribute filter.
	 */
	public Attribute[] filterManifestAttributes( HashMap attributesMap, BooleanEvaluator evaluator ) {
		if (this.manifestAttributesFilters != null) {
			AttributesFilter[] filters = (AttributesFilter[]) this.manifestAttributesFilters.toArray( new AttributesFilter[ this.manifestAttributesFilters.size() ]);
			for (int i = 0; i < filters.length; i++) {
				AttributesFilter filter = filters[i];
				if (filter.isActive(evaluator, this.antProject)) {
					return filter.filterAttributes(attributesMap);
				}
			}
		} 
		return this.defaultManifestFilter.filterAttributes(attributesMap);
	}
	
	/**
	 * Same as setBinaryLibraries
	 * 
	 * @param librariesStr the paths to either a jar-file, a zip-file or a directory 
	 * 			containing class files, which are needed for the project.
	 * 			Several libraries can be seperated with either a colon or a semicolon. 
	 */
	public void setBinaryLibrary( String librariesStr ) {
		setBinaryLibraries(librariesStr);
	}
	
	/**
	 * Sets additional third party libraries which are only available in binary form.
	 * 
	 * @param librariesStr the paths to either a jar-file, a zip-file or a directory 
	 * 			containing class files, which are needed for the project.
	 * 			Several libraries can be seperated with either a colon or a semicolon. 
	 */
	public void setBinaryLibraries( String librariesStr ) {
		//System.out.println("SETTING BINARY LIBRARIES=" + librariesStr );
		String[] libraryPaths = StringUtil.split(librariesStr, ':');
		boolean useSemicolonSplit = false;
		for (int i = 0; i < libraryPaths.length; i++) {
			String path = libraryPaths[i];
			if (path.length() == 1) { // this could be C:, D: etc on Windows systems
				useSemicolonSplit = true;
			}
		}
		if (libraryPaths.length == 1 || useSemicolonSplit) {
			libraryPaths = StringUtil.split( librariesStr, ';' );
		}
		if (this.binaryLibraries == null ) {
			this.binaryLibraries = new LibrariesSetting( this.environment );
		}
		for (int i = 0; i < libraryPaths.length; i++) {
			String libPath = libraryPaths[i];
			File file = getFile( libPath );
			if (!file.exists()) {
				// check if the file resides in the api folder (usually "import"):
				file = new File( this.apiDir, libPath );
				if (!file.exists()) {
					// try ${polish.home}/import:
					file = new File( this.polishHomeDir, "import" + File.separatorChar + libPath );
					//System.out.println("polish.home=" + this.polishHomeDir.getAbsolutePath() );
					if (!file.exists()) {
						throw new BuildException("The binary library [" + libPath + "] could not be found - please check your \"binaryLibraries\"-attribute of the <build> element: File not found: " + file.getAbsolutePath() );
					}
				}
			}
			//System.out.println("Adding binary library [" + file.getAbsolutePath() + "]");
			LibrarySetting setting = new LibrarySetting();
			if (file.isDirectory()) {
				setting.setDir( file );
			} else {
				setting.setFile( file );
			}
			this.binaryLibraries.addConfiguredLibrary( setting  );
		}
	}
	
	/**
	 * Creates a new &lt;libraries&gt; element or returns an existing one.
	 * 
	 * @return a new &lt;libraries&gt; element
	 */
	public LibrariesSetting createLibraries() {
		if (this.binaryLibraries == null) {
			this.binaryLibraries = new LibrariesSetting( this.environment );
		}
		return this.binaryLibraries;
	}
	
//	/**
//	 * Sets the complete library-settings
//	 *  
//	 * @param setting the libraries
//	 */
//	public void addConfiguredLibraries( LibrariesSetting setting ) {
//		if ( this.binaryLibraries == null ) {
//			this.binaryLibraries = setting;
//		} else {
//			this.binaryLibraries.add( setting );
//		}
//	}
	
	/**
	 * Retrieves third party libraries which are only available in binary form.
	 *  
	 * @return The libraries setting
	 */
	public LibrariesSetting getBinaryLibraries() {
		return this.binaryLibraries;
	}
	
	/**
	 * Retrieves the extensions with a java-element.
	 * 
	 * @return an array of JavaExtension
	 */
	public JavaExtension[] getJavaExtensions() {
		if (this.javaTasks == null) {
			return new JavaExtension[0];
		} else {
			return (JavaExtension[]) this.javaTasks.toArray( new JavaExtension[ this.javaTasks.size() ]);
		}
	}
	
	/**
	 * Opens the [css-attributes.xml] file.
	 * 
	 * @return an input stream to that file
	 * @throws BuildException when the file could not be found
	 */
	public InputStream openStandardCssAttributes(){
		try {
			return openResource( getFile("css-attributes.xml"), "css-attributes.xml");
		} catch (FileNotFoundException e) {
			throw new BuildException("Unable to load [css-attributes.xml].");
		}
	}

	/**
	 * Opens the [custom-css-attributes.xml] file.
	 * 
	 * @return an input stream to that file or null when the file could not be found.
	 */
	public InputStream openCustomCssAttributes(){
		try {
			return openResource( getFile("custom-css-attributes.xml"), "custom-css-attributes.xml");
		} catch (FileNotFoundException e) {
			System.out.println("Warning: Unable to load [custom-css-attributes.xml].");
			return null;
		}
	}

	/**
	 * Retrieves the target to which the java-sources should be compiled.
	 * When a specific target has been set, that one will be used.
	 * Otherwise a target will dynamically be created:
	 * <ul>
	 * <li>when a WTK-version smaller than 2.0 is used, the "1.1" target will be used;</li>
	 * <li>when the OS is Mac OS X, the "1.1" target will be used;</li>
	 * <li>in all other cases the "1.2" target is used.</li>
	 * </ul>
	 * 
	 * @return Returns the javac-target.
	 */
	public String getJavacTarget() {
		if (this.javacTarget != null) {
			return this.javacTarget;
		} else {
			// check for OS X:
			String osName = "OS-Name: " + System.getProperty("os.name");
			if (osName.indexOf("Mac OS X") != -1) {
				return TARGET_1_1;
			}
			// check for WTK version < 2.0:
			String wtkHomePath = this.antProject.getProperty("wtk.home");
			if (wtkHomePath != null) {
				if ((wtkHomePath.indexOf('1') != -1) && 
				(wtkHomePath.indexOf("1.") != -1 
						|| wtkHomePath.indexOf("WTK1") != -1
						|| wtkHomePath.indexOf("1_") != -1)) 
				{
					return TARGET_1_1;
				}
			}
						
			// return default:
			return TARGET_1_2;
		}
	}
	
	/**
	 * Sets the target to which the java-sources should be compiled.
	 * Should be either "1.1" or "1.2".
	 * 
	 * @param javacTarget The javac-target to set.
	 */
	public void setJavacTarget(String javacTarget) {
		if (javacTarget.equals( TARGET_1_1)) {
			this.javacTarget = TARGET_1_1;
		} else if (javacTarget.equals( TARGET_1_2)) {
			this.javacTarget = TARGET_1_2;
		} else {
			this.javacTarget = javacTarget;
		}
	}
	
	/**
	 * Enables or disables the compiler-mode of J2ME Polish.
	 * In the compiler mode only one device will be processed
	 * and the execution will be aborted before the packaging is done.
	 * 
	 * @param enable true when the compiler-mode should be enabled.
	 *        The mode is disabled by default.
	 */
	public void setCompilerMode( boolean enable ) {
		this.compilerMode = enable;
	}
	
	/**
	 * Determines whether the compiler-mode of J2ME Polish is activated.
	 * In the compiler mode only one device will be processed
	 * and the execution will be aborted before the packaging is done.
	 * The mode is disabled by default.
	 * 
	 * @return true when the compiler-mode should be enabled.
	 */
	public boolean isInCompilerMode() {
		return this.compilerMode;
	}
	
	/**
	 * Sets the destination directory for compiled classes.
	 * This setting is only used when the compiler-mode is activated.
	 * 
	 * @param destinationDir the target directory for compiled classes.
	 *        The default directory is "bin/classes". 
	 */
	public void setCompilerDestDir( File destinationDir ) {
		this.compilerDestDir = destinationDir;
	}
	
	/**
	 * Retrieves the target directory for compiled classes.
	 * 
	 * @return the target directory for compiled classes.
	 *        The default directory is "bin/classes". 
	 */
	public File getCompilerDestDir() {
		if (this.compilerDestDir == null) {
			return new File( this.projectBasePath + "bin/classes" );
		} else {
			return this.compilerDestDir;
		}
	}
	
	public void setCompilerModePreverify( boolean enable ) {
		this.compilerModePreverify = enable;
	}
	
	public boolean doPreverifyInCompilerMode() {
		return this.compilerModePreverify;
	}
	
	public PackageSetting[] getPackageSettings() {
		if (this.packageSettings == null) {
			return new PackageSetting[0];
		} else {
			return (PackageSetting[]) this.packageSettings.toArray( new PackageSetting[ this.packageSettings.size() ]);
		}
	}

	/**
	 * Determines whether all classes should be moved into the default package ("").
	 * 
	 * @return true when all classes should be moved into the default package.
	 */
//	public boolean useDefaultPackage() {
//		if (this.obfuscatorSettings == null) {
//			return false;
//		} else {
//			ObfuscatorSetting[] settings = getObfuscatorSettings();
//			for (int i = 0; i < settings.length; i++) {
//				ObfuscatorSetting setting = settings[i];
//				if (setting.useDefaultPackage()) {
//					return true;
//				}
//			}
//		}
//		return false;
//	}
	
	/**
	 * Retrieves the appropriate compiler setting
	 * 
	 * @param evaluator the evaluator for boolean conditions 
	 * @return a suitable compiler task
	 */
	public CompilerTask getCompiler( BooleanEvaluator evaluator ) {
		if (this.compilers != null) {
			CompilerTask[] tasks = (CompilerTask[]) this.compilers.toArray( new CompilerTask[this.compilers.size() ]);
			for (int i = 0; i < tasks.length; i++) {
				CompilerTask task = tasks[i];
				if (task.isActive(evaluator, this.antProject)) {
					return task.copy();
				}
			}
		}
		return new CompilerTask();
	}
	
	/**
	 * Sets the encoding for the JAD, MANIFEST.
	 * 
	 * @param encoding the encoding, defaults to "UTF8"
	 */
	public void setEncoding( String encoding ) {
		this.encoding = encoding;
	}
	
	/**
	 * Sets the encoding for the JAD, MANIFEST.
	 * 
	 * @return the encoding, defaults to "UTF8"
	 */
	public String getEncoding() {
		return this.encoding;
	}
	
	public PreCompilerSetting[] getPreCompilers() {
		if (this.preCompilers == null) {
			return new PreCompilerSetting[0]; // { screenChangerSetting }; 
		} else {
			//this.postCompilers.add( screenChangerSetting );
			return (PreCompilerSetting[]) this.preCompilers.toArray( new PreCompilerSetting[this.preCompilers.size()] );
		}
	}

	public boolean doPreCompile() {
		return this.preCompilers != null;
	}
	
	public PostObfuscatorSetting[] getPostObfuscators() {
		// init standard postcompilers:
		//PostCompilerSetting screenChangerSetting = new PostCompilerSetting();
		//screenChangerSetting.setName("screenchanger");
		if (this.postObfuscators == null) {
			return new PostObfuscatorSetting[0]; // { screenChangerSetting }; 
		} else {
			//this.postCompilers.add( screenChangerSetting );
			return (PostObfuscatorSetting[]) this.postObfuscators.toArray( new PostObfuscatorSetting[this.postObfuscators.size()] );
		}
	}

	/**
	 * @return true when there are postcompilers
	 */
	public boolean doPostCompile() {
		return this.postCompilers != null;
	}
	
	public PostCompilerSetting[] getPostCompilers() {
		// init standard postcompilers:
		//PostCompilerSetting screenChangerSetting = new PostCompilerSetting();
		//screenChangerSetting.setName("screenchanger");
		if (this.postCompilers == null) {
			return new PostCompilerSetting[0]; // { screenChangerSetting }; 
		} else {
			//this.postCompilers.add( screenChangerSetting );
			return (PostCompilerSetting[]) this.postCompilers.toArray( new PostCompilerSetting[this.postCompilers.size()] );
		}
	}

	/**
	 * @return true when there are postobfuscators
	 */
	public boolean doPostObfuscate() {
		return this.postObfuscators != null;
	}
	
	/**
	 * @param manager
	 * @param environment
	 * @return an array of initialized finalizers
	 */
	public Finalizer[] getFinalizers(ExtensionManager manager, Environment environment) 
	{
		if (this.finalizers == null) {
			return new Finalizer[0];
		}
		BooleanEvaluator evaluator = environment.getBooleanEvaluator();
		ArrayList list = new ArrayList( this.finalizers.size() );
		for (Iterator iter = this.finalizers.iterator(); iter.hasNext();) {
			FinalizerSetting finalizerSetting = (FinalizerSetting) iter.next();
			if (finalizerSetting.isActive( evaluator, this.antProject)) {
				try {
					// dont't store the finalizer, so that several different "antcall" finalizers are used
					//boolean storeExtension = false;
					//System.out.println("getting finalizer " + finalizerSetting.getName() );
					//Finalizer finalizer = (Finalizer) manager.getExtension( ExtensionManager.TYPE_FINALIZER, null, finalizerSetting, environment, storeExtension );
					Finalizer finalizer = (Finalizer) manager.getExtension( ExtensionManager.TYPE_FINALIZER, finalizerSetting, environment );
					list.add( finalizer );
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return (Finalizer[]) list.toArray( new Finalizer[ list.size() ] );
	}
	
	
	/**
	 * @param customApis The customApis to set.
	 */
	public void setCustomApis(File customApis) {
		if (!customApis.exists()) {
			throw new BuildException("The <build> attribute \"customApis\" points to a non-existing file: " + customApis.getAbsolutePath() );
		}
		this.customApis = customApis;
	}
	
	
	/**
	 * @param customDevices The customDevices to set.
	 */
	public void setCustomDevices(File customDevices) {
		if (!customDevices.exists()) {
			throw new BuildException("The <build> attribute \"customDevices\" points to a non-existing file: " + customDevices.getAbsolutePath() );
		}
		this.customDevices = customDevices;
	}
	
	/**
	 * @param customConfigurations The file containing custom-configurations.xml
	 */
	public void setCustomConfigurations(File customConfigurations) {
		if (!customConfigurations.exists()) {
			throw new BuildException("The <build> attribute \"customConfigurations\" points to a non-existing file: " + customConfigurations.getAbsolutePath() );
		}
		this.customConfigurations = customConfigurations;
	}

	/**
	 * @param customPlatforms The file containing custom-platforms.xml
	 */
	public void setCustomPlatforms(File customPlatforms) {
		if (!customPlatforms.exists()) {
			throw new BuildException("The <build> attribute \"customPlatforms\" points to a non-existing file: " + customPlatforms.getAbsolutePath() );
		}
		this.customPlatforms = customPlatforms;
	}

	
	/**
	 * @param customExtensions The customExtensions to set.
	 */
	public void setCustomExtensions(File customExtensions) {
		if (!customExtensions.exists()) {
			throw new BuildException("The <build> attribute \"customExtensions\" points to a non-existing file: " + customExtensions.getAbsolutePath() );
		}
		this.customExtensions = customExtensions;
	}

	public File getCustomExtensions() {
		return this.customExtensions;
	}
	
	/**
	 * @param customGroups The customGroups to set.
	 */
	public void setCustomGroups(File customGroups) {
		if (!customGroups.exists()) {
			throw new BuildException("The <build> attribute \"customGroups\" points to a non-existing file: " + customGroups.getAbsolutePath() );
		}
		this.customGroups = customGroups;
	}
	
	/**
	 * @param customVendors The customVendors to set.
	 */
	public void setCustomVendors(File customVendors) {
		if (!customVendors.exists()) {
			throw new BuildException("The <build> attribute \"customVendors\" points to a non-existing file: " + customVendors.getAbsolutePath() );
		}
		this.customVendors = customVendors;
	}
	
	
	public PreverifierSetting[] getPreverifierSettings() {
		if (this.preverifiers == null) {
			return new PreverifierSetting[0];
		} else {
			return (PreverifierSetting[]) this.preverifiers.toArray( new PreverifierSetting[ this.preverifiers.size() ] );
		}
	}

	/**
	 * @return a map which contains the openend input stream for each device database file, e.g. "devices.xml"
	 */
	public Map getDeviceDatabaseInputStreams() {
		Map map = new HashMap();
		map.put( "capabilities.xml", openCapabilities() );
		map.put( "apis.xml", openApis() );
		map.put( "configurations.xml", openConfigurations() );
		map.put( "platforms.xml", openPlatforms() );
		map.put( "vendors.xml", openVendors() );
		map.put( "groups.xml", openGroups() );
		map.put( "devices.xml", openDevices() );
		//map.put( "bugs.xml", openBugs() );
		return map;
	}

	/**
	 * @return a map that contains user defined files for custom-XXX files, e.g. custom-devices.xml 
	 */
	public Map getDeviceDatabaseFiles() {
		Map map = new HashMap();
		if (this.customApis != null) {
			map.put( "custom-apis.xml", this.customApis );
		}
		if (this.customConfigurations != null) {
			map.put( "custom-configurations.xml", this.customConfigurations );
		}
		if (this.customPlatforms != null) {
			map.put( "custom-platforms.xml", this.customPlatforms );
		}
		if (this.customVendors != null) {
			map.put( "custom-vendors.xml", this.customVendors );
		}
		if (this.customGroups != null) {
			map.put( "custom-groups.xml", this.customGroups );
		}
		if (this.customDevices != null) {
			map.put( "custom-devices.xml", this.customDevices );
		}
		//map.put( "bugs.xml", openBugs() );
		return map;
	}

	/**
	 * @return true when ant properties should be replaced without an explicit directive, e.g. String message="Hello ${polish.Identifier}";
	 */
	public boolean replacePropertiesWithoutDirective() {
		return this.replacePropertiesWithoutDirective;
	}

	public void setReplacePropertiesWithoutDirective( boolean enable ) {
		this.replacePropertiesWithoutDirective = enable;
	}

	public boolean abortOnError() {
		return this.abortOnError;
	}

	public void setAbortOnError(boolean abortOnError) {
		this.abortOnError = abortOnError;
	}

	public String getOnError() {
		return this.onError;
	}

	public void setOnError(String onError) {
		this.onError = onError;
	}

	/**
	 * @return Returns the dojaClassSetting.
	 */
	public ClassSetting getDojaClassSetting() {
		return this.dojaClassSetting;
	}

	/**
	 * @param setting The dojaClassSetting to set.
	 */
	public void addConfiguredDoja(ClassSetting setting) {
		if (setting.getClassName() == null ) {
			throw new BuildException("Invalid <doja>/<iappli> element: the \"class\" attribute is required.");
		}
		this.dojaClassSetting = setting;
	}

	/**
	 * @param setting The dojaClassSetting to set.
	 */
	public void addConfiguredIappli(ClassSetting setting) {
		addConfiguredDoja( setting );
	}

	/**
	 * @return Returns the mainClassSetting.
	 */
	public ClassSetting getMainClassSetting() {
		return this.mainClassSetting;
	}

	/**
	 * @param setting The mainClassSetting to set.
	 */
	public void addConfiguredMain(ClassSetting setting) {
		if (setting.getClassName() == null ) {
			throw new BuildException("Invalid <main> element: the \"class\" attribute is required.");
		}
		this.mainClassSetting = setting;
	}

	public FileSetting getFileSetting() {
		return fileSetting;
	}

	public ArrayList getSerializers() {
		return serializers;
	}


	
}
