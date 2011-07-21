/*
 * Created on 02-May-2006 at 17:31:01.
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

/**
 * <p>Provides some helper methods missing in java.lang.Math</p>
 *
 * <p>Copyright Enough Software 2006 - 2009</p>
 * <pre>
 * history
 *        02-May-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public final class MathUtil
{
    /**
     * Disallows instantiation
     */
    private MathUtil()
    {
    }

    //#if polish.hasFloatingPoint
    /**
     * Rounds the given double value
     *
     * @param value the value
     * @return the rounded value, x.5 and higher is rounded to x + 1.
     * @since CLDC 1.1
     */
    public static long round(double value)
    {
        if (value < 0) {
            return (long) (value - 0.5);
        }
        else {
            return (long) (value + 0.5);
        }
    }
    //#endif

    /**
     * This function returns an approximated value of sin using the taylor
     * 	approximation of power 5. Please keep in mind that the input angle
     * 	is NOT measured in degree or radian. It is measured in 1000, which
     *  equals 2 * PI. Therefore you have to convert degree via 'd*1000/360'
     *  and radian via 'r*1000/2/PI'.
     *  On top of that the resulting value equals sin()*1000 to avoid doubleing
     *  point errors.
     *  Keep also in mind that this aproximation is not necessary
     *  monotonically increasing (especially arround apxSin=1000).
     *
     * @param x1k this is the angle 360 degree correspond to 1000.
     * @return sin()*1000
     */
    public static int apxSin(int x1k)
    {
        int p = 1;
        x1k = x1k % 1000;

        if (x1k < 0) {
            x1k += 1000;
        }

        if (x1k > 250) {
            if (x1k < 500) {
                x1k = 500 - x1k;
            }
            else {
                if (x1k < 750) {
                    p = -1;
                    x1k = x1k - 500;
                }
                else {
                    p = -1;
                    x1k = 1000 - x1k;
                }
            }
        }

        x1k = (x1k * 3141 * 2) / 1000;

        int sq = x1k * x1k / 1000;
        long ret = x1k * 1000000 + x1k * (-sq / 6 * 1000 + sq * sq / 120);

        return (int) (ret / 1005000) * p;
    }

    /**
     * see apxSin()
     */
    public static int apxCos(int x1k)
    {
        return apxSin(x1k + 250);
    }

    //#if polish.hasFloatingPoint
    /**
     * Approximates the atan function. Uses a polynomial approximation that should
     * be accurate enough for most practical purposes.
     * 
     * @param x
     * @return the calculated value
     */
    public static double atan(double x)
    {
        double res;

        if (Math.abs(x) < 1) {
            res= x/(1 + 0.28 * x*x);
        }
        else {
            // NOTE : Due to some weird JVM behavior,
            // if x is negative it's more accurate to calculate
            // atan for -x and then negate it, rather than calculate atan
            // for x directly.
            if ( x < 0.0) {
                x = -x;
                res = ( Math.PI/2 - x/(x*x + 0.28) );
                res = -res ;
            }
            else {
                res = ( Math.PI/2 - x/(x*x + 0.28) );
            }            
        }

        return res;
    }
    //#endif

    //#if polish.hasFloatingPoint
    /**
     * Approximates the atan2 function. Results are in the [0,2*PI) range.
     * 
     * @param x
     * @param y
     * @return the calculated value
     */
    public static double atan2(double x, double y)
    {
        // Origin - return zero
        if (y == 0.0 && x == 0.0) {
            return 0.0;
        }
        else if (x > 0.0) {
            if (y > 0.0) { // Point is in first quadrant
                return atan(y / x);
            }
            else { // Point is in fourth quadrant
                return 2*Math.PI - atan(-y / x);
            }
        }
        else if (x < 0.0) {
            if (y < 0.0) { // Point is in third quadrant
                return Math.PI + atan(y / x);
            }
            else { // Point is in second quadrant
                return Math.PI - atan(-y / x);
            }
        }
        else if (y < 0.0) { // Special cases for when the point is directly on the Y axis.
            return 2 * Math.PI - Math.PI / 2.;
        }
        else {
            return Math.PI / 2.;
        }
    }
    //#endif
}
