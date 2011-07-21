//#condition polish.usePolishGui
/*
 * Copyright (c) 2010-2011 Robert Virkus / Enough Software
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

import java.io.IOException;
import java.io.InputStream;

/**
 * This class provides an abstraction for platform-independent Images.
 *
 * @author Ovidiu Iliescu
 */
public class Image
{
    protected
		//#if polish.build.classes.NativeImage:defined
    		//#= ${polish.build.classes.NativeImage}
    	//#else
    		javax.microedition.lcdui.Image
    	//#endif
    	image = null ;

    /**
     * Creates an empty image
     */
    public Image()
    {
       // Do nothing
    }

    /**
     * Creates a new image
     * @param image the parent image
     */
    public Image(
    		//#if polish.build.classes.NativeImage:defined
    			//#= ${polish.build.classes.NativeImage}
	    	//#else
	    		javax.microedition.lcdui.Image
	    	//#endif
    		image)
    {
       this.image = image;
    }

    /**
     * Creates an immutable image which is decoded from the data stored in
     * the specified byte array at the specified offset and length. The data
     * must be in a self-identifying image file format supported by the
     * implementation, such as <a href="#PNG">PNG</A>.
     *
     * <p>The <code>imageoffset</code> and <code>imagelength</code>
     * parameters specify a range of
     * data within the <code>imageData</code> byte array. The
     * <code>imageOffset</code> parameter
     * specifies the
     * offset into the array of the first data byte to be used. It must
     * therefore lie within the range
     * <code>[0..(imageData.length-1)]</code>. The
     * <code>imageLength</code>
     * parameter specifies the number of data bytes to be used. It must be a
     * positive integer and it must not cause the range to extend beyond
     * the end
     * of the array. That is, it must be true that
     * <code>imageOffset + imageLength &lt; imageData.length</code>. </p>
     *
     * <p> This method is intended for use when loading an
     * image from a variety of sources, such as from
     * persistent storage or from the network.</p>
     *
     * @param imageData the array of image data in a supported image format
     * @param imageOffset the offset of the start of the data in the array
     * @param imageLength the length of the data in the array
     *
     * @return the created image
     * @throws ArrayIndexOutOfBoundsException if <code>imageOffset</code>
     * and <code>imageLength</code>
     * specify an invalid range
     * @throws NullPointerException if <code>imageData</code> is
     * <code>null</code>
     * @throws IllegalArgumentException if <code>imageData</code> is incorrectly
     * formatted or otherwise cannot be decoded
     */
    public static Image createImage(byte[] imageData, int imageOffset, int imageLength)
    {
		//#if polish.build.classes.NativeImage:defined
    		//#= ${polish.build.classes.NativeImage}
		//#else
			javax.microedition.lcdui.Image
		//#endif
         thisImage = 
 		//#if polish.build.classes.NativeImage:defined
     		//#= ${polish.build.classes.NativeImage}
 		//#else
 			javax.microedition.lcdui.Image
 		//#endif
 				.createImage(imageData,imageOffset,imageLength);
        Image result = new Image(thisImage);
        return result;
    }

