//#condition polish.android
/**
 * 
 */
package de.enough.polish.android.pim.impl;

import de.enough.polish.android.pim.PIMItem;


/**
 * This class describes a field in the JavaME PIM API.
 * @author rickyn
 *
 */
class FieldInfo 
{
	
	public static final int DEFAULT_PREFERRED_INDEX = 0;
	public static final int DEFAULT_NUMBER_OF_ARRAYELEMENTS = 0;
	private final int numberOfArrayElements;
	protected final int fieldId;
	private final int type;
	private final String label;
	private final int preferredIndex;
	private int maxValues;
	private final int[]	supportedAttributes;
	private final int[] supportedArrayElements;

	
	/**
	 * This constructor can be used for PIMItem.STRING_ARRAY fields
	 * @param pimId
	 * @param type
	 * @param label
	 * @param numberOfArrayElements
	 * @param preferredIndex
	 * @param supportedArrayElements
	 * @param supportedAttributes
	 */
	public FieldInfo (int pimId, String label, int numberOfArrayElements, int preferredIndex, int[] supportedArrayElements, int[] supportedAttributes, int maxValues ) 
	{
		this.fieldId = pimId;
		this.type = PIMItem.STRING_ARRAY;
		this.label = label;
		this.numberOfArrayElements = numberOfArrayElements;
		this.preferredIndex = preferredIndex;
		this.supportedArrayElements = supportedArrayElements;
		this.supportedAttributes = supportedAttributes;
		this.maxValues = maxValues;
	}
	
	/**
	 * This constructor can be used for non-array types.
	 * @param pimId
	 * @param pimType
	 * @param label
	 * @param supportedAttributes
	 */
	public FieldInfo (int pimId, int pimType, String label, int[] supportedAttributes, int maxValues) 
	{
		this.fieldId = pimId;
		this.type = pimType;
		this.label = label;
		this.supportedAttributes = supportedAttributes;
		this.numberOfArrayElements = DEFAULT_NUMBER_OF_ARRAYELEMENTS;
		this.preferredIndex = DEFAULT_PREFERRED_INDEX;
		this.supportedArrayElements = new int[0];
		this.maxValues = maxValues;
	}
	
	
	
	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof FieldInfo)) 
		{
			return false;
		}
		FieldInfo other = (FieldInfo) obj;
		if (this.fieldId != other.fieldId) {
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.fieldId;
		return result;
	}
	
	@Override
	public String toString() {
		return "FieldInfo:Id:"+this.fieldId+".Type:"+this.type+".Label:"+this.label+".ArrayElements:"+this.numberOfArrayElements+".";
	}

	public int getType()
	{
		return this.type;
	}

	public String getLabel()
	{
		return this.label;
	}

	public int[] getSupportedArrayElements()
	{
		return this.supportedArrayElements;
	}

	public int[] getSupportedAttributes()
	{
		return this.supportedAttributes;
	}

	public boolean isSupportedArrayElement(int arrayElement)
	{
		int numberOfValues = this.supportedArrayElements.length;
		for (int i = 0; i < numberOfValues; i++) {
			if (arrayElement == this.supportedArrayElements[i]) 
			{
				return true;
			}
		}
		return false;
	}

	public boolean isSupportedAttribute(int attribute)
	{
		int numberOfValues = this.supportedAttributes.length;
		for (int i = 0; i < numberOfValues; i++) 
		{
			if (attribute == this.supportedAttributes[i]) 
			{
				return true;
			}
		}
		return false;
	}

	public int getNumberOfArrayElements()
	{
		return this.numberOfArrayElements;
	}

	public int getMaxValues()
	{
		return this.maxValues;
	}



}