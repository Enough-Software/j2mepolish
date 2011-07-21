//#condition polish.blackberry
package de.enough.polish.blackberry.persistent;

import de.enough.polish.util.ToStringHelper;
import net.rim.device.api.util.Persistable;

/**
 * A class representing a record of bytes for the PersistentStore
 * @author Andre
 *
 */
public class PersistentRecord implements Persistable {
	
	/**
	 * the data
	 */
	byte[] data;
	
	/**
	 * the size 
	 */
	int size;
	
	/**
	 * Create a new PersistentRecord instance
	 * @param data the data
	 */
	public PersistentRecord(byte[] data) {
		this.data = data;
		this.size = data.length;
	}
	
	/**
	 * Sets the record
	 * @param data the data
	 */
	public void set(byte[] data) {
		this.data = data;
		this.size = data.length;
	}
	
	/**
	 * Returns the data
	 * @return the data
	 */
	public byte[] getData() {
		return this.data;
	}
	
	/**
	 * Returns the size
	 * @return the size
	 */
	public int getSize() {
		return this.size;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return ToStringHelper.createInstance("PersistentRecord").
		set("data", this.data).
		set("size", this.size).
		toString();
	}
}