    //#if polish.midp2
    /**
     * Creates an immutable image using pixel data from the specified
     * region of a source image, transformed as specified.
     *
     * <p>The source image may be mutable or immutable.  For immutable source
     * images, transparency information, if any, is copied to the new
     * image unchanged.</p>
     *
     * <p>On some devices, pre-transformed images may render more quickly
     * than images that are transformed on the fly using
     * <code>drawRegion</code>.
     * However, creating such images does consume additional heap space,
     * so this technique should be applied only to images whose rendering
     * speed is critical.</p>
     *
     * <p>The transform function used must be one of the following, as defined
     * in the {@link javax.microedition.lcdui.game.Sprite Sprite} class:<br>
     *
     * <code>Sprite.TRANS_NONE</code> - causes the specified image
     * region to be copied unchanged<br>
     * <code>Sprite.TRANS_ROT90</code> - causes the specified image
     * region to be rotated clockwise by 90 degrees.<br>
     * <code>Sprite.TRANS_ROT180</code> - causes the specified image
     * region to be rotated clockwise by 180 degrees.<br>
     * <code>Sprite.TRANS_ROT270</code> - causes the specified image
     * region to be rotated clockwise by 270 degrees.<br>
     * <code>Sprite.TRANS_MIRROR</code> - causes the specified image
     * region to be reflected about its vertical center.<br>
     * <code>Sprite.TRANS_MIRROR_ROT90</code> - causes the specified image
     * region to be reflected about its vertical center and then rotated
     * clockwise by 90 degrees.<br>
     * <code>Sprite.TRANS_MIRROR_ROT180</code> - causes the specified image
     * region to be reflected about its vertical center and then rotated
     * clockwise by 180 degrees.<br>
     * <code>Sprite.TRANS_MIRROR_ROT270</code> - causes the specified image
     * region to be reflected about its vertical center and then rotated
     * clockwise by 270 degrees.<br></p>
     *
     * <p>
     * The size of the returned image will be the size of the specified region
     * with the transform applied.  For example, if the region is
     * <code>100&nbsp;x&nbsp;50</code> pixels and the transform is
     * <code>TRANS_ROT90</code>, the
     * returned image will be <code>50&nbsp;x&nbsp;100</code> pixels.</p>
     *
     * <p><strong>Note:</strong> If all of the following conditions
     * are met, this method may
     * simply return the source <code>Image</code> without creating a
     * new one:</p>
     * <ul>
     * <li>the source image is immutable;</li>
     * <li>the region represents the entire source image; and</li>
     * <li>the transform is <code>TRANS_NONE</code>.</li>
     * </ul>
     *
     * @param img the source image to be copied from
     * @param x the horizontal location of the region to be copied
     * @param y the vertical location of the region to be copied
     * @param width the width of the region to be copied
     * @param height the height of the region to be copied
     * @param transform the transform to be applied to the region
     * @return the new, immutable image
     *
     * @throws NullPointerException if <code>image</code> is <code>null</code>
     * @throws IllegalArgumentException if the region to be copied exceeds
     * the bounds of the source image
     * @throws IllegalArgumentException if either <code>width</code> or
     * <code>height</code> is zero or less
     * @throws IllegalArgumentException if the <code>transform</code>
     * is not valid
     *
     */
    public static Image createImage(Image img, int x, int y, int width, int height, int transform)
    {
 		//#if polish.build.classes.NativeImage:defined
    		//#= ${polish.build.classes.NativeImage}
		//#else
			javax.microedition.lcdui.Image
		//#endif
	     thisImage = 
	  		//#if polish.build.classes.NativeImage:defined
	     		//#= ${polish.build.classes.NativeImage}
			//#else
				javax.microedition.lcdui.Image
			//#endif
					.createImage(img.image, x, y, width, height, transform);
	    Image result = new Image(thisImage);
        return result;
    }
    //#endif

    /**
     * Creates an immutable image from a source image.
     * If the source image is mutable, an immutable copy is created and
     * returned.  If the source image is immutable, the implementation may
     * simply return it without creating a new image.  If an immutable source
     * image contains transparency information, this information is copied to
     * the new image unchanged.
     *
     * <p> This method is useful for placing the contents of mutable images
     * into <code>Choice</code> objects.  The application can create
     * an off-screen image
     * using the
     * {@link #createImage(int, int) createImage(w, h)}
     * method, draw into it using a <code>Graphics</code> object
     * obtained with the
     * {@link #getGraphics() getGraphics()}
     * method, and then create an immutable copy of it with this method.
     * The immutable copy may then be placed into <code>Choice</code>
     * objects. </p>
     *
     * @param source the source image to be copied
     * @return the new, immutable image
     *
     * @throws NullPointerException if <code>source</code> is <code>null</code>
     */
    public static Image createImage(Image source)
    {
  		//#if polish.build.classes.NativeImage:defined
    		//#= ${polish.build.classes.NativeImage}
		//#else
			javax.microedition.lcdui.Image
		//#endif
	     thisImage = 
  		//#if polish.build.classes.NativeImage:defined
     		//#= ${polish.build.classes.NativeImage}
		//#else
    	 	javax.microedition.lcdui.Image
		//#endif
    	 			.createImage(source.image) ;
	    Image result = new Image(thisImage);
        return result;
    }

