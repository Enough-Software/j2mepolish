/*
 * Created on 14-Apr-2005 at 22:12:37.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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

import de.enough.polish.BuildException;
import de.enough.polish.Environment;

import de.enough.polish.ant.Setting;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.JarUtil;
import de.enough.polish.util.StringUtil;

/**
 * <p>Allows to include a binary library depending on conditions.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        14-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class LibrarySetting extends Setting {
	
	private File[] files;
	private File cacheDir;
	private boolean libraryChanged;
	private long lastModified;
	private boolean isDynamic;
	private String dynamicLibraryPath;

	/**
	 * Creates a new library binding.
	 */
	public LibrarySetting() {
		super();
	}
	
//	/**
//	 * @return Returns the dir.
//	 */
//	public File getDir() {
//		return this.dir;
//	}

	/**
	 * @param dir The dir to set.
	 */
	public void setDir(File dir) {
		if (!dir.isDirectory()) {
			throw new BuildException("The <library>-dir [" + dir.getAbsolutePath() + "] is a file and not a directory. Please use the \"file\" attribute instead in your build.xml.");
		}
		if (this.files != null) {
			throw new BuildException("You cannot specify both the \"file\" as well as the \"dir\" attribute in one <library>-element. Please correct this in your build.xml.");
		}
		this.files = dir.listFiles();
//		this.dir = dir;
//		this.dirOrFile = dir;
	}

//  /**
//	 * @return Returns the file.
//	 */
//	public File getFile() {
//		return this.file;
//	}

	/**
	 * @param file The file to set.
	 */
	public void setFile(File file) {
		if (file.isDirectory()) {
			throw new BuildException("The <library> file [" + file.getAbsolutePath() + "] is a directory and not a file. Please use the \"dir\" attribute instead in your build.xml.");
		}
		if (this.files != null) {
			throw new BuildException("You cannot specify both the \"file\" as well as the \"dir\" attribute or \"files\" attribute in one <library>-element. Please correct this in your build.xml.");
		}
		if (!file.exists()) {
			String filePath = file.getAbsolutePath();
			if (filePath.indexOf('$') != -1) {
				this.isDynamic = true;
				this.dynamicLibraryPath = filePath;
				return;
			} else {
				throw new BuildException("Unable find library " +filePath + ": file not found. Check your <library> tags in your build.xml script.");
			}
		}
		this.files = new File[]{ file };
		this.lastModified = file.lastModified();
		//this.dirOrFile = file;
	}
	
	public void setFiles( String colonSeparatedPaths ) {
		String[] paths = StringUtil.split(colonSeparatedPaths, ':');
		this.files = new File[ paths.length ];
		for (int i = 0; i < paths.length; i++) {
			String path = paths[i];
			File file = new File( path );
			if ( !(file.exists())) {
				throw new BuildException("Unable find library " + path + ": file not found. Check your <library> tags in your build.xml script.");
			}
			this.files[i] = file;
		}
	}

//	/**
//	 * @return
//	 */
//	public File getDirOrFile() {
//		return this.dirOrFile;
//	}


