package com.motorola.io.file;

import java.util.Enumeration;

import javax.microedition.io.Connection;
import javax.microedition.io.StreamConnection;

public interface FileConnection extends StreamConnection, Connection {

  int fileSize();

  Enumeration list(String string, boolean b);
}
