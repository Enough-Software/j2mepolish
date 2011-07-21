/*
 * Created on Dec 28, 2006 at 4:08:53 AM.
 * 
 * Copyright (c) 2006 Robert Virkus / Enough Software
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
package de.enough.polish.rmi;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * <p>A Servlet that tunnels any incoming requests to the RemoteServer, which in turn forwards any calls to the actual implementation object.</p>
 * <p>This servlet requires the "polish.rmi.server" init parameter within its web.xml configuration file. 
 *    You can add your own configuration variables by implementing the method <code>public void init( java.util.Map config )</code> or 
 *    <code>public void init( javax.servlet.ServletConfig config )</code>. 
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        Jan 19, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class RemoteHttpServlet extends HttpServlet {
	
	private static final long serialVersionUID = -8191803948220688172L;
	
	/** Contains a HttServletRequest for each thread. */
	protected final Map requestsByThread;

	private RmiResolver resolver;

	/** Creates a new RemoteHttpServlet */
	public RemoteHttpServlet() {
		super();
		this.requestsByThread = new HashMap();
	}
	
	
	
	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	public void init(ServletConfig cfg) throws ServletException {
		super.init(cfg);
		Remote implementation = null;
		String implementationClassName = cfg.getInitParameter("polish.rmi.server");
		if (implementationClassName == null) {
			if (this instanceof Remote) {
				implementation = (Remote) this;
				this.resolver = new RmiResolver( implementation );
				return;
			} else {
				throw new ServletException("no \"polish.rmi.server\" parameter given.");
			}
		}
		Class implementationClass = null;
		try {
			implementationClass = Class.forName( implementationClassName );
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new ServletException("\"polish.rmi.server\" cannot be resolved: " + e.toString() );
		}
		try {
			implementation = (Remote) implementationClass.newInstance();
			if (implementation.getClass().equals(getClass())){
				implementation = (Remote) this;
				this.resolver = new RmiResolver( implementation );
				return;
			}
			try {
				// check out if there is a init( java.util.Map ) method:
				Method configureMethod = implementationClass.getMethod("init", new Class[]{ Map.class } );
				configureMethod.invoke( implementation, new Object[]{ convertConfigurationToMap( cfg ) } );
			} catch (NoSuchMethodException e) {
				// check out if there is a init( ServletConfig ) method:
				try {
					Method configureMethod = implementationClass.getMethod("init", new Class[]{ ServletConfig.class } );
					configureMethod.invoke( implementation, new Object[]{ cfg } );
				} catch (NoSuchMethodException e1) {
					System.out.println("Note: if you want to configure your " + implementationClassName + " implementation upon startup, implement public void configure( java.util.Map ) or public void configure( javax.servlet.ServletConfig ).");
				}
			}
			this.resolver = new RmiResolver( implementation );
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new ServletException("\"polish.rmi.server\" cannot be instatiated: " + e.toString() );
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new ServletException("\"polish.rmi.server\" cannot be accessed: " + e.toString() );
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException("\"polish.rmi.server\" cannot be initialized: " + e.toString() );
		}
	}


	/**
	 * Stores all servlet configurations values into a map.
	 * 
	 * @param cfg the servlet configuration
	 * @return a map with all init parameters taken from the ServletConfig
	 */
	protected Map convertConfigurationToMap(ServletConfig cfg) {
		Map map = new HashMap();
		Enumeration enumeration = cfg.getInitParameterNames();
		while (enumeration.hasMoreElements()) {
			String paramName = (String) enumeration.nextElement();
			String paramValue = cfg.getInitParameter( paramName );
			map.put( paramName, paramValue );
		}
		return map;
	}



	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Thread thread = Thread.currentThread();
		try {
			this.requestsByThread.put( thread, request );
			DataInputStream in = new DataInputStream( request.getInputStream() );
			//DataOutputStream out = new DataOutputStream( response.getOutputStream() );
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream( byteOut );
			this.resolver.process(in, out);
			byte[] responseData = byteOut.toByteArray();
			response.setContentLength( responseData.length );
			response.setContentType( "application/octet-stream" );
			response.getOutputStream().write(responseData, 0, responseData.length );
			response.flushBuffer();
		} catch (IOException e) {
			e.printStackTrace();
			try {
				response.getWriter().write("this is the J2ME Polish remote server waiting for input.");
				response.flushBuffer();
			} catch (IOException nestedE) {
				nestedE.printStackTrace();
			}
		} finally {
			this.requestsByThread.remove( thread );
		}
	}
	
	
	
	/**
	 * Retrieves the session and creates it if necessary.
	 * 
	 * @return the session for the current thread.
	 */
	public HttpSession getSession() {
		return getSession( true );
	}
	
	/**
	 * Retrieves the session and creates it if necessary.
	 *
	 * @param create true when the session should be created if it does not yet exist.
	 * @return the session for the current thread.
	 */
	public HttpSession getSession(boolean create) {
		return getRequest().getSession(create);
	}
	
	/**
	 * Retrieves the request for the current thread.
	 * 
	 * @return the HttpServletRequest for the current thread.
	 */
	public HttpServletRequest getRequest() {
		Thread thread = Thread.currentThread();
		return (HttpServletRequest) this.requestsByThread.get( thread );
	}
	

}
