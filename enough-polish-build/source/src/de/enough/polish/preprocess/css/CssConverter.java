/*
 * Created on 04-Jan-2004 at 23:04:12.
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
package de.enough.polish.preprocess.css;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.enough.polish.BuildException;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.preprocess.Preprocessor;
import de.enough.polish.preprocess.css.attributes.ParameterizedCssAttribute;
import de.enough.polish.preprocess.css.attributes.StyleCssAttribute;
import de.enough.polish.util.AbbreviationsGenerator;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.StringList;
import de.enough.polish.util.StringUtil;

/**
 * <p>Converts CSS files to Java-Code.</p>
 *
 * @author Robert Virkus, robert@enough.de
 * <pre>
 * history
 *        04-Jan-2004 - rob creation
 * </pre>
 */
public class CssConverter extends Converter {
	
	private static final int BLOCKSIZE = 100;
	private static final int MAX_BLOCKS_FOR_STYLESHEET_JAVA = 1; 
	private static final int MAX_BLOCKS_PER_FILE = 4;
	private static final String INCLUDE_MARK = "//$$IncludeStyleSheetDefinitionHere$$//";

	protected ArrayList referencedStyles;
	protected AbbreviationsGenerator abbreviationGenerator;
	protected CssAttributesManager attributesManager;
	private CssAttribute fontFaceAttribute;
	private CssAttribute fontStyleAttribute;
	private CssAttribute fontSizeAttribute;
	private CssAttribute layoutAttribute;
	private CssAttribute backgroundAttribute;
	private CssAttribute borderAttribute;
	private final ParameterizedAttributeConverter parameterizedAttributeConverter;

	private String[] backgroundAttributeNames;
	private String[] borderAttributeNames;
	
	/**
	 * Creates a new CSS converter
	 * 
	 */
	public CssConverter() {
		this.colorConverter = new ColorConverter();
		this.parameterizedAttributeConverter = new ParameterizedAttributeConverter();
	}
	
	/**
	 * Sets the manager of CSS attributes. 
	 * 
	 * @param attributesManager the manager for CSS attributes
	 */
	public void setAttributesManager( CssAttributesManager attributesManager ) {
		this.attributesManager = attributesManager;
		this.fontFaceAttribute = attributesManager.getAttribute("font-face");
		this.fontStyleAttribute = attributesManager.getAttribute("font-style");
		this.fontSizeAttribute = attributesManager.getAttribute("font-size");
		this.layoutAttribute = attributesManager.getAttribute("layout");
		this.backgroundAttribute = attributesManager.getAttribute("background");
		this.borderAttribute = attributesManager.getAttribute("border");
		// save all external background attributes:
		ArrayList bgAttributesList = new ArrayList();
		ArrayList brdAttributesList = new ArrayList();
		CssAttribute[] attributes = attributesManager.getAttributes();
		for (int i = 0; i < attributes.length; i++)
		{
			CssAttribute attribute = attributes[i];
			if ( !attribute.isImplicit()) {
				String name = attribute.getName();
				if (name.startsWith("background-")) {
					bgAttributesList.add( name.substring("background-".length()) );
				} else if (name.startsWith("border-")) {
					brdAttributesList.add( name.substring("border-".length()) );
				}
			}
		}
		this.backgroundAttributeNames = (String[])bgAttributesList.toArray( new String[ bgAttributesList.size() ] );
		this.borderAttributeNames = (String[])brdAttributesList.toArray( new String[ brdAttributesList.size() ] );
	}
	

