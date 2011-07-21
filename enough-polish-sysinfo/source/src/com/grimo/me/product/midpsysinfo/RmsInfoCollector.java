/*
 * SoundInfoForm.java
 *
 * Created on 29 de junio de 2004, 19:34
 */

package com.grimo.me.product.midpsysinfo;

import javax.microedition.lcdui.Display;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import de.enough.sysinfo.MIDPSysInfoMIDlet;

/**
 * Collects information about the sound support of the device.
 * 
 * @author  Waldemar Baraldi <waldemar.baraldi@grimo-software.com>
 * @author  Robert Virkus <j2mepolish@enough.de> (architectural changes)
 * @author  Mark Schrijver <mark.schrijver@mobillion.nl> (additional system properties)
 */
public class RmsInfoCollector extends InfoCollector {
    
    
    /** 
     * Creates a new instance of SoundInfoCollector 
     */
    public RmsInfoCollector() {
        super();
    }
    
    /* (non-Javadoc)
	 * @see com.grimo.me.product.midpsysinfo.InfoCollector#collectInfos(com.grimo.me.product.midpsysinfo.MIDPSysInfoMIDlet, javax.microedition.lcdui.Display)
	 */
	public void collectInfos(MIDPSysInfoMIDlet midlet, Display display) {
        
        int lastSizeAvailable = -1;
        int maxSizeAvailable = -1;
        boolean isSizeAvailableStable = true;
        int lowestRecordSetId = Integer.MAX_VALUE;
        int highestRecordSetId = Integer.MIN_VALUE;
		byte[] data = "HelloWorldHowAreYou?".getBytes();
        for (int i = 0; i < 10; i++) {
        	try {
				RecordStore recordStore = RecordStore.openRecordStore("test",true);
				int sizeAvailable = recordStore.getSizeAvailable();
				if (sizeAvailable > maxSizeAvailable) {
					maxSizeAvailable = sizeAvailable;
				}
				if (lastSizeAvailable != -1  &&  lastSizeAvailable != sizeAvailable ) {
					isSizeAvailableStable = false;
				}
				lastSizeAvailable = sizeAvailable;
				int recordSetId = recordStore.addRecord( data, 0, data.length );
				if (recordSetId > highestRecordSetId) {
					highestRecordSetId = recordSetId;
				}
				if (recordSetId < lowestRecordSetId) {
					lowestRecordSetId = recordSetId;
				}
			} catch (RecordStoreException e) {
				e.printStackTrace();
			}        	
			try {
		        RecordStore.deleteRecordStore("test");
	        } catch (Exception e) {
				e.printStackTrace();
	        }        		
		}
        addInfo("rms.SizeStableAfterDeletingStore", ""+ isSizeAvailableStable);
        addInfo("rms.MaxSizePerRecordStore", "" + (maxSizeAvailable/1024) + " kb");
        boolean firstIdStable = highestRecordSetId == lowestRecordSetId;
        if (firstIdStable) {
        	addInfo("rms.FirstRecordSetId", "" + highestRecordSetId );
        } else {
        	
        	addInfo("rms.FirstRecordSetIdNotStable", "true");
        	addInfo("rms.LowesFirstRecordSetId", "" + lowestRecordSetId );
        	addInfo("rms.HighestFirstRecordSetId", "" + highestRecordSetId );
        }
        
        int maxId = -1;
        int numberOfRecordStores = 31;
        try {
	        for (int i = 0; i < numberOfRecordStores; i++) {
	        	RecordStore.openRecordStore("i" + i , true );
	        	maxId = i;
			}
        } catch (RecordStoreException e) {
        	// it's over
        }
        for (int i = 0; i <= maxId; i++) {
            try {
        	RecordStore.deleteRecordStore("i" + i );
            } catch (RecordStoreException e) {
            	// ignore
            }
		}
        if (maxId < numberOfRecordStores) {
        	addInfo("rms.maxNumberRecordStores",  "" + maxId );
        } else {
        	addInfo("rms.maxNumberRecordStores",  ">" + maxId );
        }
	}
 }
