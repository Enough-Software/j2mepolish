//#condition polish.javapackage.jsr184

package de.enough.polish.graphics3d.m3g.utils;

import javax.microedition.m3g.Appearance;
import javax.microedition.m3g.IndexBuffer;
import javax.microedition.m3g.Light;
import javax.microedition.m3g.Material;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.PolygonMode;
import javax.microedition.m3g.TriangleStripArray;
import javax.microedition.m3g.VertexArray;
import javax.microedition.m3g.VertexBuffer;

/**
 * Factory class for common M3G nodes and shapes.
 * 
 * @author Anders Bo Pedersen, anders@wicore.dk
 */
public class NodeFactory
{
    /*
     * Quad constants
     */
	
	//Quad vertices
    public static final byte[] QUAD_VERTS = 
    {
        1, 1, 0,  -1, 1, 0,   1,-1, 0,  -1,-1, 0
	};

    //Quad implied verts index
    public static final int[] QUAD_INDEX = 
    {  
    	4
    };
    
    //Quad normals
    public static final byte[] QUAD_NORMS = 
    {  
        0, 0, 127,    0, 0, 127,    0, 0, 127,    0, 0, 127,
       };

    //Quad texture coordinates
    public static final byte[] QUAD_TEXT_COORDS = 
    {  
        1, 0,       0, 0,       1, 1,       0, 1,
    };
    
	/**
     * Creates a quad mesh using argument Apperance object
	 * 
	 * @return Mesh a quad
	 */
	public static Mesh createQuad(boolean useNormals, boolean useTextCords)
	{
		//create quad vertex buffer
		VertexBuffer vertices = new VertexBuffer();
		
        // create a VertexArray for vertices
	    VertexArray vertArray = new VertexArray(QUAD_VERTS.length / 3, 3, 1);
	    vertArray.set(0, QUAD_VERTS.length/3, QUAD_VERTS);
	    vertices.setPositions(vertArray, 1.0f, null);      // unit scale, zero bias
	    
	    // IndexBuffer for quad
	    IndexBuffer triangles = new TriangleStripArray( 0, QUAD_INDEX );
	    
	    if(useNormals)
	    {
	    	// create a VertexArray for surface normals
	    	VertexArray normArray = new VertexArray(QUAD_NORMS.length / 3, 3, 1);
	    	normArray.set(0, QUAD_NORMS.length/3, QUAD_NORMS);
	    	vertices.setNormals(normArray);
	    }

	    if(useTextCords)
	    {
	    	// create a VertexArray for texture coordinates
	    	VertexArray texArray = new VertexArray(QUAD_TEXT_COORDS.length / 2, 2, 1);
	    	texArray.set(0, QUAD_TEXT_COORDS.length/2, QUAD_TEXT_COORDS);
	    	vertices.setTexCoords(0, texArray, 1.0f, null);    // unit scale, zero bias
	    }
        
        return new Mesh(vertices, triangles, null);
	}
	
    /*
     * Cube constants
     */
	
	//Cube vertices
    public static final byte[] CUBE_VERTS = 
    {
        -1, -1,  1,   1, -1,  1,  -1,  1,  1,   1,  1,  1, // front
        1, -1, -1,  -1, -1, -1,   1,  1, -1,  -1,  1, -1,  // back
        1, -1,  1,   1, -1, -1,   1,  1,  1,   1,  1, -1,  // right
       -1, -1, -1,  -1, -1,  1,  -1,  1, -1,  -1,  1,  1,  // left
       -1,  1,  1,   1,  1,  1,  -1,  1, -1,   1,  1, -1,  // top
       -1, -1, -1,   1, -1, -1,  -1, -1,  1,   1, -1,  1   // bottom
	};

    //Cube implied verts index
    public static final int[] CUBE_INDEX = 
    {  
    	4, 4, 4, 4, 4, 4
    };
    
    //Cube normals
    public static final byte[] CUBE_NORMS = 
    {  
        0, 0, 127,		0, 0, 127,   	0, 0, 127,   	0, 0, 127,   // front
        0, 0, -128,   	0, 0, -128,   	0, 0, -128,   	0, 0, -128,  // back
        127, 0, 0,   	127, 0, 0,   	127, 0, 0,   	127, 0, 0,   // right
        -128, 0, 0,   	-128, 0, 0,   	-128, 0, 0,   	-128, 0, 0,  // left
        0, 127, 0,   	0, 127, 0,   	0, 127, 0,   	0, 127, 0,   // top
        0, -128, 0,   	0, -128, 0,   	0, -128, 0,  	0, -128, 0,  // bottom
    };

