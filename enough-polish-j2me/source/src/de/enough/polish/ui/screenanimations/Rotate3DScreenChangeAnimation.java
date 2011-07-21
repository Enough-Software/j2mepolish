//#condition polish.usePolishGui && polish.api.3dapi

/*
 * Created on 29-May-2005 at 18:44:00.
 * 
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
package de.enough.polish.ui.screenanimations;

import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.m3g.Appearance;
import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.Image2D;
import javax.microedition.m3g.Light;
import javax.microedition.m3g.Material;
import javax.microedition.m3g.PolygonMode;
import javax.microedition.m3g.Texture2D;
import javax.microedition.m3g.Transform;
import javax.microedition.m3g.TriangleStripArray;
import javax.microedition.m3g.VertexArray;
import javax.microedition.m3g.VertexBuffer;

import de.enough.polish.ui.ScreenChangeAnimation;
import de.enough.polish.ui.Style;

/**
 * <p></p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2009</p>
 * <pre>
 * history
 *        29-May-2005 - rob creation
 * </pre>
 * @author Tim Muders, j2mepolish@enough.de
 */
public class Rotate3DScreenChangeAnimation extends ScreenChangeAnimation {
	
	private float angle;
	private Graphics3D g3D;
	private javax.microedition.m3g.Background background;
	private Camera camera;
	private Light light;
	private TriangleStripArray triangleStripArray;
	private VertexBuffer vertexBuffer;
	private Appearance appearance;

