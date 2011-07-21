//#condition polish.usePolishGui
/*
 * Created on 04-Jan-2004 at 19:43:08.
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
package de.enough.polish.ui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import de.enough.polish.io.Externalizable;
import de.enough.polish.io.Serializer;
import de.enough.polish.util.IntHashMap;

/**
 * <p>Style defines the design of any widget.</p>
 * <p>This class is used by the widgets. If you only use the predefined
 *       widgets you do not need to work with this class.
 * </p>
 * @author Robert Virkus, robert@enough.de
 * <pre>
 * history
 *        04-Jan-2004 - rob creation
 * </pre>
 */
public class Style implements Externalizable
{
	//#if polish.cldc1.1
		//# public final static Boolean TRUE = Boolean.TRUE;
		//# public final static Boolean FALSE = Boolean.FALSE;
	//#else
		public final static Boolean TRUE = new Boolean( true );
		public final static Boolean FALSE = new Boolean( false );
	//#endif

	/**
	 * The name of this style.
	 */
	public transient String name;
	public transient String dynamicName;

	public Background background;
	public Border border;
	private Font font;

	public int layout;
	
	private short[] attributeKeys;
	private Object[] attributeValues;
	//#if polish.css.animations
		private CssAnimation[] animations;
	//#endif
	//#if polish.LibraryBuild
		private final de.enough.polish.util.HashMap libraryBuildAttributes = new de.enough.polish.util.HashMap();
	//#endif
		

	
	/**
	 * Creates a new style with default settings
	 */
	public Style() {
		this( 	null, Graphics.TOP | Graphics.LEFT, // layout
				null, null, // background, border
				null, null // attributes
		);
	}
	
	/**
	 * Creates a new style by creating a deep copy of the given style
	 * @param style the style
	 */
	public Style(Style style) {
		super();
		
		this.name = style.name;
		this.layout = style.layout;
		this.background = style.background;
		this.border = style.border;
		
		short[] keys = style.attributeKeys;
		Object[] values = style.attributeValues;
		
		if (keys != null) {
			this.attributeKeys = new short[ keys.length ];
			System.arraycopy( keys, 0, this.attributeKeys, 0, keys.length );
			this.attributeValues = new Object[ values.length ];
			System.arraycopy( values, 0, this.attributeValues, 0, values.length );
		}
		
		Style focusedStyle = (Style)style.getObjectProperty("focused-style");
		if(focusedStyle != null) {
			style.addAttribute("focused-style", new Style(focusedStyle));
		}
	}
	
	public void extendStyle(Style style) {
		this.name = style.name;
		this.layout = style.layout;
		this.background = style.background;
		this.border = style.border;
		
		IntHashMap map = new IntHashMap();
		
		//TODO make more performant
		addAttributesToMap(map, style);
		addAttributesToMap(map, this);
		
		int[] keys = map.keys();
		Object[] values = map.values();
		this.attributeKeys = new short[keys.length];
		this.attributeValues = new Object[keys.length];
		for (int i = 0; i < keys.length; i++) {
			this.attributeKeys[i] = (short)keys[i];
			this.attributeValues[i] = values[i];
			System.out.println(values[i]);
		}
	}

	void addAttributesToMap(IntHashMap map, Style style) {
		short[] keys = style.attributeKeys;
		Object[] values = style.attributeValues;
		
		if(keys != null && values != null) {
			for (int i = 0; i < keys.length; i++) {
				map.put(keys[i], values[i]);
			}
		}
	}

