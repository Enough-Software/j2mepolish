//#condition polish.midp || polish.usePolishGui
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.processing;

import de.enough.polish.util.RgbImage;
import java.io.InputStream;
import java.util.Vector;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;

/**
 * This interface defines all the methods required to write a standalone Mobile Processing implementation which can be integrated with J2ME Polish.
 * The methods defined within include the Mobile Processing methods as defined by the specs, extension methods specific to J2ME Polish and a set of system methods
 * to be used for integrating J2ME Polish and the Mobile Processing implementation.
 * @author Ovidiu Iliescu
 */
public interface ProcessingInterface {

    /**
     * A vector that keeps track of all objects that implement ProcessingInterface.
     */
    public static Vector processingContextObjects = new Vector();

    /*
     * Non-Processing methods that are used by the system. These methods are used to create a bridge between J2ME Polish and
     * Mobile Processing. They are not meant to be used by the end-programmer.
     *
     */

    /**
     * Signals a size change to the Mobile Processing implementation
     * @param width the new width
     * @param height the new height
     */
    public void signalSizeChange(int width, int height);

    /**
     * Signals a pointer dragged event to the Mobile Processing implementation.
     * @param x the x-coordinate of the poiner
     * @param y the y-coordinate of the pointer
     */
    public void signalPointerDragged(int x, int y);

    /**
     * Signals a pointer released event to the Mobile Processing implementation.
     * @param x the x-coordinate of the pointer
     * @param y the y-coordinate of the pointer
     */
    public void signalPointerReleased(int x, int y);

    /**
     * Signals a pointer pressed event to the Mobile Processing implementation.
     * @param x the x-coordinate of the pointer
     * @param y the y-coordinate of the pointer
     */
    public void signalPointerPressed(int x, int y);

    /**
     * Signals to the Mobile Processing implementation that a softkey has been pressed.
     * @param label the label of the softkey
     */
    public void signalSoftkeyPressed(String label);

    /**
     * Signals to the Mobile Processing implementation that a key has been pressed
     * @param keyCode the keycode of the key, as reported by the JVM
     */
    public void signalKeyPressed(int keyCode);

    /**
     * Signals to the Mobile Processing implementation that a key has been released
     * @param keyCode the keycode of the key, as reported by the JVM
     */
    public void signalKeyReleased(int keyCode);

    /**
     * Signals to the Mobile Processing implementation that the application is about to be suspended.
     */
    public void signalApplicationSuspend();

    /**
     * Signals to the Mobile Processing implementation that the application has been resumed.
     */
    public void signalApplicationResume();

    /**
     * Signals to the Mobile Processing implementation that it is about to be destroyed.
     */
    public void signalDestroy();

    /**
     * Signals to the Mobile Processing implementation that it must initialize itself.
     */
    public void signalInitialization();

    /**
     * Signals to the Mobile Processing implementation that it has focus.
     */
    public void signalHasFocus();

    /**
     * Signals to the Mobile Processing implementation that it lost focus
     */
    public void signalLostFocus();

    /**
     * Returns the image buffer of the Mobile Processing implementation
     * @return image buffer
     */
    public Image getBuffer();

    /**
     * Returns the label of the softkey set via softkey(), if any, or null for none.
     * @return the softkey label, or null for none.
     */
    public String getSoftkeyLabel();

    /**
     * Returns the system time at which the last frame was drawn
     * @return the last frame time, or -1 if no frames have been drawn yet.
     */
    public long getLastFrameTime();

    /**
     * Returns the time (in milliseconds) between two consecutive frames.
     * @return the interval between frames, in milliseconds
     */
    public long getIntervalBetweenFrames();

    /**
     * Checks if a refresh is needed since the last time the function was called.
     * @return true if a refresh should be executed, false otherwise
     */
    public boolean checkForRefresh();

    /**
     * Checks if the Processing draw() routine is called in a loop or not.
     * @return true if draw() is called in a loop, false otherwise.
     */
    public boolean isLooping();

    /**
     * Sets the parent container of the Mobile Processing implementation
     * @param parent the parent container
     */
    public void setParent ( ProcessingContextContainerInterface parent);

    /**
     * Sets the pointer coordinates to the given values.
     * @param x the x-coordinate of the pointer
     * @param y the y-coordinate of the pointer
     */
    public void setPointerCoordinates ( int x, int y);

    /**
     * Sets the key and keyCode variables (which correspond to the last active key) to the given values.
     * @param key the key's corresponding character
     * @param keyCode the keycode of key
     */
    public void setKeyAndKeyCode (char key, int keyCode);

    /**
     * Executes a forced refresh by calling draw().
     * @param alsoUpdateLastFrameTime should the last frame time be updated also?
     */
    public void executeRefresh(boolean alsoUpdateLastFrameTime);

    /**
     * Triggers a repaint of the Mobile Processing context and/or of its parent container.
     */
    public void triggerRepaint();

    /**
     * Checks if keypress events should be captured and handled solely by the Mobile Processing implementation, or if they should also be bubbled up to the parent container and above.
     * @return true if keypress events should be captured, false otherwise
     */
    public boolean areKeypressesCaptured();

    /**
     * Checks if softkey presses should be captured and handled solely by the Mobile Processing implementation, or if they should also be bubbled up to the parent container and above.
     * @return true if softkey presses should be captured, false otherwise
     */
    public boolean areSoftkeysCaptured();

    /**
     * Checks if pointer events should be captured and handled solely by the Mobile Processing implementation, or if they should also be bubbled up to the parent container and above.
     * @return true if pointer events should be captured, false otherwise
     */
    public boolean arePointerEventsCaptured();

    /**
     * Checks if transparent drawing is active or not
     * @return true if transparent drawing is active, false otherwise
     */
    public boolean isDrawingTransparent();

    //#if polish.midp2
    /**
     * Returns the image buffer as an RgbImage, with transparency applied to it.
     * @return the image buffer as an RgbImage
     */
    public RgbImage getTransparentRgbImage();
    //#endif

    /**
     * Returns the current transparent color (pixels of this color will be replaced with transparent ones when calling getTransparentRgbImage()).
     * @return the transparent color
     * @see #getTransparentRgbImage()
     */
    public int getTransparentColor();


    /**
     * Starts the initialization sequence ( calls setup() and other initialization-related tasks )
     */
    public void executeInitializationSequence();

    /*
     * J2ME Polish extension methods and event handlers that shoud be implemented.
     * These methods and event handlers can be used within Processing code by the end-programmer in the same way methods from the original Mobile Processing specs can be used.
     */

    /**
     * Event handler for the "has focus" event.
     */
    public void focus();

    /**
     * Event handler for the "lost focus" event.
     */
    public void lostFocus();

    /**
     * Signals that keypress events should be captured (such events should not be bubbled to the parent container and above anymore).
     */
    public void captureKeyPresses();

    /**
     * Releases keypress events (resumes bubbling of such events to the parent container and above).
     */
    public void releaseKeyPresses();

    /**
     * Signals that softkey presses should be captured (such events should not be bubbled to the parent container and above anymore).
     */
    public void captureSoftkeys();

    /**
     * Releases softkey events (resumes bubbling of such events to the parent container and above).
     */
    public void releaseSoftkeys();

    /**
     * Signals that pointer events should be captured (such events should not be bubbled to the parent container and above anymore).
     */
    public void capturePointerEvents();

    /**
     * Releases pointer events (resumes bubbling of such events to the parent container and above).
     */
    public void releasePointerEvents();

    /**
     * Starts/resumes repainting the background between frames.
     */
    public void repaintBackground();

    /**
     * Stops repainting the background between frames.
     */
    public void dontRepaintBackground();

    /**
     * Activates transparent drawing.
     */
    public void transparentDrawing();

    /**
     * Deactivates transparent drawing.
     */
    public void opaqueDrawing();

    /**
     * Sets the transparent color to be used with getTransparentRgbImage().
     * @param color the transparent color to use
     * @see #getTransparentRgbImage()
     */
    public void setTransparentColor ( color color );

