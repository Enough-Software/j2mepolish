package de.enough.polish.postcompile.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.enough.polish.io.Externalizable;

public class TestSerialization_int_template
  implements Externalizable
{
	public int i;

	public void read(DataInputStream input)
	  throws IOException
	{
		this.i = input.readInt();
	}
	
	public void write(DataOutputStream output)
	  throws IOException
	{
		output.writeInt(this.i);
	}
}
