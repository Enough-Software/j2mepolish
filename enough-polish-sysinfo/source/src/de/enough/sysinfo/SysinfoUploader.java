/*
 * Created on 18.06.2007 at 11:31:53.
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

import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.StringItem;

import com.grimo.me.product.midpsysinfo.DisplayInfoCollector;
import com.grimo.me.product.midpsysinfo.Info;
import com.grimo.me.product.midpsysinfo.LibrariesInfoCollector;
import com.grimo.me.product.midpsysinfo.MultiMediaInfoCollector;
import com.grimo.me.product.midpsysinfo.NormalCanvasTest;
import com.grimo.me.product.midpsysinfo.SysPropCollector;
import com.grimo.me.product.midpsysinfo.SystemInfoCollector;

import de.enough.polish.util.Locale;

/**
 * Collects infos and uploads them to the enough sysinfo database.
 *
 * <p>Copyright Enough Software 2007, 2008</p>
 * <pre>
 * history
 *        18.06.2007 - timon creation
 * </pre>
 * @author Timon Gruetzmacher, timon@enough.de
 */
public class SysinfoUploader implements Runnable{

	private Display display;
	private Hashtable infoTable = new Hashtable();
	private MIDPSysInfoMIDlet sysinfoMidlet;

	/**
	 * key codes to be read
	 */
	private int[] keyCodes = new int[] { Canvas.DOWN, Canvas.UP, Canvas.LEFT,
			Canvas.RIGHT, Canvas.FIRE, Canvas.GAME_A, Canvas.GAME_B,
			Canvas.GAME_C, Canvas.GAME_D };
	/**
	 * key code infos to be set
	 */
	private Info[] keyCodeInfos = new Info[] { new Info("sysinfo.keydown", ""),
			new Info("sysinfo.keyup", ""), new Info("sysinfo.keyleft", ""),
			new Info("sysinfo.keyright", ""), new Info("sysinfo.keyfire", ""),
			new Info("sysinfo.keygamea", ""), new Info("sysinfo.keygameb", ""),
			new Info("sysinfo.keygamec", ""), new Info("sysinfo.keygamed", "") };
	
	
	public SysinfoUploader(Display display, MIDPSysInfoMIDlet sysinfoMidlet) {
		super();
		this.display = display;
		this.sysinfoMidlet = sysinfoMidlet;
		
		Thread t = new Thread(this);
		t.start();
	}


