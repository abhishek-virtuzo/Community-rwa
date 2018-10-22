package virtuzo.abhishek.community.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import virtuzo.abhishek.community.AppUtils;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.activity.AudioPostActivity;
import virtuzo.abhishek.community.activity.PostActivity;
import virtuzo.abhishek.community.activity.VideoPostActivity;
import virtuzo.abhishek.community.adapter.HeadGalleryItem;
import virtuzo.abhishek.community.adapter.HeadGalleryItemAdapter;
import virtuzo.abhishek.community.adapter.MainPost;
import virtuzo.abhishek.community.adapter.MainPostAdapter;
import virtuzo.abhishek.community.adapter.PostItem;
import virtuzo.abhishek.community.adapter.PostItemAdapter;
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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainPostFragment extends Fragment implements PostItemAdapter.Listner, Network.Listener, OnTouchListener {

    private RecyclerView recyclerView;
    private List<MainPost> mainList;
    private MainPostAdapter mainPostAdapter;
    private BaseUtils baseUtils;
    private Network network;
    private MainPost cityPostItem, regionPostItem, countryPostItem;

    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

//    private List<Item> youtubeItems = new ArrayList<>();
//    private RecyclerView youtubeRecyclerView;
//    VideoAdapter videoAdapter;
//    Call<Videos> videoCall;

    FrameLayout mainFrameLayout;
    ViewPager headGalleryViewPager;
    HeadGalleryItemAdapter headGalleryItemAdapter;
    ArrayList<HeadGalleryItem> headGalleryItemArrayList;
    LinearLayout sliderDotspanel;
    int dotCount;
    public ImageView[] dots;

    public MainPostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_post, container, false);
    }

    private void loadFragment() {
        baseUtils = new BaseUtils(getContext());
        network = new Network(getContext(), this);
        progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipeRefreshLayout);

        mainList = new ArrayList<>();
        mainPostAdapter = new MainPostAdapter(getView().getContext(), mainList, this);

        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getView().getContext());

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mainPostAdapter);
        progressBar.setVisibility(View.GONE);

        cityPostItem = new MainPost(getContext().getResources().getString(R.string.text_imagepost), null);
        regionPostItem = new MainPost(getContext().getResources().getString(R.string.text_audiopost), null);
        countryPostItem = new MainPost(getContext().getResources().getString(R.string.text_videopost), null);

        mainList.add(cityPostItem);
        mainList.add(regionPostItem);
        mainList.add(countryPostItem);
        mainPostAdapter.notifyDataSetChanged();

        // youtube recycler view
//        Retrofit retrofit = YouTubeClient.getClient();
//
//        YouTubeInterface apiService = retrofit.create(YouTubeInterface.class);
//        videoAdapter = new VideoAdapter(getContext(), youtubeItems);
//
//        youtubeRecyclerView = (RecyclerView) getView().findViewById(R.id.youtubeRecyclerView);
//        youtubeRecyclerView.setHasFixedSize(true);
//        youtubeRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        //to use RecycleView, you need a layout manager. default is LinearLayoutManager
//        youtubeRecyclerView.setAdapter(videoAdapter);
//        LinearLayoutManager linearLayoutManager1=new LinearLayoutManager(getView().getContext());
//        linearLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
//        youtubeRecyclerView.setLayoutManager(linearLayoutManager1);
//        videoCall = apiService.getVideos();
//        callYoutubeVideos();

        // Head Gallery
        mainFrameLayout = (FrameLayout) getView().findViewById(R.id.mainFrameLayout);
        headGalleryViewPager = (ViewPager) getView().findViewById(R.id.headGalleryViewPager);
        headGalleryItemArrayList = new ArrayList<>();
        headGalleryItemAdapter = new HeadGalleryItemAdapter(headGalleryItemArrayList, getContext());
        headGalleryViewPager.setAdapter(headGalleryItemAdapter);
        headGalleryViewPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                mainFrameLayout.setEnabled(false);
                swipeRefreshLayout.setEnabled(false);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
//                        mainFrameLayout.setEnabled(true);
                        swipeRefreshLayout.setEnabled(true);
                        break;
                }
                return false;
            }
        });

        sliderDotspanel = (LinearLayout) getView().findViewById(R.id.SliderDots);

        makeNetworkCall();

        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                makeNetworkCall();
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.i("ScreenTouch", event.getAction()+"");
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setEnabled(true);
                }
                break;
        }
        return false;
    }

