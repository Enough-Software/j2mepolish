//#condition polish.usePolishGui

/*
 * Created on 11-Jan-2006 at 19:20:28.
 * 
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
package de.enough.polish.browser.html;

import de.enough.polish.ui.Choice;
import de.enough.polish.ui.ChoiceGroup;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ArrayList;

public class HtmlSelect
{
	private final static Style NO_STYLE = new Style();
	public static final String SELECT = "select";

	private final String name;
	private int size;
	private boolean isMultiple;
	private int selectedIndex;
	private final ArrayList optionNames;
	private final ArrayList optionValues;
	private final ArrayList optionStyles;

	private Style style;

	public HtmlSelect(String name, int size, boolean isMultiple, Style style)
	{
		if (name == null) {
			name = "";
		}
		this.name = name;
		this.size = size;
		this.isMultiple = isMultiple;
		this.style = style;

		this.selectedIndex = -1;
		this.optionNames = new ArrayList();
		this.optionValues = new ArrayList();
		this.optionStyles = new ArrayList();
	}

	public String getName()
	{
		return this.name;
	}

	public String getValue(int index)
	{
		return (String) this.optionValues.get(index);
	}
	
	public void addOption(String name)
	{
		addOption(name, name, false, null);
	}

	public void addOption(String name, String value, boolean selected, Style optionStyle)
	{
		if (selected) {
			this.selectedIndex = this.optionNames.size();
		}
		this.optionNames.add(name);
		this.optionValues.add(value);
		if (optionStyle != null) {
			this.optionStyles.add(optionStyle);
		} else {
			this.optionStyles.add( NO_STYLE );
		}

	}
	
	public ChoiceGroup getChoiceGroup()
	{
		try
		{
			int choiceType = Choice.EXCLUSIVE;

			if (this.isMultiple) {
				choiceType = Choice.MULTIPLE;
			}
			else if (this.size == 1) {
				choiceType = Choice.POPUP;
			}
			
			//#style browserOption
			ChoiceGroup choiceGroup = new ChoiceGroup(null, choiceType);
			if (this.style != null) {
				choiceGroup.setStyle(this.style);
			}
			for (int i = 0; i < this.optionNames.size(); i++) {
				//#style browserOptionItem
				choiceGroup.append((String) this.optionNames.get(i), null);
			}

			if (this.selectedIndex != -1) {
				choiceGroup.setSelectedIndex(this.selectedIndex, true);
			}
			choiceGroup.setAttribute("name", this.name);
			choiceGroup.setAttribute(SELECT, this);
			return choiceGroup;
		}
		catch (Exception e)
		{
			// TODO: handle exception
			//#debug error
			System.out.println("Unable to create choice group" + e);
			return null;
		}
	}
}
