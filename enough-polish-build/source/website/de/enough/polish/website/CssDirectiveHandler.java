/*
 * Created on Dec 21, 2007 at 2:32:05 PM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package de.enough.polish.website;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import de.enough.polish.preprocess.css.CssAttribute;
import de.enough.polish.preprocess.css.CssAttributesManager;
import de.enough.polish.preprocess.css.CssMapping;
import de.enough.polish.preprocess.css.attributes.BooleanCssAttribute;
import de.enough.polish.preprocess.css.attributes.ColorCssAttribute;
import de.enough.polish.preprocess.css.attributes.DimensionCssAttribute;
import de.enough.polish.preprocess.css.attributes.ImageUrlOrNoneCssAttribute;
import de.enough.polish.preprocess.css.attributes.MapCssAttribute;
import de.enough.polish.preprocess.css.attributes.ParameterizedCssAttribute;
import de.enough.webprocessor.DirectiveHandler;
import de.enough.webprocessor.util.StringList;

/**
 * <p>Handles cssattributes tag and includes all CSS attribute definitions for a specific class</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Dec 21, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CssDirectiveHandler extends DirectiveHandler
{
	
	protected CssAttributesManager cssManager;

	/**
	 * Creates a new handler
	 *
	 */
	public CssDirectiveHandler() {
		// the handler
	}
	
	public void init( Project project ) {
		super.init(project);
		String polishHome = project.getProperty("polish.home");
		if (polishHome == null) {
			throw new BuildException("no polish.home property set");
		}
		FileInputStream in = null;
		try
		{
			in = new FileInputStream( polishHome + File.separatorChar + "css-attributes.xml");
			CssAttributesManager manager = new CssAttributesManager(in);
			this.cssManager = manager;
		} catch (Exception e)
		{
			e.printStackTrace();
			throw new BuildException("unable to load css-attributes.xml: " + e);
		} finally {
			if (in != null) {
				try
				{
					in.close();
				} catch (IOException e)
				{
					// ignore
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.webprocessor.DirectiveHandler#processDirective(java.lang.String, java.lang.String, java.lang.String, de.enough.webprocessor.util.StringList)
	 */
	public String processDirective(String directiveName, String directive, String fileName, StringList lines)
	{
		String uiElementClassName = directive.substring( directiveName.length() ).trim();
		if (uiElementClassName.indexOf('.') == -1) {
			uiElementClassName = "de.enough.polish.ui." + uiElementClassName;
		}
		Class uiElementClass;
		
		try
		{
			uiElementClass = Class.forName(uiElementClassName);
			CssAttribute[] attributes = this.cssManager.getApplicableAttributes( uiElementClass );
			return processAttributes(attributes, uiElementClassName, uiElementClass);
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
			System.err.println("Unable to get attributes for class " + uiElementClassName + ": " + e);
			return "";
		}
	}

	/**
	 * @param attributes
	 * @param uiElementClassName
	 * @param uiElementClass
	 * @return
	 */
	protected String processAttributes(CssAttribute[] attributes, String uiElementClassName, Class uiElementClass)
	{
		if (attributes.length == 0) {
			return "";
		}
		StringBuffer html = new StringBuffer();
		addAttributes(attributes, html, uiElementClassName, uiElementClass);
		return html.toString();
	}

	/**
	 * @param attributes
	 * @param html
	 * @param uiElementClassName
	 * @param uiElementClass
	 */
	protected void addAttributes(CssAttribute[] attributes, StringBuffer html, String uiElementClassName, Class uiElementClass)
	{
		html.append("<table class=\"borderedTable\"  cellspacing=\"0\" cellpadding=\"3\" border=\"1\">\n");
		html.append( "<tr><th>CSS Attribute&nbsp;&nbsp;</th><th>Default</th><th>Values</th><th>Explanation</th><th>Since</th></tr>\n");
		boolean isInFirstClassMatches = true;
		for (int i = 0; i < attributes.length; i++)
		{
			CssAttribute attribute = attributes[i];
			if (isInFirstClassMatches && !attribute.appliesTo(uiElementClassName)) {
				isInFirstClassMatches = false;
				if (i != 0) {
					html.append( "<tr><th>Further CSS Attribute&nbsp;&nbsp;</th><th>Default</th><th>Values</th><th>Explanation</th><th>Since</th></tr>\n");
				}
			}
			addAttribute(html, attribute, uiElementClass);
		}
		html.append("</table>");
	}

	/**
	 * @param html
	 * @param attribute
	 * @param uiElementClass
	 */
	protected void addAttribute(StringBuffer html, CssAttribute attribute, Class uiElementClass)
	{
		html.append("	<tr>\n");
		html.append("		<td>").append( attribute.getName() ).append("</td>\n");
		if (attribute.getDefaultValue() != null) {
			html.append( "		<td>").append( attribute.getDefaultValue() ).append("</td>\n");
		} else {
			html.append( "		<td>-</td>\n");
		}
		if (attribute.getAllowedValues() == null) {
			if (attribute instanceof ColorCssAttribute) {
				boolean isArgb = ((ColorCssAttribute) attribute).isTranslucentSupported();
				if (isArgb) {
					html.append( "		<td>ARGB color</td>\n");
				} else {
					html.append( "		<td>color</td>\n");
				}
			} else if (attribute instanceof BooleanCssAttribute) {
				html.append( "		<td>true, false</td>\n");
			} else if (attribute instanceof DimensionCssAttribute) {
				html.append( "		<td>dimension (px, %), e.g. 3.5%</td>\n");
			} else if (attribute instanceof ImageUrlOrNoneCssAttribute) {
				html.append( "		<td>URL or &quot;none&quot;</td>\n");
			} else if (attribute instanceof MapCssAttribute) {
				html.append( "		<td>");
				CssMapping[] mappings = attribute.getApplicableMappings(uiElementClass);
				for (int j = 0; j < mappings.length; j++)
				{
					CssMapping mapping = mappings[j];
					html.append( mapping.getFrom() );
					if (j != mappings.length - 1) {
						html.append(", ");
					}
				}
				html.append( "</td>\n");
			} else if (attribute instanceof ParameterizedCssAttribute) {
				html.append( "		<td>").append( attribute.getName() ).append(" definition</td>\n");
			} else {
				html.append( "		<td>").append( attribute.getType() ).append("</td>\n");
			}
		} else {
			html.append( "		<td>");
			String[] values = attribute.getAllowedValues();
			for (int j = 0; j < values.length; j++)
			{
				String value = values[j];
				html.append( value );
				if (j != values.length -1) {
					html.append(", ");
				}
			}
			html.append("</td>\n");
		}
		if (attribute.getDescription() == null) {
			html.append( "		<td>-</td>\n");
		} else {
			html.append( "		<td>").append( attribute.getDescription() ).append("</td>\n");
		}
		String since = attribute.getSince();
		if (since == null) {
			html.append( "		<td>1.3</td>\n");
		} else {
			if (since.startsWith("J2ME Polish")) {
				since = since.substring( "J2ME Polish ".length() );
			}
			html.append( "		<td>").append( since ).append("</td>\n");
		}
		html.append("	</tr>\n");
	}

}
