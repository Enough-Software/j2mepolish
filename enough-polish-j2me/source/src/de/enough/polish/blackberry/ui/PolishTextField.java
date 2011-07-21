//#condition polish.usePolishGui && polish.blackberry

/*
 * Created on Mar 13, 2007 at 12:37:50 PM.
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
package de.enough.polish.blackberry.ui;


import de.enough.polish.ui.Style;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.text.TextFilter;

/**
 * <p>Provides common functionality of the PolishEditField, PolishPasswordEditField, etc.</p>
 *
 * <p>Copyright Enough Software 2007 - 2011</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public interface PolishTextField {
	
	String getText();
	
	void setText(String text);
	
	void focusRemove();
	
	void setStyle( Style style );
	
	XYRect getExtent();
	
	void doLayout( int width, int height );
	
	void setPaintPosition( int x, int y );
	
	void setCursorPosition( int pos );
	
	int getInsertPositionOffset();
	
	void setChangeListener( FieldChangeListener listener );

	int getCursorPosition();
	
	void setFilter (TextFilter filter);

	//#if polish.JavaPlatform >= BlackBerry/6.0
	public boolean needsNavigationFix();
	//#endif

}
