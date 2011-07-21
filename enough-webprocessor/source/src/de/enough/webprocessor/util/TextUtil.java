/*
 * Created on 24-Nov-2003 at 14:38:43
 *
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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
package de.enough.webprocessor.util;

import java.util.ArrayList;

/**
 * <p>Provides some usefull String methods.</p>
 * <p></p>
 * <p>copyright Enough Software 2003, 2004</p>
 * <pre>
 *    history
 *       14-Jan-2004 (rob) added split-method  
 *       24-Nov-2003 (rob) creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public final class TextUtil {

	/**
	 * Replaces all search-strings in the given input.
	 *  
	 * @param input The original string
	 * @param search The string which should be replaced in the input
	 * @param replacement The replacement for the search-string
	 * @return The input string in which all search-strings are replaced with the replacement-string.
	 * @throws IllegalArgumentException when on of the parameters is null.
	 * @see #replace(String, char, char) for a fast alternative
	 */
	public static final String replace(String input, String search, String replacement) {
		if (input == null || search == null || replacement == null) {
			throw new IllegalArgumentException( "TextUtil.replace: given input parameters must not be null.");
		}
		int startPos = 0;
		int pos;
		int add = replacement.length() - search.length();
		int totalAdd = 0;
		int replaceLength = search.length();
		StringBuffer replace = new StringBuffer(input);
		while ((pos = input.indexOf(search, startPos)) != -1) {
			replace.replace(pos + totalAdd, pos + totalAdd + replaceLength, replacement);
			totalAdd += add;
			startPos = pos + 1;
		}
		return replace.toString();
	}

	/**
	 * Replaces all search-chars in the given string.
	 * This method is much faster than replace*(String, String, String).
	 * 
	 * @param input The original string
	 * @param search The char which should be replaced in the input
	 * @param replacement The replacement for the search-char
	 * @return The input string in which all search-chars are replaced with the replacement-char.
	 * @throws NullPointerException when the input is null.
	 */
	public static String replace(String input, char search, char replacement) {
		return input.replace(search, replacement);
	}
	
	/**
	 * Replaces the first search-string in the given input.
	 *  
	 * @param input the original string
	 * @param search the string which should be replaced in the input
	 * @param replacement the replacement for the search-string
	 * @return the input string in which all search-strings are replaced with the replacement-string.
	 * @throws IllegalArgumentException when on of the parameters is null.
	 */
	public static String replaceFirst(String input, String search, String replacement) {
		if (input == null || search == null || replacement == null) {
			throw new IllegalArgumentException( "TextUtil.replace: given input parameters must not be null.");
		}
		int pos = input.indexOf( search ); 
		if (pos == -1){
			return input;
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append( input.substring(0, pos ))
			    .append( replacement )
				.append( input.substring( pos + search.length() ) );
		return buffer.toString();
	}
	
	/**
	 * Splits the given String around the matches defined by the given delimiter into an array.
	 * Example:
	 * <value>TextUtil.split("one;;two;;three", ";;")</value> results into the array
	 * <value>{"one", "two", "three"}</value>.
	 *
	 * @param value the String which should be split into an array
	 * @param delimiter the delimiter which marks the boundaries of the array 
	 * @return an array, when the delimiter was not found, the array will only have a single element.
	 * @see #split(String, char) for an even faster alternative
	 */
	public static String[] split(String value, String delimiter) {
		int lastIndex = 0;
		ArrayList strings = null;
		int currentIndex = 0;
		while ( (currentIndex = value.indexOf(delimiter, lastIndex ) ) != -1) {
			if (strings == null) {
				strings = new ArrayList();
			}
			strings.add( value.substring( lastIndex, currentIndex ) );
			lastIndex = currentIndex + delimiter.length();
		}
		if (strings == null) {
			return new String[]{ value };
		}
		// add tail:
		strings.add( value.substring( lastIndex ) );
		return (String[]) strings.toArray( new String[ strings.size() ] );
	}
	
	/**
	 * Splits the given String around the matches defined by the given delimiter into an array.
	 * Example:
	 * <value>TextUtil.split("one;two;three", ';')</value> results into the array
	 * <value>{"one", "two", "three"}</value>.
	 *
	 * @param value the String which should be split into an array
	 * @param delimiter the delimiter which marks the boundaries of the array 
	 * @return an array, when the delimiter was not found, the array will only have a single element.
	 */
	public static String[] split(String value, char delimiter) {
		char[] valueChars = value.toCharArray();
		int lastIndex = 0;
		ArrayList strings = null;
		for (int i = 0; i < valueChars.length; i++) {
			char c = valueChars[i];
			if (c == delimiter) {
				if (strings == null) {
					strings = new ArrayList();
				}
				strings.add( new String( valueChars, lastIndex, i - lastIndex ) );
				lastIndex = i + 1;
			}
		}
		if (strings == null) {
			return new String[]{ value };
		}
		// add tail:
		strings.add( new String( valueChars, lastIndex, valueChars.length - lastIndex ) );
		return (String[]) strings.toArray( new String[ strings.size() ] );
	}

	/**
	 * Splits the given String around the matches defined by the given delimiter into an array.
	 * The resulting text-chunks will be trimmed afterwards.
	 * Example:
	 * <value>TextUtil.split("one ; two;   three ", ';')</value> results into the array
	 * <value>{"one", "two", "three"}</value>.
	 *
	 * @param value the String which should be split into an array
	 * @param delimiter the delimiter which marks the boundaries of the array 
	 * @return an array, when the delimiter was not found, the array will only have a single element.
	 */
	public static String[] splitAndTrim(String value, char delimiter) {
		String[] result = split( value, delimiter );
		for (int i = 0; i < result.length; i++) {
			result[i] = result[i].trim();
		}
		return result;
	}

	/**
	 * Splits the given String around the matches defined by the given delimiter into an array.
	 * The resulting text-chunks will be trimmed afterwards.
	 * Example:
	 * <value>TextUtil.split(" one ;;  two ;;three ", ";;")</value> results into the array
	 * <value>{"one", "two", "three"}</value>.
	 *
	 * @param value the String which should be split into an array
	 * @param delimiter the delimiter which marks the boundaries of the array 
	 * @return an array, when the delimiter was not found, the array will only have a single element.
	 */
	public static String[] splitAndTrim(String value, String delimiter) {
		String[] result = split( value, delimiter );
		for (int i = 0; i < result.length; i++) {
			result[i] = result[i].trim();
		}
		return result;
	}


}
