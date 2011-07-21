package de.enough.polish.postcompile.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.enough.polish.io.Externalizable;

public class TestSerialization_double_template
  implements Externalizable
{
	public double i;

	public void read(DataInputStream input)
	  throws IOException
	{
		this.i = input.readDouble();
	}
	
	public void write(DataOutputStream output)
	  throws IOException
	{
		output.writeDouble(this.i);
	}
}
