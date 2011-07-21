//#condition polish.usePolishGui && polish.TextField.usePredictiveInput && polish.TextField.useDirectInput && !(polish.blackberry || polish.android)
package de.enough.polish.predictive.trie;

import java.io.DataInputStream;
import java.util.Vector;

import javax.microedition.io.ConnectionNotFoundException;
import de.enough.polish.ui.Alert;
import de.enough.polish.ui.Command;
import de.enough.polish.ui.CommandListener;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import de.enough.polish.ui.Form;
import de.enough.polish.ui.Gauge;
import de.enough.polish.ui.List;
import de.enough.polish.ui.StringItem;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

import de.enough.polish.io.RedirectHttpConnection;
import de.enough.polish.util.Locale;
import de.enough.polish.ui.PredictiveAccess;
import de.enough.polish.ui.StyleSheet;

public class TrieSetup 
implements Runnable, CommandListener
{
	private PredictiveAccess parent;
	private Vector listeners;
	
	TrieInstaller installer = null;
	
	protected Command exitCommand = new Command( Locale.get("polish.predictive.setup.cmd.exit"), Command.EXIT, 0 );
	protected Command cancelCommand = new Command( Locale.get("polish.predictive.setup.cmd.cancel"), Command.CANCEL, 0 );
	
	protected Command yesCommand = new Command( Locale.get("polish.predictive.setup.cmd.yes"), Command.OK, 0 );
	protected Command noCommand = new Command( Locale.get("polish.predictive.setup.cmd.no"), Command.CANCEL, 0 );
	
	List list = null;
	
	Form setupForm = null;
	
	StringItem info = null;
	StringItem status = null;
	StringItem error = null;
	Gauge gauge = null;
		
	Alert infoAlert = null;
	Alert cancelAlert = null;
	
	boolean pause = false;
	
	public TrieSetup(PredictiveAccess access)
	{
		this.parent = access;
	}
	
	public void initForm()
	{
		//#style setupForm?
		this.setupForm = new Form( null );
		
		//#if polish.predictive.setup.showCommands
		this.setupForm.addCommand( this.cancelCommand );
		this.setupForm.setCommandListener( this );
		//#endif
		
		this.info = new StringItem(null,Locale.get("polish.predictive.setup.info"));
		this.status = new StringItem(null,"");
		this.error = new StringItem(null,"");
		this.gauge = new Gauge(null,true,100,0);
		
		//#style setupIntro?
		this.setupForm.append(this.info);
		
		//#if !polish.Bugs.sharedRmsRequiresSigning
			//#style setupGauge?
			this.setupForm.append(this.gauge);
		//#endif
		
		//#style setupStatus?
		this.setupForm.append(this.status);
		//#style setupError?
		this.setupForm.append(this.error);
	}
	
	public DataInputStream getStream()
	{
		RedirectHttpConnection connection = null;
		
		try {
			DataInputStream in;
			//#if !polish.predictive.useLocalRMS && (polish.Bugs.sharedRmsRequiresSigning || polish.midp1)
				connection = new RedirectHttpConnection(
					"http://dl.j2mepolish.org/predictive/index.jsp?type=local&lang=en");

				in = connection.openDataInputStream();
			//#else
				in = new DataInputStream(getClass().getResourceAsStream("/predictive.trie"));
			//#endif
			return in;
		} catch (Exception e) {
			//#debug error
			System.out.println("Unable to download dictionary " + e);
			return null;
		} 
	}
	
	public void pause()
	{
		this.pause = true;
	}
	
	public void registerListener(TrieSetupCallback listener)
	{
		if(this.listeners == null)
		{
			this.listeners = new Vector();
		}
		
		this.listeners.addElement(listener);
	}
	
	public void runCallback(boolean finishedGraceful)
	{
		if(this.listeners != null)
		{
			for (int i = 0; i < this.listeners.size(); i++) {
				TrieSetupCallback callback = (TrieSetupCallback)this.listeners.elementAt(i);
				callback.setupFinished(finishedGraceful);
			}
		}
	}
	
	private void forceClose(String recordStore)
	{
		RecordStore store = null;
		try
		{
			store = RecordStore.openRecordStore(recordStore, true);
		}
		catch(RecordStoreException e)
		{
			this.status.setText(e.getMessage());
		}
		
		if(store != null)
		{
			try {
				while(true)
				{
					store.closeRecordStore();
				}
			} catch (RecordStoreNotOpenException e) {
				//All closed
			} catch (RecordStoreException e) 
			{
				this.status.setText(e.getMessage());
			}
		}
	}
	
	public void run()
	{
		initForm();
		
		Display display = Display.getInstance();
		while (display == null) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// ignpore
			}
			display = Display.getInstance();
		}
		display.setCurrent(this.setupForm);
		
		try {
			DataInputStream stream = getStream();
			this.installer = new TrieInstaller(stream);
			
			this.status.setText(Locale.get("polish.predictive.setup.status.delete"));
			
			String[] storeList = RecordStore.listRecordStores();
			
			if(storeList != null)
			{
				for(int i=0; i<storeList.length; i++) {
					String store = storeList[i];
					if(store.startsWith(TrieInstaller.PREFIX))
					{
						forceClose(store);
						RecordStore.deleteRecordStore(store);
					}
				}
			}
			
			this.status.setText(Locale.get("polish.predictive.setup.status.install"));
			
			int totalBytes = stream.available();
			this.gauge.setMaxValue(totalBytes);
			
			byte[] nodes;
			RecordStore store = null;
			
			int count = 0;
			int storeID = 0;
			
			do
			{
				nodes = null;
				
				if((count % this.installer.getChunkSize()) == 0)
				{
					if(store != null)
					{
						store.closeRecordStore();
						storeID +=this. installer.getChunkSize();
					}
					
					//#if (polish.Bugs.sharedRmsRequiresSigning || polish.predictive.useLocalRMS || polish.midp1)
						store = RecordStore.openRecordStore(TrieInstaller.PREFIX + "_" + storeID, true); 
					//#else
						store = RecordStore.openRecordStore(TrieInstaller.PREFIX + "_" + storeID, true, RecordStore.AUTHMODE_ANY, true);
					//#endif
					
					if(storeID == 0)
					{
						this.installer.createHeaderRecord(store);
						this.installer.createCustomRecord(store);
						this.installer.createOrderRecord(store);
					}
				}
					
				nodes = this.installer.getRecords(stream, this.installer.getLineCount());
				//TODO ANDRE: InpuStream.available() may return 0!!!
				this.gauge.setValue(totalBytes - stream.available());
				
				count++;
				
				store.addRecord(nodes, 0, nodes.length);
				
				if(this.pause)
				{
					try
					{
						store.closeRecordStore();
						
						synchronized (this){
							this.wait();
						}
						
						//#if (polish.Bugs.sharedRmsRequiresSigning || polish.predictive.useLocalRMS || polish.midp1)
							store = RecordStore.openRecordStore(TrieInstaller.PREFIX + "_" + storeID, true);
						//#else
							store = RecordStore.openRecordStore(TrieInstaller.PREFIX + "_" + storeID, true, RecordStore.AUTHMODE_ANY, true);
						//#endif
						
					} catch(InterruptedException e){
						// ignore
					}
					this.pause = false;
				}
				
				//TODO ANDRE: InpuStream.available() may return 0!!!
			}while(stream.available() > 0);
			
			//Add a record with the magic number to check for failed installations
			byte[] magicBytes = TrieUtils.intToByte(TrieInstaller.MAGIC);
			store.addRecord(magicBytes, 0, magicBytes.length);
			
			store.closeRecordStore();
			
			stream.close();
			
			this.status.setText(Locale.get("polish.predictive.setup.status.finished"));
			
			//#if polish.predictive.setup.showCommands
				this.setupForm.removeCommand( this.cancelCommand );
				this.setupForm.addCommand( this.exitCommand );
			//#endif
			
			//#if polish.predictive.useLocalRMS
				if(this.parent != null)
				{
					this.parent.initPredictiveInput(null);
				}
			//#endif
			
			//#if !polish.predictive.setup.showCommands
				runCallback(true);
			//#endif
		} catch (Exception e) {
			this.status.setText(Locale.get("polish.predictive.setup.error"));
			this.status.setText(e.getMessage());
			
			//#if polish.predictive.setup.showCommands
				this.setupForm.removeCommand( this.cancelCommand );
				this.setupForm.addCommand( this.exitCommand );
			//#endif
			
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e1) {
				// ignore
			}
			//#debug error
			System.out.println("Error during predictive setup" + e);
			
			runCallback(false);
		}
	}

	public Form getSetupForm() {
		return this.setupForm;
	}
	
	public void showInfo()
	{
		//#style predictiveInstallDialog?
		this.infoAlert = new Alert(Locale.get("polish.predictive.download.title"));
		
		//#if polish.predictive.useLocalRMS
			//#style predictiveInstallMessage?
			this.infoAlert.setString(Locale.get("polish.predictive.local.message"));
		//#else
			//#style predictiveInstallMessage?
			this.infoAlert.setString(Locale.get("polish.predictive.download.message") );
		//#endif

		this.infoAlert.addCommand(StyleSheet.CANCEL_CMD);
		this.infoAlert.addCommand(StyleSheet.OK_CMD);

		this.infoAlert.setCommandListener(this);

		StyleSheet.display.setCurrent(this.infoAlert);
	}
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
	 */
	public void commandAction(Command cmd, Displayable disp) {
		if (cmd == this.cancelCommand) {
			//#style setupForm?
			this.cancelAlert = new Alert( null );
			
			this.cancelAlert.setString(Locale.get("polish.predictive.setup.cancel"));
			this.cancelAlert.addCommand(this.yesCommand);
			this.cancelAlert.addCommand(this.noCommand);
			
			this.cancelAlert.setCommandListener(this);
			
			this.pause();
			
			StyleSheet.display.setCurrent(this.cancelAlert);
		} else if (cmd == this.exitCommand) {
			runCallback(true);
		} 
		
		if(disp == this.cancelAlert)
		{
			if(cmd == this.yesCommand)
			{
				runCallback(false);
			} 
			else if(cmd == this.noCommand)
			{
				synchronized (this){
					this.notify();
				}
				
				StyleSheet.display.setCurrent(this.setupForm);
			}
		}
		
		if(disp == this.infoAlert)
		{
			if(cmd == StyleSheet.OK_CMD)
			{
				//#if polish.predictive.useLocalRMS || polish.Bugs.sharedRmsRequiresSigning || polish.midp1
					registerListener(this.parent);
					Thread thread = new Thread(this);
					thread.start();
				//#else
					try {
						StyleSheet.midlet.platformRequest("http://dl.j2mepolish.org/predictive/index.jsp?type=shared");
						StyleSheet.midlet.notifyDestroyed();
					} catch (ConnectionNotFoundException e) {
						//#debug error
						System.out.println("Unable to load dictionary app" + e);
					}
					StyleSheet.display.setCurrent(this.parent.getParent().getScreen());
				//#endif
			} 
			else if(cmd == StyleSheet.CANCEL_CMD)
			{
				StyleSheet.display.setCurrent(this.parent.getParent().getScreen());
			}
		}
	}
	
	public void install()
	{
		registerListener(this.parent);
		Thread thread = new Thread(this);
		thread.start();
	}
}