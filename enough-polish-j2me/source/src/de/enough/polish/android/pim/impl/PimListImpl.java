//#condition polish.android
package de.enough.polish.android.pim.impl;

import de.enough.polish.android.pim.PIM;
import de.enough.polish.android.pim.PIMException;
import de.enough.polish.android.pim.PIMItem;
import de.enough.polish.android.pim.PIMList;
import de.enough.polish.android.pim.UnsupportedFieldException;

/**
 * This is an abstract class to unify common functionality of a PIMList
 * 
 * @author rickyn, robert virkus
 */
public abstract class PimListImpl implements PIMList 
{

	private final FieldInfo[] fieldInfos;
	private final int mode;
	private final String name;
	
	public PimListImpl(String name, int mode, FieldInfo[] fieldInfos) {
		if (mode != PIM.READ_ONLY && mode != PIM.WRITE_ONLY && mode != PIM.READ_WRITE) {
			throw new IllegalArgumentException("The mode '"+mode+"' is not supported.");
		}
		this.name = name;
		if (fieldInfos == null)
		{
			throw new NullPointerException();
		}
		this.fieldInfos = fieldInfos;
		this.mode = mode;
	}
	
	public FieldInfo getFieldInfo(int fieldId) {
		return getFieldInfo(fieldId,true);
	}
	
	/**
	 * 
	 * @param fieldId
	 * @param throwException
	 * @return May return null if no field with the given id is present and now exception should be thrown.
	 */
	private FieldInfo getFieldInfo(int fieldId, boolean throwException) {
		for (int i = 0; i < this.fieldInfos.length; i++) 
		{
			FieldInfo fieldInfo = this.fieldInfos[i];
			if (fieldId == fieldInfo.fieldId) 
			{
				return fieldInfo;
			}
		}
		if (throwException) 
		{
			throw new UnsupportedFieldException("The field with id '"+fieldId+"' is not supported.");
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMList#getAttributeLabel(int)
	 */
	public String getAttributeLabel(int attribute) 
	{
		switch (attribute) {
			case PIMItem.ATTR_NONE: return "None";
			default: throw new IllegalArgumentException("Attribute '"+attribute+"' is not valid.");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMList#getFieldDataType(int)
	 */
	public int getFieldDataType(int fieldId) 
	{
		FieldInfo fieldInfo = getFieldInfo(fieldId);
		return fieldInfo.getType();
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMList#getFieldLabel(int)
	 */
	public String getFieldLabel(int fieldId) 
	{
		FieldInfo field = getFieldInfo(fieldId);
		return field.getLabel();
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMList#getName()
	 */
	public String getName() 
	{
		return this.name;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMList#getSupportedArrayElements(int)
	 */
	public int[] getSupportedArrayElements(int stringArrayField) 
	{
		FieldInfo fieldInfo = getFieldInfo(stringArrayField);
		return fieldInfo.getSupportedArrayElements();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMList#getSupportedAttributes(int)
	 */
	public int[] getSupportedAttributes(int field) 
	{
		FieldInfo fieldInfo = getFieldInfo(field);
		return fieldInfo.getSupportedAttributes();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMList#getSupportedFields()
	 */
	public int[] getSupportedFields() 
	{
		int[] supportedFields = new int[this.fieldInfos.length];
		for (int i = 0; i < this.fieldInfos.length; i++) {
			supportedFields[i] = this.fieldInfos[i].fieldId;
		}
		return supportedFields;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMList#isSupportedArrayElement(int, int)
	 */
	public boolean isSupportedArrayElement(int stringArrayField, int arrayElement) 
	{
		FieldInfo fieldInfo = getFieldInfo(stringArrayField);
		if(fieldInfo.getType() != PIMItem.STRING_ARRAY) {
			throw new IllegalArgumentException("The field with id '"+stringArrayField+"' is not of type 'PIMItem.STRING_ARRAY'.");
		}
		return fieldInfo.isSupportedArrayElement(arrayElement);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMList#isSupportedAttribute(int, int)
	 */
	public boolean isSupportedAttribute(int fieldId, int attribute) 
	{
		FieldInfo fieldInfo = getFieldInfo(fieldId);
		return fieldInfo.isSupportedAttribute(attribute);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMList#isSupportedField(int)
	 */
	public boolean isSupportedField(int fieldId) 
	{
		FieldInfo fieldInfo = getFieldInfo(fieldId, false);
		if (fieldInfo == null) 
		{
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMList#maxValues(int)
	 */
	public int maxValues(int fieldId) 
	{
		FieldInfo fieldInfo = getFieldInfo(fieldId, false);
		if (fieldInfo == null) 
		{
			return 0;
		}
		return fieldInfo.getMaxValues();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMList#stringArraySize(int)
	 */
	public int stringArraySize(int stringArrayFieldId) 
	{
		FieldInfo fieldInfo = getFieldInfo(stringArrayFieldId);
		return fieldInfo.getNumberOfArrayElements();
	}

	/**
	 * This method throws a SecurityException if the list is not readable. This is the case if the list was opened in WRITE_ONLY mode.
	 */
	protected void ensureListReadable() 
	{
		if(this.mode == PIM.WRITE_ONLY) 
		{
			throw new SecurityException("The list is only writeable.");
		}
	}

	protected void ensureListWriteable() throws PIMException 
	{
		if(this.mode == PIM.READ_ONLY) 
		{
			throw new PIMException("The list is only readable.");
		}
	}
}