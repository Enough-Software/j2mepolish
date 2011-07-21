//#condition polish.api.btapi
/*
 * Created on Jul 29, 2008 at 12:41:23 PM.
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
package de.enough.polish.bluetooth;

import javax.bluetooth.L2CAPConnection;

/**
 * <p>Defines constants that are used by both input and output stream</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public interface L2CapStream
{
	/**
	 * Error code for not receiving a confirmation from the input stream that the connection now can be closed.
	 * The value of this code is 1.
	 */
	int ERROR_NO_EOF_CONFIRMATION_FROM_INPUT_STREAM = 1;
	
	/** magic sequence for detecting eof streams */
	byte[] EOF_SEQUENCE = new byte[]{ 74, 50, 77, 69, 1, 2, 3 };
	
	/** The minimum transfer size for byte arrays is 48 */
	int MINIMUM_MTU = L2CAPConnection.MINIMUM_MTU;

}
