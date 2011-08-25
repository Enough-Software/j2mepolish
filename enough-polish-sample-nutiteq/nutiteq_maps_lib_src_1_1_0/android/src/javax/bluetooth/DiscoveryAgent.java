package javax.bluetooth;

public class DiscoveryAgent {
  public static final int CACHED = 0;
  public static final int GIAC = 10390323;
  public static final int LIAC = 10390272;
  public static final int NOT_DISCOVERABLE = 0;
  public static final int PREKNOWN = 1;

  public boolean cancelInquiry(final DiscoveryListener listener) {
    return true;
  }

  public boolean cancelServiceSearch(final int transID) {
    return true;
  }

  public int searchServices(final int[] attrSet, final UUID[] uuidSet, final RemoteDevice btDev,
      final DiscoveryListener discListener) throws BluetoothStateException {
    return 0;
  }

  public boolean startInquiry(final int accessCode, final DiscoveryListener listener)
      throws BluetoothStateException {
    return false;
  }
}
