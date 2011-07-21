//#condition polish.usePolishGui && polish.api.mmapi
package de.enough.polish.video.util;

import javax.microedition.media.control.FramePositioningControl;

public class VideoUtil {
	public static int getSeconds(long time)
	{
		return (int)time / 1000000;
	}
	
	public static String getTime(long time)
	{
		String second = "" + getSeconds(time) % 60;
		String minute = "" + getSeconds(time) / 60;
		
		if(second.length() == 1)
		{
			second = "0" + second;
		}
		
		if(second.length() == 1)
		{
			minute = "0" + minute;
		}
		
		return minute + ":" + second;
	}
	
	public static long getPercent(long value, long max)
	{
		return (value * 100) / max;
	}
	
	public static long getPermille(long value, long max)
	{
		return (value * 1000) / max;
	}

	
	public static int getFrame(FramePositioningControl control, long time, long length)
	{
		long percent = getPercent(time, length);
		int totalFrames = control.mapTimeToFrame(length);
		
		int frame = (int)((totalFrames * percent) / 100L);
		
		return frame;
	}
}
