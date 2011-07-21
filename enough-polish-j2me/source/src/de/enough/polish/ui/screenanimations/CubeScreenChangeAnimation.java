//#condition polish.usePolishGui && polish.api.3dapi
/*
 * Created on 26.08.2005 at 10:33:18.
 * 
 * Copyright (c) 2010 Robert Virkus / Enough Software
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
package de.enough.polish.ui.screenanimations;

import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.m3g.Appearance;
import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.Image2D;
import javax.microedition.m3g.IndexBuffer;
import javax.microedition.m3g.Light;
import javax.microedition.m3g.Material;
import javax.microedition.m3g.Texture2D;
import javax.microedition.m3g.Transform;
import javax.microedition.m3g.TriangleStripArray;
import javax.microedition.m3g.VertexArray;
import javax.microedition.m3g.VertexBuffer;

import de.enough.polish.ui.ScreenChangeAnimation;
import de.enough.polish.ui.Style;



public class CubeScreenChangeAnimation extends ScreenChangeAnimation {
	private Graphics3D  graphics3d;
	private Camera   camera;
	private Light   light;
	private float   angle = 0.0f,angle2 = 0.0f;
	//private Transform  transform = new Transform();
	private javax.microedition.m3g.Background  background = new javax.microedition.m3g.Background();
	private VertexBuffer vbuffer;
	private IndexBuffer  indexbuffer;
	private Appearance  appearance;
	private Material  material = new Material();
	private float fHeight,fWidth;
	
	public CubeScreenChangeAnimation() {
		super();
	}
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#show(de.enough.polish.ui.Style, javax.microedition.lcdui.Display, int, int, javax.microedition.lcdui.Image, javax.microedition.lcdui.Image, de.enough.polish.ui.Screen)
	 */
	protected void onShow(Style style, Display dsplay, int width, int height,
			Displayable lstDisplayable, Displayable nxtDisplayable, boolean isForward  ) 
	{
		  super.onShow(style, dsplay, width, height, lstDisplayable, nxtDisplayable, isForward );
		  
		float wPercent = width / 256.0f;
		float hPercent = height / 256.0f;
		this.fWidth = (5.0f*wPercent); this.fHeight = (5.0f*hPercent);
		//System.out.print("fwidth:"+(this.fWidth)+";fheight:"+this.fHeight+";fwprocent:"+(wPercent)+";fhprocent:"+(hPercent)+"\n");
		 this.graphics3d = Graphics3D.getInstance();
		 this.camera = new Camera();
		 //System.out.print((float)getWidth()/ (float)getHeight()+"\n");
		 this.camera.setPerspective( 90.0f,
		   (float)getWidth()/ (float)getHeight(),
		   0.1f,
		   200.0f );

//		 this.camera.setOrientation(100.0f,1.0f,1.0f,1.0f);
		 this.light = new Light();
		 this.light.setColor(0xffffff);
		 this.light.setMode(Light.AMBIENT);
		 this.light.setIntensity(2.25f);

		  short[] vert = {
				  5, 5, 5,  -5, 5, 5,   5,-5, 5,  -5,-5, 5, // 0,0,5,   5,0,5,   0,-5,5,  -5,0,5,  0,5,5,//front
//				 -5, 5,-5,   5, 5,-5,  -5,-5,-5,   5,-5,-5, //back
				 -5, 5, 5,  -5, 5,-5,  -5,-5, 5,  -5,-5,-5, //left
//				  5, 5,-5,   5, 5, 5,   5,-5,-5,   5,-5, 5,  //right
//				  5, 5,-5,  -5, 5,-5,   5, 5, 5,  -5, 5, 5,//up
//				  5,-5, 5,  -5,-5, 5,   5,-5,-5,  -5,-5,-5//down
		  };
		  VertexArray vertArray = new VertexArray(vert.length/3 , 3, 2);
		  vertArray.set(0, vert.length/3, vert);
		  int[] indices = { 
//				  			8,1,4,7,
//				  			4,7,6,3,
//				  			0,8,5,4,
//				  			5,4,2,6,
//				  			9,10,11,12
				  			0,1,2,3,
				  			4,5,6,7
	  			}; 
		  int[] stripLen = { 4,4};
		  // The per-vertex normals for the cube
		  byte[] norm = {
				   0, 0, 127,     0, 0, 127,     0, 0, 127,   0, 0, 127, //0, 0, 127, 0, 0, 127, 0, 0, 127, 0, 0, 127, 0, 0, 127,
				   -127, 0,0,     -127, 0,0,     -127, 0,0,   -127, 0,0,  
		  };
		  VertexArray normArray = new VertexArray(norm.length / 3, 3, 1);
		  normArray.set(0, norm.length/3, norm);
//		  // per vertex texture coordinates
// 			must build an Z
		  short[] tex = {
//	                256,0, 0,0, 256,256, 0,256 ,//128,128, 256,128, 128,256, 0,128 ,128,0,
				  0,0,  0,0, 0,0,0,0,    			 
				  0,0,  256,0,   0,256,   256,256,
				  };
		  VertexArray texArray = new VertexArray(tex.length / 2, 2, 2);
		  texArray.set(0, tex.length/2, tex);
		  short[] lsttex = {    
				  256,0,  0,0,   256,256,   0,256,
				  	0,0,  0,0,   0,0,   0,0,	//0,0, 0,0, 0,0, 0,0, 0,0,
	                	
	                };

		  VertexArray lsttexArray = new VertexArray(tex.length / 2, 2, 2);
		  lsttexArray.set(0,tex.length/2, lsttex);
		  
		  

		  // the VertexBuffer for our object
		  VertexBuffer vb = this.vbuffer = new VertexBuffer();
		  vb.setPositions(vertArray,1.0f, null);
		  vb.setNormals(normArray);
		  vb.setTexCoords(0, lsttexArray,(1.0f/256.0f),null);
		  vb.setTexCoords(1, texArray, 1.0f/256.0f, null);
		  this.indexbuffer = new TriangleStripArray( indices, stripLen );
		  int[] rgbData = new int[256*256];
		  int[] lstrgbData = new int[256*256];
		  int[] rgbbuffer = new int [this.nextCanvasImage.getWidth() * this.nextCanvasImage.getHeight()];
		  int[] lstrgbbuffer = new int [this.lastCanvasImage.getWidth() * this.lastCanvasImage.getHeight()];
		  int row = 0,rgbCount=0;
		  for(int i = 0; i < rgbData.length;i++){
			  if(row < width && rgbCount < rgbbuffer.length){
				  rgbData[i] = rgbbuffer[rgbCount];
				  rgbCount ++;
			  }
			  row = (row + 1) % 256;		  
		  }
		  rgbCount = 0;row = 0;
		  for(int i = 0; i < lstrgbData.length;i++){
			  if(row < width && rgbCount < lstrgbbuffer.length){
				  lstrgbData[i] = lstrgbbuffer[rgbCount];
				  rgbCount++;
			  }
			  row = (row + 1) % 256;		  
		  }
		  // the image for the texture
		  Image image = Image.createRGBImage(rgbData,256,256,false);
		  Image lstImage = Image.createRGBImage(lstrgbData,256,256,false);
		  
		  Image2D image2D = new Image2D( Image2D.RGB, image );
		  Image2D lstImage2D = new Image2D (Image2D.RGB,lstImage);
		  
		  Texture2D texture = new Texture2D( lstImage2D );
		  Texture2D lsttexture = new Texture2D( image2D );
		  
		  texture.setFiltering(Texture2D.FILTER_NEAREST,
		        Texture2D.FILTER_NEAREST);
		  texture.setWrapping(Texture2D.WRAP_CLAMP,
		       Texture2D.WRAP_CLAMP);
		  texture.setBlending(Texture2D.FUNC_MODULATE);
		  // create the appearance
		  this.appearance = new Appearance();	
		  this.appearance.setTexture(0,lsttexture );
		  this.appearance.setTexture(1,texture);  
		 
		  this.appearance.setMaterial(this.material);
		  this.material.setVertexColorTrackingEnable(true);
		  this.material.setColor(Material.DIFFUSE, 0xFFFFFFcc);
		  this.material.setColor(Material.SPECULAR, 0xFFFFFFcc);
		  this.material.setShininess(100.0f);
		  this.background.setColor(0xFFFFFFcc);
		

	}
	
	
	protected boolean animate() {
		return true;
	}

	public void paintAnimation(Graphics g) {
		
		  this.graphics3d.bindTarget(g, true,
			      Graphics3D.DITHER |
			      Graphics3D.TRUE_COLOR);
//		  this.graphics3d.setViewport(0,0,this.width,this.height);
		  this.graphics3d.clear(this.background);
			  // Set the camera
			  Transform transform = new Transform();
//			  transform.postTranslate(0.0f, 0.0f, 18.0f);
//			  transform.postTranslate(-(this.fWidth/2.0f), (this.fHeight/2.0f),(this.fWidth*this.fWidth)/2.0f);
			  //System.out.print((this.fWidth/2.0f)+";"+(this.fHeight/2.0f)+";"+(this.fHeight*this.fHeight)/2.0f+"\n");
			  transform.postTranslate(-(this.fWidth/2.0f),(0.78f),(this.fHeight*this.fHeight)/2.0f);
//			  System.out.print((this.fWidth/2.0f)+";"+(this.fHeight/2.0f)+";"+(this.fHeight*this.fHeight)/2.0f+"\n");
//			  System.out.print("height:"+this.height+";width:"+this.width);
			  transform.postTranslate(-(this.fWidth/2.0f),(this.fHeight/2.0f),(this.fHeight*this.fHeight)/2.0f);
//			  transform.postRotate(this.angle2,1.0f,this.angle2,1.0f);
//			  this.camera.translate(1.0f,1.0f,1.0f);
//			  this.camera.setOrientation(90.0f,1.0f,1.0f,1.0f);
			  this.graphics3d.setCamera(this.camera,transform);
			  // Set light
			  this.graphics3d.resetLights();
			  this.graphics3d.addLight(this.light, transform);
			  // see rotation
			  this.angle += 0.5f;
			 // System.out.print(this.angle+"\n");
			  this.angle2 += 0.2f;
			  transform.setIdentity();
//			  transform.postRotate(1.0f,1.0f,1.0f, 1.0f);

			  transform.postTranslate(0.0f,0.0f,0.0f);
			  this.graphics3d.render(this.vbuffer, this.indexbuffer, this.appearance, transform);
			  this.graphics3d.render(this.vbuffer, this.indexbuffer, this.appearance, transform);
			  this.graphics3d.releaseTarget();
	}
}