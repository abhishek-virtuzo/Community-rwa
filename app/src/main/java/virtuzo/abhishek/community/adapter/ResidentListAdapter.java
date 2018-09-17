package virtuzo.abhishek.community.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.model.Resident;
import virtuzo.abhishek.community.model.ResidentBlock;
import virtuzo.abhishek.community.realm.RealmHelper;

/**
 * Created by Abhishek Aggarwal on 4/25/2018.
 */

public class ResidentListAdapter extends RecyclerView.Adapter<ResidentListAdapter.MyViewHolder> {

    private List<Resident> residentList;
    private ResidentListAdapter.OnClickListener listener;
    Context context;

    public ResidentListAdapter(ArrayList<Resident> stateList, Context context, ResidentListAdapter.OnClickListener onClickListener) {
        this.residentList = stateList;
        this.listener = onClickListener;
        this.context = context;
    }

    @Override
    public ResidentListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.resident_item_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ResidentListAdapter.MyViewHolder holder, final int position) {

        Resident resident = residentList.get(position);

        holder.bind(resident);
        holder.nameTextView.setText(resident.getResidentName());

        ResidentBlock residentBlock = RealmHelper.getInstance().getResidentBlock(resident.getResidentBlockID());
        StringBuilder builder = new StringBuilder();
        builder.append(context.getResources().getString(R.string.text_houseno) + " - " + resident.getHouseNumber());
        if (residentBlock != null) {
            builder.append(", " + residentBlock.getBlockName());
        }
        String address = builder.toString();
        holder.designationTextView.setText(address.toString());

        Glide.with(context).load(resident.getProfileUrl()).placeholder(R.drawable.ic_userblank).dontAnimate().into(holder.profileImage);

    }

    @Override
    public int getItemCount() {
        return residentList.size();
    }

    public interface OnClickListener {
        void onItemClick(Resident resident);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;
        public TextView designationTextView;
        public CircleImageView profileImage;

        public View myView;

        public MyViewHolder(View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            designationTextView = (TextView) itemView.findViewById(R.id.designationTextView);
            profileImage = (CircleImageView) itemView.findViewById(R.id.profileImage);

            myView = itemView;
        }

        public void bind(final Resident resident) {
            myView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(resident);
                }
            });
        }

    }
}
