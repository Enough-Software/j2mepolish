//#condition polish.usePolishGui
/*
 * Created on 05-Jan-2004 at 20:41:52.
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
package de.enough.polish.ui;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Timer;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;

import de.enough.polish.ui.tasks.ImageTask;
import de.enough.polish.util.DeviceInfo;
import de.enough.polish.util.Locale;
import de.enough.polish.util.TextUtil;

/**
 * <p>Manages all defined styles of a specific project.</p>
 * <p>This class is actually pre-processed to get the styles specific for the project and the device.</p>
 *
 * @author Robert Virkus, robert@enough.de
 * <pre>
 * history
 *        05-Jan-2004 - rob creation
 * </pre>
 */
public final class StyleSheet {
	
	protected static Hashtable imagesByName;
	//#ifdef polish.images.backgroundLoad
		private static Hashtable scheduledImagesByName;
		//private static final Boolean TRUE = new Boolean( true );
		private static Timer timer;
	//#endif
	//#ifdef polish.LibraryBuild
		/** default style */
		public static Style defaultStyle = new Style( 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0x000000, Font.getDefaultFont(), null, null, null, null );
		/** default style for focused/hovered items */
		public static Style focusedStyle = new Style( 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0xFF0000, Font.getDefaultFont(), null, null, null, null );
		/** default style for labels */
		public static Style labelStyle = new Style( 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, Item.LAYOUT_NEWLINE_AFTER, 0x000000, Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL ), null, null, null, null );
		/** default style for the commands menu */
		public static Style menuStyle = new Style( 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, Item.LAYOUT_NEWLINE_AFTER, 0x000000, Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL ), null, null, null, null );
		private static Hashtable stylesByName = new Hashtable();
	//#endif


		private StyleSheet() {
			// disallow instantion
			//#if false
				// use Graphics so that the import is being kept:
				System.out.println(Graphics.LEFT);
			//#endif
		}

	
	
	// do not change the following line!