//	/**
//	 * @return <code>true</code> if 
//	 */
//	public boolean isDirectory() {
//		return (this.dir != null);
//	}

	/**
	 * Returns the timestamp of the last modification.
	 * 
	 * @return the timestamp
	 */
	public long lastModified() {
		return this.lastModified;
	}
	

	/**
	 * Copies this libary to it's cache position
	 * @param cacheBaseDir
	 * @param id the ID of this library
	 * @param environment the environment
	 * @return true when any libraries have been copied actually
	 */
	public boolean copyToCache( File cacheBaseDir, String id, Environment environment ) {
		
		boolean changed = false;
		this.cacheDir = new File( cacheBaseDir, id );
		File jarCacheDir = new File( cacheBaseDir, id + "jar");
		
		
		//System.out.println(">>>>copyToCache: copying " + getPath(environment) + " to " + this.cacheDir );
		//if ( this.dir != null ) {
			// a directory can contain library-files (jar, zip) as well
			// as plain class or resource files. Each library-file
			// will be extracted whereas other files will just be copied
		File[] myFiles = getFiles( environment );
			for (int j = 0; j < myFiles.length; j++) {
				File file = myFiles[j];
				String fileName = file.getName();
				//File fileInDir = new File( this.dir, fileName );
				if (fileName.endsWith(".zip") || fileName.endsWith(".jar")) 
				{
					// this is a library file:
					// only extract it when the original is newer than the copy:
					File cacheCopy = new File( jarCacheDir, fileName );
					if ( (!cacheCopy.exists())
							|| (lastModified() > cacheCopy.lastModified())) 
					{
						changed = true;
						try {
							// copy the library to the cache:
							FileUtil.copy(file, cacheCopy );
							// unzip / unjar the content:
							JarUtil.unjar(file, this.cacheDir );
						} catch (IOException e) {
							e.printStackTrace();
							throw new BuildException("Unable to extract the binary class files from the library [" + file.getAbsolutePath() + "]: " + e.toString(), e );
						}
					}
				} else {
					// this is a normal class or resource file:
					try {
						File targetFile = new File( this.cacheDir, fileName );
						// copy the file only when it is newer
						// than the existing copy: 
						if ( (!targetFile.exists()) 
								|| (this.lastModified() > targetFile.lastModified()) ) 
						{
							changed = true;
							if (file.isDirectory()) {
								FileUtil.copyDirectoryContents(file, targetFile, true);
							} else {
								FileUtil.copy(file, targetFile);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
						throw new BuildException("Unable to copy the binary class files from the library [" + file.getAbsolutePath() + "]: " + e.toString(), e );
					}
				}
			//}
		//}  else {
//			// this is a library (jar or zip) file:
//			// copy only when the original is newer than the cached copy: 
//			if ( (!this.cacheDir.exists())
//					|| (this.file.lastModified() > this.cacheDir.lastModified())) {
//				changed = true;
//				if (!this.file.exists()) {
//					throw new BuildException("Unable to extract the binary class files from the library [" + this.file.getAbsolutePath() + "]: this library does not exist. Check the <library> settings in your build.xml script." );
//				}
//				try {
//					// copy the library to the cache:
//					FileUtil.copy(this.file, new File( jarCacheDir, this.file.getName() ) );
//					// unzip / unjar the content:
//					JarUtil.unjar(this.file, this.cacheDir);
//				} catch (IOException e) {
//					e.printStackTrace();
//					throw new BuildException("Unable to extract the binary class files from the library [" + this.file.getAbsolutePath() + "]: " + e.toString(), e );
//				}
//			}
			/*
			File cacheCopy = new File( cachePath + lib.getId() + File.separatorChar + lib.getName() );
			if ( (!cacheCopy.exists())
					|| (lib.lastModified() > cacheCopy.lastModified())) {
				try {
					this.binaryLibrariesUpdated = true;
					// copy the library to the cache:
					FileUtil.copy(lib.getFile(), cacheCopy );
					// unzip / unjar the content:
					JarUtil.unjar(lib, binaryBaseDir);
				} catch (IOException e) {
					e.printStackTrace();
					throw new BuildException("Unable to extract the binary class files from the library [" + lib.getAbsolutePath() + "]: " + e.toString(), e );
				}
			}
			*/
		}
		this.libraryChanged = changed;
		return changed;
	}
//	
//	/**
//	 * @param targetDir
//	 */
//	public void copyFromCache( File targetDir ) {
//		//targetDir = new File( targetDir, "" + this.id );
//		//System.out.println("<<<<copyFromCache: copying " + this.cacheDir + " to " + targetDir );
//		try {
//			FileUtil.copyDirectoryContents( this.cacheDir, targetDir, true );
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new BuildException( "Unable to copy binary library: " + e.toString() );
//		}
//	}
	
	/**
	 * Retrieves the directory that contains the cache.
	 * @return the cache directory or null if copryToCache has not been called before.
	 */
	public File getCacheDirectory() {
		return this.cacheDir;
	}
	
	/**
	 * @return true if library was changed, false otherwise
	 */
	public boolean isLibraryChanged() {
		return this.libraryChanged;
	}

	/**
	 * Retrieves the full path(s) of this library.
	 * 
	 * @param environment the environment
	 * @return the full path(s) of this library that can be used as a key. 
	 */
	public String getPath( Environment environment ) {
		if (this.isDynamic) {
			return environment.resolveFile( this.dynamicLibraryPath ).getAbsolutePath();
		}
		if (this.files == null) {
			return null;
		} else if (this.files.length == 1) {
			return this.files[0].getAbsolutePath();
		} else {
			StringBuffer buffer = new StringBuffer();
			File[] includedFiles = this.files;
			for (int j = 0; j < includedFiles.length; j++) {
				File file = includedFiles[j];
				buffer.append( file.getAbsolutePath() );
				if (j != includedFiles.length -1) {
					buffer.append(':');
				}
			}
			return buffer.toString();
		}
	}

	/**
	 * Retrieves all files that are contained in this library.
	 * 
	 * @param environment the environment
	 * @return all files that are contained in this library.
	 */
	public File[] getFiles( Environment environment ) {
		if (this.isDynamic) {
			File resolved = environment.resolveFile( this.dynamicLibraryPath );
			if (!resolved.exists()) {
				new RuntimeException().printStackTrace();
				throw new BuildException("Unable to resolve library path [" + this.dynamicLibraryPath + "] /  [" + resolved.getAbsolutePath() + "]: please check your <library> settings.");
			}
			return new File[] { resolved };
		}
		return this.files;
	}

	/**
	 * @return true when settings seem to be correct for this library
	 */
	public boolean isValid() {
		return this.files != null || this.isDynamic;
	}
	
	public String toString() {
		if (this.files == null) {
			return "(no files in library)";
		}
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < this.files.length; i++)
		{
			buffer.append( this.files[i].getPath() ).append(", ");
		}
		return buffer.toString();
	}
}

