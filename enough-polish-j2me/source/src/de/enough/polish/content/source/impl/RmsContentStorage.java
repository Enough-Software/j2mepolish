package de.enough.polish.content.source.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import de.enough.polish.content.ContentDescriptor;
import de.enough.polish.content.source.ContentSource;
import de.enough.polish.content.storage.StorageIndex;
import de.enough.polish.content.storage.StorageReference;
import de.enough.polish.io.Serializer;

/**
 * @author Andre Schmidt
 * 
 */
public class RmsContentStorage extends ContentSource {
	
	static final String STORAGE = "RMSContentStorage";
	
	protected RecordStore store;

	public RmsContentStorage(String id, StorageIndex index) {
		super(id, index);
		
		// open the record store
		try {
			store = RecordStore.openRecordStore(STORAGE, true);
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
			
			// add the record
			store.deleteRecord(recordId);
			
		} catch (RecordStoreException e) {
			throw new IOException("unable to delete data " + e);
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.content.source.ContentSource#storeContentAndGetDataSize(de.enough.polish.content.ContentDescriptor, java.lang.Object)
	 */
	protected Object[] storeContentAndGetDataSize(ContentDescriptor descriptor, Object data) throws IOException {
		try {
			// serialize the data and convert it to a byte array 
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			Serializer.serialize(data, new DataOutputStream(byteStream));
			
			// get the bytes
			byte[] bytes = byteStream.toByteArray();
			
			// add the record
			int recordId = store.addRecord(bytes, 0, bytes.length);
			
			return new Object[] { new Integer(bytes.length), new Integer(recordId)};
		} catch (RecordStoreException e) {
			throw new IOException("unable to store data " + e);
		}				
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
