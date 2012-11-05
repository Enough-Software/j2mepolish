//#condition polish.android
package de.enough.polish.android.pim.impl;

import java.util.ArrayList;

import de.enough.polish.android.pim.PIMItem;
import de.enough.polish.android.pim.impl.Field.Populator;


/**
 * Allows to access a PIM field
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class Field 
{
	private final int id;
	private final int type;
	
	private ArrayList<Object> values;
	private ArrayList<Integer> attributes;
	private Populator	populator;
	private boolean isPopulated;
	
	public static interface Populator 
	{
		void populateField( Field field );
	}
	
	public Field(int id, int type, ArrayList<Integer> attributes, ArrayList<Object> values)
	{
		this.id = id;
		this.type = type;
		this.attributes = attributes;
		this.values = values;
		this.isPopulated = true;
	}
	
	public Field(int fieldId, int type)
	{
		this.id = fieldId;
		this.type = type;
	}

	public Field(int fieldId, int type, Populator populator)
	{
		this.id = fieldId;
		this.type = type;
		this.populator = populator;
	}
	
	public void populate()
	{
		if (!this.isPopulated)
		{
			this.isPopulated = true;
			if (this.populator != null)
			{
				this.populator.populateField(this);
			}
		}
	}

	public int getId() 
	{
		return this.id;
	}
	
	public int getType()
	{
		return this.type;
	}
	
	public Object getValue(int index)
	{
		if (this.values == null || index >= this.values.size())
		{
			// this is actual a legal retrieval, compare PIMList.maxValues()
			return null;
		}
		return this.values.get(index);
	}
	
	public void setValues(ArrayList<Object> values)
	{
		this.values = values;
		this.isPopulated = true;
	}
	
	public int getAttributes(int index)
	{
		if (this.attributes == null)
		{
			return PIMItem.ATTR_NONE;
		}
		return this.attributes.get(index);
	}
	
	public void setAttributes( int index, int attributes )
	{
		this.attributes.set(index,  new Integer(attributes));
	}
	
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("id=").append(this.id)
		.append(", type=").append(this.type)
		.append(", attributes=").append(this.attributes)
		.append(", value=").append(this.values);
		return buffer.toString();
	}

	public void addValue(Object value, int valueAttributes)
	{
		if (this.values == null)
		{
			this.values = new ArrayList<Object>();
			this.attributes = new ArrayList<Integer>();
		}
		synchronized (this.values)
		{
			this.values.add(value);
			this.attributes.add( new Integer(valueAttributes));
			this.isPopulated = true;
		}
	}

	public void setValue(int index, Object value, int valueAttributes)
	{
		if (this.values == null)
		{
			throw new IndexOutOfBoundsException("for index " + index + " in a field with 0 elements. Use PIMItem.addXXX() instead.");
		}
		synchronized (this.values)
		{
			this.values.set(index, value);
			this.attributes.set(index, new Integer(valueAttributes));
			this.isPopulated = true;
		}
	}

	public int countValues()
	{
		if (this.values == null) 
		{
			return 0;
		}
		synchronized (this.values) 
		{
			return this.values.size();
		}
	}

	public void removeValue(int index)
	{
		if (this.values == null)
		{
			return;
		}
		synchronized (this.values) 
		{
			this.values.remove(index);
			this.attributes.remove(index);
		}
	}

	public int getIndexForAttribute(int attr)
	{
		if (this.values == null)
		{
			return 0;
		}
		synchronized (this.values)
		{
			for (int i=0; i<this.attributes.size(); i++)
			{
				int storedAttribute = this.attributes.get(i).intValue();
				if ((storedAttribute & attr) == attr)
				{
					return i;
				}
			}
		}
		return -1;
	}
	
//	public static int resolveType(int fieldId)
//	{
//		switch (fieldId)
//		{
//		case Event.ALARM: return PIMItem.INT;
//		case Event.CLASS: return PIMItem.INT;
////		case Event.: return PIMItem.;
//		}
//		return -1;
//	}

}