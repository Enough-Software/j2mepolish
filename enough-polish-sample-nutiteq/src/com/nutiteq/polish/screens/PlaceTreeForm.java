package com.nutiteq.polish.screens;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;

import com.nutiteq.polish.J2MEPolishSample;

import de.enough.polish.ui.TreeItem;

public class PlaceTreeForm extends Form {
  public TreeItem tree;

  public PlaceTreeForm(final String title) {
    super(title);

    //#style placeTree
    tree = new TreeItem(null);
    final Item node;

    //    Place[] places = J2MEPolishSample.instance.mapData.getPlaces();

    final StringItem placeAddress;

    final StringItem placeCoordinates;
    //    for (int i = 0; i < places.length; i++) {
    //      //#style place
    //      node = tree.appendToRoot(places[i].getName(), places[i].getIcon());
    //      //Currently placeCoordinates, placeAddress, placeDescription and placePhone use the same style -> #placeDetail
    //      //#style placeDetail
    //      placeAddress = new StringItem(Locale.get("ADDRESS"), MapData.PLACE_ADDRESSES[i]);
    //      tree.appendToNode(node, placeAddress);
    //      //#style placeDetail
    //      placeCoordinates = new StringItem(Locale.get("COORDINATES"), places[i].getWgs().toString());
    //      tree.appendToNode(node, placeCoordinates);
    //
    //    }
    //tree.focus(0);
    this.append(tree);

    this.addCommand(J2MEPolishSample.cmdToMain);
    this.addCommand(J2MEPolishSample.cmdFocusMap);
    this.addCommand(J2MEPolishSample.cmdDetails);
    this.setCommandListener(J2MEPolishSample.instance);
  }

}
