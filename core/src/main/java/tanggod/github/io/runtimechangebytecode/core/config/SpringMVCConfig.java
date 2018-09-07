package tanggod.github.io.runtimechangebytecode.core.config;

import javassist.*;
import javassist.bytecode.*;
import javassist.bytecode.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tanggod.github.io.common.annotation.enable.EnableSpringMVCProxy;
import tanggod.github.io.runtimechangebytecode.core.RuntimeChangeBytecode;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/*
 *
 *@author teddy
 *@date 2018/9/3
 */
public class SpringMVCConfig implements RuntimeChangeBytecode {

    //public static String baseControllerPackage = PropertyUtil.getProperty("proxy.controller.basepackage");
    //public static String baseServicePackage= PropertyUtil.getProperty("proxy.service.basepackage");

    private static final RequestMethod[] REQUEST_METHODS = new RequestMethod[]{RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE};

    @Override
    public String createProxy(Class<?> primarySource) throws Exception {
        Set<Class<?>> classes = new HashSet<>();
        ClassPool classPool = ClassPool.getDefault();

        EnableSpringMVCProxy enableSpringMVCProxy = primarySource.getAnnotation(EnableSpringMVCProxy.class);
        String[] scanRequestMappingBasePackages = enableSpringMVCProxy.scanRequestMappingBasePackages();

        for (int i = 0; i < scanRequestMappingBasePackages.length; i++) {
            classes.addAll(loaderClassSet(scanRequestMappingBasePackages[i]));
        }
        //service包下的class
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

                //method
                CtMethod[] methods = proxyService.getDeclaredMethods();
                for (int i = 0; i < methods.length; i++) {
                    CtMethod method = methods[i];
                    String methodName = method.getName();
                    RequestMethod requestMethod = getRequestMethod(methodName);
                    if (null == requestMethod)
                        continue;

                    String requestMappingValue = getRequestMapping(requestMethod, methodName);
                    //requestmapping
                    Annotation requestMapping = new Annotation(RequestMapping.class.getTypeName(), constPool);
                    ArrayMemberValue valueAmv = new ArrayMemberValue(constPool);
                    valueAmv.setValue(new StringMemberValue[]{new StringMemberValue(requestMappingValue, constPool)});
                    requestMapping.addMemberValue("value", valueAmv);

                    ArrayMemberValue methodAmv = new ArrayMemberValue(constPool);
                    EnumMemberValue enumMemberValue = new EnumMemberValue(constPool);
                    enumMemberValue.setType(RequestMethod.class.getTypeName());
                    enumMemberValue.setValue(requestMethod.name());
                    methodAmv.setValue(new EnumMemberValue[]{enumMemberValue});
                    requestMapping.addMemberValue("method", methodAmv);
                    copyMethodAnnotationsAttribute(method, method, requestMapping);
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

        return null;
    }

    @Override
    public String createChangeProxy(Class<?> primarySource) throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        Set<String> classes = new HashSet<>();

        EnableSpringMVCProxy enableSpringMVCProxy = primarySource.getAnnotation(EnableSpringMVCProxy.class);
        String[] scanRequestMappingBasePackages = enableSpringMVCProxy.scanRequestMappingBasePackages();

        for (int i = 0; i < scanRequestMappingBasePackages.length; i++) {
            scanClasses(new File(getResolverSearchPath()), scanRequestMappingBasePackages[i], classes);
        }

        getSacnClasses.addAll(classes);

        classes.stream().forEach(classFullyQualifiedName -> {
            try {
                CtClass currentCtClass = classPool.get(classFullyQualifiedName);
                //当前class
                ClassFile classFile = currentCtClass.getClassFile();
                //constPool
                ConstPool constPool = classFile.getConstPool();

                //method
                CtMethod[] methods = currentCtClass.getDeclaredMethods();
                for (int i = 0; i < methods.length; i++) {
                    CtMethod method = methods[i];
                    if (method.hasAnnotation(RequestMapping.class))
                        continue;

                    String methodName = method.getName();
                    RequestMethod requestMethod = getRequestMethod(methodName);
                    if (null == requestMethod)
                        continue;

                    String requestMappingValue = getRequestMapping(requestMethod, methodName);
                    //requestmapping
                    Annotation requestMapping = new Annotation(RequestMapping.class.getTypeName(), constPool);
                    ArrayMemberValue valueAmv = new ArrayMemberValue(constPool);
                    valueAmv.setValue(new StringMemberValue[]{new StringMemberValue(requestMappingValue, constPool)});
                    requestMapping.addMemberValue("value", valueAmv);

                    ArrayMemberValue methodAmv = new ArrayMemberValue(constPool);
                    EnumMemberValue enumMemberValue = new EnumMemberValue(constPool);
                    enumMemberValue.setType(RequestMethod.class.getTypeName());
                    enumMemberValue.setValue(requestMethod.name());
                    methodAmv.setValue(new EnumMemberValue[]{enumMemberValue});
                    requestMapping.addMemberValue("method", methodAmv);
                    copyMethodAnnotationsAttribute(method, method, requestMapping);
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