    //#if polish.midp2
    /**
     * Creates an immutable image from decoded image data obtained froH an
     * <code>InputStream</code>.  This method blocks until all image data has 
     * been read and decoded.  After this method completes (whether by 
     * returning or by throwing an exception) the stream is left open and its 
     * current position is undefined.
     *
     * @param stream the name of the resource containing the image data
     * in one of the supported image formats
     *
     * @return the created image
     * @throws NullPointerException if <code>stream</code> is <code>null</code>
     * @throws java.io.IOException if an I/O error occurs, if the image data
     * cannot be loaded, or if the image data cannot be decoded
     *
     */
    public static Image createImage(InputStream stream) throws IOException
    {
  		//#if polish.build.classes.NativeImage:defined
    		//#= ${polish.build.classes.NativeImage}
		//#else
			javax.microedition.lcdui.Image
		//#endif
	     thisImage = 
   		//#if polish.build.classes.NativeImage:defined
     		//#= ${polish.build.classes.NativeImage}
		//#else
		 	javax.microedition.lcdui.Image
		//#endif
		 		.createImage(stream) ;
	    Image result = new Image(thisImage);
        return result;
    }
    //#endif

    /**
     * Creates a new, mutable image for off-screen drawing. Every pixel
     * within the newly created image is white.  The width and height of the
     * image must both be greater than zero.
     *
     * @param width the width of the new image, in pixels
     * @param height the height of the new image, in pixels
     * @return the created image
     *
     * @throws IllegalArgumentException if either <code>width</code> or
     * <code>height</code> is zero or less
     */
    public static Image createImage(int width, int height)
    {
   		//#if polish.build.classes.NativeImage:defined
    		//#= ${polish.build.classes.NativeImage}
		//#else
			javax.microedition.lcdui.Image
		//#endif
	     thisImage = 
		//#if polish.build.classes.NativeImage:defined
     		//#= ${polish.build.classes.NativeImage}
		//#else
		 	javax.microedition.lcdui.Image
		//#endif
		 		.createImage(width,height) ;
	    Image result = new Image(thisImage);
        return result;
    }

    /**
     * Creates an immutable image from decoded image data obtained from the
     * named resource.  The name parameter is a resource name as defined by
     * {@link Class#getResourceAsStream(String)
     * Class.getResourceAsStream(name)}.  The rules for resolving resource
     * names are defined in the
     * <a href="../../../java/lang/package-summary.html">
     * Application Resource Files</a> section of the
     * <code>java.lang</code> package documentation.
     *
     * @param url the path of the resource containing the image data in one of
     * the supported image formats
     * @return the created image
     * @throws NullPointerException if <code>name</code> is <code>null</code>
     * @throws java.io.IOException if the resource does not exist,
     * the data cannot
     * be loaded, or the image data cannot be decoded
     */
    public static Image createImage(String url) throws IOException
    {
		//#if polish.build.classes.NativeImage:defined
    		//#= ${polish.build.classes.NativeImage}
		//#else
			javax.microedition.lcdui.Image
		//#endif
	     thisImage = 
 		//#if polish.build.classes.NativeImage:defined
    		//#= ${polish.build.classes.NativeImage}
		//#else
		 	javax.microedition.lcdui.Image
		//#endif
		 		.createImage(url) ;
	    Image result = new Image(thisImage);
        return result;
    }

    //#if polish.midp2
    /**
     * Creates an immutable image from a sequence of ARGB values, specified
     * as <code>0xAARRGGBB</code>.
     * The ARGB data within the <code>rgb</code> array is arranged
     * horizontally from left to right within each row,
     * row by row from top to bottom.
     * If <code>processAlpha</code> is <code>true</code>,
     * the high-order byte specifies opacity; that is,
     * <code>0x00RRGGBB</code> specifies
     * a fully transparent pixel and <code>0xFFRRGGBB</code> specifies
     * a fully opaque
     * pixel.  Intermediate alpha values specify semitransparency.  If the
     * implementation does not support alpha blending for image rendering
     * operations, it must replace any semitransparent pixels with fully
     * transparent pixels.  (See <a href="#alpha">Alpha Processing</a>
     * for further discussion.)  If <code>processAlpha</code> is
     * <code>false</code>, the alpha values
     * are ignored and all pixels must be treated as fully opaque.
     *
     * <p>Consider <code>P(a,b)</code> to be the value of the pixel
     * located at column <code>a</code> and row <code>b</code> of the
     * Image, where rows and columns are numbered downward from the
     * top starting at zero, and columns are numbered rightward from
     * the left starting at zero. This operation can then be defined
     * as:</p>
     *
     * <TABLE BORDER="2">
     * <TR>
     * <TD ROWSPAN="1" COLSPAN="1">
     *    <pre><code>
     *    P(a, b) = rgb[a + b * width];    </code></pre>
     * </TD>
     * </TR>
     * </TABLE>
     * <p>for</p>
     *
     * <TABLE BORDER="2">
     * <TR>
     * <TD ROWSPAN="1" COLSPAN="1">
     *    <pre><code>
     *     0 &lt;= a &lt; width
     *     0 &lt;= b &lt; height    </code></pre>
     * </TD>
     * </TR>
     * </TABLE>
     * <p> </p>
     *
     * @param rgb an array of ARGB values that composes the image
     * @param width the width of the image
     * @param height the height of the image
     * @param processAlpha <code>true</code> if <code>rgb</code>
     * has an alpha channel,
     * <code>false</code> if all pixels are fully opaque
     * @return the created image
     * @throws NullPointerException if <code>rgb</code> is <code>null</code>.
     * @throws IllegalArgumentException if either <code>width</code> or
     * <code>height</code> is zero or less
     * @throws ArrayIndexOutOfBoundsException if the length of
     * <code>rgb</code> is
     * less than<code> width&nbsp;*&nbsp;height</code>.
     *
     */
    public static Image createRGBImage(int[] rgb, int width, int height, boolean processAlpha)
    {
 		//#if polish.build.classes.NativeImage:defined
    		//#= ${polish.build.classes.NativeImage}
		//#else
			javax.microedition.lcdui.Image
		//#endif
	     thisImage = 
  		//#if polish.build.classes.NativeImage:defined
    		//#= ${polish.build.classes.NativeImage}
		//#else
		 	javax.microedition.lcdui.Image
		//#endif
		 		.createRGBImage(rgb,width,height,processAlpha) ;
	    Image result = new Image(thisImage);
        return result;
    }
    //#endif

