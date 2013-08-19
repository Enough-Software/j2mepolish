//#condition polish.usePolishGui
package de.enough.polish.ui;

/**
 * Wraps a J2ME Polish command in a different one.
 * @author Robert Virkus, j2mepolish@enough.de
 */
public interface CommandWrapper
{
	/**
	 * Retrieves the J2ME Polish command that's wrapped by this object
	 * @return the command
	 */
	Command getCommand();
}
