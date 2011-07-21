/*
 * Created on 02-Nov-2005 at 02:19:42.
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
package de.enough.polish.preprocess.css;

import java.util.HashMap;
import java.util.Map;

import de.enough.polish.BuildException;
import org.jdom.Element;

import de.enough.polish.BooleanEvaluator;
import de.enough.polish.util.StringUtil;

/**
 * <p>Maps a short CSS name to any string.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        02-Nov-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CssMapping
implements Comparable
{
	
	private String from;
	private String to;
	private String condition;
	private String converter;
	private String[] excludes;
	private Map appliesToMap;
	


	/**
	 * Creates a new mapping.
	 * 
	 * @param definition the definition 
	 */
	public CssMapping( Element definition ) {
		super();
		this.from = definition.getAttributeValue("from");
		if (this.from == null) {
			throw new BuildException("Invalid mapping without \"from\" attribute - check your css-attributes.xml file(s).");
		}
		this.to = definition.getAttributeValue("to");
		this.condition = definition.getAttributeValue("condition");
		if (this.condition == null) {
			this.condition = definition.getChildTextTrim("condition");
		}
		this.converter = definition.getAttributeValue("converter");
		if (this.to == null && this.converter == null) {
			throw new BuildException("Invalid mapping without \"to\" and  without \"converter\" attribute - check your css-attributes.xml file(s) of the mapping from [" + this.from + "].");
		}
		String excludesStr = definition.getAttributeValue("excludes");
		if (excludesStr != null) {
			this.excludes = StringUtil.splitAndTrim( excludesStr, ',');
		}
		String appliesToStr = definition.getAttributeValue("appliesTo");
		if (appliesToStr != null) {
			String[] appliesToNames = StringUtil.splitAndTrim(appliesToStr, ',');
			this.appliesToMap = new HashMap( appliesToNames.length );
			for (int i = 0; i < appliesToNames.length; i++) {
				String appliesToName = appliesToNames[i];
				this.appliesToMap.put(appliesToName, Boolean.TRUE );
			}
		}
	}

	/**
	 * @return Returns the condition.
	 */
	public String getCondition() {
		return this.condition;
	}

	/**
	 * @return Returns the from.
	 */
	public String getFrom() {
		return this.from;
	}

	/**
	 * @return Returns the to.
	 */
	public String getTo() {
		return this.to;
	}
	

	/**
	 * @return Returns the converter.
	 */
	public String getConverter() {
		return this.converter;
	}

	/**
	 * @return Returns the excludes.
	 */
	public String[] getExcludes() {
		return this.excludes;
	}

	/**
	 * Checks whether this attribute mapping has a condion or an exclusion.
	 * When the condion is not met or there are conflicting values a BuildException will be thrown.
	 * 
	 * @param attributeName the name of the attribute
	 * @param attributeValue the value of the attribute 
	 * @param evaluator the boolean evaluator
	 * @throws BuildException when the condion is not met or when there are conflicting values.
	 */
	public void checkCondition( String attributeName, String attributeValue, BooleanEvaluator evaluator ) {
		if (this.condition != null) {
			if (!evaluator.evaluate(this.condition, "polish.css", 0)) {
				throw new BuildException( "Invalid CSS: the value \"" + this.from + "\" of attribute \"" + attributeName + "\" is invalid for the current device, the condition \"" + this.condition + "\" is not met. Use this value in appropriate subfolders like \"resources/midp2/polish.css\"."  );
			}
		}
		if (this.excludes != null) {
			for (int i = 0; i < this.excludes.length; i++) {
				String exclude = this.excludes[i];
				int  index = attributeValue.indexOf( exclude );
				while ( index != -1 ) {
					// double check that the exclude is really in there, not a similar word:
					boolean match = true;
					if (index != 0) {
						char c = attributeValue.charAt(index - 1);
						if (c != ' ' && c != '\t' && c != '|' && c != '&' && c != '!' && c != '^' && c != ';') {
							//System.out.println("not a match for " + exclude + " in " + attributeValue + ", char before is=" + c);
							match = false;
						}							
					}
					if (index + exclude.length() + 1 < attributeValue.length()) {
						char c = attributeValue.charAt(index + exclude.length());
						if (c != ' ' && c != '\t' && c != '|' && c != '&' && c != '!' && c != '^' && c != ';') {
							//System.out.println("not a match for " + exclude + " in " + attributeValue + ", char after is=" + c);
							match = false;
						}							
					}
					if (match) {
						throw new BuildException( "Invalid CSS: the value \"" + this.from + "\" of the attribute \"" + attributeName + "\" conflicts with the value \"" + exclude + "\" - please change your polish.css setting for the value \"" + attributeValue + "\"."  );
					}
					index = attributeValue.indexOf( exclude, index + 1 );
				}
			}
		}
	}

	/**
	 * Retrieves the class name of the class this mapping maps to.
	 * 
	 * @return the class name
	 */
	public String getToClassName() {
		if (this.to == null) {
			return null;
		}
		String target = this.to.trim();
		if (target.startsWith("new ")) {
			target = target.substring( "new ".length() );
		}
		int parenthesesPos = target.indexOf('(');
		if (parenthesesPos != -1) {
			target = target.substring(0, parenthesesPos );
		}
		target = target.trim();
		return target;
	}

	/**
	 * Sets the class name of the class this mapping applies to.
	 * 
	 * @param className the class name
	 * @return true when the attribute can be used for the given class.
	 */
	public boolean appliesTo(String className) {
		if (this.appliesToMap == null) {
			//System.out.println("CssAttribute.appliesTo=[" + this.appliesTo + "], to [" + className + "] = NO APPLIES MAP DEFINED!");
			return false;
		} else {
			return this.appliesToMap.get( className ) != null;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		if (o instanceof CssMapping) {
			return this.from.compareTo( ((CssMapping)o).from );
		}
		return 0;
	}

	/**
	 * Tells whether this mapping has an appliesTo attribute, or no.
	 * 
	 * @return <code>true</code> if this mapping has an appliesTo attribute,
	 * <code>false</code> otherwise 
	 */
	public boolean hasAppliesTo() {
		// TODO robertvirkus implement getApplicableTo
		return this.appliesToMap != null;
	}
}