	/**
	 * Creates a new Style.
	 * 
	 * @param marginLeft the margin in pixels to the next element on the left
	 * @param marginRight the margin in pixels to the next element on the right
	 * @param marginTop the margin in pixels to the next element on the top
	 * @param marginBottom the margin in pixels to the next element on the bottom
	 * @param paddingLeft the padding between the left border and content in pixels
	 * @param paddingRight the padding between the right border and content in pixels
	 * @param paddingTop the padding between the top border and content in pixels
	 * @param paddingBottom the padding between the bottom border and content in pixels
	 * @param paddingVertical the vertical padding between internal elements of an item 
	 * @param paddingHorizontal the horizontal padding between internal elements of an item
	 * @param layout the layout for this style, e.g. Item.LAYOUT_CENTER
	 * @param fontColor the color of the font
	 * @param font the content-font for this style
	 * @param background the background for this style
	 * @param border the border for this style
	 * @param attributeKeys the integer-IDs of any additional attributes. Can be null.
	 * @param attributeValues the values of any additional attributes. Can be null.
	 */
	public Style( int marginLeft, int marginRight, int marginTop, int marginBottom,
			int paddingLeft, int paddingRight, int paddingTop, int paddingBottom, int paddingVertical, int paddingHorizontal,
			int layout,
			int fontColor, Font font,  
			Background background, Border border, 
			short[] attributeKeys,
			Object[] attributeValues
			) 
	{
		this( marginLeft, marginRight, marginTop, marginBottom,
				paddingLeft, paddingRight, paddingTop, paddingBottom, paddingVertical, paddingHorizontal,
				layout,
				fontColor, null, font,
				background, border,
				attributeKeys,
				attributeValues
		);
	}
	
	/**
	 * Creates a new Style.
	 * 
	 * @param marginLeft the margin in pixels to the next element on the left
	 * @param marginRight the margin in pixels to the next element on the right
	 * @param marginTop the margin in pixels to the next element on the top
	 * @param marginBottom the margin in pixels to the next element on the bottom
	 * @param paddingLeft the padding between the left border and content in pixels
	 * @param paddingRight the padding between the right border and content in pixels
	 * @param paddingTop the padding between the top border and content in pixels
	 * @param paddingBottom the padding between the bottom border and content in pixels
	 * @param paddingVertical the vertical padding between internal elements of an item 
	 * @param paddingHorizontal the horizontal padding between internal elements of an item
	 * @param layout the layout for this style, e.g. Item.LAYOUT_CENTER
	 * @param fontColor the color of the font
	 * @param fontColorObj the color of the font, might be a dynamic color like COLOR_FOREGROUND
	 * @param font the content-font for this style
	 * @param background the background for this style
	 * @param border the border for this style
	 * @param attributeKeys the integer-IDs of any additional attributes. Can be null.
	 * @param attributeValues the values of any additional attributes. Can be null.
	 */
	public Style( int marginLeft, int marginRight, int marginTop, int marginBottom,
			int paddingLeft, int paddingRight, int paddingTop, int paddingBottom, int paddingVertical, int paddingHorizontal,
			int layout,
			int fontColor, Color fontColorObj, Font font,  
			Background background, Border border,
			short[] attributeKeys,
			Object[] attributeValues
			) 
	{
		
		this.layout = layout;
		this.font = font;
		this.background = background;
		this.border = border;

		this.attributeValues = attributeValues;
		this.attributeKeys = attributeKeys;
		//#ifdef false
			// this is only used, so that the IDE does not complain about unused code:
			getProperty( -1 );
			getIntProperty( -1 );
			getBooleanProperty( -1 );
			getObjectProperty( -1 );
			getColorProperty( -1 );
		//#endif
	}
	
	/**
	 * Creates a new Style
	 * 
	 * @param name the name of the style
	 * @param layout the layout for this style, e.g. Item.LAYOUT_CENTER
	 * @param background the background for this style
	 * @param border the border for this style
	 * @param attributeKeys the integer-IDs of any additional attributes. Can be null.
	 * @param attributeValues the values of any additional attributes. Can be null.
	 */
	public Style( 
			String name,
			int layout, 
			Background background, Border border,
			short[] attributeKeys,
			Object[] attributeValues
			) 
	{
		this.name = name;
		this.layout = layout;
		this.background = background;
		this.border = border;
		this.attributeKeys = attributeKeys;
		this.attributeValues = attributeValues;
	}
	
