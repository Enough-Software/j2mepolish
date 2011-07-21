/**
 * 
 */
package de.enough.polish.pim;

import java.util.Date;

/**
 * @author RamaKrishna Sharvirala
 *
 */
public class PimContact {
	/**
	 * object to contain Contact Name details
	*/
	private ContactName contactName;
	/**
	 * object to contain Address details
	*/
	
	private ContactAddress contactAddress;
	
	/**
	 * field to contain formatted Address of PIM Contact
	*/
	private String formattedAddress;
	
	/**
	 * field to contain name of PIM Contact
	*/
	private String name;
	
	/**
	 * field to contain formattedName of PIM Contact
	*/
	private String formattedName;
	/**
	 * field to contain nickName of PIM Contact
	 */
	private String nickName;
	/**
	 * field to contain emailAddress of PIM Contact
	 */
	private String emailAddress;
	/**
	 * field to contain telephoneNumber of PIM Contact
	*/
	private String telephoneNumber;
	
	/**
	 * field to contain dateOfBirth of PIM Contact
	 */
	private Date dateOfBirth;
	/**
	 * field to contain notes of PIM Contact
	 */
	private String notes;
	/**
	 * field to contain organization of PIM Contact
	 */
	private String organization;	
	
	/**
	 * field to contain title of PIM Contact
	 */
	private String titleOfContact;
	/**
	 * field to contain url of PIM Contact
	 */
	private String urlForContact;
	/**
	 * field to contain photoUrl of PIM Contact
	 */
	private String photoUrl;
	/**
	 * field to contain publicKeyString of PIM Contact
	 */
	private String publicKeyString;
	
	/**
	 * field to contain binary data of publicKey of PIM Contact
	 */
	private byte[] publicKey;
	/**
	 * field to contain binary data of photo of PIM Contact
	 */
	private byte[] photo;
	
	/**
	 * field to contain Unique Identifier of PIM Contact
	*/
	private String uid;
	
	/**
	 * field to contain Class Type of PIM Contact
	*/
	private int classOfContact;
	
	/**
	 * field to contain Last revised date of PIM Contact
	 */
	private Date lastRevision;
	
	
	/**
	 * returns contactName
	 * @return contactName
	 */
	public ContactName getContactName() {
		return this.contactName;
	}
	
	/**
	 * setter method for contactName
	 * @param contactName
	 */
	public void setContactName(ContactName contactName) {
		this.contactName = contactName;
	}
	
	/**
	 * returns contactAddress
	 * @return contactAddress
	 */
	public ContactAddress getContactAddress() {
		return this.contactAddress;
	}
	
	/**
	 * setter method for contactAddress
	 * @param contactAddress
	 */
	public void setContactAddress(ContactAddress contactAddress) {
		this.contactAddress = contactAddress;
	}
	
	/**
	 * returns formattedAddress
	 * @return formattedAddress
	 */
	public String getFormattedAddress() {
		if(this.formattedAddress != null) {
			return this.formattedAddress;
		} else {
			if(this.contactAddress != null) {
				return this.contactAddress.getPoBoxOfAddress() + " " + this.contactAddress.getStreetAddress() + " " 
				+ this.contactAddress.getLocalityOfAddress() + " " + this.contactAddress.getRegionOfAddress() + " "
				+ this.contactAddress.getExtraInfoOfAddress() + " " + this.contactAddress.getCountryOfAddress() + " "
				+ this.contactAddress.getPostalCode();
			} else {
				return "";
			}
		}
	}
	
	/**
	 *  setter method for formattedAddress
	 * @param formattedAddress
	 */
	public void setFormattedAddress(String formattedAddress) {
		this.formattedAddress = formattedAddress;
	}
	
	/**
	 * returns name
	 * @return name
	 */
	public String getName() {
		if(this.name != null) {
			return this.name;
		} else {
			return "";
		}
	}
	
	/**
	 *  setter method for name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * returns formattedName
	 * @return formattedName
	 */
	public String getFormattedName() {
		if(this.formattedName != null) {
			return this.formattedName;
		} else {
			if(this.contactName != null) {
				return this.contactName.getGivenName() + " " + this.contactName.getFamilyName() 
					+ " " + this.contactName.getOtherName();
			} else {
				return "";
			}
		}
	}
	
	/**
	 * setter method for formattedName
	 * @param formattedName
	 */
	public void setFormattedName(String formattedName) {
		this.formattedName = formattedName;
	}
	
	/**
	 * returns nickName
	 * @return nickName
	 */
	public String getNickName() {
		if(this.nickName != null) {
			return this.nickName;
		} else {
			return "";
		}
	}
	
