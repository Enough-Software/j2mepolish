package de.enough.polish.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator{

	private static Activator instance;
	
	public void start(BundleContext context) throws Exception {
		instance = this;
	}

	public void stop(BundleContext context) throws Exception {
		instance = null;
	}

	public static Activator getDefault() {
		return instance;
	}
}
