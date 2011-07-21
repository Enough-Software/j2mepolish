//#condition polish.midp || polish.android
/*
 * Created on Jan 14, 2007 at 11:54:15 AM.
 * 
 * Copyright (c) 2010 Robert Virkus / Enough Software
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
package de.enough.polish.util;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;

/**
 * <p>Manages a history of screens in a stack, when a previous screen is shown all following screens are removed from the history.</p>
 * <p>ScreenHistory is thread safe.</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Jan 14, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ScreenHistory {
	
	private final ArrayList history;
	private final int maxStep;
	
	/**
	 * Creates a new ScreenStack with no limitations on the number of possible screens between a former and a new screen.
	 * 
	 * @see #ScreenHistory(int)
	 */
	public ScreenHistory( ) {
		this( -1 );
	}

	/**
	 * Creates a new ScreenStack
	 * 
	 * @param maxStep the maximum number of screens that is checked if a new screen has been previously shown, -1 when all screens should be checked
	 */
	public ScreenHistory(int maxStep) {
		this.maxStep = maxStep;
		this.history = new ArrayList();
	}
	
	//#if polish.LibraryBuild && !polish.usePolishGui
	/**
	 * Shows the screen
	 * When the screen has been shown previously, any screens that followed the screen are removed from the history.
	 * 
	 * @param screen the screen that should be shown next
	 * @param display the display used for showing the screen
	 */
	public synchronized void show( de.enough.polish.ui.Displayable screen, de.enough.polish.ui.Display display ) {
		// ignore
	}
	//#endif

	
	/**
	 * Shows the screen
	 * When the screen has been shown previously, any screens that followed the screen are removed from the history.
	 * 
	 * @param screen the screen that should be shown next
	 * @param display the display used for showing the screen
	 */
	public synchronized void show( Displayable screen, Display display ) {
		// check for previous screen:
		Object[] internal = this.history.getInternalArray();
		int currentIndex = this.history.size() - 1;
		int maxCount = this.maxStep;
		int count = 0;
		boolean foundPrevious = false;
		while ( currentIndex >= 0 && (count < maxCount || maxCount == -1)) {
			if (screen == internal[currentIndex]) {
				for (int i = this.history.size() - 1; i > currentIndex; i--) {
					//#debug
					System.out.println("show(): Removing history screen " + i);
					this.history.remove(i);
				}
				foundPrevious = true;
				break;
			}
			currentIndex--;
			count++;
		}
		if (!foundPrevious) {
			this.history.add(screen);
		}
		//#debug
		System.out.println("show(): history.size()=" + this.history.size() );
		display.setCurrent(screen);
	}
	
	/**
	 * Determines whether there is a previous screen in this ScreenStack.
	 * 
	 * @return true when there is a previous screen
	 * @see #getPrevious()
	 * @see #showPrevious(Display)
	 */
	public synchronized boolean hasPrevious() {
		return this.history.size() > 1;
	}
	
	//#if polish.LibraryBuild && !polish.usePolishGui
		/**
		 * Shows the previous screen when there is any.
		 * 
		 * @param display the display used for showing the screen
		 * @return true when there is a previous screen
		 * @see #hasPrevious()
		 * @see #getPrevious()
		 */
		public synchronized boolean showPrevious(de.enough.polish.ui.Display display) {
			return false;
		}
	//#endif
		
	/**
	 * Shows the previous screen when there is any.
	 * 
	 * @param display the display used for showing the screen
	 * @return true when there is a previous screen
	 * @see #hasPrevious()
	 * @see #getPrevious()
	 */
	public synchronized boolean showPrevious(Display display) {
		if (this.history.size() > 1) {
			// remove current screen:
			this.history.remove( this.history.size() - 1);
			// show previous screen:
			Displayable screen = (Displayable) this.history.get( this.history.size() - 1 );
			//#debug
			System.out.println("showPrevious(): history.size()=" + this.history.size() );
			display.setCurrent(screen);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Retrieves the previous screen when there is any.
	 * The history is not changed by this operation.
	 * 
	 * @return the previous screen or null when there is no previous screen
	 * @see #hasPrevious()
	 * @see #showPrevious(Display)
	 * @see #getCurrent()
	 */
	public synchronized Displayable getPrevious() {
		if (this.history.size() > 1) {
			Displayable screen = (Displayable) this.history.get( this.history.size() - 2 );
			return screen;
		} else {
			return null;
		}
	}
	
	/**
	 * Retrieves the current screen when there is any.
	 * The history is not changed by this operation.
	 * 
	 * @return the current screen or null when there is no current screen
	 */
	public synchronized Displayable getCurrent() {
		if (this.history.size() > 0) {
			return (Displayable) this.history.get( this.history.size() - 1);
		} else {
			return null;
		}
	}
	
	/**
	 * Retrieves the size of the history managed by this stack.
	 * 
	 * @return the size of the history, 0 when there no screens
	 */
	public synchronized int getHistorySize() {
		return this.history.size();
	}
	
	/**
	 * Clears the history.
	 */
	public synchronized void clearHistory() {
		this.history.clear();
	}

	/**
	 * Removes the last history entry, if there is one
	 */
	 public synchronized void popHistory() {
         if (this.history.size()>0) {
               this.history.remove(this.history.size()-1);
         }
   }

	/**
	 * Pushes a displayable on the history without showing it.
	 * 
	 * @param displayable the displayable which should be added to this history
	 */
	public void push(Displayable displayable )
	{
		this.history.add( displayable );
	}

}
