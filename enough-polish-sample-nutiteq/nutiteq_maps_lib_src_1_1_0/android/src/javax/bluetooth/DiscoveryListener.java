package javax.bluetooth;

public interface DiscoveryListener {
  int INQUIRY_COMPLETED = 0;
  int INQUIRY_ERROR = 7;
  int INQUIRY_TERMINATED = 5;
  int SERVICE_SEARCH_COMPLETED = 1;
  int SERVICE_SEARCH_DEVICE_NOT_REACHABLE = 6;
  int SERVICE_SEARCH_ERROR = 3;
  int SERVICE_SEARCH_NO_RECORDS = 4;
  int SERVICE_SEARCH_TERMINATED = 2;

  void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod);

  void servicesDiscovered(int transID, ServiceRecord[] servRecord);

  void serviceSearchCompleted(int transID, int respCode);

  void inquiryCompleted(int discType);
}
