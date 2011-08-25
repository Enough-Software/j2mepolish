package com.motorola.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.microedition.io.Connection;
import javax.microedition.io.StreamConnection;

public interface FileConnection extends StreamConnection, Connection {

  InputStream openInputStream() throws IOException;

  DataInputStream openDataInputStream() throws IOException;

  OutputStream openOutputStream() throws IOException;

  DataOutputStream openDataOutputStream() throws IOException;

  long totalSize();

  long availableSize();

  long usedSize();

  long directorySize(boolean flag) throws IOException;

  long fileSize() throws IOException;

  boolean canRead() throws IOException;

  boolean canWrite() throws IOException;

  boolean isHidden() throws IOException;

  void setReadable(boolean flag) throws IOException;

  void setWriteable(boolean flag) throws IOException;

  void setHidden(boolean flag) throws IOException;

  String[] list() throws IOException;

  boolean create();

  boolean mkdir();

  boolean exists();

  boolean isDirectory();

  boolean delete();

  boolean rename(String s);

  String getPath();

  String getURL();

  long lastModified();

  void close() throws IOException;

  Enumeration list(String string, boolean b);
}