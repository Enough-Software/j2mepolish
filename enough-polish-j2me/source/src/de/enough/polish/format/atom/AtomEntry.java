/*
 * Created on Jul 13, 2010 at 1:39:27 PM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package de.enough.polish.format.atom;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import de.enough.polish.io.Externalizable;
import de.enough.polish.io.RedirectHttpConnection;
import de.enough.polish.util.HashMap;
import de.enough.polish.util.IdentityArrayList;
import de.enough.polish.util.StreamUtil;
import de.enough.polish.util.TimePoint;
import de.enough.polish.xml.XmlDomNode;

/**
 * <p>Manages an entry within an Atom based news feed</p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class AtomEntry
implements Externalizable
{

	private static final int VERSION = 102;
	private String id;
	private String title;
	
	private String sourceId;
	private String sourceTitle;
	
	private String updatedString;
	private TimePoint updated;
	private String summary;
	private String content;
	private String contentType;
	private IdentityArrayList images;
	private IdentityArrayList linksList;
	private boolean hasLoadedImages;
	private boolean isRead;

	public AtomEntry() {
		// nothing to init here
	}
	
	public AtomEntry(XmlDomNode node) {
		this.id = node.getChildText("id");
		this.title = node.getChildText("title");
		
		XmlDomNode sourceNode = node.getChild("source");
		if (sourceNode != null) {
			this.sourceId = sourceNode.getChildText("id");
			this.sourceTitle = sourceNode.getChildText("title");
		}
		
		this.updatedString = node.getChildText("updated");
		this.summary = node.getChildText("summary");
		XmlDomNode contNode = node.getChild("content");
		if (contNode == null) {
			contNode = node.getChild("atom:content");
		}
		if (contNode != null) {
			this.content = contNode.getText();
			this.contentType = contNode.getAttribute("type");
		}
		int childCount = node.getChildCount();
		for (int i=0; i<childCount; i++) {
			XmlDomNode linkNode = node.getChild(i);
			if ("link".equals(linkNode.getName())) {
				if (this.linksList == null) {
					this.linksList = new IdentityArrayList();
				}
				AtomEntryLink link = new AtomEntryLink(linkNode);
				this.linksList.add(link);
				
				String type = linkNode.getAttribute("type");
				if (type != null && type.startsWith("image")) {
					String href = linkNode.getAttribute("href");
					if (href != null) {
						if (this.images == null) {
							this.images = new IdentityArrayList();
						}
						this.images.add( new AtomImage(href) );
					}
				}
			}
		}
	}


	/**
	 * Retrieves the ID of this entry
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Retrieves the title of this entry
	 * @return the title
	 */
	public String getTitle() {
		return this.title;
	}
	
	/**
	 * Retrieves the ID of the entry's source
	 * @return the source id
	 */
	public String getSourceId() {
		return sourceId;
	}
	
	/**
	 * Retrieves the title of the entry's source
	 * @return the source title
	 */
	public String getSourceTitle() {
		return sourceTitle;
	}

	/**
	 * Retrieves the updated time
	 * @return the update time of this entry, might be null
	 */
	public TimePoint getUpdated() {
		if (this.updated == null && this.updatedString != null) {
			this.updated = TimePoint.parseRfc3339(this.updatedString);
		}
		return this.updated;
	}

	/**
	 * Retrieves the summary of this entry
	 * @return the summary
	 */
	public String getSummary() {
		return this.summary;
	}

	/**
	 * Retrieves the content of this entry
	 * @return the content
	 * @see #getContentType()
	 */
	public String getContent() {
		return this.content;
	}

	
	/**
	 * Sets the content of this entry
	 * @param content the new content
	 */
	public void setContent(String content) {
		this.content = content;		
	}

	/**
	 * Retrieves the type of this content
	 * @return the contentType, e.g. html
	 */
	public String getContentType() {
		return this.contentType;
	}
	
	public AtomContentLink[] getContentLinks() {
		if (this.content == null) {
			return new AtomContentLink[0];
		}
		IdentityArrayList links = new IdentityArrayList();	
		int startIndex = 0;
		int foundIndex;
		while ((foundIndex = this.content.indexOf("<a", startIndex)) != -1) {
			int hrefIndex = this.content.indexOf("href", foundIndex+2);
			if (hrefIndex != -1) {
				boolean isInQuotes = false;
				StringBuffer href = new StringBuffer();
				char c;
				while (hrefIndex < this.content.length()) {
					c = this.content.charAt(hrefIndex);
					if (c == '"') {
						if (isInQuotes) {
							break;
						} else {
							isInQuotes = true;
						}
					} else if (isInQuotes) {
						href.append(c);
					}
					hrefIndex++;
				}
				StringBuffer description = new StringBuffer();
				isInQuotes = false;
				while (hrefIndex < this.content.length()) {
					c = this.content.charAt(hrefIndex);
					if (c == '>') {
						isInQuotes = true;
					} else if (c == '<') {
						break;
					} else if (isInQuotes) {
						description.append(c);
					}
					hrefIndex++;
				}
				links.add( new AtomContentLink(href.toString(), description.toString()));
			}
			startIndex = hrefIndex;
		}
		return (AtomContentLink[]) links.toArray(new AtomContentLink[links.size()]);
	}
	
	/**
	 * Determines whether this entry has any images at all.
	 * @return true when there are referenced images
	 */
	public boolean hasImages() {
		return (this.images != null);
	}
	
	/**
	 * Retrieves the images that are stored in this entry
	 * @return an array of AtomImages of referenced images, can be empty but not null
	 */
	public AtomImage[] getImages() {
		if (this.images == null) {
			return new AtomImage[0];
		}
		return (AtomImage[]) this.images.toArray( new AtomImage[ this.images.size() ] );
	}
	
	/**
	 * Retrieves the number of images that are stored in this entry
	 * @return the number of images that are stored in this entry
	 */
	public int getImagesSize() {
		if (this.images == null) {
			return 0;
		}
		return this.images.size();
	}

	
	/**
	 * Retrieves the internal array of the image URLs stored in this entry
	 * @return either null or the internal array of stored AtomImages which may contain null values
	 * @see #getImagesSize()
	 */
	public Object[] getImagesAsInternalArray() {
		if (this.images == null) {
			return null;
		}
		return this.images.getInternalArray();
	}

	/**
	 * Determines whether the referenced images have been loaded yet.
	 * @return true when the images have been loaded
	 * @see #getImages()
	 * @see #loadImages(AtomImageConsumer)
	 */
	public boolean hasLoadedImages() {
		return this.hasLoadedImages;
	}
	
	/**
	 * Loads the referenced images in this thread.
	 * @param consumer the consumer
	 */
	public void loadImages( AtomImageConsumer consumer ) {
		loadImages( consumer, null);
	}
	
	/**
	 * Loads the referenced images in this thread.
	 * @param consumer the consumer
	 * @param requestProperties the request properties to be set for each http request (String name, String value)
	 */
	public void loadImages( AtomImageConsumer consumer, HashMap requestProperties ) {
		if (this.images == null) {
			if (consumer != null) {
				consumer.onAtomImageLoadFinished(this);
			}
			return;
		}
		InputStream in = null;
		RedirectHttpConnection connection = null;
		for (int i=0; i<this.images.size(); i++) {
			AtomImage image = (AtomImage) this.images.get(i);
			String url = image.getUrl();
			try {
				connection = new RedirectHttpConnection( url, requestProperties );
				in = connection.openInputStream();
				if (connection.getResponseCode() != 200) {
					throw new IOException("response code " + connection.getResponseCode() + " for " + url);
				}
				byte[] data = StreamUtil.readFully(in);
				image.setData( data );
				if (consumer != null) {
					consumer.onAtomImageLoaded(image, this);
				}
				try {
					in.close();
					in = null;
					connection.close();
					connection = null;
				} catch (Exception e) {
					// ignore
				}
			} catch (Throwable e) {
				if (consumer != null) {
					consumer.onAtomImageLoadError(image, this, e);
				}
			}
		}
		if (consumer != null) {
			consumer.onAtomImageLoadFinished(this);
		}
		this.hasLoadedImages = true;
	}
	
	/**
	 * Retrieves all links in this entry
	 * @return all links, the array might be empty but not null
	 */
	public AtomEntryLink[] getLinks() {
		if (this.linksList == null) {
			return new AtomEntryLink[0];
		}
		return (AtomEntryLink[]) this.linksList.toArray( new AtomEntryLink[ this.linksList.size() ] );
	}
	
	/**
	 * Retrieves the number of links in this entry
	 * @return the number of links
	 * @see #getLinksAsInternalArray()
	 */
	public int getLinksSize() {
		if (this.linksList == null) {
			return 0;
		}
		return this.linksList.size();
	}
	
	/**
	 * Retrieves all links as an internal array
	 * @return either null when there are no links or an object array which might include null values
	 * @see #getLinksSize() for retrieving the number of links
	 */
	public Object[] getLinksAsInternalArray() {
		if (this.linksList == null) {
			return null;
		}
		return this.linksList.getInternalArray();
	}
	
	/**
	 * Retrieves the first matching link
	 * @param rel the relation, null of the relation should be ignored
	 * @param type the type, null if the type should be ignored
	 * @return the first matching link or null
	 */
	public AtomEntryLink getLink( String rel, String type ) {
		if (this.linksList == null) {
			return null;
		}
		Object[] links = this.linksList.getInternalArray();
		boolean isMatching;
		for (int i = 0; i < links.length; i++) {
			AtomEntryLink link = (AtomEntryLink) links[i];
			if (link == null) {
				break;
			}
			isMatching = true;
			if ((rel != null) && !(rel.equals(link.getRel()))) {
				isMatching = false;
			}
			if (isMatching && (type != null) && !(type.equals(link.getType())) ) {
				isMatching = false;
			}
			if (isMatching) {
				return link;
			}
		}
		return null;
	}
	
	/**
	 * Determines whether this entry has been read already.
	 * @return true when this entry has been read
	 * @see #markRead()
	 */
	public boolean isRead() {
		return this.isRead;
	}
	
	/**
	 * Sets this entry as read.
	 * @return true when the read state of this entry has been changed
	 * @see #isRead()
	 */
	public boolean markRead() {
		return setRead(true);
	}
	

	/**
	 * Sets the read state of this entry.
	 * @param isRead true when this article is deemed as read
	 * @return true when the read state of this entry has been changed
	 * @see #isRead()
	 */
	public boolean setRead(boolean isRead) {
		if (this.isRead != isRead) {
			this.isRead = isRead;
			return true;
		}
		return false;
	}


	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#write(java.io.DataOutputStream)
	 */
	public void write(DataOutputStream out) throws IOException {
		out.writeInt( VERSION );
		boolean notNull = (this.id != null);
		out.writeBoolean(notNull);
		if (notNull) {
			out.writeUTF(this.id);
		}
		notNull = (this.title != null);
		out.writeBoolean(notNull);
		if (notNull) {
			out.writeUTF(this.title);
		}
		notNull = (this.updatedString != null);
		out.writeBoolean(notNull);
		if (notNull) {
			out.writeUTF(this.updatedString);
		}
		notNull = (this.summary != null);
		out.writeBoolean(notNull);
		if (notNull) {
			out.writeUTF(this.summary);
		}
		notNull = (this.content != null);
		out.writeBoolean(notNull);
		if (notNull) {
			out.writeUTF(this.content);
		}
		notNull = (this.images != null);
		out.writeBoolean(notNull);
		if (notNull) {
			int size = this.images.size();
			out.writeInt( size );
			for (int i=0; i<size; i++) {
				AtomImage image = (AtomImage) this.images.get(i);
				image.write(out);
			}
		}
		out.writeBoolean(this.isRead);
		notNull = (this.linksList != null);
		out.writeBoolean(notNull);
		if (notNull) {
			int size = this.linksList.size();
			out.writeInt( size );
			for (int i=0; i<size; i++) {
				AtomEntryLink link = (AtomEntryLink) this.linksList.get(i);
				link.write(out);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#read(java.io.DataInputStream)
	 */
	public void read(DataInputStream in) throws IOException {
		int version = in.readInt();
		if (version > VERSION) {
			throw new IOException("unknown verion " + version);
		}
		boolean notNull = in.readBoolean();
		if (notNull) {
			this.id = in.readUTF();
		}
		notNull = in.readBoolean();
		if (notNull) {
			this.title = in.readUTF();
		}
		notNull = in.readBoolean();
		if (notNull) {
			this.updatedString = in.readUTF();
		}
		notNull = in.readBoolean();
		if (notNull) {
			this.summary = in.readUTF();
		}
		notNull = in.readBoolean();
		if (notNull) {
			this.content = in.readUTF();
		}
		notNull = in.readBoolean();
		if (notNull) {
			int size = in.readInt();
			this.images = new IdentityArrayList(size);
			boolean dataLoaded = false;
			for (int i=0; i<size; i++) {
				AtomImage image = new AtomImage();
				image.read(in);
				this.images.add(image);
				if (image.getData() != null) {
					dataLoaded = true;
				}
			}
			this.hasLoadedImages = dataLoaded;
		}
		if (version > 100) {
			this.isRead = in.readBoolean();
		}
		if (version > 101) {
			notNull = in.readBoolean();
			if (notNull) {
				int size = in.readInt();
				this.linksList = new IdentityArrayList(size);
				for (int i=0; i<size; i++) {
					AtomEntryLink link = new AtomEntryLink();
					link.read(in);
					this.linksList.add(link);
				}
			}
			
		}
	}

	/**
	 * Sets the images for this entry
	 * @param images the images
	 */
	public void setImages(AtomImage[] images) {
		if (images == null)
		{
			this.images = null;
			this.hasLoadedImages = false;
		} 
		else
		{
			boolean isLoaded = true;
			if (this.images == null) {
				this.images = new IdentityArrayList();
			} else {
				this.images.clear();
			}
			for (int i = 0; i < images.length; i++) {
				AtomImage atomImage = images[i];
				this.images.add(atomImage);
				if (isLoaded && atomImage.getData() == null) {
					isLoaded = false;
				}
			}
			this.hasLoadedImages = isLoaded;
		}
	}

}
