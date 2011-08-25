/*
 * Created on Aug 20, 2008
 */
package com.nutiteq.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import com.nutiteq.log.Log;
import com.nutiteq.utils.IOUtils;

/**
 * This class includes implementation for reading files using Android FileSystem
 * (actually Java IO).
 * 
 * @author JaakL
 */
public class AndroidFileSystem implements FileSystem {

  /**
   * Read a file using Java IO API.
   * 
   * @param filename
   *          fully-qualified file path following "file:///" qualifier
   * @return file data
   * @throws IOException
   *           if an exception occurs
   */
  public byte[] readFile(final String filename) throws IOException {
    Log.debug("Loading file:///" + filename);

    FileInputStream fis = null;

    fis = new FileInputStream("/" + filename);
    return IOUtils.readFullyAndClose(fis);
  }

  /**
   * List all roots in the filesystem
   * 
   * @return a vector containing all the roots
   * @see com.nutiteq.fs.FileSystem#getRoots()
   */
  public Vector getRoots() {
    //    TODO: to be implemented. Usually not needed
    return null;
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

    final File file = new File(path);

    final String[] files = file.list();

    return new Vector(Arrays.asList(files));
  }

  /**
   * Check if a file is a directory
   * 
   * @param filename
   *          file to check
   * @return true if it is a directory
   */
  public boolean isDirectory(final String filename) {
    return new File(filename).isDirectory();
  }

  public FileSystemConnection openConnectionToFile(final String fileName) throws IOException {
    return new AndroidFileSystemConnection(new FileInputStream("/" + fileName));
  }
}
