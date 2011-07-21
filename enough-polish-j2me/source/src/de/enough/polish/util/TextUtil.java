/*
 * Created on 20-Apr-2004 at 01:30:49.
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

import java.io.UnsupportedEncodingException;

//#if polish.midp || polish.usePolishGui
import javax.microedition.lcdui.Font;
//#endif

/**
 * <p>Provides some useful String methods.</p>
 *
 * <p>Copyright Enough Software 2004 - 2011</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public final class TextUtil {

	/**
	 * standard maximum lines number for text wrapping 
	 * @see #wrap(String, Font, int, int, int, String)
	 */
	public static final int MAXLINES_UNLIMITED = Integer.MAX_VALUE;

	/**
	 * Position of the appendix is at the end of the text.
	 * @see #wrap(String, Font, int, int, int, String)
	 */
	public static final int MAXLINES_APPENDIX_POSITION_AFTER = 0x00;

	/**
	 * Position of the appendix is at the beginning of the text.
	 * @see #wrap(String, Font, int, int, int, String)
	 */
	public static final int MAXLINES_APPENDIX_POSITION_BEFORE = 0x01;

	/**
	 * the default appendix to attach to a truncated text
	 * @see #wrap(String, Font, int, int, int, String)
	 */
	public static final String MAXLINES_APPENDIX = "...";

	private static final String UNRESERVED = "-_.!~*'()\"";

	private static final String HEXES = "0123456789ABCDEF";
	/**
	 * A string form (for less overhead when unescaping is not used) of named HTML entities, compare 
	 * http://www.w3.org/TR/WD-html40-970708/sgml/entities.html.
	 * For making this list as dense as possible, we just have the name followed by the integer value of that named character.
	 * nbsp160iexcl161 means &amp;nbsp; translates to &amp;#160; and &amp;iexcl; to &amp;#161;, for example.
	 * Problematic are names such as sup2 or frac34 which include numbers, they can lead to wrong unescaping when
	 * there is for example an (invalid) &amp;frac; entity. For now we deal with that risk, however.
	 * When going further there are elements such as sub, sube and nsub for example, when they are not ordered correctly we might find nsub when searching for sub, for example. 
	 * Right now we don't support neither Greek nor mathematical named HTML entities, however. 
	 */
	private static final String HTML_NAMED_ENTITIES = "quot34amp38lt60gt62"
													+ "nbsp160iexcl161cent162pound163curren164yen165brvbar166sect167uml168copy169ordf170laquo171not172shy173reg174macr175deg176plusmn177sup2178sup3179acute180micro181para182middot183cedil184sup1185ordm186raquo187frac14188frac12189frac34190iquest191Agrave192Aacute193Acirc194Atilde195Auml196Aring197AElig198Ccedil199Egrave200Eacute201Ecirc202Euml203Igrave204Iacute205Icirc206Iuml207ETH208Ntilde209Ograve210Oacute211Ocirc212Otilde213Ouml214times215Oslash216Ugrave217Uacute218Ucirc219Uuml220Yacute221THORN222szlig223"
													+ "agrave224aacute225acirc226atilde227auml228aring229aelig230ccedil231egrave232eacute233ecirc234euml235igrave236iacute237icirc238iuml239eth240ntilde241ograve242oacute243ocirc244otilde245ouml246divide247oslash248ugrave249uacute250ucirc251uuml252yacute253thorn254yuml255";

	/**
	 * Splits the given String around the matches defined by the given delimiter into an array.
	 * Example:
	 * <code>TextUtil.split("one;two;three", ';')</code> results into the array
	 * <code>{"one", "two", "three"}</code>.<br />
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
	 * Example:
	 * <code>TextUtil.splitAndTrim(" one; two; three", ';')</code> results into the array
	 * <code>{"one", "two", "three"}</code>.<br />
	 *
	 * @param value the String which should be split into an array
	 * @param delimiter the delimiter which marks the boundaries of the array 
	 * @return an array, when the delimiter was not found, the array will only have a single element.
	 */
	public static String[] splitAndTrim(String value, char delimiter) 
	{
		String[] result = split(value, delimiter);
		for (int i = 0; i < result.length; i++)
		{
			result[i] = result[i].trim();

		}
		return result;
	}

	/**
	 * Splits the given String around the matches defined by the given delimiter into an array.
	 * Example:
	 * <code>TextUtil.split("one;two;three", ';', 3)</code> results into the array
	 * <code>{"one", "two", "three"}</code>.<br />
	 * <code>TextUtil.split("one;two;three", ';', 4)</code> results into the array
	 * <code>{"one", "two", "three", null}</code>.<br />
	 * <code>TextUtil.split("one;two;three", ';', 2)</code> results into the array
	 * <code>{"one", "two"}</code>.<br />
	 * This method is less resource intensive compared to the other split method, since
	 * no temporary list needs to be created
	 *
	 * @param value the String which should be split into an array
	 * @param delimiter the delimiter which marks the boundaries of the array 
	 * @param numberOfChunks the number of expected matches
	 * @return an array with the length of numberOfChunks, when not enough elements are found, the array will contain null elements
	 */
	public static String[] split(String value, char delimiter, int numberOfChunks) {
		char[] valueChars = value.toCharArray();
		int lastIndex = 0;
		String[] chunks = new String[ numberOfChunks ];
		int chunkIndex = 0;
		for (int i = 0; i < valueChars.length; i++) {
			char c = valueChars[i];
			if (c == delimiter) {
				chunks[ chunkIndex ] = value.substring( lastIndex, i );
				lastIndex = i + 1;
				chunkIndex++;
				if (chunkIndex == numberOfChunks ) {
					break;
				}
			}
		}
		if (chunkIndex < numberOfChunks) {
			// add tail:
			chunks[chunkIndex] = value.substring( lastIndex, valueChars.length );
		}
		return chunks;
	}

	//#if polish.midp || polish.usePolishGui
	/**
	 * Wraps the given string so it fits on the specified lines.
	 * First of all it is split at the line-breaks ('\n'), subsequently the substrings
	 * are split when they do not fit on a single line.
	 *  
	 * @param value the string which should be split
	 * @param font the font which is used to display the font
	 * @param firstLineWidth the allowed width for the first line
	 * @param lineWidth the allowed width for all other lines, lineWidth >= firstLineWidth
	 * @return the array containing the substrings
	 * @deprecated please use wrap instead
	 * @see #wrap(String, Font, int, int)
	 */
	public static String[] split( String value, Font font, int firstLineWidth, int lineWidth ) {
		return wrap(value, font, firstLineWidth, lineWidth, MAXLINES_UNLIMITED, (String)null, TextUtil.MAXLINES_APPENDIX_POSITION_AFTER);
	}
	//#endif

	//#if polish.midp || polish.usePolishGui
	/**
	 * Wraps the given string so it fits on the specified lines.
	 * First of all it is split at the line-breaks ('\n'), subsequently the substrings
	 * are split when they do not fit on a single line.
	 *  
	 * @param value the string which should be split
	 * @param font the font which is used to display the font
	 * @param firstLineWidth the allowed width for the first line
	 * @param lineWidth the allowed width for all other lines, lineWidth >= firstLineWidth
	 * @return the array containing the substrings
	 */
	public static String[] wrap( String value, Font font, int firstLineWidth, int lineWidth) {
		return wrap(value, font, firstLineWidth, lineWidth, MAXLINES_UNLIMITED, (String)null, TextUtil.MAXLINES_APPENDIX_POSITION_AFTER);
	}

	/**
	 * Wraps the given string so it fits on the specified lines.
	 * First of all the text is split at the line-breaks ('\n'), subsequently the substrings
	 * are split when they do not fit on a single line.
	 *  
	 * @param value the string which should be wrapped
	 * @param font the font which is used to display the font
	 * @param firstLineWidth the allowed width for the first line
	 * @param lineWidth the allowed width for all other lines, lineWidth >= firstLineWidth
	 * @param maxLines the maximum number of lines
	 * @param maxLinesAppendix the appendix that should be added to the last line when the line number is greater than maxLines
	 * @return the array containing the substrings
	 */
	public static String[] wrap( String value, Font font, int firstLineWidth, int lineWidth, int maxLines, String maxLinesAppendix ) {
		return wrap( value, font, firstLineWidth, lineWidth, maxLines, maxLinesAppendix, MAXLINES_APPENDIX_POSITION_AFTER );
	}
	
	/**
	 * Wraps the given string so it fits on the specified lines.
	 * First of all the text is split at the line-breaks ('\n'), subsequently the substrings
	 * are split when they do not fit on a single line.
	 *  
	 * @param value the string which should be wrapped
	 * @param font the font which is used to display the font
	 * @param firstLineWidth the allowed width for the first line
	 * @param lineWidth the allowed width for all other lines, lineWidth >= firstLineWidth
	 * @param maxLines the maximum number of lines
	 * @param maxLinesAppendix the appendix that should be added to the last line when the line number is greater than maxLines
	 * @param maxLinesAppendixPosition either MAXLINES_APPENDIX_POSITION_AFTER or MAXLINES_APPENDIX_POSITION_BEFORE
	 * @return the array containing the substrings
	 */
	public static String[] wrap( String value, Font font, int firstLineWidth, int lineWidth, int maxLines, String maxLinesAppendix, int maxLinesAppendixPosition ) {
		WrappedText result = new WrappedText();
		wrap( value, font, firstLineWidth, lineWidth, maxLines, maxLinesAppendix, maxLinesAppendixPosition, result );
		return result.getLines();
	}
	
	/**
	 * Wraps the given string so it fits on the specified lines.
	 * First of all the text is split at the line-breaks ('\n'), subsequently the substrings
	 * are split when they do not fit on a single line.
	 *  
	 * @param value the string which should be wrapped
	 * @param font the font which is used to display the font
	 * @param firstLineWidth the allowed width for the first line
	 * @param lineWidth the allowed width for all other lines, lineWidth >= firstLineWidth
	 * @param maxLines the maximum number of lines
	 * @param maxLinesAppendix the appendix that should be added to the last line when the line number is greater than maxLines
	 * @param maxLinesAppendixPosition either MAXLINES_APPENDIX_POSITION_AFTER or MAXLINES_APPENDIX_POSITION_BEFORE
	 * @param result the WrappedText that should be reused (should be cleared by caller in most cases)
	 */
	public static void wrap( String value, Font font, int firstLineWidth, int lineWidth, int maxLines, String maxLinesAppendix, int maxLinesAppendixPosition, WrappedText result ) 
	{
		if (firstLineWidth <= 0 || lineWidth <= 0) {
			//#debug error
			System.out.println("INVALID LINE WIDTH FOR SPLITTING " + firstLineWidth + " / " + lineWidth + " ( for string " + value + ")");
			//#if polish.debug.error
			try { throw new RuntimeException("INVALID LINE WIDTH FOR SPLITTING " + firstLineWidth + " / " + lineWidth + " ( for string " + value + ")"); } catch (Exception e) { e.printStackTrace(); }	
			//#endif
			int width = font.stringWidth(value);
			result.addLine(value, width);
			return;
		}
		boolean hasLineBreaks = (value.indexOf('\n') != -1);
		int completeWidth = font.stringWidth(value);
		if ( (completeWidth <= firstLineWidth && !hasLineBreaks) ) {
			// the given string fits on the first line:
			result.addLine(value, completeWidth);
			return;
		}
		// the given string does not fit on the first line:
		if (!hasLineBreaks) {
			wrap( value, font, completeWidth, firstLineWidth, lineWidth, result, maxLines, maxLinesAppendixPosition);
		} else {
			// now the string will be split at the line-breaks and
			// then each line is processed:
			char[] valueChars = value.toCharArray();
			int lastIndex = 0;
			char c =' ';
			int lineBreakCount = 0;
			for (int i = 0; i < valueChars.length; i++) {
				c = valueChars[i];
				boolean isCRLF = (c == 0x0D && i < valueChars.length -1 &&  valueChars[i +1] == 0x0A);
				if (c == '\n' || i == valueChars.length -1 || isCRLF ) {
					lineBreakCount++;
					String line = null;
					if (i == valueChars.length -1) {
						line = new String( valueChars, lastIndex, (i + 1) - lastIndex );
						//System.out.println("wrap: adding last line " + line );
					} else {
						line = new String( valueChars, lastIndex, i - lastIndex );
						//System.out.println("wrap: adding " + line );
					}
					completeWidth = font.stringWidth(line);
					if (completeWidth <= firstLineWidth ) {
						result.addLine( line, completeWidth );

						if(result.size() == maxLines)
						{
							// break if the maximum number of lines is reached
							break;
						}
					} else {
						wrap(line, font, completeWidth, firstLineWidth, lineWidth, result, maxLines, maxLinesAppendixPosition);
					}
					if (isCRLF) {
						i++;
					}
					lastIndex = i + 1;
					// after the first line all line widths are the same:
					firstLineWidth = lineWidth;
				} // for each line
			} // for all chars
			// special case for lines that end with \n: add a further line
			if (lineBreakCount > 1 && (c == '\n' || c == 10) && result.size() != maxLines) {
				result.addLine("", 0);
			}
		}

		if(result.size() >= maxLines)
		{
			if (maxLinesAppendix == null) {
				maxLinesAppendix = MAXLINES_APPENDIX;
			}
			addAppendix(font, firstLineWidth, maxLinesAppendix, maxLinesAppendixPosition, maxLines - 1, result  );
			while (result.size() > maxLines) {
				result.removeLine( result.size() -1 );
			}
		}

		//#debug
		System.out.println("Wrapped [" + value + "] into " + result.size() + " rows.");
	}
	//#endif

	//#if polish.midp || polish.usePolishGui
	/**
	 * Wraps the given string so that the substrings fit into the the given line-widths.
	 * It is expected that the specified lineWidth >= firstLineWidth.
	 * The resulting substrings will be added to the given ArrayList.
	 * When the complete string fits into the first line, it will be added
	 * to the list.
	 * When the string needs to be wrapped to fit on the lines, it is tried to
	 * split the string at a gap between words. When this is not possible, the
	 * given string will be wrapped in the middle of the corresponding word. 
	 * 
	 * 
	 * @param value the string which should be wrapped
	 * @param font the font which is used to display the font
	 * @param completeWidth the complete width of the given string for the specified font.
	 * @param firstLineWidth the allowed width for the first line
	 * @param lineWidth the allowed width for all other lines, lineWidth >= firstLineWidth
	 * @param result the WrappedText to which the substrings will be added.
	 * @deprecated please use wrap instead
	 * @see #wrap(String, Font, int, int, int, WrappedText, int, int)
	 */
	public static void split( String value, Font font, 
			int completeWidth, int firstLineWidth, int lineWidth, 
			WrappedText result ) 
	{
		wrap(value, font, completeWidth, firstLineWidth, lineWidth, result, MAXLINES_UNLIMITED, MAXLINES_APPENDIX_POSITION_AFTER);
	}
	//#endif

	//#if polish.midp || polish.usePolishGui
	/**
	 * Wraps the given string so that the substrings fit into the the given line-widths.
	 * It is expected that the specified lineWidth >= firstLineWidth.
	 * The resulting substrings will be added to the given ArrayList.
	 * When the complete string fits into the first line, it will be added
	 * to the list.
	 * When the string needs to be wrapped to fit on the lines, it is tried to
	 * split the string at a gap between words. When this is not possible, the
	 * given string will be wrapped in the middle of the corresponding word. 
	 * 
	 * 
	 * @param value the string which should be wrapped
	 * @param font the font which is used to display the font
	 * @param completeWidth the complete width of the given string for the specified font.
	 * @param firstLineWidth the allowed width for the first line
	 * @param lineWidth the allowed width for all other lines, lineWidth >= firstLineWidth
	 * @param maxLines the maximum number of lines 
	 * @param maxLinesAppendixPosition either MAXLINES_APPENDIX_POSITION_AFTER or MAXLINES_APPENDIX_POSITION_BEFORE
	 * @param list the list to which the substrings will be added.
	 */
	public static void wrap( String value, Font font, 
			int completeWidth, int firstLineWidth, int lineWidth, 
			WrappedText list,
			int maxLines, int maxLinesAppendixPosition ) 
	{
		
		//TODO extend wrapping : bottom line - based wrapping 
		if(maxLinesAppendixPosition == MAXLINES_APPENDIX_POSITION_BEFORE && maxLines == 1) {
			list.addLine(value, 0);
			return;
		}

		int lastLineIndex = maxLines - 1;
		char[] valueChars = value.toCharArray();
		int startPos = 0;
		int lastSpacePos = -1;
		int lastSpacePosLineWidth = 0;
		int currentLineWidth = 0;
		for (int i = 0; i < valueChars.length; i++) {
			char c = valueChars[i];
			currentLineWidth += font.charWidth( c );
			if (c == '\n') {
				list.addLine( new String( valueChars, startPos, i - startPos ), currentLineWidth );
				lastSpacePos = -1;
				startPos = i+1;
				currentLineWidth = 0;
				firstLineWidth = lineWidth; 
				i = startPos;
			} else if (currentLineWidth >= firstLineWidth && i > 0) {
				if(list.size() == lastLineIndex)
				{
					// add the remainder of the value
					String line = new String( valueChars, startPos, valueChars.length - startPos );
					currentLineWidth = font.stringWidth(line);
					list.addLine( line, currentLineWidth );
					break;
				}

				// need to create a line break:
				if (c == ' ' || c == '\t') { 
					String line = new String( valueChars, startPos, i - startPos );
					int stringWidth = font.stringWidth(line);
					if (lastSpacePos != -1 && (stringWidth > currentLineWidth) ) {
						// adding the widths of characters does not always yield a correct result,
						// so using stringWidth here ensures that we really break at the correct position:
						if (i > startPos + 1) {
							i--;
						}
						//System.out.println("value=" + value + ", i=" + i + ", startPos=" + startPos);
						line = new String( valueChars, startPos, lastSpacePos - startPos );
						stringWidth = font.stringWidth(line);
						list.addLine( line, stringWidth);
						startPos =  lastSpacePos;
						currentLineWidth -= lastSpacePosLineWidth;
						lastSpacePos = i;
						lastSpacePosLineWidth = currentLineWidth;
					} else {
						//line += font.stringWidth(line) + "[" + currentLineWidth + "]";
						list.addLine( line, stringWidth );
						startPos =  ++i;
						currentLineWidth = 0;
						lastSpacePos = -1;
					}
				} else if ( lastSpacePos == -1) {
					/**/
					//System.out.println("value=" + value + ", i=" + i + ", startPos=" + startPos);
					String line = new String( valueChars, startPos, i - startPos );
					int stringWidth = font.stringWidth(line);
					list.addLine( line, stringWidth );
					startPos =  i;
					currentLineWidth = font.charWidth(valueChars[i]);
				} else {
					currentLineWidth -= lastSpacePosLineWidth;
					String line = new String( valueChars, startPos, lastSpacePos - startPos );
					//System.out.println("stringWidth=" + font.stringWidth(line) + ", lastSpacePosLineWidth=" + lastSpacePosLineWidth + " for [" + line + "]");
					//lastSpacePosLineWidth = font.stringWidth(line);
					//line += font.stringWidth(line) + "<" + lastSpacePosLength + ">";
					int stringWidth = font.stringWidth(line);
					list.addLine( line, stringWidth );
					startPos =  lastSpacePos + 1;
					lastSpacePos = -1;
				}

				firstLineWidth = lineWidth; 
			} else if (c == ' ' || c == '\t') {
				lastSpacePos = i;
				lastSpacePosLineWidth = currentLineWidth;
			}

		} 

		if(list.size() != maxLines)
		{
			// add tail:
			String tail = new String( valueChars, startPos, valueChars.length - startPos );
			currentLineWidth = font.stringWidth(tail);
			list.addLine( tail, currentLineWidth );
		}

		/*
		try {
		int widthPerChar = (completeWidth * 100) / valueChars.length;
		int startIndex = 0;
		while (true) {
			int index = (firstLineWidth * 100) / widthPerChar;
			int i = index + startIndex;
			if (i >= valueChars.length) {
				// reached the end of the given string:
				list.add( new String( valueChars, startIndex, valueChars.length - startIndex ) );
				break;
			}
			// find the last word gap:
			while (valueChars[i] != ' ') {
				i--;
				if (i < startIndex ) {
					// unable to find a gap:
					i = index + startIndex;
					break;
				}
			}
			//TODO rob maybe check whether the found string really fits into the line
			list.add( new String( valueChars, startIndex, i - startIndex ) );
			if (valueChars[i] == ' ' || valueChars[i] == '\t') {
				startIndex = i + 1;
			} else {				
				startIndex = i;
			}
			firstLineWidth = lineWidth;
		}
		} catch (ArithmeticException e) {
			System.out.println("unable to split: " + e);
			e.printStackTrace();
			System.out.println("complete width=" + completeWidth + " number of chars=" + value.length());
		}
		 */
	}
	//#endif

	//#if polish.midp || polish.usePolishGui

	/**
	 * Adds the specified appendix to the give line of text
	 * and shortens the text to fit the available width 
	 * @param font the font used
	 * @param availWidth the available width
	 * @param appendix the appendix to be added
	 */
	private static void addAppendix(Font font, int availWidth, String appendix, int position, int index, WrappedText result)
	{
		try
		{
			int appendixWidth = font.stringWidth(appendix);
			String line = result.getLine(index);
			int lineWidth = result.getLineWidth(index);
			int completeWidth = lineWidth +  appendixWidth;
			if(availWidth < appendixWidth)
			{
				line = appendix;
				completeWidth = appendixWidth;
				while(completeWidth > availWidth)
				{
					if(position == TextUtil.MAXLINES_APPENDIX_POSITION_AFTER) {
						line = line.substring(0,line.length() - 1);
					} else if(position == TextUtil.MAXLINES_APPENDIX_POSITION_BEFORE) {
						line = line.substring(1);
					}
					completeWidth = font.stringWidth(line);
				}
				result.setLine(index, line, completeWidth);
				return;
			}
			else
			{
				if(lineWidth > availWidth) {
					while(completeWidth > availWidth)
					{
						if(position == TextUtil.MAXLINES_APPENDIX_POSITION_AFTER) {
							line = line.substring(0,line.length() - 1);
						} else if(position == TextUtil.MAXLINES_APPENDIX_POSITION_BEFORE) {
							line = line.substring(1);
						}

						completeWidth = font.stringWidth(line) +  appendixWidth;
					}

					if(position == TextUtil.MAXLINES_APPENDIX_POSITION_AFTER) {
						result.setLine(index, line + appendix, completeWidth);
						return;
					} else {
						result.setLine(index, appendix + line, completeWidth);
						return;
					}
				} else {
					result.setLine(index, line, completeWidth);
				}
			}
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			//#debug error
			System.out.println("Unable to add max-lines-appendiz" + e);
			return;
		}
	}
	//#endif

	//	 Unreserved punctuation mark/symbols

	/**
	 * Converts Hex digit to a UTF-8 "Hex" character
	 * 
	 * @param digitValue digit to convert to Hex
	 * @return the converted Hex digit
	 */
	private static char toHexChar(int digitValue) {
		if (digitValue < 10)
			// Convert value 0-9 to char 0-9 hex char
			return (char)('0' + digitValue);
		else
			// Convert value 10-15 to A-F hex char
			return (char)('A' + (digitValue - 10));
	}

	/**
	 * Encodes a URL string.
	 * This method assumes UTF-8 usage.
	 * 
	 * @param url URL to encode
	 * @return the encoded URL
	 */
	public static String encodeUrl(String url) {
		StringBuffer encodedUrl = new StringBuffer(); // Encoded URL
		int len = url.length();
		// Encode each URL character
		for (int i = 0; i < len; i++) {
			char c = url.charAt(i); // Get next character
			if ((c >= '0' && c <= '9') ||
					(c >= 'a' && c <= 'z') ||
					(c >= 'A' && c <= 'Z')) {
				// Alphanumeric characters require no encoding, append as is
				encodedUrl.append(c);
			} else {
				int imark = UNRESERVED.indexOf(c);
				if (imark >=0) {
					// Unreserved punctuation marks and symbols require
					//  no encoding, append as is
					encodedUrl.append(c);
				} else {
					// Encode all other characters to Hex, using the format "%XX",
					//  where XX are the hex digits
					encodedUrl.append('%'); // Add % character
					// Encode the character's high-order nibble to Hex
					encodedUrl.append(toHexChar((c & 0xF0) >> 4));
					// Encode the character's low-order nibble to Hex
					encodedUrl.append(toHexChar (c & 0x0F));
				}
			}
		}
		return encodedUrl.toString(); // Return encoded URL
	}

	/**
	 * Encodes a string for parsing using an XML parser.
	 * &amp, &lt; and &gt; are replaced.
	 * 
	 * @param text the text
	 * @return the cleaned text
	 */
	public static String encodeForXmlParser( String text ) {
		int len = text.length();
		StringBuffer buffer = new StringBuffer(len + 10);
		for (int i = 0; i < len; i++) {
			char c = text.charAt(i);
			if (c == '&') {
				buffer.append("&amp;");
			} else if (c == '<') {
				buffer.append("&lt;");
			} else if (c == '>') {
				buffer.append("&gt;");
			} else {
				buffer.append(c);
			}
		}
		return buffer.toString();
	}

	/**
	 * Replaces the all matches within a String.
	 * 
	 * @param input the input string
	 * @param search the string that should be replaced
	 * @param replacement the replacement
	 * @return the input string where the search string has been replaced
	 * @throws NullPointerException when one of the specified strings is null
	 */
	public static String replace( String input, String search, String replacement ) {
		int pos = input.indexOf( search );
		if (pos != -1) {
			StringBuffer buffer = new StringBuffer();
			int lastPos = 0;
			do {
				buffer.append( input.substring( lastPos, pos ) )
				.append( replacement );
				lastPos = pos + search.length();
				pos = input.indexOf( search, lastPos );
			} while (pos != -1);
			buffer.append( input.substring( lastPos ));
			input = buffer.toString();
		}
		return input;
	}


	/**
	 * Replaces the first match in a String.
	 * 
	 * @param input the input string
	 * @param search the string that should be replaced
	 * @param replacement the replacement
	 * @return the input string where the first match of the search string has been replaced
	 * @throws NullPointerException when one of the specified strings is null
	 */
	public static String replaceFirst( String input, String search, String replacement ) {
		int pos = input.indexOf( search );
		if (pos != -1) {
			input = input.substring(0, pos) + replacement + input.substring( pos + search.length() );
		}
		return input;
	}

	/**
	 * Replaces the last match in a String.
	 * 
	 * @param input the input string
	 * @param search the string that should be replaced
	 * @param replacement the replacement
	 * @return the input string where the last match of the search string has been replaced
	 * @throws NullPointerException when one of the specified strings is null
	 */
	public static String replaceLast( String input, String search, String replacement ) {
		int pos = input.indexOf( search );
		if (pos != -1) {
			int lastPos = pos;
			while (true) {
				pos = input.indexOf( search, lastPos + 1 );
				if (pos == -1) {
					break;
				} else {
					lastPos = pos;
				}
			}
			input = input.substring(0, lastPos) + replacement + input.substring( lastPos + search.length() );
		}
		return input;
	}

	/**
	 * Retrieves the last index of the given match in the specified text.
	 * 
	 * @param text the text in which the match is given
	 * @param match the match within the text
	 * @return the last index of the match or -1 when the match is not found in the given text
	 * @throws NullPointerException when text or match is null
	 */
	public static int lastIndexOf(String text, String match) {
		int lastIndex = -1;
		int index = text.indexOf( match );
		while (index != -1) {
			lastIndex = index;
			index = text.indexOf( match, lastIndex + 1 );
		}
		return lastIndex;
	}


	/**
	 * Compares two strings in a case-insensitive way. Both strings are lower cased and
	 * then compared. If both are equal this method returns <code>true</code>,
	 * <code>false</code> otherwise.
	 *    
	 * @param str1 the string to compare
	 * @param str2 the string to compare to
	 * 
	 * @return <code>true</code> if both strings are equals except case,
	 * <code>false</code>
	 * 
	 * @throws NullPointerException if <code>str1</code> is <code>null</code>
	 * 
	 * @see String#equals(Object)
	 * @see String#equalsIgnoreCase(String)
	 */
	public static boolean equalsIgnoreCase(String str1, String str2)
	{
		//#if polish.cldc1.1
		//# return str1.equalsIgnoreCase(str2);
		//#else
		if (str2 == null || str1.length() != str2.length() )
		{
			return false;
		}
		return str1.toLowerCase().equals(str2.toLowerCase());
		//#endif
	}


	/**
	 * Reverses the given text while keeping English texts and numbers in the normal position.
	 * 
	 * @param input the text
	 * @return the reversed text
	 */
	public static String reverseForRtlLanguage(String input)
	{
		StringBuffer output = new StringBuffer( input.length() );
		StringBuffer ltrCharacters = new StringBuffer();
		boolean isCurrRTL = true;

		int size = input.length();
		for(int index = size - 1; index >= 0;)
		{
			while(isCurrRTL && index >= 0) // while we are in hebrew
			{
				char curr = input.charAt(index); 
				char nextChr = '\0';
				if(index > 0) {
					nextChr = input.charAt(index-1);
				} else {
					nextChr = curr;
				}

				if(isEnglishChar(curr) || isEnglishChar(nextChr))
				{
					isCurrRTL = false;
				}
				else
				{
					if(curr == '(')
					{
						output.append( ')' );
					}
					else if(curr == ')')
					{
						output.append( '(' );
					}
					else 
					{
						output.append( curr ); //left to right language - save the chars
					}

					index--;
				}

			}
			ltrCharacters.delete(0, ltrCharacters.length() );
			while(!isCurrRTL && index >= 0) // English....
			{
				char curr = input.charAt(index);
				char nextChr = '\0';
				if(index > 0) 
				{
					nextChr = input.charAt(index-1);
				}
				else 
				{
					nextChr = curr;
				}
				if(isEnglishChar(curr) || isEnglishChar(nextChr))
				{
					ltrCharacters.insert( 0, curr );
					index--;
				}
				else 
				{
					isCurrRTL = true;
				}
			}

			output.append( ltrCharacters );
		}
		return output.toString();
	}

	private static boolean isEnglishChar(char chr)
	{
		if ( chr < 128 && (chr >= 'a' && chr <= 'z' || chr >= 'A' && chr <= 'Z' || chr >= '0' && chr <= '9' || chr == '+' ) )
		{
			return true;
		}
		else 
		{
			return false;
		}
	}


	/**
	 * This method encodes a string to a quoted-printable string according to <a href="http://tools.ietf.org/html/rfc2045#section-6.7">RFC 2045</a>.
	 * All five rules are implemented.
	 * @param clearText the string to encode
	 * @param enc The encoding which should be used to interpret the cleartext.
	 * @return the encoded string
	 * @throws UnsupportedEncodingException if the given encoding is not supported
	 */
	public static String encodeAsQuotedPrintable(String clearText, String enc) throws UnsupportedEncodingException {
		StringBuffer buffer = new StringBuffer();

		int lastIndex = clearText.length()-1;
		int numberCharsInRow = 0;
		for(int i = 0; i <= lastIndex; i++) {
			char character = clearText.charAt(i);
			if(character == '=') {
				// Quote the quote character.
				numberCharsInRow = numberCharsInRow + encodeCharacterAsQP(buffer,character,enc);
			} else {
				if((33 <= character && character <= 60) || (62 <= character && character <= 126)) {
					// Rule 2. Literal representation
					buffer.append(character);
					numberCharsInRow = numberCharsInRow + 1;
				} else {
					// Rule 3. White Space
					if(character == 9 || character == 32) {
						if(i+2 <= lastIndex && clearText.charAt(i+1) == '\r' && clearText.charAt(i+2) == '\n') {
							// Encode whitespace at the end of line of the clear text.
							numberCharsInRow = numberCharsInRow + encodeCharacterAsQP(buffer,character,enc);
						} else {
							buffer.append(character);
							numberCharsInRow++;
						}
					} else {
						// Other non-printable character.
						// Rule 4. Line Breaks
						if(i+1 <= lastIndex && character == '\r' && clearText.charAt(i+1) == '\n') {
							buffer.append('\r');
						} else {
							// Rule 4. Line Breaks
							if(i-1>=0 && character == '\n' && clearText.charAt(i-1) == '\r') {
								buffer.append('\n');
								numberCharsInRow = 0;
							} else {
								// Rule 1. General 8bit representation
								numberCharsInRow = numberCharsInRow + encodeCharacterAsQP(buffer,character,enc);
							}
						}
					}
				}
			}
			// Rule 5. Soft Line Breaks
			if(numberCharsInRow > 76) {
				int lastIndexOfBuffer = buffer.length()-1;
				int numberOfCharsOverflowingLine = numberCharsInRow-76;
				int lastIndexOnLine = lastIndexOfBuffer-numberOfCharsOverflowingLine;
				buffer.insert(lastIndexOnLine,"=\r\n");
				// Add the one character that was the last on the previous line.
				numberCharsInRow = numberOfCharsOverflowingLine + 1;
			}
		}
		return buffer.toString();
	}

	private static int encodeCharacterAsQP(StringBuffer buffer, char character,String encoding) throws UnsupportedEncodingException {
		int numberCharactersGenerated = 0;
		//			byte[] bytes = Character.toString(character).getBytes(encoding);
		byte[] bytes = new String(new char[] {character}).getBytes(encoding);
		for (int i = 0; i < bytes.length; i++) {
			byte b = bytes[i];
			buffer.append("=");
			// Encode the byte as hex
			buffer.append(HEXES.charAt((b & 0xF0) >> 4));
			buffer.append(HEXES.charAt((b & 0x0F)));
			numberCharactersGenerated = numberCharactersGenerated + 3;
		}
		return numberCharactersGenerated;
	}
	
	/**
	 * Resolves the specified HTML entity such as "lt", "quot", "auml" and similar.
	 * Please note that right now no Greek nor mathematical symbols are supported.
	 * Compare http://www.w3.org/TR/WD-html40-970708/sgml/entities.html for known HTML entities.
	 * 
	 * @param name the name of the entity
	 * @return the corresponding character, '?' when the entity could not be resolved.
	 */
	public static char resolveNamedHtmlEntity( String name ) {
		int index = HTML_NAMED_ENTITIES.indexOf(name);
		if (index == -1) {
			return '?';
		}
		index += name.length();
		StringBuffer digits = new StringBuffer(5);
		char c = HTML_NAMED_ENTITIES.charAt(index);
		while (Character.isDigit( c )) {
			digits.append(c);
			index++;
			c = HTML_NAMED_ENTITIES.charAt(index);
		}
		try {
			int value = Integer.parseInt( digits.toString() );
			return (char)value;
		} catch (Exception e) {
			//#debug error
			System.out.println("Unable to resolve html entity " + name + e );
			return '?';
		}
	}

	/**
	 * Decodes text that contains HTML entities such as &amp;quot; or &amp;#62;.
	 * @param input the text input that might contain HTML entities
	 * @return the cleaned text. When an HTML entity could not be resolved, it will be replaced with a question mark '?'.
	 */
	public static String unescapeHtmlEntities(String input) {
		int length = input.length();
		StringBuffer output = new StringBuffer( length );
		StringBuffer entity = new StringBuffer(7);
		int start;
		for (int i=0; i<length; i++) {
			char c = input.charAt(i);
			if (c == '&') {
				// this could be an HTML entity:
				start = i;
				while ( (++i < length) && (i < start + 10) && ((c = input.charAt(i)) != ';')) {
					entity.append(c);
				}
				if (c == ';') {
					// okay, this was a named entity.
					c = entity.charAt(0);
					if (c == '#') {
						// this is a &#123; numerical entity
						entity.delete(0, 1);
						try {
							c = (char)Integer.parseInt(entity.toString());
						} catch (Exception e) {
							//#debug error
							System.out.println("Unable to parse HTML entity &#" + entity.toString() + "; " + e);
							c = '?';
						}
					} else {
						// this is a named HTML entity, e.g. &amp;
						c = resolveNamedHtmlEntity(entity.toString());
					}
				} else { // this is not an HTML entity:
					i = start;
					c = '&';
				}
				entity.delete(0, entity.length());
			}
			output.append(c);
		}
		return output.toString();
	}
}
