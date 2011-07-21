//#condition polish.usePolishGui
/*
 * Created on 27-Feb-2006 at 7:36:20.
 * 
 * Copyright (c) 2004-2006 Robert Virkus / Enough Software
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

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import de.enough.polish.util.TextUtil;

/**
 * <p>Provides a TextField that provides the user with possible matches for the current input.</p>
 *
 * <p>Copyright Enough Software 2006 - 2009</p>
 * <pre>
 * history
 *        27-Feb-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ChoiceTextField 
//#if polish.LibraryBuild
	extends FakeTextFieldCustomItem
//#else
	//# extends TextField 
//#endif
{

	/** 
	 * The matching mode that selects choices that start with the same characters as the current input
	 */
	public static final int MATCH_STARTS_WITH = 0;
	/** 
	 * The matching mode that selects choices that contain the same characters as the current input
	 */
	public static final int MATCH_INDEX_OF = 1;

	private final boolean isAllowFreeTextEntry;
	private final Container choicesContainer;
	private String[] choices;
	private String[] lowerCaseChoices;
	private int numberOfMatches;
	private boolean isInChoice;
	private Item[] choiceItems;
	private String lastMatchingText;
	private Style originalStyle;
	private Style focusingStyle;
	private int matchMode;
	private boolean reenableCaretFlashing = true;
	private int choicesYOffsetAdjustment;
	private boolean isOpen;
	private Style choiceItemStyle;
	private String appendChoiceDelimiter;
	private boolean isAppendMode;
	private int appendDelimiterIndex = -1;
	private boolean choiceTriggerEnabled;
	private char choiceTrigger;
	private boolean choiceTriggerAllowInputBeforeTrigger;

	/**
	 * Creates a new ChoiceTextField.
	 * 
	 * @param label the label
	 * @param text the current text
	 * @param maxSize the maximum size for the input
	 * @param constraints input constraints, TextField.ANY for no constraints
	 * @param availableChoices a list of available texts for the user
	 * @param allowFreeTextEntry true when the user should be allowed to enter any text that does not match any existing choice
	 */
	public ChoiceTextField(String label, String text, int maxSize, int constraints, String[] availableChoices, boolean allowFreeTextEntry) {
		this(label, text, maxSize, constraints, availableChoices, allowFreeTextEntry, false, ";", null);
	}
	
	/**
	 * Creates a new ChoiceTextField.
	 * 
	 * @param label the label
	 * @param text the current text
	 * @param maxSize the maximum size for the input
	 * @param constraints input constraints, TextField.ANY for no constraints
	 * @param availableChoices a list of available texts for the user
	 * @param allowFreeTextEntry true when the user should be allowed to enter any text that does not match any existing choice
	 * @param style the style for this item
	 */
	public ChoiceTextField(String label, String text, int maxSize, int constraints, String[] availableChoices, boolean allowFreeTextEntry, Style style) {
		this(label, text, maxSize, constraints, availableChoices, allowFreeTextEntry, false, ";", style );		
	}
	
	/**
	 * Creates a new ChoiceTextField.
	 * 
	 * @param label the label
	 * @param text the current text
	 * @param maxSize the maximum size for the input
	 * @param constraints input constraints, TextField.ANY for no constraints
	 * @param availableChoices a list of available texts for the user
	 * @param allowFreeTextEntry true when the user should be allowed to enter any text that does not match any existing choice
	 * @param appendChoice true when the selected choices should be appended to the text rather than replacing the text
	 * @param appendChoiceDelimiter the character that separates several selections, e.g. '\n' or ';' 
	 */
	public ChoiceTextField(String label, String text, int maxSize, int constraints, String[] availableChoices, boolean allowFreeTextEntry, boolean appendChoice, String appendChoiceDelimiter) {
		this(label, text, maxSize, constraints, availableChoices, allowFreeTextEntry, appendChoice, appendChoiceDelimiter, null);
	}
	
	/**
	 * Creates a new ChoiceTextField.
	 * 
	 * @param label the label
	 * @param text the current text
	 * @param maxSize the maximum size for the input
	 * @param constraints input constraints, TextField.ANY for no constraints
	 * @param availableChoices a list of available texts for the user
	 * @param allowFreeTextEntry true when the user should be allowed to enter any text that does not match any existing choice
	 * @param appendChoice true when the selected choices should be appended to the text rather than replacing the text
	 * @param appendChoiceDelimiter the character that separates several selections, e.g. "\n" or ";" or null. 
	 * @param style the style for this item
	 */
	public ChoiceTextField(String label, String text, int maxSize, int constraints, String[] availableChoices, boolean allowFreeTextEntry, boolean appendChoice, String appendChoiceDelimiter, Style style) {
		super(label, text, maxSize, constraints, style);
		this.choices = availableChoices;
		if (availableChoices != null) {
			this.lowerCaseChoices = new String[ availableChoices.length ];
			for (int i = 0; i < availableChoices.length; i++) {
				String choice = availableChoices[i];
				this.lowerCaseChoices[i] = choice.toLowerCase();
			}
			this.choiceItems = new Item[ availableChoices.length ];
		}
		this.isAllowFreeTextEntry = allowFreeTextEntry;
		this.choicesContainer = new Container( false );
		//#if polish.Container.allowCycling != false
			this.choicesContainer.allowCycling = false;
		//#endif
		//#if polish.usePolishGui && !polish.LibraryBuild
			//# this.choicesContainer.parent = this;
		//#endif	
		this.isAppendMode = appendChoice;
		this.appendChoiceDelimiter = appendChoiceDelimiter;
		if (appendChoiceDelimiter != null && appendChoiceDelimiter.length() > 0) {
			this.emailSeparatorChar = appendChoiceDelimiter.charAt(0);
		}
	}
	
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.FakeTextFieldCustomItem#initContent(int, int)
	 */
	protected void initContent(int firstLineWidth, int availWidth, int availHeight) {
		super.initContent(firstLineWidth, availWidth, availHeight);
		this.choicesContainer.relativeY = this.contentHeight + this.paddingVertical;
 	}

	/**
	 * Enables that available choices should (only) be shown after the specified character is entered.
	 * This method automatically enables the append mode.
	 * 
	 * @param choiceTrigger the trigger for showing choice
	 * @param allowChoicesBeforeTrigger true when the user should be able to add choices before he has entered the trigger character
	 */
	public void setChoiceTrigger( char choiceTrigger, boolean allowChoicesBeforeTrigger  ) {
		this.isAppendMode = true;
		this.choiceTriggerEnabled = true;
		this.choiceTrigger = choiceTrigger;
		this.choiceTriggerAllowInputBeforeTrigger = allowChoicesBeforeTrigger;
	}


	
	/**
	 * Sets the available choices.
	 * Use this method in conjunction with an ItemStateListener for using complex rules for creating choices.
	 * 
	 * @param choices the new choices, null when no choices are given
	 */
	public void setChoices( String[] choices ) {
		this.choicesContainer.clear();
		if  ( choices == null ) {
			this.choiceItems = new Item[ 0 ];
			openChoices( false );
			return;
		}
		this.choiceItems = new Item[ choices.length ];
		for (int i = 0; i < choices.length; i++) {
			String choiceText = choices[i];
			Item item = new ChoiceItem( choiceText, null, Choice.IMPLICIT, this.choiceItemStyle );
			this.choiceItems[i] = item;
			this.choicesContainer.add( item );
		}
		this.choices = choices;
		if (this.isFocused) {
			openChoices( choices.length > 0 );
		}
	}
	
	
	/**
	 * Sets the available choices.
	 * Use this method in conjunction with an ItemStateListener for using complex rules for creating choices.
	 * The given items should implement the "toString()" method and return the correct string value for the text field.
	 * 
	 * @param choices the new choices, null when no choices are available
	 */
	public void setChoices( Item[] choices ) {
		this.choicesContainer.clear();
		if  ( choices == null ) {
			this.choiceItems = new Item[ 0 ];
			openChoices( false );
			return;
		}
		this.choiceItems = choices;
		for (int i = 0; i < choices.length; i++) {
			Item item = choices[i];
			this.choicesContainer.add( item );
		}
		if (this.isFocused) {
			openChoices( choices.length > 0 );
		}
	}
	
	/**
	 * Sets the matching algorithm that is used for finding out whether an available choices matches the input of the user.
	 * 
	 * @param mode the matching mode, the default mode is ChoiceTextField.MATCH_STARTS_WITH, so the user input is compared with the start of the available choices. 
	 * @see #MATCH_STARTS_WITH
	 * @see #MATCH_INDEX_OF
	 * @see #setChoices(Item[]) for using complex matching rules
	 * @see #setChoices(String[]) for using complex matching rules
	 */
	public void setMatchMode( int mode ) {
		this.matchMode = mode;
	}
	
	/**
	 * Retrieves the matching algorithm that is used for finding out whether an available choices matches the input of the user.
	 * 
	 * @return the matching mode, the default mode is ChoiceTextField.MATCH_STARTS_WITH, so the user input is compared with the start of the available choices. 
	 * @see #MATCH_STARTS_WITH
	 * @see #MATCH_INDEX_OF
	 * @see #setChoices(Item[]) for using complex matching rules
	 * @see #setChoices(String[]) for using complex matching rules
	 */
	public int getMatchMode() {
		return this.matchMode;
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextField#defocus(de.enough.polish.ui.Style)
	 */
	protected void defocus(Style origStyle) {
		super.defocus(origStyle);
		if (!this.isAllowFreeTextEntry && this.numberOfMatches > 0 && this.choicesContainer.size() > 0) {
			Item item = this.choicesContainer.get( 0 );
			if (item instanceof StringItem) {
				setString( ((StringItem)item).getText() );
			} else {
				setString( item.toString() );
			}			
		}
		this.numberOfMatches = 0;
		this.choicesContainer.clear();
		openChoices(false);
	}
		

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.FakeCustomItem#animate(long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(long currentTime, ClippingRegion repaintRegion)
	{
		super.animate(currentTime, repaintRegion);
		if (this.isFocused && this.numberOfMatches > 0) {
			this.choicesContainer.animate( currentTime, repaintRegion );
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextField#focus(de.enough.polish.ui.Style, int)
	 */
	protected Style focus(Style focStyle, int direction) {
		this.originalStyle = super.focus(focStyle, direction);
		this.focusingStyle = this.style;
		return this.originalStyle;
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextField#handleKeyPressed(int, int)
	 */
	protected boolean handleKeyPressed(int keyCode, int gameAction) {
		//#debug
		System.out.println("handleKeyPressed( keyCode=" + keyCode + ", gameAction=" + gameAction +  ", isInChoice=" + this.isInChoice + ", isOpen=" + this.isOpen + ", matches=" + this.numberOfMatches +" )");
		boolean isFireGameAction = getScreen().isGameActionFire(keyCode, gameAction);
		if (this.isInChoice) {
			if ( this.choicesContainer.handleKeyPressed(keyCode, gameAction) ) {
				//#debug
				System.out.println("keyPressed handled by choices container");
				return true;
			}
			if (isFireGameAction) {
				// option has been selected!
				Item item = this.choicesContainer.getFocusedItem();
				item.notifyItemPressedStart();
			} else {
				enterChoices( false );
			}
			return true;
		} else if ( (gameAction == Canvas.DOWN && keyCode != Canvas.KEY_NUM8)
				&& this.numberOfMatches > 0) 
		{
			//System.out.println("focusing choices container");
			enterChoices( true );
			return true;
		} else if (isFireGameAction) {
			if (this.isOpen) {
				notifyItemPressedStart();
				this.numberOfMatches = 0;
				//openChoices( false );
				return true;
			}
			// open all available choices:
			if (this.choiceTriggerEnabled && !this.choiceTriggerAllowInputBeforeTrigger) {
				String currentText = getString();
				if (currentText == null || currentText.length() == 0 || currentText.charAt( currentText.length() -1 ) != this.choiceTrigger) {
					//System.out.println("foward fire to textfield...");
					return super.handleKeyPressed(keyCode, gameAction);
				}

			}
			//if (this.numberOfMatches == 0) {
				if (this.choices == null) {
					return super.handleKeyPressed(keyCode, gameAction);
				}
				notifyItemPressedStart();
//				if (this.numberOfMatches == this.choices.length) {
//					this.numberOfMatches = 0; // close choices container
//					openChoices( false );
//				} else {
					this.appendDelimiterIndex = -1;
//					if (!this.isAllowFreeTextEntry) {
//						
//					}
					this.choicesContainer.clear();
					for (int i = 0; i < this.choices.length; i++) {
						Item item = this.choiceItems[i];
						if (item == null) {
							// create new ChoiceItem (lazy initialisation)
							item = new ChoiceItem( this.choices[i], null, Choice.IMPLICIT, this.choiceItemStyle );
						}
						this.choicesContainer.add( item );
					}
					this.numberOfMatches = this.choicesContainer.size();
					//openChoices( true );
//				}
			//}
			return true;
		//#if polish.Key.ReturnKey:defined
		} else if (this.isOpen
				//#= && (keyCode == ${polish.Key.ReturnKey})
		) {
			openChoices(false);
			return true;
		//#endif
		}
		return super.handleKeyPressed(keyCode, gameAction);
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextField#handleKeyReleased(int, int)
	 */
	protected boolean handleKeyReleased(int keyCode, int gameAction) {
		//#debug
		System.out.println("handleKeyReleased( keyCode=" + keyCode + ", gameAction=" + gameAction +  ", isInChoice=" + this.isInChoice + ", isOpen=" + this.isOpen + ", matches=" + this.numberOfMatches +" )");
		boolean isFireGameAction = getScreen().isGameActionFire(keyCode, gameAction);
		if (this.isInChoice) {
			if ( this.choicesContainer.handleKeyReleased(keyCode, gameAction) ) {
				//#debug
				System.out.println("handleKeyReleased handled by choices container");
				if (this.choicesContainer.internalX != Item.NO_POSITION_SET) {
					this.internalX = this.choicesContainer.relativeX + this.choicesContainer.internalX;
					this.internalY = this.choicesContainer.relativeY + this.choicesContainer.internalY;
				}
				return true;
			}
			//System.out.println("focusing textfield again, isFocused=" + this.isFocused);
			
			if (isFireGameAction) {
				enterChoices( false );
				// option has been selected!
				Item item = this.choicesContainer.getFocusedItem();
				item.notifyItemPressedEnd();
				String choiceText;
				if ( item instanceof ChoiceItem ) {
					choiceText = ((ChoiceItem) item).getText();
				} else if (item != null) {
					choiceText = item.toString();
				} else {
					return false;
				}
				if (this.isAppendMode) {
					String currentText = getString();
					if ( (currentText != null) ) {
						if (this.appendDelimiterIndex != -1 && this.appendDelimiterIndex < currentText.length() ) {
							currentText = currentText.substring( 0, this.appendDelimiterIndex );
						}
						if ( choiceText.startsWith( currentText) ) {
							if (this.appendChoiceDelimiter != null ) {
								choiceText += this.appendChoiceDelimiter;
							}
						} else {
							if (this.appendChoiceDelimiter == null) {
								choiceText = currentText + choiceText; 
							} else {
								if ( this.choiceTriggerEnabled) {
									choiceText = currentText + choiceText + this.appendChoiceDelimiter;
								} else if ( currentText.endsWith( this.appendChoiceDelimiter ) ) {
									choiceText = currentText + choiceText + this.appendChoiceDelimiter;
								} else {
									choiceText = currentText + this.appendChoiceDelimiter + choiceText + this.appendChoiceDelimiter;
								}
							}
						}
					} else if (this.appendChoiceDelimiter != null) {
						choiceText += this.appendChoiceDelimiter;
					}
					this.appendDelimiterIndex = choiceText.length();
				}
				if (!this.isAllowFreeTextEntry) {
					this.lastMatchingText = choiceText;
				}
				//#if polish.usePolishGui	
					setString( choiceText );
					//# setCaretPosition( choiceText.length() );
				//#endif
				this.numberOfMatches = 0;
				openChoices( false );
				super.notifyStateChanged();
			}
			return true;
		} 
		else if (isFireGameAction)
		{
			notifyItemPressedEnd();
			openChoices(!this.isOpen);
			return true;
		}
		return super.handleKeyReleased(keyCode, gameAction);
	}
	
	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handlePointerPressed(int, int)
	 */
	protected boolean handlePointerPressed(int x, int y) {
		boolean handled = super.handlePointerPressed(x, y);
		//#debug
		System.out.println("handlePointerPressed(" + x + ", " + y + ") for " + this + ", isOpen=" + this.isOpen + ", super handled=" + handled);
		if (!handled && this.isOpen) {
			handled = this.choicesContainer.handlePointerPressed(x - this.contentX, y - (this.choicesContainer.relativeY + this.contentY) );
			if (handled && !this.isInChoice) {
				this.isInChoice = true;
			} else {
				openChoices(false);
				handled = true;
			}
		}
		return handled;
	}
	//#endif
	
	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#handlePointerPressed(int, int)
	 */
	protected boolean handlePointerReleased(int x, int y) {
		boolean handled = super.handlePointerReleased(x, y);
		//#debug
		System.out.println("handlePointerReleased(" + x + ", " + y + ") for " + this + ", isOpen=" + this.isOpen + ", super handled=" + handled);
		if (!handled && this.isOpen) {
			handled = this.choicesContainer.handlePointerReleased(x - this.contentX, y - (this.choicesContainer.relativeY + this.contentY) );
			if (!handled && this.choicesContainer.focusedItem != null) {
				this.isInChoice = true;
				handleKeyReleased( 0, Canvas.FIRE );
			}
			handled = true;
		}
		return handled;
	}
	//#endif


	private void enterChoices( boolean enter ) {
		//#debug
		System.out.println("enter choices: " + enter + ", have been entered already: " + this.isInChoice);
		if (enter) {
			this.choicesContainer.focusChild(0);
			setStyle( this.originalStyle );
			//#if polish.usePolishGui && !polish.LibraryBuild
				this.flashCaret = false;
				this.showCaret = false;
				if (!this.isInChoice) {
					//# getScreen().removeItemCommands( this );
				}
			//#endif
		} else {
			this.internalX = Item.NO_POSITION_SET;
			setStyle( this.focusingStyle );
			this.flashCaret = this.reenableCaretFlashing;
			this.showCaret = true;
			this.choicesContainer.setScrollYOffset(0, false );
			// move focus to TextField input again
			this.choicesContainer.defocus( this.originalStyle );
			if (this.isInChoice) {
				//#if polish.usePolishGui  && !polish.LibraryBuild
					showCommands();
				//#endif
			}
		}
		this.isInChoice = enter;
	}
	
	private void openChoices( boolean open ) {
		//#debug
		System.out.println("open choices: " + open + ", have been opened already:" + this.isOpen);
		this.choicesContainer.focusChild( -1 );
		if (open) {
			if (this.choicesContainer.size() == 0) {
				open = false;
			} else if (this.parent instanceof Container) {
				Container parentContainer = (Container) this.parent;
				if ( parentContainer.enableScrolling ) {
					int availWidth = this.itemWidth - (this.marginLeft + this.marginRight);
					int choicesHeight = this.choicesContainer.getItemHeight( availWidth, availWidth, this.availableHeight	 );
					int choicesBottomY = this.contentY + this.contentHeight + this.paddingVertical + choicesHeight;
					//#debug
					System.out.println("choicesHeight " + choicesHeight + ", choicesBottom=" + choicesBottomY + ", parent.height=" + parentContainer.availableHeight  );
					int parentYOffset = parentContainer.getScrollYOffset();
					int overlap = choicesBottomY - (parentContainer.getContentScrollHeight() - (this.relativeY + parentYOffset));
					//System.out.println("overlap=" + overlap );
					if (overlap > 0) {
						// try to scroll up this item, so that the user sees all matches:
						int yOffsetAdjustment = Math.min( this.relativeY + parentYOffset, overlap );
						this.choicesYOffsetAdjustment = yOffsetAdjustment;
						//#debug
						System.out.println("Adjusting yOffset of parent by " + yOffsetAdjustment );
						parentContainer.setScrollYOffset( parentYOffset - yOffsetAdjustment, true );
						//System.out.println("choice.itemHeight=" + this.choicesContainer.itemHeight + ", parentContainer.availableHeight=" + parentContainer.availableHeight + ", (this.contentY + this.contentHeight + this.paddingVertical)=" + (this.contentY + this.contentHeight + this.paddingVertical) + ", children.relativeY=" + this.choicesContainer.relativeY );
						//TODO this needs some finetuning!
						int itHeight = this.itemHeight;
						int ctHeight = this.contentY + this.contentHeight + this.paddingVertical;
						int max = Math.max( itHeight, ctHeight);
						this.choicesContainer.setScrollHeight( parentContainer.getContentScrollHeight()  - max );
					} else {
						this.choicesYOffsetAdjustment = 0;
					}
				}
			}			
		} else {
			if (this.isInChoice) {
				enterChoices( false );
			}
			this.choicesContainer.clear();
			if (this.choicesYOffsetAdjustment != 0 && this.parent instanceof Container) {
				Container parentContainer = (Container) this.parent;
				parentContainer.setScrollYOffset( parentContainer.getScrollYOffset() + this.choicesYOffsetAdjustment, true );
				this.choicesYOffsetAdjustment = 0;
			}
		}
		this.isOpen = open;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextField#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		super.paintContent(x, y, leftBorder, rightBorder, g);
		if ( this.isFocused && this.isOpen && this.numberOfMatches > 0 ) {
			// paint container:
			y += this.contentHeight + this.paddingVertical;
			this.choicesContainer.paint(x, y, leftBorder, rightBorder, g);			
		}
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.Item#notifyStateChanged()
	 */
	public void notifyStateChanged() {
		Screen scr = getScreen();
		if (scr != null && scr.itemStateListener != null ) {
			// let the external item state listener do the work
			super.notifyStateChanged();
		} else {
			// find out possible matches yourself:
			if ( this.lowerCaseChoices == null ) {
				return; // no choices are known
			}
			if (this.isOpen) {
				this.choicesContainer.focusChild(-1);
			}
			String currentText = getString();
			//#debug
			System.out.println("notifyStateChanged: text=[" + currentText + "]");
			if (currentText != null) {
				if (this.isAppendMode) {
					if (this.appendChoiceDelimiter != null) {
						int caretPosition = getCaretPosition();
						if (caretPosition < currentText.length() && caretPosition != -1) {
							currentText = currentText.substring( 0, caretPosition );
						}
						this.appendDelimiterIndex = TextUtil.lastIndexOf( currentText, this.appendChoiceDelimiter );
						if (this.appendDelimiterIndex != -1) {
							currentText = currentText.substring( this.appendDelimiterIndex + 1 );
						}
					} else if (this.appendDelimiterIndex != -1 && this.appendDelimiterIndex < currentText.length()) {
						currentText = currentText.substring( this.appendDelimiterIndex );
					}
				}
				if (this.choiceTriggerEnabled && currentText.length() > 0) {
					int lastChar = currentText.charAt( currentText.length() - 1);
					if (lastChar == this.choiceTrigger && !this.isOpen) {
						handleKeyPressed(0, Canvas.FIRE );
						handleKeyReleased(0, Canvas.FIRE );
					} 
					return;
				}
				currentText = currentText.toLowerCase();
				// cycle through available choices and add the ones resulting in matches.
				// There is one special case, though: when only one of the available choices
				// can be used (=no free text entry alllowed), we need to ensure that there is at least one match, before updating
				// the choicesContainer:
				if (this.isAllowFreeTextEntry) {
					this.choicesContainer.clear();
				}
				int foundMatches = 0;
				for (int i = 0; i < this.lowerCaseChoices.length; i++) {
					String choice = this.lowerCaseChoices[i];
					if ( matches( currentText, choice ) ) {
						// found a match!
						foundMatches++;
						Item item = this.choiceItems[i];
						if (item == null) {
							// create new ChoiceItem (lazy initialisation)
							item = new ChoiceItem( this.choices[i], null, Choice.IMPLICIT, this.choiceItemStyle );
						}
						//#debug
						System.out.println("found match: " + choice);
						this.choicesContainer.add( item );
					}
				}
				// handle case when there are no matches, but only matches are allowed as the input:
				if ( this.isAllowFreeTextEntry ) {
					this.numberOfMatches = foundMatches;
				} else {
					if ( foundMatches == 0 ) {
						// re-set the text to the last match:
						setString( this.lastMatchingText );
					} else {
						// remove all previous matches and remember this text:						
						this.lastMatchingText = getString();
						
						for ( int i = this.numberOfMatches; --i >= 0; ) {
							this.choicesContainer.remove( 0 );
						}
						//TODO: why not using this.choicesContainer.clear(); ?
						this.numberOfMatches = foundMatches;
					}
					
				}
			}
			openChoices( this.numberOfMatches > 0 );
		}
	}
	
	

	/**
	 * Checks if the input and the available choice do match.
	 * 
	 * @param currentText the current input of the user
	 * @param choice one of the available choices
	 * @return true when they match - this depends on this chosen matching, usually the start need to be equal
	 * @see #setMatchMode(int)
	 */
	private boolean matches(String currentText, String choice) {
		if (this.matchMode == MATCH_STARTS_WITH) {
			return choice.startsWith( currentText );
		} else {
			return choice.indexOf(currentText) != -1; 
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextField#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		//#if polish.usePolishGui
			//# super.setStyle(style);
		//#endif
		//#ifdef polish.css.textfield-caret-flash
			Boolean flashCursorBool = style.getBooleanProperty( "textfield-caret-flash" );
			if ( flashCursorBool != null ) {
				this.reenableCaretFlashing = flashCursorBool.booleanValue();
			}
		//#endif
		//#if polish.css.choicetextfield-containerstyle
			Style containerstyle = (Style) style.getObjectProperty("choicetextfield-containerstyle");
			if (containerstyle != null) {
				this.choicesContainer.setStyle( containerstyle );
			}
		//#endif
		//#if polish.css.choicetextfield-choicestyle
			Style choicestyle = (Style) style.getObjectProperty("choicetextfield-choicestyle");
			if (choicestyle != null) {
				this.choiceItemStyle = choicestyle;
				if (this.choiceItems != null) {
					for (int i=0; i<this.choiceItems.length; i++) {
						Item item = this.choiceItems[i];
						if (item != null) {
							item.setStyle( choicestyle );
							if (item.isFocused) {
								item.focus( null, 0 );
							}
						}
					}
				}
			}
		//#endif
	}
}
