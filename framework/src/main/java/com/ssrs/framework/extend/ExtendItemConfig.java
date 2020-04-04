package com.ssrs.framework.extend;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import com.ssrs.framework.extend.plugin.PluginConfig;
import com.ssrs.framework.extend.plugin.PluginException;
import org.w3c.dom.Element;


/**
 * 扩展项类，对应一个扩展项
 */
public class ExtendItemConfig {
    private boolean enable;
    private PluginConfig pluginConfig;
    private String id;
    private String description;
    private String extendServiceID;
    private String className;
    private IExtendItem instance = null;

    public void init(PluginConfig pc, Element element) throws PluginException {
        pluginConfig = pc;
        id = XmlUtil.elementText(element, "id");
        description = XmlUtil.elementText(element, "description");
        extendServiceID = XmlUtil.elementText(element, "extend-service");
        className = XmlUtil.elementText(element, "class");
        if (StrUtil.isEmpty(id)) {
            throw new PluginException("extendItem's id is empty!");
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

    public String getExtendServiceID() {
        return extendServiceID;
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

    public void setExtendServiceID(String extendServiceID) {
        this.extendServiceID = extendServiceID;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public IExtendItem getInstance() {
        try {
            if (instance == null) {
                Class<?> clazz = Class.forName(className);
                try {
                    instance = (IExtendItem) clazz.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("ExtendItem " + className + " must implements IExtendItem");
                }
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
