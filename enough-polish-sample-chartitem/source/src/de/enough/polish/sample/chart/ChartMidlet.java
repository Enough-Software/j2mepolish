package de.enough.polish.sample.chart;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import de.enough.polish.ui.ChartItem;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.UiAccess;

public class ChartMidlet 
extends MIDlet
implements CommandListener
{
	
	private Form form;
	private Command exitCommand = new Command( "Exit", Command.EXIT, 3 );
	private Command styleLines = new Command("Lines", Command.SCREEN, 1 );
	private Command styleVerticalBar = new Command("Vertical Bars", Command.SCREEN, 1 );
	private Command stylePieChart = new Command("Pie Chart", Command.SCREEN, 1 );

	public ChartMidlet() {
		//#style mainScreen
		this.form = new Form( "J2ME Polish Chart");
		this.form.addCommand( this.exitCommand );
		Command parent = new Command( "View-Type", Command.SCREEN, 2);
		this.form.addCommand( parent );
		UiAccess.addSubCommand(this.styleLines, parent, this.form );
		UiAccess.addSubCommand(this.styleVerticalBar, parent, this.form );
		UiAccess.addSubCommand(this.stylePieChart, parent, this.form );
		this.form.setCommandListener( this );


	}

     protected void startApp() throws MIDletStateChangeException{
          Display display = Display.getDisplay( this );
          updateChart();
          display.setCurrent( this.form );
     }

     protected void pauseApp(){
          // ignore
     }

     protected void destroyApp(boolean unconditional) throws MIDletStateChangeException{
          // ignore
     }

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
	 */
	public void commandAction(Command cmd, Displayable disp) {
		if (cmd == this.exitCommand) {
			notifyDestroyed();
		} else if (cmd == this.styleLines) {
			//#style lineChart
			updateChart();			
		} else if (cmd == this.styleVerticalBar) {
			//#style verticalBarChart
			updateChart();
		} else if (cmd == this.stylePieChart) {
			//#style pieChart
			updateChart();		
		}
	}
	
	private void updateChart() {
		//#style lineChart
		updateChart();
	}
	
	private void updateChart( Style style ) {
		this.form.deleteAll();
		int[][] dataSequences = new int[][] {
				new int[]{ 12, 0, 5, 20, 25, 40 },
				new int[]{ 0, 2, 4, 8, 16, 32 },
				new int[]{ 1, 42, 7, 12, 16, 1 }
		};
		int[] colors = new int[]{ 0xFF0000, 0x00FF00, 0x0000FF, 0x660033, 0x009999, 0xFFCC33, 0x00CC99, 0xFFFF33, 0x996600 };
		ChartItem chart = new ChartItem( "Profits:", dataSequences, colors, style );
		this.form.append( chart );
		
		dataSequences = new int[][] {
				new int[]{ -12, -10, -5, -20, -25, -40, -10, -40, -10 },
				new int[]{ -30, -2, -4, -8, -16, -32, -16, -8, -14 },
				new int[]{ -19, -42, -7, -12, -16, -9, -22, -10, -35 }
		};
		chart = new ChartItem( "Losses:", dataSequences, colors, style );
		chart.setLabelX( "years");
		chart.setLabelY("losses");
		this.form.append( chart );

		dataSequences = new int[][] {
				new int[]{ -12, 0, 5, 20, -25, -40, -10, -40, -10 },
				new int[]{ 0, -2, -4, -8, 16, 32, 16, -8, -4 },
				new int[]{ 1, -42, 7, 12, -16, -1, 0, -10, 35 }
		};
		chart = new ChartItem( "Performance Review:", dataSequences, colors, style );
		this.form.append( chart );

		dataSequences = new int[][] {
				new int[]{ -12, 0, 5, 20, -25, -40, -10, -40, -10 },
				new int[]{ 0, -2, -4, -8, 16, 32, 16, -8, -4 },
				new int[]{ 1, -42, 7, 12, -16, -1, 0, -10, 35 }
		};
		chart = new ChartItem( "Turnover:", dataSequences, colors, style );
		this.form.append( chart );
	}

}