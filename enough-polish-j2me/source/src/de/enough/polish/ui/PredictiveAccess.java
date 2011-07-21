//#condition polish.usePolishGui && polish.TextField.usePredictiveInput && polish.TextField.useDirectInput && !(polish.blackberry || polish.android)

package de.enough.polish.ui;

import javax.microedition.lcdui.Canvas;

import javax.microedition.lcdui.Graphics;
import javax.microedition.rms.RecordStoreException;

import de.enough.polish.predictive.TextBuilder;
import de.enough.polish.predictive.TextElement;
import de.enough.polish.predictive.array.ArrayReader;
import de.enough.polish.predictive.array.ArrayTextBuilder;
import de.enough.polish.predictive.trie.TrieProvider;
import de.enough.polish.predictive.trie.TrieReader;
import de.enough.polish.predictive.trie.TrieSetupCallback;
import de.enough.polish.predictive.trie.TrieTextBuilder;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.Locale;
import de.enough.polish.util.TextUtil;
import de.enough.polish.predictive.trie.TrieSetup;

public class PredictiveAccess implements TrieSetupCallback{
	private TextField parent;
	
	public static final int ORIENTATION_BOTTOM = 0;

	public static final int ORIENTATION_TOP = 1;

	public static final int TRIE = 0;

	public static final int ARRAY = 1;
	
	//#if polish.predictive.globalSwitch
		ArrayList registeredFields;
	//#endif

	/**
	 * The provider for retrieving rms records, implemented as a static variable
	 * for use of a single provider in multiple textfields    
	 */
	public static TrieProvider PROVIDER = new TrieProvider();

	//#ifdef polish.predictive.command.install.priority:defined
	//#= private static int INSTALL_PRIORITY = ${polish.predictive.command.install.priority};
	//#else
		private static int INSTALL_PRIORITY = 10;
	//#endif
	
	/**
	 * The command for starting the setup dialog of the predictive input
	 */
	public static Command INSTALL_PREDICTIVE_CMD = new Command(Locale.get("polish.predictive.command.install"), Command.ITEM, INSTALL_PRIORITY);

	//#ifdef polish.predictive.command.enable.priority:defined
	//#= private static int ENABLE_PRIORITY = ${polish.predictive.command.enable.priority};
	//#else
		private static int ENABLE_PRIORITY = 10;
	//#endif
	
	/**
	 * The command for enabling the predictive input
	 */
	public static Command ENABLE_PREDICTIVE_CMD = new Command(Locale.get("polish.predictive.command.enable"), Command.SCREEN, ENABLE_PRIORITY);
	
	//#ifdef polish.predictive.command.disable.priority:defined
	//#= private static int DISABLE_PRIORITY = ${polish.predictive.command.disable.priority};
	//#else
		private static int DISABLE_PRIORITY = 10;
	//#endif
	
	/**
	 * The command for disabling the predictive input and returning to the standard input method
	 */
	public static Command DISABLE_PREDICTIVE_CMD = new Command(Locale.get("polish.predictive.command.disable"), Command.SCREEN, DISABLE_PRIORITY);

	//#ifdef polish.predictive.command.addword.priority:defined
	//#= private static int ADDWORD_PRIORITY = ${polish.predictive.command.addword.priority};
	//#else
		private static int ADDWORD_PRIORITY = 10;
	//#endif
	
	/**
	 * The command to start the dialog to add a custom word to the predictive dictionary
	 */
	public static Command ADD_WORD_CMD = new Command(Locale.get("polish.predictive.registerNewWord.command"), Command.SCREEN, ADDWORD_PRIORITY);

	/**
	 * Holds the key code for the space key of the model running the application
	 */
	private static int SPACE_BUTTON = getSpaceKey();
	
	/**
	 * The indicator which is shown in the info box of a textfield indication the predictive
	 * mode is activated
	 */
	public static String INDICATOR = "\u00bb";
	
	Container choicesContainer;

	private int numberOfMatches;

	private boolean isInChoice;

	private int choicesYOffsetAdjustment;

	private boolean isOpen;

	Style choiceItemStyle;

	int choiceOrientation;

	private int predictiveType = TRIE;

	private TextBuilder builder = null;

	private int elementX = 0;

	//private int elementY = 0;

	private boolean refreshChoices = true;

	private boolean predictiveEnabled;
	
	private String[] words;
	
	private ArrayList results;
	
	Alert alert;
	
	String info;
	
	TrieSetup setup;
	
