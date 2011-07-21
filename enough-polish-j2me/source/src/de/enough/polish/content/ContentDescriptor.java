package de.enough.polish.content;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Hashtable;

import de.enough.polish.io.Externalizable;
import de.enough.polish.io.Serializer;
import de.enough.polish.util.ToStringHelper;

/**
 * A class to identify a content with an url, a version and a transform id
 * 
 * @author Andre
 * 
 */
public class ContentDescriptor implements Externalizable {
	/**
	 * the default version
	 */
	public static String TRANSFORM_NONE = "none";

	/**
	 * the default version
	 */
	public static int VERSION_DEFAULT = Integer.MIN_VALUE;

	/**
	 * the default caching policy for only read
	 */
	public static int CACHING_READ = 0x01;

	/**
	 * the default caching policy for read and writing of content
	 */
	public static int CACHING_READ_WRITE = 0x02;

	/**
	 * the url
	 */
	protected String url;

	/**
	 * the hash of the url
	 */
	protected int hash;

	/**
	 * the transformation
	 */
	protected String transformId = TRANSFORM_NONE;

	/**
	 * the version
	 */
	protected int version = VERSION_DEFAULT;

	/**
	 * the caching policy
	 */
	protected int cachingPolicy = CACHING_READ_WRITE;
	
	/**
	 * the attributes hashtable
	 */
	protected Hashtable attributes = new Hashtable();
	
	/**
	 * the priority of this content. content with higher priorities get deleted last.
	 */
	protected int priority = 0;

	
	/**
	 * Default descriptor for instantiation for serialization
	 * DO NOT USE !
	 */
	public ContentDescriptor() {
		// do nothing
	}
	
	/**
	 * Creates a new ContentDescriptor instance
	 * 
	 * @param url
	 *            the url
	 */
	public ContentDescriptor(String url) {
		this.url = url;
		this.hash = (url == null) ? 0 : url.hashCode();
	}

	/**
	 * Copies a ContentDescriptor instance
	 * 
	 * @param descriptor
	 *            the descriptor to copy
	 */
	protected ContentDescriptor(ContentDescriptor descriptor) {
		this.url = descriptor.getUrl();
		this.hash = descriptor.getHash();
		this.version = descriptor.getVersion();
		this.transformId = descriptor.getTransformID();
	}
	
	/**
	 * Sets the priority
	 * @param priority
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	/**
	 * Returns the priority
	 * @return
	 */
	public int getPriority() {
		return this.priority;
	}

	/**
	 * Returns the url
	 * 
	 * @return the url
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * Returns the hash of the url
	 * 
	 * @return the hash of the url
	 */
	public int getHash() {
		return this.hash;
	}

	/**
	 * Returns the transform id
	 * 
	 * @return the transform id
	 */
	public String getTransformID() {
		return this.transformId;
	}

	/**
	 * Set the transform id
	 * 
	 * @param transformId
	 *            the transform id
	 */
	public void setTransformID(String transformId) {
		this.transformId = transformId;
	}

	/**
	 * Returns the version
	 * 
	 * @return the version
	 */
	public int getVersion() {
		return this.version;
	}

	/**
	 * Set the version
	 * 
	 * @param version
	 *            the version
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * Returns the caching policy
	 * 
	 * @return the caching policy
	 */
	public int getCachingPolicy() {
		return this.cachingPolicy;
	}

	/**
	 * Sets the caching policy
	 * 
	 * @param cachingPolicy
	 *            the caching policy
	 */
	public void setCachingPolicy(int cachingPolicy) {
		this.cachingPolicy = cachingPolicy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		ContentDescriptor reference = (ContentDescriptor) obj;
		return reference.getHash() == getHash();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return getHash();
	}
	
	/**
	 * Retrieves an attribute
	 * @param attributeKey the attribute's key
	 * @return the value of the attribute
	 */
	public Object getAttribute(Object attributeKey) {
		return this.attributes.get(attributeKey);
	}
	
	/**
	 * Sets an attribute
	 * @param attributeKey the attribute's key
	 * @param attributeValue the attribute itself
	 */
	public void setAttribute(Object attributeKey, Object attributeValue) {
		this.attributes.put(attributeKey, attributeValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return ToStringHelper.createInstance("ContentDescriptor").set("url", this.url)
				.set("hash", this.hash).set("version", this.version).set(
						"cachingPolicy", this.cachingPolicy).toString();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#read(java.io.DataInputStream)
	 */
	public void read(DataInputStream in) throws IOException {
		this.url = in.readUTF();
		this.hash = in.readInt();
		this.transformId = in.readUTF();
		this.version = in.readInt();
		this.cachingPolicy = in.readInt();
		this.priority = in.readInt();
		this.attributes = (Hashtable) Serializer.deserialize(in);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#write(java.io.DataOutputStream)
	 */
	public void write(DataOutputStream out) throws IOException {
		out.writeUTF(this.url);
		out.writeInt(this.hash);
		out.writeUTF(this.transformId);
		out.writeInt(this.version);
		out.writeInt(this.cachingPolicy);
		out.writeInt(this.priority);
		Serializer.serialize(this.attributes, out);
	}

}
