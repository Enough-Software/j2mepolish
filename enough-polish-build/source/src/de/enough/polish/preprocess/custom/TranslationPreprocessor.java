/*
 * Created on 13-Sep-2004 at 14:47:47.
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
package de.enough.polish.preprocess.custom;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.enough.polish.BuildException;

import de.enough.polish.Device;
import de.enough.polish.preprocess.CustomPreprocessor;
import de.enough.polish.resources.Translation;
import de.enough.polish.resources.TranslationManager;
import de.enough.polish.util.StringList;
import de.enough.polish.util.StringUtil;

/**
 * <p>Incorporates localized messages into the source code.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        13-Sep-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TranslationPreprocessor extends CustomPreprocessor {

	private final static String KEY_PATTERN = "\\\"[\\w|\\-|_|:|\\.|\\]|\\[|\\s]+\\\"";
	private final static String PARAMETER_PATTERN = "\\\".*\\\"(\\s*\\+\\s*" + KEY_PATTERN + ")?";
	
	private final static String LOCALE_GET_PATTERN_STR = "Locale\\.get\\s*\\(\\s*" + KEY_PATTERN + "(\\s*\\,\\s*([\\w|\\.]+|" + PARAMETER_PATTERN + "))?\\s*\\)"; 
	protected final static Pattern LOCALE_GET_PATTERN = Pattern.compile( LOCALE_GET_PATTERN_STR );
	
	private TranslationManager translationManager;
	private boolean useDynamicTranslation;
	private boolean ignoreMissingTranslations;
	
	/**
	 * Creates a new empty preprocessor for translations.
	 */
	public TranslationPreprocessor() {
		super();
	}
	
	/**
	 * Sets a new translation manager.
	 * @param translationManager the new translation manager.
	 */
	public void setTranslationManager( TranslationManager translationManager ) {
		this.translationManager = translationManager;
		this.useDynamicTranslation = translationManager.isDynamic();
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.CustomPreprocessor#notifyDevice(de.enough.polish.Device, boolean)
	 */
	public void notifyDevice(Device device, boolean usesPolishGui) {
		super.notifyDevice(device, usesPolishGui);
		this.ignoreMissingTranslations = device.getEnvironment().hasSymbol("polish.translations.ignoremissing");
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.CustomPreprocessor#notifyDeviceEnd(de.enough.polish.Device, boolean)
	 */
	public void notifyDeviceEnd(Device device, boolean usesPolishGui) {
		super.notifyDeviceEnd(device, usesPolishGui);
		try {
			// now save the found messages-IDs to the disk:
			this.translationManager.writeIdsMaps();
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException("Unable to store IDs-map of translations: " + e.toString(), e );
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.CustomPreprocessor#processClass(de.enough.polish.util.StringList, java.lang.String)
	 */
	public void processClass(StringList lines, String className) {
		while (lines.next()) {
			String line = lines.getCurrent();
			int startIndex = line.indexOf("Locale.get");
			if (startIndex != -1 ) {
				int commentIndex = line.indexOf("//");
				if ( commentIndex != -1 && commentIndex < startIndex && line.indexOf("//#") != commentIndex) {
					continue;
				}
				Matcher matcher = LOCALE_GET_PATTERN.matcher( line );
				boolean matchFound = false;
				while (matcher.find()) {
					matchFound = true;
					String call = matcher.group();
					int keyStart = call.indexOf('"');
					int keyEnd = call.indexOf('"', keyStart + 1);
					String key = call.substring( keyStart + 1, keyEnd );
					Translation translation = this.translationManager.getTranslation(key);
					if (translation == null) {
						if (this.ignoreMissingTranslations) {
							line = StringUtil.replace( line, call, '"' + key + '"' );
							continue;
						} 
						throw new BuildException( getErrorStart(className, lines) + "Found no translation for key [" + key + "].");
					}
					if (this.useDynamicTranslation) {
						// replace String-key with integer ID:
						if (translation.getId() <= 0) {
							throw new BuildException(getErrorStart(className, lines) + "Translation for key [" + key + "] has no assigned ID. Please report this error to j2mepolish@enough.de along with the error message and messages.txt file.");
						} else if (translation.getId() > this.translationManager.numberOfTranslations( translation.getType() )) {
							throw new BuildException(getErrorStart(className, lines) + "Translation for key [" + key + "] has the invalid assigned ID " + translation.getId() + ", max=" + this.translationManager.numberOfTranslations( translation.getType() ) + ". Please remove the .polishSettings directory from the root directory of this project. If this does not solve this problem, please report this error to j2mepolish@enough.de along with the error message, the .polishSettings directory and messages.txt file.");
						}
						String id = "" + (translation.getId() - 1);
						String quotedKey = '"' + key + '"';
						int index = call.indexOf(quotedKey);
						String callReplacement;
						if (translation.isPlain() && call.indexOf(',', index + quotedKey.length()) != -1) {
							callReplacement = "Locale.get(" + id + ")";
						} else {
							callReplacement = StringUtil.replace( call, quotedKey, id );
						}
						line = StringUtil.replace( line, call, callReplacement );						
					} else {
						if (translation.isPlain()) {
							line = StringUtil.replace( line, call, translation.getQuotedValue() );
						} else {
							// there is at least one parameter:
							int parameterStart = call.indexOf(',');
							if (parameterStart == -1) {
								throw new BuildException( getErrorStart(className, lines) + "The translation for key [" + key + "] expects at least one parameter, but none is given. The tranlation is: [" + translation.getValue() + "].");
							}
							if (translation.hasOneParameter()) {
								String parameter = call.substring( parameterStart + 1, call.length() - 1).trim();
								line = StringUtil.replace( line, call, translation.getQuotedValue( parameter ) );
							} else {
								// replace String-key with integer-ID to save valueable size:
								String id = "" + (translation.getId() - 1);
								String quotedKey = '"' + key + '"';
								String callReplacement = StringUtil.replace( call, quotedKey, id );
								line = StringUtil.replace( line, call, callReplacement );
							}
						}
					}
				} // while matcher.find()
				if (matchFound) {
					lines.setCurrent( line );
				//} else {
				//	System.out.println("WARNING: No Locale-call() found in " + line );
				}
			}
		}
	}


}
