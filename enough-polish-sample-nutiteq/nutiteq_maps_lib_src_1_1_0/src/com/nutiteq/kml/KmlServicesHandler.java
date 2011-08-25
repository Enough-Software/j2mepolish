package com.nutiteq.kml;

import java.util.Vector;

import javax.microedition.lcdui.Image;

import com.nutiteq.BasicMapComponent;
import com.nutiteq.cache.Cache;
import com.nutiteq.cache.ImageWaiter;
import com.nutiteq.components.KmlPlace;
import com.nutiteq.components.MapPos;
import com.nutiteq.components.Place;
import com.nutiteq.components.PlaceInfo;
import com.nutiteq.components.WgsBoundingBox;
import com.nutiteq.io.ResourceRequestor;
import com.nutiteq.net.DownloadRequestor;
import com.nutiteq.task.TasksRunner;
import com.nutiteq.utils.Utils;

/**
 * @author Jaak Laineste
 *
 */
public class KmlServicesHandler implements DownloadRequestor, ImageWaiter, KmlElementsWaiter {
  private WgsBoundingBox lastUpdateBBox;
  private int lastUpdateZoom;

  private KmlService[] services = new KmlService[0];
  private Vector[] placesForService = new Vector[0];
  private boolean[] needsUpdate = new boolean[0];

  private final KmlStylesCache stylesCache = new KmlStylesCache();

  private final BasicMapComponent mapComponent;

  private final TasksRunner tasksRunner;
  private boolean zoomedOut;

  public KmlServicesHandler(final BasicMapComponent mapComponent, final TasksRunner taskRunner) {
    this.mapComponent = mapComponent;
    tasksRunner = taskRunner;
  }

  public KmlService[] getServices() {
    return services;
  }

  public void removeService(final KmlService service) {
    // TODO jaanus : optimize
    boolean found = false;
    for (int i = 0; i < services.length; i++) {
      found = services[i] == service;

      if (found) {
        break;
      }
    }

    if (!found) {
      return;
    }

    final KmlService[] result = new KmlService[services.length - 1];
    final Vector[] kmlPlaces = new Vector[placesForService.length - 1];
    final boolean[] update = new boolean[needsUpdate.length - 1];

    int count = 0;

    Place[] removedPlaces = null;

    for (int i = 0; i < services.length; i++) {
      if (services[i] == service) {
        final Vector removedKmlPlaces = placesForService[i];
        removedPlaces = new Place[removedKmlPlaces.size()];
        for (int j = 0; j < removedKmlPlaces.size(); j++) {
          removedPlaces[j] = ((KmlPlace) removedKmlPlaces.elementAt(j)).getPlace();
        }
        continue;
      }

      result[count] = services[i];
      kmlPlaces[count] = placesForService[i];
      update[count] = needsUpdate[i];
      count++;
    }

    // TODO jaanus : null check to map component
    if (removedPlaces != null) {
      mapComponent.removePlaces(removedPlaces);
    }

    services = result;
    placesForService = kmlPlaces;
    needsUpdate = update;
  }

  public void addService(final KmlService service) {
    // TODO jaanus : handle duplicates
    services = appendObject(services, service);
    placesForService = resizePlacesArrayByOne(placesForService);
    needsUpdate = addOneBooleanToArray(needsUpdate, false);
  }

  private boolean[] addOneBooleanToArray(final boolean[] source, final boolean valueForAdded) {
    final boolean[] result = new boolean[source.length + 1];
    System.arraycopy(source, 0, result, 0, source.length);
    result[result.length - 1] = valueForAdded;
    return result;
  }

  private Vector[] resizePlacesArrayByOne(final Vector[] original) {
    final Vector[] result = new Vector[original.length + 1];
    System.arraycopy(original, 0, result, 0, original.length);
    result[result.length - 1] = new Vector();
    return result;
  }

  private KmlService[] appendObject(final KmlService[] array, final KmlService added) {
    if (array.length == 0) {
      return new KmlService[] { added };
    }

    final KmlService[] result = new KmlService[array.length + 1];
    System.arraycopy(array, 0, result, 0, array.length);
    result[result.length - 1] = added;
    return result;
  }

  // TODO jaanus: !!!!!
  public void mapMoved(final WgsBoundingBox boundingBox, final int zoom) {
    for (int i = 0; i < services.length; i++) {
      if (needsUpdate[i]) {
        continue;
      }

      if (services[i].needsUpdate(boundingBox, zoom)) {
        needsUpdate[i] = true;
      }
    }

    zoomedOut = lastUpdateZoom - zoom < 0;

    if (someServiceNeedsUpdate(needsUpdate)) {
      lastUpdateBBox = boundingBox;
      lastUpdateZoom = zoom;
      tasksRunner.enqueueDownloadRequestor(this, Cache.CACHE_LEVEL_NONE);
    }
  }

  private boolean someServiceNeedsUpdate(final boolean[] serviceNeedsUpdate) {
    for (int i = 0; i < serviceNeedsUpdate.length; i++) {
      if (serviceNeedsUpdate[i]) {
        return true;
      }
    }
    return false;
  }