	/**
	 * Converts a stylesheet to Java source code.
	 * @param sourceCode the source code to which the generated code is appended
	 * @param styleSheet the style sheet
	 * @param device the current device
	 * @param preprocessor a preprocessor
	 * @param env the environment
	 */
	public void convertStyleSheet( StringList sourceCode, 
								   StyleSheet styleSheet, 
								   Device device,
								   Preprocessor preprocessor,
								   Environment env ) 
	{
		env.set( ColorConverter.ENVIRONMENT_KEY,  this.colorConverter );
		this.abbreviationGenerator = null;
		// search for the position to include the style-sheet definitions:
		int index = -1;
		while (sourceCode.next()) {
			String line = sourceCode.getCurrent();
			if (INCLUDE_MARK.equals(line)) {
				index = sourceCode.getCurrentIndex() + 1;
				break;
			}
		}
		if (index == -1) {
			throw new BuildException("Unable to modify StyleSheet.java, include point [" + INCLUDE_MARK + "] not found.");
		}
		// okay start with the creation of source code:
		ArrayList codeList = new ArrayList();
		
		this.referencedStyles = new ArrayList();
		// initialise the syle sheet:
		styleSheet.inherit();
		// set the color-definitions:
		this.colorConverter.setTemporaryColors( styleSheet.getColors() );
		// add the font-definitions:
		boolean defaultFontDefined = false;
		HashMap fonts = styleSheet.getFonts();
		Set keys = fonts.keySet();
		for (Iterator iter = keys.iterator(); iter.hasNext();) {
			String groupName = (String) iter.next();
			//System.out.println("processing font " + fonts.get( groupName ) );
			if ("default".equals(groupName)) {
				defaultFontDefined = true;
				HashMap group = (HashMap) fonts.get( groupName );
				processFont(group, groupName, null, codeList, styleSheet, true, env );
			} else {
				HashMap group = (HashMap) fonts.get( groupName );
				processFont(group, groupName, null, codeList, styleSheet, true, env );
			}
		}
		
		// add the backgrounds-definition:
		boolean defaultBackgroundDefined = false;
		HashMap backgrounds = styleSheet.getBackgrounds();
		Object[] backgroundKeys = BackgroundComparator.sort( backgrounds, (ParameterizedCssAttribute) this.backgroundAttribute );
		
		//Arrays.sort( backgroundKeys, new BackgroundComparator( backgrounds, (ParameterizedCssAttribute) this.backgroundAttribute ));
		for (int i = 0; i < backgroundKeys.length; i++)
		{
			String groupName = (String) backgroundKeys[i];
			if ("default".equals(groupName)) {
				defaultBackgroundDefined = true;
			} 
			HashMap group = (HashMap) backgrounds.get( groupName );
			processBackground(groupName, group, null, codeList, false, styleSheet, true, env );
		}
		
		// add the borders-definition:
		boolean defaultBorderDefined = false;
		HashMap borders = styleSheet.getBorders();
		keys = borders.keySet();
		for (Iterator iter = keys.iterator(); iter.hasNext();) {
			String groupName = (String) iter.next();
			if ("default".equals(groupName)) {
				defaultBorderDefined = true;
			} 
			HashMap group = (HashMap) borders.get( groupName );
			processBorder(groupName, group, null, codeList, false, styleSheet, true, env );
		}
		ArrayList staticCodeList = new ArrayList();
		ArrayList styleCodeList = new ArrayList();
		
		// retrieve all used styles:
		ArrayList defaultStyleNames = new ArrayList();
		defaultStyleNames.add( "default");
		defaultStyleNames.add( "label");
		defaultStyleNames.add( "focused");
		// check if fullscreen mode is enabled with menu:
		if ((env.hasSymbol("polish.useMenuFullScreen") 
			&& ((device.getCapability("polish.classes.fullscreen") != null) 
					|| (device.isMidp2() && env.hasSymbol("polish.hasCommandKeyEvents") ) ))
			|| env.hasSymbol("polish.needsManualMenu") 
			) 
		{
			defaultStyleNames.add( "menu");
			defaultStyleNames.add( "menuitem");
		}
		
		
		String[] defaultNames = (String[]) defaultStyleNames.toArray( new String[ defaultStyleNames.size() ]);
		Style[] styles = styleSheet.getUsedAndReferencedStyles(defaultNames, this.attributesManager);
		
		int blocks = styles.length / BLOCKSIZE +  (styles.length % BLOCKSIZE == 0 ? 0 : 1);
		boolean defineStylesOutside = (blocks > MAX_BLOCKS_FOR_STYLESHEET_JAVA);

		// add the default style:
		processDefaultStyle( defaultFontDefined,  
				defaultBackgroundDefined, defaultBorderDefined,
				styleCodeList, codeList, defineStylesOutside, styleSheet, device, env  );
		
		
		// now add all other static and referenced styles:
		boolean isLabelStyleReferenced = false;
		codeList.add("\t//static and referenced styles:");
		for (int i = 0; i < styles.length; i++) {
			Style style = styles[i];
			if (!"default".equals(style.getSelector())) {
				if ("label".equals(style.getSelector())) {
					isLabelStyleReferenced = true;
				}
				processStyle( style, styleCodeList, codeList, defineStylesOutside, styleSheet, device, env );
			}
		}
		// register referenced and dynamic styles:
		codeList.add("\tprotected static final Hashtable stylesByName = new Hashtable(" + styles.length + ");");
		
		if (defineStylesOutside) {
			codeList.add("static { // init styles:" );
			int fileNumber = 0;
			int fileIndex = 0;
			for (int i=0; i<blocks; i++) {
				codeList.add("\tStyleDefinitions" + fileNumber + ".initStyles" + i + "();");
				fileIndex++;
				if (fileIndex >= MAX_BLOCKS_PER_FILE) {
					fileIndex = 0;
					fileNumber++;
				}
			}
			codeList.add("}");
			fileNumber = -1;
			fileIndex = MAX_BLOCKS_PER_FILE;
			ArrayList externalCodeList = new ArrayList(1000);
			int lineIndex = 0;
			for (int i=0; i<blocks; i++) {
				if (fileIndex >= MAX_BLOCKS_PER_FILE) {
					if (externalCodeList.size() > 0) {
						externalCodeList.add("}");
						try {
							File target = new File( device.getSourceDir() + "/de/enough/polish/ui/StyleDefinitions" + fileNumber + ".java");
							FileUtil.writeTextFile(target, externalCodeList);
						} catch (IOException e) {
							e.printStackTrace();
							throw new BuildException(e);
						}
						externalCodeList.clear();
					}
					fileIndex = 0;
					fileNumber++;
					externalCodeList.add("package de.enough.polish.ui;");
					externalCodeList.add("");
					externalCodeList.add("import java.util.Hashtable;");
					externalCodeList.add("class StyleDefinitions" + fileNumber + " {");
					externalCodeList.add("");
				}
				int start = i * BLOCKSIZE;
				int end = Math.min(  (i+1) * BLOCKSIZE, styles.length);
				lineIndex = addInitStylesMethod(externalCodeList, staticCodeList, styleCodeList,
						styles, blocks, i, lineIndex, start, end, styleSheet);
				fileIndex++;
			}
			if (externalCodeList.size() > 0) {
				externalCodeList.add("}");
				try {
					File target = new File( device.getSourceDir() + "/de/enough/polish/ui/StyleDefinitions" + fileNumber + ".java");
					FileUtil.writeTextFile(target, externalCodeList);
				} catch (IOException e) {
					e.printStackTrace();
					throw new BuildException(e);
				}
			}
		} else { // define all styles within StyleSheet.java:
			codeList.add("static { // init styles:" );
			for (int i=0; i<blocks; i++) {
				codeList.add("\tinitStyles" + i + "();");
			}
			codeList.add("}");
			int lineIndex = 0;
			for (int i=0; i<blocks; i++) {
				int start = i * BLOCKSIZE;
				int end = Math.min(  (i+1) * BLOCKSIZE, styles.length);
				lineIndex = addInitStylesMethod(codeList, staticCodeList, styleCodeList,
						styles, blocks, i, lineIndex, start, end, styleSheet);
			}
		}

		
		/*
		String[] styleNames = styleSheet.getUsedStyleNames();
		//System.out.println("processing [" + styleNames.length + "] styles.");
		int insertIndexForReferencedStyles = codeList.size();
		codeList.add("\t//normal used styles:");
		for (int i = 0; i < styleNames.length; i++) {
			String styleName = styleNames[i];
			if (!styleName.equals("default")) {
				Style style = styleSheet.getStyle( styleName );
				processStyle( style, codeList, styleSheet, device );
			}
		}
		codeList.add( STANDALONE_MODIFIER + "String lic=\"" + test +"\";");
		
		// process referenced styles:
		styles = (Style[]) this.referencedStyles.toArray( new Style[ this.referencedStyles.size() ] );
		boolean isLabelStyleReferenced = false;
		if (styles.length > 0) {
			ArrayList referencedCode = new ArrayList();
			referencedCode.add("\t//referenced styles:");
			for (int i = styles.length -1; i >= 0; i--) {
				Style style = styles[i];
				if (style.getSelector().equals("label")) {
					isLabelStyleReferenced = true;
				}
				if (!styleSheet.isUsed(style.getSelector())) {
					processStyle( style, referencedCode, styleSheet, device );
				}
			}
			codeList.addAll( insertIndexForReferencedStyles, referencedCode );
		}
		*/
		
		// check if label-style has been defined:
		if (!isLabelStyleReferenced) {
			//if (styleSheet.getStyle("label" ) == null) {
				codeList.add( "\tpublic static Style labelStyle = defaultStyle; // no specific label-style has been defined");
			/*} else {
				processStyle( styleSheet.getStyle("label" ), codeList, styleSheet, device );
			}*/
		}
		
		
		// check if fullscreen mode is enabled with menu:
		/*
		if ((preprocessor.hasSymbol("polish.useMenuFullScreen") 
			&& ((device.getCapability("polish.classes.fullscreen") != null) 
					|| (device.isMidp2() && preprocessor.hasSymbol("polish.hasCommandKeyEvents") ) ))
			|| preprocessor.hasSymbol("polish.needsManualMenu") 
			) {
			if (styleSheet.getStyle("menu" ) == null) {
				System.out.println("Warning: CSS style [menu] not found, you should define it for designing the FullScreen-menu.");
			} else {
				//processStyle( styleSheet.getStyle("menu" ), codeList, styleSheet, device );
				this.referencedStyles.add(styleSheet.getStyle("menu" ));
			}
		}
		*/
				
		
		// create focused style if necessary:
		Style focusedStyle = styleSheet.getStyle("focused"); 
		if (focusedStyle == null) {
			//System.out.println("Warning: CSS-Style [focused] not found, now using the default style instead. If you use Forms or Lists, you should define the style [focused].");
			codeList.add(  "\tpublic static Style focusedStyle = new Style(\"focused\", 0, new de.enough.polish.ui.backgrounds.SimpleBackground(0), null, new short[]{-17}, new Object[]{new Color(0xffffff, false)} );\t// the focused-style is not defined.");
		//} else {
		//	processStyle( focusedStyle, codeList, styleSheet, device );
		}
		
		// generate general warnings and hints:
		// check if title style has beend defined:
		if (styleSheet.getStyle("title" ) == null) {
			System.out.println("Warning: CSS style [title] not found, you should define it for designing the titles of screens.");
		}
		
		// add media queries:
		StyleSheet[] mediaQueries = styleSheet.getMediaQueries();
		if (mediaQueries != null) {
			styleCodeList.clear();
			codeList.add( "\tpublic static void showNotify(){" );
			for (int i = 0; i < mediaQueries.length; i++) {
				StyleSheet mediaQuery = mediaQueries[i];
				Style[] mediaStyles = mediaQuery.getAllStyles();
				codeList.add("\t\taddMediaQuery(\"" + mediaQuery.getMediaQueryCondition() + "\", new Style[]{");
				for (int j = 0; j < mediaStyles.length; j++) {
					Style style = mediaStyles[j];
					processStyle( false, style, codeList, null, false, styleSheet, device, env );
					if (j != mediaStyles.length-1) {
						codeList.add("\t\t, ");
					}
				}
				codeList.add("\t\t}); // end of media query " + mediaQuery.getMediaQueryCondition() );
			}
			codeList.add("\t} // end of showNotify()");
		}
		

		// now insert the created source code into the source of the polish-StyleSheet.java:
		String[] code = (String[]) codeList.toArray( new String[ codeList.size()]);
		sourceCode.insert(code);
		sourceCode.forward( code.length );
	}

