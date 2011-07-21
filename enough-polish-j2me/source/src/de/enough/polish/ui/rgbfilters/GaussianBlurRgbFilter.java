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
 * <p>
 * Blurs an image.
 * </p>
 *
 * <p>
 * Copyright Enough Software 2008
 * </p>
 *
 * @author Nagendra Sharma, nagendra@prompttechnologies.net
 * @author Robert Virkus, j2mepolish@enough.de (blur animation & fixes)
 * @author Ovidiu Iliescu
 */
public class GaussianBlurRgbFilter extends RgbFilter {

    protected Dimension blur;
    protected transient RgbImage output;
    int width, height;

    /**
     * Creates a new GaussianBlurRgb Filter
     */
    public GaussianBlurRgbFilter() {
        // just create a new instance
    }

    /*
     * (non-Javadoc)
     *
     * @see de.enough.polish.ui.RgbFilter#isActive()
     */
    public boolean isActive() {
        if (this.blur == null) {
            return false;
        }
        return (this.blur.getValue(255) != 0);
    }

    private RgbImage horizontalPass(RgbImage input) {
        if (!isActive()) {
            return input;
        }

        int[] rgbInput = input.getRgbData();
        int[] rgbOutput = this.output.getRgbData();

        // Define all variables here instead of inside the loops.
        int red = 0;
        int green = 0;
        int blue = 0;
        int totalPercentage = 100;
        int startY = 0;
        int endY = 0;
        int x = 0, y = 0, c = 0, percentage = 0;
        int yTimesWidth = 0;
        int tempEnd = 0;
        int dimensionDivTwo = 0;
        int imgWidth = this.width;
        int imgHeight = this.height;

        int arraySize = imgHeight + 1;
        int[] redArray = new int[arraySize];
        int[] greenArray = new int[arraySize];
        int[] blueArray = new int[arraySize];
        int[] alphaArray = new int[arraySize];

        int dimension = this.blur.getValue(100);
        dimension = Math.max(2, (imgHeight * dimension) / 100);
        dimensionDivTwo = dimension / 2;

        // HORIZONTAL PASS

        yTimesWidth = 0;
        x = 0;
        while (x < imgWidth) {

            // Calculate the dynamic array for the current column

            // First, initialize the first element
            redArray[0] = 0; // Red channel sum
            greenArray[0] = 0; // Green channel sum
            blueArray[0] = 0; // Blue channel sum
            alphaArray[0] = 0; // Alpha channel sum

            // Then, calculate the rest
            yTimesWidth = 0;
            y = 0;
            while (y < imgHeight) {
                //System.out.println( imgWidth + " " + imgHeight + " " + x + " " + y + " " + (x + yTimesWidth) + " " + rgbOutput.length);
                c = rgbInput[x + yTimesWidth];
                percentage = c >>> 25;
                redArray[y + 1] = redArray[y] + ((((c & 0x00ff0000) >> 16)) * percentage);
                greenArray[y + 1] = greenArray[y] + ((((c & 0x0000ff00) >> 8)) * percentage);
                blueArray[y + 1] = blueArray[y] + (((c & 0x000000ff)) * percentage);
                alphaArray[y + 1] = alphaArray[y] + percentage;
                yTimesWidth += imgWidth;
                y++;
            }

            yTimesWidth = 0;

            // Use dynamic programming to fill out the corresponding column of the output image

            // First, fill in from 0 to dimension/2
            tempEnd = dimension / 2;
            y = 0;
            while (y < tempEnd) {
                startY = 0;
                endY = y + tempEnd;

                totalPercentage = alphaArray[endY] - alphaArray[startY] + 1;
                red = ((redArray[endY] - redArray[startY]) / totalPercentage) << 16;
                green = ((greenArray[endY] - greenArray[startY]) / totalPercentage) << 8;
                blue = ((blueArray[endY] - blueArray[startY]) / totalPercentage);

                rgbOutput[x + yTimesWidth] = (rgbInput[x + yTimesWidth] & 0xff000000) | red | green | blue;
                yTimesWidth += imgWidth;
                y++;
            }

            // Next, fill in from dimension/2 to imageSize-dimension/2
            tempEnd = imgHeight - dimension / 2;
            y = dimensionDivTwo;
            while (y < tempEnd) {
                startY = y - dimensionDivTwo + 1;
                endY = y + dimensionDivTwo;

                totalPercentage = alphaArray[endY] - alphaArray[startY] + 1;
                red = ((redArray[endY] - redArray[startY]) / totalPercentage) << 16;
                green = ((greenArray[endY] - greenArray[startY]) / totalPercentage) << 8;
                blue = ((blueArray[endY] - blueArray[startY]) / totalPercentage);

                rgbOutput[x + yTimesWidth] = (rgbInput[x + yTimesWidth] & 0xff000000) | red | green | blue;
                yTimesWidth += imgWidth;
                y++;
            }

            // Last, fill in from imageSize - dimension/2 to imageWidth
            tempEnd = imgHeight;
            y = imgHeight - dimensionDivTwo;
            while (y < imgHeight) {
                startY = y - dimensionDivTwo + 1;
                endY = imgHeight;


                totalPercentage = alphaArray[endY] - alphaArray[startY] + 1;
                red = ((redArray[endY] - redArray[startY]) / totalPercentage) << 16;
                green = ((greenArray[endY] - greenArray[startY]) / totalPercentage) << 8;
                blue = ((blueArray[endY] - blueArray[startY]) / totalPercentage);

                rgbOutput[x + yTimesWidth] = (rgbInput[x + yTimesWidth] & 0xff000000) | red | green | blue;
                yTimesWidth += imgWidth;
                y++;
            }
            x++;

        }


        return this.output;
    }

