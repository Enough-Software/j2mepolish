/*
 * Created on 16-Jan-2004 at 12:04:15.
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

import de.enough.polish.Device;
import de.enough.polish.util.StringUtil;

import de.enough.polish.BuildException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * <p>Represents a StyleSheet for a specific application.</p>
 *
 * <p>Copyright Enough Software 2004 - 2011</p>

 * @author Robert Virkus, robert@enough.de
 * @author Eugene Markov, fixed inheritance of styles
 */
public class StyleSheet {
	
	private static final HashMap KEYWORDS = new HashMap();
	static {
		KEYWORDS.put("font", Boolean.TRUE );
		KEYWORDS.put("background", Boolean.TRUE );
		KEYWORDS.put("border", Boolean.TRUE );
		KEYWORDS.put("extends", Boolean.TRUE );
		KEYWORDS.put("int", Boolean.TRUE );
		KEYWORDS.put("double", Boolean.TRUE );
		KEYWORDS.put("float", Boolean.TRUE );
		KEYWORDS.put("boolean", Boolean.TRUE );
		KEYWORDS.put("class", Boolean.TRUE );
		KEYWORDS.put("public", Boolean.TRUE );
		KEYWORDS.put("private", Boolean.TRUE );
		KEYWORDS.put("protected", Boolean.TRUE );
		KEYWORDS.put("final", Boolean.TRUE );
		KEYWORDS.put("static", Boolean.TRUE );
		KEYWORDS.put("transient", Boolean.TRUE );
	}
	// some CSS classes which are NOT dynamic classes (like p, a, or form) 
	private static final HashMap PSEUDO_CLASSES = new HashMap();
	static {
		PSEUDO_CLASSES.put("focused", Boolean.TRUE );
		PSEUDO_CLASSES.put("title", Boolean.TRUE );
		PSEUDO_CLASSES.put("default", Boolean.TRUE );
		PSEUDO_CLASSES.put("label", Boolean.TRUE );
		PSEUDO_CLASSES.put("menu", Boolean.TRUE );
		PSEUDO_CLASSES.put("menu1", Boolean.TRUE );
		PSEUDO_CLASSES.put("menu2", Boolean.TRUE );
		PSEUDO_CLASSES.put("menu3", Boolean.TRUE );
		PSEUDO_CLASSES.put("menuitem", Boolean.TRUE );
		PSEUDO_CLASSES.put("info", Boolean.TRUE );
		PSEUDO_CLASSES.put("tabbar", Boolean.TRUE );
		PSEUDO_CLASSES.put("activetab", Boolean.TRUE );
		PSEUDO_CLASSES.put("inactivetab", Boolean.TRUE );
		PSEUDO_CLASSES.put("tab", Boolean.TRUE );
		PSEUDO_CLASSES.put("menubar", Boolean.TRUE );
		PSEUDO_CLASSES.put("rightcommand", Boolean.TRUE );
		PSEUDO_CLASSES.put("centercommand", Boolean.TRUE );
		PSEUDO_CLASSES.put("middlecommand", Boolean.TRUE );
		PSEUDO_CLASSES.put("leftcommand", Boolean.TRUE );
		PSEUDO_CLASSES.put("command", Boolean.TRUE );
		PSEUDO_CLASSES.put("frame", Boolean.TRUE );
		PSEUDO_CLASSES.put("leftframe", Boolean.TRUE );
		PSEUDO_CLASSES.put("rightframe", Boolean.TRUE );
		PSEUDO_CLASSES.put("bottomframe", Boolean.TRUE );
		PSEUDO_CLASSES.put("topframe", Boolean.TRUE );
		PSEUDO_CLASSES.put("screeninfo", Boolean.TRUE );
		PSEUDO_CLASSES.put("messageheadline", Boolean.TRUE );
		PSEUDO_CLASSES.put("messagetext", Boolean.TRUE );
		PSEUDO_CLASSES.put("alert", Boolean.TRUE );
		PSEUDO_CLASSES.put("alertcontent", Boolean.TRUE );
		PSEUDO_CLASSES.put("scrollbar", Boolean.TRUE );
	}
	private final static CssBlock DEFAULT_STYLE = new CssBlock( 
			"default {"
			+ "font-color: black;"
			+ "font-face: system;"
			+ "font-style: plain;"
			+ "font-size: medium;"
			+ "}"
			);

	private HashMap stylesByName;
	private ArrayList styles;
	private HashMap backgrounds;
	private HashMap borders;
	private HashMap fonts;
	private HashMap colors;
	private HashMap usedStyles;
	
	private boolean isInitialised;
	private long lastModified;

	private boolean containsDynamicStyles;

	private HashMap cssPreprocessingSymbols;
	private HashMap cssAttributes;
	private Map attributesIds;

