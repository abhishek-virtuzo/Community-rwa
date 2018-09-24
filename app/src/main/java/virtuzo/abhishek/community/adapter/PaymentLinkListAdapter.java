package virtuzo.abhishek.community.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import virtuzo.abhishek.community.R;
import virtuzo.abhishek.community.model.PaymentLink;
import virtuzo.abhishek.community.utils.AnimationUtils;
import virtuzo.abhishek.community.utils.MyFunctions;
import virtuzo.abhishek.community.utils.SquareImageView;

/**
 * Created by Abhishek Aggarwal on 4/25/2018.
 */

public class PaymentLinkListAdapter extends RecyclerView.Adapter<PaymentLinkListAdapter.MyViewHolder> {

    private List<PaymentLink> paymentLinkList;
    private PaymentLinkListAdapter.OnClickListener listener;
    Context context;

    boolean isFirstTime = true;

    public PaymentLinkListAdapter(ArrayList<PaymentLink> stateList, Context context, PaymentLinkListAdapter.OnClickListener onClickListener) {
        this.paymentLinkList = stateList;
        this.listener = onClickListener;
        this.context = context;
    }

    @Override
    public PaymentLinkListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.payment_link_item_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PaymentLinkListAdapter.MyViewHolder holder, final int position) {

        PaymentLink paymentLink = paymentLinkList.get(position);

        holder.bind(paymentLink);
        Glide.with(context).load(paymentLink.getImageUrl()).placeholder(R.drawable.image_background).dontAnimate().into(holder.squareImageView);

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
                    AnimationUtils.animateImageVisible(holder);
                }
            };
            int multiplier = position/2; // as two items in one line (making animation same for item in one line)
            multiplier *= 4;
            handler.postDelayed(runnable, AnimationUtils.DELAY_TIME * (multiplier + 1));
        }

    }

    @Override
    public int getItemCount() {
        return paymentLinkList.size();
    }

    public interface OnClickListener {
        void onItemClick(PaymentLink paymentLink);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public SquareImageView squareImageView;

        public View myView;

        public MyViewHolder(View itemView) {
            super(itemView);

            squareImageView = (SquareImageView) itemView.findViewById(R.id.squareImageView);

            myView = itemView;
        }

        public void bind(final PaymentLink paymentLink) {
            myView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(paymentLink);
                }
            });
        }

    }
}
