package com.nutiteq.polish;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import com.nutiteq.components.OnMapElement;
import com.nutiteq.components.Place;
import com.nutiteq.components.PlaceInfo;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.controls.ControlKeys;
import com.nutiteq.kml.KmlUrlReader;
import com.nutiteq.location.LocationSource;
import com.nutiteq.location.LocationSourceWaiter;
import com.nutiteq.maps.NutiteqStreamedMap;
import com.nutiteq.polish.screens.MapItem;
import com.nutiteq.polish.screens.MapScreen;
import com.nutiteq.polish.screens.NHtmlForm;
import com.nutiteq.polish.screens.PlaceDetailsForm;
import com.nutiteq.polish.screens.PlaceTreeForm;
import com.nutiteq.utils.Utils;

import de.enough.polish.util.Locale;

public class J2MEPolishSample extends MIDlet implements CommandListener, LocationSourceWaiter {
  public static final KmlUrlReader PANORAMIO = new KmlUrlReader(
      "http://www.panoramio.com/panoramio.kml?LANG=en_US.utf8", true);
  public static final KmlUrlReader WIKIPEDIA = new KmlUrlReader(
      "http://lbs.nutiteq.ee/kml/wikipedia_gn.php?", true);
  public static final KmlUrlReader WIKIMAPIA = new KmlUrlReader(
      "http://lbs.nutiteq.com/kml/wikimapia.php?", true);

  public static J2MEPolishSample instance;
  private List mainMenuScreen;
  Command cmdQuit = new Command(Locale.get("QUIT"), Command.EXIT, 10);
  private Display display;
  public MapScreen mapScreen;
  private PlaceTreeForm placeTreeScreen;
  private PlaceDetailsForm placeDetailsForm;
  private MapItem mapItem;
  private List gpsOptions;
  private LibraryGPSSettings libraryGPSSettings;

  private static final Command cmdSelect = new Command(Locale.get("CMD_SELECT"), Command.OK, 1);
  public static final Command cmdBack = new Command(Locale.get("BACK"), Command.BACK, 1);
  public static final Command cmdToMain = new Command(Locale.get("TOMAINSCREEN"), Command.BACK, 0);
  public static final Command cmdFocusMap = new Command(Locale.get("FOCUSMAP"), Command.OK, 0);
  public static final Command cmdDetails = new Command(Locale.get("DETAILS"), Command.ITEM, 1);

  public static final WgsPoint STARTUP = new WgsPoint(2.14874, 41.37482);
  private LocationSource locationSource;
  private Image gpsImage;
  private Image gpsConnectionLostImage;

  public J2MEPolishSample() {
    instance = this;
  }

  public Display getDisplay(){
      if(this.display == null)
          this.display = Display.getDisplay(this);
      return this.display;
  }

  public List getMainMenuScreen(){
      if(this.mainMenuScreen == null) {
    	    //#style mainScreen
    	    this.mainMenuScreen = new List(Locale.get("TITLE"), List.IMPLICIT);
    	    //#style mainCommand
    	    this.mainMenuScreen.append(Locale.get("OPENMAP"), null);
    	    //#style mainCommand
    	    this.mainMenuScreen.append(Locale.get("SERVICES"), null);
    	    //#style mainCommand
    	    this.mainMenuScreen.append(Locale.get("GPS"), null);
    	    //#style mainCommand
    	    this.mainMenuScreen.append(Locale.get("QUIT"), null);

    	    this.mainMenuScreen.setCommandListener(this);
    	    this.mainMenuScreen.addCommand(this.cmdQuit);
      }
      return this.mainMenuScreen;
  }

  protected void destroyApp(final boolean unconditional) throws MIDletStateChangeException {
    instance.notifyDestroyed();
    instance = null;
  }

  protected void pauseApp() {
	  // ignore
  }

  /**
   * Start application show main menu.
   */
  protected void startApp() throws MIDletStateChangeException {
    this.gpsImage =  Utils.createImage("/gps_marker.png");
    this.gpsConnectionLostImage = Utils.createImage("/gps_connection_lost.png");
    this.mapItem = createAndStartMapItem();
    this.display = Display.getDisplay(this);
    this.display.setCurrent(getMainMenuScreen());
  }

  private MapItem createAndStartMapItem() {
    final MapItem mapDisplay = new MapItem(10, 10);
    mapDisplay.setMap(new NutiteqStreamedMap("http://aws.nutiteq.ee/mapstream.php?ts=64&",
        "CloudMade", 64, 0, 18));
    mapDisplay.setOnMapListener(mapDisplay);
    mapDisplay.showDefaultControlsOnScreen(false);

    mapDisplay.defineControlKey(ControlKeys.MOVE_UP_KEY, -1);
    mapDisplay.defineControlKey(ControlKeys.MOVE_DOWN_KEY, -2);
    mapDisplay.defineControlKey(ControlKeys.MOVE_LEFT_KEY, -3);
    mapDisplay.defineControlKey(ControlKeys.MOVE_RIGHT_KEY, -4);
    mapDisplay.defineControlKey(ControlKeys.SELECT_KEY, -5);

    mapDisplay.startMapping();

    return mapDisplay;
  }

