/*
 * Created on 05-Aug-2005 at 14:33:58.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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
package de.enough.polish.resources;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Locale;

import javax.imageio.ImageIO;

import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGUniverse;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.StringUtil;

/**
 * <p>Converts SVG files during the build process.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        05-Aug-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 * @author Tim Muders, tim@enough.de
 */
public class SvgConverterResourceCopier extends ResourceCopier {

	private boolean scaleToFit = false;
	private int defaultIconWidth = 15;
	private int defaultIconHeight = 15;
	private int defaultScreenWidth;
	private int defaultScreenHeight;
	
	
	public SvgConverterResourceCopier() {
		super();
	}
	
	
	
	/**
	 * Determines whether the device supports the SVG API.
	 * 
	 * @param env the environment settings
	 * @return true when the device supports the SVG API
	 */
	protected boolean supportsSvgApi( Environment env ) {
		return env.hasSymbol( "polish.api.svg" );
	}
	
	/**
	 * Retrieves the maximum allowed icon size, if available.
	 * 
	 * @param env the environment settings
	 * @return the maximum allowed size or null when the target device does not define the "IconSize" capability.
	 */
	protected Dimension getIconSize( Environment env ) {
		String iconSizesStr = env.getVariable("polish.IconSize");
		if (iconSizesStr == null) {
			return new Dimension( this.defaultIconWidth, this.defaultIconHeight );
		} else {
			String[] iconSizes = StringUtil.splitAndTrim( iconSizesStr, ',' );
			int maxWidth = 0;
			int maxHeight = 0;
			for (int i = 0; i < iconSizes.length; i++) {
				String sizeStr = iconSizes[i];
				String[] dimensions = StringUtil.splitAndTrim(sizeStr, 'x' );
				int width = Integer.parseInt( dimensions[0]);
				int height = Integer.parseInt( dimensions[1]);
				if (width > maxWidth || height > maxHeight ) {
					maxWidth = width;
					maxHeight = height;
				}
			}
			return new Dimension( maxWidth, maxHeight );
		}
	}
	
	
	/**
	 * Retrieves the size of the screen, if available.
	 * 
	 * @param env the environment settings
	 * @return the sceen size or null, when the current device does not define the "polish.ScreenSize" capability.
	 */
	protected Dimension getScreenSize( Environment env ) {
		String screenSizesStr = env.getVariable("polish.ScreenSize");
		if (screenSizesStr == null) {
			return null;
		} else {
			return new Dimension(
					Integer.parseInt( env.getVariable("polish.ScreenWidth") ),
					Integer.parseInt( env.getVariable("polish.ScreenHeight") )
					);
		}		
	}

	/**
	 * Copies all resources for the target device and the target locale to the final resources directory, SVG files are converted on the fly.
	 * 
	 * @param device the current target device
	 * @param locale the current target locale, can be null
	 * @param resources an array of resources
	 * @param targetDir the target directory
	 * @throws IOException when a resource could not be copied.
	 */
	public void copyResources(Device device, Locale locale, File[] resources,
			File targetDir) 
	throws IOException 
	{
		Environment env = device.getEnvironment();
		boolean supportsSvgApi = supportsSvgApi( env );
		Dimension iconSize = getIconSize( env );
		Dimension screenSize = getScreenSize( env );
		
		ArrayList leftResourcesList = new ArrayList();
		for (int i = 0; i < resources.length; i++) {
			File file = resources[i];
			if ( file.getName().endsWith(".svg") || file.getName().endsWith(".SVG") ) {
	
					handleSvgFile( file, env, device, supportsSvgApi, iconSize, screenSize, targetDir );
		
			} else {
				leftResourcesList.add( file );
			}
		}
		File[] leftResources = (File[]) leftResourcesList.toArray( new File[ leftResourcesList.size() ] );
		FileUtil.copy( leftResources, targetDir );
	}
	
