package com.nutiteq.helpers;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;

import com.nutiteq.core.MappingCore;
import com.nutiteq.fs.FileSystem;
import com.nutiteq.log.Log;
import com.nutiteq.task.LocalTask;

/**
 * This class implements a file browser using J2ME MIDP UI.
 * 
 * @author CristiS
 * @version 1.0
 */
public class FileBrowser implements CommandListener {
  private final FileSystem fs;
  private final List list;
  private final Command cmdSelect;
  private final Command cmdCancel;

  private String currentDir;
  private String oldCurrentDir;
  private FileBrowserListener lsn;
  private boolean found;

  /**
   * Create a FileBrowser object.
   * 
   * @param fs
   *          FileSystem implementation to use
   */
  public FileBrowser(final FileSystem fs) {
    this.fs = fs;
    cmdSelect = new Command("Select", Command.SCREEN, 0);
    cmdCancel = new Command("Cancel", Command.CANCEL, 0);
    list = new List("", List.IMPLICIT);
    list.setSelectCommand(cmdSelect);
    list.addCommand(cmdCancel);
    list.setCommandListener(this);
  }

  /**
   * Show file browser UI (list).
   * 
   * @param midlet
   * 
   * @param startDir
   *          directory to start browsing. Use null to start with the list of
   *          roots.
   * @param listener
   *          FileBrowserListener object used for accepting a file or directory.
   *          The FileBrowserListener implementation should remember the file or
   *          directory selected and should change the displayable before
   *          returning true in one of its methods.
   */
  public void showUI(final MIDlet midlet, final String startDir, final FileBrowserListener listener) {
    MappingCore.getInstance().runAsync(new LocalTask() {
      public void execute() {
        pushUI(midlet, startDir, listener);
      }
    });
  }

  private void pushUI(final MIDlet midlet, final String startDir, final FileBrowserListener listener) {
    currentDir = startDir;
    lsn = listener;
    found = false;

    try {
      populateList();
      if (found) {
        return;
      }
    } catch (final IOException ex) {
      Log.error("Error reading filesystem");
      currentDir = oldCurrentDir;
    }

    oldCurrentDir = currentDir;

    list.setTitle("/" + (currentDir == null ? "" : currentDir));
    Display.getDisplay(midlet).setCurrent(list);
  }

  private void populateList() throws IOException {
    final Vector files = fs.listFiles(currentDir);
    list.deleteAll();

    if (lsn.directoryListed(currentDir, files)) {
      found = true;
      return;
    }

    final int num = files.size();
    for (int i = 0; i < num; i++) {
      final String fn = (String) files.elementAt(i);
      list.append(fn, null);
    }

    if (list.size() > 0) {
      list.setSelectedIndex(0, true);
    }
  }

  /**
   * Called when a command is selected.
   * 
   * @param cmd
   *          command
   * @param disp
   *          current displayable
   * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command,
   *      javax.microedition.lcdui.Displayable)
   */
  public void commandAction(final Command cmd, final Displayable disp) {
    if (cmd == cmdCancel) {
      lsn.browsingCancelled();
    } else {
      MappingCore.getInstance().runAsync(new LocalTask() {
        public void execute() {
          // check what we have selected
          final int index = list.getSelectedIndex();
          final String filename = list.getString(index);
          // error
          if (filename == null) {
            return;
          }

          String checkFilename = filename;
          boolean isDir = currentDir == null;
          if (!isDir) {
            try {
              isDir = fs.isDirectory(currentDir + filename);
            } catch (final IOException ie) {
            }
          }
          if (filename.endsWith("/")) {
            checkFilename = filename.substring(0, filename.length() - 1);
          }

          if (lsn.fileSelected(currentDir, filename)) {
            return;
          }

          // change directory
          if (isDir) {
            // parent dir
            if (checkFilename.equals("..")) {
              final int len = currentDir.length();
              int lastpos;
              // get the 'but-last' slash
              for (lastpos = len - 2; lastpos >= 0; lastpos--) {
                if (currentDir.charAt(lastpos) == '/') {
                  break;
                }
              }
              if (lastpos < 0) {
                currentDir = null;
              } else {
                currentDir = currentDir.substring(0, lastpos + 1);
              }
            } else if (currentDir == null || currentDir.equals("/")) {
              currentDir = filename;
            } else {
              currentDir += filename;
            }

            // trim slashes
            if (currentDir != null) {
              while (currentDir.startsWith("/")) {
                currentDir = currentDir.substring(1);
              }
              while (currentDir.endsWith("//")) {
                currentDir = currentDir.substring(0, currentDir.length() - 1);
              }
            }

            try {
              populateList();
            } catch (final IOException ex) {
              Log.error("Error reading filesystem");
            }

            oldCurrentDir = currentDir;
            list.setTitle("/" + (currentDir == null ? "" : currentDir));
          } // end isDir
        }
      });
    } // end cmdSelect
  } // end commandAction
}
