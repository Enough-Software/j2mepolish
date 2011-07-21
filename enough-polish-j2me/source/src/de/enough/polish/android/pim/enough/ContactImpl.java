//#condition polish.android
package de.enough.polish.android.pim.enough;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import de.enough.polish.android.pim.Contact;
import de.enough.polish.android.pim.PIMException;
import de.enough.polish.android.pim.PIMItem;
import de.enough.polish.android.pim.PIMList;
import de.enough.polish.android.pim.UnsupportedFieldException;

// TODO: Extract abstract base class.
public class ContactImpl implements Contact {

	private final ContactListImpl contactList;
	// We use an ArrayList object to save the space a HashMap would waste. There are only a few fields so searching linearly them is less memory intensive than a lookup.
	private final ArrayList<Field> fields;
	private boolean isModified;
	private final long id;
	private boolean addrLoaded = false;
	private boolean telLoaded = false;
	
	ContactImpl(long id, ContactListImpl contactList) {
		this.id = id;
		this.contactList = contactList;
		this.fields = new ArrayList<Field>();
	}
	
	ContactImpl(ContactListImpl contactList) {
		this(-1,contactList);
	}
	
	public void addBinary(int fieldId, int attributes, byte[] value, int offset, int length) {
		if(value == null) {
			throw new NullPointerException("The parameter value is null.");
		}
		if(length < 1) {
			throw new IllegalArgumentException("The parameter 'length' violates contraint 'length > 0'");
		}
		if(length > value.length) {
			throw new IllegalArgumentException("The parameter 'length' violates contraint 'length <= value.length'");
		}
		if(offset < 0) {
			throw new IllegalArgumentException("The parameter 'offset' violates contraint 'offset > 0'");
		}
		if(offset >= value.length) {
			throw new IllegalArgumentException("The parameter 'offset' violates contraint 'offset < value.length'");
		}
		Field field = getField(fieldId, PIMItem.BINARY);
		byte[] result = new byte[length];
		System.arraycopy(value, offset, result, 0, length);
		field.values.add(result);
		field.attributes.add(new Integer(attributes));
		this.isModified = true;
	}

	public void addBoolean(int fieldId, int attributes, boolean value) {
		Field field = getField(fieldId, PIMItem.BOOLEAN);
		field.values.add(new Boolean(value));
		field.attributes.add(new Integer(attributes));
		this.isModified = true;
	}
	
	public void addDate(int fieldId, int attributes, long value) {
		Field field = getField(fieldId, PIMItem.DATE);
		field.values.add(new Date(value));
		field.attributes.add(new Integer(attributes));
		this.isModified = true;
	}

	public void addInt(int fieldId, int attributes, int value) {
		Field field = getField(fieldId, PIMItem.INT);
		field.values.add(new Integer(value));
		field.attributes.add(new Integer(attributes));
		this.isModified = true;
	}

	public void addString(int fieldId, int attributes, String value) {
		if(value == null) {
			throw new NullPointerException("Parameter 'value' must not be null.");
		}
		Field field = getField(fieldId, PIMItem.STRING);
		field.values.add(value);
		field.attributes.add(new Integer(attributes));
		this.isModified = true;
	}

	public void addStringArray(int fieldId, int attributes, String[] value) {
		if(value == null) {
			throw new NullPointerException("Parameter 'value' must not be null.");
		}
		Field field = getField(fieldId, PIMItem.STRING_ARRAY);
		field.values.add(value);
		field.attributes.add(new Integer(attributes));
		this.isModified = true;
	}
	
	/**
	 * This method returns a field from the given fieldId. If the field does not exists, it is created.
	 * @param fieldId
	 * @param expectedType the type the field must have.
	 * @throws UnsupportedFieldException if no field with the given id is supported.
	 * @return
	 */
	private Field getField(int fieldId, int expectedType) {
		FieldInfo fieldInfo = this.contactList.findFieldInfo(fieldId);
		if(fieldInfo == null) {
			throw new UnsupportedFieldException("The field with id '"+fieldId+"' is not supported.");
		}
		if(fieldInfo.type != expectedType) {
			throw new IllegalArgumentException("The field with metadata '"+fieldInfo+"' is not of type '"+expectedType+"'.");
		}
		for (Iterator<Field> iterator = this.fields.iterator(); iterator.hasNext();) {
			Field field = iterator.next();
			if(field.fieldInfo.equals(fieldInfo)) {
				return field;
			}
		}
		Field field = new Field(fieldInfo);
		this.fields.add(field);
		return field;
	}
	
