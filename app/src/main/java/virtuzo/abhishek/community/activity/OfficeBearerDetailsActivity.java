package virtuzo.abhishek.community.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;

import de.hdodenhof.circleimageview.CircleImageView;
import virtuzo.abhishek.community.AppUtils;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.model.OfficeBearer;
import virtuzo.abhishek.community.realm.RealmHelper;
import virtuzo.abhishek.community.utils.BaseUtils;
import virtuzo.abhishek.community.utils.Lang;
import virtuzo.abhishek.community.utils.MyFunctions;
import virtuzo.abhishek.community.utils.Network;

public class OfficeBearerDetailsActivity extends LangSupportBaseActivity {

    CircleImageView profileImage;
    TextView nameTextView, designationTextView, emailTextView, mobileTextView, addressTextView;
    LinearLayout designationLayout, emailLayout, mobileLayout, addressLayout;

    Bundle bundle;
    int officeBearerID;

    OfficeBearer officeBearer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office_bearer_details);

        initActivity();

        MyFunctions.setStatusBarAndNavigationBarColor(this);
    }

    private void initActivity() {
        profileImage = (CircleImageView) findViewById(R.id.profileImage);

        nameTextView = (TextView) findViewById(R.id.nameTextView);
        designationTextView = (TextView) findViewById(R.id.designationTextView);
        addressTextView = (TextView) findViewById(R.id.addressTextView);
        emailTextView = (TextView) findViewById(R.id.emailTextView);
        mobileTextView = (TextView) findViewById(R.id.mobileTextView);

        designationLayout = (LinearLayout) findViewById(R.id.designationLayout);
        addressLayout = (LinearLayout) findViewById(R.id.addressLayout);
        emailLayout = (LinearLayout) findViewById(R.id.emailLayout);
        mobileLayout = (LinearLayout) findViewById(R.id.mobileLayout);

        profileImage.setImageResource(R.drawable.ic_userblank);

        bundle = getIntent().getExtras();
//        String officeBearerString = bundle.getString("OfficeBearer", "");
//        Gson gson = new Gson();
//        officeBearer = gson.fromJson(officeBearerString, OfficeBearer.class);

        officeBearerID = bundle.getInt("ID", 0);
        officeBearer = RealmHelper.getInstance().getOfficeBearer(officeBearerID);
        if (officeBearer != null) {
            initContent();
        }
    }

    private void initContent() {
        Glide.with(this).load(officeBearer.getProfileUrl()).crossFade().thumbnail(1f).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                return false;
            }
        }).into(profileImage);

        nameTextView.setText(officeBearer.getName());

        if (MyFunctions.StringLength(officeBearer.getDesignation()) != 0) {
            designationTextView.setText(officeBearer.getDesignation());
        } else {
            designationLayout.setVisibility(View.GONE);
        }

        if (MyFunctions.StringLength(officeBearer.getAddress()) != 0) {
            addressTextView.setText(officeBearer.getAddress());
        } else {
            addressLayout.setVisibility(View.GONE);
        }

        if (MyFunctions.StringLength(officeBearer.getEmailId()) != 0) {
            emailTextView.setText(officeBearer.getEmailId());
            emailLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contactEmail(officeBearer.getEmailId());
                }
            });
        } else {
            emailLayout.setVisibility(View.GONE);
        }

        if (MyFunctions.StringLength(officeBearer.getMobileNumber()) != 0) {
            mobileTextView.setText(officeBearer.getMobileNumber());
            mobileLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contactDial(officeBearer.getMobileNumber());
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

}
