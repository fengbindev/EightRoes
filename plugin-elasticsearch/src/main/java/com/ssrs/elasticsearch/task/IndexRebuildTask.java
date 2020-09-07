package com.ssrs.elasticsearch.task;

import com.ssrs.elasticsearch.service.IIndex;
import com.ssrs.elasticsearch.service.IndexService;
import com.ssrs.framework.config.AppDataPath;
import com.ssrs.framework.schedule.SystemTask;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Slf4j
public class IndexRebuildTask extends SystemTask {

    private static boolean deleteIndexLock = false;
    public static volatile boolean rebuildAllLock = false;

    public static final String ID = "com.ssrs.elasticsearch.task.IndexRebuildTask";

    @Override
    public String getExtendItemID() {
        return ID;
    }

    @Override
    public String getExtendItemName() {
        return "索引库定时重建";
    }

    @Override
    public void execute() {
        if (rebuildAllLock) {
            return;
        }
        rebuildAllLock = true;
        IIndex.updateIndexId();
        List<IIndex> indexList = IndexService.getInstance().getAll();
        for (IIndex index : indexList) {
            index.build(new Date(0));
            index.delete(new Date(0));
        }
        String alias = IIndex.getIndexAlias();
        // 已存在的索引需要删除
        String name1 = new IndexService().getIndexIdByAlias(alias);
        String name2 = IIndex.getIndexId();
        System.out.println("name1:" + name1);
        System.out.println("name2:" + name2);
        if (!name1.equals(name2)) {
            if (IndexService.delete(alias)) {
                log.info("~~~~~~~删除旧的索引库~~~~~~~");
                File file = new File(AppDataPath.getValue() + "essearch/es_index.time");
                file.delete();
            }
        }
        rebuildAllLock = false;
        try {
            new IndexService().addAlias();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getDefaultCronExpression() {
        return "0 0 22 * * ?";
    }
}
