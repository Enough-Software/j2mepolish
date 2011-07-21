package de.enough.polish.postcompile.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.enough.polish.io.Externalizable;
import de.enough.polish.io.Serializer;

public class TestSerialization_Serializable_template
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
	
	public InnerSerializable field;

	public void read(DataInputStream input)
		throws IOException
	{
		this.field = (InnerSerializable) Serializer.deserialize(input);
	}

	public void write(DataOutputStream output)
		throws IOException
	{
		Serializer.serialize(this.field, output);
	}
}
