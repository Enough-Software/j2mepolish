//#condition polish.api.motorola
/*
 * Created on Sep 14, 2008
 */
package com.nutiteq.fs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.microedition.io.Connector;

import com.motorola.io.FileConnection;
import com.motorola.io.FileSystemRegistry;
import com.nutiteq.log.Log;
import com.nutiteq.utils.IOUtils;

/**
 * File reading using Motorola (not IDEN) file API.
 * 
 * @author CristiS
 */
public class MotoFileSystem implements FileSystem {

  /**
   * List all roots in the filesystem
   * 
   * @return a vector containing all the roots
   * @see com.nutiteq.fs.FileSystem#getRoots()
   */
  public Vector getRoots() throws IOException {
    final Vector v = new Vector();

    // list roots
    final String[] roots = FileSystemRegistry.listRoots();

    // enumerate
    for (int i = 0; i < roots.length; i++) {
      String root = roots[i];
      if (!root.endsWith("/")) {
        root += '/';
      }
      v.addElement(root);
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
      final String[] lst = fconn.list();
      for (int i = 0; i < lst.length; i++) {
        String filename = lst[i];

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

  public FileSystemConnection openConnectionToFile(final String fileName) throws IOException {
    return new MidpFileSystemConnection((FileConnection) Connector.open("file:///" + fileName,
        Connector.READ));
  }
}
