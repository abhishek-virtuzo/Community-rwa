package virtuzo.abhishek.community.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.adapter.ContactCategoryListAdapter;
import virtuzo.abhishek.community.adapter.ResidentBlockListAdapter;
import virtuzo.abhishek.community.model.ContactCategory;
import virtuzo.abhishek.community.model.ResidentBlock;
import virtuzo.abhishek.community.realm.RealmHelper;

public class ResidentBlockListActivity extends LangSupportBaseActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    ResidentBlockListAdapter residentBlockListAdapter;
    ArrayList<ResidentBlock> residentBlocks;
    TextView headerTextView;

    private Bundle bundle;

    ArrayList<String> CategoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resident_block_list);

        initToolbar();
        loadActivity();

    }

    private void loadActivity() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        residentBlocks = new ArrayList<>();

        residentBlockListAdapter = new ResidentBlockListAdapter(residentBlocks, this, new ResidentBlockListAdapter.OnClickListener() {
            @Override
            public void onItemClick(ResidentBlock residentBlock) {
                Toast.makeText(ResidentBlockListActivity.this, residentBlock.getBlockName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ResidentBlockListActivity.this, ResidentListActivity.class);
                CategoryList.add(residentBlock.getBlockName());

                Bundle bundle = new Bundle();
                bundle.putInt("ID", residentBlock.getID());
                bundle.putStringArrayList("CategoryList", CategoryList);
                intent.putExtras(bundle);
                startActivityForResult(intent, 101);

            }
        });
        recyclerView.setAdapter(residentBlockListAdapter);

        bundle = getIntent().getExtras();

        CategoryList = new ArrayList<>();
        CategoryList.addAll(bundle.getStringArrayList("CategoryList"));

        headerTextView = (TextView) findViewById(R.id.headerTextView);
        StringBuilder builder = new StringBuilder();
        for (String Category : CategoryList) {
            builder.append(Category + " > ");
        }
        headerTextView.setText(builder.toString());

        loadDataFromRealm();
    }

    private void loadDataFromRealm() {
        residentBlocks.clear();
        residentBlocks.addAll(RealmHelper.getInstance().getResidentBlocks());
        residentBlockListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            CategoryList.remove(CategoryList.size() - 1);
        }
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.text_menu_blocks));
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

}