	public PredictiveAccess() {
		//#style predictiveWordNotFound?
		this.alert = new Alert(null);
		//#style predictiveWordNotFoundText?
		this.alert.setString(Locale.get("polish.predictive.wordNotFound"));
		this.alert.setTimeout(2000);
		
		//#if polish.predictive.globalSwitch
		this.registeredFields = new ArrayList();
		//#endif
	}

	/**
	 * Initializes the predictive input for a textfield by creating objects for the choices container
	 * and setting the input mode
	 * 
	 * @param parent the textfield which likes to use the predictive input
	 */
	public void init(TextField parent) {
		this.parent = parent;

		initPredictiveInput(null);
		//#style predictiveChoice?
		this.choicesContainer = new Container(false);
		this.choicesContainer.setParent(parent);

		if(this.builder != null)
		{
			this.parent.setInputMode(this.builder.getMode());
		}
		
		//#if polish.predictive.globalSwitch
			this.registeredFields.add(parent);
		//#endif
	}

	/**
	 * Initializes the predictive input. If <code>words</code> is not null,
	 * the predictive type <code>ARRAY</code> is set and the dictionary
	 * for this type is <code>words</code>. Otherwise, the method tries
	 * to read from the dictionary located in the RMS by calling 
	 * <code>PROVIDER.init()</code>. Based on the outcome of this operation, 
	 * commands are set for the parent textfield like 
	 * <code>INSTALL_PREDICTIVE_CMD</code> if the dictionary is not installed.
	 * If the dictionary is already installed, the predictive input is activated and
	 * the commands <code>DISABLE_PREDICTIVE_CMD</code> and 
	 * <code>ADD_WORD_CMD</code> is added to the textfield.   
	 * 
	 * @param allowedWords the words array
	 */
	public void initPredictiveInput(String[] allowedWords) {
		this.parent.removeCommand(ENABLE_PREDICTIVE_CMD);
		this.parent.removeCommand(INSTALL_PREDICTIVE_CMD);
		this.parent.removeCommand(ADD_WORD_CMD);
		this.parent.removeCommand(DISABLE_PREDICTIVE_CMD);
		
		if(allowedWords != null)
		{
			this.parent.predictiveInput = true;
			
			this.predictiveType = ARRAY;
			
			this.builder = new ArrayTextBuilder(this.parent.getMaxSize());
			
			this.words = allowedWords;
		}
		else
		{
			try {
				this.parent.predictiveInput = true;
				this.predictiveType = TRIE;
				
				if (!PROVIDER.isInit()) {
					PROVIDER.init();
				}
				
				this.parent.addCommand(ADD_WORD_CMD);
				
				//#if polish.TextField.predictive.showCommands || !polish.key.ChangeInputModeKey:defined
				this.parent.addCommand(DISABLE_PREDICTIVE_CMD);
				//#endif
				
				this.builder = new TrieTextBuilder(this.parent.getMaxSize());
	
			} catch (RecordStoreException e) {
				this.parent.addCommand(INSTALL_PREDICTIVE_CMD);
				this.parent.predictiveInput = false;
			} catch (Exception e) {
				//#debug error
				System.out.println("unable to load predictive dictionary " + e);
			}
		}
	}

	/**
	 * Returns the key code for the space key of the model running the application
	 */
	public static int getSpaceKey() {
		if (TextField.charactersKeyPound != null)
			if (TextField.charactersKeyPound.charAt(0) == ' ')
				return Canvas.KEY_POUND;

		if (TextField.charactersKeyStar != null)
			if (TextField.charactersKeyStar.charAt(0) == ' ')
				return Canvas.KEY_STAR;

		if (TextField.charactersKey0 != null)
			if (TextField.charactersKey0.charAt(0) == ' ')
				return Canvas.KEY_NUM0;

		return -1;
	}
	
	public void disablePredictiveInput() {
		
		//#if polish.TextField.predictive.showCommands || !polish.key.ChangeInputModeKey:defined
			this.parent.addCommand(PredictiveAccess.ENABLE_PREDICTIVE_CMD);
		//#endif
			
		this.parent.removeCommand(PredictiveAccess.DISABLE_PREDICTIVE_CMD);
		
		this.parent.removeCommand(PredictiveAccess.ADD_WORD_CMD);

		if(this.predictiveEnabled)
		{
		this.parent.setText(this.builder.getText().toString());
		this.parent.setCaretPosition(this.builder.getCaretPosition());
		}
		
		this.predictiveEnabled = false;
		this.parent.predictiveInput = false;

		this.parent.updateInfo();

		openChoices(false);

		this.parent.updateInfo();
		this.parent.notifyStateChanged();
	}
	
