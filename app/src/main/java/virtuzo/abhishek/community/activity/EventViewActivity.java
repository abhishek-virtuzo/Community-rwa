package virtuzo.abhishek.community.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.CalendarContract.Events;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import virtuzo.abhishek.community.AppUtils;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.adapter.Event;
import virtuzo.abhishek.community.config.UrlConfig;
import virtuzo.abhishek.community.utils.BaseUtils;
import virtuzo.abhishek.community.utils.Lang;
import virtuzo.abhishek.community.utils.MyFunctions;
import virtuzo.abhishek.community.utils.Network;
import virtuzo.abhishek.community.utils.Permission;

public class EventViewActivity extends LangSupportBaseActivity implements Network.Listener, View.OnClickListener {

    Bundle bundle;
    Event event;
//    ProgressDialog progressDialog;
    Network network;
    ImageView eventImageView, interestedImageView;
    TextView eventTitleTextView, timingsTextView, venueTextView, descriptionTextView, interestedTextView, imageNotFoundTextView;
    ProgressBar eventImageProgressBar;
    LinearLayout mapsButtonView, interestedButtonView, shareButtonView, calendarButtonView;
    ProgressBar loadingProgressBar;
    ScrollView scrollView;
    BaseUtils baseUtils;
    Lang lang;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_view);

        eventImageView = (ImageView) findViewById(R.id.eventImageView);
        interestedImageView = findViewById(R.id.interestedImageView);

        eventTitleTextView = findViewById(R.id.eventTitleTextView);
        timingsTextView = findViewById(R.id.timingsTextView);
        venueTextView = findViewById(R.id.venueTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        interestedTextView = findViewById(R.id.interestedTextView);
        imageNotFoundTextView = findViewById(R.id.imageNotFoundTextView);

        eventImageProgressBar = findViewById(R.id.eventImageProgressBar);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);

        scrollView = findViewById(R.id.scrollView);

        mapsButtonView = findViewById(R.id.mapsButtonView);
        interestedButtonView = findViewById(R.id.interestedButtonView);
        shareButtonView = findViewById(R.id.shareButtonView);
        calendarButtonView = findViewById(R.id.calendarButtonView);

        mapsButtonView.setOnClickListener(this);
        interestedButtonView.setOnClickListener(this);
        shareButtonView.setOnClickListener(this);
        calendarButtonView.setOnClickListener(this);

        scrollView.setVisibility(View.GONE);
        loadingProgressBar.setVisibility(View.VISIBLE);

        bundle = getIntent().getExtras();
        event = new Event();
        event.setId(bundle.getString("ID"));

