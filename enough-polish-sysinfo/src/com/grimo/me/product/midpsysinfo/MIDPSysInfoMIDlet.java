/*
 * grimodemo.java
 *
 * Created on 29 de junio de 2004, 16:27
 */

package com.grimo.me.product.midpsysinfo;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

import de.enough.polish.util.Locale;

/**
*
* @author  Waldemar Baraldi <waldemar.baraldi@grimo-software.com>
*/

public class MIDPSysInfoMIDlet 
extends MIDlet
implements CommandListener
{
	/*
    private final Command displayInfoCmd = new Command(Locale.get("cmd.display"), Command.SCREEN, 1);
    private final Command soundInfoCmd = new Command(Locale.get("cmd.multimedia"), Command.SCREEN, 2);
    private final Command systemInfoCmd = new Command(Locale.get("cmd.system"), Command.SCREEN, 3);
    private final Command gameControlsInfoCmd = new Command(Locale.get("cmd.keys"), Command.SCREEN, 4);
    private final Command arithmeticBenchmarkCmd = new Command(Locale.get("cmd.arithmeticBenchmark"), Command.SCREEN, 6);
    private final Command creditsCmd = new Command(Locale.get("cmd.credits"), Command.SCREEN, 6);
    */
    private final Command exitCmd = new Command(Locale.get("cmd.exit"), Command.EXIT, 10);
    private final Command backCmd = new Command(Locale.get("cmd.back"), Command.BACK, 1);
    private final Command selectCmd = new Command(Locale.get("cmd.select"), Command.OK, 1);
    
    
    private List mainMenu;
	private Display display;
    
    public MIDPSysInfoMIDlet(){
    	this.mainMenu = new List(Locale.get("title.main"), List.IMPLICIT );
    	this.mainMenu.append( Locale.get("cmd.display"), null);
    	this.mainMenu.append(Locale.get("cmd.multimedia"), null);
    	this.mainMenu.append(Locale.get("cmd.system"), null);
    	this.mainMenu.append(Locale.get("cmd.libraries"), null);
    	this.mainMenu.append(Locale.get("cmd.keys"), null);
    	this.mainMenu.append(Locale.get("cmd.arithmeticBenchmark"), null);
    	this.mainMenu.append(Locale.get("cmd.credits"), null);
    	this.mainMenu.append(Locale.get("cmd.exit"), null);
    	
    }
    
    public void startApp() {
        this.display = Display.getDisplay( this );
        show( this.mainMenu );
    }
    
    public void pauseApp() {
    	// ignore
    }
   
    public void destroyApp(boolean unconditional) {
    	// nothing to be cleaned
    }
    
	/**
	 * Shows an information collection.
	 * 
	 * @param title the title of the form
	 * @param collector the info collector
	 */
	private void show( String title, InfoCollector collector) {
		collector.collectInfos(this, this.display);
		InfoForm form = new InfoForm( title, collector );
		setCommands(form);
		collector.show(this.display, form);
	}
	
	private void setCommands( Displayable disp) {
		/*
    	disp.addCommand( this.displayInfoCmd );
    	disp.addCommand( this.soundInfoCmd );
    	disp.addCommand( this.systemInfoCmd );
    	disp.addCommand( this.gameControlsInfoCmd );
    	disp.addCommand( this.arithmeticBenchmarkCmd );
    	disp.addCommand( this.creditsCmd );
    	*/
    	if (disp == this.mainMenu) {
        	disp.addCommand( this.selectCmd );
        	disp.addCommand( this.exitCmd );
    	} else {
    		disp.addCommand( this.backCmd );
    	}
    	disp.setCommandListener( this );
	}
	
    private void show( Displayable disp) {
    	setCommands(disp);
    	this.display.setCurrent( disp );
    }

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
	 */
	public void commandAction(Command cmd, Displayable disp) {
		if ( (cmd == List.SELECT_COMMAND || cmd == this.selectCmd) && disp == this.mainMenu) {
			switch( this.mainMenu.getSelectedIndex() ) {
				case 0:
					show( Locale.get("title.display"), new DisplayInfoCollector() );
					return;
				case 1:
					show( Locale.get("title.multimedia"), new MultiMediaInfoCollector() );
					return;
				case 2:
					show( Locale.get("title.system"), new SystemInfoCollector() );
					return;
				case 3:
					show( Locale.get("title.libraries"), new LibrariesInfoCollector() );
					return;
				case 4:
					show( Locale.get("title.keys"), new KeysInfoCollector() );
					return;
				case 5:
					show( Locale.get("title.arithmeticBenchmark"), new ArithmeticBenchmarkInfoCollector() );
					return;
				case 6:
					show( new CreditsCanvas() );
					return;
				case 7:
					cmd = this.exitCmd;
					break;
			}
		}
		if (cmd == this.backCmd ) {
			this.display.setCurrent( this.mainMenu );
		} else if (cmd == this.exitCmd) {
			destroyApp( true );
			notifyDestroyed();
		}
		
	}


}
