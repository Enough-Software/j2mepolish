//#condition polish.android
package de.enough.polish.android.helper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;
import de.enough.polish.android.midlet.MIDlet;
import de.enough.polish.android.midlet.MidletBridge;
import de.enough.polish.util.TextUtil;

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
		String cleanedName = resourceName.replace('-', '_');
		cleanedName = cleanedName.replace(' ', '_');
		cleanedName = TextUtil.replace(cleanedName, ".", "_dot_");
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
			AssetManager assetManager = resources.getAssets();
			try {
				print( "", assetManager.list(""));
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				print( "images", assetManager.list("images"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				print( "sounds", assetManager.list("sounds"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				print( "webkit", assetManager.list("webkit"));
			} catch (IOException e) {
				e.printStackTrace();
			}
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
			e.printStackTrace();
		}
	}
	

	private static void print(String path, String[] list) {
		if (list == null || list.length == 0) {
			System.out.println("path: [" + path + "]: no resources" );
			return;
		}
		for (int i = 0; i < list.length; i++) {
			String string = list[i];
			System.out.println("path: [" + path + "]: " + string );
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
			resourceMap.put(resourceName, idInteger);
		}
		int resourceId = idInteger.intValue();
		return resourceId;
	}
	
	/**
	 * Retrieves a resource as an input stream
	 * 
	 * @param resourceName the path of the resource, e.g. /myimage.png
	 * @return the resource as input stream, either null when not found or as a ResourceInputStream
	 * @see ResourceInputStream
	 */
	public static InputStream getResourceAsStream(String resourceName)
	{
		//#if polish.JavaPlatform >= Android/1.5
			try {
				if (resources == null) {
					resources = MidletBridge.getInstance().getResources();
				}
				String cleanedResourceName = resourceName.substring(1);
				InputStream in = resources.getAssets().open(cleanedResourceName);
				return new ResourceInputStream(resourceName, cleanedResourceName, 0, in);
			} catch (IOException e) {
				//#debug error
				System.out.println("Unable to get resource [" + resourceName + "]" + e );
			}
		//#else
			try {
				String cleanedResourceName = resourceName.toLowerCase();
				if(resourceMap == null) {
					initResources();
				}
				Integer idInteger = resourceMap.get(cleanedResourceName);
				if (idInteger == null) {
					cleanedResourceName = cleanResourceName(cleanedResourceName);
					idInteger = resourceMap.get(cleanedResourceName);
					if (idInteger == null) {
						throw new IOException("resource does not exist: " + resourceName );
					}
					resourceMap.put(resourceName, idInteger);
				}
				int resourceId = idInteger.intValue();
				InputStream is = resources.openRawResource(resourceId);
				if (is == null) {
					return null;
				}
				return new ResourceInputStream( resourceName, cleanedResourceName, resourceId, is );
			} catch (IOException e) {
				//#debug error
				System.out.println("Unable to get resource [" + resourceName + "]" + e );
			}
		//#endif
		return null;
	}

	/**
	 * Same as getResource(resourceName), but this method is easier to map within the postcompilation phase.
	 * 
	 * @param clazz the class
	 * @param resourceName the path of the resource, e.g. /myimage.png
	 * @return the resource as input stream, either null when not found or as a ResourceInputStream
	 * @see ResourceInputStream
	 */
	public static InputStream getResourceAsStream(Class clazz, String resourceName)
	{
		return getResourceAsStream(resourceName);
	}

}
