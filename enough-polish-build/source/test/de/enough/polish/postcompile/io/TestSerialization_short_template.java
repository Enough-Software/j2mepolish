package de.enough.polish.postcompile.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.enough.polish.io.Externalizable;

public class TestSerialization_short_template
  implements Externalizable
{
	public short i;

	public void read(DataInputStream input)
	  throws IOException
	{
		this.i = input.readShort();
	}
	
	public void write(DataOutputStream output)
	  throws IOException
	{
		output.writeShort(this.i);
	}
}
