/*
 * Created on 12-Feb-2005 at 17:38:40.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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
package de.enough.polish.swing;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;

/**
 * <p>Provides a menu item that can a have a context.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        12-Feb-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ContextJMenuItem extends JMenuItem {

	private static final long serialVersionUID = -2467077350097887900L;

	private Object context;

	/**
	 * Creates a new empty item
	 */
	public ContextJMenuItem() {
		super();
	}

	/**
	 * Creates a new item
	 * 
	 * @param text the text of the item
	 */
	public ContextJMenuItem(String text) {
		super(text);
	}

	/**
	 * Creates a new item
	 * 
	 * @param text the text of the item
	 * @param context tje context of this item
	 */
	public ContextJMenuItem(String text, Object context) {
		super(text);
		this.context = context;
	}

	
	/**
	 * Creates a new item.
	 * 
	 * @param text the text of the item
	 * @param mnemonic the shortcut for selecting the item
	 */
	public ContextJMenuItem(String text, int mnemonic) {
		super(text, mnemonic);
	}

	/**
	 * Creates a new item.
	 * 
	 * @param a the associated action
	 */
	public ContextJMenuItem(Action a) {
		super(a);
	}

	/**
	 * Creates a new item.
	 * 
	 * @param icon the image of the item
	 */
	public ContextJMenuItem(Icon icon) {
		super(icon);
	}

	/**
	 * Creates a new item.
	 * 
	 * @param text the text of the item
	 * @param icon the image of the item
	 */
	public ContextJMenuItem(String text, Icon icon) {
		super(text, icon);
	}
	
	/**
	 * Sets the context of this item
	 * 
	 * @param context the context
	 */
	public void setContext( Object context ) {
		this.context = context;
	}
	
	/**
	 * Retrieves the context of this item
	 * 
	 * @return the context
	 */
	public Object getContext() {
		return this.context;
	}

}
