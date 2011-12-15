//#condition polish.usePolishGui && polish.blackberry

/*
 * Copyright (c) 2005 - 2011 Robert Virkus / Enough Software
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
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.blackberry.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import de.enough.polish.blackberry.midlet.MIDlet;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;
import de.enough.polish.util.ArrayList;

public final class BaseScreenManager extends Manager {
	
	private static BaseScreenManager instance;
	public static BaseScreenManager getInstance() {
		if (instance == null) {
			instance = new BaseScreenManager();
		}
		return instance;
	}


	private BaseScreen screen;
	private ArrayList permanentItems;

	private BaseScreenManager() {
		super(Manager.NO_HORIZONTAL_SCROLL | Manager.NO_VERTICAL_SCROLL);
	}
	
	public void setBaseScreen( BaseScreen screen ) {
		this.screen = screen;
	}
	
	public void addPermanentNativeItem( Item item ) {
		if (item._bbField == null) {
			throw new IllegalArgumentException();
		}
		if (this.permanentItems == null) {
			this.permanentItems = new ArrayList();
		} else if (this.permanentItems.contains(item)) {
			// has been added already:
			return;
		}
		this.permanentItems.add(item);
        Object lock = MIDlet.getEventLock();
        synchronized (lock) {
            add(item._bbField);
        }
	}
	
	public void removePermanentNativeItem(Item item) {
		if (this.permanentItems != null) {
			this.permanentItems.remove(item);
			Field field = item._bbField;
			if (field != null && field.getManager() != null) {
				try {
			        Object lock = MIDlet.getEventLock();
			        synchronized (lock) {
			            delete(field);
			        }
				} catch (Exception e) {
					//#debug error
					System.out.println("Warning: unable to delete native field" + e);
				}
			}
	
		}
		
	}

	
	public void clearPermanentNativeItems() {
		if (this.permanentItems != null) {
			Object[] objects = this.permanentItems.getInternalArray();
			for (int i = 0; i < objects.length; i++) {
				Item item = (Item) objects[i];
				if (item == null) {
					break;
				}
				removePermanentNativeItem(item);
			}
		}
	}
	
	protected void sublayout(int w, int h) {
		Item currentItem =  this.screen.currentItem;
		for (int i=0; i<getFieldCount(); i++) {
			Field field = getField(i);
			if (currentItem != null && field == currentItem._bbField) {
				int itemW = currentItem.getContentWidth();
				if (itemW == 0) {
					itemW = currentItem.itemWidth;
				}
				itemW += 2;
				layoutChild( field, itemW, currentItem.itemHeight);
				setPositionChild( field, currentItem.getAbsoluteX() + currentItem.getContentX(), currentItem.getAbsoluteY() + currentItem.getContentY() );
			} else {
				Item item = getItem( field );
				if (item != null) {
					int itemW = item.getContentWidth();
					if (itemW == 0) {
						itemW = item.itemWidth;
					}
					itemW += 2;
					layoutChild( field, itemW, item.itemHeight);
					//layoutChild( field, w, h);
					setPositionChild( field, item.getAbsoluteX() + item.getContentX(), item.getAbsoluteY() + item.getContentY() );
				} else {
					layoutChild( field, w, h );
				}
			}
		}
		setExtent( w, h );
	}
	
	/**
	 * Calls layout on the specified child
	 * @param child the child field
	 * @param width the available width
	 * @param height the available height
	 */
	public void layoutAndPosition( Item item, int width, int height ) {
		addPermanentNativeItem(item);
		Field field = item._bbField;
		super.layoutChild(field, width, height);
		setPositionChild( field, item.getAbsoluteX() + item.getContentX(), item.getAbsoluteY() + item.getContentY() );
	}
	
	public void position(Item item, int x, int y) {
		Field field = item._bbField;
		setPositionChild( field, x, y);
	}

	
	

	private Item getItem(Field field) {
		if (this.permanentItems == null) {
			return null;
		}
		Object[] objects = this.permanentItems.getInternalArray();
		for (int i = 0; i < objects.length; i++) {
			Item item = (Item) objects[i];
			if (item == null) {
				break;
			}
			if (item._bbField == field) {
				return item;
			}
		}
		return null;
	}

	
	//#if !polish.useFullScreen
    protected void paint( net.rim.device.api.ui.Graphics g ) {
	    // when extending the BB MainScreen, super.paint(g) will
    	// clear the paint area, subpaint(g) will only render the fields.
	    subpaint(g);
    }
    //#endif
	
	protected void subpaint(net.rim.device.api.ui.Graphics g) {
		BaseScreen baseScreen = this.screen;
		Screen polishScreen = baseScreen.getPolishScreen();
		if (polishScreen == null || !polishScreen.isMenuOpened()) {
			Item currentItem = baseScreen.currentItem;
			if (currentItem != null) {
				Item parent = currentItem;
				if (polishScreen != null) {
					while (parent.getParent() != null) {
						parent = parent.getParent();
					}
					if (parent == polishScreen.getRootContainer()) {
						int x = polishScreen.getScreenContentX();
						int y = polishScreen.getScreenContentY();
						int width = polishScreen.getScreenContentWidth();
						int height = polishScreen.getScreenContentHeight();
						g.pushContext(x - g.getTranslateX(), y - g.getTranslateY(), width, height, 0, 0 );
					} else {
						polishScreen = null; 
					}
				}
				Field field = currentItem._bbField;
				paintChild( g, field );
				if (polishScreen != null) {
					g.popContext();
				}
			}
			if (this.permanentItems != null) {
				Object[] objects = this.permanentItems.getInternalArray();
				for (int i = 0; i < objects.length; i++) {
					Item item = (Item) objects[i];
					if (item == null) {
						break;
					}
					paintChild( g, item._bbField);
				}			
			}
		}
	}

	
}