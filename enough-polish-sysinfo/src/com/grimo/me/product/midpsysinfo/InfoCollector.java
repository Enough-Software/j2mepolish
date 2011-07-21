/*
 * Created on 10-Feb-2005 at 23:31:00.
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
package com.grimo.me.product.midpsysinfo;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.StringItem;

import de.enough.polish.util.ArrayList;

/**
 * <p>Collects information.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        10-Feb-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class InfoCollector {
	
	private final ArrayList settings;
	protected InfoForm view;

	/**
	 * Creates a new collector
	 */
	public InfoCollector() {
		super();
		this.settings = new ArrayList();
	}
	
	
	/**
	 * Collects information.
	 * 
	 * @param midlet the parent MIDlet
	 * @param display the display
	 */
	public abstract void collectInfos( MIDPSysInfoMIDlet midlet, Display display );
	
	
	public void addInfo( String name, String value ) {
		this.settings.add( new Info( name, value ));
		if (this.view != null) {
			this.view.append( new StringItem( name, value ));
		}
	}
	
	public Info[] getInfos() {
		return (Info[]) this.settings.toArray( new Info[ this.settings.size() ] );
	}
	
	public void show( Display display, InfoForm infoForm ) {
		this.view = infoForm;
		display.setCurrent( infoForm );
	}


	public String getInfo(String info) {
		if (info == null) {
			return "<unknown>";
		} else {
			return info;
		}
	}
	
	public boolean isFinished() {
		return true;
	}
	

}
