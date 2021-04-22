package cn.edsmall.lib_section.aspect;

import android.content.Context;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * 防止重复点击注解处理器
 */
@Aspect
public class DoubleClickAspect {
    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime=0;
    private static final String TAG = "DoubleClickAspect";
    @Pointcut("execution(@cn.edsmall.lib_section.clickannotation.DoubleClick * *(..))")
    public void preventClick() {

    }
    @Around("preventClick()")
    public void aroundJointPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        long curClickTime = System.currentTimeMillis();
        System.out.println(TAG + " 双击  context==" + (Context) joinPoint.getThis());
      if (Math.abs(lastClickTime-curClickTime)>MIN_CLICK_DELAY_TIME){
          joinPoint.proceed();
      }
        lastClickTime = curClickTime;
    }


}
