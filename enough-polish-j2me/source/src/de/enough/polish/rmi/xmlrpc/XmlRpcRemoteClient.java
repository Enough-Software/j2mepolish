//#condition polish.usePolishGui || polish.midp

/*
 * Created on Dec 9, 2007 at 7:39:26 PM.
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
package de.enough.polish.rmi.xmlrpc;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Hashtable;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import de.enough.polish.io.RedirectHttpConnection;
import de.enough.polish.io.xmlrpc.XmlRpcSerializer;
import de.enough.polish.rmi.RemoteClient;
import de.enough.polish.rmi.RemoteException;
import de.enough.polish.util.TextUtil;
import de.enough.polish.xml.XmlDomNode;
import de.enough.polish.xml.XmlDomParser;

/**
 * <p>Allows to communicate with XML-RPC servers</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Dec 9, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class XmlRpcRemoteClient extends RemoteClient
{

	/**
	 * Creates a new XML-RPC client.
	 * 
	 * @param url the URL of the server
	 */
	public XmlRpcRemoteClient(String url)
	{
		super(url);
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
	protected Object callMethod(String name, long primitivesFlag, Object[] parameters) throws RemoteException
	{
		// prepare call:
		String dot = "__";
		//#if polish.rmi.xmlrpc.methodname.dot:defined
			//#= dot = "${polish.rmi.xmlrpc.methodname.dot}";
		//#endif
		name = TextUtil.replace(name, dot, ".");
		StringBuffer methodBuffer = new StringBuffer();
		methodBuffer.append("<?xml version=\"1.0\"?>")
			.append("<methodCall>")
			.append("<methodName>").append(name).append("</methodName>");
		if (parameters != null && parameters.length > 0) {
			methodBuffer.append("<params>");
			for (int i = 0; i < parameters.length; i++)
			{
				methodBuffer.append("<param><value>");
				Object object = parameters[i];
				try
				{
					XmlRpcSerializer.serialize(methodBuffer, object);
				} catch (IOException e)
				{
					//#debug error
					System.out.println("Unable to serialize " + object +  e );
					throw new RemoteException(e);
				}
				methodBuffer.append("</value></param>");
			}
			methodBuffer.append("</params>");
		}
		methodBuffer.append("</methodCall>");
		byte[] methodData = methodBuffer.toString().getBytes();
		
		// call remote XML-RPC method:
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
			connection.setRequestProperty("Content-Type", "text/xml");
			connection.setRequestProperty("Content-Length", Integer.toString( methodData.length ) );
			// add cookie, if present:
			if (this.cookie != null) {
				connection.setRequestProperty("cookie", this.cookie );
			}
			// write parameters:
			out = connection.openDataOutputStream();
			out.write( methodData );
			// send request and read return values:
			in = connection.openDataInputStream();
			int status = connection.getResponseCode();
			if (status != HttpConnection.HTTP_OK) {
				throw new RemoteException("Server responded with response code " + status );
			} else {
				try {
					out.flush();
				} catch (IllegalStateException e) {
					// ignore
				}
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
				ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
				byte[] readBuffer = new byte[ 8 * 1024 ];
				int read;
				while ((read = in.read(readBuffer)) != -1) {
					byteOut.write(readBuffer, 0, read);
				}
				String response = new String( byteOut.toByteArray() );
				XmlDomNode root = XmlDomParser.parseTree(response);
				XmlDomNode node;
				if ("methodResponse".equals(root.getName())) {
					node = root;
				} else {
					node = root.getChild("methodResponse");
					if (node == null) {
						throw new IOException("Invalid XML RPC Response: " + response);
					}
				}
				node = node.getChild(0);
				if (node.getName().equals("fault")) {
					node = node.getChild("value");
					Hashtable struct = (Hashtable) XmlRpcSerializer.deserialize(node);
					int faultCode = -1;
					Integer faultCodeInt = (Integer) struct.get( "faultCode");
					if (faultCodeInt != null) {
						faultCode = faultCodeInt.intValue();
					}
					String message = (String) struct.get( "faultString" );
					throw new XmlRpcRemoteException( faultCode, message );
				} else {
					node = node.getChild("param").getChild("value");
					return XmlRpcSerializer.deserialize(node);
				}
			}
		} catch (IOException e) {
			// create new RemoteException for this:
			throw new RemoteException( e );					
		} catch (Throwable e) {
			// create new RemoteException for this (e.g. SecurityException):
			//#debug error
			System.out.println("Unexpected error during XML RPC call: " + e);
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

	
}
