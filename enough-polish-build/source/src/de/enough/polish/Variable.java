/*
 * Created on 03-Oct-2003 at 17:14:23
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
package de.enough.polish;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import de.enough.polish.preprocess.Preprocessor;
import de.enough.polish.util.CastUtil;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.StringList;
import de.enough.polish.util.StringUtil;

/**
 * <p>Variable provides the definition of a name-value pair.</p>
 * <p></p>
 * <p>copyright Enough Software 2003, 2004</p>
 * <pre>
 *    history
 *       03-Oct-2003 (rob) creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Variable {
	
	private String name;
	private String value;
	private String type;
	private String ifCondition;
	private String unlessCondition;
	private boolean hasPropertiesInFileName;
	protected String fileName;

	/**
	 * Creates new uninitialised Variable
	 */
	public Variable() {
		// no values are set here
	}
	
	/**
	 * Copies all settings from the given variable.
	 * 
	 * @param variable the original.
	 */
	public Variable(Variable variable) {
		this.name = variable.name;
		this.value = variable.value;
		this.type = variable.type;
		this.ifCondition = variable.ifCondition;
		this.unlessCondition = variable.unlessCondition;
		this.hasPropertiesInFileName = variable.hasPropertiesInFileName;
		this.fileName = variable.fileName;
	}

	/**
	 * Creates a new Varable
	 * @param name (String) the name of this variable
	 * @param value (String) the value of this variable
	 */
	public Variable(String name, String value) {
		this.name = name;
		this.value = value;
	}


	/**
	 * @return the name of this variables
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the value of this variable
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * @param name the name of this variable
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param value the value of this variable
	 */
	public void setValue(String value ) {
		this.value = value;
	}

	/**
	 * @return Returns the type of this variable.
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * @param type The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return Returns the ifCondition.
	 */
	public String getIfCondition() {
		return this.ifCondition;
	}
	
	/**
	 * @param ifCondition The ifCondition to set.
	 */
	public void setIf(String ifCondition) {
		this.ifCondition = ifCondition;
	}
	
	/**
	 * @return Returns the unlessCondition.
	 */
	public String getUnlessCondition() {
		return this.unlessCondition;
	}
	
	/**
	 * @param unlessCondition The unlessCondition to set.
	 */
	public void setUnless(String unlessCondition) {
		this.unlessCondition = unlessCondition;
	}
	
	/**
	 * Determines whether this variable should only be added when a condition is fullfilled.
	 *  
	 * @return true when this variable has a condition
	 * @see #getIfCondition()
	 * @see #getUnlessCondition()
	 */
	public boolean hasCondition(){
		return this.ifCondition != null || this.unlessCondition != null;
	}
	
	/**
	 * Sets the file which contains several variables.
	 * 
	 * @param fileName the name file, e.g. <variable file="cfg/${ lowercase(polish.vendor) }.properties" />
	 */
	public void setFile( String fileName ) {
		this.fileName = fileName;
		if ( fileName.indexOf("${") != -1 ) {
			this.hasPropertiesInFileName = true;
		}		
	}
	
	/**
	 * Determines whether this variable contains several variable-definitions.
	 * This is the case when a [file]-attribute has been set.
	 * 
	 * @return true when this variable contains several variable-definitions.
	 */
	public boolean containsMultipleVariables() {
		return this.fileName != null;
	}
	
	/**
	 * Loads all variable-definitions from the specified file.
	 * 
	 * @param environment the environment settings
	 * @return an array of variable definitions found in the specified file.
	 */
	public Variable[] loadVariables(Environment environment ) {
		File file = getFile( environment );
		if (!file.exists()) {
			System.err.println( getFileNotFoundWarning( file )  );
			return new Variable[0];
		}
		try {
			Preprocessor preprocessor = (Preprocessor) environment.get( Preprocessor.ENVIRONMENT_KEY );
			String[] lines = FileUtil.readTextFile( file );
			boolean removePreprocessingComments = true;
			lines = preprocessor.preprocess( file.getAbsolutePath(), new StringList( lines ), removePreprocessingComments );
			Map map = StringUtil.getProperties( lines, getDelimiter() );
			//HashMap map = FileUtil.readPropertiesFile( file, getDelimiter() );
			Object[] keys = map.keySet().toArray();
			Variable[] variables = createArray( keys.length );
			for (int i = 0; i < variables.length; i++) {
				String key = (String) keys[i];
				variables[i] = createVariable( key, (String) map.get( key ) );
			}
			return variables;
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException( getFileIOError(file, e), e );
		}
	}
	
	protected Variable[] createArray( int size ) {
		return new Variable[ size ];
	}
	
	public Variable createVariable( String varName, String varValue ) {
		return new Variable( varName, varValue );
	}

	/**
	 * The delimiter for separating variable-names from variable-values.
	 * 
	 * @return '=' by default
	 */
	protected char getDelimiter() {
		return '=';
	}

	/**
	 * Gets the warning that is shown when a dynamic variables file could not be found.
	 * 
	 * @param file the file that couldn't be found
	 * @return a warning message
	 */
	protected String getFileNotFoundWarning(File file) {
		return "Warning: unable to load <variable>-file [" + this.fileName + "] from [" + file.getAbsolutePath() + "]: file not found.";
	}
	
	/**
	 * Gets the error-message that is shown when a static variables file could not be found.
	 * 
	 * @param file the file that couldn't be found
	 * @return an error message
	 */
	protected String getFileNotFoundError(File file) {
		return " The [file]-attribute  [" + this.fileName + "] of a <variable>-element points to the non-existing file [" + file.getAbsolutePath() + "]. Please correct this [file]-attribute.";
	}

	/**
	 * Gets the error-message that is shown when a variables file could not be loaded.
	 * 
	 * @param file the file that couldn't be found
	 * @param e the IOException
	 * @return an error message
	 */
	protected String getFileIOError(File file, IOException e) {
		return "Unable to load <variable>-file [" + file.getAbsolutePath() + "]:" + e.toString();
	}

	/**
	 * @param environment
	 * @return the file containing variable definitions
	 */
	protected File getFile(Environment environment) {
		return environment.resolveFile( this.fileName );

	}

	/**
	 * Checks if the conditions for this variable are met.
	 * 
	 * @param environment the environment settings
	 * @return true when no condition has been specified 
	 * 			or the specified conditions have been met.
	 */
	public boolean isConditionFulfilled( Environment environment ) {
		return isConditionFulfilled( environment.getBooleanEvaluator(), null );
	}

	/**
	 * Checks if the conditions for this variable are met.
	 * 
	 * @param evaluator the boolean evaluator with the settings for the current device
	 * @param properties any basic properties like Ant properties
	 * @return true when no condition has been specified 
	 * 			or the specified conditions have been met.
	 */
	public boolean isConditionFulfilled(BooleanEvaluator evaluator, Map properties) {
		if (this.ifCondition != null) {
			// first check if there is an Ant-attribute:
			String antProperty = properties == null? null : (String) properties.get( this.ifCondition );
			if (antProperty != null) {
				boolean success = CastUtil.getBoolean(antProperty );
				if (!success) {
					return false;
				}
			} else {
				boolean success = evaluator.evaluate( this.ifCondition, "build.xml", 0);
				if (!success) {
					return false;
				}
			}
		}
		if (this.unlessCondition != null) {
			// first check if there is an Ant-attribute:
			String antProperty = properties == null? null : (String) properties.get( this.unlessCondition );
			if (antProperty != null) {
				boolean success = CastUtil.getBoolean(antProperty );
				if (success) {
					return false;
				}
			} else {
				boolean success = evaluator.evaluate( this.unlessCondition, "build.xml", 0);
				if (success) {
					return false;
				}
			}
		}
		return true;
	}
	
	public String toString()
	{
		return this.name + ":" + this.value;
	}

}
