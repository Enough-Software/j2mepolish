package de.enough.polish.postcompile.io;

import de.enough.polish.io.Externalizable;
import de.enough.polish.io.Serializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

public class TestSerialization_complex2_template
	implements Externalizable
{
	private String text;
	private transient Date date;
	private long time;

	public TestSerialization_complex2_template( String text )
	{
		this.text = text;
		this.date = new Date();
		this.time = System.currentTimeMillis();
	}
  
	public TestSerialization_complex2_template()
	{
	}
  
	public void read(DataInputStream input)
		throws IOException
	{
		this.text = (String) Serializer.deserialize(input);
		this.time = input.readLong();
	}
  
	public void write(DataOutputStream output)
		throws IOException
	{
		Serializer.serialize(this.text, output);
		output.writeLong(this.time);
	}

	public String getText()
	{
		return text;
	}

	public Date getDate()
	{
		return date;
	}

	public long getTime()
	{
		return time;
	}
}