    private RgbImage verticalPass(RgbImage input) {
        if (!isActive()) {
            return input;
        }

        int[] rgbInput = input.getRgbData();
        int[] rgbOutput = this.output.getRgbData();

        // Define all variables here instead of inside the loops.
        int red = 0;
        int green = 0;
        int blue = 0;
        int totalPercentage = 100;
        int x = 0, y = 0,  startX = 0, endX, c = 0, percentage = 0;
        int yTimesWidth = 0;
        int tempEnd = 0;
        int dimensionDivTwo = 0;
        int imgWidth = this.width;
        int imgHeight = this.height;

        int arraySize = imgWidth + 1;
        int[] redArray = new int[arraySize];
        int[] greenArray = new int[arraySize];
        int[] blueArray = new int[arraySize];
        int[] alphaArray = new int[arraySize];

        int dimension = this.blur.getValue(100);
        dimension = Math.max(2, imgWidth * dimension / 100);
        dimensionDivTwo = dimension / 2;

        // VERTICAL PASS
        yTimesWidth = 0;
        y = 0;
        while (y < imgHeight) {

            // Calculate the dynamic array for the current row

            // First, initialize the first element
            redArray[0] = 0; // Red channel sum
            greenArray[0] = 0; // Green channel sum
            blueArray[0] = 0; // Blue channel sum
            alphaArray[0] = 0; // Alpha channel sum

            // Then, calculate the rest
            x = 0;
            while (x < imgWidth) {

                c = rgbInput[x + yTimesWidth];
                percentage = c >>> 25;
                redArray[x + 1] = redArray[x] + ((((c & 0x00ff0000) >> 16)) * percentage);
                greenArray[x + 1] = greenArray[x] + ((((c & 0x0000ff00) >> 8)) * percentage);
                blueArray[x + 1] = blueArray[x] + (((c & 0x000000ff)) * percentage);
                alphaArray[x + 1] = alphaArray[x] + percentage;
                x++;
            }

            // Use dynamic programming to fill out the corresponding row of the output image

            // First, fill in from 0 to dimension/2
            tempEnd = dimension / 2;
            x = 0;
            while (x < tempEnd) {
                startX = 0;
                endX = x + tempEnd;

                totalPercentage = (alphaArray[endX] - alphaArray[startX]) + 1;
                red = (((redArray[endX] - redArray[startX])) / totalPercentage) << 16;
                green = (((greenArray[endX] - greenArray[startX])) / totalPercentage) << 8;
                blue = (((blueArray[endX] - blueArray[startX])) / totalPercentage);

                rgbOutput[x + yTimesWidth] = (rgbInput[x + yTimesWidth] & 0xff000000) | red | green | blue;
                x++;
            }

            // Next, fill in from dimension/2 to imageSize-dimension/2
            tempEnd = imgWidth - dimension / 2;
            x = dimensionDivTwo;
            while (x < tempEnd) {
                startX = x - dimensionDivTwo + 1;
                endX = x + dimensionDivTwo;

                totalPercentage = (alphaArray[endX] - alphaArray[startX]) + 1;
                red = (((redArray[endX] - redArray[startX])) / totalPercentage) << 16;
                green = (((greenArray[endX] - greenArray[startX])) / totalPercentage) << 8;
                blue = (((blueArray[endX] - blueArray[startX])) / totalPercentage);

                rgbOutput[x + yTimesWidth] = (rgbInput[x + yTimesWidth] & 0xff000000) | red | green | blue;
                x++;
            }

            // Last, fill in from imageSize - dimension/2 to imageWidth
            tempEnd = imgWidth;
            x = imgWidth - dimensionDivTwo;
            while (x < imgWidth) {
                startX = x - dimensionDivTwo + 1;
                endX = imgWidth;

                totalPercentage = (alphaArray[endX] - alphaArray[startX]) + 1;
                red = (((redArray[endX] - redArray[startX])) / totalPercentage) << 16;
                green = (((greenArray[endX] - greenArray[startX])) / totalPercentage) << 8;
                blue = (((blueArray[endX] - blueArray[startX])) / totalPercentage);


                rgbOutput[x + yTimesWidth] = (rgbInput[x + yTimesWidth] & 0xff000000) | red | green | blue;
                x++;
            }

            yTimesWidth += imgWidth;
            y++;
        }

        return this.output;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.enough.polish.ui.RgbFilter#process(de.enough.polish.util.RgbImage)
     */
    public final RgbImage process(RgbImage input) {

        /*
         * Gaussian Blur is linearly separable, so let's take advantage of this
         * by doing first a vertical pass and then a horizontal pass.
         *
         * FullGauss (image) = VertGauss(HorizGauss(image))
         *
         * Also, since we basically move the blur window from left to right, or
         * from top to bottom, we can use dynamic programming to optimize
         * the blur process.
         *
         * Consider the following:
         * For every image channel, we will have an array X with the meaning:
         *
         * X[n] = sum(pixelLevels[0] ... pixelLevels[n-1]);
         *
         * where pixelLevels is the sum of the channel levels for the
         * specified pixels.
         *
         * It is obvious we can fill the X array quickly in one pass
         * because X[n] = X[n-1] + currentPixel .
         *
         * Thus, the channel sum for all pixels between A and B will be
         * SUM(A,B) = X[B+1] - X[A]
         *
         * After that, to do a box blur for the current window row or
         * column between pixels A and B, all we have to do is :
         *
         * SUM(A,B) / (B-A)
         *
         * Since SUM(A,B) is quickly computable, this speeds up the
         * blur process considerably. Of course, we have to take into
         * account transparency, and this eats up some CPU time.
         *
         * Also heavily optimized the loops by moving all inner-"if"s
         * and using pre-calculation and bit shifts wherever possible.
         *
         * Because of bit shifts, some values (like totalPercentage) were refactored
         * to base-2 numbers (128 instead of 100). The math has been modified to take
         * this into consideration, however very small visual differences might occur
         * because of this. These differences are, for all intents and purposes unnoticeable.
         *
         */

        this.width = input.getWidth();
        this.height = input.getHeight();
        if (this.output == null || this.output.getWidth() != input.getWidth()
                || this.output.getHeight() != input.getHeight()) {
            this.output = new RgbImage(input.getWidth(), input.getHeight());
        }

        this.output = verticalPass(horizontalPass(input));


        return this.output;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.enough.polish.ui.RgbFilter#setStyle(de.enough.polish.ui.Style,
     * boolean)
     */
    public void setStyle(Style style, boolean resetStyle) {

        super.setStyle(style, resetStyle);
        // #if polish.css.filter-blur-grade
        Dimension blurInt = (Dimension) style.getObjectProperty("filter-blur-grade");
        if (blurInt != null) {
            this.blur = blurInt;
        }
        // #endif

    }

    /*
     * (non-Javadoc)
     *
     * @see de.enough.polish.ui.RgbFilter#releaseResources()
     */
    public void releaseResources() {
        this.output = null;
    }
}
