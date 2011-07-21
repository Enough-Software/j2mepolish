package de.enough.polish.sample.browser;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import de.enough.polish.browser.html.HtmlBrowser;

/**
 * <p>Demonstrates a simple browser app using the HtmlBrowser component of J2ME Polish.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class BrowserMidlet 
	extends MIDlet
	implements CommandListener
{
	private Command cmdBack = new Command("Back", Command.BACK, 9);
	private Command cmdExit = new Command("Exit", Command.EXIT, 10 );
	private Form browserScreen;
	private HtmlBrowser htmlBrowser;

     protected void startApp() throws MIDletStateChangeException{
          Display display = Display.getDisplay( this );
          if (this.browserScreen == null) {
	          //#style browserScreen
	          Form form = new Form("HTML Browser");
	          //#style browser
	          this.htmlBrowser = new HtmlBrowser();
	          // you can add a gzip resource handler for reading gzipped resources:
	          //this.htmlBrowser.addProtocolHandler( new GZipResourceProtocolHandler() );
	          new ChartTagHandler( this.htmlBrowser.getTagHandler("div")).register(this.htmlBrowser);
	          form.append(this.htmlBrowser);
	          this.htmlBrowser.go( "resource://index.html");
	          this.htmlBrowser.setBackCommand( this.cmdBack );
	          form.addCommand( this.cmdExit );
	          form.setCommandListener( this );
	          this.browserScreen = form;
          }
          display.setCurrent( this.browserScreen );
     }

     protected void pauseApp(){
          // ignore
     }

     protected void destroyApp(boolean unconditional) throws MIDletStateChangeException{
          // just exit
     }

	public void commandAction(Command cmd, Displayable disp) {
	    //#debug
		System.out.println("BrowserMidlet.commandAction for cmd=" + cmd.getLabel() );
		if (cmd == this.cmdBack) {
			this.htmlBrowser.goBack();
		} else if (cmd == this.cmdExit) {
			//destroyApp( true );
			notifyDestroyed();
		}
	}

}