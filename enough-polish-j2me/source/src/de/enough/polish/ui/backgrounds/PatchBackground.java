//#condition polish.usePolishGui

package de.enough.polish.ui.backgrounds;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Color;
import de.enough.polish.ui.Item;
import de.enough.polish.util.ImageUtil;

/**
 * Creates a patch-based background
 * @author Ovidiu
 *
 */
public class PatchBackground extends ImageResourceBackground {
	
	private final int leftWidth;
	private final int topHeight;
	private final int rightWidth;
	private final int bottomHeight;
	private final boolean isTransparent;
	
	/**
	 * 
	 * @param imageUrl
	 * @param leftWidth
	 * @param topHeight
	 * @param rightWidth
	 * @param bottomHeight
	 * @param isTransparent 
	 * @param style
	 */
	public PatchBackground( String imageUrl, int leftWidth, int topHeight, int rightWidth, int bottomHeight, boolean isTransparent) {
		super(imageUrl);
		
		this.leftWidth = leftWidth;
		this.topHeight = topHeight;
		this.rightWidth = rightWidth;
		this.bottomHeight = bottomHeight;
		this.isTransparent = isTransparent;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paint(int x, int y, int width, int height, Graphics g) {
		if (!this.isLoaded) {
			load();
		}
		
		int clipX = g.getClipX();
		int clipY = g.getClipY();
		int clipWidth = g.getClipWidth();
		int clipHeight = g.getClipHeight();
		paintHorizontalTiles(x, y, width, height, clipX, clipY, clipWidth, clipHeight, g);
		paintVerticalTiles(x, y, width, height, clipX, clipY, clipWidth, clipHeight, g);
		if (!this.isTransparent) {
			paintCenterTiles(x, y, width, height, clipX, clipY, clipWidth, clipHeight, g);
		}
		paintCorners(x, y, width,height,clipX, clipY, clipWidth, clipHeight, g);
	}
	
	private void paintCorners(int x, int y, int width, int height, int clipX, int clipY, int clipWidth, int clipHeight, Graphics g) {
		int dstX;
		int dstY;
		
		int srcX;
		int srcY;
		
		// draw top left
		srcX = 0;
		srcY = 0;
		dstX = x + srcX;
		dstY = y + srcY;
		drawRegion(this.image, srcX, srcY, this.leftWidth, this.topHeight, dstX, dstY, clipX, clipY, clipWidth, clipHeight, g);
		
		// draw top right
		srcX = this.imageWidth - this.rightWidth;
		srcY = 0;
		dstX = x + (width - this.rightWidth);
		dstY = y;
		drawRegion(this.image, srcX, srcY, this.rightWidth, this.topHeight, dstX, dstY, clipX, clipY, clipWidth, clipHeight, g);
		
		// draw bottom left
		srcX = 0;
		srcY = this.imageHeight - this.bottomHeight;
		dstX = x;
		dstY = y + (height - this.bottomHeight);
		drawRegion(this.image, srcX, srcY, this.leftWidth, this.bottomHeight, dstX, dstY, clipX, clipY, clipWidth, clipHeight, g);

		// draw bottom right
		srcX = this.imageWidth - this.rightWidth;
		srcY = this.imageHeight - this.bottomHeight;
		dstX = x + (width - this.rightWidth);
		dstY = y + (height - this.bottomHeight);
		drawRegion(this.image, srcX, srcY, this.rightWidth, this.bottomHeight, dstX, dstY, clipX, clipY, clipWidth, clipHeight, g);
	}
	
	private void paintHorizontalTiles(int x, int y, int width, int height, int clipX, int clipY, int clipWidth, int clipHeight, Graphics g) {
		int srcX = this.leftWidth;
		int srcY;
		
		int srcWidth = this.imageWidth - (this.leftWidth + this.rightWidth);
		int srcHeight = this.topHeight;
		
		int dstX = x + this.leftWidth;
		int dstY;
		
		int fillWidth = width - (this.leftWidth + this.rightWidth);
		
		storeClipping(g);
		g.clipRect(dstX, y, fillWidth, height);
		clipX = dstX;
		clipY = y;
		clipWidth = fillWidth;
		clipHeight = height;
		
		// draw horizontal top tiles:
		srcY = 0;
		dstY = y;
		
		for (int xOffset = 0; xOffset < fillWidth; xOffset = xOffset + srcWidth) {
			drawRegion(this.image, srcX, srcY, srcWidth, srcHeight, dstX + xOffset, dstY, clipX, clipY, clipWidth, clipHeight, g);
		}
		
		// draw horizontal bottom tiles:
		srcY = this.imageHeight - this.bottomHeight;
		dstY = y + (height - this.bottomHeight); 
		
		for (int xOffset = 0; xOffset < fillWidth; xOffset = xOffset + srcWidth) {
			drawRegion(this.image, srcX, srcY, srcWidth, srcHeight,dstX + xOffset, dstY, clipX, clipY, clipWidth, clipHeight, g);
		}
		
		restoreClipping(g);
	}
	
	/**
	 * Paint the vertical fill. The tile to paint the vertical fills
	 * is calculated with the 
	 * @param x the x position
	 * @param y the y position
	 * @param width the width
	 * @param height the height
	 * @param g the Graphics instance
	 */
	private void paintVerticalTiles(int x, int y, int width, int height, int clipX, int clipY, int clipWidth, int clipHeight, Graphics g) {
		// draw vertical left fill
		int srcX = 0;
		int srcY = this.topHeight;
		
		int srcWidth = this.leftWidth;
		int srcHeight = this.imageHeight - (this.topHeight + this.bottomHeight);
		
		int dstX = x;
		int dstY = y + this.topHeight;
		
		
		int fillHeight = height - this.topHeight - this.bottomHeight;
		
		storeClipping(g);
		g.clipRect(x, dstY, width, fillHeight);
		clipX = x;
		clipY = dstY;
		clipWidth = width;
		clipHeight = fillHeight;
		
		for (int yOffset = 0; yOffset < fillHeight; yOffset = yOffset + srcHeight) {
			drawRegion(this.image, srcX, srcY, srcWidth, srcHeight, dstX, dstY + yOffset, clipX, clipY, clipWidth, clipHeight, g);
		}
		
		// draw vertical right fill
		srcX = this.imageWidth - this.rightWidth;
		dstX = x + (width - this.rightWidth);
		
		for (int yOffset = 0; yOffset < fillHeight; yOffset = yOffset + srcHeight) {
			drawRegion(this.image, srcX, srcY, srcWidth, srcHeight, dstX, dstY + yOffset, clipX, clipY, clipWidth, clipHeight, g);
		}
		
		restoreClipping(g);
	}
	
	/**
	 * Fill the inner area of a patch with the specified color
	 * @param x the x position
	 * @param y the y position
	 * @param width the width
	 * @param height the height
	 * @param g the Graphics instance
	 */
	private void paintCenterTiles(int x, int y, int width, int height, int clipX, int clipY, int clipWidth, int clipHeight, Graphics g) {
		int dstX = x + this.leftWidth;
		int dstY = y +  this.topHeight;
		
		int fillWidth = width - (this.leftWidth + this.rightWidth);
		int fillHeight = height - this.topHeight - this.bottomHeight;

		int srcX = this.leftWidth;
		int srcY = this.topHeight;
		int srcWidth = this.imageWidth - (this.leftWidth + this.rightWidth);
		int srcHeight = this.imageHeight - (this.topHeight + this.bottomHeight);
		
		storeClipping(g);
		g.clipRect(dstX, y, fillWidth, height);
		clipX = dstX;
		clipY = dstY;
		clipWidth = fillWidth;
		clipHeight = fillHeight;


		for (int yOffset = 0; yOffset < fillHeight; yOffset = yOffset + srcHeight) {
			for (int xOffset = 0; xOffset < fillWidth; xOffset = xOffset + srcWidth) {
				drawRegion(this.image, srcX, srcY, srcWidth, srcHeight, dstX + xOffset, dstY + yOffset, clipX, clipY, clipWidth, clipHeight, g);
			}

		}
		
		restoreClipping(g);

	}
}
