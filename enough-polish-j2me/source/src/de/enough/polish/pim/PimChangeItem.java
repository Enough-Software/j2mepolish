/**
 * 
 */
package de.enough.polish.pim;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

import de.enough.polish.io.Externalizable;

/**
 * @author Rama
 *
 */
public class PimChangeItem implements Externalizable {
	
	/**
	 * field to capture unique id of PIM Item
	 */
	private String uid;
	
	/**
	 * field to capture last modified date and time of PIM Item
	 */
	private Date revisionDate;
	
	/**
	 * field to capture type of PIM Item
	 */
	private String typeOfItem;
	
	/**
	 * field to hold version number
	 */
	private long version;
	
	/**
	 * 
	 * @return returns unique id of PIM Item
	 */
	public String getUid() {
		return this.uid;
	}
	
	/**
	 * setter method for unique id of PIM Item
	 * @param uid unique id of PIM Item
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	/**
	 * @return returns last revision date of PIM Item
	 */
	public Date getRevisionDate() {
		return this.revisionDate;
	}
	
	/**
	 * setter method for last revision date of PIM Item
	 * @param revisionDate
	 */
	public void setRevisionDate(Date revisionDate) {
		this.revisionDate = revisionDate;
	}
	/**
	 * @return returns type of PIM Item
	 */
	public String getTypeOfItem() {
		return this.typeOfItem;
	}
	/**
	 * setter method for type of PIM Item
	 * @param typeOfItem
	 */
	public void setTypeOfItem(String typeOfItem) {
		this.typeOfItem = typeOfItem;
	}
	
	/**
	 * @return returns version number
	 */
	public long getVersion() {
		return this.version;
	}
	
	/**
	 * @param version version number
	 */
	public void setVersion(long version) {
		this.version = version;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#read(java.io.DataInputStream)
	 */
	public void read(DataInputStream in) throws IOException {
		this.uid = in.readUTF();
		this.version = in.readLong();
		this.typeOfItem = in.readUTF();
		long time = in.readLong();
		if(time == 1L) {
			this.setRevisionDate(null);
		} else {
			Date d = new Date();
			d.setTime(time);
			this.setRevisionDate(d);
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#write(java.io.DataOutputStream)
	 */
	public void write(DataOutputStream out) throws IOException {
		out.writeUTF(this.uid);
		out.writeLong(this.version);
		out.writeUTF(this.typeOfItem);
		if(this.getRevisionDate() != null) {
			out.writeLong(this.getRevisionDate().getTime());
		} else {
			out.writeLong(1L);
		}
	}
	
	
}
