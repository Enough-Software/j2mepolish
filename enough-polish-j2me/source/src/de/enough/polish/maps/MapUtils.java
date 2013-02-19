//#condition polish.hasFloatingPoint
package de.enough.polish.maps;

/**
 * Contains various general-purpose utility methods related to maps
 * @author Ovidiu Iliescu
 */
public class MapUtils {
	
	/**
	 * Calculates the distance between two points
	 * @param lat1 first point latitude
	 * @param lon1 first point longitude
	 * @param lat2 second point latitude
	 * @param lon2 second point longitude
	 * @return the distance between the points
	 */
	public static double distanceBetweenPoints(double lat1, double lon1, double lat2, double lon2 ) {
		return ( Math.sqrt((lat1-lat2)*(lat1-lat2) + (lon1-lon2)*(lon1-lon2)));
	}
	
}
