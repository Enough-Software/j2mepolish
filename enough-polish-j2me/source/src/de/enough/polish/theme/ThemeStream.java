//#condition polish.usePolishGui && polish.useThemes
package de.enough.polish.theme;

import java.io.DataInputStream;
import java.io.IOException;

import de.enough.polish.util.HashMap;

/**
 * <p>
 * Provides a generic way to access a DataInputStream for RAG.
 * Introduced for Samsung devices which don't support mark() and reset()
 * in a DataInputStream
 * </p>
 * 
 * <p>
 * Copyright (c) 2009 Enough Software
 * </p>
 * 
 * <pre>
 * history
 *        23-Jan-2008 - asc creation
 * </pre>
 * 
 * @author Andre Schmidt, andre@enough.de
 */
public class ThemeStream {
	Object midlet;
	String file;
	
	HashMap index = null;
		
	/**
	 * Constructor for ThemeStream
	 * @param midlet the midlet object (can as well be any object, used for getClass().getResourceAsStream())
	 * @param file the full path of the file to stream
	 */
	public ThemeStream(Object midlet, String file)
	{
		this.midlet = midlet;
		this.file = file;
		this.index = readIndex();
	}
	
	/**
	 * Returns the index of the stream
	 * @return the index
	 */
	protected HashMap getIndex()
	{
		return this.index;
	}
	
	/**
	 * Reads the index from the stream
	 * @return a hashmap representing the index
	 */
	private HashMap readIndex()
	{
		DataInputStream stream = new DataInputStream(this.midlet.getClass().getResourceAsStream(this.file));
		
		HashMap result = new HashMap();
		try
		{
			int indexLength = stream.readInt();
			
			for (int i = 0; i < indexLength; i++) {
				ThemeContainer container = new ThemeContainer();
				
				container.setName(stream.readUTF());
				container.setOffset(stream.readInt());
				container.setSize(stream.readInt());
				container.setType(stream.readByte());
				
				result.put(container.getName(),container);
//				System.out.println("ThemeStream.readIndex() read container: " + container.getName() + " size: " + container.getSize() + " type: " + container.getType());
			}
			
			stream.close();
		}
		catch(IOException e)
		{
			//#debug
			System.out.println("unable to read index from stream " + e);
		}
		
		return result;
	}
		
	/**
	 * Returns an array of bytes from the stream
	 * @param offset the offset to read the array
	 * @param length the length of the array
	 * @return the data
	 */
	protected byte[] getBytes(int offset, int length)
	{
		DataInputStream stream = new DataInputStream(this.midlet.getClass().getResourceAsStream(this.file));
		byte[] result = new byte[length];
		
		try
		{
			stream.skip(offset);
			stream.readFully(result, 0, length);
			stream.close();
		}
		catch(IOException e)
		{
			//#debug
			System.out.println("unable to read bytes " + e);
		}
		
		return result;
	}
}
