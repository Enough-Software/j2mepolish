/*
 * Created on Dec 13, 2007 at 1:03:17 PM.
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

import java.io.UnsupportedEncodingException;

/**
 * <p>A Base64 Encoder/Decoder based on Robert's Harder Java SE implementation available at <a href="http://iharder.net/base64">http://iharder.net/base64</a>.</p>
 * 
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Dec 13, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class Base64
{

/* ********  P U B L I C   F I E L D S  ******** */   
	    
    
    /** No options specified. Value is zero. */
    public final static int NO_OPTIONS = 0;
    
    
    /** Don't break lines when encoding (violates strict Base64 specification) */
    public final static int DONT_BREAK_LINES = 8;
	
	/** 
	 * Encode using Base64-like encoding that is URL- and Filename-safe as described
	 * in Section 4 of RFC3548: 
	 * <a href="http://www.faqs.org/rfcs/rfc3548.html">http://www.faqs.org/rfcs/rfc3548.html</a>.
	 * It is important to note that data encoded this way is <em>not</em> officially valid Base64, 
	 * or at the very least should not be called Base64 without also specifying that is
	 * was encoded using the URL- and Filename-safe dialect.
	 */
	 public final static int URL_SAFE = 16;
	 
	 
	 /**
	  * Encode using the special "ordered" dialect of Base64 described here:
	  * <a href="http://www.faqs.org/qa/rfcc-1940.html">http://www.faqs.org/qa/rfcc-1940.html</a>.
	  */
	 public final static int ORDERED = 32;
    
    
/* ********  P R I V A T E   F I E L D S  ******** */  
    
    
    /** Maximum line length (76) of Base64 output. */
    protected final static int MAX_LINE_LENGTH = 76;
    
    
    /** The equals sign (=) as a byte. */
    private final static int EQUALS_SIGN = '=';
    
    
    /** The new line character (\n) as a byte. */
    protected final static int NEW_LINE = '\n';
    
    
    /** Preferred encoding. */
    private final static String PREFERRED_ENCODING = "UTF-8";
    
	
    // I think I end up not using the BAD_ENCODING indicator.
    //private final static byte BAD_ENCODING    = -9; // Indicates error in encoding
    protected final static byte WHITE_SPACE_ENC = -5; // Indicates white space in encoding
    private final static byte EQUALS_SIGN_ENC = -1; // Indicates equals sign in encoding
	
	
/* ********  S T A N D A R D   B A S E 6 4   A L P H A B E T  ******** */	
    
    /** The 64 valid Base64 values. */
    //private final static byte[] ALPHABET;
	/* Host platform me be something funny like EBCDIC, so we hardcode these values. */
	private final static byte[] _STANDARD_ALPHABET =
    {
        (byte)'A', (byte)'B', (byte)'C', (byte)'D', (byte)'E', (byte)'F', (byte)'G',
        (byte)'H', (byte)'I', (byte)'J', (byte)'K', (byte)'L', (byte)'M', (byte)'N',
        (byte)'O', (byte)'P', (byte)'Q', (byte)'R', (byte)'S', (byte)'T', (byte)'U', 
        (byte)'V', (byte)'W', (byte)'X', (byte)'Y', (byte)'Z',
        (byte)'a', (byte)'b', (byte)'c', (byte)'d', (byte)'e', (byte)'f', (byte)'g',
        (byte)'h', (byte)'i', (byte)'j', (byte)'k', (byte)'l', (byte)'m', (byte)'n',
        (byte)'o', (byte)'p', (byte)'q', (byte)'r', (byte)'s', (byte)'t', (byte)'u', 
        (byte)'v', (byte)'w', (byte)'x', (byte)'y', (byte)'z',
        (byte)'0', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'5', 
        (byte)'6', (byte)'7', (byte)'8', (byte)'9', (byte)'+', (byte)'/'
    };
	
    
    /** 
     * Translates a Base64 value to either its 6-bit reconstruction value
     * or a negative number indicating some other meaning.
     **/
    private final static byte[] _STANDARD_DECODABET =
    {   
        -9,-9,-9,-9,-9,-9,-9,-9,-9,                 // Decimal  0 -  8
        -5,-5,                                      // Whitespace: Tab and Linefeed
        -9,-9,                                      // Decimal 11 - 12
        -5,                                         // Whitespace: Carriage Return
        -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 14 - 26
        -9,-9,-9,-9,-9,                             // Decimal 27 - 31
        -5,                                         // Whitespace: Space
        -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,              // Decimal 33 - 42
        62,                                         // Plus sign at decimal 43
        -9,-9,-9,                                   // Decimal 44 - 46
        63,                                         // Slash at decimal 47
        52,53,54,55,56,57,58,59,60,61,              // Numbers zero through nine
        -9,-9,-9,                                   // Decimal 58 - 60
        -1,                                         // Equals sign at decimal 61
        -9,-9,-9,                                      // Decimal 62 - 64
        0,1,2,3,4,5,6,7,8,9,10,11,12,13,            // Letters 'A' through 'N'
        14,15,16,17,18,19,20,21,22,23,24,25,        // Letters 'O' through 'Z'
        -9,-9,-9,-9,-9,-9,                          // Decimal 91 - 96
        26,27,28,29,30,31,32,33,34,35,36,37,38,     // Letters 'a' through 'm'
        39,40,41,42,43,44,45,46,47,48,49,50,51,     // Letters 'n' through 'z'
        -9,-9,-9,-9                                 // Decimal 123 - 126
        /*,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 127 - 139
        -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 140 - 152
        -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 153 - 165
        -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 166 - 178
        -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 179 - 191
        -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 192 - 204
        -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 205 - 217
        -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 218 - 230
        -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 231 - 243
        -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9         // Decimal 244 - 255 */
    };
	
	
