/*
 * Created on Dec 29, 2006 at 12:52:38 AM.
 * 
 * Copyright (c) 2006 Robert Virkus / Enough Software
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
package de.enough.polish.sourceparser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.enough.polish.sourceparser.JavaSourceClass;
import junit.framework.TestCase;

/**
 * <p>tests the JavaSourceFile implementation</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        Dec 29, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class JavaSourceClassTest extends TestCase {


	/**
	 * @param name of the test
	 */
	public JavaSourceClassTest(String name) {
		super(name);
	}
	
	public void testRemoveComments() {
		String[] lines = new String[] {
				"package de.enough.polish.util;	 ",
				"  ",
				"	/* some explanation",
				"	   over several ",
				"	lines */import de.enough.io;",
				""
		};
		String[] cleanCode = JavaSourceClass.removeComments(lines);
		assertEquals( 2, cleanCode.length );
		assertEquals("package de.enough.polish.util;", cleanCode[0]);
		assertEquals("import de.enough.io;", cleanCode[1]);
		
		
		lines = new String[] {
				"package de.enough.polish.util;	// end of line comment ",
				"  // comment",
				"	/* some explanation",
				"	   over several ",
				"	lines */import de.enough.io;// more comments",
				"// single line comment"
		};
		cleanCode = JavaSourceClass.removeComments(lines);
		assertEquals( 2, cleanCode.length );
		assertEquals("package de.enough.polish.util;", cleanCode[0]);
		assertEquals("import de.enough.io;", cleanCode[1]);
		
		lines = new String[] {
				"package de.enough.polish.util;	 ",
				"  // comment",
				"	/* some explanation",
				"	   over several ",
				"	lines *import de.enough.io;// more comments",
				"// single line comment"
		};
		try {
			JavaSourceClass.removeComments(lines);
			fail("unbound multiline comment should trigger exception.");
		} catch (RuntimeException e) {
			// expected
		}

	}
	

	
	public void testPackage() {
		String[] lines = new String[] {
				"package de.enough.polish.util;	 ",
				"  ",
				"	/* some explanation",
				"	   over several ",
				"	lines */import de.enough.io;",
				""
		};
		JavaSourceClass file = new JavaSourceClass(lines);
		assertEquals("de.enough.polish.util", file.getPackageName() );
	}
	
	public void testImports() {
		String[] lines = new String[] {
				"package de.enough.polish.util;	 ",
				"  ",
				"	/* some explanation",
				"	   over several ",
				"	lines */import de.enough.io;",
				""
		};
		JavaSourceClass file = new JavaSourceClass(lines);
		String[] imports = file.getImportStatements();
		assertNotNull( imports );
		assertEquals( 1, imports.length );
		assertEquals("de.enough.io", imports[0] );
		
		lines = new String[] {
				"package de.enough.polish.util;	 ",
				"  ",
				"	/* some explanation",
				"	   over several ",
				"	lines */import de.enough.io;import de.enough.util.*;",
				"",
				"public class MyClass extends Vector"
		};
		file = new JavaSourceClass(lines);
		imports = file.getImportStatements();
		assertNotNull( imports );
		assertEquals( 2, imports.length );
		assertEquals("de.enough.io", imports[0] );
		assertEquals("de.enough.util.*", imports[1] );

		lines = new String[] {
				"package de.enough.polish.util;	 ",
				"  ",
				"	/* some explanation",
				"	   over several ",
				"	lines */import de.enough.io;import de.enough.util.*;",
				"		import java.util.Vector;",
				"public class MyClass extends Vector"
		};
		file = new JavaSourceClass(lines);
		imports = file.getImportStatements();
		assertNotNull( imports );
		assertEquals( 3, imports.length );
		assertEquals("de.enough.io", imports[0] );
		assertEquals("de.enough.util.*", imports[1] );
		assertEquals("java.util.Vector", imports[2] );

	}
	
	
	public void testClassName() {
		String[] lines = new String[] {
				"package de.enough.polish.util;	 ",
				"  ",
				"	/* some explanation",
				"	   over several ",
				"	lines */import de.enough.io;",
				"",
				"public class MyClass extends Vector"
		};
		JavaSourceClass file = new JavaSourceClass(lines);
		assertEquals("MyClass", file.getClassName() );
		assertEquals( true, file.isClass() );
		
		lines = new String[] {
				"package de.enough.polish.util;	 ",
				"  ",
				"	/* some explanation",
				"	   over several ",
				"	lines */import de.enough.io;import de.enough.util.*;",
				"",
				"public class MyClass extends Vector"
		};
		file = new JavaSourceClass(lines);
		assertEquals("MyClass", file.getClassName() );
		assertEquals( true, file.isClass() );

		lines = new String[] {
				"package de.enough.polish.util;	 ",
				"  ",
				"	/* some explanation",
				"	   over several ",
				"	lines */import de.enough.io;import de.enough.util.*;",
				"		import java.util.Vector;",
				"public interface MyClass extends Remote"
		};
		file = new JavaSourceClass(lines);
		assertEquals("MyClass", file.getClassName() );
		assertEquals( false, file.isClass() );

	}
	
	public void testExtends() {
		String[] lines = new String[] {
				"package de.enough.polish.util;	 ",
				"  ",
				"	/* some explanation",
				"	   over several ",
				"	lines */import de.enough.io;",
				"",
				"public class MyClass extends Vector {"
		};
		JavaSourceClass file = new JavaSourceClass(lines);
		assertEquals("Vector", file.getExtendsStatement() );
		
		lines = new String[] {
				"package de.enough.polish.util;	 ",
				"  ",
				"	/* some explanation",
				"	   over several ",
				"	lines */import de.enough.io;import de.enough.util.*;",
				"",
				"public class MyClass extends java.util.Vector"
		};
		file = new JavaSourceClass(lines);
		assertEquals("java.util.Vector", file.getExtendsStatement() );

		lines = new String[] {
				"package de.enough.polish.util;	 ",
				"  ",
				"	/* some explanation",
				"	   over several ",
				"	lines */import de.enough.io;import de.enough.util.*;",
				"",
				"public class MyClass",
				"extends java.util.Vector",
				"implements Remote",
				"{"
		};
		file = new JavaSourceClass(lines);
		assertEquals("java.util.Vector", file.getExtendsStatement() );
	}
	
	public void testImplements() {
		Matcher matcher = JavaSourceClass.IMPLEMENTS_PATTERN.matcher("public class MyClass implements MyServer");
		assertEquals( true, matcher.find() );
		assertEquals( "implements MyServer", matcher.group() );
		
		String[] lines = new String[] {
				"package de.enough.polish.util;	 ",
				"  ",
				"	/* some explanation",
				"	   over several ",
				"	lines */import de.enough.io;",
				"public class MyClass implements MyServer"
		};
		JavaSourceClass file = new JavaSourceClass(lines);
		String[] implementedInterfaces = file.getImplementedInterfaces();
		
		assertNotNull( implementedInterfaces );
		assertEquals( 1, implementedInterfaces.length );
		assertEquals("MyServer", implementedInterfaces[0] );
		
		lines = new String[] {
				"package de.enough.polish.util;	 ",
				"  ",
				"	/* some explanation",
				"	   over several ",
				"	lines */import de.enough.io;import de.enough.util.*;",
				"",
				"public class MyClass extends Vector implements MyServer, MyOtherServer"
		};
		file = new JavaSourceClass(lines);
		implementedInterfaces = file.getImplementedInterfaces();
		assertNotNull( implementedInterfaces );
		assertEquals( 2, implementedInterfaces.length );
		assertEquals("MyServer", implementedInterfaces[0] );
		assertEquals("MyOtherServer", implementedInterfaces[1] );

		lines = new String[] {
				"package de.enough.polish.util;	 ",
				"  ",
				"	/* some explanation",
				"	   over several ",
				"	lines */import de.enough.io;import de.enough.util.*;",
				"		import java.util.Vector;",
				"public class MyClass extends Vector implements de.enough.io.Storage, MyServer"
		};
		file = new JavaSourceClass(lines);
		implementedInterfaces = file.getImplementedInterfaces();
		assertNotNull( implementedInterfaces );
		assertEquals( 2, implementedInterfaces.length );
		assertEquals("de.enough.io.Storage", implementedInterfaces[0] );
		assertEquals("MyServer", implementedInterfaces[1] );
		
	}
	
	
	public void testMethods() {
		Pattern pattern = JavaSourceClass.METHOD_PATTERN;
		Matcher matcher = pattern.matcher("	public static void hello();");
		assertEquals(true, matcher.find() );
		assertEquals("public static void hello()", matcher.group() );
		matcher = pattern.matcher("	public static void hello(String first, Integer second, com.server.MyClass third);");
		assertEquals(true, matcher.find() );
		assertEquals("public static void hello(String first, Integer second, com.server.MyClass third)", matcher.group() );		
		matcher = pattern.matcher("	public static void hello(String first, Integer second, com.server.MyClass third) throws IOException, BBEx;");
		assertEquals(true, matcher.find() );
		assertEquals("public static void hello(String first, Integer second, com.server.MyClass third) throws IOException, BBEx", matcher.group() );
		matcher = pattern.matcher("	public static void hello(String first, Integer second, com.server.MyClass[] third) throws IOException, BBEx;");
		assertEquals(true, matcher.find() );
		assertEquals("public static void hello(String first, Integer second, com.server.MyClass[] third) throws IOException, BBEx", matcher.group() );
		
		String[] lines = new String[] {
				"package de.enough.polish.util;	 ",
				"  ",
				"	/* some explanation",
				"	   over several ",
				"	lines */import de.enough.io;",
				"",
				"public interface MyClass extends Remote {",
				"	public void hello();",
				"}"
		};
		JavaSourceClass file = new JavaSourceClass(lines);
		JavaSourceMethod[] methods = file.getMethods();
		assertNotNull( methods );
		assertEquals( 1, methods.length );
		assertEquals("public", methods[0].getModifier() );
		assertEquals("void", methods[0].getReturnType() );
		assertEquals("hello", methods[0].getName() );
		assertEquals(null, methods[0].getParameterNames() );
		assertEquals(null, methods[0].getThrownExceptions() );
		
		lines = new String[] {
				"package de.enough.polish.util;	 ",
				"  ",
				"	/* some explanation",
				"	   over several ",
				"	lines */import de.enough.io;",
				"",
				"public interface MyClass extends Remote {",
				"	public void hello();",
				"	public final com.server.User hello( String first, Integer second, com.server.User third );",
				"	void testNoModifiers();",
				"}"
		};
		file = new JavaSourceClass(lines);
		methods = file.getMethods();
		assertNotNull( methods );
		assertEquals( 3, methods.length );
		assertEquals("public", methods[0].getModifier() );
		assertEquals("void", methods[0].getReturnType() );
		assertEquals("hello", methods[0].getName() );
		assertEquals(null, methods[0].getParameterNames() );
		assertEquals(null, methods[0].getThrownExceptions() );
		assertEquals("public final", methods[1].getModifier() );
		assertEquals("com.server.User", methods[1].getReturnType() );
		assertEquals("hello", methods[1].getName() );
		assertEquals(3, methods[1].getParameterNames().length );
		assertEquals("String", methods[1].getParameterTypes()[0] );
		assertEquals("first", methods[1].getParameterNames()[0] );
		assertEquals("Integer", methods[1].getParameterTypes()[1] );
		assertEquals("second", methods[1].getParameterNames()[1] );
		assertEquals("com.server.User", methods[1].getParameterTypes()[2] );
		assertEquals("third", methods[1].getParameterNames()[2] );
		assertEquals("", methods[2].getModifier() );
		assertEquals("void", methods[2].getReturnType() );
		assertEquals("testNoModifiers", methods[2].getName());
		assertEquals(null, methods[2].getParameterNames() );
		assertEquals(null, methods[2].getThrownExceptions() );
		
		lines = new String[] {
				"package de.enough.polish.util;	 ",
				"  ",
				"	/* some explanation",
				"	   over several ",
				"	lines */import de.enough.io;",
				"",
				"public interface MyClass extends Remote {",
				"	public void hello();",
				"	public final com.server.User hello( String first, Integer second, ",
				"com.server.User third )",
				"throws IOException, EOFException;",
				"}"
		};
		file = new JavaSourceClass(lines);
		methods = file.getMethods();
		assertNotNull( methods );
		assertEquals( 2, methods.length );
		assertEquals("public", methods[0].getModifier() );
		assertEquals("void", methods[0].getReturnType() );
		assertEquals("hello", methods[0].getName() );
		assertEquals(null, methods[0].getParameterNames() );
		assertEquals(null, methods[0].getThrownExceptions() );
		assertEquals("public final", methods[1].getModifier() );
		assertEquals("com.server.User", methods[1].getReturnType() );
		assertEquals("hello", methods[1].getName() );
		assertEquals(3, methods[1].getParameterNames().length );
		assertEquals("String", methods[1].getParameterTypes()[0] );
		assertEquals("first", methods[1].getParameterNames()[0] );
		assertEquals("Integer", methods[1].getParameterTypes()[1] );
		assertEquals("second", methods[1].getParameterNames()[1] );
		assertEquals("com.server.User", methods[1].getParameterTypes()[2] );
		assertEquals("third", methods[1].getParameterNames()[2] );
		assertEquals(2, methods[1].getThrownExceptions().length );
		assertEquals("IOException", methods[1].getThrownExceptions()[0] );
		assertEquals("EOFException", methods[1].getThrownExceptions()[1] );


		lines = new String[] {
				"package de.enough.polish.util;	 ",
				"  ",
				"	/* some explanation",
				"	   over several ",
				"	lines */import de.enough.io;",
				"",
				"public interface MyInterface extends PushListener {",
				"	void hello(String message);",
				"}"
		};
		file = new JavaSourceClass(lines);
		methods = file.getMethods();
		assertNotNull( methods );
		assertEquals(1, methods.length);
		assertEquals("", methods[0].getModifier());
		assertEquals("void", methods[0].getReturnType());
		assertEquals("hello", methods[0].getName());
		assertEquals(1, methods[0].getParameterNames().length );
		assertEquals("String", methods[0].getParameterTypes()[0]);
		assertEquals("message", methods[0].getParameterNames()[0]);
		assertEquals(null, methods[0].getThrownExceptions());
	}

	public void testGenerics()
	{
		String[] lines = new String[] {
				"package de.enough.polish.util;",
				"",
				"import java.util.Vector;",
				"",
				"public interface MyClass extends Remote {",
				"	public Vector<String> getData();",
				"	public void setData(Vector<String> data);",
				"}"
		};
		JavaSourceClass file = new JavaSourceClass(lines);
		JavaSourceMethod[] methods = file.getMethods();

		assertNotNull( methods );
		assertEquals( 2, methods.length );

		assertEquals("public", methods[0].getModifier() );
		assertEquals("Vector<String>", methods[0].getReturnType() );
		assertEquals("getData", methods[0].getName() );
		assertEquals(null, methods[0].getParameterNames() );
		assertEquals(null, methods[0].getThrownExceptions() );

		assertEquals("public", methods[1].getModifier() );
		assertEquals("void", methods[1].getReturnType() );
		assertEquals("setData", methods[1].getName() );
		assertEquals(1, methods[1].getParameterNames().length );
		assertEquals("Vector<String>", methods[1].getParameterTypes()[0] );
		assertEquals("data", methods[1].getParameterNames()[0] );
		assertEquals(null, methods[1].getThrownExceptions() );
	}
}
