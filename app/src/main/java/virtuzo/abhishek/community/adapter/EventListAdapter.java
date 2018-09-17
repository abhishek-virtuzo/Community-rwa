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
import virtuzo.abhishek.community.utils.MyFunctions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhishek Aggarwal on 4/25/2018.
 */

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.MyViewHolder> {

    private List<Event> events;
    private EventListAdapter.OnClickListener listener;
    Context context;

    public EventListAdapter(ArrayList<Event> stateList, Context context, EventListAdapter.OnClickListener onClickListener) {
        this.events = stateList;
        this.listener = onClickListener;
        this.context = context;
    }

    @Override
    public EventListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final EventListAdapter.MyViewHolder holder, final int position) {

        Event event = events.get(position);

        holder.bind(event);
        holder.eventNameTextView.setText(event.getEventTitle());
        String dateTime = MyFunctions.convertDateFormat(event.getDate(), context);
        holder.dateTimeTextView.setText(dateTime);
        holder.venueTextView.setText(event.getVenue());

        if (event.isInterested()) {
            holder.interestedImageView.setImageResource(R.drawable.star_blue);
        } else {
            holder.interestedImageView.setImageResource(R.drawable.star_white);
        }

        Glide.with(context).load(event.getImage()).crossFade().thumbnail(0.1f).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                holder.progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                holder.progressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public interface OnClickListener {
        void onItemClick(Event event);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView eventNameTextView;
        public TextView venueTextView;
        public TextView dateTimeTextView;
        public ImageView imageView, interestedImageView;
//        ProgressBar progressBar;

        public View myView;

        public MyViewHolder(View itemView) {
            super(itemView);

            eventNameTextView = (TextView) itemView.findViewById(R.id.eventNameTextView);
            venueTextView = (TextView) itemView.findViewById(R.id.venueTextView);
            dateTimeTextView = (TextView) itemView.findViewById(R.id.dateTimeTextView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            interestedImageView = (ImageView) itemView.findViewById(R.id.interestedImageView);
//            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);

            myView = itemView;
        }

        public void bind(final Event event) {
            myView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(event);
                }
            });
        }

    }
}
