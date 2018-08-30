package tanggod.github.io.runtimechangebytecode.core;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

/*
 *
 *@author teddy
 *@date 2018/8/29
 */
//@Component
public class BytecodeConfig {
    public static final String resolverSearchPath = getResolverSearchPath();

    public static String getResolverSearchPath() {
        try {
            File file = org.springframework.util.ResourceUtils.getFile("classpath:application.properties");
            if (file.isFile()) {
                String resolverSearchPath = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("\\"));
                return resolverSearchPath;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //修改jar包
    public static void installJarClass() throws Exception{
        File file = File.createTempFile("Proxy$UserApi", ".class");
        //临时目录
        String resolverSearchPath = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("\\"));
        System.out.println(resolverSearchPath);
        String filePath = markProxy(getResolverSearchPath());
        //filePath=filePath.substring(0, filePath.lastIndexOf("\\"));
        String javaPath=filePath.substring(0, filePath.lastIndexOf("\\"));
        byte[] data = readStream(new FileInputStream(new File(filePath)));
        writeJarFile("C:\\Users\\uteamtec\\Desktop\\feign.jar","Proxy$UserApi","tanggod\\github\\io\\ribbon",data);
    }

    /**
     * 修改Jar包里的文件或者添加文件
     * @param jarFilePath jar包路径
     * @param entryName 要写的文件名
     * @param data   文件内容
     * @throws Exception
     */
    public static void writeJarFile(String jarFilePath,String entryName,String installPath,byte[] data) throws Exception{

        //1、首先将原Jar包里的所有内容读取到内存里，用TreeMap保存
        JarFile jarFile = new JarFile(jarFilePath);
        //可以保持排列的顺序,所以用TreeMap 而不用HashMap
        TreeMap tm = new TreeMap();
        Enumeration es = jarFile.entries();
        while(es.hasMoreElements()){
            JarEntry je = (JarEntry)es.nextElement();
            byte[] b = readStream(jarFile.getInputStream(je));
            tm.put(je.getName(),b);
        }

        JarOutputStream jos = new JarOutputStream(new FileOutputStream(jarFilePath));
        Iterator it = tm.entrySet().iterator();
        boolean has = false;

        //2、将TreeMap重新写到原jar里，如果TreeMap里已经有entryName文件那么覆盖，否则在最后添加
        while(it.hasNext()){
            Map.Entry item = (Map.Entry) it.next();
            String name = (String)item.getKey();
            JarEntry entry = new JarEntry(name);
            jos.putNextEntry(entry);
            byte[] temp ;
            if(name.equals(entryName)){
                //覆盖
                temp = data;
                has = true ;
            }else{
                temp = (byte[])item.getValue();
            }
            jos.write(temp, 0, temp.length);
        }

        if(!has){
            //最后添加
            JarEntry newEntry = new JarEntry("BOOT-INF\\classes\\"+installPath+"\\"+entryName);
            jos.putNextEntry(newEntry);
            jos.write(data, 0, data.length);
        }
        jos.finish();
        jos.close();

    }

    /**
     * 读取流
     *
     * @param inStream
     * @return 字节数组
     * @throws Exception
     */
    public static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = inStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }
        outSteam.close();
        inStream.close();
        return outSteam.toByteArray();
    }

    public static void main(String[] args) throws Exception {
        BytecodeConfig.installJarClass();
    }


    public static String markProxy(String resolverSearchPath) throws Exception {
        ClassPool classPool = ClassPool.getDefault();
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
        makeClass.writeFile(resolverSearchPath);
        return resolverSearchPath+"\\tanggod\\github\\io\\ribbon\\Proxy$UserApi.class";
    }

    public static void mark_1() throws Exception {
        //Class<?> userApiClass = Class.forName("tanggod.github.io.api.UserApi");
        ClassPool classPool = ClassPool.getDefault();
        //构建一个代理类
        CtClass makeClass = classPool.get("tanggod.github.io.api.UserApi"); //classPool.makeClass("tanggod.github.io.api.proxy$UserApiImpl");
        //test
        // CtField make = CtField.make("int x=1;", makeClass);
        //makeClass.addField(make);
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
        //classAttr.setAnnotation(requestMapping);
        //classAttr.setAnnotation(feignClient);
        //要改成添加 数组的 注解  不然会被覆盖
        classAttr.setAnnotations(new Annotation[]{requestMapping, feignClient});
        //给类添加上注解
        classFile.addAttribute(classAttr);

        //这一步会修改java的字节码
        Class api = makeClass.toClass();
        ClassLoader.getSystemClassLoader().loadClass("tanggod.github.io.api.UserApi");
        System.out.println(api);
        //SpringBeanUtils.getInstance().registerBean("userApi",api);
        //makeClass.writeFile("D:\\ideaProject\\spring-cloud\\demo\\2-声明式服务调用feign\\feign\\feign\\src\\main\\java");
    }

}
