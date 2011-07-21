//#condition polish.midp2 && polish.javapackage.jsr239

package de.enough.polish.graphics3d.gles.nodes;

import java.nio.ByteBuffer;

import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.lcdui.Image;

import de.enough.polish.graphics3d.gles.utils.UtilitiesGLES;
import de.enough.polish.graphics3d.utils.Utilities3d;

/**
 * Quad shape node
 * <p>
 * Optionally supporting 2d texture
 * 
 * @author Anders Bo Pedersen, anders@wicore.dk
 */
public class Quad extends Actorx
{
	//Quad vertices
    public static final byte[] QUAD_VERTS = 
    {
        1, 1, 0,  -1, 1, 0,   1,-1, 0,  -1,-1, 0
	};

    //Quad texture coordinates
    public static final byte[] QUAD_TEXT_COORDS = 
    {  
        1, 0,       0, 0,       1, 1,       0, 1,
    };
    
    boolean useNormals, useTextCords, useColors;
    
	protected ByteBuffer vertBuf;
	
	protected ByteBuffer texCordsBuf;
	
	protected ByteBuffer texBuf;
	
	protected int[] texName;
	protected int texWidth, texHeight;
	
	protected GL11 gl11 = null;
	
	public Quad(GL gl) 
	{
		this(gl, null);
	}
	
	public Quad(GL gl, Image texture)
	{
		super(gl);
		
		if(null != gl && gl instanceof GL11)
			this.gl11 = (GL11)gl;
		
	    // init vert buf
	    this.vertBuf = ByteBuffer.allocateDirect(QUAD_VERTS.length);
	    this.vertBuf.put(QUAD_VERTS).rewind();
	    
	    if(null != texture)
	    	setTexture(texture);
	}
	
	public void setTexture(String path)
	{
		Image image = null;
		
		try
		{
			image = Image.createImage(path);
		}
		catch (Exception e) 
		{ 
			System.out.println("Error creating texture for " + path); 
		}
		
		setTexture(image);
	}
	
	public void setTexture(Image img)
	{
		if(null != img)
		{
			//#mdebug debug
			
			int[] params = new int[1];
			this.gl10.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, params, 0);
			
			if( !Utilities3d.validateTexture(img, params[0]) )
				throw new IllegalArgumentException("Error: Texture dimension not power of 2");
			
			//#enddebug
			
			this.texWidth = img.getWidth();
			this.texHeight = img.getHeight();
			
			if(null == texName)
			{
				// generate a texture name
				this.texName = new int[1];
				this.gl10.glGenTextures(1, this.texName, 0); 	
			}
			
			this.gl10.glBindTexture(GL10.GL_TEXTURE_2D, this.texName[0]);
			
			if(null == this.texCordsBuf)
			{
				this.texCordsBuf = ByteBuffer.allocateDirect(QUAD_TEXT_COORDS.length);
				this.texCordsBuf.put(QUAD_TEXT_COORDS).rewind();
			}
			
			// set the minification/magnification techniques
			this.gl10.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
			this.gl10.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
			
			// set wrapping
			this.gl10.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
			this.gl10.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
			
			//#debug debug
			long updateStart = System.currentTimeMillis();
			
			//update texture buffer
			this.texBuf = UtilitiesGLES.textureToByteBuffer(img, this.texBuf, false);
			
			//#debug debug
			System.out.println("UtilitiesGLES.textureToByteBuffer() time: "+(System.currentTimeMillis()-updateStart));
			
			// specify the texture for the currently bound tex name
			this.gl10.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGB, this.texWidth, this.texHeight, 0, 
					GL10.GL_RGB, GL10.GL_UNSIGNED_BYTE, this.texBuf);
		}
	}
	
	protected void renderActor() 
	{
	    // enable the use of vertex arrays when rendering
	    this.gl10.glEnableClientState(GL10.GL_VERTEX_ARRAY);
	    
	    if(null != this.texBuf)
	    {
			// enable the use of texture coords when rendering
			this.gl10.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			
			this.gl10.glEnable(GL10.GL_TEXTURE_2D);
			
			this.gl10.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);
	    }
	    
	    this.gl10.glVertexPointer(3, GL10.GL_BYTE, 0, this.vertBuf);

	    if(null != this.texBuf)
	    {
	    	this.gl10.glBindTexture(GL10.GL_TEXTURE_2D, this.texName[0]);	    	
	    	this.gl10.glTexCoordPointer(2, GL10.GL_BYTE, 0, this.texCordsBuf);  // use tex coords
	    }

	    this.gl10.glNormal3f( 0, 0, 1.0f);   // facing up
	    this.gl10.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
	    
	    if(null != this.texBuf)
	    {
			this.gl10.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			
			this.gl10.glDisable(GL10.GL_TEXTURE_2D);
	    }
	    
	    this.gl10.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}

	protected void updateActor()
	{
		//no update
	}
	
	private boolean bindQuadAsVBO()
	{
		//TODO: if GL11, bind buffers as VBOs
		
		return false;
	}
}
