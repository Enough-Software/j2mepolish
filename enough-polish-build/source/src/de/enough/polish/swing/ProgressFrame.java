/*
 * Created on 11-May-2005 at 13:22:32.
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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 * <p>Shows a progess that is started in a background thread.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        11-May-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ProgressFrame 
extends JFrame
implements Runnable
{
	private static final long serialVersionUID = -6353535564417065394L;

	private final BackgroundProcess process;
	private final JLabel statusText;
	private final JProgressBar progressBar;
	private int step;

	/**
	 * Creates a new progress frame
	 * 
	 * @param splash the splash image, can be null
	 * @param title the title of the frame
	 * @param process the process that is to be started in a background thread
	 * @throws java.awt.HeadlessException when the Swing initialization fails
	 */
	public ProgressFrame(Image splash, String title, BackgroundProcess process ) 
	throws HeadlessException 
	{
		super(title);
		setUndecorated( true );
		this.process = process;
		int steps = process.getSteps();
		if (steps == -1) {
			this.progressBar = new JProgressBar();
			this.progressBar.setIndeterminate( true );
		} else {
			this.progressBar = new JProgressBar( 0, steps);
		}
		this.statusText = new JLabel("Just a long dummy message");
		Container contentPane = getContentPane();
		contentPane.setLayout( new BorderLayout() );
		if (splash != null) {
			JLabel splashLabel = new JLabel();
			splashLabel.setIcon( new ImageIcon( splash  ) );
			contentPane.add( splashLabel, BorderLayout.NORTH );
		}
		contentPane.add( this.progressBar, BorderLayout.CENTER );
		contentPane.add( this.statusText, BorderLayout.SOUTH );
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation( ( screenSize.width / 2 ) - (getWidth()/2), (screenSize.height / 2) - (getHeight()/2) );
		this.statusText.setText(null);
		Thread thread = new Thread( this );
		thread.start();
	}
	
	public void setStep( int step ) {
		this.step = step;
		this.progressBar.setValue( step );
	}
	
	public void nextStep() {
		this.step++;
		this.progressBar.setValue( this.step );
	}
	
	
	public void setStatusText( String text ) {
		this.statusText.setText( text );
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// starting the process in this background thread:
		this.process.runBackgroundProcess(this);
		setVisible(false);
		dispose();
	}
}
