package cn.edsmall.router_compiler.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import cn.edsmall.router_annotation.Route;
import cn.edsmall.router_annotation.destination.FragmentDestination;
import cn.edsmall.router_annotation.model.RouteMeta;
import cn.edsmall.router_compiler.utils.Consts;
import cn.edsmall.router_compiler.utils.Log;
import cn.edsmall.router_compiler.utils.Utils;

import static javax.lang.model.element.Modifier.*;

/**
 * 注解处理器
 * 处理注解{@link Route }
 */
//用 annotationProcessor 'com.google.auto.service:auto-service:1.0-rc6' 自动注册
//可以在build/java/main/META-INF/services/javax.annotation.processing.Processor看到，可作为检查当前类有没有注册上
@AutoService(Processor.class)
/**
 * 处理器接收的参数 代替{@link AbstractProcessor#getSupportedOptions()} 函数
 */
@SupportedOptions(Consts.ARGUMENTS_NAME)
/**
 * 指定使用的Java版本 代替{@link AbstractProcessor#getSupportedSourceVersion()} 函数
 */
@SupportedSourceVersion(SourceVersion.RELEASE_7)
/**
 * 注册给哪些注解的 代替{@link AbstractProcessor#getSupportedAnnotationTypes()} 函数
 */
@SupportedAnnotationTypes({Consts.ANN_TYPE_ROUTE})
public class RouteProcessor extends AbstractProcessor {
    /**
     * key:组名 value:类名
     */
    private Map<String, String> rootMap = new TreeMap<>();
    /**
     * 分组 key:组名 value:对应组的路由信息
     */
    private Map<String, List<RouteMeta>> groupMap = new HashMap<>();
    /**
     * 节点工具类 (类、函数、属性都是节点)
     */
    private Elements elementUtils;

    /**
     * type(类信息)工具类
     */
    private Types typeUtils;
    /**
     * 文件生成器 类/资源
     */
    private Filer filerUtils;
    /**
     * 参数
     */
    private String moduleName;

    private Log log;

