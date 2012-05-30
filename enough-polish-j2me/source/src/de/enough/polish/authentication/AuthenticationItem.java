package de.enough.polish.authentication;

import java.io.IOException;

import javax.microedition.io.StreamConnection;

import de.enough.polish.browser.TagHandler;
import de.enough.polish.browser.html.HtmlBrowser;
import de.enough.polish.browser.html.HtmlTagHandler;
import de.enough.polish.browser.protocols.HttpProtocolHandler;
import de.enough.polish.io.RedirectHttpConnection;
import de.enough.polish.ui.Style;

public class AuthenticationItem 
extends HtmlBrowser
{
	
	private final AuthenticationProcess authenticationProcess;
	private boolean isAuthenticationInProcess;

	public AuthenticationItem( AuthenticationProcess process ) {
		this( process, null);
	}

	public AuthenticationItem( AuthenticationProcess process, Style style ) {
		super( new HtmlTagHandler(), null, process.getCookieManager(), style);
		HttpProtocolHandler.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:11.0) Gecko/20100101 Firefox/11.0");
		addProtocolHandler( new AuthenticationHttpProtocolHandler("http") );
		addProtocolHandler( new AuthenticationHttpProtocolHandler("https") );
		this.authenticationProcess = process;
		TagHandler handler = getTagHandler("input");
		if (handler instanceof HtmlTagHandler) {
			//#style authenticationInput?
			((HtmlTagHandler)handler).setTextInputStyle();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.Item#showNotify()
	 */
	public void showNotify() {
		super.showNotify();
		if (!this.isAuthenticationInProcess) {
			startAuthentication();
		}
	}

	/**
	 * Starts the authentication process.
	 * This process is started automatically when this item is being shown, so this method is only useful if you would like to restart the authentication process at a later point.
	 */
	public void startAuthentication() {
		this.isAuthenticationInProcess = true;
		String url = this.authenticationProcess.getStartUrl(); 
		go(url);
	}

	
	private class AuthenticationHttpProtocolHandler extends HttpProtocolHandler {
		public AuthenticationHttpProtocolHandler(String protocol) {
			super(protocol);
		}
		
		/*
		 * (non-Javadoc)
		 * @see de.enough.polish.browser.protocols.HttpProtocolHandler#getConnection(java.lang.String)
		 */
		public StreamConnection getConnection(String url) throws IOException {
			RedirectHttpConnection connection =  (RedirectHttpConnection) super.getConnection(url);
			connection.setRedirectListener(AuthenticationItem.this.authenticationProcess);
			return connection;
		}
		
		
	}
}
