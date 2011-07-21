/*
 * Created on Jul 23, 2007 at 10:53:12 AM.
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
package de.enough.polish.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.enough.polish.util.zip.GZipInputStream;
import de.enough.polish.util.zip.GZipOutputStream;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Jul 23, 2007 - Simon creation
 * </pre>
 * @author Simon Schmitt, simon.schmitt@enough.de
 */
public final class ZipUtil {
	/**
	 *  This function decompresses a given byte array using the DEFLATE algorithm.
	 *  
	 * @param data		the input data to be decompressed
	 * @return			the resulting uncompressed byte array 
	 * @throws IOException
	 */
	public static byte[] decompress( byte[] data ) throws IOException{
		//#debug
		System.out.println("simple decompress");
		return decompress(data,GZipInputStream.TYPE_DEFLATE);
	}
	/**
	 * This function decompresses a given byte array using the DEFLATE or the GZIP algorithm.
	 * 
	 * @param data		the input data to be decompressed
	 * @param compressionType TYPE_GZIP or TYPE_DEFLATE
	 * @return			the resulting uncompressed byte array 
	 * @throws IOException
	 */
	public static byte[] decompress( byte[] data , int compressionType) throws IOException{
		//#debug
		System.out.println("simple decompress, creating new GZipInputStream");
		byte[] tmp=new byte[1024];
		int read;
		
		GZipInputStream zipInputStream = new GZipInputStream(new ByteArrayInputStream( data ) ,1024 ,compressionType,true);
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream(1024);
		//#debug
		System.out.println("now reading from GZipInputStream and writing to ByteArrayOutputStream");
		// take from ZipInputStream and fill into ByteArrayOutputStream
		while ( (read=zipInputStream.read(tmp, 0, 1024))>0 ){
			//#debug
			System.out.println("read=" + read + ", size=" + bout.size());
			bout.write(tmp,0,read);
		}
		
		return bout.toByteArray();
	}
	/**
	 * This function compresses a given byte array using the DEFLATE algorithm.
	 * 
	 * @param data		the input data to be compressed
	 * @return			the resulting compressed byte array 
	 * @throws IOException
	 */
	public static byte[] compress( byte[] data ) throws IOException{
		return compress( data, GZipOutputStream.TYPE_DEFLATE );
	}
	/**
	 * This function compresses a given byte array using the DEFLATE algorithm.
	 * 
	 * @param data		the input data to be compressed
	 * @param compressionType	TYPE_GZIP or TYPE_DEFLATE
	 * @return			the resulting compressed byte array 
	 * @throws IOException
	 */
	public static byte[] compress( byte[] data, int compressionType ) throws IOException{
		if (data.length > 1<<15){ 
			return compress(data,compressionType, 1<<15, 1<<15);
			
		} else{
			return compress(data,compressionType, data.length, data.length);
			
		}
	}
	/**
	 * This function compresses a given byte array using the DEFLATE algorithm.
	 * 
	 * @param data		the input data to be compressed
	 * @param compressionType	TYPE_GZIP or TYPE_DEFLATE
	 * @param plainWindowSize	this size is important for the lz77 search. Larger values
	 * 							will result in better compression. Maximum is 32768.
	 * @param huffmanWindowSize	this size is important for the huffmanencoding. A large
	 * 							value will result to a better frequency statistic and therefore to a better compression.
	 * @return					the resulting compressed byte array 
	 * @throws IOException
	 */
	public static byte[] compress( byte[] data , int compressionType,int plainWindowSize, int huffmanWindowSize) throws IOException{
		ByteArrayOutputStream bout = new ByteArrayOutputStream(1024);
		GZipOutputStream zipOutputStream = new GZipOutputStream(bout, 1024, compressionType, plainWindowSize, huffmanWindowSize);
		
		zipOutputStream.write(data);
		zipOutputStream.close();
		
		return bout.toByteArray();
	}
	
	/**
	 * Compresses an integer array, e.g. RGB data.
	 * @param rgb the int array
	 * @return the compressed byte data
	 * @throws IOException when compression fails 
	 */
	public static byte[] compressIntArray(int[] rgb) throws IOException
	{
		return compress( convertIntToByteArray(rgb) );
	}
	
