package de.enough.polish.authentication;

public interface AuthenticationListener {
	
	public static final int REASON_ABORTED = 1;
	public static final int REASON_DENIED = 2;
	public static final int REASON_NETWORK_FAILURE = 3;
	
	void onAuthenticationSuccess( AccessToken token );
	
	void onAuthenticationFailure( int reason, Object errorData );

}
