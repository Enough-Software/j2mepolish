//#condition polish.usePolishGui && polish.hasFloatingPoint && !polish.blackberry && !polish.midp1
/*
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

package de.enough.polish.ui.gaugeviews;

import de.enough.polish.ui.ClippingRegion;
import de.enough.polish.ui.Gauge;
import de.enough.polish.ui.Image;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemView;
import de.enough.polish.ui.Point;
import de.enough.polish.ui.RgbFilter;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.rgbfilters.DropShadowRgbFilter;
import de.enough.polish.util.ImageUtil;
import de.enough.polish.util.MathUtil;
import de.enough.polish.util.RgbImage;
import java.io.IOException;
import javax.microedition.lcdui.Graphics;

/**
 * This class implements a Clock-like view for gauges.
 *
 * @author Ovidiu Iliescu
 */
public class ClockInstrumentGaugeView extends ItemView {

    transient Image backgroundImage = null ;
    transient Image needleImage = null;
    transient Image needleShadowImage = null;
    int needleX = -1;
    int needleY = -1;
    int needleCenterX = -1;
    int needleCenterY = -1;

    int shadowCenterX = -1;
    int shadowCenterY = -1;
    int shadowOffsetX = 0;
    int shadowOffsetY = 0;

    int startAngle = 180;
    int endAngle = 0;

    long lastAnimationTime = 0;
    transient Gauge gauge = null;

    //#if polish.css.needle-filter
    private RgbFilter[] needleFilters = null ;
    //#endif

    //#if polish.css.face-filter
    private RgbFilter[] faceFilters = null ;
    //#endif


    /**
     * (non-javadoc)
     * @see de.enough.polish.ui.ItemView#initContent(de.enough.polish.ui.Item, int, int, int)
     */
    protected void initContent(Item parent, int firstLineWidth, int availWidth, int availHeight) {

        gauge = (Gauge) parent;
        contentWidth = this.backgroundImage.getWidth();
        contentHeight = this.backgroundImage.getHeight();
            
    }

    /**
     * (non-javadoc)
     * @see de.enough.polish.ui.ItemView#setStyle(de.enough.polish.ui.Style) 
     */
    protected void setStyle(Style style)
    {
        super.setStyle(style);
    }

