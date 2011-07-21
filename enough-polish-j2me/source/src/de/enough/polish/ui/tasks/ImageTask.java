//#condition polish.usePolishGui
/*
 * Created on 05-Jan-2004 at 22:27:59.
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
package de.enough.polish.ui.tasks;

import java.io.IOException;
import java.util.TimerTask;

import javax.microedition.lcdui.Image;

import de.enough.polish.ui.StyleSheet;

/**
 * <p>Loads an Image in the background using a Timer.</p>
 * <p>The user needs to schedule this task at the timer himself/herself.</p>
 *
 * @author Robert Virkus, robert@enough.de
 * <pre>
 * history
 *        05-Jan-2004 - rob creation
 * </pre>
 */
public class ImageTask extends TimerTask {
	private String url;

	/**
	 * Creates a new ImageTask.
	 * 
	 * @param url the URL of the image, e.g. "/background.png"
	 */
	public ImageTask(String url ) 
	{
		this.url = url;
	}

	/**
	 * tries to load the image. 
	 */
	public void run() {
		//#debug
		System.out.println( "ImageTask: loading image [" + this.url + "].");
		
		try {
			//#ifdef polish.classes.ImageLoader:defined
				//#= Image image = ${polish.classes.ImageLoader}.loadImage( this.url );
			//#else
				Image image = Image.createImage( this.url );
			//#endif
			//#ifdef polish.images.backgroundLoad
			StyleSheet.notifyImageConsumers(this.url, image);
			//#endif
		} catch (IOException e) {
			//#debug error
			System.out.println( "ImageTask: unable to load image [" + this.url + "]." + e);
		} catch (Throwable e) {
			//#debug fatal
			System.out.println( "ImageTask: unable to set image [" + this.url + "]." + e);
		}

	}

}
