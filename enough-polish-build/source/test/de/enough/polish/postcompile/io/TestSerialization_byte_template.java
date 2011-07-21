package de.enough.polish.postcompile.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.enough.polish.io.Externalizable;

public class TestSerialization_byte_template
  implements Externalizable
{
	public byte i;

	public void read(DataInputStream input)
	  throws IOException
	{
		this.i = input.readByte();
	}
	
	public void write(DataOutputStream output)
	  throws IOException
	{
		output.writeByte(this.i);
	}
}
