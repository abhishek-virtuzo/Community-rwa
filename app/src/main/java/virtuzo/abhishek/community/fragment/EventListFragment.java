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

import virtuzo.abhishek.community.AppUtils;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.activity.CircularViewActivity;
import virtuzo.abhishek.community.activity.EventViewActivity;
import virtuzo.abhishek.community.adapter.Event;
import virtuzo.abhishek.community.adapter.EventListAdapter;
import virtuzo.abhishek.community.adapter.PrarangItem;
import virtuzo.abhishek.community.config.UrlConfig;
import virtuzo.abhishek.community.utils.BaseUtils;
import virtuzo.abhishek.community.utils.Lang;
import virtuzo.abhishek.community.utils.MyFunctions;
import virtuzo.abhishek.community.utils.Network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventListFragment extends Fragment implements Network.Listener {

    private RecyclerView recyclerView;
    private EventListAdapter eventListAdapter;
    private ArrayList<PrarangItem> sanskritList;
    private ArrayList<PrarangItem> prakritList;
    private ArrayList<Event> eventArrayList = new ArrayList<>();

    private FrameLayout sanskritCard, prakritCard;
    private BaseUtils baseUtils;
    private Network network;
    private AppUtils appUtils;
    private Lang lang;

    TextView sanskritCount, prakritCount;
    private int selectedType = 1;
    private static final int REQUESTCODE_EventView = 131;

    public EventListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_list, container, false);
    }

    private void showSanskrit() {
        selectedType = 1;
        eventArrayList.clear();
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
//            eventArrayList.add(item);
        }
        eventListAdapter.notifyDataSetChanged();
    }

    private void showPrakrit() {
        selectedType = 2;
        sanskritCard.setBackgroundResource(R.drawable.xml_no_border);
        prakritCard.setBackgroundResource(R.drawable.xml_nature_border);
        eventArrayList.clear();
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
//            eventArrayList.add(item);
        }
        eventListAdapter.notifyDataSetChanged();
    }

    private void loadFragment() {
        baseUtils = new BaseUtils(getContext());
        appUtils = new AppUtils(getContext());
//        network = new Network(getContext(), this);
        lang = new Lang(getContext());

        eventArrayList = new ArrayList<>();

        eventListAdapter = new EventListAdapter(eventArrayList, getView().getContext(), new EventListAdapter.OnClickListener() {
            @Override
            public void onItemClick(Event event) {
//                Intent intent = new Intent(getContext(), EventViewActivity.class);
//                startActivity(intent);
                if (baseUtils.isNetworkAvailable()) {
                    Intent intent = new Intent(getContext(), EventViewActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putString("ID", event.getId());
                    intent.putExtras(bundle);
                    startActivityForResult(intent, REQUESTCODE_EventView);
                } else {
                    Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getView().getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(eventListAdapter);

//        onNetworkSuccess(appUtils.getTagsResponse(), null);
        loadFragmentData(false);

    }

    public void loadFragmentData(boolean filterApply) {
        lang = new Lang(getContext());

        HashMap<String, String> map = new HashMap<>();
        map.put("languageCode", lang.getAppLanguage());
        map.put("SubscriberId", AppUtils.getInstance(getContext()).getSubscriberId());
//        if (filterApply) {
//            map.put("filterApply", "1");
//        }

        network = new Network(getContext(), this);
        network.makeRequest(map, UrlConfig.EventList);

//        eventArrayList.clear();
//        eventArrayList.add(new Event("Event 1", "10-10-2017", "iThum Tower, A-40, Sector-62, Noida", "https://www.simplifiedcoding.net/demos/marvel/ironman.jpg"));
//        eventArrayList.add(new Event("Event 2", "11-10-2017", "Gurgaon", "https://www.simplifiedcoding.net/demos/marvel/captainamerica.jpg"));
//        eventArrayList.add(new Event("Event 3", "12-10-2017", "New Delhi", "https://www.simplifiedcoding.net/demos/marvel/ironman.jpg"));
//        eventListAdapter.notifyDataSetChanged();

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

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.prakritCard:
//                showPrakrit();
//                break;
//            case R.id.sanskritCard:
//                showSanskrit();
//                break;
//        }
//        baseUtils.vibrate();
//    }

    @Override
    public void onNetworkSuccess(String response, String url) {
        if (response == null) {
            return;
        }
        String savedResponse = appUtils.getEventsResponse();
        Log.e("Response", response + "");
        try {
            JSONObject json = new JSONObject(response);
            if (json.getInt("responseCode") == 1) {
                JSONArray array = json.getJSONArray("Payload");

                Gson gson = new Gson();
                eventArrayList.clear();
                for (int i = 0; i < array.length(); i++) {
                    json = array.getJSONObject(i);
                    Event event = gson.fromJson(json.toString(), Event.class);
                    eventArrayList.add(event);
                }
                appUtils.setEventsResponse(response);
                eventListAdapter.notifyDataSetChanged();
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
        String savedResponse = appUtils.getEventsResponse();
        if (savedResponse == null) {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.msg_common), Toast.LENGTH_LONG).show();
        } else {
            onNetworkSuccess(savedResponse, url);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE_EventView) {
            Log.e("RequestCode Event", REQUESTCODE_EventView+"");
            onNetworkSuccess(AppUtils.getInstance(getContext()).getEventsResponse(), null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        network.cancelRequest();
    }

}