    /**
     * Creates a new <code>Graphics</code> object that renders to this
     * image. This image
     * must be
     * mutable; it is illegal to call this method on an immutable image.
     * The mutability of an image may be tested
     * with the <code>isMutable()</code> method.
     *
     * <P>The newly created <code>Graphics</code> object has the
     * following properties:
     * </P>
     * <UL>
     * <LI>the destination is this <code>Image</code> object;</LI>
     * <LI>the clip region encompasses the entire <code>Image</code>;</LI>
     * <LI>the current color is black;</LI>
     * <LI>the font is the same as the font returned by
     * {@link Font#getDefaultFont() Font.getDefaultFont()};</LI>
     * <LI>the stroke style is {@link Graphics#SOLID SOLID}; and
     * </LI>
     * <LI>the origin of the coordinate system is located at the upper-left
     * corner of the Image.</LI>
     * </UL>
     *
     * <P>The lifetime of <code>Graphics</code> objects created using
     * this method is
     * indefinite.  They may be used at any time, by any thread.</P>
     *
     * @return a <code>Graphics</code> object with this image as its destination
     * @throws IllegalStateException if the image is immutable
     */
    //#if polish.midp
    public Graphics getGraphics ()
    {
        if ( this.image != null )
        {
            return new Graphics(this.image.getGraphics());
        }
        else
        {
            return null;
        }
    }
    //#endif

    /**
     * Gets the width of the image in pixels. The value returned
     * must reflect the actual width of the image when rendered.
     * @return width of the image
     */
    public int getWidth()
    {
        if ( image == null )
        {
            return -1;
        }
        else
        {
            return image.getWidth();
        }
    }

    /**
     * Gets the height of the image in pixels. The value returned
     * must reflect the actual height of the image when rendered.
     * @return height of the image
     */
    public int getHeight()
    {
        if ( image == null )
        {
            return -1;
        }
        else
        {
            return image.getHeight();
        }
    }