    /**
     * Sets the transparent color to be used with getTransparentRgbImage().
     * @param gray the level of gray to use as a transparent color
     * @see #getTransparentRgbImage()
     */
    public void setTransparentColor ( int gray );

    /**
     * Sets the transparent color to be used with getTransparentRgbImage().
     * @param value1 red or hue values relative to the current color range
     * @param value2 green or saturation values relative to the current color range
     * @param value3 blue or brightness values relative to the current color range
     */
    public void setTransparentColor (int value1, int value2, int value3);

    /**
     * Enables fast drawing.
     */
    public void enableFastDrawing();

    /**
     * Disable fast drawing.
     */
    public void disableFastDrawing();
    
    /*
     * Non-static variables that need defining :
     *
     * width, height - dimensions of the Processing sketch
     *
     * key, keycode, rawKeyCode - key-related variables
     *
     * pointerX, pointerY - last known coordinates of the pointer
     *
     * These variables are required by the Mobile Processing specifications are can be used
     * by the end-programmer within Processing code. Always make sure they are available and
     * that their values are the proper ones.
     *
     */


    /*
     * Mobile Processing constants
     */
    
    // color modes
    public static final int RGB = 1;
    public static final int HSB = 2;

    // Shape modes
    public static final int POINTS          = 0;
    public static final int LINES           = 1;
    public static final int LINE_STRIP      = 2;
    public static final int LINE_LOOP       = 3;
    public static final int TRIANGLES       = 4;
    public static final int TRIANGLE_STRIP  = 5;
    public static final int QUADS           = 6;
    public static final int QUAD_STRIP      = 7;
    public static final int POLYGON         = 8;

    // Drawing style and positions
    public static final int CENTER          = 0;
    public static final int CENTER_RADIUS   = 1;
    public static final int CORNER          = 2;
    public static final int CORNERS         = 3;

    // Keycode constants
    public static final int UP              = Canvas.UP;
    public static final int DOWN            = Canvas.DOWN;
    public static final int LEFT            = Canvas.LEFT;
    public static final int RIGHT           = Canvas.RIGHT;
    public static final int FIRE            = Canvas.FIRE;
    public static final int GAME_A          = Canvas.GAME_A;
    public static final int GAME_B          = Canvas.GAME_B;
    public static final int GAME_C          = Canvas.GAME_C;
    public static final int GAME_D          = Canvas.GAME_D;
    public static final int SOFTKEY1        = -6;
    public static final int SOFTKEY2        = -7;
    public static final int SEND            = -10;

    // Font related constants
    public static final int FACE_SYSTEM         = Font.FACE_SYSTEM;
    public static final int FACE_MONOSPACE      = Font.FACE_MONOSPACE;
    public static final int FACE_PROPORTIONAL   = Font.FACE_PROPORTIONAL;

    public static final int STYLE_PLAIN         = Font.STYLE_PLAIN;
    public static final int STYLE_BOLD          = Font.STYLE_BOLD;
    public static final int STYLE_ITALIC        = Font.STYLE_ITALIC;
    public static final int STYLE_UNDERLINED    = Font.STYLE_UNDERLINED;

    public static final int SIZE_SMALL          = Font.SIZE_SMALL;
    public static final int SIZE_MEDIUM         = Font.SIZE_MEDIUM;
    public static final int SIZE_LARGE          = Font.SIZE_LARGE;

    /*
     * Methods required and defined by the Mobile Processing specs.
     * For compatibility with existing Mobile Processing code, always make sure your implementation is according to the sepcs.
     */

    /**
     * Returns an estimate of the current amount of free memory in the system, in bytes. 
     * @return free memory
     */
    public int currentMemory();

    /**
     * Returns the reported total amount of memory available. This may change if the operating system is dynamically adjusting the amount of memory available. 
     */
    public int reportedMemory();

    /**
     * Specifies the number of frames to be displayed every second. If the processor is not fast enough to maintain the specified rate (for whatever reason), it will not be acheived. For example, the function call framerate(30) will attempt to refresh 30 times a second. It is recommended to set the framerate within setup().
     * @param framerate
     */
    public void framerate(int framerate);

    /**
     * Called directly after setup() and continuously executes the lines of code contained inside its block until the program is stopped or noLoop() is called. draw() is called automatically and should never be called explicitly. It should always be controlled with noLoop(), redraw()  and loop(). After noLoop() stops the code in draw()  from executing, redraw() causes the code inside draw() to execute once and loop() will causes the code inside draw() to execute continuously again. The number of times draw() executes in each second may be controlled with the framerate() function. There can only be one draw() function for each sketch and draw() must exist if you want the code to run continuously. You can call redraw() from within an event handler to execute a redraw even if noLoop() is active.
     */
    public void draw();

    /**
     * Executes the code within draw() one time. This functions allows the program to update the display window only when necessary, for example when an event registered by keyPressed() occurs. In structuring a program, it only makes sense to call redraw() within events such as keyPressed(). Calling it within draw() has no effect because draw() is continuously called anyway.
     */
    public void redraw();

    /**
     * Causes Processing to continuously execute the code within draw(). If noLoop() is called, the code in draw() stops executing. 
     */
    public void loop();

    /**
     * Stops Processing from continuously executing the code within draw(). If loop() is called, the code in draw() is executed continuously
     */
    public void noLoop();

    /**
     * Event handler that is called once when the sketch is about to be ended. Save your data and do cleanup work within this event handler.
     */
    public void destroy();

    /**
     * Event handler that is called once when the application is about to be suspended.
     */
    public void suspend();

    /**
     * Event handler that is called once when the application has resumed.
     */
    public void resume();

    /**
     * Called once when the program is started and every time the size of the sketch changes (eg: when the phone is switched from portrait to landscape). Used to define initial enviroment properties background color, loading images, etc. before the draw() begins executing. Variables declared within setup() are not accessible within other functions, including draw(). There can only be one setup() function for each sketch.
     */
    public void setup();

    /**
     * Call this function to stop running your sketch. The destroy() callback will be called before the sketch exits.
     */
    public void exit();

    /**
     * Changes the way Processing interprets color data (by using either the RGB or the HSB color spaces).
     * @param mode Either RGB or HSB, corresponding to Red/Green/Blue and Hue/Saturation/Brightness
     */
    public void colorMode(int mode);

    /**
     * Changes the way Processing interprets color data (by using either the RGB or the HSB color spaces). Also allows you to specify a range for all three color channels at once
     * @param mode Either RGB or HSB, corresponding to Red/Green/Blue and Hue/Saturation/Brightness
     * @param range range for all color elements
     */
    public void colorMode(int mode, int range);

    /**
     * Changes the way Processing interprets color data (by using either the RGB or the HSB color spaces). Also allows you to specify a range for all three color channels individually
     * @param mode Either RGB or HSB, corresponding to Red/Green/Blue and Hue/Saturation/Brightness
     * @param range1 range for the red or hue depending on the current color mode
     * @param range2 range for the green or saturation depending on the current color mode
     * @param range3 range for the blue or brightness depending on the current color mode
     */
    public void colorMode(int mode, int range1, int range2, int range3);

    /**
     * Creates colors for storing in variables of the color datatype. The resulting color is a shade of gray.
     * @param gray shade of gray, between 0 and 255
     * @return the resulting color
     */
    public color color(int gray);

    /**
     * Creates colors for storing in variables of the color datatype. The resulting color is a shade of gray.
     * @param gray shade of gray, between 0 and 255
     * @param alpha alpha level (NOTE: on current MIDP 2.1 devices and below this is ignored)
     * @return the resulting color
     */
    public color color(int gray, int alpha);

    /**
     * Creates colors for storing in variables of the color datatype. The parameters are interpreted as RGB or HSB values depending on the current colorMode().
     * @param value1 red or hue values relative to the current color range
     * @param value2 green or saturation values relative to the current color range
     * @param value3 blue or brightness values relative to the current color range
     * @param alpha alpha level (NOTE: on current MIDP 2.1 devices and below this is ignored)
     * @return the resulting color
     */
    public color color(int value1, int value2, int value3, int alpha);

