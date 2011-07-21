//#condition polish.usePolishGui
/**
 * 
 */
package de.enough.polish.ui.containerviews;

import de.enough.polish.ui.ClippingRegion;
import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.Item;

/**
 * Animates all embedded items instead only one.
 * 
 * @author simon
 *
 */
public class AnimationContainerView extends ContainerView {


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#animate(long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(long currentTime, ClippingRegion repaintRegion)
	{
		super.animate(currentTime, repaintRegion);
		Item[] items = this.parentContainer.getItems();
		for (int i = 0; i < items.length; i++) {
			Item item = items[i];
			if (item != this.focusedItem) {
				item.animate(currentTime, repaintRegion);
			}
		}
	}

}
