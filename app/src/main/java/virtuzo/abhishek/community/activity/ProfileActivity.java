package virtuzo.abhishek.community.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import virtuzo.abhishek.community.AppUtils;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.config.UrlConfig;
import virtuzo.abhishek.community.model.BloodGroup;
import virtuzo.abhishek.community.model.Message;
import virtuzo.abhishek.community.model.UserProfile;
import virtuzo.abhishek.community.realm.RealmHelper;
import virtuzo.abhishek.community.utils.BaseUtils;
import virtuzo.abhishek.community.utils.Lang;
import virtuzo.abhishek.community.utils.MyFunctions;
import virtuzo.abhishek.community.utils.Network;

public class ProfileActivity extends LangSupportBaseActivity implements Network.Listener {

    private static final int DATE_PICKER_DIALOG_ID = 101;
    Network network;
    private Toolbar toolbar;

    UserProfile userProfile;

    EditText mobileNoEditText, nameEditText, addressEditText, professionEditText, aboutMeEditText, dobEditText;

    Button submitButton;
    private int dobYear, dobMonth, dobDay;
    String dobString;

    Switch bloodDonationSwitch;
    ArrayList<BloodGroup> bloodGroupArrayList;
    Spinner bloodGroupSpinner;
    ArrayAdapter bloodGroupAdapter;
    LinearLayout bloodGroupLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initToolbar();
        initActivity();
        dateInit();
        loadData();

        MyFunctions.setStatusBarAndNavigationBarColor(this);
    }

    private void initActivity() {
        mobileNoEditText = findViewById(R.id.mobileNoEditText);
        nameEditText = findViewById(R.id.nameEditText);
        addressEditText = findViewById(R.id.addressEditText);
        professionEditText = findViewById(R.id.professionEditText);
        aboutMeEditText = findViewById(R.id.aboutMeEditText);
        dobEditText = findViewById(R.id.dobEditText);

        bloodGroupLayout = findViewById(R.id.bloodGroupLayout);

        bloodDonationSwitch = findViewById(R.id.bloodDonationSwitch);
        bloodDonationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    bloodGroupLayout.setVisibility(View.VISIBLE);
                } else {
                    bloodGroupLayout.setVisibility(View.GONE);
                }
            }
        });

        bloodGroupSpinner = findViewById(R.id.bloodGroupSpinner);
        bloodGroupArrayList = new ArrayList<>();
        bloodGroupAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, bloodGroupArrayList);
        bloodGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodGroupAdapter.notifyDataSetChanged();
        bloodGroupSpinner.setAdapter(bloodGroupAdapter);

        submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveButtonClick();
            }
        });

    }

    private void dateInit() {
        dobMonth = 1;
        dobDay = 1;
        dobYear = 1970;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DATE_PICKER_DIALOG_ID) {
            DatePickerDialog dpDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    dobYear = year;
                    dobMonth = monthOfYear + 1;
                    dobDay = dayOfMonth;

                    String dobDayString = "00" + dobDay;
                    dobDayString = dobDayString.substring(dobDayString.length() - 2);

                    String dobMonthString = "00" + dobMonth;
                    dobMonthString = dobMonthString.substring(dobMonthString.length() - 2);

                    String dateString = dobYear + "-" + dobMonthString + "-" + dobDayString;
//                    Toast.makeText(ProfileActivity.this, dateString, Toast.LENGTH_SHORT).show();
                    dobEditText.setText(MyFunctions.convertDobFormat(dateString, ProfileActivity.this));
                    dobString = dateString;
                }
            }, dobYear, dobMonth, dobDay);
