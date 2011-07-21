package de.enough.polish.graphics3d.utils;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * Generel 3D graphics utility class.
 * <p>
 * Implements helper functions associated with 3d graphics development
 * 
 * @author Anders Bo Pedersen, anders@wicore.dk
 */
public class Utilities3d 
{
	/**
	 * Divides argument image into a matrix of smaller images based on the allowed maxTexDim
	 * size. The resulting Image[row][column] contains the smaller images.  
	 * 
	 * @param sourceImg
	 * @param maxTexDim
	 * @param texMatrix
	 * @return the image matrix
	 */
	public static Image[][] imageToTextureMatrix(Image sourceImg, int maxTexDim, Image[][] texMatrix)
	{
		if(null != sourceImg)
		{
			if(maxTexDim < 0)
				maxTexDim = 1 << 8;
			
			int imgW = sourceImg.getWidth();
			int imgH = sourceImg.getHeight();
			
    		// define max img dim
    		int maxImgDim = Math.max(imgW, imgH);
    		
    		// find minimum text dim that fits image
    		int i = 0;
    		while(maxImgDim > (1 << ++i));
    		
    		int minTexDimRespect = 1 << i;
    		
    		// is sub divisioning nessesary?
    		if(minTexDimRespect <= maxTexDim)
    		{
    			//No subdivision
    			
    			//init texmatrix?
    			if(null == texMatrix)
    				texMatrix = new Image[1][1];
    			
    			Image base = texMatrix[0][0] = Image.createImage(maxTexDim, maxTexDim); 
    			
    			Graphics g = base.getGraphics();
    			
    			//fill black
    			g.setColor(0x000000);
    			g.fillRect(0, 0, base.getWidth(), base.getHeight());
    			
    			// draw image on texture base
    			g.drawImage(sourceImg, (base.getWidth() - imgW) >> 1, (base.getHeight() - imgH) >> 1, 0);
    		}
    		else
    		{
    			//Sub division needed
    			
    			//define number of needed subdivisions
    			int numSubTexPerRow = 0, numSubTexPerColumn = 0;
    			
    			while(maxTexDim * numSubTexPerRow < imgW)
    				++numSubTexPerRow;
    			
    			while(maxTexDim * numSubTexPerColumn < imgH)
    				++numSubTexPerColumn;
    			
    			//create texSet
    			int totalNumSubTex = numSubTexPerRow*numSubTexPerColumn;
    			int totalRowTexSetDim = numSubTexPerRow * maxTexDim;
    			int totalColumnTexSetDim = numSubTexPerColumn * maxTexDim;
    			
    			//init texmatrix?
    			if(null == texMatrix)
    				texMatrix = new Image[numSubTexPerColumn][numSubTexPerRow];
    			
				//calc generel source image offset
				int xOffsetBase = (totalRowTexSetDim - imgW) >> 1;
				int yOffsetBase = (totalColumnTexSetDim - imgH) >> 1;
    			
    			i = 0;
    			Image img;
    			while(i < totalNumSubTex)
    			{
    				//draw source image to subtex
    				int column = i % numSubTexPerRow;
    				int row = i / numSubTexPerRow;
    				
    				if(null == texMatrix[row] || null == texMatrix[row][column])
    					//create sub texture
    					texMatrix[row][column] = img = Image.createImage(maxTexDim, maxTexDim);
    				else
    					img = texMatrix[row][column];
    				
    				//get graphics handle and set default background to black
    				Graphics g = img.getGraphics();
    				g.setColor(0xFF000000);
    				g.fillRect(0, 0, img.getWidth(), img.getHeight());
    				
    				//calc generel source image offset
    				int xOffset = xOffsetBase;
    				int yOffset = yOffsetBase;
    				
    				//adjust offsets to current sub tex 
    				xOffset -= column * maxTexDim;
    				yOffset -= row * maxTexDim;
    				
    				g.drawImage(sourceImg, xOffset, yOffset, 0);
    				
        			++i;
    			}
    		}
    		
    		return texMatrix;
		}
		else
			return null;
	}
	
