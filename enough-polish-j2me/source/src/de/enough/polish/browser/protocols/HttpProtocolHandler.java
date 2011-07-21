//#condition polish.usePolishGui || polish.midp

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

import de.enough.polish.browser.ProtocolHandler;
import de.enough.polish.io.RedirectHttpConnection;
import de.enough.polish.util.HashMap;
import de.enough.polish.util.Locale;

//#if polish.usePolishGui
	import de.enough.polish.ui.StyleSheet;
//#endif

import java.io.IOException;

import javax.microedition.io.StreamConnection;

/**
 * Protocol handler class to handle HTTP.
 * 
 * The sent user agent is normally the string defined in the
 * <code>microedition.platform</code> system property. You can overwrite
 * this value by defining the <code>polish.Browser.UserAgent</code> variable
 * in your <code>build.xml</code>.
 */
public class HttpProtocolHandler extends ProtocolHandler
{
	private static String USER_AGENT = 
	//#if polish.Browser.UserAgent:defined
		//#= 	"${polish.Browser.UserAgent}";
	//#else
				"J2ME-Polish/" + System.getProperty("microedition.platform");
	//#endif

	private static boolean userAgentSet = false;
	
	private HashMap requestProperties;
	
	/**
	 * Sets the USER_AGENT string used for the request header
	 * @param userAgent the USER_AGENT string to set
	 */
	public static void setUserAgent(String userAgent) {
		USER_AGENT = userAgent;
		userAgentSet = true;
	}
	
	/**
	 * Returns the user agent string
	 * @return the user agent string
	 */
	public static String getUserAgent() {
		return USER_AGENT;
	}

	/**
	 * Creates a new HttpProtocolHandler object with "http" as it's protocol.
	 */
	public HttpProtocolHandler()
	{
		this("http",new HashMap() );
	}

	/**
	 * Creates a new HttpProtocolHandler object with "http" as it's protocol.
	 * 
	 * @param requestProperties the request properties to use for each request
	 */
	public HttpProtocolHandler(HashMap requestProperties)
	{
		this("http", requestProperties );
	}

	/**
	 * Creates a new HttpProtocolHandler object.
	 * 
	 * @param protocolName the protocolname (usually "http" or "https")
	 */
	public HttpProtocolHandler(String protocolName)
	{
		this(protocolName,new HashMap() );
	}
	
	/**
	 * Creates a new HttpProtocolHandler object.
	 * 
	 * @param protocolName the protocolname (usually "http" or "https")
	 * @param requestProperties the request properties to use for each request
	 */
	public HttpProtocolHandler(String protocolName, HashMap requestProperties)
	{
		super( protocolName );	
		if (requestProperties == null) {
			requestProperties = new HashMap();
		}
		this.requestProperties = requestProperties;
		if ( requestProperties.get("User-Agent") == null || userAgentSet)
		{
			if(USER_AGENT != null) {
				requestProperties.put("User-Agent", USER_AGENT );
			} else {
				requestProperties.remove("User-Agent");
			}
			 
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
		return new RedirectHttpConnection(url, this.requestProperties);
	}
}
