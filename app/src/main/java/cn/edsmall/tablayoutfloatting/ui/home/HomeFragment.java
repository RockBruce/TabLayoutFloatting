package cn.edsmall.tablayoutfloatting.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import cn.edsmall.router_annotation.destination.FragmentDestination;
import cn.edsmall.tablayoutfloatting.R;
import cn.edsmall.tablayoutfloatting.adapter.ViewPagerAdapter;
import cn.edsmall.tablayoutfloatting.databinding.FragmentHomeBinding;
import cn.edsmall.tablayoutfloatting.ui.fragment.RecyclerViewFragment;

@FragmentDestination(pageUrl = "main/tabs/home",asStarter = true)
public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;
    FragmentHomeBinding mBinding;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding= DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        ViewPagerAdapter pagerAdapter=new ViewPagerAdapter(this.getActivity(),getPageFragment());
        mBinding.viewPager.setAdapter(pagerAdapter);
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        homeViewModel.getTabs().observe(getViewLifecycleOwner(), new Observer<String[]>() {
            @Override
            public void onChanged(String[] strings) {

                new TabLayoutMediator(mBinding.tab, mBinding.viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText(strings[position]);
                    }
                }).attach();
            }
        });
        LinearLayout linearLayout = (LinearLayout) mBinding.tab.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(this.getContext(),R.drawable.bg_line));
        linearLayout.setDividerPadding(30);
        mBinding.tablayoutViewpager.post(new Runnable() {
            @Override
            public void run() {
             mBinding.tablayoutViewpager.getLayoutParams().height=mBinding.scrollView .getMeasuredHeight();
             mBinding.tablayoutViewpager.requestLayout();
            }
        });
        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private List<Fragment> getPageFragment() {
        List<Fragment> data=new ArrayList<>();
        data.add(new RecyclerViewFragment());
        data.add(new RecyclerViewFragment());
        data.add(new RecyclerViewFragment());
        data.add(new RecyclerViewFragment());
        return data;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e("tag","onHiddenChanged="+hidden);
    }
}