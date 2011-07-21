//#condition polish.blackberry
package de.enough.polish.content.source.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.rms.RecordStoreException;

import de.enough.polish.blackberry.persistent.PersistentRecordEnumeration;
import de.enough.polish.blackberry.persistent.PersistentRecordStore;
import de.enough.polish.content.storage.StorageIndex;
import de.enough.polish.io.Serializer;
import de.enough.polish.util.ArrayList;

public class PersistentStorageIndex extends StorageIndex {
	static final String STORAGE = "RMSStorageIndex";
	static final int RECORD_UNKNOWN = Integer.MIN_VALUE;

	PersistentRecordStore store;
	int recordId = RECORD_UNKNOWN;

	public PersistentStorageIndex(int maxCacheSize) {
		super(maxCacheSize);

		try {
			// open the record store
			this.store = PersistentRecordStore.openRecordStore(STORAGE, true);
			PersistentRecordEnumeration recordEnumeration = this.store.enumerateRecords(
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
				// add the record
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
