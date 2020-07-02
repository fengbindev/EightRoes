package com.ssrs.framework.extend;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.ssrs.framework.extend.plugin.IPlugin;
import com.ssrs.framework.extend.plugin.PluginConfig;
import com.ssrs.framework.extend.plugin.PluginException;
import com.ssrs.framework.extend.plugin.PluginManager;
import com.ssrs.framework.point.AfterAllPluginStartedPoint;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 扩展管理器
 *
 * @author ssrs
 */
public class ExtendManager {
    private static final Log log = LogFactory.get();
    private Map<String, ArrayList<ExtendActionConfig>> extendActionMap;
    private Map<String, ArrayList<ExtendItemConfig>> extendItemMap;
    private Map<String, ExtendPointConfig> extendPointMap;
    private Map<String, ExtendServiceConfig> extendServiceMap;
    private Map<String, ExtendServiceConfig> extendServiceClassMap;
    private static ExtendManager instance = new ExtendManager();
    private ReentrantLock lock = new ReentrantLock();

    public static ExtendManager getInstance() {
        return instance;
    }

    /**
     * 加载插件配置文件，初始化相关扩展数据。
     */
    public void start() {
        if (extendActionMap == null) {
            lock.lock();
            try {
                if (extendActionMap == null) {
                    extendActionMap = new HashMap<>();
                    extendItemMap = new HashMap<>();
                    extendPointMap = new HashMap<>();
                    extendServiceMap = new HashMap<>();
                    extendServiceClassMap = new HashMap<>();

                    long t = System.currentTimeMillis();
                    // 先读取所有插件信息
                    PluginManager.getInstance().init();
                    List<IPlugin> list = new ArrayList<>();
                    List<PluginConfig> configList = PluginManager.getInstance().getAllPluginConfig();
                    for (PluginConfig pc : configList) {
                        if (!pc.isEnabled() || pc.isRunning()) {
                            continue;
                        }
                        initPlugin(pc, list);
                    }
                    // 所有扩展信息读取完成后再逐个启动
                    for (IPlugin plugin : list) {
                        try {
                            plugin.start();
                        } catch (PluginException e) {
                            e.printStackTrace();
                        }
                    }
                    log.info("All plugins started,cost " + (System.currentTimeMillis() - t) + " ms");
                }
            } finally {
                lock.unlock();
                ExtendManager.invoke(AfterAllPluginStartedPoint.ID, new Object[]{});
            }
        }
    }

