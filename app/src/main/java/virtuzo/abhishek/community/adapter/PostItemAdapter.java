package virtuzo.abhishek.community.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
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

import java.util.List;

/**
 * Created by ARPIT on 09-03-2017.
 */

public class PostItemAdapter extends RecyclerView.Adapter<PostItemAdapter.MyViewHolder> {

    private List<PostItem> list;
    private Context context;
    private int parentPostion;
    private Listner listner;

    public PostItemAdapter(Context context, List<PostItem> list, int parentPostion, PostItemAdapter.Listner listner) {
        this.context = context;
        this.list = list;
        this.parentPostion = parentPostion;
        this.listner = listner;
    }

    @Override
    public PostItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_main_post_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PostItemAdapter.MyViewHolder holder, final int position) {
        final PostItem postItem = list.get(position);
        if (postItem.getName() == null) {
            holder.viewMore.setVisibility(View.VISIBLE);
        } else {
            holder.viewMore.setVisibility(View.GONE);
            holder.postName.setText(postItem.getName());

            switch (postItem.getPostType()) {

                case 1: // Image
                    Glide.with(context).load(list.get(position).getImageUrl()).crossFade().thumbnail(0.1f).listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            holder.postImageProgressBar.setVisibility(View.GONE);
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
                    holder.postImageView.setImageResource(R.drawable.music_tune);
                    holder.postImageProgressBar.setVisibility(View.GONE);
                    break;

                case 3: // Video
                    String videoId = list.get(position).getVideoId();
//                    String videoId = "eacFaztQvao";
//                    String videoThumbnailUrl = "https://img.youtube.com/vi/" + list.get(position).getImageUrl() + "/0.jpg";
                    String videoThumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/0.jpg";
                    Glide.with(context).load(videoThumbnailUrl).crossFade().thumbnail(0.1f).listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            holder.postImageProgressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            holder.postImageProgressBar.setVisibility(View.GONE);
                            return false;
                        }
                    }).into(holder.postImageView);
                    break;

            }

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView postName;
        private ImageView postImageView;
        private ProgressBar postImageProgressBar;
        private View cardView, viewMore;

        FrameLayout imageFrameLayout, videoFrameLayout;
        YouTubeThumbnailView youTubeThumbnailView;
        Button videoPlayButton;

        public MyViewHolder(View itemView) {
            super(itemView);
            postName = (TextView) itemView.findViewById(R.id.postName);
            postImageView = (ImageView) itemView.findViewById(R.id.postImageView);
            postImageProgressBar = (ProgressBar) itemView.findViewById(R.id.postImageProgressBar);
            cardView = itemView.findViewById(R.id.cardView);
            cardView.setOnClickListener(this);

            viewMore = itemView.findViewById(R.id.viewMore);

            imageFrameLayout = (FrameLayout) itemView.findViewById(R.id.imageFrameLayout);
            videoFrameLayout = (FrameLayout) itemView.findViewById(R.id.videoFrameLayout);
            youTubeThumbnailView = (YouTubeThumbnailView) itemView.findViewById(R.id.youTubeThumbnailView);
            videoPlayButton = (Button) itemView.findViewById(R.id.videoPlayButton);
            videoPlayButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listner != null) {
                if (viewMore.getVisibility() == View.VISIBLE) {
                    listner.onClickPostItem(v, parentPostion, -1);
                    return;
                }
                listner.onClickPostItem(v, parentPostion, getAdapterPosition());
            }
        }
    }

    public interface Listner {
        public void onClickPostItem(View v, int parentPosition, int position);
    }
}
