package virtuzo.abhishek.community.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import virtuzo.abhishek.community.AppUtils;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.adapter.AudioPostAdapter;
import virtuzo.abhishek.community.adapter.MainPost;
import virtuzo.abhishek.community.adapter.PostItem;
import virtuzo.abhishek.community.config.UrlConfig;
import virtuzo.abhishek.community.custom.EndlessRecyclerOnScrollListener;
import virtuzo.abhishek.community.db.DbHandler;
import virtuzo.abhishek.community.utils.BaseUtils;
import virtuzo.abhishek.community.utils.Lang;
import virtuzo.abhishek.community.utils.MyFunctions;
import virtuzo.abhishek.community.utils.Network;
import virtuzo.abhishek.community.utils.Permission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class AudioPostActivity extends LangSupportBaseActivity implements AudioPostAdapter.Listener, Network.Listener {

    private RecyclerView recyclerView;

    private Toolbar toolbar;
    private AdView mAdView;
    private AudioPostAdapter postAdapter;
    private MainPost mainPost;
    private int mainPosition = 0;
    private int parentPosition = 0;
    List<PostItem> list = null;

    private BaseUtils baseUtils;
    private DbHandler db;
    private Network network;
    private Bundle bundle;

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().hide();
//        getSupportActionBar().setTitle(mainPost.getName());
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initAdmobBanner() {
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }
        });
    }

    private boolean isLoading = false;

    private void addLoading() {
        if (list.size() < 21) {
            return;
        }
        if (!isLoading) {
            isLoading = true;
            PostItem loadingPost = new PostItem();
            loadingPost.setType(1);
            list.add(loadingPost);
            postAdapter.notifyItemInserted(list.size());
            networkCalling();
        }
    }

    private void removeLoading() {
        if (isLoading) {
            isLoading = false;
            list.remove(list.size() - 1);
            postAdapter.notifyItemRemoved(list.size());
        }
    }

    private void networkCalling() {
        HashMap<String, String> map = new HashMap<>();
        map.put("SubscriberId", AppUtils.getInstance(this).getSubscriberId());
        map.put("languageCode", new Lang(this).getAppLanguage());
        map.put("offset", (list.size() - 1) + "");
        String url = null;
        switch (parentPosition) {
            case 0:
                url = UrlConfig.cityPost;
                break;
            case 1:
                url = UrlConfig.regionPost;
                break;
            case 2:
                url = UrlConfig.countryPost;
                break;
            default:
                url = UrlConfig.countryPost;
                break;
        }
        network.makeRequest(map, url);
    }

    private List<PostItem> orignalList;

    private boolean checkSavedItem(PostItem item) {
        if (item == null) {
            for (int i = 0; i < list.size(); i++) {
                List<PostItem> tempList = db.getGulak(list.get(i).getId());
                if (tempList.size() > 0) {
                    list.get(i).setSaved(true);
                }
                if (list.get(i).getName() == null) {
                    list.remove(i);
                }
            }
        } else {
            List<PostItem> tempList = db.getGulak(item.getId());
            if (tempList.size() > 0) {
                return true;
            }
        }
        return false;
    }

    private void initRecyclerView() {
        list = mainPost.getList();
        checkSavedItem(null);
        orignalList = list;

        postAdapter = new AudioPostAdapter(this, list, this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                addLoading();
            }
        });
        recyclerView.setAdapter(postAdapter);
        recyclerView.scrollToPosition(mainPosition);
        if (bundle.getBoolean("notify")) {
            if (list.size() == 0) {
                addLoading();
                HashMap<String, String> map = new HashMap<>();
                map.put("ChittiId", mainPost.getId() + "");
                map.put("SubscriberId", AppUtils.getInstance(this).getSubscriberId());
                map.put("languageCode", new Lang(this).getAppLanguage());
                network.makeRequest(map, UrlConfig.chittiDetails);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        bundle = getIntent().getExtras();
        mainPost = (MainPost) bundle.getSerializable("item");
        mainPosition = bundle.getInt("position");
        parentPosition = bundle.getInt("parentPosition");


        baseUtils = new BaseUtils(this);
        db = new DbHandler(this);
        network = new Network(this, this);
        initToolbar();
        initRecyclerView();
        initAdmobBanner();

        MyFunctions.setStatusBarAndNavigationBarColor(this);
    }

    private AudioPostAdapter.ViewHolder shareHolder;
    private int sharePosition;

    @Override
    public void onClickRecycleView(View view, int position, AudioPostAdapter.ViewHolder holder) {
        switch (view.getId()) {
            case R.id.shareButtonView:
                shareHolder = holder;
                sharePosition = position;
                Permission permission = new Permission(this);
                if (permission.chckSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    baseUtils.sharePost(null, list.get(position).getPostUrl());
                } else {
                    permission.requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, null);
                }
                break;
            case R.id.likeButtonView:
                int totalLike = list.get(position).getTotalLike();
                if (Boolean.parseBoolean(list.get(position).getLiked())) {
                    list.get(position).setLiked("false");
                    holder.likeImageView.setImageResource(R.drawable.ic_like);
                    totalLike = totalLike - 1;
                } else {
                    list.get(position).setLiked("true");
                    holder.likeImageView.setImageResource(R.drawable.ic_like_blue);
                    totalLike = totalLike + 1;
                }
                list.get(position).setTotalLike(totalLike);
                holder.totalLike.setText("[" + totalLike + "]");


                HashMap<String, String> map = new HashMap<String, String>();
                map.put("subscriberId", AppUtils.getInstance(this).getSubscriberId());
                map.put("chittiId", list.get(position).getId());
                map.put("isLiked", list.get(position).getLiked());
                map.put("languageCode", new Lang(this).getAppLanguage());
                network.makeRequest(map, UrlConfig.chittiLike);
                db.updateGulalItem(list.get(position));
                baseUtils.vibrate();
                break;
            case R.id.commentButtonView:
                Intent i = new Intent(this, CommentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("item", list.get(position));
                bundle.putInt("position", position);
                i.putExtras(bundle);
                startActivityForResult(i, 1001);
                break;
            case R.id.gulakButtonView:
                if (list.get(position).isSaved()) {
                    db.deleteFromGulak(list.get(position).getId());
                    list.get(position).setSaved(false);
                    holder.gulakImageView.setImageResource(R.drawable.ic_sanrakhit_karey);
                } else {
                    db.saveIntoGulak(list.get(position));
                    list.get(position).setSaved(true);
                    holder.gulakImageView.setImageResource(R.drawable.ic_sanrakhit_karey_blue);
                }
                baseUtils.vibrate();
                break;
            case R.id.postImageView:
                Intent intent = new Intent(this, PostImagesActivity.class);
                intent.putStringArrayListExtra("images", list.get(position).getImageList());
                startActivity(intent);
                break;
            case R.id.showTagView:
                i = new Intent(this, TagListActivity.class);
                bundle = new Bundle();
                bundle.putSerializable("item", list.get(position));
                bundle.putBoolean("showtags", true);
                i.putExtras(bundle);
                startActivity(i);
                break;
        }
        //postAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                baseUtils.sharePost(null, list.get(sharePosition).getPostUrl());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
//        Intent intent = new Intent();
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("item", mainPost);
//        bundle.putInt("position", parentPosition);
//        intent.putExtras(bundle);
//        setResult(RESULT_OK, intent);
        if (bundle.getBoolean("notify")) {
//            Intent intent = new Intent(this, HomeActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
            super.onBackPressed();
        } else {
            setResult(RESULT_OK);
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        Log.e("AudioPostActivity", "onStop() called");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (network != null) {
            network.cancelRequest();
        }
        if (postAdapter.audioWebView != null) {
            postAdapter.audioWebView.loadUrl("about:blank");
        }
        super.onDestroy();
    }

    @Override
    public void onNetworkSuccess(String response, String url) {
        Log.e("Response", response);
        if (url.indexOf(UrlConfig.chittiLike) != -1) {
            return;
        }
        if (url.indexOf(UrlConfig.chittiDetails) != -1) {
            try {
                JSONObject json = new JSONObject(response);
                if (json.getString("responseCode").equals("1")) {
                    JSONArray array = json.getJSONArray("Payload");
                    json = array.getJSONObject(0);
                    JSONArray imageArray = json.getJSONArray("image");
                    JSONArray tagArray = json.getJSONArray("tags");
                    PostItem item = new PostItem();
                    item.setName(json.getString("chittiname"));
                    item.setId(json.getString("chittiId"));
                    item.setDescription(json.getString("description"));
                    item.setLiked(json.getString("isLiked"));
                    item.setTotalLike(json.getInt("totalLike"));
                    item.setTotalComment(json.getInt("totalComment"));
                    if (json.has("url")) {
                        item.setPostUrl(json.getString("url"));
                    }
                    item.setDateTime(json.getString("dateOfApprove"));
                    item.setImageList(imageArray);
                    item.setTagList(tagArray);
                    item.setSaved(checkSavedItem(item));
                    item.setPostType(2); // audio
                    list.add(0, item);
                    postAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ee) {

            }
            removeLoading();
            return;
        }
        try {
            JSONObject json = new JSONObject(response);
            if (json.getString("responseCode").equals("1")) {
                removeLoading();
                JSONArray array = json.getJSONArray("Payload");
                for (int i = 0; i < array.length(); i++) {
                    json = array.getJSONObject(i);
                    if (json.has("tags") && json.has("image")) {
                        JSONArray imageArray = json.getJSONArray("image");
                        JSONArray tagArray = json.getJSONArray("tags");
                        PostItem item = new PostItem();
                        item.setName(json.getString("chittiname"));
                        item.setId(json.getString("chittiId"));
                        item.setDescription(json.getString("description"));
                        item.setLiked(json.getString("isLiked"));
                        item.setTotalLike(json.getInt("totalLike"));
                        item.setTotalComment(json.getInt("totalComment"));
                        item.setPostUrl(json.getString("url"));
                        item.setDateTime(json.getString("dateOfApprove"));
                        item.setImageList(imageArray);
                        item.setTagList(tagArray);
                        list.add(item);
                        postAdapter.notifyItemInserted(list.size());
                    }
                }
            } else {
                //Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show();
                removeLoading();
            }
        } catch (JSONException e) {
            removeLoading();
        }
    }

    @Override
    public void onNetworkError(String error, String url) {
        removeLoading();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 1001) {
                Bundle bundle = data.getExtras();
                int comment = bundle.getInt("comment");
                int position = bundle.getInt("position");

                PostItem item = list.get(position);
                item.setTotalComment(comment);
                list.set(position, item);
                postAdapter.notifyItemChanged(position);
            }
        }
    }
}
