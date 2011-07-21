/**
 * 
 */
package de.enough.polish.pim;

/**
 * <p>
 * 
 * </p>
 * @author RamaKrishna Sharvirala
 *
 */

public class ContactAddress {
	
	/**
	 * field to contain Country Part of Address
	*/
	private String countryOfAddress;
	
	/**
	 * field to contain Extra information about address
	*/
	private String extraInfoOfAddress;
	
	/**
	 * field to contain location details of address
	*/
	private String localityOfAddress;
	
	/**
	 * field to contain post box details of address
	*/
	private String poBoxOfAddress;
	
	/**
	 * field to contain postal code or zip code of address
	*/
	private String postalCode;
	
	/**
	 * field to contain location details of address
	*/
	private String regionOfAddress;
	
	/**
	 * field to contain location details of address
	*/
	private String streetAddress;
	
	
	/**
	 * @return returns country of address
	*/
	public String getCountryOfAddress() {
		if(this.countryOfAddress != null) {
			return this.countryOfAddress;
		} else {
			return "";
		}
	}
	
	/**
	 * setter method for countryOfAddress
	 * @param countryOfAddress 
	*/
	public void setCountryOfAddress(String countryOfAddress) {
		this.countryOfAddress = countryOfAddress;
	}
	
	/**
	 * @return returns extra info of address
	*/
	public String getExtraInfoOfAddress() {
		if(this.extraInfoOfAddress != null) {
			return this.extraInfoOfAddress;
		} else {
			return "";
		}
	}
	
	/**
	 * setter method for extraInfoOfAddress
	 * @param extraInfoOfAddress 
	*/
	public void setExtraInfoOfAddress(String extraInfoOfAddress) {
		this.extraInfoOfAddress = extraInfoOfAddress;
	}
	
	/**
	 * @return returns localityOfAddress
	*/
	public String getLocalityOfAddress() {
		if(this.localityOfAddress != null) {
			return this.localityOfAddress;
		} else {
			return "";
		}
	}
	
	/**
	 * setter method for localityOfAddress
	 * @param localityOfAddress 
	*/
	public void setLocalityOfAddress(String localityOfAddress) {
		this.localityOfAddress = localityOfAddress;
	}
	
	/**
	 * @return returns poBoxOfAddress
	*/
	public String getPoBoxOfAddress() {
		if(this.poBoxOfAddress != null) {
			return this.poBoxOfAddress;
		} else {
			return "";
		}
	}
	
	
	/**
	 * setter method for poBoxOfAddress
	 * @param poBoxOfAddress
	 */
	public void setPoBoxOfAddress(String poBoxOfAddress) {
		this.poBoxOfAddress = poBoxOfAddress;
	}
	
	/**
	 * @return returns postalCode
	 */
	public String getPostalCode() {
		if(this.postalCode != null) {
			return this.postalCode;
		} else {
			return "";
		}
	}
	
	/**
	 * setter method for postalCode
	 * @param postalCode
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	
	/**
	 * @return returns regionOfAddress
	 */
	public String getRegionOfAddress() {
		if(this.regionOfAddress != null) {
			return this.regionOfAddress;
		} else {
			return "";
		}
	}
	
	/**
	 * setter method for regionOfAddress
	 * @param regionOfAddress
	 */
	public void setRegionOfAddress(String regionOfAddress) {
		this.regionOfAddress = regionOfAddress;
	}
	
	/**
	 * @return returns streetAddress
	 */
	public String getStreetAddress() {
		if(this.streetAddress != null) {
			return this.streetAddress;
		} else {
			return "";
		}
	}
	
	/**
	 * setter method for streetAddress
	 * @param streetAddress 
	 */
	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}
	
	
}
