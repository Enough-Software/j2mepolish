/*
 * Created on 19-Aug-2004 at 14:55:09.
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
package de.enough.polish.preprocess.css;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.enough.polish.BuildException;
import org.jdom.Element;

import de.enough.polish.BooleanEvaluator;
import de.enough.polish.Environment;
import de.enough.polish.util.CastUtil;
import de.enough.polish.util.ReflectionUtil;
import de.enough.polish.util.StringUtil;

/**
 * <p>Represents a CSS attribute.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        19-Aug-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CssAttribute
implements Comparable
{
	
	protected final static String ANIMATION_PACKAGE = "de.enough.polish.ui.cssanimations.";
	
	protected String name;
	protected String[] allowedValues;
	protected String defaultValue;
	protected String appliesTo;
	protected String description;
	protected int id;
	protected Map appliesToMap;
	protected Map mappingsByName;
	protected String group;
	protected boolean isBaseAttribute;
	protected boolean isCaseSensitive;
	protected boolean allowsCombinations;
	protected boolean requiresMapping;
	protected String type;
	private boolean isHidden;
	protected String shell;

	private boolean isImplicit;

	private String deprecatedMessage;

	protected String since;
	
	protected CssAttribute() {
		// do nothing
	}

	/**
	 * Creates a new CSS-attribute
	 *  
	 * @param definition the XML definition of this attribute.
	 */
	public CssAttribute(Element definition) {
		setDefinition( definition );
	}
	
	/**
	 * Creates a new CSS-attribute
	 *  
	 * @param definition the XML definition of this attribute.
	 */
	public void setDefinition(Element definition) {
		this.name = definition.getAttributeValue("name");
		if (this.name == null) {
			throw new BuildException("All CSS-attributes need to define the attribute [name]. Please check the files [standard-css-attributes.xml] and [custom-css-attributes.xml].");
		}
		this.type = definition.getAttributeValue("type");
		this.defaultValue = definition.getAttributeValue("default");
		if ("true".equals( definition.getAttributeValue("hidden")))  {
			this.isHidden = true;
		}
//		String typeStr = definition.getAttributeValue("type");
//		if (typeStr != null) {
//			Integer typeInt = (Integer) TYPES_MAP.get( typeStr.toLowerCase() );
//			if (typeInt == null) {
//				throw new BuildException("The CSS-attribute-type [" + typeStr + "] is not supported. It needs to be either [integer], [color], [boolean], [style] or [string]. Please check your custom-css-attributes.xml file.");
//			} else {
//				this.type = typeInt.intValue();
//			}
//		} else {
//			this.type = STRING;
//		}
		String allowedValuesStr = definition.getAttributeValue("values");
		if (allowedValuesStr != null) {
			this.allowedValues = StringUtil.splitAndTrim( allowedValuesStr, ',' );
		} else {
			this.allowedValues = null;
		}
		this.appliesTo = definition.getAttributeValue("appliesTo");
		if ( this.appliesTo != null ) {
			String[] appliesChunks = StringUtil.splitAndTrim( this.appliesTo, ',');
			this.appliesToMap = new HashMap();
			for (int i = 0; i < appliesChunks.length; i++) {
				String chunk = appliesChunks[i];
				this.appliesToMap.put( chunk, Boolean.TRUE );
			}
		} else {
			this.appliesToMap = null;
		}
		List mappingsList = definition.getChildren("mapping");
		if (mappingsList == null || mappingsList.size() == 0) {
			this.mappingsByName = null;
		} else {
			this.mappingsByName = new HashMap( mappingsList.size() );
			for (Iterator iter = mappingsList.iterator(); iter.hasNext();) {
				CssMapping mapping = createMapping( (Element) iter.next());
				this.mappingsByName.put( mapping.getFrom(), mapping );
			}
		}
		this.description = definition.getAttributeValue("description");
		String idStr = definition.getAttributeValue("id");
		if (idStr != null && !idStr.equals("none")) {
			this.id = Integer.parseInt(idStr);
		} else {
			this.id = -1;
		}
		String groupStr = definition.getAttributeValue("group");
		if (groupStr != null) {
			this.group = groupStr;
			this.isBaseAttribute = "base".equals(groupStr);
		} else {
			this.group = null;
			this.isBaseAttribute = false;
		}
		String boolStr = definition.getAttributeValue("primitive");
		if (boolStr != null) {
			this.isBaseAttribute = boolStr.equals("true");
		}
		String isCaseSensitiveStr = definition.getAttributeValue("isCaseSensitive");
		if (isCaseSensitiveStr != null) {
			this.isCaseSensitive = CastUtil.getBoolean( isCaseSensitiveStr );
		} else {
			this.isCaseSensitive = true;
		}
		String allowsCombinationsStr = definition.getAttributeValue("allowsCombinations");
		if (allowsCombinationsStr != null) {
			this.allowsCombinations = CastUtil.getBoolean( allowsCombinationsStr );
		} else {
			this.allowsCombinations = false;
		}
		String requiresMappingStr = definition.getAttributeValue("requiresMapping");
		if (requiresMappingStr != null) {
			this.requiresMapping = CastUtil.getBoolean(requiresMappingStr);
		} else {
			this.requiresMapping = false;
		}
		this.shell = definition.getAttributeValue("shell");
		this.deprecatedMessage = definition.getAttributeValue("deprecated");
		this.since = definition.getAttributeValue("since");
	}

	/**
	 * Creates a CSS mapping - can be overridden by subclasses to create specific mapping subclasses
	 * @param element the XML definition
	 * @return a new CSS mapping
	 */
	protected CssMapping createMapping(Element element) {
		return new CssMapping( element );
	}

	/**
	 * @return Returns the allowedValues.
	 */
	public String[] getAllowedValues() {
		return this.allowedValues;
	}
	/**
	 * @return Returns the defaultValue.
	 */
	public String getDefaultValue() {
		return this.defaultValue;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Checks and transforms the given CSS value for this attribute.
	 * 
	 * @param value the attribute value
	 * @param environment the environment
	 * @return the transformed value or the same value if no transformation is required.
	 * @throws BuildException when a condition is not met or when the value contains conflicting values
	 */
	public String getValue(String value, Environment environment ) {
		if (this.mappingsByName != null) {
			if (!this.allowsCombinations) {
				CssMapping mapping = getMapping(value);
				if (mapping != null) {
					mapping.checkCondition( this.name, value, environment.getBooleanEvaluator() );
					return mapping.getTo();
				} else if (this.requiresMapping) {
					throw new BuildException("Invalid CSS: the attribute \"" + this.name + "\" does not support the value \"" + value + "\".");
				} else {
					// System.out.println("returning unmapped value " + value );
					return value;
				}
			} else {
				// combinations are allowed
				if (!this.isCaseSensitive) {
					value = value.toLowerCase();
				}
				BooleanEvaluator evaluator =  environment.getBooleanEvaluator();
				value = StringUtil.replace(value, " or ", " | ");
				value = StringUtil.replace(value, " and ", " | ");
				value = StringUtil.replace(value, " || ", " | ");
				value = StringUtil.replace(value, " && ", " | ");
				value = value.replace('&', '|');
				value = value.replace(',', '|');
				String[] values = StringUtil.splitAndTrim(value, '|');
				StringBuffer convertedValueBuffer = new StringBuffer();
				for (int i = 0; i < values.length; i++) {
					String singleValue = values[i];
					CssMapping mapping = getMapping(singleValue);
					if (mapping != null) {
						mapping.checkCondition( this.name, value, evaluator );
						convertedValueBuffer.append( mapping.getTo() );
					} else if (this.requiresMapping) {
						throw new BuildException("Invalid CSS: the attribute \"" + this.name + "\" does not support the value \"" + singleValue + "\".");
					} else {
						convertedValueBuffer.append( singleValue );
					}
					if (i != values.length - 1) {
						convertedValueBuffer.append(" | ");
					}
				}
				if (this.shell != null) {
					int strtInde = this.shell.indexOf(')');
					convertedValueBuffer.insert(0, this.shell.substring(0, strtInde) );
					convertedValueBuffer.append( this.shell.substring(strtInde));
				}
				return convertedValueBuffer.toString();
//				if (this.isBaseAttribute) {
//					return convertedValueBuffer.toString();
//				} else if ( isInteger() ){
//					return "new Integer( " + convertedValueBuffer.toString() + ")";
//				}
			}
		} else if ("none".equals(value)) {
			return "null";
		}
		return value;
	}
	
	/**
	 * Instantiates the referenced value.
	 * 
	 * @param sourceCode the transformed value of this attribute
	 * @return the instantiated value (value as object instead of source code) 
	 */
	public Object instantiateValue( String sourceCode ) {
		//System.out.println("instantiate=[" + sourceCode + "] - starts with new: " + (sourceCode.startsWith("new ")) );
		if ("null".equals(sourceCode)) {
			return null;
		}
		if (sourceCode.startsWith("new ")) {
			int parenthesesPos = sourceCode.indexOf('(');
			String className = sourceCode.substring( "new ".length(), parenthesesPos );
			Class valueClass;
			try {
				valueClass = loadClass(className);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new IllegalArgumentException("Unable to instantiate " + sourceCode  + ": " + e.toString() );
			}
			String parametersStr = sourceCode.substring( parenthesesPos + 1, sourceCode.lastIndexOf(')')).trim();
			if (parametersStr.length() == 0) {
				try {
					return valueClass.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
					throw new IllegalArgumentException("Unable to instantiate " + sourceCode + ": " + e, e );
				}
			}
			String[] parameters = StringUtil.splitAndTrim(parametersStr, ',');
			// getting constructor:
			Constructor[] constructors = valueClass.getConstructors();
			for (int i = 0; i < constructors.length; i++) {
				Constructor constructor = constructors[i];
				Class[] parametersClasses = constructor.getParameterTypes();
				if (parametersClasses.length == parameters.length) {
					Object[] parameterValues = new Object[ parameters.length ];
					try {
						for (int j = 0; j < parametersClasses.length; j++) {
							Class parameterClass = parametersClasses[j];
							String parameter = parameters[j];
							parameterValues[j] = instantiate( parameterClass, parameter );
						}
						// got all parameters:
						return constructor.newInstance(parameterValues);
					} catch (IllegalArgumentException e) {
						System.out.println("Warning: unable to instantiate constructor parameter: " + e.getMessage() );
						throw new IllegalArgumentException("Unable to instantiate " + sourceCode, e );
					} catch (Exception e) {
						System.out.println("Warning: unable to instantiate constructor for value " + sourceCode + ": " + e.getMessage() );
						e.printStackTrace();
						throw new IllegalArgumentException("Unable to instantiate " + sourceCode, e );
					}
				}
			}
		}
		if (this.allowsCombinations && this.mappingsByName != null) {
			try {
				String[] parameters = StringUtil.splitAndTrim(sourceCode, '|');
				int result = 0;
				for (int i = 0; i < parameters.length; i++) {
					String parameter = parameters[i];
					//System.out.println("instantiating " + i + ": " + parameter);
					Integer parameterValue;
					int lastDotIndex = parameter.lastIndexOf('.');
					if (lastDotIndex != -1) {
						String className = parameter.substring( 0, lastDotIndex );
						String fieldName = parameter.substring( lastDotIndex + 1);
						Class parameterClass = loadClass( className );
						parameterValue = (Integer) ReflectionUtil.getStaticFieldValue(parameterClass, fieldName);
						//System.out.println(parameter + "=" + Integer.toBinaryString( parameterValue.intValue() ) );
					} else {
						parameterValue = new Integer( Long.decode(parameter).intValue() );
					}
					result |= parameterValue.intValue();
					//System.out.println("result=" + Integer.toBinaryString( result));
				}
				return new Integer( result );
			} catch (Exception e) {
				e.printStackTrace();
				throw new IllegalArgumentException("Unable to instantiate " + sourceCode + ": " + e.toString(), e );
			}
		}
		if (this.mappingsByName != null) {
			try {
				int lastDotIndex = sourceCode.lastIndexOf('.');
				if (lastDotIndex != -1) {
					String className = sourceCode.substring( 0, lastDotIndex );
					String fieldName = sourceCode.substring( lastDotIndex + 1);
					Class parameterClass = loadClass( className );
					//System.out.println(sourceCode + "=" +  ReflectionUtil.getStaticFieldValue(parameterClass, fieldName) );
					return ReflectionUtil.getStaticFieldValue(parameterClass, fieldName);
				}			
			} catch (Exception e) {
				e.printStackTrace();
				throw new IllegalArgumentException("Unable to instantiate " + sourceCode + ": " + e.toString(), e );
			}
		}
		throw new IllegalArgumentException("Unable to instantiate " + sourceCode );
	}

	protected Object instantiate(Class parameterClass, String parameter) {
		if ("null".equals(parameter) && !parameterClass.isPrimitive()) {
			return null;
		}
		// check if a static class field is referenced, e.g. Color.COLOR_HIGHLIGHTED_BACKGROUND
		Object returnValue = resolveField( parameterClass, parameter);
		if (returnValue != null) {
			return returnValue;
		}
		if (parameterClass == Byte.TYPE || parameterClass == Byte.class) {
			return instantiateByte( parameter );
		} else if (parameterClass == Short.TYPE || parameterClass == Short.class) {
			return instantiateShort( parameter );
		} else if (parameterClass == Integer.TYPE || parameterClass == Integer.class) {
			return instantiateInt( parameter );
		} else if (parameterClass == Long.TYPE || parameterClass == Long.class) {
			return instantiateLong( parameter );
		} else if (parameterClass == Float.TYPE || parameterClass == Float.class) {
			return instantiateFloat( parameter );
		} else if (parameterClass == Double.TYPE || parameterClass == Double.class) {
			return instantiateDouble( parameter );
		} else if (parameterClass == Character.TYPE || parameterClass == Character.class) {
			return instantiateChar( parameter );
		} else if (parameterClass == Boolean.TYPE || parameterClass == Boolean.class) {
			return instantiateBoolean( parameter );
		} else if (parameterClass == String.class) {
			if (parameter.length() > 2 && parameter.charAt(0) == '"' && parameter.charAt(parameter.length()-1) == '"') {
				return parameter.substring(1, parameter.length() - 1);
			} else {
				return parameter;
			}
		}
		throw new IllegalArgumentException("Unable to initial parameter [" + parameter + "] of class " + parameterClass.getName() );
	}

	/**
	 * Retrieves the value of a field.
	 * 
	 * @param parameterClass unused
	 * @param parameter the name of the field to resolve, including class name
	 * @return the value of the field
	 */
	protected Object resolveField(Class parameterClass, String parameter) {
		int dotPos = parameter.indexOf('.'); 
		if (dotPos != -1) {
			String fieldClassName = parameter.substring(0, dotPos );
			String fieldName = parameter.substring(dotPos+1);
			try {
				Class fieldClass = loadClass(fieldClassName);
				if (fieldName.indexOf('.') != -1) {
					return resolveField( fieldClass, fieldName );
				} else {
					Field field = ReflectionUtil.getField(fieldClass, fieldName);
					return field.get(null);
				}
			} catch (ClassNotFoundException e) {
				// ignore
			} catch (NoSuchFieldException e) {
				// ignore
			} catch (IllegalArgumentException e) {
				// TODO robertvirkus handle IllegalArgumentException
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO robertvirkus handle IllegalAccessException
				e.printStackTrace();
			}
		}
		return null;
	}
	
	protected Class loadClass( String className ) throws ClassNotFoundException {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			if (className.indexOf('.') == -1) {
				try {
					return Class.forName( "de.enough.polish.ui." + className );
				} catch (ClassNotFoundException e1) {
					try {
						return Class.forName( "java.lang." + className );
					} catch (ClassNotFoundException e2) {
						return Class.forName( "javax.microedition.lcdui." + className );
					}
				}
			}
			throw e;
		}
		
	}

	protected Object instantiateByte(String parameter) {
		return Byte.decode(parameter);
	}
	protected Object instantiateShort(String parameter) {
		return Short.decode(parameter);
	}
	protected Object instantiateInt(String parameter) {
		// use long, as otherwise ARGB color values cannot be read correctly:
		Long longObj = Long.decode(parameter);
		return new Integer( longObj.intValue() );
	}
	protected Object instantiateLong(String parameter) {
		return Long.decode(parameter);
	}
	protected Object instantiateFloat(String parameter) {
		return new Float( Float.parseFloat(parameter));
	}
	protected Object instantiateDouble(String parameter) {
		return new Double( Double.parseDouble(parameter));
	}
	protected Object instantiateChar(String parameter) {
		if (parameter.length() == 3) {
			return new Character( parameter.charAt(1));
		} else if (parameter.length() == 1) {
			return new Character( parameter.charAt(0));
		}
		throw new IllegalArgumentException("Unable to parse character [" + parameter + "]");
	}
	protected Object instantiateBoolean(String parameter) {
		if ("true".equals(parameter) || "Style.TRUE".equals(parameter) || "Boolean.TRUE".equals( parameter)) {
			return Boolean.TRUE;
		} else if ( "false".equals(parameter) || "Style.FALSE".equals(parameter) || "Boolean.FALSE".equals( parameter)) {
			return Boolean.FALSE;
		}
		throw new IllegalArgumentException("Unable to parse boolean [" + parameter + "]");
	}
	
	
	
	/**
	 * Determines whether the given value is the same as the default value of this attribute.
	 * 
	 * @param value the value defined by the user
	 * @return true when the given value is the default-value.
	 */
	public boolean isDefault( String value ) {
		return value.equals( this.defaultValue );
	}
	/**
	 * @return Returns the class-names for which this attribute can be used.
	 */
	public String getAppliesTo() {
		return this.appliesTo;
	}
	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @return true when this CSS attribute has a fix list of allowed values
	 */
	public boolean hasFixValues() {
		return this.allowedValues != null;
	}
	
	/**
	 * Retrieves the position of the given value in list of allowed values.
	 * 
	 * @param value the value
	 * @return the position, a value between 0 and the length of allowed values.
	 *         When the value is invalid, -1 will be returned
	 * @throws NullPointerException when no fix allowed values are defined
	 * @see #hasFixValues()
	 */
	public int getValuePosition( String value ) {
		for (int i = 0; i < this.allowedValues.length; i++) {
			if (value.equals( this.allowedValues[i])) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Retrieves the ID of this attribute.
	 * 
	 * @return the ID - or -1 if it is not known
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Sets the ID of this attribute.
	 * The ID should only be set, when it was previously not know (== -1).
	 *  
	 * @param id the new ID
	 */
	public void setId( int id ) {
		this.id = id;
	}

	/**
	 * @param className
	 * @return true when the attribute can be used for the given class.
	 */
	public boolean appliesTo(String className) {
		if (this.appliesToMap == null) {
			//System.out.println("CssAttribute.appliesTo=[" + this.appliesTo + "], to [" + className + "] = NO APPLIES MAP DEFINED!");
			return false;
		} else {
			//System.out.println("CssAttribute.appliesTo=[" + this.appliesTo + "], to [" + className + "] = " + (this.appliesToMap.get( className ) != null));
			String shortClassName = className;
			int lastDotIndex = className.lastIndexOf('.');
			if (lastDotIndex != -1) {
				shortClassName = className.substring(lastDotIndex + 1);
			}
//			if (this.name.equals("text-layout")) {
//				System.out.println("appliesToMap=" + appliesToMap);
//			}
			return (this.appliesToMap.get( shortClassName ) != null) || (this.appliesToMap.get( className ) != null);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		if (o instanceof CssAttribute) {
			return this.name.compareTo( ((CssAttribute)o).name );
		}
		return 0;
	}
	
	public CssMapping getMapping( String fromName ) {
		if (this.mappingsByName == null) {
			return null;
		} else {
			return (CssMapping) this.mappingsByName.get( fromName );
		}
	}
	
	/**
	 * Retrieves mapping by its name.
	 * 
	 * @param toName the name to which the mapping is usally converted, e.g. a class name
	 * @return the mapping
	 */
	public CssMapping getMappingByTo(String toName) {
		if (this.mappingsByName == null) {
			return null;
		}
		CssMapping[] mappings = getMappings();
		CssMapping matchingMapping = null;
		for (int i = 0; i < mappings.length; i++) {
			CssMapping mapping = mappings[i];
			if (toName.equals(mapping.getTo()) || toName.equals(mapping.getToClassName())) {
				if (mapping.hasAppliesTo()) {
					return mapping;
				}
				matchingMapping = mapping;
			}
		}
		return matchingMapping;
	}


	/**
	 * Adds the mappings of the given capability to this one.
	 * With this feature new mappings can be added in custom-css-attributes.xml
	 * 
	 * @param extension the attribute containing further mappings
	 */
	public void add( CssAttribute extension ) {
		try {
			this.mappingsByName.putAll( extension.mappingsByName );
		} catch (RuntimeException e) {
			System.err.println("Warning: unable to add extension " + extension.getName() + " to CSS attribute " + this.name );
			e.printStackTrace();
			System.out.println("attribute=" + this.name);
			System.out.println("mappingsByName=" + this.mappingsByName );
			System.out.println("extension=" + extension );
		}
	}

	
	/**
	 * Retrieves the group to which this attribute belongs to.
	 * 
	 * @return the group name, can be null.
	 */
	public String getGroup() {
		return this.group;
	}

	/**
	 * Retrieves all mappings for this attribute.
	 * 
	 * @return an array of mappings
	 */
	public CssMapping[] getMappings() {
		if (this.mappingsByName == null) {
			return new CssMapping[0];
		}
		return (CssMapping[]) this.mappingsByName.values().toArray( new CssMapping[ this.mappingsByName.size() ] );
	}

	/**
	 * Retrieves the type of the attribute
	 * 
	 * @return the type of the attribute
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Retrieves the default value for the attribute.
	 * 
	 * @param environment the environment
	 * @return the default value
	 */
	public Object instantiateDefault(Environment environment) {
		if (getDefaultValue() == null) {
			return null;
		}
		return instantiateValue( getValue( getDefaultValue(), environment) );
	}

	/**
	 * Retrieves the mappings that are appliable to a given class.
	 * 
	 * @param targetClass the class to return mappings for
	 * @return an array of mappings
	 */
	public CssMapping[] getApplicableMappings(Class targetClass) {
		ArrayList fullClassNamesList = new ArrayList();
		while (targetClass != null) {
			fullClassNamesList.add( targetClass.getName() );
			targetClass = targetClass.getSuperclass();
		}
		String[] fullClassNames = (String[]) fullClassNamesList.toArray( new String[ fullClassNamesList.size()] );
		String[] classNames = new String[ fullClassNames.length ];
		for (int i = 0; i < classNames.length; i++) {
			String className = fullClassNames[i];
			int lastDotPos = className.lastIndexOf('.');
			if (lastDotPos != -1) {
				className = className.substring( lastDotPos + 1 );
			}
			classNames[i] = className;
		}
		CssMapping[] mappings = getMappings();
		ArrayList matchingMappingsList = new ArrayList();
		for (int i = 0; i < mappings.length; i++) {
			CssMapping mapping = mappings[i];
			for (int j = 0; j < classNames.length; j++) {
				if ( mapping.appliesTo( classNames[j] ) || mapping.appliesTo( fullClassNames[j] ) ) {
					matchingMappingsList.add( mapping );
					break;
				}
			}
		}
		mappings = (CssMapping[]) matchingMappingsList.toArray( new CssMapping[ matchingMappingsList.size() ] );
		Arrays.sort( mappings );
		return mappings;
	}

	/**
	 * Parses and instantiates a value in the attribute.
	 * 
	 * @param valueStr the value string to parse
	 * @param environment the enviroment
	 * @return the instantiated value
	 */
	public Object parseAndInstantiateValue(String valueStr, Environment environment ) {
		String sourceCodeValue = getValue(valueStr, environment);
		return instantiateValue( sourceCodeValue );
	}

	/**
	 * Tells whether this attribute is a base attribute.
	 * 
	 * @return <code>true</code> if this is a base attribute,
	 * <code>false</code> otherwise
	 */
	public boolean isBaseAttribute() {
		return this.isBaseAttribute;
	}

	/**
	 * Determines whether this attribute can be ignored
	 * @return true when this parameter should be ignored when instantiating new backgrounds/borders
	 */
	public boolean isHidden()
	{
		return this.isHidden;
	}

	/**
	 * Generates source code for creating a CssAnimation
	 * @param cssAnimation the settings of the animation
	 * @param style the parent style
	 * @param environment the environment 
	 * @return source code that creates the animation
	 */
	public String generateAnimationSourceCode(CssAnimationSetting cssAnimation,
			Style style, Environment environment)
	{
		return null;
	}
	
	/**
	 * Creates the base source code for instantiating an animation - the subclasse then still needs to add range settings 
	 * @param className
	 * @param cssAnimation
	 * @param style
	 * @param environment
	 * @return a string buffer with the generated source code
	 */
	protected StringBuffer generateAnimationSourceCodeStart(String className, CssAnimationSetting cssAnimation, Style style, Environment environment)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append( "new " );
		buffer.append( ANIMATION_PACKAGE );
		buffer.append( className ).append("(");
		buffer.append( this.id ).append(", ");
		buffer.append( '"').append( cssAnimation.getOn() ).append("\", ");
		buffer.append( cssAnimation.getDuration() ).append(", ");
		buffer.append( cssAnimation.getDelay() ).append(", ");
		buffer.append( cssAnimation.getFunction() ).append(", ");
		buffer.append( cssAnimation.getRepeat() ).append(", ");
		String fireEvent = cssAnimation.getFireEvent();
		if (fireEvent  == null) {
			buffer.append( "null, ");
		} else {
			buffer.append('"').append( fireEvent ).append("\", ");
		}
		CssAnimationRange range = cssAnimation.getRange();
		if (range == null) {
			throw new BuildException("No \"range\" specified for CSS animation for attribute " + cssAnimation.getCssAttributeName() + " in style " + style.getSelector() + ": check your polish.css.");
		}
		return buffer;
	}
	

	/**
	 * Creates the base source code for instantiating an animation - the subclasse then still needs to add range settings 
	 * @param className
	 * @param cssAnimation
	 * @param style
	 * @param environment
	 * @return a string with the generated source code
	 */
	protected String generateAnimationSourceCode(String className, CssAnimationSetting cssAnimation, Style style, Environment environment)
	{
		StringBuffer buffer = generateAnimationSourceCodeStart(className, cssAnimation, style, environment);
		CssAnimationRange range = cssAnimation.getRange();
		buffer.append( getValue(range.getFrom(), environment)  ).append(", ");
		buffer.append( getValue(range.getTo(), environment) );
		buffer.append(')');
		return buffer.toString();
	}

	/**
	 * @param implicitName the name of this attribute
	 * @param param the implicit parent
	 */
	public void setImplicitDefinition(String implicitName, CssAttribute param)
	{
		this.isImplicit = true;
		this.name = implicitName;
		this.type = param.type;
		this.allowsCombinations = param.allowsCombinations;
		this.allowedValues = param.allowedValues;
		this.defaultValue = param.defaultValue;
		this.description = param.description;
		this.group = param.group;
		if (param.id != -1) {
			this.id = param.id;
		} else {
			int hashCode = implicitName.hashCode();
			short hash = (short) (hashCode >> 16 ^ hashCode);
			this.id = (short) (10000 + hash);
		}
		this.isCaseSensitive = param.isCaseSensitive;
		this.requiresMapping = param.requiresMapping;
		this.mappingsByName =  param.mappingsByName;
		this.isBaseAttribute = false;
	}
	
	/**
	 * Determines whether this CSS attribute has been defined indirectoy or directly within css-attributes.xml
	 * @return true when it has been defined indirectly
	 */
	public boolean isImplicit() {
		return this.isImplicit;
	}

	/**
	 * @return the shell
	 */
	public String getShell()
	{
		return this.shell;
	}

	public boolean isDeprecated() {
		return this.deprecatedMessage != null;
	}
	
	public String getDeprecatedMessage() {
		return this.deprecatedMessage;
	}

	/**
	 * Retrieves the version since this attribute is supported. 
	 * @return the version or null, when not specified.
	 */
	public String getSince() {
		return this.since;
	}

	public void setName(String name) {
		this.name = name;
	}
}
