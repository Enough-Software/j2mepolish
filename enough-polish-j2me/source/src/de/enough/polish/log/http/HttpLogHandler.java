//#condition polish.midp || polish.usePolishGui

/*
 * Created on 27-Apr-2005 at 22:32:15.
 * 
 * Copyright (c) 2010 Robert Virkus / Enough Software
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
package de.enough.polish.log.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import de.enough.polish.log.LogEntry;
import de.enough.polish.log.LogHandler;
import de.enough.polish.util.ArrayList;
/**
 * <p>Pushes log entries to a remote server using GET</p>
 * <p>
 * Use the loghandler by specifying the following handler in your build.xml script:
 * <pre>
 * &lt;debug ...&gt;
 *    &lt;handler name=&quot;http&quot; /&gt;
 * &lt;/debug&gt;
 * </pre>
 * </p>
 * <p>
 * You can configure this log handler with following parameters:
 * </p>
 * <ul>
 *   <li><b>server</b>: The URL of the server into which the log should be written, e.g. http://company.com/log</li>
 * </ul>
 * <p>Example:
 * <pre>
 * &lt;debug ...&gt;
 *    &lt;handler name=&quot;http&quot; />
 *      &lt;-- optional parameters --&gt;
 *      &lt;parameter name=&quot;server&quot; value=&quot;http://www.j2mepolish.org/log&quot; /&gt;
 *    &lt;/handler&gt;
 * &lt;/debug&gt;
 * </pre>
 * </p>
 * <p>
 * You can log the code for example with following ruby script on your server (also created by Vera Wahler). Start this script
 * with <code>ruby server.rb</code>.
 * <pre>
 * #!/usr/bin/ruby
 * require 'webrick'
 * include WEBrick
 * 
 * s = HTTPServer.new( :Port => 8100)
 * 
 * class LogServlet < HTTPServlet::AbstractServlet
 *   def do_GET(req, res)
 *     hash = req.query
 *     puts "Time: "+hash['time'].to_s
 *     puts "Message: "+hash['message'].to_s
 *     puts "Classname: "+hash['classname'].to_s
 *     puts "LineNumber: "+hash['lineNumber'].to_s
 *     puts "Exception: "+hash['Exception'].to_s
 *     puts "Level: "+hash['level'].to_s
 *   end  
 * end
 * 
 * s.mount("/loghandler", LogServlet)
 * 
 * trap("INT") { s.shutdown }
 * 
 * s.start
 * </pre>
 * </p>
 * 
 *
 * @author Vera Wahler, wahler@iwelt.de
 */
public class HttpLogHandler extends LogHandler implements Runnable
{
	//#if polish.log.http.server:defined
		//#= public static final String SERVER_URL = "${polish.log.http.server}";
	//#elifdef logging.server:defined
		//#= public static final String SERVER_URL = "${logging.server}";
	//#else
		public static final String SERVER_URL = null;
	//#endif
	
	private static final String HTTP_VALUE_LANGUAGE = "en-DE";
	private static final String HTTP_KEY_LANGUAGE = "Content-Language";
	private static final String HTTP_VALUE_USER_AGENT = "Profile/MIDP-2.0 Configuration/CLDC-1.0";
	private static final String HTTP_KEY_USER_AGENT = "User-Agent";
	private static final String HTTP_VALUE_IF_MODIFIER = "10 Nov 2000 17:29:12 GMT";
	private static final String HTTP_KEY_IF_MODIFIER = "IF-Modified-Since";
	
	private volatile ArrayList logStack = new ArrayList();
	
	private Thread logThread;
	
	private volatile boolean keepRunning = true;
	
	/**
	 * Creates a new log handler
	 */
	public HttpLogHandler()
	{
		if(SERVER_URL != null)
		{
    		this.logThread = new Thread(this);
    		this.logThread.start();
		}
	}
	
	public void handleLogEntry(LogEntry logEntry) throws Exception
	{
		synchronized (this.logStack) {
			this.logStack.add(logEntry);
		}
	}

