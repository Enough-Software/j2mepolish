//#condition polish.midp || polish.usePolishGui

/*
 * Copyright (c) 2013 Robert Virkus / Enough Software
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
package de.enough.polish.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.enough.polish.util.ArrayList;
import de.enough.polish.util.IntList;

/**
 * Allows to store lots of data that is retrieved lazily
 * @author Robert Virkus
 */
public abstract class ChunkedStorageCollection
implements Externalizable
{
	public final static int DEFAULT_CHUNK_SIZE = 20;
	public final static int STORAGE_STRATEGY_DIRECT = 1;
	public final static int STORAGE_STRATEGY_CHUNKED = 2;
	//public final static int STORAGE_STRATEGY_MANUAL = 3;

	private final static int PERSISTENCE_VERSION = 2;
	
	private int chunkSize;
	private String identifier;
	
	private int completeSize;
	private int numberOfDeletedEntries;
	private IntList deletedIndecesList;
	private int tailCollectionStartIndex;
	private ArrayList tailCollection;
	private boolean tailCollectionIsDirty;
	private ArrayList currentCollection;
	private int currentCollectionIndex = -1;
	private boolean currentCollectionIsDirty;

	private ChunkedStorageSystem storageSystem;
	private boolean tailCollectionIsLoaded;
	private int storageStrategy;
	
	public ChunkedStorageCollection(String identifier, int chunkSize, ChunkedStorageSystem storageSystem, int storageStrategy)
	{
		this.identifier = identifier;
		this.chunkSize = chunkSize;
		this.storageSystem = storageSystem;
		this.tailCollection = new ArrayList(chunkSize*2);
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#write(java.io.DataOutputStream)
	 */
	public synchronized void write(DataOutputStream out) throws IOException {
		out.writeInt(PERSISTENCE_VERSION);
		out.writeInt(this.chunkSize);
		out.writeInt(this.completeSize);
		out.writeInt(this.tailCollectionStartIndex);
		// persistence version 2+:
		out.writeInt(this.numberOfDeletedEntries);
		if (this.numberOfDeletedEntries > 0)
		{
			this.deletedIndecesList.write(out);
		}
		// persistence version 1+:
		int tailSize = getTailCollection().size();
		out.writeInt(tailSize);
		Object[] internalObjects = getTailCollection().getInternalArray();
		for (int i = 0; i < tailSize; i++) {
			Externalizable externalizable = (Externalizable) internalObjects[i];
			externalizable.write(out);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#read(java.io.DataInputStream)
	 */
	public synchronized void read(DataInputStream in) throws IOException {
		int version = in.readInt();
		if (version > PERSISTENCE_VERSION)
		{
			throw new IOException("for version " + version);
		}
		this.chunkSize = in.readInt();
		this.completeSize = in.readInt();
		this.tailCollectionStartIndex = in.readInt();
		// persistence version 2+:
		if (version >= 2)
		{
			this.numberOfDeletedEntries = in.readInt();
			if (this.numberOfDeletedEntries > 0)
			{
				this.deletedIndecesList = new IntList();
				this.deletedIndecesList.read(in);
			}
		}
		// persistence version 1+:
		int size = in.readInt();
		if ( this.tailCollection == null ) {
			this.tailCollection = new ArrayList(chunkSize*2);
		}
		fillCollection(this.tailCollection, size, in);
	}
	
	/**
	 * Retrieves the size of this collection
	 * @return the complete size of this collection
	 * @see #sizeTail();
	 */
	public synchronized int size()
	{
		if (!this.tailCollectionIsLoaded)
		{
			loadTailCollection();
		}
		return this.completeSize - this.numberOfDeletedEntries;
	}
	
	public synchronized int sizeTail()
	{
		if (!this.tailCollectionIsLoaded)
		{
			loadTailCollection();
		}
		return getTailCollection().size();
	}
	
	private synchronized void fillCollection(ArrayList collection, int size,
			DataInputStream in) throws IOException 
	{
		for (int i=0; i < size; i++)
		{
			Mutable externalizable = createCollectionObject();
			externalizable.read(in);
			collection.add(externalizable);
		}
	}

	
	protected abstract Mutable createCollectionObject();
	
	protected int getExternalIndex(int index)
	{
		if (this.numberOfDeletedEntries == 0)
		{
			return index;
		}
		int[] internal = this.deletedIndecesList.getInternalArray();
		int internalSize = this.deletedIndecesList.size();
		for (int i = 0; i < internalSize; i++)
		{
			int deletedIndex = internal[i];
			if (deletedIndex <= index)
			{
				index--;
			}
			else
			{
				break;
			}
		}
		return index;
	}
	
	protected int getInternalIndex(int index)
	{
		if (this.numberOfDeletedEntries == 0)
		{
			return index;
		}
		int[] internal = this.deletedIndecesList.getInternalArray();
		int internalSize = this.deletedIndecesList.size();
		for (int i = 0; i < internalSize; i++)
		{
			int deletedIndex = internal[i];
			if (deletedIndex <= index)
			{
				index++;
			}
			else
			{
				break;
			}
		}
		return index;
	}
	
	
	public synchronized Object get(int index)
	{
		if (!this.tailCollectionIsLoaded)
		{
			loadTailCollection();
		}
		index = getInternalIndex(index);
		if (index < 0 || index >= this.completeSize)
		{
			throw new ArrayIndexOutOfBoundsException("for index " + index + ", completeSize=" + this.completeSize);
		}
		
		if (index >= this.tailCollectionStartIndex)
		{
			index -= this.tailCollectionStartIndex;
			return getTailCollection().get(index);
		}
		int chunkIndex = index / this.chunkSize;
		int indexWithinChunk = index % this.chunkSize;
		if (chunkIndex != this.currentCollectionIndex)
		{
			try {
				loadChunk(chunkIndex);
			} catch (IOException e) {
				//#debug error
				System.out.println("Unable to load chunk " + chunkIndex + e);
				return null;
			}
		}
		return this.currentCollection.get(indexWithinChunk);
	}
	
	public synchronized int indexOf(Mutable element)
	{
		if (!this.tailCollectionIsLoaded)
		{
			loadTailCollection();
		}
		int index = getTailCollection().indexOf(element);
		if (index != -1)
		{
			return getExternalIndex(this.tailCollectionStartIndex + index);
		}
		if (this.currentCollection != null)
		{
			index = this.currentCollection.indexOf(element);
			if (index != -1)
			{
				return getExternalIndex(index + (this.chunkSize * this.currentCollectionIndex));
			}
		}
		return -1;
	}
	
	public synchronized void clear()
	{
		this.tailCollectionIsLoaded = false;
		this.tailCollectionIsDirty = false; 
		this.tailCollection = null;
		this.currentCollection = null;
	}
	
	public synchronized void deleteCollection()
	{
		this.tailCollectionIsDirty = false; 
		this.currentCollectionIsDirty = false; 
		this.tailCollectionIsLoaded = false;
		clear();
		try {
			this.storageSystem.delete(this.identifier);
		} catch (IOException e) {
			//#debug error
			System.out.println("unable to delete storage" + e);
			e.printStackTrace();
		}
	}
	
	public synchronized Object lastElement()
	{
		if (!this.tailCollectionIsLoaded)
		{
			loadTailCollection();
		}
		int size = getTailCollection().size();
		if (size > 0)
		{
			return getTailCollection().get(size - 1);
		}
		throw new IllegalStateException("there is no element");
	}
	
	public synchronized void add(Mutable element)
	{
		//try { throw new RuntimeException("ADD FOR " + element); } catch (Exception e) { e.printStackTrace(); }
		if (!this.tailCollectionIsLoaded)
		{
			loadTailCollection();
		}
		getTailCollection().add(element);
		this.completeSize++;
		this.tailCollectionIsDirty = true; 
		// check if we have reached a new chunk for the archive:
		if (getTailCollection().size() >= this.chunkSize * 2)
		{
			saveChunk();
		}
		else if (this.storageStrategy == STORAGE_STRATEGY_DIRECT)
		{
			saveTailCollection();
		}
	}
	
	private synchronized void saveTailCollection() {
		try
		{
			boolean serializeDirectly = true;
			byte[] data = Serializer.serialize(this, serializeDirectly);
			this.storageSystem.saveTailData(this.identifier, data);
		} 
		catch (Exception e)
		{
			//#debug error
			System.out.println("unable to save tail collection" + e);
			e.printStackTrace();
		}
		finally
		{
			this.tailCollectionIsDirty = false;
		}
	}

	private synchronized void saveChunk() 
	{
		try
		{
			byte[] data = serializeCollectionChunk(getTailCollection());
			int chunkIndex = this.tailCollectionStartIndex / this.chunkSize;

			this.tailCollectionStartIndex += this.chunkSize;
			//#debug
			System.out.println("saving chunk " + chunkIndex + ", tailCollectionStartIndex=" + this.tailCollectionStartIndex);
			for (int i=0; i<this.chunkSize; i++)
			{
				getTailCollection().remove(0);
			}
			saveTailCollection();
			
			// save the chunk after the tail as the storage implementation might rely on that:
			this.storageSystem.saveChunkData(chunkIndex, identifier, data);
		} 
		catch (IOException e)
		{
			//#debug error
			System.out.println("Unable to save current collection " + e);
			e.printStackTrace();
		}
	}

	public synchronized Object remove(int index)
	{
//		if (!isRemovable(index))
//		{
//			throw new IllegalArgumentException("cannot remove already chunked element " + index);
//		}
		if (index < 0)
		{
			throw new IndexOutOfBoundsException("for " + index);
		}
		if (!this.tailCollectionIsLoaded)
		{
			loadTailCollection();
		}
		int internalIndex = getInternalIndex(index);
		if (internalIndex >= this.completeSize)
		{
			throw new IndexOutOfBoundsException("for " + index);			
		}
		if (internalIndex >= this.tailCollectionStartIndex)
		{
			internalIndex -= this.tailCollectionStartIndex;
			Object removed = getTailCollection().remove(internalIndex);
			this.tailCollectionIsDirty = true;
			this.completeSize--;
			return removed;
		}
		else
		{
			Object removed = set(index, null);
			if (this.deletedIndecesList == null)
			{
				this.deletedIndecesList = new IntList();
				this.deletedIndecesList.add(internalIndex);
			}
			else
			{
				int[] internal = this.deletedIndecesList.getInternalArray();
				int internalSize = this.deletedIndecesList.size();
				boolean isInserted = false;
				for (int i=0; i< internalSize; i++)
				{
					int deletedIndex = internal[i];
					if (deletedIndex > internalIndex)
					{
						this.deletedIndecesList.add(i, internalIndex);
						isInserted = true;
						break;
					}
				}
				if (!isInserted)
				{
					this.deletedIndecesList.add(internalIndex);
				}
			}
			this.numberOfDeletedEntries++;
			return removed;
		}
	}
	
	public synchronized boolean isRemovable(int index)
	{
		if (!this.tailCollectionIsLoaded)
		{
			loadTailCollection();
		}
		return true; // (index >= this.tailCollectionStartIndex);
	}

	
	public synchronized Object set(int index, Mutable element)
	{
		if (!this.tailCollectionIsLoaded)
		{
			loadTailCollection();
		}
		int internalIndex = getInternalIndex(index);
		if (internalIndex >= this.tailCollectionStartIndex)
		{
			internalIndex -= this.tailCollectionStartIndex;
			this.tailCollectionIsDirty = true;
			return getTailCollection().set(internalIndex, element);
		}
		Object previous = get(index);
		int indexWithinChunk = internalIndex % this.chunkSize;
		this.currentCollection.set(indexWithinChunk, element);
		return previous;
	}

	private synchronized void loadTailCollection() {
		try 
		{
			byte[] data = this.storageSystem.loadTailData(this.identifier);
			DataInputStream in = new DataInputStream( new ByteArrayInputStream(data));
			read(in);
			in.close();
		} 
		catch (IOException e) 
		{
			// either there is no stored tail collection or it could not be loaded
			//#debug info
			System.out.println("Unable to load tail collection for " + this.identifier + e);
		}
		finally
		{
			this.tailCollectionIsLoaded = true;
			this.tailCollectionIsDirty = false;
		}
	}

	private synchronized void loadChunk(int chunkIndex) 
	throws IOException 
	{
		//#debug
		System.out.println("loading chunk " + chunkIndex);
		byte[] data = this.storageSystem.loadData(chunkIndex, this.identifier);
		DataInputStream in = new DataInputStream( new ByteArrayInputStream(data));
		if (this.currentCollection != null)
		{
			if (this.currentCollectionIsDirty || containsDirtyElement(this.currentCollection))
			{
				saveCurrentCollection();
				this.currentCollectionIsDirty = false;
			}
			this.currentCollection.clear();
		}
		else
		{
			this.currentCollection = new ArrayList(this.chunkSize);
		}
		int version = in.readInt();
		if (version > PERSISTENCE_VERSION)
		{
			throw new IOException("for version " + version);
		}
		this.currentCollectionIndex = chunkIndex;
		fillCollection(this.currentCollection, this.chunkSize, in);
		in.close();
		// remove deleted elements to save memory:
		if (this.numberOfDeletedEntries > 0)
		{
			int chunkStartIndex = chunkIndex * this.chunkSize;
			int[] internal = this.deletedIndecesList.getInternalArray();
			int internalSize = this.deletedIndecesList.size();
			for (int i=0; i<internalSize; i++)
			{
				int index = internal[i];
				if (index >= chunkStartIndex && index < chunkStartIndex + this.chunkSize)
				{
					this.currentCollection.set(index - chunkStartIndex, null);
				}
			}
		}
	}

	private synchronized void saveCurrentCollection() {
		if (this.currentCollection == null)
		{
			return;
		}
		try
		{
			byte[] data = serializeCollectionChunk(this.currentCollection);
			this.storageSystem.saveChunkData(this.currentCollectionIndex, identifier, data);
		} 
		catch (IOException e)
		{
			//#debug error
			System.out.println("Unable to save current collection " + e);
			e.printStackTrace();
		}
	}
	
	private synchronized byte[] serializeCollectionChunk(ArrayList collection)
	{
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(byteOut);
		try
		{
			out.writeInt(PERSISTENCE_VERSION);
			Object[] objects = collection.getInternalArray();
			for (int i=0; i < this.chunkSize; i++)
			{
				Externalizable externalizable = (Externalizable) objects[i];
				externalizable.write(out);
			}
			out.flush();
			byte[] data = byteOut.toByteArray();
			return data;
		} 
		catch (Exception e)
		{
			//#debug error
			System.out.println("Unable to save collection chunk " + e);
			e.printStackTrace();
		}
		finally
		{
			try { out.close(); } catch (Exception e) { }
		}
		return null;
	}
	
	
	protected synchronized ArrayList getTailCollection()
	{
		if (!this.tailCollectionIsLoaded || this.tailCollection == null )
		{
			loadTailCollection();
		}
		return this.tailCollection;
	}

	protected synchronized ArrayList getCurrentCollection()
	{
		return this.currentCollection;
	}
	
	public synchronized void releaseResources()
	{
		this.tailCollection.clear();
		this.tailCollectionIsLoaded = false;
		if (this.currentCollection != null)
		{
			this.currentCollection.clear();
			this.currentCollectionIndex = -1;
			this.currentCollectionIsDirty = false;
		}
	}
	
	public synchronized void saveCollection() throws IOException
	{
		if (this.tailCollection != null)
		{
			boolean saveTailCollection = this.tailCollectionIsDirty || containsDirtyElement(this.tailCollection);
			if (saveTailCollection)
			{
				saveTailCollection();
			}
		}
		if (this.currentCollection != null)
		{
			boolean saveCurrentCollection = this.currentCollectionIsDirty || containsDirtyElement(this.currentCollection);
			if (saveCurrentCollection)
			{
				saveCurrentCollection();
				this.currentCollectionIsDirty = false;
			}
		}
	}
	
	/**
	 * Checks if this collection contains at least one updated element
	 * @return true when this collection contains at least one updated element
	 */
	public synchronized boolean isDirty()
	{
		if (this.currentCollectionIsDirty || this.tailCollectionIsDirty)
		{
			return true;
		}
		if (this.tailCollectionIsLoaded && containsDirtyElement(getTailCollection()))
		{
			return true;
		}
		if (this.currentCollection != null && containsDirtyElement(this.currentCollection))
		{
			return true;
		}
		return false;
	}
	

	private synchronized boolean containsDirtyElement(ArrayList collection) 
	{
		int size = collection.size();
		Object[] objects = collection.getInternalArray();
		for (int i=0; i < size; i++)
		{
			Mutable mutable = (Mutable) objects[i];
			if ((mutable != null) && mutable.isDirty())
			{
				return true;
			}
		}
		return false;
	}
}
