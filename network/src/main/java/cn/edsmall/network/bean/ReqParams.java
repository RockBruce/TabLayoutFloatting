package cn.edsmall.network.bean;

import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import cn.edsmall.network.BuildConfig;
import cn.edsmall.network.R;
import cn.edsmall.network.interceptor.ParamsInterceptor;
import cn.edsmall.network.utils.Md5Util;

/**
 * 请求头的数据(提供给{@link ParamsInterceptor }获取数据,同时也提供给带调用这{@link #initReqParamMap})
 */
public class ReqParams {
    private static ReqParams ourInstance = null;
    private Map<String, Object> paramsMap=new HashMap<>();

    public static ReqParams getInstance() {
        if (ourInstance == null) {
            synchronized (ReqParams.class) {
                if (ourInstance == null) {
                    ourInstance = new ReqParams();
                }
            }
        }
        return ourInstance;
    }

    private ReqParams() {
    }


    /**
     * 设置请求头
     * 提供给{@link ParamsInterceptor}
     * @return
     */
    public Map<String, Object> getParamsMap() {
        Long timestamp = System.currentTimeMillis() / 1000;
//         token = SharedPreferencesUtils.getString(LOGIN_DATA, TOKEN);
        paramsMap.put("token","");
        paramsMap.put("appkey", "f7ab9296f156213c00a55a0e5e74c34a");
        paramsMap.put("appsecret", Md5Util.md5("56b108c7073d475099872e3803733272$timestamp"));
        paramsMap.put("timestamp", timestamp);
        paramsMap.put("platform", "android");
        return paramsMap;
    }
    /**
     * 该方法提供给调用层初始化的时候调用（可在Application中调用）
     *
     * @param paramsMap 请求头的参数
     */
    public void initReqParamMap(Map<String, Object> paramsMap) {
        this.paramsMap = paramsMap;
    }
}
