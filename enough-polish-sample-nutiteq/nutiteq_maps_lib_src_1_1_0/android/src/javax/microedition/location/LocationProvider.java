package javax.microedition.location;

public abstract class LocationProvider {
  public static final int AVAILABLE = 1;
  public static final int OUT_OF_SERVICE = 3;
  public static final int TEMPORARILY_UNAVAILABLE = 2;

  public static LocationProvider getInstance(final Criteria criteria) throws LocationException {
    return null;
  }

  public abstract Location getLocation(final int timeout) throws LocationException,
      InterruptedException;

  public abstract int getState();

  public abstract void setLocationListener(LocationListener listener, int interval, int timeout,
      int maxAge);
}
