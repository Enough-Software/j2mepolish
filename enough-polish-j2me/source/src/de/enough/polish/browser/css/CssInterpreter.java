//#condition polish.usePolishGui
/*
 * Copyright (c) 2010 Robert Virkus / Enough Software
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

package de.enough.polish.browser.css;

import java.io.IOException;
import java.io.Reader;
import java.util.Hashtable;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.io.ResourceLoader;
import de.enough.polish.io.StringReader;
import de.enough.polish.ui.Color;
import de.enough.polish.ui.Dimension;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;
import de.enough.polish.ui.backgrounds.ImageBackground;
import de.enough.polish.ui.backgrounds.SimpleBackground;
import de.enough.polish.ui.backgrounds.TiledImageBackground;
import de.enough.polish.util.HashMap;
import de.enough.polish.util.StreamUtil;
import de.enough.polish.util.TextUtil;

/**
 * Allows to interpret simple CSS constructs.
 * 
 * @author Robert Virkus
 *
 */
public class CssInterpreter {
	private static final HashMap colorMap = new HashMap();
	static {
		colorMap.put("aqua",   new Color(0x00ffff) );
		colorMap.put("black",  new Color(0x000000) );
		colorMap.put("blue",   new Color(0x0000ff) );
		colorMap.put("fuchsia",new Color(0xff00ff) );
		colorMap.put("gray",   new Color(0x808080) );
		colorMap.put("green",  new Color(0x008000) );
		colorMap.put("lime",   new Color(0x00ff00) );
		colorMap.put("maroon", new Color(0x800000) );
		colorMap.put("navy",   new Color(0x000080) );
		colorMap.put("olive",  new Color(0x808000) );
		colorMap.put("orange", new Color(0xffa500) );
		colorMap.put("purple", new Color(0x800080) );
		colorMap.put("red",    new Color(0xff0000) );
		colorMap.put("silver", new Color(0xc0c0c0) );
		colorMap.put("teal",   new Color(0x008080) );
		colorMap.put("white",  new Color(0xffffff) );
		colorMap.put("yellow", new Color(0xffff00) );
	}
	private static final int BUFFER_SIZE = 100;
	private final Reader reader;
	private final char[] readBuffer;
	private int bufferStartIndex;
	private int bufferEndIndex;
	private boolean isInComment;
	private boolean hasNextToken;
	
	private Hashtable styles;
	private ResourceLoader resourceLoader;

	/**
	 * Creates a new CSS Reader
	 * 
	 * @param cssCode the CSS code 
	 */
	public CssInterpreter(String cssCode ) {
		this( new StringReader(cssCode), null );
	}
	
	/**
	 * Creates a new CSS Reader
	 * 
	 * @param cssCode the CSS code 
	 */
	public CssInterpreter(String cssCode, ResourceLoader resourceLoader ) {
		this( new StringReader(cssCode), resourceLoader );
	}
	
	/**
	 * Creates a new CSS reader
	 * @param reader the underlying reader
	 */
	public CssInterpreter( Reader reader ) {
		this( reader, null );
	}
	
	/**
	 * Creates a new CSS reader
	 * @param reader the underlying reader
	 */
	public CssInterpreter( Reader reader, ResourceLoader resourceLoader ) {
		this.reader = reader;
		this.resourceLoader = resourceLoader;
		this.readBuffer = new char[ BUFFER_SIZE ];
		this.bufferStartIndex = BUFFER_SIZE;
		this.hasNextToken = true;
	}
	
	/**
	 * Checks if there are more tokens available
	 * @return true when there are more tokens
	 */
	protected boolean hasNextToken() {
		return this.hasNextToken || this.bufferStartIndex < this.bufferEndIndex;
	}
	
