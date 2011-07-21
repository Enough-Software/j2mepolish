/*
 * Copyright (c) 2011 Robert Virkus / Enough Software
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
package de.enough.polish.json;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class parses JSON data encoded as a string and returns an equivalent native object tree.
 * @author Ovidiu Iliescu
 *
 */
public class JsonParser {
	
	//#if !polish.cldc1.1
	private static final Boolean TRUE = new Boolean(true);
	private static final Boolean FALSE = new Boolean(false);
	//#endif
	
	/**
	 * This class defines the default NULL object in JSON.
	 * @author Ovidiu Iliescu
	 *
	 */
	private static final class Null { 
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return "null";
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object object) {
            return object == null || object == this || object instanceof JsonParser.Null;
        }
	}
	
	// Token definitions
	protected static final char TOKEN_BEGIN_ARRAY = '[' ;	
	protected static final char TOKEN_BEGIN_OBJECT = '{' ;	
	protected static final char TOKEN_END_ARRAY = ']' ;	
	protected static final char TOKEN_END_OBJECT = '}' ;	
	protected static final char TOKEN_NAME_SEPARATOR = ':' ;	
	protected static final char TOKEN_VALUE_SEPARATOR = ',' ;	
	protected static final char TOKEN_WHITESPACE_SPACE = ' ';	
	protected static final char TOKEN_WHITESPACE_TAB = '\t' ;	
	protected static final char TOKEN_WHITSPACE_LINE_FEED = '\n' ;	
	protected static final char TOKEN_WHITESPACE_CARRIAGE_RETURN = '\r' ;	
	protected static final char TOKEN_ESCAPE_CHARACTER = '\\';	
	protected static final char TOKEN_QUOTATION_MARK = '\"' ;	
	protected static final char CHARACTER_BACKSPACE = '\b';	
	protected static final char CHARACTER_FORM_FEED = '\f';	
	protected static final char CHARACTER_TAB = '\t' ;	
	protected static final char CHARACTER_LINE_FEED = '\n' ;	
	protected static final char CHARACTER_CARRIAGE_RETURN = '\r' ;
	
	/**
	 * The JSON null object.
	 */
	public static final Object NULL = new Null();
	
	// JSON exception types
	protected static final int EXCEPTION_UNEXPECTED_END_OF_STREAM = 1;	
	protected static final int EXCEPTION_UNEXPECTED_TOKEN = 2;	
	protected static final int EXCEPTION_INVALID_ESCAPE_CHARACTER = 3;	
	protected static final int EXCEPTION_UNTERMINATED_STRING = 4;
	
	// JSON literals as integer arrays, for faster parsing
	protected static final int [] LITERAL_CODES_TRUE = new int[] { 't', 'r', 'u', 'e' };	
	protected static final int [] LITERAL_CODES_NULL = new int[] { 'n', 'u', 'l', 'l' };	
	protected static final int [] LITERAL_CODES_FALSE = new int[] { 'f', 'a', 'l', 's', 'e' };
	
	/**
	 * The current character in the underlying parser InputStream
	 */
	int currentChar;
	
	/**
	 * The underlying InputStream of the parser 
	 */
	InputStream stream;
	
	/**
	 * Convenience method for throwing a JSONException
	 * @param exceptionType the type of the exception
	 * @param extraInfo extra information about the exception
	 * @throws JsonException
	 */
	protected static void throwJsonException(int exceptionType, String extraInfo) throws JsonException {
		String exceptionText;
		
		switch ( exceptionType ) {
			case EXCEPTION_UNEXPECTED_END_OF_STREAM:
				exceptionText = "Unexpected end of this.stream encountered";
				if ( extraInfo != null ) {
					exceptionText += ": " + extraInfo;
				}
				throw new JsonException(exceptionText);
				
			case EXCEPTION_UNEXPECTED_TOKEN:
				throw new JsonException("Unexpected token encountered: " + extraInfo);
				
			case EXCEPTION_INVALID_ESCAPE_CHARACTER:
				throw new JsonException("Invalid escape character encountered: " + extraInfo);
				
			case EXCEPTION_UNTERMINATED_STRING:
				throw new JsonException("Unterminated string \"" + extraInfo + "...\"");
				
			default:
				exceptionText = "General exception encountered";
				if ( extraInfo != null ) {
					exceptionText += ": " + extraInfo;
				}
				throw new JsonException(exceptionText);
					
			
		}
	}	
	
	/**
	 * Skips whitespace characters and stops on the first non-whitespace character encountered. If the current
	 * character is non-whitespace, it stops on the current character.
	 * @throws IOException
	 * @throws JsonException
	 */
	protected void skipWhitespace() throws IOException, JsonException {
		// Read character tokens until a non-whitespace one is encountered
		while ( this.currentChar == TOKEN_WHITESPACE_SPACE || this.currentChar == TOKEN_WHITESPACE_TAB || this.currentChar == TOKEN_WHITSPACE_LINE_FEED || this.currentChar == TOKEN_WHITESPACE_CARRIAGE_RETURN ) {
			this.currentChar = this.stream.read();
		}	
		
		// Check for end of the stream and throw an error if found
		if ( this.currentChar == -1 ) {
			throwJsonException(EXCEPTION_UNEXPECTED_END_OF_STREAM, null);
		}
	}
	
		
	/**
	 * Parses JSON data from a String
	 * @param json the String containing the JSON data
	 * @return the native object hierarchy corresponding to the JSON data
	 * @throws IOException
	 * @throws JsonException
	 */
	public Object parseJson(String json) throws IOException, JsonException {
		ByteArrayInputStream byteArrayInputStream= new ByteArrayInputStream(json.getBytes());
		return parseJson(byteArrayInputStream);
	}
	
	/**
	 * Parses JSON data from an InputStream
	 * @param inputStream the InputStream containing the JSON data
	 * @return the native object hierarchy corresponding to the JSON data
	 * @throws IOException
	 * @throws JsonException
	 */
	public Object parseJson(InputStream inputStream) throws IOException, JsonException {
		this.stream = inputStream;
		this.currentChar = TOKEN_WHITESPACE_SPACE;
		skipWhitespace();
		return readEntity();
	}
	
	/**
	 * Reads a JSON object from the current source. The object is expected to be encoded according to standard JSON rules
	 * @return the corresponding JSONObject instance
	 * @throws IOException
	 * @throws JsonException
	 */
	protected JsonObject readJsonObject() throws IOException, JsonException {    	
		// Check if we really are at the start of a JSON object. Raise an exception otherwise
		if ( this.currentChar != TOKEN_BEGIN_OBJECT ) {
			throwJsonException(EXCEPTION_UNEXPECTED_TOKEN, "encountered '" + (char) this.currentChar + "' while expecting '" + TOKEN_BEGIN_OBJECT + "' when reading an object");
		}
		
		// Declare needed variables
    	JsonObject result = new JsonObject();
    	String memberName;
    	Object value;    	
		
		// Move inside the body of the object
		this.currentChar = this.stream.read();

		// Read the first character in the object's body and check if it's the end of object token, just in case the object is empty
		skipWhitespace();   		
		if ( this.currentChar == TOKEN_END_OBJECT) {			
			// Move outside the body of the object, to the next character in the stream, and return an empty object.
			this.currentChar = this.stream.read();			
			return result;
		}
    	
		// If the object is not empty, read its content
    	do {    		    		
    		
    		// First, make sure we have a string as a member name    		
    		if ( this.currentChar != TOKEN_QUOTATION_MARK ) {
    			StringBuffer currentObject= new StringBuffer();
    			result.serializeToStringBuffer(currentObject);
    			throwJsonException(EXCEPTION_UNEXPECTED_TOKEN, "'" + (char) this.currentChar + "' encountered while reading a member name of the object " + currentObject.toString());   			
    		}
    		
    		// Read the object's next member name
    		memberName  = readJsonString();
    		
    		// Read to the next character in the object body    		
    		skipWhitespace(); 
    		
    		// Make sure the next character is the name separator   		
    		if ( this.currentChar != TOKEN_NAME_SEPARATOR ) {
    			StringBuffer currentObject= new StringBuffer();
    			result.serializeToStringBuffer(currentObject);
    			throwJsonException(EXCEPTION_UNEXPECTED_TOKEN, "'" + (char) this.currentChar + "' encountered instead of name separator in the object " + currentObject.toString());   			
    		}
    		    		
    		// Read to the begining of the value
    		this.currentChar = this.stream.read();
    		skipWhitespace();
    		
    		// Read the value
    		value = readEntity();
    		
    		// Put the name-value pair in the object
    		result.put(memberName, value);
    		
    		// Read to the next character
    		skipWhitespace();
    		
    		if ( this.currentChar == TOKEN_END_OBJECT) {
    			// If we have reached the end of the object's body, move outside the body of the object
    			// to the next character in the stream, and return the object   			
    			this.currentChar = this.stream.read();
    			return result;
    		} else if ( this.currentChar == TOKEN_VALUE_SEPARATOR ) {
    			// If we have reached a value separator token, read until the beginning of the next member name
    			this.currentChar = this.stream.read();
    			skipWhitespace();
    		} else {
    			// If we have read something else, throw an unexpected token exception
    			StringBuffer currentArray = new StringBuffer();
    			result.serializeToStringBuffer(currentArray);
    			throwJsonException(EXCEPTION_UNEXPECTED_TOKEN, "'" + (String.valueOf((char) this.currentChar)) + "' encountered while reading array " + currentArray.toString());
    		}
    		   		    		
		} while (true);
    }
		
	/**
	 * Reads a JSON array from the current source. The array is expected to be encoded according to standard JSON rules
	 * @return the corresponding JSONArray instance
	 * @throws IOException
	 * @throws JsonException
	 */
    protected Object readJsonArray() throws IOException, JsonException {
    	
    	// Check if we really are at the start of a JSON array. Raise an exception otherwise
    	if ( this.currentChar != TOKEN_BEGIN_ARRAY ) {
			throwJsonException(EXCEPTION_UNEXPECTED_TOKEN, "encountered '" + (char) this.currentChar + "' while expecting '" + TOKEN_BEGIN_ARRAY + "' when reading an object");
		}
    	
    	// Declare the array variable
    	JsonArray result = new JsonArray();
    	
    	// Move inside the body of the array
    	this.currentChar = this.stream.read();
    	
		// Read the first character in the array and check if it's the end of array token, just in case the array is empty
		skipWhitespace();   		
		if ( this.currentChar == TOKEN_END_ARRAY) {			
			// Move outside the body of the array, to the next character in the stream, and return an empty array    			
			this.currentChar = this.stream.read();			
			return result;
		}
    	
		// If the array is not empty, read its content
    	do {    		    		
    		// Read an array element
    		result.put ( readEntity() );   
    		
    		// Read the next character in the array body    		
    		skipWhitespace(); 
    		
    		if ( this.currentChar == TOKEN_END_ARRAY) {    			
    			// If we have reached the end of the array's body, move outside the body of the array
    			// to the next character in the stream and return the array.    			
    			this.currentChar = this.stream.read();
    			return result;
    		} else if ( this.currentChar == TOKEN_VALUE_SEPARATOR ) {
    			// If we have reached a value separator token, read until the beginning of the next value
    			this.currentChar = this.stream.read();
    			skipWhitespace();
    		} else {
    			// If we have read something else, throw an unexpected token exception
    			StringBuffer currentArray = new StringBuffer();
    			result.serializeToStringBuffer(currentArray);
    			throwJsonException(EXCEPTION_UNEXPECTED_TOKEN, (String.valueOf((char) this.currentChar)) + " encountered while reading array " + currentArray.toString());
    		}
    		   		    		
		} while (true);
    }
    
    /**
     * Reads either an JSON array, object, string, number or literal from the current source, according to
     * the value of the current character in the source (e.g. '{' means that an object will be read, '[' means
     * that an array will be read, etc)
     * @return the read entity
     * @throws IOException
     * @throws JsonException
     */
    protected Object readEntity() throws IOException, JsonException {
    	switch (this.currentChar){
    		case TOKEN_BEGIN_ARRAY:
    			return readJsonArray();
    			
    		case TOKEN_BEGIN_OBJECT:
    			return readJsonObject();
    			
    		case TOKEN_QUOTATION_MARK:
    			return readJsonString();
    			
    		case '-':
    		case '0':
    		case '1':
    		case '2':
    		case '3':
    		case '4':
    		case '5':
    		case '6':
    		case '7':
    		case '8':
    		case '9':
    			return readJsonNumber();    
    			
    		case 't':
    		case 'f':
    		case 'n':
    			return readJsonLiteral();
    			
			default:
				throwJsonException(EXCEPTION_UNEXPECTED_TOKEN, (String.valueOf((char) this.currentChar)) + " encountered while trying to read a value");		    		
    	}
    	return null;
    }
    
    /**
     * Reads a JSON literal from the current source.
     * @return the corresponding value. Returns either Boolean.FALSE, Boolean.TRUE or JSONParser.NULL.
     * @throws JsonException
     * @throws IOException
     */
    protected Object readJsonLiteral() throws JsonException, IOException {
    	    	
    	// Keep a backup of the original character
    	int originalCharacter = this.currentChar ;
    	
    	// Find out which LITERAL_CODES array to use
    	int [] arrayToUse = null;
    	switch ( this.currentChar ) 	{
    		case 't':
    			arrayToUse = LITERAL_CODES_TRUE;
    			break;
    		case 'n':
    			arrayToUse = LITERAL_CODES_NULL;
    			break;
    			
    		case 'f':
    			arrayToUse = LITERAL_CODES_FALSE;
    			break;
    			
			default:
				throwJsonException(EXCEPTION_UNEXPECTED_TOKEN, "expected literal, but no JSON literal can start with the letter '" + (char) this.currentChar + "'");    			
    		
    	}
    	
    	// See if the characters in the stream match their corresponding literal characters
    	int size = arrayToUse.length;
    	int currentIndex = 0;
    	while (currentIndex<size) {
    		
    		if ( this.currentChar == arrayToUse[currentIndex] ) {
    			// The expected character has been found. Proceed to the next.
    			currentIndex++;
    		} else if ( this.currentChar == -1) {
	    		// End of the stream found, throw an error
    			throwJsonException(EXCEPTION_UNEXPECTED_END_OF_STREAM, " end of stream encountered while trying to read literal" );
    		} else {
    			// Invalid character found
    			throwJsonException(EXCEPTION_UNEXPECTED_TOKEN, "found unexpected character '" + (char) this.currentChar + "' in literal" );
    		}    		

    		// Read the next character from the stream
    		this.currentChar = this.stream.read();
    	}
    
    	// Everything went OK. Return the corresponding object
    	switch ( originalCharacter ) {
    		case 't':
    			//#if polish.cldc1.1
    				//# return Boolean.TRUE;
				//#else
    				return TRUE;
				//#endif
    				
    		case 'f':
    			//#if polish.cldc1.1
					//# return Boolean.FALSE;
				//#else
					return FALSE;
				//#endif
    			
    		case 'n':
    			return JsonParser.NULL;
    			
			default:
				return null;
    	}
    }
    
    /**
     * Reads a JSON number from the current source
     * @return a corresponding JsonNumber instance
     * @throws IOException
     * @throws JsonException
     */
    protected JsonNumber readJsonNumber() throws IOException, JsonException {
    	
    	// Declare the needed variables
    	boolean integerPartFound = false;
    	boolean decimalPointFound = false;
    	boolean exponentSignFound = false;    	
    	StringBuffer result = new StringBuffer();
    	
		// Handle the initial minus sign (if any)
    	if ( this.currentChar == '-' ) {
    		result.append('-');
    		this.currentChar = this.stream.read();
    	}		
		
    	// Handle the rest of the number
    	while ( true ) {
    		
    		// Check if the current character is valid in the context of the current number
    		switch (this.currentChar) {    		
			
				// Digits are always valid. Make sure we mark that the integer part of the number has been found. Event if the
				// digit is part of the fractional part of the number, it still means an integer part came before it.
				case '0':
	    		case '1':
	    		case '2':
	    		case '3':
	    		case '4':
	    		case '5':
	    		case '6':
	    		case '7':
	    		case '8':
	    		case '9':
	    			integerPartFound = true;
	    			break;
	    			
	    		// If a decimal point is encountered, check if this is the first decimal point encountered 
	    		// and if we have an integer part before the decimal point.
    			case '.':
    				if ( decimalPointFound ) {
    					throwJsonException(EXCEPTION_UNEXPECTED_TOKEN, "extra decimal point found in number" + result.toString());
    				} else if (! integerPartFound) {
    					throwJsonException(EXCEPTION_UNEXPECTED_TOKEN, "decimal point found before integer part in number" + result.toString());
    				}
    				decimalPointFound = true;
    				break;
    				

    			// If an exponent sign is encountered, check if this is the first exponent sign found
    			// and if we have an integer part before it	
    			case 'E':
    			case 'e':
    				if ( exponentSignFound ) {
    					throwJsonException(EXCEPTION_UNEXPECTED_TOKEN, "extra exponent sign found in number" + result.toString());
    				} else if (! integerPartFound) {
    					throwJsonException(EXCEPTION_UNEXPECTED_TOKEN, "exponent sign found before integer part in number" + result.toString());
    				}
    				exponentSignFound = true;
    				break;
    				
    			// If we have reached the end of this.stream, throw an error
	    		case -1:
	    			throwJsonException(EXCEPTION_UNEXPECTED_END_OF_STREAM, "while trying to read number beginning with \"" + result.toString() + "...\"");

				// If any other character is encountered, the number has been fully read. Return it.	
    			default:
    				return new JsonNumber(result.toString());
    		}
    		
    		// If the character is a valid character in the context of the current number, add it to the buffer
    		result.append((char) this.currentChar);
    		
    		// Go to the next character
    		this.currentChar = this.stream.read();
    	}
    }
    
    /**
     * Reads a JSON string from the current source
     * @return the string
     * @throws IOException
     * @throws JsonException
     */
    protected String readJsonString() throws IOException, JsonException {

    	// Check if we really are at the beginning of a string. Raise an exception otherwise
    	if ( this.currentChar != TOKEN_QUOTATION_MARK ) {
			throwJsonException(EXCEPTION_UNEXPECTED_TOKEN, "encountered '" + (char) this.currentChar + "' while expecting '" + TOKEN_QUOTATION_MARK + "' when reading an object");
		}
    	
    	// Declare the needed variables
    	int unicodeIndex;
    	StringBuffer buffer = new StringBuffer();
    	StringBuffer unicodeBuffer = new StringBuffer(4);
    	
    	// Move into the body of the string
    	this.currentChar = this.stream.read();
    	
    	do {    		
    		
    		// Process the current character in the string's body
    		switch (this.currentChar) {
    		
    			// If we have reached the end of this.stream, throw an error
	    		case -1:
	    			throwJsonException(EXCEPTION_UNEXPECTED_END_OF_STREAM, "while trying to read string beginning with \"" + buffer.toString() + "...\"");
	    			
	    		// End of string has been reached. Move outside the body of the string and return the result
	    		case TOKEN_QUOTATION_MARK:
	    			this.currentChar = this.stream.read();
	    			return buffer.toString();    			    			
	    			
	    		// Decode an escape character
	    		case TOKEN_ESCAPE_CHARACTER:
	    			this.currentChar = this.stream.read();
	    			switch (this.currentChar) {
	    				case  't':
	    					buffer.append(CHARACTER_TAB);
	    					break;
	    					
	    				case 'n':
	    					buffer.append(CHARACTER_LINE_FEED);
	    					break;
	    					
	    				case 'r':
	    					buffer.append(CHARACTER_CARRIAGE_RETURN);
	    					break;
	    					
	    				case 'f':
	    					buffer.append(CHARACTER_FORM_FEED);
	    					break;
	    					
	    				case 'b':
	    					buffer.append(CHARACTER_BACKSPACE);
	    					break;
	    					
	    				case '\"':
	    				case '\\':
	    				case '/':
	    					buffer.append((char)this.currentChar);
	    					break;
	    					
	    				case 'u':
	    					// Decode an unicode character
	    					
	    					// Reset the buffer and unicode counter
	    					unicodeBuffer.delete(0,4);
	    					unicodeIndex = 0;
	    					
	    					// First, get the four digits into the unicode string buffer.
	    					while (unicodeIndex < 4) {
	    						this.currentChar = this.stream.read();
	    						
	    						if ( this.currentChar == -1 ) {
	    							throwJsonException(EXCEPTION_UNEXPECTED_END_OF_STREAM, "while trying to read next unicode character in string \"" + buffer.toString() + "...\"");
	    						}
	    						
    							unicodeBuffer.append((char) this.currentChar);	    						
	    						unicodeIndex++;
	    					}
	    					
	    					// Then decode, the buffer as a character
	    					buffer.append((char) Integer.parseInt(unicodeBuffer.toString(), 16));
	    					break;
	    					
    					default:
    						throwJsonException(EXCEPTION_INVALID_ESCAPE_CHARACTER, "'" + (char) this.currentChar + "' found in string \"" + buffer.toString() + "...\"");
    						break;	    						
	    			}
	    			break;
	    			
	    		case TOKEN_WHITSPACE_LINE_FEED:
	    		case TOKEN_WHITESPACE_CARRIAGE_RETURN:
	    			throwJsonException(EXCEPTION_UNTERMINATED_STRING, buffer.toString());
	    			
    			default:
    				buffer.append((char) this.currentChar);    				    			
    		}
    		
    		// Read the next char in the string
    		this.currentChar = this.stream.read();
    		
    	} while (true);
    }

}