/* ********  U R L   S A F E   B A S E 6 4   A L P H A B E T  ******** */
	
	/**
	 * Used in the URL- and Filename-safe dialect described in Section 4 of RFC3548: 
	 * <a href="http://www.faqs.org/rfcs/rfc3548.html">http://www.faqs.org/rfcs/rfc3548.html</a>.
	 * Notice that the last two bytes become "hyphen" and "underscore" instead of "plus" and "slash."
	 */
    private final static byte[] _URL_SAFE_ALPHABET =
    {
      (byte)'A', (byte)'B', (byte)'C', (byte)'D', (byte)'E', (byte)'F', (byte)'G',
      (byte)'H', (byte)'I', (byte)'J', (byte)'K', (byte)'L', (byte)'M', (byte)'N',
      (byte)'O', (byte)'P', (byte)'Q', (byte)'R', (byte)'S', (byte)'T', (byte)'U', 
      (byte)'V', (byte)'W', (byte)'X', (byte)'Y', (byte)'Z',
      (byte)'a', (byte)'b', (byte)'c', (byte)'d', (byte)'e', (byte)'f', (byte)'g',
      (byte)'h', (byte)'i', (byte)'j', (byte)'k', (byte)'l', (byte)'m', (byte)'n',
      (byte)'o', (byte)'p', (byte)'q', (byte)'r', (byte)'s', (byte)'t', (byte)'u', 
      (byte)'v', (byte)'w', (byte)'x', (byte)'y', (byte)'z',
      (byte)'0', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'5', 
      (byte)'6', (byte)'7', (byte)'8', (byte)'9', (byte)'-', (byte)'_'
    };
	
	/**
	 * Used in decoding URL- and Filename-safe dialects of Base64.
	 */
    private final static byte[] _URL_SAFE_DECODABET =
    {   
      -9,-9,-9,-9,-9,-9,-9,-9,-9,                 // Decimal  0 -  8
      -5,-5,                                      // Whitespace: Tab and Linefeed
      -9,-9,                                      // Decimal 11 - 12
      -5,                                         // Whitespace: Carriage Return
      -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 14 - 26
      -9,-9,-9,-9,-9,                             // Decimal 27 - 31
      -5,                                         // Whitespace: Space
      -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,              // Decimal 33 - 42
      -9,                                         // Plus sign at decimal 43
      -9,                                         // Decimal 44
      62,                                         // Minus sign at decimal 45
      -9,                                         // Decimal 46
      -9,                                         // Slash at decimal 47
      52,53,54,55,56,57,58,59,60,61,              // Numbers zero through nine
      -9,-9,-9,                                   // Decimal 58 - 60
      -1,                                         // Equals sign at decimal 61
      -9,-9,-9,                                   // Decimal 62 - 64
      0,1,2,3,4,5,6,7,8,9,10,11,12,13,            // Letters 'A' through 'N'
      14,15,16,17,18,19,20,21,22,23,24,25,        // Letters 'O' through 'Z'
      -9,-9,-9,-9,                                // Decimal 91 - 94
      63,                                         // Underscore at decimal 95
      -9,                                         // Decimal 96
      26,27,28,29,30,31,32,33,34,35,36,37,38,     // Letters 'a' through 'm'
      39,40,41,42,43,44,45,46,47,48,49,50,51,     // Letters 'n' through 'z'
      -9,-9,-9,-9                                 // Decimal 123 - 126
      /*,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 127 - 139
      -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 140 - 152
      -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 153 - 165
      -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 166 - 178
      -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 179 - 191
      -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 192 - 204
      -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 205 - 217
      -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 218 - 230
      -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 231 - 243
      -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9         // Decimal 244 - 255 */
    };