    /**
     * (non-javadoc)
     * @see de.enough.polish.ui.ItemView#setStyle(de.enough.polish.ui.Style, boolean) 
     */
    protected void setStyle(Style style, boolean resetStyle) {
        super.setStyle(style,resetStyle);

        removeParentBackground();
        removeParentBorder();

        //#if polish.css.gauge-clock-instrument-needle-image
        String imageUrl = style.getProperty("gauge-clock-instrument-needle-image");
        if (imageUrl != null)
        {
            try
            {
                    this.needleImage = Image.createImage(imageUrl);
            }
            catch(IOException e)
            {
                    //#debug error
                    System.out.println("unable to load image " + e);
            }

            needleCenterX = needleImage.getWidth() / 2;
            needleCenterY = needleImage.getHeight() / 2;
        }
        //#endif

        //#if polish.css.gauge-clock-instrument-needle-shadow-image
        imageUrl = style.getProperty("gauge-clock-instrument-needle-shadow-image");
        if (imageUrl != null)
        {
            try
            {
                    this.needleShadowImage = Image.createImage(imageUrl);
            }
            catch(IOException e)
            {
                    //#debug error
                    System.out.println("unable to load image " + e);
            }

            shadowCenterX = needleShadowImage.getWidth() / 2;
            shadowCenterY = needleShadowImage.getHeight() / 2;
        }
        //#endif

        //#if polish.css.gauge-clock-instrument-background-image
        imageUrl = style.getProperty("gauge-clock-instrument-background-image");
        if (imageUrl != null)
        {
            try
            {
                    this.backgroundImage = Image.createImage(imageUrl);
            }
            catch(IOException e)
            {
                    //#debug error
                    System.out.println("unable to load image " + e);
            }

            needleX = backgroundImage.getWidth() / 2;
            needleY = backgroundImage.getHeight() / 2;
        }
        //#endif

        //#if polish.css.gauge-clock-instrument-needle-center-y
        Integer temp = style.getIntProperty("gauge-clock-instrument-needle-center-y");
        if ( temp != null )
        {
            needleCenterY = temp.intValue();
        }
        //#endif

        //#if polish.css.gauge-clock-instrument-needle-center-x
        temp = style.getIntProperty("gauge-clock-instrument-needle-center-x");
        if ( temp != null )
        {
            needleCenterX = temp.intValue();
        }
        //#endif

        //#if polish.css.gauge-clock-instrument-needle-shadow-center-y
        temp = style.getIntProperty("gauge-clock-instrument-needle-shadow-center-y");
        if ( temp != null )
        {
            shadowCenterY = temp.intValue();
        }
        //#endif

        //#if polish.css.gauge-clock-instrument-needle-shadow-center-x
        temp = style.getIntProperty("gauge-clock-instrument-needle-shadow-center-x");
        if ( temp != null )
        {
            shadowCenterX = temp.intValue();
        }
        //#endif

        //#if polish.css.gauge-clock-instrument-needle-shadow-offset-y
        temp = style.getIntProperty("gauge-clock-instrument-needle-shadow-offset-y");
        if ( temp != null )
        {
            shadowOffsetY = temp.intValue();
        }
        //#endif

        //#if polish.css.gauge-clock-instrument-needle-shadow-offset-x
        temp = style.getIntProperty("gauge-clock-instrument-needle-shadow-offset-x");
        if ( temp != null )
        {
            shadowOffsetX = temp.intValue();
        }
        //#endif

        //#if polish.css.gauge-clock-instrument-needle-x
        temp = style.getIntProperty("gauge-clock-instrument-needle-x");
        if ( temp != null )
        {
            needleX = temp.intValue();
        }
        //#endif

        //#if polish.css.gauge-clock-instrument-needle-y
        temp = style.getIntProperty("gauge-clock-instrument-needle-y");
        if ( temp != null )
        {
            needleY = temp.intValue();
        }        
        //#endif

        //#if polish.css.gauge-clock-instrument-start-angle
        temp = style.getIntProperty("gauge-clock-instrument-start-angle");
        if ( temp != null )
        {
           startAngle = temp.intValue();
        }
        //#endif

        //#if polish.css.gauge-clock-instrument-end-angle
        temp = style.getIntProperty("gauge-clock-instrument-end-angle");
        if ( temp != null )
        {
            endAngle = temp.intValue() ;
        }
        //#endif

        RgbFilter [] currentFilters = null ;

        //#if polish.css.needle-filter
        currentFilters = (RgbFilter[]) style.getObjectProperty("needle-filter");
        if (currentFilters != null) {
            if (currentFilters!= needleFilters)
            {
                needleFilters = new RgbFilter[ currentFilters.length ];
                for (int i = 0; i < currentFilters.length; i++)
                {
                        RgbFilter rgbFilter = currentFilters[i];
                        try
                        {
                                needleFilters[i] = (RgbFilter) rgbFilter.getClass().newInstance();
                                needleFilters[i].setStyle(style, resetStyle);
                        } catch (Exception e)
                        {
                                //#debug warn
                                System.out.println("Unable to initialize filter class " + rgbFilter.getClass().getName() + e );
                        }
                }
            }
        }
        //#endif

        //#if polish.css.face-filter
        currentFilters = (RgbFilter[]) style.getObjectProperty("face-filter");
        if (currentFilters != null) {
            if (currentFilters!= faceFilters)
            {
                faceFilters = new RgbFilter[ currentFilters.length ];
                for (int i = 0; i < currentFilters.length; i++)
                {
                        RgbFilter rgbFilter = currentFilters[i];
                        try
                        {
                                faceFilters[i] = (RgbFilter) rgbFilter.getClass().newInstance();
                                faceFilters[i].setStyle(style, resetStyle);
                        } catch (Exception e)
                        {
                                //#debug warn
                                System.out.println("Unable to initialize filter class " + rgbFilter.getClass().getName() + e );
                        }
                }
            }
        }
        //#endif

    }

