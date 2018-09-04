package tanggod.github.io.runtimechangebytecode.core.config;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.conf.HystrixPropertiesManager;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.*;
import javassist.bytecode.annotation.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tanggod.github.io.common.annotation.FeignProxy;
import tanggod.github.io.common.annotation.ServerFallbackProxy;
import tanggod.github.io.common.dto.MessageBean;
import tanggod.github.io.common.type.ApplicationCache;
import tanggod.github.io.common.utils.PropertyUtil;
import tanggod.github.io.runtimechangebytecode.core.RuntimeChangeBytecode;

import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/*
 *
 *@author teddy
 *@date 2018/8/31
 */
public class HystrixConfig implements RuntimeChangeBytecode {

    public static String basePackage = PropertyUtil.getProperty("proxy.hystrix.basepackage");

    @Override
    public String createProxy(String basePackage, String resolverSearchPath) throws Exception {
        basePackage = this.basePackage;
        Set<Class<?>> classes = loaderClassSet(basePackage);
        //过滤后的feign客户端class
        classes = filterAnnotation(ServerFallbackProxy.class, classes);
        ClassPool classPool = ClassPool.getDefault();


        classes.stream().forEach(currentClass -> {
            try {
                //构建一个代理接口
                CtClass proxyService = classPool.get(currentClass.getTypeName());
                //代理的class
                ClassFile classFile = proxyService.getClassFile();
                //constPool
                ConstPool constPool = classFile.getConstPool();

                CtMethod[] methods = proxyService.getMethods();
                CtMethod method = Arrays.stream(methods).filter(currentMethod -> currentMethod.getName().equals("get")).findFirst().get();
                MethodInfo methodInfo = method.getMethodInfo();

                //构建注解
                AnnotationsAttribute methodAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
                Annotation hystrixCommand = new Annotation(HystrixCommand.class.getTypeName(), constPool);
                hystrixCommand.addMemberValue("fallbackMethod", new StringMemberValue("fallback", constPool));

                //注解里包含注解
                //默认10秒;如果并发数达到该设置值，请求会被拒绝和抛出异常并且fallback不会被调用。
                String fallbackIsolationSemaphoreMaxConcurrentRequests = HystrixPropertiesManager.FALLBACK_ISOLATION_SEMAPHORE_MAX_CONCURRENT_REQUESTS;
                Annotation hystrixProperty = new Annotation(HystrixProperty.class.getTypeName(), constPool);
                hystrixProperty.addMemberValue("name", new StringMemberValue(fallbackIsolationSemaphoreMaxConcurrentRequests, constPool));
                //并发大小
                hystrixProperty.addMemberValue("value", new StringMemberValue("15", constPool));

                //hystrixCommand.addMemberValue("commandProperties", new AnnotationMemberValue(hystrixProperty, constPool));

                //要改成添加 数组的 注解  不然会被覆盖
                methodAttr.setAnnotations(new Annotation[]{hystrixCommand});

                //给方法添加上注解
                methodInfo.addAttribute(methodAttr);

                CtMethod newMethod = methods[0];


                Class api;
                try {
                    //修改类名
                    proxyService.setName(getProxyPackageName(currentClass));

                    api = proxyService.toClass();
                } catch (Exception e) {
                    System.out.println("target/classes 已加载该class ：" + getProxyPackageName(currentClass));
                }

                //生成到resolverSearchPath
                proxyService.writeFile(getResolverSearchPath());

                ApplicationCache.proxyService.put(currentClass.getTypeName(), getProxyPackageName(currentClass));
                //解冻
              /*  proxyService.defrost();

                CtClass ctClass = classPool.get(getProxyPackageName(currentClass));
                ctClass.setSuperclass(classPool.get(currentClass.getTypeName()));
                ctClass.writeFile(getResolverSearchPath());
                System.out.println("");*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        loaderClassSet(basePackage);

        return null;
    }

    @Override
    public String createChangeProxy(String basePackage, String resolverSearchPath) throws Exception {
        return null;
    }

    /*@Override
    public String createChangeProxy(String basePackage, String resolverSearchPath) throws Exception {
        basePackage = this.basePackage;
        Set<Class<?>> classes = loaderClassSet(basePackage);
        //过滤后的feign客户端class
        classes = filterAnnotation(ServerFallbackProxy.class, classes);
        ClassPool classPool = ClassPool.getDefault();


        classes.stream().forEach(currentClass -> {
            try {
               *//* FeignProxy annotation = currentClass.getAnnotation(FeignProxy.class);
                //服务提供者 application.name
                String providerApplicationName = annotation.value();*//*

                //构建一个代理接口
                //currentClass.getTypeName()
                CtClass proxyService = classPool.get("tanggod.github.io.provider.UserApiService");
                //代理的class
                ClassFile classFile = proxyService.getClassFile();
                //constPool
                ConstPool constPool = classFile.getConstPool();

                CtMethod[] methods = proxyService.getMethods();
                CtMethod method = Arrays.stream(methods).filter(currentMethod->currentMethod.getName().equals("get")).findFirst().get();
                MethodInfo methodInfo = method.getMethodInfo();



                //构建注解
                AnnotationsAttribute methodAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
                Annotation hystrixCommand = new Annotation(HystrixCommand.class.getTypeName(), constPool);
                hystrixCommand.addMemberValue("fallbackMethod", new StringMemberValue("fallback", constPool));

                //注解里包含注解
                //默认10秒;如果并发数达到该设置值，请求会被拒绝和抛出异常并且fallback不会被调用。
                String fallbackIsolationSemaphoreMaxConcurrentRequests = HystrixPropertiesManager.FALLBACK_ISOLATION_SEMAPHORE_MAX_CONCURRENT_REQUESTS;
                Annotation hystrixProperty = new Annotation(HystrixProperty.class.getTypeName(), constPool);
                hystrixProperty.addMemberValue("name", new StringMemberValue(fallbackIsolationSemaphoreMaxConcurrentRequests, constPool));
                //并发大小
                hystrixProperty.addMemberValue("value", new StringMemberValue("15", constPool));

                hystrixCommand.addMemberValue("commandProperties", new AnnotationMemberValue(hystrixProperty, constPool));

                //要改成添加 数组的 注解  不然会被覆盖
                methodAttr.setAnnotations(new Annotation[]{hystrixCommand});

                //给方法添加上注解
                methodInfo.addAttribute(methodAttr);
                //类添加注解
            *//*    AnnotationsAttribute classAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
                Annotation service = new Annotation(Service.class.getTypeName(), constPool);
                classAttr.setAnnotations(new Annotation[]{service});
                classFile.addAttribute(classAttr);*//*

                try {
                    //Class api = proxyService.toClass();
                } catch (Exception e) {
                    System.out.println("target/classes 已加载该class ：" + getProxyPackageName(currentClass));
                }

                proxyService.addMethod(CtMethod.make("public void testx(){}",proxyService));
                //生成到resolverSearchPath
               // Class<?> aClass = proxyService.toClass();
                proxyService.writeFile(getResolverSearchPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        //Set<Class<?>> classes1 = loaderClassSet(basePackage);

        return null;
    }*/



   /* @Override
    public String createChangeProxy(String basePackage, String resolverSearchPath) throws Exception {
        basePackage = this.basePackage;
        Set<Class<?>> classes = loaderClassSet(basePackage);
        //过滤后的feign客户端class
        classes = filterAnnotation(ServerFallbackProxy.class, classes);
        ClassPool classPool = ClassPool.getDefault();


        classes.stream().forEach(currentClass -> {
            try {
                //构建一个代理接口
                //currentClass.getTypeName()
                CtClass makeClass = classPool.makeClass("tanggod.github.io.provider.Proxy$UserApiService");

                CtClass proxyService = classPool.get("tanggod.github.io.provider.UserApiService");
                makeClass.setSuperclass(proxyService);


                CtMethod makeMethod = CtMethod.make("    public tanggod.github.io.common.dto.MessageBean get() {\n" +
                        "        return super.get();\n" +
                        "    }", makeClass);
                makeClass.addMethod(makeMethod);


                //代理的class
                ClassFile classFile = makeClass.getClassFile();
                //constPool
                ConstPool constPool = classFile.getConstPool();

                CtMethod[] methods = makeClass.getMethods();
                CtMethod method = Arrays.stream(methods).filter(currentMethod->currentMethod.getName().equals("get")).findFirst().get();
                MethodInfo methodInfo = method.getMethodInfo();



                //构建注解
                AnnotationsAttribute methodAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
                Annotation hystrixCommand = new Annotation(HystrixCommand.class.getTypeName(), constPool);
                hystrixCommand.addMemberValue("fallbackMethod", new StringMemberValue("fallback", constPool));

                //注解里包含注解
                //默认10秒;如果并发数达到该设置值，请求会被拒绝和抛出异常并且fallback不会被调用。
                String fallbackIsolationSemaphoreMaxConcurrentRequests = HystrixPropertiesManager.FALLBACK_ISOLATION_SEMAPHORE_MAX_CONCURRENT_REQUESTS;
                Annotation hystrixProperty = new Annotation(HystrixProperty.class.getTypeName(), constPool);
                hystrixProperty.addMemberValue("name", new StringMemberValue(fallbackIsolationSemaphoreMaxConcurrentRequests, constPool));
                //并发大小
                hystrixProperty.addMemberValue("value", new StringMemberValue("15", constPool));

                //hystrixCommand.addMemberValue("commandProperties", new AnnotationMemberValue(hystrixProperty, constPool));

                //要改成添加 数组的 注解  不然会被覆盖
                methodAttr.setAnnotations(new Annotation[]{hystrixCommand});

                //给方法添加上注解
                methodInfo.addAttribute(methodAttr);
                //类添加注解
                AnnotationsAttribute classAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
                Annotation service = new Annotation(RestController.class.getTypeName(), constPool);
                classAttr.setAnnotations(new Annotation[]{service});
                classFile.addAttribute(classAttr);

                //test
                AnnotationsAttribute methodAttr2 = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
                Annotation ServerFallbackProxy = new Annotation(ServerFallbackProxy.class.getTypeName(), constPool);
                ServerFallbackProxy.addMemberValue("resultType",new ClassMemberValue(MessageBean.class.getTypeName(),constPool));
                methodAttr2.setAnnotations(new Annotation[]{ServerFallbackProxy,hystrixCommand*//*,hystrixCommand*//*});
                methodInfo.addAttribute(methodAttr2);

                try {
                    //Class api = proxyService.toClass();
                } catch (Exception e) {
                    System.out.println("target/classes 已加载该class ：" + getProxyPackageName(currentClass));
                }

                //生成到resolverSearchPath
                Class<?> aClass = makeClass.toClass();
                makeClass.writeFile(getResolverSearchPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        loaderClassSet(basePackage);

        return null;
    }*/

/*    @Override
    public String createChangeProxy(String basePackage, String resolverSearchPath) throws Exception {
        basePackage = this.basePackage;
        Set<Class<?>> classes = loaderClassSet(basePackage);
        //过滤后的feign客户端class
        classes = filterAnnotation(ServerFallbackProxy.class, classes);
        ClassPool classPool = ClassPool.getDefault();


        classes.stream().forEach(currentClass -> {
            try {
                //创建代理类
                CtClass proxyService = classPool.makeClass(getProxyPackageName(currentClass));
                //获取需要代理的类
                CtClass service = classPool.get(currentClass.getTypeName());
                //代理的class
                ClassFile classFile = proxyService.getClassFile();
                //constPool
                ConstPool constPool = classFile.getConstPool();
                //获取类实现的接口
                CtClass[] interfaces = service.getInterfaces();
                //获取类的父类
                CtClass superclass = service.getSuperclass();
                //获取类上的注解
                //Object[] annotations = service.getAnnotations();
                //获取所有方法
                Method[] methods = currentClass.getDeclaredMethods();

                //获取方法的注解

                //构建class
                proxyService.setInterfaces(interfaces);
                proxyService.setSuperclass(superclass);
                //类添加注解
                copyClassAnnotationsAttribute(service, proxyService);


                //test
                CtClass param1 = classPool.get(String.class.getTypeName());
                //类添加注解
                AnnotationsAttribute param1Attr = new AnnotationsAttribute(param1.getClassFile().getConstPool(), AnnotationsAttribute.visibleTag);
                Annotation param1AttrServiceAnno = new Annotation(RequestBody.class.getTypeName(), param1.getClassFile().getConstPool());
                param1Attr.setAnnotations(new Annotation[]{param1AttrServiceAnno});
                param1.getClassFile().addAttribute(param1Attr);





                CtMethod add = new CtMethod(classPool.get(String.class.getTypeName()), "add", new CtClass[]{classPool.get(RequestBody.class.getTypeName()),param1}, proxyService);
                proxyService.addMethod(add);
                proxyService.writeFile(getResolverSearchPath());
                if (true)
                    System.exit(0);

                //添加方法
                for (int i = 0; i < methods.length; i++) {
                    Method method = methods[i];
                    StringBuilder methodSrc = new StringBuilder();
                    //参数注解
                    java.lang.annotation.Annotation[][] parameterAnnotations = method.getParameterAnnotations();
                    //修饰符
                    String modifier = Modifier.toString(method.getModifiers());
                    //返回值
                    String typeName = method.getReturnType().getTypeName();
                    //方法名
                    String name = method.getName();
                    //参数列表
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    StringBuilder parameterTypeSrc = new StringBuilder();
                    StringBuilder methodInvokeParameter = new StringBuilder();
                    //构建参数列表
                    for (int j = 0; j < parameterTypes.length; j++) {
                        Class<?> parameterType = parameterTypes[j];
                        String paramTypeName = parameterType.getTypeName();
                        //获取参数注解(默认是获取一个)
                        String annotationTypeName="";
                        java.lang.annotation.Annotation[] annotations = parameterAnnotations[j];
                        for (int annotationIndex = 0; annotationIndex < annotations.length; annotationIndex++) {
                            java.lang.annotation.Annotation annotation = annotations[annotationIndex];
                             //annotationTypeName = annotation.annotationType().getTypeName();
                            annotationTypeName="@"+annotation.annotationType().getTypeName();
                        }

                        parameterTypeSrc
                                .append(annotationTypeName)
                                .append(" ")
                                .append(paramTypeName)
                                .append(" ")
                                .append("var" + j);
                        methodInvokeParameter.append("var" + j);
                        if (j != parameterTypes.length - 1) {
                            parameterTypeSrc.append(",");
                            methodInvokeParameter.append(",");
                        }
                    }
                    //构建方法调用
                    StringBuilder methodInvoke = new StringBuilder();
                    methodInvoke.append("super")
                            .append(".")
                            .append(name)
                            .append("(")
                            .append(methodInvokeParameter)
                            .append(")")
                            .append(";");


                    methodSrc.append(modifier)
                            .append(" ")
                            .append(typeName)
                            .append(" ")
                            .append(name)
                            .append(" ")
                            .append("(")
                            .append(parameterTypeSrc)
                            .append(")")
                            .append("{")
                            .append("\n")
                            //.append(methodInvoke)
                            .append("return null;")
                            .append("\n")
                            .append("}")
                            .append("");


                    System.out.println(methodSrc.toString());
                    System.out.println("==============================================");
                    CtMethod makeMethod = CtMethod.make(methodSrc.toString(), proxyService);
                    proxyService.addMethod(makeMethod);
                }

                proxyService.writeFile(getResolverSearchPath());
                if (true)
                    System.exit(0);

                CtMethod makeMethod = CtMethod.make("    public tanggod.github.io.common.dto.MessageBean get() {\n" +
                        "        return super.get();\n" +
                        "    }", proxyService);
                proxyService.addMethod(makeMethod);


                CtMethod[] methodsList = proxyService.getMethods();
                CtMethod method = Arrays.stream(methodsList).filter(currentMethod -> currentMethod.getName().equals("get")).findFirst().get();
                MethodInfo methodInfo = method.getMethodInfo();


                //构建注解
                AnnotationsAttribute methodAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
                Annotation hystrixCommand = new Annotation(HystrixCommand.class.getTypeName(), constPool);
                hystrixCommand.addMemberValue("fallbackMethod", new StringMemberValue("fallback", constPool));

                //注解里包含注解
                //默认10秒;如果并发数达到该设置值，请求会被拒绝和抛出异常并且fallback不会被调用。
                String fallbackIsolationSemaphoreMaxConcurrentRequests = HystrixPropertiesManager.FALLBACK_ISOLATION_SEMAPHORE_MAX_CONCURRENT_REQUESTS;
                Annotation hystrixProperty = new Annotation(HystrixProperty.class.getTypeName(), constPool);
                hystrixProperty.addMemberValue("name", new StringMemberValue(fallbackIsolationSemaphoreMaxConcurrentRequests, constPool));
                //并发大小
                hystrixProperty.addMemberValue("value", new StringMemberValue("15", constPool));

                //hystrixCommand.addMemberValue("commandProperties", new AnnotationMemberValue(hystrixProperty, constPool));

                //要改成添加 数组的 注解  不然会被覆盖
                methodAttr.setAnnotations(new Annotation[]{hystrixCommand});

                //给方法添加上注解
                methodInfo.addAttribute(methodAttr);
                //类添加注解
                AnnotationsAttribute classAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
                Annotation serviceAnno = new Annotation(RestController.class.getTypeName(), constPool);
                classAttr.setAnnotations(new Annotation[]{serviceAnno});
                classFile.addAttribute(classAttr);

                //test
                AnnotationsAttribute methodAttr2 = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
                Annotation ServerFallbackProxy = new Annotation(ServerFallbackProxy.class.getTypeName(), constPool);
                ServerFallbackProxy.addMemberValue("resultType", new ClassMemberValue(MessageBean.class.getTypeName(), constPool));
                methodAttr2.setAnnotations(new Annotation[]{ServerFallbackProxy, hystrixCommand});
                methodInfo.addAttribute(methodAttr2);

                try {
                    //Class api = proxyService.toClass();
                } catch (Exception e) {
                    System.out.println("target/classes 已加载该class ：" + getProxyPackageName(currentClass));
                }

                //生成到resolverSearchPath
                Class<?> aClass = proxyService.toClass();
                proxyService.writeFile(getResolverSearchPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        loaderClassSet(basePackage);

        return null;
    }*/


}
