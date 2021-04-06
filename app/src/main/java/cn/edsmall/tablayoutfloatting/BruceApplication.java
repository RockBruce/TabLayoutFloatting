package cn.edsmall.tablayoutfloatting;

import android.app.Application;

import java.util.HashMap;

import cn.edsmall.network.bean.ReqParams;
import cn.edsmall.network.rx.RetrofitBuilder;
import cn.edsmall.network.utils.Md5Util;

public class BruceApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RetrofitBuilder.getInstance().setBaseUrl("https://channelmachine-pre.edsmall.com");
    }
}
