/*
 * Created on 19-Aug-2004 at 15:21:38.
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.enough.polish.BuildException;
import de.enough.polish.preprocess.css.attributes.ArrayCssAttribute;
import de.enough.polish.preprocess.css.attributes.WrappedCssAttribute;
import de.enough.polish.util.ResourceUtil;
import de.enough.polish.util.StringUtil;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * <p>Manages all CSS attribute definitions loaded from [standard-css-attributes.xml] and [custom-css-aatributes.xml].</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        19-Aug-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CssAttributesManager {
	private static CssAttributesManager INSTANCE;
	private final HashMap typesClassesByName;
	private final HashMap typesByName;
	private final HashMap attributesByName;

	/**
	 * Creates a new CSS attributes manager.
	 * 
	 * @param is the input stream for the [standard-css-attributes.xml] file.
	 */
	public CssAttributesManager( InputStream is ) {
		this.attributesByName = new HashMap();
		this.typesClassesByName = new HashMap();
		this.typesByName = new HashMap();
		INSTANCE = this;
		addCssAttributes( is );
	}
	
	/**
	 * Retrieves the instance of this manager.
	 * @return an previously created instance, might be null
	 */
	public static CssAttributesManager getInstance() {
		return INSTANCE;
	}

	/**
	 * Adds the attributes from the given xml-file.
	 * 
	 * @param is the input stream for the [standard-css-attributes.xml] file.
	 * @throws BuildException when there are duplicate CSS entries or the InputStream is null. 
	 */
	public void addCssAttributes(InputStream is) {
		if (is == null) {
			throw new BuildException("Unable to load CSS attributes from NULL input stream. Please report this error to j2mepolish@enough.de.");
		}
		SAXBuilder builder = new SAXBuilder( false );
		Document document;
		try {
			document = builder.build( is );
		} catch (JDOMException e) {
			throw new BuildException("Unable to read [custom-css-attributes.xml]: " + e, e );
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException("Unable to read [custom-css-attributes.xml] or [standard-css-attributes.xml]: " + e, e );
		}
		Element typesElement = document.getRootElement().getChild("types");
		if (typesElement != null) {
			registerTypes( typesElement.getChildren() );
		}
		List xmlList = document.getRootElement().getChildren( "attribute" );
		for (Iterator iter = xmlList.iterator(); iter.hasNext();) {
			Element definition = (Element) iter.next();
			String name = definition.getAttributeValue("name");
			if (name != null) {
				CssAttribute existingAttribute = (CssAttribute) this.attributesByName.get( name );
				if (existingAttribute != null) {
					definition.setAttribute("type", existingAttribute.getType() );
					CssAttribute attr = createCssAttribute( definition );
					if (attr.mappingsByName == null) {
						throw new BuildException("Unable to read [custom-css-attributes.xml]: atribute " + name + " is defined more than once.");
					}
					existingAttribute.add( attr );
					continue;
				}
			}
			CssAttribute attribute = createCssAttribute( definition );
			this.attributesByName.put( attribute.getName(), attribute );
		}
	}
	
	/**
	 * Adds a new CSS attribute.
	 * 
	 * @param definition the XML definition of the CSS attribute.
	 * @return a new CSS attribute
	 */
	public CssAttribute createCssAttribute(Element definition) {
		String type = definition.getAttributeValue("type");
		if (type == null) {
			System.out.println("Warning: CSS attribute definition has no \"type\" attribute - now assuming \"string\" type for " + definition.getAttributeValue("name") );
			type = "string";
		} else if (type.endsWith("[]")) {
			CssAttribute arrayAttribute = new ArrayCssAttribute();
			arrayAttribute.setDefinition(definition);
			return arrayAttribute;
		}
		Class attributeClass = (Class) this.typesClassesByName.get( type );
		if (attributeClass != null) {
			// traditional CSS attribute type:
			CssAttribute attribute;
			try {
				attribute = (CssAttribute) attributeClass.newInstance();
				attribute.setDefinition(definition);
				return attribute;
			} catch (Exception e) {
				e.printStackTrace();
				throw new BuildException("Invalid CSS attribute: unable to instantiate type " + type + ": " + e.toString() );
			}
		} else {
			// previously defined CSS attribute type:
			CssAttribute previousAttribute = getAttribute(type);
			if (previousAttribute == null) {
				throw new BuildException( "Invalid CSS attribute: unable to instantiate CSS attribute type \"" + type + "\": no definition found. Check your custom-css-attributes.xml.");				
			}
			return new WrappedCssAttribute( previousAttribute, definition );
		}
	}

	/**
	 * Loads type definitions from the css-attributes.xml file.
	 * 
	 * @param xmlList the list of &lt;type&gt; elements
	 */
	private void registerTypes(List xmlList) {
		for (Iterator iter = xmlList.iterator(); iter.hasNext();) {
			Element definition = (Element) iter.next();
			String name = definition.getAttributeValue("name");
			String[] names;
			if (name == null) {
				name = definition.getAttributeValue("names");
			}
			if (name == null) {
				throw new BuildException("Invalid CSS type definition: no name nor names attribute found: " + definition );
			}
			names = StringUtil.splitAndTrim(name.toLowerCase(), ',');
			String className = definition.getAttributeValue("class");
			if (className == null) {
				throw new BuildException("Invalid CSS type definition: no class attribute found: " + definition );
			}
			try {
				Class typeClass = Class.forName( className );
				for (int i = 0; i < names.length; i++) {
					name = names[i];
					this.typesClassesByName.put( name, typeClass );
					this.typesByName.put( name, typeClass.newInstance() );
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new BuildException("Invalid CSS type definition: class attribute " + className + " points to invalid class: " + e.toString() );
			} catch (InstantiationException e) {
				e.printStackTrace();
				throw new BuildException("Invalid CSS type definition: class attribute " + className + " points to invalid class: " + e.toString() );
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				throw new BuildException("Invalid CSS type definition: class attribute " + className + " points to invalid class: " + e.toString() );
			}
		}
	}

	/**
	 * Retrieves the specified attribute.
	 * 
	 * @param name the name of the attribute
	 * @return either the attribute or null, when the specified attribute has not been defined within the appropriate XML files.
	 */
	public CssAttribute getAttribute( String name ) {
		return (CssAttribute) this.attributesByName.get( name );
	}
	
	/**
	 * Retrieves the specified attribute.
	 * 
	 * @param name the name of the attribute
	 * @return either the attribute or null, when the specified attribute has not been defined within the appropriate XML files.
	 */
	public CssAttribute getType( String name ) {
		return (CssAttribute) this.typesByName.get( name );
	}
	

	/**
	 * @return all registered CSS attributes
	 */
	public CssAttribute[] getAttributes() {
		return (CssAttribute[]) this.attributesByName.values().toArray(  new CssAttribute[ this.attributesByName.size() ] );
	}

	/**
	 * Retrieves all attributes for the given class.
	 * 
	 * @param targetClass the target class, e.g. "de.enough.polish.ui.StringItem"
	 * @return all applicable attributes for the given class
	 */
	public CssAttribute[] getApplicableAttributes( Class targetClass ) {
		CssAttribute[] attributes = getAttributes();
		ArrayList fullClassNamesList = new ArrayList();
		while (targetClass != null) {
			fullClassNamesList.add( targetClass.getName() );
			targetClass = targetClass.getSuperclass();
		}
		String[] fullClassNames = (String[]) fullClassNamesList.toArray( new String[ fullClassNamesList.size()] );
		String[] classNames = new String[ fullClassNames.length ];
		for (int i = 0; i < classNames.length; i++) {
			String name = fullClassNames[i];
			int lastDotPos = name.lastIndexOf('.');
			if (lastDotPos != -1) {
				name = name.substring( lastDotPos + 1 );
			}
			classNames[i] = name;
		}
		ArrayList firstClassAttributes = new ArrayList();
		ArrayList secondClassAttributes = new ArrayList();
		for (int i = 0; i < attributes.length; i++) {
			CssAttribute attribute = attributes[i];
			if (attribute.isBaseAttribute) {
				continue;
			}
			for (int j = 0; j < classNames.length; j++) {
				if ( attribute.appliesTo( classNames[j] ) || attribute.appliesTo( fullClassNames[j] ) ) {
					//System.out.println("attribute " + attribute.getName() + " applies to " + classNames[j] + ", j=" + j);
					if (j == 0) {
						firstClassAttributes.add( attribute );
					} else {
						secondClassAttributes.add( attribute );
					}
					break;
				}
			}
		}
		CssAttribute[] firstAttributes = (CssAttribute[]) firstClassAttributes.toArray( new CssAttribute[ firstClassAttributes.size() ] );
		Arrays.sort( firstAttributes );
		attributes = (CssAttribute[]) secondClassAttributes.toArray( new CssAttribute[ secondClassAttributes.size() ] ); 
		Arrays.sort( attributes );
		CssAttribute[] combined = new CssAttribute[ firstAttributes.length + attributes.length ];
		System.arraycopy( firstAttributes, 0, combined, 0, firstAttributes.length );
		System.arraycopy( attributes, 0, combined, firstAttributes.length, attributes.length );
		return combined;
	}

	/**
	 * Retrieves the attribute with the given ID-key.
	 * 
	 * @param id the ID of the desired attribute
	 * @return the attribute, null when it was not found
	 */
	public CssAttribute getAttribute(int id) {
		CssAttribute[] attributes = getAttributes();
		for (int i = 0; i < attributes.length; i++) {
			CssAttribute attribute = attributes[i];
			if (attribute.getId() == id) {
				return attribute;
			}
		}
		return null;
	}

	/**
	 * Retrieves an attribute manager instance for the given arguments.
	 * 
	 * @param polishHome the J2ME Polish installation directory
	 * @param resourceUtil resourc utility for loading resources from the classpath
	 * @return the attribute manager instance
	 */
	public static CssAttributesManager getInstance(File polishHome, ResourceUtil resourceUtil) 
	{
		try {
			InputStream in = resourceUtil.open( polishHome, "css-attributes.xml" );
			CssAttributesManager manager = new CssAttributesManager( in );
			try {
				in = resourceUtil.open( polishHome, "custom-css-attributes.xml" );
				if (in != null) {
					manager.addCssAttributes(in);
				}
			} catch (Exception e) {
				// ignore
			}
			return manager;
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Unable to load css attributes: " + e );
		}
	}

	/**
	 * Adds an implicit attribute definition for a background or border parameter
	 * @param param the parameter attribute
	 * @param mappingFrom the from attribute of the mapping
	 * @param paramParent the parent attribute of the parameter
	 */
	public void addImplicitAttribute(CssAttribute param, String mappingFrom, CssAttribute paramParent)
	{
		if (param.isHidden()) {
			// hidden parameters cannot be animated
			return;
		}
		String name = paramParent.getName() + "-" + mappingFrom + "-" + param.getName();
		try
		{
			CssAttribute implicitAttribute = (CssAttribute) param.getClass().newInstance();
			implicitAttribute.setImplicitDefinition( name, param );
			this.attributesByName.put( name, implicitAttribute);
		} catch (Exception e)
		{
			e.printStackTrace();
			throw new BuildException("Unable to register implicit attribute \"" + name + "\": " + e.toString() + ". Please report this error to j2mepolish@enough.de.");
		}
		
	}

}
