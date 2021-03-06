package tanggod.github.io.runtimechangebytecode.core;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.annotation.MemberValue;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import tanggod.github.io.common.annotation.Debug;
import tanggod.github.io.common.annotation.ServerFallbackProxy;
import tanggod.github.io.runtimechangebytecode.core.config.SpringMVCConfig;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.ZipOutputStream;

/*
 *动态改变字节码
 *@author teddy
 *@date 2018/8/30
 */
@SuppressWarnings("all")
public interface RuntimeChangeBytecode {

    String proxyPrefix = "Proxy$";
    String proxyPackage = "proxy";

    /**
     * 生成代理类
     *
     * @param basePackage        扫描的包路径
     * @param resolverSearchPath 代理类生成的文件夹路径(target下的classes)
     * @return
     */
    String createProxy(Class<?> primarySource) throws Exception;

    /**
     * 动态改变当前class(代理类和class同包同名)
     *
     * @param basePackage        扫描的包路径
     * @param resolverSearchPath 代理类生成的文件夹路径(target下的classes)
     * @return
     */
    String createChangeProxy(Class<?> primarySource) throws Exception;

    /**
     * 获取要修改的所有class
     *
     * @return
     */
    Set<String> getSacnClasses();


    static void writeFile(RuntimeChangeBytecode... runtimeChangeBytecodes) {
        if (runtimeChangeBytecodes.length == 0)
            return;
        List<RuntimeChangeBytecode> runtimeChangeBytecodeList = new ArrayList<>();
        for (int i = 0; i < runtimeChangeBytecodes.length; i++) {
            if (null != runtimeChangeBytecodes[i])
                runtimeChangeBytecodeList.add(runtimeChangeBytecodes[i]);
        }

        String resolverSearchPath = getResolverSearchPath2();
        ClassPool classPool = ClassPool.getDefault();
        Set<String> sacnClasses = new HashSet<>();
        runtimeChangeBytecodeList.forEach(runtimeChangeBytecode -> {
            Set<String> sacnClassesSet = runtimeChangeBytecode.getSacnClasses();
            if (null != sacnClassesSet)
                sacnClasses.addAll(sacnClassesSet);
        });

        sacnClasses.stream().forEach(classFullyQualifiedName -> {
            try {
                CtClass api = classPool.get(classFullyQualifiedName);
                api.writeFile(resolverSearchPath);
                System.out.println("proxy：" + api.getSimpleName());
            } catch (Exception e) {
                System.out.println("target/classes 已加载该class ：" + classFullyQualifiedName);
            }
        });
    }

