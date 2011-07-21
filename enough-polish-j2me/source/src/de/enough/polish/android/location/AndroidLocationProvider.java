//#condition polish.android
package de.enough.polish.android.location;

import java.util.List;

import android.content.Context;
import android.location.LocationManager;
import android.os.Looper;

import de.enough.polish.android.midlet.MidletBridge;

public class AndroidLocationProvider extends LocationProvider {

	private final class LocationUpdateThread extends Thread {
		private Looper looper;

		public LocationUpdateThread(String name) {
			super(name);
		}

		public void run() {
			Looper.prepare();
			this.looper = Looper.myLooper();
			Looper.loop();
		}

		public Looper getLooper() {
			while(this.looper == null) {
				try {
					Thread.sleep(30);
					//#debug
					System.out.println("Waited for 30ms for the looper to prepare.");
				} catch (InterruptedException e) {
					// ignore
				}
			}
			return this.looper;
		}
	}

	public static final int ACCURACY_THRESHOLD = 50;
	public static final int DEFAULT_POWER_REQIREMENT = android.location.Criteria.POWER_MEDIUM;
	public static final int DEFAULT_MINIMAL_LOCATION_UPDATES = 1000; //msec
	public static final float DEFAULT_MINIMAL_LOCATION_DISTANCE = 1; // meters
	
	private static LocationManager locationManager;

	private String providerId;
	private AndroidLocationListenerAdapter currentLocationListener;
	private LocationUpdateThread locationUpdateThread;
	private int state = OUT_OF_SERVICE;
	
	/**
	 * @param meCriteria May be null.
	 * @throws LocationException 
	 */
	public static AndroidLocationProvider getAndroidLocationProviderInstance(Criteria meCriteria) throws LocationException {
		if(locationManager == null) {
			locationManager = (LocationManager)MidletBridge.instance.getSystemService(Context.LOCATION_SERVICE);
		}
		int powerRequirement;
		if(meCriteria == null) {
			meCriteria = new Criteria();
		}
		
		String bestProvider = null;

		String requestedLocationProviderId = meCriteria.getRequestedLocationProviderId();
		if(requestedLocationProviderId != null) {
			bestProvider = requestedLocationProviderId;
		} else {
			int preferredPowerConsumption = meCriteria.getPreferredPowerConsumption();
			switch(preferredPowerConsumption) {
				case Criteria.NO_REQUIREMENT: powerRequirement = DEFAULT_POWER_REQIREMENT; break;
				case Criteria.POWER_USAGE_HIGH: powerRequirement = android.location.Criteria.POWER_HIGH; break;
				case Criteria.POWER_USAGE_MEDIUM: powerRequirement = android.location.Criteria.POWER_MEDIUM; break;
				case Criteria.POWER_USAGE_LOW: powerRequirement = android.location.Criteria.POWER_LOW; break;
				default: throw new IllegalArgumentException("The power consumption must be one of Critiera.NO_REQUIREMENT, Criteria.POWER_USAGE_HIGH, Criteria.POWER_USAGE_MEDIUM or Criteria.POWER_USAGE_LOW.");
			}
			boolean altitudeRequired = meCriteria.isAltitudeRequired();
			boolean bearingRequired = meCriteria.isSpeedAndCourseRequired();
			boolean speedRequired = meCriteria.isSpeedAndCourseRequired();
			boolean costAllowed = meCriteria.isAllowedToCost();
			int accuracy;
			int horizontalAccuracy = meCriteria.getHorizontalAccuracy();
			if(horizontalAccuracy < ACCURACY_THRESHOLD) {
				accuracy = android.location.Criteria.ACCURACY_FINE;
			} else {
				accuracy = android.location.Criteria.ACCURACY_COARSE;
			}
			
			android.location.Criteria criteria = new android.location.Criteria();
			criteria.setAccuracy(accuracy);
			criteria.setSpeedRequired(speedRequired);
			criteria.setAltitudeRequired(altitudeRequired); 
			criteria.setBearingRequired(bearingRequired);
			criteria.setCostAllowed(costAllowed); 
			criteria.setPowerRequirement(powerRequirement);
			
			bestProvider = locationManager.getBestProvider(criteria, true);
		}
		
		//#debug
		System.out.println("getAndroidLocationProvider: Best provider for criteria is '"+bestProvider+"'");

		
		if(bestProvider == null) {
			// We have no provider found. If there is no provider enabled, throw an exception according to API.
			List<String> providers = locationManager.getProviders(true);
			if(providers.isEmpty()) {
				throw new LocationException("No enabled LocationProvider found. Enable an Location Provider and try again.");
			}
			return null;
		}
		
		return new AndroidLocationProvider(bestProvider);
	}

	private AndroidLocationProvider(String providerId) {
		this.providerId = providerId;
		this.locationUpdateThread = new LocationUpdateThread("LocationUpdateThread");
		this.locationUpdateThread.start();
	}
	
	@Override
	public Location getLocation(int timeout) throws LocationException,InterruptedException {
		android.location.Location location = locationManager.getLastKnownLocation(this.providerId);
		if(location == null) {
			//#debug warn
			System.out.println("getLocation():null received from LocationManager.getLastKnownLocation.");
			return null;
		}
		Location newLocation = new Location(location);
		LocationProvider.setLastKnownLocation(newLocation);
		//#debug
		System.out.println("getLocation():"+newLocation);
		return newLocation;
	}

	@Override
	public int getState() {
		return this.state;
	}

	@Override
	public void reset() {
		if(this.currentLocationListener != null) {
			locationManager.removeUpdates(this.currentLocationListener);
		}
	}

	@Override
	public void setLocationListener(de.enough.polish.android.location.LocationListener listener, int interval, int timeout, int maxAge) {
		//#debug
		System.out.println("Setting a location listener:"+listener);
		if(this.currentLocationListener == null) {
			// TODO: Create and add the adapter when the the provider is created. In this method, only set the new listener in a sync'ed way.
			this.currentLocationListener = new AndroidLocationListenerAdapter(this);
		}
		if(listener == null) {
			// Remove the listener.
			locationManager.removeUpdates(this.currentLocationListener);
			return;
		}
		//TODO: Handle the minTime gracefully. Use 0 for the fastest updates.
//		if(interval == -1) {
//			this.currentMinTime = DEFAULT_MINIMAL_LOCATION_UPDATES;
//		} else {
//			this.currentMinTime = interval;
//		}
		this.currentLocationListener.setListener(listener);
		registerLocationListener();
	}

	private void registerLocationListener() {
		// TODO: Promote the looper variable to a field.
		Looper looper = this.locationUpdateThread.getLooper();
		locationManager.requestLocationUpdates(this.providerId,0,0 ,this.currentLocationListener,looper);
	}

	public String getLocationProviderName() {
		return this.providerId;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("AndroidLocationProvider{provider='");
		buffer.append(this.providerId);
		buffer.append("',state='");
		buffer.append(getStateName(getState()));
		buffer.append("'}");
		String result = buffer.toString();
		return result;
	}

	private String getStateName(int stateId) {
		switch(stateId) {
			case AVAILABLE: return "Available";
			case TEMPORARILY_UNAVAILABLE: return "Temporarily Unavailable";
			case OUT_OF_SERVICE: return "Out of Service";
			default: return "Unknown State '"+stateId+"'";
		}
	}
	
	void setState(int state) {
		this.state = state;
	}
}
