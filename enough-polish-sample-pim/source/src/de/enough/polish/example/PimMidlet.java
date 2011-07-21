/**
 * 
 */
package de.enough.polish.example;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import de.enough.polish.calendar.CalendarEntry;
import de.enough.polish.event.AsynchronousCommandListener;
import de.enough.polish.pim.PimContact;
import de.enough.polish.pim.PimToDo;
import de.enough.polish.pim.PimUtility;
/**
 * @author Rama
 *
 */
public class PimMidlet extends MIDlet implements CommandListener{
	
	private final Command selectCommand = new Command("Select", Command.OK, 1);
	private final Command quitCommand = new Command("Quit", Command.EXIT, 1);
	
	private final Command fieldLabelsCommand = new Command("Get Field Labels", Command.OK, 1);
	private final Command arrElementLabelsCommand = new Command("Get Array Element Labels", Command.OK, 1);
	private final Command attrLabelsCommand = new Command("Get Attribute Labels", Command.OK, 1);
	
	private final Command newCommand = new Command("Add New Contact", Command.OK, 2);
	private final Command removeCommand = new Command("Delete Contact", Command.OK, 3);
	private final Command editCommand = new Command("Edit Contact", Command.OK, 4);
	private final Command backCommand = new Command("Back", Command.BACK, 1);
	
	private final List pimDatabases = new List("Select Type Of List", List.IMPLICIT);
	
	private final List contactDatabases = new List("Select ContactList", List.IMPLICIT);
	private final List eventDatabases = new List("Select EventList", List.IMPLICIT);
	private final List toDoDatabases = new List("Select ToDoList", List.IMPLICIT);
	
	private final List contactListMenu = new List("Select Contacts", List.IMPLICIT);
	private final List eventListMenu = new List("Select Events", List.IMPLICIT);
	private final List toDoListMenu = new List("Select ToDo", List.IMPLICIT);
	
	private final List contactFieldLabelsMenu = new List("List Of Field Labels", List.IMPLICIT);
	private final List contactArrayFieldLabelsMenu = new List("Array Field Labels", List.IMPLICIT);
	private final List contactFieldAttributeLabelsMenu = new List("Attribute Labels", List.IMPLICIT);
	
	private final List eventFieldLabelsMenu = new List("List Of Field Labels", List.IMPLICIT);
	private final List eventArrayFieldLabelsMenu = new List("Array Field Labels", List.IMPLICIT);
	private final List eventFieldAttributeLabelsMenu = new List("Attribute Labels", List.IMPLICIT);
	
	private final List toDoFieldLabelsMenu = new List("List Of Field Labels", List.IMPLICIT);
	private final List toDoArrayFieldLabelsMenu = new List("Array Field Labels", List.IMPLICIT);
	private final List toDoFieldAttributeLabelsMenu = new List("Attribute Labels", List.IMPLICIT);
	
	private final PimUtility pimUtil = new PimUtility();
	private Display display = null;
	