/* ********  O R D E R E D   B A S E 6 4   A L P H A B E T  ******** */

	/**
	 * I don't get the point of this technique, but it is described here:
	 * <a href="http://www.faqs.org/qa/rfcc-1940.html">http://www.faqs.org/qa/rfcc-1940.html</a>.
	 */
    private final static byte[] _ORDERED_ALPHABET =
    {
      (byte)'-',
      (byte)'0', (byte)'1', (byte)'2', (byte)'3', (byte)'4',
      (byte)'5', (byte)'6', (byte)'7', (byte)'8', (byte)'9',
      (byte)'A', (byte)'B', (byte)'C', (byte)'D', (byte)'E', (byte)'F', (byte)'G',
      (byte)'H', (byte)'I', (byte)'J', (byte)'K', (byte)'L', (byte)'M', (byte)'N',
      (byte)'O', (byte)'P', (byte)'Q', (byte)'R', (byte)'S', (byte)'T', (byte)'U',
      (byte)'V', (byte)'W', (byte)'X', (byte)'Y', (byte)'Z',
      (byte)'_',
      (byte)'a', (byte)'b', (byte)'c', (byte)'d', (byte)'e', (byte)'f', (byte)'g',
      (byte)'h', (byte)'i', (byte)'j', (byte)'k', (byte)'l', (byte)'m', (byte)'n',
      (byte)'o', (byte)'p', (byte)'q', (byte)'r', (byte)'s', (byte)'t', (byte)'u',
      (byte)'v', (byte)'w', (byte)'x', (byte)'y', (byte)'z'
    };
	
	/**
	 * Used in decoding the "ordered" dialect of Base64.
	 */
    private final static byte[] _ORDERED_DECODABET =
    {   
      -9,-9,-9,-9,-9,-9,-9,-9,-9,                 // Decimal  0 -  8
      -5,-5,                                      // Whitespace: Tab and Linefeed
      -9,-9,                                      // Decimal 11 - 12
      -5,                                         // Whitespace: Carriage Return
      -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 14 - 26
      -9,-9,-9,-9,-9,                             // Decimal 27 - 31
      -5,                                         // Whitespace: Space
      -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,              // Decimal 33 - 42
      -9,                                         // Plus sign at decimal 43
      -9,                                         // Decimal 44
      0,                                          // Minus sign at decimal 45
      -9,                                         // Decimal 46
      -9,                                         // Slash at decimal 47
      1,2,3,4,5,6,7,8,9,10,                       // Numbers zero through nine
      -9,-9,-9,                                   // Decimal 58 - 60
      -1,                                         // Equals sign at decimal 61
      -9,-9,-9,                                   // Decimal 62 - 64
      11,12,13,14,15,16,17,18,19,20,21,22,23,     // Letters 'A' through 'M'
      24,25,26,27,28,29,30,31,32,33,34,35,36,     // Letters 'N' through 'Z'
      -9,-9,-9,-9,                                // Decimal 91 - 94
      37,                                         // Underscore at decimal 95
      -9,                                         // Decimal 96
      38,39,40,41,42,43,44,45,46,47,48,49,50,     // Letters 'a' through 'm'
      51,52,53,54,55,56,57,58,59,60,61,62,63,     // Letters 'n' through 'z'
      -9,-9,-9,-9                                 // Decimal 123 - 126
      /*,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 127 - 139
        -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 140 - 152
        -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 153 - 165
        -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 166 - 178
        -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 179 - 191
        -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 192 - 204
        -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 205 - 217
        -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 218 - 230
        -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 231 - 243
        -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9         // Decimal 244 - 255 */
    };

	
