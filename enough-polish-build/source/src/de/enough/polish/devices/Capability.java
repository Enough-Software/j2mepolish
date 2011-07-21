/*
 * Created on 23-May-2005 at 10:34:03.
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
package de.enough.polish.devices;

import org.jdom.Element;

import de.enough.polish.exceptions.InvalidComponentException;
import de.enough.polish.util.CastUtil;
import de.enough.polish.util.StringUtil;

/**
 * <p>Configures the behavior of &lt;capability&gt; elements.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        23-May-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class Capability {

	private final String identifier;
	private final boolean appendValues;
	private final char singleValuesSeparator;
	private final String group;
	private final boolean required;
	private final String type;
	private final String implicitGroup;
	private final String description;
    private boolean appendZeroDelimitedExtension;
	private boolean prependValues;

	/**
	 * Creates a new capability
	 * 
	 * @param definition the XML definition
	 * @param manager the parent manager
	 * @throws InvalidComponentException when the XML definition has syntax errors 
	 */
	public Capability(Element definition, CapabilityManager manager) 
	throws InvalidComponentException 
	{
		super();
		this.identifier = definition.getChildTextTrim("identifier");
		if (this.identifier == null) {
			System.out.println("definition=" + definition.toString() );
			throw new InvalidComponentException("Each defined capability need the <identifier> element in capabilities.xml");
		}
		String extensionMode = definition.getChildTextTrim("extension-mode");
		this.appendValues = "append".equals( extensionMode ) || "appendZero".equals(definition.getChildTextTrim("extension-mode"));
		this.prependValues = "prepend".equals( extensionMode );
		String separator = definition.getChildTextTrim("extension-mode-separator");
		this.appendZeroDelimitedExtension = "appendZero".equals(definition.getChildTextTrim("extension-mode"));
        if (this.appendZeroDelimitedExtension) {
            this.singleValuesSeparator = '\1';
        } else if (separator == null || separator.length()==0) {
			this.singleValuesSeparator = ',';
		} else {
			this.singleValuesSeparator = separator.charAt(0);
		}
        this.group = definition.getChildTextTrim("group");
		this.required = CastUtil.getBoolean( definition.getChildTextTrim("required") );
		this.type = definition.getChildTextTrim("type");
		this.implicitGroup = definition.getChildTextTrim("implicit-group");
		this.description = definition.getChildTextTrim("description");
	}

	
	/**
	 * @return Returns true when extensions of this capability should be appended (instead overwriting the old values).
	 */
	public boolean appendExtensions() {
		return this.appendValues;
	}
	public char getAppendExtensionsSeparator(){
		return this.singleValuesSeparator;
	}
	/**
	 * @return Returns the group.
	 */
	public String getGroup() {
		return this.group;
	}
	/**
	 * @return Returns the identifier.
	 */
	public String getIdentifier() {
		return this.identifier;
	}
	/**
	 * @return Returns the implicitGroup.
	 */
	public String getImplicitGroup() {
		return this.implicitGroup;
	}
	/**
	 * @return Returns the required.
	 */
	public boolean isRequired() {
		return this.required;
	}
	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return this.type;
	}
	
	public String getDescription() {
		return this.description;
	}

	/**
	 * Determines whether extensions should be prepended.
	 * @return true when new values should be placed in front
	 */
	public boolean prependExtensions() {
		return this.prependValues;
	}
	
	/**
	 * Checks if values should be overwritten.
	 * @return true when values should be overwritten.
	 */
	public boolean overwrite() {
		return !(this.appendValues || this.prependValues);
	}

	/**
	 * @return true if this capability will concatinate several occurences of a capability definition
     * with a zero (0x0) as delimiter.
	 */
    public boolean appendZeroDelimitedExtension() {
        return this.appendZeroDelimitedExtension;
    }
    
    /**
     * Marges a value with a new value.
     * @param previousValue the previous value of this capability, may be null
     * @param currentValue the current value of this capability
     * @return the resulting, possibly merged value.
     */
    public String getValue( String previousValue, String currentValue ) {
    	if (previousValue == null) {
    		return currentValue;
    	}
    	if (this.appendValues || this.prependValues) {
    		// check for duplicates:
			int existingValueIndex = previousValue.indexOf( currentValue ); 
			if ( existingValueIndex != -1 ) {
				// this is a value that seems to be present already,
				// double check it:
				int commaIndex = previousValue.indexOf(this.singleValuesSeparator, existingValueIndex );
				// now move towards the beginning of the value until we find a separator or the beginning:
				int i = existingValueIndex -1;
				while (i >= 0 && !(Character.isWhitespace(previousValue.charAt(i)) || previousValue.charAt(i) == this.singleValuesSeparator) ) {
					i--;
				}
				existingValueIndex = i+1;
				String checkValue;
				if (commaIndex == -1) {
					checkValue = previousValue.substring( existingValueIndex ).trim();
				} else {
					checkValue = previousValue.substring( existingValueIndex, commaIndex ).trim();
				}
				if ( !checkValue.equals( currentValue ) ) {
					// this was just a similar value...
					existingValueIndex = -1;
				}
			}
			if ( existingValueIndex != -1) {
				return previousValue;
			}

    	}
    	if (this.appendValues) {
    		return previousValue + this.singleValuesSeparator + currentValue;
    	} else if (this.prependValues) {
    		return currentValue + this.singleValuesSeparator + previousValue;
    	} else {
    		return currentValue;
    	}
    }
    
    /**
     * Splits a possibly combined capability value into it's single values
     * @param combinedValue the combined value
     * @return an array of the values
     */
    public String[] getSingleValues( String combinedValue ) {
    	return StringUtil.splitAndTrim( combinedValue, this.singleValuesSeparator );
    }

}
