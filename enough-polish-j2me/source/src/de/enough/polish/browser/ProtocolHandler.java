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
package de.enough.polish.browser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.StreamConnection;

/**
 * Base class for all protocol handlers. Protocol handlers implement
 * a way to make a <code>StreamConnection</code> for a specific protcol
 * available for the <code>Browser</code>
 *
 * @see Browser
 * @see StreamConnection
 */
public abstract class ProtocolHandler
{
  protected static byte[] bytebuf = new byte[0x1000];

  protected String protocolName;
  
  /**
   * @param protocolName the name of the handled protocol.
   */
  protected ProtocolHandler(String protocolName)
  {
    this.protocolName = protocolName;
  }
  
  /**
   * Strips the protol part off an url.
   * 
   * @param url the url to remove the protocol from
   * 
   * @return the host and part part of the given url
   */
  protected String stripProtocol(String url)
  {
    return url.substring(this.protocolName.length() + 2);
  }

  /**
   * Reads raw bytes from InputStream. Returns a byte array.
   *
   * Attempts to be efficient by reading the stream in buffered chunks.
   *
   * Uses a static preallocated temp buffer, bytebuf. The access to this
   * buffer is synchronized. Usage of this method from several threads
   * is allowed.
   * 
   * @param in the input stream to read from
   * @return the read data in a byte array
   * @throws IOException if an I/O error occurs
   */
  public static byte[] readByteArrayFromStream(InputStream in)
      throws IOException
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    while (true)
    {
      synchronized (bytebuf)
      {
        int len = in.read(bytebuf);
        
        if (len < 0)
        {
          break;
        }
        
        baos.write(bytebuf, 0, len);
      }
    }

    return baos.toByteArray();
  }

  /**
   * Creates a <code>StreamConnection</code> for the given url.
   * 
   * @param url the url
   * 
   * @return the stream conenction for the given url
   * 
   * @throws IOException if an error occurs
   */
  public abstract StreamConnection getConnection(String url)
    throws IOException;

	/**
	 * Retrieves the name of this protocol
	 * 
	 * @return the name of the protocol like "http" or "navigate"
	 */
	public String getProtocolName() {
		return this.protocolName;
	}
}
