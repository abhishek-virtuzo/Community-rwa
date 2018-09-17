package virtuzo.abhishek.community.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.utils.MyFunctions;

/**
 * Created by Abhishek Aggarwal on 4/25/2018.
 */

public class CircularListAdapter extends RecyclerView.Adapter<CircularListAdapter.MyViewHolder> {

    private List<Circular> circulars;
    private CircularListAdapter.OnClickListener listener;
    Context context;

    public CircularListAdapter(ArrayList<Circular> stateList, Context context, CircularListAdapter.OnClickListener onClickListener) {
        this.circulars = stateList;
        this.listener = onClickListener;
        this.context = context;
    }

    @Override
    public CircularListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.circular_item_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CircularListAdapter.MyViewHolder holder, final int position) {

        Circular circular = circulars.get(position);

        holder.bind(circular);
        holder.circularHeadingTextView.setText(circular.getSubject());
        String dateTime = MyFunctions.convertDateFormat(circular.getCreatedDtTm(), context);
        holder.circularDtTmTextView.setText(dateTime);
        if (MyFunctions.Circular_UNREAD.equals(circular.getSeen())) {
            holder.outerLayout.setBackgroundColor(context.getResources().getColor(R.color.color_unread));
        } else {
            holder.outerLayout.setBackgroundColor(context.getResources().getColor(R.color.color_read));
        }
//        holder.dateTimeTextView.setText(circular.getDateTime());
//        holder.venueTextView.setText(circular.getVenue());
//        Glide.with(context).load(circular.getImageUrl()).crossFade().thumbnail(0.1f).listener(new RequestListener<String, GlideDrawable>() {
//            @Override
//            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                holder.progressBar.setVisibility(View.GONE);
//                return false;
//            }
//
//            @Override
//            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                holder.progressBar.setVisibility(View.GONE);
//                return false;
//            }
//        }).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return circulars.size();
    }

    public interface OnClickListener {
        void onItemClick(Circular event);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView circularHeadingTextView, circularDtTmTextView;
        public LinearLayout outerLayout;
//        public TextView venueTextView;
//        public TextView dateTimeTextView;
//        public ImageView imageView;
//        ProgressBar progressBar;

        public View myView;

        public MyViewHolder(View itemView) {
            super(itemView);

            circularHeadingTextView = (TextView) itemView.findViewById(R.id.circularHeadingTextView);
            circularDtTmTextView = (TextView) itemView.findViewById(R.id.circularDtTmTextView);
            outerLayout = (LinearLayout) itemView.findViewById(R.id.outerLayout);
//            venueTextView = (TextView) itemView.findViewById(R.id.venueTextView);
//            dateTimeTextView = (TextView) itemView.findViewById(R.id.dateTimeTextView);
//            imageView = (ImageView) itemView.findViewById(R.id.imageView);
//            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);

            myView = itemView;
        }

        public void bind(final Circular event) {
            myView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(event);
                }
            });
        }

    }
}
