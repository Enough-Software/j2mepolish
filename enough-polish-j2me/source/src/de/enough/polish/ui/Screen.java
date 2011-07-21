//#condition polish.usePolishGui
/*
 * Created on 12-Mar-2004 at 21:46:17.
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

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

//#if polish.doja
import com.nttdocomo.ui.Frame;
//#endif

import de.enough.polish.event.AsynchronousMultipleCommandListener;
import de.enough.polish.event.EventManager;
import de.enough.polish.event.UiEventListener;
import de.enough.polish.ui.backgrounds.TranslucentSimpleBackground;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.DeviceControl;
import de.enough.polish.util.Locale;

//#ifdef polish.Screen.imports:defined
	//#include ${polish.Screen.imports}
//#endif


/**
 * The common superclass of all high-level user interface classes.
 * 
 * The contents displayed and their interaction with the user are defined by
 * subclasses.
 * 
 * <P>Using subclass-defined methods, the application may change the contents
 * of a <code>Screen</code> object while it is shown to the user.  If
 * this occurs, and the
 * <code>Screen</code> object is visible, the display will be updated
 * automatically.  That
 * is, the implementation will refresh the display in a timely fashion without
 * waiting for any further action by the application.  For example, suppose a
 * <code>List</code> object is currently displayed, and every element
 * of the <code>List</code> is
 * visible.  If the application inserts a new element at the beginning of the
 * <code>List</code>, it is displayed immediately, and the other
 * elements will be
 * rearranged appropriately.  There is no need for the application to call
 * another method to refresh the display.</P>
 * 
 * <P>It is recommended that applications change the contents of a
 * <code>Screen</code> only
 * while it is not visible (that is, while another
 * <code>Displayable</code> is current).
 * Changing the contents of a <code>Screen</code> while it is visible
 * may result in
 * performance problems on some devices, and it may also be confusing if the
 * <code>Screen's</code> contents changes while the user is
 * interacting with it.</P>
 * 
 * <P>In MIDP 2.0 the four <code>Screen</code> methods that defined
 * read/write ticker and title properties were moved to <code>Displayable</code>,
 * <code>Screen's</code> superclass.  The semantics of these methods have not changed.</P>
 * <HR>
 * 
 * @since MIDP 1.0
 * 
 */
public abstract class Screen
//#if polish.Bugs.needsNokiaUiForSystemAlerts && !polish.SystemAlertNotUsed
	//#define tmp.needsNokiaUiForSystemAlerts
//#endif
//#if polish.hasCommandKeyEvents || (polish.key.LeftSoftKey:defined && polish.key.RightSoftKey:defined)
	//#define tmp.hasCommandKeyEvents
