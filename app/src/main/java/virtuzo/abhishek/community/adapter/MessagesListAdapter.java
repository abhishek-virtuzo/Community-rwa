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
import virtuzo.abhishek.community.model.Message;

/**
 * Created by Abhishek Aggarwal on 4/25/2018.
 */

public class MessagesListAdapter extends RecyclerView.Adapter<MessagesListAdapter.MyViewHolder> {

    private List<Message> messages;
    private MessagesListAdapter.OnClickListener listener;
    Context context;

    public MessagesListAdapter(ArrayList<Message> stateList, Context context, MessagesListAdapter.OnClickListener onClickListener) {
        this.messages = stateList;
        this.listener = onClickListener;
        this.context = context;
    }

    @Override
    public MessagesListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_item_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MessagesListAdapter.MyViewHolder holder, final int position) {

        Message message = messages.get(position);

        holder.bind(message);
        holder.textView.setText(message.getName());

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public interface OnClickListener {
        void onItemClick(Message message);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;

        public View myView;

        public MyViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.textView);

            myView = itemView;
        }

        public void bind(final Message message) {
            myView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(message);
                }
            });
        }

    }
}
