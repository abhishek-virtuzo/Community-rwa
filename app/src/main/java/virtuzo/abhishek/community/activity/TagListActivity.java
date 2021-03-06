package virtuzo.abhishek.community.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.custom.ToolbarColorizeHelper;
import virtuzo.abhishek.community.fragment.TagListFragment;
import virtuzo.abhishek.community.utils.BaseUtils;
import virtuzo.abhishek.community.utils.MyFunctions;

public class TagListActivity extends LangSupportBaseActivity {

    private Toolbar toolbar;
    private TextView toolbarTitle, toolbarSubTitle;
    private AdView mAdView;
    private TagListFragment tagListFragment;

    //Classes
    BaseUtils baseUtils;

    private Context context = TagListActivity.this;


    public void setTaskBarColored(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //status bar height
            int statusBarHeight = baseUtils.getStatusBarHeight();
            View view = new View(context);
            view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            view.getLayoutParams().height = statusBarHeight;
            ((ViewGroup) w.getDecorView()).addView(view);
            view.setBackgroundColor(color);
        }
    }

    private void initToobar(boolean showTags) {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!showTags) {
            getSupportActionBar().setTitle("");
            int bgColor = getIntent().getIntExtra("color", 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) toolbar.getLayoutParams();
                params.setMargins(0, baseUtils.getStatusBarHeight(), 0, 0);
                toolbar.setLayoutParams(params);
            }

            toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
            toolbarSubTitle = (TextView) findViewById(R.id.toolbarSubTitle);

            String title = getResources().getString(R.string.text_sanskrit);
            if (getIntent().getIntExtra("type", 0) == 2) {
                title = getResources().getString(R.string.text_prakrit);
            }
            toolbarTitle.setText(title);
            toolbarSubTitle.setText(getIntent().getStringExtra("title"));
            toolbar.setBackgroundColor(bgColor);
            setTaskBarColored(bgColor);
            if (bgColor == getResources().getColor(R.color.colorSanskritRed)
                    || bgColor == getResources().getColor(R.color.colorSanskritBlue)
                    || bgColor == getResources().getColor(R.color.colorPrakritGreen)) {
                ToolbarColorizeHelper.colorizeToolbar(toolbar, Color.parseColor("#FFFFFF"), this);
                toolbarTitle.setTextColor(Color.parseColor("#FFFFFF"));
                toolbarSubTitle.setTextColor(Color.parseColor("#FFFFFF"));
            } else {
                ToolbarColorizeHelper.colorizeToolbar(toolbar, Color.parseColor("#000000"), this);
                toolbarTitle.setTextColor(Color.parseColor("#000000"));
                toolbarSubTitle.setTextColor(Color.parseColor("#000000"));
            }
        } else {
            getSupportActionBar().setTitle(getResources().getString(R.string.title_showtag));
        }
    }

    private void initAdmobBanner() {
        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.setVisibility(View.GONE);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_list);
        baseUtils = new BaseUtils(context);

        tagListFragment = new TagListFragment();
        Bundle bundle = getIntent().getExtras();
        bundle.putInt("color", getIntent().getIntExtra("color", 0));
        tagListFragment.setArguments(bundle);

        initToobar(bundle.getBoolean("showtags", false));
        initAdmobBanner();

        replaceFragment(R.id.fragmentFrame, tagListFragment, false, null);

        MyFunctions.setStatusBarAndNavigationBarColor(this);
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public class DetailsTransition extends TransitionSet {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public DetailsTransition() {
            setOrdering(ORDERING_TOGETHER);
            addTransition(new ChangeBounds()).
                    addTransition(new ChangeTransform()).
                    addTransition(new ChangeImageTransform());
        }

    }

    public void replaceFragment(int redId, Fragment fragment, boolean addToBackStack, View sharedView) {
        FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
        if (sharedView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                fragment.setSharedElementEnterTransition(new DetailsTransition());
//                fragment.setEnterTransition(new Fade());
//                fragment.setExitTransition(new Fade());
//                fragment.setSharedElementReturnTransition(new DetailsTransition());
            }
        }
        transaction.replace(redId, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        if (sharedView != null) {
            transaction.addSharedElement(sharedView, "sharedView");
        }
        transaction.commitAllowingStateLoss();
    }

}
