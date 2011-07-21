//#condition polish.usePolishGui
/*
 * Copyright (c) 2009 Robert Virkus / Enough Software
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

package de.enough.polish.qualitycontrol;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.enough.polish.util.FileUtil;

/**
 * @author robertvirkus
 *
 */
public class VariablesFinder {
	
//	private final Map deviceCapabilities;
	private final File sourcesHome;
	private final Map foundVariables;
	private boolean isVerbose;
	private Map defaultTranslations;



	public VariablesFinder( String polishHome, String j2meSourcesHome ) throws IOException {
//		this.deviceCapabilities = new HashMap();
//		DeviceDatabase deviceDatabase = DeviceDatabase.getInstance( new File(polishHome));
//		Device[] devices = deviceDatabase.getDevices();
//		for (int i = 0; i < devices.length; i++) {
//			Device device = devices[i];
//			this.deviceCapabilities.putAll( device.getCapabilities() );
//		}
		this.sourcesHome = new File( j2meSourcesHome );
		this.foundVariables = new HashMap();
		this.defaultTranslations = FileUtil.readProperties( new File( polishHome + File.separator + "translations.txt"));
	}

	public void find() throws IOException {
		String[] fileNames = FileUtil.filterDirectory(this.sourcesHome, ".java", true);
		//String[] fileNames = new String[] { "de/enough/polish/ui/Screen.java" };
		for (int i = 0; i < fileNames.length; i++) {
			String fileName = fileNames[i];
			if (this.isVerbose) {
				System.out.println("Parsing " + fileName);
			}
			processFile( new File( this.sourcesHome, fileName), fileName );
		}
	}
	
