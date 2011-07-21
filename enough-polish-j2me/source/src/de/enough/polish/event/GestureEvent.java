/*
 * Created on Dec 16, 2009 at 7:31:46 PM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package de.enough.polish.event;

/**
 * <p>Represents an recognized gesture.</p>
 *
 * <p>Copyright Enough Software 2009</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class GestureEvent extends PointerEvent {
	
	/** The 'hold' touch gesture is triggered when the user holds an item for more than 500 milliseconds */
	public static final int GESTURE_HOLD = 1;
	/** The 'swipe-left' touch gesture is triggered when the user swipes on the screen to the left side */
	public static final int GESTURE_SWIPE_LEFT = 2;
	/** The 'swipe-right' touch gesture is triggered when the user swipes on the screen to the right side */
	public static final int GESTURE_SWIPE_RIGHT = 3;
	
	/** event name when the touch gesture 'SWIPE LEFT' was recognized */
	public static final String EVENT_GESTURE_SWIPE_LEFT = "swipe-left";
	/** event name when the touch gesture 'SWIPE RIGHT' was recognized */
	public static final String EVENT_GESTURE_SWIPE_RIGHT = "swipe-right";
	/** event name when the touch gesture 'HOLD' was recognized */
	public static final String EVENT_GESTURE_HOLD = "hold";
	/** event name when the unknown touch gesture was recognized */
	public static final String EVENT_GESTURE_UNKNOWN = "unknown";

	
	private static GestureEvent INSTANCE = new GestureEvent();
	
	private int gestureId;

	/**
	 * Creates a new gesture event
	 */
	public GestureEvent() {
		// nothing to implement
	}
	
	/**
	 * Retrieves the instance of gesture event for re-using the object.
	 * @return the instance
	 */
	public static GestureEvent getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Resets this gesture event for re-using it.
	 * @param id the ID of the gesture
	 * @param x the last pointer x position relative to the source of this event
	 * @param y the last pointer y position relative to the source of this event
	 */
	public void reset( int id, int x, int y ) {
		this.gestureId = id;
		super.reset(x, y);
	}

	/**
	 * Retrieves the ID of the recognized gesture
	 * @return the ID
	 * @see #GESTURE_HOLD
	 * @see #GESTURE_SWIPE_LEFT
	 * @see #GESTURE_SWIPE_RIGHT
	 */
	public int getGestureId() {
		return this.gestureId;
	}

	/**
	 * Retrieves the event name of the recognized gesture
	 * @return the name
	 * @see #EVENT_GESTURE_HOLD
	 * @see #EVENT_GESTURE_SWIPE_LEFT
	 * @see #EVENT_GESTURE_SWIPE_RIGHT
	 */
	public String getGestureName() {
		String name;
		switch (this.gestureId) {
		case GESTURE_HOLD: name = EVENT_GESTURE_HOLD; break;
		case GESTURE_SWIPE_LEFT: name = EVENT_GESTURE_SWIPE_LEFT; break;
		case GESTURE_SWIPE_RIGHT: name = EVENT_GESTURE_SWIPE_RIGHT; break;
		default: name = EVENT_GESTURE_UNKNOWN;
		}
		return name;
	}
	
	
}
