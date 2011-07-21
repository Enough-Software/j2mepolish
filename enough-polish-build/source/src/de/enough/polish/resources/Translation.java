/*
 * Created on 12-Sep-2004 at 13:10:54.
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
package de.enough.polish.resources;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.enough.polish.util.IntegerIdGenerator;

/**
 * <p>Represents a translation.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        12-Sep-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class Translation {
	
	/** a plain translation contains only text and no dynamic variables */
	public static final int PLAIN = 0; 
	/** a translation containing only one parameter */
	public static final int SINGLE_PARAMETER = 1;
	/** a translation containing several parameter, these must be combined in Locale.java */
	public static final int MULTIPLE_PARAMETERS = 2;
	
	private final static String PARAMETER_PATTERN_STR = "\\{\\d+\\}";
	private final static Pattern PARAMETER_PATTERN = Pattern.compile( PARAMETER_PATTERN_STR );
	
	private final String key;
	private String value;
	private int type;
	private int parameterIndex;
	private String oneValueStart;
	private String oneValueEnd;
	private int[] parameterIndices;
	private String[] valueChunks;
	private int id = -1;

	/**
	 * Creates a new translation.
	 * 
	 * @param key the string key for the translation, e.g. "text.Greetings"
	 * @param value the value of the translation including parameters, e.g. "Hey {0}!"
	 * @param useDynamicTranslations true when dynamic translations are used (translations that can be changed during the runtime)
	 * @param idGeneratorPlain the ID generator for translations with no parameters, is only used when dynamic translations are used
	 * @param idGeneratorSingleParameters the ID generator for translations with one parameter, is only used when dynamic translations are used
	 * @param idGeneratorMultipleParameters the ID generator for translations with multiple parameters
	 */
	public Translation(String key, String value, boolean useDynamicTranslations, 
			IntegerIdGenerator idGeneratorPlain, 
			IntegerIdGenerator idGeneratorSingleParameters, 
			IntegerIdGenerator idGeneratorMultipleParameters) 
	{
		this.key = key;
		setValue( value );
		if (useDynamicTranslations) {
			switch (this.type) {
			case SINGLE_PARAMETER:
				this.id = idGeneratorSingleParameters.getId(key, true);
				break;
			case MULTIPLE_PARAMETERS:
				this.id = idGeneratorMultipleParameters.getId(key, true);
				break;
			default:
				this.id = idGeneratorPlain.getId(key, true);
			}
		} else if (this.type == MULTIPLE_PARAMETERS) {
			this.id = idGeneratorMultipleParameters.getId(key, true);			
		}

//		if (key.startsWith("polish.") ) {
//			System.out.println("NEW TRANSLATION: " + key + " => ID = " + this.id +", isDynamic=" + useDynamicTranslations);
//		}
	}

	/**
	 * Creates a new plain translation.
	 * @param key the key
	 * @param value the translation
	 * @param id the integer ID
	 */
	public Translation(String key, String value, int id)
	{
		this.key = key;
		this.value = value;
		this.id = id;
		this.type = PLAIN;
	}
	

	/**
	 * Creates a new single param translation.
	 * @param key the key
	 * @param start the first part of the translation
	 * @param end the last part of the translation
	 * @param id the integer ID
	 */
	public Translation(String key, String start, String end, int id)
	{
		this.key = key;
		this.oneValueStart = start;
		this.oneValueEnd = end;
		this.id = id;
		this.type = SINGLE_PARAMETER;
	}
	

	/**
	 * Creates a new multiple param translation.
	 * @param key the key
	 * @param chunks the value chunks 
	 * @param indeces  the indices of the parameters
	 * @param id the integer ID
	 */
	public Translation(String key, String[] chunks, int[] indeces, int id)
	{
		this.key = key;
		this.valueChunks = chunks;
		this.parameterIndices = indeces;
		this.id = id;
		this.type = MULTIPLE_PARAMETERS;
	}

	/**
	 * @return Returns the id.
	 */
	public int getId() {
		return this.id;
	}
	/**
	 * @return Returns the key.
	 */
	public String getKey() {
		return this.key;
	}
	/**
	 * Retrieves the index of the referenced parameter.
	 * This method can only be used when the Translation-type is SINGLE_PARAMETER
	 * 
	 * @return Returns the index of the referenced parameter.
	 */
	public int getParameterIndex() {
		return this.parameterIndex;
	}
	/**
	 * @return Returns the type.
	 */
	public int getType() {
		return this.type;
	}
	/**
	 * @return Returns the value.
	 */
	public String getValue() {
		return this.value;
	}
	/**
	 * Retrieves the indices of the parameters.
	 * This method can be called only when the type of this
	 * translation is MULTIPLE_PARAMETERS.
	 * 
	 * @return Returns the indices of the parameters.
	 */
	public int[] getParameterIndices() {
		return this.parameterIndices;
	}
	/**
	 * @return Returns the oneValueEnd.
	 */
	public String getOneValueEnd() {
		return this.oneValueEnd;
	}
	/**
	 * @return Returns the oneValueStart.
	 */
	public String getOneValueStart() {
		return this.oneValueStart;
	}
	/**
	 * @return Returns the valueChunks.
	 */
	public String[] getValueChunks() {
		return this.valueChunks;
	}

	/**
	 * @return true when this value has no parameters at all.
	 */
	public boolean isPlain() {
		return this.type == PLAIN;
	}
	
	public boolean hasOneParameter() {
		return this.type == SINGLE_PARAMETER;
	}
	
	public boolean hasSeveralParameters() {
		return this.type == MULTIPLE_PARAMETERS;
	}

	/**
	 * Retrieves the translation with quotation marks,
	 * 
	 * @return this value with quotation marks.
	 */
	public String getQuotedValue() {
		return '"' + this.value + '"';
	}

	/**
	 * Retrieves the complete value for a translation with one parameter.
	 * 
	 * @param parameter the parameter
	 * @return the value of the translation including necessary quotation marks.
	 */
	public String getQuotedValue(String parameter) {
		StringBuffer result = new StringBuffer();
		result.append('"')
			.append( this.oneValueStart )
			.append("\" + ");
		if (this.parameterIndex == 0) {
			// assume that only a single parameter is given:
			result.append( parameter );
		} else {
			// assume that a parameter array is given:
			result.append( parameter )
				.append('[')
				.append( this.parameterIndex )
				.append(']');
		}
		result.append(" + \"")
			.append( this.oneValueEnd )
			.append( '"' );
		return result.toString();
	}

	/**
	 * Sets the ID programmatically.
	 * 
	 * @param id the new ID
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Sets a new value for an existing translation
	 * @param value
	 */
	public void setValue(String value)
	{
		this.value = value;
		// split the value along the "{0}" etc patterns:
		Matcher matcher = PARAMETER_PATTERN.matcher( value );
		int numberOfParameters = 0;
		while (matcher.find()) {
			numberOfParameters++;
		}
		matcher.reset();
		//System.out.println("value: [" + value + "]  number of references: " + numberOfParameters );
		if (numberOfParameters == 0) {
			this.type = PLAIN;
		} else if (numberOfParameters == 1) {
			this.type = SINGLE_PARAMETER;
			matcher.find();
			String param = matcher.group();
			this.parameterIndex = Integer.parseInt( param.substring(1 , param.length()-1) );
			this.oneValueStart = value.substring( 0, matcher.start() );
			this.oneValueEnd = value.substring( matcher.end() );
		} else {
			this.type = MULTIPLE_PARAMETERS;
			this.parameterIndices = new int[ numberOfParameters ];
			this.valueChunks = new String[ numberOfParameters + 1 ];
			int i = 0;
			int lastEnd = 0;
			while (matcher.find()) {
				String param = matcher.group();
				this.parameterIndices[i] = Integer.parseInt( param.substring(1 , param.length()-1) );
				this.valueChunks[i] = value.substring( lastEnd, matcher.start() );
				lastEnd = matcher.end();
				i++;
			}
			this.valueChunks[i] = value.substring( lastEnd );
		}	}

}
