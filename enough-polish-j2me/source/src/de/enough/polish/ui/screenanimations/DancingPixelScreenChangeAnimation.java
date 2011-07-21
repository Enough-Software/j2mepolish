//#condition polish.usePolishGui && polish.midp2

package de.enough.polish.ui.screenanimations;

import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.ui.ScreenChangeAnimation;
import de.enough.polish.ui.Style;

public class DancingPixelScreenChangeAnimation extends ScreenChangeAnimation {
		private boolean stillRun = true;
		//the start degrees of the images
		//the nxtImage to start in screen
		private int row ,id;
		private int[] left ,right ,up,down;
		//the rgb - images
		private int[] rgbbuffer ;
		//the height of the columns
		private int[] scaleableHeight;
		private int[] scaleableWidth;
	 	//the scale from the row
//		//kann nachher weg nur zum testen
//		private boolean first = true;
		public DancingPixelScreenChangeAnimation() {
			super();
		}
		
		protected void onShow(Style style, Display dsplay, int width, int height,
				Displayable lstDisplayable, Displayable nxtDisplayable, boolean isForward  ) 
		{
				System.gc();
				this.id = 20;
				this.row = 0;
				this.stillRun = true;
				int size = width * height;
				this.left = new int [height];
				this.right = new int [height];
				this.scaleableWidth = new int [height];
				for(int i = 0; i < this.scaleableWidth.length;i++){
					this.scaleableWidth[i] = width;
					this.left[i] = 0;
					this.right[i] = width;
				}
				this.up = new int [width];
				this.down = new int [width];
				this.scaleableHeight = new int [width];
				for(int i = 0;i < this.scaleableHeight.length;i++){
					this.scaleableHeight[i] = height;
					this.up[i] = 0;
					this.down[i] = height;
				}
				super.onShow(style, dsplay, width, height, lstDisplayable, nxtDisplayable, isForward );
				this.rgbbuffer = new int[size];
				this.nextCanvasImage.getRGB(this.rgbbuffer, 0, width, 0, 0, width, height );
		}
		
		
		
		protected boolean animate() {
			this.cubeEffect();
			int length = this.nextCanvasRgb.length-1;
			int sH,c,scalePercentH,scalePercentWidth,r,newI,sW = 0,left = 0,right = this.screenWidth;
			for(int y = 0; y < this.screenHeight;y++){
					left = this.left[y];
					right = this.right[y];
					sW = this.scaleableWidth[y];
					scalePercentWidth = ((sW*100) / this.screenWidth);
					int column = y*this.screenWidth;
					for(int x = 1; x < this.screenWidth;x++){
//						System.out.print(y*x+"\n");
						sH = this.scaleableHeight[x];
						
						if(left > x || right < x || this.down[x] < y || this.up[x] > y){
							this.nextCanvasRgb[column+x] = this.rgbbuffer[column+x];
						}
						else{
							c = y - (this.screenHeight - sH);
							if(c < 1)c++;
							scalePercentH = (((this.screenHeight-((this.screenHeight-sH)))*100)/this.screenHeight);
							this.row = left + ((this.screenWidth - right)/this.screenWidth);
							if(this.row <= x){
								r = x - this.row;
								scalePercentWidth = (sW*100) / this.screenWidth;
							}else{
								r = x;
								scalePercentWidth = (this.row*100) / this.screenWidth;
							}
							
//							if(r < 1)r++;
//							if(sW < 1)sW++;
							scalePercentWidth = (((this.screenWidth-((this.screenWidth-sW)))*100)/this.screenWidth);
							if(scalePercentWidth < 1)scalePercentWidth++;
							if(scalePercentH < 1)scalePercentH++;
							newI = ((r*100)/scalePercentWidth)+(this.screenWidth * ((c*100)/scalePercentH));
							if(newI >= length)newI = length;
							if(newI < 0)newI = 0;

							this.nextCanvasRgb[x+column] = this.lastCanvasRgb[newI];
						}	
					}
					

			}
			
			if(this.scaleableHeight[this.scaleableHeight.length-1] <= 0)this.stillRun = false;
			return this.stillRun;
		}
		
		
		private void cubeEffect(){		
			int id = 0;
			for(int i = 0; i < this.id;i++){
				id = (id + 1) % 20; 
				if(this.scaleableHeight[i] > 0 && id == 0){
					this.up[i]+=20;
				}	
				else if(this.scaleableHeight[i] > 0 ){
					this.scaleableHeight[i]-=20;
					this.down[i]-=20;
				}
			}

			this.id+=20;
			if(this.id > this.screenWidth)this.id = this.scaleableHeight.length;
		}
		
		public void handleKeyPressed(int keyCode, Image next) {
			next.getRGB( this.rgbbuffer, 0, this.screenWidth, 0, 0, this.screenWidth, this.screenHeight );
		}

		//#if polish.hasPointerEvents
		public void pointerPressed(int x, int y) {
			super.pointerPressed(x, y);
			this.nextCanvasImage.getRGB( this.rgbbuffer, 0, this.screenWidth, 0, 0, this.screenWidth, this.screenHeight );
		}
		//#endif
		
		public void paintAnimation(Graphics g) {
			g.drawRGB(this.nextCanvasRgb,0,this.screenWidth,0,0,this.screenWidth,this.screenHeight,false);
		}

	}