/* ********  D E T E R M I N E   W H I C H   A L H A B E T  ******** */


	/**
	 * Returns one of the _SOMETHING_ALPHABET byte arrays depending on
	 * the options specified.
	 * It's possible, though silly, to specify ORDERED and URLSAFE
	 * in which case one of them will be picked, though there is
	 * no guarantee as to which one will be picked.
	 */
	protected final static byte[] getAlphabet( int options )
	{
		if( (options & URL_SAFE) == URL_SAFE ) return _URL_SAFE_ALPHABET;
		else if( (options & ORDERED) == ORDERED ) return _ORDERED_ALPHABET;
		else return _STANDARD_ALPHABET;
		
	}	// end getAlphabet
	
	
	/**
	 * Returns one of the _SOMETHING_DECODABET byte arrays depending on
	 * the options specified.
	 * It's possible, though silly, to specify ORDERED and URL_SAFE
	 * in which case one of them will be picked, though there is
	 * no guarantee as to which one will be picked.
	 */
	protected final static byte[] getDecodabet( int options )
	{
		if( (options & URL_SAFE) == URL_SAFE ) return _URL_SAFE_DECODABET;
		else if( (options & ORDERED) == ORDERED ) return _ORDERED_DECODABET;
		else return _STANDARD_DECODABET;
		
	}	// end getAlphabet


	private Base64()
	{
		// disallow instantiation
	}
    
