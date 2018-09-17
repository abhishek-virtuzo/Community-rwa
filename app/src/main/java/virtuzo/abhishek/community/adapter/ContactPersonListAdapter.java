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
import virtuzo.abhishek.community.model.ContactPerson;
import virtuzo.abhishek.community.utils.MyFunctions;

/**
 * Created by Abhishek Aggarwal on 4/25/2018.
 */

public class ContactPersonListAdapter extends RecyclerView.Adapter<ContactPersonListAdapter.MyViewHolder> {

    private List<ContactPerson> contactPersonList;
    private ContactPersonListAdapter.OnClickListener listener;
    Context context;

    public ContactPersonListAdapter(ArrayList<ContactPerson> stateList, Context context, ContactPersonListAdapter.OnClickListener onClickListener) {
        this.contactPersonList = stateList;
        this.listener = onClickListener;
        this.context = context;
    }

    @Override
    public ContactPersonListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_person_item_layout_new, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ContactPersonListAdapter.MyViewHolder holder, final int position) {

        ContactPerson contactPerson = contactPersonList.get(position);

        holder.bind(contactPerson);
        holder.nameTextView.setText(contactPerson.getContactName());
        if (MyFunctions.StringLength(contactPerson.getDesignation()) > 0) {
            holder.designationTextView.setText(contactPerson.getDesignation());
        } else {
            holder.designationTextView.setVisibility(View.GONE);
        }
        Glide.with(context).load(contactPerson.getProfileUrl()).placeholder(R.drawable.ic_userblank).dontAnimate().into(holder.profileImage);

    }

    @Override
    public int getItemCount() {
        return contactPersonList.size();
    }

    public interface OnClickListener {
        void onItemClick(ContactPerson contactCategory);
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

        public void bind(final ContactPerson contactCategory) {
            myView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(contactCategory);
                }
            });
        }

    }
}
