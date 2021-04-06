package cn.edsmall.network.disposable;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import cn.edsmall.network.BuildConfig;
import cn.edsmall.network.errorhandler.ExceptionHandle;
import cn.edsmall.network.utils.SubscriberUtils;
import io.reactivex.subscribers.DisposableSubscriber;


public abstract class NetworkDisposable<T> extends DisposableSubscriber<T> {
    public static final String TAG = NetworkDisposable.class.getSimpleName();
    private final Context mContext;

    public NetworkDisposable(Context context) {
        this.mContext = context;
        SubscriberUtils.getInstance().addSubscriber(this);
        Log.e(TAG, "disposable的对象= " + this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (BuildConfig.LEO_DEBUG) {
            Log.e(TAG, "网络开始：onStart() ");
        }
    }

    @Override
    public void onNext(T t) {
        if (BuildConfig.LEO_DEBUG) {
            Log.e(TAG, "有数据返回：onNext() ");
        }
    }

    @Override
    public void onComplete() {
        if (BuildConfig.LEO_DEBUG) {
            Log.e(TAG, "完成了：onComplete()");
        }
    }

    /**
     * @param e
     * @see ExceptionHandle
     */
    @Override
    public void onError(Throwable e) {


        // 服务器收到了请求 参数错误/处理错误  500 501 502
        //服务没有收到请求 400 401 404 403
        if (!TextUtils.isEmpty(e.getMessage())) {
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }
}
