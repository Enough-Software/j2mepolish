/*
 * Created on 04-Jul-2005 at 18:21:43.
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
package de.enough.polish.preprocess.custom;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.Project;
import org.jdom.Element;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ExtensionDefinition;
import de.enough.polish.ExtensionManager;
import de.enough.polish.ExtensionSetting;
import de.enough.polish.ExtensionTypeDefinition;
import de.enough.polish.preprocess.CustomPreprocessor;
import de.enough.polish.util.StringList;
import de.enough.polish.util.StringUtil;
import de.enough.polish.util.TextFileManager;

public class ImportPreprocessor extends CustomPreprocessor {
	
	/**
	 * Constant for retrieving imports
	 */
	public final static String KEY_IMPORT_MAP = "import.Map";
	protected static final Pattern IMPORT_PATTERN = Pattern.compile( "import\\s+[\\w|\\.|\\*]+\\s*;" );
	private final Map mappingsByCondition;
	private String[] conditions;
	
	private Map currentImportMappings;
	private boolean useDefaultPackage;

	public ImportPreprocessor() {
		super();
		this.mappingsByCondition = new HashMap();
	}

	protected void init(ExtensionTypeDefinition typeDefinition, ExtensionDefinition definition, ExtensionSetting setting, Project project, ExtensionManager manager, Environment env) {
		super.init(typeDefinition, definition, setting, project, manager, env);
		Element definitionElement = definition.getElement();
		List mappingsList = definitionElement.getChildren( "mappings" );
		this.conditions = new String[ mappingsList.size() ];
		int i = 0;
		for (Iterator iter = mappingsList.iterator(); iter.hasNext();) {
			Element mappingsElement = (Element) iter.next();
			Map mappingsByImport = new HashMap();
			List mappingList = mappingsElement.getChildren( "mapping" );
			for (Iterator iterator = mappingList.iterator(); iterator.hasNext();) {
				Element mappingElement = (Element) iterator.next();
				String from = mappingElement.getAttributeValue("from");
				if (from == null) {
					throw new IllegalArgumentException("Invalid import mapping without \"from\" in extensions.xml: " + definitionElement );
				}
				String to = mappingElement.getAttributeValue("to");
				if ( to == null ) {
					to = mappingElement.getChildTextTrim("to");
					if ( to == null ) {
						throw new IllegalArgumentException("Invalid import mapping without \"to\" in extensions.xml: " + definitionElement );
					}
					to = to.replace('\n', ' ');
				}
				mappingsByImport.put( from, to );
			}
			String condition = mappingsElement.getAttributeValue("if");
			this.conditions[i] = condition;
			if ( condition == null ) {
				throw new IllegalArgumentException("Invalid import mappings without \"if\" in extensions.xml: " + definitionElement );
			}
			this.mappingsByCondition.put( condition, mappingsByImport );
			i++;
		}
	}
	
	protected Map getImportMappings() {
		Map mappings = new HashMap();
		for (int i = 0; i < this.conditions.length; i++) {
			String condition = this.conditions[i];
			if ( this.environment.isConditionFulfilled(condition) ) {
				//System.out.println("ImportMapper: condition fulfilled: " + condition );
				Map importMappings = (Map) this.mappingsByCondition.get( condition );
				mappings.putAll( importMappings );
			}
		}
		return mappings;
	}
	
	

	public void notifyDevice(Device device, boolean usesPolishGui) {
		super.notifyDevice(device, usesPolishGui);
		this.currentImportMappings = getImportMappings();
		this.useDefaultPackage = this.environment.hasSymbol("polish.useDefaultPackage");
		
		device.getEnvironment().set( KEY_IMPORT_MAP, this.currentImportMappings );

	}

	public void processClass(StringList sourceCode, String className) {
		TextFileManager textFileManager = this.preprocessor.getTextFileManager();
		// go through the code and search for import statements:
		while (sourceCode.next()) {
			String line = sourceCode.getCurrent().trim();
			if ( this.useDefaultPackage && line.startsWith("package ")) {
				line = "// " + line;
				sourceCode.setCurrent( line );
			} else if (line.indexOf("import") != -1) {
				Matcher matcher = IMPORT_PATTERN.matcher( line );
				boolean changed = false;
				while (matcher.find()) {
					String importStatement = matcher.group();
					String importContent = importStatement.substring( "import ".length(), importStatement.length() -1 ).trim();
					String replacement = (String) this.currentImportMappings.get(importContent);
					if ( replacement != null ) {
						changed = true;
						if ( replacement.length() == 0 ) {
							line = "//" + line;
						} else {
							line = StringUtil.replace( line, importStatement, "import " + replacement + ";" );
						}
					} else if ( this.useDefaultPackage && textFileManager.containsImport(importContent) ) {
						changed = true;
						line = "//" + line;
					}
				}
				if ( changed ) {
					sourceCode.setCurrent( line );
				}			
			} else if (line.startsWith("public class ")) {
				break;
			} else if (line.startsWith("protected class ")) {
				break;
			} else if (line.startsWith("class ")) {
				break;
			} else if (line.startsWith("public interface ")) {
				break;
			} else if (line.startsWith("protected interface ")) {
				break;
			} else if (line.startsWith("interface ")) {
				break;
			}
		}
	}
	
	

}