	private int addInitStylesMethod(ArrayList codeList,
			ArrayList staticCodeList, ArrayList styleCodeList, Style[] styles,
			int blocks, int i, int lineIndex, int start, int end,
			StyleSheet styleSheet) 
	{
		codeList.add( "protected static final void initStyles" + i + "(){");
		for (int j=start; j<end;) {
			String line = (String) styleCodeList.get(lineIndex);
			codeList.add( line );
			if (line.indexOf(';') != -1) {
				j++;
			}
			lineIndex++;
		}
		if (i == blocks -1) {
			// register all styles in the last static method:
			// process dynamic styles:
			if (styleSheet.containsDynamicStyles()) {
				Style[] dynamicStyles = styleSheet.getDynamicStyles(); 
				for (int j = 0; j < dynamicStyles.length; j++) {
					Style style = dynamicStyles[j];
					this.referencedStyles.add( style );
				}
			}
			codeList.add("");
			codeList.add("\t//register referenced and dynamic styles:");
			for (int j = 0; j < styles.length; j++) {
				Style style = styles[j];
				String selector = style.getSelector();
				codeList.add("\tStyleSheet.stylesByName.put( \"" + selector + "\", StyleSheet." + style.getStyleName() + "Style );");
			}
			styles = (Style[]) this.referencedStyles.toArray( new Style[ this.referencedStyles.size() ] );
			for (int j = 0; j < styles.length; j++) {
				Style style = styles[j];
				String name = style.getAbbreviation();
				if (name == null) {
					// quickfix 2009-02-24: 
					// only when there is no abbreviation, this style is a truly dynamic
					// one... Or so we hope.
					name = style.getSelector();
					codeList.add("\tStyleSheet.stylesByName.put( \"" + name + "\", StyleSheet." + style.getStyleName() + "Style );");
				}
			}
			for (Iterator iter = staticCodeList.iterator(); iter.hasNext();) {
				String line  = (String) iter.next();
				codeList.add( line );
			}
		}
		codeList.add( "}");
		return lineIndex;
	}



	/**
	 * Processes the default style:
	 * 
	 * @param defaultFontDefined true when the default font has been defined already
	 * @param defaultBackgroundDefined true when the default background has been defined already
	 * @param defaultBorderDefined true when the default border has been defined already
	 * @param codeList the list to which the declarations should be added
	 * @param styleSheet the parent style sheet
	 * @param device the device for which the style should be processed
	 * @param environment the environment
	 */
	protected void processDefaultStyle(boolean defaultFontDefined, boolean defaultBackgroundDefined, boolean defaultBorderDefined, ArrayList codeList, ArrayList staticCodeList, boolean defineStylesOutside, StyleSheet styleSheet, Device device, Environment environment ) {
		//System.out.println("PROCESSSING DEFAULT STYLE " + styleSheet.getStyle("default").toString() );
		Style copy = new Style( styleSheet.getStyle("default"));
		HashMap group = copy.getGroup("font");
//		if (!defaultFontDefined) {
//			if (group == null) {
//				codeList.add( STANDALONE_MODIFIER + "int defaultFontColor = 0x000000;");
//				codeList.add( STANDALONE_MODIFIER + "Font defaultFont = Font.getDefaultFont();");
//			} else {
//				processFont(group, "default", copy, codeList, styleSheet, true, environment );
//			}
//		}
		group = copy.getGroup("background");
		if (!defaultBackgroundDefined) {
			if (group == null) {
				staticCodeList.add( STANDALONE_MODIFIER + "Background defaultBackground = null;");
			} else {
				processBackground("default", group, copy, staticCodeList, false, styleSheet, true, environment );
			}
		}
		group = copy.getGroup("border");
		if (!defaultBorderDefined) {
			if (group == null) {
				staticCodeList.add( STANDALONE_MODIFIER + "Border defaultBorder = null;");
			} else {
				processBorder("default", group, copy, staticCodeList, false, styleSheet, true, environment );
			}
		}
		// set default values:
		//copy.setSelector("defaultStyle");
		group = new HashMap();
		if (defaultFontDefined) {
			group.put("font", "default");
		}
		copy.addGroup("font", group );
		group = new HashMap();
		group.put("background", "default");
		copy.addGroup("background", group );
		group = new HashMap();
		group.put("border", "default");
		copy.addGroup("border", group );
		// now process the rest of the style completely normal:
		processStyle(copy, codeList, staticCodeList, defineStylesOutside, styleSheet, device, environment );
	}

