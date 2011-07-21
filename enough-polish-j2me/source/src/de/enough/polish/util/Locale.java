//#condition polish.cldc

/*
 * Created on 09-Sep-2004 at 10:30:04.
 * 
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

//#if polish.usePolishGui || polish.midp
	import javax.microedition.rms.RecordEnumeration;
	import javax.microedition.rms.RecordStore;
	import javax.microedition.rms.RecordStoreException;
//#endif
//#if polish.blackberry
	import net.rim.device.api.i18n.SimpleDateFormat;
//#endif
	
import de.enough.polish.io.RecordingDataInputStream;


//#if polish.Locale.imports:defined
	//#include ${polish.Locale.imports}
//#endif

/**
 * <p>Locale provides translations and format dates and currencies depending on the chosen localization.</p>
 *
 * <p>Copyright Enough Software 2004, 2005, 2006, 2007 - 2009</p>

 * <pre>
 * history
 *        09-Sep-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public final class Locale {
	
	private static short[][] multipleParameterOrders;
	private static String[][] multipleParameterTranslations;
	//#if polish.i18n.useDynamicTranslations || polish.LibraryBuild
		private static String[] plainTranslations;
		private static String[] singleParameterTranslationsStart;
		private static String[] singleParameterTranslationsEnd;
		private static boolean isLoaded;
		private static boolean isLoadError;
		/*
		static {
			try {
				//#if polish.locale:defined
					//#= loadTranslations( "/${polish.locale}.loc" );
				//#else
					loadTranslations( "/default.loc" );
				//#endif
			} catch (IOException e ) {
				//#if polish.locale:defined
					//#= String fileName = "/${polish.locale}.loc";
				//#else
					String fileName = "/default.loc";
				//#endif
				//#debug error
				System.out.println("Unable to load default translations from " + fileName + ": " + e );
			}
			
		}
		*/
	//#endif
		
	
	// do not change the following line!
