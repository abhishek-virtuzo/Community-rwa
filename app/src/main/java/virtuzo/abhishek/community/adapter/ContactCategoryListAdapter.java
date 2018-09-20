package virtuzo.abhishek.community.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import android.os.Handler;

import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.model.ContactCategory;
import virtuzo.abhishek.community.utils.AnimationUtils;

/**
 * Created by Abhishek Aggarwal on 4/25/2018.
 */

public class ContactCategoryListAdapter extends RecyclerView.Adapter<ContactCategoryListAdapter.MyViewHolder> {

    private List<ContactCategory> contactCategories;
    private ContactCategoryListAdapter.OnClickListener listener;
    Context context;

    boolean isFirstTime = true;

    public ContactCategoryListAdapter(ArrayList<ContactCategory> stateList, Context context, ContactCategoryListAdapter.OnClickListener onClickListener) {
        this.contactCategories = stateList;
        this.listener = onClickListener;
        this.context = context;
    }

    @Override
    public ContactCategoryListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_category_item_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ContactCategoryListAdapter.MyViewHolder holder, final int position) {

        ContactCategory contactCategory = contactCategories.get(position);

        holder.bind(contactCategory);
        holder.textView.setText("> " + contactCategory.getCategoryName());

        // for animation - comment the below code to stop animation
        if (isFirstTime) {
            Log.e("First Time", position+"");
            holder.itemView.setVisibility(View.GONE);
            Handler handler = new android.os.Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    isFirstTime = false;
                    holder.itemView.setVisibility(View.VISIBLE);
                    AnimationUtils.animate(holder);
                }
            };
            handler.postDelayed(runnable, (position + 1) * AnimationUtils.DELAY_TIME);
        }

    }

    @Override
    public int getItemCount() {
        return contactCategories.size();
    }

    public interface OnClickListener {
        void onItemClick(ContactCategory contactCategory);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;

        public View myView;

        public MyViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.textView);

            myView = itemView;
        }

        public void bind(final ContactCategory contactCategory) {
            myView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(contactCategory);
                }
            });
        }

    }
}
