package virtuzo.abhishek.community.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import virtuzo.abhishek.community.config.UrlConfig;
import virtuzo.abhishek.community.model.ContactCategory;
import virtuzo.abhishek.community.realm.RealmHelper;
import virtuzo.abhishek.community.utils.BaseUtils;
import virtuzo.abhishek.community.utils.Lang;
import virtuzo.abhishek.community.utils.MyFunctions;
import virtuzo.abhishek.community.utils.Network;

public class ContactCategoryActivity extends LangSupportBaseActivity implements Network.Listener {

    Toolbar toolbar;
    RecyclerView recyclerView;
    ContactCategoryListAdapter contactCategoryListAdapter;
    ArrayList<ContactCategory> contactCategories;
    TextView headerTextView;

    private BaseUtils baseUtils;
    private Network network;
    private AppUtils appUtils;
    private Lang lang;

    private Bundle bundle;
    private int ParentCategoryID;

    ArrayList<String> CategoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_category);

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

        contactCategories = new ArrayList<>();

        contactCategoryListAdapter = new ContactCategoryListAdapter(contactCategories, this, new ContactCategoryListAdapter.OnClickListener() {
            @Override
            public void onItemClick(ContactCategory contactCategory) {
//                Toast.makeText(ContactCategoryActivity.this, contactCategory.getCategoryName(), Toast.LENGTH_SHORT).show();
                if (contactCategory.getChildCount() > 0) {
                    Intent intent = new Intent(ContactCategoryActivity.this, ContactCategoryActivity.class);
                    CategoryList.add(contactCategory.getCategoryName());

                    Bundle bundle = new Bundle();
                    bundle.putInt("CategoryId", contactCategory.getID());
                    bundle.putStringArrayList("CategoryList", CategoryList);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 101);

                } else {
//                    Toast.makeText(ContactCategoryActivity.this, contactCategory.getCategoryName(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ContactCategoryActivity.this, ContactPersonListActivity.class);
                    CategoryList.add(contactCategory.getCategoryName());

                    Bundle bundle = new Bundle();
                    bundle.putInt("CategoryId", contactCategory.getID());
                    bundle.putStringArrayList("CategoryList", CategoryList);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 102);

                }
            }
        });
        recyclerView.setAdapter(contactCategoryListAdapter);

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

//        loadData();
        loadDataFromRealm();
    }

    private void loadDataFromRealm() {
        contactCategories.clear();
        contactCategories.addAll(RealmHelper.getInstance().getContactCategories(ParentCategoryID));
        contactCategoryListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 || requestCode == 102) {
            CategoryList.remove(CategoryList.size() - 1);
        }
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
        getSupportActionBar().setTitle(getResources().getString(R.string.text_menu_importantnumbers));
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
                contactCategories.clear();
                for (int i = 0; i < array.length(); i++) {
                    json = array.getJSONObject(i);
                    ContactCategory contactCategory = gson.fromJson(json.toString(), ContactCategory.class);
                    contactCategories.add(contactCategory);
                }
                contactCategoryListAdapter.notifyDataSetChanged();
            } else {
                    Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {

        }
    }

    @Override
    public void onNetworkError(String error, String url) {

    }
}
