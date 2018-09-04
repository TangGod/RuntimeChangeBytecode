package tanggod.github.io.common.dto;

/*
 *
 *@author teddy
 *@date 2018/9/3
 */
public class ApplicationBootstrapInitializeException extends RuntimeException {

    public ApplicationBootstrapInitializeException(String message) {
        super(message);
    }

    public ApplicationBootstrapInitializeException() {
        super("Application  初始化失败");
    }
}
