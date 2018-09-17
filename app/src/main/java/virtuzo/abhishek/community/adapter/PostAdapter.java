package virtuzo.abhishek.community.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.utils.BaseUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ARPIT on 02-03-2017.
 */
public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<PostItem> list;
    private Listener listener;
    private BaseUtils baseUtils;

    public PostAdapter(Context context, List<PostItem> list, PostAdapter.Listener listener) {
        this.mContext = context;
        this.listener = listener;
        this.list = list;
        baseUtils = new BaseUtils(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        if (viewType == 0) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_post, parent, false);
            return new ViewHolder(itemView);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_loading, parent, false);
            return new LoadingViewHolder(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getType();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder rholder, final int position) {
        final PostItem postItem = list.get(position);
        if (postItem.getType() != 0) {
            return;
        }
        final ViewHolder holder = (ViewHolder) rholder;
        if (postItem.isSaved()) {
            holder.gulakImageView.setImageResource(R.drawable.ic_sanrakhit_karey_blue);
        } else {
            holder.gulakImageView.setImageResource(R.drawable.ic_sanrakhit_karey);
        }
        if (Boolean.parseBoolean(postItem.getLiked())) {
            holder.likeImageView.setImageResource(R.drawable.ic_like_blue);
        } else {
            holder.likeImageView.setImageResource(R.drawable.ic_like);
        }

        switch (postItem.getPostType()) {

            case 1: // Image
                Glide.with(mContext).load(postItem.getImageUrl()).crossFade().thumbnail(0.1f).listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        //holder.postImageProgressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        holder.postImageProgressBar.setVisibility(View.GONE);
                        return false;
                    }
                }).into(holder.postImageView);
                break;

            case 2: // Audio
                holder.postImageView.setImageResource(R.drawable.music_tune_large_landscape);
                holder.postImageProgressBar.setVisibility(View.GONE);
                holder.postImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onClickMediaPost(position, 2);
                    }
                });
                break;

            case 3: // Video
                holder.videoFrameLayout.setVisibility(View.VISIBLE);
                holder.imageFrameLayout.setVisibility(View.GONE);
                final YouTubeThumbnailLoader.OnThumbnailLoadedListener onThumbnailLoadedListener = new YouTubeThumbnailLoader.OnThumbnailLoadedListener(){
                    @Override
                    public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {

                    }

                    @Override
                    public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                        youTubeThumbnailView.setVisibility(View.VISIBLE);
                    }
                };

                final View.OnClickListener clickVideoListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onClickMediaPost(position, 3);
                    }
                };

                holder.youTubeThumbnailView.initialize("AIzaSyDJyTHMpYs9iP77dLngUwFRVD1mxViId7k", new YouTubeThumbnailView.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
                        String videoID = postItem.getImageUrl().replace("http://", "");
                        Log.e("YouVideoID", postItem.getImageUrl());
                        youTubeThumbnailLoader.setVideo(videoID);
                        youTubeThumbnailView.setImageBitmap(null);
                        holder.videoPlayButton.setVisibility(View.VISIBLE);
                        //new LoadImage(youTubeThumbnailView).execute(item.getSnippet().getThumbnails().getDefault().getUrl());
                        youTubeThumbnailLoader.setOnThumbnailLoadedListener(onThumbnailLoadedListener);
                        holder.videoPlayButton.setOnClickListener(clickVideoListener);
                    }

                    @Override
                    public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                        //write something for failure
                    }
                });

                break;

        }


        String description = postItem.getDescription();
        if (description != null)

        {
            if (description.length() > 500) {
                description = description.substring(0, 500) + "... ";
                description = description.replaceAll("\n", "<br />");
                holder.messageTextView.setText(Html.fromHtml(description + "<font color='#2196f3'><u>" + mContext.getResources().getString(R.string.viewmore) + "</u></font>"));
            } else {
                holder.messageTextView.setText(Html.fromHtml(description));
            }
        }

        //For Tags and more
        holder.postCity.setText(postItem.getName() + " - " + postItem.getDateTime());

        List<String> tagList = postItem.getTagList();
        String tagNamesText = null;
        int uptoCounter = 0;
        if (tagList.size() > 2) {
            uptoCounter = 2;
        } else {
            uptoCounter = tagList.size();
        }
        for (int i = 0; i < uptoCounter; i++) {
            if (tagNamesText == null) {
                tagNamesText = tagList.get(i).toString().trim();
            } else {
                tagNamesText = tagNamesText + ", " + tagList.get(i).toString().trim();
            }
        }
        holder.builder.clear();
        if (tagNamesText != null) {
            holder.builder.append(baseUtils.encodeToUTF8(tagNamesText)).append("  ");
            holder.builder.setSpan(new ImageSpan(mContext, R.drawable.ic_showtags),
                    holder.builder.length() - 1, holder.builder.length(), 0);
            if (uptoCounter == 2) {
                if ((tagList.size() - 2) > 0) {
                    holder.builder.append(" +" + (tagList.size() - 2) + " " + mContext.getResources().getString(R.string.more));
                }
            }
            holder.tagNames.setText(holder.builder);
            holder.tagNames.setVisibility(View.VISIBLE);
        } else {
            holder.tagNames.setVisibility(View.GONE);
        }

        holder.dotView.removeAllViews();
        ArrayList<String> imageList = postItem.getImageList();
        if (imageList.size() > 1) {
            for (int i = 0; i < imageList.size(); i++) {
                addDotView(i, holder.dotView);
            }
        }

        if (list.get(position).getTotalLike() != 0) {
            holder.totalLike.setText("[" + postItem.getTotalLike() + "]");
        } else {
            holder.totalLike.setText("");
        }
        if (list.get(position).getTotalComment() != 0) {
            holder.totalComment.setText("[" + postItem.getTotalComment() + "]");
        } else {
            holder.totalComment.setText("");

        }
    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView postImageView, gulakImageView, likeImageView;
        View shareButtonView, likeButtonView, commentButtonView, gulakButtonView;
        ProgressBar postImageProgressBar;
        private LinearLayout showTagView, dotView;

        private TextView messageTextView, postCity, tagNames, totalComment;
        public TextView totalLike;
        private SpannableStringBuilder builder;
        private AlertDialog alertDialog;

        FrameLayout imageFrameLayout, videoFrameLayout;
        YouTubeThumbnailView youTubeThumbnailView;
        Button videoPlayButton;

        public ViewHolder(View view) {
            super(view);
            builder = new SpannableStringBuilder();
            showTagView = (LinearLayout) view.findViewById(R.id.showTagView);
            dotView = (LinearLayout) view.findViewById(R.id.dotView);

            postImageView = (ImageView) view.findViewById(R.id.postImageView);
            gulakImageView = (ImageView) view.findViewById(R.id.gulakImageView);
            likeImageView = (ImageView) view.findViewById(R.id.likeImageView);

            messageTextView = (TextView) view.findViewById(R.id.messageTextView);
            postImageProgressBar = (ProgressBar) view.findViewById(R.id.postImageProgressBar);

            postCity = (TextView) view.findViewById(R.id.postCity);
            tagNames = (TextView) view.findViewById(R.id.tagNames);
            tagNames.setClickable(false);
            totalLike = (TextView) view.findViewById(R.id.totalLike);
            totalComment = (TextView) view.findViewById(R.id.totalComment);

            shareButtonView = view.findViewById(R.id.shareButtonView);
            likeButtonView = view.findViewById(R.id.likeButtonView);
            commentButtonView = view.findViewById(R.id.commentButtonView);
            gulakButtonView = view.findViewById(R.id.gulakButtonView);


            shareButtonView.setOnClickListener(this);
            likeButtonView.setOnClickListener(this);
            commentButtonView.setOnClickListener(this);
            gulakButtonView.setOnClickListener(this);
            postImageView.setOnClickListener(this);
            showTagView.setOnClickListener(this);

            messageTextView.setOnClickListener(this);

            alertDialog = new AlertDialog.Builder(mContext).create();
//            alertDialog.setTitle(mContext.getResources().getString(R.string.tab_prarang));
            alertDialog.setMessage(mContext.getResources().getString(R.string.msg_savebank));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            imageFrameLayout = (FrameLayout) itemView.findViewById(R.id.imageFrameLayout);
            videoFrameLayout = (FrameLayout) itemView.findViewById(R.id.videoFrameLayout);
            youTubeThumbnailView = (YouTubeThumbnailView) itemView.findViewById(R.id.youTubeThumbnailView);
            videoPlayButton = (Button) itemView.findViewById(R.id.videoPlayButton);

        }

        @Override
        public void onClick(View v) {
            if (v.getId() == messageTextView.getId()) {
                if (!(list.get(getAdapterPosition()).getDescription()).equalsIgnoreCase(messageTextView.getText().toString())) {
                    String description = list.get(getAdapterPosition()).getDescription();
                    description = description.replaceAll("\n", "<br />");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        messageTextView.setText(Html.fromHtml(description , Html.FROM_HTML_MODE_COMPACT));
                    }
                    else
                    {
                        messageTextView.setText(Html.fromHtml(description ));
                    }
                }
                return;
            }
            if (v.getId() == gulakButtonView.getId()) {
                if (!list.get(getAdapterPosition()).isSaved()) {
                    alertDialog.show();
                }
            }
            listener.onClickRecycleView(v, getAdapterPosition(), ViewHolder.this);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        public LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface Listener {
        public void onClickRecycleView(View view, int position, ViewHolder holdar);
        public void onClickMediaPost(int position, int MediaType);
    }

    private void addDotView(int position, LinearLayout linearLayout) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        ImageView dot = new ImageView(mContext);
        if (position == 0) {
            dot.setImageDrawable(mContext.getResources().getDrawable(R.drawable.paging_active_bubble));
            params.setMargins(0, 8, 0, 0);
        } else {
            dot.setImageDrawable(mContext.getResources().getDrawable(R.drawable.paging_inactive_bubble));
            params.setMargins(8, 8, 0, 0);
        }
        linearLayout.addView(dot, params);
    }
}
