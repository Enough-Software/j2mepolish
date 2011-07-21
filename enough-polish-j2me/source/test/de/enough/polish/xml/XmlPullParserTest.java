package de.enough.polish.xml;

import java.io.IOException;

import de.enough.polish.io.StringReader;
import junit.framework.TestCase;

public class XmlPullParserTest extends TestCase {
	
	public void testLinux() throws IOException {
		String textext = "Hello\n\n\n\nWorld";
		doTestXmlPullParser(textext);
	}

	public void testMac() throws IOException {
		String text = "Hello\r\r\r\rWorld";
		doTestXmlPullParser(text);
	}

	public void testWindows() throws IOException {
		String text = "Hello\r\n\r\n\r\nWorld";
		doTestXmlPullParser(text);
	}
	
	private void doTestXmlPullParser(String text) throws IOException {
		String document1 = "<root>"+text+"</root>";
		
		XmlPullParser xmlPullParser = new XmlPullParser(new StringReader(document1));
		// Start document
		xmlPullParser.next();
		// Root element
		xmlPullParser.next();
		assertEquals(text,xmlPullParser.getText());
	}
}