    //Cube texture coordinates
    public static final byte[] CUBE_TEXT_COORDS = 
    {  
        0, 1,   1, 1,   0, 0,   1, 0,   // front
        0, 1,   1, 1,   0, 0,   1, 0,   // back
        0, 1,   1, 1,   0, 0,   1, 0,   // right
        0, 1,   1, 1,   0, 0,   1, 0,   // left
        0, 1,   1, 1,   0, 0,   1, 0,   // top
        0, 1,   1, 1,   0, 0,   1, 0,   // bottom
    };
    
	/**
     * Creates a Cube mesh
	 * 
	 * @return Mesh a cube
	 */
	public static Mesh createCube(boolean useNormals, boolean useTextCords)
	{
		//create quad vertex buffer
		VertexBuffer vertices = new VertexBuffer();
		
        // create a VertexArray for vertices
	    VertexArray vertArray = new VertexArray(CUBE_VERTS.length / 3, 3, 1);
	    vertArray.set(0, CUBE_VERTS.length/3, CUBE_VERTS);
	    vertices.setPositions(vertArray, 1.0f, null);      // unit scale, zero bias
	    
	    // IndexBuffer for quad
	    IndexBuffer triangles = new TriangleStripArray( 0, CUBE_INDEX );
	    
	    if(useNormals)
	    {
	    	// create a VertexArray for surface normals
	    	VertexArray normArray = new VertexArray(CUBE_NORMS.length / 3, 3, 1);
	    	normArray.set(0, CUBE_NORMS.length/3, CUBE_NORMS);
	    	vertices.setNormals(normArray);
	    }

	    if(useTextCords)
	    {
	    	// create a VertexArray for texture coordinates
	    	VertexArray texArray = new VertexArray(CUBE_TEXT_COORDS.length / 2, 2, 1);
	    	texArray.set(0, CUBE_TEXT_COORDS.length/2, CUBE_TEXT_COORDS);
	    	vertices.setTexCoords(0, texArray, 1.0f, null);    // unit scale, zero bias
	    }
        
        return new Mesh(vertices, triangles, null);
	}
	
	/**
	 * Creates a ambient light source using argument settings
	 * 
	 * @param color
	 * @param intensity
	 * @return a ambient light node
	 */
	public static Light createAmbientLight(int color, float intensity)
	{
		Light l = new Light();
		l.setMode(Light.AMBIENT);
		l.setColor(color);
		l.setIntensity(intensity);
		return l;
	}
	
	/**
	 * Creates a directional light source using argument settings
	 * 
	 * @param color
	 * @param intensity
	 * @param directionVec
	 * @return a directional light node
	 */
	public static Light createDirectionalLight(int color, float intensity, float[] directionVec)
	{
		Light l = new Light();
		//l.setMode(Light.DIRECTIONAL); // set by default
		l.setColor(color);
		l.setIntensity(intensity);
		return l;
	}
	
	/**
	 * Creates a default Appearance object with lighting turned on.
	 *  
	 * @return default apperance
	 */
	public static Appearance getDefaultApperance()
	{
        // default surface specs
        Material planeMaterial = new Material();
        /* Default specs:
        planeMaterial.setVertexColorTrackingEnable(false);
        planeMaterial.setColor(Material.AMBIENT, 0x00333333);
        planeMaterial.setColor(Material.DIFFUSE, 0xFFCCCCCC);
        planeMaterial.setColor(Material.EMISSIVE, 0x00000000);
        planeMaterial.setColor(Material.SPECULAR, 0x00000000);
        planeMaterial.setShininess(.0f);
        */
        
        // Plane polygn mode, ensure precise rendering of texture surface
        PolygonMode polymode = new PolygonMode();
        /* Default specs:        
        polymode.setCulling(PolygonMode.CULL_BACK);
        polymode.setWinding(PolygonMode.WINDING_CCW);
        polymode.setShading(PolygonMode.SHADE_SMOOTH);
        polymode.setTwoSidedLightingEnable(false);
        polymode.setLocalCameraLightingEnable(false);
        polymode.setPerspectiveCorrectionEnable(false);
        */
        
        //create Plane apperance
    	Appearance appearance = new Appearance();
    	appearance.setMaterial(planeMaterial);
    	appearance.setPolygonMode(polymode);
    	/* Default specs:
    	appearance.setLayer(0);
    	appearance.setCompositingMode(null);
    	appearance.setTexture(0, null); // tex disabled
    	appearance.setFog(null);
    	*/
    	
    	return appearance;
	}
}
