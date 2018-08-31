package tanggod.github.io.common.dto;

import lombok.Data;

import java.io.Serializable;

/*
 *接口返回类型Bean
 *@author teddy
 *@date 2018/1/30
 */
@Data
public class MessageBean<T> extends BaseBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userInfoId;
    private String operation;//添加,删除,修改
    private T data;

    public MessageBean() {
    }

    public MessageBean(String meta, T data, String userInfoId, String operation) {
        this.userInfoId = userInfoId;
        this.operation = operation;
        this.meta = meta;
        this.data = data;
    }

}