	//#if polish.css.animations
	/**
	 * Creates a new Style when CSS animations are active.
	 * 
	 * @param name the name of the style
	 * @param layout the layout for this style, e.g. Item.LAYOUT_CENTER
	 * @param background the background for this style
	 * @param border the border for this style
	 * @param attributeKeys the integer-IDs of any additional attributes. Can be null.
	 * @param attributeValues the values of any additional attributes. Can be null.
	 * @param animations CSS animation settings
	 */
	public Style( 
			String name,
			int layout, 
			Background background, Border border,
			short[] attributeKeys,
			Object[] attributeValues,
			CssAnimation[] animations
			) 
	{
		this(   name, layout, background, border,
				attributeKeys,
				attributeValues
		);
		this.animations = animations;
	}
	//#endif
	
	/**
	 * Creates a copy of this style.
	 * A shallow copy will use the same internal arrays for CSS attributes and animations, so adding an attribute
	 * will also add the attribute to this style.
	 * Note that even with a deep copy, the border and background, as well as 
	 * embedded CSS attribute and animation references are kept the same.
	 * 
	 * @param deepCopy true when arrays should be copied instead of referenced
	 * @return a style copy
	 */	
	public Style clone(boolean deepCopy) {
		Style style = null;
		if (deepCopy) {
			short[] keys = this.attributeKeys;
			Object[] values = this.attributeValues;
			if (keys != null) {
				short[] cKeys = new short[ keys.length ];
				System.arraycopy( keys, 0, cKeys, 0, keys.length );
				keys = cKeys;
				Object[] cValues = new Object[ values.length ];
				System.arraycopy( values, 0, cValues, 0, values.length );
				values = cValues;
			}
			style = new Style ( this.name, this.layout, this.background, this.border, keys, values );
			//#if polish.css.animations
				if (this.animations != null) {
					style.animations = new CssAnimation[ this.animations.length ];
					System.arraycopy( this.animations, 0, style.animations, 0, this.animations.length );
				}
			//#endif			
		} else {
			style = new Style ( this.name, this.layout, this.background, this.border, this.attributeKeys, this.attributeValues );
			//#if polish.css.animations
				style.animations = this.animations;
			//#endif			
		}
		return style;
	}
	
	//#ifdef polish.LibraryBuild
	/**
	 * Retrieves a non-standard property of this style.
	 * 
	 * @param propName the name of the property
	 * @return the value of this property as a String. If none has been defined, null will be returned.
	 */
	public String getProperty( String propName ) {
		return (String) this.libraryBuildAttributes.get(propName);
	}
	//#endif
	
	//#ifdef polish.LibraryBuild
	/**
	 * Retrieves a non-standard property of this style.
	 * 
	 * @param propName the name of the property
	 * @return the value of this property. If none has been defined, null will be returned.
	 */
	public Object getObjectProperty( String propName ) {
		return this.libraryBuildAttributes.get(propName);
	}
	//#endif


	//#ifdef polish.LibraryBuild
	/**
	 * Retrieves a non-standard integer property of this style.
	 * 
	 * @param propName the name of the property
	 * @return the value of this property as an Integer object. If none has been defined, null will be returned.
	 */
	public Integer getIntProperty( String propName ) {
		Object val = this.libraryBuildAttributes.get(propName);
		if (val instanceof Dimension) {
			return new Integer( ((Dimension) val).getValue(100) );
		}
		return (Integer) val;
	}
	//#endif

	//#ifdef polish.LibraryBuild
	/**
	 * Retrieves a non-standard color property of this style.
	 * 
	 * @param propName the name of the property
	 * @return the value of this property as an Color object. If none has been defined, null will be returned.
	 */
	public Color getColorProperty( String propName ) {
		return (Color) this.libraryBuildAttributes.get(propName);
	}
	//#endif
	
	//#ifdef polish.LibraryBuild
	/**
	 * Retrieves a non-standard boolean property of this style.
	 * 
	 * @param propName the name of the property
	 * @return the value of this property as an Boolean object. If none has been defined, null will be returned.
	 */
	public Boolean getBooleanProperty( String propName ) {
		return (Boolean) this.libraryBuildAttributes.get(propName);
	}
	//#endif

	//#ifdef polish.LibraryBuild
		private String getProperty( int key ) {
	//#else
		//# public String getProperty( int key ) {
	//#endif
		if (this.attributeKeys == null) {
			return null;
		}
		for (int i = 0; i < this.attributeKeys.length; i++ ) {
			if (this.attributeKeys[i] == key) {
				Object value = this.attributeValues[i];
				if (value != null) {
					return value.toString();
				} else {
					return null;
				}
			}
		}
		return null;
	}
		
