package de.enough.polish.app.view;

import de.enough.polish.app.model.Contact;
import de.enough.polish.app.model.ContactCollection;
import de.enough.polish.ui.Command;
import de.enough.polish.ui.FramedForm;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemChangedEvent;
import de.enough.polish.ui.ItemConsumer;
import de.enough.polish.ui.ItemStateListener;
import de.enough.polish.ui.SourcedContainer;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.TextField;
import de.enough.polish.ui.UniformContainer;
import de.enough.polish.ui.UniformItemSource;
import de.enough.polish.util.ArrayList;

/**
 * Allows to select a contact.
 * 
 * @author Robert Virkus
 */
public class ContactSelectionForm extends FramedForm implements
		ItemStateListener
{
	private TextField	filterTextField;
	private ContactItemSource contactItemSource;
	private UniformContainer	uniformContainer;

	public ContactSelectionForm(String title, Command cmdSelectContact)
	{
		//#style screenMessage
		super(title);
		this.contactItemSource = new ContactItemSource( ContactCollection.getInstance(), cmdSelectContact );
		//#style contactList
		UniformContainer cont = new UniformContainer(this.contactItemSource);
		setRootContainer(cont);
		this.uniformContainer = cont;
		// add a text field for filtering contacts:
		//#style messageInput
		TextField textField = new TextField(null, "", 1000, TextField.ANY);
		textField.setItemStateListener(this);
		this.filterTextField = textField;
		append(FRAME_TOP, textField);
	}
	
	/**
	 * Retrieves the currently focused contact
	 * @return the current contact or null when none is focused.
	 */
	public Contact getCurrentContact()
	{
		int itemSourceIndex = this.uniformContainer.getFocusedIndex();
		return this.contactItemSource.getContact( itemSourceIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.enough.polish.ui.ItemStateListener#itemStateChanged(de.enough.polish.ui.Item)
	 */
	public void itemStateChanged(Item item)
	{
		if (item == this.filterTextField)
		{
			this.contactItemSource.setFilter(this.filterTextField.getString());
		}
	}
	
	public void showNotify()
	{
		super.showNotify();
		if (true)
		{
			return;
		}
		if (true)
		{
			Thread t = new Thread()
			{
				public void run()
				{
					try 
					{
						Thread.sleep(2000);
					}
					catch (Exception e)
					{
						// ignore
					}
					Contact contact = ContactCollection.getInstance().getContact(2);
					contact.setFirstName("# " + contact.getFirstName());
					ItemChangedEvent event = new ItemChangedEvent( ItemChangedEvent.CHANGE_SET, 2, null);
					ContactSelectionForm.this.uniformContainer.onItemsChanged(event);
				}
			};
			t.start();
			return;
		}
		Thread t = new Thread(){
			public void run() {
				try {
					Thread.sleep(2500);
				} catch (Exception e) {}
				String[] filters = new String[]{ "a", "an", "ann", "anna"};
				while (true)
				{
					for (int i = 0; i < filters.length; i++)
					{
						String filter = filters[i];
						filterTextField.setString(filter);
						filterTextField.notifyStateChanged();
						try {
							Thread.sleep(500);
						} catch (Exception e) {}
					}
					scrollToBottom();
					try {
						Thread.sleep(1500);
					} catch (Exception e) {}
				}
			}
		};
		t.start();
		Thread t2 = new Thread(){
			public void run() {
				while (true)
				{
					try {
						Thread.sleep(1510);
					} catch (Exception e) {}
					scrollToBottom();
				}
			}
		};
		t2.start();
	}


	private static class ContactItemSource implements UniformItemSource
	{
		private ContactCollection	contactCollection;
		private boolean isFiltered = false;
		private String filterText;
		private ArrayList filteredContacts = new ArrayList();
		private ItemConsumer	itemConsumer;
		private ItemChangedEvent eventRefreshAll = new ItemChangedEvent(ItemChangedEvent.CHANGE_COMPLETE_REFRESH, -1, null);
		private final Command	cmdSelectContact;

		public ContactItemSource( ContactCollection contactCollection, Command cmdSelectContact)
		{
			this.contactCollection = contactCollection;
			this.cmdSelectContact = cmdSelectContact;
		}
		
		
		public void setFilter(String text)
		{
			if (text == this.filterText)
			{
				return; // nothing has changed
			}
			if (text == null || "".equals(text))
			{
				this.isFiltered = false;
				this.filteredContacts.clear();
				this.filterText = null;
				return;
			}
			text = text.toLowerCase();
			this.filterText = text;
			this.filteredContacts.clear();
			Object[] contacts = this.contactCollection.getInternalArray();
			for (int i = 0; i < contacts.length; i++)
			{
				Contact contact = (Contact) contacts[i];
				if (contact == null)
				{
					break;
				}
				if (contact.matches(text))
				{
					this.filteredContacts.add(contact);
				}
			}
			this.isFiltered = true;
			if (this.itemConsumer != null)
			{
				this.itemConsumer.onItemsChanged(this.eventRefreshAll);
			}
		}


		/*
		 * (non-Javadoc)
		 * @see de.enough.polish.ui.ItemSource#countItems()
		 */
		public int countItems()
		{
			if (this.isFiltered)
			{
				return this.filteredContacts.size();
			}
			return this.contactCollection.size();
		}


		/*
		 * (non-Javadoc)
		 * @see de.enough.polish.ui.ItemSource#createItem(int)
		 */
		public Item createItem(int index)
		{
			Contact contact = getContact(index);
			//#style contactItem
			StringItem item = new StringItem(null, contact.getFirstName() + " " +  contact.getLastName());
			item.setDefaultCommand(this.cmdSelectContact);
			return item;
		}
		
		/*
		 * (non-Javadoc)
		 * @see de.enough.polish.ui.UniformItemSource#populateItem(int, de.enough.polish.ui.Item)
		 */
		public void populateItem(int itemIndex, Item item)
		{
			Contact contact = getContact(itemIndex);
			StringItem stringItem = (StringItem) item;
			stringItem.setText(contact.getFirstName() + " " + contact.getLastName());
		}


		private Contact getContact(int index)
		{
			if (this.isFiltered)
			{
				return (Contact) this.filteredContacts.get(index);
			}
			return this.contactCollection.getContact(index);
		}

		/*
		 * (non-Javadoc)
		 * @see de.enough.polish.ui.ItemSource#setItemConsumer(de.enough.polish.ui.ItemConsumer)
		 */
		public void setItemConsumer(ItemConsumer consumer)
		{
			this.itemConsumer = consumer;
		}

		/*
		 * (non-Javadoc)
		 * @see de.enough.polish.ui.ItemSource#getDistributionPreference()
		 */
		public int getDistributionPreference()
		{
			return DISTRIBUTION_PREFERENCE_TOP;
		}

	}

}
