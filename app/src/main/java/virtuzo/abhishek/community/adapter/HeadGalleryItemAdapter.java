package virtuzo.abhishek.community.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import virtuzo.abhishek.community.R;

/**
 * Created by virtuzo on 5/8/2018.
 */

public class HeadGalleryItemAdapter extends PagerAdapter {

    ArrayList<HeadGalleryItem> headGalleryItemArrayList;
    Context context;
    LayoutInflater layoutInflater;

    public HeadGalleryItemAdapter(ArrayList<HeadGalleryItem> headGalleryItemArrayList, Context context) {
        this.headGalleryItemArrayList = headGalleryItemArrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return headGalleryItemArrayList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == (RelativeLayout)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.layout_head_gallery_item, container, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.galleryImageView);
        final ProgressBar postImageProgressBar = (ProgressBar) itemView.findViewById(R.id.postImageProgressBar);

        HeadGalleryItem galleryItem = headGalleryItemArrayList.get(position);

        Glide.with(context).load(galleryItem.getImageUrl()).crossFade().thumbnail(0.1f).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                postImageProgressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                postImageProgressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(imageView);

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView( (RelativeLayout) object);
    }
}
