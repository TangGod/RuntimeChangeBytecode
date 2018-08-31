package tanggod.github.io.runtimechangebytecode;

import tanggod.github.io.runtimechangebytecode.core.config.FeignConfig;
import tanggod.github.io.runtimechangebytecode.core.RuntimeChangeBytecode;

/*
 *
 *@author teddy
 *@date 2018/8/30
 */
public class ProxyInstall {

    public static void main(String[] args) throws Exception {
        RuntimeChangeBytecode run = new FeignConfig();
        run.createProxy(null,null);
        System.out.println("install success ! ");
    }


}
