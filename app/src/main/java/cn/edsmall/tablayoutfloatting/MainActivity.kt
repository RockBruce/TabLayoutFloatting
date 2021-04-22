package cn.edsmall.tablayoutfloatting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import cn.edsmall.lib_section.clickannotation.DoubleClick
import cn.edsmall.lib_section.statisticsannotation.Statistics
import cn.edsmall.network.bean.RespMsg
import cn.edsmall.network.disposable.NetworkDisposable
import cn.edsmall.network.rx.RetrofitManager
import cn.edsmall.tablayoutfloatting.databinding.ActivityMainBinding
import cn.edsmall.tablayoutfloatting.model.AddAddressBaen
import cn.edsmall.tablayoutfloatting.service.UserService
import com.dongnao.router.core.DNRouter
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val parasMap: MutableMap<String, Any> = mutableMapOf()
    private lateinit var reqDispose: NetworkDisposable<RespMsg<AddAddressBaen>>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataBindingUtil =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        tl.setOnClickListener {
            doubuleClick()
        }
    }

    @Statistics("购买")
    @DoubleClick
    private fun doubuleClick() {

//        startActivity(new Intent(this.getActivity(), MainActivity.class));
        var flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        DNRouter.getInstance().build("/router/login").withFlags(flags).navigation()
    }

    private fun loadData2() {
        val defaultClient = RetrofitManager().getDefaultClient(UserService::class.java)
        reqDispose = object : NetworkDisposable<RespMsg<AddAddressBaen>>(this) {
            override fun onNext(t: RespMsg<AddAddressBaen>?) {
                super.onNext(t)
                Log.e("MainActivity", t.toString())
            }
        }
        defaultClient.queryArea()
            .compose(RetrofitManager().applySchedulers(reqDispose))
        Log.e("MainActivity", reqDispose.isDisposed.toString())
    }
}