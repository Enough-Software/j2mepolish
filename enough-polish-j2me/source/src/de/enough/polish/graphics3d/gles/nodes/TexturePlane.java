//#condition polish.javapackage.jsr239

package de.enough.polish.graphics3d.gles.nodes;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.lcdui.Image;

import de.enough.polish.graphics3d.utils.Utilities3d;

import de.enough.polish.math.FP;

/**
 * Node abstraction that ensure that images of arbitrary size can be represented as
 * a textured 'Quad'.
 * <p>
 * Argument Image object is divided into subquads (submeshes) according to either maximum
 * supported texture size or manually defined texture size.
 *
 * @author Anders Bo Pedersen, anders@wicore.dk
 */
public class TexturePlane extends Actorx
{
	private Image[][] textureMatrix;
	
	private int textureDimension;
	private int textureDimensionEmployed = 0;
	
	private boolean isExplicitTextureDimension = false;
	
	private int sourceImgWidth = 0, sourceImgHeight = 0;
	
	private int texPerRow, texPerColumn;  

	public TexturePlane(GL10 gl, Image sourceImg)
	{
		super(gl);
		
		int[] params = new int[1];
		gl.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, params, 0);
		
		this.textureDimension = params[0];
		
		if(null != sourceImg)
		{
			this.sourceImgWidth = sourceImg.getWidth();
			this.sourceImgHeight = sourceImg.getHeight();
		}
		
		generateSubQuads(sourceImg, false);
		
