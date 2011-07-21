package de.enough.polish.postcompile.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.enough.polish.io.Externalizable;

public class TestSerialization_float_template
  implements Externalizable
{
	public float i;

	public void read(DataInputStream input)
	  throws IOException
	{
		this.i = input.readFloat();
	}
	
	public void write(DataOutputStream output)
	  throws IOException
	{
		output.writeFloat(this.i);
	}
}