	/**
	 * Processes the give style and includes the generated code to the codeList.
	 * 
	 * @param style the style
	 * @param codeList the array list into which generated code is written
	 * @param styleSheet the parent style sheet
	 * @param device the device for which the style should be processed
	 * @param environment the environment
	 */
	protected void processStyle(Style style, ArrayList codeList, ArrayList staticCodeList, boolean defineStylesOutside, StyleSheet styleSheet, Device device, Environment environment ) {
		processStyle( true, style, codeList, staticCodeList, defineStylesOutside, styleSheet, device, environment );
	}

	/**
	 * Processes the give style and includes the generated code to the codeList.
	 * 
	 * @param style the style
	 * @param codeList the array list into which generated code is written
	 * @param styleSheet the parent style sheet
	 * @param device the device for which the style should be processed
	 * @param environment the environment
	 */
	protected void processStyle(boolean declareVariable, Style style, ArrayList codeList, ArrayList staticCodeList, boolean defineStylesOutside, StyleSheet styleSheet, Device device, Environment environment ) {
		String styleName = style.getStyleName();
		//System.out.println("processing style " + style.getStyleName() + ": " + style.toString() );
		// create a new style field:
		if (declareVariable) {
			staticCodeList.add( STANDALONE_MODIFIER_NON_FINAL + "Style " + styleName + "Style;");
			// create a new style:
			if (defineStylesOutside) {
				styleName = "StyleSheet." + styleName;
			}
			codeList.add( "\t" + styleName + "Style = new Style (");
		} else {
			codeList.add("new Style(");
		}
		codeList.add("\t\t\"" + style.getSelector() + "\", ");
		// process all animations, do this here so animations can also be done for margins, paddings, font settings etc:
		CssAnimationSetting[] cssAnimations = extractAnimationSettings(style);
		ArrayList animationsList = new ArrayList();
		for (int g=0; g<cssAnimations.length; g++) {
			CssAnimationSetting cssAnimation = cssAnimations[g];
			//System.out.println("got animation for " + cssAnimation.getCssAttributeName()  + " in style " + styleName + " with trigger " + cssAnimation.getOn() );
			String cssAttributeName = cssAnimation.getCssAttributeName();
			CssAttribute  attribute = this.attributesManager.getAttribute( cssAttributeName );
			if (attribute == null) {
				throw new BuildException("Invalid CSS: the attribute \"" + cssAttributeName + "\" is unknown and cannot be animated. Check the animation in style \"" + style.getSelector() + "\" in your polish.css file.");
			}
			if (attribute.getId() == -1) {
				if (attribute.isBaseAttribute()) {
					throw new BuildException("Invalid CSS: Sorry, but at the moment you cannot animate the base CSS attribute \"" + cssAttributeName + "\".");
				}
				throw new BuildException("Invalid CSS: Unable to process animation for CSS attribute \"" + cssAttributeName + "\" with an unknown id: please register the \"id\" attribute in custom-css-attributes.xml.");
			}
			CssAnimationRange[] ranges = cssAnimation.getRanges();
			if (ranges != null && ranges.length > 1) {
				// okay, there are several css animations within one setting, so used a combined CssAnimation:
				StringBuffer animationSourceBuffer = attribute.generateAnimationSourceCodeStart("CombinedCssAnimation", cssAnimation, style, environment);
				animationSourceBuffer.append( "new CssAnimation[]{");
				for (int i = 0; i < ranges.length; i++)
				{
					CssAnimationRange cssAnimationRange = ranges[i];
					CssAnimationSetting setting = cssAnimation.getSubSetting( i, ranges.length, cssAnimationRange );
					String animationSourceCode = attribute.generateAnimationSourceCode( setting, style, environment );
					if (animationSourceCode == null) {
						throw new BuildException("Animations for " + cssAttributeName + " are not supported - check your polish.css for style " + style.getSelector() + ".");
					}
					animationSourceBuffer.append( animationSourceCode );
					if (i < ranges.length - 1) {
						animationSourceBuffer.append(", ");
					}
				}
				animationSourceBuffer.append( "} )");
				animationsList.add( animationSourceBuffer.toString() );
				//System.out.println("adding combined animation: " + animationSourceBuffer.toString() );
			} else {
				String animationSourceCode = attribute.generateAnimationSourceCode( cssAnimation, style, environment );
				if (animationSourceCode == null) {
					throw new BuildException("Animations for " + cssAttributeName + " are not supported - check your polish.css for style " + style.getSelector() + ".");
				}
				//System.out.println("adding single animation: " + animationSourceCode);
				animationsList.add( animationSourceCode );
			}
		}
		
		// backwards compatibility hack: in previous versions bitmap fonts were implemented without using
		// a text effect:
		String fontBitmap = style.getAttributeValue("font-bitmap");
		if (fontBitmap != null && style.getAttributeValue("text-effect") == null && !"none".equals(fontBitmap) ) {
			style.addAttribute("text-effect", "bitmap");
		}

//		// process the margins:
//		HashMap group = style.removeGroup("margin");
//		if ( group != null ) {
//			processFields( 0, false, group, "margin", style, codeList, styleSheet, device );
//		} else {
//			codeList.add("\t\t0,0,0,0,\t// default margin");
//		}
//		// process the paddings:
//		group = style.removeGroup("padding");
//		if ( group != null ) {
//			processFields( 1, true, group, "padding", style, codeList, styleSheet, device );
//		} else {
//			codeList.add("\t\t1,1,1,1,1,1,\t// default padding");
//		}
		// process the layout:
		Map group = style.removeGroup("layout");
		if ( group != null ) {
			processLayout( group, style, codeList, styleSheet, environment );
		} else {
			codeList.add("\t\tItem.LAYOUT_DEFAULT,\t// default layout");
		}
		
		// process font references:
		group = style.getGroup("font");
		if (group != null) {
			String font = (String) group.get("font");
			if (font != null) {
				Map fontGroup = (Map) styleSheet.getFonts().get(font);
				if (fontGroup == null) {
					fontGroup = (Map) styleSheet.getFonts().get(font + "Font");
					if (fontGroup == null) {
						if ("default".equals(font)) {
							fontGroup = new HashMap();
							styleSheet.getFonts().put("default", fontGroup);
						} else {
							throw new BuildException("Invalid CSS: invalid font-reference \"font: " + font + "\" - please check your polish.css style " + style.getSelector() + "." );
						}
					}
				}
				group.remove("font");
				Object[] keys = fontGroup.keySet().toArray();
				for (int j = 0; j < keys.length; j++)
				{
					String key = (String) keys[j];
					if (!group.containsKey(key)) {
						String name = key;
						if (name.startsWith("font-")) {
							name = name.substring("font-".length());
						}
						//System.out.println("adding key " + name + "=" + fontGroup.get(key));
						group.put( name, fontGroup.get(key));
					}
				}
			}
		}
		
//		// process the content-font:
//		group = style.removeGroup("font");
//		String bitMapFontUrl = null;
//		if ( group != null ) {
//			// if a bitmap-font is defined,
//			// add a extended (string) attribute:
//			bitMapFontUrl = (String) group.get("bitmap");
//			if (bitMapFontUrl != null && !"none".equals( bitMapFontUrl)) {
//				if (!bitMapFontUrl.endsWith(".bmf")) {
//					bitMapFontUrl += ".bmf";
//				}
//				if (bitMapFontUrl.charAt(0) != '/') {
//					bitMapFontUrl = "/" + bitMapFontUrl;
//				}
//				HashMap bitMapFontGroup = new HashMap(1);
//				bitMapFontGroup.put("bitmap", bitMapFontUrl );
//				style.addGroup( "font", bitMapFontGroup );
//				//System.out.println("adding bitmap-font " + bitMapFontUrl);
//			}
//			processFont( group, "font", style, codeList, styleSheet, false, environment );
//		} else {
//			codeList.add("\t\tdefaultFontColor,\t// font-color is not defined");
//			codeList.add("\t\tnull,\t// font-color is not defined");
//			codeList.add("\t\tdefaultFont,");
//		}
		
//		// process the content-font:
//		boolean removeLabelGroup = true;
//		group = style.getGroup("label");
//		if (group != null) {
//			String labelStyle = (String) group.get("style"); 
//			if ( labelStyle != null ) {
//				if (styleSheet.isDefined(labelStyle)) {
//					// okay this is a valid reference:
//					removeLabelGroup = false;
//				}
//			}
//		}
//		if (removeLabelGroup) {
//			style.removeGroup("label");			
//		}
		
		// process the background:
		group = style.removeGroup("background");
		if ( group != null ) {
			processBackground( style.getSelector(), group, style, codeList, defineStylesOutside, styleSheet, false, environment );
			// ugly hack to preserve background-bottom and background-top, etc. attributes:
			preserveAttributes( group, style, "background", this.backgroundAttributeNames );
		} else {
			codeList.add("\t\tnull,\t// no background");
		}

		// process the border:
		group = style.removeGroup("border");
		if ( group != null ) {
			processBorder( style.getSelector(), group, style, codeList, defineStylesOutside, styleSheet, false, environment );
			// ugly hack to preserve background-bottom and background-top, etc. attributes:
			preserveAttributes( group, style, "border", this.borderAttributeNames );
		} else {
			codeList.add("\t\tnull, \t// no border");
		}
		
		// now add all additional non standard-properties:
		String[] groupNames = style.getGroupNames();
		if (groupNames.length == 0) {
			codeList.add( "\t\tnull, null\t// no additional attributes have been defined" );
		} else {
			// count the number of CSS attributes:
			int numberOfAttributes = 0;
			for (int i = 0; i < groupNames.length; i++) {
				String groupName = groupNames[i];
				group = style.getGroup(groupName);
				numberOfAttributes += group.size();
			}
			StringBuffer keyList = new StringBuffer();
			keyList.append("new short[]{ ");
			StringBuffer valueList = new StringBuffer();
			valueList.append("new Object[]{ ");
			int currentAttribute = 0;
			for (int i = 0; i < groupNames.length; i++) {
				String groupName = groupNames[i];
				group = style.getGroup(groupName);
				if (group == null) {
					System.err.println("unable to get group [" + groupName + "] of style : " + style.toString());
				}
				Set keys = group.entrySet();
				for (Iterator iter = keys.iterator(); iter.hasNext();) {
					currentAttribute++;
					Map.Entry entry = (Map.Entry) iter.next();
					String key = (String) entry.getKey();
					String value = (String) entry.getValue();
					String attributeName;
					if (key.equals(groupName)) {
						attributeName = groupName;
					} else {
						attributeName = groupName + "-" + key;
					}
					short attributesId = styleSheet.getAttributeId(attributeName);
					CssAttribute attribute = this.attributesManager.getAttribute( attributeName );
					if (attributesId == -1) {
						// the CSS attribute is nowhere used in the preprocessing code,
						// now check if it has been registered in css-attributes.xml/custom-css-attributes.xml:
						if (attribute == null) {
							throw new BuildException("Invalid CSS: The CSS setting \"" + attributeName + ": " + value + ";\" is not supported. Please check style \"" + style.getSelector() + "\" in your \"polish.css\" file(s).");
						}
					}
					keyList.append( attributesId );
					if ( currentAttribute < numberOfAttributes) {
						keyList.append(", ");
					}
					
					if (attribute != null) {
						if (attribute.isDeprecated()) {
							System.out.println("Warning: CSS attribute \"" + attributeName + "\" is deprecated: " + attribute.getDeprecatedMessage() );
						}
						value = attribute.getValue( value, environment );
//						if ("none".equals(value) && !("plain".equals(key) || "selected".equals(key)) ) {
//							System.out.println("setting value from none to null for key " + key);
//							// note: !"plain".equals(key) is an ugly hack for
//							// radiobox-plain and choicebox-plain...
//							// For such cases a converter should really be used instead.
//							value = "null";
//						}
						//attribute.checkValue( value, evaluator );
//						if (attributeType == CssAttribute.INTEGER && attribute.hasFixValues()) {
//							value = Integer.toString( attribute.getValuePosition(value) );
//						}
					} else {
						// the attribute is not registered anywhere:
						System.out.println("Recommendation: It is advised to register the CSS-attribute [" + attributeName + "] in the [custom-css-attributes.xml].");
						if ( attributeName.endsWith("-color")) {
							value = Integer.toString( Integer.parseInt( this.colorConverter.parseColor(value).substring(2), 16) );
						}
					}
					// process columns-width value:
					// remove all spaces and check for the star-value (e.g. "20, *"):
					if (key.equals("width") && groupName.equals("columns")) {
						// remove any spaces, e.g. "columns-width: 50, 100, *":
						// TODO: use specialized CSS attribute
						value = StringUtil.replace( value, " ", "" );
					}
					if (attribute instanceof StyleCssAttribute || ( attribute == null && (key.endsWith("style") || value.startsWith("style(")) ) ) {
						value = getStyleReference( value, style, styleSheet );
					}
					if (attribute != null) {
						valueList.append( value );
					} else {
						if ( "none".equals(value) && !"plain".equals(key)) {
							// note: !"plain".equals(key) is an ugly hack for
							// radiobox-plain and choicebox-plain...
							// For such cases a converter should really be used instead.
							valueList.append("null");
						} else {
							valueList.append('"')
							.append( value )
							.append('"');
						}
					}
					if ( currentAttribute < numberOfAttributes) {
						valueList.append(", ");
					}					
				}
			}
			keyList.append("}");
			valueList.append("}");
			codeList.add( "\t\t" + keyList.toString() + ",");
			codeList.add( "\t\t" + valueList.toString());
		} // if there are any non-standard attribute-groups
		if (animationsList.size() > 0) {
			codeList.add("\t\t, new CssAnimation[]{");
			for (int i=0; i<animationsList.size(); i++) {
				String code = "\t\t\t" + animationsList.get(i);
				if (i < animationsList.size() - 1) {
					code += ",";
				}
				codeList.add(code);
			}
			codeList.add("\t\t}");
		}
		
		// close the style definition:
		if (declareVariable) {
			codeList.add("\t);");
		} else {
			codeList.add("\t)");
		}
		
		// add the selector of the style, but only when dynamic styles are used:
		//TODO: hack for WYSIWYG designer:
//		//if (styleSheet.containsDynamicStyles()) {
//			staticCodeList.add("\t" + styleName + "Style.name = \"" + style.getSelector() + "\"; \t// the selector of the above style");
//		//}

	}


	
	/**
	 * @param group
	 * @param style
	 * @param string
	 * @param backgroundAttributeNames2
	 */
	private void preserveAttributes(Map group, Style style, String groupName,
			String[] names)
	{
		HashMap preserveGroup = new HashMap();
		for (int i = 0; i < names.length; i++)
		{
			String name = names[i];
			Object value = group.get(name);
			if (value != null) {
				preserveGroup.put( name, value );
			}
		}
		if (preserveGroup.size() > 0) {
			style.addGroup(groupName, preserveGroup);
		}
	}