	public void run() {
		//System.out.println("collecting infos...");
		
		/* set a waiting form */
		this.display.setCurrent(this.sysinfoMidlet.waitForm);
		this.sysinfoMidlet.waitForm.append(new StringItem(null, "collecting..."));

		this.infoTable.clear();
		
		/* collect display infos */
		DisplayInfoCollector displayCollector = new DisplayInfoCollector();
		displayCollector.collectInfos(this.sysinfoMidlet, this.display);
		Info[] displayInfos = displayCollector.getInfos();
		for (int i = 0; i < displayInfos.length; i++) {
			if ((displayInfos[i].value.compareTo("<unknown>")) != 0)
				this.infoTable.put(displayInfos[i].name, displayInfos[i].value);
		}
		//System.out.println("collected display infos...");

		/* collect library infos */
		LibrariesInfoCollector lCollector = new LibrariesInfoCollector();
		lCollector.collectInfos(this.sysinfoMidlet, this.display);
		Info[] libInfos = lCollector.getInfos();
		for (int i = 0; i < libInfos.length; i++) {
			if (libInfos[i].value.compareTo("<unknown>") != 0)
				this.infoTable.put(libInfos[i].name, libInfos[i].value);
		}
		//System.out.println("collected library infos...");

		/* collect multimedia infos */
		MultiMediaInfoCollector mmCollector = new MultiMediaInfoCollector();
		mmCollector.collectInfos(this.sysinfoMidlet, this.display);
		Info[] mmInfos = mmCollector.getInfos();
		for (int i = 0; i < mmInfos.length; i++) {
			if (mmInfos[i].value.compareTo("<unknown>") != 0)
				this.infoTable.put(mmInfos[i].name, mmInfos[i].value);
		}
		//System.out.println("collected multimedia infos...");

		/* collect system infos */
		SystemInfoCollector sCollector = new SystemInfoCollector();
		sCollector.collectInfos(this.sysinfoMidlet, this.display);
		Info[] sInfos = sCollector.getInfos();
		for (int i = 0; i < sInfos.length; i++) {
			if (sInfos[i].value.compareTo("<unknown>") != 0)
				this.infoTable.put(sInfos[i].name, sInfos[i].value);
		}
		//System.out.println("collected system infos... trying to collect J2ME system properties...");

		/* collect keycodes infos */
		NormalCanvasTest c = new NormalCanvasTest();
		for (int i = 0; i < this.keyCodes.length; i++) {
			try {
				this.keyCodeInfos[i] = new Info(this.keyCodeInfos[i].name,
						String.valueOf(c.getKeyCode(this.keyCodes[i])));
			} catch (Exception e) {
				//#debug error
				System.err.println("Could not get keyInfo for "
						+ this.keyCodeInfos[i]);
			}
		}
		for (int i = 0; i < this.keyCodeInfos.length; i++) {
			if (this.keyCodeInfos[i].value.length() > 0)
				this.infoTable.put(this.keyCodeInfos[i].name, this.keyCodeInfos[i].value);
		}

		
		/* collect system properties */
		SysPropCollector spCollector = new SysPropCollector();
		spCollector.collectInfos(this.sysinfoMidlet, this.display);
		Info[] spInfos = spCollector.getInfos();
		for (int i = 0; i < spInfos.length; i++) {
			Info info = spInfos[i];
			if ( (info.value.compareTo("<unknown>")) != 0 && (info.value.compareTo("N/A") != 0) )
				this.infoTable.put(info.name, info.value);
		}

		/* collect memory infos */
		try {
			Runtime rt = Runtime.getRuntime();
			this.infoTable.put("totalMemory", String.valueOf(rt.totalMemory()));
			long memBefore;
			long memAfter;
			memBefore = rt.freeMemory();
			Object o = new Object();
			memAfter = rt.freeMemory();
			this.infoTable.put("objectMemory", String.valueOf((memBefore - memAfter)));
		}catch (Exception e) {
			/* */
		}
		
		/* put the given identifier */
		this.infoTable.put( "identifier" , this.sysinfoMidlet.vendorField.getString() + "/" + this.sysinfoMidlet.deviceField.getString());
		
		try{
			if(this.sysinfoMidlet.getAppProperty("user-agent") != null){
				this.infoTable.put( "wap.useragent" , this.sysinfoMidlet.getAppProperty("user-agent"));
			}
		}catch(Exception e){
			/* if not set */
		}

		/* put keyCollector infos */
		try{
			if(this.sysinfoMidlet.keyInfos != null){
				for (Enumeration elems = this.sysinfoMidlet.keyInfos.keys() ; elems.hasMoreElements() ;) {
			        String key = (String) elems.nextElement();
			        //System.out.println("Key: " + key + "; Value: " + sysinfoMidlet.keyInfos.get(key));
			        this.infoTable.put(key, this.sysinfoMidlet.keyInfos.get(key));
			     }
			}
		}catch (Exception e) {
			/* if not set */
		}
		

		/* test image support for gif and jpg */
		String imageFormat = "";
		try{
			Image.createImage("/test.jpg");
			imageFormat += "jpeg";
		}catch(Exception e){
			//System.out.println("jpg not supported " + e.getMessage());
			
		}
		try{
			Image.createImage("/test.gif");
			if(imageFormat.length()>0){
				imageFormat += ", gif";
			}else{
				imageFormat = "gif";
			}
		}catch(Exception e){
			//System.out.println("gif not supported " + e.getMessage());
		}
		try{
			Image.createImage("/test.bmp");
			if(imageFormat.length()>0){
				imageFormat +=", bmp";
			}else{
				imageFormat += "bmp";
			}
		}catch(Exception e){
			//System.out.println("bmp not supported " + e.getMessage());
		}
		try{
			Image.createImage("/test.wbmp");
			if(imageFormat.length()>0){
				imageFormat += ", wbmp";
			}else{
				imageFormat += "wbmp";
			}
		}catch(Exception e){
			//System.out.println("wbmp not supported " + e.getMessage());
		}
		if(imageFormat.length()>0){
			//System.out.println("putting: " + imageFormat);
			this.infoTable.put("polish.imagecreateformat", imageFormat);
		}
		
		this.sysinfoMidlet.waitForm.append("collecting done...");
		
		try {
			this.sysinfoMidlet.waitForm.append("trying to send...");
			this.sysinfoMidlet.remoteServer.sendInfos(this.infoTable, this.sysinfoMidlet.userField.getString(), this.sysinfoMidlet.pwdField.getString());
			this.sysinfoMidlet.doneForm.deleteAll();
			this.sysinfoMidlet.doneForm.setTitle(Locale.get("msg.thanks"));
			this.sysinfoMidlet.doneForm.append(Locale.get("msg.uploaded"));
			this.sysinfoMidlet.doneForm.setCommandListener(this.sysinfoMidlet);
			this.display.setCurrent(this.sysinfoMidlet.doneForm);
		}catch (Exception e) {	
			this.sysinfoMidlet.doneForm.deleteAll();
			this.sysinfoMidlet.doneForm.setTitle(Locale.get("msg.uploadfailedtitle"));
			this.sysinfoMidlet.doneForm.append(Locale.get("msg.uploadfailed") + "\n");
			this.sysinfoMidlet.doneForm.append(e.getMessage());
			this.display.setCurrent(this.sysinfoMidlet.doneForm);
		}
		
	}

}
