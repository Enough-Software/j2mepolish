/*
 * Created on Jun 1, 2004
 */
package de.enough.webprocessor.util;

/**
 * @author robertvirkus
 */
public class Variable {
	private String name;
	private String value;
	private String type;

	/**
	 * Creates new uninitialised Variable
	 */
	public Variable() {
		// no values are set here
	}

	/**
	 * Creates a new Varable
	 * @param name (String) the name of this variable
	 * @param value (String) the value of this variable
	 */
	public Variable(String name, String value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * @return the name of this variables
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the value of this variable
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * @param name the name of this variable
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param value the value of this variable
	 */
	public void setValue(String value ) {
		this.value = value;
	}

	/**
	 * @return Returns the type of this variable.
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * @param type The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}


}
