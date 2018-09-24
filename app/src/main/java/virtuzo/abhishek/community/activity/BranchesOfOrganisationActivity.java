package virtuzo.abhishek.community.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
//import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import virtuzo.abhishek.community.AppUtils;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.adapter.BranchOfOrganisation;
import virtuzo.abhishek.community.adapter.BranchesOfOrgListAdapter;
import virtuzo.abhishek.community.adapter.Event;
import virtuzo.abhishek.community.config.UrlConfig;
import virtuzo.abhishek.community.utils.BaseUtils;
import virtuzo.abhishek.community.utils.Lang;
import virtuzo.abhishek.community.utils.MyFunctions;
import virtuzo.abhishek.community.utils.Network;

import java.util.ArrayList;
import java.util.HashMap;

public class BranchesOfOrganisationActivity extends LangSupportBaseActivity implements Network.Listener {

    private Toolbar toolbar;
    RecyclerView branchesRecyclerView;
    ArrayList<BranchOfOrganisation> branchOfOrganisationArrayList;
    BranchesOfOrgListAdapter branchesOfOrgListAdapter;

    private BaseUtils baseUtils;
    private Network network;
    private AppUtils appUtils;
    private Lang lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branches_of_organisation);

        initToolbar();
        loadActivity();

        MyFunctions.setStatusBarAndNavigationBarColor(this);
    }

    private void loadActivity() {
        baseUtils = new BaseUtils(this);
        appUtils = new AppUtils(this);
        network = new Network(this, this);
        lang = new Lang(this);

        branchesRecyclerView = (RecyclerView) findViewById(R.id.branchesRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        branchesRecyclerView.setLayoutManager(mLayoutManager);
        branchesRecyclerView.setItemAnimator(new DefaultItemAnimator());

        branchOfOrganisationArrayList = new ArrayList<>();

        branchesOfOrgListAdapter = new BranchesOfOrgListAdapter(branchOfOrganisationArrayList, this, new BranchesOfOrgListAdapter.OnClickListener() {
            @Override
            public void onItemClick(BranchOfOrganisation branchOfOrganisation) {
//                Toast.makeText(getApplicationContext(), branchOfOrganisation.getBranchName(), Toast.LENGTH_SHORT).show();
                if (baseUtils.isNetworkAvailable()) {
                    Intent intent = new Intent(getApplicationContext(), BranchOfOrgViewActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putString("ID", branchOfOrganisation.getId());
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        branchesRecyclerView.setAdapter(branchesOfOrgListAdapter);

        loadData();

    }

    private void loadData() {
        HashMap<String, String> map = new HashMap<>();

        network.makeRequest(map, UrlConfig.BranchList);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.text_menu_branches_of_organisation));
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
        String savedResponse = appUtils.getBranchesResponse();
        Log.e("Response", response + "");
        try {
            JSONObject json = new JSONObject(response);
            if (json.getInt("responseCode") == 1) {
                JSONArray array = json.getJSONArray("Payload");

                Gson gson = new Gson();
                branchOfOrganisationArrayList.clear();
                for (int i = 0; i < array.length(); i++) {
                    json = array.getJSONObject(i);
                    BranchOfOrganisation branchOfOrganisation = gson.fromJson(json.toString(), BranchOfOrganisation.class);
                    branchOfOrganisationArrayList.add(branchOfOrganisation);
                }
                appUtils.setBranchesResponse(response);
                branchesOfOrgListAdapter.notifyDataSetChanged();
            } else {
                if (savedResponse == null) {
                    Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show();
                } else {
                    onNetworkSuccess(savedResponse, url);
                }
            }
        } catch (JSONException e) {
            if (savedResponse != null) {
                onNetworkSuccess(savedResponse, url);
            }
        }
    }

    @Override
    public void onNetworkError(String error, String url) {
        Log.e("onNetworkError", error + "");
        String savedResponse = appUtils.getBranchesResponse();
        if (savedResponse == null) {
            Toast.makeText(this, getResources().getString(R.string.msg_common), Toast.LENGTH_LONG).show();
        } else {
            onNetworkSuccess(savedResponse, url);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        network.cancelRequest();
    }

}
