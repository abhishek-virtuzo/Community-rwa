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
import virtuzo.abhishek.community.model.ResidentBlock;

/**
 * Created by Abhishek Aggarwal on 4/25/2018.
 */

public class ResidentBlockListAdapter extends RecyclerView.Adapter<ResidentBlockListAdapter.MyViewHolder> {

    private List<ResidentBlock> residentBlocks;
    private ResidentBlockListAdapter.OnClickListener listener;
    Context context;

    public ResidentBlockListAdapter(ArrayList<ResidentBlock> stateList, Context context, ResidentBlockListAdapter.OnClickListener onClickListener) {
        this.residentBlocks = stateList;
        this.listener = onClickListener;
        this.context = context;
    }

    @Override
    public ResidentBlockListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.resident_block_item_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ResidentBlockListAdapter.MyViewHolder holder, final int position) {

        ResidentBlock residentBlock = residentBlocks.get(position);

        holder.bind(residentBlock);
        holder.textView.setText(residentBlock.getBlockName());

    }

    @Override
    public int getItemCount() {
        return residentBlocks.size();
    }

    public interface OnClickListener {
        void onItemClick(ResidentBlock residentBlock);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;

        public View myView;

        public MyViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.textView);

            myView = itemView;
        }

        public void bind(final ResidentBlock residentBlock) {
            myView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(residentBlock);
                }
            });
        }

    }
}
