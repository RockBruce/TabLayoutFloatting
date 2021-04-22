package cn.edsmall.tablayoutfloatting.ui.home;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import cn.edsmall.network.bean.RespMsg;
import cn.edsmall.network.disposable.NetworkDisposable;
import cn.edsmall.network.rx.RetrofitManager;
import cn.edsmall.tablayoutfloatting.model.AddAddressBaen;
import cn.edsmall.tablayoutfloatting.service.UserService;

public class HomeViewModel extends AndroidViewModel {
    private final String[] labels = new String[]{"推荐", "歌单", "电台", "排行"};
    private MutableLiveData<String[]> mTabs;
    private MutableLiveData<AddAddressBaen> data = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        Context applicationContext = getApplication().getApplicationContext();
        Log.e("HomeViewModel Context",applicationContext.toString());
        Log.e("HomeViewModel Context",application.toString());
        mTabs = new MutableLiveData<>();
        new RetrofitManager().getDefaultClient(UserService.class).queryArea()
                .compose(new RetrofitManager().applySchedulers(new NetworkDisposable<RespMsg<AddAddressBaen>>(application) {
                    @Override
                    public void onNext(RespMsg<AddAddressBaen> addAddressBaenRespMsg) {
                        super.onNext(addAddressBaenRespMsg);

                        data.setValue(addAddressBaenRespMsg.getData());
                    }
                }));
        mTabs.setValue(labels);
    }

    public LiveData<String[]> getTabs() {
        return mTabs;
    }

    public MutableLiveData<AddAddressBaen> getData() {
        return data;
    }
}