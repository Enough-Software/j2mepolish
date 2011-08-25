package com.nutiteq.net;

/**
 * Interface for download objects that also need to send data to server. Data
 * will be sent in request body.
 */
public interface DataPostingDownloadable extends Downloadable {
  /**
   * Get content that should be sent to server
   * 
   * @return content string
   */
  String getPostContent();

  /**
   * Content type for sent data
   * 
   * @return content type
   */
  String getContentType();
  
  /**
   * Set the method for the URL request. See {@link javax.microedition.io.HttpConnection} for valid methods.
   * 
   * @return request method
   */
  String getRequestMethod();
}
