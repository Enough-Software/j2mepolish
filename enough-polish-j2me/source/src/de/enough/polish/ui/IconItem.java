//#condition polish.usePolishGui
/*
 * Created on 04-Apr-2004 at 21:30:32.
 *
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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
package de.enough.polish.ui;

import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.util.DrawUtil;
import de.enough.polish.util.ImageUtil;
import de.enough.polish.util.RgbImage;

/**
 * <p>Shows a string with an optional image attached to it.</p>
 * <p>The dynamic CSS selector of the IconItem is "icon".</p>
 * <p>
 * Following CSS attributes can be set:
 * <ul>
 * 		<li><b>icon-image-align</b>: The position of the icon-image relative to the text,
 * 				either "right", "left", "top" or "bottom". Default is "left".</li>
 * 		<li><b>icon-image</b>: The name of the image for this icon. The name can
 * 			include the index of this item relative to the parent-container:
 * 		    The icon-image "%INDEX%icon.png" would be renamed to "0icon.png" when
 * 			this icon would be the first one in a list.</li>
 * 		<li><b></b>: </li>
 * </ul>
 * </p>
 * 
 * <p>Copyright Enough Software 2004 - 2009</p>

 * <pre>
 * history
 *        04-Apr-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class IconItem extends StringItem 
//#ifdef polish.images.backgroundLoad
implements ImageConsumer
//#endif
{
	
	protected Image image;
	protected int imageAlign = Graphics.LEFT;
	//#ifdef polish.css.icon-image-align-next
		private boolean imageAlignNext;
	//#endif
	protected int imageHeight;
	protected int imageWidth;
	protected int yAdjustText;
	protected boolean isTextVisible = true;
	//#if polish.midp2 && polish.css.scale-factor
		private int scaleFactor;
		private int scaleSteps = -1;
		private int currentStep;
		private int[] rgbData;
		private boolean scaleDown;
		protected boolean scaleFinished;
		// a short array, so that we don't need to use synchronization.
		// scaleData[0] = scaled RGB Data
		// scaleData[1] = width of RGB Data 
		protected Object[] scaleData;
	//#endif
	//#if polish.css.icon-vertical-adjustment
		protected int verticalAdjustment;
	//#endif
	//#if polish.css.icon-horizontal-adjustment
		protected int horizontalAdjustment;
	//#endif
	//#if polish.css.icon-padding
		private int paddingIcon;
	//#endif

	//private int yAdjustImage;
	protected int relativeIconX;
	protected int relativeIconY;
	private RgbFilter[] iconFilters;
	//#if polish.css.icon-filter && polish.midp2
		private boolean isIconFiltersActive;
		private RgbImage iconFilterRgbImage;
		private RgbImage iconFilterProcessedRgbImage;
		private RgbFilter[] originalIconFilters;
		private int iconFilterLayout;
	//#endif
	private Image imageHover;
	private Image imageNormal;
	
	/**
	 * Creates a new icon.
	 * 
	 * @param text the text of this item
	 * @param image the image of this item, null when no image should be displayed
	 */
	public IconItem( String text, Image image ) {
		this( null, text, image, null );
	}
	
	/**
	 * Creates a new icon.
	 * 
	 * @param text the text of this item
	 * @param image the image of this item, null when no image should be displayed
	 * @param style the style of this item
	 */
	public IconItem( String text, Image image, Style style) {
		this( null, text, image, style );
	}
	
	/**
	 * Creates a new icon.
	 * 
	 * @param label the label of this item
	 * @param text the text of this item
	 * @param image the image of this item, null when no image should be displayed
	 */
	public IconItem( String label, String text, Image image) {
		this( label, text, image, null );
	}

	/**
	 * Creates a new icon.
	 * 
	 * @param label the label of this item
	 * @param text the text of this item
	 * @param image the image of this item, null when no image should be displayed
	 * @param style the style of this item
	 */
	public IconItem( String label, String text, Image image, Style style) {
		super(label, text, Item.INTERACTIVE, style);
		if (image != null) {
			setImage( image );
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#createCssSelector()
	 */
	protected String createCssSelector() {
		return "icon";
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#initItem()
	 */
	protected void initContent(int firstLineWidth, int availWidth, int availHeight) {
		//#debug
		System.out.println("IconItem " + this + ".initContent(" + firstLineWidth + ", " + availWidth + ")");
		if (this.image == null) {
			if (this.isTextVisible) {
				super.initContent(firstLineWidth, availWidth, availHeight);
				this.yAdjustText = 0;
			} else {
				this.contentWidth = 0;
				this.contentHeight = 0;
			}
			return;
		}
		int imgWidth = this.image.getWidth();
		int imgHeight = this.image.getHeight();
		this.imageWidth = imgWidth;
		this.imageHeight = imgHeight;
		int yAdjustImage = 0;
		boolean hasText = (getText() != null);
		if (this.imageAlign == Graphics.LEFT || this.imageAlign == Graphics.RIGHT ) {
			if (this.isTextVisible && hasText) {
				imgWidth += this.paddingHorizontal;
				this.imageWidth = imgWidth;
				firstLineWidth -= imgWidth;
				availWidth -= imgWidth;
				if (availWidth <= 0) {
					availWidth += imgWidth;
				}
				super.initContent(firstLineWidth, availWidth, availHeight);
			} else {
				this.contentWidth = 0;
				this.contentHeight = imgHeight;
			}
			// this.originalContentWidth = this.contentWidth;
			if (imgHeight > this.contentHeight) {
				int verticalAlign = this.layout & LAYOUT_VCENTER;
				if ( verticalAlign == LAYOUT_VCENTER || verticalAlign == 0 ) {
					this.yAdjustText = (imgHeight - this.contentHeight) / 2;
				} else if ( verticalAlign == LAYOUT_BOTTOM ) {
					this.yAdjustText = imgHeight - this.contentHeight;
				} else {
					// top layout:
					this.yAdjustText = 0;
				}
				this.contentHeight = imgHeight;
			} else if (this.contentHeight > imgHeight) {
				this.yAdjustText = 0;
				int verticalAlign = this.layout & LAYOUT_VCENTER;
				if ( verticalAlign == LAYOUT_VCENTER || verticalAlign == 0 ) {
					yAdjustImage = (this.contentHeight - imgHeight) / 2;
				} else if ( verticalAlign == LAYOUT_BOTTOM ) {
					yAdjustImage = (this.contentHeight - imgHeight);
				}	
			} else {
				this.yAdjustText = 0;
			}
			if (this.isLayoutExpand // && (this.imageAlign == Graphics.RIGHT || this.imageAlign == Graphics.LEFT) 
				//#ifdef polish.css.icon-image-align-next
					&& !this.imageAlignNext 
				//#endif
				&& hasText
			) {
				this.contentWidth = firstLineWidth;
			}
			this.contentWidth += imgWidth;
		} else { // image align is top or bottom:
			if (this.isTextVisible && hasText) {
				//#if polish.css.icon-padding
					if (this.paddingIcon == 0) {
						this.paddingIcon = this.paddingVertical;
					}
					imgHeight += this.paddingIcon;
				//#else
					imgHeight += this.paddingVertical;
				//#endif
				this.imageHeight = imgHeight;
				super.initContent(firstLineWidth, availWidth, availHeight);
			} else {
				this.contentHeight = 0;
				this.contentWidth = 0;
			}
			this.contentHeight += imgHeight;   
			if (imgWidth > this.contentWidth) {
				this.contentWidth = imgWidth;
			}
		} 
		
		// calculate icon positions:
		int iconLeftX = 0;
		int iconTopY = yAdjustImage;
		if (hasText && this.imageAlign == Graphics.RIGHT ) {
			iconLeftX = this.contentWidth - imgWidth + this.paddingHorizontal;
		} else if (hasText && this.imageAlign == Graphics.LEFT) {
			iconLeftX = 0;
		} else { // image align is top or bottom:
			iconLeftX = (this.contentWidth - imgWidth) >> 1;
			if (this.imageAlign == Graphics.BOTTOM ){
				iconTopY += this.contentHeight - imgHeight;
			}
		}
		this.relativeIconX = iconLeftX;
		this.relativeIconY = iconTopY;
		//#if polish.midp2 && polish.css.scale-factor
			if (this.scaleSteps == -1 && this.scaleFactor != 0 && this.scaleFactor != 100) {
				// use a clean values without padding (left/right - top/bottom image align):
				imgWidth = this.image.getWidth();
				imgHeight = this.image.getHeight();
				if (this.rgbData == null) {
					this.rgbData = new int[ imgWidth * imgHeight ];
					this.image.getRGB(this.rgbData, 0, imgWidth, 0, 0, imgWidth, imgHeight );
				}
				int scaleWidth = imgWidth + ((imgWidth * this.scaleFactor) / 100);
				int scaleHeight = imgHeight + ((imgHeight * this.scaleFactor) / 100);
				//System.out.println("\nstep=" + step + ", scaleSteps=" + this.scaleSteps + "\nscaleWidth=" + this.scaleWidth + ", scaleHeight=" + this.scaleHeight + ", imgWidth=" + imgWidth + ", imgHeight=" + imgHeight + "\n");
				int[] scaledRgbData = ImageUtil.scale( scaleWidth, scaleHeight, imgWidth, 
						imgWidth, imgHeight, this.rgbData);
				this.scaleData = new Object[]{ scaledRgbData, new Integer( scaleWidth ) };				
			}
		//#endif
	} 
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Item#setContentWidth(int)
	 */
	protected void setContentWidth( int width ) {
		if (this.imageAlign == Graphics.TOP || this.imageAlign == Graphics.BOTTOM || this.text == null) {
			int diff = width - this.contentWidth;
			this.relativeIconX += diff/2;
		} else if (this.imageAlign == Graphics.RIGHT) {
			this.relativeIconX = width - this.imageWidth;
		}
		super.setContentWidth(width);
	}
		
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paintContent(int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		if (this.image != null) {
			//#if polish.css.icon-horizontal-adjustment
				x += this.horizontalAdjustment;
			//#endif
			//#if polish.css.icon-vertical-adjustment
				y += this.verticalAdjustment;
			//#endif
			//#if polish.midp2 && polish.css.scale-factor
				Object[] localeScaleData = this.scaleData;
				boolean useScaledImage = 
						(this.scaleSteps != -1 && this.isFocused && (localeScaleData != null) && !this.scaleFinished)
						|| (this.scaleSteps == -1 && localeScaleData != null && this.scaleFactor != 0);
				if (useScaledImage) {
					int[] sData = (int[]) localeScaleData[0];
					int sWidth = ((Integer) localeScaleData[1]).intValue();
					int sHeight = sData.length / sWidth;
					int scaleX = x + this.relativeIconX + ((this.imageWidth - sWidth) >> 1);
					int scaleY = y + this.relativeIconY + ((this.imageHeight - sHeight) >> 1);
					DrawUtil.drawRgb(sData, scaleX, scaleY, sWidth, sHeight, true, g );
				} else {
			//#endif
				//#if polish.css.icon-filter && polish.midp2
					if (this.isIconFiltersActive && this.iconFilters != null) {
						RgbImage rgbImage = this.iconFilterRgbImage;
						if ( rgbImage == null) {
							rgbImage = new RgbImage(this.image, true);
							this.iconFilterRgbImage = rgbImage;
						} 
						int lo = this.layout;
						//#if polish.css.icon-filter-layout
							lo = this.iconFilterLayout;
						//#endif
						this.iconFilterProcessedRgbImage = paintFilter( x + this.relativeIconX, y + this.relativeIconY, this.iconFilters, rgbImage, lo, g );
					} else {
				//#endif
						g.drawImage( this.image, x + this.relativeIconX, y + this.relativeIconY, Graphics.TOP | Graphics.LEFT );
				//#if polish.css.icon-filter && polish.midp2
					}
				//#endif
			//#if polish.midp2 && polish.css.scale-factor
				}
			//#endif
			if (this.imageAlign == Graphics.LEFT ) {
				x += this.imageWidth;
				leftBorder += this.imageWidth;
				y += this.yAdjustText;
			} else if (this.imageAlign == Graphics.RIGHT ) {
				rightBorder -= this.imageWidth;
				y += this.yAdjustText;
			} else if (this.imageAlign == Graphics.TOP ) {
				y += this.imageHeight;
			}
		
			//#if polish.css.icon-horizontal-adjustment
				x -= this.horizontalAdjustment;
			//#endif
			//#if polish.css.icon-vertical-adjustment
				y -= this.verticalAdjustment;
			//#endif
		}
		if (this.isTextVisible) {
			super.paintContent(x, y, leftBorder, rightBorder, g);
		}
	}
	
	/**
	 * Retrieves the image of this item.
	 * 
	 * @return the image of this icon.
	 */
	public Image getImage() {
		return this.image;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		// call super.setStyle(style) at the end, so that setStyle(style, boolean) is called after initializing icon-filters,
		// if there are any...
		
		//#ifdef polish.css.icon-image-align
			Integer align = style.getIntProperty("icon-image-align");
			if (align == null) {
				// keep align setting
			} else {
				switch (align.intValue()) {
					case 0: this.imageAlign = Graphics.LEFT; break; 
					case 1: this.imageAlign = Graphics.RIGHT; break; 
					case 2: this.imageAlign = Graphics.TOP; break; 
					case 3: this.imageAlign = Graphics.BOTTOM; break; 
					case 4: this.imageAlign = Graphics.HCENTER | Graphics.VCENTER; break; 
				}
			}
		//#endif
		//#ifdef polish.css.icon-image-align-next
			Boolean alignExpandBool = style.getBooleanProperty("icon-image-align-next");
			if (alignExpandBool != null) {
				this.imageAlignNext = alignExpandBool.booleanValue();
			}
		//#endif
		//#ifdef polish.css.icon-image
			String imageName = style.getProperty("icon-image");
			if (imageName != null) {
				Item item = this;
				Item container = this.parent;
				while ((container != null) 
						&& !(container instanceof Container) 
						&& (container.parent != null)) 
				{
					item = container;
					container = container.parent;
				}
				if (container instanceof Container) {
					imageName = ((Container) container).parseIndexUrl( imageName, item );
				}
				//#if polish.debug.error
					else if ( imageName.indexOf( "%INDEX%") != -1) {
						throw new IllegalStateException("IconItem cannot resolve %INDEX% in url since parent is not a container: " + container + ", parent=" + this.parent);
					}
				//#endif
				try {
					Image img = StyleSheet.getImage(imageName, this, true);
					if (img != null) {
						this.image = img;
						//#if polish.midp2 && polish.css.scale-factor
							this.rgbData = null;
							this.scaleData = null;
						//#endif
					}
				} catch (IOException e) {
					//#debug error
					System.out.println("unable to load image [" + imageName + "]" + e);
				}
			}
		//#endif

		//#if polish.midp2	
			//#ifdef polish.css.scale-steps
				Integer scaleStepsInt = style.getIntProperty( "scale-steps" );
				if (scaleStepsInt != null) {
					this.scaleSteps = scaleStepsInt.intValue();
				}
			//#endif
			//#if polish.css.icon-filter
				RgbFilter[] filterObjects = (RgbFilter[]) style.getObjectProperty("icon-filter");
				if (filterObjects != null) {
					if (filterObjects != this.originalIconFilters) {
						this.iconFilters = new RgbFilter[ filterObjects.length ];
						for (int i = 0; i < filterObjects.length; i++)
						{
							RgbFilter rgbFilter = filterObjects[i];
							try
							{
								this.iconFilters[i] = (RgbFilter) rgbFilter.getClass().newInstance();
							} catch (Exception e)
							{
								//#debug warn
								System.out.println("Unable to initialize filter class " + rgbFilter.getClass().getName() + e );
							}
						}
						this.originalIconFilters = filterObjects;
					}
				} else if (this.iconFilterRgbImage != null) {
					this.originalIconFilters = null;
					this.iconFilters = null;
					this.iconFilterRgbImage = null;
				}
				//#if polish.css.icon-filter-layout
					Integer iconFilterLayoutInt = style.getIntProperty("icon-filter-layout");
					if (iconFilterLayoutInt != null) {
						this.iconFilterLayout = iconFilterLayoutInt.intValue();
					}
				//#endif
			//#endif
		//#endif	
		//#if polish.css.icon-inactive
			Boolean inactiveBool = style.getBooleanProperty("icon-inactive");
			if (inactiveBool != null) {
				if (inactiveBool.booleanValue()) {
					this.appearanceMode = Item.PLAIN;
				} else {
					this.appearanceMode = Item.INTERACTIVE;								
				}
			}
		//#endif
		super.setStyle(style);
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.StringItem#setStyle(de.enough.polish.ui.Style, boolean)
	 */
	public void setStyle(Style style, boolean resetStyle)
	{
		super.setStyle(style, resetStyle);
		//#if polish.css.icon-padding
			Integer paddingIconInt = style.getIntProperty("icon-padding");
			if (paddingIconInt != null) {
				this.paddingIcon = paddingIconInt.intValue();
			} else if (this.paddingIcon == 0) {
				this.paddingIcon = this.paddingVertical;
			}
		//#endif
		//#if polish.css.icon-vertical-adjustment
			Integer verticalAdjustmentInt = style.getIntProperty("icon-vertical-adjustment");
			if (verticalAdjustmentInt != null) {
				this.verticalAdjustment = verticalAdjustmentInt.intValue();
			}
		//#endif
		//#if polish.css.icon-horizontal-adjustment
			Integer horizontalAdjustmentInt = style.getIntProperty("icon-horizontal-adjustment");
			if (horizontalAdjustmentInt != null) {
				this.horizontalAdjustment = horizontalAdjustmentInt.intValue();
			}
		//#endif
		//#if polish.midp2	
			//#ifdef polish.css.scale-factor
				Integer scaleFactorInt = style.getIntProperty( "scale-factor" );
				if (scaleFactorInt != null) {
					this.scaleFactor = scaleFactorInt.intValue();
					setInitialized(false);
				}
			//#endif
				
			//#if polish.css.icon-filter
				if (this.iconFilters != null) {
					boolean isActive = false;
					for (int i=0; i<this.iconFilters.length; i++) {
						RgbFilter filter = this.iconFilters[i];
						filter.setStyle(style, resetStyle);
						isActive |= filter.isActive();
					}
					this.isIconFiltersActive = isActive;
					this.iconFilterRgbImage = null;
				}
			//#endif
		//#endif
	}

	/**
	 * Loads the specified image.
	 * 
	 * @param url the local URL of the image
	 */
	public void setImage( String url ) {
		try {
			Image img = StyleSheet.getImage(url, this, false);
			if (img != null) {
				setImage( img );
			}
		} catch (IOException e) {
			//#debug error
			System.out.println("unable to load image [" + url + "]" + e);
		}		
	}

	//#ifdef polish.images.backgroundLoad
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ImageConsumer#setImage(java.lang.String, javax.microedition.lcdui.Image)
	 */
	public void setImage(String name, Image image) {
		this.image = image;
		//System.out.println("image [" + name + "] has been set.");
		if (isInitialized()) {
			setInitialized(false);
			repaint();
		}
	}
	//#endif
	
	
	/**
	 * Sets the image for this icon.
	 * 
	 * @param image the image for this icon, when null is given, no image is painted.
	 */
	public void setImage( Image image ) {
		setImage( image, null );
	}
	
	/**
	 * Sets the image for this icon.
	 * 
	 * @param img the image for this icon, when null is given, no image is painted.
	 * @param style the new style of this item, is ignored when null
	 */
	public void setImage(Image img, Style style) {
		if (img == this.image && (style == null || style == this.style)) {
			return;
		}
		if (style != null) {
			setStyle( style );
		}
		this.image = img;
		//#if polish.midp2 && polish.css.scale-factor
			this.rgbData = null;
			this.scaleData = null;
		//#endif
		
		//no initialization if the style is null and the image dimensions haven't changed
		if (isInitialized()
			//#if !polish.css.scale-factor
			&& (img != null && (style != null || 
				this.imageWidth != img.getWidth() || 
				this.imageHeight != img.getHeight()))
			//#endif
			) {
			requestInit();
		}
		else if (this.isShown)
		{
			repaint();
		}
	}

	/**
	 * Sets the image for this icon when it is focused (:hover).
	 * 
	 * @param img the image for this focused icon, when null is given, the default image is painted.
	 */
	public void setHoverImage(Image img) {
		this.imageHover = img;
		if (this.isFocused && img != null) {
			this.imageNormal = this.image;
			setImage( img, null );
		}
	}
	
	
	/**
	 * Sets the image align for this icon.
	 * 
	 * @param imageAlign either Graphics.TOP, Graphics.LEFT, Graphics.BOTTOM or Graphics.RIGHT
	 */
	public void setImageAlign( int imageAlign ) {
		this.imageAlign = imageAlign;
		setInitialized(false);
	}
	
	
	//#if polish.css.icon-filter
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#addRepaintArea(de.enough.polish.ui.ClippingRegion)
	 */
	public void addRepaintArea(ClippingRegion repaintRegion) {
		super.addRepaintArea(repaintRegion);
		//#if polish.css.icon-filter && polish.midp2
		RgbImage img = this.iconFilterProcessedRgbImage;
		if (img != null && (img.getHeight() > this.imageHeight || img.getWidth() > this.imageWidth)) {
			int lo = this.layout;
			//#if polish.css.icon-filter-layout
				lo = this.iconFilterLayout;
			//#endif 
			int w = img.getWidth();
			int h = img.getHeight();
			int horDiff = w - this.imageWidth;
			int verDiff = h - this.imageHeight;
			int absX = getAbsoluteX() + this.relativeIconX;
			int absY = getAbsoluteY() + this.relativeIconY;
			if ((lo & LAYOUT_CENTER) == LAYOUT_CENTER) {
				absX -= horDiff / 2;
			} else if ((lo & LAYOUT_CENTER) == LAYOUT_RIGHT) {
				absX -= horDiff;
			}
			if ((lo & LAYOUT_VCENTER) == LAYOUT_VCENTER) {
				absY -= verDiff / 2; 
			} else if ((lo & LAYOUT_VCENTER) == LAYOUT_TOP) {
				absY -= verDiff; 
			}
			repaintRegion.addRegion( absX, absY, w, h );
		}
		//#endif
	}
	//#endif

	//#if polish.midp2 && polish.css.scale-factor
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#animate(long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(long currentTime, ClippingRegion repaintRegion) {
		super.animate(currentTime, repaintRegion);
		if (this.scaleSteps != -1 && this.scaleFactor != 0) {
			if (this.scaleFinished || this.image == null) {
				return;
			}
			int imgWidth = this.image.getWidth();
			int imgHeight = this.image.getHeight();
			if (this.rgbData == null) {
				this.rgbData = new int[ imgWidth * imgHeight ];
				this.image.getRGB(this.rgbData, 0, imgWidth, 0, 0, imgWidth, imgHeight );
			}
			int step = this.currentStep;
			if (this.scaleDown) {
				step--;
				if (step <= 0) {
					this.scaleFinished =  true;
					this.scaleData = null;
					return;
				}
			} else {
				step++;
				if (step > this.scaleSteps) {
					this.scaleDown = true;
					return;
				}
			}
			this.currentStep = step;
			int scaleWidth = imgWidth + ((imgWidth * this.scaleFactor * step) / (this.scaleSteps * 100));
			int scaleHeight = imgHeight + ((imgHeight * this.scaleFactor * step) / (this.scaleSteps * 100));
			//System.out.println("\nstep=" + step + ", scaleSteps=" + this.scaleSteps + "\nscaleWidth=" + this.scaleWidth + ", scaleHeight=" + this.scaleHeight + ", imgWidth=" + imgWidth + ", imgHeight=" + imgHeight + "\n");
			int[] scaledRgbData = ImageUtil.scale( scaleWidth, scaleHeight, imgWidth, 
					imgWidth, imgHeight, this.rgbData);
			Object[] localeScaleData = new Object[]{ scaledRgbData, new Integer( scaleWidth ) };
			// read the last scale data for repainting the correctly sized area:
			Object[] lastScaleData = this.scaleData;
			if (lastScaleData != null) {
				int[] sData = (int[]) lastScaleData[0];
				int lastScaleWidth = ((Integer) lastScaleData[1]).intValue();
				int lastScaleHeight = sData.length / lastScaleWidth;
				if (lastScaleWidth > scaleWidth) {
					scaleWidth = lastScaleWidth;
				}
				if (lastScaleHeight > scaleHeight) {
					scaleHeight = lastScaleHeight;
				}
			}
			// this operation is atomic:
			this.scaleData = localeScaleData;
			// add repaint area:
			addRelativeToContentRegion( repaintRegion, 
								this.relativeIconX + ((this.imageWidth - scaleWidth) >> 1), 
								this.relativeIconY + ((this.imageHeight - scaleHeight) >> 1),
								scaleWidth,
								scaleHeight 
			);
		}	
	}
	//#endif

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#defocus(de.enough.polish.ui.Style)
	 */
	protected void defocus(Style originalStyle) {
		if (this.imageNormal != null) {
			setImage( this.imageNormal );
		}
		super.defocus(originalStyle);		
		//#if polish.midp2 && polish.css.scale-factor
			this.scaleFinished = false;
			this.scaleDown = false;
			this.currentStep = 0;
			this.scaleData = null;
		//#endif
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#focus(de.enough.polish.ui.Style, int)
	 */
	protected Style focus(Style newStyle, int direction) {
		if (this.imageHover != null && this.image != this.imageHover) {
			this.imageNormal = this.image;
			setImage( this.imageHover );
		}
		return super.focus(newStyle, direction);
	}

		//#if polish.debug.enabled
		public String toString(){
			return "IconItem(" + this.text + ")/" + super.toString();
		}
	//#endif
		
	/**
	 * Releases all (memory intensive) resources such as images or RGB arrays of this item.
	 * The default implementation does release any background resources.
	 */
	public void releaseResources() {
		super.releaseResources();
		//#if polish.midp2 && polish.css.scale-factor
			this.scaleData = null;
			this.isStyleInitialised = false;
			setInitialized(false);
		//#endif
			//#if polish.css.icon-filter && polish.midp2
			if (this.iconFilters != null) {
				for (int i=0; i<this.iconFilters.length; i++) {
					RgbFilter filter = this.iconFilters[i];
					filter.releaseResources();
				}
			}
		//#endif
	}

	/**
	 * Determines if the text of this icon item is visible
	 * @return true when the text is shown
	 */
	public boolean isTextVisible() {
		return this.isTextVisible;
	}
	
	/**
	 * Toggles the visibility of this icon's text
	 * @param isTextVisible true when the text should be shown
	 */
	public void setTextVisible( boolean isTextVisible ) {
		this.isTextVisible = isTextVisible;
		if (!isTextVisible) {
			this.textLines.clear();
		}
		this.isTextInitializationRequired = true;
		setInitialized(false);
	}

	//#if polish.midp2
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#getRgbData(boolean, int)
	 */
	public int[] getRgbData(boolean supportTranslucency, int rgbOpacity) {
		if ( this.background == null 
			&& this.border == null
			&& (this.itemWidth == this.imageWidth || this.itemWidth == this.imageWidth + 2)
			&& (this.itemHeight == this.imageHeight|| this.itemHeight == this.imageHeight + 2)
			&& this.image != null
			) 
			
		{
			// this item only displays the image, so we can speed up the RGB retrieval:
			int[] rgbImageData = new int[ this.itemWidth * this.itemHeight ];
			int offset = 0;
			if (this.itemWidth > this.imageWidth) {
				offset = 1;
			}
			if (this.itemHeight > this.imageHeight) {
				offset += this.itemWidth;
			}
			this.image.getRGB(rgbImageData, offset, this.itemWidth, 0, 0, this.imageWidth, this.imageHeight );
			return rgbImageData;
		}
		return super.getRgbData(supportTranslucency, rgbOpacity);
	}
	//#endif


	
//#ifdef polish.IconItem.additionalMethods:defined
	//#include ${polish.IconItem.additionalMethods}
//#endif

}
