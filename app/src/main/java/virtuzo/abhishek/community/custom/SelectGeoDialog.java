package virtuzo.abhishek.community.custom;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import virtuzo.abhishek.community.AppUtils;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.activity.HomeActivity;
import virtuzo.abhishek.community.utils.BaseUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ARPIT on 10-03-2017.
 */

public class SelectGeoDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private List<String> c1, c2, r1, r2, co1, co2;
    private Spinner s1, s2, s3;
    private ArrayAdapter<String> da1, da2, da3;
    private Button depositeButton;
    private AppUtils appUtils;
    private BaseUtils baseUtils;

    private void initCities() {
        baseUtils = new BaseUtils(context);
        c1 = new ArrayList<String>();
        c2 = new ArrayList<String>();

        r1 = new ArrayList<String>();
        r2 = new ArrayList<String>();

        co1 = new ArrayList<String>();
        co2 = new ArrayList<String>();

        c1.add(context.getResources().getString(R.string.no_city));
        c2.add("none");
        r1.add(context.getResources().getString(R.string.no_region));
        r2.add("none");
        co1.add(context.getResources().getString(R.string.no_country));
        co2.add("none");
        try {
            JSONArray array = new JSONArray(appUtils.getFilerLocation());
            JSONObject json = array.getJSONObject(2);

            JSONArray mainArray = json.getJSONArray("city");
            for (int i = 0; i < mainArray.length(); i++) {
                c1.add(mainArray.getJSONObject(i).getString("cityName"));
                c2.add(mainArray.getJSONObject(i).getString("cityCode"));
            }

            json = array.getJSONObject(1);
            mainArray = json.getJSONArray("region");
            for (int i = 0; i < mainArray.length(); i++) {
                r1.add(mainArray.getJSONObject(i).getString("regionName"));
                r2.add(mainArray.getJSONObject(i).getString("regionCode"));
            }

            json = array.getJSONObject(0);
            mainArray = json.getJSONArray("country");
            for (int i = 0; i < mainArray.length(); i++) {
                co1.add(mainArray.getJSONObject(i).getString("countryName"));
                co2.add(mainArray.getJSONObject(i).getString("countryCode"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initDialog() {

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.layout_selectgeo);
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams wmlp = this.getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP | Gravity.RIGHT;

        this.getWindow().setAttributes(wmlp);
    }

    private void configSpinners() {
        da1 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, c1);
        da1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        da2 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, r1);
        da2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        da3 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, co1);
        da3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        depositeButton = (Button) findViewById(R.id.depositeButton);
        depositeButton.setOnClickListener(this);


        s1 = (Spinner) this.findViewById(R.id.geoSpinner1);
        s1.setAdapter(da1);

        s2 = (Spinner) this.findViewById(R.id.geoSpinner2);
        s2.setAdapter(da2);

        s3 = (Spinner) this.findViewById(R.id.geoSpinner3);
        s3.setAdapter(da3);

        String geoFiltersId = appUtils.getGeoFilterId();
        if (geoFiltersId == null) {
            if (s1.getSelectedItemPosition() == 0 && c1.size() >= 2) {
                s1.setSelection(1);
            }
            if (s2.getSelectedItemPosition() == 0 && r1.size() >= 2) {
                s2.setSelection(1);
            }
            if (s3.getSelectedItemPosition() == 0 && co1.size() >= 2) {
                s3.setSelection(1);
            }
            return;
        }
        String[] geoId = geoFiltersId.split(",");
        for (int i = 0; i < geoId.length; i++) {
            if (i == 0) {
                for (int j = 0; j < c2.size(); j++) {
                    if (geoId[i].equalsIgnoreCase(c2.get(j))) {
                        s1.setSelection(j);
                    }
                }
            } else if (i == 1) {
                for (int j = 0; j < r2.size(); j++) {
                    if (geoId[i].equalsIgnoreCase(r2.get(j))) {
                        s2.setSelection(j);
                    }
                }
            } else if (i == 2) {
                for (int j = 0; j < co2.size(); j++) {
                    if (geoId[i].equalsIgnoreCase(co2.get(j))) {
                        s3.setSelection(j);
                    }
                }
            }
        }
    }

    public SelectGeoDialog(Context context) {
        super(context);
        this.context = context;
        appUtils = AppUtils.getInstance(context);
        initCities();
        initDialog();
        configSpinners();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.depositeButton:
//                if ((s1.getSelectedItemPosition() == 0) && (s2.getSelectedItemPosition() == 0) && (s3.getSelectedItemPosition() == 0)) {
//                    //Toast.makeText(context, context.getString(R.string.msg_selectonegeo), Toast.LENGTH_SHORT).show();
//                    return;
//                }
                if (s1.getSelectedItemPosition() == 1) {
                    baseUtils.setStringData("cityFilter", null);
                } else {
                    baseUtils.setStringData("cityFilter", c1.get(s1.getSelectedItemPosition()));
                }
                if (s2.getSelectedItemPosition() == 1) {
                    baseUtils.setStringData("regionFilter", null);
                } else {
                    baseUtils.setStringData("regionFilter", r1.get(s2.getSelectedItemPosition()));
                }
                if (s3.getSelectedItemPosition() == 1) {
                    baseUtils.setStringData("countryFilter", null);
                } else {
                    baseUtils.setStringData("countryFilter", co1.get(s3.getSelectedItemPosition()));
                }

                String geographyid = c2.get(s1.getSelectedItemPosition()) + "," + r2.get(s2.getSelectedItemPosition()) + "," + co2.get(s3.getSelectedItemPosition());
                ((HomeActivity) context).makeReqCall(geographyid);
                this.dismiss();
                break;
        }
    }
}
