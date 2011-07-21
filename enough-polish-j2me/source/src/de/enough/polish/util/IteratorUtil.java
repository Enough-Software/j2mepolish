/*
 * Created on 17-Aug-2006 at 12:18:43.
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
package de.enough.polish.util;

import java.util.Vector;

/**
 * <p>Is used to traverse through maps and lists.</p>
 * <p>The iterator passes through the original internal array
 *    of the parent map/list, so no memory overhead occurs.
 * </p>
 *
 * <p>Copyright (c) 2009 Enough Software</p>
 * <pre>
 * history
 *        17-Aug-2006 - mkoch creation
 * </pre>
 * @author Michael Koch, michael.koch@enough.de
 */
public class IteratorUtil
{
  public static Iterator iterator(Object obj)
  {
    if (obj instanceof Vector)
      {
        return new VectorIterator((Vector) obj);
      }
    else
      {
        throw new RuntimeException("Type doesn't support iterators: " + obj.getClass().getName());
      }
  }
}