	public void enablePredictiveInput()
	{
		try {
			if (!PROVIDER.isInit()) {
				PROVIDER.init();
			}

			this.predictiveEnabled = true;
		} catch (RecordStoreException e)
		{
			//#debug error
			System.out.println("unable to enable predictive input");
			return;
		}

		if (this.predictiveEnabled) {
			this.predictiveEnabled = true;
			this.parent.predictiveInput = true;
			synchronize();
		}
		
		this.parent.removeCommand(ENABLE_PREDICTIVE_CMD);

		//#if polish.TextField.predictive.showCommands || !polish.key.ChangeInputModeKey:defined
			this.parent.addCommand(DISABLE_PREDICTIVE_CMD);
		//#endif
		
		this.parent.addCommand(ADD_WORD_CMD);
		this.parent.notifyStateChanged();
	}

	/**
	 * Synchronizes the <code>TextBuilder</code> object used for the predictive input
	 * by deleting all current entries of the builder, splitting the current text at 
	 * spaces, inserting the resulting chunks as objects of <code>TextElement</code>
	 * to the builder and finally setting the caret position 
	 */
	public void synchronize() {
		openChoices(false);

		if(this.builder != null)
		{
			while (this.builder.deleteCurrent());
	
			String text = this.parent.getText();
			
			if (text != null) {
				String[] elements = TextUtil.split(this.parent.getText(), ' ');
	
				for (int i = 0; i < elements.length; i++) {
					if (elements[i].length() > 0) {
						this.builder.addString(elements[i]);
						this.builder.addString(" ");
					}
				}
	
				if (this.parent.inputMode == TextField.MODE_NUMBERS) {
					this.parent.setInputMode(TextField.MODE_LOWERCASE);
					this.builder.setMode(TextField.MODE_LOWERCASE);
				}
	
				this.parent.updateInfo();
	
				this.builder.setCurrentElementNear(this.parent.getCaretPosition());
			}
		}
	}

	/**
	 * Sets the choices of the textfield based on the current 
	 * active <code>TextElement</code>.  
	 */
	private void setChoices(TextElement element) {
		this.choicesContainer.clear();
		if (element != null && element.getResults() != null
				&& element.getCustomResults() != null) {
			ArrayList trieResults = element.getResults();
			ArrayList customResults = element.getCustomResults();

			if (trieResults.size() == 0 && customResults.size() == 0) {
				openChoices(false);
				return;
			}

			this.numberOfMatches = trieResults.size() + customResults.size();

			for (int i = 0; i < trieResults.size(); i++) {
				String choiceText = (String)trieResults.get(i);
				Item item = new ChoiceItem(choiceText, null, Choice.IMPLICIT,
						this.choiceItemStyle);

				if (this.choiceOrientation == ORIENTATION_BOTTOM) {
					this.choicesContainer.add(item);
				} else {
					this.choicesContainer.add(0, item);
				}
			}

			for (int i = 0; i < customResults.size(); i++) {
				String choiceText = (String)customResults.get(i);
				Item item = new ChoiceItem(choiceText, null, Choice.IMPLICIT,
						this.choiceItemStyle);

				if (this.choiceOrientation == ORIENTATION_BOTTOM) {
					this.choicesContainer.add(item);
				} else {
					this.choicesContainer.add(0, item);
				}
			}
			if (!this.isOpen )
			{
				openChoices(this.numberOfMatches > 0);
			}
			
			if(this.numberOfMatches > 0)
			{
				if (this.choiceOrientation == ORIENTATION_BOTTOM) {
					this.choicesContainer.focusChild(0);
				}
				else
				{
					this.choicesContainer.focusChild(this.choicesContainer.size() - 1);
				}
			}
			
			this.results = element.getResults();			

		} else
			openChoices(false);
	}

