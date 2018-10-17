package virtuzo.abhishek.community.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Abhishek Aggarwal on 9/19/2018.
 */

public class AnimationUtils {

    public static final int DELAY_TIME = 40;
    public static final int EXTRA_TIME = 200;

    public static void animateListWave(RecyclerView.ViewHolder holder) {

        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(holder.itemView, "translationX", 500, 0);
        objectAnimator.setDuration(DELAY_TIME + EXTRA_TIME);

        animatorSet.playTogether(objectAnimator);
        animatorSet.start();

    }

    public static final int VISIBLE_TIME = 500;

    public static void animateImageVisible(RecyclerView.ViewHolder holder) {

        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(holder.itemView, "translationX", 500, 0);
        objectAnimator.setDuration(DELAY_TIME + EXTRA_TIME);

        animatorSet.playTogether(objectAnimator);
        animatorSet.start();

    }

}
