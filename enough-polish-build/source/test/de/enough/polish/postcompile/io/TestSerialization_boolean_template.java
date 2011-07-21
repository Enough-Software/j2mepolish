package de.enough.polish.postcompile.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.enough.polish.io.Externalizable;

public class TestSerialization_boolean_template
  implements Externalizable
{
	public boolean i;

	public void read(DataInputStream input)
	  throws IOException
	{
		this.i = input.readBoolean();
	}
	
	public void write(DataOutputStream output)
	  throws IOException
	{
		output.writeBoolean(this.i);
	}
}
