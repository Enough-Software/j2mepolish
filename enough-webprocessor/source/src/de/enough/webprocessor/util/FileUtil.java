/*
 * Created on 14-Jan-2004 at 16:07:22.
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
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.webprocessor.util;

import java.io.*;
import java.util.ArrayList;

/**
 * <p>Provides some often used methods for handling files.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        14-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public final class FileUtil {
	
	/**
	 * Reads a text file.
	 *  
	 * @param fileName the name of the text file
	 * @return the lines of the text file
	 * @throws FileNotFoundException when the file was not found
	 * @throws IOException when file could not be read.
	 */
	public static String[] readTextFile(String fileName ) 
	throws FileNotFoundException, IOException 
	{
		return readTextFile( new File( fileName) );
	}
	
	/**
	 * Reads a text file.
	 *  
	 * @param file the text file
	 * @return the lines of the text file
	 * @throws FileNotFoundException when the file was not found
	 * @throws IOException when file could not be read.
	 */
	public static String[] readTextFile( File file ) 
	throws FileNotFoundException, IOException 
	{
		ArrayList lines = new ArrayList();
		BufferedReader in = new BufferedReader(new FileReader(file));
		String line;
		while ((line = in.readLine()) != null) {
			lines.add( line );
		}
		in.close();
		return (String[]) lines.toArray( new String[ lines.size() ]);
	}

	/**
	 * Writes (and creates) a text file.
	 * 
	 * @param file the file to which the text should be written
	 * @param lines the text lines of the file
	 * @throws IOException
	 */
	public static void writeTextFile(File file, String[] lines) 
	throws IOException 
	{
		File parentDir = file.getParentFile(); 
		if (! parentDir.exists()) {
			parentDir.mkdirs();
		}
		PrintWriter out = new PrintWriter(new FileWriter( file ) );
		for (int i = 0; i < lines.length; i++) {
			out.println( lines[i] );
		}
		out.close();
	}

	/**
	 * Copies the given files to the specified target directory.
	 * 
	 * @param files The files which should be copied.
	 * @param targetDir The directory to which the given files should be copied to.
	 * @throws FileNotFoundException when the source file was not found
	 * @throws IOException when there is an error while copying the file.
	 * @throws NullPointerException when files or targetDir is null.
	 */
	public static void copy(File[] files, File targetDir) 
	throws FileNotFoundException, IOException 
	{
		String targetPath = targetDir.getAbsolutePath() + File.separatorChar;
		byte[] buffer = new byte[ 1024 * 1024 ];
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			copy( file, new File( targetPath + file.getName() ), buffer );
		}
	}

	/**
	 * Copies a file.
	 * 
	 * @param source The file which should be copied
	 * @param target The file to which the source-file should be copied to.
	 * @throws FileNotFoundException when the source file was not found
	 * @throws IOException when there is an error while copying the file.
	 */
	public static void copy(File source, File target) 
	throws FileNotFoundException, IOException 
	{
		copy( source, target, new byte[ 1024 * 1024 ] );
	}
	
	/**
	 * Copies a file.
	 * 
	 * @param source The file which should be copied
	 * @param target The file to which the source-file should be copied to.
	 * @param buffer A buffer used for the copying.
	 * @throws FileNotFoundException when the source file was not found
	 * @throws IOException when there is an error while copying the file.
	 */
	private static void copy(File source, File target, byte[] buffer ) 
	throws FileNotFoundException, IOException 
	{
		InputStream in = new FileInputStream( source );
		// create parent directory of target-file if necessary:
		File parentDir = target.getParentFile();
		if (!parentDir.exists()) {
			parentDir.mkdirs();
		}
		OutputStream out = new FileOutputStream( target );
		int read;
		try {
			while ( (read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read );
			}
		} catch (IOException e) {
			throw e;
		} finally {
			in.close();
			out.close();
		}
	}
	
}
