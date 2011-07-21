//#condition polish.usePolishGui
/*
 * Created on 08-Mar-2006 at 01:32:28.
 * 
 * Copyright (c) 2006 Robert Virkus / Enough Software
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
package de.enough.polish.sample.email;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.TextField;

import de.enough.polish.ui.ChoiceTextField;
import de.enough.polish.ui.FilteredChoiceGroup;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;

/**
 * <p>Provides a form for creating a new mail.</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        08-Mar-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CreateMessageForm 
extends Form
implements ItemCommandListener
{
		
	private final static String[] RECEIVERS = new String[] {
		"google.com", "yahoo.com", "msn.com", "somewhere.com"
		//, "a.com", "b.com", "c.com", "d.com", "e.com", "f.com", "g.com", "h.com", "i.com", "j.com", "k.com", "l.com", "m.com", "n.com", "o.com", "p.com", "q.com", "r.com", "s.com", "t.com", "u.com", "v.com", "w.com", "x.com"  
//		"aaron@somewhere.com", "ajax@hype.com", "asynchron@performance.com", "best@j2mepolish.org",
//		"beta@j2mepolish.org", "circus@ms.com", "doing@going.com", "info@enough.de", "j2mepolish@enough.de"		
	};
	private final static String[] SENDERS = new String[] {
		"auser@somewhere.net", "another@anywhere.com", "after@dark.com", "buser@other.org", "donkey@p2p.net", "doja@japan.jp", "gold@ironr.us", "info@enough.de", "j2mepolish@enough.de", "jamba@europe.eu", "relative@einstein.net", "uncertainty@heisenberg.net", "you@conquertheworld.com"
	};
	private final static String DEFAULT_SENDER = "j2me@polish.org";
	         
	private final ChoiceTextField receiver;
	private final FilteredChoiceGroup sender;
	private final FilteredChoiceGroup priority;
	private TextField text;
	private Command cmdChoose = new Command( "Choose...", Command.ITEM, 2 ); 

	/**
	 * Creates a new form for writing an email.
	 * 
	 * @param title the title of the frame
	 */
	public CreateMessageForm(String title) {
		//#if polish.usePolishGui && polish.css.style.createMessageForm
			//#style createMessageForm?
			//# this( title );
		//#else
			this( title, null );
		//#endif
	}
	
	
	/**
	 * Creates a new form for writing an email.
	 * 
	 * @param title the title of the frame
	 * @param style the style for this form, is applied using the #style preprocessing directive
	 */
	public CreateMessageForm(String title, Style style ) {
		//#if polish.usePolishGui
			//# super( title, style );
		//#else
			super( title );
		//#endif
		boolean allowFreeText = false;
		boolean appendSelectedChoice = true;
		String appendDelimiter = ";";
		//#style addressInput
		this.receiver = new ChoiceTextField( "to: " , null, 255, TextField.EMAILADDR, RECEIVERS, allowFreeText, appendSelectedChoice, appendDelimiter );
		char choiceTriggerChar = '@';
		boolean allowChoicesBeforeChoiceTriggerHasBeenEntered = false;
		this.receiver.setChoiceTrigger( choiceTriggerChar, allowChoicesBeforeChoiceTriggerHasBeenEntered );
		append( this.receiver );
		//#style addressInput
		this.sender = new FilteredChoiceGroup( "from: ", "select sender...", Choice.MULTIPLE );
		//this.sender.setFilterText("");
		this.sender.addCommand(this.cmdChoose);
		this.sender.setItemCommandListener( this );
		for (int i = 0; i < SENDERS.length; i++) {
			String senderAddress = SENDERS[i];
			//#style senderOption
			this.sender.append( senderAddress, null );
		}
		append( this.sender );
		//#style addressInput
		this.priority = new FilteredChoiceGroup( "priority: ", "select priority...", Choice.IMPLICIT );
		this.priority.addCommand(this.cmdChoose);
		this.priority.setItemCommandListener( this );
		//#style senderOption
		this.priority.append( "low", null );
		//#style senderOption
		this.priority.append( "normal", null );
		//#style senderOption
		this.priority.append( "important", null );
		append( this.priority );
		
		
		//#style messageInput, input, addressInput
		this.text = new TextField( "message: ", "Hello J2ME Polish World!", 255, TextField.ANY );
		append( this.text );
	
	}
	
	/**
	 * Retrieves the reveiver for the email
	 * 
	 * @return the reveiver for the email
	 */
	public String getReceiver() {
		return this.receiver.getString();
	}
	
	/**
	 * Retrieves the sender for the email
	 * 
	 * @return the sender for the email
	 */
	public String getSender() {
		int index = this.sender.getSelectedIndex();
		if (index == -1) {
			return DEFAULT_SENDER;
		} else {
			return this.sender.getString(index );			
		}
	}


	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.ItemCommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Item)
	 */
	public void commandAction(Command cmd, Item item) {
		//#debug
		System.out.println("commandAction for command=" + cmd.getLabel() + " and item=" + item );
		if (item == this.sender) {
			this.sender.showFilteredList( StyleSheet.display );
		}
	}

}