	/**
	 * Retrieves the next token
	 * @param strBuffer the buffer into which tokens are written
	 * @return the trimmed next token, an empty string when a block is closed
	 * @throws IOException when reading fails
	 */
	protected String nextToken(StringBuffer strBuffer) throws IOException {
		if (this.bufferStartIndex >= this.bufferEndIndex) {
			int read = this.reader.read( this.readBuffer );
			if (read == -1) {
				return null;
			}
			this.bufferStartIndex = 0;
			this.bufferEndIndex = read;
			this.hasNextToken = (read == BUFFER_SIZE);
		}
		boolean comment = this.isInComment;
		boolean checkNextCharForEndComment = false;
		boolean checkNextCharForStartComment = false;
		for (int index = this.bufferStartIndex; index < this.bufferEndIndex; index++ ) {
			char c = this.readBuffer[index];
			if (comment && c == '*') {
				checkNextCharForEndComment = true;
			} else if (checkNextCharForEndComment && c == '/') {
				comment = false;
				this.isInComment = false;
			} else if (comment) {
				// ignore comment char
				checkNextCharForEndComment = false;
			} else if (c == '/'){
				// this _could_ be the start of a new comment:
				checkNextCharForStartComment = true;
				strBuffer.append(c);
			} else if (checkNextCharForStartComment && c == '*') {
				checkNextCharForStartComment = false;
				comment = true;
				strBuffer.deleteCharAt(strBuffer.length() - 1);
			} else {
				checkNextCharForStartComment = false;
				if (c == ';' || c == '{' || c == '}') {
					String result = strBuffer.toString().trim();
					if (c == '}' && result.length() > 0) {
						// we always want an empty string when a parentheses is closed, so that
						// we can parse complex styles easier.
						this.bufferStartIndex = index;
					} else {
						this.bufferStartIndex = index + 1;
					}
					strBuffer.delete(0, strBuffer.length() );
					return result;
				} else if (c == '\n'){
					// ignore line breaks
				} else {
					strBuffer.append(c);
				}
			}
		}
		this.bufferStartIndex = this.bufferEndIndex;
		return nextToken(strBuffer);
	}
	
	/**
	 * Reads and parses the next style
	 * @return the next style
	 * @throws IOException when reading fails
	 */
	public Style nextStyle() throws IOException {
		if (!hasNextToken()) {
			return null;
		}
		StringBuffer strBuffer = new StringBuffer();
		String styleName = nextToken( strBuffer );
		if (styleName == null) {
			return null;
		}
		if (styleName.length() == 0) {
			throw new IllegalArgumentException("Invalid empty style name.");
		}
		if (styleName.charAt(0) == '.') {
			styleName = styleName.substring(1);
		}
		styleName = styleName.toLowerCase();
		Style style;
		if (styleName.endsWith(":hover")) {
			styleName = styleName.substring(0, styleName.length() - ":hover".length() );
			Style originalStyle = getStyle(styleName);
			styleName += "focused";
			if (originalStyle == null) {
				style = new Style(); 
			} else {
				style = originalStyle.clone(true);
				originalStyle.addAttribute("focused-style", style);
			}
		} else if (styleName.endsWith(":pressed")){
			styleName = styleName.substring(0, styleName.length() - ":pressed".length() );
			Style originalStyle = getStyle(styleName);
			styleName += "pressed";
			if (originalStyle == null) {
				style = new Style(); 
			} else {
				style = originalStyle.clone(true);
				originalStyle.addAttribute("pressed-style", style);
			}
		} else {
			 style = new Style();
		}
		style.name = styleName;
		String blockName = null;
		HashMap backgroundAttributes = null;
		HashMap borderAttributes = null;
		while (hasNextToken()) {
			String property = nextToken(strBuffer);
			if (property.length() == 0) {
				if (blockName != null) {
					// close block:
					blockName = null;
				} else {
					// style is finished:
					break;
				}
			} else {
				// this is either a blockname or a CSS attribute:
				int splitPos = property.indexOf(':');
				if (splitPos == -1) {
					// a new block starts:
					if (blockName != null) {
						throw new IOException("invalid block nesting in style " + style.name + ": " + blockName + "-" + property);
					}
					blockName = property;
				} else {
					// this is a CSS property:
					String attributeValue = property.substring(splitPos+1).trim();
					String attributeName = property.substring(0, splitPos).trim();
					if (blockName != null) {
						attributeName = blockName + "-" + attributeName;
					}
					if (attributeName.startsWith("background-")) {
						if (backgroundAttributes == null) {
							backgroundAttributes = new HashMap();
						}
						backgroundAttributes.put(attributeName, attributeValue);
					} else if (attributeName.startsWith("border-")) {
						if (borderAttributes == null) {
							borderAttributes = new HashMap();
						}
						borderAttributes.put(attributeName, attributeValue);
					} else {
						try {
							addAttribute( attributeName, attributeValue, style );
						} catch (IllegalArgumentException e) {
							// ignore invalid setting
							//#debug warn
							System.out.println("Invalid CSS: " + e.getMessage() );
						} 
					}
				}
			}
		}
		if (backgroundAttributes != null) {
			addBackground( backgroundAttributes, style );
		}
		if (borderAttributes != null) {
			addBorder( borderAttributes, style );
		}
		return style;
	}

