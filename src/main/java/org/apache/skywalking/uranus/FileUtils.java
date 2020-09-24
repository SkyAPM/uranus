package org.apache.skywalking.uranus;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

public class FileUtils extends org.apache.commons.io.FileUtils {
    /**
     * 获取当前用户家目录 例如/Users/harvies
     *
     * @return 路径
     */
    public static String getCurrentUserHomePath() {
        return System.getProperty("user.home");
    }

    /**
     * 分隔同一个路径字符串中的目录的，例如：
     * C:/Program Files/Common Files
     * 就是指“/”
     * <p>
     * unix下是/ windows下是\\
     *
     * @return
     */
    public static String getSeparator() {
        return File.separator;
    }

    /**
     * 分隔连续多个路径字符串的分隔符，例如:
     * java   -cp   test.jar;abc.jar   HelloWorld
     * 就是指“;”
     * <p>
     * linux下是: windows下是;
     *
     * @return
     */
    public static String getPathSeparator() {
        return File.pathSeparator;
    }

    /**
     * 获取操作系统临时目录
     * example: /var/folders/ch/p2kq0ygs0hv24fd8swp1skqh0000gn/T/
     *
     * @return 临时目录
     */
    public static String getTmpDir() {
        return System.getProperty("java.io.tmpdir");
    }

    /**
     * 根据文件路径获取目录路径
     * /org/mongodb/mongo-java-driver/3.8.0/mongo-java-driver-3.8.0.jar
     *
     * @param filePath
     * @return
     */
    public static String getDirectoryPath(String filePath) {
        return StringUtils.substring(filePath, 0, StringUtils.lastIndexOf(filePath, "/"));
    }
}
