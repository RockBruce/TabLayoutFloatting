package cn.edsmall.router_annotation.destination;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
//@Retention(RetentionPolicy.CLASS)
public @interface ActivityDestination {
    String pageUrl();

    boolean needLogin() default false;

    boolean asStarter() default false;
}
