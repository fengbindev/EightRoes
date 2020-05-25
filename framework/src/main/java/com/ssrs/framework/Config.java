package com.ssrs.framework;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.system.*;
import com.ssrs.framework.cache.ConfigCacheProvider;
import com.ssrs.framework.cache.FrameworkCacheManager;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


public class Config {
    private static final Log log = LogFactory.get(Config.class);
    private static boolean loaded = false;
    protected static ConcurrentHashMap<String, String> configMap = new ConcurrentHashMap<>(50);
    /**
     * 构建信息
     */
    protected static BuildProperties buildInfo = null;
    /**
     * 应用code
     */
    protected static String appCode = null;
    /**
     * 应用名称
     */
    protected static String appName = null;
    /**
     * 应用版本
     */
    protected static String appVersion = null;
    /**
     * Servlet容器支持的JSP规范的最大版本
     */
    protected static int servletMajorVersion;

    /**
     * Servlet容器支持的JSP规范的最小版本
     */
    protected static int servletMinorVersion;

    /**
     * 全局字符集设置，在读写文本、与数据库通信等所有涉及到字符串但又未明确指定字符集的地方会使用全局字符集进行操作
     */
    protected static String globalCharset = "UTF-8";

    protected static ServletContext servletContext;

    protected static WebApplicationContext webApplicationContext;

    protected static String contextRealPath;

    /**
     * 日志，索引等存放路径
     */
    protected static String appDataPath;

    /**
     * 应用程序目录的全路径
     */
    protected static String applicationPath;

    protected static void init() {
        if (!loaded) {
            configMap.putAll(ConfigLoader.getAppConfig());
            JavaInfo javaInfo = SystemUtil.getJavaInfo();
            JavaRuntimeInfo javaRuntimeInfo = SystemUtil.getJavaRuntimeInfo();
            OsInfo osInfo = SystemUtil.getOsInfo();
            UserInfo userInfo = SystemUtil.getUserInfo();
            HostInfo hostInfo = SystemUtil.getHostInfo();
            configMap.put("system.javaVersion", javaInfo.getVersion());
            configMap.put("system.javaVendor", javaInfo.getVendor());
            configMap.put("system.javaHome", javaRuntimeInfo.getHomeDir());
            configMap.put("system.osArch", osInfo.getArch());
            configMap.put("system.osName", osInfo.getName());
            configMap.put("system.osVersion", osInfo.getVersion());
            configMap.put("system.fileSeparator", osInfo.getFileSeparator());
            configMap.put("system.lineSeparator", osInfo.getLineSeparator());
            configMap.put("system.systemName", userInfo.getName());
            configMap.put("system.systemLanguage", userInfo.getLanguage() + userInfo.getCountry());
            configMap.put("system.hostName", hostInfo.getName());
            configMap.put("system.hostAddress", hostInfo.getAddress());
            initProduct();
            setAppDataPath(ConfigLoader.getAppConfig().get("App.appDataPath"));
            loaded = true;
            log.info("----{} ({}): Config Initialized----", getAppCode(), getAppName());
        }
    }

    private static void initProduct() {
        appCode = Optional.ofNullable(appCode)
                .orElseGet(() -> configMap.getOrDefault("App.code", "EightRoes"));
        appName = Optional.ofNullable(appName)
                .orElseGet(() -> configMap.getOrDefault("App.name", "八玫瑰快速开发框架"));
        appVersion = Optional.ofNullable(appVersion)
                .orElseGet(() -> configMap.getOrDefault("App.version", "1.0"));
    }

    public static void reloadConfig() {
        loaded = false;
        configMap.clear();
        FrameworkCacheManager.removeType(ConfigCacheProvider.ProviderID, ConfigCacheProvider.ProviderID);
        ConfigLoader.reload();
        init();
    }

    private static Map<String, String> getMap() {
        return configMap;
    }

    public static String getValue(String key) {
        init();
        String value = configMap.get(key);
        if (StrUtil.isNotEmpty(value)){
            return  value;
        }
        Object cacheValue = FrameworkCacheManager.get(ConfigCacheProvider.ProviderID, ConfigCacheProvider.ProviderID, key);
        if (ObjectUtil.isNotEmpty(cacheValue)){
            setValue(key, Convert.toStr(cacheValue));
        }
        return Convert.toStr(cacheValue);
    }

