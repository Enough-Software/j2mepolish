//#condition polish.TextField.useVirtualKeyboard
package de.enough.polish.ui.keyboard;

import de.enough.polish.util.Comparator;

/**
 * A key comparator to sort KeyItems by their specified position
 * @author Andre
 *
 */
public class KeyComparator implements Comparator{

	/* (non-Javadoc)
	 * @see de.enough.polish.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object firstObject, Object secondObject) {
		KeyItem first = (KeyItem)firstObject;
		
		if(secondObject == null) 
		{
			return 0; 
		}
		
		KeyItem second = (KeyItem)secondObject;
		
		if(first.getRow() < second.getRow()) {
			return -1;
		} else if(first.getRow() > second.getRow()) {
			return 1;
		} else {
			if(first.getIndex() < second.getIndex()) {
				return -1;
			} else if(first.getIndex() > second.getIndex()) {
				return 1;
			} else {
				return 0;
			} 
		} 
	}
}
