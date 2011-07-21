/*
 * Created on Jul 9, 2007 at 10:23:37 PM.
 * 
 * Copyright (c) 2009 Andre Schmidt / Enough Software
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
package de.enough.polish.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import de.enough.polish.io.Externalizable;

/**
 * Reads a property list (key and element pairs) from the input stream and stores it in the internal <code>Hashtable</code>. 
 * The stream is assumed to be using the ISO 8859-1 character encoding; 
 * that is each byte is one Latin1 character.
 * 
 * <p>Copyright (c) 2009-2008 Enough Software</p>
 * <pre>
 * history
 *        09-Jul-2007 - asc creation
 * </pre>
 * 
 * @author Andre Schmidt
 * @author Robert Virkus (optimizations)
 */
public class Properties 
extends HashMap
implements Externalizable
{
	private boolean isIntegerValues;
	private char separator = '=';
	private char comment = '#';

	ArrayList orderedKeys = new ArrayList();
	
	ArrayList orderedValues = new ArrayList();
	
	/**
	 * Creates a new empty Properties set.
	 */
	public Properties() {
		super();
	}
	
	/**
	 * Creates a new Properties set and loads the specified resources.
	 * @param url the URL of the properties, e.g. "/settings.properties"
	 * @throws IOException when loading fails
	 */
	public Properties( String url) throws IOException {
		this( url, null );
	}

	/**
	 * Creates a new Properties set and loads the specified resources.
	 * @param url the URL of the properties, e.g. "/settings.properties"
	 * @param encoding the encoding of the referenced file
	 * @throws IOException when loading fails
	 */
	public Properties( String url, String encoding) throws IOException {
		load( getClass().getResourceAsStream(url), encoding, false );
	}
	
	
	/**
	 * Returns the property value corresponding to the given key 
	 * @param key the key
	 * @return the String value corresponding to the key
	 */
	public String getProperty(String key)
	{
		return (String) this.get(key);
	}
	
	/**
	 * Returns the property value corresponding to the given key,
	 * or if no value exists the default value. 
	 * @param key the key
	 * @param defaultValue the default value
	 * @return the String value corresponding to the key, or the
	 * default value
	 */
	public String getProperty(String key, String defaultValue)
	{
		String value = (String) this.get(key);
		return value != null ? value : defaultValue;
	}
	
	/**
	 * Inserts a property with the given key and the corresponding value
	 * @param key the key to be placed into this property list.
     * @param value the value corresponding to key
	 */
	public void setProperty(String key, String value)
	{
		this.put(key, value);
	}
	
	/**
	 * Reads a property list (key and element pairs) from the input stream. 
	 * The stream is assumed to be using the ISO 8859-1 character encoding; 
	 * that is each byte is one Latin1 character.
	 * @param in the input stream.
	 * @throws IOException if an error occurred when reading from the input stream.
	 */
	public void load(InputStream in) throws IOException
	{
		load( in, null, false );
	}
	/**
	 * Reads a property list (key and element pairs) from the input stream.
	 *  
	 * @param in the input stream.
	 * @param encoding the expected encoding, null for the default encoding of the system
	 * @param generateIntegerValues true when Integer values should be generated. Integers must be retrieved using get(key) instead of getProperty(key) later onwards.
	 * @throws IOException if an error occurred when reading from the input stream.
	 */
	public void load(InputStream in, String encoding, boolean generateIntegerValues ) throws IOException
	{
		this.orderedKeys.clear(); 
		this.orderedValues.clear(); 
		this.isIntegerValues = generateIntegerValues;
		int bufferLength = 2 * 1024;
		byte[] buffer = new byte[ bufferLength ];
		int read;
		int start = 0;
		int end = 0;
		boolean newLineFound;
		while ( (read = in.read(buffer, start, bufferLength - start )) != -1) {
			//add \r to last line if its missing
			if(	buffer.length != start + read && 
				buffer[read-1] != '\n' && buffer[read-1] != '\r')
			{
				buffer[read] = '\r';
				read++;
			}
			// search for next \r or \n
			String line;
			if (encoding != null) {
				line = new String( buffer, 0, read + start, encoding );
			} else {
				line = new String( buffer, 0, read + start );				
			}
			start = 0;
			newLineFound = true;
			while (newLineFound) {
				newLineFound = false;
				char c = '\n';
				for (int i = start; i < line.length(); i++) {
					c = line.charAt(i);
					if (c == '\r' || c == '\n') {
						end = i;
						newLineFound = true;
						break;
					}
				}
				if (newLineFound) {
					if (line.indexOf(this.comment, start) != start) { // this is not a comment
						int splitPos = line.indexOf(this.separator, start);
						if(splitPos == -1) {
							throw new IOException("no " + this.separator + " separator: " + line.substring( start, end ));
						}
						String key = line.substring( start, splitPos );
						String value = line.substring( splitPos + 1, end );
						this.orderedKeys.add(key);
						this.orderedValues.add(value);
						if (generateIntegerValues) {
							try {
								put( key, Integer.valueOf(value) );
							} catch(NumberFormatException ex) {
								throw new IOException( ex.toString() );
							}												
						} else {
							put( key, value );	
						}
					}
					if (c == '\r') {
						start = end + 2;
					} else {
						start = end + 1;
					}
				}
			}
			// now all key-value pairs have been read, now move any remaining data to the beginning of the buffer:
			if (start < read) {
				System.arraycopy( buffer, start, buffer, 0, read - start );
				start = read - start;
			} else {
				start = 0;
			}
		}
	}
	
	/**
	 * Returns an enumeration of all the keys in this property list 
	 * @return an enumeration of all the keys in this property list
	 */
	public Object[] propertyNames()
	{
		return this.keys();
	}
	
	/**
	 * Returns the keys in the original order
	 * @return the ordered keys
	 */
	public String[] orderedPropertyNames() 
	{
		return (String[])this.orderedKeys.toArray(new String[this.orderedKeys.size()]);
	}
	
	/**
	 * Returns the values in the original order
	 * @return the ordered values
	 */
	public String[] orderedPropertyValues() 
	{
		return (String[])this.orderedValues.toArray(new String[this.orderedValues.size()]);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#read(java.io.DataInputStream)
	 */
	public void read(DataInputStream in) throws IOException {
		int size = in.readInt();
		this.isIntegerValues = in.readBoolean();
		for (int i=0; i < size; i++) {
			String key = in.readUTF();
			Object value;
			if (this.isIntegerValues) {
				value = new Integer( in.readInt() );
			} else {
				value = in.readUTF();
			}
			put( key, value );
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#write(java.io.DataOutputStream)
	 */
	public void write(DataOutputStream out) throws IOException {
		int size = size();
		out.writeInt( size );
		out.writeBoolean( this.isIntegerValues );
		Object[] keys = keys();
		for(int i=0; i<keys.length; i++)
		{
			String key = (String)keys[i];
			out.writeUTF( key );
			if (this.isIntegerValues) {
				Integer value = (Integer) get( key );
				out.writeInt( value.intValue() );
			} else {
				out.writeUTF( (String) get(key ) );
			}
		}
	}
	
	/**
	 * Returns the separator used to split keys and values in a properties file
	 * @return the separator, '=' by default
	 */

	public char getSeparator() {
		return this.separator;
	}

	/**
	 * Sets the separator used to split keys and values in a properties file, defaults to '='
	 * @param separator the separator
	 */
	public void setSeparator(char separator) {
		this.separator = separator;
	}
	
	/**
	 * Returns the comment char that marks lines that should be skipped.
	 * 
	 * @return the separator
	 */

	public char getComment() {
		return this.comment;
	}

	/**
	 * Sets the comment used to skip a line, defaults to '#'
	 * @param comment the comment char, '#' by default
	 */
	public void setComment(char comment) {
		this.comment = comment;
	}
	
	
	
	
}
