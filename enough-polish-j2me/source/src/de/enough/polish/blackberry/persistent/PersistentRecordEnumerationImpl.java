//#condition polish.blackberry
package de.enough.polish.blackberry.persistent;

import java.util.Vector;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

import de.enough.polish.blackberry.persistent.PersistentRecordEnumeration;
import de.enough.polish.blackberry.persistent.PersistentRecordStore;

/**
 * The PersistentRecordEnumeration implementation
 * @author Andre
 *
 */
public class PersistentRecordEnumerationImpl implements PersistentRecordEnumeration{

	/**
	 * the record store
	 */
	PersistentRecordStore store;
	
	/**
	 * the record ids
	 */
	Vector recordIds;
	
	int recordIdIndex;
	
	/**
	 * Creates a new PersistentRecordEnumerationImpl instance
	 * @param store the record store
	 */
	public PersistentRecordEnumerationImpl(PersistentRecordStore store) {
		this.store = store;
		this.recordIds = store.getRecordIds();
		this.recordIdIndex = -1;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.persistent.PersistentRecordEnumeration#destroy()
	 */
	public void destroy() {
		//TODO implement
	}

	
	/* (non-Javadoc)
	 * @see de.enough.polish.persistent.PersistentRecordEnumeration#hasNextElement()
	 */
	public boolean hasNextElement() {
		return (hasRecordIds() && this.recordIdIndex + 1 < this.recordIds.size());
	}

	
	/* (non-Javadoc)
	 * @see de.enough.polish.persistent.PersistentRecordEnumeration#hasPreviousElement()
	 */
	public boolean hasPreviousElement() {
		return (hasRecordIds() && this.recordIdIndex - 1 >= 0);
	}

	
	public boolean isKeptUpdated() {
		//TODO implement
		return false;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.persistent.PersistentRecordEnumeration#keepUpdated(boolean)
	 */
	public void keepUpdated(boolean keepUpdated) {
		//TODO implement
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.persistent.PersistentRecordEnumeration#nextRecord()
	 */
	public byte[] nextRecord() throws InvalidRecordIDException,
			RecordStoreNotOpenException, RecordStoreException {
		int recordId = nextRecordId();
		return this.store.getRecord(recordId);
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.persistent.PersistentRecordEnumeration#nextRecordId()
	 */
	public int nextRecordId() throws InvalidRecordIDException {
		if(!moveToNextRecordId()) {
			throw new InvalidRecordIDException("there is no next record available");
		}
		
		return getSelectedRecordId();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.persistent.PersistentRecordEnumeration#numRecords()
	 */
	public int numRecords() {
		try {
			return this.store.getNumRecords();
		} catch (RecordStoreNotOpenException e) {
			//#debug error
			System.out.println("record store " + this.store.getName() + " is not open");
			e.printStackTrace();
			return -1;
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.persistent.PersistentRecordEnumeration#previousRecord()
	 */
	public byte[] previousRecord() throws InvalidRecordIDException,
			RecordStoreNotOpenException, RecordStoreException {
		int recordId = previousRecordId();
		return this.store.getRecord(recordId);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.persistent.PersistentRecordEnumeration#previousRecordId()
	 */
	public int previousRecordId() throws InvalidRecordIDException {
		if(!moveToPreviousRecordId()) {
			throw new InvalidRecordIDException("there is no previous record available");
		}
		
		return getSelectedRecordId();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.persistent.PersistentRecordEnumeration#rebuild()
	 */
	public void rebuild() {
		//TODO implement
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.persistent.PersistentRecordEnumeration#reset()
	 */
	public void reset() {
		//TODO implement
	}
	
	/**
	 * Returns true if there are record ids
	 * @return true if there are record ids otherwise false
	 */
	boolean hasRecordIds() {
		return this.recordIds.size() > 0;
	}
	
	/**
	 * Move the index to the next record id
	 * @return true if there is a next index otherwise false
	 */
	boolean moveToNextRecordId() {
		if(!hasNextElement()) {
			return false;
		} else {
			this.recordIdIndex++;
			return true;
		}
	}
	
	/**
	 * Move the index to the previous record id
	 * @return true if there is a previous index otherwise false
	 */
	boolean moveToPreviousRecordId() {
		if(!hasPreviousElement()) {
			return false;
		} else {
			this.recordIdIndex--;
			return true;
		}
	}
	
	/**
	 * Returns the selected record id from the record id vector
	 * @return the record id
	 */
	protected int getSelectedRecordId() throws ArrayIndexOutOfBoundsException {
		Integer recordIdInt = (Integer)this.recordIds.elementAt(this.recordIdIndex);
		return recordIdInt.intValue();
	}
	
}
