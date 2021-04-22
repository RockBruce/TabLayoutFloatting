package cn.edsmall.lib_section

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityCompat
import cn.edsmall.lib_section.core.IPermission
import cn.edsmall.lib_section.utils.PermissionUtils

/**
 * 目的 切人后真正实现权限申请的类
 */
class PermissionActivity : Activity() {
    private var mRequestCode: Int = 0
    private lateinit var mPermission: Array<String>

   companion object{
       private const val PARAM_PERMISSION: String = "param_permission"
       private const val PARAM_REQUEST_REQUESTCODE: String = "param_request_requestCode"
       private lateinit var sPermissionListener: IPermission
       @JvmStatic
       fun requestPermission(
           context: Context,
           permission: Array<String>,
           requestCode: Int,
           iPermission: IPermission
       ) {
           this.sPermissionListener = iPermission
           val intent = Intent(context,PermissionActivity::class.java)
           intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
           val bundle = Bundle()
           bundle.putStringArray(PARAM_PERMISSION, permission)
           bundle.putInt(PARAM_REQUEST_REQUESTCODE, requestCode)
           intent.putExtras(bundle)
           context.startActivity(intent)
           if (context is Activity) {
               context.overridePendingTransition(0, 0)
           }
       }
   }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)
        this.mPermission = intent.getStringArrayExtra(PARAM_PERMISSION)
        this.mRequestCode = intent.getIntExtra(PARAM_REQUEST_REQUESTCODE, 0)
        if (mPermission == null || sPermissionListener == null) {
            this.finish()
            return
        }
        //权限检查
        if (PermissionUtils.hasPermission(this, *mPermission)) {
            //申请的权限已经存在
            sPermissionListener.authorized()
            finish()
            return
        }
        //权限申请
        ActivityCompat.requestPermissions(this, mPermission, mRequestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //检查是否已授权
        if (PermissionUtils.verifyPermission(this, grantResults)) {
            sPermissionListener.authorized()
            finish()
            return
        }
        //用户点击不在显示，提示UI,跳转到设置
        if (!PermissionUtils.shouldShowRequestPermissionRationale(this, *permissions)) {
            sPermissionListener.denied(*permissions)
            finish()
            return
        }
        sPermissionListener.prohibit()
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}