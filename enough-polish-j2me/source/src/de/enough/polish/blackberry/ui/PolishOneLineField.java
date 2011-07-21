//#condition polish.usePolishGui && polish.blackberry

/*
 * Copyright (c) 2004 Robert Virkus / Enough Software
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
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.blackberry.ui;

import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.text.TextFilter;
import de.enough.polish.ui.Style;

/**
 * 
 * <p>An EditField that uses a single line (without text wrapping)</p>
 *
 * <p>Copyright Enough Software 2011</p>
 * @author Ovidiu Iliescu
 */
public class PolishOneLineField extends VerticalFieldManager implements PolishTextField, AccessibleField {
        
    private PolishEditField editField;
    public boolean processKeyEvents = true;
    public boolean ignoreLocalSetCurrentLocation = false;
    int oldX = 0;
    int oldY = 0;
    
    /**
     * Creates a new edit field that uses a single line (without text wrapping)
     * @param label the label
     * @param text the current text
     * @param numChars the number of allowed characters
     * @param style the style
     */
    public PolishOneLineField(String label, String text, int numChars, long style) {
	    super(HORIZONTAL_SCROLL | NO_VERTICAL_SCROLL);
	    //#if polish.JavaPlatform >= BlackBerry/6.0
	    	setScrollingInertial(false);
	    //#endif
	    this.editField = new PolishEditField(label, "", numChars, style | EditField.NO_NEWLINE ) 
	    {
	        protected boolean keyChar(char key, int status, int time)
	        {
	            boolean value = super.keyChar(key,status,time);     
	            relayout();                           
	            updateScroll();
	            return value;
	        }
	        
	        public void paint ( Graphics g )
	        {
	            super.paint( g ) ;
	        }
	        
	        protected void updateScroll()
	        {
	            int strLength = getFont().getAdvance( this.getText() ) ;
	            int currentScroll = getManager().getHorizontalScroll() ;
	            int strLengthToCursor =  getFont().getAdvance( this.getText().substring(0,getCursorPosition()) );
	            int strLengthFromCursorToEnd = getFont().getAdvance( this.getText().substring(getCursorPosition(), this.getText().length() ) ) ;
	            int currentManagerWidth = getManager().getVisibleWidth();
	            int neededOffset = 0;
	            
	            int cursorMargin = 30;
	            
	            if ( strLengthToCursor >= cursorMargin && currentManagerWidth > cursorMargin * 2 )
	            {
	            	// Cursor near the end of the string
	                if ( strLengthFromCursorToEnd < currentManagerWidth && strLength > currentManagerWidth )
	                {
	                    neededOffset = strLength - currentManagerWidth + 10; // Leave some pixels so the user can see the end of the string and the cursor
	                }
	                else
	                // Cursor close to right edge of the field
	                if ( currentScroll + currentManagerWidth - cursorMargin < strLengthToCursor )
	                {
	                    neededOffset = strLengthToCursor + cursorMargin - currentManagerWidth;
	                }
	                else // Cursor close to left edge of the field
	                if ( currentScroll + cursorMargin > strLengthToCursor )
	                {
	                    neededOffset = strLengthToCursor - cursorMargin ;
	                }                    
	            }
	            
	            
	            if ( neededOffset > 0 )
	            {
	                getManager().setHorizontalScroll(neededOffset);
	            }     
	                          
	            
	        }
	        
	        public void onFocus(int direction)
	        {              
	        	PolishOneLineField.this.ignoreLocalSetCurrentLocation = false;
	            super.onFocus(direction);
	        }
	        
	    };
	    
	    add(this.editField);
    }

    /**
     * Retrieves the internally used edit field
     * @return the edit field
     */
    public PolishEditField getEditField()
    {
    	return this.editField;
    }
    
    /**
     * Sets the position of the field
     * @param x horizontal position
     * @param y vertical position
     */
    public void moveTo(int x, int y)
    {
        setPosition(x,y);
    }
    
    /*
     * (non-Javadoc)
     * @see net.rim.device.api.ui.container.VerticalFieldManager#sublayout(int, int)
     */
    public void sublayout(int width, int height) 
    {        
        super.sublayout(width,height);   
        if ( this.editField != null )
        {     
            int textLength = this.editField.getFont().getAdvance( this.editField.getText() ) ;
            // Leave some room to see the cursor at the end and to prevent scroll bugs
            setVirtualExtent(textLength + width / 2 ,height);         }
    }
    
    /**
     * Updates the layout (just calls updateLayout())
     */
    public void relayout()
    {
        updateLayout();
    }

