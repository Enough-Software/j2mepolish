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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA02111-1307USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.browser.html;

import java.util.Enumeration;
import java.util.Hashtable;

import de.enough.polish.ui.ChoiceGroup;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.TextField;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.KeyValueList;
import de.enough.polish.util.TextUtil;

public class HtmlForm
{
	public static final String GET = "GET";
	public static final String POST = "POST";
	
	private final String formName;
	private final String actionUrl;
	private final String method;
	
	private final ArrayList formItems = new ArrayList();
	private Hashtable hiddenElememts;
	private final HtmlBrowser browser;
	private final String encoding;
	private final FormListener formListener;
	
	/**
	 * Creates a new HTML form
	 * @param name the name of the form
	 * @param actionUrl the action
	 * @param method the method (get or post)
	 * @param encoding the encoding (is currently not used)
	 * @param browser the browser
	 * @param formListener the listener for form elements
	 */
	public HtmlForm(String name, String actionUrl, String method, String encoding, HtmlBrowser browser, FormListener formListener )
	{
		this.formName = name;
		this.actionUrl = actionUrl;
		this.encoding = encoding;
		this.browser = browser;
		this.formListener = formListener;
		this.method = method.toUpperCase();
	}
	
	public String getAction()
	{
		return this.actionUrl;
	}

	public String getEncoding()
	{
		return this.encoding;
	}

	public String getMethod()
	{
		return this.method;
	}
	
	public boolean isGet() {
		return GET.equals(this.method);
	}
	
	public boolean isPost() {
		return POST.equals(this.method);
	}

	
	/**
	 * Retrieves the (optional) name
	 * @return the name of this form, if specified
	 */
	public String getName() {
		return this.formName;
	}
	
	public void addItem(Item item)
	{
		this.formItems.add(item);
	}
	
	public Item[] getItems()
	{
		return (Item[]) this.formItems.toArray(new Item[this.formItems.size()]);
	}
	
	/**
	 * Adds a hidden element to this form. The hidden element will not be shown.
	 * @param name the name of the hidden element
	 * @param value the value of the hidden element
	 */
	public void addHiddenElement(String name, String value)
	{
		if (this.hiddenElememts == null) {
			this.hiddenElememts = new Hashtable();
		}
		if (value == null) {
			value = "";
		}
		this.hiddenElememts.put( name, value );
	}
	
	/**
	 * Retrieves all form input elements for submitting this form as string-pairs (name:value) in a Hashtable.
	 * @return a hashtable with all input elements
	 */
	public KeyValueList getFormElements() {
		return getFormElements(null, null);
	}
	
	/**
	 * Retrieves all form input elements for submitting this form as string-pairs (name:value) in a Hashtable.
	 * @param listener the form listener that may change the values, can be null
	 * @param submitItem the submitItem that triggered the submission of the form, can be null
	 * @return a key value list with all input elements, some keys may be used several times
	 */
	public KeyValueList getFormElements(FormListener listener, Item submitItem) {
		int size = this.hiddenElememts != null ? this.hiddenElememts.size() + this.formItems.size() : this.formItems.size();
		KeyValueList elements = new KeyValueList(size);
		if (this.hiddenElememts != null) {
			Enumeration enumeration = this.hiddenElememts.keys();
			while (enumeration.hasMoreElements()) {
				String name = (String) enumeration.nextElement();
				String value = (String) this.hiddenElememts.get(name);
				if (listener != null) {
					value = listener.verifySubmitFormValue(this.actionUrl, name, value);
				}
				if (value == null) {
					value = "";
				}
				elements.add( name, value );
			}
		}
		Object[] items = this.formItems.getInternalArray();
		for (int i = 0; i < items.length; i++)
		{
			Item item = (Item) items[i];
			if (item == null) {
				break;
			}
			if ("submit".equals(item.getAttribute(HtmlTagHandler.ATTR_TYPE))
					&& item != submitItem)
			{
				continue;
			}

			String name = (String) item.getAttribute(HtmlTagHandler.ATTR_NAME);
			if (name == null) {
				continue;
			}
			String value = (String) item.getAttribute(HtmlTagHandler.ATTR_VALUE);

			if (item instanceof TextField)
			{
				TextField textField = (TextField) item;
				value = textField.getString();
			}
			else if (item instanceof ChoiceGroup) 
			{
				ChoiceGroup choiceGroup = (ChoiceGroup) item;
				HtmlSelect htmlSelect = (HtmlSelect) choiceGroup.getAttribute(HtmlSelect.SELECT);
				if (htmlSelect != null) {
					value = htmlSelect.getValue(choiceGroup.getSelectedIndex());
				} else {
					boolean[] choices = new boolean[ choiceGroup.size() ];
					choiceGroup.getSelectedFlags(choices);
					for (int j = 0; j < choices.length; j++)
					{
						if ( choices[j] ) {
							Item choiceItem = choiceGroup.get(j);
							elements.add(name, choiceItem.getAttribute(HtmlTagHandler.ATTR_VALUE) );
						}
						
					}
					continue;
				}
			}
			if (listener != null) {
				value = listener.verifySubmitFormValue(this.actionUrl, name, value);
			}
			if (value == null) {
				value = "";
			}
			elements.add(name, value);
			
		}
		return elements;
	}
	
