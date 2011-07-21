package de.enough.polish.postcompile.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.enough.polish.io.Externalizable;
import de.enough.polish.io.Serializer;

public class TestSerialization_SerializableArray_template
  implements Externalizable
{
	public static class InnerSerializable
		implements Externalizable
	{
		public int i;

		public void write(DataOutputStream out)
			throws IOException
		{
		}

		public void read(DataInputStream in)
			throws IOException
		{
		}
	}

	public InnerSerializable[] fields;
	
	public void read(DataInputStream input)
	  throws IOException
	{
		if (input.readBoolean())
		  {
			int length = input.readInt();
			this.fields = new InnerSerializable[length];
			
			for (int i = 0; i < length; i++)
			{
				this.fields[i] = (InnerSerializable) Serializer.deserialize(input);
			}
		  }
	}
	
	public void write(DataOutputStream output)
	  throws IOException
	{
		if (this.fields == null)
		  {
			output.writeBoolean(false);
		  }
		else
		  {
			output.writeBoolean(true);
			int length = this.fields.length;
			output.writeInt(length);
			
			for (int i = 0; i < length; i++)
			  {
				Serializer.serialize(this.fields[i], output);
			  }
		  }
	}
}
