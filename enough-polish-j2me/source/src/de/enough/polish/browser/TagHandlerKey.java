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

/**
 * Internal class used when registering tag handlers.
 *
 * @see TagHandler
 */
class TagHandlerKey
{
  public String tagName;
  public String attributeName;
  public String attributeValue;
  
  public TagHandlerKey(String tagName)
  {
    this.tagName = tagName;
  }

  public TagHandlerKey(String tagName, String attributeName, String attributeValue)
  {
    this.tagName = tagName;
    this.attributeName = attributeName;
    this.attributeValue = attributeValue;
  }
  
  public boolean equals(Object obj)
  {
    if (obj instanceof TagHandlerKey)
    {
      TagHandlerKey other = (TagHandlerKey) obj;
      
      if (this.attributeName == null && other.attributeName == null)
      {
        return (this.tagName.equals(other.tagName));
      }
      else if (this.attributeName != null)
      {
        return (this.tagName.equals(other.tagName)
                && this.attributeName.equals(other.attributeName)
                && this.attributeValue.equals(other.attributeValue));
      }
      else
      {
        return false;
      }
    }
    
    return false;
  }
  
  public int hashCode()
  {
    if (this.attributeName == null)
    {
      return this.tagName.hashCode();
    }
    
    return (this.tagName.hashCode()
            ^ this.attributeName.hashCode()
            ^ this.attributeValue.hashCode());
  }
  
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append(this.tagName);
    sb.append(":");
    sb.append(this.attributeName);
    sb.append(":");
    sb.append(this.attributeValue);
    return sb.toString();
  }
}