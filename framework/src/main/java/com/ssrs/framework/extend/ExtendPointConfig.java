package com.ssrs.framework.extend;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import com.ssrs.framework.extend.plugin.PluginConfig;
import com.ssrs.framework.extend.plugin.PluginException;
import org.w3c.dom.Element;

/**
 * 扩展点类，对应一个扩展点
 *
 * @author ssrs
 */
public class ExtendPointConfig {
    private boolean enable;
    private PluginConfig pluginConfig;
    private String id;
    private String description;
    private String className;
    private Class<?> clazz;

    public void init(PluginConfig pc, Element element) throws PluginException {
        pluginConfig = pc;
        id = XmlUtil.elementText(element, "id");
        description = XmlUtil.elementText(element, "description");
        className = XmlUtil.elementText(element, "className");
        if (StrUtil.isEmpty(id)) {
            throw new PluginException("extendPoint's id is empty!");
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

    public boolean isChild(Class<?> cls) {
        if (className == null) {
            return false;
        }
        if (clazz == null) {
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return cls.isAssignableFrom(clazz);
    }

    public Class<?> getParentClass() throws PluginException {
        if (className == null) {
            return null;
        }
        if (clazz == null) {
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new PluginException("ExtendPoint's class not found:" + className);
            }
        }
        return clazz;
    }
}
