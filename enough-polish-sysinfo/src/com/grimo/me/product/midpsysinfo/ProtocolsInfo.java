/*
 * ProtocolsInfo.java
 *
 * Created on 29 de junio de 2004, 20:01
 */

package com.grimo.me.product.midpsysinfo;

/**
*
* @author  Waldemar Baraldi <waldemar.baraldi@grimo-software.com>
*/

import javax.microedition.media.*;

public class ProtocolsInfo {
    
    
    public static String[] getSupportedProtocols(String contentType){
        
        return Manager.getSupportedProtocols(contentType);
    }
    
    public static String[] getSupportedContentTypes(String protocol){
        
        return Manager.getSupportedContentTypes(protocol);
    }
}
