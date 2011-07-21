/*
 * Created on 30-Jan-2006 at 20:37:57.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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

public class ArraysTest extends TestCase {

	public ArraysTest(String name) {
		super(name);
	}
	



	public void testShellSort() {
		System.out.println("testing shell sort");
		String[] j2meObjects = new String[]{ "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim" };
		String[] j2seObjects = new String[]{ "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim" };
		Arrays.shellSort( j2meObjects );
		java.util.Arrays.sort( j2seObjects );
		assertEquals( j2meObjects.length, j2seObjects.length );
		for (int i = 0; i < j2meObjects.length; i++) {
			String j2me = j2meObjects[i];
			String j2se = j2seObjects[i];
			assertEquals( j2se, j2me );
			//System.out.println( j2me );
		}

	
		j2meObjects = new String[]{ "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim", "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim", "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim" };
		j2seObjects = new String[]{ "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim", "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim", "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim" };
		Arrays.shellSort( j2meObjects );
		java.util.Arrays.sort( j2seObjects );
		assertEquals( j2meObjects.length, j2seObjects.length );
		for (int i = 0; i < j2meObjects.length; i++) {
			String j2me = j2meObjects[i];
			String j2se = j2seObjects[i];
			assertEquals( j2se, j2me );
			//System.out.println( j2me );
		}
	}

	
	public void testSort() {

		System.out.println("testing sort");
		String[] j2meObjects = new String[]{ "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim", "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim", "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim","z" ,"x"};
		String[] j2seObjects = new String[]{ "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim", "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim", "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim","z" ,"x"};
		System.out.print("length:"+j2meObjects.length+"\n");
		Arrays.sort( j2meObjects);
		System.out.print("length:"+j2meObjects.length+"\n");
		java.util.Arrays.sort( j2seObjects );
		assertEquals( j2meObjects.length, j2seObjects.length );
		for (int i = 0; i < j2meObjects.length; i++) {
			String j2me = j2meObjects[i];
			String j2se = j2seObjects[i];
			assertEquals( j2se, j2me );
			System.out.println(j2me );
		}

		System.out.println( 	"\nFIRST SORT OK; NEXT:2");
		j2meObjects = new String[]{ "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim", "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim", "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim" };
		j2seObjects = new String[]{ "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim", "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim", "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim" };
		Arrays.sort( j2meObjects );
		java.util.Arrays.sort( j2seObjects );
		assertEquals( j2meObjects.length, j2seObjects.length );
		for (int i = 0; i < j2meObjects.length; i++) {
			String j2me = j2meObjects[i];
			String j2se = j2seObjects[i];
			assertEquals( j2se, j2me );
			System.out.println( j2me );
		}

	}
	
	public void testQuickSort() {
		System.out.println("int array");
		int[] intMEArray = new int[]{55551,54562,5645,64658,5645640,54547,546546,7897897,5546,45454,21312305,5645,87985,545642,45645,545,456,5646,31,64658,64521,1515,489,4562,47895,4567,1890,540,4505,47860,4546,7890,87,780,48540,45640,789,5864,454,4564,5,88,454545,454,45456,4546,45646,78971,64658,123,456,1654,8452,4,7484541,87465454,54545,4545,4,45,5,1,1,2,56,2,72006,544,66,44,34,333,7,5,5,8,40,5,88,7,1,1,1,1,1,1,1,1,1,1,2,2,2,44,44,55,66,77,88,99,10,1,5,5,8,40,5,88,7435,5,5435,4358,435,26,7,843,9,40,34510,9,64658,40,10,11,56433,45644,46555,55,67,4543,14,23455,35,35,45,14355,13451,84358,103450,3454,34543545,3459};
		int[] intSEArray = new int[]{55551,54562,5645,64658,5645640,54547,546546,7897897,5546,45454,21312305,5645,87985,545642,45645,545,456,5646,31,64658,64521,1515,489,4562,47895,4567,1890,540,4505,47860,4546,7890,87,780,48540,45640,789,5864,454,4564,5,88,454545,454,45456,4546,45646,78971,64658,123,456,1654,8452,4,7484541,87465454,54545,4545,4,45,5,1,1,2,56,2,72006,544,66,44,34,333,7,5,5,8,40,5,88,7,1,1,1,1,1,1,1,1,1,1,2,2,2,44,44,55,66,77,88,99,10,1,5,5,8,40,5,88,7435,5,5435,4358,435,26,7,843,9,40,34510,9,64658,40,10,11,56433,45644,46555,55,67,4543,14,23455,35,35,45,14355,13451,84358,103450,3454,34543545,3459};
		System.out.println("intA:"+intMEArray.length);
		java.util.Arrays.sort( intSEArray );
		Arrays.iQuick(intMEArray,intMEArray.length);
		for (int i = 0; i < intMEArray.length; i++) {
			System.out.println(intMEArray[i]+":"+intSEArray[i]);	
			if(intMEArray[i] != intSEArray[i]){
				System.out.println("fail");
				break;
			}
		}
		System.out.println("testing quicksort");
		String[] j2meObjects = new String[]{ "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "ddd", "ddd", "baerta", "emil", "sarlfred", "gerhard", "ricky","fofofo","zu","auf"};
		String[] j2seObjects = new String[]{ "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "ddd", "ddd", "baerta", "emil", "sarlfred", "gerhard", "ricky","fofofo","zu","auf"};
		System.out.print("length:"+j2meObjects.length+"\n");
		Arrays.quicksort( j2meObjects);
		System.out.print("length:"+j2meObjects.length+"\n");
		java.util.Arrays.sort( j2seObjects );
		assertEquals( j2meObjects.length, j2seObjects.length );
		for (int i = 0; i < j2meObjects.length; i++) {
			String j2me = j2meObjects[i];
			String j2se = j2seObjects[i];
			System.out.print("'"+j2me+"'"+",");
			assertEquals( j2se, j2me );
//			System.out.println(j2me );
		}

		System.out.println( 	"\nFIRST QUICK SORT OK; NEXT:2");
		j2meObjects = new String[]{ "aaa","aad","abc","acd","alfred","berta","auf","baerta","ddd","ddd","ddd","emil","emil","fofofo","gerhard","gerhard","ricky","ricky","sarlfred","zu","auf","bae","bae","aud","auf","fofofo","kkkl","tim","polish","aad","mut","lala"};
		j2seObjects = new String[]{ "aaa","aad","abc","acd","alfred","berta","auf","baerta","ddd","ddd","ddd","emil","emil","fofofo","gerhard","gerhard","ricky","ricky","sarlfred","zu","auf","bae","bae","aud","auf","fofofo","kkkl","tim","polish","aad","mut","lala"};
		System.out.print("length:"+j2meObjects.length+"\n");
		Arrays.quicksort( j2meObjects );
		java.util.Arrays.sort( j2seObjects );
		assertEquals( j2meObjects.length, j2seObjects.length );
		for (int i = 0; i < j2meObjects.length; i++) {
			String j2me = j2meObjects[i];
			String j2se = j2seObjects[i];
			System.out.println( j2me +":::"+j2se);
			assertEquals( j2se, j2me );
		}
	}


	public void testArraycopy() {
		int[] input = new int[]{ 0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16 };
		int[] output = new int[ input.length ];
		Arrays.arraycopy(input, 0, output, 0, input.length );
		assertTrue( Arrays.equals(input, output) );
		
		// test array copy on itself (1):
		Arrays.arraycopy(input, 0, input, 9, input.length - 9 );
		for (int i=9; i<input.length - 9; i++) {
			assertEquals( i, input[i] );
		}
		
		// test array copy on itself (2):
		input = new int[]{ 0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16 };
		Arrays.arraycopy(input, 0, input, 2, 5 );
		for (int i=2; i<2+5; i++) {
			System.out.println("i=" + i + ", input[i]=" + input[i]);
			assertEquals( i-2, input[i] );
		}
		
		// test array copy on itself (3):
		input = new int[]{ 0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16 };
		Arrays.arraycopy(input, 3, input, 2, 5 );
		for (int i=2; i<2+5; i++) {
			System.out.println("i=" + i + ", input[i]=" + input[i]);
			assertEquals( i+1, input[i] );
		}

		// test array copy on itself (4):
		System.out.println(">>>4");
		input = new int[]{ 0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16 };
		Arrays.arraycopy(input, 1, input, 2, 5 );
		for (int i=2; i<2+5; i++) {
			System.out.println("i=" + i + ", input[i]=" + input[i]);
			assertEquals( i-1, input[i] );
		}

	}



}
