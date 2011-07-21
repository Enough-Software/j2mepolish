/*
 * Created on 29-Mar-2005 at 21:22:28.
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
package de.enough.polish.swing;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JLabel;

/**
 * <p>Prvoides a button with an image that contains several areas.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        29-Mar-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ImageAreaButton 
extends JLabel
implements MouseListener
{
	private static final long serialVersionUID = 6256843509690643955L;

	private final Rectangle[] areas;
	private final ActionListener listener;
	private int selectedArea;
	private final Dimension minimumSize;

	/**
	 * @param icon
	 * @param areas
	 * @param listener
	 */
	public ImageAreaButton(Icon icon, Rectangle[] areas, ActionListener listener ) {
		super(icon);
		this.minimumSize = new Dimension( icon.getIconWidth(), icon.getIconHeight() );
		this.areas = areas;
		this.listener = listener;
		addMouseListener( this );
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		for (int i = 0; i < this.areas.length; i++) {
			Rectangle rectangle = this.areas[i];
			if (x < rectangle.x || y < rectangle.y || x > rectangle.x + rectangle.width || y > rectangle.y + rectangle.height ) {
				continue;
			}
			// found a match:
			this.selectedArea = i;
			ActionEvent event = new ActionEvent( this, i, null );
			this.listener.actionPerformed(event);
		}		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e) {
		// ignore
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e) {
		// ignore	
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
		// ignore	
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
		// ignore
	}
	
	public int getSelectedArea() {
		return this.selectedArea;
	}

	public Dimension getMinimumSize() {
		return this.minimumSize;
	}
	
	public Dimension getPreferredSize() {
		return this.minimumSize;
	}
	

}