	private String mediaQueryCondition;

	private ArrayList mediaQueries;
	
	/**
	 * Creates a new empty style sheet
	 */
	public StyleSheet() {
		this.stylesByName = new HashMap();
		this.usedStyles = new HashMap();
		this.styles = new ArrayList();
		this.backgrounds = new HashMap();
		this.borders = new HashMap();
		this.fonts = new HashMap();
		this.colors = new HashMap();
	}
	
	/**
	 * Creates a new style sheet which is initialised with the given stylesheet.
	 * @param sheet the base style sheet
	 */
	public StyleSheet(StyleSheet sheet) {
		this();
		add( sheet );
		this.lastModified = sheet.lastModified;
		this.containsDynamicStyles = sheet.containsDynamicStyles;
	}
	

	/**
	 * Copies all styles from the parent style sheet.
	 * 
	 * @param parent the parent style sheet
	 */
	private void copyStyles( StyleSheet parent ) {
		HashMap source = parent.stylesByName;
		HashMap target = this.stylesByName;
		Set keys = source.keySet();
		for (Iterator iter = keys.iterator(); iter.hasNext();) {
			Object key = iter.next();
			Style original = (Style) source.get( key );
			Style existing = (Style) target.get( key );
			if (existing == null) {
				Style copy = new Style( original );
				target.put(key, copy );
				this.styles.add( copy );
			} else {
				existing.add( original );
			}
		}
	}

	/**
	 * Copies the contents of the given source HashMap to the target.
	 * 
	 * @param source the source map containing other HashMaps
	 * @param target the target 
	 */
	private void copyHashMap(HashMap source, HashMap target) {
		Set keys = source.keySet();
		for (Iterator iter = keys.iterator(); iter.hasNext();) {
			Object key = iter.next();
			HashMap original = (HashMap) source.get( key );
			HashMap existing = (HashMap) target.get( key );
			if (existing == null ) {
				HashMap copy = new HashMap( original );
				target.put(key, copy );
			} else {
				existing.putAll( original );
			}
		}
	}

	/**
	 * Retrieves the specified (usually unprocessed) style.
	 *   
	 * @param name the (not case-sensitive) name of the style
	 * @return the style.
	 */
	public Style getStyle( String name ) {
		name = name.toLowerCase();
		Style style = (Style) this.stylesByName.get( name );
		if (style == null && name.charAt(0) == '.') {
			style = (Style) this.stylesByName.get( name.substring(1) );
		}
		return style;
	}

	/**
	 * Retrieves the default style.
	 * 
	 * @return the default style
	 * @see #getStyle(String) - getStyle("default") yields the same result.
	 */
	public Style getDefaultStyle() {
		return (Style) this.stylesByName.get( "default" );
	}	
	/**
	 * Determines whether the specified style is known.
	 * 
	 * @param name the (not case-sensitive) name of the style.
	 * @return true when the specified style is known.
	 */
	public boolean isDefined( String name ) {
		if (name.charAt(0) == '.') {
			name = name.substring( 1 );
		}
		name = name.toLowerCase();
		if ("default".equals(name)) {
			// the default style is always defined!
			return true;
		}
		return this.stylesByName.get(name) != null;
	}
	
	/**
	 * Indicates that the given style is really used.
	 * 
	 * @param name the name of the style which is used.
	 */
	public void addUsedStyle( String name ) {
		this.usedStyles.put( name, Boolean.TRUE );
	}

	/**
	 * Retrieves all styles which are actually used by the application.
	 * 
	 * @return an array of all styles which are actually used.
	 */
	public String[] getUsedStyleNames() {
		return (String[]) this.usedStyles.keySet().toArray( new String[ this.usedStyles.size() ] );
	}

