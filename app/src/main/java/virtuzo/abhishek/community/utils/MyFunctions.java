package virtuzo.abhishek.community.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import virtuzo.abhishek.community.R;

/**
 * Created by Abhishek Aggarwal on 5/7/2018.
 */

public class MyFunctions {

    public static final String IMAGE_MediaType = "2";
    public static final String AUDIO_MediaType = "3";
    public static final String VIDEO_MediaType = "1";

    public static final String Circular_UNREAD = "0";
    public static final String Circular_READ = "1";

    // App level variables
    public static final String AppNameForDeepLinks = "community"; // 1
    public static final String DashboardImagesDirectory = "http://sirrat.com/community/app/DashboardImages/"; // 2
    // PathPrefix for deep links specified in manifest file // 3
    // App Name in strings.xml // 4


    public static int StringLength(String s) {
        if(s == null) {
            return 0;
        }
        return s.length();
    }

    public static String getCurrentDateTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm");
        String datetime = df.format(c.getTime());
        return datetime;
    }

    public static String convertDateFormat(String inputDate, Context context) {
        Lang lang = new Lang(context);
        Calendar cal = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            cal.setTime(sdf.parse(inputDate));
            SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM", Locale.ENGLISH);
            inputDate = df.format(cal.getTime());
            if (lang.getAppLanguage().equals("hi")) {
                df = new SimpleDateFormat("EEEE, dd MMMM", new Locale("hi","IN"));
            }
            inputDate = df.format(cal.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return inputDate;
    }

    public static String convertDateTimeFormat(String inputDate) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        try {
            cal.setTime(sdf.parse(inputDate));
            SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm", Locale.ENGLISH);
            inputDate = df.format(cal.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return inputDate;
    }

    public static String convertDobFormat(String inputDate, Context context) {
        Calendar cal = Calendar.getInstance();
        Lang lang = new Lang(context);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            cal.setTime(sdf.parse(inputDate));
            SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
            inputDate = df.format(cal.getTime());
            if (lang.getAppLanguage().equals("hi")) {
                df = new SimpleDateFormat("MMMM dd, yyyy", new Locale("hi","IN"));
            }
            inputDate = df.format(cal.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return inputDate;
    }

    public static String getCurrentDateTimeForComplaint() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = df.format(c.getTime());
        return datetime;
    }

    public static void toggleKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    public static void setStatusBarAndNavigationBarColor(Activity activity) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.status_bar_color));
            activity.getWindow().setNavigationBarColor(activity.getResources().getColor(R.color.nav_bar_color));
        }
    }

}
