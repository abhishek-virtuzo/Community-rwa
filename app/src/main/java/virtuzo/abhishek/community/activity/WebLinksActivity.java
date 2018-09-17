package virtuzo.abhishek.community.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.adapter.PaymentLinkListAdapter;
import virtuzo.abhishek.community.model.PaymentLink;
import virtuzo.abhishek.community.realm.RealmHelper;

public class WebLinksActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    ArrayList<PaymentLink> paymentLinkArrayList;
    PaymentLinkListAdapter paymentLinkListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_links);

        initToolbar();
        initActivity();
    }

    private void initActivity() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        paymentLinkArrayList = new ArrayList<>();

        paymentLinkListAdapter = new PaymentLinkListAdapter(paymentLinkArrayList, this, new PaymentLinkListAdapter.OnClickListener() {
            @Override
            public void onItemClick(PaymentLink paymentLink) {
                redirectToWeb(paymentLink.getWebLink());
            }
        });
        recyclerView.setAdapter(paymentLinkListAdapter);

//        PaymentLink paymentLink = new PaymentLink();
//        paymentLink.setImageUrl("http://sirrat.com/community/eventimage/a1.jpg");
//        paymentLinkArrayList.add(paymentLink);
//
//        PaymentLink paymentLink1 = new PaymentLink();
//        paymentLink1.setImageUrl("http://sirrat.com/community/eventimage/a2.jpg");
//        paymentLinkArrayList.add(paymentLink1);
//
//        PaymentLink paymentLink2 = new PaymentLink();
//        paymentLink2.setImageUrl("http://sirrat.com/community/eventimage/a3.jpg");
//        paymentLinkArrayList.add(paymentLink2);
//
//        PaymentLink paymentLink3 = new PaymentLink();
//        paymentLink3.setImageUrl("http://sirrat.com/community/eventimage/b1.jpg");
//        paymentLinkArrayList.add(paymentLink3);
//
//        PaymentLink paymentLink4 = new PaymentLink();
//        paymentLink4.setImageUrl("http://sirrat.com/community/eventimage/b2.jpg");
//        paymentLinkArrayList.add(paymentLink4);

        paymentLinkArrayList.addAll(RealmHelper.getInstance().getPaymentLinks());

        paymentLinkListAdapter.notifyDataSetChanged();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.text_menu_weblinks));
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

    public void redirectToWeb(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Invalid link", Toast.LENGTH_SHORT).show();
        }
    }
}
