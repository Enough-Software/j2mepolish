/*
 * Created on Nov 21, 2007 at 9:45:55 PM.
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
import java.util.HashMap;
import java.util.Map;

import de.enough.polish.BuildException;
import de.enough.polish.preprocess.css.attributes.ParameterizedCssAttribute;

/**
 * <p>Sorts backgrounds within the backgrounds section - backgrounds without dependencies need to be created before backgrounds with dependencies like the combined or the mask background.</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Nov 21, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class BackgroundComparator
{


	/**
	 * Sorts all backgrounds corresponding to their dependencies
	 * @param backgrounds the backgrounds
	 * @param backgroundTypes the background types
	 * @return a sorted array of the given background names
	 */
	public static Object[] sort( HashMap backgrounds, ParameterizedCssAttribute backgroundTypes)
	{
		Object[] backgroundNames = backgrounds.keySet().toArray();
		ArrayList backgroundsWithoutDependencies = new ArrayList();
		ArrayList backgroundsWithDependencies = new ArrayList();
		for (int i = 0; i < backgroundNames.length; i++)
		{
			Object name = backgroundNames[i];
			Map background = (Map) backgrounds.get(name);
			String type = (String) background.get("type");
			if (type == null) {
				// must be a simple background without dependencies:
				//System.out.println("directly adding simple background " + name);
				backgroundsWithoutDependencies.add( name );
				continue;
			}
			ParameterizedCssMapping mapping = (ParameterizedCssMapping) backgroundTypes.getMapping(type);
			if (mapping == null) {
				throw new BuildException("Unable to resolve background type \"" + type + "\" - please check your polish.css file." );
			}
			CssAttribute[] parameters = mapping.getParameters();
			boolean hasDependencies = false;
			for (int j = 0; j < parameters.length; j++)
			{
				CssAttribute parameter = parameters[j];
				if ("background".equals(parameter.getType())) {
					//System.out.println("adding " + name + " to list of dependent backgrounds");
					backgroundsWithDependencies.add( name );
					hasDependencies = true;
					break;
				}
			}
			if (!hasDependencies) {
				// there was no background type, so this background has no dependencies:
				backgroundsWithoutDependencies.add( name );
				//System.out.println("directly adding " + name);
			}
		}
		
		// now sort the backgrounds with dependencies and 
		// add each one with resolved dependencies to the list of backgrounds without dependencies:
		while (backgroundsWithDependencies.size() > 0) {
			boolean backgroundResolved = false;
			backgroundNames = backgroundsWithDependencies.toArray();
			for (int i=0; i<backgroundNames.length; i++) {
				Object name = backgroundNames[i];
				Map background = (Map) backgrounds.get(name);
				String type = (String) background.get("type");
				ParameterizedCssMapping mapping = (ParameterizedCssMapping) backgroundTypes.getMapping(type);
				CssAttribute[] parameters = mapping.getParameters();
				boolean hasDependency = false;
				for (int j = 0; j < parameters.length; j++)
				{
					CssAttribute parameter = parameters[j];
					if ("background".equals(parameter.getType())) {
						Object referencedBackground = background.get(parameter.getName());
						if (referencedBackground != null && backgroundsWithDependencies.contains(referencedBackground)) {
							//System.out.println( name + " depends on " + referencedBackground);
							hasDependency = true;
							break;
//						} else {
//							System.out.println( name + " reference " + referencedBackground + " has been resolved.");
						}
					}
				}
				if (!hasDependency) {
					//System.out.println("adding " + name);
					backgroundResolved = true;
					backgroundsWithoutDependencies.add( name );
					backgroundsWithDependencies.remove( name );
				}
			}
			
			if (!backgroundResolved) {
				StringBuffer buffer = new StringBuffer();
				buffer.append( "There are unresolvable dependencies between following backgrounds: " );
				for (int i=0; i<backgroundsWithDependencies.size();i++) {
					buffer.append( backgroundsWithDependencies.get(i));
					if (i != backgroundsWithDependencies.size() - 1) {
						buffer.append(", ");
					}
				}
				buffer.append(". Please check your polish.css settings.");
				throw new BuildException( buffer.toString() );
			}
		}
		
		return backgroundsWithoutDependencies.toArray();
	}
	

}