	private void addBorder(HashMap borderAttributes, Style style) {
		// TODO Auto-generated method stub
		
	}

	private void addBackground(HashMap backgroundAttributes, Style style) {
		String colorValue  = (String) backgroundAttributes.get("background-color");
		String imageValue = (String) backgroundAttributes.get("background-image");
		String repeatValue  = (String) backgroundAttributes.get("background-repeat");
		//TODO support background-attachment
		//String attachment  = (String) backgroundAttributes.get("background-attachment");
		String positionValue = (String) backgroundAttributes.get("background-position");
		Color color;
		if (colorValue != null) {
			color = parseColor(colorValue);
		} else {
			color = new Color( Color.TRANSPARENT );
		}
		if (imageValue == null || "none".equals(imageValue)) {
			style.background = new SimpleBackground(color);
		} else {
			String imageUrl = parseUrl(imageValue);
			Image image = null;
			if (this.resourceLoader != null) {
				try {
					//#if polish.midp2
						image = Image.createImage( this.resourceLoader.getResourceAsStream(imageUrl) );
					//#else
						byte[] data = StreamUtil.readFully( this.resourceLoader.getResourceAsStream(imageUrl) );
						image = Image.createImage( data, 0, data.length );
					//#endif
				} catch (IOException e) {
					//#debug error
					System.out.println("Unable to load background-image " + imageValue + e);
				}
			}
			int anchor = 0;
			Dimension xOffset = new Dimension(0);
			Dimension yOffset = new Dimension(0);
			if (positionValue != null) {
				String[] positions = TextUtil.splitAndTrim(positionValue, ' ');
				boolean horizontalSet = false;
				boolean verticalSet = false;
				String remaining = null;
				for (int i = 0; i < positions.length; i++) {
					String pos = positions[i];
					if ("top".equals(pos)) {
						verticalSet = true;
						anchor |= Graphics.TOP;
					} else if ("bottom".equals(pos)) {
						verticalSet = true;
						anchor |= Graphics.BOTTOM;
					} else if ("left".equals(pos)) {
						horizontalSet = true;
						anchor |= Graphics.LEFT;
					}  else if ("right".equals(pos)) {
						horizontalSet = true;
						anchor |= Graphics.RIGHT;
					} else {
						remaining = pos;
					}
				}
				if (!(horizontalSet && verticalSet)) {
					boolean isCenter = remaining == null || "center".equals(remaining) || "50%".equals(remaining);
					if (horizontalSet) {
						if (isCenter) {
							anchor |= Graphics.VCENTER;
						} else if ("0%".equals(remaining)) {
							anchor |= Graphics.TOP;
						} else if ("100%".equals(remaining)) {
							anchor |= Graphics.BOTTOM;
						} else {
							yOffset.setValue(remaining);
						}
					} else if (verticalSet) {
						if (isCenter) {
							anchor |= Graphics.HCENTER;
						} else if ("0%".equals(remaining)) {
							anchor |= Graphics.LEFT;
						} else if ("100%".equals(remaining)) {
							anchor |= Graphics.RIGHT;
						} else {
							xOffset.setValue(remaining);
						}
					} else {
						if (positions.length == 1 && isCenter) {
							anchor = Graphics.HCENTER | Graphics.VCENTER;
						} else {
							anchor = Graphics.TOP | Graphics.LEFT;
							xOffset.setValue( positions[0]);
							if (positions.length > 1) {
								yOffset.setValue( positions[1] );
							}
						}
					}
				}
			} else {
				anchor = Graphics.TOP | Graphics.LEFT;
			}
			if (repeatValue == null || "no-repeat".equals(repeatValue)) {
				if (image != null) {
					style.background = new ImageBackground(color.getColor(), image, anchor, xOffset, yOffset );
				} else {
					style.background = new ImageBackground(color.getColor(), imageUrl, anchor, xOffset, yOffset );
				}
			} else {
				int repeat = TiledImageBackground.REPEAT;
				if ("repeat-x".equals(repeatValue)) {
					repeat = TiledImageBackground.REPEAT_X;
				} else if ("repeat-y".equals(repeatValue)) {
					repeat = TiledImageBackground.REPEAT_Y;
				}
				if (image != null) {
					style.background = new TiledImageBackground(color.getColor(), image, repeat, anchor, 0, 0, false, xOffset, yOffset );
				} else {
					style.background = new TiledImageBackground(color.getColor(), imageUrl, repeat, anchor, 0, 0, false, xOffset, yOffset );
				}
			}
		}
//		if ("background-color".equals(name)) {
//			Color color = parseColor( value );
//			style.background = new SimpleBackground(color);
//			return;
//		}
//		if ("background-image".equals(name)) {
//			if ("none".equals(value)) {
//				// ignore 'none' image:
//				return;
//			}
//			String imageUrl = parseUrl(value);
//			
//			System.out.println("imageUrl=" + imageUrl);
//			return;
//		}
		// TODO Auto-generated method stub
		
	}

