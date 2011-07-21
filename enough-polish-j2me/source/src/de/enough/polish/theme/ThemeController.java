//#condition polish.usePolishGui && polish.useThemes
package de.enough.polish.theme;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import javax.microedition.lcdui.Image;

import de.enough.polish.io.Serializer;
import de.enough.polish.ui.Style;

/**
 * <p>
 * Provides static methods to retrieve <code>ThemeContainer</code> objects from
 * a theme file.
 * </p>
 * 
 * <p>
 * Copyright (c) 2009-2008 Enough Software
 * </p>
 * 
 * <pre>
 * history
 *        10-Dec-2007 - asc creation
 *        11-Dec-2007 - asc added index
 * </pre>
 * 
 * @author Andre Schmidt, andre@enough.de
 */
public class ThemeController {
	/**
	 * Returns a container header with the id <code>container</code> from the given stream
	 * @param stream the stream
	 * @param id the id of the container
	 * @return the container
	 */
	public static ThemeContainer getContainerHeader(ThemeStream stream,String id)
	{
		return (ThemeContainer)stream.getIndex().get(id);
	}
	
	/**
	 * Returns a byte array from the stream using the offset and size in <code>container</code>
	 * @param stream the stream to read the array
	 * @param container the container containing the offset and size
	 * @return the byte array
	 * @throws IOException
	 */
	public static byte[] getContainerData(ThemeStream stream, ThemeContainer container) throws IOException
	{
		byte[] data = null;
		
		try
		{
			data = stream.getBytes(container.getOffset(), container.getSize());
		}
		catch(Exception e)
		{
			//#debug error
			System.out.println("Unable to load " + container.getName() + e );
		}
		
		return data;
	}
	
	/**
	 * Returns an byte array from a ThemeStream
	 * @param stream the stream to read
	 * @param id the id of the data
	 * @return the byte array
	 * @throws IOException
	 */
	public static byte[] getData(ThemeStream stream, String id) throws IOException
	{
		ThemeContainer header = getContainerHeader(stream, id);
		return getContainerData(stream, header);
	}
	
	/**
	 * Returns an object from a ThemeStream
	 * @param stream the stream to read
	 * @param id the id of the object
	 * @return the object
	 * @throws IOException
	 */
	public static Object getObject(ThemeStream stream, String id) throws IOException
	{

		ThemeContainer header = getContainerHeader(stream, id);
		byte[] data = getContainerData(stream, header);
		
		return Serializer.deserialize(new DataInputStream(new ByteArrayInputStream(data)));
	}
	
	/**
	 * Returns a themed image (png) as an Image object  
	 * @param stream the stream to read
	 * @param id the id of the file
	 * @return the image
	 * @throws IOException
	 */
	public static Style getStyle(ThemeStream stream, String id) throws IOException
	{
		id = "de.enough.polish.ui.stylesheet." + id.toLowerCase() + "style";
		return (Style) getObject(stream, id);
	}
	
	/**
	 * Returns a themed image (png) as an Image object  
	 * @param stream the stream to read
	 * @param id the id of the file
	 * @return the image
	 * @throws IOException
	 */
	public static Image getImage(ThemeStream stream, String id) throws IOException
	{
		id = id + ".png";
		byte[] data = getData(stream, id);
		return Image.createImage(data, 0, data.length);
	}
	
	/**
	 * Returns the contents of a themed file as a String
	 * @param stream the stream to read
	 * @param id the id of the file
	 * @return the string
	 * @throws IOException
	 */
	public static String getContentString(ThemeStream stream, String id) throws IOException
	{
		byte[] data = getData(stream,id);
		return new String(data);
	}
}
