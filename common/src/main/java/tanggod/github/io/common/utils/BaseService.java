package tanggod.github.io.common.utils;


import tanggod.github.io.common.dto.BaseBean;
import tanggod.github.io.common.dto.MessageBean;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

/*
 *服务接口父类
 *@author teddy
 *@date 2018/1/30
 */
public class BaseService {
    private static final String successKey = "success";
    private static final String messageKey = "message";
    private static final String metaKey = "meta";
    private static final String dataKey = "data";

    public BaseService() {
        //loadBean();
    }

    private static final Map<Class, Class> beanMap = new HashMap<Class, Class>();

    @SuppressWarnings("all")
    //接口返回时,把调用者不需要的 "单个实体" 字段剔除(设为null)
    protected <T> T clearResultNoNeedEntityData(T entity, String... field) {
        T t = entity;
        if (t == null || field.length < 1)
            return t;
        try {
            Class<?> entityClass = t.getClass();
            for (int i = 0; i < field.length; i++) {
                String fieldKey = field[i];
                Field entityField = entityClass.getDeclaredField(fieldKey);
                entityField.setAccessible(true);
                entityField.set(t, null);
            }
        } catch (Exception e) {
            return t;
        }
        return t;
    }

    @SuppressWarnings("all")
    //接口返回时,把调用者不需要的 "集合实体" 字段剔除(设为null)
    protected <T> List<T> clearResultNoNeedEntityListData(List<T> source, String... field) {
        List<T> entityList = source;
        if (entityList == null || entityList.size() == 0 || field.length < 1)
            return entityList;
        if (entityList instanceof ArrayList) {
            for (int i = 0; i < entityList.size(); i++) {
                T entity = entityList.get(i);
                clearResultNoNeedEntityData(entity, field);
            }
        } else {
            for (T entity : entityList) {
                clearResultNoNeedEntityData(entity, field);
            }
        }
        return entityList;
    }

    @SuppressWarnings("all")
    //规范服务接口,统一返回值 web调用需要返回多个值时,使用

    protected <K extends String, V, M extends Map<K, V>> M result(boolean success, V message, Class<M> mapType, Map... source) {
        M result;
        try {
            result = mapType.newInstance();
            for (int var = 0; var < source.length; var++) {
                result.putAll(source[var]);
            }
            result.put((K) successKey, (V) String.valueOf(success));
            result.put((K) messageKey, message);
        } catch (Exception e) {
            result = (M) new HashMap<K, V>();
            result.put((K) successKey, (V) String.valueOf(false));
            result.put((K) messageKey, (V) StackTraceUtil.getStackTrace(e));
            return result;
        }
        return result;
    }

    //规范服务接口,统一返回值

    protected <T> MessageBean result(String meta) {
        return new MessageBean<T>(meta, null, null, null);
    }

    protected <T> MessageBean result(String meta, String userInfoId) {
        return new MessageBean<T>(meta, null, userInfoId, null);
    }

    protected <T> MessageBean result(String meta, T data) {
        return new MessageBean<T>(meta, data, null, null);
    }

    protected <T> MessageBean result(T data, String userInfoId) {
        return new MessageBean<T>(null, data, userInfoId, null);
    }

    protected <T> MessageBean result(T data) {
        return new MessageBean<T>(null, data, null, null);
    }

    protected <T> MessageBean result(T data, String userInfoId,String operation) {
        return new MessageBean<T>(null, data, userInfoId, operation);
    }

    protected <T> MessageBean result(String meta, T data, String userInfoId) {
        return new MessageBean<T>(meta, data, userInfoId, null);
    }

    protected <T> MessageBean result(String meta, String userInfoId,String operation) {
        return new MessageBean<T>(meta, null, userInfoId, operation);
    }

    protected <T> MessageBean result(String meta, T data, String userInfoId, String operation) {
        return new MessageBean<T>(meta, data, userInfoId, operation);
    }

    protected <T> MessageBean result2(String meta, T data, String operation) {
        return new MessageBean<T>(meta, data, null, operation);
    }

    @SuppressWarnings("all")
    protected <T1 extends BaseBean, T2 extends Collection> T1 resultList(String meta, Class<T1> resultType) {
        return resultList(meta, null, resultType);
    }
    protected <T1 extends BaseBean, T2 extends Collection> T1 resultList(T2 source, Class<T1> resultType) {
        return resultList(null, source, resultType);
    }

    @SuppressWarnings("all")
    protected <T1 extends BaseBean, T2 extends Collection> T1 resultList(String meta, T2 source, Class<T1> resultType) {
        T1 t1 = null;
        try {
            t1 = resultType.newInstance();
            Field t1Field1 = t1.getClass().getSuperclass().getDeclaredField(metaKey);
            Field t1Field2 = t1.getClass().getDeclaredField(dataKey);
            t1Field1.setAccessible(true);
            t1Field2.setAccessible(true);
            t1Field1.set(t1, meta);
            t1Field2.set(t1, source);
        } catch (Exception e) {
            return t1;
        }
        return t1;
    }

    @SuppressWarnings("all")
    public <T extends BaseBean, V> T result(Class<T> type, V... value) {
        Class currentType = type;
        if (beanMap.containsKey(currentType)) {
            currentType = (Class<T>) beanMap.get(currentType);
        }
        T t = null;
        Class<?> parameterTypes[] = new Class[value.length];
        for (int i = 0; i < parameterTypes.length; i++)
            parameterTypes[i] = value[i].getClass();
        try {
            //Constructor<T> constructor = type.getConstructor(parameterTypes); //无法多态、参数类型必须完全匹配
            Constructor<?>[] constructors = currentType.getConstructors();
            for (int i = 0; i < constructors.length; i++) {
                Constructor<?> constructor = constructors[i];
                Class<?>[] entityParameterTypes = constructor.getParameterTypes();
                if (entityParameterTypes.length != parameterTypes.length)
                    continue;
                for (int entityParameterTypesIndex = 0; entityParameterTypesIndex < entityParameterTypes.length; entityParameterTypesIndex++) {
                    Class<?> entityParameterType = entityParameterTypes[entityParameterTypesIndex];
                    Class<?> parameterType = parameterTypes[entityParameterTypesIndex];
                    boolean typeCompareMessage = parameterType.isAssignableFrom(entityParameterType);
                    if (entityParameterType.getTypeName().equals(Object.class.getTypeName()))  //object类型
                        typeCompareMessage = true;
                    if (!typeCompareMessage) break;
                    if (entityParameterTypesIndex == entityParameterTypes.length - 1) {
                        Object result = constructor.newInstance(value);
                        t=(T) result;
                        return t;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }


}
