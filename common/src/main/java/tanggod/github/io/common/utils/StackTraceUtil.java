package tanggod.github.io.common.utils;


import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 
 * @author ytx
 *
 */
public class StackTraceUtil {
    /**
     * 取出exception中的信息
     * 
     * @param exception
     * @return
     */
    public static String getStackTrace(Throwable exception) {
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            exception.printStackTrace(pw);
            return sw.toString();
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }
}
