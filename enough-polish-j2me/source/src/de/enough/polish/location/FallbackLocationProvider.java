//#condition polish.api.locationapi
package de.enough.polish.location;
import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;

import de.enough.polish.util.ArrayList;
import de.enough.polish.util.HashMap;

/**
 * A location provider that uses different location providers, e.g. Network and GPS providers.
 * Location updates will be retrieved by the best (first) provider.
 *  
 * @author Robert Virkus
 * @author Richard Nkrumah
 *
 */
public class FallbackLocationProvider 
extends LocationProvider
implements LocationListener
{
	
	private FallbackLocationListener fallbackLocationListener;
	private LocationProvider activeProvider;
	private boolean isReset;
	private LocationListener locationListener;
	private final LocationProvider[] providers;
	private HashMap criteriaByProvider;
	
	private FallbackLocationProvider( Criteria[] criteriaFallbacks ) throws LocationException {
		HashMap uniqueProviders = new HashMap(criteriaFallbacks.length);
		ArrayList providersList = new ArrayList(criteriaFallbacks.length);
		// go from bottom to top, in case there is a location API that uses a single Location provider for all criteria.
		// In such a case we have at least the most important provider:
		for (int i=criteriaFallbacks.length; --i >= 0; ) {
			Criteria criteria = criteriaFallbacks[i];
			LocationProvider provider = LocationProvider.getInstance(criteria);
			if (provider != null) {
				if (criteria != null) {
					uniqueProviders.put( provider, criteria);
				} else {
					uniqueProviders.put( provider, new Object() );				
				}
				providersList.add( 0, provider );
			}
		}
		if (uniqueProviders.size() == 0) {
			throw new LocationException();
		}
		this.providers = (LocationProvider[]) providersList.toArray( new LocationProvider[ uniqueProviders.size() ]);
		this.criteriaByProvider = uniqueProviders;
		
	}

	/**
	 * Retrieves an instance of the FallbackLocationProvier
	 * @param criteria the criteria that are sorted from the most to the least important one (so the most important one is in criteria[0]).
	 * @return the FallbackLocationProvider instance
	 * @throws LocationException if all location providers are currently out of service
	 */
	public static FallbackLocationProvider getInstance( Criteria[] criteria ) throws LocationException {
		//TODO consider to use singletons, depending on usage of this API.
		return new FallbackLocationProvider( criteria );
	}

	/**
	 * Sets the listener that is informed when a criteria is enabled
	 * @param fallbackLocationListener the listener, use null to remove listener
	 */
	public void setFallbackLocationListener(FallbackLocationListener fallbackLocationListener) {
		this.fallbackLocationListener = fallbackLocationListener;
	}
	
	/**
	 * Retrieves the number of unique location providers that are used within this FallbackLocationProvider
	 * @return the number
	 */
	public int getNumberOfUniqueLocationProviders() {
		return this.providers.length;
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.microedition.location.LocationProvider#getLocation(int)
	 */
	public Location getLocation(int timeoutInSeconds) throws LocationException, InterruptedException {
		LocationProvider provider = this.activeProvider;
		if (this.locationListener != null) {
			long waitedTime = 0;
			while (provider == null) {
				try {
					Thread.sleep(200);
					waitedTime += 200;
					if (waitedTime >= timeoutInSeconds*1000) {
						throw new InterruptedException();
					}
					if (this.isReset) {
						this.isReset = false;
						throw new InterruptedException();
					}
				} catch (InterruptedException e) {
					// ignore
				}
				provider = this.activeProvider;
			}
			timeoutInSeconds -= (waitedTime/1000);
		} else {
			for (int i=0; i < this.providers.length; i++) {
				LocationProvider prov = this.providers[i];
				if (prov.getState() == AVAILABLE) {
					provider = prov;
					break;
				}
			}
			if (provider == null) {
				provider = this.providers[0];
			}
		}
		return provider.getLocation(timeoutInSeconds);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.microedition.location.LocationProvider#getState()
	 */
	public int getState() {
		LocationProvider provider = this.activeProvider;
		if (provider == null) {
			return TEMPORARILY_UNAVAILABLE;
		}
		return provider.getState();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.microedition.location.LocationProvider#reset()
	 */
	public void reset() {
		LocationProvider provider = this.activeProvider;
		if (provider == null) {
			this.isReset = true;
		} else {
			provider.reset();
		}
		//TODO: reset other providers as well?
	}

	/*
	 * (non-Javadoc)
	 * @see javax.microedition.location.LocationProvider#setLocationListener(javax.microedition.location.LocationListener, int, int, int)
	 */
	public void setLocationListener(LocationListener listener, int interval, int timeout, int maxAge) {
		this.locationListener = listener;
		for (int i = 0; i < this.providers.length; i++) {
			LocationProvider provider = this.providers[i];
			provider.setLocationListener( listener == null ? null : this, interval, timeout, maxAge);
			
		}
	}
	
	/////////////////////// Location Listener methods ////////////////////////////////////////////

	/**
	 * LocationListener method - do not call.
	 */
	public void locationUpdated(LocationProvider provider, Location loc) {
		if ((provider == this.activeProvider || this.activeProvider == null) && this.locationListener != null) {
			this.locationListener.locationUpdated(this, loc);
		}
		
	}

	/**
	 * LocationListener method - do not call.
	 */
	public void providerStateChanged(LocationProvider changedProvider, int state) {
		LocationProvider enabledProvider = null;
		if (state == AVAILABLE) {
			if (this.activeProvider == null) {
				enabledProvider = changedProvider;
			} else {
				for (int i=0; i<this.providers.length; i++) {
					LocationProvider prov = this.providers[i];
					if (prov == this.activeProvider) {
						break;
					}
					if (prov == changedProvider) {
						enabledProvider = changedProvider;
						break;
					}
				}
			}
		} else if (changedProvider == this.activeProvider) {
			// the active provider has been deactivated, so find the next best matching one:
			for (int i=0; i<this.providers.length; i++) {
				LocationProvider prov = this.providers[i];
				if (prov.getState() == AVAILABLE) {
					enabledProvider = prov;
					break;
				}
			}
			if (enabledProvider == null) {
				// handle case when active provider has been disabled and no other is available:
				this.activeProvider = null;
				if (this.locationListener != null) {
					this.locationListener.providerStateChanged(this, TEMPORARILY_UNAVAILABLE);
				}
			}
		}
		
		if (enabledProvider != null) {
			this.activeProvider = enabledProvider;
			if (this.fallbackLocationListener != null) {
				Object criteriaObj = this.criteriaByProvider.get(enabledProvider);
				if (criteriaObj instanceof Criteria) {
					this.fallbackLocationListener.providerEnabled( (Criteria) criteriaObj);
				} else {
					//TODO: This should be a providerDisabled method.
					this.fallbackLocationListener.providerEnabled( null );
				}
			}
			this.locationListener.providerStateChanged(this, AVAILABLE);
		}
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("FallbackLocationProvider{activeProvider='");
		buffer.append(this.activeProvider);
		buffer.append("',AllProviders=[");
		if(this.providers.length > 0) {
			buffer.append(this.providers[0]);
		}
		for (int i = 1; i < this.providers.length; i++) {
			buffer.append("',");
			buffer.append(this.providers[i]);
		}
		buffer.append("]}");
		String result = buffer.toString();
		return result;
	}
	
	public LocationProvider getActiveLocationProvider() {
		return this.activeProvider;
	}
	
}