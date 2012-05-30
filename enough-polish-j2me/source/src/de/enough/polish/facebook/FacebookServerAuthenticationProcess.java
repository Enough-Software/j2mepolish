package de.enough.polish.facebook;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import de.enough.polish.authentication.AccessToken;
import de.enough.polish.authentication.AuthenticationItem;
import de.enough.polish.authentication.AuthenticationListener;
import de.enough.polish.authentication.AuthenticationProcess;
import de.enough.polish.io.RedirectHttpConnection;
import de.enough.polish.util.StreamUtil;
import de.enough.polish.util.TextUtil;
import de.enough.polish.util.TimePoint;

/**
 * Implements the Facebook OAuth 2.0 based authentication process using a server side creation of the token.
 * 
 * @author Robert Virkus, j2mepolish@enough.de
 * @see AuthenticationItem
 * @see AuthenticationProcess
 * @see http://developers.facebook.com/docs/authentication/server-side/
 */
public class FacebookServerAuthenticationProcess extends BaseFacebookAuthenticationProcess {
	
	private String trackedState;

	/**
	 * Creates a new authentication process
	 * @param facebookAppId the application ID
 	 * @param facebookAppRedirectUrl the registered redirect URL
	 * @param listener the authentication listener
	 */
	public FacebookServerAuthenticationProcess(String facebookAppId,
			String facebookAppRedirectUrl,
			AuthenticationListener listener
			) 
	{
		this( facebookAppId, facebookAppRedirectUrl, null, listener);
	}
	
	/**
	 * Creates a new authentication process
	 * @param facebookAppId the application ID
	 * @param facebookAppSecret the application secret
 	 * @param facebookAppRedirectUrl the registered redirect URL
 	 * @param optional comma separated list of Facebook permissions that should be authorized, compare http://developers.facebook.com/docs/authentication/permissions/
	 * @param listener the authentication listener
	 */
	public FacebookServerAuthenticationProcess(String facebookAppId,
			String facebookAppRedirectUrl,
			String permissions, AuthenticationListener listener
			) 
	{
		super(facebookAppId, facebookAppRedirectUrl, permissions, listener);
	}
	
	

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.facebook.FacebookServerAuthenticationProcess#retrieveAccessToken(java.lang.String)
	 */
	protected String getState() {
		// you could generate a state on the server side here as well to combine the current user with the corresponding Facebook authentication token later
		String state = super.getState();
		this.trackedState = state;
		return state;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.facebook.BaseFacebookAuthenticationProcess#retrieveAccessToken(java.lang.String)
	 */
	public void retrieveAccessToken(String accessCode) {
		String url = this.facebookAppRedirectUrl + "?state=" + this.trackedState + "&code=" + accessCode;
		RedirectHttpConnection connection = null;
		InputStream is = null;
		try {
			connection = new RedirectHttpConnection(url);
			int responseCode = connection.getResponseCode();
			is = connection.openInputStream();
			String result = StreamUtil.getString(is, "UTF-8", 8*1024);
			if (responseCode >= 400) {
				this.authenticationListener.onAuthenticationFailure(AuthenticationListener.REASON_NETWORK_FAILURE, result);
			} else {
				Hashtable table = new Hashtable();
				TextUtil.parseGetParameters(result, table);
				String tokenStr = (String) table.get("access_token");
				String expiresInSecondsStr = (String) table.get("expires");
				if ((tokenStr == null) || (expiresInSecondsStr == null)) {
					//#debug error
					System.out.println("Either access_token or expires not defined in response " + result);
					this.authenticationListener.onAuthenticationFailure(AuthenticationListener.REASON_NETWORK_FAILURE, result);
				} else {
					// hooray, we have a valid access token :-)
					TimePoint expires = TimePoint.now();
					try {
						long expiresInSeconds = Long.parseLong(expiresInSecondsStr); 
						expires.addSecond(expiresInSeconds);
					} catch (Exception e) {
						//#debug error
						System.out.println("Unable to parse access_token expires setting of " + expiresInSecondsStr + e);
						expires.addHour(1);
					}
					// store the current state of the cookie manager:
					saveCookieManager();
					// notify listener about success:
					AccessToken token = new AccessToken(tokenStr, expires);
					this.authenticationListener.onAuthenticationSuccess(token);				}
			}
		} catch (IOException e) {
			//#debug error
			System.out.println("Unable to retrieve access token" + e);
			this.authenticationListener.onAuthenticationFailure(AuthenticationListener.REASON_NETWORK_FAILURE, e);
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				// ignore
			}
			try {
				connection.close();
			} catch (IOException e) {
				// ignore
			}
		}		
	}

}
