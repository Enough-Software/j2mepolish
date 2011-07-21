/*
 * Copyright (c) 2004 Robert Virkus / Enough Software
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
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.blackberry;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.tools.ant.BuildException;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.OutputFilter;
import de.enough.polish.util.ProcessUtil;
import de.enough.polish.util.StringUtil;

/**
 * <p>Requests signatures using the BB SigTool.jar tool.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SignatureRequester
implements Runnable, OutputFilter
{

	private String password;
	private Environment environment;
	
	private boolean isSupportingPasswordParameter;

	public SignatureRequester() {
		// no initialisation
	}
	
	public int requestSignature( Device device, Locale locale, File jdeHome, File codFile, Environment env ) 
	throws IOException 
	{
		this.environment = env;
		
		String certificateDirStr = env.getVariable( "blackberry.certificate.dir" );
		if (certificateDirStr == null ) {
			System.err.println("Unable to request signature for BlackBerry: you need to specify the variable or Ant property \"blackberry.certificate.dir\".");
			return -1;
		}
		File certificateDir = new File( env.writeProperties(certificateDirStr, true) );
		if (!certificateDir.exists()) {
			System.err.println("Unable to request signature for BlackBerry: the variable or Ant property \"blackberry.certificate.dir\" points to the non-existing directory [" + certificateDir.getAbsolutePath() + "].");
			return -1;
		}
		
		File sigtoolDir;
		File testForSigtoolCsk = new File( certificateDir, "sigtool.csk");
		if (testForSigtoolCsk.exists()) {
			sigtoolDir = certificateDir;
		} else {
			String sigtoolDirStr = env.getVariable( "blackberry.sigtool.dir" );
			if (sigtoolDirStr == null ) {
				System.err.println("Unable to request signature for BlackBerry: you need to specify the variable or Ant property \"blackberry.sigtool.dir\".");
				return -1;
			}
			sigtoolDir = new File( env.writeProperties(sigtoolDirStr, true) );
			if (!sigtoolDir.exists()) {
				System.err.println("Unable to request signature for BlackBerry: the variable or Ant property \"blackberry.sigtool.dir\" points to the non-existing directory [" + sigtoolDir.getAbsolutePath() + "].");
				return -1;
			}			
		}
		
		String pw = env.getVariable( "blackberry.certificate.password" );
		
		return requestSignature( jdeHome, codFile, certificateDir, sigtoolDir, pw );
	}

	private int requestSignature(File jdeHome, File codFile, File certificateDir, File sigtoolDir, String pw) 
	throws IOException 
	{
		File jdeBin = new File( jdeHome, "bin" );
		// copy sigtool files to jdeBin:
		if (!sigtoolDir.equals(jdeBin)) {
			File sigtoolCskSource = new File( sigtoolDir, "sigtool.csk");
			File sigtoolDbSource = new File( sigtoolDir, "sigtool.db");
			File sigtoolCskTarget = new File( jdeBin, "sigtool.csk");
			File sigtoolDbTarget = new File( jdeBin, "sigtool.db");
			if (!sigtoolCskTarget.exists() || !sigtoolDbTarget.exists()
				|| sigtoolCskSource.lastModified() > sigtoolCskTarget.lastModified()
				|| sigtoolDbSource.lastModified() > sigtoolDbTarget.lastModified()
				|| sigtoolCskSource.length() != sigtoolCskTarget.length()
				|| sigtoolDbSource.length() != sigtoolDbTarget.length()
			) {
				FileUtil.copy( sigtoolCskSource,  sigtoolCskTarget );
				FileUtil.copy( sigtoolDbSource, sigtoolDbTarget );
			}
		}
				
		// execute BlackBerry's SignatureTool.jar:
		String signToolPath = new File( jdeBin, "SignatureTool.jar" ).getAbsolutePath();
		ArrayList arguments = new ArrayList();
		// check if the SignatureTool version supports the "-p password" argument first:
		if (pw != null) {
			this.isSupportingPasswordParameter = false;
			arguments.add( "java" );
			arguments.add( "-jar" );
			arguments.add( signToolPath );
			arguments.add( "-?" );
			// the output will be analyzed within filter( message, out ):
			ProcessUtil.exec( arguments, "SignatureTool: ", true, this, certificateDir );
		}
		// now call the actual SignatureTool process:
		arguments.clear();
		arguments.add( "java" );
		arguments.add( "-jar" );
		arguments.add( signToolPath );
		arguments.add( "-a" ); // automatically request for signatures.
		arguments.add( "-s" ); //  display the number of signatures requested and the number that were signed
		arguments.add( "-C" ); //  close regardless of its success.
		if (this.isSupportingPasswordParameter && pw != null) {
			// specify password:
			arguments.add("-p");
			arguments.add(pw);
		}
		arguments.add( codFile.getAbsolutePath() ); // cod file to be signed
		if ( pw != null && ! this.isSupportingPasswordParameter ) {
			if (jdeHome.getAbsolutePath().indexOf("4.1") != -1 ) {
				pw = '\t' + pw;
			}
			this.password = pw;
			Thread thread = new Thread( this );
			thread.start();
		}
		System.out.println("Signing COD file: launching " + signToolPath );
		int result =  ProcessUtil.exec( arguments, "SignatureTool: ", true, null, certificateDir );
		if (result != 0 && pw != null) {
			System.err.println("BlackBerry signing failed with result [" + result + "].");
			String call = StringUtil.toString( arguments);
			if (this.isSupportingPasswordParameter &&  pw != null) {
				call = StringUtil.replace(call, pw, "<password>" );
			}
			System.err.println("Call was: \n" + call );
		}
		return result;
	}

	public void run() {
		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		long delay = 4500;
		String signatureWaitTimeStr = this.environment.getVariable("blackberry.certifcate.inputdelay");
		if (signatureWaitTimeStr != null) {
			try {
				delay = Long.parseLong( signatureWaitTimeStr );
			} catch (Exception e) {
				System.out.println("Invalid blackberry.certifcate.inputdelay setting: " + signatureWaitTimeStr + " is not a number: " + e.toString() );
			}
		}
		//while (! this.isFinished ) {
			try {
				Thread.sleep( delay );
			} catch (InterruptedException e) {
				// ignore
			}
			try {
				char[] chars = this.password.toCharArray();
				int[] keyEvents = parseKeyEvents( this.password );
				//System.out.println("Entering password with length [" + chars.length + "], no. of keyEvents=" + keyEvents.length );
				GraphicsDevice[] devices = graphicsEnvironment.getScreenDevices();
				for (int i = 0; i < devices.length; i++) {
					GraphicsDevice device = devices[i];
					//System.out.println( i + ": " + device.getIDstring() );
	//				GraphicsConfiguration configuration = device.getDefaultConfiguration();
					try {
						Robot robot = new Robot( device );
						for (int j = 0; j < keyEvents.length; j++) {
							int keyEvent = keyEvents[j];
							char originalChar = chars[j];
							//System.out.println("Entering pw-char [" + originalChar + "]");
							try {
								if (Character.isUpperCase(originalChar)) {
									robot.keyPress( KeyEvent.VK_SHIFT );
									robot.keyPress(keyEvent);
									robot.keyRelease(keyEvent);
									robot.keyRelease( KeyEvent.VK_SHIFT );
									
								} else {
									robot.keyPress(keyEvent);
									robot.keyRelease(keyEvent);
								}
							} catch (IllegalArgumentException e) {
								System.err.println("Unable to enter char [" + originalChar + "], keyEvent [" + keyEvent + "] of password [" + this.password + "].");
								return;
								
							}
//							try {
//								Thread.sleep( 200 );
//							} catch (InterruptedException e) {
//								// ignore
//							}
						}
						robot.keyPress( KeyEvent.VK_ENTER );
						
					} catch (AWTException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				System.err.println("Unable to automate code signing: " + e.toString() );
				return;
			}
			//System.out.println();
		//}		
	}

	private int[] parseKeyEvents(String input) {
		int[] keyEvents = new int[ input.length() ];
		char[] chars = input.toLowerCase().toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			keyEvents[i] = parseKeyEvent( c );
		}
		return keyEvents;
	}

	private int parseKeyEvent(char c) {
		switch (c) {
		case '0': return KeyEvent.VK_0;
		case '1': return KeyEvent.VK_1;
		case '2': return KeyEvent.VK_2;
		case '3': return KeyEvent.VK_3;
		case '4': return KeyEvent.VK_4;
		case '5': return KeyEvent.VK_5;
		case '6': return KeyEvent.VK_6;
		case '7': return KeyEvent.VK_7;
		case '8': return KeyEvent.VK_8;
		case '9': return KeyEvent.VK_9;
		case 'a': return KeyEvent.VK_A;
		case 'b': return KeyEvent.VK_B;
		case 'c': return KeyEvent.VK_C;
		case 'd': return KeyEvent.VK_D;
		case 'e': return KeyEvent.VK_E;
		case 'f': return KeyEvent.VK_F;
		case 'g': return KeyEvent.VK_G;
		case 'h': return KeyEvent.VK_H;
		case 'i': return KeyEvent.VK_I;
		case 'j': return KeyEvent.VK_J;
		case 'k': return KeyEvent.VK_K;
		case 'l': return KeyEvent.VK_L;
		case 'm': return KeyEvent.VK_M;
		case 'n': return KeyEvent.VK_N;
		case 'o': return KeyEvent.VK_O;
		case 'p': return KeyEvent.VK_P;
		case 'q': return KeyEvent.VK_Q;
		case 'r': return KeyEvent.VK_R;
		case 's': return KeyEvent.VK_S;
		case 't': return KeyEvent.VK_T;
		case 'u': return KeyEvent.VK_U;
		case 'v': return KeyEvent.VK_V;
		case 'w': return KeyEvent.VK_W;
		case 'x': return KeyEvent.VK_X;
		case 'y': return KeyEvent.VK_Y;
		case 'z': return KeyEvent.VK_Z;
		case ' ': return KeyEvent.VK_SPACE;
		case '\t': return KeyEvent.VK_TAB;
		case '$': return KeyEvent.VK_DOLLAR;
		case '@': return KeyEvent.VK_AT;
		case '&': return KeyEvent.VK_AMPERSAND;
		case '*': return KeyEvent.VK_ASTERISK;
		case '/': return KeyEvent.VK_SLASH;
		case '\\': return KeyEvent.VK_BACK_SLASH;
		case '(': return KeyEvent.VK_BRACELEFT;
		case ')': return KeyEvent.VK_BRACERIGHT;
		case '^': return KeyEvent.VK_CIRCUMFLEX;
		case '[': return KeyEvent.VK_OPEN_BRACKET;
		case ']': return KeyEvent.VK_CLOSE_BRACKET;
		case ':': return KeyEvent.VK_COLON;
		case ';': return KeyEvent.VK_SEMICOLON;
		case ',': return KeyEvent.VK_COMMA;
		case '.': return KeyEvent.VK_PERIOD;
		case '=': return KeyEvent.VK_EQUALS;
		case '!': return KeyEvent.VK_EXCLAMATION_MARK;
		case '>': return KeyEvent.VK_GREATER;
		case '<': return KeyEvent.VK_LESS;
		case '-': return KeyEvent.VK_MINUS;
		case '+': return KeyEvent.VK_PLUS;
		case '#': return KeyEvent.VK_NUMBER_SIGN;
		case '"': return KeyEvent.VK_QUOTE;
		case '_': return KeyEvent.VK_UNDERSCORE;
		}
		throw new BuildException("The BlackBerry password contains the unsupported character [" + c + "] - you need to enter the password for yourself.");
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.util.OutputFilter#filter(java.lang.String, java.io.PrintStream)
	 */
	public void filter(String message, PrintStream output)
	{
		if (message.indexOf("-p password") != -1) {
			this.isSupportingPasswordParameter = true;
		}
	}

}
