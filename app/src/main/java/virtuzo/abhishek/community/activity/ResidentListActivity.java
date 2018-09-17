package virtuzo.abhishek.community.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.realm.Realm;
import virtuzo.abhishek.community.AppUtils;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.adapter.ResidentListAdapter;
import virtuzo.abhishek.community.model.Resident;
import virtuzo.abhishek.community.model.ResidentBlock;
import virtuzo.abhishek.community.realm.RealmHelper;
import virtuzo.abhishek.community.utils.BaseUtils;
import virtuzo.abhishek.community.utils.Lang;
import virtuzo.abhishek.community.utils.Network;
import android.support.v7.widget.SearchView;

public class ResidentListActivity extends LangSupportBaseActivity implements SearchView.OnQueryTextListener {

    Toolbar toolbar;
    RecyclerView recyclerView;
    ResidentListAdapter residentListAdapter;
    ArrayList<Resident> residentArrayList;
//    TextView headerTextView;

//    private Bundle bundle;
//    private int BlockID;

//    ArrayList<String> CategoryList;
    SearchView searchView;
    ResidentBlock chosenResidentBlock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resident_list);

        initToolbar();
        loadActivity();

    }

    private void loadActivity() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        residentArrayList = new ArrayList<>();

        residentListAdapter = new ResidentListAdapter(residentArrayList, this, new ResidentListAdapter.OnClickListener() {
            @Override
            public void onItemClick(Resident resident) {
                Toast.makeText(ResidentListActivity.this, resident.getResidentName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ResidentListActivity.this, ResidentDetailsActivity.class);

                Bundle bundle = new Bundle();
                bundle.putInt("ID", resident.getID());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(residentListAdapter);

//        bundle = getIntent().getExtras();
//        BlockID = bundle.getInt("ID", 0);

//        CategoryList = new ArrayList<>();
//        CategoryList.addAll(bundle.getStringArrayList("CategoryList"));

//        headerTextView = (TextView) findViewById(R.id.headerTextView);
//        StringBuilder builder = new StringBuilder();
//        for (String Category : CategoryList) {
//            builder.append(Category + " > ");
//        }
//        headerTextView.setText(builder.toString());

//        chosenResidentBlock = new ResidentBlock(0, "All");
        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(getString(R.string.text_search));
        searchView.setIconified(false);

        loadDataFromRealm();
    }

    private void loadDataFromRealm() {
        residentArrayList.clear();
//        residentArrayList.addAll(RealmHelper.getInstance().getResidents(BlockID));
        residentArrayList.addAll(RealmHelper.getInstance().getResidents());
        residentListAdapter.notifyDataSetChanged();
    }

//    TextView filterTextView;

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);

//        LayoutInflater mInflater = LayoutInflater.from(this);
//        View mCustomView = mInflater.inflate(R.layout.layout_filter_residents, null);
//        filterTextView = mCustomView.findViewById(R.id.filterTextView);
//        toolbar.addView(mCustomView, new Toolbar.LayoutParams(Gravity.CENTER_VERTICAL | Gravity.RIGHT));
//
//        mCustomView.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//            }
//
//        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.text_menu_blockresidents));
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
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.trim().toLowerCase();
        ArrayList<Resident> residents = new ArrayList<>();
//        if (chosenResidentBlock.getID() == 0) {
            residents.addAll(RealmHelper.getInstance().getResidents());
//        } else {
//            residents.addAll(RealmHelper.getInstance().getResidents(chosenResidentBlock.getID()));
//        }
        residentArrayList.clear();
        for (Resident resident : residents) {
            String name = resident.getResidentName();
            if (name == null) {
                name = "";
            }
            name = name.trim().toLowerCase();

            String contactNumber = resident.getContactNumber();
            if (contactNumber == null) {
                contactNumber = "";
            }
            contactNumber = contactNumber.trim().toLowerCase();

            String houseNumber = resident.getResidentName();
            if (houseNumber == null) {
                houseNumber = "";
            }
            houseNumber = houseNumber.trim().toLowerCase();

            if (name.contains(newText)
                    || contactNumber.contains(newText)
                    || houseNumber.contains(newText)) {
                residentArrayList.add(resident);
            }
        }
        residentListAdapter.notifyDataSetChanged();

        return false;
    }
}
