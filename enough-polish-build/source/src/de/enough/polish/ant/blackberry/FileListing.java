package de.enough.polish.ant.blackberry;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author javapractices.com
 * @author Alex Wong
 */
public final class FileListing {

	/**
	 * Recursively walk a directory tree and return a List of all Files found;
	 * the List is sorted using File.compareTo.
	 * 
	 * @param directory
	 *            is a valid directory, which can be read.
	 */
	public static List getFileListing(File directory, String filetype, boolean recursive)
			throws FileNotFoundException {
		validateDirectory(directory);
		List result = new ArrayList();

		File[] filesAndDirs = directory.listFiles();
		List filesDirs = Arrays.asList(filesAndDirs);
		for (int i = 0; i < filesDirs.size(); i++) {
			File file = (File) filesDirs.get(i);
			
			if (!file.isFile()) {
				// must be a directory
				// recursive call!
				if(recursive)
				{
					List deeperList = getFileListing(file,filetype,true);
					result.addAll(deeperList);
				}	
			}
			else
			{
				if(filetype != null)
				{
					if(file.getName().endsWith(filetype))
					{
						result.add(file); 
					}
				}
				else
				{
					result.add(file);
				}
			}
		}
		Collections.sort(result);
		return result;
	}

	/**
	 * Directory is valid if it exists, does not represent a file, and can be
	 * read.
	 */
	private static void validateDirectory(File aDirectory)
			throws FileNotFoundException {
		if (aDirectory == null) {
			throw new IllegalArgumentException("Directory should not be null.");
		}
		if (!aDirectory.exists()) {
			throw new FileNotFoundException("Directory does not exist: "
					+ aDirectory);
		}
		if (!aDirectory.isDirectory()) {
			throw new IllegalArgumentException("Is not a directory: "
					+ aDirectory);
		}
		if (!aDirectory.canRead()) {
			throw new IllegalArgumentException("Directory cannot be read: "
					+ aDirectory);
		}
	}
}
