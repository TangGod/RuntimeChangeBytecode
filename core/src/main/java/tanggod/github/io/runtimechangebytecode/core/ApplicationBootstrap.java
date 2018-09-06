package tanggod.github.io.runtimechangebytecode.core;

import tanggod.github.io.common.annotation.EnableServerFallbackProxy;
import tanggod.github.io.common.dto.ApplicationBootstrapInitializeException;
import tanggod.github.io.common.utils.ServiceLoaderApi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

/*
 *
 *@author teddy
 *@date 2018/9/3
 */
public interface ApplicationBootstrap {

    static void run(Class<?> primarySource, String... args) {
        RuntimeChangeBytecode.clearTargetClasses();
        ServiceLoader<ApplicationBootstrap> applicationBootstraps = ServiceLoaderApi.loadAll(ApplicationBootstrap.class);
        StreamSupport.stream(applicationBootstraps.spliterator(), false).forEach(applicationBootstrap -> {
            try {
                invoke(applicationBootstrap, "run", primarySource, args);
                if (primarySource.isAnnotationPresent(EnableServerFallbackProxy.class))
                    RuntimeChangeBytecode.checkMethodGenericity(primarySource.getTypeName().substring(0, primarySource.getTypeName().indexOf(".")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    static void invoke(Object entity, String methodName, Object... args) {
        boolean invokeSuccess = false;
        Class<?> entityClass = entity.getClass();
        Method filterMethod = Arrays.stream(entityClass.getMethods()).filter(method -> method.getName().equals(methodName)).findFirst().orElseThrow(ApplicationBootstrapInitializeException::new);
        try {
            filterMethod.invoke(entity, args);
            System.out.println("====================================================================== Application  启动成功 ======================================================================");
            invokeSuccess = true;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        if (!invokeSuccess) {
            System.out.println("====================================================================== Application  初始化失败 ======================================================================");
            System.exit(1);
        }
    }
}
