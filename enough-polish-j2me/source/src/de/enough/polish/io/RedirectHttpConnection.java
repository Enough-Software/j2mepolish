//#condition polish.usePolishGui || polish.midp

/*
 * Created on 13-Jan-2007 at 10:12:48.
 * 
 * Copyright (c) 2009 Michael Koch / Enough Software
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
package de.enough.polish.io;

import de.enough.polish.util.HashMap;
import de.enough.polish.util.TextUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

/**
 * Provides a <code>HttpConnection</code> that supports HTTP redirects. This
 * class is compatible to <code>javax.microedition.io.HttpConnection</code>.
 * 
 * <p>
 * When connecting to an URL and a HTTP redirect is return this class follows
 * the redirect and uses the following HTTP connection. This works over multiple
 * levels. By default five redirects are supported. The number of supported
 * redirects can be tuned by setting the preprocessing variable
 * <code>polish.Browser.MaxRedirects</code> to some integer value.
 * </p>
 * <p>
 * You can specify a timeout with the  <code>RedirectHttpConnection( String url, int timeout)</code> or <code>RedirectHttpConnection( String url, Hashmap requestProperties, int timeout)</code> constructor. On
 * MIDP and BlackBerry devices this will result in an additional thread being launched that waits for the specific time and then closes the connection
 * in case it has so far not succeeded.
 * </p>
 * <p>
 * In case you want to do POST requests, you can also use the PostRedirectHttpConnection!
 * </p>
 *  
 * @see HttpConnection
 * @see PostRedirectHttpConnection
 */
public class RedirectHttpConnection implements HttpConnection
{
	private static final int MAX_REDIRECTS
	//#if polish.Browser.MaxRedirects:defined
		//#= = ${polish.Browser.MaxRedirects};
	//#else
		= 5;
	//#endif

	private final String originalUrl;
	private String requestMethod = HttpConnection.GET;
	private HashMap requestProperties;
	HttpConnection httpConnection;
	private ByteArrayOutputStream byteArrayOutputStream;
	private InputStream inputStream;
	private HttpConnection currentHttpConnection;
	private boolean limitContentLengthParams;
	private int timeout;
	private Object timeoutLock;
	private boolean timeoutReached;
	public IOException exception;
	
	private RedirectListener redirectListener;

	private CookieManager cookieManager;

	/**
	 * Creates a new http connection that understands redirects.
	 * 
	 * @param url  the url to connect to
	 * @throws IOException when Connector.open() fails
	 */
	public RedirectHttpConnection(String url) throws IOException
	{
		this(url, null, 0);
	}

	/**
	 * Creates a new http connection that understands redirects.
	 * 
	 * @param url  the url to connect to
	 * @param timeout the timeout for the HTTP connection (both read timeout and connect timeout), use 0 for not specifying a timeout.
	 * @throws IOException when Connector.open() fails
	 */
	public RedirectHttpConnection(String url, int timeout) throws IOException
	{
		this(url, null, timeout);
	}

	/**
	 * Creates a new http connection that understands redirects.
	 * 
	 * @param url  the url to connect to
	 * @param requestProperties the request properties to be set for each http request
	 * @throws IOException when Connector.open() fails
	 */
	public RedirectHttpConnection(String url, HashMap requestProperties)
	throws IOException
	{
		this(url, requestProperties, 0);
	}
	
	/**
	 * Creates a new http connection that understands redirects.
	 * 
	 * @param url  the url to connect to
	 * @param requestProperties the request properties to be set for each http request
	 * @param timeout the timeout for the HTTP connection (both read timeout and connect timeout), use 0 for not specifying a timeout.
	 * @throws IOException when Connector.open() fails
	 */
	public RedirectHttpConnection(String url, HashMap requestProperties, int timeout)
	throws IOException
	{
		this.originalUrl = url;
		this.requestProperties = new HashMap();
		this.timeout = timeout;
		
		if (requestProperties != null) {
			Object[] keys = requestProperties.keys();
			for (int i = 0; i < keys.length; i++)
			{
				String key = (String) keys[i];
				String value = (String) requestProperties.get(keys[i]);
				setRequestProperty(key, value);
			}
		}
		
		this.currentHttpConnection = (HttpConnection) Connector.open(url, Connector.READ_WRITE, true);
		//#if polish.android
			if (timeout > 0) {
				//# ((de.enough.polish.android.io.HttpConnectionImpl) this.currentHttpConnection).setTimeout( timeout );
			}
		//#endif
		
	}
	
