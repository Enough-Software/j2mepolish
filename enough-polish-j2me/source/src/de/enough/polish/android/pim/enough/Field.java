//#condition polish.android
package de.enough.polish.android.pim.enough;

import java.util.ArrayList;
import java.util.Arrays;

public class Field {
	protected final FieldInfo fieldInfo;
	protected ArrayList<Object> values;
	protected ArrayList<Integer> attributes;
	public Field(FieldInfo fieldInfo) {
		this.fieldInfo = fieldInfo;
		this.values = new ArrayList<Object>();
		this.attributes = new ArrayList<Integer>();
	}
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.fieldInfo);
		buffer.append("\n");
		int numberOfValues = this.values.size();
		for(int i = 0; i < numberOfValues; i++) {
			Object value = this.values.get(i);
			if(value instanceof Object[]) {
				Object[] array = (Object[]) value;
				value = Arrays.toString(array);
			}
			buffer.append("Value["+i+"]:"+value+".");
			buffer.append("Attr["+i+"]:"+this.attributes.get(i)+".");
			buffer.append("\n");
		}
		buffer.append("\n");
		return buffer.toString();
	}
}