	public Object getObjectProperty( int key ) {
		if (this.attributeKeys == null) {
			return null;
		}
		for (int i = 0; i < this.attributeKeys.length; i++ ) {
			if (this.attributeKeys[i] == key) {
				return this.attributeValues[i];
			}
		}
		return null;
	}


	//#ifdef polish.LibraryBuild
		private Integer getIntProperty( int key ) {
	//#else
		//# public Integer getIntProperty( int key ) {
	//#endif
		if (this.attributeKeys == null) {
			return null;
		}
		for (int i = 0; i < this.attributeKeys.length; i++ ) {
			if (this.attributeKeys[i] == key) {
				Object value = this.attributeValues[i];
				if (value instanceof Color) {
					return ((Color)value).getInteger();
				}
				return (Integer) value;
			}
		}
		return null;
	}
		
	//#ifdef polish.LibraryBuild
		private Color getColorProperty( int key ) {
	//#else
		//# public Color getColorProperty( int key ) {
	//#endif
		if (this.attributeKeys == null) {
			return null;
		}
		for (int i = 0; i < this.attributeKeys.length; i++ ) {
			if (this.attributeKeys[i] == key) {
				Object value = this.attributeValues[i];
				return (Color) value;
			}
		}
		return null;
	}
	
	//#ifdef polish.LibraryBuild
		private Boolean getBooleanProperty( int key ) {
	//#else
		//# public Boolean getBooleanProperty( int key ) {
	//#endif
		if (this.attributeKeys == null) {
			return null;
		}
		for (int i = 0; i < this.attributeKeys.length; i++ ) {
			if (this.attributeKeys[i] == key) {
				return (Boolean) this.attributeValues[i];
			}
		}
		return null;
	}
	
		
	//#if polish.css.animations
		/**
		 * Retrieves all registered animations
		 * Note that this method is only defined when css animations are being used.
		 * You can test this with the preprocessing symbol 'polish.css.animations'
		 * 
		 * @return an array of registered animations for this style, can be null
		 */
		public 	CssAnimation[] getAnimations() {
			return this.animations;
		}
	//#endif
	
	/**
	 * Releases all (memory intensive) resources such as images or RGB arrays of this style.
	 */
	public void releaseResources() {
		if (this.background != null) {
			this.background.releaseResources();
		}
		//TODO how to handle before/after images?
	}
	
	/**
	 * Retrieves the font color that should be used.
	 * The color can be dynamic like Display.COLOR_FOREGROUND and should always be retrieved
	 * using this method instead of using the public field fontColor.
	 *  
	 * @return the color for the font.
	 */
	public int getFontColor() {
		//#if polish.css.font-color
			Style style = this;
			Color color = style.getColorProperty("font-color");
			if (color != null) {
				return color.getColor();
			}
		//#endif
		return 0;
	}
	
