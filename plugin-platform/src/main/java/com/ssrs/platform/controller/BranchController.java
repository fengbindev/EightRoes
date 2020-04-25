package com.ssrs.platform.controller;


import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ssrs.framework.security.annotation.Priv;
import com.ssrs.framework.web.ApiResponses;
import com.ssrs.framework.web.BaseController;
import com.ssrs.platform.code.YesOrNo;
import com.ssrs.platform.model.entity.Branch;
import com.ssrs.platform.model.entity.User;
import com.ssrs.platform.model.parm.BranchParm;
import com.ssrs.platform.service.IBranchService;
import com.ssrs.platform.service.IUserService;
import com.ssrs.platform.util.NoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * <p>
 * 组织机构表 前端控制器
 * </p>
 *
 * @author ssrs
 * @since 2020-04-04
 */
@RestController
@RequestMapping("/api/branch")
public class BranchController extends BaseController {

    @Autowired
    private IBranchService branchService;
    @Autowired
    private IUserService userService;

    @Priv
    @GetMapping
    public ApiResponses<List<Branch>> list(String name) {
        List<Branch> branchList = branchService.list(Wrappers.<Branch>lambdaQuery()
                .like(StrUtil.isNotEmpty(name), Branch::getName, name)
                .orderByAsc(Branch::getOrderFlag)
        );
        return success(branchList);
    }

    @Priv
    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public ApiResponses<String> create(@Validated @RequestBody BranchParm branchParm) {
        Branch branch = branchParm.convert(Branch.class);
        if (StrUtil.isEmpty(branch.getParentInnercode()) || "0000".equals(branch.getParentInnercode())) {
            branch.setBranchInnercode(NoUtil.getMaxNo("BranchInnerCode", 4));
            branch.setType("1");
            branch.setIsLeaf(YesOrNo.Yes);
            branch.setTreeLevel(1L);
        } else {
            Branch parentBranch = branchService.getOne(Wrappers.<Branch>lambdaQuery().eq(Branch::getBranchInnercode, branch.getParentInnercode()));
            branch.setBranchInnercode(NoUtil.getMaxNo("BranchInnerCode", parentBranch.getBranchInnercode(), 4));
            branch.setType("0");
            branch.setTreeLevel(parentBranch.getTreeLevel() + 1);
            branch.setIsLeaf(YesOrNo.Yes);
            if (YesOrNo.isYes(parentBranch.getIsLeaf())) {
                parentBranch.setIsLeaf(YesOrNo.No);
            }
            branchService.updateById(parentBranch);
        }
        branchService.save(branch);
        return success("保存成功");
    }

    @Priv
    @PutMapping("/{bracnInnercode}")
    public ApiResponses<String> update(@PathVariable String bracnInnercode, @Validated @RequestBody BranchParm branchParm) {
        Branch branch = branchParm.convert(Branch.class);
        branch.setBranchInnercode(bracnInnercode);
        branchService.updateById(branch);
        return success("修改成功");
    }

    @Priv
    @DeleteMapping("/{bracnInnercode}")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponses<String> delete(@PathVariable String bracnInnercode) {
        // TODO 还需要删除权限关联表和角色关联表
        branchService.removeById(bracnInnercode);
        return success("删除成功");
    }


    // TODO 选择机构主管接口，后续优化改成分页获取
    @Priv
    @GetMapping("/user")
    public ApiResponses<List<User>> getUser(){
        List<User> users = userService.list();
        return success(users);
    }

}