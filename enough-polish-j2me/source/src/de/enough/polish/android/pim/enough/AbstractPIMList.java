//#condition polish.android
package de.enough.polish.android.pim.enough;

import de.enough.polish.android.pim.PIM;
import de.enough.polish.android.pim.PIMItem;
import de.enough.polish.android.pim.PIMList;
import de.enough.polish.android.pim.UnsupportedFieldException;

/**
 * This is an abstract class to unify common functionality of a PIMList
 * @author rickyn
 *
 */
public abstract class AbstractPIMList implements PIMList {

	private FieldInfo[] fieldInfos;
	private final int mode;
	private final String name;
	
	public AbstractPIMList(String name, int mode) {
		this.name = name;
		if(mode != PIM.READ_ONLY && mode != PIM.WRITE_ONLY && mode != PIM.READ_WRITE) {
			throw new IllegalArgumentException("The mode '"+mode+"' is not supported.");
		}
		this.mode = mode;
	}

	public FieldInfo findFieldInfo(int fieldId) {
		return findFieldInfo(fieldId,true);
	}
	
	public String getAttributeLabel(int attribute) {
		switch(attribute) {
			case PIMItem.ATTR_NONE: return "None";
			default: throw new IllegalArgumentException("Attribute '"+attribute+"' is not valid.");
		}
	}

	public int getFieldDataType(int fieldId) {
		FieldInfo field = findFieldInfo(fieldId);
		return field.type;
	}

	public String getFieldLabel(int fieldId) {
		FieldInfo field = findFieldInfo(fieldId);
		return field.label;
	}

	public String getName() {
		return this.name;
	}

	public int[] getSupportedArrayElements(int stringArrayField) {
		FieldInfo fieldInfo = findFieldInfo(stringArrayField);
		return fieldInfo.supportedArrayElements;
	}
	
	public int[] getSupportedAttributes(int field) {
		FieldInfo fieldInfo = findFieldInfo(field);
		return fieldInfo.supportedAttributes;
	}
	
	public int[] getSupportedFields() {
		int[] supportedFields = new int[this.fieldInfos.length];
		for (int i = 0; i < this.fieldInfos.length; i++) {
			supportedFields[i] = this.fieldInfos[i].pimId;
		}
		return supportedFields;
	}
	
	public boolean isSupportedArrayElement(int stringArrayField, int arrayElement) {
		FieldInfo fieldInfo = findFieldInfo(stringArrayField);
		if(fieldInfo.type != PIMItem.STRING_ARRAY) {
			throw new IllegalArgumentException("The field with id '"+stringArrayField+"' is not of type 'PIMItem.STRING_ARRAY'.");
		}
		int numberOfValues = fieldInfo.supportedArrayElements.length;
		for(int i = 0; i < numberOfValues; i++) {
			if(arrayElement == fieldInfo.supportedArrayElements[i]) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isSupportedAttribute(int field, int attribute) {
		FieldInfo fieldInfo = findFieldInfo(field);
		int numberOfValues = fieldInfo.supportedAttributes.length;
		for(int i = 0; i < numberOfValues; i++) {
			if(attribute == fieldInfo.supportedAttributes[i]) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isSupportedField(int fieldId) {
		FieldInfo field = findFieldInfo(fieldId,false);
		if(field == null) {
			return false;
		}
		return true;
	}

	public int maxValues(int field) {
		return -1;
	}
	
	public int stringArraySize(int stringArrayField) {
		FieldInfo fieldInfo = findFieldInfo(stringArrayField);
		return fieldInfo.numberOfArrayElements;
	}

	/**
	 * This method throws a SecurityException if the list is not readable. This is the case if the list was opened in WRITE_ONLY mode.
	 */
	protected void ensureListReadable() {
		if(this.mode == PIM.WRITE_ONLY) {
			throw new SecurityException("The list is only writeable.");
		}
	}

	protected void ensureListWriteable() {
		if(this.mode == PIM.READ_ONLY) {
			throw new SecurityException("The list is only readable.");
		}
	}

	/**
	 * This method will set the fields this list can handle.
	 * @param fieldInfos
	 */
	protected void setFieldInfos(FieldInfo[] fieldInfos) {
		this.fieldInfos = fieldInfos;
	}

	/**
	 * 
	 * @param fieldId
	 * @param throwException
	 * @return May return null if no field with the given id is present and now exception should be thrown.
	 */
	private FieldInfo findFieldInfo(int fieldId, boolean throwException) {
		for (int i = 0; i < this.fieldInfos.length; i++) {
			FieldInfo fieldInfo = this.fieldInfos[i];
			if(fieldId == fieldInfo.pimId) {
				return fieldInfo;
			}
		}
		if(throwException) {
			throw new UnsupportedFieldException("The field with id '"+fieldId+"' is not supported.");
		}
		return null;
	}

}