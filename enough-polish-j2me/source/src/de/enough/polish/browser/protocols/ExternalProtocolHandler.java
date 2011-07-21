//#condition polish.midp2
/*
 * Created on 11-Jan-2006 at 19:20:28.
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
package de.enough.polish.browser.protocols;

import java.io.IOException;

import javax.microedition.io.StreamConnection;
import javax.microedition.midlet.MIDlet;

import de.enough.polish.browser.ProtocolHandler;

/**
 * Protocol handler to handle the <code>external:</code> protocol. This class calls the given URLs in an external browser on MIDP 2.0 phones.
 * Example: &lt;a href=&quot;external:http://www.j2mepolish.org&quot;>J2ME Polish&lt;/a&gt;
 * Note that this handler requires MIDP 2.0 or higher.
 */
public class ExternalProtocolHandler
  extends ProtocolHandler
{
  private MIDlet midlet;
  
  /**
   * Creates an ExternalProtocolHandler object using the default "external" protocol name.
   * 
   * @param midlet the midlet object of the application
   */
  public ExternalProtocolHandler(MIDlet midlet)
  {
    this( "external", midlet );
  }

  /**
   * Creates an ExternalProtocolHandler object using the specified protocol name.
   * 
   * @param protocolName the name of the protocol to handle
   * @param midlet the midlet object of the application
   */
  public ExternalProtocolHandler(String protocolName, MIDlet midlet)
  {
    super( protocolName );
    this.midlet = midlet;
  }

  
  /* (non-Javadoc)
   * @see de.enough.polish.browser.ProtocolHandler#getConnection(java.lang.String)
   */
  public StreamConnection getConnection(String url) throws IOException
  {
    this.midlet.platformRequest(stripProtocol(url));
    
    return null;
  }
  
  /**
   * Strips the protol part off an url. 
   * In contrast to other protocol handlers, the external protocol handler only uses a single colon to
   * separate the external protocol from the folllowing protocol, e.g. external:http://www.j2mepolish.org
   * 
   * @param url the url to remove the protocol from
   * 
   * @return the host and part part of the given url
   */
  protected String stripProtocol(String url)
  {
    return url.substring(this.protocolName.length() + 1);
  }

}
