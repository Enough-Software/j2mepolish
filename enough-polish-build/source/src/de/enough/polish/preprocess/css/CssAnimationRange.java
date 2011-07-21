/*
 * Created on Jun 23, 2008 at 11:08:27 AM.
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

import java.util.ArrayList;

import de.enough.polish.BuildException;
import de.enough.polish.util.StringUtil;

/**
 * <p>Stores one range of an CSS animation. A single range uses two dots to split the start from the end value, e.g. range: 0..10; </p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CssAnimationRange
{
	
	private final String from;
	private final String to;

	/**
	 * Creates a new CssAnimationRange.
	 * @param from the start point
	 * @param to the end point
	 */
	public CssAnimationRange( String from, String to ) {
		this.from = from;
		this.to = to;
	}

	/**
	 * @return the from
	 */
	public String getFrom() {
		return this.from;
	}
	

	/**
	 * @return the to
	 */
	public String getTo() {
		return this.to;
	}
	
	
	/**
	 * Parses the specified range string
	 * @param range the range string. e.g. "#fff..#666"
	 * @return an array of ranges, e.g. new CssAnimationRange[]{ new CssAnimationRange( "#fff", "#666" ) }
	 */
	public static CssAnimationRange[] parseRanges( String range ) {
		String[] rangeValues = StringUtil.splitWhileKeepingParenthesesAndTrim(range, ',');
		ArrayList rangesList = new ArrayList(rangeValues.length);
		for (int i = 0; i < rangeValues.length; i++)
		{
			String rangeValue = rangeValues[i];
			String[] ranges = StringUtil.splitAndTrim(rangeValue, "..");
			if (ranges.length == 2) {
				rangesList.add( new CssAnimationRange( ranges[0], ranges[1]));
			} else if (ranges.length < 2) {
				throw new BuildException("Invalid CSS animation range: \"" + range + "\" with fragment \"" + rangeValue + "\": use two dots (..) to separate from and to value. Check your polish.css file." );				
			} else {
				for (int j=1; j<ranges.length; j++) {
					CssAnimationRange rng = new CssAnimationRange( ranges[j-1], ranges[j]);
					rangesList.add( rng );
				}
			}
		}
		CssAnimationRange[] ranges = (CssAnimationRange[]) rangesList.toArray( new CssAnimationRange[ rangesList.size() ] );
		return ranges;
	}

	/**
	 * @param range as string
	 * @return the range
	 */
	public static CssAnimationRange parseRange(String range)
	{
		String[] ranges = StringUtil.splitAndTrim(range, "..");
		if (ranges.length != 2){
			throw new BuildException("Invalid CSS animation range: \"" + range + "\": use two dots (..) to separate from and to value. Check your polish.css file." );
		}
		return new CssAnimationRange( ranges[0], ranges[1] );
	}
}
