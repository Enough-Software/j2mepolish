package com.nutiteq.fs;

import java.io.IOException;
import java.io.InputStream;

public interface FileSystemConnection {

  InputStream openInputStream() throws IOException;

  void close();

}
