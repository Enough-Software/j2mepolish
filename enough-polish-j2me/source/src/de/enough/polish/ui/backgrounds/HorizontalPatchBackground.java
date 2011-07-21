//#condition polish.usePolishGui

package de.enough.polish.ui.backgrounds;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.Sprite;

/**
 * A horizontal patch-based background
 * @author Ovidiu
 *
 */
public class HorizontalPatchBackground extends ImageResourceBackground {
	
	private final int cornerWidth;
	private final int marginLeft;
	private final int marginTop;
	private final int marginRight;
	private final int marginBottom;
	
	/**
	 * Creates a new instance
	 * @param imageUrl
	 * @param cornerWidth
	 * @param marginLeft
	 * @param marginTop
	 * @param marginRight
	 * @param marginBottom
	 */
	public HorizontalPatchBackground( String imageUrl, int cornerWidth, int marginLeft, int marginTop, int marginRight, int marginBottom ) {
		super(imageUrl);
		
		this.cornerWidth = cornerWidth;
		this.marginLeft = marginLeft;
		this.marginTop = marginTop;
		this.marginRight = marginRight;
		this.marginBottom = marginBottom;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		if (!this.isLoaded) {
			load();
		}
		x -= this.marginLeft;
		y -= this.marginTop; 
		
		width = width + this.marginLeft + this.marginRight;
		height = height + this.marginTop + this.marginBottom;
		
		paintTiles(x,y,width,height,g);
		paintFill(x,y,width,height,g);
	}
	
	/**
	 * Paints the background's tiles
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param g
	 */
	private void paintTiles(int x, int y, int width, int height, Graphics g) {
		int dstX;
		int dstY;
		
		int srcX;
		int srcY;
		
		// draw left
		srcX = 0;
		srcY = 0;
		dstX = x;
		dstY = y;
		g.drawRegion(this.image, srcX, srcY, this.cornerWidth, this.image.getHeight(), Sprite.TRANS_NONE, dstX, dstY, Graphics.LEFT | Graphics.TOP);
	
		// draw right
		srcX = this.image.getWidth() - this.cornerWidth;
		srcY = 0;
		dstX = x + (width - this.cornerWidth);
		dstY = y;
		g.drawRegion(this.image, srcX, srcY, this.cornerWidth, this.image.getHeight(), Sprite.TRANS_NONE, dstX, dstY, Graphics.LEFT | Graphics.TOP);
	}
	
	/**
	 * Fills a region of the @Graphics object
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param g
	 */
	private void paintFill(int x, int y, int width, int height, Graphics g) {
		int dstX;
		int dstY;
		
		int srcX;
		int srcY;
		
		int srcWidth;
		int srcHeight;
		
		int fillWidth;
		int fillHeight;
		
		// draw horizontal top fill
		srcX = this.cornerWidth;
		srcY = 0;
		
		srcWidth = image.getWidth() - (this.cornerWidth * 2);
		srcHeight = image.getHeight();
		
		dstX = x + this.cornerWidth;
		dstY = y;
		
		fillWidth = width - (this.cornerWidth * 2);
		fillHeight = srcHeight;
		
		storeClipping(g);
		g.clipRect(dstX, dstY, fillWidth, fillHeight);
		for (int xOffset = 0; xOffset < fillWidth; xOffset = xOffset + srcWidth) {
			g.drawRegion(this.image, srcX, srcY, srcWidth, srcHeight, Sprite.TRANS_NONE, dstX + xOffset, dstY, Graphics.LEFT | Graphics.TOP);
		}
		restoreClipping(g);
	}
}