	/**
	 * Retriees a locally registered style
	 * @param styleName the style name in lower case
	 * @return the correpsonding locally defined style
	 */
	protected Style getStyle(String styleName) {
		Style style = null;
		if (this.styles != null) {
			style = (Style) this.styles.get(styleName);
		}
		if (style == null) {
			style = StyleSheet.getStyle( styleName );
		}
		return style;
	}

	/**
	 * Parses an attribute and adds the attribute to the specified style.
	 * 
	 * @param name the name of the attribute
	 * @param value the value of the attribute in string form
	 * @param style the style to which the attribute should be added
	 * @throws IllegalArgumentException when parsing or reading fails 
	 */
	protected void addAttribute(String name, String value, Style style) {
		//TODO need to generate parsing code automatically:
		//#if polish.css.font-color
			if ("font-color".equals(name) || "color".equals(name)) {
				style.addAttribute( "font-color", parseColor( value ) );
				return;
			}
		//#endif
		//#if polish.css.margin
			if ("margin".equals(name)) {
				String[] values = TextUtil.split(value, ' ');
				if (values.length == 1) {
					style.addAttribute("margin", new Dimension(value));
				} else if (values.length == 2) {
					Dimension vertical = new Dimension( values[0]);
					Dimension horizontal = new Dimension( values[1]);
					style.addAttribute("margin-top", vertical);
					style.addAttribute("margin-right", horizontal);
					style.addAttribute("margin-bottom", vertical);
					style.addAttribute("margin-left", horizontal);
				} else if (values.length == 3) {
					Dimension horizontal = new Dimension( values[1]);
					style.addAttribute("margin-top", new Dimension( values[0]));
					style.addAttribute("margin-right", horizontal);
					style.addAttribute("margin-bottom", new Dimension( values[2]));
					style.addAttribute("margin-left", horizontal);
				} else {
					style.addAttribute("margin-top", new Dimension( values[0]));
					style.addAttribute("margin-right", new Dimension( values[1]));
					style.addAttribute("margin-bottom", new Dimension( values[2]));
					style.addAttribute("margin-left", new Dimension( values[3]));
					
				}
				return;
			}
		//#endif
		//#if polish.css.margin-left
			if ("margin-left".equals(name)) {
				style.addAttribute("margin-left", new Dimension(value));
				return;
			}
		//#endif
		//#if polish.css.margin-right
			if ("margin-right".equals(name)) {
				style.addAttribute("margin-right", new Dimension(value));
				return;
			}
		//#endif
		//#if polish.css.margin-top
			if ("margin-top".equals(name)) {
				style.addAttribute("margin-top", new Dimension(value));
				return;
			}
		//#endif
		//#if polish.css.margin-bottom
			if ("margin-bottom".equals(name)) {
				style.addAttribute("margin-bottom", new Dimension(value));
				return;
			}
		//#endif
		//#if polish.css.padding
			if ("padding".equals(name)) {
				String[] values = TextUtil.split(value, ' ');
				if (values.length == 1) {
					style.addAttribute("padding", new Dimension(value));
				} else if (values.length == 2) {
					Dimension vertical = new Dimension( values[0]);
					Dimension horizontal = new Dimension( values[1]);
					style.addAttribute("padding-top", vertical);
					style.addAttribute("padding-right", horizontal);
					style.addAttribute("padding-bottom", vertical);
					style.addAttribute("padding-left", horizontal);
				} else if (values.length == 3) {
					Dimension horizontal = new Dimension( values[1]);
					style.addAttribute("padding-top", new Dimension( values[0]));
					style.addAttribute("padding-right", horizontal);
					style.addAttribute("padding-bottom", new Dimension( values[2]));
					style.addAttribute("padding-left", horizontal);
				} else {
					style.addAttribute("padding-top", new Dimension( values[0]));
					style.addAttribute("padding-right", new Dimension( values[1]));
					style.addAttribute("padding-bottom", new Dimension( values[2]));
					style.addAttribute("padding-left", new Dimension( values[3]));
					
				}
				return;
			}
		//#endif
		//#if polish.css.padding-left
			if ("padding-left".equals(name)) {
				style.addAttribute("padding-left", new Dimension(value));
				return;
			}
		//#endif
		//#if polish.css.padding-right
			if ("padding-right".equals(name)) {
				style.addAttribute("padding-right", new Dimension(value));
				return;
			}
		//#endif
		//#if polish.css.padding-top
			if ("padding-top".equals(name)) {
				style.addAttribute("padding-top", new Dimension(value));
				return;
			}
		//#endif
		//#if polish.css.padding-bottom
			if ("padding-bottom".equals(name)) {
				style.addAttribute("padding-bottom", new Dimension(value));
				return;
			}
		//#endif

	}

