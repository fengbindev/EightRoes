package com.ssrs.elasticsearch.util;

import com.ssrs.elasticsearch.annotation.FieldWeight;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 工具类,获取某个包下的所有类,获取某个包下所有类的包含某个注解的全局变量
 */
public class ClassUtil {

    private ClassUtil() {
    }

    /**
     * 取得某一类所在包的所有类名 不含迭代
     */
    public static String[] getPackageAllClassName(String classLocation, String packageName) {
        String[] packagePathSplit = packageName.split("[.]");
        StringBuilder realClassLocation = new StringBuilder(classLocation);
        int packageLength = packagePathSplit.length;
        for (int i = 0; i < packageLength; i++) {
            realClassLocation.append(realClassLocation).append(File.separator).append(packagePathSplit[i]);
        }
        File packeageDir = new File(realClassLocation.toString());
        if (packeageDir.isDirectory()) {
            return packeageDir.list();
        }
        return new String[]{};
    }

    /**
     * 从包package中获取所有的Class
     *
     * @param pack
     * @return
     */
    public static List<Class<?>> getClasses(String packageName) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        boolean recursive = true;
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                } else if ("jar".equals(protocol)) {
                    JarFile jar;
                    try {
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        Enumeration<JarEntry> entries = jar.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            if (name.charAt(0) == '/') {
                                name = name.substring(1);
                            }
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                if (idx != -1) {
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                if ((idx != -1) || recursive) {
                                    if (name.endsWith(".class") && !entry.isDirectory()) {
                                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                                        try {
                                            classes.add(Class.forName(packageName + '.' + className));
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;

    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName
     * @param packagePath
     * @param recursive
     * @param classes
     */
    private static void findAndAddClassesInPackageByFile(String packageName, String packagePath,
                                                         final boolean recursive, List<Class<?>> classes) {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] dirfiles = dir
                .listFiles((File file) -> (recursive && file.isDirectory()) || (file.getName().endsWith(".class")));
        for (File file : dirfiles) {
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive,
                        classes);
            } else {
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    classes.add(Class.forName(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据包名和注解类获取此包下所有类的此注解的全局变量集合
     */
    public static List<String> getFieldListByAnnotation(String packageName, Class<FieldWeight> annotationClass) {
        ArrayList<String> fields = new ArrayList<>();
        List<Class<?>> classList = getClasses(packageName);
        for (Class<?> c : classList) {
            Field[] declaredFields = c.getDeclaredFields();
            for (Field f : declaredFields) {
                if (f.getAnnotation(annotationClass) != null) {
                    fields.add(f.getName());
                }

            }
        }
        return fields;
    }
}
