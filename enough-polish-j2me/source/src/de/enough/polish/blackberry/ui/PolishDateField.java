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

import java.util.Date;
import java.util.TimeZone;

import de.enough.polish.ui.Screen;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.i18n.SimpleDateFormat;

/**
 * <p>A field used by de.enough.polish.ui.DateField for input of dates on blackberry platforms.</p>
 *
 * <p>copyright Enough Software 2006 - 2009</p>
 * <pre>
 * history
 *        18.09.2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class PolishDateField 
extends DateField
//#if polish.hasTrackballEvents
implements AccessibleField
//#endif
{

	private static final int MIDP_DATE = 1;
	private static final int MIDP_TIME = 2;
	private static final int MIDP_DATE_TIME = 3;

	private boolean isFocused;
	public boolean processKeyEvents = true;
	private int fontColor;
	private int maxLayoutHeight;
	private int maxLayoutWidth;
	//#if polish.JavaPlatform >= BlackBerry/4.6
		private BackgroundWrapper backgroundWrapper;
	//#endif
	private final XYRect xyFocusRect;

	/**
	 * Creates a new date field
	 *
	 * @param date the date
	 * @param inputMode the input mode for the date, either
	 * DateField.DATE, DateField.DATE_TIME or DateField.TIME.
	 *
	 * @see javax.microedition.lcdui.DateField
	 */
	public PolishDateField( Date date, int inputMode ) {
		this( getTime( date ), getDateFormat( inputMode), getStyle(inputMode) );
	}

	/**
	 * Creates a new date field
	 *
	 * @param time the date
	 * @param inputMode the input mode for the date, either
	 * DateField.DATE, DateField.DATE_TIME or DateField.TIME.
	 *
	 * @see javax.microedition.lcdui.DateField
	 */
	public PolishDateField( long time, int inputMode ) {
		this( time, getDateFormat( inputMode), getStyle(inputMode) );
	}

	/**
	 * Creates a new date field
	 *
	 * @param date the date in ms from 1970-01-01
	 * @param dateFormat the format
	 * @param style the editing style, e.g. Field.EDITABLE
	 */
	public PolishDateField(long date, DateFormat dateFormat, long style) {
		super(null, date, dateFormat, style);
		this.xyFocusRect = new XYRect();
	}

	public static long getStyle(int inputMode) {
		long style = EDITABLE;
		style |= DrawStyle.LEFT;
		switch (inputMode) {
		case MIDP_DATE:
			style |= DateField.DATE;
			break;
		case MIDP_DATE_TIME:
			style |= DateField.DATE_TIME;
			break;
		case MIDP_TIME:
			style |= DateField.TIME;
			break;
		}
		return style;
	}

	private static long getTime( Date date ) {
		if (date == null) {
			return System.currentTimeMillis();
		} else {
			return date.getTime();
		}
	}

	private static DateFormat getDateFormat( int mode ) {
		int blackberryMode;
		switch (mode) {
		case MIDP_TIME:
			blackberryMode = DateFormat.TIME_DEFAULT;
			break;
		case MIDP_DATE_TIME:
			blackberryMode = DateFormat.DATETIME_DEFAULT;
			break;
		default:
			blackberryMode = DateFormat.DATE_DEFAULT;
		}
		return DateFormat.getInstance( blackberryMode );
	}



	public void focusAdd( boolean draw ) {
		//System.out.println("DateField: focusAdd (" + getText() + ")");
		Object bbLock = Application.getEventLock();
		synchronized (bbLock) {    
			super.focusAdd( draw );
		}
		this.isFocused = true;
	}

	public void focusRemove() {
		//System.out.println("DateField: focusRemove (" + getText() + ")");
		Object bbLock = Application.getEventLock();
		synchronized (bbLock) {    
			super.focusRemove();
		}
		this.isFocused = false;
	}

	public void paint( net.rim.device.api.ui.Graphics g ) {
		Screen screen = StyleSheet.currentScreen;
		if (this.isFocused && !screen.isMenuOpened()) {
//			int x = screen.getScreenContentX();
//			int y = screen.getScreenContentY();
//			int width = screen.getScreenContentWidth();
//			int height = screen.getScreenContentHeight();
//			g.pushContext(x - g.getTranslateX(), y - g.getTranslateY(), width, height, 0, 0 );
			g.setColor( this.fontColor );
			super.paint( g );
//			g.popContext();
		}
	}
	protected void drawFocus(net.rim.device.api.ui.Graphics g, boolean on)
	{
//		Screen screen = StyleSheet.currentScreen;
//		int x = screen.getScreenContentX();
//		int y = screen.getScreenContentY();
//		int width = screen.getScreenContentWidth();
//		int height = screen.getScreenContentHeight();
//		g.pushContext(x - g.getTranslateX(), y - g.getTranslateY(), width, height, 0, 0 );
		getFocusRect( this.xyFocusRect );
		g.setColor( this.fontColor );
		// apparently the focus is paint _after_ the content is drawn on BB.
		// Weird design decision, this means that we cannot use any fill method here...
//		graphics.fillRoundRect( this.xyFocusRect.x - 2, this.xyFocusRect.y - 2, this.xyFocusRect.width + 4, this.xyFocusRect.height + 4, 6, 6 );
//		int complementaryColor = DrawUtil.getComplementaryColor( this.fontColor );
//		graphics.setColor( complementaryColor );
//		graphics.fillRoundRect( this.xyFocusRect.x, this.xyFocusRect.y, this.xyFocusRect.width, this.xyFocusRect.height, 6, 6 );
		g.drawRoundRect( this.xyFocusRect.x, this.xyFocusRect.y, this.xyFocusRect.width, this.xyFocusRect.height, 6, 6 );
//		g.popContext();

	}

	public void layout( int width, int height) {
		if (this.maxLayoutWidth != 0) {
			super.layout( this.maxLayoutWidth, this.maxLayoutHeight );
		} else {
			super.layout( width, height );
		}
	}

	public void setStyle(Style style) {
		Font font = (Font)(Object)style.getFont();
		if (font == null) {
			font = Font.getDefaultFont();
		}
		try {
			super.setFont( font.font );
		} catch (IllegalStateException e) {
			//#debug error
			System.out.println("Layout error: " + e );
		}
		this.fontColor = style.getFontColor();
		//#if polish.JavaPlatform >= BlackBerry/4.6
			if (style.background != null) {
				if (this.backgroundWrapper == null) {
					this.backgroundWrapper = new BackgroundWrapper( style.background );
					try {
						setBackground(this.backgroundWrapper);
					} catch (Exception e) {
						//#debug error
						System.out.println("Unable to set background" + e);
					}
				} else {
					this.backgroundWrapper.setBackground(style.background);
				}
			}
		//#endif
	}

	public void setPaintPosition(int x, int y ) {
		this.isFocused = true;
		super.setPosition(x, y);
	}

	public void setInputMode(int mode) {
		DateFormat format = getDateFormat(mode);
		Object bbLock = Application.getEventLock();
		synchronized (bbLock) {
			setFormat( format );
		}
	}

	public static final DateFormat getDateFormat(int mode, TimeZone timeZone) {
		DateFormat format = getDateFormat( mode );
		//TODO RIM API says it's possible to set timezone on DateFormat, however no applicable methods can be found
		return format;
	}

	public void doLayout(int width, int height) {
		this.maxLayoutWidth = width;
		this.maxLayoutHeight = height;
		layout( width, height );
	}

	//#if polish.hasTrackballEvents
	public boolean navigationMovement(int dx, int dy, int status, int time) {
		int lastSubfield = getCurrentSubfield();
		boolean processed = super.navigationMovement(dx, dy, status, time);
		if (!processed && dx != 0) {
			super.moveFocus( dx < 0 ? -1 : +1, 0, time);
			processed = true;
		}
		if (processed && getCurrentSubfield() == lastSubfield ) {
			processed = false;
		}
		return processed;
	}
	//#endif
	
	//#if polish.hasTrackballEvents
	public boolean navigationClick(int status, int time) {
		boolean processed = super.navigationClick(status, time);
		if (!processed) {
			super.invokeAction(ACTION_INVOKE);
		}
		return true;
	}
	//#endif


	/**
	 * Sets a new dateformat for this field.
	 * @param pattern the new pattern, e.g. 'yyyy-MM-dd'.
	 */
	public void setDateFormatPattern(String pattern) {
		setFormat( getDateFormat( pattern) );
	}
	
	/**
	 * Returns a simple date format
	 * @param dateFormatPattern the pattern as a String
	 * @return a SimpleDateFormat
	 */
	public static DateFormat getDateFormat(String dateFormatPattern) {
		return new SimpleDateFormat( dateFormatPattern );
	}

}
