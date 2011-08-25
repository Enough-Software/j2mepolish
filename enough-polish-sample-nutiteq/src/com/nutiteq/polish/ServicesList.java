package com.nutiteq.polish;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import com.nutiteq.kml.KmlService;
import com.nutiteq.kml.KmlUrlReader;
import com.nutiteq.polish.screens.MapItem;
import com.nutiteq.utils.Utils;

import de.enough.polish.util.Locale;

public class ServicesList extends List implements CommandListener {
  private final Command back;
  //private final Command done;
  private final Displayable backTo;
  private final MapItem mapItem;

  public ServicesList(final Displayable backTo, final MapItem mapItem) {
	//#style serviceList
    super(Locale.get("SERVICES"), List.MULTIPLE);
    this.backTo = backTo;
    this.mapItem = mapItem;

    back = new Command(Locale.get("CMD_BACK"), Command.BACK, 0);
    //done = new Command(Locale.get("CMD_DONE"), Command.OK, 0);

    final KmlService[] used = mapItem.getKmlServices();
    //#style serviceListItem
    append("Panoramio", Utils.createImage("/panoramio-marker.png"));
    //#style serviceListItem
    append("Wikipedia", null);
    //#style serviceListItem
    append("WikiMapia", Utils.createImage("/wikimapia.png"));

    setSelectedIndex(0, contains(used, J2MEPolishSample.PANORAMIO));
    setSelectedIndex(1, contains(used, J2MEPolishSample.WIKIPEDIA));
    setSelectedIndex(2, contains(used, J2MEPolishSample.WIKIMAPIA));

    addCommand(back);
    //addCommand(done);
    //setSelectCommand(done);

    setCommandListener(this);
  }

  private boolean contains(final KmlService[] used, final KmlUrlReader service) {
    for (int i = 0; i < used.length; i++) {
      if (used[i] == service) {
        return true;
      }
    }
    return false;
  }

  public void commandAction(final Command cmd, final Displayable d) {
    if (cmd == back) {
    	/*  J2MEPolishSample.instance.show(backTo);
    } else {*/
      mapItem.removeKmlService(J2MEPolishSample.PANORAMIO);
      mapItem.removeKmlService(J2MEPolishSample.WIKIPEDIA);
      mapItem.removeKmlService(J2MEPolishSample.WIKIMAPIA);

      //hack, hack, hack
      final boolean[] selected = new boolean[3];
      getSelectedFlags(selected);

      if (selected[0]) {
        mapItem.addKmlService(J2MEPolishSample.PANORAMIO);
      }
      if (selected[1]) {
        mapItem.addKmlService(J2MEPolishSample.WIKIPEDIA);
      }
      if (selected[2]) {
        mapItem.addKmlService(J2MEPolishSample.WIKIMAPIA);
      }

      J2MEPolishSample.instance.show(backTo);
    }
  }
}
