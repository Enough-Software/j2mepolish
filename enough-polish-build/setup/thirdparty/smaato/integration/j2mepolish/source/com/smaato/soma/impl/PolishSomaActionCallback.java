package com.smaato.soma.impl;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDletStateChangeException;

import com.smaato.soma.SomaActionCallback;

import de.enough.polish.ui.StyleSheet;

/**
 * <p>A default action callback for J2ME Polish based applications</p>
 *
 * <p>Copyright Smaato Inc. 2007, 2008</p>
 */
public class PolishSomaActionCallback implements SomaActionCallback
{

	/* (non-Javadoc)
	 * @see com.smaato.soma.SomaActionCallback#destroyApp(boolean)
	 */
	public void destroyApp(boolean unconditional)
			throws MIDletStateChangeException
	{
		StyleSheet.midlet.notifyDestroyed();

	}

	/* (non-Javadoc)
	 * @see com.smaato.soma.SomaActionCallback#platformRequest(java.lang.String)
	 */
	public boolean platformRequest(String url)
			throws ConnectionNotFoundException
	{
		return StyleSheet.midlet.platformRequest(url);
	}

	/* (non-Javadoc)
	 * @see com.smaato.soma.SomaActionCallback#getDisplay()
	 */
	public Display getDisplay()
	{
		return Display.getDisplay( StyleSheet.midlet );
	}

}
