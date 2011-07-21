//#condition polish.hasdoubleingPoint
package de.enough.polish.graphics3d.m3g.utils;

/**
 * Quaternion implementation.
 * <p>
 * Useful for Transform.postRotateQuat() transforms 
 *
 * @author Anders Bo Pedersen, anders@wicore.dk
 */
public class Quaternion 
{
	public volatile float x, y, z, w;

	public Quaternion()
	{
		//set to identity by default
		set(0, 0, 0, 1);
	}

	public Quaternion(float x, float y, float z, float w) 
	{
		set(x, y, z, w);
	}

	public void setIdentity()
	{
		set(0, 0, 0, 1);
	}

	/**
	 * Sets the values of quaternion by converting the given axis-angle values.
	 *
	 * @param angle of rotation in degrees
	 * @param x component of rotation axis
	 * @param y component of rotation axis
	 * @param z component of rotation axis
	 */
	public void setAxisAngle(float angle, float x, float y, float z)
	{
		double length = this.length();

		if (length == 0)
		{
			//Axis of rotation is zero vector, set to identity.
			setIdentity();
			return;
		}
		
		//Convert to radians and divide by 2
		angle *= Math.PI / 360;

		double cangle = Math.cos(angle);
		double sangle = Math.sin(angle);
		double scale = sangle / length; //normalize axis

		set(
			(float) (scale * x),        
			(float) (scale * y),         
			(float) (scale * z),         
			(float) cangle
		);
	}

	/**
	 * Sets the values of quaternion from Euler angles
	 *
	 * @param rx angle of rotation around x-axis in degrees
	 * @param ry angle of rotation around y-axis in degrees
	 * @param rz angle of rotation around z-axis in degrees
	 */
	public void setFromEulerAngles(float rx, float ry, float rz) 
	{
		//Convert to radians and divide by 2
		rx *= Math.PI / 360;
		ry *= Math.PI / 360;
		rz *= Math.PI / 360;

		double sx = Math.sin(rx);
		double sy = Math.sin(ry);
		double sz = Math.sin(rz);
		
		double cx = Math.cos(rx);
		double cy = Math.cos(ry);
		double cz = Math.cos(rz);

		double cxsy = cx * sy;
		double cxcy = cx * cy;
		double sxsy = sx * sy;

		set(
			(float) (cxsy*sz + cz*cy*sx),
			(float) (cxsy*cz - sx*cy*sz),
			(float) (cxcy*sz + sxsy*cz), 
			(float) (cxcy*cz - sxsy*sz)	
		);    
	}

	/**
	 * Multiply quaternions
	 * <p>
	 * self * other
	 *
	 * @param other right hand side quaternion
	 * @return his quaternion
	 */
	public Quaternion postMultiply(Quaternion other) 
	{
		set(
			this.w*other.x + this.x*other.w + this.y*other.z - this.z*other.y,
			this.w*other.y + this.y*other.w + this.z*other.x - this.x*other.z,		
			this.w*other.z + this.z*other.w + this.x*other.y - this.y*other.x,		
			this.w*other.w - this.x*other.x - this.y*other.y - this.z*other.z
		);
		
		return this;
	}

	public void normalize() 
	{
		float squareFactor = this.x*this.x + this.y*this.y + this.z*this.z + this.w*this.w;
		float normFactor = (float) (1.0/Math.sqrt(squareFactor));
		
		set(this.x * normFactor, this.y * normFactor, this.z * normFactor, this.w * normFactor);
	}
	
	public double length()
	{
		return Math.sqrt(this.x*this.x + this.y*this.y + this.z*this.z);
	}

	public void set(float x, float y, float z, float w)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public float getX()
	{
		return this.x;
	}

	public float getY()
	{
		return this.y;
	}

	public float getZ() 
	{
		return this.z;
	}

	public float getW()
	{
		return this.w;
	}

	//#mdebug debug

	public String propertiesToString()
	{
		String newline = "\n";

		String output = 
			"x: "+this.x+newline+
			"y: "+this.y+newline+
			"z: "+this.z+newline+
			"W: "+this.w;

		return output;
	}

	//#enddebug
}
