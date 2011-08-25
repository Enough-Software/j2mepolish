package javax.bluetooth;

public interface ServiceRecord {
  int AUTHENTICATE_ENCRYPT = 2;
  int AUTHENTICATE_NOENCRYPT = 1;
  int NOAUTHENTICATE_NOENCRYPT = 0;

  String getConnectionURL(int requiredSecurity, boolean mustBeMaster);
}
