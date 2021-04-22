package cn.edsmall.router_compiler.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.auto.service.AutoService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import cn.edsmall.router_annotation.destination.ActivityDestination;
import cn.edsmall.router_annotation.destination.FragmentDestination;
import cn.edsmall.router_compiler.utils.Log;
import netscape.javascript.JSObject;

/**
 * 注解处理器 根据注解pageUrl,在assets目录下生成destanation.json文件
 * 处理注解{@link ActivityDestination }
 * 处理注解{@link FragmentDestination }
 */
//
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
//处理哪些注解
@SupportedAnnotationTypes({"cn.edsmall.router_annotation.destination.ActivityDestination", "cn.edsmall.router_annotation.destination.FragmentDestination"})
public class NavProcessor extends AbstractProcessor {
    private Messager messager;
    private Filer filer;
    private static final String OUTPUT_FILE_NAME = "destnation.json";
    private Log log;
    private JSONObject object;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        //获得apt的日志输出
        log = Log.newLog(processingEnv.getMessager());
        log.i("Hi this is NavProcessor init:");
        filer = processingEnv.getFiler();

    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> activityElements = roundEnv.getElementsAnnotatedWith(ActivityDestination.class);
        Set<? extends Element> fragmentElements = roundEnv.getElementsAnnotatedWith(FragmentDestination.class);
        if (!fragmentElements.isEmpty() || !activityElements.isEmpty()) {
            HashMap<String, JSONObject> destMap = new HashMap<>();
            handleDestination(fragmentElements, FragmentDestination.class, destMap);
            handleDestination(activityElements, ActivityDestination.class, destMap);
            //app/src/main/assets
            FileOutputStream fos = null;
            OutputStreamWriter writer = null;
            try {
                FileObject resource = filer.createResource(StandardLocation.CLASS_OUTPUT, "", OUTPUT_FILE_NAME);
                String resourcePath = resource.toUri().getPath();
                log.i("resourcePath:" + resourcePath);
                String appPath = resourcePath.substring(0, resourcePath.indexOf("app") + 4);
                String assetsPath = appPath + "src/main/assets";
                File file = new File(assetsPath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                File outPutFile = new File(file, OUTPUT_FILE_NAME);
                if (outPutFile.exists()) {
                    outPutFile.delete();
                }
                outPutFile.createNewFile();
                log.i("destMap value size:" + destMap.size());
                String content = JSON.toJSONString(destMap);
                log.i("json content:" + content);
                fos = new FileOutputStream(outPutFile);
                writer = new OutputStreamWriter(fos, "UTF-8");
                writer.write(content);
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
                log.i("Exception:" + e.toString());
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        log.i("Exception1:" + e.toString());
                    }
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        log.i("Exception2:" + e.toString());
                    }
                }
            }
        }
        return true;
    }

    private void handleDestination(Set<? extends Element> elements, Class<? extends Annotation> annotationClaz, HashMap<String, JSONObject> destMap) {
        for (Element element : elements) {
            TypeElement typeElement = (TypeElement) element;
            String pageUrl = null;
            String className = typeElement.getQualifiedName().toString();
            int id = Math.abs(className.hashCode());
            boolean needLogin = false;
            boolean asStarter = false;
            boolean isFragment = false;
            Annotation annotation = typeElement.getAnnotation(annotationClaz);
            if (annotation instanceof FragmentDestination) {
                FragmentDestination dest = (FragmentDestination) annotation;
                pageUrl = dest.pageUrl();
                asStarter = dest.asStarter();
                needLogin = dest.needLogin();
                isFragment = true;
//                messager.printMessage(Diagnostic.Kind.ERROR, "pageUrl:" + pageUrl);
            } else if (annotation instanceof ActivityDestination) {
                ActivityDestination dest = (ActivityDestination) annotation;
                pageUrl = dest.pageUrl();
                asStarter = dest.asStarter();
                needLogin = dest.needLogin();
                isFragment = false;
            }
            if (destMap.containsKey(pageUrl)) {
                messager.printMessage(Diagnostic.Kind.ERROR, "不同的页面不允许使用相同的pageUrl");
            } else {
                JSONObject object = new JSONObject();
                object.put("id", id);
                object.put("pageUrl", pageUrl);
                object.put("asStarter", asStarter);
                object.put("needLogin", needLogin);
                object.put("className", className);
                object.put("isFragment", isFragment);
                log.i("------------------------------:");
                destMap.put(pageUrl, object);
            }


        }


    }
}
