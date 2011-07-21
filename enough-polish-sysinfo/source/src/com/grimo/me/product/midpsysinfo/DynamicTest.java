/*
 * Created on 10-Feb-2005 at 19:51:57.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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
package com.grimo.me.product.midpsysinfo;

import javax.microedition.lcdui.Displayable;


/**
 * <p>Adds information that is assembled during runtime to a form.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        10-Feb-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public interface DynamicTest {
	
	/**
	 * Adds the results of the test to the collector.
	 * 
	 * @param collector the collector that will receive the test results.
	 */
	public void addTestResults( InfoCollector collector );
	
	/**
	 * Registers the given view for the test.
	 * 
	 * @param view the view that wants to receive events and wants to paint the test.
	 */
	public void setView( DynamicTestView view );
	
	public int getWidth();
	
	public int getHeight();
	
	public void repaint();
	
	public Displayable getDisplayable();

}
