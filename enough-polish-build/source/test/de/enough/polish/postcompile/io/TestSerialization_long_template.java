package de.enough.polish.postcompile.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.enough.polish.io.Externalizable;

public class TestSerialization_long_template
  implements Externalizable
{
	public long i;

	public void read(DataInputStream input)
	  throws IOException
	{
		this.i = input.readLong();
	}
	
	public void write(DataOutputStream output)
	  throws IOException
	{
		output.writeLong(this.i);
	}
}
