//#condition polish.usePolishGui
/*
 * Copyright (c) 2012 Robert Virkus / Enough Software
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


import javax.microedition.lcdui.Font;
import javax.microedition.midlet.MIDlet;

import de.enough.polish.ui.Command;
import de.enough.polish.ui.StringItem;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.TextUtil;
import de.enough.polish.util.WrappedText;

/**
 * Allows to use simple HTML markup, message markup such as *bold*, /italic/ or _underlined_ and smileys for the design of the text. 
 * Typically this effect will be used for visualizes received or sent messages. 
 * Usage in polish.css:
 * <pre>
 * .myText {
 * 	 text-effect: message;
 * }
 * </pre>
 * 
 * Usage in Java:
 * <pre>
 * //#style myText
 * StringItem item = new StringItem(null, "hello &lt;div style=\"color: red\"&gt;red, red&lt;/div&gt; &lt;b&gt;world&lt;/b&gt; :-)" );
 * </pre>
 * 
 * @author Robert Virkus
 *
 */
public class MessageTextEffect extends HtmlTextEffect {
	
	private static final ArrayList MARKUP_LIST = new ArrayList();
	
	static {
		addMarkup(new SurroundMarkup("*", "<b>", "</b>"));
		addMarkup(new SurroundMarkup("/", "<i>", "</i>"));
		addMarkup(new SurroundMarkup("_", "<div style=\"font-style: underlined;\">", "</div>"));
		addMarkup(new ReplacementMarkup(":-)", "<img src=\"/emoticon_smile.png\"/>"));
		addMarkup(new ReplacementMarkup(":-(", "<img src=\"/emoticon_cry.png\"/>"));
		addMarkup(new ReplacementMarkup(":-/", "<img src=\"/emoticon_frown.png\"/>"));
		addMarkup(new ReplacementMarkup(":-D", "<img src=\"/emoticon_laugh.png\"/>"));
	}
	
	/**
	 * Removes all default markups
	 */
	public static void clearDefaultMarkups() {
		MessageTextEffect.MARKUP_LIST.clear();
	}
	
	/**
	 * Adds a specific markup
	 * @param markup the markup
	 */
	public static void addMarkup( Markup markup ) {
		MessageTextEffect.MARKUP_LIST.add( markup );
	}
	
	/**
	 * Sets a midlet, so that email and web addresses can be resolved by opening them in the native browser
	 * @param midlet the midlet
	 * @param cmdOpenWebsite the command for opening websites
	 * @param cmdOpenMailto the command for opening mailto/email addresses
	 */
	public static void setMidlet(MIDlet midlet, Command cmdOpenWebsite, Command cmdOpenMailto) {
		HtmlTextEffect.setMidlet(midlet, cmdOpenWebsite, cmdOpenMailto);
		addMarkup( new PatternStartMarkup("www.", "<a href=\"http://@0\">", "</a>"));
		//addMarkup( new PatternMiddleMarkup("@", "<a href=\"mailto:@0\">", "</a>"));
		addMarkup( new EmailMarkup("<a href=\"mailto:@0\">", "</a>"));
		addMarkup( new PatternStartMarkup("#", "<a href=\"http://twitter.com/#!/search?q=%23@0\">", "</a>", false));
		addMarkup( new PatternStartMarkup("@", "<a href=\"http://twitter.com/#!/@0\">", "</a>", false));
	}

