package com.nutiteq.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

import javax.microedition.io.Connection;

import com.nutiteq.log.Log;

public class IOUtils {
  public static final int BUFSIZE = 4096;

  private IOUtils() {
  }

  public static void closeStream(final InputStream is) {
    if (is != null) {
      try {
        is.close();
      } catch (final IOException ignore) {
      }
    }
  }

  public static void closeStream(final OutputStream os) {
    if (os != null) {
      try {
        os.close();
      } catch (final IOException ignore) {
      }
    }
  }

  public static void closeReader(final Reader reader) {
    if (reader != null) {
      try {
        reader.close();
      } catch (final IOException ignore) {
      }
    }
  }

  public static void closeConnection(final Connection conn) {
    if (conn != null) {
      try {
        conn.close();
      } catch (final IOException ignore) {
      }
    }
  }

  public static byte[] readFullyAndClose(final InputStream is) {
    final byte[] result = readFully(is);
    closeStream(is);
    return result;
  }

  public static byte[] readFully(final InputStream is) {
    ByteArrayOutputStream out = null;
    final byte[] buffer = new byte[1024];
    byte[] result;
    try {
      out = new ByteArrayOutputStream();
      int read;
      while ((read = is.read(buffer)) != -1) {
        out.write(buffer, 0, read);
      }
      out.flush();
      result = out.toByteArray();
    } catch (final IOException e) {
      Log.printStackTrace(e);
      result = new byte[0];
    } finally {
      closeStream(out);
    }
    return result;
  }

  public static int skip(final InputStream is, final int n) throws IOException {
    int rd = 0;
    long ch = 0;
    while (rd < n && ch >= 0) {
      final long cn = (n - rd > BUFSIZE) ? BUFSIZE : (n - rd);
      ch = is.skip(cn);

      if (ch > 0) {
        rd += ch;
      }
    }
    return rd;
  }
}
