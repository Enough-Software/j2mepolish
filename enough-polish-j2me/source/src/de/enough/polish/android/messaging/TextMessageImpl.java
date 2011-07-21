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
 * Provides TextMessage functionalities.
 */
public class TextMessageImpl extends MessageImpl implements TextMessage {
	
	private String payloadText;

	public TextMessageImpl(String address) {
		this( address, null );
	}

	public TextMessageImpl(String address, Date timestamp) {
		super( address, timestamp );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.android.messaging.TextMessage#getPayloadText()
	 */
	public String getPayloadText() {
		return this.payloadText;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.android.messaging.TextMessage#setPayloadText(java.lang.String)
	 */
	public void setPayloadText(String data) {
		this.payloadText = data;
	}

}
