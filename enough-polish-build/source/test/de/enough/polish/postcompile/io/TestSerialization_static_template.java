package de.enough.polish.postcompile.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.enough.polish.io.Externalizable;

public class TestSerialization_static_template
  implements Externalizable
{
  public static final int CONSTANT_VALUE = 5;
  
  private int i;

	public int getValue()
	{
		return i;
	}

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
