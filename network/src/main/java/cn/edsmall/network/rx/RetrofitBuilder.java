package cn.edsmall.network.rx;

import retrofit2.Retrofit;

/**
 * 创建Retrofit对象(作用注解封装网络请求)
 */
public class RetrofitBuilder {
    private static final RetrofitBuilder ourInstance = new RetrofitBuilder();
    private static Retrofit.Builder builder;

    public static RetrofitBuilder getInstance() {
        return ourInstance;
    }

    private RetrofitBuilder() {
        builder = new Retrofit.Builder();
    }

    Retrofit.Builder getBuilder() {
        return builder;
    }

    /**
     * 提供给调用层设置
     *
     * @param baseUrl
     */
    public void setBaseUrl(String baseUrl) {
        builder.baseUrl(baseUrl);
    }
}
