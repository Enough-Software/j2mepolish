/*
 * Created on 15-Jan-2004 at 15:00:29.
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
package de.enough.polish.devices;


import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jdom.Element;

import de.enough.polish.Variable;
import de.enough.polish.exceptions.InvalidComponentException;
import de.enough.polish.util.StringUtil;

/**
 * <p>Provides common functionalities for PolishProject, Vendor, DeviceGroup and Device.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        15-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class PolishComponent
implements Comparable
{
	
	protected String identifier;
	protected PolishComponent parent;
	protected boolean supportsPolishGui;
	protected HashMap features;
	protected HashMap capabilities;
	private String featuresAsString;
	protected CapabilityManager capabilityManager;
	protected final HashMap implicitGroupsByName;
	protected String description;
	
	/**
	 * Creates a new component.
	 * @param definition the XML definition, can be null
	 */
	public PolishComponent( Element definition ) {
		this( null, null, definition );
	}
	/**
	 * Creates a new component.
	 * 
	 * @param parent the parent, e.g. is the parent of a vendor a project,
	 *              the parent of a device is a vendor.
	 * @param capabilityManager knows about how to deal with capabilities
	 * @param definition the XML definition, can be null
	 */
	public PolishComponent( PolishComponent parent, CapabilityManager capabilityManager, Element definition ) {
		this.parent = parent;
		this.capabilityManager = capabilityManager;
		this.capabilities = new HashMap();
		//this.capabilitiesList = new ArrayList();
		this.features = new HashMap();
		this.implicitGroupsByName = new HashMap();
		//this.featuresList = new ArrayList();
		if (parent != null) {
			this.capabilities.putAll( parent.getCapabilities() );
			this.features.putAll(  parent.getFeatures() );
			this.featuresAsString = parent.featuresAsString;
		}
		if (definition != null) {
			this.description = definition.getChildTextTrim("description");
            this.description = stripText(this.description);
		}
        if(this.description == null) {
		    this.description = "";
        }
	}

	/**
     * Converts new lines, tabs and successive whitespace to a single whitespace.
     * 
     * @param string the string to strip. May be null.
     * @return the striped string or null if the parameter was null.
     */
    protected String stripText(String string) {
        if(string == null) {
            return null;
        }
        return string.replaceAll("\\s+|\r\n|\n"," ");
        //string = string.replaceAll("  "," ").trim();
//        String temp = string;
//        temp = temp.replaceAll("\t+"," ");
//        temp = temp.replaceAll(" +"," ");
//        return temp;
    }
    
    /**
	 * Loads all found capabilities of this component.
	 * 
	 * @param definition The xml definition.
	 * @param componentName The name of the component, e.g. "Nokia/3650" for a device.
	 * @param fileName The name of the source-file, e.g. "devices.xml".
	 * @throws InvalidComponentException when the defintion contains errors.
	 */
	protected void loadCapabilities(Element definition, String componentName, String fileName ) 
	throws InvalidComponentException 
	{
		// read capabilities:
		List capDefinitions = definition.getChildren("capability");
		for (Iterator iter = capDefinitions.iterator(); iter.hasNext();) {
			Element element = (Element) iter.next();
			String capName = element.getAttributeValue( "name" );
			if (capName == null) {
				capName = element.getChildTextTrim("capability-name");
			}
			if (capName == null) {
				throw new InvalidComponentException("The component [" + componentName + "] has an invalid [capability] - every capability needs to define the attribute [name]. Please check you [" + fileName + "].");
			}
			String capValue = element.getAttributeValue( "value" );
			if (capValue == null) {
				capValue = element.getChildTextTrim("capability-value");
			}
			// add the capability:
			addCapability( capName, capValue );
		} // end of reading all capabilties
		
		// now set features:
		String featureDefinition = definition.getChildTextTrim( "features");
		if (featureDefinition != null && featureDefinition.length() > 0) {
			String[] definedFeatures = StringUtil.splitAndTrim( featureDefinition, ',');
			for (int i = 0; i < definedFeatures.length; i++) {
				addFeature( definedFeatures[i] );
			}
			if (this.featuresAsString != null) {
				this.featuresAsString += ", " + featureDefinition;
			} else {
				this.featuresAsString = featureDefinition;
			}
			//System.out.println( this.identifier + ".loadCapabilities(): featuresAsString=" + this.featuresAsString);
		}
	}
	
	/**
	 * Loads all groups to which this component belongs to and sets the capabilities accordingly.
	 * 
	 * @param definition the xml definition
	 * @param groupManager the manager of groups
	 * @param invalidGroupMessage the message for the InvalidComponentException when a group name is not valid.
	 *        {0} is for the identifier, {1} for the group name.
	 * @return an array of strings with the names of the groups, null when no groups are used
	 * @throws InvalidComponentException when a group name is not valid
	 */
	protected String[] loadGroups(Element definition, DeviceGroupManager groupManager, String invalidGroupMessage) 
	throws InvalidComponentException {
		String groupsDefinition = definition.getChildTextTrim("groups");
		String[] explicitGroupNames = null;
		if (groupsDefinition != null && groupsDefinition.length() > 0) {
			explicitGroupNames = StringUtil.splitAndTrim(groupsDefinition, ',');
			for (int i = 0; i < explicitGroupNames.length; i++) {
				String groupName = explicitGroupNames[i];
				DeviceGroup group = groupManager.getGroup(groupName);
				if (group == null) {
					throw new InvalidComponentException(
							MessageFormat.format( invalidGroupMessage, new String[]{ this.identifier, groupName } ) );
				}
				//System.out.println( this.identifier + ": adding group [" + groupName + "], JavaPackage=" + group.getCapability("polish.JavaPackage") );
				addComponent( group );
				/*
				String parentName = group.getParentIdentifier();
				while (parentName != null) {
					DeviceGroup parentGroup = groupManager.getGroup(parentName);
					System.out.println( this.identifier + ": adding parent group [" + parentName + "] + JavaPackage=" + parentGroup.getCapability("polish.JavaPackage"));
					addComponent( parentGroup );
					parentName = parentGroup.getParentIdentifier();
				}
				*/
			}
		}
		return explicitGroupNames;
	}


	/**
	 * Adds a sub-component to this component, replacing or appending to existing capabilities during the process.
	 * 
	 * @param component The component which definitions should be added 
	 */
	public void addComponent(PolishComponent component ) {
		// 1. set the capabilities:
		HashMap caps = component.getCapabilities();
		for (Iterator iter = caps.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String componentValue = (String) caps.get( name );
			boolean add = this.capabilities.get(name) == null;
			if (!add) {
				Capability capability = this.capabilityManager.getCapability(name);
				if (capability != null && !capability.overwrite()) {
					add = true;
				}
			}
			if (add) {
				addCapability( name, componentValue );
			}
		}
		
		// 2. set all features (overwriting will do no harm):
		Set feats = component.features.keySet();
		for ( Iterator iter = feats.iterator(); iter.hasNext(); ) {
			String name = (String) iter.next();
			this.features.put( name, Boolean.TRUE );
		}
		
		// 3. set the features-string:
		if (component.featuresAsString != null) {
			if (this.featuresAsString != null) {
				this.featuresAsString += ", " + component.featuresAsString;
			} else {
				this.featuresAsString = component.featuresAsString;
			}
			//System.out.println( this.identifier + ".addComponent(): featuresAsString=" + this.featuresAsString);
		}
	}
	
	/**
	 * Removes a component:
	 * @param component
	 */
	protected void removeComponent(PolishComponent component)
	{
		//System.out.println("REMOVING " + component.getIdentifier());
		// 1. remove the capabilities:
		HashMap caps = component.getCapabilities();
		for (Iterator iter = caps.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String componentValue = (String) caps.get( name );
			String feature = name + "." + componentValue;
			this.features.remove( feature );
			//System.out.println("REMOVED: " + feature);
			String existingValue = getCapability(name);
			if (existingValue != null) {
				int index = existingValue.indexOf(componentValue);
				if (index != -1) {
					String start = existingValue.substring(0, index);
					if (start.length() == 1) { // this will be a comma or similar
						start = ""; 
					} else if (start.endsWith(",")) {
						start = start.substring(0, start.length() -1 );
					}
					String end = existingValue.substring( index + componentValue.length() );
					if (end.length() == 1) {
						end = "";
					} else if (end.startsWith(",")) {
						end = end.substring(1);
					}
					
					existingValue = start + end;
					this.capabilities.put( name, existingValue );
					//System.out.println("NEW VALUE FOR " + name + ": " + existingValue );
				}
			}
			
		}
		
		// 2. remove all features:
		Set feats = component.features.keySet();
		for ( Iterator iter = feats.iterator(); iter.hasNext(); ) {
			String name = (String) iter.next();
			this.features.remove( name );
			//System.out.println("REMOVED: " + name);
		}
	}

	
	/**
	 * Retrieves all preprocessing-symbols of this component and its parent component.
	 * The symbols are arranged in a HashMap, so one can check for the definition
	 * of a symbol with <code> if (map.get( "symbol-name") != null) { // symbol is defined</code>.
	 * <p>Symbols can be retrieved in different ways:
	 * <ul>
	 * 	<li><b>polish.symbol-name</b>: this is the recommended way to check for a symbol.
	 * 				The symbols starting with "polish" can be defined by  the project, the vendor, 
	 * 				group or device used.</li>
	 * 	<li><b>project.symbol-name</b>: this name can be used to check for symbols which
	 * 				need to be defined in the project itself.</li>
	 * 	<li><b>vendor.symbol-name</b>: this name can be used to check for symbols which
	 * 				need to be defined in the current manufacturer definition (e.g. Nokia) itself.</li>
	 * 	<li><b>group.symbol-name</b>: this name can be used to check for symbols which
	 * 				need to be defined in the current groups (e.g. Series 60) itself.</li>
	 * 	<li><b>device.symbol-name</b>: this name can be used to check for symbols which
	 * 				need to be defined in the current device itself.</li>
	 * </ul>
	 * </p>
	 * @return the HashMap containing all names of the defined symbols as keys.
	 */
	public HashMap getFeatures() {
		return new HashMap( this.features );
	}
	
	/**
	 * Retrieves all preprocessing-variables of this component and its parent component.
	 * The values defined by a variable can be retrieved by calling 
	 * <code>String value = (String) map.get("variable-name")</code>.
	 * 
	 * <p>Variables which are defined in the device-database all start with "polish.":
	 * <ul>
	 * 	<li><b>polish.variable-name</b>: 
	 * 				The variables starting with "polish" can be defined by  the project, the vendor, 
	 * 				group or device used. They also can be overriden by specific settings.
	 * 			    For example a color "color.focus" could be defined in the project and
	 * 				also be defined in a device definition. "polish.color.focus" would then
	 * 			 	return the device definition while "project.color " would return the original 
	 * 				project definition, when the application is preprocessed for that
	 * 				device.</li>
	 * </ul>
	 * </p>
	 * @return a HashMap containing all names of the defined variables as the keys.
	 */
	public HashMap getCapabilities() {
		//HashMap copy = new HashMap( this.capabilities.size() + 20 );
		//copy.putAll( this.capabilities );
		return new HashMap( this.capabilities );
	}
	
	/**
	 * Adds a capability to this component.
	 * 
	 * @param name the name of the capability
	 * @param value the value of the capability
	 */
	public void addCapability( String name, String value ) {
		//System.out.println("adding capability " + name + " with value " + value );
		// when the capability starts with "SoftwarePlatform." or similiar, 
		// make it also accessible without it:
		if (name.startsWith("SoftwarePlatform.")) {
			name = name.substring( 17 );
		} else if (name.startsWith("HardwarePlatform.")) {
			name = name.substring( 17 );
		}
//		String originalName = name;
		name = name.toLowerCase();		
		if (!name.startsWith("polish.")) {
			name = "polish." + name;
//			originalName = "polish." + originalName;
		}
		if (value.length() == 0) {
			String existingValue = (String) this.capabilities.remove( name );
			this.features.remove( name + "." + existingValue );
			this.features.remove(name + ":defined" );
			if (existingValue != null) {
				String[] individualValues = StringUtil.splitAndTrim( existingValue.toLowerCase(), ',' );
				for (int i = 0; i < individualValues.length; i++) {
					String individualValue = individualValues[i];
					this.features.remove( name + "." + individualValue );
				}
			}
			return;
		}
		String existingValue = getCapability( name );
		// remove any feature relying on the current value:
		if (existingValue != null) {
			this.features.remove( name + "." + existingValue );
		}
		//boolean debug = (name.indexOf("javapackage") != -1);
		if (this.capabilityManager != null) {
			Capability capability = this.capabilityManager.getCapability( name );
			if ( capability != null ) {
				value = capability.getValue(existingValue, value);
				if (value.equals(existingValue)) {
					// this is a duplicate:
					return;
				}
//				if ( capability.appendExtensions() ) {
//					//value = value.toLowerCase();
//					//if (debug) {
//					//	System.out.println( this.identifier + ": " + name + ": value = [" + value + "], existingValue = [" + existingValue + "]");
//					//}
//					if (existingValue != null) {
//						String[] singleValues = StringUtil.splitAndTrim( value, ',' );
//						boolean valueAdded = false;
//						for (int i = 0; i < singleValues.length; i++) {
//							String singleValue = singleValues[i];
//							//TODO what happens if I add "mmapi" to a "mmaapi1.1" device?
//							int existingValueIndex = existingValue.indexOf( singleValue ); 
//							if ( existingValueIndex != -1 ) {
//								// this is a value that seems to be present already,
//								// double check it:
//								int commaIndex = existingValue.indexOf(',', existingValueIndex );
//								String checkValue;
//								if (commaIndex == -1) {
//									checkValue = existingValue.substring( existingValueIndex ).trim();
//								} else {
//									checkValue = existingValue.substring( existingValueIndex, commaIndex ).trim();
//								}
//								if ( !checkValue.equals( singleValue ) ) {
//									// this was just a similar value...
//									//System.out.println( this.identifier + ": Adding value " + singleValue + " to " + name );
//									existingValueIndex = -1;
//								//} else {
//								//	System.out.println( this.identifier + ": Duplicate value " + singleValue + " of " + name );
//								}
//							}
//							if ( existingValueIndex == -1) {
//								existingValue += ", " + singleValue;
//								valueAdded = true;
//							}
//						}
//						if (valueAdded) {
//							value = existingValue;
//						} else {
//							return;
//						}
//					}
//				}
//                if(capability.appendZeroDelimitedExtension()) {
//                    if(existingValue != null) {
//                        String temp = existingValue + '\1' + value;
//                        value = temp;
//                    }
//                }
				String group = capability.getImplicitGroup();
				if ( group != null ) {
					addImplicitGroups( value );
				}
			}
		}
		//if (debug) {
		//	System.out.println(this.identifier + ": " + name + "=" + value);
		//}
		/*
		if ( (Device.JAVA_PACKAGE.equals(name) ) 
				|| (Device.JAVA_PROTOCOL.equals(name)) 
				|| (Device.VIDEO_FORMAT.equals(name))
				|| (Device.SOUND_FORMAT.equals(name)) ) {
			value = value.toLowerCase();
			String existingValue = getCapability( name );
			if (existingValue != null) {
				value += "," + existingValue;
			}
		}
		*/
		addSingleCapability( name, value );
//		addSingleCapability( originalName, value );
		
		// when the capability is a size, then also add a height and a width:
		if (name.endsWith("size") && value.indexOf('x') > 0) {
			String[] values = StringUtil.splitAndTrim( value, 'x' );
			String nameStart = name.substring(0, name.length() - 4);
			String width = nameStart + "width";
			addSingleCapability( width, values[0]);
			String height = nameStart + "height";
			addSingleCapability( height, values[1]);
			if (values.length == 3) {
				String depth = nameStart + "depth";
				addSingleCapability( depth, values[2]);
			}
		}
		
		// add all capability-values as symbols/features:
		String[] values = StringUtil.splitAndTrim( value, ',' );
		for (int i = 0; i < values.length; i++) {
			addFeature( name + "." + values[i] );
		}
	}
	
	/**
	 * @param value
	 */
	protected void addImplicitGroups(String value) {
		String[] values = StringUtil.splitAndTrim( value.toLowerCase(), ',' );
		for (int i = 0; i < values.length; i++) {
			String name = values[i];
			this.implicitGroupsByName.put( name, Boolean.TRUE );
		}
	}
	
	/**
	 * Adds a single capability to this component.
	 * 
	 * @param name the name of the capability
	 * @param value the value of the capability
	 */
	private void addSingleCapability( String name, String value ) {
//		String originalName = name;
		name = name.toLowerCase(); 
		String previousValue = (String) this.capabilities.get(name);
		if (previousValue != null) {
			// remove previous values from defined symbols first:
			if ("true".equals(previousValue)) {
				this.features.remove( name );
			}
			this.features.remove( name + "." + previousValue );
			String[] individualValues = StringUtil.splitAndTrim( previousValue.toLowerCase(), ',' );
			for (int i = 0; i < individualValues.length; i++) {
				String individualValue = individualValues[i];
				this.features.remove( name + "." + individualValue );
			}
		}
		if (value.length() == 0) {
			this.capabilities.remove(name);
			this.features.remove(name + ":defined" );
			name = name.toLowerCase();
			this.capabilities.remove(name);
			this.features.remove(name + ":defined" );
			return;
		}

		this.capabilities.put( name, value );
//		this.capabilities.put( originalName, value );
		this.features.put( name + ":defined", Boolean.TRUE );
//		this.features.put( originalName + ":defined", Boolean.TRUE );
		if ("true".equalsIgnoreCase(value)) {
			this.features.put( name, Boolean.TRUE );
//			this.features.put( originalName, Boolean.TRUE );
		}
	}
	
	/**
	 * Adds a capability without changing its name to this component.
	 * 
	 * @param capability The capability which should be added
	 */
	public void addDirectCapability(Variable capability) {
		addDirectCapability( capability.getName(), capability.getValue()  ); 
	}
	

	/**
	 * Adds a capability without changing its name to this component.
	 * 
	 * @param name The name of the capability.
	 * @param value The value of the capability.
	 */
	public void addDirectCapability(String name, String value) {
		//name = name.toLowerCase();
		addSingleCapability(name, value);
		// add all capability-values as symbols/features:
		String[] values = StringUtil.splitAndTrim( value, ',' );
		for (int i = 0; i < values.length; i++) {
			addDirectFeature( name + "." + values[i] );
		}
		if ("true".equalsIgnoreCase(value)) {
			addDirectFeature( name );
		}
	}
	
	/**
	 * Adds a feature this this component.
	 * 
	 * @param name the name of the feature
	 */
	public void addFeature( String name ) {
		if (name.length() == 0) {
			return;
		}
		name = name.toLowerCase();
		if (!name.startsWith("polish.")) {
			name = "polish." + name;
		} 
		this.features.put( name, Boolean.TRUE );
	}
	
	/**
	 * Adds a feature without inserting a ".polish" before the feature-name.
	 * 
	 * @param name The feature which should be added.
	 */
	public void addDirectFeature( String name ) {
		name = name.toLowerCase();
		this.features.put( name, Boolean.TRUE );
	}

	/**
	 * Checks if this component has a specific feature.
	 * A feature is a capability without a value.
	 * 
	 * @param name the feature which should be defined, e.g. "hardware.camera"
	 * @return true when this feature is defined.
	 */
	public boolean hasFeature(String name) {
		if ("supportsPolishGui".equals(name)) {
			return this.supportsPolishGui;
		}
		name = name.toLowerCase();
		boolean hasFeature = this.features.get(name) != null;
		if (!hasFeature) {
			hasFeature = this.features.get("polish." + name) != null;
		}
		return hasFeature;
	}

	/**
	 * Determines whether this device supports the polish-gui-framework.
	 * Usually this is the case when the device meets some capabilities like
	 * the bits per pixel and the possible size of the heap.
	 * Devices can also define this directly by setting the attribute [supportsPolishGui]. 
	 * 
	 * @return true when this device supports the polish-gui.
	 */
	public boolean supportsPolishGui() {
		return this.supportsPolishGui;
	}
	
	/**
	 * Retrieves a specific capability of this component.
	 * 
	 * @param name the name of the capability. 
	 * @return the value of the capability or null when the given capability is not defined.
	 */
	public String getCapability(String name) {
		name = name.toLowerCase();
		String capability = (String) this.capabilities.get( name );
		if (capability == null) {
			capability = (String) this.capabilities.get( "polish." + name );
		}
		return capability;
	}

	/**
	 * Retrieves the identifier or name of this component.
	 * 
	 * @return The identifier of this component, e.g. "Nokia". 
	 */
	public String getIdentifier() {
		return this.identifier;
	}
	
	/**
	 * Retrieves the description of this component.
	 * 
	 * @return the description or null, when none has been defined
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Retrieves the defined features of this component.
	 * 
	 * @return the comma separated features which have been set in the appropriate component.xml file
	 */
	public String getFeaturesAsString() {
		return this.featuresAsString;
	}
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		if (this.identifier != null && o instanceof PolishComponent) {
			String otherIdentifier = ((PolishComponent)o).identifier;
			if (otherIdentifier != null) {
				return this.identifier.compareTo(otherIdentifier);
			//} else {
			//	System.out.println("Other's identifier == null!");
			}
		}
		//System.out.println("Unable to compare  " + getClass().getName() + " with " + o.getClass().getName() + ": this.identifier == null: " + (this.identifier == null) );
		return 0;
	}
    
    public String toString() {
        if(this.identifier != null) {
            return this.identifier;
        }
        return super.toString();
    }
}
