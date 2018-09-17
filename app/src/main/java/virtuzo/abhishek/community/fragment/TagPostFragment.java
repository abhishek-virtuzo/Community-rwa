package virtuzo.abhishek.community.fragment;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import virtuzo.abhishek.community.AppUtils;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.activity.CommentActivity;
import virtuzo.abhishek.community.activity.PostImagesActivity;
import virtuzo.abhishek.community.activity.TagListActivity;
import virtuzo.abhishek.community.adapter.PostAdapter;
import virtuzo.abhishek.community.adapter.PostItem;
import virtuzo.abhishek.community.adapter.TagItem;
import virtuzo.abhishek.community.config.UrlConfig;
import virtuzo.abhishek.community.db.DbHandler;
import virtuzo.abhishek.community.utils.BaseUtils;
import virtuzo.abhishek.community.utils.Lang;
import virtuzo.abhishek.community.utils.Network;
import virtuzo.abhishek.community.utils.Permission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TagPostFragment extends Fragment implements PostAdapter.Listener, Network.Listener {

    private TagItem item;

    public TagPostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        item = (TagItem) bundle.getSerializable("item");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tag_post, container, false);
    }

    private BaseUtils baseUtils;
    private TextView tagTitle;
    private FrameLayout iconFrame;
    private ImageView tagIcon;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private PostAdapter postAdapter;
    private List<PostItem> list;
    private DbHandler db;

    private Network network;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        baseUtils = new BaseUtils(getContext());
        db = new DbHandler(getContext());
        network = new Network(getContext(), this);

        tagTitle = (TextView) getView().findViewById(R.id.tagTitle);
        tagIcon = (ImageView) getView().findViewById(R.id.tagIcon);
        iconFrame = (FrameLayout) getView().findViewById(R.id.iconFrame);
        progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);

        tagTitle.setText(item.getTitle());
        Glide.with(getContext()).load(item.getIconUrl()).into(tagIcon);
        GradientDrawable gd = (GradientDrawable) iconFrame.getBackground().getCurrent();
        gd.setStroke(baseUtils.dpToPx(1), item.getFrameColor());

        list = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), list, this);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(postAdapter);
        makeNetworkCall();

    }

    private void makeNetworkCall() {
        HashMap<String, String> map = new HashMap<>();
        map.put("subscriberId", AppUtils.getInstance(getContext()).getSubscriberId());
        map.put("languageCode", new Lang(getContext()).getAppLanguage());
        map.put("tagId", item.getId());
        network.makeRequest(map, UrlConfig.tagWisePost);
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
                list.get(position).setTotalLike(totalLike);
                holder.totalLike.setText("[" + totalLike + "]");

                Network network = new Network(getContext());
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("subscriberId", AppUtils.getInstance(getContext()).getSubscriberId());
                map.put("chittiId", list.get(position).getId());
                map.put("isLiked", list.get(position).getLiked());
                map.put("languageCode", new Lang(getContext()).getAppLanguage());
                network.makeRequest(map, UrlConfig.chittiLike);
                db.updateGulalItem(list.get(position));
                baseUtils.vibrate();
                break;
            case R.id.commentButtonView:
                Intent i = new Intent(getContext(), CommentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("item", list.get(position));
                bundle.putInt("position", position);
                i.putExtras(bundle);
                this.startActivityForResult(i, 1001);
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
    public void onNetworkSuccess(String response, String url) {
        Log.e("Response", response);
        progressBar.setVisibility(View.GONE);
        try {
            JSONObject json = new JSONObject(response);
            if (json.getInt("responseCode") == 1) {
                JSONArray array = json.getJSONArray("Payload");
                for (int i = 0; i < array.length(); i++) {
                    json = array.getJSONObject(i);
                    if (json.has("image") && json.has("tags")) {
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
                        item.setTagList(tagArray);
                        item.setImageList(imageArray);
                        //Check for saved item
                        List<PostItem> tempList = db.getGulak(item.getId());
                        if (tempList.size() > 0) {
                            item.setSaved(true);
                        }
                        list.add(item);
                    }
                }
                postAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Toast.makeText(getContext(), e.getMessage() + " " + getContext().getResources().getString(R.string.msg_common), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNetworkError(String error, String url) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        network.cancelRequest();
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
            }
        }
    }
}