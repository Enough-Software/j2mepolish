//#condition polish.usePolishGui

/*
 * Created on 11-Jan-2006 at 19:20:28.
 * 
 * Copyright (c) 2009 - 2009 Michael Koch / Enough Software
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
package de.enough.polish.browser.html;

import de.enough.polish.browser.Browser;
import de.enough.polish.browser.RedirectThread;
import de.enough.polish.browser.TagHandler;
import de.enough.polish.browser.css.CssInterpreter;
import de.enough.polish.ui.Choice;
import de.enough.polish.ui.ChoiceGroup;
import de.enough.polish.ui.ChoiceItem;
import de.enough.polish.ui.Command;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.ImageItem;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemCommandListener;
import de.enough.polish.ui.Dimension;
import de.enough.polish.ui.ScaledImageItem;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;
import de.enough.polish.ui.TableItem;
import de.enough.polish.ui.TextField;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.BooleanStack;
import de.enough.polish.util.HashMap;
import de.enough.polish.util.Locale;
import de.enough.polish.util.TextUtil;
import de.enough.polish.xml.SimplePullParser;

//#if polish.cldc1.0
	//# import de.enough.polish.util.TextUtil;
//#endif

/**
 * Handles HTML tags.
 */
public class HtmlTagHandler
  extends TagHandler
  implements ItemCommandListener
{
	
	private static final Style STYLE_LINE_BREAK = new Style();

	/** title tag */
  public static final String TAG_TITLE = "title";
	/** meta tag */
  public static final String TAG_META = "meta";
	/** body tag */
  public static final String TAG_BODY = "body";
	/** style tag */
  public static final String TAG_STYLE = "style";
	/** br tag */
  public static final String TAG_BR = "br";
	/** p tag */
  public static final String TAG_P = "p";
	/** img tag */
  public static final String TAG_IMG = "img";
	/** div tag */
  public static final String TAG_DIV = "div";
	/** span tag */
  public static final String TAG_SPAN = "span";
	/** a tag */
  public static final String TAG_A = "a";
	/** b tag */
  public static final String TAG_B = "b";
	/** strong tag */
  public static final String TAG_STRONG = "strong";
	/** i tag */
  public static final String TAG_I = "i";
	/** em tag */
  public static final String TAG_EM = "em";
	/** form tag */
  public static final String TAG_FORM = "form";
	/** input tag */
  public static final String TAG_INPUT = "input";
	/** button tag */
  public static final String TAG_BUTTON = "button";
	/** text area tag */
  public static final String TAG_TEXT_AREA = "textarea";
	/** select tag */
  public static final String TAG_SELECT = "select";
	/** option tag */
  public static final String TAG_OPTION = "option";
	/** script tag */
  public static final String TAG_SCRIPT = "script";
	/** table tag */
  public static final String TAG_TABLE = "table";
	/** table row tag */
  public static final String TAG_TR = "tr";
	/** table header tag */
  public static final String TAG_TH = "th";
	/** table data tag */
  public static final String TAG_TD = "td";
	/** code tag */
  public static final String TAG_CODE = "code";
  
	/** type attribute */
  public static final String INPUT_TYPE = "type";
	/** name attribute */
  public static final String INPUT_NAME = "name";
	/** value attribute */
  public static final String INPUT_VALUE = "value";

	/** text type-value */
  public static final String INPUTTYPE_TEXT = "text";
	/** password type-value */
  public static final String INPUTTYPE_PASSWORD = "password";
	/** hidden type-value */
  public static final String INPUTTYPE_HIDDEN = "hidden";
	/** submit type-value */
  public static final String INPUTTYPE_SUBMIT = "submit";
	/** checkbox type-value */
  public static final String INPUTTYPE_CHECKBOX = "checkbox";
	/** radio type-value */
  public static final String INPUTTYPE_RADIO = "radio";
	/** text type-value */
  public static final String INPUTTYPE_NUMERIC = "numeric";
  
	/** href attribute */
  public static final String ATTR_HREF = "href";
	/** form attribute */
  public static final String ATTR_FORM = "polish_form";
	/** type attribute */
  public static final String ATTR_TYPE = "type";
	/** value attribute */
  public static final String ATTR_VALUE = "value";
	/** name attribute */
  public static final String ATTR_NAME = "name";
  	/** size attribute */
  	public static final String ATTR_SIZE = "size";
  	/** multiple attribute */
  	public static final String ATTR_MULTIPLE = "multiple";

  /** default link command */
	//#ifdef polish.i18n.useDynamicTranslations
		public static Command CMD_LINK = new Command( Locale.get("polish.command.followlink"), Command.OK, 2 );
	//#elifdef polish.command.followlink:defined
		//#= public static final Command CMD_LINK = new Command("${polish.command.followlink}", Command.OK, 2 );
	//#else
		//# public static final Command CMD_LINK = new Command("Go", Command.OK, 2);
	//#endif
	/** default submit command */
	//#ifdef polish.i18n.useDynamicTranslations
		public static Command CMD_SUBMIT = new Command( Locale.get("polish.command.submit"), Command.ITEM, 2 );
	//#elifdef polish.command.submit:defined
		//#= public static final Command CMD_SUBMIT = new Command("${polish.command.submit}", Command.ITEM, 2 );
	//#else
		//# public static final Command CMD_SUBMIT = new Command("Submit", Command.ITEM, 2);
	//#endif
	/** default back command */
	//#ifdef polish.i18n.useDynamicTranslations
		public static Command CMD_BACK = new Command( Locale.get("polish.command.back"), Command.BACK, 10 );
	//#elifdef polish.command.back:defined
		//#= public static final Command CMD_BACK = new Command("${polish.command.back}", Command.BACK, 10 );
	//#else
		//# public static final Command CMD_BACK = new Command("Back", Command.BACK, 10);
	//#endif
  
	private HtmlForm currentForm;
	private HtmlSelect currentSelect;
	private TableItem currentTable;

	protected HtmlBrowser browser;

	/** next text should be added in bold font style */
	public boolean textBold;
	/** next text should be added in italic font style */
	public boolean textItalic;
	/** style for the forthcoming text */
	public Style textStyle;
	private FormListener formListener;
	private ChoiceGroup currentCheckBoxChoiceGroup;
	private ChoiceGroup currentRadioChoiceGroup;
	private BooleanStack isDivOrSpanOpened;

	private String anchorHref;

	/**
	 * Creates a new html tag handler
	 */
	public HtmlTagHandler() {
		//#ifdef polish.i18n.useDynamicTranslations
		if (Locale.get("polish.command.followlink") != CMD_LINK.getLabel()) {
			CMD_LINK = new Command( Locale.get("polish.command.followlink"), Command.OK, 2 );
			CMD_SUBMIT = new Command( Locale.get("polish.command.submit"), Command.ITEM, 2 );
			CMD_BACK = new Command( Locale.get("polish.command.back"), Command.BACK, 10 );
		}
		//#endif
		STYLE_LINE_BREAK.layout = Item.LAYOUT_NEWLINE_AFTER;
	}

	public void register(Browser parent)
	{
		this.browser = (HtmlBrowser) parent;
		this.textBold = false;
		this.textItalic = false;

		parent.addTagHandler(TAG_BODY, this);
		parent.addTagHandler(TAG_TITLE, this);
		parent.addTagHandler(TAG_META, this);
		parent.addTagHandler(TAG_STYLE, this);

		parent.addTagHandler(TAG_BR, this);
		parent.addTagHandler(TAG_P, this);
		parent.addTagHandler(TAG_IMG, this);
		parent.addTagHandler(TAG_DIV, this);
		parent.addTagHandler(TAG_SPAN, this);
		parent.addTagHandler(TAG_A, this);
		parent.addTagHandler(TAG_B, this);
		parent.addTagHandler(TAG_STRONG, this);
		parent.addTagHandler(TAG_I, this);
		parent.addTagHandler(TAG_EM, this);
		parent.addTagHandler(TAG_FORM, this);
		parent.addTagHandler(TAG_INPUT, this);
		parent.addTagHandler(TAG_BUTTON, this);
		parent.addTagHandler(TAG_SELECT, this);
		parent.addTagHandler(TAG_OPTION, this);
		parent.addTagHandler(TAG_SCRIPT, this);
		parent.addTagHandler(TAG_TEXT_AREA, this);
		parent.addTagHandler(TAG_TABLE, this);
		parent.addTagHandler(TAG_TR, this);
		parent.addTagHandler(TAG_TH, this);
		parent.addTagHandler(TAG_TD, this);
		parent.addTagHandler(TAG_CODE, this);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.browser.TagHandler#handleTag(de.enough.polish.ui.Container, de.enough.polish.xml.PullParser, java.lang.String, boolean, de.enough.polish.util.HashMap, de.enough.polish.ui.Style)
	 */
	public boolean handleTag(Container parentItem, SimplePullParser parser, String tagName, boolean opening, HashMap attributeMap, Style style)
	{
		//#debug
		System.out.println( (opening ?  "<" : "</" ) + tagName + ">" );
		tagName = tagName.toLowerCase();
		if (TAG_DIV.equals(tagName) || TAG_SPAN.equals(tagName)) {
			if (opening) {
				String itemStyleName = (String) attributeMap.get("textclass");
				Style itemStyle = (itemStyleName == null ? null : StyleSheet.getStyle(itemStyleName));
				if (itemStyle != null) {
					this.textStyle = itemStyle;
				}
				if (this.isDivOrSpanOpened == null) {
					this.isDivOrSpanOpened = new BooleanStack();
				}
//				System.out.println("opening <div> with style " + (style == null ? null : style.name));
				if (style != null) {
					this.browser.openContainer( style );
				}
				this.isDivOrSpanOpened.push( style != null );
//				this.browser.openContainer( style );
//				this.isDivOrSpanOpened.push( true );
			} else {
				this.textStyle = null;
				//System.out.println("closing <div> with container=" + this.isDivOrSpanOpened.peek());
				if (this.isDivOrSpanOpened.pop()) {
					Container container = this.browser.closeContainer();
					if (UiAccess.cast(container) instanceof TableItem) {
						this.currentTable = (TableItem)UiAccess.cast(container);
					}
				}
//				Style divStyle = container.getStyle();
//					if (divStyle != null) {
//					Object[] items = container.getInternalArray();
//					for (int i = 0; i < items.length; i++)
//					{
//						Item item = (Item) items[i];
//						if (item == null) {
//							break;
//						}
//						item.setStyle( divStyle );
//					}
//				}
			}
		} else if (TAG_SELECT.equals(tagName)) {
			if (opening) {
				if (this.currentSelect != null) {
					//#debug error
					System.out.println("Error in HTML-Code: You cannot open a <select>-tag inside another <select>-tag.");

					ChoiceGroup choiceGroup = this.currentSelect.getChoiceGroup();
					add(choiceGroup);
					if (this.currentForm == null) {
						//#debug error
						System.out.println("Error in HTML-Code: no <form> for <select> element found!");
					} else {
						this.currentForm.addItem(choiceGroup);
					}
					this.currentSelect = null;
				}

				String name = parser.getAttributeValue(ATTR_NAME);
				String sizeStr = parser.getAttributeValue(ATTR_SIZE);
				int size;

				try {
					size = Integer.parseInt(sizeStr);
				}
				catch (NumberFormatException e) {
					size = -1;
				}

				boolean isMultiple = parser.getAttributeValue(ATTR_MULTIPLE) != null;
				this.currentSelect = new HtmlSelect(name, size, isMultiple, style);
			} else { // tag is closed
				if (this.currentSelect != null) {
					ChoiceGroup choiceGroup = this.currentSelect.getChoiceGroup();
					add(choiceGroup);
					if (this.currentForm == null) {
						//#debug error
						System.out.println("Error in HTML-Code: no <form> for <select> element found!");
					} else {
						this.currentForm.addItem(choiceGroup);
					}
					this.currentSelect = null;
				}
				//#mdebug error
				else {
					//#debug error
					System.out.println("Error in HTML-Code. You cannot close a <select>-tag without opening one.");
				}
				//#enddebug
			}
			return true;
		}
		else if (TAG_OPTION.equals(tagName)) {
			if (this.currentSelect != null && opening) {
				// TODO: handle "selected" attribute.
				String value = parser.getAttributeValue(ATTR_VALUE);
				String selected = parser.getAttributeValue("selected");
				parser.next();
				String name = handleText(parser.getText());

				if (value == null) {
					value = name;
				}

				this.currentSelect.addOption(name, value, selected != null, style);
			}
			return true;
		} else if (TAG_A.equals(tagName)) {
			if (opening) {
				this.anchorHref = (String) attributeMap.get(ATTR_HREF);
				//#if polish.debug.error
				if (this.anchorHref == null) {
					//#debug error
					System.out.println("Unable to handle anchor tag <a> without " + ATTR_HREF + " attribute: " + attributeMap);
				}
				//#endif
				this.browser.openContainer(style);
				if (style == null) {
					Container container = this.browser.getCurrentContainer();
					//#style browserLink
					UiAccess.setStyle(container);
				}
			} else {
				// apply link to last item(s):
				Container container = this.browser.removeCurrentContainer();
				Style contStyle = container.getStyle();
				Item linkItem;
				if (container.size() == 0) {
					linkItem = new StringItem( null, null );
				} else if (container.size() == 1) {
					linkItem = container.get(0);
				} else {
					// check if all elements are StringItems - then we should combine them:
					boolean allItemsAreStringItems = true;
					StringBuffer text = new StringBuffer();
					for (int i=0; i<container.size(); i++) {
						Item item = container.get(i);
						if (!(item instanceof StringItem)) { 
							allItemsAreStringItems = false;
							break;
						} else {
							if (text.length() > 0) {
								text.append(' ');
							}
							text.append( ((StringItem)item).getText() );
						}
					}
					if (allItemsAreStringItems) {
						linkItem = new StringItem( null, text.toString() );
					} else {
						linkItem = container;
					}
				}
//				System.out.println("closing <a>: container.size()=" + container.size() + ", linkItem=" + linkItem + ", style=" + (contStyle != null ? contStyle.name : "<no style>") );
				if (this.anchorHref != null) {
					if (contStyle != null) {
						linkItem.setStyle( contStyle );
					} else if (linkItem.getStyle() == null) {
						//#style browserLink
						UiAccess.setStyle( linkItem );
					}
					linkItem.setDefaultCommand(CMD_LINK);
					linkItem.setItemCommandListener( this );
					linkItem.setAttribute(ATTR_HREF, this.anchorHref );
					addCommands(TAG_A, linkItem);
					add(linkItem);
				}

				//this.browser.closeContainer();
			}
		}

		if (opening)
		{    
//			if (TAG_A.equals(tagName))
//			{
//				String href = (String) attributeMap.get(ATTR_HREF);
//				parser.next();
//				Item linkItem;
//				if (href != null)
//				{
//					String anchorText = handleText(parser.getText());
//					// hack for image links:
//					if ("".equals(anchorText) && TAG_IMG.equals(parser.getName())) {
//						// this is an image link:
//						attributeMap.clear();
//						for (int i = 0; i < parser.getAttributeCount(); i++)
//						{
//							String attributeName = parser.getAttributeName(i);
//							String attributeValue = parser.getAttributeValue(i);
//							attributeMap.put(attributeName, attributeValue);
//						}
//						String src = (String) attributeMap.get("src");
//						String url = this.browser.makeAbsoluteURL(src);
//						Image image = this.browser.loadImage(url);
//						//#style browserLink
//						linkItem = new ImageItem(null, image, 0, (String) attributeMap.get("alt") );
//						//this.browser.loadImageLater( url, (ImageItem) linkItem );
//
//					} else {
//						//#style browserLink
//						linkItem = new StringItem(null, anchorText);
//					}
//					linkItem.setDefaultCommand(CMD_LINK);
//					linkItem.setItemCommandListener( this );
//					linkItem.setAttribute(ATTR_HREF, href );
//					addCommands(TAG_A, linkItem);
//				}
//				else
//				{
//					//#style browserText
//					linkItem = new StringItem(null, handleText(parser.getText()));
//				}
//				if (style != null) {
//					linkItem.setStyle(style);
//				}
//				add(linkItem);
//				return true;
//			}
//			else 
			if (TAG_BR.equals(tagName))
			{
				addLineBreak();
				return true;
			}
			else if (TAG_P.equals(tagName))
			{
				addLineBreak();
				if (opening) {
					this.textStyle = style;
				}
				return true;
			}
			else if (TAG_IMG.equals(tagName))
			{
				String src = (String) attributeMap.get("src");
				String url = this.browser.makeAbsoluteURL(src);
				Dimension width = parseSizeValue((String) attributeMap.get("width"));
				Dimension height = parseSizeValue((String) attributeMap.get("height"));
				Style imageStyle = new Style();
				imageStyle.addAttribute(58 /* "min-width" */, width);
				imageStyle.addAttribute(144 /* "min-height" */, height);
				ImageItem item;

				if (width != null || height != null) {
					item = new ScaledImageItem(null, null, width, height, Item.LAYOUT_DEFAULT, "");
				}
				else {
					item = new ImageItem(null, null, Item.LAYOUT_DEFAULT, "");
				}

				if (imageStyle != null) {
					item.setStyle(imageStyle);
				}

				this.browser.loadImageLater(item, url);
				add(item);
				return true;
			}
			else if (TAG_TITLE.equals(tagName))
			{
				// Hack to read title.
				parser.next();
				String name = handleText(parser.getText());
				Screen myScreen = this.browser.getScreen();
				if (name != null && myScreen != null) {
					myScreen.setTitle( name );
				}
				return true; 
			}
			else if (TAG_STYLE.equals(tagName))
			{
				// Hack to read style content.
				parser.next();
				String cssCode = parser.getText();
				try {
					CssInterpreter reader = new CssInterpreter( cssCode, this.browser );
					this.browser.setCssStyles(reader.getAllStyles());
				} catch (Exception e) {
					//#debug error
					System.out.println("Unable to parse CSS" + e );
				}
				//parser.next();
				return true;
			}
			else if (TAG_META.equals(tagName)) 
			{
				String httpEquiv = (String) attributeMap.get("http-equiv");
				if (httpEquiv != null && TextUtil.equalsIgnoreCase( "refresh", httpEquiv)) {
					String content = (String) attributeMap.get("content");
					if (content != null) {
						int semicolonPos = content.indexOf(';');
						int waitTime = 0;
						String url = content;
						if (semicolonPos != -1) {
							try {
								waitTime = Integer.parseInt(content.substring(0, semicolonPos));
								url = content.substring(semicolonPos+1);
							} catch (Exception e) {
								if (semicolonPos == 0) {
									url = content.substring(1);
								}
							}
						}
						int equalsPos = url.indexOf('=');
						if (equalsPos != -1) {
							url = url.substring(equalsPos + 1).trim();
						}
						if (url.indexOf(':') == -1) {
							// assuming resource protocol:
							if (url.charAt(0) != '/') {
								url = "resource://" + url;
							} else {
								url = "resource:/" + url; 
							}
						}
						if (waitTime > 0) {
							(new RedirectThread( this.browser, waitTime * 1000L, url )).start();
						} else {
							// consume rest of document:
							while (parser.next() != SimplePullParser.END_DOCUMENT)
							{
								// read document..
							}
							this.browser.go( url );
						}
						return true;
					}
				}
				return false;
			}
			//#if polish.Browser.supportTextInput != false
			else if (TAG_TEXT_AREA.equals(tagName)) 
			{
				parser.next();
				String value = handleText(parser.getText());
				int maxCharNumber = 500;
				String cols = (String) attributeMap.get("cols");
				String rows = (String) attributeMap.get("rows");
				if (cols != null && rows != null) {
					try {
						maxCharNumber = Integer.parseInt(cols) * Integer.parseInt(rows);
					} catch (Exception e) {
						//#debug error
						System.out.println("Unable to parse textarea cols or rows attribute: cols=" + cols + ", rows=" + rows);
					}
				}
				//#style browserInput
				TextField textField = new TextField(null, value, maxCharNumber, TextField.ANY);
				if (style != null) {
					textField.setStyle(style);
				}
				add(textField);
				if (this.currentForm != null) {
					this.currentForm.addItem(textField);
					textField.setAttribute(ATTR_FORM, this.currentForm);
					String name = (String) attributeMap.get(INPUT_NAME);
					if (value == null) {
						value = name;
					}
					if (name != null) {
						textField.setAttribute(ATTR_NAME, name);
						textField.setAttribute(ATTR_VALUE, value);            	  
					}
				}
				return true;
			}
			//#endif
			else if (TAG_BUTTON.equals(tagName) && this.currentForm != null) {
				String name = (String) attributeMap.get(INPUT_NAME);
				String value = (String) attributeMap.get(INPUT_VALUE);

				if (value == null) {
					value = name;
				}

				//#style browserLink
				StringItem buttonItem = new StringItem(null, value);
				if (style != null) {
					buttonItem.setStyle(style);
				}
				buttonItem.setDefaultCommand(CMD_SUBMIT);
				buttonItem.setItemCommandListener(this);
				addCommands(TAG_INPUT, INPUT_TYPE, INPUTTYPE_SUBMIT, buttonItem);
				add(buttonItem);

				this.currentForm.addItem(buttonItem);
				buttonItem.setAttribute(ATTR_FORM, this.currentForm);
				buttonItem.setAttribute(ATTR_TYPE, "submit");

				if (name != null) {
					buttonItem.setAttribute(ATTR_NAME, name);
					buttonItem.setAttribute(ATTR_VALUE, value);
				}    	  
			}
			else if (TAG_INPUT.equals(tagName))
			{
				if (this.currentForm != null)
				{
					String type = (String) attributeMap.get(INPUT_TYPE);
					String name = (String) attributeMap.get(INPUT_NAME);
					String value = (String) attributeMap.get(INPUT_VALUE);
					if (type != null) {
						type = type.toLowerCase();
					}

					if (this.formListener != null && name != null) {
						value = this.formListener.verifyInitialFormValue(this.currentForm.getAction(),  name, value);
					}
					//#if polish.Browser.supportTextInput != false
					if (INPUTTYPE_TEXT.equals(type) || INPUTTYPE_PASSWORD.equals(type) || INPUTTYPE_NUMERIC.equals(type))
					{
						int constraints;
						if (INPUTTYPE_NUMERIC.equals(type)) {
							constraints = TextField.NUMERIC;
						} else {
							constraints = INPUTTYPE_TEXT.equals(type) ? TextField.ANY : TextField.PASSWORD;
						}
						//#style browserInput
						TextField textField = new TextField((String)attributeMap.get("label"), value, 100, constraints);
						if (style != null) {
							textField.setStyle(style);
						}
						add(textField);

						this.currentForm.addItem(textField);
						textField.setAttribute(ATTR_FORM, this.currentForm);

						if (name != null) {
							textField.setAttribute(ATTR_NAME, name);
							if (value == null) {
								value = "";
							}
							textField.setAttribute(ATTR_VALUE, value);
						}
					}
					else
					//#endif
					if (INPUTTYPE_SUBMIT.equals(type))
					{

						if (value == null) {
							value = name;
						}

						//#style browserLink
						StringItem buttonItem = new StringItem((String)attributeMap.get("label"), value);
						if (style != null) {
							buttonItem.setStyle(style);
						}
						buttonItem.setDefaultCommand(CMD_SUBMIT);
						buttonItem.setItemCommandListener(this);
						addCommands(TAG_INPUT, INPUT_TYPE, INPUTTYPE_SUBMIT, buttonItem);
						add(buttonItem);

						this.currentForm.addItem(buttonItem);
						buttonItem.setAttribute(ATTR_FORM, this.currentForm);
						buttonItem.setAttribute(ATTR_TYPE, "submit");

						if (name != null) {
							buttonItem.setAttribute(ATTR_NAME, name);
							buttonItem.setAttribute(ATTR_VALUE, value);
						}
					}
					else if (INPUTTYPE_HIDDEN.equals(type)) {
						this.currentForm.addHiddenElement( name, value );
					} 
					else if (INPUTTYPE_CHECKBOX.equals(type) || INPUTTYPE_RADIO.equals(type) ) {
						boolean isCheckBox = INPUTTYPE_CHECKBOX.equals(type);
						parser.next();
						String label = parser.getText().trim();
						int labelLength = label.length();
						if (labelLength == 0) {
							parser.next();
							label = parser.getText().trim();
							labelLength = label.length();
						}
						if ((labelLength > 0) && label.charAt(labelLength -1) == '\n') {
							label = label.substring(0, labelLength - 1);
						}
						ChoiceGroup choiceGroup;
						if (isCheckBox) {
							choiceGroup = this.currentCheckBoxChoiceGroup;
						} else {
							choiceGroup = this.currentRadioChoiceGroup;
						}
						if (choiceGroup == null || !name.equals(choiceGroup.getAttribute(ATTR_NAME))) {
							// create a new choice group:
							String groupLabel = (String) attributeMap.get("label");
							if (isCheckBox) {
								//#style browserChoiceGroupMultiple
								choiceGroup = new ChoiceGroup(groupLabel,  Choice.MULTIPLE );
							} else {
								String choiceType = (String) attributeMap.get("choice");
								if ("popup".equals(choiceType)) {
									//#style browserChoiceGroupPopup
									choiceGroup = new ChoiceGroup(groupLabel,  Choice.POPUP );								
									this.currentRadioChoiceGroup = choiceGroup;
								} else {
									//#style browserChoiceGroupExclusive
									choiceGroup = new ChoiceGroup(groupLabel,  Choice.EXCLUSIVE );
									this.currentRadioChoiceGroup = choiceGroup;
								}								
							}
							choiceGroup.setAttribute(ATTR_NAME, name);
							String styleName = (String) attributeMap.get("groupclass");
							if (styleName != null) {
								Style groupStyle = StyleSheet.getStyle(styleName);
								if (groupStyle != null) {
									choiceGroup.setStyle(groupStyle);
								}
							}
							add(choiceGroup);
							if (this.currentForm == null) {
								//#debug error
								System.out.println("Error in HTML-Code: no <form> for <select> element found!");
							} else {
								this.currentForm.addItem(choiceGroup);
							}
							// end of creating a new choice group
						}
						ChoiceItem item;
						if (isCheckBox) {
							//#style browserCheckBox
							item = new ChoiceItem(label, null, Choice.MULTIPLE);
						} else {
							//#style browserRadio
							item = new ChoiceItem(label, null, choiceGroup.getType() );							
						}
						item.setAttribute(ATTR_VALUE, value);
						choiceGroup.append(item);
						if (attributeMap.get("checked") != null ) {
							choiceGroup.setSelectedIndex(choiceGroup.size()-1,true);
						}
						if (style != null) {
							item.setStyle(style);
						}
					}
					//#if polish.debug.debug
					else
					{
						//#debug
						System.out.println("unhandled html form input type: " + type);
					}
					//#endif
				}

				return true;
			}
			else if (TAG_SCRIPT.equals(tagName)) {
				// Consume javascript code.
				parser.next();
				return true;
			}
			else if (TAG_TABLE.equals(tagName)) {
				//#style browserTable?
				TableItem table  = new TableItem();
				table.setSelectionMode( TableItem.SELECTION_MODE_CELL | TableItem.SELECTION_MODE_INTERACTIVE );
				table.setCellContainerStyle( this.browser.getStyle() );
				if (style != null) {
					table.setStyle(style);
				}

				this.currentTable = table;
				this.browser.openContainer(table);
				return true;
			}
			else if (this.currentTable != null && TAG_TR.equals(tagName)) {
				this.currentTable.moveToNextRow();
				return true;
			}
			else if (this.currentTable != null && TAG_TH.equals(tagName)) {
				//TODO differentiate between th and td
				this.currentTable.moveToNextColumn();
				return true;
			}
			else if (this.currentTable != null && TAG_TD.equals(tagName)) {
				//TODO differentiate between th and td
				this.currentTable.moveToNextColumn();
				return true;
			}
			else if (TAG_CODE.equals(tagName)) {
				//#if polish.css.style.browsertextcode && !polish.LibraryBuild
					//#style browserTextCode
					//# this.textStyle = ();
				//#endif
			}
		}
		else 
		{
			// the tag is being closed:
			if (TAG_TABLE.equals(tagName)) {
				Container container = this.browser.closeContainer();
				if (UiAccess.cast(container) instanceof TableItem) {
					this.currentTable = (TableItem)UiAccess.cast(container);
				} else {
					this.currentTable = null;
				}
				return true;
			} if (TAG_CODE.equals(tagName)) {
				this.textStyle = null;
			}
		}



		if (TAG_B.equals(tagName)
				|| TAG_STRONG.equals(tagName))
		{
			this.textBold = opening;
			return true;
		}
		else if (TAG_I.equals(tagName)
				|| TAG_EM.equals(tagName))
		{
			this.textItalic = opening;
			return true;
		}
		else if (TAG_FORM.equals(tagName))
		{
			if (opening)
			{
				String name = (String) attributeMap.get("name");	
				String action = (String) attributeMap.get("action");
				String method = (String) attributeMap.get("method");
				String encoding = (String) attributeMap.get("enctype");
				
				if (method == null)
				{
					method = "GET";
				}
				this.currentForm = new HtmlForm(name, action, method, encoding, this.browser, this.formListener );
				this.browser.addForm(this.currentForm);
				this.browser.openContainer(style);
			}
			else
			{
				this.browser.closeContainer();
				this.currentForm = null;
				this.currentCheckBoxChoiceGroup = null;
				this.currentRadioChoiceGroup = null;
			}

			return true;
		} else if (opening && TAG_BODY.equals(tagName)) {
			if (style != null) {
				//System.out.println("applying style " + style.name + " for body with bg=" + style.background);
				this.browser.setBackground( style.background );
				this.browser.setBorder( style.border );
				this.browser.setStyle(style, false);
			}
		}

		return false;
	}

	private Dimension parseSizeValue(String s)
	{
		if (s == null) {
			return null;
		}

		s = s.trim();
		boolean isPercent = false;

		if (s.endsWith("%")) {
			isPercent = true;
			s = s.substring(0, s.length() - 1);
		}

		int value = 0;

		try {
			value = Integer.parseInt(s);
		}
		catch (NumberFormatException e) {
			//#debug
			System.out.println("failed to parse integer value: " + s);

			return null;
		}

		return new Dimension(value, isPercent);
	}

	/**
	 * Adds a linebreak.
	 */
	protected void addLineBreak() {
		StringItem stringItem = new StringItem(null, null, STYLE_LINE_BREAK);
		add(stringItem);		
	}

	/**
	 * Used by derived classes to modify parsed text 
	 * @param text the text to modify
	 * @return the modified text
	 */
	protected String handleText(String text)
	{
		return text;
	}

	/**
	 * Adds an item either to the browser or to the current table.
	 * @param item the item
	 */
	private void add(Item item)
	{
		//System.out.println("adding " + item + ", currentTable=" + this.currentTable);
		this.browser.add(item);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.browser.TagHandler#handleCommand(javax.microedition.lcdui.Command)
	 */
	public boolean handleCommand(Command command)
	{
		if (command == CMD_LINK)
		{
			handleLinkCommand();
			return true;
		}
		else if (command == CMD_SUBMIT)
		{
			handleSubmitCommand();
			return true;
		}
		else if (command == CMD_BACK)
		{
			handleBackCommand();
			return true;
		}

		return false;
	}

	protected void handleBackCommand()
	{
		this.browser.goBack();
	}



	protected void handleSubmitCommand()
	{
		Item submitItem = this.browser.getFocusedItem();
		HtmlForm form = (HtmlForm) submitItem.getAttribute(ATTR_FORM);
		while (form == null && (submitItem instanceof Container)) {
			submitItem = ((Container)submitItem).getFocusedItem();
			form = (HtmlForm) submitItem.getAttribute(ATTR_FORM);
		}
		if (form == null) {
			return;
		}

		form.submit(submitItem);
	}

	protected void handleLinkCommand()
	{

		Item linkItem = getFocusedItemWithAttribute( ATTR_HREF, this.browser );
		if (linkItem == null) {
			return;
		}
		String href = (String) linkItem.getAttribute(ATTR_HREF);
		if (href != null) {
			this.browser.go(this.browser.makeAbsoluteURL(href));
		}
		//#if polish.debug.error
		else {
			//#debug error
			System.out.println("Unable to handle link command for item " + linkItem + ": no " + ATTR_HREF + " attribute found.");
		}
		//#endif
	}

	//#if polish.LibraryBuild
	private Item getFocusedItemWithAttribute(String attribute, de.enough.polish.ui.FakeContainerCustomItem container )
	{
		return null;
	}
	//#endif

	/**
	 * Retrieves the currently focused item that has specified the attribute
	 * @param attribute the attribute
	 * @param container the container that should have focused the item
	 * @return the item that contains the attribute or the focused item which is not a Container itself
	 */
	protected Item getFocusedItemWithAttribute(String attribute, Container container )
	{
		Item item = container.getFocusedItem();
		if (item != null && item.getAttribute(attribute) == null && item instanceof Container) {
			return getFocusedItemWithAttribute(attribute, (Container) item );
		}
		return item;
	}

	/**
	 * Handles item commands (implements ItemCommandListener).
	 * 
	 * @param command the command
	 * @param item the item from which the command originates
	 */
	public void commandAction(Command command, Item item)
	{
		handleCommand(command);
	}

	/**
	 * Sets the form listener that is notified about form creation and submission events
	 * 
	 * @param listener the listener, use null for de-registering a previous listener
	 */
	public void setFormListener(FormListener listener)
	{
		this.formListener = listener;
	}

}