    /**
     * Sets the color used to draw lines and borders around shapes.
     * @param color the color to use
     */
    public void stroke(color color);

    /**
     * Sets a shade of gray to be used used to draw lines and borders around shapes.
     * @param gray the shade of gray to use, between 0 and 255
     */
    public void stroke(int gray);

    /**
     * Sets the color used to draw lines and borders around shapes. This color is either specified in terms of the RGB or HSB color depending on the current colorMode().
     * @param param1 red or hue values relative to the current color range
     * @param param2 green or saturation values relative to the current color range
     * @param param3 blue or brightness values relative to the current color range
     */
    public void stroke(int param1, int param2, int param3);

    /**
     * Sets the width of the stroke used for lines, points, and the border around shapes. All widths are set in units of pixels.
     * @param width the stroke width
     */
    public void strokeWeight(int width);

    /**
     * Disables drawing the stroke (outline). If both noStroke() and noFill()  are called, nothing will be drawn to the screen. 
     */
    public void noStroke();

    /**
     * Sets the shade of gray to be used to fill shapes.
     * @param gray the shade of gray to use, between 0 and 255
     */
    public void fill(int gray);

    /**
     * Sets the color used to fill shapes. 
     * @param color the fill color for shapes
     */
    public void fill(color color);

    /**
     * Sets the color used to fill shapes. This color is either specified in terms of the RGB or HSB color depending on the current colorMode().
     * @param value1 red or hue values relative to the current color range
     * @param value2 green or saturation values relative to the current color range
     * @param value3 blue or brightness values relative to the current color range
     */
    public void fill(int value1, int value2, int value3);

    /**
     * Disables filling geometry. If both noStroke() and noFill()  are called, nothing will be drawn to the screen. 
     */
    public void noFill();

    /**
     * Sets the shade of gray to be used as a background color.
     * @param gray the shade of gray to use, between 0 and 255
     */
    public void background(int gray);

    /**
     * Sets the color to use as a background color.
     * @param color color the color to use as a background color
     */
    public void background(color color);

    /**
     * Sets the color to use as a background color. This color is either specified in terms of the RGB or HSB color depending on the current colorMode().
     * @param value1 red or hue values relative to the current color range
     * @param value2 green or saturation values relative to the current color range
     * @param value3 blue or brightness values relative to the current color range
     */
    public void background(int value1, int value2, int value3);

    /**
     * Sets the image to be used as a background. The image will be centered.
     * @param img the image to use.
     */
    public void background(PImage img);

    /**
     * Draws a line (a direct path between two points) to the screen. The version of line() with four parameters draws the line in 2D. To color a line, use the stroke() function. A line cannot be filled, therefore the fill() method will not affect the color of a line. 2D lines are drawn with a width of one pixel by default, but this can be changed with the strokeWeight() function. 
     * @param x1 x-coordinate of the first point
     * @param y1 y-coordinate of the first point
     * @param x2 x-coordinate of the second point
     * @param y2 y-coordinate of the second point
     */
    public void line(int x1, int y1, int x2, int y2);

    /**
     * Draws a Bezier curve on the screen. These curves are defined by a series of anchor and control points. The first two parameters specify the first anchor point and the last two parameters specify the other anchor point. The middle parameters specify the control points which define the shape of the curve.
     * @param x1 x-coordinate of the first anchor point
     * @param y1 y-coordinate of the first anchor point
     * @param x2 x-coordinate of the first control point
     * @param y2 y-coordinate of the first control point
     * @param x3 x-coordinate of the second control point
     * @param y3 y-coordinate of the second control point
     * @param x4 x-coordinate of the second anchor point
     * @param y4 y-coordinate of the second anchor point
     */
    public void bezier(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4);

    /**
     * Draws a point on the screen
     * @param x the x coordinate of the point
     * @param y the y coordinate of the point
     */
    public void point(int x, int y);

    /**
     * A triangle is a plane created by connecting three points. The first two arguments specify the first point, the middle two arguments specify the second point, and the last two arguments specify the third point. 
     * @param x1 x-coordinate of the first point
     * @param y1 y-coordinate of the first point
     * @param x2 x-coordinate of the second point
     * @param y2 y-coordinate of the second point
     * @param x3 x-coordinate of the third point
     * @param y3 y-coordinate of the third point
     */
    public void triangle(int x1, int y1, int x2, int y2, int x3, int y3);

    /**
     * A quad is a quadrilateral, a four sided polygon. It is similar to a rectangle, but the angles between its edges are not constrained to ninety degrees. 
     * @param x1 x-coordinate of the first corner
     * @param y1 y-coordinate of the first corner
     * @param x2 x-coordinate of the second corner
     * @param y2 y-coordinate of the second corner
     * @param x3 x-coordinate of the third corner
     * @param y3 y-coordinate of the third corner
     * @param x4 x-coordinate of the fourth corner
     * @param y4 y-coordinate of the fourth corner
     */
    public void quad(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4);

    /**
     * Draws a rectangle to the screen. A rectangle is a four-sided shape with every angle at ninety degrees. The first two parameters set the location, the third sets the width, and the fourth sets the height. The origin is changed with the rectMode() function. 
     * @param x x-coordinate of the rectangle
     * @param y y-coordinate of the rectangle
     * @param width width of the rectangle
     * @param height height of the rectangle
     * @see #rectMode(int)
     */
    public void rect(int x, int y, int width, int height);

    /**
     * Modifies the location from which rectangles draw. The default mode is rectMode(CORNER), which specifies the location to be the upper left corner of the shape and uses the third and fourth parameters of rect() to specify the width and height. The syntax rectMode(CORNERS) uses the first and second parameters of rect() to set the location of one corner and uses the third and fourth parameters to set the opposite corner. The syntax rectMode(CENTER) draws the image from its center point and uses the third and forth parameters to rect() to specify the image's width and height.
     * @param mode Either CORNER, CORNERS, CENTER
     */
    public void rectMode(int mode);

    /**
     * Draws an ellipse (oval) in the display window. An ellipse with an equal width  and height is a circle. The first two parameters set the location, the third sets the width, and the fourth sets the height. The origin may be changed with the ellipseMode() function. 
     * @param x x-coordinate of the ellipse
     * @param y y-coordinate of the ellipse
     * @param width width of the ellipse
     * @param height height of the ellipse
     * @see #ellipseMode(int)
     */
    public void ellipse(int x, int y, int width, int height);

    /**
     * Draws a curved line on the screen. The first and second parameters specify the first anchor point and the last two parameters specify the second anchor. The middle parameters specify the points for defining the shape of the curve. Longer curves can be created by putting a series of curve() functions together. The curve() function is an implementation of Catmull-Rom splines. 
     * @param x1 x-coordinate of the first anchor point
     * @param y1 y-coordinate of the first anchor point
     * @param x2 x-coordinate of the first control point
     * @param y2 y-coordinate of the first control point
     * @param x3 x-coordinate of the second control point
     * @param y3 y-coordinate of the second control point
     * @param x4 x-coordinate of the second anchor point
     * @param y4 y-coordinate of the second anchor point
     */
    public void curve(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4);

    /**
     * The origin of the ellipse is modified by the ellipseMode() function. The default configuration is ellipseMode(CENTER), which specifies the location of the ellipse as the center of the shape. The CENTER_RADIUS mode is the same, but the width and height parameters to ellipse()  specify the radius of the ellipse, rather than the diameter. The CORNER mode draws the shape from the upper-left corner of its bounding box. The CORNERS mode uses the four parameters to ellipse() to set two opposing corners of the ellipse's bounding box.
     * @param mode Either CENTER, CENTER_RADIUS, CORNER, CORNERS.
     */
    public void ellipseMode(int mode);

