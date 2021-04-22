package cn.edsmall.lib_section.permissionannotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.edsmall.lib_section.utils.PermissionUtils;

/**
 * @Autor HGH
 * 2020/11/16
 * 选择禁止后不在提示框
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PermissionDenied {
    String[] value();
    int requestCode() default PermissionUtils.DEFAULT_REQUEST_CODE;
}
