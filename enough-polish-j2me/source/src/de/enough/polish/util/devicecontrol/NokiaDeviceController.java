//#condition polish.optional-api.samsung
/**
 * 
 */
package de.enough.polish.util.devicecontrol;

import com.nokia.mid.ui.DeviceControl;

/**
 * Controls a device that supports the Nokia-UI API
 * 
 * @author Robert Virkus
 */
public class NokiaDeviceController 
implements DeviceController, Runnable 
{

	Object lightsLock = new Object();
	private boolean isLightOn = false;
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.util.devicecontrol.DeviceController#lightOn()
	 */
	public boolean lightOn() {
		synchronized (this.lightsLock) {
			if (isLightSupported()) {
				if (!this.isLightOn) { 
					this.isLightOn = true;
					Thread t = new Thread(this);
					t.start();
				}
				return true;
			}  else {
				return false;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.util.devicecontrol.DeviceController#lightOff()
	 */
	public void lightOff() {
		synchronized(this.lightsLock ) {
			this.isLightOn = false;
			DeviceControl.setLights(0,0);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.util.devicecontrol.DeviceController#isLightSupported()
	 */
	public boolean isLightSupported() {
		//TODO find out if the bug polish.Bugs.NoBacklight is present ... dynamically...
		return true;
	}

	/**
	 * Keeps the backlight on until lightOff() is being called.
	 */
	public void run() {
		int displaytime = 10000;
		long sleeptime = (displaytime * 90) / 100;
		boolean increaseAfterFirstLoop = true;
		while(this.isLightOn)
		{
			// #if polish.Bugs.BacklightRequiresLightOff
				//TODO how to find out if we need to do this dynamically?
				DeviceControl.setLights(0,0);
			// #endif
			DeviceControl.setLights(0,100);
			try {
				Thread.sleep(sleeptime);
			} catch (InterruptedException e) {
				// ignore
			}
			if (increaseAfterFirstLoop) {
				increaseAfterFirstLoop = false;
				displaytime = 20000;
				sleeptime = 18000;
			}
		}
	}

}
