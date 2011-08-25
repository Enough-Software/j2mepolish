package javax.microedition.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.nutiteq.android.http.AndroidHttpConnection;

//TODO jaanus : actually android connection opener should be added
public class Connector {
  public static final int READ = 1;
  public static final int READ_WRITE = 3;
  public static final int WRITE = 2;

  private static final String PROTOCOL_HTTP = "http:";

  private Connector() {

  }

  public static Connection open(final String name) throws IOException {
    return open(name, READ_WRITE);
  }

  public static Connection open(final String name, final int mode) throws IOException {
    return open(name, mode, false);
  }

  public static Connection open(final String name, final int mode, final boolean timeouts)
      throws IOException {
    if (name.startsWith(PROTOCOL_HTTP)) {
      return new AndroidHttpConnection(name, mode, timeouts);
    }
    return null;
  }

  public static DataInputStream openDataInputStream(final String name) throws IOException {
    return null;
  }

  public static DataOutputStream openDataOutputStream(final String name) throws IOException {
    return null;
  }

  public static InputStream openInputStream(final String name) throws IOException {
    return null;
  }

  public static OutputStream openOutputStream(final String name) throws IOException {
    return null;
  }
}
