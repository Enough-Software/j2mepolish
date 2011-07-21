//#condition polish.javapackage.jsr184

package de.enough.polish.graphics3d.m3g.nodes;

import javax.microedition.lcdui.Image;
import javax.microedition.m3g.Appearance;
import javax.microedition.m3g.Group;
import javax.microedition.m3g.Image2D;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.Texture2D;

import de.enough.polish.graphics3d.m3g.utils.NodeFactory;
import de.enough.polish.graphics3d.m3g.utils.UtilitiesM3G;
import de.enough.polish.graphics3d.utils.Utilities3d;

/**
 * Node abstraction that ensure that images of arbitrary size can be represented as
 * a textured 'Quad'. This mimics the ability to do so held by Sprite3D while keeping
 * transformation abilities.  
 * <p>
 * Argument Image object is divided into subquads (submeshes) according to either maximum
 * supported texture size or manually defined texture size.
 *
 * @author Anders Bo Pedersen, anders@wicore.dk
 */
public class TexturePlane extends Group
{
	private int sourceImgWidth = 0, sourceImgHeight = 0;
	
	private int texPerRow, texPerColumn;  
	
	private int textureDimension = 0;
	private int textureDimensionEmployed = 0;
	
	private boolean isExplicitTextureDimension = false;
	
	private Image[][] textureMatrix;
	
	/**
	 * Constructs TexturePlane
	 * 
	 * @param sourceImg source image that is to be shown by subquads
	 */
	public TexturePlane(Image sourceImg) 
	{
		//no explicit texture size defined, using maximum supported
		this(sourceImg, UtilitiesM3G.M3G_MAX_TEXTURE_DIMENSION, false, null);
	}
	
	/**
	 * Constructs TexturePlane
	 * 
	 * @param sourceImg source image that is to be shown by subquads
	 * @param textureDimension texture dimension to be used
	 */
	public TexturePlane(Image sourceImg, int textureDimension) 
	{
		this(sourceImg, textureDimension, true, null );
	}
	
	/**
	 * Constructs TexturePlane
	 * 
	 * @param sourceImg source image that is to be shown by subquads
	 * @param textureDimension texture dimension to be used
	 * @param isExplicit if texture dimension is set explicitly
	 * @param baseAppearance appearance whose settings are to be shared amongst subquads  
	 */
	public TexturePlane(Image sourceImg, int textureDimension, boolean isExplicit, Appearance baseAppearance ) 
	{
		this.textureDimension = textureDimension;
		this.isExplicitTextureDimension = isExplicit;
		
		if(null != sourceImg)
		{
			this.sourceImgWidth = sourceImg.getWidth();
			this.sourceImgHeight = sourceImg.getHeight();
		}
			
		generateSubQuads(sourceImg, baseAppearance, false);
		
		//#mdebug
		
		System.out.println("textureDimension: "+this.textureDimension);
		System.out.println("textureDimensionEmployed: "+this.textureDimensionEmployed);
		System.out.println("texPerRow: "+this.texPerRow);
		System.out.println("texPerColumn: "+this.texPerColumn);
		System.out.println("isExplicitTextureDimension: "+this.isExplicitTextureDimension);
		
		//#enddebug
	}
	
	/**
	 * Update or replace image represented by TexturePlane 
	 * 
	 * @param sourceImg source image that is to be shown by subquads
	 */
	public void setTexture(Image sourceImg) 
	{
		//using default max texture size
		setTexture(sourceImg, UtilitiesM3G.M3G_MAX_TEXTURE_DIMENSION, false, null);
	}
	
	/**
	 * Update or replace image represented by TexturePlane 
	 * 
	 * @param sourceImg source image that is to be shown by subquads
	 * @param textureDimension texture dimension to be used
	 */
	public void setTexture(Image sourceImg, int textureDimension) 
	{
		setTexture(sourceImg, textureDimension, true, null);
	}
	
	/**
	 * Update or replace image represented by TexturePlane
	 * 
	 * @param sourceImg source image that is to be shown by subquads
	 * @param textureDimension texture dimension to be used
	 * @param isExplicit if texture dimension is set explicitly
	 * @param baseAppearance appearance whose settings are to be shared amongst subquads 
	 */
	public void setTexture(Image sourceImg, int textureDimension, boolean isExplicit, Appearance baseAppearance) 
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
		
