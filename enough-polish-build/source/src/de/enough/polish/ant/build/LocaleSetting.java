/*
 * Created on 07-Apr-2006 at 23:27:15.
 * 
 * Copyright (c) 2006 Robert Virkus / Enough Software
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
package de.enough.polish.ant.build;

import java.util.Locale;

import de.enough.polish.ant.Setting;
import de.enough.polish.util.StringUtil;

/**
 * <p>Is used to configure a single locale.</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        07-Apr-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class LocaleSetting extends Setting {
	
	private Locale locale;
	private Locale[] locales;
	private String encoding;
	private boolean convert;

	/**
	 * Creates a new setting
	 */
	public LocaleSetting() {
		super();
	}
	
	public LocaleSetting(Locale locale) {
		this.locale = locale;
		this.locales = new Locale[]{ locale };
	}

	public LocaleSetting(String localeDef ) {
		this.locales = getLocales( localeDef );
		this.locale = this.locales[0];
	}

	public LocaleSetting(Locale locale, LocaleSetting setting) {
		super( setting );
		this.encoding = setting.encoding;
		this.convert = setting.convert;
		this.locale = locale;
		this.locales = new Locale[]{ locale };
	}
	
	public void setName( String localeDef ) {
		setLocales( localeDef );
	}
	
	public void setNames( String localeDef ) {
		setLocales( localeDef );
	}

	public void setLocale( String localeDef ) {
		setLocales( localeDef );
	}

	public void setLocales( String localeDef ) {
		this.locales = getLocales( localeDef );
		this.locale = this.locales[0];
	}
	
	public Locale getLocale() {
		return this.locale;
	}

	public Locale[] getLocales() {
		return this.locales;
	}

	/**
	 * @return Returns the convert.
	 */
	public boolean isConvert() {
		return this.convert;
	}

	/**
	 * @param convert The convert to set.
	 */
	public void setConvert(boolean convert) {
		this.convert = convert;
	}

	/**
	 * @return Returns the encoding.
	 */
	public String getEncoding() {
		return this.encoding;
	}

	/**
	 * @param encoding The encoding to set.
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	/**
	 * Parses the locale from the given definition
	 * 
	 * @param definitionsStr the definition, e.g. "de_DE, en, fr_CA"
	 * @return the corresponding locale
	 */
	private Locale[] getLocales( String definitionsStr ) {
		String[] definitions = StringUtil.splitAndTrim(definitionsStr, ',');
		Locale[] myLocales = new Locale[ definitions.length ];
		for (int i = 0; i < definitions.length; i++) {
			String definition = definitions[i];
			int countryStart = definition.indexOf('_');
			if (countryStart == -1) {
				countryStart = definition.indexOf('-');
			}
			if (countryStart == -1) {
				myLocales[i] = new Locale( definition );
			} else {
				String language = definition.substring( 0, countryStart );
				String country = definition.substring( countryStart + 1 );
				myLocales[i] = new Locale( language, country );
			}
		}
		return myLocales;
	}

	public String toString() {
		if (this.locale == null) {
			return super.toString();
		}
		return this.locale.toString();
	}

}
