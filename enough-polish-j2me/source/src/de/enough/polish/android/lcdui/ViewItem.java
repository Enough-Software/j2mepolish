//#condition polish.usePolishGui && polish.android
package de.enough.polish.android.lcdui;

import javax.microedition.lcdui.Graphics;

import android.view.View;
import android.view.View.MeasureSpec;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;

/**
 * <p>ViewItem allows you to embed native Android views in your J2ME Polish UI</p>
 * <p>Usage example:</p>
 * <pre>
 * 
 * //#if polish.android
 *    import android.widget.AnalogClock;
 *    import de.enough.polish.android.lcdui.ViewItem;
 *    import de.enough.polish.android.midlet.MidletBridge;
 * //#endif
 * public MyForm extends Form {
 *    public MyForm() {
 *       //#style myform
 *       super(null);
 *       append("Hello J2ME Polish world");
 *       //#if polish.android
 *         AnalogClock view = new AnalogClock( MidletBridge.getInstance() );
 *	       ViewItem viewItem = new ViewItem( view );
 *	 	   append( viewItem );
 *	    //#endif
 *    }
 * }
 * </pre>
 * 
 * <p>copyright (c) Enough Software 2011</p>
 * @author Robert Virkus
 *
 */
public class ViewItem 
extends Item
implements View.OnFocusChangeListener
{
	
	/**
	 * Creates a new ViewItem
	 * @param nativeView the native Android view that should be displayed as content
	 */
	public ViewItem( View nativeView ) {
		this( nativeView, null);
	}
	
	/**
	 * Creates a new ViewItem
	 * @param nativeView the native Android view that should be displayed as content
	 * @param style the J2ME Polish style associated with this item
	 */
	public ViewItem( View nativeView, Style style ) {
		super( style );
		this._androidView = nativeView;
		nativeView.setOnFocusChangeListener(this);
	}


	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Item#initContent(int, int, int)
	 */
	protected void initContent(int firstLineWidth, int availWidth, int availHeight) {
		View nativeView = this._androidView;
		nativeView.requestLayout();
		nativeView.measure(
				MeasureSpec.makeMeasureSpec(availWidth, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(availHeight, MeasureSpec.AT_MOST)
		);
		this.contentWidth = nativeView.getMeasuredWidth();
		this.contentHeight = nativeView.getMeasuredHeight();
		this.appearanceMode = INTERACTIVE; //TODO not all views are interactive...
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		// leave empty as the content is rendered by the native view
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Item#createCssSelector()
	 */
	protected String createCssSelector() {
		return "native";
	}

	/*
	 * (non-Javadoc)
	 * @see android.view.View.OnFocusChangeListener#onFocusChange(android.view.View, boolean)
	 */
	public void onFocusChange(View view, boolean receivedFocus) {
		//#debug
		System.out.println("ViewItem.onFocusChange for " + view + ", receivedFocus=" + receivedFocus + ", item.isFocused()=" + isFocused() + ", screen=" + getScreen());
		if (receivedFocus) {
			if (!isFocused()) {
				getScreen().focus(this);
			}
		}
		
	}

}
