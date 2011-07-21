package de.enough.polish.util;

/**
 * A helper class to create conforming toString outputs
 * @author Andre
 *
 */
public class ToStringHelper {
	
	static String FORMAT_HEADER = " [";
	
	static String FORMAT_FOOTER = "]";
	
	static String  FORMAT_DESCRIPTION_SEPARATOR = ":";
	
	static String  FORMAT_DESCRIPTION_LIST_SEPARATOR = " , ";
	
	static String  FORMAT_DESCRIPTION_KEYVALUE_SEPARATOR = " : ";
	
	static String  FORMAT_LIST_SEPARATOR = " / ";
	
	/**
	 * The name of the parenting object
	 */
	String name;
	
	StringBuffer buffer;

	private boolean isNameInserted;
	private boolean addListSeparator;
	
	/**
	 * Creates a new instance of ToStringHelper
	 *  
	 * @param name the name of instance
	 * @return a new ToStringHelper instance
	 */
	public static ToStringHelper createInstance(String name) {		
		return new ToStringHelper(name);
	}
	
	/**
	 * Creates a new ToStringHelper instance
	 */
	public ToStringHelper() {
		this(null);
	}
	
	/**
	 * Creates anew ToStringHelper instance
	 * @param object the object
	 */
	public ToStringHelper(Object object) {
		// resolve class and use as name
		this(object.getClass().getName());
	}
	
	/**
	 * Creates a new ToStringHelper instance
	 * @param name the name to use
	 */
	public ToStringHelper(String name) {
		this.buffer = new StringBuffer();
		this.buffer.append(FORMAT_HEADER);
		this.name = name;
	}
	
	/**
	 * Clear the attribute map
	 */
	void clear() {
		if (this.isNameInserted) {
			this.buffer.delete(0, this.buffer.length());
			this.isNameInserted = false;
			this.buffer.append(FORMAT_HEADER);
		} else {
			this.buffer.delete(FORMAT_HEADER.length(), this.buffer.length());
		}
		this.addListSeparator = false;
	}

	/**
	 * sets the name
	 * @param name the name
	 */
	void setName(String name) {
		if (this.isNameInserted) {
			// remove current name from buffer:
			this.buffer.delete(0, this.name.length());
			this.isNameInserted = false;
		}
		this.name = name;
	}
	
	/**
	 * sets a numerical value
	 * @param id the id 
	 * @param value the value
	 * @return the ToStringHelper instance
	 */
	public ToStringHelper set(String id, long value) {
		if (this.addListSeparator) {
			this.buffer.append(FORMAT_LIST_SEPARATOR);
		}
		this.buffer.append(id).append(FORMAT_DESCRIPTION_SEPARATOR).append(value);
		this.addListSeparator = true;
		return this;
	}

	/**
	 * sets a boolean value
	 * @param id the id 
	 * @param value the value
	 * @return the ToStringHelper instance
	 */
	public ToStringHelper set(String id, boolean value) {
		if (this.addListSeparator) {
			this.buffer.append(FORMAT_LIST_SEPARATOR);
		}
		this.buffer.append( id ).append(FORMAT_DESCRIPTION_SEPARATOR).append(value);
		this.addListSeparator = true;
		return this;
	}
	
	/**
	 * sets an Object value
	 * @param id the id 
	 * @param value the value
	 * @return the ToStringHelper instance
	 */
	public ToStringHelper set(String id, Object value) {
		if (this.addListSeparator) {
			this.buffer.append(FORMAT_LIST_SEPARATOR);
		}
		this.buffer.append( id ).append( FORMAT_DESCRIPTION_SEPARATOR ).append(value);
		this.addListSeparator = true;
		return this;
	}
	
	/**
	 * sets a String value
	 * @param id the id 
	 * @param value the value
	 * @return the ToStringHelper instance
	 */
	public ToStringHelper set(String id, String value) {
		if (this.addListSeparator) {
			this.buffer.append(FORMAT_LIST_SEPARATOR);
		}
		this.buffer.append(id).append(FORMAT_DESCRIPTION_SEPARATOR).append('"').append(value).append('"');
		this.addListSeparator = true;
		return this;
	}
	
	/**
	 * sets an ArrayList
	 * @param id the id 
	 * @param value the value
	 * @return the ToStringHelper instance
	 */
	public ToStringHelper set(String id, ArrayList value) {
		if (this.addListSeparator) {
			this.buffer.append(FORMAT_LIST_SEPARATOR);
		}
		this.buffer.append(id).append(FORMAT_DESCRIPTION_SEPARATOR);
		int size = value.size();
		for (int i = 0; i < size; i++) {
			Object object = value.get(i);
			this.buffer.append( object );
			if(i != size  - 1) {
				this.buffer.append( FORMAT_DESCRIPTION_LIST_SEPARATOR );
			}
		}
		this.addListSeparator = true;
		return this;
	}
	
	/**
	 * sets a HashMap
	 * @param id the id 
	 * @param value the value
	 * @return the ToStringHelper instance
	 */
	public ToStringHelper set(String id, HashMap value) {
		if (this.addListSeparator) {
			this.buffer.append(FORMAT_LIST_SEPARATOR);
		}
		this.buffer.append( id ).append( FORMAT_DESCRIPTION_SEPARATOR );
		String description = id + FORMAT_DESCRIPTION_SEPARATOR;
		Object[] keys = value.keys();
		for (int i = 0; i < keys.length; i++) {
			Object key = keys[i];
			Object object = value.get(key);
			this.buffer.append(key).append(FORMAT_DESCRIPTION_KEYVALUE_SEPARATOR).append(object);
			if(i != keys.length - 1) {
				this.buffer.append( FORMAT_DESCRIPTION_LIST_SEPARATOR );
			}
		}
		this.addListSeparator = true;
		return this;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (!this.isNameInserted && this.name != null) {
			this.buffer.insert(0, this.name);
			this.isNameInserted = true;
		}
		this.buffer.append(FORMAT_FOOTER);
		String result = this.buffer.toString();
		// remove footer again for subsequent changes:
		this.buffer.delete(this.buffer.length() - FORMAT_FOOTER.length(), this.buffer.length());
		return result;
	}
}
