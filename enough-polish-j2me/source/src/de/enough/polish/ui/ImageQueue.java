//#condition polish.images.backgroundLoad && polish.usePolishGui
/*
 * Created on 19-Apr-2004 at 15:09:38.
 *
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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

import javax.microedition.lcdui.Image;

/**
 * <p>Provides a queue for loading images in the background.</p>
 *
 * <p>Copyright Enough Software 2004 - 2009</p>

 * <pre>
 * history
 *        19-Apr-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class ImageQueue {
	public boolean cache;
	private int pos = 1;
	private ImageConsumer[] consumers = new ImageConsumer[5];
	
	public ImageQueue( ImageConsumer consumer, boolean cache ) {
		this.consumers[0] = consumer;
		this.cache = cache;
	}
	public void addConsumer( ImageConsumer consumer ) {
		if (this.pos < 5 ) {
			this.consumers[this.pos] = consumer;
			this.pos++;
		}
	}
	
	public void notifyConsumers( String name, Image image ) {
		for (int i = 0; i < this.pos; i++) {
			try {
				this.consumers[i].setImage(name, image );
			} catch (Exception e) {
				//#debug error
				System.out.println( "Unable to notify ImageConsumer about image [" + name + "]" + e );
			}
		}
	}
}
