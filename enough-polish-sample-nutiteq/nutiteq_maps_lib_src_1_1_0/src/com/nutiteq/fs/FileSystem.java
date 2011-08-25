/*
 * Created on Aug 20, 2008
 */
package com.nutiteq.fs;

import java.io.IOException;
import java.util.Vector;


/**
 * Abstract class for support of various file system access implementations.
 */
public interface FileSystem {
  /**
   * Read file from disk/memory card/flash.
   * 
   * @param filename
   *          fully-qualified file path (following "file:///" qualifier)
   * @return file data
   * @throws IOException
   *           if an exception occurs
   */
  byte[] readFile(String filename) throws IOException;

  FileSystemConnection openConnectionToFile(String fileName) throws IOException;

  /**
   * Check if a file is a directory
   * 
   * @param filename
   *          file to check
   * @return true if it is a directory
   */
  boolean isDirectory(String filename) throws IOException;

  /**
   * Get file system roots.
   * 
   * @return an array including all the roots in the filesystem
   */
  Vector getRoots() throws IOException;

  /**
   * List all files in a directory.
   * 
   * @param path
   *          path to list, null to list root
   * @return a vector of file names
   */
  Vector listFiles(final String path) throws IOException;
}
