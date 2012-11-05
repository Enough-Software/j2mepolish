//#condition polish.android
package de.enough.polish.android.pim.impl;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import de.enough.polish.android.midlet.MidletBridge;
import de.enough.polish.android.pim.Contact;
import de.enough.polish.android.pim.PIMItem;
import de.enough.polish.util.IntKeyIntValueMap;

/**
 * Data access layer
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ContactDaoImpl
implements ContactDao
{
	
	private Cursor	contactCursor;
	
	public ContactDaoImpl()
	{
	}
	
	private void initCursor()
	{
		ContentResolver contentResolver = MidletBridge.instance.getContentResolver();
		this.contactCursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.android.pim.impl.ContactDao#getContactsCount()
	 */
	public int getContactsCount()
	{
		if (this.contactCursor == null)
		{
			initCursor();
		}
		return this.contactCursor.getCount();
	}

	private static final FieldInfo UID_FIELD_INFO = new FieldInfo(Contact.UID, PIMItem.STRING, "Unique identifier", new int[] {Contact.ATTR_NONE}, 1);
	private static final FieldInfo FORMATTED_NAME_FIELD_INFO = new FieldInfo(Contact.FORMATTED_NAME, PIMItem.STRING, "Formatted Name", new int[] {Contact.ATTR_NONE}, 1);
	public ContactImpl loadContact(int index)
	{
		if (this.contactCursor == null)
		{
			initCursor();
		}
		boolean success = this.contactCursor.moveToPosition(index);
		if (!success)
		{
			return null;
		}
		ContentResolver contentResolver = MidletBridge.instance.getContentResolver();
		
		Cursor cur = this.contactCursor;
		ContactImpl contact = new ContactImpl();
		// UID:
		String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
		Field uidField = new Field(Contact.UID, PIMItem.STRING);
		uidField.addValue(id, PIMItem.ATTR_NONE);
		
		// formatted name:
		String formattedName = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		Field formattedNameField = new Field(Contact.FORMATTED_NAME, PIMItem.STRING);
		formattedNameField.addValue(formattedName, Contact.ATTR_NONE);
		contact.addField(formattedNameField);
		
		loadNames(contentResolver, contact, id);

		
		// registered phone numbers:
		int hasPhoneNumber = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
		if (hasPhoneNumber > 0) 
		{
			loadTelNumbers(contentResolver, contact, id);
		}
		
		loadEmails(contentResolver, contact, id);
		loadAddresses(contentResolver, contact, id);
		loadNotes(contentResolver, contact, id);
		
		return contact;
	}
	
	private static final FieldInfo NAME_FIELD_INFO = new FieldInfo(Contact.NAME, "Name", 5, Contact.NAME_GIVEN, new int[] {Contact.NAME_FAMILY, Contact.NAME_GIVEN, Contact.NAME_OTHER, Contact.NAME_PREFIX, Contact.NAME_SUFFIX},new int[] {Contact.ATTR_NONE}, 1);
	private void loadNames(ContentResolver contentResolver, ContactImpl contact, String id)
	{
    	Field namesField = new Field(Contact.NAME, PIMItem.STRING_ARRAY, new NameFieldPopulator(id, contentResolver));
    	contact.addField(namesField);
	}

	private static final FieldInfo TEL_FIELD_INFO = new FieldInfo(Contact.TEL, PIMItem.STRING, "Phone", new int[] {Contact.ATTR_MOBILE,Contact.ATTR_WORK, Contact.ATTR_PREFERRED, Contact.ATTR_SMS, Contact.ATTR_ASST, Contact.ATTR_HOME,Contact.ATTR_OTHER,Contact.ATTR_PAGER,Contact.ATTR_FAX,Contact.ATTR_NONE}, 21);
	private static final IntKeyIntValueMap TEL_ATTRIBUTES_MAPPPING = new IntKeyIntValueMap();
	static {
		TEL_ATTRIBUTES_MAPPPING.put(ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT, Contact.ATTR_ASST);
		TEL_ATTRIBUTES_MAPPPING.put(ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK, Contact.ATTR_OTHER);
		TEL_ATTRIBUTES_MAPPPING.put(ContactsContract.CommonDataKinds.Phone.TYPE_CAR, Contact.ATTR_OTHER);
		TEL_ATTRIBUTES_MAPPPING.put(ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN, Contact.ATTR_OTHER);
		TEL_ATTRIBUTES_MAPPPING.put(ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME, Contact.ATTR_FAX);
		TEL_ATTRIBUTES_MAPPPING.put(ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK, Contact.ATTR_FAX);
		TEL_ATTRIBUTES_MAPPPING.put(ContactsContract.CommonDataKinds.Phone.TYPE_HOME, Contact.ATTR_HOME);
		TEL_ATTRIBUTES_MAPPPING.put(ContactsContract.CommonDataKinds.Phone.TYPE_ISDN, Contact.ATTR_OTHER);
		TEL_ATTRIBUTES_MAPPPING.put(ContactsContract.CommonDataKinds.Phone.TYPE_MAIN, Contact.ATTR_PREFERRED);
		TEL_ATTRIBUTES_MAPPPING.put(ContactsContract.CommonDataKinds.Phone.TYPE_MMS, Contact.ATTR_SMS);
		TEL_ATTRIBUTES_MAPPPING.put(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE, Contact.ATTR_MOBILE);
		TEL_ATTRIBUTES_MAPPPING.put(ContactsContract.CommonDataKinds.Phone.TYPE_OTHER, Contact.ATTR_OTHER);
		TEL_ATTRIBUTES_MAPPPING.put(ContactsContract.CommonDataKinds.Phone.TYPE_OTHER_FAX, Contact.ATTR_FAX);
		TEL_ATTRIBUTES_MAPPPING.put(ContactsContract.CommonDataKinds.Phone.TYPE_PAGER, Contact.ATTR_PAGER);
		TEL_ATTRIBUTES_MAPPPING.put(ContactsContract.CommonDataKinds.Phone.TYPE_RADIO, Contact.ATTR_OTHER);
		TEL_ATTRIBUTES_MAPPPING.put(ContactsContract.CommonDataKinds.Phone.TYPE_TELEX, Contact.ATTR_OTHER);
		TEL_ATTRIBUTES_MAPPPING.put(ContactsContract.CommonDataKinds.Phone.TYPE_TTY_TDD, Contact.ATTR_OTHER);
		TEL_ATTRIBUTES_MAPPPING.put(ContactsContract.CommonDataKinds.Phone.TYPE_WORK, Contact.ATTR_WORK);
		TEL_ATTRIBUTES_MAPPPING.put(ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE, Contact.ATTR_WORK);
		TEL_ATTRIBUTES_MAPPPING.put(ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER, Contact.ATTR_WORK);
		TEL_ATTRIBUTES_MAPPPING.put(ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM, Contact.ATTR_OTHER);
	}
	private void loadTelNumbers(ContentResolver contentResolver, ContactImpl contact, String id)
	{
		Field.Populator populator = new TelFieldPopulator(id, contentResolver);
		Field telField = new Field(Contact.TEL, PIMItem.STRING, populator);
		contact.addField(telField);
	}
	
	private static final FieldInfo EMAIL_FIELD_INFO = new FieldInfo(Contact.EMAIL, PIMItem.STRING, "Email", new int[] {Contact.ATTR_HOME, Contact.ATTR_MOBILE, Contact.ATTR_WORK, Contact.ATTR_OTHER, Contact.ATTR_NONE}, 5);
	private static final IntKeyIntValueMap EMAIL_ATTRIBUTES_MAPPPING = new IntKeyIntValueMap();
	static {
		EMAIL_ATTRIBUTES_MAPPPING.put(ContactsContract.CommonDataKinds.Email.TYPE_HOME, Contact.ATTR_HOME);
		EMAIL_ATTRIBUTES_MAPPPING.put(ContactsContract.CommonDataKinds.Email.TYPE_MOBILE, Contact.ATTR_MOBILE);
		EMAIL_ATTRIBUTES_MAPPPING.put(ContactsContract.CommonDataKinds.Email.TYPE_OTHER, Contact.ATTR_OTHER);
		EMAIL_ATTRIBUTES_MAPPPING.put(ContactsContract.CommonDataKinds.Email.TYPE_WORK, Contact.ATTR_WORK);
		EMAIL_ATTRIBUTES_MAPPPING.put(ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM, Contact.ATTR_OTHER);
	}
	private void loadEmails(ContentResolver contentResolver, ContactImpl contact, String id)
	{
		Field.Populator populator = new EmailFieldPopulator(id, contentResolver);
		Field emailField = new Field(Contact.EMAIL, PIMItem.STRING, populator);
		contact.addField(emailField);
	}
	
	private static final FieldInfo ADDR_FIELD_INFO = new FieldInfo(Contact.ADDR, "Address", 7, Contact.ADDR_EXTRA, new int[] {Contact.ADDR_EXTRA}, new int[] {Contact.ATTR_HOME, Contact.ATTR_WORK, Contact.ATTR_OTHER, Contact.ATTR_MOBILE, Contact.ATTR_NONE}, 4);
	private static final FieldInfo FORMATTED_ADDR_FIELD_INFO = new FieldInfo(Contact.FORMATTED_ADDR, PIMItem.STRING, "Formatted Address", new int[] {Contact.ATTR_NONE, Contact.ATTR_HOME, Contact.ATTR_WORK, Contact.ATTR_OTHER}, 4);
	private static final IntKeyIntValueMap ADDR_ATTRIBUTES_MAPPPING = new IntKeyIntValueMap();
	static {
		ADDR_ATTRIBUTES_MAPPPING.put(ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME, Contact.ATTR_HOME);
		ADDR_ATTRIBUTES_MAPPPING.put(ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER, Contact.ATTR_OTHER);
		ADDR_ATTRIBUTES_MAPPPING.put(ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK, Contact.ATTR_WORK);
		ADDR_ATTRIBUTES_MAPPPING.put(ContactsContract.CommonDataKinds.StructuredPostal.TYPE_CUSTOM, Contact.ATTR_OTHER);
	}
	private void loadAddresses(ContentResolver contentResolver, ContactImpl contact, String id)
	{
		Field.Populator populator = new AddressFieldPopulator(id, contentResolver);
	    Field formattedAddressField = new Field(Contact.FORMATTED_ADDR, PIMItem.STRING, populator);
    	Field addressField = new Field(Contact.ADDR, PIMItem.STRING_ARRAY, populator);
    	contact.addField(addressField);
	    contact.addField(formattedAddressField);
	}	


	private static final FieldInfo NOTE_FIELD_INFO = new FieldInfo(Contact.NOTE,PIMItem.STRING,"Note",new int[] {Contact.ATTR_NONE}, 1);
	private void loadNotes(ContentResolver contentResolver, ContactImpl contact, String id)
	{
		Field.Populator populator = new NoteFieldPopulator(id, contentResolver);
	    Field noteField = new Field(Contact.NOTE, PIMItem.STRING, populator);
	    contact.addField(noteField);
	}
	
	
	private static FieldInfo[] supportedFields = new FieldInfo[] {UID_FIELD_INFO, FORMATTED_NAME_FIELD_INFO, NAME_FIELD_INFO, TEL_FIELD_INFO, EMAIL_FIELD_INFO, ADDR_FIELD_INFO, FORMATTED_ADDR_FIELD_INFO, NOTE_FIELD_INFO};
	public FieldInfo[] getSupportedFields()
	{
		return supportedFields;
	}


	public void close()
	{
		if (this.contactCursor != null)
		{
			this.contactCursor.close();
			this.contactCursor = null;
		}
	}
	
	private static class NameFieldPopulator 
	implements Field.Populator
	{
		
		private final String id;
		private final ContentResolver	contentResolver;
		
		public NameFieldPopulator(String id, ContentResolver contentResolver)
		{
			this.id = id;
			this.contentResolver = contentResolver;
		}

		public void populateField(Field field)
		{
		    String[] projection = new String[] {CommonDataKinds.StructuredName.FAMILY_NAME, CommonDataKinds.StructuredName.GIVEN_NAME, CommonDataKinds.StructuredName.MIDDLE_NAME, CommonDataKinds.StructuredName.PREFIX, CommonDataKinds.StructuredName.SUFFIX};
		    String where = ContactsContract.Data.RAW_CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
		    String[] whereParameters = new String[]{this.id, CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};

		    Cursor cursor = this.contentResolver.query(ContactsContract.Data.CONTENT_URI, projection, where, whereParameters, null);

		    String[] names = new String[5];
		    if (cursor.moveToFirst()) 
		    { 
		    	for (int columnIndex=0; columnIndex<projection.length; columnIndex++)
		    	{
		    		String namePart = cursor.getString(columnIndex);
		    		if (namePart == null) {
		    			namePart = "";
		    		}
					names[columnIndex] = (namePart);
		    	}
		    } 
	    	field.addValue(names, Contact.ATTR_NONE );
		    cursor.close();
		}
		
	}
	
	private static class AddressFieldPopulator
	implements Field.Populator
	{
		
		private final String id;
		private final ContentResolver	contentResolver;
		
		public AddressFieldPopulator(String id, ContentResolver contentResolver)
		{
			this.id = id;
			this.contentResolver = contentResolver;
		}

		public void populateField(Field field)
		{
			
		    String[] projection = new String[] {CommonDataKinds.StructuredPostal.POBOX, CommonDataKinds.StructuredPostal.TYPE, CommonDataKinds.StructuredPostal.STREET, CommonDataKinds.StructuredPostal.CITY, CommonDataKinds.StructuredPostal.REGION, CommonDataKinds.StructuredPostal.POSTCODE, CommonDataKinds.StructuredPostal.COUNTRY, CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS };
		    String where = ContactsContract.Data.RAW_CONTACT_ID + " = ? AND " + CommonDataKinds.StructuredPostal.MIMETYPE + " = ?"; 
		    String[] whereParameters = new String[]{this.id, CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};

		    Cursor cursor = this.contentResolver.query(ContactsContract.Data.CONTENT_URI, projection, where, whereParameters, null);
		    boolean arrayFieldAdded = false;
	    	String[] addresses = new String[projection.length-1];
	    	if (field.getId() == Contact.ADDR) 
	    	{
	    		field.addValue(addresses, Contact.ATTR_NONE);
	    	}
		    while (cursor.moveToNext()) 
		    { 
		    	if (!arrayFieldAdded)
		    	{
			    	// there can be more than one address on Android, however the PIM API only supports one address. So we just take the first one.	
			    	for (int columnIndex=0; columnIndex<projection.length-1; columnIndex++)
			    	{
			    		String addressPart = cursor.getString(columnIndex);
			    		if (columnIndex == 1)
			    		{
			    			addressPart = null;
			    		}
			    		if (addressPart == null) {
			    			addressPart = "";
			    		} 
			    		else 
			    		{
			    			arrayFieldAdded = true;
			    		}
			    		addresses[columnIndex] = addressPart;
			    	}
		    	}
		    	String address = cursor.getString(projection.length - 1);
		    	if (address == null)
		    	{
		    		continue;
		    	}
		    	if (field.getId() == Contact.FORMATTED_ADDR) {
			    	int type = cursor.getInt(1);
			    	int pimAttribute = ADDR_ATTRIBUTES_MAPPPING.get(type);
		    		field.addValue(address, pimAttribute);
		    	}
		    }
		    cursor.close();
		}
		
	}
	
	private static class TelFieldPopulator 
	implements Field.Populator
	{
		
		private final String id;
		private final ContentResolver	contentResolver;
		
		public TelFieldPopulator(String id, ContentResolver contentResolver)
		{
			this.id = id;
			this.contentResolver = contentResolver;
		}

		public void populateField(Field field)
		{
			Cursor cursor = this.contentResolver.query( CommonDataKinds.Phone.CONTENT_URI,
					null,
					CommonDataKinds.Phone.CONTACT_ID +" = ?",
					new String[]{this.id}, null);
			
					
			while (cursor.moveToNext()) 
			{
				String phoneNumber = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER));
				int phoneNumberType = cursor.getInt(cursor.getColumnIndex(CommonDataKinds.Phone.TYPE));
				int pimAttribute = TEL_ATTRIBUTES_MAPPPING.get(phoneNumberType);
				if (pimAttribute == Integer.MIN_VALUE)
				{
					pimAttribute = Contact.ATTR_NONE;
				}
				field.addValue(phoneNumber, pimAttribute);
			}
			cursor.close();
		}
		
	}
	
	private static class EmailFieldPopulator 
	implements Field.Populator
	{
		
		private final String id;
		private final ContentResolver	contentResolver;
		
		public EmailFieldPopulator(String id, ContentResolver contentResolver)
		{
			this.id = id;
			this.contentResolver = contentResolver;
		}

		public void populateField(Field field)
		{
			Cursor cursor = this.contentResolver.query( CommonDataKinds.Email.CONTENT_URI,
					new String[]{ CommonDataKinds.Email.DATA, CommonDataKinds.Email.TYPE},
					CommonDataKinds.Phone.CONTACT_ID +" = ?",
					new String[]{this.id}, null);
			
					
			while (cursor.moveToNext()) 
			{
				String emailAddress = cursor.getString(0); //pCur.getColumnIndex(CommonDataKinds.Email.DATA));
				int emailType = cursor.getInt(1); //pCur.getColumnIndex(CommonDataKinds.Email.TYPE));
				int pimAttribute = EMAIL_ATTRIBUTES_MAPPPING.get(emailType);
				if (pimAttribute == Integer.MIN_VALUE)
				{
					pimAttribute = Contact.ATTR_NONE;
				}
				field.addValue(emailAddress, pimAttribute);
			}
			cursor.close();
		}
		
	}
	
	private static class NoteFieldPopulator 
	implements Field.Populator
	{
		
		private final String id;
		private final ContentResolver	contentResolver;
		
		public NoteFieldPopulator(String id, ContentResolver contentResolver)
		{
			this.id = id;
			this.contentResolver = contentResolver;
		}

		public void populateField(Field field)
		{
		    String[] projection = new String[] {CommonDataKinds.Note.NOTE};
		    String where = ContactsContract.Data.RAW_CONTACT_ID + " = ? AND " + CommonDataKinds.Note.MIMETYPE + " = ?"; 
		    String[] whereParameters = new String[]{this.id, CommonDataKinds.Note.CONTENT_ITEM_TYPE};

		    Cursor cursor = this.contentResolver.query(ContactsContract.Data.CONTENT_URI, projection, where, whereParameters, null);
		    
		    if (cursor.moveToFirst()) 
		    { 
		    	String noteStr = cursor.getString(0);
		    	field.addValue(noteStr, PIMItem.ATTR_NONE);
		    } 
		    cursor.close();
		}
		
	}
	
	

}
