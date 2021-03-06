package virtuzo.abhishek.community.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.apache.commons.lang3.StringEscapeUtils;

import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.utils.BaseUtils;
import virtuzo.abhishek.community.utils.Lang;
import virtuzo.abhishek.community.utils.MyFunctions;
import virtuzo.abhishek.community.utils.Permission;

public class AboutUsActivity extends LangSupportBaseActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private AdView mAdView;
    private View shareButtonView, rateButtonView;
    private BaseUtils baseUtils;
    private Permission permission;
    private Lang lang;

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.text_menu_aboutus));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initAdmobBanner() {
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }
        });
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        baseUtils = new BaseUtils(this);
        permission = new Permission(this);
        lang = new Lang(this);
        initToolbar();

        WebView mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("file:///android_asset/html/" + lang.getAppLanguage() + ".html");

        shareButtonView = findViewById(R.id.shareButtonView);
        rateButtonView = findViewById(R.id.rateButtonView);
        shareButtonView.setOnClickListener(this);
        rateButtonView.setOnClickListener(this);

        initAdmobBanner();
        MyFunctions.setStatusBarAndNavigationBarColor(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shareButtonView:
                if (!permission.chckSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    permission.requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, null);
                    return;
                }
                baseUtils.shareApp(getResources().getString(R.string.sharetext_aboutus));
                break;
            case R.id.rateButtonView:
                baseUtils.rateApp();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                baseUtils.shareApp(getResources().getString(R.string.sharetext_aboutus));
            }
        }
    }
}
