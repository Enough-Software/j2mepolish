package de.enough.polish.postcompile.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.enough.polish.io.Externalizable;
import de.enough.polish.io.Serializer;

public class TestSerialization_String_template
  implements Externalizable
{
	public String str;
	
	public void read(DataInputStream output)
	  throws IOException
	{
		this.str = (String) Serializer.deserialize(output);
	}
	
	public void write(DataOutputStream output)
	  throws IOException
	{
		Serializer.serialize(this.str, output);
	}
}