	/**
	 * Retrieves the font associated with this style
	 * @return the font of this style, can be null if none is defined
	 */
	public Font getFont() {
		if (this.font == null) {
			Style style = this;
			int fontStyle = Font.STYLE_PLAIN;
			boolean fontDefined = false;
			//#if polish.css.font
				Font f = (Font) style.getObjectProperty( "font" );
				if (f != null) {
					this.font = f;
					return f;
				}
			//#endif
			//#if polish.css.font-style
				Integer styleInt = style.getIntProperty("font-style");
				if (styleInt != null) {
					fontStyle = styleInt.intValue();
					fontDefined = true;
				}
			//#endif
			int fontSizeConstant = Font.SIZE_MEDIUM;
			Dimension fontSizeDimension = null;
			//#if polish.css.font-size
				Object size = style.getObjectProperty("font-size");
				if (size instanceof Dimension) {
					fontSizeDimension = (Dimension) size;
					fontDefined = true;
				} else {
					Integer sizeInt = style.getIntProperty("font-size");
					if (sizeInt != null) {
						fontSizeConstant = sizeInt.intValue();
						fontDefined = true;
					}
				}
			//#endif
			int fontFace = Font.FACE_SYSTEM;
			//#if polish.css.font-face;
				Integer faceInt = style.getIntProperty("font-face");
				if (faceInt != null) {
					fontFace = faceInt.intValue();
					fontDefined = true;
				}
			//#endif
			if (fontDefined) {
				if (fontSizeDimension != null) {
				//#if polish.android
					//# this.font = de.enough.polish.android.lcdui.Font.getFont(fontFace, fontStyle, fontSizeDimension);
				//#elif polish.blackberry
					//# this.font = de.enough.polish.blackberry.ui.Font.getFont(fontFace, fontStyle, fontSizeDimension);
				//#else
					if (fontSizeDimension.isPercent()) {
						if (fontSizeDimension.getValue(100) < 100) {
							fontSizeConstant = Font.SIZE_SMALL;
						} else if (fontSizeDimension.getValue(100) == 100) {
							fontSizeConstant = Font.SIZE_MEDIUM;
						} else {
							fontSizeConstant = Font.SIZE_LARGE;
						}
					} else {
						int height = fontSizeDimension.getValue( 100 );
						Font fnt = Font.getFont( fontFace, fontStyle, Font.SIZE_MEDIUM );
						int minDifference = Math.abs( fnt.getHeight() - height ); 
						fnt = Font.getFont( fontFace, fontStyle, Font.SIZE_SMALL );
						int difference = Math.abs( fnt.getHeight() - height );
						if (difference < minDifference) {
							fontSizeConstant = Font.SIZE_SMALL;
						} else {
							fnt = Font.getFont( fontFace, fontStyle, Font.SIZE_LARGE );
							difference = Math.abs( fnt.getHeight() - height );
							if (difference < minDifference) {
								fontSizeConstant = Font.SIZE_LARGE;
							}
						}
						
					}
					
					this.font = Font.getFont(fontFace, fontStyle, fontSizeConstant);
				//#endif
				} else {
					this.font = Font.getFont(fontFace, fontStyle, fontSizeConstant);
				}
			}
		}
		return this.font;
	}


	/**
	 * Removes the specified attribute from this style.
	 * 
	 * @param key the integer key of the attribute
	 */
	public void removeAttribute(int key) {
		if (this.attributeKeys == null) {
			return;
		}
		for (int i = 0; i < this.attributeKeys.length; i++ ) {
			if (this.attributeKeys[i] == key) {
				//TODO decrease array
				this.attributeValues[i] = null;
				return;
			}
		}
	}
	
	//#if polish.LibraryBuild
	/**
	 * Removes the specified attribute from this style.
	 * 
	 * @param key the key name of the attribute
	 */
	public void removeAttribute(String key) {
		this.libraryBuildAttributes.remove(key);
	}
	//#endif
	
	//#if polish.LibraryBuild
	/**
	 * Adds the specified attribute to this style, possibly replacing a previous set value.
	 * This method expects the property name referenced directly like "padding-top", not
	 * as a variable like propName, since the property name is converted to an integer value
	 * in the preprocessing phase.
	 * 
	 * @param key the integer key of the attribute
	 * @param value the value of the attribute
	 */
	public void addAttribute(String key, Object value) {
		this.libraryBuildAttributes.put( key, value );
	}
	//#endif

	
	/**
	 * Adds the specified attribute to this style, possibly replacing a previous set value
	 * 
	 * @param key the integer key of the attribute
	 * @param value the value of the attribute
	 */
	public void addAttribute(int key, Object value) {
		if (this.attributeKeys == null) {
			this.attributeKeys = new short[]{ (short)key };
			this.attributeValues = new Object[]{ value };
			return;
		}
		for (int i = 0; i < this.attributeKeys.length; i++ ) {
			if (this.attributeKeys[i] == key) {
				this.attributeValues[i] = value;
				return;
			}
		}
		// need to increase the attributes tables:
		short[] keys = new short[ this.attributeKeys.length + 1 ];
		System.arraycopy(this.attributeKeys,0, keys, 0, this.attributeKeys.length );
		keys[ this.attributeKeys.length ] = (short) key;
		Object[] values = new Object[ this.attributeKeys.length + 1 ];
		System.arraycopy(this.attributeValues,0, values, 0, this.attributeKeys.length );
		values[ this.attributeKeys.length ] = value;
		this.attributeKeys = keys;
		this.attributeValues = values;
	}
	
