/*
 * Created on 23-Jan-2003 at 08:09:52.
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
package de.enough.polish.ant.info;


import de.enough.polish.BuildException;

import de.enough.polish.Attribute;
import de.enough.polish.Environment;

import java.util.ArrayList;

/**
 * <p>Represents the info-section of a polish project.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        23-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class InfoSetting {
	
	// REQUIRED ATTRIBUTES
	// J=Jad, M=Manifest
	public static final String MIDLET_NAME = "MIDlet-Name"; // J & M
	public static final String MIDLET_VERSION = "MIDlet-Version"; // J & M
	public static final String MIDLET_VENDOR = "MIDlet-Vendor"; // J & M
	public static final String MIDLET_JAR_URL = "MIDlet-Jar-URL"; // J
	public static final String MIDLET_JAR_SIZE = "MIDlet-Jar-Size"; // J
	public static final String MICRO_EDITION_PROFILE = "MicroEdition-Profile"; // M
	public static final String MICRO_EDITION_CONFIGURATION = "MicroEdition-Configuration"; // M
	
	// OPTIONAL ATTRIBUTES
	public static final String MIDLET_ICON = "MIDlet-Icon"; // J & M
	public static final String MIDLET_DESCRIPTION = "MIDlet-Description"; // J & M
	public static final String MIDLET_INFO_URL = "MIDlet-Info-URL"; // J & M
	public static final String MIDLET_DATA_SIZE = "MIDlet-Data-Size"; // J & M
	public static final String MIDLET_DELETE_CONFIRM = "MIDlet-Delete-Confirm"; // J & M
	public static final String MIDLET_DELETE_NOTIFY = "MIDlet-Delete-Notify"; // J & M
	public static final String MIDLET_INSTALL_NOTIFY = "MIDlet-Install-Notify"; // J & M

	public static final String MIDLET_PERMISSIONS = "MIDlet-Permissions"; // J & M 	
	public static final String MIDLET_OPTIONAL_PERMISSIONS = "MIDlet-Permissions-Opt"; // J & M
	
	// For a list of Midlet-N attributes see 
	// http://java.sun.com/j2me/docs/wtk2.0/user_html/Ap_Attributes.html#wp21956
	public static final String NMIDLET = "MIDlet-";
	
	public static final String CLDC1_0 = "CLDC-1.0";
	public static final String CLDC1_1 = "CLDC-1.1";
	public static final String MIDP1 = "MIDP-1.0";
	public static final String MIDP2 = "MIDP-2.0";
	
	private String name;
	private String version;
	private String description;
	private String infoUrl;
	private String icon;
	private String jarUrl;
	private String copyright;
	private String deleteConfirm;
	private String deleteNotify;
	private String installNotify;
	private Author[] authors;
	private VendorInfo vendorInfo;
	private String dataSize;
	private ArrayList manifestAttributes;
	private ArrayList jadAttributes;
	private String vendorName;
	private String jarName;
	private String profile;
	private String configuration;
	
	/**
	 * Creates a new InfoSetting
	 */
	public InfoSetting() {
		this.manifestAttributes = new ArrayList();
		this.jadAttributes = new ArrayList();
	}

	/**
	 * @return Returns the copyright.
	 */
	public String getCopyright() {
		return this.copyright;
	}

	/**
	 * @param copyright The copyright to set.
	 */
	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	/**
	 * @return Returns the deleteConfirm.
	 */
	public String getDeleteConfirm() {
		return this.deleteConfirm;
	}

	/**
	 * @param deleteConfirm The deleteConfirm to set.
	 */
	public void setDeleteConfirm(String deleteConfirm) {
		Attribute var = new Attribute( MIDLET_DELETE_CONFIRM, deleteConfirm );
		this.manifestAttributes.add( var );
		this.jadAttributes.add( var );
		this.deleteConfirm = deleteConfirm;
	}

	/**
	 * @return Returns the deleteNotify.
	 */
	public String getDeleteNotify() {
		return this.deleteNotify;
	}

	/**
	 * @param deleteNotify The deleteNotify to set.
	 */
	public void setDeleteNotify(String deleteNotify) {
		Attribute var = new Attribute( MIDLET_DELETE_NOTIFY, deleteNotify );
		this.manifestAttributes.add( var );
		this.jadAttributes.add( var );
		this.deleteNotify = deleteNotify;
	}

	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		Attribute var = new Attribute( MIDLET_DESCRIPTION, description );
		this.manifestAttributes.add( var );
		this.jadAttributes.add( var );
		this.description = description;
	}

	/**
	 * @return Returns the icon.
	 */
	public String getIcon() {
		return this.icon;
	}

	/**
	 * @param icon The icon to set.
	 */
	public void setIcon(String icon) {
		if ( icon == null || icon.length() == 0) {
			this.icon = null;
			return;
		}
		if (!icon.startsWith("/")) {
			icon = "/" + icon;
		}
		Attribute var = new Attribute( MIDLET_ICON, icon );
		this.manifestAttributes.add( var );
		this.jadAttributes.add( var );
		this.icon = icon;
	}

	/**
	 * @return Returns the infoUrl.
	 */
	public String getInfoUrl() {
		return this.infoUrl;
	}

	/**
	 * @param infoUrl The infoUrl to set.
	 */
	public void setInfoUrl(String infoUrl) {
		Attribute var = new Attribute( MIDLET_INFO_URL, infoUrl );
		this.manifestAttributes.add( var );
		this.jadAttributes.add( var );
		this.infoUrl = infoUrl;
	}

	/**
	 * @return Returns the installNotify.
	 */
	public String getInstallNotify() {
		return this.installNotify;
	}

	/**
	 * @param installNotify The installNotify to set.
	 */
	public void setInstallNotify(String installNotify) {
		Attribute var = new Attribute( MIDLET_INSTALL_NOTIFY, installNotify );
		this.manifestAttributes.add( var );
		this.jadAttributes.add( var );
		this.installNotify = installNotify;
	}

	/**
	 * Retrieves the URL of the Jar-File.
	 *  
	 * The URL can contain several properties. Following properties are allowed:
	 * <ul>
	 * 	<li><b>polish.identifier</b>: The vendor and name of the current device, 
	 * 		   e.g. "Nokia/3650". 
	 * 		   Note that the identifier contains a slash, which might result 
	 * 		   in undesired behaviour (since it might point to a different folder).</li>
	 * 	<li><b>polish.name</b>: The name of the current device, e.g. "3650".</li>
	 * 	<li><b>polish.vendor</b>: The name of the vendor of the current device, e.g. "Nokia".</li>
	 * 	<li><b>polish.version</b>: The version of the project as defined in the attribute [version].</li>
	 * 	<li><b>polish.jarName</b>: The name of the jar-file as defined in the attribute [jarName].</li>
	 * </ul>
	 * 
	 * @return Returns the URL of the jar-file.
	 */
	public String getJarUrl() {
		if (this.jarUrl == null) {
			return this.jarName;
		} else {
			return this.jarUrl;
		}
	}

	/**
	 * Sets the URL of the Jar-File.
	 *  
	 * The URL can contain several properties. Following properties are allowed:
	 * <ul>
	 * 	<li><b>polish.identifier</b>: The vendor and name of the current device, 
	 * 		   e.g. "Nokia/3650". 
	 * 		   Note that the identifier contains a slash, which might result 
	 * 		   in undesired behaviour (since it might point to a different folder).</li>
	 * 	<li><b>polish.name</b>: The name of the current device, e.g. "3650".</li>
	 * 	<li><b>polish.vendor</b>: The name of the vendor of the current device, e.g. "Nokia".</li>
	 * 	<li><b>polish.version</b>: The version of the project as defined in the attribute [version].</li>
	 * 	<li><b>polish.jarName</b>: The name of the jar-file as defined in the attribute [jarName].</li>
	 * </ul>
	 * 
	 * @param jarUrl The jarUrl to set.
	 */
	public void setJarUrl(String jarUrl) {
		Attribute var = new Attribute( MIDLET_JAR_URL, jarUrl);
		this.jadAttributes.add( var );
		this.jarUrl = jarUrl;
	}

	/**
	 * @return Returns the name of this project.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name The name of this project.
	 */
	public void setName(String name) {
		Attribute var = new Attribute( MIDLET_NAME, name );
		this.manifestAttributes.add( var );
		this.jadAttributes.add( var );
		this.name = name;
	}
	
	public void addConfiguredAuthors( Authors authorsList ) {
		this.authors = authorsList.getAuthors();
		if (this.authors.length == 0) {
			throw new BuildException("The [authors] elements needs to have at least one nested [author] element.");
		}
	}
	
	public Author[] getAuthors() {
		return this.authors;
	}
	
	public void addConfiguredVendorInfo( VendorInfo info ) {
		if (info.getName() == null) {
			throw new BuildException("The <vendorInfo> Element needs to define the attribute [name].");
		}
		Attribute var = new Attribute( MIDLET_VENDOR, info.getName() );
		this.manifestAttributes.add( var );
		this.jadAttributes.add( var );
		
		this.vendorInfo = info;
	}
	
	public VendorInfo getVendorInfo() {
		return this.vendorInfo;
	}
	
	public void setVendorName( String vendorName ) {
		Attribute var = new Attribute( MIDLET_VENDOR, vendorName );
		this.manifestAttributes.add( var );
		this.jadAttributes.add( var );
		this.vendorName = vendorName;
	}
	
	public String getVendorName() {
		return this.vendorName;
	}

	/**
	 * @return the size which this midlet needs for the storage of data
	 */
	public String getDataSize() {
		return this.dataSize;
	}

	/**
	 * @param dataSize the size which this midlet needs for the storage of data
	 */
	public void setDataSize(String dataSize) {
		Attribute var = new Attribute( MIDLET_DATA_SIZE, dataSize );
		this.manifestAttributes.add( var );
		this.jadAttributes.add( var );
		this.dataSize = dataSize;
	}
	
	/**
	 * Retrieves all manifest attributes for the JAR file.
	 * 
	 * @param env the environment
	 * @return an Attribute array containing all defined attributes for the JAR-Manifest.
	 */
	public Attribute[] getManifestAttributes( Environment env ) {
		return getAttributes( this.manifestAttributes, env );
	}

	/**
	 * Retrieves all attributes for the JAD file.
	 * 
	 * @param env the environment
	 * @return an Attribute array containing all defined attributes for the JAD-file.
	 */
	public Attribute[] getJadAttributes( Environment env ) {
		return getAttributes( this.jadAttributes, env );
	}
	
	private Attribute[] getAttributes( ArrayList list, Environment env ) {
		Attribute[] attributes = (Attribute[]) list.toArray( new Attribute[ list.size() ] );
		ArrayList newList = new ArrayList( attributes.length );
		
		for (int i = 0; i < attributes.length; i++) {
			Attribute attribute = attributes[i];
			String variableValue = env.getVariable( attribute.getName() );
			if (variableValue == null) { 
				variableValue = attribute.getValue();
			}
			if (variableValue != null) {
				variableValue = env.writeProperties(variableValue);
				if ( variableValue.length() > 0 ) {				
					newList.add( attribute = new Attribute( attribute.getName(), variableValue ) );
				}
			}
		}
		return (Attribute[]) newList.toArray( new Attribute[ newList.size() ] );
	}
	
	/**
	 * @return Returns the midlet-version.
	 */
	public String getVersion() {
		return this.version;
	}
	
	/**
	 * @param version The version to set.
	 */
	public void setVersion(String version) {
		Attribute var = new Attribute( MIDLET_VERSION, version );
		this.manifestAttributes.add( var );
		this.jadAttributes.add( var );
		this.version = version;
	}

	/**
	 * Gets the name of the jar file.
	 * The name can contain several properties. Following properties are allowed:
	 * <ul>
	 * 	<li><b>polish.identifier</b>: The vendor and name of the current device, 
	 * 		   e.g. "Nokia/3650". 
	 * 		   Note that the identifier contains a slash, which might result 
	 * 		   in undesired behaviour (since it might point to a different folder).</li>
	 * 	<li><b>polish.name</b>: The name of the current device, e.g. "3650".</li>
	 * 	<li><b>polish.vendor</b>: The name of the vendor of the current device, e.g. "Nokia".</li>
	 * 	<li><b>polish.version</b>: The version of the project as defined in the attribute [version].</li>
	 * 	<li><b></b>: </li>
	 * </ul>
	 * 
	 * @return The name of this Jar-File.
	 */ 
	public String getJarName() {
		return this.jarName;
	}
	
	/**
	 * Sets the name of the jar file which will be created.
	 * The name can contain several properties. Following properties are allowed:
	 * <ul>
	 * 	<li><b>polish.identifier</b>: The vendor and name of the current device, 
	 * 		   e.g. "Nokia/3650". 
	 * 		   Note that the identifier contains a slash, which might result 
	 * 		   in undesired behaviour (since it might point to a different folder).</li>
	 * 	<li><b>polish.name</b>: The name of the current device, e.g. "3650".</li>
	 * 	<li><b>polish.vendor</b>: The name of the vendor of the current device, e.g. "Nokia".</li>
	 * 	<li><b>polish.version</b>: The version of the project as defined in the attribute [version].</li>
	 * 	<li><b></b>: </li>
	 * </ul>
	 * 
	 * @param jarName The name of the jar file.
	 */
	public void setJarName( String jarName ) {
		this.jarName = jarName;
	}

	/**
	 * Sets the optional permissions of this application.
	 * 
	 * @param optionalPermissions The permissions which are useful for this application to work, 
	 * 			e.g. "javax.microedition.io.Connector.http"
	 */
	public void setOptionalPermissions(String optionalPermissions) {
		Attribute var = new Attribute( MIDLET_OPTIONAL_PERMISSIONS, optionalPermissions );
		this.manifestAttributes.add( var );
		this.jadAttributes.add( var );
	}
	
	/**
	 * Sets the permissions of this application.
	 * 
	 * @param permissions The permissions which are needed for this application to work, 
	 * 			e.g. "javax.microedition.io.Connector.http"
	 */
	public void setPermissions(String permissions) {
		Attribute var = new Attribute( MIDLET_PERMISSIONS, permissions );
		this.manifestAttributes.add( var );
		this.jadAttributes.add( var );
	}

	
	/**
	 * Sets the license of the license key for this project.
	 * 
	 * @param license The license of the created applications, either "GPL" or the license-key for commercial use.
	 */
	public void setLicense(String license) {
		System.out.println("info: the license attribute is no longer supported. Please place your license.key file either to ${project.home} or to ${polish.home}.");
	}
	
	/**
	 * Sets the jarName as the default jarUrl but only when no jarUrl has been specified.
	 */
	public void setDefaultJarUrl() {
		if (this.jarUrl == null) {
			setJarUrl( this.jarName );
		}
	}
	
	public String getConfiguration() {
		return this.configuration;
	}
	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}
	public String getProfile() {
		return this.profile;
	}
	public void setProfile(String profile) {
		this.profile = profile;
	}

}
