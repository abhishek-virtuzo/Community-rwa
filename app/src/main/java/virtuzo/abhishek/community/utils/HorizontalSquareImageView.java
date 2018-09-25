package virtuzo.abhishek.community.utils;

import android.content.Context;
import android.util.AttributeSet;

// Square is adjusted with respect to width
public class HorizontalSquareImageView extends android.support.v7.widget.AppCompatImageView {

    public HorizontalSquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }

}