//#condition polish.usePolishGui
package de.enough.polish.ui.gaugeviews;

import java.io.IOException;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Color;
import de.enough.polish.ui.Gauge;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemView;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;
import javax.microedition.lcdui.Image;

public class ScanningGaugeView extends ItemView {

	private int width = 5;
	private int height = 5;
	private int number = 4;
	private int selectedColor = 0xFF00FF;
	private int unSelectedColor = 0xFFFFFF;	
	private int valuePosition = 0;
	private long animationInterval = 300;
	private long lastAnimationTime;
	private Image  selectedImage;
	private Image selectedImageRight;
	private Image selectedImageLeft;
	private int step = 1;
	private Image selectedBackgroundImage;
	private Image selectedBackgroundImageRight;
	private Image selectedBackgroundImageLeft;
	
	public boolean animate() {
		boolean animated = super.animate();
		Gauge gauge = (Gauge)this.parentItem;
		if ( gauge.getMaxValue() == Gauge.INDEFINITE && gauge.getValue() == Gauge.CONTINUOUS_RUNNING ) {
			long time = System.currentTimeMillis();
			if (time - this.lastAnimationTime >= this.animationInterval) {
				this.lastAnimationTime = time;
				int position = this.valuePosition + step;
				if (position >= this.number-1) {
					step *= -1;
					//position = this.number +step;
					//position +=step;
					
				}else if (position <= 0) {
					step *= -1;
					//position +=step;
				}
				this.valuePosition = position ; 
				animated = true;				
			}
		}
		return animated;
	}
	
