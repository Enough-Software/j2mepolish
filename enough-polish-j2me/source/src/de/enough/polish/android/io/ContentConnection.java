//#condition polish.android
package de.enough.polish.android.io;

public interface ContentConnection extends StreamConnection {

	String getEncoding(); 
    
	long getLength(); 
    
	String getType(); 
	
}
