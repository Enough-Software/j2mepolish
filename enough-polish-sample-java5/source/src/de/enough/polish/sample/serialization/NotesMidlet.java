/*
 * Created on 30-Jan-2006 at 03:27:38.
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
package de.enough.polish.sample.serialization;

import java.io.IOException;
import java.util.Calendar;
import java.util.Vector;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import de.enough.polish.io.RmsStorage;
import de.enough.polish.ui.UiAccess;

/**
 * <p>Example for using Java 5 syntax elements with J2ME Polish.</p>
 * <p>
 *    For having Java 5 syntax elements like generics, enums and autoboxing, you need to activate the "java5" postcompiler in your
 *    build.xml script:
 *    <pre>
 *    &lt;postcompiler name=&quot;java5&quot; /&gt;
 *    </pre> 
 * </p>
 * <p>
 *    Within your IDE you need to add cldc-1.1-java5.jar and enough-j2mepolish-client-java5.jar to your project's classpath instead of the usual
 *    cldc-1.1.jar and enough-j2mepolish-client.jar packages. Otherwise your IDE will complain about not parameterizable collection classes
 *    when using generics. You might also need to activate Java 5 compiler settings, in Eclipse this is done by rightclicking the project,
 *    choosing Properties and setting the Compiler Complience Level to 5.0 in the Java Compiler tab.
 * </p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        30-June-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class NotesMidlet 
extends MIDlet
implements CommandListener
{
	private final Command createNewCommand = new Command("New", Command.SCREEN, 1 );
	private final Command createNewNoteCommand = new Command( "Note", Command.SCREEN, 1 );
	private final Command createNewReminderCommand = new Command( "Reminder", Command.SCREEN, 2 );
	private final Command exitCommand = new Command( "Exit", Command.EXIT, 10 );
	private final Command okCommand = new Command( "OK", Command.OK, 1 );
	private final Command abortCommand = new Command( "Cancel", Command.BACK, 2 );
	private final Command deleteCommand = new Command( "Delete", Command.ITEM, 3 );

	private List notesList;
	private CreateNoteForm createNoteForm;
	private Display display;
	// you can use generics for having type safe collections - remember to add the cldc1.1-java5.jar to your project settings of your IDE:
	private final Vector<Note> notes;
	private final RmsStorage<Vector<Note>> storage;
	
	public enum Weekday {
		MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
	}
	
	public String getWeekDayName( Weekday weekday ) {
		switch( weekday ) {
		case MONDAY:
			return "Monday";	
		case TUESDAY:
			return "Tuesday";
		case WEDNESDAY:
			return "Wednesday";
		case THURSDAY:
			return "Thursday";
		case FRIDAY:
			return "Friday";
		case SATURDAY:
			return "Saturday";
		case SUNDAY:
			return "Sonday";
			
		}
		throw new IllegalArgumentException(); // should not be possible since an emum is used
	}

	/**
	 * Creates a new midlet.
	 */
	public NotesMidlet() {
		// create main menu / notes list:
		//#style notesList
		this.notesList = new List("Notes (" + getTodayName() + ")" , Choice.IMPLICIT );
		this.notesList.setCommandListener( this );
		this.notesList.addCommand( this.createNewCommand );
		this.notesList.addCommand( this.exitCommand );
		UiAccess.addSubCommand( this.createNewNoteCommand, this.createNewCommand, this.notesList );
		UiAccess.addSubCommand( this.createNewReminderCommand, this.createNewCommand, this.notesList );
		this.notesList.addCommand( this.deleteCommand );
		
		
		// restore notes from record store:
		this.storage = new RmsStorage<Vector<Note>>();
		Vector<Note> vector;
		try {
			vector = this.storage.read("notes");
			// populate list:
			for (Note note : vector) {
				//#style notesItem
				this.notesList.append(note.getText(), null);				
			}
			if (vector.size() != 0) {
				UiAccess.focus( this.notesList, vector.size()-1 );
			}
//			
//			int size = vector.size();
//			for (int i = 0; i < size; i++) {
//				// Note: no casting necessary
//				Note note = vector.elementAt(i);
//				//#style notesItem
//				this.notesList.append(note.getText(), null);
//			}
//			if (size != 0) {
//				UiAccess.focus( this.notesList, size-1 );
//			}
		} catch (IOException e) {
			// storage does not yet exist
			vector = new Vector<Note>();
		}
		this.notes = vector;				
	}

	private String getTodayName() {
		Calendar cal = Calendar.getInstance();
		// really stupid example for enum, but what the heck:
		int day = cal.get( Calendar.DAY_OF_WEEK );
		switch (day) {
		case 1: return getWeekDayName( Weekday.SUNDAY );
		case 2: return getWeekDayName( Weekday.MONDAY );
		case 3: return getWeekDayName( Weekday.TUESDAY );
		case 4: return getWeekDayName( Weekday.WEDNESDAY );
		case 5: return getWeekDayName( Weekday.THURSDAY );
		case 6: return getWeekDayName( Weekday.FRIDAY);
		case 7: return getWeekDayName( Weekday.SATURDAY);
		}
		return "unknown";
	}

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#startApp()
	 */
	protected void startApp() throws MIDletStateChangeException {
		this.display = Display.getDisplay( this );
		this.display.setCurrent( this.notesList );
	}

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#pauseApp()
	 */
	protected void pauseApp() {
		// just keep on pausing
	}

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
	 */
	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
		try {
			this.storage.save( this.notes, "notes");
		} catch (IOException e) {
			//#debug error
			System.out.println("Unable to store notes" + e );
		}
	}

	public void commandAction(Command cmd, Displayable disp) {
		//#debug
		System.out.println("commandAction with cmd=" + cmd.getLabel() + ", screen=" + disp );
		if ( disp == this.notesList ) {
			if (cmd == this.exitCommand ) {
				try {
					destroyApp(false);
					notifyDestroyed();				
				} catch (MIDletStateChangeException e) {
					//#debug error
					System.out.println("Unable to quit app" + e );
				}
			} else if (cmd == this.createNewNoteCommand) {
				CreateNoteForm form = new CreateNoteForm( "Create Note");
				form.setCommandListener( this );
				form.addCommand( this.okCommand );
				form.addCommand( this.abortCommand );
				this.createNoteForm = form;
				this.display.setCurrent( form );
			} else if (cmd == this.createNewReminderCommand) {
				CreateNoteForm form = new CreateNoteForm( "Create Reminder");
				form.setCommandListener( this );
				form.addCommand( this.okCommand );
				form.addCommand( this.abortCommand );
				this.createNoteForm = form;
				this.display.setCurrent( form );
			} else if (cmd == this.deleteCommand) { 
				int selectedIndex = this.notesList.getSelectedIndex();
				if (selectedIndex != -1) {
					this.notes.removeElementAt(selectedIndex);
					this.notesList.delete(selectedIndex);
				}
			}
		} else if (disp == this.createNoteForm ){
			if (cmd == this.okCommand) {
				Note note = this.createNoteForm.getNote();
				this.notes.addElement( note );
				//#style notesItem
				this.notesList.append(note.getText(), null);
				UiAccess.focus( this.notesList, this.notesList.size() - 1 );
				this.createNoteForm = null;
				this.display.setCurrent( this.notesList );
			} else {
				//#debug
				System.out.println("aborting note creation.");
				this.display.setCurrent( this.notesList );
			}
		}
	}


}
