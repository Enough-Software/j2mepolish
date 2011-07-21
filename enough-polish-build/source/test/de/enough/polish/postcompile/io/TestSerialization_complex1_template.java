package de.enough.polish.postcompile.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.enough.polish.io.Externalizable;

public class TestSerialization_complex1_template
  implements Externalizable
{
  private int rgb;
  private int[] buffer;
  private int width;

  public TestSerialization_complex1_template()
  {
    this.rgb = 1;
  }


	public int getRgb()
	{
		return rgb;
	}

	public int[] getBuffer()
	{
		return buffer;
	}

	public int getWidth()
	{
		return width;
	}
	
  public void read(DataInputStream input)
	  throws IOException
	{
		if (input.readBoolean())
		  {
		    int length = input.readInt();
		    this.buffer = new int[length];
			
		    for (int i = 0; i < length; i++)
		      {
		        this.buffer[i] = input.readInt();
		      }
		  }
    
		this.rgb = input.readInt();
    this.width = input.readInt();
	}
	
	public void write(DataOutputStream output)
	  throws IOException
	{
		if (this.buffer == null)
		  {
		    output.writeBoolean(false);
		  }
		else
		  {
		    output.writeBoolean(true);
		    int length = this.buffer.length;
		    output.writeInt(length);
			
		    for (int i = 0; i < length; i++)
		      {
		        output.writeInt(this.buffer[i]);
		      }
		  }
    
		output.writeInt(this.rgb);
    output.writeInt(this.width);
	}
}