	/**
	 * This method returns a field from the given fieldId. If the field does not exists, it is created.
	 * @param fieldId
	 * @throws UnsupportedFieldException if no field with the given id is supported.
	 * @return an existing or a newly created field with the given fieldId.
	 * TODO: Code duplication with getField(int,int).
	 */
	private Field getField(int fieldId) {
		FieldInfo fieldInfo = this.contactList.findFieldInfo(fieldId);
		if(fieldInfo == null) {
			throw new UnsupportedFieldException("The field with id '"+fieldId+"' is not supported.");
		}
		for (Iterator<Field> iterator = this.fields.iterator(); iterator.hasNext();) {
			Field field = iterator.next();
			if(field.fieldInfo.equals(fieldInfo)) {
				return field;
			}
		}
		Field field = new Field(fieldInfo);
		this.fields.add(field);
		return field;
	}

	public void addToCategory(String category) throws PIMException {
		throw new UnsupportedOperationException();
	}

	public void commit() throws PIMException {
		if(!this.isModified) {
			return;
		}
		this.contactList.persist(this);
	}

	public int countValues(int fieldId) {
		if(fieldId == Contact.TEL && !this.telLoaded) {
			// Lazy load
			this.telLoaded = true;
			this.contactList.getContactDao().lazyLoadTelFields(this);
		}
		Field field = getField(fieldId);
		return field.values.size();
	}

	public int getAttributes(int fieldId, int index) {
		if(index < 0) {
			throw new IndexOutOfBoundsException("The index '"+index+"' must not be < 0.");
		}
		Field field = getField(fieldId);
		int lastValidIndex = field.attributes.size() - 1;
		if(index > lastValidIndex) {
			throw new IndexOutOfBoundsException("The index '"+index+"' is larger then the last valid index of '"+lastValidIndex+"'");
		}
		Integer attributes = field.attributes.get(index);
		return attributes.intValue();
	}

	public byte[] getBinary(int fieldId, int index) {
		Field field = getField(fieldId, PIMItem.BINARY);
		int numberOfValues = field.values.size();
		if(index >= numberOfValues) {
			throw new IndexOutOfBoundsException("The parameter 'index' violates constraint 'index < numberOfValues'.");
		}
		byte[] result = (byte[]) field.values.get(index);
		return result;
	}

	public boolean getBoolean(int fieldId, int index) {
		if(index < 0) {
			throw new IndexOutOfBoundsException("The parameter 'index' violates constraint 'index >= 0'");
		}
		Field field = getField(fieldId, PIMItem.BINARY);
		int numberOfValues = field.values.size();
		if(index >= numberOfValues) {
			throw new IndexOutOfBoundsException("The parameter 'index' violates constraint 'index < numberOfValues'.");
		}
		Boolean result = (Boolean) field.values.get(index);
		return result.booleanValue();
	}

	public String[] getCategories() {
		return new String[0];
	}

	public long getDate(int fieldId, int index) {
		Field field = getField(fieldId, PIMItem.DATE);
		if(index < 0) {
			throw new IndexOutOfBoundsException("The parameter 'index' violates constraint 'index >= 0'");
		}
		int numberOfValues = field.values.size();
		if(index >= numberOfValues) {
			throw new IndexOutOfBoundsException("The parameter 'index' violates constraint 'index < numberOfValues'.");
		}
		Date result = (Date) field.values.get(index);
		return result.getTime();
	}

	public int[] getFields() {
		int numberOfFields = this.fields.size();
		int[] fieldIds = new int[numberOfFields];
		int i = 0;
		for (Iterator<Field> iterator = this.fields.iterator(); iterator.hasNext();) {
			Field field = iterator.next();
			fieldIds[i] = field.fieldInfo.pimId;
			i++;
		}
		// restrict the array to usefull cells
		int[] returnFields = new int[i];
		while(--i>=0)
			returnFields[i]=fieldIds[i];
		return returnFields;
	}

	public int getInt(int fieldId, int index) {
		if(index < 0) {
			throw new IndexOutOfBoundsException("The parameter 'index' violates constraint 'index >= 0'");
		}
		Field field = getField(fieldId,PIMItem.INT);
		int numberOfValues = field.values.size();
		if(index >= numberOfValues) {
			throw new IndexOutOfBoundsException("The parameter 'index' violates constraint 'index < numberOfValues'.");
		}
		Integer result = (Integer) field.values.get(index);
		return result.intValue();
	}

	public PIMList getPIMList() {
		return this.contactList;
	}

	public int getPreferredIndex(int fieldId) {
		FieldInfo fieldInfo = this.contactList.findFieldInfo(fieldId);
		return fieldInfo.preferredIndex;
	}

