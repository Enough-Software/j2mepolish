package de.enough.polish.postcompile.io;

import de.enough.polish.io.Serializable;

import java.util.Date;

public class TestSerialization_complex2
	implements Serializable
{
	private final String text;
	private transient final Date date;
	private long time;

	public TestSerialization_complex2( String text )
	{
		this.text = text;
		this.date = new Date();
		this.time = System.currentTimeMillis();
	}

	public TestSerialization_complex2()
	{
		super();
		this.text = null;
		this.date = null;
		this.time = 0;
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
