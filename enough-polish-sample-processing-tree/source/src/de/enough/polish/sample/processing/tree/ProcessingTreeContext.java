/*
 * Copyright (c) 2011 Robert Virkus / Enough Software
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

package de.enough.polish.sample.processing.tree;

import de.enough.polish.processing.ProcessingContext;
import de.enough.polish.processing.color;

/**
 * An example Processing Context that renders a randomly generated tree.
 * Adapted from the sketch at http://www.openprocessing.org/visuals/?visualID=4732
 *
 * @author Ovidiu Iliescu
 */
public class ProcessingTreeContext extends ProcessingContext {

	float recursions;
	public float[] branchX = new float[1];
	float[] branchY = new float[1];
	float[] branchR = new float[1];
	float[] tempR = new float[0];
	float[] tempX = new float[0];
	float[] tempY = new float[0];
	float sw;
	 
	public void setup(){
		branchX = new float[1];
		branchY = new float[1];
		branchR = new float[1];
		tempR = new float[0];
		tempX = new float[0];
		tempY = new float[0];
		noLoop();
		redraw();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.processing.ProcessingContext#draw()
	 */
	public void draw(){ 

		// Initial drawing settings
		// ------------------------
		
		// Number of recursion steps. The more steps there are, the more branches the tree has.
		recursions = 25;
		  
		// Maximum line thickness ( should be >= than the number of recursions)
		sw = 25;
		
		// Grass colour
		color grass = color(110, 210, 110, 255);
		
		// Cloud colour
		color clouds = color(255,170);
		
		// Leaf colours
		color[] leafColors = { color(0xD56F2B), color(0xD6593A), color(0xF7BF6F), color(0xF59D1B), color(0x9E2525), color(0xF97117) };
		
		// Initial node the tree position and angle
		branchR[0] = 270;
		branchX[0] = (int) (width/2);
		branchY[0] = (int) (height * 0.875);
		  
		// Drawing code below
		// ------------------
		
		// Draw BG
		fill(190, 235, 256);
		rect(0,0,width,height);
		  
		// Draw clouds
		fill( clouds );
		noStroke();
		drawCloud( (int) (150*width/800), (int) (200*height/800), (int) (300*width/800));
		drawCloud( (int) (700*width/800), (int) (350*height/800), (int) (200*width/800));
		  
		// Draw ground		  
		fill(100, 200, 100);
		ellipse( (int) (400*width/800), (int) (800*height/800), (int) (1000*width/800), (int) (300*height/800));
		
		// Draw grass behind tree
		noFill();
		stroke(grass);
		for(int i=0; i<width; i++){
            double b2 = height*height*9/256;
            double a2 = width*width*25/64;
            int ycoord = height - (int) Math.sqrt(Math.abs( b2 - (b2*(i-width/2)*(i-width/2))/a2 )) ;
            drawGrass(i, ycoord,  random(5)+3, true);
		}
		  
		// Draw branches
		stroke(0x764F14);		  
		float maxRecursions = recursions;
		for(int i = 0; i<recursions; i++){
			strokeWeight((int)sw);
			sw -= maxRecursions/recursions;
			for(int j = 0; j<branchX.length; j++){
			   if(i > 0){ 
			     if(i % 5 == 0){
			       int num = (int)(random(2)+2);
			       for(int k = 0; k < num; k++){
			         drawBranch(branchX[j], branchY[j], branchR[j], true, false);
			       }
			     } else{
			       drawBranch(branchX[j], branchY[j], branchR[j], false, false);
			     }
			   } else{
			      drawBranch(branchX[j], branchY[j], branchR[j], true, true);
			   }
			 }
			 branchX = tempX;
			 tempX = new float[0];
			 branchY = tempY;
			 tempY = new float[0];
			 branchR = tempR;
			 tempR = new float[0];
		}
		  
		// Draw leafs
		for(int i = 0; i<branchX.length; i++){
			noStroke();
			int pos = random(leafColors.length);
			fill(leafColors[pos]);
			ellipse((int)branchX[i], (int)branchY[i], random(4)+8, random(4)+8);
		}
  	
		// Draw grass in front of the tree
		stroke(grass);
		for(int i = 0; i<width; i+=2){
			noFill();
			drawGrass(i, 720*height/800, 5+random(10), false);
		}
		
		noLoop();	  	  
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.processing.ProcessingContext#keyPressed()
	 */
	public void keyPressed(){
	    branchX = new float[1];
	    branchY = new float[1];
	    tempX = new float[0];
	    tempY = new float[0];
	    redraw();
	}
	
	
	 
	/* (non-Javadoc)
	 * @see de.enough.polish.processing.ProcessingContext#pointerPressed()
	 */
	public void pointerPressed() {
		keyPressed();
	}

	void drawBranch(float x, float y, float r, boolean newBranch, boolean firstBranch){
	  float rotation;
	  float lowerlimit;
	  float upperlimit;
	  int len = (random(8)+3);
	  float ang;
	  if(firstBranch){
	    len *= 1.2;
	    ang = (float) ((r + random(10) - 5) * PI_D / 180);
	    rotation = 0;
	  } else {
	     if(newBranch){
	       len += 2;
	         //angle cannot be less than 170, greater than 370
	         //or deviate past 60 degrees of the parent branch
	         if(r-170 < 60){ lowerlimit = r-170; } else{ lowerlimit = 60; }
	         if(370-r < 60){ upperlimit = 370-r; } else{ upperlimit = 60; }
	     } else{
	         //same as before but with 10 degree deviation
	         if(r-170 < 10){ lowerlimit = r-170; } else{ lowerlimit = 10; }
	         if(370-r < 10){ upperlimit = 370-r; } else{ upperlimit = 10; }
	     }
	     rotation = (float) (random(  (upperlimit + lowerlimit) )-lowerlimit);
	     ang = (float) ((rotation + r) * PI_D / 180);
	  }
	   line((int)x, (int)y, (int)(x+cosd(ang)*len), (int)(y+sind(ang)*len));
	   tempX = append(tempX, (float)(x+cosd(ang)*len));
	   tempY = append(tempY, (float)(y+sind(ang)*len));
	   tempR = append(tempR, (float)(r+rotation));
	}
	
	void drawGrass(float x, float y, float len, boolean direction){
	  float r = (float)(3*PI_D/2);
	  strokeWeight(random(2)+1);
	  if(direction) {
		  r += random(PI_D/10);
	  } else { 
		  r-= random(PI_D/10);
	  }
	  curve( (int)(x+len*(random(1)+1)), (int)(y+len*2), (int)x, (int)y, (int)(x+cosd(r)*len), (int)(y+sind(r)*len),  (int)x, (int)y);		
	}
	
	void drawCloud(float x, float y, float s){
	  for(int i = 0; i < 20; i++){
	    ellipse( (int) (x+random(s)-s/2), (int) (y+random(s/5)-s/10), (int)s, (int)(s*.8));
	  }
	}

}