	public void setIconSize( String defaultSize ) {
//		System.out.print("seticonsizeStart\n");
		if (defaultSize == null) {
			setDefaultIconWidth( 15 );
			setDefaultIconHeight( 15 );
		} else {
			String[] chunks = StringUtil.splitAndTrim( defaultSize, 'x' );
			if (chunks.length != 2) {
				throw new IllegalArgumentException("Invalid IconSize parameter for svgconverter: " + defaultSize );
			}
			setDefaultIconWidth( Integer.parseInt(chunks[0]) );
			setDefaultIconHeight( Integer.parseInt(chunks[1]) );
		}
//		System.out.print("seticonsizeEnde\n");
	}
	
	public void setScaleToFit( boolean scaleToFit ) {
		this.scaleToFit = scaleToFit;
	}
	
	
	
	private void handleSvgFile(File file, Environment env, Device device, boolean supportsSvgApi, Dimension iconSize, Dimension screenSize, File targetDir) 
	throws IOException  
	{		
		if(file.getName().startsWith("icon") || file.getName().startsWith("bg")){
		double newWidth = 0,newHeight = 0;
		if (file.getName().startsWith("icon")) {
	  		newWidth = iconSize.width;
	  		newHeight = iconSize.height; 
		} else if(file.getName().startsWith("bg")){
	    	if(screenSize == null && !this.scaleToFit){
	    		  screenSize = new Dimension(128,160);
	    	}
			newWidth = screenSize.width;
			newHeight = screenSize.height; 
	   }	
  		SVGUniverse universe = new SVGUniverse();
  		SVGDiagram diagram = null;	
//  		System.out.print(file.getAbsolutePath()+"\n");
  		URI uri = universe.loadSVG( new FileInputStream( file ), "svgimage" );
//  		System.out.print(file.getAbsolutePath()+"\n");
//  		System.out.print(uri.getPath()+"\n");
  		diagram = universe.getDiagram(uri);	 
  		double width = diagram.getWidth();
  		double height = diagram.getHeight();
  		BufferedImage image = new BufferedImage( (int)newWidth, (int)newHeight, BufferedImage.TYPE_4BYTE_ABGR);		
  		Graphics2D g = image.createGraphics();	
  		g.setClip(0,0,(int)newWidth,(int)newHeight);
  		double sX = newWidth / width;
  		double sY =  newHeight / height;
  		g.scale(sX,sY);
  		
//	  		System.out.println("handling svg file " + file.getAbsolutePath() );
//	  		System.out.println("targetDir=" + targetDir.getAbsolutePath() );
//	  		System.out.println("width: " + width );
//	  		System.out.println("height: " + height );
//	  		System.out.println("newWidth: " + newWidth );
//	  		System.out.println("newHeight: " +newHeight );
//	  		System.out.println("sX: " + sX );
//	  		System.out.println("sY: " +sY );
//	  		System.out.println("breite: " + width * sX );
//	  		System.out.println("hoehe: " + height * sY );
		try {
			diagram.render(g);
		} catch (SVGException e) {
			throw new IOException(e.toString());
		}
		  		
  		File target = new File(targetDir, file.getName().substring( 0, file.getName().length() - 4 )  +  ".png");
  		FileOutputStream out;
  		out = new FileOutputStream( target );
  		ImageIO.write( image, "png", out );
  		out.close();
		}
	}



	public void setDefaultIconHeight(int defaultIconHeight) {
		this.defaultIconHeight = defaultIconHeight;
	}



	public void setDefaultIconWidth(int defaultIconWidth) {
		this.defaultIconWidth = defaultIconWidth;
	}



	public void setDefaultScreenHeight(int defaultScreenHeight) {
		this.defaultScreenHeight = defaultScreenHeight;
	}



	public void setDefaultScreenWidth(int defaultScreenWidth) {
		this.defaultScreenWidth = defaultScreenWidth;
	}

}
