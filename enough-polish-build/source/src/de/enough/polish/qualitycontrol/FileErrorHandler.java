//#condition polish.usePolishGui
/*
 * Copyright (c) 2009 Robert Virkus / Enough Software
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

package de.enough.polish.qualitycontrol;

import java.io.File;
import java.util.ArrayList;

import de.enough.polish.Environment;
import de.enough.polish.ErrorHandler;
import de.enough.polish.util.FileUtil;

/**
 * Writes log entries about failures to "./error.log" by default. 
 * Use the "fileerrorhandler.file" environment property for specifying the path to a different file, if desired.
 * 
 * @author Robert Virkus
 *
 */
public class FileErrorHandler implements ErrorHandler {

	/* (non-Javadoc)
	 * @see de.enough.polish.ErrorHandler#handleBuildFailure(java.lang.String, java.lang.String, java.lang.Throwable)
	 */
	public void handleBuildFailure(String deviceIdentifier, String locale,
			Throwable exception) 
	{
		String fileName = Environment.getInstance().getVariable("fileerrorhandler.file");
		if (fileName == null) {
			fileName = System.getProperty("fileerrorhandler.file");
			if (fileName == null) {
				fileName = "error.log";
			}
		}
		File file = new File( fileName );
		ArrayList lines = new ArrayList();
		lines.add("device: " + deviceIdentifier);
		lines.add("locale: " + locale);
		lines.add("error: " + exception.toString());
		StackTraceElement[] traces = exception.getStackTrace();
		if (traces != null && traces.length > 0) {
			lines.add("Stacktrace: ");
			for (int i = 0; i < traces.length; i++) {
				StackTraceElement stackTraceElement = traces[i];
				lines.add( stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "():" + stackTraceElement.getLineNumber() );
			}
		}
		try {
			FileUtil.writeTextFile(file, lines );
		} catch (Exception e) {
			System.out.println("Unable to store error log: " + e.toString() );
			e.printStackTrace();
		}
	}

}
