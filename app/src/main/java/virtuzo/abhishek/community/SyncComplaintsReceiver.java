package virtuzo.abhishek.community;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmResults;
import virtuzo.abhishek.community.activity.ComplaintListActivity;
import virtuzo.abhishek.community.config.UrlConfig;
import virtuzo.abhishek.community.model.Complaint;
import virtuzo.abhishek.community.model.ContactCategory;
import virtuzo.abhishek.community.realm.RealmHelper;
import virtuzo.abhishek.community.utils.BaseUtils;
import virtuzo.abhishek.community.utils.MyFunctions;
import virtuzo.abhishek.community.utils.Network;

/**
 * Created by Abhishek Aggarwal on 8/24/2018.
 */

public class SyncComplaintsReceiver extends BroadcastReceiver implements Network.Listener {

    public static final String TAG = "SyncComplaintsReceiver";
    Context context;
    private boolean allow = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive called - " + intent.getAction());
        this.context = context;

        if (allow)
            syncComplaints();
    }

    public void syncComplaints() {
        if (BaseUtils.getInstance(context).isNetworkAvailable()) {
        //    Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Connected");
            ArrayList<Complaint> unsyncedComplaints = RealmHelper.getInstance().getUnsyncedComplaints();

            Network network = new Network(context, this);
//                network.setShowProgress(true);

            HashMap<String, String> map = new HashMap<>();
            map.put("SubscriberId", AppUtils.getInstance(context).getSubscriberId());
            if (unsyncedComplaints.size() == 0) {
                allow = false;
                network.makeRequest(map, UrlConfig.GetComplaintList);
            } else {
                allow = false;
                JSONObject jsonObject = new JSONObject();

                Gson gson = new Gson();
                JSONArray jsonArray = new JSONArray();
                for(Complaint complaint : unsyncedComplaints) {
                    JSONObject jsonObject1 = null;

                    // Only few parameters are required to send to server, thus reducing the size of url json
                    Complaint.ComplaintToSend complaintToSend = complaint.getComplaintToSend(complaint);
                    try {
                        jsonObject1 = new JSONObject(gson.toJson(complaintToSend));
                        jsonObject.put("Payload", jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    jsonArray.put(jsonObject1);
                }

                Log.e("URL Json", jsonObject.toString());
                map.put("complaintsList", jsonObject.toString());
                network.makeRequest(map, UrlConfig.SyncComplaintList);
            }

        } else {
            Log.e(TAG, "Not connected");
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ComplaintListActivity.STOP_Refresh);
            context.sendBroadcast(intent);
        }
    }

    @Override
    public void onNetworkSuccess(String response, String url) {
        allow = true;
        Intent intent = new Intent(ComplaintListActivity.STOP_Refresh);
        context.sendBroadcast(intent);
        if (response == null) {
            return;
        }
        try {
            JSONObject json = new JSONObject(response);
            if (json.getInt("responseCode") == 1) {
                JSONArray array = json.getJSONArray("Payload");

                Gson gson = new Gson();
                Realm realm = AppController.realm;
                realm.beginTransaction();
                RealmResults<Complaint> complaints = realm.where(Complaint.class).findAll();
                complaints.deleteAllFromRealm();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    Complaint complaint = gson.fromJson(object.toString(), Complaint.class);
                    realm.copyToRealm(complaint);
                }
                realm.commitTransaction();

                intent = new Intent(TAG);
                context.sendBroadcast(intent);
                String msg = json.getString("message");
                Log.e(TAG, msg);
             //   Toast.makeText(context, json.getString("message"), Toast.LENGTH_SHORT).show();
            } else if(json.getInt("responseCode") == 0) {
                String msg = json.getString("message");
                Log.e(TAG, msg);
          //      Toast.makeText(context, json.getString("message"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error occured - "+response);
         //   Toast.makeText(context, "Error occured", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onNetworkError(String error, String url) {
        allow = true;
        Intent intent = new Intent(ComplaintListActivity.STOP_Refresh);
        context.sendBroadcast(intent);
        Log.e(TAG, error);
     //   Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
    }
}
