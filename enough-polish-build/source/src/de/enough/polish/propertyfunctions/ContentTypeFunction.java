/*
 * Created on 14-Dec-2005 at 18:39:54.
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
package de.enough.polish.propertyfunctions;

import java.util.HashMap;
import java.util.Map;

import de.enough.polish.BuildException;

import de.enough.polish.Environment;
import de.enough.polish.util.StringUtil;

/**
 * <p>The "contenttype" function gets a content type like "video/mp4" or "audio/amr" and returns the correct content type name for the current target device.</p>
 * <p>The current target device needs to define the appropriate mmapi.protocol.* properties.
 * <p>Sample usage for video playback with the MMAPI:
 * <pre>
 *   Player player = null;
 * //#= player = Manager.createPlayer( is, "${ contentype(video/mp4) }" );
 * </pre>
 * </p>
 * <p>The above example is equivalent with the following:
 * <pre>
 *   Player player = null;
 * //#= player = Manager.createPlayer( is, "${ contentype(video/mpeg4) }" );
 * </pre>
 * </p>
 * <p>Sample usage for audio playback over the http protocol with the MMAPI:
 * <pre>
 *   Player player = null;
 * //#= player = Manager.createPlayer( "http://myserver.com/sound.mp3", "${ contentype(audio/mp3, http) }" );
 * </pre>
 * </p>
 * <p>When no protocol is specified, the "http" protocol is assumed.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        14-Dec-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ContentTypeFunction extends PropertyFunction {
	
	private final static Map TYPES_MAP = new HashMap();
	static {
		addTypes( new String[]{ "audio/3gpp", "audio/3gp" } );
		addTypes( new String[]{ "audio/x-mp3", "audio/mp3", "audio/x-mp3", "audio/mpeg3", "audio/x-mpeg3", "audio/mpeg-3" } );
		addTypes( new String[]{ "audio/midi", "audio/x-midi", "audio/mid", "audio/x-mid", "audio/sp-midi" } );
		addTypes( new String[]{ "audio/wav", "audio/x-wav" } );
		addTypes( new String[]{ "audio/amr", "audio/x-amr" } );
		addTypes( new String[]{ "audio/mpeg4", "audio/mpeg-4", "audio/mp4", "audio/mp4a-latm" } );
		addTypes( new String[]{ "audio/imelody", "audio/x-imelody", "audio/imy", "audio/x-imy" } );
		addTypes( new String[]{ "video/mpeg4", "video/mpeg-4", "video/mp4", "video/mp4v-es", "video/h263", "video/h263-2000" } );
		addTypes( new String[]{ "video/3gpp", "video/3gp" } );
		addTypes( new String[]{ "image/jpeg", "image/jpg" } );
		addTypes( new String[]{ "image/mng", "image/x-mng" } );
		addTypes( new String[]{ "video/mng", "video/x-mng" } );
	}
	
	private static void addTypes( String[] types ) {
		Map nestedMap = new HashMap();
		for (int i = 0; i < types.length; i++) {
			String type = types[i];
			nestedMap.put( type, type );
			TYPES_MAP.put( type, nestedMap );			
		}
	}

	/**
	 * Create a new function
	 */
	public ContentTypeFunction() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.propertyfunctions.PropertyFunction#process(java.lang.String, java.lang.String[], de.enough.polish.Environment)
	 */
	public String process(String input, String[] arguments, Environment env) {
		// sanity check of input:
		if ( input.indexOf('/') == -1 ) {
			throw new BuildException("Unable to retrieve content type for \"" + input + "\" - you need to specify both media and type like \"video/mpeg4\" or \"audio/amr\".");
		}

		Map mappings = (Map) TYPES_MAP.get( input );
		if (mappings == null) {
			// no content type mappings known for the given input:
			//System.out.println("got no mapping for " + input );
			return input;
		}
		
		String protocol = "http";
		if (arguments != null && arguments.length > 0) {
			protocol = arguments[0];
		}
		String supportedContentTypesStr = env.getVariable("polish.mmapi.protocol." + protocol );
		if (supportedContentTypesStr == null) {
			// if nothing was found, degrade gracefully:
			//System.out.println("got no value for  polish.mmapi.protocol." + protocol );
			return input;				
		}
		String[] supportedContentTypes = StringUtil.splitAndTrim( supportedContentTypesStr, ',' );
		if (supportedContentTypes.length == 1) {
			supportedContentTypes = StringUtil.splitAndTrim( supportedContentTypesStr, ' ' );
		}
		for (int i = 0; i < supportedContentTypes.length; i++) {
			String contentType = supportedContentTypes[i];
			if (mappings.containsKey(contentType)) {
				//System.out.println("found correct type " + contentType);
				return contentType;
			}
			//System.out.println("content-type " + contentType + " is not applicable");
		}
		// if nothing was found, degrade gracefully:
		//System.out.println("nothing found in " + mappings );
		return input;
	}

}
