//#condition polish.usePolishGui && polish.api.nokia-ui-2.0

package de.enough.polish.ui;

import javax.microedition.lcdui.Image;

import com.nokia.mid.ui.IconCommand;

/**
 * Manages an IconCommand and keeps a reference to the original command.
 * This is necessary as the IconCommand does not accept null images.
 * @author Robert Virkus, j2mepolish@enough.de
 *
 */
public class NokiaIconCommand 
extends IconCommand
implements CommandWrapper
{

	private final Command	command;

	/**
	 * Creates a new icon command
	 * @param command the original command
	 * @param iconImage the icon image
	 */
	public NokiaIconCommand( Command command, Image iconImage)
	{
		super(command.getLabel(), command.getLongLabel(), iconImage, iconImage, command.getCommandType(), command.getPriority());
		this.command = command;
		
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.CommandWrapper#getCommand()
	 */
	public Command getCommand()
	{
		return this.command;
	}

}
