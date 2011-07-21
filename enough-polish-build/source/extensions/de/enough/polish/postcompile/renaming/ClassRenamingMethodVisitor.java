package de.enough.polish.postcompile.renaming;

import java.util.Map;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import de.enough.polish.libraryprocessor.ImportConversionMap.ConversionTarget;

public class ClassRenamingMethodVisitor
	extends MethodAdapter
{
	private Map renamingMap;

	public ClassRenamingMethodVisitor(MethodVisitor mv, Map renamingMap)
	{
		super(mv);
		this.renamingMap = renamingMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.asm.MethodAdapter#visitFieldInsn(int,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	public void visitFieldInsn(int opcode, String owner, String name, String desc)
	{
		owner = ClassRenamingHelper.doRenaming(owner, this.renamingMap);
		desc = ClassRenamingHelper.doRenaming(desc, this.renamingMap);
		this.mv.visitFieldInsn(opcode, owner, name, desc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.asm.MethodAdapter#visitLocalVariable(java.lang.String,
	 * java.lang.String, java.lang.String, org.objectweb.asm.Label,
	 * org.objectweb.asm.Label, int)
	 */
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index)
	{
		desc = ClassRenamingHelper.doRenaming(desc, this.renamingMap);
		signature = ClassRenamingHelper.doRenaming(signature, this.renamingMap);
		this.mv.visitLocalVariable(name, desc, signature, start, end, index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.asm.MethodAdapter#visitMethodInsn(int,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	public void visitMethodInsn(int opcode, String owner, String name, String desc)
	{
		ConversionTarget target = (ConversionTarget) this.renamingMap.get(owner);

		if (target != null) {
			if (opcode == Opcodes.INVOKEVIRTUAL && target.isInterface()) {
				opcode = Opcodes.INVOKEINTERFACE;
			} else if (opcode == Opcodes.INVOKEINTERFACE
					&& !target.isInterface()) {
				opcode = Opcodes.INVOKEVIRTUAL;
			}
		}

		owner = ClassRenamingHelper.doRenaming(owner, this.renamingMap);
		desc = ClassRenamingHelper.doRenaming(desc, this.renamingMap);
		this.mv.visitMethodInsn(opcode, owner, name, desc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.objectweb.asm.MethodAdapter#visitMultiANewArrayInsn(java.lang.String,
	 * int)
	 */
	public void visitMultiANewArrayInsn(String desc, int dims)
	{
		desc = ClassRenamingHelper.doRenaming(desc, this.renamingMap);
		this.mv.visitMultiANewArrayInsn(desc, dims);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.objectweb.asm.MethodAdapter#visitTryCatchBlock(org.objectweb.asm.
	 * Label, org.objectweb.asm.Label, org.objectweb.asm.Label,
	 * java.lang.String)
	 */
	public void visitTryCatchBlock(Label start, Label end, Label handler, String type)
	{
		type = ClassRenamingHelper.doRenaming(type, this.renamingMap);
		super.visitTryCatchBlock(start, end, handler, type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.asm.MethodAdapter#visitTypeInsn(int, java.lang.String)
	 */
	public void visitTypeInsn(int opcode, String desc)
	{
		desc = ClassRenamingHelper.doRenaming(desc, this.renamingMap);
		this.mv.visitTypeInsn(opcode, desc);
	}
}