	/**
	 * Retrieves a list of all used or referenced styles.
	 * 
	 * @param defaultStyleNames an array with styles which are needed by default, e.g. "menu" in the fullscreen-mode.
	 * @param attributesManager the manager for CSS attributes
	 * @return an array of styles, can be empty but not null 
	 */
	public Style[] getUsedAndReferencedStyles(final String[] defaultStyleNames, CssAttributesManager attributesManager ) {
		final ArrayList stylesList = new ArrayList();
		final ArrayList finalStylesList = new ArrayList();
		final HashMap referencedStylesByName = new HashMap();
		
		// fill internal style list:
		// used styles:
		final String[] usedStyleNames = getUsedStyleNames();
		for (int i = 0; i < usedStyleNames.length; i++) {
			String styleName = usedStyleNames[i];
			Style style = getStyle( styleName );
			stylesList.add( style );
			markReferences(style, referencedStylesByName, attributesManager);
		}
		// default styles:
		for (int i = 0; i < defaultStyleNames.length; i++) {
			String styleName = defaultStyleNames[i];
			Style style = getStyle( styleName );
			if (style != null && !stylesList.contains(style)) {
				//System.out.println("adding default style " + style.getSelector());
				stylesList.add( style );
				markReferences(style, referencedStylesByName, attributesManager);
			}
		}
		// always include styles:
		Style[] allStyles = getAllStyles();
		for (int i = 0; i < allStyles.length; i++) {
			Style style = allStyles[i];
			if ( !stylesList.contains(style) && "true".equals(style.getAttributeValue( "always-include" ) ) ) 
			{
				stylesList.add( style );
				markReferences(style, referencedStylesByName, attributesManager);				
			}
		}

		// dynamic styles:
		if (this.containsDynamicStyles) {
			Style[] dynamicStyles = getDynamicStyles();
			for (int i = 0; i < dynamicStyles.length; i++) {
				Style style = dynamicStyles[i];
				if (!stylesList.contains(style)) {
					stylesList.add( style );
					markReferences(style, referencedStylesByName, attributesManager);
				}
			}
		}
		
		// add all referenced styles to stylesList:
		Collection referencedStyles = referencedStylesByName.values();
		for (Iterator iter = referencedStyles.iterator(); iter.hasNext();) {
			Style style = (Style) iter.next();
			if (!stylesList.contains(style)) {
				stylesList.add( style );
			}
		}
		// first batch: add styles which have no references themselves and are not referenced:
		//System.out.println("1. Batch");
		Style[] tmpStyles = (Style[]) stylesList.toArray( new Style[ stylesList.size() ]);
		for (int i = 0; i < tmpStyles.length; i++) {
			Style style = tmpStyles[i];
			if (!( style.isReferenced() || style.hasReferences() )) {
				//System.out.println( style.getSelector());
				finalStylesList.add( style );
				stylesList.remove( style );
			}
		}
		
		//System.out.println("2. Batch");
		// second batch: add styles which are referenced, but have no references themselves:
		tmpStyles = (Style[]) stylesList.toArray( new Style[ stylesList.size() ]);
		for (int i = 0; i < tmpStyles.length; i++) {
			Style style = tmpStyles[i];
			if ( style.isReferenced() && !style.hasReferences() ) {
				//System.out.println( style.getSelector());
				finalStylesList.add( style );
				stylesList.remove( style );
			}
		}
		
		//System.out.println("3. Batch");
		// third batch: add styles which are referenced and do have references themselves:
		tmpStyles = (Style[]) stylesList.toArray( new Style[ stylesList.size() ]);
		ArrayList sortedList = new ArrayList();
		for (int i = 0; i < tmpStyles.length; i++) {
			Style style = tmpStyles[i];
			if (style.isReferenced()) {
				//System.out.println( style.getSelector());
				sortedList.add( style );
				stylesList.remove( style );
			}
		}
		if (sortedList.size() > 1) {
			sortReferencedStyles( finalStylesList, sortedList );
		} else {
			finalStylesList.addAll( sortedList );
		}
		
		// fourth batch: add all remaining styles:
		finalStylesList.addAll( stylesList );
		/*System.out.println("4. Batch");
		tmpStyles = (Style[]) stylesList.toArray( new Style[ stylesList.size() ]);
		for (int i = 0; i < tmpStyles.length; i++) {
			Style style = tmpStyles[i];
			System.out.println( style.getSelector());
		}
		*/
		
		return (Style[]) finalStylesList.toArray( new Style[ finalStylesList.size() ]);
	}
	
	
	/**
	 * Sorts the styles references and adds them to the stored list.
	 * 
	 * @param storedStyles list of already stored styles
	 * @param unsortedStyles list with unsorted styles which have references to other styles.
	 */
	private void sortReferencedStyles(ArrayList storedStyles, ArrayList unsortedStyles) {
		// try to sort for max. unsortedStyles.size() times: 
		for (int i = unsortedStyles.size() - 1; i >= 0; i-- ) {
			Style[] unsortStyles = (Style[]) unsortedStyles.toArray( new Style[unsortedStyles.size()] );
			for (int j = 0; j < unsortStyles.length; j++) {
				Style style = unsortStyles[j];
				boolean addStyle = true;
				Style[] referencedStyles = style.getReferencedStyles();
				for (int k = 0; k < referencedStyles.length; k++) {
					Style referencedStyle = referencedStyles[k];
					if (! storedStyles.contains(referencedStyle) )  {
						addStyle = false;
						break;
					}
				}
				if (addStyle) {
					//System.out.println("adding sorted style: " + style.getSelector());
					storedStyles.add( style );
					unsortedStyles.remove( style );
				}
			}
			if (unsortedStyles.size() == 0) {
				return;
			}
		}
		if (unsortedStyles.size() != 0) {
			StringBuffer message = new StringBuffer();
			message.append( "Unable to resolve Style references: the following styles have circular references: " );
			Style[] unsortStyles = (Style[]) unsortedStyles.toArray( new Style[unsortedStyles.size()] );
			for (int j = 0; j < unsortStyles.length; j++) {
				Style style = unsortStyles[j];
				message.append( style.getSelector() );
				if (j != unsortStyles.length - 1) {
					message.append(", ");
				}
			}
			message.append(". Please check your \"polish.css\" file.");
			throw new BuildException( message.toString() );
		}
	}

