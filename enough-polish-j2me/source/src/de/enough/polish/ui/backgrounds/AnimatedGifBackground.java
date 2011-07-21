//#condition polish.usePolishGui && polish.api.mmapi
/*
 * Created on Nov 8, 2007 at 8:52:39 PM.
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
package de.enough.polish.ui.backgrounds;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * <p>Plays back animated gifs in GIF89a format.</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Nov 8, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class AnimatedGifBackground extends VideoBackground
{

	private byte[] data;

	/**
	 * @param url
	 * @param loopCount
	 */
	public AnimatedGifBackground(int color, String url, int loopCount, int anchor, int xOffset, int yOffset)
	{
		super(color, url, "image/gif", loopCount, anchor, xOffset, yOffset );
	}

	/**
	 * @param data
	 * @param loopCount
	 */
	public AnimatedGifBackground(int color, byte[] data, int loopCount, int anchor, int xOffset, int yOffset)
	{
		super(color, null, "image/gif", loopCount, anchor, xOffset, yOffset );
		this.data = data;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.backgrounds.VideoBackground#openInputStream()
	 */
	protected InputStream openInputStream() {
		if (this.data != null) {
			return new ByteArrayInputStream(this.data);
		}
		return super.openInputStream();
	}
	
	
}
