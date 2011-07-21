//#condition polish.usePolishGui
package de.enough.polish.ui.backgrounds;

import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.ui.Background;
import de.enough.polish.ui.ImageConsumer;
import de.enough.polish.ui.StyleSheet;

public abstract class ImageResourceBackground 
extends Background 
//#ifdef polish.images.backgroundLoad
implements ImageConsumer
//#endif
{
	
	protected String imageUrl;
	protected boolean isLoaded;
	protected Image image;
	protected int imageWidth;
	protected int imageHeight;
	
	/**
	 * clipping variables for temporary storage of the clipping
	 */
	int storedClipX;
	int storedClipY;
	int storedClipWidth;
	int storedClipHeight;
	
	/**
	 * Creates a new image based background
	 * @param url the URL of the image resource
	 * @see #load() for loading the image
	 */
	public ImageResourceBackground(String url) {
		this.imageUrl = url;
	}
	
	protected void load() {
		if (!this.isLoaded) {
			try {
				this.image = StyleSheet.getImage(this.imageUrl, this, false);
				this.imageWidth = this.image.getWidth();
				this.imageHeight = this.image.getHeight();
			} catch (IOException e) {
				//#debug error
				System.out.println( "unable to load image [" + this.imageUrl + "]" + e );
			}
			this.isLoaded = true;
		}
	}
	
	/**
	 * Stores the clipping
	 * 
	 * @param g
	 *            the Graphics instance
	 */
	protected void storeClipping(Graphics g) {
		this.storedClipX = g.getClipX();
		this.storedClipY = g.getClipY();
		this.storedClipWidth = g.getClipWidth();
		this.storedClipHeight = g.getClipHeight();
	}

	/**
	 * Restores the clipping
	 * 
	 * @param g
	 *            the Graphics instance
	 */
	protected void restoreClipping(Graphics g) {
		g.setClip(this.storedClipX, this.storedClipY, this.storedClipWidth, this.storedClipHeight);
	}
	
	/**
	 * Sets the image for this background.
	 * 
	 * @param image the image
	 */
	public void setImage( Image image ) {
		this.image = image;
		this.isLoaded = (image != null);
	}
	
	/**
	 * Retrieves the image from this background.
	 * 
	 * @return the image
	 */
	public Image getImage() {
		return this.image;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Background#releaseResources()
	 */
	public void releaseResources() {
		if (this.imageUrl != null) {
			this.isLoaded = false;
			this.image = null;
		}
	}
	
	//#ifdef polish.images.backgroundLoad
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ImageConsumer#setImage(java.lang.String, javax.microedition.lcdui.Image)
	 */
	public void setImage(String url, Image image) {
		this.image = image;
	}
	//#endif
	
	
	protected void drawRegion( Image img, int x_src, int y_src, int width, int height, int x_dest, int y_dest, int clipX, int clipY, int clipWidth, int clipHeight, Graphics g) {
		g.clipRect( x_dest, y_dest, width, height );
		g.drawImage(img, x_dest - x_src, y_dest - y_src, Graphics.TOP | Graphics.LEFT );
		g.setClip(clipX, clipY, clipWidth, clipHeight);
	}
}
