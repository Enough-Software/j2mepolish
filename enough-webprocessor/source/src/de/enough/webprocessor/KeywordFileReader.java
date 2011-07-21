/*
 * Created on Jun 6, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.enough.webprocessor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import de.enough.webprocessor.util.FileUtil;
import de.enough.webprocessor.util.TextUtil;

/**
 * Reads keyword files which can contain any number of lines with any number of keywords separated by comma.
 * Comments starting with "##" are ignored.
 * A keyword can differentiate between the index-word (the word which is actually
 * shown in the index) and the searchkey by seperating them with a double colon
 * ("::").
 * 
 * @author robertvirkus
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class KeywordFileReader {
	

	/** 
	 * Creates a new reader for a keyword file
	 * @param file the file containing the keywords
	 * @return an array of found keywords
	 * @throws FileNotFoundException when the file is not found
	 * @throws IOException when the file cannot be read
	 */
	public static final Keyword[] readKeywordFile( File file ) 
	throws FileNotFoundException, IOException {
		ArrayList keywordsList = new ArrayList();
		String[] lines = FileUtil.readTextFile( file );
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i].trim();
			if (!( line.length() ==0 || line.startsWith( "##") )) {
				String[] keywords = TextUtil.splitAndTrim( line, ',');
				for (int j = 0; j < keywords.length; j++) {
					String pattern = keywords[j];
					if (pattern.length() != 0) {
						Keyword keyword = new Keyword( pattern );
						keywordsList.add( keyword );
					}
				}
			}
		}
		return (Keyword[]) keywordsList.toArray( new Keyword[ keywordsList.size() ] );
	}
	
}
