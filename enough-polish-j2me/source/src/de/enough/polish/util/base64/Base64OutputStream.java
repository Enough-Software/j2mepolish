/*
 * Created on Dec 13, 2007 at 1:16:06 PM.
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
package de.enough.polish.util.base64;

import java.io.IOException;
import java.io.OutputStream;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Dec 13, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class Base64OutputStream extends OutputStream
{
	private static final int     BUFFER_LENGTH = 3;
    private int     position;
    private byte[]  buffer;
    private int     lineLength;
    private boolean breakLines;
    private byte[]  b4; // Scratch used in a few places
    private boolean suspendEncoding;
	private byte[]  alphabet;		// Local copies to avoid extra method calls
	private OutputStream out;
    
    /**
     * Constructs a {@link Base64OutputStream} in ENCODE mode.
     *
     * @param out the <tt>java.io.OutputStream</tt> to which data will be written.
     * @since 1.3
     */
    public Base64OutputStream( OutputStream out )
    {   
        this( out, Base64.NO_OPTIONS );
    }   // end constructor
    
    
    /**
     * Constructs a {@link Base64OutputStream} in
     * either ENCODE or DECODE mode.
     * <p>
     * Valid options:<pre>
     *   ENCODE or DECODE: Encode or Decode as data is read.
     *   DONT_BREAK_LINES: don't break lines at 76 characters
     *     (only meaningful when encoding)
     *     <i>Note: Technically, this makes your encoding non-compliant.</i>
     * </pre>
     * <p>
     * Example: <code>new Base64.OutputStream( out, Base64.ENCODE )</code>
     *
     * @param out the <tt>java.io.OutputStream</tt> to which data will be written.
     * @param options Specified options.
     * @see Base64#DONT_BREAK_LINES
     * @since 1.3
     */
    public Base64OutputStream( OutputStream out, int options )
    {   
        super();
		this.out = out;
        this.breakLines   = (options & Base64.DONT_BREAK_LINES) != Base64.DONT_BREAK_LINES;
        this.buffer       = new byte[ BUFFER_LENGTH ];
        this.position     = 0;
        this.lineLength   = 0;
        this.suspendEncoding = false;
        this.b4           = new byte[4];
		this.alphabet    = Base64.getAlphabet(options);
    }   // end constructor
    
    
    /**
     * Writes the byte to the output stream after
     * converting to/from Base64 notation.
     * When encoding, bytes are buffered three
     * at a time before the output stream actually
     * gets a write() call.
     * When decoding, bytes are buffered four
     * at a time.
     *
     * @param theByte the byte to write
     */
    public void write(int theByte) throws IOException
    {
        // Encoding suspended?
        if( this.suspendEncoding )
        {
            this.out.write( theByte );
            return;
        }   // end if: suspended
        this.buffer[ this.position++ ] = (byte)theByte;
        if( this.position >= BUFFER_LENGTH )  // Enough to encode.
        {
        	byte[] data = Base64.encode3to4( this.b4, this.buffer, BUFFER_LENGTH, this.alphabet );
            this.out.write( data );

            this.lineLength += 4;
            if( this.breakLines && this.lineLength >= Base64.MAX_LINE_LENGTH )
            {
                this.out.write( Base64.NEW_LINE );
                this.lineLength = 0;
            }   // end if: end of line

            this.position = 0;
        }   // end if: enough to output
    }   // end write
    
    
    
    /**
     * Calls {@link #write(int)} repeatedly until <pre>len</pre> 
     * bytes are written.
     *
     * @param theBytes array from which to read bytes
     * @param off offset for array
     * @param len max number of bytes to read into array
     */
    public void write( byte[] theBytes, int off, int len ) throws IOException
    {
        // Encoding suspended?
        if( this.suspendEncoding )
        {
            this.out.write( theBytes, off, len );
            return;
        }   // end if: suspended
        
        for( int i = 0; i < len; i++ )
        {
            write( theBytes[ off + i ] );
        }   // end for: each byte written
        
    }   // end write
    
    /* (non-Javadoc)
	 * @see java.io.OutputStream#write(byte[])
	 */
	public void write(byte[] data) throws IOException
	{
		write( data, 0, data.length );
	}
    
    
    /**
     * Method added by PHIL. [Thanks, PHIL. -Rob]
     * This pads the buffer without closing the stream.
     * @throws IOException when the stream could not be flushed
     */
    public void flushBase64() throws IOException 
    {
        if( this.position > 0 )
        {
            this.out.write( Base64.encode3to4( this.b4, this.buffer, this.position, this.alphabet ) );
            this.position = 0;
        }   // end if: buffer partially full

    }   // end flush


	/* (non-Javadoc)
	 * @see java.io.OutputStream#flush()
	 */
	public void flush() throws IOException
	{
		flushBase64();
		this.out.flush();
	}


    
    /** 
     * Flushes and closes (I think, in the superclass) the stream. 
     */
    public void close() throws IOException
    {
        // 1. Ensure that pending characters are written
        flushBase64();

        // 2. Actually close the stream
        // Base class both flushes and closes.
        this.out.close();
        
        this.buffer = null;
        this.out    = null;
    }   // end close
    
    
    
    /**
     * Suspends encoding of the stream.
     * May be helpful if you need to embed a piece of
     * base64-encoded data in a stream.
     * @throws IOException when the stream could not be flushed
     * @see #flushBase64()
     */
    public void suspendEncoding() throws IOException 
    {
        flushBase64();
        this.suspendEncoding = true;
    }   // end suspendEncoding
    
    
    /**
     * Resumes encoding of the stream.
     * May be helpful if you need to embed a piece of
     * base64-encoded data in a stream.
     */
    public void resumeEncoding()
    {
        this.suspendEncoding = false;
    }   // end resumeEncoding

}
