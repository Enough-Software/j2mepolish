package de.enough.polish.graphics3d.linalg;

import de.enough.polish.math.FP;

/**
 * A 4 dimensional Vector implementation. Class using fixed point precision.
 * 
 * @author Anders Bo Pedersen, anders@wicore.dk
 */
public class Vec4Dx
{
	public volatile int xx, yx, zx, wx;
	
	public Vec4Dx() 
	{
	}
	
	public Vec4Dx(int xx, int yx, int zx, int wx)
	{
		this.xx = xx;
		this.yx = yx;
		this.zx = zx;
		this.wx = wx;
	}
	
	public Vec4Dx(Vec4Dx other)
	{
		this.xx = other.xx;
		this.yx = other.yx;
		this.zx = other.zx;
		this.wx = other.wx;
	}
	
	public void add(Vec4Dx other)
	{
		this.xx += other.xx;
		this.yx += other.yx;
		this.zx += other.zx;
		this.wx += other.wx;
	}
	
	public void sub(Vec4Dx other)
	{
		this.xx -= other.xx;
		this.yx -= other.yx;
		this.zx -= other.zx;
		this.wx -= other.wx;
	}
	
	public void invert()
	{
		this.xx = -this.xx;
		this.yx = -this.yx;
		this.zx = -this.zx;
		this.wx = -this.wx;
	}

	public void scale(int factorx)
	{
		this.xx = FP.mul(this.xx, factorx);
		this.yx = FP.mul(this.yx, factorx);
		this.zx = FP.mul(this.zx, factorx);
		this.wx = FP.mul(this.wx, factorx);
	}
	
	public Vec4Dx scaleNew(int factorx)
	{
		return new Vec4Dx(
				FP.mul(this.xx, factorx),
				FP.mul(this.yx, factorx),
				FP.mul(this.zx, factorx),
				FP.mul(this.wx, factorx)
				);
	}

	public int dot(Vec4Dx other)
	{
		return 
		FP.mul(this.xx, other.xx) +
		FP.mul(this.yx, other.yx) +
		FP.mul(this.zx, other.zx) +
		FP.mul(this.wx, other.wx);
	}
	
	public boolean equals(Vec4Dx other)
	{
		return 	
			this.xx == other.xx &&
			this.yx == other.yx &&
			this.zx == other.zx &&
			this.wx == other.wx;
	}
	
	/**
	 * Euclidean length of vector
	 * 
	 * @return the length
	 */
	public int length()
	{
		return FP.sqrt(this.dot(this));
	}
	
	/**
	 * Normalize the vector to unit length
	 */
	public void normalize()
	{
		//*this *= EGL_InvSqrt(LengthSq()); 
		
		int mag = this.length();
		
		this.xx = FP.div(this.xx, mag);
		this.yx = FP.div(this.yx, mag);
		this.zx = FP.div(this.zx, mag);
		this.wx = FP.div(this.wx, mag);
	}
	
	/**
	 * Projects 4D vector
	 */
	public Vec3Dx project()
	{
		return new Vec3Dx(xx, yx, zx);
	}
	
	//#mdebug debug
	
	public String propertiesToString()
	{
		String newline = "\n";
		
		String output = 
			"x: "+FP.fixToFloat(this.xx)+newline+
			"y: "+FP.fixToFloat(this.yx)+newline+
			"z: "+FP.fixToFloat(this.zx);
		
		return output;
	}
	
	//#enddebug
}
