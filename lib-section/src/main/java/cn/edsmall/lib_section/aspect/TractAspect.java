package cn.edsmall.lib_section.aspect;

import android.content.Context;
import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.jetbrains.annotations.NotNull;

import cn.edsmall.lib_section.bean.TimeStatistics;
import cn.edsmall.lib_section.db.BaseDao;
import cn.edsmall.lib_section.db.BaseDaoFactory;

/**
 * 页面时长统计注解处理器
 * 使用时一定要在Activity中重写onResume()、onPause()，才能检测到
 */
@Aspect
public class TractAspect {
    private static final String TAG = "TractAspect";
    private long startTime;
    private long endTime;

    @After("execution(* android.app.Activity.onResume(..))")
    public void onActivityMethodBefore(@NotNull JoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        System.out.println(TAG + " 切面的点执行开始  context==" + (Context) joinPoint.getThis());
        System.out.println(TAG + "切面的点执行开始类名" + signature.getDeclaringType().getCanonicalName());
        startTime = System.currentTimeMillis();


    }

    @After("execution(* android.app.Activity.onPause(..))")
    public void onActivityMethodDestory(@NotNull JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        System.out.println(TAG + " 切面的点执行销毁  context==" + (Context) joinPoint.getThis());
        System.out.println(TAG + "切面的点执行销毁类名" + signature.getDeclaringType().getSimpleName());
        endTime = System.currentTimeMillis();
        Log.e(TAG, "在页面" + signature.getDeclaringType().getSimpleName() + "停留时间" + (endTime - startTime));
        BaseDao<TimeStatistics> baseDao = BaseDaoFactory.getOurInstance().getBaseDao(TimeStatistics.class);
        TimeStatistics statistics = new TimeStatistics(signature.getDeclaringType().getSimpleName(), (endTime - startTime));
        baseDao.insert(statistics);
    }
    @After("execution(* androidx.fragment.app.Fragment.onPause(..))")
    public void onFragmentMethodBefore(@NotNull JoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        System.out.println(TAG + " 切面的点执行开始  context==" + (Context) joinPoint.getThis());
        System.out.println(TAG + "切面的点执行开始类名" + signature.getDeclaringType().getCanonicalName());



    }
}
