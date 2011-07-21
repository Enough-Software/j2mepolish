/*
 * Created on 28-Apr-2005 at 02:30:55.
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
package de.enough.polish.ant.build;

import java.io.File;

import de.enough.polish.BuildException;

/**
 * <p>Is used to configure the automatic signing of MIDlets.</p>
 * <p>You can use the sign task using the &lt;sign&gt; element within the &lt;build&gt; section: 
 * <pre>
 * &lt;sign
 * 	keystore=&quot;midlets.ks=&quot;
 * 	key==&quot;MyKey=&quot;
 * 	password=&quot;${pw}=&quot;
 * /&gt;
 * </pre>
 * </p>
 * 
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        28-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SignSetting extends FinalizerSetting {
	
	private File keystore;
	private String key;
	private String password;
	private boolean skipCertificate;

	/**
	 * Creates a new sign setting
	 */
	public SignSetting() {
		super();
		super.setName( "sign" );
		setIf( "polish.midp2" );
	}
	
	public void setName( String name ) {
		throw new BuildException("You must not set the name of the <sign> element. Use <finalizer> for more flexibility.");
	}

	/**
	 * @return Returns the key.
	 */
	public String getKey() {
		return this.key;
	}
	/**
	 * @param key The key to set.
	 */
	public void setKey(String key) {
		this.key = key;
	}
	/**
	 * @return Returns the keystore.
	 */
	public File getKeystore() {
		return this.keystore;
	}
	/**
	 * @param keystore The keystore to set.
	 */
	public void setKeystore(File keystore) {
		this.keystore = keystore;
	}
	/**
	 * @return Returns the password.
	 */
	public String getPassword() {
		return this.password;
	}
	/**
	 * @param password The password to set.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return Returns the skipCertificate.
	 */
	public boolean isSkipCertificate() {
		return this.skipCertificate;
	}

	/**
	 * @param skipCertificate The skipCertificate to set.
	 */
	public void setSkipCertificate(boolean skipCertificate) {
		this.skipCertificate = skipCertificate;
	}
}
