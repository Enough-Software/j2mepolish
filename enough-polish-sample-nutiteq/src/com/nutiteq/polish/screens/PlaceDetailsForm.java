package com.nutiteq.polish.screens;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;

import com.nutiteq.polish.J2MEPolishSample;

import de.enough.polish.util.Locale;

public class PlaceDetailsForm extends Form implements CommandListener {

  private StringItem address;
  private StringItem details;

  private static final Command cmdBackTo = new Command(Locale.get("BACK"), Command.BACK, 1);

  private Displayable backDisplay;

  public PlaceDetailsForm() {
    //#style placeForm
    super("Details");
    this.addCommand(cmdBackTo);
    this.setCommandListener(this);
  }

  /**
   * 
   * @param p
   *          place which details is set on Form
   * @param playNow
   *          whether to start playing the audio automatically
   * @param current
   *          the Displayable that is current, when the setDetails is called, so
   *          back button could refer user back to that screen
   */
  public void setDetails(final String name, final String address, final String description,
      final Displayable current) {
    if (current != null) {
      backDisplay = current;
    }
    this.deleteAll();
    //#style placeDetailItem
    this.append(name);
    //#style placeDetailItem
    this.append(address);
    //#style placeDetailItem
    this.append(description);
  }

  public void commandAction(final Command cmd, final Displayable disp) {
    System.out.println("CommandAction");
    if (cmd == cmdBackTo) {
      J2MEPolishSample.instance.getDisplay().setCurrent(backDisplay);
    }

  }

}
