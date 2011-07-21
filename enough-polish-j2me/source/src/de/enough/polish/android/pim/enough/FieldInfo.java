//#condition polish.android
/**
 * 
 */
package de.enough.polish.android.pim.enough;

import de.enough.polish.android.pim.PIMItem;


/**
 * This class describes a field in the JavaME PIM API.
 * @author rickyn
 *
 */
class FieldInfo {
	
	// TODO: Is -1 correct here?
	public static final int DEFAULT_PREFERRED_INDEX = -1;
	public static final int DEFAULT_NUMBER_OF_ARRAYELEMENTS = 0;
	protected final int numberOfArrayElements;
	protected final int pimId;
	protected final int type;
	protected final String label;
	protected final int preferredIndex;
	protected final int[] supportedArrayElements;
	protected final int[] supportedAttributes;
	
	/**
	 * This constructor can be used for arraytypes
	 * @param pimId
	 * @param type
	 * @param label
	 * @param numberOfArrayElements
	 * @param preferredIndex
	 * @param supportedArrayElements
	 * @param supportedAttributes
	 */
	public FieldInfo (int pimId, String label,int numberOfArrayElements, int preferredIndex,int[] supportedArrayElements, int[] supportedAttributes) {
		this.pimId = pimId;
		this.type = PIMItem.STRING_ARRAY;
		this.label = label;
		this.numberOfArrayElements = numberOfArrayElements;
		this.preferredIndex = preferredIndex;
		this.supportedArrayElements = supportedArrayElements;
		this.supportedAttributes = supportedAttributes;
	}
	
	/**
	 * This constructor can be used for non-array types.
	 * @param pimId
	 * @param type
	 * @param label
	 * @param supportedAttributes
	 */
	public FieldInfo (int pimId, int type, String label,int[] supportedAttributes) {
		this.pimId = pimId;
		this.type = type;
		this.label = label;
		this.supportedAttributes = supportedAttributes;
		this.numberOfArrayElements = DEFAULT_NUMBER_OF_ARRAYELEMENTS;
		this.preferredIndex = DEFAULT_PREFERRED_INDEX;
		this.supportedArrayElements = new int[0];
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		FieldInfo other = (FieldInfo) obj;
		if (this.pimId != other.pimId) {
			return false;
		}
		return true;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.pimId;
		return result;
	}
	@Override
	public String toString() {
		return "FieldInfo:Id:"+this.pimId+".Type:"+this.type+".Label:"+this.label+".ArrayElements:"+this.numberOfArrayElements+".";
	}
}