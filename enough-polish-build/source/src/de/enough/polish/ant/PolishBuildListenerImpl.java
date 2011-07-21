package de.enough.polish.ant;

import java.io.File;

public class PolishBuildListenerImpl implements PolishBuildListener {

	public void notifyBuildEvent(String name, Object data) {
		System.out.println("Event=" + name + ", data=" + data );
		if (EVENT_PREPROCESS_SOURCE_DIR.equals(name)) {
			File dir = (File) data;
			System.out.println("Preprocessing source dir=" + dir.getAbsolutePath() );
		}
	}

}
