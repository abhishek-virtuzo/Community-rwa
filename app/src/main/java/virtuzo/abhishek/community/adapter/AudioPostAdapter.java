package virtuzo.abhishek.community.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.utils.BaseUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by ARPIT on 02-03-2017.
 */
public class AudioPostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<PostItem> list;
    private Listener listener;
    private BaseUtils baseUtils;
    private Activity activity;

    public WebView audioWebView;

    private String mimeTypeWebView = "text/html";
    private String encodingWebView = "UTF-8";

    public AudioPostAdapter(Activity context, List<PostItem> list, AudioPostAdapter.Listener listener) {
        this.mContext = context;
        this.activity = context;
        this.listener = listener;
        this.list = list;
        baseUtils = new BaseUtils(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        if (viewType == 0) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_audio_post, parent, false);
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

        holder.webView.getSettings().setAllowContentAccess(true);
        holder.webView.getSettings().setDomStorageEnabled(true);
        String string = "<html class=\"gr__hustlegrl_com\">" +
                "   <head><meta name=\"viewport\" content=\"width=device-width\"></head>" +
//                "   <head>" +
//                "<meta name=\"viewport\" content=\"width=device-width,initial-scale=1\" />\n" +
//                    "<link rel=\"stylesheet\" href=\"http://fonts.googleapis.com/css?family=Lato:400,700\" />\n" +
//                    "<link rel=\"stylesheet\" href=\"https://tympanus.net/Development/AudioPlayer/css/reset.css\" />\n" +
//                    "<link rel=\"stylesheet\" href=\"https://tympanus.net/Development/AudioPlayer/css/reset.csscss/demo.css\" />\n" +
//                    "<link rel=\"stylesheet\" href=\"https://tympanus.net/Development/AudioPlayer/css/reset.csscss/audioplayer.css\" />" +
//                "</head>" +

//                "   <body style=\"align:center; data-gr-c-s-loaded=\"true\">" +
//                "<div id=\"wrapper\">\n" +
//                "<audio preload=\"auto\" controls>\n" +
//                "<source src=\"" + postItem.getImageUrl() + "\">\n" +
//                "</audio>\n" +
//                "<script src=\"https://tympanus.net/Development/AudioPlayer/js/jquery.js\"></script>\n" +
//                "<script src=\"https://tympanus.net/Development/AudioPlayer/js/audioplayer.js\"></script>\n" +
//                "<script>$( function() { $( 'audio' ).audioPlayer(); } );</script>\n" +
//                "</div>" +
                "   <body style=\"align:center; background-color: rgb(0, 0, 0);\" data-gr-c-s-loaded=\"true\">" +
                "   <audio controls=\"\" autoplay=\"\" name=\"media\" " +
                "   style=\"position: absolute;" +
                "   top: 0px;" +
                "   right: 0px;" +
                "   bottom: 0px;" +
                "   left: 0px;" +
                "   max-height: 100%;" +
                "   max-width: 100%;" +
                "   margin: auto;\"><source src=\"" + postItem.getImageUrl() + "\" type=\"audio/mpeg\">" +
                "</audio>" +
                "</body></html>";

        holder.webView.loadData(string, mimeTypeWebView, encodingWebView);
//        holder.webView.setWebViewClient(new WebViewClient() {
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                holder.injectCSS(view);
//                super.onPageFinished(view, url);
//            }
//        });

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

        WebView webView;

        public ViewHolder(View view) {
            super(view);
            builder = new SpannableStringBuilder();
            showTagView = (LinearLayout) view.findViewById(R.id.showTagView);
//            dotView = (LinearLayout) view.findViewById(R.id.dotView);

            webView = view.findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient());
            webView.setWebChromeClient(new WebChromeClient());
            audioWebView = webView;

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
//            postImageView.setOnClickListener(this);
            showTagView.setOnClickListener(this);

            postImageView.setImageResource(R.drawable.music_tune_large);

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
