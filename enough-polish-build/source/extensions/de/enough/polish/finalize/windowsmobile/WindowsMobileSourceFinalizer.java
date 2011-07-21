package de.enough.polish.finalize.windowsmobile;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.finalize.Finalizer;

public class WindowsMobileSourceFinalizer
	extends Finalizer
{
	private static final String DOTTER_MAIN_CLASS = "de.enough.polish.dotter.Dotter";

	private File userJar;

	/* (non-Javadoc)
	 * @see de.enough.polish.finalize.Finalizer#finalize(java.io.File, java.io.File, de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void finalize(File jadFile, File jarFile, Device device, Locale locale, Environment env)
	{
		File sourceDir = new File(device.getSourceDir());
		File csSourceDir = new File(sourceDir, "../source-cs");
		File importDir = new File(environment.getProjectHome(), "../enough-polish-build/import");

		// Create argument array for Dotter.
		Object[] arguments = new Object[] {
				sourceDir,
				csSourceDir,
				importDir,
				this.userJar,
		};

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
			startMethod = dotterClass.getMethod("doDotter", new Class[] { File.class, File.class, File.class, File.class });
		}
		catch (NoSuchMethodException e) {
			System.err.println("Cannot find Dotter main method");
			return;
		}
		catch (SecurityException e) {
			System.err.println("Cannot find Dotter main method due to security reasons");
			return;
		}

		// Run start method.
		try {
			startMethod.invoke(null, arguments);
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

	public void setUserJar(String userJar)
	{
		System.out.println("Setting user jar: " + new File(userJar));

		this.userJar = new File(userJar);
	}
}
