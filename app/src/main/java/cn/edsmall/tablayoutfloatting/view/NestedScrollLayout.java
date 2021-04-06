package cn.edsmall.tablayoutfloatting.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import cn.edsmall.tablayoutfloatting.utils.FlingHelper;

/**
 * 参考
 * https://github.com/ming123aaa/test_NestScorllView
 */
public class NestedScrollLayout extends NestedScrollView {
    private View topView; //头部的View
    private ViewGroup contentView; // ViewPager中的ReyclerView
    private static final String TAG = "NestedScrollLayout"; //TAG
    @RequiresApi(api = Build.VERSION_CODES.M)
    public NestedScrollLayout(Context context) {
        this(context, null);
        init();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public NestedScrollLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public NestedScrollLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public NestedScrollLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private FlingHelper mFlingHelper; // veltociy和distance的转换

    int totalDy = 0;
    /**
     * 用于判断RecyclerView是否在fling
     */
    boolean isStartFling = false;
    /**
     * 记录当前滑动的y轴加速度
     */
    private int velocityY = 0;

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void init() {
        mFlingHelper = new FlingHelper(getContext());
        setOnScrollChangeListener(new View.OnScrollChangeListener() { //监听自己（NestedScrollView）滑动
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (isStartFling) {
                    totalDy = 0;
                    isStartFling = false;
                }
                if (scrollY == 0) { // 到达顶部的时候
                    Log.i(TAG, "TOP SCROLL");
                    // refreshLayout.setEnabled(true);
                }
                // topView完全消失了，该子View处理了
                if (scrollY == (getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    Log.i(TAG, "BOTTOM SCROLL");
                    dispatchChildFling();
                }
                //在RecyclerView fling情况下，记录当前RecyclerView在y轴的偏移
                totalDy += scrollY - oldScrollY;
            }
        });
    }

    private void dispatchChildFling() {
        if (velocityY != 0) { // 滑动的速度，和distance可以相互转换
            // 转换成距离
            Double splineFlingDistance = mFlingHelper.getSplineFlingDistance(velocityY);
            if (splineFlingDistance > totalDy) {
                // 转换成velocityY
                childFling(mFlingHelper.getVelocityByDistance(splineFlingDistance - Double.valueOf(totalDy)));
            }
        }
        // 处理完之后，恢复默认值
        totalDy = 0;
        velocityY = 0;
    }

    private void childFling(int velY) {
        RecyclerView childRecyclerView = getChildRecyclerView(contentView); // 找到子View（RecyclerView）
        if (childRecyclerView != null) {
            childRecyclerView.fling(0, velY); //fling事件传出去
        }
    }

    @Override
    public void fling(int velocityY) { // 自己的fling
        super.fling(velocityY);
        if (velocityY <= 0) {
            this.velocityY = 0;
        } else {
            isStartFling = true; //开始fling滑动
            this.velocityY = velocityY; //滑动的速度
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //顶部的View
        topView = ((ViewGroup) getChildAt(0)).getChildAt(0);
        //下面的RecyclerView，
        contentView = (ViewGroup) ((ViewGroup) getChildAt(0)).getChildAt(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 调整contentView的高度为父容器高度，使之填充布局，避免父容器滚动后出现空白
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        ViewGroup.LayoutParams lp = contentView.getLayoutParams();
//        lp.height = getMeasuredHeight(); //测量的是整个父View的高度
//        contentView.setLayoutParams(lp);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        Log.i("NestedScrollLayout", getScrollY()+"::onNestedPreScroll::"+topView.getMeasuredHeight());
        // 向上滑动。若当前topview可见，需要将topview滑动至不可见
        boolean hideTop = dy > 0 && getScrollY() < topView.getMeasuredHeight();
        if (hideTop) {
            scrollBy(0, dy); //相对滑动了多少位置
            consumed[1] = dy; // 消费掉y轴的滑动事件
        }
    }

    private RecyclerView getChildRecyclerView(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof RecyclerView && view.getClass() == RecyclerView.class) {
                return (RecyclerView) viewGroup.getChildAt(i);
            } else if (viewGroup.getChildAt(i) instanceof ViewGroup) {
                ViewGroup childRecyclerView = getChildRecyclerView((ViewGroup) viewGroup.getChildAt(i));
                if (childRecyclerView instanceof RecyclerView) {
                    return (RecyclerView) childRecyclerView;
                }
            }
            continue;
        }
        return null;
    }
}

