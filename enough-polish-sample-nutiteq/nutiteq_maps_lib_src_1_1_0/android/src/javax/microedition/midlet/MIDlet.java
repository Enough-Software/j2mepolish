package javax.microedition.midlet;

public class MIDlet {
  public final String getAppProperty(final String key) {
    throw new RuntimeException("Never call me again!");
  }

  public final void notifyDestroyed() {
    throw new RuntimeException("Never call me again!");
  }
}
