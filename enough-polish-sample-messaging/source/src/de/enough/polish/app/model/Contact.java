package de.enough.polish.app.model;

public class Contact
{
	private String	firstName;
	private String	lastName;
	private String lowercaseCombined;

	public Contact(String firstName, String lastName)
	{
		this.firstName = firstName;
		this.lastName = lastName;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName()
	{
		return this.firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName()
	{
		return this.lastName;
	}

	public boolean matches(String text)
	{
		if (this.lowercaseCombined == null)
		{
			this.lowercaseCombined = this.firstName.toLowerCase() + this.lastName.toLowerCase();
		}
		return (this.lowercaseCombined.indexOf(text) != -1);
	}
	
	public String toString()
	{
		return this.firstName + " " + this.lastName;
	}

	public void setFirstName(String name)
	{
		this.firstName = name;
	}

	public void setLastName(String name)
	{
		this.lastName = name;
	}

}
