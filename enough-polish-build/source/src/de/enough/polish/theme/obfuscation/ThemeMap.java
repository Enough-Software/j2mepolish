//#condition polish.JavaSE && polish.useThemes
package de.enough.polish.theme.obfuscation;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;


/**
 * <p>
 * Reads the lines from an obfuscation file and provides
 * methods to get the obfuscation names for classes and fields 
 * </p>
 * 
 * <p>
 * Copyright (c) 2005, 2006, 2007 Enough Software
 * </p>
 * 
 * <pre>
 * history
 *        13-Dec-2007 - asc creation
 * </pre>
 * 
 * @author Andre Schmidt, andre@enough.de
 */
public class ThemeMap {
	ThemeEntry root;

	/**
	 * Opens a file and reads the lines to <code>lines</code>
	 * @param file the file
	 * @throws IOException
	 */
	public ThemeMap(File file) throws IOException {
		FileInputStream fileStream = null;
		BufferedInputStream bufferedStream = null;
		
		DataInputStream dataStream = null;
		fileStream = new FileInputStream(file);

		bufferedStream = new BufferedInputStream(fileStream);
		dataStream = new DataInputStream(bufferedStream);

		ArrayList lines = new ArrayList();
		
		while (dataStream.available() != 0) {
			lines.add(dataStream.readLine());
		}
		
		fileStream.close();
		bufferedStream.close();
		dataStream.close();
		
		this.root = parseMap(lines);
	}
	
	private ThemeEntry parseMap(ArrayList lines)
	{
		ThemeEntry rootEntry = new ThemeEntry();
		
		rootEntry.setGroup(ThemeEntry.ROOT);
		
		ThemeEntry classEntry = null;
		
		for (int i = 0; i < lines.size(); i++) {
			ThemeEntry entry = parseLine((String)lines.get(i));
			
			switch(entry.getGroup())
			{
				case ThemeEntry.CLASS:
					classEntry = entry;
					rootEntry.getChildren().add(classEntry);
					break;
				case ThemeEntry.METHOD:
				case ThemeEntry.FIELD:
					classEntry.getChildren().add(entry);
					break;
			}
		}
		
		return rootEntry;
	}
	
	private ThemeEntry parseLine(String line)
	{
		ThemeEntry entry = new ThemeEntry();
	
		line = line.trim();
		String[] ids = line.split("->");
		
		if(isClass(line))
		{
			entry.setGroup(ThemeEntry.CLASS);
			
			entry.setType(null);
			entry.setName(ids[0].trim());
			entry.setObfuscated(ids[1].trim().replace(":", "")); 
		}
		else if(isMethod(line))
		{
			entry.setGroup(ThemeEntry.METHOD);
			
			String type = ids[0].trim().split(" ")[0];
			String name = ids[0].trim().split(" ")[1];
			
			entry.setType(type);
			entry.setName(name);
			entry.setObfuscated(ids[1].trim());
		}
		else if(isField(line))
		{
			entry.setGroup(ThemeEntry.FIELD);
			
			String type = ids[0].trim().split(" ")[0];
			String name = ids[0].trim().split(" ")[1];
			
			entry.setType(type);
			entry.setName(name);
			entry.setObfuscated(ids[1].trim());
		}
		
		return entry;
	}
	

	private boolean isClass(String line)
	{
		return line.indexOf(":") != -1 && line.indexOf("->") != -1;
	}
	
	private boolean isField(String line)
	{
		String name = line.split("->")[0].trim();
		return name.indexOf(" ") != -1 && name.indexOf("(") == -1;
	}
	
	private boolean isMethod(String line)
	{
		String name = line.split("->")[0].trim();
		return name.indexOf(" ") != -1 && name.indexOf("(") != -1;
	}
	
	/**
	 * Returns the obfuscated or normal name for <code>className</code>
	 * @param name a full class specifier
	 * @param obfuscated true, if <code>className</code> is an obfuscation name and should be resolved
	 * @return the entry of the class
	 */
	public static ThemeEntry getChildEntry(ThemeEntry entry, String name, boolean obfuscated)
	{
		ArrayList classes = entry.getChildren();
		
		for (int i = 0; i < classes.size(); i++) {
			ThemeEntry child = (ThemeEntry)classes.get(i);
			
			if(obfuscated)
			{
				if(child.getObfuscated().equals(name))
				{
					return child;
				}
			}
			else
			{
				if(child.getName().equals(name))
				{
					return child;
				}
			}
		}
		
		return null;
	}

	public ThemeEntry getRoot() {
		return root;
	}

	public void setRoot(ThemeEntry root) {
		this.root = root;
	}
}