	public void run()
	{
		while(this.keepRunning)
		{
			// naechste Logmeldung abarbeiten
			LogEntry entryToLog = getNextEntry();
			if(entryToLog == null) {
				try
				{
					Thread.sleep(1000);
				}
				catch(InterruptedException e)
				{
					// wurde der Thread unterbrochen, einfach weiterlaufen lassen
				}
			}
			else
			{
				logCurrentEntry(entryToLog);
			}
				
		}
	}
	
	public void exit()
	{
		this.keepRunning = false;
	}
	
	private void logCurrentEntry(LogEntry logEntry)
	{
		HttpConnection connection = null;
		OutputStream outputStream = null;
		InputStream inputStream = null;
		
		try
		{
			connection = initHttpConnection(getUrl(logEntry));
			connection.getResponseCode();
		}
		catch(IOException e)
		{
			// hier kann nichts mehr gemacht werden
			e.printStackTrace();
		}
		finally
		{
			closeStreams(connection, inputStream, outputStream);
			
			// Resourcen freigeben
			connection = null;
			inputStream = null;
			outputStream = null;
		}
	}
	
	private void closeStreams(HttpConnection c, InputStream is, OutputStream os)
	{
		if(is != null)
		{
			try
			{
				is.close();
			}
			catch(IOException exc)
			{
				// Stream is already closed.
			}
		}
		if(os != null)
		{
			try
			{
				os.close();
			}
			catch(IOException exc)
			{
				// Stream is already closed.
			}
		}
		if(c != null)
		{
			try
			{
				c.close();
			}
			catch(IOException exc)
			{
				// Stream is already closed.
			}
		}
	}
	
	private String getUrl(LogEntry logEntry)
	{
		StringBuffer url = new StringBuffer();
		Date timeDate = new Date(logEntry.time);
		url.append(SERVER_URL).append("?");
		url.append("time=").append(encodeURL(timeDate.toString()));
		url.append("&message=").append(encodeURL(logEntry.message));
		url.append("&classname=").append(logEntry.className);
		url.append("&lineNumber=").append(logEntry.lineNumber);
		url.append("&exception=").append(encodeURL(logEntry.exception));
		url.append("&level=").append(logEntry.level);
		
		return url.toString();
	}
	
	private LogEntry getNextEntry() {
		if(this.logStack.size() == 0) {
			return null;
		}
		synchronized (this.logStack) {
			return (LogEntry)this.logStack.remove(0);
		}
	}
	
	private HttpConnection initHttpConnection(String url) throws IOException
	{
		HttpConnection logConnection = (HttpConnection)Connector.open(url);
		logConnection.setRequestMethod(HttpConnection.GET);
		logConnection.setRequestProperty(HTTP_KEY_IF_MODIFIER, HTTP_VALUE_IF_MODIFIER);
		logConnection.setRequestProperty(HTTP_KEY_USER_AGENT, HTTP_VALUE_USER_AGENT);
		logConnection.setRequestProperty(HTTP_KEY_LANGUAGE, HTTP_VALUE_LANGUAGE);
		return logConnection;
	}
	
	public String encodeURL(String url)
	{
		String newurl = "";
		int urllen = url.length();
		for(int i = 0; i < urllen; ++i)
		{
			char c = url.charAt(i);
			if(isCharEncodingNotNecessary(c))
			{
				newurl += c;
			}
			else if(c == ' ')
			{
				newurl += '+';
			}
			else
			{
				newurl += encodeChar(c);
			}
		}
		return newurl;
	}
	
	private boolean isCharEncodingNotNecessary(char c)
	{
		return ((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z')) || ((c >= '0') && (c <= '9')) || (c == '.') || (c == '-') || (c == '*') || (c == '_') || (c == '/') || (c == '~');
	}

	private String encodeChar(char c)
	{
		String encchar = "%";
		encchar += Integer.toHexString((c / 16) % 16).toUpperCase();
		encchar += Integer.toHexString(c % 16).toUpperCase();
		return encchar;
	}
}
