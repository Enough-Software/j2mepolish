//#condition polish.usePolishGui

/*
 * Created on 24-Apr-2007 at 19:20:28.
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
package de.enough.polish.browser.rss;

import de.enough.polish.browser.html.HtmlBrowser;
import de.enough.polish.browser.html.HtmlTagHandler;
import de.enough.polish.ui.Command;
import de.enough.polish.ui.CommandListener;
import de.enough.polish.ui.Displayable;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemCommandListener;
import de.enough.polish.ui.Style;

/**
 * The RSS browser is like the HTML browser, but it additionally supports RSS 2.0 feeds.
 * 
 * @author Michael Koch
 * @author Robert Virkus
 *
 */
public class RssBrowser
	extends HtmlBrowser
	implements CommandListener
{
	private ItemCommandListener rssItemCommandListener;
	private RssTagHandler rssTagHandler;
	private Command linkCommand = HtmlTagHandler.CMD_LINK;

	/**
	 * Creates a new RSS reader
	 */
	public RssBrowser() {
		this((Style) null);
	}

	/**
	 * Creates a new RSS reader.
	 * @param style the style, typically defined using a #style preprocessing directive
	 */
	public RssBrowser(Style style) {
		this(new DefaultRssItemCommandListener(), style); 
	}

	//#if polish.LibraryBuild
	/**
	 * Creates a new RSS reader
	 * @param listener the command listener
	 */
	public RssBrowser(javax.microedition.lcdui.ItemCommandListener listener) {
		this( listener, null ); 
	}
	//#endif

	//#if polish.LibraryBuild
	/**
	 * Creates a new RSS reader.
	 * @param listener the command listener
	 * @param style the style, typically defined using a #style preprocessing directive
	 */
	public RssBrowser(javax.microedition.lcdui.ItemCommandListener listener, Style style ) {
		super( style );
		// nothing done here
	}
	//#endif
	
	/**
	 * Creates a new RSS reader.
	 * @param listener the command listener
	 */
	public RssBrowser(ItemCommandListener listener)
	{
		this( listener, null );
	}

	/**
	 * Creates a new RSS reader.
	 * @param listener the command listener
	 * @param style the style, typically defined using a #style preprocessing directive
	 */
	public RssBrowser(ItemCommandListener listener, Style style)
	{
		super(style);
		this.rssItemCommandListener = listener;
		this.rssTagHandler = new RssTagHandler(getLinkCommand(), getRssItemCommandListener()); 
		this.rssTagHandler.register(this);
		if (listener instanceof DefaultRssItemCommandListener) {
			DefaultRssItemCommandListener rssListener = (DefaultRssItemCommandListener) listener;
			rssListener.setRssBrowser(this);
			rssListener.setCommandListener(this);
		}
	}

	/**
	 * @return the ItemCommandListener for the RssItems
	 */
	public ItemCommandListener getRssItemCommandListener() {
		return this.rssItemCommandListener;
	}

	/**
	 * @return the Command triggered by links
	 */
	public Command getLinkCommand() {
		return this.linkCommand  ;
	}
	
	/**
	 * Sets the command that is used for opening &lt;a href...&gt; links. 
	 * @param link the new command
	 */
	public void setLinkCommand( Command link ) {
		this.linkCommand = link;
		this.rssTagHandler.setLinkCommand( link );
	}
	
	
	/**
	 * Sets a handler different from the default RSS tag handler.
	 * @param handler the new RSS tag handler.
	 * @throws NullPointerException when handler is null
	 */
	public void setRssTagHandler( RssTagHandler handler ) {
		this.rssTagHandler = handler;
		handler.register(this);
	}

	/**
	 * Tries to handle the specified command.
	 * The item checks if the command belongs to this item and if it has an associated ItemCommandListener.
	 * Only then it handles the command.
	 * @param command the command
	 * @return true when the command has been handled by this item
	 */
	public boolean handleCommand(Command command)
	{
		if (getRssItemCommandListener() != null
			&& command == RssTagHandler.CMD_GO_TO_ARTICLE) 
		{
			Item rssItem = getFocusedItem();
			String rssUrl = (String) rssItem.getAttribute(HtmlTagHandler.ATTR_HREF);
			if (rssUrl != null) {
				this.rssTagHandler.onViewUrl(rssUrl, rssItem);
			}
			getRssItemCommandListener().commandAction(command, rssItem);
			return true;
		}

		return super.handleCommand(command);
	}
	

	/**
	 * Tries to handle the specified command.
	 * The item checks if the command belongs to this item and if it has an associated ItemCommandListener.
	 * Only then it handles the command.
	 * @param command the command
	 * @param displayable the displayable
	 */
	public void commandAction(Command command, Displayable displayable)
	{
		handleCommand(command);
	}

	
	//#if polish.LibraryBuild
		/**
		 * Tries to handle the specified command.
		 * The item checks if the command belongs to this item and if it has an associated ItemCommandListener.
		 * Only then it handles the command.
		 * @param cmd the command
		 * @param displayable the displayable
		 */
		public boolean commandAction( javax.microedition.lcdui.Command cmd, javax.microedition.lcdui.Displayable displayable ) {
			return false;
		}
	//#endif


	/**
	 * Determines whether RSS descriptions should be included directly on the overview page
	 * 
	 * @return true when descriptions should be included
	 */
	public boolean isIncludeDescriptions() {
		if (this.rssTagHandler != null) {
			return this.rssTagHandler.isIncludeDescriptions();
		} else {
			return false;
		}
	}
	

	/**
	 * Specifies whether RSS descriptions should be included directly on the overview page
	 * @param includeDescriptions true when descriptions should be included
	 */
	public void setIncludeDescriptions(boolean includeDescriptions)
	{
		if (this.rssTagHandler != null) {
			this.rssTagHandler.setIncludeDescriptions(includeDescriptions);
		}
	}

	/**
	 * Retrieves the RSS Tag Handeler
	 * @return the tag handler
	 */
	public RssTagHandler getRssTagHandler() {
		return this.rssTagHandler;
	}
}
