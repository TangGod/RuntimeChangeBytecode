package tanggod.github.io.runtimechangebytecode.core.config;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.conf.HystrixPropertiesManager;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.AnnotationMemberValue;
import javassist.bytecode.annotation.StringMemberValue;
import org.springframework.cloud.openfeign.FeignClient;
import tanggod.github.io.common.annotation.FeignProxy;
import tanggod.github.io.common.annotation.ServerFallbackProxy;
import tanggod.github.io.common.dto.MessageBean;
import tanggod.github.io.common.utils.PropertyUtil;
import tanggod.github.io.runtimechangebytecode.core.RuntimeChangeBytecode;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Set;

/*
 *
 *@author teddy
 *@date 2018/8/31
 */
public class HystrixConfig implements RuntimeChangeBytecode {

    public static String basePackage = PropertyUtil.getProperty("proxy.hystrix.basepackage");

    @Override
    public String createProxy(String basePackage, String resolverSearchPath) throws Exception {
        return null;
    }

    @Override
    public String createChangeProxy(String basePackage, String resolverSearchPath) throws Exception {
        basePackage = this.basePackage;
        Set<Class<?>> classes = loaderClassSet(basePackage);
        //过滤后的feign客户端class
        classes = filterAnnotation(ServerFallbackProxy.class, classes);
        ClassPool classPool = ClassPool.getDefault();

        // TODO
        classPool.makeClass(new FileInputStream(""));

        classes.stream().forEach(currentClass -> {
            try {
                /*FeignProxy annotation = currentClass.getAnnotation(FeignProxy.class);
                //服务提供者 application.name
                String providerApplicationName = annotation.value();*/

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
                try {
                    //Class api = proxyService.toClass();
                } catch (Exception e) {
                    System.out.println("target/classes 已加载该class ：" + getProxyPackageName(currentClass));
                }

                //生成到resolverSearchPath
               // Class<?> aClass = proxyService.toClass();
                proxyService.writeFile(getResolverSearchPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
       loaderClassSet(basePackage);

        return null;
    }
}
