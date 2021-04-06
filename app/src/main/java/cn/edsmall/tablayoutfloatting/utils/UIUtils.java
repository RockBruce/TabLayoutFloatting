package cn.edsmall.tablayoutfloatting.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.lang.reflect.Field;

public class UIUtils {
    private Context context;
//    //标准值
//    private static final float STANDARD_WIDTH = 540f;
//    private static final float STANDARD_HEIGHT = 960f;
//    //标准值
    private static final float STANDARD_WIDTH = 1080f;
    private static final float STANDARD_HEIGHT = 1920f;
    //实际值
    private static float displayMetricsWidth;
    private static float displayMetricsHeight;
    private static UIUtils instance;

    public static UIUtils getInstance(Context context) {
        if (instance == null) {
            instance = new UIUtils(context);
        }
        return instance;
    }

    private UIUtils(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (displayMetricsWidth == 0.0f || displayMetricsHeight == 0.0f) {
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            int systemBarHeight = getSystemBarHeight(context);
            if (displayMetrics.widthPixels > displayMetrics.heightPixels) {
                //横屏
                displayMetricsWidth = displayMetrics.heightPixels;
                displayMetricsHeight = displayMetrics.widthPixels - systemBarHeight;
                Log.e("UIUtils","这是横屏");
                Toast.makeText(context,"这是横屏",Toast.LENGTH_LONG);
            } else {
                //竖屏
                displayMetricsWidth = (float) displayMetrics.widthPixels;
                displayMetricsHeight = (float) displayMetrics.heightPixels - systemBarHeight;
                Toast.makeText(context,"这是竖屏",Toast.LENGTH_LONG);
                Log.e("UIUtils","这是竖屏");
            }
        }
    }

    private int getSystemBarHeight(Context context) {
        return getValue(context, "com.android.internal.R$dimen", "status_bar_height", 48);
    }

    private int getValue(Context context, String dimeClass, String system_bar_height, int default_height) {
        try {
            Class<?> aClass = Class.forName(dimeClass);
            Object object = aClass.newInstance();
            //反射属性 system_bar_height
            Field field = aClass.getField(system_bar_height);
            int id = Integer.parseInt(field.get(object).toString());
            return context.getResources().getDimensionPixelSize(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return default_height;
    }

    public float getHorValueX() {
        return ((float) displayMetricsWidth) / STANDARD_WIDTH;
    }

    public float getVerValueY() {
        return ((float) displayMetricsHeight) / (STANDARD_HEIGHT - getSystemBarHeight(context));
    }
}