	/**
	 * Marks styles as either referencing or referenced.
	 * 
	 * @param style the parent style
	 * @param referencedStylesByName a map in which referenced styles are marked
	 */
	private void markReferences(Style style, HashMap referencedStylesByName, CssAttributesManager attributesManager) {
		String[] references = style.getReferencedStyleNames(attributesManager);
		for (int i = 0; i < references.length; i++) {
			String referencedStyleName = references[i].toLowerCase();
			if (referencedStyleName.charAt(0) == '.') {
				referencedStyleName = referencedStyleName.substring( 1 );
			}
			Style referencedStyle = getStyle( referencedStyleName );
			if (referencedStyle != null) {
//				if (style.getSelector().equals(referencedStyleName)) {
//					System.out.println("style references itself: style=[" + style.getSelector() + "] references " + referencedStyleName );
//					continue;
//				}
				style.addReferencedStyle( referencedStyle );
				referencedStyle.setIsReferenced(true);
				Style reference = (Style) referencedStylesByName.get( referencedStyleName );
				if (reference == null) {
					// this style has not been referenced before
					referencedStylesByName.put( referencedStyleName, referencedStyle );
					markReferences( referencedStyle, referencedStylesByName, attributesManager );
				}
			}
		}
	}

	/**
	 * Adds a CSS block to this style sheet.
	 * A CSS block contains either a style or the colors, fonts,
	 * borders or backgrounds areas.
	 * 
	 * @param cssBlock the block containing CSS declarations
	 */
	public void addCssBlock( CssBlock cssBlock ) {
		this.isInitialised = false;
		String selector = cssBlock.getSelector().toLowerCase();
		String[] groupNames = cssBlock.getGroupNames();
		HashMap[] groups = new HashMap[ groupNames.length ];
		for (int i = 0; i < groups.length; i++) {
			groups[i] = cssBlock.getGroupDeclarations(groupNames[i]);
		}
		HashMap target = null;
		if ("colors".equals(selector)) {
			target = this.colors;
		} else if ("fonts".equals(selector)) {
			target = this.fonts;
		} else if ("backgrounds".equals(selector)) {
			target = this.backgrounds;
		} else if ("borders".equals(selector)) {
			target = this.borders;
		}
		if (target != null) {
			for (int i = 0; i < groups.length; i++) {
				String name = groupNames[i];
				HashMap group = groups[i]; 
				HashMap targetGroup = (HashMap) target.get( name );
				if (targetGroup == null) {
					target.put( name, group );
				} else {
					targetGroup.putAll( group );
				}
			}
		} else { // this is a style:
			String parent = null;
			int extendsPos = selector.indexOf(" extends ");
			int colonPos = selector.indexOf(':');
			if (extendsPos != -1) {
				if ( colonPos != -1) {
					throw new BuildException("Invalid CSS: the CSS style \"" + selector + "\" is a pseudo style that uses an extends clause. Please either use a pseudo style (:hover, etc) or use the extends clause. Pseudo styles inherit from their base styles automatically. Please adjust your polish.css design settings.");
				}
				parent = selector.substring( extendsPos + 9).trim();
				if (parent.charAt(0) == '.') {
					parent = parent.substring(1);
				}
				selector = selector.substring(0, extendsPos ).trim();
				if ("default".equals(selector ) ) {
					throw new BuildException( "Invalid CSS code: The style [default] must not extend any other style.");
				}
			}
			// check for the "[parentStyleName]:hover" "[parentStyleName]:pressed" etc syntax:
			if ( colonPos != -1) {
				// allow any number of levels of pseudo styles, e.g. myStyle:hover:visited:pressed
				String subName = null;
				Style parentStyle = null;
				while (colonPos != -1) {
					subName = selector.substring( colonPos + 1 );
					int subNameColonIndex = subName.indexOf(':');
					if (subNameColonIndex != -1) {
						subName = subName.substring(0, subNameColonIndex).trim();
					}
					parent = selector.substring(0, colonPos ).trim();
					if (parent.charAt(0) == '.') {
						parent = parent.substring(1);
					}
					
					parentStyle = getStyle( parent );
					if (parentStyle == null && this.mediaQueryCondition == null) {
						throw new BuildException("Invalid CSS: the :" + subName+  " CSS style \"" + selector + "\" needs to follow AFTER the referenced style definition. Please adjust your polish.css design settings.");
					} 
					// found parent style, now set the implicit focused-style attribute:
					String newStyleName = subName;
					if (subName.equals("hover")) {
						newStyleName = "focused";
					}
					
					selector = (selector.substring(0, colonPos ).trim() + newStyleName + selector.substring( colonPos + subName.length() + 1 ).trim()).trim();
					subName = newStyleName;
//					System.out.println("selector: " + selector );
//					System.out.println( "subname: [" + subName + "]");
					colonPos = selector.indexOf(':');
				}
				cssBlock.setSelector( selector );
				if (parentStyle != null) {
					HashMap referenceMap = new HashMap(1);
					referenceMap.put("style", parent + subName );
					parentStyle.addGroup(subName, referenceMap );
				}
				
			}
			boolean isDynamicStyle = false;
			// check if this style is dynamic:
			isDynamicStyle =  (selector.indexOf(' ') != -1)
						   || (selector.indexOf('\t') != -1)
						   || (selector.indexOf('>') != -1)
						   || (selector.indexOf('*') != -1);
			if (selector.charAt(0) == '.') {
				selector = selector.substring( 1 );
				if (PSEUDO_CLASSES.get(selector) != null) { 
					throw new BuildException("Invalid CSS code: The style [." + selector + "] uses a reserved name, please choose another one or remove the leading dot of the name.");
				}
			} else {
				// this could be a DYNAMIC style:
				if (PSEUDO_CLASSES.get(selector) == null && selector.indexOf(':') == -1) {
					isDynamicStyle = true;
				}
			}
			if (isDynamicStyle) {
				this.containsDynamicStyles = true;
				//System.out.println("project uses dynamic style: [" + selector + "]");				
			}
			// check for reserved names of the style-selector:
			if (KEYWORDS.get(selector) != null) {
				throw new BuildException( "Invalid CSS code: The style-selector [" + selector + "] uses a reserved keyword, please choose another name.");
			}
			if (selector.startsWith("@media ")) {
				addMediaQuery( selector.substring("@media ".length()).trim(), cssBlock );
			} else {
				// this is a traditional style:
				String styleName = StringUtil.replace( selector, '-', '_' );
				if (isDynamicStyle) {
					selector = StringUtil.replace( selector, ".", "");
					selector = StringUtil.replace( selector, '\t', ' ');
					selector = StringUtil.replace( selector, " > ", ">");
					selector = StringUtil.replace( selector, " > ", ">");
					selector = StringUtil.replace( selector, " * ", "*");
					selector = StringUtil.replace( selector, "  ", " ");
					styleName = StringUtil.replace( selector, ' ', '_');
					styleName = StringUtil.replace( styleName, ">", "__");
					styleName = StringUtil.replace( styleName, "*", "___");
				}
				
				// check style name for invalid characters:
				if ( (styleName.indexOf('.') != -1 )
					|| (styleName.indexOf('"') != -1 )
					|| (styleName.indexOf('\'') != -1 )
					|| (styleName.indexOf('*') != -1 )
					|| (styleName.indexOf('+') != -1 )
					|| (styleName.indexOf('-') != -1 )
					|| (styleName.indexOf('/') != -1 )
					|| (styleName.indexOf(':') != -1 )
					|| (styleName.indexOf('=') != -1 )
					|| (styleName.indexOf('|') != -1 )
					|| (styleName.indexOf('&') != -1 )
					|| (styleName.indexOf('~') != -1 )
					|| (styleName.indexOf('!') != -1 )
					|| (styleName.indexOf('^') != -1 )
					|| (styleName.indexOf('(') != -1 )
					|| (styleName.indexOf(')') != -1 )
					|| (styleName.indexOf('%') != -1 )
					|| (styleName.indexOf('?') != -1 )
					|| (styleName.indexOf('#') != -1 )
					|| (styleName.indexOf('$') != -1 )
					|| (styleName.indexOf('@') != -1 ) ) {
					throw new BuildException( "Invalid CSS code: The style-selector [" + selector + "] contains invalid characters, please use only alpha-numeric characters for style-names.");
				}
					
				
				Style style = (Style) this.stylesByName.get( styleName );
				if (style == null) {
					style = new Style( selector, styleName, isDynamicStyle, parent, cssBlock );
					this.styles.add( style );
					//System.out.println("added new style [" + style.getStyleName() + "].");
					this.stylesByName.put(  selector, style );
				} else {
					style.add( cssBlock );
				}
			}
		}
	}
	
