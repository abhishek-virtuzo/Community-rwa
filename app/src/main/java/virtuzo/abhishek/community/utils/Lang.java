package virtuzo.abhishek.community.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.LocaleList;
import android.preference.PreferenceManager;

import java.util.Locale;

/**
 * Created by ARPIT on 19-02-2017.
 */

public class Lang {
    Context mContext;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    Locale locale;
    Configuration config;
    Listener langListener;

    public Lang(Context context) {
        mContext = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public Lang(Context context, Listener listener) {
        mContext = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        langListener = listener;
    }

    private void setStringData(String key, String value) {
        editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private String getStringData(String key) {
        return sharedPreferences.getString(key, null);
    }

    public String getAppLanguage() {
        String cLang = getStringData("lang");
        if (cLang == null) {
            cLang = "en";
        }
        return cLang;
    }

    public void setAppLanguage(String value) {
        if (value == null) {
            value = getStringData("lang");
        } else {
            if (value.equals(getStringData("lang"))) {
                if (langListener != null) {
                    langListener.onLanguageChange();
                }
            }
        }
        if (value != null) {
            locale = new Locale(value);
//            Locale.setDefault(locale);
//            config = new Configuration();
//            config.locale = locale;
//            mContext.getResources().updateConfiguration(config,
//                    mContext.getResources().getDisplayMetrics());
            config = mContext.getResources().getConfiguration();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.setLocale(locale);
                LocaleList locale1 = new LocaleList(locale);
                locale1.setDefault(locale1);
                config.setLocales(locale1);
                mContext = mContext.createConfigurationContext(config);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                config.setLocale(locale);
                mContext = mContext.createConfigurationContext(config);
            } else {
                config.locale = locale;
                mContext.getResources().updateConfiguration(config, mContext.getResources().getDisplayMetrics());
            }

            setStringData("lang", value);
        }
        if (langListener != null) {
            langListener.onLanguageChange();
        }
    }

    public void setAppLanguage(String value, boolean temp) {
        if (value == null) {
            value = getStringData("lang");
        } else {
            if (value.equals(getStringData("lang"))) {
                if (langListener != null) {
                    langListener.onLanguageChange();
                }
            }
        }
        if (value != null) {
            locale = new Locale(value);
//            Locale.setDefault(locale);
//            config = new Configuration();
//            config.locale = locale;
//            mContext.getResources().updateConfiguration(config,
//                    mContext.getResources().getDisplayMetrics());
            config = mContext.getResources().getConfiguration();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.setLocale(locale);
                LocaleList locale1 = new LocaleList(locale);
                locale1.setDefault(locale1);
                config.setLocales(locale1);
                mContext = mContext.createConfigurationContext(config);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                config.setLocale(locale);
                mContext = mContext.createConfigurationContext(config);
            } else {
                config.locale = locale;
                mContext.getResources().updateConfiguration(config, mContext.getResources().getDisplayMetrics());
            }
            if (!temp) {
                setStringData("lang", value);
            }
        }
        if (!temp) {
            if (langListener != null) {
                langListener.onLanguageChange();
            }
        }
    }

    public Context getmContext() {
        return mContext;
    }

    public interface Listener {
        public void onLanguageChange();
    }

}