    /*
     * (non-Javadoc)
     * @see de.enough.polish.blackberry.ui.PolishTextField#getText()
     */
	public String getText() 
	{
		return this.editField.getText();
	}
	     
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.blackberry.ui.PolishTextField#setText(java.lang.String)
	 */
	public void setText(String text) 
	{
	    this.editField.setText(text);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.blackberry.ui.PolishTextField#setStyle(de.enough.polish.ui.Style)
	 */
    public void setStyle(Style style) 
    {
           this.editField.setStyle(style);            
    }
    
    /*
     * (non-Javadoc)
     * @see de.enough.polish.blackberry.ui.PolishTextField#setPaintPosition(int, int)
     */
    public void setPaintPosition(int x, int y ) 
    {
            super.setPosition(x, y);
        	//#if polish.JavaPlatform >= BlackBerry/6.0
            if ( x != this.oldX || y!= this.oldY )
            {
                this.editField.resetRoundRect();
                this.oldX = x;
                this.oldY = y;
            }
            //#endif
    }               
    
    /*
     * (non-Javadoc)
     * @see net.rim.device.api.ui.Field#focusRemove()
     */
    public void focusRemove()
    {
    	// Do nothing
    }
    
    /*
     * (non-Javadoc)
     * @see net.rim.device.api.ui.Field#focusAdd(boolean)
     */
    public void focusAdd(boolean draw)
    {    	
    	// Do nothing
    }
    
    //#if polish.JavaPlatform >= BlackBerry/6.0
    /*
     * (non-Javadoc)
     * @see net.rim.device.api.ui.ScrollView#setCurrentLocation(int, int)
     */
    public void setCurrentLocation(int x, int y)
    {
        if ( this.ignoreLocalSetCurrentLocation )
        {
            return;
        }
        
        
        super.setCurrentLocation(x,y);
              
    }
    //#endif

    /*
     * (non-Javadoc)
     * @see net.rim.device.api.ui.Manager#onUnfocus()
     */
    protected void onUnfocus()
    {                  
    	this.ignoreLocalSetCurrentLocation = true;               
        setCursorPosition(0);   
        //#if polish.JavaPlatform >= BlackBerry/6.0
        	super.setCurrentLocation(0,0); 
        //#else
        	super.setHorizontalScroll(0);
        //#endif
        super.onUnfocus(); 
    }
    
    /*
     * (non-Javadoc)
     * @see net.rim.device.api.ui.Manager#navigationMovement(int, int, int, int)
     */
    public boolean navigationMovement(int dx, int dy, int status, int time) {
        int curPos = getCursorPosition() + dx;
        setCursorPosition(curPos);   
        if ( dy == 0 )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * @see de.enough.polish.blackberry.ui.PolishTextField#setCursorPosition(int)
     */
    public void setCursorPosition(int pos) {
            this.editField.setCursorPosition(pos);
    }

    /*
     * (non-Javadoc)
     * @see de.enough.polish.blackberry.ui.PolishTextField#getInsertPositionOffset()
     */
    public int getInsertPositionOffset() {
            return this.editField.getCursorPosition();
    }
    
    /*
     * (non-Javadoc)
     * @see net.rim.device.api.ui.Field#setChangeListener(net.rim.device.api.ui.FieldChangeListener)
     */
    public void setChangeListener( FieldChangeListener listener )
    {
        super.setChangeListener(listener);
        this.editField.setChangeListener(listener);
    }

    /*
     * (non-Javadoc)
     * @see de.enough.polish.blackberry.ui.PolishTextField#getCursorPosition()
     */
    public int getCursorPosition() {
            return this.editField.getCursorPosition();
    }
    
    /*
     * (non-Javadoc)
     * @see de.enough.polish.blackberry.ui.PolishTextField#doLayout(int, int)
     */
    public void doLayout( int width, int height) 
    {
            layout(width, height);            
    }        

    //#if polish.JavaPlatform >= BlackBerry/6.0
    /*
     * (non-Javadoc)
     * @see de.enough.polish.blackberry.ui.PolishTextField#needsNavigationFix()
     */
    public boolean needsNavigationFix() {
            return this.editField.needsNavigationFix() ;               
    }
    //#endif
    
    /*
     * (non-Javadoc)
     * @see de.enough.polish.blackberry.ui.PolishTextField#setFilter(net.rim.device.api.ui.text.TextFilter)
     */
	public void setFilter(TextFilter filter) {
		this.editField.setFilter(filter);		
	}

}