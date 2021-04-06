package cn.edsmall.network.interceptor;

import android.util.Log;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import cn.edsmall.network.BuildConfig;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * @see cn.edsmall.network.rx.OkHttpClientManager
 */
public class LoggingInterceptor implements Interceptor {

    private static final String TAG = "LoggingInterceptor";
    private static final LoggingInterceptor ourInstance = new LoggingInterceptor();

    public static LoggingInterceptor getInstance() {
        return ourInstance;
    }

    private LoggingInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Charset UTF8 = Charset.forName("UTF-8");
        if (BuildConfig.LEO_DEBUG || true) {
            // 打印请求报文
            Request request = chain.request();
            long t1 = System.nanoTime();
            Log.d(TAG, String.format("请求方式 %s 请求Url: %s on %s%n%s", request.method(), request.url(), chain.connection(), request.headers()));
            if (request.method().equals("POST")) {
                Request copy = request.newBuilder().build();
                if (copy.body() != null) {
                    Buffer buffer = new Buffer();
                    copy.body().writeTo(buffer);
                    Log.d(TAG, String.format("%n Request Body%n %s", buffer.readUtf8()));
                }
            }
            // 打印返回报文
            // 先执行请求，才能够获取报文
            Response response = chain.proceed(request);
            ResponseBody responseBody = response.body();
            String respBody = null;
            if (responseBody != null) {
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE);
                Buffer buffer = source.buffer();

                Charset charset = UTF8;
                MediaType contentType = responseBody.contentType();
                if (contentType != null) {
                    try {
                        charset = contentType.charset(UTF8);
                    } catch (UnsupportedCharsetException e) {
                        e.printStackTrace();
                    }
                }
                respBody = buffer.clone().readString(charset);
            }
            Log.d(TAG, String.format("收到响应\n响应码：%s\n响应信息：%s\n响应头：%s\n请求url：%s\n响应body：%s",
                    response.code(), response.message(),response.headers(), response.request().url(), respBody));
            long t2 = System.nanoTime();
//            Log.d(TAG, String.format("Received response for %s in %.1fms%n%s", response.request().url(), (t2 - t1) / 1e6d, response.headers()));
            return response;
        } else {
            return chain.proceed(chain.request());
        }
    }
}
