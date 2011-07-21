//#condition polish.java5

package de.enough.polish.java5;

/*
 * Copyright (c) February 2004, Toby Reyelts

All rights reserved.



Redistribution and use in source and binary forms, with or without modification,

are permitted provided that the following conditions are met:



Redistributions of source code must retain the above copyright notice,

this list of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice,

this list of conditions and the following disclaimer in the documentation

and/or other materials provided with the distribution.

Neither the name of Toby Reyelts nor the names of his contributors

may be used to endorse or promote products derived from this software

without specific prior written permission.



THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"

AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE

IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE

ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE

LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR

CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE

GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)

HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,

OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF

THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 */

/**
 * A replacement for the new boxing functions which were added for autoboxing in Java 1.5.
 * This class is based upon RetroWeaver's J2SE 1.4 ports, see http://retroweaver.sourceforge.net/ for more information.
 * 
 * @author Toby Reyelts
 * @author Robert Virkus adjustments for CLDC 1.0, removal of value buffer. 
 * 
 */
public final class Autobox {


	public static Boolean valueOf(boolean b) {
		//#if polish.cldc1.1
			return b ? Boolean.TRUE : Boolean.FALSE;
		//#else
			//# return new Boolean( b );
		//#endif
	}

	public static Byte valueOf(byte val) {
		return new Byte( val );
	}

	public static Character valueOf(char val) {
		return new Character(val);
	}

	public static Short valueOf(short val) {
		return new Short(val);
	}

	public static Integer valueOf(int val) {
		return new Integer(val);
	}

	public static Long valueOf(long l) {
		return new Long(l);
	}
	
	//#if polish.hasFloatingPoint
	public static Float valueOf(float f) {
		return new Float(f);
	}
	//#endif
	
	//#if polish.hasFloatingPoint
	public static Double valueOf(double d) {
		return new Double(d);
	}
	//#endif
}
