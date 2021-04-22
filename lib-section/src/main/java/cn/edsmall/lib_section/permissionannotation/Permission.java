package cn.edsmall.lib_section.permissionannotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.edsmall.lib_section.utils.PermissionUtils;

/**
 * @Autor HGH
 * 2020/11/16
 * 权限申请注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Permission {
    String[] value(); //所申请的权限 (用Value这个字段注解的参数可以不用写)
    int requestCode() default PermissionUtils.DEFAULT_REQUEST_CODE;


}
