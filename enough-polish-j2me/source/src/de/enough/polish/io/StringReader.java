/*
 * Created on 12-Dec-2006 at 12:02:32.
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

import java.io.IOException;
import java.io.Reader;

/**
 * Reader class for String objects.
 *
 * <p>Copyright Enough Software 2006 - 2009</p>
 * 
 * <pre>
 * history
 *        12-Dec-2006 - mkoch creation
 * </pre>
 * 
 * @author Michael Koch, j2mepolish@enough.de
 */
public class StringReader extends Reader
{
  private int index;
  private int mark;
  private int len;
  private String str;
  
  /**
   * Creates a <code>StringReader</code> object.
   * 
   * @param str the string to be read
   */
  public StringReader(String str)
  {
    this.index = 0;
    this.index = 0;
    this.str = str;
    this.len = str.length();
  }

  /* (non-Javadoc)
   * @see java.io.Reader#close()
   */
  public void close() throws IOException
  {
    synchronized (this.lock)
    {
      this.str = null;
    }
  }

  /* (non-Javadoc)
   * @see java.io.Reader#mark(int)
   */
  public void mark(int readAheadLimit) throws IOException
  {
    synchronized (this.lock)
    {
      if (this.str == null)
        throw new IOException("stream closed");
      
      this.mark = this.index;
    }
  }

  /* (non-Javadoc)
   * @see java.io.Reader#markSupported()
   */
  public boolean markSupported()
  {
    return true;
  }

  /* (non-Javadoc)
   * @see java.io.Reader#read()
   */
  public int read() throws IOException
  {
    synchronized (this.lock)
    {
      if (this.str == null)
        throw new IOException("stream closed");
      
      if (this.index >= this.len)
        return -1;

      return (this.str.charAt(this.index++)) & 0xffff;
    }
  }

  /* (non-Javadoc)
   * @see java.io.Reader#read(char[], int, int)
   */
  public int read(char[] cbuf, int off, int length) throws IOException
  {
    synchronized (this.lock)
    {
      if (this.str == null)
        throw new IOException("stream closed");
      
      if (off < 0 || length < 0 || off + length > cbuf.length)
        throw new ArrayIndexOutOfBoundsException();
      
      if (this.index >= this.len)
        return -1;
 
      int lastChar = Math.min(this.len, this.index + length);
      this.str.getChars(this.index, lastChar, cbuf, off);
      int numChars = lastChar - this.index;
      this.index = lastChar;
      return numChars; 
    }
  }

  /* (non-Javadoc)
   * @see java.io.Reader#ready()
   */
  public boolean ready() throws IOException
  {
    if (this.str == null)
      throw new IOException("stream closed");

    return true;
  }

  /* (non-Javadoc)
   * @see java.io.Reader#reset()
   */
  public void reset() throws IOException
  {
    synchronized (this.lock)
    {
      if (this.str == null)
        throw new IOException("stream closed");
      
      this.index = this.mark;
    }
  }

  /* (non-Javadoc)
   * @see java.io.Reader#skip(long)
   */
  public long skip(long n) throws IOException
  {
    synchronized (this.lock)
    {
      long skipped = Math.min((this.len - this.index), n < 0 ? 0L : n);
      this.index += skipped;
      return skipped;
    }
  }
}
