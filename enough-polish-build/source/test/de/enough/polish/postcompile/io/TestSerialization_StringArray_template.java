package de.enough.polish.postcompile.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.enough.polish.io.Externalizable;
import de.enough.polish.io.Serializer;

public class TestSerialization_StringArray_template
  implements Externalizable
{
	public String[] strings;
	
	public void read(DataInputStream input)
	  throws IOException
	{
		if (input.readBoolean())
		  {
			int length = input.readInt();
			this.strings = new String[length];
			
			for (int i = 0; i < length; i++)
			{
				this.strings[i] = (String) Serializer.deserialize(input);
			}
		  }
	}
	
	public void write(DataOutputStream output)
	  throws IOException
	{
		if (this.strings == null)
		  {
			output.writeBoolean(false);
		  }
		else
		  {
			output.writeBoolean(true);
			int length = this.strings.length;
			output.writeInt(length);
			
			for (int i = 0; i < length; i++)
			  {
				Serializer.serialize(this.strings[i], output);
			  }
		  }
	}
}
