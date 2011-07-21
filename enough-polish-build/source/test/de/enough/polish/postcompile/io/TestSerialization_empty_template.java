package de.enough.polish.postcompile.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.enough.polish.io.Externalizable;

public class TestSerialization_empty_template
  implements Externalizable
{
	public void read(DataInputStream input)
	  throws IOException
	{
	}
	
	public void write(DataOutputStream output)
	  throws IOException
	{
	}
}
