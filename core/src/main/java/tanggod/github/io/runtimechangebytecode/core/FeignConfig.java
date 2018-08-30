package tanggod.github.io.runtimechangebytecode.core;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.cloud.openfeign.FeignClient;
import tanggod.github.io.api.Proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/*
 *
 *@author teddy
 *@date 2018/8/30
 */
public class FeignConfig implements RuntimeChangeBytecode {

    public static final List<String> packages = new ArrayList<>();
    public static final List<String> classNames = new ArrayList<>();
    public static final List<String> classFilePaths = new ArrayList<>();

    @Override
    public String createProxy(String basePackage, String resolverSearchPath) throws Exception {
        Set<Class<?>> classes = loaderClassSet(basePackage);
        //过滤后的feign客户端class
        classes = filterAnnotation(Proxy.class, classes);

        ClassPool classPool = ClassPool.getDefault();

        classes.stream().forEach(currentClass -> {
            try {
                packages.add(currentClass.getTypeName().replace("." + currentClass.getSimpleName(), ""));
                classNames.add(getProxyName(currentClass));
                classFilePaths.add(resolverSearchPath + "\\" + getProxyPackageName(currentClass).replace(".", "\\")+".class");

                Proxy annotation = currentClass.getAnnotation(Proxy.class);
                //服务提供者 application.name
                String providerApplicationName = annotation.value();

                //构建一个代理接口
                CtClass proxyInterface = classPool.makeInterface(getProxyPackageName(currentClass));
                //添加api接口
                proxyInterface.addInterface(classPool.get(currentClass.getTypeName()));
                //代理的class
                ClassFile classFile = proxyInterface.getClassFile();
                //constPool
                ConstPool constPool = classFile.getConstPool();

                //构建注解
                AnnotationsAttribute classAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
                Annotation feignClient = new Annotation(FeignClient.class.getTypeName(), constPool);
                feignClient.addMemberValue("value", new StringMemberValue(providerApplicationName, constPool));
                //要改成添加 数组的 注解  不然会被覆盖
                classAttr.setAnnotations(new Annotation[]{feignClient});

                //给类添加上注解
                classFile.addAttribute(classAttr);
                //这一步会修改java的字节码
                try {
                    Class api = proxyInterface.toClass();
                } catch (CannotCompileException e) {
                    System.out.println("target/classes 已加载该class ：" + getProxyPackageName(currentClass));
                }

                //生成到resolverSearchPath
                proxyInterface.writeFile(resolverSearchPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return resolverSearchPath + "\\tanggod\\github\\io\\ribbon\\Proxy$UserApi.class";
    }

    @Override
    public String createChangeProxy(String basePackage, String resolverSearchPath) throws Exception {
        return null;
    }

    public static void main(String[] args) throws Exception {
        FeignConfig feignConfig = new FeignConfig();
        feignConfig.createProxy("tanggod", feignConfig.getResolverSearchPath());

    }
}
