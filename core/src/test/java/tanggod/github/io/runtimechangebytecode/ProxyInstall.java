package tanggod.github.io.runtimechangebytecode;

import tanggod.github.io.runtimechangebytecode.core.FeignConfig;
import tanggod.github.io.runtimechangebytecode.core.RuntimeChangeBytecode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.jar.*;
import java.util.stream.Collectors;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/*
 *
 *@author teddy
 *@date 2018/8/30
 */
public class ProxyInstall {

    public static void main(String[] args) throws Exception {
        RuntimeChangeBytecode run = new FeignConfig();
        run.createProxy("tanggod",run.getResolverSearchPath());
        System.out.println("install success ! ");
    }


}