//#endif
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
//#endif
extends Canvas
implements UiElement, Animatable
{
	private final static int POSITION_TOP = 0;
	private final static int POSITION_LEFT = 1;
	
	//#if polish.handleEvents || polish.css.animations
		//#define tmp.handleEvents
		private boolean hasBeenShownBefore;
	//#endif
	
	//#if tmp.fullScreen || polish.midp1 || (polish.usePolishTitle == true)
		//#define tmp.usingTitle
		protected Item title;
		private boolean excludeTitleForBackground;
		//#if polish.css.separate-title
			private boolean separateTitle;
		//#endif
		
		//#ifdef polish.css.title-style
			private Style titleStyle;
		//#endif
		//#if polish.css.title-position
			private boolean paintTitleAtTop = true;
		//#endif	
		//#if polish.Vendor.Motorola || polish.Bugs.ignoreTitleCall
			//#define tmp.ignoreTitleCall
			private boolean ignoreTitleCall = true;
		//#endif
	//#endif

	//#ifdef polish.Vendor.Siemens
		// Siemens sometimes calls hideNotify directly
		// after showNotify for some reason.
		// So hideNotify checks how long the screen
		// has been shown - if not long enough,
		// the call will be ignored.
		private long showNotifyTime;
	//#endif
	private Item subTitle;
	protected int subTitleHeight;
	//#if polish.css.subtitle-position
		private boolean paintSubTitleAtTop = true;
	//#endif
	protected int titleHeight;
	protected Background background;
	protected int backgroundX;
	protected int backgroundY;
	protected int backgroundWidth;
	protected int backgroundHeight;
	protected Border border;
	protected Style style;
	//#if (polish.css.content-background-width || polish.css.content-background-height) && (polish.css.content-bgborder || polish.css.content-background || polish.css.content-border)
		protected Dimension contentBackgroundWidth;
		protected Dimension contentBackgroundHeight;
		protected int contentBackgroundAnchor = Graphics.HCENTER | Graphics.VCENTER;
	//#endif
	//#if polish.css.content-background
		/** the content's (container) background */
		protected Background contentBackground;
	//#endif
	//#if polish.css.content-border
		/** the content's (container) border */
		protected Border contentBorder;
	//#endif
	//#if polish.css.content-bgborder
		/** the content's (container) border that is drawn before the content-background is drawn */
		protected Border contentBgBorder;
	//#endif
	/** the screen height minus the ticker height and the height of the menu bar */
	protected int screenHeight;
	/** the screen height minus the height of the menu bar */
	protected int originalScreenHeight;
	protected int screenWidth;
	//#ifndef polish.skipTicker
		private Ticker ticker;
		//#if polish.Ticker.Position:defined
			//#if top == ${ lowercase(polish.Ticker.Position) }
				//#define tmp.paintTickerAtTop
			//#else
				//#define tmp.paintTickerAtBottom
			//#endif
		//#elif polish.css.ticker-position
			private boolean paintTickerAtTop;
		//#else
			//#define tmp.paintTickerAtBottom
		//#endif
	//#endif
	protected String cssSelector;
	private ForwardCommandListener forwardCommandListener;
	protected Container container;
	private boolean isLayoutCenter;
	private boolean isLayoutRight;
	private boolean isLayoutVCenter;
	private boolean isLayoutBottom;
	private boolean isLayoutHorizontalShrink;
	private boolean isLayoutVerticalShrink;
	boolean isInitialized;
	//#if polish.css.screen-transition || polish.css.screen-change-animation || polish.ScreenChangeAnimation.forward:defined
		//#define tmp.screenTransitions
		protected Command lastTriggeredCommand;
	//#endif	
	//#if polish.key.ReturnKey:defined || polish.css.repaint-previous-screen
		//#define tmp.triggerBackCommand
		private Command backCommand;
	//#endif
	//#if (polish.useMenuFullScreen && tmp.fullScreen) || polish.needsManualMenu
		//#define tmp.menuFullScreen
		/** the real, complete height of the screen - this includes title, subtitle, content and menubar */
		protected int fullScreenHeight;
		protected int menuBarHeight;
		private boolean excludeMenuBarForBackground;
		
		private Command okCommand;
		//#if polish.key.ClearKey:defined
			//#define tmp.triggerCancelCommand
			private Command cancelCommand;
		//#endif

		//#if polish.MenuBar.useExtendedMenuBar || polish.classes.MenuBar:defined
			//#if polish.classes.MenuBar:defined
				//#= public final ${polish.classes.MenuBar} menuBar;
			//#else
				private final MenuBar menuBar;
			//#endif
			//#define tmp.useExternalMenuBar
			//#if polish.css.title-menu
				//#define tmp.useTitleMenu
			//#endif
		//#else
			private Command menuSingleLeftCommand;
			private String menuLeftString;
			private Command menuSingleRightCommand;
			private String menuRightString;
			private Container menuContainer;
			private ArrayList menuCommands;
			private boolean menuOpened;
			private Font menuFont;
			private int menuFontColor = 0;
			private int menuBarColor = 0xFFFFFF;
			//#ifdef polish.hasPointerEvents
				private int menuRightCommandX;
				private int menuLeftCommandX;
			//#endif
		//#endif
	//#endif
	/** The currently focused items which has item-commands */
	/*package-private*/ Item focusedItem;
	//#if polish.useScrollBar || polish.classes.ScrollBar:defined
		//#define tmp.useScrollBar
		//#if polish.classes.ScrollBar:defined
			//#style scrollbar?
			//#= protected final ${polish.classes.ScrollBar} scrollBar = new ${polish.classes.ScrollBar}();
		//#else
			//#style scrollbar?
			protected final ScrollBar scrollBar = new ScrollBar();
		//#endif
		//#if polish.css.scrollbar-position
			protected boolean paintScrollBarOnRightSide = true;
		//#endif
		//#if polish.css.show-scrollbar
			protected boolean scrollBarVisible = true;
		//#endif
	//#elif !polish.deactivateScrollIndicator
		//#define tmp.useScrollIndicator
		private boolean paintScrollIndicator;
		private boolean paintScrollIndicatorUp;
		private boolean paintScrollIndicatorDown;
		private int scrollIndicatorColor;
		private int scrollIndicatorX; // left x position of scroll indicator
		private int scrollIndicatorY; // top y position of scroll indicator
		private int scrollIndicatorWidth; // width of the indicator
		private int scrollIndicatorHeight; // width of the indicator
		//#if polish.css.scrollindicator-up-image || polish.css.scrollindicator-down-image 
			private Image scrollIndicatorUpImage; 
			private Image scrollIndicatorDownImage; 
		//#endif
	//#endif
	//#if tmp.usingTitle || tmp.menuFullScreen
		private boolean showTitleOrMenu = true;
	//#endif
	/** an info text which is shown e.g. when some content is added to textfields */
	private Item infoItem;
	/** determines whether the info text should be shown */
	protected int infoHeight;
	//#ifdef polish.css.foreground-image
		private Image foregroundImage;
		private int foregroundX;
		private int foregroundY;
	//#endif
	//#if polish.css.clip-screen-info
		private boolean clipScreenInfo;
	//#endif	
	//#if polish.android
		//#define tmp.trackKeyUsage
		/** flag for key pressed events - only for internal usage on Android platforms! */
		public boolean keyPressedProcessed;
		/** flag for key released events - only for internal usage on Android platforms! */
		public boolean keyReleasedProcessed;
	//#endif
	protected int contentX;
	protected int contentY;
	protected int contentWidth;
	protected int contentHeight;
	private int marginLeft;
	private int marginRight;
	private int marginTop;
	private int marginBottom;
	//#if polish.css.separate-menubar
		private boolean separateMenubar = true;
	//#endif
	//#if polish.css.repaint-previous-screen
		protected boolean repaintPreviousScreen;
		//#if polish.css.repaint-previous-screen-anchor
			private int repaintPreviousScreenAnchor;
		//#endif
		private Canvas previousScreen;
		//#if !polish.Bugs.noTranslucencyWithDrawRgb
			protected Background previousScreenOverlayBackground;
		//#endif
		//#if polish.ScreenOrientationCanChange && tmp.usingTitle
			private Background previousScreenTitleBackground;
			private int previousScreenTitleHeight;
		//#endif
	//#endif
	protected ScreenStateListener screenStateListener;
	private boolean isScreenChangeDirtyFlag;
	protected ItemStateListener itemStateListener;
//	private ArrayList stateNotifyQueue;
	private final Object paintLock;
	private ArrayList itemCommands;
	private Object data;
	/** The last time in ms when the user interacted with this screen. This value is used for stopping
	 * animations after a period of inactivity. This defaults to 3 minutes, but it can be set with the preprocessing
	 * variable polish.Animation.MaxIdleTime (integer with the number of ms, 60000 is one minute).
	 */
	protected long lastInteractionTime;
	//private boolean isInRequestInit;
	protected boolean ignoreRepaintRequests;
	protected boolean isRepaintRequested;
	/** requests a call to calcuateContentArea() the next time this screen is being painted */
	protected boolean isInitRequested;
	private CommandListener realCommandListener;
	private boolean isResourcesReleased;
	//private String lastSizeChangedEvent;
	//#if polish.Bugs.noSoftKeyReleasedEvents
		protected int triggerReleasedKeyCode;
		protected long triggerReleasedTime;
	//#endif
	//#if polish.css.scroll-background
		protected boolean isScrollBackground;
	//#endif
	
	protected long lastAnimateTime = -1;
	protected boolean isAnimated = false;
	
	private int keyStates;
	private int releasedKeys;
	
	//#if polish.css.content-expand
		boolean isContentExpand;
	//#endif	
	
	//#if tmp.useTitleMenu
		boolean hasTitleMenu;
	//#endif	
	//#if polish.css.scroll-up-background
		private Background scrollUpBackground;
	//#endif
	//#if polish.css.scroll-down-background
		private Background scrollDownBackground;
	//#endif
	//#if polish.css.move-scroll-backgrounds && (polish.css.scroll-up-background || polish.css.scroll-down-background)
		private boolean isMoveScrollBackgrounds = true;
	//#endif
	//#if polish.css.screen-change-animation	
		protected boolean enableScreenChangeAnimation = true;
	//#endif
	//#if polish.useNativeGui
		private NativeScreen nativeScreen;
	//#endif
	private UiEventListener uiEventListener;
	
	//#if polish.css.portrait-style || polish.css.landscape-style
		protected Style landscapeStyle;
		protected Style portraitStyle;
	//#endif
	//#if polish.hasPointerEvents
		protected final ClippingRegion userEventRepaintRegion = new ClippingRegion();
	//#endif
	private ScreenInitializerListener screenInitializerListener;
	
	/**
	 * Creates a new screen, this constructor can be used together with the //#style directive.
	 * 
	 * @param title the title, or null for no title
	 * @param createDefaultContainer true when the default container should be created.
	 */
	public Screen( String title, boolean createDefaultContainer ) {
		this( title, null, createDefaultContainer );
	}

	/**
	 * Creates a new screen, this constructor can be used together with the //#style directive.
	 * 
	 * @param title the title, or null for no title
	 * @param style the style of this screen
	 * @param createDefaultContainer true when the default container should be created.
	 */
	public Screen( String title, boolean createDefaultContainer, Style style ) {
		this( title, style, createDefaultContainer );
	}

	/**
	 * Creates a new screen
	 * 
	 * @param title the title, or null for no title
	 * @param style the style of this screen
	 * @param createDefaultContainer true when the default container should be created.
	 */
	public Screen( String title, Style style, boolean createDefaultContainer ) {
		super();
		//#if tmp.useScrollBar
			this.scrollBar.screen = this;
		//#endif
						
		// creating standard container:
		if (createDefaultContainer) {
			//#if polish.hasVirtualKeyboard
				this.container = new Container( false );
			//#else
				this.container = new Container( true );
			//#endif
			this.container.screen = this;
			this.container.isFocused = true;
		}
		this.style = style;
		
		//#if tmp.useTitleMenu
		if(style != null)
		{
			this.hasTitleMenu = hasTitleMenu(this.style);
		}
		//#endif
		
		this.forwardCommandListener = new ForwardCommandListener();
		//#ifndef tmp.menuFullScreen
			super.setCommandListener(this.forwardCommandListener);
		//#endif
		//#ifdef tmp.useExternalMenuBar
			//#if polish.classes.MenuBar:defined
				//#style menubar, menu, default
				//#= this.menuBar = new ${polish.classes.MenuBar}( this );
			//#else
				//#if tmp.useTitleMenu
				if(this.hasTitleMenu)
				{
					//#style menubar, menu, default
			 		this.menuBar = new TitleMenuBar( this );
			 		setTitle(this.menuBar);
				}
				else
				//#endif
				{
		 			//#style menubar, menu, default
		 			this.menuBar = new MenuBar( this );
				}
	 		//#endif
		//#endif
	 	
		setTitle( title );
		
		//make sure paintlock is initialized after all the other variables
		this.paintLock = new Object();
	}
		
	/**
	 * Initializes this screen before it is painted for the first time.
	 */
	protected void init( int width, int height) {
		//#debug
		System.out.println("Initializing screen " + this + " with dimension " + width + "x" + height);
		
		// calling super.setFullScreenMode(true) is already done within the showNotify() method
		if (height == 0 || width == 0) {
			return; // invalid initialization values...
		}
		//#ifdef tmp.menuFullScreen
			this.fullScreenHeight = height;
		//#endif
		this.screenHeight = height;
		this.originalScreenHeight =  height;
		this.screenWidth = width;

		//#ifdef polish.Screen.initCode:defined
			//#include ${polish.Screen.initCode}
		//#endif
		if (this.style != null) {
			setStyle( this.style );
		}
		//#ifdef polish.useDynamicStyles
			// check if this screen has got a style:
			if (this.style == null) {
				this.cssSelector = createCssSelector();
				setStyle( StyleSheet.getStyle( this ) );
			} else {
				this.cssSelector = this.style.name;
			}
		//#endif

		//#if tmp.usingTitle
			if (this.title != null) {
				this.title.onScreenSizeChanged(width, height);
			}
		//#endif
		//#if tmp.useExternalMenuBar
			this.menuBar.onScreenSizeChanged(width, height);
		//#endif
		if (this.subTitle != null) {
			this.subTitle.onScreenSizeChanged(width, height);
		}
		if (this.container != null) {
			this.container.onScreenSizeChanged(width, height);
		}

		if (this.style != null) {
			this.marginLeft = this.style.getMarginLeft(this.screenWidth);
			this.marginRight = this.style.getMarginRight(this.screenWidth);			
			this.marginTop = this.style.getMarginTop(this.screenHeight);
			this.marginBottom = this.style.getMarginBottom(this.screenHeight);
		}
		//#ifdef tmp.menuFullScreen
			//#ifdef tmp.useExternalMenuBar
				int availableScreenWidth = this.screenWidth;
				//#if polish.css.separate-menubar
					if (!this.separateMenubar) {
						availableScreenWidth -= (this.marginLeft + this.marginRight);
					}
				//#endif
				//#if polish.ScreenOrientationCanChange && ! ( polish.key.TopLeftSotkey:defined || polish.hasPointerEvents )
//					if (this.screenWidth > this.screenHeight) {
//						this.menuBar.setOrientationVertical( true );
//					} else {
//						this.menuBar.setOrientationVertical( false );
//					}
				//#endif
					
				//#if tmp.useTitleMenu
					if(this.hasTitleMenu)
					{
						this.menuBarHeight = 0;
					}
					else
				//#endif
				{
					this.menuBarHeight = this.menuBar.getSpaceBottom( availableScreenWidth, this.fullScreenHeight);
				}
				
				//#if tmp.useScrollIndicator
					int scrollWidth = this.menuBar.contentHeight + this.menuBar.paddingTop + this.menuBar.paddingBottom;
					int scrollHeight = scrollWidth;
					//#if polish.css.scrollindicator-up-image
						if (this.scrollIndicatorUpImage != null) {
							scrollWidth = this.scrollIndicatorUpImage.getWidth(); 
							scrollHeight = this.scrollIndicatorUpImage.getHeight();
						}
					//#elif polish.css.scrollindicator-down-image
						if (this.scrollIndicatorDownImage != null) {
							scrollWidth = this.scrollIndicatorDownImage.getWidth(); 
							scrollHeight = this.scrollIndicatorDownImage.getHeight();
						}
					//#endif
					this.scrollIndicatorWidth = scrollWidth;
					this.scrollIndicatorHeight = scrollHeight;
					this.scrollIndicatorX = (this.screenWidth >> 1) - (scrollWidth >> 1);
					int space = Math.max( 0, this.menuBarHeight - ((scrollHeight << 1) + 1) );
					//System.out.println("space=" + space + ", menubarHEIGHT="+ this.menuBarHeight);
					this.scrollIndicatorY = (this.fullScreenHeight - this.menuBarHeight) + (space >> 1);
				//#endif
				//System.out.println("without ExternalMenu: scrollIndicatorY=" + this.scrollIndicatorY + ", screenHeight=" + this.screenHeight + ", FullScreenHeight=" + this.fullScreenHeight );	
				//System.out.println("Screen.init: menuBarHeight=" + this.menuBarHeight + " scrollIndicatorWidth=" + this.scrollIndicatorWidth );
			//#else
				//#ifdef polish.css.style.menu
					Style menustyle = StyleSheet.menuStyle;
				//#else
					//# Style menustyle = this.style;
				//#endif
				if (menustyle != null) {
					Integer colorInt = null;
					if (this.style != null) {
						colorInt = this.style.getIntProperty("menubar-color");
					}
					if (colorInt == null) {
						colorInt = menustyle.getIntProperty("menubar-color");
					}
					if (colorInt != null) {
						this.menuBarColor = colorInt.intValue();
					}
					this.menuFontColor = menustyle.getFontColor();
					if (menustyle.getFont() != null) {
						this.menuFont = menustyle.getFont();
					} else {
						this.menuFont = Font.getFont( Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM );				
					}			
				} else {
					this.menuFont = Font.getFont( Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM );
				}
				int localMenuBarHeight = this.menuFont.getHeight();
				//#ifdef polish.MenuBar.PaddingBottom:defined
					//#= localMenuBarHeight += ${polish.MenuBar.PaddingBottom};  
				//#endif
				//#ifdef polish.MenuBar.PaddingTop:defined
					//#= localMenuBarHeight += ${polish.MenuBar.PaddingTop};  
				//#endif
				//#if !polish.MenuBar.PaddingBottom:defined && !polish.MenuBar.PaddingTop:defined
					localMenuBarHeight += 2;
				//#endif
				//#if tmp.useScrollIndicator
					//# int scrollWidth = localMenuBarHeight;
					//# int scrollHeight = scrollWidth >> 1;
					//#if polish.css.scrollindicator-up-image
						if (this.scrollIndicatorUpImage != null) {
							scrollWidth = this.scrollIndicatorUpImage.getWidth(); 
							scrollHeight = this.scrollIndicatorUpImage.getHeight();
						}
					//#elif polish.css.scrollindicator-down-image
						if (this.scrollIndicatorDownImage != null) {
							scrollWidth = this.scrollIndicatorDownImage.getWidth(); 
							scrollHeight = this.scrollIndicatorDownImage.getHeight();
						}
					//#endif
					this.scrollIndicatorWidth = scrollWidth;
					this.scrollIndicatorHeight = scrollHeight;
					this.scrollIndicatorX = this.screenWidth / 2 - scrollWidth / 2;
					//# int space = Math.max( 0, localMenuBarHeight - ((scrollHeight << 1) + 1) );
					this.scrollIndicatorY = (this.fullScreenHeight - localMenuBarHeight) + (space >> 1);
				//#endif
				//System.out.println("without ExternalMenu: scrollIndicatorY=" + this.scrollIndicatorY + ", screenHeight=" + this.screenHeight + ", FullScreenHeight=" + this.fullScreenHeight + ", localMenuBarHeight=" + localMenuBarHeight);	
				//#ifdef polish.MenuBar.MarginBottom:defined 
					//#= localMenuBarHeight += ${polish.MenuBar.MarginBottom};
					//#= this.scrollIndicatorY -=  ${polish.MenuBar.MarginBottom};
				//#endif
				//#ifdef polish.MenuBar.MarginTop:defined 
					//#= localMenuBarHeight += ${polish.MenuBar.MarginTop};
				//#endif
				//#if polish.doja 
					localMenuBarHeight = 0;
				//#endif
				this.menuBarHeight = localMenuBarHeight;
				updateMenuTexts();
			//#endif
			int diff = this.originalScreenHeight - this.screenHeight;
			this.originalScreenHeight = this.fullScreenHeight - this.menuBarHeight;
			this.screenHeight = this.originalScreenHeight - diff;
			// set position of scroll indicator:
		//#elif polish.vendor.Siemens && tmp.useScrollIndicator
			// set the position of scroll indicator for Siemens devices 
			// on the left side, so that the menu-indicator is visible:
			//# int scrollWidth = 12;
			//#if polish.css.scrollindicator-up-image
				if (this.scrollIndicatorUpImage != null) {
					scrollWidth = this.scrollIndicatorUpImage.getWidth(); 
				}
			//#elif polish.css.scrollindicator-down-image 
				if (this.scrollIndicatorDownImage != null) {
					scrollWidth = this.scrollIndicatorDownImage.getWidth(); 
				}
			//#endif
			this.scrollIndicatorWidth = scrollWidth;
			this.scrollIndicatorX = 0;
			this.scrollIndicatorY = this.screenHeight - (this.scrollIndicatorWidth + 1);
		//#elif tmp.useScrollIndicator
			// set position of scroll indicator:
			//# int scrollWidth = 12;
			//#if polish.css.scrollindicator-up-image
				if (this.scrollIndicatorUpImage != null) {
					scrollWidth = this.scrollIndicatorUpImage.getWidth(); 
				}
			//#elif polish.css.scrollindicator-down-image 
				if (this.scrollIndicatorDownImage != null) {
					scrollWidth = this.scrollIndicatorDownImage.getWidth(); 
				}
			//#endif
			this.scrollIndicatorWidth = scrollWidth;
			this.scrollIndicatorX = this.screenWidth - this.scrollIndicatorWidth;
			this.scrollIndicatorY = this.screenHeight - (this.scrollIndicatorWidth + 1);
		//#endif
			
		//System.out.println("final: scrollIndicatorY=" + this.scrollIndicatorY + ", screenHeight=" + this.screenHeight + ", FullScreenHeight=" + this.fullScreenHeight );	
		if (this.container != null) {
			this.container.screen = this;
		}
		int availableWidth = this.screenWidth - this.marginLeft + this.marginRight;
		if (this.border != null) {
			availableWidth -= this.border.borderWidthLeft + this.border.borderWidthRight;
		}
		//#if tmp.menuFullScreen && tmp.useExternalMenuBar
			//#if tmp.usingTitle
				if (this.title != null) {
					this.titleHeight = this.title.getItemHeight( availableWidth, availableWidth, this.screenHeight ); 
				}
			//#endif
			this.menuBar.relativeY = this.screenHeight;
		//#endif
		calculateContentArea( 0, 0, this.screenWidth, this.screenHeight );
		//#if tmp.menuFullScreen &&  tmp.useExternalMenuBar 
			//#if tmp.useTitleMenu
			if(!this.hasTitleMenu)
			//#endif
			{
				int menuHeight = this.menuBar.getSpaceBottom( availableScreenWidth, this.fullScreenHeight);
				if (menuHeight != this.menuBarHeight) {
					// this can happen when the container has been initialized within calculateContentArea...
					this.menuBarHeight = menuHeight;
					this.screenHeight = this.fullScreenHeight - menuHeight;
					calculateContentArea( 0, 0, this.screenWidth, this.screenHeight );
				}
			}
		//#endif

		this.isInitialized = true;
	}
	
	/**
	 * Reinitializes this screen's content area.
	 * 
	 * This call returns immediately and results in a run of the initialization at some later point in some screen subclasses
	 * or when this screen has a shrink layout.
	 */
	protected void requestInit() {
		//#debug
		System.out.println("requestInit() for " + this);
		this.isInitRequested = true;
	}
	

	/**
	 * Forwards a repaint request only when those requests should not be ignored.
	 * Requests should be usually ignored during the event handling, for example. 
	 * @see #ignoreRepaintRequests
	 */
	public void requestRepaint() {
		if (this.ignoreRepaintRequests) {
			this.isRepaintRequested = true;
			return;
		}
		//#if polish.Screen.callSuperEvents
			//#  super.requestRepaint();
			super.repaint();
		//#else
			Display instance = Display.getInstance();
			if (instance != null) {
				instance.requestRepaint();
			}
		//#endif
	}

	/**
	 * Forwards a repaint request only when those requests should not be ignored.
	 * Requests should be usually ignored during the event handling, for example. 
	 * @param x the x coordinate of the area that needs to be refreshed
	 * @param y the y coordinate of the area that needs to be refreshed
	 * @param width the width of the area that needs to be refreshed
	 * @param height the height of the area that needs to be refreshed
	 * @see #ignoreRepaintRequests
	 */
	public void requestRepaint( int x, int y, int width, int height ) {
		if (this.ignoreRepaintRequests) {
			return;
		}
		//#if polish.Screen.callSuperEvents
			//#  super.requestRepaint( x, y, width, height );
		//#else
			Display instance = Display.getInstance();
			if (instance != null) {
				instance.requestRepaint(x, y, width, height );
			}
		//#endif
		
	}
	
	
	
	/**
	 * Checks if this screen's content area should be refreshed when the specified item has changed it's size.
	 * 
	 * @param source the source of the event
	 * @return true when the source is a directly visible element (default container or title, for example)
	 */
	protected boolean checkForRequestInit(Item source) {
		if (		source == this.container 
			//#if tmp.usingTitle
				|| 	source == this.title
			//#endif
			//#if tmp.useExternalMenuBar
				|| 	source == this.menuBar
			//#endif
				) 
		{
			return true;
		} else {
			return false;
		}
	}


		
	/**
	 * Calculates and initializes the content area for this screen.
	 * Usually no items are painted outside of the specified area.
	 * This method knows about the title, subtitle, infoarea and ticker
	 * and adjusts the content area accordingly
	 * 
	 * @param x left start of the content area, might later be adjusted by an external scrollindicator
	 * @param y top start of the content area, is adjusted by the top margin, title height, subtitle height, 
	 *        info height and maybe ticker height (when the ticker should be painted at the top).
	 * @param width width of the content area, might later be adjusted by an external scrollindicator
	 * @param height height of the content area, is adjusted by the title height, subtitle height, 
	 *        info height and ticker height.
	 */
	protected void calculateContentArea( int x, int y, int width, int height ) {
		//#debug
		System.out.println("calculateContentArea(" + x + ", " + y + ", " + width + ", " + height + ") for " + this);
		if (width < 1 || height < 1 ) {
			//#debug info
			System.out.println("invalid content dimension, width=" + width + ", height=" + height);
			return;
		}
		
		int borderWidthL = 0;
		int borderWidthR = 0;
		int borderWidthT = 0;
		int borderWidthB = 0;
		if (this.border != null) {
			borderWidthL = this.border.borderWidthLeft;
			x += borderWidthL;
			borderWidthR = this.border.borderWidthRight;
			width -= borderWidthL + borderWidthR;
			borderWidthT = this.border.borderWidthTop;
			y += borderWidthT;
			borderWidthB = this.border.borderWidthBottom;
			height -= borderWidthT + borderWidthB;
		}
		x += this.marginLeft;
		width -= this.marginLeft + this.marginRight;
		y += this.marginTop;
		height -= this.marginTop + this.marginBottom;

		Container cont = this.container;
//		int containerWidth = 0;
//		int containerHeight = 0; 
		int originalWidth = width;
		if (cont != null && this.isLayoutHorizontalShrink) {
			width = cont.getItemWidth(width, width, height) + cont.paddingLeft + cont.paddingRight;
//			containerHeight = cont.itemHeight;
//			width = containerWidth;
		}
		boolean isTitleAtTop = true;
		//#if tmp.usingTitle
			if (this.title != null) {
				//TODO 	use #if polish.css.separate-title
				this.titleHeight = this.title.getItemHeight( width, width, height );
				this.title.relativeX = x;
				this.title.relativeY = y;
				//#if polish.css.title-position
					if (!this.paintTitleAtTop) {
						isTitleAtTop = false;
						this.title.relativeY = this.screenHeight - this.titleHeight;
					}
				//#endif
				int tw = this.title.itemWidth;
				if (tw < originalWidth) {
					if ((this.title.isLayoutRight && !this.isLayoutHorizontalShrink) || (this.isLayoutHorizontalShrink && this.isLayoutRight)) {
						this.title.relativeX = x + (originalWidth - tw);
					} else if ((this.title.isLayoutCenter && !this.isLayoutHorizontalShrink) || (this.isLayoutHorizontalShrink && this.isLayoutCenter)) {
						this.title.relativeX = x + (originalWidth - tw) / 2;
					}
				}
			}
			if (isTitleAtTop && this.excludeTitleForBackground) {
				this.backgroundY += this.titleHeight;
				this.backgroundHeight -= this.titleHeight;
			}
		//#endif
		boolean isSubTitleAtTop = true;
		if (this.subTitle != null) {
			this.subTitle.relativeX = x;
			//#if polish.css.subtitle-position
				if (!this.paintSubTitleAtTop) {
					isSubTitleAtTop = false;
				} else {
			//#endif
					this.subTitle.relativeY = y + this.titleHeight;
			//#if polish.css.subtitle-position
				}
			//#endif
			
			this.subTitleHeight = this.subTitle.getItemHeight( width, width, height );
			int sw = this.subTitle.itemWidth;
			if (sw < originalWidth) {
				if (this.subTitle.isLayoutRight) {
					this.subTitle.relativeX += originalWidth - sw;
				} else if (this.subTitle.isLayoutCenter) {
					this.subTitle.relativeX += (originalWidth - sw) / 2;
				}
			}
		}
//		x += this.marginLeft;
//		width -= this.marginLeft + this.marginRight;
//		y += this.marginTop;
//		height -= this.marginTop + this.marginBottom;
//		if (this.border != null) {
//			width  -= borderWidthL + borderWidthR;
//			height -= borderWidthT + borderWidthB;
//			x += this.border.borderWidthLeft;
//			y += this.border.borderWidthTop;
//		}
		
		int topHeight = 0;
		//#if polish.css.clip-screen-info
			if (this.clipScreenInfo) {
				topHeight += this.infoHeight;
			}
		//#endif
		if (isTitleAtTop) {
			topHeight += this.titleHeight;
		} else {
			height -= this.titleHeight;
		}
		if (isSubTitleAtTop) {
			topHeight += this.subTitleHeight;
		} else {
			height -= this.subTitleHeight;
		}
		y += topHeight;
		//#if tmp.useExternalMenuBar
			//TODO use #if polish.css.separate-menubar    
			// if (!this.separateMenubar) {

			//#if polish.MenuBar.Position:defined
				if(this.menuBar != null)
				{
					if (this.excludeMenuBarForBackground) {
						this.backgroundHeight -= this.menuBar.getSpaceBottom( this.screenWidth, this.fullScreenHeight );
					}
					int space = this.menuBar.getSpaceLeft( this.screenWidth, this.fullScreenHeight );
					x += space;
					width -= space;
					space = this.menuBar.getSpaceRight( this.screenWidth, this.fullScreenHeight );
					width -= space;
					space = this.menuBar.getSpaceTop( this.screenWidth, this.fullScreenHeight );
					y += space;
					height -= space;
				}
			//#endif
		//#endif
		//#ifndef polish.skipTicker			
			int tickerHeight = 0;
			Item tick = this.ticker;
			if (tick != null) {
				tickerHeight = tick.getItemHeight( width, width, height );
				tick.relativeX = x;
				tick.relativeY = this.screenHeight - tickerHeight;
				//#if tmp.paintTickerAtTop
					tick.relativeY = y;
					y += tickerHeight;
				//#elif polish.css.ticker-position && !polish.TickerPosition:defined
					if (this.paintTickerAtTop ) {
						tick.relativeY = y;
						y += tickerHeight;
					}
				//#endif
			} 	
			height -= topHeight + tickerHeight;
		//#else
			height -= topHeight;	
		//#endif
			
			
		// now set the content coordinates:	
		this.contentX = x;
		//#ifndef polish.skipTicker			
			//#if tmp.paintTickerAtTop
				y += tickerHeight;
			//#elif polish.css.ticker-position && !polish.TickerPosition:defined
				if (this.paintTickerAtTop) {
					y += tickerHeight;
				}
			//#endif
		//#endif
		this.contentY = y;
		this.contentWidth = originalWidth;
		this.contentHeight = height;
			
		adjustContentArea( x, y, originalWidth, height, cont );
		height = this.contentHeight;
		originalWidth = this.contentWidth;
		
		//#if tmp.useExternalMenuBar
			int previousMenuBarHeight = this.menuBarHeight;
		//#endif
		//#if tmp.useScrollBar
			//#if polish.css.show-scrollbar
				if ( this.scrollBarVisible ) {
			//#endif
					if ( cont != null ) {
						this.scrollBar.scrollBarHeight = height;
						//System.out.println("calculateContentArea for " + this + ": container.isInitialised=" + cont.isInitialised );
						int scrollBarWidth = this.scrollBar.getItemWidth(width, width, height);
						if (this.scrollBar.overlap) {
							scrollBarWidth = 0;
						}
						if (cont.itemHeight > height) {
							// it is quite likely that the container's height remains larger than the screen's height, so we substract
							// the scrollbar's width right away:
							int w = originalWidth - scrollBarWidth;
							int containerHeight = cont.getItemHeight(w, w, height);
							if (containerHeight <= height &&  scrollBarWidth != 0 ) {
								// okay, the container's height has changed and we don't need to display the scrollbar anymore, so give the 
								// container more width:
								cont.getItemHeight(originalWidth, originalWidth, height);
							}
						} else {
							int containerHeight = cont.getItemHeight(originalWidth, originalWidth, height);
							if (containerHeight > height &&  scrollBarWidth != 0 ) {
								//System.out.println("calculateContentArea for" + this + ": scrollBar is required for containerHeight of " + containerHeight + ", availableHeight=" + height );					
								int w = originalWidth - scrollBarWidth;
								cont.getItemHeight(w, w, height);
							}
						}
						if (cont.itemHeight <= height) {
							this.scrollBar.isVisible = false;
						}
					}
			//#if polish.css.show-scrollbar
				} else {
					// ensure that container is initialized:
					if(cont != null) {
						cont.getItemHeight(originalWidth, originalWidth, height);
					}
					this.scrollBar.isVisible = false;
				}
			//#endif
			this.scrollBar.relativeX = x + width;
		//#else 
			// ensure that container is initialized:
			if (cont != null) {
				cont.getItemHeight(originalWidth, originalWidth, height);
			}
		//#endif
		//#if tmp.useExternalMenuBar
			int space = this.menuBar.getSpaceBottom( this.screenWidth, this.fullScreenHeight );
			if (space != previousMenuBarHeight
				//#if tmp.useTitleMenu
					&& (!this.hasTitleMenu)
				//#endif
			){
				height += (previousMenuBarHeight - space);
				this.contentHeight = height;
				if (cont != null) {
					cont.getItemHeight(originalWidth, originalWidth, height);
				}
			}
		//#endif

		//System.out.println("calculateContentArea: container.itemHeight=" + cont.itemHeight + ", screenHeight=" + this.screenHeight + ", cont.itemWidth=" + cont.itemWidth );
			
		Item info = this.infoItem;
		if (info != null) {
			//TODO use #if polish.css.clip-screen-info
				//			if (this.infoItem != null && this.clipScreenInfo) {			
				//				topHeight += this.infoHeight;
				//			}
			this.infoHeight = info.getItemHeight(width, width, height);
			info.relativeX = x;
			int iw = info.itemWidth;
			if (iw < originalWidth) {
				if (info.isLayoutRight) {
					info.relativeX += originalWidth - iw;
				} else if (info.isLayoutCenter) {
					info.relativeX += (originalWidth - iw)/2;
				}
			}
			info.relativeY = topHeight;
			//#if polish.css.clip-screen-info
				if (this.clipScreenInfo) {
					info.relativeY = topHeight - this.infoHeight;				
				}
			//#endif
		}
			
		//#if polish.css.subtitle-position
			if (!isSubTitleAtTop) {
				this.subTitle.relativeY = y + height;
			}
		//#endif
		//#if tmp.usingTitle
			if (cont != null
					&& cont.itemHeight < height
					//#if polish.css.separate-title
						&& !this.separateTitle
					//#endif
					&& this.isLayoutVerticalShrink 
					&& this.title != null 
					) 
			{
				
				if (this.isLayoutBottom && isTitleAtTop) {
					this.title.relativeY +=  (height - cont.itemHeight);
				}
				else if (this.isLayoutVCenter) {
					this.title.relativeY +=  (height - cont.itemHeight) / 2;
				}
			}
		//#endif
//		// now set the content coordinates:	
//		this.contentX = x;
//		//#ifndef polish.skipTicker			
//			//#if tmp.paintTickerAtTop
//				y += tickerHeight;
//			//#elif polish.css.ticker-position && !polish.TickerPosition:defined
//				if (this.paintTickerAtTop) {
//					y += tickerHeight;
//				}
//			//#endif
//		//#endif
//		this.contentY = y;
//		this.contentWidth = originalWidth;
//		this.contentHeight = height;
		//#debug
		System.out.println("calculateContentArea: x=" + this.contentX + ", y=" + this.contentY + ", width=" + this.contentWidth + ", height=" + this.contentHeight);
		initContent(cont);
		if (this.screenInitializerListener != null) {
			this.screenInitializerListener.notifyScreenInitialized(this);
		}
		
	}
	
	/**
	 * Subclasses may override this to adjust the content area of a screen.
	 * The default implementation informs a ScreenInitializerListener
	 * 
	 * @param x the contentX field
	 * @param y the contentY field
	 * @param width the contentWidth field
	 * @param height the contentHeight field
	 * @param cont the container, may be null
	 * @see #setScreenInitializerListener(ScreenInitializerListener)
	 */
	protected void adjustContentArea(int x, int y, int width, int height, Container cont) {
		if (this.screenInitializerListener != null) {
			this.screenInitializerListener.adjustContentArea(this);
		}
	}

	/**
	 * Initializes the container and background position 
	 * @param cont the container, may be null if no container is used at all.
	 */
	protected void initContent(Container cont) {
		int borderWidthL = 0;
		int borderWidthR = 0;
		int borderWidthT = 0;
		int borderWidthB = 0;
		if (this.border != null) {
			borderWidthL = this.border.borderWidthLeft;
			borderWidthR = this.border.borderWidthRight;
			borderWidthT = this.border.borderWidthTop;
			borderWidthB = this.border.borderWidthBottom;
		}
		this.backgroundX = this.marginLeft + borderWidthL;
		this.backgroundY = this.marginTop + borderWidthT;
		this.backgroundWidth = this.screenWidth - this.backgroundX - borderWidthR - this.marginRight;
		//#if tmp.menuFullScreen
			this.backgroundHeight = this.fullScreenHeight - this.backgroundY - borderWidthB - this.marginBottom;
			//#if polish.css.background-bottom || polish.css.backgroundrange-bottom
				if (this.excludeMenuBarForBackground) {
					this.backgroundHeight -= this.menuBarHeight - 1;
				} else {
			//#endif
					if (this.isLayoutVerticalShrink) {
						this.backgroundHeight -= this.menuBarHeight - 1;
					}
			//#if polish.css.background-bottom || polish.css.backgroundrange-bottom
				}
			//#endif
		//#else
			this.backgroundHeight = this.screenHeight - this.backgroundY - borderWidthB - this.marginBottom;
		//#endif
		//#if (polish.css.background-top || polish.css.backgroundrange-top) && tmp.usingTitle
			if (this.excludeTitleForBackground) {
				this.backgroundY += this.titleHeight;
				this.backgroundHeight -= this.titleHeight;
			}
		//#endif
		
			
		int x = this.contentX;
		int y = this.contentY;
		int width = this.contentWidth;
		int height = this.contentHeight;
		
		
		if (cont != null) {
			cont.relativeX = x;
			cont.relativeY = y;
			//#debug
			System.out.println("initContent: cont=" + x + ", " + y + " width=" + cont.itemWidth);
			cont.setScrollHeight( height );
			
			int containerHeight = cont.itemHeight;
			int containerWidth = cont.itemWidth;
			if (containerHeight < height) {
				if (this.isLayoutVCenter) {
					cont.relativeY = y + (height - containerHeight)/2;
					if (this.isLayoutVerticalShrink) {
						this.backgroundY += (height - containerHeight)/2;
					}
				} else  if (this.isLayoutBottom) {
					cont.relativeY = y + (height - containerHeight);						
					if (this.isLayoutVerticalShrink) {
						this.backgroundY += (height - containerHeight);
					}
				}
				if (this.isLayoutVerticalShrink) {
					this.backgroundHeight -= (height - containerHeight);
				}

				/*
				//#if polish.css.repaint-previous-screen && polish.css.repaint-previous-screen-anchor
					if (this.repaintPreviousScreen && this.repaintPreviousScreenAnchor != 0 && (this.previousScreen instanceof Screen)) {
						Item previousFocused = ((Screen)this.previousScreen).getCurrentItem();
						if (previousFocused != null) {
							while (previousFocused instanceof Container && ((Container)previousFocused).getFocusedItem() != null) {
								previousFocused = ((Container)previousFocused).getFocusedItem();
							}
							int absY = previousFocused.getAbsoluteY();
							int target;
							if (this.isLayoutVCenter) {
								target = Math.max( absY - (containerHeight / 2), y );
							} else  if (this.isLayoutBottom) {
								target = absY;
								if (target + containerHeight > y + height) {
									target = y + height - containerHeight;
								}
							} else {
								target = Math.max( absY - containerHeight, y );
							}
							int diff = target - cont.relativeY; 
							cont.relativeY = target;
							if (this.isLayoutVerticalShrink) {
								this.backgroundY += diff;
							}
							//#if tmp.usingTitle
								if (this.title != null) {
									this.title.relativeY += diff;
								}
							//#endif
						}
					}
					System.out.println("RESULT:::::::::::: cont.relativeY=" + cont.relativeY + ", title.relativeY=" + title.relativeY + ", backgroundY=" + backgroundY + ", bgHeight=" + backgroundHeight);
				//#endif
				 * 
				 */
			}
			if (containerHeight > height) {
				width -= getScrollBarWidth();
			}
			if (containerWidth < width) {
				int paddingH = 0;
				if (this.isLayoutHorizontalShrink) {
					Style myStyle = this.style;
					if (myStyle != null) {
						paddingH =  myStyle.getPaddingLeft( width ) + myStyle.getPaddingRight( width );
					}
				}

				if (this.isLayoutCenter) {
					cont.relativeX = x + (width - containerWidth)/2;
					if (this.isLayoutHorizontalShrink) {
						this.backgroundX += (width - containerWidth - paddingH)/2;
					}
				} else if (this.isLayoutRight) {
					cont.relativeX = x + (width - containerWidth);
					if (this.isLayoutHorizontalShrink) {
						this.backgroundX += (width - containerWidth - paddingH);
					}
				}
				if (this.isLayoutHorizontalShrink) {
					this.backgroundWidth -= (width - containerWidth - paddingH);
				}
			} else {
				cont.relativeX = x;
			}
		}
	}
	

	/**
	 * Initialises this screen and informs all items about being painted soon.
	 */
	public void showNotify() {
		//#if polish.Screen.callSuperEvents
			super.showNotify();
		//#endif

		//#debug
		System.out.println("showNotify " + this + " isInitialized=" + this.isInitialized);
		try {
			
			//#ifdef polish.Screen.showNotifyCode:defined
				//#include ${polish.Screen.showNotifyCode}
			//#endif
			if (!this.isInitialized) {
				int w = getScreenFullWidth();
				int h = getScreenFullHeight();
				init( w, h );
			}
			//#if polish.blackberry
				else {
					notifyFocusSet(getCurrentItem());
				}
			//#endif
			//#if polish.css.repaint-previous-screen
				if (this.repaintPreviousScreen) {
					//#if !polish.Bugs.noTranslucencyWithDrawRgb
						if (this.previousScreenOverlayBackground == null) {
							//#if polish.color.overlay:defined
								//#= this.previousScreenOverlayBackground = new TranslucentSimpleBackground( ${polish.color.overlay} );
							//#else
								this.previousScreenOverlayBackground = new TranslucentSimpleBackground( 0xAA000000 );
							//#endif
						}
					//#endif
					// by using StyleSheet.currentScreen instead of Display.getCurrent() we
					// circumvent using screen change animations as the previous screen:
					Displayable currentDisplayable = StyleSheet.currentScreen;
					if (currentDisplayable == null || currentDisplayable == this) {
						currentDisplayable = Display.getInstance().getCurrent();
					}
					//#if polish.css.screen-change-animation
						if (currentDisplayable instanceof ScreenChangeAnimation) {
							currentDisplayable = ((ScreenChangeAnimation)currentDisplayable).nextDisplayable;
						}
					//#endif
					if ( currentDisplayable != this && currentDisplayable instanceof Canvas) {
						Screen screen = currentDisplayable instanceof Screen ? (Screen) currentDisplayable : null;
						if (screen != null) {
							// detect circles within previous-screen-queue:
							Screen previous = screen;
							while (previous != null) {
								if (previous.previousScreen instanceof Screen) {
									previous = (Screen) previous.previousScreen;
									if (previous == this) {
										currentDisplayable = (Displayable) previous.previousScreen;
										screen = currentDisplayable instanceof Screen ? (Screen) currentDisplayable : null;
										break;
									}
								} else {
									break;
								}
							}
						}
						//#if polish.ScreenOrientationCanChange && tmp.usingTitle
							if ( screen != null && this.screenWidth > this.screenHeight ) {
								Background titleBg = null;
								if (screen.title != null) {
									titleBg = screen.title.background;
									this.previousScreenTitleHeight = screen.titleHeight;
								} 
								if (titleBg == null && this.title != null) {
									titleBg = this.title.background;
									this.previousScreenTitleHeight = this.titleHeight;
								}
								this.previousScreenTitleBackground = titleBg;
							}
						//#endif
						if (this.previousScreen != null && screen != null) {
							if (screen.previousScreen != this) {
								this.previousScreen = screen; //(Canvas) currentDisplayable;
							} else {
								screen.previousScreen = null;
//									System.out.println("1: showNotify of " + this + ", current=" + currentDisplayable + ", current.previous=" + screen.previousScreen );
							}
						} else {
//								System.out.println("2: showNotify of " + this + ", current=" + currentDisplayable);
							this.previousScreen = (Canvas) currentDisplayable;
						}
					}
					/* TODO: more work required
					//#if polish.css.repaint-previous-screen-anchor
						if (this.repaintPreviousScreenAnchor != 0 && this.container != null) {
							initContent(this.container);
						}
					//#endif
					 */
				}
			//#endif
			
			//#if tmp.handleEvents
				if (!this.hasBeenShownBefore) {
					EventManager.fireEvent( EventManager.EVENT_SHOW_FIRST_TIME,  this, null );
					this.hasBeenShownBefore = true;
				}
				EventManager.fireEvent( EventManager.EVENT_SHOW,  this, null );
			//#endif
			// inform all root items that they belong to this screen
			// and that they will be shown soon:
			Item[] items = getRootItems();
			for (int i = 0; i < items.length; i++) {
				Item item = items[i];
				item.screen = this;
				item.showNotify();
			}
			if (this.container != null) {
				this.container.showNotify();
			}
			//#ifndef polish.skipTicker
				if (this.ticker != null) {
					this.ticker.showNotify();
				}
			//#endif
			//#ifdef tmp.usingTitle
				if (this.title != null) {
					this.title.showNotify();
				}
			//#endif
			//#if tmp.ignoreTitleCall
				this.ignoreTitleCall = true;
			//#endif
			//#ifdef tmp.useExternalMenuBar
				this.menuBar.showNotify();
			//#endif
			
		
			// init components (why? Should not be necessary... 2009-02-01):
//			if (this.isInitialized) {
//				int	width = this.screenWidth - (this.marginLeft + this.marginRight);
//				int availHeight = this.screenHeight - (this.marginTop + this.marginBottom);
//				//#ifdef tmp.menuFullScreen
//					//#ifdef tmp.useExternalMenuBar
//						this.menuBar.showNotify();
//						if (!this.menuBar.isInitialized) {
//							//#if polish.css.separate-menubar
//								if (this.separateMenubar) {
//									this.menuBar.init( width, width, availHeight );								
//								} else {
//									this.menuBar.init( this.screenWidth, this.screenWidth, availHeight );								
//								}
//							//#else
//								this.menuBar.init( this.screenWidth, this.screenWidth, availHeight );								
//							//#endif
//						}
//					//#else
//						if (this.menuOpened) {
//							if (!this.menuContainer.isInitialized) {
//								this.menuContainer.init( width, width, availHeight );
//							}
//						} else
//					//#endif
//				//#endif
//				if (this.container != null && !this.container.isInitialized) {
//					this.container.init( width, width, availHeight );
//				}
//				//#ifdef tmp.usingTitle
//					if (this.title != null) {
//						if (!this.title.isInitialized) {
//							this.title.init( width, width, availHeight );
//						}
//					}
//				//#endif
//				//#ifndef polish.skipTicker
//					if (this.ticker != null) {
//						if (!this.ticker.isInitialized) {
//							this.ticker.init( width, width, availHeight );
//						}
//					}
//				//#endif
//				calculateContentArea(0, 0, this.screenWidth, this.screenHeight);
//			}
		} catch (Exception e) {
			//#debug error
			System.out.println("error while calling showNotify" + e );
		}

		// register this screen:
		//#debug
		System.out.println("registering screen at StyleSheet");
		StyleSheet.currentScreen = this;

		//#ifdef polish.Vendor.Siemens
			this.showNotifyTime = System.currentTimeMillis();
		//#endif
		//#if polish.ScreenInfo.enable
			ScreenInfo.setScreen( this );
		//#endif
		if (this.background != null) {
			this.background.showNotify();
		}
		if (this.border != null) {
			this.border.showNotify();
		}

		this.lastInteractionTime = System.currentTimeMillis();
		this.isResourcesReleased = false;
		//#if polish.Bugs.repaintInShowNotify
			// request one repaint with full dimensions:
			repaint();
		//#endif
	}
	
	/**
	 * Unregisters this screen and notifies all items that they will not be shown anymore.
	 */
	public void hideNotify() {
		//#if polish.Screen.callSuperEvents
			super.hideNotify();
		//#endif
		//#if polish.Bugs.noSoftKeyReleasedEvents
			this.triggerReleasedKeyCode = 0;
			this.triggerReleasedTime = 0;
		//#endif
		//#if polish.css.repaint-previous-screen
			//TODO when the previousScreen reference is removed, there is no way how several popus can be handled correctly
			// e.g. an input form with a TextField as a popup that allows to enter symbols in another popup.
			// Previously the symbol-popup was painted in the background after returning to the input-popup.
			// However, not removing the reference might result in "memory leaks" when references to popup-screens are hold
			// while the "parent" screen should be removed from the memory. However - we just take this risk for now.
		 	// (rob, 2007-07-09)
			// this.previousScreen = null;
			//#if !polish.Bugs.noTranslucencyWithDrawRgb && !polish.css.overlay-background
				this.previousScreenOverlayBackground = null;
			//#endif
			//#if polish.ScreenOrientationCanChange && tmp.usingTitle
				this.previousScreenTitleBackground = null;
			//#endif
		//#endif
		//#ifdef polish.Vendor.Siemens
			// Siemens sometimes calls hideNotify directly
			// after showNotify for some reason.
			// So hideNotify checks how long the screen
			// has been shown - if not long enough,
			// the call will be ignored:
			//TODO this should be handled by a bug rather than doing it for the vendor... but: Siemens is dead anyhow ;-)
			if (System.currentTimeMillis() - this.showNotifyTime < 500) {
				//#debug
				System.out.println("Ignoring hideNotify on Siemens");
				return;
			}
		//#endif
		//#debug
		System.out.println("hideNotify " + this);
		//#if !polish.css.repaint-previous-screen
			// un-register this screen:
			if (StyleSheet.currentScreen == this) {
				//#debug
				System.out.println("de-registering screen at StyleSheet");
				StyleSheet.currentScreen = null;
			}
		//#endif
		//#if tmp.handleEvents
			EventManager.fireEvent( EventManager.EVENT_HIDE,  this, null );
		//#endif
		Item[] items = getRootItems();
		for (int i = 0; i < items.length; i++) {
			Item item = items[i];
			item.hideNotify();
		}
		if (this.container != null) {
			this.container.hideNotify();
		}
		//#ifndef polish.skipTicker
			if (this.ticker != null) {
				this.ticker.hideNotify();
			}
		//#endif
		//#ifdef tmp.usingTitle
			if (this.title != null) {
				this.title.hideNotify();
			}
		//#endif
		//#ifdef tmp.ignoreTitleCall
			this.ignoreTitleCall = true;
		//#endif
		//#if polish.ScreenInfo.enable
			// de-register screen from ScreenInfo element:
			if (ScreenInfo.item.screen == this ) {
				ScreenInfo.setScreen( null );
			}
		//#endif
		if (this.background != null) {
			this.background.hideNotify();
		}
		if (this.border != null) {
			this.border.hideNotify();
		}
		//#ifdef tmp.useExternalMenuBar
			this.menuBar.hideNotify();
		//#endif
	}
	
	/**
	 * Resets the style of this screen and all its elements.
	 * This is useful when you have applied changes to this screen's style or one of its elements.
	 * @param recursive true when all subelements of this screen should reset their style as well.
	 */
	public void resetStyle(boolean recursive) {
		if (this.style != null) {
			setStyle(this.style);
		}
		if (recursive) {
			//#if tmp.usingTitle
				if (this.title != null) {
					this.title.resetStyle(recursive);
				}
			//#endif
			//#if tmp.useExternalMenuBar
				this.menuBar.resetStyle(recursive);
			//#endif
			if (this.subTitle != null) {
				this.subTitle.resetStyle(recursive);
			}
			if (this.container != null) {
				this.container.resetStyle(recursive);
			}
		}
	}
	
	/**
	 * Sets the style of this screen.
	 * 
	 * @param style the style
	 * @see #setStyle(Style, boolean)
	 */
	public void setStyle(Style style) {
		if (style != this.style && this.style != null ) {
			this.style.releaseResources();
		}
	 	//#debug
		System.out.println("Setting screen-style for " + this );
		this.style = style;
		this.background = style.background;
		this.border = style.border;
		if (this.container != null) {
			// use the same style for the container - but ignore the background, border and margin settings:
			this.container.setStyleWithBackground(style, true);
		}
		this.isLayoutVCenter = (( style.layout & Item.LAYOUT_VCENTER ) == Item.LAYOUT_VCENTER);
		this.isLayoutBottom = !this.isLayoutVCenter 
							&& (( style.layout & Item.LAYOUT_BOTTOM ) == Item.LAYOUT_BOTTOM);
		this.isLayoutCenter = (( style.layout & Item.LAYOUT_CENTER ) == Item.LAYOUT_CENTER);
		this.isLayoutRight = !this.isLayoutCenter
							&& (( style.layout & Item.LAYOUT_RIGHT ) == Item.LAYOUT_RIGHT);
		this.isLayoutHorizontalShrink = (style.layout & Item.LAYOUT_SHRINK) == Item.LAYOUT_SHRINK; 
		this.isLayoutVerticalShrink = (style.layout & Item.LAYOUT_VSHRINK) == Item.LAYOUT_VSHRINK; 
		//#if polish.css.content-background
			Background contBackground = (Background) style.getObjectProperty("content-background");
			if (contBackground != null) {
				this.contentBackground = contBackground;
			}
		//#endif
		//#if polish.css.content-border
			Border contBorder = (Border) style.getObjectProperty("content-border");
			if (contBorder != null) {
				this.contentBorder = contBorder;
			}
		//#endif
		//#if polish.css.content-bgborder
			Border contBgBorder = (Border) style.getObjectProperty("content-bgborder");
			if (contBgBorder != null) {
				this.contentBgBorder = contBgBorder;
			}
		//#endif
		//#if polish.css.scrollindicator-up-image && tmp.useScrollIndicator
			String scrollUpUrl = style.getProperty("scrollindicator-up-image");
			if (scrollUpUrl != null) {
				try {
					this.scrollIndicatorUpImage = StyleSheet.getImage(scrollUpUrl, null, true);
				} catch (IOException e) {
					//#debug error
					System.out.println("Unable to load scroll up image" + e );
				}
			} else {
				this.scrollIndicatorUpImage = null;
			}
		//#endif		
		//#if polish.css.scrollindicator-down-image && tmp.useScrollIndicator
			String scrollDownUrl = style.getProperty("scrollindicator-down-image");
			if (scrollDownUrl != null) {
				try {
					this.scrollIndicatorDownImage = StyleSheet.getImage(scrollDownUrl, null, true);
				} catch (IOException e) {
					//#debug error
					System.out.println("Unable to load scroll down image" + e );
				}
			} else {
				this.scrollIndicatorDownImage = null;
			}
		//#endif		
		//#if polish.css.scrollindicator-up-image && polish.css.scrollindicator-down-image && tmp.useScrollIndicator
			if (this.scrollIndicatorUpImage != null && this.scrollIndicatorDownImage != null) {
				int height = this.scrollIndicatorUpImage.getHeight() + this.scrollIndicatorDownImage.getHeight();
				int width = Math.max( this.scrollIndicatorUpImage.getWidth(), this.scrollIndicatorDownImage.getWidth() );
				this.scrollIndicatorWidth = width;
				this.scrollIndicatorX = this.screenWidth / 2 - width / 2;
				//#ifdef tmp.menuFullScreen
					//#ifdef tmp.useExternalMenuBar
						this.scrollIndicatorY = this.fullScreenHeight - (this.menuBar.marginBottom + 1 + height);
					//#else
						this.scrollIndicatorY = this.fullScreenHeight - (height + 1);
					//#endif					
				//#elif polish.vendor.Siemens
					// set the position of scroll indicator for Siemens devices 
					// on the left side, so that the menu-indicator is visible:
					this.scrollIndicatorWidth = width;
					this.scrollIndicatorX = 0;
					this.scrollIndicatorY = this.screenHeight - height - 1;
				//#else
					// set position of scroll indicator:
					this.scrollIndicatorWidth = width;
					this.scrollIndicatorX = this.screenWidth - width - 1;
					this.scrollIndicatorY = this.screenHeight - height - 1;
				//#endif					
			}
		//#endif

		//#if tmp.usingTitle && polish.css.title-style
			this.titleStyle = (Style) style.getObjectProperty("title-style");
			if (this.titleStyle != null && this.title != null) {
				this.title.setStyle(this.titleStyle);
				if (this.isInitialized) {
					int width = this.screenWidth - (this.marginLeft + this.marginRight);
					this.titleHeight = this.title.getItemHeight( width, width, this.screenHeight );
				}
			} else {
		//#endif
		 //#if tmp.usingTitle
		 	if (this.title != null && this.title.isInitialized) {
		 		this.title.isInitialized = false;
				if (this.isInitialized) {
					int width = this.screenWidth - (this.marginLeft + this.marginRight);
					this.titleHeight = this.title.getItemHeight( width, width, this.screenHeight );
				}
		 	}
		 //#endif
		//#if tmp.usingTitle && polish.css.title-style				
			}
		//#endif
		//#if tmp.usingTitle && polish.css.title-position
			Integer titlePositionInt = style.getIntProperty("title-position");
			if (titlePositionInt == null && this.title != null && this.title.style != null) {
				titlePositionInt = this.title.style.getIntProperty("title-position");
			}
			if (titlePositionInt != null) {
				this.paintTitleAtTop = (titlePositionInt.intValue() == POSITION_TOP);
			}
			
		//#endif
		//#if tmp.usingTitle && polish.css.separate-title
			Boolean separateTitleBool = style.getBooleanProperty("separate-title");
			if (separateTitleBool != null) {
				this.separateTitle = separateTitleBool.booleanValue();
			}
		//#endif
		//#if polish.css.subtitle-position
			Integer subtitlePositionInt = style.getIntProperty("subtitle-position");
			if (subtitlePositionInt == null && this.subTitle != null && this.subTitle.style != null) {
				subtitlePositionInt = this.subTitle.style.getIntProperty("subtitle-position");
			}
			if (subtitlePositionInt != null) {
				this.paintSubTitleAtTop = (subtitlePositionInt.intValue() == POSITION_TOP);
			}
		//#endif
		//#if tmp.useScrollBar && polish.css.scrollbar-style
			Style scrollbarStyle = (Style) style.getObjectProperty("scrollbar-style");
			if (scrollbarStyle != null) {
				this.scrollBar.setStyle( scrollbarStyle );
			}
		//#endif
		//#if tmp.useScrollBar && polish.css.scrollbar-position
			Integer scrollBarPositionInt = style.getIntProperty( "scrollbar-position" );
			if (scrollBarPositionInt == null && this.scrollBar.style != null) {
				scrollBarPositionInt = this.scrollBar.style.getIntProperty( "scrollbar-position" );
			}
			if (scrollBarPositionInt != null) {
				this.paintScrollBarOnRightSide = (scrollBarPositionInt.intValue() != POSITION_LEFT);
			}
		//#endif
		//#if tmp.useScrollBar && polish.css.show-scrollbar
			Boolean showScrollBarBool = style.getBooleanProperty("show-scrollbar");
			if (showScrollBarBool != null) {
				this.scrollBarVisible = showScrollBarBool.booleanValue();
			}
		//#endif
			

		//#ifdef polish.css.foreground-image
			String foregroundImageUrl = this.style.getProperty("foreground-image");
			if (foregroundImageUrl == null) {
				this.foregroundImage = null;
			} else {
				try {
					this.foregroundImage = StyleSheet.getImage(foregroundImageUrl, null,true);
					//#ifdef polish.css.foreground-x
						Integer xInteger = this.style.getIntProperty("foreground-x");
						if (xInteger != null) {
							this.foregroundX = xInteger.intValue();
						}
					//#endif
					//#ifdef polish.css.foreground-y
						Integer yInteger = this.style.getIntProperty("foreground-y");
						if (yInteger != null) {
							this.foregroundY = yInteger.intValue();
						}
					//#endif
				} catch (IOException e) {
					//#debug error
					System.out.println("Unable to load foreground-image [" + foregroundImageUrl + "]: " + e);
				}
			}
		//#endif
		//#if polish.css.clip-screen-info
			Boolean clipScreenInfoBool = style.getBooleanProperty( "clip-screen-info" );
			if (clipScreenInfoBool != null) {
				this.clipScreenInfo = clipScreenInfoBool.booleanValue();
			}
		//#endif	
		//#if polish.css.menubar-style  && tmp.useExternalMenuBar
			Style menuBarStyle = (Style) style.getObjectProperty("menubar-style");
			if (menuBarStyle != null) {
				this.menuBar.setStyle( menuBarStyle );
			}
		//#endif

		//#ifndef polish.skipTicker
			//#if polish.css.ticker-position
				Integer tickerPositionInt = style.getIntProperty("ticker-position");
				if (tickerPositionInt == null && this.ticker != null && this.ticker.style != null) {
					tickerPositionInt = this.ticker.style.getIntProperty("ticker-position");
				}
				if (tickerPositionInt != null) {
					this.paintTickerAtTop = (tickerPositionInt.intValue() == POSITION_TOP);
				}
			//#endif
		//#endif
		//#if polish.css.repaint-previous-screen
			Boolean repaintPreviousScreenBool = style.getBooleanProperty("repaint-previous-screen");
			if (repaintPreviousScreenBool != null) {
				this.repaintPreviousScreen = repaintPreviousScreenBool.booleanValue();
				//#if !polish.Bugs.noTranslucencyWithDrawRgb && polish.css.overlay-background
				if (this.repaintPreviousScreen) {
					Background bg = (Background) style.getObjectProperty("overlay-background");
					if (bg != null) {
						this.previousScreenOverlayBackground = bg;
					}
				}
				//#endif
				/* TODO reenable repaint-previous-screen-anchor
				//#if polish.css.repaint-previous-screen-anchor
					if (this.repaintPreviousScreen) {
						Integer repaintAnchorInt = style.getIntProperty("repaint-previous-screen-anchor");
						if (repaintAnchorInt != null) {
							this.repaintPreviousScreenAnchor = repaintAnchorInt.intValue();
						}
						
					}
				//#endif
				 * 
				 */
			}
		//#endif
		//#if polish.css.separate-menubar
			Boolean separateMenubarBool = style.getBooleanProperty("separate-menubar");
			if (separateMenubarBool != null) {
				this.separateMenubar = separateMenubarBool.booleanValue();
			}
		//#endif
		//#if polish.css.scroll-background
			Boolean scrollBackgroundBool = style.getBooleanProperty("scroll-background");
			if (scrollBackgroundBool != null) {
				this.isScrollBackground = scrollBackgroundBool.booleanValue();
			}
		//#endif
		//#if tmp.menuFullScreen
			//#if polish.css.background-bottom
				Integer backgroundBottomInt = style.getIntProperty("background-bottom");
				if (backgroundBottomInt != null) {
					this.excludeMenuBarForBackground = backgroundBottomInt.intValue() == 1; 
				}
			//#endif
			//#if polish.css.backgroundrange-bottom
				Integer backgroundRangeBottomInt = style.getIntProperty("backgroundrange-bottom");
				if (backgroundRangeBottomInt != null) {
					this.excludeMenuBarForBackground = backgroundRangeBottomInt.intValue() == 1; 
				}
			//#endif
		//#endif
		//#ifdef tmp.usingTitle
			//#if polish.css.background-top
				Integer backgroundTopInt = style.getIntProperty("background-top");
				if (backgroundTopInt != null) {
					this.excludeTitleForBackground = backgroundTopInt.intValue() == 1; 
				}
			//#endif
			//#if polish.css.backgroundrange-top
				Integer backgroundRangeTopInt = style.getIntProperty("backgroundrange-top");
				if (backgroundRangeTopInt != null) {
					this.excludeTitleForBackground = backgroundRangeTopInt.intValue() == 1; 
				}
			//#endif
		//#endif
		//#if polish.css.content-expand
			Boolean contentExpandBool = style.getBooleanProperty("content-expand");
			if(contentExpandBool != null)
			{
				this.isContentExpand = contentExpandBool.booleanValue();
			}
		//#endif
		//#if polish.css.scroll-up-background
			Background upBg = (Background) style.getObjectProperty("scroll-up-background");
			if (upBg != null) {
				this.scrollUpBackground = upBg;
			}
		//#endif
		//#if polish.css.scroll-down-background
			Background downBg = (Background) style.getObjectProperty("scroll-down-background");
			if (downBg != null) {
				this.scrollDownBackground = downBg;
			}
		//#endif
		//#if polish.css.move-scroll-backgrounds && (polish.css.scroll-up-background || polish.css.scroll-down-background)
			Boolean moveBool = style.getBooleanProperty("move-scroll-backgrounds");
			if (moveBool != null) {
				this.isMoveScrollBackgrounds = moveBool.booleanValue();
			}
		//#endif
		//#if polish.css.portrait-style || polish.css.landscape-style
			//#if polish.css.landscape-style
				Style lsStyle = (Style) style.getObjectProperty("landscape-style");
				if (lsStyle != null) {
					this.landscapeStyle = lsStyle;
					this.portraitStyle = style;
				}
			//#endif
			//#if polish.css.portrait-style
				Style ptStyle = (Style) style.getObjectProperty("portrait-style");
				if (ptStyle != null) {
					if (this.landscapeStyle == null) {
						this.landscapeStyle = style;
					}
					this.portraitStyle = ptStyle;
				}
			//#endif
		//#endif
		
			
		setStyle( style, true );
	}
	
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.UiElement#setStyle(de.enough.polish.ui.Style, boolean)
	 */
	public void setStyle(Style style, boolean resetStyle)
	{
		if(!resetStyle && this.isInitialized) {
			Dimension value;
			//#if polish.css.margin
				value = (Dimension) style.getObjectProperty("margin");
				if (value != null) {
					int margin = value.getValue(this.screenWidth);
					this.marginLeft = margin;
					this.marginRight = margin;
					this.marginTop = margin;
					this.marginBottom = margin;
				}
			//#endif
			//#if polish.css.margin-left
				value = (Dimension) style.getObjectProperty("margin-left");
				if (value != null) {
					this.marginLeft = value.getValue(this.screenWidth);
				}
			//#endif
			//#if polish.css.mragin-right
				value = (Dimension) style.getObjectProperty("margin-right");
				if (value != null) {
					this.marginRight = value.getValue(this.screenWidth);
				}
			//#endif
			//#if polish.css.margin-top
				value = (Dimension) style.getObjectProperty("margin-top");
				if (value != null) {
					this.marginTop = value.getValue(this.screenHeight);
				}
			//#endif
			//#if polish.css.margin-bottom
				value = (Dimension) style.getObjectProperty("margin-bottom");
				if (value != null) {
					this.marginBottom = value.getValue(this.screenHeight);
				}
			//#endif
		}
		//#if polish.css.content-background
			//#if polish.css.content-background-width
				Dimension contBackgroundW = (Dimension) style.getObjectProperty("content-background-width");
				if (contBackgroundW != null) {
					this.contentBackgroundWidth = contBackgroundW;
				}
			//#endif
			//#if polish.css.content-background-height
				Dimension contBackgroundH = (Dimension) style.getObjectProperty("content-background-height");
				if (contBackgroundH != null) {
					this.contentBackgroundHeight = contBackgroundH;
				}
			//#endif
			//#if polish.css.content-background-anchor
				Integer contBackgroundA = (Integer) style.getObjectProperty("content-background-anchor");
				if (contBackgroundA != null) {
					this.contentBackgroundAnchor = contBackgroundA.intValue();
				}
			//#endif
		//#endif
		//#if polish.css.scrollindicator-color && tmp.useScrollIndicator
			Integer scrollIndicatorColorInt = style.getIntProperty( "scrollindicator-color" );
			if (scrollIndicatorColorInt != null) {
				this.scrollIndicatorColor = scrollIndicatorColorInt.intValue();
			}
		//#endif
		//#if polish.css.animations
			if (!resetStyle) {
				if (this.container != null) {
					this.container.setStyle(style, resetStyle);
				}
				if (this.background != null) {
					this.background.setStyle(style);
				}
				if (this.border != null) {
					this.border.setStyle(style);
				}
			}
		//#endif
	}
	
	//#if tmp.useTitleMenu
	boolean hasTitleMenu(Style screenStyle)
	{
		Boolean titleMenuBool = screenStyle.getBooleanProperty("title-menu");
		if(titleMenuBool != null)
		{
			return titleMenuBool.booleanValue();
		}
		
		return false;
	}
	//#endif
	
	/**
	 * Animates this screen.
	 * Subclasses can override this method to create animations.
	 * All embedded items are also animated.
	 * 
	 * @param currentTime the current time in milliseconds
	 * @param repaintRegion the repaint area that needs to be updated when this item is animated
	 */
	public void animate( long currentTime, ClippingRegion repaintRegion) {
		//#if polish.useNativeGui
			if (this.nativeScreen != null) {
				this.nativeScreen.animate(currentTime, repaintRegion);
			}
		//#endif
		if (!this.isInitialized) {
			return;
		}
		
		this.lastAnimateTime = currentTime;
		
		synchronized (this.paintLock) {
			try {
				//#if tmp.useScrollBar
					int scrollYOffset = 0;
					if (this.container != null) {
						scrollYOffset = this.container.yOffset;
					}
				//#endif
				// for ensured backward compatibility call the standard animate method:
				if (animate()) {
					//#ifdef tmp.menuFullScreen
						repaintRegion.addRegion( 0,0, this.screenWidth, this.fullScreenHeight );
					//#else
						repaintRegion.addRegion( 0,0, this.screenWidth, this.screenHeight );
					//#endif
				}
				if (this.background != null) {
					this.background.animate( this, null, currentTime, repaintRegion );
				}
				if (this.border != null) {
					this.border.animate( this, null, currentTime, repaintRegion );
				}
				//#if polish.css.content-background
					if (this.contentBackground != null) {
						this.contentBackground.animate(this, null, currentTime, repaintRegion);
					}
				//#endif
				//#if polish.css.content-border
					if (this.contentBorder != null) {
						this.contentBorder.animate(this, null, currentTime, repaintRegion);
					}
				//#endif
				boolean isMenuOpen = isMenuOpened();
				//#ifdef tmp.menuFullScreen
					//#ifdef tmp.useExternalMenuBar
						this.menuBar.animate( currentTime, repaintRegion );
					//#else
						if (isMenuOpen) {
							this.menuContainer.animate( currentTime, repaintRegion );
						}
					//#endif
				//#endif
				if (this.container != null && !isMenuOpen) {
					this.container.animate( currentTime, repaintRegion );
				}
				//#ifdef tmp.usingTitle
					if (this.title != null) {
						this.title.animate( currentTime, repaintRegion );
					}
				//#endif
				//#if polish.ScreenInfo.enable
					if (ScreenInfo.item != null && ScreenInfo.isVisible()) {
						ScreenInfo.item.animate( currentTime, repaintRegion );
					}
				//#endif
				//#if tmp.useScrollBar
					this.scrollBar.animate(currentTime, repaintRegion);
					if (this.container != null) {
						if (this.container.yOffset != scrollYOffset) {
							// scrollbar area needs to be added:
							repaintRegion.addRegion(this.scrollBar.relativeX, this.scrollBar.relativeY, this.scrollBar.itemWidth, this.scrollBar.itemHeight);
						}
					}
				//#endif
				//#if polish.Bugs.noSoftKeyReleasedEvents
					int keyCode = this.triggerReleasedKeyCode;
					long time = this.triggerReleasedTime;
					
					//#if polish.Screen.releaseInterval:defined
						//#= int interval = ${polish.Screen.releaseInterval};
					//#else
						int interval = 400;
					//#endif
					
					if (time != 0 && keyCode != 0 && (currentTime - time) > interval) {
						keyReleased(keyCode);
					}
				//#endif
			} catch (Exception e) {
				//#debug error
				System.out.println("animate(currentTime, repaintRegion) threw an exception" + e );
			}
		}	
	}

	
	/**
	 * Animates this Screen.
	 * It's recommended to use animate( long, ClippingRegion ) instead for performance reasons.
	 * 
	 * @return true when at least one animated item needs a redraw/repaint.
	 * @see #animate(long, ClippingRegion)
	 */
	public boolean animate() {
		return false;
	}
	
	//#if polish.enableSmileySupport
	//# public void paint(javax.microedition.lcdui.Graphics g) {
	//#	de.enough.polish.ui.smiley.Graphics graphics = new de.enough.polish.ui.smiley.Graphics( g );
	//#	paint (graphics);
	//# }
	//#endif
	
	/**
	 * Paints the screen.
	 * When you subclass Screen you should override paintScreen(Graphics g) instead, if possible.
	 * 
	 * @param g the graphics context.
	 * @see #paintScreen(Graphics)
	 */
	public void paint(Graphics g) {
		if (this.isResourcesReleased) {
			return;
		}
		//#if polish.Screen.callSuperEvents
			//# super.paint(g);
		//#endif

		//System.out.println("..paint");
		//System.out.println("Painting screen "+ this + ", background == null: " + (this.background == null) + ", clipping=" + g.getClipX() + "/" + g.getClipY() + " - " + g.getClipWidth() + "/" + g.getClipHeight() );
		if (!this.isInitialized) {
			init( getScreenFullWidth(), getScreenFullHeight() );
		} else  if (this.isInitRequested) {
			calculateContentArea(0, 0, this.screenWidth, this.screenHeight);
			this.isInitRequested = false;
		}

		synchronized (this.paintLock ) {
			//#if polish.Bugs.losesFullScreen
				super.setFullScreenMode( true );
			//#endif
			//#if polish.debug.error
			try {
			//#endif
				//#if polish.css.repaint-previous-screen
					if (this.repaintPreviousScreen && this.previousScreen != null) {
						this.previousScreen.paint(g);
						//#if !polish.Bugs.noTranslucencyWithDrawRgb
							if (this.previousScreenOverlayBackground != null) {
								int height;
								//#if tmp.menuFullScreen
									height = this.fullScreenHeight;
								//#else
									height = this.screenHeight;
								//#endif
								this.previousScreenOverlayBackground.paint(0, 0, this.screenWidth, height, g);
							}
						//#endif
						//#if polish.ScreenOrientationCanChange && tmp.usingTitle
							if ( this.previousScreenTitleBackground != null ) {
								this.previousScreenTitleBackground.paint(0, 0, this.screenWidth, this.previousScreenTitleHeight, g );
							}
						//#endif
					}
				//#endif
//				int borderWidthL = 0;
//				int borderWidthR = 0;
//				int borderWidthT = 0;
//				int borderWidthB = 0;
//				if (this.border != null) {
//					borderWidthL = this.border.borderWidthLeft;
//					borderWidthR = this.border.borderWidthRight;
//					borderWidthT = this.border.borderWidthTop;
//					borderWidthB = this.border.borderWidthBottom;
//				}
//				int sWidth = this.screenWidth - this.marginLeft - this.marginRight - borderWidthL - borderWidthR;
//				int leftBorder = this.marginLeft + borderWidthL;
//				int rightBorder = leftBorder + sWidth;
//				int sHeight;
//				//#ifdef tmp.menuFullScreen
//					sHeight = this.fullScreenHeight - this.marginTop - this.marginBottom - borderWidthT - borderWidthB;
//				//#else
//					sHeight = this.screenHeight - this.marginTop - this.marginBottom- borderWidthT - borderWidthB;
//				//#endif
//				int topBorder = this.marginTop + borderWidthT;
//				//#if tmp.useExternalMenuBar
//					int space;
//					//#if polish.MenuBar.Position:defined
//						space = this.menuBar.getSpaceLeft( this.screenWidth, this.fullScreenHeight );
//						leftBorder += space;
//						sWidth -= space;
//						rightBorder -= space;
//						space = this.menuBar.getSpaceRight( this.screenWidth, this.fullScreenHeight );
//						sWidth -= space;
//						rightBorder -= space;
//						space = this.menuBar.getSpaceTop( this.screenWidth, this.fullScreenHeight );
//						topBorder += space;
//						sHeight -= space;
//					//#endif
//					space = this.menuBar.getSpaceBottom( this.screenWidth, this.fullScreenHeight );
//					sHeight -= space;
//				//#endif		
//				if (this.isLayoutHorizontalShrink) {
//					int contWidth = this.contentWidth;
//					if (this.container != null) {
//						contWidth = this.container.getItemWidth(sWidth, sWidth, sHeight); 
//					}
//					sWidth = contWidth;
//					//System.out.println("is horizontal shrink - from sWidth=" + (this.screenWidth - this.marginLeft - this.marginRight) + ", to=" + sWidth );					
//					if (this.isLayoutRight) {
//						leftBorder = rightBorder - sWidth;
//					} else if (this.isLayoutCenter) {
//						int diff = (rightBorder - leftBorder - sWidth) >> 1;
//						leftBorder += diff;
//						rightBorder -= diff;
////						leftBorder = (this.screenWidth - sWidth) >> 1;
////						rightBorder = this.screenWidth - leftBorder;
//					} else { // layout left:
//						rightBorder = leftBorder + sWidth;
////						rightBorder = this.screenWidth - sWidth;
//					}
//					//System.out.println("leftBorder=" + leftBorder + ", rightBorder=" + rightBorder );
//				}
//				
//				if (this.isLayoutVerticalShrink) {
//					int contHeight = this.contentHeight;
//					if (this.container != null) {
//						int height = this.container.getItemHeight(sWidth, sWidth, sHeight);
//						if (height < contHeight) {
//							contHeight = height;  
//						}
//					}
////					//#if tmp.menuFullScreen
////						sHeight = contHeight + this.titleHeight + this.menuBarHeight;
////					//#else
//						sHeight = contHeight + this.titleHeight;
////					//#endif
//					//System.out.println("isLayoutVerticalShrink - sHeight: from=" + (this.fullScreenHeight - this.marginTop - this.marginBottom) + ", to=" + sHeight + ", contentHeight=" + this.contentHeight + ", topBorder=" + topBorder );
//					if (this.isLayoutBottom) {
////						//#ifdef tmp.menuFullScreen
////							topBorder = this.fullScreenHeight - (this.marginBottom + sHeight + 1);
////						//#else
//							topBorder = this.screenHeight - (this.marginBottom + sHeight + 1);
////						//#endif
//						//System.out.println("bottom -> topBorder=" + topBorder + ", contY=>" + (topBorder + this.titleHeight) );
//					} else if (this.isLayoutVCenter) {
////						//#ifdef tmp.menuFullScreen
////							topBorder = (this.fullScreenHeight - (this.marginBottom + this.marginBottom))/2 - sHeight/2;
////						//#else
//							topBorder = (this.screenHeight - (this.marginBottom + this.marginTop + sHeight)) >> 1;
////						//#endif						 
//						//System.out.println("vcenter -> topBorder=" + topBorder + ", contY=>" + (topBorder + this.titleHeight) );
//					}
//				}

//				int clipY = g.getClipY();
				// paint background:
//				int backgroundHeight = sHeight; // - (borderWidth << 1);
//				int backgroundY = topBorder;
//				//#ifdef tmp.menuFullScreen
//					if (!this.excludeMenuBarForBackground && this.marginBottom == 0 && !this.isLayoutVerticalShrink) {
//						//#if tmp.useExternalMenuBar
//							backgroundHeight += this.menuBar.getSpaceBottom( this.screenWidth, this.fullScreenHeight );
//						//#else
//							backgroundHeight += this.menuBarHeight;
//						//#endif
//					}
//				//#endif
//				//#ifdef tmp.usingTitle
//					if (this.excludeTitleForBackground) {
//						backgroundHeight -= this.titleHeight;
//						backgroundY += this.titleHeight;
//					}
//				//#endif
				paintBackgroundAndBorder( g );
//				
//				int topHeight = topBorder;
				paintTitleAndSubtitle(g);
				//#ifndef polish.skipTicker
					Item tick = this.ticker;
					if (tick != null) {
						tick.paint( tick.relativeX, tick.relativeY, 0, this.screenWidth, g );
					}
				//#endif
	
				//System.out.println("topHeight=" + topHeight + ", contentY=" + contentY);
					
				
				// paint content:
				paintScreen( g );
	//				g.setColor( 0xff0000 );
	//				g.drawRect(g.getClipX() + 1 , g.getClipY() + 1 , g.getClipWidth() - 2 ,  g.getClipHeight() - 2);
					
				paintScrollBar(g);

//				g.setColor( 0x88ff88 );
//				g.drawRect( this.container.relativeX, this.container.relativeY + 1, this.container.itemWidth, this.container.itemHeight - 2 );
//				g.setColor( 0xff7788 );
//				g.drawRect( this.contentX, this.contentY, this.contentWidth, this.contentHeight );

//				//#ifdef tmp.menuFullScreen
//				 	g.setClip(0, 0, this.screenWidth, this.fullScreenHeight );
//				//#else
//				 	g.setClip(0, 0, this.screenWidth, this.originalScreenHeight );
//				//#endif
				 
//				 // remove test code
//				 g.setColor( 0x00ff00 );
//				 g.drawRect( leftBorder, topHeight, sWidth, this.screenHeight - topHeight  );
//				 g.drawRect( leftBorder + 1, topHeight + 1, sWidth - 2, this.screenHeight - topHeight -2 );
//				 g.drawLine( leftBorder, topHeight, leftBorder + sWidth, this.screenHeight );
//				 g.drawString( this.screenWidth + "x" + this.screenHeight, 60, 30, Graphics.TOP | Graphics.LEFT );
//				 g.setColor( 0xff0000 );
//				 g.drawRect( this.contentX, this.contentY, this.contentWidth, this.contentHeight );
//				 g.drawRect( this.contentX + 1, this.contentY + 1, this.contentWidth - 2, this.contentHeight -2 );
//				 g.drawLine( this.contentX, this.contentY, this.contentX + this.contentWidth, this.contentY + this.contentHeight );
//				 g.drawString( this.contentWidth+ "x" + this.contentHeight , 60, 60, Graphics.TOP | Graphics.LEFT );
				 
				// paint info element:
				if (this.infoItem != null) {			
					this.infoItem.paint( this.infoItem.relativeX, this.infoItem.relativeY, this.marginLeft, this.screenWidth - this.marginRight, g );
				}
	 	
				//#if polish.ScreenInfo.enable
					ScreenInfo.paint( g, this.titleHeight, this.screenWidth );
				//#endif
				
				paintMenuBar(g);
				//#ifdef polish.css.foreground-image
					if (this.foregroundImage != null) {
						g.drawImage( this.foregroundImage, this.foregroundX, this.foregroundY, Graphics.TOP | Graphics.LEFT  );
					}
				//#endif
					
//					g.setColor( 0xff);
//					g.drawRect( g.getClipX(), g.getClipY(), g.getClipWidth() - 2, g.getClipHeight() - 2 );
//					g.setColor(0xffff00);
//					g.drawRect( this.container.relativeX, this.container.relativeY, this.contentWidth, this.contentHeight );
			//#if polish.debug.error
			} catch (RuntimeException e) {
				//#debug error
				System.out.println( "unable to paint screen (" + getClass().getName() + "):" + e );
			}
			//#endif
		}

//			g.setColor( 0xff0000 );
//			g.drawString( "key =" + this.triggerReleasedKeyCode, 20, 30, 0 );
//			g.drawString( "time=" + this.triggerReleasedTime, 20, 55, 0 );
//			g.drawString( "trig=" + this.hasBeenTriggered, 20, 80, 0 );
//			g.drawString( "rej=" + this.rejectedKeyCode, 20, 105, 0 );
//		g.setColor( 0xff0000 );
//		if (this.lastSizeChangedEvent != null) {
//			g.drawString( this.lastSizeChangedEvent, 5, 10, 0 );
//		}
//		g.drawString( Integer.toString( MasterCanvas.getScreenHeight() ), 5, 25, 0);
	}

	/**
	 * Paints the scrollbar of this screen.
	 * Note: from J2ME Polish 2.2 this functionality will move into Item.
	 * @param g the Graphics context
	 */
	protected void paintScrollBar(Graphics g) {
		//#if tmp.useScrollBar
			if (this.container != null && this.container.itemHeight > this.contentHeight) {
				int rightBorder = this.screenWidth - this.marginRight;
				// paint scroll bar: - this.container.yOffset
				// #debug
				// System.out.println("Screen/ScrollBar: container.contentY=" + this.container.contentY + ", container.internalY=" +  this.container.internalY + ", container.yOffset=" + this.container.yOffset + ", container.height=" + this.container.availableHeight + ", container.relativeY=" + this.container.relativeY);
				
				int scrollX =  rightBorder
							- this.scrollBar.initScrollBar(this.screenWidth, this.contentHeight, this.container.itemHeight, this.container.yOffset, this.container.internalY, this.container.internalHeight, this.container.focusedIndex, this.container.size() );
				//TODO allow scroll bar on the left side
				this.scrollBar.relativeX = scrollX;
				this.scrollBar.relativeY = this.container.relativeY;
				//#if polish.css.show-scrollbar
					if ( this.scrollBarVisible ) {
				//#endif
						this.scrollBar.paint( scrollX , this.scrollBar.relativeY, scrollX, rightBorder, g);
				//#if polish.css.show-scrollbar
					}
				//#endif
				//g.setColor( 0x00ff00 );
				//g.drawRect( this.contentX, this.contentY, this.contentWidth - 3, this.contentHeight );
				//System.out.println("scrollbar: width=" + scrollBar.itemWidth + ", backgroundWidth=" + scrollBar.backgroundWidth + ", height=" + scrollBar.itemHeight + ", backgroundHeight=" + scrollBar.backgroundHeight + ", contentY=" + contentY + ", contentHeight=" + this.contentHeight );
			}
		//#endif
	}

	/**
	 * Paints the menubar, if in fullscreen mode.
	 * Also scroll indicators are painted here, in case no scrollbar is being used
	 * @param g the graphics context
	 */
	protected void paintMenuBar(Graphics g) {
		int leftBorder = this.marginLeft;
		Border bord = this.border;
		if (bord != null) {
			leftBorder += bord.borderWidthLeft;
		}
		int menuLeftX = 0;
		int menuRightX = this.screenWidth;
		int menuY = this.screenHeight; // + this.marginBottom;
		//#if polish.css.separate-menubar
			if (!this.separateMenubar) {
				menuLeftX = leftBorder;
				menuRightX = this.screenWidth - this.marginRight;
				if (bord != null) {
					menuRightX -= bord.borderWidthRight;
				}
				menuY = this.screenHeight - this.marginBottom;
			}
		//#endif
		//#ifdef tmp.menuFullScreen
			// paint menu in full-screen mode:
			
			//#ifdef tmp.useExternalMenuBar
//				//#if polish.MenuBar.Position == right
//					menuLeftX = menuRightX - this.menuBar.getItemWidth( this.screenWidth, this.screenWidth, this.screenHeight ) - 1;
//					menuY = 0;
//				//#endif

				//#if tmp.useTitleMenu
				if(this.hasTitleMenu)
				{
					Item t = this.title;
					t.paint( t.relativeX, t.relativeY, t.relativeX, t.relativeX + t.itemWidth, g);
				}
				else
				//#endif
				{
					Item mb  = this.menuBar;
					//#if (polish.MenuBar.Position == invisible) || (polish.blackberry && (polish.BlackBerry.useStandardMenuBar != true))
						//#define tmp.useInvisibleMenuBar
					//#else
						menuRightX = mb.relativeX + mb.itemWidth;
					//#endif
					mb.paint( mb.relativeX, mb.relativeY, mb.relativeX, menuRightX, g );
					//this.menuBar.paint(menuLeftX, menuY, menuLeftX, menuRightX, g);
				}
				
				//#if tmp.useScrollIndicator
					if (this.menuBar.isOpened) {
						this.paintScrollIndicator = this.menuBar.paintScrollIndicator;
						this.paintScrollIndicatorUp = this.menuBar.canScrollUpwards;
						this.paintScrollIndicatorDown = this.menuBar.canScrollDownwards;
					}
				//#endif
			//#else
				int topHeight = this.contentY;
				if (this.menuOpened) {
					topHeight -= this.infoHeight;
					int menuHeight = this.menuContainer.getItemHeight(this.screenWidth, this.screenWidth, this.screenHeight);
					int y = this.originalScreenHeight - (menuHeight + 1);
					if (y < topHeight) {
						//#if tmp.useScrollIndicator
						this.paintScrollIndicator = true;
						this.paintScrollIndicatorUp = (this.menuContainer.yOffset != 0);
						this.paintScrollIndicatorDown = ( (this.menuContainer.focusedIndex != this.menuContainer.size() - 1)
								&& (this.menuContainer.yOffset + menuHeight > this.originalScreenHeight - topHeight)) ;
						//#endif
						y = topHeight; 
					//#if tmp.useScrollIndicator
					} else {
						this.paintScrollIndicator = false;
					//#endif
					}
					this.menuContainer.setScrollHeight( this.originalScreenHeight - y );
					// set clip so that submenu items do know where they should be painted.
					// ATTENTION: this leads to situations in which items are repainted that
					// need no refresh...
					g.setClip(0, topHeight, this.screenWidth, this.originalScreenHeight - topHeight );
					this.menuContainer.paint(menuLeftX, y, menuLeftX, menuLeftX + this.screenWidth, g);
					this.menuContainer.relativeY = y;
					this.menuContainer.relativeX = menuLeftX;
				 	g.setClip(0, 0, this.screenWidth, this.fullScreenHeight );
				} 
				//#if !polish.doja
					if (this.showTitleOrMenu || this.menuOpened) {
						// clear menu-bar:
						if (this.menuBarColor != Item.TRANSPARENT) {
							g.setColor( this.menuBarColor );
							//TODO check use menuY instead of this.originalScreenHeight?
							g.fillRect(menuLeftX, menuY, menuRightX,  this.menuBarHeight );
						}
						g.setColor( this.menuFontColor );
						g.setFont( this.menuFont );
						String menuText = this.menuLeftString;
						if (menuText != null) {
							//#ifdef polish.MenuBar.MarginLeft:defined
								//#= menuLeftX += ${polish.MenuBar.MarginLeft};
							//#elifdef polish.MenuBar.PaddingLeft:defined
								//#= menuLeftX += ${polish.MenuBar.PaddingLeft};
							//#else
								menuLeftX += 2;
							//#endif
							//#ifdef polish.MenuBar.MarginTop:defined
								//#= g.drawString(menuText, menuX, this.originalScreenHeight + ${polish.MenuBar.MarginTop}, Graphics.TOP | Graphics.LEFT );
							//#else
								g.drawString(menuText, menuLeftX, this.originalScreenHeight + 2, Graphics.TOP | Graphics.LEFT );
							//#endif
							
						}
						menuText = this.menuRightString;
						if (menuText != null) {
							//#ifdef polish.MenuBar.MarginRight:defined
								//#= menuRightX -= ${polish.MenuBar.MarginRight};
							//#elifdef polish.MenuBar.PaddingRight:defined
								//#= menuRightX -= ${polish.MenuBar.PaddingRight};
							//#elifdef polish.MenuBar.MarginLeft:defined
								menuRightX -= 2;
							//#endif

							//#ifdef polish.MenuBar.MarginTop:defined
								//#= g.drawString(menuText, menuRightX, this.originalScreenHeight + ${polish.MenuBar.MarginTop}, Graphics.TOP | Graphics.RIGHT );
							//#else
								g.drawString(menuText, menuRightX, this.originalScreenHeight + 2, Graphics.TOP | Graphics.RIGHT );
							//#endif
							//#ifdef polish.hasPointerEvents
								this.menuRightCommandX = menuRightX - this.menuFont.stringWidth( menuText );
							//#endif
						}
					} // if this.showTitleOrMenu || this.menuOpened
				//#endif
			//#endif
		//#endif
				
		//#if tmp.useScrollIndicator
			// paint scroll-indicator in the middle of the menu:					
			if (this.paintScrollIndicator) {
				g.setColor( this.scrollIndicatorColor );
				int x = this.scrollIndicatorX;
				int y = this.scrollIndicatorY;
				//System.out.println("paint: this.scrollIndicatorY=" + this.scrollIndicatorY);
				int width = this.scrollIndicatorWidth;
				int halfWidth = width / 2;
				if (this.paintScrollIndicatorUp) {
					//#if polish.css.scrollindicator-up-image
						if (this.scrollIndicatorUpImage != null) {
							g.drawImage(this.scrollIndicatorUpImage, x, y, Graphics.LEFT | Graphics.TOP );
						} else {
					//#endif						
						//#ifdef polish.midp2
							g.fillTriangle(x, y + halfWidth-1, x + width, y + halfWidth-1, x + halfWidth, y );
						//#else
							g.drawLine( x, y + halfWidth-1, x + width, y + halfWidth-1 );
							g.drawLine( x, y + halfWidth-1, x + halfWidth, y );
							g.drawLine( x + width, y + halfWidth-1, x + halfWidth, y );
						//#endif
					//#if polish.css.scrollindicator-up-image
						}
					//#endif
				}
				if (this.paintScrollIndicatorDown) {
					//#if polish.css.scrollindicator-down-image
						if (this.scrollIndicatorDownImage != null) {
							//#if polish.css.scrollindicator-down-image
								if (this.scrollIndicatorUpImage != null) {
									y += this.scrollIndicatorUpImage.getHeight() + 1;
								} else {
									y += halfWidth;
								}
							//#else
								y += halfWidth;
							//#endif
							g.drawImage(this.scrollIndicatorDownImage, x, y, Graphics.LEFT | Graphics.TOP );
						} else {
					//#endif						
						//#ifdef polish.midp2
							g.fillTriangle(x, y + halfWidth+1, x + width, y + halfWidth+1, x + halfWidth, y + width );
						//#else
							g.drawLine( x, y + halfWidth+1, x + width, y + halfWidth+1 );
							g.drawLine( x, y + halfWidth+1, x + halfWidth, y + width );
							g.drawLine(x + width, y + halfWidth+1, x + halfWidth, y + width );
						//#endif
					//#if polish.css.scrollindicator-down-image
						}
					//#endif
				}
			}
		//#endif
	}

	/**
	 * Paints the title (if in fullscreen mode) and the subtitle of this screen
	 * @param g the graphics context
	 */
	protected void paintTitleAndSubtitle(Graphics g) {
		//#ifdef tmp.usingTitle
			// paint title:
			Item t = this.title;
			if (t != null && this.showTitleOrMenu
				//#if tmp.useTitleMenu
					&& !this.hasTitleMenu 
				//#endif
				)
			{
				int clipY = g.getClipY();
				if (clipY < t.relativeY + t.itemHeight) {
					// since most of the cases the title is at the top, this will remove most unecessary paint actions:
					t.paint( t.relativeX, t.relativeY, t.relativeX, t.relativeX + t.itemWidth, g);
				}
			}
		//#endif
		Item st = this.subTitle;
		if (st != null) {
			st.paint( st.relativeX, st.relativeY, st.relativeX, st.relativeX + st.itemWidth, g );
		}
	}

	/**
	 * Paints the background and border for this screen.
	 * @param g the graphics context
	 */
	protected void paintBackgroundAndBorder(Graphics g) {
		if (this.background != null) {
			//#if polish.css.scroll-background
				if (this.isScrollBackground && this.container != null) {
					 int bgHeight = Math.max( this.backgroundHeight, this.container.itemHeight);
					 int bgY = this.backgroundY + this.container.yOffset;
					 if (bgY + bgHeight < this.backgroundY + this.backgroundHeight) {
						 bgHeight += (this.backgroundY + this.backgroundHeight) - (bgY + bgHeight); 
					 }
					 int clipY = g.getClipY();
					 int clipX = g.getClipX();
					 int clipWidth = g.getClipWidth();
					 int clipHeight = g.getClipHeight();
					 g.clipRect( clipX, this.backgroundY, clipWidth, this.backgroundHeight );
					 this.background.paint(this.backgroundX, bgY, this.backgroundWidth, bgHeight, g);
					 g.setClip( clipX, clipY, clipWidth, clipHeight );
				} else {
			//#endif
					this.background.paint(this.backgroundX, this.backgroundY, this.backgroundWidth, this.backgroundHeight, g);
			//#if polish.css.scroll-background
				}
			//#endif
			//#if polish.css.scroll-up-background
				if (this.scrollUpBackground != null && canScrollUp()) {
					int bgY = this.backgroundY;
					//#if polish.css.move-scroll-backgrounds
						if (this.isMoveScrollBackgrounds) {
					//#endif
							int off = getScrollUpBackgroundOffset();
							if (off < this.backgroundHeight) {
								int yAdjust = ((this.backgroundHeight - off) * 5) / 100;
								bgY -= yAdjust;
							}
					//#if polish.css.move-scroll-backgrounds
						}
					//#endif
					this.scrollUpBackground.paint(this.backgroundX, bgY, this.backgroundWidth, this.backgroundHeight, g);
				}
			//#endif
			//#if polish.css.scroll-down-background
				if (this.scrollDownBackground != null && canScrollDown()) {
					int bgY = this.backgroundY;
					//#if polish.css.move-scroll-backgrounds
						if (this.isMoveScrollBackgrounds) {
					//#endif
							int off = getScrollDownBackgroundOffset();
							if (off < this.backgroundHeight) {
								int yAdjust = ((this.backgroundHeight - off) * 5) / 100;
								bgY += yAdjust;
							}
					//#if polish.css.move-scroll-backgrounds
						}
					//#endif
					this.scrollDownBackground.paint(this.backgroundX, bgY, this.backgroundWidth, this.backgroundHeight, g);
				}
			//#endif
		} else {
			//#if polish.css.repaint-previous-screen
				if (!this.repaintPreviousScreen) {
			//#endif
					g.setColor( 0xFFFFFF );
					g.fillRect( this.backgroundX, this.backgroundY, this.backgroundWidth, this.backgroundHeight );
			//#if polish.css.repaint-previous-screen
				}
			//#endif
		}
		Border bord = this.border;
		if (bord != null) {
			int x = this.backgroundX - bord.borderWidthLeft;
			int y = this.backgroundY - bord.borderWidthTop;
			int width = this.backgroundWidth + bord.borderWidthLeft + bord.borderWidthRight;
			if (this.marginRight != 0 || this.isLayoutHorizontalShrink) {
				width++;
			}
			int height = this.backgroundHeight + bord.borderWidthTop + bord.borderWidthBottom;
			bord.paint( x, y, width, height, g );
		}
	}

	/**
	 * Checks if this screen can currently scroll downwards.
	 * @return true when scrolling down is possible.
	 */
	protected boolean canScrollDown() {
		Container cont = this.container;
		if (cont != null && cont.itemHeight + cont.yOffset > this.backgroundHeight ) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if this screen can currently scroll upwards.
	 * @return true when scrolling up is possible.
	 */
	protected boolean canScrollUp() {
		Container cont = this.container;
		if (cont != null && cont.yOffset != 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * Retrieves the scroll down offset
	 * @return the offset in pixels until the bottom of the scrollable area, 0 if the bottom is reached, otherwise a positive number of pixels.
	 */
	protected int getScrollDownBackgroundOffset() {
		Container cont = this.container;
		if (cont != null && cont.itemHeight + cont.yOffset > this.backgroundHeight ) {
			return cont.itemHeight + cont.yOffset - this.backgroundHeight;
		}
		return 0;
	}

	/**
	 * Retrieves the scroll up offset
	 * @return the offset in pixels until the top of the scrollable area, 0 if the top is reached, otherwise a positive number of pixels.
	 */
	protected int getScrollUpBackgroundOffset() {
		Container cont = this.container;
		if (cont != null) {
			return -cont.yOffset;
		}
		return 0;
	}

	/**
	 * Paints the screen.
	 * This method also needs to set the protected variables
	 * paintScrollIndicator, paintScrollIndicatorUp and paintScrollIndicatorDown.
	 * 
	 * @param g the graphics on which the screen should be painted
	 * @see #contentX
	 * @see #contentY
	 * @see #contentWidth
	 * @see #contentHeight
	 * @see #paintScrollIndicator
	 * @see #paintScrollIndicatorUp
	 * @see #paintScrollIndicatorDown
	 */
	protected void paintScreen( Graphics g ) {
		Container cont = this.container;
		if (cont == null || cont.size() == 0) {
			return;
		}
		if (!cont.isInitialized()) {
			calculateContentArea(0, 0, this.screenWidth, this.screenHeight );
		}
		int clipX = g.getClipX();
		int clipY = g.getClipY();
		int clipWidth = g.getClipWidth();
		int clipHeight = g.getClipHeight();
		
		int y = this.contentY;
		int x = this.contentX;
		int height = this.contentHeight;
		int width = this.contentWidth;
		
		//#if polish.css.content-expand
			if(this.isContentExpand)
			{
				//#if tmp.fullscreen
					height = this.fullScreenHeight - this.contentY;
				//#else
					height = this.screenHeight - this.contentY;
				//#endif
			}
		//#endif
		
//		g.setColor( 0x00ff00 );
//		g.drawRect( x, y, width, height);
//		g.setColor( 0xff0000 );
//		g.drawLine( (int) (this.screenWidth * 0.05), this.contentY, this.screenWidth - (int)(this.screenWidth * 0.05), this.contentY + this.contentHeight );
		// initialising the container here prevents clipping problems on BlackBerry for some
		// screens like FilteredList. Why this makes a difference is beyond me, but well... it works.
		int containerHeight = cont.itemHeight;
//		if (cont.itemHeight > height) {
//			int sbw = getScrollBarWidth();
//			containerHeight = cont.getItemHeight( width - sbw, width - sbw, height);
//		} else {
//			containerHeight = cont.getItemHeight( width, width, height);
//		}

		g.clipRect(x, y, width, height );
		
		if ( g.getClipHeight() > 0 ) {
			
			//#if tmp.useScrollIndicator
				this.paintScrollIndicator = false; // defaults to false
			//#endif
			if (containerHeight > cont.availableHeight ) {
				//#if tmp.useScrollIndicator
					this.paintScrollIndicator = true;
					this.paintScrollIndicatorUp = (cont.yOffset != 0);
						//&& (this.container.focusedIndex != 0);
					this.paintScrollIndicatorDown = (  
							( (cont.focusedIndex != cont.size() - 1) 
									|| (cont.focusedItem != null && cont.focusedItem.itemHeight > cont.availableHeight) )
							 && (cont.getScrollYOffset() + containerHeight > cont.availableHeight) );
				//#endif
//			} else if (this.isLayoutVCenter) {
//				/*
//				//#debug
//				System.out.println("Screen: adjusting y from [" + y + "] to [" + ( y + (height - containerHeight) / 2) + "] - containerHeight=" + containerHeight);
//				*/
//				y += ((height - containerHeight) >> 1);
//				//this.container.availableHeight = height - ((height - containerHeight) >> 1);
//			} else if (this.isLayoutBottom) {
//				y += (height - containerHeight);
//				//this.container.availableHeight = containerHeight;
//	//			System.out.println("content: y=" + y + ", contentY=" + this.contentY + ", contentHeight="+ this.contentHeight + ", containerHeight=" + containerHeight);
			}
//			int containerWidth = this.container.itemWidth;
//			if (this.isLayoutCenter) {
//				int diff = (width - containerWidth) >> 1;
//				x += diff;
//				width -= (width - containerWidth);
//			} else if (this.isLayoutRight) {
//				int diff = width - containerWidth;
//				x += diff;
//				width -= diff;
//			}
//			this.container.relativeX = this.contentX;
//			//TODO  move this positioning to calculcateContentArea or initScreen
//			this.container.relativeY = y;
			//System.out.println("screen: content: x=" + x + ", rightBorder=" + (x + width) );
			//System.out.println("content: y=" + y + ", bottom=" + (y + height) );
			//#if polish.css.content-bgborder || polish.css.content-background || polish.css.content-border
				int bX = cont.relativeX;
				int bY = cont.relativeY;
				int bW = cont.itemWidth;
				int bH = cont.itemHeight; 
				//#if (polish.css.content-background-width || polish.css.content-background-height)
					if (this.contentBackgroundWidth != null) {
						int prev = bW;
						bW = this.contentBackgroundWidth.getValue(bW);
						if ((this.contentBackgroundAnchor & Graphics.HCENTER) == Graphics.HCENTER) {
							bX -= (bW - prev) >> 1;
						} else if ((this.contentBackgroundAnchor & Graphics.LEFT) == Graphics.LEFT) {
							bX -= (bW - prev);
						}
					}
					if (this.contentBackgroundHeight != null) {
						bH = this.contentBackgroundHeight.getValue(bH);
						if ((this.contentBackgroundAnchor & Graphics.VCENTER) == Graphics.VCENTER) {
							bY -= (bH - containerHeight) >> 1;
						} else if ((this.contentBackgroundAnchor & Graphics.TOP) == Graphics.TOP) {
							bY -= (bH - containerHeight);
						}
					}
				//#endif
				//#if polish.css.content-bgborder
					if (this.contentBgBorder != null ) {
						this.contentBgBorder.paint( bX, bY, bW, bH, g);
					}
				//#endif
				//#if polish.css.content-background
					if (this.contentBackground != null ) {
						this.contentBackground.paint( bX, bY, bW, bH, g);
					}
				//#endif
				//#if polish.css.content-border
					if (this.contentBorder != null ) {
						this.contentBorder.paint( bX, bY, bW, bH, g);
					}
				//#endif
			//#endif
			cont.paint( cont.relativeX, cont.relativeY, cont.relativeX, cont.relativeX + cont.itemWidth, g);
//			this.container.paint( x, y, x, x + width, g );
//			g.setColor(0x0000ff);
//			g.drawRect( x, y, containerWidth, containerHeight);
//			g.setColor(0x0000ff00);
//			g.drawRect( this.container.getAbsoluteX(), this.container.getAbsoluteY(), this.container.itemWidth, this.container.itemHeight);
//			g.setColor(0x00ff00);
//			g.drawString( Integer.toString(g.getClipHeight()), x, y, Graphics.TOP | Graphics.LEFT );
//		} else {
//			g.setClip( clipX, clipY, clipWidth, clipHeight );
//			g.setColor(0x00ff00);
//			g.fillRect( clipX, clipY, 10, 10 );
		}
		
		// allow painting outside of the content area again:
		g.setClip( clipX, clipY, clipWidth, clipHeight );
	}
	
	//#ifdef tmp.usingTitle
	/**
	 * Gets the title of the Screen. 
	 * Returns null if there is no title.
	 * 
	 * @return the title of this screen
	 */
	public String getTitle()
	{
		//#if polish.Bugs.getTitleRequiresNull
			//# return null;
		//#else
			if (this.title == null) {
				return null;
			} else if ( !(this.title instanceof StringItem ) ) {
				return null;
			} else {
				return ((StringItem)this.title).getText();
			}
		//#endif
	}
	//#endif

	//#ifdef tmp.usingTitle
	/**
	 * Sets the title of the Screen. If null is given, removes the title.
	 * 
	 * If the Screen is physically visible, the visible effect
	 * should take place no later than immediately
	 * after the callback or
	 * <A HREF="../../../javax/microedition/midlet/MIDlet.html#startApp()"><CODE>startApp</CODE></A>
	 * returns back to the implementation.
	 * 
	 * @param s - the new title, or null for no title
	 */
	public void setTitle( String s)
	{
		setTitle( s, null );
	}
	//#endif
	
	
	/**
	 * Sets the title of the Screen. If null is given, removes the title.
	 * 
	 * If the Screen is physically visible, the visible effect
	 * should take place no later than immediately
	 * after the callback or
	 * <A HREF="../../../javax/microedition/midlet/MIDlet.html#startApp()"><CODE>startApp</CODE></A>
	 * returns back to the implementation.
	 * 
	 * @param text the new title, or null for no title
	 * @param tStyle the new style for the title, is ignored when null
	 */
	public void setTitle( String text, Style tStyle)
	{
		//#if !tmp.usingTitle
			setTitle(text);
		//#else
			//#debug
			System.out.println("Setting title " + text );
			//#ifdef tmp.ignoreTitleCall
				if (text == null) {
					if (this.ignoreTitleCall) {
						this.ignoreTitleCall = false;
						return;
					}
					//return;
				}
			//#endif
			if (text != null) {
				//#if tmp.useTitleMenu
					if(this.hasTitleMenu)
					{
						((TitleMenuBar)this.menuBar).setTitle(text, tStyle);
					}
					else
				//#endif
					{
						if (this.title == null || !(this.title instanceof StringItem)) {
							//#if polish.classes.title:defined
								//#style title, default
								//#= this.title = new ${polish.classes.title}( null, text );
							//#else
								//#style title, default
								this.title = new StringItem( null, text );
							//#endif
							this.title.screen = this;
							//#ifdef polish.css.title-style
								if (this.titleStyle != null) {
									this.title.setStyle( this.titleStyle );
								}
							//#endif
						} else {
							((StringItem)this.title).setText( text );
						}
						if ( tStyle != null ) {
							this.title.setStyle( tStyle );
						}
					}
				// the Nokia 6600 has an amazing bug - when trying to refer the
				// field screenWidth, it returns 0 in setTitle(). Obviously this works
				// in other phones and in the simulator, but not on the Nokia 6600.
				// That's why hardcoded values are used here. 
				// The name of the field does not matter by the way. This is 
				// a very interesting behaviour and should be analysed
				// at some point...
	//			//#ifdef polish.ScreenWidth:defined
	//				//#= int width = ${polish.ScreenWidth}  - (this.marginLeft + this.marginRight);
	//			//#else
	//				int width = this.screenWidth - (this.marginLeft + this.marginRight);
	//			//#endif
	//			this.titleHeight = this.title.getItemHeight( width, width );			
			} else {
				//#if tmp.useTitleMenu
				if(!this.hasTitleMenu)
				{
				//#endif
					this.title = null;
					this.titleHeight = 0;
				//#if tmp.useTitleMenu
				}
				//#endif
			}
			if (this.isInitialized && super.isShown()) {
				if (this.title != null) {
					int width = this.screenWidth - this.marginLeft - this.marginRight;
					if (this.border != null) {
						width -= this.border.borderWidthLeft + this.border.borderWidthRight;
					}
					this.titleHeight = this.title.getItemHeight( width, width, this.screenHeight );
				}
				calculateContentArea( 0, 0, this.screenWidth, this.screenHeight );
				//this.isInitialized = false;
				requestRepaint();
			}
		//#endif
	}
	
	/**
	 * Sets an Item as the title for this screen.
	 * WARNING: You must not call setTitle(String) after calling this method anymore!
	 * 
	 * @param item the title Item 
	 */
	public void setTitle(Item item) {
		//#ifdef tmp.usingTitle
		setTitle( item, null );
		//#endif
	}
	

	/**
	 * Sets an Item as the title for this screen.
	 * WARNING: You must not call setTitle(String) after calling this method anymore!
	 * 
	 * @param item the title Item 
	 * @param tStyle the new style for the title, is ignored when null
	 */
	public void setTitle(Item item, Style tStyle) {
		//#ifdef tmp.usingTitle
			//#if tmp.useTitleMenu
				if(this.hasTitleMenu && item != this.menuBar)
				{
					((TitleMenuBar)this.menuBar).setTitle(item, tStyle);
				}
				else
			//#endif
			{
				this.title = item;
				if (item != null){
					//#ifdef polish.ScreenWidth:defined
						//#= int width = ${polish.ScreenWidth}  - (this.marginLeft + this.marginRight);
					//#else
						int width = this.screenWidth - (this.marginLeft + this.marginRight);
					//#endif
					if (width > 1) {
						this.titleHeight = this.title.getItemHeight( width, width, this.screenHeight );
					}
					item.screen = this;
				} else {
					this.titleHeight = 0;
				}				
				if (this.isInitialized || super.isShown()) {
					calculateContentArea( 0, 0, this.screenWidth, this.screenHeight );
					requestRepaint();
				}
			}
		//#endif
	}
	
	/**
	 * Retrieves this screen's title item (when the fullscreen mode of J2ME Polish is activated)
	 * 
	 * @return the title of this screen as an item
	 */
	public Item getTitleItem()
	{
		Item item = null;
		//#ifdef tmp.usingTitle
			item = this.title;
		//#endif
		return item;
	}




	/**
	 * Sets the information which should be shown to the user.
	 * The info is shown below the title (if any) and can be designed
	 * using the predefined style "info".
	 * At the moment this feature is only used by the TextField implementation,
	 * when the direct input mode is enabled.
	 * A repaint is not triggered automatically by calling this method.
	 * 
	 * @param infoText the text which will be shown to the user
	 */
	public void setInfo( String infoText ) {
		//#debug
		System.out.println("setInfoString=" + infoText);
		if (infoText == null) {
			this.infoItem = null;
		} else if (this.infoItem == null || !(this.infoItem instanceof StringItem)) {
			//#style info, default
			this.infoItem = new StringItem( null, infoText );
			this.infoItem.screen = this;
		} else if (this.isInitialized){
			StringItem info = ((StringItem)this.infoItem); 
			info.setText( infoText );
			int leftX = this.marginLeft + (this.border != null ? this.border.borderWidthLeft : 0 );
			int width = this.screenWidth - leftX - this.marginRight - (this.border != null ? this.border.borderWidthRight : 0 );
			int ih = info.getItemHeight(width, width, this.contentHeight);
			if (ih == this.infoHeight) {
				info.relativeX = leftX;
				int iw = info.itemWidth;
				if (iw < width) {
					if (info.isLayoutRight) {
						info.relativeX = leftX + width - iw;
					} else if (info.isLayoutCenter) {
						info.relativeX = leftX + (width - iw)/2;
					}
				}
				return;
			}
		}
		setInfo( this.infoItem );
	}

	/**
	 * Sets the information which should be shown to the user.
	 * The info is shown below the title (if any) and can be designed
	 * using the predefined style "info".
	 * At the moment this feature is only used by the TextField implementation,
	 * when the direct input mode is enabled.
	 * A repaint is not triggered automatically by calling this method.
	 * 
	 * @param info the info item which will be shown to the user
	 */
	public void setInfo( Item info ) {
		//#debug
		System.out.println("setInfoItem=" + info + ", current=" + this.infoItem + ", screen.isInitialized=" + this.isInitialized);
		this.infoItem = info; 
		if (info == null) {
			this.infoHeight = 0;
		} 
		this.isInitRequested = this.isInitialized;
	}
	//#ifndef polish.skipTicker
	/**
	 * Set a ticker for use with this Screen, replacing any previous ticker.
	 * If null, removes the ticker object
	 * from this screen. The same ticker is may be shared by several Screen
	 * objects within an application. This is done by calling setTicker() on
	 * different screens with the same Ticker object.
	 * If the Screen is physically visible, the visible effect
	 * should take place no later than immediately
	 * after the callback or
	 * <CODE>startApp</CODE>
	 * returns back to the implementation.
	 * 
	 * @param ticker - the ticker object used on this screen
	 */
	public void setPolishTicker( Ticker ticker)
	{
		setPolishTicker( ticker, null );
	}
	//#endif
	
	//#ifndef polish.skipTicker
	/**
	 * Set a ticker for use with this Screen, replacing any previous ticker.
	 * If null, removes the ticker object
	 * from this screen. The same ticker is may be shared by several Screen
	 * objects within an application. This is done by calling setTicker() on
	 * different screens with the same Ticker object.
	 * If the Screen is physically visible, the visible effect
	 * should take place no later than immediately
	 * after the callback or
	 * <CODE>startApp</CODE>
	 * returns back to the implementation.
	 * 
	 * @param ticker the ticker object used on this screen
	 * @param tickerStyle the style of the ticker
	 */
	public void setPolishTicker( Ticker ticker, Style tickerStyle )
	{
		//#debug
		System.out.println("setting ticker " + ticker);
		if (this.ticker != null) {
			this.ticker.hideNotify();
		}
		this.ticker = ticker;
		if (ticker != null) {
			if (tickerStyle != null) {
				ticker.setStyle(tickerStyle);
			}
			ticker.screen = this;
		}
		//System.out.println("setTicker(): screenHeight=" + this.screenHeight + ", original=" + this.originalScreenHeight );
		if (super.isShown()) {
			calculateContentArea( 0, 0, this.screenWidth, this.screenHeight );
			if (ticker != null) {
				ticker.showNotify();
			}
			requestRepaint();
		}
	}
	//#endif
	
	//#ifndef polish.skipTicker
	/**
	 * Gets the ticker used by this Screen.
	 * 
	 * @return ticker object used, or null if no ticker is present
	 */
	public Ticker getPolishTicker()
	{
		return this.ticker;
	}
	//#endif
	
	/**
	 * Gets the states of the physical game keys.  
	 * Each bit in the returned
	 * integer represents a specific key on the device.  A key's bit will be
	 * 1 if the key is currently down or has been pressed at least once since
	 * the last time this method was called.  The bit will be 0 if the key
	 * is currently up and has not been pressed at all since the last time
	 * this method was called.  This latching behavior ensures that a rapid
	 * key press and release will always be caught by an application loop,
	 * regardless of how slowly the loop runs.
	 * <p>
	 * For example:
	 * <code>
	 * <pre>
	 * 
	 * // Get the key state and store it
	 * int keyState = getKeyStates();
	 * if ((keyState & UiAccess.LEFT_KEY) != 0) {
	 * 		positionX--;
	 * }
	 * else if ((keyState & UiAccess.RIGHT_KEY) != 0) {
	 * 		positionX++;
	 * }
	 * 
	 * </pre>
	 * </code>
	 * <p>
	 * Calling this method has the side effect of clearing any latched state.
	 * Another call to getKeyStates immediately after a prior call will
	 * therefore report the system's best idea of the current state of the
	 * keys, the latched bits having been cleared by the first call.
	 * <p>
	 * On J2ME Polish this method is implemented by monitoring key press and
	 * release events.  Thus the state reported by getKeyStates might
	 * lag the actual state of the physical keys since the timeliness
	 * of the key information is be subject to the capabilities of each
	 * device.  Also, some devices may be incapable of detecting simultaneous
	 * presses of multiple keys.
	 * <p>
	 * This method returns 0 unless the GameCanvas is currently visible as
	 * reported by <A HREF="../../../../javax/microedition/lcdui/Displayable.html#isShown()"><CODE>Displayable.isShown()</CODE></A>.
	 * Upon becoming visible, a GameCanvas will initially indicate that
	 * all keys are unpressed (0); if a key is held down while the GameCanvas
	 * is being shown, the key must be first released and then pressed in
	 * order for the key press to be reported by the GameCanvas.
	 * <p>
	 * 
	 * @return An integer containing the key state information (one bit per  key), or 0 if the GameCanvas is not currently shown.
	 * @see UiAccess#UP_PRESSED
	 * @see UiAccess#DOWN_PRESSED
	 * @see UiAccess#LEFT_PRESSED
	 * @see UiAccess#RIGHT_PRESSED
	 * @see UiAccess#FIRE_PRESSED
	 * @see UiAccess#GAME_A_PRESSED
	 * @see UiAccess#GAME_B_PRESSED
	 * @see UiAccess#GAME_C_PRESSED
	 * @see UiAccess#GAME_D_PRESSED
	 */
	public int getKeyStates()
	{
		
		int states = this.keyStates;
	    this.keyStates &= ~this.releasedKeys;
	    this.releasedKeys = 0;
	    return states;
	}


	/**
	 * Handles key events.
	 * 
	 * WARNING: When this method should be overwritten, one need
	 * to ensure that super.keyPressed( int ) is called!
	 * 
	 * @param keyCode The code of the pressed key
	 * @return true when the event was handled/consumed
	 */
	public boolean _keyPressed(int keyCode) {
		//#if polish.Screen.callSuperEvents
			super._keyPressed(keyCode);
		//#else
			keyPressed(keyCode); // for backwards compatibility
		//#endif
		boolean processed = false;
		this.lastInteractionTime = System.currentTimeMillis();
		synchronized (this.paintLock) {
			//#if polish.Bugs.noSoftKeyReleasedEvents
				this.triggerReleasedKeyCode = 0;
			//#endif
			try {
				this.ignoreRepaintRequests = true;
				//#debug
				System.out.println("keyPressed: [" + keyCode + "].");
				int gameAction = getGameAction(keyCode);
				//#if tmp.trackKeyUsage
					this.keyPressedProcessed = true;
				//#endif
				if (gameAction != 0) {
					int bit = 1 << gameAction;
					this.keyStates |= bit;
			        this.releasedKeys &= ~bit;
				}

				//#if tmp.menuFullScreen
					boolean letTheMenuBarProcessKey;
					//#ifdef tmp.useExternalMenuBar
						letTheMenuBarProcessKey = this.menuBar.isOpened;
					//#else
						letTheMenuBarProcessKey = this.menuOpened;
					//#endif
					//#if polish.Bugs.SoftKeyMappedToFire
						if (gameAction == FIRE && isSoftKey(keyCode, gameAction) 
						) {
							letTheMenuBarProcessKey = true;
							gameAction = 0;
						}
					//#endif
					if (!letTheMenuBarProcessKey) {
				//#endif
						processed = handleKeyPressed(keyCode, gameAction);
				//#if tmp.menuFullScreen
					}
				//#endif
				
				//#ifdef polish.debug.debug
					if (!processed) {
						//#debug
						System.out.println("unable to handle key [" + keyCode + "].");
					}
				//#endif
				//#if tmp.menuFullScreen
					if (!processed) {
						//#ifdef tmp.useExternalMenuBar
							if (this.menuBar.handleKeyPressed(keyCode, gameAction)) {
								//#if polish.Bugs.noSoftKeyReleasedEvents
									if (isSoftKeyLeft(keyCode, gameAction) || isSoftKeyRight(keyCode, gameAction)) {
										this.triggerReleasedKeyCode = keyCode;
										this.triggerReleasedTime = System.currentTimeMillis();
									}
								//#endif
								repaint();
								return true;
							}
							if (this.menuBar.isSoftKeyPressed) {
								//System.out.println("menubar detected softkey " + keyCode );
								//#if tmp.trackKeyUsage
									this.keyPressedProcessed = false;
								//#endif
								return false;
							}
							//System.out.println("menubar did not handle " + keyCode );
						//#else
							// internal menubar is used:
							if ( isSoftKeyLeft(keyCode, gameAction) ) {
								//#if polish.Bugs.noSoftKeyReleasedEvents
									this.triggerReleasedKeyCode = keyCode;
									this.triggerReleasedTime = System.currentTimeMillis();
								//#endif
								if ( this.menuSingleLeftCommand != null) {
									callCommandListener( this.menuSingleLeftCommand );
									return true;
								} else {
									if (!this.menuOpened 
											&& this.menuContainer != null 
											&&  this.menuContainer.size() != 0 ) 
									{
										openMenu( true );
										repaint();
										return true;
									} else {
										gameAction = Canvas.FIRE;
										//#if polish.Bugs.SoftKeyMappedToFire
											keyCode = 0;
										//#endif
									}
								}
							} else if ( isSoftKeyRight(keyCode, gameAction)) {
								//#if polish.Bugs.noSoftKeyReleasedEvents
									this.triggerReleasedKeyCode = keyCode;
									this.triggerReleasedTime = System.currentTimeMillis();
								//#endif
								if (!this.menuOpened && this.menuSingleRightCommand != null) {
									callCommandListener( this.menuSingleRightCommand );
									repaint();
									return true;
								}
							}
							boolean doReturn = false;
							if ( isSoftKeyLeft(keyCode, gameAction) || isSoftKeyRight(keyCode, gameAction)) {
								//#if tmp.trackKeyUsage
									this.keyPressedProcessed = false;
								//#endif
								doReturn = true;
							}
							if (this.menuOpened) {
								if ( isSoftKeyRight(keyCode, gameAction) ) {
									int selectedIndex = this.menuContainer.getFocusedIndex();
									if (!this.menuContainer.handleKeyPressed(0, LEFT)
											|| selectedIndex != this.menuContainer.getFocusedIndex() ) 
									{
										openMenu( false );
									}
		//						} else if ( gameAction == Canvas.FIRE ) {
		//							int focusedIndex = this.menuContainer.getFocusedIndex();
		//							Command cmd = (Command) this.menuCommands.get( focusedIndex );
		//							this.menuOpened = false;
		//							callCommandListener( cmd );
								} else { 
									processed = this.menuContainer.handleKeyPressed(keyCode, gameAction);
									//#if polish.key.ReturnKey:defined && polish.key.ReturnKey != polish.key.ClearKey
									if ((!processed)
										//#= && ( (keyCode == ${polish.key.ReturnKey}) && this.menuOpened ) 
									) {
										openMenu( false );
									}
									//#endif
								}
								repaint();
								return true;
							}
							if (doReturn) {
								return false;
							}
						//#endif
					}
				//#endif
				//#if (polish.Screen.FireTriggersOkCommand == true) && tmp.menuFullScreen
					if (gameAction == FIRE && keyCode != Canvas.KEY_NUM5 && this.okCommand != null) {
						callCommandListener(this.okCommand);
						return true;
					}
				//#endif
				//#if polish.key.ReturnKey:defined && tmp.trackKeyUsage
					if (!processed) {
						int backKey = 0;
						//#= backKey = ${polish.key.ReturnKey};
						if ( (keyCode == backKey) && this.backCommand != null) {
							// this is handled in keyReleased():
							processed = true;
						}
					}
				//#endif

				//#if tmp.trackKeyUsage
					this.keyPressedProcessed = processed;
				//#endif
				if (processed || this.isRepaintRequested) {
					this.isRepaintRequested = false;
					//#if tmp.useScrollBar
						if (gameAction == Canvas.UP || gameAction == Canvas.DOWN) {
							this.scrollBar.resetAnimation();
						}
					//#endif
					notifyScreenStateChanged();
					repaint();
					processed = true;
				}
			//#if polish.debug.eror
			} catch (Exception e) {
				//#debug error
				System.out.println("keyPressed() threw an exception" + e );
			//#endif
			} finally {
				this.ignoreRepaintRequests = false;
				this.isScreenChangeDirtyFlag = false;
			}
			return processed;
		}
	}
	
	
	/**
	 * Just maps the event to the the keyPressed method.
	 * 
	 * @param keyCode the code of the key, which is pressed repeatedly
	 * @return true when the event was handled/consumed
	 */
	public boolean _keyRepeated(int keyCode) {
		//#if polish.Screen.callSuperEvents
			super._keyRepeated(keyCode);
		//#else
			keyRepeated(keyCode); // for backwards compatibility
		//#endif
		//#if polish.Bugs.noSoftKeyReleasedEvents
			if (keyCode == this.triggerReleasedKeyCode && this.triggerReleasedTime == 0) {
				return false;
			}
		//#endif
		//synchronized (this.paintLock) {
		try {
			this.ignoreRepaintRequests = true;
			//#debug
			System.out.println("keyRepeated(" + keyCode + ")");
			this.lastInteractionTime = System.currentTimeMillis();
			int gameAction = getGameAction( keyCode );
			//#if tmp.menuFullScreen
				//#ifdef tmp.useExternalMenuBar
					if (this.menuBar.handleKeyRepeated(keyCode, gameAction)) {
						repaint();
						return true;
					} else if (this.menuBar.isOpened) {
						return true;
					}
				//#else
					if (this.menuOpened  && this.menuContainer != null ) {
						if (this.menuContainer.handleKeyRepeated(keyCode, gameAction)) {
							repaint();
						}
						return true;
					}
	
				//#endif
			//#endif
			boolean handled = handleKeyRepeated( keyCode, gameAction );
			if ( handled  || this.isRepaintRequested ) {
				this.isRepaintRequested = false;
				repaint();
				return true;
			}
		//}
		} finally {
			this.ignoreRepaintRequests = false;
		}
		return false;
	}

	/**
	 * Is called when a key is released.
	 * 
	 * @param keyCode the code of the key, which has been released
	 * @return true when the event was handled/consumed
	 */
	public boolean _keyReleased(int keyCode) {
		//#if polish.Screen.callSuperEvents
			super._keyReleased(keyCode);
		//#else
			keyReleased(keyCode); // for backwards compatibility
		//#endif
		boolean processed = false;
		try {
			synchronized (this.paintLock) {
				//#if polish.Bugs.noSoftKeyReleasedEvents
					if (keyCode == this.triggerReleasedKeyCode) {
						if (this.triggerReleasedTime == 0) {
							this.triggerReleasedKeyCode = 0;
							return true;
						} else {
							this.triggerReleasedTime = 0;
						}
					}
				//#endif
				this.ignoreRepaintRequests = true;
				//#debug
				System.out.println("keyReleased(" + keyCode + ")");
				this.lastInteractionTime = System.currentTimeMillis();
				int gameAction = getGameAction( keyCode );
				if (gameAction != 0) {
					this.releasedKeys |= 1 << gameAction;
				}
				//#if tmp.menuFullScreen
					if (isMenuOpened()) { // || (isSoftKeyLeft(keyCode, gameAction)) || (isSoftKeyRight(keyCode, gameAction))) {
						//#ifdef tmp.useExternalMenuBar
							processed = this.menuBar.handleKeyReleased(keyCode, gameAction);
							if (processed) {
								repaint();
							}
							//# return true;
						//#else
							if (this.menuOpened  && this.menuContainer != null ) {
								if ( isSoftKeyLeft(keyCode, gameAction)) {
									gameAction = FIRE;
									//#if polish.Bugs.SoftKeyMappedToFire
										keyCode = 0;
									//#endif
								}
								processed = this.menuContainer.handleKeyReleased(keyCode, gameAction);
								if (processed) {
									repaint();
								}
								return true;
							}
			
						//#endif
					}
				//#endif
				processed = handleKeyReleased( keyCode, gameAction );
				//#if tmp.menuFullScreen
					if (!processed && !isMenuOpened()) {
						//#ifdef tmp.useExternalMenuBar
							processed = this.menuBar.handleKeyReleased(keyCode, gameAction);
						//#endif		
					}
				//#endif
				//#if tmp.menuFullScreen
					// only trigger the OK command with the main FIRE key (keyCode == getKeyCode(FIRE))
					if (!processed && gameAction == FIRE && keyCode != KEY_NUM5 && this.okCommand != null && !isMenuOpened() && this.container.isFocused) {
						callCommandListener(this.okCommand);
						processed = true;
					} 
					//#if tmp.triggerCancelCommand
						int clearKey =
						//#if polish.key.ClearKey:defined
							//#= ${polish.key.ClearKey};
						//#else
							-8;
						//#endif
						if (!processed && keyCode == clearKey && this.cancelCommand != null) {
							callCommandListener(this.cancelCommand);
							processed = true;
						}
					//#endif
				//#endif
				//#if polish.key.ReturnKey:defined
					if (!processed) {
						int backKey = 0;
						//#= backKey = ${polish.key.ReturnKey};
						if ( (keyCode == backKey)) {
							Command cmd = this.backCommand;
							//#ifdef tmp.useExternalMenuBar
								if (cmd == null && this.menuBar.size() == 1 && this.menuBar.getCommand(0).getCommandType() == Command.OK ) {
									cmd = this.menuBar.getCommand(0);
								}
							//#endif
							if (cmd != null) {
								//#debug
								System.out.println("keyPressed: invoking commandListener for " + cmd.getLabel() );
								callCommandListener( cmd );
								processed = true;
							}
						}
					}
				//#endif
				//#debug
				System.out.println("keyReleased handled=" + processed);
				if ( processed  || this.isRepaintRequested) {
					this.isRepaintRequested = false;
					repaint();
					processed = true;
				}
			}
		} finally {
			this.ignoreRepaintRequests = false;
			//#if tmp.trackKeyUsage
				this.keyReleasedProcessed = processed;
			//#endif

		}
		return processed;
	}

	//#ifdef polish.useDynamicStyles	
	/**
	 * Retrieves the CSS selector for this screen.
	 * The CSS selector is used for the dynamic assignment of styles -
	 * that is the styles are assigned by the usage of the screen and
	 * not by a predefined style-name.
	 * With the #style preprocessing command styles are set in a static way, this method
	 * yields in a faster GUI and is recommended. When in a style-sheet
	 * dynamic styles are used, e.g. "form>p", than the selector of the
	 * screen is needed.
	 * This abstract method needs only be implemented, when dynamic styles
	 * are used: #ifdef polish.useDynamicStyles
	 * 
	 * @return the name of the appropriate CSS Selector for this screen.
	 */
	protected abstract String createCssSelector();	
	//#endif
	
	/**
	 * Retrieves all root-items of this screen.
	 * The root items are those in first hierarchy, in a Form this is 
	 * a Container for example.
	 * The default implementation does return an empty array, since apart
	 * from the container no additional items are used.
	 * Subclasses which use more root items than the container needs
	 * to override this method.
	 * 
	 * @return the root items an array, the array can be empty but not null.
	 */
	protected Item[] getRootItems() {
		return new Item[0];
	}
	
	/**
	 * Handles the key-pressed event.
	 * Please note, that implementation should first try to handle the
	 * given key-code, before the game-action is processed.
	 * 
	 * @param keyCode the code of the pressed key, e.g. Canvas.KEY_NUM2
	 * @param gameAction the corresponding game-action, e.g. Canvas.UP
	 * @return true when the key-event was processed
	 */
	protected boolean handleKeyPressed( int keyCode, int gameAction ) {
		if (this.container == null) {
			return false;
		}
		return this.container.handleKeyPressed(keyCode, gameAction);
	}
	
	/**
	 * Handles the key-repeated event.
	 * Please note, that implementation should first try to handle the
	 * given key-code, before the game-action is processed.
	 * 
	 * @param keyCode the code of the repeated key, e.g. Canvas.KEY_NUM2
	 * @param gameAction the corresponding game-action, e.g. Canvas.UP
	 * @return true when the key-event was processed
	 */
	protected boolean handleKeyRepeated( int keyCode, int gameAction ) {
		if (this.container == null) {
			return false;
		}
		return this.container.handleKeyRepeated(keyCode, gameAction);
	}
	
	/**
	 * Handles the key-released event.
	 * Please note, that implementation should first try to handle the
	 * given key-code, before the game-action is processed.
	 * 
	 * @param keyCode the code of the released key, e.g. Canvas.KEY_NUM2
	 * @param gameAction the corresponding game-action, e.g. Canvas.UP
	 * @return true when the key-event was processed
	 */
	protected boolean handleKeyReleased( int keyCode, int gameAction ) {
		if (this.container == null) {
			return false;
		}
		return this.container.handleKeyReleased(keyCode, gameAction);
	}

	
	/**
	 * Sets the screen listener for this screen.
	 * 
	 * @param listener the listener that is notified whenever the user changes the internal state of this screen.
	 */
	public void setScreenStateListener( ScreenStateListener listener ) {
		this.screenStateListener = listener;
	}
	
	/**
	 * Notifies the screen state change listener about a change in this screen.
	 */
	public void notifyScreenStateChanged() {
		if (this.screenStateListener != null && !this.isScreenChangeDirtyFlag) {
			this.isScreenChangeDirtyFlag = true;
			this.screenStateListener.screenStateChanged( this );
		}
	}

	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Displayable#setCommandListener(javax.microedition.lcdui.CommandListener)
	 */
	public void setCommandListener(CommandListener listener) {
		this.realCommandListener = listener;
	}
	
	//#if polish.LibraryBuild
		/**
		 * Sets the command listener for this screen
		 * 
		 * @param listener the listener
		 */
		public void setCommandListener(javax.microedition.lcdui.CommandListener listener) {
			// ignore
		}
	//#endif

	
	/**
	 * Retrieves the asscociated command listener of this screen (if any).
	 * 
	 * @return the command listener or null when none has been registered before.
	 */
	public CommandListener getCommandListener() {
		return this.realCommandListener;
	}

	//#if tmp.menuFullScreen && !tmp.useExternalMenuBar
	private void updateMenuTexts() {
		//#debug
		System.out.println("updating menu-command texts");
		String left = null;
		String right = null;
		int menuLeftX = 0;
		int menuRightX = this.screenWidth;
		//#if polish.css.separate-menubar
			if (!this.separateMenubar) {
				menuLeftX = this.marginLeft;
				menuRightX -= this.marginRight;
			}
		//#endif
		if (this.menuContainer != null && this.menuContainer.size() > 0) {
			String menuText = null;
			if (this.menuOpened) {
				//#ifdef polish.i18n.useDynamicTranslations
					menuText = Locale.get( "polish.command.select" ); 
				//#elifdef polish.command.select:defined
					//#= menuText = "${polish.command.select}";
				//#else
					menuText = "Select";
				//#endif
			} else {
				if (this.menuSingleLeftCommand != null) {
					menuText = this.menuSingleLeftCommand.getLabel();
				} else {
					//#ifdef polish.i18n.useDynamicTranslations
						menuText = Locale.get( "polish.command.options" ); 
					//#elifdef polish.command.options:defined
						//#= menuText = "${polish.command.options}";
					//#else
						menuText = "Options";				
					//#endif
				}
			}
			left = menuText;
			//#ifdef polish.hasPointerEvents
				if (this.menuFont != null) {
					this.menuLeftCommandX = menuLeftX + this.menuFont.stringWidth( menuText );
				}
			//#endif
			if ( this.menuOpened ) {
				// set cancel string:
				//#ifdef polish.i18n.useDynamicTranslations
					menuText = Locale.get( "polish.command.cancel" ); 
				//#elifdef polish.command.cancel:defined
					//#= menuText = "${polish.command.cancel}";
				//#else
					menuText = "Cancel";
				//#endif
				//#ifdef polish.MenuBar.MarginRight:defined
					//#= menuRightX -= ${polish.MenuBar.MarginRight};
				//#elifdef polish.MenuBar.PaddingRight:defined
					//#= menuRightX -= ${polish.MenuBar.PaddingRight};
				//#elifdef polish.MenuBar.MarginLeft:defined
					menuRightX -= 2;
				//#endif
				right = menuText;
			}
		}
		if (this.menuSingleRightCommand != null && !this.menuOpened) {
			String menuText = this.menuSingleRightCommand.getLabel();
			//#ifdef polish.MenuBar.MarginRight:defined
				//#= menuRightX -= ${polish.MenuBar.MarginRight};
			//#elifdef polish.MenuBar.PaddingRight:defined
				//#= menuRightX -= ${polish.MenuBar.PaddingRight};
			//#elifdef polish.MenuBar.MarginLeft:defined
				menuRightX -= 2;
			//#endif
			//#ifdef polish.hasPointerEvents
				if (this.menuFont != null) {
					this.menuRightCommandX = menuRightX - this.menuFont.stringWidth( menuText );
				}
			//#endif
			right = menuText;
		}
		this.menuLeftString = left;
		this.menuRightString = right;
		//#if polish.doja
			Frame frame = (Frame) ((Object)this);
			frame.setSoftLabel( Frame.SOFT_KEY_1, left );
			frame.setSoftLabel( Frame.SOFT_KEY_2, right );
		//#endif
	}
	//#endif
	
	//#if tmp.menuFullScreen && !tmp.useExternalMenuBar
	private void openMenu( boolean open ) {
		if (!open && this.menuOpened) {
			//#if tmp.handleEvents
				EventManager.fireEvent( EventManager.EVENT_MENU_CLOSE, this, null );
			//#endif
			this.menuContainer.hideNotify();
		} else if (open && !this.menuOpened) {
			//#if tmp.handleEvents
				EventManager.fireEvent( EventManager.EVENT_MENU_OPEN, this, null );
			//#endif
			//#if !polish.MenuBar.focusFirstAfterClose
				// focus the first item again, so when the user opens the menu again, it will be "fresh" again
				this.menuContainer.focusChild(0);
			//#endif
			this.menuContainer.showNotify();
		}
		this.menuOpened = open;
		updateMenuTexts();
	}
	//#endif
	
	//#if polish.LibraryBuild
		/**
		 * Adds a command to this screen
		 * 
		 * @param cmd the command
		 */
		public void addCommand(javax.microedition.lcdui.Command cmd) {
			// ignore
		}
	//#endif

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Displayable#addCommand(javax.microedition.lcdui.Command)
	 */
	public void addCommand(Command cmd) {
		//#if polish.Item.suppressItemCommands
			if (cmd.getCommandType() == Command.ITEM) {
				return;
			}
		//#endif
		//#ifdef tmp.menuFullScreen
			//#if tmp.useExternalMenuBar 
				Style menuItemStyle = this.menuBar.getMenuItemStyle();
				if(menuItemStyle != null)
				{
					addCommand(cmd, menuItemStyle);
				}
				else
				{
			//#endif
				//#style menuitem, menu, default
				addCommand( cmd );
			//#if tmp.useExternalMenuBar 
				}
			//#endif
		//#else
				super.addCommand(cmd);
				//#ifdef tmp.triggerBackCommand
					int cmdType = cmd.getCommandType();
					if ((	   cmdType == Command.BACK 
						//#if ! tmp.triggerCancelCommand
							|| cmdType == Command.CANCEL 
						//#endif
							|| cmdType == Command.EXIT
						) 
						&& (this.backCommand == null || cmd.getPriority() < this.backCommand.getPriority())) 
					{
						this.backCommand = cmd;
					}
				//#endif
		//#endif
	}
	
	//#ifdef tmp.menuFullScreen
	/**
	 * Adds a command to this screen with the specified style.
	 * 
	 * @param cmd the command
	 * @param commandStyle the style for the command
	 */
	public void addCommand(Command cmd, Style commandStyle ) {
		int cmdType = cmd.getCommandType();
		//#if polish.Item.suppressItemCommands
			if (cmdType == Command.ITEM) {
				return;
			}
		//#endif
		//#debug
		System.out.println("adding command [" + cmd.getLabel() + "] to screen [" + this + "].");
		//#if tmp.useExternalMenuBar && polish.vendor.siemens
			if (this.menuBar == null) {
				//#debug
				System.out.println("Ignoring command [" + cmd.getLabel() + "] that is added while Screen is not even initialized.");
				return;
			}
		//#endif
		if ( cmdType == Command.OK 
				&&  (this.okCommand == null || this.okCommand.getPriority() > cmd.getPriority() ) ) 
		{
			this.okCommand = cmd;
		}
		//#if tmp.triggerCancelCommand
			else if ( cmdType == Command.CANCEL
					&& (this.cancelCommand == null || this.cancelCommand.getPriority() > cmd.getPriority()) ) 
			{
				this.cancelCommand = cmd;
			}
		//#endif
		
		//#ifdef tmp.triggerBackCommand
			else if ( (cmdType == Command.BACK
					//#if ! tmp.triggerCancelCommand
					|| cmdType == Command.CANCEL 
					//#endif
					|| cmdType == Command.EXIT ) 
				&& ( this.backCommand == null || this.backCommand.getPriority() > cmd.getPriority())  ) 
			{
				//#debug
				System.out.println("setting new backcommand=" + cmd.getLabel() + " for screen " + this + " " + getTitle() );
				this.backCommand = cmd;
			}
		//#endif
		//#ifdef tmp.useExternalMenuBar
			this.menuBar.addCommand(cmd, commandStyle);
			if (this.isInitialized || this.screenWidth > 0) {
				initMenuBar();
			}
			if (super.isShown()) {
				requestRepaint();
			}
		//#else
			if (this.menuCommands == null) {
				this.menuCommands = new ArrayList( 6, 50 );
				//#style menu, default
				 this.menuContainer = new Container( true );
				 this.menuContainer.screen = this;
				 if (this.menuContainer.style != null) {
					 //System.out.println("setting style for menuContainer " + this.menuContainer);
					 this.menuContainer.setStyle( this.menuContainer.style );
				 }
				 this.menuContainer.layout |= Item.LAYOUT_SHRINK;
			}
			if (cmd == this.menuSingleLeftCommand 
					|| cmd == this.menuSingleRightCommand 
					|| this.menuCommands.contains(cmd)) 
			{
				// do not add an existing command again...
				//#debug
				System.out.println("Ignoring existing command " + cmd.getLabel() );
				return;
			}
			if ( (cmdType == Command.BACK || cmdType == Command.CANCEL || cmdType == Command.EXIT) ) 
			{
				if ( (this.menuSingleRightCommand == null)
						|| (cmd.getPriority() < this.menuSingleRightCommand.getPriority())	)
				{
					// okay set the right menu command:
					if (this.menuSingleRightCommand != null) {
						// the right menu command is replaced by the new one,
						// so insert the original one into the options-menu:
						CommandItem menuItem = new CommandItem( this.menuSingleRightCommand, this.menuContainer, commandStyle  );
						menuItem.screen = this;
						this.menuContainer.add( menuItem );
						if (this.menuContainer.size() == 1) {
							this.menuSingleLeftCommand = this.menuSingleRightCommand;
						} else {
							this.menuSingleLeftCommand = null;
						}
						this.menuCommands.add( this.menuSingleRightCommand );
					}					
					// this is a command for the right side of the menu:
					this.menuSingleRightCommand = cmd;
					updateMenuTexts();
					requestRepaint();
					return;
				}
			}
			CommandItem menuItem = new CommandItem( cmd, this.menuContainer, commandStyle  );
			menuItem.screen = this;
			if ( this.menuCommands.size() == 0 ) {
				// using this command as the single-left-command:
				this.menuCommands.add( cmd );
				this.menuContainer.add( menuItem );
				this.menuSingleLeftCommand = cmd;
			} else {
				this.menuSingleLeftCommand = null;
				// there are already several commands,
				// so add this cmd to the appropriate sorted position:
				int priority = cmd.getPriority();
				Command[] myCommands = (Command[]) this.menuCommands.toArray( new Command[ this.menuCommands.size() ]);
				boolean inserted = false;
				for (int i = 0; i < myCommands.length; i++) {
					Command command = myCommands[i];
					if ( cmd == command ) {
						// ignore existing command:
						return;
					}
					if (command.getPriority() > priority ) {
						this.menuCommands.add( i, cmd );
						this.menuContainer.add(i, menuItem);
						inserted = true;
						break;
					}
				}
				if (!inserted) {
					this.menuCommands.add( cmd );
					this.menuContainer.add( menuItem );
				}
			}
			updateMenuTexts();
			requestRepaint();
		//#endif
	}
	//#endif
	
	/**
	 * Retrieves the CommandItem used for rendering the specified command. 
	 * 
	 * @param command the command
	 * @return the corresponding CommandItem or null when this command is not present in this MenuBar.
	 */
	public CommandItem getCommandItem(Command command) {
		CommandItem commandItem = null;
		//#if tmp.menuFullScreen
			//#ifdef tmp.useExternalMenuBar
				commandItem = this.menuBar.getCommandItem( command );
			//#else
				if (this.menuCommands != null) {
					int index = this.menuCommands.indexOf(command);
					if (index != -1) {
						commandItem = (CommandItem) this.menuContainer.get(index);
					} else {
						for (int i = 0; i < this.menuContainer.size(); i++) {
							CommandItem item = (CommandItem) this.menuContainer.get(i);
							item = item.getChild(command);
							if (item != null) {
								commandItem = item;
								break;
							}
						}
					}
				}
			//#endif
		//#endif
		return commandItem;
	}

	/**
	 * Removes all commands from this screen.
	 * This option is only available when the "menu" fullscreen mode is activated.
	 */
	public void removeAllCommands() {
		//#ifdef tmp.menuFullScreen
			//#ifdef tmp.useExternalMenuBar
				this.menuBar.removeAllCommands();
				if (this.isInitialized) {
					initMenuBar();
				}
			//#else
				this.menuCommands.clear();
				this.menuContainer.clear();
				updateMenuTexts();
			//#endif
			if (super.isShown()) {
				requestRepaint();
			}
		//#else
			Object[] commands = getCommands();
			if (commands != null) {
				for (int i = 0; i < commands.length; i++) {
					Command cmd = (Command) commands[i];
					if (cmd == null) {
						break;
					}
					removeCommand(cmd);
				}
			}
		//#endif
		//#ifdef tmp.triggerBackCommand
			this.backCommand = null;
		//#endif
		//#ifdef tmp.triggerCancelCommand
			this.cancelCommand = null;
		//#endif
	}

	//#ifdef tmp.menuFullScreen
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Canvas#getCommands()
	 */
	public Object[] getCommands()
	{
		Object[] commands;
		//#ifdef tmp.useExternalMenuBar
			commands = this.menuBar.getCommands();
		//#else
			commands = this.menuCommands.getInternalArray();
		//#endif
		return commands;
	}
	//#endif

	
	/**
	 * Adds the given command as a subcommand to the specified parent command.
	 * 
	 * @param child the child command
	 * @param parent the parent command
	 */
	public void addSubCommand(Command child, Command parent) {
		if (child.getStyle() != null) {
			addSubCommand( child, parent, child.getStyle() );
		} else {
			//#style menuitem, menu, default
			addSubCommand( child, parent );
		}
	}
	
	/**
	 * Adds the given command as a subcommand to the specified parent command.
	 * 
	 * @param child the child command
	 * @param parent the parent command
	 * @param commandStyle the style for the command
	 * @throws IllegalStateException when the parent command has not been added before
	 */
	public void addSubCommand(Command child, Command parent, Style commandStyle) {
		if (commandStyle != null) {
			child.setStyle( commandStyle );
		}
		if (!parent.addSubCommand(child)) {
			return; // this command has been added already
		}
		//#if tmp.menuFullScreen
			//#debug
			System.out.println("Adding subcommand " + child.getLabel() );
			//#ifdef tmp.useExternalMenuBar
				this.menuBar.addSubCommand( child, parent, commandStyle );
			//#else
				// find parent CommandItem, could be tricky, especially when there are nested commands over several layers
				if ( this.menuCommands == null ) {
					throw new IllegalStateException();
				}
				int index = this.menuCommands.indexOf( parent );
				CommandItem parentCommandItem = null;
				if (index != -1) {
					// found it:
					parentCommandItem = (CommandItem) this.menuContainer.get( index );
				} else {
					// search through all commands
					for ( int i=0; i < this.menuContainer.size(); i++ ) {
						CommandItem item = (CommandItem) this.menuContainer.get( i );
						parentCommandItem = item.getChild( parent );
						if ( parentCommandItem != null ) {
							break;
						}
					}
				}
				if ( parentCommandItem == null ) {
					throw new IllegalStateException();
				}
				parentCommandItem.addChild( child, commandStyle );
				if (parent == this.menuSingleLeftCommand) {
					this.menuSingleLeftCommand = null;
				}
			//#endif	
			if (super.isShown() || isMenuOpened()) {
				requestRepaint();
			}
		//#endif
	}
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Displayable#removeCommand(javax.microedition.lcdui.Command)
	 */
	public void removeCommand(Command cmd) {
		//#debug
		System.out.println("removing command " + cmd.getLabel() + " from screen " + this );
		//#ifdef tmp.triggerBackCommand
			if (cmd == this.backCommand) {
				this.backCommand = null;
				Object[] commands = getCommands();
				for (int i = 0; i < commands.length; i++) {
					Command command = (Command) commands[i];
					if (command == null) {
						break;
					}
					int cmdType = command.getCommandType();
					if (command != cmd && 
							(cmdType == Command.BACK 
							//#if ! tmp.triggerCancelCommand
								|| cmdType == Command.CANCEL 
							//#endif
								|| cmdType == Command.EXIT
							)
							&& (this.backCommand == null || command.getPriority() < this.backCommand.getPriority())
					) {
						this.backCommand = command;
					}
				}
			}
		//#endif
		//#ifdef tmp.menuFullScreen
			if (this.okCommand == cmd) {
				this.okCommand = null;
			}
			//#ifdef tmp.useExternalMenuBar
				this.menuBar.removeCommand(cmd);
				if (this.isInitialized) {
					initMenuBar();
				}
				if (super.isShown()) {
					requestRepaint();
				}
			//#else
				if (this.menuSingleRightCommand == cmd) {
					this.menuSingleRightCommand = null;
					//move another suitable command-item to the right-pos:
					if (this.menuCommands != null) {
						Object[] commands = this.menuCommands.getInternalArray();
						for (int i = 0; i < commands.length; i++) {
							Command command = (Command) commands[i];
							if (command == null) {
								break;
							}
							int type = command.getCommandType(); 
							if ( type == Command.BACK || type == Command.CANCEL ) {
								//System.out.println("removing right command [" + cmd.getLabel() + "], now using " + command.getLabel() + ", menuContainer=" + this.menuContainer.size() + ", menuCommands=" + this.menuCommands.size() + ",i=" + i );
								this.menuCommands.remove( i );
								this.menuContainer.remove( i );
								this.menuSingleRightCommand = command;
								break;
							}
						}
					}
					updateMenuTexts();
					requestRepaint();
					return;
				}
				if (this.menuCommands == null) {
					return;
				}
				int index = this.menuCommands.indexOf(cmd);
				if (index == -1) {
					return;
				}
				this.menuCommands.remove(index);
				if (this.menuSingleLeftCommand == cmd ) {
					this.menuSingleLeftCommand = null;
					this.menuContainer.remove(index);			
				} else {
					this.menuContainer.remove(index);			
				}
				updateMenuTexts();
				requestRepaint();
			//#endif
		//#else
			super.removeCommand(cmd);
		//#endif
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
		//#if !tmp.menuFullScreen
			removeCommand( childCommand );
		//#else
			//#debug
			System.out.println("Removing subcommand " + childCommand.getLabel() );
			//#ifdef tmp.useExternalMenuBar
				this.menuBar.removeSubCommand( childCommand, parentCommand );
			//#else
				// find parent CommandItem, could be tricky, especially when there are nested commands over several layers
				if ( this.menuCommands == null ) {
					throw new IllegalStateException();
				}
				int index = this.menuCommands.indexOf( childCommand );
				CommandItem commandItem = null;
				if (index != -1) {
					// found it:
					commandItem = (CommandItem) this.menuContainer.get( index );
				} else {
					// search through all commands
					for ( int i=0; i < this.menuContainer.size(); i++ ) {
						CommandItem item = (CommandItem) this.menuContainer.get( i );
						commandItem = item.getChild( childCommand );
						if ( commandItem != null ) {
							break;
						}
					}
				}
				if ( commandItem == null ) {
					throw new IllegalStateException();
				}
				index = this.menuCommands.indexOf( parentCommand );
				CommandItem parentCommandItem = null;
				if (index != -1) {
					// found it:
					parentCommandItem = (CommandItem) this.menuContainer.get( index );
				} else {
					// search through all commands
					for ( int i=0; i < this.menuContainer.size(); i++ ) {
						CommandItem item = (CommandItem) this.menuContainer.get( i );
						parentCommandItem = item.getChild( parentCommand );
						if ( parentCommandItem != null ) {
							break;
						}
					}
				}
				if ( parentCommandItem == null ) {
					throw new IllegalStateException();
				}
				parentCommandItem.removeChild(childCommand);
				this.menuCommands.remove(childCommand);
			//#endif	
			if (super.isShown() || isMenuOpened()) {
				requestRepaint();
			}
		//#endif
	}

	//#if tmp.menuFullScreen &&  tmp.useExternalMenuBar
	/**
	 * Initializes the menubar and calls calculateContentArea again if necessary.
	 * This method is only available when the external menubar and the fullscree mode is used.
	 */
	private void initMenuBar()
	{
		//#if tmp.useTitleMenu
			if (this.hasTitleMenu)
			{
				// ignore, any size updates are handled within the item
				return;
			}
		//#endif
		//TODO adjust for other menubar positions
		int availableWidth = this.screenWidth;
		//#if polish.css.separate-menubar
			if (!this.separateMenubar) {
				availableWidth -= (this.marginLeft + this.marginRight);
			}
		//#endif
		int previousHeight = this.menuBar.itemHeight;
		//int previousWidth = this.menuBar.itemWidth;
		int height = this.menuBar.getSpaceBottom( availableWidth, this.fullScreenHeight );
		if (height != previousHeight) {
			this.menuBarHeight = height;
			this.screenHeight = this.fullScreenHeight - height;
			this.menuBar.relativeY = this.screenHeight;
			this.isInitialized = false;
		}
	}
	//#endif
	
	/**
	 * @param commands
	 */
	public void addCommandsLayer(Command[] commands)
	{
		//#if tmp.useExternalMenuBar
			this.menuBar.addCommandsLayer( commands );
		//#endif
	}

	/**
	 * 
	 */
	public void removeCommandsLayer()
	{
		//#if tmp.useExternalMenuBar
			this.menuBar.removeCommandsLayer();
		//#endif
	}



//	private void showMenuItems() {
//		System.out.println("menuContainer.size=" + this.menuContainer.size() + ", menuCommands.size=" + this.menuCommands.size());
//		System.out.println("menuCommands: ");
//		for (int i=0; i<this.menuCommands.size(); i++) {
//			Command cmd = (Command) this.menuCommands.get(i);
//			System.out.println( i + "=" + cmd.getLabel() );
//		}
//		System.out.println("menuContainer: ");
//		for (int i=0; i<this.menuContainer.size(); i++) {
//			CommandItem cmd = (CommandItem) this.menuContainer.get(i);
//			System.out.println( i + "=" + cmd );
//		}
//	}
	
	/**
	 * Sets the commands of the given item
	 * 
	 * @param commandsList the commands that are added 
	 * @param item the item which contains the specified commands 
	 * @see #removeItemCommands(Item)
	 */
	protected void setItemCommands( ArrayList commandsList, Item item ) {
		//System.out.println("setItemCommands for " + item );
		this.focusedItem = item;
		if (commandsList != null) {
			Object[] commands = commandsList.getInternalArray();
			// register item commands, so that later onwards only commands that have been actually added
			// will be removed:
			if (this.itemCommands == null) {
				this.itemCommands = new ArrayList( commands.length );
			} else {
				if (this.itemCommands.size() > 0) {
					clearItemCommands();
				}
			}
			for (int i = 0; i < commands.length; i++) {
				Command command = (Command) commands[i];
				if (command == null) {
					break;
				}
				// workaround for cases where the very same command has been added to both an item as well as this screen:
				//System.out.println("scren: add ItemCommand " + command.getLabel() );
				boolean addCommand;
				//#ifdef tmp.useExternalMenuBar
					addCommand = !this.menuBar.contains( command );
				//#elif tmp.menuFullScreen
					addCommand = ( ( this.menuCommands == null) ||  !this.menuCommands.contains( command) )
								&& ( command != this.menuSingleLeftCommand && command != this.menuSingleRightCommand);
				//#else
					addCommand = true;
				//#endif
				if (addCommand) {
					addCommand(command);
					this.itemCommands.add( command );
				}
			}
		} else if (this.itemCommands != null) {
			this.itemCommands.clear();
		}
		//#ifdef tmp.useExternalMenuBar
			//#if ((polish.key.MiddleSoftKey:defined || polish.key.CenterSoftKey:defined) && (polish.MenuBar.useMiddleCommand != false) && polish.useScrollBar) || polish.MenuBar.useMiddleCommand || polish.MenuBar.useCenterCommand
				Command cmd = getDefaultCommand(item);
				if (cmd != null) {
					this.menuBar.informDefaultCommand( cmd );
				}
			//#endif
			if (isShown()) {
				requestRepaint();
			}
		//#endif
	}

	/**
	 * Informs this screen about a new default command for a focused item
	 * @param cmd the new default command
	 */
	public void notifyDefaultCommand(Command cmd) {
		//#ifdef tmp.useExternalMenuBar
			//#if ((polish.key.MiddleSoftKey:defined || polish.key.CenterSoftKey:defined) && (polish.MenuBar.useMiddleCommand != false) && polish.useScrollBar) || polish.MenuBar.useMiddleCommand || polish.MenuBar.useCenterCommand
				if (cmd != null) {
					this.menuBar.informDefaultCommand(cmd);
				}
			//#endif
		//#endif
	}

	
	/**
	 * Retrieves the default command of the specfied item.
	 * Default implementation just calls item.getDefaultCommand
	 * @param item the item
	 * @return the default command associated with the item, or null
	 */
	protected Command getDefaultCommand(Item item) {
		return item.defaultCommand;
	}
	
	/**
	 * Removes commands from the complete focused item hierarchy
	 */
	protected void clearItemCommands() {
		//System.out.println("removeItemCommands for " + item);
		if (this.itemCommands != null) {
			// use the Screen's itemCommands list, since in this list only commands that are only present on the item
			// are listed (not commands that are also present on the screen).
			Object[] commands = this.itemCommands.getInternalArray();
			for (int i = 0; i < commands.length; i++) {
				Command command = (Command) commands[i];
				if (command == null) {
					break;
				}
				removeCommand(command);
			}
		}
		//#ifdef tmp.useExternalMenuBar
			if (this.menuBar.size() ==0) {
				this.menuBarHeight = 0;
			}
		//#endif
			
		//#ifdef tmp.useExternalMenuBar
			if (super.isShown()) {
				requestRepaint();
			}
		//#endif
		if(this.itemCommands != null) {
			this.itemCommands.clear();
		}
	}
	
	/**
	 * Removes the commands of the given item.
	 *  
	 * @param item the item which has at least one command 
	 * @see #setItemCommands(ArrayList,Item)
	 */
	protected void removeItemCommands( Item item ) {
//		ArrayList commandsFromItem = item.getItemCommands();
		//System.out.println("removeItemCommands for " + item);
		if (this.itemCommands != null && this.itemCommands.size() > 0) {
			// use the Screen's itemCommands list, since in this list only commands that are only present on the item
			// are listed (not commands that are also present on the screen).
			Object[] commands = this.itemCommands.getInternalArray();
			for (int i = 0; i < commands.length; i++) {
				Command command = (Command) commands[i];
				if (command == null) {
					break;
				}
				
				removeCommand(command);
			}
			this.itemCommands.clear();
			//#ifdef tmp.useExternalMenuBar
				if (this.menuBar.size() ==0) {
					this.menuBarHeight = 0;
				}
			//#endif
			if (this.focusedItem == item) {
				this.focusedItem = null;
			}
			//#ifdef tmp.useExternalMenuBar
				if (super.isShown()) {
					requestRepaint();
				}
			//#endif
		}
	}
	
	/**
	 * Calls the command listener with the specified command.
	 * 
	 * @param cmd the command wich should be issued to the listener
	 */
	protected void callCommandListener( Command cmd ) {
		//System.out.println("Screen.callCommandListener() for command " + cmd.getLabel() + ", forwardCommandListener=" + this.forwardCommandListener );
		//#ifdef tmp.useExternalMenuBar
			this.menuBar.setOpen(false);
		//#elif tmp.menuFullScreen
			openMenu(false);
		//#endif
		//#if tmp.screenTransitions
			this.lastTriggeredCommand = cmd;
		//#endif
		if (this.forwardCommandListener != null) {
			try {
				this.forwardCommandListener.commandAction(cmd, this );
			} catch (Exception e) {
				//#debug error
				System.out.println("Screen: unable to process command [" + cmd.getLabel() + "]" + e  );
			}
		}
	}
	
	/**
	 * Retrieves the available height for this screen.
	 * This is equivalent to the Canvas#getHeight() method.
	 * This method cannot be overriden for Nokia's FullScreen though.
	 * So this method is used insted.
	 * 
	 * @return the available height in pixels.
	 */
	public int getAvailableHeight() {
		return this.screenHeight - this.titleHeight;
	}
	 	
	
	//#if polish.debugVerbose && polish.useDebugGui
	public void commandAction( Command command, Displayable screen ) {
		StyleSheet.display.setCurrent( this );
	}
	//#endif
	

	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#pointerPressed(int, int)
	 */
	public boolean _pointerPressed(int x, int y) {
		//#if polish.Screen.callSuperEvents
			super.pointerPressed(x, y);
		//#else
			pointerPressed(x, y); // for backwards compatibility
		//#endif
		//#debug
		System.out.println("pointerPressed at " + x + ", " + y );
		this.lastInteractionTime = System.currentTimeMillis();
		try {
			this.ignoreRepaintRequests = true;
			// let the screen handle the pointer pressing:
			boolean processed = false;
			// check for scroll-indicator:
			//#if tmp.useScrollIndicator
				if (  !processed && this.paintScrollIndicator &&
						(x > this.scrollIndicatorX) &&
						(y > this.scrollIndicatorY) &&
						(x < this.scrollIndicatorX + this.scrollIndicatorWidth) &&
						(y < this.scrollIndicatorY + (this.scrollIndicatorHeight << 1) + 1) ) 
				{
					//#debug
					System.out.println("ScrollIndicator has been clicked... ");
					// the scroll-indicator has been clicked:
					int gameAction;
					if ( (( !this.paintScrollIndicatorUp) || (y > this.scrollIndicatorY + this.scrollIndicatorHeight)) && this.paintScrollIndicatorDown) {
						gameAction = Canvas.DOWN;
					} else {
						gameAction = Canvas.UP;
					}
					//#if tmp.menuFullScreen
						//#ifdef tmp.useExternalMenuBar
							if (this.menuBar.isOpened) {
								this.menuBar.handleKeyPressed( 0, gameAction );
							} else {
								handleKeyPressed( 0, gameAction );
							}
						//#else
							if (this.menuOpened) {
								this.menuContainer.handleKeyPressed( 0, gameAction );
							} else {
								handleKeyPressed( 0, gameAction );
							}
						//#endif
					//#else
						handleKeyPressed( 0, gameAction );
					//#endif
					repaint();
					return true;
				}
			//#endif
			//#ifdef tmp.menuFullScreen
				//#ifdef tmp.useExternalMenuBar
					if (!processed) {
						processed = this.menuBar.handlePointerPressed(x - this.menuBar.relativeX, y - this.menuBar.relativeY);
					}
				//#else
					// check if one of the command buttons has been pressed:
					if (!processed && this.menuOpened) {
						if (y <= this.screenHeight) {
							// a menu-item could have been selected:
							if (this.menuContainer.handlePointerPressed( x - this.menuContainer.relativeX, y - this.menuContainer.relativeY )) {
								//openMenu( false ); close the menu in pointerReleased so that the user can scroll within large commands menu
								repaint();
							}
							return true;
						} else if (x <= this.menuLeftCommandX){
							// the "SELECT" command has been clicked:
							this.menuContainer.handleKeyPressed(0, Canvas.FIRE);
							repaint();
							return true;
						}
					}
				//#endif
			//#endif
			if (!processed) {
				processed = handlePointerPressed( x, y  );
			}
			if (!processed && this.subTitle != null) {
				processed = this.subTitle.handlePointerPressed(x - this.subTitle.relativeX, y - this.subTitle.relativeY);
			}
			//#if tmp.useScrollBar
				if (!processed) {
					this.scrollBar.handlePointerPressed( x - this.scrollBar.relativeX, y - this.scrollBar.relativeY );
				} else {
					this.scrollBar.isPointerPressedHandled = false;
				}
			//#endif
			//#if tmp.usingTitle
				if (!processed && this.title != null) {
					processed = this.title.handlePointerPressed(x - this.title.relativeX, y - this.title.relativeY);
				}
			//#endif
			//#ifdef tmp.usingTitle
				//boolean processed = handlePointerPressed( x, y - (this.titleHeight + this.infoHeight + this.subTitleHeight) );
				if (processed || this.isRepaintRequested) {
					this.isRepaintRequested = false;
					notifyScreenStateChanged();
					repaint();
					processed = true;
				}
			//#else
				if (processed || this.isRepaintRequested) {
					this.isRepaintRequested = false;
					repaint();
					processed = true;
				}
			//#endif
			
			//#ifdef polish.debug.debug
				if (!processed) {
					//#debug
					System.out.println("PointerPressed at " + x + ", " + y + " not processed.");					
				}
			//#endif
			return processed;
		} catch (Exception e) {
			//#debug error
			System.out.println("PointerPressed at " + x + "," + y + " resulted in exception" + e );
			return false;
		} finally {
			this.ignoreRepaintRequests = false;
			this.isScreenChangeDirtyFlag = false;
		}
	}
	//#endif
	

	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#_pointerDragged(int, int)
	 */
	public boolean _pointerDragged(int x, int y)
	{
		//#if polish.Screen.callSuperEvents
			super._pointerDragged(x, y);
		//#else
			pointerDragged(x, y); // for backwards compatibility
		//#endif
		//#debug
		System.out.println("screen: pointer drag " + x + ", " + y);
		try {
			this.ignoreRepaintRequests = true;
			//#if polish.Screen.callSuperEvents
				super.pointerDragged(x, y);
			//#endif
			ClippingRegion repaintRegion = this.userEventRepaintRegion;
			repaintRegion.reset();
			//#if tmp.useScrollBar
				if (this.scrollBar.handlePointerDragged( x - this.scrollBar.relativeX, y - this.scrollBar.relativeY, repaintRegion )) {
					repaint(repaintRegion);
					return true;
				}			
			//#endif
			//#ifdef tmp.menuFullScreen
				//#ifdef tmp.useExternalMenuBar
					if (this.menuBar.handlePointerDragged(x - this.menuBar.relativeX, y - this.menuBar.relativeY, repaintRegion)) {
						repaint(repaintRegion);
						return true;
					}
				//#else
					// check if one of the command buttons has been pressed:
					if (this.menuOpened && y <= this.screenHeight) {
						// a menu-item could have been selected:
						if (this.menuContainer.handlePointerDragged( x - this.menuContainer.relativeX, y - this.menuContainer.relativeY, repaintRegion )) {
							//openMenu( false ); close the menu in pointerReleased so that the user can scroll within large commands menu
							repaint(repaintRegion);
						}
						return true;
					}
				//#endif
			//#endif
			//#if tmp.usingTitle
				if (this.title != null) {
					if (this.title.handlePointerDragged(x - this.title.relativeX, y - this.title.relativeY, repaintRegion)) {
						repaint(repaintRegion);
						return true;
					}
				}
			//#endif
					
			if (handlePointerDragged(x,y)) {
				repaint();
			}
			repaintRegion.reset();
			handlePointerDragged(x, y, repaintRegion );
			if (repaintRegion.containsRegion()) {
				repaint( repaintRegion );
				return true;
			}
		} finally {
			this.ignoreRepaintRequests = false;
		}
		return false;
	}
	//#endif

	
	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#_pointerReleased(int, int)
	 */
	public boolean _pointerReleased(int x, int y)
	{
		//#if polish.Screen.callSuperEvents
			super.pointerReleased(x, y);
		//#else
			pointerReleased(x, y); // for backwards compatibility
		//#endif
		//#debug
		System.out.println("pointerReleased at " + x + ", " + y );
		boolean processed = false;
		try {
			this.ignoreRepaintRequests = true;
			this.lastInteractionTime = System.currentTimeMillis();
			//#ifdef tmp.menuFullScreen
				//#ifdef tmp.useExternalMenuBar
					if (!processed) {
						processed = this.menuBar.handlePointerReleased(x - this.menuBar.relativeX, y - this.menuBar.relativeY);
					}
				//#else
					// check if one of the command buttons has been pressed:
					if (!processed && y > this.screenHeight) {
		//				System.out.println("pointer at x=" + x + ", menuLeftCommandX=" + menuLeftCommandX );
						if (x >= this.menuRightCommandX && this.menuRightCommandX != 0) {
							if (this.menuOpened) {
								openMenu( false );
							} else if (this.menuSingleRightCommand != null) {
								callCommandListener(this.menuSingleRightCommand );
							}
							processed = true;
						} else if (x <= this.menuLeftCommandX){
							// assume that the left command has been pressed:
		//					System.out.println("x <= this.menuLeftCommandX: open=" + this.menuOpened );
							if (this.menuOpened ) {
								// the "SELECT" command has been clicked:
								this.menuContainer.handleKeyReleased(0, Canvas.FIRE);
								openMenu( false );
							} else if (this.menuSingleLeftCommand != null) {								
								this.callCommandListener(this.menuSingleLeftCommand);
							} else {
								openMenu( true );
								//this.menuOpened = true;
							}
							processed = true;
						}
					} else if (!processed && this.menuOpened) {
						// a menu-item could have been selected:
						//int menuY = this.originalScreenHeight - (this.menuContainer.itemHeight + 1);
						if (!this.menuContainer.handlePointerReleased( x - this.menuContainer.relativeX, y - this.menuContainer.relativeY )) {
							openMenu( false );
						}
						processed = true;
					}
				//#endif
			//#endif
			if (!processed) {
				processed = handlePointerReleased(x, y);
			}
			if (!processed && this.subTitle != null) {
				processed = this.subTitle.handlePointerReleased(x - this.subTitle.relativeX, y - this.subTitle.relativeY);
			}
			//#if tmp.useScrollBar
				if (!processed) {
					processed = this.scrollBar.handlePointerReleased( x - this.scrollBar.relativeX, y - this.scrollBar.relativeY );
				}
			//#endif
			//#if tmp.usingTitle
				if (!processed && this.title != null) {
					processed = this.title.handlePointerReleased(x - this.title.relativeX, y - this.title.relativeY);
				}
			//#endif
				
			if (processed || this.isRepaintRequested) {
				this.isRepaintRequested = false;
				repaint();
				processed = true;
			}
		} finally {
			this.ignoreRepaintRequests = false;
		}
		return processed;
	}
	//#endif
	
	
	/**
	 * Handles the pressing of a pointer.
	 * This method should be overwritten only when the polish.hasPointerEvents 
	 * preprocessing symbol is defined.
	 * When the screen could handle the pointer pressing, it needs to 
	 * return true.
	 * The default implementation returns the result of calling the container's
	 *  handlePointerPressed-method
	 *  
	 * @param x the absolute x position of the pointer pressing
	 * @param y the absolute y position of the pointer pressing
	 * @return true when the pressing of the pointer was actually handled by this screen.
	 */
	protected boolean handlePointerPressed( int x, int y ) {
		boolean handled = false;
		//#ifdef polish.hasPointerEvents
			if (this.container != null) {
				handled = this.container.handlePointerPressed(x - this.container.relativeX, y - this.container.relativeY );
			}
		//#endif
		return handled;
	}
	
	/**
	 * Handles the release of a pointer.
	 * This method should be overwritten only when the polish.hasPointerEvents 
	 * preprocessing symbol is defined.
	 * When the screen could handle the pointer release, it needs to 
	 * return true.
	 * The default implementation returns the result of calling the container's
	 *  handlePointerReleased-method
	 *  
	 * @param x the absolute x position of the pointer release
	 * @param y the absolute y position of the pointer release
	 * @return true when releasing the pointer was actually handled by this screen.
	 */
	protected boolean handlePointerReleased( int x, int y ) {
		boolean handled = false;
		//#ifdef polish.hasPointerEvents
			Container cont = this.container;
			if (cont != null) {
				handled = cont.handlePointerReleased(x - cont.relativeX, y - cont.relativeY );
			}
			//#if polish.css.repaint-previous-screen
				if (!handled && this.repaintPreviousScreen) {
					if (cont != null) {
						int left = cont.relativeX;
						int top = cont.relativeY - this.titleHeight;
						int right = left + cont.itemWidth;
						int bottom = top + Math.min( this.contentY + this.contentHeight, cont.itemHeight );
						if (x <= left || y <= top
							 || x >= right || y >= bottom)
						{
							handlePointerReleasedOutsideScreenArea(x, y);
						}
					} else {
						if (x <= this.marginLeft || y <= this.marginTop 
						 || x >= this.screenWidth - this.marginRight || y >= this.screenHeight - this.marginBottom)
						{
							handlePointerReleasedOutsideScreenArea(x, y);
						}
					}
				}
			//#endif
		//#endif
		return handled;
	}
	
	/**
	 * Handles the release of a pointer outside of this screens area.
	 * This method should be overwritten only when the polish.hasPointerEvents 
	 * preprocessing symbol is defined.
	 * When the screen could handle the pointer release, it needs to 
	 * return true.
	 * The default implementation fires the back command, if present. Otherwise it will fire the only command (when there is just one command on this screen).
	 *  
	 * @param x the absolute x position of the pointer release (outside of this screen's area)
	 * @param y the absolute y position of the pointer release (outside of this screen's area)
	 * @return true when releasing the pointer was actually handled by this screen.
	 */
	protected boolean handlePointerReleasedOutsideScreenArea( int x, int y ) {
		boolean handled = false;
		//#if polish.hasPointerEvents && polish.css.repaint-previous-screen
			//#if tmp.triggerBackCommand
				if (this.backCommand != null) {
					handleCommand( this.backCommand );
				} else {
			//#endif
					Object[] commands = getCommands();
					if (commands != null) {
						int commandCount = 0;
						for (int i = 0; i < commands.length; i++) {
							Object cmd = commands[i];
							if (cmd == null) {
								break;
							}
							commandCount++;
						}
						if (commandCount == 1) {
							handleCommand( (Command) commands[0] );
						}
					}
			//#if tmp.triggerBackCommand
				}
			//#endif
		//#endif
		return handled;
	}
	
	/**
	 * Handles the dragging/movement of a pointer.
	 * This method should be overwritten only when the polish.hasPointerEvents 
	 * preprocessing symbol is defined.
	 * When the screen could handle the pointer drag event, it needs to 
	 * return true.
	 * The default implementation returns the result of calling the container's
	 * handlePointerDragged-method
	 *  
	 * @param x the absolute x position of the pointer movement
	 * @param y the absolute y position of the pointer movement
	 * @return true when the dragging of the pointer was actually handled by this screen.
	 */
	protected boolean handlePointerDragged(int x, int y, ClippingRegion repaintRegion)
	{
		boolean handled = false;
		//#ifdef polish.hasPointerEvents
			if (this.container != null) {
				handled = this.container.handlePointerDragged(x - this.container.relativeX, y - this.container.relativeY, repaintRegion );
			}
		//#endif
		return handled;
	}

	/**
	 * Handles the dragging/movement of a pointer.
	 * This method should be overwritten only when the polish.hasPointerEvents 
	 * preprocessing symbol is defined.
	 * When the screen could handle the pointer drag event, it needs to 
	 * return true.
	 * The default implementation returns false.
	 *  
	 * @param x the absolute x position of the pointer movement
	 * @param y the absolute y position of the pointer movement
	 * @return true when the dragging of the pointer was actually handled by this screen.
	 * @see #handlePointerDragged(int, int, ClippingRegion)
	 */
	protected boolean handlePointerDragged(int x, int y)
	{
		return false;
	}

	/**
	 * Handles a touch down/press event. 
	 * This is similar to a pointerPressed event, however it is only available on devices with screens that differentiate
	 * between press and touch events (read: BlackBerry Storm).
	 * 
	 * @param x the absolute horizontal pixel position of the touch event 
	 * @param y  the absolute vertical pixel position of the touch event
	 * @return true when the event was handled
	 */
	public boolean handlePointerTouchDown( int x, int y ) {
		boolean handled = false;
		//#ifdef polish.hasTouchEvents
			Container cont = null;
			if (!isMenuOpened()) {
				cont = this.container;
				if (cont != null) {
					if (cont.handlePointerTouchDown(x - cont.relativeX, y - cont.relativeY)) {
						return true;
					}
				}
			}
			Item item = getItemAt(x, y);
			if (item != null) {
				int offset = getScrollYOffset();
				focus(item);
				if (offset != getScrollYOffset()) {
					setScrollYOffset(offset, false );
				}
				notifyScreenStateChanged();
			}
			// stop scrolling:
			if (cont != null) {
				cont.setScrollYOffset(cont.yOffset, false);
			}
			handled = true;
		//#endif
		return handled;
	}
	

	/**
	 * Handles a touch up/release event. 
	 * This is similar to a pointerReleased event, however it is only available on devices with screens that differentiate
	 * between press and touch events (read: BlackBerry Storm).
	 * 
	 * @param x the absolute horizontal pixel position of the touch event 
	 * @param y  the absolute vertical pixel position of the touch event
	 * @return true when the event was handled
	 */
	public boolean handlePointerTouchUp( int x, int y ) {
		boolean handled = false;
		//#ifdef polish.hasTouchEvents
			this.lastInteractionTime = System.currentTimeMillis();
			Container cont = this.container;
			if (cont != null) {
				if (cont.handlePointerTouchUp(x - cont.relativeX, y - cont.relativeY)) {
					return true;
				}
			}
			handled = true;
		//#endif
		return handled;
	}
	
	/**
	 * Tries to handle the specified command.
	 * The default implementation forwards the call to the container. When the container is unable to process the command,
	 * it will be forwarded to an external command listener that has been set using setCommandListener(..)
	 * 
	 * @param cmd the command
	 * @return true when the command has been handled by this screen
	 */	
	protected boolean handleCommand( Command cmd ) {
		//#if polish.Screen.callSuperEvents
			boolean handled = false;
			//# handled = super.handleCommand(cmd);
			if (handled) {
				return true;
			}
		//#endif
		//this.isInRequestInit = true;
			//check if the given command is from the currently focused item:
			Item item = this.container;
			if (item == null) {
				item = getCurrentItem();
			}
			//#debug
			System.out.println("ForwardCommandListener: processing command " + cmd.getLabel() + " for item " + item + " and screen " + Screen.this + ", itemCommandListener=" + (item == null ? null : item.itemCommandListener) + ", real commandListener=" + this.realCommandListener);
			if ( item != null && item.handleCommand(cmd)) {
				return true;
			}
			// try to invoke command specific listener:
			if (cmd.commandAction(item, this)) {
				return true;
			}
			// now invoke the usual command listener:
			CommandListener listener = this.realCommandListener;
			if (listener != null) {
				//#if polish.executeCommandsAsynchrone
					AsynchronousMultipleCommandListener.getInstance().commandAction(listener, cmd, this);
				//#else
					listener.commandAction(cmd, this);
				//#endif
				return true;
			}
		
		return false;
	}
	
	//#if polish.BuildLibrary
	/**
	 * Tries to handle the specified command.
	 * The default implementation forwards the call to the container. When the container is unable to process the command,
	 * it will be forwarded to an external command listener that has been set using setCommandListener(..)
	 * 
	 * @param cmd the command
	 * @return true when the command has been handled by this screen
	 */	
	protected boolean handleCommand( javax.microedition.lcdui.Command cmd ) {
		return false;
	}
	//#endif
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#setFullScreenMode(boolean)
	 */
	public void setFullScreenMode(boolean enable) {
		//#if polish.midp2 && !tmp.fullScreen
			super.setFullScreenMode(enable);
		//#endif
	    //#if tmp.usingTitle || tmp.menuFullScreen
			this.showTitleOrMenu = !enable;
		//#endif
		//#ifdef tmp.menuFullScreen
			if (enable) {
				this.screenHeight = this.fullScreenHeight;
			} else {
				this.screenHeight = this.originalScreenHeight;
			}
		//#endif
		requestRepaint();
	}
	
	/**
	 * Adjusts the size of this screen.
	 * @param width the new width of the screen
	 * @param height the new height of the screen
	 */
	public void sizeChanged(int width, int height) {
		//If the constructor has not been called, exit the method
		//This is done because on some devices Displayable.sizeChanged() is
		//called in the super constructor of Screen
		//this.lastSizeChangedEvent = width + "x" +  height;
		if(this.paintLock == null) {
			return;
		}
		//#if polish.css.portrait-style || polish.css.landscape-style
		if (!DeviceControl.isSoftKeyboardShown()) {
			Style newStyle = null;
			if ((width > height)) {
				if (this.landscapeStyle != null && this.style != this.landscapeStyle) {
					newStyle = this.landscapeStyle;
				}
			} else if (this.portraitStyle != null && this.style != this.portraitStyle){
				newStyle = this.portraitStyle;
			}
			if (newStyle != null) {
				setStyle( newStyle );
			}
		}
		//#endif
		
		//#if !polish.Bugs.sizeChangedReportsWrongHeight 
			//#debug
			System.out.println("Screen: sizeChanged to width=" + width + ", height=" + height );
			boolean doInit; // = !this.isInitialized;
		
			//#ifdef tmp.menuFullScreen
				doInit = width != this.screenWidth || height != this.fullScreenHeight;
				if (doInit) {
					if (this.fullScreenHeight != 0) {
						int diff = height - this.fullScreenHeight;
						if (diff > 0 && this.container != null) {
							int offset = this.container.getScrollYOffset() + diff;
							if (offset > 0) {
								offset = 0;
							}
							this.container.setScrollYOffset(offset, false);
						}
					}
					this.fullScreenHeight = height;
					this.screenHeight = height - this.menuBarHeight;
					this.originalScreenHeight = this.screenHeight;
					this.screenWidth = width;
				}
			//#else
				doInit = width != this.screenWidth || height != this.originalScreenHeight;
				if (doInit) {
					this.originalScreenHeight = this.screenHeight;
					this.screenWidth = width;
					this.screenHeight = height;
				}
			//#endif
			//System.out.println("sizeChanged - doInit=" + doInit);
			if (doInit) {
				int beforeYOffset = getScrollYOffset();
				synchronized (this.paintLock) {
					init( width, height );
				}
				int afterYOffset = getScrollYOffset();
				if (beforeYOffset != afterYOffset && getRootContainer() != null && getRootContainer().itemHeight < this.contentHeight) {
					setScrollYOffset(0, false);
				}
			}
			//#if polish.css.repaint-previous-screen
				if (this.repaintPreviousScreen && this.previousScreen != null) {
					this.previousScreen.sizeChanged(width, height);
				}
			//#endif
		//#endif
	}
	
	/**
	 * @return the usable screen height
	 */
	public int getScreenHeight() {
		int result;
		//#ifdef tmp.menuFullScreen
			result = this.fullScreenHeight;
		//#else
			result = this.screenHeight;
		//#endif
		return result;
	}
	
	
	/**
	 * Focuses the specified item.
	 * 
	 * @param item the item which is already shown on this screen.
	 */
	public void focus(Item item) {
		focus( item, false );
	}
	
	/**
	 * Focuses the specified item.
	 * 
	 * @param item the item which is already shown on this screen.
	 * @param force true when the item should be focused even when it is inactive (like a label for example)
	 */
	public void focus(Item item, boolean force ) {
		int index;
		if (item == null) {
			index = -1;
		} else if (item.isFocused){
			// ignore:
			return;
		} else if (force || item.isInteractive()){
			index = this.container.itemsList.indexOf(item);
			if (index == -1) {
				ArrayList children = new ArrayList();
				children.add( item );
				Item parent = item.parent;
				while (parent != null) {
					if (parent.parent == this.container) {
						index = this.container.itemsList.indexOf(parent);
						if (index != -1) {
							// found the item!
							focus( index, parent, force );
							//System.out.println("found nested item " + parent + " - number of parents=" + children.size());
							for (int i=children.size() - 1; i>=0; i--) {
								Item child = (Item) children.get(i);
								if (child.appearanceMode != Item.PLAIN || force ) {
									Container parentContainer = (Container) child.parent;
									//System.out.println("focusing " + child + " in parent " + parentContainer);
									parentContainer.focusChild( parentContainer.indexOf(child), child, 0, force );
								} else {
									//System.out.println("child is not focussable: " + child);
									return;
								}
							}
							return;
						}
					}
					children.add( parent );
					parent = parent.parent;
				}
				if (index == -1) {
					//#ifdef tmp.menuFullScreen
						//#ifdef tmp.useExternalMenuBar
							this.menuBar.focusChild( item );
						//#else
							if (this.menuOpened) {
								index = this.menuContainer.indexOf( item );
								if (index != -1) {
									this.menuContainer.focusChild(index);
								}
							} 
						//#endif
					//#endif
					// ignore this call, the item should be focused, but it is unknown for this screen:
					return;
				}
			}
		} else {
			return;
		}
		focus( index, item, force );
	}
	
	/**
	 * Focuses the specified item.
	 * 
	 * @param index the index of the item which is already shown on this screen.
	 */
	public void focus(int index) {
		focus( index, true );
	}
	
	/**
	 * Focuses the specified item.
	 * 
	 * @param index the index of the item which is already shown on this screen.
	 * @param force true when the item should be focused even when it is inactive (like a label for example)
	 */
	public void focus(int index, boolean force) {
		Item item = null;
		if (index != -1) {
			item = this.container.get(index);
		}
		focus( index, item, force );
	}
	
	/**
	 * Focuses the specified item.
	 * 
	 * @param index the index of the item which is already shown on this screen.
	 * @param item the item which is already shown on this screen.
	 * @param force true when the item should be focused even when it is inactive (like a label for example)
	 */
	public void focus(int index, Item item, boolean force) {
		if (item != null && item.isFocused){
			// ignore refocusing of already focused item:
			return;
		}
		if (index != -1 && item != null && (item.appearanceMode != Item.PLAIN || force ) ) {
			//#debug
			System.out.println("Screen: focusing item " + index + ": " + item );
			this.container.focusChild( index, item, 0, force );
			if (index == 0) {
				this.container.setScrollYOffset( 0, false );
			}
		} else if (index == -1) {
			this.container.focusChild( -1 );
		} else {
			//#debug warn
			System.out.println("Screen: unable to focus item (did not find it in the container or is not activatable): index=" + index + ", item=" + item);
		}
		repaint();
	}
	
	/**
	 * Sets the subtitle element.
	 * The subtitle is drawn directly below of the title (above the info-item, if there
	 * is any) and is always shown (unless it is null).
	 * 
	 * @param subTitle the new subtitle element.
	 * @see #getSubTitleItem()
	 */
	protected void setSubTitle( Item subTitle ) {
		this.subTitle = subTitle;
		if (subTitle == null) {
			this.subTitleHeight = 0;
		} else {
			subTitle.screen = this;
		}
		if (this.isInitialized) {
			calculateContentArea( 0, 0, this.screenWidth, this.screenHeight );
		}
	}
	
	
	/**
	 * Retrieves the currently focused item.
	 * 
	 * @return the currently focused item, null when none is focused.
	 */
	public Item getCurrentItem() {
		if (this.container != null) {
			if (this.container.autoFocusEnabled) {
				Item[] items = this.container.getItems();
				for (int i = 0; i < items.length; i++) {
					if (i >= this.container.autoFocusIndex && items[i].appearanceMode != Item.PLAIN) {
						return items[i];
					}
				}
			}
			return this.container.focusedItem;
		}
		return this.focusedItem;
	}
	
	/**
	 * Retrieves the index of the currently focused item.
	 * 
	 * @return  the index of the currently focused item, -1 when no item has been selected
	 */
	public int getCurrentIndex() {
		if (this.container != null) {
			return this.container.focusedIndex;
		}
		return -1;
	}

	/**
	 * Checks whether the commands menu of the screen is currently opened.
	 * Useful when overriding the keyPressed() method.
	 * 
	 * @return true when the commands menu is opened.
	 */
	public boolean isMenuOpened() {
		boolean result = false;
		//#if tmp.useExternalMenuBar
			result = this.menuBar.isOpened;
		//#elif tmp.menuFullScreen
			result = this.menuOpened;
		//#endif
		return result;
	}
	
	/**
	 * Closes the commands menu of this screen.
	 * This can only be done when the fullscreen mode is set to "menu" in your build.xml script.
	 */
	public void closeMenu() {
		//#if tmp.menuFullScreen
			//#if tmp.useExternalMenuBar
				this.menuBar.setOpen(false);
			//#else
				openMenu( false );
			//#endif
		//#endif
	}

	
	/**
	 * Releases all (memory intensive) resources such as images or RGB arrays of this item.
	 * The default implementation does release any background resources.
	 * This method must not be called from within a paint call, as this will result in a dead lock.
	 */
	public void releaseResources() {
		synchronized (this.paintLock) {
			if (this.background != null) {
				this.background.releaseResources();
			}
			if (this.container != null) {
				this.container.releaseResources();
			}
					
			//#ifdef tmp.menuFullScreen
				//#ifdef tmp.useExternalMenuBar
					this.menuBar.releaseResources();
				//#else
					this.menuContainer.releaseResources();
				//#endif
			//#endif
			//#ifdef tmp.usingTitle
				if (this.title != null) {
					this.title.releaseResources();
				}
			//#endif
			//#ifndef polish.skipTicker
				if (this.ticker != null) {
					this.ticker.releaseResources();
				}
			//#endif
			this.isResourcesReleased = true;
		}
	}
	
	/**
	 * Destroys the screen
	 */
	public void destroy() {
		releaseResources();
		
		this.container.destroy();
		
		if(this.screenStateListener != null) {
			this.screenStateListener = null;
		}
		
		if(this.itemStateListener != null) {
			this.itemStateListener = null;
		}
		
		if(this.forwardCommandListener != null ) {
			this.forwardCommandListener = null;
		}
		
		if(this.realCommandListener != null) {
			this.realCommandListener = null;	
		}
	}

	/**
	 * Scrolls this screen by the given amount.
	 * 
	 * @param amount the number of pixels, positive values scroll upwards, negative scroll downwards
	 * @see #setScrollYOffset(int, boolean)
	 */
	public void scrollRelative(int amount) {
		if (this.container != null) {
			this.container.setScrollYOffset( this.container.getScrollYOffset() + amount );
			requestRepaint();
		}
	}
	

	/**
	 * Attaches data to this screen.
	 * This mechanism can be used to add business logic to screens.
	 * 
	 * @param data the screen specific data
	 * @see UiAccess#setData(Screen, Object)
	 * @see UiAccess#getData(Screen)
	 */
	public void setScreenData(Object data) {
		this.data = data;
	}
	
	/**
	 * Retrieves screen specific data.
	 * This mechanism can be used to add business logic to screens.
	 * 
	 * @return any screen specific data or null when no data has been attached before
	 * @see UiAccess#setData(Screen, Object)
	 * @see UiAccess#getData(Screen)
	 */
	public Object getScreenData() {
		return this.data;
	}
	
	/**
	 * Locates and returns the item at the given coordinate.
	 * 
	 * @param x horizontal position in pixels
	 * @param y vertical position in pixels
	 * @return the found item or null when no item is at the specific coordinate
	 */
	public Item getItemAt( int x, int y ) {
		Item item = null;
		//#ifdef tmp.menuFullScreen
			//#ifdef tmp.useExternalMenuBar
				item = this.menuBar.getItemAt( x - this.menuBar.relativeX, y - this.menuBar.relativeY );
				if (item != null) {
					return item;
				}
			//#else
				if (this.menuOpened) {
					item = this.menuContainer.getItemAt( x - this.menuContainer.relativeX, y - this.menuContainer.relativeY );
				} else
			//#endif
		//#endif
		if (this.container != null) {
			item = this.container.getItemAt( x - this.container.relativeX, y - this.container.relativeY );
			if (item != null) {
				return item;
			}
		}
		//#ifdef tmp.usingTitle
			if (this.title != null) {
				//item = this.title.getItemAt( x - this.title.relativeX, y - this.title.relativeY );
				item = this.title.getItemAt( x - this.title.relativeX, y - this.title.relativeY );
				if (item != null) {
					return item;
				}
			}
		//#endif
		//#ifndef polish.skipTicker
			if (this.ticker != null) {
				item = this.ticker.getItemAt( x - this.ticker.relativeX, y - this.ticker.relativeY );
				if (item != null) {
					return item;
				}
			}
		//#endif
		//#if polish.ScreenInfo.enable
			if (ScreenInfo.item != null && ScreenInfo.isVisible()) {
				item = ScreenInfo.item.getItemAt( x - ScreenInfo.item.relativeX, y - ScreenInfo.item.relativeY );
				if (item != null) {
					return item;
				}
			}
		//#endif
		return null;
	}

	/**
	 * Retrieves the style currently used by this screen.
	 * 
	 * @return this screen's style
	 */
	public Style getScreenStyle() {
		return this.style;
	}
	
	/**
	 * Sets the <code>ItemStateListener</code> for the <code>Screen</code>, 
	 * replacing any previous <code>ItemStateListener</code>. 
	 * If
	 * <code>iListener</code> is <code>null</code>, simply
	 * removes the previous <code>ItemStateListener</code>.
	 * 
	 * @param iListener the new listener, or null to remove it
	 */
	public void setItemStateListener( ItemStateListener iListener)
	{
		this.itemStateListener = iListener;
	}
	
	//#if polish.LibraryBuild
	/**
	 * Sets the <code>ItemStateListener</code> for the <code>Screen</code>, 
	 * replacing any previous <code>ItemStateListener</code>. 
	 * If
	 * <code>iListener</code> is <code>null</code>, simply
	 * removes the previous <code>ItemStateListener</code>.
	 * 
	 * @param iListener the new listener, or null to remove it
	 */
	public void setItemStateListener( javax.microedition.lcdui.ItemStateListener iListener ) {
		throw new RuntimeException("Unable to use standard ItemStateListener in a screen.");
	}
	//#endif

	
	/**
	 * Adds the given item to the queue for state notifications.
	 * The ItemStateListener will be called at the next possibility.
	 * 
	 * @param item the item which contents have been edited.
	 */
	protected void notifyStateListener( Item item ) {
		if (this.itemStateListener != null) {
			try {
				this.itemStateListener.itemStateChanged(item);
			} catch (Exception e) {
				//#debug error
				System.out.println("Unable to forward itemStateChanged event for listener " + this.itemStateListener + e );
			}
		}

//		if (this.itemStateListener != null) {
//			if (this.stateNotifyQueue == null) {
//				this.stateNotifyQueue = new ArrayList();
//			}
//			synchronized (this.stateNotifyQueue) {
//				this.stateNotifyQueue.add( item );
//			}
//			//#debug
//			System.out.println("added item " + item + " to stateNotifyQueue with listener " + this.itemStateListener + ", size of queue=" + this.stateNotifyQueue.size() + " to form " + this  );
//		}
	}
	
//	/**
//	 * Notifies the ItemStateListener about the changes which occurred to the items.
//	 */
//	protected void notifyStateListener() {
//		if (this.stateNotifyQueue != null && this.itemStateListener != null) {
//			//Item lastItem = null;
//			while (this.stateNotifyQueue.size() > 0) {
//				Item item;
//				synchronized (this.stateNotifyQueue) {
//					item = (Item) this.stateNotifyQueue.remove(0);
//				}
//				//if (item != lastItem) { // 2007-08-06: sometimes there are two subsequent fast changes, so this has to be forwarded correctly:
//					//#debug
//					System.out.println("notifying ItemStateListener for item " + item + " and form " + this ); 
//					this.itemStateListener.itemStateChanged(item);
//					//lastItem = item;
//				//}
//			}
//			//#debug
//			System.out.println("done notifying ItemStateListener."); 
//		}
//	}

	/**
	 * Checks if the keyboard (if any) is currently accessible by the application.
	 * This is useful for devices that can be opened by the user like the Nokia/E70.
	 * 
	 * @return true when the keyboard (if there is one) is accessible by the application.
	 */
	protected boolean isKeyboardAccessible() {
		//#if polish.api.windows
			//# return de.enough.polish.windows.Keyboard.isKeyboardAccessible();
		//#elif polish.key.supportsAsciiKeyMap.condition == open
			return getWidth() > getHeight();
		//#elif polish.key.supportsAsciiKeyMap && !polish.key.supportsAsciiKeyMap.condition:defined
			//# return true;
		//#elif polish.key.maybeSupportsAsciiKeyMap
			//# return true;
		//#else
			//# return false;
		//#endif
	}

	/**
	 * Retrieves the width of the scrollbar
	 * Note that you need to activate the usage of the scrollbar
	 * by setting polish.useScrollbar=true
	 * 
	 * @return the width of the scrollbar, 0 when none is used or when the screen is not yet initialized
	 */
	protected int getScrollBarWidth() {
		int width = 0;
		//#if tmp.useScrollBar
			//#if polish.css.show-scrollbar
				if ( this.scrollBarVisible ) {
			//#endif
					if (!this.scrollBar.overlap) {
						width = this.scrollBar.itemWidth;
					}
			//#if polish.css.show-scrollbar
				}
			//#endif
		//#endif
		return width;
	}




	/**
	 * Retrieves the height of the content area.
	 * 
	 * @return the height available for the content in pixels
	 * @see #getScrollHeight() for retrieving the screen's actual content height
	 */ 
	public int getScreenContentHeight() {
		if (this.contentHeight == 0) {
			calculateContentArea(0, 0, this.screenWidth, this.screenHeight);
		}
		return this.contentHeight;
	}

	/**
	 * Retrieves the width of the content area.
	 * 
	 * @return the width available for the content
	 */ 
	public int getScreenContentWidth() {
		if (this.contentHeight == 0) {
			calculateContentArea(0, 0, this.screenWidth, this.screenHeight);
		}
		return this.contentWidth;
	}
	
	/**
	 * Retrieves the horizontal start position of the screen's content area
	 * @return the horizontal start in pixels from the left
	 */
	public int getScreenContentX() {
		if (this.contentHeight == 0) {
			calculateContentArea(0, 0, this.screenWidth, this.screenHeight);
		}
		return this.contentX;
	}

	/**
	 * Retrieves the vertical start position of the screen's content area
	 * @return the vertical start in pixels from the top
	 */
	public int getScreenContentY() {
		if (this.contentHeight == 0) {
			calculateContentArea(0, 0, this.screenWidth, this.screenHeight);
		}
		return this.contentY;
	}

	
	/**
	 * Retrieves the height that the complete screen uses, including title, menubar, ticker, etc.
	 * 
	 * @return the fully available height.
	 */
	public int getScreenFullHeight() {
		int height;
		//#ifdef tmp.menuFullScreen
			height = this.fullScreenHeight;
		//#else
			height = this.screenHeight;
		//#endif
		if (height == 0) {
			height = Display.getScreenHeight();
		}
		return height;
	}
	
	/**
	 * Retrieves the width that the complete screen uses, including scrollbar, etc.
	 * 
	 * @return the fully available width.
	 */
	public int getScreenFullWidth() {
		return Display.getScreenWidth();
	}

	
	//#ifdef tmp.useExternalMenuBar
	/**
	 * Sets the style for menuItems.
	 * This can only be used within the menu fullscreen mode and when the external menu bar is used.
	 * 
	 * @param menuItemStyle the style for menu items
	 */
	public void setMenuItemStyle(Style menuItemStyle)
	{
		this.menuBar.setMenuItemStyle(menuItemStyle);
	}
	//#endif
	
	//#ifdef tmp.useExternalMenuBar
	/**
	 * Allows to access the menu bar.
	 * This can only be used within the menu fullscreen mode and when the external menu bar is used.
	 * 
	 * @return the menubar instanse
	 */
	public MenuBar getMenuBar()
	{
		return this.menuBar;
	}
	//#endif


	/**
	 * Sets the style for the menubar.
	 * A full style is only applied when the external menubar is used, set the preprocessing variable
	 * polish.MenuBar.useExtendedMenuBar to true for this.
	 * 
	 * @param menuBarStyle
	 */
	public void setMenuBarStyle(Style menuBarStyle)
	{
		//#ifdef tmp.useExternalMenuBar
			this.menuBar.setStyle(menuBarStyle);
			requestInit();
		//#elif tmp.menuFullScreen
			this.menuFontColor = menuBarStyle.getFontColor();
		//#endif
	}
	
	
	/**
	 * Retrieves the vertical scroll offset.
	 * 
	 * @return the vertical scroll offset in pixels.
	 * @see #setScrollYOffset(int, boolean)
	 * @see #getScrollHeight()
	 */
	public int getScrollYOffset()
	{
		if (this.container != null) {
			return this.container.getScrollYOffset();
		}
		return 0;
	}
	
	/**
	 * Retrieves this screen's actual content's height
	 * @return the content's height, 0 if unknown or not initialized yet
	 * @see #getScrollYOffset() 
	 */
	public int getScrollHeight() {
		if (this.container != null) {
			return this.container.getItemAreaHeight();
		}
		return 0;
	}

	/**
	 * Sets the vertical scrolling offset of this screen.
	 *  
	 * @param offset either the new offset
	 * @param smooth scroll to this new offset smooth if allowed
	 * @see #getScrollYOffset()
	 */
	public void setScrollYOffset(int offset, boolean smooth)
	{
		if (this.container != null) {
			this.container.setScrollYOffset(offset, smooth);
		}
	}
	
	
	/**
	 * Indicates if this screen is active (scrolling, key pressed etc.)
	 * @return true, if the screen was active in the given interval, otherwise false
	 */
	public boolean isActive()
	{
		long currentTime = System.currentTimeMillis();
		return 	(currentTime - this.lastInteractionTime) < 100 ||
				(currentTime - this.lastAnimateTime) < 100;
	}
	
	/**
	 * Indicates if this screen was interacted with in the given timespan
	 * @param timespan the timespan 
	 * @return true, if the screen was active in the given interval, otherwise false
	 */
	public boolean isInteracted(long timespan)
	{
		return 	(System.currentTimeMillis() - this.lastInteractionTime) < timespan;
	}
	
	/**
	 * Sets the screen orientation in 90 degrees steps.
	 * The preprocessing variable "polish.ScreenOrientationCanChangeManually" needs to be set to "true" for supporting this mode.
	 * 
	 * @param degrees the screen orientation in degrees: 90, 180, 270 or 0
	 * @deprecated use Display.getInstance().setScreenOrientation(int) instead
	 * @see Display#setScreenOrientation(int)
	 */
	public void setScreenOrientation( int degrees ) {
		Display.setScreenOrientation(degrees);
	}
	
	/**
	 * Determines whether the given key is really a Canvas.FIRE game action
	 * @param keyCode the key code
	 * @param gameAction the game action
	 * @return true when the gameAction is Canvas.FIRE and the given key is not '5' or a soft key
	 */
	public boolean isGameActionFire(int keyCode, int gameAction)
	{
		return gameAction == FIRE && keyCode != KEY_NUM5
		//#if polish.Bugs.SoftKeyMappedToFire
			&& ( !(isSoftKeyLeft(keyCode, gameAction) || isSoftKeyRight(keyCode, gameAction)))
		//#endif
		;
	}
	
	/**
	 * Checks if the given keycode is the left softkey
	 * @param keyCode the key code
	 * @param gameAction the associated game action
	 * @return true when the key is the left soft key
	 */
	public final boolean isSoftKeyLeft( int keyCode, int gameAction ) {
		if (gameAction == LEFT || gameAction == RIGHT || gameAction == UP || gameAction == DOWN) {
			return false;
		}
		return Display.getInstance().isSoftKeyLeft(keyCode, gameAction);

	}
	
	/**
	 * Checks if the given keycode is the right softkey
	 * @param keyCode the key code
	 * @param gameAction the associated game action
	 * @return true when the key is the right soft key
	 */
	public final boolean isSoftKeyRight( int keyCode, int gameAction ) {
		if (gameAction == LEFT || gameAction == RIGHT || gameAction == UP || gameAction == DOWN) {
			return false;
		}
		return Display.getInstance().isSoftKeyRight(keyCode, gameAction);

	}
	
	/**
	 * Checks if the given keycode is the middle softkey
	 * @param keyCode the key code
	 * @param gameAction the associated game action
	 * @return true when the key is the middle soft key
	 */
	public final boolean isSoftKeyMiddle( int keyCode, int gameAction ) {
		if (gameAction == LEFT || gameAction == RIGHT || gameAction == UP || gameAction == DOWN) {
			return false;
		}
		return Display.getInstance().isSoftKeyMiddle(keyCode, gameAction);
	}
	
	
	/**
	 * Determines if the given keycode belongs to a softkey
	 * 
	 * @param keyCode the keycode
	 * @return true when the key code represents a softkey
	 */
	public boolean isSoftKey( int keyCode ) {
		return Display.getInstance().isSoftKey(keyCode);
	}
	


	/**
	 * Determines if the given keycode belongs to a soft key
	 * @param keyCode the key code
	 * @param gameAction the associated game action
	 * @return true when the given key is a keycode	
	 */
	public boolean isSoftKey(int keyCode, int gameAction)
	{
		return Display.getInstance().isSoftKey(keyCode, gameAction);
	}

	/**
	 * Notifies this screen about the new item that is focused on BlackBerry platforms.
	 * This is only called for BlackBerry platforms - check for the preprocesing
	 * symbol polish.blackberry.
	 * 
	 * @param item the item that has been focused
	 */
	protected void notifyFocusSet( Item item ) {
		//#if polish.blackberry
			Display.getInstance().notifyFocusSet(item);
		//#endif
	}
	
	/**
	 * Determines whether a native UI component is shown for the specified item.
	 * This is currently only implemented for BlackBerry platforms - check for the preprocesing
	 * symbol polish.blackberry.
	 * 
	 * @param item the item that has been focused
	 */
	protected boolean isNativeUiShownFor( Item item ) {
		return Display.getInstance().isNativeUiShownFor(item);
	}
	
	/**
	 * Notifies this screen about the new item with a native componen that is added on BlackBerry platforms.
	 * This is only called for BlackBerry platforms - check for the preprocesing
	 * symbol polish.blackberry.
	 * 
	 * @param item the item with a native component which needs to be displayed all the time.
	 */
	public void addPermanentNativeItem( Item item ) {
		//#if polish.blackberry
			((de.enough.polish.blackberry.ui.BaseScreen)(Object)Display.getInstance()).addPermanentNativeItem(item);
		//#endif
	}
	
	/**
	 * Notifies this screen about an item with a native componen that is removed on BlackBerry platforms.
	 * This is only called for BlackBerry platforms - check for the preprocesing
	 * symbol polish.blackberry.
	 * 
	 * @param item the item with a native component which was displayed all the time.
	 */
	public void removePermanentNativeItem(Item item) {
		//#if polish.blackberry
			((de.enough.polish.blackberry.ui.BaseScreen)(Object)Display.getInstance()).removePermanentNativeItem(item);
		//#endif
	}

	
	
	/********* UI ELEMENT INTERFACE *************************************/

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.UiElement#addRepaintArea(de.enough.polish.ui.ClippingRegion)
	 */
	public void addRepaintArea(ClippingRegion repaintArea)
	{
		repaintArea.addRegion(0, 0, this.screenWidth, getScreenFullHeight() );
	}
	
	/**
	 * Adds a region relative to this screen's content x/y start position.
	 * @param repaintRegion the clipping region
	 * @param x horizontal start relative to this item's content position
	 * @param y vertical start relative to this item's content position
	 * @param width width
	 * @param height height
	 * @see #getScreenContentWidth()
	 * @see #getScreenContentWidth()
	 */
	public void addRelativeToContentRegion(ClippingRegion repaintRegion, int x, int y, int width, int height) {
		repaintRegion.addRegion( 
				this.contentX + x, 
				this.contentY + y,
				width,
				height 
				);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.UiElement#getStyle()
	 */
	public Style getStyle()
	{
		return this.style;
	}

    	
	

	/**
	 * <p>A command listener which forwards commands to the item command listener in case it encounters an item command.</p>
	 *
	 * <p>Copyright Enough Software 2004 - 2009</p>

	 * <pre>
	 * history
	 *        09-Jun-2004 - rob creation
	 * </pre>
	 * @author Robert Virkus, robert@enough.de
	 */
	class ForwardCommandListener implements CommandListener {
		/** the original command listener set by the programmer */
		public CommandListener realCommandListener;

		/* (non-Javadoc)
		 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
		 */
		public void commandAction(Command cmd, Displayable thisScreen) {
			handleCommand( cmd );
		}
		
	}




	/**
	 * Fires an event for this screen and all its components.
	 * This is typically used for triggering animations within screen components like its title or menubar.
	 * Since all screen components fire events, this method should be called within a background thread and never
	 * from within a de.enough.polish.event.EventListener.
	 * 
	 * @param eventName the name of the event 
	 * @param eventData the associated data of the event
	 * @see Screen#fireEventForTitleAndMenubar(String, Object)
	 */
	public void fireEvent(String eventName, Object eventData)
	{
		EventManager.fireEvent(eventName, this, eventData);
		//#if tmp.useScrollBar
			this.scrollBar.fireEvent( eventName, eventData );
		//#endif

		//#ifdef tmp.menuFullScreen
			//#ifdef tmp.useExternalMenuBar
				this.menuBar.fireEvent(eventName, eventData);
			//#else
				this.menuContainer.fireEvent(eventName, eventData);
			//#endif
		//#endif
		if (this.container != null ) {
			this.container.fireEvent(eventName, eventData);
		}
		//#ifdef tmp.usingTitle
			if (this.title != null) {
				this.title.fireEvent(eventName, eventData);
			}
		//#endif
		//#if polish.ScreenInfo.enable
			if (ScreenInfo.item != null && ScreenInfo.isVisible()) {
				ScreenInfo.item.fireEvent(eventName, eventData);
			}
		//#endif
	}

	/**
	 * Fires an event for the title and menubar of the specified screen.
	 * This is typically used for triggering animations for the title and/or menubar.
	 * Since all these screen components fire events, this method should be called within a background thread and never
	 * from within a de.enough.polish.event.EventListener.
	 * 
	 * @param eventName the name of the event 
	 * @param eventData the associated data of the event
	 * @see Screen#fireEvent(String, Object)
	 */
	public void fireEventForTitleAndMenubar(String eventName, Object eventData)
	{
		//#ifdef tmp.menuFullScreen
			//#ifdef tmp.useExternalMenuBar
				UiAccess.fireEvent ( eventName, this.menuBar, eventData);
			//#endif
		//#endif
		//#ifdef tmp.usingTitle
			if (this.title != null) {
				//#ifdef tmp.useExternalMenuBar
					UiAccess.fireEvent ( eventName, this.title, eventData);
				//#endif
			}
		//#endif
	}



	/**
	 * Retrieves the lock object for the paint thread.
	 * You can use this paint lock to synchronize with the paint method of this screen.
	 * 
	 * @return the paint lock object
	 */
	public Object getPaintLock() {
		return this.paintLock;
	}

	/**
	 * Retrieves the root container of this screen
	 * @return  the root container, this might be null for some subclasses
	 */
	public Container getRootContainer() {
		return this.container;
	}
	
	/**
	 * Sets the root container of this screen
	 * @param cont the root container
	 */
	public void setRootContainer(Container cont) {
		if (cont != null) {
			cont.screen = this;
			cont.isFocused = true;
		}
		this.container = cont;
		if (this.isInitialized) {
			requestInit();
		}
	}

	/**
	 * Retrieves the height of the title.
	 * 
	 * @return the height of the title in pixels or 0 when the title is null or is not rendered by J2ME Polish
	 */
	public int getTitleHeight() {
		return this.titleHeight;
	}

	/**
	 * Sets the last interaction time for this screen.
	 * This is being used for stopping animations after an activity timeout.
	 * @param currentTimeMillis the time for the last interaction, typically System.currentTimeMillis()
	 */
	public void setLastInteractionTime(long currentTimeMillis) {
		this.lastInteractionTime = currentTimeMillis;
	}
	
	/**
	 * Sets an UiEventListener for this screen and its items.
	 * @param listener the listener, use null to remove a listener
	 */
	public void setUiEventListener(UiEventListener listener) {
		this.uiEventListener = listener;
	}

	/**
	 * Retrieves the UiEventListener for this screen
	 * @return the listener or null, if none has been registered
	 */
	public UiEventListener getUiEventListener() {
		return this.uiEventListener;
	}
	
	/**
	 * Retrieves the subtitle of this screen.
	 * @return the subtitle, may be null
	 * @see #setSubTitle(Item)
	 */
	public Item getSubTitleItem() {
		return this.subTitle;
	}
	
	/**
	 * Sets a new screen initializer listener.
	 * 
	 * @param listener the screen initialization listener
	 * @see #getScreenInitializerListener()
	 */
	public void setScreenInitializerListener(ScreenInitializerListener listener) {
		this.screenInitializerListener = listener;
	}

	/**
	 * Sets a new screen initializer listener.
	 * 
	 * @return listener the screen initialization listener
	 * @see #setScreenInitializerListener(ScreenInitializerListener)
	 */
	public ScreenInitializerListener getScreenInitializerListener() {
		return this.screenInitializerListener;
	}
	
	//#if polish.useNativeGui
		/**
		 * Species a native implementation for this screen.
		 * This method is only available when the preprocessing variable polish.useNativeGui is set to true.
		 * @param nativeScreen the native implementation
		 */
		public void setNativeScreen( NativeScreen nativeScreen ) {
			this.nativeScreen = nativeScreen;
		}
	//#endif
	
	//#if polish.useNativeGui
		/**
		 * Species a native implementation for this screen.
		 * This method is only available when the preprocessing variable polish.useNativeGui is set to true.
		 * @return the native implementation, can be null
		 */
		public NativeScreen getNativeScreen() {
			return this.nativeScreen;
		}
	//#endif


	/**
	 * Adds a command separator to the menu of this screen.
	 * @param priority the priority of the sepator, same as for Command
	 */
	public void addCommandSeparator(int priority) {
		//#if polish.css.style.menuSeparator
			//#style menuSeparator?
			addCommandSeparator( priority );
		//#else
			addCommandSeparator( priority, null );
		//#endif
	}
	
	/**
	 * Adds a command separator to the menu of this screen.
	 * @param priority the priority of the sepator, same as for Command
	 * @param separatorStyle the style of the separator
	 */
	public void addCommandSeparator(int priority, Style separatorStyle) {
		addCommand( new Command(null, Command.SEPARATOR, priority, separatorStyle));
	}



//#ifdef polish.Screen.additionalMethods:defined
	//#include ${polish.Screen.additionalMethods}
//#endif
	
	
	
	
	

}