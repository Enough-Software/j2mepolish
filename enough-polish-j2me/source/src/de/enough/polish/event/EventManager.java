/*
 * Created on Sep 15, 2007 at 3:46:41 AM.
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
package de.enough.polish.event;

import java.util.Hashtable;

import de.enough.polish.util.ArrayList;

/**
 * <p>Manages events and forwards them to appropriate listeners</p>
 *
 * <p>Copyright Enough Software 2007 - 2010</p>
 * <pre>
 * history
 *        Sep 15, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class EventManager {
	
	/** name of the pressed state event **/
	public static final String EVENT_PRESS = "press";
	/** name of the pressed state event **/
	public static final String EVENT_UNPRESS = "unpress";
	/** name of the focussed state event **/
	public static final String EVENT_FOCUS = "focus";
	/** name of the unfocussed state event **/
	public static final String EVENT_DEFOCUS = "defocus";
	/** name of the show notify event **/
	public static final String EVENT_SHOW = "show";
	/** name of the show notify event when a UI element is shown for the first time **/
	public static final String EVENT_SHOW_FIRST_TIME = "show-first";
	/** name of the hide notify event **/
	public static final String EVENT_HIDE = "hide";
	/** name of the event when a value of an item is changed **/
	public static final String EVENT_VALUE_CHANGE = "value-change";
	/** event when a commands menu is clpsed */
	public static final String EVENT_MENU_CLOSE =  "menu-close";
	/** event when a commands menu is opened */
	public static final String EVENT_MENU_OPEN =  "menu-open";
	/** event when an item has been visited */
	public static final String EVENT_VISIT = "visit";
	/** event when the visited stated of an item is rolled back */
	public static final String EVENT_UNVISIT = "unvisit";
