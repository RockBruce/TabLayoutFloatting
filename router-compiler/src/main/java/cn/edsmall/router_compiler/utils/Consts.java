package cn.edsmall.router_compiler.utils;

import com.squareup.javapoet.ClassName;

public class Consts {
    public static final ClassName ROUTER = ClassName.get("com.dongnao.router.core", "DNRouter");
    public static final String ARGUMENTS_NAME = "moduleName";
    public static final String ANN_TYPE_ROUTE = "cn.edsmall.router_annotation.Route"; // 定义注解的类
    public static final String ACTIVITY = "android.app.Activity";
    public static final String IROUTE_GROUP = "com.dongnao.router.core.template.IRouteGroup";
    public static final String IROUTE_ROOT = "com.dongnao.router.core.template.IRouteRoot";
    public static final String SEPARATOR = "$$";
    public static final String PROJECT = "DNRouter";
    public static final String NAME_OF_ROOT = PROJECT + SEPARATOR + "Root" + SEPARATOR;
    public static final String NAME_OF_GROUP = PROJECT + SEPARATOR + "Group" + SEPARATOR;
    public static final String PACKAGE_OF_GENERATE_FILE = "cn.edsmall.router.routes";
    public static final String METHOD_LOAD_INTO = "loadInto";
}
