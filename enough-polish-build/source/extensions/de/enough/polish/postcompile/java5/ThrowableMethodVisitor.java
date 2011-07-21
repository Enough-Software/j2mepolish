package de.enough.polish.postcompile.java5;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

public class ThrowableMethodVisitor
    extends GeneratorAdapter
    implements Opcodes
{
  private static final Method METHOD_INITCAUSE =
    Method.getMethod("java.lang.Throwable initCause(java.lang.Throwable)");
  
  public ThrowableMethodVisitor(MethodVisitor mv, int access, String name, String desc)
  {
    super(mv, access, name, desc);
  }

  public void visitMethodInsn(int opcode, String owner, String name, String desc)
  {
    Method method = new Method(name, desc);
    
    if (INVOKEVIRTUAL == opcode
        && METHOD_INITCAUSE.equals(method))
      {
        pop();
        return;
      }

    super.visitMethodInsn(opcode, owner, name, desc);
  }
}
