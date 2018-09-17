package virtuzo.abhishek.community.activity;

import android.Manifest;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import virtuzo.abhishek.community.AppUtils;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.config.UrlConfig;
import virtuzo.abhishek.community.utils.Lang;
import virtuzo.abhishek.community.utils.Network;
import virtuzo.abhishek.community.utils.Permission;
import virtuzo.abhishek.community.utils.Valid;

public class FirstLoginActivity extends LangSupportBaseActivity implements Network.Listener, View.OnClickListener {

    private Toolbar toolbar;
    private AdView mAdView;

    private TextView timerText, otpDetailTextView, access_code;
    private EditText name, number, otp;
    private Button submitOTP;
    private FrameLayout otpFrame, loginFrame;
    Button submitButton;

    private AppUtils appUtils;
    private Valid valid;
    private Network network;
    private JSONObject payloadJson;

    private Permission permission;
    String AccessCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_login);

        valid = new Valid(this);
        network = new Network(this, this);
        appUtils = AppUtils.getInstance(this);
        permission = new Permission(this);

        otpDetailTextView = (TextView) findViewById(R.id.otpDetailTextView);
        timerText = (TextView) findViewById(R.id.timerText);
        access_code = findViewById(R.id.accesscode);

        name = (EditText) findViewById(R.id.name);
        number = (EditText) findViewById(R.id.number);
        otp = (EditText) findViewById(R.id.otp);

        submitOTP = (Button) findViewById(R.id.submitOTP);

        otpFrame = (FrameLayout) findViewById(R.id.otpFrame);
        loginFrame = (FrameLayout) findViewById(R.id.loginFrame);

        submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(this);
        submitOTP.setOnClickListener(this);
        timerText.setOnClickListener(this);

//        initToolbar();
        initAdmobBanner();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submitButton:
                onClickSubmitButton();
                break;

            case R.id.submitOTP:
                try {
                    String payloadCode = payloadJson.getString("otp");
                    if (otp.getText().toString().equals(payloadCode)) {
                        countDownTimer.cancel();

                        Intent intent = new Intent(FirstLoginActivity.this, LoginActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("AccessCode", AccessCode);
                        intent.putExtras(bundle);

                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    } else {
                        otp.setError(Html
                                .fromHtml("<font color='red'>Invalid</font>"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    private void onClickSubmitButton() {

        if (!permission.chckSelfPermission(Manifest.permission.SEND_SMS)) {
            permission.requestPermissions(Manifest.permission.SEND_SMS, null);
            return;
        }
        if (!permission.chckSelfPermission(Manifest.permission.RECEIVE_SMS)) {
            permission.requestPermissions(Manifest.permission.RECEIVE_SMS, null);
            return;
        }
        if (!permission.chckSelfPermission(Manifest.permission.READ_SMS)) {
            permission.requestPermissions(Manifest.permission.READ_SMS, null);
            return;
        }

        AccessCode = access_code.getText().toString().trim();
        if (AccessCode.length() == 0) {
            Toast.makeText(FirstLoginActivity.this, getResources().getString(R.string.invalid_accesscode), Toast.LENGTH_SHORT).show();
            return;
        }

        network.setShowProgress(true);
        HashMap<String, String> map = new HashMap();
        map.put("AccessCode", AccessCode);
        map.put("languageCode", new Lang(this).getAppLanguage());
        network.makeRequest(map, UrlConfig.validateAccessCode);

    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_login));
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    boolean finishCounter = false;
    private CountDownTimer countDownTimer = new CountDownTimer(60 * 1000, 1000) {
        public void onTick(long millisUntilFinished) {
            long remaingTime = millisUntilFinished / 1000;
            if (remaingTime < 10) {
                timerText.setText("00:0" + remaingTime);
            } else {
                timerText.setText("00:" + remaingTime);
            }
        }

        public void onFinish() {
            finishCounter = true;
            timerText.setText(getResources().getString(R.string.text_resend));
        }
    };

    private void openOTPScreen() throws JSONException {
        countDownTimer.start();
        loginFrame.setClickable(false);
        otpFrame.setClickable(true);
        otpFrame.setVisibility(View.VISIBLE);

        StringBuilder builder = new StringBuilder();
        builder.append(getResources().getString(R.string.text_otpdetail_init));
        builder.append(payloadJson.getString("MobileNumber"));
        builder.append(getResources().getString(R.string.text_otpdetail_later));
        otpDetailTextView.setText(builder.toString());
    }

    @Override
    public void onBackPressed() {
        loginFrame.setClickable(true);
        if (otpFrame.getVisibility() == View.VISIBLE) {
            otpFrame.setClickable(false);
            otpFrame.setVisibility(View.GONE);
            countDownTimer.cancel();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            network.cancelRequest();
        } catch (Exception ee) {

        }
    }

    @Override
    public void onNetworkSuccess(String response, String url) {
        if (response == null) {
            return;
        }

        Log.e("Response", response + "");
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getInt("responseCode") == 1) {
                payloadJson = jsonObject.getJSONObject("Payload");
                Log.e("OTP", payloadJson.toString());
                openOTPScreen();
            } else {
                Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
            Toast.makeText(this, getResources().getString(R.string.msg_common), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onNetworkError(String error, String url) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        onClickSubmitButton();
    }
}
