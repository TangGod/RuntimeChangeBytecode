package tanggod.github.io.api;

import lombok.Data;

import java.io.Serializable;

/*
 *
 *@author teddy
 *@date 2018/8/27
 */
@Data
public class UserDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String address;
    private String telephone;
    private String ip;
}
