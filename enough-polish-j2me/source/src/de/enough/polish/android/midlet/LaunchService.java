//#condition polish.android
package de.enough.polish.android.midlet;

import android.app.IntentService;
import android.content.Intent;

public class LaunchService extends IntentService {

	public static final String EXTRA_CLASSNAME_KEY = "activity.class";

	public LaunchService() {
		super("LaunchService");
	}

	/**
	 * The IntentService calls this method from the default worker thread with
	 * the intent that started the service. When this method returns,
	 * IntentService stops the service, as appropriate.
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			CharSequence className = intent.getExtras().getCharSequence(EXTRA_CLASSNAME_KEY);
			//#debug
			System.out.println("Launching " + className);
			Class clazz = Class.forName(className.toString());
			Intent launchIntent = new Intent(getApplicationContext(), clazz);
			launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			getApplicationContext().startActivity(launchIntent);
			stopSelf();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}