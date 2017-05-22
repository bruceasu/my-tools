package asu.tool.util;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

/**
 * 动态加载Jar，dir到classpath
 *
 * @author <a href="mailto:victor.su@gwtsz.net">Victor Su&lt;victor.su@gwtsz.net&gt;</a>
 * @version 1.0.0
 * @date 2016/7/11 11:28
 * @copyright 2016 Victor All rights reserved.
 * @since ${since}
 */
public class ExtClasspathLoader {
    private static Method addURL = initAddMethod();
    private static URLClassLoader classloader = (URLClassLoader) ClassLoader.getSystemClassLoader();

    /**
     * 初始化addUrl 方法.
     *
     * @return 可访问addUrl方法的Method对象
     */
    private static Method initAddMethod() {
        try {
            Method add = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
            add.setAccessible(true);
            return add;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     *
     */
    /**
     * 加载jar classpath。
     * @param jarDirFiles include jars directories
     * @param resFiles include resources directories
     */
    public static void addJarAndResFile(
            List<String> jarDirFiles, List<String> resFiles) {
        if (jarDirFiles != null) {
            for (String f : jarDirFiles) {
                addJarFile(f);
            }
        }
        if (resFiles != null) {
            for (String r : resFiles) {
                addResourceDir(r);
            }
        }
    }

    public static void addJarFile(String filepath) {
        File file = new File(filepath);
        loopJarDir(file);
    }

    public static void addResourceDir(String filepath) {
        File file = new File(filepath);
        loopResourceDirs(file);
    }

    /**
     * 通过filepath加载文件到classpath。
     *
     * @param file 文件路径
     */
    public static void addURL(File file) {
        try {
            addURL.invoke(classloader, file.toURI().toURL());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 循环遍历目录，找出所有的资源路径。
     *
     * @param file 当前遍历文件
     */
    private static void loopResourceDirs(File file) {
        // 资源文件只加载路径
        if (file.isDirectory()) {
            addURL(file);
        }
    }

    /**
     * 循环遍历目录，找出所有的jar包。
     *
     * @param file 当前遍历文件
     */
    private static void loopJarDir(File file) {
        if (file.isDirectory()) {
            File[] tmps = file.listFiles();
            for (File tmp : tmps) {
                loopJarDir(tmp);
            }
        } else {
            if (file.getAbsolutePath().endsWith(".jar") || file.getAbsolutePath().endsWith(".zip")) {
                addURL(file);
            }
        }
    }

}