/* ********  E N C O D I N G   M E T H O D S  ******** */    
    
    
    /**
     * Encodes up to the first three bytes of array <pre>threeBytes</pre>
     * and returns a four-byte array in Base64 notation.
     * The actual number of significant bytes in your array is
     * given by <pre>numSigBytes</pre>.
     * The array <pre>threeBytes</pre> needs only be as big as
     * <pre>numSigBytes</pre>.
     * Code can reuse a byte array by passing a four-byte array as <pre>b4</pre>.
     *
     * @param b4 A reusable byte array to reduce array instantiation
     * @param threeBytes the array to convert
     * @param numSigBytes the number of significant bytes in your array
     * @return four byte array in Base64 notation.
     */
    protected static byte[] encode3to4( byte[] b4, byte[] threeBytes, int numSigBytes, int options )
    {
        encode3to4( threeBytes, 0, numSigBytes, b4, 0, getAlphabet( options ) );
        return b4;
    }   // end encode3to4
    
    /**
     * Encodes up to the first three bytes of array <pre>threeBytes</pre>
     * and returns a four-byte array in Base64 notation.
     * The actual number of significant bytes in your array is
     * given by <pre>numSigBytes</pre>.
     * The array <pre>threeBytes</pre> needs only be as big as
     * <pre>numSigBytes</pre>.
     * Code can reuse a byte array by passing a four-byte array as <pre>b4</pre>.
     *
     * @param b4 A reusable byte array to reduce array instantiation
     * @param threeBytes the array to convert
     * @param numSigBytes the number of significant bytes in your array
     * @return four byte array in Base64 notation.
     */
    protected static byte[] encode3to4( byte[] b4, byte[] threeBytes, int numSigBytes, byte[] alphabet )
    {
        encode3to4( threeBytes, 0, numSigBytes, b4, 0, alphabet );
        return b4;
    }   // end encode3to4


    
    /**
     * <p>Encodes up to three bytes of the array <pre>source</pre>
     * and writes the resulting four Base64 bytes to <pre>destination</pre>.
     * The source and destination arrays can be manipulated
     * anywhere along their length by specifying 
     * <pre>srcOffset</pre> and <pre>destOffset</pre>.
     * This method does not check to make sure your arrays
     * are large enough to accomodate <pre>srcOffset</pre> + 3 for
     * the <pre>source</pre> array or <pre>destOffset</pre> + 4 for
     * the <pre>destination</pre> array.
     * The actual number of significant bytes in your array is
     * given by <pre>numSigBytes</pre>.</p>
	 * <p>This is the lowest level of the encoding methods with
	 * all possible parameters.</p>
     *
     * @param source the array to convert
     * @param srcOffset the index where conversion begins
     * @param numSigBytes the number of significant bytes in your array
     * @param destination the array to hold the conversion
     * @param destOffset the index where output will be put
     * @return the <pre>destination</pre> array
     */
    protected static byte[] encode3to4( 
     byte[] source, int srcOffset, int numSigBytes,
     byte[] destination, int destOffset, int options )
    {
		byte[] alphabet = getAlphabet( options ); 
		return encode3to4( source, srcOffset, numSigBytes, destination, destOffset, alphabet );
    }
    
	 /**
     * <p>Encodes up to three bytes of the array <pre>source</pre>
     * and writes the resulting four Base64 bytes to <pre>destination</pre>.
     * The source and destination arrays can be manipulated
     * anywhere along their length by specifying 
     * <pre>srcOffset</pre> and <pre>destOffset</pre>.
     * This method does not check to make sure your arrays
     * are large enough to accomodate <pre>srcOffset</pre> + 3 for
     * the <pre>source</pre> array or <pre>destOffset</pre> + 4 for
     * the <pre>destination</pre> array.
     * The actual number of significant bytes in your array is
     * given by <pre>numSigBytes</pre>.</p>
	 * <p>This is the lowest level of the encoding methods with
	 * all possible parameters.</p>
     *
     * @param source the array to convert
     * @param srcOffset the index where conversion begins
     * @param numSigBytes the number of significant bytes in your array
     * @param destination the array to hold the conversion
     * @param destOffset the index where output will be put
     * @return the <pre>destination</pre> array
     */
    protected static byte[] encode3to4( 
     byte[] source, int srcOffset, int numSigBytes,
     byte[] destination, int destOffset, byte[] alphabet )
    {
	
        //           1         2         3  
        // 01234567890123456789012345678901 Bit position
        // --------000000001111111122222222 Array position from threeBytes
        // --------|    ||    ||    ||    | Six bit groups to index ALPHABET
        //          >>18  >>12  >> 6  >> 0  Right shift necessary
        //                0x3f  0x3f  0x3f  Additional AND
        
        // Create buffer with zero-padding if there are only one or two
        // significant bytes passed in the array.
        // We have to shift left 24 in order to flush out the 1's that appear
        // when Java treats a value as negative that is cast from a byte to an int.
        int inBuff =   ( numSigBytes > 0 ? ((source[ srcOffset     ] << 24) >>>  8) : 0 )
                     | ( numSigBytes > 1 ? ((source[ srcOffset + 1 ] << 24) >>> 16) : 0 )
                     | ( numSigBytes > 2 ? ((source[ srcOffset + 2 ] << 24) >>> 24) : 0 );

        switch( numSigBytes )
        {
            case 3:
                destination[ destOffset     ] = alphabet[ (inBuff >>> 18)        ];
                destination[ destOffset + 1 ] = alphabet[ (inBuff >>> 12) & 0x3f ];
                destination[ destOffset + 2 ] = alphabet[ (inBuff >>>  6) & 0x3f ];
                destination[ destOffset + 3 ] = alphabet[ (inBuff       ) & 0x3f ];
                return destination;
                
            case 2:
                destination[ destOffset     ] = alphabet[ (inBuff >>> 18)        ];
                destination[ destOffset + 1 ] = alphabet[ (inBuff >>> 12) & 0x3f ];
                destination[ destOffset + 2 ] = alphabet[ (inBuff >>>  6) & 0x3f ];
                destination[ destOffset + 3 ] = EQUALS_SIGN;
                return destination;
                
            case 1:
                destination[ destOffset     ] = alphabet[ (inBuff >>> 18)        ];
                destination[ destOffset + 1 ] = alphabet[ (inBuff >>> 12) & 0x3f ];
                destination[ destOffset + 2 ] = EQUALS_SIGN;
                destination[ destOffset + 3 ] = EQUALS_SIGN;
                return destination;
                
            default:
                return destination;
        }   // end switch
    }   // end encode3to4
    
    /**
     * Encodes the specified string in base64 notation
     * @param data the data in String format
     * @return the encoded data in String format
     */
    public static String encode( String data ) {
    	return encodeBytes( data.getBytes() );
    }
    
 
    /**
     * Encodes a byte array into Base64 notation.
     *
     * @param source The data to convert
     * @return the encoded String
     */
    public static String encodeBytes( byte[] source )
    {
        return encodeBytes( source, 0, source.length, NO_OPTIONS );
    }   // end encodeBytes
    


    /**
     * Encodes a byte array into Base64 notation.
     * <p>
     * Valid options:<pre>
     *   DONT_BREAK_LINES: don't break lines at 76 characters
     *     <i>Note: Technically, this makes your encoding non-compliant.</i>
     * </pre>
     * <p>
     * Example: <code>encodeBytes( myData, Base64.DONT_BREAK_LINES )</code>
     *
     *
     * @param source The data to convert
     * @param options Specified options
     * @see Base64#DONT_BREAK_LINES
     * @return the encoded String
     */
    public static String encodeBytes( byte[] source, int options )
    {   
        return encodeBytes( source, 0, source.length, options );
    }   // end encodeBytes
    
    
    /**
     * Encodes a byte array into Base64 notation.
     *
     * @param source The data to convert
     * @param off Offset in array where conversion should begin
     * @param len Length of data to convert
     * @return the encoded String
     */
    public static String encodeBytes( byte[] source, int off, int len )
    {
        return encodeBytes( source, off, len, NO_OPTIONS );
    }   // end encodeBytes
    
    

    /**
     * Encodes a byte array into Base64 notation.
     * <p>
     * Valid options:<pre>
     *   DONT_BREAK_LINES: don't break lines at 76 characters
     *     <i>Note: Technically, this makes your encoding non-compliant.</i>
     * </pre>
     * <p>
     * Example: <code>encodeBytes( myData, Base64.DONT_BREAK_LINES )</code>
     *
     *
     * @param source The data to convert
     * @param off Offset in array where conversion should begin
     * @param len Length of data to convert
     * @param options Specified options
     * @return the encoded String
     * @see Base64#DONT_BREAK_LINES
     */
    public static String encodeBytes( byte[] source, int off, int len, int options )
    {
        // Convert option to boolean in way that code likes it.
        boolean breakLines = ( options & DONT_BREAK_LINES ) == 0;
        
        int    len43   = len * 4 / 3;
        byte[] outBuff = new byte[   ( len43 )                      // Main 4:3
                                   + ( (len % 3) > 0 ? 4 : 0 )      // Account for padding
                                   + (breakLines ? ( len43 / MAX_LINE_LENGTH ) : 0) ]; // New lines      
        int d = 0;
        int e = 0;
        int len2 = len - 2;
        int lineLength = 0;
        for( ; d < len2; d+=3, e+=4 )
        {
            encode3to4( source, d+off, 3, outBuff, e, options );

            lineLength += 4;
            if( breakLines && lineLength == MAX_LINE_LENGTH )
            {   
                outBuff[e+4] = NEW_LINE;
                e++;
                lineLength = 0;
            }   // end if: end of line
        }   // end for: each piece of array

        if( d < len )
        {
            encode3to4( source, d+off, len - d, outBuff, e, options );
            e += 4;
        }   // end if: some padding needed

        
        // Return value according to relevant encoding.
        try
        {
            return new String( outBuff, 0, e, PREFERRED_ENCODING );
        }   // end try
        catch (UnsupportedEncodingException uue)
        {
            return new String( outBuff, 0, e );
        }   // end catch
        
        
    }   // end encodeBytes
    

    
    
    
