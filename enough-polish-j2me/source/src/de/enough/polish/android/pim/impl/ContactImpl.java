//#condition polish.android
package de.enough.polish.android.pim.impl;

import de.enough.polish.android.pim.Contact;
import de.enough.polish.android.pim.PIMException;
/**
 * Implements a Contact for Android
 * 
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ContactImpl 
extends PimItemImpl 
implements Contact
{

	public ContactImpl()
	{
		this(null);
	}

	public ContactImpl(ContactListImpl parent)
	{
		super(parent);
	}
	

	public void commit() throws PIMException
	{
		ensureWritable();
		((ContactListImpl)this.pimList).commit(this);
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.Contact#getPreferredIndex(int)
	 */
	public int getPreferredIndex(int fieldId)
	{
		Field fieldObj = getField(fieldId);
		if (fieldObj == null)
		{
			return 0;
		}
		return fieldObj.getIndexForAttribute(ATTR_PREFERRED);
	}

	public void setContactList(ContactListImpl contactListImpl)
	{
		this.pimList = contactListImpl;
	}




//	public int getFieldType(int fieldId)
//	{
//		switch (fieldId)
//		{
//		case Contact.ADDR: return PIMItem.STRING_ARRAY;
//		case Contact.BIRTHDAY: return PIMItem.DATE;
//		case Contact.CLASS: return PIMItem.INT;
//		case Contact.EMAIL: return PIMItem.STRING;
//		case Contact.FORMATTED_ADDR: return PIMItem.STRING;
//		case Contact.FORMATTED_NAME: return PIMItem.STRING;
//		case Contact.NAME: return PIMItem.STRING_ARRAY;
//		case Contact.NICKNAME: return PIMItem.STRING;
//		case Contact.NOTE: return PIMItem.STRING;
//		case Contact.ORG: return PIMItem.STRING;
//		case Contact.PHOTO: return PIMItem.BINARY;
//		case Contact.PHOTO_URL: return PIMItem.STRING;
//		case Contact.PUBLIC_KEY: return PIMItem.BINARY;
//		case Contact.PUBLIC_KEY_STRING: return PIMItem.STRING;
//		case Contact.REVISION: return PIMItem.DATE;
//		case Contact.TEL: return PIMItem.STRING;
//		case Contact.TITLE: return PIMItem.STRING;
//		case Contact.UID: return PIMItem.STRING;
//		case Contact.URL: return PIMItem.STRING;
//		}
//		return -1;
//	}

}
