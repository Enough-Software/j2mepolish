//#condition polish.usePolishGui && polish.blackberry && (polish.useNativeGui || polish.useNativeAlerts)
/*
 * Created on Dec 5, 2010 at 7:56:37 PM.
 * 
 * Copyright (c) 2010 Robert Virkus / Enough Software
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
package de.enough.polish.blackberry.nativeui;

import de.enough.polish.ui.Alert;
import de.enough.polish.ui.Command;
import de.enough.polish.ui.Display;
import de.enough.polish.util.ArrayList;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.DialogClosedListener;

/**
 * <p>Displays an alert as a native Dialog on BlackBerry OS.</p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class AlertDialog extends Dialog implements DialogClosedListener {

	private final Alert alert;



	/**
	 * Creates a new dialog.
	 * @param alert the J2ME Polish based Alert
	 */
	public AlertDialog(Alert alert) {
		super( getMessage(alert), getChoices(alert), getChoiceValues(alert), 0, null, 0 );
		this.alert = alert;
		this.alert.setNextDisplayable( Display.getInstance().getCurrent() );
		setDialogClosedListener(this);
	}

	private static String getMessage(Alert alert) {
		if (alert.getTitle() == null && alert.getString() != null) {
			return alert.getString();
		} else if (alert.getTitle() != null && alert.getString() == null) {
			return alert.getTitle();
		} else {
			return alert.getTitle() + "\n" + alert.getString();
		}
	}

	private static Object[] getChoices(Alert alert) {
		Object[] commands = alert.getCommands();
		if (commands == null) {
			return new Object[0];
		}
		ArrayList choicesList = new ArrayList( commands.length );
		for (int i = 0; i < commands.length; i++) {
			Command cmd = (Command) commands[i];
			if (cmd == null) {
				break;
			}
			choicesList.add(cmd.getLabel());
		}
		return choicesList.toArray();
	}

	private static int[] getChoiceValues(Alert alert2) {
		return null;
	}


	/* (non-Javadoc)
	 * @see net.rim.device.api.ui.component.DialogClosedListener#dialogClosed(net.rim.device.api.ui.component.Dialog, int)
	 */
	public void dialogClosed(Dialog dialog, int selectedChoice) {
		if (this.alert.getCommandListener() != null) {			
			Command cmd = (Command) this.alert.getCommands()[selectedChoice];
			this.alert.getCommandListener().commandAction(cmd, this.alert);
		}
	}

}
