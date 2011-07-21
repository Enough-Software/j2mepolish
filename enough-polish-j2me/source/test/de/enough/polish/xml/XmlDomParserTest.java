package de.enough.polish.xml;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import junit.framework.TestCase;

/*
 * Created on Jun 4, 2008 at 12:11:41 PM.
 * 
 * Copyright (c) 2010 Robert Virkus / Enough Software
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

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class XmlDomParserTest extends TestCase
{
	
	public void testParse() {
		String document = "<rootelement><childnode x=\"10\">textvalue10</childnode><childnode x=\"20\">textvalue20</childnode></rootelement>";
		XmlDomNode root = XmlDomParser.parseTree(document);
		System.out.println("root " + root.getName() + ", has " + root.getChildCount() + " children");
		// indirect access:
		for (int i=0; i < root.getChildCount(); i++ ) {
			XmlDomNode child = root.getChild(i);
			System.out.println("child: " + child.getName() );
			System.out.println("child: " + child.getName() + ", x=" + child.getAttribute("x") + ", text=" + child.getText() );
		}
		// direct access:
		XmlDomNode child = root.getChild("childnode");		
		System.out.println("first child: " + child.getName() + ", x=" + child.getAttribute("x") + ", text=" + child.getText() );
	}
	
	public void testPerformance() throws IOException {
		int repeat = 1000;
		long startTime = System.currentTimeMillis();
		//XmlDomParser.parseTree(getClass().getResourceAsStream("/source/test/de/enough/polish/xml/test.html"));
		for (int i=0; i<repeat; i++) {
			XmlDomParser.parseTree(getClass().getResourceAsStream("test.html"));
		}
		System.out.println("XmlDomNode parsing took " + (System.currentTimeMillis() - startTime) + " ms");
		
		startTime = System.currentTimeMillis();
		//SimplePullParser parser = new XmlPullParser(new InputStreamReader(getClass().getResourceAsStream("/source/test/de/enough/polish/xml/test.html")));
		for (int i=0; i<repeat; i++) {
			SimplePullParser parser = new XmlPullParser(new InputStreamReader(getClass().getResourceAsStream("test.html")));
			while (parser.next() != SimplePullParser.END_DOCUMENT)
		    {
		      if (parser.getType() == SimplePullParser.START_TAG
		          || parser.getType() == SimplePullParser.END_TAG)
		      {
		        boolean openingTag = parser.getType() == SimplePullParser.START_TAG;
		      }
		      
		    }
		}
		System.out.println("SimplePullParser parsing took " + (System.currentTimeMillis() - startTime) + " ms");
	}
	
	public void testGetChildren() {
		String document = "<P>(BANG) - <P>Ali Larter is expecting her first child."
			+ "</P><P>The 'Heroes' actress - who married stand-up comedian Hayes McArthur last August - will reportedly welcome the new addition to her family into the world this winter."
			+ "</P><P>Her spokesperson confirmed: \"Ali and Hayes are thrilled to be expecting their first child.\" "
			+ "</P><P>Ali, 34, has previously admitted that she was desperate to settle down and have children with Hayes from the first time they started dating."
			+ "</P><P>Speaking after their engagement in 2007, she confessed: \"I told Hayes after three weeks that I wanted to marry him and that we could do it tomorrow. I look forward to the time that I'm at home with babies."
			+ "</P><P>\"He has brought light to my life. I feel lucky every morning when I wake up and see him. Not everybody is fortunate enough to find their soul mate in life.\""
			+ "</P><P>Ali has also previously admitted she ruined several relationships before she met Hayes because she was so obsessed with getting married."
			+ "</P><P>She <a href=\"http://test.com?x=10&amp;y=11\" >said:</a> \"Since I was 15 years old, all I wanted was to find the guy I was going to marry. My heart got broken so many times because I put so much pressure on it. And then I got blindsided." 
			+ "</P><P>\"Hayes showed me the way, and all was right. This is how it's supposed to be.\""
			+ "</P><P></P> (C) BANG Media International</P> "
            + "<br/> "
            + "<xoom><a  href=\"http://m.cosmopolitan.co.za/pl/svt/si/ttcosmo/po/thumbtribe/pa/157881\" >xx</a><a>yy</a></xoom>"
            + "<div>" 
            + "<a href=\"http://m.cosmopolitan.co.za/pl/svt/si/ttcosmo/po/thumbtribe/pa/157881\" >Read more </a><a href=\"http://test.com?x=10&amp;y=11\" >testing </a>"
            + "</div>"
	        + "<br/> "
	        + "<a  href=\"http://m.cosmopolitan.co.za/pl/svt/si/ttcosmo/po/thumbtribe/pa/157881\" >xx</a><a>yy</a>";
		XmlDomNode root = XmlDomParser.parseTree(document);
		print( root, "" );
		XmlDomNode[] children = root.getChildren("a");
		assertEquals( 2, children.length );
		children = root.getChildren("P");
		assertEquals( 1, children.length );
		children = root.getChildren("a", true);
		assertEquals( 7, children.length );
	}

	void print( XmlDomNode node, String start ) {
		System.out.println(start + node.getName()+ ": " + node.getText());
		for (int i=0; i<node.getChildCount(); i++) {
			XmlDomNode child = node.getChild(i);
			print( child, start + " ");
		}
	}
}