	private void addMediaQuery(String query, CssBlock cssBlock) {
		System.out.println("adding media query " + query );
		String[] groups = cssBlock.getGroupNames();
		for (int i = 0; i < groups.length; i++) {
			String group = groups[i];
			HashMap map = cssBlock.getGroupDeclarations(group);
			System.out.println("style " + group + " has " + map.size() + " attributes ");
		}
		// TODO Besitzer implement addMediaQuery
		
	}

	/**
	 * Determines whether this sheet contains dynamic styles.
	 * Dynamic styles are set during runtime and can be used
	 * to make design changes without actually changing the source
	 * code at all.
	 * Since dynamic styles are much slower than static ones,
	 * a preprocessing-variable "polish.usesDynamicStyles" is
	 * set whenever dynamic styles are used.
	 * 
	 * @return true when this sheet contains dynamic styles.
	 */
	public boolean containsDynamicStyles() {
		return this.containsDynamicStyles;
	}
	
	/**
	 * Retrieves all defined styles of this sheet.
	 * 
	 * @return array of Style with all defined styles.
	 */
	public Style[] getAllStyles() {
		return (Style[]) this.styles.toArray( new Style[ this.styles.size() ]);
	}
	
	/**
	 * Removes the style of the given index
	 * @param index the index of the style
	 */
	public void removeStyle(int index)
	{
		this.styles.remove(index);
	}
	
