/*
 * Created on 01-Sep-2004 at 22:52:41.
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
package de.enough.polish.ant;

import java.io.File;
import java.io.PrintStream;
import java.util.Map;

import org.apache.tools.ant.BuildEvent;
//import de.enough.polish.BuildException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.BuildLogger;

import de.enough.polish.Environment;



/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        01-Sep-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class PolishLogger implements BuildLogger {
	
	private final static char SEPERATOR_CHAR = File.separatorChar; // this weird construct is needed
	// so that Ant does not load the File class in the messageLogged() method...

	private BuildListener logger;
	private boolean isInObfuscateMode;
	private boolean isInCompileMode;
	private Map classPathTranslations;
	private boolean isInternalCompilationError;
	private final Environment environment;


	/**
	 * Creates a new logger
	 * 
	 * @param logger the original logger
	 * @param classPathTranslations a map containing all original paths for each loaded java file.
	 * @param environment the environment settings
	 * 
	 */
	public PolishLogger( BuildListener logger, Map classPathTranslations, Environment environment ) {
		this.logger = logger;
		this.classPathTranslations = classPathTranslations;
		this.environment = environment;
		try {
			// force class loading, so that no additional build-event
			// is triggered when the messageLogged()-method
			// is invoced:
			Class.forName( "org.apache.tools.ant.BuildEvent" );
			Class.forName( "java.lang.String" );
			Class.forName( "java.lang.StringBuffer" );
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new BuildException("Unable to load BuildEvent from the classpath. Please report this error to j2mepolish@enough.de stating your Ant-version.");
		}
	}
	
	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildListener#messageLogged(org.apache.tools.ant.BuildEvent)
	 */
	public void messageLogged(BuildEvent event) {
		if (this.isInCompileMode) {
			String message = event.getMessage();
			int index;
			if (message != null && (index = message.indexOf(".java")) != -1) {
				int startIndex = message.substring(0, index).lastIndexOf("source");
				if (startIndex == -1) {
					startIndex = 0;
				} else {
					if (startIndex != 0 && message.charAt(startIndex-1) != File.separatorChar) {
						startIndex = message.indexOf( "source" );
					}
					startIndex += "source/".length();
				}
				String classPath = message.substring(startIndex, index + ".java".length() );
				String originalPath = (String) this.classPathTranslations.get( classPath );
				if (originalPath == null && File.separatorChar == '\\') {
					originalPath = (String) this.classPathTranslations.get( classPath.replace('\\', '/') );
				}
				if (originalPath != null) {
					// [javac] is needed by some IDEs like Eclipse,
					// so that it can map the source-code position
					// to the editor. (2005-08: this is now done directly, since Eclipse 3.1
					// expects [javac] at the beginning of the line.
					//message = "[javac] " + originalPath + message.substring( index + ".java".length() );
					message = originalPath + message.substring( index + ".java".length() );
					event.setMessage(message, event.getPriority());
					this.isInternalCompilationError = false;
				} else {
					String polishSource = this.environment.getVariable("polish.internal.source");
					if (polishSource != null) {
						message = polishSource + SEPERATOR_CHAR +  message.substring( startIndex );
						event.setMessage(message, event.getPriority() );
					} else {
						event.setMessage("Internal J2ME Polish class: " + message, event.getPriority() );
					}
					this.isInternalCompilationError = true;
//					message = "unable to resolve: " + classPath;
//					event.setMessage(message, event.getPriority());
				}
			}
		} else if (this.isInObfuscateMode) {
			String message = event.getMessage();
			if (message != null) {
				if (message.startsWith("Note")
						|| message.startsWith("Reading")
						|| message.startsWith("Copying")
						) 
				{
					// suppress this event:
					return;
				}
			}
		}
		this.logger.messageLogged(event);
	}
	
	/**
	 * Enables or disables the obfuscate mode.
	 * 
	 * @param enable true when the obfuscation mode is enabled.
	 */
	public void setObfuscateMode(boolean enable) {
		this.isInObfuscateMode = enable;
	}

	/**
	 * Enables or disables the compile mode.
	 * 
	 * @param enable true when the compile mode is enabled.
	 */
	public void setCompileMode(boolean enable) {
		this.isInCompileMode = enable;
	}
	
	/**
	 * Determines if a compilation error occurred witin the internal J2ME Polish libraries.
	 * 
	 * @return true when a compilation error occurred witin the internal J2ME Polish libraries.
	 */
	public boolean isInternalCompilationError() {
		return this.isInternalCompilationError;
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	// ONLY WRAPPER METHODS ARE AFTER THIS POINT
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	

	
	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildLogger#setOutputPrintStream(java.io.PrintStream)
	 */
	public void setOutputPrintStream(PrintStream out) {
		if (this.logger instanceof BuildLogger) {
			((BuildLogger)this.logger).setOutputPrintStream(out);
		}
	}



	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildLogger#setMessageOutputLevel(int)
	 */
	public void setMessageOutputLevel(int level) {
		if (this.logger instanceof BuildLogger) {
			((BuildLogger)this.logger).setMessageOutputLevel(level);
		}
	}



	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildLogger#setEmacsMode(boolean)
	 */
	public void setEmacsMode(boolean enabled) {
		if (this.logger instanceof BuildLogger) {
			((BuildLogger)this.logger).setEmacsMode(enabled);
		}
	}



	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildLogger#setErrorPrintStream(java.io.PrintStream)
	 */
	public void setErrorPrintStream(PrintStream out) {
		if (this.logger instanceof BuildLogger) {
			((BuildLogger)this.logger).setErrorPrintStream(out);
		}
	}



	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildListener#buildStarted(org.apache.tools.ant.BuildEvent)
	 */
	public void buildStarted(BuildEvent event) {
		this.logger.buildStarted(event);
	}



	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildListener#buildFinished(org.apache.tools.ant.BuildEvent)
	 */
	public void buildFinished(BuildEvent event) {
		this.logger.buildFinished(event);
	}



	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildListener#targetStarted(org.apache.tools.ant.BuildEvent)
	 */
	public void targetStarted(BuildEvent event) {
		this.logger.targetStarted(event);
	}



	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildListener#targetFinished(org.apache.tools.ant.BuildEvent)
	 */
	public void targetFinished(BuildEvent event) {
		this.logger.targetFinished(event);
	}



	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildListener#taskStarted(org.apache.tools.ant.BuildEvent)
	 */
	public void taskStarted(BuildEvent event) {
		this.logger.taskStarted(event);
	}



	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildListener#taskFinished(org.apache.tools.ant.BuildEvent)
	 */
	public void taskFinished(BuildEvent event) {
		this.logger.taskFinished(event);
	}


} 