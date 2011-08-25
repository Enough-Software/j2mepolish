package javax.microedition.lcdui;

public abstract class Displayable {
  public void addCommand(final Command cmd) {

  }

  public void setCommandListener(final CommandListener l) {

  }

  public void setTitle(final String s) {

  }

  public int getHeight() {
    throw new RuntimeException();
  }

  public int getWidth() {
    throw new RuntimeException();
  }

  public String getTitle() {
    throw new UnsupportedOperationException();
  }

  public Ticker getTicker() {
    return null;
  }

  public void setTicker(final Ticker ticker) {

  }

  public boolean isShown() {
    return false;
  }

  public void removeCommand(final Command cmd) {

  }

  protected void sizeChanged(final int w, final int h) {

  }
}
