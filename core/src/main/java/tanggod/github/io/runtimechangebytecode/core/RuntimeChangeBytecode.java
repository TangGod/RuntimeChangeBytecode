package tanggod.github.io.runtimechangebytecode.core;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
 *动态改变字节码
 *@author teddy
 *@date 2018/8/30
 */
public interface RuntimeChangeBytecode {

    String proxyPrefix = "Proxy$";

    /**
     * 生成代理类
     *
     * @param basePackage        扫描的包路径
     * @param resolverSearchPath 代理类生成的文件夹路径(target下的classes)
     * @return
     */
    String createProxy(String basePackage, String resolverSearchPath) throws Exception;

    /**
     * 动态改变当前class(代理类和class同包同名)
     *
     * @param basePackage        扫描的包路径
     * @param resolverSearchPath 代理类生成的文件夹路径(target下的classes)
     * @return
     */
    String createChangeProxy(String basePackage, String resolverSearchPath) throws Exception;

    /**
     * 获取当前模块target下的classes文件夹的路径
     *
     * @return
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
        Set<Class<?>> filterAnnotation = new HashSet<>();
        for (Class data : classes) {
            if (data.isAnnotationPresent(annotation))
                filterAnnotation.add(data);
        }
        return filterAnnotation;
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
        return typePackageName.replace(cla.getSimpleName(), getProxyName(cla));
    }
}
