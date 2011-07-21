/*
 * Created on May 12, 2006 at 4:39:43 PM.
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
package de.enough.polish.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import de.enough.polish.util.base64.Base64;

/**
 * <p>Exports both the private as well as the public key from a J2SE keystore.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        May 12, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class KeyExporter {

	/**
	 * 
	 */
	public KeyExporter() {
		super();
		// TODO rv implement KeyExporter
	}
	
	public void exportPrivateKey() throws NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, KeyStoreException {
		KeyStore ks = KeyStore.getInstance("JKS");
		String fileName = "testkeys";
	 
		char[] passPhrase = "passphrase".toCharArray();
	 
		File certificateFile = new File(fileName);
		FileInputStream fis = new FileInputStream(certificateFile);
		ks.load(fis, passPhrase);
	 
		KeyPair kp = getPrivateKey(ks, "duke", passPhrase);
			
		PrivateKey privKey = kp.getPrivate();
		
		String b64 = Base64.encodeBytes(privKey.getEncoded());
	 
		System.out.println("-----BEGIN PRIVATE KEY-----");
		System.out.println(b64);
		System.out.println("-----END PRIVATE KEY-----");	 

		fis.close();
	}
	 
//	 From http://javaalmanac.com/egs/java.security/GetKeyFromKs.html
	 
   public KeyPair getPrivateKey(KeyStore keystore, String alias, char[] password) {
        try {
            // Get private key
            Key key = keystore.getKey(alias, password);
            if (key instanceof PrivateKey) {
                // Get certificate of public key
                Certificate cert = keystore.getCertificate(alias);
    
                // Get public key
                PublicKey publicKey = cert.getPublicKey();
    
                // Return a key pair
                return new KeyPair(publicKey, (PrivateKey)key);
            }
        } catch (UnrecoverableKeyException e) {
            // Ignore error.
        } catch (NoSuchAlgorithmException e) {
            // Ignore error.
        } catch (KeyStoreException e) {
            // Ignore error.
        }
        return null;
    }
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO rv implement main

	}
}
