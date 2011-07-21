/*
 * Created on 05.01.2006 at 15:14:27.
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

import java.util.Enumeration;


/**
 * Arrays is used for sorting array elements.
 * When no comparator is specified, the default toString() comparator is used. That comparator
 * just compares the string values of the elements using the toString() method.
 * <p>Copyright (c) Enough Software 2006 - 2009</p>
 * 
 * @author Tim Muders
 * @author Robert Virkus
 *
 */
public final class Arrays implements Comparator { 
	
	private static final Comparator STRING_COMPARATOR = new Arrays();
	// instantiation is not allowed
	private Arrays() {
		super();
	}
	
	public int compare(Object o1, Object o2) {
		return o1.toString().compareTo(o2.toString());
	}

	public static boolean byteArrayEquals(byte[] a, byte[] b) {
		if(a == null && b == null) {
			return true;
		}
		if(a == null && b != null) {
			return false;
		}
		if(a != null && b == null) {
			return false;
		}
		if(a.length != b.length) {
			return false;
		}
		for(int i = 0; i < a.length; i++) {
			if(a[i] != b[i]) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Sorts the objects using the standard shell sort implementation and the toString() comparator.
	 * 
	 * @param obj the array of objects that is sorted
	 */
    public static void sort(Object[] obj) {
    		sort( obj, STRING_COMPARATOR );
    }
    
    /**
     * Sorts the given array using the shell sorting algorithm and the default toString() comparator.
     * The shell sort is not as fast as the default quicksort, but it
     * uses less resources and is well suited to sort less than 5,000 entries.
     * 
     * @param array the array elements that should be sorted
     */
    public static void shellSort( Object[] array ) {
    		shellSort( array, STRING_COMPARATOR );
    }

    /**
     * Sorts the given array using the shell sorting algorithm.
     * The shell sort is not as fast as the default quicksort, but it
     * uses less resources and is well suited to sort less than 5,000 entries.
     * 
     * @param array the array elements that should be sorted
     * @param comparator the comparator used for comparing the entries
     */
    public static void shellSort(Object[] array, Comparator comparator) {
    		int i, j, increment;
    		Object temp;
    		increment = 3;
    		while (true) {
    			for (i=0; i < array.length; i++) {
    				j = i;
    				temp = array[i];
    				while ((j >= increment) && ( comparator.compare( array[j-increment], temp) > 0 ) ) {
    					array[j] = array[j - increment];
    					j -= increment;
    				}
    				array[j] = temp;
    			}
    			if (increment == 1) {
    				break;
    			} else {
    				increment >>=  1;
    			}
    		}
	}

    /**
     * Sorts the given array by the toString() output
     * @param obj the input array that is sorted
     */
	public static void quicksort(Object[] obj) {
		quicksort( obj,obj.length, STRING_COMPARATOR );
    }
    
    /**
     * Sorts the given int array
     * @param array the to be sorted array
     * @param elements the split position
     */
    public static void iQuick(int[] array, int elements)
    {
            int left=0, right=elements-1, top=0;
            int sSize = elements/2;
            int[] lStack = new int[sSize];
            int[] rStack = new int[sSize];
            int tmp;
            int i, j, x;

            lStack[top] = left; rStack[top] = right;

            while (top >= 0)
            {
                    left = lStack[top];
                    right = rStack[top];
                    top--;

                    while (left < right)
                    {
                            i = left;
                            j = right;
                            x = array[(left+right)/2];

                            while (i <= j)
                            {
                                while (array[i] < x) i++;
                                while (array[j] > x) j--;

                                    if (i<=j)
                                    {
                                            { // SWAP
                                                    tmp = array[i];
                                                    array[i] = array[j];
                                                    array[j] = tmp;
                                            }
                                            i++;
                                            j--;
                                    }
                            }

                            if (j-left < right-i)
                            {
                                    if (i < right)
                                    {
                                            top++;
                                            lStack[top] = i;
                                            rStack[top] = right;
                                    }
                                    right = j;
                            }
                            else
                            {
                                    if (left < j)
                                    {
                                            top++;
                                            lStack[top] = left;
                                            rStack[top] = j;
                                    }
                                    left = i;
                            }
                    }
            }
    }
    
    public static void quicksort(Object[] obj, int elements, Comparator comparator)
    {
    	if(elements <= 1) {
    		return;
    	}
    	
        int left=0, right=elements-1, top=0;
        int sSize = elements/2;
        int[] lStack = new int[sSize];
        int[] rStack = new int[sSize];
        Object tmp;
        int i, j;
        Object pivot;

        lStack[top] = left; rStack[top] = right;

        while (top >= 0)
        {
                left = lStack[top];
                right = rStack[top];
                top--;

                while (left < right)
                {
                        i = left;
                        j = right;
                        pivot = obj[(left+right)/2];

                        while (i < j)
                        {
                        	while (comparator.compare(pivot,obj[i]) > 0 )i++;
                            while (comparator.compare(pivot,obj[j]) < 0) j--;

                                if (i<=j)
                                {
                                        { // SWAP
                                                tmp = obj[i];
                                                obj[i] = obj[j];
                                                obj[j] = tmp;
                                        }
                                        i++;
                                        j--;
                                }
                        }

                        if (j-left < right-i)
                        {
                                if (i < right)
                                {
                                        top++;
                                        lStack[top] = i;
                                        rStack[top] = right;
                                }
                                right = j;
                        }
                        else
                        {
                                if (left < j)
                                {
                                        top++;
                                        lStack[top] = left;
                                        rStack[top] = j;
                                }
                                left = i;
                        }
                }
        }
    }
    
    
    public static void sort(Object[] obj, Comparator comparator) {
    		shellSort( obj, comparator );
    		/*
    	int elements = obj.length;
    	int left=0, right=elements-1, top=0;
    	int sSize = elements/2;
    	int lStack[] = new int[sSize];
    	int rStack[] = new int[sSize];
    	Object tmp;
    	int i = 0, j = 0, x = 0;
    	lStack[top] = left; rStack[top] = right;
        while (top >= 0) {
            left = lStack[top];
            right = rStack[top];
            top--;
            do{
                i = left;
                j = right;
                x = (left+right)/2;
                while (i <= j) {    	
                    while (comparator.compare(obj[x],obj[i]) >= 0) {
                    	i++;
                    }
                    while (comparator.compare(obj[x],obj[j]) <= 0) {
                    	j--;
                    }
                    if (i<=j) {
                        // SWAP
                        tmp = obj[i];
                        obj[i] = obj[j];
                        obj[j] = tmp;         
                        i++;
                        j--;
                    }
                }

                if (j-left < right-i) {
                    if (i < right) {
                        top++;
                        lStack[top] = i;
                        rStack[top] = right;
                    }
                    right = j;
                } else {
                    if (left < j) {
                        top++;
                        lStack[top] = left;
                        rStack[top] = j;
                    }
                    left = i;
                }
            }while (left <= right) ;
        }
        */
    }
    
    /**
     * Compares two given array on equality.
     * You can test primitive arrays (like int[] or float[]) and Object based arrays.
     * Ob Object arrays the equals() method is used for comparison.
     * 
     * @param array1 the first array
     * @param array2 the second array
     * @return true when both arrays contain equal elements
     * @throws ClassCastException when the arrays do have different types and the first one has a primitive type
     */
    public static boolean equals( Object array1, Object array2 )  {
    		if (array1 instanceof byte[] ) {
    			byte[] pa1 = (byte[]) array1;
    			byte[] pa2 = (byte[]) array2;
    			if (pa1.length != pa2.length) {
    				return false;
    			}
    			for (int i = 0; i < pa1.length; i++) {
				byte p1 = pa1[i];
				byte p2 = pa2[i];
				if (p1 != p2) {
					return false;
				}
			}
    		} else if (array1 instanceof short[] ) {
    			short[] pa1 = (short[]) array1;
    			short[] pa2 = (short[]) array2;
    			if (pa1.length != pa2.length) {
    				return false;
    			}
    			for (int i = 0; i < pa1.length; i++) {
				short p1 = pa1[i];
				short p2 = pa2[i];
				if (p1 != p2) {
					return false;
				}
			}
    		} else if (array1 instanceof int[] ) {
    			int[] pa1 = (int[]) array1;
    			int[] pa2 = (int[]) array2;
    			if (pa1.length != pa2.length) {
    				return false;
    			}
    			for (int i = 0; i < pa1.length; i++) {
				int p1 = pa1[i];
				int p2 = pa2[i];
				if (p1 != p2) {
					return false;
				}
			}
    		} else if (array1 instanceof long[] ) {
    			long[] pa1 = (long[]) array1;
    			long[] pa2 = (long[]) array2;
    			if (pa1.length != pa2.length) {
    				return false;
    			}
    			for (int i = 0; i < pa1.length; i++) {
    				long p1 = pa1[i];
    				long p2 = pa2[i];
				if (p1 != p2) {
					return false;
				}
			}
    		} else if (array1 instanceof boolean[] ) {
    			boolean[] pa1 = (boolean[]) array1;
    			boolean[] pa2 = (boolean[]) array2;
    			if (pa1.length != pa2.length) {
    				return false;
    			}
    			for (int i = 0; i < pa1.length; i++) {
    				boolean p1 = pa1[i];
    				boolean p2 = pa2[i];
				if (p1 != p2) {
					return false;
				}
			}
    		//#if polish.hasFloatingPoint
    		} else if (array1 instanceof float[] ) {
    			float[] pa1 = (float[]) array1;
    			float[] pa2 = (float[]) array2;
    			if (pa1.length != pa2.length) {
    				return false;
    			}
    			for (int i = 0; i < pa1.length; i++) {
    				float p1 = pa1[i];
    				float p2 = pa2[i];
				if (p1 != p2) {
					return false;
				}
			}
    		} else if (array1 instanceof double[] ) {
    			double[] pa1 = (double[]) array1;
    			double[] pa2 = (double[]) array2;
    			if (pa1.length != pa2.length) {
    				return false;
    			}
    			for (int i = 0; i < pa1.length; i++) {
    				double p1 = pa1[i];
    				double p2 = pa2[i];
				if (p1 != p2) {
					return false;
				}
			}
    		//#endif
    		} else if (array1 instanceof Object[] ) {
    			Object[] oa1 = (Object[]) array1; 
    			Object[] oa2 = (Object[]) array2; 
    			if (oa1.length != oa2.length) {
    				return false;
    			}
    			for (int i = 0; i < oa1.length; i++) {
    				Object o1 = oa1[i];
    				Object o2 = oa2[i];
    				if ( (o1 == null && o2 != null) || (!o1.equals(o2)) ) {
					return false;
				}
			}
    		} else {
    			return array1.equals(array2);
    		}
    		return true;
    }

	/**
	 * Casts the given input array into the output array 
	 * @param input the input array, may be null
	 * @param output the output array
	 * @return the filled output array
	 */
	public static Object[] cast(Object[] input, Object[] output)
	{
		if (input == null) {
			return null;
		}
		System.arraycopy(input, 0, output, 0, Math.min( input.length, output.length) );
		return output;
	}
	
	/**
	 * A replacement for System.arraycopy() which crashes badly on some Nokia N78 firmware versions
	 * Usage of this method is automatically triggered when the Bug "noSystemArrayCopy" is defined.
	 * 
	 * @param source the source array
	 * @param sourceOffset the start index for copying
	 * @param destination the target array
	 * @param destinationOffset the start index for copying
	 * @param length the number of copied elements 
	 */
	public static void arraycopy( Object source, int sourceOffset, Object destination, int destinationOffset, int length ) {
		if (source instanceof byte[]) {
			arraycopyImpl( (byte[])source, sourceOffset, (byte[])destination, destinationOffset, length);
		} else if (source instanceof int[]) {
			arraycopyImpl( (int[])source, sourceOffset, (int[])destination, destinationOffset, length);
		} else if (source instanceof Object[]) {
			arraycopyImpl( (Object[])source, sourceOffset, (Object[])destination, destinationOffset, length);
		} else if (source instanceof short[]) {
			arraycopyImpl( (short[])source, sourceOffset, (short[])destination, destinationOffset, length);
		} else if (source instanceof long[]) {
			arraycopyImpl( (long[])source, sourceOffset, (long[])destination, destinationOffset, length);
		} else if (source instanceof boolean[]) {
			arraycopyImpl( (boolean[])source, sourceOffset, (boolean[])destination, destinationOffset, length);
		} else if (source instanceof char[]) {
			arraycopyImpl( (char[])source, sourceOffset, (char[])destination, destinationOffset, length);
		//#if polish.hasFloatingPoint
		} else if (source instanceof float[]) {
			arraycopyImpl( (float[])source, sourceOffset, (float[])destination, destinationOffset, length);
		} else if (source instanceof double[]) {
			arraycopyImpl( (double[])source, sourceOffset, (double[])destination, destinationOffset, length);
		//#endif
		}
	}

	private static void arraycopyImpl(char[] source, int sourceOffset,
			char[] destination, int destinationOffset, int length)
	{
		if (source == destination && destinationOffset > sourceOffset && sourceOffset + length > destinationOffset) {
			for (int i=length; --i >= 0; ) {
				destination[destinationOffset+i] = source[sourceOffset+i];
			}			
		} else {
			for (int i=0; i<length; i++) {
				destination[destinationOffset+i] = source[sourceOffset+i];
			}
		}
	}

	//#if polish.hasFloatingPoint
	private static void arraycopyImpl(double[] source, int sourceOffset,
			double[] destination, int destinationOffset, int length)
	{
		if (source == destination && destinationOffset > sourceOffset && sourceOffset + length > destinationOffset) {
			for (int i=length; --i >= 0; ) {
				destination[destinationOffset+i] = source[sourceOffset+i];
			}			
		} else {
			for (int i=0; i<length; i++) {
				destination[destinationOffset+i] = source[sourceOffset+i];
			}
		}
	}
	//#endif

	//#if polish.hasFloatingPoint
	private static void arraycopyImpl(float[] source, int sourceOffset,
			float[] destination, int destinationOffset, int length)
	{
		if (source == destination && destinationOffset > sourceOffset && sourceOffset + length > destinationOffset) {
			for (int i=length; --i >= 0; ) {
				destination[destinationOffset+i] = source[sourceOffset+i];
			}			
		} else {
			for (int i=0; i<length; i++) {
				destination[destinationOffset+i] = source[sourceOffset+i];
			}
		}
	}
	//#endif

	private static void arraycopyImpl(boolean[] source, int sourceOffset,
			boolean[] destination, int destinationOffset, int length)
	{
		if (source == destination && destinationOffset > sourceOffset && sourceOffset + length > destinationOffset) {
			for (int i=length; --i >= 0; ) {
				destination[destinationOffset+i] = source[sourceOffset+i];
			}			
		} else {
			for (int i=0; i<length; i++) {
				destination[destinationOffset+i] = source[sourceOffset+i];
			}
		}
	}

	private static void arraycopyImpl(long[] source, int sourceOffset,
			long[] destination, int destinationOffset, int length)
	{
		if (source == destination && destinationOffset > sourceOffset && sourceOffset + length > destinationOffset) {
			for (int i=length; --i >= 0; ) {
				destination[destinationOffset+i] = source[sourceOffset+i];
			}			
		} else {
			for (int i=0; i<length; i++) {
				destination[destinationOffset+i] = source[sourceOffset+i];
			}
		}
	}

	private static void arraycopyImpl(short[] source, int sourceOffset,
			short[] destination, int destinationOffset, int length)
	{
		if (source == destination && destinationOffset > sourceOffset && sourceOffset + length > destinationOffset) {
			for (int i=length; --i >= 0; ) {
				destination[destinationOffset+i] = source[sourceOffset+i];
			}			
		} else {
			for (int i=0; i<length; i++) {
				destination[destinationOffset+i] = source[sourceOffset+i];
			}
		}
	}

	private static void arraycopyImpl(Object[] source, int sourceOffset,
			Object[] destination, int destinationOffset, int length)
	{
		if (source == destination && destinationOffset > sourceOffset && sourceOffset + length > destinationOffset) {
			for (int i=length; --i >= 0; ) {
				destination[destinationOffset+i] = source[sourceOffset+i];
			}			
		} else {
			for (int i=0; i<length; i++) {
				destination[destinationOffset+i] = source[sourceOffset+i];
			}
		}
	}

	private static void arraycopyImpl(int[] source, int sourceOffset,
			int[] destination, int destinationOffset, int length)
	{
		if (source == destination && destinationOffset > sourceOffset && sourceOffset + length > destinationOffset) {
			for (int i=length; --i >= 0; ) {
				destination[destinationOffset+i] = source[sourceOffset+i];
			}			
		} else {
			for (int i=0; i<length; i++) {
				destination[destinationOffset+i] = source[sourceOffset+i];
			}
		}
	}

	private static void arraycopyImpl(byte[] source, int sourceOffset, byte[] destination, int destinationOffset, int length )
	{
		if (source == destination && destinationOffset > sourceOffset && sourceOffset + length > destinationOffset) {
			for (int i=length; --i >= 0; ) {
				destination[destinationOffset+i] = source[sourceOffset+i];
			}			
		} else {
			for (int i=0; i<length; i++) {
				destination[destinationOffset+i] = source[sourceOffset+i];
			}
		}
	}
	
	/**
	 * Extracts the array out of the specified enumeration.
	 * @param enumeration the enumeration
	 * @return an array of all elements within the enumeration
	 */
	public static Object[] toArray( Enumeration enumeration ) {
		ArrayList list = new ArrayList();
		while (enumeration.hasMoreElements()) {
			list.add( enumeration.nextElement() );
		}
		return list.toArray();
	}
	
	/**
	 * Copies the enumeration into the given (possibly typed) array.
	 * @param enumeration the enumeration
	 * @param target the array
	 * @return the original target array
	 */
	public static Object[] toArray( Enumeration enumeration, Object[] target ) {
		for (int i = 0; i < target.length; i++) {
			target[i] = enumeration.nextElement();
		}
		return target;
	}

}