	/**
	 * Looks up the margin value and falls back to the normal margin value.
	 * @param value the margin value
	 * @param range the available width or height
	 * @return the margin or 0 if it is not defined
	 */
	private int getMargin(Dimension value, int range)
	{
		//#if polish.css.margin
			if (value == null) {
				Style style = this;
				value = (Dimension) style.getObjectProperty("margin"); 	
			}
		//#endif
		if (value != null) {
			return value.getValue(range);
		}	
		return 0;
	}
	
	/**
	 * Looks up the left margin value and falls back to the default margin value.
	 * @param width the corresponding available width
	 * @return the left margin or 0 if neither the left nor the normal margin is defined
	 */
	public int getMarginLeft( int width ) {
		Style style = this;
		Dimension value =  null;
		//#if polish.css.margin-left
			value = (Dimension) style.getObjectProperty("margin-left");
		//#endif
		return getMargin( value, width );
	}
	
	/**
	 * Looks up the right margin value and falls back to the default margin value.
	 * @param width the corresponding available width
	 * @return the right margin or 0 if neither the right nor the normal margin is defined
	 */
	public int getMarginRight( int width ) {
		Style style = this;
		Dimension value =  null;
		//#if polish.css.margin-right
			value = (Dimension) style.getObjectProperty("margin-right");
		//#endif
		return getMargin( value, width );
	}

	/**
	 * Looks up the top margin value and falls back to the default margin value.
	 * @param height the corresponding available height or width - note that the CSS standard uses the width (so will J2ME Polish items) for calculating percentage values for all margins
	 * @return the top margin or 0 if neither the top nor the normal margin is defined
	 */
	public int getMarginTop( int height ) {
		Style style = this;
		Dimension value =  null;
		//#if polish.css.margin-top
			value = (Dimension) style.getObjectProperty("margin-top");
		//#endif
		return getMargin( value, height );
	}
	
	/**
	 * Looks up the bottom margin value and falls back to the default margin value.
	 * @param height the corresponding available height or width - note that the CSS standard uses the width (so will J2ME Polish items) for calculating percentage values for all margins
	 * @return the bottom margin or 0 if neither the bottom nor the normal margin is defined
	 */
	public int getMarginBottom( int height ) {
		Style style = this;
		Dimension value =  null;
		//#if polish.css.margin-bottom
			value = (Dimension) style.getObjectProperty("margin-bottom");
		//#endif
		return getMargin( value, height );
	}
	
	/**
	 * Looks up the padding value and falls back to the normal padding value.
	 * @param value the padding value
	 * @param range the available width or height
	 * @return the padding or 1 if it is not defined
	 */
	private int getPadding(Dimension value, int range)
	{
		//#if polish.css.padding
			if (value == null) {
				Style style = this;
				value = (Dimension) style.getObjectProperty("padding"); 	
			}
		//#endif
		if (value != null) {
			return value.getValue(range);
		}	
		return 1;
	}



	/**
	 * Looks up the left padding value and falls back to the default padding value.
	 * @param width the corresponding available width
	 * @return the left padding or 0 if neither the left nor the normal padding is defined
	 */
	public int getPaddingLeft( int width ) {
		Style style = this;
		Dimension value = null;
		//#if polish.css.padding-left
			value = (Dimension) style.getObjectProperty("padding-left");
		//#endif
		return getPadding( value, width );
	}
	

	/**
	 * Looks up the right padding value and falls back to the default padding value.
	 * @param width the corresponding available width
	 * @return the right padding or 0 if neither the right nor the normal padding is defined
	 */
	public int getPaddingRight( int width ) {
		Style style = this;
		Dimension value = null;
		//#if polish.css.padding-right
			value = (Dimension) style.getObjectProperty("padding-right");
		//#endif
		return getPadding( value, width );
	}

