//#condition polish.usePolishGui || polish.midp

/*
 * Created on Dec 20, 2006 at 11:09:51 AM.
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
package de.enough.polish.rmi;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import de.enough.polish.io.RedirectHttpConnection;
import de.enough.polish.io.Serializer;

/**
 * <p>The remote client is capable of calling any server based method and will decode return values accordingly.</p>
 * <p>By default the RemoteClient calls methods in an asynchrone way (in a separate thread). If you are calling methods
 *    from a thread anyhow, you can disable this behavior by setting the "polish.rmi.synchrone" variable to true:
 * <pre>
 *    &lt;variable name=&quot;polish.rmi.synchrone&quot; value=&quot;true&quot; /&gt;
 * </pre>
 * </p> 
 *
 * <p>Copyright Enough Software 2006 - 2009</p>
 * <pre>
 * history
 *        Dec 20, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class RemoteClient  {
	/** The version of the RMI protocol, currently 102 (=1.0.2) is used (support for dynamic obfuscation) */
	//public static final int RMI_VERSION = 101; // = 1.0.1 (support of primitives)
	public static final int RMI_VERSION = 102; // = 1.0.2 (support of dynamic obfuscation)
	
	protected String url;
	protected String cookie;
	
	/**
	 * Createsa new client.
	 * 
	 * @param url the url of the server, e.g. http://myserver.com/myservice
	 */
	protected RemoteClient( String url ) {
		this.url = url;
	}
	
	/**
	 * Retrieves a new remote client implementation for the specified remote interface.
	 * 
	 * @param remoteInterfaceName the name of the remote interface;
	 * @param url the URL of the server, e.g. http://myserver.com/myservice
	 * @return the stub which is capable of connecting to the server
	 */
	public static Remote open( String remoteInterfaceName, String url ) {
		return null;
	}
	
	
	
	/**
	 * Calls a remote method in the same thread.
	 * Note that this method must not be called manually when polish.rmi.synchrone is set to true.
	 * 
	 * @param name the method name
	 * @param primitivesFlag for each element of the parameters which is originally a primitive the bit will be one: 
	 *        element n = primitive means that (primitiveFlags & 2^n) != 0 
	 * @param parameters any parameters, can be null
	 * @return a return value for methods; void methods return null
	 * @throws RemoteException when a checked or an unchecked exception has occurred on the server side or the connection failed
	 */
	protected Object callMethod( String name, long primitivesFlag, Object[] parameters ) throws RemoteException {
		HttpConnection connection = null;
		DataOutputStream out = null;
		DataInputStream in = null;
		try {
			//#if polish.rmi.redirects == false
				connection = (HttpConnection) Connector.open( this.url, Connector.READ_WRITE );
			//#else
				connection = new RedirectHttpConnection( this.url );
			//#endif
			connection.setRequestMethod( HttpConnection.POST );
			// add cookie, if present:
			if (this.cookie != null) {
				connection.setRequestProperty("cookie", this.cookie );
			}
			// write parameters:
			out = connection.openDataOutputStream();
			writeMethodParameters(name, primitivesFlag, parameters, out);
			// send request and read return values:
			in = connection.openDataInputStream();
			int status = connection.getResponseCode();
			if (status != HttpConnection.HTTP_OK) {
				throw new RemoteException("Server responded with response code " + status );
			} else {
				out.flush();
				// okay, call succeeded at least partially:
				// check for cookie:
				String newCookie = connection.getHeaderField("Set-cookie");
				if ( newCookie != null) {
					int semicolonPos = newCookie.indexOf(';');
					//#debug
					System.out.println("received cookie = [" + newCookie + "]");
					if (semicolonPos != -1) {
						// a cookie has a session ID and a domain to which it should be sent, e.g. 
						newCookie = newCookie.substring(0, semicolonPos );
					}
					this.cookie = newCookie;
				}
				// check if the remote call succeeded:
				return readResponse(in);
			}
		} catch (IOException e) {
			// create new RemoteException for this:
			throw new RemoteException( e );					
		} catch (Throwable e) {
			// create new RemoteException for this (e.g. SecurityException):
			throw new RemoteException( e );					
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					// ignore
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
					// ignore
				}
			}
			if (connection != null) {
				try {
					connection.close();
					// on some Series 40 devices we need to set the connection to null,
					// which is weird, to say the least.  Nokia, anyone?
					connection = null;
				} catch (Exception e) {
					// ignore
				}
			}
		}	
	}

	/**
	 * Reads the RMI response from the given input stream
	 * @param in the input
	 * @return the read response data, or throws an exception
	 * @throws IOException when reading fails
	 * @throws RemoteException when a remote exception occurred
	 */
	protected Object readResponse(DataInputStream in) 
	throws IOException, RemoteException
	{
		int remoteCallStatus = in.readInt();
		switch ( remoteCallStatus ) {
		case Remote.STATUS_OK:
			return Serializer.deserialize(in);
		case Remote.STATUS_CHECKED_EXCEPTION:
			Throwable exception = (Throwable) Serializer.deserialize(in);
			throw new RemoteException( exception );
		case Remote.STATUS_UNCHECKED_EXCEPTION:
			String message = in.readUTF();
			throw new RemoteException( message );
		default:
			throw new RemoteException( "unknown RMI status: " + remoteCallStatus );
		}
	}

	/**
	 * Writes a method request to the specified output stream
	 * @param name the name of the method
 	 * @param primitivesFlag flag that indicates which parameters are primitive
	 * @param parameters the parameters
	 * @param out the output stream
	 * @throws IOException when writing fails
	 */
	protected void writeMethodParameters(String name, long primitivesFlag,
			Object[] parameters, DataOutputStream out) throws IOException
	{
		out.writeInt( RMI_VERSION );
		//#if polish.obfuscate
			out.writeBoolean( true );
		//#else
			out.writeBoolean( false );
		//#endif
		out.writeUTF( name );
		out.writeLong( primitivesFlag );
		Serializer.serialize( parameters, out);
	}

	
}
