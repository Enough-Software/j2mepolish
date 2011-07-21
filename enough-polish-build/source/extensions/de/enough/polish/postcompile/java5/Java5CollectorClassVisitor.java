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

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

public class Java5CollectorClassVisitor
  extends ClassAdapter
{
  private Type owner;
  private EnumManager manager;
  private boolean isEnumClass;
  private int nextEnumValue;
  
  public Java5CollectorClassVisitor(ClassVisitor cv, Type owner)
  {
    super(cv);
    this.owner = owner;
    this.manager = EnumManager.getInstance();
    this.isEnumClass = this.manager.isEnumClass(this.owner);
  }

  public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
  {
    MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
    Method method = new Method(name, desc);
    mv = new EnumCollectorMethodVisitor(mv, this.owner, method);
    return mv;
  }

  public FieldVisitor visitField(int access, String name, String desc, String signature, Object value)
  {
    if (this.isEnumClass
        && (this.manager.isEnumClass(desc)))
      {
        String fieldName = this.owner.getDescriptor().replace('\\', '/') + "." + name;
        this.manager.addEnumValue(fieldName, Integer.valueOf(this.nextEnumValue));
        this.nextEnumValue++;
      }
    
    return super.visitField(access, name, desc, signature, value);
  }
}
