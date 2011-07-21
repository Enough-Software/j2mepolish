/*
 * Created on Feb 8, 2008 at 12:01:12 PM.
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
package de.enough.polish.finalize.mea;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import de.enough.polish.BuildException;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.devices.DeviceDatabase;
import de.enough.polish.finalize.Finalizer;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.StringUtil;

/**
 * Creates a Media Archive file (.mea) from all applications which are build.
 * The process has two stages. The first stage is to collect all information about
 * the created jar/jad files. The second stages is triggered at the end of the build
 * to create the actual media archive.
 * 
 * <br>Copyright Enough Software 2005-2007
 * <pre>
 * history
 *        Feb 8, 2008 - rickyn creation
 * </pre>
 * @author Richard Nkrumah
 */
public class MeaFinalizer extends Finalizer{

    private static class Entry{
        private File jadFile;
        private File jarFile;
        private Device device;
        private String croppedJadFilename;
        private String croppedJarFilename;
		private String supportedLocales;
        public Entry(File jadFile, File jarFile, Device device,String supportedLocales, String croppedJadFilename,String croppedJarFilename) {
            this.jadFile = jadFile;
            this.jarFile = jarFile;
            this.device = device;
            this.supportedLocales = supportedLocales;
            this.croppedJadFilename = croppedJadFilename;
            this.croppedJarFilename = croppedJarFilename;
        }
        public Device getDevice() {
            return this.device;
        }
        public File getJadFile() {
            return this.jadFile;
        }
        public File getJarFile() {
            return this.jarFile;
        }
        public String getSupportedLocales() {
            return this.supportedLocales;
        }
        public String getCroppedJadFilename() {
            return this.croppedJadFilename;
        }
        public String getCroppedJarFilename() {
            return this.croppedJarFilename;
        }
		/**
		 * @return all supported languages as a comma separated string
		 */
		public String getSupportedLanguages()
		{
			if (this.supportedLocales == null) {
				return null;
			}
			String[] localeNames = StringUtil.splitAndTrim( this.supportedLocales, ',');
			StringBuffer languages = new StringBuffer();
			for (int i = 0; i < localeNames.length; i++)
			{
				String localeName = localeNames[i];
				int splitPos = localeName.indexOf('_');
				if (splitPos != -1) {
					languages.append( localeName.substring(0, splitPos));
				} else {
					languages.append( localeName);
				}
				if (i < localeNames.length - 1) {
					languages.append(',');
				}
			}
			return languages.toString();
		}
    }

    public MeaFinalizer() {
        super();
    }
    
    private static List entries = new ArrayList();
    private String fallbackDevice;
    private String tags;
    private String access = "owner";
    private String pseudoprivateToken = null;
	private String extension = "mea";
    
    public void finalize(File jadFile, File jarFile, Device device, Locale locale, Environment env) {
        finalize( env.getBuildSetting().getDestDir(env), jadFile, jarFile, device, locale, env  );
    }

    public void finalize(File distFile, File jadFile, File jarFile, Device device, Locale locale, Environment env) {
    	if( ! jarFile.canRead()) {
    		// In case we are called for an Android phone which do not have a .jar file.
    		return;
    	}
        String distFilePath = distFile.getAbsolutePath();
        String croppedJadFilePath = jadFile.getAbsolutePath().substring( distFilePath.length() + 1 ).replace('\\', '/');// replaceFirst(distFilePath+"/","");
        String croppedJarFilePath = jarFile.getAbsolutePath().substring( distFilePath.length() + 1 ).replace('\\', '/');// replaceFirst(distFilePath+"/","");
        String supportedLocales = null;
        if (env.hasSymbol("polish.i18n.useDynamicTranslations")) {
        	supportedLocales = env.getVariable("polish.SupportedLocales");
        } else if (locale != null) {
        	supportedLocales = locale.toString();
        }
        Entry entry = new Entry(jadFile,jarFile,device,supportedLocales,croppedJadFilePath,croppedJarFilePath);
        entries.add(entry);
    }

