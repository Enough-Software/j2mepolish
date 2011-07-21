//#condition polish.java5

package de.enough.polish.java5;

import java.util.Hashtable;


/**
 * A version of the 1.5 java.lang.Enum class for the Java ME VM.
 */
public class Enum {

	private final int ordinal;

	private final String name;

	private static final Hashtable enumValues = new Hashtable();

	protected Enum(String name, int ordinal) {
		this.name = name;
		this.ordinal = ordinal;
	}

	protected static final void setEnumValues(Object[] values, Class c) {
		synchronized(enumValues) {
			enumValues.put(c, values);
		}
	}

	protected static final Object[] getEnumValues(Class class_) {
		synchronized(enumValues) {
			Object[] values = (Object[]) enumValues.get(class_);
			if (values != null)
				return values;
		}

//		if (!(class_ Enum))
//			return null;

		// force initialization of class_ as
		// class loader may not have called static initializers yet
//		try {
//			Class.forName(class_.getName(), true, class_.getClassLoader());
//		} catch (ClassNotFoundException e) {
//			// can not happen: class_ has already been resolved.
//		}
	
		synchronized(enumValues) {
			return (Object[]) enumValues.get(class_);
		}
	}


	public static Enum valueOf(Class enumType, String name) {

		if (enumType == null) {
			throw new NullPointerException("enumType is null");
		}

		if (name == null) {
			throw new NullPointerException("name is null");
		}

		Object[] enums = getEnumValues(enumType);

		if (enums != null) {
			for (int i = 0; i < enums.length; i++) {
				Enum enum_ = (Enum) enums[i];
				if (enum_.name.equals(name)) {
					return enum_;
				}
			}
		}

		throw new IllegalArgumentException("No enum const " + enumType + "."
				+ name);
	}

	public final boolean equals(Object other) {
		return other == this;
	}

	public final int hashCode() {
		return System.identityHashCode(this);
	}

	public String toString() {
		return this.name;
	}

	public final int compareTo(Enum e) {
		Class c1 = getDeclaringClass();
		Class c2 = e.getDeclaringClass();

		if (c1 == c2) {
			return this.ordinal - e.ordinal;
		}

		throw new ClassCastException();
	}


	public final String name() {
		return this.name;
	}

	public final int ordinal() {
		return this.ordinal;
	}

	public final Class getDeclaringClass() {
		return getClass();
//		Class clazz = getClass();
//		Class superClass = clazz.getSuperclass();
//		if (superClass != Enum_.class) {
//			return superClass;
//		} else {
//			return clazz;
//		}
	}
	
	public Object clone() {
		return this;
	}

}
