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
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

public class EnumCollectorMethodVisitor
    extends MethodAdapter
    implements Opcodes
{
  private Type owner;
  private Method method;
  private EnumManager manager;

  public EnumCollectorMethodVisitor(MethodVisitor mv, Type owner, Method method)
  {
    super(mv);
    this.owner = owner;
    this.method = method;
    this.manager = EnumManager.getInstance();
  }

  public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index)
  {
    if (this.manager.isEnumClass(desc))
      {
        this.manager.addLocalVariable(new LocalVariableInfo(this.owner, this.method, index));
      }
    
    super.visitLocalVariable(name, desc, signature, start, end, index);
  }
}