    public void notifyBuildEnd(Environment env) {
        super.notifyBuildEnd(env);
        notifyBuildEnd(  env.getBuildSetting().getDestDir(env), env);
        
    }
    
    public void notifyBuildEnd(File distFile, Environment env) {
        super.notifyBuildEnd(env);
        String description = env.getVariable("MIDlet-Description");
        if(description != null){
            description = escapeXml( description );
        }
        String name = env.getVariable("MIDlet-Name");
        if(name == null || name.length() == 0) {
            name = "unknown";
        } else {
        	name = escapeXml(name);
        	/* Since microsoft windows does not allow the following characters "?", "*", ":", "/", "|", "\", """, "<" and ">"
        	 * in their file names we have to parse these characters.
        	 */
        	int length = name.length();
        	char[] charArray = name.toCharArray();
        	for (int i = 0; i < length; i++) {
        		switch (charArray[i]) {
					case '?':
					case '*':
					case ':':
					case '/':
					case '\\':
					case '|':
					case '<':
					case '>':
					case '\"':
						charArray[i] = '_';
						break;
					default:
						break;
				}
        	}
        	name = new String(charArray);
        }
        String version = env.getVariable("MIDlet-Version");
        if(version == null || version.length() == 0) {
            version = "1.0.0";
        }
        String vendor = env.getVariable("MIDlet-Vendor");
        if(vendor == null || vendor.length() == 0) {
            vendor = "unknown";
        } else {
        	vendor = escapeXml(vendor);
        }
        
        // Write contents.xml file.

        FileWriter fileWriter = null;
        File contentsXmlFile = null;
        File meaFile = new File(distFile, name + "." + this.extension);
        System.out.println("Creating media archive '" + meaFile.getName() + "' with " + this.access + " access.");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(meaFile);
            ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
            
            contentsXmlFile = File.createTempFile("contentxml","");
            fileWriter = new FileWriter(contentsXmlFile);
            String lineSeperator = System.getProperty("line.separator");
            fileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+lineSeperator);
            fileWriter.write("<contents version=\"1.0.0\" lang=\"en\">"+lineSeperator);
            fileWriter.write("<application name=\"");
            fileWriter.write(name);
            fileWriter.write("\" description=\"");
            if (description != null) {
            	fileWriter.write(description);
            } else {
            	fileWriter.write("Build with J2ME Polish");
            }
            fileWriter.write("\" access=\""+this.access+"\" ");
            if(this.pseudoprivateToken != null && this.pseudoprivateToken.length() != 0) {
                if( ! "pseudoprivate".equals(this.access)) {
                    throw new BuildException("The parameter 'pseudoprivate' of the .mea lifecycle manager must not be set if the access restriction is not set to 'pseudoprivate'.");
                }
                fileWriter.write("pseudoprivate=\"");
                fileWriter.write(this.pseudoprivateToken);
                fileWriter.write("\" ");
            }
            fileWriter.write("fallbackDevice=\"");
            if(this.fallbackDevice == null) {
                this.fallbackDevice = "Generic/AnyPhone";
            }
            fileWriter.write(this.fallbackDevice);
            fileWriter.write("\" version=\"");
            fileWriter.write(version);
            if(this.tags != null && this.tags.length() > 0) {
                fileWriter.write("\" tags=\"");
                fileWriter.write(this.tags);
            }
            fileWriter.write("\" vendor=\"");
            fileWriter.write(vendor);
            fileWriter.write("\">"+lineSeperator);
            
