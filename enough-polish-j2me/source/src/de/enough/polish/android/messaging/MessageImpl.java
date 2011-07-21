//#condition polish.android
/*
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

package de.enough.polish.android.messaging;

import java.util.Date;

/**
 * Provides basic Message implementation.
 *
 */
public class MessageImpl implements Message {
	
	protected String address;
	protected Date timestamp;

	public MessageImpl( String address, Date timestamp ) {
		this.timestamp = timestamp;
		if (address != null) {
			setAddress(address);
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.android.messaging.Message#getTimestamp()
	 */
	public Date getTimestamp() {
		return this.timestamp;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.android.messaging.Message#getAddress()
	 */
	public String getAddress() {
		return this.address;
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.android.messaging.Message#setAddress(java.lang.String)
	 */
	public void setAddress(String addr) {
		if (addr.startsWith("sms://")) {
			addr = addr.substring( "sms://".length() );
		}
		this.address = addr;
	}

}