	/**
	 * Registers a cookie manager.
	 * The cookie manager will pick up any cookies that are sent in between redirects, but not of the final connection.
	 * @param manager the cookie manager
	 * @see #getCookieManager()
	 */
	public void setCookieManager( CookieManager manager ) {
		this.cookieManager = manager;
	}
	
	/**
	 * Retrieves a previously registered cookie manager
	 * @return the cookie manager or null if none has been set
	 * @see #setCookieManager(CookieManager)
	 */
	public CookieManager getCookieManager() {
		return this.cookieManager;
	}
	
	/**
	 * Sets the RedirectListener for this connection.
	 * @param redirectListener the listener, use null to unregister a previously set one.
	 * @see #getRedirectListener()
	 */
	public void setRedirectListener( RedirectListener redirectListener ) {
		this.redirectListener = redirectListener;
	}
	
	/**
	 * Retrieves a previously registered RedirectListener
	 * @return the listener or null if none has been registered
	 * @see #setRedirectListener(RedirectListener)
	 */
	public RedirectListener getRedirectListener() {
		return this.redirectListener;
	}

	/**
	 * Makes sure that the HTTP connection got created. This method redirects
	 * until the final connection is created.
	 * 
	 * @throws IOException when the connection failed
	 */
	protected synchronized void ensureConnectionCreated() 
	throws IOException
	{
		if (this.httpConnection != null)
		{
			return;
		}

		//#if !polish.android
			if (this.timeout != 0) {
				this.timeoutLock = new Object();
				this.timeoutReached = false;
				ConnectionThread connectionThread = new ConnectionThread();
				connectionThread.start();
				synchronized (this.timeoutLock) {					
					try {
						this.timeoutLock.wait(this.timeout);
					} catch (InterruptedException e) {
						// ignore
					}
				}
				IOException ioError = this.exception;
				if (ioError != null) {
					throw ioError;
				}
				if (this.httpConnection == null) {
					this.timeoutReached = true;
					throw new IOException("timeout");
				}
				return;
			}
		//#endif
			

		connect();
	}
	
	/**
	 * Establishes the connection and resolves any intermediate HTTP redirects in that process.
	 * 
	 * @throws IOException when the connection failed
	 */
	protected void connect() throws IOException {
		HttpConnection tmpHttpConnection = this.currentHttpConnection;
		InputStream tmpIn = null;

		int redirects = 0;
		String url = this.originalUrl;
		//#debug
		System.out.println("connecting to " + url);
		
		while (true)
		{
			if (tmpHttpConnection == null) {
				tmpHttpConnection = (HttpConnection) Connector.open(url, Connector.READ_WRITE, true);
				//#if polish.android
					if (this.timeout > 0) {
						//# ((de.enough.polish.android.io.HttpConnectionImpl) this.currentHttpConnection).setTimeout( timeout );
					}
				//#endif
				this.currentHttpConnection = tmpHttpConnection;
			}
			tmpHttpConnection.setRequestMethod(this.requestMethod);
			if (this.requestProperties != null)
			{
				Object[] keys = this.requestProperties.keys();

				if (keys != null)
				{
					for (int i = 0; i < keys.length; i++)
					{
						String key = (String) keys[i];
						String value = (String) this.requestProperties.get(keys[i]);
						tmpHttpConnection.setRequestProperty(key, value);
					}
				}
			}

			// Send POST data if exists.
			if (this.byteArrayOutputStream != null)
			{
				byte[] postData = this.byteArrayOutputStream.toByteArray();

				if (postData != null && postData.length > 0)
				{
					tmpHttpConnection.setRequestProperty("Content-Length", Integer.toString(postData.length));
					if (!this.limitContentLengthParams) {
						tmpHttpConnection.setRequestProperty("Content-length", Integer.toString(postData.length));	
					}
					OutputStream out = tmpHttpConnection.openOutputStream();
					out.write(postData);
					out.close();
				}
			}

			// Opens the connection.
			int resultCode = tmpHttpConnection.getResponseCode();
			if (resultCode < 400) {
				tmpIn = tmpHttpConnection.openInputStream();
			}
			if (this.timeoutReached) {
				return;
			}

			if (resultCode == HttpConnection.HTTP_MOVED_TEMP
					|| resultCode == HttpConnection.HTTP_MOVED_PERM
					|| resultCode == HttpConnection.HTTP_SEE_OTHER
					|| resultCode == HttpConnection.HTTP_TEMP_REDIRECT)
			{
				String tmpUrl = tmpHttpConnection.getHeaderField("Location");

				// Check if url is relative.
				if (!tmpUrl.startsWith("http://") && !tmpUrl.startsWith("https://") ) {
					url += tmpUrl; 
				}
				else {
					url = tmpUrl;
				}
				if (this.redirectListener != null) {
					url = this.redirectListener.onRedirect(url);
					if (url == null) {
						// abort the process
						throw new IOException("aborted");
					}
				}
				//#debug
				System.out.println("redirecting to " + url);
				if (this.cookieManager != null) {
					int number = this.cookieManager.extractCookies(tmpHttpConnection);
					if (number > 0) {
						setRequestProperty("Set-cookie", this.cookieManager.getCookiesForUrl(url));
					}
				}
				if (tmpIn != null) {
					tmpIn.close(); // close input stream - needed for moto devices, for example
					tmpIn = null;
				}
				tmpHttpConnection.close();
				tmpHttpConnection = null; // setting to null is needed for
									 	  // some series 40 devices
				if (++redirects > MAX_REDIRECTS)
				{
					throw new IOException("too many redirects");
				}
				

				continue;
			}
			// no redirect, we are at the final connection:
			break;
		}
		this.httpConnection = tmpHttpConnection;
		this.currentHttpConnection = tmpHttpConnection;
		this.inputStream = tmpIn;
	}
	
