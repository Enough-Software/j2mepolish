//#condition polish.usePolishGui
/*
 * Created on 15-Mar-2004 at 10:52:57.
 *
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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

import de.enough.polish.event.EventListener;
import de.enough.polish.event.EventManager;
import de.enough.polish.util.ArrayList;

/**
 * <p>Is used to animate Screens, Backgrounds and Items.</p>
 * <p>
 * 	You can specify the animation interval in milliseconds in the variables section of
 *  your build.xml script.
 *  Example:
 *  <pre>
 *  <variables>
 *		<variable name="polish.animationInterval" value="200" />
 *	</variables>
 * 	</pre>
 *  sets the interval to 200 ms. When not specified, the default interval
 *  of 100 ms will be used.
 * </p>
 * <p>Copyright Enough Software 2004 - 2009</p>

 * <pre>
 * history
 *        15-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class AnimationThread extends Thread
//#if polish.css.animations
	implements EventListener
//#endif
{
	/**
	 * Event that is triggered when entering the idle mode.
	 * The idle mode is triggered after 3 minutes or - when defined - after polish.Animation.MaxIdleTime.
	 * The event is only triggered when polish.Animation.fireIdleEvents is set to true in your build.xml.
	 */
	public static final String EVENT_IDLE_MODE_ON = "idle-on";
	/**
	 * Event that is triggered when leaving the idle mode.
	 * The idle mode is left when the user starts interacting with the application again.
	 * The event is only triggered when polish.Animation.fireIdleEvents is set to true in your build.xml.
	 */
	public static final String EVENT_IDLE_MODE_OFF = "idle-off";

	//#ifdef polish.animationInterval:defined
		//#= public final static long ANIMATION_INTERVAL = ${time(polish.animationInterval)};
	//#else
		/**
		 * The interval that the animation thread is sleeping between each animation phase.
		 * You can specify the polish.animationInterval preprocessing variable for changing the default value of 50ms, e.g.
		 * <pre>
		 * &lt;variable name=&quot;polish.animationInterval&quot; value=&quot;30ms&quot; /&gt;
		 * </pre>
		 */
		public final static long ANIMATION_INTERVAL = 50L;
	//#endif
	private static final int ANIMATION_YIELD_INTERVAL = Integer.MIN_VALUE;
	//#ifdef polish.sleepInterval:defined
		//#= private final static long SLEEP_INTERVAL = ${time(polish.sleepInterval)};
	//#else
		private final static long SLEEP_INTERVAL = 1000L;
	//#endif
	//#ifdef polish.minAnimationInterval:defined
		//#= private final static long ANIMATION_MIN_INTERVAL = ${time(polish.minAnimationInterval)};
	//#else
		private final static long ANIMATION_MIN_INTERVAL = 10L;
	//#endif
	protected static boolean releaseResourcesOnScreenChange;
	private static ArrayList animationList;
	//#if polish.Animation.fireIdleEvents
		private static String[] idleEvents;
		private static long[] idleTimeouts;
	//#endif
	//#if polish.Animation.MaxIdleTime:defined
		//#= private final static long ANIMATION_TIMEOUT = ${ time(polish.Animation.MaxIdleTime)};
	//#else
		private final static long ANIMATION_TIMEOUT = 3 * 60 * 1000; // after 3 minutes of inactivity stop the animations
	//#endif

	/**
	 * the total delta (animation, serviceRepaints, sleep) of the last frame
	 */
	long totalDelta = -1;

	/**
	 * Creates a new animation thread.
	 */
	public AnimationThread() {
		//#if polish.cldc1.1 && polish.debug.error
			super("AnimationThread");
		//#else
			//# super();
		//#endif
		//#if polish.css.animations
			EventManager.getInstance().addEventListener(null, this);
		//#endif
	}

	/**
	 * Animates the current screen.
	 */
	public void run() {
		long sleeptime = ANIMATION_INTERVAL;
		long currentTime = 0;
		long usedTime = 0 ;
		int i = 0;

		Object[] animationItems = null ;
		Animatable animatable = null ;
		Displayable d = null ;

		ClippingRegion repaintRegion = new ClippingRegion();
//		int animationCounter = 0;
		long lastIdleTime = 0;
		int sleepIntervalCounter = 0;
		
		while ( true ) {
			try {
				Screen screen = StyleSheet.currentScreen;
				//System.out.println("AnimationThread: animating " + screen + ", current=" + StyleSheet.display.getCurrent());
				if (screen != null
						//#if polish.css.repaint-previous-screen
						&& screen.isShown()
						//#endif
				) {
					currentTime = System.currentTimeMillis();
					long idleTime = currentTime - screen.lastInteractionTime;
					if ( idleTime < ANIMATION_TIMEOUT ) {

						
						screen.animate( currentTime, repaintRegion );

						if (animationList != null) {
							animationItems = animationList.getInternalArray();
							for (i = 0; i < animationItems.length; i++) {
								animatable = (Animatable) animationItems[i];
								if (animatable == null) {

									break;
								}
								//System.out.println("animating " + animatable);

								

								animatable.animate(currentTime, repaintRegion);

								//#debug repaint
								System.out.println("called animate for " + animatable + " : " + repaintRegion);
							}
						}

						
						if (repaintRegion.containsRegion()) {
							//#debug repaint
							System.out.println("repainting for " + repaintRegion);

							//System.out.println("AnimationThread: screen needs repainting");
							//#debug debug
							System.out.println("triggering repaint for screen " + screen + ", is shown: " + screen.isShown() );
							//#if polish.Bugs.fullRepaintRequired
								screen.requestRepaint();
							//#else
								//System.out.println("repaint for " + repaintRegion.getX() + ", " + repaintRegion.getY() + ", " + repaintRegion.getWidth() + ", " + repaintRegion.getHeight()  );
								screen.requestRepaint( repaintRegion.getX(), repaintRegion.getY(), repaintRegion.getWidth() + 1, repaintRegion.getHeight() + 1 );
							//#endif
							repaintRegion.reset();
							screen.serviceRepaints();
						}


						//#if polish.Animation.fireIdleEvents
							if (sleeptime == SLEEP_INTERVAL) {
								EventManager.fireEvent( EVENT_IDLE_MODE_OFF, this, null);
								lastIdleTime = 0;
							}
						//#endif
						usedTime = System.currentTimeMillis() - currentTime;
						if (usedTime >= ANIMATION_INTERVAL) {
							sleeptime = ANIMATION_YIELD_INTERVAL;
						} else {
							sleeptime = ANIMATION_INTERVAL - usedTime;
						}
					} else if (sleeptime != SLEEP_INTERVAL) {
						sleeptime = SLEEP_INTERVAL;
						sleepIntervalCounter = 0;
						//#if polish.Animation.fireIdleEvents
							EventManager.fireEvent( EVENT_IDLE_MODE_ON, this, null);
						//#endif
					} else {
						// we are in idle mode and are sleeping for the SLEEP_INTERVAL...
						sleepIntervalCounter++;
						if (sleepIntervalCounter > 20) {
							sleepIntervalCounter = 0;
							Display instance = Display.getInstance();
							if (instance != null) {
								synchronized (instance) {
									instance.emitNotifyOnUserEvents(true);
									instance.wait();
								}
								continue;
							}
						}
					}
					//#if polish.Animation.fireIdleEvents
						if (idleTime > 1000) {
							long[] timeouts = idleTimeouts;
							if (timeouts != null) {
								String[] events = idleEvents;
								for (int j = 0; j < timeouts.length; j++) {
									long timeout = timeouts[j];
									if (timeout <= idleTime && timeout > lastIdleTime) {
										EventManager.fireEvent( events[j], this, new Long(idleTime) );
									}
								}
								lastIdleTime = idleTime;
							}
						}
					//#endif

					if (releaseResourcesOnScreenChange) {
						d = StyleSheet.display.getCurrent();
						if (d != screen) {
							StyleSheet.currentScreen = null;
						}
					}

					if(sleeptime == ANIMATION_YIELD_INTERVAL) {
						//#if polish.vendor.sony-ericsson
							Thread.yield();
						//#else
							// on other platforms (most notably Nokia Series 60), yield doesn't work so well, so we better sleep:
							Thread.sleep(ANIMATION_MIN_INTERVAL);
						//#endif
						
					} else {
						Thread.sleep(sleeptime);
					}

					this.totalDelta = (System.currentTimeMillis() - currentTime) - ANIMATION_INTERVAL;
				} else {
					// there is no currently shown screen:
					if (releaseResourcesOnScreenChange) {
						StyleSheet.releaseResources();
						releaseResourcesOnScreenChange = false;
					}
					sleeptime = SLEEP_INTERVAL;
					
					Thread.sleep(sleeptime);
				}
			} catch (InterruptedException e) {
				// ignore
			} catch (Exception e) {
				//#debug error
				System.out.println("unable to animate screen" + e );
			}

		}

	}

	/**
	 * Returns the total delta of the last frame
	 * @return the total delta of the last frame
	 */
	public long getTotalDelta() {
		return this.totalDelta;
	}

	/**
	 * Adds the given item to list of items that should be animated.
	 * Typically an item adds itself to the list in the showNotify() method and
	 * then de-registers itself in the hideNotify() method.
	 *
	 * @param item the item that needs to be animated regardless of it's focused state etc.
	 * @see #removeAnimationItem(Animatable)
	 */
	public static void addAnimationItem( Animatable item ) {
		if (animationList == null) {
			animationList = new ArrayList();
		}
		if (!animationList.contains(item)) {
			animationList.add( item );
		}
	}

	//#if polish.LibraryBuild
	/**
	 * Adds the given item to list of items that should be animated.
	 * Typically an item adds itself to the list in the showNotify() method and
	 * then de-registers itself in the hideNotify() method.
	 *
	 * @param item the item that needs to be animated regardless of it's focused state etc.
	 * @see #removeAnimationItem(javax.microedition.lcdui.CustomItem)
	 */
	public static void addAnimationItem( javax.microedition.lcdui.CustomItem item) {
		// ignore
	}
	//#endif

	/**
	 * Removes the given item to list of items that should be animated.
	 * Typically an item adds itself to the list in the showNotify() method and
	 * then de-registers itself in the hideNotify() method.
	 *
	 * @param item the item that does not need to be animated anymore
	 * @see #addAnimationItem(Animatable)
	 */
	public static void removeAnimationItem( Animatable item ) {
		if (animationList != null) {
			animationList.remove(item);
		}
	}

	//#if polish.LibraryBuild
	/**
	 * Removes the given item to list of items that should be animated.
	 * Typically an item adds itself to the list in the showNotify() method and
	 * then de-registers itself in the hideNotify() method.
	 *
	 * @param item the item that does not need to be animated anymore
	 * @see #addAnimationItem(javax.microedition.lcdui.CustomItem)
	 */
	public static void removeAnimationItem( javax.microedition.lcdui.CustomItem item) {
		// ignore
	}
	//#endif
	
	/**
	 * Requests that the specified event is fired using the EventManager after the user is idle for given timeout.
	 * Note that this feature needs to be activated using the polish.Animation.fireIdleEvents preprocessing variable:
	 * <pre>
	 * &lt;variable name=&quot;polish.Animation.fireIdleEvents&quot; value=&quot;true&quot; /&gt;
	 * </pre>
	 * 
	 * @param eventName the name of the event
	 * @param timeout the timeout in milliseconds with a minum of 1 second (that is 1000)
	 * @see de.enough.polish.event.EventListener
	 * @see #removeIdleEvent(String)
	 * @throws IllegalArgumentException when eventName is null or timeout is less than 1000.
	 */
	public static void addIdleEvent( String eventName, long timeout) {
		//#if polish.Animation.fireIdleEvents
			if (eventName == null || timeout < 1000) {
				throw new IllegalArgumentException();
			}
			if (idleEvents == null) {
				idleEvents = new String[] { eventName };
				idleTimeouts = new long[]{ timeout };
			} else {
				int size = idleEvents.length;
				String[] events = new String[ size + 1];
				System.arraycopy( idleEvents, 0, events, 0, size );
				events[size] = eventName;
				long[] timeouts = new long[ size + 1 ];
				System.arraycopy( idleTimeouts, 0, timeouts, 0, size );
				timeouts[size] = timeout;
				idleTimeouts = timeouts;
				idleEvents = events;
			}
		//#else
			//# throw new IllegalStateException();
		//#endif
	}
	
	/**
	 * Removes an idle event 
	 * 
	 * @param eventName the name of the event (should be unique, otherwise all events with that name are removed)
	 * @see #addIdleEvent(String, long)
	 */
	public static void removeIdleEvent( String eventName) {
		//#if polish.Animation.fireIdleEvents
			if (idleEvents != null) {
				int size = idleEvents.length;
				if (size == 1) {
					if (eventName.equals(idleEvents[0])) {
						idleEvents = null;
						idleTimeouts = null;
					}
				} else {
					String[] events = new String[size-1];
					long[] timeouts = new long[size -1];
					int counter = 0;
					for (int i=0; i<size; i++) {
						String existing = idleEvents[i];
						if (eventName.equals(existing)) {
							continue;
						}
						if (counter < size -1) {
							events[counter] = existing;
							timeouts[counter] = idleTimeouts[i];
						}
						counter++;
					}
					if (counter < size - 1) {
						// more than one event have been removed:
						String[] events2 = new String[counter];
						long[] timeouts2 = new long[counter];
						System.arraycopy( events, 0, events2, 0, counter );
						System.arraycopy( timeouts, 0, timeouts2, 0, counter );
						events = events2;
						timeouts = timeouts2;
					}
					idleTimeouts = timeouts;
					idleEvents = events;
				}
			}
		//#else
			//# throw new IllegalStateException();
		//#endif
	}
	
	/**
	 * Retrieves the registered idle event names
	 * @return the registered idle event names, null if none have been registered or when the polish.Animation.fireIdleEvents preprocessing variable is not set to true.
	 * @see #addIdleEvent(String, long)
	 */
	public static String[] getIdleEventNames() {
		String[] events = null;
		//#if polish.Animation.fireIdleEvents
			events = idleEvents;
		//#endif
		return events;
	}
	
	/**
	 * Retrieves the registered idle event timeouts
	 * @return the registered idle event timeouts in milliseconds, null if none have been registered or when the polish.Animation.fireIdleEvents preprocessing variable is not set to true.
	 * @see #addIdleEvent(String, long)
	 */
	public static long[] getIdleEventTimeouts() {
		long[] timeouts = null;
		//#if polish.Animation.fireIdleEvents
			timeouts = idleTimeouts;
		//#endif
		return timeouts;
	}

	//#if polish.css.animations
	/**
	 * Adds a new CSS animation into the processing queue.
	 * @param animation the animation
	 * @param item the corresponding UI element (either screen or item)
	 */
	public static void addAnimation(CssAnimation animation, UiElement item)
	{
		if (animationList == null) {
			animationList = new ArrayList();
		}
		Object[] existingAnimations = animationList.getInternalArray();
		for (int i = 0; i < existingAnimations.length; i++)
		{
			Object anim = existingAnimations[i];
			if (anim == null) {
				break;
			}
			if (anim instanceof CssAnimationRun) {
				CssAnimationRun run = (CssAnimationRun) anim;
				if (run.uiElement == item && run.animation.cssAttributeId == animation.cssAttributeId) {
					run.exitOrRepeat( true, 0 );
				}
			}
		}
		CssAnimationRun cssAnimationRun = new CssAnimationRun( animation, item );
		if (animation.duration != 0 || animation.delay != 0) {
			animationList.add( cssAnimationRun);
		}
	}
	//#endif

	/* (non-Javadoc)
	 * @see de.enough.polish.event.EventListener#handleEvent(java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public void handleEvent(String name, Object source, Object data)
	{
		//#if polish.css.animations
			if (source instanceof UiElement) {
				UiElement uiElement = (UiElement) source;
				Style style = uiElement.getStyle();
				CssAnimation[] animations = style != null ?  style.getAnimations() : null;
				if (animations != null) {
					//System.out.println(name + ": checking animations from style " +  style.name + " of "+ uiElement );
					for (int i = 0; i < animations.length; i++)
					{
						CssAnimation animation = animations[i];
						//System.out.println( name + ": trigger=" + animation.triggerEventId);
						if (animation.triggerEventId.equals(name)) {
							//System.out.println("found animation in " + style.name + " for trigger " + name + " from " + animation.startValue + " to " + animation.endValue + " for " + uiElement);
							if (animation.delay == 0) {
								if (animation.duration == 0) {
									animation.setEndValue( style );
								} else {
									animation.setStartValue( style );
								}
							}
							if (animation.repeat == -2) {
								// only use this animation once in a lifetime of the application:
								animation.triggerEventId = "";
							}
							addAnimation( animation, uiElement);
						}
					}
				}
			}
		//#endif
	}

	//#if polish.css.animations
	private static class CssAnimationRun implements Animatable{
		private final CssAnimation animation;
		private Object lastValue;
		private UiElement uiElement;
		private Style style;
		private long startTime;
		private final Style uiElementStyle;
		private boolean isStarted;
		private int repeats;

		public CssAnimationRun( CssAnimation animation, UiElement item ) {
			this.animation = animation;
			this.startTime = System.currentTimeMillis();
			this.uiElement = item;
			this.uiElementStyle = item.getStyle();
			this.lastValue = this.uiElementStyle.getObjectProperty( animation.cssAttributeId );
			this.style = new Style();
			//this.style.font = this.itemStyle.font;
			//this.style.name = item.getStyle().name;
			this.isStarted = (animation.delay == 0);
			this.repeats = animation.getRepeat();
			if (this.isStarted) {
				if (animation.duration != 0) {
					this.style.addAttribute( animation.cssAttributeId, animation.getStartValue() );
				} else {
					this.style.addAttribute( animation.cssAttributeId, animation.getEndValue() );
					if (animation.fireEvent != null) {
						EventManager.fireEvent(animation.fireEvent, item, animation );
					}
				}
				item.setStyle(this.style, false);
			} else {
				this.style.addAttribute( animation.cssAttributeId, this.lastValue);
			}
		}

		public void animate( long currentTime, ClippingRegion repaintArea ) {
			//return;

			this.uiElement.addRepaintArea(repaintArea);
			if (!this.isStarted) {
				if (currentTime - this.startTime >= this.animation.delay) {
					this.isStarted = true;
					this.startTime = currentTime;
					if (this.animation.duration != 0) {
						this.animation.setStartValue(this.style);
					} else {
						this.animation.setEndValue(this.style);
					}
					if (this.uiElement.getStyle() == this.uiElementStyle) {
						this.uiElement.setStyle( this.style, false );
						this.uiElement.addRepaintArea(repaintArea);
					}
					if (this.animation.duration == 0) {
						exitOrRepeat(true, currentTime);
					}
				}
				return;
			}
			long passedTime = currentTime - this.startTime;
			Object newValue = this.animation.animate( this.style, this.lastValue, passedTime );
			boolean animate = false;
			if (newValue == CssAnimation.ANIMATION_FINISHED) {
				exitOrRepeat( false, currentTime );
				this.animation.setEndValue( this.uiElementStyle );
				this.animation.setEndValue( this.style );
				animate = true;
				//System.out.println("finished animation in " + (passedTime) + "ms, expected=" + this.animation.duration + ", value=" + this.itemStyle.getObjectProperty(this.animation.cssAttributeId) + ", for item " + this.item);
			} else if (newValue != null) {
				//System.out.println("setting " + animation.cssAttributeId + " to " + newValue + " for " + this.itemStyle.name + " and item " +  this.item );
				if (newValue != this.lastValue) {
					this.style.addAttribute(this.animation.cssAttributeId, newValue);
					this.lastValue = newValue;
					animate = true;
				}
			} else {
				this.style.removeAttribute( this.animation.cssAttributeId );
				animate = true;
			}
			if (this.uiElement.getStyle() == this.uiElementStyle) {
				if (animate) {
					this.uiElement.setStyle( this.style, false );
					this.uiElement.addRepaintArea(repaintArea);
				}
			} else {
				// style has changed:
				//System.out.println("style changed to " + this.item.getStyle().name  + ", value=" + this.itemStyle.getObjectProperty(this.animation.cssAttributeId) + ", for item " + this.item );
				exitOrRepeat( true, currentTime );
			}
		}

		/**
		 * Finishes or repeats the current animation.
		 * @param force true when it should be finished in any case
		 * @param currentTime the current time in ms
		 */
		protected void exitOrRepeat(boolean force, long currentTime)
		{
			//System.out.println("exit or repeat: force=" + force + ", repeat=" + this.repeats + ", passedTime=" + (currentTime - this.startTime) );
			if (force || this.repeats == 0 || this.repeats  == -2) {
				AnimationThread.animationList.remove(this);
				if (this.animation.fireEvent != null) {
					if (this.uiElement instanceof Item) {
						UiAccess.fireEvent( this.animation.fireEvent, ((Item)this.uiElement).getScreen(), this.animation );
					} else if (this.uiElement instanceof Screen) {
						UiAccess.fireEvent( this.animation.fireEvent, (Screen)this.uiElement, this.animation );
					} else {
						EventManager.fireEvent(this.animation.fireEvent, this.uiElement, this.animation );
					}
				}
			} else  {
				if (this.repeats != -1) {
					this.repeats--;
				}
				//System.out.println("repeating animation: repeat=" + this.repeats );
				if (this.animation.delay != 0) {
					this.isStarted = false;
				}
				this.startTime = currentTime;
			}
		}

	}
	//#endif

}