            for (Iterator iterator = entries.iterator(); iterator.hasNext(); ) {
                Entry entry = (Entry) iterator.next();
                
                // Preserve possible subdirectories but make the path as short as possible to put into the zip
                String croppedJadFilename = entry.getCroppedJadFilename();
                String croppedJarFilename = entry.getCroppedJarFilename();
                fileWriter.write("<javaMeBundle>"+lineSeperator);
                fileWriter.write("\t<supportedDevices>"+lineSeperator);
                fileWriter.write("\t\t<supportedDevice>");
                fileWriter.write(entry.getDevice().getIdentifier());
                fileWriter.write("</supportedDevice>"+lineSeperator);
                fileWriter.write("\t</supportedDevices>"+lineSeperator);
                fileWriter.write("\t<data filename=\""+croppedJadFilename+"\" />"+lineSeperator);
                fileWriter.write("\t<data filename=\""+croppedJarFilename+"\" />"+lineSeperator);
//                if (entry.getSupportedLocales() != null) {
//                	fileWriter.write("\t<supportedLocales>"+ entry.getSupportedLocales() +"</supportedLocales>"+lineSeperator);
//                	fileWriter.write("\t<supportedLanguages>"+ entry.getSupportedLanguages() +"</supportedLanguages>"+lineSeperator);
//                }
                fileWriter.write("</javaMeBundle>"+lineSeperator);
                
                // Jad into the zip
                ZipEntry zipEntry;
                byte[] bytes;

                zipEntry = new ZipEntry(croppedJadFilename);
                zipEntry.setSize(entry.getJadFile().length());
                
                zipOutputStream.putNextEntry(zipEntry);
                bytes = FileUtil.getBytesFromFile(entry.getJadFile());
                zipOutputStream.write(bytes);
                
                zipEntry = new ZipEntry(croppedJarFilename);
                zipEntry.setSize(entry.getJarFile().length());
                
                zipOutputStream.putNextEntry(zipEntry);
                bytes = FileUtil.getBytesFromFile(entry.getJarFile());
                zipOutputStream.write(bytes);
            }
            fileWriter.write("</application>"+lineSeperator);
            fileWriter.write("</contents>"+lineSeperator);

            fileWriter.close();
            byte[] bytes = FileUtil.getBytesFromFile(contentsXmlFile);
            
            ZipEntry zipEntry = new ZipEntry("contents.xml");
            zipEntry.setSize(bytes.length);
            zipOutputStream.putNextEntry(zipEntry);
            zipOutputStream.write(bytes);

