package de.enough.polish.content.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.enough.polish.content.ContentDescriptor;
import de.enough.polish.io.Serializer;
import de.enough.polish.util.ToStringHelper;

/**
 * Represents a reference to stored content data.
 * 
 * @author Andre Schmidt
 * 
 */
public class StorageReference extends ContentDescriptor {
	/**
	 * the reference to the data
	 */
	Object reference;

	/**
	 * the creation time of this reference
	 */
	long creationTime;

	/**
	 * the size of the content data
	 */
	int size;

	/**
	 * the activity of the content data
	 */
	int activityCount;

	/**
	 * the last activity time of the content data
	 */
	long activityTime;

	/**
	 * Default descriptor for instantiation for serialization
	 * DO NOT USE !
	 */
	public StorageReference() {}
	
	/**
	 * Creates a new StorageReference instance.
	 * 
	 * @param descriptor
	 *            the content descriptor
	 * @param size
	 *            the size of the content data
	 * @param reference
	 *            the reference to the content data
	 */
	public StorageReference(ContentDescriptor descriptor, int size,
			Object reference) {
		super(descriptor);

		this.creationTime = System.currentTimeMillis();
		this.reference = reference;
		this.size = size;
		this.activityCount = 0;
		this.activityTime = this.creationTime;
		this.priority = descriptor.getPriority();
	}

	/**
	 * Returns the reference to the content data
	 * 
	 * @return the reference to the content data
	 */
	public Object getReference() {
		return this.reference;
	}

	/**
	 * Returns the creation time
	 * 
	 * @return the creation time
	 */
	public long getCreationTime() {
		return this.creationTime;
	}

	/**
	 * Returns the size
	 * 
	 * @return the size
	 */
	public int size() {
		return this.size;
	}

	/**
	 * Returns the activity
	 * 
	 * @return the activity
	 */
	public int getActivity() {
		return this.activityCount;
	}

	/**
	 * Returns the last activity time
	 * 
	 * @return the last activity time
	 */
	public long getLastActivityTime() {
		return this.activityTime;
	}

	/**
	 * Updates the activity and last activity time
	 */
	public void updateActivity() {
		this.activityCount++;
		this.activityTime = System.currentTimeMillis();
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.content.ContentDescriptor#read(java.io.DataInputStream)
	 */
	public void read(DataInputStream in) throws IOException {
		super.read(in);
		this.reference = Serializer.deserialize(in);
		this.creationTime = in.readLong();
		this.size = in.readInt();
		this.activityCount = in.readInt();
		this.activityTime = in.readLong();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.content.ContentDescriptor#write(java.io.DataOutputStream)
	 */
	public void write(DataOutputStream out) throws IOException {
		super.write(out);
		Serializer.serialize(this.reference, out);
		out.writeLong(this.creationTime);
		out.writeInt(this.size);
		out.writeInt(this.activityCount);
		out.writeLong(this.activityTime);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return ToStringHelper.createInstance("StorageReference").set("url", this.url).set(
				"hash", this.hash).set("creationTime", this.creationTime).set(
				"activityCount", this.activityCount).set("activityTime",
				this.activityTime).set("reference", this.reference).set("size",
				this.size).toString();
	}
}
