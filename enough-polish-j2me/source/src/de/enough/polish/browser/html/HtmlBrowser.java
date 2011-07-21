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
import de.enough.polish.browser.ProtocolHandler;
import de.enough.polish.browser.protocols.HttpProtocolHandler;
import de.enough.polish.browser.protocols.ResourceProtocolHandler;
import de.enough.polish.io.RedirectHttpConnection;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.StringTokenizer;
import de.enough.polish.util.TextUtil;
import de.enough.polish.xml.SimplePullParser;

/**
 * TODO: Write good docs.
 * 
 * polish.Browser.UserAgent
 * polish.Browser.MaxRedirects
 * polish.Browser.Gzip
 * polish.Browser.POISupport
 * polish.Browser.PaintDownloadIndicator
 * 
 * @see HttpProtocolHandler
 * @see ResourceProtocolHandler
 * @see RedirectHttpConnection
 */
public class HtmlBrowser
extends Browser
{

	protected HtmlTagHandler htmlTagHandler;
	protected final ArrayList forms;

	/**
	 * Creates a new browser using the default ".browser" style and default tag- and protocol handlers.
	 */
	public HtmlBrowser()
	{
		//#if polish.css.style.browser && !polish.LibraryBuild
			//#style browser
			//# this();	
		//#else
			this( (Style) null );
		//#endif
	}

	/**
	 * Creates a new browser with the given style, the default tag handler and default protocol handlers (http, https, resource)
	 * 
	 * @param style the style
	 * @see #getDefaultProtocolHandlers()
	 * @see HtmlTagHandler
	 */
	public HtmlBrowser( Style style )
	{
		this( new HtmlTagHandler(), getDefaultProtocolHandlers(), style );
	}

	/**
	 * Creates a new browser with the specified html tag handler
	 * 
	 * @param tagHandler the HtmlTagHandler used for this browser
	 * @param protocolHandlers the protocol handlers
	 * 
	 * @throws NullPointerException when the tagHandler is null
	 */
	public HtmlBrowser( HtmlTagHandler tagHandler, ProtocolHandler[] protocolHandlers )
	{
		//#if polish.css.style.browser && !polish.LibraryBuild
			//#style browser
			//# this( tagHandler, protocolHandlers );	
		//#else
			this( tagHandler, protocolHandlers, (Style) null );
		//#endif	  
	}

	/**
	 * Creates a new browser with the specified html tag handler
	 * 
	 * @param tagHandler the HtmlTagHandler used for this browser
	 * @param protocolHandlers the protocol handlers
	 * @param style the style of this browser
	 * 
	 * @throws NullPointerException when the tagHandler is null
	 */
	public HtmlBrowser( HtmlTagHandler tagHandler, ProtocolHandler[] protocolHandlers,  Style style )
	{
		super( protocolHandlers, style );
		tagHandler.register(this);
		this.htmlTagHandler = tagHandler;
		this.forms = new ArrayList();
	}

	/**
	 * Sets the form listener that is notified about form creation and submission events
	 * 
	 * @param listener the listener, use null for de-registering a previous listener
	 */
	public void setFormListener( FormListener listener ) {
		this.htmlTagHandler.setFormListener( listener );
	}


	protected void handleText(String text)
	{
		if (text.length() > 0)
		{
			StringTokenizer st = new StringTokenizer(text, " \r\n\t");

			while (st.hasMoreTokens())
			{
				String str = st.nextToken();
				str = TextUtil.replace(str, "&nbsp;", " ");
				StringItem stringItem = null;
				if (this.htmlTagHandler.textStyle != null) {
					stringItem = new StringItem(null, str, this.htmlTagHandler.textStyle);
				} 
				else 
					if (this.htmlTagHandler.textBold && this.htmlTagHandler.textItalic)
					{
						//#style browserTextBoldItalic
						stringItem = new StringItem(null, str);
					}
					else if (this.htmlTagHandler.textBold)
					{
						//#style browserTextBold
						stringItem = new StringItem(null, str);
					}
					else if (this.htmlTagHandler.textItalic)
					{
						//#style browserTextItalic
						stringItem = new StringItem(null, str);
					}
					else
					{
						//#style browserText
						stringItem = new StringItem(null, str);
					}
				add(stringItem);
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.browser.Browser#parsePage(de.enough.polish.xml.SimplePullParser)
	 */
	protected void parsePage(SimplePullParser parser)
	{
		this.forms.clear();
		super.parsePage(parser);
	}

	/**
	 * Retrieves the form with the specified name
	 * @param name the name of the HTML form
	 * @return the form or null when no form with this name is defined in the current page
	 */
	public HtmlForm getForm( String name) {
		for (int i=0; i<this.forms.size(); i++) {
			HtmlForm form = (HtmlForm) this.forms.get(i);
			if (name.equals(form.getName())) {
				return form;
			}
		}
		return null;
	}
	
	/**
	 * Retrieves the form with the specified index
	 * @param index the index of the HTML form
	 * @return the form
	 * @throws ArrayIndexOutOfBoundsException when the index is invalid
	 */
	public HtmlForm getForm( int index ) {
		return (HtmlForm) this.forms.get(index);
	}
	
	/**
	 * Retrieves the number of forms within the current page.
	 * @return the number of forms in the current page
	 */
	public int getNumberOfForms() {
		return this.forms.size();
	}

	/**
	 * Retrieves all forms of the current page
	 * @return an array of all forms, can be empty but not null
	 */
	public HtmlForm[] getForms() {
		return (HtmlForm[]) this.forms.toArray( new HtmlForm[ this.forms.size() ] );
	}

	/**
	 * Submits the specified form
	 * 
	 * @param index the index of the form
	 * @throws ArrayIndexOutOfBoundsException when the index is invalid
	 */
	public void submitForm( int index ) {
		submitForm( index, null);
	}
	
	/**
	 * Submits the specified form
	 * 
	 * @param index the index of the form
	 * @param submitElement the form element that triggered the submission, can be null
	 * @throws IllegalArgumentException when the given name of the submit element is not known for the specified form
	 * @throws ArrayIndexOutOfBoundsException when the index is invalid
	 */
	public void submitForm( int index, String submitElement) {
		getForm(index).submit( submitElement );
	}
	
	/**
	 * Submits the specified form
	 * 
	 * @param name the name of the HTML form
	 * @throws ArrayIndexOutOfBoundsException when the index is invalid
	 */
	public void submitForm( String name ) {
		submitForm( name, null);
	}
	
	/**
	 * Submits the specified form
	 * 
	 * @param name the name of the HTML form
	 * @param submitElement the form element that triggered the submission, can be null
	 * @throws IllegalArgumentException when the given name of the submit element is not known for the specified form
	 * @throws NullPointerException when a form with the given name is not known
	 */
	public void submitForm( String name, String submitElement) {
		getForm(name).submit( submitElement );
	}
	
	/**
	 * Adds the specified form to the list of forms for the current page.
	 * @param form the to be added form
	 */
	protected void addForm( HtmlForm form) {
		this.forms.add( form );
	}

}
