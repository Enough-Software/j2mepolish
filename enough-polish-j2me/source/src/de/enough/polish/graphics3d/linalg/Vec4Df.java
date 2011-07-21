//#condition polish.hasFloatingPoint == true

package de.enough.polish.graphics3d.linalg;

/**
 * A 4 dimensional Vector implementation. Class using floating point precision.
 * 
 * @author Anders Bo Pedersen, anders@wicore.dk
 */
public class Vec4Df
{
	public volatile float xf, yf, zf, wf;
	
	public Vec4Df() 
	{
	}
	
	public Vec4Df(float xf, float yf, float zf, float wf)
	{
		this.xf = xf;
		this.yf = yf;
		this.zf = zf;
		this.wf = wf;
	}
	
	public Vec4Df(Vec4Df other)
	{
		this.xf = other.xf;
		this.yf = other.yf;
		this.zf = other.zf;
		this.wf = other.wf;
	}
	
	public void add(Vec4Df other)
	{
		this.xf += other.xf;
		this.yf += other.yf;
		this.zf += other.zf;
		this.wf += other.wf;
	}
	
	public void sub(Vec4Df other)
	{
		this.xf -= other.xf;
		this.yf -= other.yf;
		this.zf -= other.zf;
		this.wf -= other.wf;
	}
	
	public void invert()
	{
		this.xf = -this.xf;
		this.yf = -this.yf;
		this.zf = -this.zf;
		this.wf = -this.wf;
	}

	public void scale(float factorx)
	{
		this.xf = this.xf * factorx;
		this.yf = this.yf * factorx;
		this.zf = this.zf * factorx;
		this.wf = this.wf * factorx;
	}
	
	public Vec4Df scaleNew(float factorx)
	{
		return new Vec4Df(
				this.xf * factorx,
				this.yf * factorx,
				this.zf * factorx,
				this.wf * factorx
				);
	}

	public float dot(Vec4Df other)
	{
		return 
			(this.xf * other.xf) +
			(this.yf * other.yf) +
			(this.zf * other.zf) +
			(this.wf * other.wf);
	}
	
	public boolean equals(Vec4Df other)
	{
		return 	
			this.xf == other.xf &&
			this.yf == other.yf &&
			this.zf == other.zf &&
			this.wf == other.wf;
	}
	
	/**
	 * Euclidean length of vector
	 * 
	 * @return the length
	 */
	public float length()
	{
		return (float)Math.sqrt(this.dot(this));
	}
	
	/**
	 * Normalize the vector to unit length
	 */
	public void normalize()
	{
		//*this *= EGL_InvSqrt(LengthSq()); 
		
		float mag = this.length();
		
		this.xf = this.xf / mag;
		this.yf = this.yf / mag;
		this.zf = this.zf / mag;
		this.wf = this.wf / mag;
	}
	
	/**
	 * Projects 4D vector
	 */
	public Vec3Df project()
	{
		return new Vec3Df(xf, yf, zf);
	}
	
	//#mdebug debug
	
	public String propertiesToString()
	{
		String newline = "\n";
		
		String output = 
			"x: "+(this.xf)+newline+
			"y: "+(this.yf)+newline+
			"z: "+(this.zf);
		
		return output;
	}
	
	//#enddebug
}
