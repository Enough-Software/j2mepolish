/*
 * Created on 10-Feb-2005 at 23:25:59.
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

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;


/**
 * <p>Views collected information in a standard form.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        10-Feb-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class InfoForm extends Form {

	/**
	 * Creates a new viewer.
	 * 
	 * @param title the title of the form
	 * @param infoCollector the collected information for this view.
	 */
	public InfoForm(String title, InfoCollector infoCollector ) {
		super(title);
		Info[] infos = infoCollector.getInfos();
		for (int i = 0; i < infos.length; i++) {
			Info info = infos[i];
			append( new StringItem( info.name, info.value ) );
		}
	}

}
