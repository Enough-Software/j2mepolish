package de.enough.polish.postcompile.io;

import de.enough.polish.io.Externalizable;
import de.enough.polish.io.Serializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TestSerialization_SerializableInheritance_template
	implements Externalizable
{
  public static class AbstractSerializable
    implements Externalizable
  {
    public int i1;
    
    public void read(DataInputStream input)
      throws IOException
    {
      this.i1 = input.readInt();
    }
  
    public void write(DataOutputStream output)
      throws IOException
    {
      output.writeInt(this.i1);
    }
  }
  
	public static class InnerSerializable
		extends AbstractSerializable
	{
		public int i2;

    public void read(DataInputStream input)
      throws IOException
    {
      super.read(input);
      this.i2 = input.readInt();
    }

    public void write(DataOutputStream output)
      throws IOException
    {
      super.write(output);
      output.writeInt(this.i2);
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