	/**
	 * @param style
	 * @return an array of animation settings
	 */
	protected CssAnimationSetting[] extractAnimationSettings(Style style)
	{
		CssDeclarationBlock[] animationBlocks = style.removeDeclarationBlocksEndingWith("-animation");
		// since several animations for the same CSS attribute are allowed when
		// they use different "on" trigger, we can end up with more than one
		// animation for the same CSS attribute and the same trigger. We use a 
		// hashtable for only instantiating the first animation:
		ArrayList settingsList = new ArrayList();
		HashMap settingsMap = new HashMap();
		for (int i = 0; i < animationBlocks.length; i++)
		{
			CssDeclarationBlock cssDeclarationBlock = animationBlocks[i];
			CssAnimationSetting setting = new CssAnimationSetting(cssDeclarationBlock);
			String key = setting.getCssAttributeName() + setting.getOn();
			if (settingsMap.get(key) == null) {
				settingsList.add( setting );
				settingsMap.put(key, setting);
			}
		}
		return (CssAnimationSetting[]) settingsList.toArray( new CssAnimationSetting[ settingsList.size() ] );
	}

	/**
	 * Retrieves the color value as a decimal integer value.
	 * 
	 * @param value the color
	 * @return the color as a decimal integer value
	 */
	protected String getColor(String value) {
		String color = this.colorConverter.parseColor(value);
		return color;
	}