//	/** event when the recognized HOLD gesture was actually handled */
//	public static final String EVENT_GESTURE_HOLD_HANDLED = "hold-handled";
	/** event when a pointer press or release event was handled */
	public static final String EVENT_POINTER_HANDLED = "touch-handled";
	/** event when a key press or release event was handled */
	public static final String EVENT_KEY_HANDLED = "key-handled";
	
	
	private static EventManager INSTANCE = new EventManager();
	private final Hashtable eventListenersByEvent;
	private final ArrayList generalPurposeListeners;
	private Hashtable remappedEventsByName;
	
	private EventManager() {
		this.eventListenersByEvent = new Hashtable();
		this.generalPurposeListeners = new ArrayList();
	}
	
	/**
	 * Retrieves the instance of the event manager (singleton pattern).
	 * 
	 * @return the instance of the event manager (singleton pattern).
	 */
	public static EventManager getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Allows users to create a new EventManager instance.
	 * This might be used to process a completely set of different events and listeners in a separate manner.
	 * 
	 * @return a new EventManager
	 */
	public static EventManager createNewInstance() {
		return new EventManager();
	}
	
	/**
	 * Forwards the specified event to any listeners.
	 * 
	 * @param name the name of the event, e.g. EVENT_PRESS
	 * @param source the source of the event, for example an item
	 * @param data additional optional data, depends on the event - might be null!
	 */
	public static void fireEvent( String name, Object source, Object data ) {
		INSTANCE.fireEventImpl(name, source, data);
		if (INSTANCE.remappedEventsByName != null) {
			Object[] sources = (Object[]) INSTANCE.remappedEventsByName.get(name);
			if (sources != null) {
				for (int i = 0; i < sources.length; i++)
				{
					Object altSource = sources[i];
					//#debug
					System.out.println("remapping event " + name + " to " + altSource);
					INSTANCE.fireEventImpl(name, altSource, data);
				}
			}
		}
	}

	
	/**
	 * Forwards the specified event to any listeners.
	 * 
	 * @param name the name of the event, e.g. EVENT_PRESS
	 * @param source the source of the event, for example an item
	 * @param data additional optional data, depends on the event - might be null!
	 */
	private void fireEventImpl( String name, Object source, Object data ) {
		Object[] generalListeners = this.generalPurposeListeners.getInternalArray();
		for (int i = 0; i < generalListeners.length; i++)
		{
			EventListener listener = (EventListener) generalListeners[i];
			if (listener == null) {
				break;
			}
			try {
				listener.handleEvent(name, source, data);
			} catch (Exception e) {
				//#debug error
				System.out.println("Unable to forward event " + name + " to " + listener + e );				
			}
		}
		EventListener[] listeners = (EventListener[]) this.eventListenersByEvent.get( name );
		if (listeners != null) {
			for (int i = 0; i < listeners.length; i++) {
				EventListener listener = listeners[i];
				try {
					listener.handleEvent(name, source, data);
				} catch (Exception e) {
					//#debug error
					System.out.println("Unable to forward event " + name + " to " + listener + e );
				}
			}
		}
	}

	

	/**
	 * Adds a event listener.
	 * 
	 * @param eventName the name of events that the listener is interested in, or null when all events should be passed to the listener
	 * @param listener the listener
	 * @throws NullPointerException  when the listener is null
	 */
	public void addEventListener( String eventName, EventListener listener ) {
		if (eventName == null) {
			this.generalPurposeListeners.add(listener);
		} else {
			EventListener[] listeners = (EventListener[]) this.eventListenersByEvent.get( eventName );
			if (listeners == null) {
				this.eventListenersByEvent.put( eventName, new EventListener[]{ listener } );
			} else {
				EventListener[] newListeners = new EventListener[ listeners.length + 1 ];
				System.arraycopy( listeners, 0, newListeners, 0, listeners.length );
				newListeners[ listeners.length ] = listener;
				this.eventListenersByEvent.put( eventName, newListeners );
			}
		}
		
	}

	/**
	 * Remaps the named event to the specified alternative source.
	 * This can be used for triggering animations in UI elements that did not
	 * trigger the named event themselves. Note that the events should be of a unique
	 * nature.
	 * 
	 * @param eventName the name of the event, usually this is a custom event name 
	 * @param alternativeSource the source that should also seem to fire this event
	 * @see #removeAllRemappings()
	 * @see #removeRemapEvent(String)
	 */
	public void remapEvent(String eventName, Object alternativeSource)
	{
		if (this.remappedEventsByName == null) {
			this.remappedEventsByName = new Hashtable();
		}
		Object[] sources = (Object[]) this.remappedEventsByName.get(eventName);
		if (sources == null) {
			this.remappedEventsByName.put(eventName, new Object[]{ alternativeSource });
		} else {
			Object[] newSources = new Object[ sources.length + 1 ];
			System.arraycopy( sources, 0, newSources, 0, sources.length );
			newSources[ sources.length ] = alternativeSource;
			this.remappedEventsByName.put(eventName, newSources);
		}
		
	}
	
	/**
	 * Removes all remappings for the specified event
	 * @param eventName the name of the event that was remapped
	 * @see #remapEvent(String, Object)
	 */
	public void removeRemapEvent(String eventName) {
		if (this.remappedEventsByName == null) {
			return;
		}
		this.remappedEventsByName.remove(eventName);
	}
	

	/**
	 * Removes a single remapping for the specified event
	 * @param eventName the name of the event that was remapped
	 * @param alternativeSource the source that should also seem to fire this event
	 * @see #remapEvent(String, Object)
	 */
	public void removeRemapEvent(String eventName, Object alternativeSource) {
		if (this.remappedEventsByName == null) {
			return;
		}
		Object[] sources = (Object[]) this.remappedEventsByName.remove(eventName);
		if (sources == null) {
			return;
		}
		if (sources.length == 1) {
			return;
		}
		Object[] altSources = new Object[ sources.length - 1];
		int fillIndex = 0;
		for (int i = 0; i < sources.length; i++)
		{
			Object source = sources[i];
			if (source != alternativeSource) {
				altSources[i] = source;
				fillIndex++;
			}
		}
		this.remappedEventsByName.put( eventName, altSources);
		
	}

	/**
	 * Removes all remappings
	 */
	public void removeAllRemappings()
	{
		this.remappedEventsByName = null;
	}
	
	

}
