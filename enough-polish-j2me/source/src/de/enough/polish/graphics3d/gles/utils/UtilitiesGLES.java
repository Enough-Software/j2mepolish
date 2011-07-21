//#condition polish.javapackage.jsr239

package de.enough.polish.graphics3d.gles.utils;

import java.nio.ByteBuffer;

import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.lcdui.Image;

/**
 * OpenGL ES Utility class.
 * <p>
 * Defines properties specific to runtime kvm.
 * <p>
 * Implements helper functions associated with GLES scene maintenance.
 * 
 * @author Anders Bo Pedersen, anders@wicore.dk
 */
public class UtilitiesGLES 
{
	public static ByteBuffer textureToByteBuffer(Image texImg, boolean isARGB)
	{
		return textureToByteBuffer(texImg, null, isARGB);
	}
	
	public static ByteBuffer textureToByteBuffer(Image texImg, ByteBuffer texBuf, boolean isARGB)
	{
		if(null != texImg)
		{
			int texWidth = texImg.getWidth(), texHeight = texImg.getHeight();
			int rgbType = (isARGB ? 4 : 3);
			int newLimit = texWidth * texHeight * rgbType;
			
			//#debug debug
			long updateStart = System.currentTimeMillis();
			
			if(null == texBuf || newLimit > texBuf.limit())
			{
				//init
				texBuf = ByteBuffer.allocateDirect(newLimit);
			}
			else
			{
				//reset
				texBuf.clear();
				if(texBuf.limit() != newLimit)
					texBuf.limit(newLimit);
				texBuf.rewind();
			}
			
			//#debug debug
			System.out.println("textureToByteBuffer() allocation time: "+(System.currentTimeMillis()-updateStart));
			
			//#debug debug
			updateStart = System.currentTimeMillis();
			
			/* Read image in reverse row order. Orders the texture
			 * correctly for OpenGL's coordinate system, where (0,0)
			 * is the lower left.
			 */
			int pixels[] = new int[texWidth * texHeight];
			int pix;
			texImg.getRGB(pixels, 0, texWidth, 0, 0, texWidth, texHeight);
			if(isARGB)
			{
				for (int i = 0, length = pixels.length; i < length; i++)
				{
					texBuf.putInt(pixels[i]);
					//pix = pixels[i];
					//texBuf.put((byte)((pix >> 24) & 0xFF)).put((byte)((pix >> 16) & 0xFF)).put((byte)((pix >> 8) & 0xFF)).put((byte)(pix & 0xFF));
				}
			}
			else
			{
				for (int i = 0, length = pixels.length; i < length; i++)
				{
					pix = pixels[i];
					texBuf.put((byte)((pix >> 16) & 0xFF)).put((byte)((pix >> 8) & 0xFF)).put((byte)(pix & 0xFF));
				}
			}
			texBuf.rewind();	
			
			//#mdebug
			/*
			byte[] bytes = new byte[texBuf.capacity()];
			texBuf.get(bytes);

			for (int i = 0; i < bytes.length; i++) 
			{
				System.out.println("byte["+i+"]: "+bytes[i]);
			}
			 */
			//#enddebug
			
			//#debug debug
			System.out.println("textureToByteBuffer() trversal time: "+(System.currentTimeMillis()-updateStart));
		}
		
		return texBuf;
	}
	
//#mdebug debug	
	public static String propertiesGLESToString(GL gl)
	{
		String newline = "\n";
		
		GL10 gl10 = (GL10)gl;
		
		int[] params = new int[6];
		gl10.glGetIntegerv(GL10.GL_MAX_LIGHTS, params, 0);
		gl10.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, params, 1);
		gl10.glGetIntegerv(GL10.GL_MAX_MODELVIEW_STACK_DEPTH, params, 2);
		gl10.glGetIntegerv(GL10.GL_MAX_PROJECTION_STACK_DEPTH, params, 3);
		gl10.glGetIntegerv(GL10.GL_MAX_ELEMENTS_INDICES, params, 4);
		gl10.glGetIntegerv(GL10.GL_MAX_ELEMENTS_VERTICES, params, 5);
		
		String properties = 
			
			"Vendor: "+gl10.glGetString(GL10.GL_VENDOR)+newline+
			"Renderer: "+gl10.glGetString(GL10.GL_RENDERER)+newline+
			"Version: "+gl10.glGetString(GL10.GL_VERSION)+newline+
			"Extensions: "+gl10.glGetString(GL10.GL_EXTENSIONS)+newline+
			
			"GL_MAX_LIGHTS: "+params[0]+newline+
			"GL_MAX_TEXTURE_SIZE: "+params[1]+newline+
			"GL_MAX_MODELVIEW_STACK_DEPTH: "+params[2]+newline+
			"GL_MAX_PROJECTION_STACK_DEPTH: "+params[3]+newline+
			"GL_MAX_ELEMENTS_INDICES: "+params[4]+newline+
			"GL_MAX_ELEMENTS_VERTICES: "+params[5];
		
		return properties;
	}
//#enddebug	
}
