package virtuzo.abhishek.community.activity;

import android.content.Intent;
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
import virtuzo.abhishek.community.adapter.ContactCategoryListAdapter;
import virtuzo.abhishek.community.adapter.ContactPersonListAdapter;
import virtuzo.abhishek.community.config.UrlConfig;
import virtuzo.abhishek.community.model.ContactPerson;
import virtuzo.abhishek.community.model.OfficeBearer;
import virtuzo.abhishek.community.realm.RealmHelper;
import virtuzo.abhishek.community.utils.BaseUtils;
import virtuzo.abhishek.community.utils.Lang;
import virtuzo.abhishek.community.utils.Network;

public class ContactPersonListActivity extends LangSupportBaseActivity implements Network.Listener, SearchView.OnQueryTextListener {

    Toolbar toolbar;
    RecyclerView recyclerView;
    ContactPersonListAdapter contactPersonListAdapter;
    ArrayList<ContactPerson> contactPersonArrayList;
    TextView headerTextView;

    private BaseUtils baseUtils;
    private Network network;
    private AppUtils appUtils;
    private Lang lang;

    private Bundle bundle;
    private int ParentCategoryID;

    ArrayList<String> CategoryList;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_person_list);

        initToolbar();
        loadActivity();

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

        contactPersonArrayList = new ArrayList<>();

        contactPersonListAdapter = new ContactPersonListAdapter(contactPersonArrayList, this, new ContactPersonListAdapter.OnClickListener() {
            @Override
            public void onItemClick(ContactPerson contactPerson) {
                Toast.makeText(ContactPersonListActivity.this, contactPerson.getContactName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ContactPersonListActivity.this, ContactPersonDetailsActivity.class);

                Bundle bundle = new Bundle();
                bundle.putInt("ID", contactPerson.getContactID());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(contactPersonListAdapter);

        bundle = getIntent().getExtras();
        ParentCategoryID = bundle.getInt("CategoryId", 0);

        CategoryList = new ArrayList<>();
        CategoryList.addAll(bundle.getStringArrayList("CategoryList"));

        headerTextView = (TextView) findViewById(R.id.headerTextView);
        StringBuilder builder = new StringBuilder();
        for (String Category : CategoryList) {
            builder.append(Category + " > ");
        }
        headerTextView.setText(builder.toString());

        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(getString(R.string.text_search));
        searchView.setIconified(false);

        loadDataFromRealm();
    }

    private void loadDataFromRealm() {
        contactPersonArrayList.clear();
        contactPersonArrayList.addAll(RealmHelper.getInstance().getContactPeople(ParentCategoryID));
        contactPersonListAdapter.notifyDataSetChanged();
    }

    private void loadData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("CategoryId", ParentCategoryID+"");
        network.setShowProgress(true);
        network.makeRequest(map, UrlConfig.ContactCategory);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.text_menu_contacts));
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
            if (json.getInt("responseCode") == 2) {
                JSONArray array = json.getJSONArray("Payload");

                Gson gson = new Gson();
                contactPersonArrayList.clear();
                for (int i = 0; i < array.length(); i++) {
                    json = array.getJSONObject(i);
                    ContactPerson contactPerson = gson.fromJson(json.toString(), ContactPerson.class);
                    contactPersonArrayList.add(contactPerson);
                }
                contactPersonListAdapter.notifyDataSetChanged();
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
        ArrayList<ContactPerson> contactPeople = new ArrayList<>();
        contactPeople.addAll(RealmHelper.getInstance().getContactPeople(ParentCategoryID));
        contactPersonArrayList.clear();
        for (ContactPerson contactPerson : contactPeople) {
            String name = contactPerson.getContactName();
            if (name == null) {
                name = "";
            }
            name = name.trim().toLowerCase();

            String designation = contactPerson.getDesignation();
            if (designation == null) {
                designation = "";
            }
            designation = designation.trim().toLowerCase();

            String mobileNumber = contactPerson.getContactNumber();
            if (mobileNumber == null) {
                mobileNumber = "";
            }
            mobileNumber = mobileNumber.trim().toLowerCase();

            String emailId = contactPerson.getEmailId();
            if (emailId == null) {
                emailId = "";
            }
            emailId = emailId.trim().toLowerCase();

            String address = contactPerson.getAddress();
            if (address == null) {
                address = "";
            }
            address = address.trim().toLowerCase();

            if (name.contains(newText)
                    || designation.contains(newText)
                    || mobileNumber.contains(newText)
                    || emailId.contains(newText)
                    || address.contains(newText)) {
                contactPersonArrayList.add(contactPerson);
            }
        }
        contactPersonListAdapter.notifyDataSetChanged();
        return false;
    }
}
