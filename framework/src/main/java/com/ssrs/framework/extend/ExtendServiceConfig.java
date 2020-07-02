package com.ssrs.framework.extend;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.ssrs.framework.extend.plugin.PluginConfig;
import com.ssrs.framework.extend.plugin.PluginException;
import org.w3c.dom.Element;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 扩展服务类，对应一个扩展服务
 *
 * @author ssrs
 */
public class ExtendServiceConfig {
    private static final Log log = LogFactory.get();
    private boolean enable;
    private PluginConfig pluginConfig;
    private String id;
    private String description;
    private String className;
    private String itemClassName;

    private IExtendService<?> instance = null;
    private static ReentrantLock lock = new ReentrantLock();

    public void init(PluginConfig pc, Element element) throws PluginException {
        pluginConfig = pc;
        id = XmlUtil.elementText(element, "id");
        description = XmlUtil.elementText(element, "description");
        className = XmlUtil.elementText(element, "class");
        itemClassName = XmlUtil.elementText(element, "item-class");
        if (StrUtil.isEmpty(id)) {
            throw new PluginException("extendService's id is empty!");
        }
    }

    public String getID() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getClassName() {
        return className;
    }

    public void setID(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    public String getItemClassName() {
        return itemClassName;
    }

    public void setItemClassName(String itemClassName) {
        this.itemClassName = itemClassName;
    }

    public IExtendService<?> getInstance() {
        try {
            if (instance == null) {
                lock.lock();
                try {
                    if (instance == null) {
                        Class<?> clazz = Class.forName(className);
                        IExtendService<?> tmp = (IExtendService<?>) clazz.newInstance();
                        try {
                            List<ExtendItemConfig> list = ExtendManager.getInstance().findItemsByServiceID(id);
                            if (CollUtil.isNotEmpty(list)) {
                                for (ExtendItemConfig item : list) {
                                    try {
                                        tmp.register(item.getInstance());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        log.error("Load ExtendItem " + item.getClassName() + " failed!", e);
                                    }
                                }
                            }
                            instance = tmp;
                        } catch (Exception e) {
                            e.printStackTrace();
                            log.error("Load ExtendService " + className + " failed!", e);
                        }
                    }
                } finally {
                    lock.unlock();
                }
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void destory() {
        if (instance != null) {
            instance.destory();
        }
    }
}
