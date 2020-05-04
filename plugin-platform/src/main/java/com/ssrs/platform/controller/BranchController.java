package com.ssrs.platform.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ssrs.framework.Current;
import com.ssrs.framework.PrivilegeModel;
import com.ssrs.framework.cache.FrameworkCacheManager;
import com.ssrs.framework.core.OperateReport;
import com.ssrs.framework.extend.ExtendManager;
import com.ssrs.framework.security.annotation.Priv;
import com.ssrs.framework.web.ApiResponses;
import com.ssrs.framework.web.BaseController;
import com.ssrs.platform.bl.PrivBL;
import com.ssrs.platform.code.YesOrNo;
import com.ssrs.platform.model.entity.Branch;
import com.ssrs.platform.model.entity.Privilege;
import com.ssrs.platform.model.entity.Role;
import com.ssrs.platform.model.entity.User;
import com.ssrs.platform.model.parm.BranchParm;
import com.ssrs.platform.point.AfterBranchAddPoint;
import com.ssrs.platform.point.AfterBranchDeletePoint;
import com.ssrs.platform.point.AfterBranchModifyPoint;
import com.ssrs.platform.service.IBranchService;
import com.ssrs.platform.service.IPrivilegeService;
import com.ssrs.platform.service.IRoleService;
import com.ssrs.platform.service.IUserService;
import com.ssrs.platform.util.NoUtil;
import com.ssrs.platform.util.PlatformCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    @Autowired
    private IRoleService roleService;
    @Autowired
    private IPrivilegeService privilegeService;

    @Priv
    @GetMapping
    public ApiResponses<List<Tree<String>>> list(@RequestParam Map<String, Object> params) {
        String name = (String) params.get("name");
        String isSelect = (String) params.get("isSelect");
        List<Branch> branchList = branchService.list(Wrappers.<Branch>lambdaQuery()
                .like(StrUtil.isNotEmpty(name), Branch::getName, name)
                .likeRight(Branch::getBranchInnercode, com.ssrs.framework.User.getBranchInnerCode())
                .orderByAsc(Branch::getOrderFlag)
        );
        String parentId = PlatformCache.getBranch(com.ssrs.framework.User.getBranchInnerCode()).getParentInnercode();
        List<Tree<String>> branchTree = TreeUtil.build(branchList, parentId, (branch, treeNode) -> {
            treeNode.setId(branch.getBranchInnercode());
            treeNode.setParentId(branch.getParentInnercode());
            treeNode.setName(branch.getName());
            //扩展字段
            Map<String, Object> extra = BeanUtil.beanToMap(branch);
            if (MapUtil.isNotEmpty(extra)) {
                extra.forEach(treeNode::putExtra);
            }
            if (YesOrNo.isYes((String) extra.get("isLeaf"))) {
                treeNode.put("isLeaf", true);
            } else {
                treeNode.put("isLeaf", false);
            }
            if (YesOrNo.isYes(isSelect)) {
                treeNode.putExtra("key", branch.getBranchInnercode());
                treeNode.putExtra("title", branch.getName());
                treeNode.putExtra("value", branch.getBranchInnercode());
            }
        });
        return success(branchTree);
    }

    @Priv
    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public ApiResponses<String> create(@Validated  BranchParm branchParm) {
        Branch branch = branchParm.convert(Branch.class);
        OperateReport operateReport = branchService.isNameOrBranchCodeExists(branch.getName(), branch.getBranchCode(), null);
        if (!operateReport.isSuccess()) {
            return failure(operateReport.getMessage());
        }
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
        ExtendManager.invoke(AfterBranchAddPoint.ID, new Object[]{branch});
        return success("保存成功");
    }

    @Priv
    @PutMapping("/{bracnInnercode}")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponses<String> update(@PathVariable String bracnInnercode, @Validated BranchParm branchParm) {
        Branch branch = branchParm.convert(Branch.class);
        branch.setBranchInnercode(bracnInnercode);
        OperateReport operateReport = branchService.isNameOrBranchCodeExists(branch.getName(), branch.getBranchCode(), branch.getBranchInnercode());
        if (!operateReport.isSuccess()) {
            return failure(operateReport.getMessage());
        }
        if (bracnInnercode.length() == 4) {
            branch.setParentInnercode("0000");
        }
        PrivBL.assertBranch(bracnInnercode);
        branchService.updateById(branch);
        FrameworkCacheManager.set(PlatformCache.ProviderID, PlatformCache.Type_Branch, branch.getBranchInnercode(), branch);
        ExtendManager.invoke(AfterBranchModifyPoint.ID, new Object[]{branch});
        return success("修改成功");
    }

    @Priv
    @DeleteMapping("/{ids}")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponses<String> delete(@PathVariable String ids) {
        String[] idArr = ids.split(",");
        for (String innerCode : idArr) {
            PrivBL.assertBranch(innerCode);
            int count1 = userService.count(Wrappers.<User>lambdaQuery().eq(User::getBranchInnercode, innerCode));
            int count2 = roleService.count(Wrappers.<Role>lambdaQuery().eq(Role::getBranchInnercode, innerCode));
            if (count1 > 0 || count2 > 0) {
                return failure("不能删除拥有角色和用户的机构，请先删除机构下的角色和用户！");
            }
            Branch branch = branchService.getById(innerCode);
            if (ObjectUtil.isNotNull(branch)) {
                if ("0000".equals(branch.getParentInnercode())) {
                    return failure("删除失败：不能删除顶级机构！");
                }
                branchService.remove(Wrappers.<Branch>lambdaQuery().likeRight(Branch::getBranchInnercode, innerCode));
                privilegeService.remove(Wrappers.<Privilege>lambdaQuery().eq(Privilege::getOwnerType, PrivilegeModel.OwnerType_Branch).eq(Privilege::getOwner, innerCode));
            }
        }
        // 删除机构后的扩展点
        ExtendManager.invoke(AfterBranchDeletePoint.ID, new Object[]{ids});
        for (String innercode : idArr) {
            FrameworkCacheManager.remove(PlatformCache.ProviderID, PlatformCache.Type_Branch, innercode);
        }
        return success("删除成功");
    }

    @Priv
    @GetMapping("/user")
    public ApiResponses<List<User>> getUser(String branchInnercode) {
        if (StrUtil.isEmpty(branchInnercode)) {
            branchInnercode = Current.getUser().getBranchInnerCode();
        }
        List<User> users = userService.list(Wrappers.<User>lambdaQuery().likeRight(StrUtil.isNotEmpty(branchInnercode), User::getBranchInnercode, branchInnercode));
        return success(users);
    }

}