	/**
	 * setter method for nickName
	 * @param nickName
	 */
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	/**
	 * returns emailAddress
	 * @return emailAddress
	 */ 
	public String getEmailAddress() {
		if(this.emailAddress != null) {
			return this.emailAddress;
		} else {
			return "";
		}
	}
	
	/**
	 * setter method for emailAddress
	 * @param emailAddress
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * returns telephoneNumber
	 * @return telephoneNumber
	 */
	public String getTelephoneNumber() {
		if(this.telephoneNumber != null) {
			return this.telephoneNumber;
		} else {
			return "";
		}
	}
	
	/**
	 *  setter method for telephoneNumber
	 * @param telephoneNumber
	 */
	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}
	
	/**
	 * returns dateOfBirth
	 * @return dateOfBirth
	 */
	public Date getDateOfBirth() {
		if(this.dateOfBirth != null) {
			return this.dateOfBirth;
		} else {
			return null;
		}
	}
	
	/**
	 * setter method for dateOfBirth
	 * @param dateOfBirth
	 */
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	
	/**
	 * returns notes
	 * @return notes
	 */
	public String getNotes() {
		if(this.notes != null) {
			return this.notes;
		} else {
			return "";
		}
	}
	
	/**
	 *  setter method for notes
	 * @param notes
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	/**
	 * returns organization
	 * @return organization
	 */
	public String getOrganization() {
		if(this.organization != null) {
			return this.organization;
		} else {
			return "";
		}
	}
	
	/**
	 * setter method for organization
	 * @param organization
	*/
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	
	/**
	 * returns titleOfContact
	 * @return titleOfContact
	*/
	public String getTitleOfContact() {
		if(this.titleOfContact != null) {
			return this.titleOfContact;
		} else {
			return "";
		}
	}
	
	/**
	 * setter method for titleOfContact
	 * @param titleOfContact
	*/
	public void setTitleOfContact(String titleOfContact) {
		this.titleOfContact = titleOfContact;
	}
	
	/**
	 * returns urlForContact
	 * @return urlForContact
	*/
	public String getUrlForContact() {
		if(this.urlForContact != null) {
			return this.urlForContact;
		} else {
			return "";
		}
	}
	
	/**
	 * setter method for urlForContact
	 * @param urlForContact
	*/
	public void setUrlForContact(String urlForContact) {
		this.urlForContact = urlForContact;
	}
	
	/**
	 * returns photoUrl
	 * @return photoUrl
	*/
	public String getPhotoUrl() {
		if(this.photoUrl != null) {
			return this.photoUrl;
		} else {
			return "";
		}
	}
	
	/**
	 * setter method for photoUrl
	 * @param photoUrl
	*/
	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}
	
	/**
	 * returns publicKeyString
	 * @return publicKeyString
	*/ 
	public String getPublicKeyString() {
		if(this.publicKeyString != null) {
			return this.publicKeyString;
		} else {
			return "";
		}
	}
	
	/**
	 * setter method for publicKeyString
	 * @param publicKeyString
	*/
	public void setPublicKeyString(String publicKeyString) {
		this.publicKeyString = publicKeyString;
	}
	
	/**
	 * returns publicKey
	 * @return publicKey
	*/
	public byte[] getPublicKey() {
		return this.publicKey;
	}
	
	/**
	 * setter method for publicKey
	 * @param publicKey
	*/
	public void setPublicKey(byte[] publicKey) {
		this.publicKey = publicKey;
	}
	
	/**
	 * returns photo
	 * @return photo
	*/
	public byte[] getPhoto() {
		return this.photo;
	}
	
	/**
	 * setter method for photo
	 * @param photo
	*/
	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}
	
	/**
	 * returns uid
	 * @return uid
	*/
	public String getUid() {
		if(this.uid != null) {
			return this.uid;
		} else {
			return "";
		}
	}
	
	/**
	 * setter method for uid
	 * @param uid
	*/
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	/**
	 * returns classOfContact
	 * @return classOfContact
	*/
	public int getClassOfContact() {
		return this.classOfContact;
	}
	
	/**
	 * setter method for classOfContact
	 * @param classOfContact
	*/
	public void setClassOfContact(int classOfContact) {
		this.classOfContact = classOfContact;
	}
	
	/**
	 * returns lastRevision
	 * @return lastRevision
	*/
	public Date getLastRevision() {
		return this.lastRevision;
	}
	
	/**
	 *  setter method for lastRevision
	 * @param lastRevision
	*/
	public void setLastRevision(Date lastRevision) {
		this.lastRevision = lastRevision;
	}
	
	
}
