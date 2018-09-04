package tanggod.github.io.runtimechangebytecode.core.config;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.conf.HystrixPropertiesManager;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.bytecode.*;
import javassist.bytecode.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tanggod.github.io.common.annotation.ServerFallbackProxy;
import tanggod.github.io.common.dto.MessageBean;
import tanggod.github.io.common.utils.PropertyUtil;
import tanggod.github.io.runtimechangebytecode.core.RuntimeChangeBytecode;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;

/*
 *
 *@author teddy
 *@date 2018/9/3
 */
public class SpringMVCConfig implements RuntimeChangeBytecode {

    public static String baseControllerPackage = PropertyUtil.getProperty("proxy.controller.basepackage");
    public static String baseServicePackage = PropertyUtil.getProperty("proxy.service.basepackage");


    @Override
    public String createProxy(String basePackage, String resolverSearchPath) throws Exception {
        ClassPool classPool = ClassPool.getDefault();

        //service包下的class
        Set<Class<?>> classes = loaderClassSet(baseServicePackage);
        classes = filterDebug(classes);
        //获取代理类
        Set<Class<?>> proxyClasses;
        proxyClasses = filterProxy(classes);
        //有代理类的话,就只给代理类的生成
        if (proxyClasses.size() > 0)
            classes = proxyClasses;


        classes.stream().forEach(currentClass -> {
            try {
                String proxyPackageClassName = getProxyPackageName(currentClass) + "_mvc";
                String proxyClassName = currentClass.getSimpleName() + "_mvc";
                CtClass proxyService = classPool.get(currentClass.getTypeName());
                proxyService.defrost();
                //代理的class
                ClassFile classFile = proxyService.getClassFile();
                //constPool
                ConstPool constPool = classFile.getConstPool();

                //类添加注解
                Annotation service = new Annotation(Service.class.getTypeName(), constPool);
                service.addMemberValue("value", new StringMemberValue(proxyClassName, constPool));

                copyClassAnnotationsAttribute(proxyService, proxyService, service);


                //属性添加注解
                Annotation autowired = new Annotation(Autowired.class.getTypeName(), constPool);
                autowired.addMemberValue("required", new BooleanMemberValue(false, constPool));

                CtField[] declaredFields = proxyService.getDeclaredFields();
                for (int i = 0; i < declaredFields.length; i++) {
                    AnnotationsAttribute fieldAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
                    CtField declaredField = declaredFields[i];
                    FieldInfo fieldInfo = declaredField.getFieldInfo();
                    fieldAttr.addAnnotation(autowired);
                    fieldInfo.addAttribute(fieldAttr);
                }

                try {
                    proxyService.setName(proxyPackageClassName);
                    Class api = proxyService.toClass();
                } catch (Exception e) {
                    System.out.println("target/classes 已加载该class ：" + getProxyPackageName(currentClass));
                }

                //生成到resolverSearchPath
                proxyService.writeFile(getResolverSearchPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //controller包下的class
        classes = loaderClassSet(baseControllerPackage);
        classes = filterDebug(classes);
        //获取代理类
        proxyClasses = filterProxy(classes);
        //有代理类的话,就只给代理类的生成
        if (proxyClasses.size() > 0)
            classes = proxyClasses;
    /*    if (true)
            return null;*/
        //controller
        classes.stream().forEach(currentClass -> {
            try {
                String proxyPackageClassName = getProxyPackageName(currentClass) + "_mvc";
                String proxyClassName = currentClass.getSimpleName() + "_mvc";
                CtClass proxyService = classPool.get(currentClass.getTypeName());
                proxyService.defrost();
                //代理的class
                ClassFile classFile = proxyService.getClassFile();
                //constPool
                ConstPool constPool = classFile.getConstPool();

                //类添加注解
                Annotation controller = new Annotation(RestController.class.getTypeName(), constPool);
                controller.addMemberValue("value", new StringMemberValue(proxyClassName, constPool));

                copyClassAnnotationsAttribute(proxyService, proxyService, controller);

                //属性添加注解
                Annotation autowired = new Annotation(Autowired.class.getTypeName(), constPool);
                autowired.addMemberValue("required", new BooleanMemberValue(false, constPool));

                CtField[] declaredFields = proxyService.getDeclaredFields();
                for (int i = 0; i < declaredFields.length; i++) {
                    AnnotationsAttribute fieldAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
                    CtField declaredField = declaredFields[i];
                    FieldInfo fieldInfo = declaredField.getFieldInfo();
                    fieldAttr.addAnnotation(autowired);
                    fieldInfo.addAttribute(fieldAttr);
                }

                //requestmapping
                /*Annotation requestMapping = new Annotation(RequestMapping.class.getTypeName(), constPool);
                ArrayMemberValue amv = new ArrayMemberValue(constPool);
                amv.setValue(new StringMemberValue[]{new StringMemberValue("x", constPool)});
                requestMapping.addMemberValue("value", amv);
                methodAttr2.setAnnotations(new Annotation[]{requestMapping});
                methodInfo.addAttribute(methodAttr2);*/


                try {
                    proxyService.setName(proxyPackageClassName);
                    Class api = proxyService.toClass();
                } catch (Exception e) {
                    System.out.println("target/classes 已加载该class ：" + getProxyPackageName(currentClass));
                }

                //生成到resolverSearchPath
                proxyService.writeFile(getResolverSearchPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return null;
    }

    @Override
    public String createChangeProxy(String basePackage, String resolverSearchPath) throws Exception {
        return null;
    }
}
