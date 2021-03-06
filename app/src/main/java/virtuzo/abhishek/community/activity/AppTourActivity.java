package virtuzo.abhishek.community.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.github.paolorotolo.appintro.AppIntro;
import virtuzo.abhishek.community.AppUtils;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.TourFragment;
import virtuzo.abhishek.community.utils.BaseUtils;
import virtuzo.abhishek.community.utils.Lang;
import virtuzo.abhishek.community.utils.MyFunctions;

public class AppTourActivity extends AppIntro {

    BaseUtils baseUtils;

    @Override
    protected void attachBaseContext(Context newBase) {
        Lang lang = new Lang(newBase);
        String value = lang.getAppLanguage();
        lang.setAppLanguage(value);
        super.attachBaseContext(lang.getmContext());
    }

    public void setTaskBarColored(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //status bar height
            int statusBarHeight = baseUtils.getStatusBarHeight();
            View view = new View(this);
            view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            view.getLayoutParams().height = statusBarHeight;
            ((ViewGroup) w.getDecorView()).addView(view);
            view.setBackgroundColor(color);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseUtils = new BaseUtils(this);
        setTaskBarColored(getResources().getColor(R.color.colorAccent));
        // remove title

        // Note here that we DO NOT use setContentView();


        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        String[] array = getResources().getStringArray(R.array.tutorial);
        for (int i = 0; i < array.length; i++) {
            String[] s = array[i].split("#");
            int imageId = getResources().getIdentifier("drawable/ic_" + (i + 1), null, getPackageName());
            Bundle bundle = new Bundle();
            bundle.putString("title", s[0].trim());
            bundle.putString("content", s[1].trim());
            bundle.putInt("imageid", imageId);

            TourFragment tourFragment = new TourFragment();
            tourFragment.setArguments(bundle);

            addSlide(tourFragment);
        }
        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(getResources().getColor(R.color.colorAccent));
        setSeparatorColor(Color.parseColor("#FFFFFF"));

        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);

        setSkipText(getResources().getString(R.string.skip));
        setDoneText(getResources().getString(R.string.done));

        MyFunctions.setStatusBarAndNavigationBarColor(this);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        AppUtils.getInstance(this).setFirstLaunch(true);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }

}
