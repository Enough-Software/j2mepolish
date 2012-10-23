//#condition polish.android
package de.enough.polish.android.pim.impl;

import java.util.ArrayList;
import java.util.HashMap;

import android.database.Cursor;
import android.net.Uri;

import de.enough.polish.android.pim.PIMException;
import de.enough.polish.android.pim.PIMItem;
import de.enough.polish.android.pim.PIMList;
import de.enough.polish.android.pim.UnsupportedFieldException;

/**
 * Implements a PIM Item
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class PimItemImpl implements PIMItem
{
	
	protected PimListImpl pimList;
	private boolean isModified;
	private final ArrayList<Field> fieldsList;
	private final ArrayList<String> categoriesList;
	private int[] fieldPimIds;
	
	public PimItemImpl( PimListImpl pimList )
	{
		this.pimList = pimList;
		this.categoriesList = new ArrayList<String>(1);
		this.fieldsList = new ArrayList<Field>();
	}
	
	public void addField(Field field)
	{
		this.fieldsList.add(field);
	}

	int getFieldDataType(int fieldId)
	{
		return this.pimList.getFieldDataType(fieldId);
	}


	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMItem#getPIMList()
	 */
	public PIMList getPIMList()
	{
		return this.pimList;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMItem#isModified()
	 */
	public boolean isModified()
	{
		return this.isModified;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMItem#getFields()
	 */
	public int[] getFields()
	{
		int[] fieldIds = this.fieldPimIds;
		if (fieldIds == null) {
			fieldIds = new int[ this.fieldsList.size() ];
			for (int i = 0; i < fieldIds.length; i++)
			{
				fieldIds[i] = this.fieldsList.get(i).getId();
			}
			this.fieldPimIds = fieldIds;
		}
		return fieldIds;
	}
	
	Field getField(int fieldId)
	{
		for (int i = 0; i < this.fieldsList.size(); i++)
		{
			Field fieldObj = this.fieldsList.get(i);
			if (fieldId == fieldObj.getId())
			{
				return fieldObj;
			}
		}
		return null;
	}
	
	Field getFieldThrowException(int fieldId)
	{
		Field fieldObj = getField(fieldId);
		if (fieldObj == null)
		{
			throw new UnsupportedFieldException("for field " + fieldId);
		}
		return fieldObj;
	}
	
	Field getOrCreateField(int fieldId)
	{
		Field fieldObj = getField(fieldId);
		if (fieldObj == null)
		{
			fieldObj = new Field( fieldId, getFieldDataType(fieldId) );
			this.fieldsList.add(fieldObj);
			this.isModified = true;
		}
		return fieldObj;
	}
	
	void ensureWritable() throws PIMException
	{
		this.pimList.ensureListWriteable();
	}




	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMItem#getBinary(int, int)
	 */
	public byte[] getBinary(int fieldId, int index)
	{
		Field fieldObj = getFieldThrowException(fieldId);
		if (fieldObj.getType() != BINARY)
		{
			throw new IllegalArgumentException();
		}
		return (byte[]) fieldObj.getValue(index);
	}


	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMItem#addBinary(int, int, byte[], int, int)
	 */
	public void addBinary(int fieldId, int attributes, byte[] value, int offset, int length)
	{
		Field fieldObj = getOrCreateField(fieldId);
		Object storeValue;
		if (offset == 0 && length >= value.length)
		{
			storeValue = value;
		}
		else
		{
			int partialSize = Math.min( value.length - offset, length);
			byte[] partialArray = new byte[partialSize];
			System.arraycopy(value, offset, partialArray, 0, partialSize);
			storeValue = partialArray;
		}
		fieldObj.addValue(storeValue, attributes);
		this.isModified = true;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMItem#setBinary(int, int, int, byte[], int, int)
	 */
	public void setBinary(int fieldId, int index, int attributes, byte[] value, int offset, int length)
	{
		Field fieldObj = getOrCreateField(fieldId);
		Object storeValue;
		if (offset == 0 && length >= value.length)
		{
			storeValue = value;
		}
		else
		{
			int partialSize = Math.min( value.length - offset, length);
			byte[] partialArray = new byte[partialSize];
			System.arraycopy(value, offset, partialArray, 0, partialSize);
			storeValue = partialArray;
		}
		fieldObj.setValue(index, storeValue, attributes);
		this.isModified = true;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMItem#getDate(int, int)
	 */
	public long getDate(int fieldId, int index)
	{
		Field fieldObj = getFieldThrowException(fieldId);
		if (fieldObj.getType() != DATE)
		{
			throw new IllegalArgumentException();
		}
		Long value = (Long) fieldObj.getValue(index);
		if (value == null)
		{
			return 0;
		}
		return value.longValue();
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMItem#addDate(int, int, long)
	 */
	public void addDate(int fieldId, int attributes, long value)
	{
		Field fieldObj = getOrCreateField(fieldId);
		Object storeValue = new Long(value);
		fieldObj.addValue(storeValue, attributes);
		this.isModified = true;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMItem#setDate(int, int, int, long)
	 */
	public void setDate(int fieldId, int index, int attributes, long value)
	{
		Field fieldObj = getOrCreateField(fieldId);
		Object storeValue = new Long(value);
		fieldObj.setValue(index, storeValue, attributes);
		this.isModified = true;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMItem#getInt(int, int)
	 */
	public int getInt(int fieldId, int index)
	{
		Field fieldObj = getFieldThrowException(fieldId);
		if (fieldObj.getType() != INT)
		{
			throw new IllegalArgumentException();
		}
		Integer value = (Integer) fieldObj.getValue(index);
		if (value == null)
		{
			return 0;
		}
		return value.intValue();
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMItem#addInt(int, int, int)
	 */
	public void addInt(int fieldId, int attributes, int value)
	{
		Field fieldObj = getOrCreateField(fieldId);
		Object storeValue = new Integer(value);
		fieldObj.addValue(storeValue, attributes);
		this.isModified = true;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMItem#setInt(int, int, int, int)
	 */
	public void setInt(int fieldId, int index, int attributes, int value)
	{
		Field fieldObj = getOrCreateField(fieldId);
		Object storeValue = new Integer(value);
		fieldObj.setValue(index, storeValue, attributes);
		this.isModified = true;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMItem#getString(int, int)
	 */
	public String getString(int fieldId, int index)
	{
		Field fieldObj = getFieldThrowException(fieldId);
		if (fieldObj.getType() != STRING)
		{
			throw new IllegalArgumentException();
		}
		String value = (String) fieldObj.getValue(index);
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMItem#addString(int, int, java.lang.String)
	 */
	public void addString(int fieldId, int attributes, String value)
	{
		Field fieldObj = getOrCreateField(fieldId);
		fieldObj.addValue(value, attributes);
		this.isModified = true;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMItem#setString(int, int, int, java.lang.String)
	 */
	public void setString(int fieldId, int index, int attributes, String value)
	{
		Field fieldObj = getOrCreateField(fieldId);
		fieldObj.setValue(index, value, attributes);
		this.isModified = true;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMItem#getBoolean(int, int)
	 */
	public boolean getBoolean(int fieldId, int index)
	{
		Field fieldObj = getFieldThrowException(fieldId);
		if (fieldObj.getType() != BOOLEAN)
		{
			throw new IllegalArgumentException();
		}
		Boolean value = (Boolean) fieldObj.getValue(index);
		if (value == null)
		{
			return false;
		}
		return value.booleanValue();
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMItem#addBoolean(int, int, boolean)
	 */
	public void addBoolean(int fieldId, int attributes, boolean value)
	{
		Field fieldObj = getOrCreateField(fieldId);
		Object storeValue = value ? Boolean.TRUE : Boolean.FALSE;
		fieldObj.addValue(storeValue, attributes);
		this.isModified = true;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMItem#setBoolean(int, int, int, boolean)
	 */
	public void setBoolean(int fieldId, int index, int attributes, boolean value)
	{
		Field fieldObj = getOrCreateField(fieldId);
		Object storeValue = value ? Boolean.TRUE : Boolean.FALSE;
		fieldObj.setValue(index, storeValue, attributes);
		this.isModified = true;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMItem#getStringArray(int, int)
	 */
	public String[] getStringArray(int fieldId, int index)
	{
		Field fieldObj = getFieldThrowException(fieldId);
		if (fieldObj.getType() != STRING_ARRAY)
		{
			throw new IllegalArgumentException();
		}
		String[] value = (String[]) fieldObj.getValue(index);
		if (value == null)
		{
			return null;
		}
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMItem#addStringArray(int, int, java.lang.String[])
	 */
	public void addStringArray(int fieldId, int attributes, String[] value)
	{
		Field fieldObj = getOrCreateField(fieldId);
		fieldObj.addValue(value, attributes);
		this.isModified = true;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMItem#setStringArray(int, int, int, java.lang.String[])
	 */
	public void setStringArray(int fieldId, int index, int attributes, String[] value)
	{
		Field fieldObj = getOrCreateField(fieldId);
		fieldObj.setValue(index, value, attributes);
		this.isModified = true;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMItem#countValues(int)
	 */
	public int countValues(int fieldId)
	{
		Field fieldObj = getField(fieldId);
		if (fieldObj == null)
		{
			return 0;
		}
		return fieldObj.countValues();
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMItem#removeValue(int, int)
	 */
	public void removeValue(int fieldId, int index)
	{
		Field fieldObj = getField(fieldId);
		if (fieldObj == null)
		{
			return;
		}
		fieldObj.removeValue(index);
		this.isModified = true;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMItem#getAttributes(int, int)
	 */
	public int getAttributes(int fieldId, int index)
	{
		Field fieldObj = getField(fieldId);
		if (fieldObj == null)
		{
			return ATTR_NONE;
		}
		return fieldObj.getAttributes(index);
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMItem#addToCategory(java.lang.String)
	 */
	public void addToCategory(String category) throws PIMException
	{
		this.categoriesList.add(category);
		this.isModified = true;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMItem#removeFromCategory(java.lang.String)
	 */
	public void removeFromCategory(String category)
	{
		this.categoriesList.remove(category);
		this.isModified = true;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMItem#getCategories()
	 */
	public String[] getCategories()
	{
		return this.categoriesList.toArray(new String[this.categoriesList.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.PIMItem#maxCategories()
	 */
	public int maxCategories()
	{
		return -1;
	}

}
