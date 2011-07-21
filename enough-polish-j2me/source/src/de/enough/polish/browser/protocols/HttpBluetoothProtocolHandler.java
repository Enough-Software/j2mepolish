//#condition polish.usePolishGui && polish.api.btapi

/*
 * Created on 11-Jan-2006 at 19:20:28.
 * 
 * Copyright (c) 2009 - 2009 Michael Koch / Enough Software
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA02111-1307USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.browser.protocols;

import java.io.IOException;
import java.io.OutputStream;

import javax.bluetooth.L2CAPConnection;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.ServiceRecord;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import de.enough.polish.bluetooth.L2CapStreamConnection;
import de.enough.polish.browser.ProtocolHandler;
import de.enough.polish.ui.StyleSheet;
import de.enough.polish.util.HashMap;
import de.enough.polish.util.Locale;

/**
 * Protocol handler class to handle HTTP requests over bluetooth.
 * Use the de.enough.polish.bluetooth.BluetoothProxy on a PC for forwarding HTTP requests.
 * 
 * 
 * The sent user agent is normally the string defined in the
 * <code>microedition.platform</code> system property. You can overwrite
 * this value by defining the <code>polish.Browser.UserAgent</code> variable
 * in your <code>build.xml</code>.
 */
public class HttpBluetoothProtocolHandler extends ProtocolHandler
{
	private static final String USER_AGENT = 
	//#if polish.Browser.UserAgent:defined
		//#= 	"${polish.Browser.UserAgent}";
	//#else
				"J2ME-Polish/" + System.getProperty("microedition.platform");
	//#endif

	private HashMap requestProperties;
	private final ServiceRecord serviceRecord;

	/**
	 * Creates a new HttpProtocolHandler object with "http" as it's protocol.
	 * @param serviceRecord the remote service to which connections should be established
	 */
	public HttpBluetoothProtocolHandler(ServiceRecord serviceRecord)
	{
		this("http",new HashMap(), serviceRecord );
	}

	/**
	 * Creates a new HttpProtocolHandler object with "http" as it's protocol.
	 * 
	 * @param requestProperties the request properties to use for each request
	 * @param serviceRecord the remote service to which connections should be established
	 */
	public HttpBluetoothProtocolHandler(HashMap requestProperties, ServiceRecord serviceRecord)
	{
		this("http", requestProperties, serviceRecord );
	}

	/**
	 * Creates a new HttpProtocolHandler object.
	 * 
	 * @param protocolName the protocolname (usually "http" or "https")
	 * @param serviceRecord the remote service to which connections should be established
	 */
	public HttpBluetoothProtocolHandler(String protocolName, ServiceRecord serviceRecord)
	{
		this(protocolName,new HashMap(), serviceRecord );
	}
	
	/**
	 * Creates a new HttpProtocolHandler object.
	 * 
	 * @param protocolName the protocolname (usually "http" or "https")
	 * @param requestProperties the request properties to use for each request
	 * @param serviceRecord the remote service to which connections should be established
	 */
	public HttpBluetoothProtocolHandler(String protocolName, HashMap requestProperties, ServiceRecord serviceRecord)
	{
		super( protocolName );
		this.serviceRecord = serviceRecord;	
		if (requestProperties == null) {
			requestProperties = new HashMap();
		}
		this.requestProperties = requestProperties;
		if ( requestProperties.get("User-Agent") == null )
		{
			requestProperties.put("User-Agent", USER_AGENT );
		}
		if ( requestProperties.get("Accept") == null ) {
			requestProperties.put("Accept", "text/html, text/xml, text/*, image/png, image/*, application/xhtml+xml, */*" );
		}
		if (requestProperties.get("Accept-Language") == null) {
			//#ifdef polish.i18n.useDynamicTranslations
				requestProperties.put("Accept-Language", Locale.LANGUAGE );
			//#else
				String meLocale = System.getProperty("microedition.locale");
				//#if polish.language:defined
					if (meLocale == null) { 
						//#= meLocale = "${polish.language}";
					}
				//#endif
				if (meLocale != null) {
					requestProperties.put("Accept-Language", meLocale );
				} 
			//#endif
		}
		//#if polish.usePolishGui
		if ( requestProperties.get("UA-pixels")  == null && StyleSheet.currentScreen != null)  {
			requestProperties.put("UA-pixels", StyleSheet.currentScreen.getWidth() + "x" + StyleSheet.currentScreen.getHeight() );
		}
		//#endif
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.browser.ProtocolHandler#getConnection(java.lang.String)
	 */
	public StreamConnection getConnection(String url)
	throws IOException
	{
		String btUrl = this.serviceRecord.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
        String max = LocalDevice.getProperty("bluetooth.l2cap.receiveMTU.max");
        if (max != null) {
        	btUrl += ";ReceiveMTU=" + max + ";TransmitMTU=" + max;
        }
        L2CAPConnection connection = (L2CAPConnection) Connector.open( btUrl );
        L2CapStreamConnection streamConnection = new L2CapStreamConnection( connection );
        OutputStream out = streamConnection.openOutputStream();
        out.write( url.getBytes() );
        out.close();
		return streamConnection;
	}
}