  public ResourceRequestor getDownloadable() {
    if (!someServiceNeedsUpdate(needsUpdate)) {
      return null;
    }

    for (int i = 0; i < needsUpdate.length; i++) {
      if (!needsUpdate[i]) {
        continue;
      }

      final KmlService service = services[i];
      needsUpdate[i] = false;
      return new KmlReader(this, service, service.getServiceUrl(lastUpdateBBox, lastUpdateZoom),
          stylesCache, tasksRunner, service.getDefaultIcon());
    }

    return null;
  }

  public void addKmlPlaces(final KmlService service, final KmlPlace[] addedKmlPlaces) {
    for (int j = 0; j < services.length; j++) {
      if (service != services[j]) {
        continue;
      }

      final Vector kmlPlaces = placesForService[j];
      final Vector addedPlaces = new Vector();
      final Vector removedPlaces = new Vector();

      if (zoomedOut) {
        // remove old places for service
        appendPlacesElements(removedPlaces, kmlPlaces);
        kmlPlaces.setSize(0);
      }

      for (int i = 0; i < addedKmlPlaces.length; i++) {
        if (kmlPlaces.contains(addedKmlPlaces[i])) {
          continue;
        }

        final Place tmpPlace = addedKmlPlaces[i].getPlace();
        removedPlaces.removeElement(tmpPlace);
        kmlPlaces.addElement(addedKmlPlaces[i]);
        addedPlaces.addElement(tmpPlace);
      }

      final int maxElements = service.maxResults() * 2;

      if (kmlPlaces.size() > maxElements) {
        final MapPos middlePoint = mapComponent.getInternalMiddlePoint();
        final int[] distances = new int[kmlPlaces.size()];
        final int[] indexes = new int[distances.length];
        for (int index = 0; index < indexes.length; index++) {
          indexes[index] = index;
          final MapPos placePos = ((KmlPlace) kmlPlaces.elementAt(index)).getPlace()
              .getMapPosition();
          // if new place from server, place location on map has not been
          // calculated
          distances[index] = placePos == null ? 0 : middlePoint.distanceInPixels(placePos);
        }

        Utils.doubleBubbleSort(distances, indexes);

        final Vector removedElements = new Vector();
        for (int k = distances.length - 1; k > maxElements; k--) {
          final KmlPlace p = (KmlPlace) kmlPlaces.elementAt(indexes[k]);
          removedElements.addElement(p);
          removedPlaces.addElement(p.getPlace());
        }

        for (int k = 0; k < removedElements.size(); k++) {
          kmlPlaces.removeElement(removedElements.elementAt(k));
        }
      }

      if (addedPlaces.size() > 0) {
        final Place[] added = new Place[addedPlaces.size()];
        addedPlaces.copyInto(added);

        mapComponent.addPlaces(added, removedPlaces.size() == 0);
      }

      if (removedPlaces.size() > 0) {
        final Place[] removed = new Place[removedPlaces.size()];
        removedPlaces.copyInto(removed);
        mapComponent.removePlaces(removed);
      }
    }
  }

  private void appendPlacesElements(final Vector places, final Vector kmlPlaces) {
    for (int k = 0; k < kmlPlaces.size(); k++) {
      places.addElement(((KmlPlace) kmlPlaces.elementAt(k)).getPlace());
    }
  }

  public void imageDownloaded(final String url, final Image image) {
    boolean updated = false;
    for (int j = 0; j < services.length; j++) {
      final Vector kmlPlaces = placesForService[j];
      for (int i = 0; i < kmlPlaces.size(); i++) {
        final KmlPlace kPlace = (KmlPlace) kmlPlaces.elementAt(i);
        if (kPlace.usesIcon(url, stylesCache)) {
          updated = true;
          final Place place = kPlace.getPlace();
          //TODO jaanus : fix this place icon hack
          place.setIcon(image);
        }
      }
    }

    if (updated) {
      // TODO jaanus : fix this repaint hack
      mapComponent.addPlaces(new Place[0]);
    }
  }

  public PlaceInfo getAdditionalInfo(final Place place) {
    for (int i = 0; i < services.length; i++) {
      final int placeIndex = placesForService[i].indexOf(place);
      if (placeIndex >= 0) {
        return ((KmlPlace) placesForService[i].elementAt(placeIndex)).getInfoObject();
      }
    }

    return null;
  }
  
  /**
 * get all places from a KML Service
 * @param service
 * @return Array of KmlPlace[]
 */
public KmlPlace[] getKmlPlaces(final KmlService service) {
    
    if(service == null){
        return null;
    }
    
      Vector out = new Vector();
      
        for (int i = 0; i < services.length; i++) {
            if (service.equals(services[i])) {
                for (int p = 0; p < placesForService[i].size(); p++) {
                    out.addElement((KmlPlace) placesForService[i].elementAt(p));
                }
            }
        
        }
      KmlPlace[] retval = new KmlPlace[out.size()];
      out.copyInto(retval);
      return retval;
      }

public KmlPlace[] getKmlPlaces() {

    Vector out = new Vector();
    
    for (int i = 0; i < services.length; i++) {
            for (int p = 0; p < placesForService[i].size(); p++) {
                out.addElement((KmlPlace) placesForService[i].elementAt(p));
            }
    }
  KmlPlace[] retval = new KmlPlace[out.size()];
  out.copyInto(retval);
  return retval;
}
}
