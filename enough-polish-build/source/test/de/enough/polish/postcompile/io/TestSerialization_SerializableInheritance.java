package de.enough.polish.postcompile.io;

import de.enough.polish.io.Serializable;

public class TestSerialization_SerializableInheritance
	implements Serializable
{
  public static class AbstractSerializable
    implements Serializable
  {
    public int i1;
  }
  
	public static class InnerSerializable
		extends AbstractSerializable
	{
		public int i2;
	}
	
	public InnerSerializable field;
}
