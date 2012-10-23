//#condition polish.android
package de.enough.polish.android.pim.impl;

/**
 * Data access layer
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ContactDaoFactory
{
	private static ContactDao contactDaoImpl;
	
	public static ContactDao getContactDaoInstance()
	{
		if (contactDaoImpl == null)
		{
			contactDaoImpl = new ContactDaoImpl();
		}
		return contactDaoImpl;
	}

}
