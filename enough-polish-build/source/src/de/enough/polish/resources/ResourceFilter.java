/*
 * Created on 11-Sep-2004 at 20:38:11.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.enough.polish.BuildException;

import de.enough.polish.ant.build.ResourceFilterSetting;

/**
 * <p>Filters resources by names.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        11-Sep-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ResourceFilter {
	
	private final String[] startPatterns;
	private final String[] endPatterns;
	private final HashMap excludesByName;
	private Pattern[] patterns;

	/**
	 * Creates a new filter.
	 * 
	 * @param userExcludes user defined exclused patterns, e.g *.db
	 * @param defaultExcludes default exclude patterns, e.g. "polish.css"
	 * @param useDefaultExcludes true when the default excludes should be used
	 */
	public ResourceFilter( String[] userExcludes, String[] defaultExcludes, boolean useDefaultExcludes ) {
		super();
		this.excludesByName = new HashMap();
		ArrayList startPatternsList = new ArrayList();
		ArrayList endPatternsList = new ArrayList();
		for (int i = 0; i < userExcludes.length; i++) {
			String exclude = userExcludes[i];
			if (exclude.indexOf('*') == -1) {
				this.excludesByName.put( exclude, Boolean.TRUE );
			} else if (exclude.charAt(0) == '*'){
				endPatternsList.add( exclude.substring( 1 ) );
			} else if (exclude.charAt( exclude.length()-1) == '*') {
				startPatternsList.add( exclude.substring( 0, exclude.length() -1 ) );
			} else {
				throw new BuildException("Invalid exclude-pattern [" + exclude + "]: stars are only allowed at either the start or the end of the pattern.");
			}
		}
		if (useDefaultExcludes) {
			for (int i = 0; i < defaultExcludes.length; i++) {
				String exclude = defaultExcludes[i];
				if (exclude.indexOf('*') == -1) {
					this.excludesByName.put( exclude, Boolean.TRUE );
				} else if (exclude.charAt(0) == '*'){
					endPatternsList.add( exclude.substring( 1 ) );
				} else if (exclude.charAt( exclude.length()-1) == '*') {
					startPatternsList.add( exclude.substring( 0, exclude.length() -1 ) );
				} else {
					throw new BuildException("Invalid exclude-pattern [" + exclude + "]: stars are only allowed at either the start or the end of the pattern.");
				}
			}
		}
		this.startPatterns = (String[]) startPatternsList.toArray( new String[ startPatternsList.size() ]);
		this.endPatterns = (String[]) endPatternsList.toArray( new String[ endPatternsList.size() ]);
	}
	
	public void addExclude( String fileName ) {
		this.excludesByName.put( fileName, Boolean.TRUE );
	}
	
	public void removeExclude( String fileName ) {
		this.excludesByName.remove( fileName );
	}
	
	
	/**
	 * Filters the given file names.
	 * 
	 * @param fileNames array of file names
	 * @return only those file-names which should be included
	 */
	public String[] filter( String[] fileNames ) {
		ArrayList list  = new ArrayList( fileNames.length );
		for (int i = 0; i < fileNames.length; i++) {
			String fileName = fileNames[i];
			//System.out.println("filter: checking " + fileName );
			if (this.excludesByName.get(fileName) != null) {
				// this file should not be included: 
				continue;
			}
			boolean include = true;
			for (int j = 0; j < this.startPatterns.length; j++) {
				String pattern = this.startPatterns[j];
				if (fileName.startsWith( pattern )) {
					// this file should not be included:
					include = false;
					break;
				}
			}
			for (int j = 0; j < this.endPatterns.length; j++) {
				String pattern = this.endPatterns[j];
				if (fileName.endsWith( pattern )) {
					// this file should not be included:
					include = false;
					break;
				}
			}
			if (this.patterns != null) {
				for (int j = 0; j < this.patterns.length; j++) {
					Pattern pattern = this.patterns[j];
					Matcher matcher = pattern.matcher(fileName);
					if ( matcher.find()) {
						// this file should not be included:
						//System.out.println(">>>>> MATCH!!! pattern [" + pattern.pattern() + "] matches resource [" + fileName + "]");
						include = false;
						break;
					}
					//System.out.println("pattern [" + pattern.pattern() + "] DOES NOT match resource [" + fileName + "]");
				}
			}
			if (include) {
				//System.out.println("filter: adding " + fileName );
				// okay, this file should really be included:
				list.add( fileName );
			}
		}
		return (String[]) list.toArray( new String[ list.size() ] );
	}

	public void setAdditionalFilters(ResourceFilterSetting[] filters) {
		if (filters == null) {
			this.patterns = null;
			return;
		}
		ArrayList patternsList = new ArrayList();
		for (int i = 0; i < filters.length; i++) {
			ResourceFilterSetting setting = filters[i];
			Pattern[] pats = setting.getExcludePatterns();
			for (int j = 0; j < pats.length; j++) {
				Pattern pattern = pats[j];
				patternsList.add( pattern );
			}
		}
		this.patterns = (Pattern[]) patternsList.toArray( new Pattern[ patternsList.size() ] );
	}

}
 