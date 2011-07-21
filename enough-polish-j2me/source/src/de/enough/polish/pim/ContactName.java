/**
 * 
 */
package de.enough.polish.pim;

/**
 * @author RamaKrishna Sharvirala
 *
 */
public class ContactName {
	
	/**
	 * field to contain family name of contact 
	*/
	private String familyName;
	
	/**
	 *  field to contain given name of contact 
	 */
	private String givenName;
	
	/**
	 *  field to contain other name of contact 
	 */
	private String otherName;
	
	/**
	 *  field to contain prefix name of contact 
	 */
	private String prefixOfName;
	
	/**
	 *  field to contain suffix name of contact 
	 */
	private String suffixOfName;
	
	
	/**
	 * @return returns familyName
	 */
	public String getFamilyName() {
		if(this.familyName != null) {
			return this.familyName;
		} else {
			return "";
		}
	}
	
	/**
	 *  setter method for familyName
	 * @param familyName
	 */
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}
	
	/**
	 * @return returns givenName
	 */
	public String getGivenName() {
		if(this.givenName != null) {
			return this.givenName;
		} else {
			return "";
		}
	}
	
	/**
	 * setter method for givenName
	 * @param givenName
	 */
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}
	
	/**
	 * @return returns otherName
	 */
	public String getOtherName() {
		if(this.otherName != null) {
			return this.otherName;
		} else {
			return "";
		}
	}
	
	/**
	 * setter method for otherName
	 * @param otherName
	 */
	public void setOtherName(String otherName) {
		this.otherName = otherName;
	}
	
	/**
	 * @return returns prefixOfName
	 */
	public String getPrefixOfName() {
		if(this.prefixOfName != null) {
			return this.prefixOfName;
		} else {
			return "";
		}
	}

	/**
	 * setter method for prefixOfName
	 * @param prefixOfName
	 */
	public void setPrefixOfName(String prefixOfName) {
		this.prefixOfName = prefixOfName;
	}
	
	/**
	 * @return returns suffixOfName
	 */
	public String getSuffixOfName() {
		if(this.suffixOfName != null) {
			return this.suffixOfName;
		} else {
			return "";
		}
	}
	
	/**
	 * setter method for suffixOfName
	 * @param suffixOfName
	 */
	public void setSuffixOfName(String suffixOfName) {
		this.suffixOfName = suffixOfName;
	}
	
	
	
}
