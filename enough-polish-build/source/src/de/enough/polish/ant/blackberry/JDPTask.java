package de.enough.polish.ant.blackberry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.build.LibrariesSetting;
import de.enough.polish.ant.build.LibrarySetting;
import de.enough.polish.obfuscate.Obfuscator;
import de.enough.polish.preverify.Preverifier;
import de.enough.polish.preverify.ProGuardPreverifier;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.JarUtil;
import de.enough.polish.util.StringUtil;

public class JDPTask extends Task {
	private static final String TEMPLATE = 
		"## RIM Java Development Environment\n" +
		"# RIM Project file\n" + 
		"AlwaysBuild=0\n" + 
		"[AlxImports\n" + 
		"]\n" + 
		"AutoRestart=0\n" + 
		"[ClassProtection\n" + 
		"]\n" + 
		"[CustomBuildFiles\n" + 
		"]\n" + 
		"[CustomBuildRules\n" + 
		"]\n" + 
		"[DefFiles\n" + 
		"]\n" + 
		"[DependsOn\n" + 
		"@@DEPENDENCIES@@" +
		"]\n" + 
		"ExcludeFromBuildAll=0\n" +
		"[Files\n" +
		"@@FILES@@" +
		"]\n" +
		"HaveAlxImports=0\n" +
		"HaveDefs=0\n" +
		"[Icons\n" +
		"@@ICONS@@" +
		"]\n" +
		"[RolloverIcons\n" +
		"@@ROLLOVERICONS@@" +
		 "]\n" +
		"[ImplicitRules\n" +
		"]\n" +
		"HaveImports=0\n" +
		"[Imports\n" +
		"]\n" +
		"Listing=0\n" +
		"Options=-quiet\n" +
		"OutputFileName=@@NAME@@\n" +
		"[PackageProtection\n" +
		"]\n" +
		"RibbonPosition=0\n" +
		"RunOnStartup=0\n" +
		"StartupTier=7\n" +
		"SystemModule=0\n";
		
	
	
	private static final String FILES = "@@FILES@@";
	private static final String NAME = "@@NAME@@";
	private static final String ICONS = "@@ICONS@@";
	private static final String ICONS_ROLLOVER = "@@ROLLOVERICONS@@";
	private static final String DEPENDENCIES = "@@DEPENDENCIES@@";
	//private static final String IMPORTS = "@@IMPORTS@@";

	String name;
	
	String path;

	String template;

	String sources;
	
	public void execute() throws BuildException {
		String sourceDir = this.sources + File.separatorChar + "source";

		String resourceDir = this.sources +File.separatorChar+ "resources"; 

		try {
			System.out.println("jdp: Reading template...");
			String root = this.path.trim() + File.separatorChar;
			String fullPath = root + this.name + ".jdp";

			String content;
			
			if(this.template != null)
			{
				content = readFile(new File(this.template)).trim();
			}
			else
			{
				content = TEMPLATE + "Type=0\n";
			}
			
			content = addIcons(content, root);
			content = StringUtil.replace(content, NAME, this.name);

			System.out.println("jdp: Collecting java classes from " + sourceDir + "...");
			String[] sourceFiles = FileUtil.filterDirectory( new File(sourceDir), "java", true );

			System.out.println("jdp: Collecting resources from " + resourceDir + "...");
			String[] resourceFiles = FileUtil.filterDirectory(new File(resourceDir), null, false);

			System.out.println("jdp: Writing files to project " + fullPath + "...");

			boolean hasLibraries = hasLibraries();
			StringBuffer mainProjectBuffer = new StringBuffer();
			StringBuffer libraryProjectBuffer = null;
			String dependency = "";
			if (hasLibraries) {
				libraryProjectBuffer = new StringBuffer();
				dependency = this.name + "LIB";
				content = StringUtil.replace(content, DEPENDENCIES, dependency + "\r\n");
			}
			appendFileList("source\\", sourceFiles, "resources\\", resourceFiles, hasLibraries, mainProjectBuffer, libraryProjectBuffer);
			content = StringUtil.replace(content, DEPENDENCIES, dependency );
			content = StringUtil.replace(content, FILES, mainProjectBuffer.toString() );
			writeFile(new File(fullPath),content);
			
			if (hasLibraries) {
				content = TEMPLATE + "Type=2\n";
				// add a new project and add dependencies:
				String library = processImportList(root);
				libraryProjectBuffer.append(library);
				content = StringUtil.replace(content, ICONS, "" );
				content = StringUtil.replace(content, ICONS_ROLLOVER, "" );
				content = StringUtil.replace(content, NAME, dependency);
				content = StringUtil.replace(content, DEPENDENCIES, "" );
				content = StringUtil.replace(content, FILES, libraryProjectBuffer.toString() );
				writeFile( new File( root + dependency + ".jdp"), content);
			}
			
		} catch (IOException e) {
			throw new BuildException(e);
		}
	}

