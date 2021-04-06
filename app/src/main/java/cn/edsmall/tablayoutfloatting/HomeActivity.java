package cn.edsmall.tablayoutfloatting;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.logging.Logger;

import cn.edsmall.network.bean.RespMsg;
import cn.edsmall.network.disposable.NetworkDisposable;
import cn.edsmall.network.rx.RetrofitManager;
import cn.edsmall.tablayoutfloatting.model.AddAddressBaen;
import cn.edsmall.tablayoutfloatting.service.UserService;
import cn.edsmall.tablayoutfloatting.utils.NavGraphBuilder;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private NavController navController;
    private BottomNavigationView navView;
    private long exitTime=0l;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
       navView = findViewById(R.id.nav_view);
        Fragment fragment= getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        navController = NavHostFragment.findNavController( fragment);
//        NavigationUI.setupWithNavController(navView, navController);
        NavGraphBuilder.build(this, fragment.getChildFragmentManager(), navController, fragment.getId());
        navView.setOnNavigationItemSelectedListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            navController.navigate(item.getItemId());
        return !TextUtils.isEmpty(item.getTitle());
    }
    @SuppressLint("CheckResult")
    private void loadData() {
       new RetrofitManager().getDefaultClient(UserService.class).queryArea()
               .compose(new RetrofitManager().applySchedulers(new NetworkDisposable<RespMsg<AddAddressBaen>>(this) {
                   @Override
                   public void onNext(RespMsg<AddAddressBaen> addAddressBaenRespMsg) {
                       super.onNext(addAddressBaenRespMsg);
                       Log.e("MainActivity",addAddressBaenRespMsg.getData().toString());
                   }
               }));

    }
    @Override
    public void onBackPressed() {
        //当前正在显示的页面destinationId
        int currentPageId = navController.getCurrentDestination().getId();

        //APP页面路导航结构图  首页的destinationId
        int homeDestId = navController.getGraph().getStartDestination();

//        //如果当前正在显示的页面不是首页，而我们点击了返回键，则拦截。
//        if (currentPageId != homeDestId) {
//            navView.setSelectedItemId(homeDestId);
//            return;
//        }
        if (System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(this,"再按一次退出应用程序",Toast.LENGTH_LONG).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
        //否则 finish，此处不宜调用onBackPressed。因为navigation会操作回退栈,切换到之前显示的页面。
//        finish();
    }
}