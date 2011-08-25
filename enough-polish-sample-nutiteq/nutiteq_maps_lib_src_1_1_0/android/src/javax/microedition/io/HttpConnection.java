package javax.microedition.io;

public interface HttpConnection extends Connection, ContentConnection, InputConnection,
    OutputConnection, StreamConnection {
  String GET = "GET";
  String POST = "POST";
  int HTTP_MOVED_PERM = 301;
  int HTTP_MOVED_TEMP = 302;
  int HTTP_NOT_MODIFIED = 304;
  int HTTP_OK = 200;
  int HTTP_TEMP_REDIRECT = 307;

  String getHeaderField(String name) throws java.io.IOException;

  int getResponseCode() throws java.io.IOException;

  void setRequestMethod(String method) throws java.io.IOException;

  void setRequestProperty(String key, String value) throws java.io.IOException;
}
