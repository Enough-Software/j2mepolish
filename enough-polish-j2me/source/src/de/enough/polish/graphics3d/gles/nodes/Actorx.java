//#condition polish.javapackage.jsr239

package de.enough.polish.graphics3d.gles.nodes;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL;

import de.enough.polish.math.FP;
import de.enough.polish.util.ArrayList;

/**
 * Node abstraction. Base element of GLES scenegraph.
 * <p>
 * Node uses fixed point numbers according to GLES specification. 16 bit mantissa, 16 bit exponent.
 * 
 * @author Anders Bo Pedersen, anders@wicore.dk
 */
public abstract class Actorx 
{
	protected int tXx = 0, tYx = 0, tZx = 0;
	
	protected int sXx = FP.FIX_ONE, sYx = FP.FIX_ONE, sZx = FP.FIX_ONE;
	
	protected int rXx = 0, rYx = 0, rZx = 0;
	
	protected GL10 gl10;
	
	protected Actorx parent;
	
	protected ArrayList children = new ArrayList(0);
	
	public Actorx(GL gl) 
	{
		this.gl10 = (GL10)gl;
	}
	
	public void update(long deltaMillis) 
	{
		updateActor();
		
		for(int i = this.children.size(); --i>=0;)
			((Actorx)this.children.get(i)).update(deltaMillis);
	}
	
	protected abstract void updateActor();
	
	public void render()
	{
		this.gl10.glPushMatrix();
		
		this.gl10.glScalex(this.sXx, this.sYx, this.sZx);
		
		this.gl10.glRotatex(this.rXx, 1, 0, 0);
		this.gl10.glRotatex(this.rYx, 0, 1, 0);
		this.gl10.glRotatex(this.rZx, 0, 0, 1);
		
		this.gl10.glTranslatex(this.tXx, this.tYx, this.tZx);
		
		//#mdebug debug
		/*
		System.out.println(this.toString());
		 */
		//#enddebug
		
		renderActor();

		for(int i = this.children.size(); --i>=0;)
			((Actorx)this.children.get(i)).render();
			
		this.gl10.glPopMatrix();
	}
	
	protected abstract void renderActor();
	
	public int getNumChildren()
	{
		if(null != this.children)
			return this.children.size();
		else
			return 0;
	}
	
	public void addChild(Actorx node)
	{
		if(null != node)
		{
			node.parent = this;
			this.children.add(node);
		}
	}
	
	public void removeChild(Actorx node)
	{
		if(null != node)
			if(this.children.contains(node))
				this.children.remove(node);
	}
	
	public void removeChild(int childNum)
	{
		if(childNum >= 0 && childNum < this.children.size())
			this.children.remove(childNum);
	}
	
	public Actorx getChild(int childNum)
	{
		if(childNum >= 0 && childNum < this.children.size())
			return (Actorx)this.children.get(childNum);

		return null;
	}
	
	public void setTranslationx(int x, int y, int z)
	{
		this.tXx = x;
		this.tYx = y;
		this.tZx = z;
	}
	
	public void setScalex(int x, int y, int z)
	{
		this.sXx = x;
		this.sYx = y;
		this.sZx = z;
	}
	
	public void setRotateXx(int x)
	{
		this.rXx = x;
	}
	
	public void setRotateYx(int y)
	{
		this.rYx = y;
	}
	
	public void setRotateZx(int z)
	{
		this.rZx = z;
	}
	
	//#mdebug debug
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append("Node:\n");
		sb.append("Translation:  X:" + FP.fixToFloat(this.tXx) + " Y:" + FP.fixToFloat(this.tYx) + " Z:"+FP.fixToFloat(this.tZx)+"\n");
		sb.append("Scale:  X:" + FP.fixToFloat(this.sXx) + " Y:" + FP.fixToFloat(this.sYx) + " Z:"+FP.fixToFloat(this.sZx)+"\n");
		sb.append("Rotation:  X:" + FP.fixToFloat(this.rXx) + " Y:" + FP.fixToFloat(this.rYx) + " Z:"+FP.fixToFloat(this.rZx)+"\n");
		
		return sb.toString();
	}
	//#enddebug
}
