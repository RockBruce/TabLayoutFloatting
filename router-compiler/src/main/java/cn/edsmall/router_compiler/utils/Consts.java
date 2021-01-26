package cn.edsmall.router_compiler.utils;

import com.squareup.javapoet.ClassName;

public class Consts {
    public static final ClassName ROUTER = ClassName.get("com.dongnao.router.core", "DNRouter");
    public static final String ARGUMENTS_NAME = "moduleName";
    public static final String ANN_TYPE_ROUTE = "cn.edsmall.router_annotation.Route"; // 定义注解的类
    public static final String ACTIVITY = "android.app.Activity";
}