	/**
	 * Sets the parents of the styles.
	 * This method is automatically called when the sourcecode 
	 * will be retrieved.
	 * 
	 * @throws BuildException when invalid heritances are found.
	 * @see #isInherited()
	 * @see #getSourceCode()
	 */
	public void oldInherit() {
		// create default-style when not explicitely defined:
		if (this.stylesByName.get("default") == null ) {
			addCssBlock(DEFAULT_STYLE);
		}
		Style[] allStyles = getAllStyles();
		for (int i = 0; i < allStyles.length; i++) {
			Style style = allStyles[i];
			//System.out.println("inheriting style [" + style.getSelector() + "].");
			checkInheritanceHierarchy( style );
			String parentName = style.getParentName();
			if (parentName != null) {
				Style parent = getStyle( parentName );
				if (parent == null) {
					throw new BuildException("Invalid CSS code: the style [" + style.getSelector() + "] extends the non-existing style [" + parentName + "].");
				}
				style.setParent( parent );
			}
		}
		this.isInitialised = true;
	}
	
	 public void inherit() {
//		 create default-style when not explicitly defined:
		if (this.stylesByName.get("default") == null )
		{
			addCssBlock(DEFAULT_STYLE);
		}
		HashSet set = new HashSet( 5 );
		Style[] allStyles = getAllStyles();
		for (int i = 0; i < allStyles.length; i++)
		{
			Style style = allStyles[i];
			inheritDo( style, set );
		}
		this.isInitialised = true;
	}


	private void inheritDo( Style style, HashSet set)
		{
		String parentName = style.getParentName();
		String currentName = style.getSelector();

		if (parentName == null)
		{
	//		 System.out.println("style [" + currentName + "] no parent.");
			return; // No parent.
		}
		else if ( set.contains( currentName ) )
		{
	//		 System.out.println("style [" + currentName + "] has been set.");
			return; // Has been set
		}
		else
		{
			checkInheritanceHierarchy( style );
			Style parent = getStyle( parentName );
	//		 System.out.println("inheriting style [" + currentName + "] from style [" + parentName + "].");
			if (parent == null)
			{
				throw new BuildException("Invalid CSS code: the style [" +
				currentName +
				"] extends the non-existing style [" +
				parentName + "].");
			}
			inheritDo( parent, set );
			style.setParent( parent );
			set.add( currentName );
			}
		}
	
	/**
	 * Checks whether the inheritance hierarchy is correct.
	 * An incorrect hierarchy is for example a loop definition,
	 * in which a parent-style extends a child-style:
	 * <pre>
	 * myStyle extends parent {
	 * 		font-face: monospace;
	 * }
	 * parent extends grandparent {
	 * 		font-size: large;
	 * 		font-face: system;
	 * }
	 * grandparent extends myStyle {
	 * 		background-color: white;
	 * }
	 * </pre>
	 * 
	 * 
	 * @param style the style whose inheritance hierarchy should be checked.
	 * @throws BuildException when a circle definition is found.
	 */
	private void checkInheritanceHierarchy(Style style) {
		HashMap parentNames = new HashMap( 5 );
		//System.out.println("checking inheritance of style " + style.getSelector());
		String originalSelector = style.getSelector();
		String parentName = style.getParentName(); 
		while (parentName != null) {
			//System.out.println( style.getSelector() + " extends " + parentName );
			String currentSelector = style.getSelector();
			parentName = parentName.toLowerCase();
			if (parentNames.get(parentName) == null) {
				// okay, this ancestor is not known yet.
				parentNames.put( parentName, Boolean.TRUE );
			} else {
				throw new BuildException("Invalid CSS code: Loop in inheritance found: The style [" + originalSelector + "] extends the child-style [" + parentName + "]. Please check your extends operator.");
			}
			style = getStyle( parentName );
			if (style == null) {
				throw new BuildException("Invalid CSS code: The style [" + currentSelector + "] extends the non-existing style [" + parentName + "]. Please define the style [" + parentName + "] or remove the extends operator.");
			}
			parentName = style.getParentName();
		}
	}

