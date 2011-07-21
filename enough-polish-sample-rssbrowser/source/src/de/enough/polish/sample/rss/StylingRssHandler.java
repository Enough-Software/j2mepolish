package de.enough.polish.sample.rss;

import java.io.IOException;

import de.enough.polish.browser.rss.RssBrowser;
import de.enough.polish.browser.rss.RssItem;
import de.enough.polish.browser.rss.RssTagHandler;
import de.enough.polish.event.EventListener;
import de.enough.polish.event.EventManager;
import de.enough.polish.event.GestureEvent;
import de.enough.polish.io.RmsStorage;
import de.enough.polish.ui.Command;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemCommandListener;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.ArrayList;

public class StylingRssHandler 
extends RssTagHandler
implements ItemCommandListener, EventListener
{
	
	private ArrayList visitedUrls;
	private RmsStorage urlsStorage;
	private boolean visitedUrlsReset;
	private Command cmdMarkAsUnread = new Command("Mark unread", Command.ITEM, 5 );
	private Command cmdMarkAsRead = new Command("Mark read", Command.ITEM, 5 );
	

	/**
	 * Creates a new RSS handler
	 * @param browser the original RSS browser
	 */
	public StylingRssHandler(RssBrowser browser) {
		super(browser.getLinkCommand(), browser.getRssItemCommandListener());
		try {
			this.urlsStorage = new RmsStorage(null);
			this.visitedUrls = (ArrayList) this.urlsStorage.read("_urls");
		} catch (Exception e) {
			//#debug info
			System.out.println("Unable to load urls, probably first start" + e);
		}
		if (this.visitedUrls == null) {
			this.visitedUrls = new ArrayList();
		}
		this.cmdMarkAsRead.setItemCommandListener(this);
		this.cmdMarkAsUnread.setItemCommandListener(this);
		
		EventManager.getInstance().addEventListener(GestureEvent.EVENT_GESTURE_SWIPE_LEFT, this);
		EventManager.getInstance().addEventListener(GestureEvent.EVENT_GESTURE_SWIPE_RIGHT, this);
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.browser.rss.RssTagHandler#applyStylingForRssLink(de.enough.polish.ui.Item, int, java.lang.String)
	 */
	protected void applyStylingForRssLink(Item item, int index, String rssUrl) {
		// use a gray background for every second item:
		if ((index & 1) == 1) {
			//#style browserLinkOdd
			UiAccess.setStyle(item);
		}
		//#if polish.css.visited-style
			if (this.visitedUrls.contains(rssUrl)) {
				item.notifyVisited();
				item.addCommand( this.cmdMarkAsUnread );
			} else {
				item.addCommand( this.cmdMarkAsRead );
			}
		//#endif
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.browser.rss.RssTagHandler#onViewUrl(java.lang.String, Item)
	 */
	protected void onViewUrl(String rssUrl, Item item) {
		if (!this.visitedUrlsReset) {
			this.visitedUrlsReset = true;
			if (this.visitedUrls.size() > 30) {
				// clear up old entries:
				this.visitedUrls.clear();
			}
		}
		//#debug
		System.out.println("adding URL " + rssUrl);
		this.visitedUrls.add(rssUrl);
		item.removeCommand(this.cmdMarkAsRead);
		item.addCommand(this.cmdMarkAsUnread);
		super.onViewUrl(rssUrl, item);
	}

	/**
	 * Retrieves the URLs that have been visited during this run of the application
	 * @return a list of URL, may be empty but not null
	 */
	public ArrayList getVisitedUrls() {
		return this.visitedUrls;
	}
	
	/**
	 * Saves the visited URLs upon application exit.
	 * @throws IOException when saving fails
	 */
	public void saveVisitedUrls() throws IOException {
		this.urlsStorage.save(this.visitedUrls, "_urls");
	}

	/**
	 * Handles the read/unread commands
	 */
	public void commandAction(Command cmd, Item item) {
		RssItem rssFeed = (RssItem) item.getAttribute(RssItem.ATTRIBUTE_KEY);
		//#if polish.css.visited-style
			if (cmd == this.cmdMarkAsRead) {
				visit( item, rssFeed );
			} else if (cmd == this.cmdMarkAsUnread) {
				unvisit(item, rssFeed);
			}
		//#endif

	}

	/**
	 * @param item
	 * @param rssFeed
	 */
	private void unvisit(Item item, RssItem rssFeed) {
		item.notifyUnvisited();
		item.removeCommand(this.cmdMarkAsUnread);
		item.addCommand(this.cmdMarkAsRead);
		if (rssFeed != null) {
			this.visitedUrls.remove(rssFeed.getLink());
		}
	}

	private void visit(Item item, RssItem rssFeed) {
		item.notifyVisited();
		item.removeCommand(this.cmdMarkAsRead);
		item.addCommand(this.cmdMarkAsUnread);
		if (rssFeed != null) {
			this.visitedUrls.add(rssFeed.getLink());
		}
	}

	public void handleEvent(String name, Object source, Object evntData) {		
		Item item = (Item) source;
		RssItem feedData = (RssItem) item.getAttribute(RssItem.ATTRIBUTE_KEY);
		if (feedData != null) {
			if (GestureEvent.EVENT_GESTURE_SWIPE_LEFT.equals(name)) {
				unvisit(item, feedData);
			} else {
				visit(item, feedData);
			}
			GestureEvent event = (GestureEvent) evntData;
			event.setHandled();
		}
		
	}
	

}
