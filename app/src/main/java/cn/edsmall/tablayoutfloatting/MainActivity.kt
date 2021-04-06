package cn.edsmall.tablayoutfloatting

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import cn.edsmall.network.bean.ReqParams
import cn.edsmall.network.bean.RespMsg
import cn.edsmall.network.disposable.NetworkDisposable
import cn.edsmall.network.rx.RetrofitBuilder
import cn.edsmall.network.rx.RetrofitManager
import cn.edsmall.network.utils.Md5Util
import cn.edsmall.tablayoutfloatting.model.AddAddressBaen
import cn.edsmall.tablayoutfloatting.service.UserService


class MainActivity : AppCompatActivity() {
    private val parasMap: MutableMap<String, Any> = mutableMapOf()
    private lateinit var reqDispose: NetworkDisposable<RespMsg<AddAddressBaen>>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadData2()

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
        Log.e("MainActivity",reqDispose.isDisposed.toString())
    }
}