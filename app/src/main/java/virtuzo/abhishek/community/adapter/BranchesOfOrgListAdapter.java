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

public class BranchesOfOrgListAdapter extends RecyclerView.Adapter<BranchesOfOrgListAdapter.MyViewHolder> {

    private List<BranchOfOrganisation> branchOfOrganisations;
    private BranchesOfOrgListAdapter.OnClickListener listener;
    Context context;

    public BranchesOfOrgListAdapter(ArrayList<BranchOfOrganisation> stateList, Context context, BranchesOfOrgListAdapter.OnClickListener onClickListener) {
        this.branchOfOrganisations = stateList;
        this.listener = onClickListener;
        this.context = context;
    }

    @Override
    public BranchesOfOrgListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.branch_of_org_item_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final BranchesOfOrgListAdapter.MyViewHolder holder, final int position) {

        BranchOfOrganisation branchOfOrganisation = branchOfOrganisations.get(position);

        holder.bind(branchOfOrganisation);
        holder.branchNameTextView.setText(branchOfOrganisation.getBranchName());
        holder.branchAddressTextView.setText(branchOfOrganisation.getAddress());
        Glide.with(context).load(branchOfOrganisation.getImageUrl()).crossFade().thumbnail(0.1f).listener(new RequestListener<String, GlideDrawable>() {
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
        return branchOfOrganisations.size();
    }

    public interface OnClickListener {
        void onItemClick(BranchOfOrganisation branchOfOrganisation);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView branchNameTextView;
        public TextView branchAddressTextView;
        public ImageView imageView;
//        ProgressBar progressBar;

        public View myView;

        public MyViewHolder(View itemView) {
            super(itemView);

            branchNameTextView = (TextView) itemView.findViewById(R.id.branchNameTextView);
            branchAddressTextView = (TextView) itemView.findViewById(R.id.branchAddressTextView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
//            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);

            myView = itemView;
        }

        public void bind(final BranchOfOrganisation branchOfOrganisation) {
            myView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(branchOfOrganisation);
                }
            });
        }

    }
}
