package com.nutiteq.polish.screens;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

import com.nutiteq.components.OnMapElement;
import com.nutiteq.components.Place;
import com.nutiteq.components.ZoomRange;
import com.nutiteq.listeners.OnMapElementListener;
import com.nutiteq.listeners.PlaceListener;
import com.nutiteq.polish.J2MEPolishSample;

import de.enough.polish.util.Locale;

public class MapScreen extends Form implements CommandListener, OnMapElementListener {
  public static MapScreen instance;
  public MapItem mapDisplay;
  public Place currentPlace;

  public MapScreen(final MapItem mapDisplay) {
    //#style mapScreen
    super(Locale.get("TITLE"));
    this.mapDisplay = mapDisplay;
    mapDisplay.setOnMapListener(this);
    mapDisplay.resize(getWidth() - 5, getHeight() - (getHeight() / 4));
    instance = this;

    this.append(mapDisplay);
    addCommand(J2MEPolishSample.cmdToMain);
    addCommand(J2MEPolishSample.cmdDetails);

    final ZoomRange zoomRange = mapDisplay.getZoomRange();
    System.out.println("Available zoom range for displayed map: minZoom = "
        + zoomRange.getMinZoom() + " maxZoom = " + zoomRange.getMaxZoom());

    setCommandListener(this);
  }

  public void finishMapping() {
    mapDisplay.stopMapping();
  }

  public void commandAction(final Command cmd, final Displayable disp) {
    if (cmd == J2MEPolishSample.cmdToMain) {
      Display.getDisplay(J2MEPolishSample.instance).setCurrent(
          J2MEPolishSample.instance.getMainMenuScreen());
    } else if (cmd == J2MEPolishSample.cmdDetails) {
      mapDisplay.keyPressed(-5);
    }
  }
	 /*
	  * (non-Javadoc)
	  * @see com.nutiteq.listeners.OnMapElementListener#elementClicked(com.nutiteq.components.OnMapElement)
	  */
	public void elementClicked(OnMapElement element) {
	    J2MEPolishSample.instance.openPlaceDetailsForm(element, this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.nutiteq.listeners.OnMapElementListener#elementEntered(com.nutiteq.components.OnMapElement)
	 */
	public void elementEntered(OnMapElement element) {
		// ignore
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.nutiteq.listeners.OnMapElementListener#elementLeft(com.nutiteq.components.OnMapElement)
	 */
	public void elementLeft(OnMapElement element) {
		// ignore
	}
}
