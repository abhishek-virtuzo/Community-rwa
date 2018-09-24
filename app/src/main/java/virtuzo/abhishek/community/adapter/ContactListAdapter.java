package virtuzo.abhishek.community.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.model.Contact;
import virtuzo.abhishek.community.utils.AnimationUtils;

/**
 * Created by Abhishek Aggarwal on 4/25/2018.
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.MyViewHolder> {

    private List<Contact> contactList;
    private ContactListAdapter.OnClickListener listener;
    Context context;

    boolean isFirstTime = true;

    public ContactListAdapter(ArrayList<Contact> stateList, Context context, ContactListAdapter.OnClickListener onClickListener) {
        this.contactList = stateList;
        this.listener = onClickListener;
        this.context = context;
    }

    @Override
    public ContactListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_item_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ContactListAdapter.MyViewHolder holder, final int position) {

        Contact contact = contactList.get(position);

        holder.bind(contact);
        holder.nameTextView.setText(contact.getName());
        holder.designationTextView.setText(contact.getNumber());

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDeleteClick(position);
            }
        });

        // for animation - comment the below code to stop animation
        if (isFirstTime) {
            Log.e("First Time", position + "");
            holder.itemView.setVisibility(View.GONE);
            Handler handler = new android.os.Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    isFirstTime = false;
                    holder.itemView.setVisibility(View.VISIBLE);
                    AnimationUtils.animateListWave(holder);
                }
            };
            handler.postDelayed(runnable, (position + 1) * AnimationUtils.DELAY_TIME);
        }

    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public interface OnClickListener {
        void onItemClick(Contact contactCategory);
        void onDeleteClick(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;
        public TextView designationTextView;
        public CircleImageView profileImage;
        public Button deleteButton;

        public View myView;

        public MyViewHolder(View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            designationTextView = (TextView) itemView.findViewById(R.id.designationTextView);
            profileImage = (CircleImageView) itemView.findViewById(R.id.profileImage);
            deleteButton = (Button) itemView.findViewById(R.id.deleteButton);

            myView = itemView;
        }

        public void bind(final Contact contact) {
            myView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(contact);
                }
            });
        }

    }
}
