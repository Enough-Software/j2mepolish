//#condition polish.api.locationapi
package de.enough.polish.location;

import javax.microedition.location.Criteria;
import javax.microedition.location.LocationException;

//#if polish.android
import android.content.Context;
import android.location.LocationManager;
import de.enough.polish.android.midlet.MidletBridge;;
//#endif

public class LocationService {

	public static final Criteria CRITERIA_GPS_PROVIDER = createGpsCriteria();
	public static final Criteria CRITERIA_NETWORK_PROVIDER = createNetworkCriteria();

	/**
	 * Returns a FallbackLocationProvider object.
	 * 
	 * @param criteria
	 *            The value is a array containing all Criteria objects which
	 *            should be used in the given order. If the location provider
	 *            fitting the first criteria is not available anymore, the next
	 *            criteria is used to find an available location provider.
	 * @return a FallbackLocationProvider object which will use location
	 *         providers in the order of the given criteria. 
	 * @throws LocationException when all location providers are unavailable 
	 */
	public static FallbackLocationProvider getFallbackLocationProvider(Criteria[] criteria) 
	throws LocationException
	{
		return FallbackLocationProvider.getInstance(criteria);
	}


	/**
	 * Informs the caller about the status of the GPS device.
	 * 
	 * @return The value is 'true' if the GPS device is enabled on the device
	 *         and can be used. This does not say anything about the capability
	 *         of the GPS device to actually get a fix. The value 'false' is
	 *         returned if the GPS device is disabled.
	 */
	public static boolean isGpsEnabled() {
		boolean providerEnabled = false;
		//#if polish.android
		LocationManager locationManager = (LocationManager)MidletBridge.instance.getSystemService(Context.LOCATION_SERVICE);
		providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		//#endif
		return providerEnabled;
	}
	
	/**
	 * Informs the caller about the status of the Network location provider.
	 * 
	 * @return The value is 'true' if the Network location provider is enabled on the device
	 *         and can be used. This does not say anything about the capability
	 *         of the Network location provider to actually get a fix. The value 'false' is
	 *         returned if the Network location provider is disabled.
	 */
	public static boolean isNetworkLocationProviderEnabled() {
		boolean providerEnabled = false;
		//#if polish.android
		LocationManager locationManager = (LocationManager)MidletBridge.instance.getSystemService(Context.LOCATION_SERVICE);
		providerEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		//#endif
		return providerEnabled;
	}
	
	private static Criteria createNetworkCriteria() {
		Criteria criteria = new Criteria();
		criteria.setHorizontalAccuracy( 100 ); // 10 meters
		criteria.setVerticalAccuracy( 100 ); // 10 meters
		criteria.setAltitudeRequired(false);
		criteria.setPreferredPowerConsumption(Criteria.POWER_USAGE_LOW);
		criteria.setCostAllowed(true);
		criteria.setSpeedAndCourseRequired(false);
		criteria.setAddressInfoRequired(false);
		//#if polish.android
		//# criteria.setRequestedLocationProviderId(LocationManager.NETWORK_PROVIDER);
		//#endif
		return criteria;
	}

	private static Criteria createGpsCriteria() {
		Criteria criteria = new Criteria();
		criteria.setHorizontalAccuracy( 1 ); // 10 meters
		criteria.setVerticalAccuracy( 1 ); // 10 meters
		//#if polish.android
		//# criteria.setRequestedLocationProviderId(LocationManager.GPS_PROVIDER);
		//#endif
		return criteria;
	}
}