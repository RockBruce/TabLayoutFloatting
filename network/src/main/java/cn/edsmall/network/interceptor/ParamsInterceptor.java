package cn.edsmall.network.interceptor;

import java.io.IOException;
import java.util.Map;

import cn.edsmall.network.bean.ReqParams;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 请求头设置
 */
public class ParamsInterceptor implements Interceptor {
    private static final ParamsInterceptor ourInstance = new ParamsInterceptor();

    public static ParamsInterceptor getInstance() {
        return ourInstance;
    }

    public ParamsInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        ReqParams reqParams = ReqParams.getInstance();
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        for (Map.Entry<String, Object> entry : reqParams.getParamsMap().entrySet()) {
            builder.addHeader(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return chain.proceed(builder.build());
    }
}
