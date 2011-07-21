package de.enough.polish.ant;

/**
 * <p>Allows to listen for build events like reaching specific phases etc.</p>
 * <p>
 * This can be useful for displaying information or getting settings that are established
 * during the build time (like the definition of the preprocessing source folder).
 * <br />
 * You can add build listeners in several ways:
 * </p>
 * <ol>
 *   <li>use nested &lt;buildlistener&gt; elements:
 *     <pre>
 *     &lt;j2mepolish&gt;
 *     	&lt;buildlistener class=&quot;com.company.j2mepolish.BuildListenerImpl&quot; /&gt;
 *     	&lt;buildlistener class=&quot;com.othercompany.j2me.MyBuildListenerImpl&quot; /&gt;
 *     	...
 *     &lt;/j2mepolish&gt;
 *     </pre>
 *   </li>
 *   <li>define the &quot;polish.build.listener&quot; Ant property:
 *     <pre>
 *     &lt;property name=&quot;polish.build.listener&quot; 
 *     value=&quot;com.company.j2mepolish.BuildListenerImpl, com.othercompany.j2me.MyBuildListenerImpl&quot; /&gt;
 *     </pre>
 *   </li>
 * </ol>
 * <p>
 * You can define several listeners at once.
 * </p>
 * 
 * 
 * @author robert virkus
 *
 */
public interface PolishBuildListener {
	
	/**
	 * The name of the Ant property for defining one or several classnames at the J2ME Polish task.
	 */
	String ANT_PROPERTY_NAME = "polish.build.listener";
	
	/**
	 * Used for setting up the directory that contains the preprocessed source code.
	 * The source code directory is given as a java.io.File object.
	 */
	String EVENT_PREPROCESS_SOURCE_DIR = "evt.preprocess.sourcedir";

	/**
	 * Notifies the listener about the end of a device build.
	 * The environment is given as the data
	 */
	String EVENT_BUILD_FINISHED = "evt.build.finished";

	/**
	 * Notifies the implementation about a build event.
	 * Refer to the constants defined in this interface for more information about the possible payload/data of events.
	 * 
	 * @param name the name of the build event
	 * @param data any value that might be associated with the event
	 */
	void notifyBuildEvent( String name, Object data );

}
