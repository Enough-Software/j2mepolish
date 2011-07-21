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
package de.enough.polish.browser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;

import javax.microedition.io.HttpConnection;
import javax.microedition.io.StreamConnection;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.browser.protocols.HttpProtocolHandler;
import de.enough.polish.browser.protocols.ResourceProtocolHandler;
import de.enough.polish.io.RedirectHttpConnection;
import de.enough.polish.io.ResourceLoader;
import de.enough.polish.io.StringReader;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.HashMap;
import de.enough.polish.util.zip.GZipInputStream;
import de.enough.polish.xml.SimplePullParser;
import de.enough.polish.xml.XmlPullParser;

//#if polish.LibraryBuild
import de.enough.polish.ui.FakeContainerCustomItem;
//#endif

import de.enough.polish.ui.AnimationThread;
import de.enough.polish.ui.ClippingRegion;
import de.enough.polish.ui.Command;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.Gauge;
import de.enough.polish.ui.ImageItem;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;
import de.enough.polish.ui.UiAccess;

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
 * 
 * @author Michael Koch
 */
public abstract class Browser
//#if polish.LibraryBuild
extends FakeContainerCustomItem
//#else
//# extends Container
//#endif
implements Runnable, ResourceLoader
{
	private static final String BROKEN_IMAGE = "resource://broken.png";

	private HashMap imageCache = new HashMap();

	protected String currentDocumentBase = null;
	protected HashMap protocolHandlersByProtocol = new HashMap();
	protected HashMap tagHandlersByTag = new HashMap();
	protected ArrayList tagHandlers = new ArrayList();

	protected Stack history = new Stack();
	//#if polish.Browser.PaintDownloadIndicator
	protected Gauge loadingIndicator;
	private boolean isStoppedWorking;
	//#endif

	private Thread loadingThread;
	private boolean isRunning;
	private boolean isWorking;
	private boolean isCancelRequested;
	private String nextUrl;
	private String nextPostData;
	protected BrowserListener browserListener;

	/**
	 * Currently used container for storing parsing results.
	 */
	protected Container currentContainer;

	private Command cmdBack;

	private HistoryEntry scheduledHistoryEntry;

	protected String cookie;

	protected boolean allowHtmlEntitiesInAttributes;

	private Hashtable imagesToLoad;
	protected Hashtable cssStyles;

	private Hashtable cachedConnections;

	/**
	 * Creates a new Browser without any protocol handlers, tag handlers or style.
	 */
	public Browser()
	{
		//#style browser?
		this( (String[])null, (TagHandler[])null, (ProtocolHandler[]) null );
	}

	/**
	 * Creates a new Browser without any protocol handler or tag handlers.
	 * 
	 * @param style the style of this browser
	 */
	public Browser( Style style )
	{
		this( (String[])null, (TagHandler[])null, (ProtocolHandler[]) null, style );
	}

	/**
	 * Creates a new Browser with the specified handlers and style.
	 * 
	 * @param protocolHandlers the tag handlers
	 */
	public Browser( ProtocolHandler[] protocolHandlers )
	{
		//#if polish.css.style.browser && !polish.LibraryBuild
		//#style browser
		//# this(protocolHandlers);
		//#else
		this(protocolHandlers, null);
		//#endif
	}

	/**
	 * Creates a new Browser with the specified handlers and style.
	 * 
	 * @param protocolHandlers the tag handlers
	 * @param style the style to use for the browser item
	 */
	public Browser( ProtocolHandler[] protocolHandlers, Style style )
	{
		this( (String[])null, (TagHandler[])null, protocolHandlers, style);
	}

	/**
	 * Creates a new Browser with the specified handlers and style.
	 * 
	 * @param tagNames the names of the tags that the taghandler should handle (this allows to use a single taghandler for several tags)
	 * @param tagHandlers the tag handlers
	 * @param protocolHandlers the protocol handlers
	 */
	public Browser( String[] tagNames, TagHandler[] tagHandlers, ProtocolHandler[] protocolHandlers )
	{
		//#if polish.css.style.browser && !polish.LibraryBuild
		//#style browser
		//# this(tagNames, tagHandlers, protocolHandlers);
		//#else
		this(tagNames,tagHandlers, protocolHandlers, null);
		//#endif
	}

	/**
	 * Creates a new Browser with the specified handlers and style.
	 * 
	 * @param tagNames the names of the tags that the taghandler should handle (this allows to use a single taghandler for several tags)
	 * @param tagHandlers the tag handlers
	 * @param protocolHandlers the protocol handlers
	 * @param style the style of this browser
	 */
	public Browser( String[] tagNames, TagHandler[] tagHandlers, ProtocolHandler[] protocolHandlers, Style style )
	{
		super( true, style );
		if (tagHandlers != null && tagNames != null && tagNames.length == tagHandlers.length) {
			for (int i = 0; i < tagHandlers.length; i++) {
				TagHandler handler = tagHandlers[i];
				addTagHandler(tagNames[i], handler);
			}
		}
		if (protocolHandlers != null) {
			for (int i = 0; i < protocolHandlers.length; i++) {
				ProtocolHandler handler = protocolHandlers[i];
				addProtocolHandler( handler );

			}
		}
		//#if polish.Browser.PaintDownloadIndicator
		//#style browserDownloadIndicator
		this.loadingIndicator = new Gauge(null, false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING);
		this.loadingIndicator.setParent(this);
		//#endif
		this.loadingThread = new Thread( this );
		this.loadingThread.start();
	}

	/**
	 * Instantiates and returns the default tag handlers for "http", "https" and "resource" URLs.
	 * 
	 * @return new default tag handlers
	 * @see HttpProtocolHandler
	 * @see ResourceProtocolHandler
	 */
	public static ProtocolHandler[] getDefaultProtocolHandlers() {
		HashMap httpRequestProperties = new HashMap();

		//#if polish.Browser.Gzip
		httpRequestProperties.put("Accept-Encoding", "gzip");
		//#endif

		return new ProtocolHandler[] {
				new HttpProtocolHandler("http", httpRequestProperties),
				new HttpProtocolHandler("https", httpRequestProperties),
				new ResourceProtocolHandler("resource")
		};  
	}

	public void addTagCommand(String tagName, Command command)
	{
		TagHandler handler = getTagHandler(tagName);

		if (handler != null )
		{
			handler.addTagCommand( tagName, command );
		}
	}

	public void addAttributeCommand(String attributeName, String attributeValue, Command command)
	{
		addAttributeCommand(null, attributeName, attributeValue, command);
	}

	public void addAttributeCommand(String tagName, String attributeName, String attributeValue, Command command)
	{
		TagHandler handler = getTagHandler(tagName);

		if (handler != null )
		{
			handler.addAttributeCommand( tagName, attributeName, attributeValue, command );
		}
	}

	public void addProtocolHandler(ProtocolHandler handler)
	{
		this.protocolHandlersByProtocol.put(handler.getProtocolName(), handler);
	}

	public void addProtocolHandler(String protocolName, ProtocolHandler handler)
	{
		this.protocolHandlersByProtocol.put(protocolName, handler);
	}

	protected ProtocolHandler getProtocolHandler(String protocolName)
	{
		return (ProtocolHandler) this.protocolHandlersByProtocol.get(protocolName);
	}

	protected ProtocolHandler getProtocolHandlerForURL(String url)
	throws IOException
	{
		if (url.length() > 1 && url.charAt(0) == '/') {
			url = protocolAndHostOf(this.currentDocumentBase) + url;
		}
		int pos = url.indexOf(':');
		if (pos == -1) {
			throw new IOException("malformed url");
		}
		String protocol = url.substring(0, pos);
		ProtocolHandler handler = (ProtocolHandler) this.protocolHandlersByProtocol.get(protocol);
		if (handler == null) {
			throw new IOException("protocol handler [" + protocol + "] not found for " + url);
		}
		return handler;
	}

	public void addTagHandler(String tagName, TagHandler handler)
	{
		this.tagHandlersByTag.put(new TagHandlerKey(tagName.toLowerCase()), handler);
		if (!this.tagHandlers.contains(handler)) {
			this.tagHandlers.add(handler);
		}    
	}

	public void addTagHandler(String tagName, String attributeName, String attributeValue, TagHandler handler)
	{
		TagHandlerKey key = new TagHandlerKey(tagName, attributeName, attributeValue);

		//#debug
		System.out.println("Browser.addTagHandler: adding key: " + key);

		this.tagHandlersByTag.put(key, handler);
		if (!this.tagHandlers.contains(handler)) {
			this.tagHandlers.add(handler);
		}
	}

	public TagHandler getTagHandler(String tagName)
	{
		TagHandlerKey key = new TagHandlerKey(tagName);
		return (TagHandler) this.tagHandlersByTag.get(key);
	}

	public TagHandler getTagHandler(String tagName, String attributeName, String attributeValue)
	{
		TagHandlerKey key = new TagHandlerKey(tagName, attributeName, attributeValue);
		return (TagHandler) this.tagHandlersByTag.get(key);
	}

	/**
	 * Opens a new Container into which forthcoming elements should be added.
	 * 
	 * @param containerStyle the style of the container
	 */
	public void openContainer(Style containerStyle)
	{
		Container previousContainer = this.currentContainer;
		if (containerStyle == null) {
			if (previousContainer != null) {
				containerStyle = previousContainer.getStyle();
			} else {
				containerStyle = getStyle();
			}
		}
		//#debug
		System.out.println("Opening nested container with style " + (containerStyle == null ? "<null>" : containerStyle.name));
		openContainer( new Container( false, containerStyle ) );
	}

	/**
	 * Opens a new Container into which forthcoming elements should be added.
	 * 
	 * @param container the new the container
	 */
	public void openContainer(Container container)
	{
		//		Container cont = this.currentContainer;
		//		while (cont != null) {
		//			System.out.print(" ");
		//			if (cont.getParent() instanceof Container) {
		//				cont = (Container) cont.getParent();
		//			} else {
		//				cont = null;
		//			}
		//		}
		//#debug
		System.out.println("Opening nested container " + container);

		Container previousContainer = this.currentContainer;
		if (previousContainer != null) {
			container.setParent( previousContainer );
		} else {
			container.setParent( this );
		}
		//add(container);
		this.currentContainer = container;
	}

	//#if polish.LibraryBuild
	/**
	 * Opens a new Container into which forthcoming elements should be added.
	 * 
	 * @param container the new the container
	 */
	public void openContainer(FakeContainerCustomItem container)
	{
		// ignore
	}
	//#endif
	
	/**
	 * Retrieves the current container.
	 * @return the current container
	 */
	public Container getCurrentContainer() {
		return this.currentContainer;
	}
	
	/**
	 * Removes the current container without adding it.
	 * @return the current container
	 */
	public Container removeCurrentContainer() {
		Container current = this.currentContainer;
		Container previousContainer = (Container) current.getParent();
		if (previousContainer == UiAccess.cast(this)) {
			this.currentContainer = null;
		} else {
			this.currentContainer = previousContainer;
		}
		return current;
	}

	/**
	 * Closes the current container
	 * 
	 * If the current container only contains a single item, that item will be extracted and directly appended using the current container's style.
	 * @return the previous container, if any is known
	 */
	public Container closeContainer() {
		if (this.currentContainer == null) {
			return null;
		}
		//		Container cont = this.currentContainer;
		//		while (cont != null) {
		//			System.out.print(" ");
		//			if (cont.getParent() instanceof Container) {
		//				cont = (Container) cont.getParent();
		//			} else {
		//				cont = null;
		//			}
		//		}
		//		System.out.println("closing container " + this.currentContainer + " with size " + this.currentContainer.size() );
		//#debug
		System.out.println("closing container with " + this.currentContainer.size() + " items, previous=" + this.currentContainer.getParent());
		Container current = this.currentContainer;
		Container previousContainer = (Container) current.getParent();
		if (previousContainer == UiAccess.cast(this)) {
			this.currentContainer = null;
		} else {
			this.currentContainer = previousContainer;
		}

		//System.out.println("closing container with size " + current.size() + ", 0=" + current.get(0));
		Object lock = getSynchronizationLock();
		synchronized (lock) {
			if (current.size() == 1) {
				Item item = current.get(0);
				if (item != null) {
					if (current.getStyle() != null) {
						item.setStyle( current.getStyle() );
					}
					//previousContainer.remove(current);
					add( item );
				}
			} else {
				add(current);
			}
		}
		return previousContainer;
	}

	/**
	 * 
	 */
	private void closeContainers()
	{
		while (this.currentContainer != null) {
			closeContainer();
		}
	}


	/**
	 * @param parser the parser to read the page from
	 */
	protected void parsePage(SimplePullParser parser)
	{
		// Clear out all items in the browser.
		clear();

		// Clear image cache when visiting a new page.
		this.imageCache.clear();

		if (this.imagesToLoad != null) {
			this.imagesToLoad.clear();
		}

		// Really free memory.
		System.gc();

		parsePartialPage(parser);

		Object o = this.currentContainer;
		while ( o != null && o != this ) {
			//System.out.println("closing container with " + this.currentContainer.size() );
			closeContainer();
			o = this.currentContainer;
		}

		if (this.imagesToLoad != null) {
			Enumeration e = this.imagesToLoad.keys();

			while (e.hasMoreElements()) {
				ImageItem imageItem = (ImageItem) e.nextElement();
				String url = (String) this.imagesToLoad.remove(imageItem);
				Image image = loadImage(url);
				imageItem.setImage(image);
			}
		}
	}

	/**
	 * @param parser the parser to read the page from
	 */
	private void parsePartialPage(SimplePullParser parser)
	{
		HashMap attributeMap = new HashMap();
		boolean openingTag;
		while (parser.next() != SimplePullParser.END_DOCUMENT)
		{
			int type = parser.getType();
			openingTag = (type == SimplePullParser.START_TAG);
			if (openingTag || type == SimplePullParser.END_TAG)
			{

				// #debug
				//System.out.println( "looking for handler for " + parser.getName()  + ", openingTag=" + openingTag );
				attributeMap.clear();
				TagHandler handler = getTagHandler(parser, attributeMap);

				if (handler != null)
				{
					// #debug
					//System.out.println("Calling handler: " + parser.getName() + " " + attributeMap);
					Style tagStyle = getStyle( attributeMap );
					Container container =  this.currentContainer;
					if (container == null) {
						container = (Container) ((Object)this);
					}
					handler.handleTag(container, parser, parser.getName(), openingTag, attributeMap, tagStyle);
				}
				else
				{
					//#debug
					System.out.println( "no handler for " + parser.getName() );
				}
			}
			else if (type == SimplePullParser.TEXT)
			{
				handleText(parser.getText().trim());
			}
			else
			{
				//#debug error
				System.out.println("unknown type: " + type + ", name=" + parser.getName());
			}
		} // end while (parser.next() != PullParser.END_DOCUMENT)

		//#debug
		System.out.println("end of document...");
	}

	/**
	 * Retrieves a style from the specfied attributes
	 * @param attributeMap the attribute map
	 * @return the specified style, if any
	 */
	protected Style getStyle( HashMap attributeMap ) {
		Style tagStyle = null;
		String styleName = (String) attributeMap.get("class");
		if (styleName != null) {
			if (this.cssStyles != null) {
				tagStyle = (Style) this.cssStyles.get(styleName);
			}
			if (tagStyle == null) {
				tagStyle = StyleSheet.getStyle(styleName);
			}
		}
		if (tagStyle == null || styleName == null) {
			styleName = (String) attributeMap.get("id");
			if (styleName != null) {
				if (this.cssStyles != null) {
					tagStyle = (Style) this.cssStyles.get(styleName);
				}
				if (tagStyle == null) {
					tagStyle = StyleSheet.getStyle(styleName);
				}
			}
		}
		return tagStyle;
	}

	/**
	 * Retrieves all dynamically registered styles
	 * @return the styles in a Hashtable&lt;String name, Style value&gt; form
	 */
	public Hashtable getCssStyles() {
		return this.cssStyles;
	}

	//	
	//	
	//	public void setStyle(Style style) {
	//		System.out.println("BROWSER: SETTING STYLE " + style.name);
	//		super.setStyle(style);
	//		
	//	}

	/**
	 * Sets dynamically registered styles
	 * @param table the styles in a Hashtable&lt;String name, Style value&gt; form
	 */
	public void setCssStyles(Hashtable table) {
		this.cssStyles = table;
	}

	private TagHandler getTagHandler(SimplePullParser parser, HashMap attributeMap)
	{
		TagHandlerKey key;
		TagHandler handler = null;
		String name = parser.getName().toLowerCase();

		for (int i = 0; i < parser.getAttributeCount(); i++)
		{
			String attributeName = parser.getAttributeName(i).toLowerCase();
			String attributeValue = parser.getAttributeValue(i);
			attributeMap.put(attributeName, attributeValue);

			key = new TagHandlerKey(name,
					attributeName,
					attributeValue);
			handler = (TagHandler) this.tagHandlersByTag.get(key);

			if (handler != null)
			{
				break;
			}
		}

		if (handler == null)
		{
			key = new TagHandlerKey(name);
			handler = (TagHandler) this.tagHandlersByTag.get(key);
		}
		return handler;
	}

	/**
	 * Handles normal text.
	 *  
	 * @param text the text
	 */
	protected abstract void handleText(String text);



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.FakeContainerCustomItem#add(de.enough.polish.ui.Item)
	 */
	public void add(Item item)
	{
		if (this.currentContainer != null) {
			this.currentContainer.add(item);
		} else {
			super.add(item);
		}
	}


	//#if polish.LibraryBuild
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.FakeContainerCustomItem#add(de.enough.polish.ui.Item)
	 */
	public void add(javax.microedition.lcdui.Item item)
	{
		// ignore
	}
	//#endif


	/**
	 * Loads a page from a given <code>Reader</code>.
	 * 
	 * @param reader the reader to load the page from
	 * @throws IOException of an error occurs
	 */
	public void loadPage(Reader reader)
	throws IOException
	{
		XmlPullParser xmlReader = new XmlPullParser(reader, this.allowHtmlEntitiesInAttributes);
		xmlReader.relaxed = true;
		parsePage(xmlReader);
	}

	/**
	 * Loads a page from a given <code>Reader</code>.
	 * 
	 * @param reader the reader to load the page from
	 * @throws IOException of an error occurs
	 */
	public void loadPartialPage(Reader reader)
	throws IOException
	{
		XmlPullParser xmlReader = new XmlPullParser(reader, this.allowHtmlEntitiesInAttributes);
		xmlReader.relaxed = true;
		parsePartialPage(xmlReader);
	}

	/**
	 * Specifies whether the browser should allow HTML entities like &amp; in tag attributes.
	 * This is disabled by default. Note that you need to use HTML entities when setting
	 * to true, e.g. in href attributes of &lt;a&gt; tags.
	 * 
	 * @param allow true when HTML entities should be allowed in tags
	 */
	public void setAllowHtmlEntitiesInAttributes(boolean allow) {
		this.allowHtmlEntitiesInAttributes = allow;
	}

	/**
	 * Determines whether the browser should allow HTML entities like &amp; in tag attributes.
	 * This is disabled by default. Note that you need to use HTML entities when setting
	 * to true, e.g. in href attributes of &lt;a&gt; tags.
	 * 
	 * @return allow true when HTML entities should be allowed in tags
	 */
	public boolean isAllowHtmlEntitiesInAttributes() {
		return this.allowHtmlEntitiesInAttributes;
	}

	/**
	 * "http://foo.bar.com/baz/blah.html" => "http://foo.bar.com"
	 * <p>
	 * "resource://baz/blah.html" => "resource://"
	 * 
	 * @param url the URL to the get protocol and host part from
	 * @return the protocol and host part from the given URL
	 */
	protected String protocolAndHostOf(String url)
	{
		if ("resource://".regionMatches(true, 0, url, 0, 11))
		{
			return "resource://";
		}
		else if ("http://".regionMatches(true, 0, url, 0, 7))
		{
			int hostStart = url.indexOf("//");
			// figure out what error checking to do here
			hostStart+=2;
			// look for next '/'. If none, assume rest of string is hostname
			int hostEnd = url.indexOf("/", hostStart);

			if (hostEnd != -1)
			{
				return url.substring(0, hostEnd);
			}
			else
			{
				return url;
			}
		}
		else
		{
			// unsupported protocol
			return url;
		}
	}

	/**
	 * Takes a possibly relative URL, and generate an absolute URL, merging with
	 * the current documentbase if needed.
	 * 
	 * <ol>
	 * <li> If URL starts with http:// or resource:// leave it alone
	 * <li> If URL starts with '/', prepend document base protocol and host name.
	 * <li> Otherwise, it's a relative URL, so prepend current document base and
	 * directory path.
	 * </ol>
	 * 
	 * @param url the (possibly relative) URL
	 * @return absolute URL
	 */
	public String makeAbsoluteURL(String url)
	{
		//#debug debug
		System.out.println("makeAbsoluteURL: currentDocumentBase = " + this.currentDocumentBase);

		// If no ":", assume it's a relative link, (no protocol),
		// and append current page
		if (url.indexOf("://") != -1)
		{
			return url;
		}
		else if (url.startsWith("/"))
		{
			if ("resource://".regionMatches(true, 0, this.currentDocumentBase, 0, 11))
			{
				// we need to strip a leading slash if it's a local resource, i.e.,
				// "resource://" + "/foo.png" => "resource://foo.png"
				return protocolAndHostOf(this.currentDocumentBase) + url.substring(1);
			}
			else
			{
				// for HTTP, we don't need to strip the leading slash, i.e.,
				// "http://foo.bar.com" + "/foo.png" => "http://foo.bar.com/foo.png"
				return protocolAndHostOf(this.currentDocumentBase) + url;
			}
		} 
		else
		{
			// It's a relative url, so merge it with the current document path:
			String baseUrl = this.currentDocumentBase;
			if (baseUrl == null) {
				return url;
			} else {
				String prefix = protocolAndPathOf(baseUrl);

				if (prefix.endsWith("/"))
				{
					return prefix + url;
				}
				else
				{
					return prefix + "/" + url;
				}
			}
		}
	}

	public void loadPage(String document)
	{
		try
		{
			loadPage(new StringReader(document));
		}
		catch (IOException e)
		{
			// StringReader never throws an IOException.
		}
	}

	/** 
	 * Loads a new HTML page with the specified input stream
	 * @param in the input stream
	 * @throws IOException when the page could not be read or when the input stream is null
	 */
	public void loadPage(InputStream in)
	throws IOException
	{
		loadPage(in, null);
	}

	/** 
	 * Loads a new HTML page with the specified input stream
	 * @param in the input stream
	 * @param encoding the encoding, is ignored when null
	 * @throws IOException when the page could not be read or when the input stream is null or when the specified encoding is not supported
	 */
	public void loadPage(InputStream in, String encoding)
	throws IOException
	{
		if (in == null)
		{
			throw new IOException("no input stream");
		}
		InputStreamReader reader;
		if (encoding == null) {
			reader = new InputStreamReader(in);
		} else {
			reader = new InputStreamReader(in, encoding);
		}
		loadPage(reader);
	}

	private Image loadImageInternal(String url)
	{
		Image image = (Image) this.imageCache.get(url);

		if (image == null)
		{
			StreamConnection connection = null;
			InputStream is  = null;
			try
			{
				notifyDownloadStart(url);
				ProtocolHandler handler = getProtocolHandlerForURL(url);

				connection = handler.getConnection(url);
				is = connection.openInputStream();
				if (is == null) {
					return null;
				}
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				byte[] buf = new byte[1024];
				int bytesRead;
				do
				{
					bytesRead = is.read(buf);
					if (bytesRead > 0)
					{
						bos.write(buf, 0, bytesRead);
					}
				}
				while (bytesRead >= 0);

				notifyDownloadEnd();
				buf = bos.toByteArray();

				//#debug
				System.out.println("Image requested: " + url);

				image = Image.createImage(buf, 0, buf.length);
				this.imageCache.put(url, image);
				return image;
			}
			catch (Exception e)
			{
				// TODO: Implement proper error handling.

				//#debug error
				System.out.println("Unable to load image " + url + e);

				return null;
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (Exception e) {
						//#debug error
						System.out.println("unable to close inputstream " + e );
					}
				}
				if (connection != null) {
					try {
						connection.close();
						// on some Series 40 devices we need to set the connection to null,
						// which is weird, to say the least.  Nokia, anyone?
						connection = null;
					} catch (Exception e) {
						//#debug error
						System.out.println("unable to close connection " + e );
					}
				}
			}
		}

		return image;
	}

	public Image loadImage(String url)
	{
		Image image = loadImageInternal(url);

		if (image == null)
		{
			image = loadImageInternal(BROKEN_IMAGE);
		}

		if (image == null)
		{
			image = Image.createImage(10, 10);
			Graphics g = image.getGraphics();
			g.setColor(0xFFFFFF);
			g.fillRect(0, 0, 10, 10);
			g.setColor(0xFF0000);
			g.drawLine(0, 0, 10, 10);
			g.drawLine(0, 10, 10, 0);
		}

		return image;
	}

	public void loadImageLater(ImageItem item, String url)
	{
		if (this.imagesToLoad == null) {
			this.imagesToLoad = new Hashtable();
		}

		this.imagesToLoad.put(item, url);
	}

	/**
	 * "http://foo.bar.com/baz/boo/blah.html" => "http://foo.bar.com/baz/boo"
	 * <br>
   " "http://foo.bar.com" => "http://foo.bar.com"
	 * <br>
	 * "resource://baz/blah.html" => "resource://baz"
	 * <br>
	 * "resource://blah.html" => "resource://"
	 * 
	 * @param url the URL to the get protocol and path part from
	 * @return the protocol and path part from the given URL
	 */
	protected String protocolAndPathOf (String url)
	{
		// 1. Look for query args, or end of string.
		// 2. from there, scan backward for first '/', 
		// 3. cut the string there.
		// figure out what error checking to do here

		int end = url.indexOf('?');

		if (end == -1)
		{
			end = url.length()-1;
		}

		int hostStart = url.indexOf("//");
		// figure out what error checking to do here
		hostStart += 2;

		int lastSlash = url.lastIndexOf('/', end);

		// RESOURCE urls have no host portion, so return everything between
		// the "resource://" and last slash, if it exists.
		if ("resource://".regionMatches(true, 0, url, 0, 11))
		{
			if ((lastSlash == -1) || (lastSlash <= hostStart))
			{
				return "resource://";
			}

			return url.substring(0, lastSlash);
		}
		else
		{
			if ((lastSlash == -1) || (lastSlash <= hostStart))
			{
				return url;
			}

			return url.substring(0, lastSlash);
		}
	}

	public boolean handleCommand(Command command)
	{
		Object[] handlers = this.tagHandlers.getInternalArray();
		for (int i = 0; i < handlers.length; i++)
		{
			TagHandler handler = (TagHandler) handlers[i];
			if (handler == null) {
				break;
			}
			if (handler.handleCommand(command)) {
				return true;
			}

		}
		return super.handleCommand(command);
	}

	//#if polish.LibraryBuild
	/**
	 * Tries to handle the specified command.
	 * The item checks if the command belongs to this item and if it has an associated ItemCommandListener.
	 * Only then it handles the command.
	 * @param cmd the command
	 * @return true when the command has been handled by this item
	 */
	public boolean handleCommand( javax.microedition.lcdui.Command cmd ) {
		return false;
	}
	//#endif


	protected void goImpl(String url, String postData)
	{
		String previousDocumentBase = this.currentDocumentBase;
		StreamConnection connection = null;
		InputStream is = null;
		try
		{
			// Throws an exception if no handler found.
			ProtocolHandler handler = getProtocolHandlerForURL(url);

			this.currentDocumentBase = url;
			connection = handler.getConnection(url);

			if (connection != null)
			{

				notifyPageStart(url);
				boolean isHttpConnection = connection instanceof HttpConnection;
				HttpConnection httpConnection = null;
				if (isHttpConnection) {
					httpConnection = (HttpConnection) connection;
					if (this.cookie != null) {
						httpConnection.setRequestProperty("cookie", this.cookie );
					}
				}
				if (postData != null && isHttpConnection) {
					httpConnection.setRequestMethod(HttpConnection.POST);
					httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
					OutputStream os = connection.openOutputStream();
					os.write(postData.getBytes());
					os.close();
				}

				is = connection.openInputStream();
				String contentEncoding = null;
				if (isHttpConnection) {
					contentEncoding = httpConnection.getEncoding();
					if (contentEncoding == null) {
						contentEncoding = httpConnection.getHeaderField("Content-Encoding");
					}
					String newCookie = httpConnection.getHeaderField("Set-cookie");
					if ( newCookie != null) {
						int semicolonPos = newCookie.indexOf(';');
						//#debug
						System.out.println("received cookie = [" + newCookie + "]");
						if (semicolonPos != -1) {
							// a session cookie has a session ID and a domain to which it should be sent, e.g. 
							newCookie = newCookie.substring(0, semicolonPos );
						}
						this.cookie = newCookie;
					}
				}
				if (contentEncoding == null) {
					contentEncoding = "UTF-8";
				}
				//#if polish.Browser.Gzip
				try {
					if (contentEncoding != null && contentEncoding.indexOf("gzip") != -1) {
						is = new GZipInputStream(is, GZipInputStream.TYPE_GZIP, true);
						contentEncoding = null;
					}
				}
				catch (IOException e) {
					//#debug error
					System.out.println("Unable to use GzipInputStream" + e);
				}
				//#endif
				loadPage(is, contentEncoding);
				notifyPageEnd();
			}
		}
		catch (Exception e)
		{
			//#debug error
			System.out.println("Unable to load page " + url + e );
			this.currentDocumentBase = previousDocumentBase;
			notifyPageError(url, e);
			closeContainers();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
					//#debug error
					System.out.println("unable to close inputstream " + e );
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					//#debug error
					System.out.println("Unable to close connection " + e);
				}
			}
			HistoryEntry entry = this.scheduledHistoryEntry;
			if (entry != null) {
				int index = entry.getFocusedIndex();
				if (index < size()) {
					focusChild( index );
					setScrollYOffset( entry.getScrollOffset(), false );
				}
				this.scheduledHistoryEntry = null;
			}

		}
	}


	//////////////// download indicator handling /////////////


	//#if polish.Browser.PaintDownloadIndicator
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Container#initContent(int, int)
	 */
	protected void initContent(int firstLineWidth, int availWidth, int availHeight) {
		super.initContent(firstLineWidth, availWidth, availHeight);
		// when there is a loading indicator, we need to specify the minmum size:
		int width = this.loadingIndicator.getItemWidth( availWidth, availWidth, availHeight );
		if (width > this.contentWidth) {
			this.contentWidth = width;
		}
		int height = this.loadingIndicator.itemHeight;
		if (height > this.contentHeight) {
			this.contentHeight = height;
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Container#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g)
	{
		super.paintContent(x, y, leftBorder, rightBorder, g);
		if (this.isWorking)
		{
			int relY = this.yOffset;
			if (this.parent instanceof Container) {
				relY -= ((Container)this.parent).getScrollYOffset();
			}
			int liHeight = this.loadingIndicator.getItemHeight( this.availableWidth, this.availableWidth, this.availableHeight );
			int liLayout = this.loadingIndicator.getLayout();
			if ( (liLayout & LAYOUT_VCENTER) == LAYOUT_VCENTER ) {
				relY += this.contentHeight>>1 + liHeight>>1;
			} else if ( (liLayout & LAYOUT_BOTTOM ) == LAYOUT_BOTTOM ) {
				relY += this.contentHeight - liHeight;
			}
			this.loadingIndicator.relativeY = relY;
			//		  System.out.println(">>>>>> download indicator at " + x + "(abs=" + this.loadingIndicator.getAbsoluteX() +  "), " + (y + relativeY) + " (abs=" + this.loadingIndicator.getAbsoluteY() +", rel=" + relativeY + "), yOffset=" + this.yOffset);
			this.loadingIndicator.paint(x, y + relY, leftBorder, rightBorder, g);
			//		  g.setColor( 0xff0000 );
			//		  g.drawRect( this.loadingIndicator.getAbsoluteX(), this.loadingIndicator.getAbsoluteY(), this.loadingIndicator.itemWidth, this.loadingIndicator.itemHeight);
		}
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.FakeCustomItem#animate(long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(long currentTime, ClippingRegion repaintRegion)
	{
		super.animate(currentTime, repaintRegion);
		if (this.isWorking) {
			this.loadingIndicator.animate(currentTime, repaintRegion);
		} else if (!this.isStoppedWorking) {
			this.isStoppedWorking = true;
		}
	}


	//#endif



	////////////////  downloading thread /////////////////
	public void run()
	{
		// ensure that the user is able to specify the first location before this thread is going to sleep/wait.
		try {
			Thread.sleep( 100 );
		} catch (InterruptedException e) {
			// ignore
		}
		this.isRunning = true;

		while (this.isRunning)
		{
			try {
				if (this.isRunning && this.nextUrl != null)
				{
					this.isWorking = true;
					//#if polish.Browser.PaintDownloadIndicator
						this.isStoppedWorking = false;
					//#endif
					String url = this.nextUrl;
					String postData = this.nextPostData;
					this.nextUrl = null;
					this.nextPostData = null;

					if (!this.isCancelRequested)
					{
						//#if polish.Browser.MemorySaver
							int size = 50 * 1024;
							//#if polish.Browser.MemorySaver.Amount:defined
								//#= size = ${polish.Browser.MemorySaver.Amount};
							//#endif
							byte[] memorySaver = new byte[size];
						//#endif

						try {
							goImpl(url, postData);

						}
						catch (OutOfMemoryError e) {
							//#if polish.Browser.MemorySaver
							memorySaver = null;
							System.gc();
							//#endif

							// Signal stopped parsing.
							//#style browserText?
							StringItem item = new StringItem(null, "parsing stopped");
							add(item);
						} finally {
							//#if polish.Browser.MemorySaver
							if (memorySaver != null) {
								memorySaver = null;
							}
							//#endif
						}
					}

					this.isWorking = false;
					//#if polish.Browser.PaintDownloadIndicator
					getScreen().repaint();
					//#endif
				}
			}
			catch (Exception e) {
				//#debug error
				System.out.println("Unable to load " + this.currentDocumentBase + e);
			}

			if (this.isCancelRequested)
			{
				this.isWorking = false;
				//#if polish.Browser.PaintDownloadIndicator
				getScreen().repaint();
				//#endif
				this.isCancelRequested = false;
				loadPage("Request canceled");
			}

			try
			{
				this.isWorking = false;
				if (this.nextUrl == null) {
					synchronized( this.loadingThread ) {
						this.loadingThread.wait();
					}
				}
			}
			catch (InterruptedException ie)
			{
				//				interrupt();
			}
		} // end while(isRunning)
	} // end run()

	protected void schedule(String url, String postData)
	{
		this.nextUrl = url;
		this.nextPostData = postData;
		this.isCancelRequested = false;

		synchronized( this.loadingThread ) {
			this.loadingThread.notify();
		}
	}

	public void cancel()
	{
		this.isCancelRequested = true;
	}

	public synchronized void requestStop()
	{
		this.isRunning = false;
		synchronized( this.loadingThread ) {
			this.loadingThread.notify();
		}
	}

	public boolean isRunning()
	{
		return this.isRunning;
	}

	public boolean isCanceled()
	{
		return this.isCancelRequested;
	}

	public boolean isWorking()
	{
		return this.isWorking;
	}


	//////////////////////////// History //////////////////////////////

	/**
	 * Schedules the given URL for loading.
	 * @param url the URL that should be loaded
	 */
	public void go(String url)
	{
		if (this.isWorking && url.equals(this.currentDocumentBase)) {
			//#debug info
			System.out.println("ignoring go request for " + url + ", as this is currently loading.");
			return;
		}
		//#debug
		System.out.println("Browser: going to [" + url + "]" );
		if (this.currentDocumentBase != null)
		{
			this.history.push( new HistoryEntry( this.currentDocumentBase, getFocusedIndex(), getScrollYOffset() ) );
			if (this.cmdBack != null && this.history.size() == 1 && getScreen() != null) {
				getScreen().addCommand(this.cmdBack);
			}
		}
		schedule(url, null);
	}

	/**
	 * Schedules the given URL for loading with HTTP POST data.
	 * @param url the URL that should be loaded
	 * @param postData the data to be sent via HTTP POST
	 */
	public void go(String url, String postData)
	{
		if (this.isWorking && url.equals(this.currentDocumentBase)) {
			//#debug info
			System.out.println("ignoring go request for " + url + ", as this is currently loading.");
			return;
		}
		//#debug
		System.out.println("Browser: going to [" + url + "]" );
		if (this.currentDocumentBase != null)
		{
			this.history.push(new HistoryEntry( this.currentDocumentBase, getFocusedIndex(), getScrollYOffset() ));
			if (this.cmdBack != null && this.history.size() == 1 && getScreen() != null) {
				getScreen().addCommand(this.cmdBack);
			}
		}
		schedule(url, postData);
	}

	/**
	 * Schedules the given history document for loading.
	 * 
	 * @param historySteps the steps that should go back, e.g. 1 for the last page that has been shown
	 */
	public void go(int historySteps)
	{
		HistoryEntry entry = null;

		while (historySteps > 0 && this.history.size() > 0)
		{
			entry = (HistoryEntry) this.history.pop();
			historySteps--;
		}

		if (entry != null)
		{
			this.scheduledHistoryEntry = entry;
			schedule(entry.getUrl(), null);
			if (this.history.size() == 0 && this.cmdBack != null && getScreen() != null) {
				getScreen().removeCommand(this.cmdBack);
			}
		}
	}

	public void followLink()
	{
		Item item = getFocusedItem();
		String href = (String) item.getAttribute("href");

		if (href != null)
		{
			go(makeAbsoluteURL(href));
		}
	}

	/**
	 * Sets the back command for this browser.
	 * The back command will be appended to the parent screen when the browser can go back and it will be removed when the browser cannot got back anymore.
	 * @param cmdBack the back command - set to null to remove it completely
	 */
	public void setBackCommand(Command cmdBack)
	{
		if (this.cmdBack != null && getScreen() != null) {
			getScreen().removeCommand(this.cmdBack);
		}
		this.cmdBack = cmdBack;
	}

	//#if polish.LibraryBuild
	/**
	 * Sets the back command for this browser.
	 * The back command will be appended to the parent screen when the browser can go back and it will be removed when the browser cannot got back anymore.
	 * @param cmdBack the back command - set to null to remove it completely
	 */
	public void setBackCommand(javax.microedition.lcdui.Command cmdBack)
	{
		// ignore
	}
	//#endif


	/**
	 * Goes back one history step.
	 * 
	 * @return true when the browser has a previous document in its history
	 * @see #go(int)
	 */
	public boolean goBack()
	{
		if (this.history.size() > 0) {
			go(1);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checks if the browser can go back
	 * @return true when there is a known previous document
	 * @see #goBack()
	 */
	public boolean canGoBack()
	{
		return this.history.size() > 0;
	}

	/**
	 * Clears the history
	 * @see #goBack()
	 * @see #go(int)
	 */
	public void clearHistory()
	{
		this.history.removeAllElements();
		this.imageCache.clear();
		//clear();
		this.currentDocumentBase = null;
		if (this.cmdBack != null && getScreen() != null) {
			getScreen().removeCommand(this.cmdBack);
		}
	}

	protected void notifyPageError(String url, Exception e)
	{
		AnimationThread.removeAnimationItem(this);
		if (this.browserListener != null) {
			this.browserListener.notifyPageError(url, e);
		}
	}

	protected void notifyPageStart(String url)
	{
		AnimationThread.addAnimationItem(this);
		if (this.browserListener != null) {
			this.browserListener.notifyPageStart(url);
		}
	}

	protected void notifyPageEnd()
	{
		AnimationThread.removeAnimationItem(this);
		if (this.browserListener != null) {
			this.browserListener.notifyPageEnd();
		}
	}

	protected void notifyDownloadStart(String url)
	{
		if (this.browserListener != null) {
			this.browserListener.notifyDownloadStart(url);
		}
	}

	protected void notifyDownloadEnd()
	{
		if (this.browserListener != null) {
			this.browserListener.notifyDownloadEnd();
		}
	}

	public BrowserListener getBrowserListener()
	{
		return this.browserListener;
	}

	public void setBrowserListener(BrowserListener browserListener)
	{
		this.browserListener = browserListener;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.ResourceLoader#getResourceAsStream(java.lang.String)
	 */
	public InputStream getResourceAsStream(String url) throws IOException {
		StreamConnection connection = null;
		notifyDownloadStart(url);
		ProtocolHandler handler = getProtocolHandlerForURL(url);
		connection = handler.getConnection(url);
		if (this.cachedConnections == null) {
			this.cachedConnections = new Hashtable();
		}
		this.cachedConnections.put(url, connection);
		return connection.openInputStream();
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.ResourceLoader#close(java.lang.String, java.io.InputStream)
	 */
	public void close(String url, InputStream in) throws IOException {
		try {
			in.close();
		} catch (Exception e) {
			// ignore
		}
		notifyDownloadEnd();
		if (this.cachedConnections != null) {
			StreamConnection con = (StreamConnection) this.cachedConnections.remove(url);
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}


	

}