  public void commandAction(final Command cmd, final Displayable disp) {
    if (cmd == List.SELECT_COMMAND && disp == this.mainMenuScreen) {
      final int selectedItem = this.mainMenuScreen.getSelectedIndex();
      switch (selectedItem) {
      case 0:
        openMapView(true);
        break;
      case 1:
        show(new ServicesList(this.mainMenuScreen, this.mapItem));
        break;
      case 2:
    	if (this.gpsOptions == null) {
			//#style serviceList
			this.gpsOptions = new List("Positioning implementations", List.IMPLICIT);
			//#style serviceListItem
			this.gpsOptions.append("Internal GPS (JSR179)", null);
			//#style serviceListItem
			this.gpsOptions.append("Bluetooth GPS", null);
			//#style serviceListItem
			this.gpsOptions.append("SonyEricsson CellID single query", null);
			this.gpsOptions.addCommand(cmdBack);
			this.gpsOptions.addCommand(cmdSelect);
			this.gpsOptions.setCommandListener(this);
    	}
        show(this.gpsOptions);
        break;
      case 3: {
        if (this.mapScreen != null) {
          this.mapScreen.finishMapping();
        }
        notifyDestroyed();
      }
      }
    } else if (cmd == this.cmdQuit) {
      this.mapItem.stopMapping();
      notifyDestroyed();
    } else if (cmd == cmdToMain) {
      Display.getDisplay(this).setCurrent(this.mainMenuScreen);
    } else if (cmd == cmdFocusMap) {
      openMapAtPlace(this.placeTreeScreen.tree.getFocusedIndex());
    } else if (disp == this.gpsOptions) {
      if (cmd == cmdSelect || cmd == List.SELECT_COMMAND) {
        switch (this.gpsOptions.getSelectedIndex()) {
        case LibraryGPSSettings.PROVIDER_BLUETOOTH:
          final BluetoothGpsBrowser gpsBrowser = new BluetoothGpsBrowser("Bluetooth GPS", this,
              "Searching...", "No devices found", "Select", "Back");
          show(gpsBrowser);
          return;
        default:
          show(new LibraryGPSSettings(this.gpsOptions.getSelectedIndex(), this.mapItem, this.mainMenuScreen,
              this.gpsImage, this.gpsConnectionLostImage));
          return;
        }
      } else if (cmd == cmdBack) {
        show(this.mainMenuScreen);
      }
    }
  }

  public void show(final Displayable d) {
    Display.getDisplay(this).setCurrent(d);
  }

  private void openMapView(final boolean setDisplay) {
    if (this.mapScreen == null) {
      this.mapScreen = new MapScreen(this.mapItem);
    }

    if (setDisplay) {
      Display.getDisplay(this).setCurrent(this.mapScreen);
    }
  }

  public void openPlaceDetailsForm(final OnMapElement element, final Displayable current) {
    if (this.placeDetailsForm == null) {
      this.placeDetailsForm = new PlaceDetailsForm();
    }
    if (element instanceof Place) {
	    final PlaceInfo pi = this.mapItem.getAdditionalInfo((Place)element);
	    final NHtmlForm htmlForm = new NHtmlForm(pi.getName());
	    htmlForm.setContent(pi.getDescription().trim());
	    Display.getDisplay(this).setCurrent(htmlForm);
    }
  }

  private void openPlaceTreeView(final boolean setDisplay) {
    if (this.placeTreeScreen == null) {
      this.placeTreeScreen = new PlaceTreeForm(Locale.get("PLACELIST"));
    }
    if (setDisplay) {
      Display.getDisplay(this).setCurrent(this.placeTreeScreen);
    }

  }

  public void openTreeInPlace(final Place p) {
    if (this.placeTreeScreen == null) {
      openPlaceTreeView(false);
    }
    //placeTreeScreen.tree.focus(p.getId());
    Display.getDisplay(this).setCurrent(this.placeTreeScreen);
  }

  public void openMapAtPlace(final int placeID) {
    if (this.mapScreen == null) {
      openMapView(false);
    }
    //mapScreen.setFocus(placeID);
    Display.getDisplay(this).setCurrent(this.mapScreen);
  }

  public void browsingCanceled() {
    show(this.mainMenuScreen);
  }

  public void setLocationSource(final LocationSource locationSource) {
    final LibraryGPSSettings d = new LibraryGPSSettings(this.gpsOptions.getSelectedIndex(), this.mapItem,
        this.mainMenuScreen, this.gpsImage, this.gpsConnectionLostImage);
    d.setBluetoothStuff(locationSource);
    show(d);
  }
}
