package cn.edsmall.router_compiler.processor;

import com.google.auto.service.AutoService;

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

/**
 * 注解处理器
 * 处理注解{@link Route }
 */
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
@SupportedAnnotationTypes(Consts.ANN_TYPE_ROUTE)
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
                processRout(routeElements);
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
    private void processRout(Set<? extends Element> routeElements) {
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
            //检查是否配置 ground 如果没有配置则从path中截取出组名
            categories(routeMeta);
        }
        //生成$$Group$$  记录分组表

        //生成$$Root$$  记录路由表


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
