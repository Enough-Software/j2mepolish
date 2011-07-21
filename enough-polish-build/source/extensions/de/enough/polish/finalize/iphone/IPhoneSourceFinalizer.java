package de.enough.polish.finalize.iphone;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.finalize.Finalizer;

public class IPhoneSourceFinalizer
	extends Finalizer
{
	private static final String OBJCDOTTER_MAIN_CLASS = "de.enough.polish.dotter.ObjCDotter";

	/* (non-Javadoc)
	 * @see de.enough.polish.finalize.Finalizer#finalize(java.io.File, java.io.File, de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void finalize(File jadFile, File jarFile, Device device, Locale locale, Environment env)
	{
		File sourceDir = new File(device.getSourceDir());
		File ocSourceDir = new File(sourceDir, "../source-oc");
		File importDir = new File(environment.getProjectHome(), "../enough-polish-build/import");

		// Create argument array for ObjCDotter.
		Object[] arguments = new Object[] {
				sourceDir,
				ocSourceDir,
				importDir,
		};

		Class dotterClass = null;
		Method startMethod = null;

		// Find ObjCDotter main class.
		try {
			dotterClass = Class.forName(OBJCDOTTER_MAIN_CLASS);
		}
		catch (ClassNotFoundException e) {
			System.err.println("Cannot find ObjCDotter main class");
			return;
		}

		// Find start method.
		try {
			startMethod = dotterClass.getMethod("doDotter", new Class[] { File.class, File.class, File.class });
		}
		catch (NoSuchMethodException e) {
			System.err.println("Cannot find ObjCDotter main method");
			return;
		}
		catch (SecurityException e) {
			System.err.println("Cannot find ObjCDotter main method due to security reasons");
			return;
		}

		// Run start method.
		try {
			startMethod.invoke(null, arguments);
		}
		catch (IllegalArgumentException e) {
			System.err.println("Cannot run ObjCDotter main method due to illegal arguments");
			return;
		}
		catch (IllegalAccessException e) {
			System.err.println("Cannot run ObjCDotter main method due to security reasons");
			return;
		}
		catch (InvocationTargetException e) {
			e.getCause().printStackTrace();
			return;
		}
	}
}