	/**
	 * Opens or closes the choices list.
	 * 
	 * @param open true, if the list should be opened, to close false
	 */
	void openChoices(boolean open) {
		//#debug
		System.out.println("open choices: " + open
				+ ", have been opened already:" + this.isOpen);
		if (open) {
//			if (this.parent.getParent() instanceof Container) {
//				Container parentContainer = (Container) this.parent.getParent();
//				if (parentContainer.enableScrolling) {
//					int availableWidth = this.parent.itemWidth
//							- (this.parent.marginLeft + this.parent.marginRight);
//					int choicesHeight = this.choicesContainer.getItemHeight(
//							availableWidth, availableWidth);
//					int choicesBottomY = this.parent.contentY
//							+ this.parent.contentHeight
//							+ this.parent.paddingVertical + choicesHeight;
//					//#debug
//					System.out.println("choicesHeight " + choicesHeight
//							+ ", choicesBottom=" + choicesBottomY
//							+ ", parent.height="
//							+ parentContainer.availableHeight);
////					int parentYOffset = parentContainer.getScrollYOffset();
////					int overlap = choicesBottomY
////							- (parentContainer.getContentScrollHeight() - (this.parent.relativeY + parentYOffset));
//					/*if (overlap > 0) {
//						// try to scroll up this item, so that the user sees all
//						// matches:
//						int yOffsetAdjustment = Math.min(this.parent.relativeY
//								+ parentYOffset, overlap);
//						this.choicesYOffsetAdjustment = yOffsetAdjustment;
//						//#debug
//						System.out.println("Adjusting yOffset of parent by "
//								+ yOffsetAdjustment);
//						parentContainer.setScrollYOffset(parentYOffset
//								- yOffsetAdjustment, true);
//						// System.out.println("choice.itemHeight=" +
//						// this.choicesContainer.itemHeight + ",
//						// parentContainer.availableHeight=" +
//						// parentContainer.availableHeight + ", (this.contentY +
//						// this.contentHeight + this.paddingVertical)=" +
//						// (this.contentY + this.contentHeight +
//						// this.paddingVertical) + ", children.relativeY=" +
//						// this.choicesContainer.relativeY );
//						// TODO this needs some finetuning!
//						int itHeight = this.parent.itemHeight;
//						int ctHeight = this.parent.contentY
//								+ this.parent.contentHeight
//								+ this.parent.paddingVertical;
//						int max = Math.max(itHeight, ctHeight);
//
//						this.choicesContainer.setScrollHeight(parentContainer
//								.getContentScrollHeight()
//								- max);
//					} else {
//						this.choicesYOffsetAdjustment = 0;
//					}*/
//				}
//			}
		} else {
			this.choicesContainer.clear();
			this.isInChoice = false;
		}
		
		this.isOpen = open;
		this.refreshChoices = open;
	}
	
	private void replaceCurrent()
	{
		this.builder.deleteCurrent();
	}

	/**
	 * Sets the focus to the first item of the choices or resets the focus to the
	 * textfield.
	 * 
	 * @param enter true, if the list should be entered, to leave the choices false
	 */
	private void enterChoices(boolean enter) {
		//#debug
		System.out.println("enter choices: " + enter
				+ ", have been entered already: " + this.isInChoice);
		
		if (enter) {
			int size = this.choicesContainer.size();
			if (this.choiceOrientation == ORIENTATION_BOTTOM) {
				if(size > 1)
				{
					this.choicesContainer.focusChild(1);
				}
			} else {
				if(size > 1)
				{
					this.choicesContainer.focusChild(this.choicesContainer.size() - 2);
				}
				
				int scrollOffset = this.choicesContainer.getScrollYOffset();
				// since the choices container was not focused before, it will set it's scroll offset to 0.
				// We want to see the last item, so we have to reset the scroll offset:
				this.choicesContainer.setScrollYOffset(scrollOffset, false);
			}
			//#if polish.usePolishGui && !polish.LibraryBuild
				this.parent.showCaret = false;
				if (!this.isInChoice) {
					this.parent.getScreen().removeItemCommands( this.parent );
				}
			//#endif
		} else {

			this.parent.showCaret = true;
			this.choicesContainer.yOffset = 0;
			this.choicesContainer.targetYOffset = 0;
			// move focus to TextField input again
			if (this.isInChoice) {
				//#if polish.usePolishGui && !polish.LibraryBuild
					this.parent.showCommands();
				//#endif
			}
		}
		this.isInChoice = enter;
	}

	/**
	 * Retrieves the y offset for the choices list based on the currently
	 * active <code>TextElement</code>.
	 * 
	 * @param paddingVertical the vertical padding between the textfield lines
	 * @param borderWidth the width of the border of the textfield
	 * @return the y offset
	 */
	protected int getChoicesY(int paddingVertical, int borderWidth) {
		if (this.choiceOrientation == ORIENTATION_BOTTOM) {
			int resultY = (this.parent.contentHeight / this.parent.textLines.size())
					* (this.builder.getElementLine(this.parent.textLines) + 1);

			return (resultY + paddingVertical + borderWidth);
		} else {
			int resultY = (this.parent.contentHeight / this.parent.textLines.size())
					* (this.builder.getElementLine(this.parent.textLines));

			return (resultY - this.choicesContainer.itemHeight
					- paddingVertical - borderWidth);
		}
	}

