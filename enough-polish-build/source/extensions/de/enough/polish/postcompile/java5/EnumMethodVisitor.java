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

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

/**
 * @author mkoch
 *
 */
public class EnumMethodVisitor
    extends GeneratorAdapter
    implements Opcodes
{
  private Type owner;
  private Method method;
  private EnumManager manager;
  
  public EnumMethodVisitor(MethodVisitor mv, int access, String name, String desc, String signature, String[] exceptions, Type owner)
  {
    super(mv, access, name, EnumManager.transform(desc));
    this.owner = owner;
    this.method = new Method(name, desc);
    this.manager = EnumManager.getInstance();
  }

  /* (non-Javadoc)
   * @see org.objectweb.asm.MethodAdapter#visitFieldInsn(int, java.lang.String, java.lang.String, java.lang.String)
   */
  public void visitFieldInsn(int opcode, String owner, String name, String desc)
  {
    if (GETSTATIC == opcode
        && this.manager.isEnumClass(desc))
      {
        Integer value = (Integer) this.manager.getEnumValue(owner, name);
        push(value.intValue());
        return;
      }
    
    super.visitFieldInsn(opcode, owner, name, EnumManager.transform(desc));
  }
  
  /* (non-Javadoc)
   * @see org.objectweb.asm.MethodAdapter#visitMethodInsn(int, java.lang.String, java.lang.String, java.lang.String)
   */
  public void visitMethodInsn(int opcode, String owner, String name, String desc)
  {
    // TODO: Check for enum class name too.
    if (INVOKEVIRTUAL == opcode
        && "ordinal".equals(name))
      {
        // As we converted the enum constant to int we dont need
        // to call ordinal() and can just continue.
        return;
      }
    
    super.visitMethodInsn(opcode, owner, name, EnumManager.transform(desc));
  }

  /* (non-Javadoc)
   * @see org.objectweb.asm.MethodAdapter#visitMultiANewArrayInsn(java.lang.String, int)
   */
//  public void visitMultiANewArrayInsn(String desc, int dims)
//  {
//    super.visitMultiANewArrayInsn(transform(desc), dims);
//  }

  /* (non-Javadoc)
   * @see org.objectweb.asm.MethodAdapter#visitTypeInsn(int, java.lang.String)
   */
  public void visitTypeInsn(int opcode, String desc)
  {
    if (CHECKCAST == opcode
    	&& EnumManager.getInstance().isEnumClass(desc)) {
    	super.visitInsn(NOP);
    }
    else  {
    	super.visitTypeInsn(opcode, EnumManager.transform(desc));
    }
  }
  
  /* (non-Javadoc)
   * @see org.objectweb.asm.commons.LocalVariablesSorter#visitVarInsn(int, int)
   */
  public void visitVarInsn(int opcode, int var)
  {
    EnumManager manager = EnumManager.getInstance();
    LocalVariableInfo variable = new LocalVariableInfo(this.owner, this.method, var);
    
    if (ALOAD == opcode)
      {
        if (manager.contains(variable))
          {
            opcode = ILOAD;
          }
      }
    else if (ASTORE == opcode)
      {
        if (manager.contains(variable))
          {
            opcode = ISTORE;
          }
      }
    
    super.visitVarInsn(opcode, var);
  }

  /* (non-Javadoc)
   * @see org.objectweb.asm.commons.LocalVariablesSorter#visitLocalVariable(java.lang.String, java.lang.String, java.lang.String, org.objectweb.asm.Label, org.objectweb.asm.Label, int)
   */
  public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index)
  {
    super.visitLocalVariable(name, EnumManager.transform(desc), signature, start, end, index);
  }
}
