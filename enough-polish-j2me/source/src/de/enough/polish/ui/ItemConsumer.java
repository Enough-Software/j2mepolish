//#condition polish.usePolishGui
package de.enough.polish.ui;

/**
 * Used for informing consumers about changed items
 * @author Robert Virkus, j2mepolish@enough.de
 * 
 * @see ItemSource#setItemConsumer(ItemConsumer)
 */
public interface ItemConsumer
{
	/**
	 * Used to inform a consumer about changed items
	 * @param event the event with details, it can be null - in that case the consumer will just re-initialize completely
	 */
	public void onItemsChanged( ItemChangedEvent event );

}
