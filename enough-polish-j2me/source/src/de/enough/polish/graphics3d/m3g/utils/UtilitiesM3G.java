//#condition polish.midp2 && polish.javapackage.jsr184

package de.enough.polish.graphics3d.m3g.utils;

import java.util.Hashtable;

import javax.microedition.lcdui.Image;
import javax.microedition.m3g.Appearance;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.Image2D;

/**
 * M3G Utility class.
 * <p>
 * Defines properties specific to runtime kvm.
 * <p>
 * Implements helper functions associated with M3G scene maintenance.
 * 
 * @author Anders Bo Pedersen, anders@wicore.dk
 */
public class UtilitiesM3G
{
	/*
	 * Globals
	 */
	public static final boolean M3G_SUPPORTS_ANTIALIASING;
	public static final boolean M3G_SUPPORTS_TRUE_COLOR;
	public static final boolean M3G_SUPPORTS_DITHERING;
	public static final boolean M3G_SUPPORTS_MIPMAPPING;
	public static final boolean M3G_SUPPORTS_PERSPECTIVE_CORRECTION;
	public static final boolean M3G_SUPPORTS_LOCAL_CAMERA_LIGHTING;
	
	public static final int M3G_MAX_LIGHTS;
	public static final int M3G_MAX_VIEWPORT_WIDTH;
	public static final int M3G_MAX_VIEWPORT_HEIGHT;
	public static final int M3G_MAX_VIEWPORT_DIMENSION;
	public static final int M3G_MAX_TEXTURE_DIMENSION;
	public static final int M3G_MAX_SPRITE_CROP_DIMENSION;
	public static final int M3G_MAX_TRANSFORMS_PER_VERTEX;
	
	public static final int M3G_NUM_TEXTURE_UNITS;
	
	public static final String M3G_VERSION_NUM;
	public static final boolean M3G_VERSION_IS_10; 
	public static final boolean M3G_VERSION_IS_11;
	public static final boolean M3G_VERSION_IS_20; 
	
	static
	{
		/* Defining M3G version */ 
		
		M3G_VERSION_NUM = System.getProperty("microedition.m3g.version");
		
		if( null != M3G_VERSION_NUM )
		{
			if(	M3G_VERSION_NUM.equals( "1.0" ) )
				M3G_VERSION_IS_10 = true;
			else
				M3G_VERSION_IS_10 = false;

			if(	M3G_VERSION_NUM.equals( "1.1" ) )
				M3G_VERSION_IS_11 = true;
			else
				M3G_VERSION_IS_11 = false;
			
			if(	M3G_VERSION_NUM.equals( "2.0" ) )
				M3G_VERSION_IS_20 = true;
			else
				M3G_VERSION_IS_20 = false;
		}
		else
			M3G_VERSION_IS_10 = M3G_VERSION_IS_11 = M3G_VERSION_IS_20 = false;
		
		/* Defining M3G properties */ 
		
		Hashtable m3gProperties = Graphics3D.getProperties();
		
		Object property;
		
		M3G_SUPPORTS_ANTIALIASING = 
			null != (property = m3gProperties.get("supportAntialiasing")) ? ((Boolean)property).booleanValue() : false;
		M3G_SUPPORTS_TRUE_COLOR = 
			null != (property = m3gProperties.get("supportTrueColor")) ? ((Boolean)property).booleanValue() : false;
		M3G_SUPPORTS_DITHERING = 
			null != (property = m3gProperties.get("supportDithering")) ? ((Boolean)property).booleanValue() : false;
		M3G_SUPPORTS_MIPMAPPING = 
			null != (property = m3gProperties.get("supportMipmapping")) ? ((Boolean)property).booleanValue() : false;
		M3G_SUPPORTS_PERSPECTIVE_CORRECTION = 
			null != (property = m3gProperties.get("supportPerspectiveCorrection")) ? ((Boolean)property).booleanValue() : false;
		M3G_SUPPORTS_LOCAL_CAMERA_LIGHTING = 
			null != (property = m3gProperties.get("supportLocalCameraLighting")) ? ((Boolean)property).booleanValue() : false;
		
		M3G_MAX_LIGHTS = 
			null != (property = m3gProperties.get("maxLights")) ? ((Integer)property).intValue() : 0;
		M3G_MAX_VIEWPORT_WIDTH = 
			null != (property = m3gProperties.get("maxViewportWidth")) ? ((Integer)property).intValue() : 0;
		M3G_MAX_VIEWPORT_HEIGHT = 
			null != (property = m3gProperties.get("maxViewportHeight")) ? ((Integer)property).intValue() : 0;
		M3G_MAX_VIEWPORT_DIMENSION = 
			null != (property = m3gProperties.get("maxViewportDimension")) ? ((Integer)property).intValue() : 0;
		//Note: Texture size of 512 & 1024 doesn't render correctly on SE emulator 
		M3G_MAX_TEXTURE_DIMENSION = 
			null != (property = m3gProperties.get("maxTextureDimension")) ? ((Integer)property).intValue() : 0;
//			M3G_MAX_TEXTURE_DIMENSION = 256;
		M3G_MAX_SPRITE_CROP_DIMENSION = 
			null != (property = m3gProperties.get("maxSpriteCropDimension")) ? ((Integer)property).intValue() : 0;
		M3G_MAX_TRANSFORMS_PER_VERTEX = 
			null != (property = m3gProperties.get("maxTransformsPerVertex")) ? ((Integer)property).intValue() : 0;
		
		M3G_NUM_TEXTURE_UNITS = 
			null != (property = m3gProperties.get("numTextureUnits")) ? ((Integer)property).intValue() : 0;
	}
	
