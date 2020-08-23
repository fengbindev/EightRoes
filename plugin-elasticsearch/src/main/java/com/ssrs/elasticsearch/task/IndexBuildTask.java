package com.ssrs.elasticsearch.task;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.ssrs.elasticsearch.config.EsServerConfig;
import com.ssrs.elasticsearch.service.IIndex;
import com.ssrs.elasticsearch.service.IndexService;
import com.ssrs.elasticsearch.util.IndexTypeLock;
import com.ssrs.framework.config.AppDataPath;
import com.ssrs.framework.schedule.SystemTask;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

public class IndexBuildTask extends SystemTask {

	public static final String ID = "com.ssrs.elasticsearch.task.IndexBuildTask";

	public String getExtendItemID() {
		return ID;
	}

	public String getExtendItemName() {
		return "索引自动创建";
	}

	@Override
	public void execute() {
		if (StrUtil.isEmpty(EsServerConfig.getValue())) {
			return;
		}
		Date date = getLastTime();
		List<IIndex> indexList = IndexService.getInstance().getAll();
		for (IIndex index : indexList) {
			if (IndexTypeLock.isLock(index)) {
				continue;
			}
			IndexTypeLock.lock(index);
			index.build(date);
			index.delete(date);
			IndexTypeLock.unlock(index);
		}
	}

	/** 
	 * 获取上次执行时间
	 */
	private Date getLastTime() {
		Date date;
		String dir = AppDataPath.getValue() + "essearch/";
		String file = dir + "es_index.time";
		if (!FileUtil.exist(file)) {
			if (!FileUtil.exist(dir)) {
				FileUtil.mkdir(AppDataPath.getValue() + "essearch/");
			}
			date = new Date(0);
		} else {
			date = new Date(Long.parseLong(FileUtil.readString(file, StandardCharsets.UTF_8).trim()));
		}
		FileUtil.writeUtf8String(System.currentTimeMillis() + "", file);
		return date;
	}

	@Override
	public String getDefaultCronExpression() {
		return "*/3 * * * *";
	}
}
