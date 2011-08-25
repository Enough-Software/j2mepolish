package com.nutiteq.android;

import javax.microedition.lcdui.Graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.nutiteq.BasicMapComponent;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.listeners.MapListener;

/**
 * Simple map view handler for displaying map component on screen.
 */
public class MapView extends View implements MapListener {

  private static final int ACTION_POINTER_1_UP = 6;
  private static final int ACTION_POINTER_2_UP = 262;
private static final int ACTION_POINTER_2_DOWN = 261;
  
private BasicMapComponent mapComponent;
  private Graphics g;
  private Canvas wrapped;
  private RepaintHandler repaintHandler;
  private MapListener appMapListener;
  private float altPointerStartDist;
 private boolean dualZoom;

  public MapView(final Context context, final BasicMapComponent component) {
    super(context);
    setFocusable(true);
    mapComponent = component;
    appMapListener = mapComponent.getMapListener();
    mapComponent.setMapListener(this);
    repaintHandler = new RepaintHandler(this);
  }

  @Override
  protected void onDraw(final Canvas canvas) {
    if (wrapped != canvas) {
      wrapped = canvas;
      g = new Graphics(wrapped);
      //TODO jaanus : what happens on size change
      mapComponent.resize(getWidth(), getHeight());
    }
    mapComponent.paint(g);
  }

  public boolean onTouchEvent(final MotionEvent event) {
      System.out.println("touch event action="+event.getAction());
      boolean hasMultiTouch = Integer.parseInt(Build.VERSION.SDK) >= 5;
      int nPointer = hasMultiTouch ? MotionEventWrap.getPointerCount(event) : 1;
      
      final int x = (int) event.getX();
      final int y = (int) event.getY();
      switch (event.getAction()) {
          case ACTION_POINTER_1_UP: // dual-touch finished
          case ACTION_POINTER_2_UP:
              if(hasMultiTouch){

              float altPointerStartX2 = MotionEventWrap.getX(event,1);
              float altPointerStartY2 = MotionEventWrap.getY(event,1);
              float altPointerStartDist2 = ((altPointerStartX2 - x) * (altPointerStartX2 - x))
                      + ((altPointerStartY2 - y) * (altPointerStartY2 - y));

              float moved = altPointerStartDist2 - altPointerStartDist;

              System.out.println("dt finish "+altPointerStartX2+" "+altPointerStartY2+" "+altPointerStartDist2+ " "+moved);
              
              if (moved < -10000 && moved > -70000) {
                  mapComponent.zoomOut();
                  System.out.println("Zoomed out to " + mapComponent.getZoom());
              }
              if (moved < -70000) {
                  mapComponent.zoomOut();
                  mapComponent.zoomOut();
                  System.out
                          .println("Zoomed out -2 to " + mapComponent.getZoom());
              }

              if (moved > 10000 && moved < 70000) {
                  mapComponent.zoomIn();
                  System.out.println("Zoomed in to " + mapComponent.getZoom());
              }
              if (moved > 70000) {
                  mapComponent.zoomIn();
                  mapComponent.zoomIn();
                  System.out.println("Zoomed in +2 to " + mapComponent.getZoom());
              }
              }
              break;
          case ACTION_POINTER_2_DOWN: // dual-touch started
              if(hasMultiTouch){
                  dualZoom=true;

              float altPointerStartX = MotionEventWrap.getX(event,1);
              float altPointerStartY = MotionEventWrap.getY(event,1);
              altPointerStartDist = ((altPointerStartX - x) * (altPointerStartX - x))
                      + ((altPointerStartY - y) * (altPointerStartY - y));

              System.out.println("dual-touch started from "+altPointerStartX+" "+altPointerStartY+ " "+altPointerStartDist);
              }
              
              break;
          case MotionEvent.ACTION_DOWN:
              mapComponent.pointerPressed(x, y);

              System.out.println("action down "+x+" "+ y);
              break;
          case MotionEvent.ACTION_MOVE:
              System.out.println("action move");
              if (nPointer == 1 && !dualZoom) {
                  mapComponent.pointerDragged(x, y);
                  System.out.println("dragged "+x+" "+y);
              }
              break;
          case MotionEvent.ACTION_UP:
              mapComponent.pointerReleased(x, y);
              System.out.println("action up "+x+" "+ y);
              dualZoom=false; // reset 
              break;

          }
          return true;
      
  }
  
  
  @Override
  public boolean onKeyDown(final int keyCode, final KeyEvent event) {
    if (event.getRepeatCount() == 0) {
      mapComponent.keyPressed(keyCode);
    } else {
      mapComponent.keyRepeated(keyCode);
    }

    return isMapKey(keyCode);
  }

  private boolean isMapKey(final int keyCode) {
    return keyCode >= 19 && keyCode <= 23;
  }

  @Override
  public boolean onKeyUp(final int keyCode, final KeyEvent event) {
    mapComponent.keyReleased(keyCode);
    return isMapKey(keyCode);
  }

  public void mapClicked(final WgsPoint p) {
    if (appMapListener != null) {
      appMapListener.mapClicked(p);
    }
  }

  public void mapMoved() {
    if (appMapListener != null) {
      appMapListener.mapMoved();
    }
  }

  public void needRepaint(final boolean mapIsComplete) {
    if (appMapListener != null) {
      appMapListener.needRepaint(mapIsComplete);
    }

    if (repaintHandler != null) {
      repaintHandler.sendMessage(new Message());
    }
  }

  public void clean() {
    if (repaintHandler != null) {
      repaintHandler.clean();
    }
    mapComponent = null;
    g = null;
    wrapped = null;
    repaintHandler = null;
    appMapListener = null;
  }
}
