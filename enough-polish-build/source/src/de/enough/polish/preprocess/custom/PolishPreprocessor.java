/*
 * Created on 21-Jun-2004 at 13:05:20.
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
package de.enough.polish.preprocess.custom;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.enough.polish.BuildException;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.build.PreprocessorSetting;
import de.enough.polish.preprocess.CustomPreprocessor;
import de.enough.polish.preprocess.Preprocessor;
import de.enough.polish.preprocess.css.CssAttribute;
import de.enough.polish.preprocess.css.CssAttributesManager;
import de.enough.polish.sourceparser.JavaSourceClass;
import de.enough.polish.sourceparser.JavaSourceMethod;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.IntegerIdGenerator;
import de.enough.polish.util.StringList;
import de.enough.polish.util.StringUtil;

/**
 * <p>Makes some standard preprocessing like the determination whether the Ticker-class is used etc.</p>
 *
 * <p>Copyright Enough Software 2004 - 2008</p>

 * <pre>
 * history
 *        21-Jun-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class PolishPreprocessor extends CustomPreprocessor {
	
	protected static final String SET_TICKER_STR = "([\\.|\\s]|^)setTicker\\s*\\(.+\\)";
	protected static final String GET_TICKER_STR = "([\\.|\\s]|^)getTicker\\s*\\(\\s*\\)";
	protected static final Pattern SET_TICKER_PATTERN = Pattern.compile( SET_TICKER_STR );
	protected static final Pattern GET_TICKER_PATTERN = Pattern.compile( GET_TICKER_STR );
	
	protected static final String GET_GRAPHICS_STR = "([\\.|\\s]|^)getGraphics\\s*\\(\\s*\\)";
	protected static final Pattern GET_GRAPHICS_PATTERN = Pattern.compile( GET_GRAPHICS_STR );
	
	//protected static final String SET_CURRENT_ITEM_STR = "[\\w|\\.]+\\s*\\.\\s*setCurrentItem\\s*\\([\\w|\\.]+\\s*\\)";
	protected static final String JAVA_VAR_STR = "[\\w|\\.|_]+";
	protected static final String GET_DISPLAY_METHOD_STR = "Display\\s*\\.\\s*getDisplay\\s*\\(\\s*" + JAVA_VAR_STR + "\\s*\\)\\s*";
	protected static final String SET_CURRENT_ITEM_STR = "(" + JAVA_VAR_STR + "|" + GET_DISPLAY_METHOD_STR + ")\\s*\\.\\s*setCurrentItem\\s*\\(.+\\)";
	protected static final Pattern SET_CURRENT_ITEM_PATTERN = Pattern.compile( SET_CURRENT_ITEM_STR );
	
	protected static final String ALERT_CONSTRUCTOR = "new\\s+Alert\\s*\\(\\s*.+\\)" ;
	protected static final String SET_CURRENT_ALERT_DISPLAYABLE_STR = "[\\w|\\.]+\\s*\\.\\s*setCurrent\\s*\\(\\s*[\\w*|\\.|\\_|\\(|\\)]+\\s*,\\s*[\\w*|\\.|\\_|\\(|\\)]+\\s*\\)";
	protected static final Pattern SET_CURRENT_ALERT_DISPLAYABLE_PATTERN = Pattern.compile( SET_CURRENT_ALERT_DISPLAYABLE_STR );
	
	private static final Map PRIMITIVES_BY_NAME = new HashMap();
	static {
		PRIMITIVES_BY_NAME.put( "byte", "Byte" );
		PRIMITIVES_BY_NAME.put( "short", "Short" );
		PRIMITIVES_BY_NAME.put( "int", "Integer" );
		PRIMITIVES_BY_NAME.put( "long", "Long" );
		PRIMITIVES_BY_NAME.put( "float", "Float" );
		PRIMITIVES_BY_NAME.put( "double", "Double" );
		PRIMITIVES_BY_NAME.put( "char", "Character" );
		PRIMITIVES_BY_NAME.put( "boolean", "Boolean" );
	}

	private boolean isTickerUsed;
	private File stylePropertyIdsFile;
	private File tickerFile;
	private IntegerIdGenerator idGenerator;
	private boolean isPopupUsed;
	private File popupFile;

	private CssAttributesManager cssAttributesManager;
	private boolean usesBlackBerry;
	private boolean usesDoJa;
	private boolean usesDefaultPackage;
	private boolean isLibraryBuild;

	/**
	 * Creates a new uninitialised PolishPreprocessor 
	 */
	public PolishPreprocessor() {
		super();
	}
	
	

	public void init(Preprocessor processor, PreprocessorSetting setting ) {
		super.init(processor, setting );
		this.idGenerator = new IntegerIdGenerator();
		this.cssAttributesManager = processor.getCssAttributesManager();
		//boolean allowAllCssAttributes = "true".equals( processor.getVariable("xxx.allowAllAttributes") );
		//System.out.println("allowing all CSS attributes: " + allowAllCssAttributes  + " ---> " + processor.getVariable("xxx.allowAllAttributes"));
		if (this.cssAttributesManager != null) {
			CssAttribute[] attributes = this.cssAttributesManager.getAttributes();
			for (int i = 0; i < attributes.length; i++) {
				CssAttribute attribute = attributes[i];
				int id = attribute.getId();
				String name = attribute.getName();
				if (id != -1) {
					this.idGenerator.addId( name, id );
					//System.out.println("Using ID [" + id + "] for CSS-attribute [" + attribute.getName() + "]");
				}
				/*
				if (allowAllCssAttributes) {
					processor.addSymbol( "polish.css" + name );
				}
				*/
			}
			// now register all attributes that have no ID assigned:
			for (int i = 0; i < attributes.length; i++) {
				CssAttribute attribute = attributes[i];
				int id = attribute.getId();
				String name = attribute.getName();
				if (id == -1) {
					this.idGenerator.getId( name, true );
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.LineProcessor#notifyDevice(de.enough.polish.Device, boolean)
	 */
	public void notifyDevice(Device device, boolean usesPolishGui) {
		super.notifyDevice(device, usesPolishGui);
		Environment env = device.getEnvironment();
		this.isLibraryBuild = env.hasSymbol("polish.LibraryBuild");
		this.usesBlackBerry = env.hasSymbol("polish.blackberry") && usesPolishGui;
		this.usesDoJa = env.hasSymbol("polish.doja") && usesPolishGui;
		this.usesDefaultPackage = env.hasSymbol("polish.useDefaultPackage");

		if (usesPolishGui) {
			// init ticker setting:
			this.tickerFile = new File( device.getBaseDir() + File.separatorChar 
					+ "TickerIndicator" );
			this.isTickerUsed = false;
			
			// init popup setting:
			this.popupFile = new File( device.getBaseDir() + File.separatorChar 
					+ "PopupIndicator" );
			this.isPopupUsed = false;
			
			// init abbreviations of style-properties:
			this.stylePropertyIdsFile = new File( device.getBaseDir() + File.separatorChar 
					+ "abbreviations.txt" );
			if (this.stylePropertyIdsFile.exists()) {
				//System.out.println("reading css attributes from " + this.stylePropertyIdsFile.getAbsolutePath() );
				try {
					HashMap idsByAttribute = FileUtil.readPropertiesFile( this.stylePropertyIdsFile );
					this.idGenerator.setIdsMap(idsByAttribute);
				} catch (IOException e) {
					e.printStackTrace();
					throw new BuildException("Unable to load abbreviations of style-attributes: " + e.toString() + ". Please try a clean rebuild.", e );
				}
			}
			//System.out.println( "PolishPreprocessor: preprocessor == null: " + (this.preprocessor == null) );
			//System.out.println("styleSheet == null: "  + (this.preprocessor.getStyleSheet() == null) );
			//System.out.println("idGenerator == null: "  + (this.idGenerator == null ) );
			this.preprocessor.getStyleSheet().setAttributesIds( this.idGenerator.getIdsMap() );
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.CustomPreprocessor#notifyDeviceEnd(de.enough.polish.Device, boolean)
	 */
	public void notifyDeviceEnd(Device device, boolean usesPolishGui) 
	{
		super.notifyDeviceEnd(device, usesPolishGui);
		if (!this.isUsingPolishGui) {
			return;
		}
		
		// write found abbreviations:
		try {
			FileUtil.writePropertiesFile( this.stylePropertyIdsFile, this.idGenerator.getIdsMap() );
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException("Unable to write IDs of style-properties to [" + this.stylePropertyIdsFile.getAbsolutePath() + "]: " + e.toString() + ". Please try a clean rebuild.", e );
		}
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.LineProcessor#notifyPolishPackageStart()
	 */
	public void notifyPolishPackageStart() {
		super.notifyPolishPackageStart();
		if (!this.isUsingPolishGui) {
			return;
		}
		
		// set settings for usage of Ticker:
		if (this.isTickerUsed || this.tickerFile.exists()) {
			this.environment.removeSymbol("polish.skipTicker");
			if (!this.tickerFile.exists()) {
				this.preprocessor.addToPreprocessQueue("de/enough/polish/ui/Screen.java");
				try {
					this.tickerFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					throw new BuildException("Unable to create Ticker indicator file [" + this.tickerFile.getAbsolutePath() + "]: " + e.toString() + ". Please try a clean rebuild.", e );
				}
			}
		} else {
			this.preprocessor.removeFromPreprocessQueue("de/enough/polish/ui/Screen.java");
			this.environment.addSymbol("polish.skipTicker");
		}
		
		// indicate the usage of a POPUP item:
		if (this.isPopupUsed || this.popupFile.exists()) {
			this.environment.addSymbol( "polish.usePopupItem" );
			if (!this.popupFile.exists()) {
				this.preprocessor.addToPreprocessQueue("de/enough/polish/ui/ChoiceGroup.java");
				try {
					this.popupFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					throw new BuildException("Unable to create POPUP indicator file [" + this.popupFile.getAbsolutePath() + "]: " + e.toString() + ". Please try a clean rebuild.", e );
				}
			}			
		} else {
			this.environment.removeSymbol( "polish.usePopupItem" );
			this.preprocessor.removeFromPreprocessQueue("de/enough/polish/ui/ChoiceGroup.java");
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.LineProcessor#processClass(de.enough.polish.util.StringList, java.lang.String)
	 */
	public void processClass(StringList lines, String className) {
		if (this.isLibraryBuild) {
			return;
		}
		boolean isIllegalStateExceptionClass = false;
		if (this.usesDoJa && !this.usesDefaultPackage) {
			isIllegalStateExceptionClass = className.endsWith("IllegalStateException");
			//System.out.println( "class=" + className + ", isIllegalStateExceptionClass=" + isIllegalStateExceptionClass );
		}
		//System.out.println("PolishPreprocessor: processing class " + className );
		while (lines.next() ) {
			// check for ticker:
			String line = lines.getCurrent();

			// check for comments:
			String trimmedLine = line.trim();
			if (trimmedLine.startsWith("//") && (! (trimmedLine.length() > 3 && trimmedLine.charAt(2)=='#')) ) {
				//System.out.println(className + ": ignoring " + line);
				continue;
			}
			if (trimmedLine.startsWith("/*")) {
				int stopIndex = 0;
				while ( (stopIndex = line.indexOf("*/")) == -1 && lines.next()) {
					//System.out.println(className + ": ignoring " + line);
					line = lines.getCurrent();
				}
				line = line.substring( stopIndex );
			}
			// check for style-property-usage:
			int startPos = -1;
			
			// check for RemoteClient.open("de.enough.polish.sample.rmi.GameServer", "http://localhost:8080/gameserver/myservice") etc;
			startPos = line.indexOf("RemoteClient.open");
			if (startPos != -1) {
				int parenthesesStart = line.indexOf('(', startPos );
				int parenthesesEnd = line.indexOf(')', parenthesesStart);
				if (parenthesesStart == -1 || parenthesesEnd == -1) {
					throw new BuildException (getErrorStart(className, lines) + ": Invalid RemoteClient.open() usage - please put the complete call on a single line. This line is not valid: " + line );
				}
				int commaPos = line.indexOf(',', parenthesesStart);
				String name = line.substring( parenthesesStart + 1, commaPos ).trim();
				if (name.length() < 3 || name.charAt(0) != '"' || name.charAt( name.length() -1) != '"') {
					throw new BuildException (getErrorStart(className, lines) + ": Invalid RemoteClient.open() usage - please specify the name of the interface directly with quotes, e.g. \"RemoteClient.open(\"GameServer\", \"http://localhost/gameserver/myservice\")\" . This line is not valid: " + line );					
				}
				name = name.substring( 1, name.length() - 1);
				String mockup = this.environment.getVariable("polish.rmi.mockup." + name );
				if (mockup != null) {
					line = line.substring(0, startPos) + mockup + "; //" + line.substring(startPos);
					lines.setCurrent(line);
				} else {
					String url = line.substring( commaPos + 1, parenthesesEnd ).trim();
					line = line.substring(0, startPos)
						+ "new " + name + "RemoteClient(" + url + "); //" + line.substring(startPos);
					lines.setCurrent(line);
				}
				continue;
			}
			
			// check for interfaces extending de.enough.polish.rmi.Remote:
			startPos = line.indexOf("extends Remote");
			if (startPos != -1 && (
					(line.length() == startPos + "extends Remote".length())
					|| ( !Character.isLetterOrDigit( line.charAt(startPos + "extends Remote".length())) )
					) )
			{
				// PROBLEM: this is only called in clean builds and after the interface has changed...
				//System.out.println("extends Remote: " + className );
				List rmiClassFiles = (List) this.environment.get( "rmi-classes" );
				if (rmiClassFiles == null ) {
					rmiClassFiles = new ArrayList();
					this.environment.set("rmi-classes", rmiClassFiles);
			    	this.environment.addSymbol("polish.build.hasRemoteClasses");
				}
				try {
					boolean extendsRemote = createRemoteImplementation( className, lines );
					if (extendsRemote) {
						File classFile;
						// get name of the classes directory - this is changed by the java5 postcompiler, for example
						// this is needed when the java5 postcompiler runs before the serializaton compiler, but not
						// when the serialization postcompiler runs first...
//						String classesDirName = this.environment.getVariable("polish.classes.dirname");
//						if (classesDirName == null) {
//							classesDirName = "classes";
//						}
						String classesDirName = "classes";
						if (this.environment.hasSymbol("polish.useDefaultPackage")) {
							int lastDotPost = className.lastIndexOf('.');
							if (lastDotPost != -1) {
								className = className.substring( lastDotPost + 1 );
							}
							classFile = new File( this.environment.getDevice().getBaseDir(), classesDirName + File.separatorChar +  className + ".class" );
						} else {
							classFile = new File( this.environment.getDevice().getBaseDir(), classesDirName + File.separatorChar +  className.replace('.', File.separatorChar ) + ".class" );
						}
						System.out.println("found remote class: " + classFile.getAbsolutePath());
						rmiClassFiles.add( classFile );						
					}
				} catch (IOException e) {
					e.printStackTrace();
					BuildException be = new BuildException( getErrorStart(className, lines) + "Unable to read or save interface/class file.");
					be.initCause(e);
					throw be;
				}
				continue;
			}	
		
			
			//////////////////////////////////////////////////////////////////////////
			/// The rest applies only when the J2ME Polish UI is being used      /////
			//////////////////////////////////////////////////////////////////////////
			if (this.isUsingPolishGui) {
				String methodName = "tyle.getProperty(";
				startPos = line.indexOf(methodName);
				if (startPos == -1) {
					methodName = "tyle.getIntProperty(";
					startPos = line.indexOf(methodName);
					if (startPos == -1) {
						methodName = "tyle.getBooleanProperty(";
						startPos = line.indexOf(methodName);
					}
					if (startPos == -1) {
						methodName = "tyle.getObjectProperty(";
						startPos = line.indexOf(methodName);
					}
					if (startPos == -1) {
						methodName = "tyle.getColorProperty(";
						startPos = line.indexOf(methodName);
					}
					if (startPos == -1) {
						methodName = "getStyle().getProperty(";
						startPos = line.indexOf(methodName);
						if (startPos == -1) {
							methodName = "getStyle().getIntProperty(";
							startPos = line.indexOf(methodName);
						}
						if (startPos == -1) {
							methodName = "getStyle().getBooleanProperty(";
							startPos = line.indexOf(methodName);
						}
						if (startPos == -1) {
							methodName = "getStyle().getObjectProperty(";
							startPos = line.indexOf(methodName);
						}
						if (startPos == -1) {
							methodName = "getStyle().getColorProperty(";
							startPos = line.indexOf(methodName);
						}
					}
				}
				if (startPos != -1) {
					int endPos = line.indexOf( ')', startPos + methodName.length() );
					if (endPos == -1) {
						if (!this.isInJ2MEPolishPackage) {
							System.out.println( getErrorStart(className, lines) + "Unsupported style-usage: "
									+ "style.getProperty( \"name\" ); always needs to be on a single line. "
									+ " This line might be invalid: " + line );
							System.out.println("Assuming accessing attributes are accessed using style.getXXX(int).");
						}
						continue;
					}
					
					String property = line.substring( startPos + methodName.length(),
							endPos ).trim();
					//System.out.println("last line: " + lines.getPrevious() + "\ncurrent=" + lines.getCurrent() + "\nnext = " + lines.getNext() );
					if (property.charAt(0) != '"' || property.charAt( property.length() - 1) != '"') {
						if (!this.isInJ2MEPolishPackage) {
							System.out.println(getErrorStart(className, lines) + "Direct style-usage: "
									+ "style.getProperty( \"name\" ); always needs to use the property-name directly (not a variable). "
									+ " This line might be invalid: " + line );
							System.out.println("Assuming accessing attributes are directly accessed using style.getXXX(int).");
						}
						continue;
					}
					String key = property.substring( 1, property.length() - 1);
					int id = this.idGenerator.getId(
							key, this.environment.hasSymbol("polish.css." + key) );
					// check if this property is used at all:
					//if ( id == -1 ) {
						//System.out.println("skipping attribute [" + key + "]");
						//TODO Problem: when a user does not check for the availalability of the CSS symbol
						// via if polish.css.name, then this will end with an invalid key!
						// So we add for now a -1 call, which shouldn't result in any problems
						// since the css attribute is not used anyhow
						// continue;
					//}
					//System.out.println("got id " + id + " for key " + key);
					line = StringUtil.replace( line, property, "" + id );
					//System.out.println("style: setting line[" + lines.getCurrentIndex() + " to = [" + line + "]");
					lines.setCurrent( line );
					continue;
				}
				
				//if (!this.isInJ2MEPolishPackage) {
					methodName = "tyle.addAttribute(";
					startPos = line.indexOf(methodName);
					if (startPos == -1) {
						methodName = "getStyle().addAttribute(";
						startPos = line.indexOf(methodName);
					}
					if (startPos != -1) {
						int endPos = line.indexOf( ')', startPos + methodName.length() );
						if (endPos == -1) {
								System.out.println( getErrorStart(className, lines) + "Unsupported style-usage: "
										+ "style.addAttribute( \"name\", Object ); always needs to be on a single line. "
										+ " This line might be invalid: " + line );
							continue;
						}
						
						String propertyNameAndValue = line.substring( startPos + methodName.length(),
								endPos ).trim();
						int commaIndex = propertyNameAndValue.indexOf(',');
						if (commaIndex == -1) {
							System.out.println(getErrorStart(className, lines) + "Unsupported style-usage: "
									+ "style.addAttribute(String name, Object value) require two arguments." );
							continue;
						}
						if (propertyNameAndValue.charAt(0) != '"' ) {
							// the user has used addAttribute(int, Object), so no need
							// to adjust this line:
							continue;
						}
						int quoteIndex = propertyNameAndValue.indexOf('"', 1);
						if (quoteIndex > commaIndex) {
							throw new BuildException( getErrorStart(className, lines) + ": style.addProperty(String,Object) method found with illegal comma in property name: " + line);
						} else if (quoteIndex == -1) {
							throw new BuildException( getErrorStart(className, lines) + ": style.addProperty(String,Object) method found without closing quotation marks for the property name: " + line);
						}
						String key = propertyNameAndValue.substring( 1, quoteIndex);
						int id = this.idGenerator.getId(
								key, this.environment.hasSymbol("polish.css." + key) );
						line = StringUtil.replace( line, '"' + key + '"', "" + id );
						//System.out.println("style: setting line[" + lines.getCurrentIndex() + " to = [" + line + "]");
						lines.setCurrent( line );
						continue;
					}
					
					methodName = "tyle.removeAttribute(";
					startPos = line.indexOf(methodName);
					if (startPos == -1) {
						methodName = "getStyle().removeAttribute(";
						startPos = line.indexOf(methodName);
					}
					if (startPos != -1) {
						int endPos = line.indexOf( ')', startPos + methodName.length() );
						if (endPos == -1) {
								System.out.println( getErrorStart(className, lines) + "Unsupported style-usage: "
										+ "style.removeAttribute( \"name\" ); always needs to be on a single line. "
										+ " This line might be invalid: " + line );
							continue;
						}
						
						String propertyNameAndValue = line.substring( startPos + methodName.length(),
								endPos ).trim();
						
						if (propertyNameAndValue.charAt(0) != '"' ) {
							// the user has used addAttribute(int, Object), so no need
							// to adjust this line:
							continue;
						}
						int quoteIndex = propertyNameAndValue.indexOf('"', 1);
						if (quoteIndex == -1) {
							throw new BuildException( getErrorStart(className, lines) + ": style.removeProperty(String,Object) method found without closing quotation marks for the property name: " + line);
						}
						String key = propertyNameAndValue.substring( 1, quoteIndex);
						int id = this.idGenerator.getId(
								key, this.environment.hasSymbol("polish.css." + key) );
						line = StringUtil.replace( line, '"' + key + '"', "" + id );
						//System.out.println("style: setting line[" + lines.getCurrentIndex() + " to = [" + line + "]");
						lines.setCurrent( line );
						continue;
					}
				//}
				
				// check for usage of java.lang.IllegalStateException:
				if (!this.usesDefaultPackage && this.usesDoJa && !isIllegalStateExceptionClass) {
					startPos = line.indexOf("IllegalStateException");
					if (startPos != -1) {
						line = line.substring( 0, startPos ) + "de.enough.polish.doja.lang." + line.substring( startPos );
						lines.setCurrent( line );
					}
				}
				
				if (this.isInJ2MEPolishPackage) {
					// skip the next checks, when the J2ME Polish package is preprocessed:
					continue;
				}
				
				startPos = line.indexOf("getTicker"); 
				if ( startPos != -1) {
					int commentPos = line.indexOf("//");
					if (commentPos != -1 && commentPos < startPos) {
						continue;
					}
					Matcher matcher = GET_TICKER_PATTERN.matcher( line );
					boolean matchFound = false;
					while (matcher.find()) {
						matchFound = true;
						String group = matcher.group();
						String replacement = StringUtil.replace( group, "getTicker", "getPolishTicker");
						line = StringUtil.replace( line, group, replacement );
					}
					if (matchFound) {
						this.isTickerUsed = true;
						lines.setCurrent( line );					
					}
					continue;
				}
				startPos = line.indexOf("setTicker");
				if ( startPos != -1) {
					//System.out.println("setTicker found in line " + line );
					int commentPos = line.indexOf("//");
					if (commentPos != -1 && commentPos < startPos) {
						continue;
					}
					Matcher matcher = SET_TICKER_PATTERN.matcher( line );
					boolean matchFound = false;
					while (matcher.find()) {
						matchFound = true;
						String group = matcher.group();
						String replacement = StringUtil.replace( group, "setTicker", "setPolishTicker");
						line = StringUtil.replace( line, group, replacement );
					}
					if (matchFound) {
						this.isTickerUsed = true;
						lines.setCurrent( line );
						//System.out.println( "line is now " + line );
					}
					continue;
				}
				
				// check for display.setCurrentItem:
				startPos = line.indexOf("setCurrentItem");
				if ( startPos != -1) {
					//System.out.println("setCurrentItem found in line " + line );
					int commentPos = line.indexOf("//");
					if (commentPos != -1 && commentPos < startPos) {
						continue;
					}
					Matcher matcher = SET_CURRENT_ITEM_PATTERN.matcher( line );
					if (matcher.find()) {
						String group = matcher.group();
						//System.out.println("group = [" + group + "]");
						int parenthesisPos = group.indexOf('(', startPos );
						if (parenthesisPos == -1) {
							throw new BuildException( getErrorStart(className, lines) + ": setCurrentItem() method found without opening parentheses: " + line);
						}
						String displayVar = group.substring(0, parenthesisPos);
						int dotPos = displayVar.lastIndexOf('.');
						displayVar = displayVar.substring( 0, dotPos ).trim();
						String itemVar = group.substring( parenthesisPos + 1, group.length() -1 ).trim();
						String replacement = itemVar + ".show( " + displayVar + " )"; 
						//System.out.println("replacement = [" + replacement + "].");
						line = StringUtil.replace( line, group, replacement );
						//System.out.println("line = [" + line + "]");
						lines.setCurrent( line );
					}
					continue;
				}
				
				// check for display.setCurrent( Alert, Displayable ):
				startPos = line.indexOf("setCurrent");
				if ( startPos != -1) {
					//System.out.println("setCurrent found in line " + line );
					int commentPos = line.indexOf("//");
					if (commentPos != -1 && commentPos < startPos) {
						continue;
					}
					Matcher matcher = SET_CURRENT_ALERT_DISPLAYABLE_PATTERN.matcher( line );
					if (matcher.find()) {
						String group = matcher.group();
						//System.out.println("group = [" + group + "]");
						int parenthesisPos = group.indexOf('(');
						String displayVar = group.substring(0, parenthesisPos);
						int dotPos = displayVar.lastIndexOf('.');
						displayVar = displayVar.substring( 0, dotPos ).trim();
						String alertDisplayableVars = group.substring( parenthesisPos + 1, group.length() -1 ).trim();
						//int commaPos = alertDisplayableVars.indexOf('.');
						String replacement = "Alert.setCurrent( " + displayVar + ", " + alertDisplayableVars  + " )"; 
						//System.out.println("replacement = [" + replacement + "].");
						line = StringUtil.replace( line, group, replacement );
						//System.out.println("line = [" + line + "]");
						lines.setCurrent( line );
					}
					continue;
				}			
	
				
				// check for Choice.POPUP:
				startPos = line.indexOf(".POPUP");
				if (startPos != -1) {
					this.isPopupUsed = true;
					continue;
				}
				
	
				// check for GameCanvase.getGraphics() on BlackBerry phones:
				if (this.usesBlackBerry) {
					startPos = line.indexOf("getGraphics"); 
					if ( startPos != -1) {
						int commentPos = line.indexOf("//");
						if (commentPos != -1 && commentPos < startPos) {
							continue;
						}
						Matcher matcher = GET_GRAPHICS_PATTERN.matcher( line );
						boolean matchFound = false;
						while (matcher.find()) {
							matchFound = true;
							String group = matcher.group();
							String replacement = StringUtil.replace( group, "getGraphics", "getPolishGraphics");
							line = StringUtil.replace( line, group, replacement );
						}
						if (matchFound) {
							this.isTickerUsed = true;
							lines.setCurrent( line );					
						}
						continue;
					}
				}
			}

		}
	}


	/**
	 * Parses the source code of the given interface that extends Remote and generates the client stub implementation.
	 * 
	 * @param className the name of the remote interface
	 * @param lines the source code of the interface
	 * @throws IOException when the source code could not be written
	 * @return true when the given source code really extends de.enough.polish.rmi.Remote
	 */
	protected boolean createRemoteImplementation(String className, StringList lines) throws IOException {
		JavaSourceClass sourceClass = new JavaSourceClass( lines.getArray() );
		// check if this class really extends Remote:
		String extendsStatement = sourceClass.getExtendsStatement();
		if (extendsStatement == null || !("Remote".equals(extendsStatement) || "de.enough.polish.rmi.Remote".equals(extendsStatement)) ) {
			// this class does not extend Remote:
			return false;
		}
		//boolean callSynchronly = "true".equals( this.environment.getVariable("polish.rmi.synchrone") );
		String newImplements = sourceClass.getClassName();
		sourceClass.setClassName(newImplements + "RemoteClient");
		sourceClass.setImplementedInterfaces( new String[]{ newImplements } );
		sourceClass.addImport("de.enough.polish.io.Externalizable");
		String extendsClassName = getRemoteBaseClass( this.environment );
		int lastDotIndex = extendsClassName.lastIndexOf('.');
		if (lastDotIndex != -1) {
			String name = extendsClassName.substring(lastDotIndex+1);
			sourceClass.setExtendsStatement(name);
			sourceClass.addImport( extendsClassName );
		} else {
			sourceClass.setExtendsStatement(extendsClassName);
		}
		sourceClass.setClass( true );
		
		
		 
		JavaSourceMethod[] methods = sourceClass.getMethods();
		for (int i = 0; i < methods.length; i++) {
			JavaSourceMethod method = methods[i];
			createRemoteMethodImplementation( method );
		}

		// add URL constructor:
		JavaSourceMethod constructor = new JavaSourceMethod( sourceClass, "public", "", sourceClass.getClassName(), new String[]{ "String" }, new String[]{ "url" }, null );
		constructor.setMethodCode( new String[]{
				"super( url );"
		} );
		sourceClass.addMethod( constructor );
		File targetDir;
		if (this.environment.hasSymbol("polish.useDefaultPackage")) {
			// not supported!
			throw new BuildException("When using the RMI framework, you must deactivate the \"useDefaultPackage\" option in the obfuscator. "
					+ "\nPlease change the <obfuscator> tag in your build.xml script and define useDefaultPackage=\"false\"."
					);
//			targetDir = new File( this.currentDevice.getSourceDir() );
//			sourceClass.setPackageName(null);
		} else {
			targetDir = new File( this.currentDevice.getSourceDir() + File.separatorChar + sourceClass.getPackageName().replace('.', File.separatorChar) );
		}
		File targetFile = new File( targetDir, sourceClass.getClassName() + ".java");
		String[] sourceCode;
		// let the preprocessor handle the source code again when the default package should be used:
		if (this.environment.hasSymbol("polish.useDefaultPackage")) {
			StringList sourceCodeList = new StringList(  sourceClass.renderCode() );
			this.preprocessor.preprocess( sourceClass.getClassName(), sourceCodeList  );
			sourceCode = sourceCodeList.getArray();
			
		} else {
			sourceCode = sourceClass.renderCode();
		}
		FileUtil.writeTextFile(targetFile, sourceCode );
		return true;
	}



	/**
	 * @param environment
	 * @return
	 */
	private String getRemoteBaseClass(Environment env)
	{
		if ("true".equals( env.getVariable("polish.rmi.xmlrpc"))) {
			return "de.enough.polish.rmi.xmlrpc.XmlRpcRemoteClient";
		} else if ("true".equals( env.getVariable("polish.rmi.l2cap"))) {
			return "de.enough.polish.rmi.bluetooth.L2CapRemoteClient";						
		} else if ("true".equals( env.getVariable("polish.rmi.spp")) || "true".equals( env.getVariable("polish.rmi.rfcomm")) ) {
			return "de.enough.polish.rmi.bluetooth.SppRemoteClient";						
		} else if ("true".equals( env.getVariable("polish.rmi.obex")) ) {
			return "de.enough.polish.rmi.bluetooth.ObexRemoteClient";						
		} else {
			return "de.enough.polish.rmi.RemoteClient";			
		}
	}



	private void createRemoteMethodImplementation(JavaSourceMethod method ) {
		if (!(method.throwsException("RemoteException") || method.throwsException("de.enough.polish.rmi.RemoteException")) ) {
			throw new BuildException("RMI method " + method.getName() + " does not throw RemoteException. Please correct this in your class " + method.getSourceClass().getClassName() );
		}
		String methodCall = "callMethod";
		ArrayList methodCode = new ArrayList();
		methodCode.add("String _methodName= \"" + method.getName() + "\";" );
		if (method.getParameterNames() == null) {
			methodCall += "( _methodName, 0, null );";
		} else {
			StringBuffer primitivesFlagBuffer = new StringBuffer();
			StringBuffer buffer = new StringBuffer();
			buffer.append("Object[] _params = new Object[] { ");
			String[] paramNames = method.getParameterNames();
			String[] paramTypes = method.getParameterTypes();
			for (int i = 0; i < paramNames.length; i++) {
				String paramName = paramNames[i];
				String paramType = paramTypes[i];
				if (isPrimitive( paramType )) {
					appendPrimitiveWrapper( paramType, paramName, buffer );
					primitivesFlagBuffer.append('1'); // 1 = this is a primitive
				} else {
					// this is a normal object:
					primitivesFlagBuffer.append('0'); // 0 = this is not a primitive
					buffer.append( paramName );					
				}
				if (i != paramNames.length - 1) {
					buffer.append(", ");
				}
			}
			buffer.append( " };");
			methodCode.add( buffer.toString() );
			// add primitive flags:
			if (paramNames.length == 0) {
				primitivesFlagBuffer.append('0');
			}
			String reversedFlags = primitivesFlagBuffer.reverse().toString();
			int primitiveFlags = Integer.parseInt( reversedFlags, 2 );
			methodCode.add( "long _primitiveFlags = " + primitiveFlags + "; // decimal of binary " + reversedFlags );
			methodCall += "( _methodName, _primitiveFlags, _params );";
		}
		String[] thrownExceptions = method.getThrownExceptions();
		boolean hasDeclaredExceptions = thrownExceptions.length > 1;
		if (hasDeclaredExceptions) {
			methodCode.add("try {");
		}
		String returnType = method.getReturnType();
		if ( "void".equals( returnType ) ) {
			methodCode.add( methodCall );
		} else if ( isPrimitive( returnType )) {
			addPrimitiveReturnCast( returnType, methodCall, methodCode );
		} else {
			if (isPrimitiveArray(returnType)) {
				// the return value is a normal object:
				methodCode.add( "return (" + returnType + ") " + methodCall );				
			} else if (isArray(returnType)) {
				methodCode.add("Externalizable[] _returnValues = (Externalizable[])" + methodCall);
				String plainReturnType = returnType.substring( 0, returnType.indexOf('[')).trim();
				methodCode.add(returnType + " _castedReturnValues = new " + plainReturnType + "[ _returnValues.length ];");
				methodCode.add("System.arraycopy( _returnValues, 0, _castedReturnValues, 0, _returnValues.length );");
				methodCode.add("return _castedReturnValues;");
			} else {
				// the return value is a normal object:
				methodCode.add( "return (" + returnType + ") " + methodCall );
			}
		}
		if (hasDeclaredExceptions) {
			methodCode.add("} catch (RemoteException _e) {");
			methodCode.add("Throwable _cause = _e.getCause();");
			for (int i = 0; i < thrownExceptions.length; i++) {
				String exceptionName = thrownExceptions[i];
				if (!(exceptionName.equals("RemoteException") || exceptionName.equals("de.enough.polish.rmi.RemoteException"))) {
					methodCode.add("if (_cause instanceof " + exceptionName + ") {");
					methodCode.add( "throw (" + exceptionName + ") _cause;");
					methodCode.add("}");
				}
			}
			methodCode.add("throw _e;");
			methodCode.add("}");
		}
		method.setMethodCode( (String[]) methodCode.toArray( new String[methodCode.size()]));
	}

	/**
	 * Checks if the given type is a primitive array type like long[] or boolean[]
	 * @param type the type
	 * @return true when this is a primitive array
	 */
	protected static boolean isPrimitiveArray(String type)
	{
		int arrayIndex = type.indexOf('[');
		int stopIndex = type.indexOf(']');
		if (arrayIndex != -1 && stopIndex != -1) {
			String primitiveType = type.substring(0, arrayIndex).trim();
			return isPrimitive(primitiveType);
		}
		return false;
	}



	/**
	 * Checks if the specified type constitutes an array
	 * @param type the type, e.g. "String[]"
	 * @return true when this is an array
	 */
	protected static boolean isArray(String type)
	{
		return type.indexOf('[') != -1 && type.indexOf(']') != -1;
	}



	/**
	 * Adds a cast for primitive return types.
	 * 
	 * @param returnType the primitive return type like int, boolean, etc
	 * @param methodCall the code for calling the server
	 * @param methodCode the code to which the primitive cast is added
	 */
	protected static void addPrimitiveReturnCast(String returnType, String methodCall, ArrayList methodCode) {
		// methodCode.add( "return (" + returnType + ") " + methodCall ) is not working for primitive returns,
		// required is for example following code for int return types:
		// methodCode.add( "Object _returnObject = " + methodCall );  
		// methodCode.add( "return ((" + "Integer" + ") " + _returnObject ).intValue();" );

		methodCode.add( "Object _returnObject = " + methodCall );
		if ("byte".equals(returnType)) {
			methodCode.add("return ((Byte) _returnObject ).byteValue();" );
		} else if ("short".equals(returnType)) {
			methodCode.add("return ((Short) _returnObject ).shortValue();" );
		} else if ("int".equals(returnType)) {
			methodCode.add("return ((Integer) _returnObject ).intValue();" );
		} else if ("long".equals(returnType)) {
			methodCode.add("return ((Long) _returnObject ).longValue();" );
		} else if ("float".equals(returnType)) {
			methodCode.add("return ((Float) _returnObject ).floatValue();" );
		} else if ("double".equals(returnType)) {
			methodCode.add("return ((Double) _returnObject ).doubleValue();" );
		} else if ("char".equals(returnType)) {
			methodCode.add("return ((Character) _returnObject ).charValue();" );
		} else if ("boolean".equals(returnType)) {
			methodCode.add("return ((Boolean) _returnObject ).booleanValue();" );
		} else {
			throw new IllegalArgumentException("return type [" + returnType + "] is not primitive.");
		}
	}



	/**
	 * Determines whether the given type is a primitive one like byte, int, float etc.
	 * 
	 * @param paramType the type, for example "int", "String" or similar
	 * @return true when the given type is primitive
	 */
	protected static boolean isPrimitive(String paramType) {
		return PRIMITIVES_BY_NAME.get(paramType) != null;
	}


	/**
	 * Adds the appropriate wrapper object to the given stringbuffer, for example int x becomes new Integer( x )
	 * @param paramType the primitive type like "int", "float" etc
	 * @param paramName the parameter name
	 * @param buffer the StringBuffer to which the code is added
	 */
	protected static void appendPrimitiveWrapper(String paramType, String paramName, StringBuffer buffer) {
		String wrapperClassName = (String) PRIMITIVES_BY_NAME.get(paramType);
		buffer.append("new ").append( wrapperClassName ).append("( ").append( paramName ).append(" )");
	}
}
