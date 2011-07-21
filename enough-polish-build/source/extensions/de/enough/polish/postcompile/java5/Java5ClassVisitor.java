/*
 * Created on Sep 13, 2006 at 12:10:06 AM.
 * 
 * Copyright (c) 2006 Michael Koch / Enough Software
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
package de.enough.polish.postcompile.java5;

import org.apache.tools.ant.BuildException;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

public class Java5ClassVisitor
    extends ClassAdapter
    implements Opcodes
{
  private static final String METHOD_VALUES = "values";
  
  private boolean isEnumClass;
  private String className;
  private String classDesc;
  private String signature_values;
  private String name_values;
  
  public Java5ClassVisitor(ClassVisitor cv)
  {
    super(cv);
  }

  /* (non-Javadoc)
   * @see org.objectweb.asm.ClassAdapter#visit(int, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
   */
  public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
  {
    super.visit(version, access, name, signature, superName, interfaces);
    
    this.isEnumClass = EnumManager.getInstance().isEnumClass(name);
    this.className = name;
    this.classDesc = "L" + this.className + ";";
    this.signature_values = "()[L" + this.className + ";"; 
  }

  /* (non-Javadoc)
   * @see org.objectweb.asm.ClassAdapter#visitMethod(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
   */
  public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
  {
    // We keep only the values() method in enum classes.
    if (this.isEnumClass
        && ! METHOD_VALUES.equals(name))
      {
        return null;
      }
   
    MethodVisitor mv = super.visitMethod(access, name, EnumManager.transform(desc), signature, exceptions);
    mv = new EnumMethodVisitor(mv, access, name, desc, signature, exceptions, Type.getType(this.classDesc));
    mv = new ThrowableMethodVisitor(mv, access, name, desc);
    mv = new IteratorMethodVisitor(mv, access, name, desc);
    
    if (this.isEnumClass
        && METHOD_VALUES.equals(name)
        && this.signature_values.equals(desc))
      {
        mv = new CloneMethodVisitor(mv, access, name, desc,
                                    Type.getType("L" + this.className + ";"));
      }
    
    return mv;
  }

  /* (non-Javadoc)
   * @see org.objectweb.asm.ClassAdapter#visitField(int, java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
   */
  public FieldVisitor visitField(int access, String name, String desc, String signature, Object value)
  {
    EnumManager enumManager = EnumManager.getInstance();

    if (this.isEnumClass
        && this.classDesc.equals(desc))
      {
        String fieldName = this.classDesc + "." + name;
        desc = "I";
        value = enumManager.getEnumValue(fieldName);
        
        if (value == null)
          {
            throw new BuildException("Value for enum is null");
          }
      }
    
    // Workaround for bug 64.
    if (this.isEnumClass
        && desc.startsWith("["))
//    if (this.isEnumClass
//        && desc.equals(this.classArrayDesc))
      {
        this.name_values = name;
        desc = "[I";
      }
    
    if (desc.charAt(0) == 'L'
    	&& enumManager.isEnumClass(desc.substring(1, desc.length() - 1))) {
    	desc = "I";
    }

    return super.visitField(access, name, desc, signature, value);
  }

  public void visitEnd()
  {
    if (this.isEnumClass)
      {
        if (this.name_values == null)
          {
            throw new BuildException("This is not an enum class: " + this.classDesc);
          }
        
        // Generate new <clinit> method.
        int numValues = EnumManager.getInstance().getNumEnumValues(this.classDesc);
        Method m = Method.getMethod("void <clinit> ()");
        MethodVisitor mv = super.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
        GeneratorAdapter mg = new GeneratorAdapter(ACC_STATIC, m, mv);
        mg.push(numValues);
        mg.newArray(Type.INT_TYPE);

        if (numValues <= 3)
          {
            for (int i = 1; i < numValues; i++)
              {
                mg.dup();
                mg.push(i);
                mg.push(i);
                mg.arrayStore(Type.INT_TYPE);
              }
          }
        else
          {
            Label labelInitializeField = new Label();
            Label labelCheck = new Label();
            Label labelDone = new Label();
            
            mg.push(1);
            mg.storeLocal(0, Type.INT_TYPE);
            mg.goTo(labelCheck);
            
            mg.mark(labelInitializeField);
            mg.dup();
            mg.loadLocal(0, Type.INT_TYPE);
            mg.dup();
            mg.arrayStore(Type.INT_TYPE);
            mg.iinc(0, 1);
            
            mg.mark(labelCheck);
            mg.loadLocal(0, Type.INT_TYPE);
            mg.push(numValues);
            mg.ifICmp(GeneratorAdapter.LT, labelInitializeField);
            
            mg.mark(labelDone);
          }
        
        mg.putStatic(Type.getType(this.classDesc), this.name_values, Type.getType(int[].class));
        mg.returnValue();
        mg.endMethod();
      }
    
    // Called super implementation of this method to really close this class.
    super.visitEnd();
  }
}
