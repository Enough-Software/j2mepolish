/*
 * Created on Jun 1, 2004
 *
 */
package de.enough.webprocessor;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

import de.enough.webprocessor.util.FileUtil;
import de.enough.webprocessor.util.StringList;
import de.enough.webprocessor.util.TextFile;
import de.enough.webprocessor.util.TextUtil;
import de.enough.webprocessor.util.Variable;

/**
 * 
 * @author robert virkus, robert@enough.de
 *
 */
public class WebProcessorTask 
extends Task 
{
	private HashMap directiveHandlersByName = new HashMap();
	private HashMap variables = new HashMap();
	private HashMap symbols = new HashMap();
	private HashMap tempVariables = new HashMap();
	private HashMap tempSymbols = new HashMap();
	private HashMap includesByName = new HashMap();
	private HashMap indexStyles = new HashMap();
	private HashMap alwaysUpdateFiles = new HashMap();
	private Keyword[] keywords;
	private File sourceDir;
	private File destinationDir;
	private File includesDir;
	private boolean clean;
	private int currentIfLevel;
	private String excludes;
	private String includes;

	/**
	 * Creates a new empty task.
	 * The initialisation is done by the getter and setter methods-
	 */
	public WebProcessorTask() {
		super();
	}
	
	public void addConfiguredDirective( DirectiveSetting setting ) {
		if (setting.getClassName() == null || setting.getDirective() == null) {
			throw new BuildException("Invalid directive setting - define both class and directive attributes.");
		}
		try
		{
			DirectiveHandler handler = (DirectiveHandler) Class.forName( setting.getClassName() ).newInstance();
			handler.init( getProject() );
			this.directiveHandlersByName.put( setting.getDirective(), handler);
			
		} catch (Exception e)
		{
			e.printStackTrace();
			throw new BuildException("Invalid directive setting: " + e );
		}
	}
	
	public void addConfiguredVariable( Variable var ) {
		String name = var.getName();
		if ( name == null) {
			throw new BuildException("Each <variable> element needs to have a name attribute.");
		}
		name = name.toLowerCase();
		if (var.getValue() == null) {
			throw new BuildException("Each <variable> element needs to have a value attribute.");
		}
		this.variables.put( name, var.getValue() );
		this.symbols.put( name + ":defined", Boolean.TRUE );
	}
	
	public void setSrcdir( File srcdir ) {
		this.sourceDir = srcdir;
	}
	
	public void setDestdir( File destdir ) {
		this.destinationDir = destdir;
	}
	
	public void setIncludedir( File includedir ) {
		this.includesDir = includedir;
	}
	
	public void setClean( boolean clean ) {
		this.clean = clean;
	}
	
	public void setSymbols( String symbolsString ) {
		String[] singleSymbols = TextUtil.splitAndTrim( symbolsString.toLowerCase(), ',');
		for (int i = 0; i < singleSymbols.length; i++) {
			String symbol = singleSymbols[i];
			this.symbols.put( symbol, Boolean.TRUE );
		}
	}
	
	public void setKeywords( String keywordsStr ) {
		String[] patterns = TextUtil.splitAndTrim( keywordsStr, ',');
		this.keywords = new Keyword[ patterns.length ];
		for (int i = 0; i < patterns.length; i++) {
			this.keywords[i] = new Keyword( patterns[i] );
		}
	}
	
	public void setKeywordsFile( File file ) {
		try {
			this.keywords = KeywordFileReader.readKeywordFile( file );
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException("Unable to read keywords file [" + file.getAbsolutePath() + "]: " + e.toString(), e );
		}
	}
	
	public void setUpdate( String updateStr ) {
		String[] updateFiles = TextUtil.splitAndTrim( updateStr, ',' );
		for (int i = 0; i < updateFiles.length; i++) {
			String fileName = updateFiles[i];
			this.alwaysUpdateFiles.put( fileName, Boolean.TRUE );
		}
	}
	
	public void setExcludes( String excludes ) {
		this.excludes = excludes;
	}
	
	public void setIncludes( String includes ) {
		this.includes = includes;
	}
	
	public String getVariable( String name ) {
		name = name.toLowerCase();
		String value = (String) this.tempVariables.get( name );
		if (value == null) {
			value = (String) this.variables.get( name );
		}
		return value;
	}
	
	public void addVariable( String name, String value ) {
		name =  name.toLowerCase();
		this.tempVariables.put(name, value );
		this.tempSymbols.put( name + ":defined", Boolean.TRUE );
	}
	
	public boolean isDefined( String symbol ) {
		symbol = symbol.toLowerCase();
		if ( this.symbols.get( symbol ) != null) {
			return true;
		} else if (this.tempSymbols.get( symbol ) != null) {
			return true;
		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.tools.ant.Task#execute()
	 */
	public void execute() throws BuildException {
		try {
			checkSettings();
			addVariables();
			FileSet fileSet = new FileSet();
			fileSet.setDir( this.sourceDir );
			String excl = this.excludes;
			if (excl == null) {
				excl = "**/*.bak,**/*.old,**/*.~";
			} else {
				excl += ",**/*.bak,**/*.old,**/*.~";
			}
			fileSet.setExcludes( excl );
			if (this.includes != null) {
				fileSet.setIncludes( this.includes );
			}
			String[] fileNames = fileSet.getDirectoryScanner( getProject() ).getIncludedFiles();
			for (int i = 0; i < fileNames.length; i++) {
				String fileName = fileNames[i];
				processFile( fileName, this.clean );
			}
		} catch (BuildException e ) {
			throw e;
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}


	/**
	 * Checks if all settings are valid.
	 */
	private void checkSettings() {
		if (this.sourceDir == null) {
			throw new BuildException("The attribute \"srcdir\" is required.");
		}
		if (this.destinationDir == null) {
			throw new BuildException("The attribute \"destdir\" is required.");
		}
		if (this.includesDir == null) {
			throw new BuildException("The attribute \"includedir\" is required.");
		}
	}
	
	/**
	 * Adds some pre-defined variables like "date", "time" and so on.
	 *
	 */
	protected void addVariables() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		this.variables.put( "date", format.format( new Date() ) );
		format = new SimpleDateFormat( "HH:mm" );
		this.variables.put( "time", format.format( new Date() ) );
	}
	
	protected void processFile( String fileName, boolean processAll ) {
		File sourceFile = new File( this.sourceDir.getAbsolutePath() 
				+ File.separator + fileName );
		File destinationFile = new File( this.destinationDir.getAbsolutePath()
				+ File.separator + fileName );
		boolean writeFile = true;
		if (destinationFile.exists() 
				&& (destinationFile.lastModified() > sourceFile.lastModified()) ) 
		{
			writeFile = false;
		}
		boolean alwaysUpdate = (this.alwaysUpdateFiles.get( fileName ) != null);
		if ( fileName.endsWith(".html") && !fileName.endsWith("-.html") && (writeFile || processAll || alwaysUpdate) ) {
			processHtmlFile( fileName, destinationFile, processAll || alwaysUpdate );
		} else if (writeFile) {
			try {
				// copy binary file to destination folder:
				FileUtil.copy( sourceFile, destinationFile );
			} catch (IOException e) {
				e.printStackTrace();
				throw new BuildException("Unable to copy file " + fileName 
						+ ": " + e.toString(), e);
			}
		}
	}
	 
	/** 
	 * Processes a single html file.
	 *
	 * @param fileName the name of the file
	 * @param processAll true when old files should be processed as well.
	 */
	protected void processHtmlFile(String fileName, File destinationFile, boolean processAll ) {
		System.out.println("processing [" + fileName + "].");
		try {
			TextFile sourceFile = new TextFile( this.sourceDir.getAbsolutePath(), fileName );
			boolean saveFileAnyhow = false;
			if (!processAll && destinationFile.exists()) {
				if (sourceFile.lastModified() <= destinationFile.lastModified()) {
					// the source file has not been changed
					//TODO process files that use external settings (externalindex etc) 
					// anyhow!
					return;
				} else {
					saveFileAnyhow = true;
				}
			} else {
				saveFileAnyhow = true;
			}
			StringList lines = new StringList( sourceFile.getContent() );
			boolean changed = processStringList( lines, fileName );
			if (changed 	|| saveFileAnyhow ) {
				sourceFile.setContent( lines.getArray() );
				sourceFile.saveToDir( this.destinationDir.getAbsolutePath() );
			}
			this.tempSymbols.clear();
			this.tempVariables.clear();
		} catch (IOException e) {
			throw new BuildException("Unable to process file " + fileName + ": " + e.toString(), e );
		}
	}
	
	/**
	 * Processes the contents of one source file.
	 * @param lines the contents
	 * @return true when the file contained directives.
	 */
	protected boolean processStringList( StringList lines, String fileName ) {
		boolean directiveFound = false;
		while (lines.next()) {
			String line = lines.getCurrent();
			if (processLine( line, lines, fileName )) {
				directiveFound = true;
			}
		}
		return directiveFound;
	}
	
	protected boolean processLine( String line, StringList lines, String fileName ) {
		boolean directiveFound = false;
		int startPos = line.indexOf( "<%" );
		while (startPos != -1) {
			int endPos = line.indexOf( "%>" );
			if (endPos < startPos ) {
				throw new BuildException("Invalid webprocessor-directive in file " 
						+ fileName + " line " + (lines.getCurrentIndex() + 1) 
						+ ": directive not closed: " + line );
			}
			processDirective( line, startPos, endPos, lines, fileName );
			directiveFound = true;
			line = lines.getCurrent();
			startPos = line.indexOf( "<%" );
		}
		return directiveFound;
	}

	/**
	 * @param line
	 * @param startPos
	 * @param endPos
	 * @param lines
	 */
	protected void processDirective(String line, int startPos, int endPos, StringList lines, String fileName ) {
		String directive = line.substring( startPos + 2, endPos ).trim();
		//System.out.println("found directive |" + directive + "| in line " + (lines.getCurrentIndex()) );
		String replacement = null;
		int currentIndex = lines.getCurrentIndex();
		if (directive.startsWith("= ")) {
			replacement = processInsertVar( directive, line, startPos, endPos, lines, fileName );
		} else if (directive.startsWith("set ")) {
			replacement = processSetVar( directive, line, startPos, endPos, lines, fileName );
		} else if ( directive.startsWith("define ")) {
			replacement = processDefine( directive, line, startPos, endPos, lines, fileName );
		} else if ( directive.startsWith("undefine ")) {
			replacement = processUndefine( directive, line, startPos, endPos, lines, fileName );
		} else if ( directive.startsWith("ifdef ")) {
			replacement = processIfdef( directive, line, startPos, endPos, lines, fileName );
		} else if ( directive.startsWith("ifndef ")) {
			replacement = processIfndef( directive, line, startPos, endPos, lines, fileName );
		} else if ( directive.startsWith("include ")) {
			replacement = processInclude( directive, line, startPos, endPos, lines, fileName );
		} else if (directive.startsWith("index")) {
			replacement = processIndex( directive, line, startPos, endPos, lines, fileName );			
		} else if (directive.startsWith("externalindex")) {
			replacement = processExternalIndex( directive, line, startPos, endPos, lines, fileName );			
		} else if (directive.startsWith("keywords")) {
			replacement = processKeywords( directive, line, startPos, endPos, lines, fileName );			
		} else {
			String directiveName = directive;
			int spacePos = directive.indexOf(' ');
			if (spacePos != -1) {
				directiveName = directive.substring(0, spacePos );
			}
			DirectiveHandler handler =  (DirectiveHandler) this.directiveHandlersByName.get(directiveName);
			if (handler != null) {
				replacement = handler.processDirective( directiveName, directive, fileName, lines );
			} else {
				System.out.println("warning: found unknown directive " + directive + " in file " + fileName + " in line " + ( lines.getCurrentIndex() + 1 ) + ": " + line );
				lines.prev();
				System.out.println(lines.getCurrent());
				lines.next();
				System.out.println(lines.getCurrent());
				lines.next();
				System.out.println(lines.getCurrent());
			}
		}
		if (currentIndex == lines.getCurrentIndex()) {
			line = line.substring( 0, startPos ) + replacement + line.substring( endPos + 2 );
			String trimmedLine = line.trim();
			if (trimmedLine.length() == 0) {
				lines.removeCurrent();
			} else {
				lines.setCurrent( line );			
			}
		}
	}

	/**
	 * @param directive
	 * @param line
	 * @param startPos
	 * @param endPos
	 * @param lines
	 * @param fileName
	 * @return
	 */
	private String processKeywords(String directive, String line, int startPos, int endPos, StringList lines, String fileName) {
		int startIndex = lines.getCurrentIndex();
		HashMap entriesByKeyword = new HashMap();
		Keyword[] myKeywords = this.keywords;
		String argumentStr = directive.substring( 9 ).trim();
		String[] fileNames = TextUtil.splitAndTrim( argumentStr, ' ');
		for (int i = 0; i < fileNames.length; i++) {
			String name = fileNames[i];
			if (name.length() > 0 ) {
				File file = new File( this.sourceDir.getAbsolutePath()
						+ File.separator + name );
				processKeywordsFile(file, name, fileName, entriesByKeyword, myKeywords, lines);
			}
		} // for each file
		insertKeywordIndex( entriesByKeyword, lines );
		lines.setCurrentIndex( startIndex );		
		return "";
	}

	/**
	 * @param file
	 * @param name
	 * @param fileName
	 * @param entriesByKeyword
	 * @param myKeywords
	 * @param lines
	 */
	private void processKeywordsFile(File file, String name, String fileName, HashMap entriesByKeyword, Keyword[] myKeywords, StringList lines) {
		try {
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					File subFile = files[i];
					processKeywordsFile(subFile, name, fileName, entriesByKeyword, myKeywords, lines);
				}
			} else {
				String[] fileLines = FileUtil.readTextFile( file );
				appendKeywordEntries( myKeywords, entriesByKeyword, name, new StringList( fileLines ));
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException("Unable to create keyword index for file " 
					+ name + " in source-file " + fileName + " line " 
					+ (lines.getCurrentIndex() + 1) + ": " + e.toString(), e );
		}
	}

	/**
	 * @param entriesByKeyword
	 */
	private void insertKeywordIndex(HashMap entriesByKeyword, StringList lines) {
		Keyword[] myKeywords = (Keyword[]) entriesByKeyword.keySet().toArray( new Keyword[entriesByKeyword.size()] );
		Arrays.sort( myKeywords, new KeywordComparator() );
		//char lastChar = myKeywords[0].indexKeywordLowercase.charAt(0);
		for (int i = 0; i < myKeywords.length; i++) {
			Keyword keyword = myKeywords[i];
			/*
			char c = keyword.indexKeywordLowercase.charAt(0);
			if (c != lastChar ) {
				lines.insert("<hr />");
				lines.next();
				lastChar = c;
			}
			*/
			lines.insert("<div class=\"keyword\">" + keyword.indexKeyword + "</div>");
			lines.next();
			ArrayList indexList = (ArrayList) entriesByKeyword.get( keyword );
			String[] index = createIndex( indexList );
			lines.insert( index );
			lines.setCurrentIndex( lines.getCurrentIndex() + index.length );
		}
	}

	/**
	 * @param myKeywords
	 * @param entriesByKeyword
	 */
	private void appendKeywordEntries(Keyword[] myKeywords, HashMap entriesByKeyword, String path, StringList lines) {
		IndexEntry lastH1 = null;
		IndexEntry lastH2 = null;
		IndexEntry lastH3 = null;
		IndexEntry currentEntry = null;
		HashMap keywordsForCurrentEntry = new HashMap();
		while (lines.next()) {
			String currentLine = lines.getCurrent();
			int tagPos = currentLine.indexOf("<h");
			if (tagPos != -1) {
				int idStartPos = currentLine.indexOf(" id=\"");
				if (idStartPos != -1) {
					int idEndPos = currentLine.indexOf( '"', idStartPos + 6 );
					if (idEndPos == -1)
					{
						String message = "Unable to process line " + currentLine  + " in " + path + ": id is not closed";
						throw new BuildException(message);
					}
					String id = currentLine.substring( idStartPos + 5, idEndPos );
					int headingStartPos = currentLine.indexOf( '>', idEndPos );
					int headingEndPos = currentLine.indexOf('<', headingStartPos );
					String heading = currentLine.substring( headingStartPos + 1, headingEndPos ).trim();
					int spacePos = currentLine.indexOf(' ', tagPos );
					String levelStr = currentLine.substring( tagPos + 2, spacePos );
					int level = Integer.parseInt( levelStr );
					String style = (String) this.indexStyles.get( levelStr );
					if (style == null) {
						style = getVariable( "index.h" + levelStr );
						if (style != null) {
							this.indexStyles.put( levelStr, style );
						}
					}
					IndexEntry entry = new IndexEntry(heading, id, level, style, path );
					switch ( level ) {
						case 1:
							lastH1 = entry; 
							entry.setParent( null );
							break;
						case 2: 
							lastH2 = entry; 
							entry.setParent( lastH1 );
							break;
						case 3:
							lastH3 = entry;
							entry.setParent( lastH2 );
							break;
						case 4:
							entry.setParent( lastH3 );
							break;
						default:
							entry.setParent( null );
					}
					currentEntry = entry;
					keywordsForCurrentEntry.clear();
				} 
				if (currentEntry != null) {// if line does not contain " id=""
					// search for keywords:
					String line = currentLine.toLowerCase();
					for (int i = 0; i < myKeywords.length; i++) {
						Keyword keyword =  myKeywords[i];
						if ( ( line.indexOf( keyword.searchkey ) != -1 ) 
								&& ( keywordsForCurrentEntry.get(keyword)  == null)) {
							// found a keyword-entry!
							ArrayList keywordEntries = (ArrayList) entriesByKeyword.get( keyword );
							if (keywordEntries == null) {
								keywordEntries = new ArrayList();
								entriesByKeyword.put( keyword, keywordEntries );
							}
							keywordEntries.add( currentEntry );
							keywordsForCurrentEntry.put( keyword, Boolean.TRUE );
						}
					}
				}
			} // if line contains "<h"
		} // while there are more lines		
	}

	/**
	 * @param directive
	 * @param line
	 * @param startPos
	 * @param endPos
	 * @param lines
	 * @param fileName
	 * @return
	 */
	private String processExternalIndex(String directive, String line, int startPos, int endPos, StringList lines, String fileName) {
		int startIndex = lines.getCurrentIndex();
		String argumentStr = directive.substring( 13 ).trim();
		String[] fileNames = TextUtil.splitAndTrim( argumentStr, ' ');
		ArrayList indexList = new ArrayList();
		for (int i = 0; i < fileNames.length; i++) {
			String name = fileNames[i];
			if (name.length() > 0 ) {
				File file = new File( this.sourceDir.getAbsolutePath()
						+ File.separator + name );
				
				processExternalIndexFile(file, name, fileName, indexList, lines);
			}
		} // for each file
		String[] index = createIndex( indexList );
		lines.insert( index );
		lines.setCurrentIndex( startIndex );
		return "";
	}

	/**
	 * @param file
	 * @param name
	 * @param fileName
	 * @param indexList
	 * @param lines
	 */
	private void processExternalIndexFile(File file, String name, String fileName, ArrayList indexList, StringList lines) {
		try {
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					File subFile = files[i];
					processExternalIndexFile(subFile, name, fileName, indexList, lines);
				}
			} else {
				String[] fileLines = FileUtil.readTextFile( file );
				appendIndexEntries( indexList, name, new StringList( fileLines ));
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException("Unable to create external index for file " 
					+ name + " in source-file " + fileName + " line " 
					+ (lines.getCurrentIndex() + 1) + ": " + e.toString(), e );
		}
	}

	/**
	 * Creates an internal index of a page.
	 * 
	 * @param directive
	 * @param line
	 * @param startPos
	 * @param endPos
	 * @param lines
	 * @param fileName
	 * @return
	 */
	private String processIndex(String directive, String line, int startPos, int endPos, StringList lines, String fileName) {
		int startIndex = lines.getCurrentIndex();
		String[] indexArray = getIndex( "", lines );
		// return to the initial line:
		lines.setCurrentIndex( startIndex );
		// insert the array:
		lines.insert( indexArray );
		return "";
	}
	
	private String[] getIndex( String path, StringList lines ) {
		ArrayList index = new ArrayList();
		appendIndexEntries( index, path, lines );
		// create the index:
		return createIndex( index );
	}
	
	private String[] createIndex( ArrayList index ) {
		String[] indexArray = new String[ index.size() ];
		IndexEntry[] entries = (IndexEntry[]) index.toArray( new IndexEntry[ index.size() ] );
		// sort the index if desired:
		if (isDefined("sortindex")) {
			//System.out.println("sorting entries (defined at " + (lines.getCurrentIndex() + ")."));
			Arrays.sort( entries, new IndexEntryComparator() );
		}
		for (int i=0; i < entries.length; i++ ) {
			IndexEntry entry = entries[i];
			if (entry.style != null) {
				indexArray[i] = "<div class=\"" + entry.style + "\"><a href=\""
					+ entry.path + "#"
					+ entry.id + "\">" + entry.name + "</a></div>";
			} else {
				StringBuffer buffer = new StringBuffer();
				for (int j = 0; j < entry.level; j++) {
					buffer.append("&nbsp;");
				}
				buffer.append( "<a href=\"" )
					.append( entry.path )
					.append( "#" )
					.append( entry.id )
					.append( "\">" )
					.append( entry.name )
					.append( "</a><br/>" );
				indexArray[i] = buffer.toString();
			}
		}
		return indexArray;	
	}

	private void appendIndexEntries( ArrayList index, String path, StringList lines ) {
		IndexEntry lastH1 = null;
		IndexEntry lastH2 = null;
		IndexEntry lastH3 = null;
		while (lines.next()) {
			String currentLine = lines.getCurrent();
			int tagPos = currentLine.indexOf("<h");
			if (tagPos != -1) {
				int idStartPos = currentLine.indexOf(" id=\"");
				if (idStartPos != -1) {
					int idEndPos = currentLine.indexOf( '"', idStartPos + 6 );
					String id = currentLine.substring( idStartPos + 5, idEndPos );
					int headingStartPos = currentLine.indexOf( '>', idEndPos );
					int headingEndPos = currentLine.indexOf('<', headingStartPos );
					String heading = currentLine.substring( headingStartPos + 1, headingEndPos ).trim();
					int spacePos = currentLine.indexOf(' ', tagPos );
					String levelStr = currentLine.substring( tagPos + 2, spacePos );
					int level = Integer.parseInt( levelStr );
					String style = (String) this.indexStyles.get( levelStr );
					if (style == null) {
						style = getVariable( "index.h" + levelStr );
						if (style != null) {
							this.indexStyles.put( levelStr, style );
						}
					}
					IndexEntry entry = new IndexEntry(heading, id, level, style, path );
					switch ( level ) {
						case 1:
							lastH1 = entry; 
							entry.setParent( null );
							break;
						case 2: 
							lastH2 = entry; 
							entry.setParent( lastH1 );
							break;
						case 3:
							lastH3 = entry;
							entry.setParent( lastH2 );
							break;
						case 4:
							entry.setParent( lastH3 );
							break;
						default:
							entry.setParent( null );
					}
					index.add( entry  );
				} // if line contains " id=""
			} // if line contains "<h"
		} // while there are more lines		
	}
	/**
	 * Includes a file.
	 * 
	 * @param directive
	 * @param line
	 * @param startPos
	 * @param endPos
	 * @param lines
	 * @param fileName
	 * @return
	 */
	private String processInclude(String directive, String line, int startPos, int endPos, StringList lines, String fileName) {
		String includeName = directive.substring( 8 ).trim();
		String[] myIncludes = (String[]) this.includesByName.get( includeName );
		if (myIncludes != null) {
			//lines.removeCurrent();
			lines.insert( myIncludes );
			return "";
		}
		String fullName = this.includesDir.getAbsolutePath() + File.separator
			+ includeName;
		try {
			myIncludes = FileUtil.readTextFile( new File( fullName ));
			this.includesByName.put( myIncludes, includeName );
			lines.removeCurrent();
			lines.insert( myIncludes );
		} catch (IOException e) {
			throw new BuildException("Invalid includes-directive in file " 
					+ fileName + " line " + (lines.getCurrentIndex() + 1) 
					+ ": unable to include file " + includeName + ": " + e.toString(), e  );
		}
		return "";
	}

	/**
	 * @param directive
	 * @param line
	 * @param startPos
	 * @param endPos
	 * @param lines
	 * @param fileName
	 * @return
	 */
	private String processIfdef(String directive, String line, int startPos, int endPos, StringList lines, String fileName) {
		String symbol = directive.substring( 6 ).trim();
		processIfVariation( isDefined( symbol ), lines, fileName );
		return "";
	}
	
	private String processIfndef(String directive, String line, int startPos, int endPos, StringList lines, String fileName) {
		String symbol = directive.substring( 7 ).trim();
		processIfVariation( !isDefined( symbol ), lines, fileName );
		return "";
	}
	
	protected Directive getDirective( String line, StringList lines, String fileName ) {
		int startPos = line.indexOf( "<%" );
		if (startPos == -1) {
			return null;
		}
		int endPos = line.indexOf( "%>" );
		if (endPos < startPos ) {
			throw new BuildException("Invalid webprocessor-directive in file " 
					+ fileName + " line " + (lines.getCurrentIndex() + 1) 
					+ ": directive not closed: " + line );
		}
		return new Directive( line.substring( startPos + 2, endPos ).trim(), startPos, endPos );
	}

	/**
	 * @param b
	 * @param lines
	 * @param fileName
	 */
	private void processIfVariation(boolean conditionFulfilled, StringList lines, String fileName) {
		//System.out.println("processing if-variation (" + lines.getCurrent() + ") - conditionFulfilled=" + conditionFulfilled );
		this.currentIfLevel++;
		int myIfLevel = this.currentIfLevel;
		boolean conditionHasBeenFulfilled = conditionFulfilled;
		boolean endifFound = false;
		boolean elseFound = false;
		lines.removeCurrent();
		String line = lines.getCurrent();
		while (true) {
			Directive directive = getDirective( line, lines, fileName );	
			if (directive != null) {
				//System.out.println("found directive in line " + line);
				if (directive.directive.startsWith("endif")) {
					if (myIfLevel == this.currentIfLevel) {
						endifFound = true;
						lines.removeCurrent();
						break;
					} else {
						this.currentIfLevel--;
					}
				} else if (directive.directive.startsWith("ifdef") ) {
					if (conditionFulfilled) {
						processIfdef( directive.directive, line, directive.startPos, directive.endPos, lines, fileName );
					} else {
						this.currentIfLevel++;
					}
				} else if (directive.directive.startsWith("ifndef") ) {
					if (conditionFulfilled) {
						processIfndef( directive.directive, line, directive.startPos, directive.endPos, lines, fileName );
					} else {
						this.currentIfLevel++;
					}
				} else if (directive.directive.startsWith("else") 
						&& (myIfLevel == this.currentIfLevel)) {
					if (elseFound) {
						throw new BuildException("Invalid if-directive in file " 
								+ fileName + ": found two <%else %> tags." );						
					}
					elseFound = true;
					conditionFulfilled = !conditionHasBeenFulfilled;
					if (conditionFulfilled) {
						lines.removeCurrent();
					}
				} else if (directive.directive.startsWith("elifdef ") 
						&& (myIfLevel == this.currentIfLevel)) {
					if (elseFound) {
						throw new BuildException("Invalid elifdef-directive in file " 
								+ fileName + ": elifdef cannot come after <%else %> tags." );						
					}
					if (!conditionHasBeenFulfilled) {
						String symbol = directive.directive.substring( 8 ).trim();
						conditionFulfilled = isDefined( symbol );
						if (conditionFulfilled) {
							conditionHasBeenFulfilled = true;
							lines.removeCurrent();
						}
					} else {
						conditionFulfilled = false;
					}
				} else if (directive.directive.startsWith("elifndef ") 
						&& (myIfLevel == this.currentIfLevel)) {
					if (elseFound) {
						throw new BuildException("Invalid elifndef-directive in file " 
								+ fileName + ": elifdef cannot come after <%else %> tags." );						
					}
					if (!conditionHasBeenFulfilled) {
						String symbol = directive.directive.substring( 9 ).trim();
						conditionFulfilled = !isDefined( symbol );
						if (conditionFulfilled) {
							conditionHasBeenFulfilled = true;
							lines.removeCurrent();
						}
					} else {
						conditionFulfilled = false;
					}
				}
			}
			if (conditionFulfilled) {
				processLine( lines.getCurrent(), lines, fileName );
				if (!lines.next()) {
					throw new BuildException("Invalid if-directive in file " 
							+ fileName + " no <%endif %> found." );					
				}
			} else {
				line = lines.removeCurrent();
				//System.out.println("removed line " + line);
			}

			line = lines.getCurrent();
			//System.out.println("new line " + line );
		} // while there are more lines
		if (!endifFound) {
			throw new BuildException("Invalid if-directive in file " 
					+ fileName + " no <%endif %> found." );
		}
		this.currentIfLevel--;
	}

	/**
	 * @param directive
	 * @param line
	 * @param startPos
	 * @param endPos
	 * @param lines
	 * @param fileName
	 */
	private String processUndefine(String directive, String line, int startPos, int endPos, StringList lines, String fileName) {
		String symbol = directive.substring( 9 ).trim().toLowerCase();
		this.tempSymbols.remove( symbol );
		return "";	
	}

	/**
	 * @param directive
	 * @param line
	 * @param startPos
	 * @param endPos
	 * @param lines
	 * @param fileName
	 */
	private String processDefine(String directive, String line, int startPos, int endPos, StringList lines, String fileName) {
		String symbol = directive.substring( 7 ).trim().toLowerCase();
		this.tempSymbols.put( symbol, Boolean.TRUE );
		return "";
	}

	/**
	 * @param directive
	 * @param line
	 * @param startPos
	 * @param endPos
	 * @param lines
	 * @param fileName
	 */
	private String processSetVar(String directive, String line, int startPos, int endPos, StringList lines, String fileName) {
		int pos = directive.indexOf( '=');
		if (pos == -1) {
			throw new BuildException("Invalid <%set ... %> directive in file " 
					+ fileName + " line " + (lines.getCurrentIndex() + 1) 
					+ ": no \"=\" sign found.");
			
		}
		String name = directive.substring( 3, pos ).trim();
		String value = directive.substring( pos + 1).trim();
		addVariable( name, value );
		return "";
	}

	/**
	 * Processes the "<%= var-name %> directive"
	 * @param directive
	 * @param startPos
	 * @param endPos
	 * @param lines
	 * @param fileName
	 */
	private String processInsertVar(String directive, String line, int startPos, int endPos, StringList lines, String fileName) {
		String varName = directive.substring( 2 ).trim();
		String value = getVariable( varName );
		if (value == null) {
			throw new BuildException("Invalid <%= ... %> directive in file " 
					+ fileName + " line " + (lines.getCurrentIndex() + 1) 
					+ ": the variable \"" + varName + "\" is not defined.");
		}
		return value;
	}
	
	class Directive {
		public String directive;
		public int startPos;
		public int endPos;
		
		public Directive(String directive, int startPos, int endPos) {
			this.directive = directive;
			this.startPos = startPos;
			this.endPos = endPos;
		}
	}
}
