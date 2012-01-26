package de.enough.polish.content.source.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

import de.enough.polish.content.ContentDescriptor;
import de.enough.polish.content.ContentException;
import de.enough.polish.content.source.ContentSource;
import de.enough.polish.content.storage.StorageIndex;
import de.enough.polish.content.storage.StorageReference;
import de.enough.polish.io.Serializer;

/**
 * @author Andre Schmidt
 * 
 */
public class RmsContentStorage extends ContentSource {
	
	public static final String STORAGE = "RMSContentStorage";
	
	protected RecordStore store;
	
	public static int getMaximumTotalSize() {
		int maxSize = 0;
		int currentSize = 0;
		RecordStore rs = null;
		try {
			rs = RecordStore.openRecordStore(STORAGE, true);
			currentSize = rs.getSize();
			maxSize = currentSize + rs.getSizeAvailable();			
		} catch (Exception ex) {
			// Do nothing
		} finally {
			try {
				// Close record store
				if (rs != null) {
					rs.closeRecordStore();
				}
				
				// If the store is empty, delete it as well
				if ( currentSize == 0 ) {
					RecordStore.deleteRecordStore(STORAGE);
				}
			} catch (Exception ex) {
				// Do nothing
			}
		}
		return maxSize;		
	}
	public RmsContentStorage(String id, StorageIndex index) {
		this(id, STORAGE, index);
	}

	public RmsContentStorage(String id, String recordStoreName, StorageIndex index) {
		super(id, index);
		
		// open the record store
		try {
			store = RecordStore.openRecordStore(recordStoreName, true);
		} catch (RecordStoreException e) {
			//#debug error
			System.out.println("unable to open record store " + e);
		}
	}
	
	public synchronized void shutdown() {
		// close the record store
		try
		{
			store.closeRecordStore();
		} catch (RecordStoreException e) {
			//#debug error
			System.out.println("unable to close record store " + e);
		}
		
		super.shutdown();
	}


	protected synchronized  void destroy(final StorageReference reference) throws IOException {
		try {
			// get the record id
			int recordId = ((Integer)reference.getReference()).intValue();
			
			// delete the record
			store.deleteRecord(recordId);
			
		} catch (RecordStoreException e) {
			throw new IOException("unable to delete data " + e);
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.content.source.ContentSource#storeContentAndGetDataSize(de.enough.polish.content.ContentDescriptor, java.lang.Object)
	 */
	protected Object[] storeContentAndGetDataSize(ContentDescriptor descriptor, Object data) throws IOException, ContentException {
		
		// serialize the data and convert it to a byte array 
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		Serializer.serialize(data, new DataOutputStream(byteStream));
		
		// get the bytes
		byte[] bytes = byteStream.toByteArray();		 		

		// If the data we are trying to store is bigger than the cache itself, don't even bother
		if ( bytes.length > getStorageIndex().getAvailableCacheSize() ) {
			return new Object[] { new Integer(bytes.length), null };
		}
		
		// Try to do a clean first, in case it is needed.
		clean(bytes.length);
		
		// add the record, if possible
		Integer recordId = null;
		try {
				if ( !this.getStorageIndex().isCleanNeeded(bytes.length) ) {
					recordId = new Integer(store.addRecord(bytes, 0, bytes.length));
					//#debug debug
					System.out.println("added record for " + descriptor.getUrl());
				} else {
					//#debug debug
					System.out.println("not enough space to add record for " + descriptor.getUrl());
				}
		} catch (Exception e) {
			//#debug debug
			System.out.println("exception while storing " + descriptor.getUrl() + " : " + e.getClass().getName() + " " + e.getMessage());
		}
		
		return new Object[] { new Integer(bytes.length), recordId};			
	}

	protected synchronized Object store(ContentDescriptor descriptor, Object data) throws IOException {
		// Do nothing here as the #storeContentAndGetDataSize method will be used. instead
		return null;
	}

	protected synchronized Object load(ContentDescriptor descriptor)
			throws IOException {
		// do nothing here as it is a storage
		return null;
	}

	protected synchronized Object load(StorageReference reference) throws IOException {
		try {
			// get the record id
			int recordId = ((Integer)reference.getReference()).intValue();
			
			// get the bytes
			byte[] bytes = store.getRecord(recordId);
			
			//deserialize the data
			ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
			Object data = Serializer.deserialize(new DataInputStream(byteStream));
			
			return data;
		} catch (RecordStoreException e) {
			//#debug error
			System.out.println("unable to load data " + e);
			return null;
		}
	}
	
	
}
