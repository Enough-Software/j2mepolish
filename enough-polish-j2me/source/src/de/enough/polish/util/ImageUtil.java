/*
 * Created on 15-May-2005 at 21:28:30.
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
package de.enough.polish.util;

//#if polish.midp2 || (!polish.midp && polish.usePolishGui)
//#define tmp.supportImageOperations
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
//#endif

import de.enough.polish.math.FP;

/**
 * Utility class for transforming images.
 *
 * <p>Copyright (c) Enough Software 2005 - 2009</p>
 * <pre>
 * history
 *        15-May-2005 - rob creation
 *        15-Aug-2007 - Simon hq-down scaling added
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 * @author Tim Muders, tim.muders@enough.de
 * @author Simon Schmitt, simon.schmitt@enough.de
 * @author Anders Bo Pedersen, anders@wicore.dk
 * @author Ovidiu Iliescu
 */
public final class ImageUtil {

    /**
     * No instantiation is allowed
     */
    private ImageUtil() {
    }

    /**
     * Scales the rgb data and stores it into the given scaledRgbData array.
     *
     * @param scaleFactor the factor by which the rgb data should be magnified in percent, e.g. 130
     * @param width the width of the rgbData and the scaledRgbData (scanline width)
     * @param height the height of the rgbData and the scaledRgbData (scanline width)
     * @param rgbData the source rgbData
     * @param scaledRgbData the target rgbData array, must have the same dimensions like the given rgbData
     * @throws ArithmeticException when width of scaleFactor is 0
     */
    public static void scale(int scaleFactor, int width, int height, int[] rgbData, int[] scaledRgbData) {

        /*
         * We will use a destination to source mapping. This has the advantage
         * of being considerably faster for downward resizing, since the
         * smaller the destination image, the fewer pixels have to be
         * processed.
         */

        if (scaleFactor < 100) {

            int x, y, dy;
            int srcOffset;

            // Calculate the target width and height
            final int targetWidth = (width * scaleFactor) / 100;
            final int targetHeight = (height * scaleFactor) / 100;

            // Calculate the Y and X start offsets
            final int xStart = (width - ((scaleFactor * width) / 100)) / 2;
            final int yStart = (height - ((scaleFactor * height) / 100)) / 2;

            // Calculate total fixed destination offset
            int destOffset = 0 - width + yStart * width + xStart;

            // Calculate the pixel ratio ( << 10 )
            final int pixelRatio = (1024 * 100) / scaleFactor;

            y = 0;
            while (y < targetHeight) {
                dy = ((pixelRatio * y) >> 10) * width;
                destOffset += width;
                srcOffset = 0;

                x = 0;
                while (x < targetWidth) {
                    scaledRgbData[destOffset + x] = rgbData[dy + (srcOffset >> 10)];
                    srcOffset += pixelRatio;
                    x++;
                }

                y++;
            }

        } else {


            int x, y, dy;
            int destOffset = 0;
            int srcOffset = 0;

            // Calculate the pixel ratio ( << 10 )
            final int pixelRatio = (1024 * 100) / scaleFactor;

            // Calculate the start X and Y offsets
            final int xStart = ((((width - (width * 100) / scaleFactor) / 2)));
            final int yStart = ((((height - (height * 100) / scaleFactor) / 2)));

            // Calculate the total initial source offset offset
            final int fixedSrcOffset = xStart + width * yStart;

            y = 0;
            while (y < height) {
                dy = fixedSrcOffset + width * ((y * pixelRatio) >> 10);

                x = 0;
                srcOffset = 0;
                while (x < width) {
                    scaledRgbData[destOffset + x] = rgbData[dy + (srcOffset >> 10)];
                    srcOffset += pixelRatio;
                    x++;
                }

                destOffset += width;
                y++;
            }

        }
    }

