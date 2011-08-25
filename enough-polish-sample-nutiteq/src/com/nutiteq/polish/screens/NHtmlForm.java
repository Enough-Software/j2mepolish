package com.nutiteq.polish.screens;

import java.io.ByteArrayInputStream;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

import com.nutiteq.log.Log;
import com.nutiteq.polish.J2MEPolishSample;

import de.enough.polish.browser.html.HtmlBrowser;

public class NHtmlForm extends Form implements CommandListener {
  private final HtmlBrowser browser;
  private final Command back;

  public NHtmlForm(final String name) {
    super(name);
    back = new Command("Back", Command.BACK, 0);
    browser = new HtmlBrowser();
    //#style htmlBrowser
    append(browser);
    addCommand(back);
    setCommandListener(this);
  }

  public void setContent(final String description) {
    try {
        browser.loadPage(new ByteArrayInputStream(description.getBytes()));
    } catch (final Exception e) {
      Log.error(e.getMessage());
    }
  }

  public void commandAction(final Command cmd, final Displayable d) {
    if (cmd == back) {
      J2MEPolishSample.instance.show(J2MEPolishSample.instance.mapScreen);
    }
  }
}
