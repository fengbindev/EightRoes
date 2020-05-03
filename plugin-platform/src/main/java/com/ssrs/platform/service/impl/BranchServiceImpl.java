package com.ssrs.platform.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ssrs.framework.core.OperateReport;
import com.ssrs.platform.model.entity.Branch;
import com.ssrs.platform.mapper.BranchMapper;
import com.ssrs.platform.service.IBranchService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 组织机构表 服务实现类
 * </p>
 *
 * @author ssrs
 * @since 2020-04-04
 */
@Service
public class BranchServiceImpl extends ServiceImpl<BranchMapper, Branch> implements IBranchService {

    @Override
    public OperateReport isNameOrBranchCodeExists(String name, String branchCode, String innerCode) {
        OperateReport operateReport = new OperateReport(true);
        // 相同机构编码或名称
        int count = count(Wrappers.<Branch>lambdaQuery().eq(Branch::getBranchCode, branchCode).or().eq(Branch::getName, name));
        if (count > 1) {
            operateReport.setSuccess(false, "存在重复的名称或机构编码！");
        }
        return operateReport;
    }
}
