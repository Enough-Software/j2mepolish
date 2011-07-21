//#condition polish.usePolishGui
/*
 * Created on 30-Dez-2005 at 16:32:21.
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
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.util.ArrayList;

/**
 * <p>Provides a tree of items that can contain several branches.</p>
 * <p>Each tree branch behaves like a normal J2ME Polish container, so 
 *    you can specify view-types, columns, colspans, etc.
 * </p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2009</p>
 * <pre>
 * history
 *        16-Feb-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TreeItem 
//#if polish.LibraryBuild
	extends FakeContainerCustomItem
//#else
	//# extends Container
//#endif
 
{
	
	//#if polish.css.treeitem-closed-indicator && polish.css.treeitem-opened-indicator
		//#define tmp.useIndicators
		private Image closedIndicator;
		private Image openedIndicator;
		private int indicatorWidth;
	//#endif
	private Style nodeStyle;
	private Object focusPathKey;
	private Object[] focusPathValues;
	private TreeModel treeModel;
	public ArrayList treeModelChildrenList;

	
	/**
	 * Creates a new tree item.
	 * 
	 * @param label the label of this item
	 */
	public TreeItem(String label ) {
		this( label, (Style)null );
	}

	/**
	 * Creates a new tree item.
	 * 
	 * @param label the label of this item
	 * @param style the style
	 */
	public TreeItem(String label, Style style) {
		super( false, style );
		setLabel( label );
	}
	
	/**
	 * Creates a new tree item.
	 * 
	 * @param label the label of this item
	 * @param model the data model for this tree
	 */
	public TreeItem(String label, TreeModel model ) {
		this( label, model, null );
	}

	/**
	 * Creates a new tree item.
	 * 
	 * @param label the label of this item
	 * @param model the data model for this tree
	 * @param style the style
	 */
	public TreeItem(String label, TreeModel model, Style style) {
		super( false, style );
		setLabel( label );
		this.treeModel = model;
		if (model != null) {
			this.treeModelChildrenList = new ArrayList();
			Object root = model.getRoot();
			if (root != null) {
				ArrayList list = this.treeModelChildrenList;
				model.addChildren(root, list);
				for (int i=0; i<list.size(); i++) {
					Object node = list.get(i);
					if (node instanceof Item) {
						appendToRoot((Item)node);
					} else if (node != null) {
						IconItem item = new IconItem( node.toString(), null );
						item.setAttribute(this, node);
						appendToRoot( item );
					}
				}
			}
		}
	}
	
	//#if polish.LibraryBuild
	/**
	 * Adds the specified item to this tree.
	 * 
	 * @param item the item that should be added
	 */
	public void appendToRoot( javax.microedition.lcdui.Item item ) {
		// ignore, only for the users
	}
	//#endif
	
	//#if polish.LibraryBuild
	/**
	 * Adds the specified item to this tree.
	 * 
	 * @param node the parent node that has been previously added to this tree
	 * @param item the item that should be added
	 */
	public void appendToNode( javax.microedition.lcdui.Item node, javax.microedition.lcdui.Item item ) {
		// ignore, only for the users
	}
	//#endif

	//#if polish.LibraryBuild
	/**
	 * Adds the specified text/image to this tree.
	 * 
	 * @param node the parent node that has been previously added to this tree
	 * @param text the text 
	 * @param image the image
	 * @return the created item
	 */
	public javax.microedition.lcdui.Item appendToNode( javax.microedition.lcdui.Item node, String text, Image image ) {
		// ignore, only for the users
		return null;
	}
	//#endif


	//#if polish.LibraryBuild
	/**
	 * Removes the specified item from this list.
	 * 
	 * @param item the item that should be removed
	 * @return true when the item was contained in this list.
	 */
	public boolean remove( javax.microedition.lcdui.Item item ) {
		// ignore, only for the users
		return false;
	}
	//#endif
	

	/**
	 * Appends the specified text and image to this list.
	 * 
	 * @param text the text
	 * @param image the image
	 * @return the created item
	 */
	//#if polish.LibraryBuild
	public javax.microedition.lcdui.Item appendToRoot( String text, Image image ) {
		return null;
	//#else
		//# public Item appendToRoot( String text, Image image ) {
			//# return appendToRoot( text, image, null );
	//#endif
	}

	/**
	 * Appends the specified text and image to this list and provides it with the given style.
	 * 
	 * @param text the text
	 * @param image the image
	 * @param rootStyle the style
	 * @return return the created item
	 */
	//#if polish.LibraryBuild
	public javax.microedition.lcdui.Item appendToRoot( String text, Image image, Style rootStyle ) {
	//#else
		//# public Item appendToRoot( String text, Image image, Style rootStyle ) {
	//#endif
		IconItem item = new IconItem( text, image, rootStyle );
		appendToRoot( item );
		//#if polish.LibraryBuild
			return null;
		//#else
			//# return item;
		//#endif
	}

	/**
	 * Adds the specified item to this list.
	 * 
	 * @param item the item that should be added
	 */
	public void appendToRoot( Item item ) {
		add(item);
		if (this.treeModel != null) {
			Object data = item.getAttribute(this);
			if (data == null) {
				data = item;
			}
			if (!this.treeModel.isLeaf(data)) {
				convertToNode(item);
			}
		}
//		this.lastAddedItem = item;
	}

	/**
	 * Adds the specified item to this list.
	 * 
	 * @param item the item that should be added
	 * @param childStyle the style
	 */
	public void appendToRoot( Item item, Style childStyle ) {
		if (childStyle != null) {
			item.setStyle( childStyle );
		}
		add(item);
//		this.lastAddedItem = item;
	}
	
	/**
	 * Adds the specified text/image to this tree.
	 * 
	 * @param node the parent node that has been previously added to this tree
	 * @param text the text 
	 * @param image the image
	 * @return the created item
	 */
	public Item appendToNode( Item node, String text, Image image ) {
		return appendToNode(node, text, image, null);
	}

	/**
	 * Adds the specified text/image to this tree.
	 * 
	 * @param node the parent node that has been previously added to this tree
	 * @param text the text 
	 * @param image the image
	 * @param childStyle the style
	 * @return the created item
	 */
	public Item appendToNode( Item node, String text, Image image, Style childStyle ) {
		IconItem item = new IconItem( text, image);
		appendToNode( node, item, childStyle );
		return item;
	}

	/**
	 * Adds the specified item to this tree.
	 * 
	 * @param node the parent node that has been previously added to this tree
	 * @param item the item that should be added
	 */
	public void appendToNode( Item node, Item item ) {
		appendToNode(node, item, null);
	}
	
	/**
	 * Converts a TreeItem into a node (if not realized before)
	 * @param treeElement an item within this tree
	 * @return the parent node for the the TreeItem
	 */
	protected Node convertToNode( Item treeElement) {
		Node parentNode;
		if ( !(treeElement.parent instanceof Node) ) {
			// the item has to be converted into a node:
			Container parentContainer;
			//#if polish.LibraryBuild
				if ( (Object)treeElement.parent == this) {
					// this is a root item:
					parentContainer = (Container) ((Object)this);
			//#else
				//# if (treeElement.parent == this) {
				// this is a root item:
				//# parentContainer = this;
			//#endif
			} else {
				parentContainer = (Container) treeElement.parent;
			}
			parentNode = new Node( treeElement, this.nodeStyle );
			Item[] myItems = parentContainer.getItems();
			for (int i = 0; i < myItems.length; i++) {
				Item rootItem = myItems[i];
				if ( treeElement == rootItem ) {
					parentContainer.set(i, parentNode);
					treeElement.parent = parentNode;
					break;
				}
			}
		} else {
			parentNode = ((Node)treeElement.parent);
		}
		return parentNode;
	}
	
	/**
	 * Adds the specified item to this tree.
	 * 
	 * @param node the parent node that has been previously added to this tree
	 * @param item the item that should be added
	 * @param childStyle the style
	 */
	public void appendToNode( Item node, Item item, Style childStyle  ) {
		if (childStyle != null) {
			item.setStyle( childStyle );
		}
		// find correct Node:
		Node parentNode = convertToNode( node );
		item.parent = parentNode;
		parentNode.addChild(item);
//		this.lastAddedItem = item;
		
	}

	
	/**
	 * Clears this list.
	 */
	public void removeAll() {
		clear();
	}
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.FakeContainerCustomItem#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		super.setStyle(style);
		//#if tmp.useIndicators
			String closedUrl = style.getProperty("treeitem-closed-indicator");
			if (closedUrl != null) {
				try {
					this.closedIndicator = StyleSheet.getImage(closedUrl, this, true );
				} catch (IOException e) {
					//#debug error
					System.out.println("Unable to load treeitem-closed-indicator " + closedUrl + e );
				}
			}
			String openedUrl = style.getProperty("treeitem-opened-indicator");
			if (openedUrl != null) {
				try {
					this.openedIndicator = StyleSheet.getImage(openedUrl, this, true );
				} catch (IOException e) {
					//#debug error
					System.out.println("Unable to load treeitem-opened-indicator " + openedUrl + e );
				}
			}
		//#endif
		//#if polish.css.treeitem-node-style
			Style nodeStyleObj = (Style) style.getObjectProperty("treeitem-node-style");
			if (nodeStyleObj != null) {
				this.nodeStyle = nodeStyleObj;
			}
			Object[] items = this.itemsList.getInternalArray();
			for (int i = 0; i < items.length; i++)
			{
				Item item = (Item) items[i];
				if (item == null) {
					break;
				}
				if (item instanceof Node) {
					((Node)item).children.setStyle(nodeStyleObj);
				}
				
			}
		//#endif
	}



	/**
	 * Retrieves the currently selected path of this tree item.
	 * 
	 * @return an array that contains all selected items
	 */
	public Item[] getSelectedPath()
	{
		ArrayList list = new ArrayList();
		Item current = getFocusedItem();
		while (current != null) {
			Item next = null;
			if (current instanceof Node) {
				Node node = (Node)current;
				current = node.root;
				next = node.children.getFocusedItem();
			} else if (current instanceof Container) {
				next = ((Container)current).getFocusedItem();
			}
			list.add( current );
			current = next;
			
		}
		return (Item[]) list.toArray( new Item[ list.size() ] );
	}

	/**
	 * Retrieves the currently selected path of this tree item.
	 * 
	 * @param key the key that is used for querying attributes from the focused item
	 * @return an array that contains all attribute values of the selected items. 
	 * When an item has not registered the specified attribute, the item itself will be put into that array slot. 
	 * @see UiAccess#setAttribute(Item, Object, Object)
	 * @see UiAccess#getAttribute(Item, Object)
	 */
	public Object[] getSelectedPathAsAttributes( Object key )
	{
		Object[] items = getSelectedPath();
		for (int i = 0; i < items.length; i++)
		{
			Item item = (Item) items[i];
			Object value = item.getAttribute(key);
			if (value != null) {
				items[i] = value;
			}
		}
		return items;
	}


	/**
	 * Opens the tree and focuses the specified items.
	 * All other branches are collapsed while the specified path(s) is opened.
	 * When several paths do match, the last matching path is focused.
	 * @param key the attribute key
	 * @param values the values that are set for each item / node
	 * @see UiAccess#setAttribute(Item, Object, Object)
	 * @see UiAccess#getAttribute(Item, Object)
	 */
	public void setSelectedPathByAttribute(Object key, Object[] values)
	{
		if (!this.isFocused) {
			this.focusPathKey = key;
			this.focusPathValues = values;
		} else {
			setSelectedPathByAttribute( key, values, (Container)(Object)this, 0 );
		}
	}
	
	private void setSelectedPathByAttribute( Object key, Object[] values, Container container, int index ) {
		Object[] items = container.itemsList.getInternalArray();
		Object valueExpected = values[index];
		if (container.isFocused) {
			container.focusChild(-1);
		}
		for (int i = 0; i < items.length; i++)
		{
			Item item = (Item) items[i];
			if (item == null) {
				break;
			}
			Node node = null;
			if (item instanceof Node) {
				node = (Node)item;
				item = node.root;
				if (node.isExpanded) {
					node.setExpanded( false );
				}
			}
			Object valuePresent = item.getAttribute(key);
			
			if (valueExpected.equals(valuePresent)) {
				if (node != null) {
					node.setExpanded(true);
					container.focusChild( i, node, Canvas.UP, true );
				} else {
					container.focusChild( i, item, 0, true );
				}
				if (index < values.length - 1) {
					if (node != null) {
						setSelectedPathByAttribute(key, values, node.children, index + 1);
					} else if (item instanceof Container) {
						setSelectedPathByAttribute(key, values, (Container)item, index + 1);
					}
				}
//			} else if (node != null && node.isExpanded) {
//				node.setExpanded(false);
			}
		}

	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.FakeContainerCustomItem#focus(de.enough.polish.ui.Style, int)
	 */
	protected Style focus(Style focusStyle, int direction)
	{
		Style myPlainStyle = super.focus(focusStyle, direction);
		if (this.focusPathKey != null) {
			setSelectedPathByAttribute(this.focusPathKey, this.focusPathValues, (Container)(Object)this, 0 );
			this.focusPathKey = null;
			this.focusPathValues = null;
		}
		return myPlainStyle;
	}

	/**
	 * Collapses all branches of this TreeItem.
	 */
	public void collapseAll()
	{
		Object[] items = this.itemsList.getInternalArray();
		for (int i = 0; i < items.length; i++)
		{
			Object item = items[i];
			if (item == null) {
				break;
			}
			if (item instanceof Node) {
				Node node = (Node)item;
				if (node.isExpanded) {
					node.setExpanded(false);
				}
			}
		}
	}



	class Node extends Item {
		private final Item root;
		private final Container children;
		private boolean isExpanded;
		int xLeftOffset = 10;
		private Style rootPlainStyle;
		//private int availableWidth;
		
		/**
		 * Creates a new node without children style
		 * 
		 * @param root the root element
		 */
		public Node( Item root ) {
			this( root, null );
		}
		
		/**
		 * Creates a new node without children style
		 * 
		 * @param root the root element
		 * @param childrenStyle the style for the children container
		 */
		public Node( Item root, Style childrenStyle ) {
			super( null, 0, INTERACTIVE, null );
			this.root = root;
			this.root.parent = this;
			this.children = new Container( false, childrenStyle );
			this.children.parent = this;
		}

		/**
		 * Adds a child to this note
		 * @param child the child
		 */
		public void addChild( Item child ) {
			this.children.add( child );
		}

		protected void initContent(int firstLineWidth, int availWidth, int availHeight) {
			//#debug
			System.out.println("Node (" + this.root + ").initContent()");
			//this.availableWidth = lineWidth - this.xLeftOffset;
			this.root.init(firstLineWidth, availWidth, availHeight);
			this.children.relativeX = this.xLeftOffset;
			this.children.relativeY = this.root.itemHeight;
			
			int rootWidth = this.root.itemWidth;
			//#if tmp.useIndicators
				int w = 0;
				if (TreeItem.this.openedIndicator != null) {
					w = TreeItem.this.openedIndicator.getWidth();
				}
				if (TreeItem.this.closedIndicator != null && TreeItem.this.closedIndicator.getWidth() > w) {
					w = TreeItem.this.closedIndicator.getWidth();
				}
				if (w != 0) {
					rootWidth += w + this.paddingHorizontal;
				}
				TreeItem.this.indicatorWidth = w;
			//#endif
			if (!this.isExpanded) {
				this.contentWidth = rootWidth;
				this.contentHeight = this.root.itemHeight;
			} else {
				availWidth -= this.xLeftOffset;
				this.children.init(availWidth, availWidth, availHeight);
				this.contentWidth = Math.max(rootWidth, this.children.itemWidth + this.xLeftOffset);
				this.contentHeight = this.root.itemHeight + this.paddingVertical + this.children.itemHeight;
			}
		}

		protected void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
			//#if tmp.useIndicators
				Image image;
				if (this.isExpanded) {
					image = TreeItem.this.openedIndicator;
				} else {
					image = TreeItem.this.closedIndicator;
				}
				if (image != null) {
					int height = image.getHeight();
					int rootHeight = this.root.itemHeight;
					g.drawImage(image, x, y + (rootHeight-height)/2, Graphics.TOP | Graphics.RIGHT );
				}
				x += TreeItem.this.indicatorWidth;
			//#endif
//			System.out.println("painting root at " + x + ", " + y + " borders=" + leftBorder + ", " + rightBorder + ", root=" + this.root);
			this.root.paint(x, y, leftBorder, rightBorder, g);
			if (this.isExpanded) {
				leftBorder += this.xLeftOffset;
				x = leftBorder;
				y += this.root.itemHeight + this.paddingVertical;
				this.children.paint(x, y, leftBorder, rightBorder, g);
			}
//			if (this.internalX != Item.NO_POSITION_SET) {
//				if (this.isExpanded) {
//					y -= this.root.itemHeight + this.paddingVertical;
//				}
//				g.setColor( 0xff0000 );
//				g.drawRect( x + this.internalX, y + this.internalY, this.internalWidth, this.internalHeight );
//			}
		}

		//#ifdef polish.useDynamicStyles	
		protected String createCssSelector() {
			return "node";
		}
		//#endif

		/* (non-Javadoc)
		 * @see de.enough.polish.ui.Item#handleKeyPressed(int, int)
		 */
		protected boolean handleKeyPressed(int keyCode, int gameAction) {
			//#debug
			System.out.println("Node " + this + " handleKeyPressed(" +  keyCode + ", " + gameAction + "): isExpanded=" + this.isExpanded);
			boolean handled = false;
			if (this.isExpanded) {
 				if (this.children.isFocused) {
					handled = this.children.handleKeyPressed(keyCode, gameAction);
//					System.out.println("TreeItem: children handled key pressed: " + handled);
					if (handled) {
						if (this.children.internalX != NO_POSITION_SET) {
							this.internalX = this.children.relativeX + this.children.contentX + this.children.internalX;
							this.internalY = this.children.relativeY + this.children.contentY + this.children.internalY;
							this.internalWidth = this.children.internalWidth;
							this.internalHeight = this.children.internalHeight;
						} else {
							this.internalX = this.children.relativeX;
							this.internalY = this.children.relativeY;
							this.internalWidth = this.children.itemWidth;
							this.internalHeight = this.children.itemHeight;
						}
						//System.out.println("TreeItem: children handled keyPressed, internal area: y=" + this.internalY + ", height=" + this.internalHeight );
					} else if (gameAction == Canvas.UP || gameAction == Canvas.LEFT) {
						// focus this root:
						focusRoot();
						handled = true;
					}
				} else if ((gameAction == Canvas.DOWN || gameAction == Canvas.RIGHT) && this.children.appearanceMode != PLAIN) {
					// move focus to children
					if (this.rootPlainStyle != null) {
						this.root.defocus(this.rootPlainStyle);
					}
					this.children.focus(null, gameAction);
					//this.isChildrenFocused = true;
					handled = true;
//					System.out.println("keyPressed: moving focus to children");
				}

			}
			if (!handled && getScreen().isGameActionFire(keyCode, gameAction) ) {
				handled = this.root.notifyItemPressedStart() || (this.children.size() > 0 || TreeItem.this.treeModel != null);
				if (this.isExpanded) { //will be closed in keyReleased
					this.internalX = 0;
					this.internalY = 0;
					this.internalHeight = this.root.itemHeight;
				} else if (this.children.size() > 0) { // will be opened in keyReleased
					this.internalX = 0;
					this.internalY = 0;
					this.internalHeight = this.root.itemHeight + this.paddingVertical + (this.children.itemHeight != 0 ? this.children.itemHeight : 30);
					//System.out.println("about to open children: internalHeight=" + this.internalHeight);
				}
			}
			return handled;
		}
		
		/* (non-Javadoc)
		 * @see de.enough.polish.ui.Item#handleKeyPressed(int, int)
		 */
		protected boolean handleKeyReleased(int keyCode, int gameAction) {
			//#debug
			System.out.println("Node " + this + " handleKeyReleased(" +  keyCode + ", " + gameAction + "): isExpanded=" + this.isExpanded);
			boolean handled = false;
			if (this.isExpanded) {
 				if (this.children.isFocused) {
					handled = this.children.handleKeyReleased(keyCode, gameAction);
//					System.out.println("children handled keyReleased: " + handled);
					if (handled) {
						if (this.children.internalX != NO_POSITION_SET) {
							this.internalX = this.children.relativeX + this.children.contentX + this.children.internalX;
							this.internalY = this.children.relativeY + this.children.contentY + this.children.internalY;
							this.internalWidth = this.children.internalWidth;
							this.internalHeight = this.children.internalHeight;
						} else {
							this.internalX = this.children.relativeX;
							this.internalY = this.children.relativeY;
							this.internalWidth = this.children.itemWidth;
							this.internalHeight = this.children.itemHeight;
						}
					}
//				} else {
//					System.out.println("keyReleased: Children are not focused");
				}

			}
			if (!handled && getScreen().isGameActionFire(keyCode, gameAction)) {
				this.root.notifyItemPressedEnd();
				setExpanded( !this.isExpanded );
				handled = true;
			}
			return handled;
		}
		
		//#ifdef polish.hasPointerEvents
		/* (non-Javadoc)
		 * @see de.enough.polish.ui.Item#handlePointerPressed(int, int)
		 */
		protected boolean handlePointerPressed(int x, int y) {
			boolean handled = false;
			if (this.isExpanded) {
				handled = this.children.handlePointerPressed(x - this.children.relativeX, y - this.children.relativeY );
			}
			//#debug
			System.out.println("Node: " + this + " handled pointerPressed=" + handled);
			return handled || super.handlePointerPressed(x, y);
		}
		//#endif
		
		//#ifdef polish.hasPointerEvents
		/* (non-Javadoc)
		 * @see de.enough.polish.ui.Item#handlePointerPressed(int, int)
		 */
		protected boolean handlePointerReleased(int x, int y) {
			boolean handled = false;
			if (this.isExpanded) {
				handled = this.children.handlePointerReleased(x - this.children.relativeX, y - this.children.relativeY );
				if (handled) {
					if (!this.children.isFocused) {
						this.children.isFocused = true;
					}
					if (this.rootPlainStyle != null) {
						this.root.setStyle( this.rootPlainStyle );
					}
				} else if ( this.root.isInItemArea(x, y)) {
					if (this.children.isFocused) {
						focusRoot();
					}
					setExpanded( false );
					handled = true;
				}
			}
			//#debug
			System.out.println("Node: " + this + " handled pointerReleased=" + handled);
			return handled || super.handlePointerReleased(x, y);
		}
		//#endif
		
		/* (non-Javadoc)
		 * @see de.enough.polish.ui.Item#focus(de.enough.polish.ui.Style, int)
		 */
		protected Style focus(Style focusstyle, int direction ) {
			//#debug
			System.out.println("focus node " + this.root + ", expanded=" + this.isExpanded);
			this.isFocused = true;
			if ( !this.isExpanded || direction != Canvas.UP 
					|| this.children.size() == 0 || this.children.appearanceMode == PLAIN)
			{
				this.rootPlainStyle  = this.root.focus(null, direction);
				return this.rootPlainStyle;
			}
			// focus one of the expanded children:
			//System.out.println("node " + this + ": forwarding focus event to children, (direction != Canvas.UP)=" + (direction != Canvas.UP));
			this.children.focus(null, direction); 
			return this.root.style;
		}
		
		/* (non-Javadoc)
		 * @see de.enough.polish.ui.Item#defocus(de.enough.polish.ui.Style)
		 */
		protected void defocus(Style originalStyle) {
			this.isFocused = false;
			//if (this.isExpanded && this.isChildrenFocused) {
			if (this.isExpanded && this.children.isFocused) {
				this.children.defocus(originalStyle);
				//this.isChildrenFocused = false;
			} else {
				this.root.defocus( originalStyle );
			}
		}
		
		private void focusRoot() {
			//#debug
			System.out.println("focusing root " + this + ", children.isFocused=" + this.children.isFocused );
			this.internalX = -this.contentX;
			this.internalY = -this.contentY;
			this.internalWidth = this.root.itemWidth + this.contentX;
			this.internalHeight = this.root.itemHeight + this.contentY;
			if (this.children.isFocused) {
				this.children.defocus( null );
				this.children.focusChild( -1 );
				// move focus to root:
				this.root.focus(null, Canvas.UP);
			}
		}
				
		private void setExpanded( boolean expand ) {
			if (!expand) {
				this.internalX = NO_POSITION_SET;
				// close down all children nodes as well when closing:
				Item[] myItems = this.children.getItems();
				for (int i = 0; i < myItems.length; i++) {
					Item item = myItems[i];
					if (item instanceof Node) {
						((Node)item).setExpanded(false);
					}
				}
				this.children.hideNotify();
				if (TreeItem.this.treeModel != null) {
					this.children.clear();
				}
				//if (this.isChildrenFocused) {
				focusRoot();
//			} else if (!this.isExpanded) {
//				// trick so that the parent container can scroll correctly when this node is expanded:
//				this.internalX = 0;
//				this.internalY = 0;
//				this.internalHeight = this.root.itemHeight + this.paddingVertical + (this.children.itemHeight != 0 ? this.children.itemHeight : 30);
			}
			if (expand != this.isExpanded) {
				this.isExpanded = expand;
				if (expand && TreeItem.this.treeModel != null) {
					Object data = this.root.getAttribute(TreeItem.this);
					if (data == null) {
						data = this.root;
					}
					TreeModel model = TreeItem.this.treeModel;
					ArrayList list = TreeItem.this.treeModelChildrenList;
					list.clear();
					model.addChildren(data, list);
					for (int i=0; i<list.size(); i++) {
						Object child = list.get(i);
						Item childItem = null;
						if (child instanceof Item) {
							childItem = (Item)child;
							appendToNode(this.root, childItem);
						} else if (child != null) {
							childItem = new IconItem( child.toString(), null );
							childItem.setAttribute(TreeItem.this, child);
							appendToNode( this.root, childItem );
						}
						if (childItem != null && !model.isLeaf(child)) {
							convertToNode(childItem);
						}
					}
				}
				requestInit();
				if (expand) {
					this.children.showNotify();
				}
			}
		}
		

		//#if polish.debugEnabled
		public String toString() {
			return "Node " + this.root + "/" + super.toString();
		}
		//#endif

		/* (non-Javadoc)
		 * @see de.enough.polish.ui.Item#hideNotify()
		 */
		protected void hideNotify()
		{
			super.hideNotify();
			this.root.hideNotify();
			if (this.isExpanded) {
				this.children.hideNotify();
			}
		}

		/* (non-Javadoc)
		 * @see de.enough.polish.ui.Item#showNotify()
		 */
		protected void showNotify()
		{
			super.showNotify();
			this.root.showNotify();
			if (this.isExpanded) {
				this.children.showNotify();
			}
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
		
	}


}
