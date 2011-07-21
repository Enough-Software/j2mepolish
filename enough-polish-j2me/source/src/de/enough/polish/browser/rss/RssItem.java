package de.enough.polish.browser.rss;
/*
 * Copyright (c) 2009 - 2009 Michael Koch / Enough Software
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
 * Encapsulates an item within an RSS feed.
 */
public class RssItem
{
	/** key for item attribute */
	public static final String ATTRIBUTE_KEY = "RSS_ITEM";
	
	private String title;
	private String description;
	private String link;
	
	public RssItem(String title, String description, String link)
	{
		this.title = title;
		setDescription( description );
		this.link = link;
	}

	public String getDescription()
	{
		return this.description;
	}

	public void setDescription(String description)
	{
		StringBuffer buffer = new StringBuffer(description.length());
		boolean isInTag = false;
		for (int i=0; i<description.length(); i++) {
			char c = description.charAt(i);
			if (isInTag) {
				if (c == '>') {
					isInTag = false;
				}
			} else if (c == '<') {
				isInTag = true;
			} else {
				buffer.append(c);
			}
		}
		this.description = buffer.toString();
	}

	public String getTitle()
	{
		return this.title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getLink()
	{
		return this.link;
	}

	public void setLink(String link)
	{
		this.link = link;
	}
}
