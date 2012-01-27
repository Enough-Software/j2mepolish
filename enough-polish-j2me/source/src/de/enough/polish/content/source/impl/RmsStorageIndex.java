package de.enough.polish.content.source.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotOpenException;


import de.enough.polish.content.storage.StorageIndex;
import de.enough.polish.io.RmsStorage;
import de.enough.polish.io.Serializer;
import de.enough.polish.util.ArrayList;

/**
 * Defines a Storage Index using the RMS for persistence 
 * @author Ovidiu Iliescu
 */
public class RmsStorageIndex extends StorageIndex {
	
	/**
	 * The default record store name to use
	 */
	static final String STORAGE = "RMSStorageIndex";
	
	/**
	 * Indicates that the index of the actual record containing the StorageIndex is not known
	 */
	static final int RECORD_UNKNOWN = Integer.MIN_VALUE;

	/**
	 * The record store holding the index
	 */
	RecordStore store;
	
	/**
	 * The actual recordID of the record holding the index
	 */
	int recordId = RECORD_UNKNOWN;

	public RmsStorageIndex(long maxCacheSize) {
		this(STORAGE,maxCacheSize);
	}

	/**
	 * Creates a new RmsStorageIndex instance
	 * @param recordStoreName the name of the record store to use for persisting the index
	 * @param maxCacheSize the maximum size of the index
	 */
	public RmsStorageIndex(String recordStoreName, long maxCacheSize) {
		super(maxCacheSize);

		// When using a dedicated store, use fail-safe behavior
		if ( STORAGE.equals(recordStoreName) ) {
			try {
				// open the record store
				this.store = RecordStore.openRecordStore(recordStoreName, true);
				RecordEnumeration recordEnumeration = this.store.enumerateRecords(
						null, null, false);
				
				if (recordEnumeration.hasNextElement()) {
					this.recordId = recordEnumeration.nextRecordId();
					
					//#debug debug
					System.out.println("index record id : " + this.recordId);
				}
				
			} catch (RecordStoreException e) {
				//#debug error
				System.out.println("unable to open record store " + e);
			}
		} else {
			// When using a custom store, make sure the index is always stored in the first record
			this.recordId = 1;
			
			// Initialize the first record
			try {
				// open the record store
				this.store = RecordStore.openRecordStore(recordStoreName, true);
				
				// see if the first record exists. if it does, we're done
				store.getRecord(1);				
			} catch (InvalidRecordIDException e) {				
				// If it doesn't exist, reserve a spot for it and serialize an empty index at that location
				try {
					this.store.addRecord(new byte[1], 0, 1);
					store(this.index);
				} catch (Exception e2) {
					//#debug error
					System.out.println("unable to initialize index " + e);
				}
			} catch (RecordStoreException e) {
				//#debug error
				System.out.println("unable to open record store " + e);
			}
		}		
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.content.storage.StorageIndex#getAvailableCacheSize()
	 */
	public long getAvailableCacheSize() {
		try {			
			long available = Math.min(this.maxCacheSize - getCacheSize(), store.getSizeAvailable() - getCacheSize());
			return available;
		} catch (RecordStoreNotOpenException e) {
			return this.maxCacheSize;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.zyb.nowplus.business.content.StorageIndex#load()
	 */
	protected ArrayList load() {
		try {
			if(this.recordId != RECORD_UNKNOWN)
			{
				// get the bytes
				byte[] bytes = store.getRecord(this.recordId);
				
				//deserialize the data
				ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
				Object data = Serializer.deserialize(new DataInputStream(byteStream));
				
				return (ArrayList)data;
			}
		} catch (IOException e) {
			//#debug error
			System.out.println("unable to read index " + e);
		}
		 catch (RecordStoreException e) {
			//#debug error
			System.out.println("unable to open record store " + e);
		}
		 
		 return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.zyb.nowplus.business.content.StorageIndex#store(de.enough.polish.
	 * util.ArrayList)
	 */
	protected void store(ArrayList index) {
		try {
			// serialize the data and convert it to a byte array
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			Serializer.serialize(index, new DataOutputStream(byteStream));

			// get the bytes
			byte[] bytes = byteStream.toByteArray();
			
			if(this.recordId != RECORD_UNKNOWN)
			{
				// set the record
				store.setRecord(this.recordId, bytes, 0, bytes.length);
			}
			else
			{			
				// Create the record
				this.recordId = store.addRecord(bytes,0,bytes.length);
			}
		} catch (IOException e) {
			//#debug error
			System.out.println("unable to serialize index " + e);
		} catch (RecordStoreException e) {
			//#debug error
			System.out.println("unable to store index " + e);
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.content.storage.StorageIndex#shutdown()
	 */
	public void shutdown() {
		super.shutdown();

		try {
			store.closeRecordStore();
		} catch (RecordStoreException e) {
			//#debug error
			System.out.println("unable to close index " + e.toString());
		}
	}

}
