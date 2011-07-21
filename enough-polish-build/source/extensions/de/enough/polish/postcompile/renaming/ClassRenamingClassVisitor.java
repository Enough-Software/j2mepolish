package de.enough.polish.postcompile.renaming;

import java.util.Map;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

public class ClassRenamingClassVisitor
    extends ClassAdapter
{
  private Map renamingMap;

  public ClassRenamingClassVisitor(ClassVisitor cv, Map renamingMap)
  {
    super(cv);
    this.renamingMap = renamingMap;
  }

  /* (non-Javadoc)
   * @see org.objectweb.asm.ClassAdapter#visit(int, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
   */
  public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
  {
    name = ClassRenamingHelper.doRenaming(name, this.renamingMap);
    signature = ClassRenamingHelper.doRenaming(signature, this.renamingMap);
    superName = ClassRenamingHelper.doRenaming(superName, this.renamingMap);
    interfaces = ClassRenamingHelper.doRenaming(interfaces, this.renamingMap);
    this.cv.visit(version, access, name, signature, superName, interfaces);
  }

  /* (non-Javadoc)
   * @see org.objectweb.asm.ClassAdapter#visitField(int, java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
   */
  public FieldVisitor visitField(int access, String name, String desc, String signature, Object value)
  {
    desc = ClassRenamingHelper.doRenaming(desc, this.renamingMap);
    signature = ClassRenamingHelper.doRenaming(signature, this.renamingMap);
    // TODO: What to do with the type of "value" argument.
    return this.cv.visitField(access, name, desc, signature, value);
  }

  /* (non-Javadoc)
   * @see org.objectweb.asm.ClassAdapter#visitMethod(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
   */
  public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
  {
    desc = ClassRenamingHelper.doRenaming(desc, this.renamingMap);
    signature = ClassRenamingHelper.doRenaming(signature, this.renamingMap);
    exceptions = ClassRenamingHelper.doRenaming(exceptions, this.renamingMap);
    MethodVisitor tmp = this.cv.visitMethod(access, name, desc, signature, exceptions);
    return new ClassRenamingMethodVisitor(tmp, this.renamingMap);
  }
}
