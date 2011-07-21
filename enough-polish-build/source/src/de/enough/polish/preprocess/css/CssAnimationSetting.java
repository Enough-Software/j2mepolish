/*
 * Created on Jun 14, 2008 at 12:10:10 PM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package de.enough.polish.preprocess.css;

import java.util.HashMap;
import java.util.Map;

import de.enough.polish.BuildException;
import de.enough.polish.util.StringUtil;

/**
 * <p>Stores animation CSS attributes</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CssAnimationSetting
{
	
	private static final Map TIMING_FUNCTION_BY_NAME;
	static {
		TIMING_FUNCTION_BY_NAME = new HashMap();
		TIMING_FUNCTION_BY_NAME.put("ease", "CssAnimation.FUNCTION_EASE");
		TIMING_FUNCTION_BY_NAME.put("ease-out", "CssAnimation.FUNCTION_EASE_OUT");
		TIMING_FUNCTION_BY_NAME.put("ease-in", "CssAnimation.FUNCTION_EASE_IN");
		TIMING_FUNCTION_BY_NAME.put("ease-in-out", "CssAnimation.FUNCTION_EASE");
		TIMING_FUNCTION_BY_NAME.put("ease-out-in", "CssAnimation.FUNCTION_EASE");
		TIMING_FUNCTION_BY_NAME.put("linear", "CssAnimation.FUNCTION_LINEAR");
		TIMING_FUNCTION_BY_NAME.put("CssAnimation.FUNCTION_EASE", "CssAnimation.FUNCTION_EASE");
		TIMING_FUNCTION_BY_NAME.put("CssAnimation.FUNCTION_EASE_OUT", "CssAnimation.FUNCTION_EASE_OUT");
		TIMING_FUNCTION_BY_NAME.put("CssAnimation.FUNCTION_EASE_IN", "CssAnimation.FUNCTION_EASE_IN");
		TIMING_FUNCTION_BY_NAME.put("CssAnimation.FUNCTION_LINEAR", "CssAnimation.FUNCTION_LINEAR");
	}
	
	private String cssAttributeName;
	private final Map animationAttributesByName;
	private CssAnimationRange cssAnimationRange;
	
	public CssAnimationSetting( String cssAttributeName ) {
		this.cssAttributeName = cssAttributeName;
		this.animationAttributesByName = new HashMap();
	}
	
	/**
	 * Creates a new CSS animation setting
	 * @param block the base CssDeclarationBlock 
	 */
	public CssAnimationSetting(CssDeclarationBlock block)
	{
		this.cssAttributeName = block.getBlockName().substring(0, block.getBlockName().length() - "-animation".length());
		this.animationAttributesByName = new HashMap();
		String[] attributes = block.getAttributes();
		for (int i = 0; i < attributes.length; i++)
		{
			String attribute = attributes[i];
			String value = block.getValue(i);
			this.animationAttributesByName.put(attribute, value);
		}
	}

	public void addAnimationSetting( String name, String value ) {
		this.animationAttributesByName.put(name, value);
	}
	
	public String getValue( String name ) {
		return (String) this.animationAttributesByName.get( name );
	}

	/**
	 * @return the css attribute name
	 */
	public String getCssAttributeName()
	{
		return this.cssAttributeName;
	}

	public String[] getKeys() {
		return (String[]) this.animationAttributesByName.keySet().toArray( new String[ this.animationAttributesByName.size() ]);
	}

	public String getOn()
	{
		String on = getValue("on");
		if (on == null) {
			on = "focused";
		}
		return on;
	}
	
	
	/**
	 * @param durationStr
	 * @return the time in milliseconds
	 */
	private long parseTime(String timeStr, int subsettingIndex)
	{
		String[] times = StringUtil.splitAndTrim(timeStr, ',');
		if (subsettingIndex != -1) {
			if (subsettingIndex >= times.length) {
				return -1;
			}
			timeStr = times[subsettingIndex];
			return parseTime( timeStr );
		} else {
			long completeTime = 0;
			for (int i = 0; i < times.length; i++)
			{
				timeStr = times[i];
				completeTime += parseTime( timeStr );
			}
			return completeTime;
		}
	}
	
	/**
	 * @param durationStr
	 * @return the time in milliseconds
	 */
	private long parseTime(String timeStr)
	{
		int factor = 1;
		if (timeStr.endsWith( "ms")) {
			timeStr = timeStr.substring(0, timeStr.length() - 2).trim();
		} else if (timeStr.endsWith("s")) {
			timeStr = timeStr.substring(0, timeStr.length() - 1).trim();
			factor = 1000;
		}
		long value = Long.parseLong(timeStr) * factor;
		return value;
	}

	/**
	 * @return the duration in milliseconds
	 */
	public long getDuration()
	{
		return getDuration(-1, 1);
	}
	
	/**
	 * @return the duration in milliseconds
	 */
	public long getDuration(int subsettingIndex, int numberOfSettings )
	{
		String durationStr = getValue("duration");
		if (durationStr == null) {
			return 1000L;
		}
		long result = parseTime( durationStr, subsettingIndex );
		if (result == -1 && subsettingIndex != 0) {
			// use the same time slice for each animation:
			return getDuration() / numberOfSettings;
		}
		return result;
	}
	

	/**
	 * @return the delay in milliseconds
	 */
	public long getDelay()
	{
		return getDelay(0, 1);
	}

	/**
	 * @return the delay in milliseconds
	 */
	public long getDelay(int subsettingIndex, int numberOfSettings )
	{
		String delayStr = getValue("delay");
		if (delayStr == null) {
			return 0L;
		}
		long result = parseTime( delayStr, subsettingIndex );
		if (result == -1 && subsettingIndex != 0) {
			// by default there is no additional delay:
			return 0;
		}
		return result;
	}

	/**
	 * @return the css function
	 */
	public String getFunction()
	{
		return getFunction(0);
	}
	
	/**
	 * @return the css function
	 */
	public String getFunction(int subsettingIndex)
	{
		String function = getValue("function");
		if (function == null) {
			return "CssAnimation.FUNCTION_EASE";
		}
		String[] functions = StringUtil.splitAndTrim(function, ',' );
		if (functions.length > subsettingIndex) {
			function = functions[subsettingIndex];
		}
		String realFunction = (String) TIMING_FUNCTION_BY_NAME.get(function);
		if (realFunction == null) {
			throw new BuildException("Unknown \"function\" \"" + function + "\" in CSS animation - check your polish.css for CSS animation " + getCssAttributeName() );
		}
		return realFunction;
	}
	
	
	public int getRepeat() {
		return getRepeat(0);
	}
	
	public int getRepeat( int subsettingIndex ) {
		String repeatStr = getValue("repeat");
		if (repeatStr == null) {
			return 0;
		}
		String[] repeatStrs = StringUtil.splitAndTrim(repeatStr, ',');
		if (subsettingIndex >= repeatStrs.length ) {
			return 0;
		}
		repeatStr = repeatStrs[subsettingIndex];
		
		try { 
			return Integer.parseInt(repeatStr);
		} catch (NumberFormatException e) {
			if ("always".equalsIgnoreCase(repeatStr)) {
				return -1;
			} else if ("once".equalsIgnoreCase(repeatStr)) {
				return -2;
			} else if ("never".equalsIgnoreCase(repeatStr)) {
				return 0;
			} else {
				throw new BuildException("Invalid CSS: the css animation for CSS attribute " + this.cssAttributeName + " has the invalid repeat setting \"" + repeatStr + "\". Please either use a number or \"always\", \"once\" or \"never\"." );
			}
		}
	}
	
	public CssAnimationRange[] getRanges() {
		String range = getValue("range");
		if (range == null) {
			//return null; // could be that an animation subtype wishes to use a different mechanism...
			throw new BuildException("Undefined \"range\" in CSS animation - add the \"range\" attribute to your polish.css for CSS animation " + getCssAttributeName() );
		}
		CssAnimationRange[] ranges = CssAnimationRange.parseRanges(range);
		if (ranges != null && this.cssAnimationRange == null) {
			this.cssAnimationRange = ranges[0];
		}
		return ranges;
	}
	
	public CssAnimationRange getRange() {
		if (this.cssAnimationRange == null) {
			String range = getValue("range");
			if (range == null) {
				return null; // could be that an animation subtype wishes to use a different mechanism...
				//throw new BuildException("Undefined \"range\" in CSS animation - add the \"range\" attribute to your polish.css for CSS animation " + getCssAttributeName() );
			}
			this.cssAnimationRange = CssAnimationRange.parseRange(range);
		}
		return this.cssAnimationRange;
	}

	
	public void setCssAnimationRange( CssAnimationRange range ) {
		this.cssAnimationRange = range;
	}
	
	public String getFireEvent() {
		return getValue("fire-event");
	}

	/**
	 * Extracts a subsetting from this animation setting.
	 * @param index the index of the subsetting
	 * @param numberOfSettings the available number of settings
	 * @param range the corresponding range (could be determined from the index as well, but getting this is more lazy ;-)
	 * @return the subsetting of this setting for the specified index and range.
	 */
	public CssAnimationSetting getSubSetting(int index, int numberOfSettings, CssAnimationRange range)
	{
		CssAnimationSetting setting = new CssAnimationSetting( this.cssAttributeName );
		setting.setCssAnimationRange(range);
		setting.addAnimationSetting( "on", getOn() );
		setting.addAnimationSetting( "duration", Long.toString( getDuration(index, numberOfSettings) ) );
		setting.addAnimationSetting( "delay", Long.toString( getDelay(index, numberOfSettings) ) );
		setting.addAnimationSetting( "function", getFunction(index) );
		setting.addAnimationSetting( "repeat", Integer.toString(getRepeat(index)) );
		return setting;
	}

	/**
	 * @param cssAttributeName the name of the css attribute
	 */
	public void setCssAttributeName(String cssAttributeName)
	{
		this.cssAttributeName = cssAttributeName;
	}
	
}