	protected void initContent(Item parent, int firstLineWidth, int availWidth, int availHeight) {
		int imageWidth = 0;
		int imageHeight = 0;
		if(this.selectedImageLeft != null){
			imageWidth += this.selectedImageLeft.getWidth();
			imageHeight = Math.max(imageHeight, this.selectedImageLeft.getHeight());
		}
		if(this.selectedImage != null){
			imageWidth += this.selectedImage.getWidth();
			imageHeight = Math.max(imageHeight, this.selectedImage.getHeight());
		}
		if(this.selectedImageRight != null){
			imageWidth += this.selectedImageRight.getWidth();
			imageHeight = Math.max(imageHeight, this.selectedImageRight.getHeight());
		}
		if(imageWidth>0)
		{
			this.width = imageWidth;
		}
		if(imageHeight>0)
		{
			this.height = imageHeight;
		}
		this.contentWidth = (this.width + this.paddingHorizontal) * this.number - (this.paddingHorizontal);
		this.contentHeight = this.height ;
		
		if(this.contentWidth>availWidth){
			this.contentWidth = availWidth;
			this.width = availWidth / this.number;
		}
		//this.step = this.contentWidth /this.number;
		//System.out.println("Step: "+step);
		
		System.out.println("--------- Available Height:"+parent.itemHeight);
	}

	
	protected void paintContent(Item parent, int x, int y, int leftBorder, int rightBorder, Graphics g) {
		
		
		Gauge gauge = (Gauge)parent;
//		int centerX = x + parent.itemWidth / 2;
//		int centerY = y + parent.itemHeight / 2;
		int position = this.valuePosition;
		
// 		System.out.println("Width"+this.width+"Center:"+centerX+"LeftBorder:"+leftBorder+ " RightBorder:"+rightBorder);
		
		if (gauge.getMaxValue() != Gauge.INDEFINITE) {
			int valuePercent = (gauge.getValue() * 100 ) / gauge.getMaxValue();
			position = ((valuePercent*this.number)/100);
		}
		//Background drawing
		int backRight = rightBorder;
		
		int backX = leftBorder;
		
		if(this.selectedBackgroundImageLeft != null){
			g.drawImage(this.selectedBackgroundImageLeft, backX , y, Graphics.TOP | Graphics.LEFT);
			backX+=this.selectedBackgroundImageLeft.getWidth();
		}
		
		if(this.selectedBackgroundImageRight != null){
			g.drawImage(this.selectedBackgroundImageRight, rightBorder-this.selectedBackgroundImageRight.getWidth(), y, Graphics.TOP | Graphics.LEFT);
			backRight -= this.selectedBackgroundImageRight.getWidth();
		}
		if(this.selectedBackgroundImage != null){
			while (backX<backRight) {
				g.drawImage(this.selectedBackgroundImage, backX , y, Graphics.TOP | Graphics.LEFT);
				backX+=this.selectedBackgroundImage.getWidth();
			}
		}
		
		
		
		
		int imageWidth = 0;
		
		if(this.selectedImageLeft != null){
			imageWidth += this.selectedImageLeft.getWidth();
		}
		if(this.selectedImage != null){
			imageWidth += this.selectedImage.getWidth();
		}
		if(this.selectedImageRight != null){
			imageWidth += this.selectedImageRight.getWidth();
		}
		
		int pixelStep = (this.contentWidth-imageWidth) / (this.number-1);
		//pixelStep = (rightBorder-leftBorder)-pixelStep;
		
		System.out.println("PixelStep: "+pixelStep+"ImageWidth: "+imageWidth +" Position: "+position);
		for(int i = 0; i < this.number; i++){
			int newX = x + (leftBorder);
			if(i == position ){
				int imageOffset = 0;
				if(this.selectedImageLeft != null){
					imageOffset = this.selectedImageLeft.getWidth();
					g.drawImage(this.selectedImageLeft, newX + (pixelStep*i) , y, Graphics.TOP | Graphics.LEFT);
				}
				if(this.selectedImage != null){
					g.drawImage(this.selectedImage, newX +(pixelStep*i) +imageOffset , y, Graphics.TOP | Graphics.LEFT);
					imageOffset += this.selectedImage.getWidth();
				}else{
					g.setColor(this.selectedColor);
					g.fillRect(newX + (this.width*i) , y, this.width, this.height);					
				}
				if(this.selectedImageRight != null){
					
					g.drawImage(this.selectedImageRight, newX +(pixelStep*i)+imageOffset, y, Graphics.TOP | Graphics.LEFT);
				}
			}else{
				g.setColor(this.unSelectedColor);
			}
			
			
		}
//		g.setColor(0xFF0000);
//		g.drawString(""+gauge.getValue(), x + this.width * this.number  ,  y + this.height, 0);
//		g.drawString(""+position, x + this.width * this.number +20 ,  y + this.height * 2, 0);
	}

	
	protected void setStyle(Style style) {
		super.setStyle(style);
		//#if polish.css.gauge-scanline-number
			Integer number = style.getIntProperty("gauge-scanline-number");
			if (number != null) {
				this.number = number.intValue();
			}
		//#endif
		//#if polish.css.gauge-scanline-width
			Integer width = style.getIntProperty("gauge-scanline-width");
			if (width != null) {
				this.width = width.intValue();
			}
		//#endif
		//#if polish.css.gauge-scanline-height
			Integer height = style.getIntProperty("gauge-scanline-height");
			if (height != null) {
				this.height = height.intValue();
			}
		//#endif
		//#if polish.css.gauge-scanline-selectedcolor
			Color selectedColor = style.getColorProperty("gauge-scanline-selectedcolor");
			if (selectedColor != null) {
				this.selectedColor = selectedColor.getColor();
			}
		//#endif
		//#if polish.css.gauge-scanline-unselectedcolor
			Color unSelectedColor = style.getColorProperty("gauge-scanline-unselectedcolor");
			if (unSelectedColor != null) {
				this.unSelectedColor = unSelectedColor.getColor();
			}
		//#endif
			
			//#if polish.css.gauge-scanline-image-left
			String imageURL = style.getProperty("gauge-scanline-image-left");
			if (this.selectedImageLeft == null) {
				try {
					this.selectedImageLeft = Image.createImage(imageURL);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//#endif
			
			//#if polish.css.gauge-scanline-image-right
			String imageRightURL = style.getProperty("gauge-scanline-image-right");
			if (this.selectedImageRight == null) {
				try {
					this.selectedImageRight = Image.createImage(imageRightURL);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//#endif
			
			//#if polish.css.gauge-scanline-image-middle
			String imageMiddleURL = style.getProperty("gauge-scanline-image-middle");
			if (this.selectedImage == null) {
				try {
					this.selectedImage = Image.createImage(imageMiddleURL);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println(">>>Image not found for css");
					e.printStackTrace();
				}
			}
			//#endif
			//#if polish.css.gauge-scanline-background-image-left
			String imageURLBack = style.getProperty("gauge-scanline-background-image-left");
			if (this.selectedBackgroundImageLeft == null) {
				try {
					this.selectedBackgroundImageLeft = Image.createImage(imageURLBack);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//#endif
			
			//#if polish.css.gauge-scanline-background-image-right
			String imageRightURLBack = style.getProperty("gauge-scanline-background-image-right");
			if (this.selectedBackgroundImageRight == null) {
				try {
					this.selectedBackgroundImageRight = Image.createImage(imageRightURLBack);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//#endif
			
			//#if polish.css.gauge-scanline-background-image-middle
			String imageMiddleURLBack = style.getProperty("gauge-scanline-background-image-middle");
			if (this.selectedBackgroundImage == null) {
				try {
					this.selectedBackgroundImage = Image.createImage(imageMiddleURLBack);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println(">>>Image not found for css");
					e.printStackTrace();
				}
			}
			//#endif		
	}

	
	/**
	 * Determines whether this view is valid for the given item.
	 * @return true when this view can be applied
	 */
	protected boolean isValid(Item parent, Style style) {
		return parent instanceof Gauge;
	}
}
