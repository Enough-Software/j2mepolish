package javax.microedition.lcdui;

public class Command {
  public static final int BACK = 1;
  public static final int CANCEL = 2;
  public static final int EXIT = 3;
  public static final int HELP = 4;
  public static final int ITEM = 5;
  public static final int OK = 6;
  public static final int SCREEN = 7;
  public static final int STOP = 8;
  
  private Command() {}
  
  public Command(final String label, final int commandType, final int priority) {

  }
}
