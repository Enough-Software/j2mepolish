package de.enough.polish.postcompile.io;

import de.enough.polish.io.Serializable;

public class TestSerialization_Serializable
	implements Serializable
{
	public static class InnerSerializable
		implements Serializable
	{
		public int i;
	}
	
	public InnerSerializable field;
}
