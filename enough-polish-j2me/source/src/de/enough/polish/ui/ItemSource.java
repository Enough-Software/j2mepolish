//#condition polish.usePolishGui
package de.enough.polish.ui;

public interface ItemSource
{
	
	int countItems();
	
	Item createItem(int index);
	
	void setItemConsumer( ItemConsumer consumer );

}
