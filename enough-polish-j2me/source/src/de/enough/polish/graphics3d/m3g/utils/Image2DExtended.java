//#condition polish.midp2 && polish.javapackage.jsr184

package de.enough.polish.graphics3d.m3g.utils;

import javax.microedition.lcdui.Image;
import javax.microedition.m3g.Image2D;

/**
 * Image2D wrapper class.
 * <p>
 * Enables easy replacement of image held by the Image2D object.
 * <p>
 * Obvious drawback is memory overhead of int[] representation
 * 
 * @author Anders Bo Pedersen, anders@wicore.dk
 */
public class Image2DExtended extends Image2D
{
	private int[] sourceRgb;
	
	/**
	 * 
	 * @param format
	 * @param image
	 */
	public Image2DExtended(int format, Object image)
	{
		super(format, ((Image) image).getWidth(), ((Image) image).getHeight() );
		
		//extract int[] representation
		this.sourceRgb = new int[ getWidth() * getHeight()];
		((Image)image).getRGB(this.sourceRgb, 0, ((Image) image).getWidth(), 0, 0, ((Image) image).getWidth(), ((Image) image).getHeight());
		
		//convert to byte[]
		byte[] rgbImageByte = UtilitiesM3G.getImage2DByteRepresentaionFromImage( format, this.sourceRgb );
		
		//set via super method to ensure Image2D is mutable
		super.set(0, 0, getWidth(), getHeight(), rgbImageByte);
	}
	
	/**
	 * 
	 * @param format
	 * @param width
	 * @param height
	 * @param imageRgb
	 */
	public Image2DExtended(int format, int width, int height, int[] imageRgb)
	{
		super(format, width, height );
		
		this.sourceRgb = imageRgb;
		
		//convert to byte[]
		byte[] rgbImageByte = UtilitiesM3G.getImage2DByteRepresentaionFromImage( format, this.sourceRgb );
		
		//set via super method to ensure Image2D is mutable
		super.set(0, 0, getWidth(), getHeight(), rgbImageByte);
	}
	
	/**
	 * Replaces the image data held by object to that of argument Image
	 * 
	 * @param image of same size and format as that currently held
	 */
	public void set(Image image)
	{
		if(null != image && image.getWidth() == getWidth() && image.getHeight() == getHeight())
		{
			image.getRGB(this.sourceRgb, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
			
			set(image.getWidth(), image.getHeight(), this.sourceRgb);
		}
		//#mdebug debug
		else
			throw new IllegalArgumentException("Input image is null or dimensions do not match");
		//#enddebug
	}
	
	/**
	 * Updates the Image2D using argument int[] rgb image representation 
	 * 
	 * @param width the width
	 * @param height the height
	 * @param imageRgb the image data
	 */
	public void set(int width, int height, int[] imageRgb)
	{
		if(null != imageRgb && width == getWidth() && height == getHeight())
		{
			this.sourceRgb = imageRgb;
			
			//convert to byte[]
			byte[] rgbImageByte = UtilitiesM3G.getImage2DByteRepresentaionFromImage(this.getFormat(), this.sourceRgb);
			
			//set via super method
			super.set(0, 0, getWidth(), getHeight(), rgbImageByte);
		}
		//#mdebug debug
		else
			throw new IllegalArgumentException("Input int[] is null or dimensions do not match");
		//#enddebug
	}
}
