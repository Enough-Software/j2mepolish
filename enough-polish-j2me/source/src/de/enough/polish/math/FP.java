package de.enough.polish.math;

/**
 * Basic routines for fixed point number math.
 * <p>
 * Fixed point number is represented by int32 with 16 bit mantissa
 * and 16 bit exponent.
 * 
 * @author Anders Bo Pedersen, anders@wicore.dk
 */
public class FP 
{
	/**
	 * Fixed Point precision
	 */
	public final static int FIX_PRECISION = 16; //number of fractional bits
	
	/**
	 * Masks for mantissa, exponent.
	 */
	public final static int MANTISSA_MASK = 0x7fffffff << FIX_PRECISION;
	/**
	 * Exponent mask
	 */
	public final static int EXPONENT_MASK = 0x7fffffff >> FIX_PRECISION;
	
	/**
	 * Number constants
	 */
	public final static int FIX_ONE = (1 << FIX_PRECISION); //1.0
	public final static int FIX_HALF = 1 << 15;				//0.5
	public final static int FIX_QUATER = 1 << 14;			//0.25
	public final static int FIX_EIGHTS = 1 << 13;			//0.0125
	
	/**
	 * Infinity constants
	 */
	public final static int FIX_MINF = 0x80000000;//-inf
	public final static int FIX_PINF = 0x7fffffff;//+inf
	
	/**
	 * Epsilon constant, used for == zero checks.
	 */
	public final static int FIX_EPS = 1 << 2;
	
	// using constants so that this also works on CLDC 1.0 devices:
	/**
	 * Pi as fixed point integer: 3.14159265358979323846f
	 */
	public final static int FIX_PI = 		205887; //convertFloatToFix(3.14159265358979323846f);
	/**
	 * Pi/2 as fixed point integer: 3.14159265358979323846f/2.0f
	 */
	public final static int FIX_PI_HALF = 	102943; //convertFloatToFix(3.14159265358979323846f/2.0f);
	/**
	 * Pi*2 as fixed point integer: 6.28318530717958647692f
	 */
	public final static int FIX_2PI = 		411774; //convertFloatToFix(6.28318530717958647692f);
	/**
	 * 1/2*Pi as fixed point integer: 1.0f/6.28318530717958647692f
	 */	
	public final static int FIX_R2PI = 		 10430; //convertFloatToFix(1.0f/6.28318530717958647692f);

	/**
	 * Degree constants
	 */
	public final static int FIX_90 = 90 << FIX_PRECISION;

	/**
	 * Degree constants
	 */
	public final static int FIX_180 = 180 << FIX_PRECISION;

	/**
	 * Degree constants
	 */
	public final static int FIX_270 = 270 << FIX_PRECISION;

	/**
	 * Degree constants
	 */
	public final static int FIX_360 = 360 << FIX_PRECISION;

	/**
	 * Converts a integer number to fixed point number.
	 *
	 * @param anInt int to be converted
	 * @return int fix representation of input
	 */
	public static int intToFix(int anInt)
	{
		return anInt<<FIX_PRECISION;
	}

	/**
	 * Converts a fixed point number to integer number.
	 *
	 * @param a_fix fixed point number to be converted
	 * @return int integer representation of input
	 */
	public static int fixToInt(int a_fix)
	{
		return a_fix>>FIX_PRECISION;
	}

	//#if polish.hasFloatingPoint
	/**
	 * Converts a floating point number to fixed point number.
	 *
	 * @param aFloat floating point number to be converted
	 * @return int fixed point representation of input
	 */
	public static int floatToFix(float aFloat)
	{
		return (int)(65536.0f*aFloat);
	}
	//#endif

	//#if polish.hasFloatingPoint
	/**
	 * Converts a fixed point number to floating point number.
	 *
	 * @param a_fix fixed point number to be converted
	 * @return float floating point representation of input
	 */
	public static float fixToFloat(int a_fix )
	{
		return ( (float)(a_fix) ) * (1/65536.0f);
	}
	//#endif
	
	
	/**
	 * Multiplies two fixed point numbers.
	 *
	 * @param one_fix
	 * @param two_fix
	 * @return int product of fixed point multiplication
	 */
	public static int mul(int one_fix, int two_fix)
	{
		return (int)((((long)one_fix)*two_fix)>>FIX_PRECISION);
	}

