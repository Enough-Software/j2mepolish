/*
 * Created on Jun 2, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.enough.webprocessor;

import java.io.File;

import de.enough.webprocessor.util.StringList;
import junit.framework.TestCase;

/**
 * @author robertvirkus
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WebProcessorTaskTest extends TestCase {
	
	private WebProcessorTask processor;

	public WebProcessorTaskTest(String name) {
		super(name);
	}
	

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		this.processor = new WebProcessorTask();
		this.processor.setIncludedir( new File( "./source/test/de/enough/webrocessor" ));
		this.processor.addVariables();
	}
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		this.processor = null;
	}
	
	private void print( StringList list ) {
		System.out.println("======================");
		String[] lines = list.getArray();
		for (int i = 0; i < lines.length; i++) {
			System.out.println( lines[i] );
			
		}
		System.out.println("______________________");		
	}

	public void testInsertVar() {
		String[] lines = new String[] {
				"<%set author = Robert Virkus %> ",
				"<html>",
				"	last modified  by <%= Author %>",
				"</html>"
		};
		StringList list = new StringList( lines );
		this.processor.processStringList(  list, "TestCase.html" );
		assertEquals( "	last modified  by Robert Virkus", list.getArray()[1]);
	}
	
	public void testIclude() {
		String[] lines = new String[] {
				"<%set author = Robert Virkus %> ",
				"<html>",
				"	<%include test.txt%>",
				"</html>"
		};
		StringList list = new StringList( lines );
		this.processor.processStringList(  list, "TestCase.html" );
		//assertEquals( "	last modified  by Robert Virkus", list.getArray()[1]);
		lines = list.getArray();
		for (int i = 0; i < lines.length; i++) {
			String string = lines[i];
			System.out.println(i + ": " + string);
		}
	}

	
	public void testDefine() {
		String[] lines = new String[] {
				"<%define inBuildSection %> ",
				"<html>",
				"	2",
				"	3",
				"</html>"
		};
		StringList list = new StringList( lines );
		this.processor.processStringList(  list, "TestCase.html" );
	}
	public void testIfDef() {
		String[] lines = new String[] {
				"<%define inBuildSection %> ",
				"<html>",
				"	<%ifdef inBuildSection %>",
				"	1",
				"	2",
				"	3",
				"	<%else%>",
				"	4",
				"	<%endif%>",
				"</html>"
		};
		StringList list = new StringList( lines );
		this.processor.processStringList(  list, "TestCase.html" );
		print( list );
		assertEquals( 5, list.length() );
		
		lines = new String[] {
				"<%define inBuildSection %> ",
				"<html>",
				"	<%ifndef inBuildSection %>",
				"	1",
				"	2",
				"	3",
				"	<%elifdef inCoolSection%>",
				"	cool",
				"	<%else%>",
				"	last modified on <%= date%>",
				"	<%endif%>",
				"</html>"
		};
		list = new StringList( lines );
		this.processor.processStringList(  list, "TestCase.html" );
		print( list );
		assertEquals( 3, list.length() );

		lines = new String[] {
				"<%define inBuildSection %> ",
				"<%define inCoolSection %> ",
				"<html>",
				"	<%ifndef inBuildSection %>",
				"	1",
				"	2",
				"	3",
				"	<%elifdef inCoolSection %>",
				"	cool at <%= time %>",
				"	<%elifdef inWeirdSection %>",
				"	weird at <%= time %>",
				"	<%else %>",
				"	last modified on <%= date %>",
				"	<%endif %>",
				"</html>"
		};
		list = new StringList( lines );
		this.processor.processStringList(  list, "TestCase.html" );
		print( list );
		assertEquals( 3, list.length() );

	}
	
	public void testProcessTextFile() {
		String[] lines = new String[] {
				"<html>",
				"	last modified on <%= date %> at <%= time %>",
				"</html>"
		};
		StringList list = new StringList( lines );
		this.processor.processStringList(  list, "TestCase.html" );
		
		lines = new String[] {
				"  <%set author = Robert Virkus %> ",
				"<html>",
				"	last modified on <%= date %> at <%= time %> by <%= Author %>",
				"</html>"
		};
		list = new StringList( lines );
		this.processor.processStringList(  list, "TestCase.html" );
		print( list );
	}

}