    public static void setValue(String key, String value) {
        configMap.put(key, value);
        FrameworkCacheManager.set(ConfigCacheProvider.ProviderID, ConfigCacheProvider.ProviderID, key, value);
    }

    public static void removeValue(String key) {
        configMap.remove(key);
        FrameworkCacheManager.remove(ConfigCacheProvider.ProviderID, ConfigCacheProvider.ProviderID, key);
    }

    /**
     * @return 获取应用code
     */
    public static String getAppCode() {
        if (!loaded) {
            init();
        }
        return appCode;
    }

    /**
     * @return 获取应用名称
     */
    public static String getAppName() {
        if (!loaded) {
            init();
        }
        return appName;
    }

    /**
     * @return 获取应用版本
     */
    public static String getAppVersion() {
        if (!loaded) {
            init();
        }
        return appVersion;
    }

    /**
     * @return 文本文件默认分隔符
     */
    public static String getLineSeparator() {
        if (!loaded) {
            init();
        }
        return Config.getValue("system.lineSeparator");
    }

    /**
     * @return 文件名中的路径分隔符
     */
    public static String getFileSeparator() {
        if (!loaded) {
            init();
        }
        return Config.getValue("system.fileSeparator");
    }

    /**
     * 返回操作系统名称
     */
    public static String getOSName() {
        if (!loaded) {
            init();
        }
        return Config.getValue("system.osName");
    }

    /**
     * 返回IP地址
     */
    public static String getIpAddress() {
        if (!loaded) {
            init();
        }
        return Config.getValue("system.hostAddress");
    }

    /**
     * @return 应用全局字符集
     */
    public static String getGlobalCharset() {
        return globalCharset;
    }

    /**
     * @return 获取WebApplicationContext
     */
    public static WebApplicationContext getWebApplicationContext() {
        return webApplicationContext;
    }

    static void setWebApplicationContext(WebApplicationContext webApplicationContext) {
        Config.webApplicationContext = webApplicationContext;
    }

    /**
     * 获取构建信息
     *
     * @return
     */
    public static BuildProperties getBuildInfo() {
        return buildInfo;
    }

    public static void setBuildInfo(BuildProperties buildProperties) {
        Config.buildInfo = buildProperties;
    }

    public static void setServletMajorVersion(int servletMajorVersion) {
        Config.servletMajorVersion = servletMajorVersion;
    }

    public static void setServletMinorVersion(int servletMinorVersion) {
        Config.servletMinorVersion = servletMinorVersion;
    }

    /**
     * @return 获取ServletContext
     */
    public static ServletContext getServletContext() {
        return servletContext;
    }

    static void setServletContext(ServletContext servletContext) {
        Config.servletContext = servletContext;
        configMap.put("system.containerInfo", servletContext.getServerInfo());
        setServletMajorVersion(servletContext.getMajorVersion());
        setServletMinorVersion(servletContext.getMinorVersion());
        getJBossInfo();
        contextRealPath = servletContext.getRealPath("/");
        if (contextRealPath != null) {
            contextRealPath = FileUtil.normalize(contextRealPath);
        }
        log.info("Context-RealPath: {}", contextRealPath);
    }

    /**
     * @return 中间件容器信息
     */

    public static String getContainerInfo() {
        return Config.getValue("System.ContainerInfo");
    }

    /**
     * @return 中间件容器的版本
     */
    public static String getContainerVersion() {// NO_UCD
        String str = Config.getValue("System.ContainerInfo");
        if (str.indexOf("/") > 0) {
            return str.substring(str.lastIndexOf("/") + 1);
        }
        return "0";
    }

    /**
     * @return 中间件是否是Tomcat
     */
    public static boolean isTomcat() {
        if (StrUtil.isEmpty(Config.getContainerInfo())) {
            getJBossInfo();
        }
        return Config.getContainerInfo().toLowerCase().indexOf("tomcat") >= 0;
    }

