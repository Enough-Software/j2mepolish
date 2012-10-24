//#condition polish.usePolishGui
/*
 * Created on 16-Nov-2005 at 11:59:50.
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

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

//import de.enough.polish.io.Serializable;
import de.enough.polish.io.Serializable;
import de.enough.polish.util.DrawUtil;
import de.enough.polish.util.TextUtil;
import de.enough.polish.util.WrappedText;

/**
 * <p>Allows text effects for StringItems, IconItems and ChoiceItems.</p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2009</p>
 * <pre>
 * history
 *        16-Nov-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class TextEffect implements Serializable
{
	protected transient Style style;
	/**
	 * Specifies if this effect needs a lot of texte dependent resources or processing power.
	 * When this is the case, StringItems will create a StringItem specific copy of this effect instead of
	 * using the effect from the Style (which may be used several times). 
	 */
	protected boolean isTextSensitive;

	/**
	 * Creates a new effect
	 */
	public TextEffect() {
		super();
	}
	
	/**
	 * Sets the style of this item.
	 * The implementation sets the style field and then calls setStyle( style, false ).
	 * Subclasses can override this method for getting specific settings.
	 * 
	 * @param style the new style for this item.
	 * @throws NullPointerException when style is null
	 * @see #setStyle(Style, boolean)
	 */
	public void setStyle( Style style ) {
		this.style = style;
		setStyle( style, false );
	}
	
	/**
	 * Notifies the text effect that it has been attached to the specified item.
	 * Subclasses can override this method to get access to the parent item.
	 * @param parent the parent item
	 */
	public void onAttach(Item parent){
		// subclasses may choose to override this.
	}
	
	/**
	 * Notifies the text effect that it has been detached to the specified item
	 * Subclasses can override this method to get access to the parent item.
	 * @param parent the parent item
	 */
	public void onDetach(Item parent){
		// subclasses may choose to override this.
	}
	
	/**
	 * Sets the style of this item without assuming defaults for non-set style elements.
	 * Subclasses can override this method for getting specific settings.
	 * 
	 * @param style the new style for this item.
	 * @param resetStyle true when all style elements should be reset to their default when no CSS attributes are defined.
	 * @throws NullPointerException when style is null
	 */
	public void setStyle( Style style, boolean resetStyle ) {
		// subclasses may choose to override this.
	}
	
	/**
	 * Animates this effect.
	 * Subclasses can override this method to create animations.
	 * 
	 * @return true when this effect has been animated.
	 */
	public boolean animate() {
		return false;
	}
	
	/**
	 * Animates this effect.
	 * Subclasses can override this method to create animations.
	 * The default implementation calls the animate() method and adds the full content area to the repaint region.
	 * 
	 * @param parent the parent item
	 * @param currentTime the current time in milliseconds
	 * @param repaintRegion the repaint area that needs to be updated when this item is animated
	 * @see Item#addRelativeToContentRegion(ClippingRegion, int, int, int, int)
	 */
	public void animate(Item parent, long currentTime, ClippingRegion repaintRegion) 
	{
		if (animate()) {
			parent.addRelativeToContentRegion( repaintRegion, 0, 0, parent.contentWidth, parent.contentHeight );
		}
	}
	
	//#if polish.LibraryBuild
	/**
	 * Paints the text and applies the text effect.
	 * The default implementation calls drawText( String, int, int, int, int, int, Graphics)
	 * 
	 * @param parent the parent item
	 * @param textLines the text
	 * @param textColor the color of the text
	 * @param x horizontal start coordinate
	 * @param y vertical start coordinate
	 * @param leftBorder the left border, nothing must be painted left of this position
	 * @param rightBorder the right border, nothing must be painted right of this position
	 * @param lineHeight the height of a single text line
	 * @param maxWidth the width of the longest line
	 * @param layout the anchor or the text, e.g. Item.LAYOUT_CENTER or Item.LAYOUT_RIGHT
	 * @param g the graphics context
	 * @see #drawString( String,int,int,int,int,Graphics)
	 */
	public void drawStrings( FakeCustomItem parent, WrappedText textLines, int textColor, int x, int y, int leftBorder, int rightBorder, int lineHeight, int maxWidth, int layout, Graphics g ) {
		drawStrings(textLines, textColor, x, y, leftBorder, rightBorder, lineHeight, maxWidth, layout, g);
	}
	//#endif

	/**
	 * Paints the text and applies the text effect.
	 * The default implementation calls drawStrings(String[], int, int, int, int, int, int, int, int, Graphics)
	 * 
	 * @param parent the parent item
	 * @param textLines the text
	 * @param textColor the color of the text
	 * @param x horizontal start coordinate
	 * @param y vertical start coordinate
	 * @param leftBorder the left border, nothing must be painted left of this position
	 * @param rightBorder the right border, nothing must be painted right of this position
	 * @param lineHeight the height of a single text line
	 * @param maxWidth the width of the longest line
	 * @param layout the anchor or the text, e.g. Item.LAYOUT_CENTER or Item.LAYOUT_RIGHT
	 * @param g the graphics context
	 * @see #drawStrings(WrappedText, int, int, int, int, int, int, int, int, Graphics)
	 */
	public void drawStrings( Item parent, WrappedText textLines, int textColor, int x, int y, int leftBorder, int rightBorder, int lineHeight, int maxWidth, int layout, Graphics g ) {
		drawStrings(textLines, textColor, x, y, leftBorder, rightBorder, lineHeight, maxWidth, layout, g);
	}
	
	/**
	 * Paints the text and applies the text effect.
	 * The default implementation calls drawText( String, int, int, int, int, int, Graphics)
	 * 
	 * @param textLines the text
	 * @param textColor the color of the text
	 * @param x horizontal start coordinate
	 * @param y vertical start coordinate
	 * @param leftBorder the left border, nothing must be painted left of this position
	 * @param rightBorder the right border, nothing must be painted right of this position
	 * @param lineHeight the height of a single text line
	 * @param maxWidth the width of the longest line
	 * @param layout the anchor or the text, e.g. Item.LAYOUT_CENTER or Item.LAYOUT_RIGHT
	 * @param g the graphics context
	 * @see #drawString( String,int,int,int,int,Graphics)
	 */
	public void drawStrings( WrappedText textLines, int textColor, int x, int y, int leftBorder, int rightBorder, int lineHeight, int maxWidth, int layout, Graphics g ) {
		boolean isLayoutRight = false;
		boolean isLayoutCenter = false;
		int centerX = 0;
		if ( ( layout & Item.LAYOUT_CENTER ) == Item.LAYOUT_CENTER ) {
			isLayoutCenter = true;
			centerX = leftBorder + (rightBorder - leftBorder) / 2;
		} else if ( ( layout & Item.LAYOUT_RIGHT ) == Item.LAYOUT_RIGHT ) {
			isLayoutRight = true;
		}
		Object[] lineObjects = textLines.getLinesInternalArray();
		int size = textLines.size();
		for (int i = 0; i < size; i++) {
			String line = (String) lineObjects[i];
			int lineX = x;
			int lineY = y;
			int anchor = 0;
			// adjust the painting according to the layout:
			if (isLayoutRight) {
				lineX = rightBorder;
				//#if polish.Bugs.needsBottomOrientiationForStringDrawing
					anchor = Graphics.BOTTOM | Graphics.RIGHT;
				//#else
					anchor = Graphics.TOP | Graphics.RIGHT;
				//#endif
			} else if (isLayoutCenter) {
				lineX = centerX;
				//#if polish.Bugs.needsBottomOrientiationForStringDrawing
					anchor = Graphics.BOTTOM | Graphics.HCENTER;
				//#else
					anchor = Graphics.TOP | Graphics.HCENTER;
				//#endif
			} else {
				//#if polish.Bugs.needsBottomOrientiationForStringDrawing
					anchor = Graphics.BOTTOM | Graphics.LEFT;
				//#else
					anchor = Graphics.TOP | Graphics.LEFT;
				//#endif
			}
			
			drawString( line, textColor, lineX, lineY, anchor, g );
			x = leftBorder;
			y += lineHeight;
		}
		
	}

	/**
	 * Paints the text and applies the text effect.
	 * 
	 * @param text the text
	 * @param textColor the color of the text
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param anchor the orientation, e.g. Graphics.TOP | Graphics.LEFT or Graphics.TOP | Graphics.HCENTER
	 * @param g the graphics context
	 */
	public abstract void drawString( String text, int textColor, int x, int y, int anchor, Graphics g );

	/**
	 * Retrieves the left start position for a text.
	 * 
	 * @param x the x position given in drawString()
	 * @param anchor the orientation given in drawString()
	 * @param textWidth the width of the text given in drawString()
	 * @return the left x position
	 */
	public int getLeftX( int x, int anchor, int textWidth  ) {
		if ( (anchor & Graphics.LEFT) == Graphics.LEFT) {
			return x;
		} else if ( (anchor & Graphics.RIGHT) == Graphics.RIGHT) {
			return x - textWidth;
		} else {
			return x - textWidth / 2;
		}
	}

	/**
	 * Retrieves the top y position for a text.
	 *  
	 * @param y the y position given in drawString()
	 * @param anchor the orientation given in drawString()
	 * @param font the used font, usually g.getFont()
	 * @return the top y position.
	 */
	public int getTopY( int y, int anchor, Font font ) {
		return getTopY(y, anchor, font.getHeight(), font.getBaselinePosition() );
	}

	/**
	 * Retrieves the top y position for a text.
	 *  
	 * @param y the y position given in drawString()
	 * @param anchor the orientation given in drawString()
	 * @param height the height of the used font
	 * @param baseLine the base line of the used font
	 * @return the top y position.
	 */
	public int getTopY( int y, int anchor, int height, int baseLine ) {
		if ( (anchor & Graphics.TOP) == Graphics.TOP) {
			return y;
		} else if ( (anchor & Graphics.BOTTOM) == Graphics.BOTTOM) {
			return y - height;
		} else {
			return y - (height - baseLine);
		}
	}
	
	//#if polish.midp2
	/**
	 * Retrieves an RGB integer array in which the text is written on MIDP 2.0 devices.
	 * NOTE: this method is not available on MIDP 1.0 devices!
	 * You can determine the height and width of the produced RGB data with this code:
	 * <pre>
	 * int[] rgbData = getRgbData(text, textColor, font);
	 * int height = font.getHeight();
	 * int width = rgbData.length / height;
	 * </pre>
	 * 
	 * @param text the text
	 * @param textColor the color of the text
	 * @param font the font of the text
	 * @return the RGB data that contains the given text
	 */
	public static int[] getRgbData( String text, int textColor, Font font ) {
		int transparentColor = DrawUtil.getComplementaryColor( textColor );
		if (transparentColor == textColor ) {
			transparentColor = 0;
		}
		int width = font.stringWidth( text );
		int height = font.getHeight();
		return getRgbData( text, textColor, font, 0, 0, width, height, transparentColor );
		
	}
	//#endif

	//#if polish.midp2
	/**
	 * Retrieves an RGB integer array in which the text is written on MIDP 2.0 devices.
	 * NOTE: this method is not available on MIDP 1.0 devices!
	 * 
	 * @param text the text
	 * @param textColor the color of the text
	 * @param font the font of the text
	 * @param x the left corner of the text in the created rgb data
	 * @param y the top corner of the text in the created rgb data
	 * @param width the desired width of the data array array, e.g. font.stringWidth(text) 
	 * @param height the desired height of the data array, e.g. font.getHeight()
	 * @return the RGB data that contains the given text
	 * @see DrawUtil#getComplementaryColor(int)
	 */
	public static int[] getRgbData(String text, int textColor, Font font, int x, int y, int width, int height ) {
		int transparentColor = DrawUtil.getComplementaryColor( textColor );
		if (transparentColor == textColor ) {
			transparentColor = 0;
		}
		return getRgbData( text, textColor, font, x, y, width, height, transparentColor );
	}
	//#endif
		
	//#if polish.midp2
	/**
	 * Retrieves an RGB integer array in which the text is written on MIDP 2.0 devices.
	 * NOTE: this method is not available on MIDP 1.0 devices!
	 * 
	 * @param text the text
	 * @param textColor the color of the text
	 * @param font the font of the text
	 * @param x the left corner of the text in the created rgb data
	 * @param y the top corner of the text in the created rgb data
	 * @param width the desired width of the data array array, e.g. font.stringWidth(text) 
	 * @param height the desired height of the data array, e.g. font.getHeight()
	 * @param transparentColor the color that should be used to flag transparent parts, using DrawUtil.getComplementaryColor( textColor ) might be a good idea
	 * @return the RGB data that contains the given text
	 * @see DrawUtil#getComplementaryColor(int)
	 */
	public static int[] getRgbData(String text, int textColor, Font font, int x, int y, int width, int height, int transparentColor) {
		// create Image, Graphics, ARGB-buffer
		Graphics bufferG;
		Image midp2ImageBuffer = Image.createImage( width, height); 
		bufferG = midp2ImageBuffer.getGraphics();
		
		// draw pseudo transparent Background
		bufferG.setColor( transparentColor );
		bufferG.fillRect(0,0,width, height);
		
		// draw String on Graphics
		bufferG.setFont(font);
		bufferG.setColor( textColor );
		bufferG.drawString(text, x, y, Graphics.LEFT | Graphics.TOP);
		
		// get RGB-Data from Image
		int[] rgbData = new int[ width * height ];
		midp2ImageBuffer.getRGB( rgbData,0,width, 0, 0, width, height);
		
		// check clearColor
		int[] clearColorArray = new int[1]; 
		midp2ImageBuffer.getRGB(clearColorArray, 0, 1, 0, 0, 1, 1 );
		transparentColor = clearColorArray[0];
		
		// transform RGB-Data
		for (int i=0; i<rgbData.length;i++){
			//	 perform Transparency
			if  (rgbData[i] == transparentColor){
				rgbData[i] = 0x00000000;
			}
		}
		
		return rgbData;
	}
	//#endif
	

	/**
	 * Notifies this effect that the corresponding item is to be shown.
	 * The default implementation is empty. 
	 */
	public void showNotify() {
		// default implementation is empty
	}
	
	/**
	 * Notifies this effect that the corresponding item is to be hidden.
	 * The default implementation is empty. 
	 */
	public void hideNotify() {
		// default implementation is empty
	}

	/**
	 * Releases any resources this effect might contain.
	 * For staying future proof subclasses should call super.releaseResources() first, when overriding this method.
	 */
	public void releaseResources() {
		// do nothing
	}

	/**
	 * Calculates the width of the given text.
	 * By default getFont().stringWidth(text) is returned.
	 * 
	 * @param str the text of which the width should be determined
	 * @return the width of the text
	 */
	public int stringWidth(String str) {
		return getFont().stringWidth(str);
	}
	
	/**
	 * Retrieves the width of the given char
	 * @param c the char
	 * @return the width of that char
	 */
	public int charWidth( char c) {
		return getFont().charWidth(c);
	}

	
	/**
	 * Retrieves the font height by default.
	 * @return the height of the font
	 */
	public int getFontHeight() {
		return getFont().getHeight();
	}
	/**
	 * Retrieves the font that should be used.
	 * 
	 * @return the font
	 */
	protected Font getFont() {
		if (this.style != null && this.style.getFont() != null) {
			return this.style.getFont();
		}
		return Font.getDefaultFont();
	}
	
	/**
	 * Wraps the text into several lines and adds the result to the specified wrappedText.
	 * The default implementation just calls TextUtil.wrap(text, font, firstLineWidth, lineWidth, maxLines, maxLinesAppendix).
	 * 
	 * @param parent the parent of this effect
	 * @param text the text
	 * @param textColor color of the text
	 * @param font used font
	 * @param firstLineWidth width of the first line
	 * @param lineWidth width of following lines
	 * @param maxLines the maximum number of lines
	 * @param maxLinesAppendix the appendix that should be added to the last line when the line number is greater than maxLines
	 * @param maxLinesAppendixPosition either TextUtil.MAXLINES_APPENDIX_POSITION_AFTER or TextUtil.MAXLINES_APPENDIX_POSITION_BEFORE
	 * @param wrappedText the wrapped text object to which the single text lines should be added
	 * @see TextUtil#wrap(String, Font, int, int, int, String, int)
	 */
	public void wrap(StringItem parent, String text, int textColor, Font font, int firstLineWidth, int lineWidth, int maxLines, String maxLinesAppendix, int maxLinesAppendixPosition, WrappedText wrappedText) {
		TextUtil.wrap(text, font, firstLineWidth, lineWidth, maxLines, maxLinesAppendix, maxLinesAppendixPosition, wrappedText);
	}
	
	//#if polish.libraryBuild
	/**
	 * Wraps the text into several lines and adds the result to the specified wrappedText.
	 * The default implementation just calls TextUtil.wrap(text, font, firstLineWidth, lineWidth, maxLines, maxLinesAppendix).
	 * 
	 * @param parent the parent of this effect
	 * @param text the text
	 * @param textColor color of the text
	 * @param font used font
	 * @param firstLineWidth width of the first line
	 * @param lineWidth width of following lines
	 * @param maxLines the maximum number of lines
	 * @param maxLinesAppendix the appendix that should be added to the last line when the line number is greater than maxLines
	 * @param maxLinesAppendixPosition either TextUtil.MAXLINES_APPENDIX_POSITION_AFTER or TextUtil.MAXLINES_APPENDIX_POSITION_BEFORE
	 * @param wrappedText the wrapped text object to which the single text lines should be added
	 * @see TextUtil#wrap(String, Font, int, int, int, String, int)
	 */
	public void wrap(FakeStringCustomItem parent, String text, int textColor, Font font, int firstLineWidth, int lineWidth, int maxLines, String maxLinesAppendix, int maxLinesAppendixPosition, WrappedText wrappedText) {
		TextUtil.wrap(text, font, firstLineWidth, lineWidth, maxLines, maxLinesAppendix, maxLinesAppendixPosition, wrappedText);
	}
	//#endif


	/**
	 * Draws the specified character using this effect.
	 * Subclasses may override this - by default the character is just painted.
	 * 
	 * @param c the character 
	 * @param x horizontal position
	 * @param y vertical position
	 * @param anchor anchor, e.g. Graphics.TOP | Graphics.LEFT
	 * @param g the graphics context
	 */
	public void drawChar(char c, int x, int y, int anchor, Graphics g)
	{
		g.drawChar(c, x, y, anchor);		
	}
	
	/**
	 * Calculates the content height with the lines, the lineheight and the vertical padding
	 * @param lines the lines
	 * @param lineHeight the lineheight
	 * @param paddingVertical the vertical padding
	 * @return the height in pixels, normally (lines.size() * lineHeight) - paddingVertical;
	 */
	public int calculateLinesHeight(WrappedText lines, int lineHeight, int paddingVertical) {
		return  (lines.size() * lineHeight) - paddingVertical;
	}
}
