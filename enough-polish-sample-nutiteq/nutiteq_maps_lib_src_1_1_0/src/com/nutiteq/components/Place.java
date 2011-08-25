package com.nutiteq.components;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.nutiteq.log.Log;
import com.nutiteq.maps.GeoMap;
import com.nutiteq.utils.Utils;

/**
 * Place object to be used for showing location points on maps.
 */
public class Place implements OnMapElement {
    private static final int CLICK_BOX_SIZE = 16;

    private Placemark defaultPlacemark;
    private Placemark activePlacemark;
    private WgsPoint wgs;
    private MapPos mapPosition;
    private final Label label;
    private final int id;
    private OnMapElement[] elements;
    private final boolean serverSideRender;
    private boolean centered;

    // TODO jaanus : do something about all these overloaded constructors
    public Place(final int id, final String name, final Image icon,
            final WgsPoint coordinates) {
        this(id, name, new PlaceIcon(icon, icon.getWidth() / 2, icon
                .getHeight() / 2), coordinates);
    }

    public Place(final int id, final String name, final Placemark icon,
            final WgsPoint coordinates) {
        this(id, (name == null) ? null : new PlaceLabel(name), icon,
                coordinates, false);
    }

    public Place(final int id, final Label label, final Placemark icon,
            final WgsPoint coordinates) {
        this(id, label, icon, coordinates, false);
    }

    public Place(final int id, final Label label,
            final Placemark defaultPlacemark, final Placemark activePlacemark,
            final WgsPoint coordinates) {
        this(id, label, defaultPlacemark, activePlacemark, coordinates, false);
    }

    public Place(final int id, final Label label,
            final Placemark defaultPlacemark, final Placemark activePlacemark,
            final WgsPoint coordinates, final boolean serverSideRender) {
        this.id = id;
        this.label = label;
        this.defaultPlacemark = defaultPlacemark;
        this.activePlacemark = activePlacemark;
        wgs = coordinates;
        this.serverSideRender = serverSideRender;
        if (label != null && (label instanceof PlaceLabel)) {
            ((PlaceLabel) label).setUsedIcon(defaultPlacemark);
        }
    }

    public Place(final int id, final Label label, final Placemark icon,
            final WgsPoint coordinates, final boolean serverSideRender) {
        this(id, label, icon, icon, coordinates, serverSideRender);
    }

    public Place(final int id, final String name, final Image icon,
            final double lonWgs, final double latWgs) {
        this(id, name, icon, new WgsPoint(lonWgs, latWgs));
    }

    public Place(final int id, final Label label, final Image icon,
            final WgsPoint coordinates) {
        this(id, label, new PlaceIcon(icon, icon.getWidth() / 2, icon
                .getHeight() / 2), coordinates, false);
    }

    public Place(final int id, final Label label, final Image icon,
            final double lonWgs, final double latWgs) {
        this(id, label, new PlaceIcon(icon, icon.getWidth() / 2, icon
                .getHeight() / 2), new WgsPoint(lonWgs, latWgs), false);
    }

    /**
     * Place coordinates in WGS84
     * 
     * @return coordinates in WGS84
     */
    public WgsPoint getWgs() {
        return wgs;
    }

    /**
     * Not part of public API
     * 
     * @return map position on pixel map
     */
    public MapPos getMapPosition() {
        return mapPosition;
    }

    /**
     * Get place name
     * 
     * @return name
     */
    public String getName() {
        return label.getLabel();
    }

    /**
     * Get place label
     * 
     * @return place label
     */
    public Label getLabel() {
        return label;
    }

    /**
     * Get place icon
     * 
     * @return place icon
     */
    public Image getIcon() {
        return (defaultPlacemark instanceof PlaceIcon) ? ((PlaceIcon) defaultPlacemark)
                .getIcon()
                : null;
    }

    public Placemark getPlacemark() {
        return defaultPlacemark;
    }

    /**
     * Get place id
     * 
     * @return place id
     */
    public int getId() {
        return id;
    }

