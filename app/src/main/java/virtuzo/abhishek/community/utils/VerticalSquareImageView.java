package virtuzo.abhishek.community.utils;

import android.content.Context;
import android.util.AttributeSet;

// Square is adjusted with respect to height
public class VerticalSquareImageView extends android.support.v7.widget.AppCompatImageView {

    public VerticalSquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = getMeasuredHeight();
        setMeasuredDimension(height, height);
    }

}