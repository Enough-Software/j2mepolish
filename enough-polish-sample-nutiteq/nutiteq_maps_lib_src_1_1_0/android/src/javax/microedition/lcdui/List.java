package javax.microedition.lcdui;

public class List extends Screen {
  public static final int EXCLUSIVE = 0;
  public static final int IMPLICIT = 1;
  public static final int MULTIPLE = 2;
  public static final int POPUP = 3;

  public List(final String title, final int listType) {

  }

  public int append(final String stringPart, final Image imagePart) {
    return 0;
  }

  public void deleteAll() {

  }

  public int getSelectedIndex() {
    return 0;
  }

  public String getString(final int elementNum) {
    return null;
  }

  public void setSelectCommand(final Command command) {

  }

  public void setSelectedIndex(final int elementNum, final boolean selected) {

  }

  public int size() {
    return 0;
  }
}
