//#condition polish.usePolishGui
/*
 * Created on May 7, 2007 at 12:09:44 PM.
 * 
 * Copyright (c) 2009 - 2009 Robert Virkus / Enough Software
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

import javax.microedition.lcdui.AlertType;

import de.enough.polish.browser.html.HtmlTagHandler;
import de.enough.polish.ui.Alert;
import de.enough.polish.ui.Command;
import de.enough.polish.ui.CommandListener;
import de.enough.polish.ui.Displayable;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemCommandListener;
import de.enough.polish.ui.StyleSheet;
import de.enough.polish.ui.UiAccess;

/**
 * <p>Displays an alert showing the description of the currently selected RSS item.</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        May 7, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DefaultRssItemCommandListener implements CommandListener, ItemCommandListener {

	private RssBrowser rssBrowser;
	private CommandListener commandListener;
	private String url;

	public void setRssBrowser(RssBrowser rssBrowser)
	{
		this.rssBrowser = rssBrowser;
	}

	public void setCommandListener(CommandListener commandListener)
	{
		this.commandListener = commandListener;
	}

	public void commandAction(Command command, Displayable displayable)
	{
		if (command == RssTagHandler.CMD_GO_TO_ARTICLE) {
			this.rssBrowser.go(this.url);
			this.url = null;
			StyleSheet.display.setCurrent(this.rssBrowser.getScreen());
		}
		else if (command == HtmlTagHandler.CMD_BACK) {
			this.rssBrowser.goBack();
			StyleSheet.display.setCurrent(this.rssBrowser.getScreen());
		}
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.ItemCommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Item)
	 */
	public void commandAction(Command command, Item item) {
		//System.out.println("DefaultRssItemCommandListner: command ="+ command.getLabel() );
		if (command == RssTagHandler.CMD_RSS_ITEM_SELECT) {
			RssItem rssItem = (RssItem) UiAccess.getAttribute(item, RssItem.ATTRIBUTE_KEY);
			String rssUrl = rssItem.getLink();
			if (rssUrl != null && this.rssBrowser != null) {
				this.rssBrowser.getRssTagHandler().onViewUrl( rssUrl, item );
			}

			if (rssItem != null && StyleSheet.display != null) {
				//#style rssDescriptionAlert
				Alert alert = new Alert( rssItem.getTitle(), rssItem.getDescription(), null, AlertType.INFO);
				alert.setTimeout(Alert.FOREVER);
				alert.addCommand(RssTagHandler.CMD_GO_TO_ARTICLE);
				alert.addCommand(HtmlTagHandler.CMD_BACK);
				alert.setCommandListener(this);
				StyleSheet.display.setCurrent(alert);
				this.url = rssItem.getLink();
			}
		} else {
			this.rssBrowser.handleCommand(command);
		}
	}
}
