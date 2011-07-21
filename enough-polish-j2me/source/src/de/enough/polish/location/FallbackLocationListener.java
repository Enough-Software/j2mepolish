//#condition polish.api.locationapi
package de.enough.polish.location;
import javax.microedition.location.Criteria;

/**
 * This listener is informed about Location providers that have been enabled for the specified criteria.
 * @author Robert Virkus
 * @author Richard Nkrumah
 * @see FallbackLocationProvider#setFallbackLocationListener(FallbackLocationListener)
 */
public interface FallbackLocationListener {

	/**
	 * Notifies that a provider has been enabled
	 * @param criteria the criteria, can be null
	 */
	void providerEnabled(Criteria criteria);

}
