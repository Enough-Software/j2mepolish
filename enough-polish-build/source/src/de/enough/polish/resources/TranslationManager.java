/*
 * Created on 12-Sep-2004 at 13:10:19.
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
package de.enough.polish.resources;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import de.enough.polish.BuildException;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.build.LocaleSetting;
import de.enough.polish.ant.build.LocalizationSetting;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.IntegerIdGenerator;
import de.enough.polish.util.Native2Ascii;
import de.enough.polish.util.PropertyUtil;
import de.enough.polish.util.ResourceUtil;
import de.enough.polish.util.StringList;
import de.enough.polish.util.StringUtil;

/**
 * <p>Manages translations.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        12-Sep-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TranslationManager
implements Comparator
{
	/** key for retrieving the translation manager from the Environment */
	public static final String ENVIRONMENT_KEY = "polish.TranslationManager";
	protected final Map translationsByKey;
	protected final Map preprocessingVariablesByKey;
	protected final LocaleSetting locale;
	protected final Device device;
	protected final LocalizationSetting localizationSetting;
	protected long lastModificationTime;
	protected final boolean isDynamic;
	protected final ArrayList plainTranslations;
	protected final ArrayList singleParameterTranslations;
	protected final ArrayList multipleParametersTranslations;
	protected IntegerIdGenerator idGeneratorPlain;
	protected IntegerIdGenerator idGeneratorSingleParameter;
	protected IntegerIdGenerator idGeneratorMultipleParameters;
	protected ArrayList plainTranslationsExternal;
	protected ArrayList singleParameterTranslationsExternal;
	protected ArrayList multipleParametersTranslationsExternal;
	private IntegerIdGenerator idGeneratorMultipleParametersExternal;
	private IntegerIdGenerator idGeneratorSingleParameterExternal;
	private IntegerIdGenerator idGeneratorPlainExternal;
	protected final Environment environment;

	/**
	 * Creates a new manager for translations.
	 * 
	 * @param device the current device
	 * @param locale the current locale
	 * @param environment the environment
	 * @param resourceDirs the directories containing resources for the given device
	 * @param localizationSetting the localization setting
	 * @throws IOException when resources could not be loaded
	 */
	public TranslationManager(Device device, LocaleSetting locale, Environment environment, File[] resourceDirs, LocalizationSetting localizationSetting )
	throws IOException
	{
		this.localizationSetting = localizationSetting;
		this.isDynamic = localizationSetting == null ? false : localizationSetting.isDynamic();
		this.locale = locale;
		this.device = device;
		this.environment = environment;
		this.translationsByKey = new HashMap();
		this.preprocessingVariablesByKey = new HashMap();
		this.multipleParametersTranslations = new ArrayList();
		this.singleParameterTranslations = new ArrayList();
		this.plainTranslations = new ArrayList();
		//System.out.println("loading raw translations");
		Map rawTranslations = loadRawTranslations(resourceDirs, localizationSetting.getMessagesFileName(), false);
		Map externalRawTranslations = null;
		// load IDs for variables with multiple parameters or when dynamic translations are used:
		//System.out.println("loading ids");
		loadIdsMap( false );
		if (this.isDynamic) {
			// load translations that can be loaded at a later stage, e.g. via HTTP:
			//System.out.println("loading external messages");
			externalRawTranslations = loadRawTranslations(resourceDirs, localizationSetting.getExternalMessagesFileName(), true);
			if (externalRawTranslations.size() == 0) {
				externalRawTranslations = null;
			} else {
				environment.addSymbol("polish.i18n.useExternalTranslations");
				loadIdsMap( true );
				this.plainTranslationsExternal = new ArrayList();
				this.singleParameterTranslationsExternal = new ArrayList();
				this.multipleParametersTranslationsExternal = new ArrayList();
			}
		}
		if (externalRawTranslations == null) {
			processRawTranslations( rawTranslations, this.plainTranslations, this.singleParameterTranslations, this.multipleParametersTranslations, this.idGeneratorPlain, this.idGeneratorSingleParameter, this.idGeneratorMultipleParameters );
		} else {
			// there are external translations - now find out translations that are used both internally as well as externally
			// and ensure that they use the very same lookup IDs:
			HashMap sharedInternalTranslations = new HashMap();
			HashMap sharedExternalTranslations = new HashMap();
			ArrayList sharedTranslations = new ArrayList();
			Object[] internalKeys = rawTranslations.keySet().toArray();
			for (int i = 0; i < internalKeys.length; i++)
			{
				String internalKey = (String) internalKeys[i];
				Object externalValue = externalRawTranslations.get(internalKey); 
				if (externalValue != null) {
					sharedInternalTranslations.put( internalKey, rawTranslations.get(internalKey) );
					sharedExternalTranslations.put( internalKey, externalValue );
					rawTranslations.remove(internalKey);
					externalRawTranslations.remove(internalKey);
					if (internalKey.startsWith("var:")) {
						internalKey = internalKey.substring("var:".length() );
					} else if (internalKey.startsWith("variable:")) {
						internalKey = internalKey.substring("variable:".length() );
					}
					sharedTranslations.add( internalKey );
				}
			}
			//System.out.println("found " + sharedTranslations.size() + " shared translations");
			// process shared keys:
			//System.out.println("process raw translations");
			processRawTranslations( sharedInternalTranslations, this.plainTranslations, this.singleParameterTranslations, this.multipleParametersTranslations, this.idGeneratorPlain, this.idGeneratorSingleParameter, this.idGeneratorMultipleParameters );
			for (Iterator iter = sharedTranslations.iterator(); iter.hasNext();)
			{
				String sharedKey = (String) iter.next();
				int id = this.idGeneratorPlain.getId(sharedKey, false );
				if (id != -1) {
					int externalId = this.idGeneratorPlainExternal.getId(sharedKey, false);
					if (externalId == -1) {
						this.idGeneratorPlainExternal.addId(sharedKey, id);
					} else if (externalId != id) {
						throw new BuildException("Invalid translations setup: Shared key \"" + sharedKey + "\" used different IDs. Please remove all .polishSettings/LocaleIds*.txt and restart the build.");
					}
				} else {
					id = this.idGeneratorSingleParameter.getId(sharedKey, false );
					if (id != -1) {
						int externalId = this.idGeneratorSingleParameterExternal.getId(sharedKey, false);
						if (externalId == -1) {
							this.idGeneratorSingleParameterExternal.addId(sharedKey, id);
						} else if (externalId != id) {
							throw new BuildException("Invalid translations setup: Shared key \"" + sharedKey + "\" used different IDs. Please remove all .polishSettings/LocaleIds*.txt and restart the build.");
						}
					} else {
						id = this.idGeneratorMultipleParameters.getId(sharedKey, false );
						if (id != -1) {
							int externalId = this.idGeneratorMultipleParametersExternal.getId(sharedKey, false);
							if (externalId == -1) {
								this.idGeneratorMultipleParametersExternal.addId(sharedKey, id);
							} else if (externalId != id) {
								throw new BuildException("Invalid translations setup: Shared key \"" + sharedKey + "\" used different IDs. Please remove all .polishSettings/LocaleIds*.txt and restart the build.");
							}
						} else {
							System.out.println("Warning: your translation setup might be inconsistent: Shared key \"" + sharedKey + "\" did not obtain an ID. In case of problems please remove all .polishSettings/LocaleIds*.txt and restart the build.");
							//throw new BuildException("Invalid translations setup: Shared key \"" + sharedKey + "\" was not processed at all: please remove all .polishSettings/LocaleIds*.txt and restart the build.");
						}
					}
				}
			}
			//System.out.println("processing externally shared translations");
			processRawTranslations( sharedExternalTranslations, this.plainTranslationsExternal, this.singleParameterTranslationsExternal, this.multipleParametersTranslationsExternal, this.idGeneratorPlainExternal, this.idGeneratorSingleParameterExternal, this.idGeneratorMultipleParametersExternal );
			// process all other keys:
			//System.out.println("processing raw translations internal: " + rawTranslations.size() );
			processRawTranslations( rawTranslations, this.plainTranslations, this.singleParameterTranslations, this.multipleParametersTranslations, this.idGeneratorPlain, this.idGeneratorSingleParameter, this.idGeneratorMultipleParameters );
			//System.out.println("processing raw translations external: "+ externalRawTranslations.size() );
			processRawTranslations( externalRawTranslations, this.plainTranslationsExternal, this.singleParameterTranslationsExternal, this.multipleParametersTranslationsExternal, this.idGeneratorPlainExternal, this.idGeneratorSingleParameterExternal, this.idGeneratorMultipleParametersExternal );
		}
		//System.out.println("done handling external translations");
		
		// now set DateFormat variables according to current locale:
		String dateFormat = environment.getVariable("polish.DateFormat"); 
		if ( dateFormat != null) {
			// maybe the dateformat needs to be changed:
			if (dateFormat.length() == 2) {
				String emptyText = environment.getVariable("polish.DateFormatEmptyText");
				String separator = environment.getVariable("polish.DateFormatSeparator");
				if ("YYYY-MM-DD".equals(emptyText)) {
					emptyText = null;
				}
				if ("de".equals(dateFormat)) {
					dateFormat = "dmy";
					if (separator == null) {
						separator = ".";
					}
					if (emptyText == null) {
						emptyText = "TT.MM.JJJJ";
					}
				} else if ("fr".equals(dateFormat)) {
					dateFormat = "dmy";
					if (separator == null) {
						separator = "/";
					}
					if (emptyText == null) {
						emptyText = "JJ/MM/AAAA";
					}
				} else if ("us".equals(dateFormat)) {
					dateFormat = "mdy";
					if (separator == null) {
						separator = "-";
					}
					if (emptyText == null) {
						emptyText = "MM-DD-YYYY";
					}
				}
				if (emptyText == null) {
					emptyText = "YYYY-MM-DD";
				} else {
					Translation translation = getTranslation("polish.DateFormatEmptyText");
					if (translation != null) {
						translation.setValue(emptyText);
					}
					this.preprocessingVariablesByKey.put("polish.DateFormatEmptyText", emptyText);
				}
				environment.addVariable( "polish.DateFormat", dateFormat );
				environment.addVariable( "polish.DateFormatSeparator", separator );
				environment.addVariable( "polish.DateFormatEmptyText", emptyText );
			}
		} else {
			String language = locale.getLocale().getLanguage();
			if ("de".equals(language)) {
				environment.addVariable( "polish.DateFormat", "dmy" );
				environment.addVariable( "polish.DateFormatSeparator", "." );
				environment.addVariable( "polish.DateFormatEmptyText", "TT.MM.JJJJ" );
			} else if ("fr".equals(language)) {
				environment.addVariable( "polish.DateFormat", "dmy" );
				environment.addVariable( "polish.DateFormatSeparator", "." );
				environment.addVariable( "polish.DateFormatEmptyText", "JJ/MM/AAAA" );
			} else {
				String country = locale.getLocale().getCountry();
				if ("US".equals(country)) {
					environment.addVariable( "polish.DateFormat", "mdy" );
					environment.addVariable( "polish.DateFormatSeparator", "-" );
					environment.addVariable( "polish.DateFormatEmptyText", "MM-DD-YYYY" );
				}
			}
		}
		// register manager at the environment:
		environment.set( ENVIRONMENT_KEY, this );
	}

	/**
	 * Retrieves the file for storing IDs of translations with multiple parameters.
	 * 
	 * @param isForLaterTranslations true when the id generators should be loaded for later phases
	 * @return the file that stores those IDs
	 */
	protected File getMultipleParametersIdsFile(boolean isForLaterTranslations) {
		if (isForLaterTranslations) {
			return new File( this.environment.getProjectHome(), ".polishSettings/LocaleIdsMultipleExternal.txt"); // this.device.getBaseDir() + File.separator + "LocaleIds_" + this.locale.toString() + ".txt");
		} else {
			return new File( this.environment.getProjectHome(), ".polishSettings/LocaleIdsMultiple.txt"); // this.device.getBaseDir() + File.separator + "LocaleIds_" + this.locale.toString() + ".txt");
		}
	}

	/**
	 * Retrieves the file for storing IDs of translations with one parameter.
	 * 
	 * @param isForLaterTranslations true when the id generators should be loaded for later phases
	 * @return the file that stores those IDs
	 */
	protected File getSingleParameterIdsFile(boolean isForLaterTranslations) {
		if (isForLaterTranslations) {
			return new File( this.environment.getProjectHome(), ".polishSettings/LocaleIdsSingleExternal.txt");			
		} else {
			return new File( this.environment.getProjectHome(), ".polishSettings/LocaleIdsSingle.txt");
		}
		//return new File( this.device.getBaseDir() + File.separator + "SingleLocaleIds_" + this.locale.toString() + ".txt");
	}

	/**
	 * Retrieves the file for storing IDs of translations with no parameters.
	 * 
	 * @param isForLaterTranslations true when the id generators should be loaded for later phases
	 * @return the file that stores those IDs
	 */
	protected File getPlainIdsFile(boolean isForLaterTranslations) {
		if (isForLaterTranslations) {
			return new File( this.environment.getProjectHome(), ".polishSettings/LocaleIdsPlainExternal.txt");			
		} else {
			return new File( this.environment.getProjectHome(), ".polishSettings/LocaleIdsPlain.txt");
		}
		//return new File( this.device.getBaseDir() + File.separator + "PlainLocaleIds_" + this.locale.toString() + ".txt");
	}
	
	/**
	 * Saves the IDs-map for the complex translations to disk.
	 * When dynamic translations are used, also IDs of plain and single-parameter translations are saved, too.
	 * 
	 * @throws IOException when the file(s) could not be written
	 */
	public void writeIdsMaps()
	throws IOException
	{
		writeIdsMap(false);
		if (this.plainTranslationsExternal != null) {
			writeIdsMap(true);
		}
	}

	/**
	 * Saves the IDs-map for the complex translations to disk.
	 * When dynamic translations are used, also IDs of plain and single-parameter translations are saved, too.
	 * 
	 * @param isForExternalTranslations true when the id generators should be loaded for later phases
	 * @throws IOException when the file(s) could not be written
	 */
	public void writeIdsMap(boolean isForExternalTranslations)
	throws IOException
	{
		File idsFile = getMultipleParametersIdsFile(isForExternalTranslations);
		if (isForExternalTranslations) {
			writeIdMap( idsFile, this.idGeneratorMultipleParametersExternal );
		} else {
			writeIdMap( idsFile, this.idGeneratorMultipleParameters );
		}
		if (this.isDynamic) {
			idsFile = getPlainIdsFile(isForExternalTranslations);
			if (isForExternalTranslations) {
				writeIdMap( idsFile, this.idGeneratorPlainExternal );
			} else {
				writeIdMap( idsFile, this.idGeneratorPlain );
			}
			idsFile = getSingleParameterIdsFile(isForExternalTranslations);
			if (isForExternalTranslations) {
				writeIdMap( idsFile, this.idGeneratorSingleParameterExternal );
			} else {
				writeIdMap( idsFile, this.idGeneratorSingleParameter );
			}
		}
	}
	
	/**
	 * Writes a single IDs map to a file
	 * @param idsFile
	 * @param idGenerator
	 * @throws IOException 
	 */
	private void writeIdMap(File idsFile, IntegerIdGenerator idGenerator) throws IOException
	{
		if (idGenerator.hasChanged()) {
			FileUtil.writePropertiesFile(idsFile, idGenerator.getIdsMap() );
		}
	}

	/**
	 * Loads the IDs-map for the complex translations to disk.
	 * When dynamic translations are used, also IDs of plain and single-parameter translations are loaded as well.
	 * 
	 * @param isForLaterTranslations true when the id generators should be loaded for later phases
	 * @throws IOException when the file(s) could not be read even though they exist
	 */
	public void loadIdsMap(boolean isForLaterTranslations)
	throws IOException
	{
		File idsFile = getMultipleParametersIdsFile(isForLaterTranslations);
		Map idsMap = new HashMap();
		if (idsFile.exists()) {
			FileUtil.readPropertiesFile(idsFile, '=', idsMap );
		}
		if (isForLaterTranslations) {
			this.idGeneratorMultipleParametersExternal = new IntegerIdGenerator( idsMap );
		} else {
			this.idGeneratorMultipleParameters = new IntegerIdGenerator( idsMap );
		}
		if (this.isDynamic) {
			// load IDs for variables with one parameter:
			idsFile = getSingleParameterIdsFile(isForLaterTranslations);
			idsMap = new HashMap();
			if (idsFile.exists()) {
				FileUtil.readPropertiesFile(idsFile, '=', idsMap );
			}
			if (isForLaterTranslations) {
				this.idGeneratorSingleParameterExternal = new IntegerIdGenerator( idsMap );
			} else {
				this.idGeneratorSingleParameter = new IntegerIdGenerator( idsMap );
			}
			// load IDs for variables with no parameters:
			idsFile = getPlainIdsFile(isForLaterTranslations);
			idsMap = new HashMap();
			if (idsFile.exists()) {
				FileUtil.readPropertiesFile(idsFile, '=', idsMap );
			}
			if (isForLaterTranslations) {
				this.idGeneratorPlainExternal = new IntegerIdGenerator( idsMap );
			} else {
				this.idGeneratorPlain = new IntegerIdGenerator( idsMap );
			}
		}
	}
	
	
	/**
	 * Processes the raw translations: create Translation-instances and insert variable-values.
	 * 
	 * @param rawTranslations the translations
	 */
	private void processRawTranslations(Map rawTranslations, ArrayList translationsPlain, ArrayList translationsSingle, ArrayList translationsMultiple, IntegerIdGenerator idsPlain, IntegerIdGenerator idsSingle, IntegerIdGenerator idsMultiple ) {
		//System.out.println("process raw translations for " + rawTranslations);
		// the processing is actual done in two steps.
		// In the first step all variables are set,
		// in the second steps "ordinary" translations are processed.
		// This is necessary, so that variables which are defined or
		// changed in the messages-file can be used by other translations.
		Map variables = this.environment.getVariables();
		String[] keys = (String[]) rawTranslations.keySet().toArray( new String[ rawTranslations.size()] );
		Arrays.sort( keys );
		// in the first round only set variables:
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			//String originalKey = key;
			String value = (String) rawTranslations.get( key );
			//System.out.println(key + "=" + value);
			if (value.indexOf('$') != -1) {
				value = PropertyUtil.writeProperties(value, variables);
			}
			// process key: when it starts with "var:", "variable:" or "polish.",
			// then add it as a variable to the preprocessor:
			boolean variableFound = false;
			if (key.startsWith("polish.")) {
				variableFound = true;
			} else if (key.startsWith("var:")) {
				rawTranslations.remove(key);
				key = key.substring( "var:".length() );
				variableFound = true;
				//if (key.startsWith("polish.")) {
					rawTranslations.put(key, value);
				//}
			} else if (key.startsWith("variable:")) {
				rawTranslations.remove(key);
				key = key.substring( "variable:".length() );
				variableFound = true;
				//if (key.startsWith("polish.")) {
					rawTranslations.put(key, value);
				//}
			} else if (key.startsWith("MIDlet-")) {
				rawTranslations.remove(key);
				variableFound = true;
			}
			if ( variableFound ) {
				//System.out.println("found variable: [" + key + "=" + value + "]" );
				this.environment.addVariable(key, value );
				this.preprocessingVariablesByKey.put( key, value );
			}
		}
		// in the second round set the actual translations as well:
		keys = (String[]) rawTranslations.keySet().toArray( new String[ rawTranslations.size()] );
		Arrays.sort( keys );
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			String value = (String) rawTranslations.get( key );
			if (value.indexOf('$') != -1) {
				value = PropertyUtil.writeProperties(value, variables);
			}
			// create final translation:
			Translation translation = new Translation( key, value, 
						this.isDynamic, idsPlain, idsSingle, idsMultiple );
			this.translationsByKey.put( key, translation );
			if (translation.hasSeveralParameters()) {
				translationsMultiple.add( translation );
			} else if (translation.hasOneParameter()) {
				translationsSingle.add( translation );
			} else {
				translationsPlain.add( translation );
			}
		}
		//TODO 2011-07-04: check whether this code is a) really necessary and b) really working:
		if (this.isDynamic) {
			// check if any translations have been removed - in that case empty strings need to be included,
			// otherwise the dynamic translations are not backwards compatible anymore:
			if (translationsPlain.size() < idsPlain.getIdsMap().size()) {
				//System.out.println("it appears that a translation is not used anymore: there are " + idsPlain.getIdsMap().size() + " IDs but only " + translationsPlain.size() + " translations.");
				Translation[] translations = getTranslations( translationsPlain );
				int index = 1;
				for (int i = 0; i < translations.length; i++)
				{
					Translation translation = translations[i];
					while (translation.getId() != index) {
						String key = idsPlain.getKey(index);
						if (key == null) {
							//System.out.println("Plain IDs are: " + idsPlain.getIdsMap());
							throw new BuildException("Error while auto-conceiling with unused translation with ID=" + translation.getId() + " at index=" + index  + ": " +  translation.getKey() + "=" + translation.getQuotedValue() + " in language " + this.environment.getVariable("polish.locale") + ". Please remove ${project.home}/.polishSettings and restart J2ME Polish.");
						}
						//System.out.println("adding empty translation for " + index + "=" + key);
						Translation plain = new Translation(key, "", index);
						this.translationsByKey.put(key, plain);
						translationsPlain.add(plain);
						index++;
					}
					index++;
				}
			}
			if (translationsSingle.size() < idsSingle.getIdsMap().size()) {
				Translation[] translations = getTranslations(translationsSingle);
				int index = 0;
				for (int i = 0; i < translations.length; i++)
				{
					Translation translation = translations[i];
					while (translation.getId() != (index+1)) {
						String key = idsSingle.getKey(index);
						Translation single = new Translation(key, "", "", index);
						this.translationsByKey.put(key, single);
						translationsSingle.add(single);
						index++;
					}
					index++;
				}
			}
			if (translationsMultiple.size() < idsMultiple.getIdsMap().size()) {
				//System.out.println("there are more multiple translation IDs than translations");
				Translation[] translations = getTranslations(translationsMultiple);
				int index = 0;
				for (int i = 0; i < translations.length; i++)
				{
					Translation translation = translations[i];
					while (translation.getId() != (index+1)) {
						String key = idsMultiple.getKey(index+1);
						//System.out.println("found former key " + key + " at index " + 0);
						Translation multiple = new Translation(key, (String[]) null,  (int[])null, index);
						this.translationsByKey.put(key, multiple);
						translationsMultiple.add(multiple);
						index++;
					}
					index++;
				}
			}

//			System.out.println("\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n");
//			Translation[] translations = getPlainTranslations();
//			for (int i = 0; i < translations.length; i++)
//			{
//				Translation translation = translations[i];
//				System.out.println( (i+1) + "=" + translation.getId() + "=" + translation.getKey() );
//			}
		}
	}

	/**
	 * Loads the raw translation-messages for the given device and locale.
	 * 
	 * @param resourceDirs the files containing the resources.
	 * @param messagesFileName the file name of messages.txt
	 * @return a map containing all translations
	 * @throws IOException when a translations-file could not be loaded
	 */
	private Map loadRawTranslations( File[] resourceDirs, String messagesFileName, boolean isExtended ) throws IOException {
		//System.out.println("Loading translations for locale " + this.locale.getLocale() + ", messages: " + messagesFileName );
		// load the translations by following scheme:
		// first load the base-translations:
		// resources
		// resources/vendor
		// resources/group1..n
		// resources/vendor/device
		
		// Then load the locale specific resources:
		// resources/locale
		// resources/vendor/locale
		// resources/group1..n/locale
		// resources/vendor/device/locale

		Map rawTranslations = new HashMap();
		
		// load general resources:
		//String messagesFileName = File.separator + this.localizationSetting.getMessagesFileName();
		if (messagesFileName.charAt(0) != File.separatorChar) {
			messagesFileName = File.separatorChar + messagesFileName;
		}
		String localeFileName;
		int splitPos = messagesFileName.lastIndexOf('.');
		if (splitPos != -1) {
			localeFileName = messagesFileName.substring(0, splitPos)
				+ "_"
				+ this.locale.toString()
				+ messagesFileName.substring(splitPos);
		} else {
			localeFileName = messagesFileName + this.locale.toString();
		}
		String languageFileName = null;
		if (this.locale.getLocale().getCountry().length() > 0) {
			// okay, this locale has also a country defined,
			// so we need to look at the language-resources as well:
			if (splitPos != -1) {
				languageFileName = messagesFileName.substring(0, splitPos)
					+ "_"
					+ this.locale.getLocale().getLanguage()
					+ messagesFileName.substring(splitPos);
			} else {
				languageFileName = messagesFileName + this.locale.getLocale().getLanguage();
			}
		}
		for (int i = 0; i < resourceDirs.length; i++) {
			File dir = resourceDirs[i];
			String dirPath = dir.getAbsolutePath();
			File messagesFile = new File( dirPath + messagesFileName );
			if (messagesFile.exists()) {
				//System.out.println("Loading translations from " + messagesFile.getAbsolutePath() );
				if (messagesFile.lastModified() > this.lastModificationTime) {
					this.lastModificationTime = messagesFile.lastModified();
				}
				//System.out.println("Reading translations from " + messagesFile.getAbsolutePath() );
				readPropertiesFile(messagesFile, rawTranslations, this.locale.getEncoding() );
			}
			if (languageFileName != null) {
				messagesFile = new File( dirPath + languageFileName );
				if (messagesFile.exists()) {
					//System.out.println("Loading translations from " + messagesFile.getAbsolutePath() );
					if (messagesFile.lastModified() > this.lastModificationTime) {
						this.lastModificationTime = messagesFile.lastModified();
					}
					//System.out.println("Reading translations from " + messagesFile.getAbsolutePath() );
					readPropertiesFile(messagesFile, rawTranslations, this.locale.getEncoding() );
				}
			}
			messagesFile = new File( dirPath + localeFileName );
			if (messagesFile.exists()) {
				//System.out.println("Loading translations from " + messagesFile.getAbsolutePath() );
				if (messagesFile.lastModified() > this.lastModificationTime) {
					this.lastModificationTime = messagesFile.lastModified();
				}
				//System.out.println("Reading translations from " + messagesFile.getAbsolutePath() );
				readPropertiesFile(messagesFile, rawTranslations, this.locale.getEncoding() );
			}
		}
		
		if (!isExtended || rawTranslations.size() > 0) {
			// load GUI translations when dynamic translations:
			Map guiTranslations = new HashMap();
			ResourceUtil resourceUtil = new ResourceUtil( getClass().getClassLoader() );
			InputStream in = resourceUtil.open( this.environment.getVariable("polish.home"), "translations.txt");
			readProperties(in, guiTranslations, null);
			in.close();
			//System.out.println( this.locale + ": read " + rawTranslations.size() + " translations:");
			Object[] keys = guiTranslations.keySet().toArray();
			for (int i = 0; i < keys.length; i++) {
				String key = (String) keys[i];
				if ( (rawTranslations.get(key) != null)
					||(rawTranslations.get("var:" + key) != null)
					||(rawTranslations.get("variable:" + key) != null)) {
					// don't add this translation
					guiTranslations.remove(key);
				} else {
					Object value = this.environment.getVariable( key );
					if (value != null) {
						// user-defined variables have priority over these settings:
						guiTranslations.put( key, value );
					}
					//System.out.println(key + "=" + rawTranslations.get( key ));
				}
			}
			rawTranslations.putAll(guiTranslations);
		}
		
		return rawTranslations;
	}
	
	private void readPropertiesFile( File messagesFile, Map rawTranslations, String encoding ) 
	throws FileNotFoundException, IOException 
	{
		//System.out.println("Reading properties from file " + messagesFile.getAbsolutePath() );
		InputStream in = new FileInputStream( messagesFile );
		readProperties( in, rawTranslations, encoding );
		in.close();
	}
	
	private void readProperties( InputStream in, Map rawTranslations, String encoding ) 
	throws FileNotFoundException, IOException 
	{
		boolean translateToAscii = (encoding != null) && !this.isDynamic;
		boolean translateToNative = this.isDynamic;
		FileUtil.readProperties(in, '=', rawTranslations, encoding, translateToAscii, translateToNative );
	}

	
	/**
	 * Retrieves a translation for the given key.
	 * 
	 * @param key the key , e.g. "labels.GameStart"
	 * @return the translation for the given key or null when the translation was not found.
	 */
	public Translation getTranslation( String key ) {
		return (Translation) this.translationsByKey.get( key );
	}
	
	/**
	 * Gets the latest time when one of the basic messages-files has been modified.
	 *  
	 * @return a long containing the last modification time
	 */
	public long getLastModificationTime() {
		return this.lastModificationTime;
	}

	/**
	 * Inserts any required localization settings into the Locale.java class.
	 *  
	 * @param code the source code of Locale.java
	 */
	public void processLocaleCode(StringList code) {
		if (this.environment.hasSymbol("polish.LibraryBuild")) {
			// ignore when the J2ME Polish Client library is build:
			return;
		}
		System.out.println("processing locale code... ");
		code.reset();
		boolean insertionPointFound = false;
		while (code.next()) {
			String line = code.getCurrent();
			if ("//$$IncludeLocaleDefinitionHere$$//".equals(line)) {
				insertionPointFound = true;
				// instantiate translations directly:
				if (!this.isDynamic) {
					insertMultipleParametersTranslations( code );
				}
				break;
			}
		}
		if (!insertionPointFound) {
			throw new BuildException("Unable to modify [de.enough.polish.util.Locale.java]: insertion point not found!");
		}
		// now include the static local specific fields:
		insertFields( code, !this.isDynamic );
	}
	
