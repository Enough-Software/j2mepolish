package de.enough.polish.qualitycontrol;

import de.enough.polish.exceptions.InvalidComponentException;

public class PolishVariableCondition {
	
	private String variableName;
	private String variableValue;

	public PolishVariableCondition( String definition ) throws InvalidComponentException {
		int splitIndex = definition.indexOf('=');
		if (splitIndex == -1) {
			throw new InvalidComponentException("Invalid condition: \"" + definition + "\".");
		}
		this.variableName = definition.substring(0, splitIndex );
		this.variableValue = definition.substring(splitIndex + 1);
	}

	public String getVariableName() {
		return this.variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public String getVariableValue() {
		return this.variableValue;
	}

	public void setVariableValue(String variableValue) {
		this.variableValue = variableValue;
	}

	
}