            zipOutputStream.close();
        } catch (IOException exception) {
            String absolutePath = "";
            if(contentsXmlFile != null) {
                absolutePath = contentsXmlFile.getAbsolutePath();
            }
            throw new BuildException("Could not write to temp file '"+absolutePath+"'",exception);
        }
        finally {
            try {
                if(fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException exception) {
                throw new BuildException("Could not flush temp file",exception);
            }
        }
        
        // Put contents.xml file in zip.
        
    }

    private String escapeXml(String input) {
        input.replaceAll("\"","&quot;");
        input = input.replaceAll("&","&amp;");
		return input;
	}

	public void setFallbackDevice(String fallbackDevice) {
        this.fallbackDevice = fallbackDevice;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
    
    public void setPseudoprivate(String pseudoprivateToken) {
        this.pseudoprivateToken  = pseudoprivateToken;
    }
    
    public void setAccess(String accessParameter) {
        boolean valid = "public".equals(accessParameter) || "owner".equals(accessParameter) || "pseudoprivate".equals(accessParameter);
        if(!valid) {
        	if ( "private".equals(accessParameter) ) {
        		accessParameter = "owner";
        	} else {
        		throw new BuildException("The parameter 'access' of the lifeCycleManager tag requires one of the values 'owner','public' or 'pseudoprivate'.");
        	}
        }
        this.access  = accessParameter;
    }
    
    public void setExtension( String extension ) {
		this.extension  = extension;
    }
    
    public static void main(String[] args)
	{
    	String polishHomeDir = System.getProperty("polish.home");
    	if (polishHomeDir == null) {
			polishHomeDir = getArgument("-polish.home", args);
			if (polishHomeDir == null) {
				System.err.println("No polish.home argument found.");
    			printUsage();
    			System.exit(1);
			}
    	}
    	File polishHome = new File( polishHomeDir );
    	DeviceDatabase deviceDb = DeviceDatabase.getInstance(polishHome);
		Environment env = new Environment(polishHome);
		MeaFinalizer mea = new MeaFinalizer();
		// add args to environment:
		int lastNonFileArgumentIndex = -1;
		for (int i = 0; i < args.length; i++)
		{
			String arg = args[i];
			if (arg.charAt(0) == '-') {
				lastNonFileArgumentIndex = i;
				int splitPos = arg.indexOf('=');
				if (splitPos == -1) {
					env.addSymbol( arg.substring(1).trim() );
				} else {
					String name = arg.substring(1, splitPos ).trim();
					String value = arg.substring( splitPos + 1).trim();
					env.addVariable(name, value);
					if (name.equals("access")) {
						mea.setAccess(value);
					} else if (name.equals("name")) {
						env.addVariable("MIDlet-Name", value);
					} else if (name.equals("description")) {
						env.addVariable("MIDlet-Description", value);
					} else if (name.equals("version")) {
						env.addVariable("MIDlet-Version", value);
					} else if (name.equals("vendor")) {
						env.addVariable("MIDlet-Vendor", value);
					} else if (name.equals("fallback")) {
						mea.setFallbackDevice(value);
					} else if (name.equals("pseudoprivate")) {
						mea.setPseudoprivate(value);
					} else if (name.equals("tags")) {
						mea.setTags(value);
					}
				}
			}
		}
		if (lastNonFileArgumentIndex == -1 || lastNonFileArgumentIndex == args.length -1 ) {
			System.out.println("No files arguments found.");
			printUsage();
			System.exit(1);
		}
		File jar = null;
		File jad = null;
		mea.notifyBuildStart(env);
		for (int i=lastNonFileArgumentIndex + 1; i<args.length; i += 2) {
			String fileReference = args[i];
			if (fileReference.endsWith(".jad")) {
				jad = new File( fileReference );
				jar = new File( fileReference.substring(0, fileReference.length() - ".jad".length() ) + ".jar");
			} else if (fileReference.endsWith(".jar")){
				jar = new File( fileReference );
				jad = new File( fileReference.substring(0, fileReference.length() - ".jar".length() ) + ".jad");				
			} else {
				System.err.println("Invalid file reference: " + fileReference + " - only jad and jar files are supported.");
				printUsage();
				System.exit(2);
			}
			if (i == args.length -1) {
				System.err.println("No device found after file reference: " + fileReference);
				printUsage();
				System.exit(3);				
			}
			String deviceName = args[i+1];
			Device device = deviceDb.getDevice(deviceName);
			if (device == null) {
				System.err.println("Unable to resolve device " + deviceName );
				printUsage();
				System.exit(4);								
			}
			mea.finalize(jad.getParentFile(), jad, jar, device, null, env);
		}
		mea.notifyBuildEnd(jad.getParentFile(),  env);
	}

	/**
	 * @param string
	 * @param args
	 * @return
	 */
	private static String getArgument(String name, String[] args)
	{
		for (int i = 0; i < args.length; i++)
		{
			String arg = args[i];
			if (arg.startsWith(name)) {
				int splitPos = arg.indexOf('=');
				if (splitPos == -1) {
					if (i < args.length - 1) {
						return args[i+1];
					} else {
						return null;
					}
				} else {
					return arg.substring(splitPos + 1 ).trim();
				}
			}
		}
		return null;
	}

	/**
	 * 
	 */
	private static void printUsage()
	{
		System.out.println("Usage: java -cp classpath de.enough.polish.finalize.mea.MeaFinalizer -polish.home=path/to/polish -access=[private|public|pseudoprivate]  -name=name -description=description -fallback=[fallbackdevice] -tags=tags file1 device1..fileN deviceN");
	}
    
}
