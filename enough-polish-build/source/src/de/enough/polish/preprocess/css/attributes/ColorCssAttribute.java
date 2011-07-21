/*
 * Created on Apr 15, 2007 at 10:12:36 PM.
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
package de.enough.polish.preprocess.css.attributes;

import org.jdom.Element;

import de.enough.polish.BuildException;
import de.enough.polish.Environment;
import de.enough.polish.preprocess.css.ColorConverter;
import de.enough.polish.preprocess.css.CssAnimationSetting;
import de.enough.polish.preprocess.css.CssAttribute;
import de.enough.polish.preprocess.css.Style;
import de.enough.polish.ui.Color;

/**
 * <p>A simple character based attribute.</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 15, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ColorCssAttribute extends CssAttribute {
	
	private boolean isTranslucentSupported = false;
	private boolean isTransparentSupported = false;
	private boolean isPrimitive = false;
	
	/**
	 * Creates a new instance.
	 */
	public ColorCssAttribute() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#setDefinition(org.jdom.Element)
	 */
	public void setDefinition(Element definition) {
		super.setDefinition(definition);
		String boolStr = definition.getAttributeValue("translucent");
		if (boolStr != null) {
			this.isTranslucentSupported = boolStr.equals("true");
		}
		boolStr = definition.getAttributeValue("transparent");
		if (boolStr != null) {
			this.isTransparentSupported = boolStr.equals("true");
		}
		boolStr = definition.getAttributeValue("primitive");
		if (boolStr != null) {
			this.isPrimitive = boolStr.equals("true");
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#getValue(java.lang.String, de.enough.polish.Environment)
	 */
	public String getValue(String value, Environment environment ) {
		ColorConverter colorConverter = (ColorConverter) environment.get( ColorConverter.ENVIRONMENT_KEY );
		if (colorConverter != null) {
			if (this.isBaseAttribute || this.isPrimitive) {
				//System.out.println("ColorCssAttribute.getValue( " + value  + ") results in " + colorConverter.parseColor(value));
				return colorConverter.parseColor(value);
			}
			return colorConverter.generateColorConstructor(value);
		}
		throw new BuildException("Unable to load color converter during converting the polish.css file.");
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#instantiateValue(java.lang.String)
	 */
	public Object instantiateValue(String value) {
		if (this.isPrimitive) {
			return new Integer( Long.decode(value).intValue() );
		}
		if (this.isBaseAttribute) {
			return new Color( Long.decode(value).intValue() );
		}
		// a complex Color instantiation is used, e.g. "new Color( Color.COLOR_HIGHLIGHTED_BACKGROUND, true );
		//System.out.println("instantiating value " + value + " for attribute " + getName() );
		return super.instantiateValue(value);
	}



	/**
	 * @return the isTranslucentSupported
	 */
	public boolean isTranslucentSupported() {
		return this.isTranslucentSupported;
	}
	



	/**
	 * @return the isTransparentSupported
	 */
	public boolean isTransparentSupported() {
		return this.isTransparentSupported;
	}



	/**
	 * @return the isPrimitive
	 */
	public boolean isPrimitive() {
		return this.isPrimitive;
	}
	
		


	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.css.CssAttribute#generateAnimationSourceCode(de.enough.polish.preprocess.css.CssAnimationSetting, de.enough.polish.preprocess.css.Style, de.enough.polish.Environment)
	 */
	public String generateAnimationSourceCode(CssAnimationSetting cssAnimation, Style style, Environment environment)
	{
		return generateAnimationSourceCode("ColorCssAnimation", cssAnimation, style, environment);
	}
	
	

}
