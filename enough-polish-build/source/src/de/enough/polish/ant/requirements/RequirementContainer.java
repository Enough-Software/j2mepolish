/*
 * Created on 15-Feb-2004 at 18:33:05.
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
package de.enough.polish.ant.requirements;

import de.enough.polish.Variable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>Represents an "AND", "OR", "XOR" and "NOT" relation between several requirements.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        15-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public abstract class RequirementContainer
implements DeviceFilter
{
	
	private ArrayList filters;
	protected List requiredIdentifiers;
	private Map buildProperties;
	/**
	 * Cerates a new device requirements list.
	 * @param buildProperties the build properties
	 */
	public RequirementContainer(Map buildProperties) {
		this.buildProperties = buildProperties;
		this.filters = new ArrayList();
	}

	/**
	 * Creates a new empty container
	 */
	public RequirementContainer() {
		this.filters = new ArrayList();
	}
	
	protected void add( DeviceFilter filter ) {
		if (filter instanceof Requirement) {
			((Requirement)filter).setBuildProperties( this.buildProperties );
		}
		this.filters.add( filter );
		// check if this is a <identfier> requirement of if this container/relation only includes <identifier> requirements.
		// when a non-<identifier> requirement has been added before, the requiredIdentifiers field is set to null.
		if (this.requiredIdentifiers != null || this.filters.size() == 1 ) {
			addIdentifiers( filter );
		}
	}
	
	/**
	 * Adds the (possibly nested) &lt;identifier&gt; requirements. 
	 * When there is a filter that is neither a Requirement nor a RequirementsContainer, the 
	 * requiredIdentifiers field is set to null.
	 * 
	 * @param filter the filter that is added
	 */
	private void addIdentifiers(DeviceFilter filter) {
    	if (filter instanceof IdentifierRequirement) {
    		if (this.requiredIdentifiers == null) {
    			this.requiredIdentifiers = new LinkedList();
    		}
    		String[] identifiers = ((IdentifierRequirement) filter).getIdentifers();
    		for (int i = 0; i < identifiers.length; i++) {
				String identifier = identifiers[i];
				this.requiredIdentifiers.add( identifier ); 
			}
    	} else if (filter instanceof RequirementContainer) {
    		DeviceFilter[] deviceFilters = ((RequirementContainer) filter).getFilters();
    		for (int i = 0; i < deviceFilters.length; i++) {
				DeviceFilter subFilter = deviceFilters[i];
				addIdentifiers(subFilter);
				if (this.requiredIdentifiers == null) {
					break;
				}
			}
    	} else {
    		this.requiredIdentifiers = null;
    	}
	}

	public void addConfiguredRequirement( Variable req ) {
		String name = req.getName(); 
		String value = req.getValue();
		String type = req.getType();
		Requirement requirement = Requirement.getInstance( name, value, type );
		add( requirement );
	}
    
    public void addRequirement( Requirement requirement ) {
    	add( requirement );
    }

	
	public void addConfiguredAnd( AndRelation andRelation ) {
		add( andRelation );
	}
	
	public void addConfiguredOr( OrRelation orRelation ) {
		add( orRelation );
	}
	
	public void addConfiguredNot( NotRelation notRelation ) {
		add( notRelation );
	}
	
	public void addConfiguredNand( NotRelation notRelation ) {
		add( notRelation );
	}
	
	public void addConfiguredXor( XorRelation xorRelation ) {
		this.filters.add( xorRelation );
	}
	
	public DeviceFilter[] getFilters() {
		return (DeviceFilter[]) this.filters.toArray( new DeviceFilter[this.filters.size()] );
	}

	/**
	 * Retrieves a list of the required device identifiers.
	 * This is a special case that allows a faster time for reading the device database, since 
	 * only those devices need to be read that have the wanted identifier.
	 * 
	 * @return a list of the required identifiers when only &lt;identifier&gt; requirements are used, otherwise null.
	 */
	public List getRequiredIdentifiers() {
		return this.requiredIdentifiers;
	}
	
	
}