		generateSubQuads(sourceImg, baseAppearance, isUpdate);
	}
	
	/**
	 * Divides the textureplane image into subquads according to defined texture size.
	 * <p>
	 * Object creation is optionally reduces using isUpdate argument
	 * 
	 * @param sourceImg the image to subdivide
	 * @param isUpdate is argument image same size as the one currently held? 
	 */
	private void generateSubQuads(Image sourceImg, Appearance baseAppearance, boolean isUpdate)
	{
		if(null != sourceImg)
		{
			this.textureDimensionEmployed = this.textureDimension;
			
			//if texture dimension has not been set explicitly, use smallest possible texture size
			if(!isUpdate && !this.isExplicitTextureDimension)
				this.textureDimensionEmployed = Utilities3d.calculateOptimalTextureResolution(sourceImg, this.textureDimension, 50);

			this.textureMatrix = Utilities3d.imageToTextureMatrix(sourceImg, this.textureDimensionEmployed, this.textureMatrix);
			
			this.texPerColumn = this.textureMatrix.length;
			this.texPerRow = this.textureMatrix[0].length;
			
			//using default appearance if none set
			if(null == baseAppearance)
				baseAppearance = new Appearance();
			
			Image img;
			Mesh subQuad;
			Appearance appear;
			Texture2D tex;
			Image2D img2d = null;
			
			/*
			 * Specific to test implementation using direct byte manipulation of texture via Image2D.set(0,0,width,height,byte[]) 
			 */
			/*
			Image2DExtended img2dx;
			
			int[] rgbImageInt;
			byte[] rgbImageByte;
			
			if(!isUpdate)
			{
				rgbImageInt = null;
				rgbImageByte = null;
			}
			else
			{
    			rgbImageInt = new int[textureDimension*textureDimension];
    			rgbImageByte = new byte[textureDimension*textureDimension*3];
			}
			*/
			
			if(this.textureMatrix.length == 1 && this.textureMatrix[0].length == 1)
			{
				//No subdivision
			
				// create transparent Image base with power of 2 size
				img = this.textureMatrix[0][0];
				
				if(!isUpdate)
				{
					// Create 2d texture
					img2d = UtilitiesM3G.createImage2D(img, false, false);
					tex = new Texture2D(img2d);
					tex.setWrapping(Texture2D.WRAP_CLAMP, Texture2D.WRAP_CLAMP);
					tex.setBlending(Texture2D.FUNC_REPLACE);
					tex.setFiltering(Texture2D.FILTER_BASE_LEVEL, Texture2D.FILTER_NEAREST);
					
					//soft clone appearance
					appear = UtilitiesM3G.cloneAppearance( baseAppearance, false);
					
					//apply texture
					appear.setTexture(0, tex);
					
					subQuad = NodeFactory.createQuad(true, true);
					subQuad.setAppearance( 0, appear );
					
					//add to group
					this.addChild(subQuad);
				}
				else
				{
					subQuad = (Mesh)this.getChild(0 );
					appear = subQuad.getAppearance(0);
					tex = appear.getTexture(0);
					img2d = UtilitiesM3G.createImage2D(img, false, false);
					tex.setImage(img2d);
					
					/*
					 * Specific to test implementation using direct byte manipulation of texture via Image2D.set(0,0,width,height,byte[])
					 * Test proved that the overhead over byte[] creation and processing is too big to be an advantage.
					 * If reintroduced, be sure to use mutable Image2D via UtilitiesM3G.createImage2D(img, false, true) 
					 */
					/*
					subQuad = (Mesh)this.getChild(0);
					appear = subQuad.getAppearance(0);
					
					if(null != appear)
					{
						tex = appear.getTexture(0);
						if(null != tex)
						{
							img2d = tex.getImage();
							if(null != img2d)
							{
//								UtilitiesM3G.getImage2DByteRepresentaionFromImage(img, Image2D.RGB, rgbImageInt, rgbImageByte);
								
								img2d.set(0, 0, textureDimension, textureDimension, rgbImageByte);
							}
						}
					}
					*/
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
	    			
					if(!isUpdate)
					{
						// Create 2d texture
						img2d = UtilitiesM3G.createImage2D(img, false, true);
//						img2dx = new Image2DExtended(Image2D.RGB, img);
//						tex = new Texture2D(img2dx);
						tex = new Texture2D(img2d);
						tex.setWrapping(Texture2D.WRAP_CLAMP, Texture2D.WRAP_CLAMP);
						tex.setBlending(Texture2D.FUNC_REPLACE);
						tex.setFiltering(Texture2D.FILTER_BASE_LEVEL, Texture2D.FILTER_NEAREST);
						
						//soft clone appearance
						appear = UtilitiesM3G.cloneAppearance( baseAppearance, false);
						
						//apply texture
						appear.setTexture(0, tex);
						
						// Crating subquads while making sure that softclones of base appearance are used
						subQuad = NodeFactory.createQuad( true, true );
						subQuad.setAppearance(0, appear);
						
						//add to group
						this.addChild(subQuad);
					}
					else
					{
						subQuad = (Mesh)this.getChild(row * this.texPerRow + col );
						appear = subQuad.getAppearance(0);
						tex = appear.getTexture(0);
						img2d = UtilitiesM3G.createImage2D(img, false, false);
						tex.setImage(img2d);
						
						/*
						 * Specific to test implementation using direct byte manipulation of texture via Image2D.set(0,0,width,height,byte[])
						 * Test proved that the overhead over byte[] creation and processing is too big to be an advantage.
						 * If reintroduced, be sure to use mutable Image2D via UtilitiesM3G.createImage2D(img, false, true) 
						 */
						/*
						subQuad = (Mesh)this.getChild(row * this.texPerRow + col );
						appear = subQuad.getAppearance(0);
						tex = appear.getTexture(0);
						img2dx = (Image2DExtended)tex.getImage();
						img2dx.set(img);
						*/
					}
	    			
					//#debug debug
					System.out.println("Sub texture local translation: x: "+(xOffSetBase + (col * 2))+" y: "+(yOffSetBase + (-row * 2))+" z: "+0f);
					
					//set local translation
					//Note tranlate(1,-1,0) to move plane anchor to top left corner
					subQuad.setTranslation(xOffSetBase + (col * 2) + 1 ,yOffSetBase + (-row * 2) - 1, 0);
	    			
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
	 * @return Mesh the subQuad if found otherwise null
	 */
	public Mesh getSubQuad(int rowNum, int colNum)
	{
		if( rowNum>=0 && rowNum<this.texPerRow && 
				colNum>=0 && colNum<this.texPerColumn)
		{
			return (Mesh) getChild( (rowNum*this.texPerRow) + (colNum*this.texPerColumn) );
		}
		
		return null;
	}
	
	/**
	 * 
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
		while(getChildCount() > 0)
			this.removeChild(this.getChild(0));
	}
}
