package de.enough.polish.facebook;

import java.util.Hashtable;

import de.enough.polish.authentication.AuthenticationListener;
import de.enough.polish.authentication.AuthenticationProcess;
import de.enough.polish.io.CookieManager;
import de.enough.polish.io.RedirectHttpConnection;
import de.enough.polish.io.RmsStorage;
import de.enough.polish.util.TextUtil;

/**
 * Implements the Facebook OAuth 2.0 based authentication process.
 * 
 * @author Robert Virkus, j2mepolish@enough.de
 * @see de.enough.polish.authentication.AuthenticationItem
 * @see AuthenticationProcess
 */
public abstract class BaseFacebookAuthenticationProcess implements AuthenticationProcess {
	
	protected static final String KEY_COOKIE_MANAGER = "authcm";
	protected final String facebookAppId;
	protected final String facebookAppRedirectUrl;
	protected final String facebookAppPermissions;
	protected AuthenticationListener authenticationListener;
	protected CookieManager cookieManager;
	protected RmsStorage storage;
	protected boolean isRedirectEncountered;


	
	/**
	 * Creates a new authentication process
	 * @param facebookAppId the application ID
 	 * @param facebookAppRedirectUrl the registered redirect URL
	 * @param listener the authentication listener
	 */
	public BaseFacebookAuthenticationProcess(String facebookAppId,
			String facebookAppRedirectUrl,
			AuthenticationListener listener
			) 
	{
		this( facebookAppId, facebookAppRedirectUrl, null, listener);
	}
	
	/**
	 * Creates a new authentication process
	 * @param facebookAppId the application ID
 	 * @param facebookAppRedirectUrl the registered redirect URL
 	 * @param optional comma separated list of Facebook permissions that should be authorized, compare http://developers.facebook.com/docs/authentication/permissions/
	 * @param listener the authentication listener
	 */
	public BaseFacebookAuthenticationProcess(String facebookAppId,
			String facebookAppRedirectUrl,
			String permissions, 
			AuthenticationListener listener
			) 
	{
		this.facebookAppId = facebookAppId;
		this.facebookAppRedirectUrl = facebookAppRedirectUrl;
		this.facebookAppPermissions = permissions;
		this.authenticationListener = listener;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.authentication.AuthenticationProcess#getStartUrl()
	 */
	public String getStartUrl() {
		String url = "https://m.facebook.com/dialog/oauth?client_id=" + this.facebookAppId
				+ "&redirect_uri=" + TextUtil.encodeUrl( this.facebookAppRedirectUrl );
		if (this.facebookAppPermissions != null) {
			url +=  "&scope=" + this.facebookAppPermissions;
		}
		url += "&state=" + getState();
		return url;
	}
	
	/**
	 * Retrieves the state.
	 * Subclasses can override this method to implement a specific state; otherwise a String representation of the current time in ms is being used.
	 * @return a unique state for the authentication
	 */
	protected String getState() {
		return Long.toString(System.currentTimeMillis());
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.authentication.AuthenticationProcess#getCookieManager()
	 */
	public CookieManager getCookieManager() {
		CookieManager manager = this.cookieManager;
		if (manager == null) {
			// try to load it from RMS:
			if (this.storage == null) {
				this.storage = new RmsStorage();
			}
			try {
				manager = (CookieManager) this.storage.read(KEY_COOKIE_MANAGER);
			} catch (Exception e) {
				e.printStackTrace();
				//#debug info
				System.out.println("Unable to load cookie manager" + e);
			}
			if (manager == null) {
				manager = new CookieManager();
				this.cookieManager = manager;
			}
		}
		return manager;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.RedirectListener#onRedirect(java.lang.String)
	 */
	public String onRedirect(String url) {
		if (!url.startsWith(this.facebookAppRedirectUrl)) {
			return url;
		}
		this.isRedirectEncountered = true;
		Hashtable table = TextUtil.parseGetParameters(url);
		String accessCode = (String) table.get("code");
		if (accessCode == null) {
			this.authenticationListener.onAuthenticationFailure(AuthenticationListener.REASON_DENIED, "no access code in redirect url " + url);
		} else {
			retrieveAccessToken(accessCode);
		}
		
		return null;
	}
	
	public abstract void retrieveAccessToken(String accessCode);

	/**
	 * Writes the cookie manager to the local RMS
	 * @return true when the saving was successful
	 */
	protected boolean saveCookieManager() {
		try {
			if (this.storage == null) {
				this.storage = new RmsStorage();
			}
			this.storage.save(this.cookieManager, KEY_COOKIE_MANAGER);
			return true;
		} catch (Exception e) {
			//#debug error
			System.out.println("Unable to persist cookie manager state" + e);
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.authentication.AuthenticationProcess#refreshAccess(de.enough.polish.authentication.AuthenticationListener)
	 */
	public void refreshAccess(AuthenticationListener listener) {
		this.authenticationListener = listener;
		RefreshThread thread = new RefreshThread();
		thread.start();
	}
	
	class RefreshThread extends Thread {
		
		public void run() {
			try {
				BaseFacebookAuthenticationProcess.this.isRedirectEncountered = false;
				String url = getStartUrl();
				RedirectHttpConnection connection = new RedirectHttpConnection(url);
				connection.setCookieManager( getCookieManager() );
				BaseFacebookAuthenticationProcess.this.cookieManager.setCookie(url, connection);
				connection.setRedirectListener(BaseFacebookAuthenticationProcess.this);
				connection.getResponseCode();
				if (!BaseFacebookAuthenticationProcess.this.isRedirectEncountered) {
					BaseFacebookAuthenticationProcess.this.authenticationListener.onAuthenticationFailure( AuthenticationListener.REASON_ABORTED, "please restart authentication process");
				}
			} catch (Exception e) {
				if (!BaseFacebookAuthenticationProcess.this.isRedirectEncountered) {
					BaseFacebookAuthenticationProcess.this.authenticationListener.onAuthenticationFailure( AuthenticationListener.REASON_NETWORK_FAILURE, e);
				}
			}
		}
		
	}

	public void clearAuthenticationData() {
		try {
			if (this.storage == null) {
				this.storage = new RmsStorage();
			}
			this.storage.delete(KEY_COOKIE_MANAGER);
			getCookieManager().clear();
		} catch (Exception e) {
			//#debug info
			System.out.println("Unable to delete stored cookies" + e);
		}
	}

}
