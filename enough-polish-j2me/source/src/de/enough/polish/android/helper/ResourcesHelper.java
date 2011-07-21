//#condition polish.android
package de.enough.polish.android.helper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import android.content.res.Resources;
import android.util.Log;
import de.enough.polish.android.midlet.MIDlet;
import de.enough.polish.android.midlet.MidletBridge;

public class ResourcesHelper {

	/**
	 *	The map to store the resource name 
	 */
	private static HashMap<String,Integer> resourceMap;
	private static Resources resources;
	
	/**
	 *	Get the resource path to cut it off the incoming resourcename 
	 */
	private static final String resourcePath = "/res/raw"; 
	
	//#if polish.JavaPlatform >= Android/2.1
	private static final int start = 0x7f040000;
	private static final int end =   0x7f050000;
	//#else 
	//# private static final int start = 0x7f030000;
	//# private static final int end = 0x7f040000;
	//#endif
	
	/**
	 * Replaces any illegal characters from the name with an underscore.
	 * @param resourceName
	 * @return the name without illegal characters
	 */
	public static String cleanResourceName(String resourceName) {
		//TODO remove other illegal characters like several dots, etc.
		String cleanedName = resourceName.replace('-', '_');
		int lastIndexOfSlash = cleanedName.lastIndexOf(File.separatorChar);
		if(lastIndexOfSlash > 0) {
			cleanedName = cleanedName.substring(lastIndexOfSlash+1);
		}
		return cleanedName;
	}
	
	private static void initResources()
	{
		resourceMap = new HashMap<String,Integer>();
		resources = MidletBridge.instance.getResources();
		int id = -1;
		try
		{	
			for(id=start; id<end; id++)
			{
				String name = resources.getString(id);
				int resourcePathOffset = resourcePath.length() - 1;
				name = name.substring(resourcePathOffset);
				resourceMap.put(name, new Integer(id));
			}
		}
		catch(Resources.NotFoundException e){
			// ignore, this is expected at the end of resources
		}
		catch(NullPointerException e)
		{
			Log.e(MIDlet.TAG, "Caught NullPointerException, either you're calling FileUtil.loadFileName(name) in the MIDlet constructur or you're just doing it wrong");
		}
	}
	

	public static int getResourceID(String resourceName) throws IOException
	{
		resourceName = resourceName.toLowerCase();
		if(resourceMap == null)
		{
			initResources();
		}
		Integer idInteger = resourceMap.get(resourceName);
		if (idInteger == null) {
			String cleanedResourceName = cleanResourceName(resourceName);
			idInteger = resourceMap.get(cleanedResourceName);
			if (idInteger == null) {
				throw new IOException("resource does not exist: " + resourceName);
			}
		}
		int resourceId = idInteger.intValue();
		return resourceId;
	}
	
	public static InputStream getResourceAsStream(String resourceName)
	{
		try
		{
			int id = ResourcesHelper.getResourceID(resourceName);
			InputStream is = MidletBridge.instance.getResources().openRawResource(id);
			if (is == null) {
				return null;
			}
			return new ResourceInputStream( resourceName, id, is );
		} catch (IOException e)
		{
			//#debug error
			System.out.println("Unable to get resource " + resourceName + e );
			return null;
		}
	}

	/**
	 * Same as getResource(resourceName), but this method is easier to map within the postcompilation phase.
	 * 
	 * @param clazz the class
	 * @param resourceName the path of the resource, e.g. /myimage.png
	 * @return the resource as input stream
	 */
	public static InputStream getResourceAsStream(Class clazz, String resourceName)
	{
		return getResourceAsStream(resourceName);
	}

}
