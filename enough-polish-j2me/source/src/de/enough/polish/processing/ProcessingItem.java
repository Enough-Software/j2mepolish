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

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.Command;
import javax.microedition.lcdui.Graphics;

/**
 * Implements a ProcessingItem.
 *
 * @author Ovidiu
 */
public class ProcessingItem extends Item implements ProcessingContextContainerInterface {

    protected ProcessingInterface context ;
    protected Command cmd = new Command ( "" , Command.ITEM, 0);
    protected String softkeyCommandText = null ;


    /**
     * Create a ProcessingItem based on the specified context.
     * @param context
     */
    public ProcessingItem( ProcessingInterface context)
    {
        super();

        // Disable item image caching.
        
        //#if polish.css.filter
        cacheItemImage = false ;
        //#endif

        this.context = context;
        this.context.setParent(this);
        this.appearanceMode = INTERACTIVE;
        context.signalInitialization();
        
    }

    /**
     * Checks if a given pixel (relative to the item) is within the bounds
     * of the Processing canvas.
     * @param x
     * @param y
     * @return true if the pixel is within bounds, false otherwise
     */
    protected boolean isWithinBounds(int x, int y)
    {
        if ( (x<paddingLeft) || (x>itemWidth-paddingRight) ||
              (y<paddingTop) || (y>itemHeight-paddingBottom) )
        {
            return false;
        }
        return true;
    }

   
    /* (non-Javadoc)
     * @see de.enough.polish.ui.Item#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
     */
    public void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {

		setContentWidth ( itemWidth - paddingLeft - paddingRight );
		setContentHeight ( itemHeight - paddingTop - paddingBottom );
		
		// Resize the Processing canvas area if needed.
		context.signalSizeChange( contentWidth , contentHeight );
		
		// Draw the processing buffer.
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
     * @see de.enough.polish.ui.Item#destroy()
     */
    public void destroy()
    {
         context.signalDestroy();
         super.destroy();
    }

    /* (non-Javadoc)
     * @see de.enough.polish.ui.Item#defocus(de.enough.polish.ui.Style)
     */
    protected void defocus(Style style)
    {
        // Remove the Item command (if any)
        removeCommand( cmd);
        cmd = null;
        
        context.lostFocus();
        super.defocus(style);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.ui.Item#focus(de.enough.polish.ui.Style, int)
     */
    protected Style focus(Style style, int direction)
    {
        // If requested by the Processing code, add an Item command
        if ( softkeyCommandText != null )
        {
            cmd = new Command ( softkeyCommandText, Command.ITEM, 0);
            addCommand(cmd);
        }

        context.focus();
        return super.focus(style, direction);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.ui.Item#handleCommand(de.enough.polish.ui.Command)
     */
    protected boolean handleCommand(Command cmd)
    {
        context.signalSoftkeyPressed(cmd.getLabel());
        return context.areSoftkeysCaptured() || super.handleCommand(cmd);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.ui.Item#initContent(int, int, int)
     */
    protected void initContent(int firstLineWidth, int availWidth, int availHeight) {

        setContentWidth ( availWidth );
        setContentHeight ( availHeight );

    }
    
    /* (non-Javadoc)
     * @see de.enough.polish.ui.Item#handleKeyPressed(int, int)
     */
    protected boolean handleKeyPressed( int keyCode, int gameAction ) {
        context.signalKeyPressed(keyCode);
        return context.areKeypressesCaptured();
        
    }

    /* (non-Javadoc)
     * @see de.enough.polish.ui.Item#handleKeyReleased(int, int)
     */
    protected boolean handleKeyReleased( int keyCode, int gameAction ) {

        context.signalKeyReleased(keyCode);
        return context.areKeypressesCaptured();
    }

    /* (non-Javadoc)
     * @see de.enough.polish.ui.Item#handleKeyRepeated(int, int)
     */
    protected boolean handleKeyRepeated( int keyCode, int gameAction ) {

        context.signalKeyPressed(keyCode);
        return context.areKeypressesCaptured();
        
    }

    /* (non-Javadoc)
     * @see de.enough.polish.ui.Item#handlePointerPressed(int, int)
     */
    protected boolean handlePointerPressed(int x, int y)
    {
        if ( ! isWithinBounds(x, y) )
        {
            return false;
        }
        x -= paddingLeft;
        y -= paddingTop;
        context.signalPointerPressed(x, y);
        return context.arePointerEventsCaptured();
    }

    /* (non-Javadoc)
     * @see de.enough.polish.ui.Item#handlePointerReleased(int, int)
     */
    protected boolean handlePointerReleased(int x, int y)
    {
        if ( ! isWithinBounds(x, y) )
        {
            return context.arePointerEventsCaptured();
        }
        x -= paddingLeft;
        y -= paddingTop;
        context.signalPointerReleased(x, y);
        return context.arePointerEventsCaptured();
    }

    /* (non-Javadoc)
     * @see de.enough.polish.ui.Item#handlePointerDragged(int, int)
     */
    protected boolean handlePointerDragged(int x, int y)
    {
        if ( ! isWithinBounds(x, y) )
        {
            return context.arePointerEventsCaptured();
        }
        x -= paddingLeft;
        y -= paddingTop;
        context.signalPointerDragged(x, y);
        return context.arePointerEventsCaptured();
    }

    /* (non-Javadoc)
     * @see de.enough.polish.ui.Item#handlePointerTouchDown(int, int)
     */
    public boolean handlePointerTouchDown(int x, int y)
    {
        if ( ! isWithinBounds(x, y) )
        {
            return context.arePointerEventsCaptured();
        }
        x -= paddingLeft;
        y -= paddingTop;
        context.signalPointerPressed(x, y);
        return context.arePointerEventsCaptured();
    }

    /* (non-Javadoc)
     * @see de.enough.polish.ui.Item#handlePointerTouchUp(int, int)
     */
    public boolean handlePointerTouchUp(int x, int y)
    {
        if ( ! isWithinBounds(x, y) )
        {
            return context.arePointerEventsCaptured();
        }
        x -= paddingLeft;
        y -= paddingTop;
        context.signalPointerReleased(x, y);
        return context.arePointerEventsCaptured();
    }

    /* (non-Javadoc)
     * @see de.enough.polish.ui.Item#createCssSelector()
     */
    protected String createCssSelector() {
            return "processing";
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingContextContainerInterface#processingRequestRepaint()
     */
    public void processingRequestRepaint() 
    {
        // An explicit refresh has been requested by Processing
        repaint(0,0,itemWidth,itemHeight);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingContextContainerInterface#setSoftkey(java.lang.String)
     */
    public void setSoftkey(String text)
    {
        softkeyCommandText = text ;
        
        // If requested by the Processing code, add an Item command
        if ( softkeyCommandText != null )
        {
            removeCommand(cmd);
            cmd = new Command ( softkeyCommandText, Command.ITEM, 0);
            addCommand(cmd);
        }
        else
        {
            removeCommand(cmd);
        }
    }

}
