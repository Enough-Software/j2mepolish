/*
 * Created on Jun 18, 2004
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

import java.util.ArrayList;
import java.util.List;

import de.enough.polish.Attribute;
import de.enough.polish.BuildException;
import de.enough.polish.Environment;
import de.enough.polish.Variable;

/**
 * <p>Represents user-defined attributes for the JAD and the MANIFEST</p>
 * 
 * @author robert virkus, j2mepolish@enough.de
 */
public class JadAttributes extends Variables {

	//private ArrayList list;
	private ArrayList filters;

	/**
	 * Creates a new list 
	 */
	public JadAttributes() {
		//this.list = new ArrayList();
	}

	public void addConfiguredProperty( Attribute attribute ) {
		addConfiguredAttribute(attribute);
	}

	public void addConfiguredProperties( Attribute attribute ) {
		addConfiguredAttribute(attribute);
	}

	public void addConfiguredAttributes( Attribute attribute ) {
		addConfiguredAttribute(attribute);
	}

	public void addConfiguredAttribute( Attribute attribute ) {
		if (!attribute.containsMultipleVariables()) {
			if (attribute.getName() == null) {
				throw new BuildException("Please check your <jad> definition, each attribute needs to have the attribute [name]");
			}
			if (attribute.getValue() == null) {
				throw new BuildException("Please check your <jad> definition, each attribute needs to have the attribute [value]");
			}
		}
		super.addConfiguredVariable(attribute);
			//FIXME add fix
		
			/*
		this.list.add( attribute );
	} else {
			Attribute[] attributes = attribute.loadAttributes();
			for (int i = 0; i < attributes.length; i++) {
				Attribute attr = attributes[i];
				attr.setIf( attribute.getIfCondition() );
				attr.setUnless( attribute.getUnlessCondition() );
				attr.setTargetsManifest( attribute.targetsManifest()  );
				attr.setTargetsJad( attribute.targetsJad() );
				this.list.add( attr );
			}
		}
			*/
	}
	
	public void addConfiguredFilter( AttributesFilter filterSetting ) {
		addConfiguredJadFilter(filterSetting);
	}

	public void addConfiguredJadFilter( AttributesFilter filterSetting ) {
		if ( this.filters == null ) {
			this.filters = new ArrayList();
		}
		this.filters.add( filterSetting );
	}

	protected Variable[] getVariables( List list ) {
		return (Attribute[]) list.toArray( new Attribute[ list.size() ] );	
	}
	
	public Attribute[] getAttributes( Environment environment ){
		Attribute[] attributes = (Attribute[]) getVariables( environment );
		for (int i = 0; i < attributes.length; i++) {
			Attribute attribute = attributes[i];
			String value = environment.writeProperties( attribute.getValue() );
			if (!value.equals(attribute.getValue())) {
				Attribute newAttribute = new Attribute( attribute );
				newAttribute.setValue( value );
				attributes[i] = newAttribute;
			}
		}
		return attributes;
	}
	
	public ArrayList getFilters() {
		return this.filters;
	}

}
