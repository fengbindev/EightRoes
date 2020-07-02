package com.ssrs.framework.extend.plugin;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import com.ssrs.framework.extend.ExtendActionConfig;
import com.ssrs.framework.extend.ExtendItemConfig;
import com.ssrs.framework.extend.ExtendPointConfig;
import com.ssrs.framework.extend.ExtendServiceConfig;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 插件类，对应一个插件
 *
 * @author ssrs
 */
public class PluginConfig {
    private Map<String, ExtendPointConfig> extendPoints = new HashMap<String, ExtendPointConfig>();
    private Map<String, ExtendServiceConfig> extendServices = new HashMap<String, ExtendServiceConfig>();
    private Map<String, String> requiredExtendPoints = new HashMap<String, String>();
    private Map<String, String> requiredExtendServices = new HashMap<String, String>();
    private Map<String, ExtendActionConfig> extendActions = new HashMap<String, ExtendActionConfig>();
    private Map<String, ExtendItemConfig> extendItems = new HashMap<String, ExtendItemConfig>();
    private String ID;
    private String Name;
    private String ClassName;
    private String author;
    private String provider;
    private String version;
    private String description;
    private boolean enabled;
    private boolean running;

    public Map<String, ExtendPointConfig> getExtendPoints() {
        return extendPoints;
    }

    public Map<String, ExtendServiceConfig> getExtendServices() {
        return extendServices;
    }

    public Map<String, String> getRequiredExtendPoints() {
        return requiredExtendPoints;
    }


    public Map<String, ExtendActionConfig> getExtendActions() {
        return extendActions;
    }

    public Map<String, ExtendItemConfig> getExtendItems() {
        return extendItems;
    }


    public String getID() {
        return ID;
    }

    public String getClassName() {
        return ClassName;
    }

    public String getAuthor() {
        return author;
    }

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }


    public String getName() {
        return Name;
    }


    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabling) {
        this.enabled = enabling;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public String getProvider() {
        return provider;
    }

    public void setID(String iD) {
        ID = iD;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setClassName(String className) {
        ClassName = className;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void parse(String xml) throws PluginException {
        enabled = true;// 默认开启

        // 基本信息
        XmlUtil.cleanInvalid(xml);
        Document document = XmlUtil.readXML(xml);
        Element root = XmlUtil.getRootElement(document);
        ID = XmlUtil.elementText(root, "id");
        Name = XmlUtil.elementText(root, "name");
        ClassName = XmlUtil.elementText(root, "class");
        author = XmlUtil.elementText(root, "author");
        provider = XmlUtil.elementText(root, "provider");
        version = XmlUtil.elementText(root, "version");
        description = XmlUtil.elementText(root, "description");

        if (StrUtil.isEmpty(ID)) {
            throw new PluginException("id is empty!");
        }
        if (StrUtil.isEmpty(Name)) {
            throw new PluginException("name is empty!");
        }
        if (StrUtil.isEmpty(version)) {
            throw new PluginException("version is empty!");
        }

        // 扩展点
        List<Element> extendPoint = XmlUtil.getElements(root, "extend-point");
        if (CollUtil.isNotEmpty(extendPoint)) {
            for (Element element : extendPoint) {
                ExtendPointConfig ep = new ExtendPointConfig();
                ep.init(this, element);
                extendPoints.put(ep.getID(), ep);
            }
        }

        // 扩展服务
        List<Element> extendService = XmlUtil.getElements(root, "extend-service");
        if (CollUtil.isNotEmpty(extendService)) {
            for (Element element : extendService) {
                ExtendServiceConfig ep = new ExtendServiceConfig();
                ep.init(this, element);
                extendServices.put(ep.getID(), ep);
            }
        }

        // 扩展项
        List<Element> extendItem = XmlUtil.getElements(root, "extend-item");
        if (CollUtil.isNotEmpty(extendItem)) {
            for (Element element : extendItem) {
                ExtendItemConfig ei = new ExtendItemConfig();
                ei.init(this, element);
                extendItems.put(ei.getID(), ei);
                requiredExtendServices.put(ei.getExtendServiceID(), "Y");
            }
        }

        // 扩展行为
        List<Element> extendAction = XmlUtil.getElements(root, "extend-action");
        if (CollUtil.isNotEmpty(extendAction)) {
            for (Element element : extendAction) {
                ExtendActionConfig eac = new ExtendActionConfig();
                eac.init(this, element);
                extendActions.put(eac.getID(), eac);
                requiredExtendPoints.put(eac.getExtendPointID(), "Y");
            }
        }
    }

    @Override
    public String toString() {
        String str = super.toString();
        str = str.substring(str.lastIndexOf('@'));
        str = "plugin:" + getID() + str;
        return str;
    }

}
