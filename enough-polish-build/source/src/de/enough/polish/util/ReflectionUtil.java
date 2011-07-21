/*
 * Created on 16-Jul-2004 at 02:57:35.
 * 
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.util;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import de.enough.polish.BuildException;
import de.enough.polish.Variable;

/**
 * <p>PoulateUtil is used to set variables via reflection.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        16-Jul-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public final class ReflectionUtil {
	
	private final static HashMap PRIMITIVE_CLASS_WRAPPERS = new HashMap();
	static {
		PRIMITIVE_CLASS_WRAPPERS.put(Byte.TYPE, Byte.class);
		PRIMITIVE_CLASS_WRAPPERS.put(Short.TYPE, Short.class);
		PRIMITIVE_CLASS_WRAPPERS.put(Integer.TYPE, Integer.class);
		PRIMITIVE_CLASS_WRAPPERS.put(Long.TYPE, Long.class);
		PRIMITIVE_CLASS_WRAPPERS.put(Float.TYPE, Float.class);
		PRIMITIVE_CLASS_WRAPPERS.put(Double.TYPE, Double.class);
		PRIMITIVE_CLASS_WRAPPERS.put(Character.TYPE, Character.class);
		PRIMITIVE_CLASS_WRAPPERS.put(Boolean.TYPE, Boolean.class);
	}

	/**
	 * Populates the given object with the specified parameter.
	 * For the parameter-name the given object needs to specify
	 * either set[param-name]( String ), set[param-name]( File ) or set[param-name]( boolean ).
	 * When the parameter "message" is provided, either the method
	 * setMessage( String value ), setMessage( File value ) or setMessage( boolean ) needs
	 * to be defined by the given object.
	 * When the object defines both methods, it cannot be foreseen which
	 * one of them will be called.
	 * 
	 * @param object the object which should be populated.
	 * @param parameter the parameter.
	 * @param baseDir the base directory for the population.
	 * @throws  IllegalArgumentException when a parameter has a syntax error
	 *        or when a needed method has not be found. 
	 */
	public static void populate( Object object, Variable parameter, File baseDir ) {
		String methodName = parameter.getName();
		if (methodName == null) {
			throw new IllegalArgumentException( "The parameter does not contain a name." );
		}
		methodName = "set" + Character.toUpperCase( methodName.charAt( 0 ) ) + methodName.substring( 1 );
		Class objectClass = object.getClass();
		Method method = null;
		Object argument = null;
		String value = parameter.getValue();
		try {
			method = objectClass.getMethod(methodName, new Class[]{ String.class } );
			argument = value;
		} catch (NoSuchMethodException e) {
			try {
				method = objectClass.getMethod(methodName, new Class[]{ File.class } );
				if (value != null) {
					File file = new File( value );
					if (!file.isAbsolute()) {
						file = new File( baseDir, value );
					}
					argument = file;
				}
			} catch (NoSuchMethodException e2) {
				try {
					method = objectClass.getMethod(methodName, new Class[]{ Boolean.TYPE } );
					argument = Boolean.valueOf( CastUtil.getBoolean(value) );
				} catch (NoSuchMethodException e3) {
					throw new IllegalArgumentException( "Unable to retrieve method " + methodName + ": " + e3.toString() );
				}				
			}
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new IllegalArgumentException( "Unable to retrieve method " + methodName + ": " + e.toString() );
		}
		try {
			// okay, we've now have a method and an argument, so let's invoke it:
			method.invoke(object, new Object[]{ argument } );
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			String message = "Unable to set the parameter [" + parameter.getName() + "] with value [" + parameter.getValue() + "] for class [" + object.getClass().getName() + "]: " + e.toString();
			throw new IllegalArgumentException( message );				
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			String message = "Unable to set the parameter [" + parameter.getName() + "] with value [" + parameter.getValue() + "] for class [" + object.getClass().getName() + "]: " + e.toString();
			throw new IllegalArgumentException( message );				
		}
	}
	
	/**
	 * Populates the given object with the specified parameters.
	 * For each parameter-name the given object needs to specify
	 * either set[param-name]( String ), set[param-name]( File ) or set[param-name]( boolean ).
	 * When the parameter "message" is provided, either the method
	 * setMessage( String value ), setMessage( File value ) or setMessage( boolean ) needs
	 * to be defined by the given object.
	 * When the object defines both methods, it cannot be foreseen which
	 * one of them will be called.
	 * 
	 * @param object the object which should be populated.
	 * @param parameters the parameters.
	 * @param baseDir the base directory for the population.
	 * @throws  IllegalArgumentException when a parameter has a syntax error
	 *        or when a needed method has not be found. 
	 */
	public static void populate( Object object, Variable[] parameters, File baseDir ) {
		Class objectClass = object.getClass();
		try {
			// first check whether the object in question has implmented the setParameters( Variable[] parameters, File baseDir ) method.
			Method setParametersMethod = objectClass.getMethod("setParameters", new Class[]{ Variable[].class, File.class } );
			setParametersMethod.invoke( object, new Object[]{ parameters, baseDir } );
			return;
		} catch (Exception e) {
			// okay, try the traditional settings...
		}
		// put all methods into a hash map:
		Method[] methods = objectClass.getMethods();
		HashMap methodsByName = new HashMap();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			Class[] parameterTypes = method.getParameterTypes();
			if (parameterTypes.length == 1) {
				Class parameterType = parameterTypes[0];
				if (parameterType == String.class || parameterType == File.class || parameterType == Boolean.TYPE ) {
					methodsByName.put( method.getName(), method );
				}
			}
		}
		
		// split parameters into single ones:
		for (int i = 0; i < parameters.length; i++) {
			Variable param = parameters[i];
			try {
				populate( object, baseDir, param, methodsByName );
			} catch (IllegalArgumentException e) {
                RuntimeException runtimeException = cleanupExceptionHierarchy(e,null);
                throw runtimeException;
			} catch (IllegalAccessException e) {
			    String message = "Unable to set the parameter [" + param.getName() + "] with value [" + param.getValue() + "] for class [" + object.getClass().getName() + "]: " + e.toString();
                RuntimeException runtimeException = cleanupExceptionHierarchy(e,message);
                throw runtimeException;
			} catch (InvocationTargetException e) {
			    String message = "Unable to set the parameter [" + param.getName() + "] with value [" + param.getValue() + "] for class [" + object.getClass().getName() + "]: " + e.toString();
                RuntimeException runtimeException = cleanupExceptionHierarchy(e,message);
				throw runtimeException;
			}
		}
	}

    /**
     * Searches for a BuildException instance in the cause stack of 'throwable' and returns it.
     * If it is not found the method throws a generic exception.
     * @param throwable the Throwable instance to search
     * @param message a message for the generic exception
     * @return a BuildException instance if it was found in the cause stack of 'throwable' or a generic exception
     */
    private static RuntimeException cleanupExceptionHierarchy(Throwable throwable, String message) {
        if(throwable == null) {
            return new IllegalArgumentException();
        }
        Throwable cause = throwable;
        do {
            if(cause instanceof BuildException) {
                return (BuildException)cause;
            }
            cause = cause.getCause();
        }
        while(cause != null);
        if(message == null) {
            message = "";
        }
        return new IllegalArgumentException( message ); 
    }
    
	/**
	 * Populates the object with the specified parameter.
	 * 
	 * @param object the object which should be populated.
	 * @param baseDir the base directory for the population.
	 * @param param the parameter
	 * @param methodsByName the methods known of the object
	 * @throws IllegalArgumentException when a parameter has a syntax error
	 *        or when a needed method has not be found. 
	 * @throws InvocationTargetException when the method could not be called
	 * @throws IllegalAccessException when the method could not be accessed
	 */
	private static void populate(Object object, File baseDir, Variable param, HashMap methodsByName) 
	throws IllegalArgumentException, IllegalAccessException, InvocationTargetException 
	{
		String name = param.getName();
		String value = param.getValue();
		String methodName = "set" + Character.toUpperCase( name.charAt(0)) + name.substring( 1 );
		Method method = (Method) methodsByName.get( methodName );
		if (method == null) {
			throw new IllegalArgumentException("To use the parameter [" 
					+ name + "] the class [" + object.getClass().getName() 
					+ "] needs to specify the method " + methodName 
					+ "(String), " + methodName 
					+ "(boolean) or " + methodName + "(File). " );
		}
		Class paramType = method.getParameterTypes()[0];
		if (paramType == String.class) {
			method.invoke(object, new Object[]{ value } );
		} else if (paramType == Boolean.TYPE ) {
			Boolean argument = Boolean.valueOf( CastUtil.getBoolean(value) );
			method.invoke(object, new Object[]{ argument } );
		} else {
			File file = new File( value );
			if (!file.isAbsolute()) {
				file = new File( baseDir.getAbsolutePath() + File.separator + value );
			}
			method.invoke(object, new Object[]{ file } );
		}
	}
	
	
	/**
	 * Retrieves the value of the specified int-field of the given object.
	 * 
	 * @param object the object that holds the field
	 * @param fieldName the name of the field
	 * @return the field value
	 * @throws NoSuchFieldException when the field does not exist
	 */
	public static int getIntField( Object object, String fieldName ) 
	throws NoSuchFieldException 
	{
		Field field = getField( object, fieldName );
		try {
			return field.getInt(object);
		} catch (Exception e) {
			e.printStackTrace();
			throw new NoSuchFieldException("unable to access field [" + fieldName + "]: " + e.toString() );
		}
	}

	/**
	 * Retrieves the value of the specified boolean-field of the given object.
	 * 
	 * @param object the object that holds the field
	 * @param fieldName the name of the field
	 * @return the field value
	 * @throws NoSuchFieldException when the field does not exist
	 */
	public static boolean getBooleanField(Object object, String fieldName ) 
	throws NoSuchFieldException 
	{
	
		Field field = getField( object, fieldName );
		try {
			return field.getBoolean(object);
		} catch (Exception e) {
			e.printStackTrace();
			throw new NoSuchFieldException("unable to access field [" + fieldName + "]: " + e.toString() );
		}
	}

	
	/**
	 * Retrieves the value of the specified String-field of the given object.
	 * 
	 * @param object the object that holds the field
	 * @param fieldName the name of the field
	 * @return the field value
	 * @throws NoSuchFieldException when the field does not exist
	 */
	public static String getStringField(Object object, String fieldName) 
	throws NoSuchFieldException 
	{
		Field field = getField( object, fieldName );
		try {
			return (String) field.get(object);
		} catch (Exception e) {
			e.printStackTrace();
			throw new NoSuchFieldException("unable to access field [" + fieldName + "]: " + e.toString() );
		}
	}
	
	/**
	 * Retrieves the specified field of the given object.
	 * 
	 * @param object the object that holds the field
	 * @param fieldName the name of the field
	 * @return the field
	 * @throws NoSuchFieldException when the field does not exist
	 */
	public static Field getField( Object object, String fieldName ) 
	throws NoSuchFieldException
	{
		return getField( object.getClass(), fieldName );
	}
	
	/**
	 * Retrieves the specified field of the given object.
	 * 
	 * @param instanceClass the class that contains the field
	 * @param fieldName the name of the field
	 * @return the field
	 * @throws NoSuchFieldException when the field does not exist
	 */
	public static Field getField( Class instanceClass, String fieldName ) 
	throws NoSuchFieldException
	{
		try {
			Field field = null;
			while (field == null) {
				try {
					field = instanceClass.getDeclaredField( fieldName );
				} catch (NoSuchFieldException e) {
					instanceClass = instanceClass.getSuperclass();
					if (instanceClass == null) {
						throw e;
					}
					//System.out.println("trying parent class [" + instanceClass.getName() + "]");
				}
			}
			field.setAccessible(true);
			return field;
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new NoSuchFieldException( "Unable to access field [" + fieldName + "]: " + e.toString() );
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new NoSuchFieldException( "Unable to access field [" + fieldName + "]: " + e.toString() );
		}
	}

	/**
	 * Sets a field value for the given object.
	 *  
	 * @param object the object that should be changed
	 * @param fieldName the name of the field
	 * @param value the value
	 * @throws NoSuchFieldException when the field does not exist or could not be written
	 */
	public static void setField(Object object, String fieldName, int value)
	throws NoSuchFieldException
	{
		setField( object, fieldName, new Integer( value ));
	}
	
	/**
	 * Sets a field value for the given object.
	 *  
	 * @param object the object that should be changed
	 * @param fieldName the name of the field
	 * @param value the value
	 * @throws NoSuchFieldException when the field does not exist or could not be written
	 */
	public static void setField(Object object, String fieldName, Object value)
	throws NoSuchFieldException
	{
		try {
			Field field = null;
			Class instanceClass = object.getClass();
			while (field == null) {
				try {
					field = instanceClass.getDeclaredField( fieldName );
				} catch (NoSuchFieldException e) {
					instanceClass = instanceClass.getSuperclass();
					if (instanceClass == null) {
						throw e;
					}
					//System.out.println("trying parent class [" + instanceClass.getName() + "]");
				}
			}
			field.setAccessible(true);
			field.set(object, value);
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new NoSuchFieldException( "Unable to set field [" + fieldName + "]: " + e.toString() );
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new NoSuchFieldException( "Unable to set field [" + fieldName + "]: " + e.toString() );
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new NoSuchFieldException( "Unable to set field [" + fieldName + "]: " + e.toString() );
		}
	}
	
	/**
	 * Sets a field value for the given object.
	 *  
	 * @param fieldClass the class that should be changed
	 * @param fieldName the name of the field
	 * @param value the value
	 * @throws NoSuchFieldException when the field does not exist or could not be written
	 */
	public static void setStaticField(Class fieldClass, String fieldName, Object value)
	throws NoSuchFieldException
	{
		try {
			Field field = null;
			while (field == null) {
				try {
					field = fieldClass.getDeclaredField( fieldName );
				} catch (NoSuchFieldException e) {
					fieldClass = fieldClass.getSuperclass();
					if (fieldClass == null) {
						throw e;
					}
					//System.out.println("trying parent class [" + instanceClass.getName() + "]");
				}
			}
			field.setAccessible(true);
			field.set( null, value );
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new NoSuchFieldException( "Unable to set field [" + fieldName + "]: " + e.toString() );
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new NoSuchFieldException( "Unable to set field [" + fieldName + "]: " + e.toString() );
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new NoSuchFieldException( "Unable to set field [" + fieldName + "]: " + e.toString() );
		}
	}

	
	public static Object callMethod( Object object, String methodName, int value ) 
	throws NoSuchMethodException 
	{
		Class instanceClass = object.getClass();
		Method method = null;
		while (method == null) {
			try {
				method = instanceClass.getDeclaredMethod( methodName, new Class[]{ Integer.TYPE } );
			} catch (NoSuchMethodException e) {
				instanceClass = instanceClass.getSuperclass();
				if (instanceClass == null) {
					throw e;
				}
			}
		}
        method.setAccessible(true);
		try {
			return method.invoke(object, new Object[]{ new Integer( value ) } );
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new NoSuchMethodException( e.toString() );
		} catch (InvocationTargetException e) {
			System.out.println("PopulateUtil: unable to call method " + methodName + "( " + value + ")");
			e.printStackTrace();
			throw new RuntimeException( e.getCause() );
		}
	}

	public static Object callMethod(String methodName, Object object, Class[] signature, Object[] values) 
	throws NoSuchMethodException 
	{
		Class instanceClass = object.getClass();
		Method method = null;
		while (method == null) {
			try {
				method = instanceClass.getDeclaredMethod( methodName, signature );
			} catch (NoSuchMethodException e) {
				instanceClass = instanceClass.getSuperclass();
				if (instanceClass == null) {
					throw e;
				}
			}
		}
        method.setAccessible(true);
		try {
			return method.invoke(object, values );
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new NoSuchMethodException( e.toString() );
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException( e.getCause() );
		}
	}

	public static Object callMethod(String methodName, Object object) 
	throws NoSuchMethodException 
	{
		Class instanceClass = object.getClass();
		Method method = null;
		while (method == null) {
			try {
				method = instanceClass.getDeclaredMethod( methodName, new Class[0] );
			} catch (NoSuchMethodException e) {
				instanceClass = instanceClass.getSuperclass();
				if (instanceClass == null) {
					throw e;
				}
			}
		}
        method.setAccessible(true);
		try {
			return method.invoke(object, new Object[0] );
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new NoSuchMethodException( e.toString() );
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException( e.getCause() );
		}
	}

	/**
	 * Searches for the correct constructor and instantiates a new object with the given parameters.
	 * @param constructorClass the class that contains the constructor
	 * @param parameters the parameters for the constructor
	 * @return a new instance created with the correct constructor
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 */
	public static Object newInstance(Class constructorClass, Object[] parameters) 
	throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException 
	{
		if (parameters == null || parameters.length == 0) {
			return constructorClass.newInstance();
		}
		Constructor[] constructors = constructorClass.getConstructors();
		for (int i = 0; i < constructors.length; i++) {
			Constructor constructor = constructors[i];
			if ( constructorFits( constructor, parameters ) ) {
				return constructor.newInstance(parameters);
			}
		}
		throw new IllegalArgumentException("Found no matching constructor in class " + constructorClass.getName() + " for parameters " + parameters.length );
	}

	/**
	 * @param constructor
	 * @param parameters
	 * @return true when the contructor fits
	 */
	public static boolean constructorFits(Constructor constructor, Object[] parameters) {
		return signatureFits( constructor.getParameterTypes(), parameters );
	}

	/**
	 * @param parameterTypes
	 * @param parameters
	 * @return true when the signature fits
	 */
	private static boolean signatureFits(Class[] parameterTypes, Object[] parameters) {
		if ((parameterTypes == null && parameters != null) || (parameterTypes != null && parameters == null)) {
			return false;
		}
 		if (parameterTypes != null && parameters != null && parameterTypes.length != parameters.length) {
			return false;
		}
		if (parameterTypes == null && parameters == null) {
			return true;
		}
		for (int i = 0; i < parameters.length; i++) {
			Object parameter = parameters[i];
			Class parameterType = parameterTypes[i];
			if (parameter == null) {
				if (parameterType.isPrimitive()) {
					return false;
				}
				continue; // null fits any class
			}
			Class parameterClass = parameter.getClass();
			if (! (parameterType.isAssignableFrom(parameterClass) || (parameterType.isPrimitive() && isCompatible( parameterType, parameterClass )) )) {
//				System.out.println("- signature-mismatch: wanted=" + parameterType + ", have=" + parameter.getClass() );
				return false;
//			} else {
//				System.out.println("+ signature-match: wanted=" + parameterType + ", have=" + parameter.getClass() );
			}
		}
		return true;
	}
	
	/**
	 * @param parameterType
	 * @param parameterClass
	 * @return
	 */
	private static boolean isCompatible(Class parameterType, Class parameterClass) {
		Class wrapperClass = (Class) PRIMITIVE_CLASS_WRAPPERS.get(parameterType);
		if (wrapperClass != null) {
			return wrapperClass.isAssignableFrom(parameterClass);
		}
		return false;
	}


	/**
	 * @param object
	 * @param fieldName the name of the field
	 * @return the value of the field
	 * @throws NoSuchFieldException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public static Object getFieldValue(Object object, String fieldName) 
	throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException 
	{
		Field field = getField( object, fieldName );
		return field.get(object);
	}

	/**
	 * @param parameterClass
	 * @param fieldName
	 * @return the value of the static field
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws NoSuchFieldException 
	 */
	public static Object getStaticFieldValue(Class parameterClass, String fieldName) 
	throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException 
	{
		Field field = getField(parameterClass, fieldName);
		return field.get(null);
	}

}
