/*
 * Created on Dec 13, 2007 at 1:12:36 PM.
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
import java.io.InputStream;

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
public class Base64InputStream extends InputStream
{

	private static final int     BUFFER_LENGTH = 3;   // Length of buffer (3 or 4)
	private int     position;       // Current position in the buffer
	private byte[]  buffer;         // Small buffer holding converted data
	private int     numSigBytes;    // Number of meaningful bytes in the buffer
	private int     lineLength;
	private int     options;        // Record options used to create the stream.
	private byte[]  decodabet;		// Local copies to avoid extra method calls
	private final InputStream in;
       
       
       /**
        * Constructs a {@link Base64InputStream} without further options.
        *
        * @param in the <tt>java.io.InputStream</tt> from which to read data.
        */
       public Base64InputStream( InputStream in )
       {   
           this( in, Base64.NO_OPTIONS );
       }   // end constructor
       
       
       /**
        * Constructs a {@link Base64InputStream} in
        * <p>
        * Valid options:<pre>
        *   DONT_BREAK_LINES: don't break lines at 76 characters
        *     <i>Note: Technically, this makes your encoding non-compliant.</i>
        * </pre>
        * <p>
        * Example: <code>new Base64InputStream( in, Base64.DONT_BREAK_LINES )</code>
        *
        *
        * @param in the <tt>java.io.InputStream</tt> from which to read data.
        * @param options Specified options
        * @see Base64#DONT_BREAK_LINES
        */
       public Base64InputStream( InputStream in, int options )
       {   
           super();
           this.in = in;
           this.buffer       = new byte[ BUFFER_LENGTH ];
           this.position     = -1;
           this.lineLength   = 0;
			this.options      = options; // Record for later, mostly to determine which alphabet to use
			this.decodabet    = Base64.getDecodabet(options);
       }   // end constructor
       
       /**
        * Reads enough of the input stream to convert
        * to/from Base64 and returns the next byte.
        *
        * @return next byte
        */
       public int read() throws IOException 
       { 
           // Do we need to get data?
           if( this.position < 0 )
           {
               byte[] b4 = new byte[4];
               int i = 0;
               for( i = 0; i < 4; i++ )
               {
                   // Read four "meaningful" bytes:
                   int b = 0;
                   do{ b = this.in.read(); }
                   while( b >= 0 && this.decodabet[ b & 0x7f ] <= Base64.WHITE_SPACE_ENC );
                   
                   if( b < 0 ) {
                       break; // Reads a -1 if end of stream
                   }
                   b4[i] = (byte)b;
               }   // end for: each needed input byte
               
               if( i == 4 )
               {
                   this.numSigBytes = Base64.decode4to3( b4, 0, this.buffer, 0, this.options );
                   this.position = 0;
               }   // end if: got four characters
               else if( i == 0 ){
                   return -1;
               }   // end else if: also padded correctly
               else
               {
                   // Must have broken out from above.
                   throw new IOException( "Improperly padded Base64 input." );
               }   // end 
                   
           }   // end else: get data
           
           // Got data?
           if( this.position >= 0 )
           {
               // End of relevant data?
               if( this.position >= this.numSigBytes ) {
                   return -1;
               }
             
               this.lineLength++;   // This isn't important when decoding
               						// but throwing an extra "if" seems
                               		// just as wasteful.
               
               int b = this.buffer[ this.position++ ];

               if( this.position >= BUFFER_LENGTH ) {
                   this.position = -1;
               }
               return b & 0xFF; // This is how you "cast" a byte that's
                                // intended to be unsigned.
           }   // end if: position >= 0
           
           // Else error
           else
           {   
               throw new IOException( "Error in Base64 code reading stream." );
           }   // end else
       }   // end read
       
       
       /**
        * Calls {@link #read()} repeatedly until the end of stream
        * is reached or <pre>len</pre> bytes are read.
        * Returns number of bytes read into array or -1 if
        * end of stream is encountered.
        *
        * @param dest array to hold values
        * @param off offset for array
        * @param len max number of bytes to read into array
        * @return bytes read into array or -1 if end of stream is encountered.
        */
       public int read( byte[] dest, int off, int len ) throws IOException
       {
           int i;
           int b;
           for( i = 0; i < len; i++ )
           {
               b = read();
               
               //if( b < 0 && i == 0 )
               //    return -1;
               
               if( b >= 0 ) {
                   dest[off + i] = (byte)b;
               } else if( i == 0 ) {
                   return -1;
               } else {
                   break; // Out of 'for' loop
               }
           }   // end for: each byte read
           return i;
       }   // end read


   	/* (non-Javadoc)
   	 * @see java.io.InputStream#read(byte[])
   	 */
   	public int read(byte[] data) throws IOException
   	{
   		return read( data, 0, data.length );
   	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#available()
	 */
	public int available() throws IOException
	{
		return 0;
	}


	/* (non-Javadoc)
	 * @see java.io.InputStream#close()
	 */
	public void close() throws IOException
	{
		this.in.close();
	}


	/* (non-Javadoc)
	 * @see java.io.InputStream#mark(int)
	 */
	public void mark(int readLimit)
	{
		// ignore
	}


	/* (non-Javadoc)
	 * @see java.io.InputStream#markSupported()
	 */
	public boolean markSupported()
	{
		return false;
	}


	/* (non-Javadoc)
	 * @see java.io.InputStream#reset()
	 */
	public void reset() throws IOException
	{
		throw new IOException();
	}


	/* (non-Javadoc)
	 * @see java.io.InputStream#skip(long)
	 */
	public long skip(long n) throws IOException
	{
		return this.in.skip(n);
	}
       
       

}