    //#if polish.midp2
    /**
     * Obtains ARGB pixel data from the specified region of this image and
     * stores it in the provided array of integers.  Each pixel value is
     * stored in <code>0xAARRGGBB</code> format, where the high-order
     * byte contains the
     * alpha channel and the remaining bytes contain color components for
     * red, green and blue, respectively.  The alpha channel specifies the
     * opacity of the pixel, where a value of <code>0x00</code>
     * represents a pixel that
     * is fully transparent and a value of <code>0xFF</code>
     * represents a fully opaque
     * pixel.
     *
     * <p> The returned values are not guaranteed to be identical to values
     * from the original source, such as from
     * <code>createRGBImage</code> or from a PNG
     * image.  Color values may be resampled to reflect the display
     * capabilities of the device (for example, red, green or blue pixels may
     * all be represented by the same gray value on a grayscale device).  On
     * devices that do not support alpha blending, the alpha value will be
     * <code>0xFF</code> for opaque pixels and <code>0x00</code> for
     * all other pixels (see <a
     * href="#alpha">Alpha Processing</a> for further discussion.)  On devices
     * that support alpha blending, alpha channel values may be resampled to
     * reflect the number of levels of semitransparency supported.</p>
     *
     * <p>The <code>scanlength</code> specifies the relative offset within the
     * array between the corresponding pixels of consecutive rows.  In order
     * to prevent rows of stored pixels from overlapping, the absolute value
     * of <code>scanlength</code> must be greater than or equal to
     * <code>width</code>.  Negative values of <code>scanlength</code> are
     * allowed.  In all cases, this must result in every reference being
     * within the bounds of the <code>rgbData</code> array.</p>
     *
     * <p>Consider <code>P(a,b)</code> to be the value of the pixel
     * located at column <code>a</code> and row <code>b</code> of the
     * Image, where rows and columns are numbered downward from the
     * top starting at zero, and columns are numbered rightward from
     * the left starting at zero. This operation can then be defined
     * as:</p>
     *
     * <TABLE BORDER="2">
     * <TR>
     * <TD ROWSPAN="1" COLSPAN="1">
     *    <pre><code>
     *    rgbData[offset + (a - x) + (b - y) * scanlength] = P(a, b);
     *         </code></pre>
     * </TD>
     * </TR>
     * </TABLE>
     * <p>for</p>
     *
     * <TABLE BORDER="2">
     * <TR>
     * <TD ROWSPAN="1" COLSPAN="1">
     *    <pre><code>
     *     x &lt;= a &lt; x + width
     *     y &lt;= b &lt; y + height    </code></pre>
     * </TD>
     * </TR>
     * </TABLE>
     *
     * <p>The source rectangle is required to not exceed the bounds of
     * the image.  This means: </p>
     * <TABLE BORDER="2">
     * <TR>
     * <TD ROWSPAN="1" COLSPAN="1">
     *    <pre><code>
     *   x &gt;= 0
     *   y &gt;= 0
     *   x + width &lt;= image width
     *   y + height &lt;= image height    </code></pre>
     * </TD>
     * </TR>
     * </TABLE>
     * <p>
     * If any of these conditions is not met an
     * <code>IllegalArgumentException</code> is thrown.  Otherwise, in
     * cases where <code>width &lt;= 0</code> or <code>height &lt;= 0</code>,
     * no exception is thrown, and no pixel data is copied to
     * <code>rgbData</code>.</p>
     *
     * @param rgbData an array of integers in which the ARGB pixel data is
     * stored
     * @param offset the index into the array where the first ARGB value
     * is stored
     * @param scanlength the relative offset in the array between
     * corresponding pixels in consecutive rows of the region
     * @param x the x-coordinate of the upper left corner of the region
     * @param y the y-coordinate of the upper left corner of the region
     * @param width the width of the region
     * @param height the height of the region
     *
     * @throws ArrayIndexOutOfBoundsException if the requested operation would
     * attempt to access an element in the <code>rgbData</code> array
     * whose index is either
     * negative or beyond its length (the contents of the array are unchanged)
     *
     * @throws IllegalArgumentException if the area being retrieved
     * exceeds the bounds of the source image
     *
     * @throws IllegalArgumentException if the absolute value of
     * <code>scanlength</code> is less than <code>width</code>
     *
     * @throws NullPointerException if <code>rgbData</code> is <code>null</code>
     *
     * @since MIDP 2.0
     */
    public void getRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height)
    {
        if ( image != null )
        {
            image.getRGB(rgbData, offset, scanlength, x, y, width, height);
        }
    }
    //#endif

    /**
     * Check if this image is mutable. Mutable images can be modified by
     * rendering to them through a <code>Graphics</code> object
     * obtained from the
     * <code>getGraphics()</code> method of this object.
     * @return <code>true</code> if the image is mutable,
     * <code>false</code> otherwise
     */
    public boolean isMutable()
    {
        if (this.image != null)
        {
            return this.image.isMutable();
        }
        else
        {
            return false;
        }

    }

    // Extra methods
    
    //#if polish.midp2
    /**
     * Returns an ARGB array containing a copy of the image's RGB data. Convenience method.
     * @return ARGB array
     */
    public int[] getRgbData ()
    {
        int data[] = new int [ image.getWidth() * image.getHeight() ];
        image.getRGB(data, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight() );
        return data;
    }
    //#endif

    public NativeImage getNativeImage() {
    	//todo implement getNativeImage
    	return null;//this.image;
    }

    
}