	/**
	 * Determines whether the styles have sorted their inheritance out.
	 * 
	 * @return true when the inheritance of the styles was already effected.
	 * @see #inherit()
	 */
	public boolean isInherited() {
		return this.isInitialised;
	}
	
	public String[] getSourceCode() {
		throw new BuildException("to be done.");
	}

	/**
	 * Adds another style sheet to this one.
	 * All properties in the given sheet will override this ones.
	 * 
	 * @param sheet the style sheet. The sheet will not be changed.
	 */
	public void add(StyleSheet sheet) {
		copyStyles( sheet );
		copyHashMap( sheet.backgrounds, this.backgrounds);
		copyHashMap( sheet.borders, this.borders);
		copyHashMap( sheet.fonts, this.fonts );
		copyHashMap( sheet.colors, this.colors);
	}

	/**
	 * Checks if a specific style is actually used.
	 * 
	 * @param name the name of the style
	 * @return true when the style is being used.
	 */
	public boolean isUsed(String name) {
		return this.usedStyles.get( name ) != null;
	}

	/**
	 * @return the time of the last modification 
	 */
	public long lastModified() {
		return this.lastModified;
	}
	
	public void setLastModified( long lastModified ) {
		this.lastModified = lastModified;
	}

	/**
	 * Retrieves all independent color definitions.
	 * 
	 * @return the list of colors which have been defined.
	 */
	public HashMap getColors() {
		return this.colors;
	}
	
	/**
	 * Gets all independently defined defined fonts.
	 * 
	 * @return a map with all defined fonts.
	 */
	public HashMap getFonts() {
		return this.fonts;
	}
	
	/**
	 * Gets all independently defined backgrounds.
	 * 
	 * @return all backgrounds in a map.
	 */
	public HashMap getBackgrounds() {
		return this.backgrounds;
	}
	
	/**
	 * Gets all independently defined borders.
	 * 
	 * @return all borders in a map.
	 */
	public HashMap getBorders() {
		return this.borders;
	}

	/**
	 * Gets the names of all defined styles.
	 * 
	 * @return the names of all defined styles.
	 */
	public String[] getStyleNames() {
		return (String[]) this.stylesByName.keySet().toArray( new String[ this.stylesByName.size()] );
	}

	/**
	 * Retrieves all dynamic styles.
	 *
	 * @return all dynamic styles.
	 */
	public Style[] getDynamicStyles() {
		ArrayList dynamicStyles = new ArrayList();
		Style[] allStyles = getAllStyles();
		for (int i = 0; i < allStyles.length; i++) {
			Style style = allStyles[i];
			if (style.isDynamic()) {
				dynamicStyles.add( style );
			}
		}
		return (Style[]) dynamicStyles.toArray( new Style[ dynamicStyles.size() ] );
	}

