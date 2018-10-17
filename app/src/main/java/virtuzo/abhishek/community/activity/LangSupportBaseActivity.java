package virtuzo.abhishek.community.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import virtuzo.abhishek.community.utils.Lang;

/**
 * Created by Abhishek Aggarwal on 7/17/2018.
 */

public class LangSupportBaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        Lang lang = new Lang(newBase);
        String value = lang.getAppLanguage();
        lang.setAppLanguage(value);
        super.attachBaseContext(lang.getmContext());
    }

}