    /**
     * JBoss需要特别处理 JBoss调用ServletContext.getServerInfo()时会返回Apache Tomcat 5.x之类的，
     * 且MainFilter会后面Config执行，需要特别处理
     */
    public static void getJBossInfo() {
        String jboss = System.getProperty("jboss.home.dir");
        if (StrUtil.isNotEmpty(jboss)) {
            try {
                Class<?> c = Class.forName("org.jboss.Version");
                Method m = c.getMethod("getInstance", (Class[]) null);
                Object o = m.invoke(null, (Object[]) null);
                m = c.getMethod("getMajor", (Class[]) null);
                Object major = m.invoke(o, (Object[]) null);
                m = c.getMethod("getMinor", (Class[]) null);
                Object minor = m.invoke(o, (Object[]) null);
                m = c.getMethod("getRevision", (Class[]) null);
                Object revision = m.invoke(o, (Object[]) null);
                m = c.getMethod("getTag", (Class[]) null);
                Object tag = m.invoke(o, (Object[]) null);
                Config.configMap.put("system.containerInfo",
                        "JBoss/" + major + "." + minor + "." + revision + "." + tag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return 中间件是否是JBoss
     */
    public static boolean isJboss() {
        if (StrUtil.isEmpty(Config.getContainerInfo())) {
            getJBossInfo();
        }
        return Config.getContainerInfo().toLowerCase().indexOf("jboss") >= 0;
    }

    /**
     * @return 中间件是否是WebLogic
     */
    public static boolean isWeblogic() {
        return Config.getContainerInfo().toLowerCase().indexOf("weblogic") >= 0;
    }

    /**
     * @return 中间件是否是WebSphere
     */
    public static boolean isWebSphere() {
        return Config.getContainerInfo().toLowerCase().indexOf("websphere") >= 0;
    }


    /**
     * WEB应用下返回应用的实际路径
     */
    public static String getContextRealPath() {
        return contextRealPath;
    }

    /**
     * 返回应用路径，返回值以/结束。 考虑到同一个应用在内外网有不同的路径的情况，该处变量在每一次进入Filter后都会重新设置<br>
     */
    public static String getContextPath() {
        return configMap.get("App.ContextPath");
    }


    /**
     * J2EE环境下返回程序的实际路径，独立运行时返回class的根目录
     */
    public static String getApplicationRealPath() {

        if (applicationPath == null) {
            String path = "";
            URL url = Config.class.getClassLoader().getResource("");
            if (url == null) {
                System.err.println("Config.getClassesPath() failed!");
                return "";
            }
            try {
                path = URLDecoder.decode(url.getPath(), System.getProperty("file.encoding"));
                OsInfo osInfo = SystemUtil.getOsInfo();
                if (osInfo.isWindows()) {
                    if (path.startsWith("/")) {
                        path = path.substring(1);
                    }
                }
                if (path.startsWith("file:/")) {
                    path = path.substring(6);
                } else if (path.startsWith("jar:file:/")) {
                    path = path.substring(10);
                }
                if (path.indexOf(".jar!") > 0) {
                    path = path.substring(0, path.indexOf(".jar!"));
                }
                path = path.replace('\\', '/');
                path = path.substring(0, path.lastIndexOf("/") + 1);
                if (!osInfo.isWindows()) {
                    if (!path.startsWith("/")) {
                        path = "/" + path;
                    }
                }
                int index = path.indexOf("WEB-INF");
                if (path.indexOf("WEB-INF") >= 0) {
                    path = path.substring(0, index);
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                applicationPath = FileUtil.normalize(new File(path).getCanonicalPath());
            } catch (IOException e) {
                e.printStackTrace();
                applicationPath = FileUtil.normalize(System.getProperty("user.dir"));
            }
            if (applicationPath.endsWith("/target/classes")) {
                applicationPath = applicationPath.substring(0, applicationPath.length() - "/target/classes".length());
            }
        }
        return applicationPath;
    }

    public static String getAppDataPath() {
        if (!loaded) {
            init();
        }
        return appDataPath;
    }

    public static void setAppDataPath(String appDataPath) {
        Config.appDataPath = replacePathHolder(appDataPath);
    }

    public static String replacePathHolder(String v) {
        String path = Config.getApplicationRealPath();
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        String parentPath = FileUtil.normalize(path);
        if (parentPath.endsWith("/target/classes")) {
            parentPath = parentPath.substring(0, parentPath.length() - "/target/classes".length());
        } else {
            int endIndex = path.lastIndexOf("/");
            if (endIndex >= 0) {
                parentPath = path.substring(0, endIndex);
            } else {
                log.warn("Path ${Parent} :" + path);
            }
        }

        v = StrUtil.replace(v, "${Self}", path, true);
        v = StrUtil.replace(v, "%{Self}", path, true);
        v = StrUtil.replace(v, "${Parent}", parentPath, true);
        v = StrUtil.replace(v, "%{Parent}", parentPath, true);
        v = FileUtil.normalize(v);
        return v;
    }


}
