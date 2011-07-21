/*
 * Created on Jul 28, 2007 at 12:40:56 PM.
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

import junit.framework.TestCase;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2007 - 2008</p>
 * <pre>
 * history
 *        Jul 28, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ImageUtilTest extends TestCase {
	
	public ImageUtilTest( String name ) {
		super( name );
	}
	
	public void xtestParticleScale() {
		// was jsut used to test the performance of different implementations, is now obsolete
		int rounds = 100; //30 * 1000;
		int width = 100;
		int height = 100;
		int startFactor = 120;
		int endFactor = 260;
		int[] source = new int[ width * height ];
		int[] target = new int[ width * height ];
		long startTime = System.currentTimeMillis();
		for (int i=0; i<rounds; i++) {
			int factor = startFactor + ((endFactor - startFactor) * i) / rounds;
			ImageUtil.particleScale( factor, width, height, source, target);
		}
		System.out.println("    optimized used time=" + getTime(System.currentTimeMillis() - startTime));
	}
	
	public void testParticleScale2() {
		// testing particle scale with different source/target arrays:
		int rounds = 100; //30 * 1000;
		int width = 100;
		int height = 100;
		int startFactor = 120;
		int endFactor = 260;
		int[] source = new int[ width * height ];
		int[] target = new int[ ((width * height) * endFactor) / 100 ];
		for (int i=0; i<rounds; i++) {
			int factor = startFactor + ((endFactor - startFactor) * i) / rounds;
			int w = (width * factor) / 100;
			int h = (height * factor) / 100;
			//ImageUtil.particleScale( factor, width, height, source, w, h, target);
			ImageUtil.particleScale( factor, w, h, source, target);
		}
	}
	
	private String getTime( long time ) {
		StringBuffer buffer = new StringBuffer();
		int seconds = (int) (time / 1000);
		buffer.append( seconds ).append("s ");
		time -= seconds * 1000;
		buffer.append( time ).append("ms ");
		return buffer.toString();
	}
	
	public void testAlphaHandling() {
		int originalOpacity = 0x6f;
		int[] data = createRgb( originalOpacity, 100 * 100 );
		int opacity = 0x8f;
		int[] scaled = new int[ data.length ];
		ImageUtil.scale(opacity, data, 60, 60, 100, 100, scaled );
		for (int i = 0; i < scaled.length; i++)
		{
			int pixelAlpha = scaled[i] >>> 24;
			assertEquals( originalOpacity, pixelAlpha );
		}
		
		opacity = 0x0f;
		scaled = new int[ data.length ];
		ImageUtil.scale(opacity, data, 60, 60, 100, 100, scaled );
		for (int i = 0; i < scaled.length; i++)
		{
			int pixelAlpha = scaled[i] >>> 24;
			assertEquals( opacity, pixelAlpha );
		}

	}
	
	private int[] createRgb( int opacity, int size ) {
		int[] data = new int[ size ];
		int alpha = opacity << 24;
		for (int i = 0; i < data.length; i++)
		{
			data[i] = i | alpha;
		}
		return data;
	}
	
	public void testRotateSimple() {
		int[] source;
		int[] target;
		int[] expected;
		
		source = new int[] { 0, 1, 2, 3,
				             4, 5, 6, 7,
				             8, 9, 10,11 };
		target = new int[ source.length ];
		
		expected = new int[] { 8, 4, 0,
				               9, 5, 1,
				               10,6, 2,
				               11,7, 3 };
		ImageUtil.rotateSimple(source, target, 4, 3, 90 );
		for (int i = 0; i < source.length; i++)
		{
			int v = source[i];
			System.out.print(v + ", ");
			if ( (i+1) % 4 == 0) {
				System.out.println();
			}
			
		}
		System.out.println("result for 90:");
		for (int i = 0; i < target.length; i++)
		{
			int v = target[i];
			System.out.print(v + ", ");
			if ( (i+1) % 3 == 0) {
				System.out.println();
			}
		}

		assertTrue( Arrays.equals( expected, target) );
		
		expected = new int[] { 11, 10, 9, 8,
	             				7, 6, 5, 4,
	             				3, 2, 1, 0 };
		ImageUtil.rotateSimple(source, target, 4, 3, 180 );
		assertTrue( Arrays.equals( expected, target) );
		
		
		expected = new int[] { 8, 4, 0,
	               9, 5, 1,
	               10,6, 2,
	               11,7, 3 };
		ImageUtil.rotateSimple(source, target, 4, 3, 270 );
		assertTrue( Arrays.equals( expected, target) );
		
	}
	
}
