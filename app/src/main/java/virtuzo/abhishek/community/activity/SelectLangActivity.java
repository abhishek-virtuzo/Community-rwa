package virtuzo.abhishek.community.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import virtuzo.abhishek.community.AppUtils;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.adapter.LangAdapter;
import virtuzo.abhishek.community.adapter.LangItem;
import virtuzo.abhishek.community.config.UrlConfig;
import virtuzo.abhishek.community.utils.Lang;
import virtuzo.abhishek.community.utils.Network;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SelectLangActivity extends LangSupportBaseActivity implements LangAdapter.Listener, View.OnClickListener, Lang.Listener, Network.Listener {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    TextView toolbarTitle, toolbarSubTitle;
    private LangAdapter langAdapter;
    private List<LangItem> list = new ArrayList<>();
    private Button submitButton;
    private boolean isChangeLanguage = false;
    private Lang lang;

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        toolbarSubTitle = (TextView) findViewById(R.id.toolbarSubTitle);
        toolbarSubTitle.setText(getResources().getString(R.string.toolbar_changelang));
        if (isChangeLanguage) {
            toolbarTitle.setText("सॉफ़्टवेयर भाषा बदलें / Change Software language");
        } else {
            toolbarTitle.setText("सॉफ़्टवेयर भाषा बदलें / Change Software language");
        }
    }

    private void intiRecyclerView() {

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if (list.size() == 0) {
            list.add(new LangItem("English", null, "en"));
            list.add(new LangItem("Hindi", "हिंदी", "hi"));
//        list.add(new LangItem("Tamil", "தமிழ்", "ta"));
//        list.add(new LangItem("Telugu", "లేఖిని", "te"));
//        list.add(new LangItem("Bangla", "বাংলা", "bn"));
//        list.add(new LangItem("Kannada", "ಕನ್ನಡ", "kn"));
//        list.add(new LangItem("Gujarati", "ગુજરાતી", "gu"));
            if (isChangeLanguage) {
                for (int i = 0; i < list.size(); i++) {
                    if ((list.get(i).getAbbLocal()).equals(lang.getAppLanguage())) {
                        list.get(i).setChecked(true);
                    }
                }
            }
        }
        langAdapter = new LangAdapter(this, list, this);
        recyclerView.setAdapter(langAdapter);
        langAdapter.notifyDataSetChanged();
    }

    private void loadActivity() {
        setContentView(R.layout.activity_select_lang);

        initToolbar();
        intiRecyclerView();

        submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isChangeLanguage = getIntent().getBooleanExtra("ChangeLanguage", false);
        lang = new Lang(this, this);
        loadActivity();
    }

    @Override
    public void onClickLanguageView(View v, int postion) {
        for (int i = 0; i < list.size(); i++) {
            if (i == postion) {
                list.get(postion).setChecked(true);
            } else {
                list.get(i).setChecked(false);
            }
        }
        new Lang(this).setAppLanguage(list.get(postion).getAbbLocal(), true);
        loadActivity();
        langAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submitButton:
                int position = 1000;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isChecked()) {
                        position = i;
                        break;
                    }
                }
                if (position != 1000) {
                    lang.setAppLanguage(list.get(position).getAbbLocal());
                } else {
                    Toast.makeText(this, getResources().getString(R.string.msg_selectLang), Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    @Override
    public void onLanguageChange() {
        //TODO Network Calling
        Network network = new Network(this, this);
        network.setShowProgress(true);
        HashMap<String, String> map = new HashMap<>();
        map.put("languageCode", lang.getAppLanguage());
        network.makeRequest(map, UrlConfig.userLocation);
    }

    private void serverResponseError(String error) {
        if (AppUtils.getInstance(this).getCityRegionList() == null) {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getResources().getString(R.string.msg_common), Toast.LENGTH_SHORT).show();
            }
        } else {
            serverResponseSuccess();
        }
    }

    private void serverResponseSuccess() {
        Intent intent = null;
        AppUtils.getInstance(this).setSynced(false);
        if (isChangeLanguage) {
            intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            intent = new Intent(this, FirstLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            finish();
            startActivity(intent);
        }
    }

//    private void serverResponseSuccess() {
//        Intent intent = null;
//        if (isChangeLanguage) {
//            intent = new Intent(this, HomeActivity.class);
//        } else {
//            intent = new Intent(this, LoginActivity.class);
//        }
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//    }


    @Override
    public void onNetworkSuccess(String response, String url) {
        Log.e("Response", response);
        if (response != null) {
            try {
                JSONObject json = new JSONObject(response);
                if (json.getInt("responseCode") == 1) {
                    if (json.has("Payload")) {
                        JSONArray arr = json.getJSONArray("Payload");

                        json = arr.getJSONObject(0);
                        AppUtils.getInstance(this).setLoginLocation(json.getJSONArray("login").toString());
                        Log.e("login", json.getJSONArray("login").toString());
                        json = arr.getJSONObject(1);
                        AppUtils.getInstance(this).setFilterLocation(json.getJSONArray("filter").toString());
                        serverResponseSuccess();
                    }
                } else {
                    serverResponseError(null);
                }
            } catch (Exception e) {
                serverResponseError(e.getMessage() + "");
            }
        } else {
            serverResponseError(null);
        }
    }

    @Override
    public void onNetworkError(String error, String url) {
        serverResponseError(error);
    }


}
