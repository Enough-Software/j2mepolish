//#condition polish.android
package de.enough.polish.android.pim.impl;

import java.util.Enumeration;

import de.enough.polish.android.pim.Contact;
import de.enough.polish.android.pim.ContactList;
import de.enough.polish.android.pim.PIMException;
import de.enough.polish.android.pim.PIMItem;

/**
 * Implements a contact list
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ContactListImpl 
extends PimListImpl 
implements ContactList
{
	

	
	private final ContactDao	dao;

	public ContactListImpl(String name, int mode, ContactDao dao)
	{
		super(name, mode, dao.getSupportedFields() );
		this.dao = dao;
	}

	public void close() throws PIMException
	{
		this.dao.close();
	}

	public Enumeration items() throws PIMException
	{
		return new ContactEnumeration(this, this.dao);
	}

	public Enumeration items(PIMItem matchingItem) throws PIMException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Enumeration items(String matchingValue) throws PIMException
	{
		if (matchingValue.equals(""))
		{
			return items();
		}
		// TODO Auto-generated method stub
		return null;
	}

	public Enumeration itemsByCategory(String category) throws PIMException
	{
		throw new PIMException("categories are not supported");
	}

	public String[] getCategories() throws PIMException
	{
		throw new PIMException("categories are not supported");
	}

	public boolean isCategory(String category) throws PIMException
	{
		throw new PIMException("categories are not supported");
	}

	public void addCategory(String category) throws PIMException
	{
		throw new PIMException("categories are not supported");
	}

	public void deleteCategory(String category, boolean deleteUnassignedItems)
			throws PIMException
	{
		throw new PIMException("categories are not supported");
	}

	public void renameCategory(String currentCategory, String newCategory)
			throws PIMException
	{
		throw new PIMException("categories are not supported");
	}

	public int maxCategories()
	{
		return 0;
	}

	public String getArrayElementLabel(int stringArrayField, int arrayElement)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Contact createContact()
	{
		return new ContactImpl(this);
	}

	public Contact importContact(Contact contact)
	{
		throw new UnsupportedOperationException("not implemented");
	}

	public void removeContact(Contact contact) throws PIMException
	{
		throw new PIMException("not implemented");
		// TODO Auto-generated method stub
		
	}

	public void commit(ContactImpl contactImpl) throws PIMException
	{
		throw new PIMException("not implemented");
		//TODO implement commit()
	}



	

}
