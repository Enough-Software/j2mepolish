/*
 * Created on Dec 28, 2006 at 12:21:56 AM.
 * 
 * Copyright (c) 2006 Robert Virkus / Enough Software
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
package de.enough.polish.sourceparser;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.enough.polish.util.StringUtil;

/**
 * <p>Parses Java source code and provides reflection-like information about it.</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        Dec 28, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class JavaSourceClass {
	
	protected static final String JAVA_VAR_STR = "\\w+[\\w|\\.|_]*(<\\w*>)?(\\[\\s*\\])?";

	protected static final String PACKAGE_STR = "package\\s+" + JAVA_VAR_STR;
	protected static final Pattern PACKAGE_PATTERN = Pattern.compile( PACKAGE_STR );
	protected static final String IMPORT_STR = "import\\s+" + JAVA_VAR_STR + "\\*?";
	protected static final Pattern IMPORT_PATTERN = Pattern.compile( IMPORT_STR );
	protected static final String CLASSNAME_STR = "(class|interface)\\s+\\w+[_|\\w]*";
	protected static final Pattern CLASSNAME_PATTERN = Pattern.compile( CLASSNAME_STR );
	protected static final String EXTENDS_STR = "extends\\s+" + JAVA_VAR_STR;
	protected static final Pattern EXTENDS_PATTERN = Pattern.compile( EXTENDS_STR );
	protected static final String IMPLEMENTS_STR = "implements\\s+" + JAVA_VAR_STR + "(\\s*\\,\\s*" + JAVA_VAR_STR + ")*";
	protected static final Pattern IMPLEMENTS_PATTERN = Pattern.compile( IMPLEMENTS_STR );
	protected static final String METHOD_STR = "((public|private|protected|abstract|static|final|synchronized)\\s*)*\\s*" // modifier
												+ JAVA_VAR_STR + "\\s*" // return type
												+ "\\w+[_|\\w]*\\s*\\(\\s*" // name and opening parentheses
												+ "(" + JAVA_VAR_STR + "\\s+\\w+[_|\\w]*\\s*\\,?\\s*)*" // optional parameters
												+ "\\)\\s*" // closing parentheses
												+ "(throws\\s+" + JAVA_VAR_STR + "(\\s*\\,\\s*" + JAVA_VAR_STR + ")*)?"; // optional throws clause
	protected static final Pattern METHOD_PATTERN = Pattern.compile( METHOD_STR );
	
	private String packageName;
	private String[] importStatements;
	private String className;
	private boolean isClass;
	private String extendsStatement;
	private String[] implementedInterfaces;
	private JavaSourceMethod[] methods;

	
	/**
	 * Creates a new Java source file.
	 * 
	 * @param lines the source code
	 */
	public JavaSourceClass( String[] lines ) {
		parse( lines );
	}

	/**
	 * Parses the given Java source code.
	 * 
	 * @param lines the source code
	 */
	public void parse(String[] lines) {
		lines = removeComments( lines );
		boolean isPackageResolved = false;
		this.packageName = null;
		this.importStatements = null;
		boolean isClassNameResolved = false;
		this.className = null;
		this.extendsStatement = null;
		this.implementedInterfaces = null;
		ArrayList imports = new ArrayList();
		ArrayList methods = new ArrayList();
		
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (!isPackageResolved) {
				int packageStartPos = line.indexOf("package");
				if (packageStartPos != -1) {
					Matcher matcher = PACKAGE_PATTERN.matcher( line );
					if (matcher.find()) {
						this.packageName = matcher.group().substring( "package ".length() ).trim();
						isPackageResolved = true;
					}
				}
			}
			if (!isClassNameResolved) {
				// check for import statement:
				if (line.indexOf("import") != -1) {
					Matcher matcher = IMPORT_PATTERN.matcher( line );
					while (matcher.find()) {
						String importStatement = matcher.group().substring( "import ".length() ).trim();
						imports.add( importStatement );
					}
				}
				// check for class or interface name:
				if (line.indexOf("class") != -1 || line.indexOf("interface") != -1) {
					Matcher matcher = CLASSNAME_PATTERN.matcher( line );
					if (matcher.find()) {
						String group = matcher.group();
						if (group.charAt(0) == 'c') { // starts with "class"
							this.className = group.substring( "class ".length() ).trim();
							this.isClass = true;
						} else { // this is an interface
							this.isClass = false;
							this.className = group.substring( "interface ".length() ).trim();
						}
						isClassNameResolved = true;
						isPackageResolved = true;
						// now check for extends and implements:
						StringBuffer buffer = new StringBuffer();
						buffer.append( line );
						while (line.indexOf('{') == -1  && i < lines.length -1 ) {
							i++;
							line = lines[i];
							buffer.append(' ').append( line );
						}
						line = buffer.toString();
						//System.out.println("class line=" + line);
						// retrieve extends clause:
						matcher = EXTENDS_PATTERN.matcher( line );
						if (matcher.find()) {
							this.extendsStatement = matcher.group().substring("extends ".length()).trim();
						}
						// retrieve implements clause:
						matcher = IMPLEMENTS_PATTERN.matcher( line );
						if (matcher.find()) {
							this.implementedInterfaces = StringUtil.splitAndTrim( matcher.group().substring("implements ".length()), ',' );
						}
					}
				}
			} else { // the class name has been resolved, now methods can be parsed:
				StringBuffer buffer = new StringBuffer();
				buffer.append( line );
				while ( line.indexOf('{') == -1 && line.indexOf(';') == -1 && i < lines.length -1 ) {
					i++;
					line = lines[i];
					buffer.append(' ').append( line );
				}
				line = buffer.toString();
				if (line.indexOf('(') != -1) {
					Matcher matcher = METHOD_PATTERN.matcher( line );
					if (matcher.find()) {
						String group = matcher.group();
						// read name, return type and modifier:
						int parenthesesStart = group.indexOf('(');
						String methodStart = group.substring(0, parenthesesStart).trim();
						int lastSpacePos = Math.max( methodStart.lastIndexOf(' '), methodStart.lastIndexOf('\t') );
						String methodName = methodStart.substring( lastSpacePos + 1 );
						methodStart = methodStart.substring( 0, lastSpacePos ).trim();
						lastSpacePos = Math.max( methodStart.lastIndexOf(' '), methodStart.lastIndexOf('\t') );
						String modifier = "";
						String returnType = methodStart;
						if (lastSpacePos != -1) {
							returnType = methodStart.substring( lastSpacePos + 1 );
							modifier = methodStart.substring( 0, lastSpacePos ).trim();
						}
						// read method params:
						int parenthesesEnd = group.indexOf(')');
						String paramsStr =  group.substring(parenthesesStart + 1, parenthesesEnd).trim();
						String[] pTypes = null;
						String[] pNames =  null;
						if (paramsStr.length() > 0) {
							String[] params = StringUtil.splitAndTrim(paramsStr, ',');
							pTypes = new String[ params.length ];
							pNames = new String[ params.length ];
							for (int j = 0; j < params.length; j++) {
								String param = params[j];
								int spacePos = Math.max( param.lastIndexOf(' '), param.lastIndexOf('\t') );
								pTypes[j] = param.substring( 0, spacePos ).trim();
								pNames[j] = param.substring( spacePos + 1 );
							}
						}
						// read throws declaration:
						int throwsIndex = group.indexOf("throws", parenthesesEnd );
						String[] thrownExceptions = null;
						if (throwsIndex != -1) {
							thrownExceptions = StringUtil.splitAndTrim( group.substring(throwsIndex + "throws ".length() ), ',');
						}
						
						JavaSourceMethod method = new JavaSourceMethod( this, modifier, returnType, methodName, pTypes, pNames, thrownExceptions );
						methods.add( method );
					} // pattern match found for method
				}
			}
		}
		
		this.importStatements = (String[]) imports.toArray( new String[ imports.size() ] );
		this.methods = (JavaSourceMethod[]) methods.toArray( new JavaSourceMethod[methods.size()] );
	}

	/**
	 * Removes any Java comments from the given source code.
	 * 
	 * @param lines the original source code
	 * @return the source code without any comments and empty lines and blank space removed
	 */
	public static String[] removeComments(String[] lines) {
		ArrayList cleanCode = new ArrayList( lines.length );
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i].trim();
			int startCommentPos = line.indexOf("/*");
			int startLine = i;
			while (startCommentPos != -1) {
				String remainingLine = removeSingleLineComment( line.substring(0, startCommentPos).trim() );
				if (remainingLine != null) {
					cleanCode.add( line );
				}
				int endCommentPos = line.indexOf("*/");
				while (endCommentPos == -1 && i < lines.length - 1) {
					i++;
					line = lines[i];
					endCommentPos = line.indexOf("*/");
				}
				if (endCommentPos != -1) {
					line = line.substring( endCommentPos + 2 ).trim();				
					startCommentPos = line.indexOf("/*");
					startLine = i;
				} else {
					for (int j = 0; j < Math.max( 4, lines.length); j++) {
						System.out.println( lines[j] );
					}
					throw new RuntimeException("Unfinished '/*' comment in source code, line " + startLine + ": " + lines[startLine]);
				}
			}
			line = removeSingleLineComment(line);
			if (line != null) {
				cleanCode.add( line );
			}
		}
		return (String[]) cleanCode.toArray( new String[ cleanCode.size() ] );
	}

	private static String removeSingleLineComment(String line) {
		if (line.length() == 0) {
			return null;
		}
		int singleLineCommentPos = line.indexOf("//");
		if (singleLineCommentPos == 0) {
			return null;
		} else if (singleLineCommentPos != -1) {
			line = line.substring( 0, singleLineCommentPos ).trim();
		}
		return line;
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return this.className;
	}

	/**
	 * @return the importStatements
	 */
	public String[] getImportStatements() {
		return this.importStatements;
	}

	/**
	 * @return the isClass
	 */
	public boolean isClass() {
		return this.isClass;
	}

	/**
	 * @return the packageName
	 */
	public String getPackageName() {
		return this.packageName;
	}

	/**
	 * @return the extendsStatement
	 */
	public String getExtendsStatement() {
		return this.extendsStatement;
	}

	/**
	 * @return the implementedInterfaces
	 */
	public String[] getImplementedInterfaces() {
		return this.implementedInterfaces;
	}

	/**
	 * @return the methods
	 */
	public JavaSourceMethod[] getMethods() {
		return this.methods;
	}

	/**
	 * @param methods the methods to set
	 */
	public void setMethods(JavaSourceMethod[] methods) {
		this.methods = methods;
	}

	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @param extendsStatement the extendsStatement to set
	 */
	public void setExtendsStatement(String extendsStatement) {
		this.extendsStatement = extendsStatement;
	}

	/**
	 * @param implementedInterfaces the implementedInterfaces to set
	 */
	public void setImplementedInterfaces(String[] implementedInterfaces) {
		this.implementedInterfaces = implementedInterfaces;
	}

	/**
	 * @param importStatements the importStatements to set
	 */
	public void setImportStatements(String[] importStatements) {
		this.importStatements = importStatements;
	}

	/**
	 * @param isClass the isClass to set
	 */
	public void setClass(boolean isClass) {
		this.isClass = isClass;
	}

	/**
	 * @param packageName the packageName to set
	 */
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public String[] renderCode() {
		ArrayList code = new ArrayList();
		
		if (this.packageName != null) {
			code.add( "package " + this.packageName + ";");
		}
		String[] imports = getImportStatements();
		for (int i = 0; i < imports.length; i++) {
			code.add( "import " + imports[i] + ";" );
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append("public ");
		if (this.isClass) {
			buffer.append("class ");
		} else {
			buffer.append("interface ");
		}
		buffer.append(this.className );
		if (this.extendsStatement != null) {
			buffer.append( " extends ").append( this.extendsStatement );
		}
		if (this.implementedInterfaces != null && this.implementedInterfaces.length != 0) {
			buffer.append(" implements ");
			for (int i = 0; i < this.implementedInterfaces.length; i++) {
				buffer.append( this.implementedInterfaces[i] );
				if (i != this.implementedInterfaces.length - 1) {
					buffer.append( ", ");
				}
			}
		}
		code.add( buffer.toString() );
		code.add( "{"); 
		for (int i = 0; i < this.methods.length; i++) {
			code.add("");
			this.methods[i].renderCode(code, this.isClass);		
		}
		
		code.add( "}");
		return (String[]) code.toArray( new String[code.size()] );
	}

	public void addImport(String importStatement) {
		if (this.importStatements == null || this.importStatements.length == 0) {
			this.importStatements = new String[]{ importStatement };
		} else {
			String[] newImports = new String[ this.importStatements.length + 1 ];
			System.arraycopy( this.importStatements, 0, newImports, 0, this.importStatements.length);
			newImports[this.importStatements.length] = importStatement;
			this.importStatements = newImports;
		}
	}

	public void addMethod(JavaSourceMethod method) {
		if (this.methods == null || this.methods.length == 0) {
			this.methods = new JavaSourceMethod[]{ method };
		} else {
			JavaSourceMethod[] newMethods = new JavaSourceMethod[ this.methods.length + 1 ];
			System.arraycopy( this.methods, 0, newMethods, 0, this.methods.length);
			newMethods[this.methods.length] = method;
			this.methods = newMethods;
		}
	}

}
