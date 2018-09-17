package virtuzo.abhishek.community.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import virtuzo.abhishek.community.AppUtils;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.SyncComplaintsReceiver;
import virtuzo.abhishek.community.adapter.ComplaintListAdapter;
import virtuzo.abhishek.community.config.UrlConfig;
import virtuzo.abhishek.community.model.Complaint;
import virtuzo.abhishek.community.realm.RealmHelper;
import virtuzo.abhishek.community.utils.BaseUtils;
import virtuzo.abhishek.community.utils.MyFunctions;
import virtuzo.abhishek.community.utils.Network;

public class ComplaintListActivity extends LangSupportBaseActivity implements Network.Listener {

    Toolbar toolbar;
    Network network;
    private static final int REQUESTCODE_NewComplaint = 201;

    ArrayList<Complaint> complaintArrayList;
    ComplaintListAdapter complaintListAdapter;
    RecyclerView recyclerView;
    TextView noContactsTextView;
    SwipeRefreshLayout swipeRefreshLayout;
    public static final String STOP_Refresh = "StopRefresh";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_list);

        initToolbar();
        initActivity();
    }

    private void initActivity() {
        network = new Network(this, this);

        noContactsTextView = findViewById(R.id.noContactsTextView);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        complaintArrayList = new ArrayList<>();
        complaintListAdapter = new ComplaintListAdapter(complaintArrayList, this, new ComplaintListAdapter.OnClickListener() {
            @Override
            public void onItemClick(Complaint complaint) {
                showComplaintDetails(complaint);
            }
        });
        recyclerView.setAdapter(complaintListAdapter);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (syncComplaintsReceiver != null) {
                    syncComplaintsReceiver.syncComplaints();
                }
            }
        });
    }

    private void showComplaintDetails(final Complaint complaint) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ComplaintListActivity.this);
        final View view = getLayoutInflater().inflate(R.layout.complaint_item_view, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        TextView titleTextView, descriptionTextView, complaintDtTmTextView, statusTextView, finalReplyTextView;
        titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        descriptionTextView = (TextView) view.findViewById(R.id.descriptionTextView);
        complaintDtTmTextView = (TextView) view.findViewById(R.id.complaintDtTmTextView);
        statusTextView = (TextView) view.findViewById(R.id.statusTextView);
        finalReplyTextView = (TextView) view.findViewById(R.id.finalReplyTextView);
        ImageView attachImageView = view.findViewById(R.id.attachImageView);
        LinearLayout finalReplyLayout = view.findViewById(R.id.finalReplyLayout);

        if (MyFunctions.StringLength(complaint.getPicUrl()) > 0) {
            Glide.with(this).load(complaint.getPicUrl()).placeholder(R.drawable.image_background).dontAnimate().into(attachImageView);
            attachImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> list = new ArrayList<>();
                    list.add(complaint.getPicUrl());
                    Intent intent = new Intent(ComplaintListActivity.this, PostImagesActivity.class);
                    intent.putStringArrayListExtra("images", list);
                    startActivity(intent);
                }
            });
        } else {
            attachImageView.setVisibility(View.GONE);
        }
        titleTextView.setText(complaint.getTitle());
        descriptionTextView.setText(complaint.getDescription());
        String dateTime = MyFunctions.convertDateTimeFormat(complaint.getCreatedDtTm());
        complaintDtTmTextView.setText(dateTime);
        statusTextView.setText(complaint.getStatusName());
        if (complaint.getSynced() == 1) {
            switch (complaint.getStatusId()) {
                case 1: // Open
                    statusTextView.setBackgroundColor(getResources().getColor(R.color.Red));
                    break;

                case 2: // Closed
                    statusTextView.setBackgroundColor(getResources().getColor(R.color.DarkGreen));
                    break;

                default:
                    statusTextView.setBackgroundColor(getResources().getColor(R.color.Orange));
                    break;
            }
        } else {
            statusTextView.setBackgroundColor(getResources().getColor(R.color.lightGrey));
        }

        if (MyFunctions.StringLength(complaint.getFinalReply()) > 0) {
            finalReplyTextView.setText(complaint.getFinalReply());
        } else {
            finalReplyLayout.setVisibility(View.GONE);
        }

    }

    public void onAddNewComplaintButtonClick(View view) {
        Intent intent = new Intent(ComplaintListActivity.this, NewComplaintActivity.class);
        startActivityForResult(intent, REQUESTCODE_NewComplaint);
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshList();
        setBroadcastReceivers();
    }

    private void refreshList() {
        complaintArrayList.clear();
        complaintArrayList.addAll(RealmHelper.getInstance().getComplaints());
        Collections.reverse(complaintArrayList);
        if (complaintArrayList.size() == 0) {
            noContactsTextView.setVisibility(View.VISIBLE);
        } else {
            noContactsTextView.setVisibility(View.GONE);
        }
        complaintListAdapter.notifyDataSetChanged();

    }

    IntentFilter intentFilter;
    SyncComplaintsReceiver syncComplaintsReceiver;
    BroadcastReceiver broadcastReceiver;
    BroadcastReceiver stopRefreshBroadcastReceiver;

    private void setBroadcastReceivers() {
        intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        syncComplaintsReceiver = new SyncComplaintsReceiver();
        registerReceiver(syncComplaintsReceiver, intentFilter);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                refreshList();
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(SyncComplaintsReceiver.TAG));

        stopRefreshBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                stopRefreshing();
            }
        };
        registerReceiver(stopRefreshBroadcastReceiver, new IntentFilter(ComplaintListActivity.STOP_Refresh));
    }

    private void stopRefreshing() {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        unsetBroadcastReceivers();
    }

    private void unsetBroadcastReceivers() {
        unregisterReceiver(syncComplaintsReceiver);
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(stopRefreshBroadcastReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE_NewComplaint) {
            if (resultCode == RESULT_OK) {
                if (!BaseUtils.getInstance(this).isNetworkAvailable()) {
                    popupMessage(getString(R.string.msg_complaint_saved_offline));
                }
            }
        }
    }

    private void makeNetworkCall() {
        ArrayList<Complaint> unsyncedComplaints = RealmHelper.getInstance().getUnsyncedComplaints();

        if (unsyncedComplaints.size() == 0) {
            network.setShowProgress(true);

            HashMap<String, String> map = new HashMap<>();
            map.put("SubscriberId", AppUtils.getInstance(this).getSubscriberId());
            network.makeRequest(map, UrlConfig.GetComplaintList);
        }
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_all_complaints));
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
        try {
            JSONObject json = new JSONObject(response);
            if (json.getInt("responseCode") == 1) {
                JSONArray array = json.getJSONArray("Payload");
                complaintArrayList.clear();

                Gson gson = new Gson();
                for (int i = 0; i < array.length(); i++) {
                    json = array.getJSONObject(i);

                    Complaint complaint = gson.fromJson(json.toString(), Complaint.class);
                    complaintArrayList.add(complaint);
                }
                complaintListAdapter.notifyDataSetChanged();
            } else if(json.getInt("responseCode") == 0) {

            }
        } catch (JSONException e) {
        }
    }

    @Override
    public void onNetworkError(String error, String url) {

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

}
