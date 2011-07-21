/*
 * Created on 09.10.2007 at 17:15:37.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package de.enough.sysinfo;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;

import de.enough.polish.util.Locale;

public class UpdateChecker implements Runnable{

	private Display display;
	private MIDPSysInfoMIDlet sysinfoMidlet;
	private Form updateForm;
	
	public UpdateChecker(Display display, MIDPSysInfoMIDlet sysinfoMidlet) {
		super();
		this.display = display;
		this.sysinfoMidlet = sysinfoMidlet;
		
		
		Thread t = new Thread(this);
		t.start();
	}

	public void run() {
		this.updateForm = new Form(Locale.get("msg.wait"));
		this.updateForm.addCommand(this.sysinfoMidlet.backCmd);
		this.updateForm.setCommandListener(this.sysinfoMidlet);
		
		this.display.setCurrent(this.updateForm);
		
		try {
			String url;
			//#if localbuild
				url = "http://localhost:8080/sysinfoprovider?sysinfoversion=send";
			//#else
				url = "http://sysinfo.j2mepolish.org/sysinfoprovider?sysinfoversion=send";
			//#endif
			//System.out.println("url: " + url);
			HttpConnection ic = (HttpConnection) Connector.open(url);
			InputStream is = ic.openInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			StringBuffer res = new StringBuffer();
			char[] response = new char[1024];
			while ( isr.read(response) != -1) {
                res = res.append(response);
            }
			is.close();
			ic.close();
			is = null;
			ic = null;
						
			String localVersion = this.sysinfoMidlet.getAppProperty("MIDlet-Version");
			String remoteVersion = res.toString().trim();
			
			this.updateForm.deleteAll();
			this.updateForm.append(new StringItem(Locale.get("msg.remoteversion"), remoteVersion.toString()));
			this.updateForm.append(new StringItem(Locale.get("msg.localversion"), localVersion.toString()));
						
			Vector localVersionVector = new Vector(localVersion.length());
			Vector remoteVersionVector = new Vector(remoteVersion.length());
			
			StringBuffer tmpBuffer = new StringBuffer();
			for( int i=0; i< localVersion.length(); i++){
				char c = localVersion.charAt(i);
				if( c == '.'){
					localVersionVector.addElement(tmpBuffer.toString());
					tmpBuffer = null;
					tmpBuffer = new StringBuffer();
				}else{
					tmpBuffer.append(c);
				}
				if( i == (localVersion.length() - 1) ){
					localVersionVector.addElement(tmpBuffer.toString());
					tmpBuffer = null;
				}
			}
			
			tmpBuffer = new StringBuffer();
			for( int i=0; i< remoteVersion.length(); i++){
				char c = remoteVersion.charAt(i);
				//
				if( c == '.'){
					remoteVersionVector.addElement(tmpBuffer.toString());
					tmpBuffer = null;
					tmpBuffer = new StringBuffer();
				}else{
					tmpBuffer.append(c);
				}
				if( i == (remoteVersion.length() - 1) ){
					remoteVersionVector.addElement(tmpBuffer.toString());
					tmpBuffer = null;
				}
			}
						
			boolean updateAbvailable = false;
			int remoteLength = remoteVersionVector.size();
			int localLength = localVersionVector.size();
			
			try{
				for (int i = 0; i < remoteLength; i++) {
					if( i <= localLength){				
						int localSub = Integer.valueOf((String) localVersionVector.elementAt(i)).intValue();
						int remoteSub = Integer.valueOf((String) remoteVersionVector.elementAt(i)).intValue();
						
						if( remoteSub >  localSub ){
							updateAbvailable = true;
							break;
						}
					}else{
						updateAbvailable = true;
						break;
					}
				}
			}catch (Exception e) {
				/* e.printStackTrace(); */
			}
			
			if(updateAbvailable){
				this.updateForm.append(new StringItem(null, Locale.get("msg.updateavailable")));
				this.updateForm.append(new StringItem(null, Locale.get("msg.updatevisitaddress")));
			}else{
				this.updateForm.append(new StringItem(null, Locale.get("msg.updatenotavailable")));	
			}
			this.display.setCurrent(this.updateForm);			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