/* ********  D E C O D I N G   M E T H O D S  ******** */
    
    
    /**
     * Decodes four bytes from array <pre>source</pre>
     * and writes the resulting bytes (up to three of them)
     * to <pre>destination</pre>.
     * The source and destination arrays can be manipulated
     * anywhere along their length by specifying 
     * <pre>srcOffset</pre> and <pre>destOffset</pre>.
     * This method does not check to make sure your arrays
     * are large enough to accomodate <pre>srcOffset</pre> + 4 for
     * the <pre>source</pre> array or <pre>destOffset</pre> + 3 for
     * the <pre>destination</pre> array.
     * This method returns the actual number of bytes that 
     * were converted from the Base64 encoding.
	 * <p>This is the lowest level of the decoding methods with
	 * all possible parameters.</p>
     * 
     *
     * @param source the array to convert
     * @param srcOffset the index where conversion begins
     * @param destination the array to hold the conversion
     * @param destOffset the index where output will be put
	 * @param options alphabet type is pulled from this (standard, url-safe, ordered)
     * @return the number of decoded bytes converted
     */
    protected static int decode4to3( byte[] source, int srcOffset, byte[] destination, int destOffset, int options )
    {
		byte[] DECODABET = getDecodabet( options ); 
	
        // Example: Dk==
        if( source[ srcOffset + 2] == EQUALS_SIGN )
        {
            // Two ways to do the same thing. Don't know which way I like best.
            //int outBuff =   ( ( DECODABET[ source[ srcOffset    ] ] << 24 ) >>>  6 )
            //              | ( ( DECODABET[ source[ srcOffset + 1] ] << 24 ) >>> 12 );
            int outBuff =   ( ( DECODABET[ source[ srcOffset    ] ] & 0xFF ) << 18 )
                          | ( ( DECODABET[ source[ srcOffset + 1] ] & 0xFF ) << 12 );
            
            destination[ destOffset ] = (byte)( outBuff >>> 16 );
            return 1;
        }
        
        // Example: DkL=
        else if( source[ srcOffset + 3 ] == EQUALS_SIGN )
        {
            // Two ways to do the same thing. Don't know which way I like best.
            //int outBuff =   ( ( DECODABET[ source[ srcOffset     ] ] << 24 ) >>>  6 )
            //              | ( ( DECODABET[ source[ srcOffset + 1 ] ] << 24 ) >>> 12 )
            //              | ( ( DECODABET[ source[ srcOffset + 2 ] ] << 24 ) >>> 18 );
            int outBuff =   ( ( DECODABET[ source[ srcOffset     ] ] & 0xFF ) << 18 )
                          | ( ( DECODABET[ source[ srcOffset + 1 ] ] & 0xFF ) << 12 )
                          | ( ( DECODABET[ source[ srcOffset + 2 ] ] & 0xFF ) <<  6 );
            
            destination[ destOffset     ] = (byte)( outBuff >>> 16 );
            destination[ destOffset + 1 ] = (byte)( outBuff >>>  8 );
            return 2;
        }
        
        // Example: DkLE
        else
        {
            try{
	            // Two ways to do the same thing. Don't know which way I like best.
	            //int outBuff =   ( ( DECODABET[ source[ srcOffset     ] ] << 24 ) >>>  6 )
	            //              | ( ( DECODABET[ source[ srcOffset + 1 ] ] << 24 ) >>> 12 )
	            //              | ( ( DECODABET[ source[ srcOffset + 2 ] ] << 24 ) >>> 18 )
	            //              | ( ( DECODABET[ source[ srcOffset + 3 ] ] << 24 ) >>> 24 );
	            int outBuff =   ( ( DECODABET[ source[ srcOffset     ] ] & 0xFF ) << 18 )
	                          | ( ( DECODABET[ source[ srcOffset + 1 ] ] & 0xFF ) << 12 )
	                          | ( ( DECODABET[ source[ srcOffset + 2 ] ] & 0xFF ) <<  6)
	                          | ( ( DECODABET[ source[ srcOffset + 3 ] ] & 0xFF )      );
	
	            
	            destination[ destOffset     ] = (byte)( outBuff >> 16 );
	            destination[ destOffset + 1 ] = (byte)( outBuff >>  8 );
	            destination[ destOffset + 2 ] = (byte)( outBuff       );
	
	            return 3;
            }catch( Exception e){
            	//#debug error
                System.out.println(""+source[srcOffset]+ ": " + ( DECODABET[ source[ srcOffset     ] ]  ) );
            	//#debug error
                System.out.println(""+source[srcOffset+1]+  ": " + ( DECODABET[ source[ srcOffset + 1 ] ]  ) );
            	//#debug error
                System.out.println(""+source[srcOffset+2]+  ": " + ( DECODABET[ source[ srcOffset + 2 ] ]  ) );
            	//#debug error
                System.out.println(""+source[srcOffset+3]+  ": " + ( DECODABET[ source[ srcOffset + 3 ] ]  ) );
                return -1;
            }   // end catch
        }
    }   // end decodeToBytes
    
    
    
    
    /**
     * Very low-level access to decoding ASCII characters in
     * the form of a byte array. Does not support automatically
     * gunzipping or any other "fancy" features.
     *
     * @param source The Base64 encoded data
     * @param off    The offset of where to begin decoding
     * @param len    The length of characters to decode
     * @param options The options for decoding data
     * @return decoded data
     */
    public static byte[] decode( byte[] source, int off, int len, int options )
    {
		byte[] DECODABET = getDecodabet( options );
	
        int    len34   = len * 3 / 4;
        byte[] outBuff = new byte[ len34 ]; // Upper limit on size of output
        int    outBuffPosn = 0;
        
        byte[] b4        = new byte[4];
        int    b4Posn    = 0;
        int    i         = 0;
        byte   sbiCrop   = 0;
        byte   sbiDecode = 0;
        for( i = off; i < off+len; i++ )
        {
            sbiCrop = (byte)(source[i] & 0x7f); // Only the low seven bits
            sbiDecode = DECODABET[ sbiCrop ];
            
            if( sbiDecode >= WHITE_SPACE_ENC ) // White space, Equals sign or better
            {
                if( sbiDecode >= EQUALS_SIGN_ENC )
                {
                    b4[ b4Posn++ ] = sbiCrop;
                    if( b4Posn > 3 )
                    {
                        outBuffPosn += decode4to3( b4, 0, outBuff, outBuffPosn, options );
                        b4Posn = 0;
                        
                        // If that was the equals sign, break out of 'for' loop
                        if( sbiCrop == EQUALS_SIGN )
                            break;
                    }   // end if: quartet built
                    
                }   // end if: equals sign or better
                
            }   // end if: white space, equals sign or better
            else
            {
            	//#debug error
                System.err.println( "Bad Base64 input character at " + i + ": " + source[i] + "(decimal)" );
                return null;
            }   // end else: 
        }   // each input character
                                   
        byte[] out = new byte[ outBuffPosn ];
        System.arraycopy( outBuff, 0, out, 0, outBuffPosn ); 
        return out;
    }   // end decode
    
    
	
	
    /**
     * Decodes data from Base64 notation, automatically
     * detecting gzip-compressed data and decompressing it.
     *
     * @param s the string to decode
     * @return the decoded data
     */
    public static byte[] decode( String s )
	{
		return decode( s, NO_OPTIONS );
	}
    
    
    /**
     * Decodes data from Base64 notation, automatically
     * detecting gzip-compressed data and decompressing it.
     *
     * @param s the string to decode
	 * @param options encode options such as URL_SAFE
     * @return the decoded data
     */
    public static byte[] decode( String s, int options )
    {   
        byte[] bytes;
        try
        {
            bytes = s.getBytes( PREFERRED_ENCODING );
        }   // end try
        catch( UnsupportedEncodingException uee )
        {
            bytes = s.getBytes();
        }   // end catch
		//</change>
        
        // Decode
        bytes = decode( bytes, 0, bytes.length, options );
        
        return bytes;
    }   // end decode


    

    
}
