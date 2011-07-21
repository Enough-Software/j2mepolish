//#condition polish.midp || polish.usePolishGui
/*
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

package de.enough.polish.processing;

import javax.microedition.lcdui.Image;

/**
 * Placeholder class that emulates the behavior of a Mobile Processing PImage, for compatibility with existing Mobile Processing scripts.
 *
 * @author Ovidiu Iliescu
 */

public class PImage {

    private Image img = null ;
    
    /**
     * Creates a new, empty PImage
     * @param width the width of the image
     * @param height the height of the image
     */
    public PImage(int width, int height)
    {
        img = Image.createImage(width, height);
    }

    /**
     * Creates a new PImage based on an existing image
     * @param img the existing image
     */
    public PImage(Image img)
    {
        this.img = img;
    }

    /**
     * Creates a new PImage based on an existing byte array containing the image data
     * @param data
     */
    public PImage(byte[] data)
    {
        img = Image.createImage(data, 0, data.length);
    }

    /**
     * Returns the native image object
     * @return the native image object
     */
    public Image getImage()
    {
        return img;
    }

    /**
     * Returns the image width
     * @return the image width
     */
    public int getWidth()
    {
        return img.getWidth();
    }

    /**
     * Returns the image height
     * @return the image height
     */
    public int getHeight()
    {
        return img.getHeight();
    }

}
