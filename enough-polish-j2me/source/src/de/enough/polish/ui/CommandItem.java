//#condition polish.usePolishGui
/*
 * Created on Mar 4, 2006 at 3:17:15 PM.
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
package de.enough.polish.ui;

import java.io.IOException;

import javax.microedition.lcdui.Canvas;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;


/**
 * <p>Wraps a javax.microedition.lcdui.Command object and allows to add subcommands, specific styles etc to single commands.</p>
 *
 * <p>Copyright Enough Software 2006 - 2009</p>
 * <pre>
 * history
 *        Mar 4, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CommandItem extends IconItem {
	
	protected final Command command;
	protected Container children;
	protected boolean hasChildren;
	private int childIndicatorWidth = 5;
	private int childIndicatorYOffset;
	private int childIndicatorHeight = 5;
	//#if polish.css.command-child-indicator
		private Image childIndicator;
	//#endif
	//#if polish.css.command-child-indicator-color
		private int childIndicatorColor = -1;
	//#endif
	private boolean isOpen;
	//#if polish.css.suppress-indicator
		boolean suppressIndicator;
	//#endif
	
	/**
	 * Creates a new command item.
	 * 
	 * @param command the commmand represented by this item.
	 * @param parent the parent item
	 */
	public CommandItem( Command command, Item parent ) {
		this( command, parent, command.getStyle() );
	}

	/**
	 * Creates a new command item.
	 * 
	 * @param command the command represented by this item.
	 * @param parent the parent item
	 * @param style the style for this item
	 */
	public CommandItem( Command command, Item parent, Style style ) {
		super( null, command.getLabel(), null, (command.getStyle() != null ? command.getStyle() : style) );
		this.appearanceMode = Item.INTERACTIVE;
		this.command = command;
		this.parent = parent;
		if (command.hasSubCommands()) {
			Object[] cmds = command.getSubCommmandsArray();
			for (int i = 0; i < cmds.length; i++) {
				Command child = (Command) cmds[i];
				if (child == null) {
					break;
				}
				addChild( child, child.getStyle() );
			}
		}
		if (command.getCommandType() == Command.SEPARATOR) {
			this.appearanceMode = PLAIN;
		}
	}
	
	/**
	 * Adds a subcommand to this node.
	 * 
	 * @param childCommand the child command
	 */
	public void addChild( Command childCommand ) {
		addChild( childCommand, null );
	}
	
	/**
	 * Adds a subcommand to this node.
	 * 
	 * @param childCommand the child command
	 * @param childStyle the style for the child command
	 */
	public void addChild( Command childCommand, Style childStyle ) {
		CommandItem child = new CommandItem( childCommand, this, childStyle );
		addChild( child );
	}
	
	/**
	 * Adds a subcommand to this node.
	 * 
	 * @param child the child command item
	 */
	public void addChild(CommandItem child) {
		boolean inserted = false;
		if ( this.children == null ) {
			int layer = getLayer();
			if (layer == 0) {
				//#style menu1?
				this.children = new Container( true );
			} else if (layer == 1) {
				//#style menu2?
				this.children = new Container( true );
			} else if (layer == 2) {
				//#style menu3?
				this.children = new Container( true );
			}
			if (this.children == null) {
				this.children = new Container( true, this.parent.style );
			} else if (this.children.style == null) {
				this.children.style = this.parent.style;
			}
			this.hasChildren = true;
			this.children.parent = this;
		} else {
			int priority = child.command.getPriority();
			for (int i = 0; i < this.children.size(); i++) {
				CommandItem item = (CommandItem) this.children.get(i);
				if (item.command.getPriority() > priority ) {
					this.children.add( i, child );
					inserted = true;
					break;
				}
			}
		}
		if (!inserted) {
			this.children.add( child );
		}
	}

	/**
	 * Retrieves the layer to which this command item belongs.
	 * This method is useful for sub commands.
	 * 
	 * @return the layer, 0 means this command item belongs to the main commands.
	 */
	public int getLayer() {
		Item parentItem = this.parent;
		int layer = 0;
		while (parentItem != null) {
			while (! (parentItem instanceof CommandItem) ) {
				//System.out.println("getLayer - skipping parent " + parentItem );
				parentItem = parentItem.parent;
				if (parentItem == null) {
					//System.out.println("getLayer - returning because of null parent ");
					return layer;
				}
			}
			if (parentItem == null) {
				return layer;
			}
			parentItem = parentItem.parent;
			layer++;
		}
		return layer;
	}
	
	/**
	 * Removes a child from this command item.
	 * 
	 * @param childCommand the child that should be removed
	 * @return true when the child was found
	 */
	public boolean removeChild( Command childCommand ) {
		if (this.children == null) {
			return false;
		}
		for ( int i=0; i < this.children.size(); i++ ) {
			CommandItem item = (CommandItem) this.children.get(i);
			if ( item.command == childCommand ) {
				this.children.remove( i );
				return true;
			}
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.IconItem#initContent(int, int)
	 */
	protected void initContent(int firstLineWidth, int availWidth, int availHeight) {
		//#debug
		System.out.println("initContent(" + firstLineWidth + ", " + availWidth + ", " + availHeight + ") for " + this);
		if (this.command.getCommandType() == Command.SEPARATOR) {
			this.contentWidth = availWidth;
			this.contentHeight = 0;
			return;
		}
		if (this.hasChildren) {
			firstLineWidth -= this.childIndicatorWidth + this.paddingHorizontal;
			availWidth -= this.childIndicatorWidth + this.paddingHorizontal;
		}
		
		super.initContent(firstLineWidth, availWidth, availHeight);
		
		if (this.hasChildren) {
			this.contentWidth += this.childIndicatorWidth + this.paddingHorizontal;
			if ( this.childIndicatorHeight > this.contentHeight ) {
				this.contentHeight = this.childIndicatorHeight;
			} else {
				this.childIndicatorYOffset = (this.contentHeight - this.childIndicatorHeight) / 2;
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.IconItem#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		// TODO robertvirkus adjust yOffset when childIndicatorHeight > contentHeight
		super.paintContent(x, y, leftBorder, rightBorder, g);
		
//		if (this.isFocused) {
//			System.out.println( this + ": backgroundYOffset=" + this.backgroundYOffset);
//		}
		if (this.hasChildren) {
			paintChildren(x, y, leftBorder, rightBorder, g);
		}
	}

	/**
	 * @param y
	 * @param leftBorder
	 * @param rightBorder
	 * @param g
	 */
	protected void paintChildren(int x, int y, int leftBorder, int rightBorder,
			Graphics g)
	{
		// paint child indicator
		//rightBorder -= this.childIndicatorWidth;
		//#if polish.css.suppress-indicator
		if(!this.suppressIndicator)
		{
		//#endif
			//#if polish.css.command-child-indicator
				if (this.childIndicator != null) {
					int height = this.childIndicator.getHeight(); // center image if lower than the font:
					g.drawImage( this.childIndicator, rightBorder, y + (this.contentHeight - height)/2, Graphics.TOP | Graphics.RIGHT );
				} else {
			//#endif
					//#if polish.css.command-child-indicator-color
						if ( this.childIndicatorColor != -1 ) {
							g.setColor( this.childIndicatorColor );							
						}
					//#endif
					x = rightBorder - this.childIndicatorWidth;
					int indicatorY = y + this.childIndicatorYOffset;
					//#if polish.midp2
						g.fillTriangle(x, indicatorY, rightBorder, indicatorY + this.childIndicatorHeight/2, x, indicatorY + this.childIndicatorHeight );
					//#else
						g.drawLine( x, indicatorY, rightBorder, indicatorY + this.childIndicatorHeight/2 );
						g.drawLine( x, indicatorY + this.childIndicatorHeight, rightBorder, indicatorY + this.childIndicatorHeight/2 );
						g.drawLine( x, indicatorY, x, indicatorY + this.childIndicatorHeight );
					//#endif
			//#if polish.css.command-child-indicator
				}
			//#endif
		//#if polish.css.suppress-indicator
		}
		//#endif
		
		if (this.isOpen) {
			int originalY = y;
			// draw children:
			// when there is enough space to the right, open it on the right side, otherwise open it on the left:
			int clipX = g.getClipX();
			int clipWidth = g.getClipWidth();
			int clipY = g.getClipY() - 1;
			int clipHeight = g.getClipHeight();
//				g.setColor(0x00FF00);
//				g.fillRect(clipX, clipY, clipWidth, clipHeight);
			
			int availWidth = (clipWidth * 2) / 3;
			int availHeight = this.availableHeight;
			int childrenWidth = this.children.getItemWidth( availWidth, availWidth, availHeight );
			int childrenHeight = this.children.itemHeight; // is initialised because of the getItemWidth() call
			rightBorder += this.paddingHorizontal;
//				System.out.println("drawing children: children-width " + childrenWidth + ", clipWidth=" + clipWidth + ", leftBorder=" + leftBorder + ", rightBorder=" + rightBorder);
//				System.out.println("rightBorder + childrenWidth=" + (rightBorder + childrenWidth) + ", clipX + clipWidth=" + ( clipX + clipWidth ));
//				System.out.println("clipX + clipWidth - childrenWidth=" + (clipX + clipWidth - childrenWidth) + ", (leftBorder + 10)=" + (leftBorder + 10));
//				System.out.println("clipX=" + clipX + ", leftBorder - childrenWidth=" + (leftBorder - childrenWidth));
			if ( rightBorder + childrenWidth < clipX + clipWidth ) {					
				x = rightBorder;
			} else if ( clipX + clipWidth - childrenWidth > (leftBorder + 10) ) {
				x = clipX + clipWidth - (childrenWidth + 1);
			} else {
				x = Math.max( leftBorder - childrenWidth, clipX );
			}
			//System.out.println("submenu: y=" + y + ", y + childrenHeight=" + (y + childrenHeight) + ", clipY + clipHeight=" + (clipY + clipHeight));
			if ( y + childrenHeight > clipY + clipHeight ) {
				y -= (y + childrenHeight) - (clipY + clipHeight);
				//System.out.println("submenu: adjusted y=" + y + ", clipY=" + clipY);
				if ( y < clipY ) {
					y = clipY;
				}
//					g.setColor( 0x00FFFF);
//					for (int i = 0; i < 15; i++) {
//						g.drawLine( x - 20,y-i, x+childrenWidth+10, y-i);						
//					}
			}
			this.children.relativeX = x - leftBorder;
			this.children.relativeY = y - originalY;
			this.children.setScrollHeight( clipHeight );
			this.children.paint( x, y, x, x + childrenWidth, g);
			//System.out.println("set height for children to " + clipHeight + ", yOffset=" + this.children.yOffset + ", internalY=" + this.children.internalY);				
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handleKeyPressed(int, int)
	 */
	protected boolean handleKeyPressed(int keyCode, int gameAction) {
		//#debug
		System.out.println( this + " handleKeyPressed, isOpen=" + this.isOpen);
		boolean isFireGameAction = getScreen().isGameActionFire(keyCode, gameAction);
		if ( this.isOpen ) {
			if (
			//#if !polish.blackberry || polish.hasTrackBallEvents
				(gameAction == Canvas.LEFT && keyCode != Canvas.KEY_NUM4)
			//#else
				//# false
			//#endif
			) {
				// close menu:
				notifyItemPressedStart();
			} else {
				//#if polish.Container.allowCycling != false
					// allow subcommands to cycle in any case:
					this.children.allowCycling = true;
				//#endif
				boolean handled = this.children.handleKeyPressed(keyCode, gameAction);
				if (!handled) {
					 if (keyCode >= Canvas.KEY_NUM1 && keyCode <= Canvas.KEY_NUM9) {
						int index = keyCode - Canvas.KEY_NUM1;
						if (index <= this.children.size()) {
							CommandItem item = (CommandItem) this.children.get(index);
							return item.notifyItemPressedStart();
						}
					}
					notifyItemPressedStart();
				}
			}
			return true;
		} else if ( this.hasChildren && this.appearanceMode != PLAIN ) { // has children but is not open
			if ( isFireGameAction
				//#if !polish.blackberry || polish.hasTrackBallEvents
					|| (gameAction == Canvas.RIGHT && keyCode != Canvas.KEY_NUM6)
				//#endif
			) {
				return notifyItemPressedStart();
			}
		} else if ( isFireGameAction && this.appearanceMode != PLAIN ){ // has no children:
			// press this item:
			return notifyItemPressedStart();
		}
		return super.handleKeyPressed(keyCode, gameAction);
	}
	
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handleKeyReleased(int, int)
	 */
	protected boolean handleKeyReleased(int keyCode, int gameAction) {
		//#debug
		System.out.println( this + " handleKeyReleased, isOpen=" + this.isOpen + ", isPressed=" + this.isPressed);
		if (!this.isPressed && !this.isOpen) {
			return super.handleKeyReleased(keyCode, gameAction);
		}
		if (this.isOpen) {
			if (
				//#if !polish.blackberry || polish.hasTrackBallEvents
					(gameAction == Canvas.LEFT && keyCode != Canvas.KEY_NUM4)
				//#else
					//# false
				//#endif
			) {
				// close menu:
				open( false );
				notifyItemPressedEnd();
			} else {
				boolean handled = this.children.handleKeyReleased(keyCode, gameAction);
				if (!handled) {
					 if (keyCode >= Canvas.KEY_NUM1 && keyCode <= Canvas.KEY_NUM9) {
						int index = keyCode - Canvas.KEY_NUM1;
						if (index <= this.children.size()) {
							CommandItem item = (CommandItem) this.children.get(index);
							if (item.appearanceMode != Item.PLAIN) {
								item.notifyItemPressedEnd();
								item.handleKeyReleased(0, Canvas.FIRE);
								return true;
							}
						}
						return false;
					} else if (
							(gameAction == Canvas.UP || gameAction == Canvas.DOWN)
                        //#if polish.blackberry && !polish.hasTrackBallEvents
                            || (gameAction == Canvas.RIGHT || gameAction == Canvas.LEFT)
                        //#endif
					) {
						return false;
					}
					open(false);
					notifyItemPressedEnd();
				}
			}
			return true;
		}
		else
		{
			boolean isFireGameAction = getScreen().isGameActionFire(keyCode, gameAction);
			if ( this.hasChildren && this.appearanceMode != PLAIN ) { // has children but is not open
				if ( isFireGameAction 
					//#if !polish.blackberry || polish.hasTrackBallEvents
						|| (gameAction == Canvas.RIGHT && keyCode != Canvas.KEY_NUM6)
					//#endif
				) {
					notifyItemPressedEnd();
					open( true );
					return true;
				}
			} else if (  isFireGameAction && this.appearanceMode != PLAIN ){ // has no children:
				notifyItemPressedEnd();
				// fire command action event:
				//#debug
				System.out.println( this + " invoking command " + this.command.getLabel() + " on screen " + getScreen() );
				Screen scr = getScreen();
				//#if polish.debug.error
				if (scr == null) {
					//#debug error
					System.out.println("Unable to retrieve screen for " + this + ", parent=" + this.parent );
				}
				//#endif
				scr.callCommandListener( this.command );
				return true;
			}
		}
		return super.handleKeyReleased(keyCode, gameAction);
	}

	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handlePointerPressed(int, int)
	 */
	protected boolean handlePointerPressed(int x, int y) {
		//#debug
		System.out.println("handlePointerPressed( " + x + ", " + y + ") for " + this);
		boolean handled = false;
		if (this.isOpen) {
			this.children.handlePointerPressed(x - this.children.relativeX, y - this.children.relativeY );
			// handle press events in any case since we close the children in release when the event is outside of the children's area:
			handled = true;
		}
		return handled || super.handlePointerPressed(x, y);
	}
	//#endif
	
	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handlePointerReleased(int, int)
	 */
	protected boolean handlePointerReleased(int x, int y) {
		//#debug
		System.out.println("handlePointerReleased( " + x + ", " + y + ") for " + this);
		boolean handled = false;
		if (this.isOpen) {
			int relX = x - this.children.relativeX;
			int relY = y - this.children.relativeY;
			handled = this.children.handlePointerReleased(relX, relY );
			if (!handled || !this.children.isInItemArea( relX, relY)) {
				open(false);
				handled = true;
			}
		}
		return handled || super.handlePointerReleased(x, y);
	}
	//#endif

	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handlePointerDragged(int, int, ClippingRegion)
	 */
	protected boolean handlePointerDragged(int x, int y, ClippingRegion repaintRegion) {
		//#debug
		System.out.println("handlePointerDragged( " + x + ", " + y + ") for " + this);
		boolean handled = false;
		if (this.isOpen) {
			handled = this.children.handlePointerDragged(x - this.children.relativeX, y - this.children.relativeY, repaintRegion );
		}
		return handled || super.handlePointerDragged(x, y, repaintRegion);
	}
	//#endif

	/**
	 * Opens or closes this command item so that the children commands are visible (or not).
	 * 
	 * @param open true when the children should be visible and the focus is moved to them.
	 */
	public void open(boolean open) {
		//#debug
		System.out.println( this + ": opening children: " + open);
		//try { throw new RuntimeException(); } catch (Exception e) { e.printStackTrace(); }
		this.isOpen = open;
		if ( open ) {
			// move focus to first child:
			this.children.showNotify();
			this.children.focus( getFocusedStyle(), 0 );
			this.children.focusChild( 0 );
			//#if polish.CommandItem.showCommand
			removeCommandFromScreen();
			//#endif
		} else {
			// focus myself:
			// reset selected command element to the first one in the list:
			if ( this.children != null ) {
				this.children.hideNotify();
				this.children.focusChild( -1 );
				this.children.setScrollYOffset( 0 );
			}
			//#if polish.CommandItem.showCommand
			addCommandToScreen();
			//#endif
		}
	}
	

	/**
	 * Retrieves the child item for the specified command.
	 * 
	 * @param parentCommand the corresponding command
	 * @return the corresponding item or null when the child is not found
	 */
	public CommandItem getChild(Command parentCommand) {
		if (!this.hasChildren) {
			return null;
		}
		for ( int i=0; i<this.children.size(); i++ ) {
			CommandItem child = (CommandItem) this.children.get(i);
			if ( child.command == parentCommand ) {
				return child;
			} else if (child.hasChildren) {
				child = child.getChild(parentCommand);
				if (child != null) {
					return child;
				}
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.IconItem#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		super.setStyle(style);
		//#if polish.css.command-child-indicator
			if (this.hasChildren) {
				String childIndicatorUrl = style.getProperty( "command-child-indicator" );
				if (childIndicatorUrl != null) {
					try {
						this.childIndicator = StyleSheet.getImage(childIndicatorUrl, this, true);
						this.childIndicatorWidth = this.childIndicator.getWidth();
						this.childIndicatorHeight = this.childIndicator.getHeight();
					} catch (IOException e) {
						//#debug error
						System.out.println("Unable to load command-child-indicator[ " + childIndicatorUrl + "] " + e );
					}
				}
			}
			if (this.hasChildren && this.childIndicator == null) {
		//#endif			
				// use font height for the child indicator width and height
				if (this.font == null) {
					this.font = Font.getDefaultFont();
				}
				if (this.childIndicatorWidth == 0) {
					this.childIndicatorWidth = this.font.getHeight();
					this.childIndicatorHeight = this.childIndicatorWidth;
				}
		//#if polish.css.command-child-indicator
			}
		//#endif
		
	}
	

	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.StringItem#setStyle(de.enough.polish.ui.Style, boolean)
	 */
	public void setStyle(Style style, boolean resetStyle)
	{
		super.setStyle(style, resetStyle);
		//#if polish.css.command-child-indicator-color
			Integer childIndicatorColorInt = style.getIntProperty("command-child-indicator-color");
			if ( childIndicatorColorInt != null ) {
				this.childIndicatorColor = childIndicatorColorInt.intValue();
			}
		//#endif
		//#if polish.css.command-child-indicator-width
			Integer childIndicatorWidthInt = style.getIntProperty("command-child-indicator-width");
			if ( childIndicatorWidthInt != null ) {
				this.childIndicatorWidth = childIndicatorWidthInt.intValue();
			}
		//#endif
		//#if polish.css.command-child-indicator-height
			Integer childIndicatorHeightInt = style.getIntProperty("command-child-indicator-height");
			if ( childIndicatorHeightInt != null ) {
				this.childIndicatorHeight = childIndicatorHeightInt.intValue();
			}
		//#endif
						
		//#ifdef polish.css.suppress-indicator
			Boolean suppressIndicatorBool = style.getBooleanProperty("supress-indicator");
			if(suppressIndicatorBool != null)
			{
				this.suppressIndicator = suppressIndicatorBool.booleanValue();
			}
		//#endif
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Item#onScreenSizeChanged(int, int)
	 */
	public void onScreenSizeChanged(int screenWidth, int screenHeight) {
		super.onScreenSizeChanged(screenWidth, screenHeight);
		if (this.children != null) {
			this.children.onScreenSizeChanged(screenWidth, screenHeight);
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#resetStyle(boolean)
	 */
	public void resetStyle(boolean recursive) {
		super.resetStyle(recursive);
		if (recursive) {
			if (this.children != null) {
				this.children.resetStyle(recursive);
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.IconItem#animate(long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(long currentTime, ClippingRegion repaintRegion)
	{
		super.animate(currentTime, repaintRegion);
		if (this.isOpen && this.isFocused) {
			this.children.animate(currentTime, repaintRegion);
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.StringItem#hideNotify()
	 */
	protected void hideNotify() {
		if (this.hasChildren) {
			this.children.hideNotify();
			if (this.isOpen) {
				open( false );
			}
		}
		super.hideNotify();
	}

	/**
	 * Returns the command for this item.
	 * 
	 * @return the command
	 */
	public Command getCommand() {
		return this.command;
	}

	
	/**
	 * Tells whether this item is open so that the children commands are visible (or not).
	 * 
	 * @return true of open, false otherwise
	 */
	public boolean isOpen() {
		return this.isOpen;
	}

	/**
	 * Retrieves a child item for a given index.
	 * 
	 * @param index the index
	 * @return the command item, or null if no child for this index exists
	 */
	public CommandItem getChild(int index) {
		if (index < 0 || this.children == null || index >= this.children.size() ) {
			return null;
		}
		return (CommandItem) this.children.get(index);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#fireEvent(java.lang.String, java.lang.Object)
	 */
	public void fireEvent(String eventName, Object eventData)
	{
		super.fireEvent(eventName, eventData);
		if (this.children != null) {
			this.children.fireEvent(eventName, eventData);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Item#getItemAt(int, int)
	 */
	public Item getItemAt(int relX, int relY) {
		if (this.isOpen) {
			int itemRelX = relX - this.children.relativeX;
			int itemRelY = relY - this.children.relativeY;
			Item item = this.children.getItemAt(itemRelX, itemRelY);
			if (item != null) {
				return item;
			}
		}
		return super.getItemAt(relX, relY);
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Item#isInItemArea(int, int)
	 */
	public boolean isInItemArea(int relX, int relY) {
		if (this.isOpen) {
			int itemRelX = relX - this.children.relativeX;
			int itemRelY = relY - this.children.relativeY;
			if (this.children.isInItemArea(itemRelX, itemRelY)) {
				return true;
			}
		}
		return super.isInItemArea(relX, relY);
	}

	/**
	 * Focuses the child command item of this CommandItem.
	 * @param item the child command item
	 */
	public void focusChild(Item item) {
		if (this.hasChildren) {
			int index = this.children.indexOf(item);
			if (index != -1) {
				this.children.focusChild(index);
			} else if (this.children.focusedItem != null) {
				((CommandItem)this.children.focusedItem).focusChild(item);
			}
		}
		
	}

	//#if polish.CommandItem.showCommand
	/**
	 * Adds the command to the screen of
	 * the parenting item
	 */
	protected void addCommandToScreen()
	{
		if(this.parent != null)
		{
			this.parent.getScreen().addCommand(getCommand());
		}
	}
	//#endif
	
	//#if polish.CommandItem.showCommand
	/**
	 * Removes the command from the screen of
	 * the parenting item
	 */
	protected void removeCommandFromScreen()
	{
		if(this.parent != null)
		{
			this.parent.getScreen().removeCommand(getCommand());
		}
	}
	//#endif
	
	//#if polish.CommandItem.showCommand
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#focus(de.enough.polish.ui.Style, int)
	 */
	protected Style focus(Style newStyle, int direction) {
		addCommandToScreen();
		return super.focus(newStyle, direction);
	}
	//#endif
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.IconItem#defocus(de.enough.polish.ui.Style)
	 */
	protected void defocus( Style originalStyle ) {
		//#if polish.CommandItem.showCommand
			removeCommandFromScreen();
		//#endif 
		if (this.isOpen) {
			open( false );
		}
		super.defocus(originalStyle);
	}
	
}
