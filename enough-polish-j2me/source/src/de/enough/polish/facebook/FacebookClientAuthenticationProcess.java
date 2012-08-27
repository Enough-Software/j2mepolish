package de.enough.polish.facebook;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import de.enough.polish.authentication.AccessToken;
import de.enough.polish.authentication.AuthenticationListener;
import de.enough.polish.authentication.AuthenticationProcess;
import de.enough.polish.io.RedirectHttpConnection;
import de.enough.polish.util.StreamUtil;
import de.enough.polish.util.TextUtil;
import de.enough.polish.util.TimePoint;

/**
 * Implements the Facebook OAuth 2.0 based authentication process.
 * 
 * @author Robert Virkus, j2mepolish@enough.de
 * @see de.enough.polish.authentication.AuthenticationItem
 * @see AuthenticationProcess
 */
public class FacebookClientAuthenticationProcess extends BaseFacebookAuthenticationProcess {
	
	private final String facebookAppSecret;


	
	/**
	 * Creates a new authentication process
	 * @param facebookAppId the application ID
	 * @param facebookAppSecret the application secret
 	 * @param facebookAppRedirectUrl the registered redirect URL
	 * @param listener the authentication listener
	 */
	public FacebookClientAuthenticationProcess(String facebookAppId,
			String facebookAppSecret, String facebookAppRedirectUrl,
			AuthenticationListener listener
			) 
	{
		this( facebookAppId, facebookAppSecret, facebookAppRedirectUrl, null, listener);
	}
	
	/**
	 * Creates a new authentication process
	 * @param facebookAppId the application ID
	 * @param facebookAppSecret the application secret
 	 * @param facebookAppRedirectUrl the registered redirect URL
 	 * @param optional comma separated list of Facebook permissions that should be authorized, compare http://developers.facebook.com/docs/authentication/permissions/
	 * @param listener the authentication listener
	 */
	public FacebookClientAuthenticationProcess(String facebookAppId,
			String facebookAppSecret, String facebookAppRedirectUrl,
			String permissions, AuthenticationListener listener
			) 
	{
		super(facebookAppId, facebookAppRedirectUrl, permissions, listener);
		this.facebookAppSecret = facebookAppSecret;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.facebook.FacebookServerAuthenticationProcess#retrieveAccessToken(java.lang.String)
	 */
	public void retrieveAccessToken(String accessCode) {
		// success :-) now let's retrieve the access token
		//#debug
		System.out.println("Retrieved access code " + accessCode);
		String url = "https://graph.facebook.com/oauth/access_token?client_id=" + this.facebookAppId 
				+ "&redirect_uri=" + TextUtil.encodeUrl( this.facebookAppRedirectUrl )
				+ "&client_secret=" + this.facebookAppSecret
				+ "&code=" + accessCode;
		RedirectHttpConnection connection = null;
		InputStream is = null;
		try {
			connection = new RedirectHttpConnection(url);
			int responseCode = connection.getResponseCode();
			is = connection.openInputStream();
			String result = StreamUtil.getString(is, "UTF-8", 8*1024);
			if (responseCode < 400) {
				//#debug info
				System.out.println("END RESULT=" + result);
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
						if (expiresInSeconds < 2 * 24 * 60 * 60) {
							// this is token that expires quickly
							//#debug
							System.out.println("Trying to exchange token for a long running token...");
							try {
								url = "https://graph.facebook.com/oauth/access_token?client_id=" + this.facebookAppId
										+ "&client_secret=" + this.facebookAppSecret
										+ "&grant_type=fb_exchange_token"
										+ "&fb_exchange_token=" + tokenStr;
								connection = new RedirectHttpConnection(url);
								connection.setCookieManager(this.cookieManager);
								responseCode = connection.getResponseCode();
								is = connection.openInputStream();
								if (responseCode < 400) {
									result = StreamUtil.getString(is, "UTF-8", 8*1024);
									table.clear();
									TextUtil.parseGetParameters(result, table);
									String tokenStrExtended = (String) table.get("access_token");
									String expiresInSecondsStrExtended = (String) table.get("expires");
									if (tokenStrExtended != null && expiresInSecondsStrExtended != null) {
										//#debug
										System.out.println("Extended token from " + expiresInSecondsStr + "s to " + expiresInSecondsStrExtended + "s, previousToken=" + tokenStr + ", updatedToken=" + tokenStrExtended);
										expiresInSeconds = Long.parseLong(expiresInSecondsStrExtended);
										tokenStr = tokenStrExtended;
									} else {
										//#debug warn
										System.out.println("Unable to read extended token from result [" + result + "]");
									}
								}
							} catch (Exception e) {
								//#debug error
								System.out.println("Unable to exchange token with long running one" + e);
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
					this.authenticationListener.onAuthenticationSuccess(token);
				}
			} else {
				//#debug warn
				System.out.println("FAILURE END RESULT=" + result);
				this.authenticationListener.onAuthenticationFailure(AuthenticationListener.REASON_NETWORK_FAILURE, result);
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
