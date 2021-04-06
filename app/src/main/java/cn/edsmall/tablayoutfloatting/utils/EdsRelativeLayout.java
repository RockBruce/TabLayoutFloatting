package cn.edsmall.tablayoutfloatting.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class EdsRelativeLayout extends RelativeLayout {
    public EdsRelativeLayout(Context context) {
        super(context);
    }

    public EdsRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EdsRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float scaleX = UIUtils.getInstance(getContext()).getHorValueX();
        float scaleY = UIUtils.getInstance(getContext()).getVerValueY();
        int childCount = this.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
            layoutParams.width = (int) (layoutParams.width * scaleX);
            layoutParams.height = (int) (layoutParams.height * scaleY);
            layoutParams.leftMargin = (int) (layoutParams.leftMargin * scaleX);
            layoutParams.rightMargin = (int) (layoutParams.rightMargin * scaleX);
            layoutParams.topMargin = (int) (layoutParams.topMargin * scaleY);
            layoutParams.bottomMargin = (int) (layoutParams.bottomMargin * scaleY);
        }

    }
}
