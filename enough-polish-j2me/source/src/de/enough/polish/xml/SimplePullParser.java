/*
 * Created on 11-Jan-2006 at 19:20:28.
 * 
 * Copyright (c) 2009 Michael Koch / Enough Software
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
package de.enough.polish.xml;

/**
 * This interface represents a simple pull parser which can be used in many applications.
 * The J2ME Polish browser component makes heavy use of it.
 * 
 * @see de.enough.polish.browser.Browser
 */
public interface SimplePullParser
{
  /**
   * Return value of getType before first call to next()
   */
  int START_DOCUMENT = 0;

  /**
   * Signal logical end of xml document
   */
  int END_DOCUMENT = 1;

  /**
   * Start tag was just read
   */
  int START_TAG = 2;

  /**
   * End tag was just read
   */
  int END_TAG = 3;

  /**
   * Text was just read
   */
  int TEXT = 4;

  int CDSECT = 5;

  int ENTITY_REF = 6;

  int LEGACY = 999;

  int getType();

  /**
   * Retrieves the text at the current position in the data the parser acts on.
   * 
   * @return the text
   */
  String getText();

  /**
   * Retrieves the name of a tag.
   * 
   * @return the name
   */
  String getName();

  /**
   * Retrieves the number of attributes associated with the current tag.
   * 
   * @return the number of attributes
   */
  int getAttributeCount();

  /**
   * Retrieves the name of an attribute associated with the current tag.
   * 
   * @param index the index of the attribute
   * @return the name
   */
  String getAttributeName(int index);

  /**
   * Retrieves the value of an attribute associated with the current tag.
   * 
   * @param index the index of the attribute
   * @return the value
   */
  String getAttributeValue(int index);

  /**
   * Retrieves the value of an attribute associated with the current tag.
   * 
   * @param name the name of the attribute
   * @return the value, or null of attribute doesn't exist
   */
  String getAttributeValue(String name);

  /**
   * Reads the input data up to the next tag or text section. It returns
   * the type of data the next block represents.
   * 
   * @return the data type
   */
  int next();

}