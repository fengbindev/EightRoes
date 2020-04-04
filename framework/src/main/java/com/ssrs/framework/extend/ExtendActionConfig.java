package com.ssrs.framework.extend;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import com.ssrs.framework.extend.plugin.PluginConfig;
import com.ssrs.framework.extend.plugin.PluginException;
import org.w3c.dom.Element;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 扩展行为类，对应一个扩展行为
 */
public class ExtendActionConfig {
    private boolean enable;
    private PluginConfig pluginConfig;
    private String id;
    private String description;
    private String extendPointID;
    private String className;
    private IExtendAction instance = null;
    private static ReentrantLock lock = new ReentrantLock();

    public void init(PluginConfig pc, Element element) throws PluginException {
        pluginConfig = pc;
        id = XmlUtil.elementText(element, "id");
        description = XmlUtil.elementText(element, "description");
        extendPointID = XmlUtil.elementText(element, "extend-point");
        className = XmlUtil.elementText(element, "class");
        if (StrUtil.isEmpty(id)) {
            throw new PluginException("extendAction's id is empty!");
        }
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isEnable() {
        return enable;
    }

    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    public String getID() {
        return id;
    }

    public String getDescription() {
        return description;
    }


    public String getExtendPointID() {
        return extendPointID;
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

    public void setExtendPointID(String extendPointID) {
        this.extendPointID = extendPointID;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public IExtendAction getInstance() {
        try {
            if (instance == null) {
                lock.lock();
                try {
                    if (instance == null) {
                        Class<?> clazz = Class.forName(className);
                        ExtendPointConfig ep = ExtendManager.getInstance().findExtendPoint(extendPointID);
                        if (ep.isChild(clazz)) {
                            throw new RuntimeException(
                                    "ExtendAction " + className + " must extends " + ep.getClassName());
                        }
                        instance = (IExtendAction) clazz.newInstance();
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


}
