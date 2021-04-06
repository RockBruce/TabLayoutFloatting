package cn.edsmall.network.rx;

import java.util.concurrent.TimeUnit;

import cn.edsmall.network.interceptor.LoggingInterceptor;
import cn.edsmall.network.interceptor.ParamsInterceptor;
import okhttp3.OkHttpClient;

/**
 * 构建okHttp对象
 */
public class OkHttpClientManager {
    private static final String TAG = "OkHttpClientManager";
    private OkHttpClient.Builder builder;
    private static OkHttpClientManager instance = null;

    private OkHttpClientManager() {
        builder = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(ParamsInterceptor.getInstance())     //请求头的设置拦截器
                .addInterceptor(LoggingInterceptor.getInstance());   //打印日志拦截器
    }

    public static OkHttpClientManager getInstance() {
        if (instance == null) {
            synchronized (OkHttpClientManager.class) {
                if (instance == null) {
                    instance = new OkHttpClientManager();
                }
            }
        }
        return instance;
    }

    public OkHttpClient.Builder getBuilder() {
        return builder;
    }

}