    /**
     * Using the beginShape() and endShape() functions allow creating more complex forms. beginShape() begins recording vertices for a shape and endShape() stops recording. The value of the MODE parameter tells it which types of shapes to create from the provided vertices. The parameters available for beginShape() are LINES, LINE_STRIP, LINE_LOOP, TRIANGLES, TRIANGLE_STRIP, QUADS, QUAD_STRIP, and POLYGON. If there is no MODE specified, POLYGON is used. After calling the beginShape() function, a series of vertex()  commands must follow. To stop drawing the shape, call endShape().Each shape will be outlined with the current stroke color and filled with the fill color. Transformations such as translate(), rotate(), and scale() do not work within beginShape(). 
     * @param mode Either LINES, LINE_STRIP, LINE_LOOP, TRIANGLES, TRIANGLE_STRIP, QUADS, QUAD_STRIP, POLYGON
     * @see #endShape()
     */
    public void beginShape(int mode);

    /**
     * Specifies vertex coordinates for curves. This function may only be called between beginShape() and endShape() and can only be used with the drawing types POLYGON, LINE_LOOP, and LINE_STRIP. The curveVertex()  function is an implementation of Catmull-Rom splines
     * @param x the x coordinate of the next vertex
     * @param y the y coordinate of the next vertex
     */
    public void curveVertex(int x, int y);

    /**
     * Specifies vertex coordinates for Bezier curves. Each call to bezierVertex() defines the position of two control points and one anchor point of a Bezier curve, adding a new segment to a line or shape. The first time bezierVertex() is used within a beginShape() call, it must be prefaced with a call to vertex() to set the first anchor point. This function must be used between beginShape() and endShape() can only be used with the drawing types POLYGON, LINE_LOOP, and LINE_STRIP. 
     * @param x1 x-coordinate of the first control point
     * @param y1 y-coordinate of the first control point
     * @param x2 x-coordinate of the second control point
     * @param y2 y-coordinate of the second control point
     * @param x3 x-coordinate of the anchor point
     * @param y3 y-coordinate of the anchor point
     * @see #vertex(int, int)
     */
    public void bezierVertex(int x1, int y1, int x2, int y2, int x3, int y3);

    /**
     * All shapes are constructed by connecting a series of vertices. vertex()  is used to specify the vertex coordinates for points, lines, triangles, quads, and polygons and is used exclusively within the beginShape()  and endShape() function. 
     * @param x the x coordinate of the next vertex
     * @param y the y coordinate of the next vertex
     */
    public void vertex(int x, int y);

    /**
     * The endShape() function is the companion to beginShape() and may only be called after the later. When endshape() is called, all of image data defined since the previous call to beginShape()  is written into the image buffer.
     * @see #beginShape(int) ;
     */
    public void endShape();

    /**
     * Loads an image into a variable of type PImage. Only .png images may be loaded. To load correctly, images must be located in the data directory of the current sketch. In most cases, load all images in setup()  to preload them when the program starts. Loading images in draw()  can dramatically reduce the speed of a program.
     * @param filename the name of the image file to load
     * @return the resulting PImage
     */
    public PImage loadImage(String filename);

    /**
     * Loads an image into a variable of type PImage. Only .png images may be loaded. To load correctly, images must be located in the data directory of the current sketch. In most cases, load all images in setup()  to preload them when the program starts. Loading images in draw()  can dramatically reduce the speed of a program.
     * @param data array of bytes of image data in .png format
     * @return the resulting PImage
     */
    public PImage loadImage(byte[] data);

    /**
     * Draws images to the screen.
     * @param img the image to use
     * @param x the x-coordinate of where to draw the image 
     * @param y the y coordinate of where to draw the image
     */
    public void image(PImage img, int x, int y);

    /**
     * Diplays images to the screen. To display only a sub-area of the image, specify the upper-left corner and dimensions of the sub-area. The imageMode() function changes the way the parameters work. For example, a call to imageMode(CORNERS)  will change the width and height parameters to define the x and y values of the opposite corner of the image.
     * @param img img the image to display
     * @param sx x-coordinate of the sub-area in the image
     * @param sy y-coordinate of the sub-area in the image
     * @param swidth width of the sub-area in the image
     * @param sheight height of the sub-area in the image
     * @param dx destination x-coordinate of the image
     * @param dy destination y-coordinate of the image
     * @see #imageMode(int)
     */
    public void image(PImage img, int sx, int sy, int swidth, int sheight, int dx, int dy);

    /**
     * The mode to use when displaying images
     * @param mode the image mode
     * @see #image(de.enough.polish.processing.PImage, int, int, int, int, int, int)
     */
    public void imageMode(int mode);

    /**
     * The softkey() function sets up a custom labelled softkey. 
     * @param label the custom label on the softkey
     */
    public void softkey(String label);

    /**
     * Event handler for sofkey presses. The softkeyPressed() function is called when the user presses a custom labelled softkey. A softkey is labelled using the softkey()  function. The custom label is passed to the function as a parameter.
     * @param label the label on the softkey
     */
    public void softkeyPressed(String label);

    /**
     * Event handler for when a regular key is pressed. The keyPressed() function is called once every time a key is pressed. As a general rule, nothing should be draw within the keyPressed()  block.
     */
    public void keyPressed();

    /**
     * Event handler for when a regular key is released. The keyReleased() function is called once every time a key is released. As a general rule, nothing should be draw within the keyReleased()  block.
     */
    public void keyReleased();

    /**
     * The multitap function will turn on multitap text input. When enabled, each key press will be interpreted according to the multitap input method and stored in an array of characters called multitapBuffer.
     * The multitap input method interprets each key press based on the letters associated with the key as seen on a typical phone key pad. For example, the 2 key is associated with the letters 'a', 'b', and 'c'. If the user presses the 2 key once, it is interpreted as the first letter associated with the key, which is 'a'. If the user presses the 2 key three times quickly, this will be interpreted as the letter 'c', which is the third letter associated with the key. A fourth press will be interpreted as the number 2, and subsequent presses will cycle through the same characters.
     * The multitapBuffer array contains the characters interpreted by the multitap input method. This array will automatically grow in size depending upon the number of characters entered. The variable multitapBufferLength represents the actual number of characters stored in the array.
     * The multitapBufferIndex variable represents the location of the editing "cursor" in the buffer. If the user presses the left or right directional keys, this cursor will be moved in the buffer.
     * @see #noMultitap()
     */
    public void multitap();

    /**
     * The noMultitap function will turn off multitap text input.
     * @see #multitap() 
     */
    public void noMultitap();

    /**
     * The multitapClear function will clear the multitap input buffer. Any characters that have been buffered from interpreting key presses will be lost.
     * @see #multitap()
     */
    public void multitapClear();

    /**
     * The multitapDeleteChar function will delete one character from the multitap buffer at the current index.
     * @see #multitap()
     */
    public void multitapDeleteChar();

    /**
     * The textInput function will open a new screen in which the user can input text using the native text input methods of the mobile phone (i.e. T9). Execution of the sketch will be paused until the text input screen is dismissed by the user. The text will be returned as a string. 
     * @return the user-entered text
     */
    public String textInput();

    /**
     * The textInput function will open a new screen in which the user can input text using the native text input methods of the mobile phone (i.e. T9). Execution of the sketch will be paused until the text input screen is dismissed by the user. The text will be returned as a string.
     * @param title title of the text input form
     * @param text default text of the form
     * @param max maximum length of the user entered text
     * @return the user-entered text
     */
    public String textInput(String title, String text, int max);

    /**
     * Event handler for when the pointer is dragged across the screen. The pointerDragged() function is called as the stylus or other pointing device is dragged across the touchscreen. As a general rule, nothing should be drawn within the pointerDragged() block. 
     */
    public void pointerDragged();

    /**
     * Event handler for when the pointer is pressed. The pointerPressed() function is called once every time the stylus or other pointing device has been pressed onto the touchscreen. As a general rule, nothing should be drawn within the pointerPressed()  block. 
     */
    public void pointerPressed();

