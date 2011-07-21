//#condition polish.usePolishGui
/*
 * Copyright (c) 2010 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */

//#if !tmp.isContextIncluded
package de.enough.polish.processing;
//#endif

//#if !tmp.isContextIncluded || tmp.isContextIncludedForImports

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreNotFoundException;

import de.enough.polish.math.HFloat;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import de.enough.polish.util.DrawUtil;
import de.enough.polish.util.MathUtil;
import de.enough.polish.util.RgbImage;

//#endif

/**
 * This is the standard Mobile Processing implementation for J2ME Polish.
 *
 * @author Ovidiu Iliescu
 */
//#if !tmp.isContextIncluded
public class ProcessingContext implements ProcessingInterface {
//#endif
//#if !tmp.isContextIncluded || tmp.isContextIncludedForCode

    // Inner-working methods. Not related to the Mobile Processing specs
    // -----------------------------------------------------------------

    public ProcessingContextContainerInterface _parent = null ;
    
    public Image _buffer;
    public Graphics _bufferg ;
    public int _timeBetweenFrames = 20;
    public boolean _haltExecution = false;
    public boolean _loop = true;
    public boolean _refreshFlag = false;

    public int _prevWidth = -1;
    public int _prevHeight = -1;

    public int _shapeMode = POLYGON ;
    public int _rectMode = 0;
    public int _ellipseMode = 0;
    public int _imageMode = 0;

    public int[] _vertex;
    public int _vertexIndex;
    public int[] _curveVertex;
    public int _curveVertexIndex;
    public int[] _stack;
    public int _stackIndex;

    public boolean _hasStroke = true;
    public boolean _hasFill = true;
    public int _strokeWidth = 1;
    public int _strokeColor = 0;
    public int _fillColor = 0;
    public int _bgColor = 0;
    public PImage _bgImage = null;
    public boolean _bgImageMode = false ;
    public volatile boolean _repaintBackground = false ;
    public boolean _transparentDrawing = false;
    public boolean _fastDrawingEnabled = true ;
    public int _transparentColor = 0x00FFFFFF;
    public int _colorMode = RGB;
    public int _colorRange1 = 255;
    public int _colorRange2 = 255;
    public int _colorRange3 = 255;
    
    public long _lastTransparentRgbImageTime = 0;
    transient RgbImage _transparentImage = null ;

    public static final String SOFTKEY1_NAME    = "SOFT1";
    public static final String SOFTKEY2_NAME    = "SOFT2";
    public static final String SEND_NAME        = "SEND";

    public static final int     MULTITAP_KEY_SPACE      = 0;
    public static final int     MULTITAP_KEY_UPPER      = 1;
    public static final String  MULTITAP_PUNCTUATION    = ".,?!'\"-_:;/()@&#$%*+<=>^";

    public boolean   _multitap;
    public char[]    _multitapKeySettings;
    public int       _multitapLastEdit;
    public int       _multitapEditDuration;
    public boolean   _multitapIsUpperCase;
    public String    _multitapPunctuation;
    public boolean   _pointerPressed = false ;

    public boolean _areKeypressesCaptured = false ;
    public boolean _arePointerEventsCaptured = false ;
    public boolean _areSoftkeysCaptured = false ;

    public Calendar _calendar = null ;
    public Runtime _runtime ;
    public long _startTime = -1 ;
    public long _lastFrameTime = -1 ;

    public long _lastDrawTime = 0;

    public String _softkeyLabel = null ;

    public PFont _defaultFont = new PFont ( Font.getDefaultFont() );
    public int _textAlignMode = LEFT;

    public Random _random = null ;

    // Coordinates array used within line()
    int __arrX[] = new int[4];
    int __arrY[] = new int[4];
    
    // Due to the order Java initializes class members when inheritance is
    // involved, we need to call setup() in a lazy manner, to ensure that
    // all offspring class members that are initialized directly in their
    // declarations are properly initialized _before_ setup() is called.
    public boolean _hasBeenInitialized = false ;



    /**
     * Creates a new ProcessingContext of size (-1,-1)
     */
    public ProcessingContext ()
    {
        _initVars (-1,-1);
    }

