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
package de.enough.polish.browser;

import de.enough.polish.ui.Command;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.util.HashMap;
import de.enough.polish.xml.SimplePullParser;

/**
 * This class provides the basic functionality for tag handlers. A tag handler is a
 * responsible for handling domain-specific tags in the browser. E.g. you can
 * support different micro formats in HTML with this.
 * 
 * @see Browser
 */
public abstract class TagHandler
{
  private HashMap commandsByKey;

  /**
   * Register a given browser object for this TagHandler. This method
   * is used to make a handler object aware of a browser in case the handler
   * needs to call methods of the browser during its work.
   *  
   * @param browser the browser to register.
   */
  public abstract void register(Browser browser);
  
  /**
   * @param parentItem the container item the put the browser items into 
   * @param parser the parser
   * @param tagName the name of the tag to handle
   * @param opening <code>true</code> if this is an opening tag, <code>false</code> otherwise
   * @param attributeMap all attributes for the tag
   * @param style the style for the next item, can be null
   * @return <code>true</code> if the tag was handled, <code>false</code> otherwise
   */
  public abstract boolean handleTag(Container parentItem, SimplePullParser parser, String tagName, boolean opening, HashMap attributeMap, Style style);
  

  /**
   * Handles all commands this tag handler is responsible for.
   * 
   * @param command the command to handle
   * 
   * @return <code>true</code> if the command was handled, <code>false</code> otherwise
   */
  public boolean handleCommand(Command command)
  {
    return false;
  }

	/**
   * Adds a command to a specific tag.
   * 
	 * @param tagName the tag to add the command to
	 * @param command the command to add
	 */
	public void addTagCommand(String tagName, Command command)
	{
		addCommandImpl(tagName, command);
	}

	/**
   * Adds a command to a specific tag/attribute combination.
   * 
	 * @param tagName the tag to add the command to
	 * @param attributeName the attribute name to add the command to
	 * @param attributeValue the attribute value to add the command to
	 * @param command the command to add
	 */
	public void addAttributeCommand(String tagName, String attributeName, String attributeValue, Command command)
	{
		if (tagName != null && attributeName != null && attributeValue != null)
		{
			addCommandImpl( tagName + attributeName + attributeValue, command );
		}
		else if (attributeName != null && attributeValue != null)
		{
			addCommandImpl( tagName + attributeName + attributeValue, command );
		}
	}

	/**
	 * @param key
	 * @param command
	 */
	private void addCommandImpl(String key, Command command)
	{
		Command[] existing = null;
	
		if (this.commandsByKey == null)
		{
			this.commandsByKey = new HashMap();
		}
		else
		{
			existing = (Command[]) this.commandsByKey.get( key );
		}
		
		Command[] newValue;
		
		if (existing == null)
		{
			newValue = new Command[]{ command };			
		}
		else
		{
			newValue = new Command[ existing.length + 1 ];
			System.arraycopy( existing, 0, newValue, 0, existing.length );
			newValue[ existing.length ] = command;
		}
		
		this.commandsByKey.put( key, newValue );
	}

	/**
	 * Adds registered commands to the given item.
	 *  
	 * @param tagName the name of the tag, e.g. div
	 * @param item the item used for visualising the tag
	 */
	protected void addCommands( String tagName, Item item ) {
		addCommandsImpl( tagName, item );
	}


	/**
	 * Adds registered commands to the given item.
	 *  
	 * @param tagName the name of the tag, e.g. div (can be null)
	 * @param attributeName the name of the attribute, e.g. class (can be null)
	 * @param attributeValue the value of the attribute, e.g. address (can be null)
	 * @param item the item used for visualising the tag
	 */
	protected void addCommands(String tagName, String attributeName, String attributeValue, Item item)
	{
		// register tag commands:
		if (tagName != null)
		{
			addCommandsImpl( tagName, item );
		}
		
		// register attribute commands:
		if (tagName != null && attributeName != null && attributeValue != null)
		{
			addCommandsImpl(tagName + attributeName + attributeValue, item);
		}
		else if (attributeName != null && attributeValue != null)
		{
			addCommandsImpl(tagName + attributeName + attributeValue, item);
		}
	}
	
	/**
	 * Adds registered commands to the given item.
	 *  
	 * @param key the name of the key, e.g. div
	 * @param item the item used for visualising the tag
	 */
	private void addCommandsImpl(String key, Item item)
	{
		if (this.commandsByKey == null)
		{
			return;
		}

		Command[] commands = (Command[]) this.commandsByKey.get(key);

		if (commands != null)
		{
			for (int i = 0; i < commands.length; i++)
			{
				Command command = commands[i];
				item.addCommand(command);
			}
		}
	}
	
	/**
	 * Retrieves registered commands for the specified tag.
	 * 
	 * @param tag the name of the tag
	 * @return null when there are no registered commands, otherwise an array of commands registered for the specified tag
	 */
	protected Command[] getCommandsForTag( String tag )
	{
		return (Command[]) this.commandsByKey.get(tag);
	}
	
	
	/**
	 * Trims the given text.
	 * @param text the input text
	 * @return the given text without spaces and newline-breaks at the beginning and/or end
	 */
	protected String trim( String text) {
		if (text == null || text.length() == 0) {
			return text;
		}
		int length = text.length();
		StringBuffer buffer = new StringBuffer(length);
		for (int i=length; --i >= 0; ) {
			char c = text.charAt(i);
			if (c <= ' ' || c == '\t' || c == '\n') {
				length--;
			} else {
				break;
			}
		}
		boolean isAtBeginning = true;
		for (int i=0; i<length; i++) {
			char c = text.charAt(i);
			if (isAtBeginning) {
				if (c <= ' ' || c == '\t' || c == '\n') {
					continue;
				} else {
					isAtBeginning = false;
				}
			}
			buffer.append(c);
		}
		return buffer.toString();
	}
}
