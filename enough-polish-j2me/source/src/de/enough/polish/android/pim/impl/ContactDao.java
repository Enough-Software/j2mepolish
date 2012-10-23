//#condition polish.android
package de.enough.polish.android.pim.impl;


/**
 * Data access layer
 * @author Robert Virkus, j2mepolish@enough.de
 */
public interface ContactDao
{
	
	FieldInfo[] getSupportedFields();
	
	int getContactsCount();
	
	ContactImpl loadContact(int index);

	void close();
}
