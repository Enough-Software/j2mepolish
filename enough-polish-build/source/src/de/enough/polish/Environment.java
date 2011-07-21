/*
 * Created on 22-Apr-2005 at 16:57:20.
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.enough.polish.ant.build.BuildSetting;
import de.enough.polish.devices.LibraryManager;
import de.enough.polish.propertyfunctions.PropertyFunction;
import de.enough.polish.util.ResourceUtil;
import de.enough.polish.util.StringUtil;

/**
 * <p>
 * Contains all variables, settings, etc not only for the preprocessing, but
 * also for the various other build phases.
 * </p>
 * 
 * <p>
 * Copyright Enough Software 2005
 * </p>
 * 
 * <pre>
 *  history
 *         22-Apr-2005 - rob creation
 * </pre>
 * 
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class Environment {
	private static Environment INSTANCE;

	private static final String PROPERTY_CHARS_STR = "\\w|\\.|\\-|,|\\(|\\)|\\s|/|\\\\";

	private static final String PROPERTY_PATTERN_STR = "\\$\\{\\s*[" + PROPERTY_CHARS_STR + "]+\\s*\\}";

	protected static final Pattern PROPERTY_PATTERN = Pattern.compile(PROPERTY_PATTERN_STR);

	private static final String PROPERTY_FUNCTION_CHARS_STR = "\\w|\\.|\\-|,|\\s|/|\\s|,|\\+|\\*|:|\\\\";

	private static final String FUNCTION_PATTERN_STR = "\\w+\\s*\\(\\s*[" + PROPERTY_FUNCTION_CHARS_STR + "]+\\s*\\)";

	protected static final Pattern FUNCTION_PATTERN = Pattern.compile(FUNCTION_PATTERN_STR);

	private final Map symbols;

	private final Map variables;

	/** holds all temporary defined variables */
	private final HashMap temporaryVariables;

	/** holds all temporary defined symbols */
	private final HashMap temporarySymbols;

	private ExtensionManager extensionManager;

	private final BooleanEvaluator booleanEvaluator;

	private Locale locale;

	private Device device;

	private BuildSetting buildSetting;

	private LibraryManager libraryManager;

	private final HashMap exchangeStore;

	private Map basicProperties;

	private File baseDir;

	/**
	 * Creates a new empty environment.
	 */
	public Environment() {
		super();
		this.symbols = new HashMap();
		this.variables = new HashMap();
		this.exchangeStore = new HashMap();
		this.temporarySymbols = new HashMap();
		this.temporaryVariables = new HashMap();
		this.extensionManager = null;
		this.booleanEvaluator = new BooleanEvaluator(this);
		this.basicProperties = null;
		if (INSTANCE == null) {
			INSTANCE = this;
		}
	}
	
	/**
	 * Creates a new empty environment.
	 * @param polishHome the path to the J2ME Polish installation directory
	 */
	public Environment(File polishHome) {
		this.symbols = new HashMap();
		this.variables = new HashMap();
		this.exchangeStore = new HashMap();
		this.temporarySymbols = new HashMap();
		this.temporaryVariables = new HashMap();
		if (polishHome != null) {
			set("polish.home", polishHome);
		}
		ResourceUtil resourceUtil = new ResourceUtil( getClass().getClassLoader() );
		try {
			this.extensionManager = ExtensionManager.getInstance( polishHome, resourceUtil );
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException( "Unable to initialize the ExtensionManager: " + e );
		}
		this.booleanEvaluator = new BooleanEvaluator(this);
		this.basicProperties = null;
		INSTANCE = this;
	}


	/**
	 * Creates a new empty environment.
	 * 
	 * @param extensionsManager
	 *            the manager for extensions
	 * @param properties
	 *            basic environment settings
	 * @param baseDir
	 *            the base directory like the project's home directory
	 */
	public Environment(ExtensionManager extensionsManager, Map properties, File baseDir) {
		super();
		this.symbols = new HashMap();
		this.variables = new HashMap();
		this.exchangeStore = new HashMap();
		this.temporarySymbols = new HashMap();
		this.temporaryVariables = new HashMap();
		this.extensionManager = extensionsManager;
		this.booleanEvaluator = new BooleanEvaluator(this);
		this.variables.putAll(properties);
		this.basicProperties = properties;
		this.baseDir = baseDir;
		INSTANCE = this;
	}

	/**
	 * Retrieves the instance of the environment
	 * 
	 * @return the last instantiated instance of the environment
	 */
	public static Environment getInstance() {
		return INSTANCE;
	}

	public void initialize(Device newDevice, Locale newLocale) {
		this.device = newDevice;
		this.locale = newLocale;
		this.symbols.clear();
		this.symbols.putAll(newDevice.getFeatures());
		this.variables.clear();
		if (this.basicProperties != null) {
			this.variables.putAll(this.basicProperties);
		}
		this.variables.putAll(newDevice.getCapabilities());
		this.temporaryVariables.clear();
		this.temporarySymbols.clear();
	}

	/**
	 * @param features
	 */
	public void setSymbols(Map features) {
		this.symbols.clear();
		this.symbols.putAll(features);
	}

	/**
	 * @param capabilities
	 */
	public void setVariables(Map capabilities) {
		this.variables.clear();
		this.variables.putAll(capabilities);
	}

	public void clearTemporarySettings() {
		this.temporarySymbols.clear();
		this.temporaryVariables.clear();
	}

	/**
	 * @param name
	 * @param value
	 */
	public void addVariable(String name, String value) {
		String previousValue = (String) this.variables.get(name);
		if (previousValue != null) {
			// remove previous values from defined symbols first:
			if ("true".equals(previousValue)) {
				this.symbols.remove(name);
			}
			this.symbols.remove(name + "." + previousValue);
			String[] individualValues = StringUtil.splitAndTrim(previousValue
					.toLowerCase(), ',');
			for (int i = 0; i < individualValues.length; i++) {
				String individualValue = individualValues[i];
				this.symbols.remove(name + "." + individualValue);
			}
		}
		if (value.length() == 0) {
			this.variables.remove(name);
			this.symbols.remove(name + ":defined");
			name = name.toLowerCase();
			this.variables.remove(name);
			this.symbols.remove(name + ":defined");
			return;
		}
		this.variables.put(name, value);
		this.symbols.put(name + ":defined", Boolean.TRUE);
		name = name.toLowerCase();
		this.variables.put(name, value);
		this.symbols.put(name + ":defined", Boolean.TRUE);
		value = value.toLowerCase();
		if ("true".equals(value)) {
			this.symbols.put(name, Boolean.TRUE);
		}
		String[] individualValues = StringUtil.splitAndTrim(value, ',');
		for (int i = 0; i < individualValues.length; i++) {
			String individualValue = individualValues[i];
			this.symbols.put(name + "." + individualValue, Boolean.TRUE);
		}
	}

	public String removeVariable(String name) {
		this.variables.remove(name);
		this.temporaryVariables.remove(name);
		this.variables.remove(name + ":defined");
		this.temporaryVariables.remove(name + ":defined");
		name = name.toLowerCase();
		String value = (String) this.variables.remove(name);
		String tempValue = (String) this.temporaryVariables.remove(name);
		if (value == null) {
			value = tempValue;
		}
		if (value != null) {
			this.symbols.remove(name + ":defined");
			this.temporarySymbols.remove(name + ":defined");
			String[] individualValues = StringUtil.splitAndTrim(value
					.toLowerCase(), ',');
			for (int i = 0; i < individualValues.length; i++) {
				String individualValue = name + "." + individualValues[i];
				this.symbols.remove(individualValue);
				this.temporarySymbols.remove(individualValue);
			}
		}
		return value;
	}

	/**
	 * @param name
	 * @param value
	 */
	public void setVariable(String name, String value) {
		removeVariable(name);
		addVariable(name, value);
	}

	public String removeTemporaryVariable(String name) {
		name = name.toLowerCase();
		String value = (String) this.temporaryVariables.remove(name);
		if (value != null) {
			this.temporarySymbols.remove(name + ":defined");
			String[] individualValues = StringUtil.splitAndTrim(value
					.toLowerCase(), ',');
			for (int i = 0; i < individualValues.length; i++) {
				String individualValue = name + "." + individualValues[i];
				this.temporarySymbols.remove(individualValue);
			}
		}
		return value;
	}

	public String getVariable(String name) {
		String value = (String) this.variables.get(name);
		if (value == null) {
			name = name.toLowerCase();
			value = (String) this.variables.get(name);
			if (value == null) {
				value = (String) this.temporaryVariables.get(name);
			}
		}
		return value;
	}

	public void addTemporaryVariable(String name, String value) {
		this.temporaryVariables.put(name, value);
		this.temporarySymbols.put(name + ":defined", Boolean.TRUE);
		name = name.toLowerCase();
		this.temporaryVariables.put(name, value);
		this.temporarySymbols.put(name + ":defined", Boolean.TRUE);
		value = value.toLowerCase();
		String[] individualValues = StringUtil.splitAndTrim(value, ',');
		for (int i = 0; i < individualValues.length; i++) {
			String individualValue = individualValues[i];
			this.temporarySymbols.put(name + "." + individualValue,
					Boolean.TRUE);
		}
	}

	/**
	 * @param name
	 */
	public void addSymbol(String name) {
		// if ( name.indexOf("screen-change-animation") != -1) {
		// System.out.println("ADDING SYMBOL "+ name);
		// throw new IllegalArgumentException("hier");
		// }
		this.symbols.put(name, Boolean.TRUE);
		name = name.toLowerCase();
		this.symbols.put(name, Boolean.TRUE);
	}

	public boolean removeSymbol(String name) {
		this.symbols.remove(name);
		this.temporarySymbols.remove(name);
		name = name.toLowerCase();
		boolean removed = this.symbols.remove(name) != null || this.temporarySymbols.remove(name) != null;
		return removed;
	}

	public boolean hasSymbol(String name) {
		name = name.toLowerCase();
		// System.out.println( "Environment: hasSymbol(" + name + ") = " + (
		// this.symbols.get(name) != null ) );
		// if ( name.indexOf("screen-change-animation") != -1) {
		// System.out.println("this.symbols.get(" + name + ")=" + (
		// this.symbols.get(name) != null) );
		// System.out.println("this.temporarySymbols.get(" + name + ")=" + (
		// this.temporarySymbols.get(name) != null) );
		// }
		return (this.symbols.get(name) != null || this.temporarySymbols
				.get(name) != null) || "true".equals(getVariable(name));
	}

	public void addTemporarySymbol(String name) {
		name = name.toLowerCase();
		this.temporarySymbols.put(name, Boolean.TRUE);
	}

	public boolean removeTemporarySymbol(String name) {
		name = name.toLowerCase();
		return this.temporarySymbols.remove(name) != null;
	}

	public Locale getLocale() {
		return this.locale;
	}

	/**
	 * Inserts the property-values in a string with property-definitions.
	 * 
	 * @param input
	 *            the string in which property definition might be included,
	 *            e.g. "file=${source}/MyFile.java"
	 * @return the input with all properties replaced by their values. When a
	 *         property is not defined the full property-name is inserted
	 *         instead (e.g. "${ property-name }").
	 */
	public String writeProperties(String input) {
		return writeProperties(input, false);
	}

	/**
	 * Inserts the property-values in a string with property-definitions.
	 * 
	 * @param input
	 *            the string in which property definition might be included,
	 *            e.g. "file=${source}/MyFile.java"
	 * @param needsToBeDefined
	 *            true when an IllegalArgumentException should be thrown when no
	 *            value for a property was found.
	 * @return the input with all properties replaced by their values. When a
	 *         property is not defined (and needsToBeDefined is false), the full
	 *         property-name is inserted instead (e.g. "${ property-name }").
	 * @throws IllegalArgumentException
	 *             when a property-value was not found and needsToBeDefined is
	 *             true.
	 */
	public String writeProperties(String input, boolean needsToBeDefined) {
		if (input == null) {
			throw new IllegalArgumentException(
					"internal error: input cannot be null.");
		}
		Matcher matcher = PROPERTY_PATTERN.matcher(input);
		boolean propertyFound = matcher.find();
		if (!propertyFound) {
			return input;
		}
		/*
		 * StringBuffer buffer = new StringBuffer(); int startPos = 0;
		 */
		String lastGroup = null;
		while (propertyFound) {
			// append string til start of the pattern:
			// buffer.append( input.substring( startPos, matcher.start() ) );
			// startPos = matcher.end();
			// append property:
			String group = matcher.group(); // == ${ property.name }
			// or == ${ function( property.name ) }
			// or == ${ function( fix.value ) }
			if (group.equals(lastGroup)) {
				if (matcher.find()) {
					group = matcher.group();
				} else {
					break;
				}
			}
			lastGroup = group;

			String property = group.substring(2, group.length() - 1).trim(); // ==
																				// property.name

			String value = getProperty(property, needsToBeDefined);
			if (value != null) {
				// We had an endless loop when '${foo}' got replaced by
				// '${foo}'.
				if (value.equals("${" + property + "}")) {
					System.err.println("WARNING: replacing " + value + " with "
							+ value);
					break;
				}
				input = StringUtil.replace(input, group, value);
				matcher = PROPERTY_PATTERN.matcher(input);
			}
			propertyFound = matcher.find();
		}
		/*
		 * // the property-name can also include a convert-function, e.g. bytes(
		 * polish.HeapSize ) int functionStart = property.indexOf('('); if
		 * (functionStart != -1) { int functionEnd = property.indexOf(')',
		 * functionStart); if (functionEnd == -1) { throw new
		 * IllegalArgumentException("The function [" + property + "] needs a
		 * closing paranthesis in input [" + input + "]."); } String
		 * functionName = property.substring(0, functionStart).trim(); property =
		 * property.substring( functionStart + 1, functionEnd ).trim(); String
		 * originalValue = getVariable( property ); if (originalValue == null) { //
		 * when functions are used, fix values can be used, too: originalValue =
		 * property; }
		 * 
		 * Object intermediateValue = ConvertUtil.convert( originalValue,
		 * functionName, this.variables); value =
		 * ConvertUtil.toString(intermediateValue); } else { value =
		 * getVariable( property ); } if (value == null) { if (needsToBeDefined) {
		 * throw new IllegalArgumentException("property " + group + " is not
		 * defined."); } else { value = group; } } else { if (
		 * value.indexOf("${") != -1) { Matcher valueMatcher =
		 * PROPERTY_PATTERN.matcher( value ); while ( valueMatcher.find() ) {
		 * String internalGroup = valueMatcher.group(); String internalProperty =
		 * internalGroup.substring( 2, internalGroup.length() -1 ).trim(); // ==
		 * property.name String internalValue = getVariable( internalProperty );
		 * if (internalValue != null) { value = StringUtil.replace( value,
		 * internalGroup, internalValue); } else if ( needsToBeDefined ) { throw
		 * new IllegalArgumentException("property " + internalGroup + " is not
		 * defined."); } }
		 *  } } buffer.append( value ); // look for another property:
		 * propertyFound = matcher.find(); } // append tail: buffer.append(
		 * input.substring( startPos ) ); return buffer.toString();
		 */
		return input;
	}

	/**
	 * Retrieves the given property.
	 * 
	 * @param property
	 *            the name of the property
	 * @param needsToBeDefined
	 *            true when an exception should be thrown when the property is
	 *            not defined
	 * @return the found property or null when it is not found
	 */
	public String getProperty(String property, boolean needsToBeDefined) {
		// System.out.println("getProperty for " + property);
		if (property.indexOf('(') == -1) {
			// the property does not contain a property-function:
			String value = getVariable(property);
			if (value == null) {
				if (needsToBeDefined) {
					throw new IllegalArgumentException("The property ["
							+ property + "] is not defined.");
				} else {
					return null;
				}
			} else {
				property = value;
			}
		} else {
			// the property contains a property-function:
			Matcher matcher = FUNCTION_PATTERN.matcher(property);
			while (matcher.find()) {
				String group = matcher.group(); // == function ( propertyname [,
												// arg, arg, ...] )
				int propertyNameStart = group.indexOf('(');
				int propertyNameEnd = group.indexOf(',', propertyNameStart);
				boolean hasParameters = true;
				if (propertyNameEnd == -1) {
					propertyNameEnd = group.length() - 1;
					hasParameters = false;
				}
				String propertyName = group.substring(propertyNameStart + 1,
						propertyNameEnd).trim();
				String propertyValue = getVariable(propertyName);
				if (propertyValue == null) {
					// if (needsToBeDefined) {
					// throw new IllegalArgumentException("The property [" +
					// propertyName + "] is not defined.");
					// } else {
					propertyValue = propertyName;
					// }
				}
				String[] parameters = null;
				if (hasParameters) {
//					String parametersStr = property.substring(
//							propertyNameEnd + 1, property.length() - 1).trim();
					String parametersStr = group.substring(
							propertyNameEnd + 1, group.length() - 1).trim();
					parameters = StringUtil.splitAndTrim(parametersStr, ',');
				}
				String functionName = group.substring(0, propertyNameStart)
						.trim();
				PropertyFunction function = null;
				try {
					function = (PropertyFunction) this.extensionManager
							.getExtension(
									ExtensionManager.TYPE_PROPERTY_FUNCTION,
									functionName, this);
				} catch (Exception e) {
					e.printStackTrace();
					throw new IllegalArgumentException(
							"The property function ["
									+ functionName
									+ "] could not be loaded. Please register it in custom-extensions.xml.");
				}
				if (function == null) {
					throw new IllegalArgumentException(
							"The property function ["
									+ functionName
									+ "] is not known. Please register it in custom-extensions.xml.");
				}
				// now ask the function whether it needs a defined property
				// value:
				if (propertyValue == null
						&& function.needsDefinedPropertyValue()) {
					throw new IllegalArgumentException("The property ["
							+ propertyName + "] is not defined.");
				}
				try {
					String replacement = function.process(propertyValue, parameters, this);
					property = StringUtil.replace(property, group, replacement);
					matcher = FUNCTION_PATTERN.matcher(property);
				} catch (RuntimeException e) {
					e.printStackTrace();
					System.out.println("Unable to process function ["
							+ functionName + "] on value [" + propertyValue
							+ "]: " + e.toString());
					throw e;
				}
			}
		}
		return property;
	}

	/**
	 * @param locale
	 *            the locale
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public BooleanEvaluator getBooleanEvaluator() {
		return this.booleanEvaluator;
	}

	/**
	 * Retrieves all defined variables (capabilities) for the current device and
	 * this project.
	 * 
	 * @return all defined variables
	 */
	public Map getVariables() {
		return this.variables;
	}

	/**
	 * @param additionalSymbols
	 */
	public void addSymbols(Map additionalSymbols) {
		this.symbols.putAll(additionalSymbols);
	}

	public Device getDevice() {
		return this.device;
	}

	/**
	 * @param vars
	 */
	public void addVariables(Map vars) {
		for (Iterator iter = vars.entrySet().iterator(); iter.hasNext();) {
			Map.Entry entry = (Map.Entry) iter.next();
			String name = (String) entry.getKey();
			String value = (String) entry.getValue();
			addVariable(name, value);
		}
	}

	/**
	 * Resolves the path to a file.
	 * 
	 * @param url
	 *            the filepath that can contain properties such as
	 *            ${polish.home}.
	 * @return the appropriate file. Please note that the file doesn't need to
	 *         exist, call file.exists() for determining that.
	 */
	public File resolveFile(String url) {
		url = writeProperties(url);
		File file = new File(url);
		if (!file.isAbsolute()) {
			file = new File(this.baseDir, url);
		}
		return file;
	}

	public File getBaseDir() {
		return this.baseDir;
	}

	/**
	 * @param setting
	 */
	public void setBuildSetting(BuildSetting setting) {
		this.buildSetting = setting;
	}

	public BuildSetting getBuildSetting() {
		return this.buildSetting;
	}

	public void setLibraryManager(LibraryManager manager) {
		this.libraryManager = manager;
	}

	public LibraryManager getLibraryManager() {
		return this.libraryManager;
	}

	/**
	 * Retrieves all symbols.
	 * 
	 * @return a map containing the names of all defined symbols. The value for
	 *         the symbols is always Boolean.TRUE
	 */
	public Map getSymbols() {
		return this.symbols;
	}

	/**
	 * Sets any object to this environment. This eases communication between
	 * different components.
	 * 
	 * @param key
	 *            the key under which the value is stored
	 * @param value
	 *            the value
	 * @see #get( String )
	 */
	public void set(String key, Object value) {
		this.exchangeStore.put(key, value);
	}

	/**
	 * Retrieves any object to this environment. This eases communication
	 * between different components.
	 * 
	 * @param key
	 *            the key under which the value is stored
	 * @return the object that has been stored previously, or null when none has
	 *         been set
	 * @see #set( String, Object )
	 */
	public Object get(String key) {
		return this.exchangeStore.get(key);
	}

	/**
	 * Checks whether a condition is met.
	 * 
	 * @param condition
	 *            the condition
	 * @return true when the given condition is met by this environment
	 */
	public boolean isConditionFulfilled(String condition) {
		if (condition == null) {
			return true;
		}
		return this.booleanEvaluator.evaluate(condition, "Environment", 0);
	}

	/**
	 * Retrieves the variables and writes any properties into it's value.
	 * 
	 * @param name
	 *            the name of the variable
	 * @return the value of the variable that doesn't include any properties
	 */
	public String resolveVariable(String name) {
		String value = getVariable(name);
		if (value != null && value.indexOf('$') != -1) {
			value = writeProperties(value);
		}
		return value;
	}

	/**
	 * Determines whether a variable is defined.
	 * 
	 * @param name
	 *            the name of the variable
	 * @return true when the variable is defined
	 */
	public boolean hasVariable(String name) {
		return getVariable(name) != null;
	}

	/**
	 * Puts all properties of the given map into the internal map without
	 * conversion.
	 * 
	 * @param properties
	 *            a map of properties
	 */
	public void putAll(Map properties) {
		this.variables.putAll(properties);
	}

	/**
	 * Retrieves the project's home dir.
	 * 
	 * @return the current project's base directory, the working dir when there
	 *         is no Ant project attached.
	 */
	public File getProjectHome() {
		if (this.baseDir != null) {
			return this.baseDir;
		}
		File home = (File) get("project.home");
		if (home == null) {
			home = new File(".");
			set("project.home", home);
		}
		return home;
	}

	/**
	 * Sets the project's main directory.
	 * 
	 * @param baseDir the project's home directory 
	 */
	public void setBaseDir(File baseDir) {
		this.baseDir = baseDir;
		if (baseDir != null) {
			set( "project.home", baseDir );
		}
	}

	/**
	 * Sets the extension manager.
	 * 
	 * @param manager the manager
	 */
	public void setExtensionManager(ExtensionManager manager) {
		this.extensionManager = manager;
	}
	
	/**
	 * Sets the basic properties which are available the whole time.
	 * @param properties the properties
	 */
	public void setBaseProperties( Map properties ) {
		this.variables.putAll(properties);
		this.basicProperties = properties;
	}

	/**
	 * Adds the given value to the specified variable.
	 * If the variable is already present the value will be added with a comma as a separator
	 * @param name name of the variable
	 * @param value value of the variable
	 */
	public void addToVariable(String name, String value) {
		String existing = (String) this.variables.remove(name);
		if (existing != null) {
			addVariable(name, existing + "," + value);
		} else {
			addVariable(name, value);
		}
		
	}
	
 }
