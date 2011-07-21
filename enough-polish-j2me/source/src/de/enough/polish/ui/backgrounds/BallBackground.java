//#condition polish.usePolishGui

package de.enough.polish.ui.backgrounds;

import java.util.Random;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import de.enough.polish.ui.Background;


public class BallBackground extends Background {
	private transient int[][] directionArray = {{1,5,8},{3,7,8}, {2,5,6}, {4,6,7}};
	private int color, borderColor, roundWidth, roundHeight, number, width, height;
	private transient Random random;
	private boolean gameover;
	private int[] changeX, changeY, detective, direction;
	//private final String imageURL;
	private transient Sprite[] sprite;
//	x--{1,5,8}, y--{2,5,6}, y++{3,7,8}, x++{4,6,7}
	
	public BallBackground() {
		this.random = new Random();
	}
	public BallBackground(int color, int borderColor,String url,int roundWidth,int roundHeight,int number) {
		super();
		this.color = color;
		this.borderColor = borderColor;
		this.roundWidth = roundWidth;
		this.roundHeight = roundHeight;
		this.number = number;
		this.sprite  = new Sprite [number];
		this.changeX = new int[number];
		this.changeY = new int[number];
		this.detective = new int[number];
		this.direction = new int[number];
		this.random = new Random();
		Sprite s = null;
		int a = 0;
		try {
			Image image = Image.createImage(url);
			s = new Sprite(image, this.roundWidth, this.roundHeight);
			a = (image.getHeight() / this.roundHeight) + (image.getWidth() / this.roundWidth);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i = 0;i < this.number;i++) {
			this.sprite[i] = new Sprite( s );
			this.changeX[i] = i*2;
			this.changeY[i] = i;
			this.detective[i] = 0;
			this.direction[i] = 2;
			this.sprite[i].setFrame(i % a);
		}
	}
	
	public void changeDirection(int id){
			do {
				this.direction[id] = Math.abs( this.random.nextInt() ) % 9;
			} while( this.direction[id] < 1 ||  this.direction[id] != this.directionArray[this.detective[id]][0] && this.direction[id] != this.directionArray[this.detective[id]][1] && this.direction[id] != this.directionArray[this.detective[id]][2]);
	}
	
	public void paint(int x, int y, int width, int height, Graphics g) {
		this.width = width;
		this.height = height;	
		g.setColor( this.color );
		g.fillRect( x, y, width, height );
		g.setColor( this.borderColor);
		g.drawRect(x,y,width,height);
		for(int i =0;i < this.number;i++) {
			this.sprite[i].setPosition( this.changeX[i]+x,this.changeY[i]+y);
			this.sprite[i].paint(g);
		}
	}

	private void outbounds(){
//		x--{1,5,8}, y--{2,5,6}, y++{3,7,8}, x++{4,6,7}	
		for(int i =0;i < this.number;i++) {
			if(this.changeX[i] <= 0){
				this.detective[i] = 3;
				changeDirection(i);
			}
			else if((this.changeX[i] + (this.roundWidth)) >= this.width ){
				this.detective[i] = 0;
				changeDirection(i);
			}
			else if(this.changeY[i] <= 0){
				this.detective[i] = 1;
				changeDirection(i);
			}
			else if((this.changeY[i] + (this.roundHeight)) >= this.height){
				this.detective[i] = 2;
				changeDirection(i);
			}
		}
	}
	
	public boolean animate() {
		// TODO Auto-generated method stub
		outbounds();
		//		x--{1,5,8}, y--{2,5,6}, y++{3,7,8}, x++{4,6,7}
		for(int i =0;i < this.number;i++) {
			switch(this.direction[i]) {
				case 1:
					this.changeX[i]--;
					this.sprite[i].setTransform(Sprite.TRANS_MIRROR);
					break;
				case 2:
					this.changeY[i]--;
					this.sprite[i].setTransform(Sprite.TRANS_MIRROR_ROT90);
					break; 
				case 3:
					this.changeY[i]++;
					this.sprite[i].setTransform(Sprite.TRANS_ROT90);
					break;
				case 4:
					this.changeX[i]++;
					this.sprite[i].setTransform(Sprite.TRANS_NONE);
					break;
				case 5:
					this.changeX[i]--;
					this.changeY[i]--;this.sprite[i].setTransform(Sprite.TRANS_MIRROR_ROT90);
					break;
				case 6:
					this.changeY[i]--;
					this.changeX[i]++;this.sprite[i].setTransform(Sprite.TRANS_MIRROR_ROT90);
					break;
				case 7:
					this.changeY[i]++;
					this.changeX[i]++;this.sprite[i].setTransform(Sprite.TRANS_ROT90);
					break;
				case 8:
					this.changeX[i]--;
					this.changeY[i]++;
					this.sprite[i].setTransform(Sprite.TRANS_ROT90);
					break;
			}

			this.sprite[i].nextFrame();
		}

		return !this.gameover;
	}
}
