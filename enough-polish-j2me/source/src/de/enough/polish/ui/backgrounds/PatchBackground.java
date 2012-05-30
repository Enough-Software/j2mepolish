//#condition polish.usePolishGui

package de.enough.polish.ui.backgrounds;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;


/**
 * Creates a patch-based background
 * 
 * @author Ovidiu Iliescu, Andre Schmidt, Robert Virkus
 *
 */
public class PatchBackground extends ImageResourceBackground {
	
	private final int leftWidth;
	private final int topHeight;
	private final int rightWidth;
	private final int bottomHeight;
	private final boolean isTransparent;
	
	/**
	 * Creates a new patch background
	 * @param imageUrl the URL to the image
	 * @param leftWidth the width of the left edge
	 * @param topHeight the height of the top edge
	 * @param rightWidth the width of the right edge
	 * @param bottomHeight the height of the bottom edge
	 * @param isTransparent true when no center tiles should be painted
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
			if (this.imageWidth - (this.leftWidth + this.rightWidth)  < 1) {
				throw new IllegalArgumentException("Invalid patch-width: left=" + this.leftWidth + ", right=" + this.rightWidth + ", image-width=" + this.imageWidth + " of " + this.imageUrl);
			}
			if (this.imageHeight- (this.topHeight + this.bottomHeight)  < 1) {
				throw new IllegalArgumentException("Invalid patch-height: top=" + this.topHeight + ", bottom=" + this.bottomHeight + ", image-height=" + this.imageHeight + " of " + this.imageUrl);
			}
		}
		int clipX = g.getClipX();
		int clipY = g.getClipY();
		int clipWidth = g.getClipWidth();
		int clipHeight = g.getClipHeight();
		if ((clipY + clipHeight < y) || (clipY > y + height)) {
			return;
		}
		g.setColor(0xff0000);
		g.fillRect(x, y, width, height );
		//System.out.println("patch start: clipX=" + g.getClipX() + ", clipY=" + (g.getClipY()) +", clip.width=" + g.getClipWidth() + ", clip.height=" + g.getClipHeight());
		paintHorizontalTiles(x, y, width, height, clipX, clipY, clipWidth, clipHeight, g);
		paintVerticalTiles(x, y, width, height, clipX, clipY, clipWidth, clipHeight, g);
		if (!this.isTransparent) {
			paintCenterTiles(x, y, width, height, clipX, clipY, clipWidth, clipHeight, g);
		}
		paintCorners(x, y, width,height,clipX, clipY, clipWidth, clipHeight, g);
		//System.out.println("patch end: clipX=" + g.getClipX() + ", clipY=" + (g.getClipY()) +", clip.width=" + g.getClipWidth() + ", clip.height=" + g.getClipHeight());
	}
	
	private void paintCorners(int x, int y, int width, int height, int clipX, int clipY, int clipWidth, int clipHeight, Graphics g) {
		int dstX;
		int dstY;
		
		int srcX;
		int srcY;
		
		int srcWidth;
		int srcHeight;
		
		int minLeftWidth = Math.min( this.leftWidth, width);
		int minRightWidth = Math.min( this.rightWidth, width);
		int minTopHeight = Math.min(this.topHeight, height);
		int minBottomHeight = Math.min(this.bottomHeight, height);
		
		// draw top left
		srcX = 0;
		srcY = 0;
		srcWidth = minLeftWidth;
		srcHeight = minTopHeight;
		dstX = x + srcX;
		dstY = y + srcY;
		drawRegion(this.image, srcX, srcY,srcWidth, srcHeight, dstX, dstY, clipX, clipY, clipWidth, clipHeight, g);
		
		// draw top right
		srcX = this.imageWidth - this.rightWidth;
		srcY = 0;
		srcWidth = minRightWidth;
		srcHeight = minTopHeight;
		dstX = x + (width - minRightWidth);
		dstY = y;
		//drawRegion(this.image, srcX, srcY, this.rightWidth, this.topHeight, dstX, dstY, clipX, clipY, clipWidth, clipHeight, g);
		drawRegion(this.image, srcX, srcY,srcWidth, srcHeight, dstX, dstY, clipX, clipY, clipWidth, clipHeight, g);
		
		// draw bottom left
		srcX = 0;
		srcY = this.imageHeight - minBottomHeight;
		srcWidth = minLeftWidth;
		srcHeight = minBottomHeight;
		dstX = x;
		dstY = y + (height - minBottomHeight);
		drawRegion(this.image, srcX, srcY,srcWidth, srcHeight, dstX, dstY, clipX, clipY, clipWidth, clipHeight, g);

		// draw bottom right
		srcX = this.imageWidth - this.rightWidth;
		srcY = this.imageHeight - minBottomHeight;
		srcWidth = minRightWidth;
		srcHeight = minBottomHeight;
		dstX = x + (width - minRightWidth);
		dstY = y + (height - minBottomHeight);
		drawRegion(this.image, srcX, srcY,srcWidth, srcHeight, dstX, dstY, clipX, clipY, clipWidth, clipHeight, g);
	}
	
	private void paintHorizontalTiles(int x, int y, int width, int height, int clipX, int clipY, int clipWidth, int clipHeight, Graphics g) {
		int srcX = this.leftWidth;
		int srcY;
		
		int srcWidth = this.imageWidth - (this.leftWidth + this.rightWidth);
		int srcHeight = Math.min( this.topHeight, height);
		//System.out.println("horizontal tiles: srcWidth=" + srcWidth + ", srcHeight=" + srcHeight);
		
		int dstX = x + this.leftWidth;
		int dstY;
		
		int fillWidth = width - (this.leftWidth + this.rightWidth);
		storeClipping(g);
		clipX = dstX;
		clipY = y;
		clipWidth = fillWidth;
		clipHeight = height;
		g.clipRect(clipX, clipY, clipWidth, clipHeight);
		clipX = g.getClipX();
		clipY = g.getClipY();
		clipWidth = g.getClipWidth();
		clipHeight = g.getClipHeight();
		if (clipWidth == 0 || clipHeight == 0) {
			restoreClipping(g);
			return;
		}
		
		// draw horizontal top tiles:
		srcY = 0;
		dstY = y;
		
		for (int xOffset = 0; xOffset < fillWidth; xOffset = xOffset + srcWidth) {
			drawRegion(this.image, srcX, srcY, srcWidth, srcHeight, dstX + xOffset, dstY, clipX, clipY, clipWidth, clipHeight, g);
		}
		
		// draw horizontal bottom tiles:
		srcHeight = Math.min( this.bottomHeight, height);
		srcY = this.imageHeight - srcHeight;
		dstY = y + height - srcHeight;
		
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
		int srcX = 0;
		int srcY = this.topHeight;
		
		int srcWidth = this.leftWidth;
		int srcHeight = this.imageHeight - (this.topHeight + this.bottomHeight);
		
		int dstX = x;
		int dstY = y + this.topHeight;
		
		
		int fillHeight = height - this.topHeight - this.bottomHeight;
		
		storeClipping(g);
		clipX = x;
		clipY = dstY;
		clipWidth = width;
		clipHeight = fillHeight;
		g.clipRect(clipX, clipY, clipWidth, clipHeight);
		clipX = g.getClipX();
		clipY = g.getClipY();
		clipWidth = g.getClipWidth();
		clipHeight = g.getClipHeight();
		if (clipWidth == 0 || clipHeight == 0) {
			restoreClipping(g);
			return;
		}
		
		// draw vertical left fill:
		for (int yOffset = 0; yOffset < fillHeight; yOffset = yOffset + srcHeight) {
			drawRegion(this.image, srcX, srcY, srcWidth, srcHeight, dstX, dstY + yOffset, clipX, clipY, clipWidth, clipHeight, g);
		}
		
		// draw vertical right fill:
		srcX = this.imageWidth - this.rightWidth;
		srcWidth = this.rightWidth;
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
		int fillHeight = height - (this.topHeight + this.bottomHeight);
		

		int srcX = this.leftWidth;
		int srcY = this.topHeight;
		int srcWidth = this.imageWidth - (this.leftWidth + this.rightWidth);
		int srcHeight = this.imageHeight - (this.topHeight + this.bottomHeight);
		
		storeClipping(g);
		clipX = dstX;
		clipY = dstY;
		clipWidth = fillWidth;
		clipHeight = fillHeight;
		g.clipRect(clipX, clipY, clipWidth, clipHeight);
		clipX = g.getClipX();
		clipY = g.getClipY();
		clipWidth = g.getClipWidth();
		clipHeight = g.getClipHeight();
		if (clipWidth == 0 || clipHeight == 0) {
			restoreClipping(g);
			return;
		}


		for (int yOffset = 0; yOffset < fillHeight; yOffset = yOffset + srcHeight) {
			for (int xOffset = 0; xOffset < fillWidth; xOffset = xOffset + srcWidth) {
				drawRegion(this.image, srcX, srcY, srcWidth, srcHeight, dstX + xOffset, dstY + yOffset, clipX, clipY, clipWidth, clipHeight, g);
			}
		}
		
		restoreClipping(g);


	}
}