    //相当于构造函数，注册的时候会调用这个函数
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        //获得apt的日志输出
        log = Log.newLog(processingEnvironment.getMessager());
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        filerUtils = processingEnvironment.getFiler();
        //参数是模块名 为了防止多模块/组件化开发的时候 生成相同的 xx$$ROOT$$文件
        Map<String, String> options = processingEnvironment.getOptions();
        if (!Utils.isEmpty(options)) {
            moduleName = options.get(Consts.ARGUMENTS_NAME);
        }
        log.i("RouteProcessor Parmaters:" + moduleName);
        if (Utils.isEmpty(moduleName)) {
            throw new RuntimeException("Not set Processor Parmaters.");
        }
    }

    /**
     * 相当于main函数，正式处理注解
     *
     * @param set              使用了支持处理注解的节点集合
     * @param roundEnvironment 表示当前或是之前的运行环境，可以通过该对象查找服务的注解
     * @return true 表示后续处理不会在处理（已经处理）
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (!Utils.isEmpty(set)) {
            //被Route注解  注解节点集合
            Set<? extends Element> routeElements = roundEnvironment.getElementsAnnotatedWith(Route.class);

            if (!Utils.isEmpty(routeElements)) {
                try {
                    processRout(routeElements);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 处理被注解了的节点
     *
     * @param routeElements
     */
    private void processRout(Set<? extends Element> routeElements) throws IOException {
        RouteMeta routeMeta;
        //获取Activity这个类的节点信息
        TypeElement activity = elementUtils.getTypeElement(Consts.ACTIVITY);
        log.i("RouteProcessor activity=" + activity.asType());
        for (Element element : routeElements) {
            //类信息
            TypeMirror typeMirror = element.asType();
            log.i("RouteProcessor class name=" + typeMirror.toString());
            Route route = element.getAnnotation(Route.class);
            log.i("RouteProcessor route=" + route.path());
            //只能在指定的类上面使用
            if (typeUtils.isSubtype(typeMirror, activity.asType())) {
                routeMeta = new RouteMeta(RouteMeta.Type.ACTIVITY, route, element);
                log.i("RouteProcessor isSubtype=" + typeUtils.isSubtype(typeMirror, activity.asType()));

            } else {
                throw new RuntimeException("[Sorry!! Just support] Activity Route:" + element);
            }
            //分组信息记录  groupMap <Group分组,RouteMeta路由信息> 集合
            categories(routeMeta);
        }
        //生成类需要实现的接口
        TypeElement iRouteGroup = elementUtils.getTypeElement(Consts.IROUTE_GROUP);
        log.i("RouteProcessor iRouteGroup============" + iRouteGroup);
        TypeElement iRouteRoot = elementUtils.getTypeElement(Consts.IROUTE_ROOT);
        /**
         *  生成Group类 作用:记录 <地址,RouteMeta路由信息(Class文件等信息)>
         */
        generatedGroup(iRouteGroup);
        /**
         * 生成Root类 作用:记录 <分组，对应的Group类>
         */
        generatedRoot(iRouteRoot, iRouteGroup);

    }

    private void generatedGroup(TypeElement iRouteGroup) throws IOException {
        //创建参数类型 Map<String,RouteMeta>
        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class), ClassName.get(RouteMeta.class));
        //创建参数 Map<String,RouteMeta> atlas
        ParameterSpec atlas = ParameterSpec.builder(parameterizedTypeName, "atlas").build();
        //遍历分组 每一个分组创建一个 $$Group$$类
        for (Map.Entry<String, List<RouteMeta>> entry : groupMap.entrySet()) {
            /**
             * 类成员函数loadInfo声明构建
             */
            //函数 public void loadInfo(Map<String,RouteMeta> atlas)
            MethodSpec.Builder method = MethodSpec.methodBuilder("loadInto")
                    .addModifiers(PUBLIC)
                    .addAnnotation(Override.class)
                    .addParameter(atlas);

            //分组名 与 对应分组中的信息
            String groupName = entry.getKey();
            List<RouteMeta> groupData = entry.getValue();
            //遍历分组中的条目 数据
            for (RouteMeta routeData : groupData) {
                //添加函数体
                // atlas.put(地址,RouteMeta.build(Class,path,group))
                // $S https://github.com/square/javapoet#s-for-strings
                // $T https://github.com/square/javapoet#t-for-types
                method.addStatement("atlas.put($S,$T.build($T.$L,$T.class,$S,$S))",
                        routeData.getPath(),
                        ClassName.get(RouteMeta.class),
                        ClassName.get(RouteMeta.Type.class),
                        routeData.getType(),
                        ClassName.get((TypeElement) routeData.getElement()),
                        routeData.getPath(),
                        routeData.getGroup());
            }
            //生成类名   创建java文件($$Group$$) 组  DNRouter$$Group$$xx
            String groupClassName = Consts.NAME_OF_GROUP + groupName;
            //创建类
            TypeSpec typeSpec = TypeSpec.classBuilder(groupClassName)
                    .addSuperinterface(ClassName.get(iRouteGroup))
                    .addModifiers(PUBLIC)
                    .addMethod(method.build())
                    .build();
            //生成java文件
            //在build/generated/ap_generated_sources/debug/out 下的cn.edsmall.router.routes生成这个类
            JavaFile javaFile = JavaFile.builder(Consts.PACKAGE_OF_GENERATE_FILE, typeSpec).build();
            javaFile.writeTo(filerUtils);
            //分组名和生成的对应的Group类类名
            rootMap.put(groupName, groupClassName);
        }
    }

    private void generatedRoot(TypeElement iRouteRoot, TypeElement iRouteGroup) throws IOException {
        //类型 Map<String,Class<? extends IRouteGroup>> routes>
        //Wildcard 通配符
        ParameterizedTypeName routes = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(
                        ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(iRouteGroup))
                )
        );

        //参数 Map<String,Class<? extends IRouteGroup>> routes> routes
        ParameterSpec rootParamSpec = ParameterSpec.builder(routes, "routes")
                .build();
        //函数 public void loadInfo(Map<String,Class<? extends IRouteGroup>> routes> routes)
        MethodSpec.Builder loadIntoMethodOfRootBuilder = MethodSpec.methodBuilder
                (Consts.METHOD_LOAD_INTO)
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(rootParamSpec);

        //函数体
        for (Map.Entry<String, String> entry : rootMap.entrySet()) {
            loadIntoMethodOfRootBuilder.addStatement("routes.put($S, $T.class)", entry
                    .getKey(), ClassName.get(Consts.PACKAGE_OF_GENERATE_FILE, entry.getValue
                    ()));
        }
        //生成 $Root$类
        String rootClassName = Consts.NAME_OF_ROOT + moduleName;
        JavaFile.builder(Consts.PACKAGE_OF_GENERATE_FILE,
                TypeSpec.classBuilder(rootClassName)
                        .addSuperinterface(ClassName.get(iRouteRoot))
                        .addModifiers(PUBLIC)
                        .addMethod(loadIntoMethodOfRootBuilder.build())
                        .build()
        ).build().writeTo(filerUtils);

        log.i("Generated RouteRoot: " + Consts.PACKAGE_OF_GENERATE_FILE + "." + rootClassName);
    }

    /**
     * 检查是否配置group 如果没有配置 则从path截取出组名
     *
     * @param routeMeta
     */
    private void categories(RouteMeta routeMeta) {
        if (routerVerify(routeMeta)) {
            //分组和组中的路由信息
            List<RouteMeta> routeMetas = groupMap.get(routeMeta.getGroup());
            if (Utils.isEmpty(routeMetas)) {
                routeMetas = new ArrayList<>();
                routeMetas.add(routeMeta);
                groupMap.put(routeMeta.getGroup(), routeMetas);
            } else {
                routeMetas.add(routeMeta);
            }

        } else {
            log.i("Group info Error==" + routeMeta.getPath());
        }
    }

    /**
     * 验证地址的合法性
     *
     * @param routeMeta
     * @return
     */
    private boolean routerVerify(RouteMeta routeMeta) {
        String path = routeMeta.getPath();
        String group = routeMeta.getGroup();
        //必须以 /开头来指定路由地址
        if (!path.startsWith("/")) {
            return false;
        }
        //如果group没有设置我们从path中获取group
        if (Utils.isEmpty(group)) {
            String defaultGroup = path.substring(1, path.indexOf("/", 1));
            //截取出来的group还是空
            if (Utils.isEmpty(defaultGroup)) {
                return false;
            }
            routeMeta.setGroup(defaultGroup);
        }
        return true;
    }
}