    /**
     * Event handler for when the pointer has been released (after a press).  	 The pointerReleased() function is called as the stylus or other pointing device is picked up off the touchscreen. As a general rule, nothing should be drawn within the pointerReleased() block.
     */
    public void pointerReleased();

    /**
     * Returns the number of milliseconds (thousandths of a second) since starting an applet. This information is often used for timing animation sequences. 
     * @return elapsed time (in milliseconds)
     */
    public int millis();

    /**
     * The second() function returns the current second as a value from 0 - 59.
     * @return current second
     */
    public int second();

    /**
     * The minute() function returns the current minute as a value from 0 - 59.
     * @return current minute
     */
    public int minute();

    /**
     * The hour() function returns the current hour as a value from 0 - 23. 
     * @return current hour
     */
    public int hour();

    /**
     * The day() function returns the current day as a value from 1 - 31.
     * @return current day
     */
    public int day();

    /**
     * The month() function returns the current month as a value from 1 - 12.
     * @return current month
     */
    public int month();

    /**
     * The year() function returns the current year as an integer (2003, 2004, 2005, etc). 
     * @return current year
     */
    public int year();

    /**
     * Outputs the given data to the console. Useful for debugging.
     * @param data the data to output
     */
    public void print(boolean data);

    /**
     * Outputs the given data to the console. Useful for debugging.
     * @param data the data to output
     */
    public void print(byte data);

    /**
     * Outputs the given data to the console. Useful for debugging.
     * @param data the data to output
     */
    public void print(char data);

    /**
     * Outputs the given data to the console. Useful for debugging.
     * @param data the data to output
     */
    public void print(int data);

    /**
     * Outputs the given data to the console. Useful for debugging.
     * @param data the data to output
     */
    public void print(Object data);

    /**
     * Outputs the given data to the console. Useful for debugging.
     * @param data the data to output
     */
    public void print(String data);

    /**
     * Outputs the given data to the console, creating a new line of text. Useful for debugging.
     * @param data the data to output
     */
    public void println(boolean data);

    /**
     * Outputs the given data to the console, creating a new line of text. Useful for debugging.
     * @param data the data to output
     */
    public void println(byte data);

    /**
     * Outputs the given data to the console, creating a new line of text. Useful for debugging.
     * @param data the data to output
     */
    public void println(char data);

    /**
     * Outputs the given data to the console, creating a new line of text. Useful for debugging.
     * @param data the data to output
     */
    public void println(int data);

    /**
     * Outputs the given data to the console, creating a new line of text. Useful for debugging.
     * @param data the data to output
     */
    public void println(Object data);

    /**
     * Outputs the given data to the console, creating a new line of text. Useful for debugging.
     * @param data the data to output
     */
    public void println(String data);

    /**
     * Returns the length, or size, of an array.
     * @param array the array in question
     * @return array length
     */
    public int length(boolean[] array);

    /**
     * Returns the length, or size, of an array.
     * @param array the array in question
     * @return array length
     */
    public int length(byte[] array);

    /**
     * Returns the length, or size, of an array.
     * @param array the array in question
     * @return array length
     */
    public int length(char[] array);

    /**
     * Returns the length, or size, of an array.
     * @param array the array in question
     * @return array length
     */
    public int length(int[] array);

    /**
     * Returns the length, or size, of an array.
     * @param array the array in question
     * @return array length
     */
    public int length(Object[] array);

    /**
     * Combines an array of elements into one String.
     * @param anyArray the source array
     * @param separator the separator string
     * @return the resulting string
     */
    public String join(String[] anyArray, String separator);

    /**
     * Combines an array of elements into one String.
     * @param anyArray the source array
     * @param separator the separator string
     * @return the resulting string
     */
    public String join(int[] anyArray, String separator);

    /**
     * Combines an array of elements into one String and pads each number with zeroes until it has the specified number of digits.
     * @param intArray the source array
     * @param separator the separator string
     * @param digits desired number of digits for each number
     * @return the resulting string
     */
    public String join(int[] intArray, String separator, int digits);

    /**
     * Utility function for formatting numbers into strings (with padding). The value for the digits  parameter should always be a positive integer.
     * @param intValue the number to format
     * @param digits the number of digits
     * @return the resulting string
     * @see #nfp(int, int) 
     */
    public String nf(int intValue, int digits);

    /**
     * Utility function for formatting numbers into strings. Similar to nf()  but puts a "+" in front of positive numbers and a "-" in front of negative numbers. The value for the digits parameter should always be a positive integer.
     * @param intValue the number to format
     * @param digits the number of digits
     * @return the resulting string
     * @see #nf(int, int)
     */
    public String nfp(int intValue, int digits);

    /**
     * Splits a given string into an array of strings, according to the specified delimiter
     * @param str the source string
     * @param delim delimiter
     * @return resulting array
     */
    public String[] split(String str, char delim);

    /**
     * Splits a given string into an array of strings, according to the specified delimiter
     * @param str the source string
     * @param delim delimiter
     * @return resulting array
     */
    public String[] split(String str, String delim);

    /**
     * Removes whitespace characters from the beginning and end of a String. In addition to standard whitespace characters such as space, carriage return, and tab, this function also removes the Unicode "nbsp" character. 
     * @param str original string
     * @return trimmed string
     */
    public String trim(String str);

    /**
     * Expands an array by one element and adds data to the new position. The datatype of the element parameter must be the same as the datatype of the array.
     * @param array the original array
     * @param element element to append
     * @return resulting array
     */
    public String[] append(String[] array, String element);

    /**
     * Expands an array by one element and adds data to the new position. The datatype of the element parameter must be the same as the datatype of the array.
     * @param array the original array
     * @param element element to append
     * @return resulting array
     */
    public boolean[] append(boolean[] array, boolean element);
    
    //#if polish.hasFloatingPoint
    /**
     * Expands an array by one element and adds data to the new position. The datatype of the element parameter must be the same as the datatype of the array.
     * @param array the original array
     * @param element element to append
     * @return resulting array
     */
    public float[] append(float[] array, float element);
    
    /**
     * Expands an array by one element and adds data to the new position. The datatype of the element parameter must be the same as the datatype of the array.
     * @param array the original array
     * @param element element to append
     * @return resulting array
     */
    public double[] append(double[] array, double element);
    //#endif

    /**
     * Expands an array by one element and adds data to the new position. The datatype of the element parameter must be the same as the datatype of the array.
     * @param array the original array
     * @param element element to append
     * @return resulting array
     */
    public byte[] append(byte[] array, byte element);

    /**
     * Expands an array by one element and adds data to the new position. The datatype of the element parameter must be the same as the datatype of the array.
     * @param array the original array
     * @param element element to append
     * @return resulting array
     */
    public char[] append(char[] array, char element);

    /**
     * Expands an array by one element and adds data to the new position. The datatype of the element parameter must be the same as the datatype of the array.
     * @param array the original array
     * @param element element to append
     * @return resulting array
     */
    public int[] append(int[] array, int element);

    /**
     * Concatenates two arrays. For example, concatenating the array { 1, 2, 3 } and the array { 4, 5, 6 } yields { 1, 2, 3, 4, 5, 6 }. Both parameters must be arrays of the same datatype.
     * @param array1 the first array
     * @param array2 the second array
     * @return the resulting array
     */
    public String[] concat(String[] array1, String[] array2);

    /**
     * Concatenates two arrays. For example, concatenating the array { 1, 2, 3 } and the array { 4, 5, 6 } yields { 1, 2, 3, 4, 5, 6 }. Both parameters must be arrays of the same datatype.
     * @param array1 the first array
     * @param array2 the second array
     * @return the resulting array
     */
    public boolean[] concat(boolean[] array1, boolean[] array2);

    /**
     * Concatenates two arrays. For example, concatenating the array { 1, 2, 3 } and the array { 4, 5, 6 } yields { 1, 2, 3, 4, 5, 6 }. Both parameters must be arrays of the same datatype.
     * @param array1 the first array
     * @param array2 the second array
     * @return the resulting array
     */
    public byte[] concat(byte[] array1, byte[] array2);

