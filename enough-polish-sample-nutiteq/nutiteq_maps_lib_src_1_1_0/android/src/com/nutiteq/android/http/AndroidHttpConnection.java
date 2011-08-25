package com.nutiteq.android.http;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import com.nutiteq.log.Log;

public class AndroidHttpConnection implements HttpConnection {
  private HttpURLConnection connection;
  private final String url;

  public AndroidHttpConnection(final String name, final int mode, final boolean timeouts) {
    url = name;
    try {
      final URL url = new URL(name);
      connection = (HttpURLConnection) url.openConnection();
      if (mode == Connector.WRITE || mode == Connector.READ_WRITE) {
        connection.setDoOutput(true);
      }

      if (mode == Connector.READ || mode == Connector.READ_WRITE) {
        connection.setDoInput(true);
      }

      if (mode == Connector.READ) {
        connection.setDoOutput(false);
      }
    } catch (final IOException e) {
      Log.printStackTrace(e);
    }
  }

  public String getURL() {
    return url;
  }

  public String getProtocol() {
    return connection.getURL().getProtocol();
  }

  public String getHost() {
    return connection.getURL().getHost();
  }

  public String getFile() {
    //TODO jaanus : check this
    final String file = connection.getURL().getFile();
    return file.indexOf("?") > 0 ? file.substring(0, file.indexOf("?")) : file;
  }

  public String getRef() {
    return connection.getURL().getRef();
  }

  public String getQuery() {
    return connection.getURL().getQuery();
  }

  public int getPort() {
    return connection.getURL().getPort();
  }

  public String getRequestMethod() {
    return connection.getRequestMethod();
  }

  public void setRequestMethod(final String method) throws IOException {
    connection.setRequestMethod(method);
  }

  public String getRequestProperty(final String key) {
    return connection.getRequestProperty(key);
  }

  public void setRequestProperty(final String key, final String value) throws IOException {
    connection.setRequestProperty(key, value);
  }

  public int getResponseCode() throws IOException {
    return connection.getResponseCode();
  }

  public String getResponseMessage() throws IOException {
    return connection.getResponseMessage();
  }

  public long getExpiration() throws IOException {
    return connection.getExpiration();
  }

  public long getDate() throws IOException {
    return connection.getDate();
  }

  public long getLastModified() throws IOException {
    return connection.getLastModified();
  }

  public String getHeaderField(final String name) throws IOException {
    return connection.getHeaderField(name);
  }

  public int getHeaderFieldInt(final String name, final int def) throws IOException {
    return connection.getHeaderFieldInt(name, def);
  }

  public long getHeaderFieldDate(final String name, final long def) throws IOException {
    return connection.getHeaderFieldDate(name, def);
  }

  public String getHeaderField(final int n) throws IOException {
    return connection.getHeaderField(n);
  }

  public String getHeaderFieldKey(final int n) throws IOException {
    return connection.getHeaderFieldKey(n);
  }

  public void close() throws IOException {
    //TODO jaanus : check this
    connection.disconnect();
  }

  public String getType() {
    return connection.getContentType();
  }

  public String getEncoding() {
    return connection.getContentEncoding();
  }

  public long getLength() {
    return connection.getContentLength();
  }

  public InputStream openInputStream() throws IOException {
    return connection.getInputStream();
  }

  public DataInputStream openDataInputStream() throws IOException {
    return new DataInputStream(openInputStream());
  }

  public OutputStream openOutputStream() throws IOException {
    return connection.getOutputStream();
  }

  public DataOutputStream openDataOutputStream() throws IOException {
    return new DataOutputStream(openOutputStream());
  }
}