	/**
	 * Creates a new MessageTextEffect
	 */
	public MessageTextEffect() {
		// nothing to initialize
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.texteffects.HtmlTextEffect#wrap(de.enough.polish.ui.StringItem, java.lang.String, int, javax.microedition.lcdui.Font, int, int, int, java.lang.String, int, de.enough.polish.util.WrappedText)
	 */
	public void wrap(StringItem parent, String htmlText, int textColor,
			Font meFont, int firstLineWidth, int lineWidth, int maxLines,
			String maxLinesAppendix, int maxLinesAppendixPosition,
			WrappedText wrappedText) 
	{
		htmlText = convertMarkup(htmlText, MARKUP_LIST);
		super.wrap(parent, htmlText, textColor, meFont, firstLineWidth, lineWidth,
				maxLines, maxLinesAppendix, maxLinesAppendixPosition, wrappedText);
	}
	
	public static String convertMarkup( String input ) {
		return convertMarkup( input, MARKUP_LIST);
	}
	
	public static String convertMarkup( String inputText, ArrayList markupList ) {
		int size = markupList.size();
		if (size > 0) {
			Object[] markups = markupList.getInternalArray();
			for (int i = 0; i < size; i++) {
				Markup markup = (Markup) markups[i];
				if (markup == null) {
					break;
				}
				inputText = markup.convertMarkup(inputText);
			}
		}
		return inputText;
	}
	
	public static interface Markup {
		String convertMarkup(String inputText);
	}

	/**
	 * A markup that inserts text at the beginning and end of found matches.
	 * Example:
	 * <pre>
	 * addMarkup(new SurroundMarkup("*", "&lt;b&gt;", "&lt;/b&gt;"));
	 * </pre>
	 *
	 */
	public static class SurroundMarkup implements Markup {
		String input;
		String openingTag;
		String closingTag;
		int replacementLength;
		
		public SurroundMarkup( String input, String openingTag, String closingTag) {
			this.input = input;
			this.openingTag = openingTag;
			this.closingTag = closingTag;
			this.replacementLength = openingTag.length() + closingTag.length() - (2 * input.length());
		}

		/*
		 * (non-Javadoc)
		 * @see de.enough.polish.ui.texteffects.MessageTextEffect.Markup#convertMarkup(java.lang.String)
		 */
		public String convertMarkup(String inputText) {
			String markupInput = this.input;
			int inputTextLength = inputText.length();
			int startIndex = inputText.indexOf(markupInput);
			while ((startIndex != -1) && (startIndex < inputTextLength - 2)) {
				boolean skipThisMatch = false;
				if (startIndex > 0) {
					char previousChar = inputText.charAt(startIndex -1 );
					if (!(previousChar == ' ' || previousChar == '\t')) {
						skipThisMatch = true;
					}
				}
				if (!skipThisMatch) {
					char nextChar = inputText.charAt(startIndex+1);
					if ((nextChar != ' ') && (nextChar != '\t')) {
						int endIndex = inputText.indexOf(markupInput, startIndex+1);;
						while (endIndex != -1) {
							char previousChar = inputText.charAt(endIndex-1);
							if ((previousChar != ' ') && (previousChar != '\t')) {
								break;
							}
							endIndex = inputText.indexOf(markupInput, endIndex+1);;
						}
						if (endIndex != -1) {
							// found a match!
							//System.out.println("found a match: [" + inputText.substring(startIndex, endIndex) + "]" );
							int inputLength = markupInput.length();
							inputText = inputText.substring(0, startIndex) 
									+ this.openingTag 
									+ inputText.substring(startIndex + inputLength, endIndex)
									+ this.closingTag
									+ inputText.substring( endIndex + inputLength);
							inputTextLength += this.replacementLength;
							startIndex += this.replacementLength;
						}
					}
				}
				startIndex = inputText.indexOf(markupInput, startIndex+1);
			}
			return inputText;
		}
	}
	
	/**
	 * A markup that replaces a static search string with a replacement
	 * Example:
	 * <pre>
	 * addMarkup(new ReplacementMarkup(":-)", "&lt;img src=\"/emoticon_smile.png\"/&gt;"));
	 * </pre>
	 */
	public static class ReplacementMarkup implements Markup {
		
		private String search;
		private String replacement;
		
		public ReplacementMarkup( String search, String replacement) {
			this.search = search;
			this.replacement = replacement;
		}

		/*
		 * (non-Javadoc)
		 * @see de.enough.polish.ui.texteffects.MessageTextEffect.Markup#convertMarkup(java.lang.String)
		 */
		public String convertMarkup(String inputText) {
			return TextUtil.replace(inputText, this.search, this.replacement);
		}
	}
	
	/**
	 * A markup that surrounds a matching pattern with a tag.
	 * \@0 will be replaced with the surrounded text  
	 * Example:
	 * <pre>
	 * addMarkup( new PatternStartMarkup("www.", "&lt;a href=\"http://@0\"&gt;", "&lt;/a&gt;"))
	 * </pre>
	 */
	public static class PatternStartMarkup implements Markup {
		
		private final String pattern;
		private final String openingTag;
		private final String closingTag;
		private final boolean includePattern;
		
		public PatternStartMarkup( String pattern, String openingTag, String closingTag) {
			this(pattern, openingTag, closingTag, true);
		}
		
		public PatternStartMarkup( String pattern, String openingTag, String closingTag, boolean includePattern) {
			this.pattern = pattern;
			this.openingTag = openingTag;
			this.closingTag = closingTag;
			this.includePattern = includePattern;
		}


		public String convertMarkup(String inputText) {
			int startIndex = inputText.indexOf(this.pattern);
			int endIndex;
			while (startIndex != -1) {
				// check if match is surrounded by whitespace:
				boolean startOk = (startIndex == this.pattern.length());
				if (!startOk) {
					char c = inputText.charAt(startIndex-1);
					startOk = ((c == ' ') || (c == '\t'));
				}
				if (startOk) {
					endIndex = startIndex + this.pattern.length();
					while (endIndex < inputText.length()) {
						char c = inputText.charAt(endIndex);
						if ((c == ' ') || (c == '\t')) {
							break;
						}
						endIndex++;
					}
					String text = inputText.substring(startIndex, endIndex);
					String openTagContent = text;
					if (!this.includePattern) {
						openTagContent = openTagContent.substring(this.pattern.length());
					}
					String open = TextUtil.replaceFirst(this.openingTag, "@0", openTagContent);
					inputText = inputText.substring(0, startIndex) + open + text + this.closingTag + inputText.substring(endIndex);
					endIndex += open.length() + this.closingTag.length();
				} else {
					endIndex = startIndex + 1;
				}
				startIndex = inputText.indexOf(this.pattern, endIndex);
			}
			return inputText;
		}
		
	}
	
	/**
	 * A markup that surrounds a matching pattern with a tag.
	 * \@0 will be replaced with the surrounded text  
	 * Example:
	 * <pre>
	 * addMarkup( new PatternMiddleMarkup("@", "&lt;a href=\"mailto:@0\"&gt;", "&lt;/a&gt;"))
	 * </pre>
	 */
	public static class PatternMiddleMarkup implements Markup {
		
		private String pattern;
		private String openingTag;
		private String closingTag;
		
		public PatternMiddleMarkup( String pattern, String openingTag, String closingTag) {
			this.pattern = pattern;
			this.openingTag = openingTag;
			this.closingTag = closingTag;
		}

		public String convertMarkup(String inputText) {
			int patternIndex = inputText.indexOf(this.pattern);
			int endIndex = patternIndex;
			while (patternIndex > 0) { // there needs to be at least some characters before the matching patternIndex
				// check if match is surrounded by whitespace:
				int startIndex = 0;
				for (int charIndex=patternIndex; --charIndex >= 0; ) {
					char c = inputText.charAt(charIndex);
					if ((c == ' ') || (c == '\t')) {
						startIndex = charIndex+1;
						break;
					}
				}
				endIndex = patternIndex + this.pattern.length();
				if (startIndex < patternIndex) {
					while (endIndex < inputText.length()) {
						char c = inputText.charAt(endIndex);
						if ((c == ' ') || (c == '\t')) {
							break;
						}
						endIndex++;
					}
					if (endIndex > patternIndex + this.pattern.length()) {
						String text = inputText.substring(startIndex, endIndex);
						String open = TextUtil.replaceFirst(this.openingTag, "@0", text);
						inputText = inputText.substring(0, startIndex) + open + text + this.closingTag + inputText.substring(endIndex);
						endIndex += open.length() + this.closingTag.length();
					} else {
						endIndex = patternIndex + 1;
					}
				}
				patternIndex = inputText.indexOf(this.pattern, endIndex);
			}
			return inputText;
		}
	}
	

	/**
	 * A markup that surrounds a matching email address with a tag.
	 * \@0 will be replaced with the surrounded email address  
	 * Example:
	 * <pre>
	 * addMarkup( new EmailMarkup("&lt;a href=\"mailto:@0\"&gt;", "&lt;/a&gt;"))
	 * </pre>
	 */
	public static class EmailMarkup implements Markup {
		
		private String openingTag;
		private String closingTag;
		
		public EmailMarkup( String openingTag, String closingTag) {
			this.openingTag = openingTag;
			this.closingTag = closingTag;
		}

		public String convertMarkup(String inputText) {
			int atIndex = inputText.indexOf('@');
			int endIndex = atIndex;
			while (atIndex > 0) { // there needs to be at least some characters before the matching patternIndex
				// check if match is surrounded by whitespace:
				int startIndex = 0;
				for (int charIndex=atIndex; --charIndex >= 0; ) {
					char c = inputText.charAt(charIndex);
					if ((c == ' ') || (c == '\t')) {
						startIndex = charIndex+1;
						break;
					}
				}
				endIndex = atIndex + 1;
				if (startIndex < atIndex) {
					while (endIndex < inputText.length()) {
						char c = inputText.charAt(endIndex);
						if ((c == ' ') || (c == '\t')) {
							break;
						}
						endIndex++;
					}
					if (endIndex > atIndex + 4) { // after the @ there must be at least [domain].[country code] or something similar 
						String emailAddress = inputText.substring(startIndex, endIndex);
						if ((emailAddress.indexOf('.', (atIndex-startIndex)) != -1)
							&& (emailAddress.indexOf('<') == -1)
							&& (emailAddress.indexOf('>') == -1)
						) {
							String open = TextUtil.replaceFirst(this.openingTag, "@0", emailAddress);
							inputText = inputText.substring(0, startIndex) + open + emailAddress + this.closingTag + inputText.substring(endIndex);
							endIndex += open.length() + this.closingTag.length();
						} else {
							endIndex = atIndex + 1;							
						}
					} else {
						endIndex = atIndex + 1;
					}
				}
				atIndex = inputText.indexOf('@', endIndex);
			}
			return inputText;
		}
	}
}
