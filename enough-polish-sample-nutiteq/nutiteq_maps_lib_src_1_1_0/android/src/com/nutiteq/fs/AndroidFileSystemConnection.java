package com.nutiteq.fs;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.nutiteq.utils.IOUtils;

public class AndroidFileSystemConnection implements FileSystemConnection {
  private final FileInputStream inputStream;

  public AndroidFileSystemConnection(final FileInputStream is) {
    this.inputStream = is;
  }

  public InputStream openInputStream() throws IOException {
    return inputStream;
  }

  public void close() {
    IOUtils.closeStream(inputStream);
  }
}
