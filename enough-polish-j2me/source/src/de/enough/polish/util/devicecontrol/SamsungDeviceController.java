//#condition polish.optional-api.samsung
/**
 * 
 */
package de.enough.polish.util.devicecontrol;

import com.samsung.util.LCDLight;

/**
 * Controls a device that supports the Nokia-UI API
 * 
 * @author Robert Virkus
 */
public class SamsungDeviceController 
implements DeviceController, Runnable 
{

	Object lightsLock = new Object();
	private boolean lightOn = false;
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.util.devicecontrol.DeviceController#lightOn()
	 */
	public boolean lightOn() {
		synchronized (this.lightsLock) {
			if (isLightSupported()) {
				if (!this.lightOn) { 
					this.lightOn = true;
					LCDLight.on(1000);
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
			this.lightOn = false;
			LCDLight.off();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.util.devicecontrol.DeviceController#isLightSupported()
	 */
	public boolean isLightSupported() {
		return LCDLight.isSupported();
	}

	/**
	 * Keeps the backlight on until lightOff() is being called.
	 */
	public void run() {
		int displaytime = 10000;
		long sleeptime = (displaytime * 90) / 100;
		boolean increaseAfterFirstLoop = true;
		while(this.lightOn)
		{
			LCDLight.on( displaytime );
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
