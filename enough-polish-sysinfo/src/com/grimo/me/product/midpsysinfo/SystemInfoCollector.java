/*
 * SystemInfoForm.java
 *
 * Created on 29 de junio de 2004, 21:01
 */

package com.grimo.me.product.midpsysinfo;


import javax.microedition.lcdui.Display;

/**
 * Collects information about the system mainly by querying sytem properties.
 * 
 * @author  Waldemar Baraldi <waldemar.baraldi@grimo-software.com>
 * @author  Robert Virkus <j2mepolish@enough.de> (architectural changes)
 */
public class SystemInfoCollector extends InfoCollector {
    
    
    /** 
     * Creates a new instance of SystemInfoCollector 
     */
    public SystemInfoCollector() {
    	super();
    }

	/* (non-Javadoc)
	 * @see com.grimo.me.product.midpsysinfo.InfoCollector#collectInfos(com.grimo.me.product.midpsysinfo.MIDPSysInfoMIDlet, javax.microedition.lcdui.Display)
	 */
	public void collectInfos(MIDPSysInfoMIDlet midlet, Display display) {
        addInfo( "Microedition Configuration: ", getInfo( System.getProperty("microedition.configuration") ) );
        
        addInfo( "Microedition Profiles: ", getInfo(System.getProperty("microedition.profiles"))  );
        
        addInfo( "microedition.jtwi.version:", getInfo( System.getProperty("microedition.jtwi.version") ));
        
        addInfo( "microedition.platform:", getInfo( System.getProperty("microedition.platform") ) );

        addInfo( "microedition.locale:", getInfo( System.getProperty("microedition.locale") ));

        addInfo( "default encoding:", getInfo( System.getProperty("microedition.encoding") ));

        String[] timeZoneIDs = java.util.TimeZone.getAvailableIDs();
        StringBuffer timeZonesBuffer = new StringBuffer();
        for ( int i = 0; i < timeZoneIDs.length; i++ ) {
            timeZonesBuffer.append( timeZoneIDs[i] ).append( '\n' );
        }
        
        addInfo( "Total memory:", Long.toString(Runtime.getRuntime().totalMemory()) + " bytes");
        
        addInfo( "Available TimeZones:", timeZonesBuffer.toString() );
        
        addInfo( "Default TimeZone:", java.util.TimeZone.getDefault().getID());
	}
    

}
