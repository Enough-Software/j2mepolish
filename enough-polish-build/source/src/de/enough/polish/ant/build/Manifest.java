/*
 * Created on 11-Sep-2005 at 12:09:58.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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
package de.enough.polish.ant.build;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import de.enough.polish.Attribute;
import de.enough.polish.Environment;

public class Manifest {
	
    public static final int MAX_LINE_LENGTH = 72;
    public static final String EOL = "\r\n";
    public static final int MAX_SECTION_LENGTH = MAX_LINE_LENGTH - EOL.length(); // max length when there is a line break 

//	private final Environment environment;
	private final String encoding;
	private Attribute[] attributes;

	public Manifest( Environment environment, String encoding ) {
//		this.environment = environment;
		this.encoding = encoding;
	}
  
  public void addAttribute( Attribute attribute ) {
    if (this.attributes != null) {
      Attribute[] tmpArray = new Attribute[this.attributes.length + 1];
      System.arraycopy(this.attributes, 0, tmpArray, 0, this.attributes.length);
      tmpArray[this.attributes.length] = attribute;
      this.attributes = tmpArray;
    }
    else {
      this.attributes = new Attribute[1];
      this.attributes[0] = attribute;
    }
  }
	
	public void setAttributes( Attribute[] attributes ) {
		this.attributes = attributes;
	}
	
	public void write( File file ) 
	throws IOException
	{
		File parent = file.getParentFile();
		if ( !parent.exists() ) {
			parent.mkdirs();
		}
		PrintWriter writer = new PrintWriter( 
				new OutputStreamWriter( new FileOutputStream( file ), this.encoding ) );
		write( writer );
		writer.close();
	}
	
	public void write( PrintWriter writer )
	throws IOException 
	{
		if (this.attributes == null || this.attributes.length == 0) {
			throw new IllegalStateException("Unable to store manifest without attributes!");
		}
		for (int i = 0; i < this.attributes.length; i++) {
			Attribute attribute = this.attributes[i];
			String line = attribute.getName() + ": " + attribute.getValue();
			while (line.getBytes().length > MAX_LINE_LENGTH) {
				// try to find a MAX_LINE_LENGTH byte section
				int breakIndex = MAX_SECTION_LENGTH;
				String section = line.substring(0, breakIndex);
				while (section.getBytes().length > MAX_SECTION_LENGTH && breakIndex > 0) {
					breakIndex--;
					section = line.substring(0, breakIndex);
				}
				if (breakIndex == 0) {
					throw new IOException("Unable to write manifest line " + attribute.getName() + ": " + attribute.getValue() );
				}
				writer.print(section + EOL);
				line = " " + line.substring(breakIndex);
			}
			writer.print(line + EOL);
		}
	}

}
