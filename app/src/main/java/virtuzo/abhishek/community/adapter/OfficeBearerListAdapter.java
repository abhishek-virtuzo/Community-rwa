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
import virtuzo.abhishek.community.model.OfficeBearer;

/**
 * Created by Abhishek Aggarwal on 4/25/2018.
 */

public class OfficeBearerListAdapter extends RecyclerView.Adapter<OfficeBearerListAdapter.MyViewHolder> {

    private List<OfficeBearer> officeBearerList;
    private OfficeBearerListAdapter.OnClickListener listener;
    Context context;

    public OfficeBearerListAdapter(ArrayList<OfficeBearer> stateList, Context context, OfficeBearerListAdapter.OnClickListener onClickListener) {
        this.officeBearerList = stateList;
        this.listener = onClickListener;
        this.context = context;
    }

    @Override
    public OfficeBearerListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.office_bearer_item_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final OfficeBearerListAdapter.MyViewHolder holder, final int position) {

        OfficeBearer officeBearer = officeBearerList.get(position);

        holder.bind(officeBearer);
        holder.nameTextView.setText(officeBearer.getName());
        holder.designationTextView.setText(officeBearer.getDesignation());
        Glide.with(context).load(officeBearer.getProfileUrl()).placeholder(R.drawable.ic_userblank).dontAnimate().into(holder.profileImage);

    }

    @Override
    public int getItemCount() {
        return officeBearerList.size();
    }

    public interface OnClickListener {
        void onItemClick(OfficeBearer contactCategory);
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

        public void bind(final OfficeBearer officeBearer) {
            myView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(officeBearer);
                }
            });
        }

    }
}
