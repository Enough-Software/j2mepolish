/*
 * Created on Jul 13, 2010 at 1:38:11 PM.
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
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import de.enough.polish.io.Externalizable;
import de.enough.polish.io.RedirectHttpConnection;
import de.enough.polish.io.StringReader;
import de.enough.polish.util.HashMap;
import de.enough.polish.util.IdentityArrayList;
import de.enough.polish.util.TimePoint;
import de.enough.polish.xml.SimplePullParser;
import de.enough.polish.xml.XmlDomNode;
import de.enough.polish.xml.XmlDomParser;
import de.enough.polish.xml.XmlPullParser;

/**
 * <p>Manages a list of Atom news entries</p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class AtomFeed
implements Externalizable
{
	
	private static final int VERSION = 101;
	private static int maxNumberOfSerializedEntries;
	private final IdentityArrayList entries;
	private String feedId;
	private String title;
	private String subtitle;
	private String updatedString;
	private TimePoint updated;
	private AtomAuthor author;
	private boolean isUpdating;
	private Object data;
	
	/**
	 * Creates a new empty feed
	 */
	public AtomFeed(){
		this.entries = new IdentityArrayList();
	}


	/**
	 * Creates a new feed from the given input stream
	 * @param in the stream that should be read
	 * @throws IOException when parsing fails
	 */
	public AtomFeed(InputStream in) throws IOException {
		this();
		parse( in );
	}
	
	/**
	 * Sets a limit for the number of entries when this feed is serialized.
	 * By default the number is not limited.
	 * @param max the maximum number of serialized entries
	 * @see #getMaxNumberOfSerializedEntries()
	 */
	public static void setMaxNumberOfSerializedEntries(int max) {
		maxNumberOfSerializedEntries = max;
	}
	
	/**
	 * Retrieves a possible limit for the number of entries when this feed is serialized.
	 * By default the number is not limited.
	 * @return the maximum number of serialized entries, values <= 0 mean that entries are not limited
	 * @see #setMaxNumberOfSerializedEntries(int)
	 */
	public static int getMaxNumberOfSerializedEntries() {
		return maxNumberOfSerializedEntries;
	}
	
	/**
	 * Parses the specified input stream
	 * @param document the document that contains the atom feed 
	 * @throws IOException when parsing fails
	 */
	public void parse(String document) throws IOException {
		parse( new StringReader(document));
	}

	/**
	 * Parses the specified input stream
	 * @param in the stream that should be read
	 * @throws IOException when parsing fails
	 */
	public void parse(InputStream in) throws IOException {
		parse( new InputStreamReader(in) );
	}
	
	/**
	 * Parses the specified input stream
	 * @param in the stream that should be read
	 * @param encoding the encoding of the stream
	 * @throws IOException when parsing fails
	 */
	public void parse(InputStream in, String encoding) throws IOException {
        InputStreamReader inputStreamReader;
        if (encoding != null) {
        	inputStreamReader = new InputStreamReader(in, encoding);
        } else {
        	inputStreamReader = new InputStreamReader(in);
        }
		parse( inputStreamReader );
	}

	/**
	 * Parses the atom feed from the given reader.
	 * 
	 * @param reader the reader of the XML stream
	 * @throws IOException when parsing fails
	 */
	public void parse(Reader reader) throws IOException {
		XmlDomNode root = XmlDomParser.parseTree(reader);
		parse( root );
	}


	/**
	 * Parses the XML document
	 * @param root the root of the XML document
	 */
	public void parse(XmlDomNode root) {
		// general feed information:
		this.feedId = root.getChildText("id");
		this.title = root.getChildText("title");
		this.subtitle = root.getChildText("subtitle");
		this.updatedString = root.getChildText( "updated" );
		XmlDomNode authorNode = root.getChild("author");
		if (authorNode != null) {
			this.author = new AtomAuthor( authorNode.getChildText("name"), authorNode.getChildText("email"), authorNode.getChildText("uri"));
		}
		int childCount = root.getChildCount();
		for (int i=0; i<childCount; i++) {
			XmlDomNode node = root.getChild(i);
			if ("entry".equals(node.getName())) {
				AtomEntry entry = new AtomEntry( node );
				this.entries.add(entry);
			}
		}
	}
	
	/**
	 * Updates this feed and inserts new items at the beginning.
	 * @param in the input stream
	 * @param consumer the update consumer which is informed about updated entries
	 * @throws IOException when parsing fails
	 */
	public void update(InputStream in, AtomUpdateConsumer consumer) throws IOException {
		update( new InputStreamReader(in), consumer );
	}
	
	/**
	 * Updates this feed and inserts new items at the beginning.
	 * @param in the input stream
	 * @param encoding the encoding of the stream
	 * @param consumer the update consumer which is informed about updated entries
	 * @throws IOException when parsing fails
	 */
	public void update(InputStream in, String encoding, AtomUpdateConsumer consumer) throws IOException {
        InputStreamReader inputStreamReader;
        if (encoding != null) {
        	inputStreamReader = new InputStreamReader(in, encoding);
        } else {
        	inputStreamReader = new InputStreamReader(in);
        }
        update( inputStreamReader, consumer );
	}
	
	/**
	 * Updates this feed and inserts new items at the beginning.
	 * @param reader the reader
	 * @param consumer the update consumer which is informed about updated entries
	 * @throws IOException when parsing fails
	 */
	public void update( Reader reader, AtomUpdateConsumer consumer ) throws IOException {
		XmlDomNode root = XmlDomParser.parseTree(reader);
		update( root, consumer );
	}


	/**
	 * Updates this feed and inserts new items at the beginning.
	 * @param root the root node
	 * @param consumer the update consumer which is informed about updated entries
	 * @throws IOException when parsing fails
	 */
	public void update( XmlDomNode root, AtomUpdateConsumer consumer ) throws IOException {
		this.feedId = root.getChildText("id");
		this.title = root.getChildText("title");
		this.subtitle = root.getChildText("subtitle");
		this.updatedString = root.getChildText( "updated" );
		XmlDomNode authorNode = root.getChild("author");
		if (authorNode != null) {
			this.author = new AtomAuthor( authorNode.getChildText("name"), authorNode.getChildText("email"), authorNode.getChildText("uri"));
		}
		String lastEntryId = null;
		if (this.entries.size() > 0) {
			AtomEntry lastEntry = (AtomEntry) this.entries.get( 0 );
			lastEntryId = lastEntry.getId();
		}
		int childCount = root.getChildCount();
		int index = 0;
		for (int i=0; i<childCount; i++) {
			XmlDomNode node = root.getChild(i);
			if ("entry".equals(node.getName())) {
				AtomEntry entry = new AtomEntry( node );
				String id = entry.getId();
				if (lastEntryId != null && lastEntryId.equals(id)) {
					break;
				}
				this.entries.add(index, entry);
				index++;
				if (consumer != null) {
					consumer.onUpdated(this, entry);
				}
			}
		}
	}



	/**
	 * @return the id
	 */
	public String getId() {
		return this.feedId;
	}


	/**
	 * @return the title
	 */
	public String getTitle() {
		return this.title;
	}


	/**
	 * @return the subtitle
	 */
	public String getSubtitle() {
		return this.subtitle;
	}


	/**
	 * Retrieves the updated time
	 * @return the update time of this feed, might be null
	 */
	public TimePoint getUpdated() {
		if (this.updated == null && this.updatedString != null) {
			this.updated = TimePoint.parseRfc3339( this.updatedString );
		}
		return this.updated;
	}

	/**
	 * Sets the update time for this feed
	 * @param tp the update time
	 */
	public void setUpdated( TimePoint tp) {
		this.updated = tp;
	}

	/**
	 * Retrieves information about the author of this feed.
	 * @return the author of this feed, might be null
	 */
	public AtomAuthor getAuthor() {
		return this.author;
	}

	/**
	 * Retrieves all entries of this feed
	 * @return the entries, might be empty but not null
	 */
	public AtomEntry[] getEntries() {
		return (AtomEntry[]) this.entries.toArray( new AtomEntry[ this.entries.size() ] );
	}
	
	/**
	 * Retrieves all entries in the internal array format
	 * @return the internal array in which all entries are stored, might contain null values. This is array is never null.
	 */
	public Object[] getEntriesAsInternalArray() {
		return this.entries.getInternalArray();
	}
	
	/**
	 * Retrieves the number of entries stored in this feed.
	 * @return the number of entries
	 */
	public int size() {
		return this.entries.size();
	}
	
	/**
	 * Retrieves the entry with the given index.
	 * @param index the index, the first entry has the index 0.
	 * @return the corresponding entry
	 * @throws ArrayIndexOutOfBoundsException when the index is invalid
	 */
	public AtomEntry getEntry( int index) {
		return (AtomEntry) this.entries.get(index);
	}
	
	/**
	 * Updates this feed in the current thread.
	 * 
	 * @param consumer the update consumer which is informed about updated entries
	 * @param url the URL from which the feed should be downloaded
	 */
	public void update( AtomUpdateConsumer consumer, String url ) {
		update( consumer, url, null);
	}
	
	/**
	 * Updates this feed in the current thread.
	 * 
	 * @param consumer the update consumer which is informed about updated entries
	 * @param url the URL from which the feed should be downloaded
	 * @param requestProperties the request properties to be set for each http request (String name, String value)
	 */
	public void update( AtomUpdateConsumer consumer, String url, HashMap requestProperties ) {
		this.isUpdating = true;
		RedirectHttpConnection connection = null;
		InputStream in = null;
		try {
			connection = new RedirectHttpConnection( url, requestProperties );
			in = connection.openInputStream();
			if (connection.getResponseCode() != 200) {
				throw new IOException("response code " + connection.getResponseCode() + " for " + url);
			}
			String contentEncoding = null;
			contentEncoding = connection.getEncoding();
			if (contentEncoding == null) {
				contentEncoding = connection.getHeaderField("Content-Encoding");
				if (contentEncoding == null) {
					contentEncoding = "UTF-8";
				}
			}
			update( in, contentEncoding, consumer ); 
		} catch (Throwable e) {
			if (consumer != null) {
				consumer.onUpdateError(this, e);
			}
		} finally {
			if (consumer != null) {
				consumer.onUpdateFinished(this);
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// ignore
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (IOException e) {
					// ignore
				}
			}
			this.isUpdating = false;
		}
	}
	

	/**
	 * Updates this feed in a new background thread.
	 * 
	 * @param consumer the update consumer which is informed about updated entries
	 * @param url the URL from which the feed should be downloaded
	 */
	public void updateInBackground( final AtomUpdateConsumer consumer, final String url ) {
		Thread t = new Thread() {
			public void run() {
				update( consumer, url );
			}
		};
		t.start();
	}
	
	/**
	 * Determines whether this feed is currently being updated
	 * @return true when this thread is updated
	 */
	public boolean isUpdating() {
		return this.isUpdating;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#write(java.io.DataOutputStream)
	 */
	public void write(DataOutputStream out) throws IOException {
		out.writeInt( VERSION );
		boolean notNull = (this.feedId != null);
		out.writeBoolean(notNull);
		if (notNull) {
			out.writeUTF(this.feedId);
		}
		notNull = (this.title != null);
		out.writeBoolean(notNull);
		if (notNull) {
			out.writeUTF(this.title);
		}
		notNull = (this.subtitle != null);
		out.writeBoolean(notNull);
		if (notNull) {
			out.writeUTF(this.subtitle);
		}
		notNull = (getUpdated() != null);
		out.writeBoolean(notNull);
		if (notNull) {
			getUpdated().write(out);
		}
		notNull = (this.author != null);
		out.writeBoolean(notNull);
		if (notNull) {
			this.author.write(out);
		}
		int size = this.entries.size();
		if ((maxNumberOfSerializedEntries > 0) && (size > maxNumberOfSerializedEntries)) {
			size = maxNumberOfSerializedEntries;
		}
		out.writeInt(size);
		for (int i=0; i<size; i++) {
			AtomEntry entry = (AtomEntry) this.entries.get(i);
			entry.write(out);
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
			this.feedId = in.readUTF();
		}
		notNull = in.readBoolean();
		if (notNull) {
			this.title = in.readUTF();
		}
		notNull = in.readBoolean();
		if (notNull) {
			this.subtitle = in.readUTF();
		}
		notNull = in.readBoolean();
		if (notNull) {
			if (version == 100) {
				this.updatedString = in.readUTF();
			} else {
				this.updated = new TimePoint(in);
			}
		}
		notNull = in.readBoolean();
		if (notNull) {
			this.author = new AtomAuthor();
			this.author.read(in);
		}
		int size = in.readInt();
		for (int i=0; i<size; i++) {
			AtomEntry entry = new AtomEntry();
			entry.read(in);
			this.entries.add(entry);
		}
	}
	
	/**
	 * Sets an arbitrary data object.
	 * Note that the data object is not serialized/persisted.
	 * @param data the data
	 * @see #getData()
	 */
	public void setData( Object data ) {
		this.data = data;
	}
	
	/**
	 * Retrieves a previously set data object
	 * @return the data object
	 */
	public Object getData() {
		return this.data;
	}

}
