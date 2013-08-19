//#condition polish.usePolishGui
package de.enough.polish.ui;

/**
 * An item source that creates items for all entries.
 * 
 * @author Robert Virkus, j2mepolish@enough.de
 * @see UniformItemSource
 */
public interface ItemSource
{
	/**
	 * Items have no positioning preference.
	 */
	public static final int DISTRIBUTION_PREFERENCE_NONE = 0;
	/**
	 * The top / first items should be shown by default.
	 */
	public static final int DISTRIBUTION_PREFERENCE_TOP = 1;
	/**
	 * The bottom / last items should be shown by default.
	 */
	public static final int DISTRIBUTION_PREFERENCE_BOTTOM = 2;

	/**
	 * Retrieves the number of items in this source.
	 * @return the number
	 */
	int countItems();
	
	/**
	 * Creates and populates an item for the specific index 
	 * @param index the index between 0 (including) and the countItems() (excluding) 
	 * @return the created item
	 */
	Item createItem(int index);
	
	/**
	 * Allows to register an ItemConsumer with this source. 
	 * The source is required to inform the consumer when there are changes in the number, sorting or content of the items
	 * @param consumer the consumer
	 */
	void setItemConsumer( ItemConsumer consumer );
	
	/**
	 * Retrieves the distribution preference for this source.
	 * @return the preference
	 * @see #DISTRIBUTION_PREFERENCE_BOTTOM
	 * @see #DISTRIBUTION_PREFERENCE_TOP
	 * @see #DISTRIBUTION_PREFERENCE_NONE
	 */
	int getDistributionPreference();
	
	/**
	 * Retrieves the item for notifying the user that there is currently no entry in the list. 
	 * @return the item or null, when no additional information should be shown to the user.
	 */
	Item getEmptyItem();

}
