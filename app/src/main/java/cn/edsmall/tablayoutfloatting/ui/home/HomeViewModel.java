package cn.edsmall.tablayoutfloatting.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {
   final String[] labels=new String[]{"推荐","歌单","电台","排行"};
    private MutableLiveData<String[]> mTabs;

    public HomeViewModel() {
        mTabs = new MutableLiveData<>();
        mTabs.setValue(labels);
    }

    public LiveData<String[]> getTabs() {
        return mTabs;
    }
}