	/**
	 * Returns the x offset for the choices list based on the currently
	 * active <code>TextElement</code>.
	 * 
	 * @param leftBorder the width of the left border
	 * @param rightBorder the width of the right border
	 * @param itemWidth the width of the textfield
	 * @return the x offset
	 */
	protected int getChoicesX(int leftBorder, int rightBorder, int itemWidth) {
		if (this.builder.getAlign() == TrieTextBuilder.ALIGN_FOCUS) {
			int line = this.builder.getElementLine(this.parent.textLines);
			int charsToLine = 0;

			for (int i = 0; i < line; i++)
				charsToLine += this.parent.textLines.getLine(i).length() + 1;

			TextElement element = this.builder.getTextElement();

			if (element != null) {
				int result = 0;

				int elementStart = this.builder.getCaret()
						- element.getLength();

				StringBuffer stringToLine = new StringBuffer();
				for (int i = charsToLine; i < elementStart; i++) {
					stringToLine.append(this.builder.getTextChar(i));
				}

				result += this.parent.stringWidth(stringToLine.toString());

				int overlap = (rightBorder) - (leftBorder + result + itemWidth);

				if (overlap < 0)
					result += overlap;

				return result;
			}
		}

		return 0;
	}

	protected void showWordNotFound() {
		if(this.alert != null)
		{
			StyleSheet.display.setCurrent(this.alert);
		}
	}

	protected boolean keyInsert(int keyCode, int gameAction) {
		if (this.builder == null) {
			return false;
		}
		if ((keyCode >= Canvas.KEY_NUM0 && keyCode <= Canvas.KEY_NUM9)
				|| keyCode == Canvas.KEY_POUND || keyCode == Canvas.KEY_STAR) {
			if (this.isInChoice) {
				enterChoices(false);
				openChoices(false);
			}

			try {
				if (keyCode != SPACE_BUTTON) {
					if (this.predictiveType == TRIE) {
						this.builder.keyNum(keyCode, new TrieReader());
					} else {
						ArrayReader reader = new ArrayReader();
						reader.setWords(this.words);
						this.builder.keyNum(keyCode, reader);
					}

					this.parent.setInputMode(this.builder.getMode());
					this.parent.updateInfo();

					if (!this.builder.getTextElement().isWordFound())
					{
						if(this.predictiveType != ARRAY)
						{
							showWordNotFound();
						}
					}
					else {
						if (this.builder.getAlign() == TrieTextBuilder.ALIGN_FOCUS)
						{
							this.setChoices(this.builder.getTextElement());
						}
						else
							this.openChoices(false);
					}
				} else {
					this.builder.keySpace();
					openChoices(false);
				}

			} catch (Exception e) {
				//#debug error
				e.printStackTrace();
			}

			this.parent.setText(this.builder.getText().toString());
			this.parent.setCaretPosition(this.builder.getCaretPosition());
			//this.parent.showCommands(); //getScreen().setItemCommands(this.parent);
			this.parent.notifyStateChanged();
			this.refreshChoices = true;
			
			this.parent.updateDeleteCommand(this.builder.getText().toString());

			return true;
		}

		return false;
	}

	protected boolean keyClear(int keyCode, int gameAction) {
		if (this.builder == null) {
			return false;
		}
		if (this.isInChoice) {
			enterChoices(false);
			openChoices(false);
		}

		if(this.builder.hasText())
		{
			try {
				
				this.builder.keyClear();
	
				if (!this.builder.isString(0)
						&& this.builder.getAlign() == TrieTextBuilder.ALIGN_FOCUS) {
					this.setChoices(this.builder.getTextElement());
				} else {
					this.openChoices(false);
				}
			} catch (RecordStoreException e) {
				//#debug error
				System.out.println("unable to load record store " + e);
			}
	
			this.parent.setText(this.builder.getText().toString());
			this.parent.setCaretPosition(this.builder.getCaretPosition());
			this.parent.showCommands();//this.parent.getScreen().setItemCommands(this.parent);
			this.refreshChoices = true;
			this.parent.notifyStateChanged();
			
			this.parent.updateDeleteCommand(this.builder.getText().toString());
			
			return true;
		}
		else
		{
			return false;
		}
	}

