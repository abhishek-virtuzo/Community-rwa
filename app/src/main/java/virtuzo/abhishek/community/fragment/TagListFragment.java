package virtuzo.abhishek.community.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import virtuzo.abhishek.community.AppUtils;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.activity.TagListActivity;
import virtuzo.abhishek.community.adapter.PostItem;
import virtuzo.abhishek.community.adapter.PrarangItem;
import virtuzo.abhishek.community.adapter.TagAdapter;
import virtuzo.abhishek.community.adapter.TagItem;
import virtuzo.abhishek.community.config.UrlConfig;
import virtuzo.abhishek.community.utils.BaseUtils;
import virtuzo.abhishek.community.utils.Lang;
import virtuzo.abhishek.community.utils.Network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TagListFragment extends Fragment implements TagAdapter.Listner, Network.Listener {


    public TagListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tag_list, container, false);
    }

    private RecyclerView recyclerView;
    private TagAdapter tagAdapter;
    private List<TagItem> list;
    private Lang lang;
    private PrarangItem item;
    private PostItem postItem;

    private BaseUtils baseUtils;
    private Network network;

    private ProgressBar progressBar;
    private int tagColor;
    private boolean showTags = false;


    private void loadFragment() {
        if (showTags) {
            list = postItem.getTagItemList();
        } else {
            list = new ArrayList<>();
        }
        tagAdapter = new TagAdapter(getContext(), list, this);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getView().getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(tagAdapter);
        tagAdapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lang = new Lang(getContext());
        baseUtils = new BaseUtils(getContext());
        network = new Network(getContext(), this);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        loadFragment();

        if (!showTags) {
            HashMap<String, String> map = new HashMap<>();
            map.put("languageCode", lang.getAppLanguage());
            map.put("categoryId", item.getId() + "");
            map.put("SubscriberId", AppUtils.getInstance(getContext()).getSubscriberId());

            network.makeRequest(map, UrlConfig.taglist);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showTags = getArguments().getBoolean("showtags", false);
        if (showTags) {
            postItem = (PostItem) getArguments().getSerializable("item");
        } else {
            item = (PrarangItem) getArguments().getSerializable("item");
            tagColor = getArguments().getInt("color");
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClickTagItem(int position, View view) {
        if (!showTags) {
            TagPostFragment fragment = new TagPostFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("item", list.get(position));
            fragment.setArguments(bundle);
            ((TagListActivity) getActivity()).replaceFragment(R.id.fragmentFrame, fragment, true, view);
        }
    }

    @Override
    public void onNetworkSuccess(String response, String url) {
        progressBar.setVisibility(View.GONE);
        Log.e("Response", response + "");
        try {
            JSONObject json = new JSONObject(response);
            if (json.getInt("responseCode") == 1) {
                JSONArray array = json.getJSONArray("Payload");
                Log.i("Abhishek" , array.toString());
                for (int i = 0; i < array.length(); i++) {
                    json = array.getJSONObject(i);
                    TagItem item = new TagItem();
                    item.setTitle(json.getString("tagInUnicode"));
                    item.setId(json.getString("tagId"));
                    item.setIconUrl(json.getString("tagIcon"));
                    item.setFrameColor(tagColor);
                    list.add(item);
                }
                tagAdapter.notifyDataSetChanged();
                if (url != null) {
                    baseUtils.setStringData(url, response);
                }
            } else {
                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNetworkError(String error, String url) {
        progressBar.setVisibility(View.GONE);
        String savedResponse = baseUtils.getStringData(url);
        if (savedResponse == null) {
            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        } else {
            onNetworkSuccess(savedResponse, null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        network.cancelRequest();
    }
}