	private String addIcons(String content, String rootDir) {
		Environment env = Environment.getInstance();
		String icons = env.getVariable("blackberry.Icons");
		String resourceDir = env.getDevice().getResourceDir().getAbsolutePath();
		resourceDir = StringUtil.replace(resourceDir, rootDir, "");
		if (icons != null) {
			StringBuffer sb = new StringBuffer();
			String[] iconNames = StringUtil.splitAndTrim(icons, ',');
			for (int i = 0; i < iconNames.length; i++) {
				String iconName = iconNames[i];
				sb.append(resourceDir).append(File.separatorChar).append(iconName).append("\r\n");
			}
			content = content.replace(ICONS, sb.toString());
		} else {
			content = content.replace(ICONS, resourceDir + (String)env.get("build.icon") + "\r\n");
		}
		icons = env.getVariable("blackberry.RolloverIcons");
		if (icons != null) {
			StringBuffer sb = new StringBuffer();
			String[] iconNames = StringUtil.splitAndTrim(icons, ',');
			for (int i = 0; i < iconNames.length; i++) {
				String iconName = iconNames[i];
				sb.append(resourceDir).append(File.separatorChar).append(iconName).append("\r\n");
			}
			content = content.replace(ICONS_ROLLOVER, sb.toString());
		} else {
			content = content.replace(ICONS_ROLLOVER, "");
		}
		return content;
	}

	private String readFile(File file) throws IOException {
		FileInputStream stream = new FileInputStream(file);
		int len = stream.available();
		StringBuffer sb = new StringBuffer();
		sb.append(" ");

		for (int i = 1; i <= len; i++) {
			sb.append((char) stream.read());
		}

		stream.close();
		return sb.toString();
	}
	
	private void writeFile(File file, String content) throws IOException
	{
		if(!file.exists())
		{
			file.createNewFile();
		}
		System.out.println("jdp: Creating " + file.getAbsolutePath() );
		FileWriter writer = new FileWriter(file);
		writer.write(content);
		writer.close();
	}
	
	private boolean hasLibraries() {
		Environment env = Environment.getInstance();
		LibrariesSetting libraries = (LibrariesSetting) env.get(LibrariesSetting.KEY_ENVIRONMENT);
		if (libraries == null) {
			return false;
		}
		LibrarySetting[] settings = libraries.getLibraries();
		for (int i = 0; i < settings.length; i++) {
			LibrarySetting library = settings[i];
			if (library.isActive(env)) {
				return true;
			}
		}
		return false;
	}

