/*
 * Created on Nov 8, 2008 at 6:08:13 PM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package de.enough.polish.rmi;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.enough.polish.io.Externalizable;
import de.enough.polish.io.Serializer;

/**
 * <p>Encapsulates the reading of method parameters, the writing and the invocation.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class RmiResolver
{
	
	/** The object that implements a specific Remote interface, which can actually be this servlet (when a user choses to extend this servlet). */
	protected Remote implementation;
	/** A map used converting primitive classes into the appropriate primite TYPE types */
	protected static final Map PRIMITIVES_TYPES_MAP = new HashMap();
	static {
		PRIMITIVES_TYPES_MAP.put( Byte.class, Byte.TYPE );
		PRIMITIVES_TYPES_MAP.put( Short.class, Short.TYPE );
		PRIMITIVES_TYPES_MAP.put( Integer.class, Integer.TYPE );
		PRIMITIVES_TYPES_MAP.put( Long.class, Long.TYPE );
		PRIMITIVES_TYPES_MAP.put( Float.class, Float.TYPE );
		PRIMITIVES_TYPES_MAP.put( Double.class, Double.TYPE );
		PRIMITIVES_TYPES_MAP.put( Boolean.class, Boolean.TYPE );
		PRIMITIVES_TYPES_MAP.put( Character.class, Character.TYPE );
	}

	public RmiResolver( Remote implementation ) {
		this.implementation = implementation;
	}

	/**
	 * Processes the actual method request.
	 * 
	 * @param in the input data stream that should contain the method invocation data
	 * @param out the output stream to which the results of the method call are written
	 * @throws IOException when data could not be read or written
	 */
	public void process( DataInputStream in, DataOutputStream out )
	throws IOException
	{
		process( in, out, true );
	}
	
	/**
	 * Processes the actual method request.
	 * 
	 * @param in the input data stream that should contain the method invocation data
	 * @param out the output stream to which the results of the method call are written
	 * @param closeStreams true when the given streams should be closed after processing
	 * @throws IOException when data could not be read or written
	 */
	public void process( DataInputStream in, DataOutputStream out, boolean closeStreams )
	throws IOException
	{
		boolean useObfuscation = true;

		try {
			int version;

			try {
				version = in.readInt();
			}
			catch (EOFException e) {
				// No data send. There is basically no RMI call.
				return;
			}

			if (version > 101) {
				useObfuscation = in.readBoolean();
			} else if (version < 100) {
				throw new RemoteException("Unsupported version: " + version);
			}
			String methodName = in.readUTF();
			long primitivesFlag = 0;
			if (version > 100) {
				primitivesFlag = in.readLong();
			}
			Object[] parameters = (Object[]) Serializer.deserialize(in);
			Class[] signature = null;
			int flag = 1;
			boolean hasNullParameter = false;
			if (parameters != null) {
				signature = new Class[ parameters.length ];
				for (int i = 0; i < parameters.length; i++) {
					Object param = parameters[i];
					// check for primitive wrapper:
					//System.out.println("primitiveFlag=" + primitivesFlag + ", flag=" + flag + ", (primitiveFlag & flag)=" + (primitivesFlag & flag) );
					if (param == null) {
						signature[i] = null;
						hasNullParameter = true;
					} else if ( (primitivesFlag & flag) == 0) {
						// this is a normal class, not a primitive:
						signature[i] = param.getClass();
					} else {
						// this is a primitive
						Class primitiveType = (Class) PRIMITIVES_TYPES_MAP.get( param.getClass() );
						if (primitiveType == null) {
							throw new RemoteException("Invalid primitives flag, please report this error to j2mepolish@enough.de.");
						}
						//System.out.println("using primitive type " + primitiveType.getName() );
						signature[i] = primitiveType;
					}
					flag <<= 1;
				}
			}
			Method method = lookupMethod( methodName, signature, hasNullParameter ); 
				
			Object returnValue = method.invoke(this.implementation, parameters); // for void methods null is returned...
			out.writeInt( Remote.STATUS_OK );
			Serializer.serialize(returnValue, out, useObfuscation);
			out.flush();
		} catch (SecurityException e) {
			e.printStackTrace();
			processRemoteException( e, out, useObfuscation );
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			processRemoteException( e, out, useObfuscation );
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			processRemoteException( e, out, useObfuscation );
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			processRemoteException( e, out, useObfuscation );
		} catch (InvocationTargetException e) {
			System.out.println("InvocationTargetException, cause=" + e.getCause() );
			processRemoteException( e, out, useObfuscation );
		} catch (RemoteException e) {
			e.printStackTrace();
			processRemoteException( e, out, useObfuscation );
		} catch (Exception e) {
			e.printStackTrace();
			processRemoteException( e, out, useObfuscation );
		} finally {
			if (closeStreams && in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (closeStreams && out != null) {
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected Method lookupMethod(String methodName, Class[] signature,
			boolean hasNullParameter) throws SecurityException, NoSuchMethodException 
	{
		if (!hasNullParameter) {
			try {
				return this.implementation.getClass().getMethod( methodName, signature );
			} catch (NoSuchMethodException e) {
				if (signature == null) {
					throw e;
				}
				// when there is a signature, this can happen when the method accepts generic types and the caller
				// provides specific subclasses. Continue below...
			}
		}
		Method[] methods = this.implementation.getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if (method.getName().equals(methodName)) {
				Class[] parameterTypes = method.getParameterTypes();
				if ( parameterTypes.length == signature.length) {
					boolean foundMatch = true;
					for (int j = 0; j < signature.length; j++) {
						Class signatureClass = signature[j];
						if (signatureClass == null) {
							continue;
						}
						Class parameterClass = parameterTypes[j];
						if (!parameterClass.isAssignableFrom(signatureClass)) {
							foundMatch = false;
							break;
						}
					}
					if (foundMatch) {
						return method;
					}
				}
			}
		}
		throw new NoSuchMethodException("method not found: name=" + methodName  + ", signature=" + (signature == null ? null : Arrays.toString(signature)) );
	}



	/**
	 * Processes an exception which is thrown by the method or while accessing the method.
	 * @param e the exception
	 * @param out the stream to which the exception is written as a result
	 * @param useObfuscation true when obfuscation should be used
	 * @throws IOException when data could not be written
	 */
	protected void processRemoteException(Throwable e, DataOutputStream out, boolean useObfuscation)
	throws IOException
	{
		Throwable cause = e.getCause();
		if (cause instanceof Externalizable) {
			out.writeInt( Remote.STATUS_CHECKED_EXCEPTION );
			Serializer.serialize( cause, out, useObfuscation );
		} else if (cause != null) {
			out.writeInt( Remote.STATUS_UNCHECKED_EXCEPTION );
			out.writeUTF( cause.toString() );
		} else {
			out.writeInt( Remote.STATUS_UNCHECKED_EXCEPTION );
			out.writeUTF( e.toString() );
		}
	}
}