	protected boolean keyMode(int keyCode, int gameAction) {
		if (this.builder == null) {
			return false;
		}
		if (keyCode == TextField.KEY_CHANGE_MODE
		//#if polish.key.shift:defined
		//#= || keyCode == ${polish.key.shift}
		//#endif
		) {
			if (this.isInChoice) {
				enterChoices(false);
				openChoices(false);
			}

			this.parent.setInputMode((this.parent.getInputMode() + 1) % 3);
			this.builder.setMode(this.parent.getInputMode());

			this.parent.updateInfo();

			this.refreshChoices = true;
			this.parent.showCommands();//this.parent.getScreen().setItemCommands(this.parent);
			return true;
		}

		return false;
	}

	protected boolean keyNavigation(int keyCode, int gameAction) {
		
			if (this.isInChoice) {
				if ( this.choicesContainer.handleKeyPressed(keyCode, gameAction) ) {
					//#debug
					System.out.println("keyPressed handled by choices container");
					
					this.refreshChoices = true;
					return true;
				}
				// System.out.println("focusing textfield again, isFocused=" + this.isFocused);
				// HERE WAS THE PLACE FORMERLY KNOWN AS FIRE HANDLING
				enterChoices( false );
				
				this.parent.notifyStateChanged();
				return true;
			}
			if ( (gameAction == Canvas.DOWN && keyCode != Canvas.KEY_NUM8)
					&& this.builder.getAlign() == TrieTextBuilder.ALIGN_FOCUS 
					&& this.choiceOrientation == ORIENTATION_BOTTOM)
			{
				if(!this.builder.isString(0))
				{
					this.setChoices(this.builder.getTextElement());
					
					if(this.numberOfMatches > 0)
						enterChoices( true );	
				}
				
				this.refreshChoices = true;
				this.parent.notifyStateChanged();
				return true;
			}
			else if ( (gameAction == Canvas.UP && keyCode != Canvas.KEY_NUM2)
					&& this.builder.getAlign() == TrieTextBuilder.ALIGN_FOCUS 
					&& this.choiceOrientation == ORIENTATION_TOP)
			{
				if (this.isOpen) {
					enterChoices(true);
				} else {
					if(!this.builder.isString(0))
					{
						this.setChoices(this.builder.getTextElement());
						
						if(this.numberOfMatches > 0) {
							enterChoices( true );	
						}
					}
				}
				
				this.refreshChoices = true;
				this.parent.notifyStateChanged();
				return true;
			}
			else if ( gameAction == Canvas.LEFT || gameAction == Canvas.RIGHT )
			{
				if(gameAction == Canvas.LEFT)
					this.builder.decreaseCaret();
				else if(gameAction == Canvas.RIGHT)
					this.builder.increaseCaret();
				
				if(this.builder.getAlign() == TrieTextBuilder.ALIGN_FOCUS)
				{
					this.setChoices(this.builder.getTextElement());
				}
				else
					openChoices(false);
				
				this.parent.setCaretPosition(this.builder.getCaretPosition());
			
				this.parent.notifyStateChanged();
				return true;
			}
			else if ( gameAction == Canvas.UP && !this.isInChoice)
			{
				int lineCaret = this.builder.getJumpPosition(TrieTextBuilder.JUMP_PREV, this.parent.textLines);
				
				if(lineCaret != -1)
				{
					this.builder.setCurrentElementNear(lineCaret);
					
					this.parent.setCaretPosition(this.builder.getCaretPosition());
									
					openChoices(false);
	
					this.parent.notifyStateChanged();
					return true;
				}
				
				this.parent.notifyStateChanged();
				return false;
			}
			else if ( gameAction == Canvas.DOWN && !this.isInChoice)
			{
				int lineCaret = this.builder.getJumpPosition(TrieTextBuilder.JUMP_NEXT, this.parent.textLines);
				
				if(lineCaret != -1)
				{
					this.builder.setCurrentElementNear(lineCaret);
					
					this.parent.setCaretPosition(this.builder.getCaretPosition());
									
					openChoices(false);
					
					this.parent.notifyStateChanged();
					return true;
				}
				
				this.parent.notifyStateChanged();
				return false;
			}
			else if (gameAction == Canvas.FIRE && keyCode != Canvas.KEY_NUM5) {
				
				openChoices( false );
				if(!this.builder.isString(0))
				{
					this.builder.setAlign(TrieTextBuilder.ALIGN_RIGHT);
					if(this.builder.getTextElement().isSelectedCustom())
						this.builder.getTextElement().convertReader();
				}
				
				this.parent.notifyStateChanged();
				return false;
			}

			this.parent.notifyStateChanged();
		
		return false;
	}