	/**
	 * Divides two fixed point numbers after zero check.
	 *
	 * @param one_fix
	 * @param two_fix
	 * @return int product of fixed point division
	 */
	public static int div(int one_fix, int two_fix)
	{
		if(two_fix>Math.abs(FIX_EPS))
			return (int)((((long)one_fix)<<FIX_PRECISION)/two_fix);
		else
			return 0;
	}
	
	/**
	 * Absolute value of fixed point number.
	 *
	 * @param a_fix the fixed point number to find absolute value of
	 * @return absolute value of input
	 */
	public static int abs(int a_fix)
	{
		return (a_fix < 0) ? -a_fix : a_fix;
	}

	/**
	 * Rounds fixed point number to nearest whole integer number
	 *
	 * @param a_fix the fixed point number to round
	 * @return rounded fixed point number as int
	 */
	public static int round(int a_fix) 
	{
		return (a_fix + FIX_HALF) >> FIX_PRECISION;
	}

	/**
	 * Rounds fixed point number to nearest whole fix number
	 *
	 * @param a_fix the fixed point number to round
	 * @return rounded fixed point number
	 */
	public static int nearest(int a_fix) 
	{
		return truncate(a_fix + FIX_HALF);
	}

	/**
	 * Removes the fractional part of a fixed point number
	 *
	 * @param a_fix the fixed point number to truncate
	 * @return truncated fixed point number
	 */
	public static int truncate(int a_fix)
	{
		return a_fix&0xFFFF0000;
	}

	/**
	 * Returns the fractional part of a_fix
	 *
	 * @param a_fix the fixed point number to fetch fractional part from
	 * @return signed fractional part of input
	 */
	public static int fraction(int a_fix)
	{
		return a_fix&0x8000FFFF;
	}
	
	/**
	 * Clamps a fixed point number to [0.0,1.0] according to parameter range
	 *
	 * @param a_fix the fixed point number to be clamped
	 * @param clamp_fix the range to clamps to number to
	 * @return int clamped fixed point number
	 */
	public static int clamp(int a_fix, int clamp_fix)
	{
		return div(a_fix,clamp_fix);
	}

	/**
	 * Returns the smallest of two fixed point numbers
	 *
	 * @param one_fix first fixed point number
	 * @param two_fix second fixed point number
	 * @return smallest fixed point number
	 */
	public static int min(int one_fix, int two_fix) 
	{
		return one_fix < two_fix ? one_fix : two_fix;
	}

	/**
	 * Returns the largest of two fixed point numbers
	 *
	 * @param one_fix first fixed point number
	 * @param two_fix second fixed point number
	 * @return largest fixed point number
	 */
	public static int max(int one_fix, int two_fix)
	{
		return one_fix > two_fix ? one_fix : two_fix;
	}
	
	/**
	 * Outputs fixed point number
	 *
	 * @param a_fix the fixed point number to printed
	 * @return String
	 */
	public static String toString(int a_fix )
	{
		return ((a_fix&MANTISSA_MASK)>>FIX_PRECISION)+"."+div(EXPONENT_MASK, (a_fix&EXPONENT_MASK));
	}

	/**
	 * Square root lookup table
	 */
	private static final short[] sqrt_table = { 0, 16, 22, 27, 32, 35, 39, 42,
		45, 48, 50, 53, 55, 57, 59, 61, 64, 65, 67, 69, 71, 73, 75, 76, 78,
		80, 81, 83, 84, 86, 87, 89, 90, 91, 93, 94, 96, 97, 98, 99, 101,
		102, 103, 104, 106, 107, 108, 109, 110, 112, 113, 114, 115, 116,
		117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 128, 128, 129,
		130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142,
		143, 144, 144, 145, 146, 147, 148, 149, 150, 150, 151, 152, 153,
		154, 155, 155, 156, 157, 158, 159, 160, 160, 161, 162, 163, 163,
		164, 165, 166, 167, 167, 168, 169, 170, 170, 171, 172, 173, 173,
		174, 175, 176, 176, 177, 178, 178, 179, 180, 181, 181, 182, 183,
		183, 184, 185, 185, 186, 187, 187, 188, 189, 189, 190, 191, 192,
		192, 193, 193, 194, 195, 195, 196, 197, 197, 198, 199, 199, 200,
		201, 201, 202, 203, 203, 204, 204, 205, 206, 206, 207, 208, 208,
		209, 209, 210, 211, 211, 212, 212, 213, 214, 214, 215, 215, 216,
		217, 217, 218, 218, 219, 219, 220, 221, 221, 222, 222, 223, 224,
		224, 225, 225, 226, 226, 227, 227, 228, 229, 229, 230, 230, 231,
		231, 232, 232, 233, 234, 234, 235, 235, 236, 236, 237, 237, 238,
		238, 239, 240, 240, 241, 241, 242, 242, 243, 243, 244, 244, 245,
		245, 246, 246, 247, 247, 248, 248, 249, 249, 250, 250, 251, 251,
		252, 252, 253, 253, 254, 254, 255 };