	/**
	 * Looks up the top padding value and falls back to the default padding value.
	 * @param height the corresponding available height or width - note that the CSS standard uses the width (so will J2ME Polish items) for calculating percentage values for all paddings
	 * @return the top padding or 0 if neither the top nor the normal padding is defined
	 */
	public int getPaddingTop( int height ) {
		Style style = this;
		Dimension value = null;
		//#if polish.css.padding-top
			value = (Dimension) style.getObjectProperty("padding-top");
		//#endif
		return getPadding( value, height );
	}
	
	/**
	 * Looks up the bottom padding value and falls back to the default padding value.
	 * @param height the corresponding available height or width - note that the CSS standard uses the width (so will J2ME Polish items) for calculating percentage values for all paddings
	 * @return the bottom padding or 0 if neither the bottom nor the normal padding is defined
	 */
	public int getPaddingBottom( int height ) {
		Style style = this;
		Dimension value = null;
		//#if polish.css.padding-bottom
			value = (Dimension) style.getObjectProperty("padding-bottom");
		//#endif
		return getPadding( value, height );
	}
	
	/**
	 * Looks up the horizontal padding value and falls back to the default padding value.
	 * @param width the corresponding available width
	 * @return the horizontal padding or 0 if neither the horizontal nor the normal padding is defined
	 */
	public int getPaddingHorizontal( int width ) {
		Style style = this;
		Dimension value = null;
		//#if polish.css.padding-horizontal
			value = (Dimension) style.getObjectProperty("padding-horizontal");
		//#endif
		return getPadding( value, width );
	}
	
	/**
	 * Looks up the vertical padding value and falls back to the default padding value.
	 * @param height the corresponding available height or width - note that the CSS standard uses the width (so will J2ME Polish items) for calculating percentage values for all paddings
	 * @return the vertical padding or 0 if neither the vertical nor the normal padding is defined
	 */
	public int getPaddingVertical( int height ) {
		Style style = this;
		Dimension value = null;
		//#if polish.css.padding-vertical
			value = (Dimension) style.getObjectProperty("padding-vertical");
		//#endif
		return getPadding( value, height );
	}
	
	/**
	 * Retrieves the layout of this style
	 * @return the layout
	 * @see Item#LAYOUT_LEFT
	 * @see Item#LAYOUT_RIGHT
	 * @see Item#LAYOUT_TOP
	 * @see Item#LAYOUT_BOTTOM
	 * @see Item#LAYOUT_CENTER
	 * @see Item#LAYOUT_VCENTER
	 * @see Item#LAYOUT_EXPAND
	 * @see Item#LAYOUT_VEXPAND
	 * @see Item#LAYOUT_SHRINK
	 * @see Item#LAYOUT_VSHRINK
	 */
	public int getLayout() {
		return this.layout;
	}
	
	/**
	 * Sets the layout of this style.
	 * @param layout the layout
	 * @see #getLayout()
	 */
	public void setLayout(int layout) {
		this.layout = layout;
	}


	
	/**
	 * Translates the layout into a Graphics form.
	 * Item.LAYOUT_LEFT is translated to Graphics.LEFT, for example.
	 * This method translates Item.LAYOUT_VCENTER into Graphics.VCENTER, so the resulting anchor CANNOT be used for text.
	 * @return the layout in Graphics anchor format.
	 * @see #getAnchorForText()
	 * @see Graphics#LEFT
	 * @see Graphics#RIGHT
	 * @see Graphics#HCENTER
	 * @see Graphics#TOP
	 * @see Graphics#BOTTOM
	 * @see Graphics#VCENTER
	 */
	public int getAnchor() {
		int lo = this.layout;
		int orientation = 0;
		if ((lo & Item.LAYOUT_CENTER) == Item.LAYOUT_CENTER) {
			orientation |= Graphics.HCENTER;
		} else if ((lo & Item.LAYOUT_RIGHT) == Item.LAYOUT_RIGHT) {
			orientation |= Graphics.RIGHT;
		} else {
			orientation |= Graphics.LEFT;
		}
		if ((lo & Item.LAYOUT_VCENTER) == Item.LAYOUT_VCENTER) {
			orientation |= Graphics.VCENTER;
		} else if ((lo & Item.LAYOUT_BOTTOM) == Item.LAYOUT_BOTTOM) {
			orientation |= Graphics.BOTTOM;
		} else {
			orientation |= Graphics.TOP;
		}
		return orientation;
	}
	
