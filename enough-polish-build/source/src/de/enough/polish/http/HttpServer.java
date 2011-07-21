/*
 * Created on Mar 16, 2007 at 8:33:17 PM.
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
package de.enough.polish.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * <p>Simple HTTP Server based loosely on http://www.devpapers.com/article/99</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Mar 16, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class HttpServer extends Thread
{
	
	private boolean isStopRequested;
	private final int port;
	private final File baseDir;
	
	/**
	 * Creates a new HTTP Server on the specified port and the given base directory.
	 * 
	 * @param port the port of the server
	 * @param baseDir the base directory of the server
	 */
	public HttpServer( int port, File baseDir ) {
		this.port = port;
		this.baseDir = baseDir;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		//		print out the port number for user
		try {
			ServerSocket serverSocket = new ServerSocket(this.port);
			System.out.println("HttpServer running on port " + serverSocket.getLocalPort() + " with base dir " + this.baseDir.getAbsolutePath() );
			   
			// server infinite loop
			while(!this.isStopRequested) {
				Socket socket = serverSocket.accept();
				System.out.println("HttpServer: " + (new Date().toString()) + ": New connection accepted " + socket.getInetAddress() + ":" + socket.getPort());
		
				//	Construct handler to process the HTTP request message.
				try {
				    HttpRequestHandler request = new HttpRequestHandler(socket, this.baseDir);
				    // Create a new thread to process the request.
				    Thread thread = new Thread(request);
				    // Start the thread.
				    thread.start();
				} catch(Exception e) {
				    System.out.println(e);
				}	    
			}
		} catch (IOException e) {
			System.out.println("Unable to start HttpServer: " + e.toString() );
		}
	}
	
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: java de.enough.polish.http.HttpServer [port] [base-directory]");
		}
		int port = 9090;
		if (args.length > 0) {
			try {
				port = Integer.parseInt( args[0]);
			} catch (Exception e) {
				System.err.println("Invalid port: " + args[0]);
				System.exit(1);
			}
		}
		File baseDir = new File(".");
		if (args.length > 1) {
			baseDir = new File( args[1]);
			if (!baseDir.exists()) {
				System.err.println("Invalid base-directory: " + args[1]);
				System.exit(1);
			}
		}
		HttpServer server= new HttpServer( port, baseDir );
		server.start();
	}
}

class HttpRequestHandler implements Runnable
{
    final static String CRLF = "\r\n";
    Socket socket;
    OutputStream output;
    BufferedReader bufferedReader;
	private final File baseDir;

    /**
     * Create a new request handler
     * @param socket
     * @param baseDir the base directory
     * @throws Exception
     */
    public HttpRequestHandler(Socket socket, File baseDir) throws Exception
    {
		this.socket = socket;
		this.baseDir = baseDir;
		this.output = socket.getOutputStream();
		this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
   
    // Implement the run() method of the Runnable interface.
    public void run()
    {
		try {
		    processRequest();
		}
		catch(Exception e) {
		    System.out.println("HttpServer: unable to server request: " + e);
		}
    }
   
    private void processRequest() throws Exception
    {
		while(true) {
		    String headerLine = this.bufferedReader.readLine();
		    System.out.println(headerLine);
		    if (headerLine.equals(CRLF) || headerLine.equals("")) {
		    	break; // abort processsing
		    }
		   
		    StringTokenizer s = new StringTokenizer(headerLine);
		    String temp = s.nextToken();
	   
		    if(temp.equals("GET")) {
		    	String fileName = s.nextToken();
		    	//fileName = fileName ;
	
				// Open the requested file.
				FileInputStream fis = null ;
				File file =  new File( this.baseDir, fileName );
				boolean fileExists = file.exists();
				if (fileExists) {
					try {
						fis = new FileInputStream( file ) ;
					} catch ( FileNotFoundException e ) {
						fileExists = false ;
					}
				}
	
				// Construct the response message.
				String serverLine = "Server: J2ME Polish Server/1.0" + CRLF ;
				String statusLine = null;
				String contentTypeLine = null;
				String entityBody = null;
				String contentLengthLine = "error";
				if ( fileExists ) {
					statusLine = "HTTP/1.0 200 OK" + CRLF ;
					contentTypeLine = "Content-type: " + contentType( fileName ) + CRLF ;
					contentLengthLine = "Content-Length: " + (new Long(file.length())).toString() + CRLF;
				} else {
					statusLine = "HTTP/1.0 404 Not Found" + CRLF ;
					contentTypeLine = "text/html" ;
					entityBody = "<HTML>" +
					    "<HEAD><TITLE>404 Not Found</TITLE></HEAD>" +
					    "<BODY>404 Not Found</BODY></HTML>" ;
				}

				// Send the status line.
				this.output.write(statusLine.getBytes());
		
				// Send the server line.
				this.output.write(serverLine.getBytes());
				
				// Send the content type line.
				this.output.write(contentTypeLine.getBytes());
				
				// Send the Content-Length
				this.output.write(contentLengthLine.getBytes());
				
				this.output.write( ("Connection: close" + CRLF).getBytes() );
				
				// Send a blank line to indicate the end of the header lines.
				this.output.write(CRLF.getBytes());
		
				// Send the entity body.
				if (fileExists) {
					sendBytes(fis, this.output) ;
					fis.close();
				} else {
					this.output.write(entityBody.getBytes());
				}
				this.output.flush();
				//break;
		    } // if GET
		} // while there are more request lines

		try {
		    this.output.close();
		    this.bufferedReader.close();
		    this.socket.close();
		} catch(Exception e) {
			// ignore
		}
    }
   
    private static void sendBytes(FileInputStream fis, OutputStream os)
    throws IOException
    {
		byte[] buffer = new byte[1024*1024] ;
		int bytes = 0 ;

		// Copy requested file into the socket's output stream.
		while ((bytes = fis.read(buffer)) != -1 )
		{
			os.write(buffer, 0, bytes);
		}
    }
   
    private static String contentType(String fileName)
    {
    	if (fileName.endsWith(".jad")) {
    		return "text/vnd.sun.j2me.app-descriptor";
    	} else if (fileName.endsWith(".jar")) {
    		return "application/java";
    	} else if (fileName.endsWith(".htm") || fileName.endsWith(".html"))
    	{
    		return "text/html";
    	}
    	return "";
    }
   
}
