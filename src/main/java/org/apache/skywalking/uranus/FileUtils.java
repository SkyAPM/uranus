package org.apache.skywalking.uranus;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

public class FileUtils extends org.apache.commons.io.FileUtils {
    /**
     * Get the current user's home directory such as /Users/harvies
     *
     * @return path
     */
    public static String getCurrentUserHomePath() {
        return System.getProperty("user.home");
    }

    /**
     * Separate directories in the same path string, for example:
     * C:/Program Files/Common Files
     * Means "/"
     * <p>
     * Under Unix/ \\ under Windows
     */
    public static String getSeparator() {
        return File.separator;
    }

    /**
     * Separator to separate multiple consecutive path strings, for example:
     * java -cp test.jar;abc.jar HelloWorld
     * Means ";"
     * <p>
     * Yes under linux: Yes under windows;
     */
    public static String getPathSeparator() {
        return File.pathSeparator;
    }

    /**
     * Obtain the operating system temporary directory
     * example: /var/folders/ch/p2kq0ygs0hv24fd8swp1skqh0000gn/T/
     *
     * @return path
     */
    public static String getTmpDir() {
        return System.getProperty("java.io.tmpdir");
    }

    /**
     * Get the directory path according to the file path
     * For example, /org/mongodb/mongo-java-driver/3.8.0/mongo-java-driver-3.8.0.jar
     * will get /org/mongodb/mongo-java-driver/3.8.0
     */
    public static String getDirectoryPath(String filePath) {
        return StringUtils.substring(filePath, 0, StringUtils.lastIndexOf(filePath, "/"));
    }
}
