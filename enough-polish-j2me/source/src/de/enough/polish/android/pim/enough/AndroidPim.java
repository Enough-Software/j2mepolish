//#condition polish.android
package de.enough.polish.android.pim.enough;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import de.enough.polish.android.pim.Contact;
import de.enough.polish.android.pim.PIM;
import de.enough.polish.android.pim.PIMException;
import de.enough.polish.android.pim.PIMItem;
import de.enough.polish.android.pim.PIMList;

public class AndroidPim extends PIM {

	public static final String DEFAULT_PIMLIST_NAME_CONTACTS = "contacts";
	public static final String SERIALIZATION_ENCODING_VCARD_21 = "VCARD/2.1";
	public static final String SERIALIZATION_ENCODING_VCARD_30 = "VCARD/3.0";
	
	private static ContactListImpl contactListInstance;
	private ContactDao contactDao;

	@Override
	public PIMItem[] fromSerialFormat(InputStream is, String enc) throws PIMException, UnsupportedEncodingException {
		throw new UnsupportedEncodingException("At the moment no encoding is supported.");
	}

	@Override
	public String[] listPIMLists(int pimListType) {
		switch(pimListType) {
			case PIM.CONTACT_LIST:
				return new String[] {DEFAULT_PIMLIST_NAME_CONTACTS};
			default:
				return new String[0];
		}
	}

	@Override
	public PIMList openPIMList(int pimListType, int mode) throws PIMException {
		switch(pimListType) {
		case PIM.CONTACT_LIST:
			return openPIMList(pimListType, mode,DEFAULT_PIMLIST_NAME_CONTACTS);
		default:
			throw new PIMException("The pimListType '"+pimListType+"' is not supported.");
		}
		
	}

	@Override
	public PIMList openPIMList(int pimListType, int mode, String name) throws PIMException {
		switch(pimListType) {
		case PIM.CONTACT_LIST:
			if( ! DEFAULT_PIMLIST_NAME_CONTACTS.equals(name)) {
				throw new PIMException("A PIMList with name '"+name+"' and type '"+pimListType+"' does not exist.");
			}
			if(contactListInstance == null) {
				if(contactDao == null) {
					contactDao = new ContentResolverContactDao();
				}
				contactListInstance = new ContactListImpl(name,mode,contactDao);
			}
			return contactListInstance;
		default:
			throw new PIMException("The pimListType '"+pimListType+"' is not supported.");
		}
	}

	public void setContactDao(ContactDao contactDao2) {
		contactDao = contactDao2;
	}
	
	@Override
	public String[] supportedSerialFormats(int pimListType) {
		if(PIM.CONTACT_LIST == pimListType) {
			return new String[] {SERIALIZATION_ENCODING_VCARD_21,SERIALIZATION_ENCODING_VCARD_30};
		}
		if(PIM.EVENT_LIST == pimListType) {
			return new String[0];
		}
		if(PIM.TODO_LIST == pimListType) {
			return new String[0];
		}
		throw new IllegalArgumentException("The PIMList type '"+pimListType+"' is not valid.");
	}

	/**
	 * @param dataFormat Must be either {@link #SERIALIZATION_ENCODING_VCARD_21} or {@link #SERIALIZATION_ENCODING_VCARD_30}
	 */
	@Override
	public void toSerialFormat(PIMItem item, OutputStream os, String enc, String dataFormat) throws PIMException, UnsupportedEncodingException {

		if( ! (item instanceof Contact)) {
			throw new PIMException("The PIMItem type is not supported.");
		}

		if(SERIALIZATION_ENCODING_VCARD_21.equals(dataFormat)) {
			try {
				VCardWriter.writeVCard2_1(item, os,enc);
			} catch (IOException e) {
				throw new PIMException(e.getMessage());
			}
		}
		else if (SERIALIZATION_ENCODING_VCARD_30.equals(dataFormat)){
			VCardWriter.writeVCard3_0(item, os,enc);
		} else {
			throw new IllegalArgumentException("The dataformat '"+dataFormat+"' is not known.");
		}
		
	}
//	@Override
//	public void toSerialFormat(PIMItem item, OutputStream os, String enc, String dataFormat) throws PIMException, UnsupportedEncodingException {
//		ContactStruct contactStruct = new ContactStruct();
//		Contact contact = (Contact)item;
//		ContactList contactList = (ContactList)contact.getPIMList();
//		
//		contactStruct.name = "";
//		if(contactList.isSupportedField(Contact.NAME)){
//			int numberOfValues = contact.countValues(Contact.NAME);
//			if(numberOfValues > 0) {
//				String[] nameComponents = contact.getStringArray(Contact.NAME, 0);
//				StringBuffer nameBuffer = new StringBuffer();
//				// Family name.
//				if(contactList.isSupportedArrayElement(Contact.NAME, Contact.NAME_FAMILY)) {
//					String familyName = nameComponents[Contact.NAME_FAMILY];
//					if(familyName != null) {
//						nameBuffer.append(familyName);
//					}
//				}
//				nameBuffer.append(";");
//				// Given name.
//				if(contactList.isSupportedArrayElement(Contact.NAME, Contact.NAME_GIVEN)) {
//					String givenName = nameComponents[Contact.NAME_GIVEN];
//					if(givenName != null) {
//						nameBuffer.append(givenName);
//					}
//				}
//				nameBuffer.append(";");
//				// Other name.
//				if(contactList.isSupportedArrayElement(Contact.NAME, Contact.NAME_OTHER)) {
//					String otherName = nameComponents[Contact.NAME_OTHER];
//					if(otherName != null) {
//						nameBuffer.append(otherName);
//					}
//				}
//				nameBuffer.append(";");
//				// Prefix name.
//				if(contactList.isSupportedArrayElement(Contact.NAME, Contact.NAME_PREFIX)) {
//					String prefixName = nameComponents[Contact.NAME_PREFIX];
//					if(prefixName != null) {
//						nameBuffer.append(prefixName);
//					}
//				}
//				nameBuffer.append(";");
//				// Sufix name.
//				if(contactList.isSupportedArrayElement(Contact.NAME, Contact.NAME_SUFFIX)) {
//					String suffixName = nameComponents[Contact.NAME_SUFFIX];
//					if(suffixName != null) {
//						nameBuffer.append(suffixName);
//					}
//				}
//				contactStruct.name = nameBuffer.toString();
//			}
//		}
//		
//		if(contactList.isSupportedField(Contact.FORMATTED_NAME)){
//			int numberOfValues = contact.countValues(Contact.FORMATTED_NAME);
//			if(numberOfValues > 0) {
//				String formattedName = contact.getString(Contact.FORMATTED_NAME,0);
//				contactStruct.phoneticName = formattedName;
//			}
//		} else {
//			contactStruct.phoneticName = "";
//		}
//		
//		VCardComposer composer = new VCardComposer();
//		String vCard;
//		try {
//			vCard = composer.createVCard(contactStruct, VCardComposer.VERSION_VCARD21_INT);
//		} catch (VCardException e) {
//			throw new PIMException(e.getMessage());
//		}
//		try {
//			os.write(vCard.getBytes("US-ASCII"));
//		} catch (IOException e) {
//			throw new PIMException(e.getMessage());
//		}
//	}
	
	
	

}
