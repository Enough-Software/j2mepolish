//#condition polish.usePolishGui
/*
 * Created on May 13, 2009 at 1:13:04 PM.
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
package de.enough.polish.ui;

/**
 * Helper class for debugging purposes
 * @author Andre Schmidt
 */
public class DebugHelper {
	/**
	 * Calls trace() if the item is a StringItem and the text of the StringItem
	 * contains the specified text
	 * @param item the item
	 * @param text the text
	 */
	public static boolean breakOn(Item item, String text, DebugCallback callback)
	{
		if(item instanceof StringItem)
		{
			StringItem stringItem = (StringItem)item;
			if(stringItem.getText() != null && stringItem.getText().indexOf(text) != -1)
			{
				callback(callback);
				return true;
			}
		}
		else
		{
			if(item.getLabel() != null && item.getLabel().indexOf(text) != -1)
			{
				callback(callback);
				return true;
			}
		}
			
		
		return false;
	}
	
	public static boolean breakOn(int value, int equals, DebugCallback callback)
	{
		if(value == equals)
		{
			callback(callback);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Calls trace() if the label of the command stated equals the specified text
	 * @param cmd the command
	 * @param text the text
	 */
	public static boolean breakOn(Command cmd, String text, DebugCallback callback)
	{
		if(cmd.getLabel().equals(text))
		{
			callback(callback);
			return true;
		}
		
		return false;
	}
	
	public static boolean breakOn(Style style, String name, DebugCallback callback)
	{
		if(style.name != null && style.name.indexOf(name) != -1)
		{
			callback(callback);
			return true;
		}
		
		return false;
	}
	
	static void callback(DebugCallback callback) 
	{
		if(callback != null)
		{
			callback.onDebug();
		}
		else
		{
			System.out.println("BREAK");
		}
	}
	
	/**
	 * Prints a stacktrace 
	 */
	public static void trace()
	{
		try{
			throw new RuntimeException();
		}
		//#if polish.blackberry
		//# catch(Throwable e)
		//#else
		catch(Exception e)
		//#endif
		{
			e.printStackTrace();
		}
	}
	
	public static void print(Item item) {
		if(item instanceof Container) {
			print((Container)item);
		}
		else {
			printItem(item,"");
		}
	}
	
	public static void print(Container container) {
		print(container,0);
	}
	
	static String getIndent(int depth) {
		String indent = "";
		
		for (int i = 0; i < depth; i++) {
			indent += "  ";
		}
		
		return indent;
	}
	
	static void print(Container container, int depth) {
		printItem(container,getIndent(depth));
		for (int i = 0; i < container.size(); i++) {
			Item item = container.get(i);
			printItem(item,getIndent(depth));
			if(item instanceof Container) {
				depth++;
				print((Container)item,depth);
			}
		}
	}
	
	static void printItem(Item item, String indent) {
		System.out.println(indent + item);
	}
}