    /**
     * (non-javadoc)
     * @see de.enough.polish.ui.ItemView#paintContent(de.enough.polish.ui.Item, int, int, int, int, javax.microedition.lcdui.Graphics) 
     */
    protected void paintContent(Item parent, int x, int y, int leftBorder, int rightBorder, Graphics g) {
      
        
        de.enough.polish.ui.Graphics graphics = new de.enough.polish.ui.Graphics(g);

        double degreesPerTick = ( 1.0 * ( endAngle - startAngle) ) / gauge.getMaxValue();
        int currentAngle = startAngle + (int) ( degreesPerTick * gauge.getValue() );
        int bgLeft = x + (contentWidth - backgroundImage.getWidth()) / 2;
        int bgTop = y + (contentHeight - backgroundImage.getHeight()) / 2;


        RgbImage rgbImage = null ;

        // Draw background
        //#if polish.css.face-filter
            rgbImage = new RgbImage(backgroundImage.getRgbData(),backgroundImage.getWidth());
            if ( faceFilters != null )
            {
                if ( faceFilters.length > 0 )
                {
                    for (int i=0; i<faceFilters.length; i++)
                    {
                            RgbFilter filter = faceFilters[i];
                            rgbImage = filter.process(rgbImage);
                    }
                }
            }
            graphics.drawRgb(rgbImage, bgLeft, bgTop);
        //#else
            graphics.drawImage(backgroundImage,bgLeft,bgTop, Graphics.TOP | Graphics.LEFT);
        //#endif


        // Draw the needle shadow
        if ( needleShadowImage != null )
        {
            graphics.drawRotatedImage( needleShadowImage,  shadowCenterX, shadowCenterY, bgLeft + needleX + shadowOffsetX, bgTop + needleY + shadowOffsetY , currentAngle );
        }

        // Draw the needle
        //#if polish.css.needle-filter
            DropShadowRgbFilter dropShadowFilter = null ;
            rgbImage = new RgbImage(needleImage.getRgbData(),needleImage.getWidth());
            if ( needleFilters != null )
            {
                if ( needleFilters.length > 0 )
                {
                    for (int i=0; i<needleFilters.length; i++)
                    {
                            RgbFilter filter = needleFilters[i];

                            // Treat the drop shadow filter specially.
                            if ( filter instanceof DropShadowRgbFilter )
                            {
                                dropShadowFilter = (DropShadowRgbFilter) filter;
                                continue;
                            }
                            rgbImage = filter.process(rgbImage);
                            
                    }
                }
            }

            // If there's no drop shadow filter used, draw the rotated image directly
            if ( dropShadowFilter == null )
            {
                graphics.drawRotatedImage( rgbImage,  needleCenterX, needleCenterY, bgLeft + needleX , bgTop + needleY, currentAngle );
            }
            else // If there is a drop shadow filter in use, we need to treat this as a special case
            {
                // Calculate the additional margin for the image because of the shadow
                int size = dropShadowFilter.getSize();
                int xOffset = dropShadowFilter.getXOffset();
                int yOffset = dropShadowFilter.getYOffset();
                int iLeft = size-xOffset<0 ? 0 : size-xOffset;
                int iTop = size-yOffset<0 ? 0 : size-yOffset;

                // Save the original image size
                int originalWidth = rgbImage.getWidth();
                int originalHeight = rgbImage.getHeight() ;

                // Rotate the image around its center
                int degrees = - ( currentAngle - 90 ); // We use the correct trigonometric sense, and we consider 0 degrees to be at 3 o'clock.
                ImageUtil.rotate(rgbImage, degrees);

                // Keep the rotated RGB image's size
                int rgbW = rgbImage.getWidth() ;
                int rgbH = rgbImage.getHeight();

                // Apply the drop shadow filter
                rgbImage = dropShadowFilter.process(rgbImage);

                /*for (int xx=0;xx<rgbImage.getWidth();xx++)
                for (int yy=0;yy<rgbImage.getHeight();yy++)
                {
                    if ( (xx==0) || ( yy==0) )
                    {
                        rgbImage.getRgbData()[yy*rgbImage.getWidth()+xx] = 0xAAFF00FF;
                    }
                }*/


                // Calculate the cos and sin of the rotation angle
                double degreeCos = Math.cos(Math.PI * degrees / 180);
                double degreeSin = Math.sin(Math.PI * degrees / 180);

                // Calculate the delta between the image center and the image rotation point
                int centerXDelta = needleCenterX - originalWidth/2;
                int centerYDelta = needleCenterY - originalHeight/2;

                // Calculate the coordinates of the rotation point after the image has been rotated,
                // with respect to the image's center
                int newCenterX =  + (int) (centerXDelta * degreeCos - centerYDelta * degreeSin );
                int newCenterY =  + (int) (centerXDelta * degreeSin + centerYDelta * degreeCos );

                // Calculate the coordinates of the top-left corner of the rotated image
                int posX = bgLeft + needleX - rgbW /2 ;
                int posY = bgTop + needleY - rgbH /2 ;

                // Apply the necessary offset so that the rotated image's rotation point
                // falls at the specfied point on the Graphics object and draw the rotated image
                // We need to also take into account the extra pixels added by the dropShadow filter,
                // hence the use of iLeft and iTop
                graphics.drawRgb(rgbImage, -iLeft + posX - newCenterX, -iTop + posY - newCenterY);
            }
        //#else
            graphics.drawRotatedImage( needleImage,  needleCenterX, needleCenterY, bgLeft + needleX , bgTop + needleY, currentAngle );
        //#endif
            
    }