    /**
     * Scales the image without filling gaps between single pixels.
     *
     * @param factor the zoom factor in percent, must be greater than 100
     * @param width the width of the target RGB
     * @param height the height of the target RGB
     * @param sourceRgb the source RGB data that has the original data
     * @param targetRgb the target array for storing the scaled image
     * @throws IllegalArgumentException when the factor is less than 100
     */
    public static void particleScale(int factor, int width, int height, int[] sourceRgb, int[] targetRgb) {
        if (factor < 100) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < targetRgb.length; i++) {
            targetRgb[i] = 0;
        }
        int centerX = width >> 1;
        int centerY = height >> 1;
        int distanceX = (width - (width * 100) / factor) >> 1;
        int startX = distanceX;
        int endX = width - distanceX;
        int distanceY = (height - (height * 100) / factor) >> 1;
        int startY = distanceY;
        int endY = height - distanceY;
        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                distanceX = centerX - x;
                int targetX = centerX - (distanceX * factor) / 100;
                if (targetX < 0 || targetX >= width) {
                    continue;
                }
                distanceY = centerY - y;
                int targetY = centerY - (distanceY * factor) / 100;
                if (targetY < 0 || targetY >= height) {
                    continue;
                }
                int sourceIndex = y * width + x;
                int targetIndex = targetY * width + targetX;
                targetRgb[targetIndex] = sourceRgb[sourceIndex];
            }
        }
    }

    /**
     * Scales the rgb data and stores it into the given scaledRgbData array and adds an alpha semi transparency value at the same time.
     *
     * @param opacity the alpha value, 255 (0xFF) means fully opaque, 0 fully transparent
     * @param scaleFactor the factor by which the rgb data should be magnified in percent, e.g. 130
     * @param width the width of the rgbData and the scaledRgbData (scanline width)
     * @param height the height of the rgbData and the scaledRgbData (scanline width)
     * @param rgbData the source rgbData
     * @param scaledRgbData the target rgbData array, must have the same dimensions like the given rgbData
     * @throws ArithmeticException when width of scaleFactor is 0
     */
    public static void scale(int opacity, int scaleFactor, int width, int height, int[] rgbData, int[] scaledRgbData) {
        opacity = (opacity << 24) | 0xFFFFFF;
        if (scaleFactor < 100) {
            int xStart = ((width * 100) - (width * scaleFactor)) / 200;
            int yStart = ((height * 100) - (height * scaleFactor)) / 200;
//			for (int y=yStart; y < height-yStart; y++){
//				for (int x=xStart; x < width-xStart; x++){
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int xTarget = (x * scaleFactor) / 100 + xStart;
                    int yTarget = (y * scaleFactor) / 100 + yStart;
                    scaledRgbData[(yTarget * width) + xTarget] = (rgbData[(y * width) + x] | 0xff000000) & opacity;
                }
            }
            return;
        }

        int yStart = ((height - height * 100 / scaleFactor) / 2) * width;
        int xStart = (width - width * 100 / scaleFactor) / 2;
        for (int y = 0; y < height; y++) {
            int c1 = y * width;
            int c2 = yStart + (y * 100 / scaleFactor) * width;
            for (int x = 0; x < width; x++) {
                scaledRgbData[c1 + x] = (rgbData[c2 + xStart + x * 100 / scaleFactor] | 0xff000000) & opacity;
            }
        }
    }

    /**
     * Scales the rgb data and stores it into the given scaledRgbData array and adds an alpha semi transparency value at the same time.
     *
     * @param scaledWidth the width of the scaled rgbData
     * @param scaledHeight the height of the scaled rgbData
     * @param scanlength the length of the given RGB data, usual the same as sourceWidth
     * @param sourceWidth the width of the rgbData
     * @param sourceHeight the height of the rgbData
     * @param rgbData the source rgbData
     * @return a new rgbData array that contains the scaled version
     * @throws ArithmeticException when width of scaleFactor is 0
     */
    public static int[] scale(int scaledWidth, int scaledHeight, int scanlength, int sourceWidth, int sourceHeight, int[] rgbData) {
        int[] scaledRgbData = new int[scaledWidth * scaledHeight];
        for (int y = 0; y < scaledHeight; y++) {
            int c1 = y * scaledWidth;
            int c2 = (y * sourceHeight / scaledHeight) * scanlength;
            for (int x = 0; x < scaledWidth; x++) {
                scaledRgbData[c1 + x] = rgbData[c2 + x * sourceWidth / scaledWidth];
            }
        }
        return scaledRgbData;
    }
    private static final int INTMAX = 1 << 10; // do NOT change
    private static final int PSEUDO_FLOAT = 10; // do NOT change
    private static final int PSEUDO_POW2 = 1 << PSEUDO_FLOAT;//2048;
    private static final int PSEUDO_POW2M1 = (1 << PSEUDO_FLOAT) - 1;//2047;
    /**
     * Changeable constants for the optimized scaling
     *
     * SCALE_THRESHOLD_SHIFT: between 1 and 3
     * 		Since the source pixel are weighted it is possible to sort light ones out.
     * 		A pixel has to be apx. greater than  1/(1<<(SCALE_THRESHOLD_SHIFT*2-1))
     * 		compared to the whole weight of the resulting pixel.
     *
     */
    private static final int SCALE_THRESHOLD_SHIFT = 3;
    public static final int EDGEDETECTION_MAP_HIGHEST_QUALITY = -1; 	// highest quality ... never use this!!
    public static final int EDGEDETECTION_MAP_HIGH_QUALITY = (0xff ^ 1) << 24 | (0xff ^ 1) << 16 | (0xff ^ 1) << 8 | (0xff ^ 1);
    public static final int EDGEDETECTION_MAP_MEDIUM = (0xff ^ 15) << 24 | (0xff ^ 31) << 16 | (0xff ^ 15) << 8 | (0xff ^ 31);
    public static final int EDGEDETECTION_MAP_FAST = (0xff ^ 15) << 24 | (0xff ^ 63) << 16 | (0xff ^ 63) << 8 | (0xff ^ 63);
    public static final int EDGEDETECTION_MAP_FAST_AND_SIMPLE = 0; 	// just the simple copy mode

    public static void scaleDownHq(int[] dest, int[] src, int srcWidth, int scaledWidth, int scaledHeight, int opacity, boolean SKIP_FRACTIONS) {
        scaleDownHq(dest, src, srcWidth, 0, scaledWidth, scaledHeight, opacity, EDGEDETECTION_MAP_HIGH_QUALITY, SKIP_FRACTIONS);
    }

    /**
     * EDGEDETECTION_MAP_CUSTOM
     * 		In order to save time it is possible to run an edge Detection over the source
     * 		image.
     * 		You have the possibility to specify your own edge detection mask. The bitmask
     * 		is used to compare the pixel nearby. A detailed computation is done, in case
     * 		of differeces that exceed the mask in one of the Channels.
     *
     *			private static final int EDGELEVEL=63;
     * 			public static final int EDGEDETECTION_MAP_CUSTOM=(0xff ^ 15)<<24 | (0xff ^ EDGELEVEL)<<16 | (0xff ^ EDGELEVEL)<<8 | (0xff ^ EDGELEVEL);
     */
    /**
     * This method provides an algorithm to shrink a given image (given as ARGB-array).
     * In a lot of cases you can also use perspectiveShear with leftHeigt=rightHeight since
     * this might be faster.
     *
     * 	However, the supported shrink factor is between 20 and 99 percent of the source width.
     * 	There are some optimizations in order to speed the computation up, but more speed
     * 		implies less quality. Depending on the image and the device it is recomended to
     * 		activate edgeDetection by passing an edgeDetection map and SKIP_FRACTIONS.
     * 		Those features estimated unimportant pixel and process them with less compuation
     *  	time. You can also change some constants in ImageUtil if you want to finetune
     *  	the algorithm accoring to your needs.
     *
     *
     * @param dest			the preallocated array to store the shrinked image
     * @param src			the source image
     * @param srcWidth		the width of the source image
     * @param scaleFactor	0 or the desired scalefactor in percent
     * @param scaledWidth	0 or the resulting width
     * @param scaledHeight	0 or the resulting height
     * @param opacity		you are able to add opacity information here (value between 1 and 255)
     * @param EDGEDETECTION_MAP you can specify the level of edgeDetection using constants from ImageUtil
     * @param SKIP_FRACTIONS enables optimization mode
     */
    public static void scaleDownHq(int[] dest, int[] src, int srcWidth, int scaleFactor, int scaledWidth, int scaledHeight, int opacity, int EDGEDETECTION_MAP, boolean SKIP_FRACTIONS) {
        /*// TEST CODE
        for (int i = 0; i < src.length; i++) {
        if (src[i]>>24!=0){
        src[i]=255<<24|255;
        }
        }*/

        int scaleS;
        // step

        // parse the scaling parameter
        int srcHeight = src.length / srcWidth;
        if (scaleFactor != 0) {
            scaleS = (100 << PSEUDO_FLOAT) / (scaleFactor);
        } else if (scaledWidth != 0) {
            scaleS = (srcWidth << PSEUDO_FLOAT) / (scaledWidth);
        } else {
            scaleS = (srcHeight << PSEUDO_FLOAT) / (scaledHeight);
        }
        if (scaleS == 1 << PSEUDO_FLOAT) {
            System.arraycopy(src, 0, dest, 0, src.length);
            return;
        } else if (scaleS < 1 << PSEUDO_FLOAT) {
            throw new IllegalArgumentException();
        } else if (scaleS > 5 << PSEUDO_FLOAT) { // max is probably sqrt(32)
            throw new IllegalArgumentException(" scale >5 may lead to internal overflows use perspectiveShear instead");
        }

        // recomputation of the size prevents precision errors
        if (scaledHeight == 0) {
            scaledHeight = ((srcHeight << PSEUDO_FLOAT) / scaleS);
        }
        if (scaledHeight == 0) {
            scaledWidth = ((srcWidth << PSEUDO_FLOAT) / scaleS);
        }

        int destPointer = 0;
        int[] tmp = new int[5];

        // position
        int srcXdetailed = 0;
        int srcYdetailed = 0;
        int srcYrounded = 0;

        // intensities
        int yIntensityStart, yIntensityEnd;
        int xIntensityStart, xIntensityEnd;


        // used
        int destPixelItensityMinimum;
        if (SKIP_FRACTIONS) {
            destPixelItensityMinimum = (INTMAX * scaleS * scaleS) >> 20;
        } else {
            destPixelItensityMinimum = 0;
        }
        //#debug
        long t = System.currentTimeMillis();

        // opt01
        if (EDGEDETECTION_MAP != EDGEDETECTION_MAP_HIGHEST_QUALITY) {

            scale(src, scaledWidth, scaledHeight, srcWidth, srcHeight, dest);
        }

        int currentDestEdge = 0;


        for (int scaledY = 0; scaledY < scaledHeight; scaledY++) {
            for (int scaledX = 0; scaledX < scaledWidth; scaledX++) {
                destPointer = scaledY * scaledWidth + scaledX;

                //if(srcY<srcHeight && srcX<srcWidth){

                //dest[destPointer]= src[(srcYrounded)*srcWidth  + srcXrounded];

                currentDestEdge = dest[destPointer] & EDGEDETECTION_MAP;

                if (EDGEDETECTION_MAP == EDGEDETECTION_MAP_HIGHEST_QUALITY || // process the pixel if there is no edgedetection activated
                        scaledY == scaledHeight - 1 || // process the pixels and do not check the next statements if you are not in the last row
                        scaledY == 0 || // same with the fist row
                        ( //	if there is a difference to the surrounding pixels
                        //opt01
                        (currentDestEdge ^ (dest[destPointer + 1] & EDGEDETECTION_MAP)) != 0 || // next
                        (currentDestEdge ^ (dest[destPointer - 1] & EDGEDETECTION_MAP)) != 0 || // previous
                        //opt01
                        (currentDestEdge ^ (dest[destPointer + scaledWidth] & EDGEDETECTION_MAP)) != 0 || // below
                        (currentDestEdge ^ (dest[destPointer - scaledWidth] & EDGEDETECTION_MAP)) != 0 // above
                        )) {

                    // CALC THE OVERLAP OF Y
                    //yIntensityStart=1-(srcY-(int)srcY);
                    yIntensityStart = PSEUDO_POW2 - (srcYdetailed & PSEUDO_POW2M1);

                    // yIntensity=1; // in between
                    //yIntensityEnd=(srcY+scale-(int)(srcY+scale));
                    yIntensityEnd = (srcYdetailed + scaleS) & PSEUDO_POW2M1;
                    if (yIntensityEnd == 0) {
                        //yIntensityEnd=1;
                        yIntensityEnd = PSEUDO_POW2;
                    }

                    // CALC THE OVERLAP OF X
                    xIntensityStart = PSEUDO_POW2 - (srcXdetailed & PSEUDO_POW2M1);

                    // yIntensity=1; // in between
                    //yIntensityEnd=(srcY+scale-(int)(srcY+scale));
                    xIntensityEnd = (srcXdetailed + scaleS) & PSEUDO_POW2M1;
                    if (xIntensityEnd == 0) {
                        //yIntensityEnd=1;
                        xIntensityEnd = PSEUDO_POW2;
                    }

                    //xIntensityStart=xIntensityStart>>(PSEUDO_FLOAT-10);
                    //xIntensityEnd=xIntensityEnd>>(PSEUDO_FLOAT-10);
                    //scale down because of different intesity norm
                    //yIntensityStart=yIntensityStart>>(PSEUDO_FLOAT-10);
                    //yIntensityEnd=yIntensityEnd>>(PSEUDO_FLOAT-10);


                    // sum them up
                    tmp[0] = 0;
                    tmp[1] = 0;
                    tmp[2] = 0;
                    tmp[3] = 0;
                    tmp[4] = 0;

                    // start
                    tmp = helpWithX(src, tmp, srcWidth, srcXdetailed, srcYdetailed >> PSEUDO_FLOAT, scaleS, yIntensityStart, xIntensityStart, xIntensityEnd, destPixelItensityMinimum);
                    // between
                    int smallY;
                    //for (smallY = (int) srcY+1; smallY < srcY+scale-1; smallY++) {
                    for (smallY = srcYrounded + 1; smallY <= ((srcYdetailed + scaleS) >> PSEUDO_FLOAT) - 1; smallY++) {

                        tmp = helpWithX(src, tmp, srcWidth, srcXdetailed, smallY, scaleS, INTMAX * 1, xIntensityStart, xIntensityEnd, destPixelItensityMinimum);
                    }
                    //end
                    if (smallY < srcHeight) {
                        tmp = helpWithX(src, tmp, srcWidth, srcXdetailed, smallY, scaleS, yIntensityEnd, xIntensityStart, xIntensityEnd, destPixelItensityMinimum);
                    } else {
                        //mix transparency in
                        tmp = mixPixelIn(tmp, 0, yIntensityEnd, destPixelItensityMinimum);
                    }

                    // 				alpha=alpha/int			red=  redSum/(alpha*int)
                    if (tmp[1] == 0) {
                        dest[destPointer] = 0;
                    } else {
                        dest[destPointer] = (tmp[1] * opacity / (255 * tmp[0])) << 24 | (tmp[2] / (tmp[1])) << 16 | (tmp[3] / (tmp[1])) << 8 | (tmp[4] / (tmp[1]));
                    }
                }

                // refresh the (pseudo)float
                srcXdetailed += scaleS;

                // retrieve an integer
                srcYrounded = srcYdetailed >> PSEUDO_FLOAT;
            }

            //	refresh the (pseudo)float
            srcXdetailed = 0;
            srcYdetailed += scaleS;

            // retrieve an integer
            srcYrounded = srcYdetailed >> PSEUDO_FLOAT;
        }

        //#debug
        System.out.println("hq scale down took " + (System.currentTimeMillis() - t) + " ms");
    }

    /**
     * This function adds a weighted (by yIntesity) row of source pixels to the related destination pixel.
     */
    private static int[] helpWithX(int[] src, int[] tmp, int srcWidth, int srcXdetailed, int Yrounded, int scaleS, int yIntenstiy, int xIntensityStart, int xIntensityEnd, int destPixelItensityMinimum) {
        if (yIntenstiy < (destPixelItensityMinimum >> SCALE_THRESHOLD_SHIFT)) {
            return tmp;
        }
        int srcXrounded = srcXdetailed >> PSEUDO_FLOAT;
        int Y_srcWidth = (Yrounded) * srcWidth;

        // 	start
        tmp = mixPixelIn(tmp, src[Y_srcWidth + srcXrounded], (xIntensityStart * yIntenstiy) >> 10, destPixelItensityMinimum);
        // between
        int smallX = 0;
        //for (smallX = (int) srcX+1; smallX < srcX+scale-1; smallX++) {
        for (smallX = srcXrounded + 1; smallX <= ((srcXdetailed + scaleS) >> PSEUDO_FLOAT) - 1; smallX++) {
            tmp = mixPixelIn(tmp, src[Y_srcWidth + smallX], (yIntenstiy), destPixelItensityMinimum);
        }
        //end
        if (smallX < srcWidth) {
            tmp = mixPixelIn(tmp, src[Y_srcWidth + smallX], (xIntensityEnd * yIntenstiy) >> 10, destPixelItensityMinimum);
        } else {
            // mic transparency in
            tmp = mixPixelIn(tmp, 0, (xIntensityEnd * yIntenstiy) >> 10, destPixelItensityMinimum);
        }

        return tmp;
    }

    /**
     * This method is able to mix several colors including transparency information.
     *
     */
    private static int[] mixPixelIn(int[] current, int add, int intensity, int destPixelItensityMinimum) {
        if (add == 0) {
            return current;

        } else if (intensity < destPixelItensityMinimum >> (SCALE_THRESHOLD_SHIFT << 1 - 1)) { // *8*8/2
            return current;

        } else {

            int alpha = (add >>> 24);
            current[0] += intensity;									// intensity channel

            current[1] += ((add >>> 24 & 255) * intensity);	// alpha channel

            current[2] += ((add >>> 16 & 255) * intensity * alpha);	// red
            current[3] += ((add >>> 8 & 255) * intensity * alpha);		// green
            current[4] += ((add & 255) * intensity * alpha);		// blue

            return current;
        }
    }

    /**
     * This function enables scaling a given image with differnt scaling factors in vertical and horizontal direction.
     * Futhermore you can specify a different scaling on the left and on the right side
     * as needed for implementing a coverflow.
     *
     * The resulting shrinked image will be located in the dest array that has the
     * same size and scanlength as the source array. It will be placed on the left
     * side and vertically in the middle. So the offset will be
     * dx=0, dy=(originalHeight-max(leftHeight,rightHeight))/2
     * The scanlength will remain the same!
     *
     *
     * @param src				The array containing the source image to be sheared.
     * @param dest				The array that will conatain the resulting image in
     * 								a certered position. It has	to be the same size
     * 								as the source array. Scanlength = originalWidth
     * @param originalWidth		The width of the source image
     * @param newWidth			The desired new width of the image. Attention: the
     * 								destination image will still have the same size
     *								as the source image, but contain the new image
     *								centered within.
     * @param leftHeight		The height of the left image border after scaling.
     * @param rightHeight		The height of the right image border after scaling.
     * @param opacity			The opacity of the image all in all.
     * @param EDGEDETECTION_MAP	if !=0 this enables edge detection which will speed
     * 								up the computation. Please read the documentation
     * 								of scaleDownHq for details.
     * @see #scaleDownHq(int[], int[], int, int, int, int, boolean)
     */
    public static void perspectiveShear(int[] src, int[] dest, int originalWidth, int newWidth, int leftHeight, int rightHeight, int opacity, int EDGEDETECTION_MAP) {
        //#debug
        System.out.println("perspectiveShear requested with height=" + rightHeight + " width" + newWidth + " opacity " + opacity);

        int originalHeight = src.length / originalWidth;
        int smallHeight;
        int largeHeight;
        if (leftHeight > rightHeight) {
            smallHeight = rightHeight;
            largeHeight = leftHeight;
        } else {
            smallHeight = leftHeight;
            largeHeight = rightHeight;
        }

        if (largeHeight > originalHeight || newWidth > originalWidth) {
            throw new IllegalArgumentException("just downscaling possible");
        }

        /*for (int y = 0; y <originalHeight; y++) {
        for (int x = 0; x < originalWidth; x++) {
        //if (src[y*originalWidth+ x] !=0){
        if ((x&1)==0){
        src[y*originalWidth+ x]= 255<<24 | 255<<16;
        } else {
        src[y*originalWidth+ x]= 255<<24 | 255<<8;
        }
        //src[y*originalWidth+ x]=255<<24 | (y*3)<<8;
        //}

        }
        }*/
        //#if polish.debug.debug
        long t = System.currentTimeMillis();
        //#endif

        for (int i = 0; i < dest.length; i++) {
            dest[i] = 0;
        }

        int srcX = 0;
        int srcY = 0;
        int srcYdetailed = 0;


        int h;
        int shrinkY = (1 << PSEUDO_FLOAT) * (largeHeight - smallHeight) / originalWidth / 2;
        int scaleY;
        int yIntensityStart, yIntensityEnd;

        int destPointer;
        int[] tmp = new int[5];

        // partial vertical scaling
        int smallY;
        for (srcX = 0; srcX < originalWidth; srcX++) { // there is no scaling in x direction here

            if (leftHeight > rightHeight) {
                h = (originalHeight - largeHeight) / 2 + ((shrinkY * srcX) >> PSEUDO_FLOAT);
            } else {
                h = (originalHeight - smallHeight) / 2 - ((shrinkY * srcX) >> PSEUDO_FLOAT);
            }

            scaleY = (1 << PSEUDO_FLOAT) * originalHeight / (originalHeight - (h << 1));
            for (int destY = 0; destY < originalHeight - (h << 1); destY++) {
                srcYdetailed = ((scaleY * destY));

                srcY = srcYdetailed >> PSEUDO_FLOAT;

                destPointer = (destY + h) * originalWidth + srcX; // there is no scaling in x direction here

                int srcY_width = srcY * originalWidth;
                if (EDGEDETECTION_MAP != EDGEDETECTION_MAP_HIGHEST_QUALITY && srcY != originalHeight - 1 && ((src[srcY_width + srcX] ^ src[(srcY + 1) * originalWidth + srcX]) & EDGEDETECTION_MAP) == 0) {
                    dest[destPointer] = src[srcY_width + srcX];
                    dest[destPointer] = (dest[destPointer] & 0x00ffffff) | ((opacity * (dest[destPointer] >>> 24) / 255) << 24);

                } else {

                    // process the pixel
                    tmp[0] = 0;
                    tmp[1] = 0;
                    tmp[2] = 0;
                    tmp[3] = 0;
                    tmp[4] = 0;

//					 CALC THE OVERLAP OF Y
                    yIntensityStart = PSEUDO_POW2 - (srcYdetailed & PSEUDO_POW2M1);

                    // yIntensity=1; // in between
                    yIntensityEnd = (srcYdetailed + scaleY) & PSEUDO_POW2M1;
                    if (yIntensityEnd == 0) {
                        //yIntensityEnd=1;
                        yIntensityEnd = PSEUDO_POW2;
                    }


                    // start
                    tmp = mixPixelIn(tmp, src[srcY_width + srcX], (yIntensityStart), 0);
                    // between
                    for (smallY = srcY + 1; smallY <= ((srcYdetailed + scaleY) >> PSEUDO_FLOAT) - 1; smallY++) {
                        tmp = mixPixelIn(tmp, src[smallY * originalWidth + srcX], PSEUDO_POW2, 0);
                    }
                    // end

                    if (smallY < originalHeight) {
                        tmp = mixPixelIn(tmp, src[smallY * originalWidth + srcX], (yIntensityEnd), 0);
                    } else {
                        //mix transparency in
                        tmp = mixPixelIn(tmp, 0, (yIntensityEnd), 0);
                    }

                    // assemble the pixel
                    if (tmp[1] == 0) {
                        dest[destPointer] = 0;
                    } else {
                        dest[destPointer] = (tmp[1] * opacity / (255 * tmp[0])) << 24 | (tmp[2] / (tmp[1])) << 16 | (tmp[3] / (tmp[1])) << 8 | (tmp[4] / (tmp[1]));
                    }
                }
            }

        }

        // horizontal shrinking
        int scaleX = (1 << PSEUDO_FLOAT) * originalWidth / newWidth;
        int srcXdetailed;
        int smallX;
        if (scaleX != 1) {
            int xIntensityStart, xIntensityEnd;
            //newWidth=originalWidth;
            h = (originalHeight - largeHeight) / 2;
            for (int y = h; y < originalHeight - h; y++) {
                int y_width = y * originalWidth;
                for (int destX = 0; destX < newWidth; destX++) {
                    srcXdetailed = (scaleX * destX);

                    srcX = srcXdetailed >> PSEUDO_FLOAT;

                    destPointer = y_width + destX;

                    if (EDGEDETECTION_MAP != EDGEDETECTION_MAP_HIGHEST_QUALITY && y != originalHeight - 1 && ((dest[y_width + srcX] ^ dest[y_width + srcX + 1]) & EDGEDETECTION_MAP) == 0) {
                        dest[destPointer] = dest[y_width + srcX];

                    } else {

                        tmp[0] = 0;
                        tmp[1] = 0;
                        tmp[2] = 0;
                        tmp[3] = 0;
                        tmp[4] = 0;

                        // CALC THE OVERLAP OF X
                        xIntensityStart = PSEUDO_POW2 - (srcXdetailed & PSEUDO_POW2M1);

                        // yIntensity=1; // in between
                        //yIntensityEnd=(srcY+scale-(int)(srcY+scale));
                        xIntensityEnd = (srcXdetailed + scaleX) & PSEUDO_POW2M1;
                        if (xIntensityEnd == 0) {
                            //yIntensityEnd=1;
                            xIntensityEnd = PSEUDO_POW2;
                        }

                        //	start
                        tmp = mixPixelIn(tmp, dest[y_width + srcX], xIntensityStart, 0);
                        // between
                        for (smallX = srcX + 1; smallX <= ((srcXdetailed + scaleX) >> PSEUDO_FLOAT) - 1; smallX++) {
                            tmp = mixPixelIn(tmp, dest[y_width + smallX], PSEUDO_POW2, 0);
                        }
                        // end
                        if (smallX < originalWidth) {
                            tmp = mixPixelIn(tmp, dest[y_width + smallX], xIntensityEnd, 0);
                        } else {
                            //mix transparency in
                            tmp = mixPixelIn(tmp, 0, xIntensityEnd, 0);
                        }

                        // assemble the pixel
                        if (tmp[1] == 0) {
                            dest[destPointer] = 0;
                        } else {
                            dest[destPointer] = (tmp[1] / tmp[0]) << 24 | (tmp[2] / (tmp[1])) << 16 | (tmp[3] / (tmp[1])) << 8 | (tmp[4] / (tmp[1]));
                        }
                    }

                }
                // clear the rest
                for (int x = newWidth; x < originalWidth; x++) {
                    dest[y_width + x] = 0;
                }
            }
        }

        //#debug
        System.out.println(" time for perspective: " + (System.currentTimeMillis() - t));

    }

    //#if polish.hasFloatingPoint
    /**
     * Rotates the given RGB data image and uses the center as the reference point for the rotation.
     *
     * @param image the RGB data image that is going to be rotated. Warning: the RGB data might get replaced
     * @param angle the angle for the rotation in degrees (-360..0..360)
     */
    public static RgbImage rotate(RgbImage image, int angle) {
        return rotate(image, angle, image.getWidth() / 2, image.getHeight() / 2);
    }
    //#endif

    //#if polish.hasFloatingPoint
    /**
     * Rotates the given RGB data image.
     *
     * @param image the RGB data image that is going to be rotated. Warning: the RGB data might get replaced
     * @param angle the angle for the rotation in degrees (-360..0..360)
     * @param referenceX the horizontal reference point for the rotation
     * @param referenceY the vertical reference point for the rotation
     */
    public static RgbImage rotate(RgbImage image, int angle, int referenceX, int referenceY) {
        int[] rgbData = image.getRgbData();
        int width = image.getWidth();
        int height = image.getHeight();
        double degreeCos = Math.cos(Math.PI * angle / 180);
        double degreeSin = Math.sin(Math.PI * angle / 180);
        int rotatedWidth = getRotatedWidth(angle, width, height, degreeCos, degreeSin);
        int rotatedHeight = getRotatedHeight(angle, width, height, degreeCos, degreeSin);
        int[] rotatedRgbData = new int[rotatedWidth * rotatedHeight];
        ImageUtil.rotate(rgbData, width, height,
                referenceX, referenceY, 0x00FFFFFF,
                degreeCos, degreeSin, rotatedRgbData, rotatedWidth, rotatedHeight);
        image.setRgbData(rotatedRgbData, rotatedWidth);
        image.setWidth(rotatedWidth);
        image.setHeight(rotatedHeight);
        return image;
    }
    //#endif

    //#if polish.hasFloatingPoint
    /**
     * Rotates the given rgb data and returns the rotated rgb data array.
     *
     * @param argbArray the rgb data to be rotated.
     * @param width the width of the rgb data.
     * @param height the heigth of the rgb data.
     * @param degree the degree value of the rotation.
     * @param backgroundColor the ARGB color used for the background
     * @return the rotated rgb data.
     */
    public static int[] rotate(int[] argbArray, int width, int height, int degree, int backgroundColor) {
        return rotate(argbArray, width, height, degree, width / 2, height / 2, backgroundColor);
    }
    //#endif

    //#if polish.hasFloatingPoint
    /**
     * Rotates the given rgb data and returns the rotated rgb data array.
     *
     * @param sourceRgbData the rgb data to be rotated.
     * @param width the width of the source rgb data.
     * @param height the heigth of the source rgb data.
     * @param degree the angle of the rotation.
     * @param referenceX the x position for the center of rotation.
     * @param referenceY the y position for the center of rotation.
     * @param backgroundColor the ARGB color used for the background
     * @return the rotated rgb data.
     */
    public static int[] rotate(int[] sourceRgbData, int width, int height, int degree, int referenceX, int referenceY, int backgroundColor) {
        double degreeCos = Math.cos(Math.PI * degree / 180);
        double degreeSin = Math.sin(Math.PI * degree / 180);
        int rotatedWidth = getRotatedWidth(degree, width, height, degreeCos, degreeSin);
        int rotatedHeight = getRotatedHeight(degree, width, height, degreeCos, degreeSin);
        int[] rotatedRgb = new int[rotatedHeight * rotatedWidth];
        rotate(sourceRgbData, width, height, referenceX, referenceY, backgroundColor, degreeCos, degreeSin, rotatedRgb, rotatedWidth, rotatedHeight);
        return rotatedRgb;
    }
    //#endif

    //#if polish.hasFloatingPoint
    /**
     * Rotates the source RGB data and stores it within the target RGB data array.
     *
     * @param sourceRgbData the rgb data to be rotated.
     * @param width the width of the source rgb data.
     * @param height the heigth of the source rgb data.
     * @param referenceX the x position for the center of rotation.
     * @param referenceY the y position for the center of rotation.
     * @param backgroundColor the ARGB color used for the background
     * @param degreeCos the cosine of the degree value: Math.cos(Math.PI*degree/180)
     * @param degreeSin the sine of the degree value: Math.sin(Math.PI*degree/180)
     * @param rotatedRGB the RGB data array for storing the rotated pixel data
     * @param rotatedWidth the width of the rotated rgb data
     * @param rotatedHeight the height of the rotated rgb data
     */
     public static void rotate(int[] sourceRgbData, int width, int height, int referenceX, int referenceY, int backgroundColor, double degreeCos, double degreeSin, int[] rotatedRGB, int rotatedWidth, int rotatedHeight) {

        // Declare needed variables
        int sourcePos;
        int xMinusRefXTimesDegCosXPlusHalfWidth ;
        int xMinusRefXTimesDegSinXMinusHalfOfHeight ;
        int newX, newY;
        int x, y;
        final int halfOfWidth = width / 2;
        final int halfOfHeight = height / 2;
        final int halfOfRotatedHeight = rotatedHeight/2;
        final int halfOfRotatedWidth = rotatedWidth/2;
        final int degCos = (int) (1024 * degreeCos) ;
        final int degSin = (int) (1024 * degreeSin) ;
        final int rotatedHeightMinus1 = rotatedHeight -1;
        final int rgbDataLen = sourceRgbData.length -1;
        final int maxWidth = width -1;

        // Initialize and precalculate trigonometry data
        // Thse calculations are used many times throughout the rotation
        // and as such it is worth it to precalculate them
        final int[] yMinusRefYTimesDegSin = new int[rotatedHeight];
        final int[] yMinusRefYTimesDegCos = new int[rotatedHeight];

        for (y = 0; y < rotatedHeight; y++) {
            yMinusRefYTimesDegSin[y] = ( halfOfWidth ) + (((y - halfOfRotatedHeight) * degSin) >> 10);
            yMinusRefYTimesDegCos[y] = ( halfOfHeight) + (((y - halfOfRotatedHeight) * degCos) >> 10);
        }

        // Rotate the image!
        // We use the standard 2D Matrix rotation formula, except we
        // precalculate as many things as possible before going into
        // the inner loop (see also the two arrays above).
        // The long variable names show what exactly we have concatenated
        // into each variable. :)

        x=-1;
        while ( ++x < rotatedWidth) {

            xMinusRefXTimesDegCosXPlusHalfWidth = (((x - halfOfRotatedWidth) * degCos) >> 10)  ;
            xMinusRefXTimesDegSinXMinusHalfOfHeight = (((x - halfOfRotatedWidth) * degSin) >> 10)  ;


            y=-1;
            while ( ++y < rotatedHeightMinus1){
                newX = xMinusRefXTimesDegCosXPlusHalfWidth + yMinusRefYTimesDegSin[y] ;
                if (newX < 0 || newX > maxWidth) {
                    continue;
                }
                newY = yMinusRefYTimesDegCos[y] - xMinusRefXTimesDegSinXMinusHalfOfHeight ;
                sourcePos = newX + newY * width;
                if ( (sourcePos < 0) || (sourcePos > rgbDataLen) ) {
                    continue;
                }
                rotatedRGB[x + y  * rotatedWidth] = sourceRgbData[sourcePos];
            }
        }

    }
    //#endif

    //#if polish.hasFloatingPoint
    /**
     * Returns the new height for the given degree. The new heigth symbols the heigth for an rotated rgb data array with the same degree rotation.
     *
     * @param degree the degree value of the rotation.
     * @param width the width of the rgb source.
     * @param heigth the heigth of the rgb source
     * @param degreeCos the cosine of the degree value: Math.cos(Math.PI*degree/180)
     * @param degreeSin the sine of the degree value: Math.sin(Math.PI*degree/180)
     * @return the new height of the rgb data.
     */
    public static int getRotatedHeight(int degree, int width, int heigth, double degreeCos, double degreeSin) {
        if (degree == -90 || degree == 90 || degree == 270 || degree == -270) {
            return width;
        } else if (degree == 360 || degree == 180 || degree == 0) {
            return heigth;
        }
        long pointY1 = MathUtil.round(0 * degreeSin + 0 * degreeCos);
        long pointY2 = MathUtil.round(width * degreeSin + 0 * degreeCos);
        long pointY3 = MathUtil.round(0 * degreeSin + heigth * degreeCos);
        long pointY4 = MathUtil.round(width * degreeSin + heigth * degreeCos);
        long minY = pointY1;
        if (pointY2 < minY) {
            minY = pointY2;
        }
        if (pointY3 < minY) {
            minY = pointY3;
        }
        if (pointY4 < minY) {
            minY = pointY4;
        }
        long maxY = pointY1;
        if (pointY2 > maxY) {
            maxY = pointY2;
        }
        if (pointY3 > maxY) {
            maxY = pointY3;
        }
        if (pointY4 > maxY) {
            maxY = pointY4;
        }
        return (int) (maxY - minY);
    }
    //#endif

    //#if polish.hasFloatingPoint
    /**
     * Returns the new width for the given degree. The new width symbols the width for an rotated rgb data array with the same degree rotation.
     *
     * @param degree the degree value of the rotation.
     * @param width the width of the rgb source.
     * @param heigth the heigth of the rgb source
     * @param degreeCos the cosine of the degree value: Math.cos(Math.PI*degree/180)
     * @param degreeSin the sine of the degree value: Math.sin(Math.PI*degree/180)
     * @return the new width of the rgb data.
     */
    public static int getRotatedWidth(int degree, int width, int heigth, double degreeCos, double degreeSin) {
        if (degree == -90 || degree == 90 || degree == 270 || degree == -270) {
            return heigth;
        } else if (degree == 360 || degree == 180 || degree == 0) {
            return width;
        }
        long pointX1 = 0; // MathUtil.round(0 * degreeCos - 0 * degreeSin);
        long pointX2 = MathUtil.round(width * degreeCos); //MathUtil.round(width * degreeCos - 0 *degreeSin);
        long pointX3 = MathUtil.round(-heigth * degreeSin); // MathUtil.round(0 *degreeCos - heigth *degreeSin);
        long pointX4 = MathUtil.round(width * degreeCos - heigth * degreeSin);
        long minX = pointX1;
        if (pointX2 < minX) {
            minX = pointX2;
        }
        if (pointX3 < minX) {
            minX = pointX3;
        }
        if (pointX4 < minX) {
            minX = pointX4;
        }
        long maxX = pointX1;
        if (pointX2 > maxX) {
            maxX = pointX2;
        }
        if (pointX3 > maxX) {
            maxX = pointX3;
        }
        if (pointX4 > maxX) {
            maxX = pointX4;
        }
        return (int) (maxX - minX);
    }
    //#endif

    /**
     * Rotates the given source RGB array
     * @param source the source RGB data
     * @param target the target RGB date
     * @param w the width of the source data
     * @param h the height of the source data
     * @param degrees either 90, 180 or 270 degrees
     * @throws IllegalArgumentException when the degrees are not either 90, 180 or 270
     */
    public static void rotateSimple(int[] source, int[] target, int w, int h, int degrees) {
        if (degrees == 90) {
            for (int row = 0; row < h; row++) {
                for (int col = 0; col < w; col++) {
                    target[col * h + ((h - 1) - row)] = source[row * w + col];
                }
            }
        } else if (degrees == 180) {
            for (int row = 0; row < h; row++) {
                for (int col = 0; col < w; col++) {
                    target[((h - 1) - row) * w + ((w - 1) - col)] = source[row * w + col];
                }
            }
        } else if (degrees == 270) {
            for (int row = 0; row < h; row++) {
                for (int col = 0; col < w; col++) {
                    target[((w - 1) - col) * h + row] = source[row * w + col];
                }
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Scales an rgb data unproportional in every new size you want bigger or smaller than the given original. Returns the scaled rgb data.
     *
     * @param opacity the alpha value, 255 (0xFF) means fully opaque, 0 fully transparent
     * @param rgbData the original rgbdata
     * @param newWidth the new width for the new rgbdata
     * @param newHeight the new height for the new rgbdata
     * @param oldWidth the width from the oiginal rgbdata
     * @param oldHeight the height from the oiginal rgbdata
     * @return the scaled rgb data.
     */
    public static int[] scale(int opacity, int[] rgbData, int newWidth, int newHeight, int oldWidth, int oldHeight) {
        int[] newrgbData = new int[newWidth * newHeight];
        scale(opacity, rgbData, newWidth, newHeight, oldWidth, oldHeight, newrgbData);
        return newrgbData;
    }

    /**
     * Scales an rgb data unproportional in every new size you want bigger or smaller than the given original. Returns the scaled rgb data.
     *
     * @param rgbData the original rgbdata
     * @param newWidth the new width for the new rgbdata
     * @param newHeight the new height for the new rgbdata
     * @param oldWidth the width from the oiginal rgbdata
     * @param oldHeight the height from the oiginal rgbdata
     * @return the scaled rgb data.
     */
    public static int[] scale(int[] rgbData, int newWidth, int newHeight, int oldWidth, int oldHeight) {
        int[] newRgbData = new int[newWidth * newHeight];
        scale(rgbData, newWidth, newHeight, oldWidth, oldHeight, newRgbData);
        return newRgbData;
    }

    /**
     * Scales an rgb data unproportional in every new size you want bigger or smaller than the given original. Returns the scaled rgb data.
     *
     * @param rgbData the original rgbdata
     * @param newWidth the new width for the new rgbdata
     * @param newHeight the new height for the new rgbdata
     * @param oldWidth the width from the oiginal rgbdata
     * @param oldHeight the height from the oiginal rgbdata
     * @param keepAspectRatio set to true when the original aspect ratio should be maintained, even when newWidth and newHeight are in a different ratio.
     *        Keeping the aspect ratio may result in transparent areas on the sides of the scaled image.
     * @return the scaled rgb data.
     */
    public static int[] scale(int[] rgbData, int newWidth, int newHeight, int oldWidth, int oldHeight, boolean keepAspectRatio) {
        int[] newRgbData = new int[newWidth * newHeight];
        int[] tmpRgbData = newRgbData;
        int tmpWidth = newWidth;
        int tmpHeight = newHeight;
        if (keepAspectRatio) {
            /* keep the original aspect ratio, so oldWidth/oldHeight == (newWidth'/newHeight').
             * So we have to calculate the newWidth' and newHeight' (called tmpWidth and tmpHeight):
             */
            tmpWidth = Math.min(newWidth, (oldWidth * newHeight) / oldHeight);
            tmpHeight = Math.min(newHeight, (oldHeight * newWidth) / oldWidth);
            keepAspectRatio = (tmpWidth != newWidth || tmpHeight != newHeight);
            if (keepAspectRatio) {
                tmpRgbData = new int[tmpWidth * tmpHeight];
            }
        }
        scale(rgbData, tmpWidth, tmpHeight, oldWidth, oldHeight, tmpRgbData);
        if (keepAspectRatio) {
            if (tmpWidth < newWidth) {
                // height is the same, but the width has changed,
                // so move the rows into the middle:
                for (int row = 0; row < tmpHeight; row++) {
                    int startTmp = row * tmpWidth;
                    int startNew = (row * newWidth) + (newWidth - tmpWidth) / 2;
                    System.arraycopy(tmpRgbData, startTmp, newRgbData, startNew, tmpWidth);
                }
            } else {
                // width is the same, but the height has changed:
                int startRowIndex = (newWidth * (newHeight - tmpHeight)) / 2;
                for (int row = 0; row < tmpHeight; row++) {
                    int startTmp = row * tmpWidth;
                    int startNew = startTmp + startRowIndex;
                    System.arraycopy(tmpRgbData, startTmp, newRgbData, startNew, tmpWidth);
                }
            }
        }
        return newRgbData;
    }

    /**
     * Stretches the rgb data vertical to the given top and bottom stretch factor and returns the new rgb data array.
     *
     * @param argbArray the rgb data to be stretched.
     * @param topStrechFactor the stretch factor of the top.
     * @param bottomStrechFactor the stretch factor of the bottom.
     * @param width the source width of the rgb data.
     * @param heigth the source heigth of the rgb data.
     * @return stretched rgb data array.
     */
    public static int[] stretchVertical(int[] argbArray, int topStrechFactor, int bottomStrechFactor, int width, int heigth) {
        int newWidthTop = (width * topStrechFactor) / 100;
        int newWidthBottom = (width * bottomStrechFactor) / 100;
        int procentualScalingHeight;
        int biggerWidth;
        if (newWidthTop < newWidthBottom) {
            procentualScalingHeight = (newWidthBottom - newWidthTop) / heigth;
            biggerWidth = newWidthBottom;
        } else {
            procentualScalingHeight = (newWidthTop - newWidthBottom) / heigth;
            biggerWidth = newWidthTop;
        }
        int[] newArgbArray = new int[biggerWidth * heigth];
        return stretchVertical(argbArray, newWidthTop, newWidthBottom, biggerWidth, width, heigth, procentualScalingHeight, newArgbArray);
    }

    /**
     * Stretches the rgb data vertical to the given top and bottom width and returns the new rgb data array.
     *
     *
     * @param argbArray the rgb data to be stretched.
     * @param newWidthTop the new top width of the rgb data.
     * @param newWidthBottom the new bottom width of the rgb data.
     * @param biggerWidth the bigger width of top and bottom width.
     * @param width the source width of the rgb data.
     * @param heigth the source heigth of the rgb data.
     * @param procentualScalingHeight the procentual scaling heigth(biggerWidth - smallerWidth)/heigthOfTheOriginalImage).
     * @param newArgbArray the new rgb data where the changes getting in.
     * @return return filled the newArgbArray with stretched changes.
     */
    public static int[] stretchVertical(int[] argbArray, int newWidthTop, int newWidthBottom, int biggerWidth, int width, int heigth, int procentualScalingHeight, int[] newArgbArray) {
        if (procentualScalingHeight == 0) {
            procentualScalingHeight++;
        }
        int length = newArgbArray.length;
        int oldLength = argbArray.length;
        int insideCurrentY = 0;
        int insideCurrentX = 0, outsideCurrentX = 0;
        int sum1 = (biggerWidth - newWidthTop) / 2;
        int sum2 = biggerWidth - ((biggerWidth - newWidthTop) / 2);
        for (int i = 0; i < length; i++) {

            outsideCurrentX = (outsideCurrentX + 1) % biggerWidth;
            if (outsideCurrentX == 0) {
                if (newWidthTop < newWidthBottom) {
                    newWidthTop += procentualScalingHeight;
                    sum1 = (biggerWidth - newWidthTop) / 2;
                    sum2 = biggerWidth - ((biggerWidth - newWidthTop) / 2);
                } else if (newWidthTop > newWidthBottom) {
                    newWidthTop -= procentualScalingHeight;
                    sum1 = (biggerWidth - newWidthTop) / 2;
                    sum2 = biggerWidth - ((biggerWidth - newWidthTop) / 2);
                }
                insideCurrentY++;
                insideCurrentX = 0;
            }
            if (outsideCurrentX >= sum1 && outsideCurrentX < sum2) {
                insideCurrentX = (insideCurrentX + 1) % newWidthTop;
                newArgbArray[i] = argbArray[scaledPixel(oldLength, width, heigth, newWidthTop, heigth, insideCurrentX, insideCurrentY)];
            } else {
                newArgbArray[i] = 000000;
            }
        }
        return newArgbArray;
    }

    /**
     * Returns the one scaled Pixel for the given new heigth and width.
     *
     * @param oldLength length of the rgb data source.
     * @param oldWidth the old width of the rgb data.
     * @param oldHeigth the old heigth of the rgb data.
     * @param newWidth the new width of the rgb data.
     * @param newHeigth the new heigth of the rgb data.
     * @param currentX the x position of the pixel to be scaled.
     * @param currentY the y position of the pixel to be scaled.
     * @return position of the scaled pixel in the old rgb data array.
     */
    public static int scaledPixel(int oldLength, int oldWidth, int oldHeigth, int newWidth, int newHeigth, int currentX, int currentY) {
        int targetArrayIndex;
        int verticalShrinkFactorPercent = ((newHeigth * 100) / oldHeigth);
        int horizontalScaleFactorPercent = ((newWidth * 100) / oldWidth);
        targetArrayIndex = ((currentX * 100) / horizontalScaleFactorPercent) + (oldWidth * ((currentY * 100) / verticalShrinkFactorPercent));
        if (targetArrayIndex >= oldLength) {
            targetArrayIndex = oldLength - 1;
        }
        if (targetArrayIndex < 0) {
            targetArrayIndex = 0;
        }
        return targetArrayIndex;
    }

    /**
     * Stretches the rgb data horizontal to the given left and rigth stretch factor and returns the new rgb data array.
     *
     * @param argbArray the rgb data to be stretched.
     * @param leftStrechFactor the stretch factor of the left.
     * @param rightStrechFactor the stretch factor of the rigth.
     * @param width the source width of the rgb data.
     * @param heigth the source heigth of the rgb data.
     * @return stretched rgb data array.
     */
    public static int[] stretchHorizontal(int[] argbArray, int leftStrechFactor, int rightStrechFactor, int width, int heigth) {
        int newHeigthLeft = (heigth * leftStrechFactor) / 100;
        int newHeigthRight = (heigth * rightStrechFactor) / 100;
        int procentualScalingWidth;
        int biggerHeigth;
        if (newHeigthLeft < newHeigthRight) {
            procentualScalingWidth = (newHeigthRight - newHeigthLeft) / width;
            biggerHeigth = newHeigthRight;
        } else {
            procentualScalingWidth = (newHeigthLeft - newHeigthRight) / width;
            biggerHeigth = newHeigthLeft;
        }
        int[] newArgbArray = new int[biggerHeigth * width];
        return stretchHorizontal(argbArray, newHeigthLeft, newHeigthRight, biggerHeigth, width, heigth, procentualScalingWidth, newArgbArray);
    }

    /**
     * Stretches the rgb data horizontal to the given left and heigth width and returns the new rgb data array.
     *
     *
     * @param argbArray the rgb data to be stretched.
     * @param newLeftHeigth the new left heigth of the rgb data.
     * @param newRigthHeigth the new rigth heigth of the rgb data.
     * @param biggerHeigth the bigger heigth of left and rigth heigth.
     * @param width the source width of the rgb data.
     * @param heigth the source heigth of the rgb data.
     * @param procentualScalingHeight the procentual scaling heigth(biggerHeigth - smallerSmaller)/widthOfTheOriginalImage).
     * @param newArgbArray the new rgb data where the changes getting in.
     * @return return the filled newArgbArray with stretched changes.
     */
    public static int[] stretchHorizontal(int[] argbArray, int newLeftHeigth, int newRigthHeigth, int biggerHeigth, int width, int heigth, int procentualScalingHeight, int[] newArgbArray) {
        if (procentualScalingHeight == 0) {
            procentualScalingHeight++;
        }
        int length = newArgbArray.length;
        int oldLength = argbArray.length;
        // x and y position int the new array
        int idX = 0, idY = 0;
        // x and y position of the old array
        int x = 0, y = 0;
        //position in the new array
        int whereIamAt = 0;
        // Heighth for goal
        int newHeigth = newLeftHeigth;
        //start Heigth to goal
        int startColumn = (biggerHeigth - newHeigth) / 2;
        int endColumn = biggerHeigth - ((biggerHeigth - newHeigth) / 2);

        for (int i = 0; i < length; i++) {

            if (startColumn <= idY && endColumn >= idY) {
                newArgbArray[whereIamAt] = argbArray[scaledPixel(oldLength, width, heigth, width, newHeigth, x, y)];
                y = (y + 1) % newHeigth;
            } else {
                newArgbArray[whereIamAt] = 000000;
            }
            idY = (idY + 1) % (biggerHeigth);
            whereIamAt = idX + (idY * width);
            if (idY == 0) {
                idX++;
                x++;
                y = 0;
                if (newLeftHeigth < newRigthHeigth) {
                    newHeigth += procentualScalingHeight;
                } else if (newLeftHeigth > newRigthHeigth) {
                    newHeigth -= procentualScalingHeight;
                }
                startColumn = (biggerHeigth - newHeigth) / 2;
                endColumn = biggerHeigth - ((biggerHeigth - newHeigth) / 2);
            }
        }
        return newArgbArray;
    }

    /**
     * Scales an rgb data unproportional in every new size you want bigger or smaller than the given original.
     *
     * @param rgbData the original rgbdata
     * @param newWidth the new width for the new rgbdata
     * @param newHeight the new height for the new rgbdata
     * @param oldWidth the width from the oiginal rgbdata
     * @param oldHeight the height from the oiginal rgbdata
     * @param newRgbData the new rgbdata has to be initialised
     */
    public static void scale(int[] rgbData, int newWidth, int newHeight, int oldWidth, int oldHeight, int[] newRgbData) {

        int x, y, dy;
        int srcOffset;
        int destOffset;

        // Calculate the pixel ratio ( << 10 )
        final int pixelRatioWidth = (1024 * oldWidth) / newWidth;
        final int pixelRatioHeight = (1024 * oldHeight) / newHeight;

        y = 0;
        destOffset = 0;
        while (y < newHeight) {
            dy = ((pixelRatioHeight * y) >> 10) * oldWidth;
            srcOffset = 0;

            x = 0;
            while (x < newWidth) {
                newRgbData[destOffset + x] = rgbData[dy + (srcOffset >> 10)];
                srcOffset += pixelRatioWidth;
                x++;
            }

            destOffset += newWidth;
            y++;
        }
    }

    /**
     * Scales an rgb data unproportional in every new size you want bigger or smaller than the given original.
     *
     * @param opacity the maximum alpha value, 255 (0xFF) means fully opaque, 0 fully transparent. Lower alpha values will be preserved.
     * @param rgbData the original rgbdata
     * @param newWidth the new width for the new rgbdata
     * @param newHeight the new height for the new rgbdata
     * @param oldWidth the width from the oiginal rgbdata
     * @param oldHeight the height from the oiginal rgbdata
     * @param newRgbData the new rgbdata has to be initialised
     */
    public static void scale(int opacity, int[] rgbData, int newWidth, int newHeight, int oldWidth, int oldHeight, int[] newRgbData) {
        int currentX = 0, currentY = 0;
        int oldLenght = rgbData.length;
        int newLength = newRgbData.length;
        int targetArrayIndex;
        int verticalShrinkFactorPercent = ((newHeight * 100) / oldHeight);
        int horizontalScaleFactorPercent = ((newWidth * 100) / oldWidth);
        int alpha = (opacity << 24); // is now 0xAA000000
        for (int i = 0; i < newLength; i++) {
            currentX = (currentX + 1) % newWidth;
            if (currentX == 0) {
                currentY++;
            }
            targetArrayIndex = ((currentX * 100) / horizontalScaleFactorPercent) + (oldWidth * ((currentY * 100) / verticalShrinkFactorPercent));
            if (targetArrayIndex >= oldLenght) {
                targetArrayIndex = oldLenght - 1;
            }
            if (targetArrayIndex < 0) {
                targetArrayIndex = 0;
            }
            int pixel = rgbData[targetArrayIndex];
            if (opacity != 255) {
                int pixelAlpha = (pixel & 0xff000000) >>> 24;
                if (pixelAlpha > opacity) {
                    pixel = (pixel & 0x00ffffff) | alpha;
                }
            }
            newRgbData[i] = pixel;
        }
    }

    /**
     * Sets the specified transparency to the RGB data.
     *
     * @param transparency the transparency between 0 (fully transparent) and 255 (fully opaque)
     * @param data the RGB data
     */
    public static void setTransparency(int transparency, int[] data) {
        transparency = (transparency << 24); // is now 0xtt000000
        for (int i = 0; i < data.length; i++) {
            data[i] = (data[i] & 0x00ffffff) | transparency;
        }
    }

    /**
     * Sets the specified transparency to the RGB data, but only for pixels that are not full transparent already.
     *
     * @param transparency the transparency between 0 (fully transparent) and 255 (fully opaque)
     * @param data the RGB data
     */
    public static void setTransparencyOnlyForOpaque(int transparency, int[] data) {
        transparency = (transparency << 24); // is now 0xtt000000
        for (int i = 0; i < data.length; i++) {
            int pixel = data[i];
            if ((pixel & 0xff000000) != 0) {
                data[i] = (pixel & 0x00ffffff) | transparency;
            }
        }
    }

    /**
     * Sets the specified transparency to the RGB data, but only for pixels that are not full transparent already.
     *
     * @param transparency the transparency between 0 (fully transparent) and 255 (fully opaque)
     * @param data the RGB data
     * @param onlyForPixelsWithGreaterAlphas true when the given transparency should be only applied to pixels that have a greater alpha value
     * @throws NullPointerException when data is null
     */
    public static void setTransparencyOnlyForOpaque(int transparency, int[] data, boolean onlyForPixelsWithGreaterAlphas) {
        int alpha = (transparency << 24); // is now 0xtt000000
        for (int i = 0; i < data.length; i++) {
            int pixel = data[i];
            int pixelApha = (pixel & 0xff000000) >>> 24;
            if ((pixelApha > transparency)
                    || (!onlyForPixelsWithGreaterAlphas && (pixelApha != 0))) {
                data[i] = (pixel & 0x00ffffff) | alpha;
            }
        }
    }

    /**
     * Returns the transformed color for the running device. Some devices
     * tend to transform color to a brighter or darker color due
     * to the changes in device palettes.
     *
     * @param color the original color
     * @return the transformed color
     */
    public static int getDeviceColor(int color) {
        //#if polish.midp2 || (!polish.midp && polish.usePolishGui)
        Graphics graphicsBuffer;
        Image imageBuffer = Image.createImage(1, 1);
        graphicsBuffer = imageBuffer.getGraphics();

        graphicsBuffer.setColor(color);
        graphicsBuffer.fillRect(0, 0, 1, 1);

        int[] rgbData = new int[1 * 1];
        imageBuffer.getRGB(rgbData, 0, 1, 0, 0, 1, 1);

        return rgbData[0];
        //#else
        //# return color;
        //#endif
    }

    /*******************************************************************************
     * Helper function for animation a HSL color change.
     * <p>
     * Linear interpolations will be done between sourceImage original colors and parameter
     * HSL values in accordance to parameter permille.
     *
     * @param sourceImg source image
     * @param H Hue adjustment as int, must be [-360;360]
     * @param S Saturation adjustment as int, must be [-100;100]
     * @param L Light adjustment as int, must be [-100;100]
     * @param permille intrepolations factor, must be [0;1000]
     * @return the transfored RGB image
     *******************************************************************************/
    public static RgbImage animateColorByHSL(final RgbImage sourceImg, int H, int S, int L, int permille) {
        //linear interpolation
        int tH = FP.round(FP.mul(FP.div(FP.intToFix(H), FP.intToFix(1000)), FP.intToFix(permille))),
                tS = FP.round(FP.mul(FP.div(FP.intToFix(S), FP.intToFix(1000)), FP.intToFix(permille))),
                tL = FP.round(FP.mul(FP.div(FP.intToFix(L), FP.intToFix(1000)), FP.intToFix(permille)));

        //#mdebug debug
        System.out.println("tH: " + tH);
        System.out.println("tS: " + tS);
        System.out.println("tL: " + tL);
        //#enddebug

        return changeColorByHSL(sourceImg, tH, tS, tL);
    }

    /*******************************************************************************
     * Changes the colors of a RgbImage according to HSL colorspace parameters.
     *
     * @param sourceImg source image
     * @param H Hue adjustment as int, must be [-360;360]
     * @param S Saturation adjustment as int, must be [-100;100]
     * @param L Light adjustment as int, must be [-100;100]
     * @return the transfored RGB image
     *******************************************************************************/
    public static RgbImage changeColorByHSL(final RgbImage sourceImg, int H, int S, int L) {
        return changeColorByHSL(sourceImg, H, S, L, 100);
    }

    /*******************************************************************************
     * Changes the colors of a RgbImage according to HSL colorspace parameters.
     *
     * @param sourceImg source image
     * @param H Hue adjustment as int, must be [-360;360]
     * @param S Saturation adjustment as int, must be [-100;100]
     * @param L Light adjustment as int, must be [-100;100]
     * @param alpha transparency to be applied, must be [0;100]
     * @return the transfored RGB image
     *******************************************************************************/
    public static RgbImage changeColorByHSL(final RgbImage sourceImg, int H, int S, int L, int alpha) {
        if (null == sourceImg || null == sourceImg.getRgbData() || sourceImg.getRgbData().length == 0) {
            return null;
        }

        final int[] sourceRGB = sourceImg.getRgbData();
        int[] outPutRGB = new int[sourceRGB.length];

        //Ensure that HSL values are properly constrained
        int tH = H > 360 || H < -360 ? H % 360 : H,
                tS = S > 100 || S < -100 ? S % 100 : S,
                tL = L > 100 || L < -100 ? L % 100 : L,
                alphaMask = Math.abs(alpha > 100 || alpha < -100 ? alpha % 100 : alpha);

        //clamp to [0,1.0] values
        tH = FP.clamp(FP.intToFix(tH), FP.FIX_360);
        tS = FP.clamp(FP.intToFix(tS), FP.intToFix(100));
        tL = FP.clamp(FP.intToFix(tL), FP.intToFix(100));

        //alpha mask to apply
        alphaMask = FP.round(FP.mul(FP.intToFix(alphaMask), FP.div(FP.intToFix(255), FP.intToFix(100))));
        alphaMask = 0xFF000000 & (alphaMask << 24);

        //init hash
        IntHashMap ihm = new IntHashMap();

        try {
            int[] rgb = new int[3], hsl = new int[3];
            int argb = 0, argbOutput = 0;
            int cMax_fix = FP.intToFix(255);

            for (int i = sourceRGB.length; --i >= 0;) {
                argb = sourceRGB[i];

                //has color value already been processed and is therefore cashed?
                if (null == ihm.get(argb)) {
                    //Clamping [0;255] bytes values to [0.0;1.0] fix values
                    rgb[0] = FP.clamp(FP.intToFix((argb >> 16) & 0xFF), cMax_fix);
                    rgb[1] = FP.clamp(FP.intToFix((argb >> 8) & 0xFF), cMax_fix);
                    rgb[2] = FP.clamp(FP.intToFix(argb & 0xFF), cMax_fix);

                    //#mdebug debug
                    System.out.println("clamp R: " + FP.toString(rgb[0]));
                    System.out.println("clamp G: " + FP.toString(rgb[1]));
                    System.out.println("clamp B: " + FP.toString(rgb[2]));
                    //#enddebug

                    //Going to HSL colorspace
                    RGBtoHSL(rgb, 0, hsl, 0);

                    hsl[0] += tH;
                    hsl[1] += tS;
                    hsl[2] += tL;

                    //ensure constraints
                    if (hsl[0] > FP.FIX_ONE) {
                        hsl[0] -= FP.FIX_ONE;
                    }
                    if (hsl[0] < 0) {
                        hsl[0] += FP.FIX_ONE;
                    }

                    if (hsl[1] > FP.FIX_ONE) {
                        hsl[1] = FP.FIX_ONE;
                    }
                    if (hsl[1] < 0) {
                        hsl[1] = 0;
                    }

                    if (hsl[2] > FP.FIX_ONE) {
                        hsl[2] = FP.FIX_ONE;
                    }
                    if (hsl[2] < 0) {
                        hsl[2] = 0;
                    }

                    //Back to RGB colorspace
                    HSLtoRGB(hsl, 0, rgb, 0);

                    //#mdebug debug
                    System.out.println("converted R: " + (FP.round(FP.mul(rgb[0], cMax_fix)) & 0xFF));
                    System.out.println("converted G: " + (FP.round(FP.mul(rgb[1], cMax_fix)) & 0xFF));
                    System.out.println("converted B: " + (FP.round(FP.mul(rgb[2], cMax_fix))));
                    //#enddebug

                    argbOutput =
                            alphaMask | (FP.round(FP.mul(rgb[0], cMax_fix)) & 0xFF) << 16 | (FP.round(FP.mul(rgb[1], cMax_fix)) & 0xFF) << 8 | (FP.round(FP.mul(rgb[2], cMax_fix)));
                    outPutRGB[i] = argbOutput;

                    //add to hash
                    ihm.put(argb, new Integer(argbOutput));
                } else {
                    outPutRGB[i] = ((Integer) ihm.get(argb)).intValue();
                }
            }
        } catch (Throwable t) {
            //#mdebug error
            System.out.println("Error adjusting RgbImage colorspace: " + t.getMessage());
            t.printStackTrace();
            //#enddebug

            //clean up
            ihm = null;
            outPutRGB = null;
            System.gc();

            //return original sourceImg
            return sourceImg;
        }

        return new RgbImage(outPutRGB, sourceImg.getWidth());
    }

    /*******************************************************************************
     * Converts a RGB color value to HSL.
     * <p>
     * Uses fixed point math
     *
     * @param RGB_fix input array of RGB values clamped to [0.0;1.0] in fix
     * @param rgbIndice array indice
     * @param HSL_fix output array of HSL values clamped to [0.0;1.0] in fix
     * @param hslIndice array indice
     *******************************************************************************/
    public static void RGBtoHSL(int[] RGB_fix, int rgbIndice, int[] HSL_fix, int hslIndice) {

        int H_fix = 0, S_fix, L_fix;

        int R_fix = RGB_fix[rgbIndice],
                G_fix = RGB_fix[rgbIndice + 1],
                B_fix = RGB_fix[rgbIndice + 2];

        int colorMax_fix = FP.max(R_fix, FP.max(G_fix, B_fix)),
                colorMin_fix = FP.min(R_fix, FP.min(G_fix, B_fix)),
                colorDelta = colorMax_fix - colorMin_fix;

        //Light
        L_fix = (colorMin_fix + colorMax_fix) >> 1;

        if (colorDelta == 0) //is color gray or white?
        {
            H_fix = 0;
            S_fix = 0; //HSL set grey
        } else {

            //Saturation
            if (L_fix < FP.FIX_HALF) {
                S_fix = FP.div(colorDelta, colorMax_fix + colorMin_fix);
            } else {
                S_fix = FP.div(colorDelta, FP.intToFix(2) - colorMax_fix - colorMin_fix);
            }

            int six_fix = FP.intToFix(6);
            int deltaHalf = colorDelta >> 1;

            int tR_fix = FP.div(FP.div(colorMax_fix - R_fix, six_fix) + deltaHalf, colorDelta),
                    tG_fix = FP.div(FP.div(colorMax_fix - G_fix, six_fix) + deltaHalf, colorDelta),
                    tB_fix = FP.div(FP.div(colorMax_fix - B_fix, six_fix) + deltaHalf, colorDelta);

            if (R_fix == colorMax_fix) {
                H_fix = tB_fix - tG_fix;
            } else if (G_fix == colorMax_fix) {
                H_fix = FP.div(FP.FIX_ONE, FP.intToFix(3)) + tR_fix - tB_fix;
            } else if (B_fix == colorMax_fix) {
                H_fix = FP.div(FP.intToFix(2), FP.intToFix(3)) + tG_fix - tR_fix;
            }

            if (H_fix < 0) {
                H_fix += FP.FIX_ONE;
            }

            if (H_fix > FP.FIX_ONE) {
                H_fix -= FP.FIX_ONE;
            }
        }

        HSL_fix[hslIndice] = H_fix;
        HSL_fix[hslIndice + 1] = S_fix;
        HSL_fix[hslIndice + 2] = L_fix;
    }

    /*******************************************************************************
     * Converts a HSL color value to RGB.
     * <p>
     * Uses fixed point math
     *
     * @param HSL_fix input array of HSL values clamped to [0.0;1.0] in fix
     * @param hslIndice array indice
     * @param RGB_fix output array of RGB values clamped to [0.0;1.0] in fix
     * @param rgbIndice array indice
     *******************************************************************************/
    public static void HSLtoRGB(int[] HSL_fix, int hslIndice, int[] RGB_fix, int rgbIndice) {

        int v1_fix, v2_fix;

        int L_fix = HSL_fix[hslIndice + 2]; //light

        if (L_fix == 0) //is grey
        {
            RGB_fix[rgbIndice] = L_fix;                     //RGB results = From 0 to 255
            RGB_fix[rgbIndice + 1] = L_fix;
            RGB_fix[rgbIndice + 2] = L_fix;

        } else {

            int H_fix = HSL_fix[hslIndice]; //hue
            int S_fix = HSL_fix[hslIndice + 1];	//saturation

            if (L_fix < FP.FIX_HALF) {
                v2_fix = FP.mul(L_fix, FP.FIX_ONE + S_fix);
            } else {
                v2_fix = (L_fix + S_fix) - FP.mul(L_fix, S_fix);
            }

            v1_fix = (L_fix << 1) - v2_fix;

            int onethird_fix = FP.div(FP.FIX_ONE, FP.intToFix(3));

            RGB_fix[rgbIndice] = HuetoRGB(v1_fix, v2_fix, H_fix + onethird_fix);                     //RGB results = From 0 to 255
            RGB_fix[rgbIndice + 1] = HuetoRGB(v1_fix, v2_fix, H_fix);
            RGB_fix[rgbIndice + 2] = HuetoRGB(v1_fix, v2_fix, H_fix - onethird_fix);
        }
    }

    /*******************************************************************************
     * Intermediate function to help convert Hue (H) to RGB
     * <p>
     * Uses fixed point math
     *
     * @param v1_fix
     * @param v2_fix
     * @param H_fix value of hue
     * @param rgb single colorchannel value
     *******************************************************************************/
    private static int HuetoRGB(int v1_fix, int v2_fix, int H_fix) {
        if (H_fix < 0) {
            H_fix += FP.FIX_ONE;
        }
        if (H_fix > FP.FIX_ONE) {
            H_fix -= FP.FIX_ONE;
        }
        if (FP.mul(FP.intToFix(6), H_fix) < FP.FIX_ONE) {
            return FP.mul(FP.mul(v1_fix + (v2_fix - v1_fix), FP.intToFix(6)), H_fix);
        }
        if (H_fix << 1 < FP.FIX_ONE) {
            return v2_fix;
        }
        if (FP.mul(FP.intToFix(3), H_fix) < FP.intToFix(2)) {
            return FP.mul(v1_fix + (v2_fix - v1_fix),
                    FP.mul(FP.div(FP.intToFix(2), FP.intToFix(3)) - H_fix, FP.intToFix(6)));
        }

        return v1_fix;
    }

    /*******************************************************************************
     * Helper function for animating a RGB color balance change.
     * <p>
     * Linear interpolations will be done between sourceImage original colors and parameter
     * RGB values in accordance to parameter permille.
     *
     * @param sourceImg source image
     * @param R ged channel adjustment as int, must be [-100;100]
     * @param G Green channel adjustment as int, must be [-100;100]
     * @param B Blue adjustment as int, must be [-100;100]
     * @param permille intrepolations factor, must be [0;1000]
     *******************************************************************************/
    public static RgbImage animateColorBalance(final RgbImage sourceImg, int R, int G, int B, int permille) {
        //linear interpolation
        int tR = FP.round(FP.mul(FP.div(FP.intToFix(R), FP.intToFix(1000)), FP.intToFix(permille))),
                tG = FP.round(FP.mul(FP.div(FP.intToFix(G), FP.intToFix(1000)), FP.intToFix(permille))),
                tB = FP.round(FP.mul(FP.div(FP.intToFix(B), FP.intToFix(1000)), FP.intToFix(permille)));

        //#mdebug debug
        System.out.println("tR: " + tR);
        System.out.println("tG: " + tG);
        System.out.println("tB: " + tB);
        //#enddebug

        return changeColorByHSL(sourceImg, tR, tG, tB);
    }

    /*******************************************************************************
     * Changes the color balance of a RgbImage.
     *
     * @param sourceImg source image
     * @param R Red channel adjustment as int, must be [-100;100]
     * @param G Green channel adjustment as int, must be [-100;100]
     * @param B Blue adjustment as int, must be [-100;100]
     *******************************************************************************/
    public static RgbImage changeColorBalance(final RgbImage sourceImg, int R, int G, int B) {
        return changeColorBalance(sourceImg, R, G, B, 100);
    }

    /*******************************************************************************
     * Changes the color balance of a RgbImage.
     *
     * @param sourceImg source image
     * @param R Red channel adjustment as int, must be [-100;100]
     * @param G Green channel adjustment as int, must be [-100;100]
     * @param B Blue adjustment as int, must be [-100;100]
     * @param alpha transparency to be applied, must be [0;100]
     *******************************************************************************/
    public static RgbImage changeColorBalance(final RgbImage sourceImg, int R, int G, int B, int alpha) {
        if (null == sourceImg || null == sourceImg.getRgbData() || sourceImg.getRgbData().length == 0) {
            return null;
        }

        final int[] sourceRGB = sourceImg.getRgbData();
        int[] outPutRGB = new int[sourceRGB.length];

        //Ensure that RGB values are properly constrained
        int tR = R > 100 || R < -100 ? R % 100 : R,
                tG = G > 100 || G < -100 ? G % 100 : G,
                tB = B > 100 || B < -100 ? B % 100 : B,
                alphaMask = Math.abs(alpha > 100 || alpha < -100 ? alpha % 100 : alpha);

        //alpha mask to apply
        alphaMask = FP.round(FP.mul(FP.intToFix(alphaMask), FP.div(FP.intToFix(255), FP.intToFix(100))));
        alphaMask = 0xFF000000 & (alphaMask << 24);

        try {
            int[] rgb = new int[3], newRgb = new int[3];
            int argb = 0;
            int cMax = 255;

            for (int i = sourceRGB.length; --i >= 0;) {
                argb = sourceRGB[i];

                //extracting channels
                rgb[0] = (argb >> 16) & 0xFF;
                rgb[1] = (argb >> 8) & 0xFF;
                rgb[2] = argb & 0xFF;

                //#mdebug debug
                System.out.println("R: " + FP.toString(rgb[0]));
                System.out.println("G: " + FP.toString(rgb[1]));
                System.out.println("B: " + FP.toString(rgb[2]));
                //#enddebug

                newRgb[0] = (rgb[0] + tR);
                newRgb[1] = (rgb[1] + tG);
                newRgb[2] = (rgb[2] + tB);

                //ensure constraints
                if (newRgb[0] > cMax) {
                    newRgb[0] = cMax;
                }
                if (newRgb[0] < 0) {
                    newRgb[0] = 0;
                }

                if (newRgb[1] > cMax) {
                    newRgb[1] = cMax;
                }
                if (newRgb[1] < 0) {
                    newRgb[1] = 0;
                }

                if (newRgb[2] > cMax) {
                    newRgb[2] = cMax;
                }
                if (newRgb[2] < 0) {
                    newRgb[2] = 0;
                }

                //#mdebug debug
                System.out.println("converted R: " + newRgb[0]);
                System.out.println("converted G: " + newRgb[1]);
                System.out.println("converted B: " + newRgb[2]);
                //#enddebug

                outPutRGB[i] =
                        alphaMask | (newRgb[0] & 0xFF) << 16 | (newRgb[1] & 0xFF) << 8 | (newRgb[2]);
            }
        } catch (Throwable t) {
            //#mdebug error
            System.out.println("Error balancing RgbImage: " + t.getMessage());
            t.printStackTrace();
            //#enddebug
        }

        return new RgbImage(outPutRGB, sourceImg.getWidth());
    }
    /*******************************************************************************
     * Fixed point number representation of rgb channel desaturation factors
     *******************************************************************************/
    private static final int TO_GRAY_RED_DESATURATOR_FIX = 19595; //(int)(65536.0f*0.299);
    private static final int TO_GRAY_GREEN_DESATURATOR_FIX = 38469; //(int)(65536.0f*0.587);
    private static final int TO_GRAY_BLUE_DESATURATOR_FIX = 7471; //(int)(65536.0f*0.114);

    /*******************************************************************************
     * Changes a RgbImage to grayscale colorspace
     *
     * @param rgbImg
     * @return a new RgbImage object in grayscale colorspace
     *******************************************************************************/
    public static RgbImage changeColorToGrayScale(RgbImage rgbImg) {
        RgbImage gray = null;

        if (null != rgbImg) {
            int[] copy = rgbImg.copyRgbData();

            for (int i = copy.length; --i >= 0;) {
                copy[i] = pixelToGrayScale(copy[i]);
            }

            gray = new RgbImage(copy, rgbImg.getWidth());
        }
        return gray;
    }

    /*******************************************************************************
     * Helper funciton for changeColorToGrayScale()
     * <p>
     * Converts a single ARGB pixel to gray scale
     * <p>
     * Luminance = 0.299 * R + 0.587 * G + 0.114 * B
     * <p>
     * Uses Fixed point math
     *
     * @param pixel
     * @return int grayscale pixel
     *******************************************************************************/
    private static int pixelToGrayScale(int pixel) {

        int L_fix =
                FP.mul(FP.intToFix((byte) (pixel >> 16)), TO_GRAY_RED_DESATURATOR_FIX) + FP.mul(FP.intToFix((byte) (pixel >> 8)), TO_GRAY_GREEN_DESATURATOR_FIX) + FP.mul(FP.intToFix((byte) (pixel)), TO_GRAY_BLUE_DESATURATOR_FIX);

        byte grayValue = (byte) (FP.fixToInt(L_fix) & 0xFF);

        return (pixel & 0xFF000000) | (grayValue << 16) | (grayValue << 8) | grayValue;
    }

    //#if polish.midp2
    /*******************************************************************************
     * Draws one RgbImage onto another RgbImage.
     * <p>
     * Alpha Composition is handled correctly making it possible to draw semi-transparent
     * images ontop another one.
     *
     * @param target the RgbImage that will be drawn onto
     * @param other the RgbImage that will be drawn onto the target image
     * @param x horizontal target coordinate of other picture. Anchor top|left.
     * @param y vertical target coordinate of other picture. Anchor top|left.
     *******************************************************************************/
    public static void drawRgbImageOntoOther(RgbImage target, RgbImage other, int x, int y) {
        drawRgbImageOntoOther(target, other, x, y, 0, 0, target.getWidth(), target.getHeight());
    }

    /*******************************************************************************
     * Draws one RgbImage onto another RgbImage.
     * <p>
     * Alpha Composition is handled correctly making it possible to draw semi-transparent
     * images ontop another one.
     * <p>
     * Clipping area is optionally set & respected.
     *
     * @param target the RgbImage that will be drawn onto
     * @param other the RgbImage that will be drawn onto the target image
     * @param x horizontal target coordinate of other picture. Anchor top|left.
     * @param y vertical target coordinate of other picture. Anchor top|left.
     * @param clipX x coordinate of topleft corner of clipping rectangle
     * @param clipY y coordinate of topleft corner of clipping rectangle
     * @param clipW width of clipping rectangle
     * @param clipH height of clipping rectangle
     *******************************************************************************/
    public static void drawRgbImageOntoOther(RgbImage target, RgbImage other, int x, int y, int clipX, int clipY, int clipW, int clipH) {

        int localClipX, localClipY, localClipW, localClipH;

        int subX = 0, subY = 0;

        //is image outside clipping area bounds?
        if (x >= clipX + clipW || y >= clipY + clipH || (x + other.getWidth()) < clipX || (y + other.getHeight()) < clipY) {
            return;
        }

        //is image enclosed by clipping area?
        if (x + other.getWidth() < clipX + clipW && x > clipX && y + other.getHeight() < clipY + clipH && y > clipY) {
            localClipX = x;
            localClipY = y;
            localClipW = other.getWidth();
            localClipH = other.getHeight();
        } else {
            //else, input image should be subregion'ed according to clipping area
            int width = other.getWidth(), height = other.getHeight();
            if (x < clipX) {
                subX = clipX - x;
                localClipX = clipX;
            } else {
                localClipX = x;
            }
            if (y < clipY) {
                subY = clipY - y;
                localClipY = clipY;
            } else {
                localClipY = y;
            }
            if ((x + width) > (clipX + clipW)) {
                localClipW = (clipX + clipW) - x - subX;
            } else {
                localClipW = width - subX;
            }
            if ((y + height) > (clipY + clipH)) {
                localClipH = (clipY + clipH) - y - subY;
            } else {
                localClipH = height - subY;
            }
        }

        //fect subregion
        int[] croppedOther = cropImage(other, subX, subY, localClipW, localClipH);

        int sourceOffset = localClipY * target.getWidth() + localClipX,
                targetOffset = 0;

        //apply image using alpha composition 'over' operation
        int j;
        int[] argbSource = target.getRgbData();
        for (int i = 0; i < localClipH; ++i) {
            for (j = 0; j < localClipW; ++j) {
                argbSource[sourceOffset + j] = combineColors(argbSource, sourceOffset + j, croppedOther, targetOffset + j);
            }
            sourceOffset += target.getWidth();
            targetOffset += localClipW;
        }
    }

    /*******************************************************************************
     * Helper function for drawRgbImageOntoOther()
     * <p>
     * Combines two color values using 'over' operator alpha composition.
     *
     * @param argbSource int[] source image color array
     * @param sourceIndex index in source array
     * @param argbOverlay int[] target image color array
     * @param overlayIndex index in target array
     * @return combined color
     *******************************************************************************/
    private static int combineColors(int[] argbSource, int sourceIndex, int[] argbOverlay, int overlayIndex) {

        int source = argbSource[sourceIndex];
        int over = argbOverlay[overlayIndex];

        //is source fully transparent?
        if (source == 0) {
            return over;
        }

        //is over fully transparent?
        if (over == 0) {
            return source;
        }

        //is over fully opaque?
        if ((0xFF & (over >> 24)) == 255) {
            return over;
        }

        //fetch, convert and clamp alpha channel of overlay picture
        int alphaOver_fix = FP.clamp(FP.intToFix(0xFF & (over >> 24)), FP.intToFix(255));

        //compute 'over' operator for all channels
        int alphaResult = overOperator(0xFF & (source >> 24), 0xFF & (over >> 24), alphaOver_fix);
        int redResult = overOperator(0xFF & (source >> 16), 0xFF & (over >> 16), alphaOver_fix);
        int greenResult = overOperator(0xFF & (source >> 8), 0xFF & (over >> 8), alphaOver_fix);
        int blueResult = overOperator(0xFF & source, 0xFF & over, alphaOver_fix);

        return (alphaResult << 24) | (redResult << 16) | (greenResult << 8) | blueResult;
    }

    /*******************************************************************************
     * Helper function for drawRgbImageOntoOther()
     * <p>
     * Alpha composition 'over' operation.
     * <p>
     * Cr: Color Result
     * Cs: Channel Source
     * Co: Channel Over
     * Ao: Alpha Over
     * Cr = Cs*(1-Ao) + Ao*Co
     *
     * @param channelSource channel value of source color [0..255]
     * @param channelOver channel value of overlay color [0..255]
     * @param alpha_fix alpha value in fix point precision, clamped to [0.0..1.0]
     * @return resulting channel value [0..255]
     *******************************************************************************/
    private static int overOperator(int channelSource, int channelOver, int alpha_fix) {
        return FP.fixToInt(FP.mul(FP.intToFix(channelSource), FP.FIX_ONE - alpha_fix) + FP.mul(FP.intToFix(channelOver), alpha_fix));
    }

    /*******************************************************************************
     * Helper function for drawRgbImageOntoOther()
     * <p>
     * Crops the input image according to parameter clipping area.
     *
     * @param rgbImg
     * @param x
     * @param y
     * @param width
     * @param height
     * @return
     *******************************************************************************/
    private static int[] cropImage(RgbImage rgbImg, int x, int y, int width, int height) {

        if (x == 0 && y == 0 && rgbImg.getWidth() == width && rgbImg.getHeight() == height) {
            return rgbImg.getRgbData();
        }

        int sourceOffset = y * rgbImg.getWidth() + x, targetOffset = 0;
        int[] sourceArgb = rgbImg.getRgbData();
        int[] newArgb = new int[width * height];

        for (int i = height; --i >= 0;) {
            System.arraycopy(sourceArgb, sourceOffset, newArgb, targetOffset, width);
            sourceOffset += rgbImg.getWidth();
            targetOffset += width;
        }
        return newArgb;
    }
    //#endif

    //#if tmp.supportImageOperations
    /**
     * Circular clipping effect on a RgbImage
     *
     * @param rgbImg image that should be masked
     * @param circX horizontal center coordinate of circle
     * @param circY vertical center coordinate of circle
     * @param circWidth width of circle in pixels
     * @param circHeight height of circle in pixels
     * @param invert true if mask should invert
     */
    public static void clipRgbImageCirkular(RgbImage rgbImg, int circX, int circY, int circWidth, int circHeight, boolean invert) {
        //produce circular mask
        Image img = Image.createImage(rgbImg.getWidth(), rgbImg.getHeight());
        Graphics g = img.getGraphics();
        g.setColor(0xFFFFFFFF);
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        g.setColor(0x00000000);
        g.fillArc(circX - (circWidth >> 1), circY - (circHeight >> 1), circWidth, circHeight, 0, 360);
        int[] maskRgb = new int[img.getWidth() * img.getHeight()];
        img.getRGB(maskRgb, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());
        RgbImage mask = new RgbImage(maskRgb, img.getWidth());

        //apply mask
        applyMaskOntoRgbImage(rgbImg, mask, invert);
    }
    //#endif

    //#if tmp.supportImageOperations
    /**
     * Applies an mask RgbImage onto another Rgbimage.
     * <p>
     * Mask MUST be in greyscale or B/W
     *
     * @param source the RgbImage that is to be masked
     * @param mask the RgbImage mask, must be grey scale or B/W
     * @param invert true if black mask colors should be masked, false if white mask colors should be masked
     */
    public static void applyMaskOntoRgbImage(RgbImage source, RgbImage mask, boolean invert) {
        int[] maskRgb = mask.getRgbData();
        int[] sourceRgb = source.getRgbData();

        for (int i = maskRgb.length; --i >= 0;) {
            if (invert) {
                sourceRgb[i] = ((0xFF & maskRgb[i]) << 24) | (0x00FFFFFF & sourceRgb[i]);
            } else {
                sourceRgb[i] = (255 - (0xFF & maskRgb[i])) << 24 | (0x00FFFFFF & sourceRgb[i]);
            }
        }
    }
    //#endif

    //#if tmp.supportImageOperations && polish.hasFloatingPoint
    /**
     * Scales an image to fit into the specified available width and height while maintaining the ratio.
     *
     * @param source the source image
     * @param availableWidth the new width for the new rgbdata
     * @param availableHeight the new height for the new rgbdata
     * @return the resulting image
     */
    public static Image scaleToFit(Image source, int availableWidth, int availableHeight) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        if (sourceWidth > availableWidth || sourceHeight > availableHeight) {
            int[] rgbData = new int[sourceWidth * sourceHeight];
            source.getRGB(rgbData, 0, sourceWidth, 0, 0, sourceWidth, sourceHeight);

            double availableRatio = (double) availableWidth / (double) availableHeight;
            double sourceRatio = (double) sourceWidth / (double) sourceHeight;

            int targetWidth = availableWidth;
            int targetHeight = availableHeight;

            if (availableRatio < sourceRatio) {
                targetHeight = (int) (targetWidth / sourceRatio);
            } else {
                targetWidth = (int) (targetHeight * sourceRatio);
            }

            int[] newRgbData = new int[targetWidth * targetHeight];
            scale(rgbData, targetWidth, targetHeight, sourceWidth, sourceHeight, newRgbData);

            return Image.createRGBImage(newRgbData, targetWidth, targetHeight, true);
        }

        return source;
    }
    //#endif

    //#if tmp.supportImageOperations
    /**
     * Scales an image to the specified dimension
     *
     * @param source the image
     * @param width the desired width of the resulting image
     * @param height the desired height of the resulting image
     * @return the (possibly) scaled image
     * @throws NullPointerException when source is null
     */
    public static Image scale(Image source, int width, int height) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        if (sourceWidth != width || sourceHeight != height) {
            int[] rgbData = new int[sourceWidth * sourceHeight];
            source.getRGB(rgbData, 0, sourceWidth, 0, 0, sourceWidth, sourceHeight);
            int[] newRgbData = new int[width * height];
            scale(rgbData, width, height, sourceWidth, sourceHeight, newRgbData);

            return Image.createRGBImage(newRgbData, width, height, true);
        }

        return source;
    }
    //#endif
    
    //#if tmp.supportImageOperations
    /**
     * Applies the alpha values of a RgbImage to another RgbImage
     * @param source the source RgbImage
     * @param alpha the RgbImage to use as the alpha map
     */
    public static void applyAlphaOntoRgbImage(RgbImage source, RgbImage alpha) {
    	int[] sourceData = source.getRgbData();
        int[] alphaData = alpha.getRgbData();
        
		for (int index = 0; index < sourceData.length; index++) {
			sourceData[index] = (0x00FFFFFF & sourceData[index]) | ((alphaData[index] & 0xFF000000)); 
		}
    }
    //#endif
    
    //#if polish.midp2
    /**
     * Returns the pixel of the given image at the given x/y offset
     * @param image the RgbImage
     * @param x the x offset
     * @param y the y offset
     * @return the pixel color
     */
    public static int getPixelColor(Image image, int x, int y) {
    	RgbImage rgbImage = new RgbImage(image,true);
    	return getPixelColor(rgbImage, x, y);
    }
    //#endif
    
    /**
     * Returns the pixel of the given RgbImage at the given x/y offset
     * @param rgbImage the RgbImage
     * @param x the x offset
     * @param y the y offset
     * @return the pixel;
     */
    public static int getPixelColor(RgbImage rgbImage, int x, int y) {
    	int[] rgbData = rgbImage.getRgbData();
    	// get the pixel offset
    	int offset = (y * rgbImage.getWidth()) + x;
    	
    	if(offset < rgbData.length) {
    		return rgbData[offset];
    	} else {
    		throw new IllegalArgumentException("offset is out of the image bounds");
    	}
    }
    
}
 