	public String getString(int fieldId, int index) {
		if(index < 0) {
			throw new IndexOutOfBoundsException("The parameter 'index' violates constraint 'index >= 0'");
		}
		if(fieldId == Contact.TEL && !this.telLoaded) {
			// Lazy load
			this.telLoaded = true;
			this.contactList.getContactDao().lazyLoadTelFields(this);
		}
		Field field = getField(fieldId,PIMItem.STRING);
		int numberOfValues = field.values.size();
		if(index >= numberOfValues) {
			throw new IndexOutOfBoundsException("The parameter 'index' violates constraint 'index < numberOfValues'.");
		}
		String result = (String) field.values.get(index);
		return result;
	}

	public String[] getStringArray(int fieldId, int index) {
		if(index < 0) {
			throw new IndexOutOfBoundsException("The parameter 'index' violates contraint 'index >= 0'");
		}
		if(fieldId == Contact.ADDR && !this.addrLoaded) {
			// Lazy load
			this.addrLoaded = true;
			this.contactList.getContactDao().lazyLoadAddrFields(this);
		}
		Field field = getField(fieldId,PIMItem.STRING_ARRAY);
		if(index >= field.values.size()) {
			throw new IndexOutOfBoundsException("The parameter 'index' violates contraint 'index < numberOfValuesInField'");
		}
		return (String[])field.values.get(index);
	}

	public boolean isModified() {
		return this.isModified;
	}

	public int maxCategories() {
		return 0;
	}

	public void removeFromCategory(String category) {
		if(category == null) {
			throw new NullPointerException();
		}
		// Do nothing.
	}

	public void removeValue(int fieldId, int index) {
		if(index < 0) {
			throw new IndexOutOfBoundsException("Parameter 'index' violates constraint 'index >= 0'");
		}
		Field field = getField(fieldId);
		int numberOfValues = field.values.size();
		if(index >= numberOfValues) {
			throw new IndexOutOfBoundsException("Parameter 'index' violates constraint 'index < numberOfValues'");
		}
		field.values.remove(index);
		field.attributes.remove(index);
	}
	
	public void setBinary(int fieldId, int index, int attributes, byte[] value, int offset, int length) {
		if(index < 0) {
			throw new IndexOutOfBoundsException("Parameter 'index' violates constraint 'index >= 0'");
		}
		Field field = getField(fieldId,PIMItem.BINARY);
		int numberOfValues = field.values.size();
		if(index >= numberOfValues) {
			throw new IndexOutOfBoundsException("Parameter 'index' violates constraint 'index < numberOfValues'");
		}
		if(value == null) {
			throw new NullPointerException("Parameter 'value' must not be null.");
		}
		if(value.length == 0) {
			throw new IllegalArgumentException("Array value of parameter 'value' must not have zero elements.");
		}
		if(offset < 0) {
			throw new IllegalArgumentException("Parameter 'offset' must not have a negative value.");
		}
		if(offset >= numberOfValues) {
			throw new IllegalArgumentException("Parameter 'offset' must not be larger then number of values which is '"+numberOfValues+"'");
		}
		if(length <= 0) {
			throw new IllegalArgumentException("Parameter 'length' must not have a negative value.");
		}
		if(length > numberOfValues) {
			throw new IllegalArgumentException("Parameter 'length' must not have a value which exceeds the number of values in the field which is '"+numberOfValues+"'");
		}
		if(offset+length > numberOfValues) {
			throw new IllegalArgumentException("The sum of 'offset' and 'length' must not exceed the number of values whish is '"+numberOfValues+"'");
		}
		byte[] result = new byte[length];
		System.arraycopy(value, offset, result, 0, length);
		field.values.set(index, result);
		field.attributes.set(index,new Integer(attributes));
		this.isModified = true;
	}

	public void setBoolean(int fieldId, int index, int attributes, boolean value) {
		if(index < 0) {
			throw new IndexOutOfBoundsException("Parameter 'index' violates constraint 'index >= 0'");
		}
		Field field = getField(fieldId,PIMItem.BOOLEAN);
		int numberOfValues = field.values.size();
		if(index >= numberOfValues) {
			throw new IndexOutOfBoundsException("Parameter 'index' violates constraint 'index < numberOfValues'");
		}
		field.values.set(index, new Boolean(value));
		field.attributes.set(index,new Integer(attributes));
		this.isModified = true;
	}

