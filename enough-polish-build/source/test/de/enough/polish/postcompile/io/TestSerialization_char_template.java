package de.enough.polish.postcompile.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.enough.polish.io.Externalizable;

public class TestSerialization_char_template
  implements Externalizable
{
	public char i;

	public void read(DataInputStream input)
	  throws IOException
	{
		this.i = input.readChar();
	}
	
	public void write(DataOutputStream output)
	  throws IOException
	{
		output.writeChar(this.i);
	}
}
