//#condition polish.usePolishGui
package de.enough.polish.maps;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.MapItem;

/**
 * Defines a point of interest item, used in conjunction with a {@link MapItem} to overlay information over a map.
 * @author Ovidiu Iliescu
 */
public class PoiItem {

	/**
	 * The POI's geo latitude
	 */
	protected double lat;
	
	/**
	 * The POI's geo longitude
	 */
	protected double lon;
	
	/**
	 * The inner Polish item, that is actually overlayed on the map
	 */
	protected Item innerItem;
	
	/**
	 * The listener to be notified of events related to this POIItem
	 */
	protected PoiItemListener listener;
	
	/**
	 * Creates a new PoiItem
	 * @param lat the item's latitude
	 * @param lon the item's longitude
	 */
	public PoiItem(double lat, double lon) {
		this(lat, lon, null);
	}
	
	/**
	 * Creates a new PoiItem
	 * @param lat the item's latitude
	 * @param lon the item's longitude
	 * @param item the Polish item to display 
	 */
	public PoiItem(double lat, double lon, Item item) {
		setLat(lat);
		setLon(lon);
		setInnerItem(item);
	}	
		
	/**
	 * Get the current event listener
	 * @return the current event listener
	 */
	public PoiItemListener getListener() {
		return listener;
	}

	/**
	 * Sets the current event listener
	 * @param listener the current event listener
	 */
	public void setListener(PoiItemListener listener) {
		this.listener = listener;
	}

	/**
	 * Returns the item's latitude
	 * @return the item's latitude
	 */
	public double getLat() {
		return lat;
	}
	
	/**
	 * Sets the item's latitude
	 * @param lat the item's latitude
	 */
	public void setLat(double lat) {
		this.lat = lat;
	}
	
	/**
	 * Returns the item's longitude
	 * @return the item's longitude
	 */
	public double getLon() {
		return lon;
	}
	
	/**
	 * Sets the item's longitude
	 * @param lon the item's longitude
	 */
	public void setLon(double lon) {
		this.lon = lon;
	}
	
	/**
	 * Returns the item's inner Polish item 
	 * @return the item's inner Polish item
	 */
	public Item getInnerItem() {
		return innerItem;
	}
	
	/**
	 * Sets the item's inner Polish item
	 * @param innerItem the item's inner Polish item
	 */
	public void setInnerItem(Item innerItem) {
		this.innerItem = innerItem;
	}
	
	
}
