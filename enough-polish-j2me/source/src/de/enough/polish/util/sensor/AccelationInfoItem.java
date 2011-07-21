//#condition polish.usePolishGui && polish.api.sensor

/*
 * Created on Mar 15, 2008 at 10:33:32 AM.
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
package de.enough.polish.util.sensor;

//#if polish.LibraryBuild
	import de.enough.polish.ui.FakeStringCustomItem;
//#else
	//# import de.enough.polish.ui.StringItem;
//#endif
import de.enough.polish.ui.Style;

/**
 * <p>Shows information about the current acceleration</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class AccelationInfoItem
//#if polish.LibraryBuild
	extends FakeStringCustomItem
//#else
	//# extends StringItem
//#endif
	implements AccelerationListener
{

	/**
	 */
	public AccelationInfoItem()
	{
		this(null, null);
	}

	/**
	 * @param label
	 */
	public AccelationInfoItem(String label)
	{
		this(label, null);
	}
	
	/**
	 */
	public AccelationInfoItem(Style style)
	{
		this(null, style);
	}

	/**
	 * @param label
	 */
	public AccelationInfoItem(String label, Style style)
	{
		super(label, "<waiting for data>", style);
		AccelerationUtil.addAccelerationListener(this);
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.util.sensor.AccelerationListener#notifyAcceleration(int, int, int, int, int, int, int, int, int)
	 */
	public void notifyAcceleration(int x, int minimumX, int maximumX, int y, int minimumY, int maximumY, int z, int minimumZ, int maximumZ)
	{
		setText("x=" + x + ", y=" + y + ", z=" + z);
	}

}
