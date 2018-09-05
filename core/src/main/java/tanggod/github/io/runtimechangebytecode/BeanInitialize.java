package tanggod.github.io.runtimechangebytecode;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import tanggod.github.io.common.type.ApplicationCache;

import java.lang.reflect.Field;

/*
 *
 *@author teddy
 *@date 2018/8/28
 */
//@Component
@Deprecated
public class BeanInitialize implements BeanPostProcessor {

    private boolean x = true;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println(":"+bean.getClass().getTypeName());

        if (ApplicationCache.proxyService.containsValue(bean.getClass().getTypeName())){
            ApplicationCache.invoker.put(bean.getClass().getTypeName(),bean);
        }

        //search
        Field[] declaredFields = bean.getClass().getDeclaredFields();
        for (int i=0;i<declaredFields.length;i++){
            Field declaredField = declaredFields[i];
            String type = ApplicationCache.proxyService.get(declaredField.getType().getTypeName());
            if (StringUtils.isNotBlank(type)){
                Object o = ApplicationCache.invoker.get(type);
            }
        }


        return bean;
    }
}
