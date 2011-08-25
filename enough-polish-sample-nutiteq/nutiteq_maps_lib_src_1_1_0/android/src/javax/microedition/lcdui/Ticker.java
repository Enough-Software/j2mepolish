package javax.microedition.lcdui;

public class Ticker {
  private String str;

  public Ticker(final String str) {
    this.str = str;
  }

  public void setString(final String str) {
    this.str = str;
  }

  public String getString() {
    return str;
  }
}
