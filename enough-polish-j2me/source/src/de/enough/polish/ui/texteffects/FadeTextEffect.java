//#condition polish.usePolishGui

/**
 * 
 */
package de.enough.polish.ui.texteffects;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Color;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextEffect;
import de.enough.polish.util.DrawUtil;

/**
 * <p>This is a simple fade in/out effect.</p>
 * <p>Activate the fade text effect by specifying <code>text-effect: fade;</code> in your polish.css file.
 *    You can finetune the effect with following attributes:
 * </p>
 * <ul>
 * 	 <li><b>text-fade-in-color</b>: The color, which the text has, if it is faded in.</li>
 * 	 <li><b>text-fade-out-color</b>: The color, which the text has, if it is faded out.</li>
 * 	 <li><b>text-fade-delay</b>: The delay (in animation steps) before the animation starts.</li>
 * 	 <li><b>text-fade-duration-in</b>: The time (in animation steps), which the text stays faded in.</li>
 * 	 <li><b>text-fade-duration-out</b>: The time (in animation steps), which the text stays faded out.</li>
 * 	 <li><b>text-fade-steps</b>: The time (in animation teps), which the fade-effect lasts.</li>
 * 	 <li><b>text-fade-mode</b>: The fade mode can be fadein, fadeout or loop, default is loop.</li>
 * </ul>
 * 
 *
 * <p>Copyright Enough Software 2006 - 2009</p>
 *    
 * @author Simon Schmitt
 *
 */
public class FadeTextEffect extends TextEffect {
	
	private transient final DrawUtil.FadeUtil fader;
	
	private String lastText;
	
	/**
	 * Creates a new FadeTextEffect 
	 */
	public FadeTextEffect()
	{
		this.fader=new DrawUtil.FadeUtil();
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawString(java.lang.String, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void drawString(String text, int textColor, int x, int y,
			int orientation, Graphics g) {
		
		// intialisation
		if (this.lastText!=text || text==null){ // TODO: check for other changes
			this.fader.changed=true;
			this.lastText=text;
		}
		// do not draw if you do not know the color
		if (this.fader.changed){
			this.fader.step();
			//return;
		}
		
		if (text==null) {
			return;
		}
		// draw the current state
		g.setColor(this.fader.cColor);
		g.drawString(text,x,y, orientation);
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#animate()
	 */
	public boolean animate() {
		boolean animated = super.animate();
		
		if (this.lastText == null) {
			return animated;
		}
		
		return this.fader.step() | animated;
		
	}
	
	public void setStyle(Style style, boolean resetStyle) {
		super.setStyle(style, resetStyle);
		
		//#if polish.css.text-fade-out-color
			Color startColorObj = style.getColorProperty( "text-fade-out-color" );
			if (startColorObj != null) {
				this.fader.startColor = startColorObj.getColor();
			}
		//#endif
		if (resetStyle) {
			this.fader.endColor = style.getFontColor();
		}
		//#if polish.css.text-fade-in-color
			Color endColorObj = style.getColorProperty( "text-fade-in-color" );
			if (endColorObj != null) {
				this.fader.endColor = endColorObj.getColor();
			}
		//#endif
		//#if polish.css.text-fade-delay
			Integer delayInt = style.getIntProperty("text-fade-delay");
			if (delayInt != null ) {
				this.fader.delay = delayInt.intValue();
			}
		//#endif
		//#if polish.css.text-fade-steps
			Integer fadeTimeInt = style.getIntProperty("text-fade-steps");
			if (fadeTimeInt != null ) {
				this.fader.stepsIn = fadeTimeInt.intValue();
				this.fader.stepsOut=this.fader.stepsIn;
			}
		//#endif
		//#if polish.css.text-fade-duration-in
			Integer diInt = style.getIntProperty("text-fade-duration-in");
			if (diInt != null ) {
				this.fader.sWaitTimeIn = diInt.intValue();
			}
		//#endif
		//#if polish.css.text-fade-duration-out
			Integer doInt = style.getIntProperty("text-fade-duration-out");
			if (doInt != null ) {
				this.fader.sWaitTimeOut = doInt.intValue();
			}
		//#endif
		//#if polish.css.text-fade-mode
			String tmpStr = style.getProperty("text-fade-mode");
			if (tmpStr!=null){
				if (tmpStr.equals("fadein")){
					this.fader.mode=DrawUtil.FadeUtil.FADE_IN;
				} else if (tmpStr.equals("fadeout")){
					this.fader.mode=DrawUtil.FadeUtil.FADE_OUT;
				} else {
					this.fader.mode=DrawUtil.FadeUtil.FADE_LOOP;
				}
			}
		//#endif	
	}

}
