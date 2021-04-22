package cn.edsmall.lib_section.statisticsannotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * 统计点击事件注解
 */
@Target(ElementType.METHOD) //可以加函数上
@Retention(RetentionPolicy.RUNTIME) //运行时期
public @interface Statistics {
    String value(); //注解传过来的值
}