	private void appendFileList(String sourcesRoot, String[] sources, String resourcesRoot, String[] resources, boolean hasLibraries, StringBuffer mainProjectBuffer, StringBuffer libraryProjectBuffer) {
		StringBuffer sb;

		for (int i = 0; i < sources.length; i++) {
			String fileName = sources[i];
			if (fileName.charAt(0) != '.') {
				if (hasLibraries && fileName.startsWith("de\\enough\\polish\\")) {
					sb = libraryProjectBuffer;
				} else {
					sb = mainProjectBuffer;
				}
				fileName = fileName.replace("\\", "\\\\");
				sb.append(sourcesRoot);
				sb.append(fileName);
				sb.append("\r\n");
			}
		}
		sb = mainProjectBuffer;
		for (int i = 0; i < resources.length; i++) {
			String fileName = resources[i];
			if (fileName.charAt(0) != '.' && !fileName.endsWith(".class")) {
				fileName = fileName.replace("\\", "\\\\");
				sb.append(resourcesRoot);
				sb.append(fileName);
				sb.append("\r\n");
			}
		}
	}
	

	private String processImportList(String rootFolder) {
		Environment env = Environment.getInstance();
		LibrariesSetting libraries = (LibrariesSetting) env.get(LibrariesSetting.KEY_ENVIRONMENT);
		if (libraries == null) {
			// this project does not use any libraries:
			return "";
		}
		if (env.hasSymbol(Obfuscator.SYMBOL_ENVIRONMENT_HAS_BEEN_OBFUSCATED)) {
			// this project has been obfuscated, cannot create a library project:
			System.out.println("JDP: Note: project is obfuscated, the generated JDP will not contain the necessary subproject for the imported files.");
			return "";
		}
		// create import list:
		Device device = env.getDevice();
		LibrarySetting[] settings = libraries.getLibraries();
		ArrayList filesList = new ArrayList();
		for (int i = 0; i < settings.length; i++) {
			LibrarySetting library = settings[i];
			if (library.isActive(env)) {
				File dir = library.getCacheDirectory();
				addFilesRecursively( dir, device.getClassesDir(), filesList, rootFolder );
			}
		}
		File[] files = (File[]) filesList.toArray( new File[filesList.size()]);
		try {
			File sourceDir = new File( device.getClassesDir());
			File targetDir = new File( device.getBaseDir() + File.separatorChar + "libraries");
			// now preverify the library classes:
			Path bootClassPath = new Path( getProject(), device.getBootClassPath() );
			Path classPath = null;
			String classPathStr = device.getClassPath();
			if ( classPathStr != null ) {
				classPath = new Path( getProject(), classPathStr );
			}
			
			ProGuardPreverifier preverifier = new ProGuardPreverifier();
			
			preverifier.preverify( device, sourceDir, targetDir, bootClassPath, classPath );
			// now rewrite the files:
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				File newFile = new File( targetDir, file.getAbsolutePath().replace(sourceDir.getAbsolutePath(), ""));
				files[i]= newFile;
			}
			// now create the JAR file:
			JarUtil.jar(files, targetDir, new File( rootFolder + "bb-library.jar"), false );
			return "bb-library.jar\r\n";
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException("Unable to create BlackBerry JDP package for device [" + device.getIdentifier() + "]: " + e.toString() );
		}
	}


	private void addFilesRecursively(File dir, String classesDir, ArrayList filesList, String rootFolder) {
		String[] names = dir.list();
		for (int i = 0; i < names.length; i++) {
			String fileName = names[i];
			File file = new File( classesDir + File.separatorChar + fileName );
			if (file.isDirectory()) {
				addFilesRecursively(new File(dir, fileName), classesDir + File.separatorChar + fileName, filesList, rootFolder);
			} else {
				//String filePath = file.getAbsolutePath().replace(rootFolder, "").replace("\\", "\\\\");
				if (!filesList.contains(file)) {
					filesList.add(file);
				}
			}
		}
		
	}

	public String getproject() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getSources() {
		return this.sources;
	}

	public void setSources(String sources) {
		this.sources = sources.trim();
	}

	public String getTemplate() {
		return this.template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name.trim().replace(' ', '_');
	}

}