    public boolean isVisible(final int viewX, final int viewY,
            final int viewWidth, final int viewHeight, final int zoom) {
        if (wgs != null && mapPosition != null && defaultPlacemark != null) {
            if (Utils.rectanglesIntersect(viewX, viewY, viewWidth, viewHeight,
                    mapPosition.getX() - defaultPlacemark.getAnchorX(zoom),
                    mapPosition.getY() - defaultPlacemark.getAnchorY(zoom),
                    defaultPlacemark.getWidth(zoom), defaultPlacemark
                            .getHeight(zoom))) {
                return true;
            }
        }

        if (elements != null && elements.length > 0) {
            for (int i = 0; i < elements.length; i++) {
                if (elements[i].isVisible(viewX, viewY, viewWidth, viewHeight,
                        zoom)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Not part of public API
     */
    public void paint(final Graphics g, final MapPos middlePoint,
            final int displayCenterX, final int displayCenterY,
            final Rectangle changedMapArea) {
        if (serverSideRender) {
            return;
        }
        if (elements != null) {
            for (int i = 0; i < elements.length; i++) {
                elements[i].paint(g, middlePoint, displayCenterX,
                        displayCenterY, changedMapArea);
            }
        }

        if (wgs == null) {
            return;
        }

        final Placemark usedPlacemark = centered ? activePlacemark
                : defaultPlacemark;

        final int topX = mapPosition.getX() - middlePoint.getX()
                + displayCenterX
                - usedPlacemark.getAnchorX(middlePoint.getZoom());
        final int topY = mapPosition.getY() - middlePoint.getY()
                + displayCenterY
                - usedPlacemark.getAnchorY(middlePoint.getZoom());
        usedPlacemark.paint(g, topX, topY, middlePoint.getZoom());
    }

    public boolean isCentered(final MapPos middlePoint) {
        if (mapPosition != null && defaultPlacemark != null) {
            Log.debug("placeMark " + this.getName());
            Log.debug("mapPosition " + mapPosition + " middlePoint "
                    + middlePoint + " " + middlePoint);
            int diffx = mapPosition.getX() - middlePoint.getX();
            int diffy = mapPosition.getY() - middlePoint.getY();
            Log.debug("diffx " + diffx + " y " + diffy);

            if ((Math.abs(diffx) < CLICK_BOX_SIZE)
                    && (Math.abs(diffy) < CLICK_BOX_SIZE)) {
                Log.debug("Clicked " + this.getName());
                return true;
            } else {
                Log.debug("NOT clicked " + this.getName());
            }

            // return Utils.rectanglesIntersect(middlePoint.getX(),
            // middlePoint.getY(), CLICK_BOX_SIZE, CLICK_BOX_SIZE, mapPosition
            // .getX()
            // - defaultPlacemark.getAnchorX(middlePoint.getZoom()),
            // mapPosition.getY()
            // - defaultPlacemark.getAnchorY(middlePoint.getZoom()),
            // defaultPlacemark
            // .getWidth(middlePoint.getZoom()),
            // defaultPlacemark.getHeight(middlePoint.getZoom()));
        }

        if (elements != null && elements.length > 0) {
            for (int i = 0; i < elements.length; i++) {
                if (elements[i].isCentered(middlePoint)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }

        // TODO jaanus : looks like bad design
        if (obj instanceof KmlPlace) {
            final Place p = ((KmlPlace) obj).getPlace();

            return equals(p);
        }

        if (!(obj instanceof Place)) {
            return false;
        }

        final Place other = (Place) obj;

        if (elements != null && other.elements != null && elements.length > 0
                && elements.length == other.elements.length) {
            for (int i = 0; i < elements.length; i++) {
                if (!elements[i].equals(other.elements[i])) {
                    return false;
                }
            }

            return true;
        } else {
            return wgs != null
                    && other.wgs != null
                    && wgs.toInternalWgs().equals(other.wgs.toInternalWgs())
                    && ((label == null && other.label == null) || (label != null && label
                            .equals(other.label)));
        }
    }

    public int hashCode() {
        throw new RuntimeException("hashCode() not implemented!");
    }

    /**
     * Not part of public API
     */
    public void setIcon(final Image image) {
        if (image == null) {
            defaultPlacemark = null;
        } else {
            this.setIcon(image, image.getWidth() / 2, image.getHeight() / 2);
        }
    }

    /**
     * Set the icon for this place and the icon anchor position.
     * 
     * @param image
     *            icon used
     * @param anchorX
     *            icon anchor position (x)
     * @param anchorY
     *            icon anchor position (y)
     */
    private void setIcon(final Image image, final int anchorX, final int anchorY) {
        defaultPlacemark = new PlaceIcon(image, anchorX, anchorY);
        activePlacemark = new PlaceIcon(image, anchorX, anchorY);
        if (label != null && (label instanceof PlaceLabel)) {
            ((PlaceLabel) label).setUsedIcon(defaultPlacemark);
        }
    }

    /**
     * Not part of public API
     */
    public void setOnMapElements(final OnMapElement[] elements) {
        this.elements = elements;
    }

    /**
     * Not part of public API
     */
    public void calculatePosition(final GeoMap displayedMap, final int zoomLevel) {
        if (wgs != null) {
            // if we have some concrete place, like POI, not some area or line
            mapPosition = displayedMap.wgsToMapPos(wgs.toInternalWgs(),
                    zoomLevel);
        }

        if (elements != null && elements.length > 0) {
            for (int i = 0; i < elements.length; i++) {
                elements[i].calculatePosition(displayedMap, zoomLevel);
            }
        }
    }

    /**
     * Not part of public API
     */
    public OnMapElement[] getElements() {
        return elements;
    }

    /**
     * Set the point for this place. Should only be called before adding the
     * place to the map.
     * 
     * @param p
     *            new point
     * @deprecated since 0.4.0
     */
    public void setWgs(WgsPoint p) {
        // if we call this after the point is added to the map, we should also
        // call
        // calculatePosition, but we can't do that because we don't know the
        // GeoMap
        // that contains this point
        this.wgs = p;
    }

    public int distanceInPixels(final MapPos middlePoint) {
        if (mapPosition != null) {
            return mapPosition.distanceInPixels(middlePoint);
        }

        if (elements != null && elements.length > 0) {
            int distance = Integer.MAX_VALUE;
            for (int i = 0; i < elements.length; i++) {
                distance = Math.min(distance, elements[i]
                        .distanceInPixels(middlePoint));

                if (distance == 0) {
                    break;
                }
            }

            return distance;
        }

        return Integer.MAX_VALUE;
    }

    public void labelClicked(final MapPos middlePoint, final int displayWidth,
            final int displayHeight, final int clickX, final int clickY) {
        if (label == null) {
            return;
        }

        final int screenX = mapPosition.getX() - middlePoint.getX()
                + displayWidth / 2;
        final int screenY = mapPosition.getY() - middlePoint.getY()
                + displayHeight / 2;
        label.labelClicked(screenX, screenY, displayWidth, displayHeight,
                clickX, clickY);
    }

    public boolean pointOnLabel(final MapPos middlePoint,
            final int displayWidth, final int displayHeight, final int clickX,
            final int clickY) {
        if (label == null || mapPosition == null) {
            return false;
        }

        final int screenX = mapPosition.getX() - middlePoint.getX()
                + displayWidth / 2;
        final int screenY = mapPosition.getY() - middlePoint.getY()
                + displayHeight / 2;
        return label.pointOnLabel(screenX, screenY, displayWidth,
                displayHeight, clickX, clickY);
    }

    public Point getLabelViewUpdate(final MapPos middlePoint,
            final int displayWidth, final int displayHeight) {
        if (label == null || mapPosition == null) {
            return null;
        }

        final int screenX = mapPosition.getX() - middlePoint.getX()
                + displayWidth / 2;
        final int screenY = mapPosition.getY() - middlePoint.getY()
                + displayHeight / 2;
        return label.getViewUpdate(screenX, screenY, displayWidth,
                displayHeight);
    }

    public void setIsActive(final boolean nextActivityState) {
        centered = nextActivityState;
    }

    public Rectangle toMapArea(final int zoom) {
        // TODO jaanus : can placemarks be null?
        if (mapPosition != null) {
            final Rectangle activeArea = new Rectangle(mapPosition.getX()
                    - activePlacemark.getAnchorX(zoom), mapPosition.getY()
                    - activePlacemark.getAnchorY(zoom), activePlacemark
                    .getWidth(zoom), activePlacemark.getHeight(zoom));
            final Rectangle defaultArea = new Rectangle(mapPosition.getX()
                    - defaultPlacemark.getAnchorX(zoom), mapPosition.getY()
                    - defaultPlacemark.getAnchorY(zoom), defaultPlacemark
                    .getWidth(zoom), defaultPlacemark.getHeight(zoom));
            return Utils.mergeAreas(activeArea, defaultArea);
        }

        return new Rectangle(0, 0, 0, 0);
    }

    public WgsPoint[] getPoints() {
        final WgsPoint[] ret = { wgs };
        return ret;
    }
}
