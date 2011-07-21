//#condition polish.vendor == Nokia
/*
 * Created on 10-Feb-2005 at 19:54:06.
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
import javax.microedition.lcdui.Graphics;

import com.nokia.mid.ui.FullCanvas;

/**
 * <p>Adds information about the fullcanvas.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        10-Feb-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class NokiaFullCanvasTest extends FullCanvas implements DynamicTest {

	private DynamicTestView view;
	/**
	 * Creates a new test
	 */
	public NokiaFullCanvasTest() {
		super();
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
	 */
	protected void paint(Graphics g) {
		if (this.view != null) {
			this.view.paint(g);
		}
	}

	/* (non-Javadoc)
	 * @see com.grimo.me.product.midpsysinfo.DynamicTest#addTestResults(javax.microedition.lcdui.Form)
	 */
	public void addTestResults(InfoCollector collector ) {
		//collector.addInfo( "FullCanvasWidth(Nokia): ", "" + getWidth() );
		//collector.addInfo( "FullCanvasHeight(Nokia): ", "" + getHeight() );
		collector.addInfo( "FullCanvasSize: ", "" + getWidth() + "x" + getHeight() );
	}
	
	/* (non-Javadoc)
	 * @see com.grimo.me.product.midpsysinfo.DynamicTest#registerView(com.grimo.me.product.midpsysinfo.DynamicTestView)
	 */
	public void setView(DynamicTestView view) {
		this.view = view;
	}
	
	

	protected void keyPressed(int keyCode ) {
		if (this.view != null) {
			this.view.keyPressed(keyCode);
		}
	}
	protected void keyReleased(int keyCode) {
		if (this.view != null) {
			this.view.keyReleased(keyCode);
		}
	}
	protected void keyRepeated(int keyCode) {
		if (this.view != null) {
			this.view.keyRepeated(keyCode);
		}
	}

	/* (non-Javadoc)
	 * @see com.grimo.me.product.midpsysinfo.DynamicTest#getDisplayable()
	 */
	public Displayable getDisplayable() {
		return this;
	}

}
