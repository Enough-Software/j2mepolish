//#condition polish.usePolishGui
package de.enough.polish.ui;

/**
 * Defines possible interactions with scrollable UI elements
 * @author Robert Virkus
 *
 */
public interface Scrollable extends UiElement
{ 
	/**
	 * Sets the scrolling offset
	 * @param scroller the used scroller
	 * @param offset the current offset
	 */
	void setScrollOffset( Scroller scroller, int offset);
}
