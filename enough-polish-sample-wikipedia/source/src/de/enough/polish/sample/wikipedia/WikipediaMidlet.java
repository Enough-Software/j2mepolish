package de.enough.polish.sample.wikipedia;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import de.enough.polish.browser.html.HtmlBrowser;
import de.enough.polish.event.ThreadedCommandListener;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.ui.splash.ApplicationInitializer;
import de.enough.polish.ui.splash.InitializerSplashScreen;
import de.enough.polish.util.Locale;
import de.enough.polish.util.TextUtil;

/**
 * <p>Shows Wikipedia entries</p>
 *
 * <p>Copyright Enough Software  2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class WikipediaMidlet 
	extends MIDlet
	implements CommandListener, ApplicationInitializer
{
	private static final Command CMD_MAIN_MENU = new Command( Locale.get("cmd.MainMenu"), Command.BACK, 6 );
	private static final Command CMD_BACK = new Command( Locale.get("cmd.Back"), Command.BACK, 5 );
	private static final Command CMD_EXIT = new Command( Locale.get("cmd.Exit"), Command.EXIT, 10);
	private static final Command CMD_GO = new Command( Locale.get("cmd.Go"), Command.OK, 2 );
	
	private static final String[] PROJECTS = new String[] {
		"Wikipedia",
		"Wiktionary",
		"Wikisource",
		"Wikimedia",
		"Wikiquote",
		"Wikiversity"
	};
	

	private Display display;
	private Form browserScreen;
	private HtmlBrowser htmlBrowser;
	private List mainMenu;
	private String urlDefault = "http://wikipedia.7val.com/j2mepolish?startpage=long";
	private String urlSearch = ".7val.com/j2mepolish/wiki/";
	private String urlOptions = ""; // does currently not work "&table=yes";
	
	private Form searchForm;

     protected void startApp()
     	throws MIDletStateChangeException
     {
          this.display = Display.getDisplay(this);
          if (this.mainMenu == null) {
	          try {
		          Image splashImage = Image.createImage("/splash.png");
		          InitializerSplashScreen splashScreen = new InitializerSplashScreen(this.display, splashImage, 0xffffff, null, 0, this  );
		          this.display.setCurrent( splashScreen );
	          } catch (Exception e) {
	        	  //#debug error
	        	  System.out.println("Unable to create splash screen" + e);
	        	  Displayable disp = initApp();
	        	  this.display.setCurrent( disp );
	          }
          } 
     }
     

 	/* (non-Javadoc)
 	 * @see de.enough.polish.ui.splash.ApplicationInitializer#initApp()
 	 */
 	public Displayable initApp() {
 		 //#style screenMain
         List menu = new List( Locale.get( "main.Title" ), Choice.IMPLICIT);
         //#style itemMain
         menu.append( Locale.get("main.StartPage"), null);
         //#style itemMain
         menu.append( Locale.get("main.Search"), null);
         //#style itemMain
         menu.append( Locale.get("main.About"), null);
         //#style itemMain
         menu.append( Locale.get("main.Exit"), null);
         menu.addCommand( CMD_EXIT );
         menu.setCommandListener(this);
         this.mainMenu = menu;
                  
		//#style htmlBrowserForm
		this.browserScreen = new Form(Locale.get( "browser.Title" ));
		//#style htmlBrowser
		this.htmlBrowser = new HtmlBrowser();
		this.browserScreen.append( this.htmlBrowser );
		this.browserScreen.addCommand(CMD_BACK);
		this.browserScreen.addCommand(CMD_EXIT);
		this.browserScreen.addCommand(CMD_MAIN_MENU);
		this.browserScreen.setCommandListener( new ThreadedCommandListener(this) );
         
         
		//#style screenSearch
		Form form = new Form( Locale.get("search.Title") );
		//#style itemInput
		TextField textField = new TextField( Locale.get("search.SearchLabel"), "", 80, TextField.URL );
		form.append(textField);
		//#style projectChoice
		ChoiceGroup projectsGroup = new ChoiceGroup( Locale.get("search.ProjectLabel"), ChoiceGroup.EXCLUSIVE );
		for (int i=0; i<PROJECTS.length; i++) {
			String name = PROJECTS[i];
			Image image = null;
			try {
				image = Image.createImage( "/" + name + ".png");
			} catch (Exception e) {
				// ignore
				System.out.println("Unable to load image: " + e.toString());
			}
			//#style projectChoiceItem
			projectsGroup.append( name, image);
		}
		form.append( projectsGroup );
		form.setCommandListener( this );
		form.addCommand( CMD_BACK );
		form.addCommand( CMD_GO );
		this.searchForm = form;
         
         try { Thread.sleep(500); } catch (Exception e) { // just wait a little bit so that the splash screen can be recognized 
        	 // ignore
         }
         
 		return menu;
 	}

     protected void pauseApp(){
          // ignore
     }

     protected void destroyApp(boolean unconditional) throws MIDletStateChangeException{
          // nothing to clean up
     }

	public void commandAction(Command command, Displayable displayable) {
		//#debug
		System.out.println("commandAction: cmd=" + command.getLabel() );
		if (displayable == this.mainMenu) {
			if (command == CMD_EXIT) {
				notifyDestroyed();
			} else {
				int index = this.mainMenu.getSelectedIndex();
				switch (index) {
				case 0:
					showHtmlBrowser( getDefaultUrl() );
					break;
				case 1:
					showSearch();
					break;
				case 2:
					showAbout();
					break;
				case 3:
					notifyDestroyed();
					break;
				}
			}
		} else if (displayable == this.browserScreen){
			if (this.htmlBrowser.handleCommand(command)) {
				return;
			}
			if (command == CMD_BACK) {
				if (this.htmlBrowser.canGoBack()) {
					this.htmlBrowser.goBack();
				} else {
					this.htmlBrowser.clearHistory();
					this.display.setCurrent( this.mainMenu );
				}
			} else if (command == CMD_MAIN_MENU) {
				this.htmlBrowser.clearHistory();
				this.display.setCurrent( this.mainMenu );
			}
		} else if (displayable == this.searchForm) {
			if (command == CMD_BACK) {
				this.display.setCurrent( this.mainMenu );
			} else {
				TextField textField = (TextField) this.searchForm.get(0);
				String searchWord = textField.getString();
				if (searchWord.length() == 0) {
					return;
				}
				ChoiceGroup projectsGroup = (ChoiceGroup) this.searchForm.get(1);
				int project =  projectsGroup.getSelectedIndex();
				String url = getSearchUrl(searchWord, project);
				showHtmlBrowser(url);
			}
		}
		if (command == CMD_EXIT) {
			notifyDestroyed();
		}
	}


	/**
	 * Shows the about screen
	 */
	private void showAbout() {
		//#style aboutAlert
        Alert alert = new Alert(Locale.get("about.Title"), Locale.get("about.Text"), null, null);
        alert.setTimeout(Alert.FOREVER);
        try {
	          Image splashImage = Image.createImage("/splash.png");
	          alert.setImage(splashImage);
        } catch (Exception e) {
        	//#debug error
        	System.out.println("Unable to load splash image" + e );
        }
        this.display.setCurrent( alert );
	}

	/**
	 * Shows the settings screen
	 */
	private void showSearch() {
		this.display.setCurrent( this.searchForm );
	}

	/**
	 * @param url the URL that should be shown
	 */
	private void showHtmlBrowser(String url) {
		this.htmlBrowser.go(url);
		this.display.setCurrent( this.browserScreen );
	}

	/**
	 * @return the URL of the default URL for wikipedia
	 */
	private String getDefaultUrl() {
		return this.urlDefault ;
	}


	/**
	 * @param searchUrl the searchUrl to set
	 */
	public void setSearchUrl(String searchUrl)
	{
		this.urlSearch = searchUrl;
	}


	/**
	 * @return the searchUrl
	 */
	public String getSearchUrl( String search, int project)
	{
		return "http://" + PROJECTS[project].toLowerCase() + this.urlSearch + TextUtil.encodeUrl(search) + this.urlOptions;
	}

	

}