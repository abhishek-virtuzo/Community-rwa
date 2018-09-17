package virtuzo.abhishek.community.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.model.Complaint;
import virtuzo.abhishek.community.utils.MyFunctions;

/**
 * Created by Abhishek Aggarwal on 4/25/2018.
 */

public class ComplaintListAdapter extends RecyclerView.Adapter<ComplaintListAdapter.MyViewHolder> {

    private List<Complaint> complaints;
    private ComplaintListAdapter.OnClickListener listener;
    Context context;

    public ComplaintListAdapter(ArrayList<Complaint> complaintArrayList, Context context, ComplaintListAdapter.OnClickListener onClickListener) {
        this.complaints = complaintArrayList;
        this.listener = onClickListener;
        this.context = context;
    }

    @Override
    public ComplaintListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.complaint_item_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ComplaintListAdapter.MyViewHolder holder, final int position) {

        Complaint complaint = complaints.get(position);

        holder.bind(complaint);
        holder.titleTextView.setText(complaint.getTitle());
        holder.descriptionTextView.setText(complaint.getDescription());
        String dateTime = MyFunctions.convertDateTimeFormat(complaint.getCreatedDtTm());
        holder.complaintDtTmTextView.setText(dateTime);
        holder.statusTextView.setText(complaint.getStatusName());
        if (complaint.getSynced() == 1) {
            switch (complaint.getStatusId()) {
                case 1: // Open
                    holder.statusTextView.setBackgroundColor(context.getResources().getColor(R.color.Red));
                    break;

                case 2: // Closed
                    holder.statusTextView.setBackgroundColor(context.getResources().getColor(R.color.DarkGreen));
                    break;

                default:
                    holder.statusTextView.setBackgroundColor(context.getResources().getColor(R.color.Orange));
                    break;
            }
        } else {
            holder.statusTextView.setBackgroundColor(context.getResources().getColor(R.color.lightGrey));
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
        return complaints.size();
    }

    public interface OnClickListener {
        void onItemClick(Complaint complaint);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTextView, descriptionTextView, complaintDtTmTextView, statusTextView;

        public View myView;

        public MyViewHolder(View itemView) {
            super(itemView);

            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            descriptionTextView = (TextView) itemView.findViewById(R.id.descriptionTextView);
            complaintDtTmTextView = (TextView) itemView.findViewById(R.id.complaintDtTmTextView);
            statusTextView = (TextView) itemView.findViewById(R.id.statusTextView);

            myView = itemView;
        }

        public void bind(final Complaint complaint) {
            myView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(complaint);
                }
            });
        }

    }
}
