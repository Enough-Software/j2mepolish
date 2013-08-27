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
import de.enough.polish.ui.ItemChangedEvent;
import de.enough.polish.ui.ItemConsumer;
import de.enough.polish.ui.ItemSource;
import de.enough.polish.ui.LazyLoadingContainer;
import de.enough.polish.ui.SourcedLazyContainer;
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
public class SimpleMessageForm extends FramedForm {

	TextField inputField;
	private MessageItemSource itemSource;

	/**
	 * Creates a new message form
	 */
	public SimpleMessageForm(Command cmdSend) {
		//#style screenMessage
		super(Locale.get("message.title"));
		//#style messageInput
		this.inputField = new TextField(null, "", 500, TextField.ANY);
		this.inputField.setDefaultCommand(cmdSend);
		append(FramedForm.FRAME_BOTTOM, this.inputField);
		setActiveFrame(FramedForm.FRAME_BOTTOM);
		this.itemSource = new MessageItemSource();
		SourcedLazyContainer llContainer = new SourcedLazyContainer(this.itemSource, 5, 15);
		setRootContainer(llContainer);
	}
	
	public void addMessage( Message message ) {
		this.itemSource.addMessage(message);
//		this.messageList.add(message);
//		String text = message.getMessage();
//		if (message.isFromMe()) {
//			//#style simpleMessageFromMe
//			StringItem item = new StringItem("me:", text);
//			append(item);
//		} else {
//			//#style simpleMessageFromYou
//			StringItem item = new StringItem(message.getSender() + ":", text);
//			append(item);
//		}
	}
	
	public String getInput() {
		return this.inputField.getString();
	}

	public String getInputAndClearField() {
		String input = this.inputField.getString();
		this.inputField.setString("");
		return input;
	}

	static class MessageItemSource implements ItemSource
	{
		private ArrayList messages = new ArrayList();
		private ItemConsumer consumer;
		/*
		 * (non-Javadoc)
		 * @see de.enough.polish.ui.ItemSource#countItems()
		 */
		public int countItems() {
			return this.messages.size();
		}

		/*
		 * (non-Javadoc)
		 * @see de.enough.polish.ui.ItemSource#createItem(int)
		 */
		public Item createItem(int index) {
			Message message = (Message) this.messages.get(index);
			StringItem item;
			String text = message.getMessage();
			if (message.isFromMe()) {
				//#style simpleMessageFromMe
				item = new StringItem("me:", text);
			} else {
				//#style simpleMessageFromYou
				item = new StringItem(message.getSender() + ":", text);
			};
			//item.setDefaultCommand( new Command("Resend", Command.ITEM, 2));
			return item;
		}

		/*
		 * (non-Javadoc)
		 * @see de.enough.polish.ui.ItemSource#setItemConsumer(de.enough.polish.ui.ItemConsumer)
		 */
		public void setItemConsumer(ItemConsumer consumer) {
			this.consumer = consumer;
		}

		/*
		 * (non-Javadoc)
		 * @see de.enough.polish.ui.ItemSource#getDistributionPreference()
		 */
		public int getDistributionPreference() {
			return DISTRIBUTION_PREFERENCE_BOTTOM;
		}

		/*
		 * (non-Javadoc)
		 * @see de.enough.polish.ui.ItemSource#getEmptyItem()
		 */
		public Item getEmptyItem() {
			//#style simpleMessageFromMe
			StringItem item = new StringItem(null, "Send a message to get started");
			return item;
		}
	
		public void addMessage(Message message)
		{
			this.messages.add(message);
			if (this.consumer != null)
			{
				this.consumer.onItemsChanged(ItemChangedEvent.EVENT_COMPLETE_REFRESH);
			}
		}
	}
}