	/**
  	 * Does a Form POST method call.
  	 * @param submitItem the item triggering the call
  	 */
  	protected void doPostSubmitCall(Item submitItem )
  	{
	    StringBuffer postData = new StringBuffer();
	    KeyValueList elements = getFormElements(this.formListener, submitItem);
	    boolean addAnd = false;
		for (int i=0; i<elements.size(); i++) {
			String name = (String) elements.getKey(i);
			String value = (String) elements.getValue(i);
			value = TextUtil.encodeUrl(value);
			if (addAnd) {
				postData.append('&');
			}
			postData.append(name).append('=').append( value );
			addAnd = true;
		}
	    this.browser.go( this.browser.makeAbsoluteURL( this.actionUrl ), postData.toString());
  	}
  	
  	/**
  	 * Creates a Form GET method URL for the specified browser.
  	 * 
  	 * @param submitItem the item that triggered the action
  	 * @return the GET URL or null when the browser's current item is not a Submit button
  	 */
  	protected String createGetSubmitCall(Item submitItem)
  	{

  		StringBuffer sb = new StringBuffer();
  		sb.append( this.browser.makeAbsoluteURL(this.actionUrl) );
  		KeyValueList elements = getFormElements(this.formListener, submitItem);
  		char separatorChar = '?';
  		for (int i=0; i<elements.size(); i++) {
  			String name = (String) elements.getKey(i);
  			String value = (String) elements.getValue(i);
  			value = TextUtil.encodeUrl(value);
  			sb.append(separatorChar);
  			sb.append(name).append('=').append( value );
  			separatorChar = '&';
  		}
  		return sb.toString();
  	}
  	
  	/**
	 * Submits this form without specifying a submission item
	 */
	public void submit()
	{
		submit( (Item)null );
	}

	/**
	 * Submits this form
	 * @param submitItem the form element that triggered the submission, can be null
	 * @throws IllegalArgumentException when the given name of the submit element is not known for this form
	 */
	public void submit(String submitItem)
	{
		if (submitItem == null) {
			submit( (Item)null );
		}
		Object[] items = this.formItems.getInternalArray();
		for (int i = 0; i < items.length; i++)
		{
			Item item = (Item) items[i];
			if (item == null) {
				break;
			}
			if ("submit".equals(item.getAttribute(HtmlTagHandler.ATTR_TYPE))
				&& submitItem.equals( item.getAttribute(HtmlTagHandler.ATTR_NAME))
			) {
				submit( item );
				return;
			}
		}
		throw new IllegalArgumentException();
	}

	/**
	 * Submits this form
	 * @param submitItem the item that has been clicked for triggering the submission, can be null
	 */
	public void submit(Item submitItem)
	{
		if (isPost()) {
  			doPostSubmitCall(submitItem );
  		}
  		else {
  			String url = createGetSubmitCall(submitItem);
  			this.browser.go(url);
  		}
	}

}
