package de.enough.polish.postcompile.io;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.TraceClassVisitor;

import de.enough.bytecode.ASMClassLoader;
import de.enough.polish.util.StringUtil;

import junit.framework.TestCase;

public class SerializationVisitorTest
	extends TestCase
{
	public void testBoolean()
	{
		doTest("de/enough/polish/postcompile/io/TestSerialization_boolean");
	}

	public void testByte()
	{
		doTest("de/enough/polish/postcompile/io/TestSerialization_byte");
	}

	public void testChar()
	{
		doTest("de/enough/polish/postcompile/io/TestSerialization_char");
	}

	public void testInt()
	{
		doTest("de/enough/polish/postcompile/io/TestSerialization_int");
	}

	public void testDouble()
	{
		doTest("de/enough/polish/postcompile/io/TestSerialization_double");
	}

	public void testFloat()
	{
		doTest("de/enough/polish/postcompile/io/TestSerialization_float");
	}

	public void testLong()
	{
		doTest("de/enough/polish/postcompile/io/TestSerialization_long");
	}

	public void testShort()
	{
		doTest("de/enough/polish/postcompile/io/TestSerialization_short");
	}

	public void testBooleanArray()
	{
		doTest("de/enough/polish/postcompile/io/TestSerialization_booleanArray");
	}

	public void testByteArray()
	{
		doTest("de/enough/polish/postcompile/io/TestSerialization_byteArray");
	}

	public void testCharArray()
	{
		doTest("de/enough/polish/postcompile/io/TestSerialization_charArray");
	}

	public void testDoubleArray()
	{
		doTest("de/enough/polish/postcompile/io/TestSerialization_doubleArray");
	}

	public void testFloatArray()
	{
		doTest("de/enough/polish/postcompile/io/TestSerialization_floatArray");
	}

	public void testIntArray()
	{
		doTest("de/enough/polish/postcompile/io/TestSerialization_intArray");
	}

	public void testLongArray()
	{
		doTest("de/enough/polish/postcompile/io/TestSerialization_longArray");
	}

	public void testShortArray()
	{
		doTest("de/enough/polish/postcompile/io/TestSerialization_shortArray");
	}

	public void testInteger()
	{
		doTest("de/enough/polish/postcompile/io/TestSerialization_Integer");
	}

	public void testString()
	{
		doTest("de/enough/polish/postcompile/io/TestSerialization_String");
	}

	public void testSerializable()
	{
		doTest("de/enough/polish/postcompile/io/TestSerialization_Serializable");
	}

	public void testEmpty()
	{
		doTest("de/enough/polish/postcompile/io/TestSerialization_empty");
	}

	public void testStatic()
	{
		doTest("de/enough/polish/postcompile/io/TestSerialization_static");
	}

	public void testStringArray() {
		doTest("de/enough/polish/postcompile/io/TestSerialization_StringArray");
	}

	public void testSerializableArray()
	{
		doTest("de/enough/polish/postcompile/io/TestSerialization_SerializableArray");
	}

	public void testSerializableInheritance()
	{
		doTest("de/enough/polish/postcompile/io/TestSerialization_SerializableInheritance");
	}

	public void testAbstractInnerClass()
	{
		doTest("de/enough/polish/postcompile/io/TestSerialization_SerializableInheritance$AbstractSerializable");
	}

	public void testInnerClass()
	{
		doTest("de/enough/polish/postcompile/io/TestSerialization_SerializableInheritance$InnerSerializable");
	}

	public void testConstructor()
	{
		doTest("de/enough/polish/postcompile/io/TestSerialization_constructor");
	}

	public void testComplex1()
	{
		doTest("de/enough/polish/postcompile/io/TestSerialization_complex1");
	}

	public void testComplex2()
	{
		// TODO: This test is disabled because it cannot be tested. The java
		// source code that the generated code is compared against is invalid.
//		doTest("de/enough/polish/postcompile/io/TestSerialization_complex2");
	}

	private void doTest(String className)
	{
		try {
			ASMClassLoader loader;
			ClassNode clazz;
			StringWriter result;
			String expected, postcompiled;
			String classNameTemplate;
			int pos = className.indexOf('$');

			if (pos >= 0) {
				StringBuffer sb = new StringBuffer();

				sb.append(className.substring(0, pos));
				sb.append("_template");
				sb.append(className.substring(pos));
				classNameTemplate = sb.toString();
			}
			else {
				classNameTemplate = className + "_template";
			}

			loader = new ASMClassLoader();
			clazz = loader.loadClass(classNameTemplate);
			result = new StringWriter();
			clazz.accept(new TraceClassVisitor(new PrintWriter(result)));
			expected = StringUtil.replace(result.toString(), "_template", "");
			expected = removeDebugInfo(expected);

			loader = new ASMClassLoader();
			clazz = loader.loadClass(className);
			result = new StringWriter();
			clazz.accept(new SerializationVisitor(new TraceClassVisitor(new PrintWriter(result)), loader, null));
			postcompiled = result.toString();
			postcompiled = removeDebugInfo(postcompiled);

			assertEquals(expected, postcompiled);
		}
		catch (Exception e) {
			fail();
		}
	}

	private String removeDebugInfo(String lines) {
		lines = lines.replaceAll(" +LINENUMBER [0-9]+ L[0-9]+\\n", "");
		lines = lines.replaceAll(" +L[0-9]+\\n", "");
		lines = lines.replaceAll(" +LOCALVARIABLE \\w* [\\w/\\$;]* L[0-9]+ L[0-9]+ [0-9]+\\n", "");
		return lines;
	}
}
