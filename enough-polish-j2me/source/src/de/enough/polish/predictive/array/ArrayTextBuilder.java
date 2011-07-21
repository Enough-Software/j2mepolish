//#condition polish.usePolishGui && polish.TextField.usePredictiveInput && polish.TextField.useDirectInput && !(polish.blackberry || polish.android)

package de.enough.polish.predictive.array;

import javax.microedition.rms.RecordStoreException;

import de.enough.polish.predictive.PredictiveReader;
import de.enough.polish.predictive.TextBuilder;

public class ArrayTextBuilder extends TextBuilder {

	public ArrayTextBuilder(int textSize) {
		super(textSize);
	}
	
	public boolean keyClear() throws RecordStoreException {
		if (this.align == ALIGN_LEFT) 
		{
			if (this.element != 0)
			{
				decreaseCaret();
			}
			else
			{
				return false;
			}
		}
		if (isString(0)) {
			if (!decreaseString()) 
			{
				deleteCurrent();
				return false;
			} 
			else
			{
				return true;
			}
		} else {
			if (getTextElement().getKeyCount() <= getReader().getKeyCount())
			{
				getReader().keyClear();
			}
			
			getTextElement().keyClear();
			getTextElement().setResults();

			if (getReader().isEmpty()) {
				deleteCurrent();
				return false;
			} else {
				setAlign(ALIGN_FOCUS);
				getTextElement().setSelectedWordIndex(0);
				return (getTextElement().getResults().size() > 0)
						|| (getTextElement().getCustomResults().size() > 0);
			}
		}
	}

	//TODO: andre: wtf?
	//TODO: robert: ftw!
	public void addWord(String string) {}

	public void addString(String string) {
		addElement(new ArrayTextElement(string));
		this.align = ALIGN_RIGHT;
	}

	public void addReader(PredictiveReader reader) {
		addElement(new ArrayTextElement(reader));
		this.align = ALIGN_FOCUS;
	}

	public boolean deleteCurrent() {
		if (this.textElements.size() > 0) {
			int index = this.element;

			if (this.element == 0)
				this.align = ALIGN_LEFT;
			else {
				this.element--;
				this.align = ALIGN_RIGHT;
			}

			this.textElements.remove(index);

			return true;
		}

		return false;
	}
}
