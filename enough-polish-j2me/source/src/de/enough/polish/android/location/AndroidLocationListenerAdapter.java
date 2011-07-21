//#condition polish.android
package de.enough.polish.android.location;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class AndroidLocationListenerAdapter implements LocationListener {

	private de.enough.polish.android.location.LocationListener listener;
	private final AndroidLocationProvider locationProvider;

	public AndroidLocationListenerAdapter(AndroidLocationProvider locationProvider) {
		this.locationProvider = locationProvider;
	}

	public void onLocationChanged(Location location) {
		//#debug
		System.out.println("onLocationChanged:"+location);
		if(this.listener == null) {
			//#debug warn
			System.out.println("onLocationChanged:No listener to inform about location change. Doing nothing.");
			return;
		}
		if(location == null) {
			//#debug warn
			System.out.println("onLocationChanged:location is null.");
			return;
		}
		de.enough.polish.android.location.Location wrappedLocation = new de.enough.polish.android.location.Location(location);
		LocationProvider.setLastKnownLocation(wrappedLocation);
		this.listener.locationUpdated(this.locationProvider, wrappedLocation);
	}

	/**
	 * Called when the user disables a location provider.
	 */
	public void onProviderDisabled(String providerName) {
		//#debug
		System.out.println("onProviderDisabled:"+providerName);
		onStatusChanged(providerName, android.location.LocationProvider.OUT_OF_SERVICE, null);
	}

	/**
	 * Called when the user enables a location provider.
	 */
	public void onProviderEnabled(String providerName) {
		//#debug
		System.out.println("onProviderEnabled:"+providerName);
		onStatusChanged(providerName, android.location.LocationProvider.TEMPORARILY_UNAVAILABLE, null);
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		String statusName;
		int translatedStatus;
		switch(status) {
			case android.location.LocationProvider.AVAILABLE:
				statusName = "Available";
				translatedStatus = LocationProvider.AVAILABLE;
				break;
			case android.location.LocationProvider.OUT_OF_SERVICE:
				statusName = "Out of Service";
				translatedStatus = LocationProvider.OUT_OF_SERVICE;
				break;
			case android.location.LocationProvider.TEMPORARILY_UNAVAILABLE:
				statusName = "Temporarily Unavailable";
				translatedStatus = LocationProvider.TEMPORARILY_UNAVAILABLE;
				break;
			default: throw new IllegalArgumentException("LocationProvider.onStatusChanged: Status '"+status+"' not recognized.");
		}
		
		//#debug
		System.out.println("onStatusChanged:provider:"+provider+".Status:"+statusName);
		this.listener.providerStateChanged(this.locationProvider, translatedStatus);
		
		this.locationProvider.setState(translatedStatus);
	}
	
	public void setListener(de.enough.polish.android.location.LocationListener locationListener) {
		this.listener = locationListener;
	}

	public de.enough.polish.android.location.LocationListener getListener() {
		return this.listener;
	}
	
}