//$$IncludeLocaleDefinitionHere$$//
	
	//#ifdef polish.LibraryBuild
	/** The ISO language code, 
	 * e.g. "en" for English. */
	public static String LANGUAGE = "en";
	/** 
	 * The ISO country code, 
	 * e.g. "US" for USA 
	 * This is null when no country is defined in the current locale.
	 */
	public static String COUNTRY = "US";
	/** The localized language name, 
	 * e.g. "English" or "Deutsch" */
	public static String DISPLAY_LANGUAGE = "English";
	/** 
	 * The localized country name, 
	 * e.g. "United States" or "Deutschland".
	 * This is null when no country is defined in the current locale.
	 */
	public static String DISPLAY_COUNTRY = "United States";
	/** 
	 * The symbol of the currency, 
	 * e.g. "$" or "&euro;".
	 * This is null when no country is defined in the current locale.
	 */
	public static String CURRENCY_SYMBOL = "$";
	/** 
	 * The international three letter code of the currency, 
	 * e.g. "USD" or "EUR".
	 * This is null when no country is defined in the current locale.
	 */
	public static String CURRENCY_CODE = "USD";


	/** 
	 * The character used for decimal sign.
	 * This is a '-' by default.
	 */
	public static char MINUS_SIGN = '-';

	/** 
	 * The character used for zero.
	 * This is a '0' by default.
	 */
	public static char ZERO_DIGIT = '0';

	/** 
	 * The character used for decimal sign.
	 * This is a dot by default.
	 */
	public static char DECIMAL_SEPARATOR = '.';

	/** 
	 * The monetary decimal separator.
	 * This is a dot by default.
	 */
	public static char MONETARY_DECIMAL_SEPARATOR = '.';

	/** 
	 * The character used for thousands separator.
	 * This is a comma by default.
	 */
	public static char GROUPING_SEPARATOR = ',';
	
	/**
	 * The character used to represent a percent value.
	 */
	public static char PERCENT = '%';

	/**
	 * The character used to represent a permill value.
	 */
	public static char PERMILL = '\u2030';

	/** 
	 * The string used to represent infinity.
	 */
	public static String INFINITY = "\u221e";
	
	

	private Locale() {
		// no instantiation allowed
	}
	//#endif
	
	private static String DATE_FORMAT_PATTERN;
	private static String[] monthNames;

	
	//#if polish.i18n.useDynamicTranslations || polish.LibraryBuild
	private static void initialize() {
		//#if polish.Locale.initializeMethod:defined
			//#include ${polish.Locale.initializeMethod}
		//#else
			//#if polish.i18n.useExternalTranslations && (polish.midp || polish.usePolishGui)
				RecordStore store = null;
				try
				{
					store = RecordStore.openRecordStore("_translations", false);
					RecordEnumeration enumeration = store.enumerateRecords(null, null, false);
					if (enumeration.hasNextElement()) {
						// store does already exist:
						byte[] data = enumeration.nextRecord();
						loadTranslations( new DataInputStream( new ByteArrayInputStream( data ) ) );
						isLoaded = true;
						isLoadError = false;
						//#debug
						System.out.println("loaded translations successfully from RMS.");
						return;
					}
				} catch (Exception e)
				{
					//#debug info
					System.out.println("Unable to load translations from rms" + e );
				} finally {
					if (store != null) {
						try
						{
							store.closeRecordStore();
						} catch (Exception e)
						{
							// ignore
						}
					}
				}
			//#endif
			try {
				String loc = null;
				InputStream in = null;
				String meLocale = System.getProperty("microedition.locale");
				if (meLocale != null) {
					loc = "/" + meLocale + ".loc";
					in = loc.getClass().getResourceAsStream(loc);
					if (in == null && meLocale.length() > 2) {
						// try to load just the language:
						loc = "/" + meLocale.substring(0, 2) + ".loc";
						in = loc.getClass().getResourceAsStream(loc);
					}
				}
				if (in == null) {
					//#if polish.locale:defined
						//#= loc = "/${polish.locale}.loc";
					//#else
						loc = "/default.loc";
					//#endif
					in = loc.getClass().getResourceAsStream(loc);
				}
				if (in != null) {
					loadTranslations( new DataInputStream( in ) );
					isLoaded = true;
					isLoadError = false;
					//#debug
					System.out.println("sucessfully loaded translations from " + loc);
				} else {
					//#debug warn
					System.out.println("unable to load translations from " + loc + ": no input stream");
					isLoadError = true;
				}
			} catch (Exception e) {
				isLoadError = true;
				//#debug error
				System.out.println("Unable to load localizations " + e );
			}
		//#endif		
	}
	//#endif

	//#if polish.i18n.useDynamicTranslations || polish.LibraryBuild
	/**
	 * Retrieves the translation for the specified key.
	 * 
	 * @param key the key, e.g. "labels.StartGame"
	 * @return the translation, e.g. "Start Game" or "Spiel starten", null when the key was not found
	 */
	//#if polish.LibraryBuild
	public static String get( String key ) {
		int keyId = 0;
	//#else
		//# public static String get( int keyId ) {
	//#endif
		if ( !isLoaded ) {
			initialize();
			if (isLoadError) {
				return "";
			}
		}
		// all simple translations are usually directly embedded into the source-code,
		// so this method does only need to be implemenented when dynamic translations are used:
		//#if polish.debug.error
		try {
		//#endif
			return plainTranslations[ keyId ];
		//#if polish.debug.error
		} catch (Exception e) {
			//#debug error
			System.out.println("Locale: no translation for ID " + keyId + " in " + ( plainTranslations == null ? "null" : "" + plainTranslations.length ));
			return "<unknown>";
		}
		//#endif
	}
	//#endif
	
	//#if polish.i18n.useDynamicTranslations || polish.LibraryBuild
	/**
	 * Retrieves the translation for the specified key.
	 * 
	 * @param key the key, e.g. "labels.WelcomeUser"
	 * @param parameter the parameter, e.g. "Peter"
	 * @return the translation, e.g. "Hello Peter!" or "Hallo Peter!", null when the key was not found
	 * @throws NullPointerException when the given parameter is null.
	 */
	//#if polish.LibraryBuild
	public static String get( String key, String parameter ) {
		int keyId = 0;
	//#else
		//# public static String get( int keyId, String parameter ) {
	//#endif
		if ( !isLoaded ) {
			initialize();
			if (isLoadError) {
				return "";
			}
		}
		// all simple translations are usually directly embedded into the source-code,
		// so this method does only need to be implemenented when dynamic translations are used:
		return singleParameterTranslationsStart[keyId] + parameter + singleParameterTranslationsEnd[keyId];
	}
	//#endif
	
	/**
	 * Retrieves the translation for the specified key.
	 * 
	 * @param key the key, e.g. "labels.MatchTitle"
	 * @param parameters the parameters-array, this needs to be defined outside of the call.
	 *     e.g.
	 * <pre> 
	 * 		String[] params = new String[]{ "Peter", "Jordan" };
	 * 		String translation = Locale . get( "titles.boxing", params );
	 * </pre>
   *
	 * @return the translation, e.g. "Peter vs Jordan" or "Peter gegen Jordan"
	 * @throws NullPointerException when the given parameters are null or if one of the given parameters is null.
	 */
	//#ifdef polish.LibraryBuild
	public static String get( String key, String[] parameters ) {
	//#else
		//# public static String get( int keyId, String[] parameters ) {
	//#endif
		//#ifdef polish.LibraryBuild
			short keyId = 0;
		//#endif
		//#ifdef polish.i18n.useDynamicTranslations
			if ( !isLoaded ) {
				initialize();
				if (isLoadError) {
					return "";
				}
			}
		//#endif
		
		// Reshuffle the parameters:
		// CHECK: There could be more or less parameters than expected, e.g.
		// "hello {2}, you are a {0}"???
		// --> some parameters can be ignored
		final short[] reorder = multipleParameterOrders[ keyId ];
		final String[] reorderedParameters;
		if (reorder != null) {
			reorderedParameters = new String[ reorder.length ];
			for (int i = 0; i < reorderedParameters.length; i++) {
				reorderedParameters[i] = parameters[ reorder[i] ];	
			}
		} else {
			reorderedParameters = parameters;
		}
		// Now merge the value with the reordered parameters:
		final String[] valueChunks = multipleParameterTranslations[ keyId ];
		final StringBuffer result = new StringBuffer();
		for (int i = 0; i < reorderedParameters.length; i++) {
			String value = valueChunks[i];
			result.append( value )
				  .append( reorderedParameters[ i ]);
			
		}
		
		for (int i = reorderedParameters.length; i < valueChunks.length; i++) {
			result.append( valueChunks[i] );
		}
		
		// return result:
		return result.toString();
	}
	
	/**
	 * Retrieves the default date format pattern.
	 * The pattern can consist of arbitrary characters but following characters have a meaning:
	 * <ul>
	 *  <li><b>yyyy</b>: The year in 4 digits, e.g. 2010.</li>
	 *  <li><b>MM</b>: the month as a numerical value between 1 (January) and 12 (December).</li>
	 *  <li><b>dd</b>: the day of the month as a numerical value between 1 and 31.</li>
	 *  <li><b>HH</b>: the hour of the day between 0 and 23.</li>
	 *  <li><b>mm</b>: the minute of the hour between 0 and 59.</li>
	 * </ul>
	 * @return the pattern, e.g. "yyyy-MM-dd"
	 * @see #setDefaultDateFormatPattern(String)
	 */
	public static String getDefaultDateFormatPattern() {
		if (DATE_FORMAT_PATTERN == null) {
			StringBuffer buffer = new StringBuffer();
			//#if polish.blackberry
				buffer.append( new SimpleDateFormat(SimpleDateFormat.DATE_MEDIUM).toPattern() );
			//#elif polish.DateFormat == us
				buffer.append( "MM/dd/yyyy" );
			//#elif polish.DateFormat == de
				buffer.append( "dd.MM.yyyy" );
			//#elif polish.DateFormat == fr
				buffer.append( "dd/MM/yyyy" );
			//#elif polish.DateFormat == mdy
				buffer.append( "MM" )
				//#if polish.DateFormatSeparator:defined
					//#= .append("${polish.DateFormatSeparator}")
				//#else
				        .append("-")
				//#endif
				.append("dd")
				//#if polish.DateFormatSeparator:defined
					//#= .append("${polish.DateFormatSeparator}")
				//#else
				        .append("-")
				//#endif
				.append("yyyy");
			//#elif polish.DateFormat == dmy
				buffer.append( "dd" )
				//#if polish.DateFormatSeparator:defined
					//#= .append("${polish.DateFormatSeparator}")
				//#else
				        .append("-")
				//#endif
				.append("MM")
				//#if polish.DateFormatSeparator:defined
					//#= .append("${polish.DateFormatSeparator}")
				//#else
				        .append("-")
				//#endif
				.append("yyyy");
			//#else
				// default to YMD
				buffer.append( "yyyy" )
				//#if polish.DateFormatSeparator:defined
					//#= .append("${polish.DateFormatSeparator}")
				//#else
				        .append("-")
				//#endif
				.append("MM")
				//#if polish.DateFormatSeparator:defined
					//#= .append("${polish.DateFormatSeparator}")
				//#else
				        .append("-")
				//#endif
				.append("dd");
			//#endif
			DATE_FORMAT_PATTERN = buffer.toString();
		}
		return DATE_FORMAT_PATTERN;
	}
	
	/**
	 * Sets the default date format pattern.
	 * The pattern can consist of arbitrary characters but following characters have a meaning:
	 * <ul>
	 *  <li><b>yyyy</b>: The year in 4 digits, e.g. 2010.</li>
	 *  <li><b>MM</b>: the month as a numerical value between 1 (January) and 12 (December).</li>
	 *  <li><b>dd</b>: the day of the month as a numerical value between 1 and 31.</li>
	 *  <li><b>HH</b>: the hour of the day between 0 and 23.</li>
	 *  <li><b>mm</b>: the minute of the hour between 0 and 59.</li>
	 * </ul>
	 * @param pattern the pattern, e.g. "yyyy-MM-dd"
	 * @see #getDefaultDateFormatPattern()
	 * @see #formatDate(Calendar)
	 */
	public static void setDefaultDateFormatPattern( String pattern ) {
		DATE_FORMAT_PATTERN = pattern;
	}
	
	/**
	 * Sets the names of months for date formatting.
	 * @param names the names
	 */
	public static void setMonthNames( String[] names ) {
		monthNames = names;
	}
	
	/**
	 * Retrieves the names of the months of the year
	 * @return the names of the months of the year starting with January, might be null
	 */
	public static String[] getMonthNames() {
		return monthNames;
	}
		
	/**
	 * Formats the given date to the current locale's format.
	 * This method just calls the formatDate-method with a new Date instance.
	 * 
	 * @param time the time in milliseconds after 1.1.1970 GMT.
	 * @return the locale specific date representation.
	 * @throws NullPointerException when the date is null
	 * @see #formatDate(Date)
	 * @see #getDefaultDateFormatPattern()
	 * @see #setDefaultDateFormatPattern(String)
	 */
	public static String formatDate( long time ) {		
		return formatDate( new Date( time ) );
	}
	
	/**
	 * Formats the given date to the current locale's format.
	 * This method just calls the formatDate-method with a new Date instance.
	 * 
	 * @param time the time in milliseconds after 1.1.1970 GMT.
	 * @param dateFormat the date format that may include yyyy (year), M, MM, MMMMM (month in numerical [M, MM] or textual representation), dd (day of month), HH (hour of day) and mm (minute of hour)
	 * @return the locale specific date representation.
	 * @throws NullPointerException when the date is null
	 * @see #formatDate(Date)
	 */
	public static String formatDate( long time, String dateFormat ) {		
		return formatDate( new Date( time ), dateFormat );
	}
	
	/**
	 * Formats the given date to the current locale's format.
	 * This method just calls the formatDate-method with a new Calendar instance.
	 * 
	 * @param date the date
	 * @return the locale specific date representation.
	 * @throws NullPointerException when the date is null
	 * @see #formatDate(Calendar)
	 * @see #getDefaultDateFormatPattern()
	 * @see #setDefaultDateFormatPattern(String)
	 */
	public static String formatDate( Date date ) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return formatDate( calendar );
	}
	
	/**
	 * Formats the given date to the current locale's format.
	 * This method just calls the formatDate-method with a new Calendar instance.
	 * 
	 * @param date the date
	 * @param dateFormat the date format that may include yyyy (year), M, MM, MMMMM (month in numerical [M, MM] or textual representation), dd (day of month), HH (hour of day) and mm (minute of hour)
	 * @return the locale specific date representation.
	 * @throws NullPointerException when the date is null
	 * @see #formatDate(Calendar)
	 */
	public static String formatDate( Date date, String dateFormat ) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return formatDate( calendar, dateFormat );
	}
	
	/**
	 * Formats the given calendar to the current locale's format.
	 * 
	 * @param calendar the calendar which holds the date
	 * @return the locale specific date representation.
	 * @throws NullPointerException when the calendar is null
	 * @see #getDefaultDateFormatPattern()
	 * @see #setDefaultDateFormatPattern(String)
	 */
	public static String formatDate( Calendar calendar ) {
		StringBuffer buffer = new StringBuffer(10);
		formatDate( calendar, buffer );
		return buffer.toString();
	}
	
	/**
	 * Formats the given calendar to the specified locale's format.
	 * 
	 * @param calendar the calendar which holds the date
	 * @param dateFormat the date format that may include yyyy (year), M, MM, MMMMM (month in numerical [M, MM] or textual representation), dd (day of month), HH (hour of day) and mm (minute of hour)
	 * @return the locale specific date representation.
	 * @throws NullPointerException when the calendar is null
	 */
	public static String formatDate( Calendar calendar, String dateFormat ) {
		StringBuffer buffer = new StringBuffer(dateFormat.length());
		formatDate( calendar, buffer, dateFormat );
		return buffer.toString();
	}

	/**
	 * Formats the given TimePoint to the current locale's format.
	 * 
	 * @param tp the TimePoint which holds the date
	 * @return the locale specific date representation.
	 * @throws NullPointerException when the calendar is null
	 * @see #getDefaultDateFormatPattern()
	 * @see #setDefaultDateFormatPattern(String)
	 */
	public static String formatDate( TimePoint tp ) {
		return formatDate( tp, getDefaultDateFormatPattern() );
	}
	
	/**
	 * Formats the given TimePoint to the current locale's format.
	 * Example: Locale.formatDate( tp, "MMMMM/dd/yyyy" );
	 * @param tp the TimePoint which holds the date
	 * @param dateFormat the date format that may include yyyy (year), M, MM, MMMMM (month in numerical [M, MM] or textual representation), dd (day of month), HH (hour of day) and mm (minute of hour)
	 * @return the locale specific date representation.
	 * @throws NullPointerException when the calendar is null
	 */
	public static String formatDate( TimePoint tp, String dateFormat ) {
		StringBuffer buffer = new StringBuffer(dateFormat.length());
		formatDate( tp.getYear(), tp.getMonth() + 1, tp.getDay(), tp.getHour(), tp.getMinute(), dateFormat, buffer );
		return buffer.toString();
	}
	
	/**
	 * Formats the given TimePoint to the given locale's format.
	 * 
	 * @param tp the TimePoint which holds the date
	 * @param dateFormat the date format that may include yyyy (year), M, MM, MMMMM (month in numerical [M, MM] or textual representation), dd (day of month), HH (hour of day) and mm (minute of hour)
	 * @param buffer the buffer into which the formated date is appended
	 * @throws NullPointerException when the calendar is null
	 */
	public static void formatDate( TimePoint tp, String dateFormat, StringBuffer buffer ) {
		//#if polish.blackberry
			formatDate( tp.getAsCalendar(), buffer, dateFormat );
		//#else
			formatDate( tp.getYear(), tp.getMonth() + 1, tp.getDay(), tp.getHour(), tp.getMinute(), dateFormat, buffer );
		//#endif
	}

	/**
	 * Formats the given calendar to the current locale's format.
	 * Use this method for best efficiency.
	 * 
	 * @param calendar the calendar which holds the date
	 * @param buffer a StringBuffer, should be at least 10 characters big
	 * @throws NullPointerException when the calendar is null
	 * @see #getDefaultDateFormatPattern()
	 * @see #setDefaultDateFormatPattern(String)
	 */
	public static void formatDate( Calendar calendar, StringBuffer buffer  ) {
		formatDate( calendar, buffer, getDefaultDateFormatPattern() );
	}
	
	/**
	 * Formats the given calendar to the current locale's format.
	 * Use this method for best efficiency.
	 * 
	 * @param calendar the calendar which holds the date
	 * @param buffer a StringBuffer, should be at least 10 characters big
	 * @param dateFormat the date format that may include yyyy (year), M, MM, MMMMM (month in numerical [M, MM] or textual representation), dd (day of month), HH (hour of day) and mm (minute of hour)
	 * @throws NullPointerException when the calendar is null
	 */
	public static void formatDate( Calendar calendar, StringBuffer buffer, String dateFormat  ) {
		//#if polish.blackberry
			SimpleDateFormat format = new SimpleDateFormat(dateFormat);
			format.format(calendar, buffer, null);
		//#else
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get( Calendar.MONTH ) + 1;
			int day = calendar.get( Calendar.DAY_OF_MONTH );
			int hour = calendar.get( Calendar.HOUR_OF_DAY );
			int minute = calendar.get( Calendar.MINUTE );
			formatDate(year, month, day, hour, minute, dateFormat, buffer);
		//#endif
	}

	private static void formatDate(int year, int month, int day, int hour,
			int minute, String dateFormat, StringBuffer buffer) 
	{
		int length = dateFormat.length();
		boolean isMonthTextual = (dateFormat.indexOf("MMMMM") != -1);
		boolean isMonthNumerical2Digit = !isMonthTextual && (dateFormat.indexOf("MM") != -1);
		for (int i=0; i<length-1; i++) {
			char c = dateFormat.charAt(i);
			char nextC = dateFormat.charAt(i+1);
			if (c == 'y' && i <length-3 && nextC=='y' && dateFormat.charAt(i+2)=='y' && dateFormat.charAt(i+3)=='y') {
				if (year < 10) {
					buffer.append( "000");
				} else if (year < 100) {
					buffer.append( "00");
				} else if (year < 1000) {
					buffer.append( "0");
				}
				buffer.append(year);
				i += 3;
			} else if ( c == 'M' ) {
				if (isMonthTextual) {
					if (monthNames != null) {
						String monthName = monthNames[month-1];
						buffer.append(monthName);
					} else {
						if (month < 10) {
							buffer.append('0');
						}
						buffer.append(month);
					}
					i+= "MMMMM".length() - 1;
				} else if (isMonthNumerical2Digit) {
					if (month < 10) {
						buffer.append('0');
					}
					buffer.append(month);
					i++;
				} else {
					buffer.append(month);
				}
			} else if ( c == 'd' && nextC == 'd') {
				if (day < 10) {
					buffer.append( '0' );
				}
				buffer.append( day );
				i++;
			} else if ( c == 'H' && nextC == 'H') {
				//#if polish.blackberry
				if (hour < 10) {
					buffer.append('0');
				}
				//#endif
				buffer.append(hour);
				i++;
			} else if (c == 'm' && nextC == 'm') {
				if (minute < 10) {
					buffer.append('0');
				}
				buffer.append( minute );
				i++;
			} else {
				buffer.append(c);
			}
		}
	}
	
	/**
	 * Formats the given time as a time string, e.g. '13:45' or '1:45 PM'
	 * @param time the time in milliseconds since 1.1.1970
	 * @return the formatted time
	 */
	public static String formatTime(long time)
	{
		return formatTime( new Date(time) );
	}

	
	/**
	 * Formats the given time as a time string, e.g. '13:45' or '1:45 PM'
	 * @param time the date
	 * @return the formatted time
	 */
	public static String formatTime(Date time)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(time);
		return formatTime( calendar );
	}
	

	/**
	 * Formats the given time as a time string, e.g. '13:45' or '1:45 PM'
	 * @param time the date
	 * @return the formatted time
	 */
	public static String formatTime(Calendar time)
	{
		StringBuffer buffer = new StringBuffer();
		formatTime( time, buffer );
		return buffer.toString();
	}
	
	/**
	 * Formats the given time as a time string, e.g. '13:45' or '1:45 PM'
	 * @param time the date
	 * @param buffer the buffer to which the time should be added
	 */
	public static void formatTime(Calendar time, StringBuffer buffer)
	{
		//TODO right now only 24 hour system is supported
		int hours = time.get(Calendar.HOUR_OF_DAY);
		buffer.append( hours ).append(':');
		int minutes = time.get( Calendar.MINUTE );
		if (minutes < 10) {
			buffer.append('0');
		}
		buffer.append( minutes );
	}

	//#if polish.i18n.useDynamicTranslations || polish.LibraryBuild
	/**
	 * Loads translations from the specified file which is embedded in the JAR file.
	 * Attention: only when the preprocessing symbol "polish.i18n.useExternalTranslations" is defined,
	 * J2ME Polish will automatically load the stored translations at the next startup of the application.
	 * In other cases you need to manually check if the "_translations" recordstore exists and load the data
	 * from the first recordset using Locale.loadTranslations( DataIn ).
	 * 
	 * @param in the data input stream
	 * @throws IOException when there was an error reading the translations 
	 */
	public static void loadAndStoreTranslations( InputStream in ) 
	throws IOException 
	{
		RecordingDataInputStream dataIn = new RecordingDataInputStream( in );
		loadTranslations( dataIn );
		byte[] data = dataIn.getRecordedData();
		//#if polish.midp || polish.usePolishGui
			RecordStore store = null;
			try
			{
				store = RecordStore.openRecordStore("_translations", true);
				RecordEnumeration enumeration = store.enumerateRecords(null, null, false);
				if (enumeration.hasNextElement()) {
					// store does already exist:
					store.setRecord( enumeration.nextRecordId(), data, 0, data.length );
				} else {
					store.addRecord(data, 0, data.length );
				}
			} catch (RecordStoreException e)
			{
				//#debug error
				System.out.println("Unable to store translations into rms" + e );
			} finally {
				try
				{
					store.closeRecordStore();
				} catch (RecordStoreException e)
				{
					// ignore
				}
			}
		//#endif
	}
	//#endif

	//#if polish.i18n.useDynamicTranslations || polish.LibraryBuild
	/**
	 * Loads translations from the specified file which is embedded in the JAR file.
	 * 
	 * @param url the URL of the file, e.g. "/en.loc" or "/es.loc"  - do not forget the required forward slash at the beginning of the URL
	 * @throws IOException when there was an error reading the translations 
	 */
	public static void loadTranslations( String url ) 
	throws IOException 
	{
		//#debug
		System.out.println("loading translations from " + url );
		InputStream is = 
			//#if polish.cldc11
				Locale.class.getResourceAsStream( url );
			//#else
				// on CLDC 1.0 we have no Locale.class shortcut:
				(new Locale()).getClass().getResourceAsStream(url);
			//#endif
		if (is == null) {
			//System.out.println("!!!!!Cannot find:  " + url );
			throw new IOException();
		}
		loadTranslations( new DataInputStream( is ) );
	}
	//#endif

	//#if polish.i18n.useDynamicTranslations || polish.LibraryBuild
	/**
	 * Loads translations from the specified input stream.
	 * 
	 * @param in the data input stream
	 * @throws IOException when there was an error reading the translations 
	 */
	public static void loadTranslations( DataInput in ) 
	throws IOException 
	{
		try {
		// read plain translations without any parameters:
		int numberOfPlainTranslations = in.readInt();
		String[] plainTs = new String[ numberOfPlainTranslations ];
		//System.out.println("Loading " + numberOfPlainTranslations + " translations...");
		for (int i = 0; i < numberOfPlainTranslations; i++) {
			plainTs[i] = in.readUTF();
			//System.out.println(i + "=" + plainTs[i]);
		}
		plainTranslations = plainTs;
		
		// read single parameter translations:
		int numberOfSingleParameterTranslations = in.readInt();
		String[] singleParamsTsStart = new String[ numberOfSingleParameterTranslations ];
		String[] singleParamsTsEnd = new String[ numberOfSingleParameterTranslations ];
		for (int i = 0; i < numberOfSingleParameterTranslations; i++) {
			singleParamsTsStart[i] = in.readUTF();
			singleParamsTsEnd[i] = in.readUTF();
		}
		singleParameterTranslationsStart = singleParamsTsStart;
		singleParameterTranslationsEnd = singleParamsTsEnd;

		// read translations with several parameters:
		int numberOfMultipleParametersTranslations = in.readInt();
		String[][] translationChunks = new String[ numberOfMultipleParametersTranslations ][];
		short[][] orders = new short[ numberOfMultipleParametersTranslations ][];
		for (int i = 0; i < numberOfMultipleParametersTranslations; i++) {
			int numberOfChunks = in.readUnsignedByte();
			String[] chunkValues = new String[ numberOfChunks ];
			for (int j = 0; j < numberOfChunks; j++) {
				chunkValues[j] = in.readUTF();
			}
			short[] chunkOrders = new short[ numberOfChunks - 1 ];
			for (int j = 0; j < numberOfChunks-1; j++) {
				chunkOrders[j] = (short) in.readUnsignedByte();
			}
			translationChunks[i] = chunkValues;
			orders[i] = chunkOrders;
		}
		multipleParameterOrders = orders;
		multipleParameterTranslations = translationChunks;
		
		// now load language name etc:
		LANGUAGE = in.readUTF();
		DISPLAY_LANGUAGE = in.readUTF();
		MINUS_SIGN = in.readChar();
		ZERO_DIGIT = in.readChar();
		DECIMAL_SEPARATOR = in.readChar();
		MONETARY_DECIMAL_SEPARATOR = in.readChar();
		GROUPING_SEPARATOR = in.readChar();
		PERCENT = in.readChar();
		PERMILL = in.readChar();
		INFINITY = in.readUTF();
		String country = in.readUTF();
		if (country.length() > 0) {
			COUNTRY = country;
			DISPLAY_COUNTRY = in.readUTF();
			CURRENCY_SYMBOL = in.readUTF();
			CURRENCY_CODE = in.readUTF();
		} else {
			COUNTRY = null;
			DISPLAY_COUNTRY = null;
			CURRENCY_SYMBOL = null;
			CURRENCY_CODE = null;
		}
		isLoaded = true;
		isLoadError = false;
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			//#debug error
			System.out.println("error while loading translations: " + e );
			throw new IOException( e.toString() );
		}
	}
	//#endif


	
	
}
