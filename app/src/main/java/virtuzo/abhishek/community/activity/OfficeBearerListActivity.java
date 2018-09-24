package virtuzo.abhishek.community.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
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
import virtuzo.abhishek.community.adapter.ContactPersonListAdapter;
import virtuzo.abhishek.community.adapter.OfficeBearerListAdapter;
import virtuzo.abhishek.community.config.UrlConfig;
import virtuzo.abhishek.community.model.ContactPerson;
import virtuzo.abhishek.community.model.OfficeBearer;
import virtuzo.abhishek.community.model.Resident;
import virtuzo.abhishek.community.realm.RealmHelper;
import virtuzo.abhishek.community.utils.BaseUtils;
import virtuzo.abhishek.community.utils.Lang;
import virtuzo.abhishek.community.utils.MyFunctions;
import virtuzo.abhishek.community.utils.Network;

public class OfficeBearerListActivity extends LangSupportBaseActivity implements Network.Listener, SearchView.OnQueryTextListener {

    Toolbar toolbar;
    RecyclerView recyclerView;
    OfficeBearerListAdapter officeBearerListAdapter;
    ArrayList<OfficeBearer> officeBearerArrayList;

    private BaseUtils baseUtils;
    private Network network;
    private AppUtils appUtils;
    private Lang lang;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office_bearer_list);

        initToolbar();
        loadActivity();

        MyFunctions.setStatusBarAndNavigationBarColor(this);
    }

    private void loadActivity() {
        baseUtils = new BaseUtils(this);
        appUtils = new AppUtils(this);
        network = new Network(this, this);
        lang = new Lang(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        officeBearerArrayList = new ArrayList<>();

        officeBearerListAdapter = new OfficeBearerListAdapter(officeBearerArrayList, this, new OfficeBearerListAdapter.OnClickListener() {
            @Override
            public void onItemClick(OfficeBearer officeBearer) {
                Toast.makeText(OfficeBearerListActivity.this, officeBearer.getName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OfficeBearerListActivity.this, OfficeBearerDetailsActivity.class);

                Bundle bundle = new Bundle();
                Gson gson = new Gson();
                String jsonString = gson.toJson(officeBearer);
                bundle.putInt("ID", officeBearer.getID());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(officeBearerListAdapter);

        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(getString(R.string.text_search));
        searchView.setIconified(false);

        loadDataFromRealm();
    }

    private void loadDataFromRealm() {
        officeBearerArrayList.clear();
        officeBearerArrayList.addAll(RealmHelper.getInstance().getOfficeBearers());
        officeBearerListAdapter.notifyDataSetChanged();
    }

    private void loadData() {
        HashMap<String, String> map = new HashMap<>();
        network.setShowProgress(true);
        network.makeRequest(map, UrlConfig.OfficeBearersList);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.text_menu_officebearers));
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
        try {
            JSONObject json = new JSONObject(response);
            if (json.getInt("responseCode") == 1) {
                JSONArray array = json.getJSONArray("Payload");

                Gson gson = new Gson();
                officeBearerArrayList.clear();
                for (int i = 0; i < array.length(); i++) {
                    json = array.getJSONObject(i);
                    OfficeBearer officeBearer = gson.fromJson(json.toString(), OfficeBearer.class);
                    officeBearerArrayList.add(officeBearer);
                }
                officeBearerListAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {

        }
    }

    @Override
    public void onNetworkError(String error, String url) {

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.trim().toLowerCase();
        ArrayList<OfficeBearer> officeBearers = new ArrayList<>();
        officeBearers.addAll(RealmHelper.getInstance().getOfficeBearers());
        officeBearerArrayList.clear();
        for (OfficeBearer officeBearer : officeBearers) {
            String name = officeBearer.getName();
            if (name == null) {
                name = "";
            }
            name = name.trim().toLowerCase();

            String designation = officeBearer.getDesignation();
            if (designation == null) {
                designation = "";
            }
            designation = designation.trim().toLowerCase();

            String mobileNumber = officeBearer.getMobileNumber();
            if (mobileNumber == null) {
                mobileNumber = "";
            }
            mobileNumber = mobileNumber.trim().toLowerCase();

            String emailId = officeBearer.getEmailId();
            if (emailId == null) {
                emailId = "";
            }
            emailId = emailId.trim().toLowerCase();

            String address = officeBearer.getAddress();
            if (address == null) {
                address = "";
            }
            address = address.trim().toLowerCase();

            if (name.contains(newText)
                    || designation.contains(newText)
                    || mobileNumber.contains(newText)
                    || emailId.contains(newText)
                    || address.contains(newText)) {
                officeBearerArrayList.add(officeBearer);
            }
        }
        officeBearerListAdapter.notifyDataSetChanged();
        return false;
    }
}
