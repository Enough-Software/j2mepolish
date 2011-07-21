/*
 * Created on 26-Jan-2006 at 11:42:27.
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
package de.enough.polish.ide.swing;

import javax.swing.Icon;
import javax.swing.JCheckBox;

import de.enough.polish.devices.PolishComponent;

/**
 * <p>Is used for selecting configurations.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        26-Jan-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class PolishComponentCheckBox extends JCheckBox {

	private static final long serialVersionUID = 5197073517633487420L;
	private final PolishComponent component;

	/**
	 * @param component the component
	 */
	public PolishComponentCheckBox( PolishComponent component ) {
		this( component, null, false );
	}

	/**
	 * @param component the component
	 */
	public PolishComponentCheckBox( PolishComponent component, boolean isSelected ) {
		this( component, null, isSelected );
	}

	
	/**
	 * @param component the component
	 * @param icon
	 */
	public PolishComponentCheckBox( PolishComponent component, Icon icon ) {
		this( component, icon, false );
	}

	/**
	 * @param component the component
	 * @param icon
	 * @param isSelected true when the box is selected
	 */
	public PolishComponentCheckBox( PolishComponent component, Icon icon, boolean isSelected ) {
		super( component.toString(), icon, isSelected );
		this.component = component;
	}

	/**
	 * @return Returns the component.
	 */
	public PolishComponent getComponent() {
		return this.component;
	}

	public String getDescription() {
		if (this.component == null) {
			return "";
		}
		return this.component.getDescription();
	}


}
