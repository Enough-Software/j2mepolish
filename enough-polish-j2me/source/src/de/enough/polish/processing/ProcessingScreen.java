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

import de.enough.polish.ui.Command;
import javax.microedition.lcdui.Graphics;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.Style;

/**
 * Implements a ProcessingScreen
 *
 * @author Ovidiu Iliescu
 */
public class ProcessingScreen extends Screen implements ProcessingContextContainerInterface {

    protected Command cmd = new Command("", Command.ITEM, 0);
    protected String softkeyCommandText = null;
    protected ProcessingInterface context = null;
    protected boolean focusHasBeenTriggered = false ;

    /**
     * Initializes the context
     */
    public void initProcessingContext() {
        context.signalInitialization();
        context.setParent(this);
        
    }

    /**
     * Creates a new ProcessingScreen based on the specified ProcessingContext.
     *
     * @param title the screen's title
     * @param context the context
     */
    public ProcessingScreen(String title, ProcessingInterface context) {
        super(title, null, true);
        this.context = context;
        initProcessingContext();
    }

    /**
     * Creates a new ProcessingScreen based on the specified ProcessingContext, with the given style.
     *
     * @param title the screen's title
     * @param context the context
     * @param style the style to use
     */
    public ProcessingScreen(String title, ProcessingInterface context, Style style) {
        super(title, style, true);
        this.context = context;
        initProcessingContext();
    }

    /**
     * Checks if a given pixel (relative to the item) is within the bounds
     * of the Processing canvas.
     * @param x
     * @param y
     * @return true if the pixel is within bounds, false otherwise
     */
    protected boolean isWithinBounds(int x, int y) {
        if ((x < contentX) || (x > contentWidth + contentX) || (y < contentY) || (y > contentHeight + contentY))
        {
            return false;
        }
        return true;

    }

    /* (non-Javadoc)
     * @see de.enough.polish.ui.Screen#paintScreen(javax.microedition.lcdui.Graphics)
     */
    protected void paintScreen(Graphics g) {

		context.signalSizeChange(contentWidth, contentHeight);
		
		if ( focusHasBeenTriggered == false )
		{
		    context.signalHasFocus();
		    focusHasBeenTriggered = true ;
		}
		
		// Draw the processing buffer
		if ( context.isDrawingTransparent() == false )
		{
			g.drawImage(context.getBuffer(), contentX, contentY, Graphics.TOP | Graphics.LEFT );
		}
		else
		{
			//#if polish.midp2
			context.getTransparentRgbImage().paint(contentX, contentY, g);
			//#endif
		}
    }

    /* (non-Javadoc)
     * @see de.enough.polish.ui.Screen#handleCommand(de.enough.polish.ui.Command)
     */
    protected boolean handleCommand(Command cmd)
    {
        context.signalSoftkeyPressed(cmd.getLabel());
        return context.areSoftkeysCaptured() || super.handleCommand(cmd);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.ui.Screen#handleKeyPressed(int, int) 
     */
    protected boolean handleKeyPressed(int keyCode, int gameAction) {

        context.signalKeyPressed(keyCode);
        return context.areKeypressesCaptured();

    }

    /* (non-Javadoc)
     * @see de.enough.polish.ui.Screen#handleKeyReleased(int, int) 
     */
    protected boolean handleKeyReleased(int keyCode, int gameAction) {

        context.signalKeyReleased(keyCode);
        return context.areKeypressesCaptured();
    }

    /* (non-Javadoc)
     * @see de.enough.polish.ui.Screen#handleKeyRepeated(int, int) 
     */
    protected boolean handleKeyRepeated(int keyCode, int gameAction) {

        context.signalKeyPressed(keyCode);
        return context.areKeypressesCaptured();

    }

    /* (non-Javadoc)
     * @see de.enough.polish.ui.Screen#handlePointerPressed(int, int) 
     */
    protected boolean handlePointerPressed(int x, int y) {
        if (!isWithinBounds(x, y))
        {
            return context.arePointerEventsCaptured();
        }
        x -= contentX;
        y -= contentY;
        context.signalPointerPressed(x, y);
        return context.arePointerEventsCaptured();
    }

    /* (non-Javadoc)
     * @see de.enough.polish.ui.Screen#handlePointerReleased(int, int) 
     */
    protected boolean handlePointerReleased(int x, int y) {
        if (!isWithinBounds(x, y))
        {
            return false;
        }
        x -= contentX;
        y -= contentY;
        context.signalPointerReleased(x, y);
        return context.arePointerEventsCaptured();
    }

    /* (non-Javadoc)
     * @see de.enough.polish.ui.Screen#handlePointerDragged(int, int) 
     */
    protected boolean handlePointerDragged(int x, int y) {
        if (!isWithinBounds(x, y))
        {
            return context.arePointerEventsCaptured();
        }
        x -= contentX;
        y -= contentY;
        context.signalPointerDragged(x, y);
        return context.arePointerEventsCaptured();
    }

    /* (non-Javadoc)
     * @see de.enough.polish.ui.Screen#handlePointerTouchDown(int, int) 
     */
    public boolean handlePointerTouchDown(int x, int y) {
        if (!isWithinBounds(x, y))
        {
            return context.arePointerEventsCaptured();
        }
        x -= contentX;
        y -= contentY;
        context.signalPointerPressed(x, y);
        return context.arePointerEventsCaptured();
    }

    /* (non-Javadoc)
     * @see de.enough.polish.ui.Screen#handlePointerTouchUp(int, int)
     */
    public boolean handlePointerTouchUp(int x, int y) {
        if (!isWithinBounds(x, y))
        {
            return context.arePointerEventsCaptured();
        }
        x -= contentX;
        y -= contentY;
        context.signalPointerReleased(x, y);
        return context.arePointerEventsCaptured();
    }

    /* (non-Javadoc)
     * @see de.enough.polish.ui.Screen#createCssSelector() 
     */
    protected String createCssSelector() {
        return "processing";
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingContextContainerInterface#processingRequestRepaint() 
     */
    public void processingRequestRepaint() {

        repaint();
    }

    /* (non-Javadoc)
     * @see de.enough.polish.ui.Screen#hideNotify() 
     */
    public void hideNotify() {
        context.signalLostFocus();
        focusHasBeenTriggered = false ;
        super.hideNotify();
    }

    /* (non-Javadoc)
     * @see de.enough.polish.ui.Screen#releaseResources() 
     */
    public void releaseResources() {
        context.signalDestroy();
        super.releaseResources();
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingContextContainerInterface#setSoftkey(java.lang.String) 
     */
    public void setSoftkey(String text) {
        softkeyCommandText = text;

        // If requested by the Processing code, add an Item command
        if (softkeyCommandText != null)
        {
            removeCommand(cmd);
            cmd = new Command(softkeyCommandText, Command.ITEM, 0);
            addCommand(cmd);
        }
        else
        {
            removeCommand(cmd);
        }

    }
    
}
