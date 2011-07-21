/*
 * Created on 24-Feb-2004 at 16:35:32.
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.CRC32;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * <p>Methods for the fast and simple creation of jar files.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        24-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public final class JarUtil {
	
	private final static boolean IS_WINDOWS = File.separatorChar == '\\';
	
	/**
	 * Writes all files of the given directory to the specified jar-file.
	 * 
	 * @param sourceDir The directory containing the "source" files.
	 * @param target The jar file which should be created
	 * @param compress True when the jar file should be compressed
	 * @throws FileNotFoundException when a file could not be found
	 * @throws IOException when a file could not be read or the jar file could not be written to.
	 */
	public static void jar( File sourceDir, File target, boolean compress ) 
	throws FileNotFoundException, IOException 
	{
		jar( sourceDir.listFiles(), sourceDir, target, compress );
	}
	
	/**
	 * Writes all given files to the specified jar-file.
	 * 
	 * @param files all files that should be added to the JAR file
	 * @param sourceDir The parent directory containing the given files.
	 * @param target The jar file which should be created
	 * @param compress True when the jar file should be compressed
	 * @throws FileNotFoundException when a file could not be found
	 * @throws IOException when a file could not be read or the jar file could not be written to.
	 */
	public static void jar(File[] files, File sourceDir, File target, boolean compress) 
	throws FileNotFoundException, IOException 
	{
		// create target directory if necessary: 
		if (!target.getParentFile().exists()) {
			target.getParentFile().mkdirs();
		}
		// creates target-jar-file:
		JarOutputStream out = new JarOutputStream(
								new FileOutputStream( target ) );
		if (compress) {
			out.setLevel( ZipOutputStream.DEFLATED );
		} else {
			out.setLevel( ZipOutputStream.STORED );
		}
		// create a CRC32 object:
		CRC32 crc = new CRC32();
		byte[] buffer = new byte[ 1024 * 1024 ];
		// add all files:
		int sourceDirLength = sourceDir.getAbsolutePath().length() + 1;
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			addFile( file, out, crc, sourceDirLength, buffer );
		}
		out.close();
	}


	/**
	 * Adds one file to the given jar file.
	 * If the specified file is a directory, all included files will be added.
	 * 
	 * @param file The file which should be added
	 * @param out The jar file to which the given jar file should be added
	 * @param crc A helper class for the CRC32 calculation
	 * @param sourceDirLength The number of chars which can be skipped from the file's path
	 * @param buffer A buffer for reading the files.
	 * @throws FileNotFoundException when the file was not found
	 * @throws IOException when the file could not be read or not be added
	 */
	private static void addFile(File file, JarOutputStream out, CRC32 crc, int sourceDirLength, byte[] buffer) 
	throws FileNotFoundException, IOException 
	{
		if (file.isDirectory()) {
			File[] fileNames = file.listFiles();
			for (int i = 0; i < fileNames.length; i++) {
				addFile( fileNames[i], out, crc, sourceDirLength, buffer );
			}
		} else {
			String entryName = file.getAbsolutePath().substring(sourceDirLength);
			if (IS_WINDOWS) {
					entryName = StringUtil.replace( entryName, '\\', '/' );
			}
			JarEntry entry = new JarEntry( entryName );
			// read file:
			FileInputStream in = new FileInputStream( file );
			add(entry, in, out, crc, buffer);
		}
	}

	/**
	 * @param entry
	 * @param in
	 * @param out
	 * @param crc
	 * @param buffer
	 * @throws IOException
	 */
	private static void add(JarEntry entry, InputStream in, JarOutputStream out, CRC32 crc, byte[] buffer) 
	throws IOException 
	{
		out.putNextEntry(entry);
		int read;
		long size = 0;
		while (( read = in.read(buffer)) != -1) {
			crc.update(buffer, 0, read);
			out.write(buffer, 0, read);
			size += read;
		}
		entry.setCrc( crc.getValue() );
		entry.setSize( size );
		in.close();
		out.closeEntry();
		crc.reset();
	}
	
	/**
	 * Adds the given file to the specified JAR file.
	 * 
	 * @param file the file that should be added
	 * @param jarFile The JAR to which the file should be added
	 * @param parentDir the parent directory of the file, this is used to calculate the path witin the JAR file.
	 *                  When null is given, the file will be added into the root of the JAR.
	 * @param compress True when the jar file should be compressed
	 * @throws FileNotFoundException when the jarFile does not exist
	 * @throws IOException when a file could not be written or the jar-file could not read.
	 */
	public static void addToJar( File file, File jarFile, File parentDir, boolean compress )
	throws FileNotFoundException, IOException
	{
		File tmpJarFile = File.createTempFile("tmp", ".jar", jarFile.getParentFile() );
		JarOutputStream out = new JarOutputStream(
				new FileOutputStream( tmpJarFile ) );
		if (compress) {
			out.setLevel( ZipOutputStream.DEFLATED );
		} else {
			out.setLevel( ZipOutputStream.STORED );
		}
		// copy contents of old jar to new jar:
		JarFile inputFile = new JarFile( jarFile );
		JarInputStream in = new JarInputStream( new FileInputStream( jarFile ) );
		CRC32 crc = new CRC32();
		byte[] buffer = new byte[ 512 * 1024 ];
		JarEntry entry = (JarEntry) in.getNextEntry();
		while (entry != null) {
			InputStream entryIn = inputFile.getInputStream(entry); 
			add( entry, entryIn, out, crc, buffer );
			entryIn.close();
			entry = (JarEntry) in.getNextEntry();
		}
		in.close();
		inputFile.close();
		
		int sourceDirLength;
		if (parentDir == null) {
			sourceDirLength = file.getAbsolutePath().lastIndexOf( File.separatorChar ) + 1;
		} else {
			sourceDirLength = file.getAbsolutePath().lastIndexOf( File.separatorChar ) + 1
				- parentDir.getAbsolutePath().length();			
		}
		addFile( file, out, crc, sourceDirLength, buffer );
		out.close();
		
		// remove old jar file and rename temp file to old one:
		if (jarFile.delete()) {
			if ( !tmpJarFile.renameTo(jarFile) ) {
				throw new IOException("Unable to rename temporary JAR file to [" + jarFile.getAbsolutePath() + "]." );
			}
		} else {
			throw new IOException("Unable to delete old JAR file [" + jarFile.getAbsolutePath() + "]." );
		}
		
	}

	/**
	 * Extracts the given jar-file to the specified directory.
	 * The target directory will be cleaned before the jar-file will be extracted.
	 * 
	 * @param jarFile The jar file which should be unpacked
	 * @param targetDir The directory to which the jar-content should be extracted.
	 * @throws FileNotFoundException when the jarFile does not exist
	 * @throws IOException when a file could not be written or the jar-file could not read.
	 */
	public static void unjar(File jarFile, File targetDir) 
	throws FileNotFoundException, IOException 
	{
		// clear target directory:
		if (targetDir.exists()) {
			targetDir.delete();
		}
		// create new target directory:
		targetDir.mkdirs(); 
		// read jar-file:
		String targetPath = targetDir.getAbsolutePath() + File.separatorChar;
		byte[] buffer = new byte[ 1024 * 1024 ];
		JarFile input = new JarFile( jarFile, false, ZipFile.OPEN_READ );
		Enumeration enumeration = input.entries();
		for (; enumeration.hasMoreElements();) {
			JarEntry entry = (JarEntry) enumeration.nextElement();
			if (!entry.isDirectory()) {
				// do not copy anything from the package cache:
				if (entry.getName().indexOf("package cache") == -1) {
					String path = targetPath + entry.getName();
					File file = new File( path );
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					FileOutputStream out = new FileOutputStream( file);
					InputStream in = input.getInputStream(entry);
					int read;
					while ( (read = in.read( buffer )) != -1) {
						out.write( buffer, 0, read );
					}
					in.close();
					out.close();
				}
			}
		}
		
	}
    
    /**
     * Extracts the given resource from a jar-file to the specified directory.
     * 
     * @param jarFile The jar file which should be unpacked
     * @param resource The name of a resource in the jar
     * @param targetDir The directory to which the jar-content should be extracted.
     * @throws FileNotFoundException when the jarFile does not exist
     * @throws IOException when a file could not be written or the jar-file could not read.
     */
    public static void unjar(File jarFile, String resource, File targetDir)
    throws FileNotFoundException, IOException {
        // clear target directory:
        if (targetDir.exists()) {
            targetDir.delete();
        }
        // create new target directory:
        targetDir.mkdirs(); 
        // read jar-file:
        String targetPath = targetDir.getAbsolutePath() + File.separatorChar;
        byte[] buffer = new byte[ 1024 * 1024 ];
        JarFile input = new JarFile( jarFile, false, ZipFile.OPEN_READ );
        Enumeration enumeration = input.entries();
        for (; enumeration.hasMoreElements();) {
            JarEntry entry = (JarEntry) enumeration.nextElement();
            if (!entry.isDirectory()) {
                // do not copy anything from the package cache:
                if (entry.getName().equals(resource)) {
                    String path = targetPath + entry.getName();
                    File file = new File( path );
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    FileOutputStream out = new FileOutputStream( file);
                    InputStream in = input.getInputStream(entry);
                    int read;
                    while ( (read = in.read( buffer )) != -1) {
                        out.write( buffer, 0, read );
                    }
                    in.close();
                    out.close();
                }
            }
        }
    }

	/**
	 * Reads the package-names from the given jar-file.
	 * 
	 * @param jarFile the jar file
	 * @return an array with all found package-names
	 * @throws IOException when the jar-file could not be read
	 */
	public static String[] getPackageNames(File jarFile) 
	throws IOException 
	{
		HashMap packageNames = new HashMap();
		JarFile input = new JarFile( jarFile, false, ZipFile.OPEN_READ );
		Enumeration enumeration = input.entries();
		for (; enumeration.hasMoreElements();) {
			JarEntry entry = (JarEntry) enumeration.nextElement();
			String name = entry.getName();
			if (name.endsWith(".class")) {
				int endPos = name.lastIndexOf(File.separatorChar);
				name = name.substring( 0, endPos );
				name = name.replace(File.separatorChar, '.');
				packageNames.put( name, name );
			}
		}
		return (String[]) packageNames.values().toArray( new String[ packageNames.size() ] );
	}
	
	/**
	 * Extracts the name of the main class from the given jar file.
	 * 
	 * @param jarFile the jar file
	 * @return the name of the main class from the given jar file, null when the jar does not have a manifest.
	 * @throws IOException when the jar file could not be read
	 */
	public static String getMainClassName( File jarFile ) 
	throws IOException 
	{
		if (!jarFile.exists()) {
			throw new IllegalArgumentException("File [" + jarFile.getAbsolutePath() + "] does not exist." );
		}
		JarFile input = new JarFile( jarFile, false, ZipFile.OPEN_READ );
		Manifest manifest = input.getManifest();
		if (manifest == null) {
			return null;
		}
		Attributes attributes = manifest.getMainAttributes();
		if (attributes != null) {
			return attributes.getValue("Main-Class");
		}
		return null;
	}
	
	public static void exec( File jarFile, ArrayList argsList, ClassLoader classLoader ) 
	throws IOException, ClassNotFoundException, SecurityException, NoSuchMethodException, 
	IllegalArgumentException, IllegalAccessException, InvocationTargetException 
	{
		String[] args = (String[]) argsList.toArray( new String[ argsList.size() ] );
		exec( jarFile, args, classLoader );
	}

	public static void exec( File jarFile, String[] args, ClassLoader classLoader ) 
	throws IOException, ClassNotFoundException, SecurityException, NoSuchMethodException, 
	IllegalArgumentException, IllegalAccessException, InvocationTargetException 
	{
		String mainClassName = getMainClassName(jarFile);
		if (mainClassName == null) {
			throw new ClassNotFoundException("Unable to extract name of Main-Class of " + jarFile.getAbsolutePath() );
		}
		Class mainClass = classLoader.loadClass(mainClassName);
		Method mainMethod = mainClass.getMethod("main", new Class[]{ String[].class } );
		mainMethod.invoke(null, new Object[]{ args } );
	}
	
}
