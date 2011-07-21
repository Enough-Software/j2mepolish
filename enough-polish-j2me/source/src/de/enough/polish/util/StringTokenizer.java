/*
 * Created on Mar 1, 2006 at 12:31:15 PM.
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
import java.util.NoSuchElementException;

/**
 * <p>Provides a simplified version of the J2SE StringTokenizer class for J2ME applications.</p>
 * <p><b>Note:</b>: This implementation behaves like the J2SE StringTokenizer, so
 * empty tokens are not counted and are skipped in the nextToken()/hasMoreTokens() methods.
 * If you need to obtain empty tokens as well, please use StringUtil.split(...) instead. 
 * </p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2009</p>
 * @author Robert Virkus, j2mepolish@enough.de
 * @see de.enough.polish.util.TextUtil#split(String, char)
 * @see de.enough.polish.util.TextUtil#split(String, char, int)
 */
public class StringTokenizer implements Enumeration {

	private String delimiters;
	private char[] inputChars;
	private String inputText;
	private int startPosition;
	private boolean hasMoreTokens;
	
	/**
	 * Creates a new StringTokenizer.
	 * 
	 * @param input the input, e.g. "one;two;three"
	 * @param delimiter the delimiter char, e.g. ';'
	 * @throws NullPointerException when the input is null
	 */
	public StringTokenizer( String input, char delimiter ) {
		super();
		reset(input, String.valueOf(delimiter));
	}
	
	/**
	 * Creates a new StringTokenizer.
	 * 
	 * @param input the input, e.g. "one;two;three"
	 * @param delimiters the delimiters, e.g. ";:\t\n"
	 * @throws NullPointerException when the input is null
	 */
	public StringTokenizer( String input, String delimiters ) {
		super();
		reset(input, delimiters);
	}
	
	/**
	 * Resets this StringTokenizer.
	 * 
	 * @param input the input, e.g. "one;two;three"
	 * @param delim the delimiter char, e.g. ';'
	 * @throws NullPointerException when the input is null
	 */
	public void reset( String input, char delim ) {
	  reset(input, String.valueOf(delim));
	}
	
	/**
	 * Resets this StringTokenizer.
	 * 
	 * @param input the input, e.g. "one;two;three"
   * @param delimiters the delimiters, e.g. ";:\t\n"
	 * @throws NullPointerException when the input is null
	 */
	public void reset( String input, String delimiters ) {
		this.inputText = input;
		this.delimiters = delimiters;
		this.inputChars = input.toCharArray();
		updateTokenStartPosition(input, this.inputChars, this.delimiters);
	}
	
	
	/**
	 * Updates the token start position.
	 * 
	 * @param input the input text
	 * @param chars the characters of the input text
	 * @param delimiters the delimiter characters
	 */
	protected void updateTokenStartPosition( String input, char[] chars, String delimiters ) {
		while ( this.startPosition < chars.length ) {
			char c = chars[ this.startPosition ];
			if (delimiters.indexOf(c) == -1) {
				this.hasMoreTokens =  true;
				return;
			}
			this.startPosition++;
		}
		this.hasMoreTokens =  false;
	}
	
	/**
	 * Determines whether there are more tokens left.
	 * 
	 * @return true when there are more tokens left.
	 */
	public boolean hasMoreTokens() {
		return this.hasMoreTokens;
	}
	
	/**
	 * Retrieves the next token.
	 * 
	 * @return the next token.
	 * @throws java.util.NoSuchElementException when there are no more elements.
	 */
	public String nextToken() {
		if (!this.hasMoreTokens) {
			throw new NoSuchElementException();
		}
		int position = this.startPosition + 1;
		int length = this.inputChars.length;
		while ( position < length && this.delimiters.indexOf(this.inputChars[ position ]) == -1 ) {
			position++;
		}
		// System.out.println("nextToken: startPosition=" + this.startPosition + ": [" + this.inputText.substring( this.startPosition ) + "]" + "\n");
		if (position >= length) {
			String value = this.inputText.substring( this.startPosition );
			this.startPosition = position++;
			updateTokenStartPosition(this.inputText, this.inputChars, this.delimiters);
			return value;
		} else {
			String value = this.inputText.substring( this.startPosition, position );
			this.startPosition = position++;
			updateTokenStartPosition(this.inputText, this.inputChars, this.delimiters);
			return value;
		} 
	}
	
	/**
	 * Retrieves the number of tokens which are left in this tokenizer.
	 * The current position is not modified.
	 * 
	 * @return the number of tokens which are left in this tokenizer.
	 */
	public int countTokens() {
		int position = this.startPosition;
		int length = this.inputChars.length;
		int number = 0;
		int lastPosition = position - 1;
		//System.out.println("countTokens: input=" + this.inputText + ", startPos=" + this.startPosition);
		char c = this.delimiters.charAt(0);
		while ( position < length  ) {
			c = this.inputChars[position];
			if ( this.delimiters.indexOf(c) > -1 ) {
				if ( position > lastPosition + 1) {
					//System.out.println("increase: position=" + position + ", lastPos=" + lastPosition );
					number++;
				}
				lastPosition = position;
			}
			position++;
		}
		if ( this.delimiters.indexOf(c) == -1 ) {
			//System.out.println("increase after loop: position=" + position + ", lastPos=" + lastPosition );
			number++;
		}
		//System.out.println("countTokens: result=" + number + ", lastChar=" + c + ", position=" + position + "\n");
		return number;
	}


	/* (non-Javadoc)
	 * @see java.util.Enumeration#hasMoreElements()
	 */
	public boolean hasMoreElements() {
		return hasMoreTokens();
	}

	/* (non-Javadoc)
	 * @see java.util.Enumeration#nextElement()
	 */
	public Object nextElement() {
		return nextToken();
	}

}