	/**
	 * Gets a reference to another style.
	 * 
	 * @param value the reference
	 * @param parent the parent style
	 * @param styleSheet the sheet in which the style is embedded
	 * @return the name of the reference style
	 */
	protected String getStyleReference(String value, Style parent, StyleSheet styleSheet) {
		//System.out.println("CssConverter: getting style reference for " + value );
		String reference = value.toLowerCase();
		if (value.startsWith("style(")) {
			reference = reference.substring( 6 );
			int closingPos = reference.indexOf(')');
			if (closingPos == -1) {
				throw new BuildException("Invalid CSS: the style-reference [" + value + "] in style [" + parent.getSelector() + "] needs to be closed by a parenthesis.");
			}
			reference = reference.substring( 0, closingPos ).trim();
		}
		if (reference.charAt(0) == '.') {
			reference = reference.substring( 1 );
		}
		if (this.abbreviationGenerator == null) {
			this.abbreviationGenerator = new AbbreviationsGenerator();
		}
		String abbreviation = this.abbreviationGenerator.getAbbreviation(reference, true );
		Style style = styleSheet.getStyle(reference);
		if (style == null) {
			if (reference.equals("null")) {
				return "null";
			}
			throw new BuildException("Invalid CSS: the style-reference to \"" + value + "\" in style [" + parent.getSelector() + "] refers to a non-existing style.");
		}
		style.setAbbreviation( abbreviation );
		// add it to the list of referenced styles, 
		// but only when it has not been added before:
		if (! this.referencedStyles.contains(style)) {
			//System.out.println("adding style " + style.getSelector() + " to references");
			this.referencedStyles.add( style );
		}
		//System.out.println("CssConverter: resolved style reference for " + value + " is " + (reference + "Style") );
		return "StyleSheet." + reference + "Style"; //abbreviation;
	}


