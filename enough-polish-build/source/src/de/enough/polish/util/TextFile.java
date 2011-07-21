/*
 * Created on 24-Feb-2004 at 21:29:57.
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
package de.enough.polish.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * <p>Handles text files.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        24-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class TextFile {
	
	private String fileName;
	private String filePath;
	private String baseDir;
	private File file;
	private long lastModified;
	private String[] content;
	private ResourceUtil resourceUtil;

	/**
	 * Creates a new text file.
	 * 
	 * @param baseDir The name of the source directory
	 * @param filePath The name and relative path of the text file
	 * @throws FileNotFoundException when the file does not exist.
	 */
	public TextFile( String baseDir, String filePath ) 
	throws FileNotFoundException 
	{
		this.baseDir = baseDir;
		this.filePath = filePath;
		updateFile();
		this.lastModified = this.file.lastModified();
		if (!this.file.exists()) {
			throw new FileNotFoundException("The file [" + this.file.getAbsolutePath() + "] does not exist.");
		}
	}
	
	/**
	 * Creates a new TextFile which can be loaded from the JAR file.
	 *  
	 * @param baseDir the directory within the jar file
	 * @param filePath the name of the file
	 * @param lastModificationTime the modification time of the corresponding jar
	 * @param resourceUtil the jar-loader utility
	 */
	public TextFile(String baseDir, 
			String filePath, 
			long lastModificationTime, 
			ResourceUtil resourceUtil) 
	{
		this.resourceUtil = resourceUtil;
		this.lastModified = lastModificationTime;
		this.filePath = filePath;
		this.baseDir = baseDir;
		int index = this.filePath.lastIndexOf( '/' );
		if (index == -1) {
			index = this.filePath.lastIndexOf( '\\' );
		}
		if (index == -1) {
			this.fileName = this.filePath;
		} else {
			this.fileName = this.filePath.substring( index + 1 );
		}
	}

	/**
	 * @return Returns the name and relative path of this text file.
	 */
	public String getFilePath() {
		return this.filePath;
	}
	

	/**
	 * @return Returns the time of last modification.
	 */
	public long lastModified() {
		return this.lastModified;
	}

	/**
	 * Retrieves the content of this text file.
	 * When the content has not been loaded yet, it will be loaded now.
	 * 
	 * @return Returns a copy the content of this text file.
	 * @throws IOException when the file could not be read.
	 */
	public String[] getContent() 
	throws IOException 
	{
		if (this.content == null ) {
			if (this.resourceUtil != null) {
				this.content = this.resourceUtil.readTextFile( this.baseDir, this.filePath );
			} else {
				this.content = FileUtil.readTextFile( this.file );
			}
		}
		String[] copy = (String[]) this.content.clone();
		return copy;
	}
	
	/**
	 * @param content the content of this text file.
	 */
	public void setContent(String[] content) {
		//System.out.println("TextFile [" + this.fileName + "] content is now set");
		this.content = content;
		throw new RuntimeException();
	}

	/**
	 * @return Returns the base directory of this file.
	 */
	public String getBaseDir() {
		return this.baseDir;
	}
	
	/**
	 * @param baseDir the base directory of this file
	 */
	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
		updateFile();
	}

	/**
	 * @return Returns the File instance.
	 */
	public File getFile() {
		return this.file;
	}
	
	/**
	 * Saves this text file.
	 * 
	 * @throws IOException when the saving failed.
	 */
	public void save() 
	throws IOException 
	{
		FileUtil.writeTextFile(this.file, this.content);
		updateFile();
		this.lastModified = this.file.lastModified();
	}
	
	/**
	 * Saves the text file to a different base directory.
	 * 
	 * @param targetDir The directory to which this file should be saved.
	 * @throws IOException when the saving failed.
	 */
	public void saveToDir( String targetDir ) 
	throws IOException 
	{
		saveToDir( targetDir, this.content, false );
	}

	/**
	 * Saves the text file to a different base directory.
	 * 
	 * @param targetDir The directory to which this file should be saved.
	 * @param lines The content which should be saved.
	 * @throws IOException when the saving failed.
	 */
	public void saveToDir( String targetDir, String[] lines ) 
	throws IOException 
	{
		saveToDir( targetDir, lines, false );
	}

	/**
	 * Saves the text file to a different base directory.
	 * 
	 * @param targetDir The directory to which this file should be saved.
	 * @param lines The content which should be saved.
	 * @param update True when the targetDir should be used as the new base directory,
	 *        and when the content of this file should be updated.
	 * @throws IOException when the saving failed.
	 */
	public void saveToDir(String targetDir, String[] lines, boolean update) 
	throws IOException 
	{
		if (update) {
			//System.out.println("TextFile [" + this.fileName + "] is now UPDATED");
			this.content = lines;
			this.baseDir = targetDir;
			updateFile();
			save();
		} else {
			File targetFile = new File( targetDir + File.separatorChar + this.filePath );
			FileUtil.writeTextFile(targetFile, lines);
		}
	}

	/**
	 * Resets the file.
	 */
	private void updateFile() {
		int index = this.filePath.lastIndexOf( '/' );
		if (index == -1) {
			index = this.filePath.lastIndexOf( '\\' );
		}
		if (index == -1) {
			this.fileName = this.filePath;
		} else {
			this.fileName = this.filePath.substring( index + 1 );
		}
		this.file = new File( this.baseDir + File.separatorChar + this.filePath );
	}

	/**
	 * Retrieves the target file for this text file (in a different folder).
	 * 
	 * @param baseDirectory the base directory for the target file
	 * @return the target file
	 */
	public File getTargetFile( File baseDirectory) {
		return new File( baseDirectory, this.filePath );
	}

	/**
	 * Retrieves the name of this text file.
	 * 
	 * @return the name of this text file
	 */
	public String getFileName() {
		return this.fileName;
	}
	
	/**
	 * Retrieves the name of the Java class which is defined by this source code.
	 * 
	 * @return the name of the Java class
	 */
	public String getClassName() {
		if ( ! this.fileName.endsWith(".java")) {
			return null;
		}
		String className = this.filePath.substring(0, this.filePath.length() - ".java".length()  );
		//className = className.replace( File.separatorChar, '.');
		className = className.replace( '/', '.');
		return className;
	}

}
