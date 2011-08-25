package com.nutiteq.location.providers;

import java.io.IOException;

/**
 * Interface for object that know how to open connections to location sources.
 */
public interface LocationDataConnectionProvider {
  /**
   * Open connection with given url
   * 
   * @param url
   *          url for connection
   * @return return opened connection
   * @throws IOException
   *           if something went wrong
   */
  LocationDataConnection openConnection(final String url) throws IOException;
}
