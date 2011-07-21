/*
 * Created on 19-Oct-2004 at 13:47:47.
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
package de.enough.polish.dataeditor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import de.enough.polish.util.FileUtil;

/**
 * <p>Manages a complete data-file/definition with data-types, data-entries and so on.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        19-Oct-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DataManager {
	
	private String definitionName;
	private String description;
	private String dataName;
	private final ArrayList types;
	private final Map typesByName;
	private final ArrayList entries;
	private final Map entriesByName;
	private DataEditorUI gui;
	private boolean definitionDirtyFlag;
	private boolean dataDirtyFlag;
	private File dataFile;
	private File definitionFile;
	private String extension;

	/**
	 * 
	 */
	public DataManager() {
		super();
		this.types = new ArrayList();
		this.typesByName = new HashMap();
		// add default-types:
		DataType[] defaultTypes = DataType.getDefaultTypes();
		for (int i = 0; i < defaultTypes.length; i++) {
			DataType type = defaultTypes[i];
			this.typesByName.put( type.getName(), type );			
		}
		this.entries = new ArrayList();
		this.entriesByName = new HashMap();
	}
	
	public void loadDefinition( File file ) 
	throws JDOMException, IOException
	{
		this.definitionName = file.getName();
		FileInputStream in = new FileInputStream( file );
		SAXBuilder builder = new SAXBuilder( false );
		Document document = builder.build( in );
		Element root = document.getRootElement();
		if ( !root.getName().equals("data-definition") ) {
			throw new JDOMException( "Invalid definition-file: the root-element needs to be <data-definition>.");
		}
		clear();
		// read description:
		this.description = root.getChildTextTrim("description");
		this.extension = root.getChildTextTrim("extension");
		// init user-defined data-types:
		List typesList = root.getChildren("type");
		for (Iterator iter = typesList.iterator(); iter.hasNext();) {
			Element typeElement = (Element) iter.next();
			DataType type = new DataType( typeElement, this );
			addDataType( type );
		}
		// init data-entries:
		List entriesList = root.getChildren("entry");
		for (Iterator iter = entriesList.iterator(); iter.hasNext();) {
			Element entryElement = (Element) iter.next();
			DataEntry entry = new DataEntry( entryElement, this );
			addDataEntry( entry );
		}
		this.definitionDirtyFlag = false;
		if (this.gui != null) {
			this.gui.signalUnchangedDefinition();
		}
		this.definitionFile = file;
	}
	
	public void saveDefinition( File file )
	throws IOException
	{
		this.definitionName = file.getName();
		ArrayList linesList = new ArrayList();
		// save start of file:
		linesList.add( "<!-- created by J2ME Polish on " + (new Date()).toString() + " -->" );
		linesList.add( "<data-definition>" );
		// save description:
		if (this.description != null) {
			linesList.add( "\t<description>" + this.description + "</description>" );
		}
		if (this.extension != null) {
			linesList.add( "\t<extension>" + this.extension + "</extension>" );			
		}
		// save user-defined types:
		DataType[] dataTypes = getUserDefinedTypes();
		for (int i = 0; i < dataTypes.length; i++) {
			DataType type = dataTypes[i];
			linesList.add( type.getXmlRepresentation() );
		}
		// save data-entries:
		DataEntry[] dataEntries = getDataEntries();
		for (int i = 0; i < dataEntries.length; i++) {
			DataEntry entry = dataEntries[i];
			linesList.add( entry.getXmlRepresentation() );
		}
		// save end of file:
		linesList.add( "</data-definition>" );
		String[] lines = (String[]) linesList.toArray( new String[ linesList.size() ] );
		FileUtil.writeTextFile( file, lines );
		this.definitionDirtyFlag = false;
		if (this.gui != null) {
			this.gui.signalUnchangedDefinition();
		}
		this.definitionFile = file;
	}
	

	public void saveData( File file )
	throws IOException
	{
		this.dataName = file.getName();
		DataOutputStream out = new DataOutputStream( new FileOutputStream( file ) );
		DataEntry[] dataEntries = getDataEntries();
		for (int i = 0; i < dataEntries.length; i++) {
			DataEntry entry = dataEntries[i];
			if (entry.getCount() > 0) {
				entry.saveData( out );
			}
		}
		out.close();
		this.dataDirtyFlag = false;
		if (this.gui != null) {
			this.gui.signalUnchangedData();
		}
		this.dataFile = file;
	}

	public void loadData( File file )
	throws IOException
	{
		this.dataName = file.getName();
		DataInputStream in = new DataInputStream( new FileInputStream( file ) );
		DataEntry[] dataEntries = getDataEntries();
		for (int i = 0; i < dataEntries.length; i++) {
			DataEntry entry = dataEntries[i];
			entry.loadData(in);
		}
		this.dataDirtyFlag = false;
		if (this.gui != null) {
			this.gui.signalUnchangedData();
		}
		this.dataFile = file;
	}
	
	private void definitionChanged() {
		if (! this.definitionDirtyFlag) {
			this.definitionDirtyFlag = true;
			if (this.gui != null) {
				this.gui.signalChangedDefinition();
			}
		}
	}
	
	private void dataChanged() {
		if (! this.dataDirtyFlag ) {
			this.dataDirtyFlag = true;
			if (this.gui != null) {
				this.gui.signalChangedData();
			}
		}
	}
	
	public void addDataEntry( DataEntry entry ) {
		this.entries.add( entry );
		this.entriesByName.put( entry.getName(), entry );
		definitionChanged();
	}

	public void insertDataEntry( int index, DataEntry entry ) {
		this.entries.add( index , entry );
		definitionChanged();
	}
	
	public boolean removeDataEntry( DataEntry entry ) {
		this.entriesByName.remove( entry.getName() );
		definitionChanged();
		return this.entries.remove( entry );
	}
	
	public DataEntry removeDataEntry( int index ) {
		DataEntry entry = (DataEntry) this.entries.remove(index);
		if (entry != null) {
			this.entriesByName.remove( entry.getName() );
			definitionChanged();
		}
		return entry;
	}
	
	public DataEntry[] getDataEntries() {
		return (DataEntry[]) this.entries.toArray( new DataEntry[ this.entries.size() ] ); 
	}
	
	public DataEntry getDataEntry( String name ) {
		DataEntry[] dataEntries = getDataEntries();
		for (int i = 0; i < dataEntries.length; i++) {
			DataEntry entry = dataEntries[i];
			if ( name.equals( entry.getName() )) {
				return entry;
			}
		}
		return null;
	}
	
	public CountTerm createCountTerm( String term ) {
		return CountTerm.createTerm( term, this );
	}
	
	public void addDataType( DataType type ) {
		this.types.add( type );
		this.typesByName.put( type.getName(), type );
	}

	/**
	 * Retrieves a data type by its name.
	 * 
	 * @param name the name of the data type
	 * @return the data type
	 */
	public DataType getDataType(String name ) {
		return (DataType) this.typesByName.get( name );
	}
	
	public String getDefinitionName() {
		return this.definitionName;
	}
	
	public String getDataName() {
		return this.dataName;
	}

	/**
	 * Retrieves the user-defined data types.
	 * 
	 * @return an array of data types
	 */
	public DataType[] getUserDefinedTypes() {
		return (DataType[]) this.types.toArray( new DataType[ this.types.size() ] );
	}

	/**
	 * Retrieves the number of entries managed by this manager.
	 * 
	 * @return the number of entries in this manager
	 */
	public int getNumberOfEntries() {
		return this.entries.size();
	}

	/**
	 * Retrieves a specific data entry given by an index.
	 * 
	 * @param index the index
	 * @return the data entry
	 */
	public DataEntry getDataEntry(int index) {
		return (DataEntry) this.entries.get( index );
	}

	/**
	 * Clears the manager.
	 */
	public void clear() {
		this.types.clear();
		this.entries.clear();
		this.entriesByName.clear();
		this.typesByName.clear();
		// add default-types:
		// add default-types:
		DataType[] defaultTypes = DataType.getDefaultTypes();
		for (int i = 0; i < defaultTypes.length; i++) {
			DataType type = defaultTypes[i];
			this.typesByName.put( type.getName(), type );			
		}
		this.dataName = null;
		this.definitionName = null;
		this.dataDirtyFlag = false;
		this.definitionDirtyFlag = false;
	}

	public DataType[] getDataTypes() {
		if (this.types == null) {
			return DataType.getDefaultTypes();
		}
		ArrayList list = new ArrayList();
		// add default-types:
		DataType[] defaultTypes = DataType.getDefaultTypes();
		for (int i = 0; i < defaultTypes.length; i++) {
			DataType type = defaultTypes[i];
			list.add( type );			
		}
		// add user-defined types:
		list.addAll( this.types );
		return (DataType[]) list.toArray( new DataType[ list.size() ] );
	}

	/**
	 * Moves the specified data entry one up.
	 *  
	 * @param index the index of the data entry
	 * @return true when the entry could be pushed upwards
	 */
	public boolean pushUpDataEntry(int index) {
		if ( index > 0 ) {
			DataEntry entry = (DataEntry) this.entries.remove(index);
			if (entry != null) {
				this.entries.add( --index, entry );
				return true;
			}
		}
		return false;
	}

	/**
	 * Moves the specified data entry one down.
	 *  
	 * @param index the index of the data entry
	 * @return true when the entry could be pushed downwards
	 */
	public boolean pushDownDataEntry(int index) {
		if ( index < this.entries.size() - 1 ) {
			DataEntry entry = (DataEntry) this.entries.remove(index);
			if (entry != null) {
				this.entries.add( ++index, entry );
				return true;
			}
		}
		return false;
	}
	
	public String generateJavaCode( String packageName, String className ) {
		StringBuffer buffer = new StringBuffer( this.entries.size() * 2 * 100 );
		// add class-declaration:
		buffer.append("// Generated by J2ME Polish at ").append( new Date().toString() ).append('\n');
		if (packageName != null && packageName.length() > 0) {
			buffer.append("package ").append( packageName ).append(";\n");
		}
		buffer.append("import java.io.*;\n");
		buffer.append("public final class ").append( className ).append(" {\n");
		// add field-definitions:
		DataEntry[] myEntries = getDataEntries();
		for (int i = 0; i < myEntries.length; i++) {
			DataEntry entry = myEntries[i];
			entry.addInstanceDeclaration(buffer);
		}
		// add the constructor:
		buffer.append("\n\tpublic ").append( className ).append("( String dataUrl )\n");
		buffer.append("\tthrows IOException\n\t{\n");
		buffer.append("\t\tInputStream plainIn = getClass().getResourceAsStream( dataUrl );\n");
		// check if resource really exists:
		buffer.append("\t\tif (plainIn == null) {\n" )
			.append("\t\t\tthrow new IllegalArgumentException(\"Unable to open resource [\" + dataUrl + \"]: resource not found: does it start with \\\"/\\\"?\");\n")
			.append("\t\t}\n");
		// create DataInputStream:
		buffer.append("\t\tDataInputStream in = new DataInputStream( plainIn );\n");
		buffer.append("\t\ttry {\n");
			
		// add the loading of the data:
		for (int i = 0; i < myEntries.length; i++) {
			DataEntry entry = myEntries[i];
			entry.addCode(buffer);
		}
		// add exception handling:
		buffer.append("\t\t} catch (IOException e) {\n")
			.append("\t\t\tthrow e;\n")
			.append("\t\t} catch (Exception e) {\n")
			.append("\t\t\t//#debug error\n")
			.append("\t\t\tSystem.out.println(\"Unable to load data\" + e);\n")
			.append("\t\t\tthrow new IOException( e.toString() );\n")
			.append("\t\t} finally {\n")
			.append("\t\t\ttry {\n")
			.append("\t\t\t\tin.close();\n")
			.append("\t\t\t} catch (Exception e) {\n")
			.append("\t\t\t\t//#debug error\n")
			.append("\t\t\t\tSystem.out.println(\"Unable to close input stream\" + e);\n")
			.append("\t\t\t}\n")
			.append("\t\t}\n");
		// add constructor-end:
		buffer.append("\t} // end of constructor \n\n");
		// add inner classes:
		HashMap internalClassByName = new HashMap();
		for (int i = 0; i < myEntries.length; i++) {
			DataEntry entry = myEntries[i];
			entry.getType().addInternalClass(internalClassByName, buffer);
		}		
		buffer.append("\n} // end of class\n");
		return buffer.toString();
	}

	/**
	 * Retrieves possible operants for a count-term for the specified data entry.\
	 *  
	 * @param index the index of the entry for which operants are searched
	 * @return an array of possible operants, can be empty but not null
	 */
	public DataEntry[] getCountOperants(int index) {
		ArrayList operants = new ArrayList( index );
		for (int i = 0; i < index; i++) {
			DataEntry entry = (DataEntry) this.entries.get( i );
			if (entry.getCount() == 1) {
				operants.add( entry );
			}
		}
		return (DataEntry[]) operants.toArray( new DataEntry[ operants.size() ]);
	}
	
	/**
	 * Register the user interface.
	 * 
	 * @param dataEditorUI the GUI implementation
	 */
	public void registerUI( DataEditorUI dataEditorUI ) {
		this.gui = dataEditorUI;
	}
	
	public boolean isDataChanged() {
		return this.dataDirtyFlag;
	}
	
	public boolean isDefinitionChanged() {
		return this.definitionDirtyFlag;
	}
	
	public void setDataAsString( String data, DataEntry entry ) {
		entry.setDataAsString(data);
		dataChanged();
	}

	public void setDataAsString( String[] data, DataEntry entry ) {
		entry.setDataAsString(data);
		dataChanged();
	}
	
	public void setCountAsString( String count, DataEntry entry ) {
		entry.setCount(count, this);
		definitionChanged();
	}
	
	public void setEntryName( String name, DataEntry entry ) {
		entry.setName(name);
		definitionChanged();
	}
	
	public void setEntryType( DataType type, DataEntry entry ) {
		entry.setType(type);
		definitionChanged();
	}
	
	public void setDescription( String description ) {
		this.description = description;
		definitionChanged();
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setExtension( String extension ) {
		if (extension != null) {
			if (!extension.startsWith(".")) {
				extension = "." + extension;
			}
		}
		this.extension = extension;
		definitionChanged();
	}
	
	public String getExtension() {
		return this.extension;
	}

	/**
	 * Sets the data of an entry directly.
	 * 
	 * @param data the data-object for the entry.
	 * @param entry the DataEntry
	 */
	public void setData(Object data, DataEntry entry) {
		entry.setData( data );
		dataChanged();
	}

	/**
	 * Retrieves the current directory.
	 * This is the directory into which the data is stored,
	 * or when no data has been loaded/saved, the directory from
	 * which the definition was loaded/saved.
	 * When no definition has been loaded or saved, the current directory
	 * for this application is returned.
	 * 
	 * @return the current directory
	 */
	public File getCurrentDirectory() {
		if (this.dataFile != null) {
			return this.dataFile.getParentFile();
		} else if (this.definitionFile != null) {
			return this.definitionFile.getParentFile();
		} else {
			return new File(".");
		}
	}

}
