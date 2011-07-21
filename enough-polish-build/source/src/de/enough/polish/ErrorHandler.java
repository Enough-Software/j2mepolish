package de.enough.polish;

public interface ErrorHandler {
	
	void handleBuildFailure( String deviceIdentifier, String locale, Throwable exception );

}