    /**
     * Concatenates two arrays. For example, concatenating the array { 1, 2, 3 } and the array { 4, 5, 6 } yields { 1, 2, 3, 4, 5, 6 }. Both parameters must be arrays of the same datatype.
     * @param array1 the first array
     * @param array2 the second array
     * @return the resulting array
     */
    public char[] concat(char[] array1, char[] array2);

    /**
     * Concatenates two arrays. For example, concatenating the array { 1, 2, 3 } and the array { 4, 5, 6 } yields { 1, 2, 3, 4, 5, 6 }. Both parameters must be arrays of the same datatype.
     * @param array1 the first array
     * @param array2 the second array
     * @return the resulting array
     */
    public int[] concat(int[] array1, int[] array2);

    /**
     * Decreases the size of an array
     * @param array the array in question
     * @param newSize new size
     * @return resulting array
     */
    public boolean[] contract(boolean[] array, int newSize);

    /**
     * Decreases the size of an array
     * @param array the array in question
     * @param newSize new size
     * @return resulting array
     */
    public byte[] contract(byte[] array, int newSize);

    /**
     * Decreases the size of an array
     * @param array the array in question
     * @param newSize new size
     * @return resulting array
     */
    public char[] contract(char[] array, int newSize);

    /**
     * Decreases the size of an array
     * @param array the array in question
     * @param newSize new size
     * @return resulting array
     */
    public int[] contract(int[] array, int newSize);

    /**
     * Decreases the size of an array
     * @param array the array in question
     * @param newSize new size
     * @return resulting array
     */
    public String[] contract(String[] array, int newSize);

    /**
     * Doubles the size of an array.
     * @param array the array in question
     */
    public boolean[] expand(boolean[] array);
    
    /**
     * Increases the size of an array to the specified size
     * @param array the array in question
     * @param newSize new size
     * @return resulting array
     */
    public boolean[] expand(boolean[] array, int newSize);

    /**
     * Doubles the size of an array.
     * @param array the array in question
     */
    public byte[] expand(byte[] array);

    /**
     * Increases the size of an array to the specified size
     * @param array the array in question
     * @param newSize new size
     * @return resulting array
     */
    public byte[] expand(byte[] array, int newSize);

    /**
     * Doubles the size of an array.
     * @param array the array in question
     */
    public char[] expand(char[] array);
    
    /**
     * Increases the size of an array to the specified size
     * @param array the array in question
     * @param newSize new size
     * @return resulting array
     */
    public char[] expand(char[] array, int newSize);

    /**
     * Doubles the size of an array.
     * @param array the array in question
     */
    public int[] expand(int[] array);

    /**
     * Increases the size of an array to the specified size
     * @param array the array in question
     * @param newSize new size
     * @return resulting array
     */
    public int[] expand(int[] array, int newSize);

    /**
     * Doubles the size of an array.
     * @param array the array in question
     */
    public String[] expand(String[] array);

    /**
     * Increases the size of an array to the specified size
     * @param array the array in question
     * @param newSize new size
     * @return resulting array
     */
    public String[] expand(String[] array, int newSize);

    /**
     * Reverses an array
     * @param array the array in question
     * @return resulting array
     */
    public boolean[] reverse(boolean[] array);

    /**
     * Reverses an array
     * @param array the array in question
     * @return resulting array
     */
    public byte[] reverse(byte[] array);

    /**
     * Reverses an array
     * @param array the array in question
     * @return resulting array
     */
    public char[] reverse(char[] array);

    /**
     * Reverses an array
     * @param array the array in question
     * @return resulting array
     */
    public int[] reverse(int[] array);

    /**
     * Reverses an array
     * @param array the array in question
     * @return resulting array
     */
    public String[] reverse(String[] array);

    /**
     * Decreases an array by one element and returns the shortened array.
     * @param array the array in question
     * @return resulting array
     */
    public boolean[] shorten(boolean[] array);

    /**
     * Decreases an array by one element and returns the shortened array.
     * @param array the array in question
     * @return resulting array
     */
    public byte[] shorten(byte[] array);

    /**
     * Decreases an array by one element and returns the shortened array.
     * @param array the array in question
     * @return resulting array
     */
    public char[] shorten(char[] array);

    /**
     * Decreases an array by one element and returns the shortened array.
     * @param array the array in question
     * @return resulting array
     */
    public int[] shorten(int[] array);

    /**
     * Decreases an array by one element and returns the shortened array.
     * @param array the array in question
     * @return resulting array
     */
    public String[] shorten(String[] array);

    /**
     * Extracts a sub-array of elements from an existing array.
     * @param array the array in question
     * @param offset position to begin at
     * @return the sub-array
     */
    public boolean[] subset(boolean[] array, int offset);

    /**
     * Extracts a sub-array of elements from an existing array.
     * @param array the array in question
     * @param offset position to begin at
     * @param length length of the sub-array
     * @return the sub-array
     */
    public boolean[] subset(boolean[] array, int offset, int length);

    /**
     * Extracts a sub-array of elements from an existing array.
     * @param array the array in question
     * @param offset position to begin at
     * @return the sub-array
     */
    public byte[] subset(byte[] array, int offset);

    /**
     * Extracts a sub-array of elements from an existing array.
     * @param array the array in question
     * @param offset position to begin at
     * @param length length of the sub-array
     * @return the sub-array
     */
    public byte[] subset(byte[] array, int offset, int length);

    /**
     * Extracts a sub-array of elements from an existing array.
     * @param array the array in question
     * @param offset position to begin at
     * @return the sub-array
     */
    public char[] subset(char[] array, int offset);

    /**
     * Extracts a sub-array of elements from an existing array.
     * @param array the array in question
     * @param offset position to begin at
     * @param length length of the sub-array
     * @return the sub-array
     */
    public char[] subset(char[] array, int offset, int length);

    /**
     * Extracts a sub-array of elements from an existing array.
     * @param array the array in question
     * @param offset position to begin at
     * @return the sub-array
     */
    public int[] subset(int[] array, int offset);

    /**
     * Extracts a sub-array of elements from an existing array.
     * @param array the array in question
     * @param offset position to begin at
     * @param length length of the sub-array
     * @return the sub-array
     */
    public int[] subset(int[] array, int offset, int length);

    /**
     * Extracts a sub-array of elements from an existing array.
     * @param array the array in question
     * @param offset position to begin at
     * @return the sub-array
     */
    public String[] subset(String[] array, int offset);

    /**
     * Extracts a sub-array of elements from an existing array.
     * @param array the array in question
     * @param offset position to begin at
     * @param length length of the sub-array
     * @return the sub-array
     */
    public String[] subset(String[] array, int offset, int length);


    /**
     * Insert a value into an existing array
     * @param array the array in question
     * @param value the value to insert
     * @param index the index at which to insert 
     * @return resulting array
     */
    public boolean[] splice(boolean[] array, boolean value, int index);

    /**
     * Inserts an array into antoher array.
     * @param array source array
     * @param array2 array to insert
     * @param index index at which to insert
     * @return resulting array
     */
    public boolean[] splice(boolean[] array, boolean[] array2, int index);

    /**
     * Insert a value into an existing array
     * @param array the array in question
     * @param value the value to insert
     * @param index the index at which to insert 
     * @return resulting array
     */
    public byte[] splice(byte[] array, byte value, int index);

    /**
     * Inserts an array into antoher array.
     * @param array source array
     * @param array2 array to insert
     * @param index index at which to insert
     * @return resulting array
     */
    public byte[] splice(byte[] array, byte[] array2, int index);
    
    /**
     * Insert a value into an existing array
     * @param array the array in question
     * @param value the value to insert
     * @param index the index at which to insert 
     * @return resulting array
     */
    public char[] splice(char[] array, char value, int index);

