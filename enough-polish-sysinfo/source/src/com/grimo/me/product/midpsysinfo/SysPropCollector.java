/*
 * Created on May 28, 2008
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
package com.grimo.me.product.midpsysinfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Display;

import de.enough.polish.util.TextUtil;
import de.enough.sysinfo.MIDPSysInfoMIDlet;

public class SysPropCollector extends InfoCollector{

	public Hashtable capabilities;
	private Vector validPlatforms;
	private Vector validConfigurations;
	
	public SysPropCollector(){
		super();
		this.capabilities = new Hashtable();
		try{
			InputStream is = getClass().getResourceAsStream("/validplatforms.txt");
			this.validPlatforms = this.readLines(is);
		}catch (Exception e) {
			//#debug warn
			System.out.println("Valid platforms file error: " + e.getMessage());
		}
		try{
			InputStream is = getClass().getResourceAsStream("/validconfigurations.txt");
			this.validConfigurations = this.readLines(is);
		}catch (Exception e) {
			//#debug warn
			System.out.println("Valid configurations file error: " + e.getMessage());
		}
	}
	
	public void collectInfos(MIDPSysInfoMIDlet midlet, Display display) {
		//#debug
		System.out.println("Collecting infos...");
		
		//get propsfile
		InputStream is = getClass().getResourceAsStream("/systemproperties.txt");
		if( is == null ){
			//#debug warn
			System.out.println("Properties file InputStream is null!");
			return;
		}
		try {
			//#debug
			System.out.println("Got properties file with " + is.available() + " available.");
			
			Hashtable properties = this.readProperties( is );
			is.close();
			
			//#debug
			System.out.println("Got " + properties.size() + " lines.");
			//#mdebug
			Enumeration keys = properties.keys();
			while(keys.hasMoreElements()){
				String key = (String) keys.nextElement();
				Enumeration valuesEnum = ((Vector) properties.get( key )).elements();
				String valueString = "";
				while( valuesEnum.hasMoreElements() ){
					String value = (String) valuesEnum.nextElement();
					valueString += value;
					if( valuesEnum.hasMoreElements() ){
						valueString += ", ";
					}
				}
				
			}
			//#enddebug
			
			this.processProperties( properties );
			
			//#debug info
			System.out.println("Collected " + this.capabilities.size() + " capabilities." );
		} catch ( IOException e ) {
			//#debug error
			System.err.println("Could not read properties file: " + e.getMessage());
		}
		//add infos
		//this.capabilities
		Enumeration keys = this.capabilities.keys();
		while(keys.hasMoreElements()){
			String key = (String) keys.nextElement();
			addInfo( key, (String) this.capabilities.get(key) );
			
			//#debug
			//System.out.println("adding info " + key + ": " + this.capabilities.get(key));
		}
		
		
		//collect bluetooth infos if available
		try{
			Class c = Class.forName( "javax.bluetooth.LocalDevice" );
			Class collClass = Class.forName( "com.grimo.me.product.midpsysinfo.BTPropsCollector" );
			
			SysPropCollector coll = (SysPropCollector) collClass.newInstance();
			System.out.println("infos: " + coll );
			coll.collectInfos(midlet, display);
			Info[] infos = coll.getInfos();
			//#debug
			System.out.println("infos: " + infos.length );
			if( infos != null  && infos.length > 0 ){
				for (int i = 0; i < infos.length; i++) {
					Info info = infos[i];
					addInfo(info.name, info.value);
				}
			}
		}catch (InstantiationException e) {
			//#debug
			System.out.println("Could not load bt class (InstantiationException): " + e.getMessage() );
		}catch ( IllegalAccessException e) {
			//#debug
			System.out.println("Could not load bt class (IllegalAccessException): " + e.getMessage() );
		} catch (ClassNotFoundException e) {
			//#debug
			System.out.println("Could not load bt class (ClassNotFoundException): " + e.getMessage() );
		}
	}

	private void processProperties( Hashtable properties ){
		Enumeration keys = properties.keys();
		while(keys.hasMoreElements()){
			String key = (String) keys.nextElement();
			String value = this.getSystemProperty( key );
			if( (value == null) || (value.length() == 0)  ){
				continue;
			}
			
			
			
			
			//process actions
			this.processActions(key, value, (Vector) properties.get( key ));
			this.processDefaultActions( key, value );
		}
	}
	
	private void processActions( String name, String value, Vector actions ){
		//parse values like "MIDP-2.0" to "MIDP/2.0"
		value = this.correctCapability(name, value);
		if( value.length() == 0 ){
			return;
		}
		
		
		
		Enumeration keys = actions.elements();
		while( keys.hasMoreElements() ){
			String key = (String) keys.nextElement();
			
			//add infos according to action
			if( key.indexOf("+") != -1 ){
				String[] action = TextUtil.splitAndTrim(key, '+');
				String newName = action[0];
				
				if( action[1].equals("$value") ){
					//#debug
					System.out.println("adding value to cap " + newName + ": " + value);
					addValueToCapability(newName, value);
				}else{
					addValueToCapability(newName, action[1]);
				}
			}else if( key.indexOf("=") != -1 ){
				this.setCapabilityValue(key, name, value);
			}else if( key.indexOf("-") != -1 ){
				//TODO timon implement removeValueFromCapability(name, value)
				//removeValueFromCapability(name, value);
			}
			
		}
	}
	
	private void processDefaultActions( String name, String value ){
		//general actions
		//set "property." + name
		this.setCapabilityValue( null, "property." + name, value );
		//add value to capability "Properties"
		this.addValueToCapability("Properties", name);
	}
	
	private void addValueToCapability( String name, String value ){
		if( this.capabilities.get( name ) == null ){
			this.capabilities.put( name, value );
		}else{
			String oldValue = (String) this.capabilities.get( name );
			this.capabilities.put( name, oldValue + ", " + value );
		}
		
	}
	
	private void setCapabilityValue( String action, String name, String value ){
		if( (action != null) && (action.indexOf("$value") != -1) ){
			String[] actionArray = TextUtil.splitAndTrim(action, '=');
			String newName = actionArray[0];
			this.capabilities.put( newName, value );
		}else if( action != null ){
			String[] actionArray = TextUtil.splitAndTrim( action, '=' );
			if( (actionArray != null) && (actionArray.length == 2) ){
				this.capabilities.put( actionArray[0], actionArray[1] );
			}else{
			}
		}else{
			this.capabilities.put( name, value );
		}
	}
	
	private String correctCapability( String name, String value ){
		String newValue = "";
		
		if( name.equals( "microedition.profiles") || name.equals( "microedition.platform")){
			String tmpValue = value.replace('-', '/');
			Enumeration platElem = this.validPlatforms.elements();
			while (platElem.hasMoreElements()) {
				String platform = (String) platElem.nextElement();
				if( tmpValue.indexOf(platform) != -1 ){
					//#debug
					System.out.println("found platform " + platform);
					newValue += platform + ", ";
				}
				if( platElem.hasMoreElements() == false && newValue.length() > 2){
					//remove last ,
					newValue = newValue.substring(0, newValue.length() - 2 );
				}
			}
			
			return newValue;
		}else if( name.equals( "microedition.configuration" ) ){
			String tmpValue = value.replace('-', '/');
			Enumeration confElem = this.validConfigurations.elements();
			while (confElem.hasMoreElements()) {
				String conf = (String) confElem.nextElement();
				if( tmpValue.indexOf(conf) != -1 ){
					newValue += conf + ", ";
				}
				if( confElem.hasMoreElements() == false ){
					//remove last ,
					newValue = newValue.substring(0, newValue.length() - 2 );
				}
			}
			//#debug
			System.out.println("new value is " + newValue);
			return newValue;
		}else if( name.equals( "microedition.jtwi.version" ) ){
			return "JTWI/" + value;
		}
		
		return value;
	}
	
	private Hashtable readProperties( InputStream is ) throws IOException{
		Hashtable lines = new Hashtable();
		
		StringBuffer buf = new StringBuffer();
		int i;
		while ((i = is.read()) != -1){ 
			char ch = (char) i;
			if (ch == '\n' || ch == '\r'){ 
				String[] propArray = TextUtil.splitAndTrim(buf.toString(), ':');
				if( (propArray != null) && (propArray.length > 0) ){
					Vector actions = new Vector();
					for( int count=1; count < propArray.length; count++ ){
						actions.addElement( propArray[count] ); 
					}
					lines.put(propArray[0], actions);
				}
				
				buf.delete(0,buf.length());
			}else if( ch == '#' ){
				//ignore this line ( comment char # )
				while ((i = is.read()) != -1){ 
					char c = (char) i;
					if (c == '\n' || c == '\r'){
						break;
					}
				}
			}else{ 
				buf.append(ch);
			}
		}
		/* if the last line is not empty */
		if(buf.toString().trim().length()>0){
			String[] propArray = TextUtil.splitAndTrim(buf.toString(), ':');
			if( (propArray != null) && (propArray.length > 0) ){
				Vector actions = new Vector();
				for( int count=1; count < propArray.length; count++ ){
					actions.addElement( propArray[count] ); 
				}
				lines.put(propArray[0], actions);
			}
		}
		return lines;
	}
	
	private Vector readLines( InputStream is ) throws IOException{
		Vector lines = new Vector();
		
		StringBuffer buf = new StringBuffer();
		int i;
		while ((i = is.read()) != -1){ 
			char ch = (char) i;
			if (ch == '\n' || ch == '\r'){ 
				lines.addElement(buf.toString());
				buf.delete(0,buf.length());
			}else if( ch == '#' ){
				//ignore this line ( comment char # )
				while ((i = is.read()) != -1){ 
					char c = (char) i;
					if (c == '\n' || c == '\r'){
						break;
					}
				}
			}else{ 
				buf.append(ch);
			}
		}
		/* if the last line is not empty */
		if(buf.toString().trim().length()>0){
			lines.addElement(buf.toString());
			buf.delete(0,buf.length());
		}
		return lines;
	}
	
	
	private String getSystemProperty( String name ){
		String value = null;
		try {
			value = System.getProperty( name );
		} catch (Exception e) {
			//#debug
			System.err.println("Unable to query " + name + e.getMessage() );
		}
		return value;
	}

	
}