    /**
     * Calculates the gauge value based on the pointer position
     * @param x
     * @param y
     */
    protected void valueBasedOnPointerPosition(int x, int y)
    {

        // Get the pointer coordinates relative to the content area
        Point p= adjustToContentArea(x, y);
        x = p.x;
        y = p.y ;

        // Next, get the pointer coordinates relative to the needle rotation center
        int bgLeft = (contentWidth - backgroundImage.getWidth()) / 2;
        int bgTop = (contentHeight - backgroundImage.getHeight()) / 2;
        int ndlX =  bgLeft + needleX ;
        int ndlY = bgTop + needleY ;        
        int dX = x - ndlX;
        int dY = ndlY - y;

        // Get the angle defined by the needle center and the pointer
        int angle = (int) ( ( MathUtil.atan2(dX, dY) * 180 ) / Math.PI );

        // Transform the start and end angles to normal trigonometric notation
        int processedStartAngle = startAngle ;
        int processedEndAngle = endAngle;

        // Store various information about the arc defined by the start and end angles
        int totalArcLength = Math.abs ( processedEndAngle - processedStartAngle );
        boolean initialArcFollowsTrigonometricDirection = ( startAngle <= endAngle);

        boolean anglesHaveDifferentSigns = ( ( processedStartAngle * processedEndAngle ) <= 0 ) ;

        // NOTE : Positive trigonometric direction : from quadrant I to quadrant IV
        // NOTE : Negative trigonometric direction : from quadrant IV to quadrant I

        // If, needed, swap the start and end angles so that the resulting arc always
        // follows the positive trigonometric direction.
        // That is, you will always move from the start angle towards the end angle
        // in the positive trigonometric direction.
        if ( ! initialArcFollowsTrigonometricDirection )
        {
            int temp = processedStartAngle;
            processedStartAngle = processedEndAngle;
            processedEndAngle = temp;
        }

        // Convert the angles to the interval 0-360
        processedStartAngle = ( 360 + processedStartAngle ) % 360 ;
        processedEndAngle = ( 360 + processedEndAngle ) % 360 ;

        // Various variables
        int newGaugeValue = 0;
        int arcType = 0;
        boolean isInsideArc = false ;

        // Check if the pointer is within the arc defined by (startAngle,endAngle);
        // For cases like -30, 30, that mix angles with different signs we check
        // if the needle is inside the arc that intersects the positive X-axis,
        // since moving in the positive trigonometric direction in this case means
        // moving from an angle less than 0 towards an angle greater than zero, with
        // the 0 angle somewhere in the middle.      
        if ( anglesHaveDifferentSigns )
        {
            if ( ( angle >= processedStartAngle) || ( angle <= processedEndAngle) )
            {
                isInsideArc = true;
                arcType = 1;
            }

            // NOTE : The check above (with || instead of &&) works because we converted all angles from the
            // (-360,360) interval used in the CSS to the [0,360) interval used by the math functions.
            // More specifically, we convert all negative angles to their corresponding positive values.
            // We can do this because the trigonometric circle "loops" around itself (see Trigonometry 101).
            // Thus, the arc (-30,30) will become (330,30), as -30 is outside the [0,360) interval
            // and, when converted to said interval, will become 330 degrees.
            // A pointer angle of -10 degress (which is clearly inside the (-30,30) arc) will be converted to 350 degrees.
            // A pointer angle of 20 degrees (which is also inside the arc) will remain unchanged.
            // So, the pointer angle is inside the arc if
            // pointerAngle >= startAngle (pointer angle is below zero, but above or equal to the starting angle --> -10 >= -30 becomes 350 >= 330 ) or if
            // pointerAngle <= endAngle (pointer angle is above or equal to zero, but below or equal to the end angle - 20 < 30 )
            // It's all a matter of reference frames and converting between them. :)
            
        }
        else // For cases like -60,-30 or 30,60, with angles that have the same sign, we simply check if Start < needle < End
        {
            if ( ( angle >= processedStartAngle) && ( angle <= processedEndAngle) )
            {
                isInsideArc = true;
                arcType = 2;
            }
        }

        // Calculate the new gauge value based on the pointer position, if needed.
        if ( isInsideArc )
        {
            int semiArcLength = 0;

            if ( arcType == 2 ) // The angles have the same sign.
            {
                if ( initialArcFollowsTrigonometricDirection )
                {
                    semiArcLength = Math.abs ( angle - processedStartAngle );
                }
                else
                {
                    semiArcLength = Math.abs ( angle - processedEndAngle );
                }
                newGaugeValue = ( gauge.getMaxValue() * semiArcLength ) / totalArcLength ;
            }
            else // The angles have different signs. 
            {               
                if ( angle >= processedStartAngle ) // If the needle is "below zero"
                {
                    if ( initialArcFollowsTrigonometricDirection )
                    {
                        semiArcLength = Math.abs ( angle - processedStartAngle);
                    }
                    else
                    {
                        semiArcLength = totalArcLength - Math.abs ( angle - processedStartAngle);
                    }
                }
                else // If the needle is "above zero"
                {
                    if ( initialArcFollowsTrigonometricDirection )
                    {
                        semiArcLength = totalArcLength - Math.abs ( angle - processedEndAngle);
                    }
                    else
                    {
                        semiArcLength = Math.abs ( angle - processedEndAngle);
                    }
                }
                newGaugeValue = ( gauge.getMaxValue() * semiArcLength ) / totalArcLength ;
            }
            
            gauge.setValue(newGaugeValue);
        }

        
    }

    /**
     * (non-javadoc)
     * @see de.enough.polish.ui.ItemView#handlePointerDragged(int, int, ClippingRegion) 
     */
    public boolean handlePointerDragged(int x, int y, ClippingRegion repaintRegion)
    {
        if ( this.gauge.isInteractive())
        {
            valueBasedOnPointerPosition(x, y);
            addFullRepaintRegion(this.gauge, repaintRegion);
            return true;
        }
        return false;
    }

    /**
     * (non-javadoc)
     * @see de.enough.polish.ui.ItemView#handlePointerPressed(int, int) 
     */
    public boolean handlePointerPressed(int x, int y)
    {
        if ( this.gauge.isInteractive())
        {
            valueBasedOnPointerPosition(x, y);
            return true;

        }
        return false;
    }

    /**
     * (non-javadoc)
     * @see de.enough.polish.ui.ItemView#handlePointerReleased(int, int) 
     */
    public boolean handlePointerReleased(int x, int y)
    {
        if ( gauge.isInteractive())
        {
            valueBasedOnPointerPosition(x, y);
            return true;
        }
        return false;
    }

}
