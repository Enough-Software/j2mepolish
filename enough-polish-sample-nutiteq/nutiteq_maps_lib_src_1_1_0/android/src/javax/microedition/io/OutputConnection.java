package javax.microedition.io;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public interface OutputConnection {
  OutputStream openOutputStream() throws IOException;

  DataOutputStream openDataOutputStream() throws IOException;
}
