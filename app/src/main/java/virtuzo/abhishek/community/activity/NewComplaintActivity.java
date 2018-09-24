package virtuzo.abhishek.community.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmResults;
import virtuzo.abhishek.community.AppController;
import virtuzo.abhishek.community.AppUtils;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.config.UrlConfig;
import virtuzo.abhishek.community.custom.Tasks;
import virtuzo.abhishek.community.model.Complaint;
import virtuzo.abhishek.community.realm.RealmHelper;
import virtuzo.abhishek.community.utils.BaseUtils;
import virtuzo.abhishek.community.utils.MyFunctions;
import virtuzo.abhishek.community.utils.Network;
import virtuzo.abhishek.community.utils.Permission;
import virtuzo.abhishek.community.utils.network.Multipart;

public class NewComplaintActivity extends LangSupportBaseActivity implements Network.Listener, Tasks.Listener, Multipart.Listener {

    Toolbar toolbar;
    EditText titleEditText, descriptionEditText;
    Network network;
    FrameLayout editProfileButton;
    Permission permission;
    Tasks tasks;
    private AppUtils appUtils;
    private Multipart multipart;
    TextView uploadedImageTextView;
    ImageView uploadedImageView;
    String imageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_complaint);

        initToolbar();
        initGUI();

        MyFunctions.setStatusBarAndNavigationBarColor(this);
    }

    private void initGUI() {
        titleEditText = (EditText) findViewById(R.id.titleEditText);
        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        uploadedImageTextView = findViewById(R.id.uploadedImageTextView);
        uploadedImageView = findViewById(R.id.uploadedImageView);

        permission = new Permission(this);
        tasks = new Tasks(this, this);
        appUtils = AppUtils.getInstance(this);
        multipart = new Multipart(this, this);

        editProfileButton = (FrameLayout) findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BaseUtils.getInstance(NewComplaintActivity.this).isNetworkAvailable()) {
                    Toast.makeText(NewComplaintActivity.this, "Connect to the internet", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!permission.chckSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    permission.setRequestCode(1001);
                    permission.requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, null);
                } else {
                    openGallery();
                }
            }
        });

        network = new Network(this, this);
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), 1111);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //If permission is granted
        if (grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (requestCode == 1001) {
                    openGallery();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1111:
                    CropImage.activity(data.getData()).setAllowRotation(false).setAllowFlipping(false).setFixAspectRatio(false).setActivityTitle("Profile")
                            .start(this);
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    tasks.executeCompressImage(result.getUri());
                    break;
            }
        }
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_new_complaint));
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

    public void onSendButtonClick(View view) {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (MyFunctions.StringLength(title) == 0) {
            popupMessage(getString(R.string.text_enter_title));
            return;
        }

        if (MyFunctions.StringLength(description) == 0) {
            popupMessage(getString(R.string.text_enter_description));
            return;
        }

        saveOffline(title, description);
    }

    private void saveOffline(String title, String description) {
        Complaint complaint = new Complaint();
        complaint.setTitle(title);
        complaint.setDescription(description);
        complaint.setCreatedDtTm(MyFunctions.getCurrentDateTimeForComplaint());
        complaint.setSynced(0);
        complaint.setStatusId(0);
        complaint.setStatusName("Saved Offline");
        complaint.setPicUrl(imageUrl);

        Realm realm = AppController.realm;
        realm.beginTransaction();
        realm.copyToRealm(complaint);
        realm.commitTransaction();

        setResult(RESULT_OK);
        super.onBackPressed();

        /*
        try {
            sendOffline();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        */
    }

    private void sendOnline(String title, String description) {
        network.setShowProgress(true);

        String subscriberId = AppUtils.getInstance(this).getSubscriberId();

        HashMap<String, String> map = new HashMap<>();
        map.put("SubscriberId", subscriberId);
        map.put("Title", title);
        map.put("Description", description);
        network.makeRequest(map, UrlConfig.AddNewComplaint);
    }

    private void sendOffline() throws JSONException {

        ArrayList<Complaint> complaints = RealmHelper.getInstance().getUnsyncedComplaints();
        JSONObject jsonObject = new JSONObject();

        Gson gson = new Gson();
        JSONArray jsonArray = new JSONArray();
        for(Complaint complaint : complaints) {
            JSONObject jsonObject1 = new JSONObject(gson.toJson(complaint));
            jsonArray.put(jsonObject1);
        }
        jsonObject.put("Payload", jsonArray);

        network.setShowProgress(true);

        String subscriberId = AppUtils.getInstance(this).getSubscriberId();

        Log.e("URL Json", jsonObject.toString());
        HashMap<String, String> map = new HashMap<>();
        map.put("SubscriberId", subscriberId);
        map.put("complaintsList", jsonObject.toString());
        network.makeRequest(map, UrlConfig.SyncComplaintList);
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

    public void finalMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        setResult(RESULT_OK);
                        NewComplaintActivity.super.onBackPressed();
                    }
                })
                .show();

    }

    @Override
    public void onNetworkSuccess(String response, String url) {
        Log.e("URL", "Response - " + response);
        if (response == null) {
            popupMessage("");
            return;
        }

        try {
            JSONObject json = new JSONObject(response);
            if (json.getInt("responseCode") == 1) {
                finalMessage(json.getString("message"));
            } else if(json.getInt("responseCode") == 0) {
                popupMessage(json.getString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onNetworkError(String error, String url) {
        if (error.equals(getResources().getString(R.string.msg_nointernet))) {
            popupMessage(error);
        } else {
            popupMessage(getResources().getString(R.string.msg_common));
        }
    }

    @Override
    public void onTaskPostExecute(Object object) {
        byte[] bytes = (byte[]) object;
        if (bytes == null) {
            Toast.makeText(this, getResources().getString(R.string.msg_common), Toast.LENGTH_SHORT).show();
        } else {
            HashMap<String, String> map = new HashMap<>();
            map.put("subscriberId", appUtils.getSubscriberId());
            multipart.setShowProgress(true);
            multipart.setMap(map);
            multipart.setUrl(UrlConfig.complaintsPic);
//            multipart.setUrl("http://gstmadeeasy.net/apk/api/uploadImage.php");
            multipart.setBytes(bytes, "profilePic");
            multipart.execute();
        }
    }

    @Override
    public void onMultipartSuccess(String response, String url) {
        try {
            Log.e("Response", response);
            JSONObject json = new JSONObject(response);
            if (json.getInt("responseCode") == 1) {
                if (json.has("Payload")) {
                    String picUrl = json.getString("Payload");
                    Glide.with(this).load(picUrl).placeholder(R.drawable.ic_userblank).dontAnimate().into(uploadedImageView);
//                    uploadedImageTextView.setText(picUrl);
                    uploadedImageTextView.setText("Image Uploaded");
                    imageUrl = picUrl;
                } else {
                    Toast.makeText(this, "" + json.getString("message"), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "" + json.getString("message"), Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onMultipartProgress(int progress) {

    }

    @Override
    public void onMultipartError(String error, String url) {
        Toast.makeText(this, error + "", Toast.LENGTH_LONG).show();
    }
}
