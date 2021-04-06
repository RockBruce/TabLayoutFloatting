package cn.edsmall.tablayoutfloatting.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnScrollChangeListener;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import cn.edsmall.tablayoutfloatting.utils.FlingHelper;


@SuppressLint("NewApi")
public class EdsNestedScrollView extends NestedScrollView implements OnScrollChangeListener {

    private int velocityY = 0;
    private int totalDy = 0;
    FlingHelper mFlingHelper;
    boolean isStartFling = false;
    ViewPager2 viewPager2;

    public EdsNestedScrollView(@NonNull Context context) {
        super(context);
        init();
    }

    public EdsNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EdsNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        setOnScrollChangeListener(this);
        mFlingHelper = new FlingHelper(getContext());
    }


    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        int headerViewHeight = getChildAt(0).getMeasuredHeight() - getMeasuredHeight();
        boolean hideTop = dy > 0 && getScaleY() < headerViewHeight;
        if (hideTop) {
            scrollBy(0, dy);
            consumed[1] = dy;

        }
    }

    @Override
    public void fling(int velocityY) {
        super.fling(velocityY);
        this.velocityY = velocityY;
        if (velocityY > 0) {
            isStartFling = true;
            totalDy = 0;
        }
    }


    @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        totalDy += scrollY - oldScrollY;
        if (scrollY == (getChildAt(0).getMeasuredHeight() - getMeasuredHeight())) {
            if (velocityY != 0) {
                double splineFlingDistance = mFlingHelper.getSplineFlingDistance(velocityY);
                if (splineFlingDistance > totalDy) {
                    viewPager2 = getChildRecyclerView(this, ViewPager2.class);
                    if (viewPager2 != null) {
                        RecyclerView childRecyclerView = getChildRecyclerView(((ViewGroup) viewPager2.getChildAt(0)).getChildAt(viewPager2.getCurrentItem()), RecyclerView.class);
                        if (childRecyclerView != null) {
                            childRecyclerView.fling(0, mFlingHelper.getVelocityByDistance(splineFlingDistance - Double.valueOf(totalDy)));
                        }
                    }
                }
                totalDy = 0;
                velocityY = 0;
            }
        }
    }

    private <T> T getChildRecyclerView(View viewGroup, Class<T> targetClass) {
        if (viewGroup != null && viewGroup.getClass() == targetClass) {
            return (T) viewGroup;
        }
        if (viewGroup instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) viewGroup).getChildCount(); i++) {
                View view = ((ViewGroup) viewGroup).getChildAt(i);
                if (view instanceof ViewGroup) {
                    T result = getChildRecyclerView(view, targetClass);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }
}
