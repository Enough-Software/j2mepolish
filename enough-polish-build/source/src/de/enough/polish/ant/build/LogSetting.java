/*
 * Created on 21-Jan-2003 at 15:24:03.
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

import de.enough.polish.ant.ConditionalElement;
import de.enough.polish.util.StringUtil;

import de.enough.polish.BuildException;

import java.util.ArrayList;


/**
 * <p>Represents the debug settings.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        21-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class LogSetting extends ConditionalElement {
	
	private boolean enable = true;
	private boolean verbose;
	private boolean useGui;
	private boolean showLogOnError;
	private String level;
	private final ArrayList filters;
	private final ArrayList logHandlers;
	private boolean logLevel = true;
	private boolean logTimestamp = false;
	private boolean logClassName = false;
	private boolean logLineNumber = false;
	private boolean logMessage = true;
	private boolean logException = true;
	private boolean logThread;
	private String[] levels;

	/**
	 * Creates a new empty debug-setting.
	 */
	public LogSetting() {
		this.filters = new ArrayList();
		this.logHandlers = new ArrayList();
		this.level = "debug";
	}
	

	/**
	 * Determines whether debugging is enabled.
	 * When this is not the case, no debugging information will be
	 * included at all.
	 * 
	 * @return true when debugging is enabled.
	 */
	public boolean isEnabled() {
		return this.enable;
	}

	/**
	 * Sets the debugging mode.
	 * 
	 * @param enable true when the debugging mode is enabled.
	 */
	public void setEnable(boolean enable) {
		System.out.println("Deprecation-Warning: the \"enable\"-attribute of the <debug>-element should not be used any more and is now \"true\" by default. Please use \"if\" and \"unless\" for controlling the <debug>-element.");
		this.enable = enable;
	}

	/**
	 * Gets the general debugging level.
	 * 
	 * @return the general debug level for classes which have no explicit setting.
	 */
	public String getLevel() {
		return this.level;
	}
	
	/**
	 * Retrieves additional user defined debug levels
	 * @return an array of user defined debug levels, can be null
	 */
	public String[] getLevels() {
		return this.levels;
	}

	/**
	 * Sets the general debugging level.
	 * 
	 * @param level the general debug level for classes which have no explicit setting, 
	 * 		e.g. "debug", "info", "warn", "error" or user-defined.
	 */
	public void setLevel(String level) {
		this.level = level;
	}
	
	public void setLevels( String levelsStr ) {
		this.levels = StringUtil.splitAndTrim(levelsStr, ',');
	}

	/**
	 * Determines whether the verbose mode is enabled.
	 * 
	 * @return true when before each debugging the time, class-name and source-location should
	 * 			be printed out.
	 */
	public boolean isVerbose() {
		return this.verbose;
	}

	/**
	 * Sets the verbose mode.
	 * 
	 * @param verbose true when before each debugging the time, class-name and source-location should
	 * 			be printed out.
	 */
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * Determines if the gui debugging mode is enabled.
	 * When this mode is enabled, the preprocessing-symbol "polish.useDebugGui"
	 * will be set.
	 *  
	 * @return true when the gui debugging mode is enabled.
	 */
	public boolean useGui() {
		return this.useGui;
	}

	/**
	 * Sets the gui debugging mode.
	 * 
	 * @param useGui true when the gui debugging mode is enabled.
	 */
	public void setUseGui(boolean useGui) {
		this.useGui = useGui;
	}
	
	public void addConfiguredFilter( LogFilterSetting filter ) {
		if (filter.getPattern() == null) {
			throw new BuildException("Error in debug settings: the element [filter] needs to define the attribute [pattern].");
		}
		if (filter.getLevel() == null) {
			throw new BuildException("Error in debug settings: the element [filter] needs to define the attribute [level].");
		}
		this.filters.add( filter );
	}

	/**
	 * @return an array of all debug-filters.
	 */
	public LogFilterSetting[] getFilters() {
		return (LogFilterSetting[]) this.filters.toArray( new LogFilterSetting[ this.filters.size() ] );
	}
	
	public void addConfiguredHandler( LogHandlerSetting setting ) {
		addConfiguredLoghandler(setting);
	}
	
	public void addConfiguredLoghandler( LogHandlerSetting setting ) {
		this.logHandlers.add( setting );
	}
	
	public LogHandlerSetting[] getLogHandlers() {
		return (LogHandlerSetting[]) this.logHandlers.toArray( new LogHandlerSetting[ this.logHandlers.size() ] );
	}

	/**
	 * @return Returns the showDebugOnError.
	 */
	public boolean showLogOnError() {
		return this.showLogOnError;
	}
	/**
	 * @param showDebugOnError The showDebugOnError to set.
	 */
	public void setShowLogOnError(boolean showDebugOnError) {
		this.showLogOnError = showDebugOnError;
	}


	public boolean logLevel() {
		return this.logLevel;
	}


	public void setLogLevel(boolean logLevel) {
		this.logLevel = logLevel;
	}


	public boolean logLineNumber() {
		return this.logLineNumber;
	}


	public void setLogLineNumber(boolean logLineNumber) {
		this.logLineNumber = logLineNumber;
	}


	public boolean logMessage() {
		return this.logMessage;
	}


	public void setLogMessage(boolean logMessage) {
		this.logMessage = logMessage;
	}


	public boolean logTimestamp() {
		return this.logTimestamp;
	}


	public void setLogTimestamp(boolean logTimestamp) {
		this.logTimestamp = logTimestamp;
	}
	
	public boolean logThread() {
		return this.logThread;
	}

	public void setLogThread(boolean logThread) {
		this.logThread = logThread;
	}
	


	public boolean isShowLogOnError() {
		return this.showLogOnError;
	}


	public boolean isUseGui() {
		return this.useGui;
	}


	public boolean logClassName() {
		return this.logClassName;
	}


	public void setLogClassName(boolean logClassName) {
		this.logClassName = logClassName;
	}


	public boolean logException() {
		return this.logException;
	}


	public void setLogException(boolean logException) {
		this.logException = logException;
	}

}
