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
package de.enough.polish.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
public final class StringUtil {

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
	public static String replace(String input, String search, String replacement) {
		if (input == null || search == null || replacement == null) {
			throw new IllegalArgumentException( "StringUtil.replace( \"" + input + "\", \"" + search + "\", \"" + replacement + "\" ): given input parameters must not be null.");
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
	 * <pre>TextUtil.split("one;;two;;three", ";;")</pre> results into the array
	 * <pre>{"one", "two", "three"}</pre>.
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
	 * <pre>TextUtil.split("one;two;three", ';')</pre> results into the array
	 * <pre>{"one", "two", "three"}</pre>.
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
	 * <pre>TextUtil.split("one ; two;   three ", ';')</pre> results into the array
	 * <pre>{"one", "two", "three"}</pre>.
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
	 * Splits the given String around the matches defined by the given delimiter into an array while not breaking up areas that are marked with parentheses ().
	 * Example:
	 * <pre>TextUtil.split("one; two; three(test;test2)", ';')</pre> results into the array
	 * <pre>{"one", " two", " three(test;test2)"}</pre>.
	 *
	 * @param value the String which should be split into an array
	 * @param delimiter the delimiter which marks the boundaries of the array 
	 * @return an array, when the delimiter was not found, the array will only have a single element.
	 */
	public static String[] splitWhileKeepingParentheses(String value, char delimiter)
	{
		char[] valueChars = value.toCharArray();
		int lastIndex = 0;
		ArrayList strings = null;
		boolean isParenthesesOpened = false;
		for (int i = 0; i < valueChars.length; i++) {
			char c = valueChars[i];
			if (c == delimiter && !isParenthesesOpened) {
				if (strings == null) {
					strings = new ArrayList();
				}
				strings.add( new String( valueChars, lastIndex, i - lastIndex ) );
				lastIndex = i + 1;
			} else if ( c == ')' ) {
				isParenthesesOpened = false;
			} else if ( c== '(' ) {
				isParenthesesOpened = true;
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
	 * Splits the given String around the matches defined by the given delimiter into an array while not breaking up areas that are marked with parentheses ().
	 * Example:
	 * <pre>TextUtil.split("one; two; three(test;test2)", ';')</pre> results into the array
	 * <pre>{"one", "two", "three(test;test2)"}</pre>.
	 *
	 * @param value the String which should be split into an array
	 * @param delimiter the delimiter which marks the boundaries of the array 
	 * @return an array, when the delimiter was not found, the array will only have a single element.
	 */
	public static String[] splitWhileKeepingParenthesesAndTrim(String value, char delimiter)
	{
		String[] result = splitWhileKeepingParentheses( value, delimiter );
		for (int i = 0; i < result.length; i++) {
			result[i] = result[i].trim();
		}
		return result;
	}

	/**
	 * Splits the given String around the matches defined by the given delimiter into an array.
	 * The resulting text-chunks will be trimmed afterwards.
	 * Example:
	 * <pre>TextUtil.split(" one ;;  two ;;three ", ";;")</pre> results into the array
	 * <pre>{"one", "two", "three"}</pre>.
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

	/**
	 * Checks whether the given input string is numeric.
	 * 
	 * @param text the text
	 * @return true when the given text represents an integer or double value
	 */
	public static boolean isNumeric(String text) {
		char[] chars = text.toCharArray();
		if (chars.length == 0) {
			return false;
		}
		int start = 0;
		if (chars[0] == '-') {
			start = 1;
		}
		boolean dotEncountered = false;
		for (int i = start; i < chars.length; i++) {
			char c = chars[i];
			if ( Character.isDigit(c) ) {
				// that's okay
			} else if (!dotEncountered && c == '.') {
				dotEncountered = true;
			} else {
				return false;
			}
		}
		return true;
	}

	public static String escape(String string) {
		if ( string.indexOf(' ') == -1 ) {
			return string;
		}
		return '"' + string + '"';
	}

	/**
	 * Retrieves properties from the given String array.
	 * 
	 * @param lines the String array that contains properties
	 * @return properties a map containing properties
	 * @throws IllegalArgumentException when an invalid property definition is encountered and ignoreInvalidProperties is false
	 */
	public static Map getProperties(String[] lines) {
		Map map = new HashMap();
		getProperties(lines, '=', '#', map, false );
		return map;
	}

	/**
	 * Retrieves properties from the given String array.
	 * 
	 * @param lines the String array that contains properties
	 * @param delimiter the character that separates a property-name from a property-value.
	 * @return properties a map containing properties
	 * @throws IllegalArgumentException when an invalid property definition is encountered and ignoreInvalidProperties is false
	 */
	public static Map getProperties(String[] lines, char delimiter ) {
		Map map = new HashMap();
		getProperties(lines, delimiter, '#', map, false );
		return map;
	}

	/**
	 * Retrieves properties from the given String array.
	 * 
	 * @param lines the String array that contains properties
	 * @param delimiter the character that separates a property-name from a property-value.
	 * @param comment the char denoting comments
	 * @param properties a map containing properties
	 * @param ignoreInvalidProperties when this flag is true, invalid property definition (those that do not contain the delimiter char) are ignored
	 * @throws IllegalArgumentException when an invalid property definition is encountered and ignoreInvalidProperties is false
	 */
	public static void getProperties(String[] lines, char delimiter, char comment, Map properties, boolean ignoreInvalidProperties ) 
	{
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i].trim();
			if (line.length() == 0 || line.charAt(0) == comment) {
				continue;
			}
			int delimiterPos = line.indexOf( delimiter );
			if (delimiterPos == -1) {
				if (ignoreInvalidProperties) {
					continue;
				} else {
					throw new IllegalArgumentException("The line [" + line 
							+ "] contains an invalid property definition: " +
									"missing separator-character (\"" + delimiter + "\")." );					
				}
			}
			String key = line.substring( 0, delimiterPos ).trim();
			String value = line.substring( delimiterPos + 1 );
			properties.put( key, value );
		}
	}

	/**
	 * Takes a list and returns a space separated string with the string values of each list element.
	 * 
	 * @param arguments a list with objects
	 * @return all objects in a string
	 */
	public static String toString(List arguments) {
		StringBuffer buffer = new StringBuffer();
		for (Iterator iter = arguments.iterator(); iter.hasNext();) {
			String element = iter.next().toString();
			buffer.append( element );
			if (iter.hasNext()) {
				buffer.append(' ');
			}
		}
		return buffer.toString();
	}

	public static String join(String[] array, String delimiter)
	{
		StringBuffer sb = new StringBuffer();
		if (array.length != 0) {
			sb.append(array[0]);
		}
		for (int i = 1; i < array.length; i++) {
			sb.append(delimiter);
			sb.append(array[i]);
		}
		return sb.toString();
	}
}