	/**
	 * Converts the given int[] array into a byte[] array
	 * @param rgb the original integer array
	 * @return the corresponding byte array in which integers are in the same way encoded as in DataOutputStream.writeInt()
	 */
	public static byte[] convertIntToByteArray( int[] rgb ) {
		byte[] data = new byte[ rgb.length * 4 ];
		int j = 0;
		for (int i = 0; i < rgb.length; i++)
		{
			int v = rgb[i];
			data[j+0] = (byte)((v >>> 24) & 0xFF);
			data[j+1] = (byte)((v >>> 16) & 0xFF);
			data[j+2] = (byte)((v >>>  8) & 0xFF);
			data[j+3] = (byte)((v >>>  0) & 0xFF);
			j += 4;
		}
		return data;
	}
	
	
	/**
	 * Decompresses a byte array into an integer array.
	 * @param data the byte array
	 * @return the decompressed integer array
	 * @throws IOException when decompression fails 
	 */
	public static int[] decompressIntArray(byte[] data) throws IOException
	{
		return convertByteToIntArray( decompress( data, GZipOutputStream.TYPE_DEFLATE ) );
	}
	
	/**
	 * Converts the given int[] array into a byte[] array
	 * @param data the original byte array
	 * @return the corresponding int array
	 */
	public static int[] convertByteToIntArray( byte[] data ) {
		int[] rgb = new int[ data.length / 4 ];
		int j = 0;
		for (int i = 0; i < rgb.length; i++)
		{
			rgb[i] = 
				((data[j+0] & 0xff ) << 24) 
			| 	((data[j+1] & 0xff ) << 16)
			|	((data[j+2] & 0xff ) <<  8)
			|	((data[j+3] & 0xff ) <<  0);
			j += 4;
		}
		return rgb;
	}
	
	/**
	 * Compresses an integer RGB array without translucent parts, each RGB pixel is in the format 0xAARRGGBB.
	 * @param rgb the int array
	 * @return the compressed byte data
	 * @throws IOException when compression fails 
	 */
	public static byte[] compressRgbArray(int[] rgb) throws IOException
	{
		return compress( convertRgbToByteArray(rgb, 0, rgb.length) );
	}
	
	/**
	 * Compresses an integer RGB array without translucent parts, each RGB pixel is in the format 0xAARRGGBB.
	 * @param rgb the int array
	 * @param offset the start index of the first pixel
	 * @param len the number of pixels
	 * @return the compressed byte data
	 * @throws IOException when compression fails 
	 */
	public static byte[] compressRgbArray(int[] rgb, int offset, int len) throws IOException
	{
		return compress( convertRgbToByteArray(rgb, offset, len) );
	}
	
	/**
	 * Converts the given int[] RGB array into a byte[] array without preserving the alpa channel (each RGB pixel is in the format 0xAARRGGBB).
	 * @param rgb the original integer array
	 * @return the corresponding byte array with a length of rgb.length * 3.
	 */
	public static byte[] convertRgbToByteArray( int[] rgb ) {
		return convertRgbToByteArray(rgb, 0, rgb.length);
	}
	
	/**
	 * Converts the given int[] RGB array into a byte[] array without preserving the alpa channel (each RGB pixel is in the format 0xAARRGGBB).
	 * @param rgb the original integer array
	 * @param offset the start index of the first pixel
	 * @param len the number of pixels
	 * @return the corresponding byte array with a length of rgb.length * 3.
	 */
	public static byte[] convertRgbToByteArray( int[] rgb, int offset, int len ) {
		byte[] data = new byte[ len * 3 ];
		int j = 0;
		for (int i = offset; i < offset + len; i++)
		{
			int v = rgb[i];
			data[j+0] = (byte)((v >>> 16) & 0xFF);
			data[j+1] = (byte)((v >>>  8) & 0xFF);
			//data[j+2] = (byte)((v >>>  0) & 0xFF);
			data[j+2] = (byte)( v         & 0xFF);
			j += 3;
		}
		return data;
	}
	
	
	
	/**
	 * Decompresses a byte array into an RGB array.
	 * @param data the byte array with bytes in RRGGBB order (no alpha channel).
	 * @return the decompressed integer RGB array
	 * @throws IOException when decompression fails 
	 */
	public static int[] decompressRgbArray(byte[] data) throws IOException
	{
		return convertByteToRgbArray( decompress( data, GZipOutputStream.TYPE_DEFLATE ) );
	}
	
	/**
	 * Converts the given int[] RGB array into a byte[] array
	 * @param data the original byte array with bytes in RRGGBB order (no alpha channel).
	 * @return the corresponding int RGB array
	 */
	public static int[] convertByteToRgbArray( byte[] data ) {
		int[] rgb = new int[ data.length / 3 ];
		int j = 0;
		for (int i = 0; i < rgb.length; i++)
		{
			rgb[i] = 
			 	((data[j+0] & 0xff ) << 16)
			|	((data[j+1] & 0xff ) <<  8)
			//|	((data[j+2] & 0xff ) <<  0);
			|	( data[j+2] & 0xff );
			j += 3;
		}
		return rgb;
	}
}
