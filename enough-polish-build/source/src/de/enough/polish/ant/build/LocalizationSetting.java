/*
 * Created on 09-Sep-2004 at 17:48:58.
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
package de.enough.polish.ant.build;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.enough.polish.BuildException;

import de.enough.polish.BooleanEvaluator;
import de.enough.polish.Environment;
import de.enough.polish.ant.Setting;
import de.enough.polish.util.StringUtil;

/**
 * <p>Stores the localization settings.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        09-Sep-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class LocalizationSetting extends Setting {
	
	private String messages = "messages.txt";
	private String messagesLater = "messages_external.txt";
	private boolean includeAllLocales;
	private final ArrayList supportedLocales;
	private final Map localesByName;
	private boolean dynamic;
	private LocaleSetting defaultLocale;
	private String preprocessorClassName;
	private String translationManagerClassName;
	


	/**
	 * Creates an empty setting
	 */
	public LocalizationSetting() {
		super();
		this.supportedLocales = new ArrayList();
		this.localesByName = new HashMap();
	}
	
	/**
	 * Adds a specific setting for a locale, this can be used for allowing different locale dependent encodings, for example.
	 * @param setting the setting
	 */
	public void addConfiguredLocaleSetting( LocaleSetting setting ) {
		Locale[] locales = setting.getLocales();
		if (locales == null) {
			throw new BuildException("Invalid <localesetting> definition - each <localesetting> requires the \"locale\" attribute.");
		}
		if (locales.length == 1) {
			add( setting );
		} else {
			for (int i = 0; i < locales.length; i++) {
				Locale locale = locales[i];
				add( new LocaleSetting( locale, setting ) );
			}
		}
	}
	/**
	 * Adds a specific setting for a locale, this can be used for allowing different locale dependent encodings, for example.
	 * @param setting the setting
	 */
	private void add(LocaleSetting setting) {
		LocaleSetting existingSetting = (LocaleSetting) this.localesByName.get( setting.getLocale().toString() );
		if ( existingSetting != null && (setting.getCondition() == null || (existingSetting.getCondition() == null)) ) {
			throw new BuildException("The locale " + setting.getLocale() + " has been defined several times, at least one time without a condition. Check your <localization> and <localesetting> elements.");
		}
		this.supportedLocales.add( setting );
	}

	/**
	 * Adds a specific setting for a locale, this can be used for allowing different locale dependent encodings, for example.
	 * @param setting the setting
	 */
	public void addConfiguredLocalesetting( LocaleSetting setting ) {
		addConfiguredLocaleSetting(setting);
	}
	
	/**
	 * Adds a specific setting for a locale, this can be used for allowing different locale dependent encodings, for example.
	 * @param setting the setting
	 */
	public void addConfiguredLocale( LocaleSetting setting ) {
		addConfiguredLocaleSetting(setting);
	}
	
	/**
	 * Retrieves all supported locales.
	 * This will return null when all found locales should be included.
	 * 
	 * @param env the environment
	 * @return Returns the supported locales.
	 */
	public LocaleSetting[] getSupportedLocales(Environment env) {
		BooleanEvaluator evaluator = env.getBooleanEvaluator();
		ArrayList locales = new ArrayList( this.supportedLocales.size() );
		for ( int i=0; i<this.supportedLocales.size(); i++ ) {
			LocaleSetting setting = (LocaleSetting) this.supportedLocales.get(i);
			if (setting.isActive(evaluator)) {
				locales.add( setting );
			}
		}
		return (LocaleSetting[]) locales.toArray( new LocaleSetting[ locales.size() ] );
	}
	
	/**
	 * Sets the locales which should be supported during this build.
	 * 
	 * @param supportedLocalesStr The locales which should be supported or "*" for all found locales.
	 */
	public void setLocales(String supportedLocalesStr) {
		if ("*".equals(supportedLocalesStr)) {
			this.includeAllLocales = true;
		} else {
			String[] localeDefs = StringUtil.splitAndTrim( supportedLocalesStr, ',' );
			//LocaleSetting[] locales = new LocaleSetting[ localeDefs.length ];
			for (int i = 0; i < localeDefs.length; i++) {
				String localeDefinition = localeDefs[i];
				addConfiguredLocaleSetting( new LocaleSetting( localeDefinition ) );				
			}
		}
	}
	
	/**
	 * Sets a single locale
	 * 
	 * @param locale the supported locale
	 */
	public void setLocale( String locale ) {
		setLocales(locale);
	}
	
	/**
	 * Determines whether all found locales should be included.
	 * 
	 * @return Returns true when all found locales should be included.
	 */
	public boolean includeAllLocales() {
		return this.includeAllLocales;
	}	
	
	/**
	 * Sets the name of messages-files.
	 * The default name is "messages.txt".
	 * 
	 * @param messages the name of files containing the messages.
	 */
	public void setMessages( String messages ) {
		this.messages = messages;
	}
	
	/**
	 * Retrieves the file-name containing the messages for localizations.
	 * 
	 * @return "messages.txt" or similar
	 */
	public String getMessagesFileName() {
		return this.messages;
	}
	
	/**
	 * Sets the name of messages-files which can be loaded later after application start, e.g. by downloading the dist/en.loc via HTTP later onwards.
	 * The default name is "messages_external.txt".
	 * 
	 * @param messages the name of files containing the messages.
	 */
	public void setExternalMessages( String messages ) {
		this.messagesLater = messages;
	}
	
	/**
	 * Retrieves the file-name containing the messages for localizations which should be loaded at a later stage.
	 * 
	 * @return "messages_external.txt" or similar
	 */
	public String getExternalMessagesFileName() {
		return this.messagesLater;
	}
	
	/**
	 * Determines whether dynamic translations should be supported.
	 * Dynamic translations can be changed during runtime with the Locale.loadTranslations(...)-method.
	 * 
	 * @return true when dynamic translations should be supported.
	 */
	public boolean isDynamic() {
		return this.dynamic;
	}
	
	/**
	 * Defines whether dynamic translations should be supported.
	 * Dynamic translations can be changed during runtime with the Locale.loadTranslations(...)-method.
	 * 
	 * @param dynamic true when dynamic translations should be supported.
	 */
	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}
	


	/**
	 * Sets the default locale.
	 * This is the locale that is loaded upon start of the application by the de.enough.polish.util.Locale class.
	 * 
	 * @param locale the default locale, e.g. "en_US"
	 * @see #setDefaultLocale(String)
	 */
	public void setDefault( String locale ) {
		setDefaultLocale(locale);
	}

	/**
	 * Sets the default locale.
	 * This is the locale that is loaded upon start of the application by the de.enough.polish.util.Locale class.
	 * 
	 * @param locale the default locale, e.g. "en_US"
	 */
	public void setDefaultLocale( String locale ) {
		this.defaultLocale = new LocaleSetting( locale );
	}
	
	/**
	 * Retrieves the default locale.
	 * This call makes only sense when dynamic locales are used.
	 * 
	 * @return the default locale
	 */
	public LocaleSetting getDefaultLocale() {
		if (this.defaultLocale != null) {
			return this.defaultLocale;
		} else if (this.supportedLocales.size() == 1){
			return (LocaleSetting) this.supportedLocales.get(0);
		} else if (this.supportedLocales.size() == 0){
			throw new BuildException("No locales are defined - check your <localization> setting.");
		} else {
			Locale locale = Locale.getDefault();
			for (int i = 0; i < this.supportedLocales.size(); i++) {
				LocaleSetting supportedLocale = (LocaleSetting) this.supportedLocales.get(i);
				if (locale.equals( supportedLocale.getLocale() )) {
					return supportedLocale;
				}
			}
			return (LocaleSetting) this.supportedLocales.get(0);
		}
	}

	/**
	 * Retrieves the name of the preprocessor class.
	 * 
	 * @return the name of the preprocessor class
	 */
	public String getPreprocessorClassName() {
		if (this.preprocessorClassName == null) {
			return "de.enough.polish.preprocess.custom.TranslationPreprocessor";
		} else {
			return this.preprocessorClassName;
		}
	}
	
	/**
	 * Sets the name of the preprocessor class.
	 * 
	 * @param preprocessorClassName the class name of the preprocessor
	 */
	public void setPreprocessor( String preprocessorClassName ) {
		this.preprocessorClassName = preprocessorClassName;
	}
	
	/**
	 * Retrieves the name of the translation manager class.
	 * 
	 * @return the name of the translation manager class
	 */
	public String getTranslationManagerClassName() {
		return this.translationManagerClassName;
	}
	
	/**
	 * Sets the name of the translation manager class.
	 * 
	 * @param translationManagerClassName the class name of the translation manager 
	 */
	public void setTranslationManager( String translationManagerClassName ) {
		this.translationManagerClassName = translationManagerClassName;
	}

	/**
	 * Retrieves all supported locale as a comma separated string, e.g. "de,en,fr"
	 * @param env the environment
	 * @return all supported locale as a comma separated string
	 */
	public String getSupportedLocalesAsString( Environment env ) {
		StringBuffer buffer = new StringBuffer();
		if (this.defaultLocale == null) {
			this.defaultLocale = getDefaultLocale();
		}
		buffer.append( this.defaultLocale.toString() );
		LocaleSetting[] settings = getSupportedLocales(env);
		for (int i = 0; i < settings.length; i++) {
			LocaleSetting setting = settings[i];
			if (!setting.getLocale().equals( this.defaultLocale.getLocale() )) {
				buffer.append(",").append( setting.toString() );
			}
		}
		return buffer.toString();
	}

	/**
	 * Checks if this localization setting is valid
	 * @return true when it is valid
	 */
	public boolean isValid() {
		if (!this.includeAllLocales && this.supportedLocales.size() == 0) {
			return false;
		} else {
			return true;
		}
	}

}
 