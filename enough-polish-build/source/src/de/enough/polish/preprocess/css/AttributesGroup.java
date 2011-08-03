package de.enough.polish.preprocess.css;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains CSS attributes within Style objects
 * @author Robert Virkus
 *
 */
public class AttributesGroup extends HashMap<String,String> {
	
	private static final long serialVersionUID = 1L;
	
	private Style parentStyle;
	private final String groupName;
	
	public AttributesGroup(AttributesGroup parentGroup) {
		super(parentGroup);
		this.parentStyle = parentGroup.parentStyle;
		this.groupName = parentGroup.groupName;
	}


	public AttributesGroup(Style parent, String groupName) {
		super();
		this.parentStyle = parent;
		this.groupName = groupName;
	}
	
	public AttributesGroup( Style parent, String groupName, Map<String,String> attributes )  {
		super( attributes );
		this.parentStyle = parent;
		this.groupName = groupName;
	}

	public AttributesGroup(Style parent, String groupName, int size) {
		super( size );
		this.parentStyle = parent;
		this.groupName = groupName;
	}
	
	public String getValue( String key ) {
		String value = (String) get(key);
		if (value == null) {
			value = (String) get(this.groupName + "-" + key);
		}
		return value;
	}
	
	public Style getStyle() {
		return this.parentStyle;
	}
	
	public void setStyle( Style parentStyle ) {
		this.parentStyle = parentStyle;
	}
	
	public String getGroupName() {
		return this.groupName;
	}

	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		if (this.parentStyle != null) {
			buffer.append(this.parentStyle.getSelector()).append(" {\n");
		}
		buffer.append(this.groupName).append(" {\n");
		Object[] keys = keySet().toArray();
		for (int i = 0; i < keys.length; i++) {
			Object key = keys[i];
			buffer.append(" ").append(key).append(": ").append(get(key)).append(";\n");
		}
		buffer.append("}\n");
		if (this.parentStyle != null) {
			buffer.append("}");
		}
		return buffer.toString();
	}
	
}