package com.nutiteq.fs;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.InputConnection;

import com.nutiteq.utils.IOUtils;

public class MidpFileSystemConnection implements FileSystemConnection {
  private final InputConnection inputConnection;
  private InputStream is;

  public MidpFileSystemConnection(final InputConnection connection) {
    this.inputConnection = connection;
  }

  public InputStream openInputStream() throws IOException {
    is = inputConnection.openInputStream();
    return is;
  }

  public void close() {
    IOUtils.closeStream(is);
    IOUtils.closeConnection(inputConnection);
  }

  public int fileSize() {
    return 0;
  }
}
