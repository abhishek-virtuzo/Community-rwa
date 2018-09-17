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

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import virtuzo.abhishek.community.AppUtils;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.activity.CircularViewActivity;
import virtuzo.abhishek.community.adapter.Circular;
import virtuzo.abhishek.community.adapter.CircularListAdapter;
import virtuzo.abhishek.community.adapter.PrarangItem;
import virtuzo.abhishek.community.config.UrlConfig;
import virtuzo.abhishek.community.utils.BaseUtils;
import virtuzo.abhishek.community.utils.Lang;
import virtuzo.abhishek.community.utils.MyFunctions;
import virtuzo.abhishek.community.utils.Network;

/**
 * A simple {@link Fragment} subclass.
 */
public class CircularListFragment extends Fragment implements View.OnClickListener, Network.Listener {

    private RecyclerView recyclerView;
    private CircularListAdapter circularListAdapter;
    private ArrayList<PrarangItem> sanskritList;
    private ArrayList<PrarangItem> prakritList;
    private ArrayList<Circular> circularArrayList = new ArrayList<>();

    private FrameLayout sanskritCard, prakritCard;
    private BaseUtils baseUtils;
    private Network network;
    private AppUtils appUtils;
    private Lang lang;

    TextView sanskritCount, prakritCount;
    private int selectedType = 1;

    private static final int REQUESTCODE_CircularDetails = 121;

    public CircularListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_circular_list, container, false);
    }

    private void showSanskrit() {
        selectedType = 1;
        circularArrayList.clear();
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
//            circularArrayList.add(item);
        }
        circularListAdapter.notifyDataSetChanged();
    }

    private void showPrakrit() {
        selectedType = 2;
        sanskritCard.setBackgroundResource(R.drawable.xml_no_border);
        prakritCard.setBackgroundResource(R.drawable.xml_nature_border);
        circularArrayList.clear();
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
//            circularArrayList.add(item);
        }
        circularListAdapter.notifyDataSetChanged();
    }

    private void loadFragment() {
        baseUtils = new BaseUtils(getContext());
        appUtils = new AppUtils(getContext());
        network = new Network(getContext(), this);
        lang = new Lang(getContext());

        circularArrayList = new ArrayList<>();

        circularListAdapter = new CircularListAdapter(circularArrayList, getView().getContext(), new CircularListAdapter.OnClickListener() {
            @Override
            public void onItemClick(Circular circular) {
                if (baseUtils.isNetworkAvailable()) {
                    Intent intent = new Intent(getContext(), CircularViewActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putLong("CircularId", circular.getCircularId());
                    intent.putExtras(bundle);
                    startActivityForResult(intent, REQUESTCODE_CircularDetails);
                } else {
                    Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getView().getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(circularListAdapter);

//        sanskritCount = (TextView) getView().findViewById(R.id.sanskritCount);
//        prakritCount = (TextView) getView().findViewById(R.id.prakritCount);
//        sanskritCard = (FrameLayout) getView().findViewById(R.id.sanskritCard);
//        prakritCard = (FrameLayout) getView().findViewById(R.id.prakritCard);
//        sanskritCard.setOnClickListener(this);
//        prakritCard.setOnClickListener(this);

//        onNetworkSuccess(appUtils.getTagsResponse(), null);
        loadFragmentData(false);

    }

    public void loadFragmentData(boolean filterApply) {
        circularArrayList.clear();
        lang = new Lang(getContext());

        HashMap<String, String> map = new HashMap<>();
        map.put("SubscriberId", AppUtils.getInstance(getContext()).getSubscriberId());
        map.put("languageCode", lang.getAppLanguage());
//        if (filterApply) {
//            map.put("filterApply", "1");
//        }
        network.makeRequest(map, UrlConfig.CircularNotificationDetails);

//        circularArrayList.clear();
//        circularArrayList.add(new Circular("Circular 1", "https://www.simplifiedcoding.net/demos/marvel/ironman.jpg"));
//        circularArrayList.add(new Circular("Circular 2", "https://www.simplifiedcoding.net/demos/marvel/captainamerica.jpg"));
//        circularArrayList.add(new Circular("Circular 3", "https://www.simplifiedcoding.net/demos/marvel/ironman.jpg"));
//        circularListAdapter.notifyDataSetChanged();

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

//    @Override
//    public void onClickPrarangItem(int position) {
//        Intent intent = new Intent(getContext(), TagListActivity.class);
//        intent.putExtra("type", circularArrayList.get(position).getType());
//        intent.putExtra("title", circularArrayList.get(position).getTitle());
//        intent.putExtra("color", circularArrayList.get(position).getFrameColor());
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("item", circularArrayList.get(position));
//        intent.putExtras(bundle);
//        startActivity(intent);
//    }

    @Override
    public void onNetworkSuccess(String response, String url) {
        if (response == null) {
            return;
        }
        String savedResponse = appUtils.getCircularsResponse();
        Log.e("Response", response + "");
        try {
            JSONObject json = new JSONObject(response);
            if (json.getInt("responseCode") == 1) {
                JSONArray array = json.getJSONArray("Payload");
                circularArrayList.clear();

                Gson gson = new Gson();
                for (int i = 0; i < array.length(); i++) {
                    json = array.getJSONObject(i);

                    Circular circular = gson.fromJson(json.toString(), Circular.class);
                    circularArrayList.add(circular);
                }
                circularListAdapter.notifyDataSetChanged();
                appUtils.setCircularsResponse(response);
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
        String savedResponse = appUtils.getCircularsResponse();
        if (savedResponse == null) {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.msg_common), Toast.LENGTH_LONG).show();
        } else {
            onNetworkSuccess(savedResponse, url);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE_CircularDetails) {
            Log.e("RequestCode Circular", REQUESTCODE_CircularDetails+"");
            onNetworkSuccess(AppUtils.getInstance(getContext()).getCircularsResponse(), null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        network.cancelRequest();
    }

}