	/**
	 * Paints the choices
	 * @param x horizontal content start position of the textfield
	 * @param y vertical content start position of the text
	 * @param caretX the relative horizontal offset of the caret
	 * @param caretY the relative vertical offset of the caret
	 * @param leftBorder left border
	 * @param rightBorder right border
	 * @param g graphics context
	 */
	public void paintChoices(int x, int y, int caretX, int caretY, int leftBorder, int rightBorder,
			Graphics g) 
	{
		
		if (this.numberOfMatches > 0 && this.isOpen) {
//			System.out.println("painting choices");
			if (this.refreshChoices) {
				this.elementX = getChoicesX(leftBorder, rightBorder,
						this.choicesContainer.getItemWidth(
								this.parent.itemWidth, this.parent.itemWidth, this.parent.itemHeight));
//				this.elementY = getChoicesY(this.parent.paddingVertical,
//						this.parent.borderWidth);
				this.refreshChoices = false;
			}
			// position choices container:
			int clipX = 0;
			int clipY = 0;
			int clipWidth = 0;
			int clipHeight = 0;
			int availWidth = rightBorder-leftBorder;
			int availHeight = this.parent.availableHeight;
			if (this.choiceOrientation == ORIENTATION_TOP) {
				int space;
				/*if(this.parent.getParent() != null)
				{
					space = y + caretY - this.parent.getParent().getAbsoluteY();
				}
				else
				{*/
					space = y + caretY - this.parent.getScreen().contentY;
				//}
				this.choicesContainer.setScrollHeight(space);
				if (this.choicesContainer.getItemHeight(availWidth, availWidth, availHeight) > space) {
//					this.elementY = - space;
					this.choicesContainer.relativeY = caretY - space;
					if (!this.isInChoice) {
						this.choicesContainer.setScrollYOffset( space - this.choicesContainer.itemHeight, false);
					}
					clipX = g.getClipX();
					clipY = g.getClipY();
					clipWidth = g.getClipWidth();
					clipHeight = g.getClipHeight();
					g.clipRect( clipX, y + caretY - space, clipWidth, space );
				} else {
					this.choicesContainer.relativeY = caretY - this.choicesContainer.itemHeight;
				}
			} else {
				int space;
				int verticalStart = caretY + this.parent.getFontHeight() + this.parent.paddingVertical;
				if (this.parent.getParent()!=null) {
					Container parentContainer = (Container) this.parent.getParent();
					space = parentContainer.getScrollHeight() - (y + verticalStart - this.parent.getParent().getAbsoluteY());
				} 
				else {
					space = this.parent.getScreen().contentY;
				}
				this.choicesContainer.setScrollHeight(space);
				if (this.choicesContainer.getItemHeight(availWidth, availWidth, availHeight) > space) {
					clipX = g.getClipX();
					clipY = g.getClipY();
					clipWidth = g.getClipWidth();
					clipHeight = g.getClipHeight();
					g.clipRect( clipX, y + verticalStart, clipWidth, space );					
				}
				this.choicesContainer.relativeY = verticalStart;
			}
			this.choicesContainer.relativeX = this.elementX;
			this.choicesContainer.paint(x + this.elementX, y + this.choicesContainer.relativeY,
					leftBorder + this.elementX, rightBorder, g);
			if (clipHeight != 0) {
				g.setClip(clipX, clipY, clipWidth, clipHeight);
			}
		}
	}

	public void animateChoices(long currentTime, ClippingRegion region ) {
		if (this.isOpen && this.numberOfMatches > 0) {
			this.choicesContainer.animate( currentTime, region);
		}
	}

	public boolean commandAction(Command cmd, Displayable box) {
		//#if tmp.supportsSymbolEntry
		if (box instanceof List) {
			if (cmd != StyleSheet.CANCEL_CMD) {
				int index = TextField.symbolsList.getSelectedIndex();

				this.builder.addString(TextField.definedSymbols[index]);

				this.parent.setText(this.builder.getText().toString());
				this.parent.setCaretPosition(this.builder.getCaretPosition());

				// insertCharacter( definedSymbols.charAt(index), true, true );
				StyleSheet.currentScreen = this.parent.screen;
				//#if tmp.updateDeleteCommand
				this.parent.updateDeleteCommand(this.parent.text);
				//#endif
			} else {
				StyleSheet.currentScreen = this.parent.screen;
			}
			StyleSheet.display.setCurrent(this.parent.screen);
			this.parent.notifyStateChanged();
			return true;
		} else
		//#endif
		if (box instanceof Form) {
			if (cmd == StyleSheet.OK_CMD) {
				this.builder.addWord(PROVIDER.getCustomField().getText());
			}
			StyleSheet.display.setCurrent(this.parent.getScreen());
			return true;
		} 

		return false;
	}
	
