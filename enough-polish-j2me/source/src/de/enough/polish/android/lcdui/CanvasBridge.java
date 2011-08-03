//#condition polish.usePolishGui && polish.android
/*
 * Copyright (c) 2009 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */

package de.enough.polish.android.lcdui;

import de.enough.polish.android.midlet.MidletBridge;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import de.enough.polish.ui.Screen;
import android.content.Context;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;

/**
 * @author robertvirkus
 *
 */
public class CanvasBridge 
extends View
implements OnTouchListener //, OnKeyListener
{
	
	//#if polish.useFullScreen
		//#define tmp.fullScreen
	//#endif


	private Canvas lcduiCanvas;
	private DisplayUtil util;
	private Graphics graphics;
	private int availableWidth = 100;
	private int availableHeight = 100;
	private static CanvasBridge current;

	/**
	 * @param context
	 */
	public CanvasBridge(Context context) {
		super(context);
		setFocusable(true);
		setFocusableInTouchMode(true);
	}
	
	public void setCanvas( Canvas canvas ) {
		this.lcduiCanvas = canvas;
		canvas._setBridge(this);
		setOnTouchListener(this);
		//setOnKeyListener( this );
	}

	@Override
	protected void onMeasure(int width, int height) {
		//TODO specify screen width and height
		//super.onMeasure(width, height);
		setMeasuredDimension(this.availableWidth, this.availableHeight);
		System.out.println("CanvasBridge: onMeasure: " + width + "x" + height);
	}

	

	/* (non-Javadoc)
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	protected void onDraw(android.graphics.Canvas androidCanvas) {
		if(this.lcduiCanvas != null)
		{
			if(this.graphics == null)
			{
				this.graphics = new Graphics(androidCanvas);
			}
			
			try {
				//try { throw new RuntimeException("draw canvas:  " + this.lcduiCanvas); } catch (Exception e) { e.printStackTrace(); }
				//System.out.println("Drawing canvas " + this.lcduiCanvas + " (" + System.currentTimeMillis() + ")");
				this.lcduiCanvas.paint(this.graphics);
			} catch (Exception e) {
				//#debug error
				System.out.println("Error: unable to paint screen: " + this.lcduiCanvas + ", dimension=" + this.lcduiCanvas.getWidth() + "x" + this.lcduiCanvas.getHeight() + ", isShown=" + this.lcduiCanvas.isShown() + e );
			}
		}
		AndroidDisplay.getInstance().callSeriallies();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// #debug
		System.out.println("onSizeChanged with width '"+w+"' and height '"+h+"'");
		this.availableWidth = w;
		this.availableHeight = h;
		super.onSizeChanged(w, h, oldw, oldh);
		if(this.lcduiCanvas != null) {
			this.lcduiCanvas.sizeChanged(w,h);
		}
		MidletBridge.instance.onSizeChanged(w, h);
	}
	

	private Screen getCurrentScreen() {
		Display display = Display.getInstance();
		if (display == null) {
			return null;
		}
		Displayable disp = display.getCurrent();
		if (disp == null || (!(disp instanceof Screen))) {
			return null;
		}
		return (Screen) disp;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// #debug
		System.out.println("CanvasBridge.onKeyDown: keyCode=" + keyCode + ", flags=" + event.getFlags() + ", action=" + event.getAction() + ", isFromSoftKeyBoard=" + ((event.getFlags() & KeyEvent.FLAG_SOFT_KEYBOARD) == KeyEvent.FLAG_SOFT_KEYBOARD));
		if(this.lcduiCanvas == null) {
			return false;
		}
		//#if polish.android1.5
		if(keyCode == KeyEvent.KEYCODE_ENTER && ((event.getFlags() & KeyEvent.FLAG_SOFT_KEYBOARD) == KeyEvent.FLAG_SOFT_KEYBOARD)) {
			//#debug
			System.out.println("Hiding Softkeyboard in onKeyUp");
			return true;
		}
		//#endif
		if(this.util == null)
		{
			this.util = new DisplayUtil(event.getDeviceId());
		}
		
		int key = this.util.handleKey(keyCode, event, this.lcduiCanvas);
		//#debug
		System.out.println("onKeyDown:converted android key code '" + keyCode+"' to ME code '"+key+"'");
		
		this.lcduiCanvas.keyPressed(key);
		//#if !tmp.fullScreen
			Screen screen = getCurrentScreen();
			if ((screen == null) || (!screen.keyPressedProcessed)) {			
				if (keyCode == KeyEvent.KEYCODE_MENU) { // && this.addedCommandMenuItemBridges.size() > 0) {
					return false;
				}
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					return MidletBridge.instance.onBack();
				}
				if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER ) {
					return MidletBridge.instance.onOK();
				}
			}
		//#endif
		return true;
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(this.lcduiCanvas == null) {
			return false;
		}
//		//#if polish.android1.5
//		if(keyCode == KeyEvent.KEYCODE_ENTER && ((event.getFlags() & KeyEvent.FLAG_SOFT_KEYBOARD) == KeyEvent.FLAG_SOFT_KEYBOARD)) {
//			//#debug
//			System.out.println("Hiding Softkeyboard");
//			//MidletBridge.instance.hideSoftKeyboard();
//			return true;
//		}
//		//#endif
		if(this.util == null)
		{
			this.util = new DisplayUtil(event.getDeviceId());
		}
		
		int key = this.util.handleKey(keyCode, event, this.lcduiCanvas);
		
		
		//#debug
		System.out.println("onKeyUp:converted android key code '" + keyCode+"' to ME code '"+key+"'");
		this.lcduiCanvas.keyReleased(key);
		//#if !tmp.fullScreen
			if (keyCode == KeyEvent.KEYCODE_MENU) { // && this.addedCommandMenuItemBridges.size() > 0) {
				Screen screen = getCurrentScreen();
				if ((screen == null) || (!screen.keyPressedProcessed)) {			
					return false;
				}
			}
		//#endif
		
		return true;
	}
	
	
	
	@Override
	public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event)
	{
		//#debug
		System.out.println("onMultiple: key event: characters=[" + event.getCharacters() + "], number=[" + event.getNumber() + "], unicode/meta=[" + event.getUnicodeChar(event.getMetaState()) + "], isSystem=" + event.isSystem() + ",  keyCode=[" + keyCode + "/" + event.getKeyCode() + "], action=" + event.getAction() + ", repeat=" + repeatCount + ", metaState=" + event.getMetaState() + ", describeContents=" + event.describeContents() + ", flags=" + event.getFlags());
		if(this.lcduiCanvas == null) {
			return false;
		}
		if(this.util == null)
		{
			this.util = new DisplayUtil(event.getDeviceId());
		}
		
		int key = this.util.handleKey(keyCode, event, this.lcduiCanvas);
		if (repeatCount > 0) {
			this.lcduiCanvas.keyRepeated(key);
		} else {
			if (key == 0) {
				String characters = event.getCharacters();
				if (characters != null) {
					for (int i=0; i<characters.length();i++) {
						key += characters.charAt(i);
					}
				}
			}
			this.lcduiCanvas.keyPressed(key);
			this.lcduiCanvas.keyReleased(key);
		}
		return true;
		//return super.onKeyMultiple(keyCode, repeatCount, event);
	}
	
	public boolean onKey(View view, int keyCode, KeyEvent event) {
		int action = event.getAction();
		//#debug
		System.out.println("onKey for " + view + " (this=" + this + "), keyCode=" + keyCode + ", action=" + event.getAction());
		if (view != this) {
			//bringToFront();
			requestFocus();
		}
		switch (action) {
		case KeyEvent.ACTION_DOWN:
			onKeyDown(keyCode, event);
			return true;
		case KeyEvent.ACTION_UP:
			onKeyUp(keyCode, event);
			return true;
		case KeyEvent.ACTION_MULTIPLE:
			onKeyMultiple(keyCode, event.getRepeatCount(), event);
			return true;
		}
		return false;
	}
	
	public boolean onTouch(View view, MotionEvent event) {
		if(this.lcduiCanvas == null) {
			return view.onTouchEvent(event);
		}
		float x = event.getX();
		float y = event.getY();
		int truncatedX = (int)x;
		int truncatedY = (int)y;

		int action = event.getAction();
		//#debug
		System.out.println("onTouchEvent: action="+action + ", x=" + x + ", y=" + y);
		requestFocus();
		switch(action) {
			case MotionEvent.ACTION_DOWN:
				this.lcduiCanvas.pointerPressed(truncatedX,truncatedY);
				return true;
			case MotionEvent.ACTION_UP:
				this.lcduiCanvas.pointerReleased(truncatedX,truncatedY);
				return true;
			case MotionEvent.ACTION_MOVE:
				this.lcduiCanvas.pointerDragged(truncatedX,truncatedY);
				return true;
			default: return view.onTouchEvent(event);
		}
	}
	
	public void hideNotify() {
		this.lcduiCanvas._hideNotify();
	}
	
	public void showNotify() {
		current = this;
		this.lcduiCanvas._showNotify();
	}

	public int getAvailableHeight() {
		return this.availableHeight;
	}
	
	public int getAvailableWidth() {
		return this.availableWidth;
	}

	public static CanvasBridge current() {
		return current;
	}


}
