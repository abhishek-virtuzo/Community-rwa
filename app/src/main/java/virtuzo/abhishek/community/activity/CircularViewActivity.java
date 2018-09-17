package virtuzo.abhishek.community.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import virtuzo.abhishek.community.AppUtils;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.adapter.Circular;
import virtuzo.abhishek.community.config.UrlConfig;
import virtuzo.abhishek.community.utils.BaseUtils;
import virtuzo.abhishek.community.utils.Lang;
import virtuzo.abhishek.community.utils.MyFunctions;
import virtuzo.abhishek.community.utils.Network;
import virtuzo.abhishek.community.utils.Permission;

public class CircularViewActivity extends LangSupportBaseActivity implements Network.Listener {

    private Toolbar toolbar;
    private TextView circularHeadingTextView, createdDtTmTextView;
    private WebView circularContentWebView;
    private Circular circular;
    private Network network;
    Bundle bundle;

    private ProgressDialog progressDialog;

    BaseUtils baseUtils;

    boolean seen = false;
    Lang lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circular_view);

        initToolbar();

        bundle = getIntent().getExtras();
        circular = new Circular();
        circular.setCircularId(bundle.getLong("CircularId"));

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        baseUtils = new BaseUtils(this);

//        getContent();
        loadContent();

//        initContent();

    }

    private void getContent() {
        String html = "<br /><br />Read the handouts please for tomorrow.<br /><br /><!--homework help homework" +
                "help help with homework homework assignments elementary school high school middle school" +
                "// --><font color='#60c000' size='4'><strong>Please!</strong></font>" +
                "<img src='http://www.homeworknow.com/hwnow/upload/images/tn_star300.gif'  />";
//        String html = "https:\\/\\/www.youtube.com\\/watch?v=iS0oCMSO3Js";
        circular = new Circular("India Today", html);
    }

    private void initContent() {
        circularHeadingTextView = (TextView) findViewById(R.id.circularHeadingTextView);
        circularContentWebView = (WebView) findViewById(R.id.circularContentWebView);
        createdDtTmTextView = (TextView) findViewById(R.id.createdDtTmTextView);

        circularHeadingTextView.setText(circular.getSubject());
        String createdDtTm = MyFunctions.convertDateFormat(circular.getCreatedDtTm(), this);
        createdDtTmTextView.setText(createdDtTm);
        circularContentWebView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

        });
        WebSettings webSettings = circularContentWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        final String mimeType = "text/html";
        final String encoding = "UTF-8";
//        String html = "<br /><br />Read the handouts please for tomorrow.<br /><br /><!--homework help homework" +
//                "help help with homework homework assignments elementary school high school middle school" +
//                "// --><font color='#60c000' size='4'><strong>Please!</strong></font>" +
//                "<img src='http://www.homeworknow.com/hwnow/upload/images/tn_star300.gif'  />";
        String html = circular.getContent();

        circularContentWebView.loadDataWithBaseURL("", html, mimeType, encoding, "");
//        circularContentWebView.loadUrl(html);
    }

    public void loadContent() {
        lang = new Lang(this);

        network = new Network(getApplicationContext(), this);
        HashMap<String, String> map = new HashMap<>();
        map.put("CircularId", circular.getCircularId()+"");
        map.put("SubscriberId", AppUtils.getInstance(this).getSubscriberId());
        map.put("languageCode", lang.getAppLanguage());
        network.makeRequest(map, UrlConfig.CircularNotificationDetails);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.text_menu_circular));
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

    @Override
    public void onNetworkSuccess(String response, String url) {
        if (response == null) {
            return;
        }

        Log.e("Response", response + "");
        progressDialog.dismiss();
        try {
            JSONObject json = new JSONObject(response);
            if (json.getInt("responseCode") == 1) {
                JSONArray array = json.getJSONArray("Payload");
                Gson gson = new Gson();
                if (array.length() == 1) {
                    circular = gson.fromJson(array.get(0).toString(), Circular.class);
                    initContent();
                    markSeenInLocal();
                    return;
                } else {
                    Toast.makeText(this, getResources().getString(R.string.msg_common), Toast.LENGTH_SHORT).show();
                }
            }

        } catch (JSONException e) {
            Log.e("CircularView", e.getMessage());
        }
    }

    private void markSeenInLocal() {
        seen = true;
        String savedResponse = AppUtils.getInstance(this).getCircularsResponse();
        if (savedResponse != null) {
            try {
                JSONObject json = new JSONObject(savedResponse);
                if (json.getInt("responseCode") == 1) {
                    JSONArray array = json.getJSONArray("Payload");

                    Gson gson = new Gson();
                    ArrayList<Circular> circulars = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject json1 = array.getJSONObject(i);

                        Circular circular1 = gson.fromJson(json1.toString(), Circular.class);
                        if (circular1.getCircularId() == circular.getCircularId()) {
                            circular1.setSeen(MyFunctions.Circular_READ);
                        }
                        circulars.add(circular1);
                    }
                    JSONArray jsonArray = new JSONArray(gson.toJson(circulars));
                    json.put("Payload", jsonArray);
                    Log.e("Saved Circulars", json.toString());
                    AppUtils.getInstance(this).setCircularsResponse(json.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onNetworkError(String error, String url) {
        progressDialog.dismiss();
        Toast.makeText(this, "Something went wrong...", Toast.LENGTH_SHORT).show();
    }

    public void onShareClick(View view) {
        if (circular == null) {
            Toast.makeText(this, getResources().getString(R.string.msg_common), Toast.LENGTH_SHORT).show();
        } else {
            Permission permission = new Permission(this);
            if (permission.chckSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                baseUtils.sharePost(null, circular.getShareUrl());
            } else {
                permission.requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, null);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                baseUtils.sharePost(null, circular.getShareUrl());
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
