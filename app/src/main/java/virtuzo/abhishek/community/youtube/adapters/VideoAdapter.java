package virtuzo.abhishek.community.youtube.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.youtube.models.Item;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoHolder> {

	List<Item> feedItems;
	Context ctx;

	public VideoAdapter(Context context, List<Item> feedItems) {
		this.ctx = context;
		this.feedItems = feedItems;
	}

	@Override
	public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_video_items, parent, false);
		return new VideoHolder(itemView);
	}

	@Override
	public void onBindViewHolder(final VideoHolder holder, final int position) {


		final YouTubeThumbnailLoader.OnThumbnailLoadedListener  onThumbnailLoadedListener = new YouTubeThumbnailLoader.OnThumbnailLoadedListener(){
			@Override
			public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {

			}

			@Override
			public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
				youTubeThumbnailView.setVisibility(View.VISIBLE);
				holder.relativeLayoutOverYouTubeThumbnailView.setVisibility(View.VISIBLE);
			}
		};

		holder.youTubeThumbnailView.initialize("AIzaSyDJyTHMpYs9iP77dLngUwFRVD1mxViId7k", new YouTubeThumbnailView.OnInitializedListener() {
			@Override
			public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
				final Item item = feedItems.get(position);
				youTubeThumbnailLoader.setVideo(item.getId().getVideoId());
				youTubeThumbnailView.setImageBitmap(null);
				//new LoadImage(youTubeThumbnailView).execute(item.getSnippet().getThumbnails().getDefault().getUrl());

				youTubeThumbnailLoader.setOnThumbnailLoadedListener(onThumbnailLoadedListener);
			}

			@Override
			public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
				//write something for failure
			}
		});
	}

	@Override
	public int getItemCount() {
		return feedItems.size();
	}

	public class VideoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		protected RelativeLayout relativeLayoutOverYouTubeThumbnailView;
		YouTubeThumbnailView youTubeThumbnailView;

		public VideoHolder(View itemView) {
			super(itemView);
			relativeLayoutOverYouTubeThumbnailView = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
			youTubeThumbnailView = (YouTubeThumbnailView) itemView.findViewById(R.id.youtube_thumbnail);
			youTubeThumbnailView.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
            Item item =  feedItems.get(getLayoutPosition());
			Intent intent = YouTubeStandalonePlayer.createVideoIntent((Activity) ctx, "AIzaSyDJyTHMpYs9iP77dLngUwFRVD1mxViId7k", item.getId().getVideoId(), 0, true, false);
			ctx.startActivity(intent);

//            Intent intent = new Intent(ctx, YoutubeVideoActivity.class);
//            intent.putExtra("VideoID", item.getId().getVideoId());
//            intent.putExtra("Title", item.getSnippet().getTitle());
//            intent.putExtra("ChannelTitle", item.getSnippet().getChannelTitle());
//            ctx.startActivity(intent);
		}
	}
}