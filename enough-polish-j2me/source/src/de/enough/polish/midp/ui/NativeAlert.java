//#condition polish.usePolishGui && polish.midp && !polish.blackberry && polish.useNativeAlerts
package de.enough.polish.midp.ui;

import javax.microedition.lcdui.Displayable;

import de.enough.polish.ui.Alert;
import de.enough.polish.ui.AlertType;
import de.enough.polish.ui.Ticker;

/**
 * A native alert for MIDP devices.
 * Enable this feature with the preprocessing variable polish.useNativeAlerts=true:
 * <pre>
 * &lt;variable name=&quot;polish.useNativeAlerts&quot; value=&quot;true&quot; /&gt;
 * </pre>
 * 
 * @author Robert Virkus, j2mepolish@enough.de
 *
 */
public class NativeAlert
extends javax.microedition.lcdui.Alert
implements de.enough.polish.ui.Displayable, javax.microedition.lcdui.CommandListener
{
	private final Alert	alert;

	public NativeAlert(de.enough.polish.ui.Alert alert)
	{
		super(alert.getTitle());
		String title = alert.getTitle();
		String content = alert.getString();
		if ((title == null && content != null) || !title.equals(content))
		{
			setString(content);
		}
		if (alert.getImage() != null)
		{
			setImage(alert.getImage());
		}
		setTimeout(alert.getTimeout());
		this.alert = alert;
		AlertType alertType = alert.getType();
		javax.microedition.lcdui.AlertType nativeAlertType;
		if (alertType == null || alertType == AlertType.INFO)
		{
			nativeAlertType = javax.microedition.lcdui.AlertType.INFO; 
		}
		else if (alertType == AlertType.ALARM)
		{
			nativeAlertType = javax.microedition.lcdui.AlertType.ALARM;
		}
		else if (alertType == AlertType.CONFIRMATION)
		{
			nativeAlertType = javax.microedition.lcdui.AlertType.CONFIRMATION;
		}
		else if (alertType == AlertType.ERROR)
		{
			nativeAlertType = javax.microedition.lcdui.AlertType.ERROR;
		}
		else if (alertType == AlertType.WARNING)
		{
			nativeAlertType = javax.microedition.lcdui.AlertType.WARNING;
		}
		else
		{
			nativeAlertType = javax.microedition.lcdui.AlertType.INFO;
		}
		setType(nativeAlertType);
		Object[] commands = alert.getCommands();
		if (commands != null)
		{
			for (int i = 0; i < commands.length; i++)
			{
				de.enough.polish.ui.Command command = (de.enough.polish.ui.Command) commands[i];
				if (command == null)
				{
					break;
				}
				addCommand(command);
			}
		}
		setCommandListener(this);
	}

	public Ticker getPolishTicker()
	{
		return null;
	}

	public void setTicker(Ticker ticker)
	{
		// ignore
	}

	public void addCommand(de.enough.polish.ui.Command cmd)
	{
		super.addCommand(cmd);
	}

	public void removeCommand(de.enough.polish.ui.Command cmd)
	{
		super.removeCommand(cmd);
	}

	public void setCommandListener(de.enough.polish.ui.CommandListener l)
	{
		// ignore
	}
	
	public void sizeChanged(int width, int height)
	{
		// ignore
	}

	public void commandAction(javax.microedition.lcdui.Command cmd, javax.microedition.lcdui.Displayable disp)
	{
		if (cmd instanceof de.enough.polish.ui.Command)
		{
			this.alert.getCommandListener().commandAction( (de.enough.polish.ui.Command)cmd, this.alert);
		}
		
	}
}
