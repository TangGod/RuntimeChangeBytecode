package tanggod.github.io.runtimechangebytecode.core;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;
import tanggod.github.io.api.Proxy;

import java.util.Set;

/*
 *
 *@author teddy
 *@date 2018/8/30
 */
public class FeignConfig implements RuntimeChangeBytecode {

    @Override
    public String createProxy(String basePackage, String resolverSearchPath) throws Exception {
        Set<Class<?>> classes = loaderClassSet(basePackage);
        //过滤后的feign客户端class
        classes = filterAnnotation(Proxy.class, classes);

        ClassPool classPool = ClassPool.getDefault();

        classes.stream().forEach(currentClass -> {
            //代理类名
            System.out.println("====================================");
            System.out.println(getProxyName(currentClass));
            System.out.println(getProxyPackageName(currentClass));

        });

       /* ClassPool classPool = ClassPool.getDefault();
        //构建一个代理类
        CtClass makeClass = classPool.makeInterface("tanggod.github.io.ribbon.Proxy$UserApi");
        makeClass.addInterface(classPool.get("tanggod.github.io.api.UserApi"));
        //代理的class
        ClassFile classFile = makeClass.getClassFile();
        //constPool
        ConstPool constPool = classFile.getConstPool();
        //注解
        AnnotationsAttribute classAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation feignClient = new Annotation("org.springframework.cloud.openfeign.FeignClient", constPool);
        Annotation requestMapping = new Annotation("org.springframework.web.bind.annotation.RequestMapping", constPool);
        feignClient.addMemberValue("value", new StringMemberValue("user-provider", constPool));
        requestMapping.addMemberValue("value", new StringMemberValue("/user", constPool));
        //要改成添加 数组的 注解  不然会被覆盖
        classAttr.setAnnotations(new Annotation[]{feignClient});
        //给类添加上注解
        classFile.addAttribute(classAttr);

        //这一步会修改java的字节码
        Class api = makeClass.toClass();

        Class<?> aClass = ClassLoader.getSystemClassLoader().loadClass("tanggod.github.io.ribbon.Proxy$UserApi");
        //Class<?> aClass = Class.forName("tanggod.github.io.ribbon.Proxy$UserApi");
        System.out.println(api);
        //SpringBeanUtils.getInstance().registerBean("userApi",api);
        makeClass.writeFile(resolverSearchPath);*/
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