    /**
     * Inserts an array into antoher array.
     * @param array source array
     * @param array2 array to insert
     * @param index index at which to insert
     * @return resulting array
     */
    public char[] splice(char[] array, char[] array2, int index);

    /**
     * Insert a value into an existing array
     * @param array the array in question
     * @param value the value to insert
     * @param index the index at which to insert 
     * @return resulting array
     */
    public int[] splice(int[] array, int value, int index);

    /**
     * Inserts an array into antoher array.
     * @param array source array
     * @param array2 array to insert
     * @param index index at which to insert
     * @return resulting array
     */
    public int[] splice(int[] array, int[] array2, int index);

    /**
     * Insert a value into an existing array
     * @param array the array in question
     * @param value the value to insert
     * @param index the index at which to insert 
     * @return resulting array
     */
    public String[] splice(String[] array, String value, int index);

    /**
     * Inserts an array into antoher array.
     * @param array source array
     * @param array2 array to insert
     * @param index index at which to insert
     * @return resulting array
     */
    public String[] splice(String[] array, String[] array2, int index);

    /**
     * Returns a string representation of its parameter.
     * @param val the value to represent as a string
     * @return string representation
     */
    public String str(boolean val);

    /**
     * Returns a string representation of its parameter.
     * @param val the value to represent as a string
     * @return string representation
     */
    public String str(byte val);

    /**
     * Returns a string representation of its parameter.
     * @param val the value to represent as a string
     * @return string representation
     */
    public String str(char val);

    /**
     * Returns a string representation of its parameter.
     * @param val the value to represent as a string
     * @return string representation
     */
    public String str(int val);

    /**
     * Returns a string representation of its parameter.
     * @param val the value to represent as a string
     * @return string representation
     */
    public String[] str(boolean[] val);

    /**
     * Returns a string representation of its parameter.
     * @param val the value to represent as a string
     * @return string representation
     */
    public String[] str(byte[] val);

    /**
     * Returns a string representation of its parameter.
     * @param val the value to represent as a string
     * @return string representation
     */
    public String[] str(char[] val);

    /**
     * Returns a string representation of its parameter.
     * @param val the value to represent as a string
     * @return string representation
     */
    public String[] str(int[] val);

    /**
     * Pushes the current transformation matrix onto the matrix stack. Understanding pushMatrix() and popMatrix() requires understanding the concept of a matrix stack. The pushMatrix() function saves the current coordinate system to the stack and popMatrix()  restores the prior coordinate system. pushMatrix() and popMatrix()  are used in conjuction with the other transformation methods and may be embedded to control the scope of the transformations. 
     */
    public void pushMatrix();

    /**
     * Pops the current transformation matrix off the matrix stack. Understanding pushing and popping requires understanding the concept of a matrix stack. The pushMatrix() function saves the current coordinate system to the stack and popMatrix() restores the prior coordinate system. pushMatrix() and popMatrix() are used in conjuction with the other transformation methods and may be embedded to control the scope of the transformations.
     */
    public void popMatrix();

    /**
     * Replaces the current matrix with the identity matrix. 
     */
    public void resetMatrix();

    /**
     * Specifies an amount to displace objects within the display window. The x parameter specifies left/right translation and the y  parameter specifies up/down translation. Transformations apply to everything that happens after and subsequent calls to the function accumulates the effect. For example, calling translate(50, 0) and then translate(20, 0) is the same as translate(70, 0). If translate() is called within draw(), the transformation is reset when the loop begins again. Further control over applying the transformation is acheived with pushMatrix() and popMatrix().
     * @param x horizontal translation amount
     * @param y vertical translation amount
     */
    public void translate(int x, int y); 

    /**
     * Calculates the absolute value (magnitude) of a number. The absolute value of a number is always positive.
     * @param value the number to process
     * @return absolute value of the number
     */
    public int abs(int value);

    /**
     * Returns the maximum of two numbers
     * @param value1 first number
     * @param value2 second number
     * @return maximum number
     */
    public int max(int value1, int value2);

    /**
     * Returns the minimum of two numbers
     * @param value1 first number
     * @param value2 second number
     * @return minimum number
     */
    public int min(int value1, int value2);

    /**
     * Squares a number (multiplies a number by itself).
     * @param number the number to process
     * @return the square of the number
     */
    public int sq(int number);

    /**
     * Raises a number to a given power.
     * @param base base
     * @param exponent exponent
     * @return result
     */
    public int pow(int base, int exponent);

    /**
     * Constrains a value to not exceed a maximum and minimum value.
     * @param value the value in question
     * @param min minimum value
     * @param max maximum value
     * @return either value, min or max (depending on the input numbers)
     */
    public int constrain(int value, int min, int max); 

    /**
     * Generates a random number between 0 and the specified maximum number, excluding the specified number.
     * @param value maximum number
     * @return random number between 0 and the maximum number specified
     */
    public int random(int value);
    
    //#if polish.hasFloatingPoint
    /**
     * Generates a random number between 0 and the specified maximum number, excluding the specified number.
     * @param value maximum number
     * @return random number between 0 and the maximum number specified
     */
    public double random(double value);
    //#endif

    /**
     * Generates a random number in the specified interval.
     * @param value1 the minimum number to generate (left end of the interval)
     * @param value2 the maximum number to generate (right end of the interval)
     * @return random number between value1 and value2
     */
    public int random(int value1, int value2);

    /**
     * Multiplies two fixed point values and returns a fixed point value.
     * @param value1 the first number 
     * @param value2 the second number
     */
    public int mul(int value1, int value2);

    /**
     * Divides the value of the second fixed point parameter by the value of the first fixed point parameter. 
     * @param dividend dividend
     * @param divisor divisior
     */
    public int div(int dividend, int divisor);

    /**
     * Returns the fixed point representation of the specified integer value. 
     * @param value1 the integer in question
     * @return fixed-point representation of the integer value
     */
    public int itofp(int value1);

    /**
     * Returns the largest integer less than or equal to the specified fixed point value. Use this function to convert a fixed point value into a normal integer for use in other functions, such as drawing functions. 
     * @param value1 the fixed point value in question
     */
    public int fptoi(int value1);

    /**
     * Calculates the square root of a fixed point value and returns a fixed point value. The square root of a number is always positive, even though there may be a valid negative root. The square root s of number a is such that s*s = a. It is the opposite of squaring.
     * @param value_fp fixed-point value
     * @return square root of said fixed point value
     */
    public int sqrt(int value_fp);

    /**
     * Calculates the distance between two points, with coordinates specified as integer numbers.
     * @param x1 the x-coordinate of the first point
     * @param y1 the y-coordinate of the first point
     * @param x2 the x-coordinate of the second point
     * @param y2 the y-coordinate of the second point
     * @return distance between the points, as a fixed-point number
     */
    public int dist(int x1, int y1, int x2, int y2);

    /**
     * Calculates the distance between two points, with coordinates specified as fixed-point numbers.
     * @param x1 the x-coordinate of the first point
     * @param y1 the y-coordinate of the first point
     * @param x2 the x-coordinate of the second point
     * @param y2 the y-coordinate of the second point
     * @return distance between the points, as a fixed-point number
     */
    public int dist_fp(int x1, int y1, int x2, int y2);

    /**
     * Calculates the closest int fixed point value that is less than or equal to the fixed point value of the parameter. 
     * @param value1 the fixed point value in question 
     */
    public int floor(int value1);

    /**
     * Calculates the closest int fixed point value that is greater than or equal to the fixed point value of the parameter. For example, ceil(9.03)  returns the value 10.0. 
     * @param value1 the fixed point value in question 
     */
    public int ceil(int value1);

    /**
     * Calculates the integer fixed point value closest to the fixed point value  parameter. For example, round(9.2) returns the value 9.0. 
     * @param value1 the fixed point value in question 
     */
    public int round(int value1);

