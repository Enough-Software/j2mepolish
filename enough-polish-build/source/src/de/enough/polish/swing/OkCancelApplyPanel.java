/*
 * Created on 06-Mar-2005 at 16:59:08.
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
package de.enough.polish.swing;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import de.enough.polish.util.Translations;

/**
 * <p>Provides the three buttons "ok", "cancel" and "apply" within an panel.</p>
 * <p>
 * All buttons are deactivated at first and will be activated after being notified
 * with changed().
 * </p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        06-Mar-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class OkCancelApplyPanel
extends JPanel
implements ActionListener, Translatable
{
	private static final long serialVersionUID = -3091106564017323215L;
	private final OkCancelApplyListener listener;
	private final JButton okButton;
	private final JButton cancelButton;
	private final JButton applyButton;

	/**
	 * Creates a new button panel.
	 * 
	 * @param listener the listener that will be notified when a button has been selected
	 * @param translations the translations
	 * 
	 */
	public OkCancelApplyPanel( OkCancelApplyListener listener, Translations translations ) 
	{
		super( new GridLayout( 1, 3, 5, 5) );
		this.listener = listener;
		this.okButton = new JButton( translations.getString("button.ok") );
		this.okButton.setMnemonic(translations.getString("button.ok.mnemonic").charAt(0) );
		this.okButton.addActionListener( this );
		this.cancelButton = new JButton(translations.getString("button.cancel"));
		this.cancelButton.setMnemonic( translations.getString("button.cancel.mnemonic").charAt(0) );
		this.cancelButton.addActionListener( this );
		this.applyButton = new JButton(translations.getString("button.apply"));
		this.applyButton.setMnemonic(translations.getString("button.apply.mnemonic").charAt(0));
		this.applyButton.addActionListener( this );
		
		// deactivate buttons first:
		this.okButton.setEnabled(false);
		this.applyButton.setEnabled(false);
		this.cancelButton.setEnabled(false);
		
		add( this.okButton );
		add( this.cancelButton );
		add( this.applyButton );
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == this.okButton) {
			this.okButton.setEnabled(false);
			this.applyButton.setEnabled(false);
			this.cancelButton.setEnabled(false);
			this.listener.okSelected();
		} else if (source == this.applyButton) {
			this.okButton.setEnabled(true);
			this.applyButton.setEnabled(false);
			this.cancelButton.setEnabled(true);
			this.listener.applySelected();
		} else if (source == this.cancelButton) {
			this.okButton.setEnabled(false);
			this.applyButton.setEnabled(false);
			this.cancelButton.setEnabled(false);
			this.listener.cancelSelected();
		}
	}
	
	public void changed() {
		//System.out.println("BUTTONS: Changed!");
		this.okButton.setEnabled(true);
		this.applyButton.setEnabled(true);
		this.cancelButton.setEnabled(true);
	}
	
	public void deactivate() {
		this.okButton.setEnabled(false);
		this.applyButton.setEnabled(false);
		this.cancelButton.setEnabled(false);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.swing.ResourceConsumer#setResourceBundle(java.util.ResourceBundle)
	 */
	public void setTranslations(Translations translations) {
		this.okButton.setText( translations.getString("button.ok") );
		this.okButton.setMnemonic( translations.getString("button.ok.mnemonic").charAt(0) );
		this.cancelButton.setText( translations.getString("button.cancel") );
		this.cancelButton.setMnemonic( translations.getString("button.cancel.mnemonic").charAt(0));
		this.cancelButton.addActionListener( this );
		this.applyButton.setText( translations.getString("button.apply") );
		this.applyButton.setMnemonic(translations.getString("button.apply.mnemonic").charAt(0));

	}

	public void deactivateApply() {
		this.applyButton.setEnabled(false);
	}
	
	public void deactivateOk() {
		this.okButton.setEnabled(false);
	}
	
	public void deactivateCancel() {
		this.cancelButton.setEnabled(false);
	}

	public void activateCancel() {
		this.cancelButton.setEnabled(true);
	}
	
	public void activateApply() {
		this.applyButton.setEnabled(true);
	}
	
	public void activateOk() {
		this.okButton.setEnabled(true);
	}

	public JButton getOkButton() {
		return this.okButton;
	}
	
	public JButton getCancelButton() {
		return this.cancelButton;
	}
	
	public JButton getApplyButton() {
		return this.applyButton;
	}


}
