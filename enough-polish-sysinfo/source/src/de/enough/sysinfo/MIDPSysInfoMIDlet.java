/*
 * grimodemo.java
 *
 * Created on 29 de junio de 2004, 16:27
 */
package de.enough.sysinfo;

import java.util.Hashtable;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;

import com.grimo.me.product.midpsysinfo.ArithmeticBenchmarkInfoCollector;
import com.grimo.me.product.midpsysinfo.CreditsCanvas;
import com.grimo.me.product.midpsysinfo.DisplayInfoCollector;
import com.grimo.me.product.midpsysinfo.EventViewer;
import com.grimo.me.product.midpsysinfo.InfoCollector;
import com.grimo.me.product.midpsysinfo.InfoForm;
import com.grimo.me.product.midpsysinfo.KeysInfoCollector;
import com.grimo.me.product.midpsysinfo.LibrariesInfoCollector;
import com.grimo.me.product.midpsysinfo.MultiMediaInfoCollector;
import com.grimo.me.product.midpsysinfo.PlatformRequestViewer;
import com.grimo.me.product.midpsysinfo.SysPropCollector;
import com.grimo.me.product.midpsysinfo.SystemInfoCollector;

import de.enough.polish.rmi.RemoteClient;
import de.enough.polish.util.DeviceInfo;
import de.enough.polish.util.Locale;

/**
 * 
 * @author Waldemar Baraldi <waldemar.baraldi@grimo-software.com>
 */

public class MIDPSysInfoMIDlet extends MIDlet implements CommandListener {
	
	private final Command exitCmd = new Command(Locale.get("cmd.exit"),
			Command.EXIT, 10);

	public final Command backCmd = new Command(Locale.get("cmd.back"),
			Command.BACK, 1);

	private final Command selectCmd = new Command(Locale.get("cmd.select"),
			Command.OK, 1);

	private final Command sendInfoCmd = new Command("OK", Command.OK, 1);
	
	private final Command loginCmd = new Command("OK", Command.OK, 1);

	private List mainMenu;

	private Display display;

	public SysInfoServer remoteServer;

	public Form identForm = new Form(Locale.get("msg.deviceident"));
	
	public TextField vendorField = new TextField("Vendor: ", "", 50,
			TextField.ANY);

	public TextField deviceField = new TextField("Device: ", "", 50,
			TextField.ANY);
	
	public Form userForm;
	
	public TextField userField = new TextField(Locale.get("msg.user") + ": ", "anonymous", 20,
			TextField.ANY);

	public TextField pwdField = new TextField(Locale.get("msg.pwd") + ": ", "", 50,
			TextField.PASSWORD | TextField.ANY);

	public Form waitForm;
	
	public Form doneForm;

	public Hashtable keyInfos;
	
	public MIDPSysInfoMIDlet() {
		// initialize in initApp() (called from within startApp())
	}

	public void startApp() {
		Alert alert = null;
		if (this.mainMenu == null) {
			try {
				initApp();
			} catch (Exception e) {
				alert = new Alert("error", "Unhandled exception: " + e.toString(), null, AlertType.ERROR );
			}
		}
		this.display = Display.getDisplay(this);
		if (alert != null) {
			this.display.setCurrent( alert, this.mainMenu );
		} else {
			this.display.setCurrent( this.mainMenu );
		}
	}

	/**
	 * Initializes this application.
	 */
	private void initApp() {
		// correct translations are being loaded automatically by the J2ME Polish localizationf framework...
		//#debug
		System.out.println("init app");
		
		/* build main menu */
		this.mainMenu = new List(Locale.get("title.main"), List.IMPLICIT);
		this.mainMenu.append(Locale.get("cmd.display"), null);
		this.mainMenu.append(Locale.get("cmd.multimedia"), null);
		this.mainMenu.append(Locale.get("cmd.system"), null);
		this.mainMenu.append(Locale.get("cmd.libraries"), null);
		this.mainMenu.append(Locale.get("cmd.keys"), null);
		this.mainMenu.append(Locale.get("cmd.events"), null);
		this.mainMenu.append(Locale.get("cmd.platformRequest"), null);
		this.mainMenu.append(Locale.get("cmd.arithmeticBenchmark"), null);
		this.mainMenu.append(Locale.get("cmd.sysprops"), null);
		this.mainMenu.append(Locale.get("cmd.credits"), null);
		this.mainMenu.append(Locale.get("cmd.upload"), null);
		this.mainMenu.append(Locale.get("cmd.update"), null);
		this.mainMenu.append(Locale.get("cmd.exit"), null);
		this.mainMenu.addCommand(this.selectCmd);
		this.mainMenu.addCommand(this.exitCmd);
		this.mainMenu.setCommandListener( this );
		
		/* try to guess vendor and model */
		String model = getSystemProperty( new String[]{ "device.model", "microedition.platform" } );
		if(model != null){
			this.vendorField = new TextField(Locale.get("msg.vendor") + ": ", DeviceInfo.getVendorName(), 512, TextField.ANY);
			this.deviceField = new TextField(Locale.get("msg.device") + ": ", DeviceInfo.getDeviceName(), 512, TextField.ANY);
		}
		
		// device identifier form 
		this.identForm.addCommand(this.loginCmd);
		this.identForm.setCommandListener(this);
		this.identForm.append(this.vendorField);
		this.identForm.append(this.deviceField);
		this.identForm.append(Locale.get("msg.identinfo"));
		
		// user login form 
		this.userForm = new Form(Locale.get("msg.userif"));
		this.userForm.addCommand(this.sendInfoCmd);
		this.userForm.setCommandListener(this);
		this.userForm.append(this.userField);
		this.userForm.append(this.pwdField);
		this.userForm.append(Locale.get("msg.logininfo"));

		// wait form
		this.waitForm = new Form(Locale.get("msg.wait"));
		this.waitForm.append(Locale.get("msg.wait"));
		this.waitForm.setCommandListener(this);
		
		// done form
		this.doneForm = new Form(Locale.get("msg.wait"));
		this.doneForm.addCommand(this.backCmd);
		this.doneForm.setCommandListener(this);
			
	}

