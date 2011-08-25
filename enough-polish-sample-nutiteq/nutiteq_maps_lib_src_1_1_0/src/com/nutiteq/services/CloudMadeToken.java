package com.nutiteq.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import com.nutiteq.log.Log;

public class CloudMadeToken {
  // method to request CloudMade token
  static String getCloudMadeToken(String apiKey, String userId) {

      HttpConnection httpConn = null;
      String url = "http://auth.cloudmade.com/token/" + apiKey;
      InputStream is = null;
      OutputStream os = null;
      StringBuffer sb = new StringBuffer();
      String outData="apikey="+"&userid="+userId;
      
      try {
          httpConn = (HttpConnection) Connector.open(url);
          httpConn.setRequestMethod(HttpConnection.POST);

          httpConn.setRequestProperty("User-Agent",
                  "Profile/MIDP-1.0 Confirguration/CLDC-1.0");
          httpConn.setRequestProperty("Accept_Language", "en-US");
          httpConn.setRequestProperty("Content-Type",
                  "application/x-www-form-urlencoded");
          httpConn.setRequestProperty("Content-Length", String.valueOf(outData.length()));

          os = httpConn.openOutputStream();
          
          os.write(outData.getBytes());
          os.flush();
          // Read Response from the Server

          is = httpConn.openDataInputStream();
          int chr;
          while ((chr = is.read()) != -1){
              sb.append((char) chr);
          }

      } catch (IOException e) {
          Log.error("Cannot read CloudMade token "+e.toString());
          
      } finally {
          try {
              if (is != null){
                  is.close();
                  }
//              if (os != null){
//                  os.close();
//                  }
              if (httpConn != null){
                  httpConn.close();
                  }
          } catch (IOException e) {
          }
      }
      return sb.toString();

  }

}