	/**
	 * Creates a new rotate animation 
	 */
	public Rotate3DScreenChangeAnimation() {
		super();
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#show(de.enough.polish.ui.Style, javax.microedition.lcdui.Display, int, int, javax.microedition.lcdui.Image, javax.microedition.lcdui.Image, de.enough.polish.ui.Screen)
	 */
	protected void onShow(Style style, Display dsplay, int width, int height,
			Displayable lstDisplayable, Displayable nxtDisplayable, boolean isForward  ) 
	{
		super.onShow(style, dsplay, width, height, lstDisplayable,
				nxtDisplayable, isForward );
		// initialization of 3D settings:
	    // Hole grafischen Context Graphics3D
		this.g3D = Graphics3D.getInstance();

	    // Setze Kamera
		this.camera = new Camera();
	    this.camera.setPerspective(5F, (float)width/ (float)height, 1F, 50F);

	    // Setze Beleuchtungs-Modus
	    this.light = new Light();
	    this.light.setMode(Light.AMBIENT);

	    // Eck-Koordinaten des Wuerfels
	    byte[] verticies = {
	      1, 1, 1, -1, 1, 1, 1, -1, 1, -1, -1, 1, // vorne
	      -1, 1, -1, 1, 1, -1, -1, -1, -1, 1, -1, -1, // hinten
	      -1, 1, 1, -1, 1, -1, -1, -1, 1, -1, -1, -1, // links
	      1, 1, -1, 1, 1, 1, 1, -1, -1, 1, -1, 1, // rechts
	      1, 1, -1, -1, 1, -1, 1, 1, 1, -1, 1, 1, // oben
	      1, -1, 1, -1, -1, 1, 1, -1, -1, -1, -1, -1 // unten
	    };

	    // Eck-Koordinaten in VertexArray speichern
	    VertexArray vertArray = new VertexArray(verticies.length / 3, 3, 1);
	    vertArray.set(0, verticies.length / 3, verticies);

	    // Textur-Koordinaten
	     byte[] tex = {
	       1, 0, 0, 0, 1, 1, 0, 1, // vorne
	       1, 0, 0, 0, 1, 1, 0, 1, // hinten
	       1, 0, 0, 0, 1, 1, 0, 1, // links
	       1, 0, 0, 0, 1, 1, 0, 1, // rechts
	       1, 0, 0, 0, 1, 1, 0, 1, // oben
	       1, 0, 0, 0, 1, 1, 0, 1  // unten
	     };

	    // Textur-Koord. in einem VertexArray speichern
	    VertexArray texArray = new VertexArray(tex.length / 2, 2, 1);
	    texArray.set(0, tex.length / 2, tex);

	    // Referenzen auf Eck- und Textur-Koord. in VertexBuffer
	    this.vertexBuffer = new VertexBuffer();
	    this.vertexBuffer.setPositions(vertArray, 1.0F, null);
	    this.vertexBuffer.setTexCoords(0, texArray, 1.0F, null);

	    // Laenge jeder Wuerfel-Seite
	    int[] stripLen = {4, 4, 4, 4, 4, 4};
	    this.triangleStripArray = new TriangleStripArray(0, stripLen);

	    Texture2D texture2D = null;
	    //TODO check if texture format is okay
	    Integer maxTexDimensionInt = (Integer) Graphics3D.getProperties().get("maxTextureDimension");
	    int maxWidth = width;
	    int maxHeight = height;
	    if (maxTexDimensionInt != null) {
	    	int texDimension = maxTexDimensionInt.intValue();
	    	maxWidth = Math.min( width, texDimension );
	    	maxHeight = Math.min( height, texDimension );
	    }
	    int temp = 64;
	    while ( temp < maxWidth ) {
	    	temp *= 2;
	    }
	    maxWidth = temp / 2;
	    temp = 64;
	    while ( temp < maxHeight ) {
	    	temp *= 2;
	    }
	    maxHeight = temp / 2;
	    //#debug
	    System.out.println("Textture image dimension: " + maxWidth + ", " + maxHeight );
	    //Image textureImage = Image.createImage( maxWidth, maxHeight );
	    Image textureImage = Image.createImage( 128, 128 );
	    Graphics g = textureImage.getGraphics();
	    g.drawImage( this.nextCanvasImage, 0, 0, Graphics.TOP | Graphics.LEFT );
	    texture2D = new Texture2D(new Image2D( Image2D.RGB, textureImage ) );

	    // Definiere Material
	    Material material = new Material();
	    material.setColor(Material.AMBIENT, 0xFFFFFFFF);

	    // Aktiviere Perspective Correct Textur Mapping
	    PolygonMode polygonMode = new PolygonMode();
	    polygonMode.setPerspectiveCorrectionEnable(true);

	    // Referenzen auf Textur, Material und PolygonMode Eigenschaften in Appearance speichern
	    this.appearance = new Appearance();
	    this.appearance.setTexture(0, texture2D);
	    this.appearance.setMaterial(material);
	    this.appearance.setPolygonMode(polygonMode);

	    // Hintergrund fuer die Szene definieren
	    this.background = new javax.microedition.m3g.Background();
	    this.background.setColor(0x000000); 
	}
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ScreenChangeAnimation#animate()
	 */
	protected boolean animate() {
	    this.angle += 4;
		return (this.angle < 90);
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
	 */
	public void paintAnimation(Graphics g) {
		// Zeichne Szene auf dem Graphics Objekt
	    this.g3D.bindTarget( g );
	    // Loesche Hintergrund
	    this.g3D.clear( this.background );

	    // Bestimme Position fuer Kamera und Licht
	    Transform transform = new Transform();
	    transform.postTranslate(0.0F, 0.0F, 50F);

	    // Setze Kamera
	    this.g3D.setCamera(this.camera, transform);
	    this.g3D.resetLights();
	    // Fuege in die Szene Licht hinzu
	    this.g3D.addLight(this.light, transform);

	    transform.setIdentity();
	    // Bestimme Position fuer 3D-Objekt
	    transform.postTranslate(0.0F, 0.0F, 1.0F);
	    // rotiere den Wuerfel
	    transform.postRotate(this.angle, 1.0F, 1.0F, 1.0F);

	    // und auf dem Bildschirm rendern
	    this.g3D.render( this.vertexBuffer, this.triangleStripArray, this.appearance, transform, -1 );
	    // Graphics Objekt wieder frei geben
	    this.g3D.releaseTarget();
	}

}
