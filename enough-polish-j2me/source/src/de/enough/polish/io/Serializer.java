/*
 * Created on Mar 31, 2006 at 4:18:12 PM.
 * 
 * Copyright (c) 2010 Robert Virkus / Enough Software
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
package de.enough.polish.io;

//#if polish.JavaSE
	// imports for the Java Standard Edition:
	import java.awt.image.BufferedImage;
    //# import java.lang.reflect.Array;
	import java.lang.reflect.Field;
	import javax.imageio.ImageIO;
	import java.util.Map;
	import java.util.HashMap;
	import java.io.BufferedReader;
//#endif
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Stack;
import java.util.Vector;

//#if polish.midp
	import javax.microedition.lcdui.Command;
	import javax.microedition.lcdui.Font;
//#endif
//#if polish.midp2
import javax.microedition.lcdui.Image;
//#endif


/**
 * <p>The serializer class is used for serializing and de-serializing objects in a unified way.</p>
 * <p>High level serialization components like the RmsStorage use this helper class for the actual serialization.</p>
 *
 * <p>Copyright Enough Software 2006 - 2009</p>
 * <pre>
 * history
 *        Mar 31, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public final class Serializer {
	
	private static final byte VERSION = 1; // version starts at 1 and is then increased up to 127 when incompatible changes occur
	private static final byte TYPE_EXTERNALIZABLE = 0;
	private static final byte TYPE_EXTERNALIZABLE_ARRAY = 1;
	private static final byte TYPE_OBJECT_ARRAY = 2;
	private static final byte TYPE_BYTE = 3;
	private static final byte TYPE_SHORT = 4;
	private static final byte TYPE_INTEGER = 5;
	private static final byte TYPE_LONG = 6;
	private static final byte TYPE_FLOAT = 7;
	private static final byte TYPE_DOUBLE = 8;
	private static final byte TYPE_STRING = 9;
	private static final byte TYPE_STRING_BUFFER = 10;
	private static final byte TYPE_CHARACTER = 11;
	private static final byte TYPE_BOOLEAN = 12;
	private static final byte TYPE_DATE = 13;
	private static final byte TYPE_CALENDAR = 14;
	private static final byte TYPE_RANDOM = 15;
	private static final byte TYPE_HASHTABLE = 16;
	private static final byte TYPE_STACK = 17;
	private static final byte TYPE_VECTOR = 18;
	private static final byte TYPE_IMAGE = 19;
	private static final byte TYPE_IMAGE_RGB = 0;
	private static final byte TYPE_IMAGE_BYTES = 1;
	private static final byte TYPE_FONT = 20;
	private static final byte TYPE_COMMAND = 21;
	private static final byte TYPE_BYTE_ARRAY = 22;
	private static final byte TYPE_SHORT_ARRAY = 23;
	private static final byte TYPE_INT_ARRAY = 24;
	private static final byte TYPE_LONG_ARRAY = 25;
	private static final byte TYPE_FLOAT_ARRAY = 26;
	private static final byte TYPE_DOUBLE_ARRAY = 27;
	private static final byte TYPE_CHAR_ARRAY = 28;
	private static final byte TYPE_BOOLEAN_ARRAY = 29;
	private static final byte TYPE_STRING_ARRAY = 30;
	
	//#if polish.JavaSE
	private static Map obfuscationDeserializeMap; // for translating class names while deserializing/reading data
	private static Map obfuscationSerializeMap; // for translating class names while serializing/writing data
	static {
		try {
			InputStream in = Serializer.class.getResourceAsStream( "/obfuscation-map.txt" );
			if (in == null) {
				in = new java.io.FileInputStream( new java.io.File( ".polishSettings/obfuscation-map.txt") );
			}
			if (in != null) {
				obfuscationDeserializeMap = new HashMap();
				obfuscationSerializeMap = new HashMap();
				BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
				String line;
				while ( (line = reader.readLine()) != null) {
					if (line.length() == 0 || line.charAt(0) == '#' || line.trim().length() == 0) {
						continue;
					}
					int splitPos = line.indexOf('=');
					if (splitPos == -1) {
						continue;
					}
					String fullClassName = line.substring(0, splitPos);
					String obfuscatedClassName = line.substring( splitPos + 1 );
					//System.out.println("full name=" + fullClassName + ", obfuscated=" + obfuscatedClassName);
					obfuscationDeserializeMap.put( obfuscatedClassName, fullClassName );
					obfuscationSerializeMap.put( fullClassName, obfuscatedClassName );
				}
			} else {
				System.out.println("No obfuscation map found.");
			}
		} catch (IOException e) {
			System.out.println("unable to read obfuscation map: " + e);
		}		
	}
	//#endif
	
	private Serializer() {
		// no instantiation allowed
	}
	

	/**
	 * Serializes the specified object.
	 * Any class implementing Serializable can be serialized, additionally classes like java.lang.Integer, java.util.Date, javax.util.Vector,  javax.microedition.lcdui.Image etc. can be serialized.
	 * 
	 * @param object the object
	 * @param out the stream into which the object should be serialized
	 * @throws IOException when serialization data could not be written or when encountering an object that cannot be serialized
	 * @see #deserialize(DataInputStream) for deseralizing objects
	 */
	public static void serialize( Object object, DataOutputStream out ) 
	throws IOException 
	{
		//#if polish.JavaSE
		serialize( object, out, true );
	}
	/**
	 * WARNING: Can only be used in JavaSE environments! Serializes the specified object.
	 * 
	 * @param object the object
	 * @param out the stream into which the object should be serialized
	 * @param useObfuscation true when classnames are obfuscated
	 * @throws IOException when serialization data could not be written or when encountering an object that cannot be serialized
	 */
	public static void serialize( Object object, DataOutputStream out, boolean useObfuscation )
	throws IOException
	{
		//#endif
		out.writeByte( VERSION );
		boolean isNull = (object == null);
		out.writeBoolean( isNull );
		if ( !isNull ) {
			if (object instanceof Externalizable) { 
				out.writeByte(TYPE_EXTERNALIZABLE);
				String className = object.getClass().getName();
				//#if polish.JavaSE
					if (useObfuscation && obfuscationSerializeMap != null) {
						String obfuscatedClassName = (String) obfuscationSerializeMap.get( className );
						if (obfuscatedClassName != null) {
							//System.out.println("Serializer.serialize: translating classname from " + className + " to " +  obfuscatedClassName +  " useObfuscationIndicator=" + useObfuscationIndicator.get() );
							className = obfuscatedClassName;
						}
					}
				//#endif
				//#debug debug
				//#= System.out.println("serializing " + className + "=" + object);
				out.writeUTF( className );
				((Externalizable)object).write(out);
			} else if (object instanceof Externalizable[]) { 
				out.writeByte(TYPE_EXTERNALIZABLE_ARRAY);
				String cn = object.getClass().getName();
				cn = cn.substring(cn.lastIndexOf('[') + 2, cn.length() - 1);
				//#if polish.JavaSE
				  if (useObfuscation && obfuscationSerializeMap != null) {
				    String obfuscatedClassName = (String) obfuscationSerializeMap.get( cn );
				    if (obfuscatedClassName != null) {
				      //System.out.println("Serializer.serialize: translating classname from " + className + " to " +  obfuscatedClassName );
				      cn = obfuscatedClassName;
				    }
				  }
				//#endif
				out.writeUTF(cn);
				Externalizable[] externalizables = (Externalizable[]) object;
				out.writeInt( externalizables.length );
				Hashtable classNames = new Hashtable();
				Class lastClass = null;
				byte lastId = 0;
				byte idCounter = 0;
				for (int i = 0; i < externalizables.length; i++) {
					Externalizable externalizable = externalizables[i];
					Class currentClass = externalizable.getClass();
					if (currentClass == lastClass ) {
						out.writeByte( lastId );
					} else {
						Byte knownId = (Byte) classNames.get( currentClass );
						if (knownId != null) {
							out.writeByte( knownId.byteValue() );
						} else {
							// this is a class that has not yet been encountered:
							out.writeByte( 0 );
							idCounter++;
							String className = currentClass.getName() ;
							//#if polish.JavaSE
								if (useObfuscation && obfuscationSerializeMap != null) {
									String obfuscatedClassName = (String) obfuscationSerializeMap.get( className );
									if (obfuscatedClassName != null) {
										//System.out.println("Serializer.serialize: translating classname from " + className + " to " +  obfuscatedClassName );
										className = obfuscatedClassName;
									}
								}
							//#endif
							//#debug debug
							//#= System.out.println("serializing " + className + "=" + object);
							out.writeUTF( className );
							lastClass = currentClass;
							lastId = idCounter;
							classNames.put( currentClass, new Byte( lastId ) );
						}
					}
					externalizable.write(out);
				}
			} else if (object instanceof Object[]) { 
				out.writeByte(TYPE_OBJECT_ARRAY);
				Object[] objects = (Object[]) object;
				out.writeInt( objects.length );
				for (int i = 0; i < objects.length; i++) {
					Object obj = objects[i];
					serialize(obj, out);
				}
			} else if (object instanceof Byte) {
				out.writeByte(TYPE_BYTE);
				out.writeByte( ((Byte)object).byteValue() );
			} else if (object instanceof Short) {
				out.writeByte(TYPE_SHORT);
				out.writeShort( ((Short)object).shortValue() );
			} else if (object instanceof Integer) {
				out.writeByte(TYPE_INTEGER);
				out.writeInt( ((Integer)object).intValue() );
			} else if (object instanceof Long) {
				out.writeByte(TYPE_LONG);
				out.writeLong( ((Long)object).longValue() );
			//#if polish.hasFloatingPoint
			} else if (object instanceof Float) {
				out.writeByte(TYPE_FLOAT);
				out.writeFloat( ((Float)object).floatValue() );
			} else if (object instanceof Double) {
				out.writeByte(TYPE_DOUBLE);
				out.writeDouble( ((Double)object).doubleValue() );
			//#endif
			} else if (object instanceof String) {
				out.writeByte(TYPE_STRING);
				out.writeUTF( (String)object );
			} else if (object instanceof StringBuffer) {
				out.writeByte(TYPE_STRING_BUFFER);
				out.writeUTF( ((StringBuffer)object).toString()  );
			} else if (object instanceof Character) {
				out.writeByte(TYPE_CHARACTER);
				out.writeChar( ((Character)object).charValue() );
			} else if (object instanceof Boolean) {
				out.writeByte(TYPE_BOOLEAN);
				out.writeBoolean( ((Boolean)object).booleanValue() );
			} else if (object instanceof Date) {
				out.writeByte(TYPE_DATE);
				out.writeLong( ((Date)object).getTime() );
			} else if (object instanceof Calendar) {
				out.writeByte(TYPE_CALENDAR);
				out.writeLong( ((Calendar)object).getTime().getTime() );
			} else if (object instanceof Random) {
				out.writeByte(TYPE_RANDOM);
			} else if (object instanceof Hashtable) {
				out.writeByte(TYPE_HASHTABLE);
				Hashtable table = (Hashtable) object;
				out.writeInt( table.size() );
				Enumeration enumeration = table.keys();
				while( enumeration.hasMoreElements() ) {
					Object key = enumeration.nextElement();
					serialize(key, out);
					Object value = table.get( key );
					serialize(value, out);
				}
			} else if (object instanceof Vector) { // also serializes stacks
				if (object instanceof Stack) {
					out.writeByte(TYPE_STACK);					
				} else {
					out.writeByte(TYPE_VECTOR);
				}
				Vector vector = (Vector) object;
				int size = vector.size();
				out.writeInt( size );
				for (int i = 0; i < size; i++) {
					serialize( vector.elementAt(i), out );
				}
			//#if polish.midp2
			} else if (object instanceof Image) {
				out.writeByte(TYPE_IMAGE);
				//#if polish.JavaSE
					boolean handled = false;
					// we are within a Java SE environment. When the J2ME Polish runtime librarby is used, we can 
					// store the image in PNG format instead of in the much more verbose RGB format:
					try {
						//#if false
							Field bufferedImageField = null;
						//#else
							//# Field bufferedImageField = object.getClass().getDeclaredField("bufferedImage");
						//#endif
						bufferedImageField.setAccessible( true );
						BufferedImage bufferedImage = (BufferedImage) bufferedImageField.get( object );
						ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
						ImageIO.write( bufferedImage, "png", byteOut );
						out.writeByte(TYPE_IMAGE_BYTES);
						byte[] data = byteOut.toByteArray();
						out.writeInt( data.length );
						for (int i = 0; i < data.length; i++) {
							out.writeByte( data[i] );
						}
						handled = true;
					} catch (Exception e) {
						e.printStackTrace();
						//#debug warn
						System.out.println("Warning: Unable to retrieve bufferedImage field of javax.microedition.lcdui.Image - probably the enough-polish-runtime library is not used.");
					}
					if (!handled) {
				//#endif
						Image image = (Image) object;
						out.writeByte(TYPE_IMAGE_RGB);
						int width = image.getWidth();
						int height = image.getHeight();
						out.writeInt( width );
						out.writeInt( height );
						int[] rgb = new int[ width * height ];
						image.getRGB(rgb, 0, width, 0, 0, width, height);
						for (int i = 0; i < rgb.length; i++) {
							out.writeInt( rgb[i] );
						}
				//#if polish.JavaSE
					}
				//#endif
			//#endif
			//#if polish.midp
			} else if (object instanceof Font) {
				out.writeByte(TYPE_FONT);
				Font font = (Font) object;
				out.writeInt( font.getFace() );
				out.writeInt( font.getStyle() );
				out.writeInt( font.getSize() );
			} else if (object instanceof Command) {
				out.writeByte(TYPE_COMMAND);
				Command command = (Command) object;
				out.writeInt( command.getCommandType() );
				out.writeInt( command.getPriority() );
				out.writeUTF( command.getLabel() );
			//#endif
			} else if (object instanceof byte[]) {
				out.writeByte(TYPE_BYTE_ARRAY);
				byte[] numbers = (byte[]) object;
				out.writeInt( numbers.length );
				out.write( numbers, 0, numbers.length );
			} else if (object instanceof short[]) {
				out.writeByte(TYPE_SHORT_ARRAY);
				short[] numbers = (short[]) object;
				out.writeInt( numbers.length );
				for (int i = 0; i < numbers.length; i++) {
					short number = numbers[i];
					out.writeShort( number );
				}
			} else if (object instanceof int[]) {
				out.writeByte(TYPE_INT_ARRAY);
				int[] numbers = (int[]) object;
				out.writeInt( numbers.length );
				for (int i = 0; i < numbers.length; i++) {
					int number = numbers[i];
					out.writeInt( number );
				}
			} else if (object instanceof long[]) {
				out.writeByte(TYPE_LONG_ARRAY);
				long[] numbers = (long[]) object;
				out.writeInt( numbers.length );
				for (int i = 0; i < numbers.length; i++) {
					long number = numbers[i];
					out.writeLong( number );
				}
			//#if polish.hasFloatingPoint
			} else if (object instanceof float[]) {
				out.writeByte(TYPE_FLOAT_ARRAY);
				float[] numbers = (float[]) object;
				out.writeInt( numbers.length );
				for (int i = 0; i < numbers.length; i++) {
					float number = numbers[i];
					out.writeFloat( number );
				}
			} else if (object instanceof double[]) {
				out.writeByte(TYPE_DOUBLE_ARRAY);
				double[] numbers = (double[]) object;
				out.writeInt( numbers.length );
				for (int i = 0; i < numbers.length; i++) {
					double number = numbers[i];
					out.writeDouble( number );
				}
			//#endif
			} else if (object instanceof char[]) {
				out.writeByte(TYPE_CHAR_ARRAY);
				char[] characters = (char[]) object;
				out.writeInt( characters.length );
				for (int i = 0; i < characters.length; i++) {
					char c = characters[i];
					out.writeChar( c );
				}
			} else if (object instanceof boolean[]) {
				out.writeByte(TYPE_BOOLEAN_ARRAY);
				boolean[] bools = (boolean[]) object;
				out.writeInt( bools.length );
				for (int i = 0; i < bools.length; i++) {
					boolean b = bools[i];
					out.writeBoolean( b );
				}
			} else if (object instanceof String[]) {
				out.writeByte(TYPE_STRING_ARRAY);
				String[] strings = (String[]) object;
				out.writeInt( strings.length );
				for (int i = 0; i < strings.length; i++) {
					String s = strings[i];
					out.writeUTF( s );
				}
			} else {
				throw new IOException("Cannot serialize " + object.getClass().getName() );
			}
		}
		
	}


	/**
	 * Deserializes an object from the given stream.
	 * 
	 * @param in the data input stream, from which the object is deserialized
	 * @return the serializable object
	 * @throws IOException when serialization data could not be read or the Serializable class could not get instantiated
	 */
	public static Object deserialize( DataInputStream in )
	throws IOException
	{
		byte version = in.readByte();
		//#if polish.debug.warn
			if (version > VERSION) {
				//#debug warn
				System.out.println("Warning: trying to deserialize class that has been serialized with a newer version (" + version + ">" + VERSION + ").");
			}
		//#endif
		boolean isNull = in.readBoolean();
		if (isNull) {
			return null;
		}
		byte type = in.readByte();
		switch (type) {
		case TYPE_EXTERNALIZABLE:
			String className = in.readUTF();
			//#if polish.JavaSE
				if (obfuscationDeserializeMap != null) {
					String fullClassName = (String) obfuscationDeserializeMap.get( className );
					if (fullClassName != null) {
						//System.out.println("Serializer.deserialize: translating classname from " + className + " to " +  fullClassName );
						className = fullClassName;
					}
				}
			//#endif
			//#debug debug
			//#= System.out.println("deserialize " + className + "...");
			Externalizable extern = null;
			try {
				extern = (Externalizable) Class.forName( className ).newInstance();
			} catch (Exception e) {
				//#debug error
				System.out.println("Unable to instantiate serializable \"" + className + "\"" + e);
				throw new IOException( e.toString() );
			}
			extern.read( in );
			return extern;
		case TYPE_EXTERNALIZABLE_ARRAY:
			String cn = in.readUTF();
			//#if polish.JavaSE
				if (obfuscationDeserializeMap != null) {
					String fullClassName = (String) obfuscationDeserializeMap.get( cn );
					if (fullClassName != null) {
						//System.out.println("Serializer.deserialize: translating classname from " + cn + " to " +  fullClassName );
						cn = fullClassName;
					}
				}
			//#endif

			int length = in.readInt();
			Externalizable[] externalizables;
		
			//#if !polish.JavaSE
				externalizables = new Externalizable[ length ];
			//#else
				try {
					//#if false
						externalizables = null;
					//#else
						//# externalizables = (Externalizable[]) Array.newInstance(Class.forName( cn ), length);
					//#endif
				} catch (Exception e) {
					//#debug error
					System.out.println("Unable to instantiate Serializable \"" + cn + "\"" + e);
					throw new IOException( e.toString() );
				}
			//#endif
      
			Class[] classes = new Class[ Math.min( length, 7 ) ];
			Class currentClass;
			byte idCounter = 0;
			for (int i = 0; i < externalizables.length; i++) {
				int classId = in.readByte();
				if (classId == 0) { // new class name
					className = in.readUTF();
					//#if polish.JavaSE
						if (obfuscationDeserializeMap != null) {
							String fullClassName = (String) obfuscationDeserializeMap.get( className );
							if (fullClassName != null) {
								//System.out.println("Serializer.deserialize: translating classname from " + className + " to " +  fullClassName );
								className = fullClassName;
							}
						}
					//#endif
					try {
						currentClass = Class.forName( className );
					} catch (ClassNotFoundException e) {
						//#debug error
						System.out.println("Unable to load Serializable class \"" + className + "\"" + e);
						throw new IOException( e.toString() );
					}
					if (idCounter > classes.length ) {
						Class[] newClasses = new Class[ classes.length + 7 ];
						System.arraycopy(classes, 0, newClasses, 0, classes.length);
						classes = newClasses;
					}
					classes[idCounter] = currentClass;
					idCounter++;
				} else {
					currentClass = classes[ classId  - 1 ];
				}
				Externalizable externalizable;
				try {
					externalizable = (Externalizable) currentClass.newInstance();
					externalizable.read(in);
					externalizables[i] = externalizable;
				} catch (Exception e) {
					//#debug error
					System.out.println("Unable to instantiate Serializable \"" + currentClass.getName() + "\"" + e);
					throw new IOException( e.toString() );
				}				
			}
			return externalizables;
		case TYPE_OBJECT_ARRAY:
			length = in.readInt();
			Object[] objects = new Object[ length ];
			for (int i = 0; i < objects.length; i++) {
				objects[i] = deserialize(in);
			}
			return objects;
		case TYPE_BYTE:
			return new Byte( in.readByte() );
		case TYPE_SHORT:
			return new Short( in.readShort() );
		case TYPE_INTEGER:
			return new Integer( in.readInt() );
		case TYPE_LONG:
			return new Long( in.readLong() );
		//#if polish.hasFloatingPoint
		case TYPE_FLOAT:
			return new Float( in.readFloat() );
		case TYPE_DOUBLE:
			return new Double( in.readDouble() );
		//#endif
		case TYPE_STRING:
			return in.readUTF();
		case TYPE_STRING_BUFFER:
			return new StringBuffer( in.readUTF() );
		case TYPE_CHARACTER:
			return new Character( in.readChar() );
		case TYPE_BOOLEAN:
			return new Boolean( in.readBoolean() );
		case TYPE_DATE:
			return new Date( in.readLong() );
		case TYPE_CALENDAR:
			Calendar calendar = Calendar.getInstance();
			calendar.setTime( new Date(in.readLong()) );
			return calendar;
		case TYPE_RANDOM:
			return new Random();
		case TYPE_HASHTABLE:
			int size = in.readInt();
			Hashtable hashtable = new Hashtable( size );
			for (int i = 0; i < size; i++) {
				Object key = deserialize(in);
				Object value = deserialize(in);
				hashtable.put( key, value );
			}
			return hashtable;
		case TYPE_STACK:
		case TYPE_VECTOR:
			size = in.readInt();
			Vector vector;
			if (type == TYPE_STACK) {
				vector = new Stack();
			} else {
				vector = new Vector( size );
			}
			for (int i = 0; i < size; i++) {
				Object value = deserialize(in);
				vector.addElement( value );
			}
			return vector;
		//#if polish.midp2
		case TYPE_IMAGE:
			byte subType = in.readByte();
			if (subType == TYPE_IMAGE_RGB) {
				int width = in.readInt();
				int height = in.readInt();
				int[] rgb = new int[ width * height ];
				for (int i = 0; i < rgb.length; i++) {
					rgb[i] = in.readInt();
				}
				return Image.createRGBImage(rgb, width, height, true );
			}

			// this is a bytes based format like png:
			int bytesLength = in.readInt();
			byte[] buffer = new byte[ bytesLength ];
			in.readFully( buffer );
			return Image.createImage( buffer, 0, bytesLength );
		//#endif
		//#if polish.midp
		case TYPE_FONT:
			int face = in.readInt();
			int style = in.readInt();
			size = in.readInt();
			return Font.getFont(face, style, size);
		case TYPE_COMMAND:
			int cmdType = in.readInt();
			int priority = in.readInt();
			String label = in.readUTF();
			return new Command( label, cmdType, priority );
		//#endif
		case TYPE_BYTE_ARRAY:
			length = in.readInt();
			byte[] byteNumbers = new byte[ length ];
			in.readFully( byteNumbers );
			return byteNumbers;
		case TYPE_SHORT_ARRAY:
			length = in.readInt();
			short[] shortNumbers = new short[ length ];
			for (int i = 0; i < length; i++) {
				shortNumbers[i] = in.readShort();
			}
			return shortNumbers;
		case TYPE_INT_ARRAY:
			length = in.readInt();
			int[] intNumbers = new int[ length ];
			for (int i = 0; i < length; i++) {
				intNumbers[i] = in.readInt();
			}
			return intNumbers;
		case TYPE_LONG_ARRAY:
			length = in.readInt();
			long[] longNumbers = new long[ length ];
			for (int i = 0; i < length; i++) {
				longNumbers[i] = in.readLong();
			}
			return longNumbers;
		//#if polish.hasFloatingPoint
		case TYPE_FLOAT_ARRAY:
			length = in.readInt();
			float[] floatNumbers = new float[ length ];
			for (int i = 0; i < length; i++) {
				floatNumbers[i] = in.readFloat();
			}
			return floatNumbers;
		case TYPE_DOUBLE_ARRAY:
			length = in.readInt();
			double[] doubleNumbers = new double[ length ];
			for (int i = 0; i < length; i++) {
				doubleNumbers[i] = in.readDouble();
			}
			return doubleNumbers;
		//#endif
		case TYPE_CHAR_ARRAY:
			length = in.readInt();
			char[] characters = new char[ length ];
			for (int i = 0; i < length; i++) {
				characters[i] = in.readChar();
			}
			return characters;
		case TYPE_BOOLEAN_ARRAY:
			length = in.readInt();
			boolean[] bools = new boolean[ length ];
			for (int i = 0; i < length; i++) {
				bools[i] = in.readBoolean();
			}
			return bools;
		case TYPE_STRING_ARRAY:
			length = in.readInt();
			String[] strings = new String[ length ];
			for (int i = 0; i < length; i++) {
				strings[i] = in.readUTF();
			}
			return strings;
		default: 
			throw new IOException("Unknown type: " + type );
		}
	}


}