	public PimMidlet() {
		
	}
	
	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
	
	}
	protected void pauseApp() {
		
	}

	
	protected void startApp() throws MIDletStateChangeException {
		pimDatabases.append("Contact Lists", null);
		pimDatabases.append("Event Lists", null);
		pimDatabases.append("ToDo Lists", null);
		
		pimDatabases.setSelectCommand(selectCommand);
		pimDatabases.addCommand(quitCommand);
		pimDatabases.setCommandListener(new AsynchronousCommandListener(this));
		
		display = Display.getDisplay(this);
		display.setCurrent(pimDatabases);
	}

	
	public void commandAction(Command command, Displayable displayable) {
		if(displayable == pimDatabases) {
			if(command == quitCommand) {
				notifyDestroyed();
			} else if(command == selectCommand) {
				processSelectCmdOfPimDatabases();
            }
		} else if(displayable == contactDatabases) {
			if(command == backCommand) {
				display.setCurrent(pimDatabases);
			} else if(command == fieldLabelsCommand) {
		 		try {
					String listName = contactDatabases.getString(contactDatabases.getSelectedIndex());
					String fieldLabels[] = pimUtil.getFieldLabelsOfContactList(listName);
					for(int i = 0 ; i < fieldLabels.length ; i++) {
						contactFieldLabelsMenu.append(fieldLabels[i], null);
					}
					contactFieldLabelsMenu.addCommand(this.arrElementLabelsCommand);
					contactFieldLabelsMenu.addCommand(this.attrLabelsCommand);
					contactFieldLabelsMenu.addCommand(backCommand);
					contactFieldLabelsMenu.setCommandListener(new AsynchronousCommandListener(this));
					display.setCurrent(contactFieldLabelsMenu);
				} catch(Exception e) {
					e.printStackTrace();
				}
	        } else if(command == selectCommand) {
				processSelectCmdOfContactDatabases();
			}
		} else if(displayable == eventDatabases) {
			if(command == backCommand) {
				display.setCurrent(pimDatabases);
			} else if(command == fieldLabelsCommand) {
				try {
					String listName = eventDatabases.getString(eventDatabases.getSelectedIndex());
					String fieldLabels[] = pimUtil.getFieldLabelsOfEventList(listName);
					for(int i = 0 ; i < fieldLabels.length ; i++) {
						eventFieldLabelsMenu.append(fieldLabels[i], null);
					}
					eventFieldLabelsMenu.addCommand(this.arrElementLabelsCommand);
					eventFieldLabelsMenu.addCommand(this.attrLabelsCommand);
					eventFieldLabelsMenu.addCommand(backCommand);
					eventFieldLabelsMenu.setCommandListener(new AsynchronousCommandListener(this));
					display.setCurrent(eventFieldLabelsMenu);
				} catch(Exception e) {
					e.printStackTrace();
				}
			} else if(command == selectCommand) {
				processSelectCmdOfEventDatabases();
			}
		} else if(displayable == toDoDatabases) {
			if(command == backCommand) {
				display.setCurrent(pimDatabases);
			} else if(command == fieldLabelsCommand) {
				try {
					String listName = toDoDatabases.getString(toDoDatabases.getSelectedIndex());
					String fieldLabels[] = pimUtil.getFieldLabelsOfToDoList(listName);
					for(int i = 0 ; i < fieldLabels.length ; i++) {
						toDoFieldLabelsMenu.append(fieldLabels[i], null);
					}
					toDoFieldLabelsMenu.addCommand(this.arrElementLabelsCommand);
					toDoFieldLabelsMenu.addCommand(this.attrLabelsCommand);
					toDoFieldLabelsMenu.addCommand(backCommand);
					toDoFieldLabelsMenu.setCommandListener(new AsynchronousCommandListener(this));
					display.setCurrent(toDoFieldLabelsMenu);
				} catch(Exception e) {
					e.printStackTrace();
				}
			} else if(command == selectCommand) {
				processSelectCmdOfToDoDatabases();
			}
		} else if(displayable == contactListMenu) {
			if(command == backCommand) {
				display.setCurrent(contactDatabases);
			} else if(command == newCommand) {
				//ToDo
				
			} else if(command == removeCommand) {
				//ToDo
			} else if(command == editCommand) {
				//ToDo
			}
		} else if(displayable == eventListMenu) {
			if(command == backCommand) {
				display.setCurrent(contactDatabases);
			} else if(command == newCommand) {
				//ToDo
			} else if(command == removeCommand) {
				//ToDo
			} else if(command == editCommand) {
				//ToDo
			}
		} else if(displayable == toDoListMenu) {
			if(command == backCommand) {
				display.setCurrent(contactDatabases);
			} else if(command == newCommand) {
				//ToDo
			} else if(command == removeCommand) {
				//ToDo
			} else if(command == editCommand) {
				//ToDo
			}
		} else if (displayable == contactFieldLabelsMenu) {
			if(command == backCommand) {
				display.setCurrent(contactDatabases);
			} else if(command == arrElementLabelsCommand) {
				try {
					String fieldLabel = contactFieldLabelsMenu.getString(contactFieldLabelsMenu.getSelectedIndex());
					String arrayElementLabels[] = pimUtil.getArrayElementNamesOfContactList(fieldLabel, null);
					contactArrayFieldLabelsMenu.deleteAll();
					if(arrayElementLabels != null) {
						for(int i = 0 ; i < arrayElementLabels.length ; i++) {
							contactArrayFieldLabelsMenu.append(arrayElementLabels[i], null);
						}
					} else {
						contactArrayFieldLabelsMenu.append("No Array Elements Found", null);
					}
					contactArrayFieldLabelsMenu.addCommand(backCommand);
					contactArrayFieldLabelsMenu.setCommandListener(new AsynchronousCommandListener(this));
					display.setCurrent(contactArrayFieldLabelsMenu);
				} catch(Exception e) {
					e.printStackTrace();
				}
			} else if(command == attrLabelsCommand) {
				try {
					String fieldLabel = contactFieldLabelsMenu.getString(contactFieldLabelsMenu.getSelectedIndex());
					String attrLabels[] = pimUtil.getAttributeNamesOfContactList(fieldLabel, null);
					contactFieldAttributeLabelsMenu.deleteAll();
					if(attrLabels != null) {
						for(int i = 0 ; i < attrLabels.length ; i++) {
							contactFieldAttributeLabelsMenu.append(attrLabels[i], null);
						}
					} else {
						contactFieldAttributeLabelsMenu.append("No Attributes Found", null);
					}
					contactFieldAttributeLabelsMenu.addCommand(backCommand);
					contactFieldAttributeLabelsMenu.setCommandListener(new AsynchronousCommandListener(this));
					display.setCurrent(contactFieldAttributeLabelsMenu);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		} else if (displayable == contactArrayFieldLabelsMenu) {
			if(command == backCommand) {
				display.setCurrent(contactFieldLabelsMenu);
			}
		} else if (displayable == contactFieldAttributeLabelsMenu) {
			if(command == backCommand) {
				display.setCurrent(contactFieldLabelsMenu);
			}
		} else if (displayable == eventFieldLabelsMenu) {
			if(command == backCommand) {
				display.setCurrent(eventDatabases);
			} else if(command == arrElementLabelsCommand) {
				try {
					String fieldLabel = eventFieldLabelsMenu.getString(eventFieldLabelsMenu.getSelectedIndex());
					String arrayElementLabels[] = pimUtil.getArrayElementNamesOfEventList(fieldLabel, null);
					eventArrayFieldLabelsMenu.deleteAll();
					if(arrayElementLabels != null) {
						for(int i = 0 ; i < arrayElementLabels.length ; i++) {
							eventArrayFieldLabelsMenu.append(arrayElementLabels[i], null);
						}
					} else {
						eventArrayFieldLabelsMenu.append("No Array Elements Found", null);
					}
					eventArrayFieldLabelsMenu.addCommand(backCommand);
					eventArrayFieldLabelsMenu.setCommandListener(new AsynchronousCommandListener(this));
					display.setCurrent(eventArrayFieldLabelsMenu);
				} catch(Exception e) {
					e.printStackTrace();
				}
			} else if(command == attrLabelsCommand) {
				try {
					String fieldLabel = eventFieldLabelsMenu.getString(eventFieldLabelsMenu.getSelectedIndex());
					String attrLabels[] = pimUtil.getAttributeNamesOfEventList(fieldLabel, null);
					eventFieldAttributeLabelsMenu.deleteAll();
					if(attrLabels != null) {
						for(int i = 0 ; i < attrLabels.length ; i++) {
							eventFieldAttributeLabelsMenu.append(attrLabels[i], null);
						}
					} else {
						eventFieldAttributeLabelsMenu.append("No Attributes Found", null);
					}
					eventFieldAttributeLabelsMenu.addCommand(backCommand);
					eventFieldAttributeLabelsMenu.setCommandListener(new AsynchronousCommandListener(this));
					display.setCurrent(eventFieldAttributeLabelsMenu);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		} else if (displayable == eventArrayFieldLabelsMenu) {
			if(command == backCommand) {
				display.setCurrent(eventFieldLabelsMenu);
			}
		} else if (displayable == eventFieldAttributeLabelsMenu) {
			if(command == backCommand) {
				display.setCurrent(eventFieldLabelsMenu);
			}
		} else if (displayable == toDoFieldLabelsMenu) {
			if(command == backCommand) {
				display.setCurrent(toDoDatabases);
			} else if(command == arrElementLabelsCommand) {
				try {
					String fieldLabel = toDoFieldLabelsMenu.getString(toDoFieldLabelsMenu.getSelectedIndex());
					String arrayElementLabels[] = pimUtil.getArrayElementNamesOfToDoList(fieldLabel, null);
					toDoArrayFieldLabelsMenu.deleteAll();
					if(arrayElementLabels != null) {
						for(int i = 0 ; i < arrayElementLabels.length ; i++) {
							toDoArrayFieldLabelsMenu.append(arrayElementLabels[i], null);
						}
					} else {
						toDoArrayFieldLabelsMenu.append("No Array Elements Found", null);
					}
					toDoArrayFieldLabelsMenu.addCommand(backCommand);
					toDoArrayFieldLabelsMenu.setCommandListener(new AsynchronousCommandListener(this));
					display.setCurrent(toDoArrayFieldLabelsMenu);
				} catch(Exception e) {
					e.printStackTrace();
				}
			} else if(command == attrLabelsCommand) {
				try {
					String fieldLabel = toDoFieldLabelsMenu.getString(toDoFieldLabelsMenu.getSelectedIndex());
					String attrLabels[] = pimUtil.getAttributeNamesOfToDoList(fieldLabel, null);
					toDoFieldAttributeLabelsMenu.deleteAll();
					if(attrLabels != null) {
						for(int i = 0 ; i < attrLabels.length ; i++) {
							toDoFieldAttributeLabelsMenu.append(attrLabels[i], null);
						}
					} else {
						toDoFieldAttributeLabelsMenu.append("No Attributes Found", null);
					}
					toDoFieldAttributeLabelsMenu.addCommand(backCommand);
					toDoFieldAttributeLabelsMenu.setCommandListener(new AsynchronousCommandListener(this));
					display.setCurrent(toDoFieldAttributeLabelsMenu);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}  else if (displayable == toDoArrayFieldLabelsMenu) {
			if(command == backCommand) {
				display.setCurrent(toDoFieldLabelsMenu);
			}
		}  else if (displayable == toDoFieldAttributeLabelsMenu) {
			if(command == backCommand) {
				display.setCurrent(toDoFieldLabelsMenu);
			}
		}
	}
	
	private void processSelectCmdOfPimDatabases() {
		if(pimDatabases.getSelectedIndex() == 0) {
			String[] contactListNames = pimUtil.getNamesOfContactLists();
			for(int i = 0 ; i < contactListNames.length ; i++) {
				contactDatabases.append(contactListNames[i], null);
			}
			contactDatabases.addCommand(fieldLabelsCommand);
			contactDatabases.addCommand(backCommand);
			contactDatabases.setSelectCommand(selectCommand);
			contactDatabases.setCommandListener(new AsynchronousCommandListener(this));
			display.setCurrent(contactDatabases);
		} else if(pimDatabases.getSelectedIndex() == 1) {
			String[] eventListNames = pimUtil.getNamesOfEventLists();
			for(int i = 0 ; i < eventListNames.length ; i++) {
				eventDatabases.append(eventListNames[i], null);
			}
			eventDatabases.addCommand(fieldLabelsCommand);
			eventDatabases.addCommand(backCommand);
			eventDatabases.setSelectCommand(selectCommand);
			eventDatabases.setCommandListener(new AsynchronousCommandListener(this));
			display.setCurrent(eventDatabases);
		} else if(pimDatabases.getSelectedIndex() == 2) {
			String[] toDoListNames = pimUtil.getNamesOfToDoLists();
			for(int i = 0 ; i < toDoListNames.length ; i++) {
				toDoDatabases.append(toDoListNames[i], null);
			}
			toDoDatabases.addCommand(fieldLabelsCommand);
			toDoDatabases.addCommand(backCommand);
			toDoDatabases.setSelectCommand(selectCommand);
			toDoDatabases.setCommandListener(new AsynchronousCommandListener(this));
			display.setCurrent(toDoDatabases);
		}
	}
	
	
	private void processSelectCmdOfContactDatabases() {
		try {
			String listName = contactDatabases.getString(contactDatabases.getSelectedIndex());
			PimContact[] contactObjects = pimUtil.getAllContacts(listName);
			for(int i = 0 ; i < contactObjects.length ; i++) {
				PimContact pimContact = contactObjects[i];
				contactListMenu.append(pimContact.getFormattedName() + "\n " + pimContact.getTelephoneNumber(), null);
			}
			contactListMenu.addCommand(newCommand);
			contactListMenu.addCommand(removeCommand);
			contactListMenu.addCommand(editCommand);
			contactListMenu.addCommand(backCommand);
			contactListMenu.setCommandListener(new AsynchronousCommandListener(this));
			display.setCurrent(contactListMenu);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void processSelectCmdOfEventDatabases() {
		try {
			String listName = eventDatabases.getString(eventDatabases.getSelectedIndex());
			CalendarEntry[] eventObjects = pimUtil.getAllEvents(listName);
			int size = eventObjects.length;
			for(int i = 0 ; i < size ; i++) {
				CalendarEntry pimEvent = eventObjects[i];
				eventListMenu.append(pimEvent.getSummary() + " " + pimEvent.getNotes(), null);
			}
			eventListMenu.addCommand(newCommand);
			eventListMenu.addCommand(removeCommand);
			eventListMenu.addCommand(editCommand);
			eventListMenu.addCommand(backCommand);
			eventListMenu.setCommandListener(new AsynchronousCommandListener(this));
			display.setCurrent(eventListMenu);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void processSelectCmdOfToDoDatabases() {
		try {
			String listName = toDoDatabases.getString(toDoDatabases.getSelectedIndex());
			PimToDo[] toDoObjects = pimUtil.getAllToDos(listName);
			int size = toDoObjects.length;
			for(int i = 0 ; i < size ; i++) {
				PimToDo pimToDo = toDoObjects[i];
				toDoListMenu.append(pimToDo.getSummary() + " " + pimToDo.getDueDate(), null);
			}
			toDoListMenu.addCommand(newCommand);
			toDoListMenu.addCommand(removeCommand);
			toDoListMenu.addCommand(editCommand);
			toDoListMenu.addCommand(backCommand);
			toDoListMenu.setCommandListener(new AsynchronousCommandListener(this));
			display.setCurrent(toDoListMenu);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
