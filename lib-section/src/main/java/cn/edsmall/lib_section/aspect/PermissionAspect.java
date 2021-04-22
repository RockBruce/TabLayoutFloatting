package cn.edsmall.lib_section.aspect;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import cn.edsmall.lib_section.PermissionActivity;
import cn.edsmall.lib_section.core.IPermission;
import cn.edsmall.lib_section.utils.PermissionUtils;
import cn.edsmall.lib_section.permissionannotation.Permission;
import cn.edsmall.lib_section.permissionannotation.PermissionProhibit;
import cn.edsmall.lib_section.permissionannotation.PermissionDenied;

/**
 * 权限申请注解处理器
 */
@Aspect
public class PermissionAspect {
    private static final String TAG = "PermissionAspect";

    @Pointcut("execution(@cn.edsmall.lib_section.permissionannotation.Permission * *(..)) && @annotation(permission)")
    public void requestPermission(Permission permission) {

    }

    @Around("requestPermission(permission)")
    public void aroundJointPoint(final ProceedingJoinPoint joinPoint, Permission permission) throws Throwable {
        Context context = null;
        //当前的插入点位置
        final Object object = joinPoint.getThis();
        if (joinPoint.getThis() instanceof Context) {
            context = (Context) object;
        }
        if (context == null) {
            Log.e(TAG, "没有上下文");
            return;
        }
        final Context finalContext = context;

        PermissionActivity.requestPermission(context, permission.value(), permission.requestCode(), new IPermission() {

            //已授权成功
            @Override
            public void authorized() {
                try {
                    joinPoint.proceed();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

            //禁止权限
            @Override
            public void prohibit() {
                //会调用有@PermissionCancled方法中
                /*   比如其他地方用到这个注解，利用反射PermissionUtils.invokAnnotation(object, PermissionProhibit.class)
                 *    会调用fun requestProhibit()方法中去
                 *    @PermissionProhibit
                 *    fun requestProhibit() {
                 *     Toast.makeText(this, "点击了禁止", Toast.LENGTH_LONG).show()
                 *   }
                 */
                //如果调用这学需要提示或者需要做别的事，可以调用反射。不需要就不用
                PermissionUtils.invokAnnotation(object, PermissionProhibit.class);

            }


            //不在询问
            @Override
            public void denied(String... permissions) {
                PermissionUtils.invokAnnotation(object, PermissionDenied.class);
                StringBuffer buffer = new StringBuffer();
                for (String permission : permissions) {
                    if (!PermissionUtils.hasSelfPermission(finalContext, permission)) {
                        // 这个API主要用于给用户一个申请权限的解释，该方法只有在用户在上一次已经拒绝过你的这个权限申请。
                        // 也就是说，用户已经拒绝一次了，你又弹个授权框，你需要给用户一个解释，为什么要授权，则使用该方法。
                        switch (permission) {
                            case "android.permission.WRITE_EXTERNAL_STORAGE":
                                buffer.append("读写权限");
                                buffer.append("、");
                                break;
                            case "android.permission.CAMERA":
                                buffer.append("相机权限");
                                buffer.append("、");
                                break;
                            default:
                                buffer.append("相关权限没打开、");
                        }
                    }
                }
                String s = buffer.toString().substring(0, buffer.length() - 1);
                AlertDialog aldg;
                AlertDialog.Builder adBd = new AlertDialog.Builder(finalContext);
                adBd.setTitle("温馨提示");

                adBd.setMessage("此功能需要打开:" + s);
                adBd.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PermissionUtils.goToMenu(finalContext);
                    }
                });
                adBd.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                aldg = adBd.create();
                aldg.show();
            }
        });
    }
}