    /**
     * 校验方法是否有泛型
     *
     * @param basepackage
     */
    static void checkMethodGenericity(String... basepackage) {
        Set<String> classes = new HashSet<>();
        for (int i = 0; i < basepackage.length; i++) {
            scanClasses2(new File(getResolverSearchPath2()), basepackage[i], classes);
        }
        System.out.println("classes loading success .");
        classes.stream().forEach(classFullyQualifiedName -> {
            try {
                Class<?> api = Class.forName(classFullyQualifiedName);
                if (api.isAnnotationPresent(ServerFallbackProxy.class)) {
                    String message = "";
                    if (api.getAnnotation(ServerFallbackProxy.class).supportGenerics())
                        message = "\t$###### 提示：该方法已设为支持泛型(将会影响整个团队的项目的启动时间...建议移除)";
                    Method[] methods = api.getDeclaredMethods();
                    for (int i = 0; i < methods.length; i++) {
                        Method method = methods[i];
                        Type type = method.getGenericReturnType();
                        if (type instanceof ParameterizedType) {
                            System.out.println("***************************************************************\n" +
                                    "-检测到含有泛型的方法 请修改后再进行提交：\n \t\t> > >" + api.getTypeName() + " \n\t\t\t\t> > > 方法名：" + method.getName().replace(proxyPrefix,"") + message);
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

    }

    /**
     * 获取当前模块target下的classes文件夹的路径
     */
    default String getResolverSearchPath() {
        try {
            File file = org.springframework.util.ResourceUtils.getFile("classpath:application.properties");
            if (file.isFile()) {
                String resolverSearchPath = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("\\"));
                return resolverSearchPath;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    static String getResolverSearchPath2() {
        try {
            File file = org.springframework.util.ResourceUtils.getFile("classpath:application.properties");
            if (file.isFile()) {
                String resolverSearchPath = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("\\"));
                return resolverSearchPath;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * 扫描target目录下指定包的classes
     *
     * @param classesFile 传递target目录下classes的路径 :getResolverSearchPath()
     * @param basePackage 要扫描的包名
     * @param classes     存classes的集合,必须实例化
     */
    default void scanClasses(File classesFile, String basePackage, Collection<String> classes) {
        if (null == classes)
            return;
        // 如果dir对应的文件不存在，则退出
        if (!classesFile.exists())
            return;
        if (classesFile.isDirectory()) {
            for (File file : classesFile.listFiles()) {
                scanClasses(file, basePackage, classes);
            }
        } else if (classesFile.getAbsolutePath().contains(basePackage.replace(".", "\\"))) {
            String classPackage = classesFile.getAbsolutePath().replace(getResolverSearchPath(), "").replace("\\", ".").replace(".class", "");
            classPackage = classPackage.substring(1);
            classes.add(classPackage);
            System.out.println(classPackage);

        }
    }

    static void scanClasses2(File classesFile, String basePackage, Collection<String> classes) {
        if (null == classes)
            return;
        // 如果dir对应的文件不存在，则退出
        if (!classesFile.exists())
            return;
        if (classesFile.isDirectory()) {
            for (File file : classesFile.listFiles()) {
                scanClasses2(file, basePackage, classes);
            }
        } else if (classesFile.getAbsolutePath().contains(basePackage.replace(".", "\\"))) {
            String classPackage = classesFile.getAbsolutePath().replace(getResolverSearchPath2(), "").replace("\\", ".").replace(".class", "");
            classPackage = classPackage.substring(1);
            classes.add(classPackage);
            System.out.println(classPackage);

        }
    }

    static void clearTargetClasses() {
        try {
            File file = org.springframework.util.ResourceUtils.getFile("classpath:application.properties");
            if (file.isFile()) {
                String resolverSearchPath = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("\\"));
                //递归删除
                deleteFile(new File(resolverSearchPath));
                System.out.println("==================================================== 代理类清空完毕 ====================================================");
            }
        } catch (Exception e) {
        }
    }

    static void deleteFile(File classesFile) {
        // 如果dir对应的文件不存在，则退出
        if (!classesFile.exists())
            return;
        if (classesFile.isDirectory()) {
            if (proxyPackage.equals(classesFile.getName())) {
                try {
                    FileUtils.deleteDirectory(classesFile);
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            for (File file : classesFile.listFiles()) {
                deleteFile(file);
            }

        }
    }

    /**
     * 获取当前模块target下的jar包路径
     */
    default String getResolverInstallJarPath() {
        String resolverSearchPath = getResolverSearchPath();
        String target = resolverSearchPath.substring(0, resolverSearchPath.lastIndexOf("\\"));

        File targetFile = new File(target);
        return Arrays.stream(targetFile.listFiles()).filter(file -> file.isFile()).filter(file -> {
            String filterName;
            filterName = file.getName().substring(file.getName().lastIndexOf(".") + 1);
            return "jar".equals(filterName);
        }).findFirst().get().getAbsolutePath();

    }

    /**
     * 把老的class注解 copy到新的class中
     *
     * @param oldCtClass
     * @param currentCtClass
     * @param newAnnotation
     */
    default void copyClassAnnotationsAttribute(CtClass oldCtClass, CtClass currentCtClass, javassist.bytecode.annotation.Annotation... newAnnotation) {
        //原先class的注解信息
        AnnotationsAttribute oldClassAnnotation = (AnnotationsAttribute) oldCtClass.getClassFile().getAttribute(AnnotationsAttribute.visibleTag);
        //构建一个新的 复制
        AnnotationsAttribute currentClassAnnotation = new AnnotationsAttribute(currentCtClass.getClassFile().getConstPool(), AnnotationsAttribute.visibleTag);
        javassist.bytecode.annotation.Annotation[] oldAnnotations;
        if (null != oldClassAnnotation)
            oldAnnotations = oldClassAnnotation.getAnnotations();
        else {
            oldAnnotations = new javassist.bytecode.annotation.Annotation[0];
        }

        javassist.bytecode.annotation.Annotation[] currentAnnotations = new javassist.bytecode.annotation.Annotation[oldAnnotations.length + newAnnotation.length];
        int i = 0;
        for (i = 0; i < oldAnnotations.length; i++) {
            final int index = i;
            Set<String> memberNames = oldAnnotations[index].getMemberNames();
            javassist.bytecode.annotation.Annotation currentAnnotation = new javassist.bytecode.annotation.Annotation(oldAnnotations[index].getTypeName(), currentCtClass.getClassFile().getConstPool());
            if (null != memberNames) {
                memberNames.stream().forEach(keyName -> {
                    MemberValue memberValue = oldAnnotations[index].getMemberValue(keyName);
                    currentAnnotation.addMemberValue(keyName, memberValue);
                });
            }
            currentAnnotations[i] = currentAnnotation;
        }
        //尾部追加新注解
        int newAnnotationIndex = 0;
        for (; i < currentAnnotations.length; i++) {
            currentAnnotations[i] = newAnnotation[newAnnotationIndex++];
        }

        //insert Annotations
        currentClassAnnotation.setAnnotations(currentAnnotations);
        currentCtClass.getClassFile().addAttribute(currentClassAnnotation);
    }

    /**
     * 把老的method注解 copy到新的method中
     *
     * @param oldCtClass
     * @param currentCtClass
     * @param newAnnotation
     */
    default void copyMethodAnnotationsAttribute(CtMethod oldMethod, CtMethod currentCtMethod, javassist.bytecode.annotation.Annotation... newAnnotation) {
        //原先class的注解信息
        AnnotationsAttribute oldClassAnnotation = (AnnotationsAttribute) oldMethod.getMethodInfo().getAttribute(AnnotationsAttribute.visibleTag);
        //构建一个新的 复制
        AnnotationsAttribute currentClassAnnotation = new AnnotationsAttribute(currentCtMethod.getMethodInfo().getConstPool(), AnnotationsAttribute.visibleTag);
        javassist.bytecode.annotation.Annotation[] oldAnnotations;
        if (null != oldClassAnnotation)
            oldAnnotations = oldClassAnnotation.getAnnotations();
        else {
            oldAnnotations = new javassist.bytecode.annotation.Annotation[0];
        }

        javassist.bytecode.annotation.Annotation[] currentAnnotations = new javassist.bytecode.annotation.Annotation[oldAnnotations.length + newAnnotation.length];
        int i = 0;
        for (i = 0; i < oldAnnotations.length; i++) {
            final int index = i;
            Set<String> memberNames = oldAnnotations[index].getMemberNames();
            javassist.bytecode.annotation.Annotation currentAnnotation = new javassist.bytecode.annotation.Annotation(oldAnnotations[index].getTypeName(), currentCtMethod.getMethodInfo().getConstPool());
            if (null != memberNames) {
                memberNames.stream().forEach(keyName -> {
                    MemberValue memberValue = oldAnnotations[index].getMemberValue(keyName);
                    currentAnnotation.addMemberValue(keyName, memberValue);
                });
            }
            currentAnnotations[i] = currentAnnotation;
        }
        //尾部追加新注解
        int newAnnotationIndex = 0;
        for (; i < currentAnnotations.length; i++) {
            currentAnnotations[i] = newAnnotation[newAnnotationIndex++];
        }

        //insert Annotations
        currentClassAnnotation.setAnnotations(currentAnnotations);
        currentCtMethod.getMethodInfo().addAttribute(currentClassAnnotation);
    }

    /**
     * 获取类加载器
     *
     * @param @return 设定文件
     * @return ClassLoader    返回类型
     * @throws
     * @Title: getClassLoader
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    default ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 加载类
     * 需要提供类名与是否初始化的标志，
     * 初始化是指是否执行静态代码块
     *
     * @param @param  className
     * @param @param  isInitialized  为提高性能设置为false
     * @param @return 设定文件
     * @return Class<?>    返回类型
     * @throws
     * @Title: loadClass
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    default Class<?> loadClass(String className, boolean isInitialized) {

        Class<?> cls;
        try {
            cls = Class.forName(className, isInitialized, getClassLoader());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("加载类失败");
            throw new RuntimeException(e);
        }
        return cls;
    }

    /**
     * 加载指定包下的所有类
     *
     * @param basePackage
     * @return
     */
    default Set<Class<?>> loaderClassSet(String... basePackage) {
        Set<Class<?>> classSet = new HashSet<>();

        try {
            Arrays.stream(basePackage).forEach(currentPackage -> {
                try {
                    Enumeration<URL> urls = getClassLoader().getResources(currentPackage.replace(".", "/"));

                    while (urls.hasMoreElements()) {
                        URL url = urls.nextElement();
                        if (url != null) {
                            String protocol = url.getProtocol();
                            if (protocol.equals("file")) {
                                // 转码
                                String packagePath = URLDecoder.decode(url.getFile(), "UTF-8");
                                // String packagePath =url.getPath().replaceAll("%20",
                                // "");
                                // 添加
                                addClass(classSet, packagePath, currentPackage);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return classSet;
    }


    /**
     * 添加文件到SET集合
     *
     * @param @param classSet
     * @param @param packagePath
     * @param @param packageName    设定文件
     * @return void    返回类型
     * @throws
     * @Title: addClass
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    default void addClass(Set<Class<?>> classSet, String packagePath, String packageName) {

        File[] files = new File(packagePath).listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {
                return (file.isFile() && file.getName().endsWith(".class") || file.isDirectory());
            }
        });

        for (File file : files) {

            String fileName = file.getName();

            if (file.isFile()) {
                String className = fileName.substring(0, fileName.lastIndexOf("."));

                if (StringUtils.isNotEmpty(packageName)) {

                    className = packageName + "." + className;
                    System.out.println("className：" + className);
                }
                // 添加
                doAddClass(classSet, className);
            } else {
                // 子目录
                String subPackagePath = fileName;
                if (StringUtils.isNotEmpty(packagePath)) {
                    subPackagePath = packagePath + "/" + subPackagePath;
                }

                String subPackageName = fileName;
                if (StringUtils.isNotEmpty(packageName)) {
                    subPackageName = packageName + "." + subPackageName;
                }

                addClass(classSet, subPackagePath, subPackageName);
            }
        }
    }

    /**
     * class加载之后 添加到集合
     *
     * @param classSet
     * @param className
     */
    default void doAddClass(Set<Class<?>> classSet, String className) {
        Class<?> cls = loadClass(className, false);
        classSet.add(cls);
    }

    /**
     * 过滤掉不包含注解的class
     *
     * @param annotation
     * @param classes
     * @return
     */
    default Set<Class<?>> filterAnnotation(Class annotation, Set<Class<?>> classes) {
        classes = filterDebug(classes);
        Set<Class<?>> filterAnnotation = new HashSet<>();
        for (Class data : classes) {
            if (data.isAnnotationPresent(annotation))
                filterAnnotation.add(data);
        }
        return filterAnnotation;
    }

    /**
     * 过滤掉不包含注解的class
     *
     * @param annotation
     * @param classes
     * @param classPool
     * @return
     */
    default Collection<String> filterAnnotation(Class annotation, Collection<String> classes, ClassPool classPool) {
        Set<String> filterAnnotation = new HashSet<>();
        classes.stream().forEach(classFullyQualifiedName -> {
            try {
                if (classPool.get(classFullyQualifiedName).hasAnnotation(annotation))
                    filterAnnotation.add(classFullyQualifiedName);
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
        });
        return filterAnnotation;
    }

    default Set<Class<?>> filterDebug(Set<Class<?>> classes) {
        Set<Class<?>> filterDebug = new HashSet<>();
        for (Class data : classes) {
            if (!data.isAnnotationPresent(Debug.class))
                filterDebug.add(data);
        }
        return filterDebug;
    }

    /**
     * 过滤并获取代理类
     *
     * @param classes
     * @return
     */
    default Set<Class<?>> filterProxy(Set<Class<?>> classes) {
        Set<Class<?>> filterProxy = new HashSet<>();
        for (Class data : classes) {
            if (data.getName().contains(proxyPrefix))
                filterProxy.add(data);
        }
        return filterProxy;
    }

    /**
     * 获取代理类名
     *
     * @param cla
     * @return
     */
    default String getProxyName(Class cla) {
        return proxyPrefix + cla.getSimpleName();
    }

    /**
     * 获取代理类全限定名(包名+类名)
     *
     * @param cla
     * @return
     */
    default String getProxyPackageName(Class cla) {
        String typePackageName = cla.getTypeName();
        return typePackageName.replace(cla.getSimpleName(), proxyPackage + "." + getProxyName(cla));
    }

    /**
     * 读取流
     *
     * @param inStream
     * @return 字节数组
     * @throws Exception
     */
    default byte[] readStream(InputStream inStream) throws Exception {
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

    /**
     * 修改Jar包里的文件或者添加文件
     *
     * @param jarFilePath jar包路径
     * @param entryName   要写的文件名
     * @param data        文件内容
     * @throws Exception
     */
    default void writeJarFile(String jarFilePath, String entryName, String installPath, byte[] data) throws Exception {

        //1、首先将原Jar包里的所有内容读取到内存里，用TreeMap保存
        JarFile jarFile = new JarFile(jarFilePath);
        //可以保持排列的顺序,所以用TreeMap 而不用HashMap
        TreeMap tm = new TreeMap();
        Enumeration es = jarFile.entries();
        while (es.hasMoreElements()) {
            JarEntry je = (JarEntry) es.nextElement();
            byte[] b = readStream(jarFile.getInputStream(je));
            tm.put(je.getName(), b);
        }

        JarOutputStream jos = new JarOutputStream(new FileOutputStream(jarFilePath));
        Iterator it = tm.entrySet().iterator();
        boolean has = false;

        //2、将TreeMap重新写到原jar里，如果TreeMap里已经有entryName文件那么覆盖，否则在最后添加
        while (it.hasNext()) {
            Map.Entry item = (Map.Entry) it.next();
            String name = (String) item.getKey();
            JarEntry entry = new JarEntry(name);
            jos.putNextEntry(entry);
            byte[] temp;
            if (name.equals(entryName)) {
                //覆盖
                temp = data;
                has = true;
            } else {
                temp = (byte[]) item.getValue();
            }
            jos.write(temp, 0, temp.length);
        }

        if (!has) {
            //最后添加
            JarEntry newEntry = new JarEntry("BOOT-INF\\classes\\" + installPath.replace(".", "\\") + "\\" + entryName);
            jos.putNextEntry(newEntry);
            jos.write(data, 0, data.length);
        }
        jos.finish();
        jos.close();

    }


}
