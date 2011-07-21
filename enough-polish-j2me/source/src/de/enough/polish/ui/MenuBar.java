//#condition polish.usePolishGui
/*
 * Created on 24-Jan-2005 at 05:39:27.
 * 
 * Copyright (c) 2010 Robert Virkus / Enough Software
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
package de.enough.polish.ui;


import java.io.IOException;
import javax.microedition.lcdui.Canvas;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.event.EventManager;
import de.enough.polish.ui.backgrounds.TranslucentSimpleBackground;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.DeviceControl;
import de.enough.polish.util.HashMap;
import de.enough.polish.util.IdentityArrayList;
import de.enough.polish.util.Locale;

//#if polish.android
	import de.enough.polish.android.midlet.MidletBridge;
//#endif

//#if polish.api.windows
import de.enough.polish.windows.Keyboard;
//#endif

/**
 * <p>Provides a more powerful alternative to the build-in menu bar of the Screen-class.</p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2011</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class MenuBar extends Item {
	

	//#if polish.useFullScreen
		//#if polish.Screen.base:defined
			//#define tmp.fullScreen
		//#elif (polish.midp2 && !tmp.needsNokiaUiForSystemAlerts) && (!polish.useMenuFullScreen || tmp.hasCommandKeyEvents)
			//#define tmp.fullScreen
		//#elif polish.classes.fullscreen:defined
			//#define tmp.fullScreen
		//#elif polish.build.classes.fullscreen:defined
			//#define tmp.fullScreen
		//#endif
		//#if tmp.fullScreen && (polish.MenuBar.useExtendedMenuBar || polish.classes.MenuBar:defined)
			//#if polish.css.title-menu
				//#define tmp.useTitleMenu
			//#endif
		//#endif
	//#endif
	
	//#if polish.handleEvents || polish.css.animations
		//#define tmp.handleEvents
	//#endif
	
	
	//#if polish.key.Menu:defined
		//#if false
			private final static int MENU_KEY = 268566528;
		//#else
			//#= private final static int MENU_KEY = ${polish.key.Menu};
		//#endif
	//#endif
	//#if (polish.MenuBar.Position == invisible) || (polish.blackberry && (polish.BlackBerry.useStandardMenuBar != true))
		//#define tmp.useInvisibleMenuBar
		//private Command hideCommand;
		private Command positiveCommand;
	//#endif
	//#if ${lowercase(polish.MenuBar.OptionsPosition)} == right && ${lowercase(polish.MenuBar.OkPosition)} != right
		//#define tmp.RightOptions
		//#define tmp.OkCommandOnLeft
	//#elif ${lowercase(polish.MenuBar.OptionsPosition)} == right
		//#define tmp.RightOptions
	//#elif ${lowercase(polish.MenuBar.OptionsPosition)} == middle
		//#define tmp.MiddleOptions
	//#else
		//#define tmp.LeftOptions
	//#endif
	
	protected final IdentityArrayList commandsList;
	protected final Container commandsContainer;
	protected boolean isOpened;
	protected Command singleLeftCommand;
	protected final CommandItem singleLeftCommandItem;
	protected Command singleRightCommand;
	protected final CommandItem singleRightCommandItem;
	protected Command singleMiddleCommand;
	//#if ((polish.key.MiddleSoftKey:defined || polish.key.CenterSoftKey:defined) && (polish.MenuBar.useMiddleCommand != false) && polish.useScrollBar) || polish.MenuBar.useMiddleCommand || polish.MenuBar.useCenterCommand
		//#define tmp.useMiddleCommand
		protected final CommandItem singleMiddleCommandItem;
	//#endif
	private int topY;
	protected boolean isSoftKeyPressed;
	
	protected boolean canScrollDownwards; // indicator for parent screen
	protected boolean canScrollUpwards; // indicator for parent screen
	protected boolean paintScrollIndicator; // indicator for parent screen
	protected Image optionsImage;
	protected boolean showImageAndText;
	protected Image selectImage;
	protected Image cancelImage;
	//#if !polish.Bugs.noTranslucencyWithDrawRgb
		protected Background overlayBackground;
	//#endif
	protected final HashMap
	//#if polish.java5
		<Command,CommandItem>
	//#endif
			allCommands;
	protected boolean isOrientationVertical;
	//#if polish.api.windows
		protected static Image windowsSipImage;
		protected int windowsSipX;
		protected int windowsSipY;
	//#endif
	
	protected Style menuItemStyle;
	private ArrayList commandsBeforeLayer;
	
	//rprivate int screenWidth;
	private int screenHeight;

	//Allow setting via includeAddtionalMethods if need be.
	//For apps that do not want to use polish translations
	private static String OPTIONS_CMD_TXT = "Options";
	private static String SELECT_CMD_TXT = "Select";
	private static String CANCEL_CMD_TXT = "Cancel";

	/**
	 * Creates a new menu bar
	 * 
	 * @param screen the parent screen
	 */
	public MenuBar( Screen screen ) {
		this( screen, null );
	}

	/**
	 * Creates a new menu bar
	 * 
	 * @param screen the parent screen
	 * @param style the style of this menu-bar
	 */
	public MenuBar(Screen screen, Style style) {
		super(style);
		this.screen = screen;
		this.commandsList = new IdentityArrayList();
		this.allCommands = new HashMap
		//#if polish.java5
			<Command,CommandItem>
		//#endif
			();
		//#style menu, default
		this.commandsContainer = new Container( true );
		this.commandsContainer.parent = this;
		this.commandsContainer.screen = screen;
		if (this.commandsContainer.style != null) {
			this.commandsContainer.setStyle( this.commandsContainer.style );
		}
		this.commandsContainer.layout |= LAYOUT_SHRINK;
		Command dummy = new Command("", Command.ITEM, 10000);
		//#style rightcommand, command, default
		this.singleRightCommandItem = new CommandItem( dummy, this );
		this.singleRightCommandItem.setImageAlign( Graphics.LEFT );
		//#style leftcommand, command, default
		this.singleLeftCommandItem = new CommandItem( dummy, this );
		this.singleLeftCommandItem.setImageAlign( Graphics.LEFT );
		//#if tmp.useMiddleCommand
		//#style centercommand, middlecommand, command, default
			this.singleMiddleCommandItem = new CommandItem( dummy, this );
			this.singleMiddleCommandItem.setImageAlign( Graphics.LEFT );
		//#endif
		//#if polish.api.windows
			if (windowsSipImage == null && Keyboard.hasSoftwareKeyboard()) {
				try {
					windowsSipImage = StyleSheet.getImage("/sip.png", null, false );
				} catch (Exception e) {
					//#debug error'
					System.out.println("Unable to load sip.png: " + e);
				}
			}
		//#endif
			
	}

	public void addCommand(Command cmd) {
		if(this.menuItemStyle != null)
		{
			addCommand(cmd, this.menuItemStyle);
		}
		else
		{
			//#style menuitem, menu, default
			addCommand( cmd );
		}
	}
	
	public void addCommand(Command cmd, Style commandStyle) {
		//#debug
		System.out.println("adding cmd " + cmd.getLabel() + " with style " + commandStyle );
		
		if (cmd == this.singleLeftCommand || cmd == this.singleRightCommand || cmd == this.singleMiddleCommand || this.commandsList.contains(cmd)) {
			// do not add an existing command again...
		
			//#if polish.MenuBar.rebuild
				removeCommand(cmd);
			//#else
				//#debug
				System.out.println( this + ": Ignoring existing command " + cmd.getLabel() + ",  cmd==this.singleRightCommand: " + ( cmd == this.singleRightCommand) + ", cmd==this.singleLeftCommand: " + (cmd == this.singleLeftCommand) + ", this.commandsList.contains(cmd): " + this.commandsList.contains(cmd)  );
				//#if polish.debug.debug
					for (int i = 0; i < this.commandsList.size(); i++) {
						System.out.println(  ((Command)this.commandsList.get(i)).getLabel() );
					}
				//#endif
				return;
			//#endif
		}
				
		CommandItem item = new CommandItem( cmd, this, commandStyle );
		this.allCommands.put( cmd, item );
		//#debug
		System.out.println(this + ": adding command " + cmd.getLabel() + " (" + cmd + ")");
		int type = cmd.getCommandType();
		int priority = cmd.getPriority();
		//#if tmp.useInvisibleMenuBar
			//#if !polish.android
//				if ( this.hideCommand == null )	{
//					// add hide command:
//					//#ifdef polish.i18n.useDynamicTranslations
//						String text =  Locale.get("polish.command.hide");
//					//#elifdef polish.command.hide:defined
//						//#= String text =  "${polish.command.hide}";
//					//#else
//						//# String text =  "Hide";
//					//#endif
//					
//					//#if !polish.MenuBar.suppressHideCommand					
//					this.hideCommand = new Command( text, Command.CANCEL, 2000 );
//					addCommand( this.hideCommand, commandStyle );
//					//#endif
//				}
			//#endif
			if ( //(cmd != this.hideCommand) && 
					(type == Command.BACK || type == Command.CANCEL || type == Command.EXIT) ) 
			{
				//#if tmp.RightOptions
					if ( this.singleLeftCommand == null || this.singleLeftCommand.getPriority() > priority ) {
						this.singleLeftCommand = cmd;
					}
				//#else
					if ( this.singleRightCommand == null || this.singleRightCommand.getPriority() > priority ) {
						this.singleRightCommand = cmd;
					}
				//#endif
			}
		//#else
			//#if tmp.useMiddleCommand
				if (type == Command.ITEM || type == Command.OK) {
					//System.out.println("adding item/ok command " + cmd.getLabel() + " with prio " + priority + ", previous priority=" + (this.singleMiddleCommand == null ? "<none>" : (Integer.toString(this.singleMiddleCommand.getPriority())) ));
					if (this.singleMiddleCommand == null) {
						this.singleMiddleCommand = cmd;
						this.singleMiddleCommandItem.setImage( (Image)null );
						this.singleMiddleCommandItem.setText( cmd.getLabel() );
						if (this.isInitialized) {
							setInitialized(false);
							repaint();
						}
						return;						
					} else if ( this.singleMiddleCommand.getPriority() > priority ) {
						Command oldMiddleCommand = this.singleMiddleCommand;
						this.singleMiddleCommand = cmd;
						this.singleMiddleCommandItem.setText( cmd.getLabel() );
						cmd = oldMiddleCommand;
						item = (CommandItem) this.allCommands.get( cmd );
						priority = oldMiddleCommand.getPriority();
						//System.out.println("MenuBar: now adding previous singlemiddle command " + cmd.getLabel() );
					}
				}
			//#endif
			//#if tmp.OkCommandOnLeft
				if (type == Command.OK || type == Command.ITEM || type == Command.SCREEN) {
					if (this.singleLeftCommand == null) {
						this.singleLeftCommand = cmd;
						this.singleLeftCommandItem.setImage( (Image)null );
						this.singleLeftCommandItem.setText( cmd.getLabel() );
						if (this.isInitialized) {
							setInitialized(false);
							repaint();
						}
						return;
					} else if ( this.singleLeftCommand.getPriority() > priority ) {
						Command oldLeftCommand = this.singleLeftCommand;
						this.singleLeftCommand = cmd;
						this.singleLeftCommandItem.setText( cmd.getLabel() );
						cmd = oldLeftCommand;
						item = (CommandItem) this.allCommands.get( cmd );
						priority = oldLeftCommand.getPriority();
						//System.out.println("MenuBar: now adding previous singleleft command " + cmd.getLabel() );
					}
				}
			//#else
				if (type == Command.BACK || type == Command.CANCEL || type == Command.EXIT ) {
					//#if tmp.RightOptions
						if (this.singleLeftCommand == null) {
							this.singleLeftCommand = cmd;
							this.singleLeftCommandItem.setImage( (Image)null );
							this.singleLeftCommandItem.setText( cmd.getLabel() );
							if (this.isInitialized) {
								setInitialized(false);
								repaint();
							}
							return;
						} else if ( this.singleLeftCommand.getPriority() > priority ) {
							Command oldLeftCommand = this.singleLeftCommand;
							this.singleLeftCommand = cmd;
							this.singleLeftCommandItem.setText( cmd.getLabel() );
							cmd = oldLeftCommand;
							item = (CommandItem) this.allCommands.get( cmd );
							priority = oldLeftCommand.getPriority();
						}
					//#else
						if (this.singleRightCommand == null) {
							//#debug
							System.out.println("Setting single right command " + cmd.getLabel() );
							this.singleRightCommand = cmd;
							this.singleRightCommandItem.setImage( (Image)null );
							this.singleRightCommandItem.setText( cmd.getLabel() );
							if (this.isInitialized) {
								setInitialized(false);
								repaint();
							}
							return;
						} else if ( this.singleRightCommand.getPriority() > priority ) {
							Command oldRightCommand = this.singleRightCommand;
							this.singleRightCommand = cmd;
							this.singleRightCommandItem.setText( cmd.getLabel() );
							cmd = oldRightCommand;
							item = (CommandItem) this.allCommands.get( cmd );
							priority = oldRightCommand.getPriority();
							//#debug
							System.out.println("exchanging right command " + oldRightCommand.getLabel() );
						}
					//#endif
				}
			//#endif
			//#if tmp.RightOptions
				if (this.singleRightCommand != null) {
					// add existing right command first:
					CommandItem singleItem = (CommandItem) this.allCommands.get( this.singleRightCommand );
					this.commandsList.add( this.singleRightCommand );
					this.commandsContainer.add( singleItem );
					this.singleRightCommand = null;
				}  else if (this.commandsList.size() == 0) {
					// this is the new single right command!
					//#debug
					System.out.println("Setting single right command " + cmd.getLabel() );
					this.singleRightCommand = cmd;
					this.singleRightCommandItem.setText( cmd.getLabel() );
					if (this.isInitialized) {
						setInitialized(false);
						repaint();
					}
					return;
				}
			//#else
				if (this.singleLeftCommand != null) {
					// add existing left command first:
					//#debug
					System.out.println("moving single left command " + this.singleLeftCommand.getLabel() + " to commandsContainer");
					CommandItem singleItem = (CommandItem) this.allCommands.get( this.singleLeftCommand );
					this.commandsList.add( this.singleLeftCommand );
					this.commandsContainer.add( singleItem );
					this.singleLeftCommand = null;
				}  else if (this.commandsList.size() == 0) {
					// this is the new single left command!
					//#debug
					System.out.println("Setting single left command " + cmd.getLabel() );
					this.singleLeftCommand = cmd;
					this.singleLeftCommandItem.setText( cmd.getLabel() );
					if (this.isInitialized) {
						setInitialized(false);
						repaint();
					}
					return;
				}
			//#endif
		//#endif
			
		//#if tmp.useInvisibleMenuBar
			if (this.positiveCommand == null 
					&& type != Command.BACK
					&& type != Command.CANCEL
					&& type != Command.EXIT) 
			{
				this.positiveCommand = cmd;
			}
		//#endif
	
		//#if polish.debug.debug
			if (item == null && cmd != null) {
				//#debug
				System.out.println("did not find CommandItem for command " + cmd.getLabel());
			}
		//#endif
		addCommand( item );
		
		if (this.isInitialized) {
			setInitialized(false);
			repaint();
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Displayable#removeCommand(javax.microedition.lcdui.Command)
	 */
	public void removeCommand(Command cmd) {
		//#debug
		System.out.println(this + ": removing command " + cmd.getLabel() + " (" + cmd + ")");
		this.allCommands.remove( cmd );
		
		//#if tmp.useInvisibleMenuBar
			if (cmd == this.positiveCommand) {
				this.positiveCommand = null;
			}
		//#endif
		
		// 0.case: cmd == this.singleMiddleCommand
		//#if tmp.useMiddleCommand
		if ( cmd == this.singleMiddleCommand ) {
			Command newMiddleCommand = extractNextMiddleCommand();
			this.singleMiddleCommand = newMiddleCommand;
			if (newMiddleCommand == null) {
				this.singleMiddleCommandItem.setText(null);
			} else {
				this.singleMiddleCommandItem.setText( newMiddleCommand.getLabel() );
			}
			if (this.isInitialized) {
				setInitialized(false);
				repaint();
			}
			return;
		} 
		//#endif
		// 1.case: cmd == this.singleLeftCommand
		if ( cmd == this.singleLeftCommand ) {
			this.singleLeftCommand = null;
			if (this.isInitialized) {
				setInitialized(false);
				repaint();
			}
			//#if tmp.RightOptions 
				//#if polish.MenuBar.OkPosition != left
				if (this.singleRightCommand != null) {
					if ( this.singleRightCommand.getCommandType() == Command.BACK 
						|| this.singleRightCommand.getCommandType() == Command.CANCEL ) 
					{
						this.singleLeftCommand = this.singleRightCommand;
						this.singleLeftCommandItem.setText( this.singleLeftCommand.getLabel() );
						this.singleRightCommand = null;
					}
					if (this.isInitialized) {
						setInitialized(false);
						repaint();
					}
					return;
				}
				//#endif
				int newSingleLeftCommandIndex;
				//#if tmp.OkCommandOnLeft
					newSingleLeftCommandIndex = getNextNegativeOrPositiveCommandIndex(false);
				//#else
					newSingleLeftCommandIndex = getNextNegativeOrPositiveCommandIndex(true);
				//#endif
				if ( newSingleLeftCommandIndex != -1 ) {
					//#debug
					System.out.println("moving commmand with index " + newSingleLeftCommandIndex + " from commands container (focused=" + this.commandsContainer.getFocusedIndex() + ") - new Single Left=" + ((Command) this.commandsList.get(newSingleLeftCommandIndex)).getLabel() );
					if (newSingleLeftCommandIndex == this.commandsContainer.getFocusedIndex()) {
						this.commandsContainer.focusChild(-1);
					}
					this.singleLeftCommand = (Command) this.commandsList.remove(newSingleLeftCommandIndex);
					this.singleLeftCommandItem.setText( this.singleLeftCommand.getLabel() );
					this.commandsContainer.remove( newSingleLeftCommandIndex );
				}	
				// don't return here yet, since it could well be that there is only
				// one remaining item in the commandsList. In such a case the 
				// single right command is used instead.
	
			//#endif
		}
		
		// 2.case: cmd == this.singleRightCommand
		if ( cmd == this.singleRightCommand ) {
			// remove single right command:
			this.singleRightCommand = null;
			//System.out.println("removing single right command");
			// check if there is another BACK or CANCEL command and 
			// select the one with the highest priority:
			//#if !tmp.RightOptions
				if (this.singleLeftCommand != null) {
					if ( this.singleLeftCommand.getCommandType() == Command.BACK 
						|| this.singleLeftCommand.getCommandType() == Command.CANCEL ) 
					{
						this.singleRightCommand = this.singleLeftCommand;
						this.singleRightCommandItem.setText( this.singleLeftCommand.getLabel() );
						this.singleLeftCommand = null;
					}
					if (this.isInitialized) {
						setInitialized(false);
						repaint();
					}
					return;
				}
				//#if tmp.useInvisibleMenuBar
					// remove command from commands list before trying to get a new negative command index:
					int index = this.commandsList.indexOf( cmd );
					if (index != -1) {
						//System.out.println("removing normal command");
						if (index == this.commandsContainer.getFocusedIndex()) {
							this.commandsContainer.focusChild(-1);
						}
						this.commandsList.remove( index );
						this.commandsContainer.remove( index );
					}
				//#endif
				int newSingleRightCommandIndex = getNextNegativeOrPositiveCommandIndex(true);
				if ( newSingleRightCommandIndex != -1 ) {
					//#if tmp.useInvisibleMenuBar
						this.singleRightCommand = (Command) this.commandsList.get(newSingleRightCommandIndex);
					//#else
						if (newSingleRightCommandIndex == this.commandsContainer.getFocusedIndex()) {
							this.commandsContainer.focusChild(-1);
						}
						this.singleRightCommand = (Command) this.commandsList.remove(newSingleRightCommandIndex);
						this.commandsContainer.remove( newSingleRightCommandIndex );
					//#endif
					this.singleRightCommandItem.setText( this.singleRightCommand.getLabel() );
				}	
				// don't return here yet, since it could well be that there is only
				// one remaining item in the commandsList. In such a case the 
				// single left command is used instead.

			//#endif

		}
		
		// 3.case: cmd belongs to command collection
		int index = this.commandsList.indexOf( cmd );
		if (index != -1) {
			//System.out.println("removing normal command");
			if (index == this.commandsContainer.getFocusedIndex()) {
				this.commandsContainer.focusChild(-1);
			}
			this.commandsList.remove( index );
			this.commandsContainer.remove( index );
		}
		// if there is only one remaining item in the commandsList, the 
		// single left command is used instead:
		//#if !tmp.useInvisibleMenuBar
		if (this.commandsList.size() == 1) {
			//System.out.println("moving only left command to single-left/right-one, currently focused index=" + this.commandsContainer.getFocusedIndex() );
			CommandItem item = (CommandItem) this.commandsContainer.get( 0 );
			if (!item.hasChildren) {
				Command command = (Command) this.commandsList.remove( 0 );
				this.commandsContainer.focusChild(-1);
				this.commandsContainer.remove( 0 );
				//System.out.println("MenuBar: moving command " + command.getLabel() + ", new currently focused index=" + this.commandsContainer.getFocusedIndex() );
				//#if tmp.RightOptions
					this.singleRightCommand = command;
					this.singleRightCommandItem.setText( command.getLabel() );
				//#else
					this.singleLeftCommand = command;
					this.singleLeftCommandItem.setText( command.getLabel() );
				//#endif
			}
		}
		//#endif

		if (isInitialized()) {
			setInitialized(false);
			repaint();
		}
	}

	/**
	 * Retrieves the next possible middle command (ITEM or OK with the lowest priority number).
	 * @return the next possible middle command or null if none has found
	 */
	private Command extractNextMiddleCommand() {
		Command next = null;
		next = getNextMiddleCommand( next, this.singleLeftCommand );
		next = getNextMiddleCommand( next, this.singleRightCommand );
		Object[] myCommands = this.commandsList.getInternalArray();
		int index = -1;
		for (int i = 0; i < myCommands.length; i++) {
			Command command = (Command) myCommands[i];
			if (command == null) {
				break;
			}
			Command cmd = getNextMiddleCommand( next, command );
			if (cmd != next) {
				index = i;
				next = cmd;
			}
		}
		if (next != null) {
			if (next == this.singleLeftCommand) {
				this.singleLeftCommand = null;
				this.singleLeftCommandItem.setText(null);
				index = getNextNegativeOrPositiveCommandIndex(false);
				if (index != -1) {
					this.singleLeftCommand = (Command) this.commandsList.remove(index);
					this.commandsContainer.remove( index );
					this.singleLeftCommandItem.setText( this.singleLeftCommand.getLabel() );
				}
			} else if (next == this.singleRightCommand) {
				this.singleRightCommand = null;
				this.singleRightCommandItem.setText(null);
				index = getNextNegativeOrPositiveCommandIndex(false);
				if (index != -1) {
					this.singleRightCommand = (Command) this.commandsList.remove(index);
					this.commandsContainer.remove( index );
					this.singleRightCommandItem.setText( this.singleRightCommand.getLabel() );
				}
			} else {
				if (index == this.commandsContainer.focusedIndex) {
					this.commandsContainer.focusChild(-1);
				}
				this.commandsContainer.remove(index);
				this.commandsList.remove(index);
				if(this.commandsList.size() == 1)
				{
					// check if the remaining command can be moved out of the 'Options' menu:
					CommandItem remainingItem = (CommandItem) this.commandsContainer.get(0);
					if (!remainingItem.hasChildren) {
						//#if tmp.RightOptions
							this.singleRightCommand = (Command)this.commandsList.get(0);
							this.singleRightCommandItem.setText(this.singleRightCommand.getLabel());
						//#else
							this.singleLeftCommand = (Command)this.commandsList.get(0);
							this.singleLeftCommandItem.setText(this.singleLeftCommand.getLabel());
						//#endif
						this.commandsList.clear();
						this.commandsContainer.clear();
					}
				}
			}
		}
		return next;
	}

	private Command getNextMiddleCommand(Command current, Command cmd) {
		if (cmd != null) {
			int commandType = cmd.getCommandType();
			if (commandType == Command.OK || commandType == Command.ITEM) {
				if (current == null || cmd.getPriority() < current.getPriority())  {
					return cmd;
				}
			}
		}
		return current;
	}

	private int getNextNegativeOrPositiveCommandIndex( boolean isNegative ) {
	
		// there are several commands available, from the which the BACK/CANCEL command
		// with the highest priority needs to be chosen:
		Object[] myCommands = this.commandsList.getInternalArray();
		int maxPriority = 1000;
		int maxPriorityId = -1;
		for (int i = 0; i < myCommands.length; i++) {
			Command command = (Command) myCommands[i];
			if (command == null) {
				break;
			}
			int type = command.getCommandType();
			
				if ((
					(isNegative && 
					((type == Command.BACK || type == Command.CANCEL || type == Command.STOP || type == Command.EXIT
					)))
					|| (!isNegative && 
							(type == Command.OK || type == Command.ITEM || type == Command.SCREEN) ))
					&& command.getPriority() < maxPriority ) 
			{
				maxPriority = command.getPriority();
				maxPriorityId = i;
			}
		}
		return maxPriorityId;
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#onScreenSizeChanged(int, int)
	 */
	public void onScreenSizeChanged(int sWidth, int sHeight) {
		//this.screenWidth = sWidth;
		this.screenHeight = sHeight;
		super.onScreenSizeChanged(sWidth, sHeight);
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#init(int, int, int)
	 */
	protected void init(int firstLineWidth, int availWidth, int availHeight) {
		super.init(firstLineWidth, availWidth, availHeight);
		if (this.isOpened) {
			Container cmdCont = this.commandsContainer;
			int cmdContHeight = cmdCont.getItemHeight( firstLineWidth, firstLineWidth, availHeight - this.itemHeight);
			int cmdContY = this.screenHeight - this.itemHeight - cmdContHeight - 1;
			int titleHeight = this.screen.getTitleHeight(); // + this.screen.subTitleHeight + this.screen.infoHeight;
			if ( cmdContY < titleHeight) {
				cmdContHeight -= titleHeight - cmdContY;
				cmdContY = titleHeight;
			}
			int scrollHeight = this.screenHeight - titleHeight - this.itemHeight;
			cmdCont.setScrollHeight( scrollHeight );
			//#if tmp.useInvisibleMenuBar && (polish.JavaPlatform <= BlackBerry/4.3)
				cmdCont.relativeY = this.itemHeight - this.screenHeight + this.topY;
			//#else
				//#if tmp.useTitleMenu
					if (this.screen.hasTitleMenu) {
						cmdCont.relativeY = this.itemHeight + 1;
						scrollHeight--; // adjust for +1 in relativeY
						if (cmdCont.isLayoutVerticalExpand()) {
							cmdCont.setItemHeight(scrollHeight);
						} else if (cmdCont.isLayoutVerticalCenter()) {
							cmdCont.relativeY += (scrollHeight - cmdContHeight) / 2; 
						} else if (cmdCont.isLayoutBottom()) {
							cmdCont.relativeY += (scrollHeight - cmdContHeight);
						}
						if (cmdCont.isLayoutCenter()) {
							cmdCont.relativeX = (availWidth - cmdCont.itemWidth) / 2;
						} else if (cmdCont.isLayoutRight()) {
							cmdCont.relativeX = (availWidth - cmdCont.itemWidth);
						} else {
							cmdCont.relativeX = 0;
						}
					} else 
				//#endif
				{
					cmdCont.relativeY = - cmdContHeight;
				}
			//#endif

			this.canScrollDownwards = (cmdCont.yOffset + cmdContHeight > scrollHeight) 
				&& (cmdCont.focusedIndex != this.commandsList.size() - 1 );
			this.paintScrollIndicator = this.canScrollUpwards || this.canScrollDownwards;
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#initContent(int, int)
	 */
	protected void initContent(int firstLineWidth, int availWidth, int availHeight) {
		//#debug
		System.out.println("Init content of MenuBar - isOpened=" + this.isOpened + ", firstLineWidth=" + firstLineWidth + ", lineWidth=" + availWidth + ", screen=" + this.screen );
		if (this.isOpened) {
			int titleHeight = this.screen.getTitleHeight(); // + this.screen.subTitleHeight + this.screen.infoHeight;
			this.topY = titleHeight;
			//System.out.println("setting vertical dimension: " + topMargin + ", " + (this.screen.screenHeight - topMargin) );
			//int containerHeight = this.commandsContainer.getItemHeight( firstLineWidth, firstLineWidth, availHeight);
			//int commandsContainerY = this.screenHeight - containerHeight - 1;
			//System.out.println("screenHeight=" + this.screenHeight + ", containerHeight=" + containerHeight + ", containerY=" + commandsContainerY);
			this.commandsContainer.relativeX = 0;
			//#if tmp.RightOptions || (tmp.useInvisibleMenuBar && !polish.hasTrackballEvents)
				// move menu to the right of the screen:
				this.commandsContainer.relativeX = availWidth - this.commandsContainer.itemWidth;
			//#endif
			/*
			int focusedIndex = this.commandsContainer.focusedIndex;
			this.canScrollUpwards = (focusedIndex != 0); 
			this.canScrollDownwards = (focusedIndex != this.commandsList.size() - 1 );
			*/
			this.canScrollUpwards = (this.commandsContainer.yOffset != 0)
				&& (this.commandsContainer.focusedIndex != 0);
			//#if !tmp.useInvisibleMenuBar && !polish.MenuBar.overwriteHandling
				IconItem item; 
				//#if tmp.OkCommandOnLeft
					item = this.singleLeftCommandItem;
				//#elif tmp.RightOptions
					item = this.singleRightCommandItem;
				//#else
					item = this.singleLeftCommandItem;
				//#endif
				setupCommandMenuItem(item, getMenuSelectText(), this.selectImage);
				//#if tmp.OkCommandOnLeft
					this.singleRightCommandItem.setText(null);
					this.singleRightCommandItem.setImage( (Image)null );
				//#else
					//#if tmp.RightOptions
						item = this.singleLeftCommandItem;
					//#else
						item = this.singleRightCommandItem;
					//#endif
					setupCommandMenuItem(item, getMenuCancelText(), this.cancelImage);
				//#endif
			//#endif
		} else {
			//#if tmp.useInvisibleMenuBar
				this.background = null;
				this.border = null;
				this.contentWidth = 0;
				this.contentHeight = 0;
			//#elif !polish.MenuBar.overwriteHandling
				if (this.singleLeftCommand == null && this.singleRightCommand == null 
						//#if tmp.useMiddleCommand
							&& this.singleMiddleCommand == null
						//#endif
						//#if polish.api.windows
							&& !Keyboard.hasSoftwareKeyboard()
						//#endif
						&& this.commandsList.size() == 0 ) 
				{
					this.contentWidth = 0;
					this.contentHeight = 0;
					return;
				}
				// the menu is closed
				this.paintScrollIndicator = false;
				//#if tmp.RightOptions
					this.singleLeftCommandItem.isInitialized = false;
					if (this.singleLeftCommand != null) {
						// this allows to change the text using UiAccess.setCommandLabel():
						CommandItem item = (CommandItem) this.allCommands.get( this.singleLeftCommand );
						this.singleLeftCommandItem.setText( item.getText() );
					} else {
						this.singleLeftCommandItem.setText( null );
					}
					this.singleLeftCommandItem.setImage( (Image)null );
				//#else
					this.singleRightCommandItem.isInitialized = false;
					if (this.singleRightCommand != null) {
						// this allows to change the text using UiAccess.setCommandLabel():
						CommandItem item = (CommandItem) this.allCommands.get( this.singleRightCommand );
						this.singleRightCommandItem.setText( item.getText() );
					} else {
						this.singleRightCommandItem.setText( null );
					}
					this.singleRightCommandItem.setImage( (Image)null );
				//#endif
				if (this.commandsList.size() > 0) {
					IconItem item;
					//#if tmp.RightOptions
						item = this.singleRightCommandItem;
					//#else
						item = this.singleLeftCommandItem;
					//#endif
					setupCommandMenuItem(item, getMenuOptionsText(), optionsImage);
				}
			//#endif
		}
		//#if !tmp.useInvisibleMenuBar
			int availScreenWidth;
			//#if polish.MenuBar.Position == right
				availScreenWidth = this.screen.screenWidth;
			//#else
				availScreenWidth = availWidth >> 1;
			//#endif
//			if ( ! this.isOpened && this.singleRightCommand == null ) {
//				availableWidth = lineWidth;
//			}
			//System.out.println("Initialising single commands with a width of " + availableWidth + " lineWidth is " + lineWidth);
			int height = Math.max( this.singleLeftCommandItem.getItemHeight( availScreenWidth, availScreenWidth, availHeight),
					this.singleRightCommandItem.getItemHeight( availScreenWidth, availScreenWidth, availHeight) );
			//#if tmp.useMiddleCommand
				height = Math.max( height, this.singleMiddleCommandItem.getItemHeight( availScreenWidth, availScreenWidth, availHeight) );
				this.singleMiddleCommandItem.relativeX = availScreenWidth - (this.singleMiddleCommandItem.itemWidth / 2);
			//#endif
			this.contentHeight = height;
			// items have now been initialized
			Item item = this.singleRightCommandItem;
			item.relativeX = availScreenWidth;
			int width = item.itemWidth;
			if (width < availScreenWidth) {
				if (item.isLayoutCenter) {
					item.relativeX = availScreenWidth + (availScreenWidth - width) / 2;
				} else if (item.isLayoutRight) {
					item.relativeX = availScreenWidth + (availScreenWidth - width);
				}
			}
			// TODO allow LAYOUT_VEXPAND
			item = this.singleLeftCommandItem;
			item.relativeX = 0;
			width = item.itemWidth;
			if (width < availScreenWidth) {
				if (item.isLayoutCenter) {
					item.relativeX = (availScreenWidth - width) / 2;
				} else if (item.isLayoutRight) {
					item.relativeX = (availScreenWidth - width);
				}				
			}
			
			if ( this.singleRightCommandItem.isLayoutVerticalCenter()) {
				this.singleRightCommandItem.relativeY = (this.contentHeight - this.singleRightCommandItem.itemHeight) >> 1;					
			} else if ( this.singleRightCommandItem.isLayoutBottom()) {
				this.singleRightCommandItem.relativeY = this.contentHeight - this.singleRightCommandItem.itemHeight;
			} else {
				this.singleRightCommandItem.relativeY = 0;
			}
			this.contentWidth = availWidth;
			//#if polish.ScreenOrientationCanChange
				if (this.isOrientationVertical) {
					// move the single left command to the right:
					// and the single right command to the top-right:
					this.singleLeftCommandItem.relativeX = this.contentWidth - this.singleLeftCommandItem.itemWidth;
					this.singleRightCommandItem.relativeX = this.contentWidth - this.singleRightCommandItem.itemWidth;
					this.singleRightCommandItem.relativeY = this.contentHeight - this.screenHeight;
					if (this.isOpened) {
						this.commandsContainer.relativeX = this.contentWidth - this.commandsContainer.itemWidth;
					}
				}
			//#endif
			//#if polish.MenuBar.Position == right
				int maxWidth = Math.max( this.singleLeftCommandItem.itemWidth, this.singleRightCommandItem.itemWidth );
				//#if tmp.useMiddleCommand
					maxWidth = Math.max( maxWidth, this.singleMiddleCommandItem.itemWidth ); 
				//#endif
				this.contentWidth = maxWidth;
				this.contentHeight = this.screenHeight;
				// move the single left command to the right:
				// and the single right command to the top-right:
				this.singleLeftCommandItem.relativeX = maxWidth - this.singleLeftCommandItem.itemWidth;
				this.singleRightCommandItem.relativeX = maxWidth - this.singleRightCommandItem.itemWidth;
				//#if tmp.useMiddleCommand
					this.singleMiddleCommandItem.relativeX = maxWidth - this.singleMiddleCommandItem.itemWidth;
					this.singleMiddleCommandItem.relativeY = this.singleLeftCommandItem.itemHeight + this.paddingVertical;
				//#endif
				//#if polish.MenuBar.NumberOfSoftKeys:defined
					int verticalSpacePerItem = 0;
					//#= verticalSpacePerItem = this.contentHeight / ${polish.MenuBar.NumberOfSoftKeys};
					this.singleLeftCommandItem.relativeY = (verticalSpacePerItem - this.singleLeftCommandItem.itemHeight) >> 1;
					//#if tmp.useMiddleCommand
						this.singleMiddleCommandItem.relativeY = verticalSpacePerItem + ((verticalSpacePerItem - this.singleMiddleCommandItem.itemHeight) >> 1);
					//#endif
					this.singleRightCommandItem.relativeY = this.contentHeight - verticalSpacePerItem + ((verticalSpacePerItem - this.singleRightCommandItem.itemHeight) >> 1);					
				//#else
					//#if tmp.useMiddleCommand
						this.singleMiddleCommandItem.relativeY = this.singleLeftCommandItem.itemHeight + this.paddingVertical;
					//#endif
					this.singleRightCommandItem.relativeY = this.contentHeight - this.singleRightCommandItem.itemHeight - this.paddingBottom - getBorderWidthBottom() - this.marginBottom;
				//#endif
				if (this.isOpened) {
					this.commandsContainer.relativeY = this.screen.titleHeight;
					this.commandsContainer.relativeX =  - this.commandsContainer.itemWidth;
				}
			//#endif
		//#endif
		//#if polish.api.windows
				if (windowsSipImage != null) {
					int h = windowsSipImage.getHeight();
					if (h > this.contentHeight) {
						this.contentHeight = h;
						this.windowsSipY = 0;
					}  else {
						this.windowsSipY = (this.contentHeight - h) / 2;
					}
					int w = windowsSipImage.getWidth();
					this.windowsSipX = (availWidth - w) / 2;
				}
		//#endif
	}

        /**
         * Setups up and initializes a command item. Bassed on settings and if images are shown.
         * @param item IconItem to setup
         * @param text Text to display if enabled
         * @param img  Image to show if enabled
         */
        protected void setupCommandMenuItem(IconItem item, String text, Image img){
            item.isInitialized = false;
            //Reset every thing
            item.setImage( (Image)null );
            item.setText( null );
            //Show image if there was one.
            if (img!= null) {
                item.setImage( img );
            }
            //Show text if we have both images and text OR simply if there is no image.
            if (this.showImageAndText || img ==null) {
                    item.setText(text);
            }
        }

        public String getMenuOptionsText(){
            String optionsText =
            //#ifdef polish.i18n.useDynamicTranslations
                  //#= Locale.get( "polish.command.options" );
            //#elifdef polish.command.options:defined
                  //#= "${polish.command.options}" ;
            //#else
                    OPTIONS_CMD_TXT;
            //#endif
            return optionsText;
        }

        public String getMenuSelectText(){
            String selectText =
            //#ifdef polish.i18n.useDynamicTranslations
                    Locale.get( "polish.command.select" );
            //#elifdef polish.command.select:defined
                    //#= "${polish.command.select}";
            //#else
                   //# SELECT_CMD_TXT;
            //#endif
             return selectText;
        }

        public String getMenuCancelText(){
            String cancelText =
            //#ifdef polish.i18n.useDynamicTranslations
                    //#= Locale.get( "polish.command.cancel" ) ;
            //#elifdef polish.command.cancel:defined
                    //#= "${polish.command.cancel}";
            //#else
                    CANCEL_CMD_TXT ;
            //#endif
            return cancelText;
        }
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paintBackgroundAndBorder(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintBackgroundAndBorder(int x, int y, int width,
			int height, Graphics g)
	{
		if (this.isOpened) {
			// paint overlay background:
			//#if !polish.Bugs.noTranslucencyWithDrawRgb
				if (this.overlayBackground != null) {
					int overlayWidth = this.screen.screenWidth;
					//#if polish.MenuBar.Position == right
						overlayWidth -= this.itemWidth;
					//#endif
					int titleHeight = this.screen.getTitleHeight();
					this.overlayBackground.paint( 0, titleHeight, overlayWidth, this.screenHeight - titleHeight, g );
				}
			//#endif
		}
		super.paintBackgroundAndBorder(x, y, width, height, g);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) 
	{
		//System.out.println("painting menubar from " + x + "/" + leftBorder + " to " + rightBorder + ", clipRight=" + (g.getClipX() + g.getClipWidth()) );
		boolean paintLeftCommand = false;
		boolean paintRightCommand = false;
		boolean paintMiddleCommand = false;
		if (this.isOpened) {
			// paint opened menu:
			//System.out.println("setting clip " + this.topY + ", " + (this.screen.screenHeight - this.topY) );
			int clipX = g.getClipX();
			int clipY = g.getClipY();
			int clipWidth = g.getClipWidth();
			int clipHeight = g.getClipHeight();
			int scrHeight = this.screenHeight - this.itemHeight;
			int maxClipHeight = Math.max(this.relativeY - this.topY, scrHeight - this.topY);
			//#if tmp.useTitleMenu
				if (this.screen.hasTitleMenu) {
					maxClipHeight = scrHeight;
				}
			//#endif
			//#if polish.ScreenOrientationCanChange
	        	if (this.isOrientationVertical) {
		        	g.setClip(0, this.topY, this.screen.screenWidth, scrHeight - this.topY - this.itemHeight );
	        	} else {
		        	g.setClip(0, this.topY, this.screen.screenWidth, maxClipHeight);
	        	}
	        //#else
	        	g.setClip(0, this.topY, this.screen.screenWidth, maxClipHeight);
	        //#endif
	        //#if polish.hideAllCommands
	        	//#debug
	        	//System.out.println("polish.hideAllCommands is used to suppress painting for a Command.");
	        //#else
	        	Container cont = this.commandsContainer;
	        	cont.paint( x + cont.relativeX, y + cont.relativeY, x + cont.relativeX, x + cont.relativeX + cont.itemWidth, g);
			//#endif
            g.setClip( clipX, clipY, clipWidth, clipHeight );

       //#if !tmp.useInvisibleMenuBar
			// paint menu-bar:
			//#if polish.MenuBar.Position == right
				if (this.commandsContainer.size() > 0 || this.singleLeftCommand != null) {
					paintLeftCommand = true;
				}
				if (this.singleRightCommand != null) {
					paintRightCommand = true;
				}
			//#else
				paintLeftCommand = true;
				paintRightCommand = true;
			//#endif
		} else {
			//#if polish.MenuBar.Position == right
				if (this.commandsContainer.size() > 0 || this.singleLeftCommand != null) {
					paintLeftCommand = true;
				}
				//#if tmp.useMiddleCommand
					if (this.singleMiddleCommand != null) {
						paintMiddleCommand = true;
					}
				//#endif
				if (this.singleRightCommand != null) {
					paintRightCommand = true;
				}
			//#else
				//#if polish.ScreenOrientationCanChange
					if (this.isOrientationVertical) {
						if (this.commandsContainer.size() > 0 || this.singleLeftCommand != null) {
							paintLeftCommand = true;
						}
						if (this.singleRightCommand != null) {
							paintRightCommand = true;
						}
					} else {
				//#endif
					//#if tmp.useMiddleCommand
						if (this.singleMiddleCommand != null) {
							paintMiddleCommand = true;
						}
					//#endif
					if (this.singleLeftCommand != null
						//#if !tmp.RightOptions
						 || this.commandsContainer.size() > 0
						//#endif
					) {
						paintLeftCommand = true;
					}
					if (this.singleRightCommand != null
						//#if tmp.RightOptions
							|| this.commandsContainer.size() > 0
						//#endif
					) {
						paintRightCommand = true;
					}
				//#if polish.ScreenOrientationCanChange
					}
				//#endif
			//#endif
		//#endif
		}
		if (paintLeftCommand) {
			CommandItem item = this.singleLeftCommandItem;
			int itemX = x + item.relativeX;
			item.paint( itemX, y + item.relativeY, itemX, itemX + item.itemWidth, g );
		}
		if (paintRightCommand) {
			CommandItem item = this.singleRightCommandItem;
			int itemX = x + item.relativeX;
			item.paint( itemX, y + item.relativeY, itemX, itemX + item.itemWidth, g );
		}
		//#if tmp.useMiddleCommand
			if (paintMiddleCommand) {
				CommandItem item = this.singleMiddleCommandItem;
				int itemX = x + item.relativeX;
				item.paint( itemX, y + item.relativeY, itemX, itemX + item.itemWidth, g );
			}
		//#endif
		//#if polish.api.windows
			if (MenuBar.windowsSipImage != null) {
				g.drawImage(MenuBar.windowsSipImage, this.windowsSipX, y + this.windowsSipY, Graphics.TOP | Graphics.LEFT);
			}
		//#endif
	}

	//#ifdef polish.useDynamicStyles	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#createCssSelector()
	 */
	protected String createCssSelector() {
		return "menubar";
	}
	//#endif
	
	/**
	 * Used to toggle the opened state of the menu bar
	 * 
	 * @param open true when the menu should be opened
	 */
	protected void setOpen( boolean open ) {
		if (!open && this.isOpened) {
			//#if tmp.handleEvents
				EventManager.fireEvent( EventManager.EVENT_MENU_CLOSE, this.screen, null );
			//#endif
			this.commandsContainer.hideNotify();
			this.isInitialized = (open == this.isOpened);
			this.isOpened = open;
			//#if polish.blackberry
				this.screen.notifyFocusSet(this.screen.getCurrentItem());
			//#endif
		} else if (open && !this.isOpened) {
			if (this.commandsContainer.size() == 0) {
				return;
			}
			//#if tmp.handleEvents
				EventManager.fireEvent( EventManager.EVENT_MENU_OPEN, this.screen, null );
			//#endif
			this.isInitialized = (open == this.isOpened);
			this.isOpened = open;
			//#if polish.blackberry
				Display.getInstance().notifyFocusSet(null);
			//#endif
			//#if polish.javaplatform >= Android/1.5
				MidletBridge.instance.hideSoftKeyboard();
			//#endif
			//#if !polish.MenuBar.focusFirstAfterClose
				// focus the first item again, so when the user opens the menu again, it will be "fresh" again
				this.commandsContainer.focusChild(0);
			//#endif
			this.commandsContainer.showNotify();
		}
	}
	


	protected boolean handleKeyPressed(int keyCode, int gameAction) {
		//#debug
		System.out.println("MenuBar: handleKeyPressed(" + keyCode + ", " + gameAction + ")" );
		this.isSoftKeyPressed = false;
		if (this.isOpened) {
			//#if polish.key.Menu:defined
				if (keyCode == MENU_KEY) {
					//#if polish.blackberry && !hasTrackBallEvents
						Item commandItem = this.commandsContainer.getFocusedItem();
						if (commandItem != null) {
							commandItem.handleKeyPressed( 0, Canvas.FIRE );
						}
					//#endif
					return true; // close in keyRelease
				}
			//#endif
			if (isSelectOptionsMenuKey(keyCode, gameAction)) {
				this.isSoftKeyPressed = true;	
				notifyKeyPressed();
				CommandItem commandItem = (CommandItem) this.commandsContainer.getFocusedItem();
				commandItem.handleKeyPressed( 0, Canvas.FIRE );
				return true;
			} else  if (isCloseOptionsMenuKey(keyCode, gameAction)
				//#if polish.key.ReturnKey:defined
					//#= || (keyCode == ${polish.key.ReturnKey})
				//#endif
			) {
				CommandItem item = getCloseOptionsItem();
				if (item != null) {
					item.notifyItemPressedStart();
				}
				this.isSoftKeyPressed = true;
				notifyKeyPressed();
				this.commandsContainer.handleKeyPressed(0, Canvas.LEFT);
//				int selectedIndex = this.commandsContainer.getFocusedIndex();
//				if (!this.commandsContainer.handleKeyPressed(0, Canvas.LEFT)
//						|| selectedIndex != this.commandsContainer.getFocusedIndex() ) 
//				{
//					setOpen( false );
//				}
				//System.out.println("MenuBar is closing due to key " + keyCode);
				return true;
			} else {
				//#if tmp.useInvisibleMenuBar && !polish.android
//					// handle hide command specifically:
//					if (  gameAction == Canvas.FIRE && ((CommandItem)this.commandsContainer.focusedItem).command == this.hideCommand ) {
//						//setOpen( false );
//						return true;
//					}
				//#endif
//				if (gameAction == Canvas.FIRE) {
//					int focusedIndex = this.commandsContainer.focusedIndex;
//					Command command = (Command) this.commandsList.get(focusedIndex);
//					setOpen( false );
//					this.screen.callCommandListener(command);
//					return true;
//				}
				boolean handled = this.commandsContainer.handleKeyPressed(keyCode, gameAction);
				//System.out.println("menubar: container handled key " + keyCode + ": " + handled);
				if (handled) {
					setInitialized(false);
				} else { 
					//#if polish.debug.error
						if (gameAction == Canvas.DOWN || gameAction == Canvas.UP) {
							//#if polish.Container.allowCycling != false
								//#debug error
								System.out.println("Container DID NOT HANDLE DOWN OR UP, selectedIndex=" + this.commandsContainer.getFocusedIndex() + ", count="+ this.commandsContainer.size() + ", cycling=" + this.commandsContainer.allowCycling);
							//#else
								//#debug error
								System.out.println("Container DID NOT HANDLE DOWN OR UP probably since cycling has been DEACTIVATED - check your polish.Container.allowCycling preprocessing variable" );
							//#endif
							//#if polish.css.view-type
								//#debug error
								System.out.println("view-type of container " + this.commandsContainer + " = " + this.commandsContainer.containerView );
							//#endif
						}
					//#endif
					if (keyCode >= Canvas.KEY_NUM1 && keyCode <= Canvas.KEY_NUM9) {
						int index = keyCode - Canvas.KEY_NUM1;
						if (index <= this.commandsContainer.size()) {
							CommandItem item = (CommandItem) this.commandsContainer.get(index);
							if (item.getAppearanceMode() != Item.PLAIN) {
								if (!item.isFocused) {
									this.commandsContainer.focusChild( index );
								}
								handled = item.handleKeyPressed(0, Canvas.FIRE);
								return handled;
							}
						}
					}
					//setOpen( false );
				}
				return true;				
			}
		} else { // menu is currently closed:
			//#if polish.key.Menu:defined
				if (keyCode == MENU_KEY && this.commandsContainer.size() > 0) {
					notifyKeyPressed();					
					return true;
				}
			//#endif

			
			//#if tmp.useMiddleCommand
				//#if polish.key.MiddleSoftKey:defined
					//#= if ( keyCode == ${polish.key.MiddleSoftKey}
				//#elif polish.key.CenterSoftKey:defined
					//#= if ( keyCode == ${polish.key.CenterSoftKey}
				//#else
					if ( this.screen.isGameActionFire(keyCode, gameAction)
				//#endif
							&& this.singleMiddleCommand != null && this.singleMiddleCommandItem.getAppearanceMode() != PLAIN) 
					{
						//#if polish.key.MiddleSoftKey:defined
							this.isSoftKeyPressed = true;			
						//#endif
						notifyKeyPressed();
						this.singleMiddleCommandItem.notifyItemPressedStart();
						return true;			
					}
			//#endif
			if ( this.singleLeftCommand != null && this.screen.isSoftKeyLeft(keyCode, gameAction) && this.singleLeftCommandItem.getAppearanceMode() != PLAIN) {
				this.isSoftKeyPressed = true;	
				notifyKeyPressed();
				this.singleLeftCommandItem.notifyItemPressedStart();
				return true;			
			} else if (this.singleRightCommand != null && this.screen.isSoftKeyRight(keyCode, gameAction) && this.singleRightCommandItem.getAppearanceMode() != PLAIN) {
				this.isSoftKeyPressed = true;	
				notifyKeyPressed();
				this.singleRightCommandItem.notifyItemPressedStart();
				return true;			
			} else if (isOpenOptionsMenuKey(keyCode, gameAction) ) {
				this.isSoftKeyPressed = true;	
				CommandItem item = getOpenOptionsItem();
				if (item != null) {
					item.notifyItemPressedStart();
				}
				return true;
			}
		}
		return false;
	}
	
	private CommandItem getOpenOptionsItem() {
		CommandItem result;
		//#if ${lowercase(polish.MenuBar.OptionsPosition)} == right && ${lowercase(polish.MenuBar.OkPosition)} != right
			result = this.singleRightCommandItem;
		//#elif ${lowercase(polish.MenuBar.OptionsPosition)} == right
			result = this.singleRightCommandItem;
		//#elif ${lowercase(polish.MenuBar.OptionsPosition)} == middle
			result = this.singleMiddleCommandItem;
		//#else
			result = this.singleLeftCommandItem;
		//#endif
		return result;	
	}
	
	/**
	 * Retrieves the item responsible for closing the options.
	 * @return the commanditem that closes an open menu, can be null
	 */
	private CommandItem getCloseOptionsItem()
	{
		CommandItem item;
		//#if tmp.RightOptions
			item = this.singleLeftCommandItem;
		//#else
			item = this.singleRightCommandItem;
		//#endif
		return item;
	}

	
	/**
	 * Determines whether the given key should open the commands menu
	 * @param keyCode the key code
	 * @return true when the commands menu should be opened by this key
	 */
	private boolean isOpenOptionsMenuKey(int keyCode, int gameAction)
	{
		boolean result;
		//#if ${lowercase(polish.MenuBar.OptionsPosition)} == right && ${lowercase(polish.MenuBar.OkPosition)} != right
			result = this.screen.isSoftKeyRight(keyCode, gameAction);
		//#elif ${lowercase(polish.MenuBar.OptionsPosition)} == right
			result = this.screen.isSoftKeyRight(keyCode, gameAction);
		//#elif ${lowercase(polish.MenuBar.OptionsPosition)} == middle
			result = this.screen.isSoftKeyMiddle(keyCode, gameAction);
		//#else
			result = this.screen.isSoftKeyLeft(keyCode, gameAction);
		//#endif
		return result;	
	}

	/**
	 * Determines whether the given key should close the commands menu
	 * @param keyCode the key code
	 * @return true when the commands menu should be closed by this key
	 */
	private boolean isCloseOptionsMenuKey(int keyCode, int gameAction)
	{
		boolean result;
		//#if ${lowercase(polish.MenuBar.OptionsPosition)} == right && ${lowercase(polish.MenuBar.OkPosition)} != right
			result = this.screen.isSoftKeyRight(keyCode, gameAction);
		//#elif ${lowercase(polish.MenuBar.OptionsPosition)} == right
			result = this.screen.isSoftKeyLeft(keyCode, gameAction);
		//#elif ${lowercase(polish.MenuBar.OptionsPosition)} == middle
			result = this.screen.isSoftKeyRight(keyCode, gameAction);
		//#else
			result = this.screen.isSoftKeyRight(keyCode, gameAction);
		//#endif
		return result;	
	}

	/**
	 * Determines whether the given key should select an entry of the opened the commands menu
	 * @param keyCode the key code
	 * @return true when the key should select the currently focused command option
	 */
	private boolean isSelectOptionsMenuKey(int keyCode, int gameAction)
	{
		boolean result;
		//#if ${lowercase(polish.MenuBar.OptionsPosition)} == right && ${lowercase(polish.MenuBar.OkPosition)} != right
			result = this.screen.isSoftKeyLeft(keyCode, gameAction);
		//#elif ${lowercase(polish.MenuBar.OptionsPosition)} == right
			result = this.screen.isSoftKeyRight(keyCode, gameAction);
		//#elif ${lowercase(polish.MenuBar.OptionsPosition)} == middle
			result = this.screen.isSoftKeyLeft(keyCode, gameAction);
		//#else
			result = this.screen.isSoftKeyLeft(keyCode, gameAction);
		//#endif
		return result;
	}

	/**
	 * Commits different actions depending on the device using a menubar
	 */
	public static void notifyKeyPressed()
	{
		//#if polish.softkey-vibrate
			DeviceControl.vibrate(25);
		//#endif
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handleKeyReleased(int, int)
	 */
	protected boolean handleKeyReleased(int keyCode, int gameAction) {
		//#debug
		System.out.println("MenuBar: handleKeyReleased(" + keyCode + ", " + gameAction + ") - isOpened=" + this.isOpened );
		if (this.isOpened) {			
			//#if polish.key.Menu:defined
				if (keyCode == MENU_KEY && this.commandsContainer.size() > 0) {
					//#if polish.blackberry && !hasTrackBallEvents
						CommandItem commandItem = (CommandItem) this.commandsContainer.getFocusedItem();
						if (commandItem != null) {
							if (commandItem.handleKeyReleased(0, Canvas.FIRE)) {
								return true;
							}
						}
					//#endif
					setOpen(false);
					return true;
				}
			//#endif
			if (isSelectOptionsMenuKey(keyCode, gameAction)) {
				this.isSoftKeyPressed = true;	
				CommandItem commandItem = (CommandItem) this.commandsContainer.getFocusedItem();
				//#if tmp.useInvisibleMenuBar && !polish.android
//					if (commandItem.command == this.hideCommand ) {
//						setOpen( false );
//						return true;
//					}
				//#endif
				return commandItem.handleKeyReleased(0, Canvas.FIRE);
			} else  if (isCloseOptionsMenuKey(keyCode, gameAction)
				//#if polish.key.ReturnKey:defined
					//#= || (keyCode == ${polish.key.ReturnKey})
				//#endif
			) {
				this.isSoftKeyPressed = true;
				int selectedIndex = this.commandsContainer.getFocusedIndex();
				if (!this.commandsContainer.handleKeyReleased(0, Canvas.LEFT)
						|| selectedIndex != this.commandsContainer.getFocusedIndex() ) 
				{
					CommandItem item = getCloseOptionsItem();
					if (item != null) {
						item.notifyItemPressedEnd();
					}
					setOpen( false );
				}
				//System.out.println("MenuBar is closing due to key " + keyCode);
				return true;
			} else {
				//#if tmp.useInvisibleMenuBar
					// handle hide command specifically:
//					if (  gameAction == Canvas.FIRE && ((CommandItem)this.commandsContainer.focusedItem).command == this.hideCommand ) {
//						setOpen( false );
//						return true;
//					}
				//#endif
				boolean handled = this.commandsContainer.handleKeyReleased(keyCode, gameAction);
				//System.out.println("menubar: container handled keyReleased " + keyCode + ": " + handled);
				if (handled) {
					setInitialized(false);
				} else { 
					if (keyCode >= Canvas.KEY_NUM1 && keyCode <= Canvas.KEY_NUM9) {
						int index = keyCode - Canvas.KEY_NUM1;
						if (index <= this.commandsContainer.size()) {
							CommandItem item = (CommandItem) this.commandsContainer.get(index);
							if (item.getAppearanceMode() != Item.PLAIN) {
								if (!item.isFocused) {
									this.commandsContainer.focusChild( index );
								}
								handled = item.handleKeyReleased(0, Canvas.FIRE);
								return handled;
							}
						}
					}
				}
				return true;				
			}
		} else { // menu is currently closed:
			//#if polish.key.Menu:defined
				if (keyCode == MENU_KEY && this.commandsContainer.size() > 0) {
					setOpen(true);
					return true;
				}
			//#endif

		//#if tmp.useMiddleCommand
			//#if polish.key.MiddleSoftKey:defined
				//#= if ( keyCode == ${polish.key.MiddleSoftKey}
			//#elif polish.key.CenterSoftKey:defined
				//#= if ( keyCode == ${polish.key.CenterSoftKey}
			//#else
				if ( getScreen().isGameActionFire(keyCode, gameAction)
			//#endif
					&& this.singleMiddleCommand != null && this.singleMiddleCommandItem.getAppearanceMode() != PLAIN) 
				{
					//#if polish.key.MiddleSoftKey:defined
						this.isSoftKeyPressed = true;			
					//#endif
					this.singleMiddleCommandItem.notifyItemPressedEnd();
					this.screen.callCommandListener(this.singleMiddleCommand);
					return true;			
				}
		//#endif
		if (this.singleLeftCommand != null && this.screen.isSoftKeyLeft(keyCode, gameAction) && this.singleLeftCommandItem.getAppearanceMode() != PLAIN) {
			this.isSoftKeyPressed = true;	
			this.singleLeftCommandItem.notifyItemPressedEnd();
			this.screen.callCommandListener(this.singleLeftCommand);
			return true;			
		} else if (this.singleRightCommand != null && this.screen.isSoftKeyRight(keyCode, gameAction) && this.singleRightCommandItem.getAppearanceMode() != PLAIN) {
			this.isSoftKeyPressed = true;	
			this.singleRightCommandItem.notifyItemPressedEnd();
			this.screen.callCommandListener(this.singleRightCommand);
			return true;			
		} else if (isOpenOptionsMenuKey(keyCode, gameAction) ) {
			this.isSoftKeyPressed = true;
			Item item = getOpenOptionsItem();
			if (item != null) {
				item.notifyItemPressedEnd();
			}
			//#if tmp.useInvisibleMenuBar
				if ( !this.isOpened && this.positiveCommand != null 
//						&& ((this.singleRightCommand != null && this.commandsContainer.size() == 3) ) )
						&& (this.singleRightCommand == null && this.commandsContainer.size() == 2) )  
				{
					// invoke positive command:
					this.screen.callCommandListener(this.positiveCommand);
					return true;
				} else 
			//#endif
			if (this.commandsList.size() > 0) {
				setOpen( true );
				return true;
			}
		//#if polish.blackberry && polish.hasTrackBallEvents && !polish.blackberry.dontOpenMenuOnTrackBallPress
			} else if (keyCode == -1204 && gameAction == Canvas.FIRE && !this.isOpened && this.commandsContainer.size() > 0) {
				// -1204 is BaseScreen.KEY_BB_FIRE, which is triggered when the trackball is pressed:
				setOpen(true);
				return true;
		//#endif
		}
	}
		return super.handleKeyReleased(keyCode, gameAction);
	}
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handleKeyRepeated(int, int)
	 */
	protected boolean handleKeyRepeated(int keyCode, int gameAction)
	{
		// do not convert key repeat in keypressed events for the menubar:
		return false;
	}
	
	/**
	 * Returns true if the specified relativeY is in the menubar 
	 * @return true if the specified relativeY is in the menubar, otherwise false
	 */
	protected boolean isInMenubar(int relativeY)
	{
		return relativeY > 0;
	}
	
	//#ifdef polish.hasPointerEvents
	protected boolean handlePointerPressed(int relX, int relY) {
		// check if one of the command buttons has been pressed:
		//int leftCommandEndX = this.singleLeftCommandItem.relativeX + this.singleLeftCommandItem.itemWidth;
		int rightCommandStartX = this.contentWidth/2; //this.singleRightCommandItem.relativeX;
		//#debug
		System.out.println("MenuBar: handlePointerPressed( relX=" + relX + ", relY=" + relY + " ), rightCommandStartXs = " + rightCommandStartX + " screenHeight=" + this.screenHeight);
		//#if !tmp.useInvisibleMenuBar
		if (isInMenubar(relY)) {
			//#if polish.api.windows
				if (windowsSipImage != null
						&& relX >= this.windowsSipX 
						&& relY >= this.windowsSipY 
						&& relX <= this.windowsSipX + windowsSipImage.getWidth()
						&& relY <= this.windowsSipY + windowsSipImage.getHeight()
				) {
					// Do nothing when windows sip image is pointer pressed.
					return true;
				}
			//#endif
			//System.out.println("menubar clicked");
			CommandItem selectedCommandItem = null;
			//#if tmp.useMiddleCommand
				if ( relX > this.singleMiddleCommandItem.relativeX 
						&& relX < this.singleMiddleCommandItem.relativeX + this.singleMiddleCommandItem.itemWidth
						&& this.singleMiddleCommandItem.getAppearanceMode() != PLAIN
						) 
				{
					selectedCommandItem = this.singleMiddleCommandItem;
				} else
			//#endif			
			if (relX > rightCommandStartX) {
				selectedCommandItem = this.singleRightCommandItem;
			} else {
				selectedCommandItem = this.singleLeftCommandItem;
			}
			if (selectedCommandItem != null) {
				selectedCommandItem.notifyItemPressedStart();
			}
			return true;
		// okay, y is above the menu bar, so let the commandContainer process the event:
		} else 
		//#endif
		if (this.isOpened) {
			relY -= this.commandsContainer.relativeY;
			relX -= this.commandsContainer.relativeX;
//			//#if tmp.RightOptions
//				// the menu is painted at the lower right corner:
//				relX -= this.screen.screenWidth - this.commandsContainer.itemWidth;
//			//#endif

			boolean handled = this.commandsContainer.handlePointerPressed(relX, relY);
			//#debug
			System.out.println("commandContainer.handlePointerPressed: " + handled);
			return true;
		}
		return false;
	}
	//#endif
	
	//#ifdef polish.hasPointerEvents
	protected boolean handlePointerReleased(int relX, int relY) {
		// check if one of the command buttons has been pressed:
		//int leftCommandEndX = this.singleLeftCommandItem.relativeX + this.singleLeftCommandItem.itemWidth;
		int rightCommandStartX = this.contentWidth/2; //this.singleRightCommandItem.relativeX;
		//#debug
		System.out.println("MenuBar: handlePointerReleased( relX=" + relX + ", relY=" + relY + " ), rightCommandStartXs = " + rightCommandStartX + " screenHeight=" + this.screenHeight);
		//#if !tmp.useInvisibleMenuBar
		if (isInMenubar(relY)) {
			//#if polish.api.windows
				if (windowsSipImage != null
						&& relX >= this.windowsSipX 
						&& relY >= this.windowsSipY 
						&& relX <= this.windowsSipX + windowsSipImage.getWidth()
						&& relY <= this.windowsSipY + windowsSipImage.getHeight()
				) {
					Keyboard.showSoftwareKeyboard( !Keyboard.isSoftwareKeyboardOpened() );
					return true;
				}
			//#endif
			CommandItem selectedCommandItem = null;
			//#if tmp.useMiddleCommand
				if ( relX > this.singleMiddleCommandItem.relativeX 
						&& relX < this.singleMiddleCommandItem.relativeX + this.singleMiddleCommandItem.itemWidth
						&& this.singleMiddleCommandItem.getAppearanceMode() != PLAIN
						) 
				{
					selectedCommandItem = this.singleMiddleCommandItem;
				} else
			//#endif			
			if (relX > rightCommandStartX) {
				selectedCommandItem = this.singleRightCommandItem;
			} else {
				selectedCommandItem = this.singleLeftCommandItem;
			}
			if (selectedCommandItem != null) {
				if (!selectedCommandItem.isPressed) {
					return false;
				}
				selectedCommandItem.notifyItemPressedEnd();
			}
			//System.out.println("menubar clicked");
			boolean isCloseKeySelected;
			boolean isOpenKeySelected;
			boolean isSelectKeySelected;
			//#if tmp.OkCommandOnLeft && tmp.RightOptions
				isCloseKeySelected = relX > rightCommandStartX;
				isOpenKeySelected =  isCloseKeySelected;
				isSelectKeySelected = !isCloseKeySelected;
			//#elif tmp.RightOptions
				isCloseKeySelected = relX < rightCommandStartX;
				isOpenKeySelected = !isCloseKeySelected;
				isSelectKeySelected = isOpenKeySelected;
			//#else
				isOpenKeySelected = relX < rightCommandStartX;
				isCloseKeySelected = !isOpenKeySelected;
				isSelectKeySelected = isOpenKeySelected;
			//#endif
			//System.out.println("isOpened=" + this.isOpened + ", isCloseKeySelected=" + isCloseKeySelected + ", isOpenKeySelected=" + isOpenKeySelected + ", isSelectKeySelected=" + isSelectKeySelected);
			if (this.isOpened) {
				if ( isSelectKeySelected ) {
					//System.out.println("selecting command from opened menu");
					setOpen( false );
					Command command = (Command) this.commandsList.get( this.commandsContainer.focusedIndex );
					this.screen.callCommandListener(command);
				} else if ( isCloseKeySelected ) {
					//System.out.println("closing menu");
					setOpen( false );
				}
			} else if (selectedCommandItem != null && selectedCommandItem.getAppearanceMode() != PLAIN) {
				if (selectedCommandItem == this.singleLeftCommandItem && this.singleLeftCommand != null) {
					//System.out.println("calling single left command");
					this.screen.callCommandListener(this.singleLeftCommand);
				} else if ( selectedCommandItem == this.singleRightCommandItem  && this.singleRightCommand != null) {
					//System.out.println("calling single right command");
					this.screen.callCommandListener(this.singleRightCommand);
				//#if tmp.useMiddleCommand
					} else if (selectedCommandItem == this.singleMiddleCommandItem  && this.singleMiddleCommand != null) {
						//System.out.println("calling single middle command");
						this.screen.callCommandListener(this.singleMiddleCommand);
				//#endif
				} else if (isOpenKeySelected && this.commandsList.size() > 0 )
				{
					setOpen( true );
				}
			}
			//System.out.println("nothing was clicked...");
			return true;
		// okay, y is above the menu bar, so let the commandContainer process the event:
		} else
		//#endif
		if (this.isOpened) {
			relY -= this.commandsContainer.relativeY;
			relX -= this.commandsContainer.relativeX;
//			//#if tmp.RightOptions
//				// the menu is painted at the lower right corner:
//				relX -= this.screen.screenWidth - this.commandsContainer.itemWidth;
//			//#endif

			boolean handled = this.commandsContainer.handlePointerReleased(relX, relY);
			//#debug
			System.out.println("commandContainer.handlePointerReleased: " + handled);
			if (!handled) {
				setOpen( false );
			}
			return true;
		}
		return false;
	}
	//#endif
	
	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handlePointerDragged(int, int, ClippingRegion)
	 */
	protected boolean handlePointerDragged(int relX, int relY, ClippingRegion repaintRegion) {
		// check if one of the command buttons has been pressed:
		//#debug
		System.out.println("MenuBar: handlePointerDragged( relX=" + relX + ", relY=" + relY + " ),  screenHeight=" + this.screenHeight);
		if (this.isOpened) {
			int y = relY - this.commandsContainer.relativeY;
			int x = relX = this.commandsContainer.relativeX;
//			//#if tmp.RightOptions
//				// the menu is painted at the lower right corner:
//				x -= this.screen.screenWidth - this.commandsContainer.itemWidth;
//			//#endif
			this.commandsContainer.handlePointerDragged( x, y, repaintRegion );
			return true;
		}
		//#if polish.css.pressed-style
			if (relY < 0) {
				if (this.singleLeftCommandItem.isPressed) {
					this.singleLeftCommandItem.notifyItemPressedEnd();
					return true;
				}
				if (this.singleRightCommandItem.isPressed) {
					this.singleRightCommandItem.notifyItemPressedEnd();
					return true;
				}
				//#if tmp.useMiddleCommand
				if (this.singleMiddleCommandItem.isPressed) {
					this.singleMiddleCommandItem.notifyItemPressedEnd();
					return true;
				}
				//#endif
			}
		//#endif
		return super.handlePointerDragged( relX, relY, repaintRegion );
	}
	//#endif
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		//#if !polish.Bugs.noTranslucencyWithDrawRgb
			if (this.overlayBackground == null) {
				//#if polish.color.overlay:defined
					int color = 0;
					//#= color =  ${polish.color.overlay};
					if ((color & 0xFF000000) != 0) {
						this.overlayBackground = new TranslucentSimpleBackground( color );
					}
				//#else
					this.overlayBackground = new TranslucentSimpleBackground( 0x88000000 );
				//#endif
	
			}
		//#endif
		//#if tmp.useInvisibleMenuBar
			this.background = null;
			this.border = null;
		//#else
			super.setStyle(style);
			//#ifdef polish.css.menubar-show-image-and-text
				Boolean showImageAndTextBool = style.getBooleanProperty( "menubar-show-image-and-text" );
				if (showImageAndTextBool != null) {
					this.showImageAndText = showImageAndTextBool.booleanValue();
				}
			//#endif
			//#ifdef polish.css.menubar-options-image
				String optionsUrl = style.getProperty("menubar-options-image");
				if (optionsUrl != null) {
					try {
						this.optionsImage = StyleSheet.getImage(optionsUrl, this, false);
						int imageHeight = this.optionsImage.getHeight();
						if (imageHeight > this.contentHeight) {
							this.contentHeight = imageHeight;
						}
					} catch (IOException e) {
						//#debug error
						System.out.println("Unable to load options-image " + optionsUrl + e );
					}
				}
			//#endif
			//#ifdef polish.css.menubar-select-image
				String selectUrl = style.getProperty("menubar-select-image");
				if (selectUrl != null) {
					try {
						this.selectImage = StyleSheet.getImage(selectUrl, this, false);
						int imageHeight = this.selectImage.getHeight();
						if (imageHeight > this.contentHeight) {
							this.contentHeight = imageHeight;
						}
					} catch (IOException e) {
						//#debug error
						System.out.println("Unable to load select-image " + selectUrl + e );
					}
				}
			//#endif
			//#ifdef polish.css.menubar-cancel-image
				String cancelUrl = style.getProperty("menubar-cancel-image");
				if (cancelUrl != null) {
					try {
						this.cancelImage = StyleSheet.getImage(cancelUrl, this, false);
						int imageHeight = this.cancelImage.getHeight();
						if (imageHeight > this.contentHeight) {
							this.contentHeight = imageHeight;
						}
					} catch (IOException e) {
						//#debug error
						System.out.println("Unable to load cancel-image " + cancelUrl + e );
					}
				}
			//#endif
			//#if polish.css.rightcommand-style
				Style rightStyle = (Style) style.getObjectProperty("rightcommand-style");
				if (rightStyle != null) {
					this.singleRightCommandItem.setStyle(rightStyle);
				}
			//#endif
			//#if polish.css.leftcommand-style
				Style leftStyle = (Style) style.getObjectProperty("leftcommand-style");
				if (leftStyle != null) {
					this.singleLeftCommandItem.setStyle(leftStyle);
				}
			//#endif
			//#if tmp.useMiddleCommand && polish.css.middlecommand-style
				Style middleStyle = (Style) style.getObjectProperty("middlecommand-style");
				if (middleStyle != null) {
					this.singleMiddleCommandItem.setStyle(middleStyle);
				}
			//#endif
			//#if polish.css.menu-style
				Style menuStyle = (Style) style.getObjectProperty("menu-style");
				if (menuStyle != null ) {
					this.commandsContainer.setStyle(menuStyle);
				}
			//#endif
			//#if polish.css.menu-item-style
				Style menuItmStyle = (Style) style.getObjectProperty("menu-item-style");
				if (menuItmStyle != null ) {
					setMenuItemStyle(menuItmStyle);
				}
			//#endif
				
		//#endif
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#resetStyle(boolean)
	 */
	public void resetStyle(boolean recursive) {
		super.resetStyle(recursive);
		if (recursive) {
			this.commandsContainer.resetStyle(recursive);
			this.singleLeftCommandItem.resetStyle(recursive);
			this.singleRightCommandItem.resetStyle(recursive);
			//#if tmp.useMiddleCommand
				this.singleMiddleCommandItem.resetStyle(recursive);
			//#endif
		}
	}

	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#animate(long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(long currentTime, ClippingRegion repaintRegion) {
		super.animate(currentTime, repaintRegion);
		if (this.isOpened) {
			this.commandsContainer.animate(currentTime, repaintRegion);
		}
		if (this.singleLeftCommandItem != null) {
			this.singleLeftCommandItem.animate(currentTime, repaintRegion);
		}
		if (this.singleRightCommandItem != null) {
			this.singleRightCommandItem.animate(currentTime, repaintRegion);
		}		
	}

	/**
	 * Adds the given command as a subcommand to the specified parent command.
	 * 
	 * @param parentCommand the parent command
	 * @param childCommand the child command
	 * @throws IllegalStateException when the parent command has not be added before
	 * @see #addSubCommand(Command, Command, Style)
	 */
	public void addSubCommand(Command childCommand, Command parentCommand) {
		if(this.menuItemStyle != null)
		{
			addSubCommand(childCommand, parentCommand, this.menuItemStyle);
		}
		else
		{
			//#style menuitem, menu, default
			addSubCommand(childCommand, parentCommand);
		}		
	}

	/**
	 * Adds the given command as a subcommand to the specified parent command.
	 * 
	 * @param parentCommand the parent command
	 * @param childCommand the child command
	 * @param commandStyle the style for the command
	 * @throws IllegalStateException when the parent command has not be added before
	 * @see #addSubCommand(Command, Command)
	 */
	public void addSubCommand(Command childCommand, Command parentCommand, Style commandStyle) {
        //#if tmp.useInvisibleMenuBar
	        if (parentCommand == this.positiveCommand) {
	            this.positiveCommand = null;
	        }
	    //#endif
		// find parent CommandItem:
		CommandItem parentCommandItem = (CommandItem) this.allCommands.get( parentCommand );
		if (parentCommand == this.singleLeftCommand ){
			addCommand( parentCommandItem );
			this.singleLeftCommand = null;
			//#if tmp.RightOptions
				if (this.singleRightCommand != null) {
					Command cmd = this.singleRightCommand;
					this.singleRightCommand = null;
					addCommand( cmd );
				}
			//#endif
		} else if (parentCommand == this.singleRightCommand ){
			addCommand( parentCommandItem );
			this.singleRightCommand = null;
			//#if !tmp.RightOptions
				if (this.singleLeftCommand != null) {
					Command cmd = this.singleLeftCommand;
					this.singleLeftCommand = null;
					addCommand( cmd );
				}
			//#endif
		} 
		if ( parentCommandItem == null ) {
			throw new IllegalStateException();
		}
		CommandItem child = new CommandItem( childCommand, parentCommandItem, commandStyle );
		this.allCommands.put( childCommand, child);
		parentCommandItem.addChild( child );
		if (this.isOpened) {
			setInitialized(false);
			repaint();
		}
	}

	/**
	 * Removes the given command as a subcommand.
	 * 
	 * @param childCommand the command to remove
	 * @param parentCommand the parent command of the command to remove.
	 * @throws IllegalStateException when the command has not been added before
	 */
	public void removeSubCommand(Command childCommand, Command parentCommand)
	{
		//#debug
		System.out.println("Removing subcommand " + childCommand.getLabel() );

		// Find command item for command.
		CommandItem childCommandItem = (CommandItem) this.allCommands.get( childCommand );

		if ( childCommandItem == null ) {
			throw new IllegalStateException();
		}

		// Find parent command item.
		CommandItem parentCommandItem = (CommandItem) this.allCommands.get( parentCommand);

		if ( parentCommandItem == null ) {
			throw new IllegalStateException();
		}

		// Remove command from parent command item.
		this.allCommands.remove(childCommand);
		parentCommandItem.removeChild(childCommand);

		// Repaint if needed.
		if (this.isOpened) {
			setInitialized(false);
			repaint();
		}
	}

	/**
	 * Adds the given command item to the list of commands at the appropriate place.
	 * 
	 * @param item the command item
	 */
	private void addCommand(CommandItem item ) {
		if (item == null) {
			//#debug error
			System.out.println("MenuBar.addCommand(CommandItem): Unable to add null CommandItem");
			return;
		}
		Command cmd = item.command;
		int priority = cmd.getPriority();
//		//#debug
//		System.out.println("Adding command " + cmd.getLabel() + " to the commands list...");
		if ( this.commandsList.size() == 0 ) {
			this.commandsList.add( cmd );
			this.commandsContainer.add( item );
		} else {
			// there are already several commands,
			// so add this cmd to the appropriate sorted position:
			Command[] myCommands = (Command[]) this.commandsList.toArray( new Command[ this.commandsList.size() ]);
			boolean inserted = false;
			for (int i = 0; i < myCommands.length; i++) {
				Command command = myCommands[i];
				if ( cmd == command ) {
					return;
				}
				if (command.getPriority() > priority ) {
					this.commandsList.add( i, cmd );
					this.commandsContainer.add(i, item);
					inserted = true;
					break;
				}
			}
			if (!inserted) {
				this.commandsList.add( cmd );
				this.commandsContainer.add( item );
			}
		}
	}

	/**
	 * Removes all commands from this MenuBar.
	 * This option is only available when the "menu" fullscreen mode is activated.
	 */
	public void removeAllCommands() {
		//#debug
		System.out.println("remove all commands");
		this.singleLeftCommand = null;
		this.singleRightCommand = null;
		this.singleMiddleCommand = null;
		this.commandsList.clear();
		this.allCommands.clear();
		this.commandsContainer.clear();
		this.singleLeftCommandItem.setText(null);
		this.singleLeftCommandItem.setImage( (Image)null);
		this.singleRightCommandItem.setText(null);
		this.singleRightCommandItem.setImage( (Image)null);
		//#if tmp.useMiddleCommand
			this.singleMiddleCommandItem.setText(null);
			this.singleMiddleCommandItem.setImage( (Image)null);
		//#endif
		setOpen( false );
		repaint();
	}

	/**
	 * Retrieves the CommandItem used for rendering the specified command. 
	 * 
	 * @param command the command
	 * @return the corresponding CommandItem or null when this command is not present in this MenuBar.
	 */
	public CommandItem getCommandItem(Command command) {
//		int index = this.commandsList.indexOf(command);
//		if (index != -1) {
//			return (CommandItem) this.commandsContainer.get(index);
//		} else if (command == this.singleLeftCommand){
//			return this.singleLeftCommandItem;
//		} else if (command == this.singleLeftCommand) {
//			return this.singleRightCommandItem;
//		} else {
//			for (int i = 0; i < this.commandsContainer.size(); i++) {
//				CommandItem item = (CommandItem) this.commandsContainer.get(i);
//				item = item.getChild(command);
//				if (item != null) {
//					return item;
//				}
//			}
//
//		}
		if (command == this.singleLeftCommand){
			return this.singleLeftCommandItem;
		} else if (command == this.singleRightCommand) {
			return this.singleRightCommandItem;
		//#if tmp.useMiddleCommand
		} else if (command == this.singleMiddleCommand ) {
			return this.singleMiddleCommandItem;
		//#endif
		} 
		return (CommandItem) this.allCommands.get( command );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#getItemAt(int, int)
	 */
	public Item getItemAt(int relX, int relY) {
		if (this.isOpened && relY < 0) {
			return this.commandsContainer.getItemAt(relX - this.commandsContainer.relativeX, relY - this.commandsContainer.relativeY);
		} else if (relY >= 0) {
			// test for left or right commanditem:
			Item item = this.singleLeftCommandItem.getItemAt(relX - this.singleLeftCommandItem.relativeX, relY - this.singleLeftCommandItem.relativeY);
			if (item != null) {
				return item;
			}
			item = this.singleRightCommandItem.getItemAt(relX - this.singleRightCommandItem.relativeX, relY - this.singleRightCommandItem.relativeY);
			if (item != null) {
				return item;
			}
		}
		return super.getItemAt(relX, relY);
	}

	/**
	 * @return true when this menubar should be positioned vertically, e.g. on the right side of the screen
	 */
	public boolean isOrientationVertical() {
		return this.isOrientationVertical;
	}

	public void setOrientationVertical( boolean isVertical ) {
		//#if polish.ScreenOrientationCanChange
			if (isVertical == this.isOrientationVertical) {
				// ignore
				return;
			}
			this.isOrientationVertical = isVertical;
			setInitialized(false);
		//#endif
	}
	
	public int getSpaceTop( int width, int height ) {
		return 0;
	}
	public int getSpaceLeft( int width, int height ) {
		return 0;
	}
	public int getSpaceRight( int width, int height ) {
		//#if polish.MenuBar.Position == right
			return getItemWidth( width, width, height );
		//#else
			//# return 0;
		//#endif
	}
	public int getSpaceBottom( int width, int height ) {
		//#if tmp.useInvisibleMenuBar
	        // init menubar if needed:
	        getItemHeight( width, width, height );
			//# return 0;
		//#elif polish.MenuBar.Position == right
			//# return 0;
		//#else
			return getItemHeight( width, width, height );
		//#endif
	}

	
	/**
	 * Retrieves the number of commands in this menubar.
	 * 
	 * @return the number of commands in this menubar.
	 */
	public int size() {
		return this.allCommands.size();
	}
	
	public Command getCommand(int index) {
		return (Command) this.allCommands.keys()[index];
	}

	
	public Style getMenuItemStyle()
	{
		return this.menuItemStyle;
	}

	public void setMenuItemStyle(Style menuItemStyle) {
		this.menuItemStyle = menuItemStyle;
		
		Object[] commandItems = this.allCommands.values();
		for (int i = 0; i < commandItems.length; i++)
		{
			CommandItem item = (CommandItem) commandItems[i];
			item.setStyle(menuItemStyle);
		}
		repaint();
	}

	/**
	 * Retrieves the container that contains CommandItems.
	 * Do not modify the container unless you know what you are doing.
	 * @return the container
	 */
	public Container getCommandsContainer() {
		return this.commandsContainer;
	}

	/**
	 * Checks if the specified command is already registered within this menubar.
	 * @param command the command
	 * @return true when the given command is already registered
	 */
	public boolean contains(Command command)
	{
		return (this.allCommands != null) && (this.allCommands.get(command) != null);
	}

	/**
	 * Adds a new layer of commands, e.g. used for popup ChoiceGroups
	 * @param layerCommands the new layer commands
	 */
	public void addCommandsLayer(Command[] layerCommands)
	{
		if (this.allCommands != null) {
			this.commandsBeforeLayer = new ArrayList();
			this.commandsBeforeLayer.addAll( this.allCommands.keys() );
		}
		removeAllCommands();
		for (int i = 0; i < layerCommands.length; i++)
		{
			Command command = layerCommands[i];
			addCommand( command );
		}
	}

	/**
	 * 
	 */
	public void removeCommandsLayer()
	{
		removeAllCommands();
		if (this.commandsBeforeLayer != null) {
			Object[] originalCommands = this.commandsBeforeLayer.getInternalArray();
			for (int i = 0; i < originalCommands.length; i++)
			{
				Command command = (Command) originalCommands[i];
				if (command == null) {
					break;
				}
				addCommand( command );
			}
		}
	}

	//#if tmp.useMiddleCommand
	private static Command tmpMiddleCommand = new Command( "", Command.OK, -1 );
	//#endif
	/**
	 * Informs the menubar about an possibly updated default command.
	 * When the menubar displays middle commands, the default command will be shown as the middle command.
	 * 
	 * @param cmd the new default command
	 */
	public void informDefaultCommand(Command cmd)
	{
		//#if tmp.useMiddleCommand
			//#debug
			System.out.println("setting default command " + cmd.getLabel());
			if (cmd != this.singleMiddleCommand) {
				removeCommand( cmd );
				if (this.singleMiddleCommand != null) {
					addCommand( tmpMiddleCommand );
				}
				this.singleMiddleCommand = cmd;
				this.singleMiddleCommandItem.setText( cmd.getLabel() );
			}
		//#endif
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#fireEvent(java.lang.String, java.lang.Object)
	 */
	public void fireEvent(String eventName, Object eventData)
	{
		super.fireEvent(eventName, eventData);
		this.singleLeftCommandItem.fireEvent(eventName, eventData);
		this.singleRightCommandItem.fireEvent(eventName, eventData);
		//#if tmp.useMiddleCommand
			this.singleMiddleCommandItem.fireEvent(eventName, eventData);
		//#endif
		this.commandsContainer.fireEvent(eventName, eventData);
	}

	/**
	 * Focuses the child command item of this menubar.
	 * @param item the item
	 */
	public void focusChild(Item item) {
		if (this.isOpened) {
			int index = this.commandsContainer.indexOf(item);
			if (index != -1) {
				this.commandsContainer.focusChild(index);
			} else {
				CommandItem focItem = (CommandItem) this.commandsContainer.focusedItem;
				if (focItem != null) {
					focItem.focusChild(item);	
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#hideNotify()
	 */
	protected void hideNotify()
	{
		super.hideNotify();
		if (this.isOpened) {
			setOpen(false);
		}
		if (this.singleLeftCommandItem != null) {
			this.singleLeftCommandItem.hideNotify();
		}
		if (this.singleRightCommandItem != null) {
			this.singleRightCommandItem.hideNotify();
		}
		//#if tmp.useMiddleCommand
			if (this.singleMiddleCommandItem != null) {
				this.singleMiddleCommandItem.hideNotify();
			}
		//#endif
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#showNotify()
	 */
	protected void showNotify()
	{
		super.showNotify();
		if (this.singleLeftCommandItem != null) {
			this.singleLeftCommandItem.showNotify();
		}
		if (this.singleRightCommandItem != null) {
			this.singleRightCommandItem.showNotify();
		}
		//#if tmp.useMiddleCommand
			if (this.singleMiddleCommandItem != null) {
				this.singleMiddleCommandItem.showNotify();
			}
		//#endif
	}

	public Object[] getCommands() {
		return this.allCommands.keys();
	}

	
	
//#ifdef polish.MenuBar.additionalMethods:defined
	//#include ${polish.MenuBar.additionalMethods}
//#endif

}
