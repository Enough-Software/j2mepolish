package de.enough.polish.postcompile.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.enough.polish.io.Externalizable;

public class TestSerialization_shortArray_template
	implements Externalizable
{
	public short[] i;

	public void read(DataInputStream input)
	  throws IOException
	{
		if (input.readBoolean())
		  {
			int length = input.readInt();
			this.i = new short[length];
			
			for (int i = 0; i < length; i++)
			{
				this.i[i] = input.readShort();
			}
		  }
	}
	
	public void write(DataOutputStream output)
	  throws IOException
	{
		if (this.i == null)
		  {
			output.writeBoolean(false);
		  }
		else
		  {
			output.writeBoolean(true);
			int length = this.i.length;
			output.writeInt(length);
			
			for (int i = 0; i < length; i++)
			  {
			    output.writeShort(this.i[i]);
			  }
		  }
	}
}
