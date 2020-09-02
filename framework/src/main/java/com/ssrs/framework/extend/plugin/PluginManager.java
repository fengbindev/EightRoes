package com.ssrs.framework.extend.plugin;

import cn.hutool.core.io.IoUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 插件管理器
 *
 * @author ssrs
 */
public class PluginManager {
    private static final Log log = LogFactory.get(PluginManager.class);
    private ArrayList<PluginConfig> configList = null;
    private ReentrantLock lock = new ReentrantLock();
    private static PluginManager instance = new PluginManager();

    public static PluginManager getInstance() {
        return instance;
    }

    public void init() {
        if (configList == null || configList.size() == 0) {
            lock.lock();
            try {
                if (configList == null || configList.size() == 0) {
                    loadAllConfig();
                }
            } finally {
                lock.unlock();
            }
        }
    }

    public void loadAllConfig() {
        configList = new ArrayList<PluginConfig>();
        Map<String, PluginConfig> map = new LinkedHashMap<>();
        // 首先加载framework插件
        ClassPathResource frameworkPlugin = new ClassPathResource("plugins/com.ssrs.framework.xml");
        if (frameworkPlugin.exists()) {
            try {
                InputStream is = frameworkPlugin.getInputStream();
                PluginConfig pc = new PluginConfig();
                pc.parse(new String(IoUtil.readBytes(is), "UTF-8"));
                map.put(pc.getID(), pc);
            } catch (IOException | PluginException e) {
                e.printStackTrace();
            }
        }
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] rs = resourcePatternResolver.getResources("classpath*:/plugins/*.xml");
            for (Resource r : rs) {
                try (InputStream is = r.getInputStream()) {
                    PluginConfig pc = new PluginConfig();
                    pc.parse(new String(IoUtil.readBytes(is), "UTF-8"));
                    if (map.containsKey(pc.getID()) && !pc.getID().equals("com.ssrs.framework.FrameworkPlugin")) {
                        log.warn("PluginConfig is overrode:" + r.getURI().toString());
                    }
                    map.put(pc.getID(), pc);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        configList.addAll(map.values());

        ArrayList<PluginConfig> tempEachList = new ArrayList(this.configList);
        ArrayList<PluginConfig> result = new ArrayList();
        Iterator iterator1 = tempEachList.iterator();

        while (iterator1.hasNext()) {
            PluginConfig pc = (PluginConfig) iterator1.next();
            this.sort(result, pc);
        }

        this.configList = new ArrayList<>(result);
    }


    private void sort(List<PluginConfig> result, PluginConfig pc) {
        if (this.getPluginConfig(result, pc.getID()) == null) {
            this.configList.remove(pc);
            Iterator var3 = pc.getRequiredPlugins().keySet().iterator();

            while(var3.hasNext()) {
                String pluginID = (String)var3.next();
                PluginConfig c = this.getPluginConfig(this.configList, pluginID);
                if (c != null) {
                    this.sort(result, c);
                }
            }

            if (this.getPluginConfig(result, pc.getID()) == null) {
                result.add(pc);
            }

        }
    }

    public PluginConfig getPluginConfig(List<PluginConfig> list, String pluginID) {
        for (PluginConfig c : list) {
            if (c.getID().equals(pluginID)) {
                return c;
            }
        }
        return null;
    }

    public PluginConfig getPluginConfig(String pluginID) {
        return getPluginConfig(configList, pluginID);
    }

    public ArrayList<PluginConfig> getAllPluginConfig() {
        return configList;
    }

    public void destory() {
        configList = null;
    }
}
