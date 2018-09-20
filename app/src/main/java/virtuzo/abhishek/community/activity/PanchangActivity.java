package virtuzo.abhishek.community.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import virtuzo.abhishek.community.AppUtils;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.adapter.Event;
import virtuzo.abhishek.community.adapter.Panchang;
import virtuzo.abhishek.community.adapter.PanchangListAdapter;
import virtuzo.abhishek.community.config.UrlConfig;
import virtuzo.abhishek.community.realm.RealmHelper;
import virtuzo.abhishek.community.utils.Lang;
import virtuzo.abhishek.community.utils.Network;

public class PanchangActivity extends LangSupportBaseActivity implements Network.Listener, PanchangListAdapter.OnClickListener {

    Toolbar toolbar;
    ArrayList<Panchang> panchangArrayList;
    RecyclerView recyclerView;
    Network network;
    PanchangListAdapter panchangListAdapter;
    ProgressBar loadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panchang);

        loadingProgressBar = findViewById(R.id.loadingProgressBar);

        panchangArrayList = new ArrayList<>();
        panchangListAdapter = new PanchangListAdapter(panchangArrayList, this, this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(panchangListAdapter);

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        initToolbar();
//        loadContent();
        loadDataFromRealm();
    }

    private void loadDataFromRealm() {
        panchangArrayList.clear();
        panchangArrayList.addAll(RealmHelper.getInstance().getPanchangs());
        panchangListAdapter.notifyDataSetChanged();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.text_menu_panchang));
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

    private void loadContent() {
        network = new Network(getApplicationContext(), this);
        HashMap<String, String> map = new HashMap<>();
        map.put("languageCode", new Lang(this).getAppLanguage());

        network.makeRequest(map, UrlConfig.PanchangList);
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
                Gson gson = new Gson();
                if (array.length() > 0) {
                    panchangArrayList.clear();
                    for (int i = 0; i < array.length(); i++) {
                        Panchang panchang = gson.fromJson(array.get(i).toString(), Panchang.class);
                        panchangArrayList.add(panchang);
                    }
                    panchangListAdapter.notifyDataSetChanged();
                    loadingProgressBar.setVisibility(View.GONE);
                    return;
                }
            }

        } catch (JSONException e) {
            Log.e("PanchangView", e.getMessage());
        }
        Toast.makeText(this, "Something went wrong...", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNetworkError(String error, String url) {
        loadingProgressBar.setVisibility(View.GONE);
        Toast.makeText(this, "Something went wrong...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(Panchang panchang) {

    }
}
