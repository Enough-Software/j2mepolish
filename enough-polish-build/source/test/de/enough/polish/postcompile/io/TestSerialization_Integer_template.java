package de.enough.polish.postcompile.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.enough.polish.io.Externalizable;
import de.enough.polish.io.Serializer;

public class TestSerialization_Integer_template
  implements Externalizable
{
	public Integer i;
	
	public void read(DataInputStream output)
	  throws IOException
	{
		this.i = (Integer) Serializer.deserialize(output);
	}
	
	public void write(DataOutputStream output)
	  throws IOException
	{
		Serializer.serialize(this.i, output);
	}
}
