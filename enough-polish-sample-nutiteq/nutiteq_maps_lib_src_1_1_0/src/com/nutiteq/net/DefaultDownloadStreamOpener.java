package com.nutiteq.net;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import com.nutiteq.log.Log;
import com.nutiteq.utils.IOUtils;

/**
 * Default stream opener used inside library. Handles cleanup for resources
 * opened by it.
 * 
 * Status codes 200 (OK) and 304 (not modified) are handled the same way - with
 * data read.
 * 
 * This implementation tries to follow up to 3 redirects (HTTP status codes 301,
 * 302, 307). If it is not successful, then an error notification will be sent
 * to stream waiter.
 */
public class DefaultDownloadStreamOpener implements DownloadStreamOpener {
  /**
   * Default timeout for network activity.
   */
  public static final int DEFAULT_TIMEOUT = 20 * 1000;
  private static final String HTTP_REDIRECT_LOCATION_HEADER = "Location";
  private final String urlExtension;
  private final Hashtable properties = new Hashtable();
  private static final int MAX_FOLLOWED_REDIRECTS = 3;
  private final long timeout;

  /**
   * @param urlExtension
   *          Optional extension for http URLs, for instance ";deviceside=true".
   *          Used on some Blackberry devices.
   */
  public DefaultDownloadStreamOpener(final String urlExtension) {
    this(urlExtension, DEFAULT_TIMEOUT);
  }

  public DefaultDownloadStreamOpener() {
    this("", DEFAULT_TIMEOUT);
  }

  public DefaultDownloadStreamOpener(final long timeout) {
    this("", timeout);
  }

  public DefaultDownloadStreamOpener(final String urlExtension, final long timeout) {
    this.urlExtension = urlExtension;
    this.timeout = timeout;
  }

  /**
   * Add request properties, that will be added added to every request (for
   * example User-Agent).
   * 
   * @param propertyName
   *          request property name
   * @param propertyValue
   *          request property value
   */
  public void addRequestProperty(final String propertyName, final String propertyValue) {
    properties.put(propertyName, propertyValue);
  }

  public void openInputStream(final DownloadStreamWaiter streamWaiter, final String url) {
    openInputStream(streamWaiter, url, 0, null);
  }

  public void openInputStream(final DownloadStreamWaiter streamWaiter,
      final DataPostingDownloadable postingDownloadable) {
    openInputStream(streamWaiter, postingDownloadable.getUrl(), 0, postingDownloadable);
  }

  private void openInputStream(final DownloadStreamWaiter streamWaiter, final String url,
      final int redirects, final DataPostingDownloadable downloadable) {
    final String downloadableUrl = url + urlExtension;
    final DataInputStream dis = null;
    HttpConnection connection = null;
    InputStream is = null;
    String redirectUrl = null;
    try {
      final long startTime = System.currentTimeMillis();
      Log.info("Downloading " + downloadableUrl);
      connection = (HttpConnection) Connector.open(downloadableUrl, Connector.READ_WRITE, true);
      connection.setRequestMethod(downloadable == null ? HttpConnection.GET : downloadable.getRequestMethod());
      connection.setRequestProperty("Cache-Control", "No-Transform");
      if (properties.size() > 0) {
        final Enumeration keysEnum = properties.keys();
        while (keysEnum.hasMoreElements()) {
          final String key = (String) keysEnum.nextElement();
          final String value = (String) properties.get(key);
          connection.setRequestProperty(key, value);
        }
      }

      if (downloadable != null) {
        final byte[] dataBytes = downloadable.getPostContent().getBytes("iso-8859-1");
        connection.setRequestProperty("Content-Length", Integer.toString(dataBytes.length));
        connection.setRequestProperty("content-type", downloadable.getContentType());
        final OutputStream dos = connection.openOutputStream();
        dos.write(dataBytes);
      }

      is = connection.openInputStream();
      final int responseCode = connection.getResponseCode();
      Log.debug("Connection opened in " + (System.currentTimeMillis() - startTime));
      if (responseCode == HttpConnection.HTTP_OK
          || responseCode == HttpConnection.HTTP_NOT_MODIFIED) {
        final long processStart = System.currentTimeMillis();
        streamWaiter.streamOpened(is);
        Log.debug("Response read in " + (System.currentTimeMillis() - processStart));
      } else if (responseCode == HttpConnection.HTTP_TEMP_REDIRECT
          || responseCode == HttpConnection.HTTP_MOVED_PERM
          || responseCode == HttpConnection.HTTP_MOVED_TEMP) {
        redirectUrl = connection.getHeaderField(HTTP_REDIRECT_LOCATION_HEADER);
        Log.debug("Redirect to " + redirectUrl);
      } else {
        streamWaiter.error(RESPONCE_NOT_OK, "");
      }
    } catch (final IOException e) {
      Log.error("Downloader: " + e.getMessage());
      streamWaiter.error(NETWORK_ERROR, e.getMessage());
    } catch (final SecurityException e) {
      Log.error("Downloader security: " + e.getMessage());
      streamWaiter.error(SECURITY_EXCEPTION, e.getMessage());
    } finally {
      IOUtils.closeStream(dis);
      IOUtils.closeStream(is);
      IOUtils.closeConnection(connection);
    }

    if (redirects == MAX_FOLLOWED_REDIRECTS && redirectUrl != null) {
      streamWaiter.error(TOO_MANY_REDIRECTS, "Too manu redirects created!");
      return;
    }

    if (redirectUrl != null) {
      openInputStream(streamWaiter, redirectUrl, redirects + 1, downloadable);
    }
  }

  public long getTimeout() {
    return timeout;
  }
}
