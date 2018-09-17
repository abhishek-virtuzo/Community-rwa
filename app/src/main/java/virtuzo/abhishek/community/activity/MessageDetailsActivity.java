package virtuzo.abhishek.community.activity;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;

import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.model.Message;
import virtuzo.abhishek.community.model.OfficeBearer;
import virtuzo.abhishek.community.realm.RealmHelper;

public class MessageDetailsActivity extends LangSupportBaseActivity {

    Bundle bundle;
    int messageId;
    Message message;

    Toolbar toolbar;
    ImageView imageView;
    ProgressBar imageProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);

        initActivity();
    }

    private void initActivity() {
        imageView = (ImageView) findViewById(R.id.imageView);
        imageProgressBar = findViewById(R.id.imageProgressBar);

        bundle = getIntent().getExtras();
        messageId = bundle.getInt("ID", 0);
        message = RealmHelper.getInstance().getMessage(messageId);

        initToolbar();
        if (message != null) {
            initMessage();
        }
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.text_menu_messages));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initMessage() {
        getSupportActionBar().setTitle(message.getName());

        Glide.with(this).load(message.getMessageImageUrl()).crossFade().thumbnail(1f).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                imageProgressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                imageProgressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(imageView);
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