	/**
	 * Translates the layout into a Graphics form.
	 * Item.LAYOUT_LEFT is translated to Graphics.LEFT, for example.
	 * This method translates Item.LAYOUT_VCENTER into Graphics.BASELINE, so the resulting anchor CAN be used for text.
	 * @return the layout in Graphics anchor format.
	 * @see #getAnchor()
	 * @see Graphics#LEFT
	 * @see Graphics#RIGHT
	 * @see Graphics#HCENTER
	 * @see Graphics#TOP
	 * @see Graphics#BOTTOM
	 * @see Graphics#BASELINE
	 */
	public int getAnchorForText() {
		int lo = this.layout;
		int orientation = 0;
		if ((lo & Item.LAYOUT_CENTER) == Item.LAYOUT_CENTER) {
			orientation |= Graphics.HCENTER;
		} else if ((lo & Item.LAYOUT_RIGHT) == Item.LAYOUT_RIGHT) {
			orientation |= Graphics.RIGHT;
		} else {
			orientation |= Graphics.LEFT;
		}
		if ((lo & Item.LAYOUT_VCENTER) == Item.LAYOUT_VCENTER) {
			orientation |= Graphics.BASELINE;
		} else if ((lo & Item.LAYOUT_BOTTOM) == Item.LAYOUT_BOTTOM) {
			orientation |= Graphics.BOTTOM;
		} else {
			orientation |= Graphics.TOP;
		}
		return orientation;
	}
	
	/**
	 * Translates the horizontal layout into a Graphics form.
	 * Item.LAYOUT_LEFT is translated to Graphics.LEFT, for example.
	 * @return the horizontal layout in Graphics anchor format, either Graphics.LEFT, Graphics.RIGHT or Graphics.HCENTER
	 * @see #getAnchor()
	 * @see Graphics#LEFT
	 * @see Graphics#RIGHT
	 * @see Graphics#HCENTER
	 */
	public int getAnchorHorizontal()
	{
		int lo = this.layout;
		int orientation = 0;
		if ((lo & Item.LAYOUT_CENTER) == Item.LAYOUT_CENTER) {
			orientation = Graphics.HCENTER;
		} else if ((lo & Item.LAYOUT_RIGHT) == Item.LAYOUT_RIGHT) {
			orientation = Graphics.RIGHT;
		} else {
			orientation = Graphics.LEFT;
		}
		return orientation;
	}

	
	/**
	 * Reads this style from an InputStream
	 * @param in the input stream
	 * @throws IOException when reading fails
	 */
	public void read(DataInputStream in) throws IOException {
		this.background = (Background)Serializer.deserialize(in);
		this.border 	= (Border)Serializer.deserialize(in);
		this.layout = in.readInt();
		
		this.attributeKeys 	 = (short[])Serializer.deserialize(in);
		this.attributeValues = (Object[])Serializer.deserialize(in);
	}
	
	/**
	 * Writes this style to an OutputStream
	 * @param out the output stream
	 * @throws IOException when writing fails
	 */
	public void write(DataOutputStream out) throws IOException {
		Serializer.serialize(this.background, out);
		Serializer.serialize(this.border, out);
		out.writeInt(this.layout);
		
		Serializer.serialize(this.attributeKeys, out);
		Serializer.serialize(this.attributeValues, out);
	}

	/**
	 * Retrieves the internal attribute keys 
	 * @return a short array with all the keys or null when this style has no attributes
	 */
	public short[] getRawAttributeKeys() {
		return this.attributeKeys;
	}

	/**
	 * Retrieves the internal attribute values that are defined in this style.
	 * @return the attribute values or none in case no attributes are defined for this style
	 */
	public Object[] getRawAttributeValues() {
		return this.attributeValues;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return '[' + this.name + "] "+ super.toString();
	}

	
//#ifdef polish.Style.additionalMethods:defined
	//#include ${polish.Style.additionalMethods}
//#endif
	

}
