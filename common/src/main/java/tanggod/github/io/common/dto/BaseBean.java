package tanggod.github.io.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/*
 *接口返回Bean的父类
 *@author teddy
 *@date 2018/1/31
 */
@Data
public class BaseBean implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String meta; //辅助信息

    public BaseBean() {
    }

    @SuppressWarnings("all")
    public BaseBean setFieldValue(String field, Object value) {
        String fieldKey = field;
        Object fieldValue = value;
        BaseBean bean = this;
        if (null == fieldKey || "".equals(fieldKey))
            return this;

        try {
            Class<? extends BaseBean> beanClass = bean.getClass();
            Field beanField = beanClass.getDeclaredField(fieldKey);
            beanField.setAccessible(true);
            beanField.set(bean, value);
        } catch (Exception e) {
            return bean;
        }
        return bean;
    }

    @SuppressWarnings("all")
    public <T extends BaseBean> T valueOf(Class<T> type) {
       /* T value = null;
        try {
            value = type.newInstance();
            if (value instanceof BaseBean)
                return (T) this;
            return value;
        } catch (Exception e) {
            return value;
        }*/
        type = null;
        return (T) this;
    }

}
