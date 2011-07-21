//#condition polish.usePolishGui
/*
 * Created on Jul 8, 2008 at 5:10:39 PM.
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
package de.enough.polish.ui.rgbfilters;

import de.enough.polish.ui.Dimension;
import de.enough.polish.ui.RgbFilter;
import de.enough.polish.ui.Style;
import de.enough.polish.util.RgbImage;

/**
 * <p>Transforms the color of a specified RGB image.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 * @author Ovidiu Iliescu
 */
public class GrayscaleRgbFilter extends RgbFilter {

    protected Dimension grayscale;
    protected transient RgbImage output;

    /**
     * Creates a new grayscale filter
     */
    public GrayscaleRgbFilter() {
        // just create a new instance
    }

    /* (non-Javadoc)
     * @see de.enough.polish.ui.RgbFilter#isActive()
     */
    public boolean isActive() {
        boolean isActive = false;
        if (this.grayscale != null) {
            isActive = (this.grayscale.getValue(1024) != 0);
        }
        return isActive;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.ui.RgbFilter#process(de.enough.polish.util.RgbImage)
     */
    public RgbImage process(RgbImage input) {

        if (!isActive()) {
            return input;
        }
        if (this.output == null || this.output.getWidth() != input.getWidth() || this.output.getHeight() != input.getHeight()) {
            this.output = new RgbImage(input.getWidth(), input.getHeight());
        }
        int[] rgbInput = input.getRgbData();
        int[] rgbOutput = this.output.getRgbData();

        /**
         * We will implement a fast (de)saturation algorithm using matrix multiplication.
         *
         * To make things even faster, we will use bitshifts wherever possible.
         * To support this, we will "redefine" 1 in the original formulas
         * as being 1024 ( 2 ^ 10 ). We will modify all the formulas accordingly.
         */
        int saturation = 1024 - this.grayscale.getValue(1024);

        int alpha, red, green, blue;
        int output_red, output_green, output_blue;

        // We will use the standard NTSC color quotiens, multiplied by 1024
        // in order to be able to use integer-only math throughout the code.
        int RW = 306; // 0.299 * 1024
        int RG = 601; // 0.587 * 1024
        int RB = 117; // 0.114 * 1024

        // Define and calculate matrix quotients
        final int a, b, c, d, e, f, g, h, i;
        a = (1024 - saturation) * RW + saturation * 1024;
        b = (1024 - saturation) * RW;
        c = (1024 - saturation) * RW;
        d = (1024 - saturation) * RG;
        e = (1024 - saturation) * RG + saturation * 1024;
        f = (1024 - saturation) * RG;
        g = (1024 - saturation) * RB;
        h = (1024 - saturation) * RB;
        i = (1024 - saturation) * RB + saturation * 1024;

        int pixel = 0;
        for (int p = 0; p < rgbOutput.length; p++) {
            pixel = rgbInput[p];
            alpha = (0xFF000000 & pixel);
            red = (0x00FF & (pixel >> 16));
            green = (0x0000FF & (pixel >> 8));
            blue = pixel & (0x000000FF);

            // Matrix multiplication
            output_red = ((a * red + d * green + g * blue) >> 4) & 0x00FF0000;
            output_green = ((b * red + e * green + h * blue) >> 12) & 0x0000FF00;
            output_blue = (c * red + f * green + i * blue) >> 20;

            rgbOutput[p] = alpha | output_red | output_green | output_blue;
        }

        return this.output;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.ui.RgbFilter#setStyle(de.enough.polish.ui.Style, boolean)
     */
    public void setStyle(Style style, boolean resetStyle) {
        super.setStyle(style, resetStyle);
        //#if polish.css.filter-grayscale-grade
        Dimension grayscaleInt = (Dimension) style.getObjectProperty("filter-grayscale-grade");
        if (grayscaleInt != null) {
            this.grayscale = grayscaleInt;
        }
        //#endif
    }

    /* (non-Javadoc)
     * @see de.enough.polish.ui.RgbFilter#releaseResources()
     */
    public void releaseResources() {
        this.output = null;
    }
}
