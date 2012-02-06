//#condition polish.usePolishGui
/*
 * Copyright (c) 2010 Robert Virkus / Enough Software
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

package de.enough.polish.ui.texteffects;

import java.io.IOException;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.midlet.MIDlet;

import de.enough.polish.browser.css.CssInterpreter;
import de.enough.polish.io.StringReader;
import de.enough.polish.ui.Command;
import de.enough.polish.ui.Dimension;
import de.enough.polish.ui.ImageItem;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemCommandListener;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;
import de.enough.polish.ui.TextEffect;
import de.enough.polish.ui.containerviews.Midp2ContainerView;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.TextUtil;
import de.enough.polish.util.WrappedText;
import de.enough.polish.xml.SimplePullParser;
import de.enough.polish.xml.XmlPullParser;

/**
 * Allows to use simple HTML markup for the design of the text.
 * Usage in polish.css:
 * <pre>
 * .myText {
 * 	 text-effect: html;
 * }
 * .redText {
 *   always-include: true;
 * 	 font-color: red;
 * }
 * </pre>
 * 
 * Usage in Java:
 * //#style myText
 * StringItem item = new StringItem(null, "hello <div class=\"redText\">red, red</div> <b>world</b>
 * @author Robert Virkus
 *
 */
public class HtmlTextEffect 
extends TextEffect
implements ItemCommandListener
{

	private static final String ATTRIBUTE_HREF = "href";
	private static HtmlTextParser globalParser;
	private static MIDlet midlet;
	private static Command cmdOpenWebsite;
	private static Command cmdOpenMailto;
	
	private HtmlTextParser parser;
	private HtmlTextContainerView containerView;
	private transient Item[] textItems;
	private WrappedText storedWrappedText;
	private StringItem parentStringItem;


	/**
	 * Sets the parser for all instances of the HtmlTextEffect
	 * @param parser the parser
	 */
	public static void setGlobalParser( HtmlTextParser parser ) {
		HtmlTextEffect.globalParser = parser;
	}
	
	/**
	 * Sets a midlet, so that email and web addresses can be resolved by opening them in the native browser
	 * @param midlet the midlet
	 * @param cmdOpenWebsite the command for opening websites
	 * @param cmdOpenMailto the command for opening mailto/email addresses
	 */
	public static void setMidlet(MIDlet midlet, Command cmdOpenWebsite, Command cmdOpenMailto) {
		HtmlTextEffect.midlet = midlet;
		HtmlTextEffect.cmdOpenWebsite = cmdOpenWebsite;
		HtmlTextEffect.cmdOpenMailto = cmdOpenMailto;
	}

	
	/**
	 * Creates a new HTML text effect
	 */
	public HtmlTextEffect() {
		// use style for further initialization, if required.
		this.isTextSensitive = true;
	}
	
	/**
	 * Sets the parser for this instance of the HtmlTextEffect
	 * @param parser the parser
	 */
	public void setParser( HtmlTextParser parser) {
		this.parser = parser;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawStrings(de.enough.polish.util.WrappedText, int, int, int, int, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void drawStrings(WrappedText textLines, int textColor, int x, int y,
			int leftBorder, int rightBorder, int lineHeight, int maxWidth,
			int layout, Graphics g) 
	{
		if (this.containerView == null) {
			super.drawStrings(textLines, textColor, x, y, leftBorder, rightBorder,
					lineHeight, maxWidth, layout, g);
		} else {
			//#if polish.Bugs.needsBottomOrientiationForStringDrawing
				y -= getFontHeight();
			//#endif
			if ((layout & Item.LAYOUT_CENTER) == Item.LAYOUT_CENTER) {
				x += ((rightBorder - leftBorder) - this.containerView.getContentWidth()) / 2;
			} else if ((layout & Item.LAYOUT_CENTER) == Item.LAYOUT_RIGHT) {
				x += ((rightBorder - leftBorder) - this.containerView.getContentWidth());				
			}
			//System.out.println("painting: y=" + y + ", x=" + x + ", left=" + leftBorder + " for " + this );
			//if (x + this.containerView.getContentWidth() > rightBorder) {
				x = leftBorder;
			//}
			this.containerView.paintContent( this.textItems, x, y, leftBorder, rightBorder, g );			
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#wrap(de.enough.polish.ui.StringItem, java.lang.String, int, javax.microedition.lcdui.Font, int, int, int, java.lang.String, int, de.enough.polish.util.WrappedText)
	 */
	public void wrap(StringItem parent, String htmlText, int textColor, Font meFont,
			int firstLineWidth, int lineWidth, int maxLines,
			String maxLinesAppendix, int maxLinesAppendixPosition,
			WrappedText wrappedText) 
	{
		this.parentStringItem = parent;
		this.storedWrappedText = wrappedText;
		ArrayList childList = new ArrayList();
		Style baseStyle = this.style.clone(true);
		baseStyle.removeAttribute("text-effect");
		Dimension padding = new Dimension(0);
		baseStyle.addAttribute("padding-left", padding);
		baseStyle.addAttribute("padding-right", padding);
		baseStyle.addAttribute("padding-top", padding);
		baseStyle.addAttribute("padding-bottom", padding);
		baseStyle.layout = (baseStyle.layout & (~(Item.LAYOUT_EXPAND | Item.LAYOUT_VEXPAND | Item.LAYOUT_SHRINK | Item.LAYOUT_VSHRINK)) );
		baseStyle.background = null;
		baseStyle.border = null;
		
		try {
			XmlPullParser xmlReader = new XmlPullParser(new StringReader(htmlText), false );
			xmlReader.relaxed = true;
			parse( xmlReader, baseStyle, childList );
		} catch (IOException e) {
			//#debug error
			System.out.println("Unable to parse text " + htmlText + e );
			super.wrap(parent, htmlText, textColor, meFont, firstLineWidth, lineWidth, maxLines,
					maxLinesAppendix, maxLinesAppendixPosition, wrappedText);
			return;
		}

		
		Item[] items = (Item[]) childList.toArray( new Item[childList.size()]);
		for (int i = 0; i < items.length; i++) {
			Item item = items[i];
			//System.out.println( i + ": [" + ((StringItem)item).getText() + "]");
			item.getItemWidth( lineWidth, lineWidth, -1);
		}
		HtmlTextContainerView view = new HtmlTextContainerView(wrappedText);
		wrappedText.clear();
		view.initContent(items, firstLineWidth, lineWidth, -1);
		if (view.isInitializationError && (firstLineWidth < lineWidth)) {
			wrappedText.clear();
			view.resetInitializationError();
			Item item = items[0];
			item.setInitialized(false);
			item.getItemWidth(lineWidth, lineWidth, -1);
			//((StringItem)item).setTextInitializationRequired(true);
			view.initContent(items, lineWidth, lineWidth, -1);
		}
		this.textItems = items;
		this.containerView = view;
		//System.out.println("WRAPPING RESULT: items=" + items.length + ", view=" + view.getContentWidth() + " X " + view.getContentHeight());
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#calculateLinesHeight(de.enough.polish.util.WrappedText, int, int)
	 */
	public int calculateLinesHeight(WrappedText lines, int lineHeight,
			int paddingVertical) 
	{
		if (this.containerView != null) {
			return this.containerView.getContentHeight();
		}
		return super.calculateLinesHeight(lines, lineHeight, paddingVertical);
	}

	private void parse(XmlPullParser parser, Style nodeStyle, ArrayList childList) 
	{
		while (parser.next() != SimplePullParser.END_DOCUMENT)
		{
			int type = parser.getType();
			if (type == SimplePullParser.START_TAG)
			{
				String name = parser.getName().toLowerCase();
				boolean isNameResolved = false;
				
				if (this.parser != null) {
					isNameResolved = this.parser.parseTag(name, parser, nodeStyle, childList);
				}
				if (!isNameResolved && globalParser != null) {
					isNameResolved = globalParser.parseTag(name, parser, nodeStyle, childList);
				}
				int addedFontStyle = -1;
				if (!isNameResolved) {
					if ("b".equals(name)) {
						addedFontStyle = Font.STYLE_BOLD;
						isNameResolved = true;
					} else if ("i".equals(name)) {
						addedFontStyle = Font.STYLE_ITALIC;
						isNameResolved = true;
					} else if (midlet != null) {
						if ("a".equals(name)) {
							addedFontStyle = Font.STYLE_UNDERLINED;
							String href = parser.getAttributeValue(ATTRIBUTE_HREF);
							if (href != null) {
								Command cmd;
								if (href.startsWith("mailto:")) {
									cmd = cmdOpenMailto;
								} else {
									cmd = cmdOpenWebsite;
								}
								parser.next();
								String text = parser.getText();
								Style nextNodeStyle = nodeStyle.clone(true);
								Integer fontStyle = nextNodeStyle.getIntProperty("font-style");
								nextNodeStyle.addAttribute("font-style", addToFontStyle( addedFontStyle, fontStyle ) );
								StringItem item = new StringItem(null, text, nextNodeStyle);
								childList.add(item);
								if (this.parentStringItem != null) {
									item = this.parentStringItem;
								}
								item.setAttribute(ATTRIBUTE_HREF, href);
								item.setDefaultCommand(cmd);
								item.setItemCommandListener(this);
								addedFontStyle = -1;
							}
						}
					}
					
				}
				String styleName = parser.getAttributeValue("class");
				if (styleName == null) {
					styleName = parser.getAttributeValue("id");
				}
				Style nextNodeStyle = null;
				if (styleName != null) {
					nextNodeStyle = StyleSheet.getStyle(styleName);
					if (nextNodeStyle != null) {
						nextNodeStyle.removeAttribute("padding-left");
						nextNodeStyle.removeAttribute("padding-right");
						nextNodeStyle.removeAttribute("padding-bottom");
						nextNodeStyle.removeAttribute("padding-top");
					}
				} else {
					String styleDefinition = parser.getAttributeValue("style");
					if (styleDefinition != null) {
						try {
							nextNodeStyle = CssInterpreter.parseStyle(styleDefinition);
							if (nextNodeStyle != null) {
								nextNodeStyle.extendStyle(nodeStyle);
							}
						} catch (IOException e) {
							//#debug error
							System.out.println("Unable to parse CSS style definition \"" + styleDefinition + "\"" + e );
						}
					}
				}
				if (nextNodeStyle != null) {
					nextNodeStyle.addAttribute("padding", new Dimension(0));
				}
				if (nextNodeStyle == null) {
					nextNodeStyle = nodeStyle;
				}
				if (addedFontStyle != -1) {
					nextNodeStyle = nextNodeStyle.clone(true);
					Integer fontStyle = nextNodeStyle.getIntProperty("font-style");
					nextNodeStyle.addAttribute("font-style", addToFontStyle( addedFontStyle, fontStyle ) );
				}
				if ((!isNameResolved) && ("img".equals(name))) {
					String src = parser.getAttributeValue("src");
					if (src != null) {
						childList.add( new ImageItem(null, src, nodeStyle) );
					}
				}
				parse(parser, nextNodeStyle, childList);
			} else if (type == SimplePullParser.TEXT) {
				String text = parser.getText();
				addText( text, nodeStyle, childList );
			} else if (type == SimplePullParser.END_TAG) {
				return;
			}
		} 
	}

	private Object addToFontStyle(int styleSetting, Integer fontStyle) {
		if (fontStyle == null) {
			return new Integer( styleSetting );
		}
		return new Integer( fontStyle.intValue() | styleSetting );
	}


	private void addText(String text, Style textStyle, ArrayList childList) {
		
		String[] texts = TextUtil.split(text, ' ');
		for (int i = 0; i < texts.length; i++) {
			String chunk = texts[i];
			if (i < texts.length - 1) {
				chunk += " ";
			}
			childList.add( new StringItem( null, chunk, textStyle));
		}
		
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawString(java.lang.String, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void drawString(String text, int textColor, int x, int y,
			int anchor, Graphics g) 
	{
		// just in case no font is defined:
		g.drawString( text, x, y, anchor );
		

	}


	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		super.setStyle(style);
	}


	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#getFontHeight()
	 */
	public int getFontHeight() {
		if (this.containerView == null) {
			return super.getFontHeight();
		} else {
			return this.containerView.getContentHeight() / this.storedWrappedText.size();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#stringWidth(java.lang.String)
	 */
	public int stringWidth(String str) {
		if (this.containerView == null) {
			wrap( (StringItem)null, str, 0, getFont(), Integer.MAX_VALUE, Integer.MAX_VALUE, TextUtil.MAXLINES_UNLIMITED, null, TextUtil.MAXLINES_APPENDIX_POSITION_AFTER, new WrappedText() );
		} 
		return this.containerView.getContentWidth();
	}
	

	public void commandAction(Command cmd, Item item) {
		String href = (String) item.getAttribute(ATTRIBUTE_HREF);
		if ((href != null) && (midlet != null) && (cmd == cmdOpenWebsite || cmd == cmdOpenMailto)) {
			boolean shouldExit = false;
			try {
				shouldExit = midlet.platformRequest(href);
			} catch (ConnectionNotFoundException e) {
				//#debug error
				System.out.println("unable to do a platformRequest for " + href + e);
			}
			if (shouldExit) {
				midlet.notifyDestroyed();
			}
		}
	}
	
	private static class HtmlTextContainerView extends Midp2ContainerView {
		private final WrappedText wrappedText;
		private int widthDifference;
		private boolean isFirstLine;
		private boolean isInitializationError;

		/**
		 * Creates a new container view
		 * @param wrappedText the wrapped text in which line breaks are stored
		 */
		public HtmlTextContainerView( WrappedText wrappedText ) {
			this.wrappedText = wrappedText;
		}
		
		

		/* (non-Javadoc)
		 * @see de.enough.polish.ui.containerviews.Midp2ContainerView#initContent(de.enough.polish.ui.Item[], int, int, int)
		 */
		public void initContent(Item[] items, int firstLineWidth,
				int availWidth, int availHeight) 
		{
			this.widthDifference = availWidth - firstLineWidth;
			this.isFirstLine = (this.widthDifference != 0);
			super.initContent(items, firstLineWidth, availWidth, availHeight);
		}



		/* (non-Javadoc)
		 * @see de.enough.polish.ui.containerviews.Midp2ContainerView#addLineBreak(de.enough.polish.ui.Item[], int, int, int, int, int)
		 */
		protected void addLineBreak(Item[] items, int currentRowStartIndex,
				int currentRowEndIndex, int currentRowWidth,
				int currentRowHeight, int availWidth) 
		{
			if (currentRowEndIndex == 0 && items.length > 1) {
				StringItem endOfLineItem = (StringItem) items[currentRowEndIndex];
				if (endOfLineItem.getNumberOfLines() > 1) {
					endOfLineItem.setInitialized(false);
					this.isInitializationError = true;
				}
			}
			super.addLineBreak(items, currentRowStartIndex, currentRowEndIndex,
					currentRowWidth, currentRowHeight, availWidth);
			if (this.isFirstLine) {
				this.isFirstLine = false;
				//System.out.println("adding " + this.widthDifference + " to xOffset of items...");
				for (int i=currentRowStartIndex; i <= currentRowEndIndex; i++) {
					Item item = items[i];
					item.relativeX += this.widthDifference;
				}
			}
			this.wrappedText.addLine("", currentRowWidth);
			if (currentRowStartIndex == currentRowEndIndex) {
				// this is only one time, check if it contains line breaks:
				Item item = (Item) items[currentRowStartIndex];
				if (item instanceof StringItem) {
					int numberOfLines = ((StringItem)item).getNumberOfLines();
					if (numberOfLines > 1) {
						for (int i=1; i<numberOfLines; i++) {
							this.wrappedText.addLine("", 0);
						}
					}
				}
			}
		}
		
		/**
		 * Determines if the initialization failed, e.g. when the first line was too small.
		 * @return true when the initialization failed.
		 */
		public boolean isInitializationError() {
			return this.isInitializationError;
		}
		
		/**
		 * Resets the initialization error status to false.
		 */
		public void resetInitializationError() {
			this.isInitializationError = false;
		}
		
	}
	
	/**
	 * Allows to add your own parsing to the HtmlTextEffect
	 */
	public static interface HtmlTextParser {
		/**
		 * Parses the given tag
		 * @param tagName the lowercase tag name, e.g. img for &lt;img&gt;
		 * @param parser the pull parser
		 * @param nodeStyle the current style
		 * @param childList the list of items to which the parser can add item
		 * @return true when the parser handled the tag, false when the default implementation should continue the parsing.
		 */
		boolean parseTag( String tagName, XmlPullParser parser, Style nodeStyle, ArrayList childList);
	}

}