	/**
	 * Parses an URL value, e.g. for background-image.
	 * @param value the value, e.g. url( "test.png" );
	 * @return the URL, e.g. test.png
	 */
	protected String parseUrl(String value) {
		String url = null;
		int startPos = value.indexOf('"');
		if (startPos != -1) {
			int endPos = value.indexOf('"', startPos + 1);
			if (endPos != -1) {
				url = value.substring(startPos+1, endPos);
			}
		}
		if (url == null) {
			if (value.startsWith("url")) {
				startPos = value.indexOf('(');
				int endPos = value.indexOf(')');
				if (startPos != -1 && endPos > startPos) {
					url = value.substring( startPos + 1, endPos).trim();
				}
			}
		}
		if (url == null) {
			//#debug warn
			System.out.println("Unable to extract URL from " + value);
			url = value;
		}
		if (url.length() > 0 && url.charAt(0) != '/' && !url.startsWith("http")) {
			url = '/' + url;
		}
		return url;
	}

	/**
	 * Parses a color value.
	 * @param value the value as a String
	 * @return the parsed color value
	 * @throws IllegalArgumentException when the value is not a well defined color
	 */
	protected Color parseColor(String value) {
		int valueLength = value.length();
		if ("transparent".equals(value)) {
			return new Color( Color.TRANSPARENT );
		}
		try {
			if (value.charAt(0) == '#') {
				if (valueLength <= 4) {
					// this is either #rgb or #argb:
					StringBuffer buffer = new StringBuffer( (valueLength -1) *2 );
					for (int i = 1; i < valueLength; i++) {
						char c = value.charAt(i);
						buffer.append(c).append(c);
					}
					long color = Long.parseLong(buffer.toString(), 16);
					return new Color( (int) color );
				}
				long color = Long.parseLong(value.substring(1), 16);
				return new Color( (int) color, false );
			} else if (value.startsWith("rgb") || value.startsWith("argb")) {
				int startPos = value.indexOf('(');
				int endPos = value.indexOf(')');
				if (startPos != -1 && endPos != -1) {
					String[] chunks = TextUtil.splitAndTrim(value.substring( startPos + 1, endPos), ',');
					if (chunks.length >= 3 && chunks.length <= 4) {
						int color = 0;
						for (int i = 0; i < chunks.length; i++) {
							String chunk = chunks[i];
							int chunkLength = chunk.length();
							int chunkColor;
							if (chunk.charAt(chunkLength-1) == '%') {
								int percent = Integer.parseInt( chunk.substring(0, chunkLength-1).trim() );
								chunkColor = (percent * 255) / 100; 
							} else {
								chunkColor = Integer.parseInt(chunk);
							}
							color |= chunkColor << ((chunks.length - i - 1) * 8);
						}
						return new Color( color );
					}
				}
			} else {
				Color color = (Color) colorMap.get(value);
				if (color != null) {
					return color;
				}
			}
		} catch (NumberFormatException e) {
			// ignore, fail below
		}
		throw new IllegalArgumentException("Invalid color: " + value);
	}

	
	/**
	 * Reads all styles and registers them in the internal Hashtable
	 * 
	 * @throws IOException when reading fails
	 * @throws IllegalArgumentException when encountering illegal values. 
	 */
	public void registerStyles() throws IOException {
		if (this.styles == null) {
			this.styles = new Hashtable();
		}
		Style style;
		while ((style = nextStyle()) != null) {
			this.styles.put( style.name, style);
		}
	}
	
	/**
	 * Retrieves all read styles.
	 * @return all styles in Hashtable&lt;String name, Style style&gt; format
	 */
	public Hashtable getAllStyles() {
		if (this.styles == null) {
			try {
				registerStyles();
			} catch (IOException e) {
				//#debug warn
				System.out.println("Unable to interpret styles" + e);
			}
		}
		return this.styles;
	}
}