	/**
	 * Creates a Image2D object based on parameter Image.
	 * <p>
	 * The Image2D object is optionally set to be mutable for future manipulation
	 * using Image2D.set().
	 *  
	 * @param img image to use in texture
	 * @param isRGBA is input image RGBA or RGB 
	 * @param makeMutable if Image2D should be mutable
	 * @return Texture2D object with default attributes
	 */
	public static Image2D createImage2D(Image img, boolean isRGBA, boolean makeMutable)
	{
		//#mdebug debug
		if(null == img)
			throw new IllegalArgumentException("input image is null");
		//#enddebug
		
		Image2D img2d = null;
		
		if(null != img)
		{
			if(!makeMutable)
			{
				img2d = new Image2D( isRGBA ? Image2D.RGBA: Image2D.RGB, img);
			}
			else
			{
				int format = isRGBA ? Image2D.RGBA: Image2D.RGB;
				
				img2d = new Image2D( format, img.getWidth(), img.getHeight());
				
				byte[] img2dByte = getImage2DByteRepresentaionFromImage(format, img); 
				
				img2d.set( 0, 0, img.getWidth(), img.getHeight(), img2dByte);
			}
		}
		
		return img2d;
	}
	
	/**
	 * Creates a byte[] representation of a Image object according to defined format that complies 
	 * to the specifications of class Image2D. Output byte[] can then be used in Image2d.set().
	 * <p>
	 * Supported formats are Image2D.RGBA, Image2D.RGB, Image2D.LUMINANCE, Image2D.LUMINANCE_ALPHA,
	 * Image2D.ALPHA
	 * 
	 * @param format the format of the input image
	 * @param img
	 * @return byte[] image representation complying to Image2D specs
	 */
	public static byte[] getImage2DByteRepresentaionFromImage(int format, Image img )
	{
		//#mdebug debug
		if(null == img)
			throw new IllegalArgumentException("input image is null");
		//#enddebug
		
		int[] rgbImageInt = new int[img.getWidth()*img.getHeight()]; 
		img.getRGB(rgbImageInt, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());
		
		return getImage2DByteRepresentaionFromImage(format, rgbImageInt);
	}
	
