//#condition polish.usePolishGui
/*
 * Created on 06-Jun-2006 at 15:17:05.
 * 
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
package de.enough.polish.ui;


import de.enough.polish.io.Serializable;

/**
 * <p>Wraps an (A)RGB color and can also contain dynamic references like Display.COLOR_BACKGROUND.</p>
 * <p>For compatibility with MIDP 1.0 devices, the color class also defines COLOR_BACKGROUND etc with the very same values.</p>
 * <p>Copyright Enough Software 2006 - 2009</p>
 * <pre>
 * history
 *        06-Jun-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class Color implements Serializable {
	
	/**
	 * A color that defines transparent together with the dynamic flag.
	 * The value of TRANSPARENT is -1.
	 */
	public static final int TRANSPARENT = -1;
	
	/**
	 * A color specifier for use with <code>getColor</code>.
	 * <code>COLOR_BACKGROUND</code> specifies the background color of
	 * the screen.
	 * The background color will always contrast with the foreground color.
	 * 
	 * <p>
	 * <code>COLOR_BACKGROUND</code> has the value <code>0</code>.
	 */
	public static final int COLOR_BACKGROUND = 0;

	/**
	 * A color specifier for use with <code>getColor</code>.
	 * <code>COLOR_FOREGROUND</code> specifies the foreground color,
	 * for text characters
	 * and simple graphics on the screen.  Static text or user-editable
	 * text should be drawn with the foreground color.  The foreground color
	 * will always constrast with background color.
	 * 
	 * <p> <code>COLOR_FOREGROUND</code> has the value <code>1</code>.
	 */
	public static final int COLOR_FOREGROUND = 1;

	/**
	 * A color specifier for use with <code>getColor</code>.
	 * <code>COLOR_HIGHLIGHTED_BACKGROUND</code> identifies the color for the
	 * focus, or focus highlight, when it is drawn as a
	 * filled in rectangle. The highlighted
	 * background will always constrast with the highlighted foreground.
	 * 
	 * <p>
	 * <code>COLOR_HIGHLIGHTED_BACKGROUND</code> has the value <code>2</code>.
	 */
	public static final int COLOR_HIGHLIGHTED_BACKGROUND = 2;

	/**
	 * A color specifier for use with <code>getColor</code>.
	 * <code>COLOR_HIGHLIGHTED_FOREGROUND</code> identifies the color for text
	 * characters and simple graphics when they are highlighted.
	 * Highlighted
	 * foreground is the color to be used to draw the highlighted text
	 * and graphics against the highlighted background.
	 * The highlighted foreground will always constrast with
	 * the highlighted background.
	 * 
	 * <p>
	 * <code>COLOR_HIGHLIGHTED_FOREGROUND</code> has the value <code>3</code>.
	 */
	public static final int COLOR_HIGHLIGHTED_FOREGROUND = 3;

	/**
	 * A color specifier for use with <code>getColor</code>.
	 * <code>COLOR_BORDER</code> identifies the color for boxes and borders
	 * when the object is to be drawn in a
	 * non-highlighted state.  The border color is intended to be used with
	 * the background color and will contrast with it.
	 * The application should draw its borders using the stroke style returned
	 * by <code>getBorderStyle()</code>.
	 * 
	 * <p> <code>COLOR_BORDER</code> has the value <code>4</code>.
	 */
	public static final int COLOR_BORDER = 4;

	/**
	 * A color specifier for use with <code>getColor</code>.
	 * <code>COLOR_HIGHLIGHTED_BORDER</code>
	 * identifies the color for boxes and borders when the object is to be
	 * drawn in a highlighted state.  The highlighted border color is intended
	 * to be used with the background color (not the highlighted background
	 * color) and will contrast with it.  The application should draw its
	 * borders using the stroke style returned <code>by getBorderStyle()</code>.
	 * 
	 * <p> <code>COLOR_HIGHLIGHTED_BORDER</code> has the value <code>5</code>.
	 */
	public static final int COLOR_HIGHLIGHTED_BORDER = 5;
	
	private final int argb;
	private final boolean isDynamic;
	private transient Integer integer;

	/**
	 * Creates a new color
	 * 
	 * @param argb the (A)RGB value of the color 
	 */
	public Color(int argb) {
		this( argb, false );
	}

	
	/**
	 * Creates a new color
	 * 
	 * @param argb the (A)RGB value of the color or Display.COLOR_BACKGROUND, Display.COLOR_FOREGROUND etc 
	 * @param isDynamic true when this color references one of the Display color fields like  Display.BACKGROUND_COLOR, Display.FOREGROUND_COLOR etc
	 * @see #COLOR_BACKGROUND
	 * @see #COLOR_BORDER
	 * @see #COLOR_FOREGROUND
	 * @see #COLOR_HIGHLIGHTED_BACKGROUND
	 * @see #COLOR_HIGHLIGHTED_BORDER
	 * @see #COLOR_HIGHLIGHTED_FOREGROUND
	 * @see #TRANSPARENT
	 */
	public Color(int argb, boolean isDynamic) {
		super();
		this.argb = argb;
		this.isDynamic = isDynamic;
	}
	
	/**
	 * Retrieves the integer representation of this color.
	 * This method allows a backwards compability with J2ME Polish components
	 * that use Style.getIntProperty() for retrieving colors
	 * 
	 * @return an Integer representing this color
	 * @throws IllegalStateException when this is a dynamic color and the StyleSheet.display variable is not yet set up.
	 *         The StyleSheet.display field is set within the startApp() method of the parent MIDlet. 
	 */
	public Integer getInteger() {
		if (this.integer == null) {
			this.integer= new Integer( getColor() ); 
		}
		return this.integer;
	}

	/**
	 * Retrieves the actual (A)RGB color value.
	 * 
	 * @return the color value
	 * @throws IllegalStateException when this is a dynamic color and the StyleSheet.display variable is not yet set up.
	 *         The StyleSheet.display field is set within the startApp() method of the parent MIDlet. 
	 */
	public int getColor() {
		if (!this.isDynamic) {
			return this.argb;
		}
		if (this.argb == TRANSPARENT) {
			return TRANSPARENT;
		}
		//#if !polish.midp2
			switch (this.argb) {
			case COLOR_BACKGROUND: return 0xFFFFFF;
			case COLOR_FOREGROUND: return 0;
			case COLOR_HIGHLIGHTED_BACKGROUND: return 0;
			case COLOR_HIGHLIGHTED_FOREGROUND: return 0xFFFFFF;
			case COLOR_BORDER: return 0;
			case COLOR_HIGHLIGHTED_BORDER: return 0xFFFFFF;
			//# default: return 0;
			}
		//#else
			Display display = StyleSheet.display;
			if (display == null) {
				//#if polish.debug.verbose
					throw new IllegalStateException("unable to retrieve dynamic color before the startApp() method has been called.");
				//#else
					//# throw new IllegalStateException();
				//#endif
			}
			return display.getColor(this.argb);
		//#endif
	}
	
	/**
	 * Determines whether this color represents transparent.
	 * 
	 * @return true when this is a dynamic color with TRANSPARENT as the color value
	 */
	public boolean isTransparent() {
		return this.isDynamic && (this.argb == TRANSPARENT);
	}
	
	/**
	 * Determines whether this color is a dynamic one like Display.COLOR_BACKGROUND
	 * 
	 * @return true when this is a dynamic color
	 */
	public boolean isDynamic() {
		return this.isDynamic;
	}
	
	/**
	 * Retrieves a string representation of this color.
	 * 
	 * @return the color as a hex string, when an IllegalStateException occurs, the super.toString() implementation will be used instead
	 */
	public String toString() {
		try {
			return Integer.toHexString( getColor() );
		} catch (IllegalStateException e) {
			return super.toString();
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(obj instanceof Color) {
			Color color = (Color)obj;
			return getColor() == color.getColor();
		} else {
			return false;
		}
	}
}