	/**
	 * Creates the layout-declaration for the style.
	 * 
	 * @param group the layout directive
	 * @param style the parent style
	 * @param codeList the source code
	 * @param styleSheet the parent style sheet
	 */
	protected void processLayout(Map group, Style style, ArrayList codeList, 
			StyleSheet styleSheet, Environment env) 
	{
		String layoutValue = (String) group.get("layout");
		if (layoutValue == null) {
			// this must be some other CSS definitions:
			style.addGroup( "layout", group);
			return;
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append( "\t\t" );
		buffer.append( this.layoutAttribute.getValue(layoutValue, env) );
		buffer.append(",");
		codeList.add( buffer.toString() );

//		layoutValue = layoutValue.toLowerCase();
//		String[] layouts;
//		// the layout value can combine several directives, e.g. "vcenter | hcenter"
//		if ( layoutValue.indexOf('|') != -1 ) {
//			layouts = StringUtil.splitAndTrim( layoutValue, '|');
//		} else if ( layoutValue.indexOf('&') != -1 ) {
//				layouts = StringUtil.splitAndTrim( layoutValue, '&');
//		} else if ( layoutValue.indexOf(',') != -1 ) {
//			layouts = StringUtil.splitAndTrim( layoutValue, ',');
//		} else if ( layoutValue.indexOf(" and ") != -1 ) {
//			layouts = StringUtil.splitAndTrim( layoutValue, " and ");
//		} else if ( layoutValue.indexOf(" or ") != -1 ) {
//			layouts = StringUtil.splitAndTrim( layoutValue, " or ");
//		} else {
//			layouts = new String[]{ layoutValue };
//		}
//		// now add definition for each layout:
//		StringBuffer buffer = new StringBuffer();
//		buffer.append( "\t\t" );
//		for (int i = 0; i < layouts.length; i++) {
//			boolean finished = (i == layouts.length -1 );
//			String name = layouts[i];
//			String layout = (String) LAYOUTS.get( name );
//			if (layout == null) {
//				if (layouts.length > 1) {
//					throw new BuildException("Invalid CSS: the layout directive [" + layoutValue + "] contains the invalid layout [" + name +"]. Please use a valid value instead.");
//				} else {
//					throw new BuildException("Invalid CSS: the layout directive [" + name +"] is not valid. Please use a valid value instead.");
//				}
//			}
//			buffer.append( layout );
//			if (!finished) {
//				buffer.append(" | ");
//			}
//		}
//		buffer.append(",");
//		codeList.add( buffer.toString() );
	}


	/**
	 * Creates the border-definition.
	 * 
	 * @param borderName the name of the border
	 * @param group the map containing the border information
	 * @param style the style
	 * @param codeList the array list into which generated code is written
	 * @param styleSheet the parent style sheet
	 * @param isStandalone true when a new public border-field should be created,
	 *        otherwise the border will be embedded in a style instantiation. 
	 */
	protected void processBorder(String borderName, Map group, Style style, ArrayList codeList, boolean defineStylesOutside, StyleSheet styleSheet, boolean isStandalone, Environment env ) {
		//System.out.println("processing border " + borderName + " = "+ group.toString() );
		String reference = (String) group.get("border");
		if (reference != null && group.size() == 1) {
			if ("none".equals(reference) || "null".equals(reference)) {
				if (isStandalone) {
					codeList.add( STANDALONE_MODIFIER + "Border " + borderName + "Border = null;\t// border:none was specified");
				} else {
					codeList.add( "\t\tnull,\t// border:none was specified");
				}
			} else {
				if (defineStylesOutside) {
					reference = "StyleSheet." + reference;
				}
				// a reference to an existing border is given:
				if (isStandalone) {
					codeList.add( STANDALONE_MODIFIER + "Border " + borderName + "Border = " + reference + "Border;");
				} else {
					codeList.add( "\t\t" + reference + "Border, ");
				}
			}
			return;
		}
		String type = getAttributeValue("border", "type", group );
		String originalType = type;
		if (type == null) {
			type = "simple";
		} else {
			type = type.toLowerCase();
		}
		CssMapping mapping = this.borderAttribute.getMapping(type);
		if (mapping != null && mapping.getConverter() == null) {
			String code = this.parameterizedAttributeConverter.createNewStatement(this.borderAttribute, (ParameterizedCssMapping)mapping, group, env);
			if (isStandalone) {
				codeList.add( STANDALONE_MODIFIER + "Border " + borderName + "Border = " + code + ";");
			} else {
				codeList.add( code + ", " );
			}
		} else {
			// old style converter is used
			String className;
			if (mapping != null) {
				className = mapping.getConverter();
			} else {
				className = this.borderAttribute.getValue( type, env );
			}
			if (className == null) {
				System.out.println("Warning: unable to resolve border-type [" + type + "] - please check your polish.css or register this border-type in ${polish.home}/custom-css-attributes.");
				className = originalType;
			}
			try {
				BorderConverter creator =  (BorderConverter) Class.forName(className).newInstance();
				creator.setColorConverter(this.colorConverter);
				creator.addBorder( codeList, group, borderName, style, styleSheet, isStandalone );
			} catch (ClassNotFoundException e) {
				throw new BuildException("Invalid CSS: unable to load border-type [" + type + "] with class [" + className + "]:" + e.getMessage() +  " (" + e.getClass().getName() + ")\nMaybe you need to adjust the CLASSPATH setting.", e );
			} catch (BuildException e) {
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
				throw new BuildException("Invalid CSS: unable to load border-type [" + type + "] with class [" + className + "]:" + e.getMessage() +  " (" + e.getClass().getName() + ")\nMaybe you need to adjust the CLASSPATH setting.", e );
			}
		}
	}


	/**
	 * Creates the background-definition.
	 * 
	 * @param backgroundName the name of the background
	 * @param group the map containing the background information
	 * @param style the style
	 * @param codeList the array list into which generated code is written
	 * @param styleSheet the parent style sheet
	 * @param isStandalone true when a new public background-field should be created,
	 *        otherwise the background will be embedded in a style instantiation. 
	 */
	protected void processBackground(String backgroundName, Map group, Style style, ArrayList codeList, boolean defineStylesOutside, StyleSheet styleSheet, boolean isStandalone, Environment env ) {
		//System.out.println("processing background " + backgroundName + " = " + group.toString() );
		// check if the background is just a reference to another background:
		String reference = (String) group.get("background");
		if (reference != null && group.size() == 1) {
			if ("none".equals(reference) || "null".equals(reference)) {
				if (isStandalone) {
					codeList.add( STANDALONE_MODIFIER + "Background " + backgroundName + "Background = null;\t// background:none was specified");
				} else {
					codeList.add( "\t\tnull,\t// background:none was specified");
				}
			} else {
				// a reference to an existing background is given:
				if (defineStylesOutside) {
					reference = "StyleSheet." + reference;
				}
				if (isStandalone) {
					codeList.add( STANDALONE_MODIFIER + "Background " + backgroundName + "Background = " + reference + "Background;");
				} else {
					codeList.add( "\t\t" + reference + "Background, ");
				}
			}
			return;
//		} else if (reference != null) {
//			System.out.println("ignoring reference " + reference + " in style " + style.getSelector());
		}
		
		String type = getAttributeValue("background", "type", group );
		String originalType = type;
		if (type == null) {
			// this should be a simple background:
			String imageUrl = getAttributeValue("background", "image", group );
			if (imageUrl == null || "none".equals(imageUrl)) {
				// this a simple background:
				type = "simple";
			} else {
				// this is a image background:
				type = "image";
			}
		} else {
			type = type.toLowerCase();
			if ("image".equals( type )) {
				String imageUrl = (String) group.get("image");
				if ("none".equals(imageUrl)) {
					// this a simple background, even though the type is specified:
					type = "simple";
				}
			}
		}
		//String className = (String) BACKGROUND_TYPES.get(type);
		CssMapping mapping = this.backgroundAttribute.getMapping(type);
		if (mapping != null && mapping.getTo() != null && mapping.getTo().startsWith("ref:")) {
			type = mapping.getTo().substring("ref:".length()).trim();
			mapping = this.backgroundAttribute.getMapping( type );
		}
		if (mapping != null && mapping.getConverter() == null) {
			String code = this.parameterizedAttributeConverter.createNewStatement(this.backgroundAttribute, (ParameterizedCssMapping)mapping, group, env);
			if (isStandalone) {
				codeList.add( STANDALONE_MODIFIER + "Background " + backgroundName + "Background = " + code + ";");
			} else {
				codeList.add( code + ", " );
			}
		} else {
			// old style converter is used
			String className = mapping != null ? mapping.getConverter() : originalType;
			try {
				BackgroundConverter creator =  (BackgroundConverter) Class.forName(className).newInstance();
				creator.setColorConverter(this.colorConverter);
				creator.addBackground( codeList, group, backgroundName, style, styleSheet, isStandalone );
			} catch (ClassNotFoundException e) {
				throw new BuildException("Invalid CSS: unable to load background-type [" + type + "] with class [" + className + "]:" + e.getMessage() +  " (" + e.getClass().getName() + ")\nMaybe you need to adjust the CLASSPATH setting.", e );
			} catch (BuildException e) {
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
				throw new BuildException("Invalid CSS: unable to load background-type [" + type + "] with class [" + className + "]:" + e.getMessage() +  " (" + e.getClass().getName() + ")\nMaybe you need to adjust the CLASSPATH setting.", e );
			}		
		}
	}

	/**
	 * Adds a font definition.
	 * 
	 * @param group the font definition
	 * @param groupName the name of this font - usually "font" or "labelFont"
	 * @param style the style
	 * @param codeList the array list into which generated code is written
	 * @param styleSheet the parent style sheet
	 * @param isStandalone true when a new public font-field should be created,
	 *        otherwise the font will be embedded in a style instantiation. 
	 */
	protected void processFont(HashMap group, String groupName, Style style, 
			ArrayList codeList, StyleSheet styleSheet, boolean isStandalone, Environment environment ) 
	{
		String typeName = "Font";
		// check if this is a reference to another font:
		String reference = (String) group.get(groupName);
		if (reference != null  && group.size() == 1) {
			if (isStandalone) {
				codeList.add( STANDALONE_MODIFIER + "int " + groupName + typeName 
						+ "Color = " + reference + typeName + "Color;" );
				codeList.add( STANDALONE_MODIFIER + "Font " + groupName + typeName 
						+ " = " + reference + typeName + ";" );
			} else {
				codeList.add( "\t\t" + reference + typeName + "Color," );
				codeList.add( "\t\tnull," );
				codeList.add( "\t\t" + reference + typeName + "," );
			}
			return;
		}
		// get font color:
		String fontColor = getAttributeValue( "font", "color", group );
		if (isStandalone) {
			String newStatement = STANDALONE_MODIFIER + "int " + groupName + typeName + "Color = "; 
			if (fontColor != null) {
				newStatement +=  this.colorConverter.parseColor(fontColor) + ";";
			} else {
				newStatement +=  "0x000000; // default font color is black";
			}
			codeList.add( newStatement );
		} else {
			if (fontColor != null) {
				String color = this.colorConverter.parseColor(fontColor);
				boolean isDynamic = this.colorConverter.isDynamic(fontColor);
				codeList.add( "\t\t" + color + ",\t// " + groupName + "-color");
				codeList.add( "\t\tnew Color(" + color + ", " + isDynamic + "),\t// " + groupName + "-color");
			} else {
				codeList.add( "\t\t0x000000,\t// " + groupName + "-color (default is black)");
			}
		}
		// get the font:
		String face = getAttributeValue("font", "face", group);
		String styleStr = getAttributeValue("font", "style", group);
		String size = getAttributeValue("font", "size", group);
		String newStatement;
		if (face == null && styleStr == null && size == null) {
			newStatement = "Font.getDefaultFont()";
		} else {
			// at least one font property is defined:
			FontConverter font = new FontConverter();  
			if ( face != null) {
				font.setFace( this.fontFaceAttribute.getValue( face, environment ) );
			}
			if ( styleStr != null) {
				font.setStyle( this.fontStyleAttribute.getValue(styleStr, environment) );
			}
			if ( size != null) {
				font.setSize( this.fontSizeAttribute.getValue( size, environment ) );
			}
			
			
			newStatement =  font.createNewStatement();
		}
		if (isStandalone) {
			newStatement = STANDALONE_MODIFIER + "Font " 
				+ groupName + typeName + " = " + newStatement + ";";
		} else {
			newStatement = "\t\t" + newStatement + ", //" + groupName;
			
		}
		
		codeList.add( newStatement  );
	}

	/**
	 * Retrieves a attribute value.
	 * 
	 * @param groupName the name of the attribute-group, e.g. "font" or "background" 
	 * @param attributeName the name of the attribute, e.g. "color"
	 * @param group the attribute group
	 * @return the value or null when not defined
	 */
	protected String getAttributeValue( String groupName, String attributeName, Map group) {
		 String result = (String) group.get(attributeName);
		 if (result == null) {
			 result = (String) group.get( groupName + "-" + attributeName );
		 }
		return result;
	}

	/**
	 * Processes the given fields - currently either "margins" or "paddings".  
	 * 
	 * @param defaultValue the default value for unset fields
	 * @param includeVerticalHorizontal true when vertical and horizontal fields should
	 * 		  also be processed (this is the case for paddings).
	 * @param fields the definition of the fields
	 * @param groupName the name of the group
	 * @param style the style
	 * @param codeList the array list into which generated code is written
	 * @param styleSheet the parent style sheet
	 * @param device the device for which the fields should be processed
	 */
	protected void processFields(int defaultValue, boolean includeVerticalHorizontal, HashMap fields, String groupName, Style style, ArrayList codeList, StyleSheet styleSheet, Device device) {
		StringBuffer result = new StringBuffer();
		result.append("\t\t");
		String styleName = style.getSelector();
		int screenWidth = -1;
		String screenWidthStr = device.getCapability("ScreenWidth");
		if (screenWidthStr != null) {
			screenWidth = Integer.parseInt( screenWidthStr );
		}
		int screenHeight = -1;
		String screenHeightStr = device.getCapability("ScreenHeight");
		if (screenHeightStr != null) {
			screenHeight = Integer.parseInt( screenHeightStr );
		}
				
		String defaultValueStr = (String) fields.get(groupName);
		if (defaultValueStr != null) {
			defaultValue = parseInt(styleName, groupName, "", defaultValueStr);
		}
		String value = (String) fields.get("left");
		if (value != null) {
			result.append( parseInt( styleName, groupName, "left", value, screenWidth )).append( ',');
		} else {
			result.append( defaultValue ).append( ',');
		}
		value = (String) fields.get("right");
		if (value != null) {
			result.append( parseInt( styleName, groupName, "right", value, screenWidth )).append( ',');
		} else {
			result.append( defaultValue ).append( ',');
		}
		value = (String) fields.get("top");
		if (value != null) {
			result.append( parseInt( styleName, groupName, "top",  value, screenHeight )).append( ',');
		} else {
			result.append( defaultValue ).append( ',');
		}
		value = (String) fields.get("bottom");
		if (value != null) {
			result.append( parseInt( styleName, groupName, "bottom",  value, screenHeight )).append( ',');
		} else {
			result.append( defaultValue ).append( ',');
		}
		if (includeVerticalHorizontal) {
			value = (String) fields.get("vertical");
			if (value != null) {
				result.append( parseInt( styleName, groupName, "vertical",  value, screenWidth )).append( ',');
			} else {
				result.append( defaultValue ).append( ',');
			}
			value = (String) fields.get("horizontal");
			if (value != null) {
				result.append( parseInt( styleName, groupName, "horizontal",  value, screenHeight )).append( ',');
			} else {
				result.append( defaultValue ).append( ',');
			}
		}
		result.append("\t// ").append(groupName);
		// add to the code:
		codeList.add( result.toString() );
	}


}
