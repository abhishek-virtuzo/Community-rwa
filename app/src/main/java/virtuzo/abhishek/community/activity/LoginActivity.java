package virtuzo.abhishek.community.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsMessage;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import virtuzo.abhishek.community.AppUtils;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.config.UrlConfig;
import virtuzo.abhishek.community.utils.BaseUtils;
import virtuzo.abhishek.community.utils.Lang;
import virtuzo.abhishek.community.utils.MyFunctions;
import virtuzo.abhishek.community.utils.Network;
import virtuzo.abhishek.community.utils.Permission;
import virtuzo.abhishek.community.utils.Valid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginActivity extends LangSupportBaseActivity implements View.OnClickListener, Network.Listener {

    private Toolbar toolbar;
    private AdView mAdView;
    private TextView countryCode, timerText;
    private EditText name, number, otp;
    private Button submit, submitOTP;
    private FrameLayout otpFrame, loginFrame;

    private BaseUtils baseUtils;
    private AppUtils appUtils;
    private Valid valid;
    private Network network;
    private JSONObject payloadJson;

    private BroadcastReceiver smsBroadcastReceiver;
    private Permission permission;
    private JSONArray cityJSONAarry;

    private String AccessCode = "";

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_login));
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

    private void load() {
        countryCode = (TextView) findViewById(R.id.countryCode);
        timerText = (TextView) findViewById(R.id.timerText);

        name = (EditText) findViewById(R.id.name);
        number = (EditText) findViewById(R.id.number);
        otp = (EditText) findViewById(R.id.otp);

        submit = (Button) findViewById(R.id.submitButton);
        submitOTP = (Button) findViewById(R.id.submitOTP);

        otpFrame = (FrameLayout) findViewById(R.id.otpFrame);
        loginFrame = (FrameLayout) findViewById(R.id.loginFrame);

//        initToolbar();
        initAdmobBanner();

        Bundle bundle = getIntent().getExtras();
        AccessCode = bundle.getString("AccessCode");
        Toast.makeText(this, R.string.access_code_validated, Toast.LENGTH_SHORT).show();
        AppUtils.getInstance(this).setAccessCode(AccessCode);

        submit.setOnClickListener(this);
        submitOTP.setOnClickListener(this);
        timerText.setOnClickListener(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        baseUtils = new BaseUtils(this);
        valid = new Valid(this);
        network = new Network(this, this);
        appUtils = AppUtils.getInstance(this);
        permission = new Permission(this);
        load();

        //SMS Recived Broadcast
        smsBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getExtras();
                Object messages[] = (Object[]) bundle.get("pdus");
                SmsMessage smsMessage[] = new SmsMessage[messages.length];
                for (int n = 0; n < messages.length; n++) {
                    smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
                }
                //String incomingNumber = smsMessage[0].getOriginatingAddress();
                String messageBody = smsMessage[0].getMessageBody();
                if (messageBody.indexOf("verification code") != -1) {
                    String code = messageBody.substring(messageBody.length() - 6, messageBody.length());
                    otp.setText(code);
                }
            }
        };

        MyFunctions.setStatusBarAndNavigationBarColor(this);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                onBackPressed();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

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

        if (!valid.isValidNumber(number)) {

            Toast.makeText(this, getResources().getString(R.string.invalid_mobileno) , Toast.LENGTH_SHORT).show();
            return;
        }
        if (!valid.isValidName(name)) {
            return;
        }

        appUtils.setName(name.getText().toString());
        appUtils.setNumber(countryCode.getText().toString() + "" + number.getText().toString());

        Log.i("GCMKEY" , appUtils.getFirebaseToken());

        network.setShowProgress(true);
        HashMap<String, String> map = new HashMap();
        map.put("name", name.getText().toString());
        map.put("mobile", countryCode.getText().toString() + "" + number.getText().toString());
        map.put("languageCode", new Lang(this).getAppLanguage());
        map.put("gcmKey", appUtils.getFirebaseToken());
        map.put("otpToBeSend", "1");
        map.put("isHindi", "false");
        map.put("isEnglish", "true");

        network.makeRequest(map, UrlConfig.login);
        registerReceiver(smsBroadcastReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
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
                        appUtils.setSubscriberId(payloadJson.getString("subscriberId"));
                        appUtils.setProfileUrl(payloadJson.getString("profilePicUrl"));
                        Intent intent = new Intent(this, HomeActivity.class);
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
            case R.id.timerText:
                if (finishCounter) {
                    onClickSubmitButton();
                }
                break;
        }
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
            unregisterReceiver(smsBroadcastReceiver);
        } catch (Exception ee) {

        }
    }

    private void openOTPScreen() {
        countDownTimer.start();
        loginFrame.setClickable(false);
        otpFrame.setClickable(true);
        otpFrame.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNetworkSuccess(String response, String url) {
        if (response != null) {
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
    }

    @Override
    public void onNetworkError(String error, String url) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        onClickSubmitButton();
    }
}
