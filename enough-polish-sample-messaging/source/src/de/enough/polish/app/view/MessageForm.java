/*
 * Copyright (c) 2012 Robert Virkus / Enough Software
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
package de.enough.polish.app.view;

import de.enough.polish.app.model.Message;
import de.enough.polish.ui.Command;
import de.enough.polish.ui.Form;
import de.enough.polish.ui.FramedForm;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextField;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.Locale;

/**
 * Displays messages
 * 
 * @author robert virkus, j2mepolish@enough.de
 */
public class MessageForm extends FramedForm {

	TextField inputField;
	private ArrayList messageList = new ArrayList();

	/**
	 * Creates a new message form
	 */
	public MessageForm(Command cmdSend) {
		//#style screenMessage
		super(Locale.get("message.title"));
		//#style messageInput
		this.inputField = new TextField(null, "", 500, TextField.ANY);
		this.inputField.setDefaultCommand(cmdSend);
		append(FramedForm.FRAME_BOTTOM, this.inputField);
		setActiveFrame(FramedForm.FRAME_BOTTOM);
	}

	public void addMessage( Message message) {
		//#style messageOutput
		addMessage(message);
	}
	
	public void addMessage( Message message, Style messageStyle ) {
		this.messageList.add(message);
		String text = message.getMessage();
		if (message.isFromMe()) {
			text = "<div class=\"messageFromMe\">me:</div> " + text;
		} else {
			text = "<div class=\"messageFromUser\">" + message.getSender() + ":</div> " + text;
		}
		append(text, messageStyle);
	}
	
	public String getInput() {
		return this.inputField.getString();
	}

	public String getInputAndClearField() {
		String input = this.inputField.getString();
		this.inputField.setString("");
		return input;
	}

	public void showMessageSource() {
		//#style messageSource
		useMessageStyle();
	}

	public void showMessageParsed() {
		//#style messageOutput
		useMessageStyle();
	}

	public void useMessageStyle() {
		// not used directly
	}

	public void useMessageStyle(Style style) {
		Message[] messages = (Message[]) this.messageList.toArray( new Message[this.messageList.size()] );
		deleteAll(FramedForm.FRAME_CENTER);
		this.messageList.clear();
		for (int i = 0; i < messages.length; i++) {
			Message message = messages[i];
			addMessage(message, style);
		}
		scrollToBottom();
	}

}
