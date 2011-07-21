/*
 * Created on 22-Jan-2003 at 14:31:40.
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
package de.enough.polish.ant.build;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

import de.enough.polish.BuildException;
import org.apache.tools.ant.Project;

import de.enough.polish.BooleanEvaluator;
import de.enough.polish.Environment;
import de.enough.polish.ant.ConditionalElement;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.StringUtil;

/**
 * <p>Manages all midlets of a specific project.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        22-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class MidletSetting extends ConditionalElement {
	
	private final ArrayList midlets;
	private String propertiesPath;

	public MidletSetting() {
		this.midlets = new ArrayList();
	}
	
	public void addConfiguredMidlet( Midlet midlet ) {
		if (midlet.getNumber() == 0) {
			midlet.setNumber( this.midlets.size() + 1 );
		}
		if (midlet.getClassName() == null) {
			throw new BuildException("The <midlet> elements needs to have the attribute [class], which defines the name of a MIDlet-class!");
		}
		this.midlets.add( midlet );
	}
	
	/**
	 * Sets the path to the properties file that contains MIDlet definitions just like in a JAD file.
	 * 
	 * @param path the path to the file
	 */
	public void setFile( String path ) {
		this.propertiesPath = path;
	}
	
	/**
	 * Gets all the defined midlets in the correct order.
	 * @param project
	 * @param environment 
	 * 
	 * @return All midlets in the correct order, that means Midlet-1 is the first element of the array. 
	 */
	public Midlet[] getMidlets(Project project, Environment environment) {
		if (this.propertiesPath != null) {
			File propertiesFile = environment.resolveFile(this.propertiesPath);
			if ( !propertiesFile.exists() ) {
				throw new BuildException("The \"file\" attribute of the <midlets> element points to the invalid path [" + this.propertiesPath +"].");
			}
			try {
				Map properties = FileUtil.readPropertiesFile(propertiesFile,':');
				environment.putAll( properties );
			} catch (IOException e) {
				e.printStackTrace();
				throw new BuildException("Unable to load properties from the \"file\" attribute of the <midlets> element points that points to [" + this.propertiesPath +"]: " + e.toString());
			}
		}
		BooleanEvaluator evaluator = environment.getBooleanEvaluator();
		ArrayList mids = new ArrayList();
		Midlet[] midletsArray = (Midlet[]) this.midlets.toArray( new Midlet[ this.midlets.size() ] );
		// add only active MIDlets:
		for (int i = 0; i < midletsArray.length; i++) {
			Midlet midlet = midletsArray[i];
			if (midlet.isActive( evaluator, project )) {
				mids.add( midlet );
			}
		}
		midletsArray = (Midlet[]) mids.toArray( new Midlet[ mids.size() ] );
		// sort MIDlets by priority:
		Arrays.sort( midletsArray, new MidletComparator() );
		return midletsArray;
	}
	
	/**
	 * <p>Is used to sort midlets.</p>
	 * <p>Copyright Enough Software 2004, 2005</p>

	 * <pre>
	 * history
	 *        18-Feb-2004 - rob creation
	 * </pre>
	 * @author Robert Virkus, robert@enough.de
	 */
	static class MidletComparator implements Comparator, Serializable {

		private static final long serialVersionUID = 4908901995318110248L;

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Object one, Object two) {
			if ( ! ( one instanceof Midlet && two instanceof Midlet )) {
				return 0;
			}
			Midlet midlet1 = (Midlet) one;
			Midlet midlet2 = (Midlet) two;
			return midlet1.getNumber() - midlet2.getNumber();
		}
		
	}

	public Midlet[] getMidlets() {
		return (Midlet[]) this.midlets.toArray( new Midlet[ this.midlets.size() ] );
	}
	
	public void setDefinition( String definition ) {
		if (definition == null || "".equals(definition)) {
			System.err.println("Warning: the \"definition\" attribute given in the  <midlets> element is empty.");
			return;
		}
		// check for netbeans definition:
		if ("${manifest.midlets}".equals(definition)) {
			return;
		}
		String[] definitions = StringUtil.split( definition, '\n' );
		for (int i = definitions.length; --i >= 0; ) {
			String def = definitions[i];
			if (def.length() < 2 ) {
				continue;
			}
			Midlet midlet = new Midlet();
			midlet.setDefinition( def );
			addConfiguredMidlet(midlet);
		}
	}

}
