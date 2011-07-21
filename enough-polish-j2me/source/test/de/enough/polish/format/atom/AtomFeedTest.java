/*
 * Created on Jul 13, 2010 at 3:27:01 PM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package de.enough.polish.format.atom;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;

import junit.framework.TestCase;

public class AtomFeedTest extends TestCase {

	public void testParse() throws IOException {
		AtomFeed feed = new AtomFeed();
		String document = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<?xml-stylesheet href=\"http://feeds.thumbtribe.co.za/static/c3po.static/style/thumbtribe.css\" type=\"text/css\" title=\"Thumbtribe ATOM XML\"?><!--Computer generated XML . DO NOT EDIT !!--><!--(c) Thumbtribe . C3PO--><!--Version : 1.00--><!--System Properties:"
			+ "* Operating System : Linux (amd64)"
			+ "* Java version : Sun Microsystems Inc.1.6.0_17 (1.6.0_17-b04)"
			+ "* Time zone : Africa/Johannesburg"
			+ "* Locale : en_US"
			+ "* File Encoding : UTF-8--><!--Generation Details:"
			+ "* Date : 2010-07-13T14:22:25.354+02:00"
			+ "* XSL : atom/New Newstoday/Technology News.xsl"
			+ "* XSLT : SAXON 9.1.0.6 from Saxonica--><feed xmlns=\"http://www.w3.org/2005/Atom\">"
			+ "<id>http://http://feeds.thumbtribe.co.za/sites/New_NewstodayTechnology_News</id>"
			+ "<title>New Newstoday - Technology News</title>"
			+ "<subtitle>Technology News</subtitle>"
			+ "<link href=\"http://http://feeds.thumbtribe.co.za/sites/New%20NewstodayTechnology%20News\""
			+ "rel=\"self\"/>"
			+ "<updated>2010-07-12T13:35:17+00:00</updated>"
			+ "<author>"
			+ ""
			+ "<name>Thumbtribe C3PO</name>"
			+ "<uri>http://http://feeds.thumbtribe.co.za</uri>"
			+ "<email>info@thumbtribe.co.za</email>"
			+ "</author>"
			+ "<entry>"
			+ "<id>http://http://feeds.thumbtribe.co.za/sites/New_Newstoday/Technology_News/More_bandwidth_available_after_World_Cup</id>"
			+ "<title>More bandwidth available after World Cup</title>"
			+ ""
			+ "<updated>2010-07-12T13:35:17+00:00</updated>"
			+ "<summary>More undersea cable and satellite capacity should become available from Telkom and other sources following the conclusion of the 2010 Soccer World Cup, lessening the dependence on the broken Seacom cable. Frost &amp;#38; Sullivan senior research analyst Vitalis Ozianyi says... Continue reading...</summary>"
			+ "<atom:content xmlns:atom=\"http://www.w3.org/2005/Atom\" type=\"html\">&lt;p&gt;&lt;img class=\"alignleft\" title=\"cable\" src=\"http://www.newstoday.co.za/images/stories/stories/February10/cable0222l.jpg\" alt=\"\" width=\"250\" height=\"187\" /&gt;&lt;/p&gt;"
			+ "&lt;p&gt;More undersea cable and satellite capacity should become available from Telkom and other sources following the conclusion of the 2010 Soccer World Cup, lessening the dependence on the broken Seacom cable.&lt;/p&gt;"
			+ "&lt;p&gt;Frost &amp;amp; Sullivan senior research analyst Vitalis Ozianyi says that, with the Soccer World Cup coming to an end, Telkom's commitment to carry broadcasters' signals over its SAT-3 and SA Far East cables has diminished and so more bandwidth is available.&lt;/p&gt;"
			+ ""
			+ "&lt;br /&gt; </atom:content>"
			+ "<link href=\"http://http://feeds.thumbtribe.co.za/sites/New%20Newstoday/Technology%20News/w5aab1c27\"/>"
			+ "<link href=\"http://www.newstoday.co.za/images/stories/stories/February10/cable0222l.jpg\""
			+ "rel=\"enclosure\""
			+ "type=\"image/jpeg\"/>"
			+ "</entry>"
			+ "<entry>"
			+ "<id>http://http://feeds.thumbtribe.co.za/sites/New_Newstoday/Technology_News/Cell_phone_subscriptions_now_top_5_billion</id>"
			+ "<title>Cell phone subscriptions now top 5 billion</title>"
			+ ""
			+ "<updated>2010-07-12T10:10:55+00:00</updated>"
			+ "<summary>The world now has five billion mobile subscriptions, and experts say the growth is now largely focussed on emerging markets like China, India, Nigeria and Zimbabwe. Ericsson estimates that the 5 billionth subscription was added on Thursday, July 8. This... Continue reading...</summary>"
			+ "<atom:content xmlns:atom=\"http://www.w3.org/2005/Atom\" type=\"html\">&lt;p&gt;&lt;img class=\"alignleft\" title=\"cell\" src=\"http://www.newstoday.co.za/images/stories/stories/october09/cell1013l.jpg\" alt=\"\" width=\"200\" height=\"157\" /&gt;&lt;/p&gt;"
			+ "&lt;p&gt;The world now has five billion mobile subscriptions, and experts say the growth is now largely focussed on emerging markets like China, India, Nigeria and Zimbabwe.&lt;/p&gt;"
			+ "&lt;p&gt;Ericsson estimates that the 5 billionth subscription was added on Thursday, July 8. This comes just 18 months after the 4 billion mark was reached at the end of 2008.&lt;/p&gt;"
			+ ""
			+ "&lt;p&gt;The main drivers of growth continue to be Africa and the Asia-Pacific region, which together accounted for 80 percent of global subscription net additions in the first half of 2010. Today there are 450 million mobile subscriptions in Africa as compared to the year 2000, when there were about 16 million subscriptions, less than the amount of users in Ghana today.&lt;/p&gt;"
			+ "&lt;br /&gt; </atom:content>"
			+ "<link href=\"http://http://feeds.thumbtribe.co.za/sites/New%20Newstoday/Technology%20News/w5aab1c29\"/>"
			+ "<link href=\"http://www.newstoday.co.za/images/stories/stories/october09/cell1013l.jpg\""
			+ "rel=\"enclosure\""
			+ "type=\"image/jpeg\"/>"
			+ "</entry>"
			+ "<entry>"
			+ "<id>http://http://feeds.thumbtribe.co.za/sites/New_Newstoday/Technology_News/Seacom_is_vulnerable</id>"
			+ ""
			+ "<title>Seacom is vulnerable</title>"
			+ "<updated>2010-07-10T10:04:04+00:00</updated>"
			+ "<summary>Seacom&amp;#8217;s current cable break that has all but left the undersea cable operator inoperable for a week, has raised market speculation that it may soon formalise alliances, if not an outright merger, with similar companies. This week, Seacom&amp;#8217;s vulnerability as... Continue reading...</summary>"
			+ "<atom:content xmlns:atom=\"http://www.w3.org/2005/Atom\" type=\"html\">&lt;p&gt;&lt;img class=\"alignleft\" title=\"internet\" src=\"http://www.newstoday.co.za/images/stories/stories/october09/internet1005.jpg\" alt=\"\" width=\"200\" height=\"181\" /&gt;&lt;/p&gt;"
			+ "&lt;p&gt;Seacom's current cable break that has all but left the undersea cable operator inoperable for a week, has raised market speculation that it may soon formalise alliances, if not an outright merger, with similar companies.&lt;/p&gt;"
			+ ""
			+ "&lt;p&gt;This week, Seacom's vulnerability as a single line operator was highlighted when a segment of its East African cable broke, leading to downtime of six to eight days.&lt;/p&gt;"
			+ "&lt;p&gt;Rumours that Seacom was looking at either forming a merger or formal alliance with Main One, the Nigerian-based private equity company whose cable stretching from Lagos to Portugal became operational last week, have been circulating for some time.&lt;/p&gt;"
			+ "&lt;br /&gt; </atom:content>"
			+ "<link href=\"http://http://feeds.thumbtribe.co.za/sites/New%20Newstoday/Technology%20News/w5aab1c31\"/>"
			+ "<link href=\"http://www.newstoday.co.za/images/stories/stories/october09/internet1005.jpg\""
			+ "rel=\"enclosure\""
			+ "type=\"image/jpeg\"/>"
			+ "</entry>"
			+ "</feed>";
		
		feed.parse(document);
		
		assertEquals( "http://http://feeds.thumbtribe.co.za/sites/New_NewstodayTechnology_News", feed.getId() );
		assertEquals( "New Newstoday - Technology News", feed.getTitle() );
		assertEquals( "Technology News", feed.getSubtitle() );
		assertNotNull( feed.getUpdated() );
		// 2010-07-12T13:35:17+00:00
		assertEquals( 2010, feed.getUpdated().getYear() );
		assertEquals( 2010, feed.getUpdated().getYear() );
		assertEquals( Calendar.JULY, feed.getUpdated().getMonth() );
		assertEquals( 12, feed.getUpdated().getDay() );
		assertEquals( 13, feed.getUpdated().getHour() );
		assertEquals( 35, feed.getUpdated().getMinute() );
		assertEquals( 17, feed.getUpdated().getSecond() );
		assertNotNull( feed.getUpdated().getTimeZone() );
		assertEquals( 0, feed.getUpdated().getTimeZone().getRawOffset() );
		assertNotNull( feed.getAuthor() );
		assertEquals( "Thumbtribe C3PO", feed.getAuthor().getName() );
		
		assertEquals( 3, feed.size() );
		assertEquals( 3, feed.getEntries().length );
		for (int i=0; i<feed.size(); i++) {
			AtomEntry entry = feed.getEntry(i);
			assertNotNull( entry );
		}
		
		assertEquals( "More bandwidth available after World Cup", feed.getEntry(0).getTitle() );
		assertEquals( "Cell phone subscriptions now top 5 billion", feed.getEntry(1).getTitle() );
		assertEquals( "Seacom is vulnerable", feed.getEntry(2).getTitle() );
		
		AtomEntry entry = feed.getEntries()[2];
		String content = "<p><img class=\"alignleft\" title=\"internet\" src=\"http://www.newstoday.co.za/images/stories/stories/october09/internet1005.jpg\" alt=\"\" width=\"200\" height=\"181\" /></p>"
			+ "<p>Seacom's current cable break that has all but left the undersea cable operator inoperable for a week, has raised market speculation that it may soon formalise alliances, if not an outright merger, with similar companies.</p>"
			+ ""
			+ "<p>This week, Seacom's vulnerability as a single line operator was highlighted when a segment of its East African cable broke, leading to downtime of six to eight days.</p>"
			+ "<p>Rumours that Seacom was looking at either forming a merger or formal alliance with Main One, the Nigerian-based private equity company whose cable stretching from Lagos to Portugal became operational last week, have been circulating for some time.</p>"
			+ "<br /> ";
		assertEquals( content, entry.getContent() );
		assertEquals( 1, entry.getImages().length );
		assertEquals("http://www.newstoday.co.za/images/stories/stories/october09/internet1005.jpg", entry.getImages()[0].getUrl() );
		
	}
	
	public void testSerialization() throws IOException {
		AtomFeed feed = new AtomFeed();
		String document = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<?xml-stylesheet href=\"http://feeds.thumbtribe.co.za/static/c3po.static/style/thumbtribe.css\" type=\"text/css\" title=\"Thumbtribe ATOM XML\"?><!--Computer generated XML . DO NOT EDIT !!--><!--(c) Thumbtribe . C3PO--><!--Version : 1.00--><!--System Properties:"
			+ "* Operating System : Linux (amd64)"
			+ "* Java version : Sun Microsystems Inc.1.6.0_17 (1.6.0_17-b04)"
			+ "* Time zone : Africa/Johannesburg"
			+ "* Locale : en_US"
			+ "* File Encoding : UTF-8--><!--Generation Details:"
			+ "* Date : 2010-07-13T14:22:25.354+02:00"
			+ "* XSL : atom/New Newstoday/Technology News.xsl"
			+ "* XSLT : SAXON 9.1.0.6 from Saxonica--><feed xmlns=\"http://www.w3.org/2005/Atom\">"
			+ "<id>http://http://feeds.thumbtribe.co.za/sites/New_NewstodayTechnology_News</id>"
			+ "<title>New Newstoday - Technology News</title>"
			+ "<subtitle>Technology News</subtitle>"
			+ "<link href=\"http://http://feeds.thumbtribe.co.za/sites/New%20NewstodayTechnology%20News\""
			+ "rel=\"self\"/>"
			+ "<updated>2010-07-12T13:35:17+00:00</updated>"
			+ "<author>"
			+ ""
			+ "<name>Thumbtribe C3PO</name>"
			+ "<uri>http://http://feeds.thumbtribe.co.za</uri>"
			+ "<email>info@thumbtribe.co.za</email>"
			+ "</author>"
			+ "<entry>"
			+ "<id>http://http://feeds.thumbtribe.co.za/sites/New_Newstoday/Technology_News/More_bandwidth_available_after_World_Cup</id>"
			+ "<title>More bandwidth available after World Cup</title>"
			+ ""
			+ "<updated>2010-07-12T13:35:17+00:00</updated>"
			+ "<summary>More undersea cable and satellite capacity should become available from Telkom and other sources following the conclusion of the 2010 Soccer World Cup, lessening the dependence on the broken Seacom cable. Frost &amp;#38; Sullivan senior research analyst Vitalis Ozianyi says... Continue reading...</summary>"
			+ "<atom:content xmlns:atom=\"http://www.w3.org/2005/Atom\" type=\"html\">&lt;p&gt;&lt;img class=\"alignleft\" title=\"cable\" src=\"http://www.newstoday.co.za/images/stories/stories/February10/cable0222l.jpg\" alt=\"\" width=\"250\" height=\"187\" /&gt;&lt;/p&gt;"
			+ "&lt;p&gt;More undersea cable and satellite capacity should become available from Telkom and other sources following the conclusion of the 2010 Soccer World Cup, lessening the dependence on the broken Seacom cable.&lt;/p&gt;"
			+ "&lt;p&gt;Frost &amp;amp; Sullivan senior research analyst Vitalis Ozianyi says that, with the Soccer World Cup coming to an end, Telkom's commitment to carry broadcasters' signals over its SAT-3 and SA Far East cables has diminished and so more bandwidth is available.&lt;/p&gt;"
			+ ""
			+ "&lt;br /&gt; </atom:content>"
			+ "<link href=\"http://http://feeds.thumbtribe.co.za/sites/New%20Newstoday/Technology%20News/w5aab1c27\"/>"
			+ "<link href=\"http://www.newstoday.co.za/images/stories/stories/February10/cable0222l.jpg\""
			+ "rel=\"enclosure\""
			+ "type=\"image/jpeg\"/>"
			+ "</entry>"
			+ "<entry>"
			+ "<id>http://http://feeds.thumbtribe.co.za/sites/New_Newstoday/Technology_News/Cell_phone_subscriptions_now_top_5_billion</id>"
			+ "<title>Cell phone subscriptions now top 5 billion</title>"
			+ ""
			+ "<updated>2010-07-12T10:10:55+00:00</updated>"
			+ "<summary>The world now has five billion mobile subscriptions, and experts say the growth is now largely focussed on emerging markets like China, India, Nigeria and Zimbabwe. Ericsson estimates that the 5 billionth subscription was added on Thursday, July 8. This... Continue reading...</summary>"
			+ "<atom:content xmlns:atom=\"http://www.w3.org/2005/Atom\" type=\"html\">&lt;p&gt;&lt;img class=\"alignleft\" title=\"cell\" src=\"http://www.newstoday.co.za/images/stories/stories/october09/cell1013l.jpg\" alt=\"\" width=\"200\" height=\"157\" /&gt;&lt;/p&gt;"
			+ "&lt;p&gt;The world now has five billion mobile subscriptions, and experts say the growth is now largely focussed on emerging markets like China, India, Nigeria and Zimbabwe.&lt;/p&gt;"
			+ "&lt;p&gt;Ericsson estimates that the 5 billionth subscription was added on Thursday, July 8. This comes just 18 months after the 4 billion mark was reached at the end of 2008.&lt;/p&gt;"
			+ ""
			+ "&lt;p&gt;The main drivers of growth continue to be Africa and the Asia-Pacific region, which together accounted for 80 percent of global subscription net additions in the first half of 2010. Today there are 450 million mobile subscriptions in Africa as compared to the year 2000, when there were about 16 million subscriptions, less than the amount of users in Ghana today.&lt;/p&gt;"
			+ "&lt;br /&gt; </atom:content>"
			+ "<link href=\"http://http://feeds.thumbtribe.co.za/sites/New%20Newstoday/Technology%20News/w5aab1c29\"/>"
			+ "<link href=\"http://www.newstoday.co.za/images/stories/stories/october09/cell1013l.jpg\""
			+ "rel=\"enclosure\""
			+ "type=\"image/jpeg\"/>"
			+ "</entry>"
			+ "<entry>"
			+ "<id>http://http://feeds.thumbtribe.co.za/sites/New_Newstoday/Technology_News/Seacom_is_vulnerable</id>"
			+ ""
			+ "<title>Seacom is vulnerable</title>"
			+ "<updated>2010-07-10T10:04:04+00:00</updated>"
			+ "<summary>Seacom&amp;#8217;s current cable break that has all but left the undersea cable operator inoperable for a week, has raised market speculation that it may soon formalise alliances, if not an outright merger, with similar companies. This week, Seacom&amp;#8217;s vulnerability as... Continue reading...</summary>"
			+ "<atom:content xmlns:atom=\"http://www.w3.org/2005/Atom\" type=\"html\">&lt;p&gt;&lt;img class=\"alignleft\" title=\"internet\" src=\"http://www.newstoday.co.za/images/stories/stories/october09/internet1005.jpg\" alt=\"\" width=\"200\" height=\"181\" /&gt;&lt;/p&gt;"
			+ "&lt;p&gt;Seacom's current cable break that has all but left the undersea cable operator inoperable for a week, has raised market speculation that it may soon formalise alliances, if not an outright merger, with similar companies.&lt;/p&gt;"
			+ ""
			+ "&lt;p&gt;This week, Seacom's vulnerability as a single line operator was highlighted when a segment of its East African cable broke, leading to downtime of six to eight days.&lt;/p&gt;"
			+ "&lt;p&gt;Rumours that Seacom was looking at either forming a merger or formal alliance with Main One, the Nigerian-based private equity company whose cable stretching from Lagos to Portugal became operational last week, have been circulating for some time.&lt;/p&gt;"
			+ "&lt;br /&gt; </atom:content>"
			+ "<link href=\"http://http://feeds.thumbtribe.co.za/sites/New%20Newstoday/Technology%20News/w5aab1c31\"/>"
			+ "<link href=\"http://www.newstoday.co.za/images/stories/stories/october09/internet1005.jpg\""
			+ "rel=\"enclosure\""
			+ "type=\"image/jpeg\"/>"
			+ "</entry>"
			+ "</feed>";
		
		feed.parse(document);
		
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(byteOut);
		feed.write(out);
		
		DataInputStream in = new DataInputStream( new ByteArrayInputStream( byteOut.toByteArray()));
		feed = new AtomFeed();
		feed.read(in);
		
		assertEquals( "http://http://feeds.thumbtribe.co.za/sites/New_NewstodayTechnology_News", feed.getId() );
		assertEquals( "New Newstoday - Technology News", feed.getTitle() );
		assertEquals( "Technology News", feed.getSubtitle() );
		assertNotNull( feed.getUpdated() );
		// 2010-07-12T13:35:17+00:00
		assertEquals( 2010, feed.getUpdated().getYear() );
		assertEquals( 2010, feed.getUpdated().getYear() );
		assertEquals( Calendar.JULY, feed.getUpdated().getMonth() );
		assertEquals( 12, feed.getUpdated().getDay() );
		assertEquals( 13, feed.getUpdated().getHour() );
		assertEquals( 35, feed.getUpdated().getMinute() );
		assertEquals( 17, feed.getUpdated().getSecond() );
		assertNotNull( feed.getUpdated().getTimeZone() );
		assertEquals( 0, feed.getUpdated().getTimeZone().getRawOffset() );
		assertNotNull( feed.getAuthor() );
		assertEquals( "Thumbtribe C3PO", feed.getAuthor().getName() );
		
		assertEquals( 3, feed.size() );
		assertEquals( 3, feed.getEntries().length );
		for (int i=0; i<feed.size(); i++) {
			AtomEntry entry = feed.getEntry(i);
			assertNotNull( entry );
		}
		
		assertEquals( "More bandwidth available after World Cup", feed.getEntry(0).getTitle() );
		assertEquals( "Cell phone subscriptions now top 5 billion", feed.getEntry(1).getTitle() );
		assertEquals( "Seacom is vulnerable", feed.getEntry(2).getTitle() );
		
		AtomEntry entry = feed.getEntries()[2];
		String content = "<p><img class=\"alignleft\" title=\"internet\" src=\"http://www.newstoday.co.za/images/stories/stories/october09/internet1005.jpg\" alt=\"\" width=\"200\" height=\"181\" /></p>"
			+ "<p>Seacom's current cable break that has all but left the undersea cable operator inoperable for a week, has raised market speculation that it may soon formalise alliances, if not an outright merger, with similar companies.</p>"
			+ ""
			+ "<p>This week, Seacom's vulnerability as a single line operator was highlighted when a segment of its East African cable broke, leading to downtime of six to eight days.</p>"
			+ "<p>Rumours that Seacom was looking at either forming a merger or formal alliance with Main One, the Nigerian-based private equity company whose cable stretching from Lagos to Portugal became operational last week, have been circulating for some time.</p>"
			+ "<br /> ";
		assertEquals( content, entry.getContent() );
		assertEquals( 1, entry.getImages().length );
		assertEquals("http://www.newstoday.co.za/images/stories/stories/october09/internet1005.jpg", entry.getImages()[0].getUrl() );
		
		
		// test maximum number of serialized entries:
		AtomFeed.setMaxNumberOfSerializedEntries(2);
		byteOut = new ByteArrayOutputStream();
		out = new DataOutputStream(byteOut);
		feed.write(out);
		assertEquals( 3, feed.size() );
		
		in = new DataInputStream( new ByteArrayInputStream( byteOut.toByteArray()));
		feed = new AtomFeed();
		feed.read(in);
		assertEquals( 2, feed.size() );
	}
}