	/**
	 * Returns the number of horizontal subdivisions the argument image needs to
	 * be divided into to respect argument maxTexDim size
	 * 
	 * @param sourceImg
	 * @param maxTexDim
	 * @return the number of texture matrices
	 */
	public static int getNumTextureMatrixPerRow(Image sourceImg, int maxTexDim)
	{
		if(null != sourceImg)
		{
			//define number of needed subdivisions
			int numSubTexPerRow = 0, imgW =sourceImg.getWidth();
			
			while(maxTexDim * numSubTexPerRow < imgW)
				++numSubTexPerRow;
			
			return numSubTexPerRow;
		}
		
		return -1;
	}
	
	/**
	 * Returns the number of vertical subdivisions the argument image needs to
	 * be divided into to respect argument maxTexDim size
	 * 
	 * @param sourceImg
	 * @param maxTexDim
	 * @return the number of texture matrices
	 */
	public static int getNumTextureMatrixPerColumn(Image sourceImg, int maxTexDim)
	{
		if(null != sourceImg)
		{
			//define number of needed subdivisions
			int numSubTexPerColumn = 0, imgH = sourceImg.getHeight();
			
			while(maxTexDim * numSubTexPerColumn < imgH)
				++numSubTexPerColumn;
			
			return numSubTexPerColumn;
		}
		
		return -1;
	}
	
	/**
	 * Checks if the dimension of an image is power of 2 and hence a
	 * valid texture.
	 * 
	 * @param img image to evaluate
	 * @param maxTexture max texture size allowed by platform
	 * @return true if image dimension is power of 2, false otherwise
	 */
	public static boolean validateTexture(Image img, int maxTexture)
	{
		if(null == img)
			return false;
		
		int imgWidth = img.getWidth();
		
		while(maxTexture > 0)
		{
			if(maxTexture == imgWidth)
				return true;
			maxTexture >>= 1;
		}
		
		return false;
	}
	
	
	
	/**
	 * 
	 * @param sourceImg
	 * @param maxTexDim
	 * @param thresholdPercent
	 */
	public static int calculateOptimalTextureResolution(Image sourceImg, int maxTexDim, int thresholdPercent )
	{
		if(null != sourceImg)
		{
			int imgWidth = sourceImg.getWidth();
			int imgHeight = sourceImg.getHeight();
			
			final int max = Math.max(imgWidth, imgHeight);
			
			//first, shrink to fit
			while( max < (maxTexDim >> 1) )
				maxTexDim >>= 1;
				
			long pixelRealestate = Utilities3d.getNumTextureMatrixPerRow(sourceImg, maxTexDim) * Utilities3d.getNumTextureMatrixPerColumn(sourceImg, maxTexDim) * (maxTexDim*maxTexDim);
			
			int maxTexDimCandidate = maxTexDim >> 1;
			long pixelRealestateCandidate = Utilities3d.getNumTextureMatrixPerRow(sourceImg, maxTexDimCandidate) * Utilities3d.getNumTextureMatrixPerColumn(sourceImg, maxTexDimCandidate) * (maxTexDimCandidate*maxTexDimCandidate);
			
			while( (int)( ((pixelRealestateCandidate * 1000000l) / pixelRealestate) / 10000l ) <= thresholdPercent)
			{
				pixelRealestate = pixelRealestateCandidate;
				maxTexDim = maxTexDimCandidate;
				
				maxTexDimCandidate = maxTexDim >> 1;
				pixelRealestateCandidate = Utilities3d.getNumTextureMatrixPerRow(sourceImg, maxTexDimCandidate) * Utilities3d.getNumTextureMatrixPerColumn(sourceImg, maxTexDimCandidate) * (maxTexDimCandidate*maxTexDimCandidate);
			}
			
			return maxTexDim;
		}
		
		return -1;
	}
	
//#mdebug debug
	
	public static String systemToString()
	{
		String newline = "\n";
		
		String properties = 
			
			"MIDP Version: "+System.getProperty("microedition.profiles")+newline+
			"CLDC Version: "+System.getProperty("microedition.configuration")+newline+
			
			"Platform: "+System.getProperty("microedition.platform")+newline+
			"Locale: "+System.getProperty("microedition.locale")+newline+
			"Encoding: "+System.getProperty("microedition.encoding")+newline+
			
			"Total memory: "+Runtime.getRuntime().totalMemory()+newline+
			"Free memory: "+Runtime.getRuntime().freeMemory()+newline+
			"Used memory: "+(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
		
		return properties;
	}
	
//#enddebug
}
