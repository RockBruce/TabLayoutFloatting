package cn.edsmall.network.rx;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.concurrent.TimeUnit;

import cn.edsmall.network.bean.RespMsg;
import cn.edsmall.network.errorhandler.ExceptionHandle;
import cn.edsmall.network.errorhandler.HttpErrorHandler;
import cn.edsmall.network.interceptor.CacheInterceptor;
import cn.edsmall.network.interceptor.LoggingInterceptor;
import cn.edsmall.network.interceptor.ParamsInterceptor;
import io.reactivex.BackpressureOverflowStrategy;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator_LZH on 2017/5/22.
 */
public class RetrofitManager {
    private Retrofit.Builder retrofitBuilder;
    private Gson mGson;

    public RetrofitManager() {
        mGson = new GsonBuilder().serializeNulls().create();
        retrofitBuilder = RetrofitBuilder.getInstance().getBuilder();
    }

    public <T> T getDefaultClient(Class<T> serviceClass) {
        OkHttpClient client = OkHttpClientManager.getInstance().getBuilder().build();
        return retrofitBuilder
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(mGson))  //(数据解析器工厂集合)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())) // (Call适配器工厂集合)RxJava将Retrofit的call转成Observer
                .build()
                .create(serviceClass);
    }

   /* public <T> T getNotificationClient(Class<T> serviceClass, DownNotification downNotification, final String completeTitle) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(ParamsInterceptor.getInstance())
                .addInterceptor(LoggingInterceptor.getInstance())
                .addInterceptor(new DownloadInterceptor((bytesRead, contentLength, done) -> {
                    if (done) {
                        downNotification.getBuilder().setContentTitle(completeTitle);
                        downNotification.setProgressComplete("下载完成");
                    } else {
                        downNotification.setProgress((Long.valueOf(contentLength)).intValue(), (Long.valueOf(bytesRead)).intValue(), false);
                    }
                }))
                .build();
        return retrofitBuilder.client(client)
                .addConverterFactory(GsonConverterFactory.create(mGson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build().create(serviceClass);
    }*/

   /* public <T> T getProgressDialogClient(Class<T> serviceClass, final NumberProgressBar dialog) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(ParamsInterceptor.getInstance())
                .addInterceptor(LoggingInterceptor.getInstance())
                .addInterceptor(new UpLoadInterceptor((bytesWritten, contentLength) ->
                        Observable.create((ObservableOnSubscribe<Integer>) emitter ->
                                dialog.setProgress((int) ((Long.valueOf(bytesWritten).floatValue() / Long.valueOf(contentLength).floatValue()) * 100)))
                                .subscribeOn(AndroidSchedulers.mainThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe()))
                .build();
        return retrofitBuilder.client(client)
                .addConverterFactory(GsonConverterFactory.create(mGson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build().create(serviceClass);
    }*/

    public <T> T getCacheClient(Class<T> serviceClass, File cacheFile) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(ParamsInterceptor.getInstance())
                .addInterceptor(LoggingInterceptor.getInstance())
                .cache(new Cache(cacheFile, 10 * 1024 * 1024))
                .addNetworkInterceptor(new CacheInterceptor())
                .build();
        return retrofitBuilder.client(client)
                .addConverterFactory(GsonConverterFactory.create(mGson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build().create(serviceClass);
    }

    /**
     * FlowableTransformer        要对应 DisposableSubscriber
     * ObservableTransformer     要对应Observer
     *
     * @param disposable 观察者
     * @param <T>
     * @return
     */
    public <T> FlowableTransformer<T, T> applySchedulers(final DisposableSubscriber<T> disposable) {
        return (upstream) -> {
            //被观察角色
            Flowable<T> observable = upstream
                            .subscribeOn(Schedulers.io()) //指定网络请求发生在子线程
                            .observeOn(AndroidSchedulers.mainThread())   // 切换到主线程中去
                            .map(getAppErrorHandler())                   //错误的处理
                            .onErrorResumeNext(new HttpErrorHandler<T>());
            //订阅观察者
            observable.subscribe(disposable);
            return observable;
        };
    }

    public <T> Function<T, T> getAppErrorHandler() {
        return (resopnse) -> {
            if (resopnse instanceof RespMsg && ((RespMsg) resopnse).getCode() != 200) {
                ExceptionHandle.ServerException exception = new ExceptionHandle.ServerException();
                exception.code = ((RespMsg) resopnse).getCode();
                exception.message = ((RespMsg) resopnse).getMessage() != null ? ((RespMsg) resopnse).getMessage() : "";
                throw exception;

            }
            return resopnse;
        };
    }
}
