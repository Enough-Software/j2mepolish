//#condition polish.usePolishGui
/*
 * Copyright (c) 2009 Robert Virkus / Enough Software
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

package de.enough.polish.sample.rmi.test;

import javax.microedition.lcdui.Image;

import de.enough.polish.rmi.RemoteException;
import de.enough.polish.sample.rmi.DuplicateUserException;
import de.enough.polish.sample.rmi.GameHighscore;
import de.enough.polish.sample.rmi.GameServer;
import de.enough.polish.sample.rmi.GameUser;

/**
 * Provides a testing possibility.
 * To use this mockup set the build.xml variable
 * &quot;polish.rmi.mockup.de.enough.polish.sample.rmi.GameServer&quot; to &quot;new de.enough.polish.sample.rmi.test.MockupGameServer()&quot;
 * 
 * @author Robert Virkus
 *
 */
public class MockupGameServer implements GameServer {

	/**
	 * Creates a new mockup server
	 */
	public MockupGameServer() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.sample.rmi.GameServer#getAllUsers()
	 */
	public GameUser[] getAllUsers() throws RemoteException {
		return new GameUser[]{
			new GameUser( 1232L, "test user", 120 )	
		};
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.sample.rmi.GameServer#ping(long)
	 */
	public boolean ping(long time) throws RemoteException {
		return true;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.sample.rmi.GameServer#registerUser(long, java.lang.String, java.lang.String)
	 */
	public GameUser registerUser(long time, String userName, String password)
			throws RemoteException, DuplicateUserException {
		return new GameUser( System.currentTimeMillis(), userName, 1000 );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.sample.rmi.GameServer#storeHighscore(de.enough.polish.sample.rmi.GameHighscore, boolean)
	 */
	public GameHighscore storeHighscore(GameHighscore highscore, boolean flag)
			throws RemoteException {
		return new GameHighscore( "Winner", new int[]{ 1000, 900, 500} );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.sample.rmi.GameServer#uploadScreenShot(javax.microedition.lcdui.Image)
	 */
	public void uploadScreenShot(Image image) throws RemoteException {
		// ignore
	}

}
