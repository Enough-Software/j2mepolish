/*
 * Created on 23-May-2004 at 23:04:35.
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

import de.enough.polish.exceptions.InvalidComponentException;
import de.enough.polish.util.StringUtil;

import org.jdom.Attribute;
import org.jdom.Element;

import java.io.File;
import java.util.Map;

/**
 * <p>Represents a single library.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        23-May-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Library extends PolishComponent {
	/** This library should be added to the classpath */
	public static final int POSITION_CLASSPATH = 0;
	/** This library should be preppended to the bootclasspath */
	public static final int POSITION_BOOTCP_PREPPEND = 1;
	/** This library should be appended to the bootclasspath */
	public static final int POSITION_BOOTCP_APPEND = 2;
	private final String fullName;
	//private final String description;
	private final String symbol;
	private final String[] names;
	private String[] fileNames;
	private String defaultPath;
	private File wtkLibPath;
	private File projectLibPath;
	private File polishLibPath;
	private String path;
	private boolean isInitialised;
	private final Map antProperties;
	private final String[] symbols;
	private final boolean hasPackage;
	private final int position;

	/**
	 * Creates a new library.
	 * 
	 * @param antProperties all properties which have been defined in Ant
	 * @param wtkLibPath the path to the lib-folder of the wireless toolkit
	 * @param projectLibPath the path to the lib-folder of the current project
	 * @param polishLibPath the path to the "import" folder of the J2ME Polish installation
	 * @param definition the xml definition of this library
	 * @param manager the library manager
	 * @throws InvalidComponentException when the given api definition has errors
	 */
	public Library( Map antProperties, 
			File wtkLibPath, 
			File projectLibPath,
			File polishLibPath,
			Element definition,
			LibraryManager manager) 
	throws InvalidComponentException 
	{
		super( definition );
		this.antProperties = antProperties;
		this.wtkLibPath = wtkLibPath;
		this.polishLibPath = polishLibPath;
		this.projectLibPath = projectLibPath;
		this.fullName = definition.getChildTextTrim( "name");
		this.identifier = this.fullName;
		Attribute hasPackageAttr = definition.getAttribute("hasPackage");
		this.hasPackage = hasPackageAttr == null || !"false".equals(hasPackageAttr.getValue());
		if (this.fullName == null) {
			throw new InvalidComponentException("An api listed in apis.xml does not define its name. Please insert the <name> element into the file [apis.xml] for this library.");
		}
		//this.description = definition.getChildTextTrim( "description");
		String namesString = definition.getChildTextTrim( "names");
		if (namesString == null) {
			throw new InvalidComponentException("The api [" + this.fullName + "] does not define the possible names of this library. Please insert the <names> element into the file [apis.xml] for this library.");
		}
		this.names = StringUtil.splitAndTrim( namesString, ',' );
		this.symbol = definition.getChildTextTrim( "symbol");
		if (this.symbol == null) {
			throw new InvalidComponentException("The api [" + this.fullName + "] does not define the preprocessing symbol for this library. Please insert the <symbol> element into the file [apis.xml] for this library.");
		}
		String parentLibraryName = definition.getChildTextTrim( "parent");
		if (parentLibraryName != null) {
			Library parentLib = manager.getLibrary(parentLibraryName);
			if (parentLib == null) {
				throw new InvalidComponentException("The library [" + this.fullName + "] extends the unknown library [" + parentLibraryName + "]. Please make sure that the parent-library is defined above in the file [apis.xml].");
			}
			String[] parentSymbols = parentLib.getSymbols();
			this.symbols = new String[ parentSymbols.length + 1 ];
			for (int i = 0; i < parentSymbols.length; i++) {
				this.symbols[ i ] = parentSymbols[i];
			}
			this.symbols[ parentSymbols.length ] = this.symbol;
			this.parent = parentLib; 
		} else {
			this.symbols = new String[]{ this.symbol };
		}
		String fileNamesString = definition.getChildTextTrim( "files");
		if (fileNamesString != null) {
			this.fileNames = StringUtil.splitAndTrim( fileNamesString, ',' );
		}
		String positionStr = definition.getChildTextTrim( "position");
		if (positionStr == null || "classpath".equals( positionStr)) {
			this.position = POSITION_CLASSPATH;
		} else if ("bootclasspath/p".equals( positionStr)) {
			this.position = POSITION_BOOTCP_PREPPEND;
		} else if ("bootclasspath/a".equals( positionStr) || "bootclasspath".equals( positionStr)) {
			this.position = POSITION_BOOTCP_APPEND;
		} else {
			throw new InvalidComponentException("The library [" + this.fullName + "] used the unsupported position [" + positionStr + "]. Use either \"classpath\", \"bootclasspath/p\" or \"bootclasspath/a\" in the <position> element.");
		}
		this.defaultPath = definition.getChildTextTrim( "path");
		// try and find this library only when it is used
		// (compare method findPath())

		String featuresStr = definition.getChildTextTrim("features");
		if (featuresStr != null) {
			String[] features = StringUtil.splitAndTrim(featuresStr, ',');
			for (int i = 0; i < features.length; i++) {
				String feature = features[i];
				addFeature(feature);
			}
		}
		loadCapabilities(definition, this.symbol, "apis.xml");
	}
	
	/**
	 * @param fullName
	 * @param parent
	 */
	public Library(String fullName, Library parent) {
		super( null );
		this.fullName = fullName;
		this.identifier = fullName;
		this.names = parent.names;
		this.symbol = parent.symbol;
		this.position = parent.position;
		this.description = parent.description;
		this.hasPackage = parent.hasPackage;
		this.symbols = parent.symbols;
		this.antProperties = parent.antProperties;
	}

	/**
	 * Retrieves the path for this library
	 * @return either the path for this library or null when it could
	 * 			not be resolved
	 */
	public String getPath() {
		if (!this.hasPackage) {
			return null;
		}
		if (!this.isInitialised) {
			findPath();
			this.isInitialised = true;
		}
		return this.path;
	}
	
	/**
	 * Tries to find the path for this library
	 */
	private void findPath() {
		// 1. try default path:
		if (this.defaultPath != null) {
			File libFile = new File( this.defaultPath );
			if (libFile.exists()) {
				this.path = this.defaultPath;
				return;
			}
            // This if is needed as polishLibPath may be null.
            if(this.polishLibPath != null) {
                libFile = new File( this.polishLibPath.getParent(), this.defaultPath );
                if (libFile.exists()) {				
                    this.path = libFile.getAbsolutePath();
				    return;
                }
            }
		}
		
		// 2. now check if an property has been defined for this api:
		String myPath = (String) this.antProperties.get( "polish.api." + this.symbol );
		if (myPath != null) {
			File libFile = new File( myPath );
			if (libFile.exists()) {
				this.path = libFile.getAbsolutePath();
				return;
			}
		}
		
		// 3. now check all file-names:
		if (this.fileNames != null) {
			for (int i = 0; i < this.fileNames.length; i++) {
				String fileName = this.fileNames[i];
				// look in the project:
				File libFile = new File( this.projectLibPath, fileName );
				if (libFile.exists()) {
					this.path = libFile.getAbsolutePath();
					return;
				}
				if (this.polishLibPath != null) {
					// look in the J2ME Polish installation:
					libFile = new File( this.polishLibPath, fileName );
					if (libFile.exists()) {
						this.path = libFile.getAbsolutePath();
						return;
					}		
				}
				// look in the wtk:
				libFile = new File( this.wtkLibPath, fileName );
				if (libFile.exists()) {
					this.path = libFile.getAbsolutePath();
					return;
				}
			}
		}
		
		// 4. now try the library names:
		for (int i = 0; i < this.names.length; i++) {
			
			// first look for jar files:
			String name = this.names[i] + ".jar";
			// look in the project:
			File libFile = new File( this.projectLibPath, name );
			if (libFile.exists()) {
				this.path = libFile.getAbsolutePath();
				return;
			}
			// look in the wtk:
			libFile = new File( this.wtkLibPath, name );
			if (libFile.exists()) {
				this.path = libFile.getAbsolutePath();
				return;
			}
			
			// now try zip-files:
			name = this.names[i] + ".zip";
			// look in the project:
			libFile = new File( this.projectLibPath, name );
			if (libFile.exists()) {
				this.path = libFile.getAbsolutePath();
				return;
			}
			// look in the wtk:
			libFile = new File( this.wtkLibPath, name );
			if (libFile.exists()) {
				this.path = libFile.getAbsolutePath();
				return;
			}
		}
		
		if (this.defaultPath != null) {
			System.err.println("Warning: unable to find the library \"" + this.fullName + "\" / \"" + this.defaultPath + "\" on the path. If this leads to problems, please adjust the settings for this library in the file [apis.xml].");
			System.err.println("polish.home=" + this.projectLibPath);
		} else {
			System.err.println("Warning: unable to find the library \"" + this.fullName + "\" on the path. If this leads to problems, please adjust the settings for this library in the file [apis.xml].");
		}
	}
	
	/**
	 * @return Returns the defaultPath.
	 */
	public String getDefaultPath() {
		return this.defaultPath;
	}
	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return this.description;
	}
	/**
	 * @return Returns the fileNames.
	 */
	public String[] getFileNames() {
		return this.fileNames;
	}
	/**
	 * @return Returns the fullName.
	 */
	public String getFullName() {
		return this.fullName;
	}
	/**
	 * @return Returns the names.
	 */
	public String[] getNames() {
		return this.names;
	}
	/**
	 * @return Returns the symbols.
	 */
	public String[] getSymbols() {
		return this.symbols;
	}
	
	/**
	 * @return Returns the symbol.
	 */
	public String getSymbol() {
		return this.symbol;
	}
	
	/**
	 * Retrieves the position of this library inside of the classpath for compilation/obfuscation, etc.
	 * 
	 * @return either POSITION_CLASSPATH, POSITION_BOOTCP_PREPPEND or POSITION_BOOTCP_APPEND.
	 * @see #POSITION_CLASSPATH
	 * @see #POSITION_BOOTCP_APPEND
	 * @see #POSITION_BOOTCP_PREPPEND
	 */
	public int getPosition() {
		return this.position;
	}
	
	
	public String toString() {
		return this.identifier;
	}
	
	/**
	 * Retrieves the parent library, if any.
	 * 
	 * @return the parent library or null
	 */
	public Library getParentLibrary() {
		return (Library) this.parent;
	}

}