//	public void insertDynamicFields(StringList code) {
//		code.reset();
//		boolean insertionPointFound = false;
//		while (code.next()) {
//			String line = code.getCurrent();
//			if ("//$$IncludeLocaleDefinitionHere$$//".equals(line)) {
//				insertionPointFound = true;
//				break;
//			}
//		}
//		if (!insertionPointFound) {
//			throw new BuildException("Unable to modify [de.enough.polish.util.Locale.java]: insertion point not found!");
//		}
//		// now include the static local specific fields:
//		insertFields( code, false );		
//	}


	/**
	 * Inserts the actual code fragments into Locale.java
	 * The static variables String[][] values and in[][] parameterOrders are intialized.
	 * 
	 * @param code the source code, the code should be inserted into the current position.
	 */
	private void insertMultipleParametersTranslations(StringList code) {
		ArrayList lines = new ArrayList();
		Translation[] currentTranslations = getTranslations(this.multipleParametersTranslations);
		int numberOfTranslations = currentTranslations.length;
		if (numberOfTranslations == 0) {
			// no need to init an empty array:
			return; 
		}
		lines.add("\tstatic {");
		lines.add("\t\tmultipleParameterOrders = new short[" + numberOfTranslations + "][];");
		lines.add("\t\tmultipleParameterTranslations = new String[" + numberOfTranslations + "][];");
		for (int i = 0; i < currentTranslations.length; i++) {
			Translation translation = currentTranslations[i];
			lines.add( getMultipleParametersTranslationCode( translation ));
			int[] parameterOrder = translation.getParameterIndices();
			if (!isContinuous(parameterOrder)) {
	 			lines.add( getParameterOrderCode( translation, parameterOrder ) );
			}
		}
		/*
		 * dynamic translations are instantiated during the runtime by
		 * reading them out of a resource file
		if (this.isDynamic) {
			currentTranslations = getPlainTranslations();
			lines.add("\t\tplainTranslations = new String[" + currentTranslations.length + "];");
			for (int i = 0; i < currentTranslations.length; i++) {
				Translation translation = currentTranslations[i];
				lines.add( getTranslationCode( translation ));
			}
			currentTranslations = getSingleParameterTranslations();
			lines.add("\t\tsingleParameterTranslationsStart = new String[" + currentTranslations.length + "];");
			lines.add("\t\tsingleParameterTranslationsEnd = new String[" + currentTranslations.length + "];");
			for (int i = 0; i < currentTranslations.length; i++) {
				Translation translation = currentTranslations[i];
				lines.add( getTranslationCode( translation ));
			}			
		}
		*/
		lines.add("\t} // end of translations");
		String[] codeLines = (String[]) lines.toArray( new String[ lines.size() ] );
		code.insert(codeLines);
	}
	
	/**
	 * Determines whether the given integer-array is continuous.
	 * 
	 * @param parameterOrder the integer-array
	 * @return true only when the give values are {0, 1, 2, 3, .., n } 
	 */
	private boolean isContinuous(int[] parameterOrder) {
		for (int i = 0; i < parameterOrder.length; i++) {
			int j = parameterOrder[i];
			if (j != i) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Inserts the code necessary to instantiate one translation with multiple parameters.
	 * 
	 * @param translation the translation
	 * @return the string containing the necessary code
	 */
	private String getMultipleParametersTranslationCode(Translation translation) {
		StringBuffer code = new StringBuffer();
		if (!translation.hasSeveralParameters()) {
			throw new BuildException("Cannot create code for translation [" + translation.getKey() + "]: please report this error to j2mepolish@enough.de");
		}
		code.append( "\t\tmultipleParameterTranslations[" )
			.append( translation.getId() -1 )
			.append( "] = new String[] {" );
		String[] values = translation.getValueChunks();
		for (int i = 0; i < values.length; i++) {
			String value = values[i];
			code.append( '"' )
				.append( value )
				.append( '"' );
			if (i != values.length -1 ) {
				code.append(',');
			}
		}
		code.append("};");
		/*
		else if (translation.hasOneParameter()) {
			code.append( "\t\tsingleParameterTranslationsStart[" )
				.append( translation.getId() -1 )
				.append( "] = \"" ).append( translation.getOneValueStart() ).append("\";");
			code.append( "\t\tsingleParameterTranslationsEnd[" )
				.append( translation.getId() -1 )
				.append( "] = \"" ).append( translation.getOneValueStart() ).append("\";");
		} else {
			// translation has no parameters:
			code.append( "\t\tplainTranslations[" )
				.append( translation.getId() -1 )
				.append( "] = " ).append( translation.getQuotedValue() ).append(';');
		}
		*/
		return code.toString();
	}

	/**
	 * Gets the code for instantiating the order of the parameters for the specified translation
	 * 
	 * @param translation the translation
	 * @return the code for instantiating the order of the parameters for the specified translation
	 */
	private String getParameterOrderCode(Translation translation, int[] indices) {
		StringBuffer code = new StringBuffer();
		code.append( "\t\tmultipleParameterOrders[" )
			.append( translation.getId() -1 )
			.append( "] = new short[] {" );
		for (int i = 0; i < indices.length; i++) {
			int value = indices[i];
			code.append( value );
			if (i != indices.length -1 ) {
				code.append(',');
			}
		}
		code.append("};");
		return code.toString();
	}

//	private Translation[] getMultipleParametersTranslations() {
//		/*
//		ArrayList list = new ArrayList( this.multipleParametersTranslations.size() );
//		for (Iterator iter = this.multipleParametersTranslations.iterator(); iter.hasNext();) {
//			Translation translation = (Translation) iter.next();
//			if (translation.getId() != -1) {
//				list.add( translation );
//			}
//		}
//		*/
//		Translation[] complexTranslations = (Translation[])  this.multipleParametersTranslations.toArray( new Translation[  this.multipleParametersTranslations.size() ]);
//		Arrays.sort( complexTranslations, this );
//		return complexTranslations;
//	}
//	
//	private Translation[] getSingleParameterTranslations() {
//		Translation[] myTranslations = (Translation[])  this.singleParameterTranslations.toArray( new Translation[  this.singleParameterTranslations.size() ]);
//		Arrays.sort( myTranslations, this );
//		return myTranslations;
//	}
//
//	private Translation[] getPlainTranslations() {
//		Translation[] myTranslations = (Translation[])  this.plainTranslations.toArray( new Translation[  this.plainTranslations.size() ]);
//		Arrays.sort( myTranslations, this );
//		return myTranslations;
//	}
	
	private Translation[] getTranslations( List translations ) {
		Translation[] myTranslations = (Translation[])  translations.toArray( new Translation[ translations.size() ]);
		Arrays.sort( myTranslations, this );
		return myTranslations;		
	}


	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2) {
		Translation t1 = (Translation) o1;
		Translation t2 = (Translation) o2;
		return t1.getId() - t2.getId();
	}


	/**
	 * Inserts the static fields of the Locale class.
	 * 
	 * @param code the source code, the code should be inserted into the current position.
	 * @param isFinal true when the fields should be "static final" instead of only "static". When
	 *        the dynamic localization mode is used, the fileds cannot be final.
	 */
	private void insertFields(StringList code, boolean isFinal) {
		Locale loc = this.locale.getLocale();
		if ( isFinal ) {
			code.insert( "\tpublic static final String LANGUAGE = \"" + loc.getLanguage() + "\";");
			code.insert( "\tpublic static final String DISPLAY_LANGUAGE = \"" + loc.getDisplayLanguage(loc) + "\";");
		} else {
			code.insert( "\tpublic static String LANGUAGE = \"" + loc.getLanguage() + "\";");
			code.insert( "\tpublic static String DISPLAY_LANGUAGE = \"" + loc.getDisplayLanguage(loc) + "\";");
		}
		String country = loc.getCountry();
		char minusSign = '-';
		char zeroDigit = '0';
		char decimalSeparator = '.';
		char monetaryDecimalSeparator = '.';
		char groupingSeparator = ',';
		char percent = '%';
		char permill = '\u2030';
		String infinity = "\u221e";
		
		NumberFormat format = NumberFormat.getCurrencyInstance( loc );
		try {
			DecimalFormat decimalFormat = (DecimalFormat) format;
			DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();
			minusSign = symbols.getMinusSign();
			zeroDigit = symbols.getZeroDigit();
			decimalSeparator = symbols.getDecimalSeparator();
			monetaryDecimalSeparator = symbols.getMonetaryDecimalSeparator();
			groupingSeparator = symbols.getGroupingSeparator();
			percent = symbols.getPercent();
			permill = symbols.getPerMill();
			infinity = symbols.getInfinity();
		} catch (Exception e) {
			System.out.println("Warning: the locale [" + loc + "] does not support decimal symbols: " + e.toString() );
		}
		if (isFinal) {
			code.insert( "\tpublic static final char MINUS_SIGN = '" + toSourceCode( minusSign ) + "';");
			code.insert( "\tpublic static final char ZERO_DIGIT = '" + toSourceCode( zeroDigit ) + "';");
			code.insert( "\tpublic static final char DECIMAL_SEPARATOR = '" + toSourceCode( decimalSeparator ) + "';");
			code.insert( "\tpublic static final char MONETARY_DECIMAL_SEPARATOR = '" + toSourceCode( monetaryDecimalSeparator ) + "';");
			code.insert( "\tpublic static final char GROUPING_SEPARATOR = '" + toSourceCode( groupingSeparator ) + "';");
			code.insert( "\tpublic static final char PERCENT = '" + toSourceCode( percent ) + "';");
			code.insert( "\tpublic static final char PERMILL = '" + toSourceCode( permill ) + "';");
			code.insert( "\tpublic static final String INFINITY = \"" + toSourceCode( infinity ) + "\";");
			if (country.length() > 0) {
				code.insert( "\tpublic static final String COUNTRY = \"" + loc.getCountry() + "\";");
				code.insert( "\tpublic static final String DISPLAY_COUNTRY = \"" + loc.getDisplayCountry(loc) + "\";");
				Currency currency = format.getCurrency();
				String currencySymbol = currency.getSymbol(loc);
				String currencyCode = currency.getCurrencyCode();
				code.insert( "\tpublic static final String CURRENCY_SYMBOL = \"" + toSourceCode( currencySymbol ) + "\";");
				code.insert( "\tpublic static final String CURRENCY_CODE = \"" + currencyCode + "\";");
			} else {
				// no country is defined:
				code.insert( "\tpublic static final String COUNTRY = null;");
				code.insert( "\tpublic static final String DISPLAY_COUNTRY = null;");
				code.insert( "\tpublic static final String CURRENCY_SYMBOL = null;");
				code.insert( "\tpublic static final String CURRENCY_CODE = null;");
			}
		} else {
			code.insert( "\tpublic static char MINUS_SIGN = '" + toSourceCode( minusSign ) + "';");
			code.insert( "\tpublic static char ZERO_DIGIT = '" + toSourceCode( zeroDigit ) + "';");
			code.insert( "\tpublic static char DECIMAL_SEPARATOR = '" + toSourceCode( decimalSeparator ) + "';");
			code.insert( "\tpublic static char MONETARY_DECIMAL_SEPARATOR = '" + toSourceCode( monetaryDecimalSeparator ) + "';");
			code.insert( "\tpublic static char GROUPING_SEPARATOR = '" + toSourceCode( groupingSeparator ) + "';");
			code.insert( "\tpublic static char PERCENT = '" + toSourceCode( percent ) + "';");
			code.insert( "\tpublic static char PERMILL = '" + toSourceCode( permill ) + "';");
			code.insert( "\tpublic static String INFINITY = \"" + toSourceCode( infinity ) + "\";");
			if (country.length() > 0) {
				code.insert( "\tpublic static String COUNTRY = \"" + loc.getCountry() + "\";");
				code.insert( "\tpublic static String DISPLAY_COUNTRY = \"" + loc.getDisplayCountry(loc) + "\";");
				Currency currency = format.getCurrency();
				String currencySymbol = currency.getSymbol(loc);
				String currencyCode = currency.getCurrencyCode();
				code.insert( "\tpublic static String CURRENCY_SYMBOL = \"" + toSourceCode( currencySymbol ) + "\";");
				code.insert( "\tpublic static String CURRENCY_CODE = \"" + currencyCode + "\";");
			} else {
				// no country is defined:
				code.insert( "\tpublic static String COUNTRY = null;");
				code.insert( "\tpublic static String DISPLAY_COUNTRY = null;");
				code.insert( "\tpublic static String CURRENCY_SYMBOL = null;");
				code.insert( "\tpublic static String CURRENCY_CODE = null;");
			}
		}
	}
	
	private String toSourceCode(String s) {
		StringBuffer buffer = new StringBuffer( s.length() << 1 );
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			appendSourceCode(buffer, c);
		}
		return buffer.toString();
	}

	private String toSourceCode(char c) {
		StringBuffer buffer = new StringBuffer( 2 );
		appendSourceCode( buffer, c );
		return buffer.toString();
	}

	private void appendSourceCode(StringBuffer buffer, char c) {
		if (c == '\'') {
			buffer.append( "\\'");
		} else if (c == '\\') {
			buffer.append( "\\\\");
		} else {
			Native2Ascii.nativeToAscii( c, buffer );
		}
	}

	/**
	 * Determines whether dynamic translations should be used.
	 * Such translations can be changed during runtime.
	 * 
	 * @return true when dynamic translations should be used.
	 */
	public boolean isDynamic() {
		return this.isDynamic;
	}

	/**
	 * Retrieves the default locale.
	 * This makes only sense when dynamic translations are used.
	 * 
	 * @return the default locale
	 */
	public LocaleSetting getDefaultLocale() {
		return this.localizationSetting.getDefaultLocale();
	}
	
	private void setIdsAndSort( Translation[] translations ) {
		for (int i = 0; i < translations.length; i++) {
			Translation translation = translations[i];
			Translation knownIdTranslation = getTranslation( translation.getKey() );
			if (knownIdTranslation == null) {
				throw new BuildException("The translation [" + translation.getKey() +"] is not defined in the default locale. You need to define it in that locale as well when you want to use dynamic translations." );
			}
			int id = knownIdTranslation.getId();
			translation.setId( id );
		}		
		Arrays.sort( translations, this );
	}
	
	/**
	 * Saves all translations into a *.loc file.
	 * This is only possible when dynamic translations are used.
	 * 
	 * @param targetDir the target directory
	 * @param currentDevice the current device
	 * @param dynamicLocaleSetting the locale
	 * @param isForExternalUsage 
	 * @throws IOException when a resource could not be copied
	 */
	public void saveTranslations(File targetDir, Device currentDevice, LocaleSetting dynamicLocaleSetting, boolean isForExternalUsage )
	throws IOException
	{
		//System.out.println("saveTranslations, external=" + isForExternalUsage + ", plainTranslationsExternal=" + this.plainTranslationsExternal);
		if (isForExternalUsage) {
			if (this.plainTranslationsExternal != null) {
				saveTranslations( targetDir, currentDevice, dynamicLocaleSetting, this.plainTranslationsExternal, this.singleParameterTranslationsExternal, this.multipleParametersTranslationsExternal);
			}
		} else {
			saveTranslations( targetDir, currentDevice, dynamicLocaleSetting, this.plainTranslations, this.singleParameterTranslations, this.multipleParametersTranslations);
		}
	}

	/**
	 * Saves all translations into a *.loc file.
	 * This is only possible when dynamic translations are used.
	 * 
	 * @param targetDir the target directory
	 * @param currentDevice the current device
	 * @param dynamicLocaleSetting the locale
	 * @param translationsPlain the plain translations
	 * @param translationsSingle the translations with one parameter
	 * @param translationsMultiple the translations with several parameters
	 * @throws IOException when a resource could not be copied
	 */
	public void saveTranslations(File targetDir, Device currentDevice, LocaleSetting dynamicLocaleSetting, ArrayList translationsPlain, ArrayList translationsSingle, ArrayList translationsMultiple )
	throws IOException
	{
		Locale dynamicLocale = dynamicLocaleSetting.getLocale();
		File file = new File( targetDir, dynamicLocale.toString() + ".loc" );
//		System.out.println("Writing translations to " + file.getAbsolutePath() );
		DataOutputStream out = new DataOutputStream( new FileOutputStream( file ) );
		// plain translations:
		Translation[] translations = getTranslations(translationsPlain);
		setIdsAndSort( translations );
		out.writeInt( translations.length );
		for (int i = 0; i < translations.length; i++) {
			Translation translation = translations[i];
//			System.out.println( i + "=" + translation.getKey() + "=" + translation.getValue() );
			out.writeUTF( translation.getValue() );
		}
		
		// translations with a single parameter:
		translations = getTranslations(translationsSingle);
		setIdsAndSort( translations );
		out.writeInt( translations.length );
		for (int i = 0; i < translations.length; i++) {
			Translation translation = translations[i];
			out.writeUTF( translation.getOneValueStart() );
			out.writeUTF( translation.getOneValueEnd() );
		}
		
		// translations with a multiple parameters:
		translations = getTranslations(translationsMultiple);
		setIdsAndSort( translations );
		out.writeInt( translations.length );
		for (int i = 0; i < translations.length; i++) {
			Translation translation = translations[i];
			try {
				int[] orders = translation.getParameterIndices();
				if (orders == null || orders.length == 0) {
					//System.out.println("adding key " + translation.getKey() + " with ID " + translation.getId() +  " at position " + i);
					out.writeByte(1);
					out.writeUTF("");
				} else {
					String[] chunks = translation.getValueChunks();
					if (orders.length != chunks.length - 1) {
						throw new IllegalStateException("TranslationManager: unable to save translation file: (orders.length != chunks.length - 1) for translation [" + translation.getValue() + "], please report this error to j2mepolish@enough.org");
					}
					out.writeByte( chunks.length );
					for (int j = 0; j < chunks.length; j++) {
						String chunk = chunks[j];
						out.writeUTF( chunk );
					}
					for (int j = 0; j < orders.length; j++) {
						int order = orders[j]; 
						out.writeByte( order );
					}
				}
			} catch (RuntimeException e) {
				System.err.println("Unable to process translation [" + translation.getKey() + "]: " + e.toString() );
				throw e;
			}
		}
		out.writeUTF( dynamicLocale.getLanguage() );
		out.writeUTF( dynamicLocale.getDisplayLanguage( dynamicLocale ) );
		char minusSign = '-';
		char zeroDigit = '0';
		char decimalSeparator = '.';
		char monetaryDecimalSeparator = '.';
		char groupingSeparator = ',';
		char percent = '%';
		char permill = '\u2030';
		String infinity = "\u221e";	
		NumberFormat format = NumberFormat.getCurrencyInstance( this.locale.getLocale() );
		try {
			DecimalFormat decimalFormat = (DecimalFormat) format;
			DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();
			minusSign = symbols.getMinusSign();
			zeroDigit = symbols.getZeroDigit();
			decimalSeparator = symbols.getDecimalSeparator();
			monetaryDecimalSeparator = symbols.getMonetaryDecimalSeparator();
			groupingSeparator = symbols.getGroupingSeparator();
			percent = symbols.getPercent();
			permill = symbols.getPerMill();
			infinity = symbols.getInfinity();
		} catch (Exception e) {
			System.out.println("Warning: the locale [" + this.locale.getLocale() + "] does not support decimal symbols: " + e.toString() );
		}
		out.writeChar( minusSign );
		out.writeChar( zeroDigit );
		out.writeChar( decimalSeparator );
		out.writeChar( monetaryDecimalSeparator );
		out.writeChar( groupingSeparator );
		out.writeChar( percent );
		out.writeChar(  permill );
		out.writeUTF( infinity );
		String country = dynamicLocale.getCountry();
		out.writeUTF( country );
		Locale loc = this.locale.getLocale();
		if (country.length() > 0) {
			out.writeUTF( loc.getDisplayCountry(loc) );
			Currency currency = format.getCurrency();
			String currencySymbol = currency.getSymbol(loc);
			String currencyCode = currency.getCurrencyCode();
			out.writeUTF( currencySymbol );
			out.writeUTF( currencyCode );
		}

		out.flush();
		out.close();
	}

	/**
	 * @return the locale associated with this manager
	 */
	public LocaleSetting getLocale() {
		return this.locale;
	}

	/**
	 * Retrieves all preprocessing variables that have been defined
	 * 
	 * @return all preprocessing variables that have been defined
	 */
	public Map getPreprocessingVariables() {
		return this.preprocessingVariablesByKey;
	}

	/**
	 * Retrieves the number of translation known for the given type
	 * @param type the type
	 * @return the number of translation known for the given type
	 * @see Translation#PLAIN
	 * @see Translation#SINGLE_PARAMETER
	 * @see Translation#MULTIPLE_PARAMETERS
	 */
	public int numberOfTranslations(int type)
	{
		List first;
		List second;
		switch (type) {
		case Translation.PLAIN:
			first = this.plainTranslations;
			second = this.plainTranslationsExternal;
			break;
		case Translation.SINGLE_PARAMETER:
			first = this.singleParameterTranslations;
			second = this.singleParameterTranslationsExternal;
			break;
		case Translation.MULTIPLE_PARAMETERS:
			first = this.multipleParametersTranslations;
			second = this.multipleParametersTranslationsExternal;
			break;
		default:
			return 0;
		}
		if (first != null && second == null) {
			return first.size();
		} else {
			return Math.max( first.size(), second.size() );
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		if (args.length < 2) {
			System.out.println("Usage: java de.enough.polish.resources.TranslationManager translation-key/ID locfile [locfile2, locfile3...]");
			System.exit(1);
		}
		Map idsExternalMap = FileUtil.readProperties( new File(".polishSettings/LocaleIdsPlainExternal.txt"));
		
		String idOrKey = args[0];
		ArrayList translations = new ArrayList();
		for (int i = 1; i < args.length ; i++)
		{
			String fileName = args[i];
			File locFile = new File( fileName );
			DataInputStream in = new DataInputStream( new FileInputStream( locFile ));
			int numberOfTranslation = in.readInt();
			String[] plainTranslations = new String[ numberOfTranslation ];
			for (int j = 0; j < plainTranslations.length; j++)
			{
				plainTranslations[j] = in.readUTF();
			}
			translations.add( plainTranslations );
		}
		int id = -1;
		String key = null;
		if (StringUtil.isNumeric(idOrKey)) {
			// is the ID
			id = Integer.parseInt(idOrKey);
			Object[] keys = idsExternalMap.keySet().toArray();
			for (int i = 0; i < keys.length; i++)
			{
				Object tmpKey = keys[i];
				int value = Integer.parseInt( (String) idsExternalMap.get(tmpKey) );
				if (value == id) {
					key = (String) tmpKey;
					break;
				}
			}
		} else {
			// this is the key:
			key = idOrKey;
			id = Integer.parseInt( (String)idsExternalMap.get(key) ); 
		}
		System.out.println( "id  =  key  = translation");
		for (int i = 0; i < translations.size(); i++)
		{
			System.out.println( id + " = " + key + " = " +  ( id < 1 ? "<invalid>" : ((String[])translations.get(i))[ id - 1 ] ) + "   (from " + args[i+1] + ")" );
		}
	}

}