    /**
     * 初始插件中的配置信息
     */
    private void initPlugin(PluginConfig pc, List<IPlugin> list) {
        if (pc == null || pc.isRunning()) {
            return;
        }
        if (StrUtil.isNotEmpty(pc.getClassName())) {
            try {
                log.info("Loading plugin:" + pc.getID());
                pc.setRunning(true);// 需要先设置，以免无限递归
                pc.setEnabled(true);
                Class<?> c = Class.forName(pc.getClassName());
                if (!IPlugin.class.isAssignableFrom(c)) {
                    log.error("Plugin class '" + pc.getClassName() + "' isn't inherit from IPlugin");
                    return;
                }
                readExtendInfo(pc);
                IPlugin plugin = (IPlugin) c.newInstance();
                list.add(plugin);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void readExtendInfo(PluginConfig pc) {
        // 加入扩展点
        extendPointMap.putAll(pc.getExtendPoints());

        // 加入扩展服务
        for (ExtendServiceConfig es : pc.getExtendServices().values()) {
            extendServiceMap.put(es.getID(), es);
            extendServiceClassMap.put(es.getClassName(), es);
        }

        // 加入扩展行为
        Collection<ExtendActionConfig> actions = pc.getExtendActions().values();
        for (ExtendActionConfig action : actions) {
            log.debug("Loading extendAction:" + action.getID());
            if (!extendPointMap.containsKey(action.getExtendPointID())) {
                log.debug("ExtendAction " + action.getID() + "'s ExtendPoint not found");
                continue;
            }
            ArrayList<ExtendActionConfig> list = extendActionMap.get(action.getExtendPointID());
            if (list == null) {
                list = new ArrayList<ExtendActionConfig>();
                extendActionMap.put(action.getExtendPointID(), list);
            }
            list.add(action);
        }

        // 加入扩展项
        Collection<ExtendItemConfig> items = pc.getExtendItems().values();
        for (ExtendItemConfig item : items) {
            log.debug("Loading extendItem:" + item.getID());
            if (!extendServiceMap.containsKey(item.getExtendServiceID())) {
                log.error("ExtendItem " + item.getID() + "'s ExtendService not found");
                continue;
            }

            ArrayList<ExtendItemConfig> list = extendItemMap.get(item.getExtendServiceID());
            if (list == null) {
                list = new ArrayList<ExtendItemConfig>();
                extendItemMap.put(item.getExtendServiceID(), list);
            }
            list.add(item);
        }
    }

    /**
     * 指定的扩展点下是否有扩展行为。
     */
    public boolean hasAction(String targetPoint) {
        start();
        return extendActionMap.get(targetPoint) != null;
    }

    /**
     * 查找扩展了指定扩展点的扩展行为列表
     */
    public ArrayList<ExtendActionConfig> findActionsByPointID(String extendPointID) {
        start();
        return extendActionMap.get(extendPointID);
    }

    /**
     * 查找注册到指定扩展服务的扩展项列表
     */
    public ArrayList<ExtendItemConfig> findItemsByServiceID(String extendServiceID) {
        start();
        return extendItemMap.get(extendServiceID);
    }

    /**
     * 根据扩展点类名查找扩展点描述
     */
    public ExtendPointConfig findExtendPoint(String extendPointID) {
        start();
        return extendPointMap.get(extendPointID);
    }

    /**
     * 根据扩展服务ID查找扩展服务描述
     */
    public ExtendServiceConfig findExtendService(String extendServiceID) {// NO_UCD
        start();
        return extendServiceMap.get(extendServiceID);
    }

    /**
     * 根据扩展服务类名查找扩展服务描述
     *
     * @param className 扩展服务类名
     * @return 扩展服务描述类
     */
    public ExtendServiceConfig findExtendServiceByClass(String className) {
        start();
        return extendServiceClassMap.get(className);
    }

    /**
     * 调用扩展点
     */
    public static Object[] invoke(String extendPointID, Object[] args) {
        return instance.invokePoint(extendPointID, args);
    }

    public Object[] invokePoint(String extendPointID, Object[] args) {
        try {
            start();
            if (!extendPointMap.containsKey(extendPointID)) {
                log.warn("ExtendPoint is not found:" + extendPointID);
                return new Object[]{};
            }
            ArrayList<ExtendActionConfig> actions = findActionsByPointID(extendPointID);
            if (actions == null) {
                return null;
            }
            List<Object> r = new ArrayList<Object>();
            for (int i = 0; i < actions.size(); i++) {
                try {
                    IExtendAction ea = actions.get(i).getInstance();
                    if (!ea.isUsable()) {
                        continue;
                    }
                    r.add(ea.execute(args));
                } catch (Exception e) {
                    e.printStackTrace();// extend action实例创建失败后只是输出异常
                    actions.remove(i);
                    i--;
                }
            }
            return r.toArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 启用插件
     */
    public void startPlugin(PluginConfig pc) throws PluginException {
        if (pc == null || pc.isRunning()) {
            return;
        }
        if (StrUtil.isNotEmpty(pc.getClassName())) {
            try {
                log.debug("Starting plugin:" + pc.getID());
                pc.setRunning(true);// 需要先设置，以免无限递归
                pc.setEnabled(true);
                Class<?> c = Class.forName(pc.getClassName());
                if (!IPlugin.class.isAssignableFrom(c)) {
                    log.error("Plugin class '" + pc.getClassName() + "' isn't inherit from IPlugin");
                    return;
                }
                readExtendInfo(pc);
                IPlugin plugin = (IPlugin) c.newInstance();
                plugin.start();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 停止插件
     */
    public void stopPlugin(PluginConfig pc) throws PluginException {
        if (!pc.isEnabled() || !pc.isRunning()) {
            return;
        }
        if (StrUtil.isNotEmpty(pc.getClassName())) {
            try {
                Class<?> c = Class.forName(pc.getClassName());
                if (!IPlugin.class.isAssignableFrom(c)) {
                    throw new PluginException("Plugin class '" + pc.getClassName() + "' isn't inherit from IPlugin");
                }
                IPlugin plugin = (IPlugin) c.newInstance();
                plugin.stop();
                pc.setRunning(false);
                pc.setEnabled(false);

                for (ExtendActionConfig ea : pc.getExtendActions().values()) {
                    extendActionMap.get(ea.getExtendPointID()).remove(ea);
                }

                // 去掉扩展点和扩展行为
                for (String id : pc.getExtendPoints().keySet()) {
                    extendPointMap.remove(id);
                }
                // 移除相应的扩展项(必须在移除扩展服务之前，因为本插件可以自己注册自己的扩展服务的扩展项)
                for (ExtendItemConfig ei : pc.getExtendItems().values()) {
                    ExtendServiceConfig es = extendServiceMap.get(ei.getExtendServiceID());
                    if (es != null) {
                        es.getInstance().remove(ei.getInstance().getExtendItemID());
                    }
                    extendItemMap.get(ei.getExtendServiceID()).remove(ei);
                }
                // 去掉扩展服务
                for (ExtendServiceConfig es : pc.getExtendServices().values()) {
                    extendServiceMap.remove(es.getID());
                    extendServiceClassMap.remove(es.getID());
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 停止掉所有的插件
     */
    public void destory() {
        for (PluginConfig pc : PluginManager.getInstance().getAllPluginConfig()) {
            try {
                Class<?> c = Class.forName(pc.getClassName());
                if (!IPlugin.class.isAssignableFrom(c)) {
                    throw new PluginException("Plugin class '" + pc.getClassName() + "' isn't inherit from IPlugin");
                }
                IPlugin plugin = (IPlugin) c.newInstance();
                plugin.destory();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (extendServiceMap != null) {
            for (ExtendServiceConfig es : extendServiceMap.values()) {
                es.destory();
            }
            extendServiceMap.clear();
            extendServiceMap = null;
        }
        if (extendActionMap != null) {
            extendActionMap.clear();
            extendActionMap = null;
        }
        if (extendItemMap != null) {
            extendItemMap.clear();
            extendItemMap = null;
        }
        if (extendPointMap != null) {
            extendPointMap.clear();
            extendPointMap = null;
        }
        if (extendServiceClassMap != null) {
            extendServiceClassMap.clear();
            extendServiceClassMap = null;
        }
        PluginManager.getInstance().destory();
    }

    /**
     * 启用插件
     */
    public void enablePlugin(String pluginID) throws PluginException {
        startPlugin(PluginManager.getInstance().getPluginConfig(pluginID));
    }

    /**
     * 停用插件
     */
    public void disablePlugin(String pluginID) throws PluginException {
        stopPlugin(PluginManager.getInstance().getPluginConfig(pluginID));
    }

    /**
     * 重新启动插件运行环境。<br>
     * 本方法将先中止所有会话和定时任务。<br>
     * 一般在系统安装、插件安装卸载时调用。
     */
    public void restart() {// NO_UCD
        extendActionMap = null;
        start();// 重新读入掉插件注册信息
    }

}
