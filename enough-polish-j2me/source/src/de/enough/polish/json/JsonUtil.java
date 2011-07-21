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

/**
 * This class provides various JSON-related utility methods
 * @author Ovidiu Iliescu
 *
 */
public class JsonUtil {

	/**
	 * Escapes a string according to JSON rules.
	 * @param value the string to escape
	 * @return the escaped string
	 */
	public static final String escapeToJson(String value) {
		StringBuffer result = new StringBuffer();
		escapeToJson(value,result);
		return result.toString();
	}
	
	/**
	 * Escapes a string according to JSON rules.
	 * @param value the string to escape
	 * @param stringBuffer the StringBuffer to escape to
	 */
	public static final void escapeToJson(String value, StringBuffer stringBuffer) {
		int index;
		int length = value.length();
		char c;
		String unicodeBuffer;
		for (index = 0; index < length; index++ ) {
            c = value.charAt(index);
            switch (c) {
            case JsonParser.TOKEN_ESCAPE_CHARACTER:
            case JsonParser.TOKEN_QUOTATION_MARK:
            case '/':
                stringBuffer.append('\\');
                stringBuffer.append(c);
                break;
            case JsonParser.CHARACTER_BACKSPACE:
                stringBuffer.append("\\b");
                break;
            case JsonParser.CHARACTER_TAB:
                stringBuffer.append("\\t");
                break;
            case JsonParser.CHARACTER_LINE_FEED:
                stringBuffer.append("\\n");
                break;
            case JsonParser.CHARACTER_FORM_FEED:
                stringBuffer.append("\\f");
                break;
            case JsonParser.CHARACTER_CARRIAGE_RETURN:
                stringBuffer.append("\\r");
                break;
            default:
                if (c < ' ' || (c >= '\u0080' && c < '\u00a0') ||
                               (c >= '\u2000' && c < '\u2100')) {
                    unicodeBuffer = "000" + Integer.toHexString(c);
                    stringBuffer.append("\\u" + unicodeBuffer.substring(unicodeBuffer.length() - 4));
                } else {
                    stringBuffer.append(c);
                }
            }
        }
	}
	
	/**
	 * Returns a JSON-compatible representation of the given object. If the object is not a standard JSON entity type
	 * (Boolean, String, Null, JSONObject, JSONArray) then the object will be converted to a String using toString()
	 * @param object the object to represent
	 * @return the JSON string representation of the object
	 */
	public static final String toJsonValue(Object object) {
		StringBuffer buffer = new StringBuffer();
		toJsonValue(object, buffer);
		return buffer.toString();
	}
	
	/**
	 * Returns a JSON-compatible representation of the given object. If the object is not a standard JSON entity type
	 * (Boolean, String, Null, JSONObject, JSONArray) then the object will be converted to a String using toString()
	 * @param object the object to represent
	 * @param stringBuffer the StringBuffer to write the representation to
	 */
	public static final void toJsonValue(Object object, StringBuffer stringBuffer) {
		
		if ( object instanceof JsonItem ) {
			// JSONItems can serialize themselves to JSON-compatible strings
			((JsonItem) object).serializeToStringBuffer(stringBuffer);
		} else {
			// Native object types must be serialized manually. Most of them must be quoted, i.e. placed between quotes.
			// Unquoted data types are boolean, integer, double, float and JSONParser.NULL. Everything else will be converted to String and quoted.
			boolean needsQuotes = ! (object instanceof Integer || object instanceof Long
					//#if polish.hasFloatingPoint
						|| object instanceof Double || object instanceof Float
					//#endif
						|| object instanceof Boolean || object == JsonParser.NULL );
			if ( needsQuotes ) 
			{
				stringBuffer.append(JsonParser.TOKEN_QUOTATION_MARK);
				escapeToJson(object.toString(),stringBuffer);
				stringBuffer.append(JsonParser.TOKEN_QUOTATION_MARK);
			} else {
				stringBuffer.append(object.toString());
			}
		}
	}

}
