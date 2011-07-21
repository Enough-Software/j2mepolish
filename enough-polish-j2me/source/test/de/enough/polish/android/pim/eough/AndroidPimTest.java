package de.enough.polish.android.pim.eough;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

import junit.framework.TestCase;
import android.database.Cursor;
import de.enough.polish.android.pim.Contact;
import de.enough.polish.android.pim.ContactList;
import de.enough.polish.android.pim.PIM;
import de.enough.polish.android.pim.PIMException;
import de.enough.polish.android.pim.enough.AndroidPim;
import de.enough.polish.android.pim.enough.ContactDao;
import de.enough.polish.android.pim.enough.ContactImpl;
import de.enough.polish.android.pim.enough.ContactListImpl;

public class AndroidPimTest extends TestCase {

	private final class DummyContactDao implements ContactDao {
		public ContactImpl getContactFromCursor(Cursor peopleCursor,
				ContactListImpl contactListImpl) {
			// TODO Auto-generated method stub
			return null;
		}

		public Contact importContact(ContactImpl contact) {
			// TODO Auto-generated method stub
			return null;
		}

		public Enumeration items(ContactListImpl contactListImpl) {
			// TODO Auto-generated method stub
			return null;
		}

		public Enumeration items(ContactImpl contact) {
			// TODO Auto-generated method stub
			return null;
		}

		public Enumeration items(String matchingValue) {
			// TODO Auto-generated method stub
			return null;
		}

		public Enumeration itemsByCategory(String category) {
			// TODO Auto-generated method stub
			return null;
		}

		public void persist(ContactImpl contact) {
			// TODO Auto-generated method stub
			
		}

		public void removeContact(ContactImpl contact) {
			// TODO Auto-generated method stub
			
		}

		public void lazyLoadAddrFields(ContactImpl contactImpl) {
			// TODO Auto-generated method stub
			
		}

		public void lazyLoadTelFields(ContactImpl contactImpl) {
			// TODO Auto-generated method stub
			
		}
	}

	public void testSerialize() throws IOException {
		AndroidPim pim = (AndroidPim) PIM.getInstance();
		pim.setContactDao(new DummyContactDao());
		ContactList contactList;
		try {
			contactList = (ContactList) pim.openPIMList(PIM.CONTACT_LIST, PIM.READ_WRITE);
		} catch (PIMException e) {
			e.printStackTrace();
			fail();
			return;
		}
		Contact contact = contactList.createContact();
		String[] values = new String[contactList.stringArraySize(Contact.NAME)];
		values[Contact.NAME_OTHER] = "öäü=abcdefghijklmnopqrstuvwxyzöäü=abcdefghijklmnopqrstuvwxyzöäü=abcdefghijklmnopqrstuvwxyzöäü=abcdefghijklmnopqrstuvwxyzöäü=abcdefghijklmnopqrstuvwxyzöäü=abcdefghijklmnopqrstuvwxyzöäü=abcdefghijklmnopqrstuvwxyz";
		contact.addStringArray(Contact.NAME, 0, values);
		
		ByteArrayOutputStream byteArrayOutputStream;
		try {
			byteArrayOutputStream = new ByteArrayOutputStream();
			pim.toSerialFormat(contact, byteArrayOutputStream, "ISO-8859-1", AndroidPim.SERIALIZATION_ENCODING_VCARD_21);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			fail();
			return;
		} catch (PIMException e) {
			e.printStackTrace();
			fail();
			return;
		}
//		String vCardString = byteArrayOutputStream.toString();
//		System.out.println(vCardString);
		byte[] vCardBytes = byteArrayOutputStream.toByteArray();
		int lastIndex = vCardBytes.length-1;
		int line = 0;
		int column = -1;
		for (int i = 0; i <= lastIndex; i++) {
			column++;
			byte b = vCardBytes[i];
			if( ! ((33 <= b && b <= 60) || (62 <= b && b <= 126 || b == '\r' || b == '\n' || b == '\t' || b == ' ' || b == '='))) {
				fail("A byte is not printable '"+b+"' at line '"+line+"' and column '"+column+"'");
			}
			if(b == '\r') {
				if(i+1>lastIndex || vCardBytes[i+1] != '\n') {
					fail("Found a CR without a LF at line '"+line+"' and column '"+column+"'");
				} else {
					line++;
					column = -1;
				}
			}
			if(b == '\n' && (i-1<0 || vCardBytes[i-1] != '\r')) {
				fail("Found a LF without a CR.");
			}
			
		}
		
		
	}
}
