//#condition polish.usePolishGui && polish.hasFloatingPoint
package de.enough.polish.maps;

import de.enough.polish.ui.MapItem;

/**
 * Defines an event listener for {@link PoiItem}s
 * @author Ovidiu Iliescu
 */
public interface PoiItemListener {
	
	/**
	 * This event is fired when a {@link PoiItem} is activated
	 * @param item the item that was activated
	 * @param map the associated {@link MapItem}
	 */
	public void onActivated(PoiItem item, MapItem map);

}
