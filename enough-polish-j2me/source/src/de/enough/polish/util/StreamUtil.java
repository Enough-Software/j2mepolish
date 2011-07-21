package de.enough.polish.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>Provides some useful Stream methods.</p>
 *
 * <p>Copyright Enough Software 2008</p>

 * <pre>
 * history
 *        10-Jul-2008 - asc creation
 * </pre>
 * @author Andre Schmidt, andre@enough.de
 */

public class StreamUtil {
	/**
	 * the default length for the temporary buffer is 8 kb
	 */
	public static final int DEFAULT_BUFFER = 8 * 1024;
	
	/**
	 * Returns the lines of a InputStream as an ArrayList of Strings
	 * @param in the stream to read
	 * @param encoding the encoding to use
	 * @param bufferLength the length of the used temporary buffer
	 * @return the lines as an ArrayList
	 * @throws IOException when reading fails
	 * @throws NullPointerException when the given input stream is null
	 */
	public static String[] getLines(InputStream in, String encoding, int bufferLength) 
	throws IOException
	{
		String allLines = getString(in, encoding, bufferLength);
		String[] lines = TextUtil.split(allLines, '\n');
		return lines;
	}
	
	/**
	 * Returns the content of a stream as a String 
	 * @param in the stream to read with the text in the default encoding
	 * @return the resulting String
	 * @throws IOException when reading fails
	 */	
	public static String getString(InputStream in) 
	throws IOException
	{
		return getString( in, null, DEFAULT_BUFFER );
	}
	
	/**
	 * Returns the content of a stream as a String
	 * @param in the stream to read
	 * @param encoding the encoding to use
	 * @param bufferLength the length of the used temporary buffer
	 * @return the resulting String
	 * @throws IOException when reading fails
	 * @throws NullPointerException when the given input stream is null
	 */	
	public static String getString(InputStream in, String encoding, int bufferLength) 
	throws IOException
	{
		
		byte[] buffer = readFully( in, bufferLength );
		if (encoding != null) {
			return new String( buffer, 0, buffer.length, encoding );
		} else {
			return new String( buffer, 0, buffer.length );				
		}
	}
	
	/**
	 * Returns the content of a stream as a String 
	 * @param in the stream to read with the text in the default encoding
	 * @return the resulting String
	 */	
	public static String getStringSafe(InputStream in) 
	{
		return getStringSafe( in, null, DEFAULT_BUFFER );
	}
	
	/**
	 * Returns the content of a stream as a String
	 * @param in the stream to read
	 * @param encoding the encoding to use
	 * @param bufferLength the length of the used temporary buffer
	 * @return the resulting String
	 */	
	public static String getStringSafe(InputStream in, String encoding, int bufferLength) 
	{
		byte[] buffer = readFullySafe( in, bufferLength );
		if (encoding != null) {
			try {
				return new String( buffer, 0, buffer.length, encoding );
			} catch (Exception e) {
				//#debug error
				System.out.println("Unable to encode string with " + encoding + e);
				return new String( buffer, 0, buffer.length );
			}
		} else {
			return new String( buffer, 0, buffer.length );				
		}
	}

	/**
	 * Reads the complete input stream into a byte array using a 8kb temporary buffer
	 * @param in the input stream
	 * @return the read data
	 * @throws IOException when reading fails
	 * @throws NullPointerException when the given input stream is null
	 */
	public static byte[] readFully(InputStream in)
	throws IOException 
	{
		return readFully( in, DEFAULT_BUFFER );
	}

	/**
	 * Reads the complete input stream into a byte array using the specified buffer size
	 * @param in the input stream
	 * @param bufferLength the length of the used temporary buffer
	 * @return the read data
	 * @throws IOException when reading fails
	 * @throws NullPointerException when the given input stream is null
	 */
	public static byte[] readFully(InputStream in, int bufferLength)
	throws IOException 
	{
		return readFully( in, new byte[bufferLength] );
	}
	/**
	 * Reads the complete input stream into a byte array using the specified buffer 
	 * @param in the input stream
	 * @param buffer the used temporary buffer
	 * @return the read data
	 * @throws IOException when reading fails
	 * @throws NullPointerException when the given input stream is null
	 */
	public static byte[] readFully(InputStream in, byte[] buffer)
	throws IOException 
	{
		int bufferLength = buffer.length;
		int read;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		while((read = in.read(buffer, 0, bufferLength))  != -1)
		{
			out.write( buffer, 0, read );
		}
		return out.toByteArray();
	}
	
	/**
	 * Reads the complete input stream into a byte array using a 8kb temporary buffer
	 * The input stream is read until it is empty or until an error occurred.
	 * 
	 * @param in the input stream
	 * @return the read data or null when the input stream is null
	 */
	public static byte[] readFullySafe(InputStream in)
	{
		return readFullySafe( in, DEFAULT_BUFFER );
	}

	
	/**
	 * Reads the complete input stream into a byte array using a the specified buffer size.
	 * The input stream is read until it is empty or until an error occurred.
	 * 
	 * @param in the input stream
	 * @param bufferLength the length of the used temporary buffer
	 * @return the read data or null when the input stream is null
	 */
	public static byte[] readFullySafe(InputStream in, int bufferLength)
	{
		byte[] buffer = new byte[ bufferLength ];
		return readFullySafe( in, buffer );
	}
	
	/**
	 * Reads the complete input stream into a byte array using a the specified buffer.
	 * The input stream is read until it is empty or until an error occurred.
	 * 
	 * @param in the input stream
	 * @param buffer the used temporary buffer
	 * @return the read data or null when the input stream is null
	 */
	public static byte[] readFullySafe(InputStream in, byte[] buffer)
	{
		if (in == null) {
			return null;
		}
		
		int bufferLength = buffer.length;
		int read;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			while((read = in.read(buffer, 0, bufferLength))  != -1)
			{
				out.write( buffer, 0, read );
			}
		} catch (IOException e) {
			//#debug error
			System.out.println("Unable to read stream fully " + e);
		}
		return out.toByteArray();
	}
}
