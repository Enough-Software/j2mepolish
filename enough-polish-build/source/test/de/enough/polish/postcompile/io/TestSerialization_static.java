package de.enough.polish.postcompile.io;

import de.enough.polish.io.Serializable;

public class TestSerialization_static
  implements Serializable
{
	public static final int CONSTANT_VALUE = 5;
  
	private int i;

	public int getValue()
	{
		return i;
	}
}
