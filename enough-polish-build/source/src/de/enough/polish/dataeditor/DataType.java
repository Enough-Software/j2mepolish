/*
 * Created on 18-Oct-2004 at 23:01:01.
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
package de.enough.polish.dataeditor;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.jdom.Element;

import de.enough.polish.util.CastUtil;
import de.enough.polish.util.StringUtil;

/**
 * <p>Represents a data-type. A type can be user defined and consist of other types.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        18-Oct-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DataType {
	
	public static final int BYTE_ID = 1;
	public static final int UNSIGNED_BYTE_ID = 2;
	public static final int SHORT_ID = 4;
	public static final int UNSIGNED_SHORT_ID = 5;
	public static final int INTEGER_ID = 6;
	public static final int LONG_ID = 7;
	public static final int ASCII_STRING_ID = 8;
	public static final int UTF_STRING_ID = 9;
	public static final int BOOLEAN_ID = 10;
	public static final int PNG_IMAGE_ID = 11;
	public static final int USER_DEFINED_ID = 12;

	public static final DataType BYTE = new DataType("byte", BYTE_ID, 1);
	public static final DataType UNSIGNED_BYTE = new DataType("unsigned byte", UNSIGNED_BYTE_ID, 1);
	public static final DataType SHORT = new DataType("short", SHORT_ID, 2);
	public static final DataType UNSIGNED_SHORT = new DataType("unsigned short", UNSIGNED_SHORT_ID, 2);
	public static final DataType INTEGER = new DataType("int", INTEGER_ID, 4);
	public static final DataType LONG = new DataType("long", LONG_ID, 8);
	public static final DataType ASCII_STRING = new DataType("ASCII-String", ASCII_STRING_ID, -1);
	public static final DataType UTF_STRING = new DataType("UTF-String", UTF_STRING_ID, -1);
	public static final DataType BOOLEAN = new DataType("boolean", BOOLEAN_ID, 1);
	public static final DataType PNG_IMAGE = new DataType("PNG-Image", PNG_IMAGE_ID, -1);


	private final String name;
	private final int type;
	private final DataType[] subtypes;
	private final int numberOfBytes;
	private final boolean isDynamic;

	/**
	 * Creates a new simple type.
	 * 
	 * @param name the name of this type
	 * @param type the type-ID
	 * @param numberOfBytes the number of bytes which are needed to store this type, -1 if undefined
	 */
	public DataType( String name, int type, int numberOfBytes ) {
		super();
		this.type = type;
		this.numberOfBytes = numberOfBytes;
		this.subtypes = null;
		this.name = name;
		this.isDynamic = numberOfBytes == -1;
	}

	/**
	 * Creates a new user defined type.
	 * 
	 * @param name the name of this type
	 * @param subtypes the subtypes of this user defined type.
	 */
	public DataType( String name, DataType[] subtypes ) {
		super();
		this.type = USER_DEFINED_ID;
		this.subtypes = subtypes;
		int bytes = 0;
		boolean dynamic = false;
		for (int i = 0; i < subtypes.length; i++) {
			DataType subtype = subtypes[i];
			bytes += subtype.numberOfBytes;
			if (subtype.isDynamic) {
				dynamic = true;
			}
		}
		this.name = name;
		this.isDynamic = dynamic;
		if (dynamic) {
			this.numberOfBytes = -1; 
		} else {
			this.numberOfBytes = bytes;	
		}
	}
	
	/**
	 * @param typeElement
	 * @param dataManager
	 */
	public DataType(Element typeElement, DataManager dataManager) {
		this.type = USER_DEFINED_ID;
		this.name = typeElement.getAttributeValue("name");
		List subtypesList = typeElement.getChildren("subtype");
		int bytes = 0;
		this.subtypes = new DataType[ subtypesList.size() ];
		boolean dynamic = false;
		int index = 0;
		for (Iterator iter = subtypesList.iterator(); iter.hasNext();) {
			Element subtypeElement = (Element) iter.next();
			String subtypeName = subtypeElement.getAttributeValue("type");
			DataType subtype = dataManager.getDataType( subtypeName );
			if (subtype == null) {
				throw new IllegalArgumentException("Unable to init data-type [" + this.name + "]: the subtype [" + subtypeName + "] is not known (yet).");
			}
			this.subtypes[index] = subtype;
			bytes += subtype.numberOfBytes;
			index++;
			if (subtype.isDynamic) {
				dynamic = true;
			}
		}
		this.isDynamic = dynamic;
		if (dynamic) {
			this.numberOfBytes = -1; 
		} else {
			this.numberOfBytes = bytes;	
		}
	}

	public int getType() {
		return this.type;
	}
	
	public DataType[] getSubtypes() {
		return this.subtypes;
	}
	
	public boolean isUserDefined() {
		return this.type == USER_DEFINED_ID;
	}
	
	public int getNumberOfBytes() {
		return this.numberOfBytes;
	}
	
	public Object getDefaultValue() {
		switch (this.type) {
			case BYTE_ID: 
				return new Byte( (byte)0 );
			case UNSIGNED_BYTE_ID:
				return new Short( (short)0 );
			case SHORT_ID:
				return new Short( (short)0 );
			case UNSIGNED_SHORT_ID:
				return new Integer( 0 );
			case INTEGER_ID:
				return new Integer( 0 );
			case LONG_ID:
				return new Long( 0 );
			case BOOLEAN_ID:
				return Boolean.FALSE;
			case ASCII_STRING_ID:
				return "";
			case UTF_STRING_ID:
				return "";
			case PNG_IMAGE_ID:
				return "Embedded PNG Image (should be last entry for MIDP/1.0 devices)";
			case USER_DEFINED_ID:
				Object[] results = new Object[ this.subtypes.length ];
				for (int i = 0; i < this.subtypes.length; i++) {
					DataType dataType = this.subtypes[i];
					results[i] = dataType.getDefaultValue();
				}
				return results;
			default: 
				throw new IllegalStateException( "The type [" + this.name + "] is currently not supported.");
		}
	}
	
	public String toString( Object data ) {
		switch (this.type) {
			case USER_DEFINED_ID:
				StringBuffer buffer = new StringBuffer();
				Object[] datas = (Object[]) data;
				for (int i = 0; i < this.subtypes.length; i++) {
					DataType subtype = this.subtypes[i];
					Object subData = datas[i];
					buffer.append( subtype.toString( subData ) );
					if (i != this.subtypes.length - 1) {
						buffer.append(", ");
					}
				}
				return buffer.toString();
			case PNG_IMAGE_ID: 
				return getDefaultValue().toString();
			default: 
				return data.toString();
		}
	}
	
	public Object parseDataString( String value ) {
		switch (this.type) {
			case BYTE_ID: 
				return new Byte( Byte.parseByte( value ) );
			case UNSIGNED_BYTE_ID:
				short unsignedByteValue = Short.parseShort(value);
				if (unsignedByteValue > 255 || unsignedByteValue < 0) {
					throw new IllegalArgumentException("Invalid value for unsigned byte: " + value + ". Allowed range is 0 to 255.");
				} 
				return new Short( unsignedByteValue );
			case SHORT_ID:
				return new Short( Short.parseShort(value) );
			case UNSIGNED_SHORT_ID:
				int unsignedShortValue = Integer.parseInt(value);
				if (unsignedShortValue < 0 || unsignedShortValue > Short.MAX_VALUE + (-1* Short.MIN_VALUE)) {
					throw new IllegalArgumentException("Invalid value for unsigned short: " + value + ". Allowed range is 0 to " + (Short.MAX_VALUE + (-1* Short.MIN_VALUE)) + ".");
				}
				return new Integer( unsignedShortValue );
			case INTEGER_ID:
				return new Integer( Integer.parseInt(value) );
			case LONG_ID:
				return new Long( Long.parseLong(value) );
			case BOOLEAN_ID:
				if ("true".equals( value )) {
					return Boolean.TRUE;
				} else {
					return Boolean.FALSE;
				}
			case ASCII_STRING_ID:
				if (value.length() > 255) {
					throw new IllegalArgumentException("Max length of an ASCII String is 255 chars - the given value has " + value.length() + " chars.");
				}
				return value;
			case UTF_STRING_ID:
				return value;
			case USER_DEFINED_ID:
				String[] subvalues = StringUtil.split( value, ", ");
				if (subvalues.length != this.subtypes.length) {
					throw new IllegalArgumentException("Invalid count of subtypes.");
				} 
				Object[] results = new Object[ subvalues.length ];
				for (int i = 0; i < this.subtypes.length; i++) {
					DataType dataType = this.subtypes[i];
					String subvalue = subvalues[i];
					results[i] = dataType.parseDataString( subvalue );
				}
				return results;
			default: 
				throw new IllegalStateException( "The type [" + this.name + "] is currently not supported.");
		}
	}

	/**
	 * Retrieves the name of this <code>DataType</code>.
	 * 
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Retrieves the INT representation for a given object.
	 * 
	 * @param data the data object to represent
	 * @return the INT representation
	 */
	public int getIntRepresentation(Object data) {
		switch (this.type) {
			case BYTE_ID:
				return ((Byte)data).intValue();
			case UNSIGNED_BYTE_ID:
				return ((Short)data).intValue();
			case SHORT_ID:
				return ((Short)data).intValue();
			case UNSIGNED_SHORT_ID:
				return ((Integer)data).intValue();
			case INTEGER_ID:
				return ((Integer)data).intValue();
			case LONG_ID:
				return ((Long)data).intValue();
			case BOOLEAN_ID:
				if (data == Boolean.FALSE) {
					return 0;
				} else {
					return 1;
				}
			case ASCII_STRING_ID:
				return ((String)data).length();
			case UTF_STRING_ID:
				return ((String)data).length();
			default: 
				throw new IllegalStateException( "The data-type [" + this.name + "] cannot be used for calculations.");
		}	
	}
	
	/**
	 * Retrieves the XML representation for this <code>DataEntry</code>. This is useful
	 * e.g. for saving it into a file.
	 * 
	 * @return the XML representation
	 */
	public String getXmlRepresentation() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\t<type name=\"")
			.append( this.name )
			.append( "\">");
		for (int i = 0; i < this.subtypes.length; i++) {
			DataType subtype = this.subtypes[i];
			buffer.append("<subtype type=\"")
				.append( subtype.getName() )
				.append( "\" />");
		}
		buffer.append("</type>");
		return buffer.toString();
	}
	
	public String toString() {
		return this.name;
	}
	
	public static DataType[] getDefaultTypes() {
		return new DataType[]{ BYTE, UNSIGNED_BYTE, SHORT, UNSIGNED_SHORT, INTEGER, LONG, BOOLEAN, ASCII_STRING, UTF_STRING, PNG_IMAGE };
	}
	
	/**
	 * Determines when this type has a dynamic (i.e. -1) length
	 * @return true when this type is dynamic 
	 */
	public boolean isDynamic() {
		return this.isDynamic;
	}
	
	public String getJavaType() {
		switch (this.type) {
			case BYTE_ID:
				return "byte";
			case UNSIGNED_BYTE_ID:
				return "short";
			case SHORT_ID:
				return "short";
			case UNSIGNED_SHORT_ID:
				return "int";
			case INTEGER_ID:
				return "int";
			case LONG_ID:
				return "long";
			case BOOLEAN_ID:
				return "boolean";
			case ASCII_STRING_ID:
				return "String";
			case UTF_STRING_ID:
				return "String";
			case PNG_IMAGE_ID:
				return "javax.microedition.lcdui.Image";
			case USER_DEFINED_ID:
				return this.name;
			default: 
				throw new IllegalStateException( "The type [" + this.name + "] is currently not supported.");
		}
	}
	
	public void addInstanceDeclaration( String count, String paramName, StringBuffer buffer ) {
		buffer.append("\tpublic final ");
		buffer.append( getJavaType() );
		if ( !"1".equals(count) ) {
			buffer.append("[]");
		}
		buffer.append(' ').append( paramName ).append(";\n");
	}
	
	/**
	 * Adds the Java code to load this type.
	 * The input-streams name is "in".
	 * 
	 * @param count the number of instances which should be loaded, can either
	 *  			be a number or a term like "rows * cells".
	 * @param paramName the name of the parameter
	 * @param buffer the string buffer to which the code should be added.
	 */
	public void addCode( String count, String paramName, StringBuffer buffer ) {
		boolean isArray = !("1".equals( count ));
		switch (this.type) {
			case BYTE_ID:
				if (isArray) {
					buffer.append( "\t\tthis." ).append( paramName );
					buffer.append(" = new byte[ ").append( count ).append(" ];\n");
					buffer.append("\t\tfor (int i = 0; i < ").append( count ).append("; i++) {\n");
					buffer.append("\t\t\tthis.").append(paramName).append("[i] = in.readByte();\n");
					buffer.append("\t\t}\n");
				} else {
					buffer.append( "\t\tthis." ).append( paramName );
					buffer.append(" = in.readByte();\n");
				}
				break;
			case UNSIGNED_BYTE_ID:
				if (isArray) {
					buffer.append( "\t\tthis." ).append( paramName );
					buffer.append(" = new short[ ").append( count ).append(" ];\n");
					buffer.append("\t\tfor (int i = 0; i < ").append( count ).append("; i++) {\n");
					buffer.append("\t\t\tthis.").append(paramName).append("[i] = (short) in.readUnsignedByte();\n");
					buffer.append("\t\t}\n");
				} else {
					buffer.append( "\t\tthis." ).append( paramName );
					buffer.append(" = (short) in.readUnsignedByte();\n");
				}
				break;
			case SHORT_ID:
				if (isArray) {
					buffer.append( "\t\tthis." ).append( paramName );
					buffer.append(" = new short[ ").append( count ).append(" ];\n");
					buffer.append("\t\tfor (int i = 0; i < ").append( count ).append("; i++) {\n");
					buffer.append("\t\t\tthis.").append(paramName).append("[i] = in.readShort();\n");
					buffer.append("\t\t}\n");
				} else {
					buffer.append( "\t\tthis." ).append( paramName );
					buffer.append(" = in.readShort();\n");
				}
				break;
			case UNSIGNED_SHORT_ID:
				if (isArray) {
					buffer.append( "\t\tthis." ).append( paramName );
					buffer.append(" = new int[ ").append( count ).append(" ];\n");
					buffer.append("\t\tfor (int i = 0; i < ").append( count ).append("; i++) {\n");
					buffer.append("\t\t\tthis.").append(paramName).append("[i] = in.readUnsignedShort();\n");
					buffer.append("\t\t}\n");
				} else {
					buffer.append( "\t\tthis." ).append( paramName );
					buffer.append(" = in.readUnsignedShort();\n");
				}
				break;
			case INTEGER_ID:
				if (isArray) {
					buffer.append( "\t\tthis." ).append( paramName );
					buffer.append(" = new int[ ").append( count ).append(" ];\n");
					buffer.append("\t\tfor (int i = 0; i < ").append( count ).append("; i++) {\n");
					buffer.append("\t\t\tthis.").append(paramName).append("[i] = in.readInt();\n");
					buffer.append("\t\t}\n");
				} else {
					buffer.append( "\t\tthis." ).append( paramName );
					buffer.append(" = in.readInt();\n");
				}
				break;
			case LONG_ID:
				if (isArray) {
					buffer.append( "\t\tthis." ).append( paramName );
					buffer.append(" = new long[ ").append( count ).append(" ];\n");
					buffer.append("\t\tfor (int i = 0; i < ").append( count ).append("; i++) {\n");
					buffer.append("\t\t\tthis.").append(paramName).append("[i] = in.readLong();\n");
					buffer.append("\t\t}\n");
				} else {
					buffer.append( "\t\tthis." ).append( paramName );
					buffer.append(" = in.readLong();\n");
				}
				break;
			case BOOLEAN_ID:
				if (isArray) {
					buffer.append( "\t\tthis." ).append( paramName );
					buffer.append(" = new boolean[ ").append( count ).append(" ];\n");
					buffer.append("\t\tfor (int i = 0; i < ").append( count ).append("; i++) {\n");
					buffer.append("\t\t\tthis.").append(paramName).append("[i] = in.readBoolean();\n");
					buffer.append("\t\t}\n");
				} else {
					buffer.append( "\t\tthis." ).append( paramName );
					buffer.append(" = in.readBoolean();\n");
				}
				break;
			case ASCII_STRING_ID:
				if (isArray) {
					buffer.append( "\t\tthis." ).append( paramName );
					buffer.append(" = new String[ ").append( count ).append(" ];\n");
					buffer.append("\t\tfor (int i = 0; i < ").append( count ).append("; i++) {\n");
					buffer.append( "\t\t\tint ").append( paramName ).append("Length = in.read();\n");
					buffer.append( "\t\t\tbyte[] ").append( paramName ).append("Buffer");
					buffer.append( " = new byte[" ).append( paramName ).append("Length ];\n");
					buffer.append("\t\t\tin.read( ").append( paramName ).append("Buffer").append(" );\n");
					buffer.append( "\t\tthis." ).append( paramName ).append("[i]");
					buffer.append(" = new String(" ).append( paramName ).append("Buffer );\n");
					buffer.append("\t\t}\n");
				} else {
					buffer.append( "\t\tint ").append( paramName ).append("Length = in.read();\n");
					buffer.append( "\t\tbyte[] ").append( paramName ).append("Buffer");
					buffer.append( " = new byte[" ).append( paramName ).append("Length ];\n");
					buffer.append("\t\tin.read( ").append( paramName ).append("Buffer").append(" );\n");
					buffer.append( "\t\tthis." ).append( paramName );
					buffer.append(" = new String(" ).append( paramName ).append("Buffer );\n");
				}
				break;
			case UTF_STRING_ID:
				if (isArray) {
					buffer.append( "\t\tthis." ).append( paramName );
					buffer.append(" = new String[ ").append( count ).append(" ];\n");
					buffer.append("\t\tfor (int i = 0; i < ").append( count ).append("; i++) {\n");
					buffer.append("\t\t\tthis.").append(paramName).append("[i] = in.readUTF();\n");
					buffer.append("\t\t}\n");
				} else {
					buffer.append( "\t\tthis." ).append( paramName );
					buffer.append(" = in.readUTF();\n");
				}
				break;
			case PNG_IMAGE_ID:
				if (isArray) {
					buffer.append( "\t\tthis." ).append( paramName );
					buffer.append(" = new javax.microedition.lcdui.Image[ ").append( count ).append(" ];\n");
					buffer.append("\t\tfor (int i = 0; i < ").append( count ).append("; i++) {\n");
					buffer.append("\t\t\tthis.").append(paramName).append("[i] = in.readUTF();\n");
					buffer.append("\t\t//#ifdef polish.midp2\n");
					buffer.append( "\t\t\t//# this." ).append( paramName );
					buffer.append("[i] = javax.microedition.lcdui.Image.createImage( in );\n");
					buffer.append("\t\t//#else\n");
					/*
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					byte[] pngBuffer = new byte[ 3 * 1024 ];
					int read;
					while ( (read = in.read(pngBuffer, 0, pngBuffer.length)) != -1) {
						out.write(pngBuffer, 0, read );
					}
					pngBuffer = out.toByteArray();
					out = null;
					this.fontImage = Image.createImage(pngBuffer, 0, pngBuffer.length);
					*/
					buffer.append( "\t\t\tjava.io.ByteArrayOutputStream " ).append( paramName ).append("Out = new java.io.ByteArrayOutputStream();\n");
					buffer.append( "\t\t\tbyte[] " ).append( paramName ).append("Buffer = new byte[ 3*1024 ];\n");
					buffer.append( "\t\t\tint " ).append( paramName ).append("Read;\n");
					buffer.append( "\t\t\twhile ( (").append( paramName ).append( "Read = in.read( ").append(paramName).append( "Buffer, 0, ").append(paramName).append("Buffer.length) ) != -1 ) {\n");
					buffer.append( "\t\t\t\t").append( paramName ).append( "Out.write( ").append(paramName).append( "Buffer, 0, ").append( paramName ).append( "Read );\n");
					buffer.append( "\t\t\t}\n");
					buffer.append( "\t\t\t" ).append( paramName ).append("Buffer = ").append( paramName ).append("Out.toByteArray();\n");
					buffer.append( "\t\t\tthis." ).append( paramName );
					buffer.append("[i] = javax.microedition.lcdui.Image.createImage( ").append( paramName ).append("Buffer, 0, ").append( paramName ).append("Buffer.length );\n");
					buffer.append("\t\t//#endif\n");
					buffer.append("\t\t}\n");
				} else {
					buffer.append("\t\t//#ifdef polish.midp2\n");
					buffer.append( "\t\t\tthis." ).append( paramName );
					buffer.append(" = javax.microedition.lcdui.Image.createImage( in );\n");
					buffer.append("\t\t//#else\n");
					buffer.append( "\t\t\tjava.io.ByteArrayOutputStream " ).append( paramName ).append("Out = new java.io.ByteArrayOutputStream();\n");
					buffer.append( "\t\t\tbyte[] " ).append( paramName ).append("Buffer = new byte[ 3*1024 ];\n");
					buffer.append( "\t\t\tint " ).append( paramName ).append("Read;\n");
					buffer.append( "\t\t\twhile ( (").append( paramName ).append( "Read = in.read( ").append(paramName).append( "Buffer, 0, ").append(paramName).append("Buffer.length) ) != -1 ) {\n");
					buffer.append( "\t\t\t\t").append( paramName ).append( "Out.write( ").append(paramName).append( "Buffer, 0, ").append( paramName ).append( "Read );\n");
					buffer.append( "\t\t\t}\n");
					buffer.append( "\t\t\t" ).append( paramName ).append("Buffer = ").append( paramName ).append("Out.toByteArray();\n");
					buffer.append( "\t\t\tthis." ).append( paramName );
					buffer.append(" = javax.microedition.lcdui.Image.createImage( ").append( paramName ).append("Buffer, 0, ").append( paramName ).append("Buffer.length );\n");
					buffer.append("\t\t//#endif\n");
				}
				break;
			case USER_DEFINED_ID:
				if (isArray) {
					buffer.append( "\t\tthis." ).append( paramName );
					buffer.append(" = new ").append( this.name ).append("[ ").append( count ).append(" ];\n");
					buffer.append("\t\tfor (int i = 0; i < ").append( count ).append("; i++) {\n");
					buffer.append("\t\t\tthis.").append( paramName ).append("[i] = new ")
						.append( this.name ).append( "( in );\n" );
					buffer.append("\t\t}\n");
				} else {
					buffer.append("\t\tthis.").append( paramName )
						.append( " = new ").append( this.name ).append( "( in );\n" );
				}
				break;
			default: 
				throw new IllegalStateException( "The type [" + this.name + "] is currently not supported.");
		}
	}
	
	public void addInternalClass( Map implementedTypes, StringBuffer buffer ) {
		if (this.type != USER_DEFINED_ID ) {
			// this is not a user-defined type:
			return;
		}
		if ( implementedTypes.get( this.name ) != null ) {
			// the type was implemented already:
			return;
		}
		buffer.append( "public class " ).append( this.name ).append( " {\n ");
		// add field-declarations:
		String paramName = this.name.substring(0,1).toLowerCase() + this.name.substring( 1 );
		for (int i = 0; i < this.subtypes.length; i++) {
			DataType subtype = this.subtypes[ i ];
			subtype.addInstanceDeclaration( "1", paramName + i, buffer );
		}
		// add constructor:
		buffer.append("\tpublic ").append( this.name ).append("( DataInputStream in )\n")
			.append("\tthrows IOException\n\t{\n");
		// add initialisation code:
		for (int i = 0; i < this.subtypes.length; i++) {
			DataType subtype = this.subtypes[ i ];
			subtype.addCode( "1", paramName + i, buffer );
		}
		// close constructor and class:
		buffer.append("\t} // end of constructor \n");
		buffer.append("} // end of inner class\n\n");
		// register this inner class:
		implementedTypes.put( this.name, Boolean.TRUE );
	}

	/**
	 * Loads data for this type from the given input stream
	 * 
	 * @param in the input stream
	 * @return the read type-dependent value
	 * @throws IOException when the data could not be read
	 */
	public Object loadData(DataInputStream in) 
	throws IOException 
	{
		switch (this.type) {
			case BYTE_ID:
				return new Byte( in.readByte() );
			case UNSIGNED_BYTE_ID:
				return new Short( (short) in.readUnsignedByte() );
			case SHORT_ID:
				return new Short( in.readShort() );
			case UNSIGNED_SHORT_ID:
				return new Integer( in.readUnsignedShort() );
			case INTEGER_ID:
				return new Integer( in.readInt() );
			case LONG_ID:
				return new Long( in.readLong() );
			case BOOLEAN_ID:
				if (in.readBoolean()) {
					return Boolean.TRUE;
				} else {
					return Boolean.FALSE;
				}
			case ASCII_STRING_ID:
				int length = CastUtil.toUnsignedInt( in.readByte() );
				byte[] chars = new byte[ length ];
				in.readFully(chars);
				return new String( chars );
			case UTF_STRING_ID:
				return in.readUTF();
			case PNG_IMAGE_ID:
				BufferedImage image = ImageIO.read(in);
				return image;
			case USER_DEFINED_ID:
				Object[] values = new Object[ this.subtypes.length ];
				for (int i = 0; i < values.length; i++) {
					DataType subtype = this.subtypes[i];
					values[i] = subtype.loadData(in);
				}
				return values;
			default: 
				throw new IllegalStateException( "The type [" + this.name + "] is currently not supported.");
		}
	}

	/**
	 * Saves the data for this type.
	 * 
	 * @param value the value
	 * @param out the data output stream
	 * @throws IOException when the data could not be stored
	 */
	public void saveData(Object value, DataOutputStream out) 
	throws IOException
	{
		switch (this.type) {
			case BYTE_ID:
				out.writeByte( ((Byte)value).byteValue() );
				break;
			case UNSIGNED_BYTE_ID:
				out.writeByte( ((Short)value).shortValue() );
				break;
			case SHORT_ID:
				out.writeShort( ((Short)value).shortValue() );
				break;
			case UNSIGNED_SHORT_ID:
				out.writeShort( ((Integer)value).intValue() );
				break;
			case INTEGER_ID:
				out.writeInt( ((Integer)value).intValue() );
				break;
			case LONG_ID:
				out.writeLong( ((Long)value).longValue() );
				break;
			case BOOLEAN_ID:
				out.writeBoolean( ((Boolean)value).booleanValue() );
				break;
			case ASCII_STRING_ID:
				String str = (String) value;
				out.writeByte( str.length() );
				out.writeBytes(str);
				break;
			case UTF_STRING_ID:
				str = (String) value;
				out.writeUTF(str);
				break;
			case PNG_IMAGE_ID:
				BufferedImage image = (BufferedImage) value;
				ImageIO.write(image, "png", out );
				break;
			case USER_DEFINED_ID:
				Object[] values = (Object[]) value;
				for (int i = 0; i < values.length; i++) {
					DataType subtype = this.subtypes[i];
					subtype.saveData(values[i], out);
				}
				break;
			default: 
				throw new IllegalStateException( "The type [" + this.name + "] is currently not supported.");
			}
		}

}
