//#condition polish.midp || polish.usePolishGui
/*
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

package de.enough.polish.processing;

/**
 * Implements Mobile Processing events. Each event has a specific code and a specific set of parameteres, as follows :
 * <p>
 * <ul>
 * <li>EVENT_DRAW : no parameters
 * <li>EVENT_SETUP: no parameters
 * <li>EVENT_HAS_FOCUS and EVENT_LOST_FOCUS : no parameters
 * <li>EVENT_POINTER_PRESSED + EVENT_POINTER_DRAGGED + EVENT_POINTER RELEASED : x and y position of the pointer
 * <li>EVENT_KEY_PRESSED and EVENT_KEY_RELEASED : key (as char) and keycode of the key in question
 * <li>EVENT_SOFTKEY_PRESSED : the label (as String) of the softkey that has been pressed
 * <li>EVENT_APP_SUSPEND : no parameters
 * <li>EVENT_APP_RESUME : no parameters
 * <li>EVENT_APP_DESTROY: no parameters
 * <li>EVENT_INIT : no parameters
 * </ul>
 *
 * @author Ovidiu Iliescu
 */
public class ProcessingEvent {

    public static final int EVENT_DRAW = 0 ;
    public static final int EVENT_SETUP = 1 ;
    public static final int EVENT_HAS_FOCUS = 2 ;
    public static final int EVENT_LOST_FOCUS = 3 ;
    public static final int EVENT_POINTER_PRESSED = 4 ;
    public static final int EVENT_POINTER_DRAGGED = 5 ;
    public static final int EVENT_POINTER_RELEASED = 6 ;
    public static final int EVENT_KEY_PRESSED = 7 ;
    public static final int EVENT_KEY_RELEASED = 8 ;
    public static final int EVENT_SOFTKEY_PRESSED = 9 ;
    public static final int EVENT_APP_SUSPEND = 10 ;
    public static final int EVENT_APP_RESUME = 11 ;
    public static final int EVENT_APP_DESTROY = 12 ;
    public static final int EVENT_INIT = 13 ;

    public ProcessingInterface object;
    public int eventType;
    public int param1;
    public int param2;
    public String strParam1 = null;


    /**
     * Create a new ProcessingEvent of the specified type with the specified parameter.
     * @param object the Processing object
     * @param eventType the event type
     */
    public ProcessingEvent(ProcessingInterface object, int eventType)
    {
        this ( object, eventType,0,0);
    }

    /**
     * Create a new ProcessingEvent of the specified type with the specified parameter.
     * @param object the Processing object
     * @param eventType the event type
     * @param param1 the parameter value
     */
    public ProcessingEvent(ProcessingInterface object, int eventType, String param1)
    {
        this ( object, eventType,0,0);
        this.strParam1 = param1;
    }

    /**
     * Create a new ProcessingEvent of the specified type with the specified parameters.
     * @param object the Processing object
     * @param eventType the event type
     * @param param1 the value of the first parameter
     * @param param2 the value of the second parameter
     */
    public ProcessingEvent(ProcessingInterface object, int eventType, int param1, int param2)
    {
        this.object = object;
        this.eventType = eventType ;
        this.param1 = param1;
        this.param2 = param2;
    }

}