	/**
	 * Retrieves a system property
	 * @param propertyNames
	 * @return the value of the the first existing property
	 */
	public String getSystemProperty(String[] propertyNames) {
		for (int i = 0; i < propertyNames.length; i++) {
			String name = propertyNames[i];
			try {
				String value = System.getProperty(name);
				if (value != null) {
					return value;
				}
			} catch (Exception e) {
				//#debug 
				//System.out.println("Unable to resolve property " + name + e );
			}
		}
		return null;
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
	 * @param title
	 *            the title of the form
	 * @param collector
	 *            the info collector
	 */
	private void show(String title, InfoCollector collector) {
		collector.collectInfos(this, this.display);
		InfoForm form = new InfoForm(title, collector);
		setCommands(form);
		collector.show(this.display, form);
	}

	private void setCommands(Displayable disp) {
		disp.addCommand(this.backCmd);
		disp.setCommandListener(this);
	}

	private void show(Displayable disp) {
		setCommands(disp);
		this.display.setCurrent(disp);
	}

	/**
	 * shows a form with two textfields
	 * to put vendor and device in
	 */
	public void deviceIdentify() {
		this.display.setCurrent(this.identForm);
	}

	/**
	 * shows a form with two textfields
	 * to put login and password in
	 */
	public void userIdentify() {
		this.display.setCurrent(this.userForm);
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command,
	 *      javax.microedition.lcdui.Displayable)
	 */
	public void commandAction(Command cmd, Displayable disp) {
		if ((cmd == List.SELECT_COMMAND || cmd == this.selectCmd)
				&& disp == this.mainMenu) {
			switch (this.mainMenu.getSelectedIndex()) {
			case 0:
				show(Locale.get("title.display"), new DisplayInfoCollector());
				return;
			case 1:
				show(Locale.get("title.multimedia"),
						new MultiMediaInfoCollector());
				return;
			case 2:
				show(Locale.get("title.system"), new SystemInfoCollector());
				return;
			case 3:
				show(Locale.get("title.libraries"),
						new LibrariesInfoCollector());
				return;
			case 4:
				show(Locale.get("title.keys"), new KeysInfoCollector());
				return;
			case 5:
				this.display.setCurrent( new EventViewer(this) );
				return;
			case 6:
				this.display.setCurrent( new PlatformRequestViewer(this) );
				return;
			case 7:
				show(Locale.get("title.arithmeticBenchmark"),
						new ArithmeticBenchmarkInfoCollector());
				return;
			case 8:
				show(Locale.get("cmd.sysprops"),
						new SysPropCollector());
				return;
			case 9:
				show(new CreditsCanvas());
				return;
			case 10:
				show(Locale.get("title.keys"), new KeyCollector(this));
				return;
			case 11:
				new UpdateChecker(this.display, this);
				break;
			case 12:
				cmd = this.exitCmd;
				break;
			}
		}
		if (cmd == this.backCmd) {
			this.display.setCurrent(this.mainMenu);
		} else if (cmd == this.exitCmd) {
			destroyApp(true);
			notifyDestroyed();
		}
		if (cmd == this.loginCmd) {
			//System.out.println("login pressed");
			userIdentify();
			return;
		}
		if (cmd == this.sendInfoCmd) {
			if (this.remoteServer == null) {
				try {
					//#debug
					//System.out.println("open server");
					//#if localbuild==true
					this.remoteServer = (SysInfoServer) RemoteClient.open("de.enough.sysinfo.SysInfoServer","http://localhost:8080/sysinfoserver");
					//#elif remotebuild==true
					this.remoteServer = (SysInfoServer) RemoteClient.open("de.enough.sysinfo.SysInfoServer","http://sysinfo.j2mepolish.org/sysinfoserver");
					//#endif
				} catch (Exception e) {
					//#debug error
					System.err.println("Unable to open remote server: " + e);
				}
			}
			//System.out.println("send info pressed");
			new SysinfoUploader(this.display, this);
		}
	}
	
	public void showMainMenu() {
		this.display.setCurrent( this.mainMenu );
	}

	/*public Command getBackCmd() {
		return this.backCmd;
	}
	
	public Form getIdentForm() {
		return this.identForm;
	}

	public Form getDoneForm() {
		return this.doneForm;
	}

	public Form getWaitForm() {
		return this.waitForm;
	}
	
	public Hashtable getKeyInfos() {
		return this.keyInfos;
	}

	public void setKeyInfos(Hashtable keyInfos) {
		this.keyInfos = keyInfos;
	}
	*/
}
