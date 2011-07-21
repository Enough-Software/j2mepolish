package de.enough.polish.finalize.windowsmobile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import de.enough.polish.Attribute;
import de.enough.polish.BuildException;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.descriptor.DescriptorCreator;
import de.enough.polish.finalize.Finalizer;

public class WindowsMobileFinalizer
	extends Finalizer
{
	private static final String DOTTER_MAIN_CLASS = "de.enough.windows.tools.dotter.Main";

	private void writeAppProperties(Device device, Environment env)
	{
		Attribute[] descriptorAttributes = (Attribute[]) env.get(DescriptorCreator.DESCRIPTOR_ATTRIBUTES_KEY);

		if (descriptorAttributes == null) {
			throw new BuildException("No descriptor attributes stored.");
		}

		try {
			File propertiesFile = new File(device.getResourceDir(), "/properties.txt");
			FileOutputStream fos = new FileOutputStream(propertiesFile);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));

			for (int index = 0; index < descriptorAttributes.length; index++) {
				Attribute attribute = descriptorAttributes[index];
				writer.write(attribute.getName());
				writer.write(": ");
				writer.write(attribute.getValue());
				writer.write("\r\n");
			}

			writer.close();
			fos.close();
		}
		catch (IOException e) {
			throw new BuildException(e);
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.finalize.Finalizer#finalize(java.io.File, java.io.File, de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void finalize(File jadFile, File jarFile, Device device, Locale locale, Environment env)
	{
		writeAppProperties(device, env);

		Class dotterClass = null;
		Method startMethod = null;

		// Find Dotter main class.
		try {
			dotterClass = Class.forName(DOTTER_MAIN_CLASS);
		}
		catch (ClassNotFoundException e) {
			System.err.println("Cannot find Dotter main class");
			return;
		}

		// Find start method.
		try {
			startMethod = dotterClass.getMethod("main", new Class[] { String[].class });
		}
		catch (NoSuchMethodException e) {
			System.err.println("Cannot find Dotter main method");
			return;
		}
		catch (SecurityException e) {
			System.err.println("Cannot find Dotter main  method due to security reasons");
			return;
		}

		// Run start method.
		try {
			startMethod.invoke(null, new Object[] { null });
		}
		catch (IllegalArgumentException e) {
			System.err.println("Cannot run Dotter main method due to illegal arguments");
			return;
		}
		catch (IllegalAccessException e) {
			System.err.println("Cannot run Dotter main method due to security reasons");
			return;
		}
		catch (InvocationTargetException e) {
			e.getCause().printStackTrace();
			return;
		}
	}
}
