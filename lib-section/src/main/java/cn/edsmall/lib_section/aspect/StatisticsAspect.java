package cn.edsmall.lib_section.aspect;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.Fragment;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import cn.edsmall.lib_section.statisticsannotation.Statistics;

/**
 *  防止重复点击注解处理器
 * 注意@Pointcut 切点转到切面
 */
@Aspect
public class StatisticsAspect {
    private static final String TAG = "StatisticsAspect";

    //找到切点
    //@annotation(statistics) 这句话代表可以处理注解传过来的值

    /**
     * 记录一个浪费了几个小时的小坑。
     * 使用 Aspectj 后，一开始还没问题，不知道修改了什么导致编译报错：
     * Caused by: java.util.zip.ZipException: zip file is empty
     * 各种搜索无果，最后发现，居然是因为自定义的 Pointcut 写错了
     * 这个报错信息真的是无语

     */
    @Pointcut("execution(@cn.edsmall.lib_section.statisticsannotation.Statistics * *(..)) && @annotation(permission)")
    public void pointActionMethod(Statistics permission) {

    }

    /**
     * 切面处理切点的业务逻辑，切面的方法参数必须和切点的方法保持一致
     */
    @Around("pointActionMethod(statistics)")
    public Object aroundJointPoint(final ProceedingJoinPoint proceedingJoinPoint, Statistics statistics) throws Throwable {
        //初始化Context ,需要兼容多种情况
        Context context = null;
        Object thisObject = proceedingJoinPoint.getThis();
        if (thisObject instanceof Context) {
            context = (Context) thisObject;
        } else if (thisObject instanceof Fragment) {
            context = ((Fragment) thisObject).getActivity();
        }
        //检测下上下文
        if (context == null) {
            throw new IllegalAccessException("in the StatisticsAspect class context is null,it is not to null");
        }
        //可以准确拿到 当前点击哪个按钮，我们就可以拿到注解里面的值
        String value = statistics.value();

        Log.e(TAG, "本次日志埋点记录的情况是：" + value);
        return proceedingJoinPoint.proceed();
    }
}
