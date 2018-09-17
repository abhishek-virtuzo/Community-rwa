package virtuzo.abhishek.community.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import virtuzo.abhishek.community.AppUtils;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.activity.TagListActivity;
import virtuzo.abhishek.community.adapter.PrarangAdapter;
import virtuzo.abhishek.community.adapter.PrarangItem;
import virtuzo.abhishek.community.config.UrlConfig;
import virtuzo.abhishek.community.utils.BaseUtils;
import virtuzo.abhishek.community.utils.Lang;
import virtuzo.abhishek.community.utils.Network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class PrarangFragment extends Fragment implements PrarangAdapter.Listner, View.OnClickListener, Network.Listener {

    private RecyclerView recyclerView;
    private PrarangAdapter prarangAdapter;
    private ArrayList<PrarangItem> sanskritList;
    private ArrayList<PrarangItem> prakritList;
    private ArrayList<PrarangItem> list;

    private FrameLayout sanskritCard, prakritCard;
    private BaseUtils baseUtils;
    private Network network;
    private AppUtils appUtils;
    private Lang lang;

    TextView sanskritCount, prakritCount;
    private int selectedType = 1;

    public PrarangFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_prarang, container, false);
    }

    private void showSanskrit() {
        selectedType = 1;
        list.clear();
        prakritCard.setBackgroundResource(R.drawable.xml_no_border);
        sanskritCard.setBackgroundResource(R.drawable.xml_culture_border);
        for (int i = 0; i < sanskritList.size(); i++) {
            PrarangItem item = sanskritList.get(i);
            item.setType(1);
            switch (i) {
                case 0:
                    item.setIcon(R.drawable.ic_samay_sima);
                    item.setFrameColor(getResources().getColor(R.color.colorSanskritRed));
                    break;
                case 1:
                    item.setIcon(R.drawable.ic_manav_wa_indirya);
                    item.setFrameColor(getResources().getColor(R.color.colorSanskritYellow));
                    break;
                case 2:
                    item.setFrameColor(getResources().getColor(R.color.colorSanskritBlue));
                    item.setIcon(R.drawable.ic_manav_wa_awishkar);
                    break;
            }
            list.add(item);
        }
        prarangAdapter.notifyDataSetChanged();
    }

    private void showPrakrit() {
        selectedType = 2;
        sanskritCard.setBackgroundResource(R.drawable.xml_no_border);
        prakritCard.setBackgroundResource(R.drawable.xml_nature_border);
        list.clear();
        for (int i = 0; i < prakritList.size(); i++) {
            PrarangItem item = prakritList.get(i);
            item.setType(2);
            switch (i) {
                case 0:
                    item.setIcon(R.drawable.ic_bhugol);
                    item.setFrameColor(getResources().getColor(R.color.colorPrakritLightYellow));
                    break;
                case 1:
                    item.setIcon(R.drawable.ic_jiw_jantu);
                    item.setFrameColor(getResources().getColor(R.color.colorPrakritLightGreen));
                    break;
                case 2:
                    item.setIcon(R.drawable.ic_vanaspati);
                    item.setFrameColor(getResources().getColor(R.color.colorPrakritGreen));
                    break;
            }
            list.add(item);
        }
        prarangAdapter.notifyDataSetChanged();
    }

    private void loadFragment() {
        baseUtils = new BaseUtils(getContext());
        appUtils = new AppUtils(getContext());
        network = new Network(getContext(), this);
        lang = new Lang(getContext());

        sanskritList = new ArrayList<>();
        prakritList = new ArrayList<>();
        list = new ArrayList<>();

        prarangAdapter = new PrarangAdapter(getView().getContext(), list, this);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getView().getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(prarangAdapter);

        sanskritCount = (TextView) getView().findViewById(R.id.sanskritCount);
        prakritCount = (TextView) getView().findViewById(R.id.prakritCount);
        sanskritCard = (FrameLayout) getView().findViewById(R.id.sanskritCard);
        prakritCard = (FrameLayout) getView().findViewById(R.id.prakritCard);
        sanskritCard.setOnClickListener(this);
        prakritCard.setOnClickListener(this);
        onNetworkSuccess(appUtils.getTagsResponse(), null);
        loadFragmentData(false);

    }


    public void loadFragmentData(boolean filterApply) {
        prakritList.clear();
        sanskritList.clear();
        HashMap<String, String> map = new HashMap<>();
        map.put("languageCode", lang.getAppLanguage());
        map.put("SubscriberId", AppUtils.getInstance(getContext()).getSubscriberId());
        if (filterApply) {
            map.put("filterApply", "1");
        }
        network.makeRequest(map, UrlConfig.tagcount);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                loadFragment();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prakritCard:
                showPrakrit();
                break;
            case R.id.sanskritCard:
                showSanskrit();
                break;
        }
        baseUtils.vibrate();
    }

    @Override
    public void onClickPrarangItem(int position) {
        Intent intent = new Intent(getContext(), TagListActivity.class);
        intent.putExtra("type", list.get(position).getType());
        intent.putExtra("title", list.get(position).getTitle());
        intent.putExtra("color", list.get(position).getFrameColor());
        Bundle bundle = new Bundle();
        bundle.putSerializable("item", list.get(position));
        intent.putExtras(bundle);
        startActivity(intent);
    }


    @Override
    public void onNetworkSuccess(String response, String url) {
        if (response == null) {
            return;
        }
        String savedResponse = appUtils.getTagsResponse();
        Log.e("Response", response + "");
        try {
            JSONObject json = new JSONObject(response);
            if (json.getInt("responseCode") == 1) {
                String natureCount = json.getString("natureCount");
                String cultureCount = json.getString("cultureCount");
                JSONArray array = json.getJSONArray("Payload");

                for (int i = 0; i < array.length(); i++) {
                    json = array.getJSONObject(i);
                    PrarangItem item = new PrarangItem();
                    item.setId(json.getInt("tagCategoryId"));
                    item.setTitle(json.getString("tagCategoryInUnicode"));
                    item.setCount(json.getString("totalPost"));
                    if (i < 3) {
                        sanskritList.add(item);
                    } else {
                        prakritList.add(item);
                    }
                    prakritCount.setText(natureCount);
                    sanskritCount.setText(cultureCount);
                    if (selectedType == 1) {
                        showSanskrit();
                    } else {
                        showPrakrit();
                    }
                }
                appUtils.setTagsResponse(response);
            } else {
                if (savedResponse == null) {
                    Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
                } else {
                    onNetworkSuccess(savedResponse, url);
                }
            }
        } catch (JSONException e) {
            if (savedResponse != null) {
                onNetworkSuccess(savedResponse, url);
            }
        }
    }

    @Override
    public void onNetworkError(String error, String url) {
        Log.e("onNetworkError", error + "");
        String savedResponse = appUtils.getTagsResponse();
        if (savedResponse == null) {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.msg_common), Toast.LENGTH_LONG).show();
        } else {
            onNetworkSuccess(savedResponse, url);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        network.cancelRequest();
    }
}