	public void setDate(int fieldId, int index, int attributes, long value) {
		Field field = getField(fieldId,PIMItem.DATE);
		if(index < 0) {
			throw new IndexOutOfBoundsException("Parameter 'index' violates constraint 'index >= 0'");
		}
		int numberOfValues = field.values.size();
		if(index >= numberOfValues) {
			throw new IndexOutOfBoundsException("Parameter 'index' violates constraint 'index < numberOfValues'");
		}
		field.values.set(index, new Date(value));
		field.attributes.set(index,new Integer(attributes));
		this.isModified = true;
	}

	public void setInt(int fieldId, int index, int attributes, int value) {
		Field field = getField(fieldId,PIMItem.INT);
		if(index < 0) {
			throw new IndexOutOfBoundsException("Parameter 'index' violates constraint 'index >= 0'");
		}
		int numberOfValues = field.values.size();
		if(index >= numberOfValues) {
			throw new IndexOutOfBoundsException("Parameter 'index' violates constraint 'index < numberOfValues'");
		}
		field.values.set(index, new Integer(value));
		field.attributes.set(index,new Integer(attributes));
		this.isModified = true;
	}

	public void setString(int fieldId, int index, int attributes, String value) {
		Field field = getField(fieldId,PIMItem.STRING);
		if(index < 0) {
			throw new IndexOutOfBoundsException("Parameter 'index' violates constraint 'index >= 0'");
		}
		int numberOfValues = field.values.size();
		if(index >= numberOfValues) {
			throw new IndexOutOfBoundsException("Parameter 'index' violates constraint 'index < numberOfValues'");
		}
		field.values.set(index, value);
		field.attributes.set(index,new Integer(attributes));
		this.isModified = true;
	}

	public void setStringArray(int fieldId, int index, int attributes, String[] value) {
		Field field = getField(fieldId,PIMItem.STRING_ARRAY);
		if(index < 0) {
			throw new IndexOutOfBoundsException("Parameter 'index' violates constraint 'index >= 0'");
		}
		int numberOfValues = field.values.size();
		if(index >= numberOfValues) {
			throw new IndexOutOfBoundsException("Parameter 'index' violates constraint 'index < numberOfValues'");
		}
		field.values.set(index, value);
		field.attributes.set(index,new Integer(attributes));
		this.isModified = true;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Contact ("+this.id+")");
		buffer.append("\n");
		for (Iterator<Field> iterator = this.fields.iterator(); iterator.hasNext();) {
			Field field = iterator.next();
			buffer.append(field);
			buffer.append("\n");
		}
		buffer.append("\n");
		return buffer.toString();
	}
	
	void setModified(boolean modified) {
		this.isModified = modified;
	}

	/**
	 * Checks, if the fieldId corresponds with a given type.
	 * @param fieldId
	 * @param type
	 * @return
	 * @throws UnsupportedFieldException if the field itself is not available for this class
	 * @throws IllegalArgumentException if the type of the field is not valid
	 */
//	private void checkFieldType(FieldInfo fieldInfo, int type) {
//		if(fieldInfo.type != type) {
//			throw new IllegalArgumentException("The field with metadata '"+fieldInfo+"' is not of type '"+type+"'.");
//		}
//	}
	
	

	/**
	 * Finds the field with the given field metadata. Throws an exception if it can not be found.
	 * @param fieldInfo
	 * @return
	 */
//	private Field findField(FieldInfo fieldInfo) {
//		return findField(fieldInfo,true);
//	}
//	
//	private Field findField(FieldInfo fieldInfo, boolean throwException) {
//		for (Iterator<Field> iterator = this.fields.iterator(); iterator.hasNext();) {
//			Field field = iterator.next();
//			if(field.fieldInfo.equals(fieldInfo)) {
//				return field;
//			}
//		}
//		if(throwException) {
//			throw new IndexOutOfBoundsException("The field with metadata '"+fieldInfo+"' does not have any values.");
//		}
//		return null;
//	}

	/**
	 * 
	 * @param fieldId
	 * @return the FieldInfo object with the given fieldId. Returns never null.
	 * @throws UnsupportedFieldException if field is not supported by ContactList.
	 */
//	private FieldInfo findFieldInfo(int fieldId) {
//		FieldInfo fieldInfo = this.contactList.findFieldInfo(fieldId);
//		if(fieldInfo == null) {
//			throw new UnsupportedFieldException("The field with id '"+fieldId+"' is not supported.");
//		}
//		return fieldInfo;
//	}

//	private Field findOrCreateField(FieldInfo fieldInfo) {
//		Field field = findField(fieldInfo,false);
//		if(field == null) {
//			field = new Field(fieldInfo);
//			this.fields.add(field);
//		}
//		return field;
//	}

	boolean isNew() {
		return this.id == -1;
	}
	
	long getId() {
		return this.id;
	}
}