//            DatePicker datePicker = dpDialog.getDatePicker();
//            datePicker.setCalendarViewShown(false);
//            Calendar cal = Calendar.getInstance();
//            datePicker.setMaxDate(cal.getTimeInMillis());
//            cal.set(invoiceYear, invoiceMonth - 1, 1);
//            if (invoiceDay < 6) {
//                cal.add(Calendar.MONTH, -1);
//            }
//            datePicker.setMinDate(cal.getTimeInMillis());
            return dpDialog;
        }
        return null;
    }

    private void loadData() {
        network = new Network(this, this);
        network.setShowProgress(true);

        HashMap<String, String> map = new HashMap<>();
        map.put("SubscriberId", AppUtils.getInstance(this).getSubscriberId());
        network.makeRequest(map, UrlConfig.GetProfileData);
    }

    private void submitButtonClick() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setMessage("Saved")
                .setPositiveButton("OK" ,   new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        ProfileActivity.this.finish();
                    }
                })
                .show();

    }

    private void saveButtonClick() {
        network.setShowProgress(true);
        String subscriberId = AppUtils.getInstance(this).getSubscriberId();
        String address = addressEditText.getText().toString();
        String profession = professionEditText.getText().toString();
        String aboutMe = aboutMeEditText.getText().toString();
        BloodGroup bloodGroup = (BloodGroup) bloodGroupSpinner.getSelectedItem();

//        if (MyFunctions.StringLength(dobString) == 0) {
//            Toast.makeText(this, "Choose DOB", Toast.LENGTH_SHORT).show();
//            return;
//        }

        HashMap<String, String> map = new HashMap<>();
        map.put("SubscriberId", subscriberId);
        map.put("Address", address);
        map.put("DOB", dobString);
        map.put("Profession", profession);
        map.put("AboutMe", aboutMe);
        if (bloodDonationSwitch.isChecked()) {
            map.put("BloodDonate", "1");
        } else {
            map.put("BloodDonate", "0");
        }
        map.put("BloodGroupID", bloodGroup.getID()+"");
        network.makeRequest(map, UrlConfig.SaveProfileData);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.text_menu_profile));
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
        switch (url) {
            case UrlConfig.GetProfileData:
                if (response == null) {
                    finalMessage(getResources().getString(R.string.msg_common));
                    return;
                }
                Log.e("Response", response + "");
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getInt("responseCode") == 1) {
                        JSONArray array = json.getJSONArray("Payload");

                        Gson gson = new Gson();
                        if (array.length() > 0) {
                            json = array.getJSONObject(0);
                            userProfile = gson.fromJson(json.toString(), UserProfile.class);
                            initContent(userProfile);
                        }
                    } else {
                        finalMessage(json.getString("message"));
                    }
                } catch (JSONException e) {
                    finalMessage(response);
                }
                break;

            case UrlConfig.SaveProfileData:
                if (response == null) {
                    showMessage("");
                    return;
                }
                finalMessage(response);
                break;
        }

    }

    @Override
    public void onNetworkError(String error, String url) {
        switch (url) {
            case UrlConfig.GetProfileData:
                if (error.equals(getResources().getString(R.string.msg_nointernet))) {
                    finalMessage(error);
                } else {
                    finalMessage("Network Error");
                }
                break;

            case UrlConfig.SaveProfileData:
                if (error.equals(getResources().getString(R.string.msg_nointernet))) {
                    showMessage(error);
                } else {
                    showMessage("Network Error");
                }
                break;
        }
    }

    private void initContent(UserProfile userProfile) {
        bloodGroupArrayList.clear();
        String chooseBloodGroup = getString(R.string.text_choose_blood_group);
        bloodGroupArrayList.add(new BloodGroup("- - " + chooseBloodGroup + " - -"));
        bloodGroupArrayList.addAll(RealmHelper.getInstance().getBloodGroups());
        bloodGroupAdapter.notifyDataSetChanged();

        mobileNoEditText.setText(userProfile.getMobileNo());
        nameEditText.setText(userProfile.getName());
        addressEditText.setText(userProfile.getAddress());
        professionEditText.setText(userProfile.getProfession());
        aboutMeEditText.setText(userProfile.getAboutMe());

        if (userProfile.getBloodDonate() == 1) {
            bloodDonationSwitch.setChecked(true);
        }

        try {
            for (int i = 0; i < bloodGroupArrayList.size(); i++) {
                BloodGroup bloodGroup = bloodGroupArrayList.get(i);
                if (bloodGroup.getID() == userProfile.getBloodGroupID()) {
                    bloodGroupSpinner.setSelection(i);
                    break;
                }
            }
            bloodGroupSpinner.setSelection(userProfile.getBloodGroupID());
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.msg_common), Toast.LENGTH_SHORT).show();
        }

        String[] dobArray = userProfile.getDOB().split("-");
        if ("0000-00-00".equals(userProfile.getDOB())){
            return;
        }
        if (dobArray.length >= 3) {
            dobYear = Integer.parseInt(dobArray[0]);
            dobMonth = Integer.parseInt(dobArray[1]) - 1;
            dobDay = Integer.parseInt(dobArray[2]);
            dobString = userProfile.getDOB();
            dobEditText.setText(MyFunctions.convertDobFormat(dobString, ProfileActivity.this));
        }

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
                    public void onDismiss(DialogInterface dialogInterface) {
                        ProfileActivity.super.onBackPressed();
                    }
                })
                .show();

    }

    public void showMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

    }

    public void onDobClick(View view) {
        showDialog(DATE_PICKER_DIALOG_ID);
    }
}
