/*
 * Created on 02-May-2006 at 17:34:35.
 * 
 * Copyright (c) 2006 Robert Virkus / Enough Software
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

public class MathUtilTest extends TestCase {

	public MathUtilTest(String name) {
		super(name);
	}
	
//	public void testDoubleRound() {
//		for (int i = 0; i < 360; i++) {
//			double d = java.lang.Math.sin(i);
//			assertEquals( java.lang.Math.round(d), MathUtil.round(d) );
//		}		
//	}
//
//	public void testFloatRound() {
//		for (int i = 0; i < 360; i++) {
//			float f = (float) java.lang.Math.sin(i);
//			assertEquals( java.lang.Math.round(f), MathUtil.round(f) );
//		}		
//	}
	
	public void testApxSin() {
		for (int i = -2000; i < 1400; i++) {
			//System.out.println( i + "=" + MathUtil.apxSin(i));
			assertTrue( isSimularSin( i ) );
		}
	}
	
	private boolean isSimularSin(int x) {
		int apx = MathUtil.apxSin(x);
		double radians = Math.toRadians( (x * 360D) / 1000D ); 
		int correct = (int) (Math.sin( radians ) * 1000);
		if ( apx > correct + 5 || apx < correct - 5  ) {
			fail( "not simular enough: x=" + x + ", MatUtil.apxSin()=" + apx + ", correct=" + correct );
		}
		return true;
	}

	public void testModulo() {
		int x;
		x = -2000;
		assertEquals( fastModulo( x, 1000), modulo( x, 1000 ) );
		x = -1999;
		assertEquals( fastModulo( x, 1000), modulo( x, 1000 ) );
		x = -1;
		assertEquals( fastModulo( x, 1000), modulo( x, 1000 ) );
		x = 0;
		assertEquals( fastModulo( x, 1000), modulo( x, 1000 ) );
		x = 1;
		
		long time;
		
		time = System.currentTimeMillis();
		for (int i = -2000; i < 1400; i++) {
			for (int j = 0; j < 1000; j++) {
				modulo( x, 1000);
			}
		}
		time = System.currentTimeMillis() - time;
		System.out.println("slowModulo=" + time + "ms" );

		time = System.currentTimeMillis();
		for (int i = -2000; i < 1400; i++) {
			for (int j = 0; j < 1000; j++) {
				fastModulo( x, 1000);
			}
		}
		time = System.currentTimeMillis() - time;
		System.out.println("fastModulo=" + time + "ms" );
		
		time = System.currentTimeMillis();
		for (int i = -2000; i < 1400; i++) {
			for (int j = 0; j < 1000; j++) {
				modulo( x, 1000);
			}
		}
		time = System.currentTimeMillis() - time;
		System.out.println("slowModulo=" + time + "ms" );

		time = System.currentTimeMillis();
		for (int i = -2000; i < 1400; i++) {
			for (int j = 0; j < 1000; j++) {
				fastModulo( x, 1000);
			}
		}
		time = System.currentTimeMillis() - time;
		System.out.println("fastModulo=" + time + "ms" );

	}

	/**
	 * @param x
	 * @param i
	 * @return
	 */
	private int fastModulo(int x, int i) {
		x %= i;
		if (x < 0) {
			x += i;
		}
		return x;
	}

	/**
	 * @param x
	 * @param i
	 * @return
	 */
	private int modulo(int x, int i) {
		while(x>i){x-=i;}
		while(x<0){x+=i;}
		return x;
	}

//	public void testApxSinRange() {
//		for (int i=0; i < 1000; i++) {
//			System.out.println(i + "=" + MathUtil.apxSin(i));
//		}
//	}
}
