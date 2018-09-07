package tanggod.github.io.runtimechangebytecode.core.config;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;
import org.springframework.cloud.openfeign.FeignClient;
import tanggod.github.io.common.annotation.enable.EnableFeignClientProxy;
import tanggod.github.io.common.annotation.FeignProxy;
import tanggod.github.io.runtimechangebytecode.core.RuntimeChangeBytecode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 *
 *@author teddy
 *@date 2018/8/30
 */
@SuppressWarnings("all")
public class FeignConfig implements RuntimeChangeBytecode {

    public static final List<String> packages = new ArrayList<>();
    public static final List<String> classNames = new ArrayList<>();
    public static final List<String> classFilePaths = new ArrayList<>();

    //public static String basePackage = PropertyUtil.getProperty("proxy.feign.basepackage");

    @Override
    public String createProxy(Class<?> primarySource) throws Exception {
        Set<Class<?>> classes = new HashSet<>();
        EnableFeignClientProxy enableFeignClientProxy = primarySource.getAnnotation(EnableFeignClientProxy.class);
        String[] scanBasePackages = enableFeignClientProxy.scanBasePackages();

        for (int i = 0; i < scanBasePackages.length; i++) {
            classes.addAll(loaderClassSet(scanBasePackages[i]));
        }


        //过滤后的feign客户端class
        classes = filterAnnotation(FeignProxy.class, classes);

        ClassPool classPool = ClassPool.getDefault();

        classes.stream().forEach(currentClass -> {
            try {
                packages.add(currentClass.getTypeName().replace("." + currentClass.getSimpleName(), ""));
                classNames.add(getProxyName(currentClass));
                classFilePaths.add(getResolverSearchPath() + "\\" + getProxyPackageName(currentClass).replace(".", "\\") + ".class");

                FeignProxy annotation = currentClass.getAnnotation(FeignProxy.class);
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
              /*  try {
                    Class api = proxyInterface.toClass();
                } catch (CannotCompileException e) {
                    System.out.println("target/classes 已加载该class ：" + getProxyPackageName(currentClass));
                }*/

                //生成到resolverSearchPath
                proxyInterface.writeFile(getResolverSearchPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return null;
    }

    @Override
    public String createChangeProxy(Class<?> primarySource) throws Exception {
        return null;
    }

    private static final Set<String> getSacnClasses = new HashSet<>();

    @Override
    public Set<String> getSacnClasses() {
        return this.getSacnClasses;
    }
}
