//#condition polish.hasFloatingPoint == true

package de.enough.polish.graphics3d.linalg;

/**
 * A 3 dimensional Vector implementation. Class using floating point precision.
 * 
 * @author Anders Bo Pedersen, anders@wicore.dk
 */
public class Vec3Df 
{
	public volatile float xf, yf, zf;
	
	public Vec3Df() 
	{
	}
	
	public Vec3Df(float xf, float yf, float zf)
	{
		this.xf = xf;
		this.yf = yf;
		this.zf = zf;
	}
	
	public Vec3Df(Vec3Df other)
	{
		this.xf = other.xf;
		this.yf = other.yf;
		this.zf = other.zf;
	}
	
	public void add(Vec3Df other)
	{
		this.xf += other.xf;
		this.yf += other.yf;
		this.zf += other.zf;
	}
	
	public void sub(Vec3Df other)
	{
		this.xf -= other.xf;
		this.yf -= other.yf;
		this.zf -= other.zf;		
	}
	
	public void invert()
	{
		this.xf = -this.xf;
		this.yf = -this.yf;
		this.zf = -this.zf;
	}

	public void scale(float factorf)
	{
		this.xf = this.xf * factorf;
		this.yf = this.yf * factorf;
		this.zf = this.zf * factorf;
	}
	
	public Vec3Df scaleNew(float factorf)
	{
		return new Vec3Df(
				this.xf * factorf,
				this.yf * factorf,
				this.zf * factorf
				);
	}

	public float dot(Vec3Df other)
	{
		return 
			this.xf * other.xf +
			this.yf * other.yf +
			this.zf * other.zf;
	}
	
	public Vec3Df cross(Vec3Df other)
	{
		return new Vec3Df(
				 (this.yf * other.zf) - (this.zf * other.yf),
				-(this.xf * other.zf) + (this.zf * other.xf),
				 (this.xf * other.yf) - (this.yf * other.xf)
				);
	}
	
	public boolean equals(Vec3Df other)
	{
		return 	
			this.xf == other.xf &&
			this.yf == other.yf &&
			this.zf == other.zf;
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
