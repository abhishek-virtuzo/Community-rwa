package virtuzo.abhishek.community.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.adapter.BranchOfOrganisation;
import virtuzo.abhishek.community.adapter.Event;
import virtuzo.abhishek.community.config.UrlConfig;
import virtuzo.abhishek.community.utils.MyFunctions;
import virtuzo.abhishek.community.utils.Network;

public class BranchOfOrgViewActivity extends LangSupportBaseActivity implements Network.Listener, View.OnClickListener {

    Bundle bundle;
    BranchOfOrganisation branchOfOrganisation;
    Network network;
    ImageView branchImageView;//, interestedImageView;
    TextView branchNameTextView, timingsTextView, venueTextView, descriptionTextView, interestedTextView, imageNotFoundTextView;
    ProgressBar branchImageProgressBar;
    LinearLayout mapsButtonView, emailButtonView, contactNumberButtonView;//, interestedButtonView;
    ProgressBar loadingProgressBar;
    ScrollView scrollView;
//    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_of_org_view);

        branchImageView = (ImageView) findViewById(R.id.branchImageView);
//        interestedImageView = findViewById(R.id.interestedImageView);

        branchNameTextView = findViewById(R.id.branchNameTextView);
        timingsTextView = findViewById(R.id.timingsTextView);
        venueTextView = findViewById(R.id.venueTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        interestedTextView = findViewById(R.id.interestedTextView);
        imageNotFoundTextView = findViewById(R.id.imageNotFoundTextView);

        branchImageProgressBar = findViewById(R.id.branchImageProgressBar);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);

        scrollView = findViewById(R.id.scrollView);

        mapsButtonView = findViewById(R.id.mapsButtonView);
        emailButtonView = findViewById(R.id.emailButtonView);
        contactNumberButtonView = findViewById(R.id.contactNumberButtonView);
//        interestedButtonView = findViewById(R.id.interestedButtonView);

        mapsButtonView.setOnClickListener(this);
        emailButtonView.setOnClickListener(this);
        contactNumberButtonView.setOnClickListener(this);
//        interestedButtonView.setOnClickListener(this);

        scrollView.setVisibility(View.GONE);
        loadingProgressBar.setVisibility(View.VISIBLE);

        bundle = getIntent().getExtras();
        branchOfOrganisation = new BranchOfOrganisation();
        branchOfOrganisation.setId(bundle.getString("ID"));

//        initToolbar();

        loadContent();

        MyFunctions.setStatusBarAndNavigationBarColor(this);
    }

//    private void initToolbar() {
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadContent() {
        network = new Network(this, this);
        HashMap<String, String> map = new HashMap<>();
        map.put("ID", branchOfOrganisation.getId());

        network.makeRequest(map, UrlConfig.BranchList);
    }

    @Override
    public void onNetworkSuccess(String response, String url) {
        if (response == null) {
            return;
        }

        Log.e("Response", response + "");
//        progressDialog.dismiss();
        try {
            JSONObject json = new JSONObject(response);
            if (json.getInt("responseCode") == 1) {
                JSONArray array = json.getJSONArray("Payload");
                Gson gson = new Gson();
                if (array.length() > 0) {
                    branchOfOrganisation = gson.fromJson(array.get(0).toString(), BranchOfOrganisation.class);
                    initContent(branchOfOrganisation);
                    return;
                }
            }

        } catch (JSONException e) {
            Log.e("CircularView", e.getMessage());
        }

    }

    private void initContent(BranchOfOrganisation branchOfOrganisation) {
        branchNameTextView.setText(branchOfOrganisation.getBranchName());
        timingsTextView.setText("Founded on " + branchOfOrganisation.getDate());
        venueTextView.setText(branchOfOrganisation.getAddress());
        descriptionTextView.setText(branchOfOrganisation.getDescription());

        Glide.with(this).load(branchOfOrganisation.getImageUrl()).crossFade().thumbnail(0.1f).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                branchImageProgressBar.setVisibility(View.GONE);
                imageNotFoundTextView.setVisibility(View.VISIBLE);
//                eventImageView.setImageResource(R.drawable.image_background);
                return true;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                branchImageProgressBar.setVisibility(View.GONE);
                imageNotFoundTextView.setVisibility(View.GONE);
                return false;
            }
        }).into(branchImageView);

        scrollView.setVisibility(View.VISIBLE);
        loadingProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onNetworkError(String error, String url) {
        loadingProgressBar.setVisibility(View.GONE);
        Toast.makeText(this, "Something went wrong...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
//        if (v.getId() == interestedButtonView.getId()) {
//            if (event.isInterested() == false) {
//                interestedImageView.setImageResource(R.drawable.star_blue);
//                interestedTextView.setTextColor(getResources().getColor(R.color.colorAccent));
//                event.setInterested(true);
//            } else {
//                interestedImageView.setImageResource(R.drawable.star);
//                interestedTextView.setTextColor(getResources().getColor(R.color.colorText));
//                event.setInterested(false);
//            }
//        } else
        if (v.getId() == mapsButtonView.getId()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String uri;
            try {
                uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f(%s)", Double.parseDouble(branchOfOrganisation.getLatitude()), Double.parseDouble(branchOfOrganisation.getLongitude()), Double.parseDouble(branchOfOrganisation.getLatitude()), Double.parseDouble(branchOfOrganisation.getLongitude()), branchOfOrganisation.getAddress());
            } catch (Exception e) {
                uri = String.format(Locale.ENGLISH, "geo:%s,%s?q=%s,%s(%s)", branchOfOrganisation.getLatitude(), branchOfOrganisation.getLongitude(), branchOfOrganisation.getLatitude(), branchOfOrganisation.getLongitude(), branchOfOrganisation.getAddress());
            }
            intent.setData(Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);
        } else if (v.getId() == emailButtonView.getId()) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setData(Uri.parse("mailto:"));
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[] { branchOfOrganisation.getEmaiId() } );
            intent.putExtra(Intent.EXTRA_SUBJECT, "Enquiry:: ");
            intent.putExtra(Intent.EXTRA_TEXT, "Text message");
            startActivity(Intent.createChooser(intent, "Send Email"));
        } else if (v.getId() == contactNumberButtonView.getId()) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + branchOfOrganisation.getContactNumber()));
            startActivity(intent);
        }
    }

}
