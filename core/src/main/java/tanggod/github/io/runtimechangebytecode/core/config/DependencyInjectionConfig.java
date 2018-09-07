package tanggod.github.io.runtimechangebytecode.core.config;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tanggod.github.io.common.annotation.enable.EnableDependencyInjection;
import tanggod.github.io.common.annotation.enable.EnableSpringMVCProxy;
import tanggod.github.io.runtimechangebytecode.core.RuntimeChangeBytecode;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/*
 *
 *@author teddy
 *@date 2018/9/7
 */
public class DependencyInjectionConfig implements RuntimeChangeBytecode {


    private static final RequestMethod[] REQUEST_METHODS = new RequestMethod[]{RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE};

    @Override
    public String createProxy(Class<?> primarySource) throws Exception {
        return null;
    }

    @Override
    public String createChangeProxy(Class<?> primarySource) throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        Set<String> classes = new HashSet<>();

        EnableDependencyInjection enableDependencyInjection = primarySource.getAnnotation(EnableDependencyInjection.class);
        String[] scanServiceBasePackages = enableDependencyInjection.scanServiceBasePackages();
        String[] scanRestControllerBasePackages = enableDependencyInjection.scanRestControllerBasePackages();
        String[] scanControllerBasePackages = enableDependencyInjection.scanControllerBasePackages();

        //-1 ：service    class:@Service   field:@Autowired
        for (int i = 0; i < scanServiceBasePackages.length; i++) {
            scanClasses(new File(getResolverSearchPath()), scanServiceBasePackages[i], classes);
        }
        getSacnClasses.addAll(classes);

        classes.stream().forEach(classFullyQualifiedName -> {
            try {
                CtClass currentCtClass = classPool.get(classFullyQualifiedName);

                //当前class
                ClassFile classFile = currentCtClass.getClassFile();
                //constPool
                ConstPool constPool = classFile.getConstPool();

                //类添加注解
                if (!currentCtClass.hasAnnotation(Service.class)) {
                    Annotation service = new Annotation(Service.class.getTypeName(), constPool);
                    //service.addMemberValue("value", new StringMemberValue(currentCtClass.getSimpleName(), constPool));
                    copyClassAnnotationsAttribute(currentCtClass, currentCtClass, service);
                }

                //属性添加注解
                Annotation autowired = new Annotation(Autowired.class.getTypeName(), constPool);
                autowired.addMemberValue("required", new BooleanMemberValue(false, constPool));

                CtField[] declaredFields = currentCtClass.getDeclaredFields();
                for (int i = 0; i < declaredFields.length; i++) {
                    CtField declaredField = declaredFields[i];
                    if (declaredField.hasAnnotation(Autowired.class))
                        continue;
                    AnnotationsAttribute fieldAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
                    FieldInfo fieldInfo = declaredField.getFieldInfo();
                    fieldAttr.addAnnotation(autowired);
                    fieldInfo.addAttribute(fieldAttr);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //-2 ：controller    class:@RestController   field:@Autowired   method: @RequestMapping

        classes.clear();
        for (int i = 0; i < scanRestControllerBasePackages.length; i++) {
            scanClasses(new File(getResolverSearchPath()), scanRestControllerBasePackages[i], classes);
        }

        getSacnClasses.addAll(classes);

        classes.stream().forEach(classFullyQualifiedName -> {
            try {
                CtClass currentCtClass = classPool.get(classFullyQualifiedName);
                //当前class
                ClassFile classFile = currentCtClass.getClassFile();
                //constPool
                ConstPool constPool = classFile.getConstPool();

                //类添加注解
                if (!currentCtClass.hasAnnotation(RestController.class)) {
                    Annotation controller = new Annotation(RestController.class.getTypeName(), constPool);
                    //controller.addMemberValue("value", new StringMemberValue(currentCtClass.getSimpleName(), constPool));
                    copyClassAnnotationsAttribute(currentCtClass, currentCtClass, controller);
                }

                //属性添加注解
                Annotation autowired = new Annotation(Autowired.class.getTypeName(), constPool);
                autowired.addMemberValue("required", new BooleanMemberValue(false, constPool));

                CtField[] declaredFields = currentCtClass.getDeclaredFields();
                for (int i = 0; i < declaredFields.length; i++) {
                    CtField declaredField = declaredFields[i];
                    if (declaredField.hasAnnotation(Autowired.class))
                        continue;
                    AnnotationsAttribute fieldAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
                    FieldInfo fieldInfo = declaredField.getFieldInfo();
                    fieldAttr.addAnnotation(autowired);
                    fieldInfo.addAttribute(fieldAttr);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        classes.clear();
        for (int i = 0; i < scanControllerBasePackages.length; i++) {
            scanClasses(new File(getResolverSearchPath()), scanControllerBasePackages[i], classes);
        }

        getSacnClasses.addAll(classes);

        classes.stream().forEach(classFullyQualifiedName -> {
            try {
                CtClass currentCtClass = classPool.get(classFullyQualifiedName);
                //当前class
                ClassFile classFile = currentCtClass.getClassFile();
                //constPool
                ConstPool constPool = classFile.getConstPool();

                //类添加注解
                if (!currentCtClass.hasAnnotation(Controller.class)) {
                    Annotation controller = new Annotation(Controller.class.getTypeName(), constPool);
                    //controller.addMemberValue("value", new StringMemberValue(currentCtClass.getSimpleName(), constPool));
                    copyClassAnnotationsAttribute(currentCtClass, currentCtClass, controller);
                }

                //属性添加注解
                Annotation autowired = new Annotation(Autowired.class.getTypeName(), constPool);
                autowired.addMemberValue("required", new BooleanMemberValue(false, constPool));

                CtField[] declaredFields = currentCtClass.getDeclaredFields();
                for (int i = 0; i < declaredFields.length; i++) {
                    CtField declaredField = declaredFields[i];
                    if (declaredField.hasAnnotation(Autowired.class))
                        continue;
                    AnnotationsAttribute fieldAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
                    FieldInfo fieldInfo = declaredField.getFieldInfo();
                    fieldAttr.addAnnotation(autowired);
                    fieldInfo.addAttribute(fieldAttr);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        });


        return null;
    }

    private RequestMethod getRequestMethod(String methodName) {
        RequestMethod requestMethod = null;
        if (StringUtils.isBlank(methodName))
            return requestMethod;

        methodName = methodName.toUpperCase();

        for (int i = 0; i < REQUEST_METHODS.length; i++) {
            if (methodName.startsWith(REQUEST_METHODS[i].name()))
                return REQUEST_METHODS[i];
        }
        return requestMethod;
    }

    private String getRequestMapping(RequestMethod requestMethod, String methodName) {
        String method = methodName.replaceFirst(requestMethod.name().toLowerCase(), "");
        if (StringUtils.isBlank(method))
            return "";
        String oldInitial = method.substring(0, 1);
        String newInitial = oldInitial.toLowerCase();
        return method.replaceFirst(oldInitial, newInitial);
    }


    private static final Set<String> getSacnClasses = new HashSet<>();

    @Override
    public Set<String> getSacnClasses() {
        return this.getSacnClasses;
    }
}
