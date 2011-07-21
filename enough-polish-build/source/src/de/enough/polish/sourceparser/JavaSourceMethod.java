/*
 * Created on Dec 29, 2006 at 2:34:44 AM.
 * 
 * Copyright (c) 2006 Robert Virkus / Enough Software
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
package de.enough.polish.sourceparser;

import java.util.List;

/**
 * <p>Represents a method which is availabe in the source code.</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        Dec 29, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class JavaSourceMethod {
	private String modifier;
	private String returnType;
	private String name;
	private String[] parameterTypes;
	private String[] parameterNames;
	private String[] thrownExceptions;
	private String[] methodCode;
	private final JavaSourceClass parentClass;
	
	/**
	 * @param parentClass the class/interface that contains this method. 
	 * @param modifier 
	 * @param returnType
	 * @param name
	 * @param parameterNames
	 * @param parameterTypes
	 * @param thrownExceptions
	 */
	public JavaSourceMethod( JavaSourceClass parentClass, String modifier, String returnType, String name, String[] parameterTypes, String[] parameterNames, String[] thrownExceptions) {
		super();
		this.parentClass = parentClass;
		this.modifier = modifier;
		this.returnType = returnType;
		this.name = name;
		this.parameterTypes = parameterTypes;
		this.parameterNames = parameterNames;
		this.thrownExceptions = thrownExceptions;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	/**
	 * @return the parameterNames
	 */
	public String[] getParameterNames() {
		return this.parameterNames;
	}
	/**
	 * @return the parameterTypes
	 */
	public String[] getParameterTypes() {
		return this.parameterTypes;
	}
	/**
	 * @return the returnType
	 */
	public String getReturnType() {
		return this.returnType;
	}
	/**
	 * @return the thrownExceptions
	 */
	public String[] getThrownExceptions() {
		return this.thrownExceptions;
	}
	/**
	 * @return the modifier
	 */
	public String getModifier() {
		return this.modifier;
	}
	/**
	 * @return the methodCode
	 */
	public String[] getMethodCode() {
		return this.methodCode;
	}
	/**
	 * @param methodCode the methodCode to set
	 */
	public void setMethodCode(String[] methodCode) {
		this.methodCode = methodCode;
	}
	/**
	 * @param modifier the modifier to set
	 */
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @param parameterNames the parameterNames to set
	 */
	public void setParameterNames(String[] parameterNames) {
		this.parameterNames = parameterNames;
	}
	/**
	 * @param parameterTypes the parameterTypes to set
	 */
	public void setParameterTypes(String[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}
	/**
	 * @param returnType the returnType to set
	 */
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	/**
	 * @param thrownExceptions the thrownExceptions to set
	 */
	public void setThrownExceptions(String[] thrownExceptions) {
		this.thrownExceptions = thrownExceptions;
	}
	
	/**
	 * Writes the complete method code into the specified code list.
	 *  
	 * @param code the code to which the method code should be added
	 * @param isClass true when the parent of this method is a class
	 */
	public void renderCode( List code, boolean isClass ) {
		StringBuffer buffer = new StringBuffer();
		buffer.append( this.modifier ).append(' ');
		if (isClass && this.methodCode == null) {
			buffer.append("abstract ");
		}
		buffer.append( this.returnType ).append(' ')
			.append( this.name ).append("( ");
		if (this.parameterNames != null) {
			for (int i = 0; i < this.parameterNames.length; i++) {
				String pType = this.parameterTypes[i];
				String pName = this.parameterNames[i];
				buffer.append( pType ).append(' ').append( pName );
				if (i != this.parameterNames.length - 1) {
					buffer.append(", ");
				}
			}
		}
		buffer.append(" ) ");
		if (this.thrownExceptions != null && this.thrownExceptions.length != 0) {
			buffer.append(" throws ");
			for (int i = 0; i < this.thrownExceptions.length; i++) {
				String exception = this.thrownExceptions[i];
				buffer.append( exception );
				if (i != this.thrownExceptions.length - 1) {
					buffer.append(", ");
				}
			}
		}
		if (this.methodCode == null) {
			buffer.append(';');
			code.add( buffer.toString() );
			return;
		} 
		// add method implementation code:
		buffer.append('{');
		code.add( buffer.toString() );
		for (int i = 0; i < this.methodCode.length; i++) {
			String line = this.methodCode[i];
			code.add( line );
		}
		code.add("}");
		
	}
	/**
	 * Checks whether the given exception is thrown by this method.
	 * 
	 * @param exceptionName the name of the exception
	 * @return true when this exception is thrown by this method
	 */
	public boolean throwsException(String exceptionName) {
		if (this.thrownExceptions == null) {
			return false;
		}
		for (int i = 0; i < this.thrownExceptions.length; i++) {
			String thrownException = this.thrownExceptions[i];
			if (thrownException.equals( exceptionName )) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Retrieves the class or interface to which the method belongs.
	 * @return the parent class/interface, can be null 
	 */
	public JavaSourceClass getSourceClass() {
		return this.parentClass;
	}
	
}
