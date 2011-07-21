package de.enough.polish.qualitycontrol;

import java.util.HashMap;
import java.util.Map;

import org.jdom.Element;

import de.enough.polish.exceptions.InvalidComponentException;
import de.enough.polish.util.StringUtil;

public class PolishVariable {

	private final VariablesManager variablesManager;
	private String name;
	private String type;
	private String description;
	private String[] values;
	private String defaultValue;
	private String[] appliesTo;
	private Map appliesToMap;
	private boolean invisible;
	private PolishVariableCondition[] conditions;

	public PolishVariable(Element definition, VariablesManager variablesManager) throws InvalidComponentException {
		this.variablesManager = variablesManager;
		setDefinition( definition );
	}

	public void setDefinition(Element definition) throws InvalidComponentException {
		this.name = definition.getAttributeValue("name");
		this.type = definition.getAttributeValue("type");
		this.description = definition.getAttributeValue("description");
		String valuesStr = definition.getAttributeValue("values");
		if (valuesStr != null) {
			this.values = StringUtil.splitAndTrim(valuesStr, ',');
		} else if ("boolean".equals(this.type)) {
			this.values = new String[]{ "true", "false" };
		}
		this.defaultValue = definition.getAttributeValue("default");
		String[] applies = StringUtil.splitAndTrim( definition.getAttributeValue("appliesTo"), ',' );
		this.appliesTo = applies;
		this.appliesToMap = new HashMap();
		for (int i = 0; i < applies.length; i++) {
			String apply = applies[i];
			this.appliesToMap.put(apply, Boolean.TRUE);
		}
		this.invisible = "true".equals( definition.getAttributeValue("invisible") );
		String conditionsStr = definition.getAttributeValue("conditions");
		if (conditionsStr != null) {
			String[] conditionsAsStringArray = StringUtil.splitAndTrim( conditionsStr, ',' );
			this.conditions = new PolishVariableCondition[ conditionsAsStringArray.length ];
			for (int i = 0; i < conditionsAsStringArray.length; i++) {
				this.conditions[i] = new PolishVariableCondition( conditionsAsStringArray[i] );
			}
		}
	}


	public String getName() {
		return this.name;
	}

	public String getType() {
		return this.type;
	}

	public String getDescription() {
		return this.description;
	}

	public String[] getValues() {
		return this.values;
	}

	public String getDefaultValue() {
		return this.defaultValue;
	}

	public String[] getAppliesTo() {
		return this.appliesTo;
	}

	public boolean isInvisible() {
		return this.invisible;
	}

	public PolishVariableCondition[] getConditions() {
		return this.conditions;
	}

	public boolean isVisibleAndDefined() {
		return !this.invisible && (this.defaultValue != null && this.defaultValue.length() > 0);
	}

	public boolean appliesTo(String className, String shortName) {
		return this.appliesToMap.containsKey(className) || (shortName != null && this.appliesToMap.containsKey(shortName));
	}

	
	
}
