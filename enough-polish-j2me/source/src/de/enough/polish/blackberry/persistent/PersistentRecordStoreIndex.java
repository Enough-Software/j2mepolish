//#condition polish.blackberry
package de.enough.polish.blackberry.persistent;

import java.util.Enumeration;
import java.util.Hashtable;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.util.Persistable;

/**
 * A class used as an index for PersistentRecordStore instances.
 * Usually used through the PersistentRecordStore.
 * @author Andre
 *
 */
public class PersistentRecordStoreIndex implements Persistable{
	// the hashtable to store name and id of a DataCollection instance
	Hashtable index;
	
	// the DataCollectionIndex instance
	static PersistentRecordStoreIndex instance;
	
	/**
	 * Returns the instance of the DataCollectionIndex. If the instance is not set yet it is 
	 * tried to be retrieve from the PersistentStorage. If its not found a new instance is
	 * created and stored under its ID.
	 * @return the instance of the DataCollectionIndex
	 */
	public static PersistentRecordStoreIndex getInstance() {
		// if the instance is not set yet ...
		if(instance == null) {
			// try to retrieve the DataCollectionIndex from the PersistentStorage
			PersistentObject store = PersistentStore.getPersistentObject(PersistentRecordStoreConstraints.RECORDSTOREINDEX_ID);
			PersistentRecordStoreIndex index = (PersistentRecordStoreIndex)store.getContents();
			
			// if it was not found ...
			if(index == null) {
				// create a new one and store it in the PersistentStorage
				index = new PersistentRecordStoreIndex();
				store.setContents(index);
				store.forceCommit();
			}
			
			instance = index;
		}
		
		return instance;
	}
	
	/**
	 * Create a new DataCollectionIndex
	 */
	PersistentRecordStoreIndex() {
		this.index = new Hashtable();
	}
	
	/**
	 * Returns true if this index has a DataCollection with the given name
	 * @param name the name of the DataCollection
	 * @return true if this index has a DataCollection with the given name otherwise false 
	 */
	public boolean hasRecordStore(String name) {
		return this.index.containsKey(name);
	}
	
	/**
	 * Returns the DataCollection with the given name
	 * @param name the name of the DataCollection
	 * @return the stored instance of the specified DataCollection if its stored in the PersistentStorage otherwise null
	 */
	public PersistentRecordStore getRecordStore(String name) {
		Long persistentIdLong = (Long)this.index.get(name);
		
		if(persistentIdLong != null) {
			PersistentObject store = PersistentStore.getPersistentObject(persistentIdLong.longValue());
			return (PersistentRecordStore)store.getContents();
		} else {
			return null;
		}
	}
	
	/**
	 * Create a DataCollection and returns it.
	 * @param name the name of the record store
	 * @return the created record store
	 */
	public PersistentRecordStore createRecordStore(String name) {
		// get the id from the running DataCollection ID
		Long persistentId = new Long(getFreePersistentID());
		
		// save it to the index with the name as key
		this.index.put(name, persistentId);
		
		// create a new PersistentRecordStore instance
		PersistentRecordStore recordStore = new PersistentRecordStore(name, persistentId.longValue());
		
		// save PersistentRecordStore to PersistentStorage
		PersistentObject persistent = PersistentStore.getPersistentObject(persistentId.longValue());
		persistent.setContents(recordStore);
		persistent.forceCommit();
		
		// store this instance in the PersistentStorage
		commit();
		
		return recordStore;
	}
	
	/**
	 * Returns the next free id in the PersistentStore
	 * @return the next free id 
	 */
	long getFreePersistentID() {
		long persistentId = PersistentRecordStoreConstraints.RECORDSTORE_OFFSET;
		
		while(persistentId < (PersistentRecordStoreConstraints.RECORDSTORE_MAX_NUMBER * PersistentRecordStoreConstraints.RECORDSTORE_RANGE)) {
			PersistentObject persistent = PersistentStore.getPersistentObject(persistentId);
			
			Object object = persistent.getContents();
			
			if(object == null) {
				return persistentId;
			}
			
			persistentId += PersistentRecordStoreConstraints.RECORDSTORE_RANGE;
		}
		
		throw new IllegalStateException("Too many record stores");
	}
	
	/**
	 * Deletes the specified record store
	 * @param name the name of the record store
	 */
	public boolean deleteRecordStore(String name) {
		if(hasRecordStore(name)) {
			PersistentRecordStore store = getRecordStore(name);
			
			store.clearRecordStore();
			
			Long persistentId = (Long)this.index.remove(name);
			
			PersistentStore.destroyPersistentObject(persistentId.longValue());
			
			// store this instance in the PersistentStorage
			commit();
			
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Returns a list of string with names of the stored record stores
	 * @return the list
	 */
	public String[] getRecordStoreNames() {
		String[] names = new String[this.index.size()];
		Enumeration enumeration = this.index.keys();
		
		int index = 0;
		while(enumeration.hasMoreElements()) {
			String name = (String)enumeration.nextElement();
			names[index] = name;
			index++;
		}
		
		return names;
	}
	
	public void commit() {
		// store this instance in the PersistentStorage
		PersistentObject store = PersistentStore.getPersistentObject(PersistentRecordStoreConstraints.RECORDSTOREINDEX_ID);
		store.setContents(this);
		store.forceCommit();
	}
	
	/**
	 * Wipes all record stores and the index
	 */
	public static void wipeIndex() {
		PersistentRecordStoreIndex recordStoreIndex = PersistentRecordStoreIndex.getInstance();
		String[] names = recordStoreIndex.getRecordStoreNames();
		
		for (int index = 0; index < names.length; index++) {
			String name = names[index];
			recordStoreIndex.deleteRecordStore(name);
		}
		
		PersistentStore.destroyPersistentObject(PersistentRecordStoreConstraints.RECORDSTOREINDEX_ID);
	}
	
	/**
	 * Wipes everything in the PersistentStore
	 */
	public static void wipeStore() {
		for (int i = 0; i <= Long.MAX_VALUE; i++) {
			PersistentStore.destroyPersistentObject(i);
		}
	}
}
