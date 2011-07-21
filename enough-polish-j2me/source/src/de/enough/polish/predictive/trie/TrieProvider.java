//#condition polish.usePolishGui && polish.TextField.usePredictiveInput && polish.TextField.useDirectInput && !(polish.blackberry || polish.android)
package de.enough.polish.predictive.trie;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import de.enough.polish.ui.Form;
import de.enough.polish.ui.StyleSheet;
import de.enough.polish.ui.TextField;
import de.enough.polish.util.HashMap;

public class TrieProvider {
	
	//#if polish.Bugs.sharedRmsRequiresSigning || polish.predictive.uselocalRMS || polish.midp1
		//#define tmp.useLocalRMS
	//#endif
	
	private int type;
	private boolean init = false;
	
	//TYPE_TRIE
	private RecordStore store = null;
	private HashMap records   = null; 
	
	private int version		= 0;
	private int chunkSize 	= 0;
	private int lineCount 	= 0;
	
	private int maxRecords = 5;
	
	private TrieCustom custom = null;
	private TrieOrder order = null;
	
	private Form customForm = null;
	private TextField customField = null;
	
	//TYPE_ARRAY
	
	public TrieProvider()
	{
		this.init = false;
	}
	
	public static boolean isPredictiveInstalled()
	{
		RecordStore rms = null;
		try
		{
			//#if tmp.useLocalRMS || polish.Bugs.sharedRmsRequiresSigning
				rms = RecordStore.openRecordStore(TrieInstaller.PREFIX + "_0", false);
			//#else
				rms = RecordStore.openRecordStore(TrieInstaller.PREFIX + "_0","Enough Software","PredictiveSetup");
			//#endif
				
			rms.closeRecordStore();
		}
		catch(RecordStoreException e)
		{
			return false;
		}
		
		return true;
	}
	
	public void init() throws RecordStoreException
	{	
		//#if tmp.useLocalRMS || polish.Bugs.sharedRmsRequiresSigning 
			this.store 	= RecordStore.openRecordStore(TrieInstaller.PREFIX + "_0", false);
		//#else
			this.store 	= RecordStore.openRecordStore(TrieInstaller.PREFIX + "_0","Enough Software","PredictiveSetup");
		//#endif
		
		this.records = new HashMap();
		
		byte[] bytes = this.store.getRecord(TrieInstaller.HEADER_RECORD);
		
		this.version	= TrieUtils.byteToInt(bytes, TrieInstaller.VERSION_OFFSET);
		
		if(this.version != TrieInstaller.VERSION)
		{
			throw new RecordStoreException("");
		}
		
		this.chunkSize 	= TrieUtils.byteToInt(bytes, TrieInstaller.CHUNKSIZE_OFFSET);
		this.lineCount 	= TrieUtils.byteToInt(bytes, TrieInstaller.LINECOUNT_OFFSET);
		
		this.custom = new TrieCustom();
		
		this.order = new TrieOrder();
		
		this.customForm = new Form("Add new word");
		
		this.customForm.addCommand( StyleSheet.CANCEL_CMD );
		this.customForm.addCommand( StyleSheet.OK_CMD );
		
		this.init = true;
	}
	
	public boolean isInit()
	{
		return this.init;
	}
	
	public byte[] getRecord(int id) throws RecordStoreException
	{
		Integer recordMapID = new Integer(id);
		TrieRecord record = (TrieRecord)this.records.get(recordMapID); 
		
		if(record == null)
		{
			record = new TrieRecord(id,this.store.getRecord(id));
			
			this.records.put(recordMapID, record);
		}
		
		record.addReference();
		
		return record.getRecord();
	}
	
	public void setRecord(int id, byte[] bytes) throws RecordStoreException
	{
		Integer recordMapID = new Integer(id);
		TrieRecord record = (TrieRecord)this.records.get(recordMapID);
		
		if(record == null)
		{
			record = new TrieRecord(id,bytes);
			this.records.put(recordMapID, record);
		}
		
		this.store.setRecord(id, bytes, 0, bytes.length);
	}
	
	public void releaseRecords()
	{
		if(this.records != null)
		{
			Object[] keys = this.records.keys();
		
			Integer keyToRemove = null;
			int	references = 0;
			
			while(keys.length > this.maxRecords)
			{
				references = 1000;
				
				for(int i=0; i<keys.length; i++)
				{
					Integer key = (Integer)keys[i];
					TrieRecord record = (TrieRecord)this.records.get(key);
					
					if(record.getReferences() <= references && record.getId() != TrieInstaller.CUSTOM_RECORD && record.getId() != TrieInstaller.ORDER_RECORD)
					{
						keyToRemove = (Integer)keys[i];
						references = record.getReferences();
					}
				}
				
				this.records.remove(keyToRemove);
				
				keys = this.records.keys();
			}
		}
	}

	public int getChunkSize() {
		return this.chunkSize;
	}

	public TrieCustom getCustom() {
		return this.custom;
	}
	
	public TrieOrder getOrder() {
		return this.order;
	}

	public int getLineCount() {
		return this.lineCount;
	}

	public Form getCustomForm() {
		return this.customForm;
	}

	public TextField getCustomField() {
		return this.customField;
	}

	public void setCustomField(TextField customField) {
		this.customField = customField;
	}
}
