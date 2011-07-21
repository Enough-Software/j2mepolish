/*
 * Created on Dec 9, 2007 at 2:50:28 AM.
 * 
 * Copyright (c) 2006 Robert Virkus / Enough Software
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
package de.enough.polish.sample.rmi;

import java.util.Hashtable;

import de.enough.polish.rmi.Remote;
import de.enough.polish.rmi.RemoteException;

/**
 * <p>Provides access to the wordtracker XML-RPC server - for more information please refer to http://www.wordtracker.com/docs/api/ch01.html.</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Dec 09, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public interface WordTracker extends Remote {
	
	public final static String CASE_DISTINCT = "case_distinct";
	public final static String CASE_FOLDED = "case_folded";
	public final static String CASE_SENSITIVE = "case_sensitive";
	public final static String ADULT_ONLY = "adult_only";
	public final static String ADULT_INCLUDE = "include_adult";
	public final static String ADULT_EXCLUDE = "exclude_adult";
	
	public Hashtable get_exact_phrase_popularity( String login, String[] words, String caseMode, boolean b1, boolean b2, String adultMode, int i1, int i2 )
	throws RemoteException;

}
