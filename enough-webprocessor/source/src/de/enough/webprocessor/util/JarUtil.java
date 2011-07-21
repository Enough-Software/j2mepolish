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
package de.enough.webprocessor.util;

import java.io.*;
import java.util.Enumeration;
import java.util.jar.*;
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
		// read all files from the source directory:
		File[] fileNames = sourceDir.listFiles();
		int sourceDirLength = sourceDir.getAbsolutePath().length() + 1;
		for (int i = 0; i < fileNames.length; i++) {
			File file = fileNames[i];
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
			JarEntry entry = new JarEntry( entryName );
			out.putNextEntry(entry);
			// read file:
			FileInputStream in = new FileInputStream( file );
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
	}

	/**
	 * Extracts the given jar-file to the specified directory.
	 * The target directory will be cleard before the jar-file will be extracted.
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
		Enumeration e = input.entries();
		for (; e.hasMoreElements();) {
			JarEntry entry = (JarEntry) e.nextElement();
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
