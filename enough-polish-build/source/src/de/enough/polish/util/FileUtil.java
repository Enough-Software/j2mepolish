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
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

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
	 * Reads a text file.
	 *  
	 * @param file the text file
	 * @param encoding the encoding of the textfile
	 * @return the lines of the text file
	 * @throws FileNotFoundException when the file was not found
	 * @throws IOException when file could not be read.
	 */
	public static String[] readTextFile( File file, String encoding ) 
	throws FileNotFoundException, IOException 
	{
		return readTextFile( new FileInputStream(file), encoding );
	}
	
	/**
	 * Reads the text from the given input stream in the default encoding.
	 * 
	 * @param in the input stream
	 * @return the text contained in the stream
	 * @throws IOException when stream could not be read.
	 */
	public static String[] readTextFile(InputStream in) 
	throws IOException
	{
		return readTextFile( in, null );
	}

	/**
	 * Reads the text from the given input stream in the default encoding.
	 * 
	 * @param in the input stream
	 * @param encoding the encoding of the textfile
	 * @return the text contained in the stream
	 * @throws IOException when stream could not be read.
	 */
	public static String[] readTextFile(InputStream in, String encoding) 
	throws IOException
	{
		ArrayList lines = new ArrayList();
		BufferedReader bufferedIn;
		if (encoding != null) {
			bufferedIn = new BufferedReader( new InputStreamReader( in, encoding ) );
		} else {
			bufferedIn = new BufferedReader( new InputStreamReader( in ) );
		}
		String line;
		while ((line = bufferedIn.readLine()) != null) {
			lines.add( line );
		}
		bufferedIn.close();
		in.close();
		return (String[]) lines.toArray( new String[ lines.size() ] );
	}



	/**
	 * Writes (and creates) a text file.
	 * 
	 * @param file the file to which the text should be written
	 * @param lines the text lines of the file in a collection with String-values
	 * @throws IOException when there is an input/output error during the saving
	 */
	public static void writeTextFile(File file, Collection lines) 
	throws IOException 
	{
		writeTextFile( file, (String[]) lines.toArray( new String[ lines.size() ]));
	}

	/**
	 * Writes (and creates) a text file.
	 * 
	 * @param file the file to which the text should be written
	 * @param lines the text lines of the file
	 * @throws IOException when there is an input/output error during the saving
	 */
	public static void writeTextFile(File file, String[] lines) 
	throws IOException 
	{
		File parentDir = file.getParentFile(); 
		if ( (parentDir != null) && !parentDir.exists()) {
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
	 * @param files The files which should be copied, when an array element is null, it will be ignored.
	 * @param targetDir The directory to which the given files should be copied to.
	 * @throws FileNotFoundException when the source file was not found
	 * @throws IOException when there is an error while copying the file.
	 * @throws NullPointerException when files or targetDir is null.
	 */
	public static void copy(File[] files, File targetDir) 
	throws FileNotFoundException, IOException 
	{
		copy( files, targetDir, false );
	}


	/**
	 * Copies the given files to the specified target directory.
	 * 
	 * @param files The files which should be copied, when an array element is null, it will be ignored.
	 * @param targetDir The directory to which the given files should be copied to.
	 * @param overwrite true when existing target files should be overwritten even when they are newer
	 * @throws FileNotFoundException when the source file was not found
	 * @throws IOException when there is an error while copying the file.
	 * @throws NullPointerException when files or targetDir is null.
	 */
	public static void copy(File[] files, File targetDir, boolean overwrite) 
	throws FileNotFoundException, IOException 
	{
		String targetPath = targetDir.getAbsolutePath() + File.separatorChar;
		byte[] buffer = new byte[ 1024 * 1024 ];
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file != null) {
				File targetFile = new File( targetPath + file.getName() );
				if (!overwrite && targetFile.exists() && targetFile.lastModified() > file.lastModified()) {
					continue;
				}
				copy( file, targetFile, buffer );
			}
		}
	}

	/**
	 * Copies a file.
	 * 
	 * @param source The file which should be copied
	 * @param target The file or directory to which the source-file should be copied to.
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
	 * @param target The file or directory to which the source-file should be copied to.
	 * @param buffer A buffer used for the copying.
	 * @throws FileNotFoundException when the source file was not found
	 * @throws IOException when there is an error while copying the file.
	 */
	private static void copy(File source, File target, byte[] buffer ) 
	throws FileNotFoundException, IOException 
	{
		InputStream in = new FileInputStream( source );
		// create parent directory of target-file if necessary:
		File parent = target.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		} 
		if (target.isDirectory()) {
			target = new File( target, source.getName() );
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
	
	/**
	 * Writes the properties which are defined in the given HashMap into a textfile.
	 * The notation in the file will be [name]=[value]\n for each defined property.
	 * 
	 * @param file the file which should be created or overwritten
	 * @param properties the properties which should be written. 
	 * @throws IOException when there is an input/output error during the saving
	 */
	public static void writePropertiesFile( File file, Map properties ) 
	throws IOException 
	{
		writePropertiesFile(file, '=', properties);
	}
	
	/**
	 * Writes the properties which are defined in the given HashMap into a textfile.
	 * The notation in the file will be [name]=[value]\n for each defined property.
	 * 
	 * @param file the file which should be created or overwritten
	 * @param delimiter the character that separates a property-name from a property-value.
	 * @param properties the properties which should be written. 
	 * @throws IOException when there is an input/output error during the saving
	 */
	public static void writePropertiesFile( File file, char delimiter, Map properties ) 
	throws IOException 
	{
		Object[] keys = properties.keySet().toArray();
		Arrays.sort(keys);
		String[] lines = new String[ keys.length ];
		for (int i = 0; i < lines.length; i++) {
			Object key = keys[i];
			Object value = properties.get( key );
			lines[i] = key.toString() + delimiter + value.toString();
		}
		writeTextFile( file, lines );
	}


	/**
	 * Reads a properties file.
	 * The notation of the file needs to be 
	 * "[name]=[value]\n"
	 * for each defined property.
	 * 
	 * @param file the file containing the properties
	 * @return a hashmap containing all properties found in the file
	 * @throws FileNotFoundException when the file was not found
	 * @throws IOException when file could not be read.
	 * @throws IllegalArgumentException when the line does not contain a property
	 */
	public static HashMap readPropertiesFile( File file ) 
	throws FileNotFoundException, IOException 
	{
		return readPropertiesFile(file, '=');
	}
	
	/**
	 * Reads a properties file.
	 * The notation of the file needs to be 
	 * "[name]=[value]\n"
	 * for each defined property.
	 * 
	 * @param file the file containing the properties
	 * @param delimiter the character that separates a property-name from a property-value.
	 * @return a hashmap containing all properties found in the file
	 * @throws FileNotFoundException when the file was not found
	 * @throws IOException when file could not be read.
	 * @throws IllegalArgumentException when the line does not contain a property
	 */
	public static HashMap readPropertiesFile( File file, char delimiter ) 
	throws FileNotFoundException, IOException 
	{
		
		HashMap map = new HashMap();
		readPropertiesFile( file, delimiter, map );
		return map;
	}
	
	/**
	 * Reads a properties file.
	 * The notation of the file needs to be 
	 * "[name]=[value]\n" where '=' is the defined delimiter character.
	 * for each defined property.
	 * 
	 * @param file the file containing the properties
	 * @param delimiter the character that separates a property-name from a property-value.
	 * @param map the hash map to which the properties should be added 
	 * @throws FileNotFoundException when the file was not found
	 * @throws IOException when file could not be read.
	 * @throws IllegalArgumentException when the line does not contain a property
	 */
	public static void readPropertiesFile( File file, char delimiter, Map map ) 
	throws FileNotFoundException, IOException 
	{
		readPropertiesFile( file, delimiter, '#', map, false );
		/*
		String[] lines = readTextFile( file );
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (line.length() > 0 && line.charAt(0) != '#') {
				int equalsPos = line.indexOf( delimiter );
				if (equalsPos == -1) {
					throw new IllegalArgumentException("The line [" + line 
							+ "] of the file [" + file.getAbsolutePath() 
							+ "] contains an invalid property definition: " +
									"missing separater-character (\"" + delimiter + "\")." );
				}
				String key = line.substring(0, equalsPos );
				String value = line.substring( equalsPos + 1);
				map.put( key, value );
			}
		}
		*/
	}
	
	/**
	 * Reads a properties file.
	 * The notation of the file needs to be 
	 * "[name]=[value]\n" where '=' is the defined delimiter character.
	 * for each defined property.
	 * 
	 * @param file the file containing the properties
	 * @param delimiter the character that separates a property-name from a property-value.
	 * @param comment the character that introduces a comment, e.g. '#'
	 * @param map the hash map to which the properties should be added 
	 * @param ignoreInvalidProperties when this flag is true, invalid property definition (those that do not contain the delimiter char) are ignored
	 * @throws FileNotFoundException when the file was not found
	 * @throws IOException when file could not be read.
	 * @throws IllegalArgumentException when the line does not contain a property
	 */
	public static void readPropertiesFile( File file, char delimiter, char comment, Map map, boolean ignoreInvalidProperties ) 
	throws FileNotFoundException, IOException 
	{
		try {
			readProperties( new FileInputStream( file ), delimiter, comment, map, ignoreInvalidProperties );
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("File [" + file.getAbsolutePath() + "]:  " + e.getMessage() );
		}
	}
	
	/**
	 * Copies the contents of a directory to the specified target directory.
	 * 
	 * @param directory the directory containing files
	 * @param targetDirName the directory to which the files should be copied to
	 * @param update is true when files should be only copied when the source files
	 * 	are newer compared to the target files.
	 * @throws IOException when a file could not be copied
	 * @throws IllegalArgumentException when the directory is not a directory.
	 */
	public static void copyDirectoryContents(File directory, String targetDirName, boolean update)
	throws IOException
	{
		copyDirectoryContents(directory, new File( targetDirName ), update );
	}
	
	/**
	 * Copies the contents of a directory to the specified target directory.
	 * 
	 * @param directory the directory containing files
	 * @param targetDir the directory to which the files should be copied to
	 * @param update set to true when files should be only copied when the source files
	 * 	are newer compared to the target files.
	 * @throws IOException when a file could not be copied
	 * @throws IllegalArgumentException when the directory is not a directory.
	 */
	public static void copyDirectoryContents(File directory, File targetDir, boolean update)
	throws IOException
	{
		if (!directory.isDirectory()) {
			throw new IllegalArgumentException("Cannot copy contents of the file [" + directory.getAbsolutePath() + "]: specify a directory instead.");
		}
		String[] fileNames = directory.list();
		for (int i = 0; i < fileNames.length; i++) {
			String fileName = fileNames[i];
			File file = new File( directory.getAbsolutePath(), fileName );
			if (file.isDirectory()) {
				copyDirectoryContents( file, targetDir.getAbsolutePath() + File.separatorChar + fileName, update );
			}  else {
				File targetFile = new File( targetDir, fileName  );
				if (update) {
					// update only when the source file is newer:
					if ( (!targetFile.exists())
						|| (file.lastModified() > targetFile.lastModified() )) 
					{
						copy( file, targetFile );
					}
				} else {
					// copy the file in all cases:
					copy( file, targetFile );
				}
			}
		}
	}

//	private static String getPath( File file ) {
//		String path = file.getAbsolutePath();
//		int buildIndex = path.indexOf("build");
//		if (buildIndex != -1) {
//			path = path.substring( buildIndex );
//		}
//		return path;
//	}

	/**
	 * Deletes a file or a directory.
	 * 
	 * @param file the file or directory which should be deleted.
	 * @return true when the file could be deleted
	 */
	public static boolean delete(File file) {
        if (file.isDirectory()) {
            String[] children = file.list();
            for (int i=0; i < children.length; i++) {            	
                boolean success = delete(new File(file, children[i]));
                if ( !success ) {
                    return false;
                }
            }
        }
    
        // The directory is now empty so delete it
        return file.delete();
	}
	
	/**
	 * Reads properties from the given input stream.
	 * 
	 * @param in the input stream
	 * @return a map containing all properties that could be read from the input stream
	 * @throws IOException when reading from the input stream fails
	 */
	public static Map readProperties(InputStream in) throws IOException {
		Map map = new HashMap();
		readProperties(in, '=', '#', map, false );
		return map;
	}

	/**
	 * Reads properties from the given file.
	 * 
	 * @param file the file containing properties separated with '='
	 * @return a map containing all properties that could be read from the input stream
	 * @throws IOException when reading from the file fails
	 * @throws FileNotFoundException when the file does not exist
	 */
	public static Map readProperties(File file) throws IOException {
		return readProperties( new FileInputStream( file ) );
	}

	/**
	 * Reads properties from the given reader.
	 * 
	 * @param reader the input reader
	 * @return a map containing all properties that could be read from the reader
	 * @throws IOException when reading fails
	 */
	public static Map readProperties(Reader reader)
	throws IOException
	{
		Map map = new HashMap();
		readProperties(reader, '=', '#', map, false );
		return map;
	}


	/**
	 * Reads properties from the given input stream.
	 * 
	 * @param in the input stream
	 * @param delimiter the character that separates a property-name from a property-value.
	 * @param properties a map containing properties
	 * @throws IOException when reading from the input stream fails
	 */
	public static void readProperties(InputStream in, char delimiter, Map properties ) 
	throws IOException 
	{
		readProperties( in, delimiter, '#', properties, false, null );
	}


	/**
	 * Reads properties from the given input stream.
	 * 
	 * @param in the input stream
	 * @param delimiter the character that separates a property-name from a property-value.
	 * @param properties a map containing properties
	 * @param encoding the encoding of the file
	 * @throws IOException when reading from the input stream fails
	 */
	public static void readProperties(InputStream in, char delimiter, Map properties, String encoding ) 
	throws IOException 
	{
		readProperties( in, delimiter, '#', properties, false, encoding );
	}

	/**
	 * Reads properties from the given input stream.
	 * 
	 * @param in the input stream
	 * @param delimiter the character that separates a property-name from a property-value.
	 * @param properties a map containing properties
	 * @param encoding the encoding of the file
	 * @param translateToAscii true when the FileUtil should translate the code into ASCII only code (using unicode). 
	 * @param translateToNative true when escape sequences like \t or \n should be converted to native characters
	 * @throws IOException when reading from the input stream fails
	 */
	public static void readProperties(InputStream in, char delimiter, Map properties, String encoding, boolean translateToAscii, boolean translateToNative ) 
	throws IOException 
	{
		readProperties( in, delimiter, '#', properties, false, encoding, translateToAscii, translateToNative );
	}

	/**
	 * Reads properties from the given input stream.
	 * 
	 * @param in the input stream
	 * @param delimiter the character that separates a property-name from a property-value.
	 * @param comment the char denoting comments
	 * @param properties a map containing properties
	 * @throws IOException when reading from the input stream fails
	 */
	public static void readProperties(InputStream in, char delimiter, char comment, Map properties ) 
	throws IOException 
	{
		readProperties( in, delimiter, comment, properties, false );
	}
	
	/**
	 * Reads properties from the given input stream.
	 * 
	 * @param in the input stream
	 * @param delimiter the character that separates a property-name from a property-value.
	 * @param comment the char denoting comments
	 * @param properties a map containing properties
	 * @param ignoreInvalidProperties when this flag is true, invalid property definition (those that do not contain the delimiter char) are ignored
	 * @throws IOException when reading from the input stream fails
	 * @throws IllegalArgumentException when an invalid property definition is encountered and ignoreInvalidProperties is false
	 */
	public static void readProperties(InputStream in, char delimiter, char comment, Map properties, boolean ignoreInvalidProperties ) 
	throws IOException 
	{
		readProperties( in, delimiter, comment, properties, ignoreInvalidProperties, null );
	}
	/**
	 * Reads properties from the given input stream.
	 * 
	 * @param in the input stream
	 * @param delimiter the character that separates a property-name from a property-value.
	 * @param comment the char denoting comments
	 * @param properties a map containing properties
	 * @param ignoreInvalidProperties when this flag is true, invalid property definition (those that do not contain the delimiter char) are ignored
	 * @param encoding the encoding of the text file, when null the default charset is used
	 * @throws IOException when reading from the input stream fails
	 * @throws IllegalArgumentException when an invalid property definition is encountered and ignoreInvalidProperties is false
	 */
	public static void readProperties(InputStream in, char delimiter, char comment, Map properties, boolean ignoreInvalidProperties, String encoding ) 
	throws IOException 
	{
		readProperties( in, delimiter, comment, properties, ignoreInvalidProperties, encoding, false, false );
	}
	
	/**
	 * Reads properties from the given reader.
	 * 
	 * @param reader the input reader
	 * @param delimiter the character that separates a property-name from a property-value.
	 * @param comment the char denoting comments
	 * @param properties a map containing properties
	 * @param ignoreInvalidProperties when this flag is true, invalid property definition (those that do not contain the delimiter char) are ignored
	 * @throws IOException when reading from the input stream fails
	 * @throws IllegalArgumentException when an invalid property definition is encountered and ignoreInvalidProperties is false
	 */
	public static void readProperties(Reader reader, char delimiter, char comment, Map properties, boolean ignoreInvalidProperties ) 
	throws IOException 
	{
		readProperties( reader, delimiter, comment, properties, ignoreInvalidProperties, false, false );
	}


	/**
	 * Reads properties from the given input stream.
	 * 
	 * @param in the input stream
	 * @param delimiter the character that separates a property-name from a property-value.
	 * @param comment the char denoting comments
	 * @param properties a map containing properties
	 * @param ignoreInvalidProperties when this flag is true, invalid property definition (those that do not contain the delimiter char) are ignored
	 * @param encoding the encoding of the text file, when null the default charset is used
	 * @param translateToAscii true when the FileUtil should translate the code into ASCII only code (using unicode). 
	 * @param translateToNative true when escape sequences like \t or \n should be converted to native characters
	 * @throws IOException when reading from the input stream fails
	 * @throws IllegalArgumentException when an invalid property definition is encountered and ignoreInvalidProperties is false
	 */
	public static void readProperties(InputStream in, char delimiter, char comment, Map properties, boolean ignoreInvalidProperties, String encoding, boolean translateToAscii, boolean translateToNative ) 
	throws IOException 
	{
		Reader reader;
		if (encoding == null) {
			reader = new  InputStreamReader( in );
		} else {
			reader = new InputStreamReader( in, encoding );
		}
		readProperties( reader, delimiter, comment, properties, ignoreInvalidProperties, translateToAscii, translateToNative );
		in.close();
	}
	
	/**
	 * Reads properties from the given reader.
	 * 
	 * @param reader the input reader
	 * @param delimiter the character that separates a property-name from a property-value.
	 * @param comment the char denoting comments
	 * @param properties a map containing properties
	 * @param ignoreInvalidProperties when this flag is true, invalid property definition (those that do not contain the delimiter char) are ignored
	 * @param translateToAscii true when the FileUtil should translate the code into ASCII only code (using unicode). 
	 * @param translateToNative true when escape sequences like \t or \n should be converted to native characters
	 * @throws IOException when reading from the input stream fails
	 * @throws IllegalArgumentException when an invalid property definition is encountered and ignoreInvalidProperties is false
	 */
	public static void readProperties(Reader reader, char delimiter, char comment, Map properties, boolean ignoreInvalidProperties, boolean translateToAscii, boolean translateToNative ) 
	throws IOException 
	{
		BufferedReader bufferedReader = new BufferedReader( reader );
		String line;
		int index = 0;
		while ( (line = bufferedReader.readLine()) != null) {
			index++;
			if (line.length() == 0 || line.charAt(0) == comment || line.trim().length() == 0) {
				continue;
			}
			if ( translateToAscii ) {
				line = Native2Ascii.nativeToAscii(line);
			}
			if (translateToNative) {
				line = Native2Ascii.asciiToNative(line);
			}
			int delimiterPos = line.indexOf( delimiter );
			if (delimiterPos == -1) {
				if (ignoreInvalidProperties || (index == 1)) { // ("\ufeff".equals(line)) ) {
					// "\ufeff" is an optional header announcing Big-Endian byte ordering for UTF-8 files.
					// since there are several BOM signatures, we will ignore all invalid
					// propery definitions in the first line for now.
					// For further information: 
					// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4508058
					// http://www.unicode.org/unicode/faq/utf_bom.html
					continue;
				} else {
					throw new IllegalArgumentException("The line [" + line 
							+ "] in row " + index + " contains an invalid property definition: " +
									"missing separator-character (\"" + delimiter + "\")." );					
				}
			}
			String key = line.substring( 0, delimiterPos ).trim();
			String value = line.substring( delimiterPos + 1 );
			properties.put( key, value );
		}	
	}
	


	/**
	 * Writes the given textlines into the specified file.
	 * 
	 * @param file the file to which the text should be written
	 * @param lines the text lines of the file
	 * @param encoding the encoding, e.g. "UTF8", null when the default encoding should be used
	 * @throws IOException when there is an input/output error during the saving
	 */
	public static void writeTextFile(File file, String[] lines, String encoding) 
	throws IOException 
	{
		
		File parentDir = file.getParentFile(); 
		if ( (parentDir != null) && !parentDir.exists()) {
			parentDir.mkdirs();
		}
		
		PrintWriter out;
		if (encoding != null) {
			out = new PrintWriter( new OutputStreamWriter( new FileOutputStream( file ), encoding ) );
		} else {
			out = new PrintWriter(new FileWriter( file )  );
		}
		
		for (int i = 0; i < lines.length; i++) {
			out.println( lines[i] );
		}
		out.close();
	}

	/**
	 * Adds the given line to the specified textfile.
	 * The file is created if necessary.
	 *   
	 * @param file the text file
	 * @param line the line
	 * @throws IOException when adding fails.
	 */
	public static void addLine(File file, String line )
	throws IOException
	{
		addLines( file, new String[]{ line } );
	}
	
	/**
	 * Adds the given line to the specified textfile.
	 * The file is created if necessary.
	 *   
	 * @param file the text file
	 * @param lines the lines that should be added
	 * @throws IOException when adding fails.
	 */
	public static void addLines(File file, String[] lines) 
	throws IOException 
	{
		if (file.exists()) {
			String[] oldLines = readTextFile(file);
			String[] newLines = new String[ oldLines.length + lines.length ];
			System.arraycopy( oldLines, 0, newLines, 0, oldLines.length );
			System.arraycopy( lines, 0, newLines, oldLines.length, lines.length );
			writeTextFile(file, newLines);
		} else {
			writeTextFile(file, lines );
		}
	}


	/**
	 * Retrieves all files from the given directory 
	 * 
	 * @param dir the directory
	 * @param extension the file extension, when the extension is null, all files are included
	 * @param recursive true when subdirectories should also be read.
	 * @return an String array with the file-names relative to the given directory that do have the given extension
	 */
	public static String[] filterDirectory(File dir, String extension, boolean recursive) {
		return filterDirectory(dir,extension,null,recursive);
	}
	
	public static String[] filterDirectory(File dir, String extension, FilenameFilter filenameFilter, boolean recursive) {
		if (dir == null || !dir.exists()) {
			return new String[0];
		}
		ArrayList fileNamesList = new ArrayList();
		filterDirectory( "", dir, extension, recursive, filenameFilter, fileNamesList );
		return (String[]) fileNamesList.toArray( new String[ fileNamesList.size() ] );
	}
	
	/**
	 * Retrieves all files from the given directory
	 * 
	 * @param path the start path taken from the base directory towards the current one
	 * @param dir the directory
	 * @param extension the file extension
	 * @param recursive true when subdirectories should also be read.
	 */
	private static void filterDirectory( String path, File dir, String extension, boolean recursive, FilenameFilter fileNameFilter, List fileNamesList ) {
		String[] names = dir.list(fileNameFilter);
		for (int i = 0; i < names.length; i++) {
			String name = names[i];
			File file = new File( dir, name );
			if (file.isDirectory()) {
				if (recursive) {
					filterDirectory(path + name + File.separatorChar, file, extension, recursive, null, fileNamesList );
				}
			} else if (extension == null || name.endsWith(extension)) {
				fileNamesList.add( path + name );
			}
		}
	}

    /**
     * Extracts the bytes from a file.
     * @param file the file from which the bytes should be extracted from
     * @return a byte arry corresponding to the file. Is never null.
     * @throws IOException
     */
    public static byte[] getBytesFromFile(File file) throws IOException {
        FileInputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        try {
            byte[] buffer = new byte[4096];
            inputStream = new FileInputStream(file);
            outputStream = new ByteArrayOutputStream();
            int read;
            while ( (read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read );
            }
            byte[] byteArray = null;
            byteArray = outputStream.toByteArray();
            return byteArray;
        }
        finally {
            if(inputStream != null) {
                inputStream.close();
            }
            if(outputStream != null) {
                outputStream.close();
            }
        }
    }
	
}
