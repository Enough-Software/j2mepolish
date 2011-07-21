//#condition polish.usePolishGui
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

import de.enough.polish.ui.Background;
import javax.microedition.lcdui.Graphics;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;

/**
 * Implements a Mobile Processing Background.
 *
 * @author Ovidiu Iliescu
 */
public class ProcessingBackground extends Background implements ProcessingContextContainerInterface {

    public transient ProcessingInterface context ;

    public boolean lastFocusedState = false;
    public boolean focusHasBeenInitialized = false;

    /**
     * Creates a new ProcessingBackground based on the provided context
     * @param context the Processing context to use
     */
    public ProcessingBackground ( ProcessingInterface context)
    {
        this.context = context ;
        this.context.setParent(this);
        this.borderWidth = 0;
        context.signalInitialization();
    }

    /* (non-Javadoc)
     * @see de.enough.polish.ui.Background#setParentItem(de.enough.polish.ui.Item)
     */
    public void setParentItem(Item parent)
    {
        super.setParentItem(parent);
        
        // Disable item image caching.

        //#if polish.css.filter
        parent.cacheItemImage = false ;
        //#endif
    }

    /* (non-Javadoc)
     * @see de.enough.polish.ui.Background#paint(int, int, int, int, javax.microedition.lcdui.Graphics)
     */
    public void paint(int x, int y, int width, int height, Graphics g) {

        // This part of the code deals with focus/lostfocus events.
        // The background class has no provisions for focus events
        // but based on the state of the parent item we can
        // deduce if such an event has occured.
        if ( focusHasBeenInitialized == true )
        {
            if ( parent.isFocused != lastFocusedState )
            {
                if ( parent.isFocused == true )
                {
                    lastFocusedState = true ;
                    context.signalHasFocus();
                }
                else
                {
                    lastFocusedState = false ;
                    context.signalLostFocus();
                }
            }
        }
        else
        {
                // Set the focus initialized to true
                focusHasBeenInitialized = true ;

                // Set the last focused state to the opposite of the current one
                // so that the next time paint() is called a focus/lostfocus
                // event will triggered
                lastFocusedState = ! parent.isFocused ;

                // Recursively call paint to trigger the focus/lostfocus
                // event and then graciously exit.
                paint(x, y, width, height, g);
                return;
        }

       context.signalSizeChange(width, height);

       if ( context.isDrawingTransparent() == false )
       {
          g.drawImage(context.getBuffer(), x, y, Graphics.TOP | Graphics.LEFT );
       }
       else
       {
    	  //#if polish.midp2
          context.getTransparentRgbImage().paint(x, y, g);
          //#endif
       }
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingContextContainerInterface#processingRequestRepaint()
     */
    public void processingRequestRepaint() {

        if ( parent != null )
        {
            Screen scr = parent.getScreen();

            if ( scr != null )
            {
              scr.repaint(parent.getAbsoluteX(), parent.getAbsoluteY(), parent.getBackgroundWidth(), parent.getBackgroundHeight());
            }
        }
    } 

    /* (non-Javadoc)
     * @see de.enough.polish.ui.Background#releaseResources()
     */
    public void releaseResources() {
		context.signalDestroy();
	}
    
    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingContextContainerInterface#setSoftkey(java.lang.String)
     */
    public void setSoftkey(String text)
    {
        // Do nothing
    }
    

}