//    private void callYoutubeVideos() {
////        if (!videoCall.isCanceled()) {
////            videoCall.cancel();
////        }
//
//        videoCall.enqueue(new Callback<Videos>() {
//            @Override
//            public void onResponse(Call<Videos>  call, Response<Videos> response) {
//
//                int statusCode = response.code();
//                Videos posts = response.body();
//                Log.i("Youtube", posts.toString());
//                youtubeItems = posts.getItems();
//                for (Item item : youtubeItems) {
//                    Log.i("YouItem", item.getSnippet().getTitle());
//                }
//
//                videoAdapter = new VideoAdapter(getView().getContext(), youtubeItems);
//                youtubeRecyclerView.setAdapter(videoAdapter);
//            }
//            @Override
//            public void onFailure(Call<Videos>  call, Throwable t) {
//
//            }
//        });
//    }

    public void makeNetworkCall() {

        networkCalling(UrlConfig.cityPost);
        networkCalling(UrlConfig.regionPost);
        networkCalling(UrlConfig.countryPost);
    }

    private void networkCalling(String url) {
        HashMap<String, String> map = new HashMap<>();
        map.put("SubscriberId", AppUtils.getInstance(getContext()).getSubscriberId());
        map.put("languageCode", new Lang(getContext()).getAppLanguage());
        map.put("offset", "0");
        network = new Network(getContext(), this);
        network.makeRequest(map, url);
    }

    private Boolean shouldAddIntoList(String key, String value) {
        String savedValue = baseUtils.getStringData(key);
        if (savedValue == null) {
            return true;
        }
        if (value.trim().equalsIgnoreCase(savedValue.trim())) {
            return true;
        }
        return false;
    }

    private void loadCityPost(String stringArray, String url, String msg) {
        try {
            List<PostItem> list = new ArrayList<>();
            if (stringArray != null) {
                JSONArray array = new JSONArray(stringArray);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject json = array.getJSONObject(i);
                    if (json.has("tags") && json.has("image")) {
                        JSONArray imageArray = json.getJSONArray("image");
                        JSONArray tagArray = json.getJSONArray("tags");
                        PostItem item = new PostItem();
                        String chittiName = json.getString("chittiname");
                        item.setName(chittiName);
                        item.setId(json.getString("chittiId"));
                        item.setDescription(json.getString("description"));
                        item.setLiked(json.getString("isLiked"));
                        item.setTotalLike(json.getInt("totalLike"));
                        item.setTotalComment(json.getInt("totalComment"));
                        item.setPostUrl(json.getString("url"));
                        item.setDateTime(json.getString("dateOfApprove"));
                        item.setImageList(imageArray);
                        item.setTagList(tagArray);
                        if (url.indexOf(UrlConfig.cityPost) != -1) {
                            item.setPostType(1);
//                            if (shouldAddIntoList("cityFilter", chittiName)) {
//                                list.add(item);
//                            }
                        } else if (url.indexOf(UrlConfig.regionPost) != -1) {
                            item.setPostType(2);
//                            if (shouldAddIntoList("regionFilter", chittiName)) {
//                                list.add(item);
//                            }
                        } else {
                            item.setPostType(3);
//                            if (shouldAddIntoList("countryFilter", chittiName)) {
//                                list.add(item);
//                            }
                        }
                        list.add(item);
                    }
                }
                if (list.size() >= 21) {
                    list.add(new PostItem());
                }
            }
            if (msg == null) {
                msg = getContext().getResources().getString(R.string.msg_networkerror);
            }
            String key = null;
            if (url.indexOf(UrlConfig.cityPost) != -1) {
                cityPostItem.setList(list);
                cityPostItem.setMessage(msg);
                cityPostItem.setLoading(false);
                key = "citypost";

                String url_string = MyFunctions.DashboardImagesDirectory;
                String url_extension = ".jpg";
                headGalleryItemArrayList.clear();

                for (int i = 1; i <= 4; i++) {
                    HeadGalleryItem galleryItem = new HeadGalleryItem(url_string + i + url_extension);
                    headGalleryItemArrayList.add(galleryItem);
                }
//                for (PostItem postItem : list) {
//                    HeadGalleryItem galleryItem = new HeadGalleryItem(postItem.getImageUrl());
//                    headGalleryItemArrayList.add(galleryItem);
//                }

                headGalleryItemAdapter.notifyDataSetChanged();
                setDots();

            } else if (url.indexOf(UrlConfig.regionPost) != -1) {
                regionPostItem.setList(list);
                regionPostItem.setMessage(msg);
                regionPostItem.setLoading(false);
                key = "regionpost";
            } else {
                countryPostItem.setList(list);
                countryPostItem.setMessage(msg);
                countryPostItem.setLoading(false);
                key = "countrypost";
            }
            if (stringArray != null) {
                baseUtils.setStringData(key, stringArray);
            }
            mainPostAdapter.notifyDataSetChanged();
        } catch (Exception ee) {
            //TODO
        }
    }

    private void setDots() {
        dotCount = headGalleryItemAdapter.getCount();
        dots = new ImageView[dotCount];

        sliderDotspanel.removeAllViews();
        for (int i = 0; i < dotCount; i++) {
            dots[i] = new ImageView(getContext());
            dots[i].setImageResource(R.drawable.inactive_dot);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            sliderDotspanel.addView(dots[i], params);
        }

        dots[headGalleryViewPager.getCurrentItem()].setImageResource(R.drawable.active_dot);

        headGalleryViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < dotCount; i++) {
//                    dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.inactive_dot));
                    dots[i].setImageResource(R.drawable.inactive_dot);
                }
                dots[position].setImageResource(R.drawable.active_dot);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        configureImageSliderAutoScroll();
    }

    private Handler headGalleryHandler;
    Runnable update;
    Timer timer;

    private void configureImageSliderAutoScroll() {
        headGalleryHandler = new Handler();
        update = new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setEnabled(true);
                int totalPages = headGalleryItemAdapter.getCount();
                int currentPage = headGalleryViewPager.getCurrentItem();
                if (totalPages > 0) {

                    // auto-scroll to next page
                    if (currentPage == totalPages - 1) {
                        currentPage = -1;
                    }
                    currentPage++;
                    headGalleryViewPager.setCurrentItem(currentPage, true);
                    Log.i("Slider scroll", "Position : " + currentPage + "/" + totalPages);
                }
            }
        };

        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                headGalleryHandler.post(update);
            }
        }, 5000, 5000);
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
    public void onClickPostItem(View v, int parentPosition, int position) {
        if (position == -1) {
            position = (mainList.get(parentPosition).getList().size() - 2);
        }
        switch (mainList.get(parentPosition).getList().get(0).getPostType()) {
            case 1:
                Intent cityIntent = new Intent(getContext(), PostActivity.class);
                Bundle cityBundle = new Bundle();
                cityBundle.putSerializable("item", mainList.get(parentPosition));
                cityBundle.putInt("position", position);
                cityBundle.putInt("parentPosition", parentPosition);
                cityIntent.putExtras(cityBundle);
                startActivityForResult(cityIntent, 0);
                break;

            case 2:
                Intent regionIntent = new Intent(getContext(), AudioPostActivity.class);
                Bundle regionBundle = new Bundle();

//                regionBundle.putSerializable("item", mainList.get(parentPosition));
//                regionBundle.putInt("position", position);
                MainPost regionPost = mainList.get(parentPosition);
                PostItem regionPostItem = regionPost.getList().get(position);
                List<PostItem> regionItems = new ArrayList<>();
                regionItems.add(regionPostItem);
                regionPost.setList(regionItems);
                regionBundle.putSerializable("item", regionPost);
                regionBundle.putInt("position", 0);

                regionBundle.putInt("parentPosition", parentPosition);
                regionIntent.putExtras(regionBundle);
                startActivityForResult(regionIntent, 0);
                break;

            case 3:
                Intent countryIntent = new Intent(getContext(), VideoPostActivity.class);
                Bundle countryBundle = new Bundle();

//                countryBundle.putSerializable("item", mainList.get(parentPosition));
//                countryBundle.putInt("position", position);
                MainPost countryPost = mainList.get(parentPosition);
                PostItem countryPostItem = countryPost.getList().get(position);
                List<PostItem> countryItems = new ArrayList<>();
                countryItems.add(countryPostItem);
                countryPost.setList(countryItems);
                countryBundle.putSerializable("item", countryPost);
                countryBundle.putInt("position", 0);

                countryBundle.putInt("parentPosition", parentPosition);
                countryIntent.putExtras(countryBundle);
                startActivityForResult(countryIntent, 0);
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            makeNetworkCall();
        }
    }

    @Override
    public void onNetworkSuccess(String response, String url) {
        Log.e("Response", response);
        swipeRefreshLayout.setRefreshing(false);
        JSONArray array = null;
        try {
            JSONObject json = new JSONObject(response);
            if (json.getString("responseCode").equals("1")) {
                array = json.getJSONArray("Payload");
                loadCityPost(array.toString(), url, null);
            } else {
                loadCityPost(null, url, json.getString("message"));
            }
        } catch (JSONException e) {
            loadCityPost(null, url, e.getMessage());
        }
    }

    @Override
    public void onNetworkError(String error, String url) {
        swipeRefreshLayout.setRefreshing(false);

        String key = null;
        if (url.indexOf(UrlConfig.cityPost) != -1) {
            key = "citypost";
        } else if (url.indexOf(UrlConfig.regionPost) != -1) {
            key = "regionpost";
        } else {
            key = "countrypost";
        }
        String savedData = baseUtils.getStringData(key);
        if (savedData == null) {
            loadCityPost(null, url, error);
        } else {
            loadCityPost(savedData, url, getResources().getString(R.string.msg_noletter));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        network.cancelRequest();
        headGalleryHandler.removeCallbacks(update);
    }
}
