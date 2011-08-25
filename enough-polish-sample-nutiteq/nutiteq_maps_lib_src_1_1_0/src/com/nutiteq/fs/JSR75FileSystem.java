//#condition polish.api.filesystem
/*
 * Created on Aug 20, 2008
 */
package com.nutiteq.fs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;

import com.nutiteq.log.Log;
import com.nutiteq.utils.IOUtils;

/**
 * This class includes implementation for reading files using JSR-75.
 * 
 * @author CristiS
 */
public class JSR75FileSystem implements FileSystem {

  /**
   * Read a file using JSR-75 API.
   * 
   * @param filename
   *          fully-qualified file path following "file:///" qualifier
   * @return file data
   * @throws IOException
   *           if an exception occurs
   */
  public byte[] readFile(final String filename) throws IOException {
    Log.debug("Loading file:///" + filename);

    FileConnection fconn = null;
    InputStream is = null;
    try {
      fconn = (FileConnection) Connector.open("file:///" + filename, Connector.READ);
      // commented to speed up
      // if (!fconn.exists() || !fconn.canRead())
      //   throw new Exception("File does not exist");

      final int sz = (int) fconn.fileSize();
      final byte[] result = new byte[sz];

      is = fconn.openInputStream();

      // multiple bytes
      int ch = 0;
      int rd = 0;
      while ((rd != sz) && (ch != -1)) {
        ch = is.read(result, rd, sz - rd);
        if (ch > 0) {
          rd += ch;
        }
      }

      return result;
    } finally {
      IOUtils.closeStream(is);
      IOUtils.closeConnection(fconn);
    }
  }

  /**
   * List all roots in the filesystem
   * 
   * @return a vector containing all the roots
   * @see com.nutiteq.fs.FileSystem#getRoots()
   */
  public Vector getRoots() {
    final Vector v = new Vector();

    // list roots
    final Enumeration en = FileSystemRegistry.listRoots();

    // enumerate
    while (en.hasMoreElements()) {
      String root = (String) en.nextElement();
      if (!root.endsWith("/")) {
        root += '/';
      }
      v.addElement(root);
    }

    return v;
  }

  /**
   * List all files in a directory.
   * 
   * @param path
   *          path to list, null to list root
   * @return a vector of file names
   */
  public Vector listFiles(final String path) throws IOException {
    if (path == null || path.length() == 0) {
      return getRoots();
    }

    // open directory
    final Vector v = new Vector();
    FileConnection fconn = null;
    try {
      fconn = (FileConnection) Connector.open("file:///" + path, Connector.READ);
      v.addElement("../");
      final Enumeration en = fconn.list();
      while (en.hasMoreElements()) {
        String filename = (String) en.nextElement();

        // convert absolute to relative path
        int pos = filename.length() - 2;
        while (pos >= 0 && filename.charAt(pos) != '/') {
          pos--;
        }
        if (pos >= 0) {
          filename = filename.substring(pos + 1);
        }

        v.addElement(filename);
      }
    } finally {
      if (fconn != null) {
        fconn.close();
      }
    }

    return v;
  }

  /**
   * Check if a file is a directory
   * 
   * @param filename
   *          file to check
   * @return true if it is a directory
   */
  public boolean isDirectory(final String filename) {
    return filename.endsWith("/");
  }

  public FileSystemConnection openConnectionToFile(final String fileName) throws IOException {
    return new MidpFileSystemConnection((FileConnection) Connector.open("file:///" + fileName,
        Connector.READ));
  }
}