		//#mdebug
		/*
		System.out.println("textureDimension: "+this.textureDimension);
		System.out.println("textureDimensionEmployed: "+this.textureDimensionEmployed);
		System.out.println("texPerRow: "+this.texPerRow);
		System.out.println("texPerColumn: "+this.texPerColumn);
		System.out.println("isExplicitTextureDimension: "+this.isExplicitTextureDimension);
		*/
		//#enddebug
	}
	
	public TexturePlane(GL10 gl, Image sourceImg, int textureDimension) 
	{
		this(gl, sourceImg, textureDimension, true);
	}
	
	public TexturePlane(GL10 gl, Image sourceImg, int textureDimension, boolean isExplicit) 
	{
		super(gl);
		
		this.textureDimension = textureDimension;
		this.isExplicitTextureDimension = isExplicit;
		
		if(null != sourceImg)
		{
			this.sourceImgWidth = sourceImg.getWidth();
			this.sourceImgHeight = sourceImg.getHeight();
		}
			
		generateSubQuads(sourceImg, false);
		
		//#mdebug
		/*
		System.out.println("textureDimension: "+this.textureDimension);
		System.out.println("textureDimensionEmployed: "+this.textureDimensionEmployed);
		System.out.println("texPerRow: "+this.texPerRow);
		System.out.println("texPerColumn: "+this.texPerColumn);
		System.out.println("isExplicitTextureDimension: "+this.isExplicitTextureDimension);
		*/
		//#enddebug
	}
	
	/**
	 * Update or replace image represented by TexturePlane 
	 * 
	 * @param sourceImg source image that is to be shown by subquads
	 */
	public void setTexture(Image sourceImg) 
	{
		//using last set dimension as default
		setTexture(sourceImg, this.textureDimension, false);
	}
	
	/**
	 * Update or replace image represented by TexturePlane 
	 * 
	 * @param sourceImg source image that is to be shown by subquads
	 * @param textureDimension texture dimension to be used
	 */
	public void setTexture(Image sourceImg, int textureDimension) 
	{
		setTexture(sourceImg, textureDimension, true);
	}
	
	/**
	 * Update or replace image represented by TexturePlane
	 * 
	 * @param sourceImg source image that is to be shown by subquads
	 * @param textureDimension texture dimension to be used
	 * @param isExplicit if texture dimension is set explicitly
	 */
	public void setTexture(Image sourceImg, int textureDimension, boolean isExplicit) 
	{
		boolean isUpdate = false;
		
		if(null != sourceImg)
		{
			if( this.textureDimension == textureDimension &&
					this.isExplicitTextureDimension == isExplicit &&
					sourceImg.getWidth() == this.sourceImgWidth &&
					sourceImg.getHeight() == this.sourceImgHeight)
				isUpdate = true;
			else
			{
				this.textureDimension = textureDimension;
				this.isExplicitTextureDimension = isExplicit;
				this.sourceImgWidth = sourceImg.getWidth();
				this.sourceImgHeight = sourceImg.getHeight();
				clearChildren();
			}
		}
		
		generateSubQuads(sourceImg, isUpdate);
	}
	
	private void generateSubQuads(Image sourceImg, boolean isUpdate)
	{
		if(null != sourceImg)
		{
			//if texture dimension has not been set explicitly, use smallest possible texture size
			if(!isUpdate && !this.isExplicitTextureDimension)
				this.textureDimensionEmployed = Utilities3d.calculateOptimalTextureResolution(sourceImg, this.textureDimension, 50);

			this.textureMatrix = Utilities3d.imageToTextureMatrix(sourceImg, this.textureDimensionEmployed, this.textureMatrix);
			
			this.texPerColumn = this.textureMatrix.length;
			this.texPerRow = this.textureMatrix[0].length;
			
			Image img;
			Quad subQuad;
			
			if(this.textureMatrix.length == 1 && this.textureMatrix[0].length == 1)
			{
				//No subdivision
			
				// create transparent Image base with power of 2 size
				img = this.textureMatrix[0][0];
				
				if(!isUpdate)
				{
					subQuad = new Quad(this.gl10,img);
					
					//add to group
					this.addChild(subQuad);
				}
				else
				{
					subQuad = (Quad)this.getChild(0);
					subQuad.setTexture(img);					
				}
			}
			else
			{
				//Sub divisions
				
				//matrix dims
				int numTex = this.texPerRow * this.texPerColumn;
				int xOffSetBase = (this.texPerRow * -2)>>1;
				int yOffSetBase = (this.texPerColumn * 2)>>1;
				
				int i = 0;
				while(i < numTex)
				{
					int col = i % this.texPerRow;
					int row = i / this.texPerRow;
					
					//create sub texture
					img = this.textureMatrix[row][col];
					
	    			subQuad = null;
	    			
	    			//#debug debug
	    			long updateStart = System.currentTimeMillis();
	    			
					if(!isUpdate)
					{
						subQuad = new Quad(this.gl10,img);
						
						//add to group
						this.addChild(subQuad);
					}
					else
					{
						subQuad = (Quad)this.getChild(row * this.texPerRow + col );
						subQuad.setTexture(img);
					}
					
	    			//#mdebug debug
					/*
	    			System.out.println("Quad creation time: "+(System.currentTimeMillis()-updateStart));
					System.out.println("Sub texture local translation: x: "+(xOffSetBase + (col * 2))+" y: "+(yOffSetBase + (-row * 2))+" z: "+0f);
					*/
					//#enddebug
					
					//set local translation
					//Note tranlate(1,-1,0) to move plane anchor to top left corner
					subQuad.setTranslationx(FP.intToFix(xOffSetBase + (col * 2) + 1) ,FP.intToFix(yOffSetBase + (-row * 2) - 1), 0);
	    			
	    			++i;
				}
			}
		}
	}
	
	/**
	 * @return int Number of subQuads per row
	 */
	public int getNumSubQuadPerRow()
	{
		return this.texPerRow;
	}
	
	/**
	 * @return int Number of subQuads per column
	 */
	public int getNumSubQuadPerColumn()
	{
		return this.texPerColumn;
	}
	
	/**
	 * Returns a subQuad according to argument row and column number
	 * @param rowNum 
	 * @param colNum
	 * @return Quad the subQuad if found otherwise null
	 */
	public Quad getSubTexture(int rowNum, int colNum)
	{
		if( rowNum>=0 && rowNum<this.textureMatrix.length && 
				colNum>=0 && colNum<this.textureMatrix[0].length)
		{
			return (Quad) getChild( (rowNum*this.textureMatrix.length) + (colNum*this.textureMatrix[0].length) );
		}
		
		return null;
	}
	
	/**
	 * @return the texture dimension
	 */
	public int getTextureDimension()
	{
		return this.textureDimensionEmployed;
	}
	
	/**
	 * Removes all children of this node
	 */
	private void clearChildren()
	{
		this.children.clear();
	}

	protected void renderActor()
	{
		//children drawn automatically in Actorx.render()
	}

	protected void updateActor()
	{
		//no local updates
	}
}
