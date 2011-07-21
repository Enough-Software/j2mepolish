package de.enough.polish.postcompile.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import de.enough.bytecode.ASMClassLoader;
import de.enough.bytecode.PrimitiveTypesHelper;
import de.enough.polish.Environment;

public class SerializationVisitor
  extends ClassAdapter
  implements Opcodes
{
  public static final String POLISH_USE_DEFAULT_PACKAGE = "polish.useDefaultPackage";
  public static final String SERIALIZER = "de/enough/polish/io/Serializer";
  public static final String SERIALIZABLE = "de/enough/polish/io/Serializable";
  public static final String SERIALIZABLE_RENAMED = "de/enough/polish/io/EnoughSerializable";
  public static final String EXTERNALIZABLE = "de/enough/polish/io/Externalizable";
  public static final String DATAINPUTSTREAM = "java/io/DataInputStream";
  public static final String DATAOUTPUTSTREAM = "java/io/DataOutputStream";
  public static final String IOEXCEPTION = "java/io/IOException";

  public static final int READ_MODIFIERS = Opcodes.ACC_PUBLIC;
  public static final String READ_NAME = "read";
  public static final String READ_SIGNATURE = "(Ljava/io/DataInputStream;)V";

  public static final int WRITE_MODIFIERS = Opcodes.ACC_PUBLIC;
  public static final String WRITE_NAME = "write";
  public static final String WRITE_SIGNATURE = "(Ljava/io/DataOutputStream;)V";

  private static final HashMap methodNamesRead;
  private static final HashMap methodNamesWrite;
  
  static
  {
    methodNamesRead = new HashMap();
    methodNamesRead.put("C", "readChar()C");
    methodNamesRead.put("D", "readDouble()D");
    methodNamesRead.put("F", "readFloat()F");
    methodNamesRead.put("I", "readInt()I");
    methodNamesRead.put("J", "readLong()J");
    methodNamesRead.put("B", "readByte()B");
    methodNamesRead.put("S", "readShort()S");
    methodNamesRead.put("Z", "readBoolean()Z");

    methodNamesWrite = new HashMap();
    methodNamesWrite.put("C", "writeChar(I)V");
    methodNamesWrite.put("D", "writeDouble(D)V");
    methodNamesWrite.put("F", "writeFloat(F)V");
    methodNamesWrite.put("I", "writeInt(I)V");
    methodNamesWrite.put("J", "writeLong(J)V");
    methodNamesWrite.put("B", "writeByte(I)V");
    methodNamesWrite.put("S", "writeShort(I)V");
    methodNamesWrite.put("Z", "writeBoolean(Z)V");
  }

  protected static String getClassName(String className, Environment env)
  {
    if (env != null && env.hasSymbol(POLISH_USE_DEFAULT_PACKAGE))
      {
        int index = className.lastIndexOf('/') + 1;
        return className.substring(index);
      }

    return className;
  }
  
  protected static String getSerializableClassName( ASMClassLoader loader, Environment env ) {
	  String className = getClassName( SERIALIZABLE, env);
	  if (env != null && env.hasSymbol("polish.Bugs.ReservedKeywordSerializable")) 
	  {
		  try
		{
			loader.loadClass(className);
		} catch (ClassNotFoundException e)
		{
			className = getClassName( SERIALIZABLE_RENAMED, env);
		}
	  }
	  return className;
  }

  private String className;
  private String superClassName;
  private boolean superImplementsSerializable;
  private ASMClassLoader loader;
  private Environment environment; 
  private HashMap fields;
  private boolean generateDefaultConstructor = true;
  private boolean generateReadMethod = true;
  private boolean generateWriteMethod = true;
  private HashSet serializableObjectTypes;
  
  private String serializableClassName;
  private String externalizableClassName;

  public SerializationVisitor(ClassVisitor cv, ASMClassLoader loader,
                              Environment environment)
  {
    super(cv);
    this.loader = loader;
    this.environment = environment;
    this.fields = new HashMap();
    this.serializableObjectTypes = new HashSet();
    
    this.serializableClassName = getSerializableClassName(loader, environment);
    this.externalizableClassName = getClassName( EXTERNALIZABLE, environment );

    // TODO: This throws always a NullPointerException. Make this work reliable.
    if (false)
      {
    InputStream inputStream =
    	getClass().getResourceAsStream("/de/enough/polish/postcompiler/io/serializable_classes.txt");
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    
    try
      {
        String line = reader.readLine();
        
        while (line != null)
          {
            int pos = line.indexOf('#');
            
            if (pos >= 0)
              {
                line = line.substring(0, pos);
              }
            
            line = line.trim();
            
            if (line.length() > 0)
              {
                this.serializableObjectTypes.add(line);
              }
            
            line = reader.readLine();
          }
      }
    catch (IOException e)
      {
        e.printStackTrace();
      }
      } // End of broken code.
    
      // TODO: Remove these hardcoded entries again when the above code works.
      this.serializableObjectTypes.add("java/lang/Byte");
      this.serializableObjectTypes.add("java/lang/Short");
      this.serializableObjectTypes.add("java/lang/Integer");
      this.serializableObjectTypes.add("java/lang/Long");
      this.serializableObjectTypes.add("java/lang/Float");
      this.serializableObjectTypes.add("java/lang/Double");
      this.serializableObjectTypes.add("java/lang/Character");
      this.serializableObjectTypes.add("java/lang/String");
      this.serializableObjectTypes.add("java/lang/StringBuffer");
      this.serializableObjectTypes.add("java/lang/Boolean");
      this.serializableObjectTypes.add("java/lang/Object");
      this.serializableObjectTypes.add("java/util/Date");
      this.serializableObjectTypes.add("java/util/Calendar");
      this.serializableObjectTypes.add("java/util/Random");
      this.serializableObjectTypes.add("java/util/Hashtable");
      this.serializableObjectTypes.add("java/util/Stack");
      this.serializableObjectTypes.add("java/util/Vector");
      this.serializableObjectTypes.add("javax/microedition/lcdui/Image");
      this.serializableObjectTypes.add("javax/microedition/lcdui/Font");
      this.serializableObjectTypes.add("javax/microedition/lcdui/Command");
//      this.serializableObjectTypes.add("javax/microedition/lcdui/game/Sprite");
      this.serializableObjectTypes.add("Font");
      this.serializableObjectTypes.add("Image");
      this.serializableObjectTypes.add("de/enough/polish/blackberry/ui/Font");
      this.serializableObjectTypes.add("de/enough/polish/blackberry/ui/Image");
      this.serializableObjectTypes.add("de/enough/polish/doja/ui/Font");
      this.serializableObjectTypes.add("de/enough/polish/doja/ui/Image");
      this.serializableObjectTypes.add("de/enough/polish/android/lcdui/Font");
      this.serializableObjectTypes.add("de/enough/polish/android/lcdui/Image");
  }

  private String[] getSortedFields()
  {
    Iterator it = this.fields.keySet().iterator();
    String[] result = new String[this.fields.size()];

    for (int i = 0; it.hasNext(); i++)
      {
        result[i] = (String) it.next();
      }
    
    Arrays.sort(result, new Comparator() {
      public int compare(Object o1, Object o2)
      {
        String s1 = (String) o1;
        String s2 = (String) o2;
        return s1.compareTo(s2);
      }
    });
    return result;
  }

  private String[] rewriteInterfaces(String superName, String[] interfaces)
  {
    this.superImplementsSerializable =
      this.loader.inherits(this.serializableClassName, superName);

    boolean found = false;
    String serializable = this.serializableClassName;
    String externalizable = this.externalizableClassName;
    
    for (int i = 0; i < interfaces.length; i++)
      {
        if (interfaces[i].equals(serializable))
          {
            interfaces[i] = externalizable;
            found = true;
          }
      }
    
    if (! found)
      {
        found = this.superImplementsSerializable;
      }
    
    if (! found)
      {
        String[] tmp = new String[interfaces.length + 1];
        System.arraycopy(interfaces, 0, tmp, 0, interfaces.length);
        tmp[interfaces.length] = externalizable;
        interfaces = tmp;
      }
    
    return interfaces;
  }

  public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
  {
    // Calling rewriteInterfaces has the side effect that it initializes this.superImplementsSerializable
    super.visit(version, access, name, signature, superName,
                rewriteInterfaces(superName, interfaces));
    
    this.className = name;
    this.superClassName = superName;

//    this.superImplementsSerializable =
//      this.loader.inherits(getClassName(SERIALIZABLE, this.environment), superName);
  }

  public FieldVisitor visitField(int access, String name, String desc, String signature, Object value)
  {
    // Make final fields non-final.
    if ((access & ACC_STATIC) != ACC_STATIC)
      {
        access &= ~ ACC_FINAL;
      }
    
    // Only do serialization for non-transient fields.
    if ((access & ACC_TRANSIENT) != ACC_TRANSIENT
        && (access & ACC_STATIC) != ACC_STATIC)
      {
        String descShot = desc;
        
        while (PrimitiveTypesHelper.isArrayType(descShot))
          {
            descShot = descShot.substring(1);
          }
        
        // Primitive fields.
        if (PrimitiveTypesHelper.isPrimitiveType(descShot))
          {
            this.fields.put(name, desc);
          }
        // Non-primitive fields.
        else if (descShot.startsWith("L"))
          {
            descShot = descShot.substring(1, descShot.length() - 1);
            
            if (this.loader.inherits(this.serializableClassName, descShot))
              {
            	this.fields.put(name, desc);
              }
            else if (isSerializableObject(descShot))
              {
            	this.fields.put(name, desc);
              }
            else
              {
            	System.err.println("Cannot serialize field " + this.className + "." + name + ": use the \"transient\" modifier to exclude this field from serialization.");
                //throw new BuildException("Cannot serialize field " + this.className + "." + name + ": use the \"transient\" modifier to exclude this field from serialization.");
              }
          }
      }
    
    return super.visitField(access, name, desc, signature, value);
  }

  private boolean isSerializableObject(String type)
  {
	  return this.serializableObjectTypes.contains(type);
  }

  public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
  {
    if ("<init>".equals(name) && "()V".equals(desc))
      {
        this.generateDefaultConstructor = false;
      }
    
    if (READ_NAME.equals(name)
        && READ_SIGNATURE.equals(desc))
      {
        this.generateReadMethod = false;
      }
    
    if (WRITE_NAME.equals(name)
        && WRITE_SIGNATURE.equals(desc))
      {
        this.generateWriteMethod = false;
      }

    return super.visitMethod(access, name, desc, signature, exceptions);
  }

  public void visitEnd()
  {
    generateDefaultConstructor();
    generateReadMethod();
    generateWriteMethod();
    super.visitEnd();
  }

  private void generateDefaultConstructor()
  {
    if (!this.generateDefaultConstructor)
      {
        return;
      }
    
    MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "<init>", "()V", null, new String[0]);
    mv.visitCode();
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKESPECIAL, this.superClassName, "<init>", "()V");
    mv.visitInsn(RETURN);
    mv.visitMaxs(1, 1);
    mv.visitEnd();
  }

  private void generateReadMethod()
  {
    if (!this.generateReadMethod)
      {
        return;
      }

    int maxStack = 0;
    int maxVars = 2;
    
    MethodVisitor mv = 
      super.visitMethod(READ_MODIFIERS, READ_NAME, READ_SIGNATURE,
                        null, new String[] { IOEXCEPTION });
    
    mv.visitCode();
    
    if (this.superImplementsSerializable)
      {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESPECIAL, this.superClassName, "read", "(Ljava/io/DataInputStream;)V");
        maxStack = 2;
      }
    
    String[] fields = getSortedFields();

    if (fields.length != 0) {
    	maxStack = 2;
    }

    for (int i = 0; i < fields.length; i++)
      {
        String name = fields[i];
        String desc = (String) this.fields.get(name); 
        
        if (PrimitiveTypesHelper.isArrayType(desc))
          {
        	if (desc.equals("[Ljava/lang/Object;")) {
        		mv.visitVarInsn(ALOAD, 0);
        		mv.visitVarInsn(ALOAD, 1);
        		mv.visitMethodInsn(INVOKESTATIC, getClassName(SERIALIZER, this.environment), "deserialize", "(Ljava/io/DataInputStream;)Ljava/lang/Object;");
        		mv.visitTypeInsn(CHECKCAST, desc);
        		mv.visitFieldInsn(PUTFIELD, this.className, name, desc);
        		continue;
        	}

            if (PrimitiveTypesHelper.isMultiDimensionalArrayType(desc))
              {
                throw new RuntimeException("Multidimensional arrays are not supported by the serialization framework");
              }
            
            maxStack = Math.max(maxStack, 3);
            maxVars = Math.max(maxVars, 4);
            
            String type = desc.substring(1);
            
            if ("D".equals(type) || "J".equals(type))
              {
            	maxStack = Math.max(maxStack, 4);
              }

            Label arrayAfter = new Label();
            Label arrayLoopBegin = new Label();
            Label arrayLoopExpression = new Label();
            
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, DATAINPUTSTREAM, "readBoolean", "()Z");
            mv.visitJumpInsn(IFEQ, arrayAfter);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, DATAINPUTSTREAM, "readInt", "()I");
            mv.visitVarInsn(ISTORE, 2);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ILOAD, 2);

            if (type.startsWith("L"))
              {
                mv.visitTypeInsn(ANEWARRAY, type.substring(1, type.length() - 1));
              }
            else
              {
                mv.visitIntInsn(NEWARRAY, PrimitiveTypesHelper.getPrimitiveArrayType(type));
              }
            
            mv.visitFieldInsn(PUTFIELD, this.className, name, desc);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ISTORE, 3);
            mv.visitJumpInsn(GOTO, arrayLoopExpression);
            mv.visitLabel(arrayLoopBegin);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, this.className, name, desc);
            
            mv.visitVarInsn(ILOAD, 3);

            String method = (String) methodNamesRead.get(type);
            
            if (method != null)
              {
                int pos = method.indexOf('(');

                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, DATAINPUTSTREAM, method.substring(0, pos), method.substring(pos)); 
              }
            else
              {
            	String descShort = type.substring(1, type.length() - 1);
            	
                /* TODO: According to M. Zdila this breaks with Externalizable[].
            	if ( this.loader.inherits(getClassName(SERIALIZABLE, this.environment), descShort))
            	  {
                    maxStack = Math.max(maxStack, 4);
            		
                    mv.visitTypeInsn(NEW, descShort);
                    mv.visitInsn(DUP);
                    mv.visitMethodInsn(INVOKESPECIAL, descShort, "<init>", "()V");
                    mv.visitInsn(AASTORE);
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD, this.className, name, desc);
                    mv.visitVarInsn(ILOAD, 3);
                    mv.visitInsn(AALOAD);
                    mv.visitVarInsn(ALOAD, 1);
                    mv.visitMethodInsn(INVOKEVIRTUAL, descShort, "read", "(Ljava/io/DataInputStream;)V");
            	  }
            	else
                */
            	  {
            		mv.visitVarInsn(ALOAD, 1);
            		mv.visitMethodInsn(INVOKESTATIC, getClassName(SERIALIZER, this.environment), "deserialize", "(Ljava/io/DataInputStream;)Ljava/lang/Object;");
            		mv.visitTypeInsn(CHECKCAST, descShort);
            		mv.visitInsn(AASTORE);
            	  }
              }

            if ("I".equals(type))
              {
                mv.visitInsn(IASTORE);
              }
            else if ("B".equals(type) || "Z".equals(type))
              {
                mv.visitInsn(BASTORE);
              }
            else if ("C".equals(type))
              {
                mv.visitInsn(CASTORE);
              }
            else if ("S".equals(type))
              {
                mv.visitInsn(SASTORE);
              }
            else if ("J".equals(type))
              {
                mv.visitInsn(LASTORE);
              }
            else if ("F".equals(type))
              {
                mv.visitInsn(FASTORE);
              }
            else if ("D".equals(type))
              {
                mv.visitInsn(DASTORE);
              }

            mv.visitIincInsn(3, 1);
            
            mv.visitLabel(arrayLoopExpression);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitJumpInsn(IF_ICMPLT, arrayLoopBegin);
            mv.visitLabel(arrayAfter);
          }
        else // non-array types
          {
        	if (PrimitiveTypesHelper.isPrimitiveType(desc))
              {
            	if ("J".equals(desc) || "D".equals(desc))
            	  {
                    maxStack = Math.max(maxStack, 3);
            	  }
          	
                String method = (String) methodNamesRead.get(desc);
                int pos = method.indexOf('(');

                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, DATAINPUTSTREAM, method.substring(0, pos), method.substring(pos)); 
                mv.visitFieldInsn(PUTFIELD, this.className, name, desc);
              }
            else
              {
            	String descShort = desc.substring(1, desc.length() - 1);

            	mv.visitVarInsn(ALOAD, 0);
            	mv.visitVarInsn(ALOAD, 1);
            	mv.visitMethodInsn(INVOKESTATIC, getClassName(SERIALIZER, this.environment), "deserialize", "(Ljava/io/DataInputStream;)Ljava/lang/Object;");
            	mv.visitTypeInsn(CHECKCAST, descShort);
            	mv.visitFieldInsn(PUTFIELD, this.className, name, desc);
              }
          }
      }
    
    mv.visitInsn(RETURN);
    mv.visitMaxs(maxStack, maxVars);
    mv.visitEnd();
  }

  private void generateWriteMethod()
  {
    if (!this.generateWriteMethod)
      {
        return;
      }

    int maxStack = 0;
    int maxVars = 2;
    
    MethodVisitor mv =
      super.visitMethod(WRITE_MODIFIERS, WRITE_NAME, WRITE_SIGNATURE,
                        null, new String[] { IOEXCEPTION });
    
    mv.visitCode();
    
    if (this.superImplementsSerializable)
      {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESPECIAL, this.superClassName, "write", "(Ljava/io/DataOutputStream;)V");
        maxStack = 2;
      }
    
    String[] fields = getSortedFields();

    if (fields.length != 0) {
    	maxStack = 2;
    }

    for (int i = 0; i < fields.length; i++)
      {
        String name = fields[i];
        String desc = (String) this.fields.get(name);
        
        if (PrimitiveTypesHelper.isArrayType(desc))
          {
        	if (desc.equals("[Ljava/lang/Object;")) {
        		mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, this.className, name, desc);
                mv.visitVarInsn(ALOAD, 1);
        		mv.visitMethodInsn(INVOKESTATIC, getClassName(SERIALIZER, this.environment), "serialize", "(Ljava/lang/Object;Ljava/io/DataOutputStream;)V");
        		continue;
        	}

            if (PrimitiveTypesHelper.isMultiDimensionalArrayType(desc))
              {
                throw new RuntimeException("Multidimensional arrays are not supported by the serialization framework");
              }
            
            maxVars = Math.max(maxVars, 4);
            String type = desc.substring(1);
            
            Label arrayAfter = new Label();
            Label arrayNonNull = new Label();
            Label arrayLoopBegin = new Label();
            Label arrayLoopExpression = new Label();
            
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, this.className, name, desc);
            mv.visitJumpInsn(IFNONNULL, arrayNonNull);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_0);
            mv.visitMethodInsn(INVOKEVIRTUAL, DATAOUTPUTSTREAM, "writeBoolean", "(Z)V");
            mv.visitJumpInsn(GOTO, arrayAfter);
            mv.visitLabel(arrayNonNull);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_1);
            mv.visitMethodInsn(INVOKEVIRTUAL, DATAOUTPUTSTREAM, "writeBoolean", "(Z)V");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, this.className, name, desc);
            mv.visitInsn(ARRAYLENGTH);
            mv.visitVarInsn(ISTORE, 2);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, DATAOUTPUTSTREAM, "writeInt", "(I)V");
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ISTORE, 3);
            mv.visitJumpInsn(GOTO, arrayLoopExpression);
            mv.visitLabel(arrayLoopBegin);

            if (PrimitiveTypesHelper.isPrimitiveType(type))
              {
            	mv.visitVarInsn(ALOAD, 1);
              }
            
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, this.className, name, desc);
            mv.visitVarInsn(ILOAD, 3);
            
            if ("I".equals(type))
              {
                mv.visitInsn(IALOAD);
              }
            else if ("B".equals(type) || "Z".equals(type))
              {
                mv.visitInsn(BALOAD);
              }
            else if ("C".equals(type))
              {
                mv.visitInsn(CALOAD);
              }
            else if ("S".equals(type))
              {
                mv.visitInsn(SALOAD);
              }
            else if ("J".equals(type))
              {
                mv.visitInsn(LALOAD);
              }
            else if ("F".equals(type))
              {
                mv.visitInsn(FALOAD);
              }
            else if ("D".equals(type))
              {
                mv.visitInsn(DALOAD);
              }
            else // if (type.startsWith("L"))
              {
                mv.visitInsn(AALOAD);
              }
            
            String method = (String) methodNamesWrite.get(type);
            
            if (method != null)
              {
                maxStack = Math.max(maxStack, 3);
            	
            	int pos = method.indexOf('(');
                mv.visitMethodInsn(INVOKEVIRTUAL, DATAOUTPUTSTREAM, method.substring(0, pos), method.substring(pos));
              }
            else // if (type.startsWith("L"))
              {
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKESTATIC, getClassName(SERIALIZER, this.environment), "serialize", "(Ljava/lang/Object;Ljava/io/DataOutputStream;)V");
              }

            mv.visitIincInsn(3, 1);
            mv.visitLabel(arrayLoopExpression);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitJumpInsn(IF_ICMPLT, arrayLoopBegin);
            mv.visitLabel(arrayAfter);
          }
        else
          {
            if (PrimitiveTypesHelper.isPrimitiveType(desc))
              {
            	if ("J".equals(desc) || "D".equals(desc))
            	  {
                    maxStack = Math.max(maxStack, 3);
            	  }
            	
                String method = (String) methodNamesWrite.get(desc);
                int pos = method.indexOf('(');

                mv.visitVarInsn(ALOAD, 1);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, this.className, name, desc);
                mv.visitMethodInsn(INVOKEVIRTUAL, DATAOUTPUTSTREAM, method.substring(0, pos), method.substring(pos));
              }
            else
              {
            	mv.visitVarInsn(ALOAD, 0);
            	mv.visitFieldInsn(GETFIELD, this.className, name, desc);
            	mv.visitVarInsn(ALOAD, 1);
            	mv.visitMethodInsn(INVOKESTATIC, getClassName(SERIALIZER, this.environment), "serialize", "(Ljava/lang/Object;Ljava/io/DataOutputStream;)V");
              }
          }
      }
    
    mv.visitInsn(RETURN);
    mv.visitMaxs(maxStack, maxVars);
    mv.visitEnd();
  }
}
