package javax.microedition.io.file;

import javax.microedition.io.Connection;
import javax.microedition.io.StreamConnection;

//TODO jaanus : this should be obsolete! here only for compilation?
public interface FileConnection extends StreamConnection, Connection {
  boolean isOpen();

  java.io.OutputStream openOutputStream(long byteOffset) throws java.io.IOException;

  long totalSize();

  long availableSize();

  long usedSize();

  long directorySize(boolean includeSubDirs) throws java.io.IOException;

  long fileSize() throws java.io.IOException;

  boolean canRead();

  boolean canWrite();

  boolean isHidden();

  void setReadable(boolean readable) throws java.io.IOException;

  void setWritable(boolean writable) throws java.io.IOException;

  void setHidden(boolean hidden) throws java.io.IOException;

  java.util.Enumeration list() throws java.io.IOException;

  java.util.Enumeration list(java.lang.String filter, boolean includeHidden)
      throws java.io.IOException;

  void create() throws java.io.IOException;

  void mkdir() throws java.io.IOException;

  boolean exists();

  boolean isDirectory();

  void delete() throws java.io.IOException;

  void rename(java.lang.String newName) throws java.io.IOException;

  void truncate(long byteOffset) throws java.io.IOException;

  void setFileConnection(java.lang.String fileName) throws java.io.IOException;

  java.lang.String getName();

  java.lang.String getPath();

  java.lang.String getURL();

  long lastModified();
}
