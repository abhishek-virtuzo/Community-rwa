package virtuzo.abhishek.community.fragment;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import virtuzo.abhishek.community.AppUtils;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.activity.AudioPostActivity;
import virtuzo.abhishek.community.activity.CommentActivity;
import virtuzo.abhishek.community.activity.PostImagesActivity;
import virtuzo.abhishek.community.activity.TagListActivity;
import virtuzo.abhishek.community.activity.VideoPostActivity;
import virtuzo.abhishek.community.adapter.MainPost;
import virtuzo.abhishek.community.adapter.PostAdapter;
import virtuzo.abhishek.community.adapter.PostItem;
import virtuzo.abhishek.community.config.UrlConfig;
import virtuzo.abhishek.community.db.DbHandler;
import virtuzo.abhishek.community.utils.BaseUtils;
import virtuzo.abhishek.community.utils.Lang;
import virtuzo.abhishek.community.utils.MyFunctions;
import virtuzo.abhishek.community.utils.Network;
import virtuzo.abhishek.community.utils.Permission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class GulakFragment extends Fragment implements PostAdapter.Listener {

    private View fView;
    private RecyclerView recyclerView;
    private List<PostItem> list;
    private PostAdapter postAdapter;

    private DbHandler db;
    private BaseUtils baseUtils;

    public GulakFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fView = inflater.inflate(R.layout.fragment_post, container, false);
        recyclerView = (RecyclerView) fView.findViewById(R.id.recyclerView);
        return fView;
    }

    private TextView norecord;

    private void loadFragment() {
        db = new DbHandler(getContext());
        baseUtils = new BaseUtils(getContext());
        norecord = (TextView) getView().findViewById(R.id.norecord);
        norecord.setText(getContext().getResources().getString(R.string.msg_nochitti));
        list = new ArrayList<>();

        postAdapter = new PostAdapter(getContext(), list, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getView().getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(postAdapter);

        loadFragmentData();
    }

    public void loadFragmentData() {
        List<PostItem> tempList = db.getGulak(null);
        list.clear();
        for (int i = 0; i < tempList.size(); i++) {
            tempList.get(i).setSaved(true);
            list.add(tempList.get(i));
        }
        if (list.size() > 0) {
            norecord.setVisibility(View.GONE);
        } else {
            norecord.setVisibility(View.VISIBLE);
        }
        postAdapter.notifyDataSetChanged();
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

    private PostAdapter.ViewHolder shareHolder;
    private int sharePosition;

    @Override
    public void onClickRecycleView(View view, int position, PostAdapter.ViewHolder holder) {
        switch (view.getId()) {
            case R.id.shareButtonView:
                shareHolder = holder;
                sharePosition = position;
                Permission permission = new Permission(getContext());
                if (permission.chckSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    baseUtils.sharePost(holder.postImageView, list.get(position).getPostUrl());
                } else {
                    permission.requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, this);
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
                if (totalLike < 0) {
                    totalLike = 0;
                }
                list.get(position).setTotalLike(totalLike);
                if (totalLike == 0) {
                    holder.totalLike.setText("");
                } else {
                    holder.totalLike.setText("[" + totalLike + "]");
                }
                db.updateGulalItem(list.get(position));
                Network network = new Network(getContext());
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("subscriberId", AppUtils.getInstance(getContext()).getSubscriberId());
                map.put("chittiId", list.get(position).getId());
                map.put("isLiked", list.get(position).getLiked());
                map.put("languageCode", new Lang(getContext()).getAppLanguage());
                network.makeRequest(map, UrlConfig.chittiLike);
                baseUtils.vibrate();
                break;
            case R.id.commentButtonView:
                Intent i = new Intent(getContext(), CommentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("item", list.get(position));
                bundle.putInt("position", position);
                i.putExtras(bundle);
                startActivityForResult(i, 1001);
                break;
            case R.id.gulakButtonView:
                if (list.get(position).isSaved()) {
                    db.deleteFromGulak(list.get(position).getId());
                    list.remove(position);
                    if (list.size() > 0) {
                        postAdapter.notifyItemRemoved(position);
                        norecord.setVisibility(View.GONE);
                    } else {
                        postAdapter.notifyDataSetChanged();
                        norecord.setVisibility(View.VISIBLE);
                    }
                }
                baseUtils.vibrate();
                break;
            case R.id.postImageView:
                Intent intent = new Intent(getContext(), PostImagesActivity.class);
                intent.putStringArrayListExtra("images", list.get(position).getImageList());
                startActivity(intent);
                break;
            case R.id.showTagView:
                i = new Intent(getContext(), TagListActivity.class);
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
    public void onClickMediaPost(int position, int MediaType) {

        switch(MediaType) {
            case 2: // Audio
                Intent regionIntent = new Intent(getContext(), AudioPostActivity.class);
                Bundle regionBundle = new Bundle();

                MainPost regionPost = new MainPost();
                PostItem regionPostItem = list.get(position);
                List<PostItem> regionItems = new ArrayList<>();
                regionItems.add(regionPostItem);
                regionPost.setList(regionItems);
                regionBundle.putSerializable("item", regionPost);
                regionBundle.putInt("position", 0);
                regionBundle.putInt("parentPosition", 1); // region
                regionIntent.putExtras(regionBundle);
                startActivityForResult(regionIntent, 0);
                break;

            case 3: // Video
                Intent countryIntent = new Intent(getContext(), VideoPostActivity.class);
                Bundle countryBundle = new Bundle();

                MainPost countryPost = new MainPost();
                PostItem countryPostItem = list.get(position);
                List<PostItem> countryItems = new ArrayList<>();
                countryItems.add(countryPostItem);
                countryPost.setList(countryItems);
                countryBundle.putSerializable("item", countryPost);
                countryBundle.putInt("position", 0);
                countryBundle.putInt("parentPosition", 2); // country
                countryIntent.putExtras(countryBundle);
                startActivityForResult(countryIntent, 0);
                break;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                baseUtils.sharePost(shareHolder.postImageView, list.get(sharePosition).getPostUrl());
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                db.updateGulalItem(item);
            }
        }
    }
}
