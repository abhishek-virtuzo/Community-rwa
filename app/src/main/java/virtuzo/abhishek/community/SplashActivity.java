package virtuzo.abhishek.community;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import virtuzo.abhishek.community.activity.AudioPostActivity;
import virtuzo.abhishek.community.activity.CircularViewActivity;
import virtuzo.abhishek.community.activity.EventViewActivity;
import virtuzo.abhishek.community.activity.HomeActivity;
import virtuzo.abhishek.community.activity.PostActivity;
import virtuzo.abhishek.community.activity.SelectLangActivity;
import virtuzo.abhishek.community.activity.VideoPostActivity;
import virtuzo.abhishek.community.adapter.MainPost;
import virtuzo.abhishek.community.adapter.PostItem;
import virtuzo.abhishek.community.fcm.Config;
import virtuzo.abhishek.community.fcm.NotificationUtils;
import virtuzo.abhishek.community.utils.BaseUtils;
import virtuzo.abhishek.community.utils.Lang;
import virtuzo.abhishek.community.utils.MyFunctions;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private Context mContext = SplashActivity.this;
    private Handler handler;
    private Lang lang;
    private BaseUtils baseUtils;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    TextView version;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            redirectToApp();
        }
    };

    private void redirectToApp() {
        Log.e("token", AppUtils.getInstance(mContext).getFirebaseToken() + "");
        if (AppUtils.getInstance(mContext).getSubscriberId() == null) {
            finish();
            startActivity(new Intent(mContext, SelectLangActivity.class));
        } else {
            if (isAppLink()) {
                return;
            }
            Intent intent = new Intent(mContext, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private boolean isAppLink() {

        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
        Log.e("App Link", appLinkAction);
        if (appLinkData != null) {
            List<String> str = appLinkData.getPathSegments();
            for (String s : str) {
                Log.e("App Link URI", s);
            }

            if (str.size() > 3) {
                if (!str.get(0).equals("community")) {
                    return false;
                }
                if (!str.get(1).equals("app")) {
                    return false;
                }
                switch (str.get(2)) {
                    case "gallery":
                        return isGalleryAppLink(str);

                    case "event":
                        return isEventAppLink(str);

                    case "circular":
                        return isCircularAppLink(str);

                    default:
                        return false;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    private boolean isGalleryAppLink(List<String> str) { // size should be > 4
        if (str.size() > 4) {
            switch (str.get(3)) {
                case "image":
                    try {
                        int id = Integer.parseInt(str.get(4));
                        // Goto Image post
                        imagePost(getResources().getString(R.string.text_imagepost), id);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }

                case "audio":
                    try {
                        int id = Integer.parseInt(str.get(4));
                        // Goto Audio post
                        audioPost(getResources().getString(R.string.text_audiopost), id);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }

                case "video":
                    try {
                        int id = Integer.parseInt(str.get(4));
                        // Goto Video post
                        videoPost(getResources().getString(R.string.text_videopost), id);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }

            }
        }
        return false;
    }

    private boolean isEventAppLink(List<String> str) { // size should be > 3
        if (str.size() > 3) {
            showEvent(str.get(3));
            return true;
        }
        return false;
    }

    private boolean isCircularAppLink(List<String> str) { // size should be > 3
        if (str.size() > 3) {
            try {
                long id = Long.parseLong(str.get(3));
                showCircular(id);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        baseUtils = new BaseUtils(this);
        lang = new Lang(this, null);
        lang.setAppLanguage(lang.getAppLanguage());
//        version = (TextView) findViewById(R.id.version);
//        version.setText("v" + BuildConfig.VERSION_NAME);
        try {
            Bundle bundle = getIntent().getExtras();
//                JSONObject json = new JSONObject(payload);
//                if (json.has("tags") && json.has("image")) {
//                    JSONArray imageArray = json.getJSONArray("image");
//                    JSONArray tagArray = json.getJSONArray("tags");
//                    PostItem item = new PostItem();
//                    item.setName(json.getString("chittiname"));
//                    item.setId(json.getString("chittiId"));
//                    item.setDescription(json.getString("description"));
//                    item.setLiked(json.getString("isLiked"));
//                    item.setTotalLike(json.getInt("totalLike"));
//                    item.setTotalComment(json.getInt("totalComment"));
//                    if (json.has("url")) {
//                        item.setPostUrl(json.getString("url"));
//                    }
//                    item.setDate(json.getString("dateOfApprove"));
//                    item.setImageList(imageArray);
//                    item.setTagList(tagArray);
//                    list.add(item);

            if (bundle != null) {

                if (bundle.getString("NotificationType").equals("1")) { // Post
                    String name = bundle.getString("name");
                    int id = Integer.parseInt(bundle.getString("id"));

                    String MediaType = bundle.getString("MediaType");
                    switch (MediaType) {
                        case MyFunctions.IMAGE_MediaType:
                            imagePost(name, id);
                            return;

                        case MyFunctions.AUDIO_MediaType:
                            audioPost(name, id);
                            return;

                        case MyFunctions.VIDEO_MediaType:
                            videoPost(name, id);
                            return;
                    }

                } else if (bundle.getString("NotificationType").equals("2")) { // Circular
                    String id = bundle.getString("id");
                    showCircular(Long.parseLong(id));
                    return;
                } else if (bundle.getString("NotificationType").equals("3")) { // Event
                    String id = bundle.getString("id");
                    showEvent(id);
                    return;
                }
            }
        } catch (Exception ee) {
            Log.e("Exception", ee.getMessage());
        }

        mRegistrationBroadcastReceiver = new

                BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        // checking for type intent filter
                        if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                            //Registration Complete

                        } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                            // new push notification is received
                            String message = intent.getStringExtra("message");
                            Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                        }
                    }
                }

        ;
        handler = new Handler();
        handler.postDelayed(runnable, 2 * 1000);
    }

    private void showEvent(String id) {
        Intent intent = new Intent(this, EventViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("ID", id);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private void showCircular(long id) {
        Intent intent = new Intent(this, CircularViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong("CircularId", id);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private void videoPost(String heading, int id) {
        MainPost mainPost = new MainPost();

        mainPost.setName(heading);
        mainPost.setId(id);
        mainPost.setList(new ArrayList<PostItem>());

        Intent videoIntent = new Intent(this, VideoPostActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("item", mainPost);
        bundle.putBoolean("notify", true);
        bundle.putString("MediaType", MyFunctions.VIDEO_MediaType);
        videoIntent.putExtras(bundle);
        startActivity(videoIntent);
        finish();
    }

    private void audioPost(String heading, int id) {
        MainPost mainPost = new MainPost();

        mainPost.setName(heading);
        mainPost.setId(id);
        mainPost.setList(new ArrayList<PostItem>());

        Intent audioIntent = new Intent(this, AudioPostActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("item", mainPost);
        bundle.putBoolean("notify", true);
        bundle.putString("MediaType", MyFunctions.AUDIO_MediaType);
        audioIntent.putExtras(bundle);
        startActivity(audioIntent);
        finish();
    }

    private void imagePost(String heading, int id) {
        MainPost mainPost = new MainPost();
        mainPost.setName(heading);
        mainPost.setId(id);
        mainPost.setList(new ArrayList<PostItem>());

        Intent imageIntent = new Intent(this, PostActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("item", mainPost);
        bundle.putBoolean("notify", true);
        bundle.putString("MediaType", MyFunctions.IMAGE_MediaType);
        imageIntent.putExtras(bundle);
        startActivity(imageIntent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }
}
