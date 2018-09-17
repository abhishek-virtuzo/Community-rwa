package virtuzo.abhishek.community.fcm;

/**
 * Created by ARPIT on 14-03-2017.
 */

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import virtuzo.abhishek.community.SplashActivity;
import virtuzo.abhishek.community.utils.MyFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Ravi Tamada on 08/08/16.
 * www.androidhive.info
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage == null) {
            return;
        }

        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                if (json.has("NotificationType")) {
                    int NotificationType = json.getInt("NotificationType");
                    if (NotificationType==2) { // Circular
                        handleCircularMessage(json);
                    } else if (NotificationType == 3) { // Event
                        handleEventMessage(json);
                    }
                } else {
                    // posts doesn't have NotificationType
                    handleDataMessage(json);
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleEventMessage(JSONObject json) {
        Log.e(TAG, "push event json: " + json.toString());
        try {
            JSONObject data = json.getJSONObject("data");
            String title = data.getString("title");
            String message = data.getString("message");
            String payload = null;
            if (data.has("payload")) {
                payload = data.getString("payload");
            }
            String timestamp = System.currentTimeMillis() + "";
//            if (isAppIsInBackground(getApplicationContext())) {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
                if (payload != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("id", payload);
                    bundle.putString("name", title);
                    bundle.putString("NotificationType", "3");
                    resultIntent.putExtras(bundle);
                }
                showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                Log.e("Event Notification", "Sent");

//            } else {
//                Log.e("Notification", "Not in background");
//
//            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    private void handleCircularMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());
        try {
            JSONObject data = json.getJSONObject("data");
            String title = data.getString("title");
            String message = data.getString("message");
            String payload = null;
            if (data.has("payload")) {
                payload = data.getString("payload");
            }
            String timestamp = System.currentTimeMillis() + "";
            if (isAppIsInBackground(getApplicationContext())) {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
                if (payload != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("id", payload);
                    bundle.putString("name", title);
                    bundle.putString("NotificationType", "2");
                    resultIntent.putExtras(bundle);
                }
                showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);

            } else {

            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());
        try {
            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");

            String imageUrl = null;
            if (data.has("image")) {
                imageUrl = data.getString("image");
            }
            String timestamp = null;
            if (data.has("timestamp")) {
                timestamp = data.getString("timestamp");
            } else {
                timestamp = System.currentTimeMillis() + "";
            }
            String payload = null;
            if (data.has("payload")) {
                payload = data.getString("payload");

            }

            String MediaType = "";
            if (data.has("MediaType")) {
                MediaType = data.getString("MediaType");
            }

            if (isAppIsInBackground(getApplicationContext())) {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
                if (payload != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("id", payload);
                    bundle.putString("name", title);
                    bundle.putString("MediaType", MediaType);
                    bundle.putString("NotificationType", "1");
                    resultIntent.putExtras(bundle);
                }

                // check for image attachment
                if (imageUrl != null && TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    if (MyFunctions.IMAGE_MediaType.equals(MediaType)) {
                        showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                    } else {
                        showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                    }
                }
            } else {
//                // app is in foreground, broadcast the push message
//                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
//                pushNotification.putExtra("message", message);
//                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
//
//                // play notification sound
//                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
//                notificationUtils.playNotificationSound();
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    //Custom Methods

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }

    /**
     * Method checks if the app is in background or not
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }
        return isInBackground;
    }
}