package virtuzo.abhishek.community.adapter;

import android.app.Activity;
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
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubeThumbnailView;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.utils.BaseUtils;

import java.util.List;


/**
 * Created by ARPIT on 02-03-2017.
 */
public class VideoPostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    Activity activity;
    private List<PostItem> list;
    private Listener listener;
    private BaseUtils baseUtils;
    private String mimeTypeWebView = "text/html";
    private String encodingWebView = "UTF-8";

    public VideoPostAdapter(Activity context, List<PostItem> list, VideoPostAdapter.Listener listener) {
        this.mContext = context;
        this.activity = context;
        this.listener = listener;
        this.list = list;
        baseUtils = new BaseUtils(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
//        if (viewType == 0) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_video_post, parent, false);
            return new ViewHolder(itemView);
//        } else {
//            itemView = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.row_loading, parent, false);
//            return new LoadingViewHolder(itemView);
//        }
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

//        list.get(position).setImageUrl("eacFaztQvao");
        String str = list.get(position).getVideoIframe();
        Log.e("VideoFrame", str);
        holder.youtubeViewWebView.loadData(str, mimeTypeWebView, encodingWebView);

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

//        holder.dotView.removeAllViews();
//        ArrayList<String> imageList = postItem.getImageList();
//        if (imageList.size() > 1) {
//            for (int i = 0; i < imageList.size(); i++) {
//                addDotView(i, holder.dotView);
//            }
//        }

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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, YouTubePlayer.OnInitializedListener {
//        public ImageView postImageView;
        public ImageView gulakImageView, likeImageView;
        View shareButtonView, likeButtonView, commentButtonView, gulakButtonView;
        ProgressBar postImageProgressBar;
        private LinearLayout showTagView, dotView;

        private TextView messageTextView, postCity, tagNames, totalComment;
        public TextView totalLike;
        private SpannableStringBuilder builder;
        private AlertDialog alertDialog;

        YouTubeThumbnailView youTubeThumbnailView;
        YouTubePlayerFragment youTubePlayerFragment;
        FrameLayout youtubeContainer;
//        YouTubePlayerView youTubePlayerView;
        String videoID;

        WebView youtubeViewWebView;

        public ViewHolder(View view) {
            super(view);
            builder = new SpannableStringBuilder();
            showTagView = (LinearLayout) view.findViewById(R.id.showTagView);
//            dotView = (LinearLayout) view.findViewById(R.id.dotView);

//            postImageView = (ImageView) view.findViewById(R.id.postImageView);
            gulakImageView = (ImageView) view.findViewById(R.id.gulakImageView);
            likeImageView = (ImageView) view.findViewById(R.id.likeImageView);

            youtubeViewWebView = (WebView) view.findViewById(R.id.youtubeViewWebView);
            youtubeViewWebView.getSettings().setJavaScriptEnabled(true);
            youtubeViewWebView.setWebViewClient(new WebViewClient());
            youtubeViewWebView.setWebChromeClient(new WebChromeClient());

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

            youTubeThumbnailView = (YouTubeThumbnailView) view.findViewById(R.id.youTubeThumbnailView);

//            youtubeContainer = (FrameLayout) view.findViewById(R.id.youtubeContainer);

//            youTubePlayerView =
//                    (YouTubePlayerView) view.findViewById(R.id.youTubePlayerView);

            shareButtonView.setOnClickListener(this);
            likeButtonView.setOnClickListener(this);
            commentButtonView.setOnClickListener(this);
            gulakButtonView.setOnClickListener(this);
//            postImageView.setOnClickListener(this);
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

        }

        @Override
        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
            if (!wasRestored) {
                youTubePlayer.cueVideo(getVideoID());
//                youTubePlayer.loadVideo(getVideoID());
            }
        }

        @Override
        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
            if (youTubeInitializationResult.isUserRecoverableError()) {
                Toast.makeText(mContext, "Error Occurred", Toast.LENGTH_SHORT).show();
            } else {
                String errorMessage = "There was an error initializing the youtube player";
                Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
            }
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

        public void setVideoID(String videoID) {
            this.videoID = videoID;
        }

        public String getVideoID() {
            return videoID;
        }

        public void initializeYoutubePlayer() {

//            youTubePlayerView.initialize("AIzaSyDJyTHMpYs9iP77dLngUwFRVD1mxViId7k",
//                    new YouTubePlayer.OnInitializedListener() {
//                        @Override
//                        public void onInitializationSuccess(YouTubePlayer.Provider provider,
//                                                            YouTubePlayer youTubePlayer, boolean wasRestored) {
//
//                            // do any work here to cue video, play video, etc.
//                            if (!wasRestored) {
//                                youTubePlayer.cueVideo(getVideoID());
//                            }
//                        }
//                        @Override
//                        public void onInitializationFailure(YouTubePlayer.Provider provider,
//                                                            YouTubeInitializationResult youTubeInitializationResult) {
//
//                        }
//                    });

        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        public LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface Listener {
        public void onClickRecycleView(View view, int position, ViewHolder holdar);
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
