package virtuzo.abhishek.community.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import virtuzo.abhishek.community.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhishek Aggarwal on 4/25/2018.
 */

public class PanchangListAdapter extends RecyclerView.Adapter<PanchangListAdapter.MyViewHolder> {

    private List<Panchang> panchangs;
    private PanchangListAdapter.OnClickListener listener;
    Context context;

    public PanchangListAdapter(ArrayList<Panchang> stateList, Context context, PanchangListAdapter.OnClickListener onClickListener) {
        this.panchangs = stateList;
        this.listener = onClickListener;
        this.context = context;
    }

    @Override
    public PanchangListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.panchang_item_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PanchangListAdapter.MyViewHolder holder, final int position) {

        Panchang panchang = panchangs.get(position);

        holder.bind(panchang);
//        holder.titleTextView.setText(panchang.getTitle());

        Glide.with(context).load(panchang.getImage()).crossFade().thumbnail(1).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                holder.progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                holder.progressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return panchangs.size();
    }

    public interface OnClickListener {
        void onItemClick(Panchang panchang);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

//        public TextView titleTextView;
        public ImageView imageView;
        ProgressBar progressBar;

        public View myView;

        public MyViewHolder(View itemView) {
            super(itemView);

//            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.loadingProgressBar);

            myView = itemView;
        }

        public void bind(final Panchang panchang) {
            myView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(panchang);
                }
            });
        }

    }
}
