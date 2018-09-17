package virtuzo.abhishek.community.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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

import de.hdodenhof.circleimageview.CircleImageView;
import virtuzo.abhishek.community.AppUtils;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.config.UrlConfig;
import virtuzo.abhishek.community.model.ContactPerson;
import virtuzo.abhishek.community.realm.RealmHelper;
import virtuzo.abhishek.community.utils.BaseUtils;
import virtuzo.abhishek.community.utils.Lang;
import virtuzo.abhishek.community.utils.MyFunctions;
import virtuzo.abhishek.community.utils.Network;

public class ContactPersonDetailsActivity extends LangSupportBaseActivity implements Network.Listener {

    CircleImageView profileImage;
    TextView nameTextView, designationTextView, emailTextView, mobileTextView, addressTextView;
    LinearLayout designationLayout, emailLayout, mobileLayout, addressLayout;

    private BaseUtils baseUtils;
    private Network network;
    private AppUtils appUtils;
    private Lang lang;

    Bundle bundle;
    int ContactID;

    ContactPerson contactPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_person_details);

        initActivity();

//        loadData();
        loadDataFromRealm();
    }

    private void loadDataFromRealm() {
        contactPerson = RealmHelper.getInstance().getContactPerson(ContactID);
        if (contactPerson != null) {
            initContent(contactPerson);
        }
    }

    private void initActivity() {
        profileImage = (CircleImageView) findViewById(R.id.profileImage);

        nameTextView = (TextView) findViewById(R.id.nameTextView);
        designationTextView = (TextView) findViewById(R.id.designationTextView);
        emailTextView = (TextView) findViewById(R.id.emailTextView);
        mobileTextView = (TextView) findViewById(R.id.mobileTextView);
        addressTextView = (TextView) findViewById(R.id.addressTextView);

        designationLayout = (LinearLayout) findViewById(R.id.designationLayout);
        emailLayout = (LinearLayout) findViewById(R.id.emailLayout);
        mobileLayout = (LinearLayout) findViewById(R.id.mobileLayout);
        addressLayout = (LinearLayout) findViewById(R.id.addressLayout);

        baseUtils = new BaseUtils(this);
        appUtils = new AppUtils(this);
        network = new Network(this, this);
        lang = new Lang(this);

        bundle = getIntent().getExtras();
        ContactID = bundle.getInt("ID", 0);

    }

    private void loadData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("ContactId", ContactID+"");
        network.setShowProgress(true);
        network.makeRequest(map, UrlConfig.ContactDetails);
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
                if (array.length() > 0) {
                    contactPerson = gson.fromJson(array.get(0).toString(), ContactPerson.class);
                    initContent(contactPerson);
                    return;
                }
            }

        } catch (JSONException e) {
            Log.e("ContactView", e.getMessage());
        }

    }

    private void initContent(final ContactPerson contactPerson) {
        Glide.with(this).load(contactPerson.getProfileUrl()).crossFade().thumbnail(1f).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                return false;
            }
        }).into(profileImage);

        nameTextView.setText(contactPerson.getContactName());
        if (MyFunctions.StringLength(contactPerson.getDesignation()) != 0) {
            designationTextView.setText(contactPerson.getDesignation());
        } else {
            designationLayout.setVisibility(View.GONE);
        }
        if (MyFunctions.StringLength(contactPerson.getAddress()) != 0) {
            addressTextView.setText(contactPerson.getAddress());
        } else {
            addressLayout.setVisibility(View.GONE);
        }

        if (MyFunctions.StringLength(contactPerson.getEmailId()) != 0) {
            emailTextView.setText(contactPerson.getEmailId());
            emailLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contactEmail(contactPerson.getEmailId());
                }
            });
        } else {
            emailLayout.setVisibility(View.GONE);
        }
        if (MyFunctions.StringLength(contactPerson.getContactNumber()) != 0) {
            mobileTextView.setText(contactPerson.getContactNumber());
            mobileLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contactDial(contactPerson.getContactNumber());
                }
            });
        } else {
            mobileLayout.setVisibility(View.GONE);
        }

    }

    private void contactEmail(String emailId) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("mailto:"));
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { emailId } );
        intent.putExtra(Intent.EXTRA_SUBJECT, "Enquiry:: ");
        intent.putExtra(Intent.EXTRA_TEXT, "Text message");
        startActivity(Intent.createChooser(intent, "Send Email"));
    }

    private void contactDial(String mobileNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mobileNumber));
        startActivity(intent);
    }

    @Override
    public void onNetworkError(String error, String url) {
        Toast.makeText(this, "Something went wrong...", Toast.LENGTH_SHORT).show();
    }

}
