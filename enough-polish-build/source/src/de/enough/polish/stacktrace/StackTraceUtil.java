/*
 * Created on 08-Oct-2004 at 15:22:52.
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
package de.enough.polish.stacktrace;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.enough.polish.Environment;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.StringUtil;


/**
 * <p>Translates the stacktrace given by J2ME emulators to a normal error message stating the line-number and the source-code file.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        08-Oct-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public final class StackTraceUtil {
	private final static String STACK_TRACE_PATTERN_STR = "at\\s+[\\w|\\.|<|>]+\\(\\+\\d+\\)";
	private final static Pattern STACK_TRACE_PATTERN = Pattern.compile( STACK_TRACE_PATTERN_STR );
	private final static String METHOD_PATTERN_STR = "([public|private]protected]\\s+)?\\w+s*\\w+\\s*\\(";
	private final static Pattern METHOD_PATTERN = Pattern.compile( METHOD_PATTERN_STR ); 
	private final static String CONSTRUCTOR_PATTERN_STR = "([public|private]protected]\\s+)?\\w+s*\\(";
	private final static Pattern CONSTRUCTOR_PATTERN = Pattern.compile( CONSTRUCTOR_PATTERN_STR );
	// needed for starting threads:
	private final static StackTraceUtil INSTANCE = new StackTraceUtil();
	
	private String sourceCodeLine;
	private String decompiledCodeSnippet;
	private int linesFromMethodStart;
	
	private StackTraceUtil() 
	{
		// no initialization done here
	}
	
	private BinaryStackTrace translateStackTrace( String message, String classPath, String preprocessedSourcePath, File[] sourceDirs, Environment environment, String className, String methodName, String offset )
	throws DecompilerNotInstalledException
	{
		// decompile the class-code:
		String[] lines = decompile( className, classPath, environment );
		if ( lines == null) {
			// class could not be found:
			return null;
		}
		boolean searchForConstructor = false;
		if ("<init>".equals( methodName )) {
			searchForConstructor = true;
			methodName = className;
			int dotIndex = methodName.lastIndexOf('.');
			if (dotIndex != -1) {
				methodName = methodName.substring( dotIndex + 1);
			}
		}
		// try to find the method-call which is in the error-message:
		String[] methodLines = parseMethod( lines, methodName, offset, searchForConstructor );
		if (methodLines.length == 0) {
			//System.out.println("Unable to find decompiled method-call.");
			return null;
		}
		// now try to find the original source-code-line:
		String sourceMessage = getSourceFilePosition( className, methodName, searchForConstructor, this.sourceCodeLine, preprocessedSourcePath, sourceDirs, environment );
		
		return new BinaryStackTrace( message, sourceMessage, methodLines, this.decompiledCodeSnippet );
	}

	/**
	 * Retrieves the position of the decompiled code in the source code.
	 * @param className the name of the class
	 * @param methodName the name of the method.
	 * @param searchForConstructor when the &lt;init&gt;-method is looked for
	 * @param sourceCode
	 * @param preprocessedSourcePath
	 * @param sourceDirs
	 * @param environment
	 * @return the original line in the source code
	 */
	private String getSourceFilePosition(String className, String methodName, boolean searchForConstructor, String sourceCode, String preprocessedSourcePath, File[] sourceDirs, Environment environment) 
	{
		// first find the position in the preprocessed source code
		// secondly find the original source-file in the provided source-files.
		
		// find position of call 
		String javaFileName = StringUtil.replace( className, '.', File.separatorChar )  + ".java";
		preprocessedSourcePath = preprocessedSourcePath + File.separatorChar + javaFileName;

		this.linesFromMethodStart -= 2;
		if (this.linesFromMethodStart < 1) {
			this.linesFromMethodStart = 1;
		}
		//System.out.println("searching for source-code [" + sourceCode + "] in method [" + methodName + "].");
		CodeSequence codeSequence = new CodeSequence( sourceCode );
		try {
			String[] lines = FileUtil.readTextFile( preprocessedSourcePath );
			int sourceFilePos = -1;
			String preprocessedSourceCodeLine = null;
			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];
				if (line.indexOf(methodName) != -1) {
					Matcher matcher;
					if (searchForConstructor) {
						matcher = CONSTRUCTOR_PATTERN.matcher( line );
					} else {
						matcher = METHOD_PATTERN.matcher(line);
					}
					char firstChar = line.trim().charAt(0);
					if ( (firstChar == '*')
							|| (firstChar == '/')
							|| (!matcher.find()) 
							|| (matcher.group().startsWith("return")) ) {
						continue;
					}
					//System.out.println("found method in line [" + i + "]: " + line );
					int start = i + this.linesFromMethodStart;
					if (start > lines.length) {
						start = i;
					}
					int toAdd = 10;
					if (this.linesFromMethodStart > 20) {
						toAdd = this.linesFromMethodStart/2;
					}
					int end = start + toAdd;
					if (end > lines.length) {
						end = lines.length;
					}
					//System.out.println("lines from method start: " + this.linesFromMethodStart  + " checking range from " + start + " to " + end + " in " + preprocessedSourcePath);
					for (int j = start; j < end; j++) {
						line = lines[j];
						if ( codeSequence.matches( line ) ) {
							sourceFilePos = j;
							preprocessedSourceCodeLine = line.trim();
							break;
						}						
					}
					if (sourceFilePos != -1) {
						break;
					} else {
						// try again from the method-start until [start]:
						for (int j = i+1; j < start; j++) {
							line = lines[j];
							if ( codeSequence.matches( line ) ) {
								sourceFilePos = j;
								preprocessedSourceCodeLine = line.trim();
								break;
							}						
						}
						if (sourceFilePos != -1) {
							break;
						//} else {						
						//	System.out.println("unable to call from lines " + (i+1) + " to " + end ) ;
						}
					}
				}
			}
			if ( sourceFilePos == -1 ) {
				//System.out.println("did not find source position");
				return null;
			}
			// okay, we found the position of the faulty call:
			// search for the original source file:
			for (int i = 0; i < sourceDirs.length; i++) {
				File dir = sourceDirs[i];
				File originalFile = new File( dir.getAbsolutePath() + File.separatorChar + javaFileName );
				if (originalFile.exists()) {
					return  "[javac] " + originalFile.getAbsolutePath() + ":" + (sourceFilePos+1) + ": " + preprocessedSourceCodeLine;
				}
			}
			// did not find original file:
			// now return the reference to the preprocessed source class file:
			int separatorPos = javaFileName.lastIndexOf( File.separatorChar );
			if (separatorPos != -1) {
				javaFileName = javaFileName.substring( separatorPos + 1 );
			}
			return javaFileName + ":" + (sourceFilePos+1) + ": " + preprocessedSourceCodeLine;
		} catch (FileNotFoundException e) {
			System.out.println("Unable to resolve stacktrace: did not find source file [" + preprocessedSourcePath + "].");
			return null;
		} catch (IOException e) {
			System.out.println("Unable to resolve stacktrace: unable to read source file [" + preprocessedSourcePath + "]: " + e );
			return null;
		}
	}


	/**
	 * @param lines
	 * @param methodName
	 * @param offset
	 * @param searchForConstructor
	 * @return array of decompiled method lines
	 */
	private String[] parseMethod(String[] lines, String methodName, String offset, boolean searchForConstructor) {
		ArrayList methodLines = new ArrayList();
		String offsetKey = " " + offset + ":";
		String methodKey = methodName.concat( "(" );
		boolean isInMethod = false;
		int parenthesisCount = 0;
		int methodStart = 0;
		int linesFromMethod = 0;
		boolean offsetFound = false;
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			String trimmedLine = line.trim();
			if (trimmedLine.length() == 0) {
				continue;
			}
			char firstChar = trimmedLine.charAt(0); 
			if ( firstChar == '/') {
				// this is binary code
				if (isInMethod && !offsetFound) {
					if (trimmedLine.indexOf( offsetKey ) != -1) {
						offsetFound = true;
						// now save the actual code-snippet:
						int codeStart = 0;
						for (int j=i; j >= methodStart; --j ) {
							String prevLine = lines[j].trim();
							boolean isComment = (prevLine.length() > 0) && (prevLine.charAt(0) == '/');
							if ( !isComment ) {
								if ( ( prevLine.length() > 1)
										&& !"break;".equals(prevLine)
										&& !"} else".equals(prevLine)
										) {
									codeStart = j;
									break;
								}
							}
						}
						this.sourceCodeLine = lines[ codeStart ].trim();
						StringBuffer decompiledCodeBuffer = new StringBuffer();
						decompiledCodeBuffer.append( this.sourceCodeLine )
						                     .append('\n');
						for (int j = codeStart+1; j < lines.length; j++) {
							String snippet = lines[j];
							if (snippet.indexOf('/') == -1) {
								break;
							}
							decompiledCodeBuffer.append( snippet )
										.append('\n');
						}
						this.decompiledCodeSnippet = decompiledCodeBuffer.toString();
					}
				}
			} else {
				// this is source code
				if (isInMethod) {
					if (firstChar == '{') {
						parenthesisCount++;
					} else if ( (firstChar == '}' && !"};".equals(trimmedLine) ) 
							|| "} else".equals(trimmedLine)) 
					{
						parenthesisCount--;
						if (parenthesisCount == 0 ) {
							if (offsetFound) {
								for (int j=methodStart; j <= i; j ++) {
									methodLines.add( lines[j] );
								}
								break;
							}
							isInMethod = false;
							offsetFound = false;
						}
					} else {
						// this is a normal instruction:
						linesFromMethod++;
					}
				} else {
					// a method-start includes the name of the method an _no_ semicolon.
					// (then it is probably a call of that method!)
					if ((trimmedLine.indexOf( methodKey) != -1) && (trimmedLine.indexOf(';') == -1) ) {
						isInMethod = true; 
						methodStart = i;
					}
				}
			}
		}
		this.linesFromMethodStart = linesFromMethod;
		return (String[]) methodLines.toArray( new String[ methodLines.size()] );
	}

	/**
	 * Decompiles the given class.
	 * 
	 * @param className
	 * @param classPath
	 * @param environment
	 * @return array of decompiled lines of the class, null when the class is not found
	 * @throws DecompilerNotInstalledException
	 */
	private static String[] decompile(String className, String classPath, Environment environment ) 
	throws DecompilerNotInstalledException
	{
		className = StringUtil.replace( className, '.', File.separatorChar );
		classPath = classPath + File.separatorChar + className + ".class";
		File classFile = new File( classPath );
		if (!classFile.exists()) {
			//System.out.println("class " + className + " does not exist");
			return null;
		}
		String jadExecutable = null;
		String polishHome = environment.getVariable("polish.home");
		if (polishHome != null) {
			if (File.separatorChar == '\\') {
				// this is a windows OS
				jadExecutable = polishHome + File.separatorChar + "bin" + File.separatorChar + "jad.exe";
			} else {
				jadExecutable = polishHome + File.separatorChar + "bin" + File.separatorChar + "jad";
			}
			if (!new File(jadExecutable).exists()) {
				jadExecutable = null;
			}
		}
		if (jadExecutable == null) {
			if (File.separatorChar == '\\') { // this is a windows OS
				jadExecutable = "jad.exe";
			} else { 
				jadExecutable = "jad";
			}
		}
		String[] arguments = new String[]{jadExecutable, "-a", "-p", classPath };
		try {
			Process process = Runtime.getRuntime().exec(arguments);
			ArrayList lines = new ArrayList();
			StringBuffer log = new StringBuffer( 300 );
			int c;
			InputStream input = process.getInputStream();
			// the error stream needs to be read, so that
			// the process is not blocked under windows:
			new EmptyInputStreamThread( process.getErrorStream() ).start();
			//BufferedInputStream input = new BufferedInputStream( process.getInputStream() );
			// using careful reading, because
			// in windows the input.read() got stuck otherwise:
			while ( (c = input.read() ) != -1 ) {
				if (c == '\n') {
					String line = log.toString();
					//System.out.println("decompiled: " + line);
					lines.add( line );
					log.delete(0, log.length() );
				}  else if (c != '\r') {
					log.append((char) c);
					//System.out.print( (char) c + "." );
					//System.out.print( (char) c + "=" + c );
				}
			}
			//System.out.println("done reading");
			input.close();
			return (String[]) lines.toArray( new String[ lines.size() ] );
		} catch (IOException e) {
			//e.printStackTrace();
			throw new DecompilerNotInstalledException( "Unable to start the \"jad\"-decompiler: [" + e.toString() + "]. jad is available from http://www.kpdus.com/jad.html.");
		}
	}
	
	public static BinaryStackTrace translateStackTrace( String message, String classPath, String preprocessedSourcePath, File[] sourceDirs , Environment environment )
	throws DecompilerNotInstalledException
	{
		Matcher matcher = STACK_TRACE_PATTERN.matcher(message);
		if (!matcher.find()) {
			return null;
		}
		String errorMessage = matcher.group();
		int offsetStart = errorMessage.indexOf('(');
		String classNameWithMethod = errorMessage.substring( 3, offsetStart ).trim();
		int methodStart = classNameWithMethod.lastIndexOf('.');
		if (methodStart == -1) {
			System.out.println("Unable to translate stack trace: could not resolve method-name from error-message [" + message + "].");
			return null;
		}
		String className = classNameWithMethod.substring(0, methodStart );
		String methodName = classNameWithMethod.substring( methodStart + 1);
		String offset = errorMessage.substring( offsetStart + 2, errorMessage.length() -1 );
		
		StackTraceUtil utility = new StackTraceUtil();		
		return utility.translateStackTrace(message, classPath, preprocessedSourcePath, sourceDirs, environment, className, methodName, offset);
	}
	
	private static class EmptyInputStreamThread extends Thread {

		private final InputStream input;
		
		public EmptyInputStreamThread( InputStream input ) {
			this.input = input;
		}
		
		public void run() {
			try {
				while ( this.input.read() != -1 ) {
					// do nothing
				}
			} catch (IOException e) {
				// ignore
			}
		}
			
	}
	
	private static class CodeSequence {
		private final String[] chunks;
		private final String code;
		
		public CodeSequence( String code ) {
			//System.out.println("decompiled code: " + code);
			this.code = code;
			ArrayList chunksList = new ArrayList();
			int lastDelimiterPos = 0;
			char[] chars = code.toCharArray();
			for (int i = 0; i < chars.length; i++) {
				char c = chars[i];
				if (!Character.isJavaIdentifierPart(c)) {
					int count = i - lastDelimiterPos; 
					if (count > 1) {
						String chunk = new String( chars, lastDelimiterPos, count ).trim();
						if (chunk.length() > 0) {
							//System.out.println("chunk: [" + chunk + "]." );
							chunksList.add( chunk );
						}
					}
					lastDelimiterPos = i + 1;
				}
			}
			this.chunks = (String[]) chunksList.toArray( new String[ chunksList.size() ] );
		}
		
		public boolean matches( String line ) {
			if (this.chunks.length == 0) {
				return line.indexOf( this.code ) != -1;
			}
			int minPos = 0; 
			int newPos;
			for (int i = 0; i < this.chunks.length; i++) {
				String chunk = this.chunks[i];
				if ( (newPos = line.indexOf( chunk, minPos)) != -1 ) {
					minPos = newPos + chunk.length();
				} else {
					/* 
					if (minPos > 0) {
						System.out.println("line does not match: " + line );
						System.out.println("min pos is: " + minPos + " actualPos: " + newPos + " chunk: " + chunk + " index: " + i );
					}
					*/
					return false;
				}
			}
			return true;
		}
	}

}