	/**
	 * Square root by lookup table.
	 * <p>
	 * Source: 	http://atoms.alife.co.uk/sqrt/SquareRoot.java
	 *
	 * @param x the number to find sqrt of
	 * @return rounded square root of input number
	 */
	private static int fastSqrt(int x) 
	{
		if (x >= 0x10000) {
			if (x >= 0x1000000) {
				if (x >= 0x10000000) {
					if (x >= 0x40000000) {
						return (sqrt_table[x >> 24] << 8);
					} else {
						return (sqrt_table[x >> 22] << 7);
					}
				} else if (x >= 0x4000000) {
					return (sqrt_table[x >> 20] << 6);
				} else {
					return (sqrt_table[x >> 18] << 5);
				}
			} else if (x >= 0x100000) {
				if (x >= 0x400000) {
					return (sqrt_table[x >> 16] << 4);
				} else {
					return (sqrt_table[x >> 14] << 3);
				}
			} else if (x >= 0x40000) {
				return (sqrt_table[x >> 12] << 2);
			} else {
				return (sqrt_table[x >> 10] << 1);
			}
		} else if (x >= 0x100) {
			if (x >= 0x1000) {
				if (x >= 0x4000) {
					return (sqrt_table[x >> 8]);
				} else {
					return (sqrt_table[x >> 6] >> 1);
				}
			} else if (x >= 0x400) {
				return (sqrt_table[x >> 4] >> 2);
			} else {
				return (sqrt_table[x >> 2] >> 3);
			}
		} else if (x >= 0) {
			return sqrt_table[x] >> 4;
		}
		return -1;
	}

	/**
	 * Find rounded Square root by lookup table.
	 * <p>
	 * NOTE: This function is faster than Sqrt()
	 *
	 * @param x_fix fixed point number to find sqrt of
	 * @return int rounded square root of input number
	 */
	public static int sqrtRound(int x_fix)
	{
		return fastSqrt(x_fix >> 16)<<16;
	}

	/**
	 * Find sqrt of fixed point number using Newton's Iteration
	 * <p>
	 * Source: http://mathworld.wolfram.com/NewtonsIteration.html
	 *<p>
	 * Recurrence equation:
	 * x(k+1) = 0.5*( x(k) * ( n / x(k) ) )
	 * <p>
	 * Currently set to 8 iterations to converge with 2 decimal precision
	 * 
	 * @param a_fix the fixed point number to find sqrt of
	 * @return square root of input number
	 */
	public static int sqrt(int a_fix)
	{
		int s_fixed = (a_fix + FIX_ONE) >> 1;

		for (int i = 8; --i>=0; )
			s_fixed = (s_fixed + div(a_fix, s_fixed)) >> 1;

			return s_fixed;
	}

	/**
	 * Converts a degree values to radians
	 *
	 * @param degree_fix degreee value
	 * @return radian value
	 */
	public static int degreeToRadian(int degree_fix)
	{
		return mul(degree_fix, div( FIX_PI, FIX_180) );
	}

	/**
	 * Converts a radian values to degrees
	 *
	 * @param radian_fix radian value
	 * @return degree value
	 */
	public static int radianToDegree(int radian_fix)
	{
		return mul(radian_fix, div( FIX_180 , FIX_PI) );
	}
	
	/**
	 * Constants for sine funciton
	 */
	private final static int sk_precision = 31;
	private final static int[] SK =
	{
			16342350 >> (sk_precision-FIX_PRECISION),       //7.61e-03 * 2^31
			356589659 >> (sk_precision-FIX_PRECISION)      //1.6605e-01
	};

