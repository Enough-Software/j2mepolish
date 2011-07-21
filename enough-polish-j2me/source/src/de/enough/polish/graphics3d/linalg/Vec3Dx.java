package de.enough.polish.graphics3d.linalg;

import de.enough.polish.math.FP;

/**
 * A 3 dimensional Vector implementation. Class using fixed point precision.
 * 
 * @author Anders Bo Pedersen, anders@wicore.dk
 */
public class Vec3Dx 
{
	public volatile int xx, yx, zx;
	
	public Vec3Dx() 
	{
	}
	
	public Vec3Dx(int xx, int yx, int zx)
	{
		this.xx = xx;
		this.yx = yx;
		this.zx = zx;
	}
	
	public Vec3Dx(Vec3Dx other)
	{
		this.xx = other.xx;
		this.yx = other.yx;
		this.zx = other.zx;
	}
	
	public void add(Vec3Dx other)
	{
		this.xx += other.xx;
		this.yx += other.yx;
		this.zx += other.zx;
	}
	
	public void sub(Vec3Dx other)
	{
		this.xx -= other.xx;
		this.yx -= other.yx;
		this.zx -= other.zx;		
	}
	
	public void invert()
	{
		this.xx = -this.xx;
		this.yx = -this.yx;
		this.zx = -this.zx;
	}

	public void scale(int factorx)
	{
		this.xx = FP.mul(this.xx, factorx);
		this.yx = FP.mul(this.yx, factorx);
		this.zx = FP.mul(this.zx, factorx);
	}
	
	public Vec3Dx scaleNew(int factorx)
	{
		return new Vec3Dx(
				FP.mul(this.xx, factorx),
				FP.mul(this.yx, factorx),
				FP.mul(this.zx, factorx)
				);
	}

	public int dot(Vec3Dx other)
	{
		return 
		FP.mul(this.xx, other.xx) +
		FP.mul(this.yx, other.yx) +
		FP.mul(this.zx, other.zx);
	}
	
	public Vec3Dx cross(Vec3Dx other)
	{
		return new Vec3Dx(
				FP.mul(this.yx, other.zx) - FP.mul(this.zx, other.yx),
				-FP.mul(this.xx, other.zx) + FP.mul(this.zx, other.xx),
				FP.mul(this.xx, other.yx) - FP.mul(this.yx, other.xx)
				);
	}
	
	public boolean equals(Vec3Dx other)
	{
		return 	
			this.xx == other.xx &&
			this.yx == other.yx &&
			this.zx == other.zx;
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
