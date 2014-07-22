package com.matejdro.pebblenotificationcenter.notifications;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import timber.log.Timber;

@TargetApi(value = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class JellybeanNotificationListener extends NotificationListenerService {
	private Handler handler;
	public static JellybeanNotificationListener instance;
	
	@Override
	public void onDestroy() {
		NotificationHandler.active = false;

        Timber.d("Notification Listener stopped...");

        instance = null;
	}

	@Override
	public void onCreate() {
        Timber.d("Creating Notification Listener...");

		handler = new Handler();
		instance = this;

		NotificationHandler.active = true;

        Timber.d("Finished creating Notification Listener...");


        super.onCreate();
	}

	@Override
	public void onNotificationPosted(final StatusBarNotification sbn) {
        Log.d("Posted " + sbn.getPackageName() + " " + sbn.getId() + " " + new NotificationParser(this, sbn.getNotification()).text, "TITANIUMDEBUG");

		handler.post(new Runnable() {

			@Override
			public void run() {
				NotificationHandler.newNotification(JellybeanNotificationListener.this, sbn.getPackageName(), sbn.getNotification(), sbn.getId(), sbn.getTag(), true);
			}
		});
	}

	@Override
	public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.d("Removed " + sbn.getPackageName() + " "  + sbn.getId() + " " + new NotificationParser(this, sbn.getNotification()).text, "TITANIUMDEBUG");

        NotificationHandler.notificationDismissedOnPhone(this, sbn.getPackageName(), sbn.getTag(), sbn.getId());
	}

	public static void dismissNotification(String pkg, String tag, int id)
	{
        Timber.d("dismissing");

        if (instance != null)
		    instance.cancelNotification(pkg, tag, id);
	}

	public static StatusBarNotification[] getCurrentNotifications()
	{
		if (instance == null)
			return new StatusBarNotification[0];
		
		return instance.getActiveNotifications();
	}
}