    /**
     * Creates a new ProcessingContext with the specified size
     * @param width the width of the context
     * @param height the height of the context
     */
    public ProcessingContext(int width, int height)
    {
            _initVars(width, height);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#triggerRepaint()
     */
    public void triggerRepaint()
    {
            if ( _parent != null )
            {
                _parent.processingRequestRepaint();
            }
    }

   // Used to process key events
    public static final Canvas canvas = new Canvas() {

        public void paint(Graphics graphics) {

        }
    };

    /**
     * Checks if a refresh of the buffer is needed.
     * @return true if the buffer should be refreshed, false otherwise.
     * @see de.enough.polish.processing.ProcessingInterface#checkForRefresh() 
     */
    public boolean checkForRefresh() {
                
        if (_haltExecution)
        {
            return false;
        }

        // If draw() is not called in a loop
        if (!_loop)
        {

            // Tell us if the buffer has been updated since the last time
            // checkForRefresh() was called
            boolean refreshValue = _refreshFlag ;
            _refreshFlag = false;
            return refreshValue ;
        }
        else
        {
            // If we're in a loop and enough time has passed to render a frame,
            // signal this
            long now = System.currentTimeMillis();

            if (now - _lastFrameTime > _timeBetweenFrames)
            {
                return true;
            } else
            {
                return false;
            }
        }
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#setParent(de.enough.polish.processing.ProcessingContextContainerInterface)
     */
    public void setParent ( ProcessingContextContainerInterface parent)
    {
        this._parent = parent;
    }


    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#executeRefresh(boolean)
     */
    public void executeRefresh(boolean alsoUpdateLastFrameTime)
    {
        if ( alsoUpdateLastFrameTime )
        {
            _refresh();
        }
        else
        {
        // Since the refresh is forced, we should not modify the last frame time
        // as technically the refresh is not part of the natural animation flow.
        // Doing so also avoids a lot of refresh/repaint issues.

        long goodFrameTime = _lastFrameTime;
        _refresh();
        _lastFrameTime = goodFrameTime;
        }
    }

    /**
     * Refreshes the screen and sets the buffer update flag accordingly.
     */
    public void _refresh() {

        if (_haltExecution)
        {
            return;
        }

        if( _hasBeenInitialized == false )
        {
            _startTime = System.currentTimeMillis() ;

            // Wait a while to make sure we don't get out of memory errors
            try
            {
                Thread.sleep(5);
            }
            catch (Exception ex)
            {
                
            }

            redraw();
            return;
        }

        if ( _bufferg != null )
        {
            // Paint the background
            if ( _repaintBackground == true )
            {
                
                if ( _bgImageMode == false )
                {
                        int lastColor = _bufferg.getColor() ;
                        _bufferg.setColor(_bgColor);
                        _bufferg.fillRect(0, 0, width, height);
                        _bufferg.setColor(lastColor);
                }
                else
                {
                    _bufferg.drawImage(_bgImage.getImage(), width - _bgImage.getWidth() / 2, height - _bgImage.getHeight() /2, Graphics.TOP | Graphics.LEFT );
                }
            }


        resetMatrix();
        draw();

        _lastFrameTime = System.currentTimeMillis();
        _refreshFlag = true;
        
        }
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#isLooping()
     */
    public boolean isLooping()
    {
        return _loop ;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#getLastFrameTime()
     */
    public long getLastFrameTime()
    {
        return _lastFrameTime ;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#getIntervalBetweenFrames()
     */
    public long getIntervalBetweenFrames()
    {
        if ( _loop == false )
        {
            return Long.MAX_VALUE;
        }
        else
        {
            return _timeBetweenFrames ;
        }
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#getBuffer()
     */
    public Image getBuffer() {
        return _buffer;
    }

    /*
     * Resets the buffer image to the specified dimensions
     * then reinitializes the Processing part of the code by calling setup().
     * All other variables except the ones related to the image size and
     * the ones modified within setup() are left untouched.
     * @param width the width of the buffer
     * @param height the height of the buffer
     */
    public void _resetImageSize(int width, int height)
    {
            _buffer = Image.createImage(width, height);
            _bufferg = _buffer.getGraphics() ;
            _bufferg.setColor(0x00FFFFFF);
            _bufferg.fillRect(0, 0, width, height);
            this.width = width;
            this.height = height;
            ProcessingThread.queueEvent(new ProcessingEvent(this,ProcessingEvent.EVENT_SETUP));
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#signalSizeChange(int, int)
     */
    public void signalSizeChange(int width, int height)
    {
         if ( (width != _prevWidth) || (height != _prevHeight) )
         {
            _prevWidth = width;
            _prevHeight = height;
            _resetImageSize(width, height);
         }

    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#executeInitializationSequence()
     */
    public void executeInitializationSequence()
    {
        setup();
        _hasBeenInitialized = true ;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#repaintBackground()
     */
    public void repaintBackground()
    {
        _repaintBackground = true;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#dontRepaintBackground()
     */
    public void dontRepaintBackground()
    {
        _repaintBackground = false ;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#transparentDrawing()
     */
    public void transparentDrawing()
    {
        _transparentDrawing = true ;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#opaqueDrawing()
     */
    public void opaqueDrawing()
    {
        _transparentDrawing = false ;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#setTransparentColor(de.enough.polish.processing.color)
     */
    public void setTransparentColor(color color)
    {
        _transparentColor = color.color ;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#setTransparentColor(int, int, int)
     */
    public void setTransparentColor(int value1, int value2, int value3)
    {
        _transparentColor = color(value1,value2,value3,255).color;
    }
 
    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#setTransparentColor(int)
     */
    public void setTransparentColor(int gray)
    {
        _transparentColor = color(gray).color;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#getTransparentColor()
     */
    public int getTransparentColor()
    {
        return _transparentColor;
    }


    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#isDrawingTransparent()
     */
    public boolean isDrawingTransparent()
    {
    	//#if polish.midp2
        	return _transparentDrawing ;
    	//#else
        	//#= return false;
    	//#endif
    }


    //#if polish.midp2
    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#getTransparentRgbImage()
     */
    public RgbImage getTransparentRgbImage()
    {
       // Initialize the transparent RgbImage if necessary
       if ( _transparentImage == null )
       {
           _transparentImage = new RgbImage(new int[getBuffer().getWidth() * getBuffer().getHeight()], getBuffer().getWidth());
       }

       // Only update the RGB image if another frame has been drawn since the last time
       // it was updated
       if ( _lastTransparentRgbImageTime != getLastFrameTime() )
       {
           _lastTransparentRgbImageTime = getLastFrameTime() ;

           if ( (_transparentImage.getWidth() != getBuffer().getWidth() ) ||
                   ( _transparentImage.getHeight() != getBuffer().getHeight() ) )
           {
               _transparentImage = new RgbImage(getBuffer(),true);
           }
           else
           {
              getBuffer().getRGB( _transparentImage.getRgbData(), 0, _transparentImage.getWidth(), 0,0,_transparentImage.getWidth(), _transparentImage.getHeight());
           }

           // Process transparency
           int maskColor = ( getTransparentColor() << 8);
           int data[] = _transparentImage.getRgbData();
           int dataLength = data.length -1;
           while (dataLength>=0)
           {
               if ( (data[dataLength] << 8 ) == maskColor )
               {
                   data[dataLength] = 0x00000000;
               }
               dataLength--;
           }

       }

       return _transparentImage;
    }
    //#endif

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#signalPointerDragged(int, int)
     */
    public void signalPointerDragged(int x, int y)
    {
        ProcessingThread.queueEvent(new ProcessingEvent(this,ProcessingEvent.EVENT_POINTER_DRAGGED,x,y));
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#signalPointerReleased(int, int) 
     */
    public void signalPointerReleased(int x, int y)
    {
        ProcessingThread.queueEvent(new ProcessingEvent(this,ProcessingEvent.EVENT_POINTER_RELEASED,x,y));
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#signalPointerPressed(int, int)
     */
    public void signalPointerPressed(int x, int y)
    {
        ProcessingThread.queueEvent(new ProcessingEvent(this,ProcessingEvent.EVENT_POINTER_PRESSED,x,y));
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#setPointerCoordinates(int, int)
     */
    public void setPointerCoordinates(int x, int y)
    {
        pointerX = x;
        pointerY = y;
    }

    /**
     * Sets the raw keycode to use for processing the next keypress.
     * @param keyCode the keycode to process
     */
    public void setRawKeyCode(int keyCode)
    {
        _key(keyCode);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#setKeyAndKeyCode(int, int)
     */
   public void setKeyAndKeyCode(char key, int keyCode)
   {
       this.key = key;
       this.keyCode = keyCode;
   }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#signalKeyPressed(int) 
     */
   public void signalKeyPressed(int keyCode) {
        keyPressed = true;

        if (_multitap) {
            _multitapKeyPressed(keyCode);
        }

        _key(keyCode);
        ProcessingThread.queueEvent(new ProcessingEvent(this,ProcessingEvent.EVENT_KEY_PRESSED,this.key,this.keyCode));
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#signalKeyReleased(int) 
     */
    public void signalKeyReleased(int keyCode) {
        keyPressed = false;

        _key(keyCode);
        ProcessingThread.queueEvent(new ProcessingEvent(this,ProcessingEvent.EVENT_KEY_RELEASED,this.key,this.keyCode));
    }


    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#signalSoftkeyPressed(String)
     */
    public void signalSoftkeyPressed(String label) {
        ProcessingThread.queueEvent(new ProcessingEvent(this,ProcessingEvent.EVENT_SOFTKEY_PRESSED,label));
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#signalApplicationSuspend() 
     */
    public void signalApplicationSuspend()
    {
        ProcessingThread.queueEvent(new ProcessingEvent(this,ProcessingEvent.EVENT_APP_SUSPEND));
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#signalApplicationResume()
     */
    public void signalApplicationResume()
    {
        ProcessingThread.queueEvent(new ProcessingEvent(this,ProcessingEvent.EVENT_APP_RESUME));
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#signalDestroy()
     */
    public void signalDestroy()
    {
        ProcessingThread.queueEvent(new ProcessingEvent(this,ProcessingEvent.EVENT_APP_DESTROY));
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#signalInitialization()
     */
    public void signalInitialization()
    {
        ProcessingThread.queueEvent(new ProcessingEvent(this,ProcessingEvent.EVENT_INIT));
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#signalHasFocus()
     */
    public void signalHasFocus()
    {
        if ( _hasBeenInitialized == false )
        {
            //ProcessingThread.queueEvent(new ProcessingEvent(this,ProcessingEvent.EVENT_SETUP));
        }
        
        ProcessingThread.queueEvent( new ProcessingEvent(this,ProcessingEvent.EVENT_HAS_FOCUS));
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#signalLostFocus
     */
    public void signalLostFocus()
    {
        ProcessingThread.queueEvent( new ProcessingEvent(this,ProcessingEvent.EVENT_LOST_FOCUS));
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#getSoftkeyLabel()
     */
    public String getSoftkeyLabel()
    {
        return _softkeyLabel;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#captureKeyPresses() 
     */
    public void captureKeyPresses()
    {
        _areKeypressesCaptured = true ;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#releaseKeyPresses() 
     */
    public void releaseKeyPresses()
    {
        _areKeypressesCaptured = false;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#areKeypressesCaptured() 
     */
    public boolean areKeypressesCaptured()
    {
        return _areKeypressesCaptured;
    }
    
    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#captureSoftkeys()
     */
    public void captureSoftkeys()
    {
        _areSoftkeysCaptured = true ;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#releaseSoftkeys()
     */
    public void releaseSoftkeys()
    {
        _areSoftkeysCaptured = false ;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#areSoftkeysCaptured()
     */
    public boolean areSoftkeysCaptured()
    {
        return _areSoftkeysCaptured;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#enableFastDrawing()
     */
    public void enableFastDrawing()
    {
        _fastDrawingEnabled = true;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#disableFastDrawing()
     */
    public void disableFastDrawing()
    {
        _fastDrawingEnabled = false;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#capturePointerEvents()
     */
    public void capturePointerEvents()
    {
        _arePointerEventsCaptured = true ;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#releasePointerEvents()
     */
    public void releasePointerEvents()
    {
        _arePointerEventsCaptured = false ;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#arePointerEventsCaptured()
     */
    public boolean arePointerEventsCaptured()
    {
        return _arePointerEventsCaptured ;
    }

    /**
     * Initializes the internal variables of this context and sets the image bufer width and height
     * @param width the width of the buffer
     * @param height the height of the buffer
     */
    public void _initVars(int width, int height)
    {
            background(200);

            _runtime = Runtime.getRuntime() ;
            _hasFill = true;
            _fillColor = 0xFFFFFF;

            _rectMode = CORNER;
            _ellipseMode = CENTER;
            _imageMode = CORNER;

            _prevHeight = - 1;
            _prevWidth = -1;

            _shapeMode = -1;
            _vertex = new int[16];
            _vertexIndex = 0;

            _curveVertex = new int[8];
            _curveVertexIndex = 0;

            multitapBuffer = new char[64];
            multitapText = "";
            _multitapKeySettings = new char[] { '#', '*' };
            _multitapPunctuation = MULTITAP_PUNCTUATION;
            _multitapEditDuration = 1000;

            
            _lastFrameTime = -1 ;

            _stack = new int[6];
            _stackIndex = 0;

            // setup() is called in a lazy manner in checkForRefresh();
            // see the comment above the "hasBeenInitialized" variable
            // for more information
    }

    //#if polish.hasFloatingPoint
    /**
     * Helper function used in converting colors from HSB to RGB.
     * @param v1 v1
     * @param v2 v2 
     * @param vH vH
     * @return the RGB value
     */
    public double _Hue_2_RGB(double v1, double v2, double vH)
    {
        if (vH < 0)
        {
            vH += 1;
        }
        if (vH > 1)
        {
            vH -= 1;
        }
        if ((6 * vH) < 1)
        {
            return (v1 + (v2 - v1) * 6 * vH);
        }
        if ((2 * vH) < 1)
        {
            return (v2);
        }
        if ((3 * vH) < 2)
        {
            return (v1 + (v2 - v1) * ( 2.0 / 3 - vH) * 6);
        }
        return (v1);
    }
    //#else
    public HFloat _Hue_2_RGB(HFloat v1, HFloat v2, HFloat vH)
    {
        if (vH.cmp(0).intValue() < 0 )
        {
            vH = vH.add(1);
        }
        if (vH.cmp(1).intValue() > 0 )        		
        {
            vH = vH.sbt(1);
        }
        if (vH.mlt(6).cmp(1).intValue() < 0)
        {
            return (v1.add(v2.sbt(v1)).mlt(6).mlt(vH));
        }
        if ( vH.mlt(2).cmp(1).intValue() < 0)
        {
            return (v2);
        }
        if ( vH.mlt(3).cmp(2).intValue() < 0 )
        {
            return (v1.add( v2.sbt(v1).mlt( new HFloat(2).div(3).sbt(vH) ).mlt(6) ));
        }
        return (v1);
    }
    //#endif

    /**
     * Draws the a polygon based on the vertexes in the vertex buffer. You can specify the indexes of the first and last vertexes of the polygon.
     * @param startIndex start vertex index
     * @param endIndex end vertex index
     */
    public void _polygon(int startIndex, int endIndex) {
        //// make sure at least 2 vertices
        if (endIndex >= (startIndex + 2)) {
            //// make sure at least 3 vertices for fill
            if (endIndex >= (startIndex + 4)) {
                if (_hasFill) {
                    _bufferg.setColor(_fillColor);

                    int ctr;
                    int size = 1 + ( endIndex - startIndex ) / 2;
                    int pos = 0;
                    int arrX[] = new int[size];
                    int arrY[] = new int[size];
                    for (ctr=startIndex;ctr<=endIndex;ctr=ctr+2)
                    {
                        arrX[pos] = _vertex[ctr];
                        arrY[pos] = _vertex[ctr+1];
                        pos++;
                    }

                    DrawUtil.fillPolygon(arrX, arrY, _fillColor, _bufferg);

                } 
            }

            if (_hasStroke) {
                _bufferg.setColor(_strokeColor);
                for (int i = startIndex + 2; i <= endIndex; i += 2) {
                    line(_vertex[i - 2], _vertex[i - 1], _vertex[i], _vertex[i + 1]);
                }
                line(_vertex[endIndex], _vertex[endIndex + 1], _vertex[startIndex], _vertex[startIndex + 1]);
            }
        }
    }

   
    /**
     * Converts a curve to a series of vertexes and adds said vertexes to the vertex buffer.
     * @param x0
     * @param y0
     * @param x1
     * @param y1
     * @param dx0
     * @param dx1
     * @param dy0
     * @param dy1
     */
    public void _plotCurveVertices(int x0, int y0, int x1, int y1, int dx0, int dx1, int dy0, int dy1) {
        int x, y, t, t2, t3, h0, h1, h2, h3;
        vertex(x0 >> 8, y0 >> 8);
        for (t = 0; t < 256 /* 1.0f */; t += 26 /* 0.1f */) {
            t2 = (t * t) >> 8;
            t3 = (t * t2) >> 8;

            h0 = ((512 /* 2.0f */ * t3) >> 8) - ((768 /* 3.0f */ * t2) >> 8) + 256 /* 1.0f */;
            h1 = ((-512 /* -2.0f */ * t3) >> 8) + ((768 /* 3.0f */ * t2) >> 8);
            h2 = t3 - ((512 /* 2.0f */ * t2) >> 8) + t;
            h3 = t3 - t2;

            x = ((h0 * x0) >> 8) + ((h1 * x1) >> 8) + ((h2 * dx0) >> 8) + ((h3 * dx1) >> 8);
            y = ((h0 * y0) >> 8) + ((h1 * y1) >> 8) + ((h2 * dy0) >> 8) + ((h3 * dy1) >> 8);
            vertex(x >> 8, y >> 8);
        }
        vertex(x1 >> 8, y1 >> 8);
    }

    /**
     * Sets the clip region for the image buffer.
     * @param x the x-coordinate of the clip region
     * @param y the y-coordinate of the clip region
     * @param width clip region width
     * @param height clip region height
     */
    public void _clip(int x, int y, int width, int height) {
        int x2 = x + width;
        int y2 = y + height;
        //// get current clip
        int clipX = _bufferg.getClipX();
        int clipY = _bufferg.getClipY();
        int clipX2 = clipX + _bufferg.getClipWidth();
        int clipY2 = clipY + _bufferg.getClipHeight();
        //// check for intersection
        if (!((x >= clipX2) || (x2 <= clipX) || (y >= clipY2) || (y2 <= clipY))) {
            //// intersect
            int intersectX = Math.max(x, clipX);
            int intersectY = Math.max(y, clipY);
            int intersectWidth = Math.min(x2, clipX2) - intersectX;
            int intersectHeight = Math.min(y2, clipY2) - intersectY;
            _bufferg.setClip(intersectX, intersectY, intersectWidth, intersectHeight);
        }
    }


    /**
     * Converts a keyCode to its corresponding key (as a char - e.g. '9', '*' , '#') and sets the key variable accordingly.
     * @param keyCode the keycode to convert
     */
    public void _key(int keyCode) {
        this.rawKeyCode = keyCode;
        //// MIDP 1.0 says the KEY_ values map to ASCII values, but I've seen it
        //// different on some foreign (i.e. Korean) handsets
        if ((keyCode >= Canvas.KEY_NUM0) && (keyCode <= Canvas.KEY_NUM9)) {
            key = (char) ('0' + (keyCode - Canvas.KEY_NUM0));
            this.keyCode = (int) key;
        } else {
            switch (keyCode) {
                case Canvas.KEY_POUND:
                    key = '#';
                    this.keyCode = (int) key;
                    break;
                case Canvas.KEY_STAR:
                    key = '*';
                    this.keyCode = (int) key;
                    break;
                default:
                    String name = canvas.getKeyName(keyCode);
                    if (name.equals(SOFTKEY1_NAME)) {
                        key = 0xffff;
                        this.keyCode = SOFTKEY1;
                    } else if (name.equals(SOFTKEY2_NAME)) {
                        key = 0xffff;
                        this.keyCode = SOFTKEY2;
                    } else if (name.equals(SEND_NAME)) {
                        key = 0xffff;
                        this.keyCode = SEND;
                    } else {
                        key = 0xffff;
                        this.keyCode = canvas.getGameAction(keyCode);
                        if (this.keyCode == 0) {
                            this.keyCode = keyCode;
                        }
                    }
            }
        }
    }



    /**
     * Processes the given keycode when in multitap mode.
     * @param keyCode the keycode to process
     */
    public final void _multitapKeyPressed(int keyCode) {
        boolean editing = (keyCode == this.keyCode) && ((millis() - _multitapLastEdit) <= _multitapEditDuration);
        char newChar = 0;
        if (editing) {
            newChar = multitapBuffer[multitapBufferIndex - 1];
            if (Character.isUpperCase(newChar)) {
                newChar = Character.toLowerCase(newChar);
            }
        }
        char startChar = 0, endChar = 0, otherChar = 0;
        switch (keyCode) {
            case -8: //// Sun WTK 2.2 emulator
                multitapDeleteChar();
                break;
            case Canvas.KEY_STAR:
                if (_multitapKeySettings[MULTITAP_KEY_SPACE] == '*') {
                    startChar = ' '; endChar = ' '; otherChar = '*';
                } else if (_multitapKeySettings[MULTITAP_KEY_UPPER] == '*') {
                    newChar = _multitapUpperKeyPressed(editing, newChar);
                    editing = (newChar == 0);
                } else {
                    startChar = '*'; endChar = '*'; otherChar = '*';
                    editing = false;
                }
                break;
            case Canvas.KEY_POUND:
                if (_multitapKeySettings[MULTITAP_KEY_SPACE] == '#') {
                    startChar = ' '; endChar = ' '; otherChar = '#';
                } else if (_multitapKeySettings[MULTITAP_KEY_UPPER] == '#') {
                    newChar = _multitapUpperKeyPressed(editing, newChar);
                    editing = (newChar == 0);
                } else {
                    startChar = '#'; endChar = '#'; otherChar = '#';
                    editing = false;
                }
                break;
            case Canvas.KEY_NUM0:
                if (_multitapKeySettings[MULTITAP_KEY_SPACE] == '0') {
                    startChar = ' '; endChar = ' '; otherChar = '0';
                } else if (_multitapKeySettings[MULTITAP_KEY_UPPER] == '0') {
                    newChar = _multitapUpperKeyPressed(editing, newChar);
                    editing = (newChar == 0);
                } else {
                    startChar = '0'; endChar = '0'; otherChar = '0';
                    editing = false;
                }
                break;
            case Canvas.KEY_NUM1:
                int index = 0;
                if (editing) {
                    index = _multitapPunctuation.indexOf(newChar) + 1;
                    if (index == _multitapPunctuation.length()) {
                        index = 0;
                    }
                }
                newChar = _multitapPunctuation.charAt(index);
                break;
            case Canvas.KEY_NUM2:
                startChar = 'a'; endChar = 'c'; otherChar = '2';
                break;
            case Canvas.KEY_NUM3:
                startChar = 'd'; endChar = 'f'; otherChar = '3';
                break;
            case Canvas.KEY_NUM4:
                startChar = 'g'; endChar = 'i'; otherChar = '4';
                break;
            case Canvas.KEY_NUM5:
                startChar = 'j'; endChar = 'l'; otherChar = '5';
                break;
            case Canvas.KEY_NUM6:
                startChar = 'm'; endChar = 'o'; otherChar = '6';
                break;
            case Canvas.KEY_NUM7:
                startChar = 'p'; endChar = 's'; otherChar = '7';
                break;
            case Canvas.KEY_NUM8:
                startChar = 't'; endChar = 'v'; otherChar = '8';
                break;
            case Canvas.KEY_NUM9:
                startChar = 'w'; endChar = 'z'; otherChar = '9';
                break;
            default:
                int action = canvas.getGameAction(keyCode);
                switch (action) {
                    case Canvas.LEFT:
                        _multitapLastEdit = 0;
                        multitapBufferIndex = Math.max(0, multitapBufferIndex - 1);
                        break;
                    case Canvas.RIGHT:
                        _multitapLastEdit = 0;
                        multitapBufferIndex = Math.min(multitapBufferLength, multitapBufferIndex + 1);
                        break;
                }
        }
        if (startChar > 0) {
            if (editing) {
                newChar++;
            } else {
                newChar = startChar;
            }
            if (newChar == (otherChar + 1)) {
                newChar = startChar;
            } else if (newChar > endChar) {
                newChar = otherChar;
            }
        }
        if (newChar > 0) {
            if (_multitapIsUpperCase) {
                newChar = Character.toUpperCase(newChar);
            }
            if (editing) {
                if (multitapBuffer[multitapBufferIndex - 1] != newChar) {
                    multitapBuffer[multitapBufferIndex - 1] = newChar;
                    _multitapLastEdit = millis();
                }
            } else {
                multitapBufferLength++;
                if (multitapBufferLength == multitapBuffer.length) {
                    char[] oldBuffer = multitapBuffer;
                    multitapBuffer = new char[oldBuffer.length * 2];
                    System.arraycopy(oldBuffer, 0, multitapBuffer, 0, multitapBufferIndex);
                    System.arraycopy(oldBuffer, multitapBufferIndex, multitapBuffer, multitapBufferIndex + 1, multitapBufferLength - multitapBufferIndex);
                } else {
                    System.arraycopy(multitapBuffer, multitapBufferIndex, multitapBuffer, multitapBufferIndex + 1, multitapBufferLength - multitapBufferIndex);
                }
                multitapBuffer[multitapBufferIndex] = newChar;
                multitapBufferIndex++;
                _multitapLastEdit = millis();
            }
            multitapText = new String(multitapBuffer, 0, multitapBufferLength);
        }
    }

    /**
     * Checks if the internal Calendar variable is initialized and if not initializes it.
     */
    public void _checkCalendar() {
        if (_calendar == null) {
            _calendar = Calendar.getInstance();
        }
        _calendar.setTime(new Date());
    }
    
    // Implementation of the Mobile Processing specs
    // ---------------------------------------------
    // Context vars
    public int width = 0;
    public int height = 0;

    // Key related variables
    public char      key = 0;
    public int       keyCode = 0;
    public int       rawKeyCode = 0;
    public boolean   keyPressed = false;

    // Multitap related variables
    public char[]    multitapBuffer = null ;
    public String    multitapText = "";
    public int       multitapBufferIndex = 0;
    public int       multitapBufferLength = 0;

    // Pointer related variables
    public int pointerX = 0;
    public int pointerY = 0;

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#redraw()
     */
    public void redraw() {
        ProcessingThread.queueEvent(new ProcessingEvent(this,ProcessingEvent.EVENT_DRAW));
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#loop()
     */
    public void loop() {
        _loop = true;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#noLoop()
     */
    public void noLoop() {
        _loop = false;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#destroy()
     */
    public void destroy() {        
        // Do nothing by default
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#suspend()
     */
    public void suspend() {
        // Do nothing by default
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#resume()
     */
    public void resume() {
        // Do nothing by default
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#setup()
     */
    public void setup() {
        // Do nothing by default
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#exit()
     */
    public void exit() {
        signalDestroy();
        _haltExecution = true;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#colorMode(int) 
     */
    public void colorMode(int mode) {
        _colorMode = mode;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#colorMode(int, int) 
     */
    public void colorMode(int mode, int range) {
        _colorMode = mode;
        _colorRange1 = _colorRange2 = _colorRange3 = range;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#colorMode(int, int, int, int) 
     */
    public void colorMode(int mode, int range1, int range2, int range3) {
        _colorMode = mode;
        _colorRange1 = range1;
        _colorRange2 = range2;
        _colorRange3 = range3;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#color(int)
     */
    public color color(int gray) {
        int col = (255 << 24) | (gray << 16) | (gray << 8) | (gray);
        return new color(col);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#color(int, int)
     */
    public color color(int gray, int alpha) {
        int col = (alpha << 24) | (gray << 16) | (gray << 8) | (gray);
        return new color(col);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#colorMode(int, int, int, int) 
     */
    public color color(int value1, int value2, int value3, int alpha) {
        if (_colorMode == RGB)
        {
            // Normalize the color values according to the parameters set by
            // colorMode();
            value1 = value1 * 255 / _colorRange1;
            value2 = value2 * 255 / _colorRange2;
            value3 = value3 * 255 / _colorRange3;

            int col = (alpha << 24) | (value1 << 16) | (value2 << 8) | (value3);
            return new color(col);
        }
        else if (_colorMode == HSB)
        {
        	
        	//#if polish.hasFloatingPoint
	            double R, G, B;
	            int Ri, Gi, Bi;
	            double temp2, temp1;
	            double H = ((double) value1) / _colorRange1;
	            double S = ((double) value2) / _colorRange2;
	            double L = ((double) value3) / _colorRange3;
	
	            if (S == 0) //HSL from 0 to 1
	            {
	                R = L * 255;
	                G = L * 255;
	                B = L * 255;
	            }
	            else
	            {
	                if (L < 0.5)
	                {
	                    temp2 = L * (1 + S);
	                } else
	                {
	                    temp2 = (L + S) - (S * L);
	                }
	
	                temp1 = 2 * L - temp2;
	
	                R = 255 * _Hue_2_RGB(temp1, temp2, H + ( 1.0 / 3));
	                G = 255 * _Hue_2_RGB(temp1, temp2, H);
	                B = 255 * _Hue_2_RGB(temp1, temp2, H - ( 1.0 / 3)); 
	
	            }
	
	            Ri = (int) R;
	            Gi = (int) G;
	            Bi = (int) B;
            //#else
				//#= HFloat R, G, B;
				//#= int Ri, Gi, Bi;
				//#= HFloat temp2, temp1;
				//#= HFloat H = new HFloat(value1).div(_colorRange1);
				//#= HFloat S = new HFloat(value2).div(_colorRange2);
				//#= HFloat L = new HFloat(value3).div(_colorRange3);
				//#= 
				//#= if (S.equals(new HFloat(0))) //HSL from 0 to 1
				//#= {
				//#= R = L.mlt(255);
				//#= G = L.mlt(255);
				//#= B = L.mlt(255);
				//#= }
				//#= else
				//#= {
				//#= 	if (L.cmp(new HFloat(1).div(2)).intValue() < 0 )
				//#= 	{
				//#= 		temp2 = L.mlt (S.add(1));
				//#= 	} else
				//#= 	{
				//#=		temp2 = L.add(S).sbt(S.mlt(L));
				//#=	}
				//#= 
				//#=	temp1 = L.mlt(2).sbt(temp2);
				//#= 
				//#=	R = _Hue_2_RGB(temp1, temp2, H.add( new HFloat(1).div(3))).mlt(255);
				//#=	G = _Hue_2_RGB(temp1, temp2, H).mlt(255);
				//#=	B = _Hue_2_RGB(temp1, temp2, H.sbt( new HFloat(1).div(3))).mlt(255);
				//#= }
				//#=
				//#= Ri = R.toInteger().intValue();
				//#= Gi = G.toInteger().intValue();
				//#= Bi = B.toInteger().intValue();
            //#endif

            int col = (alpha << 24) | (Ri << 16) | (Gi << 8) | (Bi);
            return new color(col);
        }
        else
        {
            return new color(0xFFFFFF);
        }
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#background(int) 
     */
    public void background(int gray) {
        _bgImageMode = false;
        color x = color(gray);
        _bgColor = x.color ;
        _repaintBackground = true ;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#background(de.enough.polish.processing.color) 
     */
    public void background(color color)
    {
        _bgImageMode = false;
        _bgColor = color.color ;
        _repaintBackground = true ;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#background(int, int, int) 
     */
    public void background(int value1, int value2, int value3)
    {
        _bgImageMode = false;
        color x = color (value1, value2, value3,255);
        _bgColor = x.color ;
        _repaintBackground = true ;
        
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#background(de.enough.polish.processing.PImage) 
     */
    public void background(PImage image)
    {
        _bgImageMode = true;
        _bgImage = image;
        _repaintBackground = true ;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#strokeWeight(int) 
     */
    public void strokeWeight(int width) {
        _strokeWidth = width;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#stroke(de.enough.polish.processing.color) 
     */
    public void stroke(color whatColor) {
        _hasStroke = true;
        _strokeColor = whatColor.color;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#stroke(int) 
     */
    public void stroke(int color) {
        color temp = color(color);
        _strokeColor = temp.color ;
        _hasStroke = true;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#stroke(int, int, int) 
     */
    public void stroke(int v1, int v2, int v3) {
        color temp = color(v1,v2,v3,255);
        _strokeColor = temp.color ;
        _hasStroke = true;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#noStroke()
     */
    public void noStroke() {
        _hasStroke = false;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#fill(int)
     */
    public void fill(int gray)
    {
        _hasFill = true;
        color x = color(gray);
        _fillColor = x.color ;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#fill(de.enough.polish.processing.color) 
     */
    public void fill(color color)
    {
        _hasFill = true;
        _fillColor = color.color;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#fill(int, int, int) 
     */
    public void fill(int value1, int value2, int value3)
    {
        _hasFill = true;
        color x = color(value1,value2,value3,255);
        _fillColor = x.color ;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#noFill()
     */
    public void noFill()
    {
        _hasFill = false;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#framerate(int)
     */
    public void framerate(int framerate) {
        _timeBetweenFrames = 1000 / framerate;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#draw()
     */
    public void draw() 
    {
        // Does nothing by default
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#line(int, int, int, int) 
     */
    public void line(int x1, int y1, int x2, int y2) {


        if (_hasStroke == false )
        {
            return;
        }
        
        _bufferg.setColor(_strokeColor);

        if ( _strokeWidth == 1)
        {
            _bufferg.drawLine(x1, y1, x2, y2);
            return;
        }

        if ( _fastDrawingEnabled == true )
        {
        	//#if polish.hasFloatingPoint			
	            // Calculate line width and other stuff
	            int strokeWidthDiv2 = _strokeWidth / 2 ;
	            double pointDistance = Math.sqrt ( (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2) ) ;
	            int startWidth = _strokeWidth / 2;
	            int remainingWidth = _strokeWidth - startWidth ;
	            double lineCos = -(x1-x2) / pointDistance;
	            double lineSin = - ( y1-y2 ) / pointDistance ;
	            double cosPerpendicularLine =  - lineSin ;
	            double sinPerpendicularLine = lineCos ;
	
	            // Calculate the polygon defining the line
	            __arrX[0] = (int)( x1 + cosPerpendicularLine * startWidth );
	            __arrX[1] = (int)( x1 - cosPerpendicularLine * remainingWidth );
	            __arrX[2] = (int)( x2 - cosPerpendicularLine * remainingWidth );
	            __arrX[3] = (int)( x2 + cosPerpendicularLine * startWidth );
	            __arrY[0] = (int)( y1 + sinPerpendicularLine * startWidth );
	            __arrY[1] = (int)( y1 - sinPerpendicularLine * remainingWidth );
	            __arrY[2] = (int)( y2 - sinPerpendicularLine * remainingWidth );
	            __arrY[3] = (int)( y2 + sinPerpendicularLine * startWidth );
            //#else
				//#= HFloat temp;
				//#= int strokeWidthDiv2 = _strokeWidth / 2 ;
				//#= temp = new HFloat((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));	
				//#= HFloat pointDistance =temp.sqrt();
				//#= int startWidth = _strokeWidth / 2;
				//#= int remainingWidth = _strokeWidth - startWidth ;
				//#= HFloat lineCos = new HFloat(-(x1-x2)).div(pointDistance);
				//#= HFloat lineSin = new HFloat(- ( y1-y2 )).div(pointDistance);
				//#= HFloat cosPerpendicularLine =new HFloat (lineSin.mlt(-1));
				//#= HFloat sinPerpendicularLine = lineCos ;
				//#= 
				//#= 	// Calculate the polygon defining the line
				//#= __arrX[0] = cosPerpendicularLine.mlt(startWidth).add(x1).toInteger().intValue();
				//#= __arrX[1] = cosPerpendicularLine.mlt(-remainingWidth).add(x1).toInteger().intValue();
				//#= __arrX[2] = cosPerpendicularLine.mlt(-remainingWidth).add(x2).toInteger().intValue();
				//#= __arrX[3] = cosPerpendicularLine.mlt(startWidth).add(x2).toInteger().intValue();
				//#= __arrY[0] = sinPerpendicularLine.mlt(startWidth).add(y1).toInteger().intValue();
				//#= __arrY[1] = sinPerpendicularLine.mlt(-remainingWidth).add(y1).toInteger().intValue();
				//#= __arrY[2] = sinPerpendicularLine.mlt(-remainingWidth).add(y2).toInteger().intValue();
				//#= __arrY[3] = sinPerpendicularLine.mlt(startWidth).add(y2).toInteger().intValue();
            //#endif

            // Draw the polygon
            DrawUtil.fillPolygon(__arrX, __arrY, _strokeColor, _bufferg);

           // Draw the rounded entpoints of the line
            _bufferg.fillArc(x1 - strokeWidthDiv2, y1-strokeWidthDiv2 , _strokeWidth, _strokeWidth, 0, 360);
            _bufferg.fillArc(x2 - strokeWidthDiv2, y2-strokeWidthDiv2 , _strokeWidth, _strokeWidth, 0, 360);

        }
        else
        {
            _bufferg.setColor(_strokeColor);
            _bufferg.drawLine(x1, y1, x2, y2);
            if (_strokeWidth > 1) {
                boolean steep = Math.abs(y2 - y1) > Math.abs(x2 - x1);
                if (steep) {
                    int swap = x1;
                    x1 = y1;
                    y1 = swap;
                    swap = x2;
                    x2 = y2;
                    y2 = swap;
                }
                if (x1 > x2) {
                    int swap = x1;
                    x1 = x2;
                    x2 = swap;
                    swap = y1;
                    y1 = y2;
                    y2 = swap;
                }
                int dx = x2 - x1;
                int dy = (y2 > y1) ? y2 - y1 : y1 - y2;
                int error = 0;
                int halfWidth = _strokeWidth >> 1;
                int y = y1 - halfWidth;
                int ystep;
                if (y1 < y2) {
                    ystep = 1;
                } else {
                    ystep = -1;
                }
                for (int x = x1 - halfWidth, endx = x2 - halfWidth; x <= endx; x++) {
                    if (steep) {
                        _bufferg.fillArc(y, x, _strokeWidth, _strokeWidth, 0, 360);
                    } else {
                        _bufferg.fillArc(x, y, _strokeWidth, _strokeWidth, 0, 360);
                    }
                    error += dy;
                    if ((2 * error) >= dx) {
                        y += ystep;
                        error -= dx;
                    }
                }
                }
            }
        
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#point(int, int) 
     */
    public void point(int x1, int y1) {
        if (_hasStroke) {
            _bufferg.setColor(_strokeColor);
            _bufferg.drawLine(x1, y1, x1, y1);
        }
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#triangle(int, int, int, int, int, int) 
     */
    public void triangle(int x1, int y1, int x2, int y2, int x3, int y3) {
        int prevShapeMode = _shapeMode ;
        _shapeMode = POLYGON;
        _vertex[0] = x1;
        _vertex[1] = y1;
        _vertex[2] = x2;
        _vertex[3] = y2;
        _vertex[4] = x3;
        _vertex[5] = y3;
        _vertexIndex = 6;
        endShape();
        _shapeMode = prevShapeMode ;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#quad(int, int, int, int, int, int, int, int) 
     */
    public void quad(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
        int prevShapeMode = _shapeMode ;
        _shapeMode = POLYGON;
        _vertex[0] = x1;
        _vertex[1] = y1;
        _vertex[2] = x2;
        _vertex[3] = y2;
        _vertex[4] = x3;
        _vertex[5] = y3;
        _vertex[6] = x4;
        _vertex[7] = y4;
        _vertexIndex = 8;
        endShape();
        _shapeMode = prevShapeMode ;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#rect(int, int, int, int) 
     */
     public void rect(int x, int y, int width, int height) {
        int temp;
        switch (_rectMode) {
            case CORNERS:
                temp = x;
                x = Math.min(x, width);
                width = Math.abs(x - temp);
                temp = y;
                y = Math.min(y, height);
                height = Math.abs(y - temp);
                break;
            case CENTER:
                x -= width / 2;
                y -= height / 2;
                break;
        }
        if (_hasFill) {
            _bufferg.setColor(_fillColor);
            _bufferg.fillRect(x, y, width, height);
        }
        if (_hasStroke) {
            _bufferg.setColor(_strokeColor);
            _bufferg.drawRect(x, y, width, height);
        }
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#rectMode(int) 
     */
     public void rectMode(int mode) {
        if ((mode >= CENTER) && (mode <= CORNER)) {
            _rectMode = mode;
        }
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#ellipse(int, int, int, int) 
     */
     public void ellipse(int x, int y, int width, int height) {
        int temp;
        switch (_ellipseMode) {
            case CORNERS:
                temp = x;
                x = Math.min(x, width);
                width = Math.abs(x - temp);
                temp = y;
                y = Math.min(y, height);
                height = Math.abs(y - temp);
                break;
            case CENTER:
                x -= width / 2;
                y -= height / 2;
                break;
            case CENTER_RADIUS:
                x -= width;
                y -= height;
                width *= 2;
                height *= 2;
                break;
        }
        if (_hasFill) {
            _bufferg.setColor(_fillColor);
            _bufferg.fillArc(x, y, width, height, 0, 360);
        }
        if (_hasStroke) {
            _bufferg.setColor(_strokeColor);
            _bufferg.drawArc(x, y, width, height, 0, 360);
        }
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#ellipseMode(int) 
     */
    public void ellipseMode(int mode) {
        if ((mode >= CENTER) && (mode <= CORNERS)) {
            _ellipseMode = mode;
        }
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#_curveVertexIndexint, int, int, int, int, int, int, int)
     */
    public void curve(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
        int prevShapeMode = _shapeMode ;
        beginShape(LINE_STRIP);
        curveVertex(x1, y1);
        curveVertex(x1, y1);
        curveVertex(x2, y2);
        curveVertex(x3, y3);
        curveVertex(x4, y4);
        curveVertex(x4, y4);
        endShape();
        _shapeMode = prevShapeMode ;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#bezier(int, int, int, int, int, int, int, int) 
     */
    public void bezier(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
        int prevShapeMode = _shapeMode ;
        beginShape(LINE_STRIP);
        vertex(x1, y1);
        bezierVertex(x2, y2, x3, y3, x4, y4);
        endShape();
        _shapeMode = prevShapeMode;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#curveVertex(int, int) 
     */
     public void curveVertex(int x, int y) {
        //// use fixed point, 8-bit precision
        _curveVertex[_curveVertexIndex] = x << 8;
        _curveVertexIndex++;
        _curveVertex[_curveVertexIndex] = y << 8;
        _curveVertexIndex++;

        if (_curveVertexIndex == 8) {
            int tension = 128 /* 0.5f */;

            int dx0 = ((_curveVertex[4] - _curveVertex[0]) * tension) >> 8;
            int dx1 = ((_curveVertex[6] - _curveVertex[2]) * tension) >> 8;
            int dy0 = ((_curveVertex[5] - _curveVertex[1]) * tension) >> 8;
            int dy1 = ((_curveVertex[7] - _curveVertex[3]) * tension) >> 8;

            _plotCurveVertices(_curveVertex[2], _curveVertex[3],
                              _curveVertex[4], _curveVertex[5],
                              dx0, dx1, dy0, dy1);

            for (int i = 0; i < 6; i++) {
                _curveVertex[i] = _curveVertex[i + 2];
            }
            _curveVertexIndex = 6;
        }
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#loop()
     */
    public void bezierVertex(int x1, int y1, int x2, int y2, int x3, int y3) {

        //// plotCurveVertices will add x0, y0 back
        _vertexIndex -= 2;
        //// use fixed point, 8-bit precision
        int x0 = _vertex[_vertexIndex] << 8;
        int y0 = _vertex[_vertexIndex + 1] << 8;
        //// convert parameters to fixed point
        x1 = x1 << 8;
        y1 = y1 << 8;
        x2 = x2 << 8;
        y2 = y2 << 8;
        x3 = x3 << 8;
        y3 = y3 << 8;
        //// use fixed point, 8-bit precision
        int tension = 768 /* 3.0f */;

        int dx0 = ((x1 - x0) * tension) >> 8;
        int dx1 = ((x3 - x2) * tension) >> 8;
        int dy0 = ((y1 - y0) * tension) >> 8;
        int dy1 = ((y3 - y2) * tension) >> 8;

        _plotCurveVertices(x0, y0,
                          x3, y3,
                          dx0, dx1, dy0, dy1);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#vertex(int, int)
     */
    public void vertex(int x, int y) {
        _vertex[_vertexIndex] = x;
        _vertexIndex++;
        _vertex[_vertexIndex] = y;
        _vertexIndex++;

        int length = _vertex.length;
        if (_vertexIndex == length) {
            int[] old = _vertex;
            _vertex = new int[length * 2];
            System.arraycopy(old, 0, _vertex, 0, length);
        }
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#beginShape(int) 
     */
    public void beginShape(int mode) {
        if ((mode >= POINTS) && (mode <= POLYGON)) {
            _shapeMode = mode;
        } else {
            _shapeMode = POINTS;
        }
        _vertexIndex = 0;
        _curveVertexIndex = 0;
    }


    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#endShape() 
     */
    public void endShape() {
        int i;
        int step;
        switch (_shapeMode) {
            case POINTS:
                i = 0;
                step = 2;
                break;
            case LINES:
                i = 2;
                step = 4;
                break;
            case LINE_STRIP:
            case LINE_LOOP:
                i = 2;
                step = 2;
                break;
            case TRIANGLES:
                i = 4;
                step = 6;
                break;
            case TRIANGLE_STRIP:
                i = 4;
                step = 2;
                break;
            case QUADS:
                i = 6;
                step = 8;
                break;
            case QUAD_STRIP:
                i = 6;
                step = 4;
                break;
            case POLYGON:
                _polygon(0, _vertexIndex - 2);
                return;
            default:
                return;
        }

        for (; i < _vertexIndex; i += step) {
            switch (_shapeMode) {
                case POINTS:
                    point(_vertex[i], _vertex[i + 1]);
                    break;
                case LINES:
                case LINE_STRIP:
                case LINE_LOOP:
                    line(_vertex[i - 2], _vertex[i - 1], _vertex[i], _vertex[i + 1]);
                    break;
                case TRIANGLES:
                case TRIANGLE_STRIP:
                    _polygon(i - 4, i);
                    break;
                case QUADS:
                case QUAD_STRIP:
                    _polygon(i - 6, i);
                    break;
            }
        }
        //// handle loop closing
        if (_shapeMode == LINE_LOOP) {
            if (_vertexIndex >= 2) {
                line(_vertex[_vertexIndex - 2], _vertex[_vertexIndex - 1], _vertex[0], _vertex[1]);
            }
        }

        _vertexIndex = 0;
    }


    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#loadImage(java.lang.String) 
     */
    public PImage loadImage(String filename) {
        try {
            Image img = Image.createImage("/" + filename);
            return new PImage(img);
        } catch(Exception e) {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#loadImage(byte[]) 
     */
    public PImage loadImage(byte[] data)
    {
        return new PImage(data);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#image(de.enough.polish.processing.PImage, int, int) 
     */
    public void image(PImage img, int x, int y)
    {
        _bufferg.drawImage(img.getImage(), x, y, Graphics.TOP | Graphics.LEFT);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#image(de.enough.polish.processing.PImage, int, int, int, int, int, int) 
     */
   public void image(PImage img, int sx, int sy, int swidth, int sheight, int dx, int dy) {
        if (_imageMode == CORNERS) {
            swidth = swidth - sx;
            sheight = sheight - sy;
        }
        _clip(dx, dy, swidth, sheight);
        _bufferg.drawImage(img.getImage(), dx - sx, dy - sy, Graphics.TOP | Graphics.LEFT);
        _clip (0,0,width,height);
   }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#imageMode(int) 
     */
   public void imageMode (int mode)
   {
       _imageMode = mode ;
   }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#multitap() 
     */
   public final void multitap() {
        _multitap = true;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#noMultitap() 
     */
    public final void noMultitap() {
        _multitap = false;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#multitapClear()
     */
    public final void multitapClear() {
        multitapBufferIndex = 0;
        multitapBufferLength = 0;
        multitapText = "";
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#softkeyPressed(java.lang.String) 
     */
    public void softkeyPressed(String label)
    {
        // Do nothing by default
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#softkey(java.lang.String) 
     */
    public void softkey(String label)
    {
        _softkeyLabel = label ;
        if ( _parent != null )
        {
            _parent.setSoftkey(label);
        }
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#multitapDeleteChar() 
     */
    public final void multitapDeleteChar() {
        if (multitapBufferIndex > 0) {
            System.arraycopy(multitapBuffer, multitapBufferIndex, multitapBuffer, multitapBufferIndex - 1, multitapBufferLength - multitapBufferIndex);
            multitapBufferLength--;
            multitapBufferIndex--;
            _multitapLastEdit = 0;
        }
        multitapText = new String(multitapBuffer, 0, multitapBufferLength);
    }

    /**
     * Called when an uppercase key is pressed in multitap mode
     * @param editing mode state (enabled/disabled)
     * @param newChar the character pressed
     * @return the mapped character
     */
    public char _multitapUpperKeyPressed(boolean editing, char newChar) {
        _multitapIsUpperCase = !_multitapIsUpperCase;
        if (editing) {
            if (newChar == _multitapKeySettings[MULTITAP_KEY_UPPER]) {
                //// delete the char
                multitapDeleteChar();
                _multitapLastEdit = millis();
                newChar = 0;
            } else {
                newChar = _multitapKeySettings[MULTITAP_KEY_UPPER];
            }
        } else {
            _multitapLastEdit = millis();
        }
        return newChar;
    }


    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#keyPressed()
     */
    public void keyPressed() {

        // Do nothing by default
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#keyReleased()
     */
    public void keyReleased() {

        // Do nothing by default
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#pointerDragged() 
     */
    public void pointerDragged()
    {
        // Do nothing by default
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#pointerPressed()
     */
    public void pointerPressed()
    {
        // Do nothing by default
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#pointerReleased()
     */
    public void pointerReleased()
    {
        // Do nothing by default
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#millis()
     */
    public final int millis() {
        return (int) (System.currentTimeMillis() - _startTime);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#second()
     */
    public final int second() {
        _checkCalendar();
        return _calendar.get(Calendar.SECOND);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#minute()
     */
    public final int minute() {
        _checkCalendar();
        return _calendar.get(Calendar.MINUTE);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#hour()
     */
    public final int hour() {
        _checkCalendar();
        return _calendar.get(Calendar.HOUR_OF_DAY);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#day()
     */
    public final int day() {
        _checkCalendar();
        return _calendar.get(Calendar.DAY_OF_MONTH);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#month()
     */
    public final int month() {
        _checkCalendar();
        return _calendar.get(Calendar.MONTH);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#year()
     */
    public final int year() {
        _checkCalendar();
        return _calendar.get(Calendar.YEAR);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#currentMemory()
     */
    public final int currentMemory() {
        return (int) _runtime.freeMemory();
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#reportedMemory()
     */
    public final int reportedMemory() {
        return (int) _runtime.totalMemory();
    }

    /**
     * This method is called when the processing context (or processing context)
     * container receives focus
     * @see de.enough.polish.processing.ProcessingInterface#focus() 
     */
    public void focus() {
        // Do nothig by default
    }

    /**
     * This method is called when the processing context (or processing context)
     * container looses focus
     * @see de.enough.polish.processing.ProcessingInterface#lostFocus() 
     */
    public void lostFocus() {
        // Do nothing by default
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#textInput() 
     */
    public String textInput() {
        return textInput ("", "", Integer.MAX_VALUE);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#textInput(java.lang.String, java.lang.String, int) 
     */
    public String textInput(String title, String text, int max) {

        Displayable current = Display.getInstance().getCurrent() ;
        ProcessingTextInputForm form = new ProcessingTextInputForm(title, text, max);
        Thread t = new Thread(form);
        t.start();
        
        String result = null;
        synchronized ( form.getExternalLockObject() )
        {
            try
            {
                form.getExternalLockObject().wait();
            }
            catch (Exception ex)
            {
                //
            }

            result = form.getText() ;
        }

        Display.getInstance().setCurrent(current);
        return result;

    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#print(boolean)
     */
     public void print(boolean data) {
        System.out.print(String.valueOf(data));
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#print(byte)
     */
    public void print(byte data) {
        System.out.print(String.valueOf(data));
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#print(char)
     */
    public void print(char data) {
        System.out.print(String.valueOf(data));
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#print(int)
     */
    public void print(int data) {
        System.out.print(String.valueOf(data));
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#print(java.lang.Object) 
     */
    public void print(Object data) {
        System.out.print(String.valueOf(data));
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#print(java.lang.String) 
     */
    public void print(String data) {
        System.out.print(data);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#println(boolean)
     */
    public void println(boolean data) {
        System.out.println(String.valueOf(data));
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#println(byte)
     */
    public void println(byte data) {
        System.out.println(String.valueOf(data));
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#println(char) 
     */
    public void println(char data) {
        System.out.println(String.valueOf(data));
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#println(int) 
     */
    public void println(int data) {
        System.out.println(String.valueOf(data));
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#println(java.lang.Object) 
     */
    public void println(Object data) {
        System.out.println(String.valueOf(data));
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#print(java.lang.String) 
     */
    public void println(String data) {
        System.out.println(data);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#length(boolean[]) 
     */
    public int length(boolean[] array) {
        return array.length;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#length(byte[]) 
     */
    public int length(byte[] array) {
        return array.length;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#length(char[]) 
     */
    public int length(char[] array) {
        return array.length;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#length(int[]) 
     */
    public int length(int[] array) {
        return array.length;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#length(java.lang.Object[]) 
     */
    public int length(Object[] array) {
        return array.length;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#join(java.lang.String[], java.lang.String) 
     */
    public String join(String[] anyArray, String separator) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0, length = anyArray.length; i < length; i++) {
            buffer.append(anyArray[i]);
            if (i < (length - 1)) {
                buffer.append(separator);
            }
        }
        return buffer.toString();
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#join(int[], java.lang.String) 
     */
    public String join(int[] anyArray, String separator) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0, length = anyArray.length; i < length; i++) {
            buffer.append(anyArray[i]);
            if (i < (length - 1)) {
                buffer.append(separator);
            }
        }
        return buffer.toString();
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#join(int[], java.lang.String, int) 
     */
    public String join(int[] intArray, String separator, int digits) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0, length = intArray.length; i < length; i++) {
            buffer.append(nf(intArray[i], digits));
            if (i < (length - 1)) {
                buffer.append(separator);
            }
        }
        return buffer.toString();
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#nf(int, int) 
     */
    public String nf(int intValue, int digits) {
        StringBuffer buffer = new StringBuffer();
        for (int j = Integer.toString(intValue).length(); j < digits; j++) {
            buffer.append("0");
        }
        buffer.append(intValue);
        return buffer.toString();
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#nfp(int, int)
     */
    public String nfp(int intValue, int digits) {
        StringBuffer buffer = new StringBuffer();
        if (intValue < 0) {
            buffer.append("-");
        } else {
            buffer.append("+");
        }
        buffer.append(nf(intValue, digits));
        return buffer.toString();
    }
   

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#split(java.lang.String, char) 
     */
    public String[] split(String str, char delim) {
        return split(str, new String(new char[] { delim }));
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#split(java.lang.String, java.lang.String) 
     */
    public String[] split(String str, String delim) {
        Vector v = new Vector();
        int prevIndex = 0;
        int nextIndex = str.indexOf(delim, prevIndex);
        int delimLength = delim.length();
        while (nextIndex >= 0) {
            v.addElement(str.substring(prevIndex, nextIndex));
            prevIndex = nextIndex + delimLength;
            nextIndex = str.indexOf(delim, prevIndex);
        }
        if (prevIndex < str.length()) {
            v.addElement(str.substring(prevIndex));
        }

        String[] tokens = new String[v.size()];
        v.copyInto(tokens);

        return tokens;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#trim(java.lang.String) 
     */
    public String trim(String str) {
        //// deal with unicode nbsp later
        return str.trim();
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#append(java.lang.String[], java.lang.String) 
     */
    public String[] append(String[] array, String element) {
        String[] old = array;
        int length = old.length;
        array = new String[length + 1];
        System.arraycopy(old, 0, array, 0, length);
        array[length] = element;
        return array;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#append(boolean[], boolean) 
     */
    public boolean[] append(boolean[] array, boolean element) {
        boolean[] old = array;
        int length = old.length;
        array = new boolean[length + 1];
        System.arraycopy(old, 0, array, 0, length);
        array[length] = element;
        return array;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#append(byte[], byte) 
     */
    public byte[] append(byte[] array, byte element) {
        byte[] old = array;
        int length = old.length;
        array = new byte[length + 1];
        System.arraycopy(old, 0, array, 0, length);
        array[length] = element;
        return array;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#append(char[], char) 
     */
    public char[] append(char[] array, char element) {
        char[] old = array;
        int length = old.length;
        array = new char[length + 1];
        System.arraycopy(old, 0, array, 0, length);
        array[length] = element;
        return array;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#append(int[], int) 
     */
    public int[] append(int[] array, int element) {
        int[] old = array;
        int length = old.length;
        array = new int[length + 1];
        System.arraycopy(old, 0, array, 0, length);
        array[length] = element;
        return array;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#concat(java.lang.String[], java.lang.String[]) 
     */
    public String[] concat(String[] array1, String[] array2) {
        int length1 = array1.length;
        int length2 = array2.length;
        String[] array = new String[length1 + length2];
        System.arraycopy(array1, 0, array, 0, length1);
        System.arraycopy(array2, 0, array, length1, length2);
        return array;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#concat(boolean[], boolean[]) 
     */
    public boolean[] concat(boolean[] array1, boolean[] array2) {
        int length1 = array1.length;
        int length2 = array2.length;
        boolean[] array = new boolean[length1 + length2];
        System.arraycopy(array1, 0, array, 0, length1);
        System.arraycopy(array2, 0, array, length1, length2);
        return array;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#concat(byte[], byte[]) 
     */
    public byte[] concat(byte[] array1, byte[] array2) {
        int length1 = array1.length;
        int length2 = array2.length;
        byte[] array = new byte[length1 + length2];
        System.arraycopy(array1, 0, array, 0, length1);
        System.arraycopy(array2, 0, array, length1, length2);
        return array;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#concat(char[], char[]) 
     */
    public char[] concat(char[] array1, char[] array2) {
        int length1 = array1.length;
        int length2 = array2.length;
        char[] array = new char[length1 + length2];
        System.arraycopy(array1, 0, array, 0, length1);
        System.arraycopy(array2, 0, array, length1, length2);
        return array;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#concat(int[], int[]) 
     */
    public int[] concat(int[] array1, int[] array2) {
        int length1 = array1.length;
        int length2 = array2.length;
        int[] array = new int[length1 + length2];
        System.arraycopy(array1, 0, array, 0, length1);
        System.arraycopy(array2, 0, array, length1, length2);
        return array;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#contract(boolean[], int) 
     */
    public boolean[] contract(boolean[] array, int newSize) {
        int length = array.length;
        if (length > newSize) {
            boolean[] old = array;
            array = new boolean[newSize];
            System.arraycopy(old, 0, array, 0, newSize);
        }
        return array;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#contract(byte[], int) 
     */
    public byte[] contract(byte[] array, int newSize) {
        int length = array.length;
        if (length > newSize) {
            byte[] old = array;
            array = new byte[newSize];
            System.arraycopy(old, 0, array, 0, newSize);
        }
        return array;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#contract(char[], int) 
     */
    public char[] contract(char[] array, int newSize) {
        int length = array.length;
        if (length > newSize) {
            char[] old = array;
            array = new char[newSize];
            System.arraycopy(old, 0, array, 0, newSize);
        }
        return array;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#contract(int[], int) 
     */
    public int[] contract(int[] array, int newSize) {
        int length = array.length;
        if (length > newSize) {
            int[] old = array;
            array = new int[newSize];
            System.arraycopy(old, 0, array, 0, newSize);
        }
        return array;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#contract(java.lang.String[], int) 
     */
    public String[] contract(String[] array, int newSize) {
        int length = array.length;
        if (length > newSize) {
            String[] old = array;
            array = new String[newSize];
            System.arraycopy(old, 0, array, 0, newSize);
        }
        return array;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#expand(boolean[]) 
     */
    public boolean[] expand(boolean[] array) {
        return expand(array, array.length * 2);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#expand(boolean[], int) 
     */
    public boolean[] expand(boolean[] array, int newSize) {
        int length = array.length;
        if (length < newSize) {
            boolean[] old = array;
            array = new boolean[newSize];
            System.arraycopy(old, 0, array, 0, length);
        }
        return array;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#expand(byte[]) 
     */
    public byte[] expand(byte[] array) {
        return expand(array, array.length * 2);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#expand(byte[], int) 
     */
    public byte[] expand(byte[] array, int newSize) {
        int length = array.length;
        if (length < newSize) {
            byte[] old = array;
            array = new byte[newSize];
            System.arraycopy(old, 0, array, 0, length);
        }
        return array;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#expand(char[]) 
     */
    public char[] expand(char[] array) {
        return expand(array, array.length * 2);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#expand(char[], int) 
     */
    public char[] expand(char[] array, int newSize) {
        int length = array.length;
        if (length < newSize) {
            char[] old = array;
            array = new char[newSize];
            System.arraycopy(old, 0, array, 0, length);
        }
        return array;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#expand(int[]) 
     */
    public int[] expand(int[] array) {
        return expand(array, array.length * 2);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#expand(int[], int) 
     */
    public int[] expand(int[] array, int newSize) {
        int length = array.length;
        if (length < newSize) {
            int[] old = array;
            array = new int[newSize];
            System.arraycopy(old, 0, array, 0, length);
        }
        return array;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#expand(java.lang.String[]) 
     */
    public String[] expand(String[] array) {
        return expand(array, array.length * 2);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#expand(java.lang.String[], int) 
     */
    public String[] expand(String[] array, int newSize) {
        int length = array.length;
        if (length < newSize) {
            String[] old = array;
            array = new String[newSize];
            System.arraycopy(old, 0, array, 0, length);
        }
        return array;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#reverse(boolean[]) 
     */
    public boolean[] reverse(boolean[] array) {
        int length = array.length;
        boolean[] reversed = new boolean[length];
        for (int i = length - 1; i >= 0; i--) {
            reversed[i] = array[length - i - 1];
        }
        return reversed;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#reverse(byte[]) 
     */
    public byte[] reverse(byte[] array) {
        int length = array.length;
        byte[] reversed = new byte[length];
        for (int i = length - 1; i >= 0; i--) {
            reversed[i] = array[length - i - 1];
        }
        return reversed;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#reverse(char[]) 
     */
    public char[] reverse(char[] array) {
        int length = array.length;
        char[] reversed = new char[length];
        for (int i = length - 1; i >= 0; i--) {
            reversed[i] = array[length - i - 1];
        }
        return reversed;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#reverse(int[]) 
     */
    public int[] reverse(int[] array) {
        int length = array.length;
        int[] reversed = new int[length];
        for (int i = length - 1; i >= 0; i--) {
            reversed[i] = array[length - i - 1];
        }
        return reversed;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#reverse(java.lang.String[]) 
     */
    public String[] reverse(String[] array) {
        int length = array.length;
        String[] reversed = new String[length];
        for (int i = length - 1; i >= 0; i--) {
            reversed[i] = array[length - i - 1];
        }
        return reversed;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#shorten(boolean[]) 
     */
    public boolean[] shorten(boolean[] array) {
        boolean[] old = array;
        int length = old.length - 1;
        array = new boolean[length];
        System.arraycopy(old, 0, array, 0, length);
        return array;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#shorten(byte[]) 
     */
    public byte[] shorten(byte[] array) {
        byte[] old = array;
        int length = old.length - 1;
        array = new byte[length];
        System.arraycopy(old, 0, array, 0, length);
        return array;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#shorten(char[])
     */
    public char[] shorten(char[] array) {
        char[] old = array;
        int length = old.length - 1;
        array = new char[length];
        System.arraycopy(old, 0, array, 0, length);
        return array;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#shorten(int[]) 
     */
    public int[] shorten(int[] array) {
        int[] old = array;
        int length = old.length - 1;
        array = new int[length];
        System.arraycopy(old, 0, array, 0, length);
        return array;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#shorten(java.lang.String[]) 
     */
    public String[] shorten(String[] array) {
        String[] old = array;
        int length = old.length - 1;
        array = new String[length];
        System.arraycopy(old, 0, array, 0, length);
        return array;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#subset(boolean[], int) 
     */
    public boolean[] subset(boolean[] array, int offset) {
        return subset(array, offset, array.length - offset);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#subset(boolean[], int, int) 
     */
    public boolean[] subset(boolean[] array, int offset, int length) {
        boolean[] subset = new boolean[length];
        System.arraycopy(array, offset, subset, 0, length);
        return subset;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#subset(byte[], int) 
     */
    public byte[] subset(byte[] array, int offset) {
        return subset(array, offset, array.length - offset);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#subset(byte[], int, int) 
     */
    public byte[] subset(byte[] array, int offset, int length) {
        byte[] subset = new byte[length];
        System.arraycopy(array, offset, subset, 0, length);
        return subset;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#subset(char[], int) 
     */
    public char[] subset(char[] array, int offset) {
        return subset(array, offset, array.length - offset);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#subset(char[], int, int) 
     */
    public char[] subset(char[] array, int offset, int length) {
        char[] subset = new char[length];
        System.arraycopy(array, offset, subset, 0, length);
        return subset;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#subset(int[], int) 
     */
    public int[] subset(int[] array, int offset) {
        return subset(array, offset, array.length - offset);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#subset(int[], int, int) 
     */
    public int[] subset(int[] array, int offset, int length) {
        int[] subset = new int[length];
        System.arraycopy(array, offset, subset, 0, length);
        return subset;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#subset(java.lang.String[], int) 
     */
    public String[] subset(String[] array, int offset) {
        return subset(array, offset, array.length - offset);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#subset(java.lang.String[], int, int) 
     */
    public String[] subset(String[] array, int offset, int length) {
        String[] subset = new String[length];
        System.arraycopy(array, offset, subset, 0, length);
        return subset;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#splice(boolean[], boolean, int)
     */
    public boolean[] splice(boolean[] array, boolean value, int index) {
        int length = array.length;
        boolean[] splice = new boolean[length + 1];
        System.arraycopy(array, 0, splice, 0, index);
        splice[index] = value;
        System.arraycopy(array, index, splice, index + 1, length - index);
        return splice;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#splice(boolean[], boolean[], int) 
     */
    public boolean[] splice(boolean[] array, boolean[] array2, int index) {
        int length = array.length;
        int length2 = array2.length;
        boolean[] splice = new boolean[length + length2];
        System.arraycopy(array, 0, splice, 0, index);
        System.arraycopy(array2, 0, splice, index, length2);
        System.arraycopy(array, index, splice, index + length2, length - index);
        return splice;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#splice(byte[], byte, int) 
     */
    public byte[] splice(byte[] array, byte value, int index) {
        int length = array.length;
        byte[] splice = new byte[length + 1];
        System.arraycopy(array, 0, splice, 0, index);
        splice[index] = value;
        System.arraycopy(array, index, splice, index + 1, length - index);
        return splice;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#splice(byte[], byte[], int) 
     */
    public byte[] splice(byte[] array, byte[] array2, int index) {
        int length = array.length;
        int length2 = array2.length;
        byte[] splice = new byte[length + length2];
        System.arraycopy(array, 0, splice, 0, index);
        System.arraycopy(array2, 0, splice, index, length2);
        System.arraycopy(array, index, splice, index + length2, length - index);
        return splice;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#splice(char[], char, int) 
     */
    public char[] splice(char[] array, char value, int index) {
        int length = array.length;
        char[] splice = new char[length + 1];
        System.arraycopy(array, 0, splice, 0, index);
        splice[index] = value;
        System.arraycopy(array, index, splice, index + 1, length - index);
        return splice;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#splice(char[], char[], int) 
     */
    public char[] splice(char[] array, char[] array2, int index) {
        int length = array.length;
        int length2 = array2.length;
        char[] splice = new char[length + length2];
        System.arraycopy(array, 0, splice, 0, index);
        System.arraycopy(array2, 0, splice, index, length2);
        System.arraycopy(array, index, splice, index + length2, length - index);
        return splice;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#splice(int[], int, int) 
     */
    public int[] splice(int[] array, int value, int index) {
        int length = array.length;
        int[] splice = new int[length + 1];
        System.arraycopy(array, 0, splice, 0, index);
        splice[index] = value;
        System.arraycopy(array, index, splice, index + 1, length - index);
        return splice;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#splice(int[], int[], int) 
     */
    public int[] splice(int[] array, int[] array2, int index) {
        int length = array.length;
        int length2 = array2.length;
        int[] splice = new int[length + length2];
        System.arraycopy(array, 0, splice, 0, index);
        System.arraycopy(array2, 0, splice, index, length2);
        System.arraycopy(array, index, splice, index + length2, length - index);
        return splice;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#splice(java.lang.String[], java.lang.String, int) 
     */
    public String[] splice(String[] array, String value, int index) {
        int length = array.length;
        String[] splice = new String[length + 1];
        System.arraycopy(array, 0, splice, 0, index);
        splice[index] = value;
        System.arraycopy(array, index, splice, index + 1, length - index);
        return splice;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#splice(java.lang.String[], java.lang.String[], int) 
     */
    public String[] splice(String[] array, String[] array2, int index) {
        int length = array.length;
        int length2 = array2.length;
        String[] splice = new String[length + length2];
        System.arraycopy(array, 0, splice, 0, index);
        System.arraycopy(array2, 0, splice, index, length2);
        System.arraycopy(array, index, splice, index + length2, length - index);
        return splice;
    }


    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#str(boolean) 
     */
    public String str(boolean val) {
        return String.valueOf(val);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#str(byte) 
     */
    public String str(byte val) {
        return String.valueOf(val);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#str(char) 
     */
    public String str(char val) {
        return String.valueOf(val);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#str(int) 
     */
    public String str(int val) {
        return String.valueOf(val);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#str(boolean[]) 
     */
    public String[] str(boolean[] val) {
        String[] result = new String[val.length];
        for (int i = val.length - 1; i >= 0; i--) {
            result[i] = String.valueOf(val[i]);
        }
        return result;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#str(byte) 
     */
    public String[] str(byte[] val) {
        String[] result = new String[val.length];
        for (int i = val.length - 1; i >= 0; i--) {
            result[i] = String.valueOf(val[i]);
        }
        return result;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#str(char[]) 
     */
    public String[] str(char[] val) {
        String[] result = new String[val.length];
        for (int i = val.length - 1; i >= 0; i--) {
            result[i] = String.valueOf(val[i]);
        }
        return result;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#str(int[]) 
     */
    public String[] str(int[] val) {
        String[] result = new String[val.length];
        for (int i = val.length - 1; i >= 0; i--) {
            result[i] = String.valueOf(val[i]);
        }
        return result;
    }


    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#pushMatrix()
     */
    public void pushMatrix() {
        if (_stackIndex == _stack.length) {
            int[] old = _stack;
            _stack = new int[_stackIndex * 2];
            System.arraycopy(old, 0, _stack, 0, _stackIndex);
        }
        _stack[_stackIndex++] = _bufferg.getTranslateX();
        _stack[_stackIndex++] = _bufferg.getTranslateY();
        _stack[_stackIndex++] = _bufferg.getClipX();
        _stack[_stackIndex++] = _bufferg.getClipY();
        _stack[_stackIndex++] = _bufferg.getClipWidth();
        _stack[_stackIndex++] = _bufferg.getClipHeight();
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#popMatrix()
     */
    public void popMatrix() {
        if (_stackIndex > 0) {
            _stackIndex -= 6;
            int translateX = _stack[_stackIndex++];
            int translateY = _stack[_stackIndex++];
            _bufferg.translate(translateX - _bufferg.getTranslateX(), translateY - _bufferg.getTranslateY());
            int clipX = _stack[_stackIndex++];
            int clipY = _stack[_stackIndex++];
            int clipWidth = _stack[_stackIndex++];
            int clipHeight = _stack[_stackIndex++];
            _bufferg.setClip(clipX, clipY, clipWidth, clipHeight);
            _stackIndex -= 6;
        }
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#resetMatrix() 
     */
    public void resetMatrix() {
        _stackIndex = 0;
        _bufferg.translate(-_bufferg.getTranslateX(), -_bufferg.getTranslateY());
        _bufferg.setClip(0, 0, width, height);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#translate(int, int) 
     */
    public void translate(int x, int y) {
        _bufferg.translate(x, y);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#random(int) 
     */
    public final int random(int value1) {
        return random(0, value1);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#random(int, int) 
     */
    public final int random(int value1, int value2) {
        if (_random == null) {
            _random = new Random();
        }
        int min = Math.min(value1, value2);
        int range = Math.abs(value2 - value1) ;

        return min + Math.abs((_random.nextInt() % range));
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#loadBytes(java.lang.String)
     */
    public final byte[] loadBytes(String filename) {
        byte [] result = null ;
        try {
            RecordStore store = null;
            try {
                String name = filename;
                if (name.length() > 32) {
                    name = name.substring(0, 32);
                }
                store = RecordStore.openRecordStore(name, false);
                return store.getRecord(1);
            } catch (RecordStoreNotFoundException rsnfe) {
            } finally {
                if (store != null) {
                    store.closeRecordStore();
                }
            }
        } catch (Exception e) {
            //
        }
        InputStream is = null;
        try {
            is = getClass().getResourceAsStream(filename);
            if (is != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                byte[] buffer = new byte[1024];
                int bytesRead = is.read(buffer);
                while (bytesRead >= 0) {
                    baos.write(buffer, 0, bytesRead);
                    bytesRead = is.read(buffer);
                }
                result = baos.toByteArray();
            } else {
                result = new byte[0];
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            //
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ioe) {
                }
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#loadStrings(java.lang.String) 
     */
    public final String[] loadStrings(String filename) {
        try {
            RecordStore store = null;
            try {
                String name = filename;
                if (name.length() > 32) {
                    name = name.substring(0, 32);
                }
                store = RecordStore.openRecordStore(name, false);
                int numRecords = store.getNumRecords();
                String[] strings = new String[numRecords];
                for (int i = 0; i < numRecords; i++) {
                    byte[] data = store.getRecord(i + 1);
                    if (data != null) {
                        strings[i] = new String(data);
                    } else {
                        strings[i] = "";
                    }
                }
                return strings;
            } catch (RecordStoreNotFoundException rsnfe) {
            } finally {
                if (store != null) {
                    store.closeRecordStore();
                }
            }
        } catch (Exception e) {
            //
        }
        Vector v = new Vector();
        InputStream is = null;
        try {
            is = getClass().getResourceAsStream(filename);
            if (is != null) {
                Reader r = new InputStreamReader(is);

                int numStrings = 0;

                StringBuffer buffer = new StringBuffer();
                int input = r.read();
                while (true) {
                    if ((input < 0) || (input == '\n')) {
                        String s = buffer.toString().trim();
                        if (s.length() > 0) {
                            numStrings++;
                            v.addElement(s);
                        }
                        buffer.delete(0, Integer.MAX_VALUE);

                        if (input < 0) {
                            break;
                        }
                    } else {
                        buffer.append((char) input);
                    }

                    input = r.read();
                }
            }
        } catch (Exception e) {
            // 
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ioe) {
                }
            }
        }
        String[] strings = new String[v.size()];
        v.copyInto(strings);

        return strings;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#saveBytes(java.lang.String, byte[]) 
     */
    public final void saveBytes(String filename, byte[] data) {
        //// max 32 char names on recordstores
        if (filename.length() > 32) {
            //
        }
        try {
            try {
                RecordStore.deleteRecordStore(filename);
            } catch (RecordStoreNotFoundException rsnfe) {
            }
            RecordStore store = RecordStore.openRecordStore(filename, true);
            store.addRecord(data, 0, data.length);
            store.closeRecordStore();
        } catch (Exception e) {
            // 
        }
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#saveStrings(java.lang.String, java.lang.String[]) 
     */
    public final void saveStrings(String filename, String[] strings) {
        //// max 32 char names on recordstores
        if (filename.length() > 32) {
            // 
        }
        try {
            //// delete recordstore, if it exists
            try {
                RecordStore.deleteRecordStore(filename);
            } catch (RecordStoreNotFoundException rsnfe) {
            }
            //// create new recordstore
            RecordStore store = RecordStore.openRecordStore(filename, true);
            //// add each string as a record
            byte[] data;
            for (int i = 0, length = strings.length; i < length; i++) {
                data = strings[i].getBytes();
                store.addRecord(data, 0, data.length);
            }
            store.closeRecordStore();
        } catch (Exception e) {
            //
        }
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#openStream(java.lang.String) 
     */
    public InputStream openStream(String fileName) {
        try {
            return getClass().getResourceAsStream("/" + fileName);
        } catch(Exception e) {
           return null;
        }
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#loadFont(java.lang.String, de.enough.polish.processing.color, de.enough.polish.processing.color) 
     */
    public PFont loadFont(String fontname, color fgColor, color bgColor) {
        return new PFont(fontname, fgColor, bgColor);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#loadFont(java.lang.String, de.enough.polish.processing.color) 
     */
    public PFont loadFont(String fontname, color fgColor) {
        return new PFont (fontname, fgColor, new color(0x00FFFFFF));
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#loadFont(java.lang.String) 
     */
    public PFont loadFont(String fontname) {
        return new PFont(fontname);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#loadFont() 
     */
    public PFont loadFont() {
        return new PFont ( Font.getDefaultFont() );
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#loadFont(int, int, int) 
     */
    public PFont loadFont(int face, int style, int size) {
        return new PFont(Font.getFont(face, style, size));
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#textFont(de.enough.polish.processing.PFont) 
     */
    public void textFont (PFont font)
    {
        _defaultFont = font ;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#textWrap(java.lang.String, int) 
     */
    public String[] textWrap(String data, int width) {
        return textWrap(data, width, Integer.MAX_VALUE);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#textWrap(java.lang.String, int, int) 
     */
    public String[] textWrap(String data, int width, int height) {
        
        //// calculate max number of lines that will fit in height
        int maxlines = height / _defaultFont.getHeight();
        //// total number of chars in text
        int textLength = data.length();
        //// current index into text;
        int i = 0;
        //// working character
        char c;
        //// vector of lines
        Vector lines = new Vector();
        //// current line
        char[] line = new char[256];
        //// number of characters in current line
        int lineLength;
        //// index of last whitespace break point in current line
        int last;
        //// width of current line
        int lineWidth;
        while (i < textLength) {
            c = data.charAt(i);
            //// start at first non-whitespace character
            if (c != ' ') {
                //// at first non-whitespace character, start building up line
                lineLength = 0;
                last = -1;
                lineWidth = 0;
                while (lineWidth <= width) {
                    if (i == textLength) {
                        last = lineLength;
                        break;
                    }
                    c = data.charAt(i);
                    i++;
                    line[lineLength] = c;
                    lineLength++;
                    if (c == ' ') {
                        last = lineLength - 1;
                        while ((i < textLength) && (data.charAt(i) == ' ')) {
                            i++;
                            line[lineLength] = ' ';
                            lineLength++;
                        }
                    } else if (c == '\n') {
                        last = lineLength - 1;
                        break;
                    }
                    lineWidth = _defaultFont.charsWidth(line, 0, lineLength);
                }
                if (last >= 0) {
                    //// take chars up to last break point
                    lines.addElement(new String(line, 0, last));
                    i -= lineLength - last;
                } else {
                    //// rare case of very long words (i.e. urls) that can't fit on one line, just split
                    lines.addElement(new String(line, 0, lineLength - 1));
                    i = i - 2;
                }
            }
            //// check if reached max number of lines
            if (lines.size() == maxlines) {
                break;
            }
            //// increment to next character
            i++;
        }
        //// finally, copy into array and return
        String[] array = new String[lines.size()];
        lines.copyInto(array);
        return array;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#textWidth(java.lang.String) 
     */
    public int textWidth(String data) {
        return _defaultFont.stringWidth(data);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#textAlign(int) 
     */
    public void textAlign (int mode)
    {
        _textAlignMode = mode ;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#text(java.lang.String, int, int) 
     */
    public void text(String text, int x, int y) {
        
        if ( _defaultFont.isBitmapFont() == false )
        {
            _bufferg.setColor(_fillColor);
        }

        pushMatrix();

        _defaultFont.draw(_bufferg, text, x, y, LEFT);
        
        popMatrix();
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#text(java.lang.String, int, int, int, int) 
     */
    public void text(String text, int x, int y, int width, int height) {
        String[] data = textWrap(text, width, height);

        if ( _defaultFont.isBitmapFont() == false )
        {
            _bufferg.setColor(_fillColor);
        }

        //// save current clip and apply clip to bounding area
        pushMatrix();
        _bufferg.setClip(x, y, width, height);
        
        //// adjust starting baseline so that text is _contained_ within the bounds
        int textX = x;
        //y += textFont.getBaseline();

        String line;
        for (int i = 0, length = data.length; i < length; i++) {
            line = data[i];
            //// calculate alignment within bounds
            switch (_textAlignMode) {
                case CENTER:
                    textX = x + ((width - textWidth(line)) >> 1);
                    break;
                case RIGHT:
                    textX = x + width - textWidth(line);
                    break;
            }
            _defaultFont.draw(_bufferg, line, textX, y+_defaultFont.getBaseline(), LEFT);
            y += _defaultFont.getHeight();
        }
        //// restore clip
        popMatrix();
    }
    
    /** Precision, in number of bits for the fractional part. */
    public static final int FP_PRECISION    = 8;
    /** Convenience constant of the value 1 in fixed point. */
    public static final int ONE             = 1 << FP_PRECISION;
    /** Convenience constant of the value of pi in fixed point. */
    public static final int PI              = (int) ((3.14159265358979323846f) * ONE);
    /** Convenience constant of the value of 2*pi in fixed point. */
    public static final int TWO_PI          = 2 * PI;
    /** Convenience constant of the value of pi/2 in fixed point. */
    public static final int HALF_PI         = PI / 2;
    
    //#if polish.hasFloatingPoint
    /** Convenience constant of the value of pi in fixed point. */
    public static final double PI_D              = Math.PI;
    /** Convenience constant of the value of 2*pi in fixed point. */
    public static final double TWO_PI_D          = 2 * Math.PI;
    /** Convenience constant of the value of pi/2 in fixed point. */
    public static final double HALF_PI_D         = Math.PI / 2;
    //#endif


    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#abs(int)
     */
    public final int abs(int value) {
        return Math.abs(value);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#max(int, int) 
     */
    public final int max(int value1, int value2) {
        return Math.max(value1, value2);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#min(int, int) 
     */
    public final int min(int value1, int value2) {
        return Math.min(value1, value2);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#sq(int) 
     */
    public final int sq(int value) {
        return value * value;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#pow(int, int) 
     */
    public final int pow(int base, int exponent) {
        int value = 1;
        for (int i = 0; i < exponent; i++) {
            value *= base;
        }

        return value;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#constrain(int, int, int) 
     */
    public final int constrain(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#mul(int, int) 
     */
    public final int mul(int value1, int value2) {
        return (value1 * value2) >> FP_PRECISION;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#div(int, int) 
     */
    public final int div(int dividend, int divisor) {
        return (dividend << FP_PRECISION) / divisor;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#itofp(int)
     */
    public final int itofp(int value1) {
        return value1 << FP_PRECISION;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#fptoi(int)
     */
    public final int fptoi(int value1) {
        if (value1 < 0) {
            value1 += ONE - 1;
        }
        return value1 >> FP_PRECISION;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#sqrt(int) 
     */
    public final int sqrt(int value_fp) {
        int prev_fp, next_fp, error_fp, prev;
        //// initialize previous result
        prev_fp = value_fp;
        next_fp = 0;
        do {
            prev = prev_fp >> FP_PRECISION;
            if (prev == 0) {
                break;
            }
            //// calculate a new approximation
            next_fp = (prev_fp + value_fp / prev) / 2;
            if (prev_fp > next_fp) {
                error_fp = prev_fp - next_fp;
            } else {
                error_fp = next_fp - prev_fp;
            }
            prev_fp = next_fp;
        } while (error_fp > ONE);

        return next_fp;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#dist(int, int, int, int) 
     */
    public final int dist(int x1, int y1, int x2, int y2) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        return sqrt((dx * dx + dy * dy) << FP_PRECISION);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#dist_fp(int, int, int, int) 
     */
    public final int dist_fp(int x1, int y1, int x2, int y2) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        return sqrt(((dx * dx) >> FP_PRECISION) + ((dy * dy) >> FP_PRECISION));
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#floor(int)
     */
    public final int floor(int value1) {
        return (value1 >> FP_PRECISION) << FP_PRECISION;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#ceil(int) 
     */
    public final int ceil(int value1) {
        return ((value1 + ONE - 1) >> FP_PRECISION) << FP_PRECISION;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#round(int)
     */
    public final int round(int value1) {
        //// return result
        return ((value1 + (ONE >> 1)) >> FP_PRECISION) << FP_PRECISION;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#radians(int) 
     */
    public final int radians(int angle) {
        return angle * PI / (180 << FP_PRECISION);
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#sin(int) 
     */
    public final int sin(int rad) {
        //// convert to degrees
        int index = rad * 180 / PI % 360;
        if (index < 0) {
            index += 360;
        }
        return sin[index];
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#cos(int) 
     */
    public final int cos(int rad) {
        //// convert to degrees
        int index = (rad * 180 / PI + 90) % 360;
        if (index < 0) {
            index += 360;
        }
        return sin[index];
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#atan(int) 
     */
    public final int atan(int value1) {
        int result;
        int sign = 1;
        if (value1 < 0) {
            sign = -1;
            value1 = -value1;
        }
        if (value1 <= ONE) {
            result = div(value1, ONE + mul(((int) (0.28f * ONE)), mul(value1, value1)));
        } else {
            result = HALF_PI - div(value1, (mul(value1, value1) + ((int) (0.28f * ONE))));
        }
        return sign * result;
    }

    /* (non-Javadoc)
     * @see de.enough.polish.processing.ProcessingInterface#atan2(int, int) 
     */
    public final int atan2(int y, int x) {
        int result;
        if ((y == 0) && (x == 0)) {
            result = 0;
        } else if (x > 0) {
            result = atan(div(y, x));
        } else if (x < 0) {
            if (y < 0) {
                result = -(PI - atan(div(-y, -x)));
            } else {
                result = PI - atan(div(y, -x));
            }
        } else {
            if (y < 0) {
                result = -HALF_PI;
            } else {
                result = HALF_PI;
            }
        }
        return result;
    }
    
    //#if polish.hasFloatingPoint
	/* (non-Javadoc)
	 * @see de.enough.polish.processing.ProcessingInterface#sin(double)
	 */
	public double sind(double rad) {
		return Math.sin(rad);
	}
    
	/* (non-Javadoc)
	 * @see de.enough.polish.processing.ProcessingInterface#cos(double)
	 */
	public double cosd(double rad) {
		return Math.cos(rad);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.processing.ProcessingInterface#atan(double)
	 */
	public double atan(double value1) {
		return MathUtil.atan(value1);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.processing.ProcessingInterface#atan2d(int, int)
	 */
	public double atan2d(int x, int y) {
		return MathUtil.atan2(x, y);
	}
	//#endif

    /** Lookup table for sin function, indexed by degrees. */
    public static final int[] sin = {
        (int) (0f * ONE),
        (int) (0.0174524064372835f * ONE),
        (int) (0.034899496702501f * ONE),
        (int) (0.0523359562429438f * ONE),
        (int) (0.0697564737441253f * ONE),
        (int) (0.0871557427476582f * ONE),
        (int) (0.104528463267653f * ONE),
        (int) (0.121869343405147f * ONE),
        (int) (0.139173100960065f * ONE),
        (int) (0.156434465040231f * ONE),
        (int) (0.17364817766693f * ONE),
        (int) (0.190808995376545f * ONE),
        (int) (0.207911690817759f * ONE),
        (int) (0.224951054343865f * ONE),
        (int) (0.241921895599668f * ONE),
        (int) (0.258819045102521f * ONE),
        (int) (0.275637355816999f * ONE),
        (int) (0.292371704722737f * ONE),
        (int) (0.309016994374947f * ONE),
        (int) (0.325568154457157f * ONE),
        (int) (0.342020143325669f * ONE),
        (int) (0.3583679495453f * ONE),
        (int) (0.374606593415912f * ONE),
        (int) (0.390731128489274f * ONE),
        (int) (0.4067366430758f * ONE),
        (int) (0.422618261740699f * ONE),
        (int) (0.438371146789077f * ONE),
        (int) (0.453990499739547f * ONE),
        (int) (0.469471562785891f * ONE),
        (int) (0.484809620246337f * ONE),
        (int) (0.5f * ONE),
        (int) (0.515038074910054f * ONE),
        (int) (0.529919264233205f * ONE),
        (int) (0.544639035015027f * ONE),
        (int) (0.559192903470747f * ONE),
        (int) (0.573576436351046f * ONE),
        (int) (0.587785252292473f * ONE),
        (int) (0.601815023152048f * ONE),
        (int) (0.615661475325658f * ONE),
        (int) (0.629320391049837f * ONE),
        (int) (0.642787609686539f * ONE),
        (int) (0.656059028990507f * ONE),
        (int) (0.669130606358858f * ONE),
        (int) (0.681998360062498f * ONE),
        (int) (0.694658370458997f * ONE),
        (int) (0.707106781186547f * ONE),
        (int) (0.719339800338651f * ONE),
        (int) (0.73135370161917f * ONE),
        (int) (0.743144825477394f * ONE),
        (int) (0.754709580222772f * ONE),
        (int) (0.766044443118978f * ONE),
        (int) (0.777145961456971f * ONE),
        (int) (0.788010753606722f * ONE),
        (int) (0.798635510047293f * ONE),
        (int) (0.809016994374947f * ONE),
        (int) (0.819152044288992f * ONE),
        (int) (0.829037572555042f * ONE),
        (int) (0.838670567945424f * ONE),
        (int) (0.848048096156426f * ONE),
        (int) (0.857167300702112f * ONE),
        (int) (0.866025403784439f * ONE),
        (int) (0.874619707139396f * ONE),
        (int) (0.882947592858927f * ONE),
        (int) (0.891006524188368f * ONE),
        (int) (0.898794046299167f * ONE),
        (int) (0.90630778703665f * ONE),
        (int) (0.913545457642601f * ONE),
        (int) (0.92050485345244f * ONE),
        (int) (0.927183854566787f * ONE),
        (int) (0.933580426497202f * ONE),
        (int) (0.939692620785908f * ONE),
        (int) (0.945518575599317f * ONE),
        (int) (0.951056516295154f * ONE),
        (int) (0.956304755963035f * ONE),
        (int) (0.961261695938319f * ONE),
        (int) (0.965925826289068f * ONE),
        (int) (0.970295726275996f * ONE),
        (int) (0.974370064785235f * ONE),
        (int) (0.978147600733806f * ONE),
        (int) (0.981627183447664f * ONE),
        (int) (0.984807753012208f * ONE),
        (int) (0.987688340595138f * ONE),
        (int) (0.99026806874157f * ONE),
        (int) (0.992546151641322f * ONE),
        (int) (0.994521895368273f * ONE),
        (int) (0.996194698091746f * ONE),
        (int) (0.997564050259824f * ONE),
        (int) (0.998629534754574f * ONE),
        (int) (0.999390827019096f * ONE),
        (int) (0.999847695156391f * ONE),
        (int) (1f * ONE),
        (int) (0.999847695156391f * ONE),
        (int) (0.999390827019096f * ONE),
        (int) (0.998629534754574f * ONE),
        (int) (0.997564050259824f * ONE),
        (int) (0.996194698091746f * ONE),
        (int) (0.994521895368273f * ONE),
        (int) (0.992546151641322f * ONE),
        (int) (0.99026806874157f * ONE),
        (int) (0.987688340595138f * ONE),
        (int) (0.984807753012208f * ONE),
        (int) (0.981627183447664f * ONE),
        (int) (0.978147600733806f * ONE),
        (int) (0.974370064785235f * ONE),
        (int) (0.970295726275996f * ONE),
        (int) (0.965925826289068f * ONE),
        (int) (0.961261695938319f * ONE),
        (int) (0.956304755963036f * ONE),
        (int) (0.951056516295154f * ONE),
        (int) (0.945518575599317f * ONE),
        (int) (0.939692620785908f * ONE),
        (int) (0.933580426497202f * ONE),
        (int) (0.927183854566787f * ONE),
        (int) (0.92050485345244f * ONE),
        (int) (0.913545457642601f * ONE),
        (int) (0.90630778703665f * ONE),
        (int) (0.898794046299167f * ONE),
        (int) (0.891006524188368f * ONE),
        (int) (0.882947592858927f * ONE),
        (int) (0.874619707139396f * ONE),
        (int) (0.866025403784439f * ONE),
        (int) (0.857167300702112f * ONE),
        (int) (0.848048096156426f * ONE),
        (int) (0.838670567945424f * ONE),
        (int) (0.829037572555042f * ONE),
        (int) (0.819152044288992f * ONE),
        (int) (0.809016994374947f * ONE),
        (int) (0.798635510047293f * ONE),
        (int) (0.788010753606722f * ONE),
        (int) (0.777145961456971f * ONE),
        (int) (0.766044443118978f * ONE),
        (int) (0.754709580222772f * ONE),
        (int) (0.743144825477394f * ONE),
        (int) (0.731353701619171f * ONE),
        (int) (0.719339800338651f * ONE),
        (int) (0.707106781186548f * ONE),
        (int) (0.694658370458997f * ONE),
        (int) (0.681998360062499f * ONE),
        (int) (0.669130606358858f * ONE),
        (int) (0.656059028990507f * ONE),
        (int) (0.642787609686539f * ONE),
        (int) (0.629320391049838f * ONE),
        (int) (0.615661475325658f * ONE),
        (int) (0.601815023152048f * ONE),
        (int) (0.587785252292473f * ONE),
        (int) (0.573576436351046f * ONE),
        (int) (0.559192903470747f * ONE),
        (int) (0.544639035015027f * ONE),
        (int) (0.529919264233205f * ONE),
        (int) (0.515038074910054f * ONE),
        (int) (0.5f * ONE),
        (int) (0.484809620246337f * ONE),
        (int) (0.469471562785891f * ONE),
        (int) (0.453990499739547f * ONE),
        (int) (0.438371146789077f * ONE),
        (int) (0.422618261740699f * ONE),
        (int) (0.4067366430758f * ONE),
        (int) (0.390731128489274f * ONE),
        (int) (0.374606593415912f * ONE),
        (int) (0.3583679495453f * ONE),
        (int) (0.342020143325669f * ONE),
        (int) (0.325568154457157f * ONE),
        (int) (0.309016994374948f * ONE),
        (int) (0.292371704722737f * ONE),
        (int) (0.275637355817f * ONE),
        (int) (0.258819045102521f * ONE),
        (int) (0.241921895599668f * ONE),
        (int) (0.224951054343865f * ONE),
        (int) (0.207911690817759f * ONE),
        (int) (0.190808995376545f * ONE),
        (int) (0.17364817766693f * ONE),
        (int) (0.156434465040231f * ONE),
        (int) (0.139173100960066f * ONE),
        (int) (0.121869343405148f * ONE),
        (int) (0.104528463267654f * ONE),
        (int) (0.0871557427476586f * ONE),
        (int) (0.0697564737441255f * ONE),
        (int) (0.0523359562429438f * ONE),
        (int) (0.0348994967025007f * ONE),
        (int) (0.0174524064372834f * ONE),
        (int) (1.22514845490862E-16f * ONE),
        (int) (-0.0174524064372832f * ONE),
        (int) (-0.0348994967025009f * ONE),
        (int) (-0.0523359562429436f * ONE),
        (int) (-0.0697564737441248f * ONE),
        (int) (-0.0871557427476579f * ONE),
        (int) (-0.104528463267653f * ONE),
        (int) (-0.121869343405148f * ONE),
        (int) (-0.139173100960066f * ONE),
        (int) (-0.156434465040231f * ONE),
        (int) (-0.17364817766693f * ONE),
        (int) (-0.190808995376545f * ONE),
        (int) (-0.207911690817759f * ONE),
        (int) (-0.224951054343865f * ONE),
        (int) (-0.241921895599668f * ONE),
        (int) (-0.25881904510252f * ONE),
        (int) (-0.275637355816999f * ONE),
        (int) (-0.292371704722736f * ONE),
        (int) (-0.309016994374948f * ONE),
        (int) (-0.325568154457157f * ONE),
        (int) (-0.342020143325669f * ONE),
        (int) (-0.3583679495453f * ONE),
        (int) (-0.374606593415912f * ONE),
        (int) (-0.390731128489274f * ONE),
        (int) (-0.4067366430758f * ONE),
        (int) (-0.422618261740699f * ONE),
        (int) (-0.438371146789077f * ONE),
        (int) (-0.453990499739546f * ONE),
        (int) (-0.469471562785891f * ONE),
        (int) (-0.484809620246337f * ONE),
        (int) (-0.5f * ONE),
        (int) (-0.515038074910054f * ONE),
        (int) (-0.529919264233205f * ONE),
        (int) (-0.544639035015027f * ONE),
        (int) (-0.559192903470747f * ONE),
        (int) (-0.573576436351046f * ONE),
        (int) (-0.587785252292473f * ONE),
        (int) (-0.601815023152048f * ONE),
        (int) (-0.615661475325658f * ONE),
        (int) (-0.629320391049838f * ONE),
        (int) (-0.642787609686539f * ONE),
        (int) (-0.656059028990507f * ONE),
        (int) (-0.669130606358858f * ONE),
        (int) (-0.681998360062498f * ONE),
        (int) (-0.694658370458997f * ONE),
        (int) (-0.707106781186547f * ONE),
        (int) (-0.719339800338651f * ONE),
        (int) (-0.73135370161917f * ONE),
        (int) (-0.743144825477394f * ONE),
        (int) (-0.754709580222772f * ONE),
        (int) (-0.766044443118978f * ONE),
        (int) (-0.777145961456971f * ONE),
        (int) (-0.788010753606722f * ONE),
        (int) (-0.798635510047293f * ONE),
        (int) (-0.809016994374947f * ONE),
        (int) (-0.819152044288992f * ONE),
        (int) (-0.829037572555041f * ONE),
        (int) (-0.838670567945424f * ONE),
        (int) (-0.848048096156426f * ONE),
        (int) (-0.857167300702112f * ONE),
        (int) (-0.866025403784438f * ONE),
        (int) (-0.874619707139396f * ONE),
        (int) (-0.882947592858927f * ONE),
        (int) (-0.891006524188368f * ONE),
        (int) (-0.898794046299167f * ONE),
        (int) (-0.90630778703665f * ONE),
        (int) (-0.913545457642601f * ONE),
        (int) (-0.92050485345244f * ONE),
        (int) (-0.927183854566787f * ONE),
        (int) (-0.933580426497202f * ONE),
        (int) (-0.939692620785908f * ONE),
        (int) (-0.945518575599317f * ONE),
        (int) (-0.951056516295154f * ONE),
        (int) (-0.956304755963035f * ONE),
        (int) (-0.961261695938319f * ONE),
        (int) (-0.965925826289068f * ONE),
        (int) (-0.970295726275996f * ONE),
        (int) (-0.974370064785235f * ONE),
        (int) (-0.978147600733806f * ONE),
        (int) (-0.981627183447664f * ONE),
        (int) (-0.984807753012208f * ONE),
        (int) (-0.987688340595138f * ONE),
        (int) (-0.99026806874157f * ONE),
        (int) (-0.992546151641322f * ONE),
        (int) (-0.994521895368273f * ONE),
        (int) (-0.996194698091746f * ONE),
        (int) (-0.997564050259824f * ONE),
        (int) (-0.998629534754574f * ONE),
        (int) (-0.999390827019096f * ONE),
        (int) (-0.999847695156391f * ONE),
        (int) (-1f * ONE),
        (int) (-0.999847695156391f * ONE),
        (int) (-0.999390827019096f * ONE),
        (int) (-0.998629534754574f * ONE),
        (int) (-0.997564050259824f * ONE),
        (int) (-0.996194698091746f * ONE),
        (int) (-0.994521895368273f * ONE),
        (int) (-0.992546151641322f * ONE),
        (int) (-0.99026806874157f * ONE),
        (int) (-0.987688340595138f * ONE),
        (int) (-0.984807753012208f * ONE),
        (int) (-0.981627183447664f * ONE),
        (int) (-0.978147600733806f * ONE),
        (int) (-0.974370064785235f * ONE),
        (int) (-0.970295726275997f * ONE),
        (int) (-0.965925826289068f * ONE),
        (int) (-0.961261695938319f * ONE),
        (int) (-0.956304755963035f * ONE),
        (int) (-0.951056516295154f * ONE),
        (int) (-0.945518575599317f * ONE),
        (int) (-0.939692620785909f * ONE),
        (int) (-0.933580426497202f * ONE),
        (int) (-0.927183854566787f * ONE),
        (int) (-0.92050485345244f * ONE),
        (int) (-0.913545457642601f * ONE),
        (int) (-0.90630778703665f * ONE),
        (int) (-0.898794046299167f * ONE),
        (int) (-0.891006524188368f * ONE),
        (int) (-0.882947592858927f * ONE),
        (int) (-0.874619707139396f * ONE),
        (int) (-0.866025403784439f * ONE),
        (int) (-0.857167300702112f * ONE),
        (int) (-0.848048096156426f * ONE),
        (int) (-0.838670567945424f * ONE),
        (int) (-0.829037572555042f * ONE),
        (int) (-0.819152044288992f * ONE),
        (int) (-0.809016994374948f * ONE),
        (int) (-0.798635510047293f * ONE),
        (int) (-0.788010753606722f * ONE),
        (int) (-0.777145961456971f * ONE),
        (int) (-0.766044443118978f * ONE),
        (int) (-0.754709580222772f * ONE),
        (int) (-0.743144825477395f * ONE),
        (int) (-0.731353701619171f * ONE),
        (int) (-0.719339800338652f * ONE),
        (int) (-0.707106781186548f * ONE),
        (int) (-0.694658370458998f * ONE),
        (int) (-0.681998360062498f * ONE),
        (int) (-0.669130606358858f * ONE),
        (int) (-0.656059028990507f * ONE),
        (int) (-0.64278760968654f * ONE),
        (int) (-0.629320391049838f * ONE),
        (int) (-0.615661475325659f * ONE),
        (int) (-0.601815023152048f * ONE),
        (int) (-0.587785252292473f * ONE),
        (int) (-0.573576436351046f * ONE),
        (int) (-0.559192903470747f * ONE),
        (int) (-0.544639035015027f * ONE),
        (int) (-0.529919264233206f * ONE),
        (int) (-0.515038074910054f * ONE),
        (int) (-0.5f * ONE),
        (int) (-0.484809620246337f * ONE),
        (int) (-0.469471562785891f * ONE),
        (int) (-0.453990499739547f * ONE),
        (int) (-0.438371146789077f * ONE),
        (int) (-0.4226182617407f * ONE),
        (int) (-0.4067366430758f * ONE),
        (int) (-0.390731128489275f * ONE),
        (int) (-0.374606593415912f * ONE),
        (int) (-0.358367949545301f * ONE),
        (int) (-0.342020143325669f * ONE),
        (int) (-0.325568154457158f * ONE),
        (int) (-0.309016994374948f * ONE),
        (int) (-0.292371704722736f * ONE),
        (int) (-0.275637355817f * ONE),
        (int) (-0.258819045102521f * ONE),
        (int) (-0.241921895599668f * ONE),
        (int) (-0.224951054343865f * ONE),
        (int) (-0.20791169081776f * ONE),
        (int) (-0.190808995376545f * ONE),
        (int) (-0.173648177666931f * ONE),
        (int) (-0.156434465040231f * ONE),
        (int) (-0.139173100960066f * ONE),
        (int) (-0.121869343405148f * ONE),
        (int) (-0.104528463267653f * ONE),
        (int) (-0.0871557427476583f * ONE),
        (int) (-0.0697564737441248f * ONE),
        (int) (-0.0523359562429444f * ONE),
        (int) (-0.0348994967025008f * ONE),
        (int) (-0.0174524064372844f * ONE),
    };

    //#if polish.hasFloatingPoint
	/* (non-Javadoc)
	 * @see de.enough.polish.processing.ProcessingInterface#append(float[], float)
	 */
	public float[] append(float[] array, float element) {
		float[] old = array;
        int length = old.length;
        array = new float[length + 1];
        System.arraycopy(old, 0, array, 0, length);
        array[length] = element;
        return array;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.processing.ProcessingInterface#append(double[], double)
	 */
	public double[] append(double[] array, double element) {
		double[] old = array;
        int length = old.length;
        array = new double[length + 1];
        System.arraycopy(old, 0, array, 0, length);
        array[length] = element;
        return array;
	}

	public double random(double value) {
		double COEFF  = 10000;
		int randomNumber = random( (int) (value * COEFF));
		return randomNumber / COEFF;
	}
	//#endif

//#endif
//#if !tmp.isContextIncluded
}
//#endif