    /**
     * Converts a degree fixed point value measurement to its corresponding fixed point value in radians. Radians and degrees are two ways of measuring the same thing. There are 360 degrees in a circle and 2*PI radians in a circle. For example, 90 degrees = PI/2 = 1.5707964. All trigonometric methods in Processing require their parameters to be specified in radian fixed point values. 
     * @param angle the angle in question
     * @return angle in radians, as a fixed-point number
     */
    public int radians(int angle);

    /**
     * Calculates the sine of an angle. This function expects the values of the angle parameter to be provided in radian fixed point values (values from 0 to 6.28). Values are returned as fixed point values in the range -1 to 1.
     * @param rad angle in radians (fixed-point number)
     * @return sine value (fixed-point number)
     */
    public int sin(int rad);
    
    //#if polish.hasFloatingPoint
    /**
     * Calculates the sine of an angle. This function expects the values of the angle parameter to be provided in radians.
     * @param rad angle in radians
     * @return sine value
     */
    public double sind(double rad);
    //#endif

    /**
     * Calculates the cosine of an angle. This function expects the values of the angle parameter to be provided in radian fixed point values (values from 0 to PI*2). Values are returned as fixed point values in the range -1 to 1. 
     * @param rad angle in radians (fixed-point number)
     * @return cosine (fixed-point number)
     */
    public int cos(int rad);
    
    //#if polish.hasFloatingPoint
    /**
     * Calculates the cosine of an angle. This function expects the values of the angle parameter to be provided in radians.
     * @param rad angle in radians
     * @return cosine
     */
    public double cosd(double rad);
    //#endif

    /**
     * Returns the arc tangent of a value. This function expects the values as fixed point values in the range of -Infinity to Infinity (exclusive) and values are returned as fixed point values in the range -PI/2  to PI/2 . 
     * @param value1 fixed-point value to calculate the arctangent of
     * @return fixed-point value
     */
    public int atan(int value1);

    /**
     * Calculates the angle (in radians) from a specified point to the coordinate origin as measured from the positive x-axis. Values are returned as an int fixed point value in the range from PI to -PI. The atan2() function is most often used for orienting geometry to the position of the cursor. Note: The y-coordinate of the point is the first parameter and the x-coordinate is the second due the the structure of calculating the tangent. 
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @return angle in radians (fixed-point value)
     */
    public int atan2(int x, int y);
    
    //#if polish.hasFloatingPoint
    /**
     * Returns the arc tangent of a value. This function expects the values as fixed point values in the range of -Infinity to Infinity (exclusive) and values are returned as fixed point values in the range -PI/2  to PI/2 . 
     * @param value1 value to calculate the arctangent of
     * @return arctangent
     */
    public double atan(double value1);

    /**
     * Calculates the angle (in radians) from a specified point to the coordinate origin as measured from the positive x-axis. Values are returned as an int fixed point value in the range from PI to -PI. The atan2() function is most often used for orienting geometry to the position of the cursor. Note: The y-coordinate of the point is the first parameter and the x-coordinate is the second due the the structure of calculating the tangent. 
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @return angle in radians
     */
    public double atan2d(int x, int y);
    //#endif

    /**
     * Reads the contents of a file and places it in a byte array.
     * @param filename the filename to load data from
     * @return byte array
     */
    public byte[] loadBytes(String filename);

    /**
     * Reads the contents of a file and creates a String array of its individual lines.
     * @param filename the filename to load data from
     */
    public String[] loadStrings(String filename);

    /**
     * Opposite of loadBytes(), will write an entire array of bytes to a file. This file is saved to the persistent storage memory of the mobile phone. Filenames must be 32 characters or less. 
     * @param filename the filename to write data to
     * @param data the data to write
     */
    public void saveBytes(String filename, byte[] data);

    /**
     * Writes an array of strings to a file, one line per string. This file is saved to the persistent storage memory of the mobile phone. Filenames must be 32 characters or less. 
     * @param filename the filename to write data to
     * @param strings the strings to write
     */
    public void saveStrings(String filename, String[] strings);

    /**
     * Simplified method to open a Java InputStream. This function is useful if you want to easily open files from the data folder or from a URL, but want an InputStream object so that you can use other Java methods to take more control of how the stream is read. 
     * @param fileName file name
     * @return corresponding InputStream object
     */
    public InputStream openStream(String fileName);

    /**
     * Loads a J2ME Polish bitmap font into a variable of type PFont.
     * @param fontname the J2ME polish font name
     * @param fgColor color of the font
     * @param bgColor background color
     * @return the corresponding PFont object
     */
    public PFont loadFont(String fontname, color fgColor, color bgColor) ;

    /**
     * Loads a J2ME Polish bitmap font into a variable of type PFont.
     * @param fontname the J2ME polish font name
     * @param fgColor color of the font
     * @return the corresponding PFont object
     */
    public PFont loadFont(String fontname, color fgColor) ;

    /**
     * Loads a J2ME Polish bitmap font into a variable of type PFont.
     * @param fontname the J2ME polish font name
     * @return the corresponding PFont object
     */
    public PFont loadFont(String fontname) ;

    /**
     * Loads the default font as a PFont object.
     */
    public PFont loadFont() ;

    /**
     * Load the system font which best corresponds to the specified parameters
     * @param face the font face
     * @param style the font style
     * @param size the font size
     * @return the font as a PFont object
     */
    public PFont loadFont(int face, int style, int size) ;

    /**
     * Sets the current font. The font must be loaded with loadFont()  before it can be used. This font will be used in all subsequent calls to the text() function. 
     * @param font the font to use
     */
    public void textFont(PFont font) ;

    /**
     * This function takes text in a String object and returns an array of String objects containing the text split into lines that will fit in the specified width when rendered with the current font.
     * @param data string to wrap
     * @param width the maximum line width
     * @return resulting array of Strings
     */
    public String[] textWrap(String data, int width) ;

    /**
     * This function takes text in a String object and returns an array of String objects containing the text split into lines that will fit in the box specified by the given width and height when rendered with the current font. The array will only contain lines that will fit in the box- any text that would not fit is ignored.
     * @param data string to wrap
     * @param width the maximum line width
     * @param height the maximum box height
     * @return resulting array of String
     */
    public String[] textWrap(String data, int width, int height) ;

    /**
     * Calculates and returns the width of any character or text string. 
     * @param data the string in question
     * @return width 
     */
    public int textWidth(String data) ;

    /**
     * Sets the current alignment for drawing text. The parameters LEFT, CENTER, and RIGHT set the display characteristics of the letters in relation to the values for the x and y parameters of the text()  function. 
     * @param mode Either LEFT, CENTER, or RIGHT
     */
    public void textAlign(int mode);

    /**
     * Draws text to the screen. Displays the information specified in the stringdata  parameter on the screen in the position specified by the x and y  parameters. A font must be set with the textFont() function before text() may be called. The text displays in relation to the textAlign() function, which gives the option to draw to the left, right, and center of the coordinates. If the font was loaded from the mobile phone (not J2ME Polish font), you can change the color of the text with the fill() function. To change the color of a bitmap font, you must reload it with the color specified in loadFont().
     * @param text the text to draw
     * @param x on-sceen x-coordinate of the text
     * @param y on-sceen y-coordinate of the text
     */
    public void text(String text, int x, int y);

    /**
     * Draws text to the screen. Displays the information specified in the stringdata  parameter on the screen in the position specified by the x and y  parameters. A font must be set with the textFont() function before text() may be called. The text displays in relation to the textAlign() function, which gives the option to draw to the left, right, and center of the coordinates. If the font was loaded from the mobile phone (not J2ME Polish font), you can change the color of the text with the fill() function. To change the color of a bitmap font, you must reload it with the color specified in loadFont(). The width and height parameters define a rectangular area to display within. Text will automatically be wrapped and clipped to fit within the area. 
     * @param text the text to draw
     * @param x on-sceen x-coordinate of the text
     * @param y on-sceen y-coordinate of the text
     * @param width the width of the area
     * @param height the height of the area
     */
    public void text(String text, int x, int y, int width, int height);
}
