/*
 * Created on 23-Jul-2004 at 12:56:03.
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

import de.enough.polish.BuildException;

/**
 * <p>Creates and stores abbreviations for any String based keys.</p>
 * <p>This is useful for storing only necessary character sequences
 * instead of full names, which saves some memory.
 * </p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        23-Jul-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class AbbreviationsGenerator {

	private int firstCharIndex = -1;
	private int secondCharIndex = -1;
	private int thirdCharIndex = -1;
	private int currentAbbreviationLength = 1;
	private Map abbreviations;
	private char[] abbreviationCharacters;
	
	/**
	 * A character array consisting of numbers and A-Z and a-z.
	 */
	public final static char[] ABBREVIATIONS_ALPHANUMERICAL = new char[]{
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
			'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
			'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'			
			 };

	/**
	 * A character array consisting of a-z.
	 */
	public final static char[] ABBREVIATIONS_ALPHABET_LOWERCASE = new char[]{
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
			'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'			
			 };

	/**
	 * A character array consisting of A-Z
	 */
	public final static char[] ABBREVIATIONS_ALPHABET_UPPERCASE = new char[]{
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
			'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
			 };

	
	/**
	 * Creates a new empty generator.
	 */
	public AbbreviationsGenerator() {
		this(new HashMap(), ABBREVIATIONS_ALPHANUMERICAL);
	}
	
	/**
	 * Creates a new generator.
	 * 
	 * @param abbreviations the known abbreviations
	 */
	public AbbreviationsGenerator( Map abbreviations ) {
		this(abbreviations, ABBREVIATIONS_ALPHANUMERICAL);
	}
	
	/**
	 * Creates a new generator.
	 * 
	 * @param abbreviations the known abbreviations
	 * @param abbreviationCharacters characters used for creating the abbreviations
	 */
	public AbbreviationsGenerator( Map abbreviations, char[] abbreviationCharacters ) {
		this.abbreviationCharacters = abbreviationCharacters;
		setAbbreviationsMap(abbreviations);
	}

	
	/**
	 * Retrieves the map containing all abbreviations.
	 *  
	 * @return the map containing all abbreviations.
	 */
	public Map getAbbreviationsMap() {
		return this.abbreviations;
	}
	
	/**
	 * Sets the abbreviations for this generator.
	 * 
	 * @param map a HashMap containing all abbreviations for full keywords 
	 */
	public void setAbbreviationsMap( Map map ) {
		this.abbreviations = map;
		this.firstCharIndex = -1;
		this.secondCharIndex = -1;
		this.thirdCharIndex = -1;
		if (map.size() == 0) {
			return;
		}
		// find out the last used abbreviation:
		String[] abbreviationsArr = (String[]) map.values().toArray( new String[ map.size() ]);
		String lastAbbreviation = getLastAbbreviation( abbreviationsArr );
		//System.out.println("last abbreviation=" + lastAbbreviation);
		this.currentAbbreviationLength = lastAbbreviation.length();
		char c = lastAbbreviation.charAt(0);
		this.firstCharIndex = getCharIndex( c, lastAbbreviation );
		if (this.currentAbbreviationLength > 1) {
			c = lastAbbreviation.charAt(1);
			this.secondCharIndex = getCharIndex( c, lastAbbreviation );
			if (this.currentAbbreviationLength > 2) {
				c = lastAbbreviation.charAt(2);
				this.thirdCharIndex = getCharIndex( c, lastAbbreviation );
			}
		}
	}

	/**
	 * Finds the last abbreviation within the given String array, e.g. is "ab" later than "z".
	 * 
	 * @param abbreviationsArr an array of already used abbreviations
	 * @return the last used abbreviation
	 */
	public static String getLastAbbreviation(String[] abbreviationsArr) {
		Arrays.sort( abbreviationsArr );
		int lastIndex = 0;
		int lastLength = 0;
		for (int i = abbreviationsArr.length - 1; i >= 0 ; i--) {
			String abbreviation = abbreviationsArr[i];
			if (abbreviation.length() > lastLength ) {
				lastLength = abbreviation.length();
				lastIndex = i;
			}
		}
		return abbreviationsArr[ lastIndex ];
	}

	/**
	 * Retrieves the index of the given character in the ABBREVIATION array.
	 * @param c the character
	 * @param abbreviation the abbreviation of the style
	 * @return the index in the ABBREVIATIONS array
	 */
	protected final int getCharIndex(char c, String abbreviation) {
		for (int i = 0; i < this.abbreviationCharacters.length; i++) {
			char d = this.abbreviationCharacters[i];
			if (d == c) {
				return i;
			}
		}
		throw new BuildException("Invalid abbreviation [" + abbreviation + "] - please try a clean rebuild (delete the temporary \"build\" folder).");
	}
	
	/**
	 * Retrieves an abbreviation for the given key.
	 * 
	 * @param name the full name of the key
	 * @param create true when a new abbreviation should be created when
	 *        none is found.
	 * @return either the abbreviation when it was found or when create is true,
	 *         or null when no abbreviation was found
	 */
	public String getAbbreviation( String name, boolean create ) {
		String abbreviation = (String) this.abbreviations.get( name );
		if (abbreviation == null && create) {
			abbreviation = getNextPropertyAbbreviation();
			this.abbreviations.put( name, abbreviation );
		}
		return abbreviation;
	}
	
	/**
	 * Creates the next property abbreviation
	 * 
	 * @return a new property abbreviation
	 */
	public String getNextPropertyAbbreviation() {
		if (this.firstCharIndex == -1) {
			this.firstCharIndex = 0;
			return "" + this.abbreviationCharacters[0];
		}
		if (this.currentAbbreviationLength == 1) {
			if (this.firstCharIndex < this.abbreviationCharacters.length -1) {
				this.firstCharIndex++;
				return "" + this.abbreviationCharacters[ this.firstCharIndex ];
			} else {
				this.firstCharIndex = 0;
				this.secondCharIndex = 0;
				this.currentAbbreviationLength = 2;
				return "" + this.abbreviationCharacters[ this.firstCharIndex ] + this.abbreviationCharacters[ this.secondCharIndex ];
			}
		}
		if (this.currentAbbreviationLength == 2) {
			if (this.secondCharIndex < this.abbreviationCharacters.length -1) {
				this.secondCharIndex++;
				return "" + this.abbreviationCharacters[ this.firstCharIndex ] + this.abbreviationCharacters[ this.secondCharIndex ];
			} else if (this.firstCharIndex < this.abbreviationCharacters.length -1) {
				this.firstCharIndex++;
				this.secondCharIndex = 0;
				return "" + this.abbreviationCharacters[ this.firstCharIndex ] + this.abbreviationCharacters[ this.secondCharIndex ];
			} else {
				this.firstCharIndex = 0;
				this.secondCharIndex = 0;
				this.thirdCharIndex = 0;
				this.currentAbbreviationLength = 3;
				return "" + this.abbreviationCharacters[ this.firstCharIndex ] + this.abbreviationCharacters[ this.secondCharIndex ] + this.abbreviationCharacters[ this.thirdCharIndex ];				
			}
		}
		// there are 3 characters available:
		if (this.thirdCharIndex < this.abbreviationCharacters.length -1) {
			this.thirdCharIndex++;
			return "" + this.abbreviationCharacters[ this.firstCharIndex ] + this.abbreviationCharacters[ this.secondCharIndex ] + this.abbreviationCharacters[ this.thirdCharIndex ];							
		} else if (this.secondCharIndex < this.abbreviationCharacters.length -1) {
			this.secondCharIndex++;
			this.thirdCharIndex = 0;
			return "" + this.abbreviationCharacters[ this.firstCharIndex ] + this.abbreviationCharacters[ this.secondCharIndex ] + this.abbreviationCharacters[ this.thirdCharIndex ];							
		} else if (this.firstCharIndex < this.abbreviationCharacters.length -1) {
			this.firstCharIndex++;
			this.secondCharIndex = 0;
			this.thirdCharIndex = 0;
			return "" + this.abbreviationCharacters[ this.firstCharIndex ] + this.abbreviationCharacters[ this.secondCharIndex ] + this.abbreviationCharacters[ this.thirdCharIndex ];							
		} else {
			// this will never happen in practice - there are more than 
			// 200.000 possible abbreviations available:
			throw new IllegalStateException("Too many abbreviations needed - only [" + this.abbreviations.size() + "] different abbreviations are supported." );
		}
	}
	
	public static void main( String[] args) throws FileNotFoundException, IOException {
		Map map = FileUtil.readPropertiesFile( new File( args[0]));
		AbbreviationsGenerator generator = new AbbreviationsGenerator( map, ABBREVIATIONS_ALPHABET_LOWERCASE);
		String abbreviation = generator.getAbbreviation("DuplicateUserException", true);
		System.out.println("Generated Abbreviation=" + abbreviation );
	}
	
}