//        progressDialog = new ProgressDialog(this);
//        progressDialog.show();

        initToolbar();
        loadContent();

        MyFunctions.setStatusBarAndNavigationBarColor(this);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadContent() {
        baseUtils = new BaseUtils(this);
        network = new Network(getApplicationContext(), this);
        lang = new Lang(this);

        HashMap<String, String> map = new HashMap<>();
        map.put("SubscriberId", AppUtils.getInstance(this).getSubscriberId());
        map.put("languageCode", lang.getAppLanguage());
        map.put("ID", event.getId());

        network.makeRequest(map, UrlConfig.EventList);
    }

    @Override
    public void onNetworkSuccess(String response, String url) {
        if (response == null) {
            return;
        }
        Log.e("Response", response + "");
        if (url.indexOf(UrlConfig.EventInterested) != -1) {
//            saveInterestedInLocal();
            return;
        }
        if (url.indexOf(UrlConfig.EventList) != -1) {
            try {
                JSONObject json = new JSONObject(response);
                if (json.getInt("responseCode") == 1) {
                    JSONArray array = json.getJSONArray("Payload");
                    Gson gson = new Gson();
                    if (array.length() == 1) {
                        event = gson.fromJson(array.get(0).toString(), Event.class);
                        initContent(event);
                        return;
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.msg_common), Toast.LENGTH_SHORT).show();
                        loadingProgressBar.setVisibility(View.GONE);
                    }
                }

            } catch (JSONException e) {
                Log.e("CircularView", e.getMessage());
            }
        }

    }

    private void saveInterestedInLocal() {
        String savedResponse = AppUtils.getInstance(this).getEventsResponse();
        try {
            JSONObject json = new JSONObject(savedResponse);
            if (json.getInt("responseCode") == 1) {
                JSONArray array = json.getJSONArray("Payload");

                Gson gson = new Gson();
                ArrayList<Event> events = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject json1 = array.getJSONObject(i);
                    Event event1 = gson.fromJson(json1.toString(), Event.class);
                    if (event.getId().equals(event1.getId())) {
                        event1.setInterested(event.isInterested());
                        Log.e("Event compared", event.getId() + " " + event.isInterested() + "," + event1.isInterested());
                    }
                    events.add(event1);
                }
                JSONArray jsonArray = new JSONArray(gson.toJson(events));
//                Log.e("JSON Array Events", jsonArray.toString());
                json.put("Payload", jsonArray);
                Log.e("Saved Events", json.toString());
                AppUtils.getInstance(this).setEventsResponse(json.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initContent(Event event) {
        String dateTime = MyFunctions.convertDateFormat(event.getDate(), this);
        eventTitleTextView.setText(event.getEventTitle());
        timingsTextView.setText(dateTime);
        venueTextView.setText(event.getVenue());
        descriptionTextView.setText(event.getDescription());

        Glide.with(this).load(event.getImage()).crossFade().thumbnail(1f).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                eventImageProgressBar.setVisibility(View.GONE);
                imageNotFoundTextView.setVisibility(View.VISIBLE);
//                eventImageView.setImageResource(R.drawable.image_background);
                return true;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                eventImageProgressBar.setVisibility(View.GONE);
                imageNotFoundTextView.setVisibility(View.GONE);
                return false;
            }
        }).into(eventImageView);

        if (event.isInterested()) {
            interestedImageView.setImageResource(R.drawable.star_blue);
            interestedTextView.setTextColor(getResources().getColor(R.color.colorAccent));
            event.setInterested(true);
        }

        scrollView.setVisibility(View.VISIBLE);
        loadingProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onNetworkError(String error, String url) {
        loadingProgressBar.setVisibility(View.GONE);
        Toast.makeText(this, "Something went wrong...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if (event == null) {
            Toast.makeText(this, getResources().getString(R.string.msg_common), Toast.LENGTH_SHORT).show();
            return;
        }
        switch (v.getId()) {
            case R.id.interestedButtonView:
                if (!BaseUtils.getInstance(this).isNetworkAvailable()) {
                    Toast.makeText(this, getResources().getString(R.string.msg_nointernet), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (event.isInterested() == false) {
                    interestedImageView.setImageResource(R.drawable.star_blue);
                    interestedTextView.setTextColor(getResources().getColor(R.color.colorAccent));
                    event.setInterested(true);
                } else {
                    interestedImageView.setImageResource(R.drawable.star);
                    interestedTextView.setTextColor(getResources().getColor(R.color.colorText));
                    event.setInterested(false);
                }
                saveInterestedInLocal();
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("subscriberId", AppUtils.getInstance(this).getSubscriberId());
                map.put("eventId", event.getId());
                map.put("isInterested", event.isInterested()+"");
                map.put("languageCode", new Lang(this).getAppLanguage());
                network.makeRequest(map, UrlConfig.EventInterested);
                baseUtils.vibrate();
                break;

            case R.id.mapsButtonView:
                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                String uri;
                try {
                    uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f(%s)", Double.parseDouble(event.getLatitude()), Double.parseDouble(event.getLongitude()), Double.parseDouble(event.getLatitude()), Double.parseDouble(event.getLongitude()), event.getVenue());
                } catch (Exception e) {
                    uri = String.format(Locale.ENGLISH, "geo:%s,%s?q=%s,%s(%s)", event.getLatitude(), event.getLongitude(), event.getLatitude(), event.getLongitude(), event.getVenue());
                }
                intent1.setData(Uri.parse(uri));
                intent1.setPackage("com.google.android.apps.maps");
                startActivity(intent1);
                break;

            case R.id.shareButtonView:
                Permission permission = new Permission(this);
                if (permission.chckSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    baseUtils.sharePost(null, event.getShareUrl());
                } else {
                    permission.requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, null);
                }
                break;

            case R.id.calendarButtonView:
//                Toast.makeText(this, "Under development", Toast.LENGTH_SHORT).show();
                addToCalendar(event);
                break;
        }
    }

    private void addToCalendar(Event event) {
//        Intent intent = new Intent(Intent.ACTION_INSERT);
//        intent.addCategory(Intent.CATEGORY_APP_CALENDAR);
//        intent.setType("vnd.android.cursor.item/event");
//        intent.putExtra(Events.TITLE, event.getEventTitle());
//        intent.putExtra(Events.EVENT_LOCATION, "");
//        intent.putExtra(Events.DESCRIPTION, event.getDescription());
//
//        Calendar cal = Calendar.getInstance();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
//        try {
//            cal.setTime(sdf.parse("Mon Mar 14 16:02:37 GMT 2011"));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        startActivity(intent);

        Calendar cal = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            cal.setTime(sdf.parse(event.getDate()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra(Events._ID, event.getId());
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal.getTimeInMillis());
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
        intent.putExtra(Events.RRULE, "FREQ=YEARLY");
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, cal.getTimeInMillis()+60*60*1000);
        intent.putExtra(Events.TITLE, event.getEventTitle());
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                baseUtils.sharePost(null, event.getShareUrl());
            }
        }
    }

}
