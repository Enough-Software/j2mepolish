/*
 * Created on 11-May-2005 at 10:05:54.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
 *
 */
package de.enough.polish.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JPanel;

/**
 * <p>Contains several options for fast selection.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        11-May-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ToolBar extends JPanel {

	private static final long serialVersionUID = -3469652953195193159L;

	private JPanel leftPanel;
	private JPanel rightPanel;

	/**
	 * Creates a new toolbar 
	 */
	public ToolBar() {
		super( new BorderLayout() );
		this.leftPanel = new JPanel( new FlowLayout() );
		this.rightPanel = new JPanel( new FlowLayout() );
		add( this.leftPanel, BorderLayout.WEST );
		add( this.rightPanel, BorderLayout.EAST );
	}

	/**
	 * Adds the component to the left side of this toolbar.
	 * Following components are arranged more towards the middle.
	 * 
	 * @param component the component to be added
	 */
	public void addToLeftPanel( Component component ) {
		this.leftPanel.add(component);
	}
	
	/**
	 * Adds the component to the right side of this toolbar.
	 * Following components are arranged more towards the right.
	 * 
	 * @param component the component to be added
	 */
	public void addToRightPanel( Component component ) {
		this.rightPanel.add(component);
	}
	
	/**
	 * Removes the component from the toolbar
	 * 
	 * @param component the component
	 */
	public void remove( Component component ) {
		super.remove(component);
		this.leftPanel.remove( component );
		this.rightPanel.remove( component );
	}
}
