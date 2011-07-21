/*
 * Created on 01-Mar-2004 at 15:27:17.
 *
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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
package de.enough.polish.preprocess.css;


/**
 * <p>Respresents a J2ME font.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        01-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class FontConverter extends Converter {

	
	private String face  = "Font.FACE_SYSTEM";
	private String style = "Font.STYLE_PLAIN";
	private String size  = "Font.SIZE_MEDIUM";
	
	/**
	 * Creates a new font converter.
	 */
	public FontConverter() {
		// initialisation is done with the setter methods
	}
	

	/**
	 * Retrieves the statement needed to create this font.
	 * 
	 * @return the new statement for the creation of this font.
	 */
	public String createNewStatement() {
		StringBuffer fontCode = new StringBuffer();
		
		// first param is the font face:
		fontCode.append("Font.getFont( ")
				.append( this.face )
				.append(", ")
				.append( this.style )
				.append(", ")
				.append( this.size )
				.append(")");
		
		return fontCode.toString(); 
	}
	
	/**
	 * @param face The face to set.
	 */
	public void setFace(String face) {
		this.face = strip(face);
	}
	
	/**
	 * @param value
	 * @return
	 */
	private String strip(String value)
	{
		if (value.startsWith("new Integer(")) {
			value = value.substring("new Integer(".length(), value.length() - 1);
		}
		return value;
	}


	/**
	 * @param size The size to set.
	 */
	public void setSize(String size) {
		this.size = strip(size);
	}
	/**
	 * @param style The style to set.
	 */
	public void setStyle(String style) {
		this.style = strip(style);
	}

}
