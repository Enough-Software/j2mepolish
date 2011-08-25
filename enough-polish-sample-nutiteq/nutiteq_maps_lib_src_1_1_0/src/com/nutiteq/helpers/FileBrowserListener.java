package com.nutiteq.helpers;

import java.util.Vector;

/**
 * This interface defines a listener that gets called by the file browser.
 *
 * @author CristiS
 * @version 1.0
 */
public interface FileBrowserListener {
  /**
   * Called when a file is clicked.
   * @param currentDir current directory
   * @param filename file (or directory) clicked
   * @return true to stop browsing (and return the complete path of filename), false to continue
   */
  boolean fileSelected(final String currentDir, final String filename);
  
  /**
   * Called when a directory is listed.
   * @param currentDir current directory
   * @param files list of files in the directory
   * @return true to stop browsing (and return currentDir), false to continue
   */
  boolean directoryListed(final String currentDir, final Vector files);
  
  /**
   * Inform that the browsing was cancelled.
   */
  void browsingCancelled();
}