	/**
	 * Allows to disable sending of both "Content-Length" and "Content-length" parameters.
	 * 
	 * @param limit false, when only the "Content-Length" header should be set, not the "Content-length" request header.
	 */
	public void setLimitContentLengthParams(boolean limit) {
		this.limitContentLengthParams = limit;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.io.HttpConnection#getDate()
	 */
	public long getDate() throws IOException
	{
		ensureConnectionCreated();
		return this.httpConnection.getDate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.io.HttpConnection#getExpiration()
	 */
	public long getExpiration() throws IOException
	{
		ensureConnectionCreated();
		return this.httpConnection.getExpiration();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.io.HttpConnection#getFile()
	 */
	public String getFile()
	{
		try
		{
			ensureConnectionCreated();
			return this.httpConnection.getFile();
		} catch (IOException e)
		{
			// #debug error
			System.out.println("Unable to open connection" + e);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.io.HttpConnection#getHeaderField(java.lang.String)
	 */
	public String getHeaderField(String name) throws IOException
	{
		ensureConnectionCreated();
		String headerFieldValue = this.httpConnection.getHeaderField(name);
		return headerFieldValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.io.HttpConnection#getHeaderField(int)
	 */
	public String getHeaderField(int n) throws IOException
	{
		ensureConnectionCreated();
		return this.httpConnection.getHeaderField(n);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.io.HttpConnection#getHeaderFieldDate(java.lang.String,
	 *      long)
	 */
	public long getHeaderFieldDate(String name, long def) throws IOException
	{
		ensureConnectionCreated();
		return this.httpConnection.getHeaderFieldDate(name, def);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.io.HttpConnection#getHeaderFieldInt(java.lang.String,
	 *      int)
	 */
	public int getHeaderFieldInt(String name, int def) throws IOException
	{
		ensureConnectionCreated();
		return this.httpConnection.getHeaderFieldInt(name, def);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.io.HttpConnection#getHeaderFieldKey(int)
	 */
	public String getHeaderFieldKey(int n) throws IOException
	{
		ensureConnectionCreated();
		return this.httpConnection.getHeaderFieldKey(n);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.io.HttpConnection#getHost()
	 */
	public String getHost()
	{
		return this.currentHttpConnection.getHost();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.io.HttpConnection#getLastModified()
	 */
	public long getLastModified() throws IOException
	{
		ensureConnectionCreated();
		return this.httpConnection.getLastModified();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.io.HttpConnection#getPort()
	 */
	public int getPort()
	{
		return this.currentHttpConnection.getPort();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.io.HttpConnection#getProtocol()
	 */
	public String getProtocol()
	{
		return this.currentHttpConnection.getProtocol();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.io.HttpConnection#getQuery()
	 */
	public String getQuery()
	{
		return this.currentHttpConnection.getQuery();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.io.HttpConnection#getRef()
	 */
	public String getRef()
	{
		return this.currentHttpConnection.getRef();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.io.HttpConnection#getRequestMethod()
	 */
	public String getRequestMethod()
	{
		return this.requestMethod;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.io.HttpConnection#getRequestProperty(java.lang.String)
	 */
	public String getRequestProperty(String key)
	{
		return (String) this.requestProperties.get(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.io.HttpConnection#getResponseCode()
	 */
	public int getResponseCode() throws IOException
	{
		ensureConnectionCreated();
		int responseCode = this.httpConnection.getResponseCode();
		return responseCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.io.HttpConnection#getResponseMessage()
	 */
	public String getResponseMessage() throws IOException
	{
		ensureConnectionCreated();
		String responseMessage = this.httpConnection.getResponseMessage();
		return responseMessage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.io.HttpConnection#getURL()
	 */
	public String getURL()
	{
		return this.originalUrl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.io.HttpConnection#setRequestMethod(java.lang.String)
	 */
	public void setRequestMethod(String requestMethod) throws IOException
	{
		this.requestMethod = requestMethod;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.io.HttpConnection#setRequestProperty(java.lang.String,
	 *      java.lang.String)
	 */
	public void setRequestProperty(String key, String value) throws IOException
	{
		if (this.requestProperties == null)
		{
			this.requestProperties = new HashMap();
		}
		// #if polish.Bugs.HttpIfModifiedSince
		if ("if-modified-since".equals(key.toLowerCase()))
		{
			Date d = new Date();
			this.requestProperties.put("IF-Modified-Since", d.toString());
		}
		// #endif

		this.requestProperties.put(key, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.io.ContentConnection#getEncoding()
	 */
	public String getEncoding()
	{
		try {
			ensureConnectionCreated();
			return this.httpConnection.getEncoding();
		} catch (IOException e) {
			//#debug error
			System.out.println("Unable to establish connection" + e);
			return this.currentHttpConnection.getEncoding();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.io.ContentConnection#getLength()
	 */
	public long getLength()
	{
		try {
			ensureConnectionCreated();
			return this.httpConnection.getLength();
		} catch (IOException e) {
			//#debug error
			System.out.println("Unable to establish connection" + e);
			return this.currentHttpConnection.getLength();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.io.ContentConnection#getType()
	 */
	public String getType()
	{
		try {
			ensureConnectionCreated();
			return this.httpConnection.getType();
		} catch (IOException e) {
			//#debug error
			System.out.println("Unable to establish connection" + e);
			return this.currentHttpConnection.getType();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.io.InputConnection#openDataInputStream()
	 */
	public DataInputStream openDataInputStream() throws IOException
	{
		// TODO: Needs to be synnchronized and the DataInputStream should only
		// be created once.
		return new DataInputStream(openInputStream());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.io.InputConnection#openInputStream()
	 */
	public InputStream openInputStream() throws IOException
	{
		ensureConnectionCreated();
		InputStream is = this.inputStream;
		if (is == null) {
			is = this.httpConnection.openInputStream();
		}
		return is;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.io.Connection#close()
	 */
	public void close() throws IOException
	{
		// Check if there is a connection to actually close.
		if (this.httpConnection != null)
		{
			if (this.inputStream != null)
			{
				try
				{
					this.inputStream.close();
				} catch (Exception e)
				{
					// #debug error
					System.out.println("Error while closing input stream" + e);
				}
				this.inputStream = null;
			}
			if (this.byteArrayOutputStream != null)
			{
				try
				{
					this.byteArrayOutputStream.close();
				} catch (Exception e)
				{
					// #debug error
					System.out.println("Error while closing output stream" + e);
				}
				this.byteArrayOutputStream = null;
			}
			this.httpConnection.close();
			this.httpConnection = null;
		}
		if (this.currentHttpConnection != null) {
			this.currentHttpConnection.close();
			this.currentHttpConnection = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.io.OutputConnection#openDataOutputStream()
	 */
	public DataOutputStream openDataOutputStream() throws IOException
	{
		// TODO: Needs to be synchronized and the DataOutputStream should only be
		// created once.
		return new DataOutputStream(openOutputStream());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.io.OutputConnection#openOutputStream()
	 */
	public synchronized OutputStream openOutputStream() throws IOException
	{
		if (this.httpConnection != null) {
			return this.httpConnection.openOutputStream();
		}
		if (this.byteArrayOutputStream == null)
		{
			this.byteArrayOutputStream = new ByteArrayOutputStream();
		}

		return this.byteArrayOutputStream;
	}

	
	private class ConnectionThread extends Thread {
		public void run() {
			try {
				connect();
				Object lock = RedirectHttpConnection.this.timeoutLock;
				synchronized (lock) {
					lock.notify();
				}
			} catch (IOException e) {
				RedirectHttpConnection.this.exception = e;
			}
		}
	}
}