	/**
	 * Finds the Sin value of input fixed point number.
	 * <p>
	 * Source: oMathFP v.1.08, http://orbisstudios.com/?link=downloads
	 *
	 * @param radians_fix the angle in radians
	 * @return sine of the angle
	 */
	public static int sin(int radians_fix) 
	{
		int sign = 1;
		radians_fix %= (FIX_PI<<1);
		if(radians_fix < 0)
			radians_fix = (FIX_PI<<1) + radians_fix;
		if ((radians_fix > FIX_PI_HALF) && (radians_fix <= FIX_PI)) {
			radians_fix = FIX_PI - radians_fix;
		} else if ((radians_fix > FIX_PI) && (radians_fix <= (FIX_PI + FIX_PI_HALF))) {
			radians_fix = radians_fix - FIX_PI;
			sign = -1;
		} else if (radians_fix > (FIX_PI + FIX_PI_HALF)) {
			radians_fix = (FIX_PI<<1)-radians_fix;
			sign = -1;
		}

		int sqr = mul(radians_fix,radians_fix);
		int result = SK[0];
		result = mul(result, sqr);
		result -= SK[1];
		result = mul(result, sqr);
		result += FIX_ONE;
		result = mul(result, radians_fix);
		return sign * result;
	}

	/**
	 * Finds the Cos value of input fixed point number.
	 * <p>
	 * Source: oMathFP v.1.08, http://orbisstudios.com/?link=downloads
	 *
	 * @param radians_fix the angle in radians
	 * @return cosine of the angle
	 */
	public static int cos(int radians_fix) {
		return sin(FIX_PI_HALF - radians_fix);
	}

	/**
	 * Finds the Tan value of input fixed point number.
	 *<p>
	 * Source: oMathFP v.1.08, http://orbisstudios.com/?link=downloads
	 *
	 * @param radians_fix the angle in radians
	 * @return Tan() of the angle
	 */
	public static int tan(int radians_fix) {
		return div(sin(radians_fix), cos(radians_fix));
	}

	/**
	 * Constants for sine function
	 */
	private final static int as_precision = 30;
	private final static int[] AS = {
			-20110432 >> (as_precision-FIX_PRECISION),      //-0.0187293 * 2^30
			79737141 >> (as_precision-FIX_PRECISION),       //0.0742610
			227756102 >> (as_precision-FIX_PRECISION),      //0.2121144
			1686557206 >> (as_precision-FIX_PRECISION)      //1.5707288
	};

	/**
	 * Finds the inverse Sin value of input fixed point number.
	 *<p>
	 * NOTE: Input range: [-1, 1] -- output range: [-PI/2, PI/2]
	 * <p>
	 * Source: oMathFP v.1.08, http://orbisstudios.com/?link=downloads
	 * <p>
	 * NOTE: THIS FUNCTION IS FAULTY!!! CONVERTING VALUES BETWEEN SIN(angle)
	 * AND ASIN(angle) PRODUCES PRECISION ERRORS OF .1 RADIANS
	 *
	 * @param a_fix the number to find inverse sin of in range [-1, 1]
	 * @return ASin() of input
	 */
	public static int asin(int a_fix) 
	{
		
		boolean neg = false;
		
		if (a_fix < 0) {
			neg = true;
			a_fix = -a_fix;
		}

		int fRoot = sqrt(FIX_ONE-a_fix);
		int result = AS[0];

		result = mul(result, a_fix);
		result += AS[1];
		result = mul(result, a_fix);
		result -= AS[2];
		result = mul(result, a_fix);
		result += AS[3];
		result = FIX_PI_HALF - (mul(fRoot,result));
		
		if(neg) {
			result = -result;
		}
		return result;
	}

	/**
	 * Finds the inverse Cos value of input fixed point number.
	 *<p>
	 * NOTE: Input range: [-1, 1] -- output range: [-PI/2, PI/2]
	 * <p>
	 * Source: oMathFP v.1.08, http://orbisstudios.com/?link=downloads
	 *
	 * @param a_fix the number to find inverse cos of in range [-1, 1]
	 * @return ACos() of input
	 */
	public static int acos(int a_fix)
	{
		return FIX_PI_HALF - asin(a_fix);
	}

	/**
	 * Finds the inverse Tan value of input fixed point number.
	 *<p>
	 * NOTE: Input range: [-1, 1] -- output range: [-PI/2, PI/2]
	 * <p>
	 * Source: oMathFP v.1.08, http://orbisstudios.com/?link=downloads
	 *
	 * @param f the number to find inverse tan of in range [-1, 1]
	 * @return ATan() of input
	 */
	public static int atan(int f) 
	{
		return asin(div(f, sqrt(FIX_ONE + mul(f, f))));
	}
}
