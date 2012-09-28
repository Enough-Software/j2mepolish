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
     *  On top of that the resulting value equals sin()*1000 to avoid doubling
     *  point errors.
     *  Keep also in mind that this approximation is not necessary
     *  monotonically increasing (especially around apxSin=1000).
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
    	double SQRT3 = 1.732050807568877294;
    	boolean signChange=false;
        boolean Invert=false;
        int sp=0;
        double x2, a;
        // check up the sign change
        if(x<0.)
        {
            x=-x;
            signChange=true;
        }
        // check up the invertation
        if(x>1.)
        {
            x=1/x;
            Invert=true;
        }
        // process shrinking the domain until x<PI/12
        while(x>Math.PI/12)
        {
            sp++;
            a=x+SQRT3;
            a=1/a;
            x=x*SQRT3;
            x=x-1;
            x=x*a;
        }
        // calculation core
        x2=x*x;
        a=x2+1.4087812;
        a=0.55913709/a;
        a=a+0.60310579;
        a=a-(x2*0.05160454);
        a=a*x;
        // process until sp=0
        while(sp>0)
        {
            a=a+Math.PI/6;
            sp--;
        }
        // inversation took place
        if(Invert) a=Math.PI/2-a;
        // sign change took place
        if(signChange) a=-a;
        //
        return a;
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
    
    //#if polish.hasFloatingPoint
    /**
     * Calculates e^x
     * @param x x
     * @return e^x
     */
    public static double exp(double x) {
    	double sum  = 0.0;
        double term = 1.0;
        for (int i = 1; sum != sum + term; i++) {
            sum  = sum + term;
            term = term * x / i;
        }
        return sum;
    }
    //#endif
    
  //#if polish.hasFloatingPoint
    /**
     * Calculates e^x, faster but less accurate than {@link MathUtil#exp(double)}
     * @param x x
     * @return e^x
     */
    public static double fastexp(double x) {
    	return (362880+x*(362880+x*(181440+x*(60480+x*(15120+x*(3024+x*(504+x*(72+x*(9+x)))))))))*2.75573192e-6;
    }
    //#endif
    
    //#if polish.hasFloatingPoint
    /**
     * Calculates a^b
     * @param a a
     * @param b b
     * @return a^b
     */
    public static double pow(double a, double b) { 
    	if ( b == 0 ) {
    		return 1;
    	}
		boolean gt1 = (Math.sqrt((a-1)*(a-1)) <= 1)? false:true; 
		int oc = -1,iter = 30;
		double p = a, x, x2, sumX, sumY;
		
		if( (b-Math.floor(b)) == 0 )
		{
			for( int i = 1; i < b; i++ )p *= a;
			return p;
		}
		
		x = (gt1)?(a /(a-1)):(a-1);
		sumX = (gt1)?(1/x):x;
		
		for( int i = 2; i < iter; i++ )
		{
			p = x;
			for( int j = 1; j < i; j++)p *= x;
			
			double xTemp = (gt1)?(1/(i*p)):(p/i);
			
			sumX = (gt1)?(sumX+xTemp):(sumX+(xTemp*oc));
					
			oc *= -1;
		}
		
		x2 = b * sumX;
		sumY = 1+x2;
				
		for( int i = 2; i <= iter; i++ )
		{
			p = x2;
			for( int j = 1; j < i; j++)p *= x2;
			
			int yTemp = 2;
			for( int j = i; j > 2; j-- )yTemp *= j;
			
			sumY += p/yTemp;
		}
		
		return sumY;
    }
    //#endif
    
    //#if polish.hasFloatingPoint
    /**
     * Calculates the natural logarithm of a number
     * @param x the number
     * @return the natural logarithm of x
     */
    public static double log(double x) {
    	long l = Double.doubleToLongBits(x);
    	long exp = ((0x7ff0000000000000L & l) >> 52) - 1023;
    	double man = (0x000fffffffffffffL & l) / (double)0x10000000000000L + 1.0;
    	double lnm = 0.0;
    	double a = (man - 1) / (man + 1);
    	for( int n = 1; n < 8; n += 2) {
    		lnm += pow(a, n) / n;
    	}
    	return 2 * lnm + exp * 0.69314718055994530941723212145818;
    }
    //#endif
    
    //#if polish.hasFloatingPoint
    /**
     * Calculates the logarithm of a number, in a given base
     * @param x the number
     * @param base the base
     * @return the logarithm of the number, in the given base
     */
    public static double log(double x, double base) {
        return log(x) / log(base);
    }
    //#endif
    
}
