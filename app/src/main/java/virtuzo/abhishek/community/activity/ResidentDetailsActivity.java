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

import de.hdodenhof.circleimageview.CircleImageView;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.model.OfficeBearer;
import virtuzo.abhishek.community.model.Resident;
import virtuzo.abhishek.community.model.ResidentBlock;
import virtuzo.abhishek.community.realm.RealmHelper;
import virtuzo.abhishek.community.utils.MyFunctions;

public class ResidentDetailsActivity extends LangSupportBaseActivity {

    CircleImageView profileImage;
    TextView nameTextView, mobileTextView, addressTextView;
    LinearLayout mobileLayout, addressLayout;

    Bundle bundle;
    int residentID;

    Resident resident;
    ResidentBlock residentBlock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resident_details);
        profileImage = (CircleImageView) findViewById(R.id.profileImage);

        nameTextView = (TextView) findViewById(R.id.nameTextView);
        addressTextView = (TextView) findViewById(R.id.addressTextView);
        mobileTextView = (TextView) findViewById(R.id.mobileTextView);

        addressLayout = (LinearLayout) findViewById(R.id.addressLayout);
        mobileLayout = (LinearLayout) findViewById(R.id.mobileLayout);

        bundle = getIntent().getExtras();

        residentID = bundle.getInt("ID", 0);
        resident = RealmHelper.getInstance().getResident(residentID);
        residentBlock = RealmHelper.getInstance().getResidentBlock(resident.getResidentBlockID());
        if (resident != null) {
            initContent();
        }

    }

    private void initContent() {
        Glide.with(this).load(resident.getProfileUrl()).crossFade().thumbnail(1f).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                return false;
            }
        }).into(profileImage);

        nameTextView.setText(resident.getResidentName());

        StringBuilder builder = new StringBuilder();
        builder.append(getResources().getString(R.string.text_houseno) + " - " + resident.getHouseNumber());
        if (residentBlock != null) {
            builder.append(", " + residentBlock.getBlockName());
        }
        String address = builder.toString();

        if (MyFunctions.StringLength(address) != 0) {
            addressTextView.setText(address);
        } else {
            addressLayout.setVisibility(View.GONE);
        }

        if (MyFunctions.StringLength(resident.getContactNumber()) != 0) {
            mobileTextView.setText(resident.getContactNumber());
            mobileLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contactDial(resident.getContactNumber());
                }
            });
        } else {
            mobileLayout.setVisibility(View.GONE);
        }

    }

    private void contactDial(String mobileNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mobileNumber));
        startActivity(intent);
    }

}
