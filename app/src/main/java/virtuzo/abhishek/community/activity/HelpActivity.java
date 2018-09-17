package virtuzo.abhishek.community.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import virtuzo.abhishek.community.AppUtils;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.model.Contact;
import virtuzo.abhishek.community.realm.RealmHelper;
import virtuzo.abhishek.community.utils.MyFunctions;
import virtuzo.abhishek.community.utils.Permission;

public class HelpActivity extends LangSupportBaseActivity {

    static final String policeNumber = "100";
    static final String ambulanceNumber = "108";
    static final String fireNumber = "101";
    private static final int SEND_SMS_REQUEST_CODE = 201;

    public static final String DEFAULT_Sms_Message = "I am in trouble. NEED YOUR HELP AS SOON AS POSSIBLE.";

    private Permission permission;
    Toolbar toolbar;
    TextView policeTextView, ambulanceTextView, fireTextView;
    TextView sendSMSTextView;

    AppUtils appUtils;

    private boolean smsToBeSend = false;
    private Handler handler;
    private Runnable runnable;
    private int timeLeft = 0;

    FrameLayout tapFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        initToolbar();
        initGUI();
    }

    private void initGUI() {
        policeTextView = findViewById(R.id.policeTextView);
        ambulanceTextView = findViewById(R.id.ambulanceTextView);
        fireTextView = findViewById(R.id.fireTextView);

        sendSMSTextView = findViewById(R.id.sendSMSTextView);

        policeTextView.setText(policeNumber);
        ambulanceTextView.setText(ambulanceNumber);
        fireTextView.setText(fireNumber);

        tapFrame = findViewById(R.id.tapFrame);

        permission = new Permission(this);

        appUtils = new AppUtils(this);

        // for first time only
        if (appUtils.getEmergencyMessage() == null) {
            appUtils.setEmergencyMessage(DEFAULT_Sms_Message);
        }

    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.text_menu_help));
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

    private void sendSmsButtonClicked() {
        if (smsToBeSend) {
            // Cancel SMS
            setSendSMSButtonText(0);
            smsToBeSend = false;
            if (handler != null) {
                handler.removeCallbacks(runnable);
            }
        } else {
            // Start timer for SMS
            smsToBeSend = true;

            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    timeLeft--;
                    if (timeLeft > 0) {
                        handler.postDelayed(runnable, 1000);
                        setSendSMSButtonText(timeLeft);
                    } else {
                        sendMessage();
                        // TODO change label to "send SMS"
                        smsToBeSend = false;
                        setSendSMSButtonText(0);
                    }
                }
            };
            handler.postDelayed(runnable, 1000);
            timeLeft = 5;
            setSendSMSButtonText(timeLeft);
        }
    }

    public void onSendSmsButtonClick(View view) {
        ArrayList<Contact> contacts = new ArrayList<>();
        contacts.addAll(RealmHelper.getInstance().getContacts());
        if (contacts.size() == 0) {
            popupMessage("Choose Emergency contacts");
            return;
        }
        sendSmsButtonClicked();
    }

    // if 0, default "tap to send"
    // else, change to "tap to cancel"
    private void setSendSMSButtonText(int timeLeft) {
        if (timeLeft > 0) {
            sendSMSTextView.setText(timeLeft + " " + getResources().getString(R.string.tap_to_cancel_sms));
            tapFrame.setBackgroundColor(getResources().getColor(R.color.darkBlue));
        } else {
            sendSMSTextView.setText(R.string.tap_to_send_sms);
            tapFrame.setBackgroundColor(getResources().getColor(R.color.lightBlue));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SEND_SMS_REQUEST_CODE) {
            if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Log.i("Message", "Permission granted");
                sendMessage();
            } else {
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendMessage() {
        Log.i("Message", "sendMessage");
        if (!permission.chckSelfPermission(Manifest.permission.SEND_SMS)) {
            permission.setRequestCode(SEND_SMS_REQUEST_CODE);
            permission.requestPermissions(Manifest.permission.SEND_SMS, null);
            Log.i("Message", "grant permission");
            return;
        } else {
            Log.i("Message", "Sending...");
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<Contact> contacts = new ArrayList<>();
            contacts.addAll(RealmHelper.getInstance().getContacts());
            if (contacts.size() == 0) {
                popupMessage("Choose Emergency contacts");
                return;
            }

            String smsMessage = appUtils.getEmergencyMessage();
            smsMessage += ("\nat " + MyFunctions.getCurrentDateTime());
            for (Contact contact : contacts) {
                smsManager.sendTextMessage(contact.getNumber(), null, smsMessage, null, null);
                Toast.makeText(this, "Sent to " + contact.getName(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void popupMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();

    }

    public void onPoliceCallClick(View view) {
//        Toast.makeText(this, "Call Police", Toast.LENGTH_SHORT).show();
        contactDial(policeNumber);
    }

    public void onAmbulanceCallClick(View view) {
//        Toast.makeText(this, "Call Ambulance", Toast.LENGTH_SHORT).show();
        contactDial(ambulanceNumber);
    }

    public void onFireCallClick(View view) {
//        Toast.makeText(this, "Call Fire", Toast.LENGTH_SHORT).show();
        contactDial(fireNumber);
    }

    public void onContactsButtonClick(View view) {
//        Toast.makeText(this, "Contacts", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(HelpActivity.this, ContactListActivity.class));
    }

    public void onSetupButtonClick(View view) {
//        Toast.makeText(this, "Setup", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(HelpActivity.this, SetupEmergencyMessageActivity.class));
    }

    public void onSirenClick(View view) throws IOException {
//        Toast.makeText(this, "Play Siren", Toast.LENGTH_SHORT).show();
        if (!stopPanic(SIREN)) {
            playPanic(R.raw.siren);
            currentAlarm = SIREN;
        }
    }

    public void onWhistleClick(View view) throws IOException {
//        Toast.makeText(this, "Play Whistle", Toast.LENGTH_SHORT).show();
        if (!stopPanic(WHISTLE)) {
            playPanic(R.raw.whistle);
            currentAlarm = WHISTLE;
        }
    }

    MediaPlayer mPlayer;
    private static final int NONE = 0;
    private static final int SIREN = 1;
    private static final int WHISTLE = 2;
    private int currentAlarm = NONE;

    private void playPanic(int resId) throws IOException {
        // full volume
        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        String str = "android.resource://" + getPackageName() + "/" + resId;
        Uri mediaUri = Uri.parse(str);
        if (mPlayer == null || !mPlayer.isPlaying()) {
            mPlayer = new MediaPlayer();
        } else {
            mPlayer.reset();
        }
        mPlayer.setDataSource(this, mediaUri);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.prepare();
        mPlayer.setLooping(true);
        mPlayer.start();
    }

    private boolean stopPanic(int panicAlarm) {
        if (currentAlarm == panicAlarm) {
            if (mPlayer != null) {
                mPlayer.stop();
            }
            currentAlarm = NONE;
            return true;
        }
        return false;
    }

    @Override
    protected void onPause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
        }
        super.onPause();
    }

    private void contactDial(String mobileNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mobileNumber));
        startActivity(intent);
    }

}
