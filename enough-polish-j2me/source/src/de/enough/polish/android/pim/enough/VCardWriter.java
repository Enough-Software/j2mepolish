//#condition polish.android

package de.enough.polish.android.pim.enough;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import de.enough.polish.android.pim.Contact;
import de.enough.polish.android.pim.ContactList;
import de.enough.polish.android.pim.PIMItem;
import de.enough.polish.util.TextUtil;

/**
 * We assume that a Contact object is a vCard object.<br>
 * The mapping of the PIM entites to the vCard entities is as follows:
 * <table border="1px solid">
 * <tr><th>Contact Field</th><th>Contact Attribute</th><th>vCard Field</th><th>vCard Property Key</th><th>vCard Property Value</th></tr>
 * <tr><td>NAME</td><td>&nbsp;</td><td>N</td>&nbsp;<td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>ADDR</td><td>&nbsp;</td><td>ADR</td>&nbsp;<td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>ADDR</td><td>HOME</td><td>ADR</td><td>TYPE</td><td>HOME,DOM</td></tr>
 * <tr><td>ADDR</td><td>WORK</td><td>ADR</td><td>TYPE</td><td>WORK</td></tr>
 * <tr><td>ADDR</td><td>OTHER</td><td>ADR</td><td>TYPE</td><td>INTL</td></tr>
 * <tr><td>FORMATTED_NAME</td><td>&nbsp;</td><td>FN</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>BIRTHDAY</td><td>&nbsp;</td><td>BDAY</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>FORMATTED_ADDR</td><td>&nbsp;</td><td>LABEL</td><td>ENCODING</td><td>QUOTED-PRINTABLE</td></tr>
 * <tr><td>FORMATTED_ADDR</td><td>HOME</td><td>LABEL</td><td>TYPE</td><td>HOME,DOM</td></tr>
 * <tr><td>FORMATTED_ADDR</td><td>WORK</td><td>LABEL</td><td>TYPE</td><td>WORK</td></tr>
 * <tr><td>TEL</td><td>&nbsp;</td><td>TEL</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>TEL</td><td>HOME</td><td>TEL</td><td>TYPE</td><td>HOME</td></tr>
 * <tr><td>TEL</td><td>WORK</td><td>TEL</td><td>TYPE</td><td>WORK</td></tr>
 * <tr><td>TEL</td><td>PREFERRED</td><td>TEL</td><td>TYPE</td><td>PREF</td></tr>
 * <tr><td>TEL</td><td>FAX</td><td>TEL</td><td>TYPE</td><td>FAX</td></tr>
 * <tr><td>TEL</td><td>MOBILE</td><td>TEL</td><td>TYPE</td><td>CELL</td></tr>
 * <tr><td>TEL</td><td>PAGER</td><td>TEL</td><td>TYPE</td><td>PAGER</td></tr>
 * <tr><td>TEL</td><td>OTHER</td><td>TEL</td><td>TYPE</td><td>VOICE</td></tr>
 * <tr><td>EMAIL</td><td>&nbsp;</td><td>EMAIL</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>TITLE</td><td>&nbsp;</td><td>TITLE</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>ORG</td><td>&nbsp;</td><td>ORG</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>NOTE</td><td>&nbsp;</td><td>NOTE</td><td>ENCODING</td><td>QUOTED-PRINTABLE</td></tr>
 * <tr><td>REV</td><td>&nbsp;</td><td>REV</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>URL</td><td>&nbsp;</td><td>URL</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>UID</td><td>&nbsp;</td><td>UID</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>PUBLIC_KEY</td><td>&nbsp;</td><td>KEY</td><td>TYPE</td><td>PGP</td></tr>
 * <tr><td>PUBLIC_KEY_STRING</td><td>&nbsp;</td><td>KEY</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * </table>
 * Only the first value of a field is serialized to prevent duplication in the vCard properties.
 */
public class VCardWriter {

	
	public static void writeVCard2_1(PIMItem item,OutputStream os, String enc) throws IOException {
		StringBuffer buffer = new StringBuffer();
		
		final String CRLF = "\r\n";
		Contact contact = (Contact)item;
		ContactList contactList = (ContactList)contact.getPIMList();
		buffer.append("BEGIN:VCARD");
		buffer.append(CRLF);
		buffer.append("VERSION:2.1");
		buffer.append(CRLF);
		
		// N
		if(contactList.isSupportedField(Contact.NAME)){
			int numberOfValues = contact.countValues(Contact.NAME);
			if(numberOfValues > 0) {
				buffer.append("N:");
				String[] nameComponents = contact.getStringArray(Contact.NAME, 0);
				addNameComponent(buffer, contactList, nameComponents,Contact.NAME_FAMILY);
				buffer.append(";");
				addNameComponent(buffer, contactList, nameComponents,Contact.NAME_GIVEN);
				buffer.append(";");
				addNameComponent(buffer, contactList, nameComponents,Contact.NAME_OTHER);
				buffer.append(";");
				addNameComponent(buffer, contactList, nameComponents,Contact.NAME_SUFFIX);
				buffer.append(";");
				addNameComponent(buffer, contactList, nameComponents,Contact.NAME_PREFIX);
			}
		}
		
		buffer.append(CRLF);
		buffer.append("END:VCARD");
		buffer.append(CRLF);
		
		String encodedVCard = TextUtil.encodeAsQuotedPrintable(buffer.toString(),enc);
		OutputStreamWriter writer = new OutputStreamWriter(os);
		writer.write(encodedVCard);
		writer.flush();
	}

	/**
	 * 
	 * @param buffer
	 * @param contactList
	 * @param nameComponents
	 * @param nameComponent NAME_FAMILY,...
	 */
	private static void addNameComponent(StringBuffer buffer,ContactList contactList, String[] nameComponents, int nameComponent) {
		String namePart;
		if(contactList.isSupportedArrayElement(Contact.NAME, nameComponent)) {
			namePart = nameComponents[nameComponent];
			if(namePart != null) {
				buffer.append(namePart);
			}
		}
	}

	public static void writeVCard3_0(PIMItem item, OutputStream os, String enc) {
		// TODO Auto-generated method stub
		
	}
}
