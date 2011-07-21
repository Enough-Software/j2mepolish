//#condition polish.usePolishGui
/*
 * Created on 05-May-2004 at 15:11:20.
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

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * <p>Paints a single item of a choice group.</p>
 * 
 * As for <code>IconItem</code>,
 * <code>ChoiceItem</code> is a <i>de.enough.polish</i> extension to group
 * in a same Object text and image, <code>ChoiceItem</code>s being aimed at
 * choice items of lists, e.g. <code>List</code> or <code>ChoiceGroup</code>.&nbsp;
 * Hence, arguments of type <code>ChoiceItem</code> are found in the constructors
 * of these lists, and in all methods for adding items to them, or retrieving
 * items from them.
 * <p>
 * This is a much better alternative to methods passing separately string and
 * image parts, for (at least) 2 reasons:
 * <ol>
 * <li>for methods taking an array of <code>String</code>s and <code>Image</code>s,
 * such as:</li>
 * <ul>
 * <li><code>
 * List(String title, int listType, String[] stringElements, Image[] imageElements)
 * </code></li>
 * <li><code>
 * ChoiceGroup(String label, int choiceType, String[] stringElements, Image[] imageElements)
 * </code></li>
 * </ul>
 * the caller <u>must</u> ensure that both arrays have the same length.&nbsp;
 * Providing an unique array of <code>ChoiceItem</code>, each of them containing
 * its image and text, solves this error-prone constraint.
 * <li>there is no easy way to associate data to a given choice.  One must create
 * a parallel array containing the associated data and ensure the synchronism between
 * the data array and the displayed choices array.&nbsp;
 * Extending <code>ChoiceItem</code> is a much
 * robust approach, as shown in this example:
 * </ol>
 * <PRE>
 * import javax.microedition.lcdui.*;
 * import de.enough.polish.ui.ChoiceItem;
 * 
 * class ListExample extends List implements CommandListener
 * {
 *    static class Entry extends ChoiceItem {
 *       String data;
 *       Entry(String text, Image img, String data) {
 *          //#style default
 *          super(text, img, List.IMPLICIT);
 *          this.data = data;
 *       }
 *    }
 * 
 *    static final Entry[] entries = {
 *       new Entry("Item #1", null, "data 1"),
 *       new Entry("Item #2", null, "data 2"),
 *       new Entry("Item #3", null, "data 3")
 *    };
 * 
 *    ListExample() {
 *       //#style default
 *       super("A List", List.IMPLICIT, entries);
 *       setCommandListener(this);
 *       display.setCurrent(this);
 *    }
 * 
 *    public void commandAction(Command c, Displayable d)
 *    {
 *       int sel;
 *       if ((c == List.SELECT_COMMAND) &amp;&amp; (-1 != (sel = getSelectedIndex()))) {
 *          System.out.println("Selected data: " + ((Entry)getItem(sel)).data);
 *       }
 *    }
 * }
 * </PRE>
 * <b><i>(Example and JavaDoc provided by Pierre G. Richard)</i></b> 
 * <p>Copyright Enough Software 2004 - 2009</p>
 * <pre>
 * history
 *        05-May-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class ChoiceItem extends IconItem 
//#ifdef polish.images.backgroundLoad
	implements ImageConsumer
//#endif
{
	private static Image defaultRadioSelected;
	private static Image defaultRadioPlain;
	private static Image defaultCheckSelected;
	private static Image defaultCheckPlain;
	
	private boolean isMultiple;
	private Image selected;
	private Image plain;
	/** determines whether this item is currently selected */
	public boolean isSelected;
	private Image boxImage;
	private int boxWidth;
	private int yAdjust;
	/** determines whether an image is drawn before the item */
	public boolean drawBox;
	private int choiceType;
	protected Font preferredFont;
	private int boxColor;
	/** defines whether the plain image should be drawn at all */ 
	private boolean drawNoPlain;
	private boolean drawNoSelected;
	//#ifdef polish.images.backgroundLoad
		private String selectedImgName;
		private String plainImgName;
	//#endif
	private int yBoxAdjust;
	//#if polish.css.box-align
		private boolean isBoxAlignRight;
	//#endif
	//#if polish.css.checked-style
		Style styleNormal;
		Style styleNormalFocused;
		Style styleChecked;
		Style styleCheckedFocused;
	//#endif

	/**
	 * Creates a new ChoiceItem.
	 * 
	 * @param text the text of the item
	 * @param image the image of the item
	 * @param choiceType the type of this item, either 
	 * 		Choice.EXCLUSIVE, Choice.MULTIPLE, Choice.IMPLICIT or Choice.POPUP.
	 * 		When IMPLICIT or POPUP is used, no selection-box will be drawn. 
	 */
	public ChoiceItem(String text, Image image, int choiceType ) {
		this( text, image, choiceType, null );
	}
	
	/**
	 * Creates a new ChoiceItem.
	 * 
	 * @param text the text of the item
	 * @param image the image of the item
	 * @param choiceType the type of this item, either 
	 * 		Choice.EXCLUSIVE, Choice.MULTIPLE, Choice.IMPLICIT or Choice.POPUP.
	 * 		When IMPLICIT or POPUP is used, no selection-box will be drawn. 
	 * @param style the CSS style of this item
	 */
	public ChoiceItem(String text, Image image, int choiceType, Style style) {
		super(text, image, style);
		this.choiceType = choiceType;
		this.drawBox = (choiceType == Choice.EXCLUSIVE) || (choiceType == Choice.MULTIPLE);
		this.isMultiple = (choiceType == Choice.MULTIPLE);
		this.appearanceMode = INTERACTIVE;
	}
	/**
	 * Changes the type of this ChoiceItem
	 * @param choiceType eithe Choice.EXCLUSIVE, Choice.MULTPIPLE or Choice.IMPLICIT
	 */
	public void setChoiceType( int choiceType ) {
		this.choiceType = choiceType;
		this.drawBox = (choiceType == Choice.EXCLUSIVE) || (choiceType == Choice.MULTIPLE);
		this.isMultiple = (choiceType == Choice.MULTIPLE);
	}
	
	//#ifdef polish.useDynamicStyles
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#createCssSelector()
	 */
	protected String createCssSelector() {
		if (this.choiceType == Choice.EXCLUSIVE) {
			return "radiobox";
		} else if (this.choiceType == Choice.MULTIPLE) {
			return "checkbox";
		} else if (this.choiceType == Choice.IMPLICIT){
			return "listitem";
		} else {
			return "popup";
		}
	}
	//#endif
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#initContent(int, int)
	 */
	protected void initContent(int firstLineWidth, int availWidth, int availHeight) {
		if (!this.drawBox) {
			initContentImpl(firstLineWidth, availWidth, availHeight);
			return;
		}
		if (this.selected == null && !this.drawNoSelected) {
			this.selected = createSelected();
		}
		if (this.plain == null && !this.drawNoPlain) {
			this.plain = createPlain();
		}
		int maxWidth = this.selected == null ? 0 : this.selected.getWidth();
		
		if (!this.drawNoPlain && this.plain.getWidth() > maxWidth ) {
			maxWidth = this.plain.getWidth();
		}
		//#if polish.css.box-align
		if(!this.isBoxAlignRight) 
		//#endif
		{
			// only add horizontal padding if the box align is not right
			maxWidth += this.paddingHorizontal;
		}
		this.boxWidth = maxWidth;
		int maxHeight = this.selected == null ? 0 : this.selected.getHeight();
		if ( !this.drawNoPlain && this.plain.getHeight() > maxHeight ) {
			maxHeight = this.plain.getHeight();
		}
		firstLineWidth -= maxWidth;
		availWidth -=  maxWidth;
		
		initContentImpl(firstLineWidth, availWidth, availHeight);
		this.contentWidth += maxWidth;
		if (this.contentHeight < maxHeight) {
			// the image is bigger than the text:
			this.yBoxAdjust = 0;
			if ((this.layout & LAYOUT_VCENTER) == LAYOUT_VCENTER){
				this.yAdjust = (maxHeight - this.contentHeight)/2;
			} else if ((this.layout & LAYOUT_BOTTOM) == LAYOUT_BOTTOM) { 
				this.yAdjust = maxHeight - this.contentHeight;
			} else {
				this.yAdjust = 0;
			}
			this.contentHeight = maxHeight;
		} else {
			// the image is smaller than the text:
			if ((this.layout & LAYOUT_VCENTER) == LAYOUT_VCENTER){
				this.yBoxAdjust = (this.contentHeight - maxHeight)/2;
			} else if ((this.layout & LAYOUT_BOTTOM) == LAYOUT_BOTTOM) { 
				this.yBoxAdjust = this.contentHeight - maxHeight;
			} else {
				this.yBoxAdjust = 0;
			}
			this.yAdjust = 0;
		}
		if (this.isSelected) {
			this.boxImage = this.selected;
		} else {
			this.boxImage = this.plain;
		}
	}
	
	/**
	 * Initializes the content of this choice item (apart from the choice box).
	 * @param firstLineWidth the available width
	 * @param availWidth the available width
	 * @param availHeight the available height
	 */
	protected void initContentImpl(int firstLineWidth, int availWidth, int availHeight) {
		super.initContent(firstLineWidth, availWidth, availHeight);
	}
	
	/**
	 * Creates a selected image.
	 * 
	 * @return the image for a selected item
	 */
	private Image createSelected() {
		if (this.isMultiple) {
			if (defaultCheckSelected == null ) {
				defaultCheckSelected = Image.createImage(12, 12);
				Graphics g = defaultCheckSelected.getGraphics();
				g.setColor( this.boxColor );
				g.drawRect(0, 0, 11, 11 );
				g.drawRect(1, 1, 9, 9 );
				g.drawLine(0, 0, 11, 11 );
				g.drawLine( 11, 0, 0, 11 );
			}
			return defaultCheckSelected;
		} else {
			if (defaultRadioSelected == null ) {
				defaultRadioSelected = Image.createImage(11, 11);
				Graphics g = defaultRadioSelected.getGraphics();
				g.setColor( this.boxColor );
				g.fillRect(0, 0, 12, 12 );
				g.setColor( 0xFFFFFF );
				g.fillArc(1, 1, 9, 9, 0, 360 );
				g.setColor( this.boxColor );
				g.fillArc(3, 3, 5, 5, 0, 360 );
			}
			return defaultRadioSelected;
		}
	}

	/**
	 * Creates a plain image
	 * @return the image for a plain item
	 */
	private Image createPlain() {
		if (this.isMultiple) {
			if (defaultCheckPlain == null ) {
				defaultCheckPlain = Image.createImage(12, 12);
				Graphics g = defaultCheckPlain.getGraphics();
				g.setColor( this.boxColor );
				g.drawRect(0, 0, 11, 11 );
				g.drawRect(1, 1, 9, 9 );
			}
			return defaultCheckPlain;
		} else {
			if (defaultRadioPlain == null ) {
				defaultRadioPlain = Image.createImage(11, 11);
				Graphics g = defaultRadioPlain.getGraphics();
				g.setColor( this.boxColor );
				g.fillRect(0, 0, 12, 12 );
				g.setColor( 0xFFFFFF );
				g.fillArc(1, 1, 9, 9, 0, 360 );
			}
			return defaultRadioPlain;
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		if (this.drawBox) {
			int boxX = x;
			int boxY = y + this.yBoxAdjust;
			//#if polish.css.box-align
				if (this.isBoxAlignRight) {
					rightBorder -= this.boxWidth;
					boxX = rightBorder;
				} else {
			//#endif
					x += this.boxWidth;
					leftBorder += this.boxWidth;					
			//#if polish.css.box-align
				}
			//#endif
			if ( this.boxImage != null) {
				g.drawImage( this.boxImage, boxX, boxY, Graphics.TOP | Graphics.LEFT );
			}
			y += this.yAdjust;
		}
		paintContentImpl(x, y, leftBorder, rightBorder, g);
	}
	
	/**
	 * Paints the content of this choice item, typically the icon item.
	 * @param x the x position
	 * @param y the y position
	 * @param leftBorder the left border
	 * @param rightBorder the right border
	 * @param g the graphics context
	 */
	protected void paintContentImpl( int x, int y, int leftBorder, int rightBorder, Graphics g) { 
		super.paintContent(x, y, leftBorder, rightBorder, g);
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		super.setStyle(style);
		if (this.drawBox) {
			Style parentStyle = null;
			if (this.parent != null) {
				parentStyle = this.parent.style;
			}
			//#ifdef polish.css.radiobox-selected
				if (this.choiceType == Choice.EXCLUSIVE) {
					String url = style.getProperty( "radiobox-selected");
					if (url == null && parentStyle != null) {
						url = parentStyle.getProperty( "radiobox-selected");
					}
					if ("none".equals(url)) {
						this.drawNoSelected = true;
					} else {
						//this.drawNoSelected = false;
						loadImage( url, false );
					}
				}
			//#endif
			//#ifdef polish.css.radiobox-plain
				if (this.choiceType == Choice.EXCLUSIVE) {
					String plainName = style.getProperty( "radiobox-plain");
					if (plainName == null && parentStyle != null) {
						plainName = parentStyle.getProperty( "radiobox-plain");
					}
					if ("none".equals(plainName)) {
						this.drawNoPlain = true;
					} else {
						//this.drawNoPlain = false;
						loadImage( plainName, true );
					}
				}
			//#endif
			//#ifdef polish.css.checkbox-selected
				if (this.choiceType == Choice.MULTIPLE) {
					String url = style.getProperty( "checkbox-selected");
					if (url == null && parentStyle != null) {
						url = parentStyle.getProperty( "checkbox-selected");
					}
					if ("none".equals(url)) {
						this.drawNoSelected = true;
					} else {
						//this.drawNoSelected = false;
						loadImage( url, false );
					}
				}
			//#endif
			//#ifdef polish.css.checkbox-plain
				if (this.choiceType == Choice.MULTIPLE) {
					String plainName = style.getProperty( "checkbox-plain");
					if (plainName == null && parentStyle != null) {
						plainName = parentStyle.getProperty( "checkbox-plain");
					}
					if ("none".equals(plainName)) {
						this.drawNoPlain = true;
					} else {
						//this.drawNoPlain = false;
						loadImage( plainName, true );
					}
				}
			//#endif
			//#ifdef polish.css.font-color
				this.boxColor = style.getFontColor();
			//#endif
			//#ifdef polish.css.choice-color
				Color color = style.getColorProperty("choice-color");
				if (color == null && parentStyle != null) {
					color = parentStyle.getColorProperty("choice-color");
				}
				if (color != null) {
					this.boxColor = color.getColor();
				}
			//#endif				
		} // if draw box
		//#if polish.css.checked-style
			Style st = (Style) style.getObjectProperty("checked-style");
			if (st != null) {
				if (this.styleNormal == null) {
					this.styleNormal = style; 
				}
				if (this.isFocused) {
					//System.out.println("Setting styleCheckedFocused=" + st.name);
					this.styleCheckedFocused = st; 
				} else {
					//System.out.println("Setting styleChecked=" + st.name);
					this.styleChecked = st;
					if (this.styleCheckedFocused == null) {
						Style focStyle = getFocusedStyle();
						st = (Style) focStyle.getObjectProperty("checked-style");
						if (st == null) {
							st = this.styleChecked;
						}
						//System.out.println("Setting styleCheckedFocused=" + st.name);
						this.styleCheckedFocused = st;
					}
				}
			}
		//#endif
		//#ifdef polish.css.box-align
			Integer boxAlignInt = style.getIntProperty("box-align");
			if (boxAlignInt != null) {
				this.isBoxAlignRight = (boxAlignInt.intValue() == 1);
			}
		//#endif
	}

	//#ifdef polish.css.choice-color
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style, boolean resetStyle) {
		super.setStyle(style, resetStyle);
		if (this.drawBox) {
			//#ifdef polish.css.choice-color
				Color color = style.getColorProperty("choice-color");
				if (color != null) {
					this.boxColor = color.getColor();
				}
			//#endif
	
		} // if draw box
	}
	//#endif
	
	private void loadImage( String name, boolean isPlain ) {
		if (name == null) {
			return;
		}
		//#ifdef polish.images.backgroundLoad
			if (isPlain) {
				this.plainImgName = name;
			} else {
				this.selectedImgName = name;
			}
		//#endif
		try {
			if (isPlain) {
				this.plain = StyleSheet.getImage( name, this, true );
			} else {
				this.selected = StyleSheet.getImage( name, this, true );
			}
		} catch (IOException e) {
			//#debug error
			System.out.println("Unable to load image [" + name + "]" + e );
		}
		
	}


	//#ifdef polish.images.backgroundLoad
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ImageConsumer#setImage(java.lang.String, javax.microedition.lcdui.Image)
	 */
	public void setImage(String name, Image image) {
		if (!this.drawBox) {
			super.setImage( name, image );
		} else if (name.equals(this.selectedImgName)) {
			this.selected = image;
		} else if (name.equals(this.plainImgName)) {
			this.plain = image;
		} else {
			setImage( image );
		}
		setInitialized(false);
		/*
		this.isInitialised = false;
		repaint();
		*/
	}
	//#endif

	/**
	 * Selects or deselects this item.
	 * 
	 * @param select true when this item should be selected
	 */
	public void select(boolean select ) {
		if (select == this.isSelected) {
			// ignore
			return;
		}
		this.isSelected = select;
		if (select) {
			this.boxImage = this.selected;
		} else {
			this.boxImage = this.plain;
		}
		//#if polish.css.checked-style
			setCheckedStyle(select);
		//#endif
		/*
		 * if this is enabled, the navigation in Lists does not work anymore...
		if (this.isInitialised) {
			this.isInitialised = false;
			repaint();
		}*/
	}

	//#if polish.css.checked-style
	/**
	 * Sets the correct style for this ChoiceItem when a checked-style is being used.
	 * @param select
	 */
	private void setCheckedStyle(boolean select) {
//			System.out.println("select occurred, all styles defined=" + (this.styleNormal != null) + ( this.styleNormalFocused != null) + ( this.styleChecked != null) + ( this.styleCheckedFocused != null) + ", isFocused=" + this.isFocused);
			if (select) {
				if (this.isFocused && this.styleCheckedFocused != null) {
//					System.out.println("x 1a checked & focused & styleCheckedFocused");
					setStyle( this.styleCheckedFocused);
				} else if (!this.isFocused && this.styleChecked != null) {
//					System.out.println("x 1b checked & !focused & styleChecked");
					setStyle( this.styleChecked);
				}
			} else {
				if (this.isFocused && this.styleNormalFocused != null) {
//					System.out.println("x 2a !checked & focused & styleNormalFocused");
					setStyle(this.styleNormalFocused);
				} else if (!this.isFocused && this.styleNormal != null) {
//					System.out.println("x 2b !checked & !focused & styleNormal");
					setStyle(this.styleNormal);
				}
			}
	}
	//#endif
	
	/**
	 * Changes the selected-state to the opposite state.
	 */
	public void toggleSelect() {
		select( !this.isSelected );
	}
	
	//#if polish.css.checked-style
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#focus(Style, int)
	 */
	protected Style focus(Style newStyle, int direction) {
		if (this.style == this.styleChecked && this.styleCheckedFocused == null) {
			this.styleCheckedFocused = newStyle;
		} else if (this.style == this.styleNormal && this.styleNormalFocused == null) {
			this.styleNormalFocused = newStyle;
		} else if (this.isSelected && this.styleCheckedFocused != null) {
			if (this.styleNormalFocused == null) {
				this.styleNormalFocused = newStyle;
			}
			newStyle = this.styleCheckedFocused;
		}
		Style st = super.focus(newStyle, direction);
		if (this.styleNormal != null) {
			st = this.styleNormal;
		}
		return st;
	}
	//#endif

	//#if polish.css.checked-style
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#focus(Style, int)
	 */
	protected void defocus(Style originalStyle) {
		if ( this.isSelected && this.styleChecked != null && originalStyle == this.styleNormal) {
			originalStyle = this.styleChecked;
		}
		super.defocus(originalStyle);
	}
	//#endif

	/**
	 * Sets the preferred font.
	 * This setting is ignored, since the J2ME Polish implementation
	 * uses the CSS settings.
	 *  
	 * @param font the preferred font
	 */
	public void setPreferredFont(Font font) {
		this.preferredFont = font;
	}

	/**
	 * @param lastItem the item to use the values from for adjusting
	 */
	public void adjustProperties(Item lastItem) {
		this.relativeY = lastItem.relativeY;
	}
	
	/**
	 * Sets the image representing the "not selected" state of this choice item.
	 * 
	 * @param image the image representing the "not selected" state
	 */
	public void setPlainImage( Image image ) {
		this.plain = image;
	}

	/**
	 * Gets the image representing the "not selected" state of this choice item.
	 * 
	 * @return the image representing the "not selected" state
	 */
	public Image getPlainImage() {
		return this.plain;
	}

	/**
	 * Sets the image representing the "selected" state of this choice item.
	 * 
	 * @param image the image representing the "selected" state
	 */
	public void setSelectedImage( Image image ) {
		this.selected = image;
	}
	
	/**
	 * Gets the image representing the "selected" state of this choice item.
	 * 
	 * @return the image representing the "selected" state
	 */
	public Image getSelectedImage() {
		return this.selected;
	}

	/**
	 * Determines whether this choice is selected.
	 * @return true when this choice is selected
	 */
	public boolean isSelected() {
		return this.isSelected;
	}

	//#if (polish.css.portrait-style || polish.css.landscape-style) && polish.css.checked-style
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Item#onScreenSizeChanged(int, int)
	 */
	public void onScreenSizeChanged( int screenWidth, int screenHeight ) {
//		if (this.text != null) {
//			System.out.println("ON SCREEN SIZE CHANGED FOR " + this.text + ", width=" + screenWidth + ", height=" + screenHeight);
//		}
		super.onScreenSizeChanged(screenWidth, screenHeight);
		Style newStyle = null;
		if (screenWidth > screenHeight) {
			if (this.landscapeStyle != null && this.styleNormal != this.landscapeStyle) {
				newStyle = this.landscapeStyle;
			}
		} else if (this.portraitStyle != null && this.styleNormal != this.portraitStyle){
			newStyle = this.portraitStyle;
		}
		if (newStyle != null) {
			//System.out.println("ChoiceItem.onScreenSizeChanged: normalStyle=" + newStyle.name);
			if (this.parent instanceof Container) {
				Container cont = (Container)this.parent;
				if (cont.itemStyle == this.styleNormal) {
					cont.itemStyle = newStyle;
				}
			}
			this.styleNormal = newStyle;
			//#if polish.css.focused-style
				Style focStyle = (Style) newStyle.getObjectProperty("focused-style");
				if (focStyle != null) {
//					System.out.println("ChoiceItem.onScreenSizeChanged: styleNormalFocused=" + focStyle.name);
					this.styleNormalFocused = focStyle;
				}
			//#endif
			newStyle = (Style) newStyle.getObjectProperty("checked-style");
			if (newStyle != null) {
				this.styleChecked = newStyle;
//				System.out.println("ChoiceItem.onScreenSizeChanged: styleChecked=" + newStyle.name + " for " + this.text);
				//#if polish.css.focused-style
					focStyle = (Style) newStyle.getObjectProperty("focused-style");
					if (focStyle != null) {
						this.styleCheckedFocused = focStyle;
//						System.out.println("ChoiceItem.onScreenSizeChanged: styleCheckedFocused=" + focStyle.name);
					}
				//#endif
			}
		}
		setCheckedStyle(this.isSelected);
	}
	//#endif

	//#if polish.css.checked-style && polish.css.pressed-style
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#notifyItemPressedEnd()
	 */
	public void notifyItemPressedEnd() {
		Style previous = this.style;
		super.notifyItemPressedEnd();
		if (this.style != previous) {
			setCheckedStyle(this.isSelected);
		}
	}
	//#endif
	
	
}
