package virtuzo.abhishek.community.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmResults;
import virtuzo.abhishek.community.AppController;
import virtuzo.abhishek.community.AppUtils;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.adapter.Panchang;
import virtuzo.abhishek.community.model.BloodGroup;
import virtuzo.abhishek.community.model.ContactCategory;
import virtuzo.abhishek.community.model.ContactPerson;
import virtuzo.abhishek.community.model.Message;
import virtuzo.abhishek.community.model.OfficeBearer;
import virtuzo.abhishek.community.model.PaymentLink;
import virtuzo.abhishek.community.model.Resident;
import virtuzo.abhishek.community.model.ResidentBlock;
import virtuzo.abhishek.community.utils.Lang;
import virtuzo.abhishek.community.utils.Network;

import virtuzo.abhishek.community.config.UrlConfig;

public class SyncDataActivity extends LangSupportBaseActivity implements Network.Listener {

    ProgressDialog progressDialog;
    Network network;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_data);

        network = new Network(this, this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.setMessage("Please wait, this may take a while");
        startSyncing();
    }

    private void startSyncing() {
        HashMap<String, String> map = new HashMap<>();
        map.put("languageCode", new Lang(this).getAppLanguage());
        network.makeRequest(map, UrlConfig.SyncData);

    }

    @Override
    public void onNetworkSuccess(String response, String url) {
        if (response == null) {
            progressDialog.dismiss();
            finalMessage("No response");
            return;
        }

        progressDialog.setMessage("Almost done.");

        Log.e("Response", response + "");
        try {
            JSONObject json = new JSONObject(response);
            if (json.getInt("responseCode") == 1) {
                Realm realm = AppController.realm;
                realm.beginTransaction();
                Gson gson = new Gson();

                JSONArray jsonArray = json.getJSONArray("ContactCategory");
                if (jsonArray.length() > 0) {
                    RealmResults<ContactCategory> contactCategories = realm.where(ContactCategory.class).findAll();
                    contactCategories.deleteAllFromRealm();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        ContactCategory contactCategory = gson.fromJson(object.toString(), ContactCategory.class);
                        realm.copyToRealm(contactCategory);
                    }
                }

                jsonArray = json.getJSONArray("ImportantContacts");
                if (jsonArray.length() > 0) {
                    RealmResults<ContactPerson> contactPeople = realm.where(ContactPerson.class).findAll();
                    contactPeople.deleteAllFromRealm();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        ContactPerson contactPerson = gson.fromJson(object.toString(), ContactPerson.class);
                        realm.copyToRealm(contactPerson);
                    }
                }

                jsonArray = json.getJSONArray("OfficeBearers");
                if (jsonArray.length() > 0) {
                    RealmResults<OfficeBearer> officeBearers = realm.where(OfficeBearer.class).findAll();
                    officeBearers.deleteAllFromRealm();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        OfficeBearer officeBearer = gson.fromJson(object.toString(), OfficeBearer.class);
                        realm.copyToRealm(officeBearer);
                    }
                }

                jsonArray = json.getJSONArray("Messages");
                if (jsonArray.length() > 0) {
                    RealmResults<Message> messages = realm.where(Message.class).findAll();
                    messages.deleteAllFromRealm();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        Message message = gson.fromJson(object.toString(), Message.class);
                        realm.copyToRealm(message);
                    }
                }

                jsonArray = json.getJSONArray("ResidentBlocks");
                if (jsonArray.length() > 0) {
                    RealmResults<ResidentBlock> residentBlocks = realm.where(ResidentBlock.class).findAll();
                    residentBlocks.deleteAllFromRealm();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        ResidentBlock residentBlock = gson.fromJson(object.toString(), ResidentBlock.class);
                        realm.copyToRealm(residentBlock);
                    }
                }

                jsonArray = json.getJSONArray("Residents");
                if (jsonArray.length() > 0) {
                    RealmResults<Resident> residents = realm.where(Resident.class).findAll();
                    residents.deleteAllFromRealm();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        Resident resident = gson.fromJson(object.toString(), Resident.class);
                        realm.copyToRealm(resident);
                    }
                }

                jsonArray = json.getJSONArray("Panchang");
                if (jsonArray.length() > 0) {
                    RealmResults<Panchang> panchangs = realm.where(Panchang.class).findAll();
                    panchangs.deleteAllFromRealm();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        Panchang panchang = gson.fromJson(object.toString(), Panchang.class);
                        realm.copyToRealm(panchang);
                    }
                }

                jsonArray = json.getJSONArray("BloodGroups");
                if (jsonArray.length() > 0) {
                    RealmResults<BloodGroup> bloodGroups = realm.where(BloodGroup.class).findAll();
                    bloodGroups.deleteAllFromRealm();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        BloodGroup bloodGroup = gson.fromJson(object.toString(), BloodGroup.class);
                        realm.copyToRealm(bloodGroup);
                    }
                }

                jsonArray = json.getJSONArray("PaymentLinks");
                if (jsonArray.length() > 0) {
                    RealmResults<PaymentLink> paymentLinks = realm.where(PaymentLink.class).findAll();
                    paymentLinks.deleteAllFromRealm();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        PaymentLink paymentLink = gson.fromJson(object.toString(), PaymentLink.class);
                        realm.copyToRealm(paymentLink);
                    }
                }

                realm.commitTransaction();

                // synced
                AppUtils.getInstance(this).setSynced(true);
                finalMessage(getString(R.string.msg_sync_success));
            }

        } catch (JSONException e) {
            Log.e("Sync Error", e.getMessage());
            finalMessage("Parsing error");
        } finally {
            progressDialog.dismiss();
        }

    }

    @Override
    public void onNetworkError(String error, String url) {
        progressDialog.dismiss();
        if (error.equals(getResources().getString(R.string.msg_nointernet))) {
            finalMessage(error);
        } else {
            finalMessage("Network Error");
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
                        SyncDataActivity.super.onBackPressed();
                    }
                })
                .show();

    }

}
