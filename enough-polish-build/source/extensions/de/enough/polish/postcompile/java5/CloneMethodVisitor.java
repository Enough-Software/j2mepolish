/*
 * Created on Aug 16, 2006 at 12:51:15 PM.
 * 
 * Copyright (c) 2007 Michael Koch / Enough Software
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

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

public class CloneMethodVisitor
    extends GeneratorAdapter
    implements Opcodes
{
  private static final Type TYPE_SYSTEM = Type.getType("Ljava/lang/System;");
  
  private static final Method METHOD_ARRAYCOPY =
    Method.getMethod("void arraycopy(java.lang.Object, int, java.lang.Object, int, int)");
  
  private static final Method METHOD_CLONE =
    Method.getMethod("java.lang.Object clone()");

  private Type enumArrayDesc;
  
  public CloneMethodVisitor(MethodVisitor mv, int access, String name, String desc, Type enumType)
  {
    super(mv, access, name, desc);
    
    this.enumArrayDesc = Type.getType("[" + enumType.toString());
  }

  public void visitMethodInsn(int opcode, String owner, String name, String desc)
  {
    Method method = new Method(name, desc);

    // Rewrite TYPE[].clone() calles.
    if (INVOKEVIRTUAL == opcode
        && this.enumArrayDesc.getDescriptor().equals(owner)
        && METHOD_CLONE.equals(method))
      {
        dup();
        arrayLength();
        dup();
        newArray(Type.INT_TYPE);
        dupX2();
        swap();
        push(0);
        dupX2();
        swap();
        invokeStatic(TYPE_SYSTEM, METHOD_ARRAYCOPY);
        return;
      }

    super.visitMethodInsn(opcode, owner, name, desc);
  }

	/* (non-Javadoc)
	 * @see org.objectweb.asm.MethodAdapter#visitTypeInsn(int, java.lang.String)
	 */
	public void visitTypeInsn(int opcode, String type)
	{
	    // JDK 6u10 and up use already System.arraycopy() instead of clone()
	    // inside the values() method of an enum type but need to be fixed.
	    if (ANEWARRAY == opcode) {
	    	visitIntInsn(NEWARRAY, T_INT);
	    	return;
	    }

		super.visitTypeInsn(opcode, type);
	}
}