	public void addWord(String word)
	{
		this.builder.addWord(word);
	}

	public boolean commandAction(Command cmd, Item item) {
		if (cmd == INSTALL_PREDICTIVE_CMD) {
			this.setup = new TrieSetup(this);
			//#if polish.predictive.noInfo
				this.setup.install();
			//#else
				this.setup.showInfo();
			//#endif
			return true;
		}
		
		if (this.builder == null) {
			return false;
		}
		if (this.parent.predictiveInput && cmd == TextField.CLEAR_CMD) {
			while (this.builder.deleteCurrent());
			openChoices(false);

			this.parent.setString(null);
			this.parent.notifyStateChanged();
			return true;
		} else if (cmd == ENABLE_PREDICTIVE_CMD) {
			enablePredictiveInput();
			return true;
		} else if (cmd == DISABLE_PREDICTIVE_CMD) {
			disablePredictiveInput();
			return true;
		} else if (cmd == ADD_WORD_CMD) {
			//#if !polish.predictive.ignoreAddWordCommand
			if (PROVIDER.getCustomField() == null) {
				TextField field = new TextField(Locale.get("polish.predictive.registerNewWord.label"), "",50, TextField.ANY);
				field.getPredictiveAccess().disablePredictiveInput();
				PROVIDER.setCustomField(field);
				PROVIDER.getCustomForm().append(PROVIDER.getCustomField());
			} else {
				PROVIDER.getCustomField().setText("");
			}
			PROVIDER.getCustomForm().setCommandListener(this.parent);
			StyleSheet.display.setCurrent(PROVIDER.getCustomForm());
			//#endif
			return true;
		}

		return false;
	}

	public TextBuilder getBuilder() {
		return this.builder;
	}

	public void setBuilder(TextBuilder builder) {
		this.builder = builder;
	}

	public int getPredictiveType() {
		return this.predictiveType;
	}

	public void setPredictiveType(int predictiveType) {
		this.predictiveType = predictiveType;
	}

	public Container getChoicesContainer() {
		return this.choicesContainer;
	}

	public void setChoicesContainer(Container choicesContainer) {
		this.choicesContainer = choicesContainer;
	}
	
	public ArrayList getResults()
	{
		return this.results;
	}

	/**
	 * Handles the key-released event.
	 * Please note, that implementation should first try to handle the
	 * given key-code, before the game-action is processed.
	 * 
	 * @param keyCode the code of the pressed key, e.g. Canvas.KEY_NUM2
	 * @param gameAction the corresponding game-action, e.g. Canvas.UP
	 * @return true when the key has been handled / recognized
	 */
	public boolean handleKeyReleased(int keyCode, int gameAction) {
		if (this.builder == null) {
			return false;
		}
		if ((gameAction == Canvas.FIRE || gameAction == Canvas.RIGHT) &&
			!(keyCode >= Canvas.KEY_NUM0 && keyCode <= Canvas.KEY_NUM9) &&
			this.isOpen) {
			// option has been selected!
			if(!this.builder.isString(0))
			{
				if(this.choiceOrientation == ORIENTATION_BOTTOM)
				{
					this.builder.getTextElement().setSelectedWordIndex(this.choicesContainer.getFocusedIndex());
				}
				else
				{
					int index = (this.choicesContainer.size() - 1) - this.choicesContainer.getFocusedIndex();
					this.builder.getTextElement().setSelectedWordIndex(index);
				}
				
				this.builder.getTextElement().convertReader();
				this.builder.setAlign(TrieTextBuilder.ALIGN_RIGHT);
				
				openChoices( false );
				this.parent.notifyStateChanged();
				
				this.parent.setText(this.builder.getText().toString()); 
				this.parent.setCaretPosition(this.builder.getCaretPosition());
				this.refreshChoices = true;
			}
			
			return true;
		}
			
		return false;
	}
	
	public void setAlert(Alert alert)
	{
		this.alert = alert;
	}
	
	public void setInfo(String info)
	{
		this.info = info;
	}
	
	public String getInfo()
	{
		return this.info;
	}

	public void setupFinished(boolean finishedGraceful) {
		if(finishedGraceful)
		{
			initPredictiveInput(null);
			enablePredictiveInput();
		}
		
		StyleSheet.display.setCurrent(this.parent.getScreen());
	}

	public TextField getParent() {
		return this.parent;
	}

	public void setParent(TextField parent) {
		this.parent = parent;
	}

	public boolean isOpen() {
		return this.isOpen;
	}

	public boolean isPredictiveEnabled() {
		return this.predictiveEnabled;
	}
}