	private void processFile(File file, String relativePath) throws IOException {
		String[] lines = FileUtil.readTextFile(file);
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
//			int dollarPos = line.indexOf('$');
//			if (dollarPos == -1) {
//				continue;
//			}
			int pos = line.indexOf("polish.");
			if (pos != -1 && line.trim().charAt(0) == '*') {
				continue;
			}
			while (pos != -1) {
				char before = line.charAt(pos-1);
				if (!isStopSign(before)) {
					break;
				}
				StringBuffer varNameBuffer = new StringBuffer();
				varNameBuffer.append("polish.");
//				int originalStartPos = pos;
				pos += "polish.".length(); 
				char c = line.charAt( pos );
				while (!isStopSign(c) && pos < line.length()) {
					varNameBuffer.append(c);
					pos++;
					if ( pos < line.length()) {
						c = line.charAt( pos );
					}
				}
				String varName = varNameBuffer.toString();
				if (varName.startsWith("polish.css") || varName.startsWith("polish.Bugs.") || (varName.startsWith("polish.api.")) 
						|| (varName.startsWith("polish.cldc")) || (varName.startsWith("polish.midp")) || (varName.startsWith("polish.debug"))
						|| ("polish.usePolishGui".equals(varName)) || ("polish.android".equals(varName)) || ("polish.android".equals(varName))) 
				{
					break;
				}
//				if (!this.deviceCapabilities.containsKey(varName)) {
					// found a real variable!
					if (this.isVerbose) {
						System.out.println("Found variable: " + varName + " in line " + (i+1) + ": " +  line + " at col " + pos);
					}
					addVariable( varName, file, relativePath );
//				} else if (this.isVerbose) {
//					System.out.println("Found device capability usage: " + varName);
//				}
				pos = line.indexOf("polish.", pos );
			}
			
		}
	}

	private void addVariable(String varName, File sourceFile, String relativePath) {
		Map appliesToMap = (Map) this.foundVariables.get(varName);
		if (appliesToMap == null) {
			appliesToMap = new HashMap();
			this.foundVariables.put( varName, appliesToMap );
		}
		String fileName;
		if ( (File.separatorChar == '/' && relativePath.indexOf( "polish/ui/") != -1)
				|| (File.separatorChar == '\\' && relativePath.indexOf( "polish\\ui\\") != -1) )
		{
			fileName = sourceFile.getName().substring( 0, sourceFile.getName().length() - ".java".length() );
		} else {
			fileName = relativePath.replace(File.separatorChar, '.').substring(0, relativePath.length() - ".java".length() );
		}
		appliesToMap.put( fileName, Boolean.TRUE );
	}

	private boolean isStopSign(char c) {
		return (c == ' ') || (c == '{') || (c == '}') || (c == '\t') || (c == '"') || (c == ':') || (c == '(') || (c == ')') || (c == '=') || (c == '>') || (c == '<') || (c == ',') || (c == '\\');
	}
	

	

	public void store(File file) throws IOException {
		ArrayList lines = new ArrayList();
		lines.add("<variables>");
		Object[] keys = this.foundVariables.keySet().toArray();
		Arrays.sort(keys);
		for (int i = 0; i < keys.length; i++) {
			String varName = (String) keys[i];
			Map appliesToMap = (Map) this.foundVariables.get(varName);
			addVariable( varName, appliesToMap, lines);
		}
		lines.add("</variables>");
		FileUtil.writeTextFile(file, lines);
	}

	private void addVariable(String varName, Map appliesToMap, ArrayList lines) {
		StringBuffer buffer = new StringBuffer();
		lines.add("\t<variable " );
		lines.add("\t\tname=\"" + varName + "\"");
		String defaultValue = (String) this.defaultTranslations.get(varName);
		boolean isTranslation = defaultValue != null;
		if (isTranslation) {
			lines.add("\t\ttype=\"translation\"");
			lines.add("\t\tdefault=\"" + defaultValue + "\"");
		} else if (varName.startsWith("polish.color.")){
			lines.add("\t\ttype=\"color\"");
			lines.add("\t\tdescription=\"Color defined in the colors section of polish.css.\"");
			lines.add("\t\tdefault=\"\"");
		} else if (varName.indexOf("class") != -1) {
			lines.add("\t\ttype=\"class\"");
			lines.add("\t\tdescription=\"Fully qualified name of class.\"");
			lines.add("\t\tdefault=\"\"");
			lines.add("\t\tcondition=\"\"");
		} else if ( varName.indexOf("nterval") != -1) {
			lines.add("\t\ttype=\"time\"");
			lines.add("\t\tdescription=\"Interval in milliseconds for.\"");
			lines.add("\t\tdefault=\"\"");
		} else if ( varName.indexOf("Key") != -1 || varName.indexOf("key") != -1) {
			lines.add("\t\ttype=\"key\"");
			lines.add("\t\tdescription=\"Key definition as integer value.\"");
			lines.add("\t\tdefault=\"\"");
			lines.add("\t\tcondition=\"\"");
		} else if ( varName.indexOf("additionalMethods") != -1 || varName.indexOf("imports") != -1) {
			lines.add("\t\ttype=\"source\"");
			lines.add("\t\tdescription=\"Path to sources.\"");
			lines.add("\t\tinvisible=\"true\"");
		} else {
			lines.add("\t\ttype=\"\"");
			lines.add("\t\tdescription=\"\"");
			lines.add("\t\tvalues=\"\"");
			lines.add("\t\tdefault=\"\"");
			lines.add("\t\tcondition=\"\"");
		}
		buffer.append("\t\tappliesTo=\"");
		Iterator it = appliesToMap.keySet().iterator();
		while (it.hasNext()) {
			String className = (String) it.next();
			buffer.append(className);
			if (it.hasNext()) {
				buffer.append(", ");
			}
		}
		buffer.append("\"");
		lines.add(buffer.toString() );
		lines.add("\t/>");
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String polishHome = null;
		String j2meSourcesHome = null;
		boolean isVerbose = false;
		if (args.length > 1) {
			for (int i = 0; i < args.length; i++) {
				String arg = args[i];
				if (arg.startsWith("polish.home=")) {
					polishHome = arg.substring("polish.home=".length());
				} else if (arg.startsWith("polish.client.source=")) {
					j2meSourcesHome = arg.substring("polish.client.source=".length());
				} else if (arg.equals("-verbose")) {
					isVerbose = true;
				}
			}
		} else {
			polishHome = System.getProperty("polish.home");
			j2meSourcesHome = System.getProperty("polish.client.source");
		}
		if (polishHome == null || j2meSourcesHome == null ) {
			usage();
			System.exit(1);
		}
		if (isVerbose) {
			System.out.println("Initializing search...");
		}
		VariablesFinder finder = new VariablesFinder( polishHome, j2meSourcesHome );
		finder.isVerbose = isVerbose;
		if (isVerbose) {
			System.out.println("Searching...");
		}
		finder.find();
		System.out.println("Found " + finder.foundVariables.size() + " preprocessing variables");
		finder.store( new File( "generated-variables.xml" ));
	}


	private static void usage() {
		System.out.println("Usage:");
		System.out.println("java de.enough.polish.qualitycontrol.VariablesFinder [-verbose] polish.home=${polish.home} polish.client.source=${path to J2ME Polish sources}");
		System.out.println("Order of parameters does not matter, parameter can also be given as environment variables.");
	}

}
