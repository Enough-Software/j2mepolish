/*
 * Created on Nov 1, 2008 at 10:46:28 AM.
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

import java.io.IOException;
import java.util.Random;

import junit.framework.TestCase;

/**
 * <p>tests the zip utility</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ZipUtilTest extends TestCase
{
	
	public void testRgbToByteConversion() {
		Random random = new Random( System.currentTimeMillis() );
		int[] rgb = new int[ 8 * 1024 + random.nextInt( 8 * 1024 )];
		for (int i = 0; i < rgb.length; i++)
		{
			rgb[i] = (random.nextInt() & 0x00ffffff);
		}
		
		byte[] data = ZipUtil.convertRgbToByteArray(rgb);
		assertEquals( rgb.length * 3, data.length );
		
		int[] rgb2 = ZipUtil.convertByteToRgbArray(data);
		assertEquals( rgb.length, rgb2.length );
		for (int i = 0; i < rgb2.length; i++)
		{
			assertEquals( rgb[i], rgb2[i] );
		}
	}
	
	public void testRgbCompression() throws IOException {
		Random random = new Random( System.currentTimeMillis() );
		int[] rgb = new int[ (8 * 1024 + random.nextInt( 8 * 1024 ) % 3) * 3];
		for (int i = 0; i < rgb.length; i += 3)
		{
			int v = (random.nextInt() & 0x00ffffff);
			rgb[i+0] = v;
			rgb[i+1] = v;
			rgb[i+2] = v;
		}
		
		byte[] data = ZipUtil.compressRgbArray(rgb);
		int[] rgb2 = ZipUtil.decompressRgbArray(data);
		assertEquals( rgb.length, rgb2.length );
		for (int i = 0; i < rgb2.length; i++)
		{
			assertEquals( rgb[i], rgb2[i] );
		}

		System.out.println("compressed rgb[] data from " + (rgb.length * 3) + " to " + data.length );
	}

	
	public void testIntToByteConversion() {
		Random random = new Random( System.currentTimeMillis() );
		int[] rgb = new int[ 8 * 1024 + random.nextInt( 8 * 1024 )];
		for (int i = 0; i < rgb.length; i++)
		{
			rgb[i] = random.nextInt();
		}
		
		byte[] data = ZipUtil.convertIntToByteArray(rgb);
		assertEquals( rgb.length * 4, data.length );
		
		int[] rgb2 = ZipUtil.convertByteToIntArray(data);
		assertEquals( rgb.length, rgb2.length );
		for (int i = 0; i < rgb2.length; i++)
		{
			assertEquals( rgb[i], rgb2[i] );
		}
	}
	
	public void testIntCompression() throws IOException {
		Random random = new Random( System.currentTimeMillis() );
		int[] rgb = new int[ (8 * 1024 + random.nextInt( 8 * 1024 ) % 3) * 3];
		for (int i = 0; i < rgb.length; i += 3)
		{
			int v = random.nextInt();
			rgb[i+0] = v;
			rgb[i+1] = v;
			rgb[i+2] = v;
		}
		
		byte[] data = ZipUtil.compressIntArray(rgb);
		int[] rgb2 = ZipUtil.decompressIntArray(data);
		assertEquals( rgb.length, rgb2.length );
		for (int i = 0; i < rgb2.length; i++)
		{
			assertEquals( rgb[i], rgb2[i] );
		}

		System.out.println("compressed int[] data from " + (rgb.length * 4) + " to " + data.length );
	}
	
}
