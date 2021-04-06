package cn.edsmall.network.errorhandler;

import android.util.Log;

import cn.edsmall.network.BuildConfig;
import cn.edsmall.network.errorhandler.ExceptionHandle;
import io.reactivex.Flowable;
import io.reactivex.functions.Function;

public class HttpErrorHandler<T> implements Function<Throwable, Flowable<T>> {
    @Override
    public Flowable<T> apply(Throwable throwable) throws Exception {
        if (BuildConfig.LEO_DEBUG){
        }
        return io.reactivex.Flowable.error(ExceptionHandle.handleException(throwable));
    }
}