	/**
	 * Creates a byte[] representation of a image rgb int[] object according to defined format
	 * that complies to the specifications of class Image2D. Output byte[] can then be used in
	 * Image2d.set().
	 * <p>
	 * Supported formats are Image2D.RGBA, Image2D.RGB, Image2D.LUMINANCE, Image2D.LUMINANCE_ALPHA,
	 * Image2D.ALPHA
	 * 
	 * @param format the format of the input image
	 * @param rgbImageInt rgb int[] image representation
	 * @return byte[] image representation complying to Image2D specs
	 */
	public static byte[] getImage2DByteRepresentaionFromImage(int format, int[] rgbImageInt )
	{
		//#mdebug debug
		if(null == rgbImageInt)
			throw new IllegalArgumentException("input int[] is null");
		//#enddebug
			
		int rgbColorInt;
		int rgbByteIndex;
		byte[] rgbImageByte = null;
		
		if(Image2D.ALPHA == format || Image2D.LUMINANCE == format )
		{
			rgbImageByte = new byte[rgbImageInt.length];
			
			for(int j = rgbImageInt.length; --j>=0;)
				rgbImageByte[j] = (byte)rgbImageInt[j]; //A or L
		}
		else
		if(Image2D.LUMINANCE_ALPHA == format)
		{
			rgbImageByte = new byte[rgbImageInt.length << 1];
			
			for(int j = rgbImageInt.length; --j>=0;)
			{
				rgbColorInt = rgbImageInt[j];
				rgbByteIndex = j << 1;
				rgbImageByte[rgbByteIndex] = (byte)(rgbColorInt>>8); //L
				rgbImageByte[rgbByteIndex+1] = (byte)rgbColorInt; //A
			}
		}
		else
		if(Image2D.RGB == format)
		{
			rgbImageByte = new byte[rgbImageInt.length * 3];
			
			for(int j = rgbImageInt.length; --j>=0;)
			{
				rgbColorInt = rgbImageInt[j];
				rgbByteIndex = j * 3;
				rgbImageByte[rgbByteIndex] = (byte)(rgbColorInt>>16); //R
				rgbImageByte[rgbByteIndex+1] = (byte)(rgbColorInt>>8); //G
				rgbImageByte[rgbByteIndex+2] = (byte)rgbColorInt; //B
			}
		}
		else
		if(Image2D.RGBA == format)
		{
			rgbImageByte = new byte[rgbImageInt.length << 2];
			
			for(int j = rgbImageInt.length; --j>=0;)
			{
				rgbColorInt = rgbImageInt[j];
				rgbByteIndex = j << 2; //*4
				rgbImageByte[rgbByteIndex] = (byte)(rgbColorInt>>16); //R
				rgbImageByte[rgbByteIndex+1] = (byte)(rgbColorInt>>8); //G
				rgbImageByte[rgbByteIndex+2] = (byte)rgbColorInt; //B
				rgbImageByte[rgbByteIndex+3] = (byte)(rgbColorInt>>24); //A
				
				/*
				//#mdebug debug
				System.out.println("RGB int index: " + j);
				System.out.println("RGB byte index: " + rgbByteIndex);
				System.out.println("int Color: " + rgbColorInt);
				System.out.println("int Color hex: " + Integer.toHexString(rgbColorInt) );
				System.out.println("Byte Color R: " + rgbImageByte[rgbByteIndex]);
				System.out.println("Byte Color G: " + rgbImageByte[rgbByteIndex+1]);
				System.out.println("Byte Color B: " + rgbImageByte[rgbByteIndex+2]);
				System.out.println("Byte Color A: " + rgbImageByte[rgbByteIndex+3]);
				System.out.println("Byte Color R hex: " + Integer.toHexString(rgbImageByte[rgbByteIndex]) );
				System.out.println("Byte Color G hex: " + Integer.toHexString(rgbImageByte[rgbByteIndex+1]) );
				System.out.println("Byte Color B hex: " + Integer.toHexString(rgbImageByte[rgbByteIndex+2]) );
				System.out.println("Byte Color A hex: " + Integer.toHexString(rgbImageByte[rgbByteIndex+3]) );
				//#enddebug
				 */
			}
		}
		
		return rgbImageByte;
	}
	
	/**
	 * Optionally duplicates an Appearance object without duplicating attached
	 * objects.
	 *  
	 * @param source Appearance object
	 * @param cloneDeep true if source should be duplicate normally, false if 
	 * attached objects should be shared between source and clone
	 * @return clone the cloned Appearance object
	 */
	public static final Appearance cloneAppearance(Appearance source, boolean cloneDeep)
	{
		Appearance clone = new Appearance();
		
		if(!cloneDeep)
		{
			clone.setLayer(source.getLayer());
			clone.setPolygonMode(source.getPolygonMode());
			clone.setCompositingMode(source.getCompositingMode());
			for(int i = 0; i < UtilitiesM3G.M3G_NUM_TEXTURE_UNITS; ++i)
				clone.setTexture(i, source.getTexture(i));
			clone.setMaterial(source.getMaterial());
			clone.setFog(source.getFog());
		}
		else
		{
			clone = (Appearance) source.duplicate();
		}
		
		return clone;
	}
	
//#mdebug debug	
	
	public static String propertiesM3GToString()
	{
		String newline = "\n";
		
		String properties = 
			
			"M3G Version: "+M3G_VERSION_NUM+newline+
			
			"supportAntialiasing: "+M3G_SUPPORTS_ANTIALIASING+newline+
			"supportTrueColor: "+M3G_SUPPORTS_TRUE_COLOR+newline+
			"supportDithering: "+M3G_SUPPORTS_DITHERING+newline+
			"supportMipmapping: "+M3G_SUPPORTS_MIPMAPPING+newline+
			"supportPerspectiveCorrection: "+M3G_SUPPORTS_PERSPECTIVE_CORRECTION+newline+
			"supportLocalCameraLighting: "+M3G_SUPPORTS_LOCAL_CAMERA_LIGHTING+newline+
			
			"maxLights: "+M3G_MAX_LIGHTS+newline+
			"maxViewportWidth: "+M3G_MAX_VIEWPORT_WIDTH+newline+
			"maxViewportHeight: "+M3G_MAX_VIEWPORT_HEIGHT+newline+
			"maxViewportDimension: "+M3G_MAX_VIEWPORT_DIMENSION+newline+
			"maxTextureDimension: "+M3G_MAX_TEXTURE_DIMENSION+newline+
			"maxSpriteCropDimension: "+M3G_MAX_SPRITE_CROP_DIMENSION+newline+
			"maxTransformsPerVertex: "+M3G_MAX_TRANSFORMS_PER_VERTEX+newline+
			
			"numTextureUnits: "+M3G_NUM_TEXTURE_UNITS;
		
		return properties;
	}
	
//#enddebug
}
