//#condition polish.usePolishGui
package de.enough.polish.ui;

/**
 * An item source that reuses items for all entries. All items are homogeneous (uniform), meaning that they use the same class and have the same dimension / height.
 * A good example is an addressbook overview in which every contact uses the same space.
 * 
 * @author Robert Virkus, j2mepolish@enough.de
 * @see UniformContainer
 * @see UniformForm
 */
public interface UniformItemSource 
extends ItemSource
{
	/**
	 * Reuses a previously created item. It sets all necessary values.
	 * @param itemIndex the index for the item
	 * @param item the previously created item
	 */
	void populateItem( int itemIndex, Item item );
}
