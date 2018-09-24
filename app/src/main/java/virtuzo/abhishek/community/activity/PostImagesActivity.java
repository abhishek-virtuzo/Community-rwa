package virtuzo.abhishek.community.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.fragment.ImageViewPagerFragment;
import virtuzo.abhishek.community.utils.Lang;
import virtuzo.abhishek.community.utils.MyFunctions;

import java.util.ArrayList;
import java.util.List;

public class PostImagesActivity extends FragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private ViewPager viewPager;
    private View transView;
    private LinearLayout dotView;

    @Override
    protected void attachBaseContext(Context newBase) {
        Lang lang = new Lang(newBase);
        String value = lang.getAppLanguage();
        lang.setAppLanguage(value);
        super.attachBaseContext(lang.getmContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_images);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        transView = findViewById(R.id.transView);
        transView.setOnClickListener(this);

        dotView = (LinearLayout) findViewById(R.id.dotView);

        ArrayList<String> imageList = getIntent().getStringArrayListExtra("images");

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        for (int i = 0; i < imageList.size(); i++) {
            ImageViewPagerFragment fragment = new ImageViewPagerFragment();
            Bundle bundle = new Bundle();
            bundle.putString("imageUrl", imageList.get(i));
            fragment.setArguments(bundle);
            viewPagerAdapter.addFrag(fragment);
            if (imageList.size() > 1) {
                addDotView(i, dotView);
            }
        }
        viewPager.setOffscreenPageLimit(imageList.size());
        viewPager.setOnPageChangeListener(this);
        viewPager.setAdapter(viewPagerAdapter);

        MyFunctions.setStatusBarAndNavigationBarColor(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.transView:
                finish();
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        selectDot(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment) {
            mFragmentList.add(fragment);
        }
    }

    ArrayList<ImageView> dotList = new ArrayList<>();

    private void addDotView(int position, LinearLayout linearLayout) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        ImageView dot = new ImageView(this);
        if (position == 0) {
            dot.setImageDrawable(getResources().getDrawable(R.drawable.paging_active_bubble));
            params.setMargins(0, 8, 0, 0);
        } else {
            dot.setImageDrawable(getResources().getDrawable(R.drawable.paging_inactive_bubble));
            params.setMargins(8, 8, 0, 0);
        }
        linearLayout.addView(dot, params);
        dotList.add(dot);
    }

    private void selectDot(int position) {
        for (int i = 0; i < dotList.size(); i++) {
            if (i == position) {
                dotList.get(i).setImageDrawable(getResources().getDrawable(R.drawable.paging_active_bubble));
            } else {
                dotList.get(i).setImageDrawable(getResources().getDrawable(R.drawable.paging_inactive_bubble));
            }
        }
    }
}