	/**
	 * Retrieves for a preprocessing-symbol for each defined CSS-attribute.
	 * When the attribute ticker-step is defined, the preprocessing-symbol
	 * "polish.css.ticker-step" will be defined.
	 * 
	 * @param device the current device
	 * @return a map containing all defined symbols as keys with each
	 *        having a Boolean.TRUE as value.
	 */
	public HashMap getCssPreprocessingSymbols( Device device ) {
		CssAttributesManager cssAttributesManager = CssAttributesManager.getInstance();
		if (this.cssPreprocessingSymbols == null) {
			HashMap symbols = new HashMap();
			HashMap attributesByName = new HashMap();
			if (this.mediaQueries != null) {
				symbols.put("polish.css.mediaquery", Boolean.TRUE);
			}
			Style[] myStyles = getAllStyles();
			for (int i = 0; i < myStyles.length; i++) {
				Style style = myStyles[i];
				symbols.put( "polish.css.style." + style.getStyleName(), Boolean.TRUE );
				String[] attributes = style.getDefinedAttributes( device );
				for (int j = 0; j < attributes.length; j++) {
					String attribute = attributes[j];
					//System.out.println("adding symbol polish.css." + attribute + " of style " + style.getSelector() );
					symbols.put( "polish.css." + attribute, Boolean.TRUE );
					attributesByName.put( attribute, Boolean.TRUE );
				}
				CssDeclarationBlock[] blocks = style.getDeclarationBlocksEndingWith("-animation");
				if (blocks.length > 0) {
					symbols.put( "polish.css.animations", Boolean.TRUE );
				}
				for (int j = 0; j < blocks.length; j++)
				{
					CssDeclarationBlock block = blocks[j];						
					String cssAttributeName = block.getBlockName().substring(0, block.getBlockName().length() - "-animation".length());
					CssAttribute cssAttribute = cssAttributesManager.getAttribute( cssAttributeName );
					if (cssAttribute == null) {
						int hyphenPos = cssAttributeName.indexOf('-');
						if (hyphenPos != -1) {
							String groupName = cssAttributeName.substring(0, hyphenPos);
							String type = style.getValue(groupName, "type");
							if (type == null) {
								type = "simple";
							}
							String alternativeCssAttributeName = groupName + "-" + type + cssAttributeName.substring(hyphenPos);
							cssAttribute = cssAttributesManager.getAttribute( alternativeCssAttributeName );
							if (cssAttribute != null) {
								cssAttributeName = alternativeCssAttributeName;
								block.setBlockName( cssAttributeName + "-animation");
							}
						}

					}
					if (cssAttribute != null) {
						symbols.put( "polish.css." + cssAttributeName, Boolean.TRUE );
					}
				}
			}
			this.cssPreprocessingSymbols = symbols;
			this.cssAttributes = attributesByName;
		}
		return this.cssPreprocessingSymbols;
	}
	
	/**
	 * Retrieves all defined css attributes in a map.
	 * 
	 * @param device the current device
	 * @return all defined css attributes in a map.
	 */
	public HashMap getCssAttributes( Device device ) {
		if (this.cssAttributes == null) {
			getCssPreprocessingSymbols( device );
		}
		return this.cssAttributes;
	}
	
	/**
	 * Determines whether the given attribute is defined at all.
	 * 
	 * @param name the name of the css-attribute, e.g. "background-color"
	 * @param device the current device
	 * @return true when the attribute is defined in this style-sheet.
	 */
	public boolean isCssAttributeDefined( String name, Device device ) {
		if (this.cssAttributes == null) {
			getCssPreprocessingSymbols( device );
		}
		return this.cssAttributes.get( name ) != null;
	}


	/**
	 * Sets the IDs for CSS attributes.
	 * 
	 * @param idsByAttribute a HashMap containing all IDs
	 */
	public void setAttributesIds(Map idsByAttribute) {
		this.attributesIds = idsByAttribute;
	}
	
	/**
	 * Retrieves the ID for the given attribute.
	 * 
	 * @param name the name of the attribute
	 * @return either the found ID or -1 when the attribute
	 * 	was not used in the preprocessed source code.
	 */
	public short getAttributeId( String name ) {
		/*
		System.out.println("all known attributes:");
		Object[] keys = this.attributesIds.keySet().toArray();
		for (int i = 0; i < keys.length; i++) {
			String key = (String) keys[i];
			System.out.println("[" + key + "]=" + this.attributesIds.get( key ));
		}
		*/
		Integer id = (Integer) this.attributesIds.get( name );
		if (id == null) {
			return -1;
		} else {
			return id.shortValue();
		}
	}

	/**
	 * Stores the condition for this set of styles
	 * @param condition the media query condition, compare http://www.w3.org/TR/css3-mediaqueries/
	 */
	public void setMediaQueryCondition(String condition) {
		this.mediaQueryCondition = condition;
	}

	/**
	 * Retrieves the condition for this set of styles
	 * @return the media query condition, compare http://www.w3.org/TR/css3-mediaqueries/
	 */
	public String getMediaQueryCondition() {
		return this.mediaQueryCondition;
	}

	/**
	 * Adds a media query to this style sheet
	 * @param mediaQuery the style sheet with conditional style
	 */
	public void addMediaQuery(StyleSheet mediaQuery) {
		if (this.mediaQueries == null) {
			this.mediaQueries = new ArrayList();
		}
		this.mediaQueries.add(mediaQuery);
	}

	/**
	 * Retrieves all media queries
	 * @return an array of all added media queries, null when none are registered;
	 */
	public StyleSheet[] getMediaQueries() {
		if (this.mediaQueries == null) {
			return null;
		}
		return (StyleSheet[]) this.mediaQueries.toArray( new StyleSheet[ this.mediaQueries.size() ] );
	}
}