//$$IncludeStyleSheetDefinitionHere$$//
	/** Access to the currently shown J2ME Polish screen, if any */
	public static Screen currentScreen;	
	/** Access to the application's Display */
	public static Display display;
	/** Access to the currently running MIDlet */
	public static MIDlet midlet;
	/** Access to the AnimationThread responsible for animating all user interface components */
	public static AnimationThread animationThread;
	/** default OK command */
	//#ifdef polish.i18n.useDynamicTranslations
		public static Command OK_CMD = new Command( Locale.get("polish.command.ok"), Command.OK, 2 );
	//#elifdef polish.command.ok:defined
		//#= public static Command OK_CMD = new Command("${polish.command.ok}", Command.OK, 2 );
	//#else
		//# public static Command OK_CMD = new Command("OK", Command.OK, 2 );
	//#endif
	/** default CANCEL command */
	//#ifdef polish.i18n.useDynamicTranslations
		public static Command CANCEL_CMD = new Command(Locale.get("polish.command.cancel"), Command.CANCEL, 3 );
	//#elifdef polish.command.cancel:defined
		//#= public static Command CANCEL_CMD = new Command("${polish.command.cancel}", Command.CANCEL, 3 );
	//#else
		//# public static Command CANCEL_CMD = new Command("Cancel", Command.CANCEL, 3 );
	//#endif

	/**
	 * Retrieves the image with the given name.
	 * When the image has been cached before, it will be returned immediately.
	 * When it has not been cached before, it either will be loaded directly
	 * or in a background thread. This behaviour is set in the 
	 * <a href="../../../../definitions/polish_xml.html">polish.xml</a> file.
	 * 
	 * @param url the URL of the Image, e.g. "/background.png"
	 * @param parent the object which needs the image, when the image should be loaded
	 * 		   		in the background, the parent need to implement
	 * 				the ImageConsumer interface when it wants to be notified when
	 * 				the picture has been loaded.
	 * @param cache true when the image should be cached for later retrieval.
	 *              This costs RAM obviously, so you should decide carefully if
	 *              large images should be cached.
	 * @return the image when it either was cached or is loaded directly.
 	 *              When the should be loaded in the background, it will be later
	 *              set via the ImageConsumer.setImage()-method.
	 * @throws IOException when the image could not be loaded directly
	 * @see ImageConsumer#setImage(String, Image)
	 */
	public static Image getImage( String url, Object parent, boolean cache )
	throws IOException 
	{
		// check if the image has been cached before:
		//#if polish.allowImageCaching != false
			if ( imagesByName != null ) {
				Image image = (Image) imagesByName.get( url );
				if (image != null) {
					return image;
				}
			}
		//#endif
		//#if ! polish.images.backgroundLoad
			// when images should be loaded directly, try to do so now:
			//#ifdef polish.classes.ImageLoader:defined
				//#= Image image = ${ classname( polish.classes.ImageLoader ) }.loadImage( url );
			//#else
				Image image = null; 
				//#if polish.i18n.loadResources
					try {
				//#endif
						image = Image.createImage( url );
				//#if polish.i18n.loadResources
					} catch (IOException e) {
						if (Locale.LANGUAGE == null || Locale.LANGUAGE.length() == 0) {
							throw e;
						}
						String localeUrl = "/" + Locale.LANGUAGE + url;
						image = Image.createImage( localeUrl );
					}
				//#endif
			//#endif
			//#if polish.allowImageCaching != false
				if (cache) {
					if (imagesByName == null ) {
						imagesByName = new Hashtable();
					}
					imagesByName.put( url, image );
				}
			//#endif
			//# return image;
		//#else
			// when images should be loaded in the background, 
			// tell the background-thread to do so now:		
			if ( ! (parent instanceof ImageConsumer)) {
				//#debug error
				System.out.println("StyleSheet.getImage(..) needs an ImageConsumer when images are loaded in the background!");
				return null;
			}
			if (scheduledImagesByName == null ) {
				scheduledImagesByName = new Hashtable();
			}
			ImageQueue queue = (ImageQueue) scheduledImagesByName.get(url);
			if (queue != null) {
				// this image is already scheduled to load:
				queue.addConsumer((ImageConsumer) parent);
				return null;
			}
			scheduledImagesByName.put( url, new ImageQueue( (ImageConsumer) parent, cache ) );
			if (imagesByName == null ) {
				imagesByName = new Hashtable();
			}
			if (timer == null) {
				timer = new Timer();
			}
			ImageTask task = new ImageTask( url );
			timer.schedule( task, 10 );
			return null;
		//#endif
	}
	
	//#ifdef polish.images.backgroundLoad
	/**
	 * Notifies the GUI items which requested images about the successful loading of thoses images.
	 * 
	 * @param name the URL of the image
	 * @param image the image 
	 */
	public static void notifyImageConsumers( String name, Image image ) {
		ImageQueue queue = (ImageQueue) scheduledImagesByName.remove(name);
		if (queue != null) {
			if (queue.cache) {
				imagesByName.put( name, image );
			}
			queue.notifyConsumers(name, image);
			if (true) {
				return;
			}
			if (currentScreen != null) {
				currentScreen.repaint();
			}
		}
	}
	//#endif
	
	/**
	 * Gets the style with the specified name.
	 * 
	 * @param name the name of the style
	 * @return the specified style or null when no style with the given 
	 * 	       name has been defined.
	 */
	public static Style getStyle( String name ) {
		Style style =  (Style) stylesByName.get( name );
		if (style == null) {
			style =  (Style) stylesByName.get( name.toLowerCase() );
		}
		return style;
	}
	
	/**
	 * Retrieves all registered styles in a Hashtable.
	 * 
	 * @return all registered styles in a Hashtable.
	 */
	public static Hashtable getStyles()
	{
		return stylesByName;
	}
	
	//#ifdef polish.useDynamicStyles
	/**
	 * Retrieves the style for the given item.
	 * This function is only available when the &lt;buildSetting&gt;-attribute
	 * [useDynamicStyles] is enabled.
	 * This function allows to set styles without actually using the preprocessing-
	 * directive //#style. Beware that this dynamic style retrieval is not as performant
	 * as the direct-style-setting with the //#style preprocessing directive.
	 *  
	 * @param item the item for which the style should be retrieved
	 * @return the appropriate style. When no specific style is found,
	 *         the default style is returned.
	 */
	public static Style getStyle( Item item ) {
		if (item.screen == null) {
			//#debug info
			System.out.println("unable to retrieve style for item [" + item.getClass().getName() + "] without screen.");
			return defaultStyle;
		}
		String itemCssSelector = item.cssSelector;
		String screenCssSelector = item.screen.cssSelector;
		Style style = null;
		String fullStyleName;
		StringBuffer buffer = new StringBuffer();
		buffer.append( screenCssSelector );
		if (item.parent == null) {
			//#debug
			System.out.println("item.parent == null");
			buffer.append('>').append( itemCssSelector );
			fullStyleName = buffer.toString();
			style = (Style) stylesByName.get( fullStyleName );
			if (style != null) {
				return style;
			}
			style = (Style) stylesByName.get( screenCssSelector + " " + itemCssSelector );
		} else if (item.parent.parent == null) {
			//#debug
			System.out.println("Item has one parent.");
			// this item is propably in a form or list,
			// typical hierarchy is for example "form>container>p"                                                 
			Item parent = item.parent;
			String parentCssSelector = parent.cssSelector;
			if (parentCssSelector == null) {
				parentCssSelector = parent.createCssSelector();
			}
			//#debug
			System.out.println( parent.getClass().getName() + "-css-selector: " + parentCssSelector );
			buffer.append('>').append( parentCssSelector )
				  .append('>').append( itemCssSelector );
			fullStyleName = buffer.toString();
			//#debug
			System.out.println("trying " + fullStyleName);
			style = (Style) stylesByName.get( fullStyleName );
			if (style != null) {
				return style;
			}
			// 1. try: "screen item":
			String styleName = screenCssSelector + " " + itemCssSelector;
			//#debug
			System.out.println("trying " + styleName);
			style = (Style) stylesByName.get( styleName );
			if (style == null) {
				// 2. try: "screen*item":
				styleName = screenCssSelector + "*" + itemCssSelector;
				//#debug
				System.out.println("trying " + styleName);
				style = (Style) stylesByName.get( styleName );
				if (style == null) {
					// 3. try: "parent>item"
					styleName = parentCssSelector + ">" + itemCssSelector;
					//#debug
					System.out.println("trying " + styleName);
					style = (Style) stylesByName.get( styleName );
					if (style == null) {
						// 4. try: "parent item"
						styleName = parentCssSelector + " " + itemCssSelector;
						//#debug
						System.out.println("trying " + styleName);
						style = (Style) stylesByName.get( styleName );
					}
				}
			}
			//#debug
			System.out.println("found style: " + (style != null));
		} else {
			//#debug
			System.out.println("so far unable to set style: complex item setup");
			// this is a tiny bit more complicated....
			fullStyleName = null;
		}
		if (style == null) {
			// try just the item:
			//#debug
			System.out.println("trying " + itemCssSelector);
			if (itemCssSelector != null) {
				style = (Style) stylesByName.get( itemCssSelector );
			}
			if (style == null) {
				//#debug
				System.out.println("Using default style for item " + item.getClass().getName() );
				style = defaultStyle;
			}
			//#ifdef polish.debug.debug
				else {
					//#debug
					System.out.println("Found style " + itemCssSelector );
				}
			//#endif
		}
		if ( fullStyleName != null && style != null ) {
			stylesByName.put( fullStyleName, style );
		}
		return style;
	}
	//#endif

	//#ifdef polish.useDynamicStyles
	/**
	 * Retrieves a dynamic style for the given screen.
	 * 
	 * @param screen the screen for which a style should be retrieved
	 * @return the style for the given screen.
	 */
	public static Style getStyle(Screen screen) {
		Style style = (Style) stylesByName.get( screen.cssSelector );
		if (style == null) {
			return defaultStyle;
		}
		return style;
	}		
	//#endif
	
	//#if !polish.css.mediaquery
	/**
	 * placeholder for showNotify method which is added when using media queries
	 */
	public static void showNotify() {
		// placeholder for showNotify method which is added when using media queries
		
	}
	//#endif
	
	//#if polish.css.mediaquery
		/**
		 * Adds a media query to this set of styles if the condition is fulfilled.
		 * This method is only accessible when the preprocessing symbol <code>polish.css.mediaquery</code> is true.
		 * 
		 * @param condition the condition, compare http://www.w3.org/TR/css3-mediaqueries/
		 * @param styles the styles that should be modified by this condition
		 */
		public static void addMediaQuery( String condition, Style[] styles ) {
			if (isMediaQueryFulfilled(condition)) {
				for (int i = 0; i < styles.length; i++) {
					Style style = styles[i];
					Style parent = getStyle( style.name );
					if (parent != null) {
						copyStyleSettings(style.name, 1, style, parent, null);
					}
				}
			}
		}
	//#endif

	//#if polish.css.mediaquery
		private static void copyStyleSettings(String name, int level, Style source, Style target, Style parentOfTarget) {
//			System.out.println(level + ": copy " + name + " to " + target.name + ", :hover=" + target.getObjectProperty(1));
			if (level < 4) {
				Object[] values = target.getRawAttributeValues();
				if (values != null) {
					for (int i = 0; i < values.length; i++) {
						Object value = values[i];
						if (value != parentOfTarget && value instanceof Style) {
							Style substyle = (Style) value;
							if (substyle.name != null && substyle.name.startsWith(name)) {
//								System.out.println("found substyle " + target.getRawAttributeKeys()[i]);
								copyStyleSettings(name, level + 1, source, substyle, target);
							}
						}
					}
				}
			}
			if (source.background != null) {
				target.background = source.background;
			}
			if (source.border != null) {
				target.border = source.border;
			}
			short[] keys = source.getRawAttributeKeys();
			if (keys != null) {
				Object[] values = source.getRawAttributeValues();
				for (int j = 0; j < values.length; j++) {
					target.addAttribute(keys[j], values[j]);
				}
			}
		}
	//#endif
		
	//#if polish.css.mediaquery
		private static boolean isMediaQueryFulfilled(String condition) {
			String[] conditions = TextUtil.split(condition, ',');
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < conditions.length; i++) {
				condition = conditions[i];
				try {
					if (isMediaQueryConditionFulfilled(condition, buffer)) {
						return true;
					}
				} catch (Exception e) {
					//#debug error
					System.out.println("Unable to parse media query condition " + condition + e);
				}
			}
			return false;
		}
		private static boolean isMediaQueryConditionFulfilled(String condition, StringBuffer buffer) {
			buffer.delete(0, buffer.length());
			int screenWidth = Display.getScreenWidth();
			int screenHeight = Display.getScreenHeight();
			
			char c;
			boolean notFound = false;
			boolean isFirstPart = true;
			String part;
			boolean result = true;
			boolean isMinFound = false;
			boolean isMaxFound = false;
			String lastFeature = null;
			boolean isExpectingFeatureName = false;
			boolean isExpectingValue = false;
			int length = condition.length()-1;
			for (int i=0; i<=length;i++) {
				c = condition.charAt(i);
				if (c != ' ' && c != ')') {
					if (c == '(') {
						isExpectingFeatureName = true;
					} else if (c == ':') {
						isExpectingValue = true;
					} else {
						buffer.append(c);
					}
					if (i<length) {
						continue;
					}
				}
				if (buffer.length() > 0) {
					// part ending here:
					part = buffer.toString().toLowerCase();
					//System.out.println("part=" + part + ", expectingFeatureName=" + isExpectingFeatureName + ", isExpectingValue=" + isExpectingValue);
					buffer.delete(0, buffer.length());
					if (isFirstPart && "only".equals(part)) {
						continue;
					}
					if (isFirstPart && "not".equals(part)) {
						notFound = true;
						continue;
					}
					if (isFirstPart) {
						isFirstPart = false;
						if (!isExpectingFeatureName) {
							if ("touchscreen".equals(part)) {
								result = DeviceInfo.hasPointerEvents();
							} else if (!("all".equals(part) || "screen".equals(part))) {
								// this is a unsupported media type (otherwise a parentheses would have been necessary):
								result = false;
							}
							continue;
						}
					}
					if ("and".equals(part)) {
						// now we (should) have this information for evaluating the last term:
						isExpectingValue = false;
						isMinFound = false;
						isMaxFound = false;
						continue;
					}
					if (isExpectingFeatureName) {
						// either this is an unknown media type or this is a feature that needs to be supported:
						if (part.startsWith("min-")) {
							isMinFound = true;
							part = part.substring(4);//"min-".length());
						} else if (part.startsWith("max-")) {
							isMaxFound = true;
							part = part.substring(4);//"max-".length());
						}
						lastFeature = part;
						isExpectingFeatureName = false;
						if (!isExpectingValue) {
							// this is a feature without value:
							if ("color".equals(lastFeature)) {
								result = (Display.getInstance().numColors() > 2);
							} else {
								result = false;
							}
						}
					} else if (isExpectingValue){
						int pixelValue = -1;
						if (part.endsWith("px")) {
							pixelValue = Integer.parseInt(part.substring(0, part.length() - 2));
						}
						if ("width".equals(lastFeature) || "device-width".equals(lastFeature)) {
							if (isMinFound) {
								result = (screenWidth >= pixelValue);
							} else if (isMaxFound) {
								result = (screenWidth <= pixelValue);
							} else {
								result = (screenWidth == pixelValue);
							}
						} else if ("height".equals(lastFeature) || "device-height".equals(lastFeature)) {
							//TODO when using height we should use the viewport height which is less than the screenheight in fullscreen mode...
							if (isMinFound) {
								result = (screenHeight >= pixelValue);
							} else if (isMaxFound) {
								result = (screenHeight <= pixelValue);
							} else {
								result = (screenHeight == pixelValue);
							}
						} else if ("orientation".equals(lastFeature)) {
							if ("landscape".equals(part)) {
								result = screenWidth > screenHeight;
							} else if ("portrait".equals(part)) {
								result = screenHeight >= screenWidth;
							}
						} else if ("aspect-ratio".equals(lastFeature) || "device-aspect-ratio".equals(lastFeature)) {
							int splitPos = part.indexOf('/');
							if (splitPos == -1) {
								result = false;
							} else {
								int w = Integer.parseInt(part.substring(0, splitPos));
								int h = Integer.parseInt(part.substring(splitPos+1));
								int given = (screenWidth << 8) / screenHeight;
								int expected = (w << 8) / h;
								if (isMinFound) {
									result = (given >= expected);
								} else if (isMaxFound) {
									result = (given <= expected);
								} else {
									result = (given == expected);
								}
							}
						} else if ("color".equals(lastFeature)) {
							int given = Display.getInstance().numColors();
							int expected = 2 ^ Integer.parseInt(part);
							if (isMinFound) {
								result = (given >= expected);
							} else if (isMaxFound) {
								result = (given <= expected);
							} else {
								result = (given == expected);
							}
						} else if ("vendor".equals(lastFeature)){
							String given = DeviceInfo.getVendorName();
							if (given == null) {
								given = "unknown";
							} else {
								given = given.toLowerCase();
							}
							result = (part.equals(given));
						}
					//#if polish.debug.debug
					} else {
						//#debug
						System.out.println("Warning: encountered invalid state in media query at term " + part);
					//#endif
					}
					if (!result) {
						break;
					}
				} // found space
			} // for each character
			if (notFound) {
				result = !result;
			}
			return result;
		}
	//#endif
	
	/**
	 * Releases all (memory intensive) resources such as images or RGB arrays of this style sheet.
	 */
	public static void releaseResources() {
		//#if polish.allowImageCaching != false
		if (imagesByName != null) {
			imagesByName.clear();
		}
		//#endif
		//#ifdef polish.useDynamicStyles
			Enumeration enumeration = stylesByName.elements();
			while (enumeration.hasMoreElements()) {
				Style style = (Style) enumeration.nextElement();
				style.releaseResources();
			}
		//#endif
		//#ifdef polish.StyleSheet.releaseResources:defined
			//#include ${polish.StyleSheet.releaseResources}
		//#endif
	}


	public static Style[] getDynamicStyles() {
		//#if polish.inSkinEditor == true
			//# return (Style[]) dynamicStylesList.toArray( new Style[ dynamicStylesList.size() ] );
			//# }
		//#else
//			java.util.Enumeration enumeration = dynamicStylesByName.elements();
//			Style[] styles = new Style[ dynamicStylesByName.size() ];
//			for (int i=0; i<styles.length; i++) {
//				styles[i] = (Style) enumeration.nextElement();
//			}
//			return styles;
			return new Style[]{ defaultStyle, focusedStyle };
		//#endif
	}


	
	
//#ifdef polish.StyleSheet.additionalMethods:defined
	//#include ${polish.StyleSheet.additionalMethods}
//#